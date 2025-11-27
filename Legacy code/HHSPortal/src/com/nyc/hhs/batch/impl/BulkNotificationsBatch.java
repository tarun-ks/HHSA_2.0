package com.nyc.hhs.batch.impl;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.model.BulkNotificationList;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

/**
 * This Batch will download the csv file in user's system and then process
 * notification. On download, the status in request_bulknotification table
 * updated from 'Not Started' to 'started' and on complete download it will be
 * 'finished' Request_id will also be the path of directory where This method
 * will get the request id from request_bulknotification table and accordingly
 * process notification In bulk notification table, entity id will be request
 * id.
 */

public class BulkNotificationsBatch implements IBatchQueue
{
	private static final LogInfo LOG_OBJECT = new LogInfo(BulkNotificationsBatch.class);

	/**
	 * This method provides the implementation of getQueue method of interface
	 * IBatchQueue
	 * 
	 * @param a map object containing parameters
	 * @return a list of notification bean
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public List<NotificationBean> getQueue(Map aoMParameters)
	{
		return null;
	}

	/**
	 * This method provides the implementation of overridden method of interface
	 * and will execute notification batch by calling notification service
	 * 
	 * @param a list of Queue
	 */
	@Override
	@SuppressWarnings(
	{ "rawtypes", "unchecked", "unused" })
	public void executeQueue(List aoLQueue)
	{
		try
		{
			List<String> loNotificationAlertList = new ArrayList<String>();
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			Object loCacheObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
					"/" + ApplicationConstants.TRANSACTION_CONFIG));
			loCacheManager.putCacheObject(ApplicationConstants.TRANSACTION_ELEMENT, loCacheObject);

			Channel loChannelObj = new Channel();
			// get data from request_bulkNotificationTable, includes Request_id
			// as filename
			TransactionManager.executeTransaction(loChannelObj, HHSR5Constants.GET_INPUT_NOTIFICATIONS_STATUS_LIST_TX);
			List<BudgetList> loInputBudgetList = (List<BudgetList>) loChannelObj
					.getData(HHSR5Constants.INPUT_NOTIFICATION_LIST);

