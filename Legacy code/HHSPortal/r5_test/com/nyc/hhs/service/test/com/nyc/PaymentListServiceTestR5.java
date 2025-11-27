package com.nyc.hhs.service.test.com.nyc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.PaymentListService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.PaymentSortAndFilter;

public class PaymentListServiceTestR5
{
	
	PaymentListService paymentListService = new PaymentListService();
	
	private static SqlSession moSession = null; // SQL Session
	
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
	
	@Test
	public void getPaymentCountCase1() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentBean = new PaymentSortAndFilter();
		loPaymentBean.setOrgId("Org_509");
		loPaymentBean.setAgency("DOC");
		loPaymentBean.setFirstSortType("invoiceProgramName");
		loPaymentBean.setSecondSortType("invoiceContractId");
		loPaymentBean.setStartNode(1);
		loPaymentBean.setEndNode(5);
		loPaymentBean.setProviderId("5");
		String lsUserType = "provider_org";
		Integer loList = paymentListService.getPaymentCount(moSession, loPaymentBean);
		assertEquals("0", loList.toString());
	}
	
	@Test
	public void getPaymentCountCase2() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter = new PaymentSortAndFilter();
		loPaymentSortAndFilter.setStartNode(1);
		loPaymentSortAndFilter.setEndNode(5);
		loPaymentSortAndFilter.setFirstSort("status_process_type_id asc,disbursement_date");
		loPaymentSortAndFilter.setSecondSort("payment_voucher_num");
		loPaymentSortAndFilter.setFirstSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setSecondSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setFirstSortDate(true);
		loPaymentSortAndFilter.setAgency("DOC");
		loPaymentSortAndFilter.getPaymentStatusList().add("64");
		loPaymentSortAndFilter.setOrgId("r3_org");
		loPaymentSortAndFilter.setOrgType(ApplicationConstants.CITY_ORG);
		Integer liPaymentCount = paymentListService.getPaymentCount(moSession, loPaymentSortAndFilter);
		assertTrue(liPaymentCount.intValue() > 0);
	}
	
	@Test
	public void getPaymentCountCase3() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter = new PaymentSortAndFilter();
		loPaymentSortAndFilter.setStartNode(1);
		loPaymentSortAndFilter.setEndNode(5);
		loPaymentSortAndFilter.setFirstSort("status_process_type_id asc,disbursement_date");
		loPaymentSortAndFilter.setSecondSort("payment_voucher_num");
		loPaymentSortAndFilter.setFirstSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setSecondSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setFirstSortDate(true);
		loPaymentSortAndFilter.setAgency("DOC");
		loPaymentSortAndFilter.getPaymentStatusList().add("64");
		loPaymentSortAndFilter.setOrgId("r3_org");
		loPaymentSortAndFilter.setOrgType(ApplicationConstants.PROVIDER_ORG);
		Integer liPaymentCount = paymentListService.getPaymentCount(moSession, loPaymentSortAndFilter);
		assertTrue(liPaymentCount.intValue() == 0);
	}
	
	@Test
	public void getPaymentCountCase4() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter = new PaymentSortAndFilter();
		loPaymentSortAndFilter.setStartNode(1);
		loPaymentSortAndFilter.setEndNode(5);
		loPaymentSortAndFilter.setFirstSort("status_process_type_id asc,disbursement_date");
		loPaymentSortAndFilter.setSecondSort("payment_voucher_num");
		loPaymentSortAndFilter.setFirstSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setSecondSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setFirstSortDate(true);
		loPaymentSortAndFilter.setAgency("DOC");
		loPaymentSortAndFilter.getPaymentStatusList().add("64");
		loPaymentSortAndFilter.setOrgId("r3_org");
		loPaymentSortAndFilter.setOrgType(ApplicationConstants.AGENCY_ORG);
		Integer liPaymentCount = paymentListService.getPaymentCount(moSession, loPaymentSortAndFilter);
		assertTrue(liPaymentCount.intValue() > 0);
	}
	
	@Test(expected = ApplicationException.class)
	public void getPaymentCountCase5() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter = new PaymentSortAndFilter();
		loPaymentSortAndFilter.setStartNode(1);
		loPaymentSortAndFilter.setEndNode(5);
		loPaymentSortAndFilter.setFirstSort("status_process_type_id asc,disbursement_date");
		loPaymentSortAndFilter.setSecondSort("payment_voucher_num");
		loPaymentSortAndFilter.setFirstSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setSecondSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setFirstSortDate(true);
		loPaymentSortAndFilter.setAgency("DOC");
		loPaymentSortAndFilter.getPaymentStatusList().add("64");
		loPaymentSortAndFilter.setOrgId("r3_org");
		loPaymentSortAndFilter.setOrgType(ApplicationConstants.PROVIDER_ORG);
		Integer liPaymentCount = paymentListService.getPaymentCount(null, loPaymentSortAndFilter);
	}
	
	@Test
	public void FetchPaymentListSummaryCase1() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter = new PaymentSortAndFilter();
		loPaymentSortAndFilter.setStartNode(1);
		loPaymentSortAndFilter.setEndNode(5);
		loPaymentSortAndFilter.setFirstSort("status_process_type_id asc,disbursement_date");
		loPaymentSortAndFilter.setSecondSort("payment_voucher_num");
		loPaymentSortAndFilter.setFirstSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setSecondSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setFirstSortDate(true);
		loPaymentSortAndFilter.setAgency("DOC");
		loPaymentSortAndFilter.getPaymentStatusList().add("64");
		loPaymentSortAndFilter.setOrgId("r3_org");
		loPaymentSortAndFilter.setOrgType(ApplicationConstants.PROVIDER_ORG);
		List<PaymentSortAndFilter> loList = (paymentListService.fetchPaymentListSummary(moSession,
				loPaymentSortAndFilter));
		assertNotNull(loList);
	}
	
	@Test
	public void FetchPaymentListSummaryCase2() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter = new PaymentSortAndFilter();
		loPaymentSortAndFilter.setStartNode(1);
		loPaymentSortAndFilter.setEndNode(5);
		loPaymentSortAndFilter.setFirstSort("status_process_type_id asc,disbursement_date");
		loPaymentSortAndFilter.setSecondSort("payment_voucher_num");
		loPaymentSortAndFilter.setFirstSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setSecondSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setFirstSortDate(true);
		loPaymentSortAndFilter.setAgency("DOC");
		loPaymentSortAndFilter.getPaymentStatusList().add("64");
		loPaymentSortAndFilter.setOrgId("r3_org");
		loPaymentSortAndFilter.setOrgType(ApplicationConstants.AGENCY_ORG);
		List<PaymentSortAndFilter> loList = (paymentListService.fetchPaymentListSummary(moSession,
				loPaymentSortAndFilter));
		assertNotNull(loList);
	}
	
	@Test
	public void FetchPaymentListSummaryCase3() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter = new PaymentSortAndFilter();
		loPaymentSortAndFilter.setStartNode(1);
		loPaymentSortAndFilter.setEndNode(5);
		loPaymentSortAndFilter.setFirstSort("status_process_type_id asc,disbursement_date");
		loPaymentSortAndFilter.setSecondSort("payment_voucher_num");
		loPaymentSortAndFilter.setFirstSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setSecondSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setFirstSortDate(true);
		loPaymentSortAndFilter.setAgency("DOC");
		loPaymentSortAndFilter.getPaymentStatusList().add("64");
		loPaymentSortAndFilter.setOrgId("r3_org");
		loPaymentSortAndFilter.setOrgType(ApplicationConstants.CITY_ORG);
		List<PaymentSortAndFilter> loList = (paymentListService.fetchPaymentListSummary(moSession,
				loPaymentSortAndFilter));
		assertNotNull(loList);
	}
	
	@Test
	public void FetchPaymentListSummaryCase4() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter = new PaymentSortAndFilter();
		loPaymentSortAndFilter.setStartNode(1);
		loPaymentSortAndFilter.setEndNode(5);
		loPaymentSortAndFilter.setFirstSort("status_process_type_id asc,disbursement_date");
		loPaymentSortAndFilter.setSecondSort("payment_voucher_num");
		loPaymentSortAndFilter.setFirstSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setSecondSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setFirstSortDate(true);
		loPaymentSortAndFilter.setAgency("DOC");
		loPaymentSortAndFilter.getPaymentStatusList().add("64");
		loPaymentSortAndFilter.setOrgId("r3_org");
		loPaymentSortAndFilter.setOrgType("Accelerator");
		List<PaymentSortAndFilter> loList = (paymentListService.fetchPaymentListSummary(moSession,
				loPaymentSortAndFilter));
		assertNull(loList);
	}
	
	@Test
	public void FetchPaymentListSummaryCase5() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter = new PaymentSortAndFilter();
		loPaymentSortAndFilter.setStartNode(1);
		loPaymentSortAndFilter.setEndNode(5);
		loPaymentSortAndFilter.setFirstSort("status_process_type_id asc,disbursement_date");
		loPaymentSortAndFilter.setSecondSort("payment_voucher_num");
		loPaymentSortAndFilter.setFirstSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setSecondSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setFirstSortDate(true);
		loPaymentSortAndFilter.setAgency("DOC");
		loPaymentSortAndFilter.getPaymentStatusList().add("64");
		loPaymentSortAndFilter.setOrgId("r3_org");
		loPaymentSortAndFilter.setPaymentValueTo("0");
		loPaymentSortAndFilter.setPaymentValueFrom("0");
		loPaymentSortAndFilter.setOrgType("");
		List<PaymentSortAndFilter> loList = (paymentListService.fetchPaymentListSummary(moSession,
				loPaymentSortAndFilter));
		assertNull(loList);
	}
	
	@Test(expected = Exception.class)
	public void FetchPaymentListSummaryCase6() throws ApplicationException
	{
		PaymentSortAndFilter loPaymentSortAndFilter = new PaymentSortAndFilter();
		loPaymentSortAndFilter.setStartNode(1);
		loPaymentSortAndFilter.setEndNode(5);
		loPaymentSortAndFilter.setFirstSort("status_process_type_id asc,disbursement_date");
		loPaymentSortAndFilter.setSecondSort("payment_voucher_num");
		loPaymentSortAndFilter.setFirstSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setSecondSortType(HHSConstants.DESCENDING);
		loPaymentSortAndFilter.setFirstSortDate(true);
		loPaymentSortAndFilter.setAgency("DOC");
		loPaymentSortAndFilter.getPaymentStatusList().add("64");
		loPaymentSortAndFilter.setOrgId("r3_org");
		loPaymentSortAndFilter.setOrgType(ApplicationConstants.PROVIDER_ORG);
		List<PaymentSortAndFilter> loList = (paymentListService.fetchPaymentListSummary(null, loPaymentSortAndFilter));
	}
	
}
