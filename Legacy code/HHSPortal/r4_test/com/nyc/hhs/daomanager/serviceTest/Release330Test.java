package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.batch.bulkupload.BulkUploadBatch;
import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.FinancialsBudgetService;
import com.nyc.hhs.daomanager.service.FinancialsInvoiceListService;
import com.nyc.hhs.daomanager.service.FinancialsListService;
import com.nyc.hhs.daomanager.service.PaymentListService;
import com.nyc.hhs.daomanager.service.PaymentModuleService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.cache.BaseCacheManagerWeb;
import com.nyc.hhs.frameworks.cache.ICacheManager;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.junit.util.JUnitUtil;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.InvoiceList;
import com.nyc.hhs.model.PaymentSortAndFilter;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.taskhandlersTest.AdvancePaymentReviewTaskHandlerTest;
import com.nyc.hhs.util.HHSUtil;
import com.nyc.hhs.util.PropertyLoader;
import com.nyc.hhs.util.XMLUtil;

public class Release330Test
{
	private static SqlSession moSession = null; // SQL Session
	
	private InvoiceList moInvoiceList;
	
	@Before
	public void setUp() throws Exception
	{
		moInvoiceList = new InvoiceList();
		moInvoiceList.setInvoiceNumber("123");
		moInvoiceList.setDateSubmittedTo("dateSubmittedTo");
		moInvoiceList.setInvoiceNumber("123");
		moInvoiceList.setInvoiceNumber("123");
		moInvoiceList.setAgency("accenture");
		moInvoiceList.setOrgId("accenture");

		moFinancialsInvoiceListService = new FinancialsInvoiceListService();
	}
	
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

	private FinancialsInvoiceListService moFinancialsInvoiceListService;
	private static FinancialsListService loFinancialsListService = null;

	@Test
	public void testDeleteInvoiceList() throws ApplicationException
	{
		String lsInvoiceNumber = "5000";
		Boolean loAuthStatusFlag = true;
		HashMap loHmDocReqProps = new HashMap();
		loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
		moFinancialsInvoiceListService.deleteInvoiceList(getFileNetSession(),lsInvoiceNumber, moSession, loAuthStatusFlag,loHmDocReqProps);
		assertEquals("Invoice deleted succesfully", moFinancialsInvoiceListService.getMoState().toString());

	}

	@Test
	public void testDeleteInvoiceListException() throws ApplicationException
	{
		String lsInvoiceNumber = "5000";
		Boolean loAuthStatusFlag = null;
		HashMap loHmDocReqProps = new HashMap();
		loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
		moFinancialsInvoiceListService.deleteInvoiceList(getFileNetSession(),lsInvoiceNumber, moSession, loAuthStatusFlag,loHmDocReqProps);
		assertEquals("User Login Credentials Failed", moFinancialsInvoiceListService.getMoState().toString());

	}

	@Test(expected = ApplicationException.class)
	public void testDeleteInvoiceListException2() throws ApplicationException
	{
		String lsInvoiceNumber = "5000";
		Boolean loAuthStatusFlag = true;
		HashMap loHmDocReqProps = new HashMap();
		loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
		moFinancialsInvoiceListService.deleteInvoiceList(getFileNetSession(),lsInvoiceNumber, null, loAuthStatusFlag,loHmDocReqProps);
		assertEquals("Invoice deleted succesfully", moFinancialsInvoiceListService.getMoState().toString());

	}
	
	@Test
	public void testDeleteInvoiceListException3() throws ApplicationException
	{
		String lsInvoiceNumber = "5000";
		Boolean loAuthStatusFlag = false;
		HashMap loHmDocReqProps = new HashMap();
		loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
		moFinancialsInvoiceListService.deleteInvoiceList(getFileNetSession(),lsInvoiceNumber, null, loAuthStatusFlag,loHmDocReqProps);
		assertEquals("User Login Credentials Failed", moFinancialsInvoiceListService.getMoState().toString());

	}
	
