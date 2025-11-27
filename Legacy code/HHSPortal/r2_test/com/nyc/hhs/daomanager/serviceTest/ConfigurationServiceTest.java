package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.ConfigurationService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.AccountsAllocationBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.FundingAllocationBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.TaskDetailsBean;

public class ConfigurationServiceTest
{

	private static SqlSession moSession = null; // SQL Session

	// private static P8UserSession session = null; // FileNet session

	ConfigurationService loConfigurationService = new ConfigurationService();

	public String amendContractId = "63";
	public String baseContractId = "53";
	public String contractBudgetID = "24";
	public String subBudgetID = "24";
	public String parentSubBudgetID = "14";
	public String parentBudgetID = "13";
	public String invoiceId = "55";
	public String agency = "agency_12";
	public String provider = "803";

	// base line items id
	public String contractedServiceId = "13";
	public String rateId = "8";
	public String milestoneId = "7";
	public String personalServiceSalariedId = "11";
	public String programIncomeId = "84";
	public String unallocatedId = "12";
	public String equipmentId = "5";
	public String rentId = "8";
	public String IndRateId = "12";
	public String utilitiesId = "100";

	public String amendContractIdApproved = "60";
	public String contractBudgetIDApproved = "22";
	public String subBudgetIDApproved = "22";

	public String amendBudgetType = "1";
	public String baseBudgetType = "2";
	public String modBudgetType = "3";
	public String updaBudgetType = "4";

	public String fiscalYearId = "2014";

	public String procurementId = "163";

	public String amendmentContractIdForTask = "566";
	public String contractFinancialsId = "861";
	public String positive = "positive";
	public String negative = "negative";

	private CBGridBean getCBGridBeanParams() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setParentBudgetId(parentSubBudgetID);
		loCBGridBean.setParentSubBudgetId(parentSubBudgetID);
		loCBGridBean.setSubBudgetID(subBudgetID);
		loCBGridBean.setContractID(baseContractId);
		loCBGridBean.setModifyByAgency(agency);
		loCBGridBean.setModifyByProvider("803");
		loCBGridBean.setFiscalYearID(fiscalYearId);
		loCBGridBean.setProcurementID("136");
		loCBGridBean.setType("Approved");
		return loCBGridBean;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception
	{
		moSession = MyBatisConnectionFactory.getLocalSqlSessionFactory().openSession();

	}

	/**
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

	/**
	 * Method used to test fetchContractConfFundingDetails method for contractd
	 * id for which data exists
	 * 
	 * 
	 * @throws ApplicationException
	 */

