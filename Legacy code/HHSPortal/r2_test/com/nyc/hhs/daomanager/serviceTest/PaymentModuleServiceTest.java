/**
 * 
 */
package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.PaymentModuleService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.AssignmentsSummaryBean;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.PaymentBean;
import com.nyc.hhs.model.PaymentChartOfAllocation;
import com.nyc.hhs.model.TaskDetailsBean;

/**
 * @author faiyaz.asharaf
 * 
 */
public class PaymentModuleServiceTest
{

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

	PaymentModuleService moPaymentModuleService = new PaymentModuleService();

	@Test
	public void testFetchAdvancePaymentReviewDetails() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1890");
		loHashMap.put(HHSConstants.BUDGET_ADVANCE_ID_WORKFLOW, "130600007");
		PaymentBean loPaymentBean = moPaymentModuleService.fetchAdvancePaymentReviewDetails(moSession, loHashMap);
		assertTrue(loPaymentBean != null);
	}

	@Test
	public void testFetchAdvancePaymentReviewDetails1() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.BUDGET_ADVANCE_ID_WORKFLOW, "1");
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1890");
		PaymentBean loPaymentBean = moPaymentModuleService.fetchAdvancePaymentReviewDetails(moSession, loHashMap);
		assertNull(loPaymentBean);
	}

	@Test
	public void testFetchAdvancePaymentReviewDetailsException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.BUDGET_ADVANCE_ID_WORKFLOW, "130600007");
			moPaymentModuleService.fetchAdvancePaymentReviewDetails(null, loHashMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchAdvancePaymentVoucherList() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.BUDGET_ADVANCE_ID_WORKFLOW, "130600007");
		List<PaymentBean> loPaymentVoucher = moPaymentModuleService
				.fetchAdvancePaymentVoucherList(moSession, loHashMap);
		assertTrue(loPaymentVoucher.size() > 0);
	}

	@Test
	public void testFetchAdvancePaymentVoucherList1() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.BUDGET_ADVANCE_ID_WORKFLOW, "1");
		List<PaymentBean> loPaymentVoucher = moPaymentModuleService
				.fetchAdvancePaymentVoucherList(moSession, loHashMap);
		assertTrue(loPaymentVoucher.size() == 0);
	}

	@Test
	public void testFetchAdvancePaymentVoucherList1Exception() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.BUDGET_ADVANCE_ID_WORKFLOW, "1");
			moPaymentModuleService.fetchAdvancePaymentVoucherList(null, loHashMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchAdvancePaymentHeaderDetails() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, "11120");
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1890");
		PaymentBean loPaymentBean = moPaymentModuleService.fetchAdvancePaymentHeaderDetails(moSession, loHashMap);
		assertTrue(loPaymentBean != null);
	}

	@Test
	public void testFetchAdvancePaymentHeaderDetails1() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, "1");
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1890");
		PaymentBean loPaymentBean = moPaymentModuleService.fetchAdvancePaymentHeaderDetails(moSession, loHashMap);
		assertNull(loPaymentBean);
	}

	@Test
	public void testFetchAdvancePaymentHeaderDetailsException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, "1");
			moPaymentModuleService.fetchAdvancePaymentHeaderDetails(moSession, loHashMap);
			moPaymentModuleService.fetchAdvancePaymentLineDetails(null, loHashMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchContractInfoForPayment() throws ApplicationException
	{

		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1638");
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "10509");

		ContractList loContractList = moPaymentModuleService.fetchContractInfoForPayment(moSession, loHashMap);

		assertTrue(loContractList != null);
	}

	@Test
	public void testFetchContractInfoForPaymentException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1638");
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "10509");

			moPaymentModuleService.fetchContractInfoForPayment(null, loHashMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchContractInfoForPaymentException1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();

			moPaymentModuleService.fetchContractInfoForPayment(moSession, loHashMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFetchFyBudgetSummaryForPayment() throws ApplicationException
	{

		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1890");
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "10543");

		BudgetDetails loBudgetDetails = moPaymentModuleService.fetchFyBudgetSummaryForPayment(moSession, loHashMap);

		assertTrue(loBudgetDetails != null);
	}

	@Test
	public void testFetchFyBudgetSummaryForPaymentException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1890");
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "10543");
			moPaymentModuleService.fetchFyBudgetSummaryForPayment(null, loHashMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testGetCbGridDataForSession() throws ApplicationException
	{

		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1890");
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "10543");

		CBGridBean loCBGridBean = moPaymentModuleService.getCbGridDataForSession(moSession, loHashMap);

		assertTrue(loCBGridBean != null);
	}

	@Test
	public void testGetCbGridDataForSessionException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1890");
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "10543");
			moPaymentModuleService.getCbGridDataForSession(null, loHashMap);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchAssignmentSummary() throws ApplicationException
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		loCBGridBeanObj.setContractBudgetID("10543");
		loCBGridBeanObj.setBudgetAdvanceId("27");
		List<AssignmentsSummaryBean> loAssignmentsList = moPaymentModuleService.fetchAssignmentSummary(moSession,
				loCBGridBeanObj);
		assertTrue(loAssignmentsList.size() > 0);
	}

	@Test
	public void testFetchAssignmentSummary1() throws ApplicationException
	{
		CBGridBean loCBGridBeanObj = new CBGridBean();
		loCBGridBeanObj.setContractBudgetID("10543");
		loCBGridBeanObj.setBudgetAdvanceId("28");
		List<AssignmentsSummaryBean> loAssignmentsList = moPaymentModuleService.fetchAssignmentSummary(moSession,
				loCBGridBeanObj);
		assertTrue(loAssignmentsList.size() > 0);
	}

	@Test
	public void testFetchAssignmentSummaryException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loCBGridBeanObj = new CBGridBean();
			loCBGridBeanObj.setContractBudgetID("10543");
			loCBGridBeanObj.setBudgetAdvanceId("27");
			moPaymentModuleService.fetchAssignmentSummary(null, loCBGridBeanObj);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditAssignmentSummary() throws ApplicationException
	{
		AssignmentsSummaryBean loAssignmentsSummaryBean = new AssignmentsSummaryBean();
		loAssignmentsSummaryBean.setId("368");
		loAssignmentsSummaryBean.setBudgetAdvanceId("27");
		loAssignmentsSummaryBean.setContractBudgetID("10543");
		loAssignmentsSummaryBean.setAssignmentAmount("2000");
		loAssignmentsSummaryBean.setModifyByAgency("agency_20");
		Boolean loReturnValue = moPaymentModuleService.editAssignmentSummary(moSession, loAssignmentsSummaryBean);
		assertTrue(loReturnValue);
	}

	@Test
	public void testEditAssignmentSummary1() throws ApplicationException
	{
		AssignmentsSummaryBean loAssignmentsSummaryBean = new AssignmentsSummaryBean();
		loAssignmentsSummaryBean.setId("297");
		loAssignmentsSummaryBean.setBudgetAdvanceId("27");
		loAssignmentsSummaryBean.setContractBudgetID("10543");
		loAssignmentsSummaryBean.setAssignmentAmount("2000");
		loAssignmentsSummaryBean.setModifyByAgency("agency_20");
		Boolean loReturnValue = moPaymentModuleService.editAssignmentSummary(moSession, loAssignmentsSummaryBean);
		assertTrue(loReturnValue);
	}

	@Test
	public void testEditAssignmentSummaryException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AssignmentsSummaryBean loAssignmentsSummaryBean = new AssignmentsSummaryBean();
			loAssignmentsSummaryBean.setId("297");
			loAssignmentsSummaryBean.setBudgetAdvanceId("27");
			loAssignmentsSummaryBean.setContractBudgetID("10543");
			loAssignmentsSummaryBean.setAssignmentAmount("2000");
			loAssignmentsSummaryBean.setModifyByAgency("agency_20");
			moPaymentModuleService.editAssignmentSummary(null, loAssignmentsSummaryBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testEditAssignmentSummaryException1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AssignmentsSummaryBean loAssignmentsSummaryBean = new AssignmentsSummaryBean();
			moPaymentModuleService.editAssignmentSummary(moSession, loAssignmentsSummaryBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testDelAssignment() throws ApplicationException
	{
		AssignmentsSummaryBean loAssignmentsSummaryBean = new AssignmentsSummaryBean();
		loAssignmentsSummaryBean.setId("373");
		loAssignmentsSummaryBean.setBudgetAdvanceId("27");
		loAssignmentsSummaryBean.setContractBudgetID("10543");
		Boolean loReturnValue = moPaymentModuleService.delAssignment(moSession, loAssignmentsSummaryBean);
		assertTrue(loReturnValue);
	}
	
	@Test
	public void testDelAssignmentException() throws ApplicationException, Exception
	{
		Boolean lbThrown = false;
		try
		{
			AssignmentsSummaryBean loAssignmentsSummaryBean = new AssignmentsSummaryBean();
			loAssignmentsSummaryBean.setId("373");
			loAssignmentsSummaryBean.setBudgetAdvanceId("27");
			loAssignmentsSummaryBean.setContractBudgetID("10543");
			moPaymentModuleService.delAssignment(null, loAssignmentsSummaryBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testSetAdvanceStatusForReviewTask() throws ApplicationException, Exception

	{
		Boolean loFinalFinish = true;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("1890");
		loTaskDetailsBean.setBudgetId("10543");
		loTaskDetailsBean.setBudgetAdvanceId("27");
		loTaskDetailsBean.setUserId("agency_21");
		String lsBudgetStatus = "92";
		moPaymentModuleService.setAdvanceStatusForReviewTask(moSession, loFinalFinish, loTaskDetailsBean,
				lsBudgetStatus);
		assertEquals("Transaction Success:: PaymentModuleService:setAdvanceStatusForReviewTask"
				+ " method - success to update record " + " \n", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testSetAdvanceStatusForReviewTask1() throws ApplicationException, Exception

	{
		Boolean loFinalFinish = false;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("1890");
		loTaskDetailsBean.setBudgetId("10543");
		loTaskDetailsBean.setBudgetAdvanceId("27");
		loTaskDetailsBean.setUserId("agency_21");
		String lsBudgetStatus = "92";
		moPaymentModuleService.setAdvanceStatusForReviewTask(moSession, loFinalFinish, loTaskDetailsBean,
				lsBudgetStatus);
		assertEquals("", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testSetAdvanceStatusForReviewTaskException() throws ApplicationException, Exception
	{
		Boolean lbThrown = false;
		try
		{
			Boolean loFinalFinish = true;
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setContractId("1890");
			loTaskDetailsBean.setBudgetId("10543");
			loTaskDetailsBean.setBudgetAdvanceId("27");
			loTaskDetailsBean.setUserId("agency_21");
			String lsBudgetStatus = "92";
			moPaymentModuleService
					.setAdvanceStatusForReviewTask(null, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testDeleteBudgetAdvance() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = true;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("1890");
		loTaskDetailsBean.setBudgetId("10543");
		loTaskDetailsBean.setBudgetAdvanceId("27");
		loTaskDetailsBean.setUserId("agency_21");
		String lsBudgetStatus = "92";
		moPaymentModuleService.deleteBudgetAdvance(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		assertEquals("Transaction Success:: PaymentModuleService:deleteBudgetAdvance"
				+ " method - success to update record " + " \n", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testDeleteBudgetAdvance1() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = false;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("1890");
		loTaskDetailsBean.setBudgetId("10543");
		loTaskDetailsBean.setBudgetAdvanceId("27");
		loTaskDetailsBean.setUserId("agency_21");
		String lsBudgetStatus = "92";
		moPaymentModuleService.deleteBudgetAdvance(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		assertEquals("", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testDeleteBudgetAdvanceException() throws ApplicationException, Exception
	{
		Boolean lbThrown = false;
		try
		{
			Boolean loFinalFinish = true;
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setContractId("1890");
			loTaskDetailsBean.setBudgetId("10543");
			loTaskDetailsBean.setBudgetAdvanceId("27");
			loTaskDetailsBean.setUserId("agency_21");
			String lsBudgetStatus = "92";
			moPaymentModuleService.deleteBudgetAdvance(null, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testSetPaymentStatus() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = true;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("1890");
		loTaskDetailsBean.setBudgetId("10543");
		loTaskDetailsBean.setBudgetAdvanceId("130600007");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setPeriod("12");
		String lsBudgetStatus = "92";
		moPaymentModuleService.setPaymentStatus(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		assertEquals("Transaction Success:: PaymentModuleService:setPaymentStatus"
				+ " method - success to update record " + " \n", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testSetPaymentStatus2() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = false;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("1890");
		loTaskDetailsBean.setBudgetId("10543");
		loTaskDetailsBean.setBudgetAdvanceId("130600007");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setPeriod("12");
		String lsBudgetStatus = "92";
		moPaymentModuleService.setPaymentStatus(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		assertEquals("", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testSetPaymentStatus1() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = true;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("1890");
		loTaskDetailsBean.setBudgetId("10543");
		loTaskDetailsBean.setInvoiceId("374");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setPeriod("12");
		String lsBudgetStatus = "92";
		moPaymentModuleService.setPaymentStatus(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		assertEquals("Transaction Success:: PaymentModuleService:setPaymentStatus"
				+ " method - success to update record " + " \n", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testSetPaymentStatusException() throws ApplicationException, Exception
	{
		Boolean lbThrown = false;
		try
		{
			Boolean loFinalFinish = true;
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setContractId("1890");
			loTaskDetailsBean.setBudgetId("10543");
			loTaskDetailsBean.setBudgetAdvanceId("130600007");
			loTaskDetailsBean.setUserId("agency_21");
			loTaskDetailsBean.setPeriod("12");
			String lsBudgetStatus = "92";
			moPaymentModuleService.setPaymentStatus(null, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testSetInvoiceStatus() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = true;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("2383");
		loTaskDetailsBean.setBudgetId("10663");
		loTaskDetailsBean.setInvoiceId("412");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setPeriod("12");
		String lsBudgetStatus = "70";
		moPaymentModuleService.setInvoiceStatus(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		assertEquals("Transaction Success:: PaymentModuleService:setInvoiceStatus"
				+ " method - success to update record " + " \n", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testSetInvoiceStatus2() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = false;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("2383");
		loTaskDetailsBean.setBudgetId("10663");
		loTaskDetailsBean.setInvoiceId("412");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setPeriod("12");
		String lsBudgetStatus = "70";
		moPaymentModuleService.setInvoiceStatus(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		assertEquals("", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testSetInvoiceStatusException() throws ApplicationException, Exception
	{
		Boolean lbThrown = false;
		try
		{
			Boolean loFinalFinish = true;
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setContractId("2383");
			loTaskDetailsBean.setBudgetId("10663");
			loTaskDetailsBean.setInvoiceId("412");
			loTaskDetailsBean.setUserId("agency_21");
			loTaskDetailsBean.setPeriod("12");
			String lsBudgetStatus = "70";
			moPaymentModuleService.setInvoiceStatus(null, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testDeletePaymentRecords() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = true;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("1890");
		loTaskDetailsBean.setBudgetId("10543");
		loTaskDetailsBean.setBudgetAdvanceId("130600007");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setPeriod("12");
		String lsBudgetStatus = "92";
		moPaymentModuleService.deletePaymentRecords(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		assertEquals("Transaction Success:: PaymentModuleService:deletePaymentRecords"
				+ " method - success to update record " + " \n", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testDeletePaymentRecords1() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = true;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("1890");
		loTaskDetailsBean.setBudgetId("10543");
		loTaskDetailsBean.setInvoiceId("374");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setPeriod("12");
		String lsBudgetStatus = "92";
		moPaymentModuleService.deletePaymentRecords(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		assertEquals("Transaction Success:: PaymentModuleService:deletePaymentRecords"
				+ " method - success to update record " + " \n", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testDeletePaymentRecords2() throws ApplicationException, Exception
	{
		Boolean loFinalFinish = false;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("1890");
		loTaskDetailsBean.setBudgetId("10543");
		loTaskDetailsBean.setInvoiceId("374");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setPeriod("12");
		String lsBudgetStatus = "92";
		moPaymentModuleService.deletePaymentRecords(moSession, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		assertEquals("", moPaymentModuleService.getMoState().toString());
	}

	@Test
	public void testDeletePaymentRecordsException() throws ApplicationException, Exception
	{
		Boolean lbThrown = false;
		try
		{
			Boolean loFinalFinish = true;
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setContractId("1890");
			loTaskDetailsBean.setBudgetId("10543");
			loTaskDetailsBean.setBudgetAdvanceId("130600007");
			loTaskDetailsBean.setUserId("agency_21");
			loTaskDetailsBean.setPeriod("12");
			String lsBudgetStatus = "92";
			moPaymentModuleService.deletePaymentRecords(null, loFinalFinish, loTaskDetailsBean, lsBudgetStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchPaymentHeaderDetails() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1890");
		loHashMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, "3");
		PaymentBean loPaymentBean = moPaymentModuleService.fetchPaymentHeaderDetails(moSession, loHashMap);
		assertTrue(loPaymentBean != null);
	}

	@Test
	public void testFetchPaymentHeaderDetails1() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1890");
		loHashMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, "1");
		PaymentBean loPaymentBean = moPaymentModuleService.fetchPaymentHeaderDetails(moSession, loHashMap);
		assertNull(loPaymentBean);
	}

	@Test
	public void testFetchPaymentHeaderDetailsException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1890");
			loHashMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, "1");
			PaymentBean loPaymentBean = moPaymentModuleService.fetchPaymentHeaderDetails(null, loHashMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchPaymentVoucherList() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.INVOICE_ID, "374");
		List<PaymentBean> loPaymentVoucher = moPaymentModuleService.fetchPaymentVoucherList(moSession, loHashMap);
		assertTrue(loPaymentVoucher.size() > 0);
	}

	@Test
	public void testFetchPaymentVoucherList1() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.INVOICE_ID, "398");
		List<PaymentBean> loPaymentVoucher = moPaymentModuleService.fetchPaymentVoucherList(moSession, loHashMap);
		assertTrue(loPaymentVoucher.size() == 0);
	}

	@Test
	public void testFetchPaymentVoucherListException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.INVOICE_ID, "374");
			moPaymentModuleService.fetchPaymentVoucherList(null, loHashMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchPaymentReviewHeaderDetails() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.INVOICE_ID, "374");
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1890");
		PaymentBean loPaymentBean = moPaymentModuleService.fetchPaymentReviewHeaderDetails(moSession, loHashMap);
		assertTrue(loPaymentBean != null);
	}

	@Test
	public void testFetchPaymentReviewHeaderDetails1() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.INVOICE_ID, "398");
		loHashMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1890");
		PaymentBean loPaymentBean = moPaymentModuleService.fetchPaymentReviewHeaderDetails(moSession, loHashMap);
		assertNull(loPaymentBean);
	}

	@Test
	public void testFetchPaymentReviewHeaderDetailsException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.INVOICE_ID, "374");
			moPaymentModuleService.fetchPaymentReviewHeaderDetails(null, loHashMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchAdvancePaymentLineDetails() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "10663");
		loHashMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, "40");
		BudgetDetails loPaymentBudgetDetails = moPaymentModuleService.fetchAdvancePaymentLineDetails(moSession,
				loHashMap);
		assertTrue(loPaymentBudgetDetails != null);
	}

	@Test
	public void testFetchAdvancePaymentLineDetails1() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "1");
		loHashMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, "40");
		BudgetDetails loPaymentBudgetDetails = moPaymentModuleService.fetchAdvancePaymentLineDetails(moSession,
				loHashMap);
		assertTrue(loPaymentBudgetDetails != null);
	}

	@Test
	public void testAdvancePaymentLineDetailsException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "1");
			loHashMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, "40");
			moPaymentModuleService.fetchAdvancePaymentLineDetails(null, loHashMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testValidateLevelOneAdvanceRequestFinishTask() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetAdvanceId("130600001");
		Map loValidationResultMap = new HashMap<String, Object>();
		loValidationResultMap = moPaymentModuleService.validateLevelOneAdvanceRequestFinishTask(moSession,
				loTaskDetailsBean);
		assertEquals(0, loValidationResultMap.get(HHSConstants.ERROR_CODE));
		assertEquals("", loValidationResultMap.get(HHSConstants.CLC_ERROR_MSG));

	}

	@Test
	public void testValidateLevelOneAdvanceRequestFinishTask1() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setBudgetAdvanceId("28");
		Map loValidationResultMap = new HashMap<String, Object>();
		loValidationResultMap = moPaymentModuleService.validateLevelOneAdvanceRequestFinishTask(moSession,
				loTaskDetailsBean);
		assertEquals(1, loValidationResultMap.get(HHSConstants.ERROR_CODE));
		assertEquals("! Total Assignment amount should not exceed budget Advance Amount.",
				loValidationResultMap.get(HHSConstants.CLC_ERROR_MSG));
	}

	@Test
	public void testValidateLevelOneAdvanceRequestFinishTaskException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setBudgetAdvanceId("28");
			moPaymentModuleService.validateLevelOneAdvanceRequestFinishTask(null, loTaskDetailsBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}

	@Test
	public void testFetchpaymentCOF() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setSubBudgetID("49");
		aoCBGridBeanObj.setContractID("2383");
		List<PaymentChartOfAllocation> loPaymentChartOfAllocationList = moPaymentModuleService.fetchpaymentCOF(moSession, aoCBGridBeanObj);
		assertTrue(loPaymentChartOfAllocationList.size() > 0);
	}

	@Test
	public void testFetchpaymentCOFException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			CBGridBean aoCBGridBeanObj = new CBGridBean();
			aoCBGridBeanObj.setSubBudgetID("49");
			aoCBGridBeanObj.setContractID("2383");
			moPaymentModuleService.fetchpaymentCOF(null, aoCBGridBeanObj);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testUpdatepaymentCOF() throws ApplicationException
	{
		PaymentChartOfAllocation loPaymentChartOfAllocation = new PaymentChartOfAllocation();
		loPaymentChartOfAllocation.setSubBudgetID("49");
		loPaymentChartOfAllocation.setId("2221-2-1-2-1");
		loPaymentChartOfAllocation.setPaymentAmount("20000");
		loPaymentChartOfAllocation.setContractID("2383");
		Boolean lbUpdateStatus = moPaymentModuleService.updatepaymentCOF(moSession, loPaymentChartOfAllocation);
		assertTrue(lbUpdateStatus);
	}

	@Test
	public void testUpdatepaymentCOFException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			PaymentChartOfAllocation loPaymentChartOfAllocation = new PaymentChartOfAllocation();
			loPaymentChartOfAllocation.setSubBudgetID("49");
			loPaymentChartOfAllocation.setId("2221-2-1-2-1");
			loPaymentChartOfAllocation.setPaymentAmount("2000000");
			loPaymentChartOfAllocation.setContractID("2383");
			Boolean lbUpdateStatus = moPaymentModuleService.updatepaymentCOF(moSession, loPaymentChartOfAllocation);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Payment Amount entered for a Commodity/Accounting Line cannot be greater than its Remaining Encumbrance", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testUpdatepaymentCOFException1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			PaymentChartOfAllocation loPaymentChartOfAllocation = new PaymentChartOfAllocation();
			loPaymentChartOfAllocation.setSubBudgetID("49");
			loPaymentChartOfAllocation.setId("2221-2-1-2-1");
			loPaymentChartOfAllocation.setPaymentAmount("2000000");
			loPaymentChartOfAllocation.setContractID("2383");
			Boolean lbUpdateStatus = moPaymentModuleService.updatepaymentCOF(null, loPaymentChartOfAllocation);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testFetchPaymentLineDetails() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.INVOICE_ID, "450");
		loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "10663");
		loHashMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, "49");
		BudgetDetails loPaymentBudgetDetails = moPaymentModuleService.fetchPaymentLineDetails(moSession, loHashMap);
		assertTrue(loPaymentBudgetDetails != null);
	}
	
	@Test
	public void testFetchPaymentLineDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.INVOICE_ID, "450");
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "10663");
			loHashMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, "49");
			moPaymentModuleService.fetchPaymentLineDetails(null, loHashMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	public void testFetchPaymentLineDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.INVOICE_ID, "450");
			loHashMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "10663");
			moPaymentModuleService.fetchPaymentLineDetails(moSession, loHashMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	public void testFetchPaymentLineDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			HashMap<String, String> loHashMap = new HashMap<String, String>();
			loHashMap.put(HHSConstants.INVOICE_ID, "450");
			loHashMap.put(HHSConstants.PAYMENT_ID_WORKFLOW, "49");
			moPaymentModuleService.fetchPaymentLineDetails(moSession, loHashMap);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertEquals("Error occured while executing service: ", loAppEx.getMessage());
			assertTrue(lbThrown);
		}
	}
	
	@Test
	public void testValidateCoATotalPayment() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setAgencyId("DOC");
		aoTaskDetailsBean.setPaymentId("11118");
		aoTaskDetailsBean.setInvoiceId("374");
		aoTaskDetailsBean.setBudgetAdvanceId("130600007");
		moPaymentModuleService.validateCoATotalPayment(moSession, aoTaskDetailsBean);
	}

	@Test
	public void testFetchAdvanceDesc1() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setAgencyId("DOC");
		aoTaskDetailsBean.setPaymentId("11118");
		aoTaskDetailsBean.setInvoiceId("374");
		aoTaskDetailsBean.setBudgetAdvanceId("130600007");
		String lsAdvanceDesc = moPaymentModuleService.fetchAdvanceDesc(moSession, aoTaskDetailsBean);
		assertNull(lsAdvanceDesc);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchAdvanceDesc2() throws ApplicationException
	{
		String lsAdvanceDesc = moPaymentModuleService.fetchAdvanceDesc(moSession, new TaskDetailsBean());
		assertNotNull(lsAdvanceDesc);
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicefetchContractInfoForPayment0Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.fetchContractInfoForPayment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicefetchFyBudgetSummaryForPayment1Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.fetchFyBudgetSummaryForPayment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicegetCbGridDataForSession2Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.getCbGridDataForSession(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicefetchAssignmentSummary3Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.fetchAssignmentSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServiceeditAssignmentSummary4Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.editAssignmentSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicedelAssignment5Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.delAssignment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicesetAdvanceStatusForReviewTask6Negative() throws Exception
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.setAdvanceStatusForReviewTask(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicedeleteBudgetAdvance7Negative() throws Exception
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.deleteBudgetAdvance(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicesetPaymentStatus8Negative() throws Exception
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.setPaymentStatus(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicesetInvoiceStatus9Negative() throws Exception
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.setInvoiceStatus(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicedeletePaymentRecords10Negative() throws Exception
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.deletePaymentRecords(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicefetchPaymentHeaderDetails11Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.fetchPaymentHeaderDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicefetchPaymentLineDetails12Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.fetchPaymentLineDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicefetchPaymentVoucherList13Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.fetchPaymentVoucherList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicefetchPaymentReviewHeaderDetails14Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.fetchPaymentReviewHeaderDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicevalidateCoATotalPayment15Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.validateCoATotalPayment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicefetchpaymentCOF16Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.fetchpaymentCOF(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServiceupdatepaymentCOF17Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.updatepaymentCOF(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicefetchAdvancePaymentHeaderDetails18Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.fetchAdvancePaymentHeaderDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicefetchAdvancePaymentLineDetails19Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.fetchAdvancePaymentLineDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicefetchAdvancePaymentVoucherList20Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.fetchAdvancePaymentVoucherList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicefetchAdvancePaymentReviewDetails21Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.fetchAdvancePaymentReviewDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicevalidateLevelOneAdvanceRequestFinishTask22Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.validateLevelOneAdvanceRequestFinishTask(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentModuleServicefetchAdvanceDesc23Negative()
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		try
		{
			loPaymentModuleService.fetchAdvanceDesc(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}
	
	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicefetchContractInfoForPayment0NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.fetchContractInfoForPayment(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicefetchFyBudgetSummaryForPayment1NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.fetchFyBudgetSummaryForPayment(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicegetCbGridDataForSession2NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.getCbGridDataForSession(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicefetchAssignmentSummary3NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.fetchAssignmentSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServiceeditAssignmentSummary4NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.editAssignmentSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicedelAssignment5NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.delAssignment(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicesetAdvanceStatusForReviewTask6NegativeApp() throws Exception
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.setAdvanceStatusForReviewTask(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicedeleteBudgetAdvance7NegativeApp() throws Exception
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.deleteBudgetAdvance(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicesetPaymentStatus8NegativeApp() throws Exception
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.setPaymentStatus(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicesetInvoiceStatus9NegativeApp() throws Exception
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.setInvoiceStatus(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicedeletePaymentRecords10NegativeApp() throws Exception
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.deletePaymentRecords(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicefetchPaymentHeaderDetails11NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.fetchPaymentHeaderDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicefetchPaymentLineDetails12NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.fetchPaymentLineDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicefetchPaymentVoucherList13NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.fetchPaymentVoucherList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicefetchPaymentReviewHeaderDetails14NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.fetchPaymentReviewHeaderDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicevalidateCoATotalPayment15NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.validateCoATotalPayment(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicefetchpaymentCOF16NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.fetchpaymentCOF(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServiceupdatepaymentCOF17NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.updatepaymentCOF(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicefetchAdvancePaymentHeaderDetails18NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.fetchAdvancePaymentHeaderDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicefetchAdvancePaymentLineDetails19NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.fetchAdvancePaymentLineDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicefetchAdvancePaymentVoucherList20NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.fetchAdvancePaymentVoucherList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicefetchAdvancePaymentReviewDetails21NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.fetchAdvancePaymentReviewDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicevalidateLevelOneAdvanceRequestFinishTask22NegativeApp()
			throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.validateLevelOneAdvanceRequestFinishTask(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentModuleServicefetchAdvanceDesc23NegativeApp() throws ApplicationException
	{
		PaymentModuleService loPaymentModuleService = new PaymentModuleService();
		loPaymentModuleService.fetchAdvanceDesc(null, null);
	}


}
