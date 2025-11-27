package com.nyc.hhs.daomanager.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.ContactUsBean;
import com.nyc.hhs.model.OrgNameChangeBean;
import com.nyc.hhs.model.ProviderStatusBean;
import com.nyc.hhs.model.SectionBean;
import com.nyc.hhs.model.SubSectionBean;
import com.nyc.hhs.model.WithdrawalBean;
import com.nyc.hhs.model.WorkflowIDServiceBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.DateUtil;

/**
 * SectionService: This Class updates, inserts , delete different status on
 * different tables from tasks for city users.
 * 
 */

public class SectionService extends ServiceState
{

	private static final LogInfo LOG_OBJECT = new LogInfo(SectionService.class);

	/**
	 * This method fetch all section related Data from SECTION table
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatisSession SQl Session
	 * @return loSectionList List of SectionBean
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<SectionBean> fetchSectionDetails(HashMap aoHMSection, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		List<SectionBean> loSectionList = null;

		loSectionList = (List<SectionBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "fetchSection", "java.util.HashMap");

		return loSectionList;
	}

	/**
	 * This method fetch Process Status of BR Application
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatisSession SQl Session
	 * @return loSectionList List of SectionBean
	 * @throws ApplicationException
	 */
	public List<SectionBean> fetchBRProcAppStatus(HashMap aoHMSection, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		List<SectionBean> loSectionList = null;

		loSectionList = (List<SectionBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "fetchBRProcAppStatus", "java.util.HashMap");

		return loSectionList;
	}

	/**
	 * This method fetch Process Status of SR Application
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatisSession SQl Session
	 * @return loSectionList List of SectionBean
	 * @throws ApplicationException
	 */
	public String fetchServiceAppStatus(HashMap aoHMSection, SqlSession aoMybatisSession) throws ApplicationException
	{
		return (String) DAOUtil.masterDAO(aoMybatisSession, aoHMSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "fetchServiceAppStatus", "java.util.HashMap");
	}

	/**
	 * This method fetch all sub section related Data from SUB_SECTION_SUMMARY
	 * table
	 * 
	 * @param aoHMSubSection HashMap of Required props
	 * @param aoMybatisSession SQl Session
	 * @return loSubSectionList List of SubSectionBean
	 * @throws ApplicationException
	 */
	public List<SubSectionBean> fetchSubSectionDetails(HashMap aoHMSubSection, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		List<SubSectionBean> loSubSectionList = null;
		loSubSectionList = (List<SubSectionBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMSubSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "fetchSubSection", "java.util.HashMap");

		return loSubSectionList;
	}

	/**
	 * This method fetch all sub section related Data from SUB_SECTION_SUMMARY
	 * table
	 * 
	 * @param aoHMSubSection HashMap of Required props
	 * @param aoMybatisSession SQl Session
	 * @return loContactUsList List of ContactUsBean
	 * @throws ApplicationException
	 */
	public List<ContactUsBean> fetchContactUsDetails(HashMap aoHMSubSection, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		List<ContactUsBean> loContactUsList = null;

		loContactUsList = (List<ContactUsBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMSubSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "fetchContactUsDetails", "java.util.HashMap");

		return loContactUsList;
	}

	/**
	 * This method update section status in SECTION table
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus update success status
	 * @throws ApplicationException
	 */
	public boolean updateSectionStatus(HashMap aoHMSection, SqlSession aoMybatiSession) throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateSectionStatus", "java.util.HashMap");
		lbUpdateStatus = true;

