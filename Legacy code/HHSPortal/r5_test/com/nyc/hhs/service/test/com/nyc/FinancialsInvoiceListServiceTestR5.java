package com.nyc.hhs.service.test.com.nyc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.FinancialsInvoiceListService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.InvoiceList;

public class FinancialsInvoiceListServiceTestR5
{
	
	FinancialsInvoiceListService financialsInvoiceListService = new FinancialsInvoiceListService();
	
	private static SqlSession moSession = null; // SQL Session
	private InvoiceList moInvoiceList;
	
	public void setUp() throws Exception
	{
		moInvoiceList = new InvoiceList();
		moInvoiceList.setInvoiceNumber("123");
		moInvoiceList.setDateSubmittedTo("dateSubmittedTo");
		moInvoiceList.setInvoiceNumber("123");
		moInvoiceList.setInvoiceNumber("123");
		moInvoiceList.setAgency("accenture");
		moInvoiceList.setOrgId("accenture");
		
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
			System.out.println("Before");
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
			moSession.rollback();
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
		finally
		{
			moSession.rollback();
			moSession.close();
		}
	}
	
	/**
	 * This method gets the invoice count for the particular invoice list object
	 * and returns Application Exception in case of error
	 * @throws ApplicationException
	 */
	@Test
	public void getInvoiceCountCase1() throws ApplicationException
	{
		InvoiceList loInvoiceBean = new InvoiceList();
		loInvoiceBean.setOrgId("Org_509");
		loInvoiceBean.setAgency("DOC");
		loInvoiceBean.setInvoiceNumber("123");
		loInvoiceBean.setDateSubmittedTo("03-AUG-16 04.29.32.420000000 PM");
		loInvoiceBean.setFirstSortType("invoiceProgramName");
		loInvoiceBean.setSecondSortType("invoiceContractId");
		loInvoiceBean.setStartNode(1);
		loInvoiceBean.setEndNode(5);
		loInvoiceBean.setProviderId("5");
		loInvoiceBean.setInvoiceStatusId("70");
		loInvoiceBean.getInvoiceStatusList().add("85");
		String lsUserType = "";
		Integer loList = financialsInvoiceListService.getInvoiceCount(moSession, loInvoiceBean, lsUserType);
		assertEquals("0", loList.toString());
	}
	
	@Test
	public void getInvoiceCountCase2() throws ApplicationException
	{
		InvoiceList loInvoiceBean = new InvoiceList();
		loInvoiceBean.setOrgId("Org_509");
		loInvoiceBean.setAgency("DOC");
		loInvoiceBean.setInvoiceNumber("123");
		loInvoiceBean.setDateSubmittedTo("03-AUG-16 04.29.32.420000000 PM");
		loInvoiceBean.setFirstSortType("invoiceProgramName");
		loInvoiceBean.setSecondSortType("invoiceContractId");
		loInvoiceBean.setStartNode(1);
		loInvoiceBean.setEndNode(5);
		loInvoiceBean.setProviderId("5");
		loInvoiceBean.setInvoiceStatusId("70");
		loInvoiceBean.getInvoiceStatusList().add("85");
		String lsUserType = "provider_org";
		Integer loList = financialsInvoiceListService.getInvoiceCount(moSession, loInvoiceBean, lsUserType);
		assertEquals("0", loList.toString());
	}
	
	@Test
	public void getInvoiceCountCase3() throws ApplicationException
	{
		InvoiceList loInvoiceBean = new InvoiceList();
		loInvoiceBean.setOrgId("Org_509");
		loInvoiceBean.setAgency("DOC");
		loInvoiceBean.setInvoiceNumber("123");
		loInvoiceBean.setDateSubmittedTo("03-AUG-16 04.29.32.420000000 PM");
		loInvoiceBean.setFirstSortType("invoiceProgramName");
		loInvoiceBean.setSecondSortType("invoiceContractId");
		loInvoiceBean.setStartNode(1);
		loInvoiceBean.setEndNode(5);
		loInvoiceBean.setProviderId("5");
		loInvoiceBean.setInvoiceStatusId("70");
		loInvoiceBean.getInvoiceStatusList().add("85");
		String lsUserType = "agency_org";
		loInvoiceBean.setInvoiceContractId("6515");
		Integer loList = financialsInvoiceListService.getInvoiceCount(moSession, loInvoiceBean, lsUserType);
		assertEquals("0", loList.toString());
	}
	
