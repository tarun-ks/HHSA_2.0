package com.nyc.hhs.component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.xml.DOMConfigurator;

import com.nyc.hhs.component.operations.BudgetOperation;
import com.nyc.hhs.component.operations.DatabaseOperations;
import com.nyc.hhs.component.operations.NotificationOperations;
import com.nyc.hhs.component.operations.ReassignEvaluationsOperation;
import com.nyc.hhs.component.operations.WorkflowOperations;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.frameworks.transaction.Channel;
import com.nyc.hhs.frameworks.transaction.TransactionManager;
import com.nyc.hhs.model.AssignementDetailsBean;
import com.nyc.hhs.model.ContractDetailsBean;
import com.nyc.hhs.model.DefaultAssignment;
import com.nyc.hhs.model.EvaluationReassignDetailsBean;
import com.nyc.hhs.model.FinancialWFBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.NotificationDataBean;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.preprocessor.PreprocessorApproval;
import com.nyc.hhs.preprocessor.PreprocessorModificationApproval;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ProcessOperationForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8services.P8ProcessServiceForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

import filenet.vw.api.VWException;
import filenet.vw.api.VWSession;
import filenet.vw.api.VWStepElement;

/**
 * This class is used to perform all P8 process component operations on FileNet
 * for uploading printable version in the FileNet repository. This will be
 * executed through work-flow component step. The main method being called is
 * uploadPrintVersionInDV(), which is invoked through a component step in the BR
 * and SC workflows.
 */

public class HHSComponentOperations
{