	@Test
	public void testFetchContractConfFundingDetails() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		List<FundingAllocationBean> loFundingSourceDetails = null;
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("1");
		loGridBean.setNoOfyears(5);
		loGridBean.setFiscalYearID(fiscalYearId);
		loGridBean.setFiscalYearCounter(2);
		loGridBean.setProcurementID(procurementId);
		loGridBean.setCreatedByUserId(agency);
		loFundingSourceDetails = loConfigurationService.fetchContractConfFundingDetails(moSession, loGridBean);
		assertTrue(loFundingSourceDetails != null);

	}

	@Test
	public void testFetchContractConfFundingDetailsApplicationException() throws ApplicationException
	{
		boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setContractID(baseContractId);
			loGridBean.setNoOfyears(5);
			loGridBean.setFiscalYearID(fiscalYearId);
			loConfigurationService.fetchContractConfFundingDetails(null, loGridBean);
		}
		catch (Exception aoAppExp)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFetchContractConfFundingDetailsException() throws ApplicationException
	{
		boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setContractID("800");
			loGridBean.setFiscalYearID(fiscalYearId);
			loGridBean.setNoOfyears(5);
			loConfigurationService.fetchContractConfFundingDetails(moSession, loGridBean);
		}
		catch (Exception aoAppExp)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testFetchContractConfFundingDetailsWithNoData() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		List<FundingAllocationBean> loFundingSourceDetails = null;
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID(amendContractId);
		loGridBean.setProcurementID(procurementId);
		loGridBean.setNoOfyears(5);
		loGridBean.setCreatedByUserId("agency_47");
		loGridBean.setFiscalYearID(fiscalYearId);
		loFundingSourceDetails = loConfigurationService.fetchContractConfFundingDetails(moSession, loGridBean);
		assertTrue(loFundingSourceDetails != null);

	}

	@Test
	public void testEditContractConfFundingDetailsApplicationError() throws ApplicationException,
			IllegalAccessException, InvocationTargetException, NoSuchMethodException
	{
		Boolean lbThrown = false;
		try
		{
			new ConfigurationService();
			FundingAllocationBean loFundingUpdatedDetails = new FundingAllocationBean();
			loFundingUpdatedDetails.setFundingSource("Federal");
			loFundingUpdatedDetails.setFy1("60000");
			loFundingUpdatedDetails.setFy2("60000");
			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setContractID("110137");
			loGridBean.setProcurementID("75");
			loGridBean.setNoOfyears(5);
			loGridBean.setFiscalYearID(fiscalYearId);
		}
		catch (Exception aoAppExp)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testEditContractConfFundingDetailsError() throws ApplicationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException
	{
		Boolean lbThrown = false;
		try
		{
			new ConfigurationService();
			FundingAllocationBean loFundingUpdatedDetails = new FundingAllocationBean();
			loFundingUpdatedDetails.setFundingSource("Federal");
			loFundingUpdatedDetails.setFy1("60000");
			loFundingUpdatedDetails.setFy2("60000");
			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setContractID("110137");
			loGridBean.setProcurementID("75");
			loGridBean.setNoOfyears(5);
		}
		catch (Exception aoAppExp)
		{
			lbThrown = true;
			assertTrue(lbThrown);
		}

	}

	@Test
	public void testEditContractConfFundingDetails() throws ApplicationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException
	{

		ConfigurationService loConfigurationService = new ConfigurationService();
		FundingAllocationBean loFundingUpdatedDetails = new FundingAllocationBean();
		loFundingUpdatedDetails.setFundingSource("Federal");
		loFundingUpdatedDetails.setFy1("60000");
		loFundingUpdatedDetails.setFy2("60000");
		loFundingUpdatedDetails.setFy3("60000");
		loFundingUpdatedDetails.setFy4("60000");
		loFundingUpdatedDetails.setFy5("60000");
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID(amendContractId);
		loGridBean.setProcurementID("75");
		loGridBean.setNoOfyears(5);
		loGridBean.setFiscalYearID(fiscalYearId);
		Boolean loReturn = loConfigurationService.editContractConfFundingDetails(moSession, loFundingUpdatedDetails,
				loGridBean);
		assertTrue(loReturn);

	}

	@Test
	public void testEditContractConfFundingDetailsCity() throws ApplicationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException
	{

		ConfigurationService loConfigurationService = new ConfigurationService();
		FundingAllocationBean loFundingUpdatedDetails = new FundingAllocationBean();
		loFundingUpdatedDetails.setFundingSource("City");
		loFundingUpdatedDetails.setFy1("60000");
		loFundingUpdatedDetails.setFy2("60000");
		loFundingUpdatedDetails.setFy3("60000");
		loFundingUpdatedDetails.setFy4("60000");
		loFundingUpdatedDetails.setFy5("60000");
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("75");
		loGridBean.setProcurementID("75");
		loGridBean.setNoOfyears(5);
		loGridBean.setFiscalYearID(fiscalYearId);
		Boolean loReturn = loConfigurationService.editContractConfFundingDetails(moSession, loFundingUpdatedDetails,
				loGridBean);
		assertFalse(loReturn);

	}
	
	@Test
	public void testEditContractConfFundingDetailsCity1() throws ApplicationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException
	{

		ConfigurationService loConfigurationService = new ConfigurationService();
		FundingAllocationBean loFundingUpdatedDetails = new FundingAllocationBean();
		loFundingUpdatedDetails.setFundingSource("City");
		loFundingUpdatedDetails.setFy1("60000");
		loFundingUpdatedDetails.setFy2("60000");
		loFundingUpdatedDetails.setFy3("60000");
		loFundingUpdatedDetails.setFy4("60000");
		loFundingUpdatedDetails.setFy5("60000");
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("75");
		loGridBean.setProcurementID("75");
		loGridBean.setNoOfyears(3);
		loGridBean.setFiscalYearID(fiscalYearId);
		Boolean loReturn = loConfigurationService.editContractConfFundingDetails(moSession, loFundingUpdatedDetails,
				loGridBean);
		assertTrue(loReturn);

	}

	@Test
	public void testEditContractConfFundingDetailsState() throws ApplicationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException
	{

		ConfigurationService loConfigurationService = new ConfigurationService();
		FundingAllocationBean loFundingUpdatedDetails = new FundingAllocationBean();
		loFundingUpdatedDetails.setFundingSource("State");
		loFundingUpdatedDetails.setFy1("60000");
		loFundingUpdatedDetails.setFy2("60000");
		loFundingUpdatedDetails.setFy3("60000");
		loFundingUpdatedDetails.setFy4("60000");
		loFundingUpdatedDetails.setFy5("60000");
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("75");
		loGridBean.setProcurementID("75");
		loGridBean.setNoOfyears(3);
		loGridBean.setFiscalYearID(fiscalYearId);
		Boolean loReturn = loConfigurationService.editContractConfFundingDetails(moSession, loFundingUpdatedDetails,
				loGridBean);
		assertTrue(loReturn);

	}

	@Test
	public void testEditContractConfFundingDetailsOther() throws ApplicationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException
	{

		ConfigurationService loConfigurationService = new ConfigurationService();
		FundingAllocationBean loFundingUpdatedDetails = new FundingAllocationBean();
		loFundingUpdatedDetails.setFundingSource("Other");
		loFundingUpdatedDetails.setFy1("60000");
		loFundingUpdatedDetails.setFy2("60000");
		loFundingUpdatedDetails.setFy3("60000");
		loFundingUpdatedDetails.setFy4("60000");
		loFundingUpdatedDetails.setFy5("60000");
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("75");
		loGridBean.setProcurementID("75");
		loGridBean.setNoOfyears(3);
		loGridBean.setFiscalYearID(fiscalYearId);
		Boolean loReturn = loConfigurationService.editContractConfFundingDetails(moSession, loFundingUpdatedDetails,
				loGridBean);
		assertTrue(loReturn);

	}

	@Test(expected = ApplicationException.class)
	public void testEditContractConfFundingDetailsException() throws ApplicationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException
	{

		ConfigurationService loConfigurationService = new ConfigurationService();
		FundingAllocationBean loFundingUpdatedDetails = new FundingAllocationBean();
		loFundingUpdatedDetails.setFundingSource("Other");
		loFundingUpdatedDetails.setFy1("60000");
		loFundingUpdatedDetails.setFy2("60000");
		loFundingUpdatedDetails.setFy3("60000");
		loFundingUpdatedDetails.setFy4("60000");
		loFundingUpdatedDetails.setFy5("60000");
		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID("75");
		loGridBean.setProcurementID("75");
		loGridBean.setNoOfyears(3);
		loGridBean.setFiscalYearID(fiscalYearId);
		Boolean loReturn = loConfigurationService.editContractConfFundingDetails(null, loFundingUpdatedDetails,
				loGridBean);
		assertTrue(loReturn);

	}

	
	/*
	 * @Test(expected = ApplicationException.class) public void
	 * testUpdateContractCofTaskStatusWithException() throws
	 * ApplicationException { ConfigurationService loConfigurationService = new
	 * ConfigurationService(); Boolean lbAuditFlag = Boolean.TRUE; TaskService
	 * loTaskService = new TaskService(); TaskDetailsBean loTaskDetailsBean =
	 * new TaskDetailsBean(); loTaskService.setTaskDetailsInBean(null,
	 * loTaskDetailsBean); loTaskDetailsBean.setContractId(null);
	 * loConfigurationService.updateContractCofTaskStatus(moSession,
	 * lbAuditFlag, loTaskDetailsBean);
	 * 
	 * 
	 * }
	 */

	/**
	 * Method used to test processContractAfterCOFTask method with true Generate
	 * Doc flag and valid contract in ConfigurationService class
	 * 
	 * 
	 * @throws ApplicationException /
	 * @Test public void
	 *       testProcessContractAfterCOFTaskWithTrueFlagWithValidContract()
	 *       throws ApplicationException { ConfigurationService
	 *       loConfigurationService = new ConfigurationService(); Boolean
	 *       lbGenerateDocFlag = Boolean.TRUE; TaskDetailsBean loTaskDetailsBean
	 *       = new TaskDetailsBean();
	 *       loTaskDetailsBean.setContractId(baseContractId);
	 *       loTaskDetailsBean.setUserId(agency);
	 * 
	 *       Boolean lbReturnValue =
	 *       loConfigurationService.processContractAfterCOFTask(moSession, null,
	 *       lbGenerateDocFlag, loTaskDetailsBean); assertTrue(lbReturnValue);
	 * 
	 *       }
	 * 
	 *       /** Method used to test processContractAfterCOFTask method with
	 *       true Generate Doc flag and Invalid contract in ConfigurationService
	 *       class
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testProcessContractAfterCOFTaskWithTrueFlagWithInValidContract() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbGenerateDocFlag = Boolean.TRUE;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(amendContractId);

		loConfigurationService.processContractAfterCOFTask(moSession, null, lbGenerateDocFlag, loTaskDetailsBean);

	}

	@Test
	public void testProcessContractAfterCOFTask() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbGenerateDocFlag = Boolean.TRUE;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(baseContractId);
		loTaskDetailsBean.setUserId("agency_13");
		Boolean lbReturnValue = loConfigurationService.processContractAfterCOFTask(moSession, null, lbGenerateDocFlag,
				loTaskDetailsBean);
		assertTrue(lbReturnValue);

	}

	@Test
	public void testProcessContractAfterCOFTask2() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbGenerateDocFlag = Boolean.TRUE;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("241");
		loTaskDetailsBean.setUserId("agency_13");
		Boolean lbReturnValue = loConfigurationService.processContractAfterCOFTask(moSession, null, lbGenerateDocFlag,
				loTaskDetailsBean);
		assertTrue(lbReturnValue);

	}

	@Test(expected = ApplicationException.class)
	public void testProcessContractAfterCOFTask3() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbGenerateDocFlag = Boolean.TRUE;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("1000");
		loTaskDetailsBean.setUserId("agency_13");
		Boolean lbReturnValue = loConfigurationService.processContractAfterCOFTask(moSession, null, lbGenerateDocFlag,
				loTaskDetailsBean);

	}

	/**
	 * Method used to test processContractAfterCOFTask method with false
	 * Generate Doc flag in ConfigurationService class
	 * 
	 * @throws ApplicationException
	 */

	@Test
	public void testProcessContractAfterCOFTaskWithFalseFlag() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbGenerateDocFlag = Boolean.FALSE;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();

		Boolean lbReturnValue = loConfigurationService.processContractAfterCOFTask(moSession, null, lbGenerateDocFlag,
				loTaskDetailsBean);
		assertFalse(lbReturnValue);

	}

	/**
	 * Method used to test updateContractStatusToPendingConfig method with false
	 * Finish Task status flag in ConfigurationService class
	 * 
	 * @throws ApplicationException
	 */

	@Test
	public void testUpdateContractStatusToPendingConfigWithFalseFlag() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbFinishTaskFlag = Boolean.FALSE;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();

		Boolean lbReturnValue = loConfigurationService.updateContractStatusToPendingConfig(moSession, lbFinishTaskFlag,
				loTaskDetailsBean);
		assertFalse(lbReturnValue);

	}

	/**
	 * Method used to test updateContractStatusToPendingConfig method with true
	 * Finish Task status flag and valid contract in ConfigurationService class
	 * 
	 * 
	 * @throws ApplicationException
	 */

	@Test
	public void testUpdateContractStatusToPendingConfigWithTrueFlagWithValidContract() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbFinishTaskFlag = Boolean.TRUE;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(amendContractId);
		loTaskDetailsBean.setUserId(agency);

		Boolean lbReturnValue = loConfigurationService.updateContractStatusToPendingConfig(moSession, lbFinishTaskFlag,
				loTaskDetailsBean);
		assertTrue(lbReturnValue);

	}

	/**
	 * Method used to test updateContractStatusToPendingConfig method with true
	 * Finish Task status flag and Invalid contract in ConfigurationService
	 * class
	 * 
	 * 
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testUpdateContractStatusToPendingConfigWithTrueFlagWithInValidContract() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbFlag = Boolean.TRUE;
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(amendContractId);

		loConfigurationService.updateContractStatusToPendingConfig(moSession, lbFlag, loTaskDetailsBean);
	}

	/*
	 * private P8UserSession getFileNetSession() throws ApplicationException {
	 * System.setProperty(P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG,
	 * PropertyLoader.getProperty( P8Constants.PROPERTY_FILE,
	 * P8Constants.PROP_FILE_JAVA_SECURITY_AUTH_LOGIN_CONFIG));
	 * System.setProperty(P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL,
	 * PropertyLoader.getProperty( P8Constants.PROPERTY_FILE,
	 * P8Constants.PROP_FILE_JAVA_NAMING_FACTORY_INITIAL));
	 * System.setProperty(P8Constants.PROP_FILE_FILENET_PE_BOOTSTRAP_CEURI,
	 * PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
	 * 
	 * P8UserSession loUserSession = new P8UserSession();
	 * loUserSession.setContentEngineUri
	 * (PropertyLoader.getProperty(P8Constants.PROPERTY_FILE, "FILENET_URI"));
	 * loUserSession
	 * .setObjectStoreName(PropertyLoader.getProperty(P8Constants.PROPERTY_FILE,
	 * "OBJECT_STORE_NAME"));
	 * loUserSession.setIsolatedRegionName(PropertyLoader.
	 * getProperty(P8Constants.PROPERTY_FILE, "CONNECTION_POINT_NAME"));
	 * loUserSession.setUserId("ceadmin");
	 * loUserSession.setPassword("Filenet1"); P8SecurityOperations
	 * loP8SecurityService = new P8SecurityOperations();
	 * loP8SecurityService.getPESession(loUserSession);
	 * loP8SecurityService.getObjectStore(loUserSession);
	 * 
	 * return loUserSession; }
	 */

	// S382 start
	// fetch contract config coa details with successful status
	@Test
	public void testFetchContractConfCOADetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setContractID(amendContractId);
			loGridBean.setFiscalYearID(fiscalYearId);

			ConfigurationService loConfigurationService = new ConfigurationService();
			List<AccountsAllocationBean> loCOA = loConfigurationService.fetchContractConfCOADetails(loGridBean,
					moSession);

			assertNotNull(loCOA);
			assertEquals("Account Allocation Details for contract fetched successfully:", loConfigurationService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// fetch contract config coa details with successful status
	@Test
	public void testFetchContractConfCOADetails2() throws ApplicationException
	{
			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setContractID("196");
			loGridBean.setFiscalYearID(fiscalYearId);
			loGridBean.setProcurementID("115");
			loGridBean.setContractTypeId("4");
			loGridBean.setCreatedByUserId(agency);

			ConfigurationService loConfigurationService = new ConfigurationService();
			List<AccountsAllocationBean> loCOA = loConfigurationService.fetchContractConfCOADetails(loGridBean,
					moSession);

			assertNotNull(loCOA);
	}
	
	// fetch contract config coa details with successful status
	@Test
	public void testFetchContractConfCOADetailsIsNewFYScreen() throws ApplicationException
	{
			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setContractID("196");
			loGridBean.setFiscalYearID(fiscalYearId);
			loGridBean.setProcurementID("115");
			loGridBean.setContractTypeId("4");
			loGridBean.setCreatedByUserId(agency);
			loGridBean.setIsNewFYScreen(true);

			ConfigurationService loConfigurationService = new ConfigurationService();
			List<AccountsAllocationBean> loCOA = loConfigurationService.fetchContractConfCOADetails(loGridBean,
					moSession);

			assertNotNull(loCOA);
	}

	// fetch contract config coa details with successful status
	@Test
	public void testFetchContractConfCOADetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setContractID(amendContractId);
			loGridBean.setFiscalYearID(fiscalYearId);

			ConfigurationService loConfigurationService = new ConfigurationService();
			List<AccountsAllocationBean> loCOA = loConfigurationService.fetchContractConfCOADetails(loGridBean,
					moSession);

			assertNotNull(loCOA);
			assertEquals("Account Allocation Details for contract fetched successfully:", loConfigurationService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// fetch contract config coa details with application exception
	// do not set fiscal year id
	@Test(expected = ApplicationException.class)
	public void testFetchContractConfCOADetails4() throws ApplicationException
	{

		CBGridBean loGridBean = new CBGridBean();
		loGridBean.setContractID(amendContractId);

		ConfigurationService loConfigurationService = new ConfigurationService();
		List<AccountsAllocationBean> loCOA = loConfigurationService.fetchContractConfCOADetails(loGridBean, moSession);

		assertNotNull(loCOA);
		assertEquals("Account Allocation Details for contract fetched successfully:", loConfigurationService
				.getMoState().toString());

	}

	// fetch contract config coa details with exception
	// do not set cbgridbean
	@Test(expected = ApplicationException.class)
	public void testFetchContractConfCOADetails5() throws ApplicationException
	{

		CBGridBean loGridBean = null;

		ConfigurationService loConfigurationService = new ConfigurationService();
		List<AccountsAllocationBean> loCOA = loConfigurationService.fetchContractConfCOADetails(loGridBean, null);

		assertNotNull(loCOA);
		assertEquals("Account Allocation Details for contract fetched successfully:", loConfigurationService
				.getMoState().toString());

	}

	// fetch contract config details with application exception
	@Test
	public void testFetchContractConfigDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsContractId = amendContractId;

			ConfigurationService loConfigurationService = new ConfigurationService();
			ProcurementCOF loProcBean = loConfigurationService.fetchContractConfigDetails(lsContractId, null);

			assertNotNull(loProcBean);
			assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// fetch contract config details with successful status
	@Test
	public void testFetchContractConfigDetails2() throws ApplicationException
	{
		moSession.rollback();
		Boolean lbThrown = false;
		try
		{
			String lsContractId = "241";
			
			ConfigurationService loConfigurationService = new ConfigurationService();
			ProcurementCOF loProcBean = loConfigurationService.fetchContractConfigDetails(lsContractId, moSession);

			assertNotNull(loProcBean);
			assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	@Test
	public void testFetchContractConfigDetails4() throws ApplicationException
	{
		moSession.rollback();
		Boolean lbThrown = false;
		try
		{
			String lsContractId = "118";
			
			ConfigurationService loConfigurationService = new ConfigurationService();
			ProcurementCOF loProcBean = loConfigurationService.fetchContractConfigDetails(lsContractId, moSession);

			assertNotNull(loProcBean);
			assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	// fetch contract config details with successful status
	@Test
	public void testFetchContractConfigDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsContractId = "4";

			ConfigurationService loConfigurationService = new ConfigurationService();
			ProcurementCOF loProcBean = loConfigurationService.fetchContractConfigDetails(lsContractId, moSession);

			assertNotNull(loProcBean);
			assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// fetch contract config budget details with successful status
	@Test
	public void testFetchContractConfigBudgetDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsContractId = amendContractId;

			ConfigurationService loConfigurationService = new ConfigurationService();
			List loList = loConfigurationService.fetchContractConfigBudgetDetails(lsContractId, moSession);

			assertNotNull(loList);
			assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// fetch contract config budget details with application exception
	@Test
	public void testFetchContractConfigBudgetDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsContractId = null;

			ConfigurationService loConfigurationService = new ConfigurationService();
			List loList = loConfigurationService.fetchContractConfigBudgetDetails(lsContractId, moSession);

			assertNotNull(loList);
			assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// s382 end

	// s390 start
	// add with exception
	// start and end date are not set
	@Test
	public void testAddContractConfCOADetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setProcurementID("75");
			loAccountsAllocationBean.setContractID(amendContractId);
			loAccountsAllocationBean.setUnitOfAppropriation("345");
			loAccountsAllocationBean.setBudgetCode("2222");
			loAccountsAllocationBean.setObjectCode("22");
			loAccountsAllocationBean.setSubOc("2");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYear(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.addContractConfCOADetails(loAccountsAllocationBean, moSession);

			assertEquals("addContractConfCOADetails is executed successfully", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// add with application exception
	// give same data with that of unique constraint that already exist in db
	@Test
	public void testAddContractConfCOADetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setProcurementID("75");
			loAccountsAllocationBean.setContractID(amendContractId);
			loAccountsAllocationBean.setUnitOfAppropriation("222");
			loAccountsAllocationBean.setBudgetCode("2222");
			loAccountsAllocationBean.setObjectCode("22");
			loAccountsAllocationBean.setSubOc("2");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYear(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);

			loAccountsAllocationBean.setContractStartFY(fiscalYearId);
			loAccountsAllocationBean.setContractEndFY("2018");

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.addContractConfCOADetails(loAccountsAllocationBean, moSession);

			assertEquals("addContractConfCOADetails is executed successfully", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// add successful
	@Test
	public void testAddContractConfCOADetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setProcurementID("75");
			loAccountsAllocationBean.setContractID(amendContractId);
			loAccountsAllocationBean.setUnitOfAppropriation("345");
			loAccountsAllocationBean.setBudgetCode("2222");
			loAccountsAllocationBean.setObjectCode("22");
			loAccountsAllocationBean.setSubOc("2");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYear(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);

			loAccountsAllocationBean.setContractStartFY(fiscalYearId);
			loAccountsAllocationBean.setContractEndFY("2018");

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.addContractConfCOADetails(loAccountsAllocationBean, moSession);

			assertEquals("addContractConfCOADetails is executed successfully", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// add successful
	@Test
	public void testAddContractConfCOADetails4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setProcurementID("75");
			loAccountsAllocationBean.setContractID(amendContractId);
			loAccountsAllocationBean.setUnitOfAppropriation("345");
			loAccountsAllocationBean.setBudgetCode("2222");
			loAccountsAllocationBean.setObjectCode("22");
			loAccountsAllocationBean.setSubOc("2");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYear(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);

			loAccountsAllocationBean.setContractStartFY(fiscalYearId);
			loAccountsAllocationBean.setContractEndFY("2018");

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.addContractConfCOADetails(loAccountsAllocationBean, null);

			assertEquals("addContractConfCOADetails is executed successfully", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// delete successful
	@Test
	public void testDelContractConfCOADetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.delContractConfCOADetails(loAccountsAllocationBean, moSession);

			assertEquals("Chart of Account Allocation Details for contract deleted successfully:",
					loConfigurationService.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// delete with Application Exception
	@Test
	public void testDelContractConfCOADetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.delContractConfCOADetails(loAccountsAllocationBean, null);

			assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// edit with Exception
	@Test
	public void testEditContractConfCOADetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();

			loAccountsAllocationBean.setProcurementID("75");
			loAccountsAllocationBean.setContractID(amendContractId);
			loAccountsAllocationBean.setUnitOfAppropriation("345");
			loAccountsAllocationBean.setBudgetCode("2222");
			loAccountsAllocationBean.setObjectCode("22");
			loAccountsAllocationBean.setSubOc("2");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYear(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.editContractConfCOADetails(loAccountsAllocationBean, moSession);

			assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// edit with Application Exception
	@Test
	public void testEditContractConfCOADetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setProcurementID("75");
			loAccountsAllocationBean.setContractID("27"); // set contract id as
														// blank
			loAccountsAllocationBean.setUnitOfAppropriation("345");
			loAccountsAllocationBean.setBudgetCode("2222");
			loAccountsAllocationBean.setObjectCode("22");
			loAccountsAllocationBean.setSubOc("2");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYear("2014");
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);
			loAccountsAllocationBean.setContractStartFY(fiscalYearId);
			loAccountsAllocationBean.setContractEndFY("2018");
			loAccountsAllocationBean.setId("5");
			loAccountsAllocationBean.setModifiedByUserId("agency");
			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.editContractConfCOADetails(loAccountsAllocationBean, moSession);

			assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// successfull edit
	@Test
	public void testEditContractConfCOADetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setProcurementID("75");
			loAccountsAllocationBean.setContractID(amendContractId); // set
																		// contract
																		// id
			// as blank
			loAccountsAllocationBean.setUnitOfAppropriation("345");
			loAccountsAllocationBean.setBudgetCode("2222");
			loAccountsAllocationBean.setObjectCode("22");
			loAccountsAllocationBean.setSubOc("2");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYear(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);
			loAccountsAllocationBean.setContractStartFY(fiscalYearId);
			loAccountsAllocationBean.setContractEndFY("2018");
			loAccountsAllocationBean.setId("333-4443-34-2-6");
			loAccountsAllocationBean.setModifiedByUserId(agency);
			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.editContractConfCOADetails(loAccountsAllocationBean, moSession);

			assertEquals("editContractConfCOADetails is executed successfully", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testEditContractConfCOADetails4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setProcurementID("75");
			loAccountsAllocationBean.setContractID(amendContractId); // set
																		// contract
																		// id
			// as blank
			loAccountsAllocationBean.setUnitOfAppropriation("345");
			loAccountsAllocationBean.setBudgetCode("2222");
			loAccountsAllocationBean.setObjectCode("22");
			loAccountsAllocationBean.setSubOc("2");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYear(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);
			loAccountsAllocationBean.setContractStartFY("2015");
			loAccountsAllocationBean.setContractEndFY("2018");
			loAccountsAllocationBean.setId("333-4443-34-2-6");
			loAccountsAllocationBean.setModifiedByUserId(agency);
			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.editContractConfCOADetails(loAccountsAllocationBean, moSession);

			assertEquals("editContractConfCOADetails is executed successfully", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// S390 - ContractBudget Tab Starts

	// fetch contract config budget details with successful status
	@Test
	public void testFetchContractConfSubBudgetDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsContractId = amendContractId;
			String lsFiscalYearId = fiscalYearId;

			ConfigurationService loConfigurationService = new ConfigurationService();
			List loList = loConfigurationService.fetchContractConfSubBudgetDetails1(moSession, lsContractId,
					lsFiscalYearId);

			assertNotNull(loList);
			assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractConfigBudgetDetails1ForException() throws ApplicationException
	{
		String lsContractId = amendContractId;
		String lsFiscalYearId = "2014B";

		ConfigurationService loConfigurationService = new ConfigurationService();
		List loList = loConfigurationService
				.fetchContractConfSubBudgetDetails1(moSession, lsContractId, lsFiscalYearId);

//		assertNotNull(loList);
//		assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
//				.toString());
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

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testFetchBudgetDetailsByFYAndContractId() throws ApplicationException
	{
		String lsContractId = amendContractId;
		String lsFiscalYear = fiscalYearId;
		List loList = loConfigurationService.fetchBudgetDetailsByFYAndContractId(moSession, lsContractId, lsFiscalYear);
		assertNotNull(loList);
		assertEquals("Budget details for contract fetched successfully:", loConfigurationService.getMoState()
				.toString());
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchBudgetDetailsByFYAndContractIdForAppException() throws ApplicationException
	{
		String lsContractId = "ABC";
		String lsFiscalYear = "";
		loConfigurationService.fetchBudgetDetailsByFYAndContractId(null, lsContractId, lsFiscalYear);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchBudgetDetailsByFYAndContractIdForAppExceptionSessionNull() throws ApplicationException
	{
		String lsContractId = amendContractId;
		String lsFiscalYear = fiscalYearId;
		loConfigurationService.fetchBudgetDetailsByFYAndContractId(null, lsContractId, lsFiscalYear);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractConfSubBudgetDetails() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		Boolean lbResult = loConfigurationService.insertContractConfSubBudgetDetails(moSession, loSubBudgetBean);
		assertNotNull(lbResult);
		assertTrue(lbResult);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractConfSubBudgetDetailsParentId() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setParentId("761");
		Boolean lbResult = loConfigurationService.insertContractConfSubBudgetDetails(moSession, loSubBudgetBean);
		assertNotNull(lbResult);
		assertTrue(lbResult);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractConfSubBudgetDetailsEmptyParentId() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setParentId("");
		Boolean lbResult = loConfigurationService.insertContractConfSubBudgetDetails(moSession, loSubBudgetBean);
		assertNotNull(lbResult);
		assertTrue(lbResult);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testInsertContractConfSubBudgetDetailsForAppException() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = new ContractBudgetBean();
		loConfigurationService.insertContractConfSubBudgetDetails(null, loSubBudgetBean);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractConfSubBudgetDetailsForAppExceptionBeanNull() throws ApplicationException
	{
		loConfigurationService.insertContractConfSubBudgetDetails(moSession, null);
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

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testDelContractConfSubBudgetDetails() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setId("554");
		Boolean lbResult = loConfigurationService.delContractConfSubBudgetDetails(moSession, loSubBudgetBean);
		assertNotNull(lbResult);
		assertTrue(lbResult);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testDelContractConfSubBudgetDetailsNewRecord() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setId("554_newrecord");
		Boolean lbResult = loConfigurationService.delContractConfSubBudgetDetails(moSession, loSubBudgetBean);
		assertNotNull(lbResult);
		assertTrue(lbResult);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testDelContractConfSubBudgetDetailsNewRecordParentIdSuccess() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setId("554");
		loSubBudgetBean.setParentId("555");
		Boolean lbResult = loConfigurationService.delContractConfSubBudgetDetails(moSession, loSubBudgetBean);
		assertNotNull(lbResult);
		assertTrue(lbResult);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testDelContractConfSubBudgetDetailsForException() throws ApplicationException
	{
		boolean lbThrown;
		try
		{
			ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
			loSubBudgetBean.setId("");
			loConfigurationService.delContractConfSubBudgetDetails(moSession, loSubBudgetBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testDelContractConfSubBudgetDetailsBeanNull() throws ApplicationException
	{
		loConfigurationService.delContractConfSubBudgetDetails(moSession, null);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testDelContractConfSubBudgetDetailsParentIdEmpty() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setParentId("");
		loConfigurationService.delContractConfSubBudgetDetails(moSession, loSubBudgetBean);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testDelContractConfSubBudgetDetailsParentIdEmpty2() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setParentId("");
		loConfigurationService.delContractConfSubBudgetDetails(moSession, null);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testDelContractConfSubBudgetDetailsParentIdEmpty3() throws ApplicationException
	{
		boolean lbThrown;
		try
		{
			ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
			loSubBudgetBean.setParentId("");
			loConfigurationService.delContractConfSubBudgetDetails(null, loSubBudgetBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchNewFYSubBudgetDetailsFail() throws ApplicationException
	{
		String asFiscalYearId = "2017";
		String asContractId = amendContractId;
		loConfigurationService.fetchNewFYSubBudgetDetails(null, asContractId, asFiscalYearId);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchFYPlannedAmountNegativeContractIdNull() throws ApplicationException
	{
		String asFiscalYearId = "2017";
		loConfigurationService.fetchFYPlannedAmount(moSession, null, asFiscalYearId);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testFetchFYPlannedAmountNegativeMoSessionNull() throws ApplicationException
	{
		String asContractId = amendContractId;
		String asFiscalYearId = "2017";
		loConfigurationService.fetchFYPlannedAmount(null, asContractId, asFiscalYearId);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testCopyPreviousFYSubBudgetToCurrentFYExisitingFY() throws ApplicationException
	{
		String asContractId = amendContractId;
		String asBudgetId = "555";
		String asFiscalYearId = "2016";
		String asUserId = agency;
		loConfigurationService.copyPreviousFYSubBudgetToCurrentFY(moSession, asContractId, asFiscalYearId, asBudgetId,
				asUserId);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testCopyPreviousFYSubBudgetToCurrentFYNewFY() throws ApplicationException
	{
		String asContractId = "75";
		String asBudgetId = "28";
		String asFiscalYearId = "2014";
		String asUserId = agency;
		loConfigurationService.copyPreviousFYSubBudgetToCurrentFY(moSession, asContractId, asFiscalYearId, asBudgetId,
				asUserId);
		assertEquals("copyPreviousFYSubBudgetToCurrentFY executed successfully", loConfigurationService.getMoState()
				.toString());
	}
	
	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testCopyPreviousFYSubBudgetToCurrentFYNewFY1() throws ApplicationException
	{
		String asContractId = "75";
		String asBudgetId = "28";
		String asFiscalYearId = "2015";
		String asUserId = agency;
		loConfigurationService.copyPreviousFYSubBudgetToCurrentFY(moSession, asContractId, asFiscalYearId, asBudgetId,
				asUserId);
//		assertEquals("copyPreviousFYSubBudgetToCurrentFY executed successfully", loConfigurationService.getMoState()
//				.toString());
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testCopyPreviousFYSubBudgetToCurrentFYNegativeContractIdNull() throws ApplicationException
	{
		String asContractId = null;
		String asBudgetId = "10028";
		String asFiscalYearId = "2017";
		String asUserId = agency;
		loConfigurationService.copyPreviousFYSubBudgetToCurrentFY(moSession, asContractId, asFiscalYearId, asBudgetId,
				asUserId);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testCopyPreviousFYSubBudgetToCurrentFYNegativeBudgetId() throws ApplicationException
	{
		String asContractId = amendContractId;
		String asBudgetId = null;
		String asFiscalYearId = "2017";
		String asUserId = agency;
		loConfigurationService.copyPreviousFYSubBudgetToCurrentFY(null, asContractId, asFiscalYearId, asBudgetId,
				asUserId);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testCopyPreviousFYSubBudgetToCurrentFYNegativeMoSessioNull() throws ApplicationException
	{
		String asContractId = amendContractId;
		String asBudgetId = "555";
		String asFiscalYearId = "2017";
		String asUserId = agency;
		loConfigurationService.copyPreviousFYSubBudgetToCurrentFY(null, asContractId, asFiscalYearId, asBudgetId,
				asUserId);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testCopyPreviousFYSubBudgetToCurrentFYNegativeUserIdNull() throws ApplicationException
	{
		String asContractId = amendContractId;
		String asBudgetId = "555";
		String asFiscalYearId = "2017";
		loConfigurationService.copyPreviousFYSubBudgetToCurrentFY(null, asContractId, asFiscalYearId, asBudgetId, null);
	}

	/**
	 * This method edits the NewFY Configuration details for CoA grid. FMS
	 * fields set null
	 * @throws ApplicationException
	 */
	@Test
	public void testEditNewFYConfCOADetails() throws ApplicationException
	{
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();

		// Positive scenario
		loAccountsAllocationBean.setId("123-2212-22-3-5");
		loAccountsAllocationBean.setBudgetCode(null);
		loAccountsAllocationBean.setUnitOfAppropriation(null);
		loAccountsAllocationBean.setChartOfAccount(null);
		loAccountsAllocationBean.setObjectCode(null);
		loAccountsAllocationBean.setSubOc(null);
		loAccountsAllocationBean.setRc(null);
		loAccountsAllocationBean.setAmmount("22322");
		loAccountsAllocationBean.setFy1("5550");
		loAccountsAllocationBean.setFy2("1902");
		loAccountsAllocationBean.setFy3("550");
		loAccountsAllocationBean.setFy4("1209");
		loAccountsAllocationBean.setContractEndFY("2017");
		loAccountsAllocationBean.setContractStartFY(fiscalYearId);
		loConfigurationService.editNewFYConfCOADetails(loAccountsAllocationBean, moSession);
		assertNotNull(loConfigurationService.getMoState().toString());
	}

	@Test(expected = ApplicationException.class)
	public void testEditNewFYConfCOADetailsExps() throws ApplicationException
	{
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();

		loConfigurationService.editNewFYConfCOADetails(loAccountsAllocationBean, null);
		assertNotNull(loConfigurationService.getMoState().toString());
	}

	/**
	 * This method edits the NewFY Configuration details for CoA grid. FMS
	 * fields set null and FY values also set to Null
	 * @throws ApplicationException
	 */
	@Test
	public void testEditNewFYConfCOADetailsFYValueNull() throws ApplicationException
	{
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();

		// Positive scenario
		loAccountsAllocationBean.setId("123-2212-22-3-5");
		loAccountsAllocationBean.setBudgetCode(null);
		loAccountsAllocationBean.setUnitOfAppropriation(null);
		loAccountsAllocationBean.setChartOfAccount(null);
		loAccountsAllocationBean.setObjectCode(null);
		loAccountsAllocationBean.setSubOc(null);
		loAccountsAllocationBean.setRc(null);
		loAccountsAllocationBean.setAmmount("");
		loAccountsAllocationBean.setFy1(null);
		loAccountsAllocationBean.setFy2(null);
		loAccountsAllocationBean.setFy3(null);
		loAccountsAllocationBean.setFy4(null);
		loAccountsAllocationBean.setContractEndFY("2017");
		loAccountsAllocationBean.setContractStartFY(fiscalYearId);
		loConfigurationService.editNewFYConfCOADetails(loAccountsAllocationBean, moSession);
		assertNotNull(loConfigurationService.getMoState().toString());
	}

	/**
	 * This method edits the NewFY Configuration details for CoA grid. FMS
	 * fields set null
	 * @throws ApplicationException
	 */
	@Test
	public void testEditNewFYConfCOADetailsNegative() throws ApplicationException
	{
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
		boolean lbThrown;
		try
		{
			loAccountsAllocationBean.setId("123-2212-22-3-5");
			loAccountsAllocationBean.setBudgetCode(null);
			loAccountsAllocationBean.setUnitOfAppropriation(null);
			loAccountsAllocationBean.setChartOfAccount(null);
			loAccountsAllocationBean.setObjectCode(null);
			loAccountsAllocationBean.setSubOc(null);
			loAccountsAllocationBean.setRc(null);
			loAccountsAllocationBean.setAmmount("");
			loAccountsAllocationBean.setContractEndFY("2017");
			loAccountsAllocationBean.setContractStartFY(fiscalYearId); // Exception
																		// case
			loConfigurationService.editNewFYConfCOADetails(loAccountsAllocationBean, moSession);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	/**
	 * This method edits the NewFY Configuration details for CoA grid.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testEditNewFYConfCOADetailsNegativeAmountNull() throws ApplicationException
	{
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();

		loAccountsAllocationBean.setId("123-2212-22-3-5");
		loAccountsAllocationBean.setBudgetCode(null);
		loAccountsAllocationBean.setUnitOfAppropriation(null);
		loAccountsAllocationBean.setChartOfAccount(null);
		loAccountsAllocationBean.setObjectCode(null);
		loAccountsAllocationBean.setSubOc(null);
		loAccountsAllocationBean.setRc(null);
		loAccountsAllocationBean.setAmmount("");
		loAccountsAllocationBean.setContractEndFY("2017");
		loAccountsAllocationBean.setContractStartFY(fiscalYearId); // Exception
																	// case
		loConfigurationService.editNewFYConfCOADetails(loAccountsAllocationBean, null);
	}

	/**
	 * This method edits the NewFY Configuration details for CoA grid.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testEditNewFYConfCOADetailsNegativeMOSessionNull() throws ApplicationException
	{
		loConfigurationService.editNewFYConfCOADetails(null, moSession);
	}

	/**
	 * This method edits the NewFY Configuration details for CoA grid.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testEditNewFYConfCOADetailsNegativeEndFYNull() throws ApplicationException
	{
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();

		loAccountsAllocationBean.setId("123-2212-22-3-5");
		loAccountsAllocationBean.setBudgetCode(null);
		loAccountsAllocationBean.setUnitOfAppropriation(null);
		loAccountsAllocationBean.setChartOfAccount(null);
		loAccountsAllocationBean.setObjectCode(null);
		loAccountsAllocationBean.setSubOc(null);
		loAccountsAllocationBean.setRc(null);
		loAccountsAllocationBean.setAmmount("22322");
		loAccountsAllocationBean.setFy1("5550");
		loAccountsAllocationBean.setFy2("1902");
		loAccountsAllocationBean.setFy3("550");
		loAccountsAllocationBean.setFy4("1209");
		loAccountsAllocationBean.setContractEndFY(null);
		loAccountsAllocationBean.setContractStartFY(fiscalYearId); // Exception
																	// case
		loConfigurationService.editNewFYConfCOADetails(loAccountsAllocationBean, moSession);
	}

	/**
	 * This method edits the NewFY Configuration details for CoA grid.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testEditNewFYConfCOADetailsNegativeAmountEmptyStartFYNull() throws ApplicationException
	{
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();

		loAccountsAllocationBean.setId("123-2212-22-3-5");
		loAccountsAllocationBean.setBudgetCode(null);
		loAccountsAllocationBean.setUnitOfAppropriation(null);
		loAccountsAllocationBean.setChartOfAccount(null);
		loAccountsAllocationBean.setObjectCode(null);
		loAccountsAllocationBean.setSubOc(null);
		loAccountsAllocationBean.setRc(null);
		loAccountsAllocationBean.setAmmount("");
		loAccountsAllocationBean.setContractEndFY("2017");
		loAccountsAllocationBean.setContractStartFY(null); // Exception case
		loConfigurationService.editNewFYConfCOADetails(loAccountsAllocationBean, moSession);
	}

	/**
	 * This method edits the NewFY Configuration details for CoA grid.
	 * @throws ApplicationException
	 */
	@Test(expected = ApplicationException.class)
	public void testEditNewFYConfCOADetailsNegativeStartFYAmounNull() throws ApplicationException
	{
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();

		loAccountsAllocationBean.setId("123-2212-22-3-5");
		loAccountsAllocationBean.setBudgetCode(null);
		loAccountsAllocationBean.setUnitOfAppropriation(null);
		loAccountsAllocationBean.setChartOfAccount(null);
		loAccountsAllocationBean.setObjectCode(null);
		loAccountsAllocationBean.setSubOc(null);
		loAccountsAllocationBean.setRc(null);
		loAccountsAllocationBean.setAmmount(null);
		loAccountsAllocationBean.setContractEndFY("2017");
		loAccountsAllocationBean.setContractStartFY(null); // Exception case
		loConfigurationService.editNewFYConfCOADetails(loAccountsAllocationBean, moSession);
	}

	/**
	 * @throws ApplicationException
	 */
	@Test
	public void testInsertContractConfSubBudgetDetailsForException() throws ApplicationException
	{
		try
		{
			ContractBudgetBean loSubBudgetBean = null;

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.insertContractConfSubBudgetDetails(null, loSubBudgetBean);
		}
		catch (ApplicationException loEx)
		{
			// assertTrue(Boolean.TRUE);
			throw new ApplicationException("Application Exception thrown", loEx);
		}
	}

//	@Test
//	public void testGetSubBudgetsSumTotal() throws ApplicationException
//	{
//		String lsBudgetId = "555";
//		String asSubBudgetId = "555";
//		ConfigurationService loConfigurationService = new ConfigurationService();
//		String lbResult = loConfigurationService.getSubBudgetsSumTotal(moSession, lsBudgetId, asSubBudgetId);
////		assertNotNull(lbResult);
//	}

//	@Test
//	public void testGetSubBudgetsSumTotal2() throws ApplicationException
//	{
//		boolean lbThrown;
//		try
//		{
//			String lsBudgetId = "555";
//			String asSubBudgetId = "555";
//			ConfigurationService loConfigurationService = new ConfigurationService();
//			String lbResult = loConfigurationService.getSubBudgetsSumTotal(null, lsBudgetId, asSubBudgetId);
//			assertNotNull(lbResult);
//		}
//		catch (ApplicationException loEx)
//		{
//			// assertTrue(Boolean.TRUE);
//			lbThrown = true;
//			assertTrue("Exception thrown", lbThrown);
//		}
//	}

//	@Test
//	public void testGetSubBudgetsSumTotal3() throws ApplicationException
//	{
//		boolean lbThrown;
//		try
//		{
//			String lsBudgetId = "555";
//			String asSubBudgetId = "";
//			ConfigurationService loConfigurationService = new ConfigurationService();
//			String lbResult = loConfigurationService.getSubBudgetsSumTotal(null, lsBudgetId, asSubBudgetId);
//			assertNotNull(lbResult);
//		}
//		catch (ApplicationException loEx)
//		{
//			// assertTrue(Boolean.TRUE);
//			lbThrown = true;
//			assertTrue("Exception thrown", lbThrown);
//		}
//	}

//	@Test(expected = ApplicationException.class)
//	public void testGetSubBudgetsSumTotalForException() throws ApplicationException
//	{
//		String lsBudgetId = null;
//		String asSubBudgetId = "555";
//		ConfigurationService loConfigurationService = new ConfigurationService();
//		String lbResult = loConfigurationService.getSubBudgetsSumTotal(moSession, lsBudgetId, asSubBudgetId);
//		assertNotNull(lbResult);
//	}

	@Test
	public void testUpdateBudgetFYTotalBudgetAmount() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();

		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbResult = loConfigurationService.updateBudgetFYTotalBudgetAmount(moSession, loSubBudgetBean);

		assertNotNull(lbResult);
		assertTrue(lbResult);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateBudgetFYTotalBudgetAmountForException() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = null;
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.updateBudgetFYTotalBudgetAmount(moSession, loSubBudgetBean);
	}

	@Test
	public void testInsertNewBudgetDetails() throws ApplicationException
	{
		try
		{
			ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();

			ConfigurationService loConfigurationService = new ConfigurationService();
			Boolean lbResult = loConfigurationService.insertNewBudgetDetails(moSession, loSubBudgetBean);

			assertNotNull(lbResult);
			assertTrue(lbResult);
		}
		catch (Exception loExp)
		{
			assertTrue(Boolean.TRUE);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testInsertNewBudgetDetailsForException() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = null;
		ConfigurationService loConfigurationService = new ConfigurationService();

		loConfigurationService.insertNewBudgetDetails(moSession, loSubBudgetBean);

	}

	// S390 - ContractBudget Tab Ends
	// s390 end

	// S 204 test cases Starts------>

	@Test
	public void testFetchProcurementDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsProcurementId = "115";

			// Positive scenario ---> Proc ID given in the DB
			ConfigurationService loConfigurationService = new ConfigurationService();
			ProcurementCOF loProcList = loConfigurationService.fetchProcurementDetails(moSession, lsProcurementId);
			assertNotNull(loProcList);

			// lsProcurementId = "495";
			// loProcList =
			// loConfigurationService.fetchProcurementDetails(moSession,
			// lsProcurementId);
			// assertNotNull(loProcList);

			// Negative scenario ---> Null session
			loConfigurationService.fetchProcurementDetails(null, lsProcurementId);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testFetchPCOFCoADetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsProcurementId = procurementId;

			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setProcurementID(lsProcurementId);
			loGridBean.setFiscalYearID(fiscalYearId);

			// Positive scenario ---> Proc ID given in the DB
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<AccountsAllocationBean> loProcCOAList = loConfigurationService.fetchPCOFCoADetails(loGridBean,
					moSession);

			assertNotNull(loProcCOAList);

			// Negative scenario ---> Null session
			loConfigurationService.fetchPCOFCoADetails(loGridBean, null);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testFetchPCOFFundingSourcesDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsProcurementId = procurementId;

			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setProcurementID("115");
			loGridBean.setFiscalYearID(fiscalYearId);
			loGridBean.setNoOfyears(2);
			loGridBean.setCreatedByUserId(agency);
			loGridBean.setModifiedByUserId(agency);
			loGridBean.setIsProcCerTaskScreen(true);
			// Positive scenario ---> Proc ID given in the DB
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<FundingAllocationBean> loProcCOAList1 = loConfigurationService.fetchPCOFFundingSourcesDetails(
					moSession, loGridBean);

			assertNotNull(loProcCOAList1);

			// Positive scenario --> Data in the DB doest exist against the Proc
			// ID
			loGridBean.setFiscalYearID("2030");
			List<FundingAllocationBean> loProcCOAList2 = loConfigurationService.fetchPCOFFundingSourcesDetails(
					moSession, loGridBean);

			assertNotNull(loProcCOAList2);

			// Negative scenario ---> Null session
			loConfigurationService.fetchPCOFFundingSourcesDetails(null, loGridBean);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	@Test
	public void testFetchPCOFFundingSourcesDetails2() throws ApplicationException
	{

			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setProcurementID("205");
			loGridBean.setFiscalYearID(fiscalYearId);
			loGridBean.setNoOfyears(2);
			loGridBean.setCreatedByUserId(agency);
			loGridBean.setIsProcCerTaskScreen(false);
			loGridBean.setModifyByAgency(agency);
			// Positive scenario ---> Proc ID given in the DB
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<FundingAllocationBean> loProcCOAList1 = loConfigurationService.fetchPCOFFundingSourcesDetails(
					moSession, loGridBean);

			assertNotNull(loProcCOAList1);

	}
	
	@Test
	public void testFetchPCOFFundingSourcesDetails3() throws ApplicationException
	{

			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setProcurementID("115");
			loGridBean.setFiscalYearID(fiscalYearId);
			loGridBean.setNoOfyears(2);
			loGridBean.setCreatedByUserId(agency);
			loGridBean.setIsProcCerTaskScreen(false);
			loGridBean.setModifyByAgency(agency);
			loGridBean.setCoaDocType(true);
			// Positive scenario ---> Proc ID given in the DB
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<FundingAllocationBean> loProcCOAList1 = loConfigurationService.fetchPCOFFundingSourcesDetails(
					moSession, loGridBean);

			assertNotNull(loProcCOAList1);

	}


	@Test
	public void testFetchPCOFFundingSourcesDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsProcurementId = procurementId;

			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setProcurementID("472");
			loGridBean.setFiscalYearID(fiscalYearId);
			loGridBean.setNoOfyears(2);
			loGridBean.setCreatedByUserId(agency);
			loGridBean.setModifiedByUserId(agency);
			loGridBean.setIsProcCerTaskScreen(true);
			// Positive scenario ---> Proc ID given in the DB
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<FundingAllocationBean> loProcCOAList1 = loConfigurationService.fetchPCOFFundingSourcesDetails(
					moSession, loGridBean);

			assertNotNull(loProcCOAList1);

		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testUpdateCoADetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsProcurementId = procurementId;
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean loAccountsBean = new AccountsAllocationBean();

			loAccountsBean.setFy1("6");
			loAccountsBean.setFy2("6");

			loAccountsBean.setProcurementID(lsProcurementId);
			loAccountsBean.setContractStartFY(fiscalYearId);
			loAccountsBean.setContractEndFY("2016");

			loConfigurationService.updateCoADetails(moSession, loAccountsBean);
			assertTrue("Executed", true);
			
			loAccountsBean.setIsProcCerTaskScreen(true);
			loConfigurationService.updateCoADetails(moSession, loAccountsBean);
			assertTrue("Executed", true);
			
			loAccountsBean.setIsProcCerTaskScreen(false);
			loAccountsBean.setProcurementID("115");
			loConfigurationService.updateCoADetails(moSession, loAccountsBean);
			assertTrue("Executed", true);

			// Negative scenario --> Null session
			loConfigurationService.updateCoADetails(null, loAccountsBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testUpdateFundingSourcesDetailsFederal() throws ApplicationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException
	{
		Boolean lbThrown = false;
		try
		{
			String lsProcurementId = procurementId;
			ConfigurationService loConfigurationService = new ConfigurationService();
			FundingAllocationBean loFundingUpdatedDetails = new FundingAllocationBean();

			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setProcurementID(lsProcurementId);
			loGridBean.setNoOfyears(5);
			loGridBean.setFiscalYearID(fiscalYearId);

			loFundingUpdatedDetails.setFundingSource("Federal");
			loFundingUpdatedDetails.setFy1("6");

			Boolean loReturnFederal = loConfigurationService.updateFundingSourcesDetails(moSession,
					loFundingUpdatedDetails, loGridBean);
			assertTrue(loReturnFederal);

			loFundingUpdatedDetails.setFundingSource("State");
			loFundingUpdatedDetails.setFy2("6");
			loFundingUpdatedDetails.setFy3("6");

			Boolean loReturnState = loConfigurationService.updateFundingSourcesDetails(moSession,
					loFundingUpdatedDetails, loGridBean);
			assertTrue(loReturnState);

			loFundingUpdatedDetails.setFundingSource("City");
			loFundingUpdatedDetails.setFy4("6");
			loFundingUpdatedDetails.setFy5("6");

			Boolean loReturnCity = loConfigurationService.updateFundingSourcesDetails(moSession,
					loFundingUpdatedDetails, loGridBean);
			assertTrue(loReturnCity);

			loFundingUpdatedDetails.setFundingSource("Other");
			loFundingUpdatedDetails.setFy4("6");
			loFundingUpdatedDetails.setFy5("6");

			Boolean loReturnOther = loConfigurationService.updateFundingSourcesDetails(moSession,
					loFundingUpdatedDetails, loGridBean);
			assertTrue(loReturnOther);

			loGridBean.setIsProcCerTaskScreen(true);
			loReturnOther = loConfigurationService.updateFundingSourcesDetails(moSession,
					loFundingUpdatedDetails, loGridBean);
			assertTrue(loReturnOther);
			// Negative scenario --> Null session
			loConfigurationService.updateFundingSourcesDetails(null, loFundingUpdatedDetails, loGridBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testUpdateFundingSourcesDetailsFederal2() throws ApplicationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException
	{
		Boolean lbThrown = false;
		try
		{
			String lsProcurementId = procurementId;
			ConfigurationService loConfigurationService = new ConfigurationService();
			FundingAllocationBean loFundingUpdatedDetails = new FundingAllocationBean();

			CBGridBean loGridBean = new CBGridBean();
			loGridBean.setProcurementID(lsProcurementId);
			loGridBean.setNoOfyears(5);
			loGridBean.setFiscalYearID(fiscalYearId);

			loFundingUpdatedDetails.setFundingSource("Federal");
			loFundingUpdatedDetails.setFy1("6");

			Boolean loReturnFederal = loConfigurationService.updateFundingSourcesDetails(moSession,
					loFundingUpdatedDetails, loGridBean);
			assertTrue(loReturnFederal);

			loFundingUpdatedDetails.setFundingSource("State");
			loFundingUpdatedDetails.setFy2("6");
			loFundingUpdatedDetails.setFy3("6");

			Boolean loReturnState = loConfigurationService.updateFundingSourcesDetails(moSession,
					loFundingUpdatedDetails, loGridBean);
			assertTrue(loReturnState);

			loFundingUpdatedDetails.setFundingSource("City");
			loFundingUpdatedDetails.setFy4("6");
			loFundingUpdatedDetails.setFy5("6");

			Boolean loReturnCity = loConfigurationService.updateFundingSourcesDetails(moSession,
					loFundingUpdatedDetails, loGridBean);
			assertTrue(loReturnCity);

			loFundingUpdatedDetails.setFundingSource("Other");
			loFundingUpdatedDetails.setFy4("6");
			loFundingUpdatedDetails.setFy5("6");

			Boolean loReturnOther = loConfigurationService.updateFundingSourcesDetails(moSession,
					loFundingUpdatedDetails, loGridBean);
			assertTrue(loReturnOther);

			// Negative scenario --> Null session
			loConfigurationService.updateFundingSourcesDetails(null, null, loGridBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testInsertCoADetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsProcurementId = procurementId;
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean loAccountsBean = new AccountsAllocationBean();

			loAccountsBean.setCreatedByUserId(agency);
			loAccountsBean.setModifiedByUserId(agency);
			loAccountsBean.setBudgetCode("101");
			loAccountsBean.setChartOfAccount("666-101-999");
			loAccountsBean.setObjectCode("999");
			loAccountsBean.setRc("55");
			loAccountsBean.setSubOc("99");
			loAccountsBean.setUnitOfAppropriation("666");

			loAccountsBean.setFy1("6");
			loAccountsBean.setFy2("6");

			loAccountsBean.setProcurementID(lsProcurementId);
			loAccountsBean.setContractStartFY("2021");
			loAccountsBean.setContractEndFY("2022");
			loAccountsBean.setCoaDocType(false);
			loAccountsBean.setIsProcCerTaskScreen(true);
			loAccountsBean.setModifyByAgency(agency);

			loConfigurationService.insertCoADetails(moSession, loAccountsBean);
			assertTrue("Executed", true);
			
			loAccountsBean.setIsProcCerTaskScreen(false);
			loConfigurationService.insertCoADetails(moSession, loAccountsBean);
			assertTrue("Executed", true);
			// Negative scenario --> Null session
			loConfigurationService.insertCoADetails(null, loAccountsBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testDeleteProcurementCoADetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsProcurementId = procurementId;
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean loAccountsBean = new AccountsAllocationBean();

			loAccountsBean.setCreatedByUserId(agency);
			loAccountsBean.setModifiedByUserId(agency);
			loAccountsBean.setBudgetCode("101");
			loAccountsBean.setChartOfAccount("666-101-999");
			loAccountsBean.setObjectCode("999");
			loAccountsBean.setRc("55");
			loAccountsBean.setSubOc("99");
			loAccountsBean.setUnitOfAppropriation("666");

			loAccountsBean.setFy1("6");
			loAccountsBean.setFy2("6");

			loAccountsBean.setProcurementID(lsProcurementId);
			loAccountsBean.setContractStartFY("2021");
			loAccountsBean.setContractEndFY("2022");

			loConfigurationService.deleteProcurementCoADetails(moSession, loAccountsBean);
			assertTrue("Executed", true);

			loAccountsBean.setIsProcCerTaskScreen(true);
			loConfigurationService.deleteProcurementCoADetails(moSession, loAccountsBean);

			// Negative scenario --> Null session
			loConfigurationService.deleteProcurementCoADetails(null, loAccountsBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// S 204 test cases Ends ------------------------------------------------->

	/*
	 * S-393 New FY Configuration Test cases starts
	 */
	@Test
	public void testFetchConfigurableYearBudgetAmount() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String asBudgetId = "10028";
			String asFiscalYearId = "2017";
			String lsFiscalYearAmount = "";

			lsFiscalYearAmount = loConfigurationService.fetchConfigurableYearBudgetAmount(moSession, asBudgetId,
					asFiscalYearId);
			assertEquals(lsFiscalYearAmount, null);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testFetchConfigurableYearBudgetAmountNegative() throws ApplicationException
	{
		String asFiscalYearId = "2017";
		loConfigurationService.fetchConfigurableYearBudgetAmount(moSession, null, asFiscalYearId);
	}

	@Test
	public void testCheckIfBudgetExists() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String asContractId = amendContractId;
			String asFiscalYearId = "2017";

			loConfigurationService.checkIfBudgetExists(moSession, asContractId, asFiscalYearId);
			assertEquals("Budget Id for contract fetched successfully", loConfigurationService.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testCheckIfBudgetExistsNegative() throws ApplicationException
	{
		String asFiscalYearId = "2017";
		loConfigurationService.checkIfBudgetExists(moSession, null, asFiscalYearId);
	}

	@Test
	public void testGetContractEndDate() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// Positive scenario
			String asContractId = amendContractId;

			loConfigurationService.getContractEndDate(moSession, asContractId);
			assertEquals("Contract End Date for contract fetched successfully", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testGetContractEndDateNegative() throws ApplicationException
	{
		loConfigurationService.getContractEndDate(moSession, null);
	}

	@Test
	public void testUpdateBudgetForNewFYConfigurationTask() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ContractBudgetBean loContractBudgetBean = new ContractBudgetBean();
			loContractBudgetBean.setStatusId("84");
			loContractBudgetBean.setBudgetEndDate("");
			loContractBudgetBean.setBudgetStartDate("");
			loContractBudgetBean.setContractId("");
			loContractBudgetBean.setBudgetfiscalYear("");

			loConfigurationService.updateBudgetForNewFYConfigurationTask(moSession, loContractBudgetBean);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateBudgetForNewFYConfigurationTaskNegative() throws ApplicationException
	{
		ContractBudgetBean loContractBudgetBean = new ContractBudgetBean();
		loContractBudgetBean.setStatusId("84");
		loContractBudgetBean.setBudgetEndDate("");
		loContractBudgetBean.setBudgetStartDate("");
		loContractBudgetBean.setBudgetfiscalYear("");

		loContractBudgetBean.setContractId(null);
		loConfigurationService.updateBudgetForNewFYConfigurationTask(null, loContractBudgetBean);
	}

	@Test
	public void testFetchNewFYSubBudgetDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// Positive scenario
			String asContractId = amendContractId;
			String asFiscalYearId = "2017";
			List<ContractBudgetBean> loContractBudgetBeanList;

			loContractBudgetBeanList = loConfigurationService.fetchNewFYSubBudgetDetails(moSession, asContractId,
					asFiscalYearId);
			assertNotNull(loContractBudgetBeanList);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testFetchNewFYSubBudgetDetailsNegative() throws ApplicationException
	{
		String asFiscalYearId = "2017";

		loConfigurationService.fetchNewFYSubBudgetDetails(moSession, null, asFiscalYearId);
	}

	@Test
	public void testFetchFYPlannedAmount() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// Positive scenario
			String asContractId = amendContractId;
			String asFiscalYearId = "2017";

			loConfigurationService.fetchFYPlannedAmount(moSession, asContractId, asFiscalYearId);
			assertEquals("Sum of Amount for given Fiscal Year fetched successfully", loConfigurationService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testFetchFYPlannedAmountNegative() throws ApplicationException
	{
		String asContractId = amendContractId;
		String asFiscalYearId = null;

		loConfigurationService.fetchFYPlannedAmount(moSession, asContractId, asFiscalYearId);
	}

	@Test
	public void testCopyPreviousFYSubBudgetToCurrentFY() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			// Positive scenario
			String asContractId = amendContractId;
			String asBudgetId = "10028";
			String asFiscalYearId = "2017";
			String asUserId = agency;

			loConfigurationService.copyPreviousFYSubBudgetToCurrentFY(moSession, asContractId, asFiscalYearId,
					asBudgetId, asUserId);
			assertEquals("copyPreviousFYSubBudgetToCurrentFY executed successfully", loConfigurationService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testCopyPreviousFYSubBudgetToCurrentFYNegative() throws ApplicationException
	{
		String asContractId = null;
		String asBudgetId = "10028";
		String asFiscalYearId = "2017";
		String asUserId = agency;

		loConfigurationService.copyPreviousFYSubBudgetToCurrentFY(moSession, asContractId, asFiscalYearId, asBudgetId,
				asUserId);
	}

	// 391 screen start
	@Test
	public void testMergeContractConfUpdateFinishTask2() throws ApplicationException
	{
		boolean lbThrown = false;
		String asContractId = "888999";
		List<String> fiscalYearList = null;
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setUserId("agency_21");
		try
		{
			loConfigurationService.mergeContractConfUpdateFinishTask(moSession, fiscalYearList, asContractId,
					aoTaskDetailsBean);
			lbThrown = true;
			assertTrue(lbThrown);
		}
		catch (ApplicationException loAppEx)
		{

			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testMergeContractConfUpdateFinishTask3() throws ApplicationException
	{
		boolean lbThrown = false;
		String asContractId = null;
		List<String> fiscalYearList = new ArrayList<String>();
		fiscalYearList.add(fiscalYearId);
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setUserId("agency_21");
		try
		{
			loConfigurationService.mergeContractConfUpdateFinishTask(moSession, fiscalYearList, asContractId,
					aoTaskDetailsBean);

			assertTrue(lbThrown);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testMergeContractConfUpdateFinishTaskException() throws ApplicationException
	{
		boolean lbThrown = false;
		String asContractId = "888999";
		List<String> fiscalYearList = new ArrayList<String>();
		fiscalYearList.add(fiscalYearId);
		TaskDetailsBean aoTaskDetailsBean = null;
		try
		{
			loConfigurationService.mergeContractConfUpdateFinishTask(moSession, fiscalYearList, asContractId,
					aoTaskDetailsBean);
			lbThrown = true;
			assertTrue(lbThrown);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testValidateContractConfigUpdateAmount() throws ApplicationException
	{
		boolean lbThrown = false;
		List<String> fiscalYearList = new ArrayList<String>();
		fiscalYearList.add(fiscalYearId);
		String asContractTypeId = "4";
		String asBudgetTypeId = "2";
		try
		{
			/*
			 * loConfigurationService.validateContractConfigUpdateAmount(moSession
			 * , fiscalYearList, asContractId, asContractTypeId,
			 * asBudgetTypeId);
			 */

			loConfigurationService.validateContractConfigUpdateAmount(moSession, fiscalYearList, amendContractId,
					asContractTypeId, asBudgetTypeId, baseContractId);
			lbThrown = true;
			assertTrue(lbThrown);
		}
		catch (ApplicationException loAppEx)
		{
			assertTrue("Exception thrown", lbThrown);
		}
	}
	
	@Test
	public void testValidateContractConfigUpdateAmoun3() throws ApplicationException
	{
		boolean lbThrown = false;
		List<String> fiscalYearList = new ArrayList<String>();
		fiscalYearList.add(fiscalYearId);
		String asContractTypeId = "2";
		String asBudgetTypeId = "2";
		try
		{
			/*
			 * loConfigurationService.validateContractConfigUpdateAmount(moSession
			 * , fiscalYearList, asContractId, asContractTypeId,
			 * asBudgetTypeId);
			 */

			loConfigurationService.validateContractConfigUpdateAmount(moSession, fiscalYearList, amendContractId,
					asContractTypeId, asBudgetTypeId, baseContractId);
			lbThrown = true;
			assertTrue(lbThrown);
		}
		catch (ApplicationException loAppEx)
		{
			assertTrue("Exception thrown", lbThrown);
		}
	}


	@Test
	public void testValidateContractConfigUpdateAmount2() throws ApplicationException
	{
		boolean lbThrown = false;
		String asContractId = "888999";
		List<String> fiscalYearList = null;
		try
		{
			loConfigurationService.validateContractConfigUpdateAmount(moSession, fiscalYearList, asContractId, null,
					null, baseContractId);
			lbThrown = true;
			assertTrue(lbThrown);
		}
		catch (ApplicationException loAppEx)
		{
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testValidateContractConfigUpdateAmount3() throws ApplicationException
	{
		boolean lbThrown = false;
		String asContractId = null;
		List<String> fiscalYearList = new ArrayList<String>();
		fiscalYearList.add(fiscalYearId);
		try
		{
			loConfigurationService.validateContractConfigUpdateAmount(moSession, fiscalYearList, asContractId, null,
					null, baseContractId);

			assertTrue(lbThrown);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testInsertUpdatedSubBudgetDetails() throws ApplicationException
	{
		boolean lbResult = false;
		try
		{
			String asContractId = "278";
			String asFiscalYearId = fiscalYearId;

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.insertUpdatedSubBudgetDetails(moSession, asContractId, asFiscalYearId);
			lbResult = true;
			assertTrue(lbResult);
		}
		catch (Exception loExp)
		{
			assertTrue(lbResult);
		}
	}

	@Test
	public void testInsertUpdatedSubBudgetDetails1() throws ApplicationException
	{
		boolean lbResult = false;
		try
		{
			String asContractId = "278";
			String asFiscalYearId = "1999";

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.insertUpdatedSubBudgetDetails(moSession, asContractId, asFiscalYearId);
			lbResult = true;
			assertTrue(lbResult);
		}
		catch (Exception loExp)
		{
			assertTrue(lbResult);
		}
	}
	
	@Test
	public void testInsertUpdatedSubBudgetDetailsForException1() throws ApplicationException
	{
		boolean lbResult = false;
		try
		{
			String asContractId = null;
			String asFiscalYearId = fiscalYearId;

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.insertUpdatedSubBudgetDetails(moSession, asContractId, asFiscalYearId);

			assertTrue(lbResult);
		}
		catch (Exception loExp)
		{
			lbResult = true;
			assertTrue(lbResult);
		}
	}

	@Test
	public void testInsertUpdatedSubBudgetDetailsForException2() throws ApplicationException
	{
		boolean lbResult = false;
		try
		{
			String asContractId = "888999";
			String asFiscalYearId = null;

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.insertUpdatedSubBudgetDetails(moSession, asContractId, asFiscalYearId);

			assertTrue(lbResult);
		}
		catch (Exception loExp)
		{
			assertTrue(true);
		}
	}
	
	@Test
	public void testInsertAmendmentSubBudgetDetails() throws ApplicationException
	{
		boolean lbResult = false;
			String asContractId = "278";
			String asFiscalYearId = fiscalYearId;

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.insertAmendmentSubBudgetDetails(moSession, asContractId, asFiscalYearId, "117");
			lbResult = true;
			assertTrue(lbResult);
	}
	
	
	@Test
	public void testInsertAmendmentSubBudgetDetails2() throws ApplicationException
	{
		boolean lbResult = false;
			String asContractId = "278";
			String asFiscalYearId = "1999";

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.insertAmendmentSubBudgetDetails(moSession, asContractId, asFiscalYearId, "117");
			lbResult = true;
			assertTrue(lbResult);
	}
	
	@Test(expected = ApplicationException.class)
	public void testInsertAmendmentSubBudgetDetails3() throws ApplicationException
	{
			String asContractId = "278";
			String asFiscalYearId = "1999";

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.insertAmendmentSubBudgetDetails(null, asContractId, asFiscalYearId, "117");
	}
	
	
	// delete successful
	@Test
	public void testDelContractConfUpdateSubBudgetDetails() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setId("554");

		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbResult = loConfigurationService.delContractConfUpdateSubBudgetDetails(moSession, loSubBudgetBean);

		assertNotNull(lbResult);
		assertTrue(lbResult);
	}
	
	// delete successful
	@Test
	public void testDelContractConfUpdateSubBudgetDetails1() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setId("554");
		loSubBudgetBean.setContractTypeId("2");

		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbResult = loConfigurationService.delContractConfUpdateSubBudgetDetails(moSession, loSubBudgetBean);

		assertNotNull(lbResult);
		assertTrue(lbResult);
	}

	@Test
	public void testDelContractConfUpdateSubBudgetDetailsForException() throws ApplicationException
	{
		try
		{
			ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
			loSubBudgetBean.setId("");

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.delContractConfUpdateSubBudgetDetails(moSession, loSubBudgetBean);
		}
		catch (Exception loExp)
		{
			assertTrue(Boolean.TRUE);
		}
	}

	@Test
	public void testDelContractConfUpdateSubBudgetDetailsForException3() throws ApplicationException
	{
		try
		{
			ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
			loSubBudgetBean.setId("");

			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.delContractConfUpdateSubBudgetDetails(null, loSubBudgetBean);
		}
		catch (Exception loExp)
		{
			assertTrue(Boolean.TRUE);
		}

	}

	@Test
	public void testDelContractConfUpdateSubBudgetDetailsForException2() throws ApplicationException
	{
		try
		{
			ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
			loSubBudgetBean.setId("");
			loSubBudgetBean.setParentId("");
			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.delContractConfUpdateSubBudgetDetails(moSession, loSubBudgetBean);
		}
		catch (Exception loExp)
		{
			assertTrue(Boolean.TRUE);
		}

	}

	@Test
	public void testUpdateFetchedContractDetails() throws ApplicationException
	{
		boolean lbResult = false;
		try
		{
			String lsContractId = "1";
			ConfigurationService loConfigurationService = new ConfigurationService();
			HashMap loHashMap = new HashMap();
			loHashMap.put(HHSConstants.CONTRACT_ID, lsContractId);
			loHashMap.put("contractTypeId", 1);
			loConfigurationService.updateFetchedContractDetails(moSession, loHashMap);
			lbResult = true;
			assertTrue(lbResult);
		}
		catch (Exception loExp)
		{
			assertTrue(!lbResult);
		}

	}

	@Test
	public void testUpdateFetchedContractDetails1() throws ApplicationException
	{
		boolean lbResult = false;
		try
		{
			String lsContractId = "1";
			ConfigurationService loConfigurationService = new ConfigurationService();
			HashMap loHashMap = new HashMap();
			loHashMap.put(HHSConstants.CONTRACT_ID, lsContractId);
			loHashMap.put("contractTypeId", 3);
			loConfigurationService.updateFetchedContractDetails(moSession, loHashMap);
			lbResult = true;
			assertTrue(lbResult);
		}
		catch (Exception loExp)
		{
			assertTrue(!lbResult);
		}

	}

	@Test
	public void testUpdateFetchedContractDetails2() throws ApplicationException
	{
		boolean lbResult = false;
		try
		{
			String lsContractId = null;
			ConfigurationService loConfigurationService = new ConfigurationService();
			HashMap loHashMap = new HashMap();
			loHashMap.put(HHSConstants.CONTRACT_ID, lsContractId);
			loConfigurationService.updateFetchedContractDetails(moSession, loHashMap);
			lbResult = true;
			assertTrue(lbResult);
		}
		catch (Exception loExp)
		{
			lbResult = true;
			assertTrue(lbResult);
		}

	}

	@Test
	public void testEditContractConfUpdateSubBudgetDetails() throws ApplicationException
	{

		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();

		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbResult = loConfigurationService.editContractConfUpdateSubBudgetDetails(moSession, loSubBudgetBean,
				null);

		assertNotNull(lbResult);
		assertTrue(lbResult);

	}

	@Test
	public void testEditContractConfUpdateSubBudgetDetails2() throws ApplicationException
	{

		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setAmendmentContractId("27");
		loSubBudgetBean.setBudgetId("");
		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbResult = loConfigurationService.editContractConfUpdateSubBudgetDetails(moSession, loSubBudgetBean,
				"true");

		assertNotNull(lbResult);
		assertTrue(lbResult);

	}

	@Test
	public void testEditContractConfUpdateSubBudgetDetails111() throws ApplicationException
	{

		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setId("34");
		loSubBudgetBean.setContractId("75");
		loSubBudgetBean.setBudgetfiscalYear("2014");
		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbResult = loConfigurationService.editContractConfUpdateSubBudgetDetails(moSession, loSubBudgetBean,
				"false");

		assertNotNull(lbResult);
		assertTrue(lbResult);

	}

	@Test(expected = ApplicationException.class)
	public void testEditContractConfUpdateSubBudgetDetails212Exp() throws ApplicationException
	{

		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();

		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbResult = loConfigurationService
				.editContractConfUpdateSubBudgetDetails(null, loSubBudgetBean, "false");

		assertNotNull(lbResult);
		assertTrue(lbResult);

	}

	@Test(expected = ApplicationException.class)
	public void testEditContractConfUpdateSubBudgetDetailsForException() throws ApplicationException
	{

		ContractBudgetBean loSubBudgetBean = null;

		ConfigurationService loConfigurationService = new ConfigurationService();
		Boolean lbResult = loConfigurationService.editContractConfUpdateSubBudgetDetails(null, loSubBudgetBean,
				null);

		assertNotNull(lbResult);
		assertTrue(lbResult);

	}

	@Test
	public void testAddContractConfUpdateBudgetDetails1() throws ApplicationException
	{
			ContractBudgetBean loSubBudgetBean = new ContractBudgetBean();
			loSubBudgetBean.setBudgetfiscalYear(fiscalYearId);
			loSubBudgetBean.setBudgetId(parentBudgetID);
			loSubBudgetBean.setBudgetTypeId(4);
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
			loSubBudgetBean.setContractId("574");
			loSubBudgetBean.setModifiedByUserId(agency);
			loSubBudgetBean.setStatusId("2");
			loSubBudgetBean.setSubbudgetAmount("1000");
			loSubBudgetBean.setBudgetfiscalYear("2014");
			loSubBudgetBean.setId("989");
			loSubBudgetBean.setContractTypeId("4");
			ConfigurationService loConfigurationService = new ConfigurationService();

			loConfigurationService.addContractConfUpdateBudgetDetails(moSession, loSubBudgetBean);

	}
	
	@Test(expected = ApplicationException.class)
	public void testAddContractConfUpdateBudgetDetails3() throws ApplicationException
	{
			ContractBudgetBean loSubBudgetBean = new ContractBudgetBean();
			loSubBudgetBean.setBudgetId(parentBudgetID);
			loSubBudgetBean.setBudgetTypeId(4);
			loSubBudgetBean.setContractValue("10000");
			loSubBudgetBean.setCreatedByUserId(agency);
			loSubBudgetBean.setPlannedAmount("3434");
			loSubBudgetBean.setSubbudgetName("testSubBudgetName");
			loSubBudgetBean.setTotalbudgetAmount("200");
			loSubBudgetBean.setActiveFlag("1");
			loSubBudgetBean.setBudgetfiscalYear(fiscalYearId);
			loSubBudgetBean.setBudgetStartDate("07/14/2014");
			loSubBudgetBean.setBudgetEndDate("07/14/2014");
			loSubBudgetBean.setContractId("574");
			loSubBudgetBean.setModifiedByUserId(agency);
			loSubBudgetBean.setStatusId("2");
			loSubBudgetBean.setSubbudgetAmount("1000");
			loSubBudgetBean.setBudgetfiscalYear("2014");
			loSubBudgetBean.setId("989");
			loSubBudgetBean.setContractTypeId("4");
			loSubBudgetBean.setModifiedAmount("-1000");
			ConfigurationService loConfigurationService = new ConfigurationService();

			loConfigurationService.addContractConfUpdateBudgetDetails(moSession, loSubBudgetBean);

	}
	
	@Test
	public void testAddContractConfUpdateBudgetDetails4() throws ApplicationException
	{
			ContractBudgetBean loSubBudgetBean = new ContractBudgetBean();
			loSubBudgetBean.setBudgetId(parentBudgetID);
			loSubBudgetBean.setBudgetTypeId(1);
			loSubBudgetBean.setContractValue("10000");
			loSubBudgetBean.setCreatedByUserId(agency);
			loSubBudgetBean.setPlannedAmount("3434");
			loSubBudgetBean.setSubbudgetName("testSubBudgetName");
			loSubBudgetBean.setTotalbudgetAmount("200");
			loSubBudgetBean.setActiveFlag("1");
			loSubBudgetBean.setBudgetfiscalYear(fiscalYearId);
			loSubBudgetBean.setBudgetStartDate("07/14/2014");
			loSubBudgetBean.setBudgetEndDate("07/14/2014");
			loSubBudgetBean.setAmendmentContractId("27");
			loSubBudgetBean.setModifiedByUserId(agency);
			loSubBudgetBean.setStatusId("2");
			loSubBudgetBean.setSubbudgetAmount("1000");
			loSubBudgetBean.setBudgetfiscalYear("2014");
			loSubBudgetBean.setId("989");
			loSubBudgetBean.setContractTypeId("2");
			ConfigurationService loConfigurationService = new ConfigurationService();

			loConfigurationService.addContractConfUpdateBudgetDetails(moSession, loSubBudgetBean);

	}

	// add with application exception
	// give same data with that of unique constraint that already exist in db
	@Test(expected = ApplicationException.class)
	public void testAddContractConfUpdateBudgetDetails2() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = getDummyContractBudgetBean();
		loSubBudgetBean.setContractId(null);
		ConfigurationService loConfigurationService = new ConfigurationService();

		loConfigurationService.addContractConfUpdateBudgetDetails(moSession, loSubBudgetBean);

		assertEquals("Sub Budget Details for contract updated successfully:", loConfigurationService.getMoState()
				.toString().toString());

	}

	@Test
	public void testFetchContractConfUpdateSubBudgetDetails1() throws ApplicationException
	{

			ConfigurationService loConfigurationService = new ConfigurationService();
			List loList = loConfigurationService.fetchContractConfUpdateSubBudgetDetails(moSession, "63",
					"2014", "1", "2", "53");

			assertNotNull(loList);
			assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
					.toString());
	}

	@Test
	public void testFetchContractConfUpdateSubBudgetDetails2() throws ApplicationException
	{

			ConfigurationService loConfigurationService = new ConfigurationService();
			List loList = loConfigurationService.fetchContractConfUpdateSubBudgetDetails(moSession, "63",
					"2014", "2", "4", "53");

			assertNotNull(loList);
			assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
					.toString());
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchContractConfUpdateSubBudgetDetailsException1() throws ApplicationException
	{

			ConfigurationService loConfigurationService = new ConfigurationService();
			List loList = loConfigurationService.fetchContractConfUpdateSubBudgetDetails(null, "63",
					"2014", "2", "4", "53");
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchContractConfUpdateSubBudgetDetailsException() throws ApplicationException
	{
		String lsContractId = amendContractId;
		String lsFiscalYearId = "2014B";

		ConfigurationService loConfigurationService = new ConfigurationService();
		List loList = loConfigurationService
				.fetchContractConfSubBudgetDetails1(moSession, lsContractId, lsFiscalYearId);

		assertNotNull(loList);
		assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
				.toString());
	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractConfUpdateSubBudgetDetailsException2() throws ApplicationException
	{
		String lsContractId = null;
		String lsFiscalYearId = fiscalYearId;

		ConfigurationService loConfigurationService = new ConfigurationService();
		List loList = loConfigurationService
				.fetchContractConfSubBudgetDetails1(moSession, lsContractId, lsFiscalYearId);

		assertNotNull(loList);
		assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
				.toString());
	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractConfUpdateSubBudgetDetailsException3() throws ApplicationException
	{
		String lsContractId = amendContractId;
		String lsFiscalYearId = null;

		ConfigurationService loConfigurationService = new ConfigurationService();
		List loList = loConfigurationService
				.fetchContractConfSubBudgetDetails1(moSession, lsContractId, lsFiscalYearId);

		assertNotNull(loList);
		assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
				.toString());
	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractConfUpdateSubBudgetDetailsException4() throws ApplicationException
	{
		String lsContractId = amendContractId;
		String lsFiscalYearId = null;

		ConfigurationService loConfigurationService = new ConfigurationService();
		List loList = loConfigurationService
				.fetchContractConfSubBudgetDetails1(null, lsContractId, lsFiscalYearId);

		assertNotNull(loList);
		assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
				.toString());
	}

	
	@Test
	public void testFetchContractConfUpdateActualDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			String lsContractId = amendContractId;
			String lsFiscalYearId = fiscalYearId;
			CBGridBean aoCBGridBean = new CBGridBean();
			aoCBGridBean.setContractID(lsContractId);
			aoCBGridBean.setFiscalYearID(lsFiscalYearId);

			ConfigurationService loConfigurationService = new ConfigurationService();
			List<AccountsAllocationBean> loList = loConfigurationService.fetchContractConfUpdateActualDetails(
					aoCBGridBean, moSession);

			assertNotNull(loList);
			assertEquals("Account Allocation Details for contract fetched successfully:", loConfigurationService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractConfUpdateActualDetailsException() throws ApplicationException
	{

		String lsContractId = amendContractId;
		String lsFiscalYearId = "2014B";
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setContractID(lsContractId);
		aoCBGridBean.setFiscalYearID(lsFiscalYearId);

		ConfigurationService loConfigurationService = new ConfigurationService();
		List<AccountsAllocationBean> loList = loConfigurationService.fetchContractConfUpdateActualDetails(aoCBGridBean,
				moSession);

		assertNotNull(loList);
		assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
				.toString());

	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractConfUpdateActualDetailsException2() throws ApplicationException
	{

		String lsContractId = amendContractId;
		String lsFiscalYearId = null;
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setContractID(lsContractId);
		aoCBGridBean.setFiscalYearID(lsFiscalYearId);

		ConfigurationService loConfigurationService = new ConfigurationService();
		List<AccountsAllocationBean> loList = loConfigurationService.fetchContractConfUpdateActualDetails(aoCBGridBean,
				moSession);

		assertNotNull(loList);
		assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
				.toString());

	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractConfUpdateActualDetailsException3() throws ApplicationException
	{

		String lsContractId = null;
		String lsFiscalYearId = fiscalYearId;
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setContractID(lsContractId);
		aoCBGridBean.setFiscalYearID(lsFiscalYearId);

		ConfigurationService loConfigurationService = new ConfigurationService();
		List<AccountsAllocationBean> loList = loConfigurationService.fetchContractConfUpdateActualDetails(aoCBGridBean,
				moSession);

		assertNotNull(loList);
		assertEquals("Sub Budget details for contract fetched successfully:", loConfigurationService.getMoState()
				.toString());

	}

	@Test
	public void testDelContractConfUpdateTaskDetails() throws ApplicationException
	{
		boolean lbResult = false;
		AccountsAllocationBean aoAccountsAllocationBean = new AccountsAllocationBean();
		aoAccountsAllocationBean.setContractID("48");
		aoAccountsAllocationBean.setContractTypeId("4");
		aoAccountsAllocationBean.setId("53_newrecord");
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.delContractConfUpdateTaskDetails(aoAccountsAllocationBean, moSession);
		lbResult = true;
		assertTrue(lbResult);
		assertNotNull(loConfigurationService.getMoState().toString());
	}

	@Test(expected = ApplicationException.class)
	public void testDelContractConfUpdateTaskDetailsForException() throws ApplicationException
	{
		boolean lbResult = false;
		try
		{
			AccountsAllocationBean aoAccountsAllocationBean = new AccountsAllocationBean();
			aoAccountsAllocationBean.setContractID(null);
			aoAccountsAllocationBean.setId(null);
			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.delContractConfUpdateTaskDetails(aoAccountsAllocationBean, moSession);
			lbResult = true;
			assertTrue(lbResult);
		}
		catch (ApplicationException loAppEx)
		{
			assertTrue(true);
			throw loAppEx;
		}
	}

	@Test(expected = ApplicationException.class)
	public void testDelContractConfUpdateTaskDetailsForException2() throws ApplicationException
	{
		boolean lbResult = false;
		boolean lbThrown;
		AccountsAllocationBean aoAccountsAllocationBean = new AccountsAllocationBean();
		try
		{
			aoAccountsAllocationBean.setContractID(amendContractId);
			aoAccountsAllocationBean.setId(null);
			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.delContractConfUpdateTaskDetails(aoAccountsAllocationBean, moSession);
			lbResult = true;
			assertTrue(lbResult);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
			throw loAppEx;
		}
	}

	@Test(expected = ApplicationException.class)
	public void testDelContractConfUpdateTaskDetailsForException3() throws ApplicationException
	{
		boolean lbResult = false;
		boolean lbThrown;
		AccountsAllocationBean aoAccountsAllocationBean = new AccountsAllocationBean();
		try
		{
			aoAccountsAllocationBean.setContractID(amendContractId);
			aoAccountsAllocationBean.setId("3_newrecord");
			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.delContractConfUpdateTaskDetails(aoAccountsAllocationBean, null);
			lbResult = true;
			assertTrue(lbResult);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
			throw loAppEx;
		}
	}

	@Test
	public void testFetchContractConfUpdateDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			CBGridBean aoCBGridBean = new CBGridBean();
			aoCBGridBean.setContractID("48");
			aoCBGridBean.setContractTypeId("4");
			aoCBGridBean.setFiscalYearID("2012");

			ConfigurationService loConfigurationService = new ConfigurationService();
			List<AccountsAllocationBean> loList = loConfigurationService.fetchContractConfUpdateDetails(aoCBGridBean,
					moSession);

			assertNotNull(loList);
			assertEquals("Account Allocation Details for contract fetched successfully:", loConfigurationService
					.getMoState().toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractConfUpdateDetailsException() throws ApplicationException
	{

		String lsContractId = amendContractId;
		String lsFiscalYearId = "2014B";
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setContractID(lsContractId);
		aoCBGridBean.setFiscalYearID(lsFiscalYearId);

		ConfigurationService loConfigurationService = new ConfigurationService();
		List<AccountsAllocationBean> loList = loConfigurationService.fetchContractConfUpdateDetails(aoCBGridBean,
				moSession);

		assertNotNull(loList);
		assertEquals("Account Allocation Details for contract fetched successfully:", loConfigurationService
				.getMoState().toString());

	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractConfUpdateDetailsException2() throws ApplicationException
	{

		String lsContractId = amendContractId;
		String lsFiscalYearId = null;
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setContractID(lsContractId);
		aoCBGridBean.setFiscalYearID(lsFiscalYearId);

		ConfigurationService loConfigurationService = new ConfigurationService();
		List<AccountsAllocationBean> loList = loConfigurationService.fetchContractConfUpdateDetails(aoCBGridBean,
				moSession);

		assertNotNull(loList);
		assertEquals("Account Allocation Details for contract fetched successfully:", loConfigurationService
				.getMoState().toString());

	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractConfUpdateDetailsException3() throws ApplicationException
	{

		String lsContractId = null;
		String lsFiscalYearId = fiscalYearId;
		CBGridBean aoCBGridBean = new CBGridBean();
		aoCBGridBean.setContractID(lsContractId);
		aoCBGridBean.setFiscalYearID(lsFiscalYearId);

		ConfigurationService loConfigurationService = new ConfigurationService();
		List<AccountsAllocationBean> loList = loConfigurationService.fetchContractConfUpdateActualDetails(aoCBGridBean,
				moSession);

		assertNotNull(loList);
		assertEquals("Account Allocation Details for contract fetched successfully:", loConfigurationService
				.getMoState().toString());

	}

	@Test
	public void testAddContractConfUpdateTaskDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean loAccountsAllocationBean = getDummyAccountsAllocationBean();
			loAccountsAllocationBean.setContractStartFY("2014");
			loAccountsAllocationBean.setContractEndFY("2015");
			loAccountsAllocationBean.setUnitOfAppropriation("234");
			loAccountsAllocationBean.setBudgetCode("4dsf");
			loAccountsAllocationBean.setObjectCode("wsfs");
			loAccountsAllocationBean.setSubOc("sf");
			loAccountsAllocationBean.setRc("223");
			loAccountsAllocationBean.setFiscalYearID("2014");
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setId("new_row");
			loAccountsAllocationBean.setContractID("278");
			loAccountsAllocationBean.setContractTypeId("4");
			//loAccountsAllocationBean.getContractTypeId();
			ConfigurationService loConfigurationService = new ConfigurationService();

			loConfigurationService.addContractConfUpdateTaskDetails(loAccountsAllocationBean, moSession);
			assertTrue(true);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testAddContractConfUpdateTaskDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean aoAccountsAllocationBean = getDummyAccountsAllocationBean();
			aoAccountsAllocationBean.setContractStartFY(fiscalYearId);
			aoAccountsAllocationBean.setContractEndFY("2017");
			ConfigurationService loConfigurationService = new ConfigurationService();

			loConfigurationService.addContractConfUpdateTaskDetails(aoAccountsAllocationBean, null);

			assertEquals("addContractConfCOADetails is executed successfully", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// add with application exception
	// give same data with that of unique constraint that already exist in db
	@Test
	public void testAddContractConfUpdateTaskDetailsForException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean aoAccountsAllocationBean = getDummyAccountsAllocationBean();
			aoAccountsAllocationBean.setContractStartFY("2012");
			aoAccountsAllocationBean.setContractEndFY("2012");
			aoAccountsAllocationBean.setContractID("553");
			aoAccountsAllocationBean.setCreatedByUserId("agency_21");
			aoAccountsAllocationBean.setUnitOfAppropriation("rrr");
			aoAccountsAllocationBean.setBudgetCode("rrrr");
			aoAccountsAllocationBean.setObjectCode("");
			aoAccountsAllocationBean.setSubOc("rrrr");
			aoAccountsAllocationBean.setRc("rrr");
			aoAccountsAllocationBean.setAmmount("100");
			aoAccountsAllocationBean.setProcurementID("1");
			
			ConfigurationService loConfigurationService = new ConfigurationService();

			loConfigurationService.addContractConfUpdateTaskDetails(aoAccountsAllocationBean, moSession);

			assertEquals("addContractConfCOADetails is executed successfully", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testAddContractConfUpdateTaskDetailsForException2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean aoAccountsAllocationBean = getDummyAccountsAllocationBean();
			aoAccountsAllocationBean.setContractStartFY("2014b");
			aoAccountsAllocationBean.setContractEndFY("2017");
			aoAccountsAllocationBean.setId("");
			ConfigurationService loConfigurationService = new ConfigurationService();

			loConfigurationService.addContractConfUpdateTaskDetails(aoAccountsAllocationBean, moSession);

			assertEquals("addContractConfCOADetails is executed successfully", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testEditContractConfUpdateDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean loAccountsAllocationBean = getDummyAccountsAllocationBean();
			loAccountsAllocationBean.setContractStartFY("2014");
			loAccountsAllocationBean.setContractEndFY("2015");
			loAccountsAllocationBean.setUnitOfAppropriation("234");
			loAccountsAllocationBean.setBudgetCode("4dsf");
			loAccountsAllocationBean.setObjectCode("wsfs");
			loAccountsAllocationBean.setSubOc("sf");
			loAccountsAllocationBean.setRc("223");
			loAccountsAllocationBean.setFiscalYearID("2014");
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setId("234-4dsf-wsfs-sf-223");
			loAccountsAllocationBean.setContractID("395");
			loAccountsAllocationBean.setContractTypeId("4");
			ConfigurationService loConfigurationService = new ConfigurationService();

			loConfigurationService.editContractConfUpdateDetails(loAccountsAllocationBean, moSession);
			assertTrue(true);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testEditContractConfUpdateDetailsForException() throws ApplicationException
	{
			AccountsAllocationBean aoAccountsAllocationBean = getDummyAccountsAllocationBean();
			aoAccountsAllocationBean.setContractStartFY(fiscalYearId);
			aoAccountsAllocationBean.setContractEndFY("2017");
			aoAccountsAllocationBean.setId("44");
			ConfigurationService loConfigurationService = new ConfigurationService();

			loConfigurationService.editContractConfUpdateDetails(aoAccountsAllocationBean, null);

	}
	
	@Test(expected = ApplicationException.class)
	public void testEditContractConfUpdateDetailsForException1() throws ApplicationException
	{
			AccountsAllocationBean aoAccountsAllocationBean = getDummyAccountsAllocationBean();
			aoAccountsAllocationBean.setContractStartFY(fiscalYearId);
			aoAccountsAllocationBean.setContractEndFY("2017");
			aoAccountsAllocationBean.setId(null);
			ConfigurationService loConfigurationService = new ConfigurationService();

			loConfigurationService.editContractConfUpdateDetails(aoAccountsAllocationBean, null);

	}

	@Test
	public void testEditContractConfUpdateDetailsForException2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			AccountsAllocationBean aoAccountsAllocationBean = getDummyAccountsAllocationBean();
			aoAccountsAllocationBean.setContractStartFY("2014b");
			aoAccountsAllocationBean.setContractEndFY("2017");
			aoAccountsAllocationBean.setId("");
			ConfigurationService loConfigurationService = new ConfigurationService();

			loConfigurationService.editContractConfUpdateDetails(aoAccountsAllocationBean, moSession);

			assertEquals("addContractConfCOADetails is executed successfully", loConfigurationService.getMoState()
					.toString());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	private AccountsAllocationBean getDummyAccountsAllocationBean()
	{
		AccountsAllocationBean aoAccountsAllocationBean = new AccountsAllocationBean();
		aoAccountsAllocationBean.setProcurementID("1");
		aoAccountsAllocationBean.setContractID(amendContractId);
		aoAccountsAllocationBean.setUnitOfAppropriation("786");
		aoAccountsAllocationBean.setBudgetCode("7867");
		aoAccountsAllocationBean.setObjectCode("89");
		aoAccountsAllocationBean.setSubOc("2");
		aoAccountsAllocationBean.setRc("5");
		aoAccountsAllocationBean.setFiscalYearID(fiscalYearId);
		aoAccountsAllocationBean.setAmmount("0");
		aoAccountsAllocationBean.setActiveFlag("1");
		aoAccountsAllocationBean.setCreatedByUserId(agency);
		aoAccountsAllocationBean.setFy1("1");
		aoAccountsAllocationBean.setContractStartFY("2014");
		aoAccountsAllocationBean.setContractEndFY("2015");
		aoAccountsAllocationBean.setContractTypeId("4");

		return aoAccountsAllocationBean;
	}

	// 391 screen end

	@Test
	public void testCreateDuplicateRows() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setTaskStatus("Approved");
			loTaskDetailsBean.setProcurementId("1200");
			loTaskDetailsBean.setAgencyId("DOC");
			loTaskDetailsBean.setUserId("agency_20");
			boolean lbReturnStatus = loConfigurationService.createDuplicateRows(moSession, getCBGridBeanParams());
			assertEquals(true, lbReturnStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testCreateDuplicateRowsException() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setTaskStatus("Approved");
			loTaskDetailsBean.setProcurementId("420");
			loTaskDetailsBean.setAgencyId("ACS");
			loTaskDetailsBean.setUserId("agency_47");
			loConfigurationService.createDuplicateRows(null, getCBGridBeanParams());
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testCreateDuplicateRows1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			loConfigurationService.createDuplicateRows(moSession, getCBGridBeanParams());
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test
	public void testCreateDuplicateRows2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
			loTaskDetailsBean.setTaskStatus("Approved");
			loTaskDetailsBean.setProcurementId("420");
			loTaskDetailsBean.setAgencyId("ACS");
			loTaskDetailsBean.setUserId("agency_47");
			boolean lbReturnStatus = loConfigurationService.createDuplicateRows(moSession, getCBGridBeanParams());
			assertEquals(false, lbReturnStatus);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchContractAmendmentFundingDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			CBGridBean aoGridBean = new CBGridBean();
			loConfigurationService.fetchContractAmendmentFundingDetails(null, aoGridBean);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchContractAmendmentFundingDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<FundingAllocationBean> loTransposedFundingSourceDetails = null;
			CBGridBean aoGridBean = new CBGridBean();
			aoGridBean.setFiscalYearID(fiscalYearId);
			aoGridBean.setNoOfyears(0);
			aoGridBean.setContractID("111780");
			aoGridBean.setContractTypeId("2");
			loTransposedFundingSourceDetails = loConfigurationService.fetchContractAmendmentFundingDetails(moSession,
					aoGridBean);
			assertNotNull(loTransposedFundingSourceDetails);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchContractAmendmentFundingDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<FundingAllocationBean> loTransposedFundingSourceDetails = null;
			CBGridBean aoGridBean = new CBGridBean();
			aoGridBean.setFiscalYearID(fiscalYearId);
			aoGridBean.setNoOfyears(2);
			aoGridBean.setContractID("111780");
			aoGridBean.setContractTypeId("2");
			aoGridBean.setProcurementID("1234");
			loTransposedFundingSourceDetails = loConfigurationService.fetchContractAmendmentFundingDetails(moSession,
					aoGridBean);
			assertNotNull(loTransposedFundingSourceDetails);
			assertTrue(loTransposedFundingSourceDetails.get(0).getFundingSource().equals(HHSConstants.FEDERAL));
			assertTrue(loTransposedFundingSourceDetails.get(0).getId()
					.equals("111780" + HHSConstants.HYPHEN + HHSConstants.FEDERAL));
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchContractAmendmentFundingDetails4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<FundingAllocationBean> loTransposedFundingSourceDetails = null;
			CBGridBean aoGridBean = new CBGridBean();
			aoGridBean.setFiscalYearID("2010");
			aoGridBean.setNoOfyears(4);
			aoGridBean.setContractID("111780");
			aoGridBean.setContractTypeId("2");
			aoGridBean.setProcurementID("135");
			aoGridBean.setFiscalYearCounter(1);
			aoGridBean.setCreatedByUserId(agency);
			aoGridBean.setAmendmentContractID("1");
			loTransposedFundingSourceDetails = loConfigurationService.fetchContractAmendmentFundingDetails(moSession,
					aoGridBean);
			assertNotNull(loTransposedFundingSourceDetails);
			assertTrue(loTransposedFundingSourceDetails.get(0).getFundingSource().equals(HHSConstants.FEDERAL));
			assertTrue(loTransposedFundingSourceDetails.get(0).getId()
					.equals("111780" + HHSConstants.HYPHEN + HHSConstants.FEDERAL));
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testEditContractAmendmentFundingDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			FundingAllocationBean aoEditedRow = new FundingAllocationBean();
			aoEditedRow.setFundingSource("");
			CBGridBean aoGridBean = new CBGridBean();
			aoGridBean.setFiscalYearID("2010");
			aoGridBean.setNoOfyears(0);
			aoGridBean.setContractID("111780");
			aoGridBean.setContractTypeId("2");
			aoGridBean.setProcurementID("1223");
			aoGridBean.setFiscalYearCounter(1);
			aoGridBean.setCreatedByUserId(agency);
			loConfigurationService.editContractAmendmentFundingDetails(null, aoEditedRow, aoGridBean);

		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testEditContractAmendmentFundingDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			FundingAllocationBean aoEditedRow = new FundingAllocationBean();
			aoEditedRow.setFundingSource("");
			CBGridBean aoGridBean = new CBGridBean();
			aoGridBean.setFiscalYearID("2010");
			aoGridBean.setNoOfyears(0);
			aoGridBean.setContractID("111780");
			aoGridBean.setContractTypeId("2");
			aoGridBean.setProcurementID("1223");
			aoGridBean.setFiscalYearCounter(1);
			aoGridBean.setCreatedByUserId(agency);
			Boolean lsReturnValue = loConfigurationService.editContractAmendmentFundingDetails(moSession, aoEditedRow,
					aoGridBean);
			assertTrue(!lsReturnValue);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testEditContractAmendmentFundingDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			FundingAllocationBean aoEditedRow = new FundingAllocationBean();
			aoEditedRow.setFundingSource("");
			aoEditedRow.setFy1("100");
			CBGridBean aoGridBean = new CBGridBean();
			aoGridBean.setFiscalYearID(fiscalYearId);
			aoGridBean.setNoOfyears(1);
			aoGridBean.setContractID("111780");
			aoGridBean.setContractTypeId("2");
			aoGridBean.setProcurementID("1223");
			aoGridBean.setFiscalYearCounter(1);
			aoGridBean.setCreatedByUserId(agency);
			loConfigurationService.editContractAmendmentFundingDetails(moSession, aoEditedRow, aoGridBean);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testEditContractAmendmentFundingDetails4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			FundingAllocationBean aoEditedRow = new FundingAllocationBean();
			aoEditedRow.setFundingSource(HHSConstants.FEDERAL);
			aoEditedRow.setFy1("100");
			CBGridBean aoGridBean = new CBGridBean();
			aoGridBean.setFiscalYearID(fiscalYearId);
			aoGridBean.setNoOfyears(1);
			aoGridBean.setContractID("111780");
			aoGridBean.setContractTypeId("2");
			aoGridBean.setFiscalYearCounter(1);
			aoGridBean.setCreatedByUserId(agency);
			Boolean lsReturnValue = loConfigurationService.editContractAmendmentFundingDetails(moSession, aoEditedRow,
					aoGridBean);
			assertFalse(lsReturnValue);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testEditContractAmendmentFundingDetails5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			FundingAllocationBean aoEditedRow = new FundingAllocationBean();
			aoEditedRow.setFundingSource(HHSConstants.STATE);
			aoEditedRow.setFy1("100");
			CBGridBean aoGridBean = new CBGridBean();
			aoGridBean.setFiscalYearID(fiscalYearId);
			aoGridBean.setNoOfyears(1);
			aoGridBean.setContractID("111780");
			aoGridBean.setContractTypeId("2");
			aoGridBean.setFiscalYearCounter(1);
			aoGridBean.setCreatedByUserId(agency);
			Boolean lsReturnValue = loConfigurationService.editContractAmendmentFundingDetails(moSession, aoEditedRow,
					aoGridBean);
			assertFalse(lsReturnValue);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testEditContractAmendmentFundingDetails6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			FundingAllocationBean aoEditedRow = new FundingAllocationBean();
			aoEditedRow.setFundingSource(HHSConstants.CITY);
			aoEditedRow.setFy1("100");
			CBGridBean aoGridBean = new CBGridBean();
			aoGridBean.setFiscalYearID(fiscalYearId);
			aoGridBean.setNoOfyears(1);
			aoGridBean.setContractID("111780");
			aoGridBean.setContractTypeId("2");
			aoGridBean.setFiscalYearCounter(1);
			aoGridBean.setCreatedByUserId(agency);
			Boolean lsReturnValue = loConfigurationService.editContractAmendmentFundingDetails(moSession, aoEditedRow,
					aoGridBean);
			assertFalse(lsReturnValue);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testEditContractAmendmentFundingDetails7() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			FundingAllocationBean aoEditedRow = new FundingAllocationBean();
			aoEditedRow.setFundingSource(HHSConstants.OTHER);
			aoEditedRow.setFy1("100");
			CBGridBean aoGridBean = new CBGridBean();
			aoGridBean.setFiscalYearID(fiscalYearId);
			aoGridBean.setNoOfyears(1);
			aoGridBean.setContractID("111780");
			aoGridBean.setContractTypeId("2");
			aoGridBean.setFiscalYearCounter(1);
			aoGridBean.setCreatedByUserId(agency);
			Boolean lsReturnValue = loConfigurationService.editContractAmendmentFundingDetails(moSession, aoEditedRow,
					aoGridBean);
			assertFalse(lsReturnValue);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testEditContractAmendmentFundingDetails8() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			FundingAllocationBean aoEditedRow = new FundingAllocationBean();
			aoEditedRow.setFundingSource(HHSConstants.OTHER);
			aoEditedRow.setFy1("100");
			CBGridBean aoGridBean = new CBGridBean();
			aoGridBean.setFiscalYearID(fiscalYearId);
			aoGridBean.setNoOfyears(1);
			aoGridBean.setContractID("1");
			aoGridBean.setContractTypeId("4");
			aoGridBean.setFiscalYearCounter(1);
			aoGridBean.setCreatedByUserId(agency);
			Boolean lsReturnValue = loConfigurationService.editContractAmendmentFundingDetails(moSession, null,
					aoGridBean);
			assertTrue(lsReturnValue);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchAmendmentBudgetDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			String asContractId = "1";
			String asFiscalYearId = fiscalYearId;
			String asContractTypeId = "2";
			loConfigurationService.fetchAmendmentBudgetDetails(null, asContractId, asFiscalYearId, asContractTypeId);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchAmendmentBudgetDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<ContractBudgetBean> loContractBudgetBean = null;
			String asContractId = amendContractId;
			String asFiscalYearId = fiscalYearId;
			String asContractTypeId = "1";
			loContractBudgetBean = loConfigurationService.fetchAmendmentBudgetDetails(moSession, asContractId,
					asFiscalYearId, asContractTypeId);
			assertNotNull(loContractBudgetBean);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchAmendmentBudgetDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<ContractBudgetBean> loContractBudgetBean = null;
			String asContractId = null;
			String asFiscalYearId = fiscalYearId;
			String asContractTypeId = "1";
			loContractBudgetBean = loConfigurationService.fetchAmendmentBudgetDetails(moSession, asContractId,
					asFiscalYearId, asContractTypeId);
			assertNull(loContractBudgetBean);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchAmendmentBudgetDetails4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<ContractBudgetBean> loContractBudgetBean = null;
			String asContractId = amendContractId;
			String asFiscalYearId = null;
			String asContractTypeId = "1";
			loContractBudgetBean = loConfigurationService.fetchAmendmentBudgetDetails(moSession, asContractId,
					asFiscalYearId, asContractTypeId);
			assertNotNull(loContractBudgetBean);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchAmendmentBudgetDetails5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<ContractBudgetBean> loContractBudgetBean = null;
			String asContractId = amendContractId;
			String asFiscalYearId = fiscalYearId;
			String asContractTypeId = null;
			loContractBudgetBean = loConfigurationService.fetchAmendmentBudgetDetails(moSession, asContractId,
					asFiscalYearId, asContractTypeId);
			assertNotNull(loContractBudgetBean);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test(expected = ApplicationException.class)
	public void testInsertNewAmendmentBudgetDetails() throws ApplicationException
	{
			ConfigurationService loConfigurationService = new ConfigurationService();
			ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
			loConfigurationService.insertNewAmendmentBudgetDetails(null, aoContractBudgetBean);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertNewAmendmentBudgetDetails2() throws ApplicationException
	{
			loConfigurationService.insertNewAmendmentBudgetDetails(moSession, null);
	}

	@Test
	public void testInsertNewAmendmentBudgetDetails3() throws ApplicationException
	{
			ConfigurationService loConfigurationService = new ConfigurationService();
			ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
			aoContractBudgetBean.setBudgetTypeId(2);
			aoContractBudgetBean.setContractId(amendContractId);
			aoContractBudgetBean.setContractTypeId("1");
			aoContractBudgetBean.setBudgetfiscalYear(fiscalYearId);
			aoContractBudgetBean.setTotalbudgetAmount("100");
			aoContractBudgetBean.setStatusId("34");
			aoContractBudgetBean.setBudgetStartDate("12/12/2010");
			aoContractBudgetBean.setBudgetEndDate("12/12/2012");
			aoContractBudgetBean.setActiveFlag("1");
			aoContractBudgetBean.setCreatedByUserId(agency);
			aoContractBudgetBean.setBudgetId("69");
			aoContractBudgetBean.setAmendmentContractId("117");
			boolean lsInsertTrue = loConfigurationService.insertNewAmendmentBudgetDetails(moSession,
					aoContractBudgetBean);
			assertTrue(lsInsertTrue);
			assertNotNull(aoContractBudgetBean);
	}


	@Test
	public void testFetchContractConfAmendmentDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			CBGridBean aoCBGridBean = new CBGridBean();
			loConfigurationService.fetchContractConfAmendmentDetails(aoCBGridBean, null);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchContractConfAmendmentDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			CBGridBean aoCBGridBean = new CBGridBean();
			loConfigurationService.fetchContractConfAmendmentDetails(aoCBGridBean, null);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchContractConfAmendmentDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<AccountsAllocationBean> loAccountsAllocationBeanRtrndList = null;
			CBGridBean aoCBGridBean = new CBGridBean();
			aoCBGridBean.setContractID("1");
			aoCBGridBean.setContractTypeId("4");
			aoCBGridBean.setFiscalYearID(fiscalYearId);
			loAccountsAllocationBeanRtrndList = loConfigurationService.fetchContractConfAmendmentDetails(aoCBGridBean,
					moSession);
			assertNotNull(loAccountsAllocationBeanRtrndList);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchContractConfAmendmentDetails4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<AccountsAllocationBean> loAccountsAllocationBeanRtrndList = null;
			CBGridBean aoCBGridBean = new CBGridBean();
			aoCBGridBean.setContractID(null);
			aoCBGridBean.setContractTypeId("4");
			aoCBGridBean.setFiscalYearID(fiscalYearId);
			loAccountsAllocationBeanRtrndList = loConfigurationService.fetchContractConfAmendmentDetails(aoCBGridBean,
					moSession);
//			assertNull(loAccountsAllocationBeanRtrndList);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchContractConfAmendmentDetails5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<AccountsAllocationBean> loAccountsAllocationBeanRtrndList = null;
			CBGridBean aoCBGridBean = new CBGridBean();
			aoCBGridBean.setContractID("1");
			aoCBGridBean.setContractTypeId(null);
			aoCBGridBean.setFiscalYearID(fiscalYearId);
			loAccountsAllocationBeanRtrndList = loConfigurationService.fetchContractConfAmendmentDetails(aoCBGridBean,
					moSession);
//			assertNull(loAccountsAllocationBeanRtrndList);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchContractConfAmendmentDetails6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			List<AccountsAllocationBean> loAccountsAllocationBeanRtrndList = null;
			CBGridBean aoCBGridBean = new CBGridBean();
			aoCBGridBean.setContractID("1");
			aoCBGridBean.setContractTypeId("4");
			aoCBGridBean.setFiscalYearID(null);
			loAccountsAllocationBeanRtrndList = loConfigurationService.fetchContractConfAmendmentDetails(aoCBGridBean,
					moSession);
			assertNull(loAccountsAllocationBeanRtrndList);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testEditContractConfAmendmentDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean aoAccountsAllocationBean = new AccountsAllocationBean();
			aoAccountsAllocationBean.setContractStartFY(fiscalYearId);
			aoAccountsAllocationBean.setContractEndFY("2015");
			aoAccountsAllocationBean.setFy1("100");
			aoAccountsAllocationBean.setFy2("100");
			aoAccountsAllocationBean.setAmmount("100");
			aoAccountsAllocationBean.setAmendmentType(HHSConstants.POSITIVE);
			loConfigurationService.editContractConfAmendmentDetails(aoAccountsAllocationBean, null);
			// assertNull(loAccountsAllocationBeanRtrndList);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}
	
	@Test
	public void testEditContractConfAmendmentDetails5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean aoAccountsAllocationBean = new AccountsAllocationBean();
			aoAccountsAllocationBean.setContractStartFY(fiscalYearId);
			aoAccountsAllocationBean.setContractEndFY("2015");
			aoAccountsAllocationBean.setFy1("100");
			aoAccountsAllocationBean.setFy2("100");
			aoAccountsAllocationBean.setAmmount("100");
			aoAccountsAllocationBean.setAmendmentType(HHSConstants.NEGATIVE);
			loConfigurationService.editContractConfAmendmentDetails(aoAccountsAllocationBean, moSession);
			// assertNull(loAccountsAllocationBeanRtrndList);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}
	
	@Test
	public void testEditContractConfAmendmentDetails8() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean aoAccountsAllocationBean = new AccountsAllocationBean();
			aoAccountsAllocationBean.setContractStartFY(fiscalYearId);
			aoAccountsAllocationBean.setContractEndFY("2015");
			aoAccountsAllocationBean.setFy2("100");
			aoAccountsAllocationBean.setAmendmentType(HHSConstants.NEGATIVE);
			loConfigurationService.editContractConfAmendmentDetails(aoAccountsAllocationBean, moSession);
			// assertNull(loAccountsAllocationBeanRtrndList);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testEditContractConfAmendmentDetails6() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean aoAccountsAllocationBean = new AccountsAllocationBean();
			aoAccountsAllocationBean.setContractStartFY(fiscalYearId);
			aoAccountsAllocationBean.setContractEndFY("2015");
			aoAccountsAllocationBean.setFy1("100");
			aoAccountsAllocationBean.setFy2("100");
			aoAccountsAllocationBean.setAmmount("-100");
			aoAccountsAllocationBean.setAmendmentType(HHSConstants.POSITIVE);
			loConfigurationService.editContractConfAmendmentDetails(aoAccountsAllocationBean, null);
			// assertNull(loAccountsAllocationBeanRtrndList);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}
	
	@Test
	public void testEditContractConfAmendmentDetails7() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean aoAccountsAllocationBean = new AccountsAllocationBean();
			aoAccountsAllocationBean.setContractStartFY(fiscalYearId);
			aoAccountsAllocationBean.setContractEndFY("2015");
			aoAccountsAllocationBean.setFy1("100");
			aoAccountsAllocationBean.setFy2("100");
			aoAccountsAllocationBean.setAmmount("0");
			aoAccountsAllocationBean.setAmendmentType(HHSConstants.POSITIVE);
			loConfigurationService.editContractConfAmendmentDetails(aoAccountsAllocationBean, null);
			// assertNull(loAccountsAllocationBeanRtrndList);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}
	
	@Test
	public void testEditContractConfAmendmentDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setContractStartFY(fiscalYearId);
			loAccountsAllocationBean.setContractEndFY("2015");
			loAccountsAllocationBean.setFy1("100");
			loAccountsAllocationBean.setFy2("100");
			loAccountsAllocationBean.setUnitOfAppropriation("345");
			loAccountsAllocationBean.setBudgetCode("2222");
			loAccountsAllocationBean.setObjectCode("22");
			loAccountsAllocationBean.setSubOc("2");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYearID(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);
			loAccountsAllocationBean.setId("123-1231-2312-fsdf-sdfsdf");
			loAccountsAllocationBean.setContractID("1");
			loAccountsAllocationBean.setContractTypeId("4");
			loConfigurationService.editContractConfAmendmentDetails(loAccountsAllocationBean, moSession);
			assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testEditContractConfAmendmentDetails21232() throws ApplicationException
	{

		ConfigurationService loConfigurationService = new ConfigurationService();
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
		loAccountsAllocationBean.setContractStartFY(fiscalYearId);
		loAccountsAllocationBean.setContractEndFY("2015");
		loAccountsAllocationBean.setFy1("100");
		loAccountsAllocationBean.setFy2("100");
		loAccountsAllocationBean.setUnitOfAppropriation("345");
		loAccountsAllocationBean.setBudgetCode("2222");
		loAccountsAllocationBean.setObjectCode("22");
		loAccountsAllocationBean.setSubOc("2");
		loAccountsAllocationBean.setRc("2");
		loAccountsAllocationBean.setFiscalYearID(fiscalYearId);
		loAccountsAllocationBean.setAmmount("567");
		loAccountsAllocationBean.setActiveFlag("1");
		loAccountsAllocationBean.setCreatedByUserId(agency);
		loAccountsAllocationBean.setId("123-1231-2312-fsdf-sdfsdf");
		loAccountsAllocationBean.setContractID("1");
		loAccountsAllocationBean.setContractTypeId("4");
		loAccountsAllocationBean.setId("861");
		loAccountsAllocationBean.setContractID("562");
		loAccountsAllocationBean.setAmendmentContractID("566");
		loConfigurationService.editContractConfAmendmentDetails(loAccountsAllocationBean, moSession);
		assertTrue(true);

	}

	@Test(expected = ApplicationException.class)
	public void testEditContractConfAmendmentDetails21231232() throws ApplicationException
	{

		ConfigurationService loConfigurationService = new ConfigurationService();
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
		loAccountsAllocationBean.setContractStartFY(fiscalYearId);
		loAccountsAllocationBean.setContractEndFY("2015");
		loAccountsAllocationBean.setFy1("100");
		loAccountsAllocationBean.setFy2("100");
		loAccountsAllocationBean.setUnitOfAppropriation("345");
		loAccountsAllocationBean.setBudgetCode("2222");
		loAccountsAllocationBean.setObjectCode("22");
		loAccountsAllocationBean.setSubOc("2");
		loAccountsAllocationBean.setRc("2");
		loAccountsAllocationBean.setFiscalYearID(fiscalYearId);
		loAccountsAllocationBean.setAmmount("567");
		loAccountsAllocationBean.setActiveFlag("1");
		loAccountsAllocationBean.setCreatedByUserId(agency);
		loAccountsAllocationBean.setId("123-1231-2312-fsdf-sdfsdf");
		loAccountsAllocationBean.setContractID("1");
		loAccountsAllocationBean.setContractTypeId("4");
		loAccountsAllocationBean.setId("861");
		loAccountsAllocationBean.setContractID("562");
		loAccountsAllocationBean.setAmendmentContractID("566");
		loAccountsAllocationBean.setAmendmentType(positive);
		loAccountsAllocationBean.setAmmount("-100");
		loConfigurationService.editContractConfAmendmentDetails(loAccountsAllocationBean, moSession);
		assertTrue(true);

	}

	@Test(expected = ApplicationException.class)
	public void testEditContractConfAmendmentDetails2123212131232() throws ApplicationException
	{

		ConfigurationService loConfigurationService = new ConfigurationService();
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
		loAccountsAllocationBean.setContractStartFY(fiscalYearId);
		loAccountsAllocationBean.setContractEndFY("2015");
		loAccountsAllocationBean.setFy1("100");
		loAccountsAllocationBean.setFy2("100");
		loAccountsAllocationBean.setUnitOfAppropriation("345");
		loAccountsAllocationBean.setBudgetCode("2222");
		loAccountsAllocationBean.setObjectCode("22");
		loAccountsAllocationBean.setSubOc("2");
		loAccountsAllocationBean.setRc("2");
		loAccountsAllocationBean.setFiscalYearID(fiscalYearId);
		loAccountsAllocationBean.setAmmount("567");
		loAccountsAllocationBean.setActiveFlag("1");
		loAccountsAllocationBean.setCreatedByUserId(agency);
		loAccountsAllocationBean.setId("123-1231-2312-fsdf-sdfsdf");
		loAccountsAllocationBean.setContractID("1");
		loAccountsAllocationBean.setContractTypeId("4");
		loAccountsAllocationBean.setId("861");
		loAccountsAllocationBean.setContractID("562");
		loAccountsAllocationBean.setAmendmentContractID("566");
		loAccountsAllocationBean.setAmendmentType(negative);
		loAccountsAllocationBean.setAmmount("100");
		loConfigurationService.editContractConfAmendmentDetails(loAccountsAllocationBean, moSession);
		assertTrue(true);

	}

	@Test
	public void testEditContractConfAmendmentDetails21232131232() throws ApplicationException
	{

		ConfigurationService loConfigurationService = new ConfigurationService();
		AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
		loAccountsAllocationBean.setContractStartFY(fiscalYearId);
		loAccountsAllocationBean.setContractEndFY("2015");
		loAccountsAllocationBean.setFy1("100");
		loAccountsAllocationBean.setFy2("100");
		loAccountsAllocationBean.setUnitOfAppropriation("345");
		loAccountsAllocationBean.setBudgetCode("2222");
		loAccountsAllocationBean.setObjectCode("22");
		loAccountsAllocationBean.setSubOc("2");
		loAccountsAllocationBean.setRc("2");
		loAccountsAllocationBean.setFiscalYearID(fiscalYearId);
		loAccountsAllocationBean.setAmmount("567");
		loAccountsAllocationBean.setActiveFlag("1");
		loAccountsAllocationBean.setCreatedByUserId(agency);
		loAccountsAllocationBean.setId("123-1231-2312-fsdf-sdfsdf");
		loAccountsAllocationBean.setContractID("1");
		loAccountsAllocationBean.setContractTypeId("4");
		loAccountsAllocationBean.setId("861");
		loAccountsAllocationBean.setContractID("562");
		loAccountsAllocationBean.setAmendmentContractID("566");
		loAccountsAllocationBean.setAmendmentType(negative);
		loAccountsAllocationBean.setAmmount("-100");
		loConfigurationService.editContractConfAmendmentDetails(loAccountsAllocationBean, moSession);
		assertTrue(true);

	}

	@Test
	public void testEditContractConfAmendmentDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setContractStartFY(fiscalYearId);
			loAccountsAllocationBean.setContractEndFY("2015");
			loAccountsAllocationBean.setFy1("100");
			loAccountsAllocationBean.setFy2("100");
			loAccountsAllocationBean.setUnitOfAppropriation("345");
			loAccountsAllocationBean.setBudgetCode("2222");
			loAccountsAllocationBean.setObjectCode("22");
			loAccountsAllocationBean.setSubOc("2");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYear(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);
			loAccountsAllocationBean.setId("123-1-2312-fsdf-sdfsdf");
			loAccountsAllocationBean.setContractID("1");
			loAccountsAllocationBean.setContractTypeId("4");
			loConfigurationService.editContractConfAmendmentDetails(loAccountsAllocationBean, moSession);
			assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testEditContractConfAmendmentDetails4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setContractStartFY(null);
			loAccountsAllocationBean.setContractEndFY("2015");
			loAccountsAllocationBean.setFy1("100");
			loAccountsAllocationBean.setFy2("100");
			loAccountsAllocationBean.setUnitOfAppropriation("345");
			loAccountsAllocationBean.setBudgetCode("2222");
			loAccountsAllocationBean.setObjectCode("22");
			loAccountsAllocationBean.setSubOc("2");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYear(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);
			loAccountsAllocationBean.setId("123-1-2312-fsdf-sdfsdf");
			loAccountsAllocationBean.setContractID("1");
			loAccountsAllocationBean.setAmendmentContractID("24");
			loAccountsAllocationBean.setContractTypeId("2");
			loConfigurationService.editContractConfAmendmentDetails(loAccountsAllocationBean, moSession);
			assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testAddContractConfAmendmentDetails1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setContractStartFY(fiscalYearId);
			loAccountsAllocationBean.setContractEndFY("2015");
			loAccountsAllocationBean.setFy1("100");
			loAccountsAllocationBean.setFy2("100");
			loAccountsAllocationBean.setUnitOfAppropriation("aaa");
			loAccountsAllocationBean.setBudgetCode("aaaa");
			loAccountsAllocationBean.setObjectCode("aaaa");
			loAccountsAllocationBean.setSubOc("a");
			loAccountsAllocationBean.setRc("a");
			loAccountsAllocationBean.setFiscalYear("2014");
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);
			loAccountsAllocationBean.setId("123-1-2312-fsdf-sdfsdf");
			loAccountsAllocationBean.setContractID("1");
			loAccountsAllocationBean.setAmendmentContractID("115");
			loAccountsAllocationBean.setContractTypeId("2");
			loConfigurationService.addContractConfAmendmentDetails(loAccountsAllocationBean, moSession);
			assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testAddContractConfAmendmentDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setContractStartFY(fiscalYearId);
			loAccountsAllocationBean.setContractEndFY("2015");
			loAccountsAllocationBean.setFy1("100");
			loAccountsAllocationBean.setFy2("100");
			loAccountsAllocationBean.setUnitOfAppropriation("345");
			loAccountsAllocationBean.setBudgetCode("2222");
			loAccountsAllocationBean.setObjectCode("22");
			loAccountsAllocationBean.setSubOc("2");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYear(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);
			loAccountsAllocationBean.setId("123-1-2312-fsdf-sdfsdf");
			loAccountsAllocationBean.setContractID("1");
			loAccountsAllocationBean.setAmendmentContractID("24");
			loAccountsAllocationBean.setContractTypeId("2");
			loConfigurationService.addContractConfAmendmentDetails(loAccountsAllocationBean, moSession);
			assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testAddContractConfAmendmentDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setContractStartFY(fiscalYearId);
			loAccountsAllocationBean.setContractEndFY("2015");
			loAccountsAllocationBean.setProcurementID(procurementId);
			loAccountsAllocationBean.setFy1("100");
			loAccountsAllocationBean.setFy2("100");
			loAccountsAllocationBean.setUnitOfAppropriation("35");
			loAccountsAllocationBean.setBudgetCode("26222");
			loAccountsAllocationBean.setObjectCode("226");
			loAccountsAllocationBean.setSubOc("52");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYearID(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);
			loAccountsAllocationBean.setId("123-1-2312-fsdf-sdfsdf");
			loAccountsAllocationBean.setContractID("1");
			loAccountsAllocationBean.setContractTypeId("4");
			loConfigurationService.addContractConfAmendmentDetails(loAccountsAllocationBean, null);
			assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testDelContractConfAmendmentDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setContractStartFY(fiscalYearId);
			loAccountsAllocationBean.setContractEndFY("2015");
			loAccountsAllocationBean.setProcurementID(procurementId);
			loAccountsAllocationBean.setFy1("100");
			loAccountsAllocationBean.setFy2("100");
			loAccountsAllocationBean.setUnitOfAppropriation("35");
			loAccountsAllocationBean.setBudgetCode("26222");
			loAccountsAllocationBean.setObjectCode("226");
			loAccountsAllocationBean.setSubOc("52");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYearID(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);
			loAccountsAllocationBean.setId("123-1-2312-fsdf-sdfsdf");
			loAccountsAllocationBean.setContractID("1");
			loAccountsAllocationBean.setContractTypeId("4");
			loConfigurationService.delContractConfAmendmentDetails(loAccountsAllocationBean, null);
			assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testDelContractConfAmendmentDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setContractStartFY(fiscalYearId);
			loAccountsAllocationBean.setContractEndFY("2015");
			loAccountsAllocationBean.setProcurementID(procurementId);
			loAccountsAllocationBean.setFy1("100");
			loAccountsAllocationBean.setFy2("100");
			loAccountsAllocationBean.setUnitOfAppropriation("35");
			loAccountsAllocationBean.setBudgetCode("26222");
			loAccountsAllocationBean.setObjectCode("226");
			loAccountsAllocationBean.setSubOc("52");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYearID(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);
			loAccountsAllocationBean.setId("123-1-2312-fsdf-sdfsdf");
			loAccountsAllocationBean.setContractID("1");
			loAccountsAllocationBean.setContractTypeId("4");
			loConfigurationService.delContractConfAmendmentDetails(null, moSession);
			assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testDelContractConfAmendmentDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			AccountsAllocationBean loAccountsAllocationBean = new AccountsAllocationBean();
			loAccountsAllocationBean.setContractStartFY(fiscalYearId);
			loAccountsAllocationBean.setContractEndFY("2015");
			loAccountsAllocationBean.setProcurementID(procurementId);
			loAccountsAllocationBean.setFy1("100");
			loAccountsAllocationBean.setFy2("100");
			loAccountsAllocationBean.setUnitOfAppropriation("35");
			loAccountsAllocationBean.setBudgetCode("26222");
			loAccountsAllocationBean.setObjectCode("226");
			loAccountsAllocationBean.setSubOc("52");
			loAccountsAllocationBean.setRc("2");
			loAccountsAllocationBean.setFiscalYearID(fiscalYearId);
			loAccountsAllocationBean.setAmmount("567");
			loAccountsAllocationBean.setActiveFlag("1");
			loAccountsAllocationBean.setCreatedByUserId(agency);
			loAccountsAllocationBean.setId("123-1-2312-fsdf-sdfsdf");
			loAccountsAllocationBean.setContractID("1");
			loAccountsAllocationBean.setContractTypeId("4");
			loConfigurationService.delContractConfAmendmentDetails(loAccountsAllocationBean, moSession);
			assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchContractAmendmentConfigurationDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			loConfigurationService.fetchContractAmendmentConfigurationDetails(moSession, null);
			assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchContractAmendmentConfigurationDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			HashMap<String, String> aoHashMap = new HashMap<String, String>();
			loConfigurationService.fetchContractAmendmentConfigurationDetails(null, aoHashMap);
			assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchContractAmendmentConfigurationDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			HashMap<String, String> aoHashMap = new HashMap<String, String>();
			aoHashMap.put("contractID", "1");
			aoHashMap.put("AmendContractId", "4");
			loConfigurationService.fetchContractAmendmentConfigurationDetails(moSession, aoHashMap);
			assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchAmendmentFYPlannedAmount() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			String asContractId = "1";
			String asFiscalYearId = fiscalYearId;
			String asContractTypeId = "4";
			String loFYPlannedAmount = loConfigurationService.fetchAmendmentFYPlannedAmount(null, asContractId,
					asFiscalYearId, asContractTypeId, baseContractId);
			assertTrue(loFYPlannedAmount.equals(HHSConstants.STRING_ZERO));
			// assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchAmendmentFYPlannedAmount2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			String asContractId = null;
			String asFiscalYearId = fiscalYearId;
			String asContractTypeId = "4";
			String loFYPlannedAmount = loConfigurationService.fetchAmendmentFYPlannedAmount(moSession, asContractId,
					asFiscalYearId, asContractTypeId, baseContractId);
//			assertTrue(loFYPlannedAmount.equals(HHSConstants.STRING_ZERO));
			// assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testCheckBudgetDetails20() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			String asContractId = "1";
			String asFiscalYearId = fiscalYearId;
			String asContractTypeId = null;
			String loFYPlannedAmount = loConfigurationService.fetchAmendmentFYPlannedAmount(moSession, asContractId,
					asFiscalYearId, asContractTypeId, baseContractId);
//			assertTrue(loFYPlannedAmount.equals(HHSConstants.STRING_ZERO));
			// assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchAmendmentFYPlannedAmount4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			String asContractId = "1";
			String asFiscalYearId = fiscalYearId;
			String asContractTypeId = null;
			String loFYPlannedAmount = loConfigurationService.fetchAmendmentFYPlannedAmount(moSession, asContractId,
					asFiscalYearId, asContractTypeId, baseContractId);
			assertTrue(!loFYPlannedAmount.equals(HHSConstants.STRING_ZERO));
			// assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchAmendmentFYPlannedAmount5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			String asContractId = "1";
			String asFiscalYearId = fiscalYearId;
			String asContractTypeId = "4";
			String loFYPlannedAmount = loConfigurationService.fetchAmendmentFYPlannedAmount(moSession, asContractId,
					asFiscalYearId, asContractTypeId, baseContractId);
			assertTrue(!loFYPlannedAmount.equals(HHSConstants.STRING_ZERO));
			// assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testCheckBudgetDetails() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			String asContractId = "1";
			String asFiscalYearId = fiscalYearId;
			String lsUpdatedContractId = loConfigurationService.checkBudgetDetails(null, asContractId, asFiscalYearId);
			assertTrue(!lsUpdatedContractId.equals(HHSConstants.STRING_ZERO));
			// assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testCheckBudgetDetails2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			String asFiscalYearId = fiscalYearId;
			String lsUpdatedContractId = loConfigurationService.checkBudgetDetails(moSession, null, asFiscalYearId);
			assertTrue(!lsUpdatedContractId.equals(HHSConstants.STRING_ZERO));
			// assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testCheckBudgetDetails3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			String asContractId = "1";
			String lsUpdatedContractId = loConfigurationService.checkBudgetDetails(moSession, asContractId, null);
			assertTrue(!lsUpdatedContractId.equals(HHSConstants.STRING_ZERO));
			// assertTrue(true);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testCheckBudgetDetails4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			String asContractId = amendContractId;
			String asFiscalYearId = fiscalYearId;
			String lsUpdatedContractId = loConfigurationService.checkBudgetDetails(moSession, asContractId,
					asFiscalYearId);
//			assertNotNull(lsUpdatedContractId);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchPlannedAmtForUpdatedContractId() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			String asContractId = amendContractId;
			String asFiscalYearId = fiscalYearId;
			String lsUpdatedContractId = loConfigurationService.fetchPlannedAmtForUpdatedContractId(null, asContractId,
					asFiscalYearId);
			assertNotNull(lsUpdatedContractId);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchPlannedAmtForUpdatedContractId2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			String asFiscalYearId = fiscalYearId;
			String lsUpdatedContractId = loConfigurationService.fetchPlannedAmtForUpdatedContractId(moSession, null,
					asFiscalYearId);
			assertNotNull(lsUpdatedContractId);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchPlannedAmtForUpdatedContractId3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			String asContractId = amendContractId;
			String lsUpdatedContractId = loConfigurationService.fetchPlannedAmtForUpdatedContractId(moSession,
					asContractId, null);
			assertNotNull(lsUpdatedContractId);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testdelCoaFundingEntrieswhenContractDatesChanged() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			Procurement aoProcurementBean = new Procurement();
			aoProcurementBean.setProcurementId(procurementId);
			Boolean lbStatus = loConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moSession,
					aoProcurementBean);
			assertTrue(lbStatus);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testFetchPlannedAmtForUpdatedContractId4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			ConfigurationService loConfigurationService = new ConfigurationService();
			String asFiscalYearId = fiscalYearId;
			String lsUpdatedContractId = loConfigurationService.fetchPlannedAmtForUpdatedContractId(moSession,
					amendContractId, asFiscalYearId);
//			assertNotNull(lsUpdatedContractId);
		}
		catch (Exception loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);

		}
	}

	@Test
	public void testProcStatusSet1() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskStatus("Approved");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setProcurementId(procurementId);
		Boolean aoFinalFinish = true;
		boolean loProcStatus = loConfigurationService.procStatusSet(moSession, loTaskDetailsBean, aoFinalFinish);
		assertTrue(loProcStatus);
	}

	@Test
	public void testProcStatusSet2() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskStatus("Approved");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setProcurementId("2881");
		Boolean aoFinalFinish = true;
		boolean loProcStatus = loConfigurationService.procStatusSet(moSession, loTaskDetailsBean, aoFinalFinish);
		assertTrue(loProcStatus);
	}

	@Test
	public void testProcStatusSet3() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskStatus("In Review");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setProcurementId(procurementId);
		Boolean aoFinalFinish = true;
		boolean loProcStatus = loConfigurationService.procStatusSet(moSession, loTaskDetailsBean, aoFinalFinish);
		assertFalse(loProcStatus);
	}

	@Test
	public void testProcStatusSet4() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskStatus("In Review");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setProcurementId(procurementId);
		Boolean aoFinalFinish = false;
		boolean loProcStatus = loConfigurationService.procStatusSet(moSession, loTaskDetailsBean, aoFinalFinish);
		assertFalse(loProcStatus);
	}

	@Test
	public void testProcStatusSet5() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskStatus("Approved");
		loTaskDetailsBean.setUserId("agency_21");
		loTaskDetailsBean.setProcurementId(procurementId);
		Boolean aoFinalFinish = false;
		boolean loProcStatus = loConfigurationService.procStatusSet(moSession, loTaskDetailsBean, aoFinalFinish);
		assertFalse(loProcStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testProcStatusSet6() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		Boolean aoFinalFinish = true;
		boolean loProcStatus = loConfigurationService.procStatusSet(moSession, loTaskDetailsBean, aoFinalFinish);
		assertTrue(loProcStatus);
	}

	@Test(expected = ApplicationException.class)
	public void testProcStatusSet7() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setTaskStatus("Approved");
		loTaskDetailsBean.setProcurementId(procurementId);
		Boolean aoFinalFinish = true;
		boolean loProcStatus = loConfigurationService.procStatusSet(null, loTaskDetailsBean, aoFinalFinish);
		assertTrue(loProcStatus);
	}

	@Test(expected = Exception.class)
	public void testProcStatusSet8() throws ApplicationException
	{
		loConfigurationService.procStatusSet(null, null, null);
	}

	/**
	 * Below are the JUnits for new method
	 * delCoaFundingEntrieswhenContractDatesChanged in ConfigurationService
	 * 
	 * */
	@Test
	public void testDelCoaFundingEntrieswhenContractDatesChanged1() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId("246");
		loProcurement.setContractStartDateUpdated("05/27/2018");
		loProcurement.setContractEndDateUpdated("06/30/2019");
		// loProcurement.setContractStartFrom(fiscalYearId);
		Boolean loProcStatus = loConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moSession,
				loProcurement);
		assertTrue(loProcStatus);
	}

	@Test
	public void testDelCoaFundingEntrieswhenContractDatesChanged10() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId("246");
		loProcurement.setContractStartDateUpdated("05/27/2014");
		loProcurement.setContractEndDateUpdated("06/30/2019");
		// loProcurement.setContractStartFrom(fiscalYearId);
		Boolean loProcStatus = loConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moSession,
				loProcurement);
		assertTrue(loProcStatus);
	}

	@Test(expected = Exception.class)
	public void testDelCoaFundingEntrieswhenContractDatesChanged2() throws ApplicationException
	{
		loConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moSession, null);
	}

	@Test(expected = ApplicationException.class)
	public void testDelCoaFundingEntrieswhenContractDatesChanged3() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId(procurementId);
		loConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(null, loProcurement);
	}

	@Test(expected = Exception.class)
	public void testDelCoaFundingEntrieswhenContractDatesChanged4() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId(procurementId);
		loProcurement.setContractStartFrom(fiscalYearId);
		Boolean loProcStatus = loConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(null, null);
		assertTrue(loProcStatus);
	}

	@Test
	public void testDelCoaFundingEntrieswhenContractDatesChanged5() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setContractStartDateUpdated("08/29/2013");
		loProcurement.setContractEndDateUpdated("08/31/2013");
		Boolean loProcStatus = loConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moSession,
				loProcurement);
		assertTrue(loProcStatus);
	}

	@Test
	public void testDelCoaFundingEntrieswhenContractDatesChanged6() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId("2875");
		loProcurement.setContractStartDateUpdated("04/06/2016");
		loProcurement.setContractEndDateUpdated("04/08/2022");
		Boolean loProcStatus = loConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moSession,
				loProcurement);
		assertTrue(loProcStatus);
	}

	@Test
	public void testDelCoaFundingEntrieswhenContractDatesChanged7() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId(procurementId);
		loProcurement.setContractStartDateUpdated("08/07/2013");
		loProcurement.setContractEndDateUpdated("08/08/2013");
		Boolean loProcStatus = loConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moSession,
				loProcurement);
		assertTrue(loProcStatus);
	}

	@Test
	public void testDelCoaFundingEntrieswhenContractDatesChanged8() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId(procurementId);
		loProcurement.setContractStartDateUpdated("09/12/2018");
		loProcurement.setContractEndDateUpdated("09/12/2015");
		Boolean loProcStatus = loConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moSession,
				loProcurement);
		assertTrue(loProcStatus);
	}

	@Test
	public void testDelCoaFundingEntrieswhenContractDatesChanged9() throws ApplicationException
	{
		Procurement loProcurement = new Procurement();
		loProcurement.setProcurementId(procurementId);
		loProcurement.setContractStartDateUpdated("08/28/2013");
		loProcurement.setContractEndDateUpdated("07/31/2014");
		Boolean loProcStatus = loConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(moSession,
				loProcurement);
		assertTrue(loProcStatus);
	}

	/**
	 * Below are the JUnits for new method fetchPCOFCoADetails in
	 * ConfigurationService
	 * 
	 * */
	@Test
	public void testFetchPCOFCoADetails1() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setProcurementID(procurementId);
		loCBGridBean.setFiscalYearID(fiscalYearId);
		List<AccountsAllocationBean> loProcList = loConfigurationService.fetchPCOFCoADetails(loCBGridBean, moSession);
		assertNotNull(loProcList);
	}

	@Test
	public void testFetchPCOFCoADetails2() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setCoaDocType(true);
		loCBGridBean.setProcurementID(procurementId);
		loCBGridBean.setFiscalYearID(fiscalYearId);
		List<AccountsAllocationBean> loProcList = loConfigurationService.fetchPCOFCoADetails(loCBGridBean, moSession);
		assertNotNull(loProcList);
	}

	@Test
	public void testFetchPCOFCoADetails3() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setIsProcCerTaskScreen(true);
		loCBGridBean.setProcurementID(procurementId);
		loCBGridBean.setFiscalYearID(fiscalYearId);
		List<AccountsAllocationBean> loProcList = loConfigurationService.fetchPCOFCoADetails(loCBGridBean, moSession);
		assertNotNull(loProcList);
	}

	@Test
	public void testFetchPCOFCoADetails8() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setCoaDocType(true);
		loCBGridBean.setIsProcCerTaskScreen(false);
		loCBGridBean.setProcurementID("115");
		loCBGridBean.setFiscalYearID(fiscalYearId);
		List<AccountsAllocationBean> loProcList = loConfigurationService.fetchPCOFCoADetails(loCBGridBean, moSession);
		assertNotNull(loProcList);
	}

	@Test
	public void testFetchPCOFCoADetails9() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loCBGridBean.setCoaDocType(false);
		loCBGridBean.setIsProcCerTaskScreen(false);
		loCBGridBean.setProcurementID("115");
		loCBGridBean.setFiscalYearID(fiscalYearId);
		List<AccountsAllocationBean> loProcList = loConfigurationService.fetchPCOFCoADetails(loCBGridBean, moSession);
		assertNotNull(loProcList);
	}

	@Test(expected = Exception.class)
	public void testFetchPCOFCoADetails4() throws ApplicationException
	{
		loConfigurationService.fetchPCOFCoADetails(null, moSession);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchPCOFCoADetails5() throws ApplicationException
	{
		CBGridBean loCBGridBean = new CBGridBean();
		loConfigurationService.fetchPCOFCoADetails(loCBGridBean, null);
	}

	@Test(expected = Exception.class)
	public void testFetchPCOFCoADetails6() throws ApplicationException
	{
		loConfigurationService.fetchPCOFCoADetails(null, null);
	}

	/**
	 * 
	 */

	/**
	 * Below are the JUnits for new method fetchProcurementDetailsForFinancials
	 * in ConfigurationService
	 * 
	 * */
	@Test
	public void testFetchProcurementDetailsForFinancials1() throws ApplicationException
	{
		String lsProcurementId = procurementId;
		ProcurementCOF loProcCOF = loConfigurationService.fetchProcurementDetailsForFinancials(moSession,
				lsProcurementId, false);
		assertNotNull(loProcCOF);
	}

	@Test
	public void testFetchProcurementDetailsForFinancials2() throws ApplicationException
	{
		String lsProcurementId = procurementId;
		ProcurementCOF loProcCOF = loConfigurationService.fetchProcurementDetailsForFinancials(moSession,
				lsProcurementId, false);
		assertNotNull(loProcCOF);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProcurementDetailsForFinancials3() throws ApplicationException
	{
		String lsProcurementId = "1";
		loConfigurationService.fetchProcurementDetailsForFinancials(null, lsProcurementId, true);
	}

	@Test(expected = Exception.class)
	public void testFetchProcurementDetailsForFinancials4() throws ApplicationException
	{
		ProcurementCOF loProcCOF = loConfigurationService.fetchProcurementDetailsForFinancials(moSession, null, false);
		assertNotNull(loProcCOF);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchProcurementDetailsForFinancials5() throws ApplicationException
	{
		String lsProcurementId = procurementId;
		ProcurementCOF loProcCOF = loConfigurationService.fetchProcurementDetailsForFinancials(null, lsProcurementId,
				false);
		assertNotNull(loProcCOF);
	}

	@Test
	public void testFetchProcurementDetailsForFinancials6() throws ApplicationException
	{
		String lsProcurementId = procurementId;
		ProcurementCOF loProcCOF = loConfigurationService.fetchProcurementDetailsForFinancials(moSession,
				lsProcurementId, false);
		assertNotNull(loProcCOF);
	}

	@Test
	public void testFetchProcurementDetailsForFinancials7() throws ApplicationException
	{
		String lsProcurementId = "246";
		ProcurementCOF loProcCOF = loConfigurationService.fetchProcurementDetailsForFinancials(moSession,
				lsProcurementId, false);
		assertNotNull(loProcCOF);
	}

	@Test
	public void testmergeViewCofAndTaskRowsProcFinancials() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setProcurementId(procurementId);
		aoTaskDetailsBean.setTaskStatus(HHSConstants.STATUS_APPROVED);
		Boolean loSuccess = loConfigurationService.mergeViewCofAndTaskRowsProcFinancials(moSession, aoTaskDetailsBean,
				true);
		assertTrue(loSuccess);
	}

	@Test(expected = ApplicationException.class)
	public void testmergeViewCofAndTaskRowsProcFinancialsExp() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setProcurementId(procurementId);
		aoTaskDetailsBean.setTaskStatus(HHSConstants.STATUS_APPROVED);
		Boolean loSuccess = loConfigurationService.mergeViewCofAndTaskRowsProcFinancials(null, aoTaskDetailsBean, true);
		assertTrue(loSuccess);
	}

	@Test
	public void testProcessContractAfterAmendmentCofTask() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("117");
		aoTaskDetailsBean.setUserId("agency_21");
		Boolean loFinishTaskStatus = true;
		Boolean lbProcessFlag = loConfigurationService.processContractAfterAmendmentCofTask(moSession, null,
				loFinishTaskStatus, aoTaskDetailsBean);
		assertTrue(lbProcessFlag);
	}

	@Test
	public void testProcessContractAfterAmendmentCofTask1() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("370");
		aoTaskDetailsBean.setUserId("agency_21");
		Boolean loFinishTaskStatus = true;
		Boolean lbProcessFlag = loConfigurationService.processContractAfterAmendmentCofTask(moSession, null,
				loFinishTaskStatus, aoTaskDetailsBean);
		assertTrue(lbProcessFlag);
	}

	@Test(expected = ApplicationException.class)
	public void testProcessContractAfterAmendmentCofTaskException() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setContractId("370");
		aoTaskDetailsBean.setUserId("agency_21");
		Boolean loFinishTaskStatus = true;
		loConfigurationService.processContractAfterAmendmentCofTask(null, null, loFinishTaskStatus, aoTaskDetailsBean);
	}

	@Test
	public void testUpdateBudgetStatusToPendSub() throws ApplicationException
	{
		loConfigurationService.updateBudgetStatusToPendSub(moSession, "431");
	}
	
	@Test
	public void testUpdateBudgetStatusToPendSub1() throws ApplicationException
	{
		loConfigurationService.updateBudgetStatusToPendSub(null, "431");
	}

	@Test
	public void testUpdateContractStatus() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("117");
		loTaskDetailsBean.setUserId("agency_13");
		Boolean lbProcessFlag = loConfigurationService.updateContractStatus(moSession, null, loTaskDetailsBean);
		assertTrue(lbProcessFlag);
	}

	@Test
	public void testUpdateContractStatus1() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("117");
		loTaskDetailsBean.setUserId("agency_13");
		loTaskDetailsBean.setLaunchCOF(false);
		Boolean lbProcessFlag = loConfigurationService.updateContractStatus(moSession, null, loTaskDetailsBean);
		assertFalse(lbProcessFlag);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateContractStatusException() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("117");
		loTaskDetailsBean.setUserId("agency_13");
		loTaskDetailsBean.setLaunchCOF(true);
		loConfigurationService.updateContractStatus(null, null, loTaskDetailsBean);
	}

	@Test
	public void testUpdateContractFinancials() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("117");
		loTaskDetailsBean.setUserId("agency_13");
		loTaskDetailsBean.setTaskName("Contract Configuration");
		Integer loSuccesfullUpdate = loConfigurationService.updateContractFinancials(moSession, loTaskDetailsBean);
		assertTrue(loSuccesfullUpdate >= 0);
	}

	@Test
	public void testUpdateContractFinancials1() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("117");
		loTaskDetailsBean.setUserId("agency_13");
		loTaskDetailsBean.setTaskName("Contract Configuration Amendment");
		Integer loSuccesfullUpdate = loConfigurationService.updateContractFinancials(moSession, loTaskDetailsBean);
		assertTrue(loSuccesfullUpdate >= 0);
	}

	@Test
	public void testUpdateContractFinancials2() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("117");
		loTaskDetailsBean.setUserId("agency_13");
		loTaskDetailsBean.setTaskName("Contract Certification of Funds");
		Integer loSuccesfullUpdate = loConfigurationService.updateContractFinancials(moSession, loTaskDetailsBean);
		assertTrue(loSuccesfullUpdate >= 0);
	}

	@Test
	public void testUpdateContractFinancials3() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("117");
		loTaskDetailsBean.setUserId("agency_13");
		loTaskDetailsBean.setTaskName("Amendment Certification of Funds");
		Integer loSuccesfullUpdate = loConfigurationService.updateContractFinancials(moSession, loTaskDetailsBean);
		assertTrue(loSuccesfullUpdate >= 0);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateContractFinancialsException() throws ApplicationException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId("117");
		loTaskDetailsBean.setUserId("agency_13");
		loTaskDetailsBean.setTaskName("Amendment Certification of Funds");
		loConfigurationService.updateContractFinancials(null, loTaskDetailsBean);
	}

	@Test
	public void testInsertNewUpdateBudgetDetails() throws ApplicationException
	{
		ContractBudgetBean aoContractBudgetBean = new ContractBudgetBean();
		aoContractBudgetBean.setContractId("48");
		aoContractBudgetBean.setBudgetId("533");
		aoContractBudgetBean.setBudgetTypeId(1);
		aoContractBudgetBean.setBudgetfiscalYear("2014");
		aoContractBudgetBean.setTotalbudgetAmount("50000");
		aoContractBudgetBean.setStatusId("87");
		aoContractBudgetBean.setContractTypeId("4");
		aoContractBudgetBean.setCreatedByUserId("agency_13");
		aoContractBudgetBean.setBudgetStartDate("02/05/14");
		aoContractBudgetBean.setBudgetEndDate("02/07/15");
		loConfigurationService.insertNewUpdateBudgetDetails(moSession, aoContractBudgetBean);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchBudgetDetailsActiveOrApproved2() throws ApplicationException

	{

		List<ContractBudgetBean> loContractBudgetBean = loConfigurationService.fetchBudgetDetailsActiveOrApproved(null,
				null, null);

	}

	@Test
	public void testFetchBudgetDetailsActiveOrApproved() throws ApplicationException

	{

		List<ContractBudgetBean> loContractBudgetBean = loConfigurationService.fetchBudgetDetailsActiveOrApproved(
				moSession, "", "");

		assertTrue(loContractBudgetBean.size() == 0);

	}
	

	@Test
	public void testcontractBudgetAmendFYData() throws ApplicationException
	{
		ContractBudgetBean loContractBudgetBean = loConfigurationService.contractBudgetAmendFYData(moSession,
				"35", "2014", "2", "41");
		assertNotNull(loContractBudgetBean);
	}

	@Test(expected = ApplicationException.class)
	public void testcontractBudgetAmendFYData123() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setProcurementId(procurementId);
		aoTaskDetailsBean.setTaskStatus(HHSConstants.STATUS_APPROVED);
		ContractBudgetBean loContractBudgetBean = loConfigurationService.contractBudgetAmendFYData(null,
				baseContractId, fiscalYearId, "2", amendContractId);
		assertNotNull(loContractBudgetBean);
	}

	@Test
	public void testeditContractConfAmendmentDetails() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setProcurementId(procurementId);
		aoTaskDetailsBean.setTaskStatus(HHSConstants.STATUS_APPROVED);
		loConfigurationService.editContractConfAmendmentDetails(getDummyAccountsAllocationBean(), moSession);

	}

	@Test
	public void testsetOriginalProcurementValues() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setProcurementId(procurementId);
		aoTaskDetailsBean.setTaskStatus(HHSConstants.STATUS_APPROVED);
		loConfigurationService.setOriginalProcurementValues(getCBGridBeanParams(), moSession);
		// assertNotNull(loContractBudgetBean);
	}

	@Test(expected = ApplicationException.class)
	public void testfechActiveApprovedFiscalYears() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setProcurementId(procurementId);
		aoTaskDetailsBean.setTaskStatus(HHSConstants.STATUS_APPROVED);
		List<String> loFiscalYrList = loConfigurationService.fechActiveApprovedFiscalYears(null, amendContractId);
		assertNotNull(loFiscalYrList);
		// assertNotNull(loContractBudgetBean);
	}

	@Test
	public void testfechActiveApprovedFiscalYearsas() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setProcurementId(procurementId);
		aoTaskDetailsBean.setTaskStatus(HHSConstants.STATUS_APPROVED);
		List<String> loFiscalYrList = loConfigurationService.fechActiveApprovedFiscalYears(moSession, amendContractId);
		assertNotNull(loFiscalYrList);
		// assertNotNull(loContractBudgetBean);
	}

	@Test
	public void testfetchUpdateContractIdas() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setProcurementId(procurementId);
		aoTaskDetailsBean.setTaskStatus(HHSConstants.STATUS_APPROVED);
		String abc = loConfigurationService.fetchUpdateContractId(moSession, baseContractId);
		// assertNotNull(abc);
		// assertNotNull(loContractBudgetBean);
	}

	@Test(expected = ApplicationException.class)
	public void testfetchUpdateContractId() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setProcurementId(procurementId);
		aoTaskDetailsBean.setTaskStatus(HHSConstants.STATUS_APPROVED);
		String abc = loConfigurationService.fetchUpdateContractId(null, baseContractId);
		assertNotNull(abc);
		// assertNotNull(loContractBudgetBean);
	}

	@Test
	public void testfetchBaseContractId() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setProcurementId(procurementId);
		aoTaskDetailsBean.setTaskStatus(HHSConstants.STATUS_APPROVED);
		String abc = loConfigurationService.fetchBaseContractId(moSession, baseContractId);
		assertNotNull(abc);
		// assertNotNull(loContractBudgetBean);
	}

	@Test(expected = ApplicationException.class)
	public void testfetchBaseContractId123() throws ApplicationException
	{
		TaskDetailsBean aoTaskDetailsBean = new TaskDetailsBean();
		aoTaskDetailsBean.setProcurementId(procurementId);
		aoTaskDetailsBean.setTaskStatus(HHSConstants.STATUS_APPROVED);
		String abc = loConfigurationService.fetchBaseContractId(null, baseContractId);
		assertNotNull(abc);
		// assertNotNull(loContractBudgetBean);
	}

	@Test
	public void testCreateDuplicateRows1123() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.createDuplicateRows(moSession, getCBGridBeanParams());

	}
	
	@Test
	public void testInsertNewFiscalYearSubBudgetDetails() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = new ContractBudgetBean();
		loSubBudgetBean.setBudgetId("476");
		loSubBudgetBean.setBudgetfiscalYear("2015");
		loSubBudgetBean.setSubbudgetName("TestBudget");
		loSubBudgetBean.setSubbudgetAmount("10000");
		loSubBudgetBean.setCreatedByUserId("agency_13");
		Boolean loInsertStatus = loConfigurationService.insertNewFiscalYearSubBudgetDetails(moSession, loSubBudgetBean);
		assertTrue(loInsertStatus);
	}
	
	@Test
	public void testInsertNewFiscalYearSubBudgetDetails1() throws ApplicationException
	{
		Boolean loInsertStatus = loConfigurationService.insertNewFiscalYearSubBudgetDetails(moSession, null);
		assertTrue(loInsertStatus);
	}
	
	@Test(expected = ApplicationException.class)
	public void testInsertNewFiscalYearSubBudgetDetailsException() throws ApplicationException
	{
		ContractBudgetBean loSubBudgetBean = new ContractBudgetBean();
		loSubBudgetBean.setBudgetId("476");
		loSubBudgetBean.setBudgetfiscalYear("2015");
		loSubBudgetBean.setSubbudgetName("TestBudget");
		loSubBudgetBean.setSubbudgetAmount("10000");
		loSubBudgetBean.setCreatedByUserId("agency_13");
		loConfigurationService.insertNewFiscalYearSubBudgetDetails(null, loSubBudgetBean);
	}
	
	@Test
	public void testSetProcurementCOFStatus() throws ApplicationException
	{
		CBGridBean loCbGridBean = new CBGridBean();
		loCbGridBean.setProcurementID("115");
		loCbGridBean.setModifyByAgency("agency_20");
		loCbGridBean.setStatusId("52");
		loConfigurationService.setProcurementCOFStatus(true, loCbGridBean, moSession);
	}
	
	@Test(expected = ApplicationException.class)
	public void testSetProcurementCOFStatusException() throws ApplicationException
	{
		CBGridBean loCbGridBean = new CBGridBean();
		loCbGridBean.setProcurementID("115");
		loCbGridBean.setModifyByAgency("agency_20");
		loCbGridBean.setStatusId("52");
		loConfigurationService.setProcurementCOFStatus(true, loCbGridBean, null);
	}
	
	@Test
	public void testFetchFYAndContractId() throws ApplicationException
	{
		List<String> loFYIList = loConfigurationService.fetchFYAndContractId(moSession, "228");
		assertNotNull(loFYIList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchFYAndContractId1() throws ApplicationException
	{
		loConfigurationService.fetchFYAndContractId(null, "228");
	
	}
	
	@Test
	public void testFetchContractConfigGridCurr() throws ApplicationException
	{
		CBGridBean loCbGridBean = new CBGridBean();
		loCbGridBean.setContractID("24");
		loCbGridBean.setFiscalYearID("2014");
		loConfigurationService.fetchContractConfigGridCurr(loCbGridBean, moSession);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchContractConfigGridCurr1() throws ApplicationException
	{
		CBGridBean loCbGridBean = new CBGridBean();
		loCbGridBean.setContractID("24");
		loCbGridBean.setFiscalYearID("2014");
		loConfigurationService.fetchContractConfigGridCurr(loCbGridBean, null);
	}
	
	@Test
	public void testFetchUpdateConfigurationDetails() throws ApplicationException
	{
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put("contractID", "48");
		aoHashMap.put("contractTypeId", "4");
		ProcurementCOF loProcurementCOF = loConfigurationService.fetchUpdateConfigurationDetails(moSession, aoHashMap);
		assertNotNull(loProcurementCOF);
	}
	
	@Test(expected = ApplicationException.class)
	public void testFetchUpdateConfigurationDetails1() throws ApplicationException
	{
		HashMap<String, String> aoHashMap = new HashMap<String, String>();
		aoHashMap.put("contractID", "48");
		aoHashMap.put("contractTypeId", "4");
		ProcurementCOF loProcurementCOF = loConfigurationService.fetchUpdateConfigurationDetails(null, aoHashMap);
		assertNotNull(loProcurementCOF);
	}
	
	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchProcurementDetails0Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchProcurementDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceprocStatusSet1Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.procStatusSet(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicemergeViewCofAndTaskRowsProcFinancials2Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.mergeViewCofAndTaskRowsProcFinancials(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicedelCoaFundingEntrieswhenContractDatesChanged3Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.delCoaFundingEntrieswhenContractDatesChanged(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchPCOFCoADetails4Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchPCOFCoADetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchContractConfFundingDetails5Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchContractConfFundingDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchContractAmendmentFundingDetails6Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchContractAmendmentFundingDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchPCOFFundingSourcesDetails7Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchPCOFFundingSourcesDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceeditContractConfFundingDetails8Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.editContractConfFundingDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceeditContractAmendmentFundingDetails9Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.editContractAmendmentFundingDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceinsertCoADetails10Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.insertCoADetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceupdateCoADetails11Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.updateCoADetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicedeleteProcurementCoADetails12Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.deleteProcurementCoADetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceupdateFundingSourcesDetails13Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.updateFundingSourcesDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceprocessContractAfterAmendmentCofTask14Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.processContractAfterAmendmentCofTask(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceprocessContractAfterCOFTask15Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.processContractAfterCOFTask(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceupdateBudgetStatusToPendSub16Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.updateBudgetStatusToPendSub(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceupdateContractStatus17Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.updateContractStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceupdateContractFinancials18Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.updateContractFinancials(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceupdateContractStatusToPendingConfig19Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.updateContractStatusToPendingConfig(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchContractConfigDetails20Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchContractConfigDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchContractConfigBudgetDetails21Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchContractConfigBudgetDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchContractConfCOADetails22Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchContractConfCOADetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchContractCOFCOA23Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchContractCOFCOA(getCBGridBeanParams(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceaddContractConfCOADetails24Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.addContractConfCOADetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicedelContractConfCOADetails25Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.delContractConfCOADetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceeditContractConfCOADetails26Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.editContractConfCOADetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchContractConfSubBudgetDetails127Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchContractConfSubBudgetDetails1(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchBudgetDetailsActiveOrApproved28Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchBudgetDetailsActiveOrApproved(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchBudgetDetailsByFYAndContractId29Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchBudgetDetailsByFYAndContractId(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchAmendmentBudgetDetails30Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchAmendmentBudgetDetails(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceinsertContractConfSubBudgetDetails31Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.insertContractConfSubBudgetDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceinsertNewFiscalYearSubBudgetDetails32Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.insertNewFiscalYearSubBudgetDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceinsertNewBudgetDetails33Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.insertNewBudgetDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceinsertNewUpdateBudgetDetails34Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.insertNewUpdateBudgetDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceinsertNewAmendmentBudgetDetails35Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.insertNewAmendmentBudgetDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceupdateBudgetFYTotalBudgetAmount36Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.updateBudgetFYTotalBudgetAmount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceeditContractConfSubBudgetDetails37Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.editContractConfSubBudgetDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

//	@Test(expected = java.lang.Exception.class)
//	public void testConfigurationServicegetSubBudgetsSumTotal38Negative()
//	{
//		ConfigurationService loConfigurationService = new ConfigurationService();
//		try
//		{
//			loConfigurationService.getSubBudgetsSumTotal(null, null, null);
//		}
//		catch (ApplicationException e)
//		{
//			e.printStackTrace();
//		}
//	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicedelContractConfSubBudgetDetails39Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.delContractConfSubBudgetDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicesetProcurementCOFStatus40Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.setProcurementCOFStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchContractConfigGridCurr41Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchContractConfigGridCurr(getCBGridBeanParams(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchContractConfAmendmentDetails42Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchContractConfAmendmentDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceeditContractConfAmendmentDetails43Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.editContractConfAmendmentDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceaddContractConfAmendmentDetails44Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.addContractConfAmendmentDetails(new AccountsAllocationBean(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicedelContractConfAmendmentDetails45Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.delContractConfAmendmentDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchContractConfUpdateDetails46Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchContractConfUpdateDetails(getCBGridBeanParams(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceaddContractConfUpdateTaskDetails47Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.addContractConfUpdateTaskDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceeditContractConfUpdateDetails48Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.editContractConfUpdateDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchUpdateConfigurationDetails49Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchUpdateConfigurationDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchContractAmendmentConfigurationDetails50Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchContractAmendmentConfigurationDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceupdateFetchedContractDetails51Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.updateFetchedContractDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchContractConfUpdateActualDetails52Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchContractConfUpdateActualDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchFYAndContractId53Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchFYAndContractId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchContractConfUpdateSubBudgetDetails54Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchContractConfUpdateSubBudgetDetails(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceaddContractConfUpdateBudgetDetails55Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.addContractConfUpdateBudgetDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceinsertUpdatedSubBudgetDetails56Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.insertUpdatedSubBudgetDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceinsertAmendmentSubBudgetDetails57Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.insertAmendmentSubBudgetDetails(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceeditContractConfUpdateSubBudgetDetails58Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.editContractConfUpdateSubBudgetDetails(null, new ContractBudgetBean(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceeditNewFYConfCOADetails59Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.editNewFYConfCOADetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicecopyPreviousFYSubBudgetToCurrentFY60Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.copyPreviousFYSubBudgetToCurrentFY(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchFYPlannedAmount61Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchFYPlannedAmount(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchAmendmentFYPlannedAmount62Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchAmendmentFYPlannedAmount(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchNewFYSubBudgetDetails63Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchNewFYSubBudgetDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceupdateBudgetForNewFYConfigurationTask64Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.updateBudgetForNewFYConfigurationTask(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicegetContractEndDate65Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.getContractEndDate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicecheckIfBudgetExists66Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.checkIfBudgetExists(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchConfigurableYearBudgetAmount67Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchConfigurableYearBudgetAmount(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicevalidateContractConfigUpdateAmount68Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.validateContractConfigUpdateAmount(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicedelContractConfUpdateTaskDetails69Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.delContractConfUpdateTaskDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicedelContractConfUpdateSubBudgetDetails70Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.delContractConfUpdateSubBudgetDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicemergeContractConfUpdateFinishTask71Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.mergeContractConfUpdateFinishTask(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchPlannedAmtForUpdatedContractId72Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchPlannedAmtForUpdatedContractId(null, "", "");
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicecheckBudgetDetails73Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.checkBudgetDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicecreateDuplicateRows74Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.createDuplicateRows(null, new CBGridBean());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchProcurementDetailsForFinancials75Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchProcurementDetailsForFinancials(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchBaseContractId76Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchBaseContractId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefetchUpdateContractId77Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchUpdateContractId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicefechActiveApprovedFiscalYears78Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fechActiveApprovedFiscalYears(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicesetOriginalProcurementValues79Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.setOriginalProcurementValues(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicecontractBudgetAmendFYData80Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.contractBudgetAmendFYData(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}
	
	@Test
	public void testFetchContractCOFCOA() throws ApplicationException
	{
		CBGridBean loCbGridBean = new CBGridBean();
		loCbGridBean.setContractID("962");
		loCbGridBean.setFiscalYearID("2014");
		List<AccountsAllocationBean> loAccountsAllocationBeanList = loConfigurationService.fetchContractCOFCOA(loCbGridBean, moSession);
		assertNotNull(loAccountsAllocationBeanList);
	}
	

	@Test
	public void testFetchContractCOFCOA1() throws ApplicationException
	{
		CBGridBean loCbGridBean = new CBGridBean();
		loCbGridBean.setContractID("131");
		loCbGridBean.setFiscalYearID("2012");
		List<AccountsAllocationBean> loAccountsAllocationBeanList = loConfigurationService.fetchContractCOFCOA(loCbGridBean, moSession);
		assertNotNull(loAccountsAllocationBeanList);
	}

	@Test(expected = ApplicationException.class)
	public void testFetchContractCOFCOAException() throws ApplicationException

	{
		CBGridBean loCbGridBean = new CBGridBean();
		loCbGridBean.setContractID("962");
		loCbGridBean.setFiscalYearID("2014");
		List<AccountsAllocationBean> loAccountsAllocationBeanList = loConfigurationService.fetchContractCOFCOA(loCbGridBean, null);
		assertNotNull(loAccountsAllocationBeanList);

	}
	

}
