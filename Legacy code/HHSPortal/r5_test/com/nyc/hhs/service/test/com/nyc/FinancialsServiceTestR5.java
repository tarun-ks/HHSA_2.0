package com.nyc.hhs.service.test.com.nyc;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.FinancialsService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ContractList;

public class FinancialsServiceTestR5
{
	
	FinancialsService financialsService = new FinancialsService();
	
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
	public void fetchAmendmentListSummaryCase1() throws ApplicationException
	{
		ContractList loContractFilterBean = new ContractList();
		List<String> lsList = new ArrayList<String>();
		lsList.add("130");
		loContractFilterBean.setContractStatusList(lsList);
		loContractFilterBean.setOrgName("r3_org");
		loContractFilterBean.setContractStatus(HHSConstants.SENT_FOR_REG);
		loContractFilterBean.setStartNode(1);
		loContractFilterBean.setEndNode(10);
		loContractFilterBean.setProvider("Provider");
		loContractFilterBean.setOrgName("DOC");
		loContractFilterBean.setOrgType("agency_org");
		List<ContractList> loContractList = financialsService.fetchAmendmentListSummary(moSession,
				loContractFilterBean, "agency_org");
		assertTrue(loContractList.size() == 0);
	}
	
	@Test
	public void fetchAmendmentListSummaryCase2() throws ApplicationException
	{
		ContractList loContractFilterBean = new ContractList();
		List<String> lsList = new ArrayList<String>();
		lsList.add("130");
		loContractFilterBean.setContractStatusList(lsList);
		loContractFilterBean.setOrgName("r3_org");
		loContractFilterBean.setStartNode(1);
		loContractFilterBean.setEndNode(10);
		loContractFilterBean.setContractValueFrom("-99999999");
		loContractFilterBean.setContractValueTo("999999");
		loContractFilterBean.setOrgName("DOC");
		loContractFilterBean.setOrgType("agency_org");
		List<ContractList> loContractList = financialsService.fetchAmendmentListSummary(moSession,
				loContractFilterBean, "provider_org");
		assertTrue(loContractList.size() == 0);
	}
	
	@Test
	public void fetchAmendmentListSummaryCase3() throws ApplicationException
	{
		ContractList loContractFilterBean = new ContractList();
		String abc[] =
		{ "61", "62" };
		loContractFilterBean.setContractStatusList(Arrays.asList(abc));
		loContractFilterBean.setOrgName("r3_org");
		loContractFilterBean.setOrgType("provider_org");
		loContractFilterBean.setStartNode(1);
		loContractFilterBean.setEndNode(10);
		loContractFilterBean.setContractValueFrom("-99999999");
		loContractFilterBean.setContractValueTo("999999");
		loContractFilterBean.setOrgName("DOC");
		loContractFilterBean.setOrgType("city_org");
		List<ContractList> loContractList = financialsService.fetchAmendmentListSummary(moSession,
				loContractFilterBean, "city_org");
		assertTrue(loContractList.size() == 0);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchAmendmentListSummaryCase4() throws ApplicationException
	{
		ContractList loContractFilterBean = new ContractList();
		String abc[] =
		{ "61", "62" };
		loContractFilterBean.setContractStatusList(Arrays.asList(abc));
		loContractFilterBean.setOrgName("r3_org");
		loContractFilterBean.setOrgType("provider_org");
		loContractFilterBean.setStartNode(1);
		loContractFilterBean.setEndNode(10);
		loContractFilterBean.setContractValueFrom("-99999999");
		loContractFilterBean.setContractValueTo("999999");
		loContractFilterBean.setOrgName("DOC");
		loContractFilterBean.setOrgType("city_org");
		List<ContractList> loContractList = financialsService.fetchAmendmentListSummary(null, loContractFilterBean,
				"city_org");
	}
	
	@Test
	public void getContractsCountCase1() throws ApplicationException
	{
		ContractList loContractFilterBean = new ContractList();
		String abc[] =
		{ "61", "62" };
		loContractFilterBean.setContractStatusList(Arrays.asList(abc));
		loContractFilterBean.setOrgName("r3_org");
		loContractFilterBean.setOrgType("agency_org");
		loContractFilterBean.setStartNode(1);
		loContractFilterBean.setEndNode(10);
		loContractFilterBean.setContractValueFrom("-99999999");
		loContractFilterBean.setContractValueTo("999999");
		loContractFilterBean.setOrgName("DOC");
		loContractFilterBean.setOrgType("agency_org");
		Integer loContractCount = financialsService.getContractsCount(moSession, loContractFilterBean, "agency_org");
		assertTrue(loContractCount == 0);
	}
	
	@Test
	public void getContractsCountCase2() throws ApplicationException
	{
		ContractList loContractFilterBean = new ContractList();
		String abc[] =
		{ "61", "62" };
		loContractFilterBean.setContractStatusList(Arrays.asList(abc));
		loContractFilterBean.setOrgName("r3_org");
		loContractFilterBean.setOrgType("provider_org");
		loContractFilterBean.setStartNode(1);
		loContractFilterBean.setEndNode(10);
		loContractFilterBean.setContractValueFrom("-99999999");
		loContractFilterBean.setContractValueTo("999999");
		loContractFilterBean.setOrgName("DOC");
		loContractFilterBean.setOrgType("provider_org");
		Integer loContractCount = financialsService.getContractsCount(moSession, loContractFilterBean, "provider_org");
		assertTrue(loContractCount == 0);
	}
	
	@Test
	public void getContractsCountCase3() throws ApplicationException
	{
		ContractList loContractFilterBean = new ContractList();
		String abc[] =
		{ "61", "62" };
		loContractFilterBean.setContractStatusList(Arrays.asList(abc));
		loContractFilterBean.setOrgName("r3_org");
		loContractFilterBean.setOrgType("city_org");
		loContractFilterBean.setStartNode(1);
		loContractFilterBean.setEndNode(10);
		loContractFilterBean.setContractValueFrom("-99999999");
		loContractFilterBean.setContractValueTo("999999");
		loContractFilterBean.setProvider("Provider");
		loContractFilterBean.setOrgName("DOC");
		loContractFilterBean.setOrgType("city_org");
		Integer loContractCount = financialsService.getContractsCount(moSession, loContractFilterBean, "city_org");
		assertTrue(loContractCount == 0);
	}
	
	@Test(expected = ApplicationException.class)
	public void getContractsCountCase4() throws ApplicationException
	{
		ContractList loContractFilterBean = new ContractList();
		String abc[] =
		{ "61", "62" };
		loContractFilterBean.setContractStatusList(Arrays.asList(abc));
		loContractFilterBean.setOrgName("r3_org");
		loContractFilterBean.setOrgType("agency_org");
		loContractFilterBean.setStartNode(1);
		loContractFilterBean.setEndNode(10);
		loContractFilterBean.setContractValueFrom("-99999999");
		loContractFilterBean.setContractValueTo("999999");
		loContractFilterBean.setOrgName("DOC");
		loContractFilterBean.setOrgType("agency_org");
		Integer loContractCount = financialsService.getContractsCount(null, loContractFilterBean, "agency_org");
	}
}
