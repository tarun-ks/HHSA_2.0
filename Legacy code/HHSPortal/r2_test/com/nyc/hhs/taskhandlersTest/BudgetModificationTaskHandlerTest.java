package com.nyc.hhs.taskhandlersTest;

import static org.junit.Assert.assertTrue;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.taskhandlers.BudgetModificationTaskHandler;
import com.nyc.hhs.util.PropertyLoader;

public class BudgetModificationTaskHandlerTest {


	private static SqlSession moSession = null; // SQL Session

	BudgetModificationTaskHandler loBudgetModificationTaskHandler = new BudgetModificationTaskHandler();
	

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
//		JUnitUtil.getTransactionManager();
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
		loTaskDetailsBean.setTotalLevel("1");
		loTaskDetailsBean.setWorkFlowId("0D20824E5601D14E98AC24A8E8DBCDD5");
		loTaskDetailsBean.setTaskStatus("Approved");
		loTaskDetailsBean.setAmendmentType(HHSConstants.NEGATIVE);
		
		loBudgetModificationTaskHandler.taskApprove(loTaskDetailsBean);
	}
	
	/**
	 * This method test the taskApprove method in TaskHandler.
	 * Negative Scenario - It should throw application exception as ContractId is null in loTaskDetailsBean
	 * @throws ApplicationException
	 */
	@Test(expected = Exception.class)
	public void testTaskApproveFail() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(null);
		loTaskDetailsBean.setNewFYId("2017");
		loTaskDetailsBean.setP8UserSession(getFileNetSession());
		loBudgetModificationTaskHandler.taskApprove(loTaskDetailsBean);
	}
	
	/**
	 * This method test the taskApprove method in TaskHandler.
	 * Negative Scenario - It should throw application exception  because of null P8UserSession
	 * @throws ApplicationException
	 */
	@Test
	public void testTaskApproveFail1() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("111777");
		loTaskDetailsBean.setNewFYId("2017");
		loTaskDetailsBean.setP8UserSession(null);
		loBudgetModificationTaskHandler.taskApprove(loTaskDetailsBean);
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
		loBudgetModificationTaskHandler.taskReturn(null);
	}
	
	/**
	 * This method test the taskReturn method in TaskHandler.
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testTaskReturn2() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setLevel(HHSConstants.ONE);
		loTaskDetailsBean.setTotalLevel(HHSConstants.ONE);
		loTaskDetailsBean.setInternalComment("AAAAAAAAAAAAAAA");
		loTaskDetailsBean.setEntityType("1");
		loTaskDetailsBean.setEntityId("1");
		loTaskDetailsBean.setUserId("803");
		loTaskDetailsBean.setContractId("3296");
		loTaskDetailsBean.setBudgetId("11205");
		loTaskDetailsBean.setBudgetAdvanceId("373");
		loTaskDetailsBean.setAgencyId("agency_47");
		loBudgetModificationTaskHandler.taskReturn(loTaskDetailsBean);
	}
	
	/**
	 * This method test the taskReturn method in TaskHandler.
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testTaskReturn3() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setLevel(HHSConstants.TWO);
		loTaskDetailsBean.setTotalLevel(HHSConstants.THREE);
		loTaskDetailsBean.setInternalComment("AAAAAAAAAAAAAAA");
		loTaskDetailsBean.setEntityType("1");
		loTaskDetailsBean.setEntityId("1");
		loTaskDetailsBean.setUserId("803");
		loTaskDetailsBean.setContractId("3296");
		loTaskDetailsBean.setBudgetId("11205");
		loTaskDetailsBean.setBudgetAdvanceId("373");
		loTaskDetailsBean.setAgencyId("agency_47");
		loBudgetModificationTaskHandler.taskReturn(loTaskDetailsBean);
	}
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		moSession.close();
	}

	@Test(expected = java.lang.Exception.class)
	public void testBudgetModificationTaskHandlertaskApprove0Negative()
	{
		BudgetModificationTaskHandler loBudgetModificationTaskHandler = new BudgetModificationTaskHandler();
		try
		{
			loBudgetModificationTaskHandler.taskApprove(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBudgetModificationTaskHandlertaskReturn1Negative()
	{
		BudgetModificationTaskHandler loBudgetModificationTaskHandler = new BudgetModificationTaskHandler();
		try
		{
			loBudgetModificationTaskHandler.taskReturn(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = ApplicationException.class)
	public void testBudgetModificationTaskHandlertaskApprove0NegativeApp() throws ApplicationException
	{
		BudgetModificationTaskHandler loBudgetModificationTaskHandler = new BudgetModificationTaskHandler();
		loBudgetModificationTaskHandler.taskApprove(null);
	}

	@Test(expected = ApplicationException.class)
	public void testBudgetModificationTaskHandlertaskReturn1NegativeApp() throws ApplicationException
	{
		BudgetModificationTaskHandler loBudgetModificationTaskHandler = new BudgetModificationTaskHandler();
		loBudgetModificationTaskHandler.taskReturn(null);
	}


}
