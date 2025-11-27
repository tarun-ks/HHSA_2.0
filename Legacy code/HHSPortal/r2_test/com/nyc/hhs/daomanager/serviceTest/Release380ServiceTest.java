package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.AgencySettingService;
import com.nyc.hhs.daomanager.service.ConfigurationService;
import com.nyc.hhs.daomanager.service.FinancialsListService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class Release380ServiceTest
{
	private static SqlSession moSession = null; // SQL Session
	private static P8UserSession session = null; // FileNet session
	private static FinancialsListService loFinancialsListService = null;
	private static ConfigurationService loConfigurationService = null;
	private static AgencySettingService loAgencySettingService = null;

	public String baseContractId = "5590";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{
		loFinancialsListService = new FinancialsListService();
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		session = getFileNetSession();
	}

	public P8UserSession getFileNetSession() throws ApplicationException
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
		loUserSession.setIsolatedRegionNumber("3");
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
		return loUserSession;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
		moSession.close();
		session.getFilenetPEDBSession().close();
		moSession.rollback();
	}

	
	@Test
	public void testdeleteContract() throws ApplicationException, IOException
	{
		boolean lbContractDeleteStatus = loFinancialsListService.deleteContract(moSession, baseContractId, Boolean.TRUE);
		assertTrue(lbContractDeleteStatus);
	}
	
	@Test
	public void testfetchContractTitleAndOrgID() throws ApplicationException, IOException
	{
		HashMap<String, String> loContractDetails = loFinancialsListService.fetchContractTitleAndOrgID(moSession, baseContractId);
		assertNotNull(loContractDetails);
	}

	@Test
	public void testfetchPendingBudget() throws ApplicationException, IOException
	{
		Boolean lbPendingBudget = loFinancialsListService.fetchPendingBudget(moSession, baseContractId);
		assertFalse(lbPendingBudget);
	}

	@Test
	public void testdeleteContractErrorCheckRule() throws ApplicationException, IOException
	{
		boolean lbErrorCheckRule = loFinancialsListService.deleteContractErrorCheckRule(moSession, baseContractId);
		assertTrue(lbErrorCheckRule);
	}
	
	@Test
	public void testterminateDeleteContractWorkFlows() throws ApplicationException, IOException
	{
		Boolean lbTerminationFlag = loFinancialsListService.terminateDeleteContractWorkFlows(session, Boolean.TRUE, baseContractId);
		assertTrue(lbTerminationFlag);
	}
	
	@Test(expected = java.lang.Exception.class)
	public void testdeleteContractEx() throws ApplicationException, IOException
	{
		boolean lbContractDeleteStatus = loFinancialsListService.deleteContract(null, baseContractId, Boolean.TRUE);
		assertTrue(lbContractDeleteStatus);
	}
	
	@Test(expected = java.lang.Exception.class)
	public void testfetchContractTitleAndOrgIDEx() throws ApplicationException, IOException
	{
		HashMap<String, String> loContractDetails = loFinancialsListService.fetchContractTitleAndOrgID(null, baseContractId);
		assertNotNull(loContractDetails);
	}

	@Test(expected = java.lang.Exception.class)
	public void testfetchPendingBudgetEx() throws ApplicationException, IOException
	{
		Boolean lbPendingBudget = loFinancialsListService.fetchPendingBudget(null, baseContractId);
		assertTrue(lbPendingBudget);
	}

	@Test(expected = java.lang.Exception.class)
	public void testdeleteContractErrorCheckRuleEx() throws ApplicationException, IOException
	{
		boolean lbErrorCheckRule = loFinancialsListService.deleteContractErrorCheckRule(null, baseContractId);
		assertTrue(lbErrorCheckRule);
	}
	
	@Test(expected = java.lang.Exception.class)
	public void testterminateDeleteContractWorkFlowsEx() throws ApplicationException, IOException
	{
		Boolean lbTerminationFlag = loFinancialsListService.terminateDeleteContractWorkFlows(null, Boolean.TRUE, baseContractId);
		assertTrue(lbTerminationFlag);
	}
	
	@Test
	public void testupdateReviewInProgressFlag() throws ApplicationException, Exception
	{
		Map aoAgencyDetailsMap = new HashMap();
		aoAgencyDetailsMap.put("updateReviewInProgressFlag", "YES");
		aoAgencyDetailsMap.put("modifiedByUserId", "system");
		aoAgencyDetailsMap.put("Agency_ID", "DOC");
		aoAgencyDetailsMap.put("reviewProcessId", "12");		
		boolean lbUpdateFlag = loAgencySettingService.updateReviewInProgressFlag(moSession, aoAgencyDetailsMap);
		assertTrue(lbUpdateFlag);
	}
	
	@Test
	public void testfetchReviewInProgressFlag() throws ApplicationException, Exception
	{
		HashMap<String, String> aoAgencyDetailsMap = new HashMap<String, String>();
		aoAgencyDetailsMap.put("agencyId", "DOC");
		aoAgencyDetailsMap.put("taskType", "Invoice Review");		
		String lsReviewFlag = loAgencySettingService.fetchReviewInProgressFlag(moSession, aoAgencyDetailsMap);
		assertNotNull(lsReviewFlag);
	}

	@Test(expected = java.lang.Exception.class)
	public void testupdateReviewInProgressFlagEx() throws ApplicationException, Exception
	{
		Map aoAgencyDetailsMap = new HashMap();
		aoAgencyDetailsMap.put("updateReviewInProgressFlag", "YES");
		aoAgencyDetailsMap.put("modifiedByUserId", "system");
		aoAgencyDetailsMap.put("Agency_ID", "DOC");
		aoAgencyDetailsMap.put("reviewProcessId", "12");		
		boolean lbUpdateFlag = loAgencySettingService.updateReviewInProgressFlag(null, aoAgencyDetailsMap);
		assertTrue(lbUpdateFlag);
	}
	
	@Test(expected = java.lang.Exception.class)
	public void testfetchReviewInProgressFlagEx() throws ApplicationException, Exception
	{
		HashMap<String, String> aoAgencyDetailsMap = new HashMap<String, String>();
		aoAgencyDetailsMap.put("agencyId", "DOC");
		aoAgencyDetailsMap.put("taskType", "Invoice Review");		
		String lsReviewFlag = loAgencySettingService.fetchReviewInProgressFlag(null, aoAgencyDetailsMap);
		assertNotNull(lsReviewFlag);
	}
	
	@Test
	public void testdeleteForCancelUpdate() throws ApplicationException, IOException
	
	{ 
		 loConfigurationService = new ConfigurationService();
		 Boolean lbCancelStatus =  loConfigurationService.deleteForCancelUpdate(moSession, "1038");
		 assertTrue(lbCancelStatus);
	}
	
	@Test(expected = java.lang.Exception.class)
	public void testdeleteForCancelUpdateException() throws ApplicationException, IOException
	
	{ 
		 loConfigurationService = new ConfigurationService();
		 loConfigurationService.deleteForCancelUpdate(null, "1038");
	}
	
	@Test(expected = java.lang.Exception.class)
	public void testdeleteForCancelUpdateException1() throws ApplicationException, IOException
	
	{ 
		 loConfigurationService = new ConfigurationService();
		 loConfigurationService.deleteForCancelUpdate(moSession, null);
	}
	
	@Test
	public void testfetchDiscrepencyDetailsForUpdateTask() throws ApplicationException, IOException
	
	{ 
		 loConfigurationService = new ConfigurationService();
		 String lsSelectedContractId =  loConfigurationService.fetchDiscrepencyDetailsForUpdateTask(moSession, "1038");
		 assertNull(lsSelectedContractId);
	}
	
	@Test(expected = java.lang.Exception.class)
	public void testfetchDiscrepencyDetailsForUpdateTaskException() throws ApplicationException, IOException
	
	{ 
		loConfigurationService = new ConfigurationService();
	    loConfigurationService.fetchDiscrepencyDetailsForUpdateTask(null, "1038");
	
	}
	
	@Test(expected = java.lang.Exception.class)
	public void testfetchDiscrepencyDetailsForUpdateTaskException1() throws ApplicationException, IOException
	
	{ 
		loConfigurationService = new ConfigurationService();
	    loConfigurationService.fetchDiscrepencyDetailsForUpdateTask(moSession, null);
	
	}

}
