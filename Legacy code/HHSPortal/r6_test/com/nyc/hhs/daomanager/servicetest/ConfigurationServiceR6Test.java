package com.nyc.hhs.daomanager.servicetest;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nyc.hhs.daomanager.service.ConfigurationService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.util.DAOUtil;
import org.apache.ibatis.session.SqlSession;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DAOUtil.class)
public class ConfigurationServiceR6Test
{
	ConfigurationService loConfigurationService = new ConfigurationService();
	private static SqlSession moSession = null; // SQL Session
	
	public String fiscalYearId = "2014";
	public String parentBudgetID = "13";
	public String agency = "agency_12";
	public String amendContractId = "63";
	
	
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
	
	private ContractBudgetBean getDummyContractBudgetBean()
	{
		ContractBudgetBean loSubBudgetBean = new ContractBudgetBean();
		loSubBudgetBean.setBudgetfiscalYear(fiscalYearId);
		loSubBudgetBean.setBudgetId(parentBudgetID);
		loSubBudgetBean.setBudgetTypeId(2);
		loSubBudgetBean.setContractValue("10000");
		loSubBudgetBean.setCreatedByUserId(agency);
		loSubBudgetBean.setId("2003");
		loSubBudgetBean.setPlannedAmount("3434");
		loSubBudgetBean.setSubbudgetName("testSubBudgetName");
		loSubBudgetBean.setTotalbudgetAmount("200");
		loSubBudgetBean.setActiveFlag("1");
		loSubBudgetBean.setBudgetfiscalYear(fiscalYearId);
		loSubBudgetBean.setBudgetStartDate("07/14/2014");
		loSubBudgetBean.setBudgetEndDate("07/14/2014");
		loSubBudgetBean.setContractId(amendContractId);
		loSubBudgetBean.setModifiedByUserId(agency);
		loSubBudgetBean.setStatusId("2");
		loSubBudgetBean.setSubbudgetAmount("1000");

		return loSubBudgetBean;
	}

	
	@Test(expected = ApplicationException.class)
	public void updateUsesFteCase1() throws ApplicationException
	
	{
		PowerMockito.mockStatic(DAOUtil.class);
		PowerMockito.when(DAOUtil.masterDAO(Mockito.any(SqlSession.class), Mockito.any(Object.class), Mockito.any(String.class), Mockito.any(String.class), Mockito.any(String.class))).thenThrow(new NullPointerException());
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setContractId("1234");
		loTaskBean.setTaskLevel("1");
		loTaskBean.setTotalLevel("1");
		loConfigurationService.updateUsesFte(null,loTaskBean);
		
	}
	@Test(expected = ApplicationException.class)
	public void updateUsesFteCase2() throws ApplicationException
	
	{
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setContractId("1234");
		loTaskBean.setTaskLevel("1");
		loTaskBean.setTotalLevel("1");
		loConfigurationService.updateUsesFte(null,loTaskBean);
		
	}
	@Test
	public void updateUsesFteCase3() throws ApplicationException
	{
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setContractId("1234");
		loTaskBean.setTaskLevel("1");
		loTaskBean.setTotalLevel("1");
		loConfigurationService.updateUsesFte(moSession,loTaskBean);
		
	}
	@Test
	public void updateUsesFteCase4() throws ApplicationException
	
	{
		TaskDetailsBean loTaskBean = new TaskDetailsBean();
		loTaskBean.setContractId("1234");
		loTaskBean.setTaskLevel("1");
		loTaskBean.setTotalLevel("2");
		loConfigurationService.updateUsesFte(moSession,loTaskBean);
		
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchNewFYSubBudgetDetails() throws ApplicationException
	{	
		String asContractId = "12716";
		String asFiscalYearId = "2017";
		loConfigurationService.fetchNewFYSubBudgetDetails(moSession, asContractId, asFiscalYearId);
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testEditContractConfSubBudgetDetails() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		Boolean lbResult = loConfigurationService.editContractConfSubBudgetDetails(moSession, loSubBudgetBean);
		assertNotNull(lbResult);
		assertTrue(lbResult);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testEditContractConfSubBudgetDetailsSetParentId() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setParentId("55");
		Boolean lbResult = loConfigurationService.editContractConfSubBudgetDetails(moSession, loSubBudgetBean);
		assertNotNull(lbResult);
		assertTrue(lbResult);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testEditContractConfSubBudgetDetailsSubBudgetNameNull() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setSubbudgetName(null);
		Boolean lbResult = loConfigurationService.editContractConfSubBudgetDetails(moSession, loSubBudgetBean);
		assertNotNull(lbResult);
		assertTrue(lbResult);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testEditContractConfSubBudgetDetailsBeanNull() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = null;
		loConfigurationService.editContractConfSubBudgetDetails(moSession, loSubBudgetBean);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testEditContractConfSubBudgetDetailsSetSubBudgetNameNParentId() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setSubbudgetName("Sub-Budget1");
		loSubBudgetBean.setParentId("55");
		Boolean lbResult = loConfigurationService.editContractConfSubBudgetDetails(moSession, loSubBudgetBean);
		assertNotNull(lbResult);
		assertTrue(lbResult);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testEditContractConfSubBudgetDetailsSetEmptyParentId() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setParentId("");
		Boolean lbResult = loConfigurationService.editContractConfSubBudgetDetails(moSession, loSubBudgetBean);
		assertNotNull(lbResult);
		assertTrue(lbResult);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testEditContractConfSubBudgetDetailsEmptySubBudgetName() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setSubbudgetName("");
		Boolean lbResult = loConfigurationService.editContractConfSubBudgetDetails(moSession, loSubBudgetBean);
		assertNotNull(lbResult);
		assertTrue(lbResult);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testEditContractConfSubBudgetDetailsForException() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loConfigurationService.editContractConfSubBudgetDetails(null, loSubBudgetBean);
	}


}
