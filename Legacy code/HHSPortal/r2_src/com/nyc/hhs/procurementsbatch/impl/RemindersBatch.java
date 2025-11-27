package com.nyc.hhs.procurementsbatch.impl;

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
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.NotificationSettingsBean;
import com.nyc.hhs.model.SMAlertNotificationBean;
import com.nyc.hhs.util.PropertyLoader;

/**
 * 
 * This class will send different due date reminders to the desired addressee if
 * the due dates approached
 * 
 */
public class RemindersBatch implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(RemindersBatch.class);

	int miProposalDueDateFirstNotification = 0;
	int miProposalDueDateSecondNotification = 0;
	int miRfpReleaseDateFirstNotification = 0;
	int miRfpReleaseDateSecondNotification = 0;
	int miFirstRoundEvaluationDateNotification = 0;
	int miFinalEvaluationDateNotification = 0;

	/**
	 * Blank implementation of getQueue method of interface
	 * 
	 * @param aoMParameters HashMap of Parameter
	 * @return null
	 */
	@Override
	public List<RemindersBatch> getQueue(Map aoMParameters)
	{
		return null;
	}

	/**
	 * Implementation of the executeQueue method . This method will call all the
	 * other methods for executing the batch operations
	 * 
	 * @param aoLQueue List of Queue
	 * @throws ApplicationException if any exception occurs
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		try
		{

			// call below method to get the settings from the
			// application_settings table and initialize the member variables
			// with the values fetched
			fetchNotificationSettings();
			// call the function for sending reminders for proposal due date
			// approaching
			sendProposalDueDateApprochingAlert();

			// call the function to send notification for rfp release due date
			// approaching
			sendRfpReleaseDueDateApprochingAlert();

			// call the method to send first round evaluation date notification
			sendFirstRoundEvaluationDateApproachAlert();

			// call the method to insert alert details for final evaluation
			// date
			sendFinalEvaluationDateApproachAlert();
		}
		/**
		 * catch the application exception thrown from the service layer log it
		 * into the console
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in RemindersBatch.executeQueue()", aoAppEx);
			throw aoAppEx;
		}
	} // end function executeQueue

	/**
	 * This method is used for fetching the values for various notification
	 * settings from database
	 * 
	 * @throws ApplicationException IF ANY EXCEPTION OCCURS
	 */
	private void fetchNotificationSettings() throws ApplicationException
	{
		try
		{
			LOG_OBJECT.Debug("Entered fetchNotificationSettings()");

			NotificationSettingsBean loNotificationSettingsBean = null;
			List<NotificationSettingsBean> loNotificationSettingsBeanList = null;

			Channel loChannelObj = new Channel();

			// Get the settings from APPLICATION_SETTINGS table for
			// Notifications

			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_NOTIFICATION_SETTINGS_TRANS_NAME);
			loNotificationSettingsBeanList = (List<NotificationSettingsBean>) loChannelObj
					.getData(HHSConstants.FETCH_NOTIFICATION_SETTINGS_TRANS_OUTPUT);

			Iterator<NotificationSettingsBean> loIteratorSett = loNotificationSettingsBeanList.iterator();

			// Iterate through the NotificationSettings bean list obtained and
			// fetch the values for
			// all constants obtained from DB table APPLICATION_SETTINGS

			while (loIteratorSett.hasNext())
			{
				loNotificationSettingsBean = loIteratorSett.next();
				if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(
						HHSConstants.PROPOSALDUEDATEFIRSTNOTIFICATION))
				{
					miProposalDueDateFirstNotification = Integer.parseInt(loNotificationSettingsBean.getSettingValue());
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(
						HHSConstants.PROPOSALDUEDATESECONDNOTIFICATION))
				{
					miProposalDueDateSecondNotification = Integer
							.parseInt(loNotificationSettingsBean.getSettingValue());
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(
						HHSConstants.RFPRELEASEDATEFIRSTNOTIFICATION))
				{
					miRfpReleaseDateFirstNotification = Integer.parseInt(loNotificationSettingsBean.getSettingValue());
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(
						HHSConstants.RFPRELEASEDATESECONDNOTIFICATION))
				{
					miRfpReleaseDateSecondNotification = Integer.parseInt(loNotificationSettingsBean.getSettingValue());
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(
						HHSConstants.FIRSTROUNDEVALUATIONDATENOTIFICATION))
				{
					miFirstRoundEvaluationDateNotification = Integer.parseInt(loNotificationSettingsBean
							.getSettingValue());
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(
						HHSConstants.FINALEVALUATIONDATENOTIFICATION))
				{
					miFinalEvaluationDateNotification = Integer.parseInt(loNotificationSettingsBean.getSettingValue());
				}
			}

			LOG_OBJECT.Debug("Notification settings obtained");

			// end get settings

		}
		/**
		 * catch the application exception thrown from the service layer log it
		 * into the console
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in BatchComponent.fetchNotificationSettings()", aoAppEx);
			throw aoAppEx;
		}

		LOG_OBJECT.Debug("Exited fetchNotificationSettings()");

	}

	/**
	 * This method is used to send the proposal due date reminder to the desired
	 * recipients
	 * <ul>
	 * <li>Get the list of procurements which proposal due date is either 7 days
	 * or 1 days by executing <code>fetchProposalDueDateAlertDetails</code>
	 * transaction</li>
	 * <li>if the days difference is 7 days then send NT216,NT205,AL207,AL209</li>
	 * <li>If the days difference is equal to 1 then send NT217,AL208,AL210</li>
	 * </ul>
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void sendProposalDueDateApprochingAlert() throws ApplicationException
	{
		HashMap loHMap = new HashMap();
		Channel loChannelObj = new Channel();
		List<SMAlertNotificationBean> loSMAlertNotificationBeansList = null;
		SMAlertNotificationBean loSMAlertNotificationBean = null;
		String lsProcurementTitle = null;
		int liNoOfDays = 0;
		List<String> loNotificationNameList = new ArrayList<String>();
		String lsProcurementId = null;
		String lsProcurementAgencyId = null;
		List<String> loApprovedProviderList = null;
		Boolean loNeedToProcessNotification = Boolean.FALSE;
		List<String> loAgencyList = new ArrayList<String>();
		StringBuffer lsBfProviderApplicationUrl = null;
		StringBuffer lsBfCityAgencyApplicationUrl = null;
		String lsServerName = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				"SERVER_NAME_FOR_PROVIDER_BATCH");
		String lsServerPort = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				"SERVER_PORT_FOR_PROVIDER_BATCH");
		String lsContextPath = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				"CONTEXT_PATH_FOR_PROVIDER_BATCH");
		String lsAppProtocol = PropertyLoader.getProperty(HHSConstants.HHS_SERVICE_PROPERTIES_PATH,
				"SERVER_PROTOCOL_FOR_PROVIDER_BATCH");
		try
		{
			LOG_OBJECT.Debug("Entered sendReminders()");
			loHMap.put(HHSConstants.NO_OF_DAYS, miProposalDueDateFirstNotification);
			loChannelObj.setData(HHSConstants.LOCAL_HASH_MAP, loHMap);
			HHSTransactionManager.executeTransaction(loChannelObj,
					HHSConstants.FETCH_PROPOSAL_DUE_DATE_ALERT_DETAILS_TRANS_NAME);
			loSMAlertNotificationBeansList = (List<SMAlertNotificationBean>) loChannelObj
					.getData(HHSConstants.DUE_DATE_ALERT_DETAILS_TRANS_OUTPUT);
			Iterator<SMAlertNotificationBean> loIterator = loSMAlertNotificationBeansList.iterator();
			while (loIterator.hasNext())
			{
				loNotificationNameList.clear();
				loSMAlertNotificationBean = (SMAlertNotificationBean) loIterator.next();
				liNoOfDays = loSMAlertNotificationBean.getNoOfDays();
				lsProcurementTitle = loSMAlertNotificationBean.getProcurementTitle();
				lsProcurementId = loSMAlertNotificationBean.getProcurementId();
				if (liNoOfDays == miProposalDueDateFirstNotification)
				{
					loNotificationNameList.add(HHSConstants.NT216);
					loNotificationNameList.add(HHSConstants.NT205);
					loNotificationNameList.add(HHSConstants.AL207);
					loNotificationNameList.add(HHSConstants.AL209);
				}
				else if (liNoOfDays == miProposalDueDateSecondNotification)
				{
					loNotificationNameList.add(HHSConstants.NT217);
					loNotificationNameList.add(HHSConstants.AL208);
					loNotificationNameList.add(HHSConstants.AL210);
				}
				if (null != loNotificationNameList && !loNotificationNameList.isEmpty())
				{
					loNeedToProcessNotification = filterNotificationsAlreadySent(loNotificationNameList,
							lsProcurementId);
					if (loNeedToProcessNotification && !loNotificationNameList.isEmpty())
					{
						for (Iterator loNotificationNameItr = loNotificationNameList.iterator(); loNotificationNameItr
								.hasNext();)
						{
							String lsNotificationName = (String) loNotificationNameItr.next();
							if (lsNotificationName.equalsIgnoreCase(HHSConstants.NT205)
									|| lsNotificationName.equalsIgnoreCase(HHSConstants.AL209)
									|| lsNotificationName.equalsIgnoreCase(HHSConstants.AL210))
							{
								loApprovedProviderList = getApprovedProviderNames(lsProcurementId);
								if (null != loApprovedProviderList && !loApprovedProviderList.isEmpty())
								{
									lsBfProviderApplicationUrl = new StringBuffer();
									lsBfProviderApplicationUrl.append(lsAppProtocol);
									lsBfProviderApplicationUrl.append("://");
									lsBfProviderApplicationUrl.append(lsServerName);
									lsBfProviderApplicationUrl.append(":");
									lsBfProviderApplicationUrl.append(lsServerPort);
									lsBfProviderApplicationUrl.append("/");
									lsBfProviderApplicationUrl.append(lsContextPath);
									lsBfProviderApplicationUrl
											.append(ApplicationConstants.PROCUREMENT_SUMMARY_PROVIDER_URL);
									lsBfProviderApplicationUrl.append(lsProcurementId);
									insertNotificationDetails(lsProcurementId, lsProcurementTitle, lsNotificationName,
											loApprovedProviderList, lsBfProviderApplicationUrl.toString());
									loAgencyList.clear();
								}
							}
							else
							{
								lsBfCityAgencyApplicationUrl = new StringBuffer(PropertyLoader.getProperty(
										HHSConstants.HHS_SERVICE_PROPERTIES_PATH, "PROP_CITY_URL"));
								lsBfCityAgencyApplicationUrl
										.append(ApplicationConstants.PROCUREMENT_SUMMARY_AGENCY_URL);
								lsBfCityAgencyApplicationUrl.append(lsProcurementId);
								lsProcurementAgencyId = loSMAlertNotificationBean.getProcurementAgencyId();
								loAgencyList.add(lsProcurementAgencyId);
								insertNotificationDetails(lsProcurementId, lsProcurementTitle, lsNotificationName,
										loAgencyList, lsBfCityAgencyApplicationUrl.toString());
								loAgencyList.clear();
							}
						}
					}
				}
			}

		}
		/**
		 * catch the application exception thrown from the service layer log it
		 * into the console
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while sending Proposal  duedate notification", aoAppEx);
			throw aoAppEx;
		}
		/**
		 * catch any exception thrown from the code and wrap it into application
		 * exception and propagate forward
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while sending Proposal  duedate notification", aoExp);
			throw new ApplicationException("Error while sending Proposal  duedate notification", aoExp);
		}
	}

	/**
	 * Changed method By: Harish Kumar Reason: To resolve production issue found
	 * in ReminderBatch for Notifications NT211,NT212 Changes Done:StringBuffer
	 * lsBfCityAgencyApplicationUrl is instantiated inside While Loop to avoid
	 * URL appending This method is used to send the rfp release due date
	 * reminder to the desired recipients
	 * <ul>
	 * <li>Get the list of procurements which rfp release due date is either 20
	 * days or 5 days by executing
	 * <code>fetchRfpReleaseDueDateAlertDetails</code> transaction</li>
	 * <li>if the days difference is 20 days then send NT211</li>
	 * <li>If the days difference is equal to 5 then send NT212</li>
	 * </ul>
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void sendRfpReleaseDueDateApprochingAlert() throws ApplicationException
	{
		HashMap loHMap = new HashMap();
		Channel loChannelObj = new Channel();
		List<SMAlertNotificationBean> loSMAlertNotificationBeansList = null;
		SMAlertNotificationBean loSMAlertNotificationBean = null;
		String lsProcurementTitle = null;
		String lsProcurementId = null;
		int liNoOfDays = 0;
		Boolean loNeedToProcessNotification = Boolean.FALSE;

		try
		{
			LOG_OBJECT.Debug("Entered sendReminders()");
			loHMap.put(HHSConstants.NO_OF_DAYS, miRfpReleaseDateFirstNotification);
			loChannelObj.setData(HHSConstants.LOCAL_HASH_MAP, loHMap);
			HHSTransactionManager.executeTransaction(loChannelObj,
					HHSConstants.FETCH_RFP_RELEASE_DUE_DATE_ALERT_DETAILS_TRANS_NAME);
			loSMAlertNotificationBeansList = (List<SMAlertNotificationBean>) loChannelObj
					.getData(HHSConstants.DUE_DATE_ALERT_DETAILS_TRANS_OUTPUT);
			Iterator<SMAlertNotificationBean> loRfpReleaseDueDateItr = loSMAlertNotificationBeansList.iterator();
			while (loRfpReleaseDueDateItr.hasNext())
			{
				loSMAlertNotificationBean = (SMAlertNotificationBean) loRfpReleaseDueDateItr.next();
				liNoOfDays = loSMAlertNotificationBean.getNoOfDays();
				lsProcurementTitle = loSMAlertNotificationBean.getProcurementTitle();
				lsProcurementId = loSMAlertNotificationBean.getProcurementId();
				// StringBuffer lsBfCityAgencyApplicationUrl is instantiated
				// inside while loop to avoid URL appending
				StringBuffer lsBfCityAgencyApplicationUrl = new StringBuffer(PropertyLoader.getProperty(
						HHSConstants.HHS_SERVICE_PROPERTIES_PATH, "PROP_CITY_URL"));
				// end
				if (liNoOfDays == miRfpReleaseDateFirstNotification)
				{
					loNeedToProcessNotification = filterNotificationsAlreadySent(HHSConstants.NT211, lsProcurementId);
					if (loNeedToProcessNotification)
					{

						lsBfCityAgencyApplicationUrl.append(ApplicationConstants.PROCUREMENT_SUMMARY_AGENCY_URL);
						lsBfCityAgencyApplicationUrl.append(lsProcurementId);
						insertNotificationDetails(lsProcurementId, lsProcurementTitle, HHSConstants.NT211, null,
								lsBfCityAgencyApplicationUrl.toString());
					}
				}
				else if (liNoOfDays == miRfpReleaseDateSecondNotification)
				{
					loNeedToProcessNotification = filterNotificationsAlreadySent(HHSConstants.NT212, lsProcurementId);
					if (loNeedToProcessNotification)
					{
						lsBfCityAgencyApplicationUrl.append(ApplicationConstants.PROCUREMENT_SUMMARY_AGENCY_URL);
						lsBfCityAgencyApplicationUrl.append(lsProcurementId);
						insertNotificationDetails(lsProcurementId, lsProcurementTitle, HHSConstants.NT212, null,
								lsBfCityAgencyApplicationUrl.toString());
					}
				}
			}
		}
		/**
		 * catch the application exception thrown from the service layer log it
		 * into the console
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while sending RFP release duedate notification", aoAppEx);
			throw aoAppEx;
		}
		/**
		 * catch any exception thrown from the code and wrap it into application
		 * exception and propagate forward
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while sending RFP release duedate notification", aoExp);
			throw new ApplicationException("Error while sending RFP release duedate notification", aoExp);
		}
	}

	/**
	 * This method is used to send the First Round due date reminder to the
	 * desired recipients
	 * <ul>
	 * <li>Get the list of procurements which rfp release due date is either 20
	 * days or 3 days by executing
	 * <code>fetchFirstRoundEvaluationDueDateAlertDetails</code> transaction</li>
	 * <li>if the days difference is 3 days then send NT204 to all evaluators</li>
	 * <li>If the days difference is equal to 3 then send AL204 to agency acco
	 * staff and manager</li>
	 * </ul>
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void sendFirstRoundEvaluationDateApproachAlert() throws ApplicationException
	{
		HashMap loHMap = new HashMap();
		Channel loChannelObj = new Channel();
		List<SMAlertNotificationBean> loSMAlertNotificationBeansList = null;
		List<String> loAgencyList = new ArrayList<String>();
		List<String> loExtIntEvaluatorUserIDList = null;
		List<String> loExtIntEvaluatorEmailIDList = null;
		SMAlertNotificationBean loSMAlertNotificationBean = null;
		String lsProcurementTitle = null;
		String lsProcurementAgencyId = null;
		String lsProcurementId = null;
		String lsEvaluationGroupId = null;
		String lsCompetitionPoolId = null;
		String lsEvaluationPoolMappingId = null;	

		Boolean loNeedToProcessNotification = Boolean.FALSE;
		StringBuffer lsBfCityAgencyApplicationUrl = new StringBuffer();
		try
		{
			LOG_OBJECT.Debug("Entered sendReminders()");
			loHMap.put(HHSConstants.NO_OF_DAYS, miFirstRoundEvaluationDateNotification);
			loChannelObj.setData(HHSConstants.LOCAL_HASH_MAP, loHMap);
			HHSTransactionManager.executeTransaction(loChannelObj,
					HHSConstants.FETCH_FRST_RND_EVALDATE_DUE_DATE_ALERT_DETAILS_TRANS_NAME);
			loSMAlertNotificationBeansList = (List<SMAlertNotificationBean>) loChannelObj
					.getData(HHSConstants.DUE_DATE_ALERT_DETAILS_TRANS_OUTPUT);
			Iterator<SMAlertNotificationBean> loRfpReleaseDueDateItr = loSMAlertNotificationBeansList.iterator();
			while (loRfpReleaseDueDateItr.hasNext())
			{
				loSMAlertNotificationBean = (SMAlertNotificationBean) loRfpReleaseDueDateItr.next();
				lsProcurementTitle = loSMAlertNotificationBean.getProcurementTitle();
				lsProcurementId = loSMAlertNotificationBean.getProcurementId();
				lsEvaluationGroupId = loSMAlertNotificationBean.getEvaluationGroupId();
				lsCompetitionPoolId = loSMAlertNotificationBean.getCompetitionPoolId();
				lsEvaluationPoolMappingId = loSMAlertNotificationBean.getEvaluationPoolMappingId();
				lsProcurementAgencyId = loSMAlertNotificationBean.getProcurementAgencyId();
				loAgencyList.add(lsProcurementAgencyId);
				if (null != lsProcurementId && !lsProcurementId.isEmpty())
				{
					loExtIntEvaluatorUserIDList = getExtIntEvaluatorList(lsProcurementId).get(
							HHSConstants.USER_ID.toUpperCase());
					loExtIntEvaluatorEmailIDList = getExtIntEvaluatorList(lsProcurementId).get(
							ApplicationConstants.KEY_SESSION_EMAIL_ID.toUpperCase());
					// insert details for notification
					loNeedToProcessNotification = filterNotificationsAlreadySent(HHSConstants.NT204, lsProcurementId);
					if (loNeedToProcessNotification)
					{
						lsBfCityAgencyApplicationUrl = new StringBuffer(PropertyLoader.getProperty(
								HHSConstants.HHS_SERVICE_PROPERTIES_PATH, "PROP_CITY_URL"));
						lsBfCityAgencyApplicationUrl.append(ApplicationConstants.AGENCY_TASK_INBOX_URL);
						insertNotificationDetails(lsProcurementId, lsProcurementTitle, HHSConstants.NT204,
								loExtIntEvaluatorEmailIDList, lsBfCityAgencyApplicationUrl.toString());
					}
					// insert details for alert
					loNeedToProcessNotification = filterNotificationsAlreadySent(HHSConstants.AL204, lsProcurementId);
					if (loNeedToProcessNotification)
					{
						lsBfCityAgencyApplicationUrl = new StringBuffer(PropertyLoader.getProperty(
								HHSConstants.HHS_SERVICE_PROPERTIES_PATH, "PROP_CITY_URL"));
						lsBfCityAgencyApplicationUrl.append(ApplicationConstants.EVALUATION_STATUS_AGENCY_LINK);
						lsBfCityAgencyApplicationUrl.append(lsProcurementId);
						lsBfCityAgencyApplicationUrl.append(HHSConstants.EVAL_GROUP_PARAMETER);
						lsBfCityAgencyApplicationUrl.append(lsEvaluationGroupId);
						lsBfCityAgencyApplicationUrl.append(HHSConstants.EVAL_POOL_MAPPING_PARAMETER);
						lsBfCityAgencyApplicationUrl.append(lsEvaluationPoolMappingId);
						lsBfCityAgencyApplicationUrl.append(HHSConstants.COMP_POOL_PARAMETER);
						lsBfCityAgencyApplicationUrl.append(lsCompetitionPoolId);
						insertNotificationDetails(lsProcurementId, lsProcurementTitle, HHSConstants.AL204,
								loAgencyList, lsBfCityAgencyApplicationUrl.toString());
					}
					// insert details for alert
					loNeedToProcessNotification = filterNotificationsAlreadySent(HHSConstants.NT218, lsProcurementId);
					if (loNeedToProcessNotification)
					{
						lsBfCityAgencyApplicationUrl = new StringBuffer(PropertyLoader.getProperty(
								HHSConstants.HHS_SERVICE_PROPERTIES_PATH, "PROP_CITY_URL"));
						lsBfCityAgencyApplicationUrl.append(ApplicationConstants.EVALUATION_RESULTS_AGENCY_LINK);
						lsBfCityAgencyApplicationUrl.append(lsProcurementId);
						insertNotificationDetails(lsProcurementId, lsProcurementTitle, HHSConstants.NT218,
								loAgencyList, lsBfCityAgencyApplicationUrl.toString());
					}
					// insert details for alert
					loNeedToProcessNotification = filterNotificationsAlreadySent(HHSConstants.AL205, lsProcurementId);
					if (loNeedToProcessNotification)
					{
						lsBfCityAgencyApplicationUrl = new StringBuffer(PropertyLoader.getProperty(
								HHSConstants.HHS_SERVICE_PROPERTIES_PATH, "PROP_CITY_URL"));
						lsBfCityAgencyApplicationUrl.append(ApplicationConstants.AGENCY_TASK_INBOX_URL);
						insertNotificationDetails(lsProcurementId, lsProcurementTitle, HHSConstants.AL205,
								loExtIntEvaluatorUserIDList, lsBfCityAgencyApplicationUrl.toString());
					}
					loAgencyList.clear();
				}
			}
		}
		/**
		 * catch the application exception thrown from the service layer log it
		 * into the console
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while sending FirstRoundEvaluationDateApproachAlert notification", aoAppEx);
			throw aoAppEx;
		}
		/**
		 * catch any exception thrown from the code and wrap it into application
		 * exception and propagate forward
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while sending FirstRoundEvaluationDateApproachAlert notification", aoExp);
			throw new ApplicationException("Error while sending FirstRoundEvaluationDateApproachAlert notification",
					aoExp);
		}
	}

	/**
	 * This method is used to send the Final Round due date reminder to the
	 * desired recipients
	 * <ul>
	 * <li>Get the list of procurements which rfp release due date is either 20
	 * days or 3 days by executing
	 * <code>fetchFinalEvaluationDueDateAlertDetails</code> transaction</li>
	 * <li>if the days difference is 3 days then send AL205 to all evaluators</li>
	 * <li>If the days difference is equal to 3 then send AL206 to agency acco
	 * staff and manager</li>
	 * </ul>
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void sendFinalEvaluationDateApproachAlert() throws ApplicationException
	{
		HashMap loHMap = new HashMap();
		Channel loChannelObj = new Channel();
		List<SMAlertNotificationBean> loSMAlertNotificationBeansList = null;
		List<String> loAgencyList = new ArrayList<String>();
		SMAlertNotificationBean loSMAlertNotificationBean = null;
		String lsProcurementTitle = null;
		String lsProcurementAgencyId = null;
		String lsProcurementId = null;
		String lsEvaluationGroupId = null;
		String lsEvaluationPoolMappingId = null;	
		Boolean loNeedToProcessNotification = Boolean.FALSE;
		StringBuffer lsBfCityAgencyApplicationUrl = null;
		try
		{
			LOG_OBJECT.Debug("Entered sendReminders()");
			loHMap.put(HHSConstants.NO_OF_DAYS, miFirstRoundEvaluationDateNotification);
			loChannelObj.setData(HHSConstants.LOCAL_HASH_MAP, loHMap);
			HHSTransactionManager.executeTransaction(loChannelObj,
					HHSConstants.FETCH_FINAL_RND_EVALDATE_DUE_DATE_ALERT_DETAILS_TRANS_NAME);
			loSMAlertNotificationBeansList = (List<SMAlertNotificationBean>) loChannelObj
					.getData(HHSConstants.DUE_DATE_ALERT_DETAILS_TRANS_OUTPUT);
			Iterator<SMAlertNotificationBean> loRfpReleaseDueDateItr = loSMAlertNotificationBeansList.iterator();
			while (loRfpReleaseDueDateItr.hasNext())
			{
				loSMAlertNotificationBean = (SMAlertNotificationBean) loRfpReleaseDueDateItr.next();
				lsProcurementTitle = loSMAlertNotificationBean.getProcurementTitle();
				lsProcurementId = loSMAlertNotificationBean.getProcurementId();
				lsEvaluationGroupId = loSMAlertNotificationBean.getEvaluationGroupId();
				lsEvaluationPoolMappingId = loSMAlertNotificationBean.getEvaluationPoolMappingId();
				lsProcurementAgencyId = loSMAlertNotificationBean.getProcurementAgencyId();
				loAgencyList.add(lsProcurementAgencyId);
				if (null != lsProcurementId && !lsProcurementId.isEmpty())
				{

					// insert details for alert
					loNeedToProcessNotification = filterNotificationsAlreadySent(HHSConstants.AL206, lsProcurementId);
					if (loNeedToProcessNotification)
					{
						lsBfCityAgencyApplicationUrl = new StringBuffer(PropertyLoader.getProperty(
								HHSConstants.HHS_SERVICE_PROPERTIES_PATH, "PROP_CITY_URL"));
						lsBfCityAgencyApplicationUrl.append(ApplicationConstants.EVALUATION_RESULTS_AGENCY_LINK);
						lsBfCityAgencyApplicationUrl.append(lsProcurementId);
						lsBfCityAgencyApplicationUrl.append(HHSConstants.EVAL_GROUP_PARAMETER);
						lsBfCityAgencyApplicationUrl.append(lsEvaluationGroupId);
						lsBfCityAgencyApplicationUrl.append(HHSConstants.EVAL_POOL_MAPPING_PARAMETER);
						lsBfCityAgencyApplicationUrl.append(lsEvaluationPoolMappingId);
						insertNotificationDetails(lsProcurementId, lsProcurementTitle, HHSConstants.AL206,
								loAgencyList, lsBfCityAgencyApplicationUrl.toString());
					}
					// insert details for alert
					loNeedToProcessNotification = filterNotificationsAlreadySent(HHSConstants.NT215, lsProcurementId);
					if (loNeedToProcessNotification)
					{
						lsBfCityAgencyApplicationUrl = new StringBuffer(PropertyLoader.getProperty(
								HHSConstants.HHS_SERVICE_PROPERTIES_PATH, "PROP_CITY_URL"));
						lsBfCityAgencyApplicationUrl.append(ApplicationConstants.EVALUATION_RESULTS_AGENCY_LINK);
						lsBfCityAgencyApplicationUrl.append(lsProcurementId);
						insertNotificationDetails(lsProcurementId, lsProcurementTitle, HHSConstants.NT215,
								loAgencyList, lsBfCityAgencyApplicationUrl.toString());
					}

					loAgencyList.clear();
				}
			}
		}
		/**
		 * catch the application exception thrown from the service layer log it
		 * into the console
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while sending Final Evaluation duedate notification", aoAppEx);
			throw aoAppEx;
		}
		/**
		 * catch any exception thrown from the code and wrap it into application
		 * exception and propagate forward
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while sending Final Evaluation duedate notification", aoExp);
			throw new ApplicationException("Error while sending Final Evaluation duedate notification", aoExp);
		}
	}

	/**
	 * This method is used to fetch the approved providers for the procurement
	 * <ul>
	 * <li>Execute transaction <code>fetchApprovedProvidersList</code> to fetch
	 * the provider list</li>
	 * </ul>
	 * @param asProcurementId procurement id
	 * @return List
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked" })
	private static List<String> getApprovedProviderNames(String asProcurementId) throws ApplicationException
	{
		List<String> loApprovedProviderList = null;
		Channel loChannelObj = new Channel();
		Map<String, String> loParamMap = new HashMap<String, String>();
		try
		{
			loParamMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
			loChannelObj.setData(HHSConstants.LOCAL_HASH_MAP, loParamMap);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_APP_PROVIDERS_LIST);
			loApprovedProviderList = (List<String>) loChannelObj
					.getData(HHSConstants.FETCH_APPROVED_PROVIDER_LIST_TRANS_OUTPUT);
		}
		/**
		 * catch the application exception thrown from the service layer log it
		 * into the console
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in sendReminders()", aoAppEx);
			throw aoAppEx;
		}
		/**
		 * catch any exception thrown from the code and wrap it into application
		 * exception and propagate forward
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception getting approved provider", aoExp);
			throw new ApplicationException("Exception getting approved provider", aoExp);
		}
		return loApprovedProviderList;
	}

	/**
	 * This method is used to insert the notification and alert details into the
	 * notification table
	 * <ul>
	 * <li>Prepare the link string to display in alert and notification</li>
	 * <li>According to the notification name set agency name and provider list
	 * into the parameter map</li>
	 * </ul>
	 * @param asProcurementId procurement id
	 * @param asProcurementName procurement title
	 * @param asNotificationName notification name
	 * @param aoApprovedProviderList user list
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void insertNotificationDetails(String asProcurementId, String asProcurementName, String asNotificationName,
			List<String> aoApprovedProviderList, String asLink) throws ApplicationException
	{
		ArrayList loAlertsList = new ArrayList();
		HashMap loRequestMap = new HashMap();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add(asNotificationName);
		NotificationDataBean loNotificationDataBean = new NotificationDataBean();
		HashMap<String, String> loLinkMap = new HashMap<String, String>();
		loLinkMap.put(HHSConstants.LINK, asLink);
		loNotificationDataBean.setAgencyLinkMap(loLinkMap);
		loNotificationDataBean.setLinkMap(loLinkMap);
		try
		{
			Channel loChannelObj;
			loRequestMap.put(HHSConstants.PROC_TITLE, asProcurementName);
			loAlertsList.add(asNotificationName);
			HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
			loNotificationMap.put(TransactionConstants.EVENT_ID_PARAMETER_NAME, loAlertsList);
			if (asNotificationName.equalsIgnoreCase(HHSConstants.NT205)
					|| asNotificationName.equalsIgnoreCase(HHSConstants.AL210)
					|| asNotificationName.equalsIgnoreCase(HHSConstants.AL209))
			{
				loNotificationDataBean.setProviderList(aoApprovedProviderList);
			}
			// Start of changes done for defect id : 6385
			// Added checks for NT204 and AL205
			if (asNotificationName.equalsIgnoreCase(HHSConstants.AL204)
					|| asNotificationName.equalsIgnoreCase(HHSConstants.AL208)
					|| asNotificationName.equalsIgnoreCase(HHSConstants.AL207)
					|| asNotificationName.equalsIgnoreCase(HHSConstants.NT218)
					|| asNotificationName.equalsIgnoreCase(HHSConstants.AL206)
					|| asNotificationName.equalsIgnoreCase(HHSConstants.NT215)
					|| asNotificationName.equalsIgnoreCase(HHSConstants.NT204)
					|| asNotificationName.equalsIgnoreCase(HHSConstants.AL205))
			{
				loNotificationDataBean.setAgencyList(aoApprovedProviderList);
			}
			// End of changes done for defect id : 6385
			// Set Parameter for Release 2 notification
			loNotificationMap.put(HHSConstants.ENTITY_TYPE, HHSConstants.PROCUREMENT);
			loNotificationMap.put(HHSConstants.ENTITY_ID, asProcurementId);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
			loNotificationMap.put(asNotificationName, loNotificationDataBean);
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
	 * This method is used to get the the external and internal evaluators for
	 * the procurement
	 * <ul>
	 * <li>Execute transaction <code>fetchExtAndIntEvaluatorBatch</code></li>
	 * <li>Get the list of email ids for the evaluators from the channel object</li>
	 * </ul>
	 * @param asProcurementId procurement id
	 * @return List of email ids
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked" })
	private static HashMap<String, List<String>> getExtIntEvaluatorList(String asProcurementId)
			throws ApplicationException
	{
		HashMap<String, List<String>> loExternalInternalEvaluatorMap = null;
		Channel loChannelObj = new Channel();
		HashMap<String, String> loParamMap = new HashMap<String, String>();
		try
		{
			loParamMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
			loChannelObj.setData(HHSConstants.LOCAL_HASH_MAP, loParamMap);
			HHSTransactionManager.executeTransaction(loChannelObj,
					HHSConstants.FETCH_EXTERNAL_INTERNAL_EVALUATOR_TRANS_NAME);
			loExternalInternalEvaluatorMap = (HashMap<String, List<String>>) loChannelObj
					.getData(HHSConstants.FETCH_EXTERNAL_INTERNAL_EVALUATOR_TRANS_OUTPUT);
		}
		/**
		 * catch the application exception thrown from the service layer log it
		 * into the console
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in sendReminders()", aoAppEx);
			throw aoAppEx;
		}
		/**
		 * catch any exception thrown from the code and wrap it into application
		 * exception and propagate forward
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception getting approved provider", aoExp);
			throw new ApplicationException("Exception getting approved provider", aoExp);
		}
		return loExternalInternalEvaluatorMap;
	}

	/**
	 * This method is used to filter out the notifications which are already
	 * been sent to user
	 * <ul>
	 * <li>Execute the transaction with id <code>filterNotificationList</code></li>
	 * <li>Get the event id if the event id is equal to the event id selected
	 * then don't do anything</li>
	 * <li>Else insert the selected event details</li>
	 * </ul>
	 * @param aoNotificationList selected entities
	 * @param asEntityId procurement id
	 * @return Boolean whether to insert details or not
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private Boolean filterNotificationsAlreadySent(Object aoNotificationList, String asEntityId)
			throws ApplicationException
	{
		List<HashMap<String, String>> loFinalNotificationList = null;
		Channel loChannelObj = new Channel();
		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		Boolean loNeedToInsertNotification = Boolean.TRUE;
		String lsEventId = null;
		try
		{
			loParamMap.put(HHSConstants.NOTIFICATION_LIST_KEY, aoNotificationList);
			loParamMap.put(HHSConstants.ENTITY_TYPE, HHSConstants.PROCUREMENT);
			loParamMap.put(HHSConstants.ENTITY_ID, asEntityId);
			loChannelObj.setData(HHSConstants.LOCAL_HASH_MAP, loParamMap);
			HHSTransactionManager.executeTransaction(loChannelObj, HHSConstants.FILTER_NOTIFICATION_LIST_TRANS_NAME);
			loFinalNotificationList = (List<HashMap<String, String>>) loChannelObj
					.getData(HHSConstants.FILTER_NOTIFICATION_LIST_TRANS_OUTPUT);
			if (null != loFinalNotificationList && !loFinalNotificationList.isEmpty())
			{
				if (aoNotificationList instanceof List)
				{
					for (Iterator<HashMap<String, String>> loRetrievedNTItr = loFinalNotificationList.iterator(); loRetrievedNTItr
							.hasNext();)
					{
						lsEventId = ((HashMap<String, String>) loRetrievedNTItr.next()).get("NOTIFICATION_ALERT_ID");
						if (((List) aoNotificationList).contains(lsEventId))
						{
							((List) aoNotificationList).remove(lsEventId);
						}
					}
					if (((List) aoNotificationList).isEmpty())
					{
						loNeedToInsertNotification = Boolean.FALSE;
					}
				}
				else if (aoNotificationList instanceof String)
				{
					for (Iterator<HashMap<String, String>> loRetrievedNTItr = loFinalNotificationList.iterator(); loRetrievedNTItr
							.hasNext();)
					{
						lsEventId = ((HashMap<String, String>) loRetrievedNTItr.next()).get("NOTIFICATION_ALERT_ID");
						if (((String) aoNotificationList).equalsIgnoreCase(lsEventId))
						{
							loNeedToInsertNotification = Boolean.FALSE;
						}
					}

				}
			}
		}
		/**
		 * catch the application exception thrown from the service layer log it
		 * into the console
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured while filtering notification list", aoAppEx);
			throw aoAppEx;
		}
		/**
		 * catch any exception thrown from the code and wrap it into application
		 * exception and propagate forward
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occured while filtering notification list", aoExp);
			throw new ApplicationException("Error occured while filtering notification list", aoExp);
		}
		return loNeedToInsertNotification;
	}
}