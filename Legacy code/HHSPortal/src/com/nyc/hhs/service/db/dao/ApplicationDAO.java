package com.nyc.hhs.service.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.Population;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.DAOUtil;

/**
 * This class provides functionality for Business Application which includes
 * database management of document type, population, language, service setting
 * and geography.
 * 
 */

public class ApplicationDAO
{
	private static final LogInfo LOG_OBJECT = new LogInfo(ApplicationDAO.class);

	/**
	 * This method deletes document type info from db
	 * 
	 * @param aoMyBatisSession - mybatis session
	 * @param asAppId - current application id
	 * @param asOrgId - current organization id
	 * @param asFormName - current form name
	 * @param asFormVersion - current form version
	 * @param asDocCategory - current doc category
	 * @param asDocType - current document type
	 * @throws ApplicationException
	 */
	public void deleteDocTypeInfo(SqlSession aoMyBatisSession, String asAppId, String asOrgId, String asFormName,
			String asFormVersion, String asDocCategory, String asDocType) throws ApplicationException
	{
		try
		{
			Map<String, String> loMap = new HashMap<String, String>();
			loMap.put("asOrgId", asOrgId);
			loMap.put("asFormName", asFormName);
			loMap.put("asFormVersion", asFormVersion);
			loMap.put("asDocCategory", asDocCategory);
			loMap.put("asDocType", asDocType);
			String lsFunctionName = "deleteDocTypeInfo";
			if (asAppId != null)
			{
				loMap.put("asAppId", asAppId);
			}
			else
			{
				lsFunctionName = "deleteDocTypeInfoForOrg";
			}
			DAOUtil.masterDAO(aoMyBatisSession, loMap, ApplicationConstants.MAPPER_CLASS_APPLICATION, lsFunctionName,
					"java.util.Map");
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error occured while deleting Doc info in Db", loEx);
			throw new ApplicationException("Error occured while deleting Doc info in Db" + loEx);
		}
	}

