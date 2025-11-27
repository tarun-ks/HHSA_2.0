package com.nyc.hhs.aop.access.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.sessionListener.SessionListener;
import com.nyc.hhs.util.PropertyLoader;

public abstract class BaseAccessHandler
{
	private static final LogInfo LOG_OBJECT = new LogInfo(BaseAccessHandler.class);

	/**
	 * This method generates the id specific to a module screens
	 * @param aoRet - Object returned by AOP
	 * @param aoRequest - Portlet Request
	 * @return Generated id for the module
	 * @throws ApplicationException - throw exception in case any
	 */
	public abstract String generateId(Object aoRet, PortletRequest aoRequest) throws ApplicationException;

	/**
	 * This method checks if a lock id exist, if yes set in request that screen
	 * is locked else add lock and set in request that screen is editable for
	 * the user
	 * <ul>
	 * <li>1. Check if application configured for DB mode locking</li>
	 * <li>2. if yes, invoke "checkAndAddLockDB" method</li>
	 * <li>3. Else, invoke "checkAndAddLockCache" method</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param asLockId - lock id to be checked against
	 * @param aoRequest - Portlet request
	 * @param aoRuleFlag Rule Flag
	 * @throws ApplicationException - throw exception in case any
	 */
	public void checkAndAddLock(String asLockId, Boolean aoRuleFlag, PortletRequest aoRequest)
			throws ApplicationException
	{
		try
		{
			String lsConcurrencyType = PropertyLoader.getProperty(ApplicationConstants.HHS_PROPERTY_FILE_PATH,
					HHSConstants.CONCURRENCY_TYPE);
			if (lsConcurrencyType != null && lsConcurrencyType.equalsIgnoreCase(HHSConstants.DB))
			{
				checkAndAddLockDB(asLockId, aoRequest);
			}
			else
			{
				checkAndAddLockCache(asLockId, aoRuleFlag, aoRequest);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while checking and locking a screen", aoAppEx);
			throw aoAppEx;
		}
	}

	/**
	 * This method checks if a lock id exist, if yes set in request that screen
	 * is locked else add lock and set in request that screen is editable for
	 * the user(Mode DB)
	 * <ul>
	 * <li>1. get data required for locking a screen(namely lock id, session id,
	 * user name, user id) and add them in a map</li>
	 * <li>2. Invoke transaction "checkAndAddLock" to check and add lock if no
	 * lock previously exist</li>
	 * <li>3. Check if a lock is added or was previously locked by current user
	 * in current session id</li>
	 * <li>4. if yes raise flag showing screen is editable else set flag that
	 * screen is non editable and is locked by a user</li>
	 * </ul>
	 * @param asLockId - lock id to be checked against
	 * @param aoRequest - Portlet request
	 * @throws ApplicationException - throw exception in case any
	 */
	@SuppressWarnings("unchecked")
	public void checkAndAddLockDB(String asLockId, PortletRequest aoRequest) throws ApplicationException
	{
		String lsSessionId = aoRequest.getPortletSession().getId();
		String lsUserName = (String) aoRequest.getPortletSession().getAttribute(
				ApplicationConstants.KEY_SESSION_USER_NAME, PortletSession.APPLICATION_SCOPE);
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		Channel loChannel = new Channel();
		loChannel.setData(HHSConstants.LOCK_ID, asLockId);
		loChannel.setData(HHSConstants.SESSION_ID, lsSessionId);
		loChannel.setData(ApplicationConstants.KEY_SESSION_USER_NAME, lsUserName);
		loChannel.setData(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
		HHSTransactionManager.executeTransaction(loChannel, HHSConstants.CHECK_ADD_LOCK);
		Boolean loIsLockAdded = (Boolean) loChannel.getData(HHSConstants.LO_IS_LOCK_ADDED);
		Map<String, String> loOutputData = (Map<String, String>) loChannel.getData(HHSConstants.OUTPUT_DATA);
		if (loIsLockAdded
				|| (loOutputData != null && loOutputData.get(HHSConstants.USER_SESSION_ID) != null && loOutputData.get(
						HHSConstants.USER_SESSION_ID).equals(lsSessionId)))
		{
			aoRequest.setAttribute(HHSConstants.ACCESS_SCREEN_ENABLE, true);
		}
		else
		{
			if (null != loOutputData && null != loOutputData.get(HHSConstants.USER_NAME))
			{
				aoRequest.setAttribute(HHSConstants.LOCKED_BY, loOutputData.get(HHSConstants.USER_NAME));
			}
			aoRequest.setAttribute(HHSConstants.ACCESS_SCREEN_ENABLE, false);
		}
	}

	/**
	 * This method checks if a lock id exist, if yes set in request that screen
	 * is locked else add lock and set in request that screen is editable for
	 * the user(Mode Cache)
	 * <ul>
	 * <li>1. get data required for locking a screen(namely lock id, session id,
	 * user name, user id) and add them in a map</li>
	 * <li>2. If yes, check and release any lock taken before 24hours</li>
	 * <li>3. Check if key exist in cache</li>
	 * <li>4. If yes, check if lock taken by taken by same user and accordingly
	 * set the accessibility flags</li>
	 * <li>5. Else, add locking key with corresponding details to cache</li>
	 * </ul>
	 * @param asLockId - lock id to be checked against
	 * @param aoRequest - Portlet request
	 * @param aoRuleFlag Rule Flag
	 * @throws ApplicationException - throw exception in case any
	 */
	@SuppressWarnings("unchecked")
	public void checkAndAddLockCache(String asLockId, Boolean aoRuleFlag, PortletRequest aoRequest)
			throws ApplicationException
	{
		HttpSession loHttpSession = ((HttpServletRequest) aoRequest.getAttribute(HHSConstants.JAVAX_SERVLET_REQUEST))
				.getSession();
		String lsUserId = (String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_ID,
				PortletSession.APPLICATION_SCOPE);
		Map<String, Object> loCoherenceMap = (Map<String, Object>) BaseCacheManagerWeb.getInstance().getCacheObject(
				asLockId);
		Map<String, Object> loUserDataMap = new HashMap<String, Object>();
		List<Map<String, Object>> loUserLockingDetails = new ArrayList<Map<String, Object>>();
		loUserDataMap.put(
				HHSConstants.KEY_SESSION_USER_NAME,
				(String) aoRequest.getPortletSession().getAttribute(ApplicationConstants.KEY_SESSION_USER_NAME,
						PortletSession.APPLICATION_SCOPE));
		loUserDataMap.put(HHSConstants.KEY_SESSION_USER_ID, lsUserId);
		long llCurrentTimeStamp = System.currentTimeMillis();

		Map<String, Object> loUserDetails = new HashMap<String, Object>();
		loUserDetails.put(HHSConstants.LOCKED_ON, System.currentTimeMillis());
		loUserDetails.put(HHSConstants.SESSION_ID, aoRequest.getPortletSession().getId());

		loUserLockingDetails.add(loUserDetails);

		loUserDataMap.put(HHSConstants.SESSION_DETAILS, loUserLockingDetails);
		if (loCoherenceMap != null)
		{
			boolean lbCanRelease = true;
			List<Map<String, Object>> loUserLockingDetailsLocal = (List<Map<String, Object>>) loCoherenceMap
					.get(HHSConstants.SESSION_DETAILS);
			String lsSessionId = null;
			long llTimestamp = 0;
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			int liMaxTimeInSec = Integer.valueOf(loApplicationSettingMap.get(HHSConstants.LOCK_CLEAR_TIME));
			for (Map<String, Object> loUserLockingDetailObj : loUserLockingDetailsLocal)
			{
				lsSessionId = (String) loUserLockingDetailObj.get(HHSConstants.SESSION_ID);
				llTimestamp = Long.parseLong(loUserLockingDetailObj.get(HHSConstants.LOCKED_ON).toString());
				if (lsSessionId != null && lsSessionId.equalsIgnoreCase(aoRequest.getPortletSession().getId())
						&& llCurrentTimeStamp - llTimestamp > liMaxTimeInSec * 1000)
				{
					lbCanRelease = false;
					break;
				}
			}
			if (!lbCanRelease)
			{
				BaseCacheManagerWeb.getInstance().removeCacheObject(asLockId);
				loCoherenceMap = null;
			}
		}
		if (loCoherenceMap != null && aoRuleFlag)
		{
			if (loCoherenceMap.get(HHSConstants.KEY_SESSION_USER_ID).equals(
					loUserDataMap.get(HHSConstants.KEY_SESSION_USER_ID)))
			{
				String lsSessionId = null;
				boolean lbAlreadyInList = false;
				List<Map<String, Object>> loUserLockingDetailsLocal = (List<Map<String, Object>>) loCoherenceMap
						.get(HHSConstants.SESSION_DETAILS);
				for (Map<String, Object> loUserLockingDetailObj : loUserLockingDetailsLocal)
				{
					lsSessionId = (String) loUserLockingDetailObj.get(HHSConstants.SESSION_ID);
					if (lsSessionId != null && lsSessionId.equalsIgnoreCase(aoRequest.getPortletSession().getId()))
					{
						lbAlreadyInList = true;
						break;
					}
				}
				if (!lbAlreadyInList)
				{
					loUserLockingDetailsLocal.add(loUserDetails);
					loCoherenceMap.put(HHSConstants.SESSION_DETAILS, loUserLockingDetailsLocal);
					BaseCacheManagerWeb.getInstance().putCacheObject(asLockId, loCoherenceMap, true);
				}
				aoRequest.setAttribute(HHSConstants.ACCESS_SCREEN_ENABLE, true);
				loHttpSession.setAttribute(HHSConstants.LAST_KEY_ACCESSED, asLockId);
			}
			else
			{
				aoRequest.setAttribute(HHSConstants.LOCKED_BY, loCoherenceMap.get(HHSConstants.KEY_SESSION_USER_NAME));
				aoRequest.setAttribute(HHSConstants.ACCESS_SCREEN_ENABLE, false);
			}
		}
		else
		{
			SessionListener.removeUserCache(loHttpSession);
			if (aoRuleFlag)
			{
				aoRequest.setAttribute(HHSConstants.ACCESS_SCREEN_ENABLE, true);
				BaseCacheManagerWeb.getInstance().putCacheObject(asLockId, loUserDataMap, true);
				loHttpSession.setAttribute(HHSConstants.LAST_KEY_ACCESSED, asLockId);
			}
		}
	}
}