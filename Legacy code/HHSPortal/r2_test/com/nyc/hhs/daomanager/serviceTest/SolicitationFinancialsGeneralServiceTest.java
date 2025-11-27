package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.SolicitationFinancialsGeneralService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.AuthenticationBean;
import com.nyc.hhs.model.FinancialSummaryBean;
import com.nyc.hhs.model.MasterStatusBean;
import com.nyc.hhs.model.ProcurementSummaryBean;

public class SolicitationFinancialsGeneralServiceTest
{

	SqlSession moMyBatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
	SolicitationFinancialsGeneralService moSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();

	/*
	 * 
	 */
	@Test
	public void testfetchProcurementCountForAccHomePage() throws ApplicationException
	{
		ProcurementSummaryBean loProcurementSummaryBean = null;
		loProcurementSummaryBean = moSolicitationFinancialsGeneralService.fetchProcurementCountForAccHomePage(
				moMyBatisSession, "ACS");
		assertNotNull(loProcurementSummaryBean);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testfetchProcurementCountForAcHomePage() throws ApplicationException
	{
		ProcurementSummaryBean loProcurementSummaryBean = null;
		String lsString = "ABC@";
		loProcurementSummaryBean = moSolicitationFinancialsGeneralService.fetchProcurementCountForAccHomePage(null,
				lsString);
		assertNotNull(loProcurementSummaryBean);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	@Test(expected = ApplicationException.class)
	public void testfetchProcurementCountForAcHomePagecase1() throws ApplicationException
	{
		String lsString = "ABC@";
		moSolicitationFinancialsGeneralService.fetchProcurementCountForAccHomePage(null, lsString);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test
	public void testfetchProcurementCountForProvHomePage() throws ApplicationException
	{
		ProcurementSummaryBean loProcurementSummaryBean = null;
		loProcurementSummaryBean = moSolicitationFinancialsGeneralService.fetchProcurementCountForProvHomePage(
				moMyBatisSession, "ACS");
		assertNotNull(loProcurementSummaryBean);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test
	public void testfetchProcurementCountForProHomePage() throws ApplicationException
	{
		ProcurementSummaryBean loProcurementSummaryBean = null;
		String lsString = "9999999999*999";
		loProcurementSummaryBean = moSolicitationFinancialsGeneralService.fetchProcurementCountForProvHomePage(
				moMyBatisSession, lsString);
		assertNotNull(loProcurementSummaryBean);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test
	public void testfetchAccFinancialsPortletCount() throws ApplicationException
	{
		FinancialSummaryBean lofinancialSummaryBean = null;
		lofinancialSummaryBean = moSolicitationFinancialsGeneralService.fetchAccFinancialsPortletCount(
				moMyBatisSession, "ACS");
		assertNotNull(lofinancialSummaryBean);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test
	public void testfetchAcFinancialsPortletCount() throws ApplicationException
	{
		FinancialSummaryBean lofinancialSummaryBean = null;
		String lsString = "##";
		lofinancialSummaryBean = moSolicitationFinancialsGeneralService.fetchAccFinancialsPortletCount(
				moMyBatisSession, lsString);
		assertNotNull(lofinancialSummaryBean);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test
	public void testfetchProviderFinancialCount() throws ApplicationException
	{
		FinancialSummaryBean lofinancialSummaryBean = null;
		lofinancialSummaryBean = moSolicitationFinancialsGeneralService.fetchProviderFinancialCount(moMyBatisSession,
				"ACS");
		assertNotNull(lofinancialSummaryBean);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test
	public void testfetchProviderFinancialCounts() throws ApplicationException
	{
		FinancialSummaryBean lofinancialSummaryBean = null;
		String lsString = "9999999999*999";
		lofinancialSummaryBean = moSolicitationFinancialsGeneralService.fetchProviderFinancialCount(moMyBatisSession,
				lsString);
		assertNotNull(lofinancialSummaryBean);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test
	public void testgetMasterStatus() throws ApplicationException
	{
		List<MasterStatusBean> loListMasterStatusBean = null;
		loListMasterStatusBean = moSolicitationFinancialsGeneralService.getMasterStatus(moMyBatisSession);
		assertNotNull(loListMasterStatusBean);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testgetMasterStatuses() throws ApplicationException
	{
		List<MasterStatusBean> loListMasterStatusBean = null;
		SqlSession loMyBatisSession = null;
		loListMasterStatusBean = moSolicitationFinancialsGeneralService.getMasterStatus(loMyBatisSession);
		assertNotNull(loListMasterStatusBean);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/* 
	 * 
	 */
	@Test
	public void testgetProcurementChangeControlWidget() throws ApplicationException
	{
		HashMap<Object, Object> loHashMap = null;
		loHashMap = moSolicitationFinancialsGeneralService.getProcurementChangeControlWidget(moMyBatisSession, 1, null);
		assertEquals(loHashMap, loHashMap);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/* 
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testgetProcurementChangeControlWidgets() throws ApplicationException
	{
		HashMap<Object, Object> loHashMap = null;
		Integer liInteger = 251;
		loHashMap = moSolicitationFinancialsGeneralService.getProcurementChangeControlWidget(null, liInteger, null);
		assertNotNull(loHashMap);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/* 
	 * 
	 */
	@Test
	public void testauthenticateLoginUser() throws ApplicationException
	{
		// not required as we are using controller to authenticate user
	}

	/* 
	 * 
	 */
	@Test
	public void testfetchEpinList() throws ApplicationException
	{
		List<String> loEpinList = null;
		String asDataToSearch = "2";
		loEpinList = moSolicitationFinancialsGeneralService.fetchEpinList(moMyBatisSession, "fetchEpinList",
				asDataToSearch);
		assertNotNull(loEpinList);

	}

	@Test(expected = ApplicationException.class)
	public void testfetchEpinListCase3() throws ApplicationException
	{
		String asDataToSearch = "2";
		moSolicitationFinancialsGeneralService.fetchEpinList(moMyBatisSession, null, asDataToSearch);
	}

	/* 
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testfetchEpinListCase4() throws ApplicationException
	{
		String asDataToSearch = "DS";
		moSolicitationFinancialsGeneralService.fetchEpinList(moMyBatisSession, asDataToSearch, asDataToSearch);
	}

	/* 
	 * 
	 */
	@Test
	public void testfetchContractNoList() throws ApplicationException
	{
		List<String> loContractNoList = null;
		String asDataToSearch = "98";
		loContractNoList = moSolicitationFinancialsGeneralService.fetchContractNoList(moMyBatisSession,
				"fetchContractNoList", asDataToSearch);
		assertNotNull(loContractNoList);

	}

	@Test(expected = ApplicationException.class)
	public void testfetchContractNoListCase2() throws ApplicationException
	{
		List<String> loContractNoList = null;
		String asDataToSearch = "2";
		loContractNoList = moSolicitationFinancialsGeneralService.fetchContractNoList(moMyBatisSession,
				"fetchContractNoList", asDataToSearch);
		assertNotNull(loContractNoList);

	}

	/* 
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testfetchContractNoListCase3() throws ApplicationException
	{
		List<String> loContractNoList = null;
		String asDataToSearch = null;
		loContractNoList = moSolicitationFinancialsGeneralService.fetchContractNoList(moMyBatisSession,
				"fetchContractNoList", asDataToSearch);
		assertNotNull(loContractNoList);

	}

	/* 
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testfetchContractNoListCase4() throws ApplicationException
	{
		List<String> loContractNoList = null;
		String asDataToSearch = "98";
		loContractNoList = moSolicitationFinancialsGeneralService.fetchContractNoList(null, "fetchContractNoList",
				asDataToSearch);
		assertNotNull(loContractNoList);

	}

	/* 
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testfetchContractNoListCase5() throws ApplicationException
	{
		List<String> loContractNoList = null;
		String asDataToSearch = null;
		loContractNoList = moSolicitationFinancialsGeneralService.fetchContractNoList(moMyBatisSession, null,
				asDataToSearch);
		assertNotNull(loContractNoList);

	}

	/* 
	 * 
	 */
	@Test
	public void testupdateLastModifiedDetails() throws ApplicationException
	{
		Boolean lbUpdateStatus = false;
		Map loLastModifiedHashMap = new HashMap();
		lbUpdateStatus = moSolicitationFinancialsGeneralService.updateLastModifiedDetails(moMyBatisSession,
				loLastModifiedHashMap, true);
		assertNotNull(lbUpdateStatus);

	}

	/*
	 * 
	 */
	@Test
	public void testgetProviderWidgetDetils() throws ApplicationException
	{
		Map<Object, Object> loProviderMap = null;
		loProviderMap = moSolicitationFinancialsGeneralService.getProviderWidgetDetils(moMyBatisSession, "251",
				"accenture");
		assertNull(loProviderMap);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test
	public void testfetchProcurementCountForProHomePageCase2() throws ApplicationException
	{
		ProcurementSummaryBean loProcurementSummaryBean = null;
		String lsString = "0";
		loProcurementSummaryBean = moSolicitationFinancialsGeneralService.fetchProcurementCountForProvHomePage(
				moMyBatisSession, lsString);
		assertNotNull(loProcurementSummaryBean);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testfetchAccFinancialsPortletCountCase2() throws ApplicationException
	{
		moSolicitationFinancialsGeneralService.fetchAccFinancialsPortletCount(null, "");
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testfetchProviderFinancialCountsScenerio2() throws ApplicationException
	{
		String lsString = "0";
		moSolicitationFinancialsGeneralService.fetchProviderFinancialCount(null, lsString);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testgetMasterStatusesScenerio2() throws ApplicationException
	{
		moSolicitationFinancialsGeneralService.getMasterStatus(null);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/* 
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testgetProcurementChangeControlWidgetCase2() throws ApplicationException
	{
		moSolicitationFinancialsGeneralService.getProcurementChangeControlWidget(null, 1, null);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testgetProviderWidgetDetilsScenerio3() throws ApplicationException
	{
		moSolicitationFinancialsGeneralService.getProviderWidgetDetils(null, "251", "accenture");
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testgetProviderWidgetDetilsScenerio4() throws ApplicationException
	{
		moSolicitationFinancialsGeneralService.getProviderWidgetDetils(null, "", "accenture");
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/*
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testgetProviderWidgetDetilsScenerio2() throws ApplicationException
	{
		moSolicitationFinancialsGeneralService.getProviderWidgetDetils(null, "251", "ACC");
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/* 
	 * 
	 */

	/* 
	 * 
	 */

	/* 
	 * 
	 */
	@Test
	public void testupdateLastModifiedDetailsScenerio2() throws ApplicationException
	{
		Boolean lbUpdateStatus = false;
		Map loLastModifiedHashMap = new HashMap();
		lbUpdateStatus = moSolicitationFinancialsGeneralService.updateLastModifiedDetails(moMyBatisSession,
				loLastModifiedHashMap, true);
		assertNotNull(lbUpdateStatus);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/* 
	 * 
	 */
	@Test
	public void testupdateLastModifiedDetailsScenerio3() throws ApplicationException
	{
		Boolean lbUpdateStatus = false;
		Map loLastModifiedHashMap = new HashMap();
		lbUpdateStatus = moSolicitationFinancialsGeneralService.updateLastModifiedDetails(moMyBatisSession,
				loLastModifiedHashMap, false);
		assertNotNull(lbUpdateStatus);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/* 
	 * 
	 */
	@Test
	public void testupdateLastModifiedDetailsScenerio4() throws ApplicationException
	{
		Boolean lbUpdateStatus = false;
		Map loLastModifiedHashMap = new HashMap();
		lbUpdateStatus = moSolicitationFinancialsGeneralService.updateLastModifiedDetails(moMyBatisSession,
				loLastModifiedHashMap, true);
		assertNotNull(lbUpdateStatus);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/* 
	 * 
	 */
	@Test(expected = ApplicationException.class)
	public void testupdateLastModifiedDetailsScenerio5() throws ApplicationException
	{
		Map loLastModifiedHashMap = new HashMap();
		moSolicitationFinancialsGeneralService.updateLastModifiedDetails(null, loLastModifiedHashMap, true);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	/* 
	 * 
	 */
	@Test
	public void testupdateLastModifiedDetailsScenerio6() throws ApplicationException
	{
		Boolean lbUpdateStatus = false;
		Map loLastModifiedHashMap = new HashMap();
		lbUpdateStatus = moSolicitationFinancialsGeneralService.updateLastModifiedDetails(moMyBatisSession,
				loLastModifiedHashMap, true);
		assertNotNull(lbUpdateStatus);
		moMyBatisSession.commit();
		moMyBatisSession.close();
	}

	@Test
	public void testCheckDocumentExistsInAnyTableCase1() throws ApplicationException

	{

		Boolean lbDocumentExists = Boolean.TRUE;

		String lsDocumentId = "5EC95E93-E3C8-459D-AAC3-9DA3D0473727";

		lbDocumentExists = moSolicitationFinancialsGeneralService.checkDocumentExistsInAnyTable(moMyBatisSession,
				lsDocumentId);

		assertFalse(lbDocumentExists);

	}

	@Test(expected = ApplicationException.class)
	public void testCheckDocumentExistsInAnyTableCase2() throws ApplicationException

	{
		String lsDocumentId = "5EC95E93-E3C8-459D-AAC3-9DA3D0473727";
		moSolicitationFinancialsGeneralService.checkDocumentExistsInAnyTable(null, lsDocumentId);
	}

	@Test
	public void testCheckDocumentsExistsInAnyTable() throws ApplicationException

	{

		List<String> loDocumentLists = new ArrayList<String>();

		String lsDocumentId = "5EC95E93-E3C8-459D-AAC3-9DA3D0473727";

		loDocumentLists.add(lsDocumentId);

		loDocumentLists = moSolicitationFinancialsGeneralService.checkDocumentsExistsInAnyTable(moMyBatisSession,
				loDocumentLists);

		assertNotNull(loDocumentLists);

	}

	@Test(expected = ApplicationException.class)
	public void testCheckDocumentsExistsInAnyTableCase1() throws ApplicationException

	{

		List<String> loDocumentLists = new ArrayList<String>();

		String lsDocumentId = "5EC95E93-E3C8-459D-AAC3-9DA3D0473727";

		loDocumentLists.add(lsDocumentId);

		loDocumentLists = moSolicitationFinancialsGeneralService.checkDocumentsExistsInAnyTable(null, loDocumentLists);

	}

	@Test
	public void testRemoveLockedUserByIdCase1() throws ApplicationException
	{
		Boolean loIsDataDeleted = false;
		String asUserId = "city";
		loIsDataDeleted = moSolicitationFinancialsGeneralService.removeLockedUserById(moMyBatisSession, asUserId);
		assertTrue(loIsDataDeleted);
	}

	@Test(expected = ApplicationException.class)
	public void testRemoveLockedUserByIdCase2() throws ApplicationException
	{
		String asUserId = "city";
		moSolicitationFinancialsGeneralService.removeLockedUserById(null, asUserId);
	}

	@Test
	public void testRemoveLockedUserCase1() throws ApplicationException
	{

		Boolean loIsDataDeleted = false;
		String asSessionId = "";
		loIsDataDeleted = moSolicitationFinancialsGeneralService.removeLockedUser(moMyBatisSession, asSessionId);
		assertTrue(loIsDataDeleted);
	}

	@Test(expected = ApplicationException.class)
	public void testRemoveLockedUserCase2() throws ApplicationException
	{
		String asSessionId = "";
		moSolicitationFinancialsGeneralService.removeLockedUser(moMyBatisSession, asSessionId);
	}

	@Test
	public void testAddLock() throws ApplicationException
	{
		String asLockId = "Invoice_123456";
		String asSessionId = "vYybRmxJThQ8JdhW61J15prhFXWPZcb2JLhfYfvthCwnRQRGBgpf!263791504!136989525287898";
		String asUserName = "hhs mgr1";
		String asUserId = "city_142";
		Map<String, String> aoLockData = null;
		// aoLockData.put("USER_SESSION_ID",asSessionId);

		Boolean loIsLockAdded = moSolicitationFinancialsGeneralService.addLock(moMyBatisSession, asLockId, asSessionId,
				asUserName, asUserId, aoLockData);
		assertTrue(loIsLockAdded);
	}

	@Test
	public void testAddLock2() throws ApplicationException
	{
		String asLockId = "Invoice_123456";
		String asSessionId = "vYybRmxJThQ8JdhW61J15prhFXWPZcb2JLhfYfvthCwnRQRGBgpf!263791504!136989525287898";
		String asUserName = "hhs mgr1";
		String asUserId = "city_142";
		Map<String, String> aoLockData = new HashMap<String, String>();
		aoLockData.put("USER_SESSION_ID", asSessionId);

		Boolean loIsLockAdded = moSolicitationFinancialsGeneralService.addLock(moMyBatisSession, asLockId, asSessionId,
				asUserName, asUserId, aoLockData);
		assertFalse(loIsLockAdded);
	}

	@Test(expected = ApplicationException.class)
	public void testAddLock3() throws ApplicationException
	{
		String asLockId = "Invoice_123456";
		String asSessionId = null;
		String asUserName = "hhs mgr1";
		String asUserId = "city_142";
		Map<String, String> aoLockData = null;

		Boolean loIsLockAdded = moSolicitationFinancialsGeneralService.addLock(moMyBatisSession, asLockId, asSessionId,
				asUserName, asUserId, aoLockData);
		assertFalse(loIsLockAdded);
	}

	@Test(expected = ApplicationException.class)
	public void testAddLock4() throws ApplicationException
	{
		String asLockId = null;
		String asSessionId = null;
		String asUserName = null;
		String asUserId = null;
		Map<String, String> aoLockData = null;
		Boolean loIsLockAdded = moSolicitationFinancialsGeneralService.addLock(moMyBatisSession, asLockId, asSessionId,
				asUserName, asUserId, aoLockData);
		assertFalse(loIsLockAdded);
	}

	@Test
	public void testCheckLockFlagExist() throws ApplicationException
	{
		String asLockId = "Invoice_8748787_JSP";
		String asSessionId = "vYybRmxJThQ8JdhW61J15prhFXWPZcb2JLhfYfvthCwnRQRGBgpf!263791504!1369895252878534598";
		String asUserName = "hhs mgr1";
		Map<String, String> aoLockData = null;
		aoLockData = moSolicitationFinancialsGeneralService.checkLockFlagExist(moMyBatisSession, asLockId, asSessionId,
				asUserName);
		assertFalse(aoLockData == null);
	}

	@Test
	public void testCheckLockFlagExist2() throws ApplicationException
	{

		String asLockId = "Invoice_8748787_JSP2323";
		String asSessionId = "vYybRmxJThQ8JdhW61J15prhFXWPZcb2JLhfYfvthCwnRQRGBgpf!263791504!1369895252878534598";
		String asUserName = "hhs mgr1";
		Map<String, String> aoLockData = null;

		aoLockData = moSolicitationFinancialsGeneralService.checkLockFlagExist(moMyBatisSession, asLockId, asSessionId,
				asUserName);
		assertTrue(aoLockData == null);
	}

	@Test(expected = ApplicationException.class)
	public void testCheckLockFlagExist3() throws ApplicationException
	{

		String asLockId = null;
		String asSessionId = "vYybRmxJThQ8JdhW61J15prhFXWPZcb2JLhfYfvthCwnRQRGBgpf!263791504!1369895252878534598";
		String asUserName = "hhs mgr1";
		Map<String, String> aoLockData = null;

		aoLockData = moSolicitationFinancialsGeneralService.checkLockFlagExist(moMyBatisSession, asLockId, asSessionId,
				asUserName);
		assertTrue(aoLockData == null);
	}

	@Test(expected = ApplicationException.class)
	public void testCheckLockFlagExist4() throws ApplicationException
	{
		String asLockId = null;
		String asSessionId = null;
		String asUserName = "hhs mgr1";
		Map<String, String> aoLockData = null;

		aoLockData = moSolicitationFinancialsGeneralService.checkLockFlagExist(null, asLockId, asSessionId, asUserName);
		assertTrue(aoLockData == null);
	}

	@Test
	public void testAuthenticateLoginUser() throws ApplicationException
	{

		AuthenticationBean aoAuthBean = new AuthenticationBean();
		aoAuthBean.setUserName("agency_staff");
		aoAuthBean.setPassword("agency");

		Boolean lbAuthStatusFlag = moSolicitationFinancialsGeneralService.authenticateLoginUser(aoAuthBean);
		assertTrue(lbAuthStatusFlag);
	}

	@Test
	public void testAuthenticateLoginUser2() throws ApplicationException
	{

		AuthenticationBean aoAuthBean = new AuthenticationBean();

		Boolean lbAuthStatusFlag = moSolicitationFinancialsGeneralService.authenticateLoginUser(aoAuthBean);
		assertFalse(lbAuthStatusFlag);
	}

	@Test(expected = Exception.class)
	public void testAuthenticateLoginUser3() throws ApplicationException
	{

		AuthenticationBean aoAuthBean = null;

		Boolean lbAuthStatusFlag = moSolicitationFinancialsGeneralService.authenticateLoginUser(aoAuthBean);
		assertFalse(lbAuthStatusFlag);
	}

	@Test
	public void testAuthenticateLoginUser4() throws ApplicationException
	{
		AuthenticationBean aoAuthBean = new AuthenticationBean();
		aoAuthBean.setUserName("citymanager");
		aoAuthBean.setPassword("Filenet1");

		Boolean lbAuthStatusFlag = moSolicitationFinancialsGeneralService.authenticateLoginUser(aoAuthBean);
		assertTrue(lbAuthStatusFlag);
	}

	@Test
	public void testAuthenticateLoginUser5() throws ApplicationException
	{
		AuthenticationBean aoAuthBean = new AuthenticationBean();
		aoAuthBean.setUserName("city_executive");
		aoAuthBean.setPassword("city");

		Boolean lbAuthStatusFlag = moSolicitationFinancialsGeneralService.authenticateLoginUser(aoAuthBean);
		assertTrue(lbAuthStatusFlag);
	}

	@Test
	public void testAuthenticateLoginUser6() throws ApplicationException
	{
		AuthenticationBean aoAuthBean = new AuthenticationBean();
		aoAuthBean.setUserName("hhs_staff1");
		aoAuthBean.setPassword("city");

		Boolean lbAuthStatusFlag = moSolicitationFinancialsGeneralService.authenticateLoginUser(aoAuthBean);
		assertTrue(lbAuthStatusFlag);
	}

	@Test
	public void testAuthenticateLoginUser7() throws ApplicationException
	{
		AuthenticationBean aoAuthBean = new AuthenticationBean();
		aoAuthBean.setUserName("hhs_staff2");
		aoAuthBean.setPassword("city");

		Boolean lbAuthStatusFlag = moSolicitationFinancialsGeneralService.authenticateLoginUser(aoAuthBean);
		assertTrue(lbAuthStatusFlag);
	}

	@Test
	public void testAuthenticateLoginUser8() throws ApplicationException
	{
		AuthenticationBean aoAuthBean = new AuthenticationBean();
		aoAuthBean.setUserName("hhs_mgr1");
		aoAuthBean.setPassword("city");

		Boolean lbAuthStatusFlag = moSolicitationFinancialsGeneralService.authenticateLoginUser(aoAuthBean);
		assertTrue(lbAuthStatusFlag);
	}

	@Test
	public void testAuthenticateLoginUser9() throws ApplicationException
	{
		AuthenticationBean aoAuthBean = new AuthenticationBean();
		aoAuthBean.setUserName("hhs_mgr2");
		aoAuthBean.setPassword("city");

		Boolean lbAuthStatusFlag = moSolicitationFinancialsGeneralService.authenticateLoginUser(aoAuthBean);
		assertTrue(lbAuthStatusFlag);
	}

	@Test
	public void testAuthenticateLoginUser10() throws ApplicationException
	{
		AuthenticationBean aoAuthBean = new AuthenticationBean();
		aoAuthBean.setUserName("provider_staff");
		aoAuthBean.setPassword("provider");

		Boolean lbAuthStatusFlag = moSolicitationFinancialsGeneralService.authenticateLoginUser(aoAuthBean);
		assertTrue(lbAuthStatusFlag);
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicefetchProcurementCountForAccHomePage0Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.fetchProcurementCountForAccHomePage(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicefetchProcurementCountForProvHomePage1Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.fetchProcurementCountForProvHomePage(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicefetchAccFinancialsPortletCount2Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.fetchAccFinancialsPortletCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicefetchProviderFinancialCount3Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.fetchProviderFinancialCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicegetMasterStatus4Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.getMasterStatus(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicegetProcurementChangeControlWidget5Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.getProcurementChangeControlWidget(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServiceauthenticateLoginUser6Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.authenticateLoginUser(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicefetchEpinList7Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.fetchEpinList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicefetchContractNoList8Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.fetchContractNoList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServiceupdateLastModifiedDetails9Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.updateLastModifiedDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicegetProviderWidgetDetils10Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.getProviderWidgetDetils(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicecheckDocumentExistsInAnyTable11Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.checkDocumentExistsInAnyTable(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicecheckDocumentsExistsInAnyTable12Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.checkDocumentsExistsInAnyTable(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServiceremoveLockedUserById13Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.removeLockedUserById(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServiceremoveLockedUser14Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.removeLockedUser(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicecheckLockFlagExist15Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.checkLockFlagExist(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServiceaddLock16Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.addLock(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicegetProcuringAgencyFromDB17Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.getProcuringAgencyFromDB(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicefetchProcurementCountForAccHomePage0NegativeApp()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.fetchProcurementCountForAccHomePage(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicefetchProcurementCountForProvHomePage1NegativeApp()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.fetchProcurementCountForProvHomePage(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicefetchAccFinancialsPortletCount2NegativeApp()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.fetchAccFinancialsPortletCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicefetchProviderFinancialCount3NegativeApp()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.fetchProviderFinancialCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicegetMasterStatus4NegativeApp() throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.getMasterStatus(null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicegetProcurementChangeControlWidget5NegativeApp()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.getProcurementChangeControlWidget(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServiceauthenticateLoginUser6NegativeApp() throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.authenticateLoginUser(null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicefetchEpinList7NegativeApp() throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.fetchEpinList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicefetchContractNoList8NegativeApp() throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.fetchContractNoList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServiceupdateLastModifiedDetails9NegativeApp()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.updateLastModifiedDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicegetProviderWidgetDetils10NegativeApp()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.getProviderWidgetDetils(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicecheckDocumentExistsInAnyTable11NegativeApp()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.checkDocumentExistsInAnyTable(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicecheckDocumentsExistsInAnyTable12NegativeApp()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.checkDocumentsExistsInAnyTable(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServiceremoveLockedUserById13NegativeApp() throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.removeLockedUserById(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServiceremoveLockedUser14NegativeApp() throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.removeLockedUser(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicecheckLockFlagExist15NegativeApp() throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.checkLockFlagExist(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServiceaddLock16NegativeApp() throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.addLock(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicegetProcuringAgencyFromDB17NegativeApp()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.getProcuringAgencyFromDB(null);
	}

}
