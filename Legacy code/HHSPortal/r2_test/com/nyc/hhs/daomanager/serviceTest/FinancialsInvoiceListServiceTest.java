package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.daomanager.service.FinancialsInvoiceListService;
import com.nyc.hhs.daomanager.service.ServiceState;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.InvoiceList;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8dataprovider.P8SecurityOperations;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

/**
 * Test class for FinancialsInvoiceListService
 * @author virender.x.kumar
 */
public class FinancialsInvoiceListServiceTest extends ServiceState
{
	private FinancialsInvoiceListService moFinancialsInvoiceListService;
	/**
	 * InvoiceList object
	 */
	private InvoiceList moInvoiceList;

	private static SqlSession moSession = null; // SQL Session

	/**
	 * sets up the test class
	 * @throws Exception
	 */
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
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
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
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * This method tests if invoices are available in database for Provider.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchInvoiceListSummaryForProvider() throws ApplicationException
	{
		moInvoiceList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		moFinancialsInvoiceListService.fetchInvoiceListSummary(moSession, moInvoiceList,
				ApplicationConstants.PROVIDER_ORG);
		assertNotNull(moInvoiceList);
		assertEquals("Invoice List fetched successfully for org Type:provider_org", moFinancialsInvoiceListService
				.getMoState().toString());
	}

	/**
	 * This method tests if invoices are available in database for agency.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchInvoiceListSummaryForAgency() throws ApplicationException
	{
		moInvoiceList.setOrgType(ApplicationConstants.AGENCY_ORG);
		moFinancialsInvoiceListService.fetchInvoiceListSummary(moSession, moInvoiceList,
				ApplicationConstants.AGENCY_ORG);
		assertNotNull(moInvoiceList);
		assertEquals("Invoice List fetched successfully for org Type:agency_org", moFinancialsInvoiceListService
				.getMoState().toString());
	}

	/**
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchInvoiceListSummaryForAccelerator() throws ApplicationException
	{
		moInvoiceList.setOrgType(ApplicationConstants.CITY_ORG);
		moFinancialsInvoiceListService.fetchInvoiceListSummary(moSession, moInvoiceList, ApplicationConstants.CITY_ORG);
		assertNotNull(moInvoiceList);
		assertEquals("Invoice List fetched successfully for org Type:city_org", moFinancialsInvoiceListService
				.getMoState().toString());
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

	/**
	 * This method gets the invoice count for the particular invoice list object
	 * @throws ApplicationException
	 */
	@Test
	public void testgetInvoiceCount() throws ApplicationException
	{
		List<InvoiceList> loInvoiceList = new ArrayList<InvoiceList>();
		InvoiceList moInvoiceList1 = new InvoiceList();
		InvoiceList moInvoiceList2 = new InvoiceList();

		loInvoiceList.add(moInvoiceList);
		loInvoiceList.add(moInvoiceList1);
		loInvoiceList.add(moInvoiceList2);

		moFinancialsInvoiceListService.getInvoiceCount(moSession, moInvoiceList, ApplicationConstants.PROVIDER_ORG);
		assertEquals("Invoices count fetched successfully for org type:provider_org", moFinancialsInvoiceListService
				.getMoState().toString());

		moFinancialsInvoiceListService.getInvoiceCount(moSession, moInvoiceList, ApplicationConstants.CITY_ORG);
		assertEquals("Invoices count fetched successfully for org type:city_org", moFinancialsInvoiceListService
				.getMoState().toString());

		moFinancialsInvoiceListService.getInvoiceCount(moSession, moInvoiceList, ApplicationConstants.AGENCY_ORG);
		assertEquals("Invoices count fetched successfully for org type:agency_org", moFinancialsInvoiceListService
				.getMoState().toString());
	}

	/**
	 * This method gets the invoice count for the particular invoice list object
	 * and returns Application Exception in case of error
	 * @throws ApplicationException
	 */
	@Test
	public void testgetInvoiceCountCatchException() throws ApplicationException
	{
		InvoiceList loInvoiceList = new InvoiceList();

		loInvoiceList.setOrgType(null);
		Integer liInvoiceCount = moFinancialsInvoiceListService.getInvoiceCount(moSession, loInvoiceList, "abc");

		assertNotNull(liInvoiceCount.toString());

	}

