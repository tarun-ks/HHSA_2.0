package com.nyc.hhs.service.test.com;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.FinancialsBudgetService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.BudgetList;

public class FinancialsBudgetServiceTestR5
{
	
	FinancialsBudgetService financialsBudgetService = new FinancialsBudgetService();
	
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
	public void fetchBudgetListSummaryCase1() throws ApplicationException
	{
		BudgetList loBudgetList = new BudgetList();
		loBudgetList.setStartNode(1);
		loBudgetList.setLsRequestFromHomePage("false");
		loBudgetList.setEndNode(5);
		loBudgetList.getBudgetStatusList().add("85");
		loBudgetList.setOrgId("Org_509");
		String lsUserType = null;
		List<BudgetList> loBudgetListResult = financialsBudgetService.fetchBudgetListSummary(moSession, lsUserType,
				loBudgetList);
		assertNull(loBudgetListResult);
	}
	
	@Test
	public void fetchBudgetListSummaryCase2() throws ApplicationException
	
	{
		BudgetList loBudgetBean = new BudgetList("city_org");
		loBudgetBean.setOrgId("city_org");
		loBudgetBean.setOrgType("city_org");
		loBudgetBean.setLsRequestFromHomePage("false");
		loBudgetBean.setBudgetValueFrom("0");
		loBudgetBean.setBudgetValueTo("0");
		String lsUserType = "city_org";
		loBudgetBean.setIsFilter(false);
		List<BudgetList> loList = financialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetBean);
		assertTrue(loList.isEmpty());
	}
	
	@Test
	public void fetchBudgetListSummaryCase3() throws ApplicationException
	{
		BudgetList loBudgetBean = new BudgetList("provider_org");
		loBudgetBean.setOrgId("Org_509");
		loBudgetBean.setUserId("3298");
		loBudgetBean.setOrgType("provider_org");
		loBudgetBean.setLsRequestFromHomePage("false");
		String lsUserType = "provider_org";
		loBudgetBean.setIsFilter(false);
		List<BudgetList> loList = financialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetBean);
		assertTrue(loList.isEmpty());
	}
	
	@Test
	public void fetchBudgetListSummaryCase4() throws ApplicationException
	{
		BudgetList loBudgetBean = new BudgetList("agency_org");
		loBudgetBean.setOrgId("DOC");
		loBudgetBean.setOrgType("agency_org");
		loBudgetBean.setLsRequestFromHomePage("false");
		String lsUserType = "agency_org";
		loBudgetBean.setIsFilter(false);
		List<BudgetList> loList = financialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetBean);
		assertTrue(loList.isEmpty());
		
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchBudgetListSummaryCase5() throws ApplicationException
	{
		BudgetList loBudgetBean = new BudgetList("agency_org");
		loBudgetBean.setOrgId("DOC");
		loBudgetBean.setOrgType("agency_org");
		loBudgetBean.setLsRequestFromHomePage("false");
		String lsUserType = "agency_org";
		loBudgetBean.setIsFilter(false);
		List<BudgetList> loList = financialsBudgetService.fetchBudgetListSummary(null, lsUserType, loBudgetBean);
		assertTrue(loList.isEmpty());
		
	}
	
	@Test
	public void fetchBudgetListSummaryCase6() throws ApplicationException
	{
		BudgetList loBudgetBean = new BudgetList("agency_org");
		loBudgetBean.setOrgId("DOC");
		loBudgetBean.setOrgType("agency_org");
		loBudgetBean.setLsRequestFromHomePage("false");
		String lsUserType = "agency_org";
		loBudgetBean.setIsFilter(false);
		List<String> llList = null;
		loBudgetBean.setBudgetTypeList(llList);
		loBudgetBean.setBudgetStatusList(llList);
		List<BudgetList> loList = financialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetBean);
		assertTrue(loList.isEmpty());
		
	}
	
	@Test
	public void fetchBudgetListSummaryCase7() throws ApplicationException
	{
		BudgetList loBudgetBean = new BudgetList("agency_org");
		List<String> alList = null;
		loBudgetBean.setBudgetTypeList(alList);
		List<String> llList = new ArrayList<String>();
		loBudgetBean.setBudgetStatusList(llList);
		loBudgetBean.setOrgId("DOC");
		loBudgetBean.setOrgType("agency_org");
		loBudgetBean.setLsRequestFromHomePage("false");
		String lsUserType = "agency_org";
		loBudgetBean.setIsFilter(false);
		List<BudgetList> loList = financialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetBean);
		assertTrue(loList.isEmpty());
	}
	
	@Test
	public void fetchBudgetListSummaryCase8() throws ApplicationException
	{
		
		BudgetList loBudgetBean = new BudgetList("agency_org");
		List<String> alList = null;
		loBudgetBean.setBudgetStatusList(alList);
		List<String> llList = new ArrayList<String>();
		loBudgetBean.setBudgetTypeList(llList);
		loBudgetBean.setOrgId("DOC");
		loBudgetBean.setOrgType("agency_org");
		loBudgetBean.setLsRequestFromHomePage("false");
		String lsUserType = "agency_org";
		loBudgetBean.setIsFilter(false);
		List<BudgetList> loList = financialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetBean);
		assertTrue(loList.isEmpty());
	}
	
	@Test
	public void getBudgetListCountCase1() throws ApplicationException
	{
		BudgetList loBudgetBean = new BudgetList("city_org");
		loBudgetBean.setOrgId("city_org");
		loBudgetBean.setOrgType("city_org");
		loBudgetBean.setLsRequestFromHomePage("false");
		String lsUserType = "city_org";
		loBudgetBean.setIsFilter(true);
		List<String> llList = new ArrayList<String>();
		llList.add("");
		loBudgetBean.setBudgetStatusList(llList);
		loBudgetBean.setBudgetTypeList(llList);
		Integer loList = financialsBudgetService.getBudgetListCount(moSession, lsUserType, loBudgetBean);
		assertTrue(loList == 0);
	}
	
	@Test
	public void getBudgetListCountCase2() throws ApplicationException
	{
		BudgetList loBudgetBean = new BudgetList("agency_org");
		List<String> llList = new ArrayList<String>();
		llList.add("");
		loBudgetBean.setBudgetStatusList(llList);
		loBudgetBean.setBudgetTypeList(llList);
		loBudgetBean.setOrgId("DOC");
		loBudgetBean.setOrgType("agency_org");
		loBudgetBean.setLsRequestFromHomePage("false");
		String lsUserType = "agency_org";
		loBudgetBean.setIsFilter(false);
		Integer loList = financialsBudgetService.getBudgetListCount(moSession, lsUserType, loBudgetBean);
		assertTrue(loList == 0);
		
	}
	
	@Test
	public void getBudgetListCountCase3() throws ApplicationException
	{
		BudgetList loBudgetBean = new BudgetList("provider_org");
		List<String> llList = new ArrayList<String>();
		llList.add("");
		loBudgetBean.setBudgetStatusList(llList);
		loBudgetBean.setBudgetTypeList(llList);
		loBudgetBean.setOrgId("DOC");
		loBudgetBean.setAgency("DOC");
		loBudgetBean.setOrgType("provider_org");
		loBudgetBean.setLsRequestFromHomePage("false");
		String lsUserType = "provider_org";
		loBudgetBean.setIsFilter(false);
		Integer loList = financialsBudgetService.getBudgetListCount(moSession, lsUserType, loBudgetBean);
		assertTrue(loList == 0);
		
	}
	
	@Test
	public void getBudgetListCountCase4() throws ApplicationException
	{
		BudgetList loBudgetBean = new BudgetList("provider_org");
		List<String> llList = new ArrayList<String>();
		llList.add("");
		loBudgetBean.setBudgetStatusList(llList);
		loBudgetBean.setBudgetTypeList(llList);
		loBudgetBean.setOrgId("DOC");
		loBudgetBean.setAgency("DOC");
		loBudgetBean.setOrgType("provider_org");
		loBudgetBean.setLsRequestFromHomePage("false");
		String lsUserType = "";
		loBudgetBean.setIsFilter(false);
		Integer loList = financialsBudgetService.getBudgetListCount(moSession, lsUserType, loBudgetBean);
		assertTrue(loList == 0);
		
	}
	
	@Test(expected = ApplicationException.class)
	public void getBudgetListCountCase5() throws ApplicationException
	{
		BudgetList loBudgetBean = new BudgetList("agency_org");
		List<String> llList = new ArrayList<String>();
		llList.add("");
		loBudgetBean.setBudgetStatusList(llList);
		loBudgetBean.setBudgetTypeList(llList);
		loBudgetBean.setOrgId("DOC");
		loBudgetBean.setOrgType("agency_org");
		loBudgetBean.setLsRequestFromHomePage("false");
		String lsUserType = "agency_org";
		loBudgetBean.setIsFilter(false);
		Integer loList = financialsBudgetService.getBudgetListCount(null, lsUserType, loBudgetBean);
		assertTrue(loList == 0);
		
	}
	
	@Test
	public void getBudgetListCountCase6() throws ApplicationException
	{
		BudgetList loBudgetBean = new BudgetList("provider_org");
		loBudgetBean.setOrgId("DOC");
		loBudgetBean.setAgency("DOC");
		loBudgetBean.setOrgType("provider_org");
		loBudgetBean.setLsRequestFromHomePage("false");
		List<String> llList = null;
		loBudgetBean.setBudgetStatusList(llList);
		loBudgetBean.setBudgetTypeList(llList);
		String lsUserType = null;
		loBudgetBean.setIsFilter(false);
		Integer loList = financialsBudgetService.getBudgetListCount(moSession, lsUserType, loBudgetBean);
		assertTrue(loList == 0);
		
	}
	
	@Test
	public void fetchBudgetListSummaryCaseXXXX() throws ApplicationException
	{
		BudgetList loBudgetBean = new BudgetList("city_org");
		loBudgetBean.setOrgId("city_org");
		loBudgetBean.setOrgType("city_org");
		loBudgetBean.setLsRequestFromHomePage("true");
		List<String> llList = null;
		loBudgetBean.setBudgetStatusList(llList);
		loBudgetBean.setBudgetTypeList(llList);
		String lsUserType = "";
		loBudgetBean.setIsFilter(false);
		List<BudgetList> loList = financialsBudgetService.fetchBudgetListSummary(moSession, lsUserType, loBudgetBean);
		assertNull(loList);
	}
	
}
