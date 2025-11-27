package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.junit.Test;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.TaskService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.CommentsHistoryBean;
import com.nyc.hhs.model.HhsAuditBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class TaskServiceTest
{

	SqlSession moMyBatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
	TaskService moTaskService = new TaskService();

	// @Test
	/**
	 * @return
	 * @throws ApplicationException
	 */
	public P8UserSession getFileNetSession() throws ApplicationException
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
		loP8SecurityService.getPESession(loUserSession);
		loP8SecurityService.getObjectStore(loUserSession);
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
		.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);


		return loUserSession;
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testLaunchWFByProvider() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap loHMWFRequiredProps = setFinancialWFProperty();
		loHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.WF_CONTRACT_CONFIGURATION);
		loHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, true);
		HhsAuditBean loHhsAuditBean = (HhsAuditBean) loTaskService.launchFinancialWorkflow(loP8UserSession,
				moMyBatisSession, loHMWFRequiredProps, true);
		assertNotNull(loHhsAuditBean);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testLaunchWFByAgency() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap loHMWFRequiredProps = setFinancialWFProperty();
		loHMWFRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.WF_CONTRACT_CONFIGURATION);
		HhsAuditBean loHhsAuditBean = (HhsAuditBean) loTaskService.launchFinancialWorkflow(loP8UserSession,
				moMyBatisSession, loHMWFRequiredProps, true);
		assertNotNull(loHhsAuditBean);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testLaunchWFByProviderAuthFail() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap loHMWFRequiredProps = setFinancialWFProperty();
		loHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, true);
		HhsAuditBean loHhsAuditBean = (HhsAuditBean) loTaskService.launchFinancialWorkflow(loP8UserSession,
				moMyBatisSession, loHMWFRequiredProps, false);
		assertNull(loHhsAuditBean);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testLaunchWFByAgencyAuthFail() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap loHMWFRequiredProps = setFinancialWFProperty();
		HhsAuditBean loHhsAuditBean = (HhsAuditBean) loTaskService.launchFinancialWorkflow(loP8UserSession,
				moMyBatisSession, loHMWFRequiredProps, false);
		assertNull(loHhsAuditBean);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testLaunchWFException() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = new P8UserSession();
		HashMap loHMWFRequiredProps = setFinancialWFProperty();
		HhsAuditBean loHhsAuditBean = (HhsAuditBean) loTaskService.launchFinancialWorkflow(loP8UserSession,
				moMyBatisSession, loHMWFRequiredProps, true);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testLaunchWFExceptionLevelNotsetProvider() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap loHMWFRequiredProps = setFinancialWFProperty();
		loHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, true);
		HhsAuditBean loHhsAuditBean = (HhsAuditBean) loTaskService.launchFinancialWorkflow(loP8UserSession,
				moMyBatisSession, loHMWFRequiredProps, true);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testLaunchWFExceptionLevelNotset() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap loHMWFRequiredProps = setFinancialWFProperty();
		loHMWFRequiredProps.put(HHSConstants.AUDIT_PROVIDER_INITIATED_TASK, false);
		HhsAuditBean loHhsAuditBean = (HhsAuditBean) loTaskService.launchFinancialWorkflow(loP8UserSession,
				moMyBatisSession, loHMWFRequiredProps, true);
	}

	/**
	 * @return
	 */
	private HashMap setPropertiesForTask()
	{
		HashMap loHmRequiredProps = new HashMap();
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROCUREMENT_EPIN, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROVIDER_ID, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_AWARD_EPIN, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CT, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_NAME, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_SUBMITTED_BY, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_SUBMITTED_DATE, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO_NAME, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_ASSIGNED_DATE, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_ID, "");
		loHmRequiredProps.put(HHSConstants.CURR_LEVEL, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TOTAL_LEVEL, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_AGENCY_ID, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROCURMENT_ID, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TYPE, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_BUDGET_ID, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_INVOICE_ID, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_NEW_FISCAL_YEAR_ID, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CONTRACT_CONF_WOB, "");
		return loHmRequiredProps;
	}

	/**
	 * @return
	 */
	private HashMap setFinancialWFProperty()
	{
		HashMap loHmRequiredProps = new HashMap();
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROCUREMENT_TITLE, "Proc 1");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROCUREMENT_EPIN, "1234");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROVIDER_ID, "accenture");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_AWARD_EPIN, "123");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CT, "456");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_SUBMITTED_BY, "803");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_AGENCY_ID, "ACS");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_PROCURMENT_ID, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CONTRACT_ID, "111777");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_TASK_TYPE, HHSConstants.WF_CONTRACT_BUDGET_MODIFICATION);
		// loHmRequiredProps.put(HHSConstants.PROPERTY_PE_CONTRACT_TITLE, "");
		loHmRequiredProps.put(HHSConstants.PROPERTY_PE_BUDGET_ID, "555");
		// loHmRequiredProps.put(HHSConstants.PROPERTY_PE_INVOICE_ID, "");
		return loHmRequiredProps;
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchLastCommentForTask() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		TaskDetailsBean loTaskDetailsBeanInput = setTaskDetailBean();
		loTaskDetailsBeanInput.setIsTaskScreen(true);
		TaskDetailsBean loTaskDetailsBeanOutput = (TaskDetailsBean) loTaskService.fetchLastComment(moMyBatisSession,
				loTaskDetailsBeanInput);
		assertTrue(loTaskDetailsBeanOutput.getInternalComment().equalsIgnoreCase(""));
	}

	@Test
	public void testFetchLastCommentForTask1() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		TaskDetailsBean loTaskDetailsBeanInput = new TaskDetailsBean();
		loTaskDetailsBeanInput.setWorkFlowId("9DDF01B9620E13439773122108159365");
		loTaskDetailsBeanInput.setTaskId("123");
		loTaskDetailsBeanInput.setIsTaskScreen(true);
		TaskDetailsBean loTaskDetailsBeanOutput = (TaskDetailsBean) loTaskService.fetchLastComment(moMyBatisSession,
				loTaskDetailsBeanInput);
		assertTrue(loTaskDetailsBeanOutput.getInternalComment().equalsIgnoreCase(""));
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchLastCommentForNonTask() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		TaskDetailsBean loTaskDetailsBeanInput = setTaskDetailBean();
		loTaskDetailsBeanInput.setIsTaskScreen(false);
		TaskDetailsBean loTaskDetailsBeanOutput = (TaskDetailsBean) loTaskService.fetchLastComment(moMyBatisSession,
				loTaskDetailsBeanInput);
		assertTrue(loTaskDetailsBeanOutput.getInternalComment().equalsIgnoreCase(""));
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchLastCommentForNonTask1() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		TaskDetailsBean loTaskDetailsBeanInput = new TaskDetailsBean();
		loTaskDetailsBeanInput.setIsTaskScreen(false);
		loTaskDetailsBeanInput.setEntityId("111777");
		loTaskDetailsBeanInput.setEntityType("Contract Budget");
		TaskDetailsBean loTaskDetailsBeanOutput = (TaskDetailsBean) loTaskService.fetchLastComment(moMyBatisSession,
				loTaskDetailsBeanInput);
		assertTrue(loTaskDetailsBeanOutput.getInternalComment().equalsIgnoreCase(""));
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchLastCommentApplicationException() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		TaskDetailsBean loTaskDetailsBeanInput = new TaskDetailsBean();
		loTaskDetailsBeanInput.setIsTaskScreen(false);
		loTaskService.fetchLastComment(moMyBatisSession, loTaskDetailsBeanInput);

	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchLastCommentApplicationException2() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		TaskDetailsBean loTaskDetailsBeanInput = new TaskDetailsBean();
		loTaskDetailsBeanInput.setIsTaskScreen(false);
		loTaskService.fetchLastComment(null, loTaskDetailsBeanInput);

	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchLastCommentApplicationException3() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		TaskDetailsBean loTaskDetailsBeanInput = new TaskDetailsBean();
		loTaskDetailsBeanInput.setIsTaskScreen(false);
		loTaskDetailsBeanInput.setEntityType("Contract Budget");
		loTaskService.fetchLastComment(moMyBatisSession, loTaskDetailsBeanInput);

	}

	/**
	 * @return
	 */
	public TaskDetailsBean setTaskDetailBean()
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setWorkFlowId("9DDF01B9620E13439773122108159365");
		loTaskDetailsBean.setTaskName("PCOF");
		loTaskDetailsBean.setTaskId("123");
		loTaskDetailsBean.setInternalComment("comment example");
		loTaskDetailsBean.setUserId("city_142");
		loTaskDetailsBean.setEntityId("111777");
		loTaskDetailsBean.setEntityType("Contract Budget");
		return loTaskDetailsBean;
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void TestFetchAgencyDetails() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		List<StaffDetails> loList = loTaskService.fetchAgencyDetails(moMyBatisSession, "DOH", "135", "4");
		assertNotNull(loList);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void TestFetchAgencyDetailsException() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchAgencyDetails(moMyBatisSession, null, null, null);
	}

	/**
	 * This method tests the execution of fetchAgencyTaskHistory method and
	 * determines whether task history exists for the given workflow Id
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test
	public void testFetchAgencyTaskHistory() throws ApplicationException
	{
		String lsWobNumber = "3A4B4B2D37BC4040B7A5172B68BDEAA6";
		HashMap loTaskPropsMap = new HashMap();
		loTaskPropsMap.put(HHSConstants.WORKFLOW_ID, lsWobNumber);
		loTaskPropsMap.put("entityId", "111777");
		loTaskPropsMap.put("entityType", "Contract Budget");
		loTaskPropsMap.put(HHSConstants.EVENT_NAME, HHSConstants.PROPERTY_TASK_CREATION_EVENT);
		List<CommentsHistoryBean> loCommentsBeanList = moTaskService.fetchAgencyTaskHistory(loTaskPropsMap,
				moMyBatisSession);
		assertNotNull(loCommentsBeanList);
		// assertTrue(loCommentsBeanList.size() > 0);
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchAgencyTaskHistory method and
	 * determines whether task history exists for the given workflow Id
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test(expected = ApplicationException.class)
	public void testFetchAgencyTaskHistory1() throws ApplicationException
	{
		String lsWobNumber = "3A4B4B2D37BC4040B7A5172B68BDEAA6";
		HashMap loTaskPropsMap = new HashMap();
		loTaskPropsMap.put(HHSConstants.WORKFLOW_ID, lsWobNumber);
		moTaskService.fetchAgencyTaskHistory(null, moMyBatisSession);
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchAgencyTaskHistory method and
	 * determines whether task history exists for the given workflow Id
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	@Test(expected = ApplicationException.class)
	public void testFetchAgencyTaskHistory2() throws ApplicationException
	{
		HashMap loTaskPropsMap = new HashMap();
		moTaskService.fetchAgencyTaskHistory(loTaskPropsMap, null);
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchAgencyTaskHistory method and
	 * determines whether task history exists for the given workflow Id
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test
	public void testfetchProviderTaskHistory() throws ApplicationException
	{
		HashMap loTaskPropsMap = new HashMap();
		loTaskPropsMap.put("entityId", "111777");
		loTaskPropsMap.put("entityType", "Contract Budget");
		loTaskPropsMap.put(HHSConstants.EVENT_NAME, HHSConstants.PROPERTY_TASK_CREATION_EVENT);
		loTaskPropsMap.put(HHSConstants.ENTITY_TYPE_FOR_AGENCY, " ");
		loTaskPropsMap.put(HHSConstants.EVENT_NAME_FOR_AGENCY, HHSConstants.AUDIT_TASK_PUBLIC_COMMENTS);
		List<CommentsHistoryBean> loCommentsBeanList = moTaskService.fetchProviderTaskHistory(loTaskPropsMap,
				moMyBatisSession);
		assertNotNull(loCommentsBeanList);
		// assertTrue(loCommentsBeanList.size() > 0);
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchAgencyTaskHistory method and
	 * determines whether task history exists for the given workflow Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testfetchProviderTaskHistory1() throws ApplicationException
	{
		moTaskService.fetchProviderTaskHistory(null, moMyBatisSession);
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchAgencyTaskHistory method and
	 * determines whether task history exists for the given workflow Id
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	@Test(expected = ApplicationException.class)
	public void testfetchProviderTaskHistory2() throws ApplicationException
	{
		HashMap loTaskPropsMap = new HashMap();
		moTaskService.fetchProviderTaskHistory(loTaskPropsMap, null);
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchUserLastComment method and
	 * determines whether internal and provider comments exists for the given
	 * workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchUserLastComment() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "338");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setEntityType(HHSConstants.ACCEPT_PROPOSAL);
		loTaskBean = moTaskService.fetchUserLastComment(moMyBatisSession, loTaskDetailMap, lsWobNumber, loTaskBean);
		assertNotNull(loTaskBean);
		assertNotNull(loTaskBean.getInternalComment());
		assertNotNull(loTaskBean.getProviderComment());
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchUserLastComment method and
	 * determines whether internal and provider comments exists for the given
	 * workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchUserLastComment1() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "339");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setEntityType(HHSConstants.ACCEPT_PROPOSAL);
		loTaskBean = moTaskService.fetchUserLastComment(moMyBatisSession, loTaskDetailMap, null, loTaskBean);
		assertNull(loTaskBean.getInternalComment());
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchUserLastComment method and
	 * determines whether internal and provider comments exists for the given
	 * workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchUserLastComment2() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "1075");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setEntityType(ApplicationConstants.AWARD);
		loTaskBean = moTaskService.fetchUserLastComment(moMyBatisSession, loTaskDetailMap, lsWobNumber, loTaskBean);
		assertNotNull(loTaskBean.getInternalComment());
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchUserLastComment method and
	 * determines whether internal and provider comments exists for the given
	 * workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchUserLastComment3() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "338");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setEntityType("Evaluation");
		loTaskBean = moTaskService.fetchUserLastComment(moMyBatisSession, null, lsWobNumber, loTaskBean);
		assertNull(loTaskBean.getInternalComment());
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchUserLastComment method and
	 * determines whether internal and provider comments exists for the given
	 * workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchUserLastComment4() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_PROPOSAL_ID, "339");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setEntityType("Evaluation");
		loTaskBean = moTaskService.fetchUserLastComment(moMyBatisSession, null, lsWobNumber, loTaskBean);
		assertNull(loTaskBean.getProviderComment());
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchUserLastComment method and
	 * determines whether internal and provider comments exists for the given
	 * workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test(expected = NullPointerException.class)
	public void testFetchUserLastComment5() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_TASK_ID, "1001");
		loTaskDetailMap.put(lsWobNumber, loTaskPropsMap);
		moTaskService.fetchUserLastComment(null, loTaskDetailMap, lsWobNumber, null);
	}

	/**
	 * This method tests the execution of fetchUserLastComment method and
	 * determines whether internal and provider comments exists for the given
	 * workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test(expected = ClassCastException.class)
	public void testFetchUserLastComment6() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_TASK_ID, "1001");
		loTaskDetailMap.put(lsWobNumber, "##");
		moTaskService.fetchUserLastComment(moMyBatisSession, loTaskDetailMap, lsWobNumber, null);
	}

	/**
	 * This method tests the execution of fetchUserLastComment method and
	 * determines whether internal and provider comments exists for the given
	 * workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchUserLastComment7() throws ApplicationException
	{
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskPropsMap = new HashMap<String, Object>();
		loTaskPropsMap.put(P8Constants.PE_WORKFLOW_EVALUATION_STATUS_ID, "380");
		loTaskDetailMap.put("##", loTaskPropsMap);
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setEntityType("Evaluator");
		loTaskBean = moTaskService.fetchUserLastComment(moMyBatisSession, loTaskDetailMap, null, loTaskBean);
		assertNull(loTaskBean.getInternalComment());
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchUserLastComment method and
	 * determines whether internal and provider comments exists for the given
	 * workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test
	public void testReassignTask1() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		TaskDetailsBean loTaskDetailsBeanInput = setTaskDetailBean();
		loTaskDetailsBeanInput.setWorkFlowId("C9ECFD3B8705224796202317435D6C0C");
		loTaskDetailsBeanInput.setReassignUserName("City_01");
		loTaskDetailsBeanInput.setReassignUserId("832");
		loTaskDetailsBeanInput.setTaskType("Contract Configuration Task");
		boolean lbStatus = loTaskService.reassignTask(loTaskDetailsBeanInput, loP8UserSession);
		assertTrue(lbStatus);
	}

	/**
	 * This method tests the execution of fetchUserLastComment method and
	 * determines whether internal and provider comments exists for the given
	 * workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test
	public void testReassignTask2() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		TaskDetailsBean loTaskDetailsBeanInput = setTaskDetailBean();
		loTaskDetailsBeanInput.setWorkFlowId("5B4D9187719DEC41BE395E98EA51EE5D");
		loTaskDetailsBeanInput.setReassignUserName("City_01");
		loTaskDetailsBeanInput.setReassignUserId("832");
		loTaskDetailsBeanInput.setTaskType("Contract Configuration Task");
		loTaskDetailsBeanInput.setTaskId("1000");
		boolean lbStatus = loTaskService.reassignTask(loTaskDetailsBeanInput, loP8UserSession);
		assertTrue(lbStatus);
	}

	/**
	 * This method tests the execution of fetchUserLastComment method and
	 * determines whether internal and provider comments exists for the given
	 * workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testReassignTask3() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();

		P8UserSession loP8UserSession = new P8UserSession();
		TaskDetailsBean loTaskDetailsBeanInput = setTaskDetailBean();
		loTaskDetailsBeanInput.setWorkFlowId("523A6EB5FD0112448A382B2D41567B76");
		loTaskDetailsBeanInput.setReassignUserName("Accenture User");
		loTaskDetailsBeanInput.setReassignUserId("832");
		loTaskDetailsBeanInput.setTaskType("Contract Configuration Task");
		loTaskDetailsBeanInput.setTaskId("1000");
		boolean lbStatus = loTaskService.reassignTask(loTaskDetailsBeanInput, loP8UserSession);
		assertTrue(lbStatus);
	}

	/**
	 * This method tests the execution of fetchUserLastComment method and
	 * determines whether internal and provider comments exists for the given
	 * workflow Id and task Id
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testReassignTask4() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		TaskDetailsBean loTaskDetailsBeanInput = setTaskDetailBean();
		// loTaskDetailsBeanInput.setWorkFlowId("523A6EB5FD0112448A382B2D41567B76");
		loTaskDetailsBeanInput.setReassignUserName("Accenture User");
		loTaskDetailsBeanInput.setReassignUserId("832");
		loTaskDetailsBeanInput.setTaskType("Contract Configuration Task");
		loTaskDetailsBeanInput.setTaskId("1000");
		loTaskService.reassignTask(loTaskDetailsBeanInput, loP8UserSession);
	}

	/**
	 * This method tests the execution of fetchAcceleratorTaskHistory method and
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test
	public void testFetchAcceleratorTaskHistory() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap loHMApplicationAudit = new HashMap();
		loTaskDetailMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "308");
		loHMApplicationAudit.put("entityType", "Accept Proposal Task");
		loHMApplicationAudit.put("eventName", "Sta+tus Change");
		loTaskMap.put(lsWobNumber, loTaskDetailMap);
		List<CommentsHistoryBean> loResultList = moTaskService.fetchAcceleratorTaskHistory(moMyBatisSession,
				loHMApplicationAudit, loTaskMap, lsWobNumber);
		assertNotNull(loResultList);
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchAcceleratorTaskHistory method and
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test
	public void testFetchAcceleratorTaskHistoryCase1() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap loHMApplicationAudit = new HashMap();
		loHMApplicationAudit.put("entityId", "308");
		loHMApplicationAudit.put("entityType", "Accept Proposal Task");
		loHMApplicationAudit.put("eventName", "Status Change");
		List<CommentsHistoryBean> loCommentsList = moTaskService.fetchAcceleratorTaskHistory(moMyBatisSession,
				loHMApplicationAudit, null, lsWobNumber);
		assertTrue(loCommentsList.size() == 0);
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchAcceleratorTaskHistory method and
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test
	public void testFetchAcceleratorTaskHistoryCase2() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap loHMApplicationAudit = new HashMap();
		loTaskDetailMap.put(P8Constants.PE_WORKFLOW_PROCUREMENT_ID, "308");
		loHMApplicationAudit.put("entityType", "Accept Proposal Task");
		loHMApplicationAudit.put("eventName", "Status Change");
		loTaskMap.put(lsWobNumber, null);
		List<CommentsHistoryBean> loCommentsList = moTaskService.fetchAcceleratorTaskHistory(moMyBatisSession,
				loHMApplicationAudit, loTaskMap, lsWobNumber);
		assertTrue(loCommentsList.size() == 0);
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchAcceleratorTaskHistory method and
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test(expected = ApplicationException.class)
	public void testFetchAcceleratorTaskHistoryCase3() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap loHMApplicationAudit = new HashMap();
		loHMApplicationAudit.put("entityType", "Accept Proposal Task");
		loHMApplicationAudit.put("eventName", "Status Change");
		loTaskMap.put(lsWobNumber, loTaskDetailMap);
		moTaskService.fetchAcceleratorTaskHistory(moMyBatisSession, loHMApplicationAudit, loTaskMap, lsWobNumber);
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchAcceleratorTaskHistory method and
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	@Test(expected = ApplicationException.class)
	public void testFetchAcceleratorTaskHistoryCase4() throws ApplicationException
	{
		String lsWobNumber = "92276F782A67E9468A40BFD3A29D7854";
		HashMap<String, Object> loTaskMap = new HashMap<String, Object>();
		HashMap<String, Object> loTaskDetailMap = new HashMap<String, Object>();
		HashMap loHMApplicationAudit = new HashMap();
		loTaskDetailMap.put("##", "308");
		loHMApplicationAudit.put("entityType", "Accept Proposal Task");
		loHMApplicationAudit.put("eventName", "Status Change");
		loTaskMap.put(lsWobNumber, loTaskDetailMap);
		moTaskService.fetchAcceleratorTaskHistory(moMyBatisSession, loHMApplicationAudit, loTaskMap, lsWobNumber);
		moMyBatisSession.close();
	}

	/**
	 * This method tests the execution of fetchUserLastComment method and
	 * determines whether internal and provider comments exists for the given
	 * workflow Id and task Id
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testfinishTask1() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap loHmProps = new HashMap();
		TaskDetailsBean loTaskDetailsBeanInput = new TaskDetailsBean();
		loTaskDetailsBeanInput.setWorkFlowId("81B6A8D73E62E54AA13E28C6F48B7A19");
		loTaskDetailsBeanInput.setTaskStatus("Returned For Revision");
		boolean lbStatus = loTaskService.finishTask(loTaskDetailsBeanInput, loP8UserSession, loHmProps);
		assertTrue(lbStatus);

	}

	/**
	 * This method tests the execution of fetchUserLastComment method and
	 * determines whether internal and provider comments exists for the given
	 * workflow Id and task Id
	 * @throws ApplicationException
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testfinishTask2() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();

		P8UserSession loP8UserSession = getFileNetSession();
		HashMap loHmProps = new HashMap();
		TaskDetailsBean loTaskDetailsBeanInput = new TaskDetailsBean();
		loTaskDetailsBeanInput.setWorkFlowId("F94312556E34344BAEC0F13D7390C98E");
		loTaskDetailsBeanInput.setTaskStatus("Approved");
		loTaskDetailsBeanInput.setTaskId("1000");
		boolean lbStatus = loTaskService.finishTask(loTaskDetailsBeanInput, loP8UserSession, loHmProps);
		assertTrue(lbStatus);

	}

	/**
	 * This method tests the execution of fetchAcceleratorTaskHistory method and
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	@Test(expected = ApplicationException.class)
	public void testfinishTask3() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();

		P8UserSession loP8UserSession = new P8UserSession();
		HashMap loHmProps = new HashMap();
		TaskDetailsBean loTaskDetailsBeanInput = new TaskDetailsBean();
		loTaskDetailsBeanInput.setWorkFlowId("4CCB59FB86B2604093E0E4A38887A295");
		loTaskDetailsBeanInput.setTaskStatus("Approved");
		loTaskDetailsBeanInput.setTaskId("1000");
		loTaskService.finishTask(loTaskDetailsBeanInput, loP8UserSession, loHmProps);
	}

	/**
	 * This method tests the execution of fetchAcceleratorTaskHistory method and
	 * @throws ApplicationException
	 */
	@SuppressWarnings(
	{ "rawtypes" })
	@Test(expected = ApplicationException.class)
	public void testfinishTask4() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		HashMap loHmProps = new HashMap();
		TaskDetailsBean loTaskDetailsBeanInput = new TaskDetailsBean();
		/*
		 * loTaskDetailsBeanInput.setWorkFlowId("4CCB59FB86B2604093E0E4A38887A295"
		 * ); loTaskDetailsBeanInput.setTaskStatus("Approved");
		 * loTaskDetailsBeanInput.setTaskId("1000");
		 */
		loTaskService.finishTask(loTaskDetailsBeanInput, loP8UserSession, loHmProps);
	}

	@Test
	public void testTaskStatusForClosedProcurment1() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		HashMap loHmReqdProps = new HashMap();
		loHmReqdProps.put("ProcurementID", "2291");
		P8UserSession loP8UserSession = getFileNetSession();
		boolean lbStatus = loTaskService.taskStatusForClosedProcurment(loP8UserSession, loHmReqdProps, true);
		assertTrue(lbStatus);
	}

	@Test
	public void testTaskStatusForClosedProcurment2() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		HashMap loHmReqdProps = new HashMap();
		loHmReqdProps.put("ProcurementID", "3211");
		P8UserSession loP8UserSession = getFileNetSession();
		boolean lbStatus = loTaskService.taskStatusForClosedProcurment(loP8UserSession, loHmReqdProps, null);
		assertTrue(lbStatus);
	}

	@Test
	public void testTaskStatusForClosedProcurment3() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		HashMap loHmReqdProps = new HashMap();
		loHmReqdProps.put("ProcurementID", "3212");
		P8UserSession loP8UserSession = getFileNetSession();
		boolean lbStatus = loTaskService.taskStatusForClosedProcurment(loP8UserSession, loHmReqdProps, false);
		assertTrue(lbStatus);
	}

	@Test
	public void testTaskStatusForClosedProcurment4() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		boolean lbStatus = loTaskService.taskStatusForClosedProcurment(loP8UserSession, null, false);
		assertTrue(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskStatusForClosedProcurment5() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		P8UserSession loP8UserSession = getFileNetSession();
		boolean lbStatus = loTaskService.taskStatusForClosedProcurment(loP8UserSession, null, true);
		assertTrue(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskStatusForClosedProcurmentNegative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		HashMap loHmReqdProps = new HashMap();
		loHmReqdProps.put("WorkFlowName", null);
		loHmReqdProps.put("ProcurementTitle", null);
		loHmReqdProps.put("AwardEPin", null);
		loHmReqdProps.put("LaunchBy", "Sadhna");
		P8UserSession loP8UserSession = getFileNetSession();
		boolean lbStatus = loTaskService.taskStatusForClosedProcurment(loP8UserSession, loHmReqdProps, true);
		assertTrue(lbStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicereassignTask0NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.reassignTask(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicelaunchFinancialWorkflow1NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.launchFinancialWorkflow(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServiceterminateWorkflow2NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.terminateWorkflow(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicecloseAllOpenTask3NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.closeAllOpenTask(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicetaskStatusForClosedProcurment4NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.taskStatusForClosedProcurment(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicecloseOriginalContractTask5NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.closeOriginalContractTask(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicedeleteNotNeededBudgetAndContract6NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.deleteNotNeededBudgetAndContract(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicedeleteBaseBudgetAndContract7NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.deleteBaseBudgetAndContract(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicesetPropertyInWF8NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.setPropertyInWF(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefinishTask9NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.finishTask(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchTaskDetails10NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchTaskDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchAgencyTaskHistory11NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchAgencyTaskHistory(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchProviderTaskHistory12NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchProviderTaskHistory(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchAgencyDetails13NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchAgencyDetails(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchAgencyDetailsForInbox14NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchAgencyDetailsForInbox(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchAgencyUserDetails15NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchAgencyUserDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchLastComment16NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchLastComment(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchUserLastComment17NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchUserLastComment(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchAgencyTaskHistory18NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchAgencyTaskHistory(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchAcceleratorTaskHistory19NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchAcceleratorTaskHistory(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicegetContractInfo20NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.getContractInfo(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchCityProviderName21NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchCityProviderName(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServiceupdateAwardEPinInTasks22NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.updateAwardEPinInTasks(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchEvaluationLastComment23NegativeApp() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchEvaluationLastComment(null, null, null, null);
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicereassignTask0Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.reassignTask(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicelaunchFinancialWorkflow1Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.launchFinancialWorkflow(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServiceterminateWorkflow2Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.terminateWorkflow(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicecloseAllOpenTask3Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.closeAllOpenTask(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicetaskStatusForClosedProcurment4Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.taskStatusForClosedProcurment(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicecloseOriginalContractTask5Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.closeOriginalContractTask(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicedeleteNotNeededBudgetAndContract6Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.deleteNotNeededBudgetAndContract(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicedeleteBaseBudgetAndContract7Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.deleteBaseBudgetAndContract(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicesetPropertyInWF8Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.setPropertyInWF(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefinishTask9Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.finishTask(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchTaskDetails10Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchTaskDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchAgencyTaskHistory11Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchAgencyTaskHistory(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchProviderTaskHistory12Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchProviderTaskHistory(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchAgencyDetails13Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchAgencyDetails(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchAgencyDetailsForInbox14Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchAgencyDetailsForInbox(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchAgencyUserDetails15Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchAgencyUserDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchLastComment16Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchLastComment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchUserLastComment17Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchUserLastComment(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchAgencyTaskHistory18Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchAgencyTaskHistory(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchAcceleratorTaskHistory19Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchAcceleratorTaskHistory(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicegetContractInfo20Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.getContractInfo(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchCityProviderName21Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchCityProviderName(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServiceupdateAwardEPinInTasks22Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.updateAwardEPinInTasks(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchEvaluationLastComment23Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchEvaluationLastComment(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}


}
