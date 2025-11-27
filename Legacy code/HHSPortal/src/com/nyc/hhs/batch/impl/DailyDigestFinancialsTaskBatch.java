package com.nyc.hhs.batch.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.HHSTransactionManager;
import com.nyc.hhs.model.DefaultAssignment;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

public class DailyDigestFinancialsTaskBatch implements IBatchQueue
{
	private static final LogInfo LOG_OBJECT = new LogInfo(DailyDigestFinancialsTaskBatch.class);

	/**
	 * This method is the entry point for the execution of the batch. it will
	 * start execution of the batch. Changes done for enhancement 6508 for
	 * Release 3.6.0
	 */
	
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
	
		List<DefaultAssignment> loDefaultAssignment = null;
		try
		{
			//Release 5
			P8UserSession loUserSession = setP8SessionVariables();
			loDefaultAssignment = getQueueWorkItems(loUserSession,
					"HHSAcceleratorProcessQueue");
		sendMail(loDefaultAssignment);
		
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in DailyDigestFinancialsTaskBatch.executeQueue()", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception in DailyDigestFinancialsTaskBatch.executeQueue()", aoExp);
			throw new ApplicationException("Exception in DailyDigestFinancialsTaskBatch.executeQueue()", aoExp);
		}
	}

	/**
	 * This method is used to fetch the work items present in the mentioned
	 * Queue since the time period mentioned in the argument
	 * 
	 * @param aoUserSession P8UserSession Bean Object
	 * @param asQueueName Queue name
	 * @param aiStuckTime Time since the items stucked in the queue
	 * @param aoQueueItemsDetailsBean Stuck Queue items details
	 * @return Details of the work items present in the queue
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private List<DefaultAssignment> getQueueWorkItems(P8UserSession aoUserSession, String asQueueName) throws ApplicationException
	{

		List<DefaultAssignment> loDefaultAssignment = new ArrayList<DefaultAssignment>();
		Channel loChannelObj = new Channel();
		try
		{
			loChannelObj.setData(HHSConstants.QUEUE_NAME, asQueueName);
			loChannelObj.setData(HHSConstants.AO_FILENET_SESSION, aoUserSession);
			HHSTransactionManager.executeTransaction(loChannelObj, "getWorkItemsCountFromQueueDailyDigest");
			loDefaultAssignment = (List<DefaultAssignment>) loChannelObj.getData(HHSConstants.NUMBER_OF_WORKITEMS_IN_QUEUE);
		}
		/**
		 * catch the application exception thrown from the service layer log it
		 * into the console
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching QueWorkItem from Queue" + asQueueName, aoAppEx);
			throw aoAppEx;
		}
		/**
		 * catch any exception thrown from the code and wrap it into application
		 * exception and propagate forward
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetching QueWorkItem from Queue" + asQueueName, aoExp);
			throw new ApplicationException("Error while fetching QueWorkItem from Queue" + asQueueName, aoExp);
		}
		return loDefaultAssignment;
	}

	/**
	 * This method will read the HHS property file and set the required
	 * parameters to establish connection with PE server
	 * 
	 * @return P8UserSession P8UserSession bean object
	 * @throws Exception
	 */
	public static P8UserSession setP8SessionVariables() throws Exception
	{
		P8UserSession loUserSession = new P8UserSession();
		loUserSession.setContentEngineUri(PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				"FILENET_URI"));
		loUserSession.setObjectStoreName(PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				"OBJECT_STORE_NAME"));
		loUserSession.setIsolatedRegionName(PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				"CONNECTION_POINT_NAME"));
		loUserSession.setIsolatedRegionNumber(PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				"CONNECTION_POINT_NUMBER"));
		loUserSession.setUserId(HHSUtil.decryptASEString(PropertyLoader.getProperty(
				HHSConstants.HHS_SERVICE_PROPERTIES_PATH, "CE_USER_ID")));
		loUserSession.setPassword(HHSUtil.decryptASEString(PropertyLoader.getProperty(
				HHSConstants.HHS_SERVICE_PROPERTIES_PATH, "CE_PASSWORD")));
		return loUserSession;
	}

	
	/**
	 * Release 5
	 * @param aoStuckItemsDetailsBeans list of stuck items details
	 * @return boolean true if email sent success
	 * @throws ApplicationException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static boolean sendMail(List<DefaultAssignment> aoDefaultAssignment) throws ApplicationException
	{
		String lsEnvironment = System.getProperty("hhs.env");
		boolean lbMailSentFlag = false;
		try
		{
			if (lsEnvironment.indexOf("_") > 0)
			{
				String lsEnvArray[] = lsEnvironment.split("_");
				lsEnvironment = lsEnvArray[0];
				if (lsEnvironment.indexOf("-") > 0)
				{
					String lsEnvNameArray[] = lsEnvironment.split("-");
					lsEnvironment = lsEnvNameArray[0];
				}
			}
			 StringBuffer loSbMessage = new StringBuffer();
			
			 LOG_OBJECT.Debug("Start Daily Digest method");
			if(null!=aoDefaultAssignment && !aoDefaultAssignment.isEmpty()){

				LOG_OBJECT.Debug("Getting task details details ");
				List<DefaultAssignment> loDefaultAssignmentListSend = new ArrayList<DefaultAssignment>();
				
				 Map<String,String> loUserListMap=new HashMap<String, String>();
				    for(DefaultAssignment defaultAssignment:aoDefaultAssignment)
				    {
				        if(!loUserListMap.containsKey(defaultAssignment.getCreatedByUserId()))
				        {
				        	if(defaultAssignment.getCreatedByUserId()!=null)
				        	loUserListMap.put(defaultAssignment.getCreatedByUserId(), "");
				        }
				        }
				    
				    Iterator iter = loUserListMap.keySet().iterator();
				    while(iter.hasNext()){						
				        String lsUserId = iter.next().toString();
				        loDefaultAssignmentListSend = new ArrayList<DefaultAssignment>();
				        for(DefaultAssignment loDefaultAssignment:aoDefaultAssignment)
				        {
				        	if(loDefaultAssignment.getCreatedByUserId() !=null && loDefaultAssignment.getCreatedByUserId().equalsIgnoreCase(lsUserId))
				        	{
				        		loDefaultAssignmentListSend.add(loDefaultAssignment);
				        		
				        	}
				        }
				        
				    Map<String,Integer> loUserListMap2 =new HashMap<String, Integer>();
				    int liCount = 1;
				    for(DefaultAssignment loDefaultAssignment:loDefaultAssignmentListSend)
				    {
				        if(loUserListMap2.containsKey(loDefaultAssignment.getTaskType()))
				        	loUserListMap2.put(loDefaultAssignment.getTaskType(),new Integer((Integer)loUserListMap2.get(loDefaultAssignment.getTaskType()) + liCount));
				        else
				        	loUserListMap2.put(loDefaultAssignment.getTaskType(), liCount);
				    }
				    
				    LOG_OBJECT.Debug("Getting user email id "+lsUserId);
				    LOG_OBJECT.Debug("Getting user details "+loUserListMap2);
				    
				    loSbMessage = new StringBuffer();
					loSbMessage.append("<br/><br/><p>");
					for (Map.Entry<String,Integer> entry : loUserListMap2.entrySet()) {
						loSbMessage.append("&nbsp;&nbsp;");
						loSbMessage.append(entry.getValue());
						loSbMessage.append("&nbsp;&nbsp;");
						loSbMessage.append(entry.getKey());
						loSbMessage.append(" Task(s)");
						loSbMessage.append("<br/><br/>");
						}
					
					loSbMessage.append("<br/><br/>");
					LOG_OBJECT.Debug("Display user message"+loSbMessage);
					List<String> loApprovedProviderList = new ArrayList<String>();
					loApprovedProviderList.add(lsUserId);
					insertNotificationDetails(loApprovedProviderList,loSbMessage.toString());
				    }
				    }
		}
		catch (Exception aoExp)
		{
			lbMailSentFlag = false;
			LOG_OBJECT.Error("Error while sending Notification Mail for daily digest", aoExp);
			throw new ApplicationException("Error while sending Notification Mail for daily digest", aoExp);
		}
		return lbMailSentFlag;
	}
	/**
	 * This method is used to insert Notification Details
	 * <ul>
	 * <li>
	 * Put key value in loNotificationMap</li>
	 * <li>Transaction called is <b>insertSMNotificationDetail<b></li>
	 * </ul>
	 * @param aoApprovedProviderList List<String>
	 * @param asAgencyTask String
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void insertNotificationDetails(List<String> aoApprovedProviderList, String asAgencyTask) throws ApplicationException
	{
		ArrayList loAlertsList = new ArrayList();
		HashMap loRequestMap = new HashMap();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT233");
		NotificationDataBean loNotificationDataBean = new NotificationDataBean();
		HashMap<String, String> loLinkMap = new HashMap<String, String>();
		loNotificationDataBean.setAgencyLinkMap(loLinkMap);
		loNotificationDataBean.setLinkMap(loLinkMap);
		try
		{
			Channel loChannelObj;
			loRequestMap.put("AGENCY_TASK", asAgencyTask);
			loRequestMap.put("AGENCY_TASK_LINK", new StringBuffer(PropertyLoader.getProperty(
					HHSConstants.HHS_SERVICE_PROPERTIES_PATH, "PROP_CITY_URL")).append(ApplicationConstants.AGENCY_TASK_INBOX_URL));
			loAlertsList.add("NT233");
			HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
			loNotificationMap.put(TransactionConstants.EVENT_ID_PARAMETER_NAME, loAlertsList);
			loNotificationDataBean.setAgencyList(aoApprovedProviderList);
			loNotificationMap.put(HHSConstants.ENTITY_TYPE, "Daily Digest");
			loNotificationMap.put(HHSConstants.ENTITY_ID, "agency_org");
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			loNotificationMap.put("NT233", loNotificationDataBean);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, HHSConstants.SYSTEM_USER);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, HHSConstants.SYSTEM_USER);
			loChannelObj = new Channel();
			LOG_OBJECT.Debug("Executing transaction insertNotificationDetail");
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loChannelObj.setData(HHSConstants.LO_HM_NOTIFY_PARAM, loNotificationMap);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.INSERT_NOTIFICATION_TRANS_NAME);
			LOG_OBJECT.Debug("Finished transaction insertNotificationDetail");
		}
		/**
		 * catch the application exception thrown from the service layer log it
		 * into the console
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in BatchComponent.insertNotificationDetails() ", aoAppEx);
			throw aoAppEx;
		}

	}
	/**
	 * This method is used to get Queue
	 * @param Map aoMParameters
	 * @return null
	 */

	@Override
	public List getQueue(Map aoMParameters)
	{
		return null;
	}

}