			if (null != loInputBudgetList && loInputBudgetList.size() > 0)
			{
				for (BudgetList loInputData : loInputBudgetList)
				{
					String lsBatchId = loInputData.getRequestNotificationId();
					String lsProgramName = loInputData.getProgramName();
					String lsFiscalYear = loInputData.getFiscalYearId();
					String lsNotificationId = HHSR5Constants.EMPTY_STRING;
					loChannelObj.setData(HHSR5Constants.NOTIFICATION_STATUS, PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSR5Constants.EXPORT_NOTIFICATIONS_STARTED));
					String lsUserId = loInputData.getUserId();
					loChannelObj.setData(HHSR5Constants.USER_ID, lsUserId);
					loChannelObj.setData(HHSR5Constants.PROGRAM_ID, loInputData.getProgramId().toString());
					loChannelObj.setData(HHSR5Constants.FISCAL_YEAR, lsFiscalYear);
					loChannelObj.setData(HHSConstants.SESSION_ID, lsBatchId);
					String lsOrganizationName = loInputData.getOrgType();
					loChannelObj.setData(HHSR5Constants.ORGANIZATION_NAME, lsOrganizationName);
					// download file in local directory
					TransactionManager.executeTransaction(loChannelObj,
							HHSR5Constants.UPDATE_EXPORT_NOTIFICATION_STATUS);
					String lsNotificationStatus = (String) loChannelObj
							.getData(HHSR5Constants.EXPORT_NOTIFICATION_STATUS);
					LOG_OBJECT.Info("Staus Updated as Started for Export Notification:");
					if (lsNotificationStatus.equalsIgnoreCase(HHSConstants.TRUE))
					{
						TransactionManager.executeTransaction(loChannelObj, HHSR5Constants.BULK_NOTIFICATION_EXPORT_TX);
						LOG_OBJECT.Info("Staus Updated as Finished for Export Notification:");
					}
					// Process notification
					// Bulk Export fileName as Directory
					HashMap<String, String> loExportStatus = (HashMap<String, String>) loChannelObj
							.getData(HHSR5Constants.EXPORT_NOTIFICATION_STATUS);
					String lsFileName = loExportStatus.get(HHSR5Constants.FILE_NAME);
					List<BulkNotificationList> loBulkNotificationList = (List<BulkNotificationList>) loChannelObj
							.getData(HHSR5Constants.EXPORT_NOTIFICATIONS_LIST);
					if (null != loBulkNotificationList && !loBulkNotificationList.isEmpty())
					{
						lsNotificationId = HHSR5Constants.NT324;
					}
					else
					{
						lsNotificationId = HHSR5Constants.NT326;
					}
					processBulkNotification(lsBatchId, lsUserId, lsFileName, lsNotificationId, lsOrganizationName,
							lsProgramName, lsFiscalYear);
				}
			}
			else
			{
				LOG_OBJECT.Info("No Pending Record Found for EXPORT Bulk Notification batch:");
			}

		}
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error occurred while running EXPORT Bulk Notification batch:", loExp);
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while running EXPORT Bulk Notification batch:", loExp);
		}
	}

	/**
	 * This method is used to write notifications in File
	 * 
	 * @param asFilename String
	 * @param aoExportData StringBuffer
	 * @throws IOException If an Application Exception occurs
	 */
	@SuppressWarnings("unused")
	private void writeTaskExportToFile(String asFilename, StringBuffer aoExportData) throws IOException
	{
		BufferedWriter loBufferedWriter = new BufferedWriter(new FileWriter(asFilename));
		loBufferedWriter.write(aoExportData.toString());
		loBufferedWriter.flush();
		loBufferedWriter.close();
	}

	/**
	 * Method is added to process notification for NT324 Bulk notification
	 * process
	 * @author amit.kumar.bansal
	 * @param aoRequestId
	 * @throws ApplicationException
	 */
	private void processBulkNotification(String aoRequestId, String asUserID, String asFileName,
			String asNotificationId, String asOrgType, String asProgName, String asFiscalYear)
			throws ApplicationException
	{
		Channel loChannel = new Channel();
		try
		{
			String lsServerName = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSR5Constants.SERVER_NAME_FOR_PROVIDER_BATCH);
			String lsServerPort = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSR5Constants.SERVER_PORT_FOR_PROVIDER_BATCH);
			String lsContextPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
					HHSR5Constants.CONTEXT_PATH_FOR_PROVIDER_BATCH);
			String lsAppProtocol = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					HHSConstants.PROP_CITY_URL);
			sendBulkNotifications(lsServerName, lsServerPort, lsContextPath, lsAppProtocol, loChannel,
					asUserID, aoRequestId, asFileName, asNotificationId, asOrgType, asProgName, asFiscalYear);

			NotificationDataBean loNotificationNT324 = new NotificationDataBean();
			HashMap<String, String> loAgencyLinkMap = new HashMap<String, String>();

			loNotificationNT324.setAgencyLinkMap(loAgencyLinkMap);
			// This method will process notification for Export_Task.
			// This method will populate group_notification table
			TransactionManager.executeTransaction(loChannel, HHSR5Constants.PROCESS_NOTIFICATION_FOR_EXPORT_BATCH);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error in processForBatchExport Method :", loAppEx);
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error  in processForBatchExport Method :", loExp);
			throw new ApplicationException("Exception in processForBatchExport Method ", loExp);
		}
	}

	/**
	 * This method is added in R6. This method will populate the list of details
	 * which will require to process notifications.
	 * @param asServerName
	 * @param asServerPort
	 * @param asContextPath
	 * @param asAppProtocol
	 * @param aoChannel
	 * @param asUserId
	 * @param asEntityId
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	private HashMap<String, Object> sendBulkNotifications(String asServerName, String asServerPort,
			String asContextPath, String asAppProtocol, Channel aoChannel, String asUserId, String asEntityId,
			String asFileName, String asNotificationId, String asOrgName, String asProgName, String asFiscalYear)
			throws ApplicationException
	{
		HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
		HashMap<String,String> loLinkedMap = new HashMap<String,String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add(asNotificationId);
		loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		try
		{
			TreeSet<String> loTreeSet = new TreeSet<String>();
			loTreeSet = FileNetOperationsUtils.getNYCAgencyListFromDB();
			Iterator<String> loItrAgencyList = loTreeSet.iterator();
			String lsAgencyUser = HHSConstants.EMPTY_STRING;
			while (loItrAgencyList.hasNext())
			{
				String loUserOrg = loItrAgencyList.next();
				if (loUserOrg.split(HHSConstants.DELIMETER_SIGN)[0].trim().equalsIgnoreCase(asOrgName))
				{
					lsAgencyUser = loUserOrg.split(HHSConstants.HYPHEN)[1];
					break;
				}
			}
			loNotificationMap.put(ApplicationConstants.ENTITY_ID, asEntityId);
			loNotificationMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, asUserId);
			NotificationDataBean loNotificationNT324 = new NotificationDataBean();
			NotificationDataBean loNotificationNT326 = new NotificationDataBean();
			List<String> loOrgId = new ArrayList<String>();
			loOrgId.add("");
			StringBuffer loSbMessage = new StringBuffer();
			String lsServerName = PropertyLoader.getProperty(ApplicationConstants.PROPERTY_FILE,
					HHSConstants.PROP_CITY_URL);
			if (null != asNotificationId && !asNotificationId.isEmpty()
					&& asNotificationId.equalsIgnoreCase(HHSR5Constants.NT324))
			{
				loNotificationNT324.setAgencyList(loOrgId);
				String lsFilePath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
						HHSConstants.BULK_UPLOAD_ABSOLUTE_PATH) + asEntityId + "\\";
				//Start: updated for notification link snapshot
				loSbMessage.append(asAppProtocol).append("/ExportAllBatch");
				loSbMessage.append("?requestId=");
				loSbMessage.append(asEntityId).append("&actionId=exportNotification").append("&filepath=")
						.append(lsFilePath).append("&fileName=").append(asFileName);
				StringBuffer loSbMessageUrl = new StringBuffer();
				LOG_OBJECT.Info("Server Name: for EXPORT Bulk Notification batch" + lsServerName);
				loSbMessageUrl.append(lsServerName).append(ApplicationConstants.PORTAL_URL);
				loLinkedMap.put(HHSConstants.LINK,loSbMessageUrl.toString());
				loLinkedMap.put(HHSConstants.LINK1, loSbMessage.toString());
				//End: Updated for notification link snapshot
				loNotificationNT324.setLinkMap(loLinkedMap);
				loNotificationMap.put(HHSR5Constants.NT324, loNotificationNT324);
				//defect 8640 start :moved in if block to handle null pointer exception.
				loRequestMap.put(HHSR5Constants.FILE_NAME, asFileName.split(HHSR5Constants.DELIMETER_SIGN)[1].trim());
				//defect 8640 END
			}
			else
			{

				loSbMessage.append(lsServerName).append(HHSConstants.AGENCY_SETTING_CITY)
						.append(HHSR5Constants.AGENCY_SETTING_TAB_URL + HHSR5Constants.BULK_NOTIFICATIONS);
				//Start:Added for notification link snapshot
				loLinkedMap.put(HHSConstants.LINK1, loSbMessage.toString());
				loNotificationNT326.setLinkMap(loLinkedMap);
				//End:Added for notification link snapshot
				loNotificationNT326.setAgencyList(loOrgId);
				loNotificationMap.put(HHSR5Constants.NT326, loNotificationNT326);
			}
			loRequestMap.put(HHSConstants.USER_ORG, lsAgencyUser.trim());
			loRequestMap.put(HHSR5Constants.PROGRAM_NAME, asProgName);
			loRequestMap.put(HHSR5Constants.FISCAL_YEAR_CAPS, asFiscalYear);
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			aoChannel.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error in sendBulkNotifications Method:", loAppEx);
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error  in sendBulkNotifications Method :", loExp);
			throw new ApplicationException("Exception in sendBulkNotifications Method ", loExp);
		}
		return loNotificationMap;
	}

}