		return lbUpdateStatus;
	}

	/**
	 * This method update section status in FILEUPLOAD table
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus update success status
	 * @throws ApplicationException
	 */
	public boolean updateDocStatus(HashMap aoHMSection, SqlSession aoMybatiSession) throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateDocStatus", "java.util.HashMap");
		lbUpdateStatus = true;

		return lbUpdateStatus;
	}

	/**
	 * This method update process status in SECTION table
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus update success status
	 * @throws ApplicationException
	 */
	public boolean updateProcessSectionStatus(HashMap aoHMSection, SqlSession aoMybatiSession)
			throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateProcessSectionStatus", "java.util.HashMap");
		lbUpdateStatus = true;

		return lbUpdateStatus;
	}

	/**
	 * This method update Application status in BUSINESS_APPLICATION table
	 * 
	 * @param aoHMBRApplication HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return boolean status
	 * @throws ApplicationException
	 */
	public boolean updateBRApplicationStatus(HashMap aoHMBRApplication, SqlSession aoMybatiSession)
			throws ApplicationException
	{

		DAOUtil.masterDAO(aoMybatiSession, aoHMBRApplication, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateBRApplicationStatus", "java.util.HashMap");
		if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase((String) aoHMBRApplication.get("applicationStatus")))
		{
			Map<String, String> loApplicationSettingMap = (Map<String, String>) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsExpirationYear = (String) loApplicationSettingMap.get("ProviderExpiration_BR001");
			int liExpirationYear = Integer.valueOf(lsExpirationYear);
			aoHMBRApplication.put("expirationDate",
					DateUtil.addYears((Date) aoHMBRApplication.get("modifiedDate"), liExpirationYear));
			aoHMBRApplication.put("startDate", new Date());
			setBusinessExpirationDate(aoMybatiSession, aoHMBRApplication);
			// start R5 Proposal Activity History and Char 500 history
			DAOUtil.masterDAO(aoMybatiSession, aoHMBRApplication, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"lastApprovedBusinessApprovedCorporateStructure", "java.util.HashMap");
			// end R5 Proposal Activity History and Char 500 history
		}
		return true;
	}

	/**
	 * This method update Application process status in BUSINESS_APPLICATION
	 * table
	 * 
	 * @param asStatus BR Status
	 * @param aoHMBRApplication HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateBRProcAppStatus(String asStatus, HashMap aoHMBRApplication, SqlSession aoMybatiSession)
			throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		aoHMBRApplication.put("procApplStatus", asStatus);
		DAOUtil.masterDAO(aoMybatiSession, aoHMBRApplication, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateBRProcAppStatus", "java.util.HashMap");
		lbUpdateStatus = true;

		return lbUpdateStatus;
	}

	/**
	 * This method update Document process status in DOCUMENT table
	 * 
	 * @param aoHMDocument HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateProcDocumentStatus(HashMap aoHMDocument, SqlSession aoMybatiSession)
			throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMDocument, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateProcDocumentStatus", "java.util.HashMap");
		lbUpdateStatus = true;

		return lbUpdateStatus;
	}

	/**
	 * This method update Process Document status in DOCUMENT table
	 * 
	 * @param aoHMDocument HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateDocumentStatus(HashMap aoHMDocument, SqlSession aoMybatiSession) throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMDocument, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateDocumentStatus", "java.util.HashMap");
		lbUpdateStatus = true;

		return lbUpdateStatus;
	}

	/**
	 * This method update Document status with Process Document Status in
	 * DOCUMENT table
	 * 
	 * @param aoHMDocument HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateDocWithProcStatus(HashMap aoHMDocument, SqlSession aoMybatiSession)
			throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMDocument, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateDocWithProcStatus", "java.util.HashMap");
		lbUpdateStatus = true;

		return lbUpdateStatus;
	}

	/**
	 * This method update sub section status in SUB_SECTION_SUMMARY table
	 * 
	 * @param aoHMSubSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateSubSectionStatus(HashMap aoHMSubSection, SqlSession aoMybatiSession)
			throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMSubSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateSubSectionStatus", "java.util.HashMap");
		lbUpdateStatus = true;

		return lbUpdateStatus;
	}

	/**
	 * This method update process sub section status in SUB_SECTION_SUMMARY
	 * table
	 * 
	 * @param aoHMSubSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateProcSubSectionStatus(HashMap aoHMSubSection, SqlSession aoMybatiSession)
			throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMSubSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateProcSubSectionStatus", "java.util.HashMap");
		lbUpdateStatus = true;

		return lbUpdateStatus;
	}

	/**
	 * This Method updates the PROCESS_STATUS in SERVICE_APPLICATION table
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateProcessServiceSectionStatus(HashMap aoHMSection, SqlSession aoMybatiSession)
			throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateProcessServiceSectionStatus", "java.util.HashMap");
		lbUpdateStatus = true;

		return lbUpdateStatus;
	}

	/**
	 * This Method updates the SERVICE_STATUS in SERVICE_APPLICATION table
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateServiceSectionStatus(HashMap aoHMSection, SqlSession aoMybatiSession)
			throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateServiceSectionStatus", "java.util.HashMap");

		if (ApplicationConstants.STATUS_APPROVED.equalsIgnoreCase((String) aoHMSection.get("serviceStatus")))
		{
			Map<String, String> loApplicationSettingMap = (Map<String, String>) BaseCacheManagerWeb.getInstance()
					.getCacheObject(ApplicationConstants.APPLICATION_SETTING);
			String lsExpirationYear = (String) loApplicationSettingMap.get("ProviderExpiration_BR001");
			int liExpirationYear = Integer.valueOf(lsExpirationYear);
			aoHMSection.put("expirationDate", DateUtil.addYears(new Date(), liExpirationYear));
			aoHMSection.put("startDate", new Date());
			setServiceDates(aoMybatiSession, aoHMSection);
		}
		lbUpdateStatus = true;

		return lbUpdateStatus;
	}

	/**
	 * This Method updates the WORKFLOW_ID in SERVICE_APPLICATION table
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateServicetask(HashMap aoHMSection, SqlSession aoMybatiSession) throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateSectionStatusDB", "java.util.HashMap");
		lbUpdateStatus = true;

		return lbUpdateStatus;
	}

	/**
	 * This method fetches ORGANIZATION_EXPIRATION_DATE from ORGANIZATION table.
	 * 
	 * @param asOrgId Provider ID
	 * @param aoMybatisSession SQl Session
	 * @return loOrgExpirationDate Provider Expiration Date
	 * @throws ApplicationException
	 */
	public Date fetchOrgExpirationDate(String asOrgId, SqlSession aoMybatisSession) throws ApplicationException
	{

		Date loOrgExpirationDate = null;
		loOrgExpirationDate = (Date) DAOUtil.masterDAO(aoMybatisSession, asOrgId,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "fetchOrgExpirationDate", "java.lang.String");

		return loOrgExpirationDate;
	}

	/**
	 * This method fetches DUE_DATE from DOC_LAPSING_RULES_MASTER table.
	 * 
	 * @param asProviderId Provider Id
	 * @param aoMybatisSession SQl Session
	 * @return loProviderDueDate Provider Due Date
	 * @throws ApplicationException
	 */
	public Date fetchproviderDueDate(String asProviderId, SqlSession aoMybatisSession) throws ApplicationException
	{

		Date loProviderDueDate = null;
		loProviderDueDate = (Date) DAOUtil.masterDAO(aoMybatisSession, asProviderId,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "fetchproviderDueDate", "java.lang.String");

		return loProviderDueDate;
	}

	/**
	 * This method updates the STATUS in the CONTACT_US_DETAILS table.
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateContactStatus(HashMap aoHMSection, SqlSession aoMybatiSession) throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateContactStatus", "java.util.HashMap");
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This method will update the doc status for filings.
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateFilingDocStatus(HashMap aoHMSection, SqlSession aoMybatiSession) throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateFilingDocStatus", "java.util.HashMap");
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This method is used to update the DOC_LAPSING_RULES_MASTER table
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return boolean Status
	 * @throws ApplicationException
	 */
	public boolean updateDocLapsingMasterForFiling(HashMap aoHMSection, SqlSession aoMybatiSession)
			throws ApplicationException
	{

		if ((Boolean) aoHMSection.get(P8Constants.PROPERTY_PE_IS_SHORT_FILING))
		{
			DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"updateDocLapsingMasterOrder", "java.util.HashMap");
			DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"insertDocLapsingMaster", "java.util.HashMap");
		}
		else
		{
			DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"updateDocLapsingMasterForFiling", "java.util.HashMap");
		}
		return true;
	}

	/**
	 * This method is used to fetch the required details from SERVICE_WITHDRAWAL
	 * and SERVICE_APPLICATION tables
	 * 
	 * @param aoHMSubSection HashMap of Required props
	 * @param aoMybatisSession SQl Session
	 * @return loServiceWithdrawalBean WithdrawalBean
	 * @throws ApplicationException
	 */
	public List<WithdrawalBean> fetchServiceWithdrawalDetails(HashMap aoHMSubSection, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		List<WithdrawalBean> loServiceWithdrawalBean = null;

		loServiceWithdrawalBean = (List<WithdrawalBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMSubSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "fetchServiceWithdrawalDetails", "java.util.HashMap");

		return loServiceWithdrawalBean;
	}

	/**
	 * This method fetch BR Application Withdrawl details
	 * 
	 * @param aoHMSubSection HashMap of Required props
	 * @param aoMybatisSession SQl Session
	 * @return loServiceWithdrawalBean WithdrawalBean
	 * @throws ApplicationException
	 */
	public List<WithdrawalBean> fetchBRWithdrawalDetails(HashMap aoHMSubSection, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		List<WithdrawalBean> loServiceWithdrawalBean = null;

		loServiceWithdrawalBean = (List<WithdrawalBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMSubSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "fetchBRWithdrawalDetails", "java.util.HashMap");

		return loServiceWithdrawalBean;
	}

	/**
	 * This methods updates the SERVICE_STATUS in SERVICE_APPLICATION table
	 * 
	 * @param aoProps HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @param aoWithdrawalBean WithdrawalBean
	 * @throws ApplicationException
	 */
	public void updateSectionWithdrawalStatusDB(HashMap aoProps, SqlSession aoMybatiSession,
			WithdrawalBean aoWithdrawalBean) throws ApplicationException
	{

		HashMap loReqdProp = new HashMap();
		HashMap loHMSection = new HashMap();
		String lsStatus = "";
		loReqdProp.put("status", aoWithdrawalBean.getMsStatus());
		loReqdProp.put("entityId", aoWithdrawalBean.getMsServiceApplicationId());
		loReqdProp.put("requestId", aoWithdrawalBean.getMsWithDrawalReqId());
		loReqdProp.put("entitytype", aoProps.get("entitytype"));
		loReqdProp.put("flag", aoProps.get("flag"));
		loReqdProp.put("event", aoProps.get("event"));
		loReqdProp.put("timestamp", aoProps.get("timestamp"));
		loReqdProp.put("userid", aoProps.get("userid"));
		loReqdProp.put("orgid", aoProps.get("orgid"));

		if (aoWithdrawalBean.getMsStatus().equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN))
		{
			loReqdProp.put("createdBy", aoWithdrawalBean.getMsApprovedBy());
			loReqdProp.put("createdDate", aoWithdrawalBean.getMoApprovedDate());
			loHMSection.put("applicationId", aoWithdrawalBean.getMsBusinessApplicationId());
			loHMSection.put("sectionId", aoWithdrawalBean.getMsServiceApplicationId());
			loHMSection.put("brApplicationId", aoWithdrawalBean.getMsBusinessApplicationId());
			loHMSection.put("serviceStatus", aoWithdrawalBean.getMsStatus());
			loHMSection.put("modifiedBy", aoProps.get("userid"));
			loHMSection.put("modifiedDate", new Date());
			// Delete entry from SUPERSEDING TABLE
			deleteEntryFromSuperseding(aoMybatiSession, loReqdProp);

			lsStatus = fetchServiceAppStatus(loHMSection, aoMybatiSession);
			if (null != lsStatus
					&& (lsStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW)
							|| lsStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED)
							|| lsStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS)
							|| lsStatus.equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN) || lsStatus
								.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED)))
			{
				DAOUtil.masterDAO(aoMybatiSession, loHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
						"updateServiceSectionStatus", "java.util.HashMap");
			}
			else
			{
				insertSuperSeding(aoMybatiSession, loReqdProp);
			}
			loHMSection.put("expirationDate", null);
			loHMSection.put("applicationId", aoWithdrawalBean.getMsBusinessApplicationId());
			loHMSection.put("sectionId", aoWithdrawalBean.getMsServiceApplicationId());
			setServiceDates(aoMybatiSession, loHMSection);
		}

	}

	/**
	 * This methods updates the WITHDRAWAN_STATUS in SERVICE_WITHDRAWAL table
	 * 
	 * @param aoMybatiSession SQl Session
	 * @param aoWithdrawalBean WithdrawalBean
	 * @throws ApplicationException
	 */
	public void updateSectionWithdrawalRequestStatusDB(SqlSession aoMybatiSession, WithdrawalBean aoWithdrawalBean)
			throws ApplicationException
	{
		if (aoWithdrawalBean.isMbToBeTerminate())
		{

			HashMap loReqdProp = new HashMap();
			loReqdProp.put("withdrawStatus", aoWithdrawalBean.getMsWithdrawStatus());
			loReqdProp.put("sectionId", aoWithdrawalBean.getMsWithDrawalReqId());
			loReqdProp.put("approvedBy", aoWithdrawalBean.getMsApprovedBy());
			loReqdProp.put("approvedDate", aoWithdrawalBean.getMoApprovedDate());
			DAOUtil.masterDAO(aoMybatiSession, loReqdProp, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"updateSectionWithdrawalRequestStatusDB", "java.util.HashMap");
		}
	}

	/**
	 * This methods updates the APPLICATION_STATUS in BUSINESS_APPLICATION table
	 * 
	 * @param aoProps HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @param aoWithdrawalBean WithdrawalBean
	 * @throws ApplicationException
	 */
	public void updateBusinessWithdrawalStatusDB(HashMap aoProps, SqlSession aoMybatiSession,
			WithdrawalBean aoWithdrawalBean) throws ApplicationException
	{

		HashMap loReqdProp = new HashMap();
		String lsStatus = "";
		HashMap loHMSection = new HashMap();
		loReqdProp.put("status", aoWithdrawalBean.getMsStatus());
		loReqdProp.put("entityId", aoWithdrawalBean.getMsBusinessApplicationId());
		loReqdProp.put("requestId", aoWithdrawalBean.getMsWithDrawalReqId());
		loReqdProp.put("entitytype", aoProps.get("entitytype"));
		loReqdProp.put("flag", aoProps.get("flag"));
		loReqdProp.put("event", aoProps.get("event"));
		loReqdProp.put("timestamp", aoProps.get("timestamp"));
		loReqdProp.put("userid", aoProps.get("userid"));
		loReqdProp.put("orgid", aoProps.get("orgid"));
		if (aoWithdrawalBean.getMsStatus().equalsIgnoreCase(ApplicationConstants.STATUS_WITHDRAWN))
		{
			loReqdProp.put("createdBy", aoWithdrawalBean.getMsApprovedBy());
			loReqdProp.put("createdDate", aoWithdrawalBean.getMoApprovedDate());
			loHMSection.put("applicationId", aoWithdrawalBean.getMsBusinessApplicationId());
			loHMSection.put("brApplicationId", aoWithdrawalBean.getMsBusinessApplicationId());
			loHMSection.put("applicationStatus", aoWithdrawalBean.getMsStatus());
			loHMSection.put("modifiedBy", aoProps.get("userid"));
			loHMSection.put("modifiedDate", new Date());
			// Delete entry from Superseding Table
			deleteEntryFromSuperseding(aoMybatiSession, loReqdProp);

			List<SectionBean> loSectionList = fetchBRProcAppStatus(loHMSection, aoMybatiSession);
			Iterator<SectionBean> loListItr = loSectionList.iterator();
			if (loListItr.hasNext())
			{
				SectionBean loBean = loListItr.next();
				lsStatus = loBean.getApplicationStatus();
			}
			if (null != lsStatus
					&& (lsStatus.equalsIgnoreCase(ApplicationConstants.STATUS_IN_REVIEW)
							|| lsStatus.equalsIgnoreCase(ApplicationConstants.STATUS_DEFFERED)
							|| lsStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS) || lsStatus
								.equalsIgnoreCase(ApplicationConstants.STATUS_REJECTED)))
			{
				DAOUtil.masterDAO(aoMybatiSession, loHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
						"updateBRApplicationStatus", "java.util.HashMap");
			}
			else
			{
				insertSuperSeding(aoMybatiSession, loReqdProp);
			}

			// Set Expiration Date to Null
			loReqdProp.put("expirationDate", null);
			loReqdProp.put("brApplicationId", aoWithdrawalBean.getMsBusinessApplicationId());
			loReqdProp.put("modifiedBy", aoProps.get("userid"));
			setBusinessExpirationDate(aoMybatiSession, loReqdProp);
		}

	}

	/**
	 * This methods updates the WITHDRAWAN_STATUS in BUSINESS_WITHDRAWAL table
	 * 
	 * @param aoMybatiSession SQl Session
	 * @param aoWithdrawalBean WithdrawalBean
	 * @throws ApplicationException
	 */
	public void updateBusinessWithdrawalRequestStatusDB(SqlSession aoMybatiSession, WithdrawalBean aoWithdrawalBean)
			throws ApplicationException
	{

		HashMap loReqdProp = new HashMap();
		loReqdProp.put("withdrawStatus", aoWithdrawalBean.getMsWithdrawStatus());
		loReqdProp.put("sectionId", aoWithdrawalBean.getMsWithDrawalReqId());
		loReqdProp.put("approvedBy", aoWithdrawalBean.getMsApprovedBy());
		loReqdProp.put("approvedDate", aoWithdrawalBean.getMoApprovedDate());
		DAOUtil.masterDAO(aoMybatiSession, loReqdProp, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateBusinessWithdrawalRequestStatusDB", "java.util.HashMap");
	}

	/**
	 * This method update the NOTIFICATION table with notification.
	 * 
	 * @param aoRequiredProps HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @throws ApplicationException
	 */
	public void insertAlertAndNotification(HashMap aoRequiredProps, SqlSession aoMybatiSession)
			throws ApplicationException
	{

		DAOUtil.masterDAO(aoMybatiSession, aoRequiredProps, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"insertAlertAndNotification", "java.util.HashMap");
	}

	/**
	 * This method modified as a part of release 3.4.0 defect 6478
	 * 
	 * <ul>
	 * <li>
	 * USER_DN is set to null on Provider Account Request Rejection by
	 * Accelerator Team</li>
	 * </ul>
	 * 
	 * This Method updates the PROC_STATUS in ORGANIZATION table.
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateOrgRequestStatus(HashMap aoHMSection, SqlSession aoMybatiSession) throws ApplicationException
	{

		boolean lbUpdateStatus = false;
		String lsWFStatus = (String) aoHMSection.get("status");

		if (lsWFStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED))
		{
			DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"updateOrgRequestStatus", "java.util.HashMap");

			aoHMSection.put("status", "Yes");
			aoHMSection.put("memberStatus", "Active");
			DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"updateUserStatus_DB", "java.util.HashMap");
			DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"updateUserStatusActiveFlag", "java.util.HashMap");
			DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"updateOrganizationActiveFlag", "java.util.HashMap");

		}
		else
		{

			lsWFStatus = (String) aoHMSection.get("status");
			aoHMSection.put("status", lsWFStatus);
			DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"updateOrgRequestStatus", "java.util.HashMap");

			// changes done as a part of release 3.4.0 defect 6478 -start
			aoHMSection.put("userDn", null);
			DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					ApplicationConstants.UPDATE_USER_DN_ON_ACCOUNT_REQUEST_REJECTION, "java.util.HashMap");
			// changes done as a part of release 3.4.0 defect 6478 -end

			aoHMSection.put("status", "No");
			aoHMSection.put("memberStatus", "In Active");
			DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"updateUserStatus_DB", "java.util.HashMap");

		}
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This Method Fetches required data from SERVICE_APPLICATION table.
	 * 
	 * @param aoHMSubSection HashMap of Required props
	 * @param aoMybatisSession SQl Session
	 * @return List<WorkflowIDServiceBean> List of WorkflowIDServiceBean
	 * @throws ApplicationException
	 */
	public List<WorkflowIDServiceBean> fetchServiceCapacityIds(HashMap aoHMSubSection, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		return (List<WorkflowIDServiceBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMSubSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "fetchServiceCapacityIds", "java.util.HashMap");
	}

	/**
	 * This method fetches services corresponding to a business application
	 * whoose status is other then 'Suspended' in superseding table
	 * 
	 * @param aoHMSubSection HashMap of Required props
	 * @param aoMybatisSession SQl Session
	 * @return List<WorkflowIDServiceBean> List of WorkflowIDServiceBean
	 * @throws ApplicationException
	 */
	public List<WorkflowIDServiceBean> fetchNonSuspendedServiceCapacityIds(HashMap aoHMSubSection,
			SqlSession aoMybatisSession) throws ApplicationException
	{

		return (List<WorkflowIDServiceBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMSubSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "fetchNonSuspendedServiceCapacityIds",
				"java.util.HashMap");
	}

	/**
	 * This Method is used to delete the entry from superseding table.
	 * 
	 * @param aoMybatiSession SQl Session
	 * @param aoReqdProp HashMap of Required props
	 * @return boolean Status
	 * @throws ApplicationException
	 */
	public boolean deleteEntryFromSuperseding(SqlSession aoMybatiSession, HashMap aoReqdProp)
			throws ApplicationException
	{

		DAOUtil.masterDAO(aoMybatiSession, aoReqdProp, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"deleteEntryFromSuperseding", "java.util.HashMap");
		return true;
	}

	/**
	 * This method is used to update the values in superseding_status table
	 * 
	 * @param aoMybatiSession SQl Session
	 * @param aoReqdProp HashMap of Required props
	 * @throws ApplicationException
	 */
	public void updateSupersedingStatus(SqlSession aoMybatiSession, HashMap aoReqdProp) throws ApplicationException
	{

		DAOUtil.masterDAO(aoMybatiSession, aoReqdProp, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateSupersedingStatus", "java.util.HashMap");
	}

	/**
	 * This method will fetch the Subsection for legal name update.
	 * 
	 * @param asAppId Application Id
	 * @param asWobNo WorkFlow Id
	 * @param aoMybatisSession SQl Session
	 * @return List<OrgNameChangeBean> List of OrgNameChangeBean
	 * @throws ApplicationException
	 */
	public List<OrgNameChangeBean> getSubsectionForLegalNameUpdateReqTask(String asAppId, String asWobNo,
			SqlSession aoMybatisSession) throws ApplicationException
	{

		List<OrgNameChangeBean> loOrgNameChangeBean = null;
		HashMap loClause = new HashMap();
		loClause.put("AppID", asAppId);
		loClause.put("WobNo", asWobNo);
		loOrgNameChangeBean = (List<OrgNameChangeBean>) DAOUtil.masterDAO(aoMybatisSession, loClause,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "getSubsectionForLegalNameUpdateReqTask",
				"java.util.HashMap");

		return loOrgNameChangeBean;
	}

	/**
	 * This method updates PROC_STATUS in ORGANIZATION_NAME_CHANGE table.
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateLegalNameApprovedRequest(HashMap aoHMSection, SqlSession aoMybatiSession)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateLegalNameApprovedRequest", "java.util.HashMap");
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This method updates PROC_STATUS in ORGANIZATION_NAME_CHANGE table.
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateLegalNameRejectedRequest(HashMap aoHMSection, SqlSession aoMybatiSession)
			throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateLegalNameRejectedRequest", "java.util.HashMap");
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This method updates ORGANIZATION_LEGAL_NAME in ORGANIZATION table.
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return lbUpdateStatus Updated Status
	 * @throws ApplicationException
	 */
	public boolean updateLegalNameRequest(HashMap aoHMSection, SqlSession aoMybatiSession) throws ApplicationException
	{

		boolean lbUpdateStatus = false;

		DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateLegalNameRequest", "java.util.HashMap");
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This method is used to fetch the documentId from the document table.
	 * 
	 * @param aoHMSubSection HashMap of Required props
	 * @param aoMybatisSession SQl Session
	 * @return List<String> List Of char500 Documents
	 * @throws ApplicationException
	 */
	public List<String> getCHAR500docFromDocument(HashMap aoHMSubSection, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		return (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoHMSubSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "getCHAR500docFromDocument", "java.util.HashMap");
	}

	/**
	 * This method is used to insert values in DOC_LAPSING_RULES_MASTER table
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @throws ApplicationException
	 */
	public void insertDocLapsingMaster(HashMap aoHMSection, SqlSession aoMybatiSession) throws ApplicationException
	{

		int liUpdateCount = (Integer) DAOUtil.masterDAO(aoMybatiSession, aoHMSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "updateDocLapsingMasterForFiling",
				"java.util.HashMap");
		if (liUpdateCount <= 0)
		{
			DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"insertDocLapsingMaster", "java.util.HashMap");
		}

	}

	/**
	 * This method is used to get count of approved application of particular
	 * organization.
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @throws ApplicationException
	 */
	public int approvedApplicationCount(HashMap aoHMSection, SqlSession aoMybatiSession) throws ApplicationException
	{
		int liCount = (Integer) DAOUtil.masterDAO(aoMybatiSession, aoHMSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "approvedApplicationCount", "java.util.HashMap");
		return liCount;
	}

	/**
	 * This method is used to insert values in DOC_LAPSING_RULES_TRANS table
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @throws ApplicationException
	 */
	public void insertDocLapsingTrans(HashMap aoHMSection, SqlSession aoMybatiSession) throws ApplicationException
	{

		DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"insertDocLapsingTrans", "java.util.HashMap");

	}

	/**
	 * This method get all Service and Application status for provider
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatisSession SQl Session
	 * @return loProviderStatusBeanList List of ProviderStatusBean
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<ProviderStatusBean> getBusinessAndServiceStatus(HashMap aoHMSection, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		List<ProviderStatusBean> loProviderStatusBeanList = null;

		loProviderStatusBeanList = (List<ProviderStatusBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "getBusinessAndServiceStatus", "java.util.HashMap");

		return loProviderStatusBeanList;
	}

	/**
	 * This method get all Service and Application status for provider From
	 * Batch
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatisSession SQl Session
	 * @return loProviderStatusBeanList List of ProviderStatusBean
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<ProviderStatusBean> getBusinessAndServiceStatusBatch(HashMap aoHMSection, SqlSession aoMybatisSession)
			throws ApplicationException
	{

		List<ProviderStatusBean> loProviderStatusBeanList = null;

		loProviderStatusBeanList = (List<ProviderStatusBean>) DAOUtil.masterDAO(aoMybatisSession, aoHMSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "getBusinessAndServiceStatusBatch",
				"java.util.HashMap");

		return loProviderStatusBeanList;
	}

	/**
	 * This method update provider status
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @throws ApplicationException
	 */
	public void updateOrganizationStatus(HashMap aoHMSection, SqlSession aoMybatiSession) throws ApplicationException
	{
		DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"updateOrganizationStatus", "java.util.HashMap");

	}

	/**
	 * This method returned current provider status
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return String Current Provider Status
	 * @throws ApplicationException
	 */
	public String getCurrentProviderStatus(HashMap aoHMSection, SqlSession aoMybatiSession) throws ApplicationException
	{

		return (String) DAOUtil.masterDAO(aoMybatiSession, aoHMSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "getCurrentProviderStatus", "java.util.HashMap");

	}

	/**
	 * This method is used to insert in superseding_status table.
	 * 
	 * @param aoMybatiSession SQl Session
	 * @param aoReqdProp HashMap of Required props
	 * @throws ApplicationException
	 */
	public void insertSuperSeding(SqlSession aoMybatiSession, HashMap aoReqdProp) throws ApplicationException
	{

		DAOUtil.masterDAO(aoMybatiSession, aoReqdProp, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"insertSupersedingStatus", "java.util.HashMap");
	}

	/**
	 * This method is to Set Business Application Expiration Date.
	 * 
	 * @param aoMybatiSession SQl Session
	 * @param aoReqdProp HashMap of Required props
	 * @throws ApplicationException
	 */
	public void setBusinessExpirationDate(SqlSession aoMybatiSession, HashMap aoReqdProp) throws ApplicationException
	{

		DAOUtil.masterDAO(aoMybatiSession, aoReqdProp, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"setBusinessExpirationDate", "java.util.HashMap");
	}

	/**
	 * This MEthod sets the dates in service application table
	 * 
	 * @param aoMybatiSession SQL Session
	 * @param aoReqdProp HashMap of Requires Props
	 * @throws ApplicationException
	 */
	public void setServiceDates(SqlSession aoMybatiSession, HashMap aoReqdProp) throws ApplicationException
	{
		DAOUtil.masterDAO(aoMybatiSession, aoReqdProp, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
				"setServiceDates", "java.util.HashMap");
	}

	/**
	 * 
	 * @param aoHMSubSection HashMap of required props
	 * @param aoMybatisSession SQL Session
	 * @return String Element Id
	 * @throws ApplicationException
	 */
	public String fetchServiceElementID(HashMap aoHMSubSection, SqlSession aoMybatisSession)
			throws ApplicationException
	{
		return (String) DAOUtil.masterDAO(aoMybatisSession, aoHMSubSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "fetchServiceElementID", "java.util.HashMap");
	}

	/**
	 * This Method is used to insert the required values in the
	 * PRINT_VIEW_GENERATION Table
	 * 
	 * @param aoMybatiSession SQL Session
	 * @param aoReqdProp HashMap Of Required Props
	 * @throws ApplicationException
	 */
	public void insertInPrintView(HashMap aoReqdProp, SqlSession aoMybatiSession) throws ApplicationException
	{
		int liPass = (Integer) DAOUtil.masterDAO(aoMybatiSession, aoReqdProp,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "UpdateprintView", "java.util.HashMap");
		if (liPass == 0)
		{
			DAOUtil.masterDAO(aoMybatiSession, aoReqdProp, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"insertInPrintView", "java.util.HashMap");
		}
	}

	/**
	 * This method returne email address of the provider
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @return String EMail Address
	 * @throws ApplicationException
	 */
	public String GetEmail(HashMap aoHMSection, SqlSession aoMybatiSession) throws ApplicationException
	{
		return (String) DAOUtil.masterDAO(aoMybatiSession, aoHMSection,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "GetEmail", "java.util.HashMap");
	}

	/**
	 * Convert String to Title Case
	 * 
	 * @param asInput - String to be converted to title case
	 * @return String converted to title case
	 */
	public static String toTitleCase(String asInput)
	{
		StringBuilder loTitleCase = new StringBuilder();
		boolean lbNextTitleCase = true;
		for (char loChar : asInput.toCharArray())
		{
			if (Character.isSpaceChar(loChar))
			{
				lbNextTitleCase = true;
			}
			else if (lbNextTitleCase)
			{
				loChar = Character.toTitleCase(loChar);
				lbNextTitleCase = false;
			}
			loTitleCase.append(loChar);
		}
		return loTitleCase.toString();
	}

	/**
	 * This method will update the public comments
	 * updated by user in section Table.
	 * query: updateSectionComments
	 * @param aoHMSection
	 * @param aoMybatiSession
	 * @return
	 * @throws ApplicationException
	 */
	public boolean updateCommentsInTransactionTable(HashMap aoHMSection, SqlSession aoMybatiSession)
			throws ApplicationException
	{
		boolean lbUpdateStatus = false;
		String lsTaskType = (String) aoHMSection.get("taskType");
		if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BASIC)
				|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_FILINGS)
				|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_BOARD)
				|| lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SECTION_POLICIES))
		{
			DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"updateSectionComments", "java.util.HashMap");
		}
		else if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_SERVICE_APPLICATION))
		{
			DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"updateServiceComments", "java.util.HashMap");
		}
		else if (lsTaskType.equalsIgnoreCase(P8Constants.PROPERTY_PE_TASK_TYPE_BR_APPLICATION))
		{
			DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
					"updateBusinessComments", "java.util.HashMap");
		}
		lbUpdateStatus = true;
		return lbUpdateStatus;
	}

	/**
	 * This Method Fetches required data from SERVICE_APPLICATION table.
	 * 
	 * @param aoHMSubSection HashMap of Required props
	 * @param aoMybatisSession SQl Session
	 * @return List<WorkflowIDServiceBean> List of WorkflowIDServiceBean
	 * @throws ApplicationException
	 */
	public String fetchWithdrawalID(String asServiceAppID, SqlSession aoMybatisSession) throws ApplicationException
	{

		return (String) DAOUtil.masterDAO(aoMybatisSession, asServiceAppID,
				ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "fetchWithdrawalID", "java.lang.String");
	}

	/**
	 * This method was added for release 3.10.0 enhancement 6573 This method
	 * deletes filing entries if the corporate structure was changed from non
	 * profit to profit on starting new business application.
	 * 
	 * @param aoHMSection HashMap of Required props
	 * @param aoMybatiSession SQl Session
	 * @throws ApplicationException
	 */
	public Boolean deleteFilingEntriesForCorporateStuctureChange(HashMap aoHMSection, SqlSession aoMybatiSession)
			throws ApplicationException
	{
		try
		{
			String lsCorporateStructure = (String) DAOUtil.masterDAO(aoMybatiSession, aoHMSection,
					ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "fetchCorporateStructure", "java.util.HashMap");
			int licount = (Integer) DAOUtil.masterDAO(aoMybatiSession, aoHMSection,
					ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "checkDoclapsingEntry", "java.util.HashMap");
			if (lsCorporateStructure.equalsIgnoreCase("For Profit") && licount > 0)
			{
				List<String> loBusinessAppIds = (List<String>) DAOUtil
						.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
								"fetchBusinessAppIds_DB", "java.util.HashMap");
				aoHMSection.put("appIdList", loBusinessAppIds);
				if (loBusinessAppIds.size() >= 1)
				{
					DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
							"deleteFromDocLapsingMaster", "java.util.HashMap");
					DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
							"deleteFromDocLapsingTrans", "java.util.HashMap");
					DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
							"deleteFromSupersedingStatus", "java.util.HashMap");
					DAOUtil.masterDAO(aoMybatiSession, aoHMSection, ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER,
							"deleteFromDueDateReminder", "java.util.HashMap");
				}
			}
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error(
					"Exception occured while deleting filing entries in SectionService: fetchBusinessAppIds_DB ",
					loAppEx);
			throw loAppEx;
		}
		return true;
	}

	/**
	 * This method was added for release 3.10.0 enhancement 6572 This method
	 * deletes checks whether there are any filings entry in sub section summary
	 * which are not in 'Not Started' status
	 * 
	 * @param asAppId Business Application id
	 * @param aoMybatiSession SQl Session
	 * @throws ApplicationException
	 */
	public HashMap<String, Object> getSubSectionStatusForFilings(String asOrgId, String asAppId,
			SqlSession aoMybatiSession) throws ApplicationException
	{
		Boolean loFilingSubSectionFlag = false;
		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		try
		{
			int lsFilingSubSectionCount = (Integer) DAOUtil.masterDAO(aoMybatiSession, asAppId,
					ApplicationConstants.MAPPER_CLASS_SECTION_MAPPER, "getSubSectionStatusForFilings",
					HHSConstants.JAVA_LANG_STRING);
			if (lsFilingSubSectionCount > 0)
			{
				loFilingSubSectionFlag = true;
			}
			String lsCorporateStructure = (String) DAOUtil.masterDAO(aoMybatiSession, asOrgId,
					ApplicationConstants.MAPPER_CLASS_APPLICATION, "fetchCSfromDb", "java.lang.String");
			loParamMap.put("filingSubSectionFlag", loFilingSubSectionFlag);
			loParamMap.put("corporateStructure", lsCorporateStructure);
		}
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Exception occured while fetching count in SectionService: getSubSectionStatusForFilings",
					loAppEx);
			throw loAppEx;
		}
		return loParamMap;
	}

}
