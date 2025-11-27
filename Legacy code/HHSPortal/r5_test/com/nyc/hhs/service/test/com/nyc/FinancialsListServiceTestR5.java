package com.nyc.hhs.service.test.com.nyc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.FinancialsListService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ContractList;

public class FinancialsListServiceTestR5
{
	
	FinancialsListService financialsListService = new FinancialsListService();
	
	private static SqlSession moSession = null; // SQL Session
	
	private ContractList setContractListFilterParams()
	{
		ContractList loContractList = new ContractList();
		List<String> loContractStatusList = new ArrayList<String>();
		loContractStatusList.add("61");
		loContractStatusList.add("67");
		loContractStatusList.add("58");
		loContractStatusList.add("60");
		loContractStatusList.add("59");
		loContractStatusList.add("62");
		loContractList.setContractStatusList(loContractStatusList);
		loContractList.setStartNode(1);
		loContractList.setEndNode(5);
		loContractList.setFirstSort(HHSConstants.STATUS_PROCESS_TYPE_ID);
		loContractList.setSecondSort(HHSConstants.LAST_UPDATE_DATE);
		loContractList.setFirstSortDate(true);
		loContractList.setSecondSortDate(true);
		loContractList.setFirstSortType(HHSConstants.ASCENDING);
		loContractList.setSecondSortType(HHSConstants.DESCENDING);
		loContractList.setSortColumnName(HHSConstants.CONTRACT_STATUS);
		loContractList.setOrgName("accenture");
		loContractList.setOrgType("city_org");
		return loContractList;
	}
	
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
	public void fetchContractListSummaryCase1() throws ApplicationException
	{
		ContractList loContractList = new ContractList();
		List<String> loContractStatusList = new ArrayList<String>();
		loContractStatusList.add("67");
		loContractStatusList.add("58");
		loContractStatusList.add("60");
		loContractStatusList.add("59");
		loContractStatusList.add("62");
		loContractList.setContractStatusList(loContractStatusList);
		loContractList.setStartNode(1);
		loContractList.setEndNode(5);
		loContractList.setFirstSort(HHSConstants.STATUS_PROCESS_TYPE_ID);
		loContractList.setSecondSort(HHSConstants.LAST_UPDATE_DATE);
		loContractList.setFirstSortDate(true);
		loContractList.setSecondSortDate(true);
		loContractList.setFirstSortType(HHSConstants.ASCENDING);
		loContractList.setSecondSortType(HHSConstants.DESCENDING);
		loContractList.setSortColumnName(HHSConstants.CONTRACT_STATUS);
		loContractList.setOrgName("accenture");
		loContractList.setOrgType("city_org");
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = financialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.isEmpty());
	}
	
	@Test
	public void fetchContractListSummaryCase2() throws ApplicationException
	{
		ContractList loContractList = new ContractList();
		loContractList.setProvider("Provider");
		loContractList.setContractValueFrom("0");
		loContractList.setContractValueTo("0");
		List<String> loContractStatusList = new ArrayList<String>();
		loContractStatusList.add("61");
		loContractStatusList.add("67");
		loContractStatusList.add("58");
		loContractStatusList.add("60");
		loContractStatusList.add("59");
		loContractStatusList.add("62");
		loContractList.setContractStatusList(loContractStatusList);
		loContractList.setStartNode(1);
		loContractList.setEndNode(5);
		loContractList.setFirstSort(HHSConstants.STATUS_PROCESS_TYPE_ID);
		loContractList.setSecondSort(HHSConstants.LAST_UPDATE_DATE);
		loContractList.setFirstSortDate(true);
		loContractList.setSecondSortDate(true);
		loContractList.setFirstSortType(HHSConstants.ASCENDING);
		loContractList.setSecondSortType(HHSConstants.DESCENDING);
		loContractList.setSortColumnName(HHSConstants.CONTRACT_STATUS);
		loContractList.setOrgName("accenture");
		loContractList.setOrgType("city_org");
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = financialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.isEmpty());
	}
	
	@Test
	public void fetchContractListSummaryCase4() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = financialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.isEmpty());
	}
	
	@Test
	public void fetchContractListSummaryCase5() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = financialsListService.fetchContractListSummary(moSession,
				loContractList, "Accelerator");
		assertNotNull(loContractListReturned);
	}
	
	@Test
	public void fetchContractListSummaryCase6() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = financialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.isEmpty());
	}
	
	@Test
	public void fetchContractListSummaryCase7() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = financialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.isEmpty());
	}
	
	@Test
	public void fetchContractListSummaryCase8() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = financialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.isEmpty());
	}
	
	@Test
	public void fetchContractListSummaryCase9() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = financialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.isEmpty());
	}
	
	@Test
	public void fetchContractListSummaryCase10() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = financialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.isEmpty());
	}
	
	@Test
	public void fetchContractListSummaryCase11() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = financialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.isEmpty());
	}
	
	@Test
	public void fetchContractListSummaryCase12() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.PROVIDER_ORG);
		loContractList.setOrgName("r3_org");
		List<ContractList> loContractListReturned = financialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.PROVIDER_ORG);
		assertTrue(loContractListReturned.isEmpty());
	}
	
	@Test
	public void fetchContractListSummaryCase13() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.AGENCY_ORG);
		loContractList.setOrgName("DOC");
		List<ContractList> loContractListReturned = financialsListService.fetchContractListSummary(moSession,
				loContractList, ApplicationConstants.AGENCY_ORG);
		assertNotNull(loContractListReturned);
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchContractListSummaryCase14() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgType(ApplicationConstants.AGENCY_ORG);
		loContractList.setOrgName("DOC");
		financialsListService.fetchContractListSummary(null, loContractList, ApplicationConstants.AGENCY_ORG);
	}
	
	@Test
	public void getContractsCountCase1() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		Integer loContractListReturned = financialsListService.getContractsCount(moSession, loContractList,
				ApplicationConstants.CITY_ORG);
		assertNotNull(loContractListReturned);
	}
	
	@Test
	public void getContractsCountCase2() throws ApplicationException
	{
		ContractList loContractList = new ContractList();
		loContractList.setOrgName("r3_org");
		loContractList.setAgencyId("DOC");
		Integer loContractListReturned = financialsListService.getContractsCount(moSession, loContractList,
				ApplicationConstants.PROVIDER_ORG);
		assertNotNull(loContractListReturned);
	}
	
	@Test
	public void getContractsCountCase3() throws ApplicationException
	{
		ContractList loContractList = new ContractList();
		loContractList.setOrgName("r3_org");
		loContractList.setAgencyId("DOC");
		Integer loContractListReturned = financialsListService.getContractsCount(moSession, loContractList,
				ApplicationConstants.PROVIDER_ORG);
		assertNotNull(loContractListReturned);
	}
	
	@Test
	public void getContractsCountCase4() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		loContractList.setProvider("Provider");
		loContractList.setOrgName("DHS");
		Integer loContractListReturned = financialsListService.getContractsCount(moSession, loContractList,
				ApplicationConstants.AGENCY_ORG);
		assertTrue(loContractListReturned == 0);
	}
	
	@Test(expected = ApplicationException.class)
	public void getContractsCountCase5() throws ApplicationException
	{
		ContractList loContractList = setContractListFilterParams();
		loContractList.setOrgName("DHS");
		financialsListService.getContractsCount(null, loContractList, ApplicationConstants.AGENCY_ORG);
	}
	
	@Test
	public void fetchParentContractIdForCancelAllAwardsCase1() throws ApplicationException
	{
		String lsEvaluationPoolMappingId = "1963";
		String loContractListReturned = financialsListService.fetchParentContractIdForCancelAllAwards(moSession,
				lsEvaluationPoolMappingId);
		assertEquals(loContractListReturned, "6718");
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchParentContractIdForCancelAllAwardsCase2() throws ApplicationException
	{
		String lsEvaluationPoolMappingId = "1963";
		financialsListService.fetchParentContractIdForCancelAllAwards(null, lsEvaluationPoolMappingId);
	}
}
