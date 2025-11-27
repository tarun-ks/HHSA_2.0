package com.nyc.hhs.service.test.com.nyc.hhs;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.InvoiceService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ContractList;

public class InvoiceServiceTestR5
{
	
	InvoiceService invoiceService = new InvoiceService();
	
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
	
	@Test(expected = ApplicationException.class)
	public void fetchContractSummaryCase1() throws ApplicationException
	{
		HashMap<String, String> loParamHashMap = new HashMap<String, String>();
		loParamHashMap.put("budgetType", "12876");
		ContractList loScore = new ContractList();
		loScore = invoiceService.fetchContractInvoiceSummary(moSession, loParamHashMap);
	}
	
	@Test
	public void fetchContractSummaryCase2() throws ApplicationException
	{
		HashMap<String, String> loParamHashMap = new HashMap<String, String>();
		loParamHashMap.put("budgetType", "12876");
		loParamHashMap.put("invoiceId", "978");
		ContractList loScore = new ContractList();
		loScore = (ContractList) invoiceService.fetchContractInvoiceSummary(moSession, loParamHashMap);
		assertNotNull(loScore);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchContractSummaryCase3() throws ApplicationException
	{
		HashMap<String, String> loParamHashMap = new HashMap<String, String>();
		loParamHashMap.put("budgetType", "12876");
		ContractList loScore = new ContractList();
		loScore = invoiceService.fetchContractInvoiceSummary(null, loParamHashMap);
	}
}
