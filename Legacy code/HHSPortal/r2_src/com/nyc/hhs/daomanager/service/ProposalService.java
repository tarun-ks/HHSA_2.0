package com.nyc.hhs.daomanager.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.model.AutoCompleteBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.ProposalQuestionAnswerBean;
import com.nyc.hhs.model.ProviderSelectionBean;
import com.nyc.hhs.model.SiteDetailsBean;
import com.nyc.hhs.model.UserBean;
import com.nyc.hhs.rule.Rule;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;

/**
 * This service class will get the method calls from controller through
 * transaction layer. Execute queries by calling mapper and return query output
 * back to controller. If any error exists, wrap the exception into Application
 * Exception and throw it to controller.
 */
public class ProposalService extends ServiceState
{
	/**
	 * This is a log object used to log any exception into log file.
	 */
	private static final LogInfo LOG_OBJECT = new LogInfo(ProposalService.class);

	/**
	 * <p>
	 * This method fetches Proposal Site Details corresponding to a proposal Id
	 * and user type
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Fetch list of Proposal Site Details for the provided Proposal Id,
	 * user type using <b>fetchProposalSiteDetails</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProposalId - Proposal Id
	 * @param asUserType - User Type
	 * @return loSiteDetailList - list of site details
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<SiteDetailsBean> fetchProposalSiteDetails(SqlSession aoMybatisSession, String asProposalId,
			String asUserType) throws ApplicationException
	{
		List<SiteDetailsBean> loSiteDetailList = null;
		try
		{
			if (asProposalId != null)
			{
				Map<String, String> loMap = new HashMap<String, String>();
				loMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
				loMap.put(HHSConstants.AS_USER_TYPE, asUserType);
				loSiteDetailList = (List<SiteDetailsBean>) DAOUtil.masterDAO(aoMybatisSession, loMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROPOSAL_SITE_DETAILS,
						HHSConstants.JAVA_UTIL_MAP);
			}
			else
			{
				throw new ApplicationException("Proposal Id cannot be null while fetching the proposal site details");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching proposal site details for user Type:" + asUserType);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error while fetching proposal site details for user Type:" + asUserType);
			throw new ApplicationException("Error while fetching proposal site details for user Type:", loExp);
		}
		setMoState("Successfully fetched proposal site details for user Type:" + asUserType);
		return loSiteDetailList;
	}

	/**
	 * <p>
	 * This method inserts/updates the sub budget site details in database
	 * <ul>
	 * <li>Execute query <b>deleteSubBudgetSiteDetails </b></li>
	 * <li>Execute query <b>insertSubBudgetSiteDetails</b></li>
	 * <li>Execute query <b>updateSubBudgetSiteDetails</b> if condition is
	 * satisfied</li>
	 * <li>Release 3.6.0 Enhancement id 6484</li>
	 * @param aoMybatisSession - mybatis SQL session.
	 * @param aoSiteDetailsBean - Site details bean.
	 * @return flag depicting save was successful.
	 * @throws ApplicationException If an ApplicationException occurs.
	 */
	public boolean saveSubBudgetDetails(SqlSession aoMybatisSession, SiteDetailsBean aoSiteDetailsBean)
			throws ApplicationException
	{
		try
		{
			if (aoSiteDetailsBean != null)
			{
				if (aoSiteDetailsBean.getActionTaken() != null
						&& aoSiteDetailsBean.getActionTaken().equalsIgnoreCase(HHSConstants.DELETE))
				{
					DAOUtil.masterDAO(aoMybatisSession, aoSiteDetailsBean, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
							HHSConstants.DEL_SUB_BUDGET_SITE_DETAILS, HHSConstants.COM_NYC_HHS_MODEL_SITEDETAILSBEAN);
				}
				else if (aoSiteDetailsBean.getActionTaken() != null
						&& aoSiteDetailsBean.getActionTaken().equalsIgnoreCase(HHSConstants.INSERT))
				{
					HHSUtil.convertAddressValidationFields(aoSiteDetailsBean.getAddressRelatedData(), aoSiteDetailsBean);
					DAOUtil.masterDAO(aoMybatisSession, aoSiteDetailsBean, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
							HHSConstants.INS_SUB_BUDGET_SITE_DETAILS, HHSConstants.COM_NYC_HHS_MODEL_SITEDETAILSBEAN);
				}
				else if (aoSiteDetailsBean.getActionTaken() != null
						&& aoSiteDetailsBean.getActionTaken().equalsIgnoreCase(HHSConstants.UPDATE))
				{
					HHSUtil.convertAddressValidationFields(aoSiteDetailsBean.getAddressRelatedData(), aoSiteDetailsBean);
					DAOUtil.masterDAO(aoMybatisSession, aoSiteDetailsBean, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
							HHSConstants.UPDATE_SUB_BUDGET_SITE_DETAILS, HHSConstants.COM_NYC_HHS_MODEL_SITEDETAILSBEAN);
				}

			}
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while saving sub budget site details", aoAppEx);
			setMoState("Error while saving proposal details");
			throw aoAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while saving sub budget site details", loExp);
			setMoState("Error while saving sub budget site details");
			throw new ApplicationException("Error while saving sub budget site details", loExp);
		}
		setMoState("Successfully saved sub budget site details");
		return true;
	}

