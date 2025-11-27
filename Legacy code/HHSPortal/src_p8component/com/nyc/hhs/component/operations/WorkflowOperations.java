package com.nyc.hhs.component.operations;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

//import com.filenet.wcm.toolkit.util.WcmException;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.ContractDetailsBean;
import com.nyc.hhs.model.FinancialWFBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ProcessOperationForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;

import filenet.vw.api.VWException;
import filenet.vw.api.VWFetchType;
import filenet.vw.api.VWRoster;
import filenet.vw.api.VWRosterQuery;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;
import filenet.vw.api.VWWorkObject;

/**
 * This class contains the generic workflow methods *
 */
public class WorkflowOperations
{

	private LogInfo aoLogInfo = null;

	public WorkflowOperations(LogInfo aoLogInfoObject)
	{
		aoLogInfo = aoLogInfoObject;
	}

	/**
	 * Cancel workflow method by giving the workflow names and the filter string
	 * 
	 * @param filter
	 * @param workflowNames
	 * @param aoPeSession
	 * @throws ApplicationException
	 */
	public void cancelWorkflows(String asFilter, String[] aoWorkflowNames, VWSession aoPeSession)
			throws ApplicationException
	{
		try
		{
			VWRoster loVWRoster = aoPeSession.getRoster(HHSP8Constants.ROSTER_NAME);
			VWRosterQuery loRQuery = loVWRoster.createQuery(null, null, null, 0, asFilter, null,
					VWFetchType.FETCH_TYPE_WORKOBJECT);

			// For each query result
			if (loRQuery.hasNext())
			{
				while (loRQuery.hasNext())
				{
					VWWorkObject loWob = (VWWorkObject) loRQuery.next();

					String lsWorkflowName = loWob.getWorkflowName();
					String lsWorkflowId = loWob.getWorkflowNumber();

					// Verify if the workflow is of the correct type
					for (String lsGivenWorkflowName : aoWorkflowNames)
					{
						if (lsWorkflowName.equals(lsGivenWorkflowName))
						{
							loWob.doLock(true);
							loWob.doTerminate();
							aoLogInfo.Debug("Workflow: " + lsWorkflowName + "-" + lsWorkflowId + " terminated.");
						}
					}
				}
			}
			else
			{
				aoLogInfo.Debug("No workflows found for filter: " + asFilter);
			}
		}
		catch (VWException aoVWExp)
		{
			aoLogInfo.Error(aoVWExp.getMessage(), aoVWExp);
			throw new ApplicationException(aoVWExp.getMessage(), aoVWExp);
		}
	}

	/**
	 * A generic method that is able to change the visiblity status for workflow
	 * found by the given workflow
	 * @param filter
	 * @param visibility
	 * @param asWfName
	 * @throws ApplicationException
	 * @throws WcmException
	 * @throws VWException
	 */
	public void triggerWorkflowStepVisilibity(VWSession aoPeSession, String asFilter, Boolean abVisibility,
			String asWfName, String asNewTaskStatus) throws ApplicationException
	{
		aoLogInfo.Debug("Entered triggerWorkflowStepVisilibity method with parameters : " + asFilter + "  : "
				+ abVisibility + " : " + asWfName + " : " + asNewTaskStatus);
		Date loDate = new Date();
		String lsDateStr = null;
		// With he given workflow names, find the workflow class ids. Because
		// the workflow class ids are index, we can search much faster through
		// the PE

		final boolean lbCompareValue = true;

		try
		{
			SimpleDateFormat loSimpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
			// Search for the current user
			VWRoster loVwroster = aoPeSession.getRoster(HHSP8Constants.ROSTER_NAME);
			VWRosterQuery loRQuery = loVwroster.createQuery(null, null, null, 0, asFilter, null,
					VWFetchType.FETCH_TYPE_WORKOBJECT);
			lsDateStr = loSimpleDateFormat.format(loDate);
			aoLogInfo.Debug("Entered triggerWorkflowStepVisilibity starting if condition :" + loRQuery);
			if (loRQuery.hasNext())
			{
				aoLogInfo.Debug("Entered triggerWorkflowStepVisilibity in if condition ");
				while (loRQuery.hasNext())
				{
					VWWorkObject loWorkObject = (VWWorkObject) loRQuery.next();
					aoLogInfo.Debug("Entered triggerWorkflowStepVisilibity While condition with workflow name "
							+ loWorkObject.getWorkflowName());
					if (asWfName.equalsIgnoreCase(loWorkObject.getWorkflowName()))
					{
						aoLogInfo
								.Debug("Entered triggerWorkflowStepVisilibity if condition & now making task visiblity true");
						loWorkObject.doLock(true);
						loWorkObject.setFieldValue(HHSP8Constants.TASKVISIBILITY, Boolean.toString(abVisibility),
								lbCompareValue);
						loWorkObject.setFieldValue(HHSP8Constants.TASK_MODIFIED_DATE, lsDateStr, lbCompareValue);
						loWorkObject.setFieldValue(HHSP8Constants.LAUNCH_DATE, lsDateStr, lbCompareValue);
						if (asNewTaskStatus != null && !asNewTaskStatus.isEmpty())
						{
							loWorkObject.setFieldValue(HHSP8Constants.TASK_STATUS, asNewTaskStatus, lbCompareValue);
						}
						loWorkObject.doSave(true);
					}

				}
			}
			else
			{
				aoLogInfo.Info("No workflows found for filter: " + asFilter);
			}
		}
		catch (VWException aoVWExp)
		{
			throw new ApplicationException(
					"Error occured during executing the triggerWorkflowStepVisilibity method in WorkflowOperations",
					aoVWExp);
		}
		catch (Exception aoExp)
		{
			throw new ApplicationException(
					"Error occured during executing the triggerWorkflowStepVisilibity method in WorkflowOperations",
					aoExp);
		}
	}

	/**
	 * This method will find all the workflowids on the roster for a certain
	 * filter The return arraylist will contain all the workflowids of the
	 * fetched workflows.
	 * 
	 * @param filter
	 * @param aoWorkflowNames
	 * @return found workflow ids
	 * @throws ApplicationException
	 */
	public ArrayList<String> findWorkflowIds(VWSession aoPeSession, String asFilter, Set<String> aoWorkflowNames)
			throws ApplicationException
	{
		ArrayList<String> loFoundWobsArrayList = new ArrayList<String>();
		try
		{
			// Search for the current user
			VWRoster loVwroster = aoPeSession.getRoster(HHSP8Constants.ROSTER_NAME);
			VWRosterQuery loRQuery = loVwroster.createQuery(null, null, null, VWRoster.QUERY_MAX_VALUES_INCLUSIVE,
					asFilter, null, VWFetchType.FETCH_TYPE_WORKOBJECT);

			if (loRQuery.hasNext())
			{
				while (loRQuery.hasNext())
				{
					VWWorkObject loWorkObject = (VWWorkObject) loRQuery.next();
					for (String lsWorkflowName : aoWorkflowNames)
					{
						if (lsWorkflowName.equalsIgnoreCase(loWorkObject.getWorkflowName()))
						{
							loFoundWobsArrayList.add(loWorkObject.getWorkObjectNumber());
						}
					}
				}
			}
			else
			{
				aoLogInfo.Info("No workflows found for filter: " + asFilter);
			}
		}
		catch (VWException aoVWExp)
		{
			throw new ApplicationException(
					"Error occured during executing the findWorkflowIds method in WorkflowOperations", aoVWExp);
		}
		return loFoundWobsArrayList;
	}

