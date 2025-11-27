package com.nyc.hhs.service.db.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.service.db.services.application.FileUploadMapper;

/**
 * This class provides the functionality of executing various DAO methods to
 * perform database transactions so as to update and select details on the basis
 * of input parameters.
 * 
 */

public class FileUploadDAO
{
	/**
	 * this method updates the details in the database on file upload
	 * 
	 * @param aoFileUpload - file to be uploaded
	 * @param aoSession - sql session
	 * @param abInsertStatus - insert status
	 * @return - abInsertStatus
	 * @throws ApplicationException
	 */
	public boolean updateDetails(Document aoFileUpload, SqlSession aoSession, boolean abInsertStatus)
			throws ApplicationException
	{
		FileUploadMapper loMapper = aoSession.getMapper(FileUploadMapper.class);
		loMapper.updateDocumentDetails(aoFileUpload);
		abInsertStatus = true;
		return abInsertStatus;
	}

	/**
	 * this method updates the details in the database on file upload
	 * 
	 * @param aoFileUpload - file to be uploaded
	 * @param aoSession - sql session
	 * @param abInsertStatus - insert status
	 * @return - abInsertStatus
	 * @throws ApplicationException
	 */
	public boolean updateDetails1(Document aoFileUpload, SqlSession aoSession, boolean abInsertStatus)
			throws ApplicationException
	{
		FileUploadMapper loMapper = aoSession.getMapper(FileUploadMapper.class);
		if (!aoFileUpload.getSectionId().equalsIgnoreCase("servicessummary")
				&& (aoFileUpload.getServiceAppID() == null || aoFileUpload.getServiceAppID().equalsIgnoreCase("null")))
		{
			loMapper.updateDocumentDetails1(aoFileUpload);
		}
		else
		{
			loMapper.updateDocumentDetails1ServiceSummary(aoFileUpload);
		}
		abInsertStatus = true;
		return abInsertStatus;
	}

	/**
	 * This method fetch application details from database
	 * 
	 * @param asApplicationId - application id
	 * @param asFormInfo - map containing form information
	 * @param asOrgId - current organization id
	 * @param aoMybatisSession - Sql Session
	 * @return
	 * @throws ApplicationException
	 */
	public List<Document> selectDetails(String asApplicationId, HashMap asFormInfo, String asOrgId,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		FileUploadMapper loMapper = aoMybatisSession.getMapper(FileUploadMapper.class);
		List<Document> loList = null;
		HashMap loHashMap = new HashMap();
		String lsFormName = null;
		String lsFormVersion = null;
		String lsSection = null;
		try
		{
			loHashMap.put("asApplicationId", asApplicationId);
			loHashMap.put("asOrgId", asOrgId);
			lsFormName = (String) asFormInfo.get("FORM_NAME");
			lsFormVersion = (String) asFormInfo.get("FORM_VERSION");
			lsSection = (String) asFormInfo.get("SECTION_ID");
			loHashMap.put("asSection", lsSection);
			loHashMap.put("asFormName", lsFormName);
			loHashMap.put("asFormVersion", lsFormVersion);
			loList = loMapper.selectDocumentDetails(loHashMap);
		}
		catch (Exception loEx)
		{
			throw new ApplicationException("Error occurred while selecting details for Application Id:"
					+ asApplicationId, loEx);
		}
		return loList;
	}

	/**
	 * This method fetch form details from database
	 * 
	 * @param asApplicationId - application id
	 * @param asOrgId - current organization id
	 * @param asTableName - name of the table
	 * @param aoMybatisSession - Sql Session
	 * @return
	 * @throws ApplicationException
	 */
	public List<HashMap<String, Object>> selectFormDetails(String asApplicationId, String asOrgId, String asTableName,
			SqlSession aoMybatisSession) throws ApplicationException
	{
		FileUploadMapper loMapper = aoMybatisSession.getMapper(FileUploadMapper.class);
		List<HashMap<String, Object>> loFormInfoListMap = null;
		HashMap loHashMap = new HashMap();
		try
		{
			loHashMap.put("TABLE", asTableName);
			loHashMap.put("asApplicationId", asApplicationId);
			loHashMap.put("asOrgId", asOrgId);
			StringBuffer loSBClause = new StringBuffer();
			if (!asTableName.equalsIgnoreCase("ORGANIZATION"))
			{
				loSBClause.append(" BUSINESS_APPLICATION_ID= '");
				loSBClause.append(asApplicationId);
				loSBClause.append("' AND ORGANIZATION_ID='");
				loSBClause.append(asOrgId);
				loSBClause.append("'");
			}
			else
			{
				loSBClause.append(" ORGANIZATION_ID='");
				loSBClause.append(asOrgId);
				loSBClause.append("'");
			}
			loHashMap.put("asWhere", loSBClause.toString());
			loFormInfoListMap = loMapper.selectFormDetails(loHashMap);
		}
		catch (Exception aoTh)
		{
			throw new ApplicationException("Error occurred while selecting form details for Application Id:"
					+ asApplicationId, aoTh);
		}
		return loFormInfoListMap;
	}

	/**
	 * This method fetches service summary from the database.
	 * 
	 * @param asServiceApplicationId - service application id
	 * @param asOrgId - current organization id
	 * @param asapplicationid - application id
	 * @param aoMybatisSession - Sql Session
	 * @return - loList
	 * @throws ApplicationException
	 */
	public List<Document> selectDetailsServiceSummary(String asServiceApplicationId, String asOrgId,
			String asapplicationid, SqlSession aoMybatisSession) throws ApplicationException
	{
		FileUploadMapper loMapper = aoMybatisSession.getMapper(FileUploadMapper.class);
		List<Document> loList = null;
		HashMap loHashMap = new HashMap();
		try
		{
			loHashMap.put("asServiceApplicationId", asServiceApplicationId);
			loHashMap.put("asOrgId", asOrgId);
			loHashMap.put("asapplicationid", asapplicationid);
			loList = loMapper.selectDocumentDetailsServiceSummary(loHashMap);
		}
		catch (Exception aoTh)
		{
			throw new ApplicationException("Error occurred while selecting service summary details for Application Id:"
					+ asServiceApplicationId, aoTh);
		}
		return loList;
	}
}