	@Test
	public void getInvoiceCountCase4() throws ApplicationException
	{
		InvoiceList loInvoiceBean = new InvoiceList();
		loInvoiceBean.setOrgId("Org_509");
		loInvoiceBean.setAgency("DOC");
		loInvoiceBean.setInvoiceNumber("123");
		loInvoiceBean.setDateSubmittedTo("03-AUG-16 04.29.32.420000000 PM");
		loInvoiceBean.setFirstSortType("invoiceProgramName");
		loInvoiceBean.setSecondSortType("invoiceContractId");
		loInvoiceBean.setStartNode(1);
		loInvoiceBean.setEndNode(5);
		loInvoiceBean.setProviderId("5");
		loInvoiceBean.setInvoiceStatusId("70");
		loInvoiceBean.getInvoiceStatusList().add("85");
		String lsUserType = "Accelerator";
		loInvoiceBean.setInvoiceContractId("6515");
		Integer loList = financialsInvoiceListService.getInvoiceCount(moSession, loInvoiceBean, lsUserType);
		assertEquals("0", loList.toString());
	}
	
	@Test(expected = ApplicationException.class)
	public void getInvoiceCountCase5() throws ApplicationException
	{
		InvoiceList loInvoiceBean = new InvoiceList();
		loInvoiceBean.setOrgId("Org_509");
		loInvoiceBean.setAgency("DOC");
		loInvoiceBean.setInvoiceNumber("123");
		loInvoiceBean.setDateSubmittedTo("03-AUG-16 04.29.32.420000000 PM");
		loInvoiceBean.setFirstSortType("invoiceProgramName");
		loInvoiceBean.setSecondSortType("invoiceContractId");
		loInvoiceBean.setStartNode(1);
		loInvoiceBean.setEndNode(5);
		loInvoiceBean.setProviderId("5");
		loInvoiceBean.setInvoiceStatusId("70");
		loInvoiceBean.getInvoiceStatusList().add("85");
		String lsUserType = "Accelerator";
		loInvoiceBean.setInvoiceContractId("6515");
		Integer loList = financialsInvoiceListService.getInvoiceCount(null, loInvoiceBean, lsUserType);
		assertEquals("0", loList.toString());
	}
	
	@Test
	public void getInvoiceCountCase6() throws ApplicationException
	{
		InvoiceList loInvoiceBean = new InvoiceList();
		loInvoiceBean.setOrgId("Org_509");
		loInvoiceBean.setAgency("DOC");
		loInvoiceBean.setInvoiceNumber("123");
		loInvoiceBean.setDateSubmittedTo("03-AUG-16 04.29.32.420000000 PM");
		loInvoiceBean.setFirstSortType("invoiceProgramName");
		loInvoiceBean.setSecondSortType("invoiceContractId");
		loInvoiceBean.setStartNode(1);
		loInvoiceBean.setEndNode(5);
		loInvoiceBean.setProviderId("5");
		loInvoiceBean.setInvoiceStatusId("70");
		List<String> llList = null;
		loInvoiceBean.setInvoiceStatusList(llList);
		String lsUserType = "Accelerator";
		loInvoiceBean.setInvoiceContractId("6515");
		Integer loList = financialsInvoiceListService.getInvoiceCount(moSession, loInvoiceBean, lsUserType);
		assertEquals("0", loList.toString());
	}
	
	@Test
	public void fetchInvoiceListSummaryCase1() throws ApplicationException
	{
		InvoiceList loInvoiceBean = new InvoiceList();
		loInvoiceBean.setOrgId("Org_509");
		loInvoiceBean.setAgency("DOC");
		List<InvoiceList> loList = new ArrayList<InvoiceList>();
		loInvoiceBean.setInvoiceNumber("123");
		loInvoiceBean.setDateSubmittedTo("03-AUG-16 04.29.32.420000000 PM");
		loInvoiceBean.setFirstSortType("invoiceProgramName");
		loInvoiceBean.setSecondSortType("invoiceContractId");
		loInvoiceBean.setOrgType("Accelerator");
		loInvoiceBean.setStartNode(1);
		loInvoiceBean.setEndNode(5);
		loInvoiceBean.setProviderId("5");
		loInvoiceBean.setInvoiceStatusId("70");
		loInvoiceBean.getInvoiceStatusList().add("85");
		String lsUserType = "Accelerator";
		loInvoiceBean.setInvoiceContractId("6515");
		loList = financialsInvoiceListService.fetchInvoiceListSummary(moSession, loInvoiceBean, lsUserType);
		assertNull(loList);
	}
	