	/**
	 * This method withdraws invoice corresponding to invoice number from
	 * database
	 * @throws ApplicationException
	 */
	@Test
	public void testwithdrawInvoiceList() throws ApplicationException
	{
		String asInvoiceNumber = "5003";
		Boolean loAuthStatusFlag = true;
		moFinancialsInvoiceListService.withdrawInvoiceList(asInvoiceNumber, moSession, loAuthStatusFlag);
		assertEquals("Invoice Withdrawn succesfully", moFinancialsInvoiceListService.getMoState().toString());

		loAuthStatusFlag = false;
		moFinancialsInvoiceListService.withdrawInvoiceList(asInvoiceNumber, moSession, loAuthStatusFlag);
		assertNotSame("User Login Credentials Failed", moFinancialsInvoiceListService.getMoState().toString());
		loAuthStatusFlag = null;
		moFinancialsInvoiceListService.withdrawInvoiceList(asInvoiceNumber, moSession, loAuthStatusFlag);
		assertNotSame("User Login Credentials Failed", moFinancialsInvoiceListService.getMoState().toString());
	}

	/**
	 * This method withdraws invoice corresponding to invoice number from
	 * database
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testwithdrawInvoiceListException() throws ApplicationException
	{
		String asInvoiceNumber = "5003";
		Boolean loAuthStatusFlag = true;

		moFinancialsInvoiceListService.withdrawInvoiceList(asInvoiceNumber, null, loAuthStatusFlag);
	}
	
	/**
	 * This method withdraws invoice corresponding to invoice number from
	 * database
	 * @throws ApplicationException
	 */
	@Test(expected = NumberFormatException.class)
	public void testwithdrawInvoiceListException2() throws ApplicationException
	{
		Boolean loAuthStatusFlag = true;

		moFinancialsInvoiceListService.withdrawInvoiceList(null, moSession, loAuthStatusFlag);
	}

	/**
	 * This method updates proc status of the task for which workflowId is
	 * fetched and terminated from database
	 * @throws ApplicationException
	 */
	@Test
	public void testselectWithdrawInvoiceWorkFlowId() throws ApplicationException
	{
		String asInvoiceNumber = "5003";
		Boolean loAuthStatusFlag = true;
		moFinancialsInvoiceListService.selectWithdrawInvoiceWorkFlowDetails(moSession, asInvoiceNumber,
				loAuthStatusFlag);
		assertEquals(
				"Transaction Successfull in selectWithdrawInvoiceWorkFlowDetails Service method for Invoice Number5003",
				moFinancialsInvoiceListService.getMoState().toString());

		loAuthStatusFlag = false;
		moFinancialsInvoiceListService.selectWithdrawInvoiceWorkFlowDetails(moSession, asInvoiceNumber,
				loAuthStatusFlag);
		assertNotSame(
				"Transaction Failed:: FinancialsInvoiceListService: selectWithdrawInvoiceWorkFlowId method -5003",
				moFinancialsInvoiceListService.getMoState().toString());

	}

	@Test(expected = ApplicationException.class)
	public void testselectWithdrawInvoiceWorkFlowIdException() throws ApplicationException
	{
		String asInvoiceNumber = "5003";
		Boolean loAuthStatusFlag = null;

		moFinancialsInvoiceListService.selectWithdrawInvoiceWorkFlowDetails(moSession, asInvoiceNumber,
				loAuthStatusFlag);
		assertEquals(
				"Transaction Failed:: FinancialsInvoiceListService:selectWithdrawInvoiceWorkFlowId method - failed to update record ",
				moFinancialsInvoiceListService.getMoState().toString());

	}

	@Test(expected = ApplicationException.class)
	public void testselectWithdrawInvoiceWorkFlowIdException2() throws ApplicationException
	{
		String asInvoiceNumber = "5003";
		Boolean loAuthStatusFlag = true;

		moFinancialsInvoiceListService.selectWithdrawInvoiceWorkFlowDetails(null, asInvoiceNumber, loAuthStatusFlag);

	}
	
	@Test(expected = ApplicationException.class)
	public void testselectWithdrawInvoiceWorkFlowIdException3() throws ApplicationException
	{
		String asInvoiceNumber = "5005";
		Boolean loAuthStatusFlag = true;

		moFinancialsInvoiceListService.selectWithdrawInvoiceWorkFlowDetails(null, asInvoiceNumber, loAuthStatusFlag);

	}
	
	@Test(expected = ApplicationException.class)
	public void testselectWithdrawInvoiceWorkFlowIdException4() throws ApplicationException
	{
		Boolean loAuthStatusFlag = true;

		moFinancialsInvoiceListService.selectWithdrawInvoiceWorkFlowDetails(moSession, null, loAuthStatusFlag);

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

}