	/**
	 * Method responsible for removing workflows
	 * 
	 * @param workflowIds
	 * @throws ApplicationException
	 */
	public void cancelWorkflows(String[] aoWorkflowIds) throws ApplicationException
	{

		try
		{

			VWSession loPeSession = null;
			P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
			loPeSession = loFilenetConnection.getPESession(loFilenetConnection.setP8SessionVariables());

			// For each given workflow id, look up the workflow object and
			// terminate the workflow.
			for (String lsWorkflowId : aoWorkflowIds)
			{
				VWRoster loVwroster = loPeSession.getRoster(HHSP8Constants.ROSTER_NAME);
				VWRosterQuery loRQuery = loVwroster.createQuery(null, null, null, 0, "F_WorkFlowNumber='"
						+ lsWorkflowId + "'", null, VWFetchType.FETCH_TYPE_WORKOBJECT);

				if (loRQuery.hasNext())
				{
					do
					{
						VWWorkObject loWorkObject = (VWWorkObject) loRQuery.next();
						boolean lbOverrideLock = true;
						loWorkObject.doLock(lbOverrideLock);
						loWorkObject.doTerminate();
						aoLogInfo.Info("Workflow: " + lsWorkflowId + " terminated.");

					}
					while (loRQuery.hasNext());
				}
				else
				{
					aoLogInfo.Error("Workflow with id " + lsWorkflowId + " not found.");
				}
			}
		}
		catch (VWException aoVWExp)
		{
			throw new ApplicationException(
					"Error occured during executing the cancelWorkflows method in WorkflowOperations", aoVWExp);
		}
	}

	/**
	 * Start a single workflow with the given workflow name and the given
	 * parameters
	 * @param asWorkFlowName
	 * @param aoTaskRequiredProps
	 * @return String
	 * @throws ApplicationException
	 */
	public String startWorkflow(String asWorkFlowName, HashMap<String, String> aoTaskRequiredProps)
			throws ApplicationException
	{
		// Initialze process engine session
		VWSession loPeSession = null;
		P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
		loPeSession = loFilenetConnection.getPESession(loFilenetConnection.setP8SessionVariables());
		String lsWobNumber = null;

		try
		{
			// Log the workflow properties that will be set on the workflow
			Set<String> loKeySet = aoTaskRequiredProps.keySet();
			if (loKeySet != null)
			{
				for (String lsKeyName : loKeySet)
				{
					aoLogInfo
							.Info("Workflow property: " + lsKeyName + " - value:" + aoTaskRequiredProps.get(lsKeyName));
				}
			}
			// Start the workflow
			lsWobNumber = new P8ProcessOperationForSolicitationFinancials().launchWorkflow(loPeSession, asWorkFlowName,
					aoTaskRequiredProps, HHSP8Constants.DEFAULT_SYSTEM_USER);

		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the startWorkflow method in WorkflowOperations", aoAppEx);
		}

		aoLogInfo.Info("Workflow: " + asWorkFlowName + " started, workflowID:" + lsWobNumber);

		return lsWobNumber;
	}

