package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.BudgetManagementService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.AccountsAllocationBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ProcurementCOF;

public class BudgetManagementServiceTest
{
	private static SqlSession moSession = null; // SQL Session

	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		Boolean lbThrown = false;
		try
		{
			moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
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
			moSession.close();
		}
		catch (Exception loEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testFetchContractConfCOADetails() throws ApplicationException
	{
		CBGridBean logridBean = new CBGridBean();
		logridBean.setContractID("111777");
		logridBean.setFiscalYearID("2014");
		BudgetManagementService loBudgetManagementService = new BudgetManagementService();
		ProcurementCOF loProcurementCOF = loBudgetManagementService.fetchContractCofDocDetails(moSession, "27");
		assertNotNull(loProcurementCOF);
	}

	@Test(expected = java.lang.Exception.class)
	public void testBudgetManagementServicefetchContractCofTaskDetails0Negative()
	{
		BudgetManagementService loBudgetManagementService = new BudgetManagementService();
		try
		{
			loBudgetManagementService.fetchContractCofTaskDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBudgetManagementServicefetchBaseAmendmentContractAmount1Negative()
	{
		BudgetManagementService loBudgetManagementService = new BudgetManagementService();
		try
		{
			loBudgetManagementService.fetchBaseAmendmentContractAmount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBudgetManagementServicefetchContractCofDocDetails2Negative()
	{
		BudgetManagementService loBudgetManagementService = new BudgetManagementService();
		try
		{
			loBudgetManagementService.fetchContractCofDocDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = ApplicationException.class)
	public void testBudgetManagementServicefetchContractCofTaskDetails0NegativeApp() throws ApplicationException
	{
		BudgetManagementService loBudgetManagementService = new BudgetManagementService();
		loBudgetManagementService.fetchContractCofTaskDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBudgetManagementServicefetchBaseAmendmentContractAmount1NegativeApp() throws ApplicationException
	{
		BudgetManagementService loBudgetManagementService = new BudgetManagementService();
		loBudgetManagementService.fetchBaseAmendmentContractAmount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBudgetManagementServicefetchContractCofDocDetails2NegativeApp() throws ApplicationException
	{
		BudgetManagementService loBudgetManagementService = new BudgetManagementService();
		loBudgetManagementService.fetchContractCofDocDetails(null, null);
	}

}
