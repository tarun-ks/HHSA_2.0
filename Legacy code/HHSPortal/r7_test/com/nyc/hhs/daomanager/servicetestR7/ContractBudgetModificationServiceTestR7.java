package com.nyc.hhs.daomanager.servicetestR7;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.validation.constraints.AssertTrue;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.ContractBudgetModificationService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBProgramIncomeBean;
import com.nyc.hhs.model.CBServicesBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.LineItemMasterBean;
import com.nyc.hhs.model.MasterBean;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.SiteDetailsBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DAOUtil.class)
public class ContractBudgetModificationServiceTestR7
{
	ContractBudgetModificationService contractBudgetModificationService = new ContractBudgetModificationService();
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
			moSession.rollback();
			moSession.close();
		}
	}

	@Test(expected = ApplicationException.class)
	public void getModificationBudgetCountTest1() throws ApplicationException
	{
		HashMap<String, String> loMap = new HashMap<String, String>();
		loMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "12434");
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		contractBudgetModificationService.getModificationBudgetCount(null, loMap);
	}

	@Test(expected = ApplicationException.class)
	public void getModificationBudgetCountTest2() throws ApplicationException

	{
		HashMap<String, String> loMap = new HashMap<String, String>();
		loMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "11050");
		contractBudgetModificationService.getModificationBudgetCount(null, loMap);
	}

	@Test
	public void getModificationBudgetCountTest3() throws ApplicationException
	{
		HashMap<String, String> loMap = new HashMap<String, String>();
		loMap.put(HHSConstants.BUDGET_ID_WORKFLOW, "11050");
		String lsCount = contractBudgetModificationService.getModificationBudgetCount(moSession, loMap);
		assertEquals(lsCount, "0");
	}

	@Test
	public void updateAutoApprovalShowInfoFlagTest1() throws ApplicationException
	{
		Integer loCount = contractBudgetModificationService.updateAutoApprovalShowInfoFlag(moSession, "11050", false);
		assertEquals(loCount.toString(), "0");
	}

	@Test
	public void updateAutoApprovalShowInfoFlagTest2() throws ApplicationException
	{
		Integer loCount = contractBudgetModificationService.updateAutoApprovalShowInfoFlag(moSession, null, true);
		assertEquals(loCount.toString(), "0");
	}

	@Test
	public void updateAutoApprovalShowInfoFlagTest3() throws ApplicationException
	{
		Integer loCount = contractBudgetModificationService.updateAutoApprovalShowInfoFlag(moSession, "", true);
		assertEquals(loCount.toString(), "0");
	}

	@Test
	public void updateAutoApprovalShowInfoFlagTest4() throws ApplicationException
	{
		Integer loCount = contractBudgetModificationService.updateAutoApprovalShowInfoFlag(moSession, "11050", true);
		assertEquals(loCount.toString(), "1");
	}

	@Test(expected = ApplicationException.class)
	public void updateAutoApprovalShowInfoFlagTest5() throws ApplicationException
	{
		Integer loCount = contractBudgetModificationService.updateAutoApprovalShowInfoFlag(null, "11050", true);
		assertEquals(loCount.toString(), "0");
	}

	@Test(expected = ApplicationException.class)
	public void updateAutoApprovalShowInfoFlagTest6() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		contractBudgetModificationService.updateAutoApprovalShowInfoFlag(null, "11050", true);
	}
	
	@Test(expected = ApplicationException.class)
	public void updateAutoApprovalShowInfoFlagTest7() throws ApplicationException
	{
		Integer loCount = contractBudgetModificationService.updateAutoApprovalShowInfoFlag(null, "10340", null);
		assertEquals(loCount.toString(), "0");
	}
	
	@Test
	public void deleteCancelledAmendmentBudgetTest7() throws ApplicationException
	{	
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
	aoHashMap.put("contractId", "12659");
	aoHashMap.put("fiscalYearID", "2018");
	//aoHashMap.put("fiscalYearID", "2018");
	
	Boolean lbStatus = contractBudgetModificationService.deleteCancelledAmendmentBudget(moSession,aoHashMap);
	assertTrue(lbStatus);
	}
	
	@Test
	public void deleteCancelledAmendmentBudgetTest8() throws ApplicationException
	{	
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
	aoHashMap.put("contractId", "12663");
	aoHashMap.put("fiscalYearID", "2018");
	Boolean lbStatus = contractBudgetModificationService.deleteCancelledAmendmentBudget(moSession,aoHashMap);
	assertTrue(lbStatus);
	}
	
	/**
	 * This method tests fetchProgramIncomeModification method for bad data
	 * inputs and an Exception is expected
	 * 
	 * @throws Exception If an Exception occurs
	 */
	@Test(expected = Exception.class)
	public void testFetchProgramIncomeModificationWithException() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetService = new ContractBudgetModificationService();
		CBGridBean loCBGridBean = null;
		MasterBean loMasterBean = null;
		loContractBudgetService.fetchProgramIncomeModification(loCBGridBean, moSession, loMasterBean);
	}
	
	@Test
	public void testUpdateIsOldPIFlagForModification() throws ApplicationException
	{
		String aobudgetId ="11620";
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Integer loRowInserted = loContractBudgetModificationService.updateIsOldPIFlagForModification(moSession, aobudgetId);
		assertEquals("1", loRowInserted.toString());
	}
	@Test
	public void testUpdateIsOldPIFlagForModification1() throws ApplicationException
	{
		String aobudgetId ="10730";
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Integer loRowInserted = loContractBudgetModificationService.updateIsOldPIFlagForModification(moSession, aobudgetId);
		assertEquals("0", loRowInserted.toString());
	}
	@Test(expected = ApplicationException.class)
	public void testUpdateIsOldPIFlagForModification2() throws ApplicationException
	{
		String aobudgetId ="11620";
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Integer loRowInserted = loContractBudgetModificationService.updateIsOldPIFlagForModification(null, aobudgetId);
		assertEquals("1", loRowInserted.toString());
	}
	
	@Test
	public void testUpdatePIPercentforSubBudget() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean= new TaskDetailsBean();
		CBGridBean aoCbGridBean =new CBGridBean();
		MasterBean loMasterBean = new MasterBean();
		aoCbGridBean.setContractBudgetID("11620");
		
		aoTaskDetailsBean.setBudgetId("11620");
		aoTaskDetailsBean.setUserId("mgr3");
		List<MasterBean> masterBeanList = new ArrayList<MasterBean>();
		loMasterBean.setBudgetId("11812");
		masterBeanList.add(loMasterBean);
		ArrayList<SiteDetailsBean> aoSubBudgetDetails = new ArrayList<SiteDetailsBean>();
		SiteDetailsBean loSiteBean =new SiteDetailsBean();
		loSiteBean.setSubBudgetId("15481");
		loSiteBean.setParentSubBudgetId("15481");
		aoSubBudgetDetails.add(loSiteBean);
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Boolean loRowInserted = loContractBudgetModificationService.updatePIPercentforSubBudget(moSession, aoTaskDetailsBean, aoSubBudgetDetails);
		assertEquals("true", loRowInserted.toString());
	}
	
	@Test
	public void testUpdatePIPercentforSubBudget1() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean= new TaskDetailsBean();
		CBGridBean aoCbGridBean =new CBGridBean();
		MasterBean loMasterBean = new MasterBean();
		aoCbGridBean.setContractBudgetID("11620");
		
		aoTaskDetailsBean.setBudgetId("11620");
		aoTaskDetailsBean.setUserId("hhs");
		List<MasterBean> masterBeanList = new ArrayList<MasterBean>();
		loMasterBean.setBudgetId("11812");
		masterBeanList.add(loMasterBean);
		ArrayList<SiteDetailsBean> aoSubBudgetDetails = new ArrayList<SiteDetailsBean>();
		SiteDetailsBean loSiteBean =new SiteDetailsBean();
		loSiteBean.setSubBudgetId("15461");
		loSiteBean.setParentSubBudgetId("15481");
		aoSubBudgetDetails.add(loSiteBean);
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Boolean loRowInserted = loContractBudgetModificationService.updatePIPercentforSubBudget(moSession, aoTaskDetailsBean, aoSubBudgetDetails);
		assertEquals("false", loRowInserted.toString());
	}
	@Test
	public void testUpdatePIPercentforSubBudget2() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean= new TaskDetailsBean();
	
		CBGridBean aoCbGridBean =new CBGridBean();
		aoCbGridBean.setContractBudgetID("11812");
		aoTaskDetailsBean.setBudgetId("11812");
		aoTaskDetailsBean.setUserId("mgr3");
		ArrayList<SiteDetailsBean> aoSubBudgetDetails = new ArrayList<SiteDetailsBean>();
		SiteDetailsBean loSiteBean =new SiteDetailsBean();
		loSiteBean.setSubBudgetId("15701");
		loSiteBean.setParentSubBudgetId("15701");
		aoSubBudgetDetails.add(loSiteBean);
		
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Boolean loRowInserted = loContractBudgetModificationService.updatePIPercentforSubBudget(moSession, aoTaskDetailsBean, aoSubBudgetDetails);
		assertEquals("true", loRowInserted.toString());
	}
	@Test(expected = ApplicationException.class)
	public void testUpdatePIPercentforSubBudget3() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean= new TaskDetailsBean();
	
		CBGridBean aoCbGridBean =new CBGridBean();
		aoCbGridBean.setContractBudgetID("11812");
		aoTaskDetailsBean.setBudgetId("11812");
		aoTaskDetailsBean.setUserId("mgr3");
		ArrayList<SiteDetailsBean> aoSubBudgetDetails = new ArrayList<SiteDetailsBean>();
		SiteDetailsBean loSiteBean =new SiteDetailsBean();
		loSiteBean.setSubBudgetId("15701");
		loSiteBean.setParentSubBudgetId("15701");
		aoSubBudgetDetails.add(loSiteBean);
		
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Boolean loRowInserted = loContractBudgetModificationService.updatePIPercentforSubBudget(null, aoTaskDetailsBean, aoSubBudgetDetails);
		assertEquals("true", loRowInserted.toString());
	}
	
	@Test
	public void testCheckBudgetsForUpdateAutoApproval() throws ApplicationException
	{
		HashMap<String, Object> aoHMWFRequiredProps = new HashMap<String, Object>();
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_TYPE,"4");
		aoHMWFRequiredProps.put("budgetId", "11990");
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Boolean lsUpdateAutoApproved = loContractBudgetModificationService.checkBudgetsForUpdateAutoApproval(moSession, aoHMWFRequiredProps);
		assertEquals("true", lsUpdateAutoApproved.toString());
	}
	@Test
	public void testCheckBudgetsForUpdateAutoApproval2() throws ApplicationException
	{
		HashMap<String, Object> aoHMWFRequiredProps = new HashMap<String, Object>();
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_TYPE,"4");
		aoHMWFRequiredProps.put("budgetId", "11749");
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Boolean lsUpdateAutoApproved = loContractBudgetModificationService.checkBudgetsForUpdateAutoApproval(moSession, aoHMWFRequiredProps);
		assertEquals("true", lsUpdateAutoApproved.toString());
	}
	@Test(expected = ApplicationException.class)
	public void testCheckBudgetsForUpdateAutoApproval3() throws ApplicationException
	{
		HashMap<String, Object> aoHMWFRequiredProps = new HashMap<String, Object>();
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_TYPE,"4");
		aoHMWFRequiredProps.put("budgetId", "11749");
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Boolean lsUpdateAutoApproved = loContractBudgetModificationService.checkBudgetsForUpdateAutoApproval(moSession, null);
		assertEquals("true", lsUpdateAutoApproved.toString());
	}
	
	@Test
	public void testCheckAutoApprovalForContractConfigurationUpdateTask() throws ApplicationException
	{
		
		TaskDetailsBean aoTaskDetailsBean= new TaskDetailsBean();
		P8UserSession aoUserSession=new P8UserSession();
		String contractId = "12849";
		String asUpdateCOntractId = "12849";
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Boolean lbAutoUpdate = loContractBudgetModificationService.checkAutoApprovalForContractConfigurationUpdateTask(aoUserSession, moSession, aoTaskDetailsBean, contractId);
		assertEquals("true", lbAutoUpdate.toString());
	}
	@Test(expected = ApplicationException.class)
	public void testCheckAutoApprovalForContractConfigurationUpdateTask2() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean= new TaskDetailsBean();
		P8UserSession aoUserSession=new P8UserSession();
		String contractId = "12625";
		String asUpdateCOntractId = "12625";
		HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapForBudgetUpdate = new HashMap<String, Object>();
		HashMap<String, Object> loHmRequiredPropsForUpdate = new HashMap<String, Object>();
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Boolean lbAutoUpdate = loContractBudgetModificationService.checkAutoApprovalForContractConfigurationUpdateTask(aoUserSession, moSession, aoTaskDetailsBean, contractId);
		assertEquals("true", lbAutoUpdate.toString());
	}
	@Test
	public void testCheckAutoApprovalForContractConfigurationUpdateTask3() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean= new TaskDetailsBean();
		P8UserSession aoUserSession=new P8UserSession();
		String contractId = "12854";
		String asUpdateCOntractId = "12854";
		HashMap<String, Object> loHmRequiredProps = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapForBudgetUpdate = new HashMap<String, Object>();
		HashMap<String, Object> loHmRequiredPropsForUpdate = new HashMap<String, Object>();
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Boolean lbAutoUpdate = loContractBudgetModificationService.checkAutoApprovalForContractConfigurationUpdateTask(aoUserSession, moSession, aoTaskDetailsBean, contractId);
		assertEquals("true", lbAutoUpdate.toString());
	}
	
	@Test
	public void testMergeBudgetLineItemsAmendmentNoBudgetEffected() throws ApplicationException
	{
		
		Boolean aoFinishTaskStatus=true;
		Boolean loNegativeAmendmentNoBudgetEffected=true;
		Boolean loMergeSuccess = false;
		HashMap aoHashMap = new HashMap();
		aoHashMap.put("contractId", "11167");
		Boolean isAmendmentRegisteredInFMS =true;
		Boolean loAmendmentBudgetIds =true;
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Boolean lbAutoUpdate = loContractBudgetModificationService.mergeBudgetLineItemsAmendmentNoBudgetEffected(moSession, aoHashMap, aoFinishTaskStatus, loNegativeAmendmentNoBudgetEffected);
		assertEquals("true", lbAutoUpdate.toString());
	}
	@Test(expected = ApplicationException.class)
	public void testMergeBudgetLineItemsAmendmentNoBudgetEffected2() throws ApplicationException
	{
		Boolean aoFinishTaskStatus=true;
		Boolean loNegativeAmendmentNoBudgetEffected=true;
		HashMap aoHashMap = new HashMap();
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Boolean lbAutoUpdate = loContractBudgetModificationService.mergeBudgetLineItemsAmendmentNoBudgetEffected(moSession, aoHashMap, aoFinishTaskStatus, loNegativeAmendmentNoBudgetEffected);
		assertEquals("true", lbAutoUpdate.toString());
	}
	@Test
	public void testMergeBudgetLineItemsAmendmentNoBudgetEffected3() throws ApplicationException
	{
		Boolean aoFinishTaskStatus=true;
		Boolean loNegativeAmendmentNoBudgetEffected=true;
		HashMap aoHashMap = new HashMap();
		aoHashMap.put("contractId", "11167");
		Boolean isAmendmentRegisteredInFMS =false;
		Boolean loAmendmentBudgetIds =false;
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		Boolean lbAutoUpdate = loContractBudgetModificationService.mergeBudgetLineItemsAmendmentNoBudgetEffected(moSession, aoHashMap, aoFinishTaskStatus, loNegativeAmendmentNoBudgetEffected);
		assertEquals("true", lbAutoUpdate.toString());
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchContractServicesModificationGridTest1() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		MasterBean master = new MasterBean();
		aoCBGridBeanObj.setParentBudgetId("11442");
		aoCBGridBeanObj.setParentSubBudgetId("15304");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		contractBudgetModificationService.fetchContractServicesModificationGrid(aoCBGridBeanObj,null, master);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchContractServicesModificationGridTest2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		MasterBean master = new MasterBean();
		aoCBGridBeanObj.setParentBudgetId("11634");
		aoCBGridBeanObj.setParentSubBudgetId("15437");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		contractBudgetModificationService.fetchContractServicesModificationGrid(aoCBGridBeanObj,moSession, master);
	}
	
	@Test
	public void fetchContractServicesModificationGridTest3() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		MasterBean master = new MasterBean();
		aoCBGridBeanObj.setParentBudgetId("11634");
		aoCBGridBeanObj.setParentSubBudgetId("15437");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		List<CBServicesBean> list = contractBudgetModificationService.fetchContractServicesModificationGrid(aoCBGridBeanObj,moSession, master);
		System.out.println(list.size());
		assertEquals(3, list.size());
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchContractCostCenterModificationGridTest1() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		MasterBean master = new MasterBean();
		aoCBGridBeanObj.setParentBudgetId("11442");
		aoCBGridBeanObj.setParentSubBudgetId("15304");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		contractBudgetModificationService.fetchContractCostCenterModificationGrid(aoCBGridBeanObj,null, master);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchContractCostCenterModificationGridTest2() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		MasterBean master = new MasterBean();
		aoCBGridBeanObj.setParentBudgetId("11634");
		aoCBGridBeanObj.setParentSubBudgetId("15437");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		contractBudgetModificationService.fetchContractCostCenterModificationGrid(aoCBGridBeanObj,moSession, master);
	}
	
	@Test
	public void fetchContractCostCenterModificationGridTest3() throws ApplicationException
	{
		CBGridBean aoCBGridBeanObj = new CBGridBean();
		MasterBean master = new MasterBean();
		aoCBGridBeanObj.setParentBudgetId("11634");
		aoCBGridBeanObj.setParentSubBudgetId("15437");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		List<CBServicesBean> list = contractBudgetModificationService.fetchContractCostCenterModificationGrid(aoCBGridBeanObj,moSession, master);
		assertEquals(1, list.size());
	}
	
	@Test(expected = ApplicationException.class)
	public void editServiceModificationDetailsTest1() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModificationAmt("200");
		aoCBGridBeanObj.setModUnits("20");
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("37");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		contractBudgetModificationService.editServiceModificationDetails(aoCBGridBeanObj,null);
	}
	
	@Test(expected = ApplicationException.class)
	public void editServiceModificationDetailsTest2() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModificationAmt("200");
		aoCBGridBeanObj.setModUnits("20");
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("37");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		contractBudgetModificationService.editServiceModificationDetails(aoCBGridBeanObj,moSession);
	}
	
	@Test
	public void editServiceModificationDetailsTest3() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModificationAmt("300");
		aoCBGridBeanObj.setModUnits("20");
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("38");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenReturn(new Integer(-1));
		contractBudgetModificationService.editServiceModificationDetails(aoCBGridBeanObj,moSession);
	}
	
	@Test(expected = ApplicationException.class)
	public void editServiceModificationDetailsTest4() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModificationAmt("-300");
		aoCBGridBeanObj.setModUnits("20");
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("38");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		contractBudgetModificationService.editServiceModificationDetails(aoCBGridBeanObj,moSession);
	}
	
	@Test(expected = ApplicationException.class)
	public void editServiceModificationDetailsTest5() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModificationAmt("300");
		aoCBGridBeanObj.setModUnits("-20");
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("38");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		contractBudgetModificationService.editServiceModificationDetails(aoCBGridBeanObj,moSession);
	}
	
	@Test
	public void editServiceModificationDetailsTest6() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModificationAmt("300");
		aoCBGridBeanObj.setModUnits("20");
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("38");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		assert(contractBudgetModificationService.editServiceModificationDetails(aoCBGridBeanObj,moSession));
	}
	
	@Test(expected = ApplicationException.class)
	public void editCostCenterModificationDetailsTest1() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModificationAmt("-200");
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("46");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		contractBudgetModificationService.editCostCenterModificationDetails(aoCBGridBeanObj,null);
	}
	
	@Test(expected = Exception.class)
	public void editCostCenterModificationDetailsTest2() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModificationAmt("200");
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("46");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		contractBudgetModificationService.editCostCenterModificationDetails(aoCBGridBeanObj,moSession);
	}
	
	@Test
	public void editCostCenterModificationDetailsTest4() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModificationAmt("-300");
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("46");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		assert(contractBudgetModificationService.editCostCenterModificationDetails(aoCBGridBeanObj,moSession));
	}
	
	
	@Test(expected = ApplicationException.class)
	public void editCostCenterModificationDetailsTest3() throws ApplicationException
	{
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModificationAmt("-350");
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("46");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		contractBudgetModificationService.editCostCenterModificationDetails(aoCBGridBeanObj,moSession);
	}
	
	@Test
	public void editCostCenterModificationDetailsTest5() throws ApplicationException
	{
		//insert block
		CBServicesBean aoCBGridBeanObj = new CBServicesBean();
		aoCBGridBeanObj.setModificationAmt("300");
		aoCBGridBeanObj.setModifyByProvider("system");
		aoCBGridBeanObj.setId("46");
		aoCBGridBeanObj.setContractBudgetID("11609");
		aoCBGridBeanObj.setSubBudgetID("15398");
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenReturn(new Integer(-1));
		contractBudgetModificationService.editCostCenterModificationDetails(aoCBGridBeanObj,moSession);
	}
	
	@Test(expected = ApplicationException.class)
	public void validateServicesModAmount() throws ApplicationException
	{
		String aoBudgetId ="10730";
		contractBudgetModificationService.validateServicesModAmount(null,aoBudgetId);
	}
	
	@Test(expected = ApplicationException.class)
	public void validateServicesModAmountTest1() throws ApplicationException
	{
		String aoBudgetId ="11442";
		contractBudgetModificationService.validateServicesModAmount(null,aoBudgetId);
	}
	
	@Test(expected = Exception.class)
	public void validateServicesModAmountTest2() throws ApplicationException
	{
		String aoBudgetId ="11442";
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(String.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		contractBudgetModificationService.validateServicesModAmount(moSession,aoBudgetId);
	}
	
	@Test
	public void validateServicesModAmountTest3() throws ApplicationException
	{
		String aoBudgetId ="11442";
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(String.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenReturn(new Integer(0));
		contractBudgetModificationService.validateServicesModAmount(moSession,aoBudgetId);
	}
	
	@Test
	public void validateServicesModAmountTest4() throws ApplicationException
	{
		String aoBudgetId ="11442";
		contractBudgetModificationService.validateServicesModAmount(moSession,aoBudgetId);
	}
	
	@Test
	public void validateServicesModAmountTest5() throws ApplicationException
	{
		String aoBudgetId ="11442";
		contractBudgetModificationService.validateServicesModAmount(moSession,aoBudgetId);
	}
	
	@Test
	public void validateServicesModAmountTest6() throws ApplicationException
	{
		String aoBudgetId ="10747";
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(String.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenReturn(new Integer(0));
		contractBudgetModificationService.validateServicesModAmount(moSession,aoBudgetId);
	}
	
	@Test(expected = ApplicationException.class)
	public void mergeBudgetLineItemsForModification() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		String aoStatus = new String();
		contractBudgetModificationService.mergeBudgetLineItemsForModification(null,null,aoStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void mergeBudgetLineItemsForModificationTest1() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		String aoStatus = new String();
		contractBudgetModificationService.mergeBudgetLineItemsForModification(null,aoTaskDetailsBean,aoStatus);
	}
	
	@Test(expected = Exception.class)
	public void mergeBudgetLineItemsForModificationTest2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		String aoStatus = new String();
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		contractBudgetModificationService.mergeBudgetLineItemsForModification(moSession,aoTaskDetailsBean,aoStatus);
	}
	
	@Test
	public void mergeBudgetLineItemsForModificationTest3() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("12372");
		aoTaskDetailsBean.setBudgetId("11442");
		aoTaskDetailsBean.setUserId("system");
		String aoStatus = new String("86");
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenReturn(new String("1"));
		contractBudgetModificationService.mergeBudgetLineItemsForModification(moSession,aoTaskDetailsBean,aoStatus);
	}
	
	@Test
	public void testgenerateModificationBudgetcase1() throws ApplicationException
	{
	
		String lsDocId = null;
		String lsModificationBudgetId = null;
		String lsConvertedXml = null;
		
		HashMap<String, Object> loSubBudgetDetails = new HashMap<String, Object>();
		List<LineItemMasterBean> loLineItemList = new ArrayList<LineItemMasterBean>();
		MasterBean loMasterBean = new MasterBean();
		ContractBudgetModificationService loCBService = new ContractBudgetModificationService();
	
     	TaskDetailsBean loGridBean = new TaskDetailsBean();
		loGridBean.setContractId("11192");
		loGridBean.setBudgetId("10746");
		loGridBean.setUserId("system");
		contractBudgetModificationService.generateModificationBudget(moSession,loGridBean,loSubBudgetDetails);
	}
	@Test(expected = ApplicationException.class)
	public void testgenerateModificationBudgetcase2() throws ApplicationException
	{
		
		String lsDocId = null;
		String lsModificationBudgetId = null;
		String lsConvertedXml = null;
		
		HashMap<String, Object> loSubBudgetDetails = new HashMap<String, Object>();
		List<LineItemMasterBean> loLineItemList = new ArrayList<LineItemMasterBean>();
		MasterBean loMasterBean = new MasterBean();
		ContractBudgetModificationService loCBService = new ContractBudgetModificationService();
	
     	TaskDetailsBean loGridBean = new TaskDetailsBean();
		loGridBean.setContractId("11192");
		loGridBean.setBudgetId("10746");
		loGridBean.setUserId("system");
		
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		contractBudgetModificationService.generateModificationBudget(null,loGridBean,loSubBudgetDetails);
	}
	
	@Test(expected = ApplicationException.class)
	public void testgenerateModificationBudgetcase3() throws ApplicationException
	{
		
		String lsDocId = null;
		String lsModificationBudgetId = null;
		String lsConvertedXml = null;
		
		HashMap<String, Object> loSubBudgetDetails = new HashMap<String, Object>();
		List<LineItemMasterBean> loLineItemList = new ArrayList<LineItemMasterBean>();
		MasterBean loMasterBean = new MasterBean();
		ContractBudgetModificationService loCBService = new ContractBudgetModificationService();
	
     	TaskDetailsBean loGridBean = new TaskDetailsBean();
		loGridBean.setContractId("11192");
		loGridBean.setBudgetId("10746");
		loGridBean.setUserId("system");
		
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		contractBudgetModificationService.generateModificationBudget(null,loGridBean,loSubBudgetDetails);
	}
	
	@Test
	public void testgenerateModificationBudgetcase4() throws ApplicationException
	{
	
		String lsDocId = null;
		String lsModificationBudgetId = null;
		String lsConvertedXml = null;
		
		HashMap<String, Object> loSubBudgetDetails = new HashMap<String, Object>();
		List<LineItemMasterBean> loLineItemList = new ArrayList<LineItemMasterBean>();
		MasterBean loMasterBean = new MasterBean();
		ContractBudgetModificationService loCBService = new ContractBudgetModificationService();
	
     	TaskDetailsBean loGridBean = new TaskDetailsBean();
		loGridBean.setContractId("11192");
		loGridBean.setBudgetId("10746");
		loGridBean.setUserId("system");
		contractBudgetModificationService.generateModificationBudget(moSession,loGridBean,loSubBudgetDetails);
	}
	
	@Test
	public void getAutoApproverUserNameForAgencyTest1() throws ApplicationException
	{
		String lsUserName = null;
		
		String lsUserName1 = contractBudgetModificationService.getAutoApproverUserNameForAgency(moSession);
		equals(lsUserName1);
	}
	
	@Test(expected = ApplicationException.class)
	public void getAutoApproverUserNameForAgencyTest2() throws ApplicationException
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		contractBudgetModificationService.getAutoApproverUserNameForAgency(null);
	}
	
	@Test
	public void testfetchBudgetsForModificationAutoApprovalcase1() throws ApplicationException
	{
	
		
		boolean isModAutoApproved = true;
		List<String> loSubBudgetIdList = null;
		Boolean lbIsEligibleForAutoApproval = false;
		Integer loNewLineItemAdded = 0;
		ContractBudgetBean loContractBean = null;
		ContractBudgetBean loContractBeanForCostCenter = null;
		
		HashMap<String, Object> aoHMWFRequiredProps = new HashMap<String, Object>();
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "10746");
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID, "11192");
		
		HashMap<String, Object> loSubBudgetDetails = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapForBudgetUpdate = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapFordetails = null;
		
		String lsReasonForApproval = null;
		
		
		
		ContractBudgetModificationService loCBService = new ContractBudgetModificationService();
        contractBudgetModificationService.fetchBudgetsForModificationAutoApproval(moSession,aoHMWFRequiredProps);
	}
	
	@Test
	public void testfetchBudgetsForModificationAutoApprovalcase2() throws ApplicationException
	{
	
		
		boolean isModAutoApproved = true;
		List<String> loSubBudgetIdList = null;
		Boolean lbIsEligibleForAutoApproval = false;
		Integer loNewLineItemAdded = 0;
		ContractBudgetBean loContractBean = null;
		ContractBudgetBean loContractBeanForCostCenter = null;
		
		HashMap<String, Object> aoHMWFRequiredProps = new HashMap<String, Object>();
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "10746");
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID, "11192");
		
		HashMap<String, Object> loSubBudgetDetails = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapForBudgetUpdate = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapFordetails = null;
	
		String lsReasonForApproval = null;
		
		
		
		ContractBudgetModificationService loCBService = new ContractBudgetModificationService();
        contractBudgetModificationService.fetchBudgetsForModificationAutoApproval(null,aoHMWFRequiredProps);
	}
	
	@Test(expected = ApplicationException.class)
	public void testfetchBudgetsForModificationAutoApprovalcase3() throws ApplicationException
	{
	
		
		boolean isModAutoApproved = true;
		List<String> loSubBudgetIdList = null;
		Boolean lbIsEligibleForAutoApproval = false;
		Integer loNewLineItemAdded = 0;
		ContractBudgetBean loContractBean = null;
		ContractBudgetBean loContractBeanForCostCenter = null;
		
		HashMap<String, Object> aoHMWFRequiredProps = new HashMap<String, Object>();
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "10746");
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID, "11192");
		
		HashMap<String, Object> loSubBudgetDetails = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapForBudgetUpdate = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapFordetails = null;
		
		String lsReasonForApproval = null;
		
		
		
		ContractBudgetModificationService loCBService = new ContractBudgetModificationService();
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
        contractBudgetModificationService.fetchBudgetsForModificationAutoApproval(null,aoHMWFRequiredProps);
	}
	
	

	@Test(expected = ApplicationException.class)
	public void testfetchBudgetsForModificationAutoApprovalcase4() throws ApplicationException
	{
	
		
		boolean isModAutoApproved = true;
		List<String> loSubBudgetIdList = null;
		Boolean lbIsEligibleForAutoApproval = false;
		Integer loNewLineItemAdded = 0;
		ContractBudgetBean loContractBean = null;
		ContractBudgetBean loContractBeanForCostCenter = null;
		
		HashMap<String, Object> aoHMWFRequiredProps = new HashMap<String, Object>();
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "10746");
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID, "11192");
		
		HashMap<String, Object> loSubBudgetDetails = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapForBudgetUpdate = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapFordetails = null;
		
		String lsReasonForApproval = null;
		
		
		
		ContractBudgetModificationService loCBService = new ContractBudgetModificationService();
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
        contractBudgetModificationService.fetchBudgetsForModificationAutoApproval(null,aoHMWFRequiredProps);
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testfetchBudgetsForModificationAutoApprovalcase5() throws ApplicationException
	{
	
		
		boolean isModAutoApproved = true;
		List<String> loSubBudgetIdList = null;
		Boolean lbIsEligibleForAutoApproval = false;
		Integer loNewLineItemAdded = 0;
		ContractBudgetBean loContractBean = null;
		ContractBudgetBean loContractBeanForCostCenter = null;
		
		HashMap<String, Object> aoHMWFRequiredProps = new HashMap<String, Object>();
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "10746");
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID, "11192");
		
		HashMap<String, Object> loSubBudgetDetails = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapForBudgetUpdate = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapFordetails = null;
		
		String lsReasonForApproval = null;
		
		
		
		ContractBudgetModificationService loCBService = new ContractBudgetModificationService();
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
        contractBudgetModificationService.fetchBudgetsForModificationAutoApproval(null,aoHMWFRequiredProps);
	}
	
	
	@Test(expected = ApplicationException.class)
	public void testfetchBudgetsForModificationAutoApprovalcase6() throws ApplicationException
	{
	
		
		boolean isModAutoApproved = true;
		List<String> loSubBudgetIdList = null;
		Boolean lbIsEligibleForAutoApproval = false;
		Integer loNewLineItemAdded = 0;
		ContractBudgetBean loContractBean = null;
		ContractBudgetBean loContractBeanForCostCenter = null;
		
		HashMap<String, Object> aoHMWFRequiredProps = new HashMap<String, Object>();
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "10746");
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID, "11192");
		
		HashMap<String, Object> loSubBudgetDetails = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapForBudgetUpdate = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapFordetails = null;
		
		String lsReasonForApproval = null;
		
		
		
		ContractBudgetModificationService loCBService = new ContractBudgetModificationService();
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
        contractBudgetModificationService.fetchBudgetsForModificationAutoApproval(null,aoHMWFRequiredProps);
	}
	@Test(expected = ApplicationException.class)
	public void testfetchBudgetsForModificationAutoApprovalcase7() throws ApplicationException
	{
	
		
		boolean isModAutoApproved = true;
		List<String> loSubBudgetIdList = null;
		Boolean lbIsEligibleForAutoApproval = false;
		Integer loNewLineItemAdded = 0;
		ContractBudgetBean loContractBean = null;
		ContractBudgetBean loContractBeanForCostCenter = null;
		
		HashMap<String, Object> aoHMWFRequiredProps = new HashMap<String, Object>();
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "10746");
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID, "11192");
		
		HashMap<String, Object> loSubBudgetDetails = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapForBudgetUpdate = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapFordetails = null;
		
		String lsReasonForApproval = null;
		
		
		
		ContractBudgetModificationService loCBService = new ContractBudgetModificationService();
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(
				DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
						Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
        contractBudgetModificationService.fetchBudgetsForModificationAutoApproval(null,aoHMWFRequiredProps);
	}
	
	@Test
	public void testfetchBudgetsForModificationAutoApprovalcase8() throws ApplicationException
	{
	
		
		boolean isModAutoApproved = true;
		List<String> loSubBudgetIdList = null;
		Boolean lbIsEligibleForAutoApproval = false;
		Integer loNewLineItemAdded = 0;
		ContractBudgetBean loContractBean = null;
		ContractBudgetBean loContractBeanForCostCenter = null;
		
		HashMap<String, Object> aoHMWFRequiredProps = new HashMap<String, Object>();
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "10746");
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID, "11192");
		
		HashMap<String, Object> loSubBudgetDetails = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapForBudgetUpdate = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapFordetails = null;
	
		String lsReasonForApproval = null;
		
		
		
		ContractBudgetModificationService loCBService = new ContractBudgetModificationService();
        contractBudgetModificationService.fetchBudgetsForModificationAutoApproval(null,aoHMWFRequiredProps);
	}
	
	@Test
	public void testfetchBudgetsForModificationAutoApprovalcase9() throws ApplicationException
	{
	
		
		boolean isModAutoApproved = true;
		List<String> loSubBudgetIdList = null;
		Boolean lbIsEligibleForAutoApproval = false;
		Integer loNewLineItemAdded = 0;
		ContractBudgetBean loContractBean = null;
		ContractBudgetBean loContractBeanForCostCenter = null;
		
		HashMap<String, Object> aoHMWFRequiredProps = new HashMap<String, Object>();
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "10746");
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID, "11192");
		
		HashMap<String, Object> loSubBudgetDetails = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapForBudgetUpdate = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapFordetails = null;
	
		String lsReasonForApproval = null;
		
		
		
		ContractBudgetModificationService loCBService = new ContractBudgetModificationService();
        contractBudgetModificationService.fetchBudgetsForModificationAutoApproval(null,aoHMWFRequiredProps);
	}
	
	@Test
	public void testfetchBudgetsForModificationAutoApprovalcase10() throws ApplicationException
	{
	
		
		boolean isModAutoApproved = true;
		List<String> loSubBudgetIdList = null;
		Boolean lbIsEligibleForAutoApproval = false;
		Integer loNewLineItemAdded = 0;
		ContractBudgetBean loContractBean = null;
		ContractBudgetBean loContractBeanForCostCenter = null;
		
		HashMap<String, Object> aoHMWFRequiredProps = new HashMap<String, Object>();
		aoHMWFRequiredProps.put(HHSConstants.BUDGET_ID_WORKFLOW, "10746");
		aoHMWFRequiredProps.put(HHSConstants.CONTRACT_ID, "11192");
		
		HashMap<String, Object> loSubBudgetDetails = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapForBudgetUpdate = new HashMap<String, Object>();
		HashMap<String, Object> loHashMapFordetails = null;
	
		String lsReasonForApproval = null;
		
		
		
		ContractBudgetModificationService loCBService = new ContractBudgetModificationService();
        contractBudgetModificationService.fetchBudgetsForModificationAutoApproval(null,aoHMWFRequiredProps);
	}
}


