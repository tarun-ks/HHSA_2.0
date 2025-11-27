package com.nyc.hhs.component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.daomanager.service.ServiceState;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.AssignementDetailsBean;
import com.nyc.hhs.model.ContractDetailsBean;
import com.nyc.hhs.model.EvaluationReassignDetailsBean;
import com.nyc.hhs.model.EvaluationStatusBean;
import com.nyc.hhs.model.Evaluator;
import com.nyc.hhs.model.FinancialWFBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.HHSUtil;

/**
 * HHSComponentP8Service: This service class is used to execute different
 * transactions involved in P8 custom components.
 */

public class HHSComponentP8Service extends ServiceState
{

	private static final LogInfo LOG_OBJECT = new LogInfo(HHSComponentP8Service.class);

	/**
	 * This method will get the list of All the proposalids related to
	 * Procurement
	 * <ul>
	 * <li>Get the SQL session from the Parameter</li>
	 * <li>Execute the query <b>fetchProposalIdList</b> of HHSComponentP8Mapper</li>
	 * </ul>
	 * @param aoMybatisSession Valid SQL Session
	 * @param aoHashMap HashMap
	 * @return loProposalIdList List of All Proposal IDs
	 * @throws ApplicationException when any error occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<String> fetchProposalIdList(SqlSession aoMybatisSession, HashMap aoHashMap) throws ApplicationException
	{
		List<String> loProposalIdList = null;
		try
		{
			HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
			loProposalIdList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_PROPOSAL_ID_LIST,
					HHSP8Constants.JAVA_UTIL_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handled over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while fetchin proposal Id List in fetchProposalIdList method in HHSComponentP8Service",
							aoAppEx);
			setMoState("Transaction Failed:: HHSComponentP8Service:fetchProposalIdList method - failed to get the Proposal Id List with queryId: \n");
			throw aoAppEx;
		}
		return loProposalIdList;
	}

	/**
	 * This method gets the procurement Summary corresponding to a procurement
	 * Id
	 * 
	 * <ul>
	 * <li>1. Retrieve procurement Id and procurement status</li>
	 * <li>2. Set procurement id and procurement status in a map for context
	 * data to be logged in case of exception.</li>
	 * <li>3. If the retrieved procurement status is not null and is equal to
	 * "2" or "3" then execute the query "getReleasedProcurementSummary"
	 * specified in the procurementMapper.</li>
	 * <li>4. If retrieved procurement summary is null then execute query
	 * <b>getProcurementSummary</b> to fetch the required procurement summary</li>
	 * <li>5. Return the Procurement Summary.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession - SqlSession
	 * @param aoHashMap - Map
	 * @return Procurement Bean
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Procurement fetchProcurementSummary(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		Procurement loProcurementSummary = null;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		LOG_OBJECT.Info("Entered into getting Procurement Details::" + loHMap.toString());
		try
		{
			loProcurementSummary = (Procurement) DAOUtil.masterDAO(aoMybatisSession, loHMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_PROCUREMENT_SUMMARY_PROVIDER, HHSP8Constants.JAVA_UTIL_MAP);

			LOG_OBJECT.Info("Procurement details fetched successfully for Procurement");
		}

		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while getting Procurement Details", aoAppEx);
			throw aoAppEx;
		}
		// handling Exception thrown by any action/resource
		catch (Exception loEx)
		{
			LOG_OBJECT.Error("Error while getting Procurement Details", loEx);
			throw new ApplicationException("Error while getting Procurement Details", loEx);
		}
		return loProcurementSummary;
	}

	/**
	 * This method will fetch the details of the selected proposal and set the
	 * details bean in the channel object
	 * <ul>
	 * <li>Get the proposal id from the channel object</li>
	 * <li>Execute the select query <b>fetchProposalDetails</b> with argument
	 * proposal id</li>
	 * <li>Set the details bean into the channel object.</li>
	 * </ul>
	 * 
	 * @param aoMyBatisSession Sql Session object
	 * @param aoHashMap Map
	 * @return ProposalDetailsBean object with all details
	 * @throws ApplicationException application exception
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public ProposalDetailsBean fetchProposalDetails(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailBean = null;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);

		LOG_OBJECT.Info("Entered into getting Procurement Details::" + loHMap.toString());

		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			loProposalDetailBean = (ProposalDetailsBean) DAOUtil.masterDAO(aoMyBatisSession, loHMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_PROPOSAL_DETAILS,
					HHSP8Constants.JAVA_UTIL_MAP);
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching proposal details for proposal id :", aoAppEx);
			throw aoAppEx;
		}
		return loProposalDetailBean;
	}

	/**
	 * This method is used to get all the internal evaluator from the database
	 * execute query fetchInternalEvaluationsList
	 * @param aoMybatisSession mybatis session to connect with the database
	 * @param aoHashMap Map
	 * @return the list of all the internal evaluator
	 * @throws ApplicationException when error occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<Evaluator> fetchInternalEvaluatorUsers(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<Evaluator> loInternalEvaluatorList = null;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		LOG_OBJECT.Info("Entered into getting Procurement Details::" + loHMap.toString());
		try
		{
			// If evaluation tasks have already been sent then find the internal
			// evaluation list
			loInternalEvaluatorList = (List<Evaluator>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_INTERNAL_EVALUATIONS_LIST, HHSP8Constants.JAVA_UTIL_MAP);
		}
		/**
		 * Any Exception from DAO class will be thrown as Application Exception
		 * which will be handles over here. It throws Application Exception back
		 * to Controllers calling method through Transaction framework
		 */
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while getting all the internal evaluators ", aoAppEx);
			throw aoAppEx;
		}
		return loInternalEvaluatorList;
	}

	/**
	 * This method fetches the evaluation users external.
	 * 
	 * <ul>
	 * <li>1. Execute the query "fetchExternalEvaluatorUsers" with asAgencyId as
	 * a parameter.</li>
	 * <li>2. Return the list of ProviderBean.</li>
	 * </ul>
	 * 
	 * @param aoMybatisSession Sql Session object
	 * @param aoHashMap Map
	 * @return List
	 * @throws ApplicationException when error occurs
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<Evaluator> fetchExternalEvaluatorUsers(SqlSession aoMybatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<Evaluator> loExternalEvaluatorList = null;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		LOG_OBJECT.Info("Entered into getting Procurement Details::" + loHMap.toString());
		try
		{
			loExternalEvaluatorList = (List<Evaluator>) DAOUtil.masterDAO(aoMybatisSession, loHMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_EXTERNAL_EVALUAITIONS_LIST, HHSP8Constants.JAVA_UTIL_MAP);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while getting all the external evaluators ", aoAppEx);
			throw new ApplicationException("Exception occured while getting external evaluation user setting from db",
					aoAppEx);
		}
		return loExternalEvaluatorList;
	}

	/**
	 * This method will fetch a list of proposal details
	 * 
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @return List
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<ProposalDetailsBean> fetchMultipleProposalsDetails(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<ProposalDetailsBean> loListProposalDetailBeans = null;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		LOG_OBJECT.Info("Entered into getting Proposal Details::" + loHMap.toString());
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		// Retrieve Proposal detail beans
		try
		{
			loListProposalDetailBeans = (List<ProposalDetailsBean>) DAOUtil.masterDAO(aoMyBatisSession, loHMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_MULTIPLE_PROPOSAL_DETAILS_TRANSACTION, HHSP8Constants.JAVA_UTIL_MAP);
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching proposal details for proposal id :", aoAppEx);
			throw aoAppEx;
		}
		return loListProposalDetailBeans;
	}

	/**
	 * This method will fetch a list of proposal details Execute query
	 * id,:fetchRequiredProposalsDetails
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @return List
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<ProposalDetailsBean> fetchRequiredProposalsDetails(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<ProposalDetailsBean> loListProposalDetailBeans = null;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);

		LOG_OBJECT.Info("Entered into getting Proposal Details::" + loHMap.toString());

		Map<String, Object> loContextDataMap = new HashMap<String, Object>();

		// Retrieve Proposal detail beans
		try
		{
			loListProposalDetailBeans = (List<ProposalDetailsBean>) DAOUtil.masterDAO(aoMyBatisSession, loHMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_REQUIRED_PROPOSAL_DETAILS, HHSP8Constants.JAVA_UTIL_MAP);
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching proposal details for proposal id :", aoAppEx);
			throw aoAppEx;
		}
		return loListProposalDetailBeans;
	}

	/**
	 * The method will return a list of propal details selected based on the
	 * given proposal id Execute transaction
	 * fetchMultipleProposalsDetailsByProposal
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @return list
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<ProposalDetailsBean> fetchMultipleProposalsDetailsByProposal(SqlSession aoMyBatisSession,
			HashMap aoHashMap) throws ApplicationException
	{
		List<ProposalDetailsBean> loListProposalDetailBeans = null;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		LOG_OBJECT.Info("Entered into getting Proposal Details::" + loHMap.toString());
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		// Retrieve Proposal detail beans
		try
		{
			loListProposalDetailBeans = (List<ProposalDetailsBean>) DAOUtil.masterDAO(aoMyBatisSession, loHMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_MULTIPLE_PROPOSAL_DETAILS, HHSP8Constants.JAVA_UTIL_MAP);
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching proposal details for proposal id :", aoAppEx);
			throw aoAppEx;
		}
		return loListProposalDetailBeans;
	}

	/**
	 * This method will fetch the details for multiple contracts Execute
	 * transaction id fetchMultipleContractDetails
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoMyBatisSession SqlSession object
	 * @param aoHashMap HashMap object
	 * @return list ContractDetailsBean list
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<ContractDetailsBean> fetchMultipleContractsDetails(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<ContractDetailsBean> loContractDetailList = null;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		LOG_OBJECT.Info("Entered into getting Contract Details::" + loHMap.toString());
		// Retrieve Contract detail beans
		try
		{
			loContractDetailList = (List<ContractDetailsBean>) DAOUtil.masterDAO(aoMyBatisSession, loHMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_MULTIPLE_CONTRACTS_TRANSACTION, HHSP8Constants.JAVA_UTIL_MAP);
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loHMap);
			LOG_OBJECT.Error("Error while fetching proposal details for contracts id :", aoAppEx);
			throw aoAppEx;
		}
		return loContractDetailList;
	}

	/**
	 * This method will update the details for multiple contracts Execute
	 * trsancatinupdateMultipleContractsDetails
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @return boolean
	 * @throws ApplicationException
	 */
	public Boolean updateMultipleContractsDetails(SqlSession aoMyBatisSession,
			List<ContractDetailsBean> aoContractDetailList, HashMap aoHashMap) throws ApplicationException
	{
		Boolean lbUpdateFlag = false;
		String lsContractId = null;
		String lsUpdateFlag = (String) aoHashMap.get(HHSR5Constants.IS_NEGOTIATION_REQUIRED);
		// Retrieve Contract detail beans
		try
		{
			if(null != lsUpdateFlag)
			{
				LOG_OBJECT.Info("Entered into updateMultipleContractsDetails::" + aoContractDetailList);
				for (ContractDetailsBean loContractBean : aoContractDetailList)
				{
					lsContractId = loContractBean.getContractId();
					DAOUtil.masterDAO(aoMyBatisSession, lsContractId,
							HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
							HHSP8Constants.UPDATE_MULTIPLE_CONTRACTS_TRANSACTION, HHSP8Constants.JAVA_LANG_STRING);
				}
			}
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching proposal details for contracts id :", aoAppEx);
			throw aoAppEx;
		}
		return lbUpdateFlag;
	}

	/**
	 * This method will fetch the details for the evaluation reassignment
	 * function, it will fetch the internal evaluator information.
	 * 
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @return list
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<EvaluationReassignDetailsBean> fetchEvaluationReassignDetails(SqlSession aoMyBatisSession,
			HashMap aoHashMap) throws ApplicationException
	{
		List<EvaluationReassignDetailsBean> loEvaluationReassignDetailsBeanList = new ArrayList<EvaluationReassignDetailsBean>();
		String lsEvaluatorFlag = null;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		lsEvaluatorFlag = (String) loHMap.get(HHSP8Constants.EVALUATOR_FLAG);
		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		LOG_OBJECT.Info("Entered into getting Contract Details::" + loHMap.toString());

		// Retrieve evaluation reassign detail beans
		try
		{
			if (lsEvaluatorFlag.equalsIgnoreCase(HHSP8Constants.INTERNAL_FLAG))
			{
				LOG_OBJECT.Info("Entered into fetchEvaluationReassignDetails if condition, lsEvaluatorFlag :"
						+ lsEvaluatorFlag);

				loEvaluationReassignDetailsBeanList = (List<EvaluationReassignDetailsBean>) DAOUtil.masterDAO(
						aoMyBatisSession, loHMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.FETCH_EVAL_REASSIGN_INT_DETAILS, HHSP8Constants.JAVA_UTIL_MAP);
			}
			else if (lsEvaluatorFlag.equalsIgnoreCase(HHSP8Constants.EXTERNAL_FLAG))
			{
				LOG_OBJECT.Info("Entered into fetchEvaluationReassignDetails else if, lsEvaluatorFlag :"
						+ lsEvaluatorFlag);
				loEvaluationReassignDetailsBeanList = (List<EvaluationReassignDetailsBean>) DAOUtil.masterDAO(
						aoMyBatisSession, loHMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.FETCH_EVAL_REASSIGN_EXT_DETAILS, HHSP8Constants.JAVA_UTIL_MAP);
			}
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching proposal details for evaluation reassignments internal id :",
					aoAppEx);
			throw aoAppEx;
		}
		return loEvaluationReassignDetailsBeanList;
	}

	/**
	 * 
	 * This method will fetch the details for the evaluation reassignment
	 * function, it will fetch the external evaluator information. Execute
	 * transaction id fetchEvaluationReassignExternalDetails
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<EvaluationReassignDetailsBean> fetchEvaluationReassignExternalDetails(SqlSession aoMyBatisSession,
			HashMap aoHashMap) throws ApplicationException
	{
		List<EvaluationReassignDetailsBean> loEvaluationReassignDetailsBeanList = null;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		LOG_OBJECT.Info("Entered into getting Contract Details::" + loHMap.toString());

		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		// Retrieve reassign details for external evaluators
		try
		{
			loEvaluationReassignDetailsBeanList = (List<EvaluationReassignDetailsBean>) DAOUtil.masterDAO(
					aoMyBatisSession, loHMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_EVAL_REASSIGN_EXT_DETAILS, HHSP8Constants.JAVA_UTIL_MAP);
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching proposal details for evaluation reassignments external id :",
					aoAppEx);
			throw aoAppEx;
		}
		return loEvaluationReassignDetailsBeanList;
	}

	/**
	 * This method will fetch the evaluation status details by the internal
	 * evaluator
	 * 
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @return list
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<String> fetchEvaluationStatusIdsByEvaluatorDetails(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<String> loStatusIdList = null;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		String lsEvaluatorFlag = (String) loHMap.get(HHSP8Constants.EVALUATOR_FLAG);
		LOG_OBJECT.Info("Entered into getting internal evaluator Details::" + loHMap.toString());

		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			if (lsEvaluatorFlag.equalsIgnoreCase(HHSP8Constants.INTERNAL_FLAG))
			{
				LOG_OBJECT.Info("Entered into fetchEvaluationStatusIdsByEvaluatorDetails if block, lsEvaluatorFlag :"
						+ lsEvaluatorFlag);
				loStatusIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, loHMap,
						HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.FETCH_EVAL_STATUS_IDS_BY_INT, HHSP8Constants.JAVA_UTIL_MAP);

			}
			else if (lsEvaluatorFlag.equalsIgnoreCase(HHSP8Constants.EXTERNAL_FLAG))
			{
				LOG_OBJECT.Info("Entered into fetchEvaluationStatusIdsByEvaluatorDetails else if, lsEvaluatorFlag :"
						+ lsEvaluatorFlag);
				loStatusIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, loHMap,
						HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.FETCH_EVAL_STATUS_IDS_BY_EXT, HHSP8Constants.JAVA_UTIL_MAP);

			}
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while fetching proposal details for evaluation status ids :", aoAppEx);
			throw aoAppEx;
		}
		return loStatusIdList;
	}

	/**
	 * This method will delete the evaluation score by the evaluation status id
	 * 
	 * @param aoMyBatisSession
	 * @param aoHashMap return boolean
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean deleteEvaluationScore(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		Boolean lbSuccess = false;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		String lsEvaluationStatusId = (String) loHMap.get(HHSP8Constants.EVALUATION_STATUS_ID);
		LOG_OBJECT.Info("Entered into deleting evaluation score::" + lsEvaluationStatusId);

		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			// Deleting Evaluation Score data against evaluationStatusId
			DAOUtil.masterDAO(aoMyBatisSession, lsEvaluationStatusId,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.DELETE_EVALUATION_SCORE_TRANSACTION, HHSP8Constants.JAVA_LANG_STRING);
			lbSuccess = true;
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while deleting evaluation scores :", aoAppEx);
			throw aoAppEx;
		}
		return lbSuccess;
	}

	/**
	 * This method will delete the evaluation status by the evaluation status id
	 * Execute transaction id deleteEvaluationStatus
	 * @param aoMyBatisSession
	 * @param aoHashMap return boolean
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean deleteEvaluationStatus(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		Boolean loSuccess = false;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		String lsEvaluationStatusId = (String) loHMap.get(HHSP8Constants.EVALUATION_STATUS_ID);
		LOG_OBJECT.Info("Entered into deleting evaluation status::" + lsEvaluationStatusId);

		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			// Deleting Evaluation status data against evaluationStatusId
			DAOUtil.masterDAO(aoMyBatisSession, lsEvaluationStatusId,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.DELETE_EVALUATION_STATUS_TRANSACTION, HHSP8Constants.JAVA_LANG_STRING);
			loSuccess = true;
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while deleting evaluation status :", aoAppEx);
			throw aoAppEx;
		}
		return loSuccess;
	}

	/**
	 * This method will fetch the evaluation status details by procurement
	 * Execute transaction fetchProposalConfigDetails
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @return List<EvaluationStatusBean>
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<EvaluationStatusBean> fetchProposalConfigDetails(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		List<EvaluationStatusBean> loEvaluationStatusBeanList = null;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		LOG_OBJECT.Info("Entered into getting evaluation status:" + loHMap.toString());

		// Retrieve the evaluationStatus beans as list
		try
		{
			loEvaluationStatusBeanList = (List<EvaluationStatusBean>) DAOUtil.masterDAO(aoMyBatisSession, loHMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_PROPOSAL_CONFIG_DETAILS_TRANSACTION, HHSP8Constants.JAVA_UTIL_MAP);
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching proposal details for evaluation status ids :", aoAppEx);
			throw aoAppEx;
		}
		return loEvaluationStatusBeanList;
	}

	/**
	 * This method will delete the evaluation status by the evaluation status id
	 * Execute transactioninsertEvaluationStatus
	 * @param aoMyBatisSession
	 * @param aoHashMap return boolean
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	public Boolean insertEvaluationStatus(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into inserting evaluation status");
		Boolean loSuccess = false;
		HashMap loHMap = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		EvaluationStatusBean loEvaluationStatusBean = (EvaluationStatusBean) loHMap
				.get(HHSP8Constants.EVALUATION_STATUS_BEAN);

		// Insert the a new record in the evaluation status table.
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, loEvaluationStatusBean,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.INSERT_EVAL_STATUS_TRANSACTION,
					HHSP8Constants.EVALUATION_STATUS_BEAN_CLASS);
			loSuccess = true;
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while inserting evaluation status :", aoAppEx);
			throw aoAppEx;
		}
		return loSuccess;
	}

	/**
	 * This method will delete the evaluation status by the evaluation status id
	 * 
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String fetchEvaluationStatusID(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into fetch evaluation status");
		Map<String, String> loInputParam = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		String lsIntExtFlag = (String) loInputParam.get(HHSP8Constants.INT_EXT_FLAG);
		String lsEvaluationStatusID = null;

		// Insert the a new record in the evaluation status table.
		try
		{
			if (lsIntExtFlag.equalsIgnoreCase(HHSP8Constants.INTERNAL_FLAG))
			{
				LOG_OBJECT.Info("Entered into fetchEvaluationStatusID if condition" + lsIntExtFlag);
				lsEvaluationStatusID = (String) DAOUtil.masterDAO(aoMyBatisSession, loInputParam,
						HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_EVAL_ID_INTERNAL,
						HHSP8Constants.JAVA_UTIL_MAP);
			}
			else
			{
				LOG_OBJECT.Info("Entered into fetchEvaluationStatusID if condition" + lsIntExtFlag);
				lsEvaluationStatusID = (String) DAOUtil.masterDAO(aoMyBatisSession, loInputParam,
						HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_EVAL_ID_EXTERNAL,
						HHSP8Constants.JAVA_UTIL_MAP);
			}
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while inserting evaluation status :", aoAppEx);
			throw aoAppEx;
		}
		return lsEvaluationStatusID;
	}

	/**
	 * This method will delete the evaluation status by the evaluation status id
	 * 
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<String> fetchAcceptedProposalID(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into fetch Accepted ProposalID");
		Map<String, String> loInputParam = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		List<String> loProposalIdList = null;
		try
		{
			loProposalIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, loInputParam,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_ACCEPTED_PROPOSAL_ID,
					HHSP8Constants.JAVA_UTIL_MAP);
			LOG_OBJECT.Info("Fetch Accepted ProposalID" + loProposalIdList);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while inserting evaluation status :", aoAppEx);
			throw aoAppEx;
		}
		return loProposalIdList;
	}

	/**
	 * This method will fetch the contract details by the contractId Execute
	 * query id findContractDetailsByContractIdForWF
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public FinancialWFBean findContractDetailsByContractIdForWF(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into find Contract details by contract Id for WF");
		Map<String, String> loInputParam = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		String lsContractId = (String) loInputParam.get(HHSP8Constants.CONTRACT_ID_WORKFLOW);
		String lsContractIdNew = lsContractId;
		FinancialWFBean loFinancialWFBean = null;

		try
		{
			Map loContractInfo = new HashMap();
			loContractInfo = (HashMap) DAOUtil.masterDAO(aoMyBatisSession, lsContractId,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_CONTRACT_INFO,
					HHSP8Constants.JAVA_LANG_STRING);
			if (String.valueOf(loContractInfo.get(HHSP8Constants.FLS_CONTRACT_TYPE_ID)).equalsIgnoreCase(
					HHSP8Constants.TWO))
			{
				LOG_OBJECT.Info("Entered into if condition for contract type id equal to 2::");
				lsContractIdNew = String.valueOf(loContractInfo.get(HHSP8Constants.CONTRACT_ID_UNDERSCORE));
			}
			loInputParam.put(HHSP8Constants.CONTRACT_ID_WORKFLOW, lsContractIdNew);
			loFinancialWFBean = (FinancialWFBean) DAOUtil.masterDAO(aoMyBatisSession, loInputParam,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FIND_CONTRACT_DETAILS_WF,
					HHSP8Constants.JAVA_UTIL_MAP);
			if (String.valueOf(loContractInfo.get(HHSP8Constants.CONTRACT_SOURCE_ID)).equalsIgnoreCase(
					HHSP8Constants.TWO)
					&& (HHSP8Constants.NA_KEY.equalsIgnoreCase(loFinancialWFBean.getProcEpin()) || null == loFinancialWFBean
							.getProcEpin()))
			{
				LOG_OBJECT
						.Info("Entered into if condition for contract source id equal to 2 and Financial Proc EPIN N/A or null::");
				FinancialWFBean loFinWFBean = new FinancialWFBean();
				loFinWFBean = (FinancialWFBean) DAOUtil.masterDAO(aoMyBatisSession, lsContractIdNew,
						HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.FIND_PROC_EPIN_R3_CONTRACT_FOR_WF, HHSP8Constants.JAVA_LANG_STRING);
				if (null != loFinWFBean)
				{
					loFinancialWFBean.setProcEpin(loFinWFBean.getProcEpin());
				}
			}

			String lsCompetitionPoolTitle = (String) DAOUtil.masterDAO(aoMyBatisSession, lsContractIdNew,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_COMPETITION_POOL_TITLE,
					HHSP8Constants.JAVA_LANG_STRING);
			if (null != lsCompetitionPoolTitle)
			{
				loFinancialWFBean.setCompetitionPoolTitle(lsCompetitionPoolTitle);
			}
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while inserting evaluation status :", aoAppEx);
			throw aoAppEx;
		}
		return loFinancialWFBean;
	}

	/**
	 * This method will fetch the contract IDs by the ProcurementID
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException return list
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<String> fetchContractIdByProcurement(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into fetch Contract id by procurement");
		Map<String, String> loInputParam = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		List<String> loContractIdList = new ArrayList<String>();

		try
		{
			loContractIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, loInputParam,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_CONTRACT_ID,
					HHSP8Constants.JAVA_UTIL_MAP);
			LOG_OBJECT.Info("Entered into fetch Contract id by procurement:" + loContractIdList);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching contract IDs :", aoAppEx);
			throw aoAppEx;
		}
		return loContractIdList;
	}

	/**
	 * This method will fetch the contract IDs by the ProcurementID
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoMyBatisSession SqlSession object
	 * @param aoHashMap HashMap object
	 * @return Integer object
	 * @throws ApplicationException return Integer
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Integer fetchCountofSelectedProposals(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into fetch count of selected proposals");
		Map<String, String> loInputParam = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		Integer loProposalCount = 0;
		try
		{
			String lsEvalPoolMappingId = loInputParam.get(HHSP8Constants.EVAL_POOL_MAPPING_ID);
			if (null == lsEvalPoolMappingId)
			{
				loProposalCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loInputParam,
						HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.FETCH_SELECTED_PROPOSALS_COUNT, HHSP8Constants.JAVA_UTIL_MAP);
			}
			else
			{
				loProposalCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loInputParam,
						HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.GET_SEL_PROP_COUNT_FOR_EVAL_POOL_MAPPING_ID, HHSP8Constants.JAVA_UTIL_MAP);
			}
			LOG_OBJECT.Info("Entered into fetch count of selected proposals:" + loProposalCount);
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching contract IDs :", aoAppEx);
			throw aoAppEx;
		}
		return loProposalCount;
	}

	/**
	 * This method will fetch the contract IDs by the ProcurementID
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException return list
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<String> fetchReopenedEvaluationTaskIds(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into fetch Reopened Evaluation Task ids");
		Map<String, String> loInputParam = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		List<String> loEvaluationIdList = new ArrayList<String>();

		try
		{
			loEvaluationIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, loInputParam,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_REOPENED_EVALUATION_TASK_IDS, HHSP8Constants.JAVA_UTIL_MAP);
			LOG_OBJECT.Info("Reopened Evaluation Task ids:" + loEvaluationIdList);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching Task Id :", aoAppEx);
			throw aoAppEx;
		}
		return loEvaluationIdList;
	}

	/**
	 * This method will fetch the contract IDs by the ProcurementID
	 * 
	 * @param aoMyBatisSession
	 * @param aoHashMapForReturn return list
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<String> fetchEvaluationStatusIdReturned(SqlSession aoMyBatisSession, HashMap aoHashMapForReturn)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered into fetch Evaluation status id returned");
		Map<String, String> loInputParam = (HashMap) aoHashMapForReturn.get(HHSP8Constants.LOHMAP);
		List<String> loEvalIdList = new ArrayList<String>();

		try
		{
			loEvalIdList = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, loInputParam,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_REOPENED_EVALUATION_TASK_IDS, HHSP8Constants.JAVA_UTIL_MAP);
			LOG_OBJECT.Info("Entered into fetch Evaluation ids" + loEvalIdList);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching task ids with returned status :", aoAppEx);
			throw aoAppEx;
		}
		return loEvalIdList;
	}

	/**
	 * This method will delete the evaluation score by the evaluation status id
	 * Execute queries on eof them is deleteEvaluatorInternal
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoMyBatisSession SqlSession object
	 * @param aoHashMap HashMap object
	 * @return Boolean
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Boolean deleteEvaluatorData(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		Boolean loSuccess = false;
		String lsProcurementId = (String) aoHashMap.get(HHSP8Constants.PROCUREMENT_ID_KEY);
		String lsEvalFlag = (String) aoHashMap.get(HHSP8Constants.EVALUATOR_FLAG);
		LOG_OBJECT.Info("Entered into deleting evaluation score::" + lsProcurementId);

		Map<String, Object> loContextDataMap = new HashMap<String, Object>();
		try
		{
			if (lsEvalFlag.equalsIgnoreCase(HHSP8Constants.EXTERNAL_FLAG))
			{
				LOG_OBJECT.Info("Entered in if condition for Eval flag:" + lsEvalFlag);
				// Deleting Evaluation Score data against evaluationStatusId
				DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.DELETE_EVALUATOR_EXTERNAL, HHSP8Constants.JAVA_UTIL_HASHMAP);
				DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.UPDATE_EVALUATOR_EXTERNAL, HHSP8Constants.JAVA_UTIL_HASHMAP);
			}
			else if (lsEvalFlag.equalsIgnoreCase(HHSP8Constants.INTERNAL_FLAG))
			{
				LOG_OBJECT.Info("Entered in if condition for Eval flag:" + lsEvalFlag);
				// Deleting Evaluation Score data against evaluationStatusId
				DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.DELETE_EVALUATOR_INTERNAL, HHSP8Constants.JAVA_UTIL_HASHMAP);
				DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.UPDATE_EVALUATOR_INTERNAL, HHSP8Constants.JAVA_UTIL_HASHMAP);
			}
			else if (lsEvalFlag.equalsIgnoreCase(HHSP8Constants.INTERNAL_EXTERNAL_FLAG))
			{

				LOG_OBJECT.Info("Entered in if condition for Eval flag:" + lsEvalFlag);
				DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.DELETE_EVALUATOR_EXTERNAL, HHSP8Constants.JAVA_UTIL_HASHMAP);
				DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.UPDATE_EVALUATOR_EXTERNAL, HHSP8Constants.JAVA_UTIL_HASHMAP);
				DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.DELETE_EVALUATOR_INTERNAL, HHSP8Constants.JAVA_UTIL_HASHMAP);
				DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
						HHSP8Constants.UPDATE_EVALUATOR_INTERNAL, HHSP8Constants.JAVA_UTIL_HASHMAP);
			}

			loSuccess = true;
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(loContextDataMap);
			LOG_OBJECT.Error("Error while deleting evaluation data :", aoAppEx);
			throw aoAppEx;
		}
		return loSuccess;
	}

	/**
	 * This method will fetch the Accelerator Comments Execute query
	 * fetchAcceleratorComments
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException return strng
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public String fetchAcceleratorComments(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		String lsAccComments = null;
		LOG_OBJECT.Info("Entered in fetch accelerator comments");
		try
		{
			lsAccComments = (String) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_ACCELERATOR_COMMENTS,
					HHSP8Constants.JAVA_UTIL_MAP);
			LOG_OBJECT.Info("Entered in fetch accelerator comments:" + lsAccComments);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching contract IDs :", aoAppEx);
			throw aoAppEx;
		}
		return lsAccComments;
	}

	/**
	 * This method will fetch the Agency Comments Execute string
	 * fetchAgencyComments
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException return string
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public String fetchAgencyComments(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetch agency comments");
		String lsAgencyComments = null;

		try
		{
			lsAgencyComments = (String) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_AGENCY_COMMENTS,
					HHSP8Constants.JAVA_UTIL_MAP);
			LOG_OBJECT.Info("Entered in fetch agency comments:" + lsAgencyComments);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching contract IDs :", aoAppEx);
			throw aoAppEx;
		}
		return lsAgencyComments;
	}

	/**
	 * This method is used to get all the evaluator Ids
	 * <ul>
	 * <li>Get the procurement id from the request parameter</li>
	 * <li>Execute the query <code>fetchEvaluatorIdsEvaluation</code> from
	 * <code>component</code> mapper</li>
	 * <li>return the list of all evaluators configured for the procurement</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoMybatisSession sql session object
	 * @param aoParameterMap hash map with all required parameters
	 * @return list of evaluator Ids
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<String> fetchEvaluatorIdsEvaluation(SqlSession aoMybatisSession, HashMap aoParameterMap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetch evaluator ids for evaluation");
		List<String> loEvaluatorEmailList = new ArrayList<String>();
		try
		{

			loEvaluatorEmailList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, aoParameterMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_EVALUATOR_ID_EVALUATION,
					HHSP8Constants.JAVA_UTIL_HASHMAP);
			LOG_OBJECT.Info("Entered in fetch evaluator ids for evaluation, eval email list:" + loEvaluatorEmailList);

		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error while lFetching Evaluators List", aoAppExp);
			setMoState("Error while lFetching Evaluators List");
			HashMap<String, Object> aoHMContextData = new HashMap<String, Object>();
			aoHMContextData.put(HHSP8Constants.PROCUREMENT_ID_KEY, aoParameterMap);
			aoAppExp.setContextData(aoHMContextData);
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
		return loEvaluatorEmailList;
	}

	/**
	 * This method is used to get all the evaluator Ids
	 * <ul>
	 * <li>Get the proposal id from the request parameter</li>
	 * <li>Execute the query <code>fetchEvaluatorIdsEvaluation</code> from
	 * <code>component</code> mapper</li>
	 * <li>return the list of all evaluators configured for the proposal</li>
	 * </ul>
	 * @param aoMybatisSession sql session object
	 * @param aoParameterMap hash map with all required parameters
	 * @return list of evaluator Ids
	 * @throws ApplicationException If an ApplicationException occurs
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public List<String> fetchEvaluatorIdsReviewScore(SqlSession aoMybatisSession, HashMap aoParameterMap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetch evaluator ids for review score");
		List<String> loEvaluatorEmailList = new ArrayList<String>();
		String lsProposalId = null;
		try
		{
			lsProposalId = (String) aoParameterMap.get(HHSP8Constants.PROPOSAL_ID);
			LOG_OBJECT.Info("Entered in fetch evaluator ids for review score, proposal id:" + lsProposalId);

			loEvaluatorEmailList = (List<String>) DAOUtil.masterDAO(aoMybatisSession, lsProposalId,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_EVALUATOR_ID_REVIEW_SCORE, HHSP8Constants.JAVA_LANG_STRING);
			LOG_OBJECT.Info("Entered in fetch evaluator ids for review score, eval email list:" + loEvaluatorEmailList);

		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handled over here. It throws Application Exception back
		// to Controllers calling method through Transaction framework
		catch (ApplicationException aoAppExp)
		{
			LOG_OBJECT.Error("Error while lFetching Evaluators List", aoAppExp);
			setMoState("Error while lFetching Evaluators List");
			HashMap<String, Object> aoHMContextData = new HashMap<String, Object>();
			aoHMContextData.put(HHSP8Constants.PROPOSAL_ID_WF_KEY, lsProposalId);
			aoAppExp.setContextData(aoHMContextData);
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
		return loEvaluatorEmailList;
	}

	/**
	 * This method will fetch the contract IDs by the ProcurementID
	 * 
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException return boolean
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Boolean updateModifiedFlag(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{

		Boolean loUpdateFlag = false;
		LOG_OBJECT.Info("Entered in update modified flag");

		try
		{

			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.UPDATE_MODIFIED_FLAG, HHSP8Constants.JAVA_UTIL_MAP);
			loUpdateFlag = true;
			LOG_OBJECT.Info("Entered in update modified flag, flag true");
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while updateModifiedFlag :", aoAppEx);
			loUpdateFlag = false;
			throw aoAppEx;
		}
		return loUpdateFlag;
	}

	/**
	 * This method will fetch the count of the assignments linked to the invoice
	 * id and where the amount is grt then 0 Execute query fetchAssignments
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException return Integer
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public Integer fetchCountAssignments(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetch count assignments");
		Map<String, String> loInputParam = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		Integer loAssignmentsCount = 0;

		try
		{
			loAssignmentsCount = (Integer) DAOUtil.masterDAO(aoMyBatisSession, loInputParam,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_ASSIGNMENTS,
					HHSP8Constants.JAVA_UTIL_MAP);
			LOG_OBJECT.Info("Entered in fetch count assignments, count:" + loAssignmentsCount);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching contract IDs :", aoAppEx);
			throw aoAppEx;
		}
		return loAssignmentsCount;
	}

	/**
	 * This method will fetch the count of the assignments linked to the invoice
	 * id and where the amount is grt then 0
	 * 
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException return List
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public List<AssignementDetailsBean> fetchAssignments(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetch assignments");
		Map<String, String> loInputParam = (HashMap) aoHashMap.get(HHSP8Constants.LOHMAP);
		List<AssignementDetailsBean> loAssignmentList = new ArrayList<AssignementDetailsBean>();

		try
		{
			loAssignmentList = (List<AssignementDetailsBean>) DAOUtil.masterDAO(aoMyBatisSession, loInputParam,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_ASSIGNMENTS,
					HHSP8Constants.JAVA_UTIL_MAP);
			LOG_OBJECT.Info("Entered in fetch assignments, list" + loAssignmentList);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching Vendor IDs :", aoAppEx);
			throw aoAppEx;
		}
		return loAssignmentList;
	}

	/**
	 * This method inserts into the Payment Table Made changes in this method
	 * for Enhancement 6405 and Release 3.3.0
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @return string
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public String insertIntoPayment(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{

		LOG_OBJECT.Info("Entered in insert into payment");
		String lsPaymentId = null;
		try
		{
			// Made changes in this method for Enhancement 6405 and Release
			// 3.3.0
			// Changes reverted for Enhancement 6405 as a part of Release 3.3.1.
			// Release 3.6.0
			LOG_OBJECT.Debug("input map1" + aoHashMap);
			getVendorId(aoMyBatisSession, aoHashMap);
			LOG_OBJECT.Debug("input map2" + aoHashMap);
			getPaymentVendorId(aoMyBatisSession, aoHashMap);
			LOG_OBJECT.Debug("input map3" + aoHashMap);
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.INSERT_INTO_PAYMENT, HHSP8Constants.JAVA_UTIL_MAP);
			lsPaymentId = (String) aoHashMap.get(HHSP8Constants.PAYMENT_ID);
			LOG_OBJECT.Info("Entered in insert into payment, payment id" + lsPaymentId);
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while insertIntoPayment :", aoAppEx);
			throw aoAppEx;
		}
		return lsPaymentId;
	}

	/**
	 * Release 3.6.0
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void getPaymentVendorId(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		LinkedList<String> loPaymentVendorAddressId = HHSP8Constants.GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_COND;
		for (String lsQueryId : loPaymentVendorAddressId)
		{
			List<String> loPayeeVendorIdFromT = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, lsQueryId, HHSP8Constants.JAVA_UTIL_HASHMAP);
			if (loPayeeVendorIdFromT != null && !loPayeeVendorIdFromT.isEmpty())
			{
				LOG_OBJECT.Debug("payment vendor first list" + loPayeeVendorIdFromT);
				aoHashMap.put(HHSP8Constants.PAYMENT_VENDOR_ADDRESS_ID, loPayeeVendorIdFromT.get(0));
				break;
			}
		}
	}

	/**
	 * Release 3.6.0
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void getVendorId(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		LinkedList<String> loVendorAddressId = HHSP8Constants.GET_VENDOR_ADDRESS_ID_FROM_T_COND;
		for (String lsQueryId : loVendorAddressId)
		{
			List<String> loVendorIdFromT = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, lsQueryId, HHSP8Constants.JAVA_UTIL_HASHMAP);
			if (loVendorIdFromT != null && !loVendorIdFromT.isEmpty())
			{
				LOG_OBJECT.Debug("payment vendor first list" + loVendorIdFromT);
				aoHashMap.put(HHSP8Constants.VENDOR_ADDRESS_ID, loVendorIdFromT.get(0));
				break;
			}
		}
	}

	/**
	 * Changes reverted for Enhancement 6405 as a part of Release 3.3.1.
	 */
	/**
	 * Made changes in this method for Enhancement 6405 and Release 3.3.0 This
	 * method will get correct vendor to be inserted in payment table. Vendor id
	 * is fetched from the reference tables. Initially data is fetched from
	 * ref_vendor_t table. If no record is found then data is fetched from
	 * ref_vendor_v table.
	 * @param aoMyBatisSession batis session as input
	 * @param aoHashMap Map as input
	 * @throws ApplicationException Exception in case a query fails
	 */
	/*
	 * @SuppressWarnings({ "unchecked", "rawtypes" }) private void
	 * getVendorId(SqlSession aoMyBatisSession, HashMap aoHashMap) throws
	 * ApplicationException { List<String> loVendorIdFromTInitailFetch = null;
	 * List<String> loVendorIdFromT = (List<String>)
	 * DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
	 * HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
	 * HHSP8Constants.GET_VENDOR_ADDRESS_ID_FROM_T,
	 * HHSP8Constants.JAVA_UTIL_HASHMAP);
	 * 
	 * if(loVendorIdFromT!=null && !loVendorIdFromT.isEmpty() &&
	 * loVendorIdFromT.size()!=1) { loVendorIdFromTInitailFetch=loVendorIdFromT;
	 * loVendorIdFromT = (ArrayList<String>) DAOUtil.masterDAO(aoMyBatisSession,
	 * aoHashMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
	 * HHSP8Constants.GET_VENDOR_ADDRESS_ID_FROM_T_MORE_THAN_ONE,
	 * HHSP8Constants.JAVA_UTIL_HASHMAP); if(loVendorIdFromT==null ||
	 * loVendorIdFromT.isEmpty()) { loVendorIdFromT =
	 * loVendorIdFromTInitailFetch; } }
	 * 
	 * if(loVendorIdFromT==null || loVendorIdFromT.isEmpty()) { loVendorIdFromT
	 * = (ArrayList<String>) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
	 * HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
	 * HHSP8Constants.GET_VENDOR_ADDRESS_ID_FROM_V,
	 * HHSP8Constants.JAVA_UTIL_HASHMAP); } if(loVendorIdFromT!=null &&
	 * !loVendorIdFromT.isEmpty()) {
	 * aoHashMap.put(HHSP8Constants.VENDOR_ADDRESS_ID, loVendorIdFromT.get(0));
	 * } }
	 *//**
	 * Made changes in this method for Enhancement 6405 and Release 3.3.0 This
	 * method will get correct payment vendor to be inserted in payment table.
	 * Vendor id is fetched from the reference tables. Initially data is fetched
	 * from ref_vendor_t table. If no record is found then data is fetched from
	 * ref_vendor_v table.
	 * @param aoMyBatisSession batis session as input
	 * @param aoHashMap Map as input
	 * @throws ApplicationException Exception in case a query fails
	 */
	/*
	 * @SuppressWarnings({ "unchecked", "rawtypes" }) private void
	 * getPaymentVendorId(SqlSession aoMyBatisSession, HashMap aoHashMap) throws
	 * ApplicationException { List<String> loPaymentVendorIdFromTInitailFetch =
	 * null; List<String> loPaymentVendorIdFromT = (List<String>)
	 * DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
	 * HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
	 * HHSP8Constants.GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T,
	 * HHSP8Constants.JAVA_UTIL_HASHMAP); if(loPaymentVendorIdFromT!=null &&
	 * !loPaymentVendorIdFromT.isEmpty() && loPaymentVendorIdFromT.size()!=1) {
	 * loPaymentVendorIdFromTInitailFetch=loPaymentVendorIdFromT;
	 * loPaymentVendorIdFromT = (ArrayList<String>)
	 * DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
	 * HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
	 * HHSP8Constants.GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_T_MORE_THAN_ONE,
	 * HHSP8Constants.JAVA_UTIL_HASHMAP); if(loPaymentVendorIdFromT==null ||
	 * loPaymentVendorIdFromT.isEmpty()) { loPaymentVendorIdFromT =
	 * loPaymentVendorIdFromTInitailFetch; } } if(loPaymentVendorIdFromT==null
	 * || loPaymentVendorIdFromT.isEmpty()) { loPaymentVendorIdFromT =
	 * (ArrayList<String>) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
	 * HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
	 * HHSP8Constants.GET_PAYMENT_VENDOR_ADDRESS_ID_FROM_V,
	 * HHSP8Constants.JAVA_UTIL_HASHMAP); } if(loPaymentVendorIdFromT!=null &&
	 * !loPaymentVendorIdFromT.isEmpty()) {
	 * aoHashMap.put(HHSP8Constants.PAYMENT_VENDOR_ADDRESS_ID,
	 * loPaymentVendorIdFromT.get(0)); } }
	 */

	/**
	 * This method modified as a part of release 3.12.0 enhancement 6578
	 * This method inserts into the PaymentAllocation Table Execute query
	 * insertIntoPaymentAllocation
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @return boolean
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "deprecation" })
	public Boolean insertIntoPaymentAllocation(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in insert into payment allocation");
		Boolean loInsertFlag = false;
		String[] loPaymentIdArray = (String[]) aoHashMap.get(HHSP8Constants.PAYMENT_ID_ARRAY);
		String lsUserId = (String) aoHashMap.get(HHSP8Constants.CREATED_BY_USERID);
		Map loBudgetFiscalYearInformation = new HashMap();
		BigDecimal loBudgetFiscalYearId = new BigDecimal(0);
		BigDecimal loFiscalYearId = new BigDecimal(0);
		Map loPaymentIDtoBeUpdatedMap = new HashMap();	
		try
		{
			if (loPaymentIdArray != null)
			{
				for (String lsPaymentId : loPaymentIdArray)
				{
					HashMap<String, String> loPropertiesMap = new HashMap<String, String>();
					loPropertiesMap.put(HHSP8Constants.PAYMENT_ID, lsPaymentId);
					// changes done as a part of release 3.8.0 enhancement 6536
					// - start
					Date loMaxInterimPeriodEndDate;
					Calendar loCalendar = Calendar.getInstance();
					Date loSystemDate = loCalendar.getTime();
					LOG_OBJECT.Debug("fetching BudgetFiscalYearInformation with parameters :: " + lsPaymentId);
					loBudgetFiscalYearInformation = (Map) DAOUtil.masterDAO(aoMyBatisSession, lsPaymentId,
							HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
							HHSP8Constants.FETCH_BUDGET_FISCAL_YEAR_INFORMATION, HHSP8Constants.JAVA_LANG_STRING);
					if(null!=loBudgetFiscalYearInformation && !loBudgetFiscalYearInformation.isEmpty()){
						loBudgetFiscalYearId = (BigDecimal) loBudgetFiscalYearInformation.get(HHSP8Constants.BUDGET_FISCAL_YEAR_ID);
						loFiscalYearId = (BigDecimal) loBudgetFiscalYearInformation.get(HHSP8Constants.PAYMENT_FISCAL_YEAR_ID);
					}
					LOG_OBJECT.Debug("budget fiscal year id ::: " + loBudgetFiscalYearId + " and fiscal year id ::: "
							+ loFiscalYearId);
					Date loMaxInterimPeriodStartDate = new Date(loSystemDate.getYear(), 06, 01);
					LOG_OBJECT.Debug("fetching InterimPeriodDetails with parameters AND :: loMaxInterimPeriodStartDate"  + loMaxInterimPeriodStartDate);
					loMaxInterimPeriodEndDate = (Date) DAOUtil.masterDAO(aoMyBatisSession, null,
							HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, "selectMaxInterimPerionEndDate", null);
					LOG_OBJECT.Debug("fetched InterimPeriodDetails :: " + loMaxInterimPeriodEndDate);
					if (loSystemDate.compareTo(loMaxInterimPeriodStartDate) < 0 && loBudgetFiscalYearId.compareTo(loFiscalYearId) > 0)
					{
						LOG_OBJECT.Debug("Outside of Interim Period and budget fiscal year  greater than fiscal year  ::: "
								+ loBudgetFiscalYearInformation + "currentDate ::" + loSystemDate + "InterimPeriodStartDate ::" 
								+ loMaxInterimPeriodStartDate);
						loPropertiesMap.put(HHSP8Constants.CREATED_BY_USERID, lsUserId);
						loPropertiesMap.put(HHSP8Constants.MODIFIED_BY_USERID, lsUserId);
						DAOUtil.masterDAO(aoMyBatisSession, loPropertiesMap,
								HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
								HHSP8Constants.INSERT_INTO_PAYMENT_ALLOCATION_OUTSIDE_INTERIM_FOR_BFY_GREATER_THAN_FY_1,
								HHSP8Constants.JAVA_UTIL_MAP);
						LOG_OBJECT.Debug("Accounting lines inserted into Payment_Allocation successfully :: insertIntoPAOutsideInterimForBFYgreaterThanFY ");
					}
					else if (null!=loMaxInterimPeriodEndDate && loSystemDate.compareTo(loMaxInterimPeriodStartDate) >= 0 
							&& loSystemDate.compareTo(loMaxInterimPeriodEndDate) <= 0 && loBudgetFiscalYearId.compareTo(loFiscalYearId) == 0)
					{
						LOG_OBJECT.Debug("Interim Overlap period + budget fiscal year id greater than fiscal year id ::: "
								+ loBudgetFiscalYearInformation + "InterimPeriod ::" + loMaxInterimPeriodEndDate);
						loPropertiesMap.put(HHSP8Constants.CREATED_BY_USERID, lsUserId);
						loPropertiesMap.put(HHSP8Constants.MODIFIED_BY_USERID, lsUserId);
						DAOUtil.masterDAO(aoMyBatisSession, loPropertiesMap,
								HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
								HHSP8Constants.INSERT_INTO_PAYMENT_ALLOCATION_FOR_BFY_GREATER_THAN_FY,
								HHSP8Constants.JAVA_UTIL_MAP);
						LOG_OBJECT.Debug("Accounting lines inserted into Payment_Allocation successfully :: insertIntoPaymentAllocationForBFYgreaterThanFY  ");
					}
					else if (null!=loMaxInterimPeriodEndDate && loSystemDate.compareTo(loMaxInterimPeriodStartDate) >= 0 
							&& loSystemDate.compareTo(loMaxInterimPeriodEndDate) <= 0 && loFiscalYearId.compareTo(loBudgetFiscalYearId) > 0)
					{
						LOG_OBJECT.Debug("Interim Overlap period + budget fiscal year id greater than fiscal year id ::: "
								+ loBudgetFiscalYearInformation + "InterimPeriod ::" + loMaxInterimPeriodEndDate);
						loPropertiesMap.put(HHSP8Constants.CREATED_BY_USERID, lsUserId);
						loPropertiesMap.put(HHSP8Constants.MODIFIED_BY_USERID, lsUserId);
						DAOUtil.masterDAO(aoMyBatisSession, loPropertiesMap,
								HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
								HHSP8Constants.INSERT_INTO_PAYMENT_ALLOCATION_OUTSIDE_INTERIM_FOR_BFY_GREATER_THAN_FY,
								HHSP8Constants.JAVA_UTIL_MAP);
						LOG_OBJECT.Debug("Accounting lines inserted into Payment_Allocation successfully :: insertIntoPaymentAllocationForBFYgreaterThanFY  ");
					}
					else if (null!=loMaxInterimPeriodEndDate && loSystemDate.compareTo(loMaxInterimPeriodEndDate) >= 0 
							 && loBudgetFiscalYearId.compareTo(loFiscalYearId) > 0)
					{
						LOG_OBJECT.Debug("Interim Overlap period + budget fiscal year id greater than fiscal year id ::: "
								+ loBudgetFiscalYearInformation + "InterimPeriod ::" + loMaxInterimPeriodEndDate);
						loPropertiesMap.put(HHSP8Constants.CREATED_BY_USERID, lsUserId);
						loPropertiesMap.put(HHSP8Constants.MODIFIED_BY_USERID, lsUserId);
						DAOUtil.masterDAO(aoMyBatisSession, loPropertiesMap,
								HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
								HHSP8Constants.INSERT_INTO_PAYMENT_ALLOCATION_OUTSIDE_INTERIM_FOR_BFY_GREATER_THAN_FY_1,
								HHSP8Constants.JAVA_UTIL_MAP);
						LOG_OBJECT.Debug("Accounting lines inserted into Payment_Allocation successfully :: insertIntoPaymentAllocationForBFYgreaterThanFY  ");
					}
					//method added as a part of release 3.8.1 enhancement 6536 -- start
					else if (null!=loMaxInterimPeriodEndDate && loSystemDate.compareTo(loMaxInterimPeriodEndDate) >= 0 
							 && loBudgetFiscalYearId.compareTo(loFiscalYearId) == 0)
					{
						LOG_OBJECT.Debug("Interim Overlap period + budget fiscal year id greater than fiscal year id ::: "
								+ loBudgetFiscalYearInformation + "InterimPeriod ::" + loMaxInterimPeriodEndDate);
						loPropertiesMap.put(HHSP8Constants.CREATED_BY_USERID, lsUserId);
						loPropertiesMap.put(HHSP8Constants.MODIFIED_BY_USERID, lsUserId);
						DAOUtil.masterDAO(aoMyBatisSession, loPropertiesMap,
								HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
								HHSP8Constants.INSERT_INTO_PAYMENT_ALLOCATION,
								HHSP8Constants.JAVA_UTIL_MAP);
						LOG_OBJECT.Debug("Accounting lines inserted into Payment_Allocation successfully :: insertIntoPaymentAllocationForBFYgreaterThanFY  ");
					}
					else if (null!=loMaxInterimPeriodEndDate && loSystemDate.compareTo(loMaxInterimPeriodEndDate) >= 0 
							 && loFiscalYearId.compareTo(loBudgetFiscalYearId) > 0)
					{
						LOG_OBJECT.Debug("Interim Overlap period + budget fiscal year id greater than fiscal year id ::: "
								+ loBudgetFiscalYearInformation + "InterimPeriod ::" + loMaxInterimPeriodEndDate);
						loPropertiesMap.put(HHSP8Constants.CREATED_BY_USERID, lsUserId);
						loPropertiesMap.put(HHSP8Constants.MODIFIED_BY_USERID, lsUserId);
						DAOUtil.masterDAO(aoMyBatisSession, loPropertiesMap,
								HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
								HHSP8Constants.INSERT_INTO_PAYMENT_ALLOCATION_OUTSIDE_INTERIM_FOR_FY_GREATER_THAN_BFY,
								HHSP8Constants.JAVA_UTIL_MAP);
						LOG_OBJECT.Debug("Accounting lines inserted into Payment_Allocation successfully :: insertIntoPaymentAllocationForBFYgreaterThanFY  ");
					}
					//method added as a part of release 3.8.1 enhancement 6536 -- end
					else
					{
						LOG_OBJECT.Debug("budget fiscal year id is not greater than fiscal year id ::: "
								+ loBudgetFiscalYearInformation);
						loPropertiesMap.put(HHSP8Constants.CREATED_BY_USERID, lsUserId);
						loPropertiesMap.put(HHSP8Constants.MODIFIED_BY_USERID, lsUserId);
						DAOUtil.masterDAO(aoMyBatisSession, loPropertiesMap,
								HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
								HHSP8Constants.INSERT_INTO_PAYMENT_ALLOCATION, HHSP8Constants.JAVA_UTIL_MAP);
						LOG_OBJECT.Debug("Accounting lines inserted into Payment_Allocation successfully :: insertIntoPaymentAllocation");
					}
					// changes done as a part of release 3.8.0 enhancement 6536
					// - end
				}
				loInsertFlag = true;
				LOG_OBJECT.Info("Entered in insert into payment allocation, insert flag true");
			}
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while insertIntoPaymentAllocation :", aoAppEx);
			loInsertFlag = false;
			throw aoAppEx;
		}
		return loInsertFlag;
	}

	/**
	 * If start date of Contract Budget <= Today’s Date; Set the Process Flag to
	 * “Y” for all payment (i.e. PAYMENT.PROCESS_FLAG=1) Execute transaction
	 * updateProcessFlagOnPayment
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException return boolean
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Boolean updateProcessFlagOnPayment(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in updating process flag on payment");
		Boolean loUpdateFlag = false;
		try
		{
			DAOUtil.masterDAO(aoMyBatisSession, aoHashMap, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.UPDATE_PROCESS_FLAG_ON_PPAYMENT, HHSP8Constants.JAVA_UTIL_MAP);
			loUpdateFlag = true;
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while updateModifiedFlag :", aoAppEx);
			loUpdateFlag = false;
			throw aoAppEx;
		}
		return loUpdateFlag;
	}

	/**
	 * This method will fetch the fiscal year id for a Budget Id that will be
	 * inserted into the payment table
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException return Integer
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Integer fetchFisalYearId(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetch fiscal year id");
		Integer loFiscalYearId = 0;
		try
		{
			loFiscalYearId = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_FISCAL_YEAR_ID,
					HHSP8Constants.JAVA_UTIL_MAP);
			LOG_OBJECT.Info("Entered in fetch fiscal year id:" + loFiscalYearId);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching Fiscal Year IDs :", aoAppEx);
			throw aoAppEx;
		}
		return loFiscalYearId;
	}

	/**
	 * This method will fetch the budget id that will be inserted into the
	 * payment table
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException return string
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public String fetchBudgetId(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetch budget id");
		String lsBudgetId = null;
		try
		{
			lsBudgetId = (String) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_BUDGET_ID,
					HHSP8Constants.JAVA_UTIL_MAP);
			LOG_OBJECT.Info("Entered in fetch budget id:" + lsBudgetId);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching Budget ID :", aoAppEx);
			throw aoAppEx;
		}
		return lsBudgetId;
	}

	/**
	 * This method will fetch the Invoice Number that will be inserted into the
	 * payment table Executing query fetchInvoiceNumber
	 * @param aoMyBatisSession containing session details
	 * @param aoHashMap containing Invoice details
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public String fetchInvoiceNumber(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetch invoice no");
		String lsInvoiceNumber = null;
		try
		{
			lsInvoiceNumber = (String) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_INVOICE_NUMBER,
					HHSP8Constants.JAVA_UTIL_MAP);
			LOG_OBJECT.Info("Entered in fetch invoice no:" + lsInvoiceNumber);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching Invoice Number :", aoAppEx);
			throw aoAppEx;
		}
		return lsInvoiceNumber;
	}

	/**
	 * This method will fetch the Advance Number that will be inserted into the
	 * payment table Executing query fetchAdvanceNumber
	 * @param aoMyBatisSession containing session details
	 * @param aoHashMap containing Invoice details
	 * @throws ApplicationException return string
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public String fetchAdvanceNumber(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetch advance no");
		String lsInvoiceNumber = null;
		try
		{
			lsInvoiceNumber = (String) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_ADVANCE_NUMBER,
					HHSP8Constants.JAVA_UTIL_MAP);
			LOG_OBJECT.Info("Entered in fetch advance no:" + lsInvoiceNumber);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching Invoice Number :", aoAppEx);
			throw aoAppEx;
		}
		return lsInvoiceNumber;
	}

	/**
	 * This method will fetch the Amount that will be inserted into the payment
	 * table
	 * 
	 * @param aoMyBatisSession
	 * @param aoHashMap
	 * @throws ApplicationException return double
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Double fetchAmount(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetch amount");
		Double loAmount = null;
		String lsWorkFlow = (String) aoHashMap.get(HHSP8Constants.WORKFLOW);

		try
		{
			if (lsWorkFlow.equalsIgnoreCase(HHSP8Constants.WF305))
			{
				LOG_OBJECT.Info("Entered in fetch amount, if condition for WF305");
				loAmount = (Double) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
						HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_AMOUNT_WF305,
						HHSP8Constants.JAVA_UTIL_MAP);
			}
			else if (lsWorkFlow.equalsIgnoreCase(HHSP8Constants.WF307))
			{
				LOG_OBJECT.Info("Entered in fetch amount, if condition for WF307");
				loAmount = (Double) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
						HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_AMOUNT_WF307,
						HHSP8Constants.JAVA_UTIL_MAP);
			}
			LOG_OBJECT.Info("Entered in fetch amount, amount:" + loAmount);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching Invoice Number :", aoAppEx);
			throw aoAppEx;
		}
		return loAmount;
	}

	/**
	 * This method fetch email id required for notification specific for
	 * individual user.
	 * @param aoMyBatisSession SQL session
	 * @param aoReqdMap Notification HashMap
	 * @return HashMap object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean fetchEmailIdForNotification(SqlSession aoMyBatisSession, HashMap aoReqdMap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetch email id for notification");
		HashMap<String, Object> loNotificationMap = (HashMap) aoReqdMap.get(HHSP8Constants.LO_HM_NOTIFY_PARAM);
		ArrayList<String> loAlertsList = (ArrayList<String>) ((loNotificationMap)
				.get(HHSP8Constants.EVENT_ID_PARAMETER_NAME));
		String lsUserId = null;
		List<String> loAudienceList = null;
		if (null != loAlertsList
				&& (loAlertsList.contains(HHSP8Constants.NT305A) || loAlertsList.contains(HHSP8Constants.NT315A)
						|| loAlertsList.contains(HHSP8Constants.NT305B) || loAlertsList.contains(HHSP8Constants.NT315B)))
		{
			loAudienceList = (List) loNotificationMap.get(HHSP8Constants.USER_ID);
			if (null != loAudienceList && !loAudienceList.isEmpty())
			{
				lsUserId = loAudienceList.get(0);
				lsUserId = (String) DAOUtil.masterDAO(aoMyBatisSession, lsUserId,
						HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_EMAIL_ID,
						HHSP8Constants.JAVA_LANG_STRING);
				LOG_OBJECT.Info("Entered in fetch email id for notification, user id:" + lsUserId);
				loAudienceList = new ArrayList<String>();
				loAudienceList.add(lsUserId);
				loNotificationMap.put(HHSP8Constants.USER_ID, loAudienceList);
			}
		}
		LOG_OBJECT.Info("Entered in fetch email id for notification, true");
		return true;
	}

	/**
	 * This method get the base budget id from the modification budget id.
	 * @param aoMyBatisSession containing session id
	 * @param asModBudId containing Bud id
	 * @return HashMap Hash Map
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public HashMap fetchBaseBudIdFromModBudId(SqlSession aoMyBatisSession, HashMap aoBudMap)
			throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetch base budget from modification budget id");
		String lsModBudId = (String) (((HashMap) aoBudMap.get(HHSP8Constants.LOHMAP)).get(HHSP8Constants.BUDGET_ID));
		HashMap loOutputMap = new HashMap();
		LOG_OBJECT.Info("modification budget id:" + lsModBudId);
		String lsBaseBudId = (String) DAOUtil.masterDAO(aoMyBatisSession, lsModBudId,
				HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_BASE_BUD_FROM_MOD,
				HHSP8Constants.JAVA_LANG_STRING);
		LOG_OBJECT.Info("base budget id:" + lsBaseBudId);
		String lsBaseContractId = (String) DAOUtil.masterDAO(aoMyBatisSession, lsBaseBudId,
				HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_BASE_CONTRACT_ID,
				HHSP8Constants.JAVA_LANG_STRING);
		LOG_OBJECT.Info("base Contract id:" + lsBaseContractId);
		loOutputMap.put(HHSP8Constants.CONTRACT_ID_WORKFLOW, lsBaseContractId);
		loOutputMap.put(HHSP8Constants.BUDGET_ID, lsBaseBudId);
		return loOutputMap;
	}

	/**
	 * This method will fetch the procurement status based on procurement id
	 * @param aoMyBatisSession containing session details
	 * @param aoHashMap containing Invoice details
	 * @return procurement status
	 * @throws ApplicationException return string
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	public Integer fetchProcurementStatus(SqlSession aoMyBatisSession, HashMap aoHashMap) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetchProcurementStatus method ::");
		Integer liProcurementStatusId = 0;
		try
		{
			liProcurementStatusId = (Integer) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_PROCUREMENT_STATUS,
					HHSP8Constants.JAVA_UTIL_HASHMAP);
			LOG_OBJECT.Info("Entered in fetch advance no:" + liProcurementStatusId);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching procurement status :", aoAppEx);
			throw aoAppEx;
		}
		return liProcurementStatusId;
	}

	/**
	 * This method added for Payment Batch - Updating Accounting Line.
	 * This method fetched the max interim period end date from database.
	 * @param aoMyBatisSession containing session details
	 * @return
	 * @throws ApplicationException
	 */
	/**
	 * @param aoMyBatisSession
	 * @return
	 * @throws ApplicationException
	 */
	public Date selectMaxInterimPerionEndDate(SqlSession aoMyBatisSession) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in selectMaxInterimPerionEndDate method ::");
		Date loMaxInterimPeriodEndDate;
		try
		{
			loMaxInterimPeriodEndDate = (Date) DAOUtil.masterDAO(aoMyBatisSession, null,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.SELECT_MAX_INTERIM_PERIOD_END_DATE, null);
			LOG_OBJECT.Info("Max interim period end date fetched succssfully:" + loMaxInterimPeriodEndDate);

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while max interim period end date :", aoAppEx);
			throw aoAppEx;
		}
		return loMaxInterimPeriodEndDate;
	}

	/**
	 * This method added for Payment Batch - Updating Accounting Line.
	 * This method fetched payment allocation rows in status pending approvals
	 * @param aoMyBatisSession containing session details
	 * @return
	 * @throws ApplicationException
	 */
	public HashMap fetchUpdatePendingApprovalPayments(SqlSession aoMyBatisSession) throws ApplicationException
	{
		LOG_OBJECT.Info("Entered in fetchUpdatePendingApprovalPayments method ::");
		List<String> loPendingApprovedPaymentIds = null;
		HashMap loPayementIdMap = new HashMap();
		try
		{
			loPendingApprovedPaymentIds = (List<String>) DAOUtil.masterDAO(aoMyBatisSession, null,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER, HHSP8Constants.FETCH_PENDING_APPROVAL_PAYMENTS, null);
			int liCurrentYear = HHSUtil.GetFiscalYear();
			DAOUtil.masterDAO(aoMyBatisSession, liCurrentYear, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.UPDATE_PENDING_APPROVAL_PAYMENTS, HHSP8Constants.INTEGER_CLASS_PATH);
			DAOUtil.masterDAO(aoMyBatisSession, null, HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.DELETE_PENDING_APPROVAL_PAYMENTS, null);
			String[] stringArray = Arrays.copyOf(loPendingApprovedPaymentIds.toArray(),
					loPendingApprovedPaymentIds.toArray().length, String[].class);
			loPayementIdMap.put(HHSP8Constants.PAYMENT_ID_ARRAY, stringArray);
			loPayementIdMap.put(HHSP8Constants.CREATED_BY_USERID, HHSConstants.SYSTEM_USER);
			LOG_OBJECT.Info("Pending Approval Payment Ids fetched and updated succssfully:");

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while fetching and updating Pending Approval Payment Ids :", aoAppEx);
			throw aoAppEx;
		}
		return loPayementIdMap;
	}
	
	/** This method will launch Negotiation Workflow
	 * @param aoMybatisSession
	 * @param aoHashMap
	 * @return
	 */
	public Boolean launchNegotiationWorkFlow(SqlSession aoMybatisSession, HashMap aoHashMap)
	{
		return false;
	}
	// R5 changes starts
	/**
	 * This method will fetch the details for contract Execute
	 * transaction id fetchNegotiationContractDetails
	 *
	 * @param aoMyBatisSession SqlSession object
	 * @param aoHashMap HashMap object
	 * @return ContractDetailsBean Bean
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public ContractDetailsBean fetchNegotiationContractDetails(SqlSession aoMyBatisSession, HashMap aoHashMap)
			throws ApplicationException
	{
		ContractDetailsBean loContractDetail = null;
		LOG_OBJECT.Info("Entered into fetchNegotiationsContractsDetails::" + aoHashMap.toString());
		// Retrieve Contract detail beans
		try
		{
			loContractDetail = (ContractDetailsBean) DAOUtil.masterDAO(aoMyBatisSession, aoHashMap,
					HHSP8Constants.MAPPER_CLASS_HHS_P8_COMPONENT_MAPPER,
					HHSP8Constants.FETCH_NEGOTIATIONS_CONTRACT_DETAILS, HHSP8Constants.JAVA_UTIL_MAP);
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			aoAppEx.setContextData(aoHashMap);
			LOG_OBJECT.Error("Error while fetching proposal details for contracts id :", aoAppEx);
			throw aoAppEx;
		}
		return loContractDetail;
	}
}
