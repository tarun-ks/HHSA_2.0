package com.nyc.hhs.daomanager.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;

import com.accenture.formtaglib.DomStatus;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.ApplicationAuditBean;
import com.nyc.hhs.model.ApplicationBean;
import com.nyc.hhs.model.BusinessApplicationSummary;
import com.nyc.hhs.model.ContractDetails;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.DocumentBean;
import com.nyc.hhs.model.OrganizationStatusBean;
import com.nyc.hhs.model.Population;
import com.nyc.hhs.model.PrintContentBean;
import com.nyc.hhs.model.ServiceQuestions;
import com.nyc.hhs.model.ServiceSettingBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.StatusBean;
import com.nyc.hhs.model.SubSectionBean;
import com.nyc.hhs.model.TaskQueue;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.model.WithdrawRequestDetails;
import com.nyc.hhs.service.db.dao.ApplicationDAO;
import com.nyc.hhs.service.db.dao.QuestionAnswerDAO;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.PropertyUtil;

/**
 * 
 * ApplicationService: This class is used to fetch the application user
 * information,inserts and deletes the user information in the database. Also it
 * manages the functionality with regards to population, language, service,
 * service setting, geography and generating printer friendly view.
 */
public class ApplicationService extends ServiceState
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ApplicationService.class);

	/**
	 * This Method is used to select Application user details for an
	 * Organization
	 * 
	 * @param asOrgId Organization Id of Logged in User
	 * @param aoMyBatisSession MyBatis Sql Session object
	 * @return Application user bean with user information values
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<ApplicationBean> checkUserAppDetails(String asOrgId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{

		ArrayList<ApplicationBean> loApplicationBean = null;
		try
		{
			loApplicationBean = (ArrayList<ApplicationBean>) DAOUtil.masterDAO(aoMyBatisSession, asOrgId,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "selectUserAppInfo", "java.lang.String");
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("asOrgId", asOrgId);
			aoAppEx.addContextData("error", "Error occured while getting application information");
			setMoState("checkUserAppDetails > fail to getting application information ");
			throw aoAppEx;
		}
		setMoState("checkUserAppDetails > successfully got user applicaton information");
		return loApplicationBean;
	}

	/**
	 * This method is used to insert new application user information based on
	 * the parameter values
	 * 
	 * @param asAppId Application Id
	 * @param asUserID User Id of Logged-in user
	 * @param asCreatedBy User creating the application
	 * @param asCreatedDate Date of Application creation
	 * @param asUpdatedBy User updating the Application
	 * @param asUpdatedDate Date of Last Update
	 * @param asStatus Application Current Status
	 * @param aoMyBatisSession MyBatis Sql Session
	 * @param asOrgId Organization Id of Logged-in user
	 * @return lbStatus Insertion Success/Failure status
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public Boolean insertApplicationInfo(String asAppId, String asUserID, String asUpdatedBy, String asUpdatedDate,
			String asOrgId, SqlSession aoMyBatisSession) throws ApplicationException
	{
		Boolean lbStatus = false;
		HashMap<String, Object> loApplicationMap = new HashMap();
		try
		{
			loApplicationMap.put("asAppId", asAppId);
			loApplicationMap.put("asUserID", asUserID);
			loApplicationMap.put("asUpdatedBy", asUpdatedBy);
			loApplicationMap.put("asUpdatedDate", asUpdatedDate);
			loApplicationMap.put("asOrgId", asOrgId);

			loApplicationMap.put("modifiedBy", asUserID);
			loApplicationMap.put("modifiedDate", new Date(System.currentTimeMillis()));

			DAOUtil.masterDAO(aoMyBatisSession, loApplicationMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"insertUserAppInfo", "java.util.Map");
			lbStatus = true;
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loApplicationMap);
			aoAppEx.addContextData("error", "Error to create new Application");
			setMoState("Fail : insertApplicationInfo > not able to create new application.");
			throw aoAppEx;
		}
		setMoState("success: insertApplicationInfo > successfully created new application");
		return lbStatus;
	}

	/**
	 * This method is used to check for an existing application and inserting
	 * data for a new business application creation values
	 * 
	 * @param asAppId Application Id
	 * @param asUserID User Id of Logged-in user
	 * @param asCreatedBy User creating the application
	 * @param asCreatedDate Date of Application creation
	 * @param asUpdatedBy User updating the Application
	 * @param asUpdatedDate Date of Last Update
	 * @param asStatus Application Current Status
	 * @param aoMyBatisSession MyBatis Sql Session
	 * @param asOrgId Organization Id of Logged-in user
	 * @return loApplicationMap HashMap containing Application data
	 * @throws ApplicationException
	 */
	public Map<String, Object> createNewBusinessApplication(String asAppId, String asBusinessAppId, String asUserID,
			String asOrgId, String asStatus, SqlSession aoMyBatisSession) throws ApplicationException
	{
		Map<String, Object> loApplicationMap = new HashMap<String, Object>();
		try
		{
			loApplicationMap.put("asAppId", asAppId);
			loApplicationMap.put("asAppStatus", asStatus);
			loApplicationMap.put("asUserID", asUserID);
			loApplicationMap.put("asBusinessAppId", asBusinessAppId);
			loApplicationMap.put("asUserID", asUserID);
			loApplicationMap.put("asOrgId", asOrgId);
			loApplicationMap.put("createdBy", asUserID);
			loApplicationMap.put("createdDate", new java.util.Date(System.currentTimeMillis()));
			loApplicationMap.put("modifiedBy", asUserID);
			loApplicationMap.put("modifiedDate", new Date(System.currentTimeMillis()));
			WithdrawRequestDetails loWithdrawRequestDetails = (WithdrawRequestDetails) DAOUtil.masterDAO(
					aoMyBatisSession, loApplicationMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"checkForExistingApplication", "java.util.Map");
			if (loWithdrawRequestDetails == null)
			{
				DAOUtil.masterDAO(aoMyBatisSession, loApplicationMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertUserAppInfo", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, loApplicationMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"createNewBusinessApplication", "java.util.Map");
				Map<String, Object> loSectionMap = new HashMap<String, Object>();
				loSectionMap.put("asBussAppId", asBusinessAppId);
				loSectionMap.put("asUserId", asUserID);
				loSectionMap.put("asOrgId", asOrgId);
				loSectionMap.put("asModifiedBy", asUserID);
				loSectionMap.put("asSubmittedBy", asUserID);
				loSectionMap.put("aoModifiedDate", new Date(System.currentTimeMillis()));
				loSectionMap.put("aoSubmittedDate", new Date(System.currentTimeMillis()));
				loSectionMap.put("asLockFlag", 0);

				HashMap<String, Object> loFormNameMap = PropertyUtil.getFormNameVersionMap("basics");
				String lsFormName = (String) loFormNameMap.get(ApplicationConstants.FORM_NAME);
				String lsVersion = (String) loFormNameMap.get(ApplicationConstants.FORMVERSION_STRING);
				String lsFormId = lsFormName + "_" + lsVersion;
				loSectionMap.put("asFormId", lsFormId);
				loSectionMap.put("asFormName", lsFormName);
				loSectionMap.put("asFormVersion", lsVersion);

				loSectionMap.put("asStatus", ApplicationConstants.DRAFT_STATE);
				loSectionMap.put("asSection", ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS);
				DAOUtil.masterDAO(aoMyBatisSession, loSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertSectionStatus", "java.util.Map");
				loSectionMap.put("asSubSection", ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION);
				DAOUtil.masterDAO(aoMyBatisSession, loSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertSubSectionStatusBasic", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, loSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateBusiAppModifiedDate", "java.util.Map");

				loSectionMap.put("asStatus", ApplicationConstants.NOT_STARTED_STATE);
				loSectionMap.put("asSubSection", ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS);
				DAOUtil.masterDAO(aoMyBatisSession, loSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertSubSectionStatusBasic", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, loSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateBusiAppModifiedDate", "java.util.Map");
				loSectionMap.put("asSubSection", ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_GEOGRAPHY);
				DAOUtil.masterDAO(aoMyBatisSession, loSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertSubSectionStatus", "java.util.Map");
				loSectionMap.put("asSubSection", ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_LANGUAGES);
				DAOUtil.masterDAO(aoMyBatisSession, loSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertSubSectionStatus", "java.util.Map");
				loSectionMap.put("asSubSection", ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_POPULATIONS);
				DAOUtil.masterDAO(aoMyBatisSession, loSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertSubSectionStatus", "java.util.Map");

				loSectionMap.put("asSection", ApplicationConstants.BUSINESS_APPLICATION_SECTION_FILINGS);
				DAOUtil.masterDAO(aoMyBatisSession, loSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertSectionStatus", "java.util.Map");
				loSectionMap.put("asSection", ApplicationConstants.BUSINESS_APPLICATION_SECTION_BOARD);
				DAOUtil.masterDAO(aoMyBatisSession, loSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertSectionStatus", "java.util.Map");
				loSectionMap.put("asSection", ApplicationConstants.BUSINESS_APPLICATION_SECTION_POLICIES);
				DAOUtil.masterDAO(aoMyBatisSession, loSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertSectionStatus", "java.util.Map");
				loApplicationMap.put("appExistingStatus", "no");
			}
			else
			{
				loApplicationMap.put("asBusinessAppId", loWithdrawRequestDetails.getMsBusinessAppId());
				loApplicationMap.put("asAppId", loWithdrawRequestDetails.getMsAppId());
				loApplicationMap.put("appExistingStatus", "yes");
			}
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loApplicationMap);
			aoAppEx.addContextData("", "createNewBusinessApplication > Error");
			setMoState("Fail: createNewBusinessApplication> Not able to create new application in DB");
			throw aoAppEx;
		}
		setMoState("Success: createNewBusinessApplication > successfully updated db with new app");
		return loApplicationMap;
	}

	/**
	 * This method inserts Document type values in database used at the time of
	 * file upload
	 * 
	 * @param asAppId Application Id
	 * @param loDomReturn the domstatus with information about errors
	 * @param aoMyBatisSession MyBatis Sql Session
	 * @return DomStatus the domstatus with information about errors
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public DomStatus insertDocType(String asAppId, String asOrgId, String asFormName, String asFormVersion,
			String asUserID, String asSection, DomStatus aoDomReturn, String asBussAppStatus,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		ApplicationDAO loApplicationDAO = new ApplicationDAO();
		String lsStatus = ApplicationConstants.NOT_STARTED_STATE;
		boolean lbSkipService = false;
		lbSkipService = BusinessApplicationUtil.getBusinessApplicationReadOnlyStatus(asBussAppStatus);
		if (!lbSkipService && asAppId != null)
		{
			if (!aoDomReturn.isDomWithError() && aoDomReturn.getDocType() != null)
			{
				try
				{
					setDocTypeValues(asAppId, asOrgId, asFormName, asFormVersion, asUserID, asSection, aoDomReturn,
							aoMyBatisSession, loApplicationDAO, lsStatus);
				}

				catch (ApplicationException aoAppEx)
				{
					aoAppEx.addContextData("Org ID", asOrgId);
					aoAppEx.addContextData("Form Name", asFormName);
					setMoState("Fail: insertDocType > Failt to delete or add doctype");
					throw aoAppEx;
				}
			}
			setMoState("Success: insertDocType > Successfully deleted and inserted");
		}

		return aoDomReturn;
	}

	/**
	 * This method select Document type information for organization and
	 * business application summary
	 * 
	 * @param asAppId
	 * @param asOrgId
	 * @param asFormName
	 * @param asFormVersion
	 * @param asUserID
	 * @param asSection
	 * @param aoDomReturn
	 * @param aoMyBatisSession
	 * @param aoApplicationDAO
	 * @param asStatus
	 * @throws ApplicationException
	 */
	private void setDocTypeValues(String asAppId, String asOrgId, String asFormName, String asFormVersion,
			String asUserID, String asSection, DomStatus aoDomReturn, SqlSession aoMyBatisSession,
			ApplicationDAO aoApplicationDAO, String asStatus) throws ApplicationException
	{
		Map<String, String> loMapDocTypeInfo = new HashMap<String, String>();
		loMapDocTypeInfo.put("asAppId", asAppId);
		loMapDocTypeInfo.put("asOrgId", asOrgId);
		loMapDocTypeInfo.put("asFormName", asFormName);
		loMapDocTypeInfo.put("asFormVersion", asFormVersion);

		List<Document> loDocument = (List<Document>) DAOUtil.masterDAO(aoMyBatisSession, loMapDocTypeInfo,
				ApplicationConstants.MAPPER_CLASS_APPLICATION, "selectDocTypeInfo", "java.util.Map");
		Map<String, String> loDocTypeToInsert = aoDomReturn.getDocType();
		for (Document loDoc : loDocument)
		{
			String lsDocType = loDoc.getDocType();
			String lsDocCategory = loDoc.getDocCategory();
			if (loDocTypeToInsert.containsKey(loDoc.getDocType()))
			{
				if (lsDocCategory.equalsIgnoreCase(loDocTypeToInsert.get(lsDocType)))
				{
					loDocTypeToInsert.remove(loDoc.getDocType());
				}
				else
				{
					aoApplicationDAO.deleteDocTypeInfo(aoMyBatisSession, asAppId, asOrgId, asFormName, asFormVersion,
							lsDocCategory, lsDocType);
				}
			}
			else
			{
				aoApplicationDAO.deleteDocTypeInfo(aoMyBatisSession, asAppId, asOrgId, asFormName, asFormVersion,
						lsDocCategory, lsDocType);
			}
		}
		for (Entry<String, String> loEntry : loDocTypeToInsert.entrySet())
		{
			aoApplicationDAO.insertDocTypeInfo(aoMyBatisSession, asAppId, asOrgId, asFormName, asFormVersion,
					loEntry.getValue(), loEntry.getKey(), asStatus, asUserID, asSection);
		}
	}

	/**
	 * This method get the Population for Application
	 * 
	 * @method This method gets the data of population selected and stored in
	 *         organization_population table
	 * @param asApplicationID Element Id
	 * @param aoMyBatisSession MyBatis session object
	 * @return Population object
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<Population> getPopulationForApplication(String asOrgId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<Population> loPopulation = null;
		try
		{
			loPopulation = (List<Population>) DAOUtil.masterDAO(aoMyBatisSession, asOrgId,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "selectPopulation", "java.lang.String");
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("org id", asOrgId);
			aoAppEx.addContextData("error", "Fail: getPopulationForApplication >  ");
			throw aoAppEx;
		}
		return loPopulation;
	}

	/**
	 * This method call the DAO class to insert the Arraylist of population data
	 * in database
	 * 
	 * @param populationdatalist List of Population Data
	 * @param aoMyBatisSession MyBatis Sql Session
	 * @return success status true= successful database entry | false=
	 *         error/exception in database entry
	 * @throws ApplicationException
	 */
	public Boolean addPopulationToApplication(String asAppId, String asOrgId, String asUserID, String asSection,
			List<Population> aoPopulationdatalist, String asBussAppStatus, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean lbSuccessStatus = false;
		Boolean lbDeleteSuccessStatus = false;
		boolean lbSkipService = false;
		if (asAppId != null)
		{
			lbSkipService = BusinessApplicationUtil.getBusinessApplicationReadOnlyStatus(asBussAppStatus);
		}
		if (!lbSkipService)
		{
			try
			{
				// In case of update, first delete all the values from
				// organization_population table with the given organization id
				DAOUtil.masterDAO(aoMyBatisSession, asOrgId, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"deletePopulation", "java.lang.String");
				lbDeleteSuccessStatus = true;
				Map<String, Object> loMapRequiredDetails = new HashMap<String, Object>();
				loMapRequiredDetails.put("asAppId", asAppId);
				loMapRequiredDetails.put("asOrgId", asOrgId);
				loMapRequiredDetails.put("asSectonId", asSection);
				loMapRequiredDetails.put("asUserID", asUserID);
				loMapRequiredDetails.put("lsStatus", ApplicationConstants.COMPLETED_STATE);
				loMapRequiredDetails.put("loDate", new Date(System.currentTimeMillis()));
				loMapRequiredDetails.put("SUB_SECTION_ID",
						ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_POPULATIONS);
				// If deletion from table is successful insert the new user
				// input population data to organization_population table
				if (lbDeleteSuccessStatus == true)
				{
					ApplicationDAO loApplicationDAO = new ApplicationDAO();
					lbSuccessStatus = loApplicationDAO.addPopulationToApplication(aoPopulationdatalist,
							loMapRequiredDetails, aoMyBatisSession);
				}
			}
			catch (ApplicationException aoAppEx)
			{
				throw aoAppEx;
			}
		}
		return lbSuccessStatus;
	}

	/**
	 * This method gets the selected languages corresponding to an organization
	 * 
	 * @method Select taxonomy details from database
	 * @param asTaxonomyType Type of taxonomy
	 * @param aoMyBatisSession MyBatis Sql Session
	 * @return Array List with <ELEMENT_TYPE>
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public ArrayList<String> getSelectedLanguages(String asOrgId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		ArrayList<String> loLanguageIdList = null;
		try
		{
			loLanguageIdList = (ArrayList<String>) DAOUtil.masterDAO(aoMyBatisSession, asOrgId,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "selectLanguageInfo", "java.lang.String");

		}
		catch (Exception loEx)
		{
			ApplicationException loApp = new ApplicationException(
					"Exception occured while fetching  data from organization_details table ", loEx);
			loApp.addContextData("org id", asOrgId);
			LOG_OBJECT.Error("Exception occured while fetching  data from organization_details table", loApp);
			throw loApp;
		}
		return loLanguageIdList;
	}
	   /**
		 * This Method get Selected Languages interpretation Service
		 * @param asOrgId String
		 * @param aoMyBatisSession SqlSession
		 * @return loLanguageinterpretationList ArrayList<String>
		 * @throws ApplicationException if any Exception occurs
		 */
	public ArrayList<String> getSelectedLanguagesinterpretationService(String asOrgId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		ArrayList<String> loLanguageinterpretationList = null;
		try
		{
			loLanguageinterpretationList = (ArrayList<String>) DAOUtil.masterDAO(aoMyBatisSession, asOrgId,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "selectLanguageInfointerpretation",
					"java.lang.String");

		}
		catch (Exception loEx)
		{
			ApplicationException loApp = new ApplicationException(
					"Exception occured while fetching  data from organization_details table ", loEx);
			loApp.addContextData("org id", asOrgId);
			LOG_OBJECT.Error("Exception occured while fetching  data from organization_details table", loEx);
			LOG_OBJECT.Error("Exception occured while fetching  data from organization_details table", loEx);
			throw loApp;
		}
		return loLanguageinterpretationList;
	}

	/**
	 * This method is used to insert language taxonomy data in
	 * organization_details table
	 * 
	 * @param asOrgId Organization Id of logged-in user
	 * @param asElementType Type of Element inserted
	 * @param aoElementId Element Id
	 * @param aoMyBatisSession MyBatis Sql seeion
	 * @return lbStatus Insert Success/Failure status
	 * @throws ApplicationException
	 */
	public boolean addLanguageToApplication(String asAppId, String asOrgId, String asUserID, String asSection,
			String asElementType, ArrayList<String> aoElementIdList, String asBussAppStatus,
			String aoLanguagesInterpretationFlag, SqlSession aoMyBatisSession) throws ApplicationException
	{
		Boolean lbStatus = false;
		boolean lbSkipService = false;
		if (asAppId != null)
		{
			lbSkipService = BusinessApplicationUtil.getBusinessApplicationReadOnlyStatus(asBussAppStatus);
		}
		if (!lbSkipService)
		{
			Map<String, Object> loRequiredDetailsM = new HashMap<String, Object>();
			try
			{
				loRequiredDetailsM.put("asAppId", asAppId);
				loRequiredDetailsM.put("asOrgId", asOrgId);
				loRequiredDetailsM.put("asSectonId", asSection);
				loRequiredDetailsM.put("asUserID", asUserID);
				loRequiredDetailsM.put("lsStatus", ApplicationConstants.COMPLETED_STATE);
				loRequiredDetailsM.put("loDate", new Date(System.currentTimeMillis()));
				loRequiredDetailsM.put("SUB_SECTION_ID",
						ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_LANGUAGES);
				loRequiredDetailsM.put("aoLanguagesInterpretationFlag", aoLanguagesInterpretationFlag);
				ApplicationDAO loApplicationDAO = new ApplicationDAO();
				lbStatus = loApplicationDAO.insertLanguageInfo(aoMyBatisSession, asOrgId, asElementType,
						aoElementIdList, asUserID, loRequiredDetailsM);
			}
			catch (ApplicationException aoAppEx)
			{
				aoAppEx.setContextData(loRequiredDetailsM);
				aoAppEx.addContextData("error", "error to adding langauage");
				throw aoAppEx;
			}
		}

		return lbStatus;
	}

	/**
	 * This method retrieves details of Services related to a selected Service
	 * 
	 * @param asAppId Application Id
	 * @param asOrgId Organization Id of Logged-in user
	 * @param aoMyBatisSession MyBatis Sql session
	 * @return List of TaxonomyTree Bean containing details of Linked Service
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<TaxonomyTree> getRelatedServices(String asAppId, String asOrgId, List<String> asSelectedServices,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<TaxonomyTree> loRelatedServiceDetails = null;
		Map<String, Object> loMapRequiredDetails = new HashMap<String, Object>();
		try
		{
			loMapRequiredDetails.put("asAppId", asAppId);
			loMapRequiredDetails.put("asOrgId", asOrgId);
			loMapRequiredDetails.put("selectedServices", asSelectedServices);
			loRelatedServiceDetails = (List<TaxonomyTree>) DAOUtil.masterDAO(aoMyBatisSession, loMapRequiredDetails,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getRelatedServices", "java.util.Map");
			if (null != loRelatedServiceDetails)
			{
				Iterator loListItr = loRelatedServiceDetails.iterator();
				while (loListItr.hasNext())
				{
					TaxonomyTree loTaxonomyTree = (TaxonomyTree) loListItr.next();
					loTaxonomyTree.setMsElementid(loTaxonomyTree.getMsLinkageId());
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loMapRequiredDetails);
			aoAppEx.addContextData("error", "error to get releated service");
			throw aoAppEx;
		}
		return loRelatedServiceDetails;

	}

	/**
	 * This method retrieves details of Services related to a selected Service
	 * for All services
	 * 
	 * @param asAppId Application Id
	 * @param asOrgId Organization Id of Logged-in user
	 * @param aoMyBatisSession MyBatis Sql session
	 * @return List of TaxonomyTree Bean containing details of Linked Service
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<TaxonomyTree> getRelatedServicesAll(String asAppId, String asOrgId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<TaxonomyTree> loRelatedServiceDetails = null;
		Map<String, Object> loMapRequiredDetails = new HashMap<String, Object>();
		try
		{
			loMapRequiredDetails.put("asAppId", asAppId);
			loMapRequiredDetails.put("asOrgId", asOrgId);
			loRelatedServiceDetails = (List<TaxonomyTree>) DAOUtil.masterDAO(aoMyBatisSession, loMapRequiredDetails,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getRelatedServicesAll", "java.util.Map");
			if (null != loRelatedServiceDetails)
			{
				Iterator loListItr = loRelatedServiceDetails.iterator();
				while (loListItr.hasNext())
				{
					TaxonomyTree loTaxonomyTree = (TaxonomyTree) loListItr.next();
					loTaxonomyTree.setMsElementid(loTaxonomyTree.getMsLinkageId());
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loMapRequiredDetails);
			aoAppEx.addContextData("error", "error to get releated service");
			throw aoAppEx;
		}
		return loRelatedServiceDetails;

	}

	/**
	 * This Methods fetches data from ServiceSetting transaction table for
	 * organization specific data against a service application
	 * 
	 * @param aoMyBatisSession MyBatis Sql session
	 * @return loTaxonomyIdList List of selected service settings
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getSelectedSettingList(SqlSession aoMyBatisSession, String asOrganizationId,
			String asServiceAppId, String asElementType) throws ApplicationException
	{
		List<Map<String, String>> loElementIdMap = null;
		try
		{

			Map<String, Object> loMapRequiredDetails = new HashMap<String, Object>();
			loMapRequiredDetails.put("OrganizationID", asOrganizationId);
			loMapRequiredDetails.put("ServiceAppID", asServiceAppId);
			loMapRequiredDetails.put("ElementType", asElementType);

			loElementIdMap = (List<Map<String, String>>) DAOUtil.masterDAO(aoMyBatisSession, loMapRequiredDetails,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getSelectedSettingList", "java.util.Map");
		}
		catch (ApplicationException aoEx)
		{
			aoEx.addContextData("error",
					"Exception occured when trying to get the data from db in DAO layer for 'getSelectedSettingList' ");
			aoEx.addContextData("org id", asOrganizationId);
			aoEx.addContextData("Servcie Id", asServiceAppId);
			LOG_OBJECT.Error(
					"Error occured while getting form data from getSelectedSettingList method in ApplicationService",
					aoEx);
			throw aoEx;
		}
		return loElementIdMap;
	}

	/**
	 * This method adds service setting selected by organization for specific
	 * service in service setting table
	 * 
	 * @param asOrgId Organization Id
	 * @param asElementType Type of Element(Setting/Specialization)
	 * @param aoElementIdList List of Settings to be added in Service Setting
	 * @param aoMyBatisSession MyBatis Sql Session
	 * @return lbStatus Insertion success/Failure status
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public boolean addSettingToService(String asAppId, String asOrgId, String asUserID, String asSection,
			String asServiceAppId, String asElementType, ArrayList aoElementIdList, String asBottomCheckBox,
			String asServiceAppStatus, SqlSession aoMyBatisSession) throws ApplicationException
	{
		Boolean lbStatus = false;
		boolean lbSkipService = false;
		lbSkipService = BusinessApplicationUtil.getServiceApplicationStatus(asServiceAppStatus);
		if (lbSkipService)
		{
			Map<String, Object> loRequiredDetailsM = new HashMap<String, Object>();
			try
			{
				ApplicationDAO loApplicationTypeDAO = new ApplicationDAO();
				loRequiredDetailsM.put("asAppId", asAppId);
				loRequiredDetailsM.put("asServiceAppId", asServiceAppId);
				loRequiredDetailsM.put("asOrgId", asOrgId);
				loRequiredDetailsM.put("asSectonId", asSection);
				loRequiredDetailsM.put("asUserID", asUserID);
				loRequiredDetailsM.put("lsStatus", ApplicationConstants.COMPLETED_STATE);
				loRequiredDetailsM.put("loDate", new Date(System.currentTimeMillis()));
				loRequiredDetailsM.put("SUB_SECTION_ID", asElementType);
				lbStatus = loApplicationTypeDAO.addSettingToService(aoMyBatisSession, asOrgId, asElementType,
						aoElementIdList, asBottomCheckBox, asServiceAppId, asUserID, loRequiredDetailsM);

			}
			catch (ApplicationException aoAppEx)
			{
				aoAppEx.setContextData(loRequiredDetailsM);
				aoAppEx.addContextData("error", "Error to adding setting information to service");
				throw aoAppEx;
			}
		}
		return lbStatus;
	}

	/**
	 * This methods get Business Application summary data from Section and
	 * Document tables in database
	 * 
	 * @param asApplicationID Application Id
	 * @param asOrgId Organization Id
	 * @param aoMyBatisSession MyBatis Sql Session
	 * @return List of BusinessApplicationSummary Bean
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<BusinessApplicationSummary> getStatusForAll(String asApplicationID, String asOrgId,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		List<BusinessApplicationSummary> loBusinessApplicationSummary = null;
		try
		{
			Map<String, String> loMap = new HashMap<String, String>();
			loMap.put("asApplicationId", asApplicationID);
			loMap.put("asOrgId", asOrgId);

			loBusinessApplicationSummary = (List<BusinessApplicationSummary>) DAOUtil.masterDAO(aoMyBatisSession,
					loMap, ApplicationConstants.MAPPER_CLASS_APPLICATION, "getBusinessApplicationSummary",
					"java.util.Map");

		}
		catch (ApplicationException aoAppEx)
		{
			throw aoAppEx;
		}
		return loBusinessApplicationSummary;
	}

	/**
	 * This method inserts Geography type values in database
	 * organization_details
	 * 
	 * @param asOrgId Organization Id
	 * @param asElementType Element type
	 * @param aoElementIdList List of elements to be inserted in database
	 * @param aoMyBatisSession MyBatis Sql Session
	 * @return lbStatus Insertion Success/Failure session
	 * @throws ApplicationException
	 */

	@SuppressWarnings("rawtypes")
	public Boolean addGeogaphyToApplication(String asAppId, String asOrgId, String asUserID, String asSection,
			String asElementType, ArrayList aoElementIdList, String asBottomCheckBox, String asBussAppStatus,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		Boolean lbStatus = false;
		boolean lbSkipService = false;
		if (asAppId != null)
		{
			lbSkipService = BusinessApplicationUtil.getBusinessApplicationReadOnlyStatus(asBussAppStatus);
		}
		if (!lbSkipService)
		{
			Map<String, Object> loRequiredDetailsM = new HashMap<String, Object>();
			try
			{
				loRequiredDetailsM.put("asAppId", asAppId);
				loRequiredDetailsM.put("asOrgId", asOrgId);
				loRequiredDetailsM.put("asSectonId", asSection);
				loRequiredDetailsM.put("asUserID", asUserID);
				loRequiredDetailsM.put("lsStatus", ApplicationConstants.COMPLETED_STATE);
				loRequiredDetailsM.put("loDate", new Date(System.currentTimeMillis()));
				loRequiredDetailsM.put("SUB_SECTION_ID",
						ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_GEOGRAPHY);
				ApplicationDAO loApplicationTypeDAO = new ApplicationDAO();
				lbStatus = loApplicationTypeDAO.addGeogaphyToApplication(aoMyBatisSession, asOrgId, asElementType,
						aoElementIdList, asBottomCheckBox, asUserID, loRequiredDetailsM);
			}
			catch (ApplicationException aoAppEx)
			{
				aoAppEx.setContextData(loRequiredDetailsM);
				aoAppEx.addContextData("error", "Error getting geography information");
				setMoState("Fail: addGeogaphyToApplication > Error getting geography information");
				throw aoAppEx;
			}
		}
		return lbStatus;
	}

	/**
	 * This method updates database when document is selected from vault
	 * 
	 * @param aoInputParamMap Map consisting of input parameters
	 * @return lbSuccessStatus Insertion Success/Failure status
	 * @throws ApplicationException
	 */
	public Boolean updateDocIdOnRadioSelect(String asDocId, String asLastModifiedBy, Date asLastModifiedDate,
			String asSubmissionBy, Date asSubmissionDate, String asDocType, String asFormName, String asFormVersion,
			String asOrgId, String asAppId, String asDocCategory, String asSection, String asDocStatus,
			String asDocTitle, String asUserID, String asFormId, String asServiceAppId, String asSectionId,
			String asEntityId, final SqlSession aoMyBatisSession) throws ApplicationException
	{
		Boolean lbSuccessStatus = false;
		HashMap<String, Object> loInputParamMap = new HashMap<String, Object>();
		try
		{

			loInputParamMap.put("asDocId", asDocId);
			loInputParamMap.put("asLastModifiedBy", asLastModifiedBy);
			loInputParamMap.put("asLastModifiedDate", asLastModifiedDate);
			loInputParamMap.put("asSubmissionBy", asSubmissionBy);
			loInputParamMap.put("asSubmissionDate", asSubmissionDate);
			loInputParamMap.put("asDocType", asDocType);
			loInputParamMap.put("asFormName", asFormName);
			loInputParamMap.put("asFormVersion", asFormVersion);
			loInputParamMap.put("asOrgId", asOrgId);
			loInputParamMap.put("asAppId", asAppId);
			loInputParamMap.put("asDocCategory", asDocCategory);
			loInputParamMap.put("asSection", asSection);
			loInputParamMap.put("asDocStatus", asDocStatus);
			loInputParamMap.put("asDocTitle", asDocTitle);
			loInputParamMap.put("asUserID", asUserID);
			loInputParamMap.put("asFormId", asFormId);
			loInputParamMap.put("asServiceAppId", asServiceAppId);
			loInputParamMap.put("asSectionId", asSectionId);
			loInputParamMap.put("asEntityId", asEntityId);// Defect #1805 fix
			ApplicationDAO loApplicationDAO = new ApplicationDAO();
			loApplicationDAO.updateDocIdOnRadioSelect(loInputParamMap, aoMyBatisSession);
			lbSuccessStatus = true;
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("error",
					"Error occured while updating document_id in document table while updateDocIdOnRadioSelect");
			aoAppEx.setContextData(loInputParamMap);
			setMoState("");
			throw aoAppEx;
		}
		return lbSuccessStatus;
	}

	/**
	 * This method removes a document from database when chosen to be deleted
	 * 
	 * @param aoInputParamMap Map containing input parameters
	 * @return lbSuccessStatus Insertion Success/Failure status
	 * @throws ApplicationException
	 */
	public Boolean removeDocFromApplication(String asDocId, String asFormName, String asFormVersion, String asOrgId,
			String asDocType, String asAppId, String asDocCat, String asLastModDate, String asLastModBy,
			String asSubmissionDate, String asSubmissionBy, String asDocTitle, String asDocStatus,
			String asServiceAppId, String asSectionId, String asEntityId, final SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean lbSuccessStatus = false;
		ApplicationDAO loApplicationDAO = new ApplicationDAO();
		HashMap<String, Object> loRemDocMap = new HashMap<String, Object>();
		try
		{

			loRemDocMap.put("asDocId", asDocId);/* set doc id as */
			loRemDocMap.put("asFormName", asFormName);
			loRemDocMap.put("asFormVersion", asFormVersion);
			loRemDocMap.put("asOrgId", asOrgId);
			loRemDocMap.put("asDocType", asDocType);
			loRemDocMap.put("asAppId", asAppId);
			loRemDocMap.put("asDocCat", asDocCat);
			loRemDocMap.put("asLastModDate", asLastModDate);
			loRemDocMap.put("asLastModBy", asLastModBy);
			loRemDocMap.put("asSubmissionDate", asSubmissionDate);
			loRemDocMap.put("asSubmissionBy", asSubmissionBy);
			loRemDocMap.put("asDocTitle", asDocTitle);
			loRemDocMap.put("asServiceAppId", asServiceAppId);
			loRemDocMap.put("asSectionId", asSectionId);
			loRemDocMap.put("asEntityId", asEntityId);// Defect #1805 fix
			loRemDocMap.put("asDocStatus", ApplicationConstants.NOT_STARTED_STATE);
			loApplicationDAO.removeDocFromApplication(loRemDocMap, aoMyBatisSession);
			lbSuccessStatus = true;
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loRemDocMap);
			aoAppEx.addContextData("error",
					"Error occured while removing document_id in document table while removeDocFromApplication");
			setMoState("Fail: removeDocFromApplication > removed doc from application ");
			throw aoAppEx;
		}
		setMoState("Success: removeDocFromApplication ");
		return lbSuccessStatus;
	}

	public HashMap getLinkToApplicationFlag(String asDocId, String asFormName, String asFormVersion, String asOrgId,
			String asDocType, String asAppId, String asDocCat, String asDocTitle, String asServiceAppId,
			String asSectionId, final SqlSession aoMyBatisSession) throws ApplicationException
	{
		HashMap<String, Object> loRemDocMap = new HashMap<String, Object>();
		HashMap loHmDocReqProps = new HashMap();
		try
		{
			loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
			loRemDocMap.put("asDocId", asDocId);/* set doc id as */
			loRemDocMap.put("asFormName", asFormName);
			loRemDocMap.put("asFormVersion", asFormVersion);
			loRemDocMap.put("asOrgId", asOrgId);
			loRemDocMap.put("asDocType", asDocType);
			loRemDocMap.put("asAppId", asAppId);
			loRemDocMap.put("asDocCat", asDocCat);
			loRemDocMap.put("asDocTitle", asDocTitle);
			loRemDocMap.put("asServiceAppId", asServiceAppId);
			loRemDocMap.put("asSectionId", asSectionId);
			List<Document> loDocument = null;
			if (asServiceAppId != null && !asServiceAppId.equalsIgnoreCase("null"))
			{
				loDocument = (List<Document>) DAOUtil.masterDAO(aoMyBatisSession, loRemDocMap,
						ApplicationConstants.MAPPER_CLASS_APPLICATION, "selectDocIdFromServiceApplication",
						"java.util.Map");
			}
			if (loDocument != null && !loDocument.isEmpty())
			{
				loHmDocReqProps = new HashMap();
				loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, true);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loRemDocMap);
			aoAppEx.addContextData("error",
					"Error occured while selecting document_id in document table while getLinkToApplicationFlag");
			setMoState("Fail: getLinkToApplicationFlag > getLinkToApplicationFlag doc from service application ");
			throw aoAppEx;
		}
		return loHmDocReqProps;
	}

	/**
	 * This method updates subsection summary table with document status
	 * 
	 * @param asAppId Application Id
	 * @param asOrgId Organization Id of logged-in user
	 * @param asFormName Form Name
	 * @param asFormVersion Form version for current form used
	 * @param asUserID User Id of logged in user
	 * @param asSection Section Name
	 * @param aoDomReturn DOM object input parameters
	 * @param aoMyBatisSession MyBatis Sql session
	 * @return aoDomReturn DOM object return status
	 * @throws ApplicationException
	 */
	public DomStatus insertUpdateDocStatus(String asAppId, String asOrgId, String asFormName, String asFormVersion,
			String asUserID, String asSection, DomStatus aoDomReturn, String asBussAppStatus,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		boolean lbSkipService = false;
		lbSkipService = BusinessApplicationUtil.getBusinessApplicationReadOnlyStatus(asBussAppStatus);
		if (!lbSkipService)
		{
			Map<String, Object> loMap = new HashMap<String, Object>();
			try
			{
				if (asAppId != null && ((aoDomReturn != null && !aoDomReturn.isDomWithError()) || aoDomReturn == null))
				{
					setDocStatus(asAppId, asOrgId, asFormName, asFormVersion, asUserID, asSection, aoMyBatisSession,
							loMap);
				}
			}
			catch (ApplicationException aoAppEx)
			{
				aoAppEx.setContextData(loMap);
				setMoState("Fail: ApplicationService > insertUpdateDocStatus - updating document status");
				aoAppEx.addContextData("errorMsg",
						"Error occured while updating document status in subsection table while insertUpdateDocStatus");
				throw aoAppEx;
			}
		}
		return aoDomReturn;
	}

	/**
	 * This method updates subsection summary table with document status
	 * 
	 * @param asAppId Application id
	 * @param asOrgId Organization id
	 * @param asFormName form name
	 * @param asFormVersion form version
	 * @param asUserID user id
	 * @param asSection section id
	 * @param aoMyBatisSession sql session
	 * @param aoMap map as input
	 * @throws ApplicationException
	 */
	private void setDocStatus(String asAppId, String asOrgId, String asFormName, String asFormVersion, String asUserID,
			String asSection, SqlSession aoMyBatisSession, Map<String, Object> aoMap) throws ApplicationException
	{
		Map<String, String> loMapDocTypeInfo = new HashMap<String, String>();
		loMapDocTypeInfo.put("asAppId", asAppId);
		loMapDocTypeInfo.put("asOrgId", asOrgId);
		loMapDocTypeInfo.put("asFormName", asFormName);
		loMapDocTypeInfo.put("asFormVersion", asFormVersion);
		List<Document> loDocument = null;
		loDocument = (List<Document>) DAOUtil.masterDAO(aoMyBatisSession, loMapDocTypeInfo,
				ApplicationConstants.MAPPER_CLASS_APPLICATION, "selectDocTypeInfo", "java.util.Map");
		boolean lbIsComplete = true;
		String lsStatus = ApplicationConstants.COMPLETED_STATE;
		int liStarted = 0;
		for (Document loDoc : loDocument)
		{
			if (loDoc.getStatus().equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE))
			{
				lbIsComplete = false;
			}
			else
			{
				liStarted++;
			}
		}
		if (!lbIsComplete)
		{
			lsStatus = ApplicationConstants.NOT_STARTED_STATE;
		}
		if (liStarted > 0 && !lbIsComplete)
		{
			lsStatus = ApplicationConstants.DRAFT_STATE;
		}
		aoMap.put("asAppId", asAppId);
		aoMap.put("asOrgId", asOrgId);
		aoMap.put("asFormName", asFormName);
		aoMap.put("asFormVersion", asFormVersion);
		aoMap.put("asSectonId", asSection);
		aoMap.put("asUserID", asUserID);
		aoMap.put("modifiedDate", new Date(System.currentTimeMillis()));
		aoMap.put("asFormId", asFormName + "_" + asFormVersion);
		aoMap.put("lsStatus", lsStatus);
		aoMap.put("loDate", new Date(System.currentTimeMillis()));
		aoMap.put("SUB_SECTION_ID", ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS);
		ApplicationDAO loApplicationDAO = new ApplicationDAO();
		if (!(asSection.equalsIgnoreCase(ApplicationConstants.DOCUMENT_LIST) && asFormName
				.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_POLICIES)))
		{
			if (asAppId != null)
			{
				loApplicationDAO.insertUpdateDocStatus(aoMap, aoMyBatisSession);
			}
			else
			{
				loApplicationDAO.insertUpdateDocStatusForOrg(aoMap, aoMyBatisSession);
			}
		}
	}

	/**
	 * This method updates subsection summary table with document status for
	 * Service
	 * 
	 * @param asAppId Application Id
	 * @param asOrgId Organization Id of logged-in User
	 * @param asSubSectionIdNextTab Nex Sub section tab
	 * @param aoMyBatisSession MyBatis Sql Session
	 * @return lbInsertUpdateDocStatusService Insert Success/Failure status
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public Boolean insertUpdateDocStatusService(String asAppId, String asOrgId, String asServiceAppId, String asUserId,
			String asSection, String asBussAppStatus, SqlSession aoMyBatisSession) throws ApplicationException
	{
		ApplicationDAO loApplicationDAO = new ApplicationDAO();
		Map<String, Object> loMapDocTypeInfo = new HashMap<String, Object>();
		Boolean lbInsertUpdateDocStatusService = false;
		boolean lbSkipService = false;
		lbSkipService = BusinessApplicationUtil.getServiceApplicationStatus(asBussAppStatus);
		if (lbSkipService)
		{
			try
			{
				lbInsertUpdateDocStatusService = insertUpdateDocStatusService(asAppId, asOrgId, asServiceAppId,
						asUserId, asSection, aoMyBatisSession, loApplicationDAO, loMapDocTypeInfo);
			}
			catch (ApplicationException aoAppEx)
			{
				aoAppEx.setContextData(loMapDocTypeInfo);
				setMoState("Fail: ApplicationService > insertUpdateDocStatus - updating document status for services");
				aoAppEx.addContextData("errorMsg",
						"Error occured while updating document status in subsection table while insertUpdateDocStatus for services");
				throw aoAppEx;
			}
		}
		return lbInsertUpdateDocStatusService;
	}

	/**
	 * This method updates subsection summary table with document status for
	 * Service
	 * 
	 * @param asAppId Application id
	 * @param asOrgId Orgnanization id
	 * @param asServiceAppId service application id
	 * @param asUserId user id
	 * @param asSection section name
	 * @param aoMyBatisSession sql session
	 * @param aoApplicationDAO Dao object
	 * @param aoMapDocTypeInfo Map as input
	 * @return lbInsertUpdateDocStatusService status
	 * @throws ApplicationException
	 */
	private Boolean insertUpdateDocStatusService(String asAppId, String asOrgId, String asServiceAppId,
			String asUserId, String asSection, SqlSession aoMyBatisSession, ApplicationDAO aoApplicationDAO,
			Map<String, Object> aoMapDocTypeInfo) throws ApplicationException
	{
		Boolean lbInsertUpdateDocStatusService;
		aoMapDocTypeInfo.put("asAppId", asAppId);
		aoMapDocTypeInfo.put("asOrgId", asOrgId);
		aoMapDocTypeInfo.put("asServiceAppId", asServiceAppId);
		List<Document> loDocument = null;
		if (asAppId == null)
		{
			loDocument = (List<Document>) DAOUtil.masterDAO(aoMyBatisSession, aoMapDocTypeInfo,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "selectDocTypeInfoForOrg", "java.util.Map");
		}
		else if (asSection != null && asSection.equalsIgnoreCase("servicessummary"))
		{
			loDocument = (List<Document>) DAOUtil.masterDAO(aoMyBatisSession, aoMapDocTypeInfo,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "selectDocTypeInfoService", "java.util.Map");
		}
		else
		{
			loDocument = (List<Document>) DAOUtil.masterDAO(aoMyBatisSession, aoMapDocTypeInfo,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "selectDocTypeInfo", "java.util.Map");
		}
		boolean lbIsComplete = true;
		String lsStatus = ApplicationConstants.COMPLETED_STATE;
		int liStarted = 0;
		for (Document loDoc : loDocument)
		{
			if (loDoc.getStatus() == null || loDoc.getStatus().equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE))
			{
				lbIsComplete = false;
			}
			else
			{
				liStarted++;
			}
		}
		if (!lbIsComplete)
		{
			lsStatus = ApplicationConstants.NOT_STARTED_STATE;
		}
		if (liStarted > 0 && !lbIsComplete)
		{
			lsStatus = ApplicationConstants.DRAFT_STATE;
		}
		aoMapDocTypeInfo.put("asAppId", asAppId);
		aoMapDocTypeInfo.put("asOrgId", asOrgId);
		aoMapDocTypeInfo.put("asServiceAppId", asServiceAppId);
		aoMapDocTypeInfo.put("asSectonId", asSection);
		aoMapDocTypeInfo.put("lsStatus", lsStatus);
		aoMapDocTypeInfo.put("asUserId", asUserId);
		aoMapDocTypeInfo.put("modifiedDate", new Date(System.currentTimeMillis()));
		aoMapDocTypeInfo.put("loDate", new Date(System.currentTimeMillis()));
		aoMapDocTypeInfo.put("SUB_SECTION_ID", ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_DOCUMENTS);
		aoApplicationDAO.insertUpdateDocStatusService(aoMapDocTypeInfo, aoMyBatisSession);
		lbInsertUpdateDocStatusService = true;
		return lbInsertUpdateDocStatusService;
	}

	/**
	 * This method gets the list of document corresponding to an application of
	 * an organization
	 * 
	 * @param asAppID Application Id
	 * @param asOrgId Organization Id of logged-in user
	 * @param aoMyBatisSession MyBAtis Sql Session
	 * @return loDocDetails Document details
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<DocumentBean> getAssociatedDocs(String asAppID, String asOrgId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<DocumentBean> loDocDetails = null;
		loDocDetails = null;
		HashMap loHMWhereClause = new HashMap();
		loHMWhereClause.put("AppID", asAppID);
		loHMWhereClause.put("OrgID", asOrgId);
		loDocDetails = (List<DocumentBean>) DAOUtil.masterDAO(aoMyBatisSession, loHMWhereClause,
				ApplicationConstants.MAPPER_CLASS_APPLICATION, "documentDetails", "java.util.HashMap");
		String lsIsCeoName = null;
		String lsIsCfoName = null;
		loHMWhereClause.put("asOrgId", asOrgId);
		loHMWhereClause.put("asCeo", "1");
		loHMWhereClause.put("asCfo", "2");
		loHMWhereClause.put("asMemberStatus", ApplicationConstants.ACTIVE);
		loHMWhereClause.put("asIsActive", ApplicationConstants.SYSTEM_YES);
		lsIsCeoName = (String) DAOUtil.masterDAO(aoMyBatisSession, loHMWhereClause,
				ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getCeoName", "java.util.HashMap");
		lsIsCfoName = (String) DAOUtil.masterDAO(aoMyBatisSession, loHMWhereClause,
				ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getCfoName", "java.util.HashMap");
		if (lsIsCeoName != null)
		{
			for (DocumentBean loDocument : loDocDetails)
			{
				if (loDocument.getDocType().equalsIgnoreCase(ApplicationConstants.CEO_NAME))
				{
					loDocument.setMbIsCeoName(true);
					loDocument.setMsCeoName(lsIsCeoName);
				}
			}
		}
		if (lsIsCfoName != null)
		{
			for (DocumentBean loDocument : loDocDetails)
			{
				if (loDocument.getDocType().equalsIgnoreCase(ApplicationConstants.CFO_NAME))
				{
					loDocument.setMbIsCfoName(true);
					loDocument.setMsCfoName(lsIsCfoName);
				}
			}
		}
		Integer liIsCeo = -1;
		liIsCeo = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHMWhereClause,
				ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getCeoCount", "java.util.HashMap");
		if (liIsCeo != 0)
		{
			for (DocumentBean loDocument : loDocDetails)
			{
				if (loDocument.getDocType().equalsIgnoreCase(ApplicationConstants.CEO_NAME))
				{
					loDocument.setMbIsCeoActive(true);
				}
			}
		}
		return loDocDetails;
	}

	/**
	 * @param asAppID Application Id
	 * @param asOrgId Organization Id
	 * @param aoMyBatisSession sql session
	 * @return loDocDetails Document details
	 * @throws ApplicationException
	 */
	public Boolean getIsActiveCeoForService(String asAppID, String asOrgId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean loGetIsActiveCeoForService = false;
		HashMap loHMWhereClause = new HashMap();
		loHMWhereClause.put("AppID", asAppID);
		loHMWhereClause.put("OrgID", asOrgId);
		loHMWhereClause.put("asOrgId", asOrgId);
		loHMWhereClause.put("asCeo", "1");
		loHMWhereClause.put("asCfo", "2");
		loHMWhereClause.put("asMemberStatus", ApplicationConstants.ACTIVE);
		loHMWhereClause.put("asIsActive", ApplicationConstants.SYSTEM_YES);
		Integer loIsCeo = -1;
		loIsCeo = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loHMWhereClause,
				ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getCeoCount", "java.util.HashMap");
		if (loIsCeo != 0)
		{
			loGetIsActiveCeoForService = true;
		}
		return loGetIsActiveCeoForService;
	}

	/**
	 * This method gets the questions from database by providing the Application
	 * ID
	 * 
	 * @param asAppID Application Id
	 * @param asOrgId Organization Id of logged-in user
	 * @param aoMyBatisSession MyBatis Sql session
	 * @return loSubSectionDetails Subsection details
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<SubSectionBean> getSubSectionDetails(String asAppID, String asOrgId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<SubSectionBean> loSubSectionDetails = null;
		HashMap loHmWhereClause = new HashMap();
		loHmWhereClause.put("AppID", asAppID);
		loHmWhereClause.put("OrgID", asOrgId);
		loSubSectionDetails = (List<SubSectionBean>) DAOUtil.masterDAO(aoMyBatisSession, loHmWhereClause,
				ApplicationConstants.MAPPER_CLASS_APPLICATION, "subSectionDetails", "java.util.HashMap");
		return loSubSectionDetails;
	}

	/**
	 * This method gets comments for print in final view
	 * 
	 * @param asAppId Application ID
	 * @param asOrgId Organization Id of logged-in user
	 * @param abIsFinalView Flag for Final view
	 * @param aoMyBatisSession MyBatis Sql Session
	 * @return loSectionMap Map containing section details
	 * @throws ApplicationException
	 */
	public Map<String, StringBuffer> printerFriendlyGetComments(String asAppId, String asOrgId, Boolean abIsFinalView,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside :: printerFriendlyGetComments :: start");
		Map<String, StringBuffer> loSectionMap = new HashMap<String, StringBuffer>();
		try
		{
			if (abIsFinalView)
			{
				Map<String, String> loMapQuery = new HashMap<String, String>();
				loMapQuery.put("appid", asAppId);
				loMapQuery.put("providerComments", P8Constants.PROPERTY_PE_TH_PROVIDER_COMMENT);
				List<ApplicationAuditBean> loAuditBeanList = (List<ApplicationAuditBean>) DAOUtil.masterDAO(
						aoMyBatisSession, loMapQuery, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"fetchLastProviderComments", "java.util.Map");
				for (ApplicationAuditBean loAuditBean : loAuditBeanList)
				{
					StringBuffer loSBFinalData = null;
					String lsSectionId = loAuditBean.getMsSectionId();
					if (loSectionMap.containsKey(lsSectionId))
					{
						loSBFinalData = loSectionMap.get(lsSectionId);
						loSBFinalData.append("<br>---------------------------------------<br>");
					}
					else
					{
						loSBFinalData = new StringBuffer();
						loSBFinalData.append("<div class='commentHeading'>");
						loSBFinalData.append(BusinessApplicationUtil.toTitleCase(lsSectionId));
						loSBFinalData.append(" Comments</div>");
					}
					String lsDate = DateUtil.getDateMMddYYYYFormat(loAuditBean.getMsAuditDate());
					loSBFinalData.append("<div class='commentBody'><b>");
					loSBFinalData.append("Accelerator");
					loSBFinalData.append(" - ");
					loSBFinalData.append(lsDate);
					loSBFinalData.append(":");
					loSBFinalData.append("</b><div>");
					loSBFinalData.append(loAuditBean.getMsData());
					loSBFinalData.append("</div></div>");
					loSectionMap.put(lsSectionId, loSBFinalData);
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("error", "Error occured while getting comments for printer friendly view");
			aoAppEx.addContextData("org id", asOrgId);
			setMoState("Error occured while getting comments for printer friendly view in printerFriendlyGetComments");
			throw aoAppEx;
		}
		setMoState("Success : printerFriendlyGetComments");
		LOG_OBJECT.Debug("Inside :: printerFriendlyGetComments :: end");
		return loSectionMap;
	}

	/**
	 * This method creates printer friendly view for business application
	 * 
	 * @param asAppId Application Id
	 * @param asOrgId Organization Id of logged-in user
	 * @param asUserId User Id of logged in User
	 * @param asWebContentPath WebContent Folder URL
	 * @param aoMyBatisSession MyBatis Sql Session
	 * @return loMap map containing Printerfriendly data
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> printerFriendly(String asAppId, String asOrgId, String asWebContentPath,
			Boolean abIsFinalView, Map<String, StringBuffer> aoPrinterFriendlyComments, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside :: printerFriendly :: start");
		String lsPrinterFriendlyContent = "";
		Map<String, String> loMap = null;
		try
		{
			ApplicationDAO loApplicationDAO = new ApplicationDAO();
			Map<String, PrintContentBean> loPrintBeanMap = new HashMap<String, PrintContentBean>();
			Map<String, String> loMapDetails = new HashMap<String, String>();
			loMapDetails.put(ApplicationConstants.ORG_ID, asOrgId);
			loMapDetails.put(ApplicationConstants.APP_ID, asAppId);
			loMapDetails.put(ApplicationConstants.BUSINESS_APP_ID, asAppId);
			List<Population> loPopulationBeanList = null;
			List<Map<String, String>> loLangList = null;
			List<Map<String, String>> loGeoList = null;
			for (String lsSectionName : ApplicationConstants.SECTION_NAMES)
			{
				PrintContentBean loPrintBean = new PrintContentBean();
				String lsTableName = PropertyUtil.getTableName(lsSectionName);
				QuestionAnswerDAO loQuestionAnswerDAO = new QuestionAnswerDAO();
				LOG_OBJECT.Debug("Inside :: printerFriendly :: getting form data");
				Map<String, Object> loFormInformation = loQuestionAnswerDAO.getFormData(aoMyBatisSession, asAppId,
						asOrgId, lsTableName);
				loPrintBean.setMoFormContent(loFormInformation);

				Map<String, String> loMapQuery = new HashMap<String, String>();
				if (!lsSectionName.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_POLICIES))
				{
					loMapQuery.put("asOrgId", asOrgId);
					loMapQuery.put("asAppId", asAppId);
					loMapQuery.put("lsSectionName", lsSectionName);
					LOG_OBJECT.Debug("Inside :: printerFriendly :: getting form document list");
					List<DocumentBean> loDocContent = (List<DocumentBean>) DAOUtil.masterDAO(aoMyBatisSession,
							loMapQuery, ApplicationConstants.MAPPER_CLASS_APPLICATION, "getDocumentList",
							"java.util.Map");

					loPrintBean.setMoDocContent(loDocContent);
				}
				loPrintBeanMap.put(lsSectionName, loPrintBean);
			}
			Map<String, String> loMapRequiredDetails = new HashMap<String, String>();
			loMapRequiredDetails.put(ApplicationConstants.ORG_ID, asOrgId);
			LOG_OBJECT.Debug("Inside :: printerFriendly :: getting lang/population/geogragpy");
			loPopulationBeanList = (List<Population>) DAOUtil.masterDAO(aoMyBatisSession, loMapRequiredDetails,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getPopulationForPrint", "java.util.Map");
			loLangList = loApplicationDAO.getLangGeoForPrint(asOrgId, true, aoMyBatisSession);
			loGeoList = loApplicationDAO.getLangGeoForPrint(asOrgId, false, aoMyBatisSession);
			lsPrinterFriendlyContent = BusinessApplicationUtil.createPrintableView(loPrintBeanMap,
					loPopulationBeanList, loLangList, loGeoList, asWebContentPath, aoPrinterFriendlyComments,
					abIsFinalView);
			LOG_OBJECT.Debug("Inside :: printerFriendly :: getting org related data");
			loMap = (Map<String, String>) DAOUtil.masterDAO(aoMyBatisSession, loMapDetails,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getOrgDetailsPrint", "java.util.Map");

		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("error", "Error occured while creating printer friendly view");
			aoAppEx.addContextData("org id", asOrgId);
			aoAppEx.addContextData("Content Path", asWebContentPath);
			setMoState("Error occured while creating printer friendly view in printerFriendly method");
			throw aoAppEx;
		}
		loMap.put("data", lsPrinterFriendlyContent);
		setMoState("Success : printer friendly view created");
		LOG_OBJECT.Debug("Inside :: printerFriendly :: end");
		return loMap;
	}

	/**
	 * This method gets the service search results
	 * 
	 * @param asData Search Parameter data
	 * @param aoMyBatisSession MyBatis Sql Session
	 * @return List containing search results output
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<TaxonomyTree> getSearchResultService(String asData, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Map<String, String> loMap = new HashMap<String, String>();
		loMap.put("asData", "%" + asData.toLowerCase() + "%");
		return (List<TaxonomyTree>) DAOUtil.masterDAO(aoMyBatisSession, loMap,
				ApplicationConstants.MAPPER_CLASS_APPLICATION, "getSearchResultService", "java.util.Map");
	}

	/**
	 * This method creates printer friendly view for a service
	 * 
	 * @param asOrgId Organization Id for logged-in user
	 * @param asServiceId Service Application ID for Organization
	 * @param aoMyBatisSession MyBatis Sql session
	 * @return loMap Map containing Service data for printer friendly view
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getServiceDataForPrint(String asOrgId, String asServiceId, Boolean abIsFinalView,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		LOG_OBJECT.Debug("Inside :: getServiceDataForPrint :: start");
		StringBuffer loSBPrinterFriendlyContent = new StringBuffer();
		Map<String, String> loMap = new HashMap<String, String>();
		try
		{
			Map<String, String> loMapDetails = new HashMap<String, String>();
			loMapDetails.put(ApplicationConstants.ORG_ID, asOrgId);
			loMapDetails.put(ApplicationConstants.SERVICE_ELEMENT_ID, asServiceId);
			LOG_OBJECT.Debug("Inside :: getServiceDataForPrint :: getting service detail");
			List<Map<String, String>> loListService = (List<Map<String, String>>) DAOUtil.masterDAO(aoMyBatisSession,
					loMapDetails, ApplicationConstants.MAPPER_CLASS_APPLICATION, "getServiceDetailForPrint",
					"java.util.Map");
			if (!loListService.isEmpty())
			{
				String lsBussAppId = loListService.get(0).get(ApplicationConstants.BUSINESS_APPID_STRING);
				loMapDetails.put(ApplicationConstants.BUSINESS_APP_ID, lsBussAppId);
				loMap = getServiceDataForPrint(asOrgId, lsBussAppId, abIsFinalView, aoMyBatisSession,
						loSBPrinterFriendlyContent, loMapDetails, loListService.get(0));
			}
			else
			{
				loMap.put("error", "No Data found");
			}
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("error", "Error occured while creating printer friendly view");
			setMoState("Fail: getServiceDataForPrint > Error occured while creating printer friendly view");
			throw aoAppEx;
		}
		LOG_OBJECT.Debug("Inside :: getServiceDataForPrint :: end");
		return loMap;
	}

	/**
	 * This method creates printer friendly view for a service
	 * 
	 * @param asOrgId Organization id
	 * @param asBussAppId business application id
	 * @param abIsFinalView is final view or not
	 * @param aoMyBatisSession sql session
	 * @param asbPrinterFriendlyContent content for printer friendly
	 * @param aoMapDetails map as input
	 * @param aoServiceDetail service details
	 * @return loMap map as input
	 * @throws ApplicationException
	 */
	private Map<String, String> getServiceDataForPrint(String asOrgId, String asBussAppId, Boolean abIsFinalView,
			SqlSession aoMyBatisSession, StringBuffer asbPrinterFriendlyContent, Map<String, String> aoMapDetails,
			Map<String, String> aoServiceDetail) throws ApplicationException
	{
		Map<String, String> loMap;
		LOG_OBJECT.Debug("Inside :: getServiceDataForPrint :: getting org details for print");
		loMap = aoServiceDetail;

		String lsServiceEltId = aoServiceDetail.get("ELEMENT_ID");
		String lsServiceAppId = aoServiceDetail.get("SERVICE_APPLICATION_ID");
		org.jdom.Document loDoc = (org.jdom.Document) BaseCacheManagerWeb.getInstance().getCacheObject(
				ApplicationConstants.TAXONOMY_ELEMENT);
		String lsServiceName = BusinessApplicationUtil.getTaxonomyName(lsServiceEltId, loDoc);
		if (null != lsServiceEltId
				&& (null == lsServiceName || lsServiceName.equalsIgnoreCase("") || lsServiceName.isEmpty()))
		{
			Map<String, String> loActionMap = new HashMap<String, String>();
			loActionMap.put("lsElementId", lsServiceEltId);
			ApplicationSummaryService loObj = new ApplicationSummaryService();
			lsServiceName = loObj.getDeletedServiceName(aoMyBatisSession, loActionMap);
		}
		aoServiceDetail.put(ApplicationConstants.ORG_ID, asOrgId);
		aoServiceDetail.put(ApplicationConstants.BUSINESS_APP_ID, asBussAppId);
		aoServiceDetail.put(ApplicationConstants.SERVICE_ELEMENT_ID, lsServiceEltId);
		aoServiceDetail.put(ApplicationConstants.SERVICE_APPLICATION_ID, lsServiceAppId);
		LOG_OBJECT.Debug("Inside :: getServiceDataForPrint :: getting service: Questions");
		ServiceQuestions loServiceQuestionsBean = (ServiceQuestions) DAOUtil.masterDAO(aoMyBatisSession,
				aoServiceDetail, ApplicationConstants.MAPPER_CLASS_APPLICATION, "getServiceQuestionDetail",
				"java.util.Map");
		LOG_OBJECT.Debug("Inside :: getServiceDataForPrint :: getting service: Document details");
		List<DocumentBean> loServiceDocumentList = (List<DocumentBean>) DAOUtil.masterDAO(aoMyBatisSession,
				aoServiceDetail, ApplicationConstants.MAPPER_CLASS_APPLICATION, "getServiceDocumentDetail",
				"java.util.Map");
		LOG_OBJECT.Debug("Inside :: getServiceDataForPrint :: getting service: Staff details");
		List<StaffDetails> loStaffDetailList = (List<StaffDetails>) DAOUtil.masterDAO(aoMyBatisSession,
				aoServiceDetail, ApplicationConstants.MAPPER_CLASS_APPLICATION, "getServiceStaffDetail",
				"java.util.Map");
		LOG_OBJECT.Debug("Inside :: getServiceDataForPrint :: getting service: contract details");
		List<ContractDetails> loContractDetailsList = (List<ContractDetails>) DAOUtil.masterDAO(aoMyBatisSession,
				aoServiceDetail, ApplicationConstants.MAPPER_CLASS_APPLICATION, "getServiceContractDetail",
				"java.util.Map");
		LOG_OBJECT.Debug("Inside :: getServiceDataForPrint :: getting service: service setting");
		List<ServiceSettingBean> loServiceSettingList = (List<ServiceSettingBean>) DAOUtil.masterDAO(aoMyBatisSession,
				aoServiceDetail, ApplicationConstants.MAPPER_CLASS_APPLICATION, "getServiceSettingDetail",
				"java.util.Map");
		asbPrinterFriendlyContent.append(BusinessApplicationUtil.createServicePrintableView(loServiceQuestionsBean,
				loServiceDocumentList, loStaffDetailList, loContractDetailsList, loServiceSettingList, lsServiceName,
				abIsFinalView, loDoc));
		loMap.put("data", asbPrinterFriendlyContent.toString());
		return loMap;
	}

	/**
	 * This method gets the complete status map of business application
	 * 
	 * @param asOrgId Organization Id of logged in User
	 * @param asAppId Application Id
	 * @param aoMyBatisSession MyBatis Sql session
	 * @return Map containing Service data for printer friendly view
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, StatusBean> getStatusMapForBusinessApp(String asOrgId, String asAppId,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		Map<String, String> loStatusMap = new HashMap<String, String>();
		Map<String, StatusBean> loStatusBeanMap = new HashMap<String, StatusBean>();
		try
		{
			loStatusMap.put("asOrgId", asOrgId);
			loStatusMap.put("asAppId", asAppId);
			loStatusMap.put("asBasic", ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS);
			List<SubSectionBean> loSubSectionList = (List<SubSectionBean>) DAOUtil.masterDAO(aoMyBatisSession,
					loStatusMap, ApplicationConstants.MAPPER_CLASS_APPLICATION, "getStatusMapForQueDoc",
					"java.util.Map");
			for (SubSectionBean loSubSec : loSubSectionList)
			{
				getStatusMapForBusinessApp(loStatusBeanMap, loSubSec);
			}
			BusinessApplicationUtil.getCompleteStatusMap(loStatusBeanMap);
			return loStatusBeanMap;
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loStatusMap);
			aoAppEx.addContextData("error", " fails to get status of the BusinessApp");
			setMoState("Fail: getStatusMapForBusinessApp method fails to get status of the BusinessApp");
			throw aoAppEx;
		}
	}

	/**
	 * This method gets the complete status map of business application
	 * 
	 * @param aoStatusBeanMap status map as input
	 * @param aoSubSec Sub section bean
	 */
	private void getStatusMapForBusinessApp(Map<String, StatusBean> aoStatusBeanMap, SubSectionBean aoSubSec)
	{
		String lsSectionId = aoSubSec.getSectionId();
		String lsStatus = "";
		if (aoStatusBeanMap.containsKey(lsSectionId))
		{
			StatusBean loStatusBean = aoStatusBeanMap.get(lsSectionId);
			Map<String, String> loHMMapStatus = loStatusBean.getMoHMSubSectionDetails();
			Map<String, String> loHMMapStatusToDisplay = loStatusBean.getMoHMSubSectionDetailsToDisplay();
			lsStatus = aoSubSec.getSubSectionStatus();
			if (null != lsStatus && !lsStatus.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE))
			{
				loHMMapStatusToDisplay.put(aoSubSec.getSubSectionID(), lsStatus);
			}
			else
			{
				loHMMapStatusToDisplay.put(aoSubSec.getSubSectionID(), ApplicationConstants.PARTIALLY_COMPLETE_STATE);
			}
			if (lsStatus != null
					&& !(lsStatus.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE)
							|| lsStatus.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE) || lsStatus
							.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE)))
			{
				lsStatus = ApplicationConstants.COMPLETED_STATE;
			}
			if (lsStatus != null)
			{
				loHMMapStatus.put(aoSubSec.getSubSectionID(), lsStatus.toLowerCase().replace(" ", ""));
			}
		}
		else
		{
			StatusBean loStatusBean = new StatusBean();
			Map<String, String> loHMMapStatus = new HashMap<String, String>();
			Map<String, String> loHMMapStatusToDisplay = new HashMap<String, String>();
			lsStatus = aoSubSec.getSubSectionStatus();
			if (null != lsStatus && !lsStatus.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE))
			{
				loHMMapStatusToDisplay.put(aoSubSec.getSubSectionID(), lsStatus);
			}
			else
			{
				loHMMapStatusToDisplay.put(aoSubSec.getSubSectionID(), ApplicationConstants.PARTIALLY_COMPLETE_STATE);
			}
			if (lsStatus != null
					&& !(lsStatus.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE)
							|| lsStatus.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE) || lsStatus
							.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE)))
			{
				lsStatus = ApplicationConstants.COMPLETED_STATE;
			}
			if (lsStatus != null)
			{
				loHMMapStatus.put(aoSubSec.getSubSectionID(), lsStatus.toLowerCase().replace(" ", ""));
			}
			loStatusBean.setMoHMSubSectionDetails(loHMMapStatus);
			loStatusBean.setMoHMSubSectionDetailsToDisplay(loHMMapStatusToDisplay);
			aoStatusBeanMap.put(lsSectionId, loStatusBean);
		}
	}

	/**
	 * This method gets the complete status map for service Applications
	 * associated with a Business Application Id for an Organization
	 * 
	 * @param asBussAppId Business Application Id
	 * @param asOrgId Organization Id of logged-in user
	 * @param aoMyBatisSession MyBatis Sql session
	 * @return loStatusBeanMap Map containing status of services
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, StatusBean> getServiceApplicationsStatus(String asBussAppId, String asOrgId,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		Map<String, String> loStatusMap = new HashMap<String, String>();
		Map<String, StatusBean> loStatusBeanMap = new HashMap<String, StatusBean>();
		try
		{
			loStatusMap.put("asOrgId", asOrgId);
			loStatusMap.put("asBussAppId", asBussAppId);

			List<SubSectionBean> loSubSectionList = (List<SubSectionBean>) DAOUtil.masterDAO(aoMyBatisSession,
					loStatusMap, ApplicationConstants.MAPPER_CLASS_APPLICATION, "getServiceAppStatusMap",
					"java.util.Map");

			for (SubSectionBean loSubSec : loSubSectionList)
			{
				getServiceApplicationsStatus(loStatusBeanMap, loSubSec);
			}

		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("error",
					"Error while fetching data for Status in Application Service 'getServiceApplicationsStatus'");
			aoAppEx.setContextData(loStatusMap);
			setMoState("Fail: getServiceApplicationsStatus > Fail to get service application status");
			throw aoAppEx;
		}
		BusinessApplicationUtil.getCompleteStatusMapService(loStatusBeanMap);
		setMoState("Success: getServiceApplicationsStatus ");
		return loStatusBeanMap;
	}

	/**
	 * This method gets the complete status map for service Applications
	 * associated with a Business Application Id for an Organization
	 * 
	 * @param aoStatusBeanMap status bean map as input
	 * @param aoSubSec sub section bean as input
	 */
	private void getServiceApplicationsStatus(Map<String, StatusBean> aoStatusBeanMap, SubSectionBean aoSubSec)
	{
		String lsServiceAppId = aoSubSec.getServiceAppId();
		String lsStatus = "";
		if (aoStatusBeanMap.containsKey(lsServiceAppId))
		{
			StatusBean loStatusBean = aoStatusBeanMap.get(lsServiceAppId);
			Map<String, String> loHMMapStatus = loStatusBean.getMoHMSubSectionDetails();
			Map<String, String> loHMMapStatusToDisplay = loStatusBean.getMoHMSubSectionDetailsToDisplay();
			lsStatus = aoSubSec.getSubSectionStatus();
			if (!lsStatus.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE))
			{
				loHMMapStatusToDisplay.put(aoSubSec.getSubSectionID(), lsStatus);
			}
			else
			{
				loHMMapStatusToDisplay.put(aoSubSec.getSubSectionID(), ApplicationConstants.PARTIALLY_COMPLETE_STATE);
			}
			if (!(ApplicationConstants.NOT_STARTED_STATE.equalsIgnoreCase(lsStatus)
					|| ApplicationConstants.COMPLETED_STATE.equalsIgnoreCase(lsStatus) || ApplicationConstants.DRAFT_STATE
					.equalsIgnoreCase(lsStatus)))
			{
				lsStatus = ApplicationConstants.COMPLETED_STATE;
			}
			loHMMapStatus.put(aoSubSec.getSubSectionID(), lsStatus.toLowerCase().replace(" ", ""));
		}
		else
		{
			StatusBean loStatusBean = new StatusBean();
			Map<String, String> loHMMapStatus = new HashMap<String, String>();
			Map<String, String> loHMMapStatusToDisplay = new HashMap<String, String>();
			lsStatus = aoSubSec.getSubSectionStatus();
			if (lsStatus == null || lsStatus.trim().length() <= 0)
			{
				lsStatus = ApplicationConstants.NOT_STARTED_STATE;
			}
			if (!lsStatus.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE))
			{
				loHMMapStatusToDisplay.put(aoSubSec.getSubSectionID(), lsStatus);
			}
			else
			{
				loHMMapStatusToDisplay.put(aoSubSec.getSubSectionID(), ApplicationConstants.PARTIALLY_COMPLETE_STATE);
			}
			if (null == lsStatus)
			{
				lsStatus = ApplicationConstants.NOT_STARTED_STATE;
			}
			else if (!(ApplicationConstants.NOT_STARTED_STATE.equalsIgnoreCase(lsStatus)
					|| ApplicationConstants.COMPLETED_STATE.equalsIgnoreCase(lsStatus) || ApplicationConstants.DRAFT_STATE
					.equalsIgnoreCase(lsStatus)))
			{
				lsStatus = ApplicationConstants.COMPLETED_STATE;
			}
			loHMMapStatus.put(aoSubSec.getSubSectionID(), lsStatus.toLowerCase().replace(" ", ""));
			loStatusBean.setMoHMSubSectionDetails(loHMMapStatus);
			loStatusBean.setMoHMSubSectionDetailsToDisplay(loHMMapStatusToDisplay);
			aoStatusBeanMap.put(lsServiceAppId, loStatusBean);
		}
	}

	/**
	 * The method updated for for Release 3.10.0 : Enhancement 6572 
	 * This method gets the complete status of Business Application and Service
	 * Application. This status determines if the application can be submitted
	 * or not.
	 * 
	 * @param aoBusinessAppM Map containing complete status of Business
	 *            Application
	 * @param aoServiceAppM Map containing complete status of Service
	 *            Application
	 * @return lbAppStatus Status flag if application can be submitted or not
	 * @throws ApplicationException
	 */
	public Map<String, Boolean> applicationSubmissionStatus(String asBussAppId, String asOrgId, String asUserId,
			String asServiceId, Map<String, StatusBean> aoBusinessAppM, Map<String, StatusBean> aoServiceAppM,
			String asSection, SqlSession aoMyBatisSession) throws ApplicationException
	{

		Map<String, Object> loSectionMap = new HashMap<String, Object>();
		loSectionMap.put("asBussAppId", asBussAppId);
		loSectionMap.put("asOrgId", asOrgId);
		if (null != asUserId && null != asSection && asSection.length() > 0
				&& (aoBusinessAppM.containsKey(asSection) || aoServiceAppM.containsKey(asSection)))
		{
			calculateAndInsertUpdate(asBussAppId, asUserId, asServiceId, aoBusinessAppM, aoServiceAppM, asSection,
					aoMyBatisSession, loSectionMap);
		}
		List<Map<String, String>> loSectionList = (List<Map<String, String>>) DAOUtil.masterDAO(aoMyBatisSession,
				loSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION, "selectSectionStatus", "java.util.Map");
		if (!loSectionList.isEmpty())
		{
			calculateFinalSectionStatusToDisplay(aoBusinessAppM, aoServiceAppM, loSectionList);
		}
		Map<String, Boolean> loFilledStatusMap = new HashMap<String, Boolean>();
		Boolean lbAppStatus = true;
		for (Entry<String, StatusBean> loEntry : aoBusinessAppM.entrySet())
		{
			StatusBean loStatusBean = loEntry.getValue();
			// Start of changes for Release 3.10.0 : Enhancement 6572
			if (!(loStatusBean != null && loStatusBean.getMsSectionStatusToDisplay() != null
					&& loStatusBean.getMsSectionStatus() != null && (loStatusBean.getMsSectionStatus()
					.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE) || (loStatusBean
					.getMsSectionStatusToDisplay().equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS) && loStatusBean
					.getMsSectionStatus().equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE)) || (loStatusBean
					.getMsSectionStatusToDisplay().equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED) && loStatusBean
					.getMsSectionStatus().equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE) ))))
			{
				loFilledStatusMap.put("businessApplication", false);
				lbAppStatus = false;
				break;
			}
			else
			{
				loFilledStatusMap.put("businessApplication", true);
			}
			// End of changes for Release 3.10.0 : Enhancement 6572
		}
		if (lbAppStatus && !aoServiceAppM.isEmpty())
		{
			for (Entry<String, StatusBean> lsKey : aoServiceAppM.entrySet())
			{
				StatusBean loStatusBean = lsKey.getValue();
				if (!(loStatusBean != null && loStatusBean.getMsSectionStatusToDisplay() != null
						&& loStatusBean.getMsSectionStatus() != null && (loStatusBean.getMsSectionStatus()
						.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE) || (loStatusBean
						.getMsSectionStatusToDisplay().equalsIgnoreCase(
								ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS) && loStatusBean
						.getMsSectionStatus().equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE)))))
				{
					loFilledStatusMap.put(lsKey.getKey(), false);
					lbAppStatus = false;
				}
				else
				{
					loFilledStatusMap.put(lsKey.getKey(), true);
				}
			}
		}
		else
		{
			loFilledStatusMap.put("businessApplication", false);
			lbAppStatus = false;
		}
		loFilledStatusMap.put("completeStatus", lbAppStatus);
		return loFilledStatusMap;
	}

	/**
	 * This method gets the complete status of Business Application and Service
	 * Application. This status determines if the application can be submitted
	 * or not.
	 * 
	 * @param aoBusinessAppM business map as input
	 * @param aoServiceAppM service map as input
	 * @param aoSectionList sectoin list map
	 */
	private void calculateFinalSectionStatusToDisplay(Map<String, StatusBean> aoBusinessAppM,
			Map<String, StatusBean> aoServiceAppM, List<Map<String, String>> aoSectionList)
	{
		List<String> loSectionsUpdated = new ArrayList<String>();
		for (Map<String, String> loMapSection : aoSectionList)
		{
			if (loMapSection != null)
			{
				String lsSection = loMapSection.get("SECTION_ID");
				String lsSectionStatus = loMapSection.get("SECTION_STATUS");
				String lsSectionSuperStatus = loMapSection.get("SUPER_STATUS");
				StatusBean loBean = null;

				if (aoBusinessAppM.containsKey(lsSection))
				{
					loBean = aoBusinessAppM.get(lsSection);
				}
				else
				{
					loBean = aoServiceAppM.get(lsSection);
				}
				if (loBean != null && !loSectionsUpdated.contains(lsSection))
				{
					if (lsSectionSuperStatus == null)
					{
						if (!lsSectionStatus.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE))
						{
							loBean.setMsSectionStatusToDisplay(lsSectionStatus);
							loBean.setMsSectionStatusOnInnerSummary(lsSectionStatus);
						}
						else
						{
							loBean.setMsSectionStatusToDisplay(ApplicationConstants.PARTIALLY_COMPLETE_STATE);
							loBean.setMsSectionStatusOnInnerSummary(ApplicationConstants.PARTIALLY_COMPLETE_STATE);
						}
					}
					else
					{
						loBean.setMsSectionStatusToDisplay(lsSectionSuperStatus);
						if (!lsSectionStatus.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE))
						{
							loBean.setMsSectionStatusOnInnerSummary(lsSectionStatus);
						}
						else
						{
							loBean.setMsSectionStatusOnInnerSummary(ApplicationConstants.PARTIALLY_COMPLETE_STATE);
						}
					}
					loSectionsUpdated.add(lsSection);
				}
			}
		}
	}

	/**
	 * This method gets the complete status of Business Application and Service
	 * Application. This status determines if the application can be submitted
	 * or not.
	 * 
	 * @param asBussAppId business application id
	 * @param asUserId user id
	 * @param asServiceId service application id
	 * @param aoBusinessAppM business map input
	 * @param aoServiceAppM service map as input
	 * @param asSection section name
	 * @param aoMyBatisSession sql session
	 * @param aoSectionMap section map as input
	 * @throws ApplicationException
	 * @throws NumberFormatException
	 */
	private void calculateAndInsertUpdate(String asBussAppId, String asUserId, String asServiceId,
			Map<String, StatusBean> aoBusinessAppM, Map<String, StatusBean> aoServiceAppM, String asSection,
			SqlSession aoMyBatisSession, Map<String, Object> aoSectionMap) throws ApplicationException,
			NumberFormatException
	{
		String lsStatus = null;
		aoSectionMap.put("asLockFlag", 0);
		aoSectionMap.put("asUserId", asUserId);
		List<String> loStatusList = new ArrayList<String>();
		loStatusList.add(ApplicationConstants.NOT_STARTED_STATE.toLowerCase());
		loStatusList.add(ApplicationConstants.COMPLETED_STATE.toLowerCase());
		loStatusList.add(ApplicationConstants.DRAFT_STATE.toLowerCase());
		boolean lbIsService = false;
		if (aoBusinessAppM.containsKey(asSection))
		{
			lsStatus = aoBusinessAppM.get(asSection).getMsSectionStatus();
		}
		else
		{
			lsStatus = aoServiceAppM.get(asSection).getMsSectionStatus();
			aoSectionMap.put("asServiceId", asServiceId);
			lbIsService = true;
		}
		aoSectionMap.put("asSection", asSection);
		aoSectionMap.put("asBussAppId", asBussAppId);
		aoSectionMap.put("status1", ApplicationConstants.NOT_STARTED_STATE.toLowerCase());
		aoSectionMap.put("status2", ApplicationConstants.COMPLETED_STATE.toLowerCase());
		aoSectionMap.put("status3", ApplicationConstants.DRAFT_STATE.toLowerCase());
		if (lsStatus != null)
		{
			insertUpdateSectionStatus(asUserId, aoBusinessAppM, aoMyBatisSession, aoSectionMap, lsStatus, lbIsService);
		}
	}

	/**
	 * This method gets the complete status of Business Application and Service
	 * Application. This status determines if the application can be submitted
	 * or not.
	 * 
	 * @param asUserId user id
	 * @param aoBusinessAppM business map
	 * @param aoMyBatisSession sql session
	 * @param aoSectionMap section map
	 * @param asStatus status
	 * @param abIsService is service or not
	 * @throws ApplicationException
	 * @throws NumberFormatException
	 */
	private void insertUpdateSectionStatus(String asUserId, Map<String, StatusBean> aoBusinessAppM,
			SqlSession aoMyBatisSession, Map<String, Object> aoSectionMap, String asStatus, boolean abIsService)
			throws ApplicationException, NumberFormatException
	{
		if (asStatus.equalsIgnoreCase(ApplicationConstants.NOT_STARTED_STATE.replace(" ", "")))
		{
			asStatus = ApplicationConstants.NOT_STARTED_STATE;
		}
		else if (asStatus.equalsIgnoreCase(ApplicationConstants.COMPLETED_STATE.replace(" ", "")))
		{
			asStatus = ApplicationConstants.COMPLETED_STATE;
		}
		else if (asStatus.equalsIgnoreCase(ApplicationConstants.DRAFT_STATE.replace(" ", "")))
		{
			asStatus = ApplicationConstants.DRAFT_STATE;
		}
		aoSectionMap.put("asStatus", asStatus);
		aoSectionMap.put("asUserId", asUserId);
		Object loCount = null;
		if (!abIsService)
		{
			loCount = DAOUtil.masterDAO(aoMyBatisSession, aoSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"updateSectionStatus", "java.util.Map");
			if (BusinessApplicationUtil.isBusinessApplicationComplete(aoBusinessAppM))
			{
				aoSectionMap.put("asStatusBApp", ApplicationConstants.COMPLETED_STATE);
			}
		}
		else
		{
			loCount = DAOUtil.masterDAO(aoMyBatisSession, aoSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"updateServiceAppStatus", "java.util.Map");
		}
		if (loCount != null && Integer.parseInt(loCount.toString()) <= 0)
		{
			if ((!abIsService && Integer.parseInt(DAOUtil.masterDAO(aoMyBatisSession, aoSectionMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "selectSectionStatusCount", "java.util.Map")
					.toString()) == 0))
			{
				aoSectionMap.put("asModifiedBy", asUserId);
				aoSectionMap.put("asSubmittedBy", asUserId);
				aoSectionMap.put("aoModifiedDate", new Date(System.currentTimeMillis()));
				aoSectionMap.put("aoSubmittedDate", new Date(System.currentTimeMillis()));
				DAOUtil.masterDAO(aoMyBatisSession, aoSectionMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertSectionStatus", "java.util.Map");
			}
		}
	}

	/**
	 * This method on creation of new Business Application fetches the data of
	 * existing service elements for an Organization from existing Business
	 * Application for same organization and makes entry in Service Application
	 * table corresponding to new Business Application ID
	 * 
	 * @param asPrevBAppId Business Application Id of Existing Business
	 *            Application
	 * @param asNewBAppId System generated Business Application Id for new
	 *            Business Application
	 * @param asNewAppId System generated Application Id for new Application
	 * @param asOrgId Organization Id of logged-in user
	 * @param asUserId User Id of logged-in user
	 * @param aoMyBatisSession MyBatis Sql session
	 * @return boolean status true for successful insertion
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public boolean reEnterExistingServices(String asPrevBAppId, String asNewBAppId, String asNewAppId, String asOrgId,
			String asUserId, SqlSession aoMyBatisSession) throws ApplicationException
	{
		boolean lbInsertStatus = false;
		if (null != asPrevBAppId)
		{
			Map<String, Object> loMapRequiredParam = new HashMap<String, Object>();
			long llTimeStamp = System.currentTimeMillis();
			// Set query parameters in map
			loMapRequiredParam.put("prevBappId", asPrevBAppId);
			loMapRequiredParam.put("newBappId", asNewBAppId);
			loMapRequiredParam.put("orgId", asOrgId);
			loMapRequiredParam.put("userId", asUserId);
			loMapRequiredParam.put("applicationId", asNewAppId);
			loMapRequiredParam.put("serviceStatus", ApplicationConstants.STATUS_APPROVED);
			List<String> loExistingServices = null;
			// Fetch element IDs of Service Applications in 'Approved' status
			// for existing Business Application
			loExistingServices = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, loMapRequiredParam,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getExistingServices", "java.util.Map");
			Iterator loListIterator = loExistingServices.iterator();
			Integer loCounter = 1;
			while (loListIterator.hasNext())
			{
				String lsServiceElemntId = null;
				String lsServiceApplicationId = null;
				lsServiceElemntId = (String) loListIterator.next();
				if (null != lsServiceElemntId)
				{
					// generate random ServiceApplicationId
					lsServiceApplicationId = "sr_" + llTimeStamp + loCounter;
					loCounter++;
					loMapRequiredParam.put("serviceElementId", lsServiceElemntId);
					loMapRequiredParam.put("serviceApplicationId", lsServiceApplicationId);
					loMapRequiredParam.put("modifiedDate", new Date(System.currentTimeMillis()));
					loMapRequiredParam.put("statusId", ApplicationConstants.ACTIVE);
					loMapRequiredParam.put("serviceStatus", ApplicationConstants.NOT_STARTED_STATE);
					// Insert existing Service Element Ids for new Business
					// Application ID in Service Application table
					DAOUtil.masterDAO(aoMyBatisSession, loMapRequiredParam,
							ApplicationConstants.MAPPER_CLASS_APPLICATION, "reEnterExistingServices", "java.util.Map");
					lbInsertStatus = true;
				}
			}
		}
		return lbInsertStatus;
	}

	/**
	 * This method gets the current Application status of a Business Application
	 * against Organization ID
	 * 
	 * @param asBussAppId Business Application Id
	 * @param asOrgId Organization Id
	 * @param aoMyBatisSession MyBatis Sql session
	 * @return Application Status
	 * @throws ApplicationException
	 */
	public String getBusinessApplicationStatus(String asOrgId, String asBussAppId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Map<String, String> loMapRequiredParam = new HashMap<String, String>();
		loMapRequiredParam.put("asBussAppId", asBussAppId);
		loMapRequiredParam.put("asOrgId", asOrgId);
		String lsBussAppStatus = null;
		if (asBussAppId == null || asBussAppId.length() <= 0)
		{
			Object loStatus = DAOUtil.masterDAO(aoMyBatisSession, loMapRequiredParam,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getBusinessAppStatusForGivenProvider",
					"java.util.Map");
			if (loStatus != null)
			{
				lsBussAppStatus = (String) loStatus;
			}
		}
		else
		{
			lsBussAppStatus = (String) DAOUtil.masterDAO(aoMyBatisSession, loMapRequiredParam,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "gettBusinessApplicationStatus", "java.util.Map");
		}
		
		return lsBussAppStatus;
	}

	/**
	 * This method gets the current Application status of a Service Application
	 * against Organization ID
	 * 
	 * @param asBussAppId Business Application Id
	 * @param asOrgId Organization Id
	 * @param aoMyBatisSession MyBatis Sql session
	 * @return Application Status
	 * @throws ApplicationException
	 */
	public String getServiceApplicationStatus(String asOrgId, String asBussAppId, String asServiceAppId,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		Map<String, String> loMapRequiredParam = new HashMap<String, String>();
		loMapRequiredParam.put("asBussAppId", asBussAppId);
		loMapRequiredParam.put("asOrgId", asOrgId);
		loMapRequiredParam.put("asServiceAppId", asServiceAppId);
		String lsBussAppStatus = null;
		if (asBussAppId == null || asBussAppId.length() <= 0)
		{
			Object loStatus = DAOUtil.masterDAO(aoMyBatisSession, loMapRequiredParam,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getBusinessAppStatusForGivenProvider",
					"java.util.Map");
			if (loStatus != null)
			{
				lsBussAppStatus = (String) loStatus;
			}
		}
		else
		{
			lsBussAppStatus = (String) DAOUtil.masterDAO(aoMyBatisSession, loMapRequiredParam,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getServiceApplicationStatus", "java.util.Map");
		}
		return lsBussAppStatus;
	}

	/**
	 * This method gets the final view data of business application ID/service
	 * application ID
	 * 
	 * @param asOrgId Organization Id
	 * @param asBussAppId Business Application Id
	 * @param asServiceAppId Service Application Id
	 * @param aoMyBatisSession MyBatis Sql session
	 * @return ID of the application for which final view has to be generated
	 * @throws ApplicationException
	 */
	public String getFinalViewData(String asOrgId, String asBussAppId, String asServiceAppId,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		Map<String, String> loMapRequiredParam = new HashMap<String, String>();
		loMapRequiredParam.put("asServiceAppId", asServiceAppId);
		loMapRequiredParam.put("asBussAppId", asBussAppId);
		loMapRequiredParam.put("asOrgId", asOrgId);
		if (asServiceAppId != null)
		{
			Object loOldServiceAppId = DAOUtil.masterDAO(aoMyBatisSession, loMapRequiredParam,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getOldServiceId", "java.util.Map");
			if (loOldServiceAppId != null)
			{
				return (String) loOldServiceAppId;
			}
			else
			{
				return asServiceAppId;
			}
		}
		return asBussAppId;
	}

	/**
	 * This Method get the WOB number from the database for an Application.
	 * 
	 * @param asOrgId Organization Id
	 * @param asAppId Application Id
	 * @param aoMyBatisSession MyBatis Sql session
	 * @return lsWobNo WOB number of application
	 * @throws ApplicationException
	 */
	public String getWobNo(String asOrgId, String asAppId, SqlSession aoMyBatisSession) throws ApplicationException
	{
		String lsWobNo = null;
		Map<String, String> loMapRequiredParam = new HashMap<String, String>();
		loMapRequiredParam.put("asOrgId", asOrgId);
		loMapRequiredParam.put("asAppId", asAppId);
		try
		{
			lsWobNo = (String) DAOUtil.masterDAO(aoMyBatisSession, loMapRequiredParam,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getWobNo", "java.util.Map");
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("error",
					"Error while fetching WoB no. for Application in Application Service 'getWobNo'");
			aoAppEx.setContextData(loMapRequiredParam);
			setMoState("Fail: getWobNo > Fail to get WoB No. for Application");
			throw aoAppEx;
		}
		return lsWobNo;
	}

	/**
	 * Gets the application setting data
	 * 
	 * @param aoMyBatisSession MyBatis Sql Session
	 * @return map of application setting data
	 * @throws ApplicationException
	 */
	public List<HashMap<String, String>> getApplicationSettingFromDB(SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		return (List<HashMap<String, String>>) DAOUtil.masterDAO(aoMyBatisSession, null,
				ApplicationConstants.MAPPER_CLASS_APPLICATION, "getApplicationSettingFromDB", null);
	}

	/**
	 * This method identifies if there exists any old Business application for
	 * organization that is Approved/Rejected
	 * 
	 * @param asOrgId Organization ID of the provider
	 * @param aoMyBatisSession MyBatis Sql Session
	 * @return true/false
	 * @throws ApplicationException
	 */
	public boolean getStatusForExistingApprovedRejected(String asOrgId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		boolean lbStatusforRejected = false;
		int liApplicationCount;
		liApplicationCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asOrgId,
				ApplicationConstants.MAPPER_CLASS_APPLICATION, "getStatusForExistingApprovedRejected",
				"java.lang.String");

		if (liApplicationCount > 0)
		{
			lbStatusforRejected = false;
		}
		else
		{
			lbStatusforRejected = true;
		}
		return lbStatusforRejected;
	}

	public List<Map<String, Object>> sectionStatusMap(String asBusAppId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<Map<String, Object>> loStatusMap = (List<Map<String, Object>>) DAOUtil.masterDAO(aoMyBatisSession,
				asBusAppId, ApplicationConstants.MAPPER_CLASS_APPLICATION, "sectionStatusMap", "java.lang.String");
		return loStatusMap;
	}

	public List<Map<String, Object>> subSectionStatusMap(String asBusAppId, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Map<String, Object> loMap = new HashMap<String, Object>();
		loMap.put("asBusAppId", asBusAppId);
		loMap.put("asServicessummary", "servicessummary");
		List<Map<String, Object>> loSubSecStatusMap = (List<Map<String, Object>>) DAOUtil.masterDAO(aoMyBatisSession,
				loMap, ApplicationConstants.MAPPER_CLASS_APPLICATION, "subSectionStatusMap", "java.util.Map");
		return loSubSecStatusMap;
	}

	public Map<String, List<Map<String, Object>>> subSectionStatusMapForDocument(String asBusAppId,
			SqlSession aoMyBatisSession) throws ApplicationException
	{
		Map<String, Object> loMap = new HashMap<String, Object>();
		loMap.put("asBusAppId", asBusAppId);
		loMap.put("asServicessummary", "servicessummary");
		String[] loSectionIds = ApplicationConstants.WORKFLOW_LAUNCH_SECTION_IDS_SEQUENCE;
		Map<String, List<Map<String, Object>>> loSubSecStatusMapForDocument = new HashMap<String, List<Map<String, Object>>>();
		for (String lsSectionName : loSectionIds)
		{
			loMap.put("asSection", lsSectionName);
			List<Map<String, Object>> loSubSecStatusMapForDocs = (List<Map<String, Object>>) DAOUtil.masterDAO(
					aoMyBatisSession, loMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"subSectionStatusMapForDocument", "java.util.Map");
			loSubSecStatusMapForDocument.put(lsSectionName, loSubSecStatusMapForDocs);
		}
		return loSubSecStatusMapForDocument;
	}

	/**
	 * This method is used to fetch procuring agencies list from database
	 * @param aoMybatisSession
	 * @return
	 * @throws ApplicationException
	 */
	public List<Map<String, String>> fetchProcuringNonProcuringAgenciesFromDB(SqlSession aoMybatisSession)
			throws ApplicationException
	{
		List<Map<String, String>> loProcuringAgencyMap = null;
		try
		{
			loProcuringAgencyMap = (List<Map<String, String>>) DAOUtil.masterDAO(aoMybatisSession, null,
					ApplicationConstants.MAPPER_CLASS_APPLICATION,
					ApplicationConstants.FETCH_PROCURING_NONPROCURING_AGENCIES, null);

		}
		catch (ApplicationException aoAppEx)
		{
			setMoState("Transaction Failed:: fetching procuring agencies from DB");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException("Error occured while adding lock for user", aoEx);
			setMoState("Transaction Failed:: fetching procuring agencies from DB");
			throw loAppEx;
		}
		return loProcuringAgencyMap;
	}

	/**
	 * The method added for for Release 3.10.0 : Enhancement 6572 
	 * This method is called when Corporate structure is changed from the Basic section
	 * @param aoMybatisSession
	 * @param asCorporateStructure
	 * @param asOrgId
	 * @param asAppId
	 * @param asSection
	 * @param asUserId
	 * @return
	 * @throws ApplicationException
	 */
	public Boolean onChangeCorporateStructure(SqlSession aoMybatisSession, String asCorporateStructure, String asOrgId,
			String asAppId, String asSection, String asUserId, String asDBCorporateStructure, DomStatus aoDomReturn, String aoAppStatus, String asAjaxCall) throws ApplicationException
	{
		Boolean lbStatus = true;
		Map<String, String> loUpdateSubSectionMap = null;
		String lsFilingStatus = null;
		try
		{
			if (asSection.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_BASICS)
					&& !aoDomReturn.isDomWithError() && asAjaxCall == null
					&& !asCorporateStructure.equalsIgnoreCase(asDBCorporateStructure))
			{
				int lsFilingSubSectionCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asAppId,
						ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, ApplicationConstants.GET_SUB_SECTION_STATUS_FILINGS,
						HHSConstants.JAVA_LANG_STRING);
				if (lsFilingSubSectionCount > 0)
				{
					lsFilingStatus = (String) DAOUtil.masterDAO(aoMybatisSession, asAppId,
							ApplicationConstants.MAPPER_CLASS_APPLICATION,
							ApplicationConstants.GET_SECTION_STATUS_FILING, ApplicationConstants.JAVA_LANG_STRING);
					loUpdateSubSectionMap = new HashMap<String, String>();
					loUpdateSubSectionMap.put(ApplicationConstants.APP_ID, asAppId);
					loUpdateSubSectionMap.put(ApplicationConstants.AS_USER_ID, asUserId);
					if (!(lsFilingStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS) || lsFilingStatus
									.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED)))
					{
						DAOUtil.masterDAO(aoMybatisSession, loUpdateSubSectionMap,
								ApplicationConstants.MAPPER_CLASS_APPLICATION,
								ApplicationConstants.UPDATE_SECTION_STATUS_FILING,
								ApplicationConstants.JAVA_UTIL_HASHMAP);
					}
					DAOUtil.masterDAO(aoMybatisSession, asAppId, ApplicationConstants.MAPPER_CLASS_APPLICATION,
							ApplicationConstants.DELETE_FILING_DOCS, ApplicationConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, asAppId, ApplicationConstants.MAPPER_CLASS_APPLICATION,
							ApplicationConstants.DELETE_FILING_INFO, ApplicationConstants.JAVA_LANG_STRING);
					DAOUtil.masterDAO(aoMybatisSession, loUpdateSubSectionMap,
							ApplicationConstants.MAPPER_CLASS_APPLICATION,
							ApplicationConstants.UPDATE_SUB_SECTION_SUMMARY, ApplicationConstants.JAVA_UTIL_HASHMAP);
					setMoState("Filings entry successfully deleted");
				}
			}

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ApplicationService: onChangeCorporateStructure method:: ", aoAppEx);
			setMoState("Error while deleting filings details on change of corporate structure");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured in ApplicationService: onChangeCorporateStructure method::", aoEx);
			setMoState("Error while deleting filings details on change of corporate structure");
			throw loAppEx;
		}
		return lbStatus;
	}
	
	/**
	 * The method added for for Release 3.10.0 : Enhancement 6572 
	 * This method gets the Changed Corporate structue value from DB.
	 * @param asOrgId
	 * @param aoMybatisSession
	 * @return
	 * @throws ApplicationException
	 */
	public String getCorporateStructure(String asOrgId, SqlSession aoMybatisSession) throws ApplicationException
	{
		String lsCorporateStructure = null;
		try
		{
			lsCorporateStructure = (String) DAOUtil.masterDAO(aoMybatisSession, asOrgId,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, ApplicationConstants.FETCH_CS_FROM_DB, ApplicationConstants.JAVA_LANG_STRING);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception occured in ApplicationService: getCorporateStructure method:: ", aoAppEx);
			setMoState("Error while fetching corporate structure");
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured in ApplicationService: getCorporateStructure method::", aoEx);
			setMoState("Error while fetching corporate structure");
			throw loAppEx;
		}
		return lsCorporateStructure;
	}

	
	/**
	 * This method pulls a list of status of provider, business application and filing.
	 * R6.1 - QC 6796 provider status 
	 * 
	 * @return  a list of status of provider, business application and filing
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	 public boolean pullStatusOfOrgApplicationFiling(SqlSession aoMybatisSession) throws ApplicationException {
			List<OrganizationStatusBean> orgStatusList = (List<OrganizationStatusBean>) DAOUtil.masterDAO(aoMybatisSession,  null,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, ApplicationConstants.PULL_LIST_OF_ORG_APP_FILING_STATUS, null);

			for(OrganizationStatusBean orgStatusBean : orgStatusList){
				try{
					if( 
 						orgStatusBean.expectedOrgStatus() == null || !orgStatusBean.needStatusInspection()  ){
						continue;
					}else{
						LOG_OBJECT.Info("Incorrect Provider status :" +orgStatusBean.toString() );

						HashMap<String, String> aoHashMap = new HashMap<String, String>();
						aoHashMap.put(HHSConstants.PROVIDER_ID_KEY , orgStatusBean.getOrgId());
						aoHashMap.put(HHSConstants.PROVIDER_STATUS_PARAM , orgStatusBean.expectedOrgStatus());
						Integer liUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoHashMap,
								ApplicationConstants.BATCH_NOTIFICATION_MAPPER_CLASS, HHSConstants.UPDATED_PROVIDER_STATUS,
								ApplicationConstants.JAVA_UTIL_MAP);

						if( liUpdateCount.intValue() != 1){
							LOG_OBJECT.Info("Unable to update Organization Status listed below \n"+ orgStatusBean.toString() + "\n" );
						}
					}
				}catch(ApplicationException aoAppEx){
					LOG_OBJECT.Error("Exception occured in ApplicationService: pullStatusOfOrgApplicationFiling method::\n" 
							+ orgStatusBean.toString() + "\n" );
					continue;
				}
				catch (Exception aoEx)
				{
					LOG_OBJECT.Error("Exception occured in ApplicationService: pullStatusOfOrgApplicationFiling method::\n" 
							+ orgStatusBean.toString() + "\n" );
					continue;
				}
			}

		 return true;
	 }

	
	/**
	 * This method reform a list of event 'Conditionally Approved' from superseeding table into a map
	 * R6.1 - QC 6796 provider status case 11 and case 14
	 * 
	 * @return  a map of status of event 'Conditionally Approved' from superseeding table
	 * @throws ApplicationException
	 */
	Map <String, OrganizationStatusBean> reformListOrgStatus(List<OrganizationStatusBean> orgStatusSkipList){
		HashMap <String, OrganizationStatusBean> orgSkip = new  HashMap <String, OrganizationStatusBean> ();
		
		if(orgStatusSkipList == null || orgStatusSkipList.isEmpty() ){
			return orgSkip;
		}

		for(OrganizationStatusBean orgStatBean : orgStatusSkipList){
			orgSkip.put(orgStatBean.getOrgId(), orgStatBean);
		}

		return orgSkip;
	}
	
	/**
	 * The class is added in R7 for getting preprocessing class for auto approval.
	 * It depends upon the tasktype
	 * @param aoMyBatisSession
	 * @param asTaskType
	 * @return
	 * @throws ApplicationException
	 */
	public String getPreprocessingClassFromApplicationSetting(SqlSession aoMyBatisSession, String asTaskType)
			throws ApplicationException
	{
		String lsPreProcessingClass = null;
		LOG_OBJECT.Info("Entering into getPreprocessingClassFromApplicationSetting with task type :: " + asTaskType);
		String lsReviewProcessId = (String) DAOUtil.masterDAO(aoMyBatisSession, asTaskType,
				ApplicationConstants.MAPPER_CLASS_APPLICATION, HHSR5Constants.GET_REVIEW_PROCESS_ID,
				ApplicationConstants.JAVA_LANG_STRING);
		LOG_OBJECT.Info("ID :::" + lsReviewProcessId);
		if (null != lsReviewProcessId && !lsReviewProcessId.isEmpty())
		{
			lsPreProcessingClass = (String) DAOUtil.masterDAO(aoMyBatisSession, lsReviewProcessId,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getPreprocessingClassFromApplicationSetting",
					ApplicationConstants.JAVA_LANG_STRING);
			LOG_OBJECT.Info("PROCESSING Class::: " + lsPreProcessingClass);
		}
		return lsPreProcessingClass;
	}	
	/** This method is added to display 'BA Status when tasktype is "Service application"' 	
	 * <ul>
	 * <li>Method Added in R7</li>	
	 * @param aoTaskDetailsList
	 * @param aoMyBatisSession
	 * @throws ApplicationException
	 */
	public ArrayList<TaskQueue> getBusinessApplicationStatus (ArrayList<TaskQueue> aoTaskDetailsList,SqlSession aoMyBatisSession) throws ApplicationException
	{
		String lsStatus = null;
		String lsApplicationId = null;
		String lsProviderId = null;
		try
		{
			for (Iterator liTaskItr = aoTaskDetailsList.iterator(); liTaskItr
					.hasNext();) {
				TaskQueue loTaskQueue = (TaskQueue) liTaskItr.next();
				lsApplicationId = loTaskQueue.getMsApplicationId();
				lsProviderId = loTaskQueue.getMsProviderId();
				if(StringUtils.isNotBlank(lsApplicationId) && StringUtils.isNotBlank(lsProviderId))
					lsStatus = getBusinessApplicationStatus(lsProviderId, lsApplicationId, aoMyBatisSession);
				//** start QC 9003 R 7.3.0 - add ability to sort by BA Status
				//** if lsStatus is null - put empty string in order to sort by BA status
				if(lsStatus == null)
					lsStatus = " ";
				//** end QC 9003 R 7.3.0- add ability to sort by BA Status
				loTaskQueue.setMsBaStatus(lsStatus);
			}
			
		}
		catch(ApplicationException aoExp)
		{
			LOG_OBJECT.Error("Exception occured in ApplicationService: getBusinessApplicationStatus method:: ", aoExp);
			setMoState("Error while fetching corporate structure");
			throw aoExp;
		}
		catch (Exception aoEx)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Exception occured in ApplicationService: getBusinessApplicationStatus method::", aoEx);
			setMoState("Error while fetching Business Status");
			throw loAppEx;
		}
		return aoTaskDetailsList;
	}
	
	/**
	 * Fix multi-tab Browsing QC6674 R7.1.0
	 * Fetch OrgId for the given BusinessApp
	 * @param aoMyBatisSession
	 * @param businessAppId
	 * @return
	 * @throws ApplicationException
	 */
	public String getOrgFromBusinessApp(SqlSession aoMyBatisSession, String businessAppId) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into getOrgFromBusinessApp for businessAppId: " + businessAppId);
		String orgId = "";
		
		if (businessAppId != null && !(businessAppId.isEmpty())) {
	
			try
			{
				orgId = (String) DAOUtil.masterDAO(aoMyBatisSession, businessAppId,
						HHSConstants.MAPPER_CLASS_APPLICATION, HHSConstants.GET_ORG_FROM_BUSINESS_APP,
						HHSConstants.JAVA_LANG_STRING);
			}
			// Application Exception handled here
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT.Error("Exception occured in ApplicationService:" + " getOrgFromBusinessApp method:: ",
						aoAppEx);
				setMoState("Transaction Failed:: ApplicationService: getOrgFromBusinessApp "
						+ "method - failed while fetching orgId from table Business_Application\n");
				throw aoAppEx;
			}
			
			// Exception handled here - May occur for any unpredictable situation
			catch (Exception aoEx)
			{
				setMoState("Transaction Failed:: ApplicationService: getOrgFromBusinessApp"
						+ " method - failed while fetching orgId from table Business_Application " + " \n");
				throw new ApplicationException("Exception occured while fetching orgId from table Business_Application :"
						+ " ApplicationService.getOrgFromBusinessApp", aoEx);
			}
		}
		
		return orgId;

	}
	// End: QC8691 R7.1.0 Tab Browsing
	
}