	@Test
	public void testDeleteInvoiceListException4() throws ApplicationException
	{
		String lsInvoiceNumber = "5000";
		Boolean loAuthStatusFlag = false;
		HashMap loHmDocReqProps = new HashMap();
		loHmDocReqProps.put(P8Constants.PROPERTY_CE_DOC_LINK_TO_APPLICATION, false);
		moFinancialsInvoiceListService.deleteInvoiceList(getFileNetSession(),lsInvoiceNumber, moSession, loAuthStatusFlag,loHmDocReqProps);
		assertEquals("User Login Credentials Failed", moFinancialsInvoiceListService.getMoState().toString());

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
	private ContractList setContractListFilterParams()
	{
		ContractList loContractList = new ContractList();
		List<String> loContractStatusList = new ArrayList<String>();
		loContractStatusList.add("61");
		loContractStatusList.add("67");
		loContractStatusList.add("58");
		loContractStatusList.add("60");
		loContractStatusList.add("59");
		loContractStatusList.add("62");
		loContractList.setContractStatusList(loContractStatusList);
		loContractList.setStartNode(1);
		loContractList.setEndNode(5);
		loContractList.setFirstSort(HHSConstants.STATUS_PROCESS_TYPE_ID);
		loContractList.setSecondSort(HHSConstants.LAST_UPDATE_DATE);
		loContractList.setFirstSortDate(true);
		loContractList.setSecondSortDate(true);
		loContractList.setFirstSortType(HHSConstants.ASCENDING);
		loContractList.setSecondSortType(HHSConstants.DESCENDING);
		loContractList.setSortColumnName(HHSConstants.CONTRACT_STATUS);
		loContractList.setOrgName("accenture");
		loContractList.setOrgType("city_org");
		return loContractList;
	}

	

	@Test
	public void testFetchContractListProvider1() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider12() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider13() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider14() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider15() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider16() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider17() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider18() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider19() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListProvider20() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListAgency21() throws ApplicationException
	{
		Boolean lbThrown = false;
		FinancialsListService loFinancialsListService = new FinancialsListService();

		// Positive Scenario -- List of loContractList type returned
		// for CITY
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.AGENCY_ORG);
		loContractList.setOrgName("DOC");
		List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.AGENCY_ORG);
		assertTrue(loContractListReturned.size() != 0);
	}

	@Test
	public void testFetchContractListCity1() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();

			// Positive Scenario -- List of loContractList type returned
			// for CITY
			ContractList loContractList = setContractListFilterParams();

			List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
					loContractList, ApplicationConstants.PROVIDER_ORG);
			assertTrue(loContractListReturned.size() != 0);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	

	@Test
	public void testFetchContractListAgency9() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			ContractList loContractList = setContractListFilterParams();
			loContractList.setOrgName("DOC");
			List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(null,
					loContractList, ApplicationConstants.AGENCY_ORG);
			assertTrue(loContractListReturned.size() != 0);

			loContractList.setOrgName("DHS");
			loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession, loContractList,
					ApplicationConstants.AGENCY_ORG);
			assertTrue(loContractListReturned.size() == 0);

			// Negative Scenario -- Application Exception handled by setting
			// incomplete data in the Bean

			loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession, null,
					ApplicationConstants.AGENCY_ORG);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchContractListAgency14() throws ApplicationException
	{
		Boolean lbThrown = false;

		try
		{
			FinancialsListService loFinancialsListService = new FinancialsListService();
			ContractList loContractList = setContractListFilterParams();
			loContractList.setOrgName("accenture");

			List<ContractList> loContractListReturned = loFinancialsListService.fetchContractListSummary(moSession,
					loContractList, ApplicationConstants.AGENCY_ORG);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
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
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceListSummaryForException() throws ApplicationException
	{
		moInvoiceList.setOrgType(ApplicationConstants.CITY_ORG);
		moInvoiceList = (InvoiceList) moFinancialsInvoiceListService.fetchInvoiceListSummary(null, moInvoiceList,
				ApplicationConstants.CITY_ORG);
	}
	
	/**
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceListSummaryForException4() throws ApplicationException
	{
		moInvoiceList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		moInvoiceList.setOrgId(null);
		moInvoiceList = (InvoiceList) moFinancialsInvoiceListService.fetchInvoiceListSummary(moSession, moInvoiceList,
				null);
	}
	
	/**
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceListSummaryForException2() throws ApplicationException
	{
		moInvoiceList.setOrgType(ApplicationConstants.AGENCY_ORG);
		moInvoiceList = (InvoiceList) moFinancialsInvoiceListService.fetchInvoiceListSummary(null, moInvoiceList,
				ApplicationConstants.AGENCY_ORG);
	}
	
	/**
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceListSummaryForException3() throws ApplicationException
	{
		moInvoiceList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		moInvoiceList = (InvoiceList) moFinancialsInvoiceListService.fetchInvoiceListSummary(null, moInvoiceList,
				ApplicationConstants.PROVIDER_ORG);
	}
	
	/**
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = NullPointerException.class)
	public void testFetchInvoiceListSummaryForException5() throws ApplicationException
	{
		moInvoiceList = (InvoiceList) moFinancialsInvoiceListService.fetchInvoiceListSummary(moSession, null,
				ApplicationConstants.PROVIDER_ORG);
	}
	
	/**
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceListSummaryForException6() throws ApplicationException
	{
		moInvoiceList.setOrgType(ApplicationConstants.AGENCY_ORG);
		moInvoiceList.setAgency(null);
		moInvoiceList = (InvoiceList) moFinancialsInvoiceListService.fetchInvoiceListSummary(moSession, moInvoiceList,
				null);
	}
	PaymentListService moPaymentListService = new PaymentListService();
	/**
	 * Method used to set the required fields in the bean for testing scenarios
	 * 
	 * This method to be used while testing positive scenarios only. If these
	 * required fields not set on the Bean, AppEx is thrown (Consider while
	 * writing Exception Handling Junit cases)
	 * 
	 * @return
	 */
	private PaymentSortAndFilter setPaymentSortAndFilterBean()
	{
		PaymentSortAndFilter loPaymentSortAndFilter = new PaymentSortAndFilter();
		loPaymentSortAndFilter.setStartNode(1);
		loPaymentSortAndFilter.setEndNode(5);
		loPaymentSortAndFilter.setFirstSort(HHSConstants.DISBURSEMENT_DATE);
		loPaymentSortAndFilter.setSecondSort(HHSConstants.PAYMENT_VOUCHER_NUMBER);
		loPaymentSortAndFilter.setFirstSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setSecondSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setAgency("DOC");
		loPaymentSortAndFilter.getPaymentStatusList().add("65");
		loPaymentSortAndFilter.setOrgId("accenture");
		return loPaymentSortAndFilter;
	}

	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a CITY User
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPaymentListSummaryOrgNullSuccess() throws ApplicationException
	{
			// Positive Scenario -- List of loPaymentSortAndFilter type returned
			// for org type null
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(null);
			List<PaymentSortAndFilter> loPaymentList = moPaymentListService.fetchPaymentListSummary(moSession,
					loPaymentSortAndFilter1);
			assertNull(loPaymentList);
	}
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a CITY User
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPaymentListSummaryOrgCitySucess() throws ApplicationException
	{
			// Positive Scenario -- List of loPaymentSortAndFilter type returned
			// for CITY
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.CITY_ORG);
			List<PaymentSortAndFilter> loPaymentList = moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter1);
			assertTrue(loPaymentList.size() != 0);

	}
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a CITY User
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPaymentListSummaryOrgCityWithPaymentValueFromComma() throws ApplicationException
	{
			// Positive Scenario -- List of loPaymentSortAndFilter type returned
			// for CITY
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.CITY_ORG);
			loPaymentSortAndFilter1.setPaymentValueFrom("10,000");
			List<PaymentSortAndFilter> loPaymentList = moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter1);
			assertTrue(loPaymentList != null);
	}
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a CITY User
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPaymentListSummaryOrgCityWithPaymentValueToComma() throws ApplicationException
	{
			// Positive Scenario -- List of loPaymentSortAndFilter type returned
			// for CITY
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.CITY_ORG);
			loPaymentSortAndFilter1.setPaymentValueTo("10,000");
			List<PaymentSortAndFilter> loPaymentList = moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter1);
			assertTrue(loPaymentList != null);
	}
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a CITY User
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchPaymentListSummaryCityException() throws ApplicationException
	{
			PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
			loPaymentSortAndFilter2.setOrgType(ApplicationConstants.CITY_ORG);
			moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter2);
	}

	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Provider User
	 * 
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchPaymentListSummaryOrgProviderPaymentValueFromToSuccess() throws ApplicationException
	{
			PaymentSortAndFilter loPaymentSortAndFilter1 = setPaymentSortAndFilterBean();
			loPaymentSortAndFilter1.setOrgType(ApplicationConstants.PROVIDER_ORG);
			loPaymentSortAndFilter1.setPaymentValueFrom("10,000");
			loPaymentSortAndFilter1.setPaymentValueTo("10,000");
			List<PaymentSortAndFilter> loPaymentList = moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter1);
			assertTrue(loPaymentList != null);
	}
	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Provider User
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchPaymentListSummaryProviderException() throws ApplicationException
	{		
			// Negative Scenario -- Application Exception handled by setting
			// incomplete data in the Bean
			PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
			loPaymentSortAndFilter2.setOrgType(ApplicationConstants.PROVIDER_ORG);
			moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter2);
	}
	
	

	
	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Agency User
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchPaymentListSummaryAgencyException() throws ApplicationException
	{
			// Negative Scenario -- Application Exception handled by setting
			// incomplete data in the Bean
			PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
			loPaymentSortAndFilter2.setOrgType(ApplicationConstants.AGENCY_ORG);
			moPaymentListService.fetchPaymentListSummary(moSession, loPaymentSortAndFilter2);
	}

	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Agency User
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchPaymentListSummaryAgencyDbDownException() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
		loPaymentSortAndFilter2.setOrgType(ApplicationConstants.AGENCY_ORG);
		moPaymentListService.fetchPaymentListSummary(null, loPaymentSortAndFilter2);

	}

	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Agency User
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchPaymentListSummaryCityDbDownException() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
		loPaymentSortAndFilter2.setOrgType(ApplicationConstants.CITY_ORG);
		moPaymentListService.fetchPaymentListSummary(null, loPaymentSortAndFilter2);
	}

	/**
	 * This method test whether Payment List Summary is fetched successfully or
	 * not for a Agency User
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchPaymentListSummaryProviderDbDownException() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter2 = new PaymentSortAndFilter();
		loPaymentSortAndFilter2.setOrgType(ApplicationConstants.PROVIDER_ORG);
		moPaymentListService.fetchPaymentListSummary(null, loPaymentSortAndFilter2);
	}

}