	/**
	 * Release 3.6.0 Enhancement id 6484. This method checks if the sub budget
	 * is configured for fiscal year on or after 2016.
	 * @param aoMybatisSession SQL session as input.
	 * @param aoCBGridBean Grid Bean as input.
	 * @param asUserType User type as input
	 * @return recordBeforeRelease True is record is before 2016 fical year.
	 * @throws ApplicationException If an ApplicationException occurs.
	 */
	@SuppressWarnings("unchecked")
	public Boolean recordBeforeRelease(SqlSession aoMybatisSession, CBGridBean aoCBGridBean, String asUserType)
			throws ApplicationException
	{
		Integer loRecordBeforeRelease = 0;
		try
		{
			if (aoCBGridBean != null && aoCBGridBean.getContractBudgetID() != null)
			{
				Map<String, String> loMap = new HashMap<String, String>();
				loMap.put(HHSConstants.SUBBUDGET_ID_KEY, aoCBGridBean.getSubBudgetID());
				loMap.put(HHSConstants.BUDGET_ID_KEY, aoCBGridBean.getContractBudgetID());
				HashMap<String, String> loApplicationSettingMap = (HashMap<String, String>) BaseCacheManagerWeb
						.getInstance().getCacheObject(ApplicationConstants.APPLICATION_SETTING);
				loMap.put(HHSConstants.FISCAL_YEAR_ID_ADD_SITE,
						loApplicationSettingMap.get(HHSConstants.FISCAL_YEAR_ID_ADD_SITE_KEY));
				loRecordBeforeRelease = (Integer) DAOUtil.masterDAO(aoMybatisSession, loMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.RECORD_BEFORE_RELEASE,
						HHSConstants.JAVA_UTIL_MAP);
				if (loRecordBeforeRelease == 0)
				{
					return Boolean.TRUE;
				}
				else
				{
					return Boolean.FALSE;
				}
			}
			else
			{
				throw new ApplicationException("Proposal Id cannot be null while fetching the proposal site details");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching sub budget site details for budget id:"
					+ aoCBGridBean.getContractBudgetID());
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error while fetching sub budget site details for budget id:"
					+ aoCBGridBean.getContractBudgetID());
			throw new ApplicationException("Error while fetching proposal site details for user Type:", loExp);
		}
	}

	/**
	 * This mthod gives the status of budget for that fiscal year. Release 3.6.0
	 * Enhancement id 6484
	 * @param aoMybatisSession SQL session as input.
	 * @param aoCBGridBean Grid Bean as input.
	 * @return Give the status of budget for record after fiscal year 2016.
	 * @throws ApplicationException If an ApplicationException occurs.
	 */
	public String fetchSubBudgetStatusId(SqlSession aoMybatisSession, CBGridBean aoCBGridBean,
			Boolean aoRecordBeforeRelease) throws ApplicationException
	{
		String lsSubBudgetStatusId = HHSConstants.EMPTY_STRING;
		try
		{
			if (!aoRecordBeforeRelease)
			{
				if (aoCBGridBean != null && aoCBGridBean.getContractBudgetID() != null)
				{
					Map<String, String> loMap = new HashMap<String, String>();
					loMap.put(HHSConstants.SUBBUDGET_ID_KEY, aoCBGridBean.getSubBudgetID());
					loMap.put(HHSConstants.BUDGET_ID_KEY, aoCBGridBean.getContractBudgetID());
					lsSubBudgetStatusId = (String) DAOUtil.masterDAO(aoMybatisSession, loMap,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_SUB_BUDGET_STATUS_ID,
							HHSConstants.JAVA_UTIL_MAP);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching sub budget site details for Budget id:"
					+ aoCBGridBean.getContractBudgetID());
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error while fetching sub budget site details for Budget Id:"
					+ aoCBGridBean.getContractBudgetID());
			throw new ApplicationException("Error while fetching sub budget site details for user Type:", loExp);
		}
		return lsSubBudgetStatusId;
	}

	/**
	 * <p>
	 * This method inserts/updates the proposal details in database
	 * <ul>
	 * <li>Execute query <b>updateProposalDetails </b></li>
	 * <li>Execute query <b>updateProposalAnswers</b> if QuestionAnswerBeanList
	 * is not null updateProposalAnswers</li>
	 * <li>Execute query <b>insertProposalSiteDetails</b> if condition is
	 * satisfied</li>
	 * <li>Execute query <b>updateProposalSiteDetails</b>for the condition</li>
	 * @param aoMybatisSession - mybatis SQL session
	 * @param aoPropDetails - Proposal details bean
	 * @return flag depecting save was successful
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public boolean saveProposalDetails(SqlSession aoMybatisSession, ProposalDetailsBean aoPropDetails)
			throws ApplicationException
	{
		try
		{
			if (aoPropDetails != null)
			{
				String lsProcurementId = aoPropDetails.getProcurementId();
				String lsProposalId = aoPropDetails.getProposalId();
				String lsModifiedBy = aoPropDetails.getModifiedBy();
				Integer loAnswerUpdated = HHSConstants.INT_ZERO;
				DAOUtil.masterDAO(aoMybatisSession, aoPropDetails, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
						HHSConstants.UPDATE_PROPOSAL_DETAILS, HHSConstants.INPUT_PARAM_CLASS_PROPOSAL_DETAILS_BEAN);
				// if the question answer bean list not null
				if (aoPropDetails.getQuestionAnswerBeanList() != null)
				{
					for (ProposalQuestionAnswerBean loQueAns : aoPropDetails.getQuestionAnswerBeanList())
					{
						loQueAns.setModifiedBy(lsModifiedBy);
						loQueAns.setProcurementId(lsProcurementId);
						loQueAns.setProposalId(lsProposalId);
						loAnswerUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, loQueAns,
								HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.UPDATE_PROPOSAL_ANSWERS,
								HHSConstants.COM_NYC_MODEL_PROP_QUE_ANS_BEAN);
						if (loAnswerUpdated <= HHSConstants.INT_ZERO)
						{
							DAOUtil.masterDAO(aoMybatisSession, loQueAns, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
									HHSConstants.INSERT_PROPOSAL_ANSWERS, HHSConstants.COM_NYC_MODEL_PROP_QUE_ANS_BEAN);
						}
					}
				}
				// Iterate through the SiteDetails map list
				for (SiteDetailsBean loSiteDetails : aoPropDetails.getSiteDetailsList())
				{
					loSiteDetails.setModifiedBy(lsModifiedBy);
					loSiteDetails.setProcurementId(lsProcurementId);
					loSiteDetails.setProposalId(lsProposalId);
					if (loSiteDetails.getActionTaken() != null
							&& loSiteDetails.getActionTaken().equalsIgnoreCase(HHSConstants.DELETE))
					{
						DAOUtil.masterDAO(aoMybatisSession, loSiteDetails, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
								HHSConstants.DEL_PROPOSAL_SITE_DETAILS, HHSConstants.COM_NYC_HHS_MODEL_SITEDETAILSBEAN);
					}
					else if (loSiteDetails.getActionTaken() != null
							&& loSiteDetails.getActionTaken().equalsIgnoreCase(HHSConstants.INSERT))
					{
						HHSUtil.convertAddressValidationFields(loSiteDetails.getAddressRelatedData(), loSiteDetails);
						DAOUtil.masterDAO(aoMybatisSession, loSiteDetails, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
								HHSConstants.INS_PROPOSAL_SITE_DETAILS, HHSConstants.COM_NYC_HHS_MODEL_SITEDETAILSBEAN);
					}
					else if (loSiteDetails.getActionTaken() != null
							&& loSiteDetails.getActionTaken().equalsIgnoreCase(HHSConstants.UPDATE))
					{
						HHSUtil.convertAddressValidationFields(loSiteDetails.getAddressRelatedData(), loSiteDetails);
						DAOUtil.masterDAO(aoMybatisSession, loSiteDetails, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
								HHSConstants.UPDATE_PROPOSAL_SITE_DETAILS,
								HHSConstants.COM_NYC_HHS_MODEL_SITEDETAILSBEAN);
					}
				}
			}
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (ApplicationException loAppEx)
		{
			LOG_OBJECT.Error("Error while saving proposal details", loAppEx);
			setMoState("Error while saving proposal details");
			throw loAppEx;
		}
		// Catch any exception thrown from the code and wrap it into application
		// Exception and throw it forward
		catch (Exception loAppEx)
		{
			LOG_OBJECT.Error("Error while saving proposal details", loAppEx);
			setMoState("Error while saving proposal details");
			throw new ApplicationException("Error while saving proposal details", loAppEx);
		}
		setMoState("Successfully saved proposal details");
		return true;
	}

	/**
	 * <p>
	 * This method fetches User Details corresponding to particular User Id
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Check if user id and organization id are not null</li>
	 * <li>If null throw exception else proceed as below</li>
	 * <li>Fetch user details from DB using query id <b>fetchMemberDetails</b></li>
	 * </ul>
	 * 
	 * Change : for single user multiple access related changes(add check based
	 * on organization id) Changed By Varun
	 * </p>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asUserId - User id
	 * @param asOrganizationId - Organization id
	 * @return map of member details
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> fetchMemberDetails(SqlSession aoMybatisSession, String asUserId, String asOrganizationId)
			throws ApplicationException
	{
		Map<String, String> loOrganizationMemberDetails = null;
		try
		{
			if (asUserId != null && asOrganizationId != null)
			{
				Map<String, String> loDataMap = new HashMap<String, String>();
				loDataMap.put(HHSConstants.AS_USER_ID, asUserId);
				loDataMap.put(HHSConstants.AS_ORGANIZATION_ID, asOrganizationId);
				loOrganizationMemberDetails = (Map<String, String>) DAOUtil.masterDAO(aoMybatisSession, loDataMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_MEMBER_DETAILS,
						HHSConstants.JAVA_UTIL_MAP);
			}
			else
			{
				throw new ApplicationException("Error while fetching member details:: user id is null");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching member details :" + asUserId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error while fetching member details :" + asUserId);
			throw new ApplicationException("Error while fetching member details :", loExp);
		}
		setMoState("Successfully fetched member details :" + asUserId);
		return loOrganizationMemberDetails;
	}

	/**
	 * <p>
	 * This method fetches list of all members associated with an organization
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>check if organization id is not null
	 * <li>Fetch list of members associated with the organization of current
	 * user using query id <b>fetchAllOrganizationMembers</b></li>
	 * </ul>
	 * 
	 * Change: used organziation id instead of staff id
	 * </p>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asOrganizationId - Organization Id
	 * @return loOrganizationMemberList list of members in the organization
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, String>> fetchAllOrganizationMembers(SqlSession aoMybatisSession, String asOrganizationId)
			throws ApplicationException
	{
		List<Map<String, String>> loOrganizationMemberList = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AS_ORGANIZATION_ID, asOrganizationId);
		try
		{
			if (asOrganizationId != null)
			{
				loOrganizationMemberList = (List<Map<String, String>>) DAOUtil.masterDAO(aoMybatisSession,
						asOrganizationId, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
						HHSConstants.FETCH_ALL_ORG_MEMBERS, HHSConstants.JAVA_LANG_STRING);
			}
			else
			{
				throw new ApplicationException("User Id cannot be null while fetching organization members");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error while fetching all org members for user id :" + asOrganizationId);
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching proposal details for proposal id :", loExp);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error while fetching all org members for user id :" + asOrganizationId);
			LOG_OBJECT.Error("Error while fetching proposal details for proposal id :", loExp);
			throw new ApplicationException("Error while fetching all org members for user id :" + asOrganizationId,
					loExp);
		}
		setMoState("Successfully fetched all org members for user id :" + asOrganizationId);
		return loOrganizationMemberList;
	}

	/**
	 * Changed as part of emergency build 2.3.1, added extra parameter
	 * asProviderStatus that checks provider eligibility to submit proposal
	 * 
	 * This method checks if all required fields are complete on S236 (proposal
	 * details) and S238 (proposal documents).
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Check for authentication flag. IF true, check for required fields</li>
	 * <li>Check for proposal details count by executing query with id
	 * "getProposalDetailsCount" from proposal mapper</li>
	 * <li>Check for proposal question response count by executing query with id
	 * "getProposalQuesResponseCount" from proposal mapper</li>
	 * <li>Check for proposal site count by executing query with id
	 * "getProposalSiteCount" from proposal mapper</li>
	 * <li>Check for proposal document count by executing query with id
	 * "getProposalDocumentCount" from proposal mapper</li>
	 * <li>If required fields are there return true else return false</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession aoMybatisSession
	 * @param asProposalId a string value of proposal Id
	 * @param asProcurementId a string value of procurement Id
	 * @param asProviderStatus status/eligibility of provider to submit/insert
	 *            proposal
	 * @return Boolean a boolean flag indicating required fields are completed
	 *         loValidationStatus or not
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean checkAllRequiredFieldsCompleted(SqlSession aoMybatisSession, String asProposalId,
			String asProcurementId, String asProviderStatus) throws ApplicationException
	{
		Boolean loValidationStatus = false;
		boolean lbProviderNotEligible = false;
		try
		{
			HashMap<String, String> loPropMap = new HashMap<String, String>();
			loPropMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
			loPropMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);

			Integer loRestrictSubmitFlag = (Integer) DAOUtil.masterDAO(aoMybatisSession, loPropMap,
					HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.GET_PROV_RESTRICT_SUBMIT_FLAG_PROPOSAL,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			if (null != asProviderStatus
					&& (!asProviderStatus.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROVIDER_SERVICE_APP_REQUIRED)) || loRestrictSubmitFlag == HHSConstants.INT_ONE))
			{
				Integer loProposalDetailsCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, asProposalId,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_PROPOSALS_DETAILS_COUNT,
						HHSConstants.JAVA_LANG_STRING);
				Integer loProposalQuesResponseId = (Integer) DAOUtil.masterDAO(aoMybatisSession, loPropMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_PROPOSALS_QUES_RESPONSE_COUNT,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				Integer loProposalSiteCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loPropMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_PROPOSAL_SITE_COUNT,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				Integer loProposalDocumentCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, loPropMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_PROPOSALS_DOC_COUNT,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				if (loProposalDetailsCount == HHSConstants.INT_ONE && loProposalQuesResponseId == HHSConstants.INT_ZERO
						&& loProposalSiteCount > HHSConstants.INT_ZERO
						&& loProposalDocumentCount == HHSConstants.INT_ZERO)
				{
					loValidationStatus = true;
				}
				setMoState("Proposal Details Required Fields checked successfully for proposalId:" + asProposalId);
			}
			else
			{
				lbProviderNotEligible = true;
				throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
						HHSConstants.ERROR_MESSSAGE_UNAUTHORIZED_ACCESS));
			}

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			if (!lbProviderNotEligible)
			{
				LOG_OBJECT.Error("Error occurred while checking proposal details required fields proposalId:",
						asProposalId);
			}
			setMoState("Error occurred while checking proposal details required fields proposalId:" + asProposalId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			if (!lbProviderNotEligible)
			{
				LOG_OBJECT.Error("Error occurred while checking proposal details required fields proposalId:",
						asProposalId);
			}
			setMoState("Error occurred while checking proposal details required fields proposalId:" + asProposalId);
			throw new ApplicationException(
					"Error occurred while checking proposal details required fields proposalId:", loExp);
		}
		return loValidationStatus;
	}

	/**
	 * This method will get planned due date for proposal
	 * <ul>
	 * <li>1. Taking procurement Id as input</li>
	 * <li>2.connect to database and querying "getDueDate" id in the database
	 * using DAOUtil</li>
	 * <li>3.Return due date</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession mybatis sql session
	 * @param asProcurementId procurement id
	 * @return Date loDueDate
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Date checkForDueDate(SqlSession aoMyBatisSession, String asProcurementId) throws ApplicationException
	{
		Date loDueDate = null;
		try
		{
			loDueDate = (Date) DAOUtil
					.masterDAO(aoMyBatisSession, asProcurementId, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
							HHSConstants.GET_DUE_DATE, HHSConstants.JAVA_LANG_STRING);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while checking for due date for Procurement Id:" + asProcurementId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error occurred while checking for due date for Procurement Id:" + asProcurementId);
			throw new ApplicationException("Error occurred while checking for due date for Procurement Id:"
					+ asProcurementId, loExp);
		}
		return loDueDate;
	}

	/**
	 * This method will update the Proposal status to submitted
	 * 
	 * <ul>
	 * <li>Check for propsoal status flag</li>
	 * <li>If true, connect to database and querying "updateProposalStatus" id
	 * in the database using DAOUtil</li>
	 * <li>If document status updated successfully, execute query with ID
	 * "updateDocumentStatusSubmitted" to update status of all documents to
	 * submitted</li>
	 * <li>Return Success status</li>
	 * </ul>
	 * 
	 * Change : Updated the flag definition of aoProposalStatusFlag(based on
	 * previous services) Change by : Pallavi 5th march
	 * 
	 * @param aoMyBatisSession SQL session
	 * @param asProposalId a string value of proposalId
	 * @param asUserId a string value of user Id
	 * @param aoProposalStatusFlag a boolean value indicating proposal status in
	 *            "Draft" or "Returned for Revision"
	 * @param aoStatusMap - Map of proposal data
	 * @return Boolean a boolean value of proposal submitted status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean submitProposal(SqlSession aoMyBatisSession, String asProposalId, String asUserId,
			Boolean aoProposalStatusFlag, Map<String, String> aoStatusMap) throws ApplicationException
	{
		Boolean loSubmitStatus = false;
		try
		{
			if (null != aoProposalStatusFlag && aoProposalStatusFlag)
			{
				HashMap<String, Object> loPropMap = new HashMap<String, Object>();
				loPropMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
				loPropMap.put(HHSConstants.USER_ID, asUserId);
				loPropMap.put(HHSConstants.STATUS_ID, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_SUBMITTED));
				Integer loUpdateStatus = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loPropMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.UPDATE_PROPOSAL_STATUS,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				String lsSubmittedDate = (String) DAOUtil.masterDAO(aoMyBatisSession, loPropMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_PROPOSAL_SUBMITTED_DATE,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				aoStatusMap.put(HHSConstants.PROP_MOD_DATE, lsSubmittedDate);
				setMoState("Proposal submitted successfully for Proposal Id:" + asProposalId);
				/**
				 * When the proposal submitted successfully then we need to
				 * update the status of all document corresponding to proposal
				 * id
				 * */
				if (loUpdateStatus > HHSConstants.INT_ZERO)
				{
					DAOUtil.masterDAO(aoMyBatisSession, loPropMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
							HHSConstants.UPDATE_PROPOSAL_DOCUMENT_STATUS, HHSConstants.JAVA_UTIL_HASH_MAP);
					setMoState("Proposal Document Status submitted successfully for Proposal Id:" + asProposalId);
				}
				loSubmitStatus = true;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error occurred while submitting proposal for Proposal Id:", asProposalId);
			setMoState("Error occurred while submitting proposal for Proposal Id:" + asProposalId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while submitting proposal for Proposal Id:", asProposalId);
			setMoState("Error occurred while submitting proposal for Proposal Id:" + asProposalId);
			throw new ApplicationException("Error occurred while submitting proposal for Proposal Id:", loExp);
		}
		return loSubmitStatus;
	}

	/**
	 * This method fetches the sorted proposal data and display it on the page.
	 * 
	 * <ul>
	 * <li>1. Execute the query "getProposalSummary" with procurement Id as a
	 * parameter.</li>
	 * <li>2. Create the map to hold the context data.</li>
	 * <li>3. Set the bean in the map.</li>
	 * <li>4. Return the list of the ProposalDetailsBean.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoProposalDetailsBean proposal details bean
	 * @return List of proposal details bean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ProposalDetailsBean> getProposalSummary(SqlSession aoMybatisSession,
			ProposalDetailsBean aoProposalDetailsBean) throws ApplicationException
	{

		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROPOSAL_DETAIL_BEAN_KEY, aoProposalDetailsBean);
		List<ProposalDetailsBean> loProposalDetailsBeanList = null;
		try
		{
			loContextDataMap.put(HHSConstants.PROCUREMENT_ID, aoProposalDetailsBean.getProcurementId());
			loProposalDetailsBeanList = (List<ProposalDetailsBean>) DAOUtil.masterDAO(aoMybatisSession,
					aoProposalDetailsBean, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
					HHSConstants.GET_PROPOSAL_SUMMARY, HHSConstants.INPUT_PARAM_CLASS_PROPOSAL_DETAILS_BEAN);
			if (loProposalDetailsBeanList == null || loProposalDetailsBeanList.isEmpty())
			{
				loProposalDetailsBeanList = (List<ProposalDetailsBean>) DAOUtil.masterDAO(aoMybatisSession,
						aoProposalDetailsBean, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
						HHSConstants.GET_PROPOSAL_SUMMARY_PROPOSAL_DUE_DATE,
						HHSConstants.INPUT_PARAM_CLASS_PROPOSAL_DETAILS_BEAN);
			}
			setMoState("Proposal details fetched successfully for Procurement Id:"
					+ aoProposalDetailsBean.getProcurementId());
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loExp)
		{
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting proposal details", loExp);
			setMoState("Error while getting proposal details");
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while getting proposal details", loEx);
			setMoState("Error while getting proposal details");
			throw new ApplicationException("Error while getting proposal details", loEx);
		}
		return loProposalDetailsBeanList;
	}

	/**
	 * This method fetches the sorted proposal data and display it on the page.
	 * 
	 * <ul>
	 * <li>1. Execute the query "getProposalCount" with procurement Id as a
	 * parameter.</li>
	 * <li>2. Create the map to hold the context data.</li>
	 * <li>3. Set the bean in the map.</li>
	 * <li>4. Return the list of the ProposalDetailsBean.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis sql session
	 * @param aoProposalDetailsBean proposal details bean
	 * @return loProposalCount Integer
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Integer getProposalCount(SqlSession aoMybatisSession, ProposalDetailsBean aoProposalDetailsBean)
			throws ApplicationException
	{

		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROPOSAL_DETAIL_BEAN_KEY, aoProposalDetailsBean);
		Integer loProposalCount = null;
		try
		{
			loContextDataMap.put(HHSConstants.PROCUREMENT_ID, aoProposalDetailsBean.getProcurementId());
			loProposalCount = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoProposalDetailsBean,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_PROPOSAL_COUNT,
					HHSConstants.INPUT_PARAM_CLASS_PROPOSAL_DETAILS_BEAN);
			setMoState("Proposal COUNT fetched successfully for Procurement Id:"
					+ aoProposalDetailsBean.getProcurementId());
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException loExp)
		{
			setMoState("Error while getting proposal details");
			loExp.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting proposal details", loExp);
			throw loExp;
		}
		// handling exception other than Application Exception.
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while getting proposal details", loEx);
			setMoState("Error while getting proposal details");
			throw new ApplicationException("Error while getting proposal details", loEx);
		}
		return loProposalCount;
	}

	/**
	 * This method will retract the proposal based on given input proposal id
	 * <ul>
	 * <li>1. Retrieve proposal Id from the channel object and execute query
	 * checkProcurementProposalStatus</li>
	 * <li>2. Execute query <b>retractProposal</b> to fetch status_id from
	 * status table where process_type='proposal' and Update status_id where
	 * proposal id is equal to fetched proposal_id</li>
	 * <li>4. If the update process is successful then set boolean flag as true
	 * else set it as false</li>
	 * <li>5. Return boolean flag.</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProposalId - proposal Id
	 * @return loRetractProposalStatus boolean flag
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean retractProposal(SqlSession aoMyBatisSession, String asProposalId) throws ApplicationException
	{
		Boolean loRetractProposalStatus = false;
		try
		{
			ProposalDetailsBean loProposalDetailsBean = (ProposalDetailsBean) DAOUtil.masterDAO(aoMyBatisSession,
					asProposalId, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
					HHSConstants.CHECK_PROCUREMENT_PROPOSAL_STATUS, HHSConstants.JAVA_LANG_STRING);
			if (null != loProposalDetailsBean)
			{
				String lsProposalStatus = loProposalDetailsBean.getProposalStatus();
				String lsEvalStatus = loProposalDetailsBean.getEvaluationGroupStatus();
				/**
				 * in below if block first it will check the proposal status for
				 * retract the proposal the proposal status should be
				 * "SUBMITTED" and evaluation group status should be "RELEASED"
				 * */
				if (null != lsProposalStatus)
				{
					if ((null != lsEvalStatus && (lsEvalStatus.equals(PropertyLoader.getProperty(
							HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_RELEASED))))
							&& (lsProposalStatus.equals(PropertyLoader.getProperty(
									HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_SUBMITTED))))
					{
						Integer lbRetractProposal = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asProposalId,
								HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.PROPOSAL_RETRACT,
								HHSConstants.JAVA_LANG_STRING);
						if (lbRetractProposal > HHSConstants.INT_ZERO)
						{
							loRetractProposalStatus = true;
							/**
							 * If the proposal is retracted successfully execute
							 * below query query update the document status
							 */
							DAOUtil.masterDAO(aoMyBatisSession, asProposalId,
									HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
									HHSConstants.RESET_PROPOSAL_DOCUMENT_STATUS_COMPLETED,
									HHSConstants.JAVA_LANG_STRING);
							setMoState("Proposal Document Status completed successfully for Proposal Id:"
									+ asProposalId);

                            /*[Start]R8.10. QC9477 */
                            /*Map<String, Integer> loProposalMap = (Map<String, Integer>) DAOUtil.masterDAO(aoMyBatisSession,
                                    asProposalId, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROC_ID_ORG_ID,
                                    HHSConstants.JAVA_LANG_STRING);

                             DAOUtil.masterDAO(aoMyBatisSession, loProposalMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, 
                                     HHSConstants.SETUP_PROVIDER_PROPOSAL_STATUS, HHSConstants.JAVA_UTIL_MAP);*/
                            /*[End]R8.10.0 QC9477 */

						}
						else
						{
							loRetractProposalStatus = false;
						}

					}

				}
			}

			else
			{
				loRetractProposalStatus = false;
			}

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while Retract the proposal", loExp);
			setMoState("Error occurred while retracting for Proposal Id:" + asProposalId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while Retract the proposal", loExp);
			setMoState("Error occurred while retracting for Proposal Id:" + asProposalId);
			throw new ApplicationException("Exception Occured while Retract the proposal", loExp);
		}
		return loRetractProposalStatus;
	}

	/**
	 * This method will cancel the provider proposal
	 * <ul>
	 * <li>1. Retrieve proposal Id and proposal status from the channel object</li>
	 * <li>2. If proposal status is "Submitted Proposal" and procurement status
	 * is "Released" then retract proposal via executing
	 * <b>updateRetractProposal</b></li>
	 * <li>3. Else if proposal status is "Draft" then execute query
	 * <b>cancelProposal</b> and <b>deleteProposalDocument</b> to delete data
	 * from the database tables corresponding to that proposal Id</li>
	 * and <b>deleteProposalSite</b> to delete the proposal site information
	 * from the data base table corresponding to that proposal id
	 * and<b>deleteProposalQueationResponse</b> to delete the proposal question
	 * response from the data base table corresponding to that proposal id
	 * <li>3. Fetch boolean status flag stating whether or not the deletion
	 * process was successful</li>
	 * <li>4. Return boolean flag</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProposalId - proposal Id
	 * @param aoProposalStatusFlag Proposal status flag
	 * @return boolean flag
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean cancelProposal(SqlSession aoMyBatisSession, String asProposalId, Boolean aoProposalStatusFlag)
			throws ApplicationException
	{
		Boolean loCancelProposalStatus = false;
		try
		{
			// checking if the value of proposal status flag is true
			if (aoProposalStatusFlag)
			{
				/**
				 * below query will delete the proposal document corresponding
				 * to proposal id from the database while we cancel the
				 * proposal.
				 * */
				Integer loDeleteProposalDocument = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asProposalId,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.DELETE_PROPOSAL_DOCUMENT,
						HHSConstants.JAVA_LANG_STRING);

				// checking if count of delete proposal document is greater than
				// zero
				if (loDeleteProposalDocument > HHSConstants.INT_ZERO)
				{
					setMoState("Proposal Document deleted successfully for Proposal Id:" + asProposalId);
				}

				/**
				 * below query will delete the proposal site information
				 * corresponding to proposal id from the database while we
				 * cancel the proposal.
				 * */

				Integer loDeleteProposalSite = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asProposalId,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.DELETE_PROPOSAL_SITE,
						HHSConstants.JAVA_LANG_STRING);

				// checking if delete proposal site is greater than zero
				if (loDeleteProposalSite > HHSConstants.INT_ZERO)
				{
					setMoState("Proposal Site deleted successfully for Proposal Id:" + asProposalId);
				}

				/**
				 * below query will delete the proposal question response
				 * corresponding to proposal id from the database while we
				 * cancel the proposal.
				 * */

				Integer loDeleteProposalQuestionResponse = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asProposalId,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.DELETE_PROPOSAL_QUESTION_RESPONSE,
						HHSConstants.JAVA_LANG_STRING);

				// checking if delete proposal question response is greater than
				// zero
				if (loDeleteProposalQuestionResponse > HHSConstants.INT_ZERO)
				{
					setMoState("Proposal Question Response deleted successfully for Proposal Id:" + asProposalId);
				}

				/**
				 * below query will delete the proposal detail information
				 * corresponding to proposal id from the database while we
				 * cancel the proposal.
				 * */
				/*[Start]R8.10.0 QC9477 */
