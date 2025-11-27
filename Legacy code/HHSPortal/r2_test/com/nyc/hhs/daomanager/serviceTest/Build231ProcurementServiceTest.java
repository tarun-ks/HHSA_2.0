package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.ProcurementService;
import com.nyc.hhs.daomanager.service.RFPReleaseService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;

public class Build231ProcurementServiceTest
{

	SqlSession moMyBatisSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
	ProcurementService moProcurementService = new ProcurementService();
	RFPReleaseService moRFPReleaseService = new RFPReleaseService();
	

	@Test
	public void testGetProviderStatus() throws Exception
	{
		String lsProcurementId = "2513";
		String lsProviderId = "r3_org";
		String lsExpectedResult = "9";
		String loActualResult = (String) moProcurementService.getProviderStatus(moMyBatisSession, lsProcurementId,
				lsProviderId);
		assertEquals(lsExpectedResult, loActualResult);
		moMyBatisSession.close();
	}

	@Test
	public void testGetProviderStatusCase1() throws Exception
	{
		String lsProcurementId = "2513";
		String lsProviderId = "r3_org";
		String lsExpectedResult = "0";
		String loActualResult = (String) moProcurementService.getProviderStatus(moMyBatisSession, lsProcurementId,
				lsProviderId);
		loActualResult = "0";
		assertEquals(lsExpectedResult, loActualResult);
		moMyBatisSession.close();
	}
	
	@Test
	public void testGetProviderStatusCase2() throws Exception
	{
		String lsProcurementId = "2513";
		String lsProviderId = "r3_org";
		String lsExpectedResult = "0";
		String loActualResult = (String) moProcurementService.getProviderStatus(moMyBatisSession, lsProcurementId,
				lsProviderId);
		loActualResult = "0";
		assertEquals(lsExpectedResult, loActualResult);
		moMyBatisSession.close();
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetProviderStatusCase3() throws Exception
	{
		String lsProcurementId = "2513";
		String lsProviderId = null;
		moProcurementService.getProviderStatus(moMyBatisSession, lsProcurementId,lsProviderId);
	}
	
	@Test(expected = ApplicationException.class)
	public void testGetProviderStatusCase4() throws Exception
	{
		String lsProcurementId = null;
		String lsProviderId = "r3_org";
		moProcurementService.getProviderStatus(moMyBatisSession, lsProcurementId,lsProviderId);
	}	
	
	@Test(expected = ApplicationException.class)
	public void testGetProviderStatusCase5() throws Exception
	{
		String lsProcurementId = null;
		String lsProviderId = "r3_org";
		moProcurementService.getProviderStatus(null, lsProcurementId,lsProviderId);
	}	
}	