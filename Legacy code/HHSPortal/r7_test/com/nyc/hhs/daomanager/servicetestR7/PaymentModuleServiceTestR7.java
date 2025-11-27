package com.nyc.hhs.daomanager.servicetestR7;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.InvoiceService;
import com.nyc.hhs.daomanager.service.PaymentModuleService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBServicesBean;
import com.nyc.hhs.model.PaymentChartOfAllocation;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class PaymentModuleServiceTestR7
{
	PaymentModuleService paymentModuleService = new PaymentModuleService();
	private static SqlSession moSession = null; // SQL Session
	private static P8UserSession loUserSession; // FilenetSession

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			loUserSession = getFileNetSession();
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
		/*
		 * catch (Exception loEx) { lbThrown = true;
		 * assertTrue("Exception thrown", lbThrown); }
		 */
		finally
		{
			/*
			 * moSession.rollback(); moSession.close();
			 */
		}
	}

	/**
	 * 
	 * @return
	 * @throws ApplicationException
	 */
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

		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
		return loUserSession;
	}
	
	@Test
	public void testEetchpaymentCOF1() throws ApplicationException
	{
		PaymentModuleService lopaymentModuleService = new PaymentModuleService();
		List<PaymentChartOfAllocation> loPaymentChartOfAllocationList = null;
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setSubBudgetID("14148");
		aoCBGridBean.setContractID("11195");
		HashMap<String, String> loApplicationSettingMap =  new HashMap<String, String>(); 
		loApplicationSettingMap.put(HHSConstants.PREV_DATE_HOUR_KEY, "16:00:00");
		List<CBServicesBean> loServiceData = lopaymentModuleService.fetchpaymentCOF(moSession, aoCBGridBean);
		assertNotNull(loServiceData);
	}

	@Test
	public void testEetchpaymentCOF2() throws ApplicationException
	{
		PaymentModuleService lopaymentModuleService = new PaymentModuleService();
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setContractBudgetID("14148");
		aoCBGridBean.setSubBudgetID("11195");
		List<CBServicesBean> loServiceData = lopaymentModuleService.fetchpaymentCOF(moSession, aoCBGridBean);
		assertNotNull(loServiceData);
	}
	

	@Test(expected = ApplicationException.class)
	public void  testEetchpaymentCOF3() throws ApplicationException
	{
		PaymentModuleService lopaymentModuleService = new PaymentModuleService();
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setContractBudgetID("14148");
		aoCBGridBean.setSubBudgetID("11195");
			List<CBServicesBean> loServiceData = lopaymentModuleService.fetchpaymentCOF(null, aoCBGridBean);
	}
	
	@Test
	public void testFetchPaymentsnotAtLevel11() throws ApplicationException
	{
		InvoiceService loinvoiceService = new InvoiceService();
		HashMap<String, String> aoPaymentDetailMap =  new HashMap<String, String>(); 
		aoPaymentDetailMap.put("invoiceId", "21804");
		aoPaymentDetailMap.put("budgetAdvanceId","5549");
		List<String> loServiceData = loinvoiceService.fetchPaymentsnotAtLevel1(moSession, aoPaymentDetailMap);
		assertNotNull(loServiceData);
	}

	@Test
	public void testFetchPaymentsnotAtLevel12() throws ApplicationException
	{
		InvoiceService loinvoiceService = new InvoiceService();
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		CBGridBean aoCBGridBean = new CBGridBean();
		HashMap<String, String> aoPaymentDetailMap =  new HashMap<String, String>();
		List<String> loServiceData = loinvoiceService.fetchPaymentsnotAtLevel1(moSession, aoPaymentDetailMap);
		assertNotNull(loServiceData);
	}
	

	@Test(expected = ApplicationException.class)
	public void  testFetchPaymentsnotAtLevel13() throws ApplicationException
	{
		InvoiceService loinvoiceService = new InvoiceService();
		HashMap<String, String> aoPaymentDetailMap =  new HashMap<String, String>();
		aoPaymentDetailMap.put("invoiceId", "21804");
		aoPaymentDetailMap.put("budgetAdvanceId","5549");
			List<String> loServiceData = loinvoiceService.fetchPaymentsnotAtLevel1(null, aoPaymentDetailMap);
	}
	
}
