package com.nyc.hhs.component.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.EvaluationReassignDetailsBean;
import com.nyc.hhs.model.EvaluationStatusBean;
import com.nyc.hhs.model.Evaluator;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ProcessOperationForSolicitationFinancials;

import filenet.vw.api.VWSession;

/**
 * This class is responsible for the reassign evaluation operations that will be
 * called from the workflow.
 */
public class ReassignEvaluationsOperation
{

	public LogInfo aoLogObject = null;

	/**
	 * To initialize this class, the LogInfo object must be given to ensure
	 * proper logging. This value can not be null.
	 * @param LogInfo aoLogObject
	 */
	public ReassignEvaluationsOperation(LogInfo loLogObject)
	{
		aoLogObject = loLogObject;
	}

	/**
	 * This method retrieves the evaluation reassignment information this
	 * information will be returned in a list of evaluation reassign details
	 * beans.
	 * <ul>
	 * <li>Execute Transaction id fetchEvaluationReassignDetails</li>
	 * <li>Method in R4</li>
	 * </ul>
	 * @param asProcurementId procurement Id
	 * @param asInteralExternalFlag interal External Flag
	 * @param asEvalPoolMappingId Eval Pool MappingId
	 * @return List
	 * @throws ApplicationException If an Application Exception occurs Updated
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationReassignDetailsBean> retrieveEvaluationReassignmentInformation(String asProcurementId,
			String asInteralExternalFlag, String asEvalPoolMappingId) throws ApplicationException
	{
		aoLogObject.Debug("Entring retrieveEvaluationReassignmentInformation ,asProcurementId : " + asProcurementId
				+ "asInteralExternalFlag :" + asInteralExternalFlag);
		Channel loChannelObj = new Channel();
		HashMap<String, String> loHMap = new HashMap<String, String>();
		loHMap.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcurementId);
		loHMap.put(HHSP8Constants.EVAL_POOL_MAPPING_ID, asEvalPoolMappingId);
		loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);
		List<EvaluationReassignDetailsBean> loEvaluationDetailBean = null;

		try
		{

			// Based on the internalExternal flag execute the correct
			// transaction
			if (HHSP8Constants.INTERNAL_FLAG.equals(asInteralExternalFlag))
			{
				loHMap.put(HHSP8Constants.EVALUATOR_FLAG, HHSP8Constants.INTERNAL_FLAG);
			}
			else if (HHSP8Constants.EXTERNAL_FLAG.equals(asInteralExternalFlag))
			{
				loHMap.put(HHSP8Constants.EVALUATOR_FLAG, HHSP8Constants.EXTERNAL_FLAG);
			}
			else
			{
				aoLogObject.Error("Unexpected internalExternalFlag retrieved for procurementId:" + asProcurementId
						+ " interalExternalFlag:" + asInteralExternalFlag);
			}

			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_EVAL_REASSIGN__DETAILS);
			loEvaluationDetailBean = (List<EvaluationReassignDetailsBean>) loChannelObj
					.getData(HHSP8Constants.EVAL_REASSIGN_DETAILS);
			aoLogObject.Debug("ProcurementId:" + asProcurementId
					+ " - ProcurementBean initialized, evaluation information retrieved" + loEvaluationDetailBean);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			aoLogObject.Error("Error while retrieving reassignment information :", aoAppEx);
			throw aoAppEx;
		}
		return loEvaluationDetailBean;
	}

	/**
	 * This method is responsible for handling the evaluations reassignment for
	 * the given internal evaluators.
	 * 
	 * <ul>
	 * <li>Based on the delete flag that is given to an evaluator in the
	 * database, this method will create a new evaluation workflow, or will
	 * remove the existing evaluation workflow and data.</li>
	 * <li>Method in R4</li>
	 * 
	 * The creation of a new workflow will be done from the
	 * startEvaluationWorkflows method.
	 * @param aoPeSession PeSession
	 * @param asProcurementId Procurement Id
	 * @param aoEvaluationReassignDetailsBeans Evaluation Reassign Details Beans
	 * @param asEvalPoolMappingId Eval Pool Mapping Id
	 * @throws ApplicationException If an Application Exception occurs Updated
	 * 
	 */
	public void handleEvaluationInternalReassignment(VWSession aoPeSession, String asProcurementId,
			List<EvaluationReassignDetailsBean> aoEvaluationReassignDetailsBeans, String asEvalPoolMappingId)
			throws ApplicationException
	{
		aoLogObject.Debug("Entring handleEvaluationInternalReassignment ,asProcurementId : " + asProcurementId
				+ "aoEvaluationReassignDetailsBeans :" + aoEvaluationReassignDetailsBeans);
		String[] loProposalIds = null;
		ArrayList<String> loProposalIdsList = new ArrayList<String>();

		try
		{
			Channel loChannelObj = new Channel();
			HashMap<String, String> loHMap = new HashMap<String, String>();
			loHMap.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcurementId);
			loHMap.put(HHSP8Constants.EVAL_POOL_MAPPING_ID, asEvalPoolMappingId);
			loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);
			// For each EvaluationReassignDetailsBean
			for (EvaluationReassignDetailsBean loEval : aoEvaluationReassignDetailsBeans)
			{
				// Check if we need to do a delete or add operation
				String lsEvalSettingsIntId = loEval.getEvalSettingsIntExtId();
				String lsAddDeleteFlag = loEval.getAddDeleteFlag();
				aoLogObject.Debug("lsAddDeleteFlag : " + lsAddDeleteFlag);
				if (HHSP8Constants.DELETE_OPERATOR.equalsIgnoreCase(lsAddDeleteFlag))
				{
					// find evaluation status ids where the add delete flag is
					// set to delete
					List<String> loEvaluationStatusIds = getEvaluationStatusIds(lsEvalSettingsIntId,
							HHSP8Constants.INTERNAL_FLAG);
					handleCleanup(aoPeSession, loEvaluationStatusIds);

				}
				else if (HHSP8Constants.ADD_OPERATOR.equalsIgnoreCase(lsAddDeleteFlag))
				{
					// Create for all proposals linked to this procurement an
					// evaluation tasks for this specific evaluator

					// retrieve proposal conf information
					List<EvaluationStatusBean> loEvalStatusBeans = getEvaluationStatusBeans(asProcurementId,
							asEvalPoolMappingId);
					for (EvaluationStatusBean loEvalStatusBean : loEvalStatusBeans)
					{
						aoLogObject.Debug("In handleEvaluationInternalReassignment , loEvalStatusBean : "
								+ loEvalStatusBean);
						loEvalStatusBean.setEvalSettingsIntId(lsEvalSettingsIntId);
						loEvalStatusBean.setStatusId(HHSP8Constants.EVALUATE_PROPOSAL_TASK_STATUS_IN_REVIEW);
						loEvalStatusBean.setCreatedByUserId(HHSP8Constants.DEFAULT_SYSTEM_USER);
						loEvalStatusBean.setModifiedByUserId(HHSP8Constants.DEFAULT_SYSTEM_USER);

						loProposalIdsList.add(loEvalStatusBean.getProposalId());

						// Add evaluation status record
						insertEvaluationStatusBeansIntoDB(loEvalStatusBean);
					}

					loProposalIds = loProposalIdsList.toArray(new String[loProposalIdsList.size()]);

				}
				else
				{
					String lsMessage = "Unexpected addDeleteFlag retrieved for evalSettingsIntId:"
							+ lsEvalSettingsIntId + " addDeleteFlag:" + lsAddDeleteFlag;
					aoLogObject.Error(lsMessage);
				}
			}
			// Start || Added as a part of release 3.6.0 for enhancement request 5905
			if (null != aoEvaluationReassignDetailsBeans && !aoEvaluationReassignDetailsBeans.isEmpty())
			{
				startEvaluationWorkflows(aoPeSession, asProcurementId, HHSP8Constants.INTERNAL_FLAG,
						asEvalPoolMappingId);
			}
			// End || Added as a part of release 3.6.0 for enhancement request 5905
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			String lsMessage = "Exception occured on starting evaluation workflows for procurement id: "
					+ asProcurementId + " and proposalIds" + loProposalIds;
			aoLogObject.Error(lsMessage, aoAppEx);
			throw new ApplicationException(lsMessage, aoAppEx);
		}
		// handling Exception thrown by any action/resource
		catch (Exception aoExp)
		{
			String lsMessage = "Exception occured on starting evaluation workflows for procurement id: "
					+ asProcurementId + " and proposalIds" + loProposalIds;
			aoLogObject.Error(lsMessage, aoExp);
			throw new ApplicationException(lsMessage, aoExp);
		}
	}

	/**
	 * This method is responsible for handling the evaluations reassignment for
	 * the given external evaluators.
	 * <ul>
	 * <li>Based on the delete flag that is given to an evaluator in the
	 * database, this method will create a new evaluation workflow, or will
	 * remove the existing evaluation workflow and data.</li>
	 * 
	 * <li>The creation of a new workflow will be done from the
	 * startEvaluationWorkflows method.</li>
	 * <li>Method in R4</li>
	 * </ul>
	 * @param aoPeSession PeSession
	 * @param asProcurementId procurementId
	 * @param aoEvaluationReassignDetailsBeans evaluationReassignDetailsBeans
	 * @param asEvalPoolMappingId Eval Pool Mapping Id
	 * @throws ApplicationException If an Application Exception occurs Updated
	 * 
	 */
	public void handleEvaluationExternalReassignment(VWSession aoPeSession, String asProcurementId,
			List<EvaluationReassignDetailsBean> aoEvaluationReassignDetailsBeans, String asEvalPoolMappingId)
			throws ApplicationException
	{
		aoLogObject.Debug("Entring In handleEvaluationExternalReassignment , asProcurementId : " + asProcurementId
				+ "aoEvaluationReassignDetailsBeans :" + aoEvaluationReassignDetailsBeans);
		String[] loProposalIds = null;
		ArrayList<String> loProposalIdsList = new ArrayList<String>();

		try
		{

			// For each EvaluationReassignDetailsBean
			for (EvaluationReassignDetailsBean loEval : aoEvaluationReassignDetailsBeans)
			{
				// Check if we need to do a delete or add operation
				String lsEvalSettingsExtId = loEval.getEvalSettingsIntExtId();
				String lsAddDeleteFlag = loEval.getAddDeleteFlag();
				aoLogObject.Debug("In handleEvaluationExternalReassignment , addDeleteFlag :" + lsAddDeleteFlag);
				if (HHSP8Constants.DELETE_OPERATOR.equalsIgnoreCase(lsAddDeleteFlag))
				{
					// find evaluation status ids where the add delete flag is
					// set to delete
					List<String> loEvaluationStatusIds = getEvaluationStatusIds(lsEvalSettingsExtId,
							HHSP8Constants.EXTERNAL_FLAG);
					handleCleanup(aoPeSession, loEvaluationStatusIds);

				}
				else if (HHSP8Constants.ADD_OPERATOR.equalsIgnoreCase(lsAddDeleteFlag))
				{
					// Create for all proposals linked to this procurement an
					// evaluation tasks for this specific evaluator

					// retrieve proposal conf information
					List<EvaluationStatusBean> loEvalStatusBeans = getEvaluationStatusBeans(asProcurementId,
							asEvalPoolMappingId);
					aoLogObject.Debug("In handleEvaluationExternalReassignment , loEvalStatusBeans :"
							+ loEvalStatusBeans);
					for (EvaluationStatusBean loEvalStatusBean : loEvalStatusBeans)
					{
						loEvalStatusBean.setEvalSettingsExt(lsEvalSettingsExtId);
						loEvalStatusBean.setStatusId(HHSP8Constants.EVALUATE_PROPOSAL_TASK_STATUS_IN_REVIEW);
						loEvalStatusBean.setCreatedByUserId(HHSP8Constants.DEFAULT_SYSTEM_USER);
						loEvalStatusBean.setModifiedByUserId(HHSP8Constants.DEFAULT_SYSTEM_USER);

						loProposalIdsList.add(loEvalStatusBean.getProposalId());

						// Add evaluation status record
						insertEvaluationStatusBeansIntoDB(loEvalStatusBean);
					}

					loProposalIds = loProposalIdsList.toArray(new String[loProposalIdsList.size()]);
				}
				else
				{
					String lsMessage = "Unexpected addDeleteFlag retrieved for evalSettingsIntId:"
							+ lsEvalSettingsExtId + " addDeleteFlag:" + lsAddDeleteFlag;
					aoLogObject.Error(lsMessage);
				}

			}
			// Start || Added as a part of release 3.6.0 for enhancement request 5905
			if (null != aoEvaluationReassignDetailsBeans && !aoEvaluationReassignDetailsBeans.isEmpty())
			{
				startEvaluationWorkflows(aoPeSession, asProcurementId, HHSP8Constants.EXTERNAL_FLAG,
						asEvalPoolMappingId);
			}
			// End || Added as a part of release 3.6.0 for enhancement request 5905
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(aoAppEx.getMessage(), aoAppEx);
			// handling Exception thrown by any action/resource
		}
		catch (Exception aoExp)
		{
			String lsMessage = "Exception occured on starting evaluation workflows for procurement id: "
					+ asProcurementId + " and proposalIds" + loProposalIds;
			throw new ApplicationException(lsMessage, aoExp);
		}
	}

	/**
	 * Private class that handles the cleanup for all the evaluations ids. The
	 * following tasks will be executed: - evaluation score records are removed
	 * - evaluation status records are removed - evaluation workflows are
	 * removed
	 * 
	 * @param aoEvaluationStatusIds evaluationStatusIds
	 * @param aoPeSession Pe Session
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private void handleCleanup(VWSession aoPeSession, List<String> aoEvaluationStatusIds) throws ApplicationException
	{
		aoLogObject.Debug("Entring In handleCleanup , aoEvaluationStatusIds : " + aoEvaluationStatusIds);
		try
		{
			// For each evaluation status Id in the given list, delete the
			// following
			for (String lsEvaluationStatusId : aoEvaluationStatusIds)
			{
				deleteEvaluationScoreRecord(lsEvaluationStatusId);
				deleteEvaluationStatusRecord(lsEvaluationStatusId);
			}

			deleteEvaluationWorkflow(aoPeSession, aoEvaluationStatusIds);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the handleCleanup method in ReassignEvaluationsOperation", aoAppEx);
		}
	}

	/**
	 * Based on the evaluation settings internal or external id and the flag
	 * that determines if the evaluator table that needs to be checked is
	 * internal or external. A list of strings will be returned containing all
	 * concerning evaluation status ids.
	 * 
	 * Execute Transaction id fetchEvaluationStatusIdsByEvaluatorDetails
	 * @param asEvalSettingsIntExtId evalSettingsIntExtId
	 * @param asInteralExternalFlag interalExternalFlag
	 * @return List
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@SuppressWarnings("unchecked")
	private List<String> getEvaluationStatusIds(String asEvalSettingsIntExtId, String asInteralExternalFlag)
			throws ApplicationException
	{
		aoLogObject.Debug("Entring In getEvaluationStatusIds , asEvalSettingsIntExtId : " + asEvalSettingsIntExtId
				+ "asInteralExternalFlag :" + asInteralExternalFlag);
		Channel loChannelObj = new Channel();
		HashMap<String, String> loHMap = new HashMap<String, String>();

		loHMap.put(HHSP8Constants.EVALUATION_SETTINGS_INTERNAL_EXTERNAL_PROPERTY, asEvalSettingsIntExtId);
		List<String> loEvaluationStatusIds = null;

		try
		{

			// Based on the internal external flag retrieve the correct set of
			// evaluation ids
			if (HHSP8Constants.INTERNAL_FLAG.equals(asInteralExternalFlag))
			{
				loHMap.put(HHSP8Constants.EVALUATOR_FLAG, HHSP8Constants.INTERNAL_FLAG);
			}
			else if (HHSP8Constants.EXTERNAL_FLAG.equals(asInteralExternalFlag))
			{
				loHMap.put(HHSP8Constants.EVALUATOR_FLAG, HHSP8Constants.EXTERNAL_FLAG);
			}
			else
			{
				String lsMessage = "Unexpected internalExternalFlag retrieved for evaluationSettingsInternalExternalProperty:"
						+ asEvalSettingsIntExtId + " interalExternalFlag:" + asInteralExternalFlag;
				aoLogObject.Error(lsMessage);
				throw new ApplicationException(lsMessage);
			}
			loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_EVAL_STATUS_IDS);
			loEvaluationStatusIds = (List<String>) loChannelObj.getData(HHSP8Constants.EVALUATION_IDS);
			aoLogObject.Debug("Entring In getEvaluationStatusIds , loEvaluationStatusIds : " + loEvaluationStatusIds);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the getEvaluationStatusIds method in ReassignEvaluationsOperation",
					aoAppEx);
		}

		return loEvaluationStatusIds;
	}

	/**
	 * This method removes all the evaluation score records for the particular
	 * evaluation status id
	 * 
	 * Execute Transaction id deleteEvaluationScore
	 * @param asEvaluationStatusId evaluationStatusId
	 * @throws ApplicationException
	 */
	private void deleteEvaluationScoreRecord(String asEvaluationStatusId) throws ApplicationException
	{
		aoLogObject.Debug("Entring In deleteEvaluationScoreRecord , asEvaluationStatusId : " + asEvaluationStatusId);
		// Initialize the channel object
		Channel loChannelObj = new Channel();
		HashMap<String, String> loHMap = new HashMap<String, String>();
		loHMap.put(HHSP8Constants.EVALUATION_STATUS_ID, asEvaluationStatusId);
		loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);

		// Execute the transaction that will delete the record from the
		// evaluation score table
		try
		{
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.DELETE_EVALUATION_SCORE_TRANSACTION);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			String lsMsg = "Exception occured on executing transaction: "
					+ HHSP8Constants.DELETE_EVALUATION_SCORE_TRANSACTION;
			throw new ApplicationException(lsMsg, aoAppEx);
		}
	}

	/**
	 * This method removes all the evaluation status records for the particular
	 * evaluation status id
	 * 
	 * Execute Transaction id deleteEvaluationStatus
	 * @param asEvaluationStatusId evaluationStatusId
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private void deleteEvaluationStatusRecord(String asEvaluationStatusId) throws ApplicationException
	{
		aoLogObject.Debug("Entring In deleteEvaluationStatusRecord , asEvaluationStatusId : " + asEvaluationStatusId);
		// Initialize the channel object
		Channel loChannelObj = new Channel();
		HashMap<String, String> loHMap = new HashMap<String, String>();
		loHMap.put(HHSP8Constants.EVALUATION_STATUS_ID, asEvaluationStatusId);
		loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);

		// Execute the transaction that will delete the record from the
		// evaluation status table
		try
		{
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.DELETE_EVALUATION_STATUS_TRANSACTION);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			String lsMessage = "Exception occured on executing transaction: "
					+ HHSP8Constants.DELETE_EVALUATION_STATUS_TRANSACTION;
			throw new ApplicationException(lsMessage, aoAppEx);
		}
	}

	/**
	 * This method removes all the evaluation workflows for the particular
	 * evaluation status id
	 * @param aoPeSession Pe Session
	 * @param aoEvaluationStatusIds evaluationStatusId
	 * @throws ApplicationException If an Application Exception occurs
	 */
	private void deleteEvaluationWorkflow(VWSession aoPeSession, List<String> aoEvaluationStatusIds)
			throws ApplicationException
	{
		aoLogObject.Debug("Entring In deleteEvaluationWorkflow , aoEvaluationStatusIds : " + aoEvaluationStatusIds);
		// Set the filter and and workflow name property
		WorkflowOperations loWFop = new WorkflowOperations(aoLogObject);

		String[] loWorkflowNamesToRemove =
		{ HHSP8Constants.WORKFLOW_NAME_202_EVALUATION };
		String lsFilter = null;

		// Cancel the evaluation workflow
		try
		{
			for (String lsEvaluationStatusId : aoEvaluationStatusIds)
			{

				lsFilter = HHSP8Constants.WF_PROP_EVALUATION_ID + "='" + lsEvaluationStatusId + "'";
				loWFop.cancelWorkflows(lsFilter, loWorkflowNamesToRemove, aoPeSession);

			}
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			String lsTxt = "Exception occured on deleting evaluation workflows with filter " + lsFilter;
			throw new ApplicationException(lsTxt, aoAppEx);
		}
	}

	/**
	 * This method will create the transaction and will retrieve the evaluation
	 * status information by the given procurement id.
	 * <ul>
	 * <li>Execute Transaction fetchProposalConfigDetails</li>
	 * <li>Method in R4</li>
	 * </ul>
	 * @param procurementId procurement Id
	 * @param asEvalPoolMappingId Eval Pool Mapping Id
	 * @return List
	 * @throws ApplicationException If an Application Exception occurs Updated
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<EvaluationStatusBean> getEvaluationStatusBeans(String asProcurementId, String asEvalPoolMappingId)
			throws ApplicationException
	{
		aoLogObject.Debug("Entring In getEvaluationStatusBeans , asProcurementId : " + asProcurementId);
		// Initialize channel object
		Channel loChannelObj = new Channel();
		HashMap<String, String> loHMap = new HashMap<String, String>();
		loHMap.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcurementId);
		loHMap.put(HHSP8Constants.EVAL_POOL_MAPPING_ID, asEvalPoolMappingId);
		loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);

		List<EvaluationStatusBean> loEvalStatusBeans = null;
		// Execute the transaction that will retrieve the evaluation status
		// beans
		try
		{
			TransactionManager.executeTransaction(loChannelObj,
					HHSP8Constants.FETCH_PROPOSAL_CONFIG_DETAILS_TRANSACTION);
			loEvalStatusBeans = (List<EvaluationStatusBean>) loChannelObj.getData(HHSP8Constants.EVAL_STATUS_BEANS);
			// handling Application Exception thrown by any action/resource
			aoLogObject.Debug("In getEvaluationStatusBeans , loEvalStatusBeans : " + loEvalStatusBeans);
		}
		catch (ApplicationException aoAppEx)
		{
			String lsMessage = "Exception occured on executing transaction: "
					+ HHSP8Constants.FETCH_PROPOSAL_CONFIG_DETAILS_TRANSACTION;
			throw new ApplicationException(lsMessage, aoAppEx);
		}
		return loEvalStatusBeans;
	}

	/**
	 * This method will insert the evaluation status information into the
	 * database Execute Transaction id insertEvaluationStatus
	 * @param aoEvaluationStatusBean EvaluationStatusBean
	 * @throws ApplicationException If an Application Exception occurs
	 */
	public void insertEvaluationStatusBeansIntoDB(EvaluationStatusBean aoEvaluationStatusBean)
			throws ApplicationException
	{
		aoLogObject.Debug("Entring In insertEvaluationStatusBeansIntoDB , aoEvaluationStatusBean : "
				+ aoEvaluationStatusBean);
		// Initialize channel object
		Channel loChannelObj = new Channel();
		HashMap<String, EvaluationStatusBean> loHMap = new HashMap<String, EvaluationStatusBean>();
		loHMap.put(HHSP8Constants.EVALUATION_STATUS_BEAN, aoEvaluationStatusBean);
		loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);

		// Execute the transaction that will insert the evaluation status
		// information
		try
		{
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.INSERT_EVAL_STATUS_TRANSACTION);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			String lsMessage = "Exception occured on executing transaction: "
					+ HHSP8Constants.INSERT_EVAL_STATUS_TRANSACTION;
			throw new ApplicationException(lsMessage, aoAppEx);
		}

	}

	/**
	 * This method is responsible for starting the evaluation workflows. Based
	 * on the given procurment id it will retrieve the proposal ids and the
	 * procurement information. For each proposal it will retrieve the proposal
	 * information And for each evaluation (internal and external) per proposal
	 * it will generate a new evaluation workflow by using the
	 * P8ProcessOperationForSolicitationFinancials.launchWorkflow method Execute
	 * <ul>
	 * <li>Execute transaction id fetchRequiredInformation</li>
	 * <li>Updated Method in R4</li>
	 * </ul>
	 * @param asProcurementId Procurement Id
	 * @param aoPeSession Pe Session
	 * @param asIntExtBothFlag Internal/External Flag
	 * @param asEvalPoolMappingId Eval Pool Mapping Id
	 * @return array of Strings
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public String[] startEvaluationWorkflows(VWSession aoPeSession, String asProcurementId, String asIntExtBothFlag,
			String asEvalPoolMappingId) throws ApplicationException
	{
		aoLogObject.Debug("Entring In startEvaluationWorkflows , asProcurementId : " + asProcurementId
				+ " asIntExtBothFlag:" + asIntExtBothFlag);
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(aoLogObject);
		ArrayList<String> loEvalWFWobNumbers = new ArrayList<String>();
		String lsWobNumber = null;
		try
		{
			Channel loChannel = new Channel();
			HashMap<String, String> loInputParam = new HashMap<String, String>();
			loInputParam.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcurementId);
			loInputParam.put(HHSP8Constants.EVAL_POOL_MAPPING_ID, asEvalPoolMappingId);
			loChannel.setData(HHSP8Constants.LOHMAP, loInputParam);
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_REQUIRED_INFORMATION);
			Procurement loProcurementBean = (Procurement) loChannel
					.getData(HHSP8Constants.GET_PROCUREMENT_SUMMARY_DATA);
			List<ProposalDetailsBean> loProposalDetailsBeans = (List<ProposalDetailsBean>) loChannel
					.getData(HHSP8Constants.GET_PROPOSAL_DETAILS_BEAN_LIST);
			aoLogObject.Debug(" In startEvaluationWorkflows , loProposalDetailsBeans : " + loProposalDetailsBeans);
			HashMap loTaskRequiredProps = new HashMap();
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROCUREMENT_TITLE,
					loProcurementBean.getProcurementTitle());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROCURMENT_ID, loProcurementBean.getProcurementId());
			loTaskRequiredProps
					.put(HHSP8Constants.PROPERTY_PE_PROCUREMENT_EPIN, loProcurementBean.getProcurementEpin());
			if (null != loProcurementBean.getFirstEvalCompletionDateUpdated())
			{
				loTaskRequiredProps.put(HHSP8Constants.PE_WORKFLOW_FIRST_ROUND_EVAL_DATE,
						loProcurementBean.getFirstEvalCompletionDateUpdated());
			}
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID, asEvalPoolMappingId);
			String lsAgencyId = loProcurementBean.getAgencyId();

			for (ProposalDetailsBean loProposalDetailsBean : loProposalDetailsBeans)
			{
				aoLogObject.Debug("Retrieving proposal title: " + loProposalDetailsBean.getProposalTitle());
				loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROPOSAL_ID, loProposalDetailsBean.getProposalId());
				loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROPOSAL_TITLE,
						loProposalDetailsBean.getProposalTitle());
				loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROPOSAL_PROVIDERNAME,
						loProposalDetailsBean.getProviderName());
				loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROPOSAL_ORGANIZATIONID,
						loProposalDetailsBean.getOrganizationId());
				loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_COMPETITION_POOL_TITLE,
						loProposalDetailsBean.getCompetitionPoolTitle());
				loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_EVAL_GRP_TITLE,
						loProposalDetailsBean.getEvaluationGroupTitle());
				loTaskRequiredProps.put(HHSP8Constants.IS_OPEN_ENDED_RFP, loProcurementBean.getIsOpenEndedRFP());
				// retrieve internal evaluator information
				loChannel = new Channel();
				loChannel.setData(HHSP8Constants.TASK_PROPS_MAP, loTaskRequiredProps);
				HashMap loHMap1 = new HashMap();
				loHMap1.put(HHSP8Constants.AGENCY_ID_KEY, lsAgencyId);
				loHMap1.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcurementId);
				loHMap1.put(HHSP8Constants.EVAL_POOL_MAPPING_ID, asEvalPoolMappingId);
				loChannel.setData(HHSP8Constants.LOHMAP, loHMap1);
				String lsEvaluationStatusId = null;

				if (HHSP8Constants.INTERNAL_EXTERNAL_FLAG.equalsIgnoreCase(asIntExtBothFlag)
						|| HHSP8Constants.INTERNAL_FLAG.equalsIgnoreCase(asIntExtBothFlag))
				{
					// hit the transaction to get the internal evaluator
					// agency list
					TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_INTERNAL_EVALUATOR_USERS);
					List<Evaluator> loInternalEvaluatorList = (List<Evaluator>) loChannel
							.getData(HHSP8Constants.INTERNAL_EVALUATOR_LIST);

					for (Evaluator loInternalEvaluator : loInternalEvaluatorList)
					{
						aoLogObject.Info("Retrieving internal evaluator: " + loInternalEvaluator.getName());
						// create evaluation task for internal user
						lsEvaluationStatusId = loWorkflowOperations.retrieveEvaluationStatusId(
								loProposalDetailsBean.getProposalId(), loInternalEvaluator.getEvalSettingId(),
								HHSP8Constants.INTERNAL_FLAG);
						aoLogObject.Info("evaluationStatusId---------------------" + lsEvaluationStatusId);

						if (null != lsEvaluationStatusId && !("").equals(lsEvaluationStatusId))
						{
							loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_AGENCY_ID,
									loInternalEvaluator.getAgencyId());
							loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_ASSIGNED_TO,
									loInternalEvaluator.getUserId());
							loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_ASSIGNED_TO_NAME,
									loInternalEvaluator.getName());
							loTaskRequiredProps.put(HHSP8Constants.EVALUATION_STATUS_ID_KEY, lsEvaluationStatusId);
							// Verify the workflow name, previous value was:
							// "WF202 - Evaluate Proposal"
							lsWobNumber = new P8ProcessOperationForSolicitationFinancials().launchWorkflow(aoPeSession,
									HHSP8Constants.WORKFLOW_NAME_202_EVALUATION, loTaskRequiredProps,
									HHSP8Constants.DEFAULT_SYSTEM_USER);
							loEvalWFWobNumbers.add(lsWobNumber);

							aoLogObject.Info("List of WobNumbers: " + loEvalWFWobNumbers.toString());
						}
					}

				}

				// retrieve external evaluator information
				if (HHSP8Constants.INTERNAL_EXTERNAL_FLAG.equalsIgnoreCase(asIntExtBothFlag)
						|| HHSP8Constants.EXTERNAL_FLAG.equalsIgnoreCase(asIntExtBothFlag))
				{
					// hit the transaction to get the internal evaluator
					// agency list
					TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_EXTERNAL_EVALUATOR_USERS);
					List<Evaluator> loExternalEvaluatorList = (List<Evaluator>) loChannel
							.getData(HHSP8Constants.EXTERNAL_EVALUATOR_LIST);

					for (Evaluator loExternalEvaluator : loExternalEvaluatorList)
					{
						aoLogObject.Info("Retrieving external evaluator: " + loExternalEvaluator.getName());
						lsEvaluationStatusId = loWorkflowOperations.retrieveEvaluationStatusId(
								loProposalDetailsBean.getProposalId(), loExternalEvaluator.getEvalSettingId(),
								HHSP8Constants.EXTERNAL_FLAG);
						aoLogObject.Info("evaluationStatusId---------------------" + lsEvaluationStatusId);
						if (null != lsEvaluationStatusId && !lsEvaluationStatusId.isEmpty())
						{
							loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_AGENCY_ID,
									loExternalEvaluator.getAgencyId());
							loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_ASSIGNED_TO,
									loExternalEvaluator.getAgencyUserId());
							loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_ASSIGNED_TO_NAME,
									loExternalEvaluator.getName());
							loTaskRequiredProps.put(HHSP8Constants.EVALUATION_STATUS_ID_KEY, lsEvaluationStatusId);
							// Verify the workflow name, previous value was:
							// "WF202 - Evaluate Proposal"
							lsWobNumber = new P8ProcessOperationForSolicitationFinancials().launchWorkflow(aoPeSession,
									HHSP8Constants.WORKFLOW_NAME_202_EVALUATION, loTaskRequiredProps,
									HHSP8Constants.DEFAULT_SYSTEM_USER);
							loEvalWFWobNumbers.add(lsWobNumber);

							aoLogObject.Info("List of WobNumbers: " + loEvalWFWobNumbers.toString());
						}
					}

				}
			}
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			aoLogObject.Error(
					"Exception occured while executing startEvaluationWorkflows method in the HHSComponentOperations",
					aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the startEvaluationWorkflows method in HHSComponentOperations",
					aoAppEx);
			// handling Exception thrown by any action/resource
		}
		catch (Exception aoExp)
		{
			aoLogObject.Error(
					"Exception occured while executing startEvaluationWorkflows method in the HHSComponentOperations"
							+ loEvalWFWobNumbers.toString(), aoExp);
		}

		String[] loWobNumbers = new String[loEvalWFWobNumbers.size()];

		return loEvalWFWobNumbers.toArray(loWobNumbers);

	}

}
