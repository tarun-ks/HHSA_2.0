package com.nyc.hhs.daomanager.servicetestR7;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
import com.nyc.hhs.contractsbatch.service.ContractsBatchService;
import com.nyc.hhs.daomanager.service.FinancialsService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DAOUtil.class)
public class FinancialsServiceTestR7 
{

	FinancialsService financialsService=new FinancialsService();
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
			
			  lbThrown = true; assertTrue("Exception thrown", lbThrown);
			 
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
		
		 catch (Exception loEx) { lbThrown = true;
		 assertTrue("Exception thrown", lbThrown); }
		 
		finally
		{
			
			 moSession.rollback(); moSession.close();
			 
		}
	}
	
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
	public void updateRequestMRCase1() throws ApplicationException
	
	{
		
		boolean lbflag =financialsService.updateRequestMRFlag(null, "11167");
		assertTrue(lbflag);
		
		
	}
	
	@Test(expected = ApplicationException.class)
	public void updateRequestMRCase2() throws ApplicationException
	{
		financialsService.updateRequestMRFlag(null, "11166");
	}
	
	
	@Test(expected = ApplicationException.class)
	public void updateRequestMRCase3() throws ApplicationException{
		
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		boolean lbflag=financialsService.updateRequestMRFlag(moSession, "11167");
		assertTrue(lbflag);
		
	}
	
	@Test
	public void updateRequestMRCase4() throws ApplicationException{
	
		boolean lbflag=financialsService.updateRequestMRFlag(moSession, "11167");
		assertTrue(lbflag);
		
	}
	
	@Test
	public void updateBudgetEndDateTest() throws ApplicationException, ParseException
	{
		List<ContractBean> aoContractBeanList= new ArrayList<ContractBean>();
		ContractBean loContractBean = new ContractBean();
		loContractBean.setContractId("12778");
		loContractBean.setParentContractId("12778");
		
		DateFormat loDf = new SimpleDateFormat("MM/dd/yyyy"); 
		Date loContractEndDate = loDf.parse("01/01/2018");
		Date loAmendContractEndDate = loDf.parse("01/09/2018");;
		loContractBean.setContractEndDate(loContractEndDate);
		loContractBean.setParentContractEndDate(loAmendContractEndDate);
		
		aoContractBeanList.add(loContractBean);
		boolean lbflag=financialsService.updateBudgetEndDate(moSession,aoContractBeanList );
		assertTrue(lbflag);
		
	}
	
	@Test
	public void updateBudgetEndDateTest2() throws ApplicationException, ParseException
	{
		List<ContractBean> aoContractBeanList= new ArrayList<ContractBean>();
		ContractBean loContractBean = new ContractBean();
		loContractBean.setContractId("12779");
		loContractBean.setParentContractId("12779");
	
		DateFormat loDf = new SimpleDateFormat("MM/dd/yyyy"); 
		Date loContractEndDate = loDf.parse("09/01/2018");
		Date loAmendContractEndDate = loDf.parse("15/09/2018");;
		loContractBean.setContractEndDate(loContractEndDate);
		loContractBean.setParentContractEndDate(loAmendContractEndDate);
		
		aoContractBeanList.add(loContractBean);
		boolean lbflag=financialsService.updateBudgetEndDate(moSession,aoContractBeanList );
		assertTrue(lbflag);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void updateBudgetEndDateTest3() throws ApplicationException, ParseException
	{
		List<ContractBean> aoContractBeanList= new ArrayList<ContractBean>();
		ContractBean loContractBean = new ContractBean();
		aoContractBeanList.add(loContractBean);
		boolean lbflag=financialsService.updateBudgetEndDate(moSession,aoContractBeanList );
		assertFalse(lbflag);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void updateBudgetEndDateTest4() throws ApplicationException, ParseException
	{
		List<ContractBean> aoContractBeanList= new ArrayList<ContractBean>();
		ContractBean loContractBean = new ContractBean();
		aoContractBeanList.add(loContractBean);
		boolean lbflag=financialsService.updateBudgetEndDate(null,aoContractBeanList );
		assertFalse(lbflag);
		
	}
	
	@Test
	public void updateServiceListDetailstest1() throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.FISCAL_YEAR_ID, "2018");
		loHashMap.put(HHSConstants.CONTRACT_ID, "4");
		loHashMap.put(HHSConstants.BUDGET_ID, "4");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		Boolean lostatus = financialsService.updateBudgetTemplate(moSession, loHashMap);
		
	}

	
	@Test
	public void updateServiceListDetailstest2() throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.FISCAL_YEAR_ID, "2017");
		loHashMap.put(HHSConstants.CONTRACT_ID, "11166");
		loHashMap.put(HHSConstants.BUDGET_ID, "10730");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		Boolean lostatus = financialsService.updateBudgetTemplate(moSession, loHashMap);
		
	}
	
	
	@Test(expected = ApplicationException.class)
	public void updateServiceListDetailstest3() throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.FISCAL_YEAR_ID, "2018");
		loHashMap.put(HHSConstants.CONTRACT_ID, "100");
		loHashMap.put(HHSConstants.BUDGET_ID, "100");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		Boolean lostatus = financialsService.updateBudgetTemplate(moSession, loHashMap);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void updateServiceListDetailstest4() throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.FISCAL_YEAR_ID, "2018");
		loHashMap.put(HHSConstants.CONTRACT_ID, "100");
		loHashMap.put(HHSConstants.BUDGET_ID, "100");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		Boolean lostatus = financialsService.updateBudgetTemplate(null, loHashMap);
		
	}

	@Test(expected = ApplicationException.class)
	public void updateServiceListDetailstest5() throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.FISCAL_YEAR_ID, "2018");
		loHashMap.put(HHSConstants.CONTRACT_ID, "100");
		loHashMap.put(HHSConstants.BUDGET_ID, "100");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		Boolean lostatus = financialsService.updateBudgetTemplate(null, null);
		
	}

	@Test
	public void updateCostCenterEnabledtest1() throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String, String>();
		
		loHashMap.put(HHSConstants.CONTRACT_ID, "11217");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_14");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		Boolean lostatus = financialsService.updateBudgetTemplate(moSession, loHashMap);
		
	}
	
	@Test
	public void updateCostCenterEnabledtest2() throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String, String>();
		
		loHashMap.put(HHSConstants.CONTRACT_ID, "11217");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_14");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		Boolean lostatus = financialsService.updateBudgetTemplate(moSession, loHashMap);
		
	}
	
	@Test
	public void updateCostCenterEnabledtest3() throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String, String>();
		
		loHashMap.put(HHSConstants.CONTRACT_ID, "11217");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_14");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		Boolean lostatus = financialsService.updateBudgetTemplate(moSession, loHashMap);
		
	}

	@Test(expected = ApplicationException.class)
	public void updateCostCenterEnabledtest4() throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, "11217");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_14");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		Boolean lostatus = financialsService.updateBudgetTemplate(moSession, loHashMap);
		
	}

	@Test(expected = ApplicationException.class)
	public void updateCostCenterEnabledtest5() throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, "11217");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_14");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		Boolean lostatus = financialsService.updateBudgetTemplate(null, loHashMap);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void updateCostCenterEnabledtest6() throws ApplicationException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, "11217");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_14");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		Boolean lostatus = financialsService.updateBudgetTemplate(null, null);
		
	}
}
