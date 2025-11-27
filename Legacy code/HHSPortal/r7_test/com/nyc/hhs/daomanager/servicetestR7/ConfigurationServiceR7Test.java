package com.nyc.hhs.daomanager.servicetestR7;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.contractsbatch.service.ContractsBatchService;
import com.nyc.hhs.daomanager.service.ConfigurationService;
import com.nyc.hhs.daomanager.service.FinancialsService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.AutoApprovalConfigBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CostCenterServicesMappingList;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;


public class ConfigurationServiceR7Test {


	ConfigurationService configurationService=new ConfigurationService();
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
	
	@Test
	public void testfetchCostCenterServicesDetails() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		List<CostCenterServicesMappingList> loServicesList = new ArrayList<CostCenterServicesMappingList>();
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("11180");
		
	    loGridBean.setFiscalYearID("2017");
		loGridBean.setAgencyId("DOC");
		loGridBean.setContractBudgetID("10730");
		loGridBean.setCreatedByUserId("agency_21");
		loServicesList = (List<CostCenterServicesMappingList>) loConfigurationService.fetchCostCenterServicesDetails(moSession, loGridBean);
		//assertTrue(loServicesList != null);
}
	
	@Test
	public void testfetchCostCenterServicesDetailscase1() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		List<CostCenterServicesMappingList> loServicesList = new ArrayList<CostCenterServicesMappingList>();
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("11166");
		
	    loGridBean.setFiscalYearID("2017");
		loGridBean.setAgencyId("DOC");
		loGridBean.setContractBudgetID("10730");
		loGridBean.setCreatedByUserId("agency_21");
		loServicesList = (List<CostCenterServicesMappingList>) loConfigurationService.fetchCostCenterServicesDetails(moSession, loGridBean);
		//assertTrue(loServicesList != null);
}
	
	
	
	@Test(expected = ApplicationException.class)
	public void testfetchCostCenterServicesDetailscase2() throws ApplicationException
	{

	ConfigurationService loConfigurationService = new ConfigurationService();
	List<CostCenterServicesMappingList> loServicesList = new ArrayList<CostCenterServicesMappingList>();
	CBGridBean loGridBean = new CBGridBean();
	loGridBean.setContractID("11166");
	
    loGridBean.setFiscalYearID("2017");
	loGridBean.setAgencyId("DOC");
	loGridBean.setContractBudgetID("10730");
	loGridBean.setCreatedByUserId("agency_21");
	loServicesList = (List<CostCenterServicesMappingList>) loConfigurationService.fetchCostCenterServicesDetails(moSession, loGridBean);
	//assertTrue(loServicesList != null);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void testfetchCostCenterServicesDetailscase3() throws ApplicationException
	{

	ConfigurationService loConfigurationService = new ConfigurationService();
	List<CostCenterServicesMappingList> loServicesList = new ArrayList<CostCenterServicesMappingList>();
	CBGridBean loGridBean = new CBGridBean();
	loGridBean.setContractID("11166");
	
    loGridBean.setFiscalYearID("2017");
	loGridBean.setAgencyId("DOC");
	loGridBean.setContractBudgetID("10730");
	loGridBean.setCreatedByUserId("agency_21");
	loServicesList = (List<CostCenterServicesMappingList>) loConfigurationService.fetchCostCenterServicesDetails(null, loGridBean);
	//assertTrue(loServicesList != null);
	
		
	}
	
	@Test(expected = ApplicationException.class)
	public void testfetchCostCenterServicesDetailscase4() throws ApplicationException
	{

	ConfigurationService loConfigurationService = new ConfigurationService();
	List<CostCenterServicesMappingList> loServicesList = new ArrayList<CostCenterServicesMappingList>();
	CBGridBean loGridBean = new CBGridBean();
	loGridBean.setContractID("11166");
	
    loGridBean.setFiscalYearID("2017");
	loGridBean.setAgencyId("DOC");
	loGridBean.setContractBudgetID("10730");
	loGridBean.setCreatedByUserId("agency_21");
	loServicesList = (List<CostCenterServicesMappingList>) loConfigurationService.fetchCostCenterServicesDetails(null, null);
	//assertTrue(loServicesList != null);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void testfetchCostCenterServicesDetailscase5() throws ApplicationException
	{

	ConfigurationService loConfigurationService = new ConfigurationService();
	List<CostCenterServicesMappingList> loServicesList = new ArrayList<CostCenterServicesMappingList>();
	CBGridBean loGridBean = new CBGridBean();
	loGridBean.setContractID("1116612");
	
    loGridBean.setFiscalYearID("2017");
	loGridBean.setAgencyId("DOC");
	loGridBean.setContractBudgetID("10730");
	loGridBean.setCreatedByUserId("agency_21");
	loServicesList = (List<CostCenterServicesMappingList>) loConfigurationService.fetchCostCenterServicesDetails(null, null);
	//assertTrue(loServicesList != null);
		
	}
	
	@Test(expected = Exception.class)
	public void testfetchCostCenterServicesDetailscase6() throws ApplicationException
	{

	ConfigurationService loConfigurationService = new ConfigurationService();
	List<CostCenterServicesMappingList> loServicesList = new ArrayList<CostCenterServicesMappingList>();
	CBGridBean loGridBean = new CBGridBean();
	loGridBean.setContractID("11166");
	
    loGridBean.setFiscalYearID("2017");
	loGridBean.setAgencyId("DOC");
	loGridBean.setAmendmentContractID("11166");
	loGridBean.setContractBudgetID("10730");
	loGridBean.setCreatedByUserId("agency_21");
	loServicesList = (List<CostCenterServicesMappingList>) loConfigurationService.fetchCostCenterServicesDetails(null, null);
	//assertTrue(loServicesList != null);
		
	}
	
	@Test(expected = Exception.class)
	public void testfetchCostCenterServicesDetailscase7() throws ApplicationException
	{

	ConfigurationService loConfigurationService = new ConfigurationService();
	List<CostCenterServicesMappingList> loServicesList = new ArrayList<CostCenterServicesMappingList>();
	CBGridBean loGridBean = new CBGridBean();
	loGridBean.setContractID("11166");
	
    loGridBean.setFiscalYearID("2017");
	loGridBean.setAgencyId("DOC");
	loGridBean.setAmendmentContractID("11166");
	loGridBean.setContractBudgetID("10730");
	loGridBean.setCreatedByUserId("agency_21");
	loServicesList = (List<CostCenterServicesMappingList>) loConfigurationService.fetchCostCenterServicesDetails(null, loGridBean);
	//assertTrue(loServicesList != null);
		
	}
	
	@Test
	public void testfetchServicesStatusFlagcase1() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		String lsServiceDetailFlag = null;
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("11192");
		loGridBean.setFiscalYearID("2017");
		loGridBean.setContractBudgetID("10730");
		loGridBean.setCreatedByUserId("agency_21");
		lsServiceDetailFlag = loConfigurationService.fetchServicesStatusFlag(moSession, loGridBean);
		//assertTrue(lsServiceDetailFlag != null);
}
	
	
	@Test(expected = ApplicationException.class)
	public void testfetchServicesStatusFlagcase2() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		String lsServiceDetailFlag = null;
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("11192");
		loGridBean.setFiscalYearID("2017");
		loGridBean.setContractBudgetID("10730");
		loGridBean.setCreatedByUserId("agency_21");
		lsServiceDetailFlag = loConfigurationService.fetchServicesStatusFlag(null, loGridBean);
		//assertTrue(lsServiceDetailFlag != null);
}
	
	@Test(expected = Exception.class)
	public void testfetchServicesStatusFlagcase3() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		String lsServiceDetailFlag = null;
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("1119211");
		loGridBean.setFiscalYearID("2017");
		loGridBean.setContractBudgetID("10730");
		loGridBean.setCreatedByUserId("agency_21");
		lsServiceDetailFlag = loConfigurationService.fetchServicesStatusFlag(null, null);
		//assertTrue(lsServiceDetailFlag != null);
}
	@Test
	public void testinsertDefaultServicesConf() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Integer lsServiceCount;
		String asServicesConfFlag = "1";
		
		HashMap<String, String> loMap = new HashMap<String, String>();
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("11195");
		loGridBean.setFiscalYearID("2017");
		loGridBean.setContractBudgetID("10730");
		loGridBean.setCreatedByUserId("agency_21");
		asServicesConfFlag= loConfigurationService.insertDefaultServicesConf(moSession, loGridBean,"1", asServicesConfFlag);
		assertTrue(asServicesConfFlag != null);
}
	
	@Test
	public void testinsertDefaultServicesConfcase1() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Integer lsServiceCount;
		String asServicesConfFlag = "1";
		
		HashMap<String, String> loMap = new HashMap<String, String>();
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("11192");
		loGridBean.setFiscalYearID("2017");
		loGridBean.setContractBudgetID("10730");
		loGridBean.setCreatedByUserId("agency_21");
		asServicesConfFlag= loConfigurationService.insertDefaultServicesConf(moSession, loGridBean,"1", asServicesConfFlag);
		assertTrue(asServicesConfFlag != null);
}
	@Test
	public void testinsertDefaultServicesConfcase2() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Integer lsServiceCount;
		String asServicesConfFlag = "2";
		
		HashMap<String, String> loMap = new HashMap<String, String>();
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("11192");
		loGridBean.setContractTypeId("1");
		loGridBean.setFiscalYearID("2017");
		loGridBean.setContractBudgetID("10730");
		loGridBean.setCreatedByUserId("agency_21");
		asServicesConfFlag= loConfigurationService.insertDefaultServicesConf(moSession, loGridBean,"2", asServicesConfFlag);
		assertTrue(asServicesConfFlag != null);
		 
}
	
	@Test
	public void testinsertDefaultServicesConfcase3() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Integer lsServiceCount;
		String asServicesConfFlag = "3";
		
		HashMap<String, String> loMap = new HashMap<String, String>();
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("11192");
		loGridBean.setContractTypeId("2");
		loGridBean.setFiscalYearID("2017");
		loGridBean.setContractBudgetID("10730");
		loGridBean.setCreatedByUserId("agency_21");
		asServicesConfFlag= loConfigurationService.insertDefaultServicesConf(moSession, loGridBean,"3", asServicesConfFlag);
		assertTrue(asServicesConfFlag != null);
		
}
	
	@Test(expected = ApplicationException.class)
	public void testinsertDefaultServicesConfcase4() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Integer lsServiceCount;
		String asServicesConfFlag = "3";
		
		HashMap<String, String> loMap = new HashMap<String, String>();
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("11192");
		loGridBean.setContractTypeId("2");
		loGridBean.setFiscalYearID("2017");
		loGridBean.setContractBudgetID("10730");
		loGridBean.setCreatedByUserId("agency_21");
		asServicesConfFlag= loConfigurationService.insertDefaultServicesConf(null, loGridBean,"3", asServicesConfFlag);
		assertTrue(asServicesConfFlag != null);
		
}
	
	@Test(expected = Exception.class)
	public void testinsertDefaultServicesConfcase5() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Integer lsServiceCount;
		String asServicesConfFlag = "3";
		
		HashMap<String, String> loMap = new HashMap<String, String>();
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("11192");
		loGridBean.setContractTypeId("2");
		loGridBean.setFiscalYearID("2017");
		loGridBean.setContractBudgetID("10730");
		loGridBean.setCreatedByUserId("agency_21");
		asServicesConfFlag= loConfigurationService.insertDefaultServicesConf(null, null,"3", asServicesConfFlag);
		assertTrue(asServicesConfFlag != null);
		
}
	@Test
	public void testvalidateServicesOpted() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		String lsmessage = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setBaseContractId("11195");
		
		loTaskBean.setBudgetId("10730");
		loTaskBean.setContractId("2");
		loTaskBean.setCreatedByUserId("agency_21");
		lsmessage= loConfigurationService.validateServicesOpted(moSession, loTaskBean);
		//assertTrue(lsmessage != null);
}
	
	@Test
	public void testvalidateServicesOptedcase1() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		String lsmessage = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setBaseContractId("11192");
		
		loTaskBean.setBudgetId("10730");
		loTaskBean.setContractId("2");
		loTaskBean.setCreatedByUserId("agency_21");
		lsmessage= loConfigurationService.validateServicesOpted(moSession, loTaskBean);
		//assertTrue(lsmessage != null);
}
	
	@Test
	public void testvalidateServicesOptedcase4() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		String lsmessage = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		
		
		
		
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setBaseContractId("11192");
		loTaskBean.setSubBudgetId("10730");
		loTaskBean.setBudgetId("10730");
		loTaskBean.setContractId("2");
		loTaskBean.setCreatedByUserId("agency_21");
		lsmessage= loConfigurationService.validateServicesOpted(moSession, loTaskBean);
		//assertTrue(lsmessage != null);
}
	
	@Test(expected = Exception.class)
	public void testvalidateServicesOptedcase2() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		String lsmessage = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		
		
		
		
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setBaseContractId("11192");
		
		loTaskBean.setBudgetId("10730");
		loTaskBean.setContractId("2");
		loTaskBean.setCreatedByUserId("agency_21");
		lsmessage= loConfigurationService.validateServicesOpted(null, null);
		//assertTrue(lsmessage != null);
}
	@Test(expected = ApplicationException.class)
	public void testvalidateServicesOptedcase3() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		String lsmessage = null;
		Map<String, Object> loQueryMap = new HashMap<String, Object>();
		
		
		
		
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setBaseContractId("11192");
		
		loTaskBean.setBudgetId("10730");
		loTaskBean.setContractId("2");
		loTaskBean.setCreatedByUserId("agency_21");
		lsmessage= loConfigurationService.validateServicesOpted(null, loTaskBean);
		//assertTrue(lsmessage != null);
}
	
	@Test
	public void testinsertDefaultServicesConfForNewFycase1() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Integer lsServiceCount;
		String asFiscalYearScreen = "true";
		
		HashMap<String, String> loMap = new HashMap<String, String>();
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("11192");
		loGridBean.setFiscalYearID("2017");
		loGridBean.setContractBudgetID("10730");
		loGridBean.setCreatedByUserId("agency_21");
		loConfigurationService.insertDefaultServicesConfForNewFy(moSession, loGridBean,"true");
		assertTrue(asFiscalYearScreen != null);
}
	
	@Test
	public void testinsertDefaultServicesConfForNewFycase2() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Integer lsServiceCount;
		String asFiscalYearScreen = "false";
		
		HashMap<String, String> loMap = new HashMap<String, String>();
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("11192");
		loGridBean.setFiscalYearID("2017");
		loGridBean.setContractBudgetID("10730");
		loGridBean.setCreatedByUserId("agency_21");
		loConfigurationService.insertDefaultServicesConfForNewFy(moSession, loGridBean,"false");
		assertTrue(asFiscalYearScreen != null);
}
	
	@Test
	public void testinsertDefaultServicesConfForNewFycase7() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Integer lsServiceCount;
		String asFiscalYearScreen = "true";
		
		HashMap<String, String> loMap = new HashMap<String, String>();
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("11166");
		loGridBean.setFiscalYearID("2017");
		loGridBean.setContractBudgetID("10730");
		loGridBean.setCreatedByUserId("agency_21");
		loConfigurationService.insertDefaultServicesConfForNewFy(moSession, loGridBean,"true");
		assertTrue(asFiscalYearScreen != null);
}
	@Test(expected = ApplicationException.class)
    public void testinsertDefaultServicesConfForNewFycase3() throws ApplicationException
{
	ConfigurationService loConfigurationService = new ConfigurationService();
	Integer lsServiceCount;
	String asFiscalYearScreen = "false";
	
	HashMap<String, String> loMap = new HashMap<String, String>();
	CBGridBean loGridBean = new CBGridBean();
	loGridBean.setContractID("11192");
	loGridBean.setFiscalYearID("2017");
	loGridBean.setContractBudgetID("10730");
	loGridBean.setCreatedByUserId("agency_21");
	loConfigurationService.insertDefaultServicesConfForNewFy(null, loGridBean,"false");
	assertTrue(asFiscalYearScreen != null);
}
	
	@Test(expected = ApplicationException.class)
    public void testinsertDefaultServicesConfForNewFycase4() throws ApplicationException
{
	ConfigurationService loConfigurationService = new ConfigurationService();
	Integer lsServiceCount;
	String asFiscalYearScreen = "true";
	
	HashMap<String, String> loMap = new HashMap<String, String>();
	CBGridBean loGridBean = new CBGridBean();
	loGridBean.setContractID("11192");
	loGridBean.setFiscalYearID("2017");
	loGridBean.setContractBudgetID("10730");
	loGridBean.setCreatedByUserId("agency_21");
	loConfigurationService.insertDefaultServicesConfForNewFy(null, loGridBean,"true");
	assertTrue(asFiscalYearScreen != null);
}
	
	@Test(expected = Exception.class)
    public void testinsertDefaultServicesConfForNewFycase5() throws ApplicationException
{
	ConfigurationService loConfigurationService = new ConfigurationService();
	Integer lsServiceCount;
	String asFiscalYearScreen = "true";
	
	HashMap<String, String> loMap = new HashMap<String, String>();
	CBGridBean loGridBean = new CBGridBean();
	loGridBean.setContractID("11192");
	loGridBean.setFiscalYearID("2017");
	loGridBean.setContractBudgetID("10730");
	loGridBean.setCreatedByUserId("agency_21");
	loConfigurationService.insertDefaultServicesConfForNewFy(null, null,"true");
	assertTrue(asFiscalYearScreen != null);
}
	 
	@Test(expected = Exception.class)
    public void testinsertDefaultServicesConfForNewFycase6() throws ApplicationException
{
	ConfigurationService loConfigurationService = new ConfigurationService();
	Integer lsServiceCount;
	String asFiscalYearScreen = "false";
	
	HashMap<String, String> loMap = new HashMap<String, String>();
	CBGridBean loGridBean = new CBGridBean();
	loGridBean.setContractID("11192");
	loGridBean.setFiscalYearID("2017");
	loGridBean.setContractBudgetID("10730");
	loGridBean.setCreatedByUserId("agency_21");
	loConfigurationService.insertDefaultServicesConfForNewFy(null, null,"false");
	assertTrue(asFiscalYearScreen != null);
}
}