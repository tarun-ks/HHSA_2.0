package com.nyc.hhs.daomanager.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.ibatis.session.SqlSession;
import org.jdom.Document;
import org.jdom.Element;

import com.accenture.factory.Validation;
import com.accenture.formtaglib.DomStatus;
import com.accenture.formtaglib.ErrorMessages;
import com.accenture.formtaglib.FileInformation;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.service.db.dao.QuestionAnswerDAO;
import com.nyc.hhs.util.BusinessApplicationUtil;
import com.nyc.hhs.util.PropertyUtil;
import com.nyc.hhs.util.XMLUtil;

/**
 * 
 * QuestionAnswerService: Service to insert update and pick data from database
 * for forms - Basic, board, filing, policies, organization profile
 * 
 */

public class QuestionAnswerService extends ServiceState
{
	private static final LogInfo LOG_OBJECT = new LogInfo(QuestionAnswerService.class);

	/**
	 * Gets data from database for a given Question(Label) and Element xml
	 * 
	 * @param asBuisAppId - Business application id of the application
	 * @param aoMyBatisSession - the my batis session
	 * @param asOrgId - the organization id of the current organization
	 * @param asTableName - the table name for corresponding form
	 * @return the form data in hasmap with key as column name
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public HashMap pickDataFromDb(String asBuisAppId, SqlSession aoMyBatisSession, String asOrgId, String asTableName)
			throws ApplicationException
	{
		HashMap loFormInformation = null;
		@SuppressWarnings("unchecked")
		Map<String, String> loHMWhereClause = new HashMap();
		try
		{
			loHMWhereClause.put("asOrgId", asOrgId);
			loHMWhereClause.put(ApplicationConstants.BUIZ_APP_ID, asBuisAppId);
			loHMWhereClause.put("TABLE", asTableName);
			QuestionAnswerDAO loQuestionAnswerDAO = new QuestionAnswerDAO();
			loFormInformation = loQuestionAnswerDAO.getFormInformation(aoMyBatisSession, loHMWhereClause);
			LOG_OBJECT.Debug("Created hashmap for Element name and Value pair from db: " + loFormInformation);
		}
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData((HashMap) loHMWhereClause);
			aoAppEx.addContextData("errorMsg",
					"Error occured while picking data from Db for a given Question and Element xml ");
			setMoState("Fail: QuestionAnswerService > pickDataFromDb - get question answers from db");
			throw aoAppEx;
		}
		setMoState("Success: QuestionAnswerService > pickDataFromDb - get question answers from db");
		return loFormInformation;
	}

	/**
	 * This method will return false if form has any error and true if no error
	 * 
	 * @param aoFormTemplate - jdom of the form template
	 * @param aoFormQuestionTemplate - jdom of the question template
	 * @param aoParameters - hashmap of the form data
	 * @param asUserRoles - current user role, used to validate data on role
	 *            basis
	 * @param asValidationClass - the custom validation class
	 * @param asFormName - the form name of the current form which needs to be
	 *            validated
	 * @param asFormVersion - the form version of the current form which needs
	 *            to be validated
	 * @param asUserRoleForDocument - String showing the role of user
	 * @return the domstatus with information about errors
	 * @throws ApplicationException
	 */
	private DomStatus validateAndCreateDom(Document aoFormTemplate, Document aoFormQuestionTemplate,
			HashMap<String, Object> aoParameters, String asUserRoles, String asValidationClass, String asFormName,
			String asFormVersion, String asUserRoleForDocument) throws ApplicationException
	{
		LOG_OBJECT.Debug("Private validateAndCreateDom start");
		DomStatus loDomReturn = new DomStatus();
		try
		{
			if (null == asValidationClass || asValidationClass.trim().length() <= 0)
			{
				asValidationClass = "com.accenture.formtaglib.FormBuilderValidation";
			}
			Class loClass = Class.forName(asValidationClass);
			Validation loValidateObj = (Validation) loClass.newInstance();
			ErrorMessages[] loErrorsBean = loValidateObj.validate(aoFormTemplate, aoFormQuestionTemplate, aoParameters,
					asUserRoles, asFormName, asFormVersion, asUserRoleForDocument);
			Element loRootElement = new Element("formtemplate");
			Document loDomObj = new Document(loRootElement);
			loDomReturn.setDomWithError(false);
			List<FileInformation> loFilesToUpload = new ArrayList<FileInformation>();
			Element loEle = loDomObj.getRootElement();
			boolean lbErrorOnPage = false;
			for (ErrorMessages loError : loErrorsBean)
			{
				if (null != loError.getCustomErrorMessage() && loError.getCustomErrorMessage().trim().length() > 0)
				{
					lbErrorOnPage = true;
					break;
				}
			}
			for (ErrorMessages loError : loErrorsBean)
			{
				iterateValidateAndCreateDom(aoParameters, loDomReturn, loFilesToUpload, loEle, lbErrorOnPage, loError);
			}
			loDomReturn.setDomObj(loDomObj);
			if (!loDomReturn.isDomWithError())
			{
				loDomReturn.setFilesToUpload(loFilesToUpload);
			}
		}
		catch (ApplicationException aoFbAppEx)
		{
			aoFbAppEx.addContextData("errorMsg", "Error occured while validating the answers for business application");
			setMoState("Fail: QuestionAnswerService > validateAndCreateDom - validating the answers for business application");
			throw aoFbAppEx;
		}
		catch (ClassNotFoundException aoClassNFEx)
		{
			throw new ApplicationException("Error occured while putting data in db :", aoClassNFEx);
		}
		catch (InstantiationException aoInsEx)
		{
			throw new ApplicationException("Error occured while putting data in db :", aoInsEx);
		}
		catch (IllegalAccessException aoIllExe)
		{
			throw new ApplicationException("Error occured while putting data in db :", aoIllExe);
		}
		LOG_OBJECT.Debug("Private validateAndCreateDom end");
		return loDomReturn;
	}

