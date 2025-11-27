package com.nyc.hhs.service.test.com.nyc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.ContractBudgetService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ContractList;

public class ContractBudgetServiceTestR5
{
	
	ContractBudgetService contractBudgetService = new ContractBudgetService();
	
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
	
	@Test(expected = ApplicationException.class)
	public void fetchContractSummaryCase1() throws ApplicationException
	{
		HashMap<String, String> loParamHashMap = new HashMap<String, String>();
		loParamHashMap.put("budgetType", "1");
		loParamHashMap.put("contractId", "6496");
		loParamHashMap.put("budgetId", "12889");
		ContractList loScore = new ContractList();
		loScore = contractBudgetService.fetchContractSummary(null, loParamHashMap);
	}
	
	@Test
	public void fetchContractSummaryCase2() throws ApplicationException
	{
		HashMap<String, String> loParamHashMap = new HashMap<String, String>();
		loParamHashMap.put("budgetId", "12871");
		loParamHashMap.put("contractId", "6374");
		loParamHashMap.put("budgetType", "2");
		ContractList loScore = new ContractList();
		loScore = contractBudgetService.fetchContractSummary(moSession, loParamHashMap);
		assertNotNull(loScore);
	}
	
	@Test
	public void fetchContractSummaryCase3() throws ApplicationException
	{
		HashMap<String, String> loParamHashMap = new HashMap<String, String>();
		loParamHashMap.put("budgetType", "1");
		loParamHashMap.put("contractId", "6496");
		loParamHashMap.put("budgetId", "12889");
		ContractList loScore = new ContractList();
		loScore = contractBudgetService.fetchContractSummary(moSession, loParamHashMap);
		assertEquals("20000", loScore.getAmendAmount());
	}
}