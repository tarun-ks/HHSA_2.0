package com.nyc.hhs.sessionListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.util.PropertyLoader;

/**
 * 
 * This class is responsible for adding/removing lock on various screen by
 * various users
 * 
 */
public class SessionListener implements HttpSessionListener
{
	private static final LogInfo LOG_OBJECT = new LogInfo(SessionListener.class);

	/**
	 * This method is invoked when a new session is created and will remove all
	 * locked screens by the user that were locked before one days(in case
	 * configured on DB)
	 * <ul>
	 * <li>1. check if session id is created for a valid user</li>
	 * <li>2. check if concurrency type is db</li>
	 * <li>3. if yes, invoke transaction "removeLockedUserById" to remove all
	 * locks taken by user before one day</li>
	 * </ul>
	 * @param aoSessionEvent Session Event
	 */
	@Override
	public void sessionCreated(HttpSessionEvent aoSessionEvent)
	{
		HttpSession loHttpSession = aoSessionEvent.getSession();
		String lsUserId = (String) loHttpSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		try
		{
			if (lsUserId != null && !lsUserId.isEmpty())
			{
				String lsConcurrencyType = PropertyLoader.getProperty(ApplicationConstants.HHS_PROPERTY_FILE_PATH,
						HHSConstants.CONCURRENCY_TYPE);
				if (lsConcurrencyType != null && lsConcurrencyType.equalsIgnoreCase(HHSConstants.DB))
				{
					Channel loChannel = new Channel();
					loChannel.setData(ApplicationConstants.KEY_SESSION_USER_ID, lsUserId);
					HHSTransactionManager.executeTransaction(loChannel, HHSConstants.REMOVE_LOCKED_USER_BY_ID);
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(
					"Error occured while removing user id from cache in removeUser method of SessionListener class",
					aoAppEx);
		}
	}

	/**
	 * This method is invoked when a user session is invalidated and will remove
	 * locked screens by the user(if any) by invoking removeUser method
	 * <ul>
	 * <li>Fetches UserId</li>
	 * <li>fetches Session id</li>
	 * <li>Updated Method in R4</li>
	 * <li>modified this method for log out issue Defect #6432 fixed in release 3.1.3 </li>
	 * </ul>
	 * @param aoSessionEvent Session Event 
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent aoSessionEvent)
	{
		HttpSession loHttpSession = aoSessionEvent.getSession();
		try
		{
			LOG_OBJECT.Debug("sessionDestroyed");
			String lsUserId = (String) loHttpSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
			String lsUserDn = (String) loHttpSession.getAttribute(ApplicationConstants.USER_DN);
			String lsSessionId = loHttpSession.getId();
			List<String> loList = (List<String>) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.SESSION_LIST_REMOVE);
			
			//[Start]R7.12.0 QC9311 Minimize Debug
			//LOG_OBJECT.Debug("SESSION_LIST_REMOVE :: "+loList);
			//[End]R7.12.0 QC9311 Minimize Debug
			
			Map<String, String> loDataMap = (Map<String, String>) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.SESSION_USER_DETAIL_CACHE);
			
			if (loDataMap != null && loDataMap.get(lsUserId) != null && loDataMap.get(lsUserId).equals(lsSessionId))
			{
				loDataMap.remove(lsUserId);
				BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.SESSION_USER_DETAIL_CACHE,
						loDataMap);
				
			}
			//made changes for log out issue Defect #6432 fixed in release 3.1.3 
			if (loDataMap != null && loDataMap.get(lsUserDn) != null && loDataMap.get(lsUserDn).equals(lsSessionId))
			{
				loDataMap.remove(lsUserDn);
				BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.SESSION_USER_DETAIL_CACHE,
						loDataMap);
			}
			
			if (loList != null && loList.contains(lsSessionId))
			{   LOG_OBJECT.Debug("remove session from SESSION_LIST_REMOVE id  :: "+lsSessionId);
				loList.remove(lsSessionId);
				BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.SESSION_LIST_REMOVE, loList);
			}
			removeUser(loHttpSession);
			removeLockedDocumentByUSer(loHttpSession);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while removing user id from cache", aoAppEx);
		}
	}

	/**
	 * This method is to remove the user from locking list
	 * <ul>
	 * <li>1. get session id from session</li>
	 * <li>2. invoke method "removeUserDB" in case system configured to manage
	 * cache on db</li>
	 * <li>3. Else invoke "removeUserCache" method</li>
	 * </ul>
	 * 
	 * @param aoHttpSession Http Session
	 * @throws ApplicationException in case of any exception
	 */
	public static void removeUser(HttpSession aoHttpSession) throws ApplicationException
	{
		String lsSessionId = aoHttpSession.getId();
		try
		{
			String lsConcurrencyType = PropertyLoader.getProperty(ApplicationConstants.HHS_PROPERTY_FILE_PATH,
					HHSConstants.CONCURRENCY_TYPE);
			if (lsConcurrencyType != null && lsConcurrencyType.equalsIgnoreCase(HHSConstants.DB))
			{
				removeUserDB(lsSessionId);
			}
			else
			{   
				removeUserCache(aoHttpSession);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while checking and locking a screen", aoAppEx);
			throw aoAppEx;
		}
	}

	/**
	 * This method is to remove the user from locking list from db
	 * <ul>
	 * <li>invoke transaction "removeLockedUser" to remove all the locks taken
	 * by current user</li>
	 * </ul>
	 * 
	 * @param asSessionId session id for current user
	 * @throws ApplicationException in case of any exception
	 */
	private static void removeUserDB(String asSessionId) throws ApplicationException
	{
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSConstants.SESSION_ID, asSessionId);
			HHSTransactionManager.executeTransaction(loChannel, HHSConstants.REMOVE_LOCKED_USER);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(
					"Error occured while removing user id from cache in removeUser method of SessionListener class",
					aoAppEx);
			throw aoAppEx;
		}
	}

	/**
	 * This method is to remove the user from locking list of cache
	 * <ul>
	 * <li>1. Get the key to be removed from session</li>
	 * <li>2. invoke "removeCacheObject" method to remove the cache key</li>
	 * <li>3. Remove the key from session</li>
	 * </ul>
	 * 
	 * @param aoHttpSession Http Session
	 * @throws ApplicationException in case of any exception
	 */
	@SuppressWarnings("unchecked")
	public static void removeUserCache(HttpSession aoHttpSession) throws ApplicationException
	{
		List<Map<String, Object>> loUserLockingDetailsLocal = null;
		String lsKeyToRemove = (String) aoHttpSession.getAttribute(HHSConstants.LAST_KEY_ACCESSED);
		if (lsKeyToRemove != null)
		{
			Map<String, Object> loCoherenceMap = (Map<String, Object>) BaseCacheManagerWeb.getInstance()
					.getCacheObject(lsKeyToRemove);
			if (loCoherenceMap != null)
			{
				loUserLockingDetailsLocal = (List<Map<String, Object>>) loCoherenceMap
						.get(HHSConstants.SESSION_DETAILS);
				String lsSessionId = null;
				int liIndex = 0;
				for (Map<String, Object> loUserLockingDetailObj : loUserLockingDetailsLocal)
				{
					lsSessionId = (String) loUserLockingDetailObj.get(HHSConstants.SESSION_ID);
					if (lsSessionId != null && lsSessionId.equalsIgnoreCase(aoHttpSession.getId()))
					{
						loUserLockingDetailsLocal.remove(liIndex);
						break;
					}
					liIndex++;
				}
			}
			if (loUserLockingDetailsLocal != null && !loUserLockingDetailsLocal.isEmpty())
			{
				loCoherenceMap.put(HHSConstants.SESSION_DETAILS, loUserLockingDetailsLocal);
				BaseCacheManagerWeb.getInstance().putCacheObject(lsKeyToRemove, loCoherenceMap);
			}
			else if (loUserLockingDetailsLocal != null && loUserLockingDetailsLocal.isEmpty())
			{
				BaseCacheManagerWeb.getInstance().removeCacheObject(lsKeyToRemove);
			}
			aoHttpSession.removeAttribute(HHSConstants.LAST_KEY_ACCESSED);
		}
	}

	/**
	 * This metod is used to remove the locked document from the cache if the
	 * user dows not logout the application and closed the screen
	 * <ul>
	 * <li>Fetches user Id</li>
	 * <li>Added in R4</li>
	 * </ul>
	 * @param loHttpSession Httpsession Object
	 * @throws ApplicationException throws application exception
	 */
	private void removeLockedDocumentByUSer(HttpSession loHttpSession) throws ApplicationException
	{
		String lsUserId = null;
		HashMap<String, List<String>> loLockedDocumentMap = null;
		lsUserId = (String) loHttpSession.getAttribute(ApplicationConstants.KEY_SESSION_USER_ID);
		String lsSessionId = loHttpSession.getId();
		if (null != lsUserId && !lsUserId.isEmpty() && null != lsSessionId && !lsSessionId.isEmpty())
		{
			String lsLockedDocKey = lsSessionId + "_" + lsUserId;
			loLockedDocumentMap = (HashMap<String, List<String>>) BaseCacheManagerWeb.getInstance().getCacheObject(
					ApplicationConstants.EDIT_DOC_LIST_MAP);
			if (null != loLockedDocumentMap && loLockedDocumentMap.containsKey(lsLockedDocKey))
			{
				loLockedDocumentMap.remove(lsLockedDocKey);
			}
			synchronized (this)
			{
				BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.EDIT_DOC_LIST_MAP,
						loLockedDocumentMap);
			}
		}
	}

}