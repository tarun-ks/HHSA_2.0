package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.ApplicationExpiryRuleBean;
import com.nyc.hhs.model.ApplicationIdBRStatusBean;
import com.nyc.hhs.model.ApplicationIdStatusBean;
import com.nyc.hhs.model.CityUserDetailsBeanForBatch;
import com.nyc.hhs.model.DocLapsingRuleBean;
import com.nyc.hhs.model.NotificationSettingsBean;
import com.nyc.hhs.model.PrintViewGenerationBean;
import com.nyc.hhs.model.ProviderExpiryRuleBean;
import com.nyc.hhs.model.ProviderStatusBean;
import com.nyc.hhs.model.SMAlertNotificationBean;
import com.nyc.hhs.model.SupersedingStatusBean;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.ProviderStatusBusinessRules;

/**
 * 
 * BatchNotificationService: This class is used to fetch all section entries,
 * notification settings, Application Expiry Rule, Provider Expiry Rule. Also it
 * selects and inserts and update the information in the database.
 */
public class BatchNotificationService extends ServiceState
{
	/**
	 * This is the llog object used to log errors into log file
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(BatchNotificationService.class);

	/**
	 * This method fetches all section entries from APPLICATION_AUDIT Table
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about due date and upload
	 *            order
	 * @return a list containing information about document lapsing master rules
	 * @throws ApplicationException
	 */

	@SuppressWarnings("unchecked")
	public List<DocLapsingRuleBean> fetchDocLapsingRule(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<DocLapsingRuleBean> loResultList = null;
		HashMap loHMap = (HashMap) aoHashMap.get("loHMap");
		loResultList = (List<DocLapsingRuleBean>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchDocLapsingRule",
				"java.util.Map");

		return loResultList;

	}

	/**
	 * This method is used for fetching all notification settings
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about component name
	 * @return a list containing information about notification settings
	 * @throws ApplicationException
	 */

	public List<NotificationSettingsBean> fetchNotificationSettings(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{

		List<NotificationSettingsBean> loResultList = null;

		loResultList = (List<NotificationSettingsBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchNotificationSettings",
				"java.util.Map");

		return loResultList;
	}

	/**
	 * This method is used for searching the table DUEDATE_REMINDER if a
	 * particular notification has been sent for a provider
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about notification name and
	 *            due date
	 * @return a string value of reminder count
	 * @throws ApplicationException
	 */

	public String fetchDueDateReminderCount(SqlSession aoMybatisSession, HashMap aoHashMap) throws ApplicationException
	{

		String lsCount = null;

		HashMap loHMap = (HashMap) aoHashMap.get("loHMap");

		lsCount = (String) DAOUtil.masterDAO(aoMybatisSession, loHMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchDueDateReminderCount",
				"java.util.Map");

		return lsCount;
	}

	/**
	 * This method is used for inserting into the table DUEDATE_REMINDER all the
	 * providers or whom notification has been sent
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing due date reminder information
	 * @return a boolean value of inser status
	 * @throws ApplicationException
	 */

	public boolean insertIntoDueDateReminder(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{

		boolean lbInsertStatus = false;
		LOG_OBJECT.Debug("aoHashMap" + aoHashMap);
		HashMap loHMap = (HashMap) aoHashMap.get("loHmNotifyParam");
		// Call to insert method
		LOG_OBJECT.Debug("loHMap" + loHMap);
		DAOUtil.masterDAO(aoMybatisSession, loHMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "insertIntoDueDateReminder",
				"java.util.Map");

		lbInsertStatus = true;
		return lbInsertStatus;
	}
	/**
	 * This method is used for inserting into the table DUEDATE_REMINDER all the
	 * providers or whom notification has been sent
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing due date reminder information
	 * @param asCount String
	 * @return a boolean value of inser status
	 * @throws ApplicationException
	 */
	public boolean insertIntoDueDateReminderNew(SqlSession aoMybatisSession, HashMap aoHashMap, String asCount)
			throws ApplicationException
	{
		boolean lbInsertStatus = false;
		if (null != asCount )
		{
			// Call to insert method
			DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "insertIntoDueDateReminder",
					"java.util.Map");

			lbInsertStatus = true;
		}
		return lbInsertStatus;
	}

	/**
	 * This method is used for setting the status of all providers to Expired
	 * whose expiry date has passed current date
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing provider expiry status
	 * @return an integer value of update status
	 * @throws ApplicationException
	 */

	public int updateProviderExpiryStatus(SqlSession aoMybatisSession, HashMap aoHashMap) throws ApplicationException
	{
		HashMap loHMap = (HashMap) aoHashMap.get("loHMap");

		// Call to insert method

		int liUpdateCount = 0;

		liUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "updateProviderExpiryStatus",
				"java.util.Map");

		return liUpdateCount;
	}

	/**
	 * This method fetches the Provider Expiry Rule from the database.
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about expiration date and
	 *            organization status
	 * @return a list of provider expiry rules
	 * @throws ApplicationException
	 */
	public List<ProviderExpiryRuleBean> fetchProviderExpiryRule(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<ProviderExpiryRuleBean> loResultList = null;

		HashMap loHMap = (HashMap) aoHashMap.get("loHMap");

		loResultList = (List<ProviderExpiryRuleBean>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchProviderExpiryRule",
				"java.util.Map");

		return loResultList;
	}

	/**
	 * This method is used for updating the status of a provider
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	public int updateProviderStatus(SqlSession aoMybatisSession, HashMap aoHashMap) throws ApplicationException
	{
		HashMap loHMap = (HashMap) aoHashMap.get("loHMap");
		// Call to insert method
		int liUpdateCount = 0;
		liUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "updateProviderStatus",
				"java.util.Map");
		LOG_OBJECT.Info("---> Provider Status updated to:: " + loHMap.get("asProviderStatus") + " ----> ProviderId:: " + loHMap.get("asProviderId"));
		return liUpdateCount;
	}

	/**
	 * This method is used for updating the status of a provider
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	public int updateProviderStatusNew(SqlSession aoMybatisSession, HashMap aoHashMap) throws ApplicationException
	{
		// Call to insert method
		HashMap loHMap = (HashMap) aoHashMap.get("loHmNotifyParam");
		int liUpdateCount = 0;
		liUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "updateProviderStatus",
				"java.util.Map");
		return liUpdateCount;
	}

	/**
	 * This method is used for updating the status of a provider
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	public int updateProviderStatusExpiredBusinessApp(SqlSession aoMybatisSession, Map aoHashMap)
			throws ApplicationException
	{
		// Call to insert method
		int liUpdateCount = 0;
		liUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "updateProviderStatus",
				"java.util.Map");
			
		LOG_OBJECT.Info("---> Provider Status was updated to:: " + aoHashMap.get("asProviderStatus") + " ----> ProviderId:: " + aoHashMap.get("ProviderId") + " Business ApplicationId:: " + aoHashMap.get("ApplicationId"));
			
		return liUpdateCount;
	}

	/**
	 * This method updates the SuperSeding_Status table in the database
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about entity id and
	 *            organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */
	public int updateSuperSeedingStatus(SqlSession aoMybatisSession, HashMap aoHashMap) throws ApplicationException
	{
		HashMap loHMRequestmap = (HashMap) aoHashMap.get("loHMap");

		// Call to insert method

		int liUpdateCount = 0;

		liUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHMRequestmap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "updateSuperSeedingStatus",
				"java.util.Map");

