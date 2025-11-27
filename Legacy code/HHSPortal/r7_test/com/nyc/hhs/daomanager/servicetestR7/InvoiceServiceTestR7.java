package com.nyc.hhs.daomanager.servicetestR7;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.InvoiceService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBProgramIncomeBean;
import com.nyc.hhs.model.CBServicesBean;
import com.nyc.hhs.model.MasterBean;
import com.nyc.hhs.util.DAOUtil;

/*@RunWith(PowerMockRunner.class)
@PrepareForTest(DAOUtil.class)*/
public class InvoiceServiceTestR7
{
	InvoiceService invoiceService=new InvoiceService();
	private static SqlSession moSession = null; // SQL Session
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
			/*
			 * lbThrown = true; assertTrue("Exception thrown", lbThrown);
			 */
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
		finally
		{

		}
	}
	
	@Test(expected = ApplicationException.class)
	public void getModificationBudgetCountTest1() throws ApplicationException
	{
		HashMap<String, String> loMap=new HashMap<String, String>(); 
		loMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "11561");
	/*	PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
*/		invoiceService.getModificationBudgetCount(null,loMap);
	}
	
	@Test(expected = ApplicationException.class)
	public void getModificationBudgetCountTest2() throws ApplicationException
	
	{
		HashMap<String, String> loMap=new HashMap<String, String>();
		loMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "11050");
		invoiceService.getModificationBudgetCount(null,loMap);
	}
	
	@Test
	public void getModificationBudgetCountTest3() throws ApplicationException
	{
		HashMap<String, String> loMap=new HashMap<String, String>();
		loMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "11561");
		String lsCount=invoiceService.getModificationBudgetCount(moSession,loMap);
		assertEquals(lsCount,"0");
	}
	
	/**
	 * This method tests updateProgramIncomeInvoice method for good data inputs
	 * for scenario where an already existing line item entry is updated
	 */
	@Test
	public void testUpdateProgramIncomeInvoice() throws ApplicationException
	{
		
		CBProgramIncomeBean loCBProgramIncomeBean = new CBProgramIncomeBean();
		loCBProgramIncomeBean.setInvoiceId("21809");
		loCBProgramIncomeBean.setSubBudgetID("14156");
		loCBProgramIncomeBean.setId("71531");
		Boolean liResult = invoiceService.updateProgramIncomeInvoice(moSession, loCBProgramIncomeBean);
		assertTrue(liResult);
	}

	/**
	 * This method tests updateProgramIncomeInvoice method for good data inputs
	 * for scenario where a new line item entry is made when does not exists
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProgramIncomeInvoiceForNewItemInsert() throws ApplicationException
	{
		InvoiceService loContractBudgetService = new InvoiceService();
		CBProgramIncomeBean loCBProgramIncomeBean = new CBProgramIncomeBean();
		loCBProgramIncomeBean.setInvoiceId("21809");
		loCBProgramIncomeBean.setSubBudgetID("14156");
		loCBProgramIncomeBean.setId("8436");
		Boolean liResult = loContractBudgetService.updateProgramIncomeInvoice(moSession, loCBProgramIncomeBean);
		assertTrue(liResult);
	}
	
	/**
	 * This method tests updateProgramIncomeInvoice method for good data inputs
	 * for scenario where a new line item entry is made when does not exists
	 * 
	 * @throws ApplicationException If an Application Exception occurs
	 */
	@Test
	public void testUpdateProgramIncomeInvoiceForNewItemInsert2() throws ApplicationException
	{
		InvoiceService loContractBudgetService = new InvoiceService();
		CBProgramIncomeBean loCBProgramIncomeBean = new CBProgramIncomeBean();
		loCBProgramIncomeBean.setInvoiceId("21079");
		loCBProgramIncomeBean.setSubBudgetID("14156");
		loCBProgramIncomeBean.setId("8436");
		Boolean liResult = loContractBudgetService.updateProgramIncomeInvoice(moSession, loCBProgramIncomeBean);
		assertTrue(liResult);
	}
	/**
	 * This method tests updateProgramIncomeInvoice method for bad data inputs
	 * and an ApplicartionException is expected
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateProgramIncomeInvoiceWithAppException() throws ApplicationException
	{
		InvoiceService loContractBudgetService = new InvoiceService();
		CBProgramIncomeBean loCBProgramIncomeBean = new CBProgramIncomeBean();
		loCBProgramIncomeBean.setId("");// Invalid programIncomeId
		loCBProgramIncomeBean.setInvoiceId("21809");
		loCBProgramIncomeBean.setSubBudgetID("14156");
		loContractBudgetService.updateProgramIncomeInvoice(moSession, loCBProgramIncomeBean);
	}
	
	@Test
	public void fetchPaymentsnotAtLevel1Test1() throws ApplicationException
	{
		HashMap<String, String> aoPaymentDetailMap=new HashMap<String, String>();
		aoPaymentDetailMap.put(HHSConstants.PAYMENT_ID, "18357");
		aoPaymentDetailMap.put(HHSConstants.INVOICE_ID, "21870");
		invoiceService.fetchPaymentsnotAtLevel1(moSession,aoPaymentDetailMap);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchPaymentsnotAtLevel1Test2() throws ApplicationException
	
	{
		HashMap<String, String> aoPaymentDetailMap=new HashMap<String, String>();
		aoPaymentDetailMap.put(HHSConstants.PAYMENT_ID, "18136");
		aoPaymentDetailMap.put(HHSConstants.INVOICE_ID, "21804");
		invoiceService.fetchPaymentsnotAtLevel1(null,aoPaymentDetailMap);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchPaymentsnotAtLevel1Test3() throws ApplicationException
	
	{
		HashMap<String, String> aoPaymentDetailMap=new HashMap<String, String>();
		aoPaymentDetailMap.put(HHSConstants.PAYMENT_ID, "18136");
		aoPaymentDetailMap.put(HHSConstants.INVOICE_ID, "21804");
		invoiceService.fetchPaymentsnotAtLevel1(null,aoPaymentDetailMap);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchServicesInvoiceGrid1() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21951");
		aoCBGridBeanObj.setSubBudgetID("16382");
		invoiceService.fetchServicesInvoiceGrid(null,aoCBGridBeanObj);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchServicesInvoiceGrid2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21950");
		aoCBGridBeanObj.setSubBudgetID("16381");
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		invoiceService.fetchServicesInvoiceGrid(aoCBGridBeanObj,moSession);
	}
	
	@Test
	public void fetchServicesInvoiceGrid3() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21949");
		aoCBGridBeanObj.setSubBudgetID("16389");
		List<CBServicesBean> list = invoiceService.fetchServicesInvoiceGrid(aoCBGridBeanObj,moSession);
		assertEquals(1, list.size());
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchCostCenterInvoiceGrid1() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21951");
		aoCBGridBeanObj.setSubBudgetID("16382");
		invoiceService.fetchCostCenterInvoiceGrid(null,aoCBGridBeanObj);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchCostCenterInvoiceGrid2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21950");
		aoCBGridBeanObj.setSubBudgetID("16381");
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		invoiceService.fetchCostCenterInvoiceGrid(aoCBGridBeanObj,moSession);
	}
	
	@Test
	public void fetchCostCenterInvoiceGrid3() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21949");
		aoCBGridBeanObj.setSubBudgetID("16389");
		List<CBServicesBean> list = invoiceService.fetchCostCenterInvoiceGrid(aoCBGridBeanObj,moSession);
		assertEquals(1, list.size());
	}
	
	@Test(expected = ApplicationException.class)
	public void editCostCenterInvoiceDetails1() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setLineItemId("1263");
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21951");
		aoCBGridBeanObj.setSubBudgetID("16382");
		invoiceService.editCostCenterInvoiceDetails(true,null,aoCBGridBeanObj);
	}
	
	@Test(expected = ApplicationException.class)
	public void editCostCenterInvoiceDetails2() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setLineItemId("1263");
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21950");
		aoCBGridBeanObj.setSubBudgetID("16381");
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		invoiceService.editCostCenterInvoiceDetails(true,aoCBGridBeanObj,moSession);
	}
	
	@Test
	public void editCostCenterInvoiceDetails3() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setLineItemId("1263");
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21949");
		aoCBGridBeanObj.setSubBudgetID("16389");
		assertEquals(true, invoiceService.editCostCenterInvoiceDetails(true,aoCBGridBeanObj,moSession));
	}
	
	@Test
	public void editCostCenterInvoiceDetails4() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setLineItemId("1263");
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21949");
		aoCBGridBeanObj.setSubBudgetID("16389");
		assertEquals(true, invoiceService.editCostCenterInvoiceDetails(false,aoCBGridBeanObj,moSession));
	}
	@Test
	public void editCostCenterInvoiceDetails5() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setLineItemId("1264");
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21949");
		aoCBGridBeanObj.setSubBudgetID("16389");
		assertEquals(true, invoiceService.editCostCenterInvoiceDetails(false,aoCBGridBeanObj,moSession));
	}
	
	@Test(expected = ApplicationException.class)
	public void validateCostCenterInvoiceAmount1() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("1263");
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21951");
		aoCBGridBeanObj.setSubBudgetID("16382");
		invoiceService.validateCostCenterInvoiceAmount(aoCBGridBeanObj,null);
	}
	
	@Test(expected = ApplicationException.class)
	public void validateCostCenterInvoiceAmount2() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("1263");
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21950");
		aoCBGridBeanObj.setSubBudgetID("16381");
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		invoiceService.validateCostCenterInvoiceAmount(aoCBGridBeanObj,moSession);
	}
	
	@Test(expected = ApplicationException.class)
	public void validateCostCenterInvoiceAmount3() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("1263");
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setYtdInvoicedAmt("2000");
		aoCBGridBeanObj.setInvoiceId("21950");
		aoCBGridBeanObj.setSubBudgetID("16381");
		invoiceService.validateCostCenterInvoiceAmount(aoCBGridBeanObj,moSession);
	}
	
	@Test(expected = ApplicationException.class)
	public void validateCostCenterInvoiceAmount4() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("1263");
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setYtdInvoicedAmt("-2000");
		aoCBGridBeanObj.setInvoiceId("21950");
		aoCBGridBeanObj.setSubBudgetID("16381");
		invoiceService.validateCostCenterInvoiceAmount(aoCBGridBeanObj,moSession);
	}
	
	@Test(expected = ApplicationException.class)
	public void validateCostCenterInvoiceAmount5() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("1263");
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setYtdInvoicedAmt("-5000");
		aoCBGridBeanObj.setInvoiceId("21950");
		aoCBGridBeanObj.setSubBudgetID("16381");
		invoiceService.validateCostCenterInvoiceAmount(aoCBGridBeanObj,moSession);
	}
	
	@Test(expected = ApplicationException.class)
	public void validateCostCenterInvoiceAmount6() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("1263");
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setYtdInvoicedAmt("5000");
		aoCBGridBeanObj.setInvoiceId("21950");
		aoCBGridBeanObj.setSubBudgetID("16381");
		invoiceService.validateCostCenterInvoiceAmount(aoCBGridBeanObj,moSession);
	}
	
	@Test(expected = ApplicationException.class)
	public void invoiceAmountServicesAssignmentValidation1() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("1263");
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21951");
		aoCBGridBeanObj.setSubBudgetID("16382");
		invoiceService.invoiceAmountServicesAssignmentValidation(null,"21951","12240");
	}
	
	@Test(expected = ApplicationException.class)
	public void invoiceAmountServicesAssignmentValidation2() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("1263");
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21950");
		aoCBGridBeanObj.setSubBudgetID("16381");
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		invoiceService.invoiceAmountServicesAssignmentValidation(moSession,"21951","12240");
	}
	
	@Test(expected = ApplicationException.class)
	public void invoiceAmountServicesAssignmentValidation3() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("1263");
		aoCBGridBeanObj.setContractBudgetID("12240");
		aoCBGridBeanObj.setInvoiceId("21950");
		aoCBGridBeanObj.setSubBudgetID("16381");
		invoiceService.invoiceAmountServicesAssignmentValidation(moSession,"21951","12240");
	}
	
	@Test(expected = ApplicationException.class)
	public void invoiceAmountServicesAssignmentValidation4() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("1263");
		aoCBGridBeanObj.setContractBudgetID("12239");
		aoCBGridBeanObj.setInvoiceId("21950");
		aoCBGridBeanObj.setSubBudgetID("16381");
		invoiceService.invoiceAmountServicesAssignmentValidation(moSession,"21951","12239");
	}
	
	@Test(expected = ApplicationException.class)
	public void invoiceAmountServicesAssignmentValidation5() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("1263");
		aoCBGridBeanObj.setContractBudgetID("12239");
		aoCBGridBeanObj.setInvoiceId("21950");
		aoCBGridBeanObj.setSubBudgetID("16381");
		invoiceService.invoiceAmountServicesAssignmentValidation(moSession,"21952","12240");
	}
	
	@Test(expected = ApplicationException.class)
	public void invoiceAmountServicesAssignmentValidation6() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("1263");
		aoCBGridBeanObj.setContractBudgetID("12239");
		aoCBGridBeanObj.setInvoiceId("21950");
		aoCBGridBeanObj.setSubBudgetID("16381");
		invoiceService.invoiceAmountServicesAssignmentValidation(moSession,"21953","12240");
	}
	
	@Test(expected = ApplicationException.class)
	public void invoiceAmountServicesAssignmentValidation7() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("1263");
		aoCBGridBeanObj.setContractBudgetID("12239");
		aoCBGridBeanObj.setInvoiceId("21950");
		aoCBGridBeanObj.setSubBudgetID("16381");
		invoiceService.invoiceAmountServicesAssignmentValidation(moSession,"21951","12240");
	}
}


