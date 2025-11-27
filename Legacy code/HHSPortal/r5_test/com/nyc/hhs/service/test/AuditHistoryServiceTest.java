package com.nyc.hhs.service.test;



import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.AuditHistoryService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ApplicationAuditBean;
import com.nyc.hhs.model.CommentsHistoryBean;



public class AuditHistoryServiceTest {
	
	AuditHistoryService moAuditHistoryService = new AuditHistoryService();

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
	
	

	@Test(expected=ApplicationException.class)
	public void getFilingsCountExp() throws ApplicationException
	{
		moAuditHistoryService.getFilingsCount(null, null);
	}

	
	@Test()
	public void getFilingsCount() throws ApplicationException
	{
		ApplicationAuditBean aoApplicationAuditBean = new ApplicationAuditBean();
		aoApplicationAuditBean.setMsOrgId("Org_522");
		Integer loCount = moAuditHistoryService.getFilingsCount(aoApplicationAuditBean, moSession);
		assertTrue(loCount>0);
	}
	
	@Test(expected=ApplicationException.class)
	public void fetchOrganizationFilingsAuditViewExp() throws ApplicationException
	{
		moAuditHistoryService.fetchOrganizationFilingsAuditView(null, null);
	}

	@Test()
	public void fetchOrganizationFilingsAuditView() throws ApplicationException
	{
		ApplicationAuditBean aoApplicationAuditBean = new ApplicationAuditBean();
		aoApplicationAuditBean.setMsOrgId("Org_522");
		List<ApplicationAuditBean>  loApplicationAuditBean = moAuditHistoryService.fetchOrganizationFilingsAuditView(aoApplicationAuditBean, moSession);
		assertTrue(loApplicationAuditBean.size()>0);
	}
	
	
	
	@Test(expected=ApplicationException.class)
	public void fetchOrganizationFilingsAuditViewFilingDropDownExp() throws ApplicationException
	{
		moAuditHistoryService.fetchOrganizationFilingsAuditViewFilingDropDown(null, null);
	}

	@Test()
	public void fetchOrganizationFilingsAuditViewFilingDropDown() throws ApplicationException
	{
		ApplicationAuditBean aoApplicationAuditBean = new ApplicationAuditBean();
		aoApplicationAuditBean.setMsOrgId("Org_522");
		List<String>  loApplicationAuditBean = moAuditHistoryService.fetchOrganizationFilingsAuditViewFilingDropDown(aoApplicationAuditBean, moSession);
		assertTrue(loApplicationAuditBean.size()>0);
	}
	
	
	
	
	@Test(expected=ApplicationException.class)
	public void getFilingsInformationHomePageExp() throws ApplicationException
	{
		moAuditHistoryService.getFilingsInformationHomePage(null, null);
	}

	@Test()
	public void getFilingsInformationHomePage() throws ApplicationException
	{
		ApplicationAuditBean aoApplicationAuditBean = new ApplicationAuditBean();
		Map<String, Object> loFilingsMap = moAuditHistoryService.getFilingsInformationHomePage(moSession , "Org_522");
		assertTrue(loFilingsMap.size()>0);
	}
	
	@Test()
	public void getFilingsInformationHomePage2() throws ApplicationException
	{
		ApplicationAuditBean aoApplicationAuditBean = new ApplicationAuditBean();
		Map<String, Object> loFilingsMap = moAuditHistoryService.getFilingsInformationHomePage(moSession , "Org_509");
		assertTrue(loFilingsMap.size()>0);
	}

	@Test()
	public void getFilingsInformationHomePage3() throws ApplicationException
	{
		ApplicationAuditBean aoApplicationAuditBean = new ApplicationAuditBean();
		Map<String, Object> loFilingsMap = moAuditHistoryService.getFilingsInformationHomePage(moSession , "Org_511");
		assertTrue(loFilingsMap.size()>0);
	}

	@Test()
	public void getFilingsInformationHomePage4() throws ApplicationException
	{
		ApplicationAuditBean aoApplicationAuditBean = new ApplicationAuditBean();
		Map<String, Object> loFilingsMap = moAuditHistoryService.getFilingsInformationHomePage(moSession , "Org_514");
		assertTrue(loFilingsMap.size()>0);
	}

	@Test()
	public void getFilingsInformationHomePage5() throws ApplicationException
	{
		ApplicationAuditBean aoApplicationAuditBean = new ApplicationAuditBean();
		Map<String, Object> loFilingsMap = moAuditHistoryService.getFilingsInformationHomePage(moSession , "Org_515");
		assertTrue(loFilingsMap.size()>0);
	}


	@Test(expected=ApplicationException.class)
	public void fetchProposalTaskHistoryExp() throws ApplicationException
	{
		moAuditHistoryService.fetchProposalTaskHistory(null, null);
	}

	@Test()
	public void fetchProposalTaskHistory() throws ApplicationException
	{
		HashMap aoHMApplicationAudit = new HashMap();
		aoHMApplicationAudit.put("proposalId", 3221);
		aoHMApplicationAudit.put("ENTITY_TYPE", "1212");
		List<CommentsHistoryBean> loResultList = moAuditHistoryService.fetchProposalTaskHistory(aoHMApplicationAudit , moSession);
	}
	
	
	
	
	
	
	
	
	
	
}
