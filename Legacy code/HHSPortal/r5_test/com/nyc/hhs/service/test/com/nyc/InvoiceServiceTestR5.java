package com.nyc.hhs.service.test.com.nyc;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.FinancialsBudgetService;
import com.nyc.hhs.daomanager.service.InvoiceService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.BudgetList;
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
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
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
	public void fetchContractInvoiceSummaryCase1() throws ApplicationException
	{

		HashMap<String, String> loHashMap = new HashMap<String, String>();
				loHashMap.put("invoiceId", "977");
				
				ContractList loContractListResult = invoiceService.fetchContractInvoiceSummary(moSession,loHashMap);
					
				assertNotNull(loContractListResult);
	
	
	
	
	}

	@Test(expected = ApplicationException.class)
	public void fetchContractInvoiceSummaryCase2() throws ApplicationException
	{

		HashMap<String, String> loHashMap = new HashMap<String, String>();
				loHashMap.put("invoiceId", "977");
				
				ContractList loContractListResult = invoiceService.fetchContractInvoiceSummary(null,loHashMap);
					
				assertNotNull(loContractListResult);
	
	
	
	
	}








}