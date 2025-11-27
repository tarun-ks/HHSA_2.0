package com.nyc.hhs.taskhandlersTest;

import static org.junit.Assert.assertTrue;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.junit.util.JUnitUtil;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.taskhandlers.ContractBudgetReviewTaskHandler;
import com.nyc.hhs.util.PropertyLoader;

public class ContractBudgetReviewTaskHandlerTest {

	private static SqlSession moSession = null; // SQL Session

	ContractBudgetReviewTaskHandler loContractBudgetReviewTaskHandler = new ContractBudgetReviewTaskHandler();

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		//JUnitUtil.getTransactionManager();
	}

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
		P8SecurityOperations loP8SecurityService = new P8SecurityOperations();
		loP8SecurityService.getPESession(loUserSession);
		loP8SecurityService.getObjectStore(loUserSession);

		return loUserSession;
	}
	
	/**
	 * This method test the taskApprove method in TaskHandler.
	 * Positive Scenario - It should update the budget details after the workflow task is completed
	 * @throws ApplicationException
	 */
	@Test
	public void testTaskApprove() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("111777");
		loTaskDetailsBean.setNewFYId("2017");
		loTaskDetailsBean.setUserId("803");
		loTaskDetailsBean.setLevel("1");
		loTaskDetailsBean.setEntityType("");
		loTaskDetailsBean.setEntityId("");
		loTaskDetailsBean.setTotalLevel("1");
		loTaskDetailsBean.setTaskName(HHSConstants.TASK_CONTRACT_CONFIGURATION);
		loTaskDetailsBean.setWorkFlowId("0D20824E5601D14E98AC24A8E8DBCDD5");
		loTaskDetailsBean.setTaskStatus("Approved");
		loTaskDetailsBean.setAmendmentType(HHSConstants.NEGATIVE);
		
		loContractBudgetReviewTaskHandler.taskApprove(loTaskDetailsBean);
	}
	
	/**
	 * This method test the taskApprove method in TaskHandler.
	 * Negative Scenario - It should throw application exception as ContractId is null in loTaskDetailsBean
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testTaskApprove1() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(null);
		loTaskDetailsBean.setEntityType("");
		loTaskDetailsBean.setEntityId("");
		loTaskDetailsBean.setNewFYId("2017");
		loTaskDetailsBean.setTaskStatus(ApplicationConstants.STATUS_APPROVED);
		loTaskDetailsBean.setTaskName(HHSConstants.TASK_CONTRACT_CONFIGURATION);
		loTaskDetailsBean.setP8UserSession(null);
		loContractBudgetReviewTaskHandler.taskApprove(loTaskDetailsBean);
	}
	
	/**
	 * This method test the taskApprove method in TaskHandler.
	 * Negative Scenario - It should throw application exception  because of null P8UserSession
	 * @throws ApplicationException
	 */
	@Test
	public void testTaskApprove2() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("111777");
		loTaskDetailsBean.setEntityType("");
		loTaskDetailsBean.setEntityId("");
		loTaskDetailsBean.setNewFYId("2017");
		loTaskDetailsBean.setTaskStatus(ApplicationConstants.STATUS_APPROVED);
		loTaskDetailsBean.setTaskName(HHSConstants.TASK_CONTRACT_CONFIGURATION);
		loTaskDetailsBean.setP8UserSession(null);
		loContractBudgetReviewTaskHandler.taskApprove(loTaskDetailsBean);
		assertTrue(true);
	}
	
	/**
	 * This method test the taskReturn method in TaskHandler.
	 * Negative Scenario
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testTaskReturn() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("111777");
		loTaskDetailsBean.setEntityType("");
		loTaskDetailsBean.setEntityId("");
		loTaskDetailsBean.setNewFYId("2017");
		loTaskDetailsBean.setTaskStatus(ApplicationConstants.STATUS_APPROVED);
		loTaskDetailsBean.setTaskName(HHSConstants.TASK_CONTRACT_CONFIGURATION);
		loTaskDetailsBean.setLevel("1");
		loTaskDetailsBean.setP8UserSession(getFileNetSession());
		loContractBudgetReviewTaskHandler.taskReturn(loTaskDetailsBean);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		moSession.close();
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetReviewTaskHandlertaskApprove0NegativeApp() throws ApplicationException
	{
		ContractBudgetReviewTaskHandler loContractBudgetReviewTaskHandler = new ContractBudgetReviewTaskHandler();
		loContractBudgetReviewTaskHandler.taskApprove(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetReviewTaskHandlertaskReturn1NegativeApp() throws ApplicationException
	{
		ContractBudgetReviewTaskHandler loContractBudgetReviewTaskHandler = new ContractBudgetReviewTaskHandler();
		loContractBudgetReviewTaskHandler.taskReturn(null);
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetReviewTaskHandlertaskApprove0Negative()
	{
		ContractBudgetReviewTaskHandler loContractBudgetReviewTaskHandler = new ContractBudgetReviewTaskHandler();
		try
		{
			loContractBudgetReviewTaskHandler.taskApprove(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetReviewTaskHandlertaskReturn1Negative()
	{
		ContractBudgetReviewTaskHandler loContractBudgetReviewTaskHandler = new ContractBudgetReviewTaskHandler();
		try
		{
			loContractBudgetReviewTaskHandler.taskReturn(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

}