	/**
	 * This method inserts document type info in db
	 * 
	 * @param aoMyBatisSession - mybatis session
	 * @param asAppId - current application id
	 * @param asOrgId - current organization id
	 * @param asFormName - current form name
	 * @param asFormVersion - current form version
	 * @param asDocCategory - current doc category
	 * @param asDocType - current document type
	 * @param asStatus - current status
	 * @param asUserId - current user id
	 * @param asSection - current section
	 * @throws ApplicationException
	 */
	public void insertDocTypeInfo(SqlSession aoMyBatisSession, String asAppId, String asOrgId, String asFormName,
			String asFormVersion, String asDocCategory, String asDocType, String asStatus, String asUserId,
			String asSection) throws ApplicationException
	{
		try
		{
			Map<String, String> loMap = new HashMap<String, String>();
			loMap.put("asAppId", asAppId);
			loMap.put("asOrgId", asOrgId);
			loMap.put("asFormName", asFormName);
			loMap.put("asFormVersion", asFormVersion);
			loMap.put("asDocCategory", asDocCategory);
			loMap.put("asDocType", asDocType);
			loMap.put("asStatus", asStatus);
			loMap.put("asUserId", asUserId);
			loMap.put("asFormId", asFormName + "_" + asFormVersion);
			loMap.put("asSection", asSection);
			DAOUtil.masterDAO(aoMyBatisSession, loMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"insertDocTypeInfo", "java.util.Map");
		}
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error occured while inserting Doc info in Db", loEx);
			throw new ApplicationException("Error occured while inserting Doc info in Db" + loEx);
		}
	}

	/**
	 * This method adds population to application.
	 * 
	 * @param aoPopulationdatalist - population data list
	 * @param aoMapRequiredDetails - required details
	 * @param aoMyBatisSession - mybatis session
	 * @return - lbSuccessStatus
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public Boolean addPopulationToApplication(List<Population> aoPopulationdatalist,
			Map<String, Object> aoMapRequiredDetails, SqlSession aoMyBatisSession) throws ApplicationException
	{
		Boolean lbSuccessStatus;
		LOG_OBJECT.Debug("Inside setPopulationForApplication");
		try
		{
			Population loPopulationBean = null;
			for (int liPopulation = 0; liPopulation < aoPopulationdatalist.size(); liPopulation++)
			{
				loPopulationBean = (Population) aoPopulationdatalist.get(liPopulation);
				DAOUtil.masterDAO(aoMyBatisSession, loPopulationBean, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"savePopulation", "com.nyc.hhs.model.Population");
			}
			lbSuccessStatus = true;
			if (aoMapRequiredDetails.get("asAppId") != null)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoMapRequiredDetails,
						ApplicationConstants.MAPPER_CLASS_APPLICATION, "deleteSubSectionDetails", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoMapRequiredDetails,
						ApplicationConstants.MAPPER_CLASS_APPLICATION, "insertSubSectionDetails", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoMapRequiredDetails,
						ApplicationConstants.MAPPER_CLASS_APPLICATION, "updateBusinessAppModifiedDate", "java.util.Map");

			}
		}
		catch (Exception aoException)
		{
			throw new ApplicationException("Error occured while saving data for basic population ", aoException);
		}
		return lbSuccessStatus;
	}

	/**
	 * This method inserts language info in database
	 * 
	 * @param aoMyBatisSession - mybatis session
	 * @param asOrgId - current organization id
	 * @param asElementType - current element type
	 * @param aoElementIdList - current element id list
	 * @param aoRequiredDetailsM - map containing required details
	 * @return - lsStatus
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "static-access" })
	public Boolean insertLanguageInfo(SqlSession aoMyBatisSession, String asOrgId, String asElementType,
			ArrayList<String> aoElementIdList, String asUserId, Map<String, Object> aoRequiredDetailsM)
			throws ApplicationException
	{
		Boolean lsStatus = false;
		LOG_OBJECT.Debug("Inside insertLanguageInfo with parameter aoLanguageInfo:");
		try
		{
			deleteLanguageInfo(aoMyBatisSession, asOrgId, asElementType);
			HashMap<String, Object> loLanguageInfo = new HashMap<String, Object>();
			if (aoElementIdList != null)
			{
				for (int liElementId = 0; liElementId < aoElementIdList.size(); liElementId++)
				{
					String lsElementId = aoElementIdList.get(liElementId);
					loLanguageInfo.put("asElementId", lsElementId);
					loLanguageInfo.put("asElementType", asElementType);
					loLanguageInfo.put("asOrgId", asOrgId);
					loLanguageInfo.put("asUserID", asUserId);
					loLanguageInfo.put("aoLanguagesInterpretationFlag",
							aoRequiredDetailsM.get("aoLanguagesInterpretationFlag"));
					DAOUtil.masterDAO(aoMyBatisSession, loLanguageInfo, ApplicationConstants.MAPPER_CLASS_APPLICATION,
							"insertLanguageInfo", "java.util.HashMap");
				}
			}
			lsStatus = true;
			if (aoRequiredDetailsM.get("asAppId") != null)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoRequiredDetailsM, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"deleteSubSectionDetails", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoRequiredDetailsM, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertSubSectionDetails", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoRequiredDetailsM, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateBusinessAppModifiedDate", "java.util.Map");
			}
		}
		catch (Exception loEx)
		{
			throw new ApplicationException(
					"Error occured while inserting language info into organization_details table", loEx);
		}

		return lsStatus;
	}

	/**
	 * This method deletes language info from database
	 * 
	 * @param aoMyBatisSession - mybatis session
	 * @param asOrgId - current organization id
	 * @param asElementType - current element type
	 * @return - lsStatus
	 * @throws ApplicationException
	 */
	@SuppressWarnings("static-access")
	public Boolean deleteLanguageInfo(SqlSession aoMyBatisSession, String asOrgId, String asElementType)
			throws ApplicationException
	{
		Boolean lsStatus = false;
		LOG_OBJECT.Debug("Inside deleteLanguageInfo with parameter aoLanguageInfo:");
		try
		{
			HashMap<String, Object> loLanguageInfo = new HashMap<String, Object>();
			loLanguageInfo.put("asElementType", asElementType);
			loLanguageInfo.put("asOrgId", asOrgId);
			DAOUtil.masterDAO(aoMyBatisSession, loLanguageInfo, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"deleteLanguageInfo", "java.util.HashMap");
			lsStatus = true;
		}
		catch (Exception loEx)
		{
			throw new ApplicationException(
					"Error occured while deleting language info into organization_details table", loEx);
		}

		return lsStatus;
	}

	/**
	 * this method adds setting to service
	 * 
	 * @param aoMyBatisSession - mybatis session
	 * @param asOrgId - current organization id
	 * @param asElementType - current element type
	 * @param aoElementIdList - current element id list
	 * @param asBottomCheckBox - bottom check box
	 * @param asServiceAppId - current service application id
	 * @param aoRequiredDetailsM - map containing required details
	 * @return - lbStatus
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean addSettingToService(SqlSession aoMyBatisSession, String asOrgId, String asElementType,
			ArrayList aoElementIdList, String asBottomCheckBox, String asServiceAppId, String asUserId,
			Map<String, Object> aoRequiredDetailsM) throws ApplicationException
	{

		boolean lbStatus = false;
		try
		{
			HashMap loMap = new HashMap();
			loMap.put("asOrgId", asOrgId);
			loMap.put("asServiceAppId", asServiceAppId);
			loMap.put("asElementType", asElementType);
			DAOUtil.masterDAO(aoMyBatisSession, loMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"deleteSettingTypeInfo", "java.util.HashMap");
			lbStatus = true;
			HashMap<String, Object> loUserInfo = new HashMap<String, Object>();
			if (aoElementIdList != null && !aoElementIdList.isEmpty())
			{
				for (int liListCounter = 0; liListCounter < aoElementIdList.size(); liListCounter++)
				{
					String lsElementId = (String) aoElementIdList.get(liListCounter);
					loUserInfo.put("asElementId", lsElementId);
					loUserInfo.put("asElementType", asElementType);
					loUserInfo.put("asServiceAppId", asServiceAppId);
					loUserInfo.put("asOrgId", asOrgId);
					loUserInfo.put("asBottomCheckBox", asBottomCheckBox);
					loUserInfo.put("asUserId", asUserId);
					DAOUtil.masterDAO(aoMyBatisSession, loUserInfo, ApplicationConstants.MAPPER_CLASS_APPLICATION,
							"addSettingToService", "java.util.HashMap");
				}
			}
			else
			{
				loUserInfo.put("asElementId", null);
				loUserInfo.put("asElementType", asElementType);
				loUserInfo.put("asServiceAppId", asServiceAppId);
				loUserInfo.put("asOrgId", asOrgId);
				loUserInfo.put("asBottomCheckBox", asBottomCheckBox);
				loUserInfo.put("asUserId", asUserId);
				DAOUtil.masterDAO(aoMyBatisSession, loUserInfo, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"addSettingToService", "java.util.HashMap");
			}
			lbStatus = true;
			if (aoRequiredDetailsM.get("asAppId") != null)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoRequiredDetailsM, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"deleteServiceSubSectionDetails", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoRequiredDetailsM, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertSubSectionDetails", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoRequiredDetailsM, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateBusiServiceApplicationModifiedDate", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoRequiredDetailsM, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateServiceApplicationModifiedDate", "java.util.Map");
			}
		}
		catch (Exception loEx)
		{
			throw new ApplicationException("Error occured while inserting org id,element id and elment type", loEx);
		}

		return lbStatus;
	}

	/**
	 * This method adds geography to application.
	 * 
	 * @param aoMyBatisSession - mybatis session
	 * @param asOrgId - current organization id
	 * @param asElementType - current element type
	 * @param aoElementIdList - current element id list
	 * @param asBottomCheckBox - bottom check box
	 * @param aoRequiredDetailsM - map containing required details
	 * @return - lsStatus
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Boolean addGeogaphyToApplication(SqlSession aoMyBatisSession, String asOrgId, String asElementType,
			ArrayList aoElementIdList, String asBottomCheckBox, String asUserID, Map<String, Object> aoRequiredDetailsM)
			throws ApplicationException
	{

		boolean lsStatus = false;
		try
		{
			HashMap loMap = new HashMap();
			loMap.put("asOrgId", asOrgId);
			loMap.put("asElementType", asElementType);

			DAOUtil.masterDAO(aoMyBatisSession, loMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
					"deleteGeographyTypeInfo", "java.util.HashMap");
			lsStatus = true;
			HashMap<String, Object> loUserInfo = new HashMap<String, Object>();
			if (aoElementIdList != null && !aoElementIdList.isEmpty())
			{
				for (int liElementId = 0; liElementId < aoElementIdList.size(); liElementId++)
				{
					String lsElementId = (String) aoElementIdList.get(liElementId);
					loUserInfo.put("asElementId", lsElementId);
					loUserInfo.put("asElementType", asElementType);
					loUserInfo.put("asOrgId", asOrgId);
					loUserInfo.put("asBottomCheckBox", asBottomCheckBox);
					loUserInfo.put("asUserID", asUserID);
					DAOUtil.masterDAO(aoMyBatisSession, loUserInfo, ApplicationConstants.MAPPER_CLASS_APPLICATION,
							"addGeogaphyToApplication", "java.util.HashMap");
				}
			}
			else
			{
				loUserInfo.put("asElementId", null);
				loUserInfo.put("asElementType", asElementType);
				loUserInfo.put("asOrgId", asOrgId);
				loUserInfo.put("asUserID", asUserID);
				loUserInfo.put("asBottomCheckBox", asBottomCheckBox);
				DAOUtil.masterDAO(aoMyBatisSession, loUserInfo, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"addGeogaphyToApplication", "java.util.HashMap");
			}
			lsStatus = true;
			if (aoRequiredDetailsM.get("asAppId") != null)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoRequiredDetailsM, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"deleteSubSectionDetails", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoRequiredDetailsM, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertSubSectionDetails", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoRequiredDetailsM, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateBusinessAppModifiedDate", "java.util.Map");
			}
		}
		catch (Exception loEx)
		{
			throw new ApplicationException("Error occured while inserting org id,element id and elment type", loEx);
		}

		return lsStatus;
	}

	/**
	 * This method updates doc id on selection of radio button
	 * 
	 * @param aoInputParamMap - map containing input parameter
	 * @param aoMyBatisSession - mybatis session
	 * @return - lbSuccessStatus
	 * @throws ApplicationException
	 */
	public Boolean updateDocIdOnRadioSelect(Map<String, Object> aoInputParamMap, final SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean lbSuccessStatus = false;
		try
		{
			if (aoInputParamMap.get("asSectionId") != null
					&& aoInputParamMap.get("asSectionId").equals("servicessummary"))
			{
				if (aoInputParamMap.get("asDocType") != null
						&& ((aoInputParamMap.get("asDocType").toString()
								.equalsIgnoreCase(P8Constants.PROPERTY_CE_DOC_TYPE_CEO)) || (aoInputParamMap.get(
								"asDocType").toString().equalsIgnoreCase(P8Constants.PROPERTY_CE_DOC_TYPE_CFO))))
				{
					aoInputParamMap.remove("asDocType");
					aoInputParamMap.put("asDocType", P8Constants.PROPERTY_CE_DOC_TYPE_KEYSTAFF_RESUME);
				}
				DAOUtil.masterDAO(aoMyBatisSession, aoInputParamMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateDocumentsServiceSummary", "java.util.Map");
			}
			else
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoInputParamMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateDocuments", "java.util.Map");
			}
			lbSuccessStatus = true;
		}
		catch (Exception aoAppEx)
		{
			throw new ApplicationException("Error occured while updating document_id in document table", aoAppEx);
		}
		return lbSuccessStatus;
	}

	/**
	 * This method removes documents from application.
	 * 
	 * @param aoRemDocMap - map containing doc info to be removed
	 * @param aoMyBatisSession - mybatis session
	 * @return - lbSuccessStatus
	 * @throws ApplicationException
	 */
	public Boolean removeDocFromApplication(Map<String, Object> aoRemDocMap, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		Boolean lbSuccessStatus = false;
		try
		{
			if (aoRemDocMap.get("asSectionId") == null)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoRemDocMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"removeDocuments", "java.util.Map");
			}
			else if (aoRemDocMap.get("asSectionId").equals("servicessummary") && aoRemDocMap.get("asSectionId") != null)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoRemDocMap, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"removeDocumentsServiceSummary", "java.util.Map");
			}

			lbSuccessStatus = true;
		}
		catch (Exception aoAppEx)
		{
			throw new ApplicationException("Error occured while updating document_id in document table", aoAppEx);
		}
		return lbSuccessStatus;
	}

	/**
	 * This method inserts and updates document status in database
	 * 
	 * @param aoProps - map containing required information
	 * @param aoMyBatisSession - mybatis session
	 * @throws ApplicationException
	 */
	public void insertUpdateDocStatus(Map<String, Object> aoProps, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		try
		{
			int liCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoProps,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getDocStatusCount", "java.util.Map"); // getDocStatusCountForOrg
			if (liCount > 0)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoProps, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateDocStatus", "java.util.Map");// updateDocStatusForOrg
				DAOUtil.masterDAO(aoMyBatisSession, aoProps, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateBusinessAppModifiedDate", "java.util.Map");
			}
			else
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoProps, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertDocStatus", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoProps, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateBusinessAppModifiedDate", "java.util.Map");
			}
		}
		catch (Exception aoAppEx)
		{
			throw new ApplicationException("Error occured while inserting/updating doc status", aoAppEx);
		}
	}

	/**
	 * This method inserts and updates document status in database
	 * 
	 * @param aoProps - map containing required information
	 * @param aoMyBatisSession - mybatis session
	 * @throws ApplicationException
	 */
	public void insertUpdateDocStatusForOrg(Map<String, Object> aoProps, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		try
		{
			int liCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoProps,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getDocStatusCountForOrg", "java.util.Map");
			if (liCount > 0)
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoProps, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateDocStatusForOrg", "java.util.Map");
			}
			else
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoProps, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertDocStatus", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoProps, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateBusinessAppModifiedDate", "java.util.Map");
			}
		}
		catch (Exception aoAppEx)
		{
			throw new ApplicationException("Error occured while inserting/updating doc status", aoAppEx);
		}
	}

	/**
	 * This method inserts and updates document status in database
	 * 
	 * @param aoProps - map containing required information
	 * @param aoMyBatisSession - mybatis session
	 * @throws ApplicationException
	 */
	public void insertUpdateDocStatusService(Map<String, Object> aoProps, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		try
		{
			int liCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoProps,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getDocStatusCountService", "java.util.Map");

			if (liCount > 0)
			{

				DAOUtil.masterDAO(aoMyBatisSession, aoProps, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateDocStatusService", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoProps, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateBusiServiceAppIdModifiedDate", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoProps, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateServiceAppIdModifiedDate", "java.util.Map");
			}
			else
			{

				DAOUtil.masterDAO(aoMyBatisSession, aoProps, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"insertDocStatusService", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoProps, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateBusiServiceAppIdModifiedDate", "java.util.Map");
				DAOUtil.masterDAO(aoMyBatisSession, aoProps, ApplicationConstants.MAPPER_CLASS_APPLICATION,
						"updateServiceAppIdModifiedDate", "java.util.Map");

			}
		}
		catch (Exception aoAppEx)
		{
			throw new ApplicationException("Error occured while inserting/updating doc status", aoAppEx);
		}
	}

	/**
	 * this method fetches language and geography information for print
	 * 
	 * @param asOrgId - current organization id
	 * @param lbIsLang - flag for language
	 * @param aoMyBatisSession - mybatis session
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> getLangGeoForPrint(String asOrgId, boolean abIsLang, SqlSession aoMyBatisSession)
			throws ApplicationException
	{
		List<Map<String, String>> loList = null;
		Map<String, String> loMap = new HashMap<String, String>();
		loMap.put(ApplicationConstants.ORG_ID, asOrgId);
		if (abIsLang)
		{
			loMap.put("asType", ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_LANGUAGES);
			loList = (List<Map<String, String>>) DAOUtil.masterDAO(aoMyBatisSession, loMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getLangForPrint", "java.util.Map");
		}
		else
		{
			loMap.put("asType", ApplicationConstants.BUSINESS_APPLICATION_SUB_SECTION_GEOGRAPHY);
			loList = (List<Map<String, String>>) DAOUtil.masterDAO(aoMyBatisSession, loMap,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "getGeoForPrint", "java.util.Map");
		}
		return loList;
	}

}
