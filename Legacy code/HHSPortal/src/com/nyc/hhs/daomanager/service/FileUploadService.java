package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.springframework.util.CollectionUtils;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.ProviderBean;
import com.nyc.hhs.service.db.dao.FileUploadDAO;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.FileNetOperationsUtils;

/**
 * FileUploadService: This service class is used to execute different
 * transaction involved in document vault and for some other scenarios also It
 * executes the transactions with the help of master dao.
 */
public class FileUploadService extends ServiceState
{
	/**
	 * This Method is used for updating Document Details in the Document Table
	 * for uploaded document
	 * 
	 * @param aoApplicationId a string value of application Id
	 * @param asDocumentId a string value of document Id
	 * @param asDocumentCategory a string value of document category
	 * @param asDocumentType a string value of document type
	 * @param asFormName a string value of form name
	 * @param asFormVersion a string value of form version
	 * @param asOrganizationId a string value of organization Id
	 * @param asDocName a string value of document name
	 * @param asLastModifiedBy a string value of document last modified by
	 * @param asLastModifiedDate a string value of document last modified date
	 * @param asSubmissionBy a string value of application submitted by
	 * @param asSubmissionDate a string value of application submission date
	 * @param serviceAppId a string value of service application Id
	 * @param sectionId a string value of section Id
	 * @param mybatis_session mybatis sql session
	 * @return a boolean value of Document table update status
	 * @throws ApplicationException If an application exception occurred
	 */
	public boolean updatefileUploadDetails1(String aoApplicationId, String asDocumentId, String asDocumentCategory,
			String asDocumentType, String asFormName, String asFormVersion, String asOrganizationId, String asDocName,
			String asLastModifiedBy, String asLastModifiedDate, String asSubmissionBy, String asSubmissionDate,
			String asServiceAppId, String asSectionId, String asEntityid, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbInsertStatus = true;
		Document loFileUpload = new Document();
		loFileUpload.setApplicationId(aoApplicationId);
		loFileUpload.setDocCategory(asDocumentCategory);
		loFileUpload.setDocumentId(asDocumentId);
		loFileUpload.setDocType(asDocumentType);
		loFileUpload.setFormName(asFormName);
		loFileUpload.setFormVersion(asFormVersion);
		loFileUpload.setOrganizationId(asOrganizationId);
		loFileUpload.setDocName(asDocName);
		loFileUpload.setStatus(ApplicationConstants.COMPLETED_STATE);
		loFileUpload.setLastModifiedBy(asLastModifiedBy);
		loFileUpload.setLastModifiedDate(DateUtil.getSqlDate(asLastModifiedDate));
		loFileUpload.setSubmissionBy(asSubmissionBy);
		loFileUpload.setSubmissionDate(DateUtil.getSqlDate(asSubmissionDate));
		loFileUpload.setServiceAppID(asServiceAppId);
		loFileUpload.setSectionId(asSectionId);
		loFileUpload.setMsEntityId(asEntityid);// Defect #1805 fix
		FileUploadDAO loFileUploadDAO = new FileUploadDAO();

		// Call to insert method FileUpload table.
		lbInsertStatus = lbInsertStatus
				&& loFileUploadDAO.updateDetails1(loFileUpload, aoMybatisSession, lbInsertStatus);

		return lbInsertStatus;
	}
	/**
	 * This method is used to set docId with same docname
	 * @param aoApplicationId 
	 * @param asDocumentId
	 * @param asDocumentCategory
	 * @param asDocumentType
	 * @param asFormName
	 * @param asFormVersion
	 * @param asOrganizationId
	 * @param asDocName
	 * @param asServiceAppId
	 * @param asSectionId
	 * @param asEntityId
	 * @param aoMybatisSession
	 * @return lbInsertStatus a boolean value
	 * @throws ApplicationException
	 */
	public boolean setDocIdWithSameDocName(String aoApplicationId, String asDocumentId, String asDocumentCategory,
			String asDocumentType, String asFormName, String asFormVersion, String asOrganizationId, String asDocName,
			String asServiceAppId, String asSectionId, String asEntityId, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		boolean lbInsertStatus = true;
		Document loFileUpload = new Document();
		loFileUpload.setApplicationId(aoApplicationId);
		loFileUpload.setDocCategory(asDocumentCategory);
		loFileUpload.setDocumentId(asDocumentId);
		loFileUpload.setDocType(asDocumentType);
		loFileUpload.setFormName(asFormName);
		loFileUpload.setFormVersion(asFormVersion);
		loFileUpload.setOrganizationId(asOrganizationId);
		loFileUpload.setDocName(asDocName);
		loFileUpload.setServiceAppID(asServiceAppId);
		loFileUpload.setSectionId(asSectionId);
		loFileUpload.setMsEntityId(asEntityId);// Defect #1805 fix
		if (asServiceAppId != null && !asServiceAppId.equalsIgnoreCase("null"))
		{
			DAOUtil.masterDAO(aoMybatisSession, loFileUpload, ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER,
					"setDocIdWithSameDocNameService", "com.nyc.hhs.model.Document");
		}
		else
		{
			DAOUtil.masterDAO(aoMybatisSession, loFileUpload, ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER,
					"setDocIdWithSameDocName", "com.nyc.hhs.model.Document");
		}
		return lbInsertStatus;
	}
	/**
	 * This method is used to check for docId
	 * @param aoApplicationId
	 * @param asServiceAppId
	 * @param asDocumentId
	 * @param asDocumentCategory
	 * @param asDocumentType
	 * @param asOrganizationId
	 * @param asDocName
	 * @param sectionId
	 * @param asEntityId
	 * @param aoUserSession
	 * @param mybatisSession
	 * @return lsGetdocIdForDocType docId as result of query
	 * @throws ApplicationException
	 */
	public String checkForDocId(String aoApplicationId, String asServiceAppId, String asDocumentId,
			String asDocumentCategory, String asDocumentType, String asOrganizationId, String asDocName,
			String sectionId, String asEntityId, P8UserSession aoUserSession, SqlSession mybatisSession)
			throws ApplicationException
	{
		HashMap<String, Object> loFileUploadInfo = new HashMap<String, Object>();
		loFileUploadInfo.put("asDocId", asDocumentId);
		loFileUploadInfo.put("asDocumentType", asDocumentType);
		loFileUploadInfo.put("asOrganizationId", asOrganizationId);
		loFileUploadInfo.put("aoApplicationId", aoApplicationId);
		loFileUploadInfo.put("asServiceAppId", asServiceAppId);
		loFileUploadInfo.put("asEntityId", asEntityId);// Defect #1805 fix
		String lsGetdocIdForDocType = null;
		if (asServiceAppId != null && !asServiceAppId.equalsIgnoreCase("null"))
		{
			lsGetdocIdForDocType = (String) DAOUtil.masterDAO(mybatisSession, loFileUploadInfo,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getDocIdForDocTypeService",
					"java.util.HashMap");
		}
		else
		{
			lsGetdocIdForDocType = (String) DAOUtil.masterDAO(mybatisSession, loFileUploadInfo,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getDocIdForDocType", "java.util.HashMap");
		}
		if (lsGetdocIdForDocType == null)
		{
			lsGetdocIdForDocType = asDocumentId;
		}
		return lsGetdocIdForDocType;
	}

	/**
	 * This Method is used to fetch Document Details from the Document Table
	 * based on Application Id, Form Information and Organization Id
	 * 
	 * @param asApplicationId a string value of application id
	 * @param asFormInfo a map containing form information
	 * @param asOrgId a string value of organization id
	 * @param aoMybatisSession mybatis sql session
	 * @return List<Document> list of document details
	 * @throws ApplicationException If an application exception occurred
	 */

	public List<Document> getDocumentDetails(String asApplicationId, HashMap asFormInfo, String asOrgId,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		FileUploadDAO loFileUploadDAO = new FileUploadDAO();
		List<Document> loResultList = null;

		if (asFormInfo != null && asFormInfo.get("FORM_NAME") != null && asFormInfo.get("FORM_VERSION") != null)
		{
			loResultList = loFileUploadDAO.selectDetails(asApplicationId, asFormInfo, asOrgId, aoMybatisSession);

			asFormInfo.put("asCeo", "1");
			asFormInfo.put("asCfo", "2");
			asFormInfo.put("asMemberStatus", ApplicationConstants.ACTIVE);
			asFormInfo.put("asIsActive", ApplicationConstants.SYSTEM_YES);
			asFormInfo.put("asOrgId", asOrgId);
			String lsIsCeoName = null;
			String lsIsCfoName = null;
			lsIsCeoName = (String) DAOUtil.masterDAO(aoMybatisSession, asFormInfo,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getCeoName", "java.util.HashMap");
			lsIsCfoName = (String) DAOUtil.masterDAO(aoMybatisSession, asFormInfo,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getCfoName", "java.util.HashMap");
			Integer liIsCeo = -1;
			liIsCeo = (Integer) DAOUtil.masterDAO(aoMybatisSession, asFormInfo,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getCeoCount", "java.util.HashMap");

			if (lsIsCeoName != null)
			{
				for (Document loDocument : loResultList)
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
				for (Document loDocument : loResultList)
				{
					if (loDocument.getDocType().equalsIgnoreCase(ApplicationConstants.CFO_NAME))
					{
						loDocument.setMbIsCfoName(true);
						loDocument.setMsCfoName(lsIsCfoName);
					}
				}
			}
			if (liIsCeo == 0)
			{
				for (Document loDocument : loResultList)
				{
					loDocument.setMbIsCeo(true);
					if (loDocument.getDocType().equalsIgnoreCase(ApplicationConstants.CEO_NAME))
					{
						loDocument.setMbIsCeo(false);
					}
				}
			}
		}
		return loResultList;
	}

	/**
	 * This Method is used to fetch Document Details Summary from the document
	 * Table based on Service Application Id, Application Id and Organization Id
	 * 
	 * @param asServiceApplicationId a string value of service application Id
	 * @param asOrgId a string value of organization Id
	 * @param asapplicationid a string value of application Id
	 * @param aoMybatisSession mybatis sql session
	 * @return List<Document> list of document details
	 * @throws ApplicationException If an application exception occurred
	 */
	public List<Document> getDocumentDetailsServiceSummary(String asServiceApplicationId, String asOrgId,
			String asapplicationid, SqlSession aoMybatisSession) throws ApplicationException
	{
		FileUploadDAO loFileUploadDAO = new FileUploadDAO();
		List<Document> loResultList = null;
		// Defect #1805 fix
		HashMap loWhereClause = new HashMap();
		loWhereClause.put("asOrgId", asOrgId);
		loWhereClause.put("asServiceAppID", asServiceApplicationId);
		loWhereClause.put("asBusinessAppId", asapplicationid);
		loResultList = loFileUploadDAO.selectDetailsServiceSummary(asServiceApplicationId, asOrgId, asapplicationid,
				aoMybatisSession);

		// Fix for defect #1805, Display Staff/Funder name if there is no
		// Document Name returned from DB.
		for (Document loDocument : loResultList)
		{
			if (null != loDocument && (null == loDocument.getDocName() || loDocument.getDocName().isEmpty())
					&& loDocument.getDocType().equals(ApplicationConstants.CONTRACT_GRANT_DOC_TYPE))
			{
				if (null != loDocument.getMsEntityId())
				{
					String lsFunderName = null;
					loWhereClause.put("entityId", loDocument.getMsEntityId());
					lsFunderName = (String) DAOUtil.masterDAO(aoMybatisSession, loWhereClause,
							ApplicationConstants.MAPPER_CLASS_APPLICATION, "fetchContractNameForgridDisplay",
							"java.util.HashMap");
					loDocument.setMsServiceDocumentName(lsFunderName);
					loDocument.setMbIsStaffFundername(true);
				}

			}
			else if (null != loDocument && (null == loDocument.getDocName() || loDocument.getDocName().isEmpty())
					&& loDocument.getDocType().equals(ApplicationConstants.KEY_STAFF_DOC_TYPE))
			{
				if (null != loDocument.getMsEntityId())
				{
					String lsFunderName = null;
					loWhereClause.put("entityId", loDocument.getMsEntityId());
					lsFunderName = (String) DAOUtil.masterDAO(aoMybatisSession, loWhereClause,
							ApplicationConstants.MAPPER_CLASS_APPLICATION, "fetchStaffNameForgridDisplay",
							"java.util.HashMap");
					loDocument.setMsServiceDocumentName(lsFunderName);
					loDocument.setMbIsStaffFundername(true);
				}
			}
		}
		return loResultList;
	}

	/**
	 * This Method is used to fetch form information from the specified Table
	 * based on Application Id and Organization Id
	 * 
	 * @param asApplicationId a string value of application Id
	 * @param asOrgId a string value of organization Id
	 * @param asTableName a string value of table name
	 * @param aoMybatisSession mybatis sql session
	 * @return HashMap<String, Object>a map containing form information
	 * @throws ApplicationException If an application exception occurred
	 */
	public HashMap<String, Object> getFormInformation(String asApplicationId, String asOrgId, String asTableName,
			SqlSession aoMybatisSession) throws ApplicationException
	{

		FileUploadDAO loFileUploadDAO = new FileUploadDAO();
		List<HashMap<String, Object>> loFormInfoListMap = null;
		HashMap<String, Object> loFormInfoMap = null;

		loFormInfoListMap = loFileUploadDAO.selectFormDetails(asApplicationId, asOrgId, asTableName, aoMybatisSession);

		if (loFormInfoListMap != null && !loFormInfoListMap.isEmpty())
		{
			loFormInfoMap = loFormInfoListMap.get(0);
		}
		return loFormInfoMap;
	}

	/**
	 * This Method is used to fetch AccountingPeriod from the
	 * DOC_LAPSING_RULES_MASTER Table based on provider ID
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param asProviderId a string value of provider Id
	 * @return List<ProviderBean> list of providers
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings("unchecked")
	public List<ProviderBean> getAccountingPeriodForProvider(SqlSession aoMybatisSession, String asProviderId)
			throws ApplicationException
	{
		List<ProviderBean> loResultList = null;
		try
		{
			loResultList = (List<ProviderBean>) DAOUtil.masterDAO(aoMybatisSession, asProviderId,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getAccountingPeriod", "java.lang.String");
			setMoState("Accounting period fetched successfully for provider Id:" + asProviderId);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while fetching accounting period for Provider Id:" + asProviderId);
			throw loExp;
		}
		return loResultList;
	}

	/**
	 * This Method is used to fetch List of DocumentIds from the
	 * aoDocumentsDetails List
	 * 
	 * @param aoDocumentsDetails list of document details
	 * @return List<String> list of document Ids
	 * @throws ApplicationException If an application exception occurred
	 */
	public List<String> getDocumentIds(List<Document> aoDocumentsDetails) throws ApplicationException
	{

		List<String> loDocIdList = new ArrayList<String>();
		if (!CollectionUtils.isEmpty(aoDocumentsDetails))
		{
			Iterator<Document> loIter = aoDocumentsDetails.iterator();
			while (loIter.hasNext())
			{

				Document loDocuments = loIter.next();
				if (loDocuments.getDocumentId() != null && loDocuments.getDocumentId().trim().length() > 0)
				{
					loDocIdList.add(loDocuments.getDocumentId());
				}
			}
		}
		return loDocIdList;
	}

	/**
	 * This Method is used to fetch agency names from the NYCAGENCYNAME Table
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @return List<ProviderBean> list of agencies
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings("unchecked")
	public List<ProviderBean> getNYCAgencyList(SqlSession aoMybatisSession) throws ApplicationException
	{
		List<ProviderBean> loProviderList = null;
		try
		{
			loProviderList = (List<ProviderBean>) DAOUtil.masterDAO(aoMybatisSession, null,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getAgencies", null);
			setMoState("NYC Agencies fetched successfully");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while fetching NYC Agencies");
			throw loExp;
		}
		return loProviderList;
	}

	/**
	 * This Method is used to fetch ORGANIZATION_ID,ORGANIZATION_LEGAL_NAME from
	 * the ORGANIZATION Table
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @return List<ProviderBean> list of providers
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings("unchecked")
	public List<ProviderBean> getProviderListAjaxCall(SqlSession aoMybatisSession) throws ApplicationException
	{
		List<ProviderBean> loProviderList = null;
		try
		{
			loProviderList = (List<ProviderBean>) DAOUtil.masterDAO(aoMybatisSession, null,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getProvidersAjax", null);
			setMoState("Provider List fetched successfully");
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while fetching provider List");
			throw loExp;
		}
		return loProviderList;
	}

	/**
	 * This Method inserts Document Lapsing details in the
	 * DOC_LAPSING_RULES_TRANS Table
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoInsertPropMap a map containing doc lapsing properties for insert
	 * @return Integer insert status
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings("rawtypes")
	public Integer insertDocLapsingTrans(SqlSession aoMybatisSession, HashMap aoInsertPropMap)
			throws ApplicationException
	{
		Integer liStatus = 0;
		try
		{
			liStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoInsertPropMap,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getDocLapsingInsertStatus",
					"java.util.HashMap");

			setMoState("Data inserted in Doc Lapsing Trans successfully for providerId:"
					+ aoInsertPropMap.get("providerId"));
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error occured while inserting data in doc Lapsing trans for providerId:"
					+ aoInsertPropMap.get("providerId"));
			throw loExp;
		}
		return liStatus;
	}

	/**
	 * This Method checks the Document Extension that ProviderId can upload
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param asProviderId a string value of provider Id
	 * @return a string value of pipe separated extensions
	 * @throws ApplicationException If an application exception occurred
	 */
	public String checkExtension(SqlSession aoMybatisSession, String asProviderId) throws ApplicationException
	{
		String lsExtension = null;
		try
		{
			lsExtension = (String) DAOUtil.masterDAO(aoMybatisSession, asProviderId,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "checkExtension", "java.lang.String");
			setMoState("Extension fetched successfully for provider Id:" + asProviderId);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting extension for Provider Id:" + asProviderId);
			throw loExp;
		}
		return lsExtension;
	}

	/**
	 * This Method gets END YEAR from DOC_LAPSING_RULES_MASTER table for char
	 * 500 Document
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param asProviderId a string value of provider Id
	 * @return Integer end year integer object
	 * @throws ApplicationException If an application exception occurred
	 */
	public Integer getEndYearForChar500(SqlSession aoMybatisSession, String aoProviderId) throws ApplicationException
	{
		Integer liEndYear = 0;
		try
		{
			liEndYear = Integer.valueOf((String) DAOUtil.masterDAO(aoMybatisSession, aoProviderId,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getEndYearForChar500", "java.lang.String"));
			setMoState("Char500 End year fetched successfully for provider Id:" + aoProviderId);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while fetching Char500 End year for provider Id:" + aoProviderId);
			throw loExp;
		}
		return liEndYear;
	}

	/**
	 * This Method gets AccountingPeriod from ORGANIZATION table for Provider Id
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param asProviderId a string value of provider Id
	 * @return list of provider bean
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings("unchecked")
	public List<ProviderBean> getAccountingPeriodForProviderFromOrg(SqlSession aoMybatisSession, String aoProviderId)
			throws ApplicationException
	{
		List<ProviderBean> loProviderList = null;
		try
		{
			loProviderList = (List<ProviderBean>) DAOUtil.masterDAO(aoMybatisSession, aoProviderId,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getAccountingPeriodForProviderFromOrg",
					"java.lang.String");
			setMoState("Accounting period fetched successfully for provider Id:" + aoProviderId);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while fetching accounting period for provider Id:" + aoProviderId);
			throw loExp;
		}
		return loProviderList;
	}

	/**
	 * This Method updates Due Date in DOC_LAPSING_RULES_MASTER table for
	 * Provider Id
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoDocLapsingMasterMap map containing doc lapsing information for
	 *            update
	 * @return Integer update status
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings("rawtypes")
	public Integer updateDocLapsingMasterDueDate(SqlSession aoMybatisSession, HashMap aoDocLapsingMasterMap)
			throws ApplicationException
	{
		Integer liStatus = 0;
		try
		{
			liStatus = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoDocLapsingMasterMap,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "updateDocLapsingMasterDueDate",
					"java.util.HashMap");
			setMoState("Doc Lapsing master due date updated successfully for providerId:"
					+ aoDocLapsingMasterMap.get("providerId"));
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error occured while updating doc Lapsing master due date for providerId:"
					+ aoDocLapsingMasterMap.get("providerId"));
			throw loExp;
		}
		return liStatus;
	}

	/**
	 * This Method deletes from DOC_LAPSING_RULES_MASTER table for Provider
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param asProviderId a string value of provider Id
	 * @return a boolean value of delete status
	 * @throws ApplicationException If an application exception occurred
	 */
	public boolean deleteShortFilingEntry(SqlSession aoMybatisSession, String asProviderId) throws ApplicationException
	{
		Boolean lbStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, asProviderId, ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER,
					"deleteShortFilingEntry", "java.lang.String");
			lbStatus = true;
			setMoState("Short Filing Entry deleted successfully for providerId:" + asProviderId);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error occured while deleting short filing entry for providerId:" + asProviderId);
			throw loExp;
		}
		return lbStatus;
	}

	/**
	 * This Method updates DOC_LAPSING_RULES_MASTER table for Provider Id
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param asProviderId a string value of provider Id
	 * @return a boolean value of update status
	 * @throws ApplicationException If an application exception occurred
	 */
	public boolean updateDocLapsingMaster(SqlSession aoMybatisSession, String asProviderId) throws ApplicationException
	{
		Boolean lbStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, asProviderId, ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER,
					"updateDocLapsingMaster", "java.lang.String");
			lbStatus = true;
			setMoState("Doc Lapsing master updated successfully for providerId:" + asProviderId);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error occured while updating doc Lapsing master for providerId:" + asProviderId);
			throw loExp;
		}
		return lbStatus;
	}

	/**
	 * This Method updates APPLICATION_STATUS in BUSINESS_APPLICATION table
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoApplicationStatusMap map containing document information
	 * @return a string value of application status
	 * @throws ApplicationException If an application exception occurred
	 */

	public String checkApplicationStatus(SqlSession aoMybatisSession, HashMap aoApplicationStatusMap)
			throws ApplicationException
	{
		List<String> loAppStatusList = null;
		String lsAppStatus = "";
		try
		{
			loAppStatusList = (List<String>) DAOUtil
					.masterDAO(aoMybatisSession, aoApplicationStatusMap,
							ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "checkApplicationStatus",
							"java.util.HashMap");

			if (loAppStatusList != null && !loAppStatusList.isEmpty())
			{

				if (loAppStatusList.contains(ApplicationConstants.STATUS_APPROVED))
				{
					lsAppStatus = ApplicationConstants.STATUS_APPROVED;
				}
				else if (loAppStatusList.contains(ApplicationConstants.STATUS_SUSPEND))
				{
					lsAppStatus = ApplicationConstants.STATUS_SUSPEND;
				}
				else if (loAppStatusList.contains(ApplicationConstants.STATUS_IN_REVIEW))
				{
					lsAppStatus = ApplicationConstants.STATUS_IN_REVIEW;
				}
				else if (loAppStatusList.contains(ApplicationConstants.STATUS_REJECTED))
				{
					lsAppStatus = ApplicationConstants.STATUS_REJECTED;
				}
				else if (loAppStatusList.contains(ApplicationConstants.STATUS_EXPIRED))
				{
					lsAppStatus = ApplicationConstants.STATUS_EXPIRED;
				}
				else if (loAppStatusList.contains(ApplicationConstants.STATUS_WITHDRAWN))
				{
					lsAppStatus = ApplicationConstants.STATUS_WITHDRAWN;
				}
				else if (loAppStatusList.contains(ApplicationConstants.STATUS_WITHDRAW_REQUESTED))
				{
					lsAppStatus = ApplicationConstants.STATUS_WITHDRAW_REQUESTED;
				}
				else if (loAppStatusList.contains(ApplicationConstants.STATUS_CONDITIONALLY_APPROVED))
				{
					lsAppStatus = ApplicationConstants.STATUS_CONDITIONALLY_APPROVED;
				}
				else if (loAppStatusList.contains(ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED))
				{
					lsAppStatus = ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED;
				}
				else if (loAppStatusList.contains(ApplicationConstants.APP_STATUS_DEFERRED)
						|| loAppStatusList.contains(ApplicationConstants.STATUS_DEFFERED))
				{
					lsAppStatus = ApplicationConstants.APP_STATUS_DEFERRED;
				}
				else if (loAppStatusList.contains(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS))
				{
					lsAppStatus = ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS;
				}
				else if (loAppStatusList.contains(ApplicationConstants.STATUS_DRAFT)
						|| loAppStatusList.contains(ApplicationConstants.STATUS_DRAFT.toLowerCase()))
				{
					lsAppStatus = ApplicationConstants.STATUS_DRAFT;
				}
				else
				{
					lsAppStatus = ApplicationConstants.STATUS_APPROVED;
				}

			}
			else
			{
				lsAppStatus = ApplicationConstants.STATUS_DRAFT;
			}
			setMoState("Application Status fetched successfully for document Id:"
					+ aoApplicationStatusMap.get("documentId"));
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting application status for document Id:"
					+ aoApplicationStatusMap.get("documentId"));
			throw loExp;
		}
		return lsAppStatus;
	}

	/**
	 * This Method updates Modified date and modified by information from
	 * DOCUMENT table
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoModifiedInfoMap a map containing document modified information
	 * @return Boolean update status
	 * @throws ApplicationException If an application exception occurred
	 */
	public Boolean updateDocModifiedInfo(SqlSession aoMybatisSession, HashMap aoModifiedInfoMap)
			throws ApplicationException
	{
		Boolean loDocumentUpdateStatus = false;
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, aoModifiedInfoMap,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "updateDocumentModifiedInfo",
					"java.util.HashMap");
			loDocumentUpdateStatus = true;
			setMoState("Updating doc modified info for documentId:" + aoModifiedInfoMap.get("documentId"));
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while updating doc modified info for documentId:" + aoModifiedInfoMap.get("documentId"));
			throw loExp;
		}
		return loDocumentUpdateStatus;
	}

	/**
	 * This Method is used to fetch LastUpdatedDocType information form
	 * DOC_LAPSING_RULES_TRANS table
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoProviderId a string value of provider Id
	 * @return hashmap map containing information of law type and last updated
	 *         document type
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap getDocTypeAndWorkFlowID(SqlSession aoMybatisSession, String aoProviderId)
			throws ApplicationException
	{
		HashMap<String, Object> loHashMap = null;
		try
		{
			Map<String, String> loMap = new HashMap<String, String>();
			loMap.put("aoProviderId", aoProviderId);
			loMap.put("asStatus", ApplicationConstants.COMPLETED_STATE);
			List<HashMap<String, Object>> loResultMap = (List<HashMap<String, Object>>) DAOUtil.masterDAO(
					aoMybatisSession, loMap, ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER,
					"getDocTypeAndWorkFlowID", "java.util.Map");

			if (loResultMap != null && !loResultMap.isEmpty())
			{
				loHashMap = loResultMap.get(0);
			}
			setMoState("Last Updated Doc Type fetched successfully for Provider Id:" + aoProviderId);
		}
		catch (ApplicationException loAppExp)
		{
			setMoState("Error while fetching Last Updated Doc Type for Provider Id:" + aoProviderId);
			throw loAppExp;
		}
		return loHashMap;
	}

	/**
	 * This Method is used to update processStatus in DOC_LAPSING_RULES_TRANS
	 * table for terminated workflow
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoWobNo a string value of workflow id
	 * @return boolean process status for terminated wob no
	 * @throws ApplicationException If an application exception occurred
	 */
	public Boolean updateProcStatusForTerminatedWobNo(SqlSession aoMybatisSession, String aoWobNo, String asUserId)
			throws ApplicationException
	{
		Boolean loProcStatus = false;
		
		HashMap<String, String> loUpdateStatusMap = new HashMap<String, String>();
		loUpdateStatusMap.put("workflowId", aoWobNo);
		loUpdateStatusMap.put("modifiedBy", asUserId);
		loUpdateStatusMap.put("procStatus", "Terminated");
		try
		{
			DAOUtil.masterDAO(aoMybatisSession, loUpdateStatusMap,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "updateProcStatusForTerminatedWobNo",
					"java.util.HashMap");
			loProcStatus = true;
			setMoState("Proc Status updated successfully for Wob no:" + aoWobNo);
		}
		catch (ApplicationException loAppExp)
		{
			setMoState("Error while updating proc status for Wob no:" + aoWobNo);
			throw loAppExp;
		}
		return loProcStatus;
	}

	/**
	 * This method is used to fetch lawtype for the input org Id
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param asOrgId a string value of organization id
	 * @return a string value of law type
	 * @throws ApplicationException If an application exception occurred
	 */
	public String getLawType(SqlSession aoMybatisSession, String asOrgId) throws ApplicationException
	{
		String lsLawType = null;
		try
		{
			lsLawType = (String) DAOUtil.masterDAO(aoMybatisSession, asOrgId,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getLawType", "java.lang.String");
			setMoState("Law Type fetched successfully for Org Id:" + asOrgId);
		}
		catch (ApplicationException loAppExp)
		{
			setMoState("Error while fetching law type for Org Id:" + asOrgId);
			throw loAppExp;
		}
		return lsLawType;
	}

	/**
	 * This method is used to get the user name from data base. It takes user id
	 * as argument and return user name
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param asUserId a string value of user Id
	 * @return a string value of user name
	 * @throws ApplicationException If an application exception occurred
	 */
	public String getUserName(String asUserId, SqlSession aoMybatisSession) throws ApplicationException
	{
		String lsUserName = null;
		try
		{
			lsUserName = (String) DAOUtil.masterDAO(aoMybatisSession, asUserId,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getUserName", "java.lang.String");
			setMoState("Provider Name fetched successfully for Provider Id:" + asUserId);
		}
		catch (ApplicationException loAppExp)
		{
			setMoState("Error while fetching provider name for Provider Id:" + asUserId);
			throw loAppExp;
		}
		return lsUserName;
	}

	/**
	 * This method is used to get the application setting for different cases
	 * like size of file allowed no of object allowed per page etc...
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoGetContentMap map containing information about component name
	 * @return long Permitted Content
	 * @throws ApplicationException If an application exception occurred
	 */
	public long getApplicationSettings(SqlSession aoMybatisSession, HashMap aoGetContentMap)
			throws ApplicationException
	{
		long llPermittedContent = 0;
		try
		{
			llPermittedContent = Long.valueOf(String.valueOf(DAOUtil
					.masterDAO(aoMybatisSession, aoGetContentMap, ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER,
							"getApplicationSettings", "java.util.HashMap")));
			setMoState("Application Settings Fetched successfully for component Name:"
					+ aoGetContentMap.get("componentName"));
		}
		catch (ApplicationException loAoExp)
		{
			setMoState("Error while fetching Application Settings for component Name:"
					+ aoGetContentMap.get("componentName"));
		}
		return llPermittedContent;
	}
	/**
	 * Added during R6.3.0 QC8693, but this method will be useful as a general utility
	 * This method is used to get the application setting as String value
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoGetContentMap map containing information about component name
	 * @return long Permitted Content
	 * @throws ApplicationException If an application exception occurred
	 */
	public String getApplicationSettingsAsString(SqlSession aoMybatisSession, HashMap aoGetContentMap)
			throws ApplicationException
	{
		String settingsValue = "";
		try
		{
			settingsValue = (String)DAOUtil.masterDAO(aoMybatisSession, aoGetContentMap, ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER,
							"getApplicationSettings", "java.util.HashMap");
			setMoState("Application Settings Fetched successfully for component Name:"
					+ aoGetContentMap.get(HHSConstants.COMPONENT_NAME));
		}
		catch (ApplicationException loAoExp)
		{
			setMoState("Error while fetching Application Settings for component Name:"
					+ aoGetContentMap.get(HHSConstants.COMPONENT_NAME));
		}
		return settingsValue;
	}
	/**
	 * This method is used to get the due date for the provider from data base.
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param asProviderId a string value of provider Id
	 * @return a string value of provider name
	 * @throws ApplicationException If an application exception occurred
	 */
	public String getDueDate(SqlSession aoMybatisSession, String asProviderId) throws ApplicationException
	{
		String lsProviderName = null;
		try
		{
			lsProviderName = (String) DAOUtil.masterDAO(aoMybatisSession, asProviderId,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getDueDate", "java.lang.String");
			setMoState("Due date fetched successfully for Provider Id:" + asProviderId);
		}
		catch (ApplicationException loAoExp)
		{
			setMoState("Error occurred while fetching due date for Provider Id:" + asProviderId);
			throw loAoExp;
		}
		return lsProviderName;
	}

	/**
	 * This method is used to update the terms and condition flag for user. It
	 * will reset the flag every time a new terms and condition is uploaded into
	 * system.
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @return int Updated User Count
	 * @throws ApplicationException If an application exception occurred
	 */
	public int updateTermaAndConditionFlag(SqlSession aoMybatisSession) throws ApplicationException
	{
		int liUpdatedUserCount = 0;
		try
		{
			liUpdatedUserCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, null,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "updateTermaAndConditionFlag", null);
		}
		catch (ApplicationException loAoExp)
		{
			setMoState("Error occurred while updating user status:");
			throw loAoExp;
		}
		return liUpdatedUserCount;
	}

	/**
	 * This method will update the entry of the document with application if the
	 * document is deleted from P8 Server
	 * 
	 * @param aoMybatisSession valid mybatis sql session
	 * @return int updated row count
	 * @throws ApplicationException
	 */
	public int updateDeletedDocumentDetails(List<HashMap<String, String>> aoDocumentList, SqlSession aoMybatisSession,
			String asDocumentId) throws ApplicationException
	{
		int liUpdatedRowsCount = 0;
		try
		{
			Map<String, String> loMap = new HashMap<String, String>();
			loMap.put("asStatus", ApplicationConstants.NOT_STARTED_STATE);
			loMap.put("asDocumentId", asDocumentId);
			liUpdatedRowsCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loMap,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "updateDeletedDocumentDetails",
					"java.util.Map");
			FileNetOperationsUtils.updateDocumentDetailsInSections(aoDocumentList, aoMybatisSession);
			setMoState("Transaction Success:Delete status updated successfully");
		}
		catch (ApplicationException loAoExp)
		{
			setMoState("Error occurred while updating user status:");
			throw loAoExp;
		}
		return liUpdatedRowsCount;
	}

	/**
	 * This Method updates APPLICATION_STATUS in BUSINESS_APPLICATION table
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoApplicationStatusMap map containing document information
	 * @return a string value of application status
	 * @throws ApplicationException If an application exception occurred
	 */

	public String getApplicationStatus(SqlSession aoMybatisSession, HashMap aoApplicationStatusMap)
			throws ApplicationException
	{
		String lsAppStatus = null;
		try
		{
			lsAppStatus = (String) DAOUtil.masterDAO(aoMybatisSession, aoApplicationStatusMap,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getApplicationStatus", "java.util.HashMap");
			setMoState("Application Status fetched successfully for org Id:" + aoApplicationStatusMap.get("asOrgId"));
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting application status for org Id:" + aoApplicationStatusMap.get("asOrgId"));
			throw loExp;
		}
		return lsAppStatus;
	}

	/**
	 * This Method gets the details of the organization from organization table
	 * & BUSINESS_APPLICATION table
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param asOrganizationId organization id
	 * @return a map containing all the details of the organization
	 * @throws ApplicationException If an application exception occurred
	 */

	public HashMap<String, String> getProviderStatusDetails(SqlSession aoMybatisSession,
			HashMap<String, String> aoParametersMap) throws ApplicationException
	{
		HashMap<String, String> loOrganizationStatusMap = null;
		try
		{
			loOrganizationStatusMap = (HashMap<String, String>) DAOUtil.masterDAO(aoMybatisSession, aoParametersMap,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getProviderStatusDetails",
					"java.util.HashMap");
			setMoState("Application details fetched successfully for org Id:" + aoParametersMap.get("asOrganizationId"));
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting application details for org Id:" + aoParametersMap.get("asOrganizationId"));
			throw loExp;
		}
		return loOrganizationStatusMap;
	}

	/**
	 * This Method gets the details of the organization from organization table
	 * for batch & BUSINESS_APPLICATION table
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param asOrganizationId organization id
	 * @return a map containing all the details of the organization
	 * @throws ApplicationException If an application exception occurred
	 */

	@SuppressWarnings("unchecked")
	public HashMap<String, String> getProviderStatusDetailsBatch(SqlSession aoMybatisSession,
			HashMap<String, Object> aoParametersMap) throws ApplicationException
	{
		HashMap<String, String> loOrganizationStatusMap = null;
		HashMap<String, Object> loParamMap = null;
		try
		{
			loParamMap = (HashMap<String, Object>) aoParametersMap.get("aoArgumentsMap");
			loOrganizationStatusMap = (HashMap<String, String>) DAOUtil.masterDAO(aoMybatisSession, loParamMap,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getProviderStatusDetailsBatch",
					"java.util.HashMap");
			setMoState("Application details fetched successfully for org Id:" + aoParametersMap.get("asOrganizationId"));
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting application details for org Id:" + aoParametersMap.get("asOrganizationId"));
			throw loExp;
		}
		return loOrganizationStatusMap;
	}

	/**
	 * This is used to get the details of the all br application correspond to
	 * the provider
	 * Updated for Release 3.2.0, Defect 5641 - Changed Input Parameter
	 * @param aoMybatisSession valid sql session
	 * @param aoApplicationStatusMap HashMap containing Parameters for the Query
	 * @return loOrganizationBrAppStatus application status
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings("unchecked")
	public List<String> getProviderBRAppStatusDetails(SqlSession aoMybatisSession, HashMap aoApplicationStatusMap)
			throws ApplicationException
	{
		List<String> loOrganizationBrAppStatus = null;
		try
		{
			loOrganizationBrAppStatus = (List<String>) DAOUtil
					.masterDAO(aoMybatisSession, aoApplicationStatusMap,
							ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getProviderBRAppDetails",
							"java.util.HashMap");
			setMoState("Application details fetched successfully for Parameters:" + aoApplicationStatusMap);
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting application details for Parameters:" + aoApplicationStatusMap);
			throw loExp;
		}
		return loOrganizationBrAppStatus;
	}

	/**
	 * This is used to delete the entry from due date reminder table the
	 * provider
	 * 
	 * @param aoMybatisSession valid sql session
	 * @param asOrganizationId provider id
	 * @return loOrganizationBrAppStatus application status
	 * @throws ApplicationException If an application exception occurred
	 */
	public Integer deleteDueDateReminderEntry(SqlSession aoMybatisSession, String asOrganizationId)
			throws ApplicationException
	{
		int liNumOfRowsDeleted = 0;
		try
		{
			liNumOfRowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, asOrganizationId,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "deleteDueDateReminderEntry",
					"java.lang.String");
		}
		catch (ApplicationException loExp)
		{
			throw loExp;
		}
		return liNumOfRowsDeleted;
	}

	/**
	 * This is used to get the details from the document table
	 * 
	 * @param aoMybatisSession valid sql session
	 * @param asOrganizationId provider id
	 * @return loOrganizationBrAppStatus application status
	 * @throws ApplicationException If an application exception occurred
	 */
	public List<HashMap<String, String>> getDocumentDetails(SqlSession aoMybatisSession, String asDocumentId)
			throws ApplicationException
	{
		List<HashMap<String, String>> loDocDetailsList = null;
		try
		{
			loDocDetailsList = (List<HashMap<String, String>>) DAOUtil.masterDAO(aoMybatisSession, asDocumentId,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "getDocumentDetails", "java.lang.String");
		}
		catch (ApplicationException loExp)
		{
			throw loExp;
		}
		return loDocDetailsList;
	}

	/**
	 * This method updates Document ID in Document table on uploading a Document
	 * with same name and type
	 * @param asOldDocumentId Old Document ID
	 * @throws ApplicationException
	 */
	public int updateOldDocumentId(Map<String, String> asDocumentIds, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		try
		{
			return (Integer) DAOUtil.masterDAO(aoMybatisSession, asDocumentIds,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "updateOldDocumentId", "java.util.Map");
		}
		catch (ApplicationException loExp)
		{
			throw loExp;
		}

	}

	/**
	 * This Method Fetches the Status of Section based on the doc ID
	 * @param aoMybatisSession Sql Session
	 * @param aoApplicationStatusMap Map of Document Id
	 * @return Status of Section
	 * @throws ApplicationException
	 */
	public String checkSectionStatus(SqlSession aoMybatisSession, HashMap aoApplicationStatusMap)
			throws ApplicationException
	{
		String lsSectionStatus = "";
		try
		{
			lsSectionStatus = (String) DAOUtil.masterDAO(aoMybatisSession, aoApplicationStatusMap,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "checkSectionStatus", "java.util.HashMap");
		}
		catch (ApplicationException loExp)
		{
			throw loExp;
		}
		return lsSectionStatus;
	}

	/**
	 * 
	 * This Method Fetches the Status of Service based on the doc ID
	 * @param aoMybatisSession Sql Session
	 * @param aoApplicationStatusMap Map of Document Id
	 * @return Status of service
	 * @throws ApplicationException
	 */
	public String checkServiceStatus(SqlSession aoMybatisSession, HashMap aoApplicationStatusMap)
			throws ApplicationException
	{
		String lsServiceStatus = "";
		try
		{
			lsServiceStatus = (String) DAOUtil.masterDAO(aoMybatisSession, aoApplicationStatusMap,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "checkServiceStatus", "java.util.HashMap");
		}
		catch (ApplicationException loExp)
		{
			throw loExp;
		}
		return lsServiceStatus;
	}

	/**
	 * This Method Fetches the count of Document ID
	 * 
	 * @param aoMybatisSession Sql Session
	 * @param aoApplicationStatusMap Document ID
	 * @return Count of Document ID's
	 * @throws ApplicationException
	 */
	public int documentIdCount(SqlSession aoMybatisSession, HashMap aoApplicationStatusMap) throws ApplicationException
	{
		int liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoApplicationStatusMap,
				ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "documentIdCount", "java.util.HashMap");
		return liCount;
	}

	/**
	 * This Method Fetches the count of Document ID which are in "In Review" &
	 * "Approved" Status
	 * 
	 * @param aoMybatisSession Sql Session
	 * @param aoApplicationStatusMap Document ID
	 * @return Count of Document ID's
	 * @throws ApplicationException
	 */
	public int statusDocumentIdCount(SqlSession aoMybatisSession, HashMap aoApplicationStatusMap)
			throws ApplicationException
	{
		int liCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoApplicationStatusMap,
				ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "statusDocumentIdCount", "java.util.HashMap");
		return liCount;
	}

	/**
	 * This method is used to get the object name with which the document is
	 * attached
	 * <ul>
	 * <li>Get the document id from the parameters</li>
	 * <li>Execute query <code>getLinkedToObjectName</code> from
	 * <code>fileupload</code> mapper</li>
	 * <li>Return the object name</li>
	 * </ul>
	 * Release 3.3.0 Defect id 6445 
	 * @param aoMybatisSession sql session object
	 * @param asDocumentId document id
	 * @return String name of the object
	 * @throws ApplicationException
	 */
	public String getLinkedToObjectName(SqlSession aoMybatisSession, String asDocumentId) throws ApplicationException
	{
		String lsLinkedObjName = null;
		try
		{
			List<String> loLinkedObjNameList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asDocumentId,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER,
					ApplicationConstants.GET_LINKED_TO_OBJECT_NAME, ApplicationConstants.JAVA_LANG_STRING);
			if (null != loLinkedObjNameList)
			{
				if (loLinkedObjNameList.size() == 1)
				{
					lsLinkedObjName = loLinkedObjNameList.get(0);
				}
				else if (loLinkedObjNameList.contains(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROCUREMENT))
				{
					lsLinkedObjName = ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROCUREMENT;
				}
				else if (loLinkedObjNameList.contains(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROPOSAL))
				{
					lsLinkedObjName = ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROPOSAL;
				}
				//Release 3.3.0 Defect id 6445 
				else if(loLinkedObjNameList.contains(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_INVOICE))
				{
					lsLinkedObjName = ApplicationConstants.UPLOADING_DOCUMENT_TYPE_INVOICE;
				}
				else if(loLinkedObjNameList.contains(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_BUDGET))
				{
					lsLinkedObjName = ApplicationConstants.UPLOADING_DOCUMENT_TYPE_BUDGET;
				} 
			}
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting Object name for document id:" + asDocumentId);
			throw loExp;
		}
		return lsLinkedObjName;
	}

	
    /**
     * Changing Return type of Method from String to Integer in New Filling Defect fix after 4.0.2, adding map as input parameter for master DAO
     * Commenting code as we need only count as on backend we changed the query
     * This method is used to get the object name with which the document is
     * attached not in 'Draft' status
     * <ul>
     * <li>Get the document id from the parameters</li>
     * <li>Execute query <code>getLinkedToObjectName</code> from
     * <code>fileupload</code> mapper</li>
     * <li>Return the object name</li>
     * </ul>
     * @param aoMybatisSession sql session object
     * @param asDocumentId document id
     * @return String name of the object
     * @throws ApplicationException
     */
    @SuppressWarnings("unchecked")
	public Integer getLinkedToObjectNameNotInDraft(SqlSession aoMybatisSession, String asDocumentId) throws ApplicationException
    {
        int liLinkedObjCount = 0;
        try
        {
        	Map<String,String> loDataMap = new HashMap<String, String>();
        	loDataMap.put(HHSConstants.BULK_UPLOAD_DOCUMENT_ID, asDocumentId);
        	loDataMap.put(HHSConstants.PROCUREMENT_STATUS, HHSR5Constants.ONE);
        	loDataMap.put(HHSConstants.PROPOSAL_STATUS, HHSConstants.DEFAULT_PROPOSAL_STATUS);
        	loDataMap.put(HHSR5Constants.PROPOSAL_STATUS_RETURNED, HHSR5Constants.RETURNED_FOR_REVISION_PROPOSAL);
        	loDataMap.put(HHSR5Constants.DEFAULT_STATUS, HHSR5Constants.ZERO);
        	loDataMap.put(HHSR5Constants.FILING_STATUS_VIEW, HHSConstants.RETURNED);
        	loDataMap.put(HHSR5Constants.APPLICATION_STATUS_DRAFT, HHSConstants.DRAFT);
        	loDataMap.put(HHSR5Constants.APPLICATION_STATUS_RFR, ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS);
            List<String> loLinkedObjNameList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
                    ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER,
                    ApplicationConstants.GET_LINKED_TO_OBJECT_NAME_NOT_IN_DRAFT, ApplicationConstants.JAVA_UTIL_MAP);
            liLinkedObjCount = loLinkedObjNameList.size();
            /*if (null != loLinkedObjNameList)
            {
                if (loLinkedObjNameList.size() == 1)
                {
                    lsLinkedObjName = loLinkedObjNameList.get(0);
                }
                else if (loLinkedObjNameList.contains(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROCUREMENT))
                {
                    lsLinkedObjName = ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROCUREMENT;
                }
                else if (loLinkedObjNameList.contains(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROPOSAL))
                {
                    lsLinkedObjName = ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROPOSAL;
                }
                else if (loLinkedObjNameList.contains(HHSR5Constants.FILING_STATUS_VIEW))
                {
                    lsLinkedObjName = HHSR5Constants.FILING_STATUS_VIEW;
                }
                
            }*/
        }
        catch (ApplicationException loExp)
        {
            setMoState("Error while getting Object name for document id:" + asDocumentId);
            throw loExp;
        }
        return liLinkedObjCount;
    }
    
    	
    /**
     * This method is used to get the object name with which the document is
     * attached in 'Draft' status
     * <ul>
     * <li>Get the document id from the parameters</li>
     * <li>Execute query <code>getLinkedToObjectName</code> from
     * <code>fileupload</code> mapper</li>
     * <li>Return the object name</li>
     * </ul>
     * @param aoMybatisSession sql session object
     * @param asDocumentId document id
     * @return String name of the object
     * @throws ApplicationException
     */
    public String getLinkedToObjectNameInDraft(SqlSession aoMybatisSession, String asDocumentId) throws ApplicationException
    {
        String lsLinkedObjName = null;
        try
        {
            List<String> loLinkedObjNameList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, asDocumentId,
                    ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER,
                    ApplicationConstants.GET_LINKED_TO_OBJECT_NAME_IN_DRAFT, ApplicationConstants.JAVA_LANG_STRING);
            if (null != loLinkedObjNameList)
            {
                if (loLinkedObjNameList.size() == 1)
                {
                    lsLinkedObjName = loLinkedObjNameList.get(0);
                }
                else if (loLinkedObjNameList.contains(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROCUREMENT))
                {
                    lsLinkedObjName = ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROCUREMENT;
                }
                else if (loLinkedObjNameList.contains(ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROPOSAL))
                {
                    lsLinkedObjName = ApplicationConstants.UPLOADING_DOCUMENT_TYPE_PROPOSAL;
                }
            }
        }
        catch (ApplicationException loExp)
        {
            setMoState("Error while getting Object name for document id:" + asDocumentId);
            throw loExp;
        }
        return lsLinkedObjName;
    }
    
	
	
	/**
	 * This method will delete all the document uplaoded or added for
	 * Solicitation and finance
	 * <ul>
	 * <li>Get the document id from the argument parameters</li>
	 * <li>Execute <code>deleteSMAndFinanceDocs</code> query to delete the
	 * document</li>
	 * </ul>
	 * @param aoMybatisSession sql session object
	 * @param asDocumentId document id to be deleted
	 * @return integer line of rows deleted
	 * @throws ApplicationException if any exception occurs
	 */
	public Integer removeSMAndFinanceDocuments(SqlSession aoMybatisSession, String asDocumentId)
			throws ApplicationException
	{
		Integer liTotalRowsDeleted = 0;
		Integer liProcRowsDeleted = 0;
		Integer liAwardRowsDeleted = 0;
		Integer liProposaRowsDeleted = 0;
		Integer liContractRowsDeleted = 0;
		Integer liBudgetRowsDeleted = 0;
		Integer liInvoiceRowsDeleted = 0;
		Integer liBAFORowsDeleted = 0;

		try
		{
			liProcRowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, asDocumentId,
					ApplicationConstants.MAPPER_CLASS_PROCUREMENT_MAPPER,
					ApplicationConstants.DEL_PROCUREMENT_DOCS_VAULT, ApplicationConstants.JAVA_LANG_STRING);
			liAwardRowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, asDocumentId,
					ApplicationConstants.MAPPER_CLASS_AWARDS_MAPPER, ApplicationConstants.REMOVE_AWARD_DOCS_VAULT,
					ApplicationConstants.JAVA_LANG_STRING);
			liProposaRowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, asDocumentId,
					ApplicationConstants.MAPPER_CLASS_PROPOSAL_MAPPER, ApplicationConstants.REMOVE_PROPOSAL_DOCS_VAULT,
					ApplicationConstants.JAVA_LANG_STRING);
			liContractRowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, asDocumentId,
					ApplicationConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					ApplicationConstants.DELETE_CONTRACT_FINANCIAL_DOC_VAULT, ApplicationConstants.JAVA_LANG_STRING);
			liBudgetRowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, asDocumentId,
					ApplicationConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					ApplicationConstants.DELETE_BUDGET_FINANCIAL_DOC_VAULT, ApplicationConstants.JAVA_LANG_STRING);
			liInvoiceRowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, asDocumentId,
					ApplicationConstants.MAPPER_CLASS_CONTRACT_BUDGET_MAPPER,
					ApplicationConstants.DELETE_INVOICE_FINANCIAL_DOC_VAULT, ApplicationConstants.JAVA_LANG_STRING);
			liBAFORowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, asDocumentId,
					ApplicationConstants.MAPPER_CLASS_EVALUATION_MAPPER, ApplicationConstants.REMOVE_BAFO_DOCS_VAULT,
					ApplicationConstants.JAVA_LANG_STRING);

			liTotalRowsDeleted = liProcRowsDeleted + liAwardRowsDeleted + liProposaRowsDeleted + liContractRowsDeleted
					+ liBudgetRowsDeleted + liInvoiceRowsDeleted + liBAFORowsDeleted;
		}
		catch (ApplicationException loExp)
		{
			setMoState("Error while deleting SM snf financial documents:" + asDocumentId);
			throw loExp;
		}
		return liTotalRowsDeleted;
	}
	
	/**
	 * This method is used to get the application settings for maintenance queue
	 * Added for enhancement 6508 for Release 3.6.0
	 * @param aoMybatisSession mybatis sql session
	 * @param aoGetContentMap map containing information about component name
	 * @return hashmap containing setting_values
	 * @throws ApplicationException If an application exception occurred
	 */
	@SuppressWarnings("rawtypes")
	public String getAppSettingsForMaintenanceQueue(SqlSession aoMybatisSession, HashMap aoGetContentMap)
			throws ApplicationException
	{
		String settingVal = "";
		try
		{
			settingVal = (String)DAOUtil
					.masterDAO(aoMybatisSession, aoGetContentMap, ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER,
							"getApplicationSettings", "java.util.HashMap");
			setMoState("Application Settings Fetched successfully for component Name:"
					+ aoGetContentMap.get("componentName"));
		}
		catch (ApplicationException loAoExp)
		{
			setMoState("Error while fetching Application Settings for component Name:"
					+ aoGetContentMap.get("componentName"));
		}
		return settingVal;
	}
	// Added below method for Release 5
	/** This method will update folder modified info into the DataBase
	 * @param aoMybatisSession
	 * @param aoModifiedInfoMap
	 * @return loFolderUpdateStatus
	 * @throws ApplicationException
	 */
	public Boolean updateFolderModifiedInfo(SqlSession aoMybatisSession, HashMap aoModifiedInfoMap)
	throws ApplicationException
{
Boolean loFolderUpdateStatus = false;
return loFolderUpdateStatus;
}
	
	/**This method will check linkage od a document to any entity in DB
	 * @param aoMybatisSession
	 * @param aoHashMap
	 * @param aoReqMap
	 * @return map with list of documents
	 * @throws Exception
	 */
	public HashMap<String,List<Document>> checkLinkageinDB(SqlSession aoMybatisSession, HashMap<String, List<Document>> aoHashMap, HashMap<String, String> aoReqMap) throws ApplicationException
	{
		List<Document> loDocList = new ArrayList<Document>();
		//HashMap<String,List<Document>> loDataMap = new HashMap<String, List<Document>>();
		try
		{
			String lsOrgType = aoReqMap.get(HHSR5Constants.ORGANIZATION_TYPE);
			String lsPropertyPath = P8Constants.ERROR_PROPERTY_FILE;
			String lsOrgId = aoReqMap.get(HHSR5Constants.ORGANIZATION_ID);
			String lsMessgetoDisplay = null;
			if(null != aoHashMap && !aoHashMap.isEmpty())
			{
				loDocList = aoHashMap.get(HHSR5Constants.DOC_LIST);
				for (Iterator iterator = loDocList.iterator(); iterator
						.hasNext();)
					{
					
						Document loDocument = (Document) iterator.next();
						String lsDoctype = loDocument.getDocType();
						if(null != lsDoctype 
								&& (ApplicationConstants.DOCUMENT_TYPE_APPLICATION_TERMS_AND_CONDITIONS.equalsIgnoreCase(lsDoctype)
								|| ApplicationConstants.DOCUMENT_TYPE_SYSTEM_TERMS_AND_CONDITIONS.equalsIgnoreCase(lsDoctype)
								|| ApplicationConstants.DOCUMENT_TYPE_APPENDIX_A.equalsIgnoreCase(lsDoctype)
								|| ApplicationConstants.DOCUMENT_TYPE_STANDARD_CONTRACT.equalsIgnoreCase(lsDoctype)))
						{
							throw new ApplicationException("The selected documents contains one of the System Document which cannot be deleted. Please reselect and try again ");
						}
						if (ApplicationConstants.PROVIDER_ORG.equalsIgnoreCase(lsOrgType))
						{
								checkLinkage(aoMybatisSession, lsOrgId, loDocument);
						}
						if(loDocument.getDeleteFlag() == 0)
						{
							lsMessgetoDisplay = FileNetOperationsUtils.checkLinkToAnyOtherObject(
									aoReqMap.get(HHSR5Constants.ORGANIZATION_TYPE),
									loDocument.getDocumentId(), lsPropertyPath, Boolean.FALSE);
									loDocument.setMsEntityId(lsMessgetoDisplay);
						}
						
				}
				List<Document> loFolderList = (List<Document>)aoHashMap.get(HHSR5Constants.FOLDER_LIST);
				loDocList = FileNetOperationsUtils.getUniqueListVal(loDocList);
				loFolderList = FileNetOperationsUtils.getUniqueListVal(loFolderList);
				aoHashMap.put(HHSR5Constants.DOC_LIST, loDocList);
				aoHashMap.put(HHSR5Constants.FOLDER_LIST,loFolderList);
			}

			
		}
		catch (ApplicationException loExp)
        {
            setMoState("Error while getting linkage of document");
            throw loExp;
        }
		catch (Exception loExp)
        {
            setMoState("Error while getting linkage of document");
            throw new ApplicationException("Error while getting linkage of document", loExp);
        }
		return aoHashMap;
	}

	/**
	 * @param aoMybatisSession
	 * @param lsOrgId
	 * @param loDocument
	 * @throws ApplicationException
	 * @throws Exception
	 */
	private void checkLinkage(SqlSession aoMybatisSession, String lsOrgId, Document loDocument)
			throws ApplicationException, Exception
	{
		HashMap<String, String> aoTemp = new HashMap<String ,String>();
		int liCount = 0;
		aoTemp.put(HHSR5Constants.DOCUMENT_ID, loDocument.getDocumentId());
		liCount = documentIdCount(aoMybatisSession, aoTemp);
		if (liCount <= 1)
		{ 
			if (loDocument.getDeleteFlag() == 0)
			{
				checkApplicationStatusForLinkedDocFinal(aoMybatisSession,loDocument,lsOrgId);
			}
		}
		else if(liCount > 1)
		{
				loDocument.setDeleteFlag(statuscheckForMultipleApplications(aoMybatisSession,loDocument));
		}
		// Adding char500 check for defect # after 4.0.2, we are putting char500 flag to true on basis of Doc Type only.
		//New Filling Check after 4.0.2
		if ((loDocument.getDocType().equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_DOC_TYPE)
				|| loDocument.getDocType().equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_EXT1_DOC_TYPE)
				|| loDocument.getDocType().equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_EXT2_DOC_TYPE) || loDocument
				.getDocType().equalsIgnoreCase(P8Constants.PROPERTY_CE_CHAR500_EXTENSION)))
		{
			loDocument.setChar500Flag(true);
		}
		// Commenting below if block for fixing defect # 8455
		/*if(ApplicationConstants.CONTRACT_GRANT_DOC_TYPE.equalsIgnoreCase(loDocument.getDocType())
		|| ApplicationConstants.KEY_STAFF_DOC_TYPE.equalsIgnoreCase(loDocument.getDocType())
		|| ApplicationConstants.CAPABILITY_STATEMENT_DOC_TYPE.equalsIgnoreCase(loDocument.getDocType()))
		{
			loDocument.setDeleteFlag(0);
		}*/
	}
	/**
	 * This method is used to check application status for linked document final
	 * @param aoSqlSession a SqlSession object
	 * @param asDocument 
	 * @param asUserId
	 * @throws Exception
	 */
	private  void checkApplicationStatusForLinkedDocFinal(SqlSession aoSqlSession, Document asDocument,String asUserId) throws Exception
	{
		HashMap<String, String> loMap = new HashMap<String, String>();
		String lsSectionStatus = null;
		
		try
		{
			loMap.put(HHSR5Constants.DOCUMENT_ID, asDocument.getDocumentId());
			loMap.put(HHSConstants.PROVIDER_ID, asUserId);
			lsSectionStatus = checkSectionStatus(aoSqlSession, loMap);
			if (lsSectionStatus != null && lsSectionStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
			{
				asDocument.setDeleteFlag(0);
			}
			else
			{
				// Adding Linkage check to other objects like Proposal,Procurement e.t.c for new filling issue after 4.0.2
				int liObjectCount = getLinkedToObjectNameNotInDraft(aoSqlSession, asDocument.getDocumentId());
				if(liObjectCount == 0)
				{
					// if delete via parent check
					if(asDocument.isDeletedViaParent())
					{
						asDocument.setDeleteFlag(1);
					}
					else
					{
						asDocument.setDeleteFlag(2);
					}
				}
				else
				{
					asDocument.setDeleteFlag(0);
				}
			}
		}
		catch (Exception loExp)
		{
			setMoState("Error while getting Object name for document id:");
			throw loExp;
		}

	}
	/**
	 * This method is used to check status for multiple applications
	 * @param aoSqlSession a SqlSession object
	 * @param asDocument 
	 * @return loFlag integer value
	 * @throws ApplicationException
	 */
	public  int statuscheckForMultipleApplications(SqlSession aoSqlSession,  Document asDocument) throws ApplicationException
	{

		Integer loFlag = 2;
		if(asDocument.isDeletedViaParent())
		{
			loFlag = 1;
		}
		int liDocIdSatutsCount = 0;
		Channel loChannel = new Channel();
		HashMap loAppStatusDocMap = new HashMap();
		loAppStatusDocMap.put(ApplicationConstants.DOCUMENT_ID, asDocument.getDocumentId());
		liDocIdSatutsCount = statusDocumentIdCount(aoSqlSession, loAppStatusDocMap);
		if (liDocIdSatutsCount > 0)
		{
			loFlag = 0;
		}

		return loFlag;
	}
	/**
	 * This method is used to update entity status in Database
	 * @param aoSqlSession a SqlSession object
	 * @param aoEntityMap
	 * @return loCount an integer value
	 * @throws ApplicationException
	 */
	public Integer updateEntityStatusinDB(SqlSession aoSqlSession, HashMap<String, List<Document>> aoEntityMap) throws ApplicationException
	{
		List<HashMap<String, String>> loListDetail = new ArrayList<HashMap<String,String>>();
		Integer loCount = 0;
		try
		{
			if(null != aoEntityMap && !aoEntityMap.isEmpty()){
				List<Document> loList = (List<Document>)aoEntityMap.get(HHSR5Constants.DOC_LIST);
				for (Iterator iterator = loList.iterator(); iterator.hasNext();) {
					Document loDocument = (Document) iterator.next();
					// Removing char500 condition for New Filling Check after 4.0.2 , as we are setting char500 flag on basis of Doc type only in previous Service.
					if(null != loDocument.getDeleteFlag() && loDocument.getDeleteFlag() > 0)
					{
						loListDetail = getDocumentDetails(aoSqlSession, loDocument.getDocumentId());
						updateDeletedDocumentDetails(loListDetail, aoSqlSession, loDocument.getDocumentId());
						loCount = removeSMAndFinanceDocuments(aoSqlSession, loDocument.getDocumentId());
					}
					
					
				}
			}
			
				
		
		}
		catch (Exception loExp)
		{
		    setMoState("Error while getting Object name for document id:");
		}
		return loCount;	
	}
	/**
	 * This method is used to check filings status in Database
	 * @param aoSqlSession a SqlSession object
	 * @param aoOrgId
	 * @return lbFlag a boolean value
	 */
	public Boolean checkFilingStatusInDb(SqlSession aoSqlSession, String aoOrgId)
	{
		Boolean lbFlag = true;
		String lsProcStatus = null;
		try
		{
			lsProcStatus = (String) DAOUtil.masterDAO(aoSqlSession, aoOrgId,
					ApplicationConstants.MAPPER_CLASS_FILE_UPLOAD_MAPPER, "checkFilingStatusInDb",
					"java.lang.String");
			if(null != lsProcStatus && !lsProcStatus.isEmpty() && lsProcStatus.equalsIgnoreCase("In Progress"))
			{
				lbFlag = false;
			}
}
		catch (Exception loExp)
		{
		    setMoState("Error while getting Object name for document id:");
		}
		return lbFlag;
	}
	/** this method will get the linkage count from view and populate a map corresponding to it.
	 * Added for defect fix # 8504
	 * @param aiLinkedCount
	 * @param asDocumentId
	 * @return
	 */
	public Map<String,Boolean> intermediateServiceCreatingMap(Integer aiLinkedCount,String asDocumentId)
	{
		Map<String,Boolean> loDataMap = new HashMap<String, Boolean>();
		if(aiLinkedCount == 0)
		{
			loDataMap.put(asDocumentId, Boolean.FALSE);
		}
		else
		{
			loDataMap.put(asDocumentId, Boolean.TRUE);
		}
		return loDataMap;
	}
}
