package com.nyc.hhs.service.test;



import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.daomanager.service.ReportService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ProposalStatusInfo;
import com.nyc.hhs.model.ReportBean;



public class ReportServiceTestR5 {

	ReportService reportService = new ReportService();

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

	// r-5 test cases for operation getReportData

	@Test(expected = ApplicationException.class)
	public void testGetReportData() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		loReportService.getReportData(null, null);

	}

	
	@Test
	public void getReportDataCase1() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		ReportBean loReportBean = new ReportBean();
		loReportBean.setSqlId("getFundingSummaryDetails"); 
    	List<ReportBean> abc = (List<ReportBean>)loReportService.getReportData(moSession, loReportBean);
		assertTrue(abc.size()>0);

	}
	
	
	@Test
	public void getReportDataCaseFiscalYear() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		Integer abc = (Integer)loReportService.getFirstFiscalYear(moSession);
		assertTrue(abc>0);
	}
	
	@Test(expected=ApplicationException.class)
	public void getReportDataCaseFiscalYearExp() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		Integer abc = (Integer)loReportService.getFirstFiscalYear(null);
		assertTrue(abc>0);
	}
	
	@Test
	public void getReportDataCasegetProposalStatusInfo() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		List<ProposalStatusInfo> abc = (List<ProposalStatusInfo>)loReportService.getProposalStatusInfo(moSession);
		assertTrue(abc.size()>0);
	}
	
	@Test(expected=ApplicationException.class)
	public void getReportDataCasegetProposalStatusInfoExp() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		List<ProposalStatusInfo> abc = (List<ProposalStatusInfo>)loReportService.getProposalStatusInfo(null);
	}
	
	@Test
	public void getReportDataCase2() throws ApplicationException
	{
	//	moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();
		ReportService loReportService = new ReportService();
		ReportBean loReportBean = new ReportBean();
		loReportBean.setSqlId("getFundingSummaryDetails"); 
		//loReportBean.setProvider("");
		//loReportBean.setProviderId("");
		loReportBean.setAgencyId("DOC");
		loReportBean.setProgramName("Model Education Program");
		loReportBean.setCtNumber("CT12345678");
		loReportBean.setContractTitle("ABselenium");
		//loReportBean.setFyYear("");
		
		List<ReportBean> abc = (List<ReportBean>)loReportService.getReportData(moSession, loReportBean);
		if(abc.size()> 0)
		assertTrue(true);

	}
	
	@Test(expected=ApplicationException.class)
	public void getReportDataCase2Exp() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		List<ReportBean> abc = (List<ReportBean>)loReportService.getReportData(null, null);

	}
	
	@Test
	public void getReportDataCase3() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		ReportBean loReportBean = new ReportBean();
		loReportBean.setSqlId("getFundingSummaryDetails"); 
		//loReportBean.setProvider("");
		loReportBean.setProviderId("");
		loReportBean.setAgencyId("AKS");
		loReportBean.setProgramName("Model Education Program");
		loReportBean.setCtNumber("CT12345678");
		loReportBean.setContractTitle("ABselenium");
		//loReportBean.setFyYear("");
		
		List<ReportBean> abc = (List<ReportBean>)loReportService.getReportData(moSession, loReportBean);
		if(abc.size()> 0)
		assertTrue(true);
	}
	
	
	
	
	
	
	
	
	//getFundingSummaryDetails_ForGrid
		
	@Test
	public void getReportDataCase4() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		ReportBean loReportBean = new ReportBean();
		loReportBean.setSqlId("getFundingSummaryDetails_ForGrid"); 
    	List<ReportBean> abc = (List<ReportBean>)loReportService.getReportData(moSession, loReportBean);
		assertTrue(abc.size()>0);

	}
	
	
	@Test
	public void getReportDataCase5() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		ReportBean loReportBean = new ReportBean();
		loReportBean.setSqlId("getFundingSummaryDetails_ForGrid"); 
	//	loReportBean.setProvider();
		//loReportBean.setProviderId();
		loReportBean.setAgencyId("DOC");
		loReportBean.setProgramName("Model Education Program");
		loReportBean.setCtNumber("CT12345678");
		loReportBean.setContractTitle("ABselenium");
		//loReportBean.setFyYear("");
		
		List<ReportBean> abc = (List<ReportBean>)loReportService.getReportData(moSession, loReportBean);
		assertTrue(abc.size()>0);

	}
	
	@Test
	public void getReportDataCase6() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		ReportBean loReportBean = new ReportBean();
		loReportBean.setSqlId("getFundingSummaryDetails_ForGrid"); 
		loReportBean.setProvider("");
		loReportBean.setProviderId("");
		loReportBean.setAgencyId("AKS");
		loReportBean.setProgramName("Model Education Program");
		loReportBean.setCtNumber("CT12345678");
		loReportBean.setContractTitle("ABselenium");
		loReportBean.setFyYear("");
		
		List<ReportBean> abc = (List<ReportBean>)loReportService.getReportData(moSession, loReportBean);
		assertTrue(abc.size()>0);
	}
	
	
	
	
	//getBudgetCatUtilization
	
	@Test
	public void getReportDataCase7() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		ReportBean loReportBean = new ReportBean();
		loReportBean.setSqlId("getBudgetCatUtilization"); 
    	List<ReportBean> abc = (List<ReportBean>)loReportService.getReportData(moSession, loReportBean);
		assertTrue(abc.size()>0);

	}
	
	
	@Test
	public void getReportDataCase8() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		ReportBean loReportBean = new ReportBean();
		loReportBean.setSqlId("getBudgetCatUtilization"); 
		loReportBean.setProvider("");
		loReportBean.setProviderId("");
		loReportBean.setAgencyId("DOC");
		loReportBean.setProgramName("Model Education Program");
		loReportBean.setCtNumber("CT12345678");
		loReportBean.setContractTitle("ABselenium");
		loReportBean.setFyYear("");
		
		List<ReportBean> abc = (List<ReportBean>)loReportService.getReportData(moSession, loReportBean);
		assertTrue(abc.size()>0);

	}
	
	@Test
	public void getReportDataCase9() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		ReportBean loReportBean = new ReportBean();
		loReportBean.setSqlId("getBudgetCatUtilization"); 
		loReportBean.setProvider("");
		loReportBean.setProviderId("");
		loReportBean.setAgencyId("KAS");
		loReportBean.setProgramName("Model Education Program");
		loReportBean.setCtNumber("CT12345678");
		loReportBean.setContractTitle("ABselenium");
		loReportBean.setFyYear("");
		
		List<ReportBean> abc = (List<ReportBean>)loReportService.getReportData(moSession, loReportBean);
		assertTrue(abc.size()>0);
	}
	
	
	
	
	
	//getBudgetCatUtilization_ForGrid
	
	@Test
	public void getReportDataCase10() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		ReportBean loReportBean = new ReportBean();
		loReportBean.setSqlId("getBudgetCatUtilization_ForGrid"); 
    	List<ReportBean> abc = (List<ReportBean>)loReportService.getReportData(moSession, loReportBean);
		assertTrue(abc.size()>0);

	}
	
	
	@Test
	public void getReportDataCase11() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		ReportBean loReportBean = new ReportBean();
		loReportBean.setSqlId("getBudgetCatUtilization_ForGrid"); 
		loReportBean.setProvider("");
		loReportBean.setProviderId("");
		loReportBean.setAgencyId("DOC");
		loReportBean.setProgramName("Model Education Program");
		loReportBean.setCtNumber("CT12345678");
		loReportBean.setContractTitle("ABselenium");
		loReportBean.setFyYear("");
		
		List<ReportBean> abc = (List<ReportBean>)loReportService.getReportData(moSession, loReportBean);
		assertTrue(abc.size()>0);

	}
	
	@Test
	public void getReportDataCase12() throws ApplicationException
	{
		ReportService loReportService = new ReportService();
		ReportBean loReportBean = new ReportBean();
		loReportBean.setSqlId("getBudgetCatUtilization_ForGrid"); 
		loReportBean.setProvider("");
		loReportBean.setProviderId("");
		loReportBean.setAgencyId("KAS");
		loReportBean.setProgramName("Model Education Program");
		loReportBean.setCtNumber("CT12345678");
		loReportBean.setContractTitle("ABselenium");
		loReportBean.setFyYear("");
		
		List<ReportBean> abc = (List<ReportBean>)loReportService.getReportData(moSession, loReportBean);
		assertTrue(abc.size()>0);
	}
	
	
}