	@Test
	public void testFetchInvoiceListSummaryCase2() throws ApplicationException
	{
		InvoiceList loInvoiceBean = new InvoiceList("city_org");
		loInvoiceBean.setIsFilter(false);
		loInvoiceBean.setLsRequestFromHomePage("false");
		loInvoiceBean.setOrgId("city_org");
		loInvoiceBean.setOrgType("city_org");
		List<InvoiceList> loList = new ArrayList<InvoiceList>();
		String lsUserType = "city_org";
		loList = financialsInvoiceListService.fetchInvoiceListSummary(moSession, loInvoiceBean, lsUserType);
		assertNotNull(loList);
	}
	
	/**
	 * This method tests if invoices are available in database for agency.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchInvoiceListSummaryCase3() throws ApplicationException
	{
		InvoiceList loInvoiceBean = new InvoiceList("agency_org");
		loInvoiceBean.setIsFilter(false);
		loInvoiceBean.setLsRequestFromHomePage("false");
		loInvoiceBean.setOrgId("DOC");
		loInvoiceBean.setOrgType("agency_org");
		List<InvoiceList> loList = new ArrayList<InvoiceList>();
		String lsUserType = "agency_org";
		loInvoiceBean.setAgency("agency_org");
		List<String> llList = new ArrayList<String>();
		llList.add("");
		loInvoiceBean.setInvoiceStatusList(llList);
		loInvoiceBean.setInvoiceValueFrom("0");
		loInvoiceBean.setInvoiceValueTo("0");
		loList = financialsInvoiceListService.fetchInvoiceListSummary(moSession, loInvoiceBean, lsUserType);
		assertNotNull(loList);
	}
	
	/**
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testFetchInvoiceListSummaryCase4() throws ApplicationException
	{
		InvoiceList loInvoiceBean = new InvoiceList("provider_org");
		
		loInvoiceBean.setIsFilter(false);
		loInvoiceBean.setLsRequestFromHomePage("false");
		loInvoiceBean.setOrgId("provider_org");
		loInvoiceBean.setOrgType("provider_org");
		List<InvoiceList> loList = new ArrayList<InvoiceList>();
		String lsUserType = "provider_org";
		loList = financialsInvoiceListService.fetchInvoiceListSummary(moSession, loInvoiceBean, lsUserType);
		assertNotNull(loList);
	}
	
	/**
	 * This method tests if invoices are available in database for Accelerator.
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchInvoiceListSummaryCase5() throws ApplicationException
	{
		InvoiceList loInvoiceBean = new InvoiceList("provider_org");
		loInvoiceBean.setIsFilter(false);
		loInvoiceBean.setLsRequestFromHomePage("false");
		loInvoiceBean.setOrgId("provider_org");
		loInvoiceBean.setOrgType("provider_org");
		List<InvoiceList> loList = new ArrayList<InvoiceList>();
		String lsUserType = "provider_org";
		loList = financialsInvoiceListService.fetchInvoiceListSummary(null, loInvoiceBean, lsUserType);
	}
	
	@Test
	public void testFetchInvoiceListSummaryCase6() throws ApplicationException
	{
		InvoiceList loInvoiceBean = new InvoiceList("agency_org");
		loInvoiceBean.setIsFilter(false);
		loInvoiceBean.setLsRequestFromHomePage("true");
		loInvoiceBean.setOrgId("DOC");
		loInvoiceBean.setOrgType("agency_org");
		List<InvoiceList> loList = new ArrayList<InvoiceList>();
		String lsUserType = null;
		loInvoiceBean.setAgency("agency_org");
		List<String> llList = null;
		loInvoiceBean.setInvoiceStatusList(llList);
		loInvoiceBean.setInvoiceValueFrom("0");
		loInvoiceBean.setInvoiceValueTo("0");
		loList = financialsInvoiceListService.fetchInvoiceListSummary(moSession, loInvoiceBean, lsUserType);
		assertNull(loList);
	}
	
	@Test
	public void testFetchInvoiceListSummaryCase7() throws ApplicationException
	{
		InvoiceList loInvoiceBean = new InvoiceList("provider_org");
		loInvoiceBean.setIsFilter(false);
		loInvoiceBean.setLsRequestFromHomePage("false");
		loInvoiceBean.setOrgId("provider_org");
		loInvoiceBean.setOrgType(null);
		List<InvoiceList> loList = new ArrayList<InvoiceList>();
		List<String> llList = new ArrayList<String>();
		llList.add("");
		loInvoiceBean.setInvoiceStatusList(llList);
		String lsUserType = "";
		loList = financialsInvoiceListService.fetchInvoiceListSummary(moSession, loInvoiceBean, lsUserType);
		assertNull(loList);
	}
	
}