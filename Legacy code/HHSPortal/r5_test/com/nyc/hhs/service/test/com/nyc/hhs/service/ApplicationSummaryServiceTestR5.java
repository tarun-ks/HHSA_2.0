package com.nyc.hhs.service.test.com.nyc.hhs.service;

import static org.junit.Assert.assertEquals;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.ApplicationSummaryService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;

public class ApplicationSummaryServiceTestR5
{
	
	ApplicationSummaryService applicationSummaryService = new ApplicationSummaryService();
	
	private static SqlSession moSession = null; // SQL Session
	
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
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
			System.out.println("Before");
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
	
	@Test
	public void getBussAppExpiringDateCase1() throws ApplicationException
	{
		String lsOrdId = "Org_511";
		String loResult = applicationSummaryService.getBussAppExpiringDate(moSession, lsOrdId);
		assertEquals(null, loResult);
	}
	
	@Test(expected = ApplicationException.class)
	public void getBussAppExpiringDateCase2() throws ApplicationException
	{
		String lsOrdId = "Org_511";
		String loResult = applicationSummaryService.getBussAppExpiringDate(null, lsOrdId);		
	}
	
	@Test
	public void getServiceAppExpiringDateCase1() throws ApplicationException
	{
		String lsOrdId = "Org_511";
		String loResult = applicationSummaryService.getServiceAppExpiringDate(moSession, lsOrdId);
		assertEquals(null, loResult);
	}
	
	@Test(expected = ApplicationException.class)
	public void getServiceAppExpiringDateCase2() throws ApplicationException
	{
		String lsOrdId = "Org_511";
		String loResult = applicationSummaryService.getServiceAppExpiringDate(null, lsOrdId);
	}
	
}
