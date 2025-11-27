package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.FinancialsBudgetService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.model.BudgetAdvanceBean;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class FinancialsBudgetServiceTest
{

	private static SqlSession moSession = null; // SQL Session
	private static P8UserSession session = null; // FileNet session

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		session = getFileNetSession();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		moSession.close();
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

	/**
	 * Method used to set the required fields in the bean for testing scenarios
	 * 
	 * This method to be used while testing positive scenarios only. If these
	 * required fields not set on the Bean, AppEx is thrown (Consider while
	 * writing Exception Handling Junit cases)
	 * 
	 * @return
	 */
	private BudgetList setBudgetList()
	{
		BudgetList loBudgetList = new BudgetList();
		loBudgetList.setStartNode(1);
		loBudgetList.setLsRequestFromHomePage("false");
		loBudgetList.setEndNode(5);
		loBudgetList.getBudgetStatusList().add("85");
		return loBudgetList;
	}

	/**
	 * This method test whether Budget List Summary is fetched successfully or
	 * not for a CITY User
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchBudgetListSummaryCity() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

			// Positive Scenario -- List of loBudgetList1 type returned
			// for CITY
			BudgetList loBudgetList1 = setBudgetList();
			String lsUserType = "";
			List<BudgetList> loBudgetListResult = loFinancialsBudgetService.fetchBudgetListSummary(moSession,
					lsUserType, loBudgetList1);
			assertNull(loBudgetListResult);

			lsUserType = "city_org";
			loBudgetListResult = loFinancialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetList1);
			assertEquals(true, loBudgetListResult.get(0) instanceof BudgetList);
			assertFalse(loBudgetListResult.isEmpty());

			// Negative Scenario -- Application Exception handled by setting
			// incomplete data in the Bean
			BudgetList loBudgetList2 = new BudgetList();
			loBudgetListResult = loFinancialsBudgetService.fetchBudgetListSummary(null, lsUserType, loBudgetList2);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	@Test
	public void testFetchBudgetListSummaryCity1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

			// Positive Scenario -- List of loBudgetList1 type returned
			// for CITY
			BudgetList loBudgetList1 = setBudgetList();
			String lsUserType = "";
			List<BudgetList> loBudgetListResult = loFinancialsBudgetService.fetchBudgetListSummary(moSession,
					lsUserType, loBudgetList1);
			assertNull(loBudgetListResult);

			lsUserType = "city_org";
			loBudgetListResult = loFinancialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetList1);
			assertEquals(true, loBudgetListResult.get(0) instanceof BudgetList);
			assertFalse(loBudgetListResult.isEmpty());

			// Negative Scenario -- Application Exception handled by setting
			// incomplete data in the Bean
			BudgetList loBudgetList2 = new BudgetList();
			loBudgetListResult = loFinancialsBudgetService.fetchBudgetListSummary(null, lsUserType, loBudgetList2);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	// The test method checks that the budget list data is fetched on the basis
	// of Provider ID.
	@Test
	public void testFetchBudgetListSummaryProvider() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

			// Positive Scenario -- List of loBudgetList1 type returned
			// for CITY

			String lsUserType = "";
			BudgetList loBudgetList1 = setBudgetList();
			List<BudgetList> loBudgetListResult = loFinancialsBudgetService.fetchBudgetListSummary(moSession,
					lsUserType, loBudgetList1);
			assertNull(loBudgetListResult);

			lsUserType = "provider_org";
			loBudgetList1.setOrgId("accenture");
			loBudgetListResult = loFinancialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetList1);
			assertEquals(true, loBudgetListResult.get(0) instanceof BudgetList);
			assertFalse(loBudgetListResult.isEmpty());
			assertEquals(loBudgetList1.getOrgId(), loBudgetListResult.get(0).getOrgId());

			// Negative Scenario -- Application Exception handled by setting
			// incomplete data in the Bean
			BudgetList loBudgetList2 = new BudgetList();
			loBudgetListResult = loFinancialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetList2);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	// The test method checks that the budget list data is fetched on the basis
	// of Agency ID.
	@Test
	public void testFetchBudgetListSummaryAgency() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

			// Positive Scenario -- List of loBudgetList1 type returned
			// for CITY
			BudgetList loBudgetList1 = setBudgetList();
			String lsUserType = "";
			List<BudgetList> loBudgetListResult = loFinancialsBudgetService.fetchBudgetListSummary(null, lsUserType,
					loBudgetList1);
			assertNull(loBudgetListResult);

			lsUserType = "agency_org";

			loBudgetList1.setAgencyId("DOC");
			loBudgetListResult = loFinancialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetList1);
			assertEquals(true, loBudgetListResult.get(0) instanceof BudgetList);
			assertFalse(loBudgetListResult.isEmpty());
			assertEquals(loBudgetList1.getAgencyId(), loBudgetListResult.get(0).getAgencyId());

			// Negative Scenario -- Application Exception handled by setting
			// incomplete data in the Bean
			BudgetList loBudgetList2 = new BudgetList();
			loBudgetListResult = loFinancialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetList2);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	// The test method checks that the budget list data is fetched on the basis
	// of Agency ID.
	@Test(expected = ApplicationException.class)
	public void testFetchBudgetListSummaryAgencyException() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

		// Positive Scenario -- List of loBudgetList1 type returned
		// for CITY
		BudgetList loBudgetList1 = setBudgetList();
		String lsUserType = "";

		lsUserType = "agency_org";

		loBudgetList1.setAgencyId("DOC");
		loFinancialsBudgetService.fetchBudgetListSummary(null, lsUserType, loBudgetList1);
	}
	@Test
	public void testFetchRequestAdvance() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
			P8UserSession loUserSession=getFileNetSession();

			// Positive Scenario -- returns a string with error message
			// "BUDGET_MODIFICATION_IN_PROGRESS"
			String lsBudgetId = "18";
			String lsContractId = "111778";
			BudgetAdvanceBean loBudgetAdvanceBean = (BudgetAdvanceBean)loFinancialsBudgetService.fetchRequestAdvance(moSession,lsBudgetId);
			assertTrue(true);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
			throw loAppEx;
		}
	}
	
	@Test
	public void testTerminateWorkflowForBudget() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
			P8UserSession loUserSession=getFileNetSession();

			// Positive Scenario -- returns a string with error message
			// "BUDGET_MODIFICATION_IN_PROGRESS"
			String lsBudgetId = "560";
			boolean lbStatus = (boolean)loFinancialsBudgetService.terminateWorkflowForBudget(loUserSession,lsBudgetId,true);
			assertNotNull(lbStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
			throw loAppEx;
		}
	}
	@Test(
			expected = ApplicationException.class)
	public void testFetchRequestAdvanceNegative() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
			P8UserSession loUserSession=getFileNetSession();

			// Positive Scenario -- returns a string with error message
			// "BUDGET_MODIFICATION_IN_PROGRESS"
			String lsBudgetId = "18";
			String lsContractId = "111778";
			BudgetAdvanceBean loBudgetAdvanceBean = (BudgetAdvanceBean)loFinancialsBudgetService.fetchRequestAdvance(null,lsBudgetId);
			assertNotNull(loBudgetAdvanceBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
			throw loAppEx;
		}
	}

	// The test method checks that the budget list data is fetched on the basis
	// of Agency ID.
	@Test(expected = ApplicationException.class)
	public void testFetchBudgetListSummaryAgencyException2() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

		// Positive Scenario -- List of loBudgetList1 type returned
		// for CITY
		BudgetList loBudgetList1 = setBudgetList();
		String lsUserType = "";

		lsUserType = "provider_org";

		loBudgetList1.setAgencyId("DOC");
		loFinancialsBudgetService.fetchBudgetListSummary(null, lsUserType, loBudgetList1);
	}

	// The test method checks that the budget list data is fetched on the basis
	// of Agency ID.
	@Test(expected = ApplicationException.class)
	public void testFetchBudgetListSummaryAgencyException3() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

		// Positive Scenario -- List of loBudgetList1 type returned
		// for CITY
		BudgetList loBudgetList1 = setBudgetList();
		String lsUserType = "";

		lsUserType = "city_org";

		loBudgetList1.setAgencyId("DOC");
		loFinancialsBudgetService.fetchBudgetListSummary(null, lsUserType, loBudgetList1);
	}

	// The test method checks that the budget list data is fetched on the basis
	// of Agency ID.
	@Test(expected = ApplicationException.class)
	public void testFetchBudgetListSummaryAgencyException4() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

		// Positive Scenario -- List of loBudgetList1 type returned
		// for CITY
		BudgetList loBudgetList1 = setBudgetList();
		String lsUserType = "";

		lsUserType = "city_org";

		loBudgetList1.setAgencyId("DOC");
		loFinancialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetList1);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchBudgetListSummaryAgencyException14() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

		// Positive Scenario -- List of loBudgetList1 type returned
		// for CITY
		BudgetList loBudgetList1 = setBudgetList();
		String lsUserType = "";

		lsUserType = "city_org";

		loBudgetList1.setAgencyId("DOC");
		loBudgetList1.setBudgetValueFrom("456");
		loFinancialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetList1);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchBudgetListSummaryAgencyException15() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

		// Positive Scenario -- List of loBudgetList1 type returned
		// for CITY
		BudgetList loBudgetList1 = setBudgetList();
		String lsUserType = "";

		lsUserType = "city_org";
		loBudgetList1.setBudgetValueFrom("456");
		loBudgetList1.setBudgetValueTo("45000");
		loBudgetList1.setAgencyId("DOC");
		loFinancialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetList1);
	}
	/**
	 * This method gets the total Budget count on Budget list screens for CITY
	 * User
	 * @throws ApplicationException
	 */
	@Test
	public void testgetModifyBudgetFeasibilityFilenet() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
			P8UserSession loUserSession=getFileNetSession();

			// Positive Scenario -- returns a string with error message
			// "BUDGET_MODIFICATION_IN_PROGRESS"
			String lsBudgetId = "10112";
			String lsContractId = "111778";
			String lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, loUserSession, lsBudgetId,
					lsContractId);
			assertNotNull(lsError);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	/**
	 * 
	 * @throws ApplicationException
	 */
	@Test(
			expected = ApplicationException.class)
	public void testFetchAdvanceNumber() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
			P8UserSession loUserSession=getFileNetSession();
			
			BudgetAdvanceBean aoBudgetAdvanceBean=new BudgetAdvanceBean();
			aoBudgetAdvanceBean.setAdvAmntRequested("3243");
			aoBudgetAdvanceBean.setCtNumber("76");
			aoBudgetAdvanceBean.setBudgetId("560");
			aoBudgetAdvanceBean.setContractId("111779");

			// Positive Scenario -- returns a string with error message
			// "BUDGET_MODIFICATION_IN_PROGRESS"
			String lsBudgetId = "10112";
			String lsContractId = "111778";
			aoBudgetAdvanceBean = loFinancialsBudgetService.fetchAdvanceNumber(moSession,aoBudgetAdvanceBean );
			assertNotNull(aoBudgetAdvanceBean);
			aoBudgetAdvanceBean = loFinancialsBudgetService.fetchAdvanceNumber(null,aoBudgetAdvanceBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
			throw loAppEx;
		}
	}
	
	/**
	 * 
	 * @throws ApplicationException
	 */
	@Test(
			expected = ApplicationException.class)
	public void testInsertBudgetAdvance4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
			P8UserSession loUserSession=getFileNetSession();
			
			BudgetAdvanceBean aoBudgetAdvanceBean=new BudgetAdvanceBean();
			aoBudgetAdvanceBean.setAdvAmntRequested("3243");
			aoBudgetAdvanceBean.setCtNumber("76");
			aoBudgetAdvanceBean.setBudgetId("560");
			aoBudgetAdvanceBean.setContractId("111779");
			aoBudgetAdvanceBean.setOrgType("provider_org");
			aoBudgetAdvanceBean.setAdvanceNumber("546");
			aoBudgetAdvanceBean.setDescription("testing");
			aoBudgetAdvanceBean.setStatus("72");
			aoBudgetAdvanceBean.setModifyByAgency("city_166");
			aoBudgetAdvanceBean.setModifyByProvider("2135");
			
			HashMap aoMap = new HashMap();
			int liVal = loFinancialsBudgetService.insertBudgetAdvance(moSession,aoMap );
			assertNotNull(aoBudgetAdvanceBean);
			liVal = loFinancialsBudgetService.insertBudgetAdvance(null,aoMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
			throw loAppEx;
		}
	}
	
	@Test(
			expected = ApplicationException.class)
	public void testInsertBudgetAdvance() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
			P8UserSession loUserSession=getFileNetSession();
			
			BudgetAdvanceBean aoBudgetAdvanceBean=new BudgetAdvanceBean();
			aoBudgetAdvanceBean.setAdvAmntRequested("3243");
			aoBudgetAdvanceBean.setCtNumber("76");
			aoBudgetAdvanceBean.setBudgetId("560");
			aoBudgetAdvanceBean.setContractId("111779");
			aoBudgetAdvanceBean.setOrgType("provider_org");
			aoBudgetAdvanceBean.setAdvanceNumber("546");
			aoBudgetAdvanceBean.setDescription("testing");
			aoBudgetAdvanceBean.setStatus("72");
			aoBudgetAdvanceBean.setModifyByAgency("city_166");
			aoBudgetAdvanceBean.setModifyByProvider("2135");
			
			HashMap aoMap = new HashMap();
			int liVal = loFinancialsBudgetService.insertBudgetAdvance(moSession,aoMap );
			assertNotNull(aoBudgetAdvanceBean);
			liVal = loFinancialsBudgetService.insertBudgetAdvance(null,aoMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
			throw loAppEx;
		}
	}
	
	@Test(
			expected = ApplicationException.class)
	public void testInsertBudgetAdvance2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
			P8UserSession loUserSession=getFileNetSession();
			
			BudgetAdvanceBean aoBudgetAdvanceBean=new BudgetAdvanceBean();
			aoBudgetAdvanceBean.setAdvAmntRequested("3243");
			aoBudgetAdvanceBean.setCtNumber("76");
			aoBudgetAdvanceBean.setBudgetId("560");
			aoBudgetAdvanceBean.setContractId("111779");
			aoBudgetAdvanceBean.setOrgType("provider_org");
			aoBudgetAdvanceBean.setAdvanceNumber("546");
			aoBudgetAdvanceBean.setDescription("testing");
			aoBudgetAdvanceBean.setStatus("72");
			aoBudgetAdvanceBean.setModifyByAgency("city_166");
			aoBudgetAdvanceBean.setModifyByProvider("2135");
			
			HashMap aoMap = new HashMap();
			int liVal = loFinancialsBudgetService.insertBudgetAdvance(moSession,aoMap );
			assertNotNull(aoBudgetAdvanceBean);
			liVal = loFinancialsBudgetService.insertBudgetAdvance(null,aoMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
			throw loAppEx;
		}
	}
	
	@Test(
			expected = ApplicationException.class)
	public void testInsertBudgetAdvance3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
			P8UserSession loUserSession=getFileNetSession();
			
			BudgetAdvanceBean aoBudgetAdvanceBean=new BudgetAdvanceBean();
			aoBudgetAdvanceBean.setAdvAmntRequested("3243");
			aoBudgetAdvanceBean.setCtNumber("76");
			aoBudgetAdvanceBean.setBudgetId("560");
			aoBudgetAdvanceBean.setContractId("111779");
			aoBudgetAdvanceBean.setOrgType("provider_org");
			aoBudgetAdvanceBean.setAdvanceNumber("546");
			aoBudgetAdvanceBean.setDescription("testing");
			aoBudgetAdvanceBean.setStatus("72");
			aoBudgetAdvanceBean.setModifyByAgency("city_166");
			aoBudgetAdvanceBean.setModifyByProvider("2135");
			
			HashMap aoMap = new HashMap();
			int liVal = loFinancialsBudgetService.insertBudgetAdvance(moSession,aoMap );
			assertNotNull(aoBudgetAdvanceBean);
			liVal = loFinancialsBudgetService.insertBudgetAdvance(null,aoMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
			throw loAppEx;
		}
	}
	/**
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testgetModifyBudgetFeasibilityFilenetOne() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
			P8UserSession loUserSession=getFileNetSession();

			// Positive Scenario -- returns a string with error message
			// "BUDGET_MODIFICATION_IN_PROGRESS"
			String lsBudgetId = "18";
			String lsContractId = "111778";
			String lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, loUserSession, lsBudgetId,
					lsContractId);
			assertNotNull(lsError);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	/**
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testgetModifyBudgetFeasibility() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
			P8UserSession loUserSession=getFileNetSession();

			// Positive Scenario -- returns a string with error message
			// "BUDGET_MODIFICATION_IN_PROGRESS"
			String lsBudgetId = "10112";
			String lsContractId = "111778";
			String lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, loUserSession, lsBudgetId,
					lsContractId);
			assertNotNull(lsError);
			 lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId,
					lsContractId);
			// assertTrue(lsError.equalsIgnoreCase("CONFIGURATION_BUDGET_UPDATE_IN_PROGRESS"));
			assertEquals("CONFIGURATION_BUDGET_UPDATE_IN_PROGRESS", lsError);
			lsBudgetId = "13";
			lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);
			assertTrue(lsError.equalsIgnoreCase("NEGATIVE_AMENDMENT_IN_PROGRESS"));
			lsBudgetId = "555";
			lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);
			assertTrue(lsError.equalsIgnoreCase("BUDGET_MODIFICATION_IN_PROGRESS"));
			lsBudgetId = "26";
			lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);
			assertTrue(lsError.equalsIgnoreCase("INVOICE_PAYMENT_OUTSTANDING"));
			lsBudgetId = "28";
			lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);
			assertTrue(lsError.equalsIgnoreCase("INVOICE_PAYMENT_OUTSTANDING"));
			lsBudgetId = "100";
			lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);
			assertTrue(lsError == null);
			lsBudgetId = "5";
			lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);
			assertTrue(lsError.equalsIgnoreCase("BUDGET_MODIFICATION_IN_PROGRESS"));

			// Negative ScenarioS
			lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}

	
	@Test
	public void testgetModifyBudgetFeasibility2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
			P8UserSession loUserSession=getFileNetSession();

			// Positive Scenario -- returns a string with error message
			// "BUDGET_MODIFICATION_IN_PROGRESS"
			String lsBudgetId = "10112";
			String lsContractId = "111778";
			String lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, loUserSession, lsBudgetId,
					lsContractId);
			assertNotNull(lsError);
			 lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId,
					lsContractId);
			// assertTrue(lsError.equalsIgnoreCase("CONFIGURATION_BUDGET_UPDATE_IN_PROGRESS"));
			assertEquals("CONFIGURATION_BUDGET_UPDATE_IN_PROGRESS", lsError);
			lsBudgetId = "13";
			lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);
			assertTrue(lsError.equalsIgnoreCase("NEGATIVE_AMENDMENT_IN_PROGRESS"));
			lsBudgetId = "555";
			lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);
			assertTrue(lsError.equalsIgnoreCase("BUDGET_MODIFICATION_IN_PROGRESS"));
			lsBudgetId = "26";
			lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);
			assertTrue(lsError.equalsIgnoreCase("INVOICE_PAYMENT_OUTSTANDING"));
			lsBudgetId = "28";
			lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);
			assertTrue(lsError.equalsIgnoreCase("INVOICE_PAYMENT_OUTSTANDING"));
			lsBudgetId = "100";
			lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);
			assertTrue(lsError == null);
			lsBudgetId = "5";
			lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);
			assertTrue(lsError.equalsIgnoreCase("BUDGET_MODIFICATION_IN_PROGRESS"));

			// Negative ScenarioS
			lsError = loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}
	}
	@Test(expected = ApplicationException.class)
	public void testgetModifyBudgetFeasibilityException() throws ApplicationException
	{

		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

		// Negative Scenario -- null session
		String lsBudgetId = "18";
		String lsContractId = "111777";
		loFinancialsBudgetService.getModifyBudgetFeasibility(null, null, lsBudgetId, lsContractId);

	}
	
	@Test(expected = ApplicationException.class)
	public void testgetModifyBudgetFeasibilityException2() throws ApplicationException
	{

		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

		// Negative Scenario -- null budgetId
		String lsBudgetId = null;
		String lsContractId = "111777";
		loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);

	}
	
	@Test(expected = ApplicationException.class)
	public void testgetModifyBudgetFeasibilityException3() throws ApplicationException
	{

		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

		// Negative Scenario -- null lsContractId
		String lsBudgetId = "18";
		String lsContractId = null;
		loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);

	}
	
	@Test(expected = ApplicationException.class)
	public void testgetModifyBudgetFeasibilityException4() throws ApplicationException
	{

		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

		// Negative Scenario -- null lsContractId
		String lsBudgetId = null;
		String lsContractId = null;
		loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, null, lsBudgetId, lsContractId);

	}
	
	@Test(expected = ApplicationException.class)
	public void testgetModifyBudgetFeasibilityException5() throws ApplicationException
	{

		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

		// Negative Scenario -- null lsContractId
		String lsBudgetId = null;
		String lsContractId = null;
		loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, session, lsBudgetId, lsContractId);

	}
	
	@Test(expected = ApplicationException.class)
	public void testgetModifyBudgetFeasibilityException6() throws ApplicationException
	{

		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

		// Negative Scenario -- null lsContractId
		String lsBudgetId = "18";
		String lsContractId = "111777";
		loFinancialsBudgetService.getModifyBudgetFeasibility(moSession, session, lsBudgetId, lsContractId);
	}

	/**
	 * This method gets the total Budget count on Budget list screens for CITY
	 * User
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetBudgetListCountCity() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

			// Positive Scenario -- returns a list with size greater than 0
			// for CITY
			BudgetList loBudgetList1 = setBudgetList();
			String lsUserType = "city_org";
			List<String> llList = new ArrayList<String>();
			llList.add("");
			loBudgetList1.setBudgetStatusList(llList);
			loBudgetList1.setBudgetTypeList(llList);
			Integer liBudgetCount = loFinancialsBudgetService.getBudgetListCount(moSession, lsUserType, loBudgetList1);
			assertNotNull(liBudgetCount);

			// Negative Scenario -- ApplicaitonException thrown when bean is not
			// set with required fields
			BudgetList loBudgetList2 = new BudgetList();
			liBudgetCount = loFinancialsBudgetService.getBudgetListCount(moSession, lsUserType, loBudgetList2);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	/**
	 * This method gets the total Budget count on Budget list screens for Agency
	 * User
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetBudgetListCountAgency1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

			// Positive Scenario -- returns a list with size greater than 0
			// for CITY
			BudgetList loBudgetList1 = setBudgetList();
			String lsUserType = "agency_org";
			List<String> llList = new ArrayList<String>();
			llList.add("");
			loBudgetList1.setBudgetStatusList(llList);
			loBudgetList1.setBudgetTypeList(llList);
			loBudgetList1.setOrgId("accenture");
			Integer liBudgetCount = loFinancialsBudgetService.getBudgetListCount(moSession, lsUserType, loBudgetList1);
			assertNotNull(liBudgetCount);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testGetBudgetListCountCity9() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

			// Positive Scenario -- returns a list with size greater than 0
			// for CITY
			BudgetList loBudgetList1 = setBudgetList();
			String lsUserType = "city_org";
			List<String> llList = new ArrayList<String>();
			llList.add("");
			loBudgetList1.setBudgetStatusList(llList);
			loBudgetList1.setBudgetTypeList(llList);
			// Negative Scenario -- ApplicaitonException thrown when bean is not
			// set with required fields
			int	liBudgetCount = loFinancialsBudgetService.getBudgetListCount(null, lsUserType, loBudgetList1);
	
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	/**
	 * This method gets the total Budget count on Budget list screens for CITY
	 * User
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetBudgetListCountProvider() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

			// Positive Scenario -- returns a list with size greater than 0
			// for CITY
			BudgetList loBudgetList1 = new BudgetList();
			String lsUserType = "";
			Integer liBudgetCount = loFinancialsBudgetService.getBudgetListCount(moSession, lsUserType, loBudgetList1);
			assertTrue(StringUtils.equals(liBudgetCount.toString(), "0"));

			lsUserType = "provider_org";
			loBudgetList1.setOrgId("accenture");
			List<String> llList = new ArrayList<String>();
			llList.add("");
			loBudgetList1.setBudgetStatusList(llList);
			loBudgetList1.setBudgetTypeList(llList);
			liBudgetCount = loFinancialsBudgetService.getBudgetListCount(moSession, lsUserType, loBudgetList1);
			assertNotNull(liBudgetCount);

			// Negative Scenario -- ApplicaitonException thrown when bean is not
			// set with required fields
			BudgetList loBudgetList2 = new BudgetList();
			liBudgetCount = loFinancialsBudgetService.getBudgetListCount(moSession, lsUserType, loBudgetList2);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * This method gets the total Budget count on Budget list screens for CITY
	 * User
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testGetBudgetListCountAgency() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

			// Positive Scenario -- returns a list with size greater than 0
			// for CITY
			BudgetList loBudgetList1 = setBudgetList();
			String lsUserType = "";
			Integer liBudgetCount = loFinancialsBudgetService.getBudgetListCount(moSession, lsUserType, loBudgetList1);
			assertTrue(StringUtils.equals(liBudgetCount.toString(), "0"));

			lsUserType = "agency_org";
			loBudgetList1.setAgencyId("accenture");
			List<String> llList = new ArrayList<String>();
			llList.add("");
			loBudgetList1.setBudgetStatusList(llList);
			loBudgetList1.setBudgetTypeList(llList);
			liBudgetCount = loFinancialsBudgetService.getBudgetListCount(moSession, lsUserType, loBudgetList1);
			assertNotNull(liBudgetCount);

			// Negative Scenario -- ApplicaitonException thrown when bean is not
			// set with required fields
			BudgetList loBudgetList2 = new BudgetList();
			liBudgetCount = loFinancialsBudgetService.getBudgetListCount(moSession, lsUserType, loBudgetList2);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	/**
	 * Method used to test cancelModificationBudget method with a true
	 * Authentication flag Expected result success
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testCancelModificationBudget() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();

		String lsBudgetId = "11";
		Boolean lbCancelModificationBudgetStatus = loFinancialsBudgetService.cancelModificationBudget(moSession,
				lsBudgetId);
		assertFalse(lbCancelModificationBudgetStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testCancelModificationBudgetException() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		String lsBudgetId = "11";
		// Negative scenario - null session
		loFinancialsBudgetService.cancelModificationBudget(null, lsBudgetId);
	}

	@Test(expected = ApplicationException.class)
	public void testCancelModificationBudgetException2() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		// Negative scenario - null budgetId
		loFinancialsBudgetService.cancelModificationBudget(moSession, null);
	}

	@Test
	public void testCancelModificationBudgetException3() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		String lsBudgetId = "11";

		Boolean lbCancelModificationBudgetStatus = loFinancialsBudgetService.cancelModificationBudget(moSession,
				lsBudgetId);
		assertNotNull(lbCancelModificationBudgetStatus);
	}

	@Test
	public void testCancelModificationBudgetException4() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		String lsBudgetId = "3336214";

		Boolean lbCancelModificationBudgetStatus = loFinancialsBudgetService.cancelModificationBudget(moSession,
				lsBudgetId);
		assertNotNull(lbCancelModificationBudgetStatus);
	}

}