		return liUpdateCount;
	}

	/**
	 * This method updates the SuperSeding_Status table in the database
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about entity id and
	 *            organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */
	public int updateSuperSeedingStatusForSuspendEfilling(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		// Call to insert method
		int liUpdate = 0;
		liUpdate = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "updateSuperSeedingStatus",
				"java.util.Map");
		return liUpdate;
	}

	/**
	 * This method is used for searching the table BR application for the latest
	 * version of a BR applicaton id
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about notification name and
	 *            due date
	 * @return a string value of reminder count
	 * @throws ApplicationException
	 */

	public String fetchLatestBRAppIdforProvider(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{

		String lsEntityId = null;

		HashMap loHMap = (HashMap) aoHashMap.get("loHMap");

		lsEntityId = (String) DAOUtil.masterDAO(aoMybatisSession, loHMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchLatestBRAppIdforProvider",
				"java.util.Map");

		return lsEntityId;
	}

	/**
	 * This method is used for searching the table BR application for the latest
	 * version of a BR applicaton id
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about notification name and
	 *            due date
	 * @return a string value of reminder count
	 * @throws ApplicationException
	 */

	public String fetchLatestBRAppIdforProviderNew(SqlSession aoMybatisSession, Map aoHashMap)
			throws ApplicationException
	{

		String lsEntityId = (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchLatestBRAppIdforProvider",
				"java.util.Map");

		return lsEntityId;
	}

	/**
	 * This method updates the SuperSeding_Status table in the database
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about entity id and
	 *            organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */
	public boolean insertIntoSuperSeeding(SqlSession aoMybatisSession, Map aoHashMap, String asCount)
			throws ApplicationException
	{

		boolean lbInsertStatus = false;
		LOG_OBJECT.Debug("aoHashMap" + aoHashMap);

		if (null != asCount && Integer.valueOf(asCount) == 0)
		{
			HashMap loHMap = (HashMap) aoHashMap.get("loHMap");

			// Call to insert method
			LOG_OBJECT.Debug("loHMap" + loHMap);
			DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "insertIntoSuperSeeding",
					"java.util.Map");

			lbInsertStatus = true;
		}
		return lbInsertStatus;
	}

	/**
	 * This method fetches the Application Expiry Rule from the database.
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about sysdate
	 * @return a list of application expiry rules
	 * @throws ApplicationException
	 */

	public List<ApplicationExpiryRuleBean> fetchApplicationExpiryRule(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{

		List<ApplicationExpiryRuleBean> loResultList = null;

		HashMap loHMap = (HashMap) aoHashMap.get("loHMap");

		loResultList = (List<ApplicationExpiryRuleBean>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchApplicationExpiryRule",
				"java.util.Map");

		return loResultList;

	}

	/**
	 * This method fetches the Applications from the BusinessApplication table
	 * whose status is draft and start_date is past 90 days from current date
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about sysdate
	 * @return a list of application expiry rules
	 * @throws ApplicationException
	 */

	public List<ApplicationExpiryRuleBean> fetchApplicationExpiryForDraft(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{

		List<ApplicationExpiryRuleBean> loResultList = null;

		HashMap loHMap = (HashMap) aoHashMap.get("loHMap");

		loResultList = (List<ApplicationExpiryRuleBean>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper",
				"fetchApplicationExpiryForDraft", "java.util.Map");

		return loResultList;

	}

	/**
	 * This method is used for updating the status of a provider
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	public int updateBRApplicationExpiryStatus(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		HashMap loMap = (HashMap) aoHashMap.get("loHMap");

		// Call to insert method

		int liCount = 0;

		liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper",
				"updateBRApplicationExpiryStatus", "java.util.Map");

		return liCount;
	}

	/**
	 * This method is used for updating the status of a provider
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	public int updateServiceApplicationExpiryStatus(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		HashMap loHMRequest = (HashMap) aoHashMap.get("loHMap");

		// Call to insert method

		int liUpdate = 0;

		liUpdate = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHMRequest,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper",
				"updateServiceApplicationExpiryStatus", "java.util.Map");

		return liUpdate;
	}

	/**
	 * This method is used for updating the status of a provider
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */

	public int deleteConditionallyApprovedfromSuperseding(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		// Call to insert method
		int liCount = 0;
		liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper",
				"deleteConditionallyApprovedfromSuperseding", "java.util.Map");

		return liCount;
	}

	/**
	 * This method is used for select providers from superseeding table whose
	 * status is 'conditioanlly approved' and BR has expired
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<HashMap<String, String>> selectConditionallyApprovedProvidersfromSuperseding(
			SqlSession aoMybatisSession, HashMap aoHashMap) throws ApplicationException
	{
		List<HashMap<String, String>> loSupersedingProviderMapList = null;
		try
		{
			HashMap loHMap = (HashMap) aoHashMap.get("loHMap");
			loSupersedingProviderMapList = (List<HashMap<String, String>>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
					"com.nyc.hhs.service.db.services.application.BatchNotificationMapper",
					"selectConditionallyApprovedProvidersfromSuperseding", "java.util.Map");
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error while Fetching Superseding provider details ", aoAppExp);
			setMoState("Error while Fetching Superseding provider details ");
			throw aoAppExp;
		}
		/**
		 * catch the exception thrown from the DAO layer log it into the console
		 * and wrap it into application exception
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while Fetching Superseding provider details ", aoExp);
			setMoState("Error while Fetching Superseding provider details ");
			throw new ApplicationException("Error while Fetching Superseding provider details ", aoExp);
		}
		return loSupersedingProviderMapList;

	}

	/**
	 * This method is used for selecting all SC applications for a BR
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	public List<String> selectSCIDsForBR(SqlSession aoMybatisSession, Map aoHashMap, String asCount)
			throws ApplicationException
	{
		List<String> loSCIDList = null;
		if (null != asCount && Integer.valueOf(asCount) == 0)
		{
			loSCIDList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "selectSCIDsForBR",
					"java.util.Map");
		}
		return loSCIDList;
	}

	/**
	 * This method is used for searching the Business_Application table the no.
	 * of applications for the same provider
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about notification name and
	 *            due date
	 * @return a string value of reminder count
	 * @throws ApplicationException
	 */
	public String fetchDuplicateProviderApplicationCount(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{

		String lsCount = null;
		lsCount = "0"; // already fetched duplicate records .. transaction no
						// longer required
		return lsCount;
	}

	/**
	 * This method is used for searching the Business_Application table the no.
	 * of applications for the same provider
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about notification name and
	 *            due date
	 * @return a string value of reminder count
	 * @throws ApplicationException
	 */
	public String fetchProviderApplicationCount(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		HashMap loHMap = (HashMap) aoHashMap.get("loHMap");
		String lsCount = null;
		lsCount = (String) DAOUtil.masterDAO(aoMybatisSession, loHMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper",
				"fetchDuplicateProviderApplicationCount", "java.util.Map");
		return lsCount;
	}

	/**
	 * This method is used for searching the table BR application for the latest
	 * version of a BR applicaton id
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about notification name and
	 *            due date
	 * @return a string value of reminder count
	 * @throws ApplicationException
	 */

	public String fetchBusinessApplicationStatus(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{

		String lsBRAppStatus = null;

		HashMap loHMap = (HashMap) aoHashMap.get("loHMap");

		lsBRAppStatus = (String) DAOUtil.masterDAO(aoMybatisSession, loHMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper",
				"fetchBusinessApplicationStatus", "java.util.Map");

		return lsBRAppStatus;
	}
	/**
	 * This method is used for searching the table BR application for the latest
	 * version of a BR applicaton id
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about notification name and
	 *            due date
	 * @return a string value of reminder count
	 * @throws ApplicationException
	 */
	public String fetchBusinessApplicationStatusNew(SqlSession aoMybatisSession, Map aoHashMap)
			throws ApplicationException
	{

		String lsBRAppStatus = null;
		lsBRAppStatus = (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper",
				"fetchBusinessApplicationStatus", "java.util.Map");

		return lsBRAppStatus;
	}

	/**
	 * This method fetches all entries from PRINT_VIEW_GENERATION Table
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about PrintViewId,
	 *            ProviderId and TaskType
	 * @return a list containing information about document lapsing master rules
	 * @throws ApplicationException
	 */

	@SuppressWarnings("unchecked")
	public List<PrintViewGenerationBean> fetchPrintViewGeneration(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<PrintViewGenerationBean> loResultList = null;
		HashMap loHMap = (HashMap) aoHashMap.get("loHMap");
		loResultList = (List<PrintViewGenerationBean>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchPrintViewGeneration",
				"java.util.Map");

		return loResultList;

	}

	/**
	 * This method updates the SuperSeding_Status table in the database
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about entity id and
	 *            organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */
	public int updatePrintViewGenerated(SqlSession aoMybatisSession, HashMap aoHashMap) throws ApplicationException
	{
		HashMap loChannelMap = (HashMap) aoHashMap.get("loHMap");

		// Call to update method

		int liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loChannelMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "updatePrintViewGenerated",
				"java.util.Map");

		return liCount;
	}

	/**
	 * This method is used for setting the expiry date and CITY_STATUS_SET_BY to
	 * null for a given application id
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */

	public int setExpiryDatetoNull(SqlSession aoMybatisSession, HashMap aoHashMap) throws ApplicationException
	{
		// Call to insert method
		int liCount = 0;
		aoHashMap.put(ApplicationConstants.SUPER_SEDING_KEY_DRAFT, ApplicationConstants.APP_STATUS_DRAFT);
		liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "setExpiryDatetoNull",
				"java.util.HashMap");
		return liCount;
	}

	/**
	 * This method is used for setting the expiry date and CITY_STATUS_SET_BY to
	 * null for a given application id
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public int setExpiryDatetoNullForSC(SqlSession aoMybatisSession, HashMap aoHashMap) throws ApplicationException
	{
		// Call to insert method
		int liUpCount = 0;
		aoHashMap.put(ApplicationConstants.SUPER_SEDING_KEY_DRAFT, ApplicationConstants.APP_STATUS_DRAFT);
		liUpCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "setExpiryDatetoNullForSC",
				"java.util.HashMap");

		return liUpCount;
	}

	/**
	 * This method fetches the Application Id and status from the database for
	 * applications which are not latest.
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about sysdate
	 * @return a list of application expiry rules
	 * @throws ApplicationException
	 */

	public List<ApplicationIdStatusBean> fetchApplicationIdStatus(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{

		List<ApplicationIdStatusBean> loResultList = null;

		HashMap loHMap = (HashMap) aoHashMap.get("loHMap");

		loResultList = (List<ApplicationIdStatusBean>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchApplicationIdStatus",
				"java.util.Map");

		return loResultList;

	}

	/**
	 * 
	 * @param aoMybatisSession
	 * @param aoOrgId
	 * @return provider name
	 * @throws ApplicationException
	 */
	/**
	 * This methods returns the provider name for a particular provider id
	 */
	public String fetchProviderNameBatch(SqlSession aoMybatisSession, HashMap aoOrgId) throws ApplicationException
	{

		HashMap loOrgMap = (HashMap) aoOrgId.get("loHMap");
		String lsOrgId = (String) loOrgMap.get("OrgID");
		String lsProviderName = (String) DAOUtil.masterDAO(aoMybatisSession, lsOrgId,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchProviderNameBatch",
				"java.lang.String");
		return lsProviderName;
	}

	/**
	 * 
	 * @param aoMybatisSession
	 * @param aoOrgId
	 * @return provider name
	 * @throws ApplicationException
	 */
	/**
	 * This methods returns the provider name for a particular provider id
	 */
	public String fetchAgencyNameBatch(SqlSession aoMybatisSession, HashMap aoAgencyIdMap) throws ApplicationException
	{

		HashMap loHMap = (HashMap) aoAgencyIdMap.get("loHMap");
		String lsAgencyId = (String) loHMap.get("asAgencyId");
		String lsAgencyName = (String) DAOUtil.masterDAO(aoMybatisSession, lsAgencyId,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchAgencyNameBatch",
				"java.lang.String");
		return lsAgencyName;
	}

	/**
	 * This method is used to get the list of procurements for which proposal
	 * due date is nearing
	 * <ul>
	 * <li>Get the parameter map from the channel object and get the details
	 * from the map</li>
	 * <li>execute query <code>fetchProposalDueDateAlertDetails</code> to fetch
	 * the procurement list</li>
	 * <li>Return list of alert eban which will be used to send notification</li>
	 * </ul>
	 * @param aoMybatisSession sql session object
	 * @param aoHashMap parameters map
	 * @return List of notification bean
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<SMAlertNotificationBean> fetchProposalDueDateAlertDetails(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<SMAlertNotificationBean> loResultList = null;
		try
		{
			HashMap loHMap = (HashMap) aoHashMap.get(ApplicationConstants.BATCH_TRANSACTION_PARAM_NAME);
			loResultList = (List<SMAlertNotificationBean>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
					ApplicationConstants.BATCH_NOTIFICATION_MAPPER_CLASS,
					ApplicationConstants.FETCH_PROPOSAL_DUE_DATE_ALERT_DETAILS, ApplicationConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error while Fetching Proposal due date details", aoAppExp);
			setMoState("Error while Fetching Proposal due date details");
			throw aoAppExp;
		}
		/**
		 * catch the exception thrown from the DAO layer log it into the console
		 * and wrap it into application exception
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while Fetching Proposal due date details", aoExp);
			setMoState("Error while Fetching Proposal due date details");
			throw new ApplicationException("Error while Fetching Proposal due date details", aoExp);
		}
		return loResultList;

	}

	/**
	 * This method is used to fetch the approved providers for the procurement
	 * <ul>
	 * <li>Get the request parameter map</li>
	 * <li>Execute transaction with id <code>fetchApprovedProvidersList</code></li>
	 * </ul>
	 * @param aoMybatisSession sql session object
	 * @param aoParamMap parameter map
	 * @return list of approved providers
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<String> fetchApprovedProvidersList(SqlSession aoMybatisSession, HashMap aoParamMap)
			throws ApplicationException
	{
		List<String> loResultList = null;
		try
		{
			HashMap loHMap = (HashMap) aoParamMap.get(ApplicationConstants.BATCH_TRANSACTION_PARAM_NAME);
			loResultList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
					ApplicationConstants.BATCH_NOTIFICATION_MAPPER_CLASS,
					ApplicationConstants.FETCH_APPROVED_PROVIDERS_LIST, ApplicationConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error while Fetching Approved Provider List", aoAppExp);
			setMoState("Error while Fetching Approved Provider List");
			throw aoAppExp;
		}
		/**
		 * catch the exception thrown from the DAO layer log it into the console
		 * and wrap it into application exception
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while Fetching Approved Provider List", aoExp);
			setMoState("Error while Fetching Approved Provider List");
			throw new ApplicationException("Error while Fetching Approved Provider List", aoExp);
		}
		return loResultList;

	}

	/**
	 * This method is used to get the list of procurements for which RFP release
	 * due date is nearing
	 * <ul>
	 * <li>Get the parameter map from the channel object and get the details
	 * from the map</li>
	 * <li>execute query <code>fetchRfpReleaseDueDateAlertDetails</code> to
	 * fetch the procurement list</li>
	 * <li>Return list of alert bean which will be used to send notification</li>
	 * </ul>
	 * @param aoMybatisSession sql session object
	 * @param aoHashMap parameters map
	 * @return List of notification bean
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<SMAlertNotificationBean> fetchRfpReleaseDueDateAlertDetails(SqlSession aoMybatisSession,
			HashMap aoHashMap) throws ApplicationException
	{
		List<SMAlertNotificationBean> loResultList = null;
		try
		{
			HashMap loHMap = (HashMap) aoHashMap.get(ApplicationConstants.BATCH_TRANSACTION_PARAM_NAME);
			loResultList = (List<SMAlertNotificationBean>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
					ApplicationConstants.BATCH_NOTIFICATION_MAPPER_CLASS,
					ApplicationConstants.FETCH_RFP_RELEASE_DUE_DATE_ALET_DETAILS, ApplicationConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error while Fetching RFP Release due date details", aoAppExp);
			setMoState("Error while Fetching RFP Release due date details");
			throw aoAppExp;
		}
		/**
		 * catch the exception thrown from the DAO layer log it into the console
		 * and wrap it into application exception
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while Fetching RFP Release due date details", aoExp);
			setMoState("Error while Fetching RFP Release due date details");
			throw new ApplicationException("Error while Fetching RFP Release due date details", aoExp);
		}
		return loResultList;

	}

	/**
	 * This method is used to get the list of procurements for which first round
	 * evaluation date is nearing
	 * <ul>
	 * <li>Get the parameter map from the channel object and get the details
	 * from the map</li>
	 * <li>execute query
	 * <code>fetchFirstRoundEvaluationDueDateAlertDetails</code> to fetch the
	 * procurement list</li>
	 * <li>Return list of alert bean which will be used to send notification</li>
	 * </ul>
	 * @param aoMybatisSession sql session object
	 * @param aoHashMap parameters map
	 * @return List of notification bean
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<SMAlertNotificationBean> fetchFirstRoundEvaluationDueDateAlertDetails(SqlSession aoMybatisSession,
			HashMap aoHashMap) throws ApplicationException
	{
		List<SMAlertNotificationBean> loResultList = null;
		try
		{

			HashMap loHMap = (HashMap) aoHashMap.get(ApplicationConstants.BATCH_TRANSACTION_PARAM_NAME);
			loResultList = (List<SMAlertNotificationBean>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
					ApplicationConstants.BATCH_NOTIFICATION_MAPPER_CLASS,
					ApplicationConstants.FETCH_FIRST_ROUND_EVALUATION_DUE_DATE_ALET_DETAILS,
					ApplicationConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error while Fetching FirstRound evaluation due date details", aoAppExp);
			setMoState("Error while Fetching FirstRound evaluation due date details");
			throw aoAppExp;
		}
		/**
		 * catch the exception thrown from the DAO layer log it into the console
		 * and wrap it into application exception
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while Fetching FirstRound evaluation due date details", aoExp);
			setMoState("Error while Fetching FirstRound evaluation due date details");
			throw new ApplicationException("Error while Fetching FirstRound evaluation due date details", aoExp);
		}

		return loResultList;

	}

	/**
	 * This method is used to get the list of procurements for which final round
	 * evaluation date is nearing
	 * <ul>
	 * <li>Get the parameter map from the channel object and get the details
	 * from the map</li>
	 * <li>execute query <code>fetchFinalEvaluationDueDateAlertDetails</code> to
	 * fetch the procurement list</li>
	 * <li>Return list of alert bean which will be used to send notification</li>
	 * </ul>
	 * @param aoMybatisSession sql session object
	 * @param aoHashMap parameters map
	 * @return List of notification bean
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<SMAlertNotificationBean> fetchFinalEvaluationDueDateAlertDetails(SqlSession aoMybatisSession,
			HashMap aoHashMap) throws ApplicationException
	{
		List<SMAlertNotificationBean> loResultList = null;
		try
		{
			HashMap loHMap = (HashMap) aoHashMap.get(ApplicationConstants.BATCH_TRANSACTION_PARAM_NAME);
			loResultList = (List<SMAlertNotificationBean>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
					ApplicationConstants.BATCH_NOTIFICATION_MAPPER_CLASS,
					ApplicationConstants.FETCH_FINAL_ROUND_EVALUATION_DUE_DATE_ALET_DETAILS,
					ApplicationConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error while Fetching final evaluation due date details", aoAppExp);
			setMoState("Error while Fetching final evaluation due date details");
			throw aoAppExp;
		}
		/**
		 * catch the exception thrown from the DAO layer log it into the console
		 * and wrap it into application exception
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while Fetching final evaluation due date details", aoExp);
			setMoState("Error while Fetching final evaluation due date details");
			throw new ApplicationException("Error while Fetching final evaluation due date details", aoExp);
		}
		return loResultList;

	}

	/**
	 * This method is used to get all the external and internal evaluator for
	 * the proposal
	 * <ul>
	 * <li>Get the procurement id from the request parameter</li>
	 * <li>Execute the query <code>fetchExtAndIntEvaluator</code> from
	 * <code>evaluation</code> mapper</li>
	 * <li>return the list of all evaluators configured for the procurement</li>
	 * </ul>
	 * @param aoMybatisSession sql session object
	 * @param aoParameterMap hash map with all required parameters
	 * @return list of evaluator
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap<String, List<String>> fetchExtAndIntEvaluator(SqlSession aoMybatisSession, HashMap aoParameterMap)
			throws ApplicationException
	{
		List<HashMap<String, String>> loEvaluatorsList = null;
		List<String> loEvaluatorEmailList = new ArrayList<String>();
		List<String> loEvaluatorUserIdList = new ArrayList<String>();
		HashMap<String, String> loInnerHashmap = null;
		String lsEvaluatorUserId = null;
		String lsEvaluatorEmailId = null;
		String lsProcurementId = null;
		HashMap<String, List<String>> loExternalInternalEvalutorDetailMap = new HashMap<String, List<String>>();
		try
		{
			lsProcurementId = (String) ((HashMap) aoParameterMap.get(ApplicationConstants.BATCH_TRANSACTION_PARAM_NAME))
					.get(ApplicationConstants.PROCUREMENT_ID);

			loEvaluatorsList = (List<HashMap<String, String>>) DAOUtil.masterDAO(aoMybatisSession, lsProcurementId,
					ApplicationConstants.MAPPER_CLASS_EVALUATION_MAPPER,
					ApplicationConstants.FETCH_EXT_AND_INT_EVALUATOR, ApplicationConstants.JAVA_LANG_STRING);
			if (null != loEvaluatorsList && !loEvaluatorsList.isEmpty())
			{
				for (Iterator<HashMap<String, String>> loEvaluatorItr = loEvaluatorsList.iterator(); loEvaluatorItr
						.hasNext();)
				{
					loInnerHashmap = loEvaluatorItr.next();
					lsEvaluatorUserId = loInnerHashmap.get(HHSConstants.USER_ID.toUpperCase());
					lsEvaluatorEmailId = loInnerHashmap.get(ApplicationConstants.KEY_SESSION_EMAIL_ID.toUpperCase());
					loEvaluatorUserIdList.add(lsEvaluatorUserId);
					loEvaluatorEmailList.add(lsEvaluatorEmailId);
				}
			}
			loExternalInternalEvalutorDetailMap.put(HHSConstants.USER_ID.toUpperCase(), loEvaluatorUserIdList);
			loExternalInternalEvalutorDetailMap.put(ApplicationConstants.KEY_SESSION_EMAIL_ID.toUpperCase(),
					loEvaluatorEmailList);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppExp)
		{
			setMoState("Error while lFetching Evaluators List");
			HashMap<String, Object> aoHMContextData = new HashMap<String, Object>();
			aoHMContextData.put(ApplicationConstants.PROCUREMENT_ID_KEY, lsProcurementId);
			aoAppExp.setContextData(aoHMContextData);
			LOG_OBJECT.Error("Error while lFetching Evaluators List", aoAppExp);
			throw aoAppExp;
		}
		/**
		 * catch the exception thrown from the DAO layer log it into the console
		 * and wrap it into application exception
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while lFetching Evaluators List", aoExp);
			setMoState("Error while lFetching Evaluators List");
			throw new ApplicationException("Error while launching workflow :", aoExp);
		}
		return loExternalInternalEvalutorDetailMap;
	}

	/**
	 * This method is used filter the notification for which notification has
	 * been already sent
	 * <ul>
	 * <li>Get the notification list object from parameter map</li>
	 * <li>If the notification list object is of type List then execute
	 * <code>filterNotificationListForList</code></li>
	 * <li>If the notification list object is of type String then execute
	 * <code>filterNotificationListForString</code></li>
	 * <li>Return the list of the notification found in tables</li>
	 * </ul>
	 * @param aoMybatisSession sql session object
	 * @param aoHashMap parameter map
	 * @return list of notifications
	 * @throws ApplicationException if any exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<HashMap<String, String>> filterNotificationList(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<HashMap<String, String>> loResultList = null;
		HashMap loHMap = (HashMap) aoHashMap.get(ApplicationConstants.BATCH_TRANSACTION_PARAM_NAME);
		Object loParamObj = loHMap.get(ApplicationConstants.NOTIFICATION_LIST_KEY);
		try
		{
			if (loParamObj instanceof List)
			{
				loResultList = (List<HashMap<String, String>>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
						ApplicationConstants.BATCH_NOTIFICATION_MAPPER_CLASS,
						ApplicationConstants.FILTER_NOTIFICATION_FOR_LIST, HHSConstants.JAVA_UTIL_MAP);

			}
			else if (loParamObj instanceof String)
			{
				loResultList = (List<HashMap<String, String>>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
						ApplicationConstants.BATCH_NOTIFICATION_MAPPER_CLASS,
						ApplicationConstants.FILTER_NOTIFICATION_FOR_STRING, ApplicationConstants.JAVA_UTIL_MAP);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error occurred while filtering notification list", aoAppExp);
			setMoState("Error occurred while filtering notification list");
			throw aoAppExp;
		}
		/**
		 * catch the exception thrown from the DAO layer log it into the console
		 * and wrap it into application exception
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error occurred while filtering notification list", aoExp);
			setMoState("Error occurred while filtering notification list");
			throw new ApplicationException("Error occurred while filtering notification list", aoExp);
		}
		return loResultList;
	}

	/**
	 * This method is used for setting the expiry date and CITY_STATUS_SET_BY to
	 * null for a given application id *
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	public int setExpiryDatetoNullForExpiredSA(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		// Call to insert method

		int liUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper",
				"setExpiryDatetoNullForExpiredSA", "java.util.Map");

		return liUpdateCount;
	}

	/**
	 * This method is used for updating the status of a provider
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	public int deleteConditionallyApprovedExpiredServiceApp(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		aoHashMap.get("loHMap");

		// Call to insert method

		int liRowUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper",
				"deleteConditionallyApprovedExpiredServiceApp", "java.util.Map");

		return liRowUpdateCount;
	}
	/**
	 * This method is used for updating the status of a provider
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @param aoRequiredProps HashMap<String, String>
	 * @param aoProviderStatusMap HashMap<String, String>
	 * @param aoProviderStatusBeanList List<ProviderStatusBean>
	 * @return HashMap<String, Object> loProviderNewStatusMap
	 * @throws ApplicationException
	 */
	public HashMap<String, Object> fetchProvidersNewStatus(SqlSession aoMybatisSession,
			HashMap<String, String> aoRequiredProps, HashMap<String, String> aoProviderStatusMap,
			List<ProviderStatusBean> aoProviderStatusBeanList) throws ApplicationException
	{
		String lsBrStatus = null;
		String lsBRAppId = null;
		String lsNewProviderStatus = null;
		ProviderStatusBean loProviderStatusBean = null;
		String lsProviderId = null;
		HashMap<String, Object> loProviderNewStatusMap = new HashMap<String, Object>();
		try
		{
			Iterator loIterator = aoProviderStatusBeanList.iterator();
			List<String> loLServiceApplicationStatuses = new ArrayList<String>();
			lsBRAppId = aoProviderStatusMap.get("BUSINESS_APPLICATION_ID");
			lsProviderId = aoRequiredProps.get("providerId");
			while (loIterator.hasNext())
			{
				loProviderStatusBean = (ProviderStatusBean) loIterator.next();
				if (loProviderStatusBean.getApplicationId().equalsIgnoreCase(lsBRAppId))
				{
					if (loProviderStatusBean.getSupersedingStatus() != null)
					{
						lsBrStatus = loProviderStatusBean.getSupersedingStatus();
					}
					else
					{
						lsBrStatus = loProviderStatusBean.getApplicationStatus();
					}
					break;
				}
			}
			loIterator = aoProviderStatusBeanList.iterator();
			while (loIterator.hasNext())
			{
				loProviderStatusBean = (ProviderStatusBean) loIterator.next();
				if (!loProviderStatusBean.getApplicationId().equalsIgnoreCase(lsBRAppId))
				{
					if (loProviderStatusBean.getSupersedingStatus() == null)
					{
						loLServiceApplicationStatuses.add(loProviderStatusBean.getApplicationStatus());
					}
					else
					{
						loLServiceApplicationStatuses.add(loProviderStatusBean.getSupersedingStatus());
					}
				}
			}
			// got the calculated provider status
			lsNewProviderStatus = ProviderStatusBusinessRules.getProviderStatusOnRemovalOfConditionalApproval(
					lsBrStatus, loLServiceApplicationStatuses);
			loProviderNewStatusMap.put("orgStatus", lsNewProviderStatus);
			loProviderNewStatusMap.put("providerId", lsProviderId);
			loProviderNewStatusMap.put("statusChangeDate", new Date());
			loProviderNewStatusMap.put("modifiedBy", "System");
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error while Fetching and updating BR and Provider details ", aoAppExp);
			setMoState("Error while Fetching and updating BR and Provider details ");
			throw aoAppExp;
		}
		/**
		 * catch the exception thrown from the DAO layer log it into the console
		 * and wrap it into application exception
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while Fetching and updating BR and Provider details ", aoExp);
			setMoState("Error while Fetching and updating BR and Provider details ");
			throw new ApplicationException("Error while Fetching and updating BR and Provider details", aoExp);
		}
		return loProviderNewStatusMap;
	}
	/**
	 * This method is used for updating Provider Status Depending Upon Count
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @param asCount String
	 * @return HashMap aoHashMap
	 * @throws ApplicationException
	 */
	public Map updateProviderStatusDependingUponCount(SqlSession aoMybatisSession, String asCount, Map aoHashMap)
			throws ApplicationException
	{
		Integer liDuplicateCount = 0;
		String lsLatestBRAppId = null;
		String lsNewProviderStatus = null;
		HashMap loRequiredProps = new HashMap();
		List<ProviderStatusBean> loProviderStatusBeanList = null;
		ProviderStatusBean loProviderStatusBean = null;
		List loLServiceApplicationStatuses = new ArrayList();
		try
		{
			if (null != asCount)
			{
				liDuplicateCount = Integer.valueOf(asCount);
			}
			// There are no other latest applications for the same
			// provider
			// 1. insert into superseeding_status table an entry for the
			// BR application with the status 'expired'
			// 2. fetch all SC applications for the BR and insert into
			// superseeding_status table
			// an entry for the all the SC application with the status
			// 'expired'
			// 3. update status of the provider to 'expired' in
			// organization table
			if (liDuplicateCount == 0)
			{
				/* fix for QC 8515 Release 6.1.0 update Provider with EXP status regardles counter */
				//updateProviderStatusExpiredBusinessApp(aoMybatisSession, aoHashMap);
			}
			// There exists other applications for the same provider
			// 1. Fetch the status of the latest BR application for the
			// same provider
			// 2. Call the method for calculating the status for the
			// provider
			// 3. Update the status of the provider to that status

			// Fetch the latest BR id for the same provider
			else if (liDuplicateCount > 0)
			{
				lsLatestBRAppId = fetchLatestBRAppIdforProviderNew(aoMybatisSession, aoHashMap);
				if (null != lsLatestBRAppId && !lsLatestBRAppId.isEmpty())
				{
					aoHashMap.put("asBussAppId", lsLatestBRAppId);
					String lsBrStatus = fetchBusinessApplicationStatusNew(aoMybatisSession, aoHashMap);
					//QC 8515 Release 6.1.0  - target only Expired status
					if (lsBrStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DRAFT) 
						//BEGIN - fix for QC 8515 Release 6.1.0	
						|| lsBrStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW)
						|| lsBrStatus.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED)
						|| lsBrStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)
						|| lsBrStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED	))
						//END - fix for QC 8515 Release 6.1.0	
					{
						lsNewProviderStatus = (String) aoHashMap.get("lsProviderExpiryStatus");
						LOG_OBJECT.Info("New ProviderStatus : " + lsNewProviderStatus);
					}
					//QC 8515 Release 6.1.0  - target only Suspended/Withdrawn status
					//BEGIN - fix for QC 8515 Release 6.1.0	
					else if (lsBrStatus.equalsIgnoreCase(ApplicationConstants.STATUS_SUSPEND))
					{   
						aoHashMap.put("asProviderStatus", ApplicationConstants.STATUS_SUSPEND);
					}	
					else if (lsBrStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN))
					{   
						aoHashMap.put("asProviderStatus", ApplicationConstants.STATUS_WITHDRAWN);
						
						/* 
						aoHashMap.put("asProviderStatus", lsNewProviderStatus);
						
						loRequiredProps.put("providerId", aoHashMap.get("asProviderId"));
						loRequiredProps.put("businessAppId", lsLatestBRAppId);
						loProviderStatusBeanList = new SectionService().getBusinessAndServiceStatus(loRequiredProps,
								aoMybatisSession);
						Iterator loIterator = loProviderStatusBeanList.iterator();
						while (loIterator.hasNext())
						{
							loProviderStatusBean = (ProviderStatusBean) loIterator.next();
							loLServiceApplicationStatuses.add(loProviderStatusBean.getApplicationStatus());
						}
						
						lsNewProviderStatus = ProviderStatusBusinessRules.getProviderStatusOnBRWithdrawalRejection(
								lsBrStatus, loLServiceApplicationStatuses);
						// fix for QC 8515 Release 6.1.0					
						if(lsNewProviderStatus != null && lsNewProviderStatus.equals("") )
						{
							aoHashMap.put("asProviderStatus", lsNewProviderStatus);
						}	
						*/
					}
					
				}
				
			}
			
			/* fix for QC 8515 Release 6.1.0 update Provider Status regardles counter */			
			updateProviderStatusExpiredBusinessApp(aoMybatisSession, aoHashMap);
			//END - fix for QC 8515 Release 6.1.0
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error while Fetching and updating BR and Provider details ", aoAppExp);
			setMoState("Error while Fetching and updating BR and Provider details ");
			throw aoAppExp;
		}
		/**
		 * catch the exception thrown from the DAO layer log it into the console
		 * and wrap it into application exception
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while Fetching and updating BR and Provider details ", aoExp);
			setMoState("Error while Fetching and updating BR and Provider details ");
			throw new ApplicationException("Error while Fetching and updating BR and Provider details", aoExp);
		}
		
		return aoHashMap;
	}
	/**
	 * This method is used to fetch And Insert SCID In Superseding
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @param asCount String
	 * @param aoSCIdList List<String>
	 * @throws ApplicationException
	 */
	public void fetchAndInsertSCIDInSuperseding(SqlSession aoMybatisSession, List<String> aoSCIdList, Map aoHashMap,
			String asCount) throws ApplicationException
	{
		try
		{
			if (null != asCount && Integer.valueOf(asCount) == 0)
			{
				Iterator loIt = aoSCIdList.iterator();
				while (loIt.hasNext())
				{
					String lsSCId = (String) loIt.next();
					aoHashMap.put("lsEntityId", lsSCId);
					aoHashMap.put("lsEntityType", ApplicationConstants.ENTITY_TYPE_SERVICE_APPLICATION);
					LOG_OBJECT.Debug("Executing transaction insertIntoSuperSeeding for SC id " + lsSCId);
					insertIntoSuperSeeding(aoMybatisSession, aoHashMap, asCount);
					LOG_OBJECT.Debug("Finished transaction insertIntoSuperSeeding for SC id " + lsSCId);
				}
			}
		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error while Fetching and updating BR and Provider details ", aoAppExp);
			setMoState("Error while Fetching and updating BR and Provider details ");
			throw aoAppExp;
		}
		/**
		 * catch the exception thrown from the DAO layer log it into the console
		 * and wrap it into application exception
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while Fetching and updating BR and Provider details ", aoExp);
			setMoState("Error while Fetching and updating BR and Provider details ");
			throw new ApplicationException("Error while Fetching and updating BR and Provider details", aoExp);
		}
	}
	/**
	 * This method is used to update Or Insert Super Seeding Status Batch
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoParamHashMap
	 * @return Boolean loBooleanFlag
	 * @throws ApplicationException
	 */
	public Boolean updateOrInsertSuperSeedingStatusBatch(SqlSession aoMybatisSession, HashMap aoParamHashMap)
			throws ApplicationException
	{
		Boolean loBooleanFlag = Boolean.FALSE;
		try
		{
			int liRowCount = 0;
			liRowCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamHashMap,
					"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "updateSuperSeedingStatus",
					"java.util.Map");
			if (liRowCount == 0)
			{
				DAOUtil.masterDAO(aoMybatisSession, aoParamHashMap,
						"com.nyc.hhs.service.db.services.application.BatchNotificationMapper",
						"insertIntoSuperSeeding", "java.util.Map");
			}
			loBooleanFlag = Boolean.TRUE;

		}
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error while Fetching and updating BR and Provider details ", aoAppExp);
			setMoState("Error while Fetching and updating BR and Provider details ");
			throw aoAppExp;
		}
		/**
		 * catch the exception thrown from the DAO layer log it into the console
		 * and wrap it into application exception
		 */
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while Fetching and updating BR and Provider details ", aoExp);
			setMoState("Error while Fetching and updating BR and Provider details ");
			throw new ApplicationException("Error while Fetching and updating BR and Provider details", aoExp);
		}
		return loBooleanFlag;
	}

	/**
	 * This method fetched the document details from document table <li>This
	 * method was updated in R4</li>
	 * @param aoMybatisSession
	 * @return
	 * @throws ApplicationException
	 */
	public List<String> fetchDocumentDetailsFromDocumentTable(SqlSession aoMybatisSession) throws ApplicationException
	{

		try
		{
			DAOUtil.masterDAO(aoMybatisSession, null,
					"com.nyc.hhs.service.db.services.application.BatchNotificationMapper",
					"fetchDocumentDetailsFromDocumentTable", "java.util.Map");
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while Fetching document details from document table ", aoExp);
			setMoState("Error while Fetching document details from document table   ");
			throw new ApplicationException("Error while Fetching document details from document table ", aoExp);
		}
		return null;
	}

	/**
	 * This method fetches all entries from CITY_USER_DETAILS Table <li>This
	 * method was updated in R4</li>
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about due date and upload
	 *            order
	 * @return a list containing information about document lapsing master rules
	 * @throws ApplicationException
	 */

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<CityUserDetailsBeanForBatch> fetchCityUserDetailsForBatch(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<CityUserDetailsBeanForBatch> loResultList = null;
		try
		{
			HashMap loHMap = (HashMap) aoHashMap.get("loHMap");
			String lsActiveFlag = (String) loHMap.get("activeFlag");
			loResultList = (List<CityUserDetailsBeanForBatch>) DAOUtil.masterDAO(aoMybatisSession, lsActiveFlag,
					"com.nyc.hhs.service.db.services.application.BatchNotificationMapper",
					"fetchCityUserDetailsForBatch", "java.lang.String");
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while Fetching city user details from DB ", aoExp);
			setMoState("Error while Fetching city user details from DB  ");
			throw new ApplicationException("Error while Fetching city user details from DB", aoExp);
		}
		return loResultList;

	}

	/**
	 * This method is used for updating the status of a provider <li>This method
	 * was updated in R4</li>
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about organization status
	 *            and organization Id
	 * @return an integer of update count
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	public int deleteUSerDNCityUserDetailsForBatch(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		HashMap loHMap = (HashMap) aoHashMap.get("loHMap");

		int liRowCount = 0;
		try
		{
			liRowCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loHMap,
					"com.nyc.hhs.service.db.services.application.BatchNotificationMapper",
					"updateUserDNCityUserDetailsForBatch", "java.util.Map");
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while updating city user details records ", aoExp);
			setMoState("Error while updating city user details records  ");
			throw new ApplicationException("Error while updating city user details records", aoExp);
		}
		return liRowCount;
	}
		
	/**
	 * This method fetch agency name from NYC_AGENCY_DETAILS Table <li>This
	 * method was updated in R4</li>
	 * @param aoMybatisSession Mybatis Sql Session
	 * @param aoHashMap - hash map
	 * @return loResultList List of NYCAgency details having Agency name
	 *         information
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List getAllNYCAgencyId(SqlSession aoMybatisSession, HashMap aoHashMap) throws ApplicationException
	{
		List loResultList = null;
		try
		{
			loResultList = (List) DAOUtil.masterDAO(aoMybatisSession, null,
					"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "getAllNYCAgencyId", null);
		}
		catch (ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Error while fetching all groups from database ", aoExp);
			setMoState("Error while fetching all groups from database   ");
			throw new ApplicationException("Error while fetching all groups from database ", aoExp);
		}
		return loResultList;
	}

	//* BEGIN QC 8515 Release 6.1.0 
	/**
	 * This method fetches all Application Id, status and exp date from the database for
	 * that were created after applications which are not latest.
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing ProviderId and BRId
	 * @return a list of Business application that have been created later then currently processing BR
	 * @throws ApplicationException
	 */

	public List<ApplicationIdBRStatusBean> fetchApplicationIdBRStatus(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{

		List<ApplicationIdBRStatusBean> loResultList = null;
		//HashMap loHMap = (HashMap) aoHashMap.get("loHMap"); 

		loResultList = (List<ApplicationIdBRStatusBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchApplicationIdBRStatus",
				"java.util.HashMap"); 
		         
		return loResultList;

	}
	/**
	* @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing ProviderId 
	 * @return counter of BR for Provider
	 * @throws ApplicationException
	 */

	public String fetchBusinessApplicationCount(SqlSession aoMybatisSession, HashMap aoHashMap) 
			throws ApplicationException
	{

		String counter = null;
		//HashMap loHMap = (HashMap) aoHashMap.get("loHMap");
		counter = (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchBusinessApplicationCount",
				"java.util.HashMap"); 
	         
		return counter;
	}
	
	/* END QC 8515 Release 6.1.0 */
	
	
	/* BEGIN QC 8393 Release 6.1.0 */
	
	/**
	 * This method fetches all Application Id, status and exp date from the database for
	 * that were created after applications which are not latest.
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing ProviderId and BRId
	 * @return a list of Business application that have been created later then currently processing BR
	 * @throws ApplicationException
	 */

	public List<SupersedingStatusBean> fetchSupersedingStatusBeforeDelete(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{

		List<SupersedingStatusBean> loResultList = null;
		
		loResultList = (List<SupersedingStatusBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchSupersedingStatusBeforeDelete",
				"java.util.HashMap"); 
		         
		return loResultList;

	}
	
	/**
	* @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing ProviderId 
	 * @return True if call was successful, even if no rows had to be deleted
	 * @throws ApplicationException
	 */
	public Boolean deleteErroneousFromSuperseding(SqlSession aoMybatisSession) 
			throws ApplicationException
	{

		Boolean loBooleanFlag = Boolean.FALSE;
		try
		{
			HashMap<String, String> aoHashMap = new HashMap<String, String>();
			
			DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "deleteErroneousFromSuperseding",
					"java.util.Map");
			loBooleanFlag = Boolean.TRUE;

		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while deleting erroneous records from Superseding_Status ", aoExp);
			setMoState("Error while deleting erroneous records from Superseding_Status ");
			throw new ApplicationException("Error while deleting erroneous records from Superseding_Status ", aoExp);
		}
		return loBooleanFlag;
	}
	/* END QC 8393 Release 6.1.0 */
	
    /* BEGIN QC 8667 Release 6.1.0 */
	/**
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing ProviderId 
	 * @return counter of specific event for Provider in Superseding table
	 * @throws ApplicationException
	 */

	public String fetchSupersedingStatusCount(SqlSession aoMybatisSession, HashMap aoHashMap) 
	throws ApplicationException
	{
	
		String counter = null;
		counter = (String) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
			"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchSupersedingStatusCount",
			"java.util.HashMap"); 
	     
		return counter;
	}
	
	/**
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing ProviderId and Event
	 * @return True if call was successful, even if no rows had to be deleted
	 * @throws ApplicationException
	 */
	public Boolean deleteEventFromSupersedingStatusForProvider(SqlSession aoMybatisSession, HashMap aoHashMap ) 
			throws ApplicationException
	{
		Boolean loBooleanFlag = Boolean.FALSE;
		try
		{						
			DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
					"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "deleteEventFromSupersedingStatusForProvider",
					"java.util.HashMap");
			loBooleanFlag = Boolean.TRUE;
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while deleting records from Superseding_Status ", aoExp);
			setMoState("Error while deleting records from Superseding_Status ");
			throw new ApplicationException("Error while deleting records from Superseding_Status ", aoExp);
		}
		return loBooleanFlag;
	}
	/* END QC 8667 Release 6.1.0 */

	/* BEGIN QC 6749 Release 6.1.0 */
	/**
	 * This method fetches the Latest Business Application Id and status from the database for
	 * provider.
	 * @param aoMybatisSession mybatis sql session
	 * @param aoHashMap a map containing information about sysdate
	 * @return 
	 * @throws ApplicationException
	 */

	public List<ApplicationIdStatusBean> fetchLatestBADetailsForProvider(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<ApplicationIdStatusBean> loResultList = null;

		loResultList = (List<ApplicationIdStatusBean>) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
				"com.nyc.hhs.service.db.services.application.BatchNotificationMapper", "fetchLatestBADetailsForProvider",
				"java.util.HashMap");

		return loResultList;
	}
	/* END QC 6749 Release 6.1.0 */
}
