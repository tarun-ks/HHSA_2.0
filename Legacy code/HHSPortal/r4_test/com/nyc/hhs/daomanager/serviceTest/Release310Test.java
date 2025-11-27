package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.batch.bulkupload.BulkUploadBatch;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.daomanager.service.CompetitionPoolService;
import com.nyc.hhs.daomanager.service.ConfigurationService;
import com.nyc.hhs.daomanager.service.HhsAuditService;
import com.nyc.hhs.daomanager.service.PaymentModuleService;
import com.nyc.hhs.daomanager.service.ProcurementService;
import com.nyc.hhs.daomanager.service.ProposalService;
import com.nyc.hhs.daomanager.service.RFPReleaseService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.junit.util.JUnitUtil;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.EvaluationCriteriaBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.ProposalQuestionAnswerBean;
import com.nyc.hhs.model.RFPReleaseBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.taskhandlers.AdvancePaymentReviewTaskHandler;
import com.nyc.hhs.taskhandlers.PaymentReviewTaskHandler;
import com.nyc.hhs.taskhandlersTest.AdvancePaymentReviewTaskHandlerTest;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

public class Release310Test
{
	private static SqlSession moSession = null; // SQL Session
	private RFPReleaseService loRFPReleaseService = new RFPReleaseService();
	private HhsAuditService loHhsAuditService = new HhsAuditService();
	private CompetitionPoolService loCompetitionPoolService = new CompetitionPoolService();
	private ProcurementService moProcurementService = new ProcurementService();
	private ProposalService moProposalService = new ProposalService();
	private String msProcId1 = "190";
	private String msProcId2 = "135";
	private String msProcId3 = "132";
	private String msProcId4 = "171";
	private String msProcId5 = "3292";
	private String msProcIdInvalid = "#@SD$%";
	private String msUserId = "city_42";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		JUnitUtil.getTransactionManager();
		loadTaskAuditXml();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession.rollback();
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	public P8UserSession getFileNetSession1() throws ApplicationException
	{
		System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
		System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL, PropertyLoader.getProperty(
				P8Constants.PROPERTY_FILE, P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
		System.setProperty(P8Constants.PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI,
				PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));

		P8UserSession loUserSession = new P8UserSession();
		loUserSession.setContentEngineUri(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
		loUserSession.setObjectStoreName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "OBJECT_STORE_NAME"));
		loUserSession.setIsolatedRegionName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
				"CONNECTION_POINT_NAME"));
		loUserSession.setUserId("ceadmin");
		loUserSession.setPassword("Filenet1");
		loUserSession.setIsolatedRegionNumber("3");
		P8SecurityOperations loP8SecurityService = new P8SecurityOperations();
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
		// loP8SecurityService.getPESession(loUserSession);
		// loP8SecurityService.getObjectStore(loUserSession);

		return loUserSession;
	}

	private static void loadTaskAuditXml() throws ApplicationException
	{
		try
		{
			ICacheManager loCacheManager = BaseCacheManagerWeb.getInstance();
			AdvancePaymentReviewTaskHandlerTest loAdvancePaymentReviewTaskHandlerTest = new AdvancePaymentReviewTaskHandlerTest();
			Object loCacheNotificationObject3 = XMLUtil.getDomObj(loAdvancePaymentReviewTaskHandlerTest.getClass()
					.getResourceAsStream(HHSConstants.BULK_UPLOAD_TASK_AUDIT_ELEMENT_PATH));
			loCacheManager.putCacheObject(HHSConstants.TASK_AUDIT_CONFIGURATION, loCacheNotificationObject3);
			synchronized (BulkUploadBatch.class)
			{
				loCacheManager.putCacheObject(ApplicationConstants.APPLICATION_SETTING,
						HHSUtil.getApplicationSettingsBulk());
			}
		}
		catch (Exception loEx)
		{
			// LOG_OBJECT.Error("Error Occured while loading Transaction Xml: loadTaskAuditXml",
			// loEx);
			throw new ApplicationException("Error Occured while loading Transaction Xml", loEx);
		}
	}

	PaymentModuleService moPaymentModuleService = new PaymentModuleService();
	String lsPaymentId = "525";
	String lsContractId = "3431";
	String lsBudgetId = "11286";
	String lsBudgetAdvanceId = "316";
	String lsInvoiceId = "583";
	String lsAssignmentId = "507";

	String lsAssignmentId2 = "648";
	String lsBudgetAdvanceId2 = "395";
	String lsBudgetId2 = "11287";

	ConfigurationService moConfigurationService = new ConfigurationService();
	@Test
	public void testDeleteForCancelConfigureNewFY() throws ApplicationException
	{
		ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
		aoContractBudgetBean.setContractId("121");
		aoContractBudgetBean.setBudgetfiscalYear("2016");
		moConfigurationService.deleteForCancelConfigureNewFY(moSession, aoContractBudgetBean);
	}
	private HashMap paramMap()
	{
		HashMap aoHMNotifyParam = new HashMap();
		HashMap aoHashMap = new HashMap();
		aoHMNotifyParam.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, aoHashMap);
		return aoHMNotifyParam;
	}

	@Test
	public void testSetAdvanceStatusForReviewTask() throws ApplicationException, Exception

	{
		Boolean loFinalFinish = true;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(lsContractId);
		loTaskDetailsBean.setBudgetId(lsBudgetId);
		loTaskDetailsBean.setBudgetAdvanceId(lsBudgetAdvanceId);
		loTaskDetailsBean.setUserId("agency_21");
		String lsBudgetStatus = "92";
		moPaymentModuleService.setAdvanceStatusForReviewTask(moSession, loFinalFinish, loTaskDetailsBean,
				lsBudgetStatus);
		assertEquals("Transaction Success:: PaymentModuleService:setAdvanceStatusForReviewTask"
				+ " method - success to update record " + " \n", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testSetAdvanceStatusForReviewTask1() throws ApplicationException, Exception

	{
		Boolean loFinalFinish = false;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(lsContractId);
		loTaskDetailsBean.setBudgetId(lsBudgetId);
		loTaskDetailsBean.setBudgetAdvanceId(lsBudgetAdvanceId);
		loTaskDetailsBean.setUserId("agency_21");
		String lsBudgetStatus = "92";
		moPaymentModuleService.setAdvanceStatusForReviewTask(moSession, loFinalFinish, loTaskDetailsBean,
				lsBudgetStatus);
		assertEquals("", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testSetAdvanceStatusForReviewTaskException() throws ApplicationException, Exception
	{
		Boolean lbThrown = false;
		try
		{
			Boolean loFinalFinish = true;
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setContractId(lsContractId);
			loTaskDetailsBean.setBudgetId(lsBudgetId);
			loTaskDetailsBean.setBudgetAdvanceId(lsBudgetAdvanceId);
			loTaskDetailsBean.setUserId("agency_21");
			String lsBudgetStatus = "92";
			moPaymentModuleService
					.setAdvanceStatusForReviewTask(null, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testDeletePaymentRecords() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = true;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(lsContractId);
		loTaskDetailsBean.setBudgetId(lsBudgetId);
		loTaskDetailsBean.setBudgetAdvanceId(lsBudgetAdvanceId);
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setPeriod("12");
		String lsBudgetStatus = "92";
		loTaskDetailsBean.setTaskSource(HHSConstants.BATCH);
		moPaymentModuleService.deletePaymentRecords(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus,
				paramMap());
		assertEquals("Transaction Success:: PaymentModuleService:deletePaymentRecords"
				+ " method - success to update record " + " \n", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testDeletePaymentRecords1() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = true;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(lsContractId);
		loTaskDetailsBean.setBudgetId(lsBudgetId);
		loTaskDetailsBean.setInvoiceId(lsInvoiceId);
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setPeriod("12");
		String lsBudgetStatus = "92";
		moPaymentModuleService.deletePaymentRecords(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus,
				paramMap());
		assertEquals("Transaction Success:: PaymentModuleService:deletePaymentRecords"
				+ " method - success to update record " + " \n", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testDeletePaymentRecords2() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = false;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(lsContractId);
		loTaskDetailsBean.setBudgetId(lsBudgetId);
		loTaskDetailsBean.setInvoiceId(lsInvoiceId);
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setPeriod("12");
		String lsBudgetStatus = "92";
		moPaymentModuleService.deletePaymentRecords(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus,
				paramMap());
		assertEquals("", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testDeletePaymentRecordsException() throws ApplicationException, Exception
	{
		Boolean lbThrown = false;
		try
		{
			Boolean loFinalFinish = true;
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setContractId(lsContractId);
			loTaskDetailsBean.setBudgetId(lsBudgetId);
			loTaskDetailsBean.setBudgetAdvanceId(lsBudgetAdvanceId);
			loTaskDetailsBean.setUserId("agency_21");
			loTaskDetailsBean.setPeriod("12");
			String lsBudgetStatus = "92";
			moPaymentModuleService.deletePaymentRecords(null, loFinalFinish, loTaskDetailsBean, lsBudgetStatus,
					paramMap());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testSetInvoiceStatus() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = true;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(lsContractId);
		loTaskDetailsBean.setBudgetId(lsBudgetId);
		loTaskDetailsBean.setInvoiceId(lsInvoiceId);
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setPeriod("12");
		String lsBudgetStatus = "70";
		moPaymentModuleService.setInvoiceStatus(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		assertEquals("Transaction Success:: PaymentModuleService:setInvoiceStatus"
				+ " method - success to update record " + " \n", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testSetInvoiceStatus2() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = false;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(lsContractId);
		loTaskDetailsBean.setBudgetId(lsBudgetId);
		loTaskDetailsBean.setInvoiceId(lsInvoiceId);
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setPeriod("12");
		String lsBudgetStatus = "70";
		moPaymentModuleService.setInvoiceStatus(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		assertEquals("", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testSetInvoiceStatusException() throws ApplicationException, Exception
	{
		Boolean lbThrown = false;
		try
		{
			Boolean loFinalFinish = true;
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setContractId(lsContractId);
			loTaskDetailsBean.setBudgetId(lsBudgetId);
			loTaskDetailsBean.setInvoiceId(lsInvoiceId);
			loTaskDetailsBean.setUserId("agency_21");
			loTaskDetailsBean.setPeriod("12");
			String lsBudgetStatus = "70";
			moPaymentModuleService.setInvoiceStatus(null, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	String lsContractIdReturn = "3431";

	String lsBudgetAdvanceIdReturn = "6571";
	String lsBudgetIdReturn = "11286";
	String lsWorkflowIdReturn = "139DB94E9B4FC642B0E9A85F2AFFDEAB";

	AdvancePaymentReviewTaskHandler loAdvancePaymentReviewTaskHandler = new AdvancePaymentReviewTaskHandler();

	@Test
	public void testAdvancePaymentReviewTaskReturn() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setLevel(HHSConstants.ONE);
		loTaskDetailsBean.setInternalComment("AAAAAAAAAAAAAAA");
		loTaskDetailsBean.setEntityType("1");
		loTaskDetailsBean.setEntityId("1");
		loTaskDetailsBean.setUserId("803");
		loTaskDetailsBean.setTotalLevel(HHSConstants.THREE);
		loTaskDetailsBean.setContractId(lsContractIdReturn);
		loTaskDetailsBean.setBudgetId(lsBudgetIdReturn);
		loTaskDetailsBean.setBudgetAdvanceId(lsBudgetAdvanceIdReturn);
		loTaskDetailsBean.setAgencyId("agency_47");
		loTaskDetailsBean.setTaskStatus("Pending Approval");
		loTaskDetailsBean.setWorkFlowId(lsWorkflowIdReturn);
		loTaskDetailsBean.setTaskName(HHSConstants.TASK_CONTRACT_CONFIGURATION);
		loTaskDetailsBean.setP8UserSession(getFileNetSession1());
		loAdvancePaymentReviewTaskHandler.taskReturn(loTaskDetailsBean);
		assertTrue(true);
	}

	@Test(expected = ApplicationException.class)
	public void testAdvancePaymentReviewTaskReturnAppExp() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loAdvancePaymentReviewTaskHandler.taskReturn(loTaskDetailsBean);

	}

	@Test(expected = Exception.class)
	public void testAdvancePaymentReviewTaskReturnExp() throws ApplicationException
	{
		loAdvancePaymentReviewTaskHandler.taskReturn(null);

	}

	String lsContractIdReturnPayment = "5202";

	String lsInvoiceIdReturnPayment = "825";
	String lsBudgetIdReturnPayment = "12455";
	String lsWorkflowIdReturnPayment = "C569884FCF6E8F458AAE00B9FD5F0D96";

	PaymentReviewTaskHandler loPaymentReviewTaskHandler = new PaymentReviewTaskHandler();

	@Test
	public void testPaymentReviewTaskReturn() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setLevel(HHSConstants.ONE);
		loTaskDetailsBean.setInternalComment("AAAAAAAAAAAAAAA");
		loTaskDetailsBean.setEntityType("1");
		loTaskDetailsBean.setEntityId("1");
		loTaskDetailsBean.setUserId("803");
		loTaskDetailsBean.setTotalLevel(HHSConstants.THREE);
		loTaskDetailsBean.setContractId(lsContractIdReturnPayment);
		loTaskDetailsBean.setBudgetId(lsBudgetIdReturnPayment);
		//loTaskDetailsBean.setBudgetAdvanceId(lsBudgetAdvanceIdReturn);
		loTaskDetailsBean.setInvoiceId(lsInvoiceIdReturnPayment);
		loTaskDetailsBean.setAgencyId("agency_47");
		loTaskDetailsBean.setTaskStatus("Pending Approval");
		loTaskDetailsBean.setWorkFlowId(lsWorkflowIdReturnPayment);
		loTaskDetailsBean.setTaskName(HHSConstants.TASK_CONTRACT_CONFIGURATION);
		loTaskDetailsBean.setP8UserSession(getFileNetSession1());
		loPaymentReviewTaskHandler.taskReturn(loTaskDetailsBean);
		assertTrue(true);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentReviewTaskReturnAppExp() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loPaymentReviewTaskHandler.taskReturn(loTaskDetailsBean);
	}

	@Test(expected = Exception.class)
	public void testPaymentReviewTaskReturnExp() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setP8UserSession(null);
		loPaymentReviewTaskHandler.taskReturn(loTaskDetailsBean);
	}

	/**
	 * The method fetchEvaluationCriteria tests fetching the evaluation criteria
	 * details from the database.
	 * @throws Exception
	 */
	@Test
	public void testFetchEvaluationCriteria1() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = (RFPReleaseBean) loRFPReleaseService.fetchEvaluationCriteria(moSession,
				"2301", "3");
		assertNull(loRFPReleaseBean);
	}

	/**
	 * The method fetchEvaluationCriteria tests fetching the evaluation criteria
	 * details from the database.
	 * @throws Exception
	 */
	@Test
	public void testFetchEvaluationCriteriaCase2() throws ApplicationException
	{
		loRFPReleaseService.fetchEvaluationCriteria(moSession, "2301", "");
	}

	/**
	 * The method fetchEvaluationCriteria tests fetching the evaluation criteria
	 * details from the database.
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchEvaluationCriteriaCase3() throws Exception
	{
		loRFPReleaseService.fetchEvaluationCriteria(null, null, null);
	}

	/**
	 * This method test the data movement from procurement_addendum_document to
	 * procurement_document_config table corresponding to the procurement Id in
	 * case it is null.
	 * @throws ApplicationException If an Application Exception occurs
	 */

	@Test
	public void testUpdateProcDocumentConfigCase1() throws ApplicationException
	{
		Boolean lbInsertSuccessful = loRFPReleaseService.updateProcDocumentConfig(moSession, null, Boolean.FALSE);
		assertTrue(!lbInsertSuccessful);
		assertNotNull(lbInsertSuccessful);
	}

	/**
	 * This method test the data movement from procurement_addendum_document to
	 * procurement_document_config table corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProcDocumentConfigCase2() throws ApplicationException
	{
		Boolean lbInsertSuccessful = loRFPReleaseService.updateProcDocumentConfig(moSession, "124", Boolean.FALSE);
		assertTrue(!lbInsertSuccessful);
		assertNotNull(lbInsertSuccessful);
	}

	/**
	 * This method test the negative execution of data movement from
	 * procurement_addendum_document to procurement_document_config table
	 * corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProcDocumentConfigApplicationException() throws ApplicationException
	{
		Boolean lbInsertSuccessful = loRFPReleaseService.updateProcDocumentConfig(null, null, Boolean.TRUE);
		assertTrue(lbInsertSuccessful);
		assertNotNull(lbInsertSuccessful);
	}

	@Test
	public void testUpdateProcQuestionConfigCase1() throws ApplicationException
	{
		Boolean lbInsertProcQuestion = loRFPReleaseService.updateProcQuestionConfig(moSession, null, Boolean.FALSE,
				"city_459");
		assertTrue(!lbInsertProcQuestion);
		assertNotNull(lbInsertProcQuestion);
	}

	/**
	 * This method tests the data movement from addendum_question_config to
	 * procurement_question_config table corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProcQuestionConfigCase2() throws ApplicationException
	{
		Boolean lbInsertProcQuestion = loRFPReleaseService.updateProcQuestionConfig(moSession, "124", Boolean.FALSE,
				"city_459");
		assertTrue(!lbInsertProcQuestion);
		assertNotNull(lbInsertProcQuestion);
	}

	/**
	 * This method tests the negative execution of data movement from
	 * addendum_question_config to procurement_question_config table
	 * corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProcQuestionConfigCase4() throws ApplicationException
	{
		loRFPReleaseService.updateProcQuestionConfig(null, null, Boolean.TRUE, null);
	}

	/**
	 * This method tests the negative execution of data movement from
	 * addendum_evaluation_criteria to evaluation_criteria table corresponding
	 * to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateEvaluationCriteriaCase1() throws ApplicationException
	{
		loRFPReleaseService.updateEvaluationCriteria(null, null, Boolean.TRUE);
	}

	/**
	 * This method tests the data movement from addendum_evaluation_criteria to
	 * evaluation_criteria table corresponding to the procurement Id
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateEvaluationCriteriaCase2() throws ApplicationException
	{
		Boolean lbInsertEvalCriteria = loRFPReleaseService.updateEvaluationCriteria(moSession, "124", Boolean.FALSE);
		assertTrue(!lbInsertEvalCriteria);
		assertNotNull(lbInsertEvalCriteria);
	}

	@Test
	public void testUpdateEvaluationCriteriaCase3() throws ApplicationException
	{
		Boolean lbInsertEvalCriteria = loRFPReleaseService.updateEvaluationCriteria(moSession, null, Boolean.FALSE);
		assertTrue(!lbInsertEvalCriteria);
		assertNotNull(lbInsertEvalCriteria);
	}

	/**
	 * This method tests saving the evaluation criteria details from the
	 * database.
	 * @throws Exception
	 */
	@Test
	public void saveEvaluationCriteriaCase2() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = getRFPReleaseBean(2);
		Boolean lbActualResult = (Boolean) loRFPReleaseService.saveEvaluationCriteria(moSession, "", loRFPReleaseBean);
		Boolean liExpectedResult = Boolean.TRUE;
		assertEquals(liExpectedResult, lbActualResult);
	}

	/**
	 * This method tests saving the evaluation criteria details from the
	 * database.
	 * @throws Exception
	 */
	@Test
	public void saveEvaluationCriteriaCase3() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = getRFPReleaseBean(3);
		Boolean lbActualResult = (Boolean) loRFPReleaseService.saveEvaluationCriteria(moSession, "", loRFPReleaseBean);
		Boolean liExpectedResult = Boolean.TRUE;
		assertEquals(liExpectedResult, lbActualResult);
	}

	/**
	 * This method tests whether evaluations are in progress for a procurement.
	 * @throws Exception
	 */
	@Test
	public void testCheckEvaluationInProgress() throws Exception
	{
		Boolean lbActualResult = (Boolean) loRFPReleaseService.checkEvaluationInProgress(moSession, msProcId5);
		Boolean liExpectedResult = Boolean.FALSE;
		assertEquals(liExpectedResult, lbActualResult);
	}
	
	/**
	 * This method tests whether evaluations are in progress for a procurement.
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testCheckEvaluationInProgressException() throws Exception
	{
		Boolean lbActualResult = (Boolean) loRFPReleaseService.checkEvaluationInProgress(null, msProcId5);
		Boolean liExpectedResult = Boolean.FALSE;
		assertEquals(liExpectedResult, lbActualResult);
	}
	
	/**
	 * This method tests getting exception while saving the evaluation criteria
	 * details in the database.
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void saveEvaluationCriteriaException7() throws Exception
	{
		RFPReleaseBean loRFPReleaseBean = getRFPReleaseBean(2);
		loRFPReleaseBean.setProcurementId("251898");
		loRFPReleaseBean.setLoEvaluationCriteriaBeanList(null);
		loRFPReleaseService.saveEvaluationCriteria(moSession, "", loRFPReleaseBean);
	}

	/**
	 * this method sets all the required data in the bean.
	 * @return RFPReleaseBean - RFPReleaseBean reference
	 * @throws Exception - throws Exception
	 */
	public RFPReleaseBean getRFPReleaseBean(Integer liCaseCheck)
	{
		RFPReleaseBean loRfpReleaseBean = new RFPReleaseBean();
		List<EvaluationCriteriaBean> loEvaluationCriteriaBeanList = new ArrayList<EvaluationCriteriaBean>();
		if (liCaseCheck == 1)
		{
			loRfpReleaseBean.setProcurementId(msProcId1);
		}
		else if (liCaseCheck == 2)
		{
			loRfpReleaseBean.setProcurementId(msProcId2);
		}
		else if (liCaseCheck == 3)
		{
			loRfpReleaseBean.setProcurementId(msProcId3);
		}
		else if (liCaseCheck == 100)
		{
			loRfpReleaseBean.setProcurementId(msProcId3);
		}
		else if (liCaseCheck == 101)
		{
			loRfpReleaseBean.setProcurementId(msProcId2);
		}
		else
		{
			loRfpReleaseBean.setProcurementId("239");
		}
		loRfpReleaseBean.setCreatedByUserId(msUserId);
		loRfpReleaseBean.setModifiedByUserId(msUserId);

		if (liCaseCheck == 100 || liCaseCheck == 101)
		{
			EvaluationCriteriaBean evaluationCriteriaBean = new EvaluationCriteriaBean();
			evaluationCriteriaBean.setScoreCriteria(null);
			evaluationCriteriaBean.setScoreSeqNumber(90);
			loEvaluationCriteriaBeanList.add(evaluationCriteriaBean);
		}
		else
		{
			loEvaluationCriteriaBeanList = getEvaluationCriteriaBeanList(liCaseCheck);
		}
		loRfpReleaseBean.setLoEvaluationCriteriaBeanList(loEvaluationCriteriaBeanList);

		return loRfpReleaseBean;
	}

	/**
	 * this method sets all the required data in the bean.
	 * @return List<EvaluationCriteriaBean> - EvaluationCriteriaBean list
	 */
	private List<EvaluationCriteriaBean> getEvaluationCriteriaBeanList(Integer liCaseCheck)
	{
		List<EvaluationCriteriaBean> loEvaluationCriteriaBeanList = new ArrayList<EvaluationCriteriaBean>();
		EvaluationCriteriaBean loEvaluationCriteriaBean = new EvaluationCriteriaBean();
		for (int liCounter = 0; liCounter < 10; liCounter++)
		{
			if (liCounter % 2 == 0)
			{

				loEvaluationCriteriaBean.setMaximumScore(liCounter + 5);
				if (liCaseCheck == 1 || liCaseCheck == 5 || liCaseCheck == 6)
				{
					loEvaluationCriteriaBean.setScoreCriteria("Score" + (liCounter + 1));
					loEvaluationCriteriaBean.setScoreSeqNumber(liCounter + 1);
				}
				else if (liCaseCheck == 2)
				{
					loEvaluationCriteriaBean.setScoreSeqNumber(liCounter + 1);
					loEvaluationCriteriaBean.setScoreCriteria("Scoreaaaaa" + (liCounter + 1));
				}
				else if (liCaseCheck == 3)
				{
					loEvaluationCriteriaBean.setScoreSeqNumber(0);
					loEvaluationCriteriaBean.setScoreCriteria("");
				}
				else if (liCaseCheck == 4)
				{
					loEvaluationCriteriaBean.setScoreSeqNumber(liCounter + 1);
					loEvaluationCriteriaBean.setScoreCriteria("");
				}

				loEvaluationCriteriaBean.setScoreFlag("Y");

			}
			else
			{
				loEvaluationCriteriaBean.setMaximumScore(liCounter + 5);
				loEvaluationCriteriaBean.setScoreCriteria("Score" + (liCounter + 1));
				loEvaluationCriteriaBean.setScoreFlag("N");
				loEvaluationCriteriaBean.setScoreSeqNumber(liCounter + 1);
			}
			loEvaluationCriteriaBeanList.add(loEvaluationCriteriaBean);
			loEvaluationCriteriaBean = new EvaluationCriteriaBean();
		}
		return loEvaluationCriteriaBeanList;
	}

	@Test
	public void testUpdateEvalGroupWithVersionInfo() throws ApplicationException
	{
		Map<String, Object> loDataMap = new HashMap<String, Object>();
		loDataMap.put("asProcurementId", "3244");
		loDataMap.put("evaluationGroupId", "403");
		Boolean lbInsertEvalCriteria = loCompetitionPoolService.updateEvalGroupWithVersionInfo(moSession, loDataMap);
		assertNotNull(lbInsertEvalCriteria);
	}

	/**
	 * This method tests if proposal custom questions are available for a
	 * procurement
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProposalCustomQuestions() throws ApplicationException
	{

		List<ProposalQuestionAnswerBean> loPropCustomQuestionList = moProcurementService.fetchProposalCustomQuestions(
				moSession, "624", "12");
		assertNotNull(loPropCustomQuestionList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProposalCustomQuestionsCase1() throws ApplicationException
	{

		moProcurementService.fetchProposalCustomQuestions(null, "624", null);
	}

	/**
	 * This method tests if proposal document types are available for a
	 * procurement
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchProposalDocumentTypes() throws ApplicationException
	{

		List<ExtendedDocument> loPropDocumentTypeList = moProcurementService.fetchProposalDocumentType(moSession,
				"624", "3");
		assertNotNull(loPropDocumentTypeList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProposalDocumentType() throws ApplicationException
	{

		moProcurementService.fetchProposalDocumentType(null, "624", null);
	}

	/**
	 * This method tests saveProposalCustomQuestions
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalCustomQuestions1() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ProposalQuestionAnswerBean loProposalQuestionAnswerBean = new ProposalQuestionAnswerBean();
		loProposalQuestionAnswerBean.setQuestionText("What's Your Name ?");

		List<ProposalQuestionAnswerBean> loProposalQuestionAnswerList = new ArrayList<ProposalQuestionAnswerBean>();
		loProposalQuestionAnswerList.add(loProposalQuestionAnswerBean);
		loProposalDetailsBean.setQuestionAnswerBeanList(loProposalQuestionAnswerList);
		loProposalDetailsBean.setProcurementStatus("3");
		Boolean lbSaveStatus = moProcurementService.saveProposalCustomQuestions(moSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalCustomQuestions
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalCustomQuestions2() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ProposalQuestionAnswerBean loProposalQuestionAnswerBean = new ProposalQuestionAnswerBean();
		loProposalQuestionAnswerBean.setIsAddendum("false");
		loProposalQuestionAnswerBean.setQuestionSeqNo("1");
		loProposalQuestionAnswerBean.setQuestionText("What's Your Name ?");

		List<ProposalQuestionAnswerBean> loProposalQuestionAnswerList = new ArrayList<ProposalQuestionAnswerBean>();
		loProposalQuestionAnswerList.add(loProposalQuestionAnswerBean);
		loProposalDetailsBean.setQuestionAnswerBeanList(loProposalQuestionAnswerList);
		loProposalDetailsBean.setProcurementStatus("3");
		Boolean lbSaveStatus = moProcurementService.saveProposalCustomQuestions(moSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalCustomQuestions
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalCustomQuestionsCasePositive() throws ApplicationException
	{

		Boolean lbSaveStatus = moProcurementService.saveProposalCustomQuestions(moSession, getProposalBeanParams());
		assertNotNull(lbSaveStatus);
	}

	/**
	 * This method tests saveProposalCustomQuestions
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalCustomQuestionsCaseNegative() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ProposalQuestionAnswerBean loProposalQuestionAnswerBean = new ProposalQuestionAnswerBean();
		loProposalQuestionAnswerBean.setQuestionText("%$@#%$@");

		List<ProposalQuestionAnswerBean> loProposalQuestionAnswerList = new ArrayList<ProposalQuestionAnswerBean>();
		loProposalQuestionAnswerList.add(loProposalQuestionAnswerBean);
		loProposalDetailsBean.setQuestionAnswerBeanList(loProposalQuestionAnswerList);

		Boolean lbSaveStatus = moProcurementService.saveProposalCustomQuestions(null, loProposalDetailsBean);
		assertTrue(lbSaveStatus);
	}

	/**
	 * This method tests saveProposalCustomQuestions
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalCustomQuestions3() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ProposalQuestionAnswerBean loProposalQuestionAnswerBean = new ProposalQuestionAnswerBean();
		loProposalQuestionAnswerBean.setQuestionFlag(null);
		loProposalQuestionAnswerBean.setQuestionSeqNo(null);
		List<ProposalQuestionAnswerBean> loProposalQuestionAnswerList = new ArrayList<ProposalQuestionAnswerBean>();
		loProposalQuestionAnswerList.add(loProposalQuestionAnswerBean);
		loProposalDetailsBean.setQuestionAnswerBeanList(loProposalQuestionAnswerList);
		loProposalDetailsBean.setProcurementStatus("2");
		Boolean lbSaveStatus = moProcurementService.saveProposalCustomQuestions(moSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalCustomQuestions
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalCustomQuestions4() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ProposalQuestionAnswerBean loProposalQuestionAnswerBean = new ProposalQuestionAnswerBean();
		loProposalQuestionAnswerBean.setQuestionFlag(null);
		loProposalQuestionAnswerBean.setQuestionSeqNo(null);

		List<ProposalQuestionAnswerBean> loProposalQuestionAnswerList = new ArrayList<ProposalQuestionAnswerBean>();
		loProposalQuestionAnswerList.add(loProposalQuestionAnswerBean);
		loProposalDetailsBean.setQuestionAnswerBeanList(loProposalQuestionAnswerList);
		loProposalDetailsBean.setProcurementStatus("2");
		Boolean lbSaveStatus = moProcurementService.saveProposalCustomQuestions(moSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method populates the filter bean object with default sorting and
	 * pagination parameters.
	 * 
	 * @return a procurement filter bean object
	 */
	private ProposalDetailsBean getProposalBeanParams()
	{
		ProposalDetailsBean loProposalDetailsBean = new ProposalDetailsBean();
		loProposalDetailsBean.setCreatedBy("city_143");
		loProposalDetailsBean.setModifiedBy("city_143");
		loProposalDetailsBean.setProcurementId("624");
		loProposalDetailsBean.setProcurementStatus("3");
		return loProposalDetailsBean;
	}

	/**
	 * This method tests saveProposalDocumentType
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalDocumentType1() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentType(null);
		loExtendedDocument.setDocumentSeqNumber(null);
		List<ExtendedDocument> loExtendedDocumentList = new ArrayList<ExtendedDocument>();
		loExtendedDocumentList.add(loExtendedDocument);
		loProposalDetailsBean.setRequiredDocumentList(loExtendedDocumentList);
		loProposalDetailsBean.setOptionalDocumentList(loExtendedDocumentList);
		Boolean lbSaveStatus = moProcurementService.saveProposalDocumentType(moSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalDocumentType
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalDocumentType2() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setIsAddendum("false");
		loExtendedDocument.setDocumentType(null);
		loExtendedDocument.setDocumentSeqNumber(null);

		List<ExtendedDocument> loExtendedDocumentList = new ArrayList<ExtendedDocument>();
		loExtendedDocumentList.add(loExtendedDocument);
		loProposalDetailsBean.setRequiredDocumentList(loExtendedDocumentList);
		loProposalDetailsBean.setOptionalDocumentList(loExtendedDocumentList);
		Boolean lbSaveStatus = moProcurementService.saveProposalDocumentType(moSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalDocumentType
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalDocumentType5() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentSeqNumber("16");
		loExtendedDocument.setRequiredFlag("Y");

		List<ExtendedDocument> loExtendedDocumentList = new ArrayList<ExtendedDocument>();
		loExtendedDocumentList.add(loExtendedDocument);
		loProposalDetailsBean.setRequiredDocumentList(loExtendedDocumentList);
		loProposalDetailsBean.setOptionalDocumentList(loExtendedDocumentList);
		Boolean lbSaveStatus = moProcurementService.saveProposalDocumentType(moSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalDocumentType
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalDocumentType8() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setRequiredFlag("N");

		List<ExtendedDocument> loExtendedDocumentList = new ArrayList<ExtendedDocument>();
		loExtendedDocumentList.add(loExtendedDocument);
		loProposalDetailsBean.setRequiredDocumentList(loExtendedDocumentList);
		loProposalDetailsBean.setOptionalDocumentList(loExtendedDocumentList);
		Boolean lbSaveStatus = moProcurementService.saveProposalDocumentType(moSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalDocumentType
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalDocumentType9() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setIsAddendum("false");
		loExtendedDocument.setDocumentSeqNumber("16");
		loExtendedDocument.setRequiredFlag("N");

		List<ExtendedDocument> loExtendedDocumentList = new ArrayList<ExtendedDocument>();
		loExtendedDocumentList.add(loExtendedDocument);
		loProposalDetailsBean.setRequiredDocumentList(loExtendedDocumentList);
		loProposalDetailsBean.setOptionalDocumentList(loExtendedDocumentList);
		Boolean lbSaveStatus = moProcurementService.saveProposalDocumentType(moSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	/**
	 * This method tests saveProposalDocumentType
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testSaveProposalDocumentType12() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = getProposalBeanParams();

		ExtendedDocument loExtendedDocument = new ExtendedDocument();
		loExtendedDocument.setDocumentSeqNumber("16");
		loExtendedDocument.setRequiredFlag("N");

		List<ExtendedDocument> loExtendedDocumentList = new ArrayList<ExtendedDocument>();
		loExtendedDocumentList.add(loExtendedDocument);
		loProposalDetailsBean.setRequiredDocumentList(loExtendedDocumentList);
		loProposalDetailsBean.setOptionalDocumentList(loExtendedDocumentList);
		Boolean lbSaveStatus = moProcurementService.saveProposalDocumentType(moSession, loProposalDetailsBean);
		assertEquals(Boolean.TRUE, lbSaveStatus);
	}

	@Test
	public void testGetNotificationMapForSubmitProposal1() throws ApplicationException
	{
		Map<String, String> aoStatusMap = new HashMap<String, String>();
		HashMap<String, Object> aoNotificationMap = new HashMap<String, Object>();
		Boolean aoProposalUpdateFlag = true;
		aoStatusMap.put("PROPOSAL_STATUS_ID", "17");
		aoStatusMap.put("PROC_REVIEW_STATUS_ID", "5");
		aoStatusMap.put("PROCUREMENT_TITLE", "Nitin");
		aoStatusMap.put("PROPOSAL_TITLE", "Untitled Prop");
		aoStatusMap.put("AGENCY_NAME", "ACS");
		aoStatusMap.put("MODIFIED_DATE", "21-MAY-13 11.48.32.000000000 AM");
		aoStatusMap.put("ORGANIZATION_LEGAL_NAME", "PROVIDER");
		aoStatusMap.put("ORGANIZATION_ID", "hhs");
		aoStatusMap.put("AGENCY_ID", "ACS");
		aoStatusMap.put("ORGANIZATION_LEGAL_NAME", "HHS");
		Map<String, String> aoRequestMap = new HashMap<String, String>();
		aoNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, aoRequestMap);
		aoNotificationMap = moProposalService.getNotificationMapForSubmitProposal(aoStatusMap, aoProposalUpdateFlag,
				aoNotificationMap);
		assertNotNull(aoNotificationMap);
	}

	@Test
	public void testGetNotificationMapForSubmitProposal2() throws ApplicationException
	{
		Map<String, String> aoStatusMap = new HashMap<String, String>();
		HashMap<String, Object> aoNotificationMap = new HashMap<String, Object>();
		Boolean aoProposalUpdateFlag = true;
		aoStatusMap.put("PROPOSAL_STATUS_ID", "17");
		aoStatusMap.put("PROC_REVIEW_STATUS_ID", null);
		aoStatusMap.put("PROC_STATUS_ID", "3");
		aoStatusMap.put("PROCUREMENT_TITLE", "Nitin");
		aoStatusMap.put("PROPOSAL_TITLE", "Untitled Prop");
		aoStatusMap.put("AGENCY_NAME", "ACS");
		aoStatusMap.put("MODIFIED_DATE", "21-MAY-13 11.48.32.000000000 AM");
		aoStatusMap.put("ORGANIZATION_LEGAL_NAME", "PROVIDER");
		aoStatusMap.put("ORGANIZATION_ID", "hhs");
		aoStatusMap.put("AGENCY_ID", "ACS");
		aoStatusMap.put("ORGANIZATION_LEGAL_NAME", "HHS");
		Map<String, String> aoRequestMap = new HashMap<String, String>();
		aoNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, aoRequestMap);
		aoNotificationMap = moProposalService.getNotificationMapForSubmitProposal(aoStatusMap, aoProposalUpdateFlag,
				aoNotificationMap);
		assertNotNull(aoNotificationMap);
	}

	@Test
	public void testGetNotificationMapForSubmitProposal3() throws ApplicationException
	{
		Map<String, String> aoStatusMap = new HashMap<String, String>();
		HashMap<String, Object> aoNotificationMap = new HashMap<String, Object>();
		Boolean aoProposalUpdateFlag = true;
		aoStatusMap.put("PROPOSAL_STATUS_ID", "19");
		aoStatusMap.put("PROC_REVIEW_STATUS_ID", "5");
		aoStatusMap.put("PROCUREMENT_TITLE", "Nitin");
		aoStatusMap.put("PROPOSAL_TITLE", "Untitled Prop");
		aoStatusMap.put("AGENCY_NAME", "ACS");
		aoStatusMap.put("MODIFIED_DATE", "21-MAY-13 11.48.32.000000000 AM");
		aoStatusMap.put("ORGANIZATION_LEGAL_NAME", "PROVIDER");
		aoStatusMap.put("ORGANIZATION_ID", "hhs");
		aoStatusMap.put("AGENCY_ID", "ACS");
		aoStatusMap.put("ORGANIZATION_LEGAL_NAME", "HHS");
		Map<String, String> aoRequestMap = new HashMap<String, String>();
		aoNotificationMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, aoRequestMap);
		aoNotificationMap = moProposalService.getNotificationMapForSubmitProposal(aoStatusMap, aoProposalUpdateFlag,
				aoNotificationMap);
		assertNotNull(aoNotificationMap);
	}

	@Test
	public void testGetProposalDocumentList() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "3247");
		loParamMap.put("proposalId", "2401");
		List<ExtendedDocument> loProposalDocumentList = (List<ExtendedDocument>) moProposalService
				.getProposalDocumentList(moSession, loParamMap);
		assertNotNull(loProposalDocumentList);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProposalDocumentListExp() throws ApplicationException
	{
		Map<String, String> loParamMap = null;
		moProposalService.getProposalDocumentList(moSession, loParamMap);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProposalDocumentListCase1() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "##");
		loParamMap.put("proposalId", "37");
		moProposalService.getProposalDocumentList(moSession, loParamMap);
	}

	@Test(expected = ApplicationException.class)
	public void testGetProposalDocumentListCase2() throws ApplicationException
	{
		Map<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("procurementId", "77");
		loParamMap.put("proposalId", "##");
		List<ExtendedDocument> loProposalDocumentList = (List<ExtendedDocument>) moProposalService
				.getProposalDocumentList(moSession, loParamMap);
		assertNotNull(loProposalDocumentList);
	}

	/**
	 * This method tests the execution of fetchVersionInformation method and
	 * determines whether or not the Proposal Documents List is getting
	 * generated.
	 */
	@Test
	public void testFetchVersionInformation1() throws ApplicationException
	{
		String lsProposalId = "1854";
		String lsProcurementID = "2523";
		ProposalDetailsBean loProposalDocumentList = moProposalService.fetchVersionInformation(moSession, lsProposalId,
				lsProcurementID);
		assertNotNull(loProposalDocumentList);

	}

	/**
	 * This method tests the execution of fetchVersionInformation method and
	 * determines whether or not the Proposal Documents List is getting
	 * generated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchVersionInformation2() throws ApplicationException
	{
		ProposalDetailsBean loProposalDocumentList = moProposalService.fetchVersionInformation(null, "", "");
		assertNotNull(loProposalDocumentList);
	}

	/**
	 * This method tests the negative execution of getProposalDocuments method
	 * and determines whether or not the Proposal Documents List is getting
	 * generated.
	 * 
	 * @throws ApplicationException - If an ApplicationException occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testGetProposalDocumentsCaseApplicationException() throws ApplicationException
	{
		String lsProposalId = "###";
		moProposalService.getProposalDocuments(moSession, lsProposalId, null);

	}
	
	/**
	 * This method tests whether evaluations are in progress for a procurement.
	 * @throws Exception
	 */
	@Test
	public void testReleaseAddendumAudit() throws Exception
	{
		HhsAuditBean loAuditBean = new HhsAuditBean();
		loAuditBean.setUserId("city_459");
		loAuditBean.setEntityId(msProcId5);
		Boolean lbActualResult = (Boolean) loHhsAuditService.releaseAddendumAudit(moSession, loAuditBean, true);
		Boolean liExpectedResult = Boolean.TRUE;
		assertEquals(liExpectedResult, lbActualResult);
	}
	
	/**
	 * This method tests whether evaluations are in progress for a procurement.
	 * @throws Exception
	 */
	@Test(expected = ApplicationException.class)
	public void testReleaseAddendumAuditException() throws Exception
	{
		HhsAuditBean loAuditBean = new HhsAuditBean();
		Boolean lbActualResult = (Boolean) loHhsAuditService.releaseAddendumAudit(null, loAuditBean, true);
		Boolean liExpectedResult = Boolean.FALSE;
		assertEquals(liExpectedResult, lbActualResult);
	}

}