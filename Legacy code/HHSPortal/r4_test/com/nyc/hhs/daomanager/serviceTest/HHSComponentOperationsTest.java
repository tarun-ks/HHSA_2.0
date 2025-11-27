package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nyc.hhs.component.HHSComponentOperations;
import com.nyc.hhs.component.operations.BudgetOperation;
import com.nyc.hhs.component.operations.ReassignEvaluationsOperation;
import com.nyc.hhs.component.operations.WorkflowOperations;
import com.nyc.hhs.constants.HHSP8Constants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.logger.LogInfo;
import com.nyc.hhs.model.EvaluationStatusBean;
import com.nyc.hhs.model.FinancialWFBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8ProcessOperationForSolicitationFinancials;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;

import filenet.vw.api.VWSession;

public class HHSComponentOperationsTest
{

	@Before
	public void setUp() throws ApplicationException
	{
	}

	@After
	public void tearDown() throws ApplicationException
	{

	}

	@Test
	public void testCallAuditService()
	{
		// 624
		boolean succes = false;
		try
		{
			HHSComponentOperations operation = new HHSComponentOperations();
			String asEventName = "Task Creation";
			String asEventType = "Workflow";
			String asData = "Task Assigned to: Unassigned Level 1";
			String asEntityType = "Contract Configuration";
			String asEntityId = "121";
			String asUserID = "";
			String asTableIdentifier = HHSP8Constants.AGENCY_AUDIT;

			// (asNotificationName);
			operation.callAuditService(asEventName, asEventType, asData, asEntityType, asEntityId, asUserID,
					asTableIdentifier);

			succes = true;

			assertTrue(succes);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testCallNotificationService()
	{
		// 624
		boolean succes = false;
		try
		{
			HHSComponentOperations operation = new HHSComponentOperations();
			String asNotificationName = "NT305a";
			String ProcurementTitle = "test22";
			String asAlertName = "NT305b";
			String[] asProperties =
			{ "PROCUREMENT_TITLE:" + ProcurementTitle, "ContractID:123", "BudgetID:11", "NewFiscalYearId:13" };
			String audienceType = "PROVIDER";
			String[] audienceList =
			{ "624" };

			operation
					.callNotificationService(asNotificationName, asAlertName, asProperties, audienceType, audienceList);
			succes = true;

			assertTrue(succes);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testHandleAlertNotificationsForAward()
	{
		// 624
		boolean succes = false;
		try
		{
			HHSComponentOperations operation = new HHSComponentOperations();

			operation.handleAlertNotificationsForAward("Approved", "1734", "Test", null, null);
			succes = true;

			assertTrue(succes);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testGetProposalIdsByProcurement()
	{
		// 624
		boolean succes = false;
		try
		{
			HHSComponentOperations operation = new HHSComponentOperations();
			operation.getProposalIdsByProcurement("624");

			succes = true;

			assertTrue(succes);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void teststartEvaluationWorkflows()
	{
		// 624
		boolean succes = false;
		try
		{
			HHSComponentOperations operation = new HHSComponentOperations();

			operation.startEvaluationWorkflows("2266",
					HHSP8Constants.INTERNAL_EXTERNAL_FLAG);

			new WorkflowOperations(new LogInfo(getClass()));
			// wfOperation.cancelWorkflows(startedWorkflowIds);
			// ("Started evaluation workflows removed");
			succes = true;

			assertTrue(succes);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testCancelWorkflows()
	{

		try
		{
			new HHSComponentOperations();

			// String[] startedWorkflowIds =
			// operation.startAcceptProposalWorkflows("624");
			WorkflowOperations wfo = new WorkflowOperations(new LogInfo(getClass()));
			VWSession loVWSession = null;
			P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
			loVWSession = loFilenetConnection.getPESession(loFilenetConnection.setP8SessionVariables());

			String filter = HHSP8Constants.PROCUREMENT_ID + "='1393'";
			String[] workflowNames =
			{ HHSP8Constants.WORKFLOW_NAME_201_ACCEPT_PROPOSAL };
			// ("Started workflows:"+startedWorkflowIds[0].toString());
			wfo.cancelWorkflows(filter, workflowNames, loVWSession);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void teststartAcceptProposalWorkflows()
	{
		// 624
		boolean succes = false;
		try
		{
			HHSComponentOperations operation = new HHSComponentOperations();

			String procurementId = "1461";
			operation.startAcceptProposalWorkflows(procurementId, null);

			new WorkflowOperations(new LogInfo(getClass()));

			succes = true;

			assertTrue(succes);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testcheckEvaluationTaskFinished()
	{

		try
		{
			VWSession loVWSession = null;
			P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
			loVWSession = loFilenetConnection.getPESession(loFilenetConnection.setP8SessionVariables());
			new HHSComponentOperations();
			WorkflowOperations loWorkflowOperations = new WorkflowOperations(null);
			// String[] startedWorkflowIds =
			// operation.startEvaluationWorkflows(procurementId,HHSP8Constants.INTERNAL_FLAG);
			String[] startedWorkflowIds =
			{ "0983E6E7359A624387AA434BD166B879", "367DE81E3140034F83B7580C2EA1FD7D",
					"6CBDCF381E00DB4087ABF683BC42FF7E", "95A334AC12EF354EBF8E138F7D485E20",
					"B1AEB43F1A47D4499A6C64D4654C7271" };

			loWorkflowOperations.checkTaskFinished(loVWSession, startedWorkflowIds);
			WorkflowOperations wfOperation = new WorkflowOperations(new LogInfo(getClass()));
			wfOperation.cancelWorkflows(startedWorkflowIds);

			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);

		}
	}

	@Test
	public void teststartReviewScoreWorkflows()
	{
		// 624
		boolean succes = false;
		try
		{
			HHSComponentOperations operation = new HHSComponentOperations();

			String procurementId = "1737";
			operation.startReviewScoreWorkflows(procurementId);

			// WorkflowOperations wfOperation = new WorkflowOperations(new
			// LogInfo(getClass()));
			// wfOperation.cancelWorkflows(startedWorkflowIds);

			succes = true;

			assertTrue(succes);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testLaunchWorkflow() throws ApplicationException
	{
		P8ProcessOperationForSolicitationFinancials loP8ProcessOperationForSolicitationFinancials = new P8ProcessOperationForSolicitationFinancials();
		HashMap loTaskRequiredProps = new HashMap();
		VWSession loVWSession = null;
		P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
		loVWSession = loFilenetConnection.getPESession(loFilenetConnection.setP8SessionVariables());
		String asWorkflowName = "WF202 - Review Scores";
		loTaskRequiredProps.put("ProcurementEPin", "07113L0034");
		loTaskRequiredProps.put("AgencyID", "DOC");
		loTaskRequiredProps.put("ProposalTitle", "ProposalA");
		loTaskRequiredProps.put("FinalizeEvaluationDate", "04/01/13 00:00:00 am");
		loTaskRequiredProps.put("ProcurementTitle", "1stJuly");
		loTaskRequiredProps.put("ProcurementID", "1816");
		loTaskRequiredProps.put("procurementId", "1816");
		loTaskRequiredProps.put("ProviderName", "Accenture Services");
		loTaskRequiredProps.put("ProviderID", "Org_487");
		loTaskRequiredProps.put("ProposalID", "834");
		loTaskRequiredProps.put("AwardSelectionDate", "04/01/13 00:00:00 am");
		loP8ProcessOperationForSolicitationFinancials.launchWorkflow(loVWSession, asWorkflowName, loTaskRequiredProps,
				HHSP8Constants.DEFAULT_SYSTEM_USER);
	}

	@Test
	public void testDate()
	{

		DateFormat loSimpleDateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ss a");
		Date loDate = new Date();
		loSimpleDateFormat.format(loDate);

	}

	/*
	 * @Test public void testreassignWorkflow() { boolean succes = false; try {
	 * HHSComponentOperations operation = new HHSComponentOperations();
	 * 
	 * String[] currentAndOldUsers = {"Bart;Trab"}; String[]
	 * workflowNamesToSearch = {};
	 * 
	 * operation.reassignWorkflows(currentAndOldUsers, null);
	 * 
	 * succes = true;
	 * 
	 * assertTrue(succes); } catch (Exception e) { // TODO Auto-generated catch
	 * block e.printStackTrace(); assertTrue(false); } }
	 */

	@Test
	public void teststartContractWorkflow()
	{
		boolean succes = false;
		try
		{
			HHSComponentOperations operation = new HHSComponentOperations();
			new WorkflowOperations(new LogInfo(getClass()));

			String procurementId = "1393";
			operation.startContractWorkflows(procurementId, true, null);

			// wfOperation.cancelWorkflows(proposalWobs);
			// ("Removed contract workflows: "+Arrays.toString(proposalWobs));
			succes = true;

			assertTrue(succes);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testgetEvaluationStatusBeans()
	{
		String procurementId = "3";
		boolean succes = false;

		try
		{
			new HHSComponentOperations();
			ReassignEvaluationsOperation reo = new ReassignEvaluationsOperation(new LogInfo(getClass()));
			List<EvaluationStatusBean> esbs = reo.getEvaluationStatusBeans(procurementId, null);
			for (EvaluationStatusBean esb : esbs)
			{
			}
			succes = true;
			assertTrue(succes);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testinsertEvaluationStatusBeansIntoDB()
	{
		boolean succes = false;
		try
		{

			new HHSComponentOperations();
			ReassignEvaluationsOperation reo = new ReassignEvaluationsOperation(new LogInfo(getClass()));

			EvaluationStatusBean evaluationStatusBean = new EvaluationStatusBean();

			evaluationStatusBean.setEvalSettingsIntId("2");
			evaluationStatusBean.setStatusId("43");
			evaluationStatusBean.setCreatedByUserId("city_142");
			evaluationStatusBean.setModifiedByUserId("city_142");
			evaluationStatusBean.setOrganizationId("hhs");
			evaluationStatusBean.setProcStatusId("1");
			evaluationStatusBean.setProcurementId("3");
			evaluationStatusBean.setProposalId("2");

			reo.insertEvaluationStatusBeansIntoDB(evaluationStatusBean);
			succes = true;

			assertTrue(succes);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	/*
	 * @Test public void testdeleteEvaluationStatusRecord() { boolean succes =
	 * false; try { String evaluationStatusId = "476"; new
	 * HHSComponentOperations(); ReassignEvaluationsOperation reo = new
	 * ReassignEvaluationsOperation(new LogInfo(getClass()));
	 * 
	 * EvaluationStatusBean evaluationStatusBean = new EvaluationStatusBean();
	 * 
	 * evaluationStatusBean.setEvalSettingsIntId("2");
	 * evaluationStatusBean.setStatusId("43");
	 * evaluationStatusBean.setCreatedByUserId("city_142");
	 * evaluationStatusBean.setModifiedByUserId("city_142");
	 * evaluationStatusBean.setOrganizationId("hhs");
	 * evaluationStatusBean.setProcStatusId("1");
	 * evaluationStatusBean.setProcurementId("3");
	 * evaluationStatusBean.setProposalId("2");
	 * 
	 * reo.insertEvaluationStatusBeansIntoDB(evaluationStatusBean);
	 * reo.deleteEvaluationStatusRecord(evaluationStatusId); succes = true;
	 * 
	 * assertTrue(succes); } catch (Exception e) { e.printStackTrace();
	 * assertTrue(false); } }
	 * 
	 * @Test public void testdeleteEvaluationScoreRecord() { boolean succes =
	 * false; try { String evaluationStatusId = "477";
	 * 
	 * new HHSComponentOperations(); ReassignEvaluationsOperation reo = new
	 * ReassignEvaluationsOperation(new LogInfo(getClass()));
	 * 
	 * // TODO: Create a new evaluation score record
	 * reo.deleteEvaluationScoreRecord(evaluationStatusId); succes = true;
	 * 
	 * assertTrue(succes); } catch (Exception e) { e.printStackTrace();
	 * assertTrue(false); } }
	 * 
	 * @Test public void testDeleteEvaluationWorkflow() { boolean succes =
	 * false; try { List<String> evaluationStatusId = new ArrayList<String>();
	 * 
	 * new HHSComponentOperations(); ReassignEvaluationsOperation reo = new
	 * ReassignEvaluationsOperation(new LogInfo(getClass())); VWSession
	 * loPeSession = new HHSComponentP8Service().getP8PESession();
	 * reo.deleteEvaluationWorkflow(loPeSession,evaluationStatusId); succes =
	 * true;
	 * 
	 * assertTrue(succes); } catch (Exception e) { e.printStackTrace();
	 * assertTrue(false); } }
	 */

	@Test
	public void testDeleteEvaluationData()
	{
		boolean success = false;
		try
		{
			String procurementId = "1411";

			HHSComponentOperations hco = new HHSComponentOperations();

			hco.deleteEvaluationData("both", procurementId);
			success = true;
			assertTrue(success);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testReassignEvaluationTasksInternal()
	{
		boolean success = false;
		try
		{
			String procurementId = "1411";

			HHSComponentOperations hco = new HHSComponentOperations();

			hco.reassignEvaluationTasks(procurementId);
			success = true;
			assertTrue(success);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testReassignEvaluationTasksExternal()
	{
		boolean success = false;
		try
		{
			String procurementId = "1411";

			HHSComponentOperations hco = new HHSComponentOperations();

			hco.reassignEvaluationTasks(procurementId);
			success = true;
			assertTrue(success);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	/*
	 * @Test public void testRetrieveEvaluationStatusId() { boolean success =
	 * false; try { String proposalId = "2"; String intExtEvaluatorId = "2";
	 * String intExtFlag = "I";
	 * 
	 * WorkflowOperations wfo = new WorkflowOperations(null);
	 * 
	 * String badname = wfo.retrieveEvaluationStatusId(proposalId,
	 * intExtEvaluatorId, intExtFlag); ("("+badname+")"); success = true;
	 * assertTrue(success); } catch (Exception e) { e.printStackTrace();
	 * assertTrue(false); } }
	 */

	@Test
	public void testSetTaskUnassignedForRemovedAgencyUsers()
	{
		try
		{
			String taskType = "Contract Configuration";
			String agencyID = "DOC";
			String[] users = new String[]
			{ "agency_18" };

			HHSComponentOperations hco = new HHSComponentOperations();

			boolean status = hco.setTaskUnassignedForRemovedAgencyUsers(taskType, agencyID, users);
			assertTrue(status);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testProcessDependentWorkflow()
	{
		try
		{

			HHSComponentOperations hco = new HHSComponentOperations();
			// String[] contractWobs = hco.startContractWorkflows("624" , true);

			String depWF = HHSP8Constants.WORKFLOW_NAME_306_PAYMENT_REVIEW;
			String wobnr = "13E9135BE31B134E81D6C90A52A6CCBC";
			String linkedWobnr = "";
			String actionFlag = "NEW";
			String contractId = "2383";
			String invoiceNumber = "419";
			String budgetId = "10663";

			String result = hco.processDependentWorkflow(depWF, wobnr, linkedWobnr, actionFlag, contractId,
					invoiceNumber, budgetId);
			assertNotNull(result);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testCheckIfContractWorkflowsAreFinished()
	{
		try
		{

			HHSComponentOperations hco = new HHSComponentOperations();
			// hco.startContractWorkflows("1412", true);
			Boolean result = hco.checkIfContractWorkflowsAreFinished("1412");

			assertNotNull(result);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testTriggerEvaluationScoreWorkFlowStep()
	{
		try
		{

			HHSComponentOperations hco = new HHSComponentOperations();
			hco.triggerEvaluationScoreWorkFlowStep("4");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testReopenSelectedEvaluationTasks()
	{
		try
		{
			// start evaluation workfow
			// set the values correctly on start
			HHSComponentOperations hco = new HHSComponentOperations();
			hco.reopenSelectedEvaluationTasks("843");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testFindNumberOfSelectedProposals()
	{
		try
		{

			HHSComponentOperations hco = new HHSComponentOperations();
			Integer proposalCount = hco.findNumberOfSelectedProposals("1393");
			assertNotNull(proposalCount);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testInsertLineItemsForContractConfigTask()
	{
		try
		{

			HHSComponentOperations hco = new HHSComponentOperations();

			String str = "{agencyQueryId=null, proposalTitle=null, applicationId=null, assignedTo=agency_21, organizationId=null, entityType=Contract Budget Update Review, procurementId=, agencyId=DOC, firstRoundEvalCompDate=null, amendmentType=null, level=2, reassignUserName=null, userId=agency_21, procurementTitle=Arvind contract second 15 july, procurementEpin=05612L0009, linkedWobNum=null, awardEpin=05612L0009001, contractId=2910, taskActions=Approved, proposalId=, previousTaskStatus=null, baseContractId=null, totalLevel=2, taskAction=null, contractSourceId=, providerComment=, launchCOF=null, class=class com.nyc.hhs.model.TaskDetailsBean, assignedDate=07/15/2013, period=, ct=N/A, providerQueryId=null, startFiscalYear=null, awardSelectionDate=null, internalComment=, isTaskAssigned=true, commentsHistory=null, submittedDate=07/15/2013, eventName=null, subBudgetId=null, comment=null, contractConfWob=null, budgetId=11001, evaluationStatusId=null, organizationName=null, paymentId=null, taskType=taskBudgetUpdate, entityId=11001, eventType=null, taskName=Contract Budget Update Review, reassignUserId=null, launchOrgType=Provider, assignedToUserName=CFO DOC, p8UserSession=com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession@19175c0, isFirstLaunch=false, submittedBy=909, entityTypeForAgency=null, budgetAdvanceId=null, taskId=1003, agencySecondaryContactId=null, newFYId=, currentTab=null, finalizeEvaluationDate=null, invoiceId=null, currentTaskStatus=In Review, isTaskScreen=true, submittedByName=test user, provider=R3 dev team organization, userRole=CFO, agencyName=null, workFlowId=8D47B78ECCBFCB46AD7905202C27CC8C, awardAmount=null, taskStatus=Approved, lastModifiedDate=null, agencyPrimaryContactId=null, isAssignableOperation=true}";

			hco.insertLineItemsForContractConfigTask(str);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testInsertOldSubBudgetLineItemsZeroCopy()
	{
		try
		{

			HHSComponentOperations hco = new HHSComponentOperations();

			String str = "{agencyQueryId=null, proposalTitle=null, applicationId=null, assignedTo=agency_21, organizationId=null, entityType=Contract Budget Update Review, procurementId=, agencyId=DOC, firstRoundEvalCompDate=null, amendmentType=null, level=2, reassignUserName=null, userId=agency_21, procurementTitle=Arvind contract second 15 july, procurementEpin=05612L0009, linkedWobNum=null, awardEpin=05612L0009001, contractId=2910, taskActions=Approved, proposalId=, previousTaskStatus=null, baseContractId=null, totalLevel=2, taskAction=null, contractSourceId=, providerComment=, launchCOF=null, class=class com.nyc.hhs.model.TaskDetailsBean, assignedDate=07/15/2013, period=, ct=N/A, providerQueryId=null, startFiscalYear=null, awardSelectionDate=null, internalComment=, isTaskAssigned=true, commentsHistory=null, submittedDate=07/15/2013, eventName=null, subBudgetId=null, comment=null, contractConfWob=null, budgetId=11001, evaluationStatusId=null, organizationName=null, paymentId=null, taskType=taskBudgetUpdate, entityId=11001, eventType=null, taskName=Contract Budget Update Review, reassignUserId=null, launchOrgType=Provider, assignedToUserName=CFO DOC, p8UserSession=com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession@19175c0, isFirstLaunch=false, submittedBy=909, entityTypeForAgency=null, budgetAdvanceId=null, taskId=1003, agencySecondaryContactId=null, newFYId=, currentTab=null, finalizeEvaluationDate=null, invoiceId=null, currentTaskStatus=In Review, isTaskScreen=true, submittedByName=test user, provider=R3 dev team organization, userRole=CFO, agencyName=null, workFlowId=8D47B78ECCBFCB46AD7905202C27CC8C, awardAmount=null, taskStatus=Approved, lastModifiedDate=null, agencyPrimaryContactId=null, isAssignableOperation=true}";

			hco.insertOldSubBudgetLineItemsZeroCopy(str);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testInsertCreateReplicaForBudgetReviewTask()
	{
		try
		{

			HHSComponentOperations hco = new HHSComponentOperations();

			String str = "{agencyQueryId=QUERY_1, proposalTitle=null, applicationId=APP_1, assignedTo=BART, organizationId=null, entityType=null, agencyId=DOCC, firstRoundEvalCompDate=null, procurementId=, amendmentType=TEST, level=1, reassignUserName=null, userId=null, procurementEpin=null, procurementTitle=null, awardEpin=EPIN100000, contractId=111, taskActions=null, proposalId=, previousTaskStatus=null, totalLevel=null, taskAction=null, contractSourceId=1, providerComment=null, class=class com.nyc.hhs.model.TaskDetailsBean, assignedDate=12-12-2012, ct=CT, providerQueryId=null, startFiscalYear=null, internalComment=null, isTaskAssigned=false, commentsHistory=null, submittedDate=null, eventName=null, subBudgetId=null, comment=Test Comment, contractConfWob=21238127398712387, budgetId=111, evaluationStatusId=null, organizationName=null, taskType=null, entityId=null, eventType=null, taskName=null, reassignUserId=null, assignedToUserName=BART.SCHAAP, p8UserSession=null, isFirstLaunch=false, submittedBy=null, entityTypeForAgency=null, taskId=null, agencySecondaryContactId=agencySecondaryContactId, newFYId=null, currentTab=null, invoiceId=null, isTaskScreen=null, provider=null, userRole=null, workFlowId=null, awardAmount=1000000, taskStatus=null, lastModifiedDate=null, agencyPrimaryContactId=agencyPrimaryContactId, isAssignableOperation=false}";

			hco.insertCreateReplicaForBudgetReviewTask(str, "86");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testMergeBudgetForModificationReviewTask()
	{
		try
		{

			HHSComponentOperations hco = new HHSComponentOperations();

			String str = "{agencyQueryId=QUERY_1, proposalTitle=null, applicationId=APP_1, assignedTo=BART, organizationId=null, entityType=null, agencyId=DOCC, firstRoundEvalCompDate=null, procurementId=, amendmentType=TEST, level=1, reassignUserName=null, userId=null, procurementEpin=null, procurementTitle=null, awardEpin=EPIN100000, contractId=111, taskActions=null, proposalId=, previousTaskStatus=null, totalLevel=null, taskAction=null, contractSourceId=1, providerComment=null, class=class com.nyc.hhs.model.TaskDetailsBean, assignedDate=12-12-2012, ct=CT, providerQueryId=null, startFiscalYear=null, internalComment=null, isTaskAssigned=false, commentsHistory=null, submittedDate=null, eventName=null, subBudgetId=null, comment=Test Comment, contractConfWob=21238127398712387, budgetId=111, evaluationStatusId=null, organizationName=null, taskType=null, entityId=null, eventType=null, taskName=null, reassignUserId=null, assignedToUserName=BART.SCHAAP, p8UserSession=null, isFirstLaunch=false, submittedBy=null, entityTypeForAgency=null, taskId=null, agencySecondaryContactId=agencySecondaryContactId, newFYId=null, currentTab=null, invoiceId=null, isTaskScreen=null, provider=null, userRole=null, workFlowId=null, awardAmount=1000000, taskStatus=null, lastModifiedDate=null, agencyPrimaryContactId=agencyPrimaryContactId, isAssignableOperation=false}";

			hco.mergeBudgetForModificationReviewTask(str, "86");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testMergeBudgetForUpdateReviewTask()
	{
		try
		{

			HHSComponentOperations hco = new HHSComponentOperations();

			String str = "{agencyQueryId=null, proposalTitle=null, applicationId=null, assignedTo=agency_21, organizationId=null, entityType=Contract Budget Update Review, procurementId=, agencyId=DOC, firstRoundEvalCompDate=null, amendmentType=null, level=2, reassignUserName=null, userId=agency_21, procurementTitle=Arvind contract second 15 july, procurementEpin=05612L0009, linkedWobNum=null, awardEpin=05612L0009001, contractId=2910, taskActions=Approved, proposalId=, previousTaskStatus=null, baseContractId=null, totalLevel=2, taskAction=null, contractSourceId=, providerComment=, launchCOF=null, class=class com.nyc.hhs.model.TaskDetailsBean, assignedDate=07/15/2013, period=, ct=N/A, providerQueryId=null, startFiscalYear=null, awardSelectionDate=null, internalComment=, isTaskAssigned=true, commentsHistory=null, submittedDate=07/15/2013, eventName=null, subBudgetId=null, comment=null, contractConfWob=null, budgetId=11001, evaluationStatusId=null, organizationName=null, paymentId=null, taskType=taskBudgetUpdate, entityId=11001, eventType=null, taskName=Contract Budget Update Review, reassignUserId=null, launchOrgType=Provider, assignedToUserName=CFO DOC, p8UserSession=com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession@19175c0, isFirstLaunch=false, submittedBy=909, entityTypeForAgency=null, budgetAdvanceId=null, taskId=1003, agencySecondaryContactId=null, newFYId=, currentTab=null, finalizeEvaluationDate=null, invoiceId=null, currentTaskStatus=In Review, isTaskScreen=true, submittedByName=test user, provider=R3 dev team organization, userRole=CFO, agencyName=null, workFlowId=8D47B78ECCBFCB46AD7905202C27CC8C, awardAmount=null, taskStatus=Approved, lastModifiedDate=null, agencyPrimaryContactId=null, isAssignableOperation=true}";

			hco.mergeBudgetForUpdateReviewTask(str, "86");

		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testMergeBudgetForAmendmentReviewTask()
	{
		try
		{

			HHSComponentOperations hco = new HHSComponentOperations();

			String str = "{agencyQueryId=QUERY_1, proposalTitle=null, applicationId=APP_1, assignedTo=BART, organizationId=null, entityType=null, agencyId=DOCC, firstRoundEvalCompDate=null, procurementId=, amendmentType=TEST, level=1, reassignUserName=null, userId=null, procurementEpin=null, procurementTitle=null, awardEpin=EPIN100000, contractId=111, taskActions=null, proposalId=, previousTaskStatus=null, totalLevel=null, taskAction=null, contractSourceId=1, providerComment=null, class=class com.nyc.hhs.model.TaskDetailsBean, assignedDate=12-12-2012, ct=CT, providerQueryId=null, startFiscalYear=null, internalComment=null, isTaskAssigned=false, commentsHistory=null, submittedDate=null, eventName=null, subBudgetId=null, comment=Test Comment, contractConfWob=21238127398712387, budgetId=111, evaluationStatusId=null, organizationName=null, taskType=null, entityId=null, eventType=null, taskName=null, reassignUserId=null, assignedToUserName=BART.SCHAAP, p8UserSession=null, isFirstLaunch=false, submittedBy=null, entityTypeForAgency=null, taskId=null, agencySecondaryContactId=agencySecondaryContactId, newFYId=null, currentTab=null, invoiceId=null, isTaskScreen=null, provider=null, userRole=null, workFlowId=null, awardAmount=1000000, taskStatus=null, lastModifiedDate=null, agencyPrimaryContactId=agencyPrimaryContactId, isAssignableOperation=false}";

			hco.mergeBudgetForAmendmentReviewTask(str, "86", 1);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testbuildTaskDetailsBean()
	{
		try
		{

			BudgetOperation loBudgetOperations = new BudgetOperation();
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();

			String str = "{agencyQueryId=null,proposalTitle=null,applicationId=null,assignedTo=agency_244,organizationId=null,entityType=Contract Budget Review,agencyId=ACS,firstRoundEvalCompDate=null,procurementId=,amendmentType=null,level=2,reassignUserName=null,userId=agency_244,procurementEpin=07112L0026001,procurementTitle=DK3_Swapnil_Do Not Use,linkedWobNum=null,awardEpin=07112L0026001,contractId=1273,taskActions=Approved,proposalId=,previousTaskStatus=null,totalLevel=2,taskAction=null,contractSourceId=,providerComment=,launchCOF=null,class=class com.nyc.hhs.model.TaskDetailsBean,assignedDate=06/26/2013,ct=N/A,providerQueryId=null,startFiscalYear=null,internalComment=,isTaskAssigned=true,commentsHistory=null,submittedDate=06/26/2013,eventName=null,subBudgetId=null,comment=null,contractConfWob=null,budgetId=10503,evaluationStatusId=null,organizationName=null,paymentId=null,taskType=taskContractBudgetReview,entityId=10503,eventType=null,taskName=Contract Budget Review,reassignUserId=null,launchOrgType=Provider,assignedToUserName=ACS PROGRAMSTAFF,p8UserSession=com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession@2f50f66c,isFirstLaunch=false,submittedBy=3065,entityTypeForAgency=null,budgetAdvanceId=null,taskId=1002,agencySecondaryContactId=null,newFYId=null,currentTab=null,invoiceId=null,isTaskScreen=true,submittedByName=DK Three,provider=DK Provider 3,userRole=PROGRAM_STAFF,agencyName=null,workFlowId=0F5D7393271B5D458F805D2CCADB7E09,awardAmount=null,taskStatus=Approved,lastModifiedDate=null,agencyPrimaryContactId=null,isAssignableOperation=true}";
			loTaskDetailsBean = loBudgetOperations.buildTaskDetailsBean(str);
			assertNotNull(loTaskDetailsBean);

		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testcontractConInfo()
	{
		try
		{
			new FinancialWFBean();
			LogInfo LOG_OBJECT = new LogInfo(HHSComponentOperations.class);
			new HHSComponentOperations();
			WorkflowOperations loWorkFlowOperations = new WorkflowOperations(LOG_OBJECT);

			loWorkFlowOperations.contractConInfo("660");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testCancelAwardProcess()
	{
		try
		{
			String asContractId = "1";
			String asOrgId = "1";
			String asProcurementId = "283";
			String asAwardId = "1";
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();

			loHHSComponentOperations.cancelAwardProcess(asContractId, asOrgId, asProcurementId, asAwardId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testCancelProcurement()
	{
		try
		{

			String asProcurementId = "624";
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();

			loHHSComponentOperations.cancelProcurement(asProcurementId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testCancelEvaluationTasks()
	{
		try
		{

			String asProcurementId = "1411";
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();

			loHHSComponentOperations.cancelEvaluationTasks(asProcurementId);
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testSuspendAlllFinancialTasks()
	{
		try
		{

			String asContractId = "3441";
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();

			loHHSComponentOperations.suspendAlllFinancialTasks(asContractId, "city_43");
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testUnsuspendAllFinancialTasks()
	{
		try
		{

			String asContractId = "3441";
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();

			loHHSComponentOperations.unSuspendAllFinancialTasks(asContractId, "city_43");
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testFetchAcceleratorComments()
	{
		try
		{
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();
			loHHSComponentOperations.fetchAcceleratorComments("Invoice Review", "383");
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testFetchAgencyComments()
	{
		try
		{
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();
			loHHSComponentOperations.fetchAgencyComments("Invoice Review", "383");
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testFetchEvaluatorIdsEvaluationWorkFlow()
	{
		try
		{
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();
			loHHSComponentOperations.fetchEvaluatorIdsEvaluationWorkFlow("1774");
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testFetchEvaluatorIdsReviewScoreWorkFlow()
	{
		try
		{
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();
			loHHSComponentOperations.fetchEvaluatorIdsReviewScoreWorkFlow("764");
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testInsertIntoPaymentforWF305()
	{
		try
		{
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();
			loHHSComponentOperations.insertIntoPayment("CT106820121435091", "479", "2746",
					"10848", "DOC", "r3_org", "909", "", "");
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testInsertIntoPaymentforWF307()
	{
		try
		{
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();
			loHHSComponentOperations.insertIntoPayment("CT106820121435091", "", "2383", "10848",
					"DOC", "r3_org", "909", "", "94");
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testInsertIntoPaymentAllocation()
	{
		try
		{
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();
			String[] paymentIds =
			{ "259", "258", "254", "256", "255", "257", "253", "252", "249", "248", "251", "250" };
			loHHSComponentOperations.insertIntoPaymentAllocation("test", paymentIds);
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testSetProcessFlagOnPayment()
	{
		try
		{
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();
			loHHSComponentOperations.setProcessFlagOnPayment("374");
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testFetchBudgetId()
	{
		try
		{
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();
			loHHSComponentOperations.fetchBudgetId("1665", "2013");
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testFetchBudgetIdfromTaskDetailsBean()
	{
		try
		{
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();
			loHHSComponentOperations
					.fetchBudgetIdandCTfromTaskDetailsBean("{agencyQueryId=null,proposalTitle=null,applicationId=null,assignedTo=agency_244,organizationId=null,entityType=Contract Budget Review,agencyId=ACS,firstRoundEvalCompDate=null,procurementId=,amendmentType=null,level=2,reassignUserName=null,userId=agency_244,procurementEpin=07112L0026001,procurementTitle=DK3_Swapnil_Do Not Use,linkedWobNum=null,awardEpin=07112L0026001,contractId=1273,taskActions=Approved,proposalId=,previousTaskStatus=null,totalLevel=2,taskAction=null,contractSourceId=,providerComment=,launchCOF=null,class=class com.nyc.hhs.model.TaskDetailsBean,assignedDate=06/26/2013,ct=N/A,providerQueryId=null,startFiscalYear=null,internalComment=,isTaskAssigned=true,commentsHistory=null,submittedDate=06/26/2013,eventName=null,subBudgetId=null,comment=null,contractConfWob=null,budgetId=10503,evaluationStatusId=null,organizationName=null,paymentId=null,taskType=taskContractBudgetReview,entityId=10503,eventType=null,taskName=Contract Budget Review,reassignUserId=null,launchOrgType=Provider,assignedToUserName=ACS PROGRAMSTAFF,p8UserSession=com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession@2f50f66c,isFirstLaunch=false,submittedBy=3065,entityTypeForAgency=null,budgetAdvanceId=null,taskId=1002,agencySecondaryContactId=null,newFYId=null,currentTab=null,invoiceId=null,isTaskScreen=true,submittedByName=DK Three,provider=DK Provider 3,userRole=PROGRAM_STAFF,agencyName=null,workFlowId=0F5D7393271B5D458F805D2CCADB7E09,awardAmount=null,taskStatus=Approved,lastModifiedDate=null,agencyPrimaryContactId=null,isAssignableOperation=true}");
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testFetchInvoiceNumber()
	{
		try
		{
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();
			loHHSComponentOperations.fetchInvoiceNumber("395");
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testFetchAdvanceNumber()
	{
		try
		{
			HHSComponentOperations loHHSComponentOperations = new HHSComponentOperations();
			loHHSComponentOperations.fetchAdvanceNumber("130600001");
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	public void testSetCancelParameterOnAwardWorkflow()
	{
		try
		{
			VWSession loVWSession = null;
			P8SecurityOperations loFilenetConnection = new P8SecurityOperations();
			loVWSession = loFilenetConnection.getPESession(loFilenetConnection.setP8SessionVariables());
			String asProcurementId = "sa1";
			String filter = HHSP8Constants.PROPERTY_PE_PROCURMENT_ID + "='" + asProcurementId + "'";
			WorkflowOperations loWorkfLowOperations = new WorkflowOperations(new LogInfo(getClass()));
			loWorkfLowOperations.setCancelParameterOnAwardWorkflow(loVWSession, filter, true);
			assertTrue(true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			assertTrue(false);
		}
	}

	/*
	 * @Test public void testStartProposalRelatedWorkflows() { try {
	 * 
	 * HHSComponentOperations hco = new HHSComponentOperations(); Boolean result
	 * = hco.startProposalRelatedWorkflows(null, null, null);
	 * assertNotNull(result); assertTrue(result);
	 * 
	 * } catch (Exception e) { e.printStackTrace(); assertTrue(false); } }
	 */

	/*
	 * @Test public void testFetchAcceptedProposalID() { try { String taskType =
	 * "Invoice Review"; String agencyID = "ACS"; String[] users = new
	 * String[]{"city_142","city_143"};
	 * 
	 * HHSComponentOperations hco = new HHSComponentOperations();
	 * 
	 * boolean status = hco.fetchAcceptedProposalID(taskType, agencyID, users);
	 * ("("+status+")"); assertTrue(status); } catch (Exception e) {
	 * e.printStackTrace(); assertTrue(false); } }
	 */

	/*
	 * @Test public void testPlay(){ boolean succes = false; try {
	 * SimpleDateFormat formatter = new
	 * SimpleDateFormat("dd-MMM-yyyy hh:mm:ss aaa"); String systemTime =
	 * formatter.format(new Date()); (systemTime); succes = true;
	 * 
	 * assertTrue(succes); } catch (Exception e) { e.printStackTrace();
	 * assertTrue(false); } }
	 */
}