	private static final LogInfo LOG_OBJECT = new LogInfo(HHSComponentOperations.class);
	VWSession moPeSession = null;
	//R7 changes start 
	P8UserSession loFilenetSession = null;
	//R7 changes end 
	static
	{
		try
		{
			System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG, PropertyLoader.getProperty(
					P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
			System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL, PropertyLoader.getProperty(
					P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
			System.setProperty(P8Constants.PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI,
					PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
		}
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error(aoExp.getMessage());
		}
	} // end static

	/**
	 * Constructor method that loads the transaction xml into the cache manager
	 * 
	 * @throws ApplicationException
	 */
	public HHSComponentOperations() throws ApplicationException
	{
		// Load log4j property file
		String lsLog4jPath = null;
		try
		{
			LOG_OBJECT.Debug("Constructor initialized");
			// Load property file for log4j
			lsLog4jPath = PropertyLoader.getProperty(HHSP8Constants.PROPERTY_FILE,
					HHSP8Constants.PROPERTY_PREDEFINED_LOG4J_COMPONENT_PATH);
			DOMConfigurator.configure(lsLog4jPath);
			LOG_OBJECT.Debug("Component Build Number ::: "
					+ PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, ApplicationConstants.PROPERTY_BUILD_NO));

			LOG_OBJECT.Debug("Creating Filenet Connection");
			P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
			moPeSession = loFilenetConnection.getPESession(loFilenetConnection.setP8SessionVariables());
			//Release 7 changes
			loFilenetSession = loFilenetConnection.setP8SessionVariables();
			
			String lsClassName = (this).getClass().getName();
			int liIndex = lsClassName.lastIndexOf(HHSConstants.DOT);
			if (liIndex > -1)
			{
				lsClassName = lsClassName.substring(liIndex + 1);
			}
			lsClassName = lsClassName + HHSConstants.DOT_CLASS;

			String lsCastorPath = (this.getClass().getResource(lsClassName) + HHSConstants.EMPTY_STRING)
					.replace("com/nyc/hhs/component/HHSComponentOperations.class", HHSConstants.CASTOR_MAPPING);

			// Load transaction xmls
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			Object loCacheObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
					HHSP8Constants.TRANSACTION_P8_XML));
			loCacheManager.putCacheObject(HHSP8Constants.TRANSACTION_LOWESCASE, loCacheObject);

			Object loCacheNotificationObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
					HHSP8Constants.EVENT_TYPE_TEMPLATE));
			loCacheManager.putCacheObject(HHSP8Constants.NOTIFICATION_CONTENT, loCacheNotificationObject);

			Object loCacheTaskAuditObject = XMLUtil.getDomObj(this.getClass().getResourceAsStream(
					HHSP8Constants.TASK_AUDIT_CONFIGURATION_XML));
			loCacheManager.putCacheObject(HHSP8Constants.TASK_AUDIT_CONFIGURATION, loCacheTaskAuditObject);
			//Release 7 changes
			HashMap<String, String> loMap = getApplicationSettingsBulk();
			loCacheManager.putCacheObject(HHSConstants.CASTER_CONFIGURATION_PATH, lsCastorPath);
			loCacheManager.putCacheObject(ApplicationConstants.FILENETDOCTYPE,
					XMLUtil.getDomObj(this.getClass().getResourceAsStream("/com/nyc/hhs/config/DocType.xml")));
			loCacheManager.putCacheObject(ApplicationConstants.APPLICATION_SETTING,loMap);
			//Release 7 changes
			LOG_OBJECT.Debug("CacheManager initialized");
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during initializing the HHSComponentOperations constructor", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during initializing the HHSComponentOperations constructor", aoAppEx);
			throw new ApplicationException("Error occured during initializing the HHSComponentOperations constructor",
					aoAppEx);
		}
	}

	/**
	 * <li>Fetches list of all the proposal IDs that are linked to a procurement
	 * id. This operation is called directly from the workflow component step.</li>
	 * @param asProcurementId Procurement Id for which the Proposal Ids are to
	 *            be retrieved
	 * @return String[] loProposalIDs Proposal Ids returned for a Procurement Id
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'fetchProposalsIdsByProcurement'
	 * 
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String[] getProposalIdsByProcurement(String asProcurementId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.getProposalIdsByProcurement.asProcurementId:" + asProcurementId);

		Channel loChannelObj = new Channel();
		HashMap loHMap = new HashMap();
		loHMap.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcurementId);
		loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);
		String[] loProposalIDs = null;
		LOG_OBJECT.Debug("CacheManager initialized");

		try
		{
			// Setup transaction
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_PROPOSAL_IDS_PROCUREMENT_DB);
			List<String> loProposalIdList = (List<String>) loChannelObj.getData(HHSP8Constants.PROPOSAL_ID_LIST);

			loProposalIDs = loProposalIdList.toArray(new String[loProposalIdList.size()]);
			LOG_OBJECT.Debug("Returning the following proposalIDs: " + loProposalIDs);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during getProposalIdsByProcurement", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during getProposalIdsByProcurement", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the getProposalIdsByProcurement method in HHSComponentOperations",
					aoAppEx);
		}
		return loProposalIDs;
	}

	/**
	 * This method is responsible for starting the evaluation workflows. For
	 * starting the workflows, it will use the startEvaluationWorkflows method
	 * in the loReassignEvaluationOperation class
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param asProcurementId Procurement Id
	 * @param asIntExtBothFlag Internal/External Flag
	 * @return String Array of Wob Numbers
	 * @throws ApplicationException
	 */
	public String[] startEvaluationWorkflows(String asProcurementId, String asIntExtBothFlag)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.startEvaluationWorkflows.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.startEvaluationWorkflows.asIntExtBothFlag:" + asIntExtBothFlag);
		ReassignEvaluationsOperation loReassignEvaluationOperation = new ReassignEvaluationsOperation(LOG_OBJECT);
		try
		{
			return loReassignEvaluationOperation.startEvaluationWorkflows(moPeSession, asProcurementId,
					asIntExtBothFlag, null);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during startEvaluationWorkflows", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during startEvaluationWorkflows", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the startEvaluationWorkflows method in HHSComponentOperations",
					aoAppEx);
		}
	}

	/**
	 * This method checks if the contract workflows have finished
	 * 
	 * It will search for all the workflows for the given procurementId Then it
	 * will filter the workflows that only the
	 * WORKFLOW_NAME_302_CONTRACT_CONFIGURATION and
	 * WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS are found. If the result
	 * count is 0, it will return true, else it will return false. *
	 * 
	 * @param asProcurementId
	 * @return boolean
	 * @throws ApplicationException
	 */
	public boolean checkIfContractWorkflowsAreFinished(String asProcurementId) throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.checkIfContractWorkflowsAreFinished.asProcurementId:"
				+ asProcurementId);

		// Find contract ids for this specific procurement
		Channel loChannel = new Channel();
		String lsFilter = null;
		ArrayList<String> loWobsList = new ArrayList<String>();
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		HashMap<String, String> loInputParam = new HashMap<String, String>();
		Set<String> loWfnames = new HashSet<String>();
		loInputParam.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcurementId);
		loChannel.setData(HHSP8Constants.LOHMAP, loInputParam);

		try
		{
			loWfnames.add(HHSP8Constants.WORKFLOW_NAME_302_CONTRACT_CONFIGURATION);
			loWfnames.add(HHSP8Constants.WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS);

			lsFilter = HHSP8Constants.PROPERTY_PE_PROCURMENT_ID + "='" + asProcurementId + "'";
			loWobsList.addAll(loWorkflowOperations.findWorkflowIds(moPeSession, lsFilter, loWfnames));

			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while executing the checkIfContractWorkflowsAreFinished method in HHSComponentOperations ",
							aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while executing the checkIfContractWorkflowsAreFinished method in HHSComponentOperations ",
							aoAppEx);
			throw new ApplicationException(aoAppEx.getMessage(), aoAppEx);
		}

		return (loWobsList.isEmpty());

	}

	/**
	 * This method triggers the workflow step visibility and changes the
	 * visibility accordingly.
	 * 
	 * The method will first look for all the WORKFLOW_NAME_202_EVALUATION
	 * workflows for the given proposalid. Next it will set the workflow
	 * visibility to true with use of the triggerWorkflowStepVisilibity method
	 * from the WorkflowOperations class
	 * 
	 * @param asProposalId Proposal ID
	 * @throws ApplicationException
	 */
	public void triggerEvaluationScoreWorkFlowStep(String asProposalId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.triggerEvaluationScoreWorkFlowStep.asProposalId:" + asProposalId);
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);

		try
		{
			String lsEvaluationFilter = HHSP8Constants.PE_WORKFLOW_PROPOSAL_ID + "='" + asProposalId + "'";
			HashSet<String> loWorkflowNamesHS = new HashSet<String>();
			loWorkflowNamesHS.add(HHSP8Constants.WORKFLOW_NAME_202_EVALUATION);
			ArrayList<String> loEvaluationWorkflowIdsArrayList = loWorkflowOperations.findWorkflowIds(moPeSession,
					lsEvaluationFilter, loWorkflowNamesHS);
			String[] loEvaluationWorkflowIds = loEvaluationWorkflowIdsArrayList
					.toArray(new String[loEvaluationWorkflowIdsArrayList.size()]);
			LOG_OBJECT.Debug("HHSComponentOperations.triggerEvaluationScoreWorkFlowStep.loEvaluationWorkflowIds:"
					+ loEvaluationWorkflowIds);
			boolean lbAllEvaluationTasksFinished = checkEvaluationTaskFinished(loEvaluationWorkflowIds);
			LOG_OBJECT.Debug("HHSComponentOperations.triggerEvaluationScoreWorkFlowStep.lbAllEvaluationTasksFinished:"
					+ lbAllEvaluationTasksFinished);
			if (lbAllEvaluationTasksFinished)
			{
				LOG_OBJECT.Debug("HHSComponentOperations.triggerEvaluationScoreWorkFlowStep in If condition");
				// Find the review score workflow for the given proposalId
				String lsFilter = HHSP8Constants.PROPOSAL_ID + "='" + asProposalId + "'";
				loWorkflowOperations.triggerWorkflowStepVisilibity(moPeSession, lsFilter, true,
						HHSP8Constants.WORKFLOW_NAME_202_REVIEW_SCORES, null);
			}

			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while executing the checkIfContractWorkflowsAreFinished method in HHSComponentOperations",
							aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while executing the checkIfContractWorkflowsAreFinished method in HHSComponentOperations",
							aoAppEx);
			throw new ApplicationException(aoAppEx.getMessage(), aoAppEx);
		}
	}

	/**
	 * This method will verify if the given workflow ids are finished.
	 * 
	 * This method uses the checkTaskFinished method from the WorkflowOperations
	 * class
	 * 
	 * @param asWorkflowIds
	 * @return boolean
	 * @throws ApplicationException
	 */
	public boolean checkEvaluationTaskFinished(String[] asWorkflowIds) throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.checkEvaluationTaskFinished.asWorkflowIds:" + asWorkflowIds);
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		boolean lbCompleted = true;

		try
		{
			// This method calls the checkTaskFinished method and passes the
			// workflowIds ad argument

			// Setup the peSession
			LOG_OBJECT.Debug("Check tasks on completed:  established");
			lbCompleted = loWorkflowOperations.checkTaskFinished(moPeSession, asWorkflowIds);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during checkEvaluationTaskFinished", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during checkEvaluationTaskFinished", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the checkEvaluationkTaskFinished method in HHSComponentOperations",
					aoAppEx);
		}
		return lbCompleted;
	}

	/**
	 * This method will start the review score workflows for the given proposal
	 * ids.
	 * 
	 * First this method will retrieve all the proposal information that is
	 * linked to this procurement. Next it will call the
	 * startProposalRelatedWorkflows method from the WorkflowOperations class to
	 * start the review score workflows for this procurement and proposals.
	 * 
	 * calls the transaction 'fetchAcceptedProposalID'
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param asProcurementId String containing procurement Id
	 * @return String[] started workflow ids
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public String[] startReviewScoreWorkflows(String asProcurementId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.startReviewScoreWorkflows.asProcurementId:" + asProcurementId);
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		String[] loAPwobs = null;

		Channel loChannel = new Channel();
		String[] loProposalIdArray = null;
		HashMap<String, String> loInputParam = new HashMap<String, String>();
		loInputParam.put(HHSP8Constants.PROCUREMENT_ID, asProcurementId);
		loChannel.setData(HHSP8Constants.LOHMAP, loInputParam);

		try
		{
			loInputParam.put(HHSP8Constants.PROCUREMENT_ID, asProcurementId);
			loChannel.setData(HHSP8Constants.LOHMAP, loInputParam);
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_ACCEPTED_PROPOSAL_ID);
			List<String> loProposalIdList = (List<String>) loChannel.getData(HHSP8Constants.PROPOSAL_ID_KEY);
			LOG_OBJECT.Debug("HHSComponentOperations.startReviewScoreWorkflows.loProposalIdList:" + loProposalIdList);
			loProposalIdArray = new String[loProposalIdList.size()];
			loProposalIdArray = loProposalIdList.toArray(loProposalIdArray);

			// Retrieve the propopsal information for the given proposal ids and
			// start the workflow with this data
			List<ProposalDetailsBean> loProposalDetailBeanList = loWorkflowOperations.retrieveProposalsInformation(
					loProposalIdArray, null);
			loAPwobs = loWorkflowOperations.startProposalRelatedWorkflows(moPeSession, asProcurementId,
					HHSP8Constants.WORKFLOW_NAME_202_REVIEW_SCORES, loProposalDetailBeanList, null, null);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during startReviewScoreWorkflows", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during startReviewScoreWorkflows", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the startReviewScoreWorkflows method in HHSComponentOperations",
					aoAppEx);
		}
		return loAPwobs;
	}

	/**
	 * This method will start the accept proposal workflows for the given
	 * procurement id.
	 * 
	 * First this method will retrieve all the related proposal information with
	 * use of the retrieveProposalInformation method from the WorkflowOperations
	 * class. After this it will start the accept proposal workflows with the
	 * startProposalRelatedWorkflows method in the WorkflowOperations class.
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param asProcurementId string containing procurement Id
	 * @param asLaunchBy string containing name of LaunchedBy
	 * @return String[] started workflow ids
	 * @throws ApplicationException
	 */
	public String[] startAcceptProposalWorkflows(String asProcurementId, String asLaunchBy) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.startAcceptProposalWorkflows.asProcurementId:" + asProcurementId);
		String[] loStartedWobs = null;
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		try
		{
			// Retrieve all proposal information linked to the procurementId and
			// start the accept proposal workflow
			List<ProposalDetailsBean> loProposalDetailBeanList = loWorkflowOperations.retrieveProposalsInformation(
					asProcurementId, null);
			LOG_OBJECT.Debug("HHSComponentOperations.startAcceptProposalWorkflows.ProposalDetailList----------------:"
					+ loProposalDetailBeanList.toString());
			loStartedWobs = loWorkflowOperations.startProposalRelatedWorkflows(moPeSession, asProcurementId,
					HHSP8Constants.WORKFLOW_NAME_201_ACCEPT_PROPOSAL, loProposalDetailBeanList, asLaunchBy, null);
			// handling Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during startAcceptProposalWorkflows", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during startAcceptProposalWorkflows", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the startAcceptProposalWorkflows method in HHSComponentOperations",
					aoAppEx);
		}
		return loStartedWobs;
	}

	/**
	 * This method will start the contract workflows for the specific
	 * procurement Id.
	 * 
	 * From the retrieveContractsInformation method in the WorkflowOperations
	 * class the contract details are retrieved. Based on this information the
	 * WORKFLOW_NAME_302_CONTRACT_CONFIGURATION workflows will be started for
	 * the retrieved contract details for the given procurement.
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param asProcurementId string containing procurement Id
	 * @param abFirstRound - If the award workflow is in the first round, this
	 *            will be true, else false.
	 * @param asTaskOwner string containing taskowner
	 * @return String[] started workflow ids
	 * @throws ApplicationException
	 */
	public String[] startContractWorkflows(String asProcurementId, boolean abFirstRound, String asTaskOwner)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.startContractWorkflows.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.startContractWorkflows.abFirstRound:" + abFirstRound);
		LOG_OBJECT.Debug("HHSComponentOperations.startContractWorkflows.asTaskOwner:" + asTaskOwner);
		// Retrieve the contract detail beans and start the contract
		// configuration workflow
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		String[] loStartedWobs = null;

		try
		{
			// R5 changes added
			List<ContractDetailsBean> loContractDetailsBeanList = loWorkflowOperations.retrieveContractsInformation(
					asProcurementId, null, false);
			// R5 changes ends
			loStartedWobs = loWorkflowOperations.startContractRelatedWorkflows(moPeSession, asProcurementId,
					HHSP8Constants.WORKFLOW_NAME_302_CONTRACT_CONFIGURATION, loContractDetailsBeanList, abFirstRound,
					asTaskOwner, null, null);

			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the startContractWorkflows:", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the startContractWorkflows:", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the startContractWorkflows method in HHSComponentOperations",
					aoAppEx);
		}
		return loStartedWobs;
	}

	/**
	 * This method will remove the unused workflow The following workflows will
	 * be removed: WORKFLOW_NAME_202_REVIEW_SCORES WORKFLOW_NAME_202_EVALUATION
	 * 
	 * For the removal, the method will search for all the workflows with the
	 * given procurementId. Next it will filter on
	 * WORKFLOW_NAME_202_REVIEW_SCORES and WORKFLOW_NAME_202_EVALUATION
	 * workflows and it will cancel those workflows. This functionality is used
	 * in the WorkflowOperations.cancelWorkflows method called from this method.
	 * 
	 * @param asProcurementId
	 * @throws ApplicationException
	 */
	public void cancelUnusedWorkflowsFromAward(String asProcurementId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.cancelUnusedWorkflowsFromAward.asProcurementId:" + asProcurementId);
		// Prepare the filter to get the
		String lsFilter = HHSP8Constants.PROPERTY_PE_PROCURMENT_ID + "='" + asProcurementId + "'";
		String[] loWorkflowNames =
		{ HHSP8Constants.WORKFLOW_NAME_202_REVIEW_SCORES, HHSP8Constants.WORKFLOW_NAME_202_EVALUATION };

		// Whenever performance issues arise: Create and index on contractId and

		try
		{
			// Call the method that will cancel the award workflows.
			WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
			loWorkflowOperations.cancelWorkflows(lsFilter, loWorkflowNames, moPeSession);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancel workflow with filter:" + lsFilter, aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancel workflow with filter:" + lsFilter, aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the cancelUnusedWorkflowsFromAward method in HHSComponentOperations",
					aoAppEx);
		}
	}

	/**
	 * The method will find the running workflow for this procurement for the
	 * specific provider. The following workflows will be
	 * terminated:WORKFLOW_NAME_302_CONTRACT_CONFIGURATION,
	 * WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS and
	 * WORKFLOW_NAME_304_CONTRACT_BUDGET.
	 * 
	 * First this method will use the cancelWorkflows functionality from the
	 * WorkflowOperations class to cancel the workflows. Next it will use the
	 * setCancelParameterOnAwardWorkflow method to set the cancelflag on the
	 * award workflow. This will make sure that the award workflow is aware that
	 * the related workflows have been cancelled.
	 * 
	 * @param asContractId
	 * @param asOrgId
	 * @param asProcurementId
	 * @param awardId
	 * @throws ApplicationException
	 */
	public void cancelAwardProcess(String asContractId, String asOrgId, String asProcurementId, String asAwardId)
			throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.cancelAwardProcess.asContractId:" + asContractId);
		LOG_OBJECT.Debug("HHSComponentOperations.cancelAwardProcess.asOrgId:" + asOrgId);
		LOG_OBJECT.Debug("HHSComponentOperations.cancelAwardProcess.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.cancelAwardProcess.asAwardId:" + asAwardId);
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		// Prepare the filter to get the
		String lsFilter = HHSP8Constants.CONTRACT_ID + "='" + asContractId + "'";
		String[] loWorkflowNames =
		{ HHSP8Constants.WORKFLOW_NAME_302_CONTRACT_CONFIGURATION,
				HHSP8Constants.WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS,
				HHSP8Constants.WORKFLOW_NAME_304_CONTRACT_BUDGET };

		// Whenever performance issues arise: Create and index on contractId and
		// change the query implementation to make use of this index

		try
		{
			// Call the method that will cancel the award workflows.
			loWorkflowOperations.cancelWorkflows(lsFilter, loWorkflowNames, moPeSession);
			// Set the cancelflag in the award workflow to true, to make sure it
			// will not go to the award doc step.
			Boolean loCancelFlag = true;
			String lsSetCancelAwardFilter = HHSP8Constants.PE_WORKFLOW_PROCUREMENT_ID + "='" + asProcurementId + "'";
			loWorkflowOperations.setCancelParameterOnAwardWorkflow(moPeSession, lsSetCancelAwardFilter, loCancelFlag);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancelAwardProcess with filter:" + lsFilter, aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancelAwardProcess with filter:" + lsFilter, aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the cancelAwardProcess method in HHSComponentOperations", aoAppEx);
		}
	}

	/**
	 * This method added as a part of release 3.10.0 enhancement 6574 This
	 * method will set the cancel flag while adding additional awards.
	 * 
	 * @param aoPeSession
	 * @param asProcurementID
	 * @param asEvaluationPoolMappingID
	 * @throws ApplicationException
	 * @throws VWException
	 */
	public void CancelAfterAwardApproval(String asEvaluationPoolMappingID, String asAwardAmount)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered in CancelAfterAwardApproval method :");
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		String loWfName = P8Constants.PE_AWARD_WORK_FLOW_SUBJECT;
		StringBuffer lsFilter = new StringBuffer(HHSP8Constants.EVAL_POOL_MAPPING_ID).append("='")
				.append(asEvaluationPoolMappingID).append("'").append(" and ")
				.append(P8Constants.PROPERTY_PE_WORKFLOW_NAME).append("='").append(loWfName).append("'");
		try
		{
			loWorkflowOperations.setCancelParameterForAdditionalAwardsProcess(moPeSession, lsFilter.toString(),
					asAwardAmount);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("Error occured on executing the CancelAfterAwardApproval with filter:" + lsFilter, aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT
					.Error("Error occured on executing the CancelAfterAwardApproval with filter:" + lsFilter, aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the CancelAfterAwardApproval method in HHSComponentOperations",
					aoAppEx);
		}
	}

	/**
	 * This method is responsible for canceling all the procurement related
	 * workflows.
	 * 
	 * The following workflows will be cancelled with the cancelWorkflows method
	 * in the WorkflowOperations class: WORKFLOW_NAME_201_ACCEPT_PROPOSAL,
	 * WORKFLOW_NAME_201_ACCEPT_PROPOSAL_MAIN,
	 * WORKFLOW_NAME_202_EVALUATE_PROPOSAL_MAIN, WORKFLOW_NAME_202_EVALUATION,
	 * WORKFLOW_NAME_202_REVIEW_SCORES, WORKFLOW_NAME_203_AWARD,
	 * WORKFLOW_NAME_302_CONTRACT_CONFIGURATION,
	 * WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS,
	 * WORKFLOW_NAME_304_CONTRACT_BUDGET
	 * 
	 * @param asProcurementId
	 * @throws ApplicationException
	 */
	public void cancelProcurement(String asProcurementId) throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.cancelProcurement.asProcurementId:" + asProcurementId);
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		// Prepare the filter for the workflows that need to be cancelled.
		String lsFilter = HHSP8Constants.PROPERTY_PE_PROCURMENT_ID + "='" + asProcurementId + "'";
		String[] loWorkflowNames =
		{ HHSP8Constants.WORKFLOW_NAME_201_ACCEPT_PROPOSAL, HHSP8Constants.WORKFLOW_NAME_201_ACCEPT_PROPOSAL_MAIN,
				HHSP8Constants.WORKFLOW_NAME_202_EVALUATE_PROPOSAL_MAIN, HHSP8Constants.WORKFLOW_NAME_202_EVALUATION,
				HHSP8Constants.WORKFLOW_NAME_202_REVIEW_SCORES, HHSP8Constants.WORKFLOW_NAME_203_AWARD,
				HHSP8Constants.WORKFLOW_NAME_301_PROCUREMENT_CERTIFICATION_FUNDS,
				HHSP8Constants.WORKFLOW_NAME_302_CONTRACT_CONFIGURATION,
				HHSP8Constants.WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS,
				HHSP8Constants.WORKFLOW_NAME_304_CONTRACT_BUDGET };

		// Whenever performance issues arise: Create and index on contractId and
		// change the query implementation to make use of this index
		try
		{
			// Call the method that will cancel the given workflows for the
			// given filter
			loWorkflowOperations.cancelWorkflows(lsFilter, loWorkflowNames, moPeSession);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancel workflow with filter:" + lsFilter, aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancel workflow with filter:" + lsFilter, aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the cancelProcurement method in HHSComponentOperations", aoAppEx);
		}
	}

	/**
	 * This method implements the reassignment functionality for the evaluation
	 * tasks.
	 * 
	 * First the evaluation reassignment is handled for the internal evaluators.
	 * Secondly the evaluation reassignment is handled for the external
	 * evaluators.
	 * 
	 * Both steps are first retrieving the evaluation information from the
	 * retrieveEvaluationReassignmentInformation method in the
	 * ReassignEvaluationsOperations class. After that the
	 * handleEvaluationInternalReassignment/
	 * handleEvaluationExternalReassignment is called in the
	 * ReassignEvaluationsOperations class to handle reassignment.
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param asProcurementId String containing procurement Id
	 * @throws ApplicationException
	 */
	public void reassignEvaluationTasks(String asProcurementId) throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.reassignEvaluationTasks.asProcurementId:" + asProcurementId);
		ReassignEvaluationsOperation loOperation = new ReassignEvaluationsOperation(LOG_OBJECT);

		try
		{
			// Handle evaluation tasks for internal evaluators
			List<EvaluationReassignDetailsBean> loEvaluationReassignDetailsInteralBeans = loOperation
					.retrieveEvaluationReassignmentInformation(asProcurementId, HHSP8Constants.INTERNAL_FLAG, null);
			loOperation.handleEvaluationInternalReassignment(moPeSession, asProcurementId,
					loEvaluationReassignDetailsInteralBeans, null);

			// Handle evaluation tasks for external evaluators
			List<EvaluationReassignDetailsBean> loEvaluationReassignDetailsExternalBeans = loOperation
					.retrieveEvaluationReassignmentInformation(asProcurementId, HHSP8Constants.EXTERNAL_FLAG, null);
			loOperation.handleEvaluationExternalReassignment(moPeSession, asProcurementId,
					loEvaluationReassignDetailsExternalBeans, null);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the reassignEvaluationTasks with Procurement ID:"
					+ asProcurementId, aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the reassignEvaluationTasks with Procurement ID:"
					+ asProcurementId, aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the reassignEvaluationTasks method in HHSComponentOperations",
					aoAppEx);
		}

	}

	/**
	 * Place holder for the old method to be compatible for the components that
	 * do not require the invoice id and the asBudgetId.
	 * 
	 * @param asDependentTaskWFName
	 * @param asWobNumber
	 * @param asLinkedWobNumber
	 * @param asActionFlag
	 * @param asContractId
	 * @return String WOB number of created/updated workflow
	 * @throws ApplicationException
	 */
	public String processDependentWorkflow(String asDependentTaskWFName, String asWobNumber, String asLinkedWobNumber,
			String asActionFlag, String asContractId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.processDependentWorkflow:" + asWobNumber + " :" + asLinkedWobNumber
				+ ": " + asActionFlag + ": " + asContractId);
		return processDependentWorkflow(asDependentTaskWFName, asWobNumber, asLinkedWobNumber, asActionFlag,
				asContractId, HHSP8Constants.EMPTY_STRING, HHSP8Constants.EMPTY_STRING);
	}

	/**
	 * Place holder for the old method to be compatible for the components that
	 * do not require the advance budget id.
	 * 
	 * @param asDependentTaskWFName
	 * @param asWobNumber
	 * @param asLinkedWobNumber
	 * @param asActionFlag
	 * @param asContractId
	 * @param asInvoiceNumber
	 * @param asBudgetId
	 * @return
	 * @throws ApplicationException
	 */
	public String processDependentWorkflow(String asDependentTaskWFName, String asWobNumber, String asLinkedWobNumber,
			String asActionFlag, String asContractId, String asInvoiceNumber, String asBudgetId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.processDependentWorkflow:" + asWobNumber + " :" + asLinkedWobNumber
				+ ": " + asActionFlag + ": " + asContractId + " :" + asInvoiceNumber);
		return processDependentWorkflow(asDependentTaskWFName, asWobNumber, asLinkedWobNumber, asActionFlag,
				asContractId, asInvoiceNumber, asBudgetId, HHSP8Constants.EMPTY_STRING);
	}

	/**
	 * Place holder for the old method to be compatible for the components that
	 * do not require the launchby
	 * 
	 * @param asDependentTaskWFName
	 * @param asWobNumber
	 * @param asLinkedWobNumber
	 * @param asActionFlag
	 * @param asContractId
	 * @param asInvoiceNumber
	 * @param asBudgetId
	 * @param asAdvanceBudgetId
	 * @return
	 */
	public String processDependentWorkflow(String asDependentTaskWFName, String asWobNumber, String asLinkedWobNumber,
			String asActionFlag, String asContractId, String asInvoiceNumber, String asBudgetId,
			String asAdvanceBudgetId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.processDependentWorkflow:asWobNumber :" + asWobNumber
				+ "asLinkedWobNumber :" + asLinkedWobNumber + "asActionFlag: " + asActionFlag + "asContractId: "
				+ asContractId + " asInvoiceNumber:" + asInvoiceNumber + "asBudgetId: " + asBudgetId);
		return processDependentWorkflow(asDependentTaskWFName, asWobNumber, asLinkedWobNumber, asActionFlag,
				asContractId, asInvoiceNumber, asBudgetId, asAdvanceBudgetId, HHSP8Constants.EMPTY_STRING);
	}

	/**
	 * This method will create dependent workflow/update task visibility to
	 * true/update task status to completed of the linked workflow. This
	 * operation is called directly from the workflow component step.
	 * 
	 * @param asDependentTaskWFName Name of dependent workflow
	 * @param asWobNumber WOB Number of calling workflow
	 * @param asLinkedWobNumber WOB Number of linked task
	 * @param asActionFlag {NEW, RETURNED, COMPLETED)
	 * @param contractId Contract Id
	 * @param asInvoiceNumber
	 * @param asBudgetId
	 * @param asAdvanceBudgetId
	 * @return String WOB number of created/updated workflow
	 * @throws ApplicationException
	 */
	public String processDependentWorkflow(String asDependentTaskWFName, String asWobNumber, String asLinkedWobNumber,
			String asActionFlag, String asContractId, String asInvoiceNumber, String asBudgetId,
			String asAdvanceBudgetId, String asLaunchBy) throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.processDependentWorkflow.asDependentTaskWFName:"
				+ asDependentTaskWFName);
		LOG_OBJECT.Debug("HHSComponentOperations.processDependentWorkflow.asWobNumber:" + asWobNumber);
		LOG_OBJECT.Debug("HHSComponentOperations.processDependentWorkflow.asLinkedWobNumber:" + asLinkedWobNumber);
		LOG_OBJECT.Debug("HHSComponentOperations.processDependentWorkflow.asActionFlag:" + asActionFlag);
		LOG_OBJECT.Debug("HHSComponentOperations.processDependentWorkflow.asContractId:" + asContractId);
		LOG_OBJECT.Debug("HHSComponentOperations.processDependentWorkflow.asInvoiceNumber:" + asInvoiceNumber);
		LOG_OBJECT.Debug("HHSComponentOperations.processDependentWorkflow.asBudgetId:" + asBudgetId);
		LOG_OBJECT.Debug("HHSComponentOperations.processDependentWorkflow.asAdvanceBudgetId:" + asAdvanceBudgetId);
		LOG_OBJECT.Debug("HHSComponentOperations.processDependentWorkflow.asLaunchBy:" + asLaunchBy);

		String lsDependentTaskWobNumber = asLinkedWobNumber;
		Channel loChannelObj = new Channel();
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);

		try
		{
			LOG_OBJECT.Debug("***************Inside  processDependentWorkflow************************");

			FinancialWFBean loFinancialWFBean = null;

			loFinancialWFBean = loWorkflowOperations.contractConInfo(asContractId);

			if (asActionFlag.equalsIgnoreCase(HHSP8Constants.COMPONENT_OPERATIONS_PARAM_ACTIONFLAG_NEW))
			{
				// The following line will handle the start of a new workflow
				// for the given dependent Task WorkflowName
				// (asDependentTaskWFName)
				lsDependentTaskWobNumber = handleProcessWorkflowNew(loFinancialWFBean, asContractId, asWobNumber,
						asInvoiceNumber, asBudgetId, asAdvanceBudgetId, asLaunchBy, asDependentTaskWFName, loChannelObj);
			}
			else if (asActionFlag.equalsIgnoreCase(HHSP8Constants.COMPONENT_OPERATIONS_PARAM_ACTIONFLAG_RETURNED))
			{
				// The following line will handle the return to an expisting
				// workflow for the given wobnumber
				/*
				 * R6: changes for bulk upload start Checking if actionFlag
				 * is returned and linkedWobNumber is empty
				 */
				// R5 changes added	
				if (StringUtils.isNotBlank(asLinkedWobNumber))
				{
					handleProcessWorkflowReturn(asLinkedWobNumber, asContractId);
					// R5 changes Ends
				}
				else
				{
					
					if (asDependentTaskWFName.equalsIgnoreCase(HHSP8Constants.WORKFLOW_NAME_302_CONTRACT_CONFIGURATION)
							|| asDependentTaskWFName.equalsIgnoreCase(HHSP8Constants.WF_AMENDMENT_CONFIGURATION))
					{
						lsDependentTaskWobNumber = handleProcessWorkflowReturnCOFBulkUpload(loFinancialWFBean,
								asContractId, asWobNumber, asLaunchBy, asDependentTaskWFName, loChannelObj);
					}
					/* R6: changes for bulk upload ends */
				}
			}
			else if (asActionFlag.equalsIgnoreCase(HHSP8Constants.COMPONENT_OPERATIONS_PARAM_ACTIONFLAG_COMPLETED))
			{
				//[Start]R7.1.0 fix for Bulk upload contract adding if-statement for ETL request
				if (StringUtils.isNotBlank(asLinkedWobNumber))
				{
					// The following line will handle the completion of the workflow
					// for the given wobnumber
					handleProcessWorkflowComplete(asLinkedWobNumber);
				}
				//[End]R7.1.0 fix for Bulk upload contract adding if-statement for ETL request
			}
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in BatchComponent.executeDocLapsingNotificationBatch()", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the processDependentWorkflow method in HHSComponentOperations",
					aoAppEx);
		}

		return lsDependentTaskWobNumber;
	}

	/**
	 * This method will handle the completion of the given workobject.
	 * 
	 * @param asLinkedWobNumber
	 * @throws ApplicationException
	 * @throws VWException
	 */
	private void handleProcessWorkflowComplete(String asLinkedWobNumber) throws ApplicationException
	{
		LOG_OBJECT.Debug("Executing update to task status: " + asLinkedWobNumber);

		try
		{
			VWStepElement loStepElement = new P8ProcessOperationForSolicitationFinancials().getStepElementfromWobNo(
					moPeSession, asLinkedWobNumber, HHSP8Constants.HSS_QUEUE_NAME);
			loStepElement.doLock(true);

			loStepElement.setParameterValue(HHSP8Constants.TASK_STATUS, HHSP8Constants.TASK_STATUS_COMPLETED, false);
			loStepElement.doSave(false);
			loStepElement.doDispatch();
		}
		catch (ApplicationException aoAppEx)
		{

			LOG_OBJECT.Error("Exception in HHSComponentOperations.handleProcessWorkflowComplete()", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the handleProcessWorkflowComplete method in HHSComponentOperations",
					aoAppEx);
		}
		catch (VWException aoVWExp)
		{
			LOG_OBJECT
					.Error("Exception occured while executing the handleProcessWorkflowComplete method in HHSComponentOperations",
							aoVWExp);
			throw new ApplicationException(aoVWExp.getMessage(), aoVWExp);
		}
	}

	/**
	 * This method modified as a part of enhancement 6534 release 3.8.0
	 * 
	 * On calling the handleProcessWorkflowReturn method the task for the given
	 * workobject will be reopened and required parameters will be set.
	 * 
	 * @param asLinkedWobNumber
	 * @param asContractId
	 * @throws ApplicationException
	 */
	// Method updated in R5
	private void handleProcessWorkflowReturn(String asLinkedWobNumber, String asContractId) throws ApplicationException
	{

		LOG_OBJECT.Debug("Executing update to task visibility: " + asLinkedWobNumber);

		try
		{
			VWStepElement loStepElement = new P8ProcessOperationForSolicitationFinancials().getStepElementfromWobNo(
					moPeSession, asLinkedWobNumber, HHSP8Constants.HSS_QUEUE_NAME);
			loStepElement.doLock(true);

			String lsTaskType = (String) loStepElement.getParameterValue(HHSP8Constants.PROPERTY_PE_TASK_TYPE);
			// added in R5
			String lsEntityIdKey = "";
			if (HHSConstants.ADVANCE_REQUEST_REVIEW.equalsIgnoreCase(lsTaskType)
					|| HHSConstants.TASK_ADVANCE_REVIEW.equalsIgnoreCase(lsTaskType)
					|| HHSConstants.TASK_ADVANCE_PAYMENT_REVIEW.equalsIgnoreCase(lsTaskType))
			{
				lsEntityIdKey = P8Constants.PROPERTY_PE_ENTITY_ID;
			}
			else if (HHSConstants.TASK_AMENDMENT_CONFIGURATION.equalsIgnoreCase(lsTaskType)
					|| HHSConstants.TASK_AMENDMENT_COF.equalsIgnoreCase(lsTaskType)
					|| HHSConstants.TASK_CONTRACT_COF.equalsIgnoreCase(lsTaskType)
					|| HHSConstants.TASK_CONTRACT_CONFIGURATION.equalsIgnoreCase(lsTaskType)
					|| HHSConstants.TASK_CONTRACT_UPDATE.equalsIgnoreCase(lsTaskType)
					|| HHSConstants.TASK_NEW_FY_CONFIGURATION.equalsIgnoreCase(lsTaskType))
			{
				lsEntityIdKey = HHSP8Constants.PROPERTY_PE_CONTRACT_ID;
			}
			else if (HHSConstants.TASK_INVOICE_REVIEW.equalsIgnoreCase(lsTaskType)
					|| HHSConstants.TASK_PAYMENT_REVIEW.equalsIgnoreCase(lsTaskType))
			{
				lsEntityIdKey = HHSP8Constants.PROPERTY_PE_INVOICE_ID;
			}
			else if (HHSConstants.TASK_BUDGET_AMENDMENT.equalsIgnoreCase(lsTaskType)
					|| HHSConstants.TASK_BUDGET_MODIFICATION.equalsIgnoreCase(lsTaskType)
					|| HHSConstants.TASK_BUDGET_UPDATE.equalsIgnoreCase(lsTaskType)
					|| HHSConstants.TASK_BUDGET_REVIEW.equalsIgnoreCase(lsTaskType))
			{
				lsEntityIdKey = HHSP8Constants.PROPERTY_PE_BUDGET_ID;
			}
			String lsEntityId = (String) loStepElement.getParameterValue(lsEntityIdKey);
			LOG_OBJECT.Debug("HHSComponentOperations.handleProcessWorkflowReturn.lsTaskType:" + lsTaskType);
			// Handle the task reopening for the TASK_CONTRACT_OF
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.ENTITY_ID, lsEntityId);
			loChannelObj.setData(HHSConstants.TASK_TYPE, HHSUtil.setTaskType(lsTaskType));

			if (lsTaskType.equalsIgnoreCase(HHSP8Constants.TASK_CONTRACT_COF)
					|| lsTaskType.equalsIgnoreCase(HHSP8Constants.TASK_AMENDMENT_COF))
			{
				loStepElement.setParameterValue(HHSP8Constants.TASKVISIBILITY, true, true);
				// R5 changes ends
				loStepElement.setParameterValue(HHSP8Constants.PE_WORKFLOW_TASK_STATUS,
						HHSP8Constants.TASK_STATUS_INREVIEW, true);
				loChannelObj.setData(HHSConstants.TASK_LEVEL, "2");
				changeOwnerDetails(loStepElement, HHSP8Constants.UNASSIGNED_LEVEL2, HHSP8Constants.UNASSIGNED_LEVEL2,
						loChannelObj);
				loStepElement.doSave(true);
			}
			else
			{
				// Handle the task reopening for the
				// TASK_ADVANCE_PAYMENT_REVIEW
				if (lsTaskType.equalsIgnoreCase(HHSP8Constants.TASK_ADVANCE_PAYMENT_REVIEW)
						|| lsTaskType.equalsIgnoreCase(HHSP8Constants.TASK_PAYMENT_REVIEW))
				{
					loStepElement.setParameterValue(HHSP8Constants.PE_WORKFLOW_TASK_STATUS,
							HHSP8Constants.TASK_STATUS_INREVIEW, true);

					SimpleDateFormat loSimpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
					String loDate = loSimpleDateFormat.format(new Date());
					loStepElement.setParameterValue(HHSP8Constants.LAUNCH_DATE, loDate, true);
				}
				else
				{
					loStepElement.setParameterValue(HHSP8Constants.PE_WORKFLOW_TASK_STATUS,
							HHSP8Constants.TASK_STATUS_RETURNEDFORREVISION, true);
				}

				loStepElement.setParameterValue(HHSP8Constants.TASKVISIBILITY, true, true);

				String lsReviewLevel = String.valueOf(loStepElement
						.getParameterValue(HHSP8Constants.PROPERTY_PE_REVIEWLEVEL));
				// Current level fetched as a part of enhancement 6534
				// release 3.8.0
				String lsCurrentLevel = String.valueOf(loStepElement.getParameterValue(HHSConstants.CURR_LEVEL));

				LOG_OBJECT.Debug("Current level fetched as  :::: " + lsCurrentLevel);

				// Ensure the tasks goes to the correct unassigned level for
				// TASK_INVOICE_REVIEW and TASK_ADVANCE_PAYMENT_REQUEST
				if ((lsTaskType.equalsIgnoreCase(HHSP8Constants.TASK_INVOICE_REVIEW) || (lsTaskType
						.equalsIgnoreCase(HHSP8Constants.TASK_ADVANCE_PAYMENT_REQUEST)))
						&& StringUtils.isNotBlank(lsReviewLevel))
				{
					loChannelObj.setData(HHSConstants.TASK_LEVEL, lsCurrentLevel);
					changeOwnerDetails(loStepElement, HHSP8Constants.UNASSIGNED_LEVEL + lsReviewLevel,
							HHSP8Constants.UNASSIGNED_LEVEL + lsCurrentLevel, loChannelObj);
				}
				else
				{
					loChannelObj.setData(HHSConstants.TASK_LEVEL, "1");
					changeOwnerDetails(loStepElement, HHSP8Constants.UNASSIGNED_LEVEL1,
							HHSP8Constants.UNASSIGNED_LEVEL1, loChannelObj);
				}
				loStepElement.doSave(true);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.handleProcessWorkflowComplete()", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the handleProcessWorkflowComplete method in HHSComponentOperations",
					aoAppEx);
		}
		catch (VWException aoVWExp)
		{
			LOG_OBJECT
					.Error("Exception occured while executing the handleProcessWorkflowReturn method in HHSComponentOperations",
							aoVWExp);
			throw new ApplicationException(aoVWExp.getMessage(), aoVWExp);
		}
	}

	// Added in R5
	/**
	 * This method is used to change Owner Details
	 * 
	 * On calling the handleProcessWorkflowReturn method the task for the given
	 * workobject will be reopened and required parameters will be set.
	 * 
	 * @param loStepElement VWStepElement
	 * @param asTaskOwner String
	 * @param asTaskOwnerName String
	 * @param aoChannelObj Channel
	 * @throws ApplicationException - if any exception occurred
	 */
	private void changeOwnerDetails(VWStepElement loStepElement, String asTaskOwner, String asTaskOwnerName,
			Channel aoChannelObj) throws ApplicationException
	{
		try
		{
			TransactionManager.executeTransaction(aoChannelObj, HHSR5Constants.GET_ASSIGNMENT_DB_DETAILS_TX);
			TaskDetailsBean loTaskDetailsBean = (TaskDetailsBean) aoChannelObj
					.getData(ApplicationConstants.TASK_DETAILS_BEAN);
			if (loTaskDetailsBean != null && StringUtils.isNotBlank(loTaskDetailsBean.getReassignUserId()))
			{
				asTaskOwner = loTaskDetailsBean.getReassignUserId();
				asTaskOwnerName = loTaskDetailsBean.getReassignUserName();
			}
			loStepElement.setParameterValue(HHSP8Constants.TASK_OWNER, asTaskOwner, true);
			loStepElement.setParameterValue(HHSP8Constants.TASK_OWNER_NAME, asTaskOwnerName, true);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.changeOwnerDetails()", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the changeOwnerDetails method in HHSComponentOperations", aoAppEx);
		}
		catch (VWException aoVWExp)
		{
			LOG_OBJECT.Error(
					"Exception occured while executing the changeOwnerDetails method in HHSComponentOperations",
					aoVWExp);
			throw new ApplicationException(aoVWExp.getMessage(), aoVWExp);
		}
	}

	/**
	 * Handles the process of starting a new workflow based on the given
	 * parameters. The method will return the wobnumber of the started workflow.
	 * 
	 * The launching of the workflow will be done by the
	 * P8ProcessOperationForSolicitationFinancials.launchWorkflow method.
	 * 
	 * @param asFinancialWFBean
	 * @param asContractId
	 * @param asWobNumber
	 * @param asInvoiceNumber
	 * @param asBudgetId
	 * @param asAdvanceBudgetId
	 * @param asLaunchBy
	 * @param asDependentTaskWFName
	 * @param asChannelObj
	 * @return
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'fetchReviewLevels'
	 * 
	 *             calls the transaction 'insertTaskAudit'
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private String handleProcessWorkflowNew(FinancialWFBean asFinancialWFBean, String asContractId, String asWobNumber,
			String asInvoiceNumber, String asBudgetId, String asAdvanceBudgetId, String asLaunchBy,
			String asDependentTaskWFName, Channel asChannelObj) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.handleProcessWorkflowNew.asFinancialWFBean:" + asFinancialWFBean);
		LOG_OBJECT.Debug("HHSComponentOperations.handleProcessWorkflowNew.asContractId:" + asLaunchBy);
		LOG_OBJECT.Debug("HHSComponentOperations.handleProcessWorkflowNew.asWobNumber:" + asWobNumber);
		LOG_OBJECT.Debug("HHSComponentOperations.handleProcessWorkflowNew.asInvoiceNumber:" + asInvoiceNumber);
		LOG_OBJECT.Debug("HHSComponentOperations.handleProcessWorkflowNew.asBudgetId:" + asBudgetId);
		LOG_OBJECT.Debug("HHSComponentOperations.handleProcessWorkflowNew.asAdvanceBudgetId:" + asAdvanceBudgetId);
		LOG_OBJECT.Debug("HHSComponentOperations.handleProcessWorkflowNew.asLaunchBy:" + asLaunchBy);
		LOG_OBJECT.Debug("HHSComponentOperations.handleProcessWorkflowNew.asDependentTaskWFName:"
				+ asDependentTaskWFName);
		LOG_OBJECT.Debug("HHSComponentOperations.handleProcessWorkflowNew.asChannelObj:" + asChannelObj);

		HashMap loTaskRequiredProps = new HashMap();
		String lsDependentTaskWobNumber = null;

		try
		{
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROCURMENT_ID, asFinancialWFBean.getProcurementId());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROCUREMENT_TITLE,
					asFinancialWFBean.getProcurementTitle());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROVIDER_ID, asFinancialWFBean.getProviderId());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROVIDER_NAME, asFinancialWFBean.getProviderName());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROCUREMENT_EPIN, asFinancialWFBean.getProcEpin());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROGRAM_NAME, asFinancialWFBean.getProgramName());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_AWARD_EPIN, asFinancialWFBean.getAwardEpin());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_CT, asFinancialWFBean.getContractNum());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_SUBMITTED_BY, asLaunchBy);
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_AGENCY_ID, asFinancialWFBean.getAgencyId());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_CONTRACT_ID, asContractId);
			loTaskRequiredProps.put(HHSP8Constants.LINKED_WOB_NO, asWobNumber);
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_COMPETITION_POOL_TITLE,
					asFinancialWFBean.getCompetitionPoolTitle());

			lsDependentTaskWobNumber = getWorkFlowDetails(asFinancialWFBean, asContractId, asInvoiceNumber, asBudgetId,
					asAdvanceBudgetId, asLaunchBy, asDependentTaskWFName, asChannelObj, loTaskRequiredProps);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.handleProcessWorkflowNew()", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the handleProcessWorkflowNew method in HHSComponentOperations",
					aoAppEx);
		}
		return lsDependentTaskWobNumber;
	}

	/**
	 * This method id used to get the Work Flow Details.
	 * @param asFinancialWFBean
	 * @param asContractId
	 * @param asInvoiceNumber
	 * @param asBudgetId
	 * @param asAdvanceBudgetId
	 * @param asLaunchBy
	 * @param asDependentTaskWFName
	 * @param asChannelObj
	 * @param loTaskRequiredProps
	 * @return
	 * @throws ApplicationException
	 */
	private String getWorkFlowDetails(FinancialWFBean asFinancialWFBean, String asContractId, String asInvoiceNumber,
			String asBudgetId, String asAdvanceBudgetId, String asLaunchBy, String asDependentTaskWFName,
			Channel asChannelObj, HashMap aoTaskRequiredProps) throws ApplicationException
	{
		String lsDependentTaskWobNumber;
		if (null != asInvoiceNumber && !asInvoiceNumber.isEmpty())
		{
			aoTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_INVOICE_ID, asInvoiceNumber);
		}

		if (null != asBudgetId && !asBudgetId.isEmpty())
		{
			aoTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_BUDGET_ID, asBudgetId);
		}

		if (null != asAdvanceBudgetId && !asAdvanceBudgetId.isEmpty())
		{
			aoTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_ADVANCE_NUMBER, asAdvanceBudgetId);
		}

		if (null != asLaunchBy && !asLaunchBy.isEmpty())
		{
			aoTaskRequiredProps.put(HHSP8Constants.LAUNCH_BY, asLaunchBy);
		}

		int liReviewProcessId = HHSP8Constants.FINANCIAL_WF_ID_MAP.get(asDependentTaskWFName);

		asChannelObj.setData(HHSP8Constants.AGENCY_ID_KEY, asFinancialWFBean.getAgencyId());
		asChannelObj.setData(HHSP8Constants.REVIEW_PROCESS_ID, liReviewProcessId);

		TransactionManager.executeTransaction(asChannelObj, HHSP8Constants.FETCH_REVIEW_LEVELS);
		Integer liReviewLevels = (Integer) asChannelObj.getData(HHSP8Constants.REVIEW_LEVEL);

		aoTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_TASK_TOTAL_LEVEL, liReviewLevels);

		LOG_OBJECT.Debug("Executing P8ProcessOperationForSolicitationFinancials().launchWorkflow: "
				+ asDependentTaskWFName);

		lsDependentTaskWobNumber = new P8ProcessOperationForSolicitationFinancials().launchWorkflow(moPeSession,
				asDependentTaskWFName, aoTaskRequiredProps, HHSP8Constants.DEFAULT_SYSTEM_USER);

		// added for task audit
		if (null != asDependentTaskWFName)
		{
			String lsEntityType = HHSConstants.FINANCIAL_WF_NAME_TYPE_MAP.get(asDependentTaskWFName);
			String lsEntityId = null;
			String lsEventType = null;
			String lsTaskLevel = null;
			if (null != lsEntityType)
			{
				if (lsEntityType.equals(HHSConstants.TASK_PAYMENT_REVIEW))
				{
					lsEntityId = asInvoiceNumber;
				}
				else if (lsEntityType.equals(HHSConstants.TASK_ADVANCE_PAYMENT_REVIEW))
				{
					lsEntityId = asAdvanceBudgetId;
				}
				else
				{
					lsEntityId = asContractId;
					lsEventType = HHSConstants.APPROVED_AND_LAUNCHED;
					lsTaskLevel = HHSConstants.LEVEL + HHSConstants.TWO;
				}
				HhsAuditBean loHhsAuditBean = new HhsAuditBean();
				loHhsAuditBean.setEntityType(lsEntityType);
				loHhsAuditBean.setWorkflowId(lsDependentTaskWobNumber);
				loHhsAuditBean.setContractId(asContractId);
				loHhsAuditBean.setUserId(null != asLaunchBy ? asLaunchBy : HHSP8Constants.DEFAULT_SYSTEM_USER);
				loHhsAuditBean.setEntityId(lsEntityId);
				loHhsAuditBean.setTaskLevel(lsTaskLevel);
				loHhsAuditBean.setTaskEvent(lsEventType);
				asChannelObj.setData(HHSP8Constants.AUDIT_BEAN, loHhsAuditBean);
				LOG_OBJECT.Debug("Inserting audit for workflow launch with properties: " + loHhsAuditBean.toString());
				TransactionManager.executeTransaction(asChannelObj, HHSP8Constants.INSERT_TASK_AUDIT);
			}
		}
		return lsDependentTaskWobNumber;
	}

	/**
	 * This method will insert a record in the Audit table with the
	 * INSERT_AUDIT_DETAILS transaction. This operation is called directly from
	 * the workflow component step.
	 * 
	 * @param asEventName Name Of the Event
	 * @param asEventType Type OF the Event
	 * @param asData Data to be inserted
	 * @param asEntityType Type of Entity
	 * @param asEntityId Id of Entity
	 * @param asUserID USer ID
	 * @param asTableIdentifier Table Identifier
	 * @return boolean indicating if transaction is successful
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'insertAuditDetails'
	 */
	public boolean callAuditService(String asEventName, String asEventType, String asData, String asEntityType,
			String asEntityId, String asUserID, String asTableIdentifier) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.callAuditService.asEventName:" + asEventName);
		LOG_OBJECT.Debug("HHSComponentOperations.callAuditService.asEventType:" + asEventType);
		LOG_OBJECT.Debug("HHSComponentOperations.callAuditService.asData:" + asData);
		LOG_OBJECT.Debug("HHSComponentOperations.callAuditService.asEntityType:" + asEntityType);
		LOG_OBJECT.Debug("HHSComponentOperations.callAuditService.asEntityId:" + asEntityId);
		LOG_OBJECT.Debug("HHSComponentOperations.callAuditService.asUserID:" + asUserID);
		LOG_OBJECT.Debug("HHSComponentOperations.callAuditService.asTableIdentifier:" + asTableIdentifier);

		boolean lbAuditInserted = false;

		try
		{
			LOG_OBJECT.Debug("***************Inside  callAuditService************************");

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

			LOG_OBJECT.Debug("Executing transaction insertAuditDetails");

			// execute notification transaction
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.INSERT_AUDIT_DETAILS);

			LOG_OBJECT.Debug("Finished transaction insertAuditDetails");

			lbAuditInserted = true;

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.deleteEvaluationData()", aoAppEx);
			throw aoAppEx;
		}

		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.deleteEvaluationData()", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the callAuditService method in HHSComponentOperations", aoAppEx);
		}
		return lbAuditInserted;
	}

	/**
	 * This method deletes the Evaluation data for a given Procurement ID
	 * 
	 * Depending on the asIntExtBothFlag the method will call the
	 * DELETE_EVALUATOR_INTERNAL or/and DELETE_EVALUATOR_EXTERNAL transaction
	 * 
	 * @param asIntExtBothFlag
	 * @param asProcurementId
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'deleteEvaluatorData'
	 */
	public void deleteEvaluationData(String asIntExtBothFlag, String asProcurementId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.deleteEvaluationData.asIntExtBothFlag:" + asIntExtBothFlag);
		LOG_OBJECT.Debug("HHSComponentOperations.deleteEvaluationData.asProcurementId:" + asProcurementId);
		Channel loChannelObj = new Channel();
		HashMap<String, String> loHMap = new HashMap<String, String>();

		try
		{
			loHMap.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcurementId);
			loHMap.put(HHSP8Constants.EVALUATOR_FLAG, asIntExtBothFlag);
			loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.DELETE_EVALUATOR_DATA);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.deleteEvaluationData()", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.deleteEvaluationData()", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the deleteEvaluationData method in HHSComponentOperations", aoAppEx);
		}
	}

	/**
	 * This method will fetch the Comments from the USER_COMMENT table based on
	 * the EntityType and EntityId
	 * 
	 * @param asEntityType
	 * @param asEntityId
	 * @return lsAccComments
	 * @throws ApplicationException
	 * 
	 * 
	 *             calls the transaction 'fetchAcceleratorComments'
	 */
	public String fetchAcceleratorComments(String asEntityType, String asEntityId) throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.fetchAcceleratorComments.asEntityType:" + asEntityType);
		LOG_OBJECT.Debug("HHSComponentOperations.fetchAcceleratorComments.asEntityId:" + asEntityId);
		Channel loChannelObj = new Channel();
		String lsAccComments = null;
		HashMap<String, String> loHMap = new HashMap<String, String>();

		try
		{
			loHMap.put(HHSP8Constants.ENTITY_TYPE, asEntityType);
			loHMap.put(HHSP8Constants.ENTITY_ID, asEntityId);
			loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);

			LOG_OBJECT.Debug("Executing transaction fecthAcceleratorComments");

			// execute fetch transaction
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_ACCELERATOR_COMMENTS);
			lsAccComments = (String) loChannelObj.getData(HHSP8Constants.ACCELERATOR_COMMENTS);

			LOG_OBJECT.Debug("Finished transaction fetchAcceleratorComments");

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.fetchAcceleratorComments()", aoAppEx);
			throw aoAppEx;

		}

		return lsAccComments;
	}

	/**
	 * This method will fetch the Comments from the AGENCY_AUDIT table based on
	 * the EntityType and EntityId
	 * 
	 * @param asEntityType
	 * @param asEntityId
	 * @return lsAccComments
	 * @throws ApplicationException
	 * 
	 * 
	 *             calls the transaction 'fetchAgencyComments'
	 */
	public String fetchAgencyComments(String asEntityType, String asEntityId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.fetchAgencyComments.asEntityType:" + asEntityType);
		LOG_OBJECT.Debug("HHSComponentOperations.fetchAgencyComments.asEntityType:" + asEntityType);
		Channel loChannelObj = new Channel();
		String lsAgencyComments = null;
		HashMap<String, String> loHMap = new HashMap<String, String>();

		try
		{
			loHMap.put(HHSP8Constants.ENTITY_TYPE, asEntityType);
			loHMap.put(HHSP8Constants.ENTITY_ID, asEntityId);
			loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);

			LOG_OBJECT.Debug("Executing transaction fetchAgencyComments");

			// execute fetch transaction
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_AGENCY_COMMENTS);
			lsAgencyComments = (String) loChannelObj.getData(HHSP8Constants.AGENCY_COMMENTS);

			LOG_OBJECT.Debug("Finished transaction fetchAgencyComments");

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.fetchAgencyComments()", aoAppEx);
			throw aoAppEx;

		}

		return lsAgencyComments;
	}

	/**
	 * This method will fetch the budget_id from the Budget Table based on the
	 * contractId and FiscalYearId
	 * 
	 * @param asContractId
	 * @param asFiscalYearId
	 * @return lsBudgetId
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'fetchBudgetId'
	 */
	public String fetchBudgetId(String asContractId, String asFiscalYearId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.fetchBudgetId.asContractId:" + asContractId);
		LOG_OBJECT.Debug("HHSComponentOperations.fetchBudgetId.asFiscalYearId:" + asFiscalYearId);
		Channel loChannelObj = new Channel();
		String lsBudgetId = null;
		HashMap<String, String> loHMap = new HashMap<String, String>();

		try
		{
			loHMap.put(HHSP8Constants.CONTRACT_ID, asContractId);
			loHMap.put(HHSP8Constants.FISCAL_YEAR_ID, asFiscalYearId);
			loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);

			LOG_OBJECT.Debug("Executing transaction fetchBudgetId");

			// execute fetch transaction
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_BUDGET_ID);
			lsBudgetId = (String) loChannelObj.getData(HHSP8Constants.BUDGET_ID);

			LOG_OBJECT.Debug("Finished transaction fetchBudgetId");

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.fetchBudgetId()", aoAppEx);
			throw aoAppEx;

		}

		return lsBudgetId;
	}

	/**
	 * This method will fetch the Invoice Number from the Invoice Table based on
	 * the given invoiceId
	 * 
	 * @param asInvoiceId
	 * @return lsInvoiceNumber
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'fetchInvoiceNumber'
	 */
	public String fetchInvoiceNumber(String asInvoiceId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.fetchInvoiceNumber.asInvoiceId:" + asInvoiceId);
		Channel loChannelObj = new Channel();
		String lsInvoiceNumber = null;
		HashMap<String, String> loHMap = new HashMap<String, String>();

		try
		{
			loHMap.put(HHSP8Constants.INVOICE_ID, asInvoiceId);
			loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);

			LOG_OBJECT.Debug("Executing transaction fetchBudgetId");

			// execute fetch transaction
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_INVOICE_NUMBER);
			lsInvoiceNumber = (String) loChannelObj.getData(HHSP8Constants.INVOICE_NUMBER);

			LOG_OBJECT.Debug("Finished transaction fetchInvoiceNumber");

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.fetchInvoiceNumber()", aoAppEx);
			throw aoAppEx;

		}

		return lsInvoiceNumber;
	}

	/**
	 * This method will fetch the Advance Number from the Budget_Advance Table
	 * by the given BudgetAdvanceId
	 * 
	 * @param asBudgetAdvanceId
	 * @return lsAdvanceNumber
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'fetchAdvanceNumber'
	 */
	public String fetchAdvanceNumber(String asBudgetAdvanceId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.fetchAdvanceNumber.asBudgetAdvanceId:" + asBudgetAdvanceId);
		Channel loChannelObj = new Channel();
		String lsAdvanceNumber = null;
		HashMap<String, String> loHMap = new HashMap<String, String>();

		try
		{
			loHMap.put(HHSP8Constants.BUDGET_ADVANCE_ID, asBudgetAdvanceId);
			loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);

			LOG_OBJECT.Debug("Executing transaction fetchBudgetId");

			// execute fetch transaction
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_ADVANCE_NUMBER);
			lsAdvanceNumber = (String) loChannelObj.getData(HHSP8Constants.ADVANCE_NUMBER);

			LOG_OBJECT.Debug("Finished transaction fetchInvoiceNumber");

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.fetchInvoiceNumber()", aoAppEx);
			throw aoAppEx;

		}

		return lsAdvanceNumber;
	}

	/**
	 * This method will fetch the budget_id from the Task Details Bean by the
	 * fieldvalues.
	 * 
	 * Because the process engine cannot handle other objects than strings,
	 * integers and a number of workflow specific objects. This method will
	 * parse the given fieldvalues to a taskDetailBean that is used by the
	 * portal app, and will retrieve the budgetId from this representation.
	 * 
	 * @param aoFieldValues
	 * @return lsBudgetId
	 * @throws ApplicationException
	 */
	public String fetchBudgetIdfromTaskDetailsBean(String asFieldValues) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.fetchBudgetIdfromTaskDetailsBean.asFieldValues:" + asFieldValues);
		BudgetOperation loBudgetOperation = new BudgetOperation();
		String lsBudgetId = null;

		try
		{
			TaskDetailsBean loTaskDetailsBean = loBudgetOperation.buildTaskDetailsBean(asFieldValues);
			lsBudgetId = loTaskDetailsBean.getBudgetId();
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.fetchBudgetIdfromTaskDetailsBean()", aoAppEx);
			throw aoAppEx;

		}

		return lsBudgetId;
	}

	/**
	 * This method will fetch the budget_id and Contract Number from the Task
	 * Details Bean.
	 * 
	 * Because the process engine cannot handle other objects than strings,
	 * integers and a number of workflow specific objects. This method will
	 * parse the given fieldvalues to a taskDetailBean that is used by the
	 * portal app, and will retrieve the budgetId and ct from this
	 * representation.
	 * 
	 * The first field of the return array will contain the budget id, the
	 * second the ct.
	 * 
	 * @param aoFieldValues
	 * @return loPropertiesArray
	 * @throws ApplicationException
	 */
	public String[] fetchBudgetIdandCTfromTaskDetailsBean(String asFieldValues) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.fetchBudgetIdandCTfromTaskDetailsBean.asFieldValues:" + asFieldValues);
		BudgetOperation loBudgetOperation = new BudgetOperation();
		String[] loPropertiesArray = new String[5];

		try
		{
			TaskDetailsBean loTaskDetailsBean = loBudgetOperation.buildTaskDetailsBean(asFieldValues);
			loPropertiesArray[0] = loTaskDetailsBean.getBudgetId();
			loPropertiesArray[1] = loTaskDetailsBean.getCt();
			loPropertiesArray[2] = loTaskDetailsBean.getOrganizationId();
			loPropertiesArray[3] = loTaskDetailsBean.getProcurementTitle();
			loPropertiesArray[4] = loTaskDetailsBean.getContractId();

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.fetchBudgetIdfromTaskDetailsBean()", aoAppEx);
			throw aoAppEx;

		}

		return loPropertiesArray;
	}

	/**
	 * This method will fetch the Evaluator Email Ids from the DB based on the
	 * Procurement Id
	 * 
	 * @param asProcurementId
	 * @return loEvalIds
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'fetchEvaluatorIdsEvaluation'
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public String[] fetchEvaluatorIdsEvaluationWorkFlow(String asProcurementId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.fetchEvaluatorIdsEvaluationWorkFlow.asProcurementId:"
				+ asProcurementId);
		Channel loChannelObj = new Channel();
		List<String> loEvalIdList = null;
		String[] loEvalIdArray = null;
		HashMap<String, String> loHMap = new HashMap<String, String>();

		try
		{
			loHMap.put(HHSP8Constants.PROCUREMENT_ID, asProcurementId);
			loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);

			LOG_OBJECT.Debug("Executing transaction fetchEvaluatorIdsEvaluationWorkFlow");

			// execute fetch transaction
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_EVALUATOR_ID_EVALUATION);
			loEvalIdList = (List<String>) loChannelObj.getData(HHSP8Constants.EVALUATOR_IDS);

			LOG_OBJECT.Debug("Finished transaction fetchEvaluatorIdsEvaluationWorkFlow");

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.fetchEvaluatorIdsEvaluationWorkFlow()", aoAppEx);
			throw aoAppEx;
		}
		// handling Application Exception thrown by any action/resource
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.fetchEvaluatorIdsEvaluationWorkFlow()", aoAppEx);
			throw new ApplicationException("Exception in HHSComponentOperations.fetchEvaluatorIdsEvaluationWorkFlow()",
					aoAppEx);
		}

		loEvalIdArray = new String[loEvalIdList.size()];
		loEvalIdArray = loEvalIdList.toArray(loEvalIdArray);

		return loEvalIdArray;
	}

	/**
	 * This method will fetch the Evaluator Ids based on the Proposal ID.
	 * 
	 * @param asProposalId
	 * @return loEvalIds
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'fetchEvaluatorIdsReviewScore'
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public String[] fetchEvaluatorIdsReviewScoreWorkFlow(String asProposalId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.fetchEvaluatorIdsReviewScoreWorkFlow.asProposalId:" + asProposalId);
		Channel loChannelObj = new Channel();
		List<String> loEvalIdList = null;
		String[] loEvalIdArray = null;
		HashMap<String, String> loHMap = new HashMap<String, String>();

		try
		{
			loHMap.put(HHSP8Constants.PROPOSAL_ID, asProposalId);
			loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);

			LOG_OBJECT.Debug("Executing transaction fetchEvaluatorIdsReviewScoreWorkFlow");

			// execute fetch transaction
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_EVALUATOR_ID_REVIEW_SCORE);
			loEvalIdList = (List<String>) loChannelObj.getData(HHSP8Constants.EVALUATOR_IDS);

			LOG_OBJECT.Debug("Finished transaction fetchEvaluatorIdsReviewScoreWorkFlow");

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.fetchEvaluatorIdsReviewScoreWorkFlow", aoAppEx);
			throw aoAppEx;

		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.fetchEvaluatorIdsReviewScoreWorkFlow", aoAppEx);
			throw new ApplicationException("Exception in HHSComponentOperations.fetchEvaluatorIdsReviewScoreWorkFlow",
					aoAppEx);
		}

		// The workflow cannot handle arraylists, there for the arraylist is
		// parsed to a string array.
		loEvalIdArray = new String[loEvalIdList.size()];
		loEvalIdArray = loEvalIdList.toArray(loEvalIdArray);

		return loEvalIdArray;
	}

	/**
	 * This function is used for calling the notification service for doc
	 * lapsing
	 * 
	 * @param aoProperties String array of Properties
	 * @param asNotificationName String denoting the notification name
	 * @param asAlertName String denoting the alert name
	 * @param asAudienceType String denoting the Audience Type
	 * @param aoAudienceList String array of Audience List
	 * @throws ApplicationException
	 * 
	 * 
	 *             calls the transaction 'insertNotificationDetail'
	 */

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean callNotificationService(String asNotificationName, String asAlertName, String[] aoProperties,
			String asAudienceType, String[] aoAudienceList) throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.callNotificationService.asNotificationName:" + asNotificationName);
		LOG_OBJECT.Debug("HHSComponentOperations.callNotificationService.asAlertName:" + asAlertName);
		LOG_OBJECT.Debug("HHSComponentOperations.callNotificationService.aoProperties:" + aoProperties);
		LOG_OBJECT.Debug("HHSComponentOperations.callNotificationService.asAudienceType:" + asAudienceType);
		LOG_OBJECT.Debug("HHSComponentOperations.callNotificationService.aoAudienceList:" + aoAudienceList);
		
		NotificationOperations loNotificationOperations = new NotificationOperations();
		boolean lbNotificationInserted = false;
		HashMap loRequestMap = new HashMap();
		String[] loValuePair = null;
		List<String> loAudienceList = new ArrayList<String>();
		try
		{
			LOG_OBJECT.Debug("***************Inside  callNotificationService************************");
			
			NotificationDataBean loNotificationBean = new NotificationDataBean();

			HashMap<String, Object> loNotificationMap = new HashMap<String, Object>();
			loNotificationOperations.getNotificationLink(asNotificationName, asAlertName, aoProperties,
					loNotificationBean, loNotificationMap, aoAudienceList);
			// Create the data for sending notifications
			ArrayList<String> loAlertsList = new ArrayList<String>();
			if (!asNotificationName.equalsIgnoreCase(""))
			{
				loAlertsList.add(asNotificationName);
				loNotificationMap.put(asNotificationName, loNotificationBean);
			}
			if (!asAlertName.equalsIgnoreCase(""))
			{
				loAlertsList.add(asAlertName);
				loNotificationMap.put(asAlertName, loNotificationBean);
			}

			if (null != asNotificationName && !asNotificationName.equalsIgnoreCase(HHSP8Constants.NT210))
			{
				loAudienceList.addAll(Arrays.asList(aoAudienceList));
				loNotificationBean.setProviderList(loAudienceList);
				loNotificationBean.setAgencyList(loAudienceList);
			}
			else if (asNotificationName.equalsIgnoreCase(HHSP8Constants.NT210))
			{
				// Condition removed for resolving defect #6434 in Release 3.2.0
				// Condition added for resolving issue for defect 6385 in
				// Release 3.1.0
				loAudienceList.addAll(Arrays.asList(aoAudienceList));
				loNotificationBean.setAgencyList(loAudienceList);
			}
			loNotificationMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loAlertsList);// added
			StringBuffer loNotificationLINK = new StringBuffer();

			LOG_OBJECT.Info("callNotificationService: Notification LINK is: " + loNotificationLINK);
			// Iterate through the array by splitting each object (aoExp.g.
			// param name:param value)
			// setting all parameters to be used by the transaction

			for (String lsProperty : aoProperties)
			{
				loValuePair = lsProperty.split("[:]", 2);

				if (loValuePair.length >= 2)
				{
					loRequestMap.put(loValuePair[0], loValuePair[1]);
				}
				else
				{
					loRequestMap.put(loValuePair[0], "");
				}
			}
			
						
			// Start QC 8963 R 8.10 AL311 provider notification alert missing agency name
			if (HHSP8Constants.NT311.equalsIgnoreCase(asNotificationName) || HHSP8Constants.AL311.equalsIgnoreCase(asAlertName))
			{
				// define agency name
				
				String contractId = (String)loRequestMap.get("ContractID");
				String budgetId = (String)loRequestMap.get("BudgetID");
				LOG_OBJECT.Debug("***ContractID :: " + contractId);
				LOG_OBJECT.Debug("***BudgetID :: " + budgetId);
				
				Channel loChannelObj = new Channel();					
				loChannelObj.setData("aoEntityId", budgetId);

				LOG_OBJECT.Debug("Executing transaction fetchAgencyNameFromBudge");

				// execute fetch transaction
				TransactionManager.executeTransaction(loChannelObj, HHSConstants.FETCH_AGENCY_NAME_FROM_BUDGET);
				String lsAgencyName = (String) loChannelObj.getData(HHSConstants.AGENCY_NAME);
				loRequestMap.put("AGENCY_NAME", lsAgencyName);
				LOG_OBJECT.Debug("***AGENCY_NAME :: " + lsAgencyName);
				LOG_OBJECT.Debug("Finished transaction fetchAgencyNameFromBudge");
				
			}
			// End QC 8963 R 8.10 AL311 provider notification alert missing agency name
			LOG_OBJECT.Debug("call Notification service: Request map:" + loRequestMap.toString());
			
			loNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			loNotificationMap.put(HHSConstants.CREATED_BY_USER_ID, HHSConstants.SYSTEM_USER);
			loNotificationMap.put(HHSConstants.MODIFIED_BY, HHSConstants.SYSTEM_USER);
			loNotificationMap.put(HHSP8Constants.EVENT_ID_PARAMETER_NAME, loAlertsList);
			loNotificationMap.put(HHSP8Constants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSP8Constants.LO_HM_NOTIFY_PARAM, loNotificationMap);
			loChannelObj.setData(HHSP8Constants.ALERT_FLAG, true);

			LOG_OBJECT.Debug("Executing transaction insertNotificationDetail:" + loNotificationMap.toString());

			// execute notification transaction
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.TRANSACTION_ID_INSERT_NOTIFICATION);

			LOG_OBJECT.Debug("Finished transaction insertNotificationDetail");

			lbNotificationInserted = true;

		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in callNotificationService()", aoAppEx);
			throw aoAppEx;

		}
		// handling Application Exception thrown by any action/resource
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception in callNotificationService()", aoEx);
			throw new ApplicationException("Exception in callNotificationService()", aoEx);
		}
		return lbNotificationInserted;
	}

	/**
	 * This method will set the Task to unassigned level if assigned user is
	 * removed from that level by agency
	 * 
	 * @param asTaskType taskType
	 * @param asAgencyID agencyID
	 * @param aoUsers List of Users
	 * @return boolean
	 * @throws ApplicationException
	 * @throws VWException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public boolean setTaskUnassignedForRemovedAgencyUsers(String asTaskType, String asAgencyID, String[] aoUsers)
			throws ApplicationException, VWException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.setTaskUnassignedForRemovedAgencyUsers.asTaskType:" + asTaskType);
		LOG_OBJECT.Debug("HHSComponentOperations.setTaskUnassignedForRemovedAgencyUsers.asAgencyID:" + asAgencyID);
		LOG_OBJECT.Debug("HHSComponentOperations.setTaskUnassignedForRemovedAgencyUsers.aoUsers:" + aoUsers);

		// Initialize the used classes
		P8ProcessOperationForSolicitationFinancials loP8OperationObj = new P8ProcessOperationForSolicitationFinancials();
		P8ProcessServiceForSolicitationFinancials loP8ServiceObj = new P8ProcessServiceForSolicitationFinancials();
		// Initialize the variables
		P8UserSession loUserSession = null;
		String lsViewName = null;
		String lsWhereClause = null;
		HashMap loWFProperties = new HashMap();
		int liPERegionId = HHSP8Constants.INT_ZERO;
		String lsWobNum = HHSP8Constants.EMPTY_STRING;
		String lsCurrentLevel = HHSP8Constants.EMPTY_STRING;
		String lsCurrentUnassigned = HHSP8Constants.EMPTY_STRING;

		// Initialize the user session
		P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
		loUserSession = loFilenetConnection.getFileNetConnection(loFilenetConnection.setP8SessionVariables());
		loUserSession.setFilenetPEDBSession(HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession());

		liPERegionId = moPeSession.getIsolatedRegion();
		lsViewName = loP8ServiceObj.getPEViewName(HHSP8Constants.HSS_QUEUE_NAME, liPERegionId);
		Statement loStatement = null;
		java.sql.Connection loConnection = loUserSession.getFilenetPEDBSession().getConnection();
		HashMap loHMreqd = new HashMap();
		ResultSet loResultSet = null;

		loWFProperties.put(HHSP8Constants.PROPERTY_PE_TASK_TYPE, asTaskType);
		loWFProperties.put(HHSP8Constants.PROPERTY_PE_AGENCY_ID, asAgencyID);

		try
		{
			// For each user find the workflows on its name, and reset the task
			// assigned
			for (String lsUser : aoUsers)
			{
				loWFProperties.put(HHSP8Constants.PROPERTY_PE_ASSIGNED_TO, lsUser);
				lsWhereClause = loP8ServiceObj.createWhereClause(loWFProperties);

				if (null == lsWhereClause || lsWhereClause.isEmpty())
				{
					throw new ApplicationException(
							"Exception in HHSComponentOperations.setTaskUnassignedForRemovedAgencyUsers() :Where Condition is not set");
				}

				loStatement = loConnection.createStatement();
				StringBuffer loQuery = new StringBuffer(HHSP8Constants.EMPTY_STRING);
				loQuery.append("select \"F_WobNum\",\"CurrentLevel\" from ");
				loQuery.append(lsViewName);
				loQuery.append(" where ");
				loQuery.append(lsWhereClause);
				loResultSet = loP8OperationObj.executeViewQuery(loUserSession.getFilenetPEDBSession(),
						loQuery.toString(), loStatement);

				while (loResultSet.next())
				{
					lsWobNum = loResultSet.getString(HHSP8Constants.F_WOB_NUM);
					lsCurrentLevel = loResultSet.getString(HHSP8Constants.CURR_LEVEL);
					lsCurrentUnassigned = HHSP8Constants.TASK_UNASSIGNED + lsCurrentLevel;
					loHMreqd.put(HHSP8Constants.PROPERTY_PE_ASSIGNED_TO, lsCurrentUnassigned);
					loHMreqd.put(HHSP8Constants.PROPERTY_PE_ASSIGNED_TO_NAME, lsCurrentUnassigned);
					loP8OperationObj.setWFProperty(loUserSession, lsWobNum, loHMreqd);
				}
			}
		}
		// handling SQLException thrown by any action/resource
		catch (SQLException aoSQLEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in setTaskUnassignedForRemovedAgencyUsers:: ", aoSQLEx);
			LOG_OBJECT.Error("Exception in HHSComponentOperations.setTaskUnassignedForRemovedAgencyUsers::", aoSQLEx);
			throw loAppex;
		}

		finally
		{
			P8ProcessOperationForSolicitationFinancials.closeFilenetDBResources(loResultSet, loStatement);
			if (null != loUserSession && null != loUserSession.getFilenetPEDBSession())
			{
				loUserSession.getFilenetPEDBSession().close();
			}

		}

		return true;
	}

	/**
	 * This method will insert the line items for the contract config tasks.
	 * 
	 * <ul>
	 * <li>Fetch list of all subBudget and get Budget id for fiscal year and
	 * contract id provided as input parameter</li> </li>For each subBudget call
	 * all tabs that has fixed line items default entries</li>
	 * </ul>
	 * 
	 * @param fieldValues values to be set in the TaskDetailsBean
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'insertLineItemsForContractConfigTasks'
	 */
	public void insertLineItemsForContractConfigTask(String aoFieldValues) throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.insertLineItemsForContractConfigTask.aoFieldValues:" + aoFieldValues);
		BudgetOperation loBudgetOperation = new BudgetOperation();
		Channel loChannelObjForContractConfig = new Channel();

		try
		{
			TaskDetailsBean loTaskDetailsBean = loBudgetOperation.buildTaskDetailsBean(aoFieldValues);
			// On this moment we have a taskDetailbean with the correct data
			loChannelObjForContractConfig.setData(HHSP8Constants.TASK_DETAILS_BEAN, loTaskDetailsBean);

			// Call the correct method: for transaction
			TransactionManager.executeTransaction(loChannelObjForContractConfig,
					HHSP8Constants.TRANSACTION_ID_INSERT_ITEMS_CONTRACT_CONFIG);

			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(aoAppEx.getMessage(), aoAppEx);
			throw aoAppEx;
		}
	}

	/**
	 * This method is used to create replica of budget starting from Contract
	 * till line item level when Contract budget review task is final Approved.
	 * 
	 * <ul>
	 * <li>1. Check if Original Contract record already exist for base contract.
	 * if count is zero create replica of contract with type as Original</li>
	 * <li>2. Check if Original Contract record already exist for base contract.
	 * if count is zero create replica of contract with type as Original</li>
	 * </ul>
	 * 
	 * @param fieldValues values to be set in the TaskDetailsBean
	 * @param contractBudgetStatus String Contract Budget Status
	 * @throws ApplicationException
	 * 
	 * 
	 *             calls the transaction 'createReplicaForBudgetReviewTasks'
	 */
	public void insertCreateReplicaForBudgetReviewTask(String aoFieldValues, String asContractBudgetStatus)
			throws ApplicationException
	{

		LOG_OBJECT
				.Debug("HHSComponentOperations.insertCreateReplicaForBudgetReviewTask.aoFieldValues:" + aoFieldValues);
		LOG_OBJECT.Debug("HHSComponentOperations.insertCreateReplicaForBudgetReviewTask.asContractBudgetStatus:"
				+ asContractBudgetStatus);
		BudgetOperation loBudgetOperation = new BudgetOperation();
		Channel loChannelObjForBudgetReview = new Channel();

		try
		{
			TaskDetailsBean loTaskDetailsBean = loBudgetOperation.buildTaskDetailsBean(aoFieldValues);
			// On this moment we have a taskDetailbean with the correct data
			loChannelObjForBudgetReview.setData(HHSP8Constants.TASK_DETAILS_BEAN, loTaskDetailsBean);
			loChannelObjForBudgetReview.setData(HHSP8Constants.CONTRACT_BUDGET_STATUS, asContractBudgetStatus);
			// Call the correct method: for transaction
			TransactionManager.executeTransaction(loChannelObjForBudgetReview,
					HHSP8Constants.TRANSACTION_ID_CREATE_REPLICA_BUDGET_REVIEW_TASKS);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(aoAppEx.getMessage(), aoAppEx);
			throw aoAppEx;
		}
	}

	/**
	 * 
	 * This method is used to merge budget, subBudget and line-items with their
	 * respective modification entries when Contract budget Modification review
	 * task is final Approved.
	 * 
	 * <ul>
	 * <li>1. Merge LineItems where-ever we have modification in existing line
	 * items</li>
	 * <li>2. Create new Line Item and link with Base SubBudget for newly added
	 * line-items during Modification</li>
	 * <li>Two service method are added for agency interface module.It update
	 * remaining and YTD invoiced amount in line items table when line items are
	 * merged.</li>
	 * <li>calls the transaction 'mergeBudgetForModificationReviewTasks'</li>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * 
	 * @param aoFieldValues values to be set in the TaskDetailsBean
	 * @param asContractBudgetStatus String Contract Budget Status
	 * @throws ApplicationException
	 * 
	 * 
	 */
	public void mergeBudgetForModificationReviewTask(String aoFieldValues, String asContractBudgetStatus)
			throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.mergeBudgetForModificationReviewTask.aoFieldValues:" + aoFieldValues);
		LOG_OBJECT.Debug("HHSComponentOperations.mergeBudgetForModificationReviewTask.asContractBudgetStatus:"
				+ asContractBudgetStatus);
		BudgetOperation loBudgetOperation = new BudgetOperation();
		Channel loChannelObjForModification = new Channel();
		//R7 changes start
		HashMap aoRequiredMap = new HashMap();
		//R7 changes end
		try
		{
			TaskDetailsBean loTaskDetailsBean = loBudgetOperation.buildTaskDetailsBean(aoFieldValues);
			// On this moment we have a taskDetailbean with the correct data

			String lsBudgetId = loTaskDetailsBean.getBudgetId(); // Added for R4
																	// changes
			loChannelObjForModification.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
			loChannelObjForModification.setData(HHSConstants.MERGE_FLAG, true);
			// enhancement 6644
			loChannelObjForModification.setData(HHSP8Constants.TASK_DETAILS_BEAN, loTaskDetailsBean);
			loChannelObjForModification.setData(HHSP8Constants.CONTRACT_BUDGET_STATUS, asContractBudgetStatus);
			// changes for agency outbound interafce 6644
			loChannelObjForModification.setData(HHSP8Constants.AS_USER_ID, loTaskDetailsBean.getUserId());
			// Call the correct method: for transaction
			//R7 changes start
			loChannelObjForModification.setData(HHSConstants.AO_FILENET_SESSION, loFilenetSession);
			loChannelObjForModification.setData(HHSP8Constants.LOHMAP, aoRequiredMap);
			//R7 changes end
			TransactionManager.executeTransaction(loChannelObjForModification,
					HHSP8Constants.TRANSACTION_ID_MERGE_BUDGET_MODIFICATION_REVIEW_TASKS);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(aoAppEx.getMessage(), aoAppEx);
			throw aoAppEx;
		}
	}

	/**
	 * This method is used to merge budget, subBudget and line-items with their
	 * respective modification entries when Contract budget Update review task
	 * is final Approved.
	 * 
	 * <ul>
	 * <li>1. Merge LineItems where-ever we have modification in existing line
	 * items</li>
	 * <li>2. Create new Line Item and link with Base SubBudget for newly added
	 * line-items during Update</li>
	 * <li>Two service method are added for agency interface module. It update
	 * remaining and YTD invoiced amount in line items table on approval of
	 * budget update task.</li>
	 * <li>calls the transaction 'mergeBudgetForUpdateReviewTasks'</li>
	 * <li>This method was updated in R4.</li>
	 * </ul>
	 * @param aoFieldValues values to be set in the TaskDetailsBean
	 * @param asContractBudgetStatus String containing Contract Budget Status
	 * @throws ApplicationException
	 * 
	 */
	public void mergeBudgetForUpdateReviewTask(String aoFieldValues, String asContractBudgetStatus)
			throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.mergeBudgetForUpdateReviewTask.aoFieldValues:" + aoFieldValues);
		LOG_OBJECT.Debug("HHSComponentOperations.mergeBudgetForUpdateReviewTask.asContractBudgetStatus:"
				+ asContractBudgetStatus);
		BudgetOperation loBudgetOperation = new BudgetOperation();
		Channel loChannelObjForUpdate = new Channel();

		try
		{
			TaskDetailsBean loTaskDetailsBean = loBudgetOperation.buildTaskDetailsBean(aoFieldValues);
			// On this moment we have a taskDetailbean with the correct data

			String lsBudgetId = loTaskDetailsBean.getBudgetId(); // Added for R4
																	// changes
			loChannelObjForUpdate.setData(HHSConstants.BUDGET_ID_KEY, lsBudgetId);
			loChannelObjForUpdate.setData(HHSConstants.MERGE_FLAG, true);
			loChannelObjForUpdate.setData(HHSP8Constants.TASK_DETAILS_BEAN, loTaskDetailsBean);
			loChannelObjForUpdate.setData(HHSP8Constants.CONTRACT_BUDGET_STATUS, asContractBudgetStatus);
			// changes for agency outbound interafce 6644
			loChannelObjForUpdate.setData(HHSP8Constants.AS_USER_ID, loTaskDetailsBean.getUserId());
			// Call the correct method: for transaction
			TransactionManager.executeTransaction(loChannelObjForUpdate,
					HHSP8Constants.TRANSACTION_ID_MERGE_BUDGET_UPDATE_REVIEW_TASKS);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(aoAppEx.getMessage(), aoAppEx);
			throw aoAppEx;
		}
	}

	/**
	 * This method is used to merge budget, subBudget and line-items with their
	 * respective modification entries when Contract budget Amendment review
	 * task is final Approved.
	 * 
	 * <ul>
	 * <li>1. Merge LineItems where-ever we have modification in existing line
	 * items</li>
	 * <li>2. Create new Line Item and link with Base SubBudget for newly added
	 * line-items during Update</li>
	 * <li>Two service method are added for agency interface module.It update
	 * remaining and YTD invoiced amount in line items table on amendment budget
	 * task approval.</li>
	 * <li>calls the transaction 'mergeBudgetForAmendmentReviewTasks'</li>
	 * <li>This method was updated in R4.</li>
	 * </ul>
	 * 
	 * @param aoFieldValues values to be set in the TaskDetailsBean
	 * @param asContractBudgetStatus String containing Contract Budget Status
	 * @param aiApprovedBudgetCount Integer containing Approved Budget Count
	 * @throws ApplicationException
	 */
	public void mergeBudgetForAmendmentReviewTask(String aoFieldValues, String asContractBudgetStatus,
			Integer aiApprovedBudgetCount) throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.mergeBudgetForAmendmentReviewTask.aoFieldValues:" + aoFieldValues);
		LOG_OBJECT.Debug("HHSComponentOperations.mergeBudgetForAmendmentReviewTask.asContractBudgetStatus:"
				+ asContractBudgetStatus);
		LOG_OBJECT.Debug("HHSComponentOperations.mergeBudgetForAmendmentReviewTask.aiApprovedBudgetCount:"
				+ aiApprovedBudgetCount);
		BudgetOperation loBudgetOperation = new BudgetOperation();
		Channel loChannelObjForAmend = new Channel();
		boolean lbMergeFlag = false;

		try
		{
			TaskDetailsBean loTaskDetailsBean = loBudgetOperation.buildTaskDetailsBean(aoFieldValues);
			// On this moment we have a taskDetailbean with the correct data
			if (HHSConstants.NEGATIVE.equalsIgnoreCase(loTaskDetailsBean.getAmendmentType()))
			{
				lbMergeFlag = true;
			}
			loChannelObjForAmend.setData(HHSConstants.BUDGET_ID_KEY, loTaskDetailsBean.getBudgetId());
			loChannelObjForAmend.setData(HHSConstants.MERGE_FLAG, lbMergeFlag);
			loChannelObjForAmend.setData(HHSP8Constants.TASK_DETAILS_BEAN, loTaskDetailsBean);
			loChannelObjForAmend.setData(HHSP8Constants.CONTRACT_BUDGET_STATUS, asContractBudgetStatus);
			loChannelObjForAmend.setData(HHSP8Constants.APPROVED_BUDGET_COUNT, aiApprovedBudgetCount);
			// Call the correct method: for transaction
			// changes for agency outbound interafce 6644
			loChannelObjForAmend.setData(HHSP8Constants.AS_USER_ID, loTaskDetailsBean.getUserId());
			TransactionManager.executeTransaction(loChannelObjForAmend,
					HHSP8Constants.TRANSACTION_ID_MERGE_BUDGET_AMENDMENT_REVIEW_TASKS);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(aoAppEx.getMessage(), aoAppEx);
			throw aoAppEx;
		}
	}

	/**
	 * This method is used to insert Old Sub Budget Line items copies.
	 * 
	 * @param fieldValues values to be set in the TaskDetailsBean
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'insertOldSubBudgetLineItemsZeroCopy'
	 */
	public void insertOldSubBudgetLineItemsZeroCopy(String aoFieldValues) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.insertOldSubBudgetLineItemsZeroCopy.aoFieldValues:" + aoFieldValues);
		BudgetOperation loBudgetOperation = new BudgetOperation();
		Channel loChannelObj = new Channel();

		try
		{
			TaskDetailsBean loTaskDetailsBean = loBudgetOperation.buildTaskDetailsBean(aoFieldValues);
			// On this moment we have a taskDetailbean with the correct data

			loChannelObj.setData(HHSP8Constants.TASK_DETAILS_BEAN, loTaskDetailsBean);
			// Call the correct method: for transaction
			TransactionManager.executeTransaction(loChannelObj,
					HHSP8Constants.TRANSACTION_ID_INSERT_OLD_SUB_BUDGET_LINE_ITEMS_ZERO_COPY);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(aoAppEx.getMessage(), aoAppEx);
			throw aoAppEx;
		}
	}

	/**
	 * This method gives the number of selected proposals for a given
	 * procurement ID.
	 * 
	 * @param asProcurementId
	 * @return liProposalCount Proposal Count
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'fetchCountofSelectedProposals'
	 */
	public Integer findNumberOfSelectedProposals(String asProcurementId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.findNumberOfSelectedProposals.asProcurementId:" + asProcurementId);
		Integer loProposalCount = null;
		Channel loChannel = new Channel();
		HashMap<String, String> loInputParam = new HashMap<String, String>();
		loInputParam.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcurementId);
		loChannel.setData(HHSP8Constants.LOHMAP, loInputParam);

		try
		{
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_SELECTED_PROPOSALS_COUNT);
			loProposalCount = (Integer) loChannel.getData(HHSP8Constants.PROPOSAL_COUNT);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(aoAppEx.getMessage(), aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error(aoAppEx.getMessage(), aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the findNumberOfSelectedProposals method in WorkflowOperations",
					aoAppEx);
		}
		return loProposalCount;
	}

	/**
	 * This method will reopen the selected evaluation tasks for a given
	 * Proposal ID.
	 * 
	 * First it will retrieve the evaluation ids for the given proposal id. Next
	 * it will call the triggerWorkflowStepVisibility method from the
	 * WorkflowOperations class to change the visility of the
	 * WORKFLOW_NAME_202_EVALUATION workflows to true. calls the transaction
	 * 'fetchReopenedEvaluationTaskIds'
	 * @param proposalId
	 * @throws ApplicationException
	 * 
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public void reopenSelectedEvaluationTasks(String asProposalId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.reopenSelectedEvaluationTasks.asProposalId:" + asProposalId);
		Channel loChannel = new Channel();
		HashMap<String, String> loInputParam = new HashMap<String, String>();
		List<String> loEvaluationIds = null;
		loInputParam.put(HHSP8Constants.PROPOSAL_ID, asProposalId);
		loChannel.setData(HHSP8Constants.LOHMAP, loInputParam);

		try
		{
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_REOPENED_EVALUATION_TASK_IDS);
			loEvaluationIds = (List<String>) loChannel.getData(HHSP8Constants.EVALUATION_IDS);
									
			// Find all the evaluation tasks that are reopend by the portal app
			// with a query
			// Store the found evaluationIds into the evaluationIds array.
			LOG_OBJECT.Debug("HHSComponentOperations.reopenSelectedEvaluationTasks Items which are returned:  "
					+ loEvaluationIds);
			WorkflowOperations loWfo = new WorkflowOperations(LOG_OBJECT);
			for (String lsEvaluationId : loEvaluationIds)
			{
				LOG_OBJECT.Debug("HHSComponentOperations.reopenSelectedEvaluationTasks: Evaluation Id in while Loop: "
						+ lsEvaluationId);
				String lsFilter = HHSP8Constants.EVALUATION_ID + "='" + lsEvaluationId + "'";

				loWfo.triggerWorkflowStepVisilibity(moPeSession, lsFilter, true,
						HHSP8Constants.WORKFLOW_NAME_202_EVALUATION, HHSP8Constants.SCORES_RETURNED);
			}
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException in HHSComponentOperations.deleteEvaluationData()", aoAppEx);
			throw aoAppEx;
			// handling WcmException thrown by any action/resource
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException in HHSComponentOperations.deleteEvaluationData()", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the deleteEvaluationData method in HHSComponentOperations", aoAppEx);
			// handling WcmException thrown by any action/resource
		}
	}

	/**
	 * This method cancels the Evaluation Tasks for the given procurement ID
	 * 
	 * First the evaluations data in the db will be removed for the given
	 * procurement Second the workflows will be removed for the given
	 * procurement id. The workflows to be removed are:
	 * WORKFLOW_NAME_202_EVALUATION, WORKFLOW_NAME_202_EVALUATE_PROPOSAL_MAIN,
	 * WORKFLOW_NAME_202_REVIEW_SCORES calls the transaction
	 * 'cancelEvaluationTask'
	 * @param asProcurementId
	 * @throws ApplicationException
	 * 
	 */
	public void cancelEvaluationTasks(String asProcurementId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.cancelEvaluationTasks.asProcurementId:" + asProcurementId);
		HashMap<String, String> loHmReqExceProp = new HashMap<String, String>();
		P8UserSession loUserSession = null;
		HashMap<String, Object> loCancelEvaluationTaskMap = new HashMap<String, Object>();

		try
		{
			LOG_OBJECT.Debug("Entered into cancelEvaluationTasks::" + loHmReqExceProp.toString());
			P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
			loUserSession = loFilenetConnection.getFileNetConnection(loFilenetConnection.setP8SessionVariables());
			loUserSession.setFilenetPEDBSession(HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
					.openSession());
			Channel loChannel = new Channel();
			loCancelEvaluationTaskMap.put(HHSP8Constants.PE_WORKFLOW_PROCUREMENT_ID, asProcurementId);
			loChannel.setData(HHSP8Constants.PROCUREMENT_ID, asProcurementId);
			loChannel.setData(HHSP8Constants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSP8Constants.AO_HMWF_REQUIRED_PROPS, loCancelEvaluationTaskMap);

			TransactionManager.executeTransaction(loChannel, HHSP8Constants.CANCEL_EVALUATION_TASKS);

			// Remove the all the evaluation workflows and evaluation main
			// workflows
			WorkflowOperations loWFO = new WorkflowOperations(LOG_OBJECT);

			String lsFilter = HHSP8Constants.PE_WORKFLOW_PROCUREMENT_ID + "='" + asProcurementId + "'";
			String[] loWorkflowNames =
			{ HHSP8Constants.WORKFLOW_NAME_202_EVALUATION, HHSP8Constants.WORKFLOW_NAME_202_EVALUATE_PROPOSAL_MAIN,
					HHSP8Constants.WORKFLOW_NAME_202_REVIEW_SCORES };
			loWFO.cancelWorkflows(lsFilter, loWorkflowNames, moPeSession);
		}
		// Handling Application Exception
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException Occured while Cancel Evaluation Tasks:", aoAppEx);
			throw aoAppEx;
		}
		// Handling Exceptions other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while Cancel Evaluation Tasks:", aoEx);
			throw new ApplicationException(
					"Exception occured during executing the cancelEvaluationTasks method in HHSComponentOperations",
					aoEx);
		}
		finally
		{
			if (null != loUserSession && null != loUserSession.getFilenetPEDBSession())
			{
				loUserSession.getFilenetPEDBSession().close();
			}

		}
	}

	/**
	 * This method will suspend all the financial tasks that are related to the
	 * given contract id by using the SUSPEND_ALL_FINANCIAL_TASKS transaction.
	 * calls the transaction 'suspendAllFinancialTasks'
	 * @param asContractId
	 * @throws ApplicationException
	 */
	public void suspendAlllFinancialTasks(String asContractId, String asUserId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.suspendAlllFinancialTasks.asContractId:" + asContractId);
		Channel loChannel = new Channel();
		HashMap<String, String> loHmWFProperties = new HashMap<String, String>();
		P8UserSession loUserSession = null;
		try
		{
			P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
			loUserSession = loFilenetConnection.setP8SessionVariables();
			loUserSession.setFilenetPEDBSession(HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
					.openSession());
			loHmWFProperties.put(HHSP8Constants.CONTRACT_ID, asContractId);
			loHmWFProperties.put(HHSConstants.USER_ID, asUserId);
			loChannel.setData(HHSP8Constants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSP8Constants.LOHMAP, loHmWFProperties);
			loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.SUSPEND_ALL_FINANCIAL_TASKS);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception Occured while Cancel Evaluation Tasks:", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the cancelEvaluationTasks method in HHSComponentOperations",
					aoAppEx);
		}
		finally
		{
			if (null != loUserSession && null != loUserSession.getFilenetPEDBSession())
			{
				loUserSession.getFilenetPEDBSession().close();
			}
		}
	}

	/**
	 * This method will unsuspend all the financial tasks that are related to
	 * the given contract id by using the UNSUSPEND_ALL_FINANCIAL_TASKS
	 * transaction. calls the transaction 'unSuspendALLFinancialTasks'
	 * @param asContractId
	 * @throws ApplicationException
	 */
	public void unSuspendAllFinancialTasks(String asContractId, String asUserId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.unSuspendAllFinancialTasks.asContractId:" + asContractId);
		Channel loChannel = new Channel();
		HashMap<String, String> loHmWFProperties = new HashMap<String, String>();
		P8UserSession loUserSession = null;
		try
		{
			P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
			loUserSession = loFilenetConnection.setP8SessionVariables();
			loUserSession.setFilenetPEDBSession(HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
					.openSession());
			loHmWFProperties.put(HHSP8Constants.CONTRACT_ID, asContractId);
			loHmWFProperties.put(HHSConstants.USER_ID, asUserId);
			loChannel.setData(HHSP8Constants.AO_FILENET_SESSION, loUserSession);
			loChannel.setData(HHSP8Constants.LOHMAP, loHmWFProperties);
			loChannel.setData(HHSConstants.NFCTH_LB_AUDIT_TRUE, true);
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.UNSUSPEND_ALL_FINANCIAL_TASKS);
		}
		catch (ApplicationException aoEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.deleteEvaluationData()", aoEx);
			throw aoEx;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.deleteEvaluationData()", aoEx);
			throw new ApplicationException(
					"Error occured during executing the deleteEvaluationData method in HHSComponentOperations", aoEx);
		}
		finally
		{
			if (null != loUserSession.getFilenetPEDBSession())
			{
				loUserSession.getFilenetPEDBSession().close();
			}
		}
	}

	// R5 change starts
	/**
	 * This method will handle the alertNotifications from the award workflow.
	 * 
	 * calls the method 'handleAlertNotificationsForAward'
	 * 
	 * @param asTaskStatus
	 * @param asProcurementId
	 * @param asProcurementTitle
	 * @param aoSelectedProviders
	 * @param aoNotSelectedProviders
	 */
	public void handleAlertNotificationsForAward(String asTaskStatus, String asProcurementId,
			String asProcurementTitle, String[] aoSelectedProviders, String[] aoNotSelectedProviders)
			throws ApplicationException
	{
		handleAlertNotificationsForAward(asTaskStatus, asProcurementId, asProcurementTitle, aoSelectedProviders,
				aoNotSelectedProviders, false);
	}

	// R5 change ends
	/**
	 * This method is updated for Release 5. This method will handle the
	 * alertNotifications from the award workflow.
	 * 
	 * Depending on the taskstatus (with or without financials) different joins
	 * alert and notifications will be set First we query the database to find
	 * the provider ids where for a evaluation is selected Next we build up a
	 * list that will contain the provider ids for the different
	 * alertnotifications. Finally we will create a call to the callNotification
	 * method for with or without financials. AND we do a callNotification for
	 * the providers which are not selected.
	 * 
	 * After all this we change the modified_flag in the evaluation_results
	 * table for the used evaluation results.
	 * 
	 * @param asTaskStatus
	 * @param asFirstRound
	 * @param asProcurementId
	 * @param asProcurementTitle
	 * @param aoNegotiationRequired
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'updateModifiedFlag'
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void handleAlertNotificationsForAward(String asTaskStatus, String asProcurementId,
			String asProcurementTitle, String[] aoSelectedProviders, String[] aoNotSelectedProviders,
			Boolean aoNegotiationRequired) throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.handleAlertNotificationsForAward.asTaskStatus:" + asTaskStatus);
		LOG_OBJECT.Debug("HHSComponentOperations.handleAlertNotificationsForAward.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.handleAlertNotificationsForAward.asProcurementTitle:"
				+ asProcurementTitle);

		Channel loChannel = new Channel();
		HashMap loHmWFProperties = new HashMap();
		try
		{

			loHmWFProperties.put(HHSP8Constants.PROCUREMENT_ID, asProcurementId);

			loChannel.setData(HHSP8Constants.LOHMAP, loHmWFProperties);

			// fix done as a part of release 3.4.0 defect 6453 - fetching
			// procurement status - start
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_PROCUREMENT_STATUS);
			Integer liProcurementStatus = (Integer) loChannel.getData(HHSP8Constants.PROCUREMENT_STATUS);
			// fix done as a part of release 3.4.0 defect 6453 - end
			// Based on the taskstatus we will call the call notification for
			// the valid providers
			if (liProcurementStatus != (Integer.parseInt(PropertyLoader.getProperty(
					HHSConstants.PROPERTIES_STATUS_CONSTANT, HHSConstants.STATUS_PROCUREMENT_CLOSED))))
			{
				if (aoSelectedProviders.length > 0)
				{
					if (HHSP8Constants.APPROVED.equals(asTaskStatus) && !aoNegotiationRequired)
					{
						String lsNotificationName = HHSP8Constants.NT203;
						String lsAlertName = HHSP8Constants.AL203;
						String[] loProperties =
						{ HHSP8Constants.PROCUREMENT_TITLE_WF + asProcurementTitle,
								HHSP8Constants.PROCUREMENT_ID_WF + asProcurementId };
						String lsAudienceType = HHSP8Constants.PROVIDER;
						callNotificationService(lsNotificationName, lsAlertName, loProperties, lsAudienceType,
								aoSelectedProviders);

					}
					else if (HHSP8Constants.OVERRIDE.equals(asTaskStatus) && !aoNegotiationRequired)
					{
						String lsNotificationName = HHSP8Constants.NT222;
						String lsAlertName = HHSP8Constants.AL221;
						String[] loProperties =
						{ HHSP8Constants.PROCUREMENT_TITLE_WF + asProcurementTitle,
								HHSP8Constants.PROCUREMENT_ID_WF + asProcurementId };
						String lsAudienceType = HHSP8Constants.PROVIDER;
						callNotificationService(lsNotificationName, lsAlertName, loProperties, lsAudienceType,
								aoSelectedProviders);
					}
					// R5 changes starts
					else if (HHSP8Constants.OVERRIDE.equals(asTaskStatus) && aoNegotiationRequired)
					{
						String lsNotificationName = HHSP8Constants.NT231;
						String lsAlertName = HHSP8Constants.AL230;
						String[] loProperties =
						{ HHSP8Constants.PROCUREMENT_TITLE_WF + asProcurementTitle,
								HHSP8Constants.PROCUREMENT_ID_WF + asProcurementId };
						String lsAudienceType = HHSP8Constants.PROVIDER;
						callNotificationService(lsNotificationName, lsAlertName, loProperties, lsAudienceType,
								aoSelectedProviders);
					}
					else if (HHSP8Constants.APPROVED.equals(asTaskStatus) && aoNegotiationRequired)
					{
						String lsNotificationName = HHSP8Constants.NT231;
						String lsAlertName = HHSP8Constants.AL230;
						String[] loProperties =
						{ HHSP8Constants.PROCUREMENT_TITLE_WF + asProcurementTitle,
								HHSP8Constants.PROCUREMENT_ID_WF + asProcurementId };
						String lsAudienceType = HHSP8Constants.PROVIDER;
						callNotificationService(lsNotificationName, lsAlertName, loProperties, lsAudienceType,
								aoSelectedProviders);
					}
					// R5 changes ends
				}
				// In all cases the providers who are not selected will retrieve
				// an
				// alertnotification
				// QC 9653 R 9.3.0 - add null validation
				if (aoNotSelectedProviders!= null && aoNotSelectedProviders.length > 0)
				{
					String lsNotificationName = HHSP8Constants.NT223;
					String lsAlertName = HHSP8Constants.AL222;
					String[] loProperties =
					{ HHSP8Constants.PROCUREMENT_TITLE_WF + asProcurementTitle,
							HHSP8Constants.PROCUREMENT_ID_WF + asProcurementId };
					String lsAudienceType = HHSP8Constants.PROVIDER;

					callNotificationService(lsNotificationName, lsAlertName, loProperties, lsAudienceType,
							aoNotSelectedProviders);
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception Occured while handleAlertNotificationsForAward:", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception Occured while handleAlertNotificationsForAward:", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the handleAlertNotificationsForAward method in HHSComponentOperations",
					aoAppEx);
		}
	}

	/**
	 * This method inserts the values into the Payment Table.
	 * 
	 * @param asContractNumber
	 * @param asInvoiceId
	 * @param asContractId
	 * @param asBudgetId
	 * @param asAgencyId
	 * @param asOrganizationId
	 * @param asUserId
	 * @param asPeriod
	 * @param asBudgetAdvanceId
	 * @return loPaymentIdArray Payment Ids
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public String[] insertIntoPayment(String asContractNumber, String asInvoiceId, String asContractId,
			String asBudgetId, String asAgencyId, String asOrganizationId, String asUserId, String asPeriod,
			String asBudgetAdvanceId) throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.insertIntoPayment.asContractNumber:" + asContractNumber);
		LOG_OBJECT.Debug("HHSComponentOperations.insertIntoPayment.asInvoiceId:" + asInvoiceId);
		LOG_OBJECT.Debug("HHSComponentOperations.insertIntoPayment.asContractId:" + asContractId);
		LOG_OBJECT.Debug("HHSComponentOperations.insertIntoPayment.asBudgetId:" + asBudgetId);
		LOG_OBJECT.Debug("HHSComponentOperations.insertIntoPayment.asAgencyId:" + asAgencyId);
		LOG_OBJECT.Debug("HHSComponentOperations.insertIntoPayment.asOrganizationId:" + asOrganizationId);
		LOG_OBJECT.Debug("HHSComponentOperations.insertIntoPayment.asUserId:" + asUserId);
		LOG_OBJECT.Debug("HHSComponentOperations.insertIntoPayment.asPeriod:" + asPeriod);
		LOG_OBJECT.Debug("HHSComponentOperations.insertIntoPayment.asBudgetAdvanceId:" + asBudgetAdvanceId);

		String lsWorkFlow = null;
		String lsPaymentVoucherNumber = null;
		String lsInvoiceNumber = null;
		String lsBudgetAdvanceNumber = null;
		List<String> loPaymentIdList = new ArrayList<String>();
		new ArrayList<AssignementDetailsBean>();
		String[] loPaymentIdArray = null;
		DatabaseOperations loDatabaseOperations = new DatabaseOperations();
		// Hashmap to be used for the parameters for the db transaction
		HashMap loHmWFProperties = new HashMap();

		try
		{
			if (null == asPeriod || asPeriod.isEmpty())
			{
				asPeriod = loDatabaseOperations.retrievePeriod(asBudgetId);
			}
			// Load the workflow properties into the hashmap
			LOG_OBJECT.Debug(" loHmWFProperties value before insert" + loHmWFProperties);
			loHmWFProperties.put(HHSP8Constants.CT_NUMBER, asContractNumber);
			loHmWFProperties.put(HHSP8Constants.INVOICE_ID, asInvoiceId);
			loHmWFProperties.put(HHSP8Constants.CONTRACT_ID, asContractId);
			loHmWFProperties.put(HHSP8Constants.BUDGET_ID, asBudgetId);
			loHmWFProperties.put(HHSP8Constants.AGENCY_ID, asAgencyId);
			loHmWFProperties.put(HHSP8Constants.ORGANIZATION_ID, asOrganizationId);
			loHmWFProperties.put(HHSP8Constants.CREATED_BY_USERID, asUserId);
			loHmWFProperties.put(HHSP8Constants.MODIFIED_BY_USERID, asUserId);
			loHmWFProperties.put(HHSP8Constants.PERIOD, asPeriod);
			loHmWFProperties.put(HHSP8Constants.BUDGET_ADVANCE_ID, asBudgetAdvanceId);
			lsInvoiceNumber = fetchInvoiceNumber(asInvoiceId);
			lsBudgetAdvanceNumber = fetchAdvanceNumber(asBudgetAdvanceId);

			if (null == asInvoiceId || asInvoiceId.isEmpty())
			{
				lsWorkFlow = HHSP8Constants.WF307;
				lsPaymentVoucherNumber = lsBudgetAdvanceNumber
						.concat(HHSP8Constants.ADVANCE_PAYMENT_VOUCHER_IDENTIFIER).concat(HHSP8Constants.ONE);

			}
			else if (null == asBudgetAdvanceId || asBudgetAdvanceId.isEmpty())
			{
				lsWorkFlow = HHSP8Constants.WF305;
				lsPaymentVoucherNumber = lsInvoiceNumber.concat(HHSP8Constants.INVOICE_PAYMENT_VOUCHER_IDENTIFIER)
						.concat(HHSP8Constants.ONE);
			}

			getPaymentVoucherNumber(asInvoiceId, asBudgetId, asBudgetAdvanceId, lsWorkFlow, lsPaymentVoucherNumber,
					lsInvoiceNumber, lsBudgetAdvanceNumber, loPaymentIdList, loDatabaseOperations, loHmWFProperties);

		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception Occured while insertIntoPayment:", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the insertIntoPayment method in HHSComponentOperations", aoAppEx);
		}

		loPaymentIdArray = new String[loPaymentIdList.size()];
		loPaymentIdArray = loPaymentIdList.toArray(loPaymentIdArray);

		return loPaymentIdArray;

	}

	/**
	 * This method is used to fetch the unique payment No.
	 * @param asInvoiceId
	 * @param asBudgetId
	 * @param asBudgetAdvanceId
	 * @param lsWorkFlow
	 * @param lsPaymentVoucherNumber
	 * @param lsInvoiceNumber
	 * @param lsBudgetAdvanceNumber
	 * @param loPaymentIdList
	 * @param loDatabaseOperations
	 * @param loHmWFProperties
	 * @throws ApplicationException
	 */
	private void getPaymentVoucherNumber(String asInvoiceId, String asBudgetId, String asBudgetAdvanceId,
			String asWorkFlow, String asPaymentVoucherNumber, String asInvoiceNumber, String asBudgetAdvanceNumber,
			List<String> aoPaymentIdList, DatabaseOperations aoDatabaseOperations, HashMap aoHmWFProperties)
			throws ApplicationException
	{
		String lsPaymentId;
		Double ldAmount;
		List<AssignementDetailsBean> loAssignmentList;
		String lsStatusId = "63"; // Status 63 = ?Pending Approval?
		aoHmWFProperties.put(HHSP8Constants.STATUS_ID, lsStatusId);

		String lsProcessFlag = "0";
		aoHmWFProperties.put(HHSP8Constants.PROCESS_FLAG, lsProcessFlag);

		String lsDeleteFlag = "0";
		aoHmWFProperties.put(HHSP8Constants.DELETE_FLAG, lsDeleteFlag);

		ldAmount = aoDatabaseOperations.fetchAmount(asWorkFlow, asInvoiceId, asBudgetAdvanceId);

		aoHmWFProperties.put(HHSP8Constants.VENDOR_ID, HHSP8Constants.EMPTY_STRING);
		aoHmWFProperties.put(HHSP8Constants.AMOUNT, ldAmount);
		aoHmWFProperties.put(HHSP8Constants.PAYMENT_VOUCHER_NUMBER, asPaymentVoucherNumber);
		lsPaymentId = aoDatabaseOperations.insertIntoPayment(aoHmWFProperties);
		aoPaymentIdList.add(lsPaymentId);

		loAssignmentList = aoDatabaseOperations.fetchAssignments(asInvoiceId, asBudgetId, asBudgetAdvanceId);

		for (int liCount = 0; liCount < loAssignmentList.size(); liCount++)
		{
			// The database query will ensure that there is a unique Payment
			// Voucher Number (Payment_Voucher_Num)
			if (asWorkFlow.equalsIgnoreCase(HHSP8Constants.WF305))
			{
				asPaymentVoucherNumber = asInvoiceNumber + (HHSP8Constants.INVOICE_PAYMENT_VOUCHER_IDENTIFIER)
						+ (liCount + 2);
			}
			else if (asWorkFlow.equalsIgnoreCase(HHSP8Constants.WF307))
			{
				asPaymentVoucherNumber = asBudgetAdvanceNumber + (HHSP8Constants.ADVANCE_PAYMENT_VOUCHER_IDENTIFIER)
						+ (liCount + 2);
			}
			aoHmWFProperties.put(HHSP8Constants.VENDOR_ID, loAssignmentList.get(liCount).getMsVendorId());
			aoHmWFProperties.put(HHSP8Constants.AMOUNT, loAssignmentList.get(liCount).getMdAmount());
			aoHmWFProperties.put(HHSP8Constants.PAYMENT_VOUCHER_NUMBER, asPaymentVoucherNumber);
			lsPaymentId = aoDatabaseOperations.insertIntoPayment(aoHmWFProperties);
			aoPaymentIdList.add(lsPaymentId);
		}
	}

	/**
	 * This method inserts the values into the PaymentAllocation Table calls the
	 * transaction 'insertIntoPaymentAllocation'
	 * @param asUserId
	 * @param aoPaymentIds
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	public void insertIntoPaymentAllocation(String asUserId, String[] aoPaymentIds) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.insertIntoPaymentAllocation.asUserId:" + asUserId);
		LOG_OBJECT.Debug("HHSComponentOperations.insertIntoPaymentAllocation.aoPaymentIds:" + aoPaymentIds);
		// The insert statement will need to be executed once for each payment
		// voucher. (1:M relationship between Payment and Payment Allocation).
		Channel loChannel = new Channel();
		HashMap loHmWFProperties = new HashMap();

		try
		{
			loHmWFProperties.put(HHSP8Constants.PAYMENT_ID_ARRAY, aoPaymentIds);
			loHmWFProperties.put(HHSP8Constants.CREATED_BY_USERID, asUserId);
			loChannel.setData(HHSP8Constants.LOHMAP, loHmWFProperties);

			TransactionManager.executeTransaction(loChannel, HHSP8Constants.INSERT_INTO_PAYMENT_ALLOCATION);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception Occured while insertIntoPaymentAllocation:", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception Occured while insertIntoPaymentAllocation:", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the insertIntoPaymentAllocation method in HHSComponentOperations",
					aoAppEx);
		}
	}

	/**
	 * If start date of Contract Budget <= Today?s Date; Set the Process Flag to
	 * "Y" for all payment (i.e. PAYMENT.PROCESS_FLAG=1) calls the transaction
	 * 'updateProcessFlagOnPayment'
	 * @param asInvoiceId
	 * @throws ApplicationException
	 */
	public void setProcessFlagOnPayment(String asInvoiceId) throws ApplicationException
	{
	}

	/**
	 * This method is responsible for starting the evaluation workflows. For
	 * starting the workflows, it will use the startEvaluationWorkflows method
	 * in the loReassignEvaluationOperation class
	 * 
	 * @param asProcurementId Procurement Id
	 * @param asIntExtBothFlag Internal/External Flag
	 * @return String Array of Wob Numbers
	 * @throws ApplicationException
	 */
	public String[] startEvaluationWorkflows(String asProcurementId, String asIntExtBothFlag, String asEvalPoolMappingId)
			throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.startEvaluationWorkflows.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.startEvaluationWorkflows.asIntExtBothFlag:" + asIntExtBothFlag);
		LOG_OBJECT.Debug("HHSComponentOperations.startEvaluationWorkflows.asEvalPoolMappingId:" + asEvalPoolMappingId);
		ReassignEvaluationsOperation loReassignEvaluationOperation = new ReassignEvaluationsOperation(LOG_OBJECT);
		try
		{
			return loReassignEvaluationOperation.startEvaluationWorkflows(moPeSession, asProcurementId,
					asIntExtBothFlag, asEvalPoolMappingId);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during startEvaluationWorkflows", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during startEvaluationWorkflows", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the startEvaluationWorkflows method in HHSComponentOperations",
					aoAppEx);
		}
	}

	/**
	 * This method will fetch the Evaluator Email Ids from the DB based on the
	 * Procurement Id
	 * 
	 * @param asProcurementId
	 * @return loEvalIds
	 * @throws ApplicationException
	 * 
	 * 
	 *             calls the transaction 'fetchEvaluatorIdsEvaluation'
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public String[] fetchEvaluatorIdsEvaluationWorkFlow(String asProcurementId, String asEvalPoolMappingId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.fetchEvaluatorIdsEvaluationWorkFlow.asProcurementId:"
				+ asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.fetchEvaluatorIdsEvaluationWorkFlow.asEvalPoolMappingId:"
				+ asEvalPoolMappingId);
		Channel loChannelObj = new Channel();
		List<String> loEvalIdList = null;
		String[] loEvalIdArray = null;
		HashMap<String, String> loHMap = new HashMap<String, String>();
		try
		{
			loHMap.put(HHSP8Constants.PROCUREMENT_ID, asProcurementId);
			loHMap.put(HHSP8Constants.EVAL_POOL_MAPPING_ID, asEvalPoolMappingId);
			loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);
			LOG_OBJECT.Debug("Executing transaction fetchEvaluatorIds");
			// execute fetch transaction
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.FETCH_EVALUATOR_ID_EVALUATION);
			loEvalIdList = (List<String>) loChannelObj.getData(HHSP8Constants.EVALUATOR_IDS);
			LOG_OBJECT.Debug("Finished transaction fetchEvaluatorIds");
			loEvalIdArray = new String[loEvalIdList.size()];
			loEvalIdArray = loEvalIdList.toArray(loEvalIdArray);
			return loEvalIdArray;
		}
		// handling Application Exception thrown by any action/resource
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.fetchEvaluatorIds()", aoAppEx);
			throw aoAppEx;
		}
		// handling Application Exception thrown by any action/resource
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.fetchEvaluatorIds()", aoAppEx);
			throw new ApplicationException("Exception in HHSComponentOperations.fetchEvaluatorIds()", aoAppEx);
		}
	}

	/**
	 * This method deletes the Evaluation data for a given Procurement ID
	 * 
	 * Depending on the asIntExtBothFlag the method will call the
	 * DELETE_EVALUATOR_INTERNAL or/and DELETE_EVALUATOR_EXTERNAL transaction
	 * 
	 * @param asIntExtBothFlag
	 * @param asProcurementId
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'deleteEvaluatorData'
	 */
	public void deleteEvaluationData(String asIntExtBothFlag, String asProcurementId, String asEvalPoolMappingId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.deleteEvaluationData.asIntExtBothFlag:" + asIntExtBothFlag);
		LOG_OBJECT.Debug("HHSComponentOperations.deleteEvaluationData.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.deleteEvaluationData.asEvalPoolMappingId:" + asEvalPoolMappingId);
		Channel loChannelObj = new Channel();
		HashMap<String, String> loHMap = new HashMap<String, String>();
		try
		{
			loHMap.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcurementId);
			loHMap.put(HHSP8Constants.EVAL_POOL_MAPPING_ID, asEvalPoolMappingId);
			loHMap.put(HHSP8Constants.EVALUATOR_FLAG, asIntExtBothFlag);
			loChannelObj.setData(HHSP8Constants.LOHMAP, loHMap);
			// Start || Changes as a part of release 3.6.0 for enhancement
			// request 5905
			loChannelObj.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
			// End || Changes as a part of release 3.6.0 for enhancement request
			// 5905
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.DELETE_EVALUATOR_DATA);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.deleteEvaluationData()", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.deleteEvaluationData()", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the deleteEvaluationData method in HHSComponentOperations", aoAppEx);
		}
	}

	/**
	 * This method will start the review score workflows for the given proposal
	 * ids.
	 * 
	 * First this method will retrieve all the proposal information that is
	 * linked to this procurement. Next it will call the
	 * startProposalRelatedWorkflows method from the WorkflowOperations class to
	 * start the review score workflows for this procurement and proposals.
	 * execute transaction fetchAcceptedProposalID.
	 * <ul>
	 * <li>Method Updated in R4</li>
	 * </ul>
	 * @param asProcurementId string containing procurement Id
	 * @param asEvalPoolMappingId string containing evaluation pool mapping Id
	 * @return String[] started workflow ids
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked" })
	public String[] startReviewScoreWorkflows(String asProcurementId, String asEvalPoolMappingId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.startReviewScoreWorkflows.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.startReviewScoreWorkflows.asEvalPoolMappingId:" + asEvalPoolMappingId);
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		String[] loAPwobs = null;
		Channel loChannel = new Channel();
		String[] loProposalIdArray = null;
		HashMap<String, String> loInputParam = new HashMap<String, String>();
		loInputParam.put(HHSP8Constants.PROCUREMENT_ID, asProcurementId);
		loInputParam.put(HHSP8Constants.EVAL_POOL_MAPPING_ID, asEvalPoolMappingId);
		loChannel.setData(HHSP8Constants.LOHMAP, loInputParam);

		try
		{
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_ACCEPTED_PROPOSAL_ID);
			List<String> loProposalIdList = (List<String>) loChannel.getData(HHSP8Constants.PROPOSAL_ID_KEY);
			loProposalIdArray = new String[loProposalIdList.size()];
			loProposalIdArray = loProposalIdList.toArray(loProposalIdArray);

			// Retrieve the propopsal information for the given proposal ids and
			// start the workflow with this data
			List<ProposalDetailsBean> loProposalDetailBeanList = loWorkflowOperations.retrieveProposalsInformation(
					loProposalIdArray, asEvalPoolMappingId);
			loAPwobs = loWorkflowOperations
					.startProposalRelatedWorkflows(moPeSession, asProcurementId,
							HHSP8Constants.WORKFLOW_NAME_202_REVIEW_SCORES, loProposalDetailBeanList, null,
							asEvalPoolMappingId);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during startReviewScoreWorkflows", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during startReviewScoreWorkflows", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the startReviewScoreWorkflows method in HHSComponentOperations",
					aoAppEx);
		}
		return loAPwobs;
	}

	/**
	 * This method cancels the Evaluation Tasks for the given procurement ID
	 * 
	 * First the evaluations data in the db will be removed for the given
	 * procurement Second the workflows will be removed for the given
	 * procurement id. The workflows to be removed are:
	 * WORKFLOW_NAME_202_EVALUATION, WORKFLOW_NAME_202_EVALUATE_PROPOSAL_MAIN,
	 * WORKFLOW_NAME_202_REVIEW_SCORES
	 * 
	 * @param asProcurementId
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'cancelEvaluationTask'
	 */
	public void cancelEvaluationTasks(String asProcurementId, String asEvalPoolMappingId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.cancelEvaluationTasks.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.cancelEvaluationTasks.asEvalPoolMappingId:" + asEvalPoolMappingId);
		HashMap<String, String> loHmReqExceProp = new HashMap<String, String>();
		HashMap<String, Object> loCancelEvaluationTaskMap = new HashMap<String, Object>();
		try
		{
			LOG_OBJECT.Debug("Entered into cancelEvaluationTasks::" + loHmReqExceProp.toString());
			Channel loChannel = new Channel();
			loCancelEvaluationTaskMap.put(HHSP8Constants.PE_WORKFLOW_PROCUREMENT_ID, asProcurementId);
			loChannel.setData(HHSP8Constants.PROCUREMENT_ID, asProcurementId);
			loChannel.setData(HHSP8Constants.EVAL_POOL_MAPPING_ID, asEvalPoolMappingId);

			Map<String, Object> loInputParam = new HashMap<String, Object>();
			loInputParam.put(HHSP8Constants.EVAL_POOL_MAPPING_ID, asEvalPoolMappingId);
			loInputParam.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcurementId);
			loInputParam.put(HHSP8Constants.COMPETITION_POOL_STATUS, PropertyLoader.getProperty(
					HHSP8Constants.PROPERTIES_STATUS_CONSTANT,
					HHSP8Constants.STATUS_COMPETITION_POOL_PROPOSALS_RECEIVED));
			loInputParam.put(HHSConstants.EVENT_NAME, HHSP8Constants.CANCEL_EVALUATION_TASK_EVENT);
			loInputParam.put(HHSConstants.EVENT_TYPE, HHSP8Constants.CANCEL_EVALUATION_TASK_EVENT);
			loInputParam.put(HHSConstants.USER_ID, HHSConstants.SYSTEM_USER);
			loChannel.setData(HHSP8Constants.INPUT_PARAM_MAP, loInputParam);
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.CANCEL_EVALUATION_TASKS);
			String lsRfpReleaseBeforeR4Flag = (String) loChannel.getData(HHSP8Constants.RFP_RELEASE_BEFORE_R4_FLAG);

			// Remove all the evaluation workflows and evaluation main
			// workflows
			WorkflowOperations loWFO = new WorkflowOperations(LOG_OBJECT);

			StringBuffer lsFilterBuffer = new StringBuffer(HHSP8Constants.PE_WORKFLOW_PROCUREMENT_ID).append("='")
					.append(asProcurementId).append("'");
			if (null == lsRfpReleaseBeforeR4Flag
					|| lsRfpReleaseBeforeR4Flag.equalsIgnoreCase(HHSP8Constants.EMPTY_STRING))
			{
				lsFilterBuffer.append(" and ");
				lsFilterBuffer.append(HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID);
				lsFilterBuffer.append("='");
				lsFilterBuffer.append(asEvalPoolMappingId);
				lsFilterBuffer.append("'");
			}
			String[] loWorkflowNames =
			{ HHSP8Constants.WORKFLOW_NAME_202_EVALUATION, HHSP8Constants.WORKFLOW_NAME_202_EVALUATE_PROPOSAL_MAIN,
					HHSP8Constants.WORKFLOW_NAME_202_REVIEW_SCORES, HHSP8Constants.WORKFLOW_NAME_203_AWARD };
			loWFO.cancelWorkflows(lsFilterBuffer.toString(), loWorkflowNames, moPeSession);
		}
		// Handling Application Exception
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("ApplicationException Occured while Cancel Evaluation Tasks:", aoAppEx);
			throw aoAppEx;
		}
		// Handling Exceptions other than Application Exception
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Exception Occured while Cancel Evaluation Tasks:", aoEx);
			throw new ApplicationException(
					"Exception occured during executing the cancelEvaluationTasks method in HHSComponentOperations",
					aoEx);
		}
	}

	/**
	 * This method implements the reassignment functionality for the evaluation
	 * tasks.
	 * 
	 * First the evaluation reassignment is handled for the internal evaluators.
	 * Secondly the evaluation reassignment is handled for the external
	 * evaluators.
	 * 
	 * Both steps are first retrieving the evaluation information from the
	 * retrieveEvaluationReassignmentInformation method in the
	 * ReassignEvaluationsOperations class. After that the
	 * handleEvaluationInternalReassignment/
	 * handleEvaluationExternalReassignment is called in the
	 * ReassignEvaluationsOperations class to handle reassignment.
	 * @param asProcurementId
	 * @throws ApplicationException
	 */
	public void reassignEvaluationTasks(String asProcurementId, String asEvalPoolMappingId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.reassignEvaluationTasks.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.reassignEvaluationTasks.asEvalPoolMappingId:" + asEvalPoolMappingId);
		ReassignEvaluationsOperation loOperation = new ReassignEvaluationsOperation(LOG_OBJECT);
		Channel loChannel = new Channel();
		try
		{
			// Handle evaluation tasks for internal evaluators
			List<EvaluationReassignDetailsBean> loEvaluationReassignDetailsInteralBeans = loOperation
					.retrieveEvaluationReassignmentInformation(asProcurementId, HHSP8Constants.INTERNAL_FLAG,
							asEvalPoolMappingId);
			loOperation.handleEvaluationInternalReassignment(moPeSession, asProcurementId,
					loEvaluationReassignDetailsInteralBeans, asEvalPoolMappingId);

			// Handle evaluation tasks for external evaluators
			List<EvaluationReassignDetailsBean> loEvaluationReassignDetailsExternalBeans = loOperation
					.retrieveEvaluationReassignmentInformation(asProcurementId, HHSP8Constants.EXTERNAL_FLAG,
							asEvalPoolMappingId);
			loOperation.handleEvaluationExternalReassignment(moPeSession, asProcurementId,
					loEvaluationReassignDetailsExternalBeans, asEvalPoolMappingId);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the reassignEvaluationTasks with Procurement ID:"
					+ asProcurementId, aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the reassignEvaluationTasks with Procurement ID:"
					+ asProcurementId, aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the reassignEvaluationTasks method in HHSComponentOperations",
					aoAppEx);
		}
	}

	/**
	 * This method will start the accept proposal workflows for the given
	 * procurement id.
	 * 
	 * First this method will retrieve all the related proposal information with
	 * use of the retrieveProposalInformation method from the WorkflowOperations
	 * class. After this it will start the accept proposal workflows with the
	 * startProposalRelatedWorkflows method in the WorkflowOperations class.
	 * 
	 * @param asProcId
	 * @return String[] started workflow ids
	 * @throws ApplicationException
	 */
	public String[] startAcceptProposalWorkflows(String asProcId, String asLaunchBy, String asEvaluationGroupId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.startAcceptProposalWorkflows.asProcurementId:" + asProcId);
		LOG_OBJECT.Debug("HHSComponentOperations.startAcceptProposalWorkflows.asEvaluationGroupId:"
				+ asEvaluationGroupId);
		String[] loStartedWobs = null;
		WorkflowOperations loWorkflowOps = new WorkflowOperations(LOG_OBJECT);
		try
		{
			// Retrieve all proposal information linked to the procurementId and
			// start the accept proposal workflow
			List<ProposalDetailsBean> loProposalDetailBeanList = loWorkflowOps.retrieveProposalsInformation(asProcId,
					asEvaluationGroupId);
			LOG_OBJECT.Debug("HHSComponentOperations.startAcceptProposalWorkflows.ProposalDetailList----------------:"
					+ loProposalDetailBeanList.toString());
			loStartedWobs = loWorkflowOps.startProposalRelatedWorkflows(moPeSession, asProcId,
					HHSP8Constants.WORKFLOW_NAME_201_ACCEPT_PROPOSAL, loProposalDetailBeanList, asLaunchBy, null);
			// handling Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during start Accept Proposal Workflows", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during start Accept Proposal Workflows", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the startAcceptProposalWorkflows method in HHSComponentOperations",
					aoAppEx);
		}
		return loStartedWobs;
	}

	/**
	 * This method gives the number of selected proposals for a given
	 * procurement ID.
	 * 
	 * @param asProcurementId
	 * @return liProposalCount Proposal Count
	 * @throws ApplicationException
	 * 
	 *             calls the transaction 'fetchCountofSelectedProposals'
	 */
	public Integer findNumberOfSelectedProposals(String asProcurementId, String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.findNumberOfSelectedProposals.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.findNumberOfSelectedProposals.asEvaluationPoolMappingId:"
				+ asEvaluationPoolMappingId);
		Integer loProposalCount = null;
		Channel loChannel = new Channel();
		HashMap<String, String> loInputParam = new HashMap<String, String>();
		loInputParam.put(HHSP8Constants.PROCUREMENT_ID_KEY, asProcurementId);
		loInputParam.put(HHSP8Constants.EVAL_POOL_MAPPING_ID, asEvaluationPoolMappingId);
		loInputParam.put(HHSP8Constants.STATUS_PROPOSAL_SELECTED, PropertyLoader.getProperty(
				HHSP8Constants.PROPERTIES_STATUS_CONSTANT, HHSP8Constants.STATUS_PROPOSAL_SELECTED));
		loChannel.setData(HHSP8Constants.LOHMAP, loInputParam);
		try
		{
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_SELECTED_PROPOSALS_COUNT);
			loProposalCount = (Integer) loChannel.getData(HHSP8Constants.PROPOSAL_COUNT);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(aoAppEx.getMessage(), aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error(aoAppEx.getMessage(), aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the findNumberOfSelectedProposals method in WorkflowOperations",
					aoAppEx);
		}
		return loProposalCount;
	}

	/**
	 * 
	 * This method start the workflow for a contract.
	 * 
	 * @param asProcurementId
	 * @param abFirstRound
	 * @param asTaskOwner
	 * @param asEvaluationPoolMappingId
	 * @return
	 * @throws ApplicationException
	 */
	public String[] startContractWorkflows(String asProcurementId, boolean abFirstRound, String asTaskOwner,
			String asEvaluationPoolMappingId, String asCompetitionPoolTitle) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.startContractWorkflows.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.startContractWorkflows.abFirstRound:" + abFirstRound);
		LOG_OBJECT.Debug("HHSComponentOperations.startContractWorkflows.asTaskOwner:" + asTaskOwner);
		LOG_OBJECT.Debug("HHSComponentOperations.startContractWorkflows.asEvaluationPoolMappingId:"
				+ asEvaluationPoolMappingId);
		LOG_OBJECT.Debug("HHSComponentOperations.startContractWorkflows.asCompetitionPoolTitle:"
				+ asCompetitionPoolTitle);
		// Retrieve the contract detail beans and start the contract
		return launchContractNegotiationWorkflow(asProcurementId, abFirstRound, asTaskOwner, asEvaluationPoolMappingId,
				asCompetitionPoolTitle, false);
	}

	/**
	 * This method is used to launch Contract Negotiation Workflow.
	 * 
	 * @param asProcurementId String
	 * @param abFirstRound boolean
	 * @param asTaskOwner String
	 * @param asEvaluationPoolMappingId String
	 * @param asCompetitionPoolTitle String
	 * @param abIsNegotiation boolean
	 * @return loStartedWobs String[]
	 * @throws ApplicationException
	 */
	private String[] launchContractNegotiationWorkflow(String asProcurementId, boolean abFirstRound,
			String asTaskOwner, String asEvaluationPoolMappingId, String asCompetitionPoolTitle, boolean abIsNegotiation)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered HHSComponentOperations.launchContractNegotiationWorkflow");
		String lsWorkflowName = null;
		List<ContractDetailsBean> loContractDetailsBeanList = null;
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		// added in R5
		if (abIsNegotiation)
		{
			lsWorkflowName = HHSP8Constants.WORKFLOW_NAME_204_NEGOTIATION_AWARD;
			loContractDetailsBeanList = loWorkflowOperations.retrieveContractsInformation(asProcurementId,
					asEvaluationPoolMappingId, false);

		}
		else
		{
			lsWorkflowName = HHSP8Constants.WORKFLOW_NAME_302_CONTRACT_CONFIGURATION;
			loContractDetailsBeanList = loWorkflowOperations.retrieveContractsInformation(asProcurementId,
					asEvaluationPoolMappingId, true);
		}
		// R5 changes ends
		String[] loStartedWobs = null;
		try
		{
			loStartedWobs = loWorkflowOperations.startContractRelatedWorkflows(moPeSession, asProcurementId,
					lsWorkflowName, loContractDetailsBeanList, abFirstRound, asTaskOwner, asEvaluationPoolMappingId,
					asCompetitionPoolTitle);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the launchContractNegotiationWorkflow:", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured on executing the launchContractNegotiationWorkflow:", aoEx);
			throw new ApplicationException(
					"Error occured during executing the launchContractNegotiationWorkflow method in HHSComponentOperations",
					aoEx);
		}
		LOG_OBJECT.Debug("Exited HHSComponentOperations.launchContractNegotiationWorkflow with Wobnumber(s)"
				+ loStartedWobs);
		return loStartedWobs;
	}

	/**
	 * This method validate if the workflow for a contract is finished. calls
	 * the transaction 'fetchRfpReleasedBeforeR4Flag'
	 * @param asProcurementId
	 * @param asEvaluationPoolMappingId
	 * @return
	 * @throws ApplicationException
	 * 
	 * 
	 */
	public boolean checkIfContractWorkflowsAreFinished(String asProcurementId, String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.checkIfContractWorkflowsAreFinished.asProcurementId:"
				+ asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.checkIfContractWorkflowsAreFinished.asEvaluationPoolMappingId:"
				+ asEvaluationPoolMappingId);
		// Find contract ids for this specific procurement
		ArrayList<String> loWobsList = new ArrayList<String>();
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		Set<String> loWfnames = new HashSet<String>();
		try
		{
			Channel loChannel = new Channel();
			loChannel.setData(HHSP8Constants.PROCUREMENT_ID, asProcurementId);
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_RFP_BEFORE_R4_FLAG);
			String lsRfpReleaseBeforeR4Flag = (String) loChannel.getData(HHSP8Constants.RFP_RELEASE_BEFORE_R4_FLAG);
			loWfnames.add(HHSP8Constants.WORKFLOW_NAME_302_CONTRACT_CONFIGURATION);
			loWfnames.add(HHSP8Constants.WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS);
			StringBuffer lsFilter = new StringBuffer(HHSP8Constants.PROPERTY_PE_PROCURMENT_ID).append("='")
					.append(asProcurementId).append("'");
			if (null == lsRfpReleaseBeforeR4Flag
					|| lsRfpReleaseBeforeR4Flag.equalsIgnoreCase(HHSP8Constants.EMPTY_STRING))
			{
				lsFilter.append(" and ");
				lsFilter.append(HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID);
				lsFilter.append("='");
				lsFilter.append(asEvaluationPoolMappingId);
				lsFilter.append("'");
			}
			LOG_OBJECT.Debug("Filter ::" + lsFilter.toString());
			loWobsList.addAll(loWorkflowOperations.findWorkflowIds(moPeSession, lsFilter.toString(), loWfnames));
			LOG_OBJECT.Debug("List of wob numbers:" + loWobsList.toString());
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while executing the checkIfContractWorkflowsAreFinished method in HHSComponentOperations ",
							aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT
					.Error("Exception occured while executing the checkIfContractWorkflowsAreFinished method in HHSComponentOperations ",
							aoAppEx);
			throw new ApplicationException(aoAppEx.getMessage(), aoAppEx);
		}
		return (loWobsList.isEmpty());
	}

	/**
	 * The method will find the running workflow for this procurement for the
	 * specific provider. The following workflows will be
	 * terminated:WORKFLOW_NAME_302_CONTRACT_CONFIGURATION,
	 * WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS and
	 * WORKFLOW_NAME_304_CONTRACT_BUDGET.
	 * 
	 * First this method will use the cancelWorkflows functionality from the
	 * WorkflowOperations class to cancel the workflows. Next it will use the
	 * setCancelParameterOnAwardWorkflow method to set the cancelflag on the
	 * award workflow. This will make sure that the award workflow is aware that
	 * the related workflows have been cancelled. calls the transaction
	 * 'fetchRfpReleasedBeforeR4Flag'
	 * <ul>
	 * <li>Method Added in R4</li>
	 * </ul>
	 * @param asContractId String object
	 * @param asOrgId String object
	 * @param asEvaluationPoolMappingId String object
	 * @throws ApplicationException
	 */
	public void cancelAwardProcess(String asContractId, String asProcurementId, String asEvaluationPoolMappingId)
			throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.cancelAwardProcess.asContractId:" + asContractId);
		LOG_OBJECT.Debug("HHSComponentOperations.cancelAwardProcess.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.cancelAwardProcess.asEvaluationPoolMappingId:"
				+ asEvaluationPoolMappingId);
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		// Prepare the filter to get the
		String lsFilter = HHSP8Constants.CONTRACT_ID + "='" + asContractId + "'";
		String[] loWorkflowNames =
		{ HHSP8Constants.WORKFLOW_NAME_302_CONTRACT_CONFIGURATION,
				HHSP8Constants.WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS,
				HHSP8Constants.WORKFLOW_NAME_304_CONTRACT_BUDGET, HHSP8Constants.WORKFLOW_NAME_204_NEGOTIATION_AWARD };

		// Whenever performance issues arise: Create and index on contractId and
		// change the query implementation to make use of this index

		try
		{
			// Call the method that will cancel the award workflows.
			loWorkflowOperations.cancelWorkflows(lsFilter, loWorkflowNames, moPeSession);
			Channel loChannel = new Channel();
			loChannel.setData(HHSP8Constants.PROCUREMENT_ID, asProcurementId);
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_RFP_BEFORE_R4_FLAG);
			String lsRfpReleaseBeforeR4Flag = (String) loChannel.getData(HHSP8Constants.RFP_RELEASE_BEFORE_R4_FLAG);
			// Set the cancelflag in the award workflow to true, to make sure it
			// will not go to the award doc step.
			Boolean loCancelFlag = true;
			StringBuffer lsSetCancelAwardFilter = new StringBuffer(HHSP8Constants.PE_WORKFLOW_PROCUREMENT_ID)
					.append("='").append(asProcurementId).append("'");
			if (null == lsRfpReleaseBeforeR4Flag
					|| lsRfpReleaseBeforeR4Flag.equalsIgnoreCase(HHSP8Constants.EMPTY_STRING))
			{
				lsSetCancelAwardFilter.append(" and ");
				lsSetCancelAwardFilter.append(HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID);
				lsSetCancelAwardFilter.append("='");
				lsSetCancelAwardFilter.append(asEvaluationPoolMappingId);
				lsSetCancelAwardFilter.append("'");
			}
			loWorkflowOperations.setCancelParameterOnAwardWorkflow(moPeSession, lsSetCancelAwardFilter.toString(),
					loCancelFlag);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancel Award   workflow with filter:" + lsFilter, aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancel Award   workflow with filter:" + lsFilter, aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the cancelAwardProcess method in HHSComponentOperations", aoAppEx);
		}
	}

	/**
	 * The method will find the running workflow for this procurement for the
	 * specific provider. The following workflows will be
	 * terminated:WORKFLOW_NAME_302_CONTRACT_CONFIGURATION,
	 * WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS.
	 * 
	 * First this method will use the cancelWorkflows functionality from the
	 * WorkflowOperations class to cancel the workflows. Next it will use the
	 * setCancelParameterOnAwardWorkflow method to set the cancelflag on the
	 * award workflow. This will make sure that the award workflow is aware that
	 * the related workflows have been cancelled. calls the transaction
	 * 'fetchRfpReleasedBeforeR4Flag'
	 * <ul>
	 * <li>Method Added for enhancement 6574 for Release 3.10.0</li>
	 * </ul>
	 * @param asContractId String object
	 * @param asOrgId String object
	 * @param asEvaluationPoolMappingId String object
	 * @throws ApplicationException
	 */
	public void cancelContractWorkflows(String[] ascontractIdArray, String asProcurementId,
			String asEvaluationPoolMappingId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.cancelContractWorkflows.ascontractIdArray:" + ascontractIdArray[0]);
		LOG_OBJECT.Debug("HHSComponentOperations.cancelContractWorkflows.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.cancelContractWorkflows.asEvaluationPoolMappingId:"
				+ asEvaluationPoolMappingId);
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		try
		{
			if (null != ascontractIdArray && null != ascontractIdArray[0])
			{
				for (String asContractId : ascontractIdArray)
				{
					LOG_OBJECT.Debug("In loop....asContractId:" + asContractId);
					String lsFilter = HHSP8Constants.CONTRACT_ID + "='" + asContractId + "'";
					String[] loWorkflowNames =
					{ HHSP8Constants.WORKFLOW_NAME_302_CONTRACT_CONFIGURATION,
							HHSP8Constants.WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS };
					loWorkflowOperations.cancelWorkflows(lsFilter, loWorkflowNames, moPeSession);
				}
			}
			else
			{
				LOG_OBJECT.Debug("No contract workflows found for deletion for evaluationpoolmappingid: "
						+ asEvaluationPoolMappingId);
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancelContractWorkflows with contractids: ", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancelContractWorkflows with contractids: ", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the cancelContractWorkflows method in HHSComponentOperations",
					aoAppEx);
		}
	}

	/**
	 * The method will cancel all workflows for Cancelled Competition Pool
	 * <ul>
	 * <li>Method Added for enhancement 6577 for Release 3.10.0</li>
	 * </ul>
	 * @param ascontractIdArray String object
	 * @param asProcurementId String object
	 * @param asEvaluationPoolMappingId String object
	 * @throws ApplicationException
	 */
	public void cancelWorkflowsForCancelCompetition(String[] ascontractIdArray, String asProcurementId,
			String asEvaluationPoolMappingId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.cancelContractWorkflows.ascontractIdArray:" + ascontractIdArray[0]);
		LOG_OBJECT.Debug("HHSComponentOperations.cancelContractWorkflows.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.cancelContractWorkflows.asEvaluationPoolMappingId:"
				+ asEvaluationPoolMappingId);
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		String[] loWorkflowNames =
		{ HHSP8Constants.WORKFLOW_NAME_201_ACCEPT_PROPOSAL, HHSP8Constants.WORKFLOW_NAME_201_ACCEPT_PROPOSAL_MAIN,
				HHSP8Constants.WORKFLOW_NAME_202_EVALUATE_PROPOSAL_MAIN, HHSP8Constants.WORKFLOW_NAME_202_EVALUATION,
				HHSP8Constants.WORKFLOW_NAME_202_REVIEW_SCORES, HHSP8Constants.WORKFLOW_NAME_203_AWARD,
				HHSP8Constants.WORKFLOW_NAME_302_CONTRACT_CONFIGURATION,
				HHSP8Constants.WORKFLOW_NAME_303_CONTRACT_CERTIFICATION_FUNDS,
				HHSP8Constants.WORKFLOW_NAME_304_CONTRACT_BUDGET, HHSP8Constants.WF_CONTRACT_CONFIGURATION_UPDATE };
		StringBuffer lsFilter = new StringBuffer();
		try
		{
			if (null != asEvaluationPoolMappingId
					&& !asEvaluationPoolMappingId.equalsIgnoreCase(ApplicationConstants.EMPTY_STRING))
			{
				lsFilter.append(HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID + "='" + asEvaluationPoolMappingId
						+ "'");
			}
			if (null != ascontractIdArray && null != ascontractIdArray[0])
			{
				lsFilter.append(" or ContractID in (");
				for (String asContractId : ascontractIdArray)
				{
					LOG_OBJECT.Debug("In loop....asContractId:" + asContractId);
					lsFilter.append("'" + asContractId + "'" + ",");
				}
				lsFilter.setCharAt(lsFilter.length() - 1, ')');
			}
			loWorkflowOperations.cancelWorkflows(lsFilter.toString(), loWorkflowNames, moPeSession);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancelContractWorkflows with contractids: ", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancelContractWorkflows with contractids: ", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the cancelContractWorkflows method in HHSComponentOperations",
					aoAppEx);
		}
	}

	/**
	 * This method will remove the unused workflow The following workflows will
	 * be removed: WORKFLOW_NAME_202_REVIEW_SCORES WORKFLOW_NAME_202_EVALUATION
	 * 
	 * For the removal, the method will search for all the workflows with the
	 * given procurementId. Next it will filter on
	 * WORKFLOW_NAME_202_REVIEW_SCORES and WORKFLOW_NAME_202_EVALUATION
	 * workflows and it will cancel those workflows. This functionality is used
	 * in the WorkflowOperations.cancelWorkflows method called from this method.
	 * <ul>
	 * <li>Method Added in R4</li>
	 * </ul>
	 * @param asProcurementId String Object
	 * @param asEvaluationPoolMappingId String Object
	 * @throws ApplicationException
	 */
	public void cancelUnusedWorkflowsFromAward(String asProcurementId, String asEvaluationPoolMappingId)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.cancelUnusedWorkflowsFromAward.asProcurementId:" + asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.cancelUnusedWorkflowsFromAward.asEvaluationPoolMappingId:"
				+ asEvaluationPoolMappingId);
		// Prepare the filter to get the
		String lsFilter = HHSP8Constants.PROPERTY_PE_PROCURMENT_ID + "='" + asProcurementId + "'";
		if (null != asEvaluationPoolMappingId
				&& !asEvaluationPoolMappingId.equalsIgnoreCase(ApplicationConstants.EMPTY_STRING))
		{
			lsFilter = lsFilter + " and " + HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID + "='"
					+ asEvaluationPoolMappingId + "'";
		}
		String[] loWorkflowNames =
		{ HHSP8Constants.WORKFLOW_NAME_201_ACCEPT_PROPOSAL, HHSP8Constants.WORKFLOW_NAME_202_REVIEW_SCORES,
				HHSP8Constants.WORKFLOW_NAME_202_EVALUATION };

		// Whenever performance issues arise: Create and index on contractId and

		try
		{
			// Call the method that will cancel the award workflows.
			WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
			loWorkflowOperations.cancelWorkflows(lsFilter, loWorkflowNames, moPeSession);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancel workflow with filter:" + lsFilter, aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the cancel workflow with filter:" + lsFilter, aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the cancelUnusedWorkflowsFromAward method in HHSComponentOperations",
					aoAppEx);
		}
	}

	/**
	 * This method will verify if the given workflow ids are finished.
	 * 
	 * This method uses the checkTaskFinished method from the WorkflowOperations
	 * class
	 * 
	 * @param asEvaluationPoolMappingId
	 * @return boolean
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean checkEvaluationTaskFinished(String asEvaluationPoolMappingId) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.checkEvaluationTaskFinished.asEvaluationPoolMappingId:"
				+ asEvaluationPoolMappingId);
		boolean lbCompleted = true;
		P8ProcessOperationForSolicitationFinancials loP8OperationObj = new P8ProcessOperationForSolicitationFinancials();
		P8ProcessServiceForSolicitationFinancials loP8ServiceObj = new P8ProcessServiceForSolicitationFinancials();
		Channel loChannel = new Channel();
		try
		{
			// This method calls the checkTaskFinished method and passes the
			// workflowIds ad argument
			HashMap loWFProperties = new HashMap();
			loWFProperties.put(HHSP8Constants.PROPERTY_PE_EVAL_POOL_MAPPING_ID, asEvaluationPoolMappingId);
			loWFProperties.put(HHSP8Constants.PROPERTY_PE_TASK_TYPE, HHSP8Constants.TASK_EVALUATE_PROPOSAL);
			loWFProperties.put(HHSP8Constants.TASK_STATUS, HHSP8Constants.TASK_STATUS_INREVIEW);
			P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
			P8UserSession loUserSession = loFilenetConnection.getFileNetConnection(loFilenetConnection
					.setP8SessionVariables());
			loUserSession.setFilenetPEDBSession(HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
					.openSession());
			int liPERegionId = moPeSession.getIsolatedRegion();
			String lsViewName = loP8ServiceObj.getPEViewName(HHSP8Constants.HSS_QUEUE_NAME, liPERegionId);
			String lsWhereClause = loP8ServiceObj.createWhereClause(loWFProperties);
			if (null == lsWhereClause || lsWhereClause.isEmpty())
			{
				throw new ApplicationException(
						"Exception in HHSComponentOperations.checkEvaluationTaskFinished() :Where Condition is not set");
			}
			List<String> loEvaluationWorkflowIdsArrayList = loP8OperationObj.fetchALLWorkflowIdFromView(
					loUserSession.getFilenetPEDBSession(), lsViewName, lsWhereClause);
			// Start || Changes as a part of release 3.6.0 for enhancement
			// request 5905
			loChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvaluationPoolMappingId);
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.GET_EVAL_PROGRESS_STATUS_FLAG);
			Boolean loEvalProgStatusFlag = (Boolean) loChannel.getData(HHSP8Constants.EVAL_PROGRESS_STATUS_FLAG);
			if ((null != loEvaluationWorkflowIdsArrayList && !loEvaluationWorkflowIdsArrayList.isEmpty() && loEvaluationWorkflowIdsArrayList
					.size() > 0) || loEvalProgStatusFlag)
			{ // End || Changes as a part of release 3.6.0 for enhancement
				// request 5905
				lbCompleted = false;
			}
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during checkEvaluationTaskFinished", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured during checkEvaluationTaskFinished", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the checkEvaluationkTaskFinished method in HHSComponentOperations",
					aoAppEx);
		}
		return lbCompleted;
	}

	/**
	 * This method added as a part of release 3.8.0 enhancement 6534
	 * 
	 * <ul>
	 * This method Enable Change of Task Levels of Approvals for all Tasks
	 * Regardless of Whether there are Tasks Inflight
	 * </ul>
	 * 
	 * @param asTaskType taskType
	 * @param asAgencyID agencyID
	 * @param aoUsers List of Users
	 * @return boolean
	 * @throws Exception
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	public boolean setReviewLevelPropertiesInFilenet(String asTaskType, String asAgencyID, int aiOldDBReviewLevel,
			int aiNewDBReviewLevel) throws Exception
	{
		try
		{
			LOG_OBJECT
					.Debug("Entering HHSComponentOperations :: setReviewLevelPropertiesInFilenet method with parameters:::"
							+ asTaskType
							+ " :: "
							+ asAgencyID
							+ " :: "
							+ aiOldDBReviewLevel
							+ " :: "
							+ aiNewDBReviewLevel);
			P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
			P8UserSession loUserSession = loFilenetConnection.getFileNetConnection(loFilenetConnection
					.setP8SessionVariables());
			P8ProcessServiceForSolicitationFinancials loP8ServiceObj = new P8ProcessServiceForSolicitationFinancials();
			LOG_OBJECT.Debug("Calling changeReviewLevels method of P8ProcessServiceForSolicitationFinancials class:::");
			loP8ServiceObj.changeReviewLevels(loUserSession, asTaskType, asAgencyID, aiOldDBReviewLevel,
					aiNewDBReviewLevel);
			LOG_OBJECT
					.Debug("Executed changeReviewLevels method of P8ProcessServiceForSolicitationFinancials successfully:::");

			Channel loChannelObj = new Channel();
			Map loAgencyMap = new HashMap();

			int liReviewProcessID = HHSConstants.INT_ZERO;
			loAgencyMap.put(HHSConstants.AGENCY_ID, asAgencyID);
			loAgencyMap.put(HHSConstants.TASK_TYPE, asTaskType);
			String lsReviewProcessID = (String) HHSConstants.TASK_ID_PROCESS_MAP.get(asTaskType);
			if (null != lsReviewProcessID)
			{
				liReviewProcessID = Integer.valueOf(lsReviewProcessID).intValue();
			}
			loAgencyMap.put(HHSConstants.REVIEW_PROCESS_ID_KEY, liReviewProcessID);
			loAgencyMap.put(HHSConstants.UPDATE_REVIEW_PROGRESS_FLAG, HHSConstants.NO);
			loAgencyMap.put(HHSConstants.MOD_BY_USER_ID, HHSConstants.SYSTEM_USER);
			loChannelObj.setData(HHSConstants.REQ_PROPS_HASHMAP, loAgencyMap);
			LOG_OBJECT.Debug("Updating REVIEW_LEVEL_CHG_IN_PROGRESS with parameters :::" + loAgencyMap);
			TransactionManager.executeTransaction(loChannelObj, HHSP8Constants.UPDATE_REVIEW_PROGRESS_FLAG);
			LOG_OBJECT.Debug("Updated REVIEW_LEVEL_CHG_IN_PROGRESS to 'NO' successfully :::");

		}
		catch (Exception aoAppEx)
		{
			ApplicationException loAppex = new ApplicationException(
					"Error in setTaskUnassignedForRemovedAgencyUsers:: ", aoAppEx);
			LOG_OBJECT.Error("Exception in HHSComponentOperations.setTaskUnassignedForRemovedAgencyUsers::", aoAppEx);
			throw loAppex;
		}
		return true;
	}

	/**
	 * This method is added in Release 5. This method is responsible for
	 * inserting Audit Details for CompletePSR Workflow.
	 * @param asProcurementId
	 * @throws ApplicationException
	 */
	public Boolean auditCompletePSRTask(String asProcurementId, String asUserId) throws ApplicationException
	{
		Boolean lbUpdateStatus = false;
		LOG_OBJECT.Debug("Entering auditCompletePSRTask");
		Channel loChannelObj = new Channel();
		try
		{
			loChannelObj.setData(HHSR5Constants.PROCUREMENT_ID, asProcurementId);
			// Task View History WorkFlow
			List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
			loAuditBeanList.add(HHSUtil.addAuditDataToChannel(HHSR5Constants.PROPERTY_TASK_CREATION_EVENT,
					HHSR5Constants.TASK_COMPLETE_PSR, HHSR5Constants.TASK_ASSIGNED_TO_UNASSIGNED_ACCO,
					HHSR5Constants.TASK_COMPLETE_PSR, asProcurementId, asUserId, HHSR5Constants.ACCELERATOR_AUDIT));

			loChannelObj.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
			loChannelObj.setData(HHSConstants.UPDATE_FLAG, HHSConstants.BOOLEAN_TRUE);
			TransactionManager.executeTransaction(loChannelObj, "insertPsrTaskAudit");
			lbUpdateStatus = (Boolean) loChannelObj.getData(HHSR5Constants.UPDATE_FLAG);
			if (!lbUpdateStatus)
			{
				throw new ApplicationException(
						"Exception in HHSComponentOperations.auditCompletePSRTask(), Exception while Updating auditCompletePSRTask in Procurement Table");
			}
		}
		// Any Exception from DAO class will be thrown as Application Exception
		// which will be handles over here. It throws Application Exception back
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error while auditCompletePSRTask", aoAppEx);
			throw aoAppEx;
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Error while auditCompletePSRTask", aoExp);
		}
		return lbUpdateStatus;
	}

	/**
	 * This method added as a part of release 5
	 * 
	 * <ul>
	 * This method update the column : PCOF_PSR_VERSION_NUMBER of Procurement
	 * table and to terminate existing PCOF tasks
	 * </ul>
	 * 
	 * @param asProcurementIds String
	 * @return asWobNumber String
	 * @throws ApplicationException
	 */
	public void updatePcofPsrVersionNumberAndTerminate(String asProcurementId, String asWobNumber)
			throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.updatePcofPsrVersionNumberAndTerminate.asProcurementId:"
				+ asProcurementId);
		WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
		// Prepare the filter for the workflows that need to be cancelled.
		String lsFilter = HHSP8Constants.PROPERTY_PE_PROCURMENT_ID + "='" + asProcurementId + "' and "
				+ HHSP8Constants.F_WOB_NUM + "!= '" + asWobNumber + "'";
		String[] loWorkflowNames =
		{ HHSP8Constants.WORKFLOW_NAME_301_PROCUREMENT_CERTIFICATION_FUNDS };

		// Whenever performance issues arise: Create and index on contractId and
		// change the query implementation to make use of this index
		try
		{
			// Call the method that will cancel the given workflows for the
			// given filter
			loWorkflowOperations.cancelWorkflows(lsFilter, loWorkflowNames, moPeSession);
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the updatePcofPsrVersionNumberAndTerminate with filter:"
					+ lsFilter, aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the updatePcofPsrVersionNumberAndTerminate with filter:"
					+ lsFilter, aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the updatePcofPsrVersionNumberAndTerminate method in HHSComponentOperations",
					aoAppEx);
		}
	}

	// R5 change starts
	/**
	 * This method is added in Release 5 This method start the workflow for a
	 * contract.
	 * 
	 * @param asProcurementId
	 * @param abFirstRound
	 * @param asTaskOwner
	 * @param asEvaluationPoolMappingId
	 * @return
	 * @throws ApplicationException
	 */
	public String[] startContractOrNegotiationWorkflows(String asProcurementId, boolean abFirstRound,
			String asTaskOwner, String asEvaluationPoolMappingId, String asCompetitionPoolTitle,
			Boolean aoIsNegotiationRequired) throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.startContractOrNegotiationWorkflows.asProcurementId:"
				+ asProcurementId);
		LOG_OBJECT.Debug("HHSComponentOperations.startContractOrNegotiationWorkflows.abFirstRound:" + abFirstRound);
		LOG_OBJECT.Debug("HHSComponentOperations.startContractOrNegotiationWorkflows.asTaskOwner:" + asTaskOwner);
		LOG_OBJECT.Debug("HHSComponentOperations.startContractOrNegotiationWorkflows.asEvaluationPoolMappingId:"
				+ asEvaluationPoolMappingId);
		LOG_OBJECT.Debug("HHSComponentOperations.startContractOrNegotiationWorkflows.asCompetitionPoolTitle:"
				+ asCompetitionPoolTitle);
		LOG_OBJECT.Debug("HHSComponentOperations.startContractOrNegotiationWorkflows.aoIsNegotiationRequired:"
				+ aoIsNegotiationRequired);
		// Retrieve the contract detail beans and start the contract
		// configuration workflow
		String[] loStartedWobs = null;
		try
		{
			if (aoIsNegotiationRequired)
			{
				loStartedWobs = launchContractNegotiationWorkflow(asProcurementId, abFirstRound, asTaskOwner,
						asEvaluationPoolMappingId, asCompetitionPoolTitle, true);
			}
			else
			{
				loStartedWobs = startContractWorkflows(asProcurementId, abFirstRound, asTaskOwner,
						asEvaluationPoolMappingId, asCompetitionPoolTitle);
			}
			// handling Application Exception thrown by any action/resource
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Error occured on executing the startContractOrNegotiationWorkflows:", aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoEx)
		{
			LOG_OBJECT.Error("Error occured on executing the startContractOrNegotiationWorkflows:", aoEx);
			throw new ApplicationException(
					"Error occured during executing the startContractOrNegotiationWorkflows method in HHSComponentOperations",
					aoEx);
		}
		return loStartedWobs;
	}

	/**
	 * This method is added to start Contracts After Negotiations are complete
	 * 
	 * @param asEvalPoolMappingId String
	 * @param asProviderId String
	 * @param asProcurementId String
	 * @param asCompetitionPoolTitle String
	 * @param asTaskOwner String
	 * @throws ApplicationException
	 */

	public void startContractsAfterNegotiations(String asEvalPoolMappingId, String asProviderId,
			String asProcurementId, String asCompetitionPoolTitle, String asTaskOwner) throws ApplicationException
	{
		LOG_OBJECT.Debug("Entered HHSComponentOperations.startContractsAfterNegotiations");
		Channel loChannel = new Channel();
		Map loHmRequiredProps = new HashMap();
		ContractDetailsBean loContractDetailsBean = null;
		String lsWorkFlowName = HHSP8Constants.WORKFLOW_NAME_302_CONTRACT_CONFIGURATION;
		try
		{
			loHmRequiredProps.put(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
			loHmRequiredProps.put(HHSConstants.PROVIDER_ID, asProviderId);
			loChannel.setData(HHSConstants.AO_HASH_MAP, loHmRequiredProps);
			TransactionManager.executeTransaction(loChannel, HHSP8Constants.FETCH_NEGOTIATIONS_CONTRACT_DETAILS);
			// Retrieve the requested data from the channel
			loContractDetailsBean = (ContractDetailsBean) loChannel.getData(HHSConstants.AO_CONTRACT_BEAN);
			if (null == loContractDetailsBean)
			{
				loChannel.setData(HHSConstants.EVALUATION_POOL_MAPPING_ID, asEvalPoolMappingId);
				TransactionManager.executeTransaction(loChannel, HHSP8Constants.FINISH_CONFIGURE_AWARD_CONTRACTS);
			}
			else
			{
				WorkflowOperations loWorkflowOperations = new WorkflowOperations(LOG_OBJECT);
				String loStartedWobs = null;
				loStartedWobs = loWorkflowOperations.startNegotiationContractRelatedWorkflows(moPeSession,
						asProcurementId, lsWorkFlowName, loContractDetailsBean, asEvalPoolMappingId,
						asCompetitionPoolTitle, asTaskOwner);
				LOG_OBJECT
						.Debug("Exited HHSComponentOperations.startContractsAfterNegotiations with created wobnumber:"
								+ loStartedWobs);
			}
		}
		catch (ApplicationException aoAppExp)
		{
			aoAppExp.setContextData(loHmRequiredProps);
			LOG_OBJECT.Error("Exception Occured while displaying pop up for Finalize/Update Results screen", aoAppExp);
		}
		// handling exception other than Application Exception.
		catch (Exception aoExp)
		{
			LOG_OBJECT.Error("Exception Occured while displaying pop up for Finalize/Update Results screen", aoExp);
		}
	}

	/**
	 * This method is added in R5 to update default assignee details in filenet
	 * @param asWobnumber
	 * @param asEntityId
	 * @param asTaskType
	 * @param asTaskLevel
	 * @throws ApplicationException
	 * 
	 */
	public String[] setAssigneeDetails(String asWobnumber, String asEntityId, String asTaskType, String asTaskLevel)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.setAssigneeDetails.asWobnumber:" + asWobnumber);
		LOG_OBJECT.Debug("HHSComponentOperations.setAssigneeDetails.asEntityId:" + asEntityId);
		LOG_OBJECT.Debug("HHSComponentOperations.setAssigneeDetails.asTaskType:" + asTaskType);
		LOG_OBJECT.Debug("HHSComponentOperations.setAssigneeDetails.asTaskLevel:" + asTaskLevel);
		//R7 start::: Auto approval changes
		String[] loReturnData = null;
		String lsClassName = null;
		Object loObj;
		//R7 end::: Auto approval changes
		try
		{
			Channel loChannelObj = new Channel();
			loChannelObj.setData(HHSConstants.ENTITY_ID, asEntityId);
			loChannelObj.setData(HHSConstants.TASK_TYPE, HHSUtil.setTaskType(asTaskType));
			loChannelObj.setData(HHSConstants.TASK_LEVEL, asTaskLevel);
			TransactionManager.executeTransaction(loChannelObj, HHSR5Constants.GET_ASSIGNMENT_DB_DETAILS_TX);
			TaskDetailsBean loTaskDetailsBean = (TaskDetailsBean) loChannelObj.getData(ApplicationConstants.TASK_DETAILS_BEAN);
			//R7 start::: Auto approval changes
			// A transaction to get preprocessing class name for the process id
			LOG_OBJECT.Info("TASK BEAN:::" + loTaskDetailsBean);
			Channel loChannelForPreprocessing = new Channel();
			loChannelForPreprocessing.setData(HHSConstants.TASK_TYPE, HHSUtil.setTaskType(asTaskType));
			LOG_OBJECT.Info("Calling he transaction for calling preprocessing");
			TransactionManager.executeTransaction(loChannelForPreprocessing, HHSR5Constants.TRANSACTION_PRE_PROCESSING_CLASS);
			lsClassName =  (String) loChannelForPreprocessing.getData(HHSR5Constants.PRE_PROCESSING_CLASS);
			LOG_OBJECT.Info("Processing Class" + lsClassName);
			if(null != lsClassName && !lsClassName.isEmpty())
			{
				LOG_OBJECT.Info("If found class - 1 :"+ lsClassName);
				// if class name is not null execute preprocessing and if satisfy threshold put system user in loReturnData
				loObj = Class.forName(lsClassName).newInstance();
				PreprocessorApproval loPreProcessing =  (PreprocessorApproval) loObj;
				loReturnData = loPreProcessing.isAutoApprovalApplicable(loFilenetSession,asEntityId,asWobnumber,loTaskDetailsBean,asTaskType);
				LOG_OBJECT.Info("Return Data:::" + loReturnData);
			}
			//R7 end::: Auto approval changes
			else
			{
				if (null == loTaskDetailsBean)
				{
					LOG_OBJECT.Info("In null taskBean");
					loReturnData = new String[1]; 
					loReturnData[0] = "";
				}
				else if (StringUtils.isNotBlank(loTaskDetailsBean.getReassignUserId()))
				{
					LOG_OBJECT.Info("In not null taskBean");
					loReturnData = new String[2];
					loReturnData[0] = loTaskDetailsBean.getReassignUserId();
					loReturnData[1] = loTaskDetailsBean.getReassignUserName();
					List<HhsAuditBean> loAuditBeanList = new ArrayList<HhsAuditBean>();
					loAuditBeanList.add(HHSUtil.addAuditDataToChannel(
							HHSR5Constants.TASK_ASSIGNMENT,
							asTaskType,
							HHSConstants.TASK_ASSIGNED_TO + HHSR5Constants.COLON_AOP
							+ loReturnData[1], asTaskType, asEntityId,
							HHSConstants.SYSTEM_USER, HHSR5Constants.AGENCY_AUDIT));
					loChannelObj.setData(ApplicationConstants.AUDIT_BEAN_LIST, loAuditBeanList);
					loChannelObj.setData(HHSConstants.UPDATE_FLAG, HHSConstants.BOOLEAN_TRUE);
					LOG_OBJECT.Debug("Creating AutoAssign Audit:" + loAuditBeanList.toString());
					TransactionManager.executeTransaction(loChannelObj, HHSR5Constants.INSERT_PSR_AUDIT);
				}
			}
		}
		catch (ApplicationException aoAppEx) {
			LOG_OBJECT.Error("Unable to get assignment details" + aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Unable to get assignment details" + aoAppEx);
			throw new ApplicationException("Error occured while fetching default assignment details",aoAppEx);
		}
		LOG_OBJECT.Debug("Exit:: HHSComponentOperations.setAssigneeDetails");
		return loReturnData;
	}

	
	// R5 change ends

	// R6 changes
	/**
	 * R6: This method will launch a new Contract Configuration work flow when COF
	 * that was generated through batch is returned
	 * @param loFinancialWFBean
	 * @param asContractId
	 * @param asWobNumber
	 * @param asInvoiceNumber
	 * @param asBudgetId
	 * @param asAdvanceBudgetId
	 * @param asLaunchBy
	 * @param asDependentTaskWFName
	 * @param loChannelObj
	 * @return linkedWobNumber
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private String handleProcessWorkflowReturnCOFBulkUpload(FinancialWFBean asFinancialWFBean, String asContractId,
			String asWobNumber, String asLaunchBy, String asDependentTaskWFName, Channel asChannelObj)
			throws ApplicationException
	{
		LOG_OBJECT.Debug("HHSComponentOperations.handleProcessWorkflowReturnCOFBulkUpload.asFinancialWFBean:"
				+ asFinancialWFBean);
		LOG_OBJECT.Debug("HHSComponentOperations.handleProcessWorkflowReturnCOFBulkUpload.asWobNumber:" + asWobNumber);
		LOG_OBJECT.Debug("HHSComponentOperations.handleProcessWorkflowReturnCOFBulkUpload.asLaunchBy:" + asLaunchBy);
		LOG_OBJECT.Debug("HHSComponentOperations.handleProcessWorkflowReturnCOFBulkUpload.asDependentTaskWFName:"
				+ asDependentTaskWFName);
		LOG_OBJECT
				.Debug("HHSComponentOperations.handleProcessWorkflowReturnCOFBulkUpload.asChannelObj:" + asChannelObj);

		HashMap loTaskRequiredProps = new HashMap();
		String lsDependentTaskWobNumber = null;

		try
		{
			/* Checking if the COF task is returned with linkedWobNumber empty */
			LOG_OBJECT.Debug("Inside the block of creating workflow:" + asDependentTaskWFName);
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROCURMENT_ID, asFinancialWFBean.getProcurementId());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROCUREMENT_TITLE,
					asFinancialWFBean.getProcurementTitle());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROVIDER_ID, asFinancialWFBean.getProviderId());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROVIDER_NAME, asFinancialWFBean.getProviderName());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROCUREMENT_EPIN, asFinancialWFBean.getProcEpin());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_PROGRAM_NAME, asFinancialWFBean.getProgramName());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_AWARD_EPIN, asFinancialWFBean.getAwardEpin());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_CT, asFinancialWFBean.getContractNum());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_SUBMITTED_BY, asLaunchBy);
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_AGENCY_ID, asFinancialWFBean.getAgencyId());
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_CONTRACT_ID, asContractId);
			loTaskRequiredProps.put(HHSP8Constants.LINKED_WOB_NO, asWobNumber);
			loTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_COMPETITION_POOL_TITLE,
					asFinancialWFBean.getCompetitionPoolTitle());
			loTaskRequiredProps.put(HHSP8Constants.ACTION_FLAG, HHSP8Constants.COMPONENT_OPERATIONS_PARAM_ACTIONFLAG_RETURNED);
			/* Action flag as returned */
			loTaskRequiredProps.put(HHSP8Constants.TASK_STATUS, HHSP8Constants.TASK_STATUS_RETURNEDFORREVISION);
			/* Task status Returned for revision */
			loTaskRequiredProps.put(HHSConstants.SUBMITTED_BY, HHSConstants.SYSTEM_USER);
			loTaskRequiredProps.put(HHSConstants.PROPERTY_PE_SUBMITTED_BY, HHSConstants.SYSTEM_USER);
			loTaskRequiredProps.put(HHSConstants.PROPERTY_PE_LAUNCH_ORG_TYPE, HHSConstants.CITY);
			lsDependentTaskWobNumber = getWorkFlowDetailsReturnCOF(asFinancialWFBean, asContractId, asLaunchBy,
					asDependentTaskWFName, asChannelObj, loTaskRequiredProps);
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.getWorkFlowDetailsReturnCOF()", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the getWorkFlowDetailsReturnCOF method in HHSComponentOperations",
					aoAppEx);
		}
		return lsDependentTaskWobNumber;
	}

	/**
	 * This method is used to get work flow details for new Contract
	 * configuration task returned from COF
	 * @param asFinancialWFBean
	 * @param asContractId
	 * @param asLaunchBy
	 * @param asDependentTaskWFName
	 * @param asChannelObj
	 * @param aoTaskRequiredProps
	 * @return
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	private String getWorkFlowDetailsReturnCOF(FinancialWFBean asFinancialWFBean, String asContractId,
			String asLaunchBy, String asDependentTaskWFName, Channel asChannelObj, HashMap aoTaskRequiredProps)
			throws ApplicationException
	{
		String lsDependentTaskWobNumber = null;
		try
		{
			LOG_OBJECT.Debug("HHSComponentOperations.getWorkFlowDetailsReturnCOF.asFinancialWFBean:"
					+ asFinancialWFBean);
			LOG_OBJECT.Debug("HHSComponentOperations.getWorkFlowDetailsReturnCOF.asContractId:" + asContractId);
			LOG_OBJECT.Debug("HHSComponentOperations.getWorkFlowDetailsReturnCOF.asLaunchBy:" + asLaunchBy);
			LOG_OBJECT.Debug("HHSComponentOperations.getWorkFlowDetailsReturnCOF.asDependentTaskWFName:"
					+ asDependentTaskWFName);
			LOG_OBJECT.Debug("HHSComponentOperations.getWorkFlowDetailsReturnCOF.asChannelObj:" + asChannelObj);
			LOG_OBJECT.Debug("HHSComponentOperations.getWorkFlowDetailsReturnCOF.aoTaskRequiredProps:"
					+ aoTaskRequiredProps);

			if (null != asLaunchBy && !asLaunchBy.isEmpty())
			{
				aoTaskRequiredProps.put(HHSP8Constants.LAUNCH_BY, asLaunchBy);
			}

			int liReviewProcessId = HHSP8Constants.FINANCIAL_WF_ID_MAP.get(asDependentTaskWFName);

			asChannelObj.setData(HHSP8Constants.AGENCY_ID_KEY, asFinancialWFBean.getAgencyId());
			asChannelObj.setData(HHSP8Constants.REVIEW_PROCESS_ID, liReviewProcessId);

			TransactionManager.executeTransaction(asChannelObj, HHSP8Constants.FETCH_REVIEW_LEVELS);
			Integer liReviewLevels = (Integer) asChannelObj.getData(HHSP8Constants.REVIEW_LEVEL);

			aoTaskRequiredProps.put(HHSP8Constants.PROPERTY_PE_TASK_TOTAL_LEVEL, liReviewLevels);

			LOG_OBJECT.Debug("Executing P8ProcessOperationForSolicitationFinancials().launchWorkflow: "
					+ asDependentTaskWFName);

			lsDependentTaskWobNumber = new P8ProcessOperationForSolicitationFinancials().launchWorkflow(moPeSession,
					asDependentTaskWFName, aoTaskRequiredProps, HHSP8Constants.DEFAULT_SYSTEM_USER);

			// added for task audit
			if (null != asDependentTaskWFName)
			{
				String lsEntityType = HHSConstants.FINANCIAL_WF_NAME_TYPE_MAP.get(asDependentTaskWFName);
				String lsEntityId = null;
				String lsEventType = null;
				String lsTaskLevel = null;
				if (null != lsEntityType)
				{
					// Since COF is returned and we are launching Configuration,
					// the level will be 1
					lsEntityId = asContractId;
					lsEventType = HHSConstants.APPROVED_AND_LAUNCHED;
					lsTaskLevel = HHSConstants.LEVEL + HHSConstants.ONE;

					HhsAuditBean loHhsAuditBean = new HhsAuditBean();
					loHhsAuditBean.setEntityType(lsEntityType);
					loHhsAuditBean.setWorkflowId(lsDependentTaskWobNumber);
					loHhsAuditBean.setContractId(asContractId);
					loHhsAuditBean.setUserId(null != asLaunchBy ? asLaunchBy : HHSP8Constants.DEFAULT_SYSTEM_USER);
					loHhsAuditBean.setEntityId(lsEntityId);
					loHhsAuditBean.setTaskLevel(lsTaskLevel);
					loHhsAuditBean.setTaskEvent(lsEventType);
					asChannelObj.setData(HHSP8Constants.AUDIT_BEAN, loHhsAuditBean);
					LOG_OBJECT.Debug("Inserting audit for workflow launch with properties: "
							+ loHhsAuditBean.toString());
					TransactionManager.executeTransaction(asChannelObj, HHSP8Constants.INSERT_TASK_AUDIT);
				}
			}
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error("Exception in HHSComponentOperations.handleProcessWorkflowReturnCOFBulkUpload()", aoAppEx);
			throw new ApplicationException(
					"Error occured during executing the handleProcessWorkflowReturnCOFBulkUpload method in HHSComponentOperations",
					aoAppEx);
		}
		return lsDependentTaskWobNumber;
	}
	//R7 changes start
	/**
	 * The method is added in Release 7 for getting application setting data.
	 * @return loApplicationSettingMap
	 * @throws ApplicationException
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, String> getApplicationSettingsBulk() throws ApplicationException
	{
		LOG_OBJECT.Info("Entering into getApplicationSettingsBulk");
		HashMap<String, String> loApplicationSettingMap = new HashMap<String, String>();
		Channel loChannel = new Channel();
		TransactionManager.executeTransaction(loChannel, HHSR5Constants.APPLICATION_SETTING_DB);
		List<HashMap<String, String>> loApplicationSettingMapList = (List<HashMap<String, String>>) loChannel
				.getData(HHSR5Constants.APPLICATION_SETTING_MAP);
		for (Iterator<HashMap<String, String>> loIterator = loApplicationSettingMapList.iterator(); loIterator.hasNext();)
		{
			HashMap<String, String> loMap = (HashMap<String, String>) loIterator.next();
			loApplicationSettingMap.put(loMap.get(HHSR5Constants.COMPONENT_NAME).concat(HHSConstants.UNDERSCORE).concat(loMap.get(HHSR5Constants.SETTINGS_NAME)),
					loMap.get(HHSR5Constants.SETTINGS_VALUE));

		}
		return loApplicationSettingMap;
	}
	
	/**
	 * The method is added in Release 7 to handle return for revision scenarios for all task.
	 * @param asWobnumber as the work object number
	 * @param asEntityId as the entity id of the task
	 * @param asWorkflowName as the name of workflow
	 * @throws ApplicationException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setDetailsForReturnForRevision(String asWobnumber, String asEntityId, String asWorkflowName) throws ApplicationException
	{
		LOG_OBJECT.Info("Entering into setDetailsForReturnForRevision: WobNumber" + asWobnumber);
		LOG_OBJECT.Info("Entering into setDetailsForReturnForRevision: Entity Id" + asEntityId);
		LOG_OBJECT.Info("Entering into setDetailsForReturnForRevision: WorkFlow Name" + asWorkflowName);
		String lsAutoApprovalFlag ="false";
		HashMap loHMWFReqProp = new HashMap();
		String lsClassName = null;
		Object loObj;
		String lsQueryId = null;
		String[] loReturnData = null;
		DefaultAssignment loDefaultAssignment = null;
		Channel loChannel =new Channel();
		try
		{
			//write a transaction to get Pre processing
			LOG_OBJECT.Info("setDetailsForReturnForRevision:::Calling the transaction for calling preprocessing");
			LOG_OBJECT.Debug("setDetailsForReturnForRevision:::Calling the transaction for calling preprocessing");
			TaskDetailsBean loTaskBean = null;
			String lsTaskType = HHSConstants.FINANCIAL_WF_NAME_TYPE_MAP.get(asWorkflowName);
			loChannel.setData(HHSConstants.TASK_TYPE, lsTaskType);
			TransactionManager.executeTransaction(loChannel, HHSR5Constants.TRANSACTION_PRE_PROCESSING_CLASS);
			lsClassName =  (String) loChannel.getData(HHSR5Constants.PRE_PROCESSING_CLASS);
			LOG_OBJECT.Info("Processing Class" + lsClassName);
			if(null != lsClassName && !lsClassName.isEmpty())
			{
				LOG_OBJECT.Info("In case of modification budget type, checking the threshold value with new modification amount");
				loObj = Class.forName(lsClassName).newInstance();
				PreprocessorApproval loPreProcessing = (PreprocessorApproval) loObj;
				loReturnData = loPreProcessing.isAutoApprovalApplicable(loFilenetSession,asEntityId,asWobnumber,loTaskBean,lsTaskType);
				LOG_OBJECT.Info("Size details:::" +loReturnData.length);
				LOG_OBJECT.Info("the assignee details:::" +loReturnData);
				
			}
			if(null != loReturnData && loReturnData.length>1)
			{
				LOG_OBJECT.Info("When the modification budget value satisfies threshold" +lsAutoApprovalFlag);
				loHMWFReqProp.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO, loReturnData[0]);
				loHMWFReqProp.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO_NAME, loReturnData[1]);
			}
			else
			{
				LOG_OBJECT.Info("When the modification budget value doesnot satisfy threshold or for other tasks'return for revision cases" +lsAutoApprovalFlag);
				loHMWFReqProp.put(HHSConstants.PROPERTY_PE_TASK_VISIBILITY, true);
				lsQueryId = HHSR5Constants.GET_QUERY_ID_DEFAULT_ASSIGNEE_FROM_WORKFLOW_NAME.get(asWorkflowName);
				if (!StringUtils.isEmpty(lsQueryId))
				{
					Channel loChannelForDefaultAssignment = new Channel();
					loChannelForDefaultAssignment.setData(HHSR5Constants.AS_ENTITY_ID,asEntityId);
					loChannelForDefaultAssignment.setData(HHSR5Constants.AS_QUERY_ID,lsQueryId);
					loChannelForDefaultAssignment.setData(HHSR5Constants.TASK_REVIEW_PROCESS_ID,HHSR5Constants.GET_QUERY_ID_DEFAULT_ASSIGNEE_FROM_REVIEW_PROCESS_ID.get(asWorkflowName));
					TransactionManager.executeTransaction(loChannelForDefaultAssignment, HHSR5Constants.DEFAULT_ASSIGNMENT_ASSIGNEE);
					loDefaultAssignment  = (DefaultAssignment) loChannelForDefaultAssignment.getData("loDefaultAssignment");
					LOG_OBJECT.Info("Default Assigmnent" +loDefaultAssignment);
				}
				if (loDefaultAssignment != null && loDefaultAssignment.getDefaultAssignmentId() != null)
				{
					loHMWFReqProp.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO, loDefaultAssignment.getDefaultAssignmentId());
					loHMWFReqProp.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO_NAME, loDefaultAssignment.getOrgName());
				}
				else
				{
					loHMWFReqProp.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO, HHSConstants.UNASSIGNED_LEVEL1);
					loHMWFReqProp.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO_NAME, HHSConstants.UNASSIGNED_LEVEL1);
				}
			}
			loHMWFReqProp.put(HHSConstants.TASK_STATUS, HHSConstants.TASK_IN_REVIEW);
			LOG_OBJECT.Info("Map for setting WorkFLow properties::: " +loHMWFReqProp );
			new P8ProcessServiceForSolicitationFinancials().setWFProperty(loFilenetSession, asWobnumber, loHMWFReqProp);
		}
		catch (ApplicationException aoAppEx) {
			LOG_OBJECT.Error("Return for revision assignment failed::" + aoAppEx);
			throw aoAppEx;
		}
		catch (Exception aoAppEx)
		{
			LOG_OBJECT.Error("Return for revision assignment failed::" + aoAppEx);
			throw new ApplicationException("Error occured while fetching default assignment details::: setDetailsForReturnForRevision method",aoAppEx);
		}
	}
	
	/**
	 * The method is added in Release 7 for checking the auto approval condition
	 * for update budget. On finish of update configuration task, it will decide
	 * if the budget will go for auto approval or not.
	 * @param aoFieldValues
	 * @param asContractBudgetId
	 * @throws ApplicationException
	 */
	public void checkAutoApprovalForUpdate(String aoFieldValues, String asContractBudgetId) throws ApplicationException
	{

		LOG_OBJECT.Debug("HHSComponentOperations.checkAutoApprovalForUpdate.aoFieldValues:" + aoFieldValues);
		LOG_OBJECT.Debug("HHSComponentOperations.checkAutoApprovalForUpdate.asContractBudgetStatus:"
				+ asContractBudgetId);
		BudgetOperation loBudgetOperation = new BudgetOperation();
		Channel loChannelObjForBudgetReview = new Channel();
		P8UserSession loUserSession = null;
		try
		{
			P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
			loUserSession = loFilenetConnection.getFileNetConnection(loFilenetConnection.setP8SessionVariables());
			loUserSession.setFilenetPEDBSession(HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
					.openSession());
			TaskDetailsBean loTaskDetailsBean = loBudgetOperation.buildTaskDetailsBean(aoFieldValues);
			// On this moment we have a taskDetailbean with the correct data
			loChannelObjForBudgetReview.setData(HHSP8Constants.TASK_DETAILS_BEAN, loTaskDetailsBean);
			loChannelObjForBudgetReview.setData(HHSConstants.CONTRACT_ID_KEY, asContractBudgetId);
			loChannelObjForBudgetReview.setData(HHSConstants.AO_FILENET_SESSION, loUserSession);
			// Call the correct method: for transaction
			TransactionManager.executeTransaction(loChannelObjForBudgetReview, "isUpdateBudgetAutoApproval");
			// handling Application Exception thrown by any action/resource
			LOG_OBJECT.Debug("Exit HHSComponentOperations.checkAutoApprovalForUpdate.aoFieldValues:");
		}
		catch (ApplicationException aoAppEx)
		{
			LOG_OBJECT.Error(aoAppEx.getMessage(), aoAppEx);
			throw aoAppEx;
		}
	}
	//R7 changes end
}