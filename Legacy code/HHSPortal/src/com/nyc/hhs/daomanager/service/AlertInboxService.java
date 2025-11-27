package com.nyc.hhs.daomanager.service;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.model.AlertInboxBean;
import com.nyc.hhs.util.DAOUtil;

/**
 * @AlertInboxService This class is used to fetch the alert information. Also it
 *                    inserts and deletes the alert information in the database.
 * 
 */
public class AlertInboxService extends ServiceState
{

	/**
	 * This method is used to get alerts information from the Notification Table
	 * 
	 * @param asUserId User Id of logged in user
	 * @param aiStartNode Start Node
	 * @param aiEndNode End Node
	 * @param asNotificationType Type of notification
	 * @param asToDate Notification to date
	 * @param asFromDate Notification From Date
	 * @param aoMyBatisSession MyBatis SQL session
	 * @return loAlertInboxList Alert Inbox Data List
	 * @throws ApplicationException
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<AlertInboxBean> getAlertInboxListFromDAO(HashMap aohmAlertProps, final SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<AlertInboxBean> loAlertInboxList = null;
		try
		{
			String lsNotificationType = (String) aohmAlertProps.get("asNotificationType");

			if (lsNotificationType.equalsIgnoreCase("%"))
			{
				loAlertInboxList = (List<AlertInboxBean>) DAOUtil.masterDAO(aoMyBatisSession, aohmAlertProps,
						ApplicationConstants.MAPPER_CLASS_ALERT_INBOX_MAPPER, "selectAlertInboxListAll",
						"java.util.HashMap");
			}
			else
			{
				loAlertInboxList = (List<AlertInboxBean>) DAOUtil.masterDAO(aoMyBatisSession, aohmAlertProps,
						ApplicationConstants.MAPPER_CLASS_ALERT_INBOX_MAPPER, "selectAlertInboxList",
						"java.util.HashMap");
			}
			setMoState("Transaction Success:Alert Inbox List fetched successfully");
		}
		catch (ApplicationException aoEx)
		{
			setMoState("Transaction Failure:Error occurred while fetching Alert Inbox List for user Id:"
					+ aohmAlertProps.get("asUserId"));
			throw aoEx;
		}
		return loAlertInboxList;
	}

	/**
	 * This method returns the selected Alert Information from the Notification
	 * table
	 * 
	 * @param asNotificationId Notification ID for a notification
	 * @param aoMyBatisSession MyBatis SQL session
	 * @return loAlertInboxBean Bean containing data for Alert Inbox
	 * @throws ApplicationException
	 */
	public AlertInboxBean getSelectedAlertInboxInformation(final String asNotificationId,
			final SqlSession aoMyBatisSession) throws ApplicationException
	{
		AlertInboxBean loAlertInboxBean = null;
		HashMap<String, String> loQueryMap = new HashMap<String, String>();
		try
		{
			loQueryMap.put("asNotificationId", asNotificationId);
			loAlertInboxBean = (AlertInboxBean) DAOUtil.masterDAO(aoMyBatisSession, loQueryMap,
					ApplicationConstants.MAPPER_CLASS_ALERT_INBOX_MAPPER, "getSelectedAlertItem", "java.util.HashMap");
			setMoState("Transaction Success: Alert Information fetched successfully for Notification Id:"
					+ asNotificationId);
		}
		catch (ApplicationException aoEx)
		{
			setMoState("Transaction failure:Error occurred while fetching information for Notification Id:"
					+ asNotificationId);
			throw aoEx;
		}
		return loAlertInboxBean;
	}

	/**
	 * This method deletes the alert from the Notification Table
	 * 
	 * @param asAlertList List containing Alert data
	 * @param aoMyBatisSession MyBatis SQL session
	 * @return loAlertDeleteStatus Alert deletion Status
	 * @throws ApplicationException
	 */
	public Boolean deleteAlertInboxListItem(List asNotificationIds, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean loDeletionSuccessStatus;
		HashMap<String, Object> loQueryMap = new HashMap<String, Object>();
		try
		{
			loQueryMap.put("asNotificationIds", asNotificationIds);
			DAOUtil.masterDAO(aoMyBatisSession, loQueryMap, ApplicationConstants.MAPPER_CLASS_ALERT_INBOX_MAPPER,
					"deleteSelectedAlertItem", "java.util.HashMap");
			loDeletionSuccessStatus = true;
			setMoState("Transaction Success:Alert Item deleted successfully for Notification Id:" + asNotificationIds);
		}
		catch (ApplicationException aoEx)
		{
			setMoState("Transaction Failure:Error occurred while deleting alert item for notification Id:"
					+ asNotificationIds);
			throw aoEx;
		}
		return loDeletionSuccessStatus;
	}

