package com.nyc.hhs.daomanager.servicetest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.nyc.hhs.daomanager.service.ReturnedPaymentService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ReturnedPayment;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;


@RunWith(PowerMockRunner.class)
@PrepareForTest(DAOUtil.class)
public class ReturnedPaymentServiceTestR6
{
	ReturnedPaymentService returnedPaymentService = new ReturnedPaymentService();
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
		// loP8SecurityService.getPESession(loUserSession);
		// loP8SecurityService.getObjectStore(loUserSession);
		return loUserSession;

	}
	
	
	@Test(expected = ApplicationException.class)
	public void cancelReturnedPaymentTest1() throws ApplicationException
	{
		ReturnedPayment loReturnedPayment = new ReturnedPayment();
		returnedPaymentService.cancelReturnedPayment(moSession, loReturnedPayment);
	}
	
	@Test
	public void cancelReturnedPaymentTest2() throws Exception
	{
		ReturnedPayment loReturnedPayment = new ReturnedPayment();
		loReturnedPayment.setReturnedPaymentId("456");
		returnedPaymentService.cancelReturnedPayment(moSession, loReturnedPayment);
	}
	
	@Test(expected = ApplicationException.class)
	public void cancelReturnedPaymentTest3() throws Exception
	{	
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		String asReturnedPaymentId = "123";
		ReturnedPayment loReturnedPayment = new ReturnedPayment();
		returnedPaymentService.cancelReturnedPayment(moSession, loReturnedPayment);
	}
	
	@Test
	public void cancelReturnedPaymentTest4() throws Exception
	{
		ReturnedPayment loReturnedPayment = new ReturnedPayment();
		loReturnedPayment.setReturnedPaymentId("456");
		returnedPaymentService.cancelReturnedPayment(moSession, loReturnedPayment);
	}
	
	@Test(expected = ApplicationException.class)
	public void initiateReturnedPaymentTest1() throws ApplicationException
	{
		ReturnedPayment loReturnedPayment = new ReturnedPayment();
		loReturnedPayment.setStatusId("185");
		loReturnedPayment.setModifiedByUserId("agency_21");
		loReturnedPayment.setReturnedPaymentId("456");
		returnedPaymentService.initiateReturnedPayment(moSession, loReturnedPayment);
	}
	
	@Test
	public void initiateReturnedPaymentTest2() throws ApplicationException
	{
		ReturnedPayment loReturnedPayment = new ReturnedPayment();
		loReturnedPayment.setStatusId("185");
		loReturnedPayment.setModifiedByUserId("agency_21");
		loReturnedPayment.setReturnedPaymentId("123");
		returnedPaymentService.initiateReturnedPayment(moSession, loReturnedPayment);
	}
	
	@Test(expected = ApplicationException.class)
	public void initiateReturnedPaymentTest3() throws ApplicationException
	{
		
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		ReturnedPayment loReturnedPayment = new ReturnedPayment();
		returnedPaymentService.initiateReturnedPayment(moSession, loReturnedPayment);
	}
	
	@Test
	public void initiateReturnedPaymentTest4() throws ApplicationException
	{
		ReturnedPayment loReturnedPayment = new ReturnedPayment(); 
		loReturnedPayment.setStatusId("185");
		loReturnedPayment.setModifiedByUserId("agency_21");
		loReturnedPayment.setReturnedPaymentId("123");
		returnedPaymentService.initiateReturnedPayment(moSession, loReturnedPayment);
	}
	
	@Test(expected = ApplicationException.class)
	public void addReturnPaymentInfoTest1() throws ApplicationException
	{
		ReturnedPayment returnedPayment = null;
		returnedPaymentService.addReturnPaymentInfo(moSession, returnedPayment);
	}
	
	@Test(expected = ApplicationException.class)
	public void addReturnPaymentInfoTest2() throws ApplicationException
	{
		
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		ReturnedPayment returnedPayment = null;
		returnedPaymentService.addReturnPaymentInfo(moSession, returnedPayment);
	}
	
	@Test
	public void addReturnPaymentInfoTest3() throws ApplicationException
	{
		ReturnedPayment returnedPayment = new ReturnedPayment();
		returnedPayment.setBudgetId("10730");
		returnedPayment.setCheckAmount("1234");
		returnedPayment.setCheckStatus("123");
		returnedPayment.setDescription("hjghjghj");
		returnedPayment.setCreatedByUserId("123");
		returnedPayment.setModifiedByUserId("123");
		returnedPayment.setNotifyProvider("k");
		returnedPayment.setCheckReceived("k");
		returnedPayment.setAgencyTrackingNumber("hjgjg");
		
		returnedPaymentService.addReturnPaymentInfo(moSession, returnedPayment);
	}
	@Test(expected = ApplicationException.class)
	public void fetchReturnedPaymentDetailsTest1() throws ApplicationException
	{
		ReturnedPayment returnedPayment = new ReturnedPayment();
		returnedPaymentService.fetchReturnedPaymentDetails(null, returnedPayment);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchReturnedPaymentDetailsTest2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		ReturnedPayment returnedPayment = new ReturnedPayment();
		returnedPaymentService.fetchReturnedPaymentDetails(moSession, returnedPayment);
	}
	
	@Test
	public void fetchReturnedPaymentDetailsTest3() throws ApplicationException
	{
		ReturnedPayment returnedPayment = new ReturnedPayment();
		returnedPayment.setBudgetId("10730");
		returnedPaymentService.fetchReturnedPaymentDetails(moSession, returnedPayment);
	}
	
	@Test(expected = ApplicationException.class)
	public void getLastNotifiedDate1() throws ApplicationException
	{
		String budgetId = "10739";
		returnedPaymentService.getLastNotifiedDate(null, budgetId);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void getLastNotifiedDate2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		String budgetId = "10739";
		returnedPaymentService.getLastNotifiedDate(moSession, budgetId);
	}
	
	@Test
	public void getLastNotifiedDate3() throws ApplicationException
	{
		String budgetId = "10739";
		returnedPaymentService.getLastNotifiedDate(moSession, budgetId);
		
	}
	
	@Test
	public void getLastNotifiedDate4() throws ApplicationException
	{
		String budgetId = null;
		returnedPaymentService.getLastNotifiedDate(moSession, budgetId);
		
	}
	
	@Test
	public void getLastNotifiedDate5() throws ApplicationException
	{
		String budgetId = "";
		returnedPaymentService.getLastNotifiedDate(moSession, budgetId);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void getUnRecoupedAmount1() throws ApplicationException
	{
		String budgetId = "10739";
		returnedPaymentService.getUnRecoupedAmount(null, budgetId);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void getUnRecoupedAmount2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		String budgetId = "10739";
		returnedPaymentService.getUnRecoupedAmount(moSession, budgetId);
	}
	
	@Test
	public void getUnRecoupedAmount3() throws ApplicationException
	{
		String budgetId = "10739";
		returnedPaymentService.getUnRecoupedAmount(moSession, budgetId);
		
	}
	
	@Test
	public void getUnRecoupedAmount4() throws ApplicationException
	{
		String budgetId = null;
		returnedPaymentService.getUnRecoupedAmount(moSession, budgetId);
		
	}
	
	@Test
	public void getUnRecoupedAmount5() throws ApplicationException
	{
		String budgetId = "";
		returnedPaymentService.getUnRecoupedAmount(moSession, budgetId);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void getTotalApprovedRetPayAmount1() throws ApplicationException
	{
		String budgetId = "10739";
		returnedPaymentService.getTotalApprovedRetPayAmount(null, budgetId);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void getTotalApprovedRetPayAmount2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		String budgetId = "10739";
		returnedPaymentService.getTotalApprovedRetPayAmount(moSession, budgetId);
	}
	
	@Test
	public void getTotalApprovedRetPayAmount3() throws ApplicationException
	{
		String budgetId = "10739";
		returnedPaymentService.getTotalApprovedRetPayAmount(moSession, budgetId);
		
	}
	
	@Test
	public void getTotalApprovedRetPayAmount4() throws ApplicationException
	{
		String budgetId = null;
		returnedPaymentService.getTotalApprovedRetPayAmount(moSession, budgetId);
		
	}
	
	@Test
	public void getTotalApprovedRetPayAmount5() throws ApplicationException
	{
		String budgetId = "";
		returnedPaymentService.getTotalApprovedRetPayAmount(moSession, budgetId);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void getNotificationHistory1() throws ApplicationException
	{
		String budgetId = "10739";
		returnedPaymentService.getNotificationHistory(null, budgetId);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void getNotificationHistory2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		String budgetId = "10739";
		returnedPaymentService.getNotificationHistory(moSession, budgetId);
	}
	
	@Test
	public void getNotificationHistory3() throws ApplicationException
	{
		String budgetId = "10739";
		returnedPaymentService.getNotificationHistory(moSession, budgetId);
		
	}
	
	@Test
	public void getNotificationHistory4() throws ApplicationException
	{
		String budgetId = null;
		returnedPaymentService.getNotificationHistory(moSession, budgetId);
		
	}
	
	@Test
	public void getNotificationHistory5() throws ApplicationException
	{
		String budgetId = "";
		returnedPaymentService.getNotificationHistory(moSession, budgetId);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void getReturnedPaymentSummaryDB1() throws ApplicationException
	{
		String asReturnedPaymentId = "351";
		returnedPaymentService.getReturnedPaymentSummaryDB(null, asReturnedPaymentId);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void getReturnedPaymentSummaryDB2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		String asReturnedPaymentId = "351";
		returnedPaymentService.getReturnedPaymentSummaryDB(moSession, asReturnedPaymentId);
	}
	
	@Test
	public void getReturnedPaymentSummaryDB3() throws ApplicationException
	{
		String asReturnedPaymentId = "351";
		returnedPaymentService.getReturnedPaymentSummaryDB(moSession, asReturnedPaymentId);
		
	}
	
	@Test
	public void getReturnedPaymentSummaryDB4() throws ApplicationException
	{
		String asReturnedPaymentId = null;
		returnedPaymentService.getReturnedPaymentSummaryDB(moSession, asReturnedPaymentId);
		
	}
	
	@Test
	public void getReturnedPaymentSummaryDB5() throws ApplicationException
	{
		String asReturnedPaymentId = "";
		returnedPaymentService.getReturnedPaymentSummaryDB(moSession, asReturnedPaymentId);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void getDocumentForReturnedPaymentSumaryFN1() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		String asReturnedPaymentId = "351";
		returnedPaymentService.getDocumentForReturnedPaymentSumaryFN(null, asReturnedPaymentId);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void getDocumentForReturnedPaymentSumaryFN2() throws ApplicationException
	{
		String asReturnedPaymentId = "351";
		returnedPaymentService.getDocumentForReturnedPaymentSumaryFN(null, asReturnedPaymentId);
		
	}
	
	@Test
	public void getDocumentForReturnedPaymentSumaryFN3() throws ApplicationException
	{
		String asReturnedPaymentId = "351";
		returnedPaymentService.getDocumentForReturnedPaymentSumaryFN(moSession, asReturnedPaymentId);
		
	}
	
	@Test
	public void getDocumentForReturnedPaymentSumaryFN4() throws ApplicationException
	{
		String asReturnedPaymentId = null;
		returnedPaymentService.getDocumentForReturnedPaymentSumaryFN(moSession, asReturnedPaymentId);
		
	}
	
	@Test
	public void getDocumentForReturnedPaymentSumaryFN5() throws ApplicationException
	{
		String asReturnedPaymentId = "";
		returnedPaymentService.getDocumentForReturnedPaymentSumaryFN(moSession, asReturnedPaymentId);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void saveReturnPaymentDetails1() throws ApplicationException
	{
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put("returnPaymentDetailId", "351");
		returnedPaymentService.saveReturnPaymentDetails(null, aoHashMap);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void saveReturnPaymentDetails2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put("returnPaymentDetailId", "351");
		returnedPaymentService.saveReturnPaymentDetails(null, aoHashMap);
		
	}
	
	@Test
	public void saveReturnPaymentDetails3() throws ApplicationException
	{
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put("returnPaymentDetailId", "351");
		aoHashMap.put("checkNumber", "351");
		aoHashMap.put("checkDate", "08/08/1992");
		aoHashMap.put("receivedDate", "08/08/1992");
		aoHashMap.put("descriptionInput", "351");
		aoHashMap.put("checkAmount", "351");
		aoHashMap.put("agencyTracking", "351");
		returnedPaymentService.saveReturnPaymentDetails(moSession, aoHashMap);
		
	}
	
	@Test
	public void saveReturnPaymentDetails4() throws ApplicationException
	{
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put("returnPaymentDetailId", null);
		aoHashMap.put("checkNumber", "351");
		aoHashMap.put("checkDate", "08/08/1992");
		aoHashMap.put("receivedDate", "08/08/1992");
		aoHashMap.put("descriptionInput", "351");
		aoHashMap.put("checkAmount", "351");
		aoHashMap.put("agencyTracking", "351");
		returnedPaymentService.saveReturnPaymentDetails(moSession, aoHashMap);
		
	}
	
	@Test
	public void saveReturnPaymentDetails5() throws ApplicationException
	{
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put("returnPaymentDetailId", "456");
		aoHashMap.put("checkNumber", "351");
		aoHashMap.put("checkDate", "08/08/1992");
		aoHashMap.put("receivedDate", "08/08/1992");
		aoHashMap.put("descriptionInput", "351");
		aoHashMap.put("checkAmount", "351");
		aoHashMap.put("agencyTracking", "351");
		returnedPaymentService.saveReturnPaymentDetails(moSession, aoHashMap);
		
	}
	
	@Test
	public void saveReturnPaymentDetails6() throws ApplicationException
	{
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put("returnPaymentDetailId", "456");
		aoHashMap.put("checkNumber", "351");
		aoHashMap.put("checkDate", "08/08/1992");
		aoHashMap.put("receivedDate", "08/08/1992");
		aoHashMap.put("descriptionInput", "351");
		aoHashMap.put("checkAmount", "351");
		aoHashMap.put("agencyTracking", "351");
		returnedPaymentService.saveReturnPaymentDetails(moSession, aoHashMap);
		
	}
	@Test
	public void saveReturnPaymentDetails7() throws ApplicationException
	{
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put("returnPaymentDetailId", "456");
		aoHashMap.put("checkNumber", "351");
		aoHashMap.put("checkDate", "08/08/1992");
		aoHashMap.put("receivedDate", "08/08/1992");
		aoHashMap.put("descriptionInput", "351");
		aoHashMap.put("checkAmount", "351");
		aoHashMap.put("agencyTracking", "351");
		returnedPaymentService.saveReturnPaymentDetails(moSession, aoHashMap);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchReturnPaymentDetails1() throws ApplicationException
	{
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put("returnPaymentDetailId", "351");
		returnedPaymentService.fetchReturnPaymentDetails(null, aoHashMap);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchReturnPaymentDetails2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put("returnPaymentDetailId", "351");
		returnedPaymentService.fetchReturnPaymentDetails(null, aoHashMap);
		
	}
	
	@Test
	public void fetchReturnPaymentDetails3() throws ApplicationException
	{
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put("returnPaymentDetailId", "351");
		returnedPaymentService.fetchReturnPaymentDetails(moSession, aoHashMap);
		
	}
	
	@Test
	public void fetchReturnPaymentDetails4() throws ApplicationException
	{
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put("returnPaymentDetailId", null);
		returnedPaymentService.fetchReturnPaymentDetails(moSession, aoHashMap);
		
	}
	
	@Test
	public void fetchReturnPaymentDetails5() throws ApplicationException
	{
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put("returnPaymentDetailId", "");
		returnedPaymentService.fetchReturnPaymentDetails(moSession, aoHashMap);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void updateReturnedPaymentForApprovedStatus1() throws Exception
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setReturnPaymentDetailId("351");
		aoTaskDetailsBean.setUserId("123");
		returnedPaymentService.updateReturnedPaymentForApprovedStatus(null, true, aoTaskDetailsBean, "123");
		
	}
	
	@Test(expected = ApplicationException.class)
	public void updateReturnedPaymentForApprovedStatus2() throws Exception
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setReturnPaymentDetailId("351");
		aoTaskDetailsBean.setUserId("123");
		returnedPaymentService.updateReturnedPaymentForApprovedStatus(null, true, aoTaskDetailsBean, "123");
		
	}
	
	@Test
	public void updateReturnedPaymentForApprovedStatus3() throws Exception
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setReturnPaymentDetailId("351");
		aoTaskDetailsBean.setUserId("123");
		returnedPaymentService.updateReturnedPaymentForApprovedStatus(moSession, true, aoTaskDetailsBean, "123");
		
	}
	
	@Test
	public void updateReturnedPaymentForApprovedStatus4() throws Exception
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setReturnPaymentDetailId("351");
		aoTaskDetailsBean.setUserId("123");
		returnedPaymentService.updateReturnedPaymentForApprovedStatus(moSession, false, aoTaskDetailsBean, "123");
		
	}
	
	@Test
	public void updateReturnedPaymentForApprovedStatus5() throws Exception
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setReturnPaymentDetailId("3781");
		aoTaskDetailsBean.setUserId("123");
		returnedPaymentService.updateReturnedPaymentForApprovedStatus(moSession, false, aoTaskDetailsBean, "123");
		
	}
	
	@Test(expected = ApplicationException.class)
	public void insertReturnedPaymentDocumentDetails1() throws Exception
	{
		Map<String, Object> aoParamMap = null;
		returnedPaymentService.insertReturnedPaymentDocumentDetails(null, aoParamMap);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void insertReturnedPaymentDocumentDetails2() throws Exception
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		Map<String, Object> aoParamMap = null;
		returnedPaymentService.insertReturnedPaymentDocumentDetails(moSession, aoParamMap);
		
	}
	
	@Test
	public void insertReturnedPaymentDocumentDetails3() throws Exception
	{
		Map<String, Object> aoParamMap = new HashMap<String, Object>();
		aoParamMap.put("returnPaymentDetailId", "351");
		aoParamMap.put("userId", "123");
		aoParamMap.put("documentId", "123");
		aoParamMap.put("DOC_TYPE", "jhghj");
		
		returnedPaymentService.insertReturnedPaymentDocumentDetails(moSession, aoParamMap);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void getReturnedPaymentStatus1() throws Exception
	{
		String asReturnedPaymentId = "351";
		returnedPaymentService.getReturnedPaymentStatus(null, asReturnedPaymentId);
	}
	
	@Test(expected = ApplicationException.class)
	public void getReturnedPaymentStatus2() throws Exception
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		String asReturnedPaymentId = "351";
		returnedPaymentService.getReturnedPaymentStatus(null, asReturnedPaymentId);
	}
	
	@Test
	public void getReturnedPaymentStatus3() throws Exception
	{
		String asReturnedPaymentId = "351";
		returnedPaymentService.getReturnedPaymentStatus(moSession, asReturnedPaymentId);
	}
	
	@Test
	public void getReturnedPaymentStatus4() throws Exception
	{
		String asReturnedPaymentId = null;
		returnedPaymentService.getReturnedPaymentStatus(moSession, asReturnedPaymentId);
	}
	
	@Test
	public void getReturnedPaymentStatus5() throws Exception
	{
		String asReturnedPaymentId = "";
		returnedPaymentService.getReturnedPaymentStatus(moSession, asReturnedPaymentId);
	}
	
	@Test(expected = ApplicationException.class)
	public void getDateForExportNotification1() throws Exception
	{
		String asRequestId = "143";
		returnedPaymentService.getDateForExportNotification(null, asRequestId);
	}
	
	@Test(expected = ApplicationException.class)
	public void getDateForExportNotification2() throws Exception
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		String asRequestId = "143";
		returnedPaymentService.getDateForExportNotification(null, asRequestId);
	}
	
	@Test
	public void getDateForExportNotification3() throws Exception
	{
		String asRequestId = "143";
		returnedPaymentService.getDateForExportNotification(moSession, asRequestId);
	}
	
	@Test
	public void getDateForExportNotification4() throws Exception
	{
		String asRequestId = null;
		returnedPaymentService.getDateForExportNotification(moSession, asRequestId);
	}
	
	@Test
	public void getDateForExportNotification5() throws Exception
	{
		String asRequestId = "";
		returnedPaymentService.getDateForExportNotification(moSession, asRequestId);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchFiscalYearDetails1() throws ApplicationException
	{
		String asOrgId = "DOC";
		returnedPaymentService.fetchFiscalYearDetails(null, asOrgId);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchFiscalYearDetails2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		String asOrgId = "DOC";
		returnedPaymentService.fetchFiscalYearDetails(null, asOrgId);
	}
	@Test
	public void fetchFiscalYearDetails3() throws ApplicationException
	{
		String asOrgId = "DOC";
		returnedPaymentService.fetchFiscalYearDetails(moSession, asOrgId);
	}
	
	@Test
	public void fetchFiscalYearDetails4() throws ApplicationException
	{
		String asOrgId = null;
		returnedPaymentService.fetchFiscalYearDetails(moSession, asOrgId);
	}
	
	@Test
	public void fetchFiscalYearDetails5() throws ApplicationException
	{
		String asOrgId = "";
		returnedPaymentService.fetchFiscalYearDetails(moSession, asOrgId);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchBudgetList1() throws ApplicationException
	{
		String asFiscalYear = "2017";
		String asProgramName = "";
		String asBudgetId = null;
		returnedPaymentService.fetchBudgetList(null, asFiscalYear, asProgramName, asBudgetId, true);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchBudgetList2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		String asFiscalYear = "2017";
		String asProgramName = "";
		String asBudgetId = null;
		returnedPaymentService.fetchBudgetList(null, asFiscalYear, asProgramName, asBudgetId, true);
	}
	
	@Test
	public void fetchBudgetList3() throws ApplicationException
	{
		List loList = new ArrayList();
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenReturn(loList);
		String asFiscalYear = "2017";
		String asProgramName = "";
		String asBudgetId = null;
		returnedPaymentService.fetchBudgetList(null, asFiscalYear, asProgramName, asBudgetId, true);
	}
	
	@Test
	public void fetchBudgetList4() throws ApplicationException
	{
		List loList = new ArrayList();
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenReturn(loList);
		String asFiscalYear = "2017";
		String asProgramName = "";
		String asBudgetId = null;
		returnedPaymentService.fetchBudgetList(null, asFiscalYear, asProgramName, asBudgetId, false);
	}
	
	@Test
	public void fetchBudgetList5() throws ApplicationException
	{
		List loList = new ArrayList();
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenReturn(loList);
		String asFiscalYear = "2017";
		String asProgramName = "";
		String asBudgetId = null;
		returnedPaymentService.fetchBudgetList(null, asFiscalYear, asProgramName, asBudgetId, true);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchExportNotificationList1() throws ApplicationException
	{
		String asFiscalYear = "2017";
		String asUserId = "123";
		String asProgramId ="456";
		returnedPaymentService.fetchExportNotificationList(null, asFiscalYear, asUserId, asProgramId); 
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchExportNotificationList2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		String asFiscalYear = "2017";
		String asUserId = "123";
		String asProgramId ="456";
		returnedPaymentService.fetchExportNotificationList(null, asFiscalYear, asUserId, asProgramId);
	}
	
	@Test
	public void fetchExportNotificationList3() throws ApplicationException
	{
		String asFiscalYear = "2017";
		String asUserId = "123";
		String asProgramId ="456";
		returnedPaymentService.fetchExportNotificationList(moSession, asFiscalYear, asUserId, asProgramId);
	}
	
	@Test
	public void fetchExportNotificationList4() throws ApplicationException
	{
		String asFiscalYear = "2017";
		String asUserId = "123";
		String asProgramId ="456";
		returnedPaymentService.fetchExportNotificationList(moSession, asFiscalYear, asUserId, asProgramId);
	}
	
	@Test
	public void fetchExportNotificationList5() throws ApplicationException
	{
		String asFiscalYear = null;;
		String asUserId = null;
		String asProgramId ="456";
		returnedPaymentService.fetchExportNotificationList(moSession, asFiscalYear, asUserId, asProgramId);
	}
	
	@Test(expected = ApplicationException.class)
	public void insertExportNotificationRequest1() throws ApplicationException
	{
		HashMap aoHashMap = new HashMap();
		returnedPaymentService.insertExportNotificationRequest(null, aoHashMap);
	}
	
	@Test(expected = ApplicationException.class)
	public void insertExportNotificationRequest2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		HashMap aoHashMap = new HashMap();
		returnedPaymentService.insertExportNotificationRequest(null, aoHashMap);
	}
	
	@Test
	public void insertExportNotificationRequest3() throws ApplicationException
	{
		HashMap aoHashMap = new HashMap();
		aoHashMap.put("fiscalYear", "2017");
		aoHashMap.put("userId", "123");
		aoHashMap.put("status", "191");
		aoHashMap.put("programId", "103");
		returnedPaymentService.insertExportNotificationRequest(moSession, aoHashMap);
	}
	
	@Test
	public void insertExportNotificationRequest4() throws ApplicationException
	{
		HashMap aoHashMap = new HashMap();
		aoHashMap.put("fiscalYear", "2016");
		aoHashMap.put("userId", "123");
		aoHashMap.put("status", "191");
		aoHashMap.put("programId", "103");
		returnedPaymentService.insertExportNotificationRequest(moSession, aoHashMap);
	}
	
	@Test
	public void insertExportNotificationRequest5() throws ApplicationException
	{
		HashMap aoHashMap = new HashMap();
		aoHashMap.put("fiscalYear", "2016");
		aoHashMap.put("userId", "123");
		aoHashMap.put("status", "191");
		aoHashMap.put("programId", "103");
		returnedPaymentService.insertExportNotificationRequest(moSession, aoHashMap);
	}
	
	@Test
	public void insertExportNotificationRequest6() throws ApplicationException
	{
		HashMap aoHashMap = new HashMap();
		aoHashMap.put("fiscalYear", "2016");
		aoHashMap.put("userId", "123");
		aoHashMap.put("status", "191");
		aoHashMap.put("programId", "103");
		returnedPaymentService.insertExportNotificationRequest(moSession, aoHashMap);
	}
	
	
	@Test(expected = ApplicationException.class)
	public void updateExportNotificationStatus1() throws ApplicationException
	{
		Map<String, String> aoNotificationStatus = null;
		String asNotificationId = null;
		returnedPaymentService.updateExportNotificationStatus(null, asNotificationId, aoNotificationStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void updateExportNotificationStatus2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		Map<String, String> aoNotificationStatus = null;
		String asNotificationId = null;
		returnedPaymentService.updateExportNotificationStatus(null, asNotificationId, aoNotificationStatus);
	}
	
	@Test
	public void updateExportNotificationStatus3() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenReturn(1);
		Map<String, String> aoNotificationStatus = new HashMap<String, String>();
		String asNotificationId = "191";
		returnedPaymentService.updateExportNotificationStatus(moSession, asNotificationId, aoNotificationStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void updateExportNotificationStatus11() throws ApplicationException
	{
		String asNotificationId = null;
		String asNotificationStatus = null;
		returnedPaymentService.updateExportNotificationStatus(null,  asNotificationId,  asNotificationStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void updateExportNotificationStatus12() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		String asNotificationId = null;
		String asNotificationStatus = null;
		returnedPaymentService.updateExportNotificationStatus(null,  asNotificationId,  asNotificationStatus);
	}
	
	@Test
	public void updateExportNotificationStatus13() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenReturn(1);
		String asNotificationId = null;
		String asNotificationStatus = null;
		returnedPaymentService.updateExportNotificationStatus(null,  asNotificationId,  asNotificationStatus);
	}
	
	@Test
	public void setAuditReturnedPaymentId() throws ApplicationException
	{
		ReturnedPayment aoReturnedPayment = new ReturnedPayment();
		aoReturnedPayment.setReturnedPaymentId("123");
		aoReturnedPayment.setCreatedByUserId("1234");
		Boolean abAuthFlag = true;
		returnedPaymentService.setAuditReturnedPaymentId(aoReturnedPayment, abAuthFlag);
	}
	
	@Test
	public void setAuditReturnedPaymentId2() throws ApplicationException
	{
		ReturnedPayment aoReturnedPayment = new ReturnedPayment();
		aoReturnedPayment.setReturnedPaymentId("123");
		aoReturnedPayment.setCreatedByUserId("1234");
		Boolean abAuthFlag = true;
		returnedPaymentService.setAuditReturnedPaymentId(aoReturnedPayment, false);
	}
	
	@Test(expected=ApplicationException.class)
	public void setAuditReturnedPaymentId3() throws ApplicationException
	{
		ReturnedPayment aoReturnedPayment = new ReturnedPayment();
		aoReturnedPayment.setReturnedPaymentId("12@!$#@#$3");
		aoReturnedPayment.setCreatedByUserId("1234");
		Boolean abAuthFlag = true;
		returnedPaymentService.setAuditReturnedPaymentId(null, true);
	}
	@Test
	public void setTaskReturnedPaymentId() throws ApplicationException
	{
		HashMap<String, Object> aoHmRequiredProps = new HashMap<String, Object>();
		ReturnedPayment aoReturnedPayment = new ReturnedPayment();
		aoReturnedPayment.setReturnedPaymentId("123");
		Boolean abAuthFlag = true;
		returnedPaymentService.setTaskReturnedPaymentId(aoHmRequiredProps, aoReturnedPayment, abAuthFlag);
	}
	@Test
	public void setTaskReturnedPaymentId2() throws ApplicationException
	{
		HashMap<String, Object> aoHmRequiredProps = new HashMap<String, Object>();
		ReturnedPayment aoReturnedPayment = new ReturnedPayment();
		aoReturnedPayment.setReturnedPaymentId("1223");
		Boolean abAuthFlag = true;
		returnedPaymentService.setTaskReturnedPaymentId(aoHmRequiredProps, aoReturnedPayment, false);
	}
	
	@Test
	public void getProgramNameTest1() throws ApplicationException
	{
		String asOrgType = null;
		String asOrgId = "HRA";
		
		returnedPaymentService.getProgramName(moSession, asOrgType, asOrgId);
	}
	
	@Test(expected=ApplicationException.class)
	public void getProgramNameTest2() throws ApplicationException
	{
		String asOrgType = null;
		String asOrgId = "HRA";
		
		returnedPaymentService.getProgramName(null, asOrgType, asOrgId);
	}
}


