package com.nyc.hhs.daomanager.servicetestR7;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.xmlsoap.schemas.soap.encoding.Int;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.ContractBudgetModificationService;
import com.nyc.hhs.daomanager.service.ContractBudgetService;
import com.nyc.hhs.daomanager.service.InvoiceService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBProgramIncomeBean;
import com.nyc.hhs.model.CBServicesBean;
import com.nyc.hhs.model.LineItemMasterBean;
import com.nyc.hhs.model.MasterBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;

public class ContractBudgetServiceTestR7
{

	ContractBudgetService contractBudgetService = new ContractBudgetService();
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
	public void testFetchServiceData1() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setContractBudgetID("10936");
		aoCBGridBean.setSubBudgetID("14454");
		List<CBServicesBean> loServiceData = loContractBudgetService.fetchServiceData(moSession, aoCBGridBean);
		assertNotNull(loServiceData);
	}

	@Test
	public void testFetchServiceData2() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setContractBudgetID("");
		aoCBGridBean.setSubBudgetID("");
		List<CBServicesBean> loServiceData = loContractBudgetService.fetchServiceData(moSession, aoCBGridBean);
		assertNotNull(loServiceData);
	}
	

	@Test(expected = ApplicationException.class)
	public void testFetchServiceData3() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setContractBudgetID("10936");
		aoCBGridBean.setSubBudgetID("14454");
			List<CBServicesBean> loServiceData = loContractBudgetService.fetchServiceData(null, aoCBGridBean);
	}
	
	@Test
	public void testFetchCostCenter1() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setContractBudgetID("10936");
		aoCBGridBean.setSubBudgetID("14454");
		List<CBServicesBean> loCostData = loContractBudgetService.fetchCostCenter(moSession, aoCBGridBean);
		assertNotNull(loCostData);
	}

	@Test
	public void testFetchCostCenter2() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setContractBudgetID("");
		aoCBGridBean.setSubBudgetID("");
		List<CBServicesBean> loCostData = loContractBudgetService.fetchCostCenter(moSession, aoCBGridBean);
		assertNotNull(loCostData);
	}
	

	@Test(expected = ApplicationException.class)
	public void testFetchCostCenter3() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setContractBudgetID("10936");
		aoCBGridBean.setSubBudgetID("14454");
			List<CBServicesBean> loCostData = loContractBudgetService.fetchCostCenter(null, aoCBGridBean);
	}
	
	@Test
	public void testEditServicesDetails1() throws ApplicationException
	{
		boolean loThrown = false;
		boolean lbUpdateStatus = false;
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBServicesBean aoCBServices = new CBServicesBean();
		aoCBServices.setFyBudget("10000");
		aoCBServices.setUnits("10");
		aoCBServices.setModifyByProvider("3273");
		aoCBServices.setServicesDetailId("37");
		aoCBServices.setId("37");
		try
		{
			boolean lbStatus = loContractBudgetService.editServicesDetails(aoCBServices, moSession);
			assertTrue(lbStatus);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testEditServicesDetails2() throws ApplicationException
	{
		boolean loThrown = false;
		boolean lbUpdateStatus = false;
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBServicesBean aoCBServices = new CBServicesBean();
		aoCBServices.setFyBudget("10000");
		aoCBServices.setUnits("10");
		aoCBServices.setModifyByProvider("3273");
		aoCBServices.setServicesDetailId("");
		aoCBServices.setId("");
		try
		{
			boolean lbStatus = loContractBudgetService.editServicesDetails(aoCBServices, moSession);
			assertTrue(lbStatus);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test(expected=ApplicationException.class)
	public void testEditServicesDetails3() throws ApplicationException
	{
		String lsBudgetId = "11602";
		CBServicesBean aoCBServices = new CBServicesBean();
		boolean lbUpdateStatus = false;
		boolean lbStatus = contractBudgetService.editServicesDetails(aoCBServices, null);
	}
	
	@Test
	public void testEditCostCenterDetails1() throws ApplicationException
	{
		boolean loThrown = false;
		boolean lbUpdateStatus = false;
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBServicesBean aoCBServices = new CBServicesBean();
		aoCBServices.setFyBudget("10000");
		aoCBServices.setModifyByProvider("3273");
		aoCBServices.setCostCenterDetailId("61");
		aoCBServices.setId("61");
		try
		{
			boolean lbStatus = loContractBudgetService.editCostCenterDetails(aoCBServices, moSession);
			assertTrue(lbStatus);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test
	public void testEditCostCenterDetails2() throws ApplicationException
	{
		boolean loThrown = false;
		boolean lbUpdateStatus = false;
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBServicesBean aoCBServices = new CBServicesBean();
		aoCBServices.setFyBudget("10000");
		aoCBServices.setModifyByProvider("3273");
		aoCBServices.setCostCenterDetailId("");
		aoCBServices.setId("");
		try
		{
			boolean lbStatus = loContractBudgetService.editCostCenterDetails(aoCBServices, moSession);
			assertTrue(lbStatus);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test(expected=ApplicationException.class)
	public void testEditCostCenterDetails3() throws ApplicationException
	{
		String lsBudgetId = "11602";
		CBServicesBean aoCBServices = new CBServicesBean();
		boolean lbUpdateStatus = false;
		boolean lbStatus = contractBudgetService.editCostCenterDetails(aoCBServices, null);
	}
	
	@Test(expected=ApplicationException.class)
	public void testValidateServicesAmount1() throws ApplicationException
	{
		String lsBudgetId = "11602";
		HashMap loMAp = (HashMap) contractBudgetService.validateServicesAmount(null, lsBudgetId);
	}
	
	@Test(expected=ApplicationException.class)
	public void testValidateServicesAmount2() throws ApplicationException
	{
		String lsBudgetId = "11602";
		HashMap loMAp = (HashMap) contractBudgetService.validateServicesAmount(null,lsBudgetId);
	}
	@Test
	public void testValidateServicesAmount3() throws ApplicationException
	{
		String lsBudgetId = "11602";
		HashMap loMAp = (HashMap) contractBudgetService.validateServicesAmount(moSession, lsBudgetId);
		assertTrue(loMAp!=null);
	}
	
	@Test
	public void testValidateServicesAmount4() throws ApplicationException
	{
		String lsBudgetId = "11602";
		List<CBGridBean> loSubBudgetList=null;
		HashMap loMAp = (HashMap) contractBudgetService.validateServicesAmount(moSession, lsBudgetId);
		assertTrue(loMAp!=null);
	}
	
	@Test
	public void testValidateServicesAmount5() throws ApplicationException
	{
		String lsBudgetId = "11602";
		List<CBGridBean> loSubBudgetList=null;
		String loBugetAmount = "1000";
		String loUpdatedAmount = "1200" ;
		HashMap loMAp = (HashMap) contractBudgetService.validateServicesAmount(moSession, lsBudgetId);
		assertTrue(loMAp!=null);
	}
	
	@Test
	public void testValidateServicesAmount6() throws ApplicationException
	{
		String lsBudgetId = "10792";
		List<CBGridBean> loSubBudgetList=null;
		String loBugetAmount = "1000";
		String loUpdatedAmount = "1200" ;
		HashMap loMAp = (HashMap) contractBudgetService.validateServicesAmount(moSession, lsBudgetId);
		assertTrue(loMAp!=null);
	}
	@Test
	public void testValidateServicesAmount7() throws ApplicationException
	{
		String lsBudgetId = "10734";
		HashMap loMAp = (HashMap) contractBudgetService.validateServicesAmount(moSession, 
				lsBudgetId);
		assertTrue(loMAp!=null);
	}
	
	@Test
	public void testFetchSources1() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBGridBean aoCBGridBean = new CBGridBean();
		String loSourceList = loContractBudgetService.fetchSources(moSession, aoCBGridBean);
	}

	@Test
	public void testFetchSources2() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBGridBean aoCBGridBean = new CBGridBean();
		String loSourceList =loContractBudgetService.fetchSources(moSession, null);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchSources3() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBGridBean aoCBGridBean = new CBGridBean();
		String loSourceList =loContractBudgetService.fetchSources(null, aoCBGridBean);
	}
	

	@Test(expected = ApplicationException.class)
	public void testFetchSources4() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBGridBean aoCBGridBean = new CBGridBean();
		String loSourceList = loContractBudgetService.fetchSources(null, null);
	}
	@Test
	public void testDeleteProgramIncome() throws ApplicationException
	{
		boolean loThrown = false;
		boolean lbDeletePI = false;
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBProgramIncomeBean aoCBProgramIncomeBean = new CBProgramIncomeBean();
		aoCBProgramIncomeBean.setProgramIncomeId("56832");
		try
		{
			boolean liStatus =  loContractBudgetService.deleteProgramIncome(moSession, aoCBProgramIncomeBean);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}
	@Test
	public void testDeleteProgramIncome1() throws ApplicationException
	{
		boolean loThrown = false;
		boolean lbDeletePI = true;
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBProgramIncomeBean aoCBProgramIncomeBean = new CBProgramIncomeBean();
		aoCBProgramIncomeBean.setProgramIncomeId("56779");
		try
		{
			boolean liStatus =  loContractBudgetService.deleteProgramIncome(moSession, aoCBProgramIncomeBean);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}
	
	@Test
	public void testDeleteProgramIncome2() throws ApplicationException
	{
		boolean loThrown = false;
		boolean lbDeletePI = true;
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBProgramIncomeBean aoCBProgramIncomeBean = new CBProgramIncomeBean();
		aoCBProgramIncomeBean.setProgramIncomeId("56832");
		try
		{
			boolean liStatus =  loContractBudgetService.deleteProgramIncome(moSession, aoCBProgramIncomeBean);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test(expected=ApplicationException.class)
	public void testDeleteProgramIncome3() throws ApplicationException
	{
		CBProgramIncomeBean aoCBProgramIncomeBean = new CBProgramIncomeBean();
		boolean lbUpdateStatus = false;
		boolean lbStatus = contractBudgetService.deleteProgramIncome(null, aoCBProgramIncomeBean);
	}
	
	@Test
	public void testAddProgramIncome1() throws ApplicationException
	{
		boolean loThrown = false;
		boolean lbInsertStatus = false;
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBProgramIncomeBean aoCBProgramIncomeBean = new CBProgramIncomeBean();
		aoCBProgramIncomeBean.setProgramIncomeId("56779");
		aoCBProgramIncomeBean.setContractBudgetID("10730");
		aoCBProgramIncomeBean.setSubBudgetID("14124");
		aoCBProgramIncomeBean.setEmpPosition("1");
		aoCBProgramIncomeBean.setAmendmentAmount("200");
		aoCBProgramIncomeBean.setCreatedByUserId("agency_14");
		aoCBProgramIncomeBean.setModifiedByUserId("system");
	    aoCBProgramIncomeBean.setActiveFlag("1");
	    aoCBProgramIncomeBean.setDescription("");
	    aoCBProgramIncomeBean.setEntryTypeId("10");
		try
		{
			 lbInsertStatus =  loContractBudgetService.addProgramIncome(moSession, aoCBProgramIncomeBean);
			assertTrue(lbInsertStatus);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}
	
	@Test
	public void testAddProgramIncome2() throws ApplicationException
	{
		boolean loThrown = false;
		boolean lbInsertStatus = true;
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBProgramIncomeBean aoCBProgramIncomeBean = new CBProgramIncomeBean();
		aoCBProgramIncomeBean.setProgramIncomeId("56779");
		aoCBProgramIncomeBean.setContractBudgetID("10730");
		aoCBProgramIncomeBean.setSubBudgetID("14124");
		aoCBProgramIncomeBean.setEmpPosition("1");
		aoCBProgramIncomeBean.setAmendmentAmount("200");
		aoCBProgramIncomeBean.setCreatedByUserId("agency_14");
		aoCBProgramIncomeBean.setModifiedByUserId("system");
	    aoCBProgramIncomeBean.setActiveFlag("1");
	    aoCBProgramIncomeBean.setDescription("");
	    aoCBProgramIncomeBean.setEntryTypeId("10");
		try
		{
			 lbInsertStatus =  loContractBudgetService.addProgramIncome(moSession, aoCBProgramIncomeBean);
			assertTrue(lbInsertStatus);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test(expected=ApplicationException.class)
	public void testAddProgramIncome3() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		CBProgramIncomeBean aoCBProgramIncomeBean = new CBProgramIncomeBean();
		boolean lbInsertStatus = false;
		boolean lbStatus = loContractBudgetService.addProgramIncome(null, aoCBProgramIncomeBean);
	}
	
	@Test
	public void testDeleteDefaultPIForNewConf() throws ApplicationException
	{
		boolean loThrown = false;
		boolean aoFinalFininsh = true;
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setEntityId("11180");
		aoTaskDetailsBean.setAssignedTo("city");
		aoTaskDetailsBean.setStartFiscalYear("2018");
		aoTaskDetailsBean.setContractId("11180");
		try
		{
			boolean liStatus =  loContractBudgetService.deleteDefaultPIForNewConf(moSession, aoTaskDetailsBean,aoFinalFininsh);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}
	
	@Test
	public void testDeleteDefaultPIForNewConf1() throws ApplicationException
	{
		boolean loThrown = false;
		boolean aoFinalFininsh = true;
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setEntityId("11180");
		aoTaskDetailsBean.setAssignedTo("city");
		aoTaskDetailsBean.setStartFiscalYear("2017");
		aoTaskDetailsBean.setContractId("11180");
		
		
		try
		{
			boolean liStatus =  loContractBudgetService.deleteDefaultPIForNewConf(moSession, aoTaskDetailsBean,aoFinalFininsh);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}
	
	@Test
	public void testDeleteDefaultPIForNewConf2() throws ApplicationException
	{
		boolean loThrown = false;
		boolean aoFinalFininsh = true;
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		try
		{
			boolean liStatus =  loContractBudgetService.deleteDefaultPIForNewConf(moSession, aoTaskDetailsBean,aoFinalFininsh);
		}
		catch (ApplicationException aoAppEx)
		{
			loThrown = true;
			assertEquals("Error occured while executing service: ", aoAppEx.getMessage());
			assertTrue(loThrown);
		}
	}

	@Test(expected=ApplicationException.class)
	public void testDeleteDefaultPIForNewConf3() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		boolean aoFinalFininsh = true;
		boolean liStatus =  loContractBudgetService.deleteDefaultPIForNewConf(null, aoTaskDetailsBean,aoFinalFininsh);
	}
	
	@Test
	public void testUpdateIsOldPiFlagForNewFyFinish() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("12854");
		aoTaskDetailsBean.setStartFiscalYear("2018");
		loContractBudgetService.updateIsOldPiFlagForNewFyFinish(aoTaskDetailsBean, moSession);
		
	}
	@Test(expected = ApplicationException.class)
	public void testUpdateIsOldPiFlagForNewFyFinish2() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("12854");
		aoTaskDetailsBean.setStartFiscalYear("2018");
		loContractBudgetService.updateIsOldPiFlagForNewFyFinish(aoTaskDetailsBean, null);
	}
	
	@Test
    public void testupdatePIIndirectRatePercentagecase1() throws ApplicationException
    {
          ContractBudgetService loContractBudgetService = new ContractBudgetService();
          CBGridBean loCBGridBean = new CBGridBean();
          MasterBean loMasterBean = new MasterBean();
          List<LineItemMasterBean> masterBeanList = new ArrayList<LineItemMasterBean>();
          LineItemMasterBean loLineItemBean = new LineItemMasterBean();
          
          
         
          
          loCBGridBean.setParentBudgetId("11620");
          loCBGridBean.setParentSubBudgetId("15481");
          loCBGridBean.setBudgetStatusId("86");
          loMasterBean.setBudgetId("11620");
          loCBGridBean.setSubBudgetID("15481");
          loCBGridBean.setEntryTypeId("10");
          loCBGridBean.setBudgetTypeId("1");
          masterBeanList.add(loLineItemBean);
          loMasterBean.setMasterBeanList(masterBeanList);
          loContractBudgetService.updatePIIndirectRatePercentage(moSession, loCBGridBean,loMasterBean);
                
    }
	 @Test
	 public void testupdatePIIndirectRatePercentagecase2() throws ApplicationException
	    {
	          ContractBudgetService loContractBudgetService = new ContractBudgetService();
	          CBGridBean loCBGridBean = new CBGridBean();
	          MasterBean loMasterBean = new MasterBean();
	          List<LineItemMasterBean> masterBeanList = new ArrayList<LineItemMasterBean>();
	          
	          LineItemMasterBean loLineItemBean = new LineItemMasterBean();
	        
	         
	          loCBGridBean.setParentBudgetId("10730");
	          loCBGridBean.setParentSubBudgetId("15481");
	          loCBGridBean.setBudgetStatusId("86");
	          loMasterBean.setBudgetId("10730");
	          loCBGridBean.setSubBudgetID("15481");
	          loCBGridBean.setEntryTypeId("10");
	          loCBGridBean.setBudgetTypeId("2");
	          masterBeanList.add(loLineItemBean);
	        
	          loMasterBean.setMasterBeanList(masterBeanList);
	          
	          loContractBudgetService.updatePIIndirectRatePercentage(moSession, loCBGridBean,loMasterBean);
	                
	    }
	 
	 
	 public void testupdatePIIndirectRatePercentagecase3() throws ApplicationException
	    {
	          ContractBudgetService loContractBudgetService = new ContractBudgetService();
	          CBGridBean loCBGridBean = new CBGridBean();
	          MasterBean loMasterBean = new MasterBean();
	          List<LineItemMasterBean> masterBeanList = new ArrayList<LineItemMasterBean>();
	          
	          LineItemMasterBean loLineItemBean = new LineItemMasterBean();
	        
	         
	         
	         
	          loMasterBean.setBudgetId("11567");
	          loCBGridBean.setContractBudgetID("11567");
	          loCBGridBean.setIsOldPI("1");
	          loCBGridBean.setBudgetTypeId("2");
	          masterBeanList.add(loLineItemBean);
	        
	          loMasterBean.setMasterBeanList(masterBeanList);
	          
	          loContractBudgetService.updatePIIndirectRatePercentage(moSession, loCBGridBean,loMasterBean);
	                
	    }
	  
	 @Test(expected = ApplicationException.class)
	    public void testupdatePIIndirectRatePercentagecase4() throws ApplicationException 
	    {
		 ContractBudgetService loContractBudgetService = new ContractBudgetService();
	          CBGridBean loCBGridBean = new CBGridBean();
	          MasterBean loMasterBean = new MasterBean();
	          loCBGridBean.setSubBudgetID(null);
	          loCBGridBean.setBudgetTypeId(null);
	          loCBGridBean.setContractBudgetID(null);// Invalid Sub Budget id
	          loContractBudgetService.updatePIIndirectRatePercentage(moSession, loCBGridBean,loMasterBean);

	    }
		
		 @Test(expected = ApplicationException.class)
		    public void testupdatePIIndirectRatePercentagecase5() throws ApplicationException
		    {
			 ContractBudgetService loContractBudgetService = new ContractBudgetService();
		          CBGridBean loCBGridBean = new CBGridBean();     
		          MasterBean loMasterBean = new MasterBean();
		          loContractBudgetService.updatePIIndirectRatePercentage(null, loCBGridBean,loMasterBean);
		    }
		 
		 @Test(expected = Exception.class)
		    public void testupdatePIIndirectRatePercentagecase6() throws ApplicationException
		    {
			 ContractBudgetService loContractBudgetService = new ContractBudgetService();
		          CBGridBean loCBGridBean = new CBGridBean();     
		          MasterBean loMasterBean = new MasterBean();
		          loContractBudgetService.updatePIIndirectRatePercentage(null, loCBGridBean,loMasterBean);
	
		   }
		 
		 
			@Test
		    public void testvalidateAndDeleteDefaultPIcase1() throws ApplicationException
		    {
		          ContractBudgetService loContractBudgetService = new ContractBudgetService();
		          
		          TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		          
		          Boolean aoFinalFininsh=true;
		          
		          aoTaskDetailsBean.setContractId("11180");
		          aoTaskDetailsBean.setAssignedToUserName("city_459");
		         
		         
		          loContractBudgetService.validateAndDeleteDefaultPI(moSession, aoTaskDetailsBean,aoFinalFininsh);
		                
		    }
			
			@Test
		    public void testvalidateAndDeleteDefaultPIcase2() throws ApplicationException
		    {
		          ContractBudgetService loContractBudgetService = new ContractBudgetService();
		          
		          TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		          
		          Boolean aoFinalFininsh=false;
		          
		          aoTaskDetailsBean.setContractId("11180");
		          aoTaskDetailsBean.setAssignedToUserName("city_459");
		          
		          
		         
		         
		          loContractBudgetService.validateAndDeleteDefaultPI(moSession, aoTaskDetailsBean,aoFinalFininsh);
		                
		    }
			
			
			@Test
		    public void testvalidateAndDeleteDefaultPIcase3() throws ApplicationException
		    {
		          ContractBudgetService loContractBudgetService = new ContractBudgetService();
		          
		          TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		          
		          Boolean aoFinalFininsh=true;
		          
		          aoTaskDetailsBean.setContractId("11180");
		          aoTaskDetailsBean.setAssignedToUserName("city_459");
		          
		          
		         
		         
		          loContractBudgetService.validateAndDeleteDefaultPI(moSession, aoTaskDetailsBean,aoFinalFininsh);
		                
		    }
			

			@Test
		    public void testvalidateAndDeleteDefaultPIcase6() throws ApplicationException
		    {
		          ContractBudgetService loContractBudgetService = new ContractBudgetService();
		          
		          TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		          
		          Boolean aoFinalFininsh=true;
		          
		          aoTaskDetailsBean.setContractId("11180");
		          aoTaskDetailsBean.setAssignedToUserName("city_459");
		          
		          
		         
		         
		          loContractBudgetService.validateAndDeleteDefaultPI(moSession, aoTaskDetailsBean,aoFinalFininsh);
		                
		    }
			
			@Test(expected = ApplicationException.class)
		    public void testvalidateAndDeleteDefaultPIcase4() throws ApplicationException
		    {
				ContractBudgetService loContractBudgetService = new ContractBudgetService();
				 TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				 Boolean aoFinalFininsh=true;
		          loContractBudgetService.validateAndDeleteDefaultPI(null, aoTaskDetailsBean,aoFinalFininsh);
		    }
			
			@Test(expected = Exception.class)
		    public void testvalidateAndDeleteDefaultPIcase5() throws ApplicationException
		    {
				ContractBudgetService loContractBudgetService = new ContractBudgetService();
				 TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				 Boolean aoFinalFininsh=true;
		          loContractBudgetService.validateAndDeleteDefaultPI(null, aoTaskDetailsBean,aoFinalFininsh);
		    }
			
			
			@Test(expected = Exception.class)
		    public void testvalidateAndDeleteDefaultPIcase7() throws ApplicationException
		    {
				ContractBudgetService loContractBudgetService = new ContractBudgetService();
				 TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				 Boolean aoFinalFininsh=false;
		          loContractBudgetService.validateAndDeleteDefaultPI(null, aoTaskDetailsBean,aoFinalFininsh);
		    }
			
			@Test
		    public void testvalidateAndDeleteDefaultPIcase8() throws ApplicationException
		    {
		          ContractBudgetService loContractBudgetService = new ContractBudgetService();
		          
		          TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		          
		          Boolean aoFinalFininsh=true;
		          
		          aoTaskDetailsBean.setContractId("11181");
		          aoTaskDetailsBean.setAssignedToUserName("city_459");
		          
		          
		         
		         
		          loContractBudgetService.validateAndDeleteDefaultPI(moSession, aoTaskDetailsBean,aoFinalFininsh);
		                
		    }
			
			@Test(expected = ApplicationException.class)
			public void insertServiceAndCostCenterTest1() throws ApplicationException
			{
				TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				aoTaskDetailsBean.setContractId("12372");
				aoTaskDetailsBean.setStartFiscalYear("2017");
				contractBudgetService.insertServiceAndCostCenter(aoTaskDetailsBean,null);
			}
			
			@Test(expected = Exception.class)
			public void insertServiceAndCostCenterTest2() throws ApplicationException
			{
				TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				aoTaskDetailsBean.setContractId("12372");
				aoTaskDetailsBean.setStartFiscalYear("2017");
				PowerMockito.mockStatic(DAOUtil.class);
				PowerMockito.when(
						DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class),
								Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
				contractBudgetService.insertServiceAndCostCenter(aoTaskDetailsBean,moSession);
			}
			
			@Test
			public void insertServiceAndCostCenterTest3() throws ApplicationException
			{
				TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				CBGridBean aoCBGridBean =new CBGridBean();
				aoTaskDetailsBean.setContractId("12372");
				aoTaskDetailsBean.setStartFiscalYear("2017");
				aoTaskDetailsBean.setEventName("Update Services");
				aoTaskDetailsBean.setUserId("city_459");
				aoCBGridBean.setSubBudgetID("11620");
				aoCBGridBean.setContractBudgetID("15482");
				
				contractBudgetService.insertServiceAndCostCenter(aoTaskDetailsBean,moSession);
			}
			
			@Test
			public void insertServiceAndCostCenterTest4() throws ApplicationException
			{
				TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				CBGridBean aoCBGridBean =new CBGridBean();
				aoTaskDetailsBean.setContractId("12372");
				aoTaskDetailsBean.setStartFiscalYear("2017");
				aoCBGridBean.setNewRecord("1");
				contractBudgetService.insertServiceAndCostCenter(aoTaskDetailsBean,moSession);
			}
			
			@Test
			public void insertServiceAndCostCenterTest5() throws ApplicationException
			{
				TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				CBGridBean aoCBGridBean =new CBGridBean();
				aoTaskDetailsBean.setContractId("12372");
				aoTaskDetailsBean.setStartFiscalYear("2017");
				aoCBGridBean.setNewRecord("1");
				aoTaskDetailsBean.setUserId("city_459");
				aoCBGridBean.setSubBudgetID("11620");
				aoCBGridBean.setContractBudgetID("15482");
				contractBudgetService.insertServiceAndCostCenter(aoTaskDetailsBean,moSession);
			}
			
			@Test
			public void insertServiceAndCostCenterTest6() throws ApplicationException
			{
				TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				CBGridBean aoCBGridBean =new CBGridBean();
				aoTaskDetailsBean.setContractId("12372");
				aoCBGridBean.setSubBudgetID("11620");
				aoCBGridBean.setContractBudgetID("15482");
				contractBudgetService.insertServiceAndCostCenter(aoTaskDetailsBean,moSession);
			}
			
			@Test
			public void insertServiceAndCostCenterTest7() throws ApplicationException
			{
				TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				CBGridBean aoCBGridBean =new CBGridBean();
				aoTaskDetailsBean.setContractId("12372");
				aoTaskDetailsBean.setStartFiscalYear("2018");
				aoCBGridBean.setNewRecord("1");
				aoTaskDetailsBean.setUserId("city_459");
				aoCBGridBean.setSubBudgetID("11620");
				aoCBGridBean.setContractBudgetID("15482");
				contractBudgetService.insertServiceAndCostCenter(aoTaskDetailsBean,moSession);
			}
			
			@Test
			public void insertServiceAndCostCenterTest8() throws ApplicationException
			{
				TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				CBGridBean aoCBGridBean =new CBGridBean();
				aoTaskDetailsBean.setContractId("12372");
				aoTaskDetailsBean.setStartFiscalYear("2018");
				aoTaskDetailsBean.setTaskName("New Fiscal Year Configuration");
				aoCBGridBean.setNewRecord("1");
				aoTaskDetailsBean.setUserId("city_459");
				aoCBGridBean.setSubBudgetID("11620");
				aoCBGridBean.setContractBudgetID("15482");
				contractBudgetService.insertServiceAndCostCenter(aoTaskDetailsBean,moSession);
			}
			
			@Test
			public void insertServiceAndCostCenterTest9() throws ApplicationException
			{
				TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
				CBGridBean aoCBGridBean =new CBGridBean();
				aoTaskDetailsBean.setContractId("12372");
				aoTaskDetailsBean.setStartFiscalYear("2018");
				aoTaskDetailsBean.setEventName("Update Services");
				aoCBGridBean.setNewRecord("1");
				aoTaskDetailsBean.setUserId("city_459");
				aoCBGridBean.setSubBudgetID("11620");
				aoCBGridBean.setContractBudgetID("15482");
				contractBudgetService.insertServiceAndCostCenter(aoTaskDetailsBean,moSession);
			}
	
}