/*				Map<String, Integer> loProposalMap = (Map<String, Integer>) DAOUtil.masterDAO(aoMyBatisSession,
						asProposalId, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROC_ID_ORG_ID,
						HHSConstants.JAVA_LANG_STRING);*/
				/*[End]R8.10.0 QC9477 */

				Integer loCancelProposal = (Integer) DAOUtil.masterDAO(aoMyBatisSession, asProposalId,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.CANCEL_PROPOSAL,
						HHSConstants.JAVA_LANG_STRING);

				// checking if count of cancel proposal is greater than zero
				if (loCancelProposal > HHSConstants.INT_ZERO)
				{
					loCancelProposalStatus = true;
					setMoState("Proposal cancelled successfully for Proposal Id:" + asProposalId);
					/*[Start]R8.10.0 QC9477 */
/*					 DAOUtil.masterDAO(aoMyBatisSession, loProposalMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, 
							 HHSConstants.SETUP_PROVIDER_PROPOSAL_STATUS, HHSConstants.JAVA_UTIL_MAP);
*/					/*[End]R8.10.0 QC9477 */
					
				}

				else
				{
					loCancelProposalStatus = false;
				}

			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while cancelling for Proposal Id:" + asProposalId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error occurred while cancelling for Proposal Id:" + asProposalId);
			throw new ApplicationException("Error occurred while cancelling for Proposal Id:" + asProposalId, loExp);
		}
		return loCancelProposalStatus;
	}

	/**
	 * This method will used for getting the proposalStatus and checks if it is
	 * in "Draft" or "Returned for Revisions" status
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get the proposal Id from input</li>
	 * <li>Get the proposal related data from database for proposal Id</li>
	 * <li>Check if proposal can be submitted based on [checkProposalEditSubmit]
	 * rule</li>
	 * <li>Execute query <b>getProposalAndPoolStatus</b></li>
	 * </ul>
	 * 
	 * Change: Used rule [checkProposalEditSubmit] to check if proposal is
	 * editable Changed By : Pallavi 5th March
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoValidateStatus - Validation status
	 * @param asProposalId - proposal Id
	 * @return Boolean loProposalEditableFlag
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	@SuppressWarnings("unchecked")
	public Boolean checkProposalEditSubmit(SqlSession aoMyBatisSession, Boolean aoValidateStatus, String asProposalId)
			throws ApplicationException
	{
		Boolean loProposalEditableFlag = false;
		try
		{
			if (null != aoValidateStatus && aoValidateStatus)
			{
				Map<String, Object> aoResultMap = (Map<String, Object>) DAOUtil.masterDAO(aoMyBatisSession,
						asProposalId, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
						HHSConstants.GET_PROPOSAL_AND_POOL_STATUS, HHSConstants.JAVA_LANG_STRING);
				if (null != aoResultMap)
				{
					Channel loChannel = new Channel();
					loChannel.setData(HHSConstants.PROCUREMENT_STATUS,
							String.valueOf(aoResultMap.get(HHSConstants.STATUS_ID_KEY)));
					loChannel.setData(HHSConstants.PROPOSAL_STATUS,
							String.valueOf(aoResultMap.get(HHSConstants.PROPOSAL_STATUS_ID)));
					loChannel.setData(HHSConstants.EVALUATION_POOL_STATUS,
							String.valueOf(aoResultMap.get(HHSConstants.EVALUATION_POOL_STATUS_ID)));
					loProposalEditableFlag = Boolean.valueOf((String) Rule.evaluateRule(
							HHSConstants.CHECK_PROPOSAL_EDIT_SUBMIT, loChannel));
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loException)
		{
			LOG_OBJECT.Error("Exception Occured while Checking proposal is edittable :: Proposal Id:", loException);
			setMoState("Exception Occured while Checking proposal is edittable :: Proposal Id:" + asProposalId);
			throw loException;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while Checking proposal is edittable :: Proposal Id:", loExp);
			setMoState("Exception Occured while Checking proposal is edittable :: Proposal Id:" + asProposalId);
			throw new ApplicationException("Exception Occured while Checking proposal is edittable :: Proposal Id:",
					loExp);
		}
		return loProposalEditableFlag;
	}

	/**
	 * This method will used for getting the proposalStatus and checks if it is
	 * in "Draft" or "Returned for Revisions" status
	 * 
	 * <ul>
	 * <li>Get the proposal Id from input</li>
	 * <li>Get the proposal status Id based on proposal Id by executing the
	 * query <b>fetchProposalStatusId</b></li>
	 * <li>If proposal status Id is in "Draft" or "Returned for Revision",
	 * return boolean flag true</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asUploadingDocType - uploading document type
	 * @param asProposalId - proposal Id
	 * @return Boolean loProposalEditableFlag
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public Boolean checkProposalEditSubmit(SqlSession aoMyBatisSession, String asUploadingDocType, String asProposalId)
			throws ApplicationException
	{
		Boolean loProposalEditableFlag = false;
		try
		{
			// if uploading document type is proposal
			if (HHSConstants.PROPOSAL.equalsIgnoreCase(asUploadingDocType) && null != asProposalId)
			{
				String lsProposalStatusId = (String) DAOUtil.masterDAO(aoMyBatisSession, asProposalId,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROPOSAL_STATUS_ID,
						HHSConstants.JAVA_LANG_STRING);
				if (null != lsProposalStatusId
						&& (lsProposalStatusId.equals(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_DRAFT)) || lsProposalStatusId
								.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION))))
				{
					loProposalEditableFlag = true;
				}
			}
			else
			{
				loProposalEditableFlag = true;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loException)
		{
			LOG_OBJECT.Error("Exception Occured while getting the proposal status ID", loException);
			setMoState("Error occurred while checking Proposal Id:" + asProposalId);
			throw loException;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while getting the proposal status ID", loExp);
			setMoState("Error occurred while checking Proposal Id:" + asProposalId);
			throw new ApplicationException("Exception Occured while getting the proposal status ID", loExp);
		}
		return loProposalEditableFlag;
	}

	/**
	 * This method will used for getting the proposalStatus and checks if it is
	 * in "Draft" or "Returned for Revisions" status
	 * 
	 * <ul>
	 * <li>Get the proposal Id from input</li>
	 * <li>Get the proposal status Id based on proposal Id</li>
	 * <li>If proposal status Id is in "Draft" or "Returned for Revision",
	 * return boolean flag true</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asUploadingDocType - uploading document type
	 * @param asProposalId - proposal Id
	 * @return Boolean loProposalEditableFlag
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public Boolean checkProposalEdit(SqlSession aoMyBatisSession, String asUploadingDocType, String asProposalId)
			throws ApplicationException
	{
		Boolean loProposalEditableFlag = false;
		try
		{
			// if uploading document type is proposal
			if (HHSConstants.PROPOSAL.equalsIgnoreCase(asUploadingDocType) && null != asProposalId)
			{
				String lsProposalStatusId = (String) DAOUtil.masterDAO(aoMyBatisSession, asProposalId,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROPOSAL_STATUS_ID,
						HHSConstants.JAVA_LANG_STRING);
				if (null != lsProposalStatusId
						&& lsProposalStatusId.equals(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_DRAFT))
						|| lsProposalStatusId.equals(PropertyLoader.getProperty(
								HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION)))
				{
					loProposalEditableFlag = true;
				}
			}
			else
			{
				loProposalEditableFlag = true;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loException)
		{
			LOG_OBJECT.Error("Exception Occured while getting the proposal status ID", loException);
			setMoState("Error occurred while checking Proposal Id:" + asProposalId);
			throw loException;
		}
		return loProposalEditableFlag;
	}

	/**
	 * This method will check proposal is elgible for cancel or not
	 * <ul>
	 * <li>fetch the proposal status based in given input proposal id by
	 * executing the query <b>fetchProposalStatusId</b></li>
	 * <li>Now check the proposal status if this is "draft" then boolean flag
	 * return true</li>
	 * </ul>
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProposalId - proposal Id
	 * @return Boolean loProposalCancelFlag
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public Boolean checkProposalCancel(SqlSession aoMyBatisSession, String asProposalId) throws ApplicationException
	{

		Boolean loProposalCancelFlag = false;
		try
		{
			String lsProposalStatusId = (String) DAOUtil.masterDAO(aoMyBatisSession, asProposalId,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROPOSAL_STATUS_ID,
					HHSConstants.JAVA_LANG_STRING);
			// checking if proposal status Id is not null and is equal to
			// proposal status draft Id
			if (null != lsProposalStatusId
					&& lsProposalStatusId.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROPOSAL_DRAFT)))
			{
				loProposalCancelFlag = true;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loException)
		{
			LOG_OBJECT.Error("Exception Occured while Checking the proposal status ID", loException);
			setMoState("Error occurred while cancelling for Proposal Id:" + asProposalId);
			throw loException;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while Checking the proposal status ID", loExp);
			setMoState("Error occurred while cancelling for Proposal Id:" + asProposalId);
			throw new ApplicationException("Exception Occured while Checking the proposal status ID", loExp);
		}
		return loProposalCancelFlag;
	}

	/**
	 * This method will fetch the document list corresponding to proposal id by
	 * executing query <b>fetchProposalDocumentId</b>
	 * @param aoMyBatisSession mybatis sql session
	 * @param asProposalId proposal id
	 * @return list loDocumentIdList
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<String> getDocumentIdList(SqlSession aoMyBatisSession, String asProposalId) throws ApplicationException
	{
		List<String> loDocumentIdList = null;
		try
		{
			loDocumentIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, asProposalId,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROPOSAL_DOCUMENT_ID,
					HHSConstants.JAVA_LANG_STRING);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Exception Occured while getting the document ID List", loExp);
			setMoState("Error occurred while getting the document ID List for Proposal Id:" + asProposalId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Exception Occured while getting the document ID List", loExp);
			setMoState("Error occurred while getting the document ID List for Proposal Id:" + asProposalId);
			throw new ApplicationException("Exception Occured while getting the document ID List", loExp);
		}

		return loDocumentIdList;
	}

	/**
	 * This method will fetch the proposal title based on given proposal id
	 * <ul>
	 * <li>Execute query <b> fetchProposalTitle </b></li>
	 * </ul>
	 * @param aoMyBatisSession mybatis SQL session
	 * @param asProposalId Proposal Id
	 * @return lsProposalTitle a String proposal Title
	 * @throws ApplicationException If an ApplicationException occurs
	 */

	public String fetchProposalTitle(SqlSession aoMyBatisSession, String asProposalId) throws ApplicationException
	{
		String lsProposalTitle = null;

		try
		{
			lsProposalTitle = (String) DAOUtil.masterDAO(aoMyBatisSession, asProposalId,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROPOSAL_TITLE,
					HHSConstants.JAVA_LANG_STRING);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here.
		 */
		catch (ApplicationException loAppEx)
		{
			setMoState("Exception occured while fetching the Proposal Title Based On Proposal ID: " + asProposalId);
			LOG_OBJECT.Error("Exception occured while fetching the Proposal Title  :", loAppEx);
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			setMoState("Exception occured while fetching the Proposal Title Based On Proposal ID: " + asProposalId);
			LOG_OBJECT.Error("Exception occured while fetching the Proposal Title  :", loExp);
			throw new ApplicationException("Exception occured while fetching the Proposal Title  :", loExp);
		}
		return lsProposalTitle;
	}

	/**
	 * This method will fetch the details of the selected proposal and set the
	 * details bean in the channel object
	 * <ul>
	 * <li>Get the proposal id from the channel object</li>
	 * <li>Execute the select query <b>fetchProposalDetails</b> with argument
	 * proposal id</li>
	 * <li>Execute the query <b> fetchProposalStaffId </b></li>
	 * <li>Set the details bean into the channel object.</li>
	 * <li>This method was updated in R4.</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Sql Session object
	 * @param asProposalId Proposal Id
	 * @return ProposalDetailsBean object with all details
	 * @throws ApplicationException application exception
	 */
	public ProposalDetailsBean fetchProposalDetails(SqlSession aoMyBatisSession, String asProposalId)
			throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailBean = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
		try
		{
			// checking whether or not the proposal Id is null
			if (asProposalId != null)
			{
				String lsStaffId = (String) DAOUtil.masterDAO(aoMyBatisSession, asProposalId,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROPOSAL_STAFF_ID,
						HHSConstants.JAVA_LANG_STRING);
				// checking if staff id is null or is blank
				if (lsStaffId == null || lsStaffId.equals(HHSConstants.EMPTY_STRING))
				{
					loProposalDetailBean = (ProposalDetailsBean) DAOUtil.masterDAO(aoMyBatisSession, asProposalId,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_NEW_PROPOSAL_DETAILS,
							HHSConstants.JAVA_LANG_STRING);
				}
				else
				{
					loProposalDetailBean = (ProposalDetailsBean) DAOUtil.masterDAO(aoMyBatisSession, asProposalId,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROPOSAL_DETAILS,
							HHSConstants.JAVA_LANG_STRING);
				}
			}
			else
			{
				throw new ApplicationException("Proposal Id cannot be null while fetching the proposal details");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching proposal details for proposal id :", aoAppEx);
			setMoState("Error while fetching proposal details for proposal id :" + asProposalId);
			throw aoAppEx;
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching proposal details for proposal id :", aoAppEx);
			setMoState("Error while fetching proposal details for proposal id :" + asProposalId);
			throw new ApplicationException("Error while fetching proposal details for proposal id :", aoAppEx);
		}
		return loProposalDetailBean;
	}

	/**
	 * This method is used to determine whether to display proposal details in
	 * read only mode.
	 * <ul>
	 * <li>Get the proposal details bean from the channel object</li>
	 * <li>check the proposal status from the bean</li>
	 * <li>If the status is either "submitted" or "acceptedforevaluation" then
	 * return read only true</li>
	 * <li>Return Read Only Status</li>
	 * </ul>
	 * 
	 * @param aoProposalDetailBean proposal details bean object
	 * @return Boolean update status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean showProposalDetailsReadonly(ProposalDetailsBean aoProposalDetailBean) throws ApplicationException
	{
		Boolean loProposalDetailsReadonly = false;
		// Start || Changes done for enhancement #6577 for Release 3.10.0
		try
		{
			if (null != aoProposalDetailBean
					&& aoProposalDetailBean.getProposalStatus() != null
					&& (aoProposalDetailBean.getProposalStatus().equalsIgnoreCase(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PROPOSAL_SUBMITTED)) || (aoProposalDetailBean
							.getProposalStatus().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY))
							|| aoProposalDetailBean.getProposalStatus().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PROPOSAL_NON_RESPONSIVE)) || (aoProposalDetailBean
							.getProposalStatus().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PROPOSAL_SELECTED))
							|| aoProposalDetailBean.getProposalStatus().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PROPOSAL_NOT_SELECTED)) || aoProposalDetailBean
							.getProposalStatus().equalsIgnoreCase(
									PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
											HHSConstants.STATUS_PROPOSAL_CANCELLED))))))
			{
				loProposalDetailsReadonly = true;
			}
			// End || Changes done for enhancement #6577 for Release 3.10.0
			else if (null != aoProposalDetailBean
					&& aoProposalDetailBean.getProcReviewStatusId() != null
					&& aoProposalDetailBean.getProcReviewStatusId().equalsIgnoreCase(
							PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.PROPOSAL_ACCEPTED_FOR_EVALUATION_KEY)))
			{
				loProposalDetailsReadonly = true;
			}
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while checking for proc status id", loExp);
			setMoState("Error occurred while checking for proc status id");
			throw new ApplicationException("Error occurred while checking for proc status id", loExp);
		}
		LOG_OBJECT.Debug("Exited showProposalDetailsReadonly ");
		return loProposalDetailsReadonly;
	}

	/**
	 * This method fetches user list, to whom a task can be reassigned based on
	 * user role and user organization
	 * <ul>
	 * <li>Get user id from input task details map</li>
	 * <li>Get agency Id associated with Procurement</li>
	 * <li>Execute the query with Id "fetchPermittedUsers" from proposal mapper</li>
	 * <li>Return the fetched list.</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoUserRoleList - a list of user roles
	 * @param aoTaskMap - a map containing task and procurement details
	 * @param asWobNumber - a string value of workflow Id
	 * @param asUserOrg - a string value of user organization
	 * @return list of type UserBean of permitable users
	 * @throws ApplicationException If an application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<UserBean> fetchPermittedUsers(SqlSession aoMyBatisSession, List<String> aoUserRoleList,
			HashMap<String, Object> aoTaskMap, String asWobNumber, String asUserOrg) throws ApplicationException
	{
		List<UserBean> loUsers = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_USER_MAP, aoTaskMap);
		LOG_OBJECT.Debug("Entered into fetchPermittedUsers for userId:" + loContextDataMap.toString());
		try
		{
			// get task details map and check for null value
			if (null != aoTaskMap)
			{
				HashMap<String, Object> loProposalMap = (HashMap<String, Object>) aoTaskMap.get(asWobNumber);
				if (null != loProposalMap)
				{
					HashMap<String, Object> loUserMap = new HashMap<String, Object>();
					loUserMap
							.put(HHSConstants.USER_ID, (String) loProposalMap.get(P8Constants.PE_WORKFLOW_ASSIGNED_TO));
					if (!(null != asUserOrg && asUserOrg.equalsIgnoreCase(ApplicationConstants.CITY)))
					{
						// get the agency Id from task details map
						String lsAgencyId = (String) loProposalMap.get(P8Constants.PE_WORKFLOW_AGENCY_ID);
						loUserMap.put(HHSConstants.ORGID, lsAgencyId);
					}
					else
					{
						loUserMap.put(HHSConstants.ORGID, asUserOrg);
					}
					loUserMap.put(ApplicationConstants.ACCELERATOR_USER_ROLE_LIST, aoUserRoleList);
					loUsers = (List<UserBean>) DAOUtil.masterDAO(aoMyBatisSession, loUserMap,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PERMITTED_USERS,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					setMoState("User List fethced successfully for user Id:" + loUserMap);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(aoTaskMap);
			LOG_OBJECT.Error("Error occurred while fetching user list for input:", aoAppEx);
			setMoState("Error occurred while fetching user list for user Id:" + aoTaskMap);
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while fetching user list for input:", aoTaskMap.toString());
			setMoState("Error occurred while fetching user list for input:" + aoTaskMap.toString());
			throw new ApplicationException("Error occurred while fetching user list for input", aoEx);
		}

		LOG_OBJECT.Debug("Exited fetchPermittedUsers for user Id:" + loContextDataMap.toString());
		return loUsers;
	}

	/**
	 * This method fetches proposal documents for input proposal Id from task
	 * detail map
	 * <ul>
	 * <li>Check if map is not null and contains proposal Id</li>
	 * <li>Execute the query "fetchProposalDocuments" for the input proposal Id</li>
	 * <li>Return the fetched list</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoTaskDetailMap - a map containing task and procurement properties
	 * @param asWobNumber - a string value of wob number
	 * @return list of documents
	 * @throws ApplicationException If an Application exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ExtendedDocument> fetchProposalDocuments(SqlSession aoMyBatisSession,
			HashMap<String, Object> aoTaskDetailMap, String asWobNumber) throws ApplicationException
	{
		List<ExtendedDocument> loDocumentList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(ApplicationConstants.WOB_NUMBER, asWobNumber);
		LOG_OBJECT.Debug("Entered into fetchProposalDocuments for wob number:" + loContextDataMap.toString());
		try
		{
			// get task details map and check for null value
			if (null != aoTaskDetailMap)
			{
				HashMap<String, Object> loProposalMap = (HashMap<String, Object>) aoTaskDetailMap.get(asWobNumber);
				Integer loVersionNo = null;
				if (null != loProposalMap)
				{
					// get the proposal Id and procurement Id from task details
					// map
					String lsProposalId = (String) loProposalMap.get(P8Constants.PE_WORKFLOW_PROPOSAL_ID);
					String lsProcurementId = (String) loProposalMap.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID);
					HashMap<String, String> loProcProposalMap = new HashMap<String, String>();
					loProcProposalMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
					loProcProposalMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
					// Changes for R5 starts
					loProcProposalMap.put(HHSR5Constants.EVALUATION_POOL_MAPPING_ID,
							(String) loProposalMap.get(P8Constants.EVALUATION_POOL_MAPPING_ID));
					// Changes for R5 ends
					loVersionNo = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loProcProposalMap,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_VERSION_NO_FOR_PROPOSAL_DOC,
							HHSConstants.JAVA_UTIL_MAP);
					loProcProposalMap.put(HHSConstants.DOC_VERSION_NO, loVersionNo.toString());
					loDocumentList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMyBatisSession, loProcProposalMap,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROPOSAL_DOCS,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					setMoState("Proposal Document Details Fetched successfully for ProposalId:" + lsProposalId);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(aoTaskDetailMap);
			LOG_OBJECT.Error("Error occurred while fetching proposal documents for input:", aoAppEx);
			setMoState("Error occurred while fetching proposal documents for input:" + aoTaskDetailMap.toString());
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while fetching proposal documents for input:", aoTaskDetailMap.toString());
			setMoState("Error occurred while fetching proposal documents for input:" + aoTaskDetailMap.toString());
			throw new ApplicationException("Error occurred while fetching proposal documents", aoEx);
		}
		LOG_OBJECT.Debug("Exited ProposalService: fetchProposalDocuments()");
		return loDocumentList;
	}

	/**
	 * This method updates the proposal document status and proposal status in
	 * db
	 * <ul>
	 * <li>Get the input list and iterate through it</li>
	 * <li>If proposal title is "Proposal Details"</li>
	 * <li>Execute the query with Id "updateProposalTaskStatus" to update
	 * proposal task status</li>
	 * <li>Else,Execute the query with Id "updateProposalDocumentStatus" to
	 * update proposal documents status</li>
	 * <li>Return the update status</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoDocumentStatusDetails - list of proposal Document details bean
	 * @return update status flag
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateProposalDocumentAndDetailStatus(SqlSession aoMyBatisSession,
			List<ExtendedDocument> aoDocumentStatusDetails) throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		LOG_OBJECT.Debug("Entered into updateProposalDocumentAndDetailStatus");
		try
		{
			if (null != aoDocumentStatusDetails && aoDocumentStatusDetails.size() > HHSConstants.INT_ZERO)
			{
				// iterate through proposal document detail list
				for (ExtendedDocument loExtendedDocument : aoDocumentStatusDetails)
				{
					// check if proposal title equals "Proposal Details"
					if (null != loExtendedDocument.getProposalTitle()
							&& loExtendedDocument.getProposalTitle()
									.equalsIgnoreCase(HHSConstants.PROPOSAL_DETAILS_KEY))
					{
						// update proposal process status Id
						Map<String, String> loProposalMap = new HashMap<String, String>();
						loProposalMap.put(HHSConstants.PROCESS_STATUS_ID, loExtendedDocument.getDocumentStatus());
						loProposalMap.put(HHSConstants.PROPOSAL_ID, loExtendedDocument.getProposalId());
						DAOUtil.masterDAO(aoMyBatisSession, loProposalMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
								HHSConstants.UPDATE_PROPOSAL_TASK_STATUS, HHSConstants.JAVA_UTIL_MAP);
					}
					else
					{
						// update proposal process document status
						Integer loUpdateDocStatus = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loExtendedDocument,
								HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.UPDATE_PROPOSAL_DOC_STATUS,
								HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
						if (loUpdateDocStatus == 0)
						{
							DAOUtil.masterDAO(aoMyBatisSession, loExtendedDocument,
									HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
									HHSConstants.INSERT_PROPOSAL_DOC_FOR_SUBMITTED_PROPOSAL,
									HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
						}
					}
				}
				setMoState("Proposal documents task status updated successfully");
			}
			loUpdateStatus = true;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occurred while updating proposal documents task status", aoAppEx);
			setMoState("Error occurred while updating proposal documents task status");
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while updating proposal documents task status", aoEx);
			setMoState("Error occurred while updating proposal documents task status");
			throw new ApplicationException("Error occurred while updating proposal documents task status", aoEx);
		}
		LOG_OBJECT.Debug("Exited updateProposalDocumentAndDetailStatus()");
		return loUpdateStatus;
	}

	/**
	 * This method fetches Required and Optional Document Details for given
	 * Proposal Id
	 * 
	 * <ul>
	 * <li>Execute <b>fetchRequiredOptionalDocuments</b></li>
	 * <li>Get list of Required and Optional Document Details and return to
	 * controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession SqlSession
	 * @param aoParamMap parameters map
	 * @param asAwardId award id
	 * @param asProcurementStatusId Procurement status Id
	 * @return List a list of required and optional document details
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ExtendedDocument> fetchRequiredOptionalDocuments(SqlSession aoMybatisSession,
			Map<String, String> aoParamMap, String asAwardId, String asProcurementStatusId) throws ApplicationException
	{
		List<ExtendedDocument> loDocumentList = null;
		if (asAwardId != null && aoParamMap != null)
		{
			try
			{
				aoParamMap.put(HHSConstants.AWARD_ID, asAwardId);
				aoParamMap.put(HHSConstants.PROCUREMENT_STATUS_ID, asProcurementStatusId);
				loDocumentList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_REQ_OPTIONAL_DOCS,
						HHSConstants.JAVA_UTIL_MAP);
				setMoState("Required and Optional Documents Details fetched successfully for proposal Id:"
						+ aoParamMap.get(HHSConstants.PROPOSAL_ID));
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoExp)
			{
				LOG_OBJECT
						.Error("Error while fetching Required and Optional Documents Details for proposal Id:", aoExp);
				setMoState("Error while fetching Required and Optional Documents Details for proposal Id:"
						+ aoParamMap.get(HHSConstants.PROPOSAL_ID));
				throw aoExp;
			}
			catch (Exception loExp)
			{
				LOG_OBJECT
						.Error("Error while fetching Required and Optional Documents Details for proposal Id:", loExp);
				setMoState("Error while fetching Required and Optional Documents Details for proposal Id:"
						+ aoParamMap.get(HHSConstants.PROPOSAL_ID));
				throw new ApplicationException(
						"Error while fetching Required and Optional Documents Details for proposal Id:", loExp);
			}
		}
		return loDocumentList;
	}

	/**
	 * Changed as part of emergency build 2.3.1, added extra parameter
	 * asProviderStatus that checks provider eligibility to insert new proposal
	 * The method will inserts the data on creating the new proposal from
	 * proposal summary page.
	 * 
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Connect to database using SqlSession</li>
	 * <li>2. Execute query id "insertNewProposalDetails"</li>
	 * <li>3. Return the insert status</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession aoMybatisSession
	 * @param aoProposalDetailMap proposal details map
	 * @param asProcurementStatus procurement status
	 * @param asProviderStatus status/eligibility of provider to submit/insert
	 *            proposal
	 * @return String Proposal id
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String insertNewProposalDetails(SqlSession aoMybatisSession, Map<String, String> aoProposalDetailMap,
			String asProcurementStatus, String asProviderStatus) throws ApplicationException
	{
		String lsProposalId = null;
		boolean lbProviderNotEligible = false;
		aoProposalDetailMap.put(HHSConstants.PROC_STATUS_ID, asProcurementStatus);
		try
		{
			if (null != asProcurementStatus
					&& (asProcurementStatus.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROCUREMENT_RELEASED))))
			{
				if (null != asProviderStatus
						&& !asProviderStatus.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
								HHSConstants.STATUS_PROVIDER_SERVICE_APP_REQUIRED)))
				{
					DAOUtil.masterDAO(aoMybatisSession, aoProposalDetailMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
							HHSConstants.INS_NEW_PROPOSAL_DETAILS, HHSConstants.JAVA_UTIL_MAP);
					lsProposalId = (String) DAOUtil.masterDAO(aoMybatisSession, null,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_PROPOSAL_ID, null);


				}
				else
				{
					lbProviderNotEligible = true;
					throw new ApplicationException(PropertyLoader.getProperty(HHSConstants.ERROR_MESSAGE_PROP_FILE,
							HHSConstants.ERROR_MESSSAGE_UNAUTHORIZED_ACCESS));
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loAppEx)
		{
			if (!lbProviderNotEligible)
			{
				LOG_OBJECT.Error("Exception occured while saving proposal details", loAppEx);
			}
			setMoState("Exception occured while saving proposal details");
			throw loAppEx;
		}
		catch (Exception loExp)
		{
			if (!lbProviderNotEligible)
			{
				LOG_OBJECT.Error("Exception occured while saving proposal details", loExp);
			}
			setMoState("Exception occured while saving proposal details");
			throw new ApplicationException("Exception occured while saving proposal details", loExp);
		}
		return lsProposalId;
	}

	/**
	 * This method fetches proposal title and proposal status based on proposal
	 * Id
	 * 
	 * <ul>
	 * <li>Get the proposal ID from input</li>
	 * <li>Execute query with id "getProposalTitle" from proposal mapper</li>
	 * <li>Return map result to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Sql session
	 * @param asProposalId a string value of proposal Id
	 * @return map containing proposal title and status
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> getProposalDetails(SqlSession aoMybatisSession, String asProposalId)
			throws ApplicationException
	{
		Map<String, String> loProposalMap = null;
		try
		{
			loProposalMap = (Map<String, String>) DAOUtil.masterDAO(aoMybatisSession, asProposalId,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_PROPOSAL_TITLE,
					HHSConstants.JAVA_LANG_STRING);
			setMoState("Proposal Title fetched successfully for Proposal Id:" + asProposalId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error occurred while fetching proposal title for proposal Id:" + asProposalId);
			throw loExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error occurred while fetching proposal title for proposal Id:", loExp);
			LOG_OBJECT.Error("Error while getting proposal comments :", loAppEx);
			setMoState("Error while getting proposal comments, proposal id:" + asProposalId);
			throw loAppEx;
		}
		return loProposalMap;
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 This method will get the document list for the specific proposal
	 * <ul>
	 * <li>Get the procurement id and proposal id from the parameter</li>
	 * <li>Execute query with ID <b>getProposalDocumentList</b> from proposal
	 * mapper</li>
	 * <li>Set the map of the proposal documents list with key <b>required</b>
	 * and <b>optional</b></li>
	 * </ul>
	 * 
	 * @param aoMybatisSession valid sql session Object
	 * @param aoParamMap parameter map
	 * @return list of proposal documents
	 * @throws ApplicationException throws application exception
	 */
	@SuppressWarnings("unchecked")
	public List<ExtendedDocument> getProposalDocumentList(SqlSession aoMybatisSession, Map<String, String> aoParamMap)
			throws ApplicationException
	{
		List<ExtendedDocument> loProposalDocList = null;
		Integer loVersionNo = null;
		try
		{
			loVersionNo = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_VERSION_NO_FOR_PROPOSAL_DOC,
					HHSConstants.JAVA_UTIL_MAP);
			aoParamMap.put(HHSConstants.DOC_VERSION_NO, loVersionNo.toString());
			loProposalDocList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_PROP_DOC_LIST,
					HHSConstants.JAVA_UTIL_MAP);
			DAOUtil.masterDAO(aoMybatisSession, aoParamMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
					HHSConstants.UPDATE_DOC_VERSION_NO, HHSConstants.JAVA_UTIL_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error Occured while fething the proposal document list :");
			throw loExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while fething the proposal document list :", loExp);
			LOG_OBJECT.Error("Error Occured while fething the proposal document list :", loAppEx);
			setMoState("Error Occured while fething the proposal document list :");
			throw loAppEx;
		}
		return loProposalDocList;

	}

	/**
	 * This method will insert the details of the proposal documents uploaded or
	 * added by an provider
	 * <ul>
	 * <li>Get the parameter map from the channel</li>
	 * <li>Execute query with ID <b>insertProposalDocumentDetails</b> from
	 * proposal mapper.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql Session
	 * @param aoParamMap Parameter map Object
	 * @param asProcStatus Procurement status
	 * @param asReplacingDocumentId replacing document id
	 * @return loRowsUpdated Integer number of raws inserted
	 * @throws ApplicationException throws application exception
	 */
	public Integer insertProposalDocumentDetails(SqlSession aoMybatisSession, Map<String, Object> aoParamMap,
			String asProcStatus, String asReplacingDocumentId) throws ApplicationException
	{
		Integer loRowsUpdated = null;
		try
		{
			aoParamMap.put(HHSConstants.PROCUREMENT_STATUS, asProcStatus);
			loRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.UPDATE_PROPOSAL_DOC_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
			if (loRowsUpdated < 1)
			{
				loRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.INSERT_PROPOSAL_DOCS_DETAILS,
						HHSConstants.JAVA_UTIL_MAP);
			}

		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error Occured while fething the proposal document list :");
			throw loExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception loExp)
		{
			setMoState("Error Occured while fething the proposal document list :");
			throw new ApplicationException("Error Occured while fething the proposal document list :", loExp);
		}
		return loRowsUpdated;
	}

	/**
	 * This method will insert the details of the BAFO documents uploaded by an
	 * acco manager
	 * <ul>
	 * <li>Get the parameter map from the channel</li>
	 * <li>Execute query with ID <b>insertBAFODocumentDetails</b> from proposal
	 * mapper.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession sql Session
	 * @param aoParamMap Parameter map Object
	 * @return loRowsUpdated Integer number of raws inserted
	 * @throws ApplicationException throws application exception
	 */
	public Integer insertBAFODocumentDetails(SqlSession aoMybatisSession, Map<String, Object> aoParamMap)
			throws ApplicationException
	{
		Integer loRowsUpdated = null;
		try
		{
			loRowsUpdated = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.INSERT_BAFO_DOCS_DETAILS,
					HHSConstants.JAVA_UTIL_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error Occured while inserting BAFO Document");
			throw loExp;
		}
		/**
		 * Any Exception from DAO class will be handles over here. It throws
		 * Application Exception back to Controllers calling method through
		 * Transaction framework
		 */
		catch (Exception loExp)
		{
			setMoState("Error Occured while inserting BAFO Document");
			throw new ApplicationException("Error Occured while inserting BAFO Document", loExp);
		}
		return loRowsUpdated;
	}

	/**
	 * This method will delete the selected document from the proposal document
	 * table
	 * <ul>
	 * <li>Get proposal id and procurement document id from the request
	 * parameter</li>
	 * <li>Execute query id <b>removeProposalDocs</b> from proposal mapper</li>
	 * <li>Throws application Exception if any exception occured</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession valid sql session
	 * @param aoParamMap parameter map with all details
	 * @param loProposalStatusFlag proposal status flag depending upon the
	 *            status of the proposal
	 * @return loRowsDeleted Integer count of the deleted rows
	 * @throws ApplicationException throws application exception
	 */
	public Integer removeProposalDocs(SqlSession aoMybatisSession, Map<String, Object> aoParamMap,
			Boolean loProposalStatusFlag) throws ApplicationException
	{
		Integer loRowsDeleted = null;
		try
		{
			if (loProposalStatusFlag)
			{
				loRowsDeleted = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoParamMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.REMOVE_PROPOSAL_DOCS,
						HHSConstants.JAVA_UTIL_MAP);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error Occured while deleting proposal document list :");
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error Occured while deleting proposal document list :");
			throw new ApplicationException("Error Occured while deleting proposal document list ", loExp);
		}
		return loRowsDeleted;
	}

	/**
	 * This method fetches proposal task details for input proposal Id from task
	 * detail map
	 * <ul>
	 * <li>Check if map is not null and contains proposal Id</li>
	 * <li>Execute the query "fetchProposalTaskDetails" for the input proposal
	 * Id</li>
	 * <li>Return the fetched list</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoTaskDetailMap - a map containing task and procurement properties
	 * @param asWobNumber - a string value of work flow id
	 * @return map containing proposal task details
	 * @throws ApplicationException If an Application exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Map<String, String> fetchProposalDetailsForTask(SqlSession aoMyBatisSession,
			HashMap<String, Object> aoTaskDetailMap, String asWobNumber) throws ApplicationException
	{
		Map<String, String> loProposalMap = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(ApplicationConstants.WOB_NUMBER, asWobNumber);
		LOG_OBJECT.Debug("Entered into fetchProposalTaskDetails for wob number:" + loContextDataMap.toString());
		try
		{
			// get task details map and check for null value
			if (null != aoTaskDetailMap)
			{
				HashMap<String, Object> loProposalTaskMap = (HashMap<String, Object>) aoTaskDetailMap.get(asWobNumber);
				if (null != loProposalTaskMap)
				{
					// get the proposal Id from task details map
					String lsProposalId = (String) loProposalTaskMap.get(P8Constants.PE_WORKFLOW_PROPOSAL_ID);
					loProposalMap = (Map<String, String>) DAOUtil.masterDAO(aoMyBatisSession, lsProposalId,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROPOSAL_TASK_DETAILS,
							HHSConstants.JAVA_LANG_STRING);
					setMoState("Proposal Task Details fetched successfully for ProposalId:" + lsProposalId);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(aoTaskDetailMap);
			LOG_OBJECT.Error("Error occurred while fetching proposal task details for input:", aoAppEx);
			setMoState("Error occurred while fetching proposal task details for input:" + aoTaskDetailMap.toString());
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while fetching proposal task details for input:",
					aoTaskDetailMap.toString());
			setMoState("Error occurred while fetching proposal task details for input:" + aoTaskDetailMap.toString());
			throw new ApplicationException("Error occurred while fetching proposal task details", aoEx);
		}
		LOG_OBJECT.Debug("Exited fetchProposalTaskDetails for wob number:" + loContextDataMap.toString());
		return loProposalMap;
	}

	/**
	 * This method fetches proposal task details for input proposal Id from task
	 * detail map
	 * <ul>
	 * <li>Check if map is not null and contains proposal Id</li>
	 * <li>Execute the query "fetchRequiredQuestionDocumentCount" for the input
	 * proposal Id</li>
	 * <li>Return the fetched list</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoTaskDetailMap - a map containing task and procurement properties
	 * @param asWobNumber - a string value of work flow id
	 * @return map containing proposal task details
	 * @throws ApplicationException If an Application exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Integer fetchRequiredQuestionDocumentCount(SqlSession aoMyBatisSession,
			HashMap<String, Object> aoTaskDetailMap, String asWobNumber) throws ApplicationException
	{
		Integer loRequiredQuestionDocument = HHSConstants.INT_ZERO;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(ApplicationConstants.WOB_NUMBER, asWobNumber);
		LOG_OBJECT.Debug("Entered into fetchRequiredQuestionDocumentCount for wob number:"
				+ loContextDataMap.toString());
		try
		{
			// get task details map and check for null value
			if (null != aoTaskDetailMap)
			{
				HashMap<String, Object> loProposalTaskMap = (HashMap<String, Object>) aoTaskDetailMap.get(asWobNumber);
				if (null != loProposalTaskMap)
				{
					// get the proposal Id from task details map
					String lsProposalId = (String) loProposalTaskMap.get(P8Constants.PE_WORKFLOW_PROPOSAL_ID);
					loRequiredQuestionDocument = (Integer) DAOUtil.masterDAO(aoMyBatisSession, lsProposalId,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
							HHSConstants.FETCH_REQUIRED_QUESTION_DOCUMENT_COUNT, HHSConstants.JAVA_LANG_STRING);
					setMoState("Proposal Task Details fetched successfully for ProposalId:" + lsProposalId);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(aoTaskDetailMap);
			LOG_OBJECT.Error("Error occurred while fetching proposal task details for input:", aoAppEx);
			setMoState("Error occurred while fetching proposal task details for input:" + aoTaskDetailMap.toString());
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while fetching proposal task details for input:",
					aoTaskDetailMap.toString());
			setMoState("Error occurred while fetching proposal task details for input:" + aoTaskDetailMap.toString());
			throw new ApplicationException("Error occurred while fetching proposal task details", aoEx);
		}
		LOG_OBJECT.Debug("Exited fetchProposalTaskDetails for wob number:" + loContextDataMap.toString());
		return loRequiredQuestionDocument;
	}

	/**
	 * <p>
	 * This method fetches Proposal Site Details corresponding to a proposal Id
	 * and user type
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Fetch list of Proposal Site Details for the provided Proposal Id,
	 * user type using <b>fetchProposalSiteDetails</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession - mybatis SQL session
	 * @param asProposalId - Proposal Id
	 * @param asUserType - User Type
	 * @param asSortSiteTable - string variable containing value whether or not
	 *            to sort the site details table
	 * @return loSiteDetailList - list of type SiteDetailsBean containing site
	 *         details
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<SiteDetailsBean> getProposalSiteDetails(SqlSession aoMybatisSession, String asProposalId,
			String asUserType, String asSortSiteTable) throws ApplicationException
	{
		List<SiteDetailsBean> loSiteDetailList = null;
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
		loContextDataMap.put(HHSConstants.USER_TYPE, asUserType);
		loContextDataMap.put(HHSConstants.AS_SORT_SITE_TABLE, asSortSiteTable);
		try
		{
			// checking if proposal Id is null or not
			if (asProposalId != null)
			{
				Map<String, String> loMap = new HashMap<String, String>();
				loMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
				loMap.put(HHSConstants.AS_USER_TYPE, asUserType);
				loMap.put(HHSConstants.AS_SORT_SITE_TABLE, asSortSiteTable);
				loSiteDetailList = (List<SiteDetailsBean>) DAOUtil.masterDAO(aoMybatisSession, loMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROPOSAL_SITE_DETAILS,
						HHSConstants.JAVA_UTIL_MAP);
			}
			else
			{
				throw new ApplicationException("Proposal Id cannot be null while fetching the proposal site details");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching proposal site details for user Type :", aoAppEx);
			setMoState("Error while fetching proposal site details for user Type:" + asUserType);
			throw aoAppEx;
		}
		// Handling Exception other than ApplicationException
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching proposal site details for user Type :", aoAppEx);
			setMoState("Error while fetching proposal site details for user Type:" + asUserType);
			throw new ApplicationException("Error while fetching proposal site details for user Type :", aoAppEx);
		}
		setMoState("Successfully fetched proposal site details for user Type:" + asUserType);
		return loSiteDetailList;
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 This method fetches proposal documents corresponding to the proposal
	 * Id
	 * <ul>
	 * <li>1. Retrieve proposal Id from the Channel object</li>
	 * <li>2. Execute the query "getProposalDocuments" to fetch proposal
	 * documents from proposal_document table corresponding to the proposal Id</li>
	 * <li>3. Return the fetched list.</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProposalId - string representation of proposal id
	 * @param asProcurementId - String Procurement ID
	 * @return List list of documents
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ExtendedDocument> getProposalDocuments(SqlSession aoMyBatisSession, String asProposalId,
			String asProcurementId) throws ApplicationException
	{
		List<ExtendedDocument> loDocumentList = null;
		Integer loVersionNo = null;
		if (asProposalId != null)
		{
			Map<String, String> loMap = new HashMap<String, String>();
			loMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
			loMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
			try
			{
				loVersionNo = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_VERSION_NO_FOR_PROPOSAL_DOC,
						HHSConstants.JAVA_UTIL_MAP);
				loMap.put(HHSConstants.DOC_VERSION_NO, loVersionNo.toString());
				loDocumentList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMyBatisSession, loMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_PROPOSAL_DOCUMENTS,
						HHSConstants.JAVA_UTIL_MAP);
				setMoState("Proposal Documents fetched successfully corresponding to the proposal Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT.Error(
						"Exception occured while fetching proposal documents corresponding to the proposal Id ",
						aoAppEx);
				setMoState("Transaction Failed:: ProposalService:getProposalDocuments method - while fetching proposal documents corresponding to the proposal Id");
				throw aoAppEx;
			}
			// Handling Exception other than ApplicationException
			catch (Exception aoAppEx)
			{
				LOG_OBJECT.Error(
						"Exception occured while fetching proposal documents corresponding to the proposal Id ",
						aoAppEx);
				setMoState("Transaction Failed:: ProposalService:getProposalDocuments method - while fetching proposal documents corresponding to the proposal Id");
				throw new ApplicationException(
						"Exception occured while fetching proposal documents corresponding to the proposal Id ",
						aoAppEx);
			}
		}

		return loDocumentList;
	}

	/**
	 * This method fetches the procurement title corresponding to the
	 * procurement Id
	 * <ul>
	 * <li>1. Retrieve procurement Id from the Channel object</li>
	 * <li>2. Execute the query "fetchProcurementTitle" to fetch procurement
	 * title from procurement table corresponding to the procurement Id</li>
	 * <li>3. Return the fetched procurement title.</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProcurementId - string representation of procurement id
	 * @return - string representation of procurement title
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public String fetchProcurementTitle(SqlSession aoMyBatisSession, String asProcurementId)
			throws ApplicationException
	{
		String lsProcurementTitle = null;

		// checking if procurement Id is not null
		if (asProcurementId != null)
		{
			try
			{
				lsProcurementTitle = (String) DAOUtil.masterDAO(aoMyBatisSession, asProcurementId,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROCUREMENT_TITLE,
						HHSConstants.JAVA_LANG_STRING);
				setMoState("Procurement title fetched successfully corresponding to the procurement Id");
			}
			/**
			 * Any Exception from DAO class will be thrown as Application
			 * Exception which will be handles over here. It throws Application
			 * Exception back to Controllers calling method through Transaction
			 * framework
			 */
			catch (ApplicationException aoAppEx)
			{
				LOG_OBJECT.Error(
						"Exception occured while fetching procurement title corresponding to the procurement Id ",
						aoAppEx);
				setMoState("Transaction Failed:: ProposalService:fetchProcurementTitle method - while fetching procurement title corresponding to the procurement Id");
				throw aoAppEx;
			}
			// Handling Exception other than ApplicationException
			catch (Exception aoAppEx)
			{
				LOG_OBJECT.Error(
						"Exception occured while fetching procurement title corresponding to the procurement Id ",
						aoAppEx);
				setMoState("Transaction Failed:: ProposalService:fetchProcurementTitle method - while fetching procurement title corresponding to the procurement Id");
				throw new ApplicationException(
						"Exception occured while fetching proposal documents corresponding to the proposal Id ",
						aoAppEx);
			}
		}
		return lsProcurementTitle;
	}

	/**
	 * Modified as part of Release 3.1.0 for enhancement 6024 Changed method -
	 * By: Siddharth Bhola Reason: Build: 2.6.0 Enhancement id: 5667, setting
	 * doc delete flag to 0 in ExtendedDocument bean. This flag 1 means soft
	 * deleted records and 0 are the rest of records
	 * 
	 * This method fetches RFP document list based on procurement Id from task
	 * map
	 * 
	 * <ul>
	 * <li>Get the input procurement ID from task details map</li>
	 * <li>Execute query with Id <b>fetchRfpDocsDetailsForTasks</b> to get
	 * document list</li>
	 * <li>Return the list to controller</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoDocumentBean a document bean object
	 * @param aoTaskMap a map containing task and procurement details
	 * @param asWobNumber a string value of wob number
	 * @return a list of rfp documents of type ExtendedDocument
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<ExtendedDocument> fetchRFPDocListForTask(SqlSession aoMybatisSession, ExtendedDocument aoDocumentBean,
			HashMap<String, Object> aoTaskMap, String asWobNumber) throws ApplicationException
	{
		List<ExtendedDocument> loRfpDocumentsBeans = null;
		HashMap<String, String> loContextDataMap = new HashMap<String, String>();
		loContextDataMap.put(ApplicationConstants.WOB_NUMBER, asWobNumber);
		LOG_OBJECT.Debug("Entered into fetchRFPDocListForTask for wob number:" + loContextDataMap.toString());
		try
		{
			// get task details map and check for null value
			if (null != aoTaskMap)
			{
				HashMap loProcurementMap = (HashMap) aoTaskMap.get(asWobNumber);
				if (null != loProcurementMap)
				{
					// build 2.6.0, defect id 5653
					aoDocumentBean.setDocDeleteFlag(HHSConstants.ZERO);

					// get the procurement Id from task details map
					aoDocumentBean.setProcurementId((String) loProcurementMap
							.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID));
					aoDocumentBean.setProposalId((String) loProcurementMap.get(P8Constants.PE_WORKFLOW_PROPOSAL_ID));
					loRfpDocumentsBeans = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMybatisSession, aoDocumentBean,
							HHSConstants.MAPPER_CLASS_PROCUREMENT_MAPPER, HHSConstants.FETCH_RFP_DOCS_FOR_TASKS,
							HHSConstants.COM_NYC_HHS_MODEL_EXTENDED_DOC);
					setMoState("RFP Document details fetched successfully for Procurement Id:"
							+ aoDocumentBean.getProcurementId());
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setMoState("Error while getting RFP Document Details");
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while getting RFP Document list :", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error while getting RFP Document Details for input:", aoTaskMap.toString());
			setMoState("Error while getting RFP Document Details for input:" + aoTaskMap.toString());
			throw new ApplicationException("Error while getting RFP Document Details for input", aoEx);
		}
		LOG_OBJECT.Debug("Exited fetchRFPDocListForTask for wob number:" + loContextDataMap.toString());
		return loRfpDocumentsBeans;
	}

	/**
	 * This method updates proposal document status for input proposal Id
	 * 
	 * <ul>
	 * <li>Get the input proposal ID</li>
	 * <li>Execute query with Id "updateProposalDocumentStatusForTask" from
	 * proposal mapper</li>
	 * <li>Return updated status to controller</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession mybatis SQL session
	 * @param aoUpdateProposalStatus a boolean value of proposal update status
	 * @param asProposalId a string value of proposal Id
	 * @return loUpdateStatus - Boolean -- return true if update is successfull
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateProposalDocumentStatusForTask(SqlSession aoMyBatisSession, Boolean aoUpdateProposalStatus,
			String asProposalId) throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		LOG_OBJECT.Debug("Entered into updateProposalDocumentStatusForTask for Proposal Id: " + asProposalId);
		try
		{
			if (aoUpdateProposalStatus)
			{
				HashMap<String, String> loProposalMap = new HashMap<String, String>();
				loProposalMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
				DAOUtil.masterDAO(aoMyBatisSession, loProposalMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
						HHSConstants.UPDATE_PROPOSAL_DOC_STATUS_TASK, HHSConstants.JAVA_UTIL_HASH_MAP);
				loUpdateStatus = true;
				setMoState("Proposal Document status updated successfully for proposal Id: " + asProposalId);
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occurred while updating proposal document status for task", asProposalId);
			setMoState("Error occurred while updating proposal document status for task" + asProposalId);
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while updating proposal document status for task", asProposalId);
			setMoState("Error occurred while updating proposal document status for task" + asProposalId);
			throw new ApplicationException("Error occurred while updating proposal document status for task"
					+ asProposalId, aoEx);
		}
		LOG_OBJECT.Debug("Exited updateProposalDocumentStatusForTask");
		return loUpdateStatus;
	}

	/**
	 * This method fetches procurement title and organization Id for input
	 * proposal ID
	 * 
	 * <ul>
	 * <li>Get the input proposal Id</li>
	 * <li>Execute query with Id "fetchProcTitleAndOrgId" from proposal mapper</li>
	 * <li>Modify notification map with procurement title and organization Id</li>
	 * <li>Return modified notification map</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession mybatis SQL session
	 * @param aoUpdateProposalStatus a boolean value of update proposal status
	 * @param asProposalId a string value of proposal Id
	 * @param aoNotificationMap notification map
	 * @return HashMap containing procurement title and organization details
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@SuppressWarnings("unchecked")
	public HashMap<String, Object> fetchProcTitleAndOrgId(SqlSession aoMyBatisSession, Boolean aoUpdateProposalStatus,
			String asProposalId, HashMap<String, Object> aoNotificationMap) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchProcTitleAndOrgId for proposal Id:" + asProposalId);
		try
		{
			if (aoUpdateProposalStatus)
			{
				// Get Procurement Title and Organization name based on proposal
				// Id
				Map<String, String> loProposalMap = (Map<String, String>) DAOUtil.masterDAO(aoMyBatisSession,
						asProposalId, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROC_TITLE_ORG_ID,
						HHSConstants.JAVA_LANG_STRING);
				if (null != aoNotificationMap && null != loProposalMap)
				{
					// Set Procurement Title and Organization name in
					// notification map
					HashMap<String, String> loRequestMap = (HashMap<String, String>) aoNotificationMap
							.get(TransactionConstants.REQUEST_MAP_PARAMETER_NAME);
					loRequestMap.put(HHSConstants.PROC_TITLE, loProposalMap.get(HHSConstants.PROC_TITLE));
					List<String> loOrgIdList = new ArrayList<String>();
					loOrgIdList.add(loProposalMap.get(HHSConstants.ORGANIZATION_ID_KEY));
					List<String> loNotificationList = (List<String>) aoNotificationMap
							.get(HHSConstants.NOTIFICATION_ALERT_ID);

					for (String loNotificationId : loNotificationList)
					{
						((NotificationDataBean) aoNotificationMap.get(loNotificationId)).setProviderList(loOrgIdList);

					}
					setMoState("Procurement Title and organization Id fetched successfully for proposal Id:"
							+ asProposalId);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("Error occurred while fetching procurement title and organization Id based on input proposal Id:"
							+ asProposalId);
			setMoState("Error occurred while fetching procurement title and organization Id based on input proposal Id:"
					+ asProposalId);
			throw aoAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown will be handles over
		 * here. It throws Application Exception back to Controllers calling
		 * method through Transaction framework
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT
					.Error("Error occurred while fetching procurement title and organization Id based on input proposal Id:"
							+ asProposalId);
			setMoState("Error occurred while fetching procurement title and organization Id based on input proposal Id:"
					+ asProposalId);
			throw new ApplicationException(
					"Error occurred while fetching procurement title and organization Id based on input proposal Id:"
							+ asProposalId, aoEx);
		}
		LOG_OBJECT.Debug("Exited fetchProcTitleAndOrgId for proposal Id:" + asProposalId);
		return aoNotificationMap;
	}

	/**
	 * This method is used to populate the Notification map for NT225 and AL224
	 * Added as a part of release 3.11.0 for enhancement request 5978
	 * @param aoMyBatisSession SqlSession
	 * @param aoUpdateProposalStatus Boolean
	 * @param asProposalId String
	 * @param aoNotificationMap HashMap<String, Object>
	 * @return aoNotificationMap HashMap<String, Object>
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> populateNotificationMap(SqlSession aoMyBatisSession, Boolean aoUpdateProposalStatus,
			String asProposalId, HashMap<String, Object> aoNotificationMap) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into populateNotificationMap for proposal Id:" + asProposalId);
		List<String> loProviderIdList = new ArrayList<String>();
		List<String> loAgencyIdList = new ArrayList<String>();
		try
		{
			if (aoUpdateProposalStatus)
			{
				HashMap<String, String> loModifiedInfoMap = (HashMap<String, String>) DAOUtil.masterDAO(
						aoMyBatisSession, asProposalId, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.FETCH_INFO_FOR_RETURNED_PROP_NOTIFICATN, HHSConstants.JAVA_LANG_STRING);
				HashMap<String, String> loLastModifiedHashMap = (HashMap<String, String>) DAOUtil.masterDAO(
						aoMyBatisSession, asProposalId, HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER,
						HHSConstants.FETCH_LAST_COMMENT, HHSConstants.JAVA_LANG_STRING);
				if (null != aoNotificationMap && null != loModifiedInfoMap && null != loLastModifiedHashMap)
				{
					HashMap<String, String> loRequestMap = (HashMap<String, String>) aoNotificationMap
							.get(TransactionConstants.REQUEST_MAP_PARAMETER_NAME);

					loRequestMap.put(HHSConstants.NT_PROCUREMENT_TITLE,
							loModifiedInfoMap.get(HHSConstants.NT_PROCUREMENT_TITLE));
					loRequestMap.put(HHSConstants.PROVIDER_NAME, loModifiedInfoMap.get(HHSConstants.ORGANIZATION_NAME));
					loRequestMap.put(HHSConstants.AGENCY_NAME_COLUMN,
							loLastModifiedHashMap.get(HHSConstants.ORGANIZATION_NAME_VAL));
					loRequestMap.put(HHSConstants.PARAM_USER_COMMENT_TABLE_PROVIDER_COMMENTS,
							loLastModifiedHashMap.get(HHSConstants.PARAM_USER_COMMENT_TABLE_PROVIDER_COMMENTS));

					loProviderIdList.add(loModifiedInfoMap.get(HHSConstants.ORGANIZATION_ID_KEY));
					loAgencyIdList.add(loLastModifiedHashMap.get(HHSConstants.ORGANIZATION_NAME_VAL));
					List<String> loNotificationList = (List<String>) aoNotificationMap
							.get(HHSConstants.NOTIFICATION_ALERT_ID);

					for (String loNotificationId : loNotificationList)
					{
						if (null != loProviderIdList && !loProviderIdList.isEmpty())
						{
							((NotificationDataBean) aoNotificationMap.get(loNotificationId))
									.setProviderList(loProviderIdList);
						}
						if (null != loAgencyIdList && !loAgencyIdList.isEmpty())
						{
							((NotificationDataBean) aoNotificationMap.get(loNotificationId))
									.setAgencyList(loAgencyIdList);
						}

					}
					setMoState("populateNotificationMap executed successfully for proposal Id:" + asProposalId);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occurred while executing populateNotificationMap based on input proposal Id:"
					+ asProposalId);
			setMoState("Error occurred while executing populateNotificationMap based on input proposal Id:"
					+ asProposalId);
			throw aoAppEx;
		}
		/**
		 * Any Exception from DAO class will be thrown will be handles over
		 * here. It throws Application Exception back to Controllers calling
		 * method through Transaction framework
		 */
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while executing populateNotificationMap based on input proposal Id:"
					+ asProposalId);
			setMoState("Error occurred while executing populateNotificationMap based on input proposal Id:"
					+ asProposalId);
			throw new ApplicationException(
					"Error occurred while executing populateNotificationMap based on input proposal Id:" + asProposalId,
					aoEx);
		}
		LOG_OBJECT.Debug("Exited populateNotificationMap for proposal Id:" + asProposalId);
		return aoNotificationMap;
	}

	/**
	 * Below method is used to add the procurement and provider status into the
	 * document bean
	 * <ul>
	 * <li>get all the required parameters from the request variables</li>
	 * <li>Iterate through the <code>aoProposalDocBeanList</code></li>
	 * <li>set procurement status and proposal status in the document bean</li>
	 * </ul>
	 * @param aoProposalMap proposal details map
	 * @param asProcurementStatus procurement status
	 * @param aoProposalDocBeanList list of all proposal documents
	 * @return List of type ExtendedDocument -- list of proposal document
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public List<ExtendedDocument> addProposalAndProcurementStatus(Map<String, String> aoProposalMap,
			String asProcurementStatus, List<ExtendedDocument> aoProposalDocBeanList) throws ApplicationException
	{
		ExtendedDocument loProposalExtendedDocument = null;
		try
		{
			if (null != aoProposalDocBeanList && null != aoProposalMap)
			{
				for (Iterator<ExtendedDocument> loExtendedDocItr = aoProposalDocBeanList.iterator(); loExtendedDocItr
						.hasNext();)
				{
					loProposalExtendedDocument = loExtendedDocItr.next();
					loProposalExtendedDocument.setProcurementStatusId(asProcurementStatus);
					loProposalExtendedDocument.setProposalStatusId(String.valueOf(aoProposalMap
							.get(HHSConstants.PROPOSAL_STATUS_ID)));
				}
			}
		}
		// catch any application exception thrown from the code and throw it
		// forward
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("error while fetching proposal document list", loEx);
			setMoState("error while fetching proposal document list");
			throw new ApplicationException("error while fetching proposal document list", loEx);
		}
		return aoProposalDocBeanList;
	}

	/**
	 * This method updates the status of approved provider based on current
	 * status of proposals
	 * <ul>
	 * <li>1. Get the count of draft, submitted proposal and procurement status
	 * using <b>getApprovedProviderDetailForProposal</b> query</li>
	 * <li>2. Check if the query returns any data, if yes send the data to
	 * <b>updateApprovedProviderStatus</b> query</li>
	 * </ul>
	 * @param aoMyBatisSession mybatis SQL session
	 * @param asProposalId a string value of proposal Id
	 * @param asProcurementId procurement id
	 * @param asOrganizationId organization id
	 * @param aoUpdateStatusFlag status depicting if proposal can be changed or
	 *            not
	 * @return Boolean if updated success fully
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateApprovedProviderStatus(SqlSession aoMyBatisSession, String asProposalId,
			String asProcurementId, String asOrganizationId, Boolean aoUpdateStatusFlag) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into updateApprovedProviderStatus for proposal Id:" + asProposalId);
		Map<String, String> loDataMap = new HashMap<String, String>();
		try
		{
			// checking if boolean update status flag is not null and is true
			if (aoUpdateStatusFlag == null || aoUpdateStatusFlag)
			{
				loDataMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
				loDataMap.put(HHSConstants.PROCUREMENT_ID, asProcurementId);
				loDataMap.put(HHSConstants.ORGANIZATION_ID, asOrganizationId);
				loDataMap.put(HHSConstants.STATUS_PROPOSAL_DRAFT, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_DRAFT));
				loDataMap.put(HHSConstants.STATUS_PROPOSAL_SUBMITTED, PropertyLoader.getProperty(
						HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_SUBMITTED));
				Map<String, String> loProviderDetail = (Map<String, String>) DAOUtil.masterDAO(aoMyBatisSession,
						loDataMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
						HHSConstants.GET_APP_PROV_DETAIL_PROPOSAL_QUERY, HHSConstants.JAVA_UTIL_MAP);

				// checking if fetched provider details map is not null and has
				// size greater than zero
				if (loProviderDetail != null && loProviderDetail.size() > HHSConstants.INT_ZERO)
				{
					loDataMap.putAll(loProviderDetail);
					DAOUtil.masterDAO(aoMyBatisSession, loDataMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
							HHSConstants.UPDATE_APP_PROV_STATUS_QUERY, HHSConstants.JAVA_UTIL_MAP);
				}
				return true;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.setContextData(loDataMap);
			setMoState("Error occurred while updating approved provider status for procurement id :" + asProcurementId);
			LOG_OBJECT.Error("Error occurred while updating approved provider status ", loExp);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error occurred while updating approved provider status for procurement id :" + asProcurementId);
			LOG_OBJECT.Error("Error occurred while updating approved provider status ", loExp);
			throw new ApplicationException("Error occurred while updating approved provider status", loExp);
		}
		return false;
	}

	/**
	 * This method is used to set the document status to completed if it is
	 * returned for revision
	 * <ul>
	 * <li>Get the document id from the parameters execute
	 * <code>getProposalDocumentStatus</code> query of
	 * <code>proposal mapper</code></li>
	 * <li>Get the status id of document returned for revision from status
	 * property file</li>
	 * <li>If the status of document matched with the status of document
	 * returned for revision</li>
	 * <li>execute <code></code></li>
	 * </ul>
	 * @param aoMyBatisSession mybatis sql session
	 * @param asDocumentId document id
	 * @param asProposalId proposal ID
	 * @return Boolean -- returns true if document properties updated success
	 *         fully
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateProposalDocumentProperties(SqlSession aoMyBatisSession, String asDocumentId,
			String asProposalId) throws ApplicationException
	{
		Boolean loDocStatusUpdated = Boolean.FALSE;
		List<String> loDocumentStatusList = null;
		String lsDocumentReturnedStatus = null;
		int liRowsUpdated = HHSConstants.INT_ZERO;
		Map<String, String> loParamMap = new HashMap<String, String>();
		try
		{
			loParamMap.put(HHSConstants.DOCUMENT_ID, asDocumentId);
			loParamMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
			lsDocumentReturnedStatus = PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
					HHSConstants.DOCUMENT_RETURNED_KEY);
			loDocumentStatusList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, loParamMap,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_PROPOSAL_DOCUMENT_STATUS_QUERY,
					HHSConstants.JAVA_UTIL_MAP);
			if (null != loDocumentStatusList && loDocumentStatusList.contains(lsDocumentReturnedStatus))
			{
				liRowsUpdated = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loParamMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.UPDATE_PROPOSAL_DOCUMENT_STATUS_QUERY,
						HHSConstants.JAVA_UTIL_MAP);
				if (liRowsUpdated > HHSConstants.INT_ZERO)
				{
					loDocStatusUpdated = Boolean.TRUE;
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error occurred while updating approved provider status ", loExp);
			setMoState("Error occurred while updating document Status for document Id :" + asDocumentId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while updating approved provider status ", loExp);
			setMoState("Error occurred while updating document Status for document Id :" + asDocumentId);
			throw new ApplicationException("Error occurred while updating approved provider status", loExp);
		}
		return loDocStatusUpdated;
	}

	/**
	 * Below method will get the organization id for the selected proposal
	 * <ul>
	 * <li>Get the parameters from the channel object</li>
	 * <li>Execute query <code>getOrgIdsForSelectedProposals</code> of Proposal
	 * Mapper</li>
	 * </ul>
	 * @param aoMyBatisSession mybatis sql session object
	 * @param aoProposalMap proposal details map
	 * @param aoTaskPropsMap task properties details map
	 * @param aoIsFirstLaunch boolean is launched first time
	 * @return HashMap list of providers
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public HashMap getOrgIdsForSelectedProposals(SqlSession aoMyBatisSession, HashMap<String, String> aoProposalMap,
			HashMap aoTaskPropsMap, Boolean aoIsFirstLaunch) throws ApplicationException
	{
		try
		{
			if (aoIsFirstLaunch)
			{
				List<String> loOrgIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, aoProposalMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_ORG_IDS_FOR_SELECTED_PROPOSAL,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				if (null != loOrgIdList && loOrgIdList.size() > HHSConstants.INT_ZERO)
				{
					aoTaskPropsMap = new HashMap();
					aoTaskPropsMap.put(HHSConstants.LIST_OF_PROVIDERS, loOrgIdList);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			loExp.setContextData(aoProposalMap);
			setMoState("Error occurred while updating document Status for document Id :" + aoProposalMap.toString());
			LOG_OBJECT.Error("Error occurred while updating approved provider status ", loExp);
			throw loExp;
		}
		catch (Exception loExp)
		{
			setMoState("Error occurred while updating document Status for document Id :" + aoProposalMap.toString());
			LOG_OBJECT.Error("Error occurred while updating approved provider status ", loExp);
			throw new ApplicationException("Error occurred while updating approved provider status ", loExp);
		}
		return aoTaskPropsMap;
	}

	/**
	 * This method will update the proposal status from the work flow task
	 * screen
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get the parameters from the channel object</li>
	 * <li>Check if Evaluation Pool mapping id is available or not</li>
	 * <li>Execute query <code>updatePropStatFromTaskForPoolId</code> if
	 * evaluation pool mapping id is available</li>
	 * <li>Execute query <code>updateProposalStatusFromTask</code> if evaluation
	 * pool mapping id is not available</li>
	 * </ul>
	 * 
	 * Change: For open ended RFP, in case task doesnot have evaluation pool
	 * mapping id Changed by: pallavi
	 * 
	 * @param aoMyBatisSession mybatis sql session
	 * @param asProcurementId procurement id
	 * @param aoIsSecondFlag - Status flag from previous service
	 * @param aoIsProposalNotSelected - flag depicting propsal is selected or
	 *            not
	 * @param asTaskStatus - Current status of task
	 * @param asEvaluationPoolMappingId - Evaluation Mapping Id
	 * @return loUpdateStatus boolean -- returns true if update is successful
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean updateProposalStatusFromTask(SqlSession aoMyBatisSession, String asProcurementId,
			Boolean aoIsSecondFlag, Boolean aoIsProposalNotSelected, String asTaskStatus,
			String asEvaluationPoolMappingId) throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		try
		{
			if (null != asTaskStatus && !asTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED)
					&& (aoIsSecondFlag || (null != aoIsProposalNotSelected && aoIsProposalNotSelected)))
			{
				if (null != asEvaluationPoolMappingId
						&& !asEvaluationPoolMappingId.equalsIgnoreCase(HHSConstants.EMPTY_STRING))
				{
					DAOUtil.masterDAO(aoMyBatisSession, asEvaluationPoolMappingId,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
							HHSConstants.UPDATE_PROP_STAT_FRM_TASK_FOR_POOL_ID, HHSConstants.JAVA_LANG_STRING);
				}
				else
				{
					DAOUtil.masterDAO(aoMyBatisSession, asProcurementId, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
							HHSConstants.UPDATE_PROPOSAL_STATUS_FROM_TASK, HHSConstants.JAVA_LANG_STRING);
				}
				loUpdateStatus = true;
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			LOG_OBJECT.Error("Error occurred while updating approved provider status ", loExp);
			setMoState("Error occurred while updating document Status for document Id :" + asProcurementId);
			throw loExp;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error occurred while updating approved provider status ", loExp);
			setMoState("Error occurred while updating document Status for document Id :" + asProcurementId);
			throw new ApplicationException("Error occurred while updating approved provider status ", loExp);
		}
		return loUpdateStatus;
	}

	/**
	 * This method updates the Proposal status from task.
	 * 
	 * <ul>
	 * <li>Taking asProposalID and asProposalStatus as input.</li>
	 * <li>Execute the query with id "updateProposalPreviousStatus" from
	 * proposal mapper</li>
	 * <li>Return update status.</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProposalId - a string value of proposal Id
	 * @param asProposalStatus a string value of proposal status
	 * @return Boolean update status -- returns true if update is successful
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateProposalPreviousStatus(SqlSession aoMyBatisSession, String asProposalId,
			String asProposalStatus) throws ApplicationException
	{
		Boolean loUpdateStatus = false;
		LOG_OBJECT.Debug("Entered into updateProposalPreviousStatus for proposal Id:" + asProposalId);
		try
		{
			HashMap<String, String> loPropMap = new HashMap<String, String>();
			loPropMap.put(HHSConstants.STATUS_ID, asProposalStatus);
			loPropMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
			DAOUtil.masterDAO(aoMyBatisSession, loPropMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
					HHSConstants.UPDATE_PROPOSAL_PREVIOUS_STATUS, HHSConstants.JAVA_UTIL_HASH_MAP);
			loUpdateStatus = true;
			setMoState("Proposal Status updated successfully for proposal Id:" + asProposalId);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occurred while updating proposal status for proposal Id:", asProposalId);
			setMoState("Error occurred while updating proposal status for proposal Id:" + asProposalId);
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while updating proposal status for proposal Id:", asProposalId);
			setMoState("Error occurred while updating proposal status for proposal Id:" + asProposalId);
			throw new ApplicationException("Error occurred while updating proposal status for proposal Id:"
					+ asProposalId, aoEx);
		}
		LOG_OBJECT.Debug("Exited updateProposalPreviousStatus for proposal Id:" + asProposalId);
		return loUpdateStatus;
	}

	/**
	 * This method updates the Proposal status from task. Changes done for
	 * Enhancement #6577 for Release 3.10.0
	 * <ul>
	 * <li>Taking asProposalID and asProposalStatus as input.</li>
	 * <li>Execute the query with id "updateProposalPreviousStatus" from
	 * proposal mapper</li>
	 * <li>Return update status.</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProposalId - a string value of proposal Id
	 * @param asProposalStatus a string value of proposal status
	 * @return Boolean update status -- returns true if update is successful
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public Boolean updateStatusForCancelComp(SqlSession aoMyBatisSession, HashMap<String, String> loStatusInfoMap,
			String aoCancelFlag) throws ApplicationException
	{
		Boolean loFinalUpdateStatus = false;
		Map<String, String> loCompPoolInfoMap = new HashMap<String, String>();
		LOG_OBJECT.Debug("Entered into updateProposalStatus");
		loStatusInfoMap.put(HHSConstants.PROP_STATUS_ID, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROPOSAL_CANCELLED));
		loStatusInfoMap.put(HHSConstants.COMP_POOL_STATUS_ID, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_COMPETITION_POOL_CANCELLED));
		loStatusInfoMap.put(HHSConstants.EVAL_GROUP_STATUS_ID, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_EVALUATION_GROUP_CANCELLED));
		try
		{
			if (null != aoCancelFlag && aoCancelFlag.equalsIgnoreCase(HHSConstants.STRING_TRUE))
			{
				DAOUtil.masterDAO(aoMyBatisSession, loStatusInfoMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
						HHSConstants.UPDATE_PROPOSAL_STATUS_FOR_CANCEL_COMP, HHSConstants.JAVA_UTIL_HASH_MAP);
				DAOUtil.masterDAO(aoMyBatisSession, loStatusInfoMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
						HHSConstants.UPDATE_COMP_POOL_CANCELLED, HHSConstants.JAVA_UTIL_HASH_MAP);
				loCompPoolInfoMap = (HashMap<String, String>) DAOUtil.masterDAO(aoMyBatisSession, loStatusInfoMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_COMP_POOL_INFO,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				if (null != loCompPoolInfoMap && null != loCompPoolInfoMap.get(HHSConstants.TOTAL_COMPS)
						&& null != loCompPoolInfoMap.get(HHSConstants.CANCELLED_COMPS)
						&& null != loCompPoolInfoMap.get(HHSConstants.CANC_NOPROPOSALS_COMPS))
				{
					if ((String.valueOf(loCompPoolInfoMap.get(HHSConstants.TOTAL_COMPS))).equalsIgnoreCase(String
							.valueOf(loCompPoolInfoMap.get(HHSConstants.CANC_NOPROPOSALS_COMPS)))
							&& !(String.valueOf(loCompPoolInfoMap.get(HHSConstants.CANCELLED_COMPS)))
									.equalsIgnoreCase(HHSConstants.STRING_ZERO))
					{
						DAOUtil.masterDAO(aoMyBatisSession, loStatusInfoMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
								HHSConstants.UPDATE_EVAL_GROUP_CANCELLED, HHSConstants.JAVA_UTIL_HASH_MAP);
					}
				}
				loFinalUpdateStatus = true;
				setMoState("Proposal Status updated successfully");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occurred while updating proposal status");
			setMoState("Error occurred while updating proposal status");
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while updating proposal status");
			setMoState("Error occurred while updating proposal status");
			throw new ApplicationException("Error occurred while updating proposal status", aoEx);
		}
		LOG_OBJECT.Debug("Exited updateProposalStatus");
		return loFinalUpdateStatus;
	}

	/**
	 * This method updates the Proposal status from task. Changes done for
	 * Enhancement #6577 for Release 3.10.0
	 * <ul>
	 * <li>Taking asProposalID and asProposalStatus as input.</li>
	 * <li>Execute the query with id "updateProposalPreviousStatus" from
	 * proposal mapper</li>
	 * <li>Return update status.</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param asProposalId - a string value of proposal Id
	 * @param asProposalStatus a string value of proposal status
	 * @return Boolean update status -- returns true if update is successful
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public Boolean updateStatusForContractBudget(SqlSession aoMyBatisSession, HashMap<String, String> loStatusInfoMap,
			String aoCancelFlag) throws ApplicationException
	{
		Boolean loFinalUpdateStatus = false;
		LOG_OBJECT.Debug("Entered into updateStatusForContractBudget");
		loStatusInfoMap.put(HHSConstants.CONTRACT_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_CONTRACT_CANCELLED));
		loStatusInfoMap.put(HHSConstants.BUDGET_STATUS, PropertyLoader.getProperty(
				HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_BUDGET_CANCELLED));
		try
		{
			if (null != aoCancelFlag && aoCancelFlag.equalsIgnoreCase(HHSConstants.STRING_TRUE))
			{
				DAOUtil.masterDAO(aoMyBatisSession, loStatusInfoMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
						HHSConstants.UPDATE_CANCELLED_BUDGET, HHSConstants.JAVA_UTIL_HASH_MAP);
				DAOUtil.masterDAO(aoMyBatisSession, loStatusInfoMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
						HHSConstants.UPDATE_CANCELLED_CONTRACT, HHSConstants.JAVA_UTIL_HASH_MAP);
				loFinalUpdateStatus = true;
				setMoState("updateStatusForContractBudget successful");
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occurred while updateStatusForContractBudget");
			setMoState("Error occurred while updateStatusForContractBudget");
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while updateStatusForContractBudget");
			setMoState("Error occurred while updateStatusForContractBudget");
			throw new ApplicationException("Error occurred while updateStatusForContractBudget", aoEx);
		}
		LOG_OBJECT.Debug("Exited updateStatusForContractBudget");
		return loFinalUpdateStatus;
	}

	/**
	 * This method is used to fetch proposal for
	 * cancellation of competition pool.
	 * @param aoMyBatisSession a SqlSession object
	 * @param loStatusInfoMap a hashmap of string type key, value pair
	 * @return loProposalIdList a list of proposals for cancellation 
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<String> fetchProposalForCancelComp(SqlSession aoMyBatisSession, HashMap<String, String> loStatusInfoMap)
			throws ApplicationException
	{
		List<String> loProposalIdList = null;
		Boolean loFinalUpdateStatus = false;
		LOG_OBJECT.Debug("Entered into fetchProposalForCancelComp");
		try
		{
			loProposalIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, loStatusInfoMap,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PROPOSAL_FOR_CANCEL_COMP,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState("Proposal Ids fetched successfully");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occurred while fetching proposal ids");
			setMoState("Error occurred while fetching proposal status");
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while fetching proposal ids");
			setMoState("Error occurred while fetching proposal ids");
			throw new ApplicationException("Error occurred while fetching proposal ids", aoEx);
		}
		LOG_OBJECT.Debug("Exited fetchProposalForCancelComp");
		return loProposalIdList;
	}

	/**
	 * This method fetches user list, to whom a task can be reassigned based on
	 * user role and user organization
	 * <ul>
	 * <li>Get user id from input task details map</li>
	 * <li>Get agency Id associated with Procurement</li>
	 * <li>Execute the query with Id "fetchPermittedUsers" from proposal mapper</li>
	 * <li>Return the fetched list.</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoUserRoleList - a list of user roles
	 * @param asUserOrg - a string value of user organization
	 * @return list of type UserBean of permitable users
	 * @throws ApplicationException If an application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<UserBean> fetchPermittedUsersForAgencyList(SqlSession aoMyBatisSession, List<String> aoUserRoleList,
			String asUserOrg) throws ApplicationException
	{
		List<UserBean> loUsers = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.AO_USER_MAP, asUserOrg);
		LOG_OBJECT.Debug("Entered into fetchPermittedUsers for userId:" + loContextDataMap.toString());
		try
		{
			HashMap<String, Object> loUserMap = new HashMap<String, Object>();
			loUserMap.put(HHSConstants.ORGID, asUserOrg);
			loUserMap.put(ApplicationConstants.ACCELERATOR_USER_ROLE_LIST, aoUserRoleList);
			loUsers = (List<UserBean>) DAOUtil.masterDAO(aoMyBatisSession, loUserMap,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_PERMITTED_USERS,
					HHSConstants.JAVA_UTIL_HASH_MAP);
			setMoState("User List fethced successfully for user Id:" + loUserMap);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.addContextData("Error occurred while fetching user list for input", aoAppEx);
			setMoState("Error occurred while fetching user list for user Id:" + aoUserRoleList);
			LOG_OBJECT.Error("Error occurred while fetching user list for input:", aoUserRoleList.toString());
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while fetching user list for input:", aoUserRoleList.toString());
			setMoState("Error occurred while fetching user list for input:" + aoUserRoleList.toString());
			throw new ApplicationException("Error occurred while fetching user list for input", aoEx);
		}

		LOG_OBJECT.Debug("Exited fetchPermittedUsers for user Id:" + loContextDataMap.toString());
		return loUsers;
	}

	/**
	 * This method modified as a part of release 3.1.0 for enhancement request
	 * 6024 This method will send the alert and notification when we submit the
	 * proposal
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>Get proposal status id from input task details map</li>
	 * <li>Get procurement review status Id associated with Procurement</li>
	 * <li>Checking for proposal and procurement status id and putting the
	 * alerts and notification values into alert list</li>
	 * <li>Create a local object for Notification map</li>
	 * <li>Create the Notification Alert List that is added to the Notification
	 * Map</li>
	 * <li>Creates the request param map</li>
	 * <li>Create NotificationDataBean type object and set the provider list,
	 * agency list, linkMap and agencyLinkMap attributes</li>
	 * <li>Then add the request map, created by, modified by, Entity ID, Entity
	 * Type,NotificationDataBean to the Notification map</li>
	 * <li>Return the notification map.</li>
	 * </ul>
	 * /**
	 * @param aoStatusMap status map
	 * @param aoProposalUpdateFlag proposal flag
	 * @param aoNotificationMap notification map
	 * @return notification map containing the information about the Event ID's
	 * @throws ApplicationException if Exception occurs
	 */
	@SuppressWarnings("unchecked")
	public HashMap<String, Object> getNotificationMapForSubmitProposal(Map<String, String> aoStatusMap,
			Boolean aoProposalUpdateFlag, HashMap<String, Object> aoNotificationMap) throws ApplicationException
	{
		if (aoProposalUpdateFlag && null != aoStatusMap && null != aoNotificationMap)
		{
			String lsStatusId = String.valueOf(aoStatusMap.get(HHSConstants.PROPOSAL_STATUS_ID));
			String lsProcStatusId = String.valueOf(aoStatusMap.get(HHSConstants.PROCUREMENT_STATUS_ID));
			String lsProcReviewStatusId = String.valueOf(aoStatusMap.get(HHSConstants.PROC_REVIEW_STATUS_ID));
			String lsEvalGroupId = String.valueOf(aoStatusMap.get(HHSConstants.EVALUATION_GROUP_ID_COL));
			HashMap<String, String> loAgencyLinkMap = new HashMap<String, String>();
			List<String> loAgencyList = new ArrayList<String>();
			String lsOpenEndedRFP = String.valueOf(aoStatusMap.get(HHSConstants.IS_OPEN_ENDED_RFP));
			if (null != lsStatusId
					&& (lsStatusId.equals(PropertyLoader.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
							HHSConstants.STATUS_PROPOSAL_DRAFT)) || lsStatusId.equals(PropertyLoader
							.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_PROPOSAL_RETURNED_FOR_REVISION))))
			{
				List<String> loNotificationAlertList = new ArrayList<String>();
				HashMap<String, String> loRequestMap = (HashMap<String, String>) aoNotificationMap
						.get(TransactionConstants.REQUEST_MAP_PARAMETER_NAME);
				String lsPropAndEvalSummUrl = (String) loRequestMap.get(HHSConstants.PROP_AND_EVAL_SUMM_LINK);

				NotificationDataBean loNotificationDataBean = new NotificationDataBean();
				if (null != lsProcReviewStatusId && !lsProcReviewStatusId.equals(HHSConstants.EMPTY_STRING)
						&& !lsProcReviewStatusId.equalsIgnoreCase(HHSConstants.NULL))
				{

					loNotificationAlertList.add(HHSConstants.AL219);
					loNotificationAlertList.add(HHSConstants.NT214);
					loAgencyLinkMap.put(HHSConstants.AGENCY_LINK, (String) loRequestMap.get(HHSConstants.AGENCY_LINK));
					loNotificationDataBean.setLinkMap(loAgencyLinkMap);
					loNotificationDataBean.setAgencyLinkMap(loAgencyLinkMap);
					loAgencyList.add(aoStatusMap.get(HHSConstants.AGENCY_ID_TABLE_COLUMN));
					loNotificationDataBean.setAgencyList(loAgencyList);
					aoNotificationMap.put(HHSConstants.AL219, loNotificationDataBean);
					aoNotificationMap.put(HHSConstants.NT214, loNotificationDataBean);
				}
				else
				{
					if (null != lsProcStatusId
							&& lsProcStatusId.equals(PropertyLoader.getProperty(
									HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_RELEASED)))
					{
						loNotificationAlertList.add(HHSConstants.AL226);
						loNotificationAlertList.add(HHSConstants.NT227);
						HashMap<String, String> loProviderLinkMap = new HashMap<String, String>();
						loProviderLinkMap.put(HHSConstants.PROVIDER_LINK,
								(String) loRequestMap.get(HHSConstants.PROVIDER_LINK));
						loNotificationDataBean.setLinkMap(loProviderLinkMap);
						loNotificationDataBean.setAgencyLinkMap(loProviderLinkMap);
						List<String> loProviderList = new ArrayList<String>();
						loProviderList.add(aoStatusMap.get(HHSConstants.ORGANIZATION_ID_KEY));
						loNotificationDataBean.setProviderList(loProviderList);
						aoNotificationMap.put(HHSConstants.AL226, loNotificationDataBean);
						aoNotificationMap.put(HHSConstants.NT227, loNotificationDataBean);
					}
				}
				if (null != lsOpenEndedRFP && lsOpenEndedRFP.equalsIgnoreCase(HHSConstants.ONE))
				{
					loNotificationAlertList.add(HHSConstants.NT401);
					NotificationDataBean loNotfctnDataBean = new NotificationDataBean();
					HashMap<String, String> loAgencyLinkMapNew = new HashMap<String, String>();
					List<String> loAgencyListNew = new ArrayList<String>();
					loAgencyLinkMapNew.put(HHSConstants.AGENCY_LINK, lsPropAndEvalSummUrl + lsEvalGroupId);
					loNotfctnDataBean.setLinkMap(loAgencyLinkMapNew);
					loNotfctnDataBean.setAgencyLinkMap(loAgencyLinkMapNew);
					loAgencyListNew.add(aoStatusMap.get(HHSConstants.AGENCY_ID_TABLE_COLUMN));
					loNotfctnDataBean.setAgencyList(loAgencyListNew);
					aoNotificationMap.put(HHSConstants.NT401, loNotfctnDataBean);
				}
				aoNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
				loRequestMap.remove(HHSConstants.AGENCY_LINK);
				loRequestMap.remove(HHSConstants.PROVIDER_LINK);
				loRequestMap.put(HHSConstants.PROC_TITLE, aoStatusMap.get(HHSConstants.PROC_TITLE));
				loRequestMap.put(HHSConstants.PROP_TITLE, aoStatusMap.get(HHSConstants.PROP_TITLE));
				loRequestMap.put(HHSConstants.AGENCY_NAME_COLUMN, aoStatusMap.get(HHSConstants.AGENCY_NAME_COLUMN));
				loRequestMap.put(HHSConstants.PROPOSAL_SUBMITTED_TIME, aoStatusMap.get(HHSConstants.PROP_MOD_DATE));
				loRequestMap.put(HHSConstants.PROVIDER_NAME, aoStatusMap.get(HHSConstants.ORGANIZATION_NAME));
			}
		}
		return aoNotificationMap;
	}

	/**
	 * This method will fetch the count of selected proposals for
	 * procurement/evaluation pool mapping
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Check if task is not in returned status</li>
	 * <li>2. Execute query <b>fetchCountofSelectedProposals</b> to get count
	 * proposal count</li>
	 * <li>3. Return boolean flag based on proposal count</li>
	 * @param aoMyBatisSession - Mybatis session
	 * @param aoDatahMap - Map containing required data
	 * @param asTaskStatus - Current task status
	 * @return abProposalNotSelected - boolean -- return true if counts of
	 *         proposal selected is > 0
	 * @throws ApplicationException
	 */
	public boolean fetchCountofSelectedProposals(SqlSession aoMyBatisSession, HashMap<String, String> aoDataMap,
			String asTaskStatus) throws ApplicationException
	{
		Integer loProposalCount = 0;
		boolean abProposalNotSelected = false;
		try
		{
			if (null != asTaskStatus && !asTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED))
			{
				loProposalCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoDataMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_SELECTED_PROPOSALS_COUNT,
						HHSConstants.JAVA_UTIL_HASH_MAP);
				if (loProposalCount == 0)
				{
					abProposalNotSelected = true;
				}
			}
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching contract IDs :", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while fetching contract IDs :", loExp);
			throw new ApplicationException("Error while fetching contract IDs :", loExp);
		}
		return abProposalNotSelected;
	}

	/**
	 * This method updates the modified flag of Award
	 * <ul>
	 * <li>Updated Method in R4</li>
	 * <li>1. Check if task is not returned</li>
	 * <li>2. if step 1 passes execute query <b>updateModifiedFlagFromAward</b></li>
	 * </ul>
	 * @param aoMyBatisSession my batis session
	 * @param aoDataMap data map containing proposal related details
	 * @param asTaskStatus current task status
	 * @return flag showing update was successful
	 * @throws ApplicationException application exception
	 */
	public boolean updateModifiedFlagFromAward(SqlSession aoMyBatisSession, HashMap<String, String> aoDataMap,
			String asTaskStatus) throws ApplicationException
	{
		boolean abUpdatedFlag = false;
		try
		{
			if (null != asTaskStatus && !asTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED))
			{
				DAOUtil.masterDAO(aoMyBatisSession, aoDataMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
						HHSConstants.UPDATE_MODIFIED_FLAG_FROM_AWARD, HHSConstants.JAVA_UTIL_HASH_MAP);
				abUpdatedFlag = true;
			}
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while updating modified flag from Award details :", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while updating modified flag from Award details :", loExp);
			throw new ApplicationException("Error while updating modified flag from Award details :", loExp);
		}
		return abUpdatedFlag;
	}

	/**
	 * This method will fetch the provider Ids for a procurement with selected,
	 * not selected proposals
	 * <ul>
	 * <li>1. Check if task status not returned</li>
	 * <li>2. If yes fetch list of providers for first round/second round based
	 * on Modified flag</li>
	 * <li>3. Separate the fetched list into two separate lists one each of
	 * selected, not selected proposals</li>
	 * <li>4. Add the generated lists to hashmap and return it</li>
	 * <li>5. Execute query <b> fetchProviderIdFirstRound </b></li>
	 * <li>6. Execute query <b> fetchProviderIdSecondRound </b></li>
	 * </ul>
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoModifiedFlag - boolean status flag
	 * @param aoDataMap - Map
	 * @param asTaskStatus - Task Status
	 * @return loProvSelectionMap map containg the list of selected providers
	 *         and not selected providers
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public HashMap fetchProviderIds(SqlSession aoMyBatisSession, Boolean aoModifiedFlag,
			HashMap<String, String> aoDataMap, String asTaskStatus) throws ApplicationException
	{
		List<ProviderSelectionBean> loProviderSelectionBeanList = null;
		HashMap loProvSelectionMap = new HashMap();
		try
		{
			if (null != asTaskStatus && !asTaskStatus.equalsIgnoreCase(ApplicationConstants.STATUS_RETURNED))
			{
				// executing query on the basis of boolean flag
				if (aoModifiedFlag)
				{
					loProviderSelectionBeanList = (List<ProviderSelectionBean>) DAOUtil.masterDAO(aoMyBatisSession,
							aoDataMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
							HHSConstants.FETCH_PROVIDER_ID_FIRST_ROUND, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
				else
				{
					loProviderSelectionBeanList = (List<ProviderSelectionBean>) DAOUtil.masterDAO(aoMyBatisSession,
							aoDataMap, HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER,
							HHSConstants.FETCH_PROVIDER_ID_SECOND_ROUND, HHSConstants.JAVA_UTIL_HASH_MAP);
				}
				if (null != loProviderSelectionBeanList)
				{
					fetchSelectedNotSelectedProvider(loProviderSelectionBeanList, loProvSelectionMap);
					// R5 change starts
					String lsNegotiationReqiured = aoDataMap.get(HHSR5Constants.IS_NEGOTIATION_REQUIRED);
					if (null != lsNegotiationReqiured)
					{
						if (lsNegotiationReqiured.equalsIgnoreCase(HHSConstants.STRING_TRUE))
						{
							loProvSelectionMap.put(HHSR5Constants.IS_NEGOTIATION_REQUIRED, HHSConstants.BOOLEAN_TRUE);
						}
						else
						{
							loProvSelectionMap.put(HHSR5Constants.IS_NEGOTIATION_REQUIRED, HHSConstants.BOOLEAN_FALSE);
						}
					}
					// R5 change ends
				}
			}
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching contract IDs :", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception loExp)
		{
			LOG_OBJECT.Error("Error while fetching contract IDs :", loExp);
			throw new ApplicationException("Error while fetching contract IDs :", loExp);
		}
		return loProvSelectionMap;
	}

	/**
	 * This method separates selected and not selected providers required for
	 * notification
	 * <ul>
	 * <li>Iterate over list or providers</li>
	 * <li>Get list of selected and not selected providers</li>
	 * <li>Convert list to array and set them in Map</li>
	 * </ul>
	 * @param aoProviderSelectionBeanList - List of all providers proposal
	 * @param aoProvSelectionMap - Map to contain selected/nonselected provider
	 *            arrays
	 */
	private void fetchSelectedNotSelectedProvider(List<ProviderSelectionBean> aoProviderSelectionBeanList,
			HashMap aoProvSelectionMap)
	{
		Iterator<ProviderSelectionBean> loProvIter = aoProviderSelectionBeanList.iterator();
		List<String> loSelectedProviderList = new ArrayList<String>();
		List<String> loNotSelectedProviderList = new ArrayList<String>();
		String[] loSelectedArray = null;
		String[] loSelectedNotArray = null;
		while (loProvIter.hasNext())
		{
			ProviderSelectionBean loProcSelectionBean = loProvIter.next();
			String lsProvStatus = loProcSelectionBean.getMiSelectionStatus();
			if (null != lsProvStatus && lsProvStatus.equalsIgnoreCase(HHSConstants.STATUS_SELECTED))
			{
				loSelectedProviderList.add(loProcSelectionBean.getMiProviderId());
			}
			else if (null != lsProvStatus && lsProvStatus.equalsIgnoreCase(HHSConstants.STATUS_NOT_SELECTED))
			{
				loNotSelectedProviderList.add(loProcSelectionBean.getMiProviderId());
			}
		}
		loNotSelectedProviderList.removeAll(loSelectedProviderList);
		if (null != loSelectedProviderList && !loSelectedProviderList.isEmpty())
		{
			loSelectedArray = new String[loSelectedProviderList.size()];
			int liInteger = 0;
			for (String lsProviderStatus : loSelectedProviderList)
			{
				loSelectedArray[liInteger++] = lsProviderStatus;
			}
		}
		if (null != loNotSelectedProviderList && !loNotSelectedProviderList.isEmpty())
		{
			loSelectedNotArray = new String[loNotSelectedProviderList.size()];
			int liInteger = 0;
			for (String lsProviderStatus : loNotSelectedProviderList)
			{
				loSelectedNotArray[liInteger++] = lsProviderStatus;
			}
		}
		aoProvSelectionMap.put(P8Constants.PE_WORKFLOW_LIST_OF_SELECTED_PROVIDERS, loSelectedArray);
		aoProvSelectionMap.put(P8Constants.PE_WORKFLOW_LIST_OF_NOT_SELECTED_PROVIDERS, loSelectedNotArray);
	}

	/**
	 * This method will get the proposal status id for the specific proposal
	 * <ul>
	 * <li>Execute query <b>getProposalStatus</b> to get proposal status</li>
	 * <li>Add the status to aoStatusMap</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession valid sql session Object
	 * @param aoStatusMap input parameter map
	 * @return map containing proposal information
	 * @throws ApplicationException throws application exception
	 */
	public Map<String, String> getProposalStatus(SqlSession aoMybatisSession, Map<String, String> aoStatusMap,
			Boolean abStatusFlag) throws ApplicationException
	{
		Integer aoStatusId = null;
		try
		{
			if (abStatusFlag)
			{
				aoStatusId = (Integer) DAOUtil.masterDAO(aoMybatisSession, aoStatusMap,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_PROPOSAL_STATUS,
						HHSConstants.JAVA_UTIL_MAP);
				aoStatusMap.put(HHSConstants.PROPOSAL_STATUS_ID, aoStatusId.toString());
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException loExp)
		{
			setMoState("Error Occured while fetching the proposal status id :");
			throw loExp;
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (Exception loExp)
		{
			ApplicationException loAppEx = new ApplicationException(
					"Error Occured while fetching the proposal status id :", loExp);
			LOG_OBJECT.Error("Error Occured while fetching the proposal status id :", loAppEx);
			setMoState("Error Occured while fetching the proposal status id :");
			throw loAppEx;
		}
		return aoStatusMap;
	}

	/**
	 * This method is used to get the evaluation sent flag which indicates that
	 * the evaluation task has already been sent.
	 * <ul>
	 * <li>Execute the query <code>fetchEvaluationSentFlag</code> from
	 * <code>proposal</code> mapper</li>
	 * <li>Return the evaluation sent flag which indicates that the evaluation
	 * task has already been sent.</li>
	 * </ul>
	 * @param aoMybatisSession SqlSession sql session object
	 * @param asProposalId String proposal id
	 * @param aoValidateFlag boolean
	 * @return loEvalSentStatus boolean -- true if Evaluation is not sent else
	 *         return false
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	public Boolean getEvaluationSentFlag(SqlSession aoMybatisSession, String asProposalId, Boolean aoValidateFlag)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into get evaluation sent flag");
		Boolean loEvalSentStatus = false;
		Map<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asProposalId);
		try
		{
			if (aoValidateFlag)
			{
				// Calling fetchEvaluationSentFlag for clearing add delete flag,
				// passing
				// required parameters using Map
				String lsEvaluationSent = (String) DAOUtil.masterDAO(aoMybatisSession, asProposalId,
						HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_EVALUATION_SENT_FLAG,
						HHSConstants.JAVA_LANG_STRING);
				if (lsEvaluationSent == null)
				{
					loEvalSentStatus = true;
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loParamMap);
			LOG_OBJECT.Error("Error while getting evaluation sent flag :", aoExp);
			setMoState("Error while getting evaluation sent flag :");
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while getting evaluation sent flag :", aoExp);
			setMoState("Error while getting evaluation sent flag :");
			throw new ApplicationException("Exception while getting evaluation sent flag ", aoExp);
		}
		return loEvalSentStatus;
	}

	/**
	 * Used to check whether the competition pool for a given proposal id is
	 * cancelled or not Changes done for Enhancement #6577 for Release 3.10.0
	 * @param aoMybatisSession SqlSession
	 * @param asProposalId String
	 * @param aoValidateFlag Boolean
	 * @return loCompPoolNotCancelledStatus Boolean
	 * @throws ApplicationException
	 */
	public Boolean checkCompPoolCancelled(SqlSession aoMybatisSession, String asProposalId, Boolean aoValidateFlag)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into checkCompPoolCancelled");
		Boolean loCompPoolNotCancelledStatus = false;
		Map<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put(HHSConstants.PROPOSAL_ID, asProposalId);
		try
		{
			if (aoValidateFlag)
			{
				if (null != asProposalId && !asProposalId.isEmpty())
				{
					String lsCompPoolStatus = (String) DAOUtil.masterDAO(aoMybatisSession, asProposalId,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.CHECK_COMP_POOL_STATUS,
							HHSConstants.JAVA_LANG_STRING);
					if (null != lsCompPoolStatus
							&& !lsCompPoolStatus.equalsIgnoreCase(PropertyLoader.getProperty(
									HHSConstants.PROPERTIES_STATUS_CONSTANT,
									HHSConstants.STATUS_COMPETITION_POOL_CANCELLED)))
					{
						loCompPoolNotCancelledStatus = true;
					}
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loParamMap);
			LOG_OBJECT.Error("Error while checkCompPoolCancelled :", aoExp);
			setMoState("Error while checkCompPoolCancelled :");
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while checkCompPoolCancelled :", aoExp);
			setMoState("Error while checkCompPoolCancelled :");
			throw new ApplicationException("Exception while checkCompPoolCancelled ", aoExp);
		}
		return loCompPoolNotCancelledStatus;
	}

	/**
	 * Used to check whether the competition pool for a given eval pool mapping
	 * id is cancelled or not Changes done for Enhancement #6577 for Release
	 * 3.10.0
	 * @param aoMybatisSession SqlSession
	 * @param evaluationPoolMappingId String
	 * @param aoValidateFlag Boolean
	 * @return loEvalPoolCancelledStatus Boolean
	 * @throws ApplicationException
	 */
	public Boolean checkEvalPoolCancelled(SqlSession aoMybatisSession, String evaluationPoolMappingId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into checkEvalPoolCancelled");
		Boolean loEvalPoolCancelledStatus = false;
		Map<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, evaluationPoolMappingId);
		try
		{
			if (null != evaluationPoolMappingId && !evaluationPoolMappingId.isEmpty())
			{
				String lsEvalPoolStatus = (String) DAOUtil.masterDAO(aoMybatisSession, evaluationPoolMappingId,
						HHSConstants.MAPPER_CLASS_EVALUATION_MAPPER, HHSConstants.FETCH_EVAL_POOL_MAPPING_STATUS,
						HHSConstants.JAVA_LANG_STRING);
				if (null != lsEvalPoolStatus
						&& lsEvalPoolStatus.equalsIgnoreCase(PropertyLoader
								.getProperty(HHSConstants.PROPERTIES_STATUS_CONSTANT,
										HHSConstants.STATUS_COMPETITION_POOL_CANCELLED)))
				{
					loEvalPoolCancelledStatus = true;
				}
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loParamMap);
			LOG_OBJECT.Error("Error while checkEvalPoolCancelled :", aoExp);
			setMoState("Error while checkEvalPoolCancelled :");
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while checkEvalPoolCancelled :", aoExp);
			setMoState("Error while checkEvalPoolCancelled :");
			throw new ApplicationException("Exception while checkEvalPoolCancelled ", aoExp);
		}
		return loEvalPoolCancelledStatus;
	}

	/**
	 * <p>
	 * This method added as a part of release 3.1.0 for enhancement request 6024
	 * 
	 * This method fetches the latest version no of the procurement questions
	 * ,latest version no of proposal documents, current version no of proposal
	 * questions and current version no of the proposal documents
	 * <ul>
	 * <li>1. Add multiple input parameters to a Map</li>
	 * <li>2. Fetch the latest version no of the procurement questions ,latest
	 * version no of proposal documents, current version no of proposal
	 * questions and current version no of the proposal documents using query id
	 * <b>fetchVersionInformation</b></li>
	 * </ul>
	 * </p>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param asProposalId - Proposal Id
	 * @param asProcurementId - Procurement Id
	 * @return loVersionInfoBean containing version information
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings("unchecked")
	public ProposalDetailsBean fetchVersionInformation(SqlSession aoMybatisSession, String asProposalId,
			String asProcurementId) throws ApplicationException
	{
		ProposalDetailsBean loVersionInfoBean = null;
		Map<String, String> loMap = new HashMap<String, String>();
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
		loContextDataMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
		LOG_OBJECT.Debug("Entered into fetching Version Number Information:" + loContextDataMap.toString());

		try
		{
			loMap.put(HHSConstants.PROPOSAL_ID_KEY, asProposalId);
			loMap.put(HHSConstants.PROCUREMENT_ID_KEY, asProcurementId);
			loVersionInfoBean = (ProposalDetailsBean) DAOUtil.masterDAO(aoMybatisSession, loMap,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.FETCH_VERSION_INFORMATION,
					HHSConstants.JAVA_UTIL_MAP);
			setMoState("Version Number Information fetched successfully");
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			setMoState("Error while fetching Version Number Information for procurement id: " + asProcurementId
					+ " and proposal id: " + asProposalId);
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching Version Number Information for procurement id: " + asProcurementId
					+ " and proposal id: " + asProposalId, aoAppEx);
			throw aoAppEx;
		}
		catch (Exception loExp)
		{
			setMoState("Error while fetching Version Number Information for procurement id: " + asProcurementId
					+ " and proposal id: " + asProposalId);
			LOG_OBJECT.Error("Error while fetching Version Number Information for procurement id: " + asProcurementId
					+ " and proposal id: " + asProposalId, loExp);
			throw new ApplicationException("Error while fetching Version Number Information for procurement id: "
					+ asProcurementId + " and proposal id: " + asProposalId, loExp);
		}
		return loVersionInfoBean;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Manage Organization
	 * Module.
	 * 
	 * This method fetches the count of proposal list on the basis of statuses,
	 * modified dates, competition pool title, procurment title,and agencyId for
	 * agency,city and provider users.
	 * <ul>
	 * <li>It will return total count for Proposal List on the basis of
	 * ProposalDetailsBean</li>
	 * <li>Fetch the data for proposal list screen using query id
	 * <b>fetchProposalCount</b></li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoProposalDetailsBean ProposalDetailsBean
	 * @return lsProposalCount String which contains TotalCount for Proposal
	 *         list data
	 * @throws ApplicationException If an application exception occurs
	 */
	public String getProposalCountData(SqlSession aoMybatisSession, ProposalDetailsBean aoProposalDetailsBean)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into getProposalCountData");
		String lsProposalCount = null;
		try
		{
			if (StringUtils.isNotBlank(aoProposalDetailsBean.getProposalTitle()))
			{
				StringBuffer loProposalTitleStrBfr = new StringBuffer(HHSConstants.PERCENT).append(
						aoProposalDetailsBean.getProposalTitle()).append(HHSConstants.PERCENT);
				aoProposalDetailsBean.setProposalTitle(loProposalTitleStrBfr.toString());
			}

			if (StringUtils.isNotBlank(aoProposalDetailsBean.getCompetitionPoolTitle()))
			{
				StringBuffer loCompetitionPoolTitleStrBfr = new StringBuffer(HHSConstants.PERCENT).append(
						aoProposalDetailsBean.getCompetitionPoolTitle()).append(HHSConstants.PERCENT);
				aoProposalDetailsBean.setCompetitionPoolTitle(loCompetitionPoolTitleStrBfr.toString());
			}

			if (StringUtils.isNotBlank(aoProposalDetailsBean.getProcurementtitle()))
			{
				StringBuffer loProcurementtitleStrBfr = new StringBuffer(HHSConstants.PERCENT).append(
						aoProposalDetailsBean.getProcurementtitle()).append(HHSConstants.PERCENT);
				aoProposalDetailsBean.setProcurementtitle(loProcurementtitleStrBfr.toString());
			}
			lsProposalCount = (String) DAOUtil.masterDAO(aoMybatisSession, aoProposalDetailsBean,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSR5Constants.FETCH_PROPOSAL_COUNT,
					HHSR5Constants.INPUT_PARAM_CLASS_PROPOSAL_DETAILS_BEAN);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			// aoExp.setContextData(loParamMap);
			LOG_OBJECT.Error("Error while getProposalCountData :", aoExp);
			setMoState("Error while getProposalCountData :");
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while getProposalCountData :", aoExp);
			setMoState("Error while getProposalCountData :");
			throw new ApplicationException("Exception while getProposalCountData ", aoExp);
		}
		return lsProposalCount;
	}

	/**
	 * <p>
	 * This method is added as a part of Release 5 for Manage Organization
	 * Module This method displays Proposal list when proposal tab is clicked.
	 * 
	 * This method fetches the proposal list on the basis of statuses, modified
	 * dates, competition pool title, procurment title,and agencyId for
	 * agency,city and provider users.
	 * <ul>
	 * <li>It will populate Proposal List on the basis of ProposalDetailsBean</li>
	 * <li>Proposal Data is fetched by executing query with id
	 * "fetchProposalDetailForCity" from ProposalMapper</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession mybatis SQL session
	 * @param aoProposalDetailsBean ProposalDetailsBean
	 * @return lsEvalPoolStatus List<ProposalDetailsBean>
	 * @throws ApplicationException If an application exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ProposalDetailsBean> getProposalData(SqlSession aoMybatisSession,
			ProposalDetailsBean aoProposalDetailsBean) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into getProposalData");
		Map<String, Object> loParamMap = new HashMap<String, Object>();
		List<ProposalDetailsBean> lsEvalPoolStatus = null;
		try
		{
			lsEvalPoolStatus = (List<ProposalDetailsBean>) DAOUtil.masterDAO(aoMybatisSession, aoProposalDetailsBean,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSR5Constants.FETCH_PROPOSAL_DETAIL_FOR_CITY,
					HHSR5Constants.INPUT_PARAM_CLASS_PROPOSAL_DETAILS_BEAN);
			String lsProposalTitle = aoProposalDetailsBean.getProposalTitle();
			if (StringUtils.isNotBlank(lsProposalTitle))
			{
				lsProposalTitle = lsProposalTitle.substring(HHSConstants.INT_ONE);
				lsProposalTitle = lsProposalTitle.substring(HHSConstants.INT_ZERO, lsProposalTitle.length()
						- HHSConstants.INT_ONE);
				aoProposalDetailsBean.setProposalTitle(lsProposalTitle);
			}

			String lsProcurementTitle = aoProposalDetailsBean.getProcurementtitle();
			if (StringUtils.isNotBlank(lsProcurementTitle))
			{
				lsProcurementTitle = lsProcurementTitle.substring(HHSConstants.INT_ONE);
				lsProcurementTitle = lsProcurementTitle.substring(HHSConstants.INT_ZERO, lsProcurementTitle.length()
						- HHSConstants.INT_ONE);
				aoProposalDetailsBean.setProcurementtitle(lsProcurementTitle);
			}

			String lsCompetitionPoolTitle = aoProposalDetailsBean.getCompetitionPoolTitle();
			if (StringUtils.isNotBlank(lsCompetitionPoolTitle))
			{
				lsCompetitionPoolTitle = lsCompetitionPoolTitle.substring(HHSConstants.INT_ONE);
				lsCompetitionPoolTitle = lsCompetitionPoolTitle.substring(HHSConstants.INT_ZERO,
						lsCompetitionPoolTitle.length() - HHSConstants.INT_ONE);
				aoProposalDetailsBean.setCompetitionPoolTitle(lsCompetitionPoolTitle);
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loParamMap);
			LOG_OBJECT.Error("Error while getProposalData :", aoExp);
			setMoState("Error while getProposalData :");
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while getProposalData :", aoExp);
			setMoState("Error while getProposalData :");
			throw new ApplicationException("Exception while getProposalData ", aoExp);
		}
		return lsEvalPoolStatus;
	}

	/**
	 * <p>
	 * This method added as a part of Release 5 for Manage Organization Module.
	 * 
	 * This method fetch Procurement Title List based on the search input given
	 * by the user in filter proposal list screen.
	 * <ul>
	 * <li>query executed "fetchProcurmentTitle"</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession mybatis SQL session
	 * @param loParamMap Map<String, String>
	 * @return loDataList List<AutoCompleteBean>
	 * @throws ApplicationException If an application exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<AutoCompleteBean> fetchProcurementTitleList(SqlSession aoMybatisSession, Map<String, String> loParamMap)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchProcurementTitleList");
		List<AutoCompleteBean> loDataList = null;
		try
		{
			loDataList = (List<AutoCompleteBean>) DAOUtil.masterDAO(aoMybatisSession, loParamMap,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSR5Constants.FETCH_PROCUREMENT_TITLE,
					HHSConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loParamMap);
			LOG_OBJECT.Error("Error while fetchProcurementTitleList :", aoExp);
			setMoState("Error while fetchProcurementTitleList :");
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetchProcurementTitleList :", aoExp);
			setMoState("Error while fetchProcurementTitleList :");
			throw new ApplicationException("Exception while fetchProcurementTitleList ", aoExp);
		}
		return loDataList;
	}

	/**
	 * <p>
	 * This method added as a part of Release 5 for Manage Organization Module.
	 * This method fetch the List of Competition Pool Title based on search
	 * input given by the user on filter proposal list screen.
	 * <ul>
	 * <li>query executed "fetchCompetitionPool"</li>
	 * </ul>
	 * </p>
	 * @param aoMybatisSession mybatis SQL session
	 * @param loParamMap Map<String, String>
	 * @return loDataList List<AutoCompleteBean>
	 * @throws ApplicationException If an application exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<AutoCompleteBean> fetchCompetitionPoolList(SqlSession aoMybatisSession, Map<String, String> loParamMap)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered into fetchCompetitionPoolList");
		List<AutoCompleteBean> loDataList = null;
		try
		{
			loDataList = (List<AutoCompleteBean>) DAOUtil.masterDAO(aoMybatisSession, loParamMap,
					HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSR5Constants.FETCH_COMPETITION_POOL,
					HHSConstants.JAVA_UTIL_MAP);
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoExp)
		{
			aoExp.setContextData(loParamMap);
			LOG_OBJECT.Error("Error while fetchCompetitionPoolList :", aoExp);
			setMoState("Error while fetchCompetitionPoolList :");
			throw aoExp;
		}
		// handling exception other than Application exception
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while fetchCompetitionPoolList :", aoExp);
			setMoState("Error while fetchCompetitionPoolList :");
			throw new ApplicationException("Exception while fetchCompetitionPoolList ", aoExp);
		}
		return loDataList;
	}
	/**
	 * Added in Release 5. This method is added for 8375.
	 * It fetches proposal documents for input proposal Id from task
	 * detail map
	 * <ul>
	 * <li>Check if map is not null and contains proposal Id</li>
	 * <li>Execute the query "fetchProposalDocumentsForEvaluation" which will not pull data for
	 * restricted documents</li>
	 * <li>Return the fetched list</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession - mybatis SQL session
	 * @param aoTaskDetailMap - a map containing task and procurement properties
	 * @param asWobNumber - a string value of wob number
	 * @return list of documents
	 * @throws ApplicationException If an Application exception occurs
	 */
	@SuppressWarnings("unchecked")
	public List<ExtendedDocument> fetchProposalDocumentsForEvaluation(SqlSession aoMyBatisSession,
			HashMap<String, Object> aoTaskDetailMap, String asWobNumber) throws ApplicationException
	{
		List<ExtendedDocument> loDocumentList = null;
		HashMap<String, Object> loContextDataMap = new HashMap<String, Object>();
		loContextDataMap.put(ApplicationConstants.WOB_NUMBER, asWobNumber);
		LOG_OBJECT.Debug("Entered into fetchProposalDocumentsForEvaluation for wob number:" + loContextDataMap.toString());
		try
		{
			// get task details map and check for null value
			if (null != aoTaskDetailMap)
			{
				HashMap<String, Object> loProposalMap = (HashMap<String, Object>) aoTaskDetailMap.get(asWobNumber);
				Integer loVersionNo = null;
				if (null != loProposalMap)
				{
					// get the proposal Id and procurement Id from task details
					// map
					String lsProposalId = (String) loProposalMap.get(P8Constants.PE_WORKFLOW_PROPOSAL_ID);
					String lsProcurementId = (String) loProposalMap.get(P8Constants.PE_WORKFLOW_PROCUREMENT_ID);
					HashMap<String, String> loProcProposalMap = new HashMap<String, String>();
					loProcProposalMap.put(HHSConstants.PROPOSAL_ID, lsProposalId);
					loProcProposalMap.put(HHSConstants.PROCUREMENT_ID, lsProcurementId);
					loProcProposalMap.put(HHSR5Constants.EVALUATION_POOL_MAPPING_ID,
							(String) loProposalMap.get(P8Constants.EVALUATION_POOL_MAPPING_ID));
					loVersionNo = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loProcProposalMap,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSConstants.GET_VERSION_NO_FOR_PROPOSAL_DOC,
							HHSConstants.JAVA_UTIL_MAP);
					loProcProposalMap.put(HHSConstants.DOC_VERSION_NO, loVersionNo.toString());
					loDocumentList = (List<ExtendedDocument>) DAOUtil.masterDAO(aoMyBatisSession, loProcProposalMap,
							HHSConstants.MAPPER_CLASS_PROPOSAL_MAPPER, HHSR5Constants.FETCH_PROPOSAL_DOCS_TASK_EVALUATION,
							HHSConstants.JAVA_UTIL_HASH_MAP);
					setMoState("Proposal Document Details Fetched successfully for ProposalId:" + lsProposalId);
				}
			}
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(aoTaskDetailMap);
			LOG_OBJECT.Error("Error occurred while fetching proposal documents for Evaluation Task :", aoAppEx);
			setMoState("Error occurred while fetching proposal documents for Evaluation Task :" + aoTaskDetailMap.toString());
			throw aoAppEx;
		}
		// handling exception other than ApplicationException
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occurred while fetching proposal documents for Evaluation Task:", aoTaskDetailMap.toString());
			setMoState("Error occurred while fetching proposal documents for Evaluation Task:" + aoTaskDetailMap.toString());
			throw new ApplicationException("Error occurred while fetching proposal documents for Evaluation Task", aoEx);
		}
		LOG_OBJECT.Debug("Exited ProposalService: fetchProposalDocumentsForEvaluation()");
		return loDocumentList;
	}

}