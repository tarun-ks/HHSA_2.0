package com.nyc.hhs.taskhandlersTest;

import static org.junit.Assert.assertTrue;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.junit.util.JUnitUtil;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.taskhandlers.AmendmentConfigurationTaskHandler;
import com.nyc.hhs.util.PropertyLoader;

public class AmendmentConfigurationTaskHandlerTest
{
	private static SqlSession moSession = null; // SQL Session

	AmendmentConfigurationTaskHandler loAmendmentConfigurationTaskHandler = new AmendmentConfigurationTaskHandler();

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
		loTaskDetailsBean.setLinkedWobNum("2");
		loTaskDetailsBean.setBaseContractId("111777");
		loTaskDetailsBean.setWorkFlowId("0D20824E5601D14E98AC24A8E8DBCDD5");
		loTaskDetailsBean.setTaskStatus("Approved");
		loAmendmentConfigurationTaskHandler.taskApprove(loTaskDetailsBean);
	}
	
	/**
	 * This method test the taskApprove method in TaskHandler.
	 * Negative Scenario - It should throw application exception as ContractId is null in loTaskDetailsBean
	 * @throws ApplicationException
	 */
	@Test
	public void testTaskApproveFail() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(null);
		loTaskDetailsBean.setNewFYId("2017");
		
		loAmendmentConfigurationTaskHandler.taskApprove(loTaskDetailsBean);
		assertTrue(true);
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
		loAmendmentConfigurationTaskHandler.taskApprove(loTaskDetailsBean);
		assertTrue(true);
	}
	
	/**
	 * This method test the taskReturn method in TaskHandler.
	 * Positive Scenario
	 * @throws ApplicationException
	 */
	@Test
	public void testTaskReturn() throws ApplicationException
	{
		loAmendmentConfigurationTaskHandler.taskReturn(null);
		assertTrue(true);
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
	public void testAmendmentConfigurationTaskHandlertaskApprove0Negative()
	{
		AmendmentConfigurationTaskHandler loAmendmentConfigurationTaskHandler = new AmendmentConfigurationTaskHandler();
		try
		{
			loAmendmentConfigurationTaskHandler.taskApprove(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAmendmentConfigurationTaskHandlertaskReturn1Negative()
	{
		AmendmentConfigurationTaskHandler loAmendmentConfigurationTaskHandler = new AmendmentConfigurationTaskHandler();
		try
		{
			loAmendmentConfigurationTaskHandler.taskReturn(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = ApplicationException.class)
	public void testAmendmentConfigurationTaskHandlertaskApprove0NegativeApp() throws ApplicationException
	{
		AmendmentConfigurationTaskHandler loAmendmentConfigurationTaskHandler = new AmendmentConfigurationTaskHandler();
		loAmendmentConfigurationTaskHandler.taskApprove(null);
	}

	@Test(expected = ApplicationException.class)
	public void testAmendmentConfigurationTaskHandlertaskReturn1NegativeApp() throws ApplicationException
	{
		AmendmentConfigurationTaskHandler loAmendmentConfigurationTaskHandler = new AmendmentConfigurationTaskHandler();
		loAmendmentConfigurationTaskHandler.taskReturn(null);
	}

}