	/**
	 * This method iterate validate And create Dom object
	 * 
	 * @param aoParameters hashmap as input
	 * @param aoDomReturn dom as input
	 * @param aoFilesToUpload file information list
	 * @param aoElement element object
	 * @param abErrorOnPage is there any error on page
	 * @param aoError error message object
	 * @throws ApplicationException
	 */
	private void iterateValidateAndCreateDom(HashMap<String, Object> aoParameters, DomStatus aoDomReturn,
			List<FileInformation> aoFilesToUpload, Element aoElement, boolean abErrorOnPage, ErrorMessages aoError)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Private iterateValidateAndCreateDom start");
		Element loElementtoWrite = new Element(aoError.getSchemaname());
		loElementtoWrite.setAttribute("name", aoError.getParameterName());
		if (null != aoError.getCustomErrorMessage() && aoError.getCustomErrorMessage().trim().length() > 0)
		{
			loElementtoWrite.setAttribute("errorcode", aoError.getCustomErrorMessage());
			aoDomReturn.setDomWithError(true);
		}
		String lsSubmitedValue = null;
		FileInformation loFileInf = null;
		if (null != aoError.getHtmlElementType() && aoError.getHtmlElementType().equalsIgnoreCase("file"))
		{
			try
			{
				loFileInf = (FileInformation) aoParameters.get(aoError.getParameterName());
				aoFilesToUpload.add(loFileInf);
			}
			catch (Exception aoExc)
			{
				lsSubmitedValue = (String) aoParameters.get(aoError.getParameterName());
			}
		}
		else
		{
			lsSubmitedValue = (String) aoParameters.get(aoError.getParameterName());
		}

		if (null != lsSubmitedValue && !lsSubmitedValue.isEmpty())
		{
			loElementtoWrite.setAttribute(ApplicationConstants.VALUE, lsSubmitedValue);
		}
		else if (!abErrorOnPage && null != loFileInf && null != aoError.getHtmlElementType()
				&& aoError.getHtmlElementType().equalsIgnoreCase("file") && loFileInf.getFileName() != null)
		{
			loElementtoWrite.setAttribute(ApplicationConstants.VALUE, loFileInf.getFileName());
		}
		else
		{
			loElementtoWrite.setAttribute(ApplicationConstants.VALUE, ApplicationConstants.EMPTY_STRING);
		}

