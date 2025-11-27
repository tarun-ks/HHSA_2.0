package com.nyc.hhs.contractsbatch.servicetestR7;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
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
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.DAOUtil;
import com.nyc.hhs.util.PropertyLoader;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DAOUtil.class)
public class ContractBatchServiceTest
{

	ContractsBatchService contractBatchService=new ContractsBatchService();
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
//R7 test cases for the defect 8644 part 3
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void contractBatchServiceTest1() throws ApplicationException{
		HashMap loMap=new HashMap<String,String>();
		
		contractBatchService.getPartialMergeRequestList(moSession,loMap);
		loMap.put(HHSConstants.CONTRACT_ID_WORKFLOW, "1");
		
		
	}

	
	@Test(expected = NullPointerException.class)
	public void contractBatchServiceTest2() throws ApplicationException{
		HashMap<String, String> loMap = new HashMap<String, String>();
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class),Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		contractBatchService.getPartialMergeRequestList(moSession, loMap);
		
	}
	
	@Test
	public void contractBatchServiceTest3() throws ApplicationException{
		HashMap<String, String> loMap=new HashMap<String,String>();
		
		contractBatchService.getPartialMergeRequestList(moSession,loMap);
		loMap.put("12473", "2");
		
		
	}
	
	@Test(expected = ApplicationException.class)
	public void MARTestCase1() throws ApplicationException
	
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("REQUEST_PARTIAL_MERGE", "1");
		loHashMap.put("contractStatus", "118");
		loHashMap.put("contractTypeId", "2");
		boolean lbFlag = contractBatchService.markAmendmentETLRegistredWithPartialMergeRequest(null, loHashMap);
		assertFalse(lbFlag);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void MARTestCase2() throws ApplicationException
	
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("REQUEST_PARTIAL_MERGE", "2");
		loHashMap.put("contractStatus", "118");
		loHashMap.put("contractTypeId", "2");
		boolean lbFlag = contractBatchService.markAmendmentETLRegistredWithPartialMergeRequest(null, loHashMap);
		assertTrue(lbFlag);
		
	}
	@Test(expected = ApplicationException.class)
	public void MARTestCase3() throws ApplicationException
	
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("REQUEST_PARTIAL_MERGE", "2");
		loHashMap.put("contractStatus", "118");
		loHashMap.put("contractTypeId", "2");
		boolean lbFlag = contractBatchService.markAmendmentETLRegistredWithPartialMergeRequest(null, loHashMap);
		assertTrue(lbFlag);
		
	}
	
	@Test(expected = NullPointerException.class)
	public void MARTestCase4() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		boolean lbFlag =contractBatchService.markAmendmentETLRegistredWithPartialMergeRequest(moSession, loHashMap);
		
	}
	
	@Test
	public void MARTestCase() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		boolean lbFlag =contractBatchService.markAmendmentETLRegistredWithPartialMergeRequest(moSession, loHashMap);
		
	}
	
	
	@Test(expected = ApplicationException.class)
	public void MARTestCase5() throws ApplicationException
	
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("REQUEST_PARTIAL_MERGE", "1");
		loHashMap.put("contractStatus", "118");
		loHashMap.put("contractTypeId", "2");
		boolean lbFlag = contractBatchService.markAmendmentETLRegistredWithPartialMergeRequest(null, loHashMap);
		assertTrue(lbFlag);
		
	}
	@Test(expected = ApplicationException.class)
	public void MARTestCase6() throws ApplicationException
	
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("REQUEST_PARTIAL_MERGE", "1");
		loHashMap.put("contractStatus", "118");
		loHashMap.put("contractTypeId", "2");
		boolean lbFlag = contractBatchService.markAmendmentETLRegistredWithPartialMergeRequest(null, loHashMap);
		assertFalse(lbFlag);
		
	}
	
	@Test
	public void updateBudgetStatus() throws ApplicationException
	{
		List<ContractBean> aoContractBeanList= new ArrayList<ContractBean>();
		ContractBean loContractBean= new ContractBean();
		aoContractBeanList.add(loContractBean);
		contractBatchService.updateBudgetStatus(moSession,aoContractBeanList);
		
	}
	@Test
	public void updateBudgetStatus1() throws ApplicationException
	{
		List<ContractBean> aoContractBeanList= new ArrayList<ContractBean>();
		ContractBean loContractBean= new ContractBean();
		loContractBean.setContractId("13003");
		aoContractBeanList.add(loContractBean);
		contractBatchService.updateBudgetStatus(moSession,aoContractBeanList);
		
	}
	@Test(expected = ApplicationException.class)
	public void updateBudgetStatusException() throws ApplicationException
	{
		List<ContractBean> aoContractBeanList= new ArrayList<ContractBean>();
		ContractBean loContractBean= new ContractBean();
		aoContractBeanList.add(loContractBean);
		contractBatchService.updateBudgetStatus(null,aoContractBeanList);
		
	}
	
	
	

}