	/**
	 * Start a multiple workflows with the given workflow name and the given
	 * parameters. Not that only multiple workflows with the same workflow
	 * definition can be started.
	 * 
	 * @param asWorkFlowName
	 * @param aoTaskRequiredPropsList
	 * @return loWobNumberList
	 * @throws ApplicationException
	 */
	public ArrayList<String> startMultipleWorkflow(String asWorkFlowName,
			List<HashMap<String, String>> aoTaskRequiredPropsList) throws ApplicationException
	{
		ArrayList<String> loWobNumberList = new ArrayList<String>();
		VWSession loPeSession = null;
		P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
		loPeSession = loFilenetConnection.getPESession(loFilenetConnection.setP8SessionVariables());

		try
		{
			// Go through all the workflows
			for (HashMap<String, String> loTaskRequiredProps : aoTaskRequiredPropsList)
			{

				// Log the workflow properties that will be set on the workflow
				Set<Entry<String, String>> loKeySet = loTaskRequiredProps.entrySet();
				for (Entry<String, String> lsKeyName : loKeySet)
				{
					aoLogInfo.Info("Workflow property: " + lsKeyName.getKey() + " - value:" + lsKeyName.getValue());
				}
				// Start the workflow
				String lsWobNumber = new P8ProcessOperationForSolicitationFinancials().launchWorkflow(loPeSession,
						asWorkFlowName, loTaskRequiredProps, HHSP8Constants.DEFAULT_SYSTEM_USER);
				loWobNumberList.add(lsWobNumber);
				aoLogInfo.Info("Workflow: " + asWorkFlowName + " started, workflowID:" + lsWobNumber);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the startMultipleWorkflow method in WorkflowOperations", aoAppEx);
		}

		return loWobNumberList;
	}

	/**
	 * Accept proposal workflows will be started For each proposal in the
	 * loProposalDetailBeanList variable an new workflow will start
	 * 
	 * WF parameters will be set: AgencyID ProcurementEPin ProcurementID
	 * ProcurementTitle ProposalID ProposalTitle ProviderID ProviderName
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param aoPeSession aoPeSession object
	 * @param procurementId string containing ProcurementId
	 * @param asWorkFlowName string containing WorkFlowName
	 * @param aoProposalDetailBeanList list of ProposalDetailsBean
	 * @param asLaunchBy string containing LaunchBy name
	 * @param asEvalPoolMappingId string containing EvalPoolMappingId
	 * @return ArrayList<String> Workflow ids of the started workflows
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String[] startProposalRelatedWorkflows(VWSession aoPeSession, String asProcurementId, String asWorkFlowName,
			List<ProposalDetailsBean> aoProposalDetailBeanList, String asLaunchBy, String asEvalPoolMappingId)
			throws ApplicationException
	{
		ArrayList<String> loWobslist = new ArrayList<String>();
		HashMap loTaskRequiredProps = new HashMap();
		try
		{
			// Setup PE session
			aoLogInfo.Debug("ProcurementId:" + asProcurementId + " - VWSession established");
			aoLogInfo.Debug("EvalPoolMappingId:" + asEvalPoolMappingId + " - VWSession established");
			// Retrieve procurement information
			fetchProcurementInformation(asProcurementId, loTaskRequiredProps);

			// Loop through the proposal beans, for each proposal bean start a
			// new workflow
			for (ProposalDetailsBean loProposalDetailBean : aoProposalDetailBeanList)
			{
				String lsProposalId = loProposalDetailBean.getProposalId();
				aoLogInfo.Debug("ProcurementId:" + asProcurementId + " - Retrieving propsal id: " + lsProposalId);

				// Add the proposal information to the required properties map
				loTaskRequiredProps.put(HHSP8Constants.PROPOSAL_ID, lsProposalId);
				loTaskRequiredProps.put(HHSP8Constants.PROPOSAL_TITLE, loProposalDetailBean.getProposalTitle());
				loTaskRequiredProps.put(HHSP8Constants.PROVIDER_ID, loProposalDetailBean.getOrganizationId());
				loTaskRequiredProps.put(HHSP8Constants.PROVIDER_NAME, loProposalDetailBean.getProviderName());
				loTaskRequiredProps.put(HHSP8Constants.PE_WORKFLOW_PROCUREMENT_ID, asProcurementId);
				loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_SUBMITTED_BY, asLaunchBy);
				loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_COMPETITION_POOL_TITLE,
						loProposalDetailBean.getCompetitionPoolTitle());
				loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_EVAL_GRP_TITLE,
						loProposalDetailBean.getEvaluationGroupTitle());
				if (null != loProposalDetailBean.getEvaluationPoolMappingId()
						&& !loProposalDetailBean.getEvaluationPoolMappingId().equalsIgnoreCase(""))
				{
					loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID,
							loProposalDetailBean.getEvaluationPoolMappingId());
				}
				else
				{
					loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID, asEvalPoolMappingId);
				}
				// Log all the properties that will be pushed to the workflow
				aoLogInfo.Info("ProcurementId:" + asProcurementId + " PropsalId: " + lsProposalId + " - Starting "
						+ asWorkFlowName);
				aoLogInfo.Info("ProcurementId:" + asProcurementId + " PropsalId: " + lsProposalId
						+ " - Printing launch properties:");

				Set<Entry<String, String>> loKeySet = loTaskRequiredProps.entrySet();
				for (Entry<String, String> lsKeyName : loKeySet)
				{
					aoLogInfo.Info("ProcurementId:" + asProcurementId + " PropsalId: " + lsProposalId + " - "
							+ lsKeyName.getValue() + ":" + lsKeyName.getValue());
				}

				// Launch the workflow and retrieve the workflow id from the
				// started workflow
				String lsWobNumber = new P8ProcessOperationForSolicitationFinancials().launchWorkflow(aoPeSession,
						asWorkFlowName, loTaskRequiredProps, HHSP8Constants.DEFAULT_SYSTEM_USER);
				aoLogInfo.Info("ProcurementId:" + asProcurementId + " PropsalId: " + lsProposalId + " - "
						+ asWorkFlowName + " started, workflowID:" + lsWobNumber);
				loWobslist.add(lsWobNumber);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the startProposalRelatedWorkflows method in WorkflowOperations",
					aoAppEx);
		}
		// Create a string[] for the started workflow ids to return
		String[] loAPwobs = loWobslist.toArray(new String[loWobslist.size()]);

		return loAPwobs;
	}

	/**
	 * Retrieve procurement information and store it in the given properties
	 * hasmap <li>This method was updated in R4</li>
	 * @param asProcurementId string containing ProcurementId
	 * @param aoTaskRequiredProps HashMap object
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void fetchProcurementInformation(String asProcurementId, HashMap aoTaskRequiredProps)
			throws ApplicationException
	{
		try
		{
			// Retrieve procurement information and store the required
			// information in the loTaskRequiredProps hashmap
			Procurement loProcurementBean = retrieveProcurementInformation(asProcurementId);
			aoTaskRequiredProps.put(HHSP8Constants.PROCUREMENT_TITLE, loProcurementBean.getProcurementTitle());
			aoTaskRequiredProps.put(HHSP8Constants.AGENCY_ID, loProcurementBean.getAgencyId());
			aoTaskRequiredProps.put(HHSP8Constants.PROCUREMENT_ID, loProcurementBean.getProcurementId());
			aoTaskRequiredProps.put(HHSP8Constants.PROCUREMENT_EPIN, loProcurementBean.getProcurementEpin());
			if (null != loProcurementBean.getFinalEvalCompletionDateUpdated())
			{
				aoTaskRequiredProps.put(HHSP8Constants.FINALIZE_EVALUATION_DATE,
						loProcurementBean.getFinalEvalCompletionDateUpdated());
			}
			if (null != loProcurementBean.getAwardSelectionDateUpdated())
			{
				aoTaskRequiredProps.put(HHSP8Constants.AWARD_SELECTION_DATE,
						loProcurementBean.getAwardSelectionDateUpdated());
			}
			aoTaskRequiredProps.put(HHSP8Constants.IS_OPEN_ENDED_RFP, loProcurementBean.getIsOpenEndedRFP());
		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the fetchProcurementInformation method in WorkflowOperations",
					aoAppEx);
		}
	}

	/**
	 * Retrieve the procurement object containing all procurement details for
	 * the given procurement Id. <li>The transaction used is:
	 * fetchProcurementSummary</li>
	 * @param procurementId
	 * @return Procurement
	 * @throws ApplicationException
	 */
	public Procurement retrieveProcurementInformation(String asProcurementId) throws ApplicationException
	{
		// Building the channel object with the required parameters
		Channel loChannelObj = new Channel();
		HashMap<String, String> loHMap = new HashMap<String, String>();
		loHMap.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcurementId);
		loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);
		Procurement loProcurementBean = null;
		aoLogInfo.Debug("ProcurementId:" + asProcurementId + " - Channel object initialized");
		aoLogInfo.Debug("ProcurementId:" + asProcurementId + " - Start retrieving procurement information");
		try
		{

			// Execute the transaction
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_PROCUREMENT_SUMMARY_TRANSACTION);

			aoLogInfo.Debug("ProcurementId:" + asProcurementId + " - Transaction executed");

