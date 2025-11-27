package com.nyc.hhs.service.test.com.nyc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.AwardService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.HHSMyBatisFilenetDBConnectionFactory;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import com.nyc.hhs.util.PropertyLoader;

public class AwardServiceTestR5
{
	AwardService awardService = new AwardService();
	private static P8UserSession moP8session = null;
	private static SqlSession moSession = null; // SQL Session
	
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
		loUserSession.setIsolatedRegionNumber("1");
		SqlSession loFilenetPEDBSession = HHSMyBatisFilenetDBConnectionFactory.getLocalSqlSessionFactory()
				.openSession();
		loUserSession.setFilenetPEDBSession(loFilenetPEDBSession);
		return loUserSession;
	}// SQL Session
	
	/**
	 * SQL session created ONCE before the class
	 * 
	 * @throws java.lang.Exception
	 */
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moP8session = getFileNetSession();
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			System.out.println("Before");
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
		
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession.rollback();
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
		finally
		{
			moSession.rollback();
			moSession.close();
		}
		
	}
	
	@Test
	public void saveFinalizedAmountinDBCase1() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("proposalId", "3182");
		loHashMap.put("AMOUNT", "2322");
		assertTrue(awardService.saveFinalizedAmountinDB(moSession, loHashMap));
	}
	
	@Test
	public void saveFinalizedAmountinDBCase2() throws ApplicationException
	{
		HashMap<String, String> loHashMap = null;
		assertFalse(awardService.saveFinalizedAmountinDB(moSession, loHashMap));
	}
	
	@Test(expected = ApplicationException.class)
	public void saveFinalizedAmountinDBCase3() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("proposalId", "3182");
		loHashMap.put("AMOUNT", "2322");
		awardService.saveFinalizedAmountinDB(null, loHashMap);
	}
	
	@Test
	public void updateAwardNegotiationDetailsCase1() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("IsNegotiationRequired", "true");
		loHashMap.put("procurementId", "4163");
		assertTrue(awardService.updateAwardNegotiationDetails(moSession, loHashMap));
	}
	
	@Test
	public void updateAwardNegotiationDetailsCase2() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("IsNegotiationRequired", "false");
		loHashMap.put("procurementId", "4163");
		assertTrue(awardService.updateAwardNegotiationDetails(moSession, loHashMap));
	}
	
	@Test
	public void updateAwardNegotiationDetailsCase3() throws ApplicationException
	{
		HashMap<String, String> loHashMap = null;
		assertFalse(awardService.updateAwardNegotiationDetails(moSession, loHashMap));
	}
	
	@Test(expected = ApplicationException.class)
	public void updateAwardNegotiationDetailsCase4() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("IsNegotiationRequired", "false");
		loHashMap.put("procurementId", "4163");
		assertTrue(awardService.updateAwardNegotiationDetails(null, loHashMap));
	}
	
	@Test
	public void updateAwardNegotiationDetailsCase5() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("procurementId", "4163");
		assertFalse(awardService.updateAwardNegotiationDetails(moSession, loHashMap));
	}
	
	@Test
	public void cancelAllAwardWorkflowsCase1() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put("IsNegotiationRequired", "false");
		loHashMap.put("procurementId", "4163");
		loHashMap.put("evaluationPoolMappingId", "2180");
		boolean Flag = awardService.cancelAllAwardWorkflows(moSession, moP8session, loHashMap);
		assertTrue(Flag);
	}
	
	@Test(expected = ApplicationException.class)
	public void cancelAllAwardWorkflowsCase2() throws ApplicationException
	{
		HashMap loHashMap = new HashMap();
		loHashMap.put("IsNegotiationRequired", "false");
		loHashMap.put("procurementId", "4163");
		loHashMap.put("evaluationPoolMappingId", "2180");
		boolean Flag = awardService.cancelAllAwardWorkflows(null, null, loHashMap);
		assertTrue(Flag);
	}
	
	@Test
	public void updateContractForNegotiationSeletedCase1() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("contractStatus", "127");
		loHashMap.put("providerId", "Org_509");
		loHashMap.put("evaluationPoolMappingId", "2004");
		boolean flag = awardService.updateContractForNegotiationSeleted(moSession, loHashMap);
		assertTrue(flag);
	}
	
	@Test
	public void updateContractForNegotiationSeletedCase2() throws ApplicationException
	{
		HashMap<String, String> loHashMap = null;
		boolean flag = awardService.updateContractForNegotiationSeleted(moSession, loHashMap);
		assertFalse(flag);
	}
	
	@Test(expected = ApplicationException.class)
	public void updateContractForNegotiationSeletedCase3() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("contractStatus", "127");
		loHashMap.put("providerId", "Org_509");
		loHashMap.put("evaluationPoolMappingId", "2004");
		boolean flag = awardService.updateContractForNegotiationSeleted(null, loHashMap);
		assertTrue(flag);
	}
	
	@Test
	public void updateContractAmountAfterNegotiationCase1() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("awardStatusId", "177");
		loHashMap.put("contractStatus", "127");
		loHashMap.put("providerId", "Org_509");
		loHashMap.put("evaluationPoolMappingId", "2004");
		boolean flag = awardService.updateContractAmountAfterNegotiation(moSession, loHashMap);
		assertTrue(flag);
	}
	
	@Test
	public void updateContractAmountAfterNegotiationCase2() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("awardStatusId", "197");
		loHashMap.put("contractStatus", "127");
		loHashMap.put("providerId", "Org_509");
		loHashMap.put("evaluationPoolMappingId", "2004");
		boolean flag = awardService.updateContractAmountAfterNegotiation(moSession, loHashMap);
		assertFalse(flag);
	}
	
	@Test(expected = ApplicationException.class)
	public void updateContractAmountAfterNegotiationCase3() throws ApplicationException
	{
		HashMap<String, String> loHashMap = new HashMap<String, String>();
		loHashMap.put("awardStatusId", "177");
		loHashMap.put("contractStatus", "127");
		loHashMap.put("providerId", "Org_509");
		loHashMap.put("evaluationPoolMappingId", "2004");
		boolean flag = awardService.updateContractAmountAfterNegotiation(null, loHashMap);
	}
}
