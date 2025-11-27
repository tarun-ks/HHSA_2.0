package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.ConfigurationService;
import com.nyc.hhs.daomanager.service.FinancialsBudgetService;
import com.nyc.hhs.daomanager.service.TaskService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.BudgetAdvanceBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class Release320ServiceTest
{

	static SqlSession moMyBatisSession = null;
	static SqlSession loFilenetPEDBSession = null;
	private static P8UserSession session = null; // FileNet session
	/**
	 * SQL session created ONCE before the class
	 * 
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{

		Boolean lbThrown = false;
		try
		{
			moMyBatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory().openSession();
			session = getFileNetSession();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * Close the SQL session created at the beginning
	 * 
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moMyBatisSession.close();
			loFilenetPEDBSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	public static P8UserSession getFileNetSession() throws ApplicationException
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
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
		
		// loP8SecurityService.getPESession(loUserSession);
		// loP8SecurityService.getObjectStore(loUserSession);

		return loUserSession;
	}
	
	/**Below are the JUnits for enhancement 6262
	 * service FinancialsBudgetService
	 * 
	 * */
	@Test
	public void testFetchContractAmountForValidation1() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		BudgetAdvanceBean loBudgetAdvanceBean = new BudgetAdvanceBean();
		loBudgetAdvanceBean.setBudgetId("12753");
		String lsAmount = null;
		lsAmount = (String)loFinancialsBudgetService.fetchContractAmountForValidation(moMyBatisSession,loBudgetAdvanceBean);
		assertNotNull(lsAmount);
	}
	
	/**Below are the JUnits for enhancement 6262
	 * service FinancialsBudgetService
	 * 
	 * */
	@Test(expected=ApplicationException.class)
	public void testFetchContractAmountForValidation2() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		BudgetAdvanceBean loBudgetAdvanceBean = new BudgetAdvanceBean();
		loBudgetAdvanceBean.setBudgetId("XX");
		String lsAmount = null;
		lsAmount = (String)loFinancialsBudgetService.fetchContractAmountForValidation(moMyBatisSession,loBudgetAdvanceBean);
		assertNotNull(lsAmount);
	}
	
	/**Below are the JUnits for enhancement 6262
	 * service FinancialsBudgetService
	 * 
	 * */
	@Test(expected=Exception.class)
	public void testFetchContractAmountForValidation3() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		String lsAmount = null;
		lsAmount = (String)loFinancialsBudgetService.fetchContractAmountForValidation(moMyBatisSession,null);
		assertNotNull(lsAmount);
	}
	
	/**Below are the JUnits for enhancement 6262
	 * service FinancialsBudgetService
	 * 
	 * */
	@Test(expected=ApplicationException.class)
	public void testFetchContractAmountForValidation4() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		BudgetAdvanceBean loBudgetAdvanceBean = new BudgetAdvanceBean();
		loBudgetAdvanceBean.setBudgetId("12753");
		String lsAmount = null;
		lsAmount = (String)loFinancialsBudgetService.fetchContractAmountForValidation(null,loBudgetAdvanceBean);
		assertNotNull(lsAmount);
	}
	
	/**Below are the JUnits for enhancement 6262
	 * service FinancialsBudgetService
	 * 
	 * */
	@Test
	public void testFetchContractAmountForValidation5() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		BudgetAdvanceBean loBudgetAdvanceBean = new BudgetAdvanceBean();
		loBudgetAdvanceBean.setBudgetId("12744");
		String lsAmount = null;
		lsAmount = (String)loFinancialsBudgetService.fetchContractAmountForValidation(moMyBatisSession,loBudgetAdvanceBean);
		assertNotNull(lsAmount);
	}
	
	/**Below are the JUnits for enhancement 6361
	 * service TaskService
	 * 
	 * */
	@Test
	public void testFetchSelectedUserAssignedLevel() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		List<StaffDetails> loStaffDetailsList = null;
		HashMap<String,String> loFilterMap = new HashMap<String, String>();
		loStaffDetailsList = (List<StaffDetails>)loTaskService.fetchSelectedUserAssignedLevel(moMyBatisSession,loFilterMap,"","");
		assertNull(loStaffDetailsList);
	}
	
	/**Below are the JUnits for enhancement 6361
	 * service TaskService
	 * 
	 * */
	@Test(expected=ApplicationException.class)
	public void testFetchSelectedUserAssignedLevelException() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		HashMap<String,String> loFilterMap = new HashMap<String, String>();
		loFilterMap.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO, "Unassigned Level");
		loTaskService.fetchSelectedUserAssignedLevel(null,loFilterMap,"","");
	}
	
	/**JUnit for enhancement 5684
	 * service ConfigurationService
	 * 
	 * */
	@Test
	public void testFetchProcCOFStatus() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		String loStatus = loConfigurationService.fetchProcCOFStatus(moMyBatisSession, "3392");
		assertNotNull(loStatus);
		
	}
	
	/**JUnit for enhancement 5684
	 * service ConfigurationService
	 * 
	 * */
	@Test(expected=ApplicationException.class)
	public void testFetchProcCOFStatusException() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchProcCOFStatus(null, "3392");
	}
	
	/**JUnit for enhancement 5684
	 * service ConfigurationService
	 * 
	 * */
	@Test
	public void testFetchPCOFTaskCount() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		P8UserSession loUserSession=getFileNetSession();
		Boolean lbTaskLaunch = loConfigurationService.fetchPCOFTaskCount(loUserSession, moMyBatisSession, "2307");
		
	}
	
	/**Below are the JUnits for enhancement 6361
	 * service TaskService
	 * 
	 * */
	@Test(expected=ApplicationException.class)
	public void testFetchPCOFTaskCountException() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		P8UserSession loUserSession=getFileNetSession();
		Boolean lbTaskLaunch = loConfigurationService.fetchPCOFTaskCount(loUserSession, null, "2307");
	}
	
	/**JUnit for enhancement 6361
	 * service TaskService
	 * 
	 * */
	@Test
	public void testFetchSelectedUserAssignedLevel1() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		HashMap<String,String> loFilterMap = new HashMap<String, String>();
		loFilterMap.put(HHSConstants.PROPERTY_PE_ASSIGNED_TO, HHSConstants.ALL_STAFF);
		loTaskService.fetchSelectedUserAssignedLevel(moMyBatisSession, loFilterMap, "DOC", "2");
		
	}
	
}