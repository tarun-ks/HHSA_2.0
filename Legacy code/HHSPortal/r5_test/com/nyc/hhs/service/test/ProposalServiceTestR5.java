package com.nyc.hhs.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSR5Constants;
import com.nyc.hhs.daomanager.service.ProposalService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ProposalDetailsBean;

public class ProposalServiceTestR5
{
	
	ProposalService proposalService = new ProposalService();
	
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
	public void fetchCompetitionPoolListCase1() throws ApplicationException
	{
		HashMap<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("asProcurementId", "3924");
		loParamMap.put("toSearch", "pool");
		HashMap<String, Object> loProposalMap = new HashMap<String, Object>();
		assertNotNull(proposalService.fetchCompetitionPoolList(moSession, loParamMap));
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchCompetitionPoolListCase2() throws ApplicationException
	{
		HashMap<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("asProcurementId", "3924");
		loParamMap.put("toSearch", "pool");
		HashMap<String, Object> loProposalMap = new HashMap<String, Object>();
		proposalService.fetchCompetitionPoolList(null, loParamMap);
	}
	
	@Test
	public void fetchProcurementTitleListCase1() throws ApplicationException
	
	{
		HashMap<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("toSearch", "Auto Test");
		HashMap<String, Object> loProposalMap = new HashMap<String, Object>();
		assertNotNull(proposalService.fetchProcurementTitleList(moSession, loParamMap));
	}
	
	@Test(expected = ApplicationException.class)
	public void fetchProcurementTitleListCase2() throws ApplicationException
	
	{
		HashMap<String, String> loParamMap = new HashMap<String, String>();
		loParamMap.put("toSearch", "Auto Test");
		HashMap<String, Object> loProposalMap = new HashMap<String, Object>();
		assertNotNull(proposalService.fetchProcurementTitleList(null, loParamMap));
	}
	
	@Test(expected = ApplicationException.class)
	public void getProposalDataCase1() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = new ProposalDetailsBean();
		loProposalDetailsBean.setOrganizationId("Org_509");
		loProposalDetailsBean.setStartNode(1);
		loProposalDetailsBean.setEndNode(5);
		loProposalDetailsBean.setFirstSortType("procurementtitle");
		loProposalDetailsBean.setSecondSortType("proposalTitle");
		loProposalDetailsBean.setFirstSort("true");
		HashMap<String, Object> loProposalMap = new HashMap<String, Object>();
		proposalService.getProposalData(null, loProposalDetailsBean);
	}
	
	@Test
	public void getProposalDataCase2() throws ApplicationException
	
	{
		ProposalDetailsBean loProposalDetailsBean = new ProposalDetailsBean();
		loProposalDetailsBean.setOrganizationId("Org_509");
		loProposalDetailsBean.setFirstSortType(ApplicationConstants.DESCENDING);
		loProposalDetailsBean.setSecondSortType(ApplicationConstants.ASCENDING);
		loProposalDetailsBean.setFirstSort(HHSR5Constants.MODIFIED_DATE);
		loProposalDetailsBean.setSecondSort(HHSR5Constants.AGENCY_ID);
		loProposalDetailsBean.setFirstSortDate(true);
		loProposalDetailsBean.setSecondSortDate(true);
		loProposalDetailsBean.setUserRole("city_org");
		loProposalDetailsBean.setModifiedDate(null);
		proposalService.getProposalData(moSession, loProposalDetailsBean);
	}
	
	@Test
	public void getProposalCountDataCase1() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = new ProposalDetailsBean();
		loProposalDetailsBean.setOrganizationId("Org_509");
		loProposalDetailsBean.setStartNode(1);
		loProposalDetailsBean.setEndNode(5);
		loProposalDetailsBean.setFirstSortType("procurementtitle");
		loProposalDetailsBean.setSecondSortType("proposalTitle");
		loProposalDetailsBean.setFirstSort("true");
		List<String> loList = new ArrayList<String>();
		loList.add("");
		loProposalDetailsBean.setProposalStatusList(loList);
		HashMap<String, Object> loProposalMap = new HashMap<String, Object>();
		String value = proposalService.getProposalCountData(moSession, loProposalDetailsBean);
		assertEquals("0", value);
	}
	
	@Test(expected = ApplicationException.class)
	public void getProposalCountDataCase2() throws ApplicationException
	{
		ProposalDetailsBean loProposalDetailsBean = new ProposalDetailsBean();
		loProposalDetailsBean.setOrganizationId("Org_509");
		loProposalDetailsBean.setStartNode(1);
		loProposalDetailsBean.setEndNode(5);
		loProposalDetailsBean.setFirstSortType("procurementtitle");
		loProposalDetailsBean.setSecondSortType("proposalTitle");
		loProposalDetailsBean.setFirstSort("true");
		List<String> loList = new ArrayList<String>();
		loList.add("");
		loProposalDetailsBean.setProposalStatusList(loList);
		HashMap<String, Object> loProposalMap = new HashMap<String, Object>();
		String value = proposalService.getProposalCountData(null, loProposalDetailsBean);
	}
}
