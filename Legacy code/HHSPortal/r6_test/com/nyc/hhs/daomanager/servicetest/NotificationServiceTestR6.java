package com.nyc.hhs.daomanager.servicetest;

import static org.junit.Assert.*;

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
import org.mockito.stubbing.OngoingStubbing;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import weblogic.jdbc.wrapper.Array;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.constants.TransactionConstants;
import com.nyc.hhs.daomanager.service.ReturnedPaymentService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.model.BulkNotificationList;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.service.db.services.notification.NotificationService;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DAOUtil.class)
public class NotificationServiceTestR6
{
	NotificationService notificationService = new NotificationService();
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

	@Test
	public void testGetMaxGroupNotificationId1() throws ApplicationException
	{
		Boolean abNotifyFlag = true;
		notificationService.getMaxGroupNotificationId(moSession, abNotifyFlag);
	}

	@Test(expected = ApplicationException.class)
	public void testGetMaxGroupNotificationId2() throws ApplicationException
	{
		Boolean abNotifyFlag = true;
		notificationService.getMaxGroupNotificationId(null, abNotifyFlag);
	}

	@Test
	public void testGetMaxGroupNotificationId3() throws ApplicationException
	{
		Boolean abNotifyFlag = false;
		notificationService.getMaxGroupNotificationId(moSession, abNotifyFlag);
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetMaxGroupNotificationId4() throws ApplicationException
	{
		Boolean abNotifyFlag = true;
		notificationService.getMaxGroupNotificationId(null, abNotifyFlag);
	}
	@Test
	public void testInsertNotificationRequestedDetails1() throws Exception
	{
		String asUserId = "agency_21";
		String asMaxId = "1342056";
		String asRole = "Financial L2";
		Boolean abProcessStatus = true;
		Boolean abNotifyFlag = true;
		notificationService.insertNotificationRequestedDetails(moSession, asUserId, asMaxId, asRole, abProcessStatus,
				abNotifyFlag);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertNotificationRequestedDetails2() throws Exception
	{
		String asUserId = "agency_21";
		String asMaxId = "1342056";
		String asRole = "Financial L2";
		Boolean abProcessStatus = true;
		Boolean abNotifyFlag = true;
		notificationService.insertNotificationRequestedDetails(null, asUserId, asMaxId, asRole, abProcessStatus,
				abNotifyFlag);
	}
	
	@Test
	public void testInsertNotificationRequestedDetails3() throws Exception
	{
		String asUserId = "agency_21";
		String asMaxId = null;
		String asRole = "Financial L2";
		Boolean abProcessStatus = true;
		Boolean abNotifyFlag = true;
		notificationService.insertNotificationRequestedDetails(moSession, asUserId, asMaxId, asRole, abProcessStatus,
				abNotifyFlag);
	}

	@Test
	public void testInsertNotificationRequestedDetails4() throws Exception
	{
		String asUserId = "agency_21";
		String asMaxId = null;
		String asRole = "Financial L2";
		Boolean abProcessStatus = false;
		Boolean abNotifyFlag = true;
		notificationService.insertNotificationRequestedDetails(moSession, asUserId, asMaxId, asRole, abProcessStatus,
				abNotifyFlag);
	}

	@Test
	public void testInsertNotificationRequestedDetails5() throws Exception
	{
		String asUserId = "agency_21";
		String asMaxId = null;
		String asRole = "Financial L2";
		Boolean abProcessStatus = true;
		Boolean abNotifyFlag = false;
		notificationService.insertNotificationRequestedDetails(moSession, asUserId, asMaxId, asRole, abProcessStatus,
				abNotifyFlag);
	}

	@Test
	public void testInsertNotificationRequestedDetails6() throws Exception
	{
		String asUserId = "agency_21";
		String asMaxId = "999999999";
		String asRole = "Financial L2";
		Boolean abProcessStatus = true;
		Boolean abNotifyFlag = true;
		notificationService.insertNotificationRequestedDetails(moSession, asUserId, asMaxId, asRole, abProcessStatus,
				abNotifyFlag);
	}
	
	@Test
	public void testInsertNotificationRequestedDetails7() throws Exception
	{
		String asUserId = "agency_31";
		String asMaxId = "999999";
		String asRole = "Financial L2";
		Boolean abProcessStatus = true;
		Boolean abNotifyFlag = true;
		notificationService.insertNotificationRequestedDetails(moSession, asUserId, asMaxId, asRole, abProcessStatus,
				abNotifyFlag);
	}
	@Test
	public void testInsertNotificationRequestedDetails8() throws Exception
	{
		String asUserId = "agency_21";
		String asMaxId = "999900999";
		String asRole = "Financial L2";
		Boolean abProcessStatus = true;
		Boolean abNotifyFlag = true;
		notificationService.insertNotificationRequestedDetails(moSession, asUserId, asMaxId, asRole, abProcessStatus,
				abNotifyFlag);
	}
	
	@Test
	public void testInsertNotificationRequestedDetails9() throws Exception
	{
		String asUserId = "agency_21";
		String asMaxId = "999900999";
		String asRole = "Financial L2";
		Boolean abProcessStatus = true;
		Boolean abNotifyFlag = true;
		notificationService.insertNotificationRequestedDetails(moSession, asUserId, asMaxId, asRole, abProcessStatus,
				abNotifyFlag);
	}
	@Test
	public void testGetInputListForExportNotifications1() throws ApplicationException
	{
		notificationService.getInputListForExportNotifications(moSession);
	}

	@Test(expected = ApplicationException.class)
	public void testGetInputListForExportNotifications2() throws ApplicationException
	{
		notificationService.getInputListForExportNotifications(null);
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetInputListForExportNotifications3() throws ApplicationException
	{
		notificationService.getInputListForExportNotifications(null);
	}

	@Test
	public void testDownloadBulkExportFile1() throws Exception
	{
		Map<String, String> loStatusMap = new HashMap<String, String>();
		PowerMockito.mockStatic(DAOUtil.class);
		OngoingStubbing<Object> thenReturn = PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenReturn(loStatusMap);
		notificationService.downloadBulkExportFile(moSession, null, null, null);
	}

	@Test
	public void testDownloadBulkExportFile2() throws Exception
	{
		ReturnedPaymentService loPaymentService = new ReturnedPaymentService();
		List<BulkNotificationList> loBulkList = loPaymentService.fetchExportNotificationList(moSession, "2016",
				"agency_21", "103");
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap = notificationService.downloadBulkExportFile(moSession, loBulkList, "199", "ACS");
		assertEquals("191", loStatusMap.get(HHSR5Constants.DOWNLOAD_STATUS));
	}
	
	@Test
	public void testDownloadBulkExportFile3() throws Exception
	{
		List<BulkNotificationList> loBulkList=new ArrayList<BulkNotificationList>();
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap = notificationService.downloadBulkExportFile(moSession, loBulkList, "199", "ACS");
		assertEquals("193", loStatusMap.get(HHSR5Constants.DOWNLOAD_STATUS));
	}

	@Test(expected=ApplicationException.class)
	public void testDownloadBulkExportFile4() throws Exception
	{
		ReturnedPaymentService loPaymentService = new ReturnedPaymentService();
		List<BulkNotificationList> loBulkList = loPaymentService.fetchExportNotificationList(moSession, "2016",
				"agency_21", "103");
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap = notificationService.downloadBulkExportFile(null, loBulkList, "199", "ACS");
		assertEquals("191", loStatusMap.get(HHSR5Constants.DOWNLOAD_STATUS));
	}
	
	@Test
	public void testDownloadBulkExportFile5() throws Exception
	{
		ReturnedPaymentService loPaymentService = new ReturnedPaymentService();
		List<BulkNotificationList> loBulkList = loPaymentService.fetchExportNotificationList(moSession, "2016",
				"agency_21", "193");
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap = notificationService.downloadBulkExportFile(moSession, loBulkList, "199", "ACS");
		assertEquals("193", loStatusMap.get(HHSR5Constants.DOWNLOAD_STATUS));
	}
	
	@Test
	public void testDownloadBulkExportFile6() throws Exception
	{
		ReturnedPaymentService loPaymentService = new ReturnedPaymentService();
		List<BulkNotificationList> loBulkList = loPaymentService.fetchExportNotificationList(moSession, "2016",
				"agency_21", "103");
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap = notificationService.downloadBulkExportFile(moSession, loBulkList, "200", "ACS");
		assertEquals("193", loStatusMap.get(HHSR5Constants.DOWNLOAD_STATUS));
	}
	
	@Test
	public void testDownloadBulkExportFile7() throws Exception
	{
		ReturnedPaymentService loPaymentService = new ReturnedPaymentService();
		List<BulkNotificationList> loBulkList = loPaymentService.fetchExportNotificationList(moSession, "2016",
				"agency_21", "103");
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap = notificationService.downloadBulkExportFile(moSession, loBulkList, "192", "DOC");
		assertEquals("191", loStatusMap.get(HHSR5Constants.DOWNLOAD_STATUS));
	}
	
	@Test
	public void testDownloadBulkExportFile8() throws Exception
	{
		ReturnedPaymentService loPaymentService = new ReturnedPaymentService();
		List<BulkNotificationList> loBulkList = loPaymentService.fetchExportNotificationList(moSession, "2016",
				"agency_21", "153");
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap = notificationService.downloadBulkExportFile(moSession, loBulkList, "199", "ACS");
		assertEquals("190", loStatusMap.get(HHSR5Constants.DOWNLOAD_STATUS));
	}
	
	@Test
	public void testDownloadBulkExportFile9() throws Exception
	{
		ReturnedPaymentService loPaymentService = new ReturnedPaymentService();
		List<BulkNotificationList> loBulkList = loPaymentService.fetchExportNotificationList(moSession, "2017",
				"agency_21", "103");
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap = notificationService.downloadBulkExportFile(moSession, loBulkList, "199", "ACS");
		assertEquals("191", loStatusMap.get(HHSR5Constants.DOWNLOAD_STATUS));
	}
	
	@Test
	public void testDownloadBulkExportFile11() throws Exception
	{
		ReturnedPaymentService loPaymentService = new ReturnedPaymentService();
		List<BulkNotificationList> loBulkList = loPaymentService.fetchExportNotificationList(moSession, "2017",
				"agency_21", "103");
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap = notificationService.downloadBulkExportFile(moSession, loBulkList, "199", "ACS");
		assertEquals("191", loStatusMap.get(HHSR5Constants.DOWNLOAD_STATUS));
	}
	
	@Test(expected=ApplicationException.class)
	public void testDownloadBulkExportFile12() throws Exception
	{
		ReturnedPaymentService loPaymentService = new ReturnedPaymentService();
		List<BulkNotificationList> loBulkList = loPaymentService.fetchExportNotificationList(moSession, "2017",
				"agency_31", "103");
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap = notificationService.downloadBulkExportFile(moSession, loBulkList, "199", "ACS");
		assertEquals("190", loStatusMap.get(HHSR5Constants.DOWNLOAD_STATUS));
	}
	
	@Test(expected=ApplicationException.class)
	public void testDownloadBulkExportFile13() throws Exception
	{
		ReturnedPaymentService loPaymentService = new ReturnedPaymentService();
		List<BulkNotificationList> loBulkList = loPaymentService.fetchExportNotificationList(moSession, "2017",
				"agency_31", "103");
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap = notificationService.downloadBulkExportFile(moSession, loBulkList, "199", null);
	}
	
	@Test
	public void testDownloadBulkExportFile14() throws Exception
	{
		ReturnedPaymentService loPaymentService = new ReturnedPaymentService();
		List<BulkNotificationList> loBulkList = loPaymentService.fetchExportNotificationList(moSession, "2016",
				"agency_21", "103");
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap = notificationService.downloadBulkExportFile(moSession, loBulkList, "199", "ACS");
		assertEquals("191", loStatusMap.get(HHSR5Constants.DOWNLOAD_STATUS));
	}
	
	@Test(expected=ApplicationException.class)
	public void testDownloadBulkExportFile15() throws Exception
	{
		ReturnedPaymentService loPaymentService = new ReturnedPaymentService();
		List<BulkNotificationList> loBulkList = loPaymentService.fetchExportNotificationList(moSession, "2016",
				"agency_21", "103");
		Map<String, String> loStatusMap = new HashMap<String, String>();
		loStatusMap = notificationService.downloadBulkExportFile(moSession, loBulkList, "220", "DOC");
		assertEquals(null, loStatusMap.get(HHSR5Constants.DOWNLOAD_STATUS));
	}
	@Test
	public void testProcessBulkNotification1() throws ApplicationException
	{
		ReturnedPaymentService loPaymentService=new ReturnedPaymentService();
		List<BudgetList> loInputBudgetList=null;
		loInputBudgetList=loPaymentService.fetchBudgetList(moSession,"2016","103",null,true);
		BudgetList loBudgetListBean = new BudgetList();
		loBudgetListBean.setBudgetId("10731");
		loBudgetListBean.setOrgId("org_509");
		loBudgetListBean.setCtId(null);
		loBudgetListBean.setUnRecoupAmount("1000");
		loBudgetListBean.setContractTitle("dynamic_FTE");
		loBudgetListBean.setProviderName("sumit,11");
		loBudgetListBean.setFiscalYear("2017");

		List<BudgetList> aoNotificationBeanList = new ArrayList<BudgetList>();
		aoNotificationBeanList.add(loBudgetListBean);
		String asUserId = "agency_21";
		String asProgramName = "Food Protection Course";
		String asFiscalYear = "2017";
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT322");
		loNotificationAlertList.add("AL322");
		aoHashMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHashMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
		aoHashMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
		aoHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoHashMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHashMap.put("DESCRIPTION", "N/a");
		aoHashMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		String asNotificationId = "NT322";
		String asUserOrg = "DOC";
		Boolean abNotifyFlag = true;
		assertTrue(notificationService.processBulkNotification(moSession, loInputBudgetList, asUserId,
				asProgramName, asFiscalYear, aoHashMap, asNotificationId, asUserOrg, abNotifyFlag));

	}
	
	@Test
	public void testProcessBulkNotification2() throws ApplicationException
	{
		ReturnedPaymentService loPaymentService=new ReturnedPaymentService();
		List<BudgetList> loInputBudgetList=null;
		loInputBudgetList=loPaymentService.fetchBudgetList(moSession,"2016","103",null,true);
		BudgetList loBudgetListBean = new BudgetList();
		loBudgetListBean.setBudgetId("10731");
		loBudgetListBean.setOrgId("org_509");
		loBudgetListBean.setCtId(null);
		loBudgetListBean.setUnRecoupAmount("1000");
		loBudgetListBean.setContractTitle("dynamic_FTE");
		loBudgetListBean.setProviderName("sumit,11");
		loBudgetListBean.setFiscalYear("2017");

		List<BudgetList> aoNotificationBeanList = new ArrayList<BudgetList>();
		aoNotificationBeanList.add(loBudgetListBean);
		String asUserId = "agency_21";
		String asProgramName = "Food Protection Course";
		String asFiscalYear = "2017";
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT323");
		loNotificationAlertList.add("AL323");
		aoHashMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHashMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
		aoHashMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
		aoHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoHashMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHashMap.put("DESCRIPTION", "N/a");
		aoHashMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		String asNotificationId = "NT323";
		String asUserOrg = "DOC";
		Boolean abNotifyFlag = true;
		assertTrue(notificationService.processBulkNotification(moSession, loInputBudgetList, asUserId,
				asProgramName, asFiscalYear, aoHashMap, asNotificationId, asUserOrg, abNotifyFlag));

	}
	
	@Test
	public void testProcessBulkNotification3() throws ApplicationException
	{
		ReturnedPaymentService loPaymentService=new ReturnedPaymentService();
		List<BudgetList> loInputBudgetList=null;
		loInputBudgetList=loPaymentService.fetchBudgetList(moSession,"2016","103",null,true);
		BudgetList loBudgetListBean = new BudgetList();
		loBudgetListBean.setBudgetId("10731");
		loBudgetListBean.setOrgId("org_509");
		loBudgetListBean.setCtId(null);
		loBudgetListBean.setUnRecoupAmount("1000");
		loBudgetListBean.setContractTitle("dynamic_FTE");
		loBudgetListBean.setProviderName("sumit,11");
		loBudgetListBean.setFiscalYear("2017");

		List<BudgetList> aoNotificationBeanList = new ArrayList<BudgetList>();
		aoNotificationBeanList.add(loBudgetListBean);
		String asUserId = "agency_21";
		String asProgramName = "Food Protection Course";
		String asFiscalYear = "2017";
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT321");
		loNotificationAlertList.add("AL321");
		aoHashMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHashMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
		aoHashMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
		aoHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoHashMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHashMap.put("DESCRIPTION", "N/a");
		aoHashMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		String asNotificationId = "NT321";
		String asUserOrg = "DOC";
		Boolean abNotifyFlag = true;
		assertTrue(notificationService.processBulkNotification(moSession, loInputBudgetList, asUserId,
				asProgramName, asFiscalYear, aoHashMap, asNotificationId, asUserOrg, abNotifyFlag));

	}
	
	@Test
	public void testProcessBulkNotification4() throws ApplicationException
	{
		ReturnedPaymentService loPaymentService=new ReturnedPaymentService();
		List<BudgetList> loInputBudgetList=null;
		loInputBudgetList=loPaymentService.fetchBudgetList(moSession,"2016","103",null,true);
		BudgetList loBudgetListBean = new BudgetList();
		loBudgetListBean.setBudgetId("10731");
		loBudgetListBean.setOrgId("org_509");
		loBudgetListBean.setCtId(null);
		loBudgetListBean.setUnRecoupAmount("1000");
		loBudgetListBean.setContractTitle("dynamic_FTE");
		loBudgetListBean.setProviderName("sumit,11");
		loBudgetListBean.setFiscalYear("2022");

		List<BudgetList> aoNotificationBeanList = new ArrayList<BudgetList>();
		aoNotificationBeanList.add(loBudgetListBean);
		String asUserId = "agency_21";
		String asProgramName = "Food Protection Course";
		String asFiscalYear = "2017";
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT321");
		loNotificationAlertList.add("AL321");
		aoHashMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHashMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
		aoHashMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
		aoHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoHashMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHashMap.put("DESCRIPTION", "N/a");
		aoHashMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		String asNotificationId = "NT321";
		String asUserOrg = "DOC";
		Boolean abNotifyFlag = true;
		assertTrue(notificationService.processBulkNotification(moSession, loInputBudgetList, asUserId,
				asProgramName, asFiscalYear, aoHashMap, asNotificationId, asUserOrg, abNotifyFlag));

	}
	
	@Test(expected=ApplicationException.class)
	public void testProcessBulkNotification5() throws ApplicationException
	{
		ReturnedPaymentService loPaymentService=new ReturnedPaymentService();
		List<BudgetList> loInputBudgetList=null;
		loInputBudgetList=loPaymentService.fetchBudgetList(moSession,"2016","103",null,true);
		BudgetList loBudgetListBean = new BudgetList();
		loBudgetListBean.setBudgetId("10731");
		loBudgetListBean.setOrgId("org_509");
		loBudgetListBean.setCtId(null);
		loBudgetListBean.setUnRecoupAmount("1000");
		loBudgetListBean.setContractTitle("dynamic_FTE");
		loBudgetListBean.setProviderName("sumit,11");
		loBudgetListBean.setFiscalYear("2016");

		List<BudgetList> aoNotificationBeanList = new ArrayList<BudgetList>();
		aoNotificationBeanList.add(loBudgetListBean);
		String asUserId = "agency_21";
		String asProgramName = "Food Protection Course";
		String asFiscalYear = "2016";
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT321");
		loNotificationAlertList.add("AL321");
		aoHashMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHashMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
		aoHashMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
		aoHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoHashMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHashMap.put("DESCRIPTION", "N/a");
		aoHashMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		String asNotificationId = "NT321";
		String asUserOrg = "DOC";
		Boolean abNotifyFlag = true;
		notificationService.processBulkNotification(null, loInputBudgetList, asUserId,
				asProgramName, asFiscalYear, aoHashMap, asNotificationId, asUserOrg, abNotifyFlag);

	}
	
	@Test(expected=ApplicationException.class)
	public void testProcessBulkNotification7() throws ApplicationException
	{
		ReturnedPaymentService loPaymentService=new ReturnedPaymentService();
		List<BudgetList> loInputBudgetList=null;
		loInputBudgetList=loPaymentService.fetchBudgetList(moSession,"2016","103",null,true);
		BudgetList loBudgetListBean = new BudgetList();
		List<BudgetList> aoNotificationBeanList = new ArrayList<BudgetList>();
		aoNotificationBeanList.add(loBudgetListBean);
		String asUserId = "agency_21";
		String asProgramName = "Food Protection Course";
		String asFiscalYear = "2016";
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT321");
		loNotificationAlertList.add("AL321");
		aoHashMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHashMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
		aoHashMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
		aoHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoHashMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHashMap.put("DESCRIPTION", "N/a");
		aoHashMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		String asNotificationId = "NT321";
		String asUserOrg = "DOC";
		Boolean abNotifyFlag = true;
		notificationService.processBulkNotification(null, null, asUserId,
				asProgramName, asFiscalYear, aoHashMap, asNotificationId, asUserOrg, abNotifyFlag);

	}
	@Test
	public void testProcessBulkNotification6() throws ApplicationException
	{
		ReturnedPaymentService loPaymentService=new ReturnedPaymentService();
		List<BudgetList> loInputBudgetList=null;
		loInputBudgetList=loPaymentService.fetchBudgetList(moSession,"2016","103",null,true);
		BudgetList loBudgetListBean = new BudgetList();
		loBudgetListBean.setBudgetId("10731");
		loBudgetListBean.setOrgId("org_509");
		loBudgetListBean.setCtId(null);
		loBudgetListBean.setUnRecoupAmount("1000");
		loBudgetListBean.setContractTitle("dynamic_FTE");
		loBudgetListBean.setProviderName("sumit,11");
		loBudgetListBean.setFiscalYear("2017");

		List<BudgetList> aoNotificationBeanList = new ArrayList<BudgetList>();
		aoNotificationBeanList.add(loBudgetListBean);
		String asUserId = "agency_21";
		String asProgramName = "Food Protection Course";
		String asFiscalYear = "2017";
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT322");
		loNotificationAlertList.add("AL322");
		aoHashMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHashMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
		aoHashMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
		aoHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoHashMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHashMap.put("DESCRIPTION", "N/a");
		aoHashMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		String asNotificationId = "NT322";
		String asUserOrg = "DOC";
		Boolean abNotifyFlag = false;
		assertFalse(notificationService.processBulkNotification(moSession, loInputBudgetList, asUserId,
				asProgramName, asFiscalYear, aoHashMap, asNotificationId, asUserOrg, abNotifyFlag));

	}
	
	@Test
	public void testProcessBulkNotification8() throws ApplicationException
	{
		ReturnedPaymentService loPaymentService=new ReturnedPaymentService();
		List<BudgetList> loInputBudgetList=null;
		loInputBudgetList=loPaymentService.fetchBudgetList(moSession,"2016","103",null,true);
		BudgetList loBudgetListBean = new BudgetList();
		loBudgetListBean.setBudgetId("10731");
		loBudgetListBean.setOrgId("org_509");
		loBudgetListBean.setCtId(null);
		loBudgetListBean.setUnRecoupAmount("1000");
		loBudgetListBean.setContractTitle("dynamic_FTE");
		loBudgetListBean.setProviderName("sumit,11");
		loBudgetListBean.setFiscalYear("2017");

		List<BudgetList> aoNotificationBeanList = new ArrayList<BudgetList>();
		aoNotificationBeanList.add(loBudgetListBean);
		String asUserId = "agency_21";
		String asProgramName = "Food Protection Course";
		String asFiscalYear = "2017";
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT323");
		loNotificationAlertList.add("AL323");
		aoHashMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHashMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
		aoHashMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
		aoHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoHashMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHashMap.put("DESCRIPTION", "N/a");
		aoHashMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		String asNotificationId = "NT323";
		String asUserOrg = "DOC";
		Boolean abNotifyFlag = false;
		assertTrue(notificationService.processBulkNotification(moSession, null, asUserId,
				asProgramName, asFiscalYear, aoHashMap, asNotificationId, asUserOrg, abNotifyFlag));

	}

	@Test(expected=ApplicationException.class)
	public void testProcessBulkNotification9() throws ApplicationException
	{
		ReturnedPaymentService loPaymentService=new ReturnedPaymentService();
		List<BudgetList> loInputBudgetList=null;
		loInputBudgetList=loPaymentService.fetchBudgetList(moSession,"2016","103",null,true);
		BudgetList loBudgetListBean = new BudgetList();
		loBudgetListBean.setBudgetId("10731");
		loBudgetListBean.setOrgId("org_509");
		loBudgetListBean.setCtId(null);
		loBudgetListBean.setUnRecoupAmount("1000");
		loBudgetListBean.setContractTitle("dynamic_FTE");
		loBudgetListBean.setProviderName("sumit,11");
		loBudgetListBean.setFiscalYear("2016");

		List<BudgetList> aoNotificationBeanList = new ArrayList<BudgetList>();
		aoNotificationBeanList.add(loBudgetListBean);
		String asUserId = "agency_21";
		String asProgramName = "Food Protection Course";
		String asFiscalYear = "2016";
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT322");
		loNotificationAlertList.add("AL322");
		aoHashMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHashMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
		aoHashMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
		aoHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoHashMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHashMap.put("DESCRIPTION", "N/a");
		aoHashMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		String asNotificationId = "NT322";
		String asUserOrg = "DOC";
		Boolean abNotifyFlag = true;
		notificationService.processBulkNotification(moSession, loInputBudgetList, asUserId,
				asProgramName, asFiscalYear, null, asNotificationId, asUserOrg, abNotifyFlag);

	}
	
	@Test
	public void testProcessBulkNotification10() throws ApplicationException
	{
		ReturnedPaymentService loPaymentService=new ReturnedPaymentService();
		List<BudgetList> loInputBudgetList=null;
		loInputBudgetList=loPaymentService.fetchBudgetList(moSession,"2016","103",null,true);
		BudgetList loBudgetListBean = new BudgetList();
		loBudgetListBean.setBudgetId("10731");
		loBudgetListBean.setOrgId("org_509");
		loBudgetListBean.setCtId("Not Registered");
		loBudgetListBean.setUnRecoupAmount("1000");
		loBudgetListBean.setContractTitle("dynamic_FTE");
		loBudgetListBean.setProviderName("sumit,11");
		loBudgetListBean.setFiscalYear("2016");

		List<BudgetList> aoNotificationBeanList = new ArrayList<BudgetList>();
		aoNotificationBeanList.add(loBudgetListBean);
		String asUserId = "agency_21";
		String asProgramName = "Food Protection Course";
		String asFiscalYear = "2016";
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT322");
		loNotificationAlertList.add("AL322");
		aoHashMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHashMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
		aoHashMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
		aoHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoHashMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHashMap.put("DESCRIPTION", "N/a");
		aoHashMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		String asNotificationId = "NT322";
		String asUserOrg = "DOC";
		Boolean abNotifyFlag = true;
		notificationService.processBulkNotification(moSession, loInputBudgetList, asUserId,
				asProgramName, asFiscalYear, aoHashMap, asNotificationId, asUserOrg, abNotifyFlag);

	}
	
	@Test(expected=ApplicationException.class)
	public void testProcessBulkNotification11() throws ApplicationException
	{
		ReturnedPaymentService loPaymentService=new ReturnedPaymentService();
		List<BudgetList> loInputBudgetList=null;
		loInputBudgetList=loPaymentService.fetchBudgetList(moSession,"2016","103",null,true);
		BudgetList loBudgetListBean = new BudgetList();
		loBudgetListBean.setBudgetId("10731");
		loBudgetListBean.setOrgId("org_509");
		loBudgetListBean.setCtId("Not Registered");
		loBudgetListBean.setUnRecoupAmount("1000");
		loBudgetListBean.setContractTitle("dynamic_FTE");
		loBudgetListBean.setProviderName("sumit,11");
		loBudgetListBean.setFiscalYear("2016");

		List<BudgetList> aoNotificationBeanList = new ArrayList<BudgetList>();
		aoNotificationBeanList.add(loBudgetListBean);
		String asUserId = "agency_21";
		String asProgramName = "Food Protection Course";
		String asFiscalYear = "2016";
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT321");
		loNotificationAlertList.add("AL321");
		aoHashMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHashMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
		aoHashMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
		aoHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoHashMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHashMap.put("DESCRIPTION", "N/a");
		aoHashMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		String asNotificationId = "NT322";
		String asUserOrg = "DOC";
		Boolean abNotifyFlag = true;
		notificationService.processBulkNotification(null, loInputBudgetList, asUserId,
				asProgramName, asFiscalYear, aoHashMap, asNotificationId, asUserOrg, abNotifyFlag);

	}

	@Test(expected=ApplicationException.class)
	public void testProcessBulkNotification12() throws ApplicationException
	{
		ReturnedPaymentService loPaymentService=new ReturnedPaymentService();
		List<BudgetList> loInputBudgetList=null;
		loInputBudgetList=loPaymentService.fetchBudgetList(moSession,"2016","103",null,true);
		BudgetList loBudgetListBean = new BudgetList();
		loBudgetListBean.setBudgetId("10731");
		loBudgetListBean.setOrgId("org_509");
		loBudgetListBean.setCtId("Not Registered");
		loBudgetListBean.setUnRecoupAmount("1000");
		loBudgetListBean.setContractTitle("dynamic_FTE");
		loBudgetListBean.setProviderName("sumit,11");
		loBudgetListBean.setFiscalYear("2016");

		List<BudgetList> aoNotificationBeanList = new ArrayList<BudgetList>();
		aoNotificationBeanList.add(loBudgetListBean);
		String asUserId = "agency_21";
		String asProgramName = "Food Protection Course";
		String asFiscalYear = "2016";
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT321");
		loNotificationAlertList.add("AL321");
		aoHashMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHashMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
		aoHashMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
		aoHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoHashMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHashMap.put("DESCRIPTION", "N/a");
		aoHashMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		String asNotificationId = "NT3222";
		String asUserOrg = "DOC";
		Boolean abNotifyFlag = true;
		notificationService.processBulkNotification(moSession, loInputBudgetList, asUserId,
				asProgramName, asFiscalYear, aoHashMap, asNotificationId, asUserOrg, abNotifyFlag);

	}
	
	@Test(expected=ApplicationException.class)
	public void testProcessBulkNotification13() throws ApplicationException
	{
		ReturnedPaymentService loPaymentService=new ReturnedPaymentService();
		List<BudgetList> loInputBudgetList=null;
		loInputBudgetList=loPaymentService.fetchBudgetList(moSession,"2016","103",null,true);
		BudgetList loBudgetListBean = new BudgetList();
		loBudgetListBean.setBudgetId("10731");
		loBudgetListBean.setOrgId("org_509");
		loBudgetListBean.setCtId("Not Registered");
		loBudgetListBean.setUnRecoupAmount("1000");
		loBudgetListBean.setContractTitle("dynamic_FTE");
		loBudgetListBean.setProviderName("sumit,11");
		loBudgetListBean.setFiscalYear("2016");

		List<BudgetList> aoNotificationBeanList = new ArrayList<BudgetList>();
		aoNotificationBeanList.add(loBudgetListBean);
		String asUserId = "agency_21";
		String asProgramName = "Food Protection Course";
		String asFiscalYear = "2016";
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT32111");
		loNotificationAlertList.add("AL3221");
		aoHashMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHashMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
		aoHashMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
		aoHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoHashMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHashMap.put("DESCRIPTION", "N/a");
		aoHashMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		String asNotificationId = "AL3222";
		String asUserOrg = "DOC";
		Boolean abNotifyFlag = true;
		notificationService.processBulkNotification(moSession, loInputBudgetList, asUserId,
				asProgramName, asFiscalYear, aoHashMap, asNotificationId, asUserOrg, abNotifyFlag);

	}
	@Test
	public void testProcessBulkNotification14() throws ApplicationException
	{
		ReturnedPaymentService loPaymentService=new ReturnedPaymentService();
		List<BudgetList> loInputBudgetList=null;
		loInputBudgetList=loPaymentService.fetchBudgetList(moSession,"2016","103",null,true);
		BudgetList loBudgetListBean = new BudgetList();
		loBudgetListBean.setBudgetId("10731");
		loBudgetListBean.setOrgId("org_509");
		loBudgetListBean.setCtId(null);
		loBudgetListBean.setUnRecoupAmount("1000");
		loBudgetListBean.setContractTitle("dynamic_FTE");
		loBudgetListBean.setProviderName("sumit,11");
		loBudgetListBean.setFiscalYear("2017");

		List<BudgetList> aoNotificationBeanList = new ArrayList<BudgetList>();
		aoNotificationBeanList.add(loBudgetListBean);
		String asUserId = "agency_21";
		String asProgramName = "Food Protection Course";
		String asFiscalYear = "2017";
		HashMap<String, Object> aoHashMap = new HashMap<String, Object>();
		HashMap<String, String> loRequestMap = new HashMap<String, String>();
		List<String> loNotificationAlertList = new ArrayList<String>();
		loNotificationAlertList.add("NT322");
		loNotificationAlertList.add("AL322");
		aoHashMap.put(HHSConstants.NOTIFICATION_ALERT_ID, loNotificationAlertList);
		aoHashMap.put(ApplicationConstants.ENTITY_ID, HHSR5Constants.EMPTY_STRING);
		aoHashMap.put(ApplicationConstants.ENTITY_TYPE, HHSConstants.BUDGET);
		aoHashMap.put(HHSConstants.CREATED_BY_USER_ID, asUserId);
		aoHashMap.put(HHSConstants.MODIFIED_BY, asUserId);
		aoHashMap.put("DESCRIPTION", "N/a");
		aoHashMap.put(TransactionConstants.REQUEST_MAP_PARAMETER_NAME, loRequestMap);
		String asNotificationId = "NT322";
		String asUserOrg = "DOC";
		Boolean abNotifyFlag = true;
		assertTrue(notificationService.processBulkNotification(moSession, loInputBudgetList, asUserId,
				asProgramName, asFiscalYear, aoHashMap, asNotificationId, asUserOrg, abNotifyFlag));

	}
	
}