			// Fetch the information from the channel object
			loProcurementBean = (Procurement) loChannelObj.getData(HHSP8Constants.GET_PROCUREMENT_SUMMARY_DATA);
			aoLogInfo.Debug("ProcurementId:" + asProcurementId
					+ " - ProcurementBean initialized, procurement information retrieved");
		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the retrieveProcurementInformation method in WorkflowOperations",
					aoAppEx);
		}

		return loProcurementBean;
	}

	/**
	 * Retrieve the proposal information for each proposal that is bound to the
	 * given procurement. <li>This method was updated in R4</li> <li>The
	 * transaction used is: fetchMultipleProposalsDetails</li>
	 * @param procurementId string containing ProcurementId
	 * @param asEvalGroupId string containing Evaluation Group Id
	 * @return List<ProposalDetailsBean> list of ProposalDetailsBean
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<ProposalDetailsBean> retrieveProposalsInformation(String asProcId, String asEvalGroupId)
			throws ApplicationException
	{
		// Building the channel object with the required parameters
		List<ProposalDetailsBean> loProposalDetailsBeanList = null;
		Channel loChannelObjForProposal = new Channel();
		HashMap<String, String> loHMapForProposal = new HashMap<String, String>();
		loHMapForProposal.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcId);
		loHMapForProposal.put(HHSP8Constants.EVAL_GROUP_ID, asEvalGroupId);
		loChannelObjForProposal.setData(HHSP8Constants.LOHMAP, loHMapForProposal);

		aoLogInfo.Debug("ProcurementId:" + asProcId + " - Start retrieving proposal information");

		try
		{
			// Execute the transaction
			TransactionManager.executeTransaction(loChannelObjForProposal,
					HHSP8Constants.FETCH_MULTIPLE_PROPOSAL_DETAILS_TRANSACTION);

			// Fetch the information from the channel object
			loProposalDetailsBeanList = (List<ProposalDetailsBean>) loChannelObjForProposal
					.getData(HHSP8Constants.GET_PROPOSAL_DETAILS_BEAN_LIST);
			aoLogInfo.Debug("ProcurementId:" + asProcId + " - Proposal information retrieved");
		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the retrieveProposalsInformation method in WorkflowOperations",
					aoAppEx);
		}
		return loProposalDetailsBeanList;
	}

	/**
	 * Retrieve the proposal information for each proposal that is bound to the
	 * given procurement. <li>The transaction used is:
	 * fetchMultipleProposalsDetailsByProposal</li>
	 * @param proposalIds
	 * @return List<ProposalDetailsBean>
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<ProposalDetailsBean> retrieveProposalsInformation(String[] aoProposalIds,
			String asEvaluationPoolMappingId) throws ApplicationException
	{
		// Initialize the channel object
		Channel loChannelProsObj = new Channel();
		HashMap<String, String> loHMapForProposal = new HashMap<String, String>();
		List<ProposalDetailsBean> loProposalDetailsBeanList = null;
		// Build the query string for the in statement
		StringBuffer loPropsQuery = new StringBuffer();
		loPropsQuery.append("(");
		for (String lsProposalId : aoProposalIds)
		{
			loPropsQuery.append(lsProposalId);
			loPropsQuery.append(",");
		}

		loPropsQuery.deleteCharAt(loPropsQuery.length() - 1);
		loPropsQuery.append(")");

		String lsProposalsQueryString = loPropsQuery.toString();

		loHMapForProposal.put(HHSP8Constants.PROPOSAL_ID_KEY, lsProposalsQueryString);
		loHMapForProposal.put(HHSP8Constants.EVAL_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		loChannelProsObj.setData(HHSP8Constants.LOHMAP, loHMapForProposal);
		aoLogInfo.Debug("ProposalIds:" + lsProposalsQueryString + " - Start retrieving proposal information");

		try
		{
			// Execute the transaction for retrieving the proposals details
			TransactionManager.executeTransaction(loChannelProsObj, HHSP8Constants.FETCH_MULTIPLE_PROPOSAL_DETAILS);

			loProposalDetailsBeanList = (List<ProposalDetailsBean>) loChannelProsObj
					.getData(HHSP8Constants.GET_PROPOSAL_DETAILS_BEAN_LIST);
			aoLogInfo.Debug("ProposalIds:" + lsProposalsQueryString + " - Proposal information retrieved");
		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the retrieveProposalsInformation method in WorkflowOperations",
					aoAppEx);
		}
		return loProposalDetailsBeanList;
	}

	/**
	 * Retrieve the contract information for each proposal that is bound to the
	 * given procurement. <li>This method was updated in R4</li> <li>The
	 * transaction used is: fetchMultipleContractDetails</li>
	 * @param asProcurementId string containing ProcurementId
	 * @param asEvaluationPoolMappingId string containing
	 *            EvaluationPoolMappingId
	 * @return List<ProposalDetailsBean> list of ProposalDetailsBean
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public List<ContractDetailsBean> retrieveContractsInformation(String asProcurementId,
			String asEvaluationPoolMappingId, boolean abIsNegotiation) throws ApplicationException
	{
		Channel loChannelObj = new Channel();
		HashMap<String, String> loHMapForProposal = new HashMap<String, String>();
		List<ContractDetailsBean> loContractDetailsBeanList = null;
		// Added for R5
		if (abIsNegotiation)
		{
			loHMapForProposal.put(HHSR5Constants.IS_NEGOTIATION_REQUIRED, HHSConstants.FALSE);
		}
		// End of R5
		loHMapForProposal.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcurementId);
		loHMapForProposal.put(HHSP8Constants.EVAL_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		loChannelObj.setData(HHSP8Constants.LOHMAP, loHMapForProposal);
		try
		{
			// Execute the transaction to retrieve the contract details for
			// multiple contracts
			aoLogInfo.Debug("ProcurementId:" + asProcurementId + "Evaluation Pool Mapping Id:"
					+ asEvaluationPoolMappingId + " - Start retrieving contract information");
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_MULTIPLE_CONTRACTS_TRANSACTION);
			// Retrieve the requested data from the channel
			loContractDetailsBeanList = (List<ContractDetailsBean>) loChannelObj
					.getData(HHSP8Constants.CONTRACT_DETAILS_BEAN_LIST);
			aoLogInfo.Debug("ProcurementId:" + asProcurementId + " - Contract information retrieved");
		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the retrieveContractsInformation method in WorkflowOperations",
					aoAppEx);
		}
		return loContractDetailsBeanList;
	}

	/**
	 * This method closes all Filenet PE database resources
	 * 
	 * @param aoResultSet Result Set object
	 * @param aoStatement Statement object
	 */
	public void closeFilenetDBResources(ResultSet aoResultSet, Statement aoStatement)
	{
		try
		{
			if (null != aoResultSet)
			{
				aoResultSet.close();
			}
		}
		catch (SQLException aoSQLEx)
		{
			aoLogInfo.Error("Exception while closing Result Set()::", aoSQLEx);
		}
		try
		{
			if (null != aoStatement)
			{
				aoStatement.close();
			}
		}
		catch (SQLException aoSQLEx)
		{
			aoLogInfo.Error("Exception while closing Statement()::", aoSQLEx);
		}

	}

	/**
	 * This method will start the contract related workflow containing the
	 * contract and procurement information. <li>This method was updated in R4</li>
	 * <li>The transaction used is: insertMultipleTaskAudit</li>
	 * @param aoPeSession VWSession object
	 * @param asProcurementId string containing ProcurementId
	 * @param asWorkFlowName string containing WorkFlowName
	 * @param aoContractDetailBeanList list of ContractDetailsBean
	 * @param abFirstRound Boolean value
	 * @param asTaskOwner string containing Task Owner name
	 * @param asEvaluationPoolMappingId string containing
	 *            EvaluationPoolMappingId
	 * @param asCompetitionPoolTitle string containing CompetitionPoolTitle
	 * @return String[] WorkflowIds of the started workflows
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String[] startContractRelatedWorkflows(VWSession aoPeSession, String asProcurementId, String asWorkFlowName,
			List<ContractDetailsBean> aoContractDetailBeanList, boolean abFirstRound, String asTaskOwner,
			String asEvaluationPoolMappingId, String asCompetitionPoolTitle) throws ApplicationException
	{
		aoLogInfo.Debug("Entered startContractRelatedWorkflows with WorkflowName::" + asWorkFlowName);
		ArrayList<String> aoWobslist = new ArrayList<String>();
		HashMap loTaskRequiredProps = new HashMap();
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(aoLogInfo);
		int liReviewLevel = HHSP8Constants.INT_ZERO;
		String[] loAPwobs = null;
		try
		{
			// Retrieve the procurement information.
			loWorkflowOperations.fetchProcurementInformation(asProcurementId, loTaskRequiredProps);
			// R5 Change starts
			if (!asWorkFlowName.equalsIgnoreCase(HHSP8Constants.WORKFLOW_NAME_204_NEGOTIATION_AWARD))
			{
				liReviewLevel = retrieveReviewLevel(
						(String) loTaskRequiredProps.get(HHSP8Constants.PROPERTY_PE_AGENCY_ID),
						HHSP8Constants.FINANCIAL_WF_ID_MAP.get(asWorkFlowName));
				loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_TASK_TOTAL_LEVEL, liReviewLevel);
			}
			// R5 Change starts
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_COMPETITION_POOL_TITLE, asCompetitionPoolTitle);
			// Load the PE session
			aoLogInfo.Debug("ProcurementId:" + asProcurementId + " - VWSession established");
			List<HhsAuditBean> loHhsAuditBeanList = new ArrayList<HhsAuditBean>();
			// Loop through all the contract detail beans and for each contract
			// start a new workflow.
			getHhsAuditBeanList(aoPeSession, asProcurementId, asWorkFlowName, aoContractDetailBeanList, abFirstRound,
					asTaskOwner, asEvaluationPoolMappingId, aoWobslist, loTaskRequiredProps, loHhsAuditBeanList);
			if (null != loHhsAuditBeanList && !loHhsAuditBeanList.isEmpty())
			{
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.AUDIT_BEAN_LIST, loHhsAuditBeanList);
				loChannel.setData(HHSP8Constants.TRANSACTION_PARAM_COMPONENT_AUDITSTATUS, true);
				TransactionManager.executeTransaction(loChannel, HHSP8Constants.INSERT_MULTIPLE_TASK_AUDIT);
			}
			// Prepare the ids of the started workflows to be returned as a
			// String[]
			loAPwobs = aoWobslist.toArray(new String[aoWobslist.size()]);
		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the startContractRelatedWorkflows method in WorkflowOperations",
					aoAppEx);
		}
		aoLogInfo.Debug("Exited startContractRelatedWorkflows with WorkflowId(s)::" + loAPwobs);
		return loAPwobs;
	}

	/**
	 * This method set HhsAuditBean
	 * 
	 * @param aoPeSession
	 * @param asProcurementId
	 * @param asWorkFlowName
	 * @param aoContractDetailBeanList
	 * @param abFirstRound
	 * @param asTaskOwner
	 * @param asEvaluationPoolMappingId
	 * @param apWobslist
	 * @param loTaskRequiredProps
	 * @param loHhsAuditBeanList
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private void getHhsAuditBeanList(VWSession aoPeSession, String asProcurementId, String asWorkFlowName,
			List<ContractDetailsBean> aoContractDetailBeanList, boolean abFirstRound, String asTaskOwner,
			String asEvaluationPoolMappingId, ArrayList<String> apWobslist, HashMap loTaskRequiredProps,
			List<HhsAuditBean> loHhsAuditBeanList) throws ApplicationException
	{
		for (ContractDetailsBean loContractDetailBean : aoContractDetailBeanList)
		{
			String lsContractId = loContractDetailBean.getContractId();
			// R5 Change starts
			if (asWorkFlowName.equalsIgnoreCase(HHSP8Constants.WORKFLOW_NAME_204_NEGOTIATION_AWARD))
			{
				loTaskRequiredProps.put(P8Constants.PROPERTY_PE_ENTITY_ID, lsContractId);
			}
			// R5 Change ends
			aoLogInfo.Debug("ProcurementId:" + asProcurementId + " - Retrieving contract id: " + lsContractId);
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROCURMENT_ID, asProcurementId);
			loTaskRequiredProps.put(HHSP8Constants.FIRST_ROUND, String.valueOf(abFirstRound));
			loTaskRequiredProps.put(HHSP8Constants.CONTRACT_ID, lsContractId);
			loTaskRequiredProps.put(HHSP8Constants.CONTRACT_TITLE, loContractDetailBean.getContractTitle());
			loTaskRequiredProps.put(HHSP8Constants.CONTRACT_NUMBER, loContractDetailBean.getContractNumber());
			loTaskRequiredProps.put(HHSP8Constants.CONTRACT_CONFIGURATOIN_ID,
					loContractDetailBean.getContractConfigurationId());
			loTaskRequiredProps.put(HHSP8Constants.PROVIDER_ID, loContractDetailBean.getProviderId());
			loTaskRequiredProps.put(HHSP8Constants.PROVIDER_NAME, loContractDetailBean.getProviderName());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_SUBMITTED_BY, asTaskOwner);
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID, asEvaluationPoolMappingId);
			// add reviewlevel to the list. this information must be
			// retrieved from ...

			// Log all the values that will be pushed to the workflow
			aoLogInfo.Info("ProcurementId:" + asProcurementId + " PropsalId: " + lsContractId + " - Starting "
					+ asWorkFlowName);
			aoLogInfo.Info("ProcurementId:" + asProcurementId + " PropsalId: " + lsContractId
					+ " - Printing launch properties:");

			Set<Entry<String, Object>> loKeySet = loTaskRequiredProps.entrySet();
			for (Entry<String, Object> lsKeyName : loKeySet)
			{
				aoLogInfo.Info("ProcurementId:" + asProcurementId + " PropsalId: " + lsContractId + " - "
						+ lsKeyName.getKey() + ":" + lsKeyName.getValue());
			}

			// Start the workflow and store the workflow Id
			String loWobNumber = new P8ProcessOperationForSolicitationFinancials().launchWorkflow(aoPeSession,
					asWorkFlowName, loTaskRequiredProps, asTaskOwner);
			aoLogInfo.Info("ProcurementId:" + asProcurementId + " ProposalId: " + lsContractId + " - " + asWorkFlowName
					+ " started, workflowID:" + loWobNumber);
			apWobslist.add(loWobNumber);

			HhsAuditBean loHhsAuditBean = new HhsAuditBean();
			// R5 Change starts
			if (asWorkFlowName.equalsIgnoreCase(HHSP8Constants.WORKFLOW_NAME_302_CONTRACT_CONFIGURATION))
			{
				loHhsAuditBean.setEntityType(HHSConstants.TASK_CONTRACT_CONFIGURATION);
				loHhsAuditBean.setEntityId(lsContractId);
				loHhsAuditBean.setEventType(HHSConstants.PROPERTY_TASK_CREATION_EVENT);
				loHhsAuditBean.setData(HHSConstants.PROPERTY_TASK_CREATION_DATA);
				// Added for R5
			}
			else
			{
				loHhsAuditBean.setEntityType(HHSR5Constants.FINALIZE_AWARD_AMOUNT);
				loHhsAuditBean.setEventType(HHSR5Constants.FINALIZE_AWARD_AMOUNT);
				loHhsAuditBean.setData(HHSR5Constants.TASK_ASSIGNED_TO_UNASSIGNED_ACCO);
				loHhsAuditBean.setEntityId(lsContractId);
			}
			loHhsAuditBean.setContractId(lsContractId);
			loHhsAuditBean.setAuditTableIdentifier(HHSConstants.AGENCY_AUDIT);
			loHhsAuditBean.setEventName(HHSConstants.PROPERTY_TASK_CREATION_EVENT);
			// R5 changes ends
			loHhsAuditBean.setUserId(asTaskOwner);
			loHhsAuditBean.setInternalComments(HHSP8Constants.AUDIT_TASK_INTERNAL_COMMENTS);
			loHhsAuditBean.setWorkflowId(loWobNumber);
			loHhsAuditBeanList.add(loHhsAuditBean);
		}
	}

	/**
	 * This method will insert a record in the Audit table with the
	 * INSERT_AUDIT_DETAILS transaction. This operation is called directly from
	 * the workflow component step. <li>The transaction used is:
	 * insertAuditDetails</li>
	 * @param asEventName Name Of the Event
	 * @param asEventType Type OF the Event
	 * @param asData Data to be inserted
	 * @param asEntityType Type of Entity
	 * @param asEntityId Id of Entity
	 * @param asUserID USer ID
	 * @param asTableIdentifier Table Identifier
	 * @return boolean indicating if transaction is successful
	 * @throws ApplicationException
	 */
	public boolean callAuditService(String asEventName, String asEventType, String asData, String asEntityType,
			String asEntityId, String asUserID, String asTableIdentifier) throws ApplicationException
	{
		aoLogInfo.Debug("HHSComponentOperations.callAuditService.asEventName:" + asEventName);
		aoLogInfo.Debug("HHSComponentOperations.callAuditService.asEventType:" + asEventType);
		aoLogInfo.Debug("HHSComponentOperations.callAuditService.asData:" + asData);
		aoLogInfo.Debug("HHSComponentOperations.callAuditService.asEntityType:" + asEntityType);
		aoLogInfo.Debug("HHSComponentOperations.callAuditService.asEntityId:" + asEntityId);
		aoLogInfo.Debug("HHSComponentOperations.callAuditService.asUserID:" + asUserID);
		aoLogInfo.Debug("HHSComponentOperations.callAuditService.asTableIdentifier:" + asTableIdentifier);

		boolean lbAuditInserted = false;

		try
		{
			aoLogInfo.Debug("***************Inside  callAuditService************************");

			HhsAuditBean loHhsAuditBean = new HhsAuditBean();
			loHhsAuditBean.setEntityId(asEntityId);
			loHhsAuditBean.setEntityType(asEntityType);
			loHhsAuditBean.setAuditTableIdentifier(asTableIdentifier);
			loHhsAuditBean.setEventName(asEventName);
			loHhsAuditBean.setInternalComments(HHSP8Constants.AUDIT_TASK_INTERNAL_COMMENTS);
			loHhsAuditBean.setData(asData);
			loHhsAuditBean.setUserId(asUserID);
			loHhsAuditBean.setEventType(asEventType);

			Channel loChannelObj = new Channel();

			loChannelObj.setData(HHSP8Constants.AUDIT_BEAN, loHhsAuditBean);
			loChannelObj.setData(HHSP8Constants.TRANSACTION_PARAM_COMPONENT_AUDITSTATUS, true);

			aoLogInfo.Debug("Executing transaction insertAuditDetails");

			// execute notification transaction
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.INSERT_AUDIT_DETAILS);

			aoLogInfo.Debug("Finished transaction insertAuditDetails");

			lbAuditInserted = true;

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			aoLogInfo.Error("Exception in HHSComponentOperations.deleteEvaluationData()", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the callAuditService method in HHSComponentOperations", aoAppEx);

		}

		return lbAuditInserted;
	}

	/**
	 * This method fetches the review level <li>The transaction used is:
	 * fetchReviewLevels</li>
	 * @param asAgencyId AgencyId
	 * @param aoReviewProcId ReviewProcId
	 * @param aoMyBatisSession
	 * @throws ApplicationException
	 */
	private int retrieveReviewLevel(String asAgencyId, Integer aoReviewProcId) throws ApplicationException
	{

		int liReviewLevels = HHSP8Constants.INT_ZERO;
		Channel loChannel = new Channel();
		loChannel.setData(HHSP8Constants.AGENCY_ID_KEY, asAgencyId);
		loChannel.setData(HHSP8Constants.REVIEW_PROCESS_ID, aoReviewProcId);

		try
		{
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_REVIEW_LEVELS);
			liReviewLevels = (Integer) loChannel.getData(HHSP8Constants.REVIEW_LEVEL);

		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the retrieveReviewLevel method in WorkflowOperations", aoAppEx);
		}
		return liReviewLevels;
	}

	/**
	 * This method fetches all the contract information for a contractId <li>The
	 * transaction used is: findContractDetailsByContractIdForWF</li>
	 * @param aoMyBatisSession
	 * @param asContractId ContractId
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public FinancialWFBean contractConInfo(String asContractId) throws ApplicationException
	{
		Channel loChannel = new Channel();
		HashMap loTaskRequiredProps = new HashMap();
		FinancialWFBean loFinancialWFBean = null;
		loTaskRequiredProps.put(HHSP8Constants.CONTRACT_ID_WORKFLOW, asContractId);
		loChannel.setData(HHSP8Constants.LOHMAP, loTaskRequiredProps);

		try
		{
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FIND_CONTRACT_DETAILS_WF);
			loFinancialWFBean = (FinancialWFBean) loChannel.getData(HHSP8Constants.FINANCIAL_BEAN);
		}
		catch (ApplicationException aoAppEx)
		{

			throw new ApplicationException(
					"Error occured during executing the contractConInfo method in WorkflowOperations", aoAppEx);
		}
		return loFinancialWFBean;
	}

	/**
	 * This method fetches all the contract information for a contractId <li>The
	 * transaction used is: fetchEvaluationStatusID</li>
	 * @param proposalId ContractId
	 * @param intExtEvaluatorId Evaluator Id
	 * @param intExtFlag Internal/External FLag
	 * @throws ApplicationException
	 */
	public String retrieveEvaluationStatusId(String asProposalId, String asIntExtEvaluatorId, String asIntExtFlag)
			throws ApplicationException
	{

		Channel loChannel = new Channel();
		HashMap<String, String> loInputParam = new HashMap<String, String>();
		loInputParam.put(HHSP8Constants.PROPOSAL_ID, asProposalId);
		loInputParam.put(HHSP8Constants.INT_EXT_EVALUATOR_ID, asIntExtEvaluatorId);
		loInputParam.put(HHSP8Constants.INT_EXT_FLAG, asIntExtFlag);
		loChannel.setData(HHSP8Constants.LOHMAP, loInputParam);
		String lsEvaluationStatusId = null;

		try
		{

			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_EVALUATION_STATUS_ID);
			lsEvaluationStatusId = (String) loChannel.getData("evaluationStatusID");
		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the retrieveEvaluationStatusId method in WorkflowOperations",
					aoAppEx);
		}
		return lsEvaluationStatusId;
	}

	/**
	 * This method will verify if all the tasks have been finished. If not, the
	 * return value will be false, else true.
	 * 
	 * @param String[] workflowIds
	 * @return boolean
	 * @throws ApplicationException
	 */
	public boolean checkTaskFinished(VWSession aoPeSession, String[] asWorkflowIds) throws ApplicationException
	{
		boolean lbCompleted = true;
		String lsQueuename = HHSP8Constants.HHS_ACCELERATOR_PROCESS_QUEUE;
		String lsParameterNameTaskStatus = HHSP8Constants.TASK_STATUS;

		int liFoundWorkflows = asWorkflowIds.length;
		aoLogInfo.Info("Number workflows found: " + liFoundWorkflows);

		try
		{
			// Loop through all the given workflowIds and check if the
			// taskstatus is set to finished
			workflowLoop: for (String lsWorkflowId : asWorkflowIds)
			{
				// search for eval workflow
				VWStepElement loWfStep = new P8ProcessOperationForSolicitationFinancials().getStepElementfromWobNo(
						aoPeSession, lsWorkflowId, lsQueuename);
				try
				{
					if (loWfStep == null)
					{
						// We do not change the complete status, this means when
						// the wf cannot be found, it acts if it is completed
						aoLogInfo.Info("Workflow: " + lsWorkflowId + ": could not be found");
					}
					else
					{
						String lsTaskStatus = loWfStep.getParameterValue(lsParameterNameTaskStatus).toString();
						loWfStep.doAbort();
						aoLogInfo.Info("Checking workflow [" + lsWorkflowId + "] taskstatus : " + lsTaskStatus);

						if (!(HHSP8Constants.TASK_STATUS_FINISHED.equalsIgnoreCase(lsTaskStatus)))
						{
							// When there is one task that is currently not
							// finished yet, we do not have to look further.
							// Until the moment that all the eval workflows are
							// finished we want to return true.
							lbCompleted = false;
							break workflowLoop;
						}
					}

				}
				catch (VWException aoVWExp)
				{
					aoLogInfo.Error(aoVWExp.getMessage(), aoVWExp);
					throw new ApplicationException(aoVWExp.getMessage(), aoVWExp);
				}
				catch (Exception aoExp)
				{
					aoLogInfo.Error("WorkflowId:" + lsWorkflowId + ": " + aoExp.getMessage(), aoExp);
					throw new ApplicationException(aoExp.getMessage(), aoExp);
				}
			}
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the checkTaskFinished method in HHSComponentOperations", aoAppEx);
		}

		if (lbCompleted)
		{
			aoLogInfo.Info("All tasks are finished; workflows checked: " + asWorkflowIds);
		}
		else
		{
			aoLogInfo.Info("Not all tasks are finished; workflows checked: " + asWorkflowIds);
		}

		return lbCompleted;

	}

	/**
	 * This method will set the cancel flag in the given workflow to true.
	 * 
	 * @param aoPeSession
	 * @param asFilter
	 * @param asCancel
	 * @throws ApplicationException
	 * @throws WcmException
	 * @throws VWException
	 */
	public void setCancelParameterOnAwardWorkflow(VWSession aoPeSession, String asFilter, Boolean asCancel)
			throws ApplicationException
	{
		aoLogInfo.Debug("setCancelParameterOnAwardWorkflow: Filter=" + asFilter + " asCancel:"
				+ String.valueOf(asCancel));
		final boolean lbCompareValue = true;
		String loWfName = HHSP8Constants.WORKFLOW_NAME_203_AWARD;
		try
		{
			// Search for the current user
			VWRoster loVWRoster = aoPeSession.getRoster(HHSP8Constants.ROSTER_NAME);
			VWRosterQuery loRQuery = loVWRoster.createQuery(null, null, null, 0, asFilter, null,
					VWFetchType.FETCH_TYPE_WORKOBJECT);

			if (loRQuery.hasNext())
			{
				while (loRQuery.hasNext())
				{
					VWWorkObject loVwWorkObject = (VWWorkObject) loRQuery.next();
					if (loWfName.equalsIgnoreCase(loVwWorkObject.getWorkflowName()))
					{
						loVwWorkObject.doLock(true);
						String lsStepName = loVwWorkObject.getStepName();
						// Start || Changes done for enhancement 6574 for
						// Release 3.10.0

						if (loVwWorkObject.hasFieldName(HHSP8Constants.CANCEL_FLAG))
						{
							loVwWorkObject.setFieldValue(HHSP8Constants.CANCEL_FLAG, Boolean.toString(asCancel),
									lbCompareValue);
							if (HHSP8Constants.STEPNAME_APPROVE_AWARD_QUEUE.equalsIgnoreCase(lsStepName))
							{
								aoLogInfo.Debug("setCancelParameterOnAwardWorkflow: Workflow is currently in step: "
										+ lsStepName + ", step is being not completed for workflow: "
										+ loVwWorkObject.getWorkObjectNumber());
								loVwWorkObject.doSave(true);
								aoLogInfo.Debug("setCancelParameterOnAwardWorkflow after save execution");
							}
							else
							{
								aoLogInfo.Debug("setCancelParameterOnAwardWorkflow: Workflow is currently in step: "
										+ lsStepName + ", step is being completed for workflow: "
										+ loVwWorkObject.getWorkObjectNumber());
								loVwWorkObject.doDispatch();
								aoLogInfo.Debug("setCancelParameterOnAwardWorkflow after dispatch execution");
							}
						}
						// End || Changes done for enhancement 6574 for Release
						// 3.10.0
						else
						{
							aoLogInfo.Debug("setCancelParameterOnAwardWorkflow: Cancel flag not found in workflow: "
									+ loVwWorkObject.getWorkObjectNumber());
							loVwWorkObject.doAbort();
						}
					}
				}
			}
			else
			{
				aoLogInfo.Info("No workflows found for filter: " + asFilter);
			}
		}
		catch (ApplicationException aoAppExp)
		{
			throw new ApplicationException(
					"Error occured during executing the reassignWorkflow method in WorkflowOperations", aoAppExp);
		}
		catch (VWException aoVWExp)
		{
			aoLogInfo.Error("Error while dispatching the workobject : " + aoVWExp.getMessage(), aoVWExp);
			throw new ApplicationException(aoVWExp.getMessage(), aoVWExp);
		}
	}

	/**
	 * This method will set the cancel flag in the given workflow to true. Added
	 * for enhancement 6574 for Release 3.10.0
	 * @param aoPeSession
	 * @param asFilter
	 * @param asCancel
	 * @throws ApplicationException
	 * @throws WcmException
	 * @throws VWException
	 */
	public void setCancelParameterForAdditionalAwardsProcess(VWSession aoPeSession, String asFilter,
			String asAwardAmount) throws ApplicationException
	{
		setCancelParameterForAdditionalAwardsProcess(aoPeSession, asFilter, asAwardAmount, true);
	}

	/**
	 * This method will set the cancel flag in the given workflow to true. Added
	 * for enhancement 6574 for Release 3.10.0
	 * @param aoPeSession
	 * @param asFilter
	 * @param asCancel
	 * @throws ApplicationException
	 * @throws WcmException
	 * @throws VWException
	 */
	public void setCancelParameterForAdditionalAwardsProcess(VWSession aoPeSession, String asFilter,
			String asAwardAmount, boolean asFlag) throws ApplicationException
	{
		aoLogInfo.Debug("setCancelParameterForAdditionalAwardsProcess:");
		final boolean lbCompareValue = true;
		try
		{
			// Search for the current user
			VWRoster loVWRoster = aoPeSession.getRoster(HHSP8Constants.ROSTER_NAME);
			VWRosterQuery loRQuery = loVWRoster.createQuery(null, null, null, 0, asFilter, null,
					VWFetchType.FETCH_TYPE_WORKOBJECT);

			if (loRQuery.hasNext())
			{
				while (loRQuery.hasNext())
				{
					VWWorkObject loVwWorkObject = (VWWorkObject) loRQuery.next();
					loVwWorkObject.doLock(true);

					aoLogInfo.Debug("cancel flag set to true for Configure Award Documents Task" + ": "
							+ loVwWorkObject.getWorkObjectNumber());
					if (loVwWorkObject.hasFieldName(HHSP8Constants.CANCEL_FLAG))
					{

						if (asFlag)
						{
							loVwWorkObject.setFieldValue(HHSP8Constants.CANCEL_FLAG, Boolean.toString(true),
									lbCompareValue);
							loVwWorkObject.setFieldValue(P8Constants.PE_WORKFLOW_PROCUREMENT_AWARD_AMOUNT,
									asAwardAmount, lbCompareValue);
							loVwWorkObject.doSave(false);
							loVwWorkObject.doDispatch();
							aoLogInfo.Debug("------------cancel flag set to true for Configure Award Documents Task"
									+ ": " + loVwWorkObject.getWorkObjectNumber());
							Thread.sleep(3000);
							setCancelParameterForAdditionalAwardsProcess(aoPeSession, asFilter, null, false);
						}
						else
						{
							loVwWorkObject.setFieldValue(P8Constants.PROPERTY_PE_IS_TASK_VISSIBLE,
									Boolean.toString(true), lbCompareValue);
							loVwWorkObject.doSave(true);
						}
					}
				}
			}
			else
			{
				aoLogInfo.Info("No workflows found for filter: " + asFilter);
			}
		}
		catch (ApplicationException aoAppExp)
		{
			throw new ApplicationException(
					"Error occured during executing the setCancelParameterForAdditionalAwardsProcess method in WorkflowOperations",
					aoAppExp);
		}
		catch (VWException aoVWExp)
		{
			aoLogInfo.Error(aoVWExp.getMessage(), aoVWExp);
			throw new ApplicationException(aoVWExp.getMessage(), aoVWExp);
		}
		catch (InterruptedException e)
		{
			throw new ApplicationException(
					"Error occured during executing the setCancelParameterForAdditionalAwardsProcess method in WorkflowOperations",
					e);
		}
	}

	// R5 change starts
	/**
	 * This method is called to start Negotiation Contract Related Workflows.
	 * <ul>
	 * <li>transaction executed is <b>insertMultipleTaskAudit<b></li>
	 * </ul>
	 * @param aoPeSession
	 * @param asProcurementId
	 * @param asWorkFlowName
	 * @param aoContractDetailBean
	 * @param asEvaluationPoolMappingId
	 * @param asCompetitionPoolTitle
	 * @param asTaskOwner
	 * @return loWobNumber String
	 * @throws ApplicationException
	 */
	public String startNegotiationContractRelatedWorkflows(VWSession aoPeSession, String asProcurementId,
			String asWorkFlowName, ContractDetailsBean aoContractDetailBean, String asEvaluationPoolMappingId,
			String asCompetitionPoolTitle, String asTaskOwner) throws ApplicationException
	{
		aoLogInfo.Debug("Entered WorkflowOperations.startNegotiationContractRelatedWorkflows");
		HashMap loTaskRequiredProps = new HashMap();
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(aoLogInfo);
		int liReviewLevel = HHSP8Constants.INT_ZERO;
		String loWobNumber = null;
		try
		{
			// Retrieve the procurement information.
			loWorkflowOperations.fetchProcurementInformation(asProcurementId, loTaskRequiredProps);
			liReviewLevel = retrieveReviewLevel((String) loTaskRequiredProps.get(HHSP8Constants.PROPERTY_PE_AGENCY_ID),
					HHSP8Constants.FINANCIAL_WF_ID_MAP.get(asWorkFlowName));
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_TASK_TOTAL_LEVEL, liReviewLevel);
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_COMPETITION_POOL_TITLE, asCompetitionPoolTitle);
			// Load the PE session
			aoLogInfo.Debug("ProcurementId:" + asProcurementId + " - VWSession established");
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROCURMENT_ID, asProcurementId);
			loTaskRequiredProps.put(HHSP8Constants.CONTRACT_ID, aoContractDetailBean.getContractId());
			loTaskRequiredProps.put(HHSP8Constants.CONTRACT_TITLE, aoContractDetailBean.getContractTitle());
			loTaskRequiredProps.put(HHSP8Constants.CONTRACT_NUMBER, aoContractDetailBean.getContractNumber());
			loTaskRequiredProps.put(HHSP8Constants.CONTRACT_CONFIGURATOIN_ID,
					aoContractDetailBean.getContractConfigurationId());
			loTaskRequiredProps.put(HHSP8Constants.PROVIDER_ID, aoContractDetailBean.getProviderId());
			loTaskRequiredProps.put(HHSP8Constants.PROVIDER_NAME, aoContractDetailBean.getProviderName());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_SUBMITTED_BY, asTaskOwner);
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID, asEvaluationPoolMappingId);
			aoLogInfo.Info("ProcurementId:" + asProcurementId + " ProposalId: " + aoContractDetailBean.getContractId()
					+ " - Starting " + asWorkFlowName);
			loWobNumber = new P8ProcessOperationForSolicitationFinancials().launchWorkflow(aoPeSession, asWorkFlowName,
					loTaskRequiredProps, asTaskOwner);
			// Audit Service
			List<HhsAuditBean> loHhsAuditBeanList = new ArrayList<HhsAuditBean>();
			HhsAuditBean loHhsAuditBean = new HhsAuditBean();
			loHhsAuditBean.setEntityType(HHSConstants.TASK_CONTRACT_CONFIGURATION);
			loHhsAuditBean.setEntityId(aoContractDetailBean.getContractId());
			loHhsAuditBean.setContractId(aoContractDetailBean.getContractId());
			loHhsAuditBean.setEventType(HHSConstants.PROPERTY_TASK_CREATION_EVENT);
			loHhsAuditBean.setData(HHSConstants.PROPERTY_TASK_CREATION_DATA);
			loHhsAuditBean.setAuditTableIdentifier(HHSConstants.AGENCY_AUDIT);
			loHhsAuditBean.setEventName(HHSConstants.PROPERTY_TASK_CREATION_EVENT);
			loHhsAuditBean.setUserId(asTaskOwner);
			loHhsAuditBean.setWorkflowId(loWobNumber);
			loHhsAuditBeanList.add(loHhsAuditBean);
			if (null != loHhsAuditBeanList && !loHhsAuditBeanList.isEmpty())
			{
				Channel loChannel = new Channel();
				loChannel.setData(HHSConstants.AUDIT_BEAN_LIST, loHhsAuditBeanList);
				loChannel.setData(HHSP8Constants.TRANSACTION_PARAM_COMPONENT_AUDITSTATUS, true);
				TransactionManager.executeTransaction(loChannel, HHSP8Constants.INSERT_MULTIPLE_TASK_AUDIT);
			}
			/*
			 * Channel loChannelObj = new Channel();
			 * loChannelObj.setData(HHSConstants.AUDIT_BEAN, loHhsAuditBean);
			 * loChannelObj.setData(HHSConstants.UPDATE_FLAG,
			 * HHSConstants.BOOLEAN_TRUE);
			 * TransactionManager.executeTransaction(loChannelObj,
			 * HHSR5Constants.INSERT_TASK_AUDIT);
			 */
			aoLogInfo.Debug("Audit Done: WorkflowOperations.startNegotiationContractRelatedWorkflows");
			aoLogInfo.Debug("Exited WorkflowOperations.startNegotiationContractRelatedWorkflows");

		}
		catch (ApplicationException aoAppEx)
		{
			throw new ApplicationException(
					"Error occured during executing the startContractRelatedWorkflows method in WorkflowOperations",
					aoAppEx);
		}
		aoLogInfo.Debug("Exited WorkflowOperations.startNegotiationContractRelatedWorkflows");
		return loWobNumber;
	}
	// R5 change ends
}