	/**
	 * This method insert the alert in the Notification Table
	 * 
	 * @param asNotificationId Notification ID for a notification
	 * @param aoMyBatisSession MyBatis SQL session
	 * @return loNotificationInsertStatus
	 * @throws ApplicationException
	 */
	public Boolean updateAlertInboxInformation(String asNotificationId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean loNotificationInsertStatus = null;
		HashMap<String, String> loQueryMap = new HashMap<String, String>();
		try
		{
			loQueryMap.put("asNotificationId", asNotificationId);
			DAOUtil.masterDAO(aoMyBatisSession, loQueryMap, ApplicationConstants.MAPPER_CLASS_ALERT_INBOX_MAPPER,
					"putNotificationStatus", "java.util.HashMap");
			loNotificationInsertStatus = true;
			setMoState("Transaction Success: Alert Information updated successfully for notification Id:"
					+ asNotificationId);
		}
		catch (ApplicationException aoEx)
		{
			setMoState("Transaction Failure: Error occurred while updated alert information for notification Id:"
					+ asNotificationId);
			throw aoEx;
		}
		return loNotificationInsertStatus;
	}

	/**
	 * This method returns the alerts count from the database
	 * 
	 * @param asUserId User Id of logged in user
	 * @param asNotificationType Notification type for a notification
	 * @param asToDate To Date
	 * @param asFromDate From Date
	 * @param aoMyBatisSession MyBatis SQL session
	 * @return liAlertRowCount row count of Alerts
	 * @throws ApplicationException
	 */
	public Integer getAlertInboxListCountFromDAO(HashMap lohmHashMap, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		int liAlertRowCount = 0;
		try
		{
			String lsNotificationType = (String) lohmHashMap.get("asNotificationType");

			if (lsNotificationType.equalsIgnoreCase("%"))
			{
				liAlertRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, lohmHashMap,
						ApplicationConstants.MAPPER_CLASS_ALERT_INBOX_MAPPER, "selectAlertInboxListCountAll",
						"java.util.HashMap");
			}
			else
			{
				liAlertRowCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, lohmHashMap,
						ApplicationConstants.MAPPER_CLASS_ALERT_INBOX_MAPPER, "selectAlertInboxListCount",
						"java.util.HashMap");
			}
			setMoState("Transaction Success: Alert Count fetched successfully for user Id:"
					+ lohmHashMap.get("asUserId"));
		}
		catch (ApplicationException aoEx)
		{
			setMoState("Transaction Failure: Error occurred while fetching user count for user Id:"
					+ lohmHashMap.get("asUserId"));
			throw aoEx;
		}
		return liAlertRowCount;
	}

	/**
	 * This Methods Fetches the users count
	 * 
	 * @param aoMyBatisSession Sql Session
	 * @param asUserId User ID
	 * @return liUserAccountCount
	 * @throws ApplicationException
	 */

	public Integer getUserAccountCount(SqlSession aoMyBatisSession, String asUserId) throws ApplicationException
	{
		int liUserAccountCount = 0;
		try
		{
			liUserAccountCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asUserId,
					ApplicationConstants.MAPPER_CLASS_ALERT_INBOX_MAPPER, "getUserAccountCount", "java.lang.String");
			setMoState("Transaction Success: User account count fetched successfully for alert portlet");
		}
		catch (ApplicationException aoEx)
		{
			setMoState("Transaction Failure: Error occurred while fetching user account count for alert portlet");
			throw aoEx;
		}
		return liUserAccountCount;
	}

	/**
	 * This method is added as a part of Release 5 to get Alert Box UnRead Data
	 * @param aoMyBatisSession Sql Session
	 * @param loParamHashMap HashMap<String, String>
	 * @return lsCount String
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public String getAlertBoxUnReadData(SqlSession aoMyBatisSession, HashMap<String, String> loParamHashMap)
			throws ApplicationException
	{
		String lsCount = HHSR5Constants.EMPTY_STRING;
		try
		{
			lsCount = (String) DAOUtil.masterDAO(aoMyBatisSession, loParamHashMap,
					ApplicationConstants.MAPPER_CLASS_ALERT_INBOX_MAPPER, HHSR5Constants.GET_ALERT_BOX_UN_READ_DATA,
					HHSR5Constants.JAVA_UTIL_HASH_MAP);
			HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
					.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			Integer liAlertCount = Integer.parseInt((String) loApplicationSettingMap.get(HHSR5Constants.ALERT_MAX_COUNT
					+ HHSConstants.UNDERSCORE + HHSR5Constants.DEFAULT_VALUE));
			if (Integer.parseInt(lsCount) > liAlertCount)
			{
				return HHSR5Constants.FIVE_HUNDRED_PLUS;
			}
		}
		catch (ApplicationException aoEx)
		{
			setMoState("Transaction Failure: Error occurred getAlertBoxUnReadData");
			throw aoEx;
		}
		return lsCount;
	}
	// R5 Changes Ends
}
