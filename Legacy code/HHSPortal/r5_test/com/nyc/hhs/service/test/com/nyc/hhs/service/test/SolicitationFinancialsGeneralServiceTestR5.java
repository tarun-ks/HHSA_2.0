package com.nyc.hhs.service.test.com.nyc.hhs.service.test;

import static org.junit.Assert.assertNotNull;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.SolicitationFinancialsGeneralService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ProcurementSummaryBean;

public class SolicitationFinancialsGeneralServiceTestR5
{
	SolicitationFinancialsGeneralService solicitationFinancialsGeneral = new SolicitationFinancialsGeneralService();
	
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
	public void fetchProcurementCountForProvHomePageCase1() throws ApplicationException
	{
		String lsOrgId = "City_14";
		ProcurementSummaryBean loScore = new ProcurementSummaryBean();
		loScore = solicitationFinancialsGeneral.fetchProcurementCountForProvHomePage(moSession, lsOrgId);
		assertNotNull(loScore);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchProcurementCountForProvHomePageCase2() throws ApplicationException
	{	
		String lsOrgId = null;
		ProcurementSummaryBean loScore = new ProcurementSummaryBean();
		loScore = solicitationFinancialsGeneral.fetchProcurementCountForProvHomePage(moSession, lsOrgId);
		
	}
	
}
