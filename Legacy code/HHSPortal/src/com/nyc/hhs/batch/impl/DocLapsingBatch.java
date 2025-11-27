package com.nyc.hhs.batch.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.nyc.hhs.batch.IBatchQueue;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.daomanager.service.SectionService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ApplicationExpiryRuleBean;
import com.nyc.hhs.model.ApplicationIdBRStatusBean;
import com.nyc.hhs.model.ApplicationIdStatusBean;
import com.nyc.hhs.model.DocLapsingRuleBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.NotificationSettingsBean;
import com.nyc.hhs.model.ProviderStatusBean;
import com.nyc.hhs.model.SupersedingStatusBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.ProviderStatusBusinessRules;

/**
 * This class is being used for running batches for Document Lapsing ,
 * Application Expire , Provider Account Expire and Conditionally Approved
 * Status expire It consists of methods that will be invoked from components
 * steps in work flows 1. executeQueue - This method is the parent method used
 * inherited form IBatchQueue class. This is used for fetching the values for
 * various notification settings from database and calling all other functions
 * 2. executeDocLapsingNotificationBatch - Send notification for documents
 * getting lapsed and lapsing 3. updateProviderStatus - This method is used for
 * updating the status of a provider 4. executeProviderAccountExpiryStatusBatch
 * - This method is used for updating the status of providers to 'Expired' whose
 * expire date has lapsed 5. executeConditionallyApprovedStatusBatch - For BR
 * applications whose status is Draft and TIMESTAMP in Superseding_status table
 * < 90 days and super-seeding status is 'conditionally approved' make provider
 * and application status to 'Expired'
 * 
 */

public class DocLapsingBatch implements IBatchQueue
{

	private static final LogInfo LOG_OBJECT = new LogInfo(DocLapsingBatch.class);

	// define integer values for doc lapsing settings
	int miNT022 = 0; // NT022 - 60 days
	int miNT023 = 0; // NT023 - 30 days
	int miNT024 = 0; // NT024 - 10 days

	// define integer values for provider expiring settings
	int miNT026 = 0; // NT026 - 90 days
	int miNT027 = 0; // NT027 - 60 days
	int miNT028 = 0; // NT028 - 30 days
	int miNT029 = 0; // NT029 - 10 days

	int miBR001 = 0; // BR001 - 3 years for provider expiry
	int miNT025 = 0; // NT025 - 180 days(6 months)

	String msProviderExpiryStatus = "";
	String msFilingSuspendedStatus = "";
	String msSuperSeededStatus = "";

	/**
	 * Blank implementation of getQueue method of interface
	 * 
	 * @param aoMParameters HashMap of Parameter
	 * @return null
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public List<DocLapsingRuleBean> getQueue(Map aoMParameters)
	{
		return null;
	}

	/**
	 * Implementation of the executeQueue method . This method will call all the
	 * other methods for executing the batch operations
	 * 
	 * @param aoLQueue List of Queue
	 * @throws ApplicationException
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public void executeQueue(List aoLQueue) throws ApplicationException
	{
		try
		{

			// call the function fetchNotificationSettings to initialize values
			fetchNotificationSettings();

			// Doc lapsing notifications :- send notifications for documents
			// expiring as per rule ie. NT022, NT023 , NT024
			LOG_OBJECT.Debug("Starting transaction for doc lapsing notifications");

			executeDocLapsingNotificationBatch();

			LOG_OBJECT.Debug("Finished transaction for doc lapsing notifications");

			LOG_OBJECT.Debug("Starting transaction for conditional approval expiry");

			executeConditionallyApprovedStatusBatch();

			LOG_OBJECT.Debug("Finished transaction for conditional approval expiry");

			LOG_OBJECT.Debug("Starting transaction for application expiry notifications");

			executeApplicationExpiryNotificationBatch();

			LOG_OBJECT.Debug("Finished transaction for application expiry notifications");

			// Start: R6.1.0
			reconcileProviderStatusBatch();

			LOG_OBJECT.Debug("Finished transaction for reconcileProviderStatusBatch");

			deleteErroneousFromSuperseding();
			LOG_OBJECT.Debug("Finished transaction for deleteErroneousFromSuperseding");
			// End: R6.1.0
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in DocLapsingBatch.executeQueue()", aoAppEx);
			throw aoAppEx;
		}
	} // end function executeQueue

	/**
	 * This method is used for fetching the values for various notification
	 * settings from database
	 * <ul>
	 * <li>Execute Transaction id <b> fetchNotificationSettings </b></li>
	 * <li>Iterate NotificationSettingsBean to fetch Setting Name</li>
	 * </ul>
	 * @throws ApplicationException
	 */

	@SuppressWarnings("unchecked")
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

			TransactionManager.executeTransaction(loChannelObj, "fetchNotificationSettings");
			loNotificationSettingsBeanList = (List<NotificationSettingsBean>) loChannelObj
					.getData("notificationSettingDetails");

			Iterator<NotificationSettingsBean> loIteratorSett = loNotificationSettingsBeanList.iterator();

			// Iterate through the NotificationSettings bean list obtained and
			// fetch the values for
			// all constants obtained from DB table APPLICATION_SETTINGS