		boolean lbIsFieldReadOnlyAsBR = PropertyUtil.filedIsReadOnly(aoError.getParameterName());
		if (lbIsFieldReadOnlyAsBR)
		{
			loElementtoWrite.setAttribute("makeFieldReadOnly", "true");
		}
		else
		{
			loElementtoWrite.setAttribute("makeFieldReadOnly", "false");
		}
		aoElement.addContent(loElementtoWrite);
		LOG_OBJECT.Debug("Private iterateValidateAndCreateDom end");
	}

	/**
	 * It prepares data for db insertion and fires db insertion query
	 * 
	 * @param asQuestionPath - the file path of the question template
	 * @param asFormTemplatePath - the file path of the form template
	 * @param asBusinessRulePath - the file path of the business rules to
	 *            generate required document corresponding to a form
	 * @param aoParameters - hashmap of the form data
	 * @param asUserRoles - current user role, used to validate data on role
	 *            basis
	 * @param asValidationClass - the custom validation class
	 * @param asFormVersion - the form version of the current form which needs
	 *            to be validated
	 * @param asFormName - the form name of the current form which needs to be
	 *            validated
	 * @param asBuisAppId - Business application id of the application
	 * @param asUserID - the user id of current user submitting the form
	 * @param aoMyBatisSession - the my batis session
	 * @param asTableName - the table name for corresponding form
	 * @param asOrgId - the organization id of the current organization
	 * @param asSection - the section name of the current form which needs to be
	 *            validated and inserted/updated
	 * @param asAppStatus - status corresponding to current form to be updated
	 *            in database
	 * @param abSelectStatus - boolean value depicting to select CFO doc for
	 *            basic form
	 * @param asBussAppStatus - gets the current business application status
	 * @param asAjaxCall - String depicting if its an ajax call
	 * @param asUserRoleForDocument - String showing the role of user
	 * @return the domstatus with information about errors
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public DomStatus validateAndCreateDom(String asQuestionPath, String asFormTemplatePath, String asBusinessRulePath,
			HashMap<String, Object> aoParameters, String asUserRoles, String asValidationClass, String asFormVersion,
			String asFormName, String asBuisAppId, String asUserID, SqlSession aoMyBatisSession, String asTableName,
			String asOrgId, String asSection, String asAppStatus, Boolean abSelectStatus, String asBussAppStatus,
			String asAjaxCall, String asUserRoleForDocument) throws ApplicationException
	{
		LOG_OBJECT.Debug("Public validateAndCreateDom start");
		DomStatus loDomReturn = new DomStatus();
		boolean lbSkipService = false;
		lbSkipService = BusinessApplicationUtil.getBusinessApplicationReadOnlyStatus(asBussAppStatus);
		if (!lbSkipService || asBuisAppId == null)
		{
			HashMap<String, String> loColumnValueMap = new HashMap<String, String>();
			try
			{
				loDomReturn = validateDomData(asQuestionPath, asFormTemplatePath, asBusinessRulePath, aoParameters,
						asUserRoles, asValidationClass, asFormVersion, asFormName, asBuisAppId, asUserID,
						aoMyBatisSession, asTableName, asOrgId, asSection, asAppStatus, abSelectStatus,
						loColumnValueMap, asAjaxCall, asUserRoleForDocument);
			}
			catch (ApplicationException aoAppEx)
			{
				aoAppEx.setContextData(loColumnValueMap);
				aoAppEx.addContextData("asFormName", asFormName);
				aoAppEx.addContextData("asBuisAppId", asBuisAppId);
				aoAppEx.addContextData("asFormVersion", asFormVersion);
				aoAppEx.addContextData("asOrgId", asOrgId);
				aoAppEx.addContextData("errorMsg",
						"Error occured while inserting/updating question for business application");
				setMoState("Fail: QuestionAnswerService > validateAndCreateDom - set question answers into db");
				throw aoAppEx;
			}
			setMoState("Success: QuestionAnswerService > validateAndCreateDom - set question answers into db");
		}
		LOG_OBJECT.Debug("Public validateAndCreateDom end");
		return loDomReturn;
	}

	/**
	 * This method validate dom data.
	 * 
	 * @param asQuestionPath Question Path as input
	 * @param asFormTemplatePath Form Template Path as input
	 * @param asBusinessRulePath Business Rule Path as input
	 * @param aoParameters Parameters as input
	 * @param asUserRoles UserRoles as input
	 * @param asValidationClass ValidationClass as input
	 * @param asFormVersion Form Version
	 * @param asFormName Form Name
	 * @param asBuisAppId business application id
	 * @param asUserID user id
	 * @param aoMyBatisSession sql session
	 * @param asTableName table name
	 * @param asOrgId organization id
	 * @param asSection section name
	 * @param asAppStatus application status
	 * @param abSelectStatus select status
	 * @param aoColumnValueMap coulmn value map
	 * @param asAjaxCall - String depicting if its an ajax call
	 * @param asUserRoleForDocument - String showing the role of user
	 * @return
	 * @throws ApplicationException
	 */
	private DomStatus validateDomData(String asQuestionPath, String asFormTemplatePath, String asBusinessRulePath,
			HashMap<String, Object> aoParameters, String asUserRoles, String asValidationClass, String asFormVersion,
			String asFormName, String asBuisAppId, String asUserID, SqlSession aoMyBatisSession, String asTableName,
			String asOrgId, String asSection, String asAppStatus, Boolean abSelectStatus,
			HashMap<String, String> aoColumnValueMap, String asAjaxCall, String asUserRoleForDocument)
			throws ApplicationException
	{
		DomStatus loDomReturn;
		HashMap loData;
		Document loFormTemplate = XMLUtil.getDomObj(asFormTemplatePath);
		Document loQuestionDocToRead = XMLUtil.getDomObj(asQuestionPath);
		loDomReturn = validateAndCreateDom(loFormTemplate, loQuestionDocToRead, aoParameters, asUserRoles,
				asValidationClass, asFormName, asFormVersion, asUserRoleForDocument);
		if (!loDomReturn.isDomWithError())
		{
			Element loElementRoot = loFormTemplate.getRootElement();
			List<Element> loAllDivs = loElementRoot.getChildren();
			Iterator<Element> loItrDiv = loAllDivs.iterator();
			while (loItrDiv.hasNext())
			{
				Element loElementNode = loItrDiv.next();
				String lsElementName = loElementNode.getAttributeValue("name");
				String lsColumnName = loElementNode.getAttributeValue("schemaname");
				if (lsElementName == null || lsElementName.equalsIgnoreCase("globalproperty"))
				{
					continue;
				}
				String lsSubmitedValue = null;
				if (null != aoParameters.get(lsElementName))
				{
					lsSubmitedValue = (String) aoParameters.get(lsElementName);
				}
				aoColumnValueMap.put(lsColumnName, lsSubmitedValue);
			}
			if (!asFormName.equalsIgnoreCase(ApplicationConstants.BUSINESS_APPLICATION_SECTION_POLICIES))
			{
				Document loRuleDoc = XMLUtil.getDomObj(asBusinessRulePath);

				String lsCorpStr = null;
				if (aoParameters.get("corpStr") != null)
				{
					lsCorpStr = (String) aoParameters.get("corpStr");
				}
				Map<String, String> loDocMap = BusinessApplicationUtil.getAllDocTypes(loQuestionDocToRead, loRuleDoc,
						aoColumnValueMap, asFormName, asFormVersion, abSelectStatus, lsCorpStr);
				loDomReturn.setDocType(loDocMap);
			}
			if (asAjaxCall == null)
			{
				QuestionAnswerDAO loQuestionAnswerDAO = new QuestionAnswerDAO();
				boolean lbDataFlag = false;
				HashMap<String, Object> loHMData = loQuestionAnswerDAO.getFormData(aoMyBatisSession, asBuisAppId,
						asOrgId, asTableName);
				if (loHMData != null && !loHMData.isEmpty())
				{
					lbDataFlag = true;
				}
				if (lbDataFlag)
				{
					loData = updateDbValues(asFormName, asFormVersion, asBuisAppId, asUserID, aoColumnValueMap,
							asOrgId, asAppStatus, asSection, loFormTemplate, asTableName, asUserRoleForDocument,
							aoParameters);
					loQuestionAnswerDAO.update(loData, aoMyBatisSession, asTableName);
				}
				else
				{
					loData = insertDbValues(asFormName, asFormVersion, asBuisAppId, asUserID, aoColumnValueMap,
							asOrgId, asAppStatus, asSection, asTableName);
					loQuestionAnswerDAO.insert(loData, aoMyBatisSession, asTableName);
				}
			}
		}
		return loDomReturn;
	}

	/**
	 * This method inserts the basic form values in db
	 * 
	 * @param asFormName - the form name of the current form which needs to be
	 *            validated
	 * @param asFormVersion - the form version of the current form which needs
	 *            to be validated
	 * @param asBuisAppId - Business application id of the application
	 * @param asUserID - the user id of current user submitting the form
	 * @param aoColumnValueMap - hashmap of the form data corresponding to
	 *            column
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppStatus - status corresponding to current form to be updated
	 *            in database
	 * @param asSection - the section name of the current form which needs to be
	 *            validated and inserted/updated
	 * @param asTableName - the table name for corresponding form
	 * @return the hashmap that will be used for database insertion
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private static HashMap insertDbValues(String asFormName, String asFormVersion, String asBuisAppId, String asUserID,
			HashMap<String, String> aoColumnValueMap, String asOrgId, String asAppStatus, String asSection,
			String asTableName)
	{
		Date loCurrentDate = new Date(System.currentTimeMillis());
		HashMap loColumnAndValueMap = new HashMap();
		StringBuffer loSBValueToInsert = new StringBuffer();
		loSBValueToInsert.append("'");
		loSBValueToInsert.append(asOrgId);
		loSBValueToInsert.append("',");
		loSBValueToInsert.append("'");
		loSBValueToInsert.append(asFormName);
		loSBValueToInsert.append("',");
		loSBValueToInsert.append("'");
		loSBValueToInsert.append(asFormVersion);
		loSBValueToInsert.append("',");
		loSBValueToInsert.append("'");
		loSBValueToInsert.append(asBuisAppId);
		loSBValueToInsert.append("',");
		loSBValueToInsert.append("'");
		loSBValueToInsert.append(asSection);
		loSBValueToInsert.append("',");
		loSBValueToInsert.append("'");
		loSBValueToInsert.append(asUserID);
		loSBValueToInsert.append("',");
		loSBValueToInsert.append("'");
		loSBValueToInsert.append(asAppStatus);
		loSBValueToInsert.append("',");
		loSBValueToInsert.append("'");
		loSBValueToInsert.append(asFormName);
		loSBValueToInsert.append("_");
		loSBValueToInsert.append(asFormVersion);
		loSBValueToInsert.append("',");
		loSBValueToInsert.append("to_date('");
		loSBValueToInsert.append(loCurrentDate);
		loSBValueToInsert.append("','yyyy-mm-dd'),");
		loSBValueToInsert.append("to_date('");
		loSBValueToInsert.append(loCurrentDate);
		loSBValueToInsert.append("','yyyy-mm-dd'),");
		loSBValueToInsert.append("'");
		loSBValueToInsert.append(asUserID);
		loSBValueToInsert.append("',");
		loSBValueToInsert.append("'");
		loSBValueToInsert.append(asUserID);
		loSBValueToInsert.append("',");
		loColumnAndValueMap.put("ORGANIZATION_ID", asOrgId);
		loColumnAndValueMap.put("FORM_NAME", asFormName);
		loColumnAndValueMap.put("FORM_VERSION", asFormVersion);
		loColumnAndValueMap.put("BUSINESS_APPLICATION_ID", asBuisAppId);
		loColumnAndValueMap.put("SECTION_ID", asSection);
		loColumnAndValueMap.put("USER_ID", asUserID);
		loColumnAndValueMap.put("STATUS_ID", asAppStatus);
		loColumnAndValueMap.put("FORM_ID", asFormName + "_" + asFormVersion);
		loColumnAndValueMap.put("SUBMISSION_DATE", loCurrentDate);
		loColumnAndValueMap.put("SUBMITTED_BY", asUserID);
		loColumnAndValueMap.put("MODIFIED_DATE", loCurrentDate);
		loColumnAndValueMap.put("MODIFIED_BY", asUserID);
		loColumnAndValueMap.put("SUB_SECTION_STATUS", ApplicationConstants.COMPLETED_STATE);
		loColumnAndValueMap.put("SUB_SECTION_ID", ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION);
		StringBuffer loSBColumnNames = null;
		loSBColumnNames = new StringBuffer(ApplicationConstants.FORM_COLUMNS_NON_ORGANIZATION);
		for (Entry<String, String> loEntry : aoColumnValueMap.entrySet())
		{
			String lsColumnName = loEntry.getKey();
			String lsSubmitedValue = loEntry.getValue();
			if (lsSubmitedValue != null && !lsSubmitedValue.equalsIgnoreCase("null"))
			{
				loSBValueToInsert.append("'");
				loSBValueToInsert.append(lsSubmitedValue.replaceAll("'", "\\''"));
				loSBValueToInsert.append("',");
				loSBColumnNames.append("");
				loSBColumnNames.append(lsColumnName);
				loSBColumnNames.append(",");
			}
		}
		String lsColumnNames = loSBColumnNames.substring(0, loSBColumnNames.lastIndexOf(ApplicationConstants.COMMA));
		String lsValuesToEnter = "("
				+ loSBValueToInsert.substring(0, loSBValueToInsert.lastIndexOf(ApplicationConstants.COMMA)) + ")";
		loColumnAndValueMap.put("COLUMN", lsColumnNames);
		loColumnAndValueMap.put("VALUESFORCOLUMN", lsValuesToEnter);
		return loColumnAndValueMap;
	}

	/**
	 * This method updates the basic form Db values
	 * 
	 * @param asFormName - the form name of the current form which needs to be
	 *            validated
	 * @param asFormVersion - the form version of the current form which needs
	 *            to be validated
	 * @param asBuisAppId - Business application id of the application
	 * @param asUserID - the user id of current user submitting the form
	 * @param aoColumnValueMap - hashmap of the form data corresponding to
	 *            column
	 * @param asOrgId - the organization id of the current organization
	 * @param asAppStatus - status corresponding to current form to be updated
	 *            in database
	 * @param asSection - the section name of the current form which needs to be
	 *            validated and inserted/updated
	 * @param aoFormTemplate - jdom of the form template
	 * @param asTableName - the table name for corresponding form
	 * @param asUserRoleForDocument - String showing the role of user
	 * @param aoParameters Parameters as input
	 * @return the hashmap that will be used for database updation
	 * @throws ApplicationException Updated Method in R4
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private static HashMap updateDbValues(String asFormName, String asFormVersion, String asBuisAppId, String asUserID,
			HashMap<String, String> aoColumnValueMap, String asOrgId, String asAppStatus, String asSection,
			Document aoFormTemplate, String asTableName, String asUserRoleForDocument,
			HashMap<String, Object> aoParameters) throws ApplicationException
	{
		Date loCurrentDate = new Date(System.currentTimeMillis());
		HashMap loColumnAndValueMap = new HashMap();
		StringBuffer loSBInsertQuery = new StringBuffer();
		if (!asTableName.equalsIgnoreCase("ORGANIZATION"))
		{
			loSBInsertQuery.append(ApplicationConstants.BUSINESS_APPID_STRING);
			loSBInsertQuery.append("='");
			loSBInsertQuery.append(asBuisAppId);
			loSBInsertQuery.append("',");
		}
		loSBInsertQuery.append(ApplicationConstants.EMPID_STRING);
		loSBInsertQuery.append("='");
		loSBInsertQuery.append(asUserID);
		loSBInsertQuery.append("',");
		loSBInsertQuery.append(ApplicationConstants.FORMVERSION_STRING);
		loSBInsertQuery.append("='");
		loSBInsertQuery.append(asFormVersion);
		loSBInsertQuery.append("',");
		loSBInsertQuery.append(ApplicationConstants.ORGANIZATIONID);
		loSBInsertQuery.append("='");
		loSBInsertQuery.append(asOrgId);
		loSBInsertQuery.append("',");
		loSBInsertQuery.append(ApplicationConstants.STATUS_ID);
		loSBInsertQuery.append("='");
		loSBInsertQuery.append(asAppStatus);
		loSBInsertQuery.append("',");
		loSBInsertQuery.append(ApplicationConstants.FORM_NAME);
		loSBInsertQuery.append("='");
		loSBInsertQuery.append(asFormName);
		loSBInsertQuery.append("',");
		loSBInsertQuery.append(ApplicationConstants.SECTION_MODIFIED_DATE);
		loSBInsertQuery.append("=to_date('");
		loSBInsertQuery.append(loCurrentDate);
		loSBInsertQuery.append("','yyyy-mm-dd'),");
		loSBInsertQuery.append(ApplicationConstants.SECTION_MODIFIED_BY);
		loSBInsertQuery.append("='");
		loSBInsertQuery.append(asUserID);
		loSBInsertQuery.append("',");
		List<Element> loList = XMLUtil.getElementList("//element[@schemaname]", aoFormTemplate);
		List<String> loListSchema = new ArrayList<String>();
		for (Element loElt : loList)
		{
			loListSchema.add(loElt.getAttributeValue("schemaname"));
		}
		for (Entry<String, String> loEntry : aoColumnValueMap.entrySet())
		{
			String lsColumnName = loEntry.getKey();
			String lsSubmitedValue = loEntry.getValue();
			if (lsSubmitedValue != null && !lsSubmitedValue.equalsIgnoreCase("null"))
			{
				loListSchema.remove(lsColumnName);
				if (!(asUserRoleForDocument.equalsIgnoreCase("admin") && ApplicationConstants.BASIC_NO_UPDATE
						.contains(lsColumnName)))
				{
					loSBInsertQuery.append(lsColumnName);
					loSBInsertQuery.append("='");
					loSBInsertQuery.append(lsSubmitedValue.replaceAll("'", "\\''"));
					loSBInsertQuery.append("',");
				}

				if (lsColumnName.equalsIgnoreCase("ORGANIZATION_LEGAL_NAME"))
				{
					List<ProviderBean> loProviderList = (List<ProviderBean>) BaseCacheManagerWeb.getInstance()
							.getCacheObject(ApplicationConstants.PROV_LIST);
					for (ProviderBean loProBean : loProviderList)
					{
						if (loProBean.getHiddenValue().equalsIgnoreCase(asOrgId))
						{
							loProBean.setDisplayValue(StringEscapeUtils.escapeJavaScript(lsSubmitedValue));
							break;
						}
					}
					BaseCacheManagerWeb.getInstance().putCacheObject(ApplicationConstants.PROV_LIST, loProviderList);
					if (!(asUserRoleForDocument.equalsIgnoreCase("admin") && ApplicationConstants.BASIC_NO_UPDATE
							.contains(lsColumnName)))
					{
						loColumnAndValueMap.put("updateProvList", "true");
					}
				}
			}
		}
		for (String lsSchemaName : loListSchema)
		{
			if (!ApplicationConstants.BASIC_NO_UPDATE.contains(lsSchemaName))
			{
				loSBInsertQuery.append(lsSchemaName);
				loSBInsertQuery.append("=null,");
			}
		}
		if (aoParameters.get("addressRelatedData") != null)
		{
			String lsAddressRelatedData = (String) aoParameters.get("addressRelatedData");
			String lsAddressRelatedDataArray[] = lsAddressRelatedData.split(ApplicationConstants.KEY_SEPARATOR);
			for (int liCount = 0; liCount < lsAddressRelatedDataArray.length; liCount++)
			{
				String lsFieldValue = lsAddressRelatedDataArray[liCount];
				if (lsFieldValue != null && !lsFieldValue.trim().equalsIgnoreCase("null"))
				{
					loSBInsertQuery.append(ApplicationConstants.ADDRESS_FIELD_MAP.get(liCount));
					loSBInsertQuery.append("='");
					loSBInsertQuery.append(lsFieldValue.trim().replaceAll("'", "\\''"));
					loSBInsertQuery.append("',");
				}
				else
				{
					loSBInsertQuery.append(ApplicationConstants.ADDRESS_FIELD_MAP.get(liCount));
					loSBInsertQuery.append("=null,");
				}
			}
		}
		String lsFinalQuesr = loSBInsertQuery.substring(0, loSBInsertQuery.length() - 1);
		loColumnAndValueMap.put("asBuisAppId", asBuisAppId);
		loColumnAndValueMap.put("asOrgId", asOrgId);
		loColumnAndValueMap.put("VALUESTOUPDATE", lsFinalQuesr);
		loColumnAndValueMap.put("SUBMISSION_DATE", loCurrentDate);
		loColumnAndValueMap.put("SUBMITTED_BY", asUserID);
		loColumnAndValueMap.put("MODIFIED_DATE", loCurrentDate);
		loColumnAndValueMap.put("MODIFIED_BY", asUserID);
		loColumnAndValueMap.put("SUB_SECTION_STATUS", ApplicationConstants.COMPLETED_STATE);
		loColumnAndValueMap.put("SUB_SECTION_ID", ApplicationConstants.BUZ_APP_SUB_SECTION_QUESTION);
		loColumnAndValueMap.put("ORGANIZATION_ID", asOrgId);
		loColumnAndValueMap.put("FORM_NAME", asFormName);
		loColumnAndValueMap.put("FORM_VERSION", asFormVersion);
		loColumnAndValueMap.put("BUSINESS_APPLICATION_ID", asBuisAppId);
		loColumnAndValueMap.put("SECTION_ID", asSection);
		loColumnAndValueMap.put("USER_ID", asUserID);
		loColumnAndValueMap.put("FORM_ID", asFormName + "_" + asFormVersion);
		return loColumnAndValueMap;
	}
}