			while (loIteratorSett.hasNext())
			{

				loNotificationSettingsBean = loIteratorSett.next();

				if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(ApplicationConstants.NT024))
				{
					miNT024 = Integer.parseInt(loNotificationSettingsBean.getSettingValue());
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(ApplicationConstants.NT023))
				{
					miNT023 = Integer.parseInt(loNotificationSettingsBean.getSettingValue());
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(ApplicationConstants.NT022))
				{
					miNT022 = Integer.parseInt(loNotificationSettingsBean.getSettingValue());
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(ApplicationConstants.NT026))
				{
					miNT026 = Integer.parseInt(loNotificationSettingsBean.getSettingValue());
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(ApplicationConstants.NT027))
				{
					miNT027 = Integer.parseInt(loNotificationSettingsBean.getSettingValue());
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(ApplicationConstants.NT028))
				{
					miNT028 = Integer.parseInt(loNotificationSettingsBean.getSettingValue());
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(ApplicationConstants.NT029))
				{
					miNT029 = Integer.parseInt(loNotificationSettingsBean.getSettingValue());
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(
						ApplicationConstants.PROVIDER_EXPIRY_STATUS))
				{
					msProviderExpiryStatus = loNotificationSettingsBean.getSettingValue();
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(
						ApplicationConstants.FILING_SUSPEND_STATUS))
				{
					msFilingSuspendedStatus = loNotificationSettingsBean.getSettingValue();
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(
						ApplicationConstants.SUPER_SEEDED_STATUS))
				{
					msSuperSeededStatus = loNotificationSettingsBean.getSettingValue();
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(ApplicationConstants.BR001))
				{
					miBR001 = Integer.parseInt(loNotificationSettingsBean.getSettingValue());
				}
				else if (loNotificationSettingsBean.getSettingName().equalsIgnoreCase(ApplicationConstants.NT025))
				{
					miNT025 = Integer.parseInt(loNotificationSettingsBean.getSettingValue());
				}
			}

			LOG_OBJECT.Debug("Notification settings obtained");

			// end get settings

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in BatchComponent.fetchNotificationSettings()", aoAppEx);
			throw aoAppEx;
		}

		LOG_OBJECT.Debug("Exited fetchNotificationSettings()");

	} // end function fetchNotificationSettings

	/**
	 * This method is used for sending notification for documents getting lapsed
	 * and lapsing
	 * <ul>
	 * <li>Execute Transaction id <b> docRulesLapsingBatch </b></li>
	 * <li>Iterate DocLapsingRuleBean to fetch Details</li>
	 * </ul>
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unused")
	public void executeDocLapsingNotificationBatch() throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered executeDocLapsingNotificationBatch() ");
		// call the function fetchNotificationSettings to initialising values
		// fetchNotificationSettings();
		DocLapsingRuleBean loDocLapsingBean = null;
		List<DocLapsingRuleBean> loDocLapsingBeanList = null;
		String lsNotificationName = null;
		String lsAlertName = null;
		Channel loChannelObj = new Channel();
		try
		{
			executeDocLapsingDetails(lsNotificationName, lsAlertName, loChannelObj);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in BatchComponent.executeDocLapsingNotificationBatch()", aoAppEx);
			throw aoAppEx;
		}
		LOG_OBJECT.Debug("Exited executeDocLapsingNotificationBatch() ");
	}

	/**
	 * This method execute the Transaction : docRulesLapsingBatch
	 * 
	 * @param lsNotificationName
	 * @param lsAlertName
	 * @param loChannelObj
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void executeDocLapsingDetails(String lsNotificationName, String lsAlertName, Channel loChannelObj)
			throws ApplicationException
	{
		DocLapsingRuleBean loDocLapsingBean;
		List<DocLapsingRuleBean> loDocLapsingBeanList;
		HashMap loHMap = new HashMap();
		loHMap.put("NT022", miNT022);
		loChannelObj.setData("loHMap", loHMap);
		LOG_OBJECT.Debug("Executing transaction docRulesLapsingBatch");
		TransactionManager.executeTransaction(loChannelObj, "docRulesLapsingBatch");
		LOG_OBJECT.Debug("Finished transaction docRulesLapsingBatch");
		loDocLapsingBeanList = (List<DocLapsingRuleBean>) loChannelObj.getData("docLapsingDetails");
		Iterator<DocLapsingRuleBean> loIterator = loDocLapsingBeanList.iterator();
		// check for NT022 - 60 , NT023 - 30 , NT024 - 10 days
		if (!loIterator.hasNext())
		{
			LOG_OBJECT.Debug("executeDocLapsingNotificationBatch :: No records found");
		}
		while (loIterator.hasNext())
		{
			loDocLapsingBean = loIterator.next();
			String lsProviderId = loDocLapsingBean.getProviderId();
			Date ldDueDate = loDocLapsingBean.getDueDate();
			String lsDocType = loDocLapsingBean.getDocType();
			SimpleDateFormat loDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			String lsDueDate = loDateFormat.format(ldDueDate);
			java.sql.Date loSqlDueDate = DateUtil.getSqlDate(lsDueDate);
			int liNumDays = loDocLapsingBean.getNumDays();
			int liNotificationDays = 0;
			ArrayList<String> idStatusList = null;
			boolean conditionallyApproved = false;
			String lsLatestEntityId = "";
			// BEGIN QC 8694 R 6.2
			// Exclude Rejected Providers
			String orgStatus = getCurrentProviderStatus(lsProviderId);
			if (ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(orgStatus))
			{
				continue;
			}
			// END QC 8694 R 6.2

			// new transaction added to find latest business application id
			loChannelObj = new Channel();
			loHMap = new HashMap();
			loHMap.put("asProviderId", lsProviderId);
			loChannelObj.setData("loHMap", loHMap);
			// Fetch the latest BR application id from BR Application table for
			// the provider
			LOG_OBJECT.Debug("Executing transaction fetchLatestBRAppIdforProvider");
			// BEGIN QC 8667 Release 6.1.0
			// lsLatestEntityId = calculateEntityId(lsProviderId);
			idStatusList = calculateEntityId(lsProviderId);
			int len = idStatusList.size();
			if (idStatusList != null & len > 0)
			{
				lsLatestEntityId = idStatusList.get(0);
				if (len > 1 && ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(idStatusList.get(1)))
				{
					conditionallyApproved = true;
				}
			}
			// END QC 8667 Release 6.1.0
			if (liNumDays <= miNT022 && liNumDays > miNT023)
			{
				lsNotificationName = ApplicationConstants.NT022;
				lsAlertName = ApplicationConstants.AL020;
				liNotificationDays = miNT022;
			}
			else if (liNumDays <= miNT023 && liNumDays > miNT024)
			{
				lsNotificationName = ApplicationConstants.NT023;
				lsAlertName = ApplicationConstants.AL021;
				liNotificationDays = miNT023;
			}
			else if (liNumDays <= miNT024 && liNumDays >= 1)
			{
				lsNotificationName = ApplicationConstants.NT024;
				lsAlertName = ApplicationConstants.AL022;
				liNotificationDays = miNT024;
			}
			else if (liNumDays <= 0)
			{
				lsNotificationName = ApplicationConstants.NT035;
				lsAlertName = ApplicationConstants.AL029;
			}
			LOG_OBJECT.Debug("Obtained lsNotificationName = " + lsNotificationName);
			LOG_OBJECT.Debug("Obtained lsAlertName = " + lsAlertName);
			lsDocType = getDocType(lsDocType);
			int liCount = fetchDueDateReminderCount(lsNotificationName, lsAlertName, lsProviderId, loSqlDueDate,
					lsLatestEntityId);
			LOG_OBJECT.Debug("Obtained liCount = " + liCount);
			if (liCount == 0)
			{
				// notification doesn't exist Call Notification service with
				// values
				callNotificationServiceDocLapsing(lsDocType, lsNotificationName, lsAlertName, lsProviderId,
						loSqlDueDate, liNotificationDays, conditionallyApproved, lsLatestEntityId);
			}
			else
			{
				// notification already sent for that type do nothing
				LOG_OBJECT.Debug("Notification already sent to provider");
			}
		} // end
	}

	/**
	 * Modified 3.1.0 . Added check for Enhancement #6021: char 500 extension
	 * This method return document type
	 * 
	 * @param lsDocType
	 * @return
	 */
	private String getDocType(String lsDocType)
	{
		if ((lsDocType.equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE + "|"
				+ P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE))
				|| (lsDocType.equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_EXTENSION + "|"
						+ lsDocType.equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE)))
				|| (lsDocType.equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE)))
		{
			return P8Constants.PROPERTY_CE_CHAR500_EXTENSION;
		}
		else
		{
			return P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE;
		}

	}

	/**
	 * This function is used for calling the notification service for doc
	 * lapsing
	 * 
	 * <ul>
	 * <li>Execute transaction id<b> insertDueDateForEfillingBatch</b></li>
	 * <li>Execute transaction id <b> suspendEfillingBatch</b></li>
	 * <li>Set the server name</li>
	 * </ul>
	 * @param asDocType String denoting the Document type
	 * @param asNotificationName String denoting the notification name
	 * @param asAlertName String denoting the alert name
	 * @param asProviderId String denoting the Provider Id
	 * @param aoNotificationDays Notification Days
	 * @param aoSqlDate Sql Date
	 * @throws ApplicationException
	 * 
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void callNotificationServiceDocLapsing(String asDocType, String asNotificationName, String asAlertName,
			String asProviderId, Date aoSqlDate, int aoNotificationDays, boolean conditionallyApproved,
			String lsEntityId) throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		HashMap loRequestMap = new HashMap();
		try
		{
			LOG_OBJECT.Debug("asDocType" + asDocType + "   asNotificationName " + asNotificationName + " asAlertName "
					+ asAlertName + " asProviderId " + asProviderId);
			StringBuffer lsBfApplicationUrl = new StringBuffer();
			
			String lsServerName = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					"SERVER_NAME_FOR_PROVIDER_BATCH");
			String lsServerPort = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					"SERVER_PORT_FOR_PROVIDER_BATCH");
			String lsContextPath = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					"CONTEXT_PATH_FOR_PROVIDER_BATCH");
			String lsAppProtocol = PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
					"SERVER_PROTOCOL_FOR_PROVIDER_BATCH");
			if (asNotificationName.equalsIgnoreCase(ApplicationConstants.NT022)
					|| asNotificationName.equalsIgnoreCase(ApplicationConstants.NT023)
					|| asNotificationName.equalsIgnoreCase(ApplicationConstants.NT024)
					|| asAlertName.equalsIgnoreCase(ApplicationConstants.AL020)
					|| asAlertName.equalsIgnoreCase(ApplicationConstants.AL021)
					|| asAlertName.equalsIgnoreCase(ApplicationConstants.AL022)
					|| asAlertName.equalsIgnoreCase(ApplicationConstants.AL029)
					//*** Start R 9.1 QC 9611 - Update in-system notifications and alerts for CHAR 500 ***
					|| asNotificationName.equalsIgnoreCase(ApplicationConstants.NT035)
					//*** End R 9.1 QC 9611 - Update in-system notifications and alerts for CHAR 500 ***
					)
			{
				//*** Start R 9.1 QC 9611 - Update in-system notifications and alerts for CHAR 500 ***
				/*
				lsBfApplicationUrl.append(lsAppProtocol);
				lsBfApplicationUrl.append("://");
				lsBfApplicationUrl.append(lsServerName);
				lsBfApplicationUrl.append(":");
				lsBfApplicationUrl.append(lsServerPort);
				lsBfApplicationUrl.append("/");
				lsBfApplicationUrl.append(lsContextPath);
				lsBfApplicationUrl.append(ApplicationConstants.SHARED_DOCUMENT_ALERT_NOTIFICATION_LINK);
				*/
				
				lsBfApplicationUrl.append(ApplicationConstants.PASSPORT_SHARED_DOCUMENT_ALERT_NOTIFICATION_LINK);
				//*** End R 9.1 QC 9611 - Update in-system notifications and alerts for CHAR 500 ***
			}
			else
			{
				lsBfApplicationUrl.append(lsAppProtocol);
				lsBfApplicationUrl.append("://");
				lsBfApplicationUrl.append(lsServerName);
				lsBfApplicationUrl.append(":");
				lsBfApplicationUrl.append(lsServerPort);
				lsBfApplicationUrl.append("/");
				lsBfApplicationUrl.append(lsContextPath);
				lsBfApplicationUrl.append(ApplicationConstants.TASK_ALERT_NOTIFICATION_LINK);
			}
			loRequestMap.put("DOCUMENT TYPE", asDocType);
			loRequestMap.put("LEFTDAYS", String.valueOf(aoNotificationDays));
			ArrayList loAlertsList = new ArrayList();
			loAlertsList.add(asNotificationName);
			loAlertsList.add(asAlertName);
			ArrayList loProviderList = new ArrayList();
			loProviderList.add(asProviderId);
			HashMap loNotificationMap = new HashMap();

			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			HashMap<String, String> loLinkMap = new HashMap<String, String>();
			loLinkMap.put(HHSConstants.LINK, lsBfApplicationUrl.toString());
			loNotificationDataBean.setAgencyLinkMap(loLinkMap);
			loNotificationDataBean.setLinkMap(loLinkMap);

			loNotificationDataBean.setProviderList(loProviderList);

			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loAlertsList);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, HHSConstants.SYSTEM_USER);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, HHSConstants.SYSTEM_USER);
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);

			// QC 8667 Release 6.1.0
			// lsEntityId already passed as parameter, no need to fire
			// transaction again
			// String lsEntityId = calculateEntityId(asProviderId);

			loNotificationMap.put(TransactionConstants.PROVIDER_ID, loProviderList);
			loNotificationMap.put("NotificationName", asNotificationName);
			loNotificationMap.put("DueDate", aoSqlDate);
			loNotificationMap.put("ProviderId", asProviderId);
			loNotificationMap.put("asProviderId", asProviderId);
			loNotificationMap.put("asProviderStatus", msFilingSuspendedStatus);
			loNotificationMap.put("lsEntityId", lsEntityId);
			loNotificationMap.put("lsEntityType", ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION);
			loNotificationMap.put(HHSConstants.ENTITY_ID, lsEntityId);
			loNotificationMap.put(HHSConstants.ENTITY_TYPE, ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION);
			loNotificationMap.put(asNotificationName, loNotificationDataBean);
			loNotificationMap.put(asAlertName, loNotificationDataBean);
			loNotificationMap.put("alertName", asAlertName);
			loChannelObj.setData("loHmNotifyParam", loNotificationMap);
			TransactionManager.executeTransaction(loChannelObj, "insertDueDateForEfillingBatch");

			if (asAlertName.equalsIgnoreCase(ApplicationConstants.AL029))
			{
				if (!conditionallyApproved)
				{
					TransactionManager.executeTransaction(loChannelObj, "suspendEfillingBatch");
					// Document has expired.
					// 1. Update the status in Superseeding_Status to
					// 'Suspended (Filings Expired)' for the provider
					// 2. Update the status of the provider in Organization
					// table to 'Suspended (Filings Expired)'
					// Update provider status
					LOG_OBJECT.Debug("Document has expired. Calling updateProviderStatus()");

					// The below code is used for calculating which BR-id is to
					// be
					// send as Entity_Id in the super-seeding table
					// This is required in case there are multiple applications
					// for
					// the same provider
					// updateProviderStatus(loChannelObj, asProviderId,
					// msFilingSuspendedStatus, lsEntityId);
					// Update superseeding status in Superseeding_Status table
					// to
					// 'Suspended (Filings Expired)'
				}
				else
				{ // BEGIN QC 8667 Release 6.1.0
					/*
					 * for Conditionally Approved 1. Update Provider with status
					 * to 'Approved' 2. Remove Filing Expire record from
					 * Superseding table for provider if such was found
					 */
					// get Provider Status

					String orgStatus = getCurrentProviderStatus(asProviderId);
					if (!ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(orgStatus))
					{
						updateProviderStatusInOrgTable(asProviderId, ApplicationConstants.STATUS_APPROVED);
					}
					// check if Suspended File Expire status was previously
					// recorded to SupersedingStatus table for Provider
					int counter = fetchSupersedingStatusCount(asProviderId,
							ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED);
					if (counter > 0)
					{
						deleteEventFromSupersedingStatusForProvider(asProviderId,
								ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED);
					}
				}
				// END QC 8667 Release 6.1.0
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in BatchComponent.executeDocLapsingNotificationBatch()", aoAppEx);
			throw aoAppEx;
		}

	}

	/**
	 * This method is used for executing the batch for application expiry
	 * notification
	 * <ul>
	 * <li>Execute Transaction id <b>ApplicationExpiryNotificationBatch</b></li>
	 * </ul>
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public void executeApplicationExpiryNotificationBatch() throws ApplicationException
	{

		LOG_OBJECT.Debug("Entered executeApplicationExpiryNotificationBatch() ");

		// call the function fetchNotificationSettings for initializing values
		// fetchNotificationSettings();

		ApplicationExpiryRuleBean loAplicationExpiryBean = null;
		List<ApplicationExpiryRuleBean> loAplicationExpiryBeanList = null;
		String lsNotificationName = null;
		String lsAlertName = null;

		Channel loChannelObj = new Channel();

		ArrayList loArrFetchedProviders = new ArrayList();

		try
		{

			HashMap loHMap = new HashMap();
			loHMap.put("NT025", miNT025);
			loHMap.put("lsProviderExpiryStatus", msProviderExpiryStatus);
			loHMap.put("BR001", miBR001 * 12); // convert no. of years obtained
												// from liBR001 to months
			loHMap.put("STATUS_APPROVED", ApplicationConstants.STATUS_APPROVED);

			loChannelObj.setData("loHMap", loHMap);
			LOG_OBJECT.Debug("Executing transaction ApplicationExpiryNotificationBatch");
			TransactionManager.executeTransaction(loChannelObj, "ApplicationExpiryNotificationBatch");
			loAplicationExpiryBeanList = (List<ApplicationExpiryRuleBean>) loChannelObj
					.getData("applicationExpiryDetails");
			LOG_OBJECT.Debug("Executed transaction ApplicationExpiryNotificationBatch");
			Iterator<ApplicationExpiryRuleBean> loIterator = loAplicationExpiryBeanList.iterator();

			if (!loIterator.hasNext())
			{
				LOG_OBJECT.Debug("executeApplicationExpiryNotificationBatch :: No records found");
			}

			// check for NT025 - 180 , NT026 - 90, NT027 - 60 , NT028 - 30 ,
			// NT029 - 10 days
			String svProviderId = "";
			List<ApplicationIdBRStatusBean> baList = new ArrayList<ApplicationIdBRStatusBean>();
			while (loIterator.hasNext())
			{
				loAplicationExpiryBean = loIterator.next();
				String lsProviderId = loAplicationExpiryBean.getProviderId();
				String lsApplicationId = loAplicationExpiryBean.getApplicationId();
				Date ldExpiryDate = loAplicationExpiryBean.getExpiryDate();
				SimpleDateFormat loDateFormat = new SimpleDateFormat("MM/dd/yyyy");
				String lsExpiryDate = loDateFormat.format(ldExpiryDate);
				java.sql.Date loSqlExpiryDate = DateUtil.getSqlDate(lsExpiryDate);
				int liNumDays = loAplicationExpiryBean.getNumDays();
				int liNotificationDays = 0;
				// an application for this provider has being processed for
				// the 1st time

				if (liNumDays <= miNT025 && liNumDays > miNT026)

				{
					lsNotificationName = ApplicationConstants.NT025;
					lsAlertName = "";
					liNotificationDays = miNT025;
				}
				else if (liNumDays <= miNT026 && liNumDays > miNT027)

				{
					lsNotificationName = ApplicationConstants.NT026;
					lsAlertName = ApplicationConstants.AL023;
					liNotificationDays = miNT026;
				}
				else if (liNumDays <= miNT027 && liNumDays > miNT028)
				{
					lsNotificationName = ApplicationConstants.NT027;
					lsAlertName = ApplicationConstants.AL024;
					liNotificationDays = miNT027;
				}
				else if (liNumDays <= miNT028 && liNumDays > miNT029)
				{
					lsNotificationName = ApplicationConstants.NT028;
					lsAlertName = ApplicationConstants.AL025;
					liNotificationDays = miNT028;
				}
				else if (liNumDays <= miNT029 && liNumDays >= 1)

				{
					lsNotificationName = ApplicationConstants.NT029;
					lsAlertName = ApplicationConstants.AL026;
					liNotificationDays = miNT029;
				}
				else if (liNumDays <= 0)

				{
					lsNotificationName = ApplicationConstants.NT030;
					lsAlertName = ApplicationConstants.AL027;
				}

				LOG_OBJECT.Debug("Obtained lsNotificationName = " + lsNotificationName);
				LOG_OBJECT.Debug("Obtained lsAlertName = " + lsAlertName);

				try
				{
					int liCount = fetchDueDateReminderCount(lsNotificationName, lsAlertName, lsProviderId,
							loSqlExpiryDate, lsApplicationId);

					/*
					 * BEGIN - fix for QC 6749 Release 6.1.0 check if latest
					 * (more then 1) BR has been created and already Approved or
					 * Conditionally Approved if yes - do not send expiration
					 * note to the Provider about old Business Application
					 */
					if (liCount == 0)
					{
						String orgStatus = getCurrentProviderStatus(lsProviderId);
						int counterBA = fetchBusinessApplicationCount(lsProviderId);
						// for currently approved BR
						if (liNumDays > 0)
						{
							/* get the latest BR for Provider if more then 1 BA */
							if (counterBA > 1)
							{
								List<String> latestBRList = new ArrayList<String>();
								latestBRList = (ArrayList<String>) getLatestBADetails(lsProviderId, lsApplicationId);
								String lsLatestBRId = latestBRList.get(0);
								String lsLatestBRStatus = latestBRList.get(1);
								String tempBRdays = latestBRList.get(2);
								Integer lsLatestBRdays = null;
								if (tempBRdays != null && tempBRdays != "")
								{
									lsLatestBRdays = Integer.parseInt(tempBRdays);
								}
								// if Latest BA is not the one that is
								// processing now
								if (!lsLatestBRId.equalsIgnoreCase(lsApplicationId))
								{
									if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(lsLatestBRStatus)
											&& ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(orgStatus)
											&& lsLatestBRdays != null && lsLatestBRdays > 0)
									{
										liCount = 1;
									}

									// check if latest BA is Conditionally
									// Approved
									String lsConditionallyApprovedStatus = "";
									if ((ApplicationConstants.STATUS_DRAFT.equalsIgnoreCase(lsLatestBRStatus)
											|| ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS
													.equalsIgnoreCase(lsLatestBRStatus) || ApplicationConstants.STATUS_IN_REVIEW
												.equalsIgnoreCase(lsLatestBRStatus))
											&& lsLatestBRdays != null
											&& lsLatestBRdays > 0)
									{
										lsConditionallyApprovedStatus = verifyCondtionallyApprovedBRStatus(
												lsProviderId, lsLatestBRId);

										if (ApplicationConstants.STATUS_CONDITIONALLY_APPROVED
												.equalsIgnoreCase(lsConditionallyApprovedStatus)
												|| ApplicationConstants.STATUS_APPROVED
														.equalsIgnoreCase(lsConditionallyApprovedStatus))
										{
											liCount = 1;
										}
									}
								}
							}
						} // END for QC 6749 Release 6.1.0
						else
						/*
						 * BEGIN - fix for QC 8515 Release 6.1.0
						 * 
						 * for provider with more then 1 BR exclude expired BR
						 * from current process if: 1. any latest BR is
						 * Approved/Conditionally Approved 2. latest
						 * BR/processing BR (if only 1 exists) is
						 * Draft/Review/Return for Revision and Provider status
						 * Expired 3. latest BR/processing BR (if only 1 exists)
						 * is Withdrawn and Provider status Withdrawn if any
						 * conditions above are not met 4. exclude from
						 * processing if any latest BR is expired
						 */
						{
							if (counterBA > 1) // more then 1 BR exists
							{
								if (!lsProviderId.equalsIgnoreCase(svProviderId))
								{
									baList = fetchAllBusinessApplicationForProvider(lsProviderId);
									svProviderId = lsProviderId;
								}

								Boolean exclude = excludeBR(lsProviderId, lsApplicationId, orgStatus, liNumDays, baList);
								if (exclude)
								{
									liCount = 1;
								}
							}
						}
					}
					// END for QC 8515 Release 6.1.0

					if (liCount == 0)
					{
						// Notification doesn't exist. Call Notification service
						// with values
						callNotificationApplicationExpiry(lsApplicationId, lsNotificationName, lsAlertName,
								lsProviderId, loSqlExpiryDate, liNotificationDays);
					}
					else
					{
						// notification already sent for that type do nothing
						LOG_OBJECT.Debug("Notification already sent, do nothing");
					}

					// add this provider name to processed providers list
					loArrFetchedProviders.add(lsProviderId);

				}
				catch (ApplicationException aoAppEx)
				{
					LOG_OBJECT.Error("Exception in BatchComponent.executeApplicationExpiryNotificationBatch() ",
							aoAppEx);
					continue;
				}
			} // end outer if

			// } // end
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in BatchComponent.executeApplicationExpiryNotificationBatch() ", aoAppEx);
			throw aoAppEx;
		}

		loArrFetchedProviders = null;
		LOG_OBJECT.Debug("Exited executeApplicationExpiryNotificationBatch() ");

	} // end executeApplicationExpiryNotificationBatch

	/**
	 * This method is used for calling the notification service for application
	 * expiry
	 * <ul>
	 * <li>Fetch the server name</li>
	 * <li>Fetch the Server Port</li>
	 * <li>Fetch the Context Path</li>
	 * <li></li>
	 * </ul>
	 * @param asApplicationId Application Id
	 * @param asNotificationName String denoting the notification name
	 * @param asAlertName String denoting the alert name
	 * @param asProviderId String denoting the Provider Id
	 * @param aoSqlDate Sql Date
	 * @param aiNotificationDays Notification Days
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes", "unused" })
	private void callNotificationApplicationExpiry(String asApplicationId, String asNotificationName,
			String asAlertName, String asProviderId, Date aoSqlDate, int aiNotificationDays)
			throws ApplicationException
	{
		HashMap<String, HashMap<String, String>> loParamMapInHashMap = null;
		try
		{
			Channel loChannelObj;

			// Create the data for sending notifications
			ArrayList<String> loAlertsList = new ArrayList<String>();
			loAlertsList.add(asNotificationName);

			if (!asAlertName.equalsIgnoreCase(""))
			{
				loAlertsList.add(asAlertName);
			}

			ArrayList<String> loProviderList = new ArrayList<String>();
			loProviderList.add(asProviderId);

			StringBuffer lsBfApplicationUrl = new StringBuffer();
			String lsServerName = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices",
					"SERVER_NAME_FOR_PROVIDER_BATCH");
			String lsServerPort = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices",
					"SERVER_PORT_FOR_PROVIDER_BATCH");
			String lsContextPath = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices",
					"CONTEXT_PATH_FOR_PROVIDER_BATCH");
			String lsAppProtocol = PropertyLoader.getProperty("com.nyc.hhs.properties.hhsservices",
					"SERVER_PROTOCOL_FOR_PROVIDER_BATCH");
			if (asNotificationName.equalsIgnoreCase(ApplicationConstants.NT022)
					|| asNotificationName.equalsIgnoreCase(ApplicationConstants.NT023)
					|| asNotificationName.equalsIgnoreCase(ApplicationConstants.NT024)
					|| asAlertName.equalsIgnoreCase(ApplicationConstants.AL020)
					|| asAlertName.equalsIgnoreCase(ApplicationConstants.AL021)
					|| asAlertName.equalsIgnoreCase(ApplicationConstants.AL022)
					|| asAlertName.equalsIgnoreCase(ApplicationConstants.AL029)
					//*** Start R 9.1 QC 9611 - Update in-system notifications and alerts for CHAR 500 ***
					|| asNotificationName.equalsIgnoreCase(ApplicationConstants.NT035)
					//*** End R 9.1 QC 9611 - Update in-system notifications and alerts for CHAR 500 ***
					)
			{
				//*** Start R 9.1 QC 9611 - Update in-system notifications and alerts for CHAR 500 ***
				/*
				lsBfApplicationUrl.append(lsAppProtocol);
				lsBfApplicationUrl.append("://");
				lsBfApplicationUrl.append(lsServerName);
				lsBfApplicationUrl.append(":");
				lsBfApplicationUrl.append(lsServerPort);
				lsBfApplicationUrl.append("/");
				lsBfApplicationUrl.append(lsContextPath);
				lsBfApplicationUrl.append(ApplicationConstants.SHARED_DOCUMENT_ALERT_NOTIFICATION_LINK);
				*/
				
				lsBfApplicationUrl.append(ApplicationConstants.PASSPORT_SHARED_DOCUMENT_ALERT_NOTIFICATION_LINK);
				//*** End R 9.1 QC 9611 - Update in-system notifications and alerts for CHAR 500 ***

			}
			else
			{
				lsBfApplicationUrl.append(lsAppProtocol);
				lsBfApplicationUrl.append("://");
				lsBfApplicationUrl.append(lsServerName);
				lsBfApplicationUrl.append(":");
				lsBfApplicationUrl.append(lsServerPort);
				lsBfApplicationUrl.append("/");
				lsBfApplicationUrl.append(lsContextPath);
				lsBfApplicationUrl.append(ApplicationConstants.TASK_ALERT_NOTIFICATION_LINK);
			}

			HashMap loRequestMap = new HashMap();
			loRequestMap.put("LEFTDAYS", String.valueOf(aiNotificationDays));
			
			NotificationDataBean loNotificationDataBean = new NotificationDataBean();
			HashMap<String, String> loLinkMap = new HashMap<String, String>();
			loLinkMap.put(HHSConstants.LINK, lsBfApplicationUrl.toString());
			loNotificationDataBean.setAgencyLinkMap(loLinkMap);
			loNotificationDataBean.setLinkMap(loLinkMap);
			loNotificationDataBean.setProviderList(loProviderList);

			SimpleDateFormat loDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			String lsDueDate = loDateFormat.format(aoSqlDate);
			loRequestMap.put("MMDDYYYY", lsDueDate);

			HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
			loNotificationMap.put(TransactionConstants.EVENT_ID_PARAMETER_NAME, loAlertsList);
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loAlertsList);
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMap.put(TransactionConstants.PROVIDER_ID, loProviderList);
			loNotificationMap.put("NotificationName", asNotificationName);
			loNotificationMap.put("DueDate", aoSqlDate);
			loNotificationMap.put("ProviderId", asProviderId);
			loNotificationMap.put("lsEntityId", asApplicationId);
			loNotificationMap.put(HHSConstants.ENTITY_ID, asApplicationId);
			loNotificationMap.put(HHSConstants.ENTITY_TYPE, ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, HHSConstants.SYSTEM_USER);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, HHSConstants.SYSTEM_USER);
			// Changed in 3.1.0 . Added check for Defect 6385
			for (String lsNotificationName : loAlertsList)
			{
				loNotificationMap.put(lsNotificationName, loNotificationDataBean);
			}
			if (asAlertName.equalsIgnoreCase(ApplicationConstants.AL027))
			{
				loNotificationMap.put(asAlertName, loNotificationDataBean);
				fetchAndUpdateProviderStatusForExpiredApplication(asApplicationId, asProviderId, loNotificationMap);

			}
			else
			{
				// ************************ APPLICATION ABOUT TO EXPIRE
				// *****************************************
				// Send general notifications
				loChannelObj = new Channel();
				loChannelObj.setData("loHmNotifyParam", loNotificationMap);

				LOG_OBJECT.Debug(" before executing transaction insertDueDateForEfillingBatch");
				// this transaction make entry in due date reminder and in
				// notification table.
				TransactionManager.executeTransaction(loChannelObj, "insertDueDateForEfillingBatch");

				LOG_OBJECT.Debug(" after executing transaction insertDueDateForEfillingBatch");
				// execute notification transaction
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in BatchComponent.executeApplicationExpiryNotificationBatch() ", aoAppEx);
			throw aoAppEx;
		}

	}

	/**
	 * This method fetches And Updates Provider Status For Expired Application
	 * <ul>
	 * <li>Execute transaction id <b> NotificationMap </b></li>
	 * <li>Set Channel object</li>
	 * </ul>
	 * @param asApplicationId Application Id
	 * @param asProviderId Provider Id
	 * @param aoNotificationMap Notification Map
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void fetchAndUpdateProviderStatusForExpiredApplication(String asApplicationId, String asProviderId,
			HashMap<String, Object> aoNotificationMap) throws ApplicationException
	{
		// ********** APPLICATION HAS EXPIRED ****************

		// Check if there exists any other latest application for the
		// same
		// provider other than this application
		Channel loChannelObj = new Channel();
		HashMap loHMap = new HashMap();
		loHMap.put("ProviderId", asProviderId);
		loHMap.put("ApplicationId", asApplicationId);
		loHMap.put("asProviderId", asProviderId);
		loHMap.put("asProviderStatus", msProviderExpiryStatus);
		loHMap.put("lsEntityId", asApplicationId);
		loHMap.put("lsEntityType", ApplicationConstants.ENTITY_TYPE_BUSINESS_APPLICATION);
		loHMap.put("asProviderId", asProviderId);
		loHMap.put("asProviderStatus", msProviderExpiryStatus);
		setAuditValuesInHashMap(asProviderId, "", asApplicationId, loChannelObj);
		loChannelObj.setData("loHMap", loHMap);
		loChannelObj.setData("loHmNotifyParam", aoNotificationMap);

		LOG_OBJECT.Debug("Executing transaction transactionApplicationExparyBatch_db");
		TransactionManager.executeTransaction(loChannelObj, "transactionApplicationExparyBatch_db");
		LOG_OBJECT.Debug("Finished transaction transactionApplicationExparyBatch_db");

	}

	/**
	 * This method is used for checking whether an entry exists in the table
	 * DueDate_Reminder for the same notification name, provider and due date.
	 * It returns 0 if no entry exists, else returns a non zero int
	 * <ul>
	 * <li>Execute transaction id <b> fetchDueDateReminderCount</b></li>
	 * <li>Set the Channel object</li>
	 * </ul>
	 * @param asNotificationName String denoting the notification name
	 * @param asAlertName String denoting the alert name
	 * @param asProviderId String denoting the Provider Id
	 * @param aoSqlDueDate java.sql.Date denoting the date entry in
	 *            DueDate_Reminder table
	 * @param asApplicationId Application Id
	 * @return int which can be 0 or non zero
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public int fetchDueDateReminderCount(String asNotificationName, String asAlertName, String asProviderId,
			java.sql.Date aoSqlDueDate, String asApplicationId) throws ApplicationException
	{

		LOG_OBJECT.Debug("Entered fetchDueDateReminderCount() ");

		try
		{
			Channel loChannelObj = new Channel();

			HashMap loHMap = new HashMap();
			loHMap.put("ProviderId", asProviderId);
			loHMap.put("DueDate", aoSqlDueDate);
			loHMap.put("NotificationName", asNotificationName);
			loHMap.put("lsEntityId", asApplicationId);

			loChannelObj.setData("loHMap", loHMap);

			LOG_OBJECT.Debug("Executing transaction fetchDueDateReminderCount");
			TransactionManager.executeTransaction(loChannelObj, "fetchDueDateReminderCount");
			String lsCount = (String) loChannelObj.getData("COUNT");
			LOG_OBJECT.Debug("Finished transaction fetchDueDateReminderCount");

			int liCount = Integer.parseInt(lsCount);

			LOG_OBJECT.Debug("Obtained liCount = " + liCount);
			LOG_OBJECT.Debug("Exited fetchDueDateReminderCount() ");
			return liCount;
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in BatchComponent.fetchDueDateReminderCount() ", aoAppEx);
			throw aoAppEx;
		}
	}

	/**
	 * This method is used for updating the status of provider with the
	 * suspended status value
	 * <ul>
	 * <li>Set the Channel object</li>
	 * <li>Execute transaction id <b> updateProviderStatus</b></li>
	 * </ul>
	 * @param asProviderId String provider id
	 * @param asProviderSuspendedStatus String status value for suspended
	 *            provider
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unused")
	private void updateProviderStatus(Channel aoChannelObj, String asProviderId, String asStatus, String asBRAppId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered updateProviderStatus() ");

		try
		{
			LOG_OBJECT.Debug("Executing transaction updateProviderStatus ");
			TransactionManager.executeTransaction(aoChannelObj, "updateProviderStatus");
			LOG_OBJECT.Debug("Finished transaction updateProviderStatus ");

			LOG_OBJECT.Debug("Updating audit info for provider status change");

			Channel loChannel = new Channel();
			setAuditValuesInChannel(asProviderId, asStatus, asBRAppId, loChannel);

			LOG_OBJECT.Debug("Updated audit info for provider status change");

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in BatchComponent.updateProviderStatus()", aoAppEx);
			throw aoAppEx;
		}

		LOG_OBJECT.Debug("Exited updateProviderStatus() ");

	}

	/**
	 * This method set Audit Values In Channel
	 * <ul>
	 * <li>Set the Channel object</li>
	 * </ul>
	 * @param asProviderId Provider Id
	 * @param asStatus Status
	 * @param asBRAppId BRApp Id
	 * @param aoChannel Channel
	 * @throws ApplicationException
	 */
	private void setAuditValuesInChannel(String asProviderId, String asStatus, String asBRAppId, Channel aoChannel)
			throws ApplicationException
	{
		aoChannel.setData("orgId", asProviderId);
		aoChannel.setData("eventName", ApplicationConstants.STATUS_CHANGE);
		aoChannel.setData("eventType", ApplicationConstants.PROVIDER_STATUS);
		aoChannel.setData("auditDate", DateUtil.getFormattedDated("dd/MM/yyyy HH:mm:ss", new Date()));
		aoChannel.setData("userId", ApplicationConstants.BATCH);
		aoChannel.setData("data", ApplicationConstants.STATUS_CHANGED_TO.concat(asStatus));

		aoChannel.setData("entityType", ApplicationConstants.PROVIDER_STATUS);
		aoChannel.setData("entityId", ApplicationConstants.PROVIDER_STATUS);
		aoChannel.setData("status", asStatus);
		aoChannel.setData("providerFlag", "false");
		aoChannel.setData("appId", asBRAppId);
		aoChannel.setData("sectionId", asBRAppId);
		aoChannel.setData("EntityIdentifier", ApplicationConstants.PROVIDER_STATUS_CHANGE);

		aoChannel.setData("asAuditType", "application");
	}

	/**
	 * This method sets Audit Values In HashMap
	 * <ul>
	 * <li>sets the loAuditMapForBatch HashMap</li>
	 * </ul>
	 * @param asProviderId Provider Id
	 * @param asStatus Status
	 * @param asBRAppId BRApp Id
	 * @param aoChannel Channel
	 * @throws ApplicationException
	 */
	private void setAuditValuesInHashMap(String asProviderId, String asStatus, String asBRAppId, Channel aoChannel)
			throws ApplicationException
	{
		HashMap<String, Object> loAuditMapForBatch = new HashMap<String, Object>();
		loAuditMapForBatch.put("orgId", asProviderId);
		loAuditMapForBatch.put("eventName", ApplicationConstants.STATUS_CHANGE_BY_BATCH);
		loAuditMapForBatch.put("eventType", ApplicationConstants.STATUS_CHANGE_BY_BATCH);
		loAuditMapForBatch.put("auditDate", DateUtil.getFormattedDated("dd/MM/yyyy HH:mm:ss", new Date()));
		loAuditMapForBatch.put("userId", ApplicationConstants.BATCH);
		loAuditMapForBatch.put("data", ApplicationConstants.STATUS_CHANGED_TO.concat(asStatus));

		if (null != asBRAppId && asBRAppId.contains("br_"))
		{
			loAuditMapForBatch.put("entityType", "Business Application");
		}
		else if (null != asBRAppId && asBRAppId.contains("sr_"))
		{
			loAuditMapForBatch.put("entityType", "Service Application");
		}
		loAuditMapForBatch.put("entityId", asBRAppId);
		loAuditMapForBatch.put("status", asStatus);
		loAuditMapForBatch.put("providerFlag", "false");
		loAuditMapForBatch.put("appId", asBRAppId);
		loAuditMapForBatch.put("sectionId", asBRAppId);
		loAuditMapForBatch.put("EntityIdentifier", ApplicationConstants.STATUS_CHANGE_BY_BATCH);
		aoChannel.setData("batchAuditMap", loAuditMapForBatch);
	}

	/**
	 * For BR applications whose status is Draft and TIMESTAMP in
	 * Superseding_status table < 90 days and super-seeding status is
	 * 'conditionally approved' make provider and application status to
	 * 'Expired'
	 * <ul>
	 * <li>This method gets and Updates Provider Status For Batch</li>
	 * <li>Execute transaction id <b>
	 * selectConditionallyApprovedProvidersfromSuperseding</b></li>
	 * </ul>
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public void executeConditionallyApprovedStatusBatch() throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered executeConditionallyApprovedStatusBatch() ");
		String lsProviderId = "";
		String lsEntityType = "";
		String lsEntityId = "";
		Channel loChannelObj = new Channel();
		HashMap<String, String> loHMap = new HashMap<String, String>();
		List<HashMap<String, String>> loProviderDetails = null;
		try
		{
			// 1. [Not needed now]
			// Conditional approval status expiry transaction for making
			// application and provider status to expired
			// for those applications and providers where conditional approval
			// date is past 3 months
			// For BR applications whose status is Draft
			// and TIMESTAMP in Superseding_status table is past 90 days
			// and super-seeding status is 'conditionally approved'
			// update provider and application status to 'suspended'
			loHMap.put(ApplicationConstants.SUPER_SEDING_KEY_CA, ApplicationConstants.STATUS_CONDITIONALLY_APPROVED);
			loHMap.put(ApplicationConstants.SUPER_SEDING_KEY_DRAFT, ApplicationConstants.APP_STATUS_DRAFT);
			loChannelObj.setData("loHMap", loHMap);
			// select all applications whose status is conditionally approved in
			// superseding_status table
			// and whose expiry_date is past today's date in application expiry
			// table
			LOG_OBJECT.Debug("Executing transaction selectConditionallyApprovedfromSuperseding");
			TransactionManager.executeTransaction(loChannelObj, "selectConditionallyApprovedProvidersfromSuperseding");
			loProviderDetails = (List<HashMap<String, String>>) loChannelObj.getData("loOutputMap");
			LOG_OBJECT.Debug("Finished transaction selectConditionallyApprovedfromSuperseding");
			if (null == loProviderDetails || loProviderDetails.isEmpty())
			{
				LOG_OBJECT.Debug("executeConditionallyApprovedStatusBatch :: No records found");
			}
			else
			{
				Iterator<HashMap<String, String>> loIterator = loProviderDetails.iterator();
				while (loIterator.hasNext())
				{
					HashMap<String, String> loServiceAppMap = loIterator.next();
					if (null != loServiceAppMap && !loServiceAppMap.isEmpty())
					{
						lsProviderId = loServiceAppMap.get("ORGANIZATION_ID");
						lsEntityType = loServiceAppMap.get("ENTITY_TYPE");
						lsEntityId = loServiceAppMap.get("ENTITY_ID");
					}

					// Call
					// FileNetOperationUtils.getAndUpdateProviderStatusForBatch
					// with the provider_id
					// It will get all the BR and SA applications for a
					// provider,
					// and calculate its previous status,
					// and update its previous status for the provider in
					// organization table.
					// It will also delete all conditionally-approved entries in
					// superseding table for that BR,SR and provider
					LOG_OBJECT.Debug("Calling FileNetOperationsUtils.getAndUpdateProviderStatus for provider = "
							+ lsProviderId);
					FileNetOperationsUtils.getAndUpdateProviderStatusForBatch(lsProviderId, lsEntityType, lsEntityId);
					LOG_OBJECT.Debug("Finished FileNetOperationsUtils.getAndUpdateProviderStatus for provider = "
							+ lsProviderId);
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in BatchComponent.executeConditionallyApprovedStatusBatch()", aoAppEx);
			throw aoAppEx;
		}
	}

	/**
	 * This method calculates Entity Id
	 * <ul>
	 * <li>Execute transaction id <b> fetchLatestBRAppIdforProvider</b></li>
	 * <li>Set the Channel object</li>
	 * </ul>
	 * @param asProviderId Provider Id
	 * @return String
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private ArrayList<String> calculateEntityId(String asProviderId) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered calculateEntityId with asProviderId = " + asProviderId);
		String lsEntityId = "";
		String lsBROldId = "";
		String lsBROldStatus = "";
		String lsLatestEntityId = "";
		ArrayList<String> idStatusList = new ArrayList<String>();
		// *********************************************************************************************************************
		/*
		 * Fetch the latest BR app id for the provider lsLatestEntityId Count
		 * the no. of applications for the same provider If there is only one
		 * application , fetch and return the BR-Id of that application Else 1.
		 * fetch the status of latest BR app id (lsLatestBRStatus) of the
		 * application 2. fetch the BR id (lsBROldId) and status (lsBROldStatus)
		 * of the next older application 3. calculate which BR id to return as
		 * entity based on the below rules :-
		 * 
		 * If lsLatestBRStatus = Approved/Rejected , return lsLatestEntityId
		 * else if lsLatestBRStatus <> Approved/Rejected and lsBROldStatus =
		 * Expired , return lsLatestEntityId else if lsLatestBRStatus <>
		 * Approved/Rejected and lsBROldStatus <> Expired , return lsBROldId
		 */
		// *************************************************************************************************************************
		try
		{
			// Check if there exists any other application for the same provider
			Channel loChannelObj = new Channel();
			HashMap loHMap = new HashMap();
			loHMap.put("asProviderId", asProviderId);
			loHMap.put("ProviderId", asProviderId);
			loChannelObj.setData("loHMap", loHMap);
			// Fetch the latest BR application id from BR Application table for
			// the provider
			LOG_OBJECT.Debug("Executing transaction fetchLatestBRAppIdforProvider");
			TransactionManager.executeTransaction(loChannelObj, "fetchLatestBRAppIdforProvider");
			lsLatestEntityId = (String) loChannelObj.getData("Entity_Id");
			LOG_OBJECT.Debug("Finished transaction fetchLatestBRAppIdforProvider");
			if (null == lsLatestEntityId)
			{
				LOG_OBJECT
						.Error("Exception in DocLapsingBatch.calculateEntityId(). No application exists for provider id "
								+ asProviderId);
				return idStatusList; // QC 8667 Release 6.1.0
			}

			idStatusList = getEntityId(asProviderId, lsEntityId, lsBROldId, lsBROldStatus, lsLatestEntityId,
					loChannelObj, loHMap);

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in DocLapsingBatch.calculateEntityId()", aoAppEx);
			throw aoAppEx;
		}
		LOG_OBJECT.Debug("Exiting calculateEntityId. Returned lsEntityId = " + idStatusList.get(0));

		// return the calculated application id
		return idStatusList;
	}

	/**
	 * This method execute Txid fetchDuplicateProviderApplicationCount
	 * 
	 * @param asProviderId
	 * @param lsEntityId
	 * @param lsBROldId
	 * @param lsBROldStatus
	 * @param lsLatestEntityId
	 * @param loChannelObj
	 * @param loHMap
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private ArrayList<String> getEntityId(String asProviderId, String lsEntityId, String lsBROldId,
			String lsBROldStatus, String lsLatestEntityId, Channel loChannelObj, HashMap loHMap)
			throws ApplicationException
	{
		ArrayList<String> idStatusList = new ArrayList<String>();
		String lsLatestBRStatus = "";
		Integer lsBOldNumDays = null;
		ApplicationIdStatusBean loAplicationIdStatusBean = null;
		List<ApplicationIdStatusBean> loAplicationIdStatusBeanList = null;
		loHMap.put("ApplicationId", lsLatestEntityId);
		// boolean conditionallyApproved = false;
		String lsConditionallyApprovedStatus = "";
		// Check if there exists more than 1 application for the same
		// provider
		// always returned 0 - hardcoded value
		LOG_OBJECT.Debug("Executing transaction fetchDuplicateProviderApplicationCount");
		TransactionManager.executeTransaction(loChannelObj, "fetchDuplicateProviderApplicationCount");
		String lsCount = (String) loChannelObj.getData("COUNT");
		LOG_OBJECT.Debug("Finished transaction fetchDuplicateProviderApplicationCount");
		int liCount = Integer.parseInt(lsCount);
		if (liCount == 1)
		{
			// there is only one application for the provider
			lsEntityId = lsLatestEntityId;
		}
		else
		{
			// There are more than 1 applications for the same provider
			// Fetch the current status for the latest BR App id
			loChannelObj = new Channel();
			loHMap = new HashMap();
			loHMap.put("asBussAppId", lsLatestEntityId);
			loHMap.put("asProviderId", asProviderId);
			loHMap.put("ProviderId", asProviderId);
			loChannelObj.setData("loHMap", loHMap);
			LOG_OBJECT.Debug("Executing transaction fetchBusinessApplicationStatus");
			TransactionManager.executeTransaction(loChannelObj, "fetchBusinessApplicationStatus");
			lsLatestBRStatus = (String) loChannelObj.getData("lsBRAppStatus");
			LOG_OBJECT.Debug("Finished transaction fetchBusinessApplicationStatus");

			// Fetch the BR id and status of the older BR application
			LOG_OBJECT.Debug("Executing transaction fetchApplicationIdStatus");
			TransactionManager.executeTransaction(loChannelObj, "fetchApplicationIdStatus");
			loAplicationIdStatusBeanList = (List<ApplicationIdStatusBean>) loChannelObj.getData("applicationIdDetails");
			LOG_OBJECT.Debug("Executed transaction applicationExpiryDetails");
			Iterator<ApplicationIdStatusBean> loIterator = loAplicationIdStatusBeanList.iterator();
			while (loIterator.hasNext())
			{
				loAplicationIdStatusBean = loIterator.next();
				lsBROldId = loAplicationIdStatusBean.getApplicationId();
				lsBROldStatus = loAplicationIdStatusBean.getStatus();
				String tempNumDays = loAplicationIdStatusBean.getMsNumDays();
				lsBOldNumDays = null;
				if (tempNumDays != null)
				{
					lsBOldNumDays = Integer.parseInt(tempNumDays);
				}

			}
			LOG_OBJECT.Debug("Finished transaction fetchApplicationIdStatus");

			// calculate which BR id to return in lsEntityId
			if (lsLatestBRStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED)
					|| lsLatestBRStatus.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED))
			{
				// return lsLatestEntityId;
				idStatusList.add(lsLatestEntityId);
				return idStatusList;
			}
			// QC 8667 Release 6.1.0
			// check if Latest BA is Conditionally Approved
			if (ApplicationConstants.STATUS_DRAFT.equalsIgnoreCase(lsLatestBRStatus)
					|| ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS.equalsIgnoreCase(lsLatestBRStatus)
					|| ApplicationConstants.STATUS_IN_REVIEW.equalsIgnoreCase(lsLatestBRStatus))
			{
				lsConditionallyApprovedStatus = verifyCondtionallyApprovedBRStatus(asProviderId, lsLatestEntityId);

				if (ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(lsConditionallyApprovedStatus)
						|| ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(lsConditionallyApprovedStatus))
				{
					idStatusList.add(0, lsLatestEntityId);
					idStatusList.add(1, ApplicationConstants.STATUS_CONDITIONALLY_APPROVED);
					return idStatusList;
				}
			}

			// since BA Status is never 'Expired' this condition is never met
			if (lsBROldStatus.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED)
					&& (!(lsLatestBRStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED) || lsLatestBRStatus
							.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED))))
			{
				// return lsLatestEntityId;
				idStatusList.add(0, lsLatestEntityId);
				return idStatusList;
			}
			// QC QC 8667 Release 6.1.0
			// if latest BA is not Approved/Conditionally Approved and not
			// Rejected,
			// check if previous BA is Conditionally Approved
			if ((ApplicationConstants.STATUS_DRAFT.equalsIgnoreCase(lsBROldStatus)
					|| ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS.equalsIgnoreCase(lsBROldStatus) || ApplicationConstants.STATUS_IN_REVIEW
						.equalsIgnoreCase(lsBROldStatus))
					&& (!(lsLatestBRStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED) || lsLatestBRStatus
							.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED))))
			{

				lsConditionallyApprovedStatus = verifyCondtionallyApprovedBRStatus(asProviderId, lsBROldId);

				if (ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(lsConditionallyApprovedStatus)
						|| ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(lsConditionallyApprovedStatus))
				{
					idStatusList.add(0, lsBROldId);
					idStatusList.add(1, ApplicationConstants.STATUS_CONDITIONALLY_APPROVED);
					return idStatusList;
				}

			}
			// QC 8667 Release 6.1.0
			// if latest BA is not Approved and not Rejected and previous BA is
			// in any status, but not Conditionally approved
			if (!lsBROldStatus.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED)
					&& (!(lsLatestBRStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED) || lsLatestBRStatus
							.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED))))
			{
				idStatusList.add(0, lsBROldId);
				// return lsBROldId;
				return idStatusList;
			}
		}

		return idStatusList; // lsEntityId;
	}

	// BEGIN QC 6749 Release 6.1.0
	/**
	 * This method execute fetchLatestBADetailsForProvider
	 * 
	 * @param asProviderId
	 * @param asApplicationId
	 * @return arrayList
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private List<String> getLatestBADetails(String asProviderId, String asApplicationId) throws ApplicationException // getLatestBRStatus
	{
		String lsLatestBAStatus = "";
		String lsLatestBRId = "";
		String lsLatestBRDays = "";
		ApplicationIdStatusBean loAplicationIdStatusBean = null;
		List<ApplicationIdStatusBean> loAplicationIdStatusBeanList = null;
		List<String> baList = new ArrayList<String>();

		LOG_OBJECT.Debug("Executing transaction fetchLatestBADetailsForProvider");
		Channel loChannelObj = new Channel();
		HashMap loHMap = new HashMap();
		loHMap.put(HHSConstants.PROVIDER_ID_KEY, asProviderId);
		loChannelObj.setData(ApplicationConstants.BATCH_TRANSACTION_PARAM_NAME, loHMap);
		LOG_OBJECT.Debug("Executing transaction fetchLatestBADetailsForProvider");
		TransactionManager.executeTransaction(loChannelObj, "fetchLatestBADetailsForProvider");
		loAplicationIdStatusBeanList = (List<ApplicationIdStatusBean>) loChannelObj.getData("latestBADetails");
		LOG_OBJECT.Debug("Executed transaction fetchLatestBADetailsForProvider");

		Iterator<ApplicationIdStatusBean> loIterator = loAplicationIdStatusBeanList.iterator();
		while (loIterator.hasNext())
		{
			loAplicationIdStatusBean = loIterator.next();
			lsLatestBAStatus = loAplicationIdStatusBean.getStatus();
			lsLatestBRId = loAplicationIdStatusBean.getApplicationId();
			lsLatestBRDays = loAplicationIdStatusBean.getMsNumDays();
			baList.add(0, lsLatestBRId);
			baList.add(1, lsLatestBAStatus);
			baList.add(2, lsLatestBRDays);
		}
		LOG_OBJECT.Debug("Finished transaction fetchApplicationIdStatus");

		return baList;
	}

	/**
	 * This method execute getBusinessAndServiceStatus
	 * 
	 * @param asProviderId
	 * @param asApplicationId
	 * @return
	 * @throws ApplicationException
	 */

	private String verifyCondtionallyApprovedBRStatus(String asProviderId, String lsLatestBAId)
			throws ApplicationException
	{

		/* check if latest BA is Conditionally Approved */
		String lsBrStatus = "";
		String lsNewProviderStatus = "";
		List<String> loLServiceApplicationStatuses = new ArrayList<String>();
		Channel loChannel = new Channel();
		Map<String, String> loRequiredProps = new HashMap<String, String>();
		loRequiredProps.put("providerId", asProviderId);
		loRequiredProps.put("businessAppId", lsLatestBAId);
		loChannel.setData("aoRequiredProps", loRequiredProps);
		TransactionManager.executeTransaction(loChannel, "getBusinessAndServiceStatus");
		List<ProviderStatusBean> loProviderStatusBeanList = (List<ProviderStatusBean>) loChannel.getData("statusList");
		Iterator<ProviderStatusBean> stIterator = loProviderStatusBeanList.iterator();

		while (stIterator.hasNext())
		{
			ProviderStatusBean loProviderStatusBean = (ProviderStatusBean) stIterator.next();
			if (loProviderStatusBean.getApplicationId().equalsIgnoreCase(lsLatestBAId))
			{
				if (loProviderStatusBean.getSupersedingStatus() != null)
				{
					lsBrStatus = loProviderStatusBean.getSupersedingStatus();
				}
				else
				{
					lsBrStatus = loProviderStatusBean.getApplicationStatus();
				}
			}
			else
			{
				loLServiceApplicationStatuses.add(loProviderStatusBean.getApplicationStatus());
				if (loProviderStatusBean.getSupersedingStatus() != null)
				{
					loLServiceApplicationStatuses.add(loProviderStatusBean.getSupersedingStatus());
				}
			}
		}

		lsNewProviderStatus = ProviderStatusBusinessRules.getProviderStatusOnBRWithdrawalRejection(lsBrStatus,
				loLServiceApplicationStatuses);

		return lsNewProviderStatus;
	}

	// END - fix for QC 6749 Release 6.1.0

	// BEGIN QC 8515 Release 6.1.0
	/**
	 * for provider with more then 1 BR
	 * @param asProviderId
	 * @param asApplicationId
	 * @return list of all Business Applications per Provider
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	private ArrayList<ApplicationIdBRStatusBean> fetchAllBusinessApplicationForProvider(String asProviderId)
			throws ApplicationException
	{
		ArrayList<ApplicationIdBRStatusBean> loAplicationIdBRStatusBeanList = null;
		Channel loChannelObj = new Channel();
		HashMap<String, String> loHMap = new HashMap<String, String>();
		loHMap.put(HHSConstants.PROVIDER_ID_KEY, asProviderId);
		loChannelObj.setData(ApplicationConstants.BATCH_TRANSACTION_PARAM_NAME, loHMap);

		LOG_OBJECT.Debug("Executing transaction fetchApplicationIdBRStatus for Provider" + asProviderId);
		TransactionManager.executeTransaction(loChannelObj, "fetchApplicationIdBRStatus");
		loAplicationIdBRStatusBeanList = (ArrayList<ApplicationIdBRStatusBean>) loChannelObj
				.getData("applicationIdBRDetails");
		LOG_OBJECT.Debug("Finished transaction fetchApplicationBRIdStatus");

		return loAplicationIdBRStatusBeanList;
	}

	/**
	 * for provider with more then 1 BR exclude expired BR from current process
	 * if 1. any latest BR is Approved/Conditionally Approved: BR status 2.
	 * latest BR/processing BR (if only 1 exists) is Draft/Review/Return for
	 * Revision and Provider status Expired 3. latest BR/processing BR (if only
	 * 1 exists) is Withdrawn and Provider status Withdrawn
	 * @param asProviderId
	 * @param asApplicationId
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	private Boolean excludeBR(String asProviderId, String asBusApplicationId, String orgStatus, int asNumDays,
			List<ApplicationIdBRStatusBean> businessApplicationList) throws ApplicationException
	{
		String loBRStatus = "";
		String loBRId = "";
		String svBRId = "";
		String svBRStatus = "";
		Integer svNumDays = null;
		int counter = 0;
		ApplicationIdBRStatusBean loAplicationIdBRStatusBean = null;
		List<ApplicationIdBRStatusBean> baList = new ArrayList<ApplicationIdBRStatusBean>(businessApplicationList);
		Iterator<ApplicationIdBRStatusBean> itr = baList.iterator();
		while (itr.hasNext())
		{
			loAplicationIdBRStatusBean = itr.next();
			loBRId = loAplicationIdBRStatusBean.getMsApplicationId();
			if (loBRId.equalsIgnoreCase(asBusApplicationId))
			{
				continue;
			}
			loBRStatus = loAplicationIdBRStatusBean.getMsApplicationStatus();
			String loNum = loAplicationIdBRStatusBean.getMiNumDays();
			counter++;
			Integer loNumDays = null;
			if (loNum != null)
			{
				loNumDays = Integer.parseInt(loNum);
			}

			// BR currently Approved/Draft and Org is Approved/Conditionally
			// Approved/Suspended because of Filing Expired
			if ((ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(orgStatus) || ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED
					.equalsIgnoreCase(orgStatus))
					&& loNumDays != null
					&& loNumDays > 0
					&& ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(loBRStatus))
			{
				return true;
			}
			// Conditionally Approved BA
			if ((ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(orgStatus)
					|| ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(orgStatus) || ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED
						.equalsIgnoreCase(orgStatus))
					&& loNumDays != null
					&& loNumDays > 0
					&& (ApplicationConstants.STATUS_DRAFT.equalsIgnoreCase(loBRStatus)
							|| ApplicationConstants.STATUS_IN_REVIEW.equalsIgnoreCase(loBRStatus) || ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS
								.equalsIgnoreCase(loBRStatus)))

			{
				String lsConditionallyApprovedStatus = verifyCondtionallyApprovedBRStatus(asProviderId, loBRId);

				if (ApplicationConstants.STATUS_CONDITIONALLY_APPROVED.equalsIgnoreCase(lsConditionallyApprovedStatus)
						|| ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase(lsConditionallyApprovedStatus))
				{
					return true;
				}
			}
			// Skip current expired BR if latest expired BR exists
			if (loNumDays != null && loNumDays <= 0 && loNumDays >= asNumDays)
			{
				return true;
			}
			// skip evaluation if BR is expired and older then processing one
			if (loNumDays != null && loNumDays <= 0 && loNumDays < asNumDays)
			{
				continue;
			}

			// save the latest BR
			svBRId = loBRId;
			svBRStatus = loBRStatus;
			svNumDays = loNumDays;
		}

		/* check THE LATEST BR */

		// Provider status was already updated to Expired - no needed to be
		// updated to Expired again
		if (ApplicationConstants.STATUS_EXPIRED.equalsIgnoreCase(orgStatus)
				&& (ApplicationConstants.STATUS_DRAFT.equalsIgnoreCase(svBRStatus)
						|| ApplicationConstants.STATUS_IN_REVIEW.equalsIgnoreCase(svBRStatus)
						|| ApplicationConstants.STATUS_REJECTED.equalsIgnoreCase(svBRStatus)
						|| ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS.equalsIgnoreCase(svBRStatus) || ApplicationConstants.STATUS_DEFFERED
							.equalsIgnoreCase(svBRStatus)))

		{
			return true;
		}
		// Provider status was already updated to Withdrawn - no needed to be
		// updated to Withdrawn again
		if (ApplicationConstants.STATUS_WITHDRAWN.equalsIgnoreCase(orgStatus)
				&& ApplicationConstants.STATUS_WITHDRAWN.equalsIgnoreCase(svBRStatus))
		{
			return true;
		}
		//
		if (ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(orgStatus)
				&& ApplicationConstants.STATUS_SUSPEND.equalsIgnoreCase(svBRStatus))
		{
			return true;
		}

		return false;
	}

	private String getCurrentProviderStatus(String asProviderId) throws ApplicationException
	{
		HashMap<String, String> loRequiredProps = new HashMap<String, String>();
		Channel loChannel = new Channel();
		loRequiredProps.put(ApplicationConstants.PROVIDER_ID, asProviderId);
		loChannel.setData(ApplicationConstants.REQUIRED_PROPS, loRequiredProps);
		TransactionManager.executeTransaction(loChannel, "getCurrentProviderStatus");
		String status = (String) loChannel.getData("status");

		return status;
	}

	private int fetchBusinessApplicationCount(String asProviderId) throws ApplicationException
	{
		HashMap<String, String> loHMap = new HashMap<String, String>();
		Channel loChannelObj = new Channel();
		loHMap.put("ProviderId", asProviderId);
		loChannelObj.setData(ApplicationConstants.BATCH_TRANSACTION_PARAM_NAME, loHMap);
		TransactionManager.executeTransaction(loChannelObj, "fetchBusinessApplicationCount");
		String count = (String) loChannelObj.getData("COUNT");
		LOG_OBJECT.Debug("Finished transaction fetchBusinessApplicationCount");
		int counter = Integer.parseInt(count);

		return counter;
	}

	// END - QC 8515 Release 6.1.0

	// BEGIN QC 8667 Release 6.1.0
	private int fetchSupersedingStatusCount(String asProviderId, String superSedingStatusKey)
			throws ApplicationException
	{
		HashMap<String, String> loHMap = new HashMap<String, String>();
		Channel loChannelObj = new Channel();
		loHMap.put(HHSConstants.PROVIDER_ID_KEY, asProviderId);
		loHMap.put("superSedingStatusKey", superSedingStatusKey);
		loChannelObj.setData(ApplicationConstants.BATCH_TRANSACTION_PARAM_NAME, loHMap);
		TransactionManager.executeTransaction(loChannelObj, "fetchSupersedingStatusCount");
		String count = (String) loChannelObj.getData("COUNT");
		LOG_OBJECT.Debug("Finished transaction fetchSupersedingStatusCount");
		int counter = Integer.parseInt(count);

		return counter;
	}

	private void deleteEventFromSupersedingStatusForProvider(String asProviderId, String superSedingStatusKey)
			throws ApplicationException
	{
		HashMap<String, String> loHMap = new HashMap<String, String>();
		Channel loChannelObj = new Channel();
		loHMap.put(HHSConstants.PROVIDER_ID_KEY, asProviderId);
		loHMap.put("superSedingStatusKey", superSedingStatusKey);
		loChannelObj.setData(ApplicationConstants.BATCH_TRANSACTION_PARAM_NAME, loHMap);
		TransactionManager.executeTransaction(loChannelObj, "deleteEventFromSupersedingStatusForProvider");
		LOG_OBJECT.Debug("Finished transaction deleteEventFromSupersedingStatusForProvider");

	}

	private void updateProviderStatusInOrgTable(String asProviderId, String asProviderStatus)
			throws ApplicationException
	{
		HashMap<String, HashMap<String, String>> aoHashMap = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> loHMap = new HashMap<String, String>();
		Channel loChannelObj = new Channel();
		loHMap.put(HHSConstants.PROVIDER_ID_KEY, asProviderId);
		loHMap.put(HHSConstants.PROVIDER_STATUS_PARAM, asProviderStatus);
		aoHashMap.put(ApplicationConstants.BATCH_TRANSACTION_PARAM_NAME, loHMap);
		// loChannelObj.setData("aoHashMap", aoHashMap);
		loChannelObj.setData(ApplicationConstants.BATCH_TRANSACTION_PARAM_NAME, loHMap);
		TransactionManager.executeTransaction(loChannelObj, "updateProviderStatus");
		LOG_OBJECT.Debug("Finished transaction updateProviderStatus");

	}

	// END QC 8667 Release 6.1.0

	/**
	 * This method fetch list of provider status regarding Business Application
	 * 
	 * @return list of provider status in java bean
	 * @throws ApplicationException
	 */
	public void reconcileProviderStatusBatch() throws ApplicationException
	{
		Channel loChannelObj = new Channel();

		LOG_OBJECT.Debug("Executing transaction reconcileProviderStatus");
		TransactionManager.executeTransaction(loChannelObj, HHSConstants.TRANSACTION_RECONCILE_PROVIDER_STATUS);

	}

	/**
	 * This method will delete records from SUSPERSEDING_STATUS when
	 * DOC_LAPSING_RULES_MASTER has DUE_DATE in future.
	 * 
	 * * @throws ApplicationException
	 */
	public void deleteErroneousFromSuperseding() throws ApplicationException
	{
		Channel loChannelObj = new Channel();

		// Before actually deleting rows, retrieve them and log them.
		LOG_OBJECT.Debug("Executing transaction fetchSupersedingStatusBeforeDeleteTx ");
		TransactionManager.executeTransaction(loChannelObj, "fetchSupersedingStatusBeforeDeleteTx");
		List<SupersedingStatusBean> supersedingStatusBeanList = (List<SupersedingStatusBean>) loChannelObj
				.getData("supersedingStatusDetails");
		LOG_OBJECT.Info("Batch will delete the following rows from Superseding_Status " + supersedingStatusBeanList);

		// Now delete the rows
		LOG_OBJECT.Debug("Executing transaction deleteErroneousFromSupersedingTx");
		TransactionManager.executeTransaction(loChannelObj, "deleteErroneousFromSupersedingTx");

	}

	public static void main(String args[]) throws ApplicationException
	{
		(new DocLapsingBatch()).executeQueue(null);

	}

} // end of class