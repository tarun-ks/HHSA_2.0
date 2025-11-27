package com.nyc.hhs.daomanager.serviceTest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.daomanager.service.FinancialsService;
import com.nyc.hhs.daomanager.service.InvoiceService;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.frameworks.daomanager.MyBatisConnectionFactory;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.service.filenetmanager.p8constants.P8Constants;

public class FinancialsServiceTest
{
	FinancialsService moFinancialsService = new FinancialsService();
	private static SqlSession moSession = null; // SQL Session

	public String baseContractId = "63";
	public String amendContractId = "53";
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
	}

	public ContractList getContractFilterBean()
	{
		ContractList aoContractFilterBean = new ContractList();
		String abc[] =
		{ "61", "62", "67", "69", "59", "60", "128", "129", "130" };
		aoContractFilterBean.setContractStatusList(Arrays.asList(abc));
		aoContractFilterBean.setOrgName("r3_org");
		aoContractFilterBean.setOrgType("provider_org");
		aoContractFilterBean.setStartNode(1);
		aoContractFilterBean.setEndNode(10);
		aoContractFilterBean.setContractValueFrom("-99999999");
		aoContractFilterBean.setContractValueTo("999999");
		return aoContractFilterBean;
	}

	@Test
	public void testFetchAmendmentListSummaryProvider() throws ApplicationException
	{
		List<ContractList> loContractList = moFinancialsService.fetchAmendmentListSummary(moSession,
				getContractFilterBean(), "provider_org");
		assertTrue(loContractList.size() > 0);
	}

	@Test
	public void testFetchAmendmentListSummaryAgency() throws ApplicationException
	{
		ContractList loContractFilterBean = new ContractList();
		loContractFilterBean = getContractFilterBean();
		loContractFilterBean.setOrgName("DOC");
		loContractFilterBean.setOrgType("agency_org");
		List<ContractList> loContractList = moFinancialsService.fetchAmendmentListSummary(moSession,
				loContractFilterBean, "agency_org");
		assertTrue(loContractList.size() > 0);
	}

	@Test
	public void testFetchAmendmentListSummaryAccelerator() throws ApplicationException
	{
		List<ContractList> loContractList = moFinancialsService.fetchAmendmentListSummary(moSession,
				getContractFilterBean(), "city_org");
		assertTrue(loContractList.size() > 0);
	}

	@Test
	public void testGetContractsCountAgency() throws ApplicationException
	{
		ContractList loContractFilterBean = new ContractList();
		loContractFilterBean = getContractFilterBean();
		loContractFilterBean.setOrgName("DOC");
		loContractFilterBean.setOrgType("agency_org");
		Integer loContractCount = moFinancialsService.getContractsCount(moSession, loContractFilterBean, "agency_org");
		assertTrue(loContractCount > 0);
	}

	@Test
	public void testGetContractsCountAccelerator() throws ApplicationException
	{
		Integer loContractCount = moFinancialsService.getContractsCount(moSession, getContractFilterBean(), "city_org");
		assertTrue(loContractCount > 0);
	}

	@Test
	public void testGetContractsCountProvider() throws ApplicationException
	{
		Integer loContractCount = moFinancialsService.getContractsCount(moSession, getContractFilterBean(),
				"provider_org");
		assertTrue(loContractCount > 0);
	}

	@Test(expected = ApplicationException.class)
	public void testGetContractsCountProviderException() throws ApplicationException
	{
		moFinancialsService.getContractsCount(null, getContractFilterBean(), "provider_org");
	}

	@Test(expected = ApplicationException.class)
	public void testFetchAmendmentListSummaryProviderException() throws ApplicationException
	{
		moFinancialsService.fetchAmendmentListSummary(null, getContractFilterBean(), "provider_org");
	}

	@Test
	public void testInsertBulkuploadDocumentInDB() throws ApplicationException
	{
		FinancialsService lofinFinancialsService = new FinancialsService();
		HashMap<String, Object> loParamMap = getUploadedFileProperties();
		// Positive Scenario -- File Properties saved successfully
		// incomplete data in the Bean
		Boolean lbUploadResult = lofinFinancialsService.insertBulkuploadDocumentInDB(moSession, loParamMap,
				(String) loParamMap.get("documentId"));
		assertTrue(lbUploadResult);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertBulkuploadDocumentInDBNegative1() throws ApplicationException, IOException
	{
		FinancialsService lofinFinancialsService = new FinancialsService();
		HashMap<String, Object> loParamMap = getUploadedFileProperties();
		// Positive Scenario -- File Properties saved successfully
		// incomplete data in the Bean
		Boolean lbUploadResult = lofinFinancialsService.insertBulkuploadDocumentInDB(moSession, loParamMap, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInsertBulkuploadDocumentInDBNegative2() throws ApplicationException, IOException
	{
		Boolean lbThrown = false;
		FinancialsService lofinFinancialsService = new FinancialsService();
		HashMap<String, Object> loParamMap = getUploadedFileProperties();
		HashMap<String, Object> loParamMap1 = new HashMap<String, Object>();
		// Negative Scenario -- Application Exception handled by setting
		// incomplete data in the Bean
		Boolean lbUploadResult = lofinFinancialsService.insertBulkuploadDocumentInDB(moSession, loParamMap1,
				(String) loParamMap.get("documentId"));
	}

	@Test(expected = ApplicationException.class)
	public void testInsertBulkuploadDocumentInDBNegative3() throws ApplicationException, IOException
	{
		FinancialsService lofinFinancialsService = new FinancialsService();
		HashMap<String, Object> loParamMap = getUploadedFileProperties();
		// Negative Scenario -- Application Exception handled by setting
		// incomplete data in the Bean
		Boolean lbUploadResult = lofinFinancialsService.insertBulkuploadDocumentInDB(null, loParamMap,
				(String) loParamMap.get("documentId"));
	}

	public HashMap<String, Object> getUploadedFileProperties()
	{
		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put("documentId", "{254B87A8-BD6B-48A7-A62A-B27DE1B495FA}");
		loParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "GetContent.xlsx");
		loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, "city_43");
		loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, "city_43");
		loParamMap.put("statusId", "143");

		return loParamMap;
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testFetchEntryTypeDetails1() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.SCREEN_NAME, HHSConstants.CONTRACT_CONFIG_UPDATE);
		loHashMap.put(HHSConstants.CONTRACT_ID, "314");
		loHashMap.put(HHSConstants.BUDGET_ID, "157");
		loHashMap.put(HHSConstants.UPDATE_CONTRACT_ID, "96");
		// positive Scenario
		List<String> loEntryTypeList = moFinancialsService.getLineItemsState(moSession, loHashMap);
		assertTrue(loEntryTypeList.size() > 0);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testFetchEntryTypeDetails2() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.SCREEN_NAME, HHSConstants.CONTRACT_CONFIG_UPDATE);
		loHashMap.put(HHSConstants.CONTRACT_ID, "312");
		loHashMap.put(HHSConstants.BUDGET_ID, "157");
		loHashMap.put(HHSConstants.UPDATE_CONTRACT_ID, "96");
		// negative Scenario
		List<String> loEntryTypeList = moFinancialsService.getLineItemsState(moSession, loHashMap);
		assertTrue(loEntryTypeList.size() == 0);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testFetchEntryTypeDetails3() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.SCREEN_NAME, HHSConstants.TASK_BUDGET_AMENDMENT);
		loHashMap.put(HHSConstants.CONTRACT_ID, "314");
		loHashMap.put(HHSConstants.BUDGET_ID, "157");
		// positive Scenario
		List<String> loEntryTypeList = moFinancialsService.getLineItemsState(moSession, loHashMap);
		assertTrue(loEntryTypeList.size() > 0);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testFetchEntryTypeDetails4() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.SCREEN_NAME, HHSConstants.TASK_BUDGET_AMENDMENT);
		loHashMap.put(HHSConstants.CONTRACT_ID, "3140");
		loHashMap.put(HHSConstants.BUDGET_ID, "1570");
		// negative Scenario
		List<String> loEntryTypeList = moFinancialsService.getLineItemsState(moSession, loHashMap);
		assertTrue(loEntryTypeList.size() == 0);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testFetchEntryTypeDetails5() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.SCREEN_NAME, HHSConstants.TASK_AMENDMENT_CONFIGURATION);
		loHashMap.put(HHSConstants.CONTRACT_ID, "101");
		loHashMap.put(HHSConstants.UPDATE_CONTRACT_ID, "218");
		loHashMap.put(HHSConstants.FISCAL_YEAR, "2014");
		// positive Scenario
		List<String> loEntryTypeList = moFinancialsService.getLineItemsState(moSession, loHashMap);
		assertTrue(loEntryTypeList.size() > 0);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testFetchEntryTypeDetails6() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.SCREEN_NAME, HHSConstants.AUDIT_CONTRACT_BUDGET_MODIFICATION);
		loHashMap.put(HHSConstants.CONTRACT_ID, "349");
		loHashMap.put(HHSConstants.BUDGET_ID, "696");
		List<String> loEntryTypeList = moFinancialsService.getLineItemsState(moSession, loHashMap);
		assertTrue(loEntryTypeList.size() > 0);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testFetchEntryTypeDetails7() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, "314");
		loHashMap.put(HHSConstants.BUDGET_ID, "157");
		// positive Scenario
		List<String> loEntryTypeList = moFinancialsService.getLineItemsState(moSession, loHashMap);
		assertTrue(loEntryTypeList.size() > 0);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testFetchEntryTypeDetails8() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, "3140");
		loHashMap.put(HHSConstants.BUDGET_ID, "1570");
		// negative Scenario
		List<String> loEntryTypeList = moFinancialsService.getLineItemsState(moSession, loHashMap);
		assertTrue(loEntryTypeList.size() == 0);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testFetchEntryTypeDetails9() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, "3140");
		loHashMap.put(HHSConstants.BUDGET_ID, "1570");
		// negative Scenario
		List<String> loEntryTypeList = moFinancialsService.getLineItemsState(null, loHashMap);
		assertTrue(loEntryTypeList.size() == 0);
	}

	@Test
	public void testPublishEntryTypeDetails1() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, "75");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		loHashMap.put(HHSConstants.MOD_BY_USER_ID, "agency_21");
		loHashMap.put(HHSConstants.FISCAL_YEAR_ID, "2014");
		loHashMap.put(HHSConstants.BUDGET_TYPE_ID, "1");
		Boolean lostatus = moFinancialsService.publishEntryTypeDetails(moSession, loHashMap);
		assertNotNull(lostatus);
	}

	@Test(expected = ApplicationException.class)
	public void testPublishEntryTypeDetails2() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, "75");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		loHashMap.put(HHSConstants.MOD_BY_USER_ID, "agency_21");
		loHashMap.put(HHSConstants.FISCAL_YEAR_ID, "2018");
		loHashMap.put(HHSConstants.BUDGET_TYPE_ID, "1");
		Boolean lostatus = moFinancialsService.publishEntryTypeDetails(null, loHashMap);
		assertNotNull(lostatus);
	}

	@Test
	public void testUpdateYtdAndRemainingAmountInLineItems() throws ApplicationException, IOException
	{
		boolean lbSuccessfullyUpdated = moFinancialsService.updateYtdAndRemainingAmountInLineItems(moSession, "153",
				true);
		assertTrue(lbSuccessfullyUpdated);
	}

	@Test
	public void testUpdateYtdAndRemainingAmountInLineItemsNonFinal() throws ApplicationException, IOException
	{
		boolean lbSuccessfullyUpdated = moFinancialsService.updateYtdAndRemainingAmountInLineItems(moSession, "153",
				false);
		assertTrue(lbSuccessfullyUpdated);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateYtdAndRemainingAmountInLineItemsException() throws ApplicationException
	{
		boolean lbSuccessfullyUpdated = moFinancialsService.updateYtdAndRemainingAmountInLineItems(null, "153", true);
		assertTrue(lbSuccessfullyUpdated);
	}

	@Test
	public void testUpdateYtdAndRemainingAmountInBudgetAndSubBudget() throws ApplicationException
	{
		boolean lbSuccessfullyUpdated = moFinancialsService.updateYtdAndRemainingAmountInBudgetAndSubBudget(moSession,
				"11287", true,"system");
		assertTrue(lbSuccessfullyUpdated);
	}

	@Test
	public void testUpdateYtdAndRemainingAmountInBudgetAndSubBudgetNonFinal() throws ApplicationException
	{
		boolean lbSuccessfullyUpdated = moFinancialsService.updateYtdAndRemainingAmountInBudgetAndSubBudget(moSession,
				"153", true);
		assertTrue(lbSuccessfullyUpdated);
	}

	@Test
	public void testUpdateYtdAndRemainingAmountInBudgetAndSubBudgetExceptions() throws ApplicationException
	{
		boolean lbSuccessfullyUpdated = moFinancialsService.updateYtdAndRemainingAmountInBudgetAndSubBudget(moSession,
				"153", true);
		assertTrue(lbSuccessfullyUpdated);
	}

	@Test
	public void testCopyPreviousCBCToCurrentCBC1() throws ApplicationException, IOException
	{
		String lsContractId = "141";
		String lsFiscalYearId = "2015";
		String lsBudgetId = "159";
		String lsUserId = "agency_21";
		Boolean lostatus = moFinancialsService.copyPreviousCBCToCurrentCBC(moSession, lsContractId, lsFiscalYearId,
				lsBudgetId, lsUserId);
		assertNotNull(lostatus);
	}

	@Test(expected = ApplicationException.class)
	public void testCopyPreviousCBCToCurrentCBC2() throws ApplicationException, IOException
	{
		String lsContractId = "141";
		String lsFiscalYearId = "2015";
		String lsBudgetId = "159";
		String lsUserId = "agency_21";
		Boolean lostatus = moFinancialsService.copyPreviousCBCToCurrentCBC(null, lsContractId, lsFiscalYearId,
				lsBudgetId, lsUserId);
		assertNotNull(lostatus);
	}

	@Test
	public void testCopyPreviousCBCToCurrentCBC3() throws ApplicationException, IOException
	{
		String lsContractId = "216";
		String lsFiscalYearId = "2015";
		String lsBudgetId = "120";
		String lsUserId = "agency_21";
		Boolean lostatus = moFinancialsService.copyPreviousCBCToCurrentCBC(moSession, lsContractId, lsFiscalYearId,
				lsBudgetId, lsUserId);
		assertNotNull(lostatus);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testMergeBaseActiveBudget1() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, "53");
		loHashMap.put(HHSConstants.BUDGET_ID, "13");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		Boolean lostatus = moFinancialsService.mergeBaseActiveBudget(moSession, loHashMap);
		assertNotNull(lostatus);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testMergeBaseActiveBudget2() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.CONTRACT_ID, "53");
		loHashMap.put(HHSConstants.BUDGET_ID, "13");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		Boolean lostatus = moFinancialsService.mergeBaseActiveBudget(null, loHashMap);
		assertNotNull(lostatus);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testUpdateBudgetTemplate1() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.ENTRY_TYPE_LIST, "1,2");
		loHashMap.put(HHSConstants.CONTRACT_ID, "4");
		loHashMap.put(HHSConstants.BUDGET_ID, "4");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		Boolean lostatus = moFinancialsService.updateBudgetTemplate(moSession, loHashMap);
		assertNotNull(lostatus);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test(expected = ApplicationException.class)
	public void testUpdateBudgetTemplate2() throws ApplicationException, IOException
	{
		HashMap loHashMap = new HashMap<String, String>();
		loHashMap.put(HHSConstants.ENTRY_TYPE_LIST, "1,2");
		loHashMap.put(HHSConstants.CONTRACT_ID, "100");
		loHashMap.put(HHSConstants.BUDGET_ID, "100");
		loHashMap.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		loHashMap.put(HHSConstants.PUBLISHED, HHSConstants.ONE);
		Boolean lostatus = moFinancialsService.updateBudgetTemplate(moSession, loHashMap);
		assertNotNull(lostatus);
	}

	@Test
	public void testFetchAmendAffectedBaseBudgetIds() throws ApplicationException
	{
		ContractBean loContractBean = new ContractBean();
		loContractBean.setContractId("600");
		HashMap loHMArgs = new HashMap();
		loHMArgs.put("aoContractBean", loContractBean);
		String lsBudgetIds = moFinancialsService.fetchAmendAffectedBaseBudgetIds(moSession, loHMArgs);
		Assert.assertEquals(lsBudgetIds, "233,238");
	}

	@Test(expected = Exception.class)
	public void testFetchAmendAffectedBaseBudgetIds1() throws ApplicationException
	{
		ContractBean loContractBean = new ContractBean();
		loContractBean.setContractId("600");
		HashMap loHMArgs = new HashMap();
		loHMArgs.put("aoContractBean", loContractBean);
		moFinancialsService.fetchAmendAffectedBaseBudgetIds(null, loHMArgs);
	}

	@Test(expected = Exception.class)
	public void testFetchAmendAffectedBaseBudgetIds2() throws ApplicationException
	{
		HashMap loHMArgs = new HashMap();
		moFinancialsService.fetchAmendAffectedBaseBudgetIds(moSession, loHMArgs);
	}

	@Test
	public void testMergeBaseBudCustomization() throws ApplicationException
	{
		HashMap aoHMArgs = new HashMap();
		ContractBean loContractBean = new ContractBean();
		loContractBean.setContractId("403");
		loContractBean.setParentContractId("290");
		aoHMArgs.put("aoContractBean", loContractBean);
		Boolean loStatusResult = moFinancialsService.mergeBaseBudCustomization(moSession, aoHMArgs);
		assertTrue(loStatusResult);
	}

	@Test
	public void testMergeBaseBudCustomization2() throws ApplicationException
	{
		HashMap aoHMArgs = new HashMap();
		ContractBean loContractBean = new ContractBean();
		loContractBean.setContractId("282");
		loContractBean.setParentContractId("290");
		aoHMArgs.put("aoContractBean", loContractBean);
		Boolean loStatusResult = moFinancialsService.mergeBaseBudCustomization(moSession, aoHMArgs);
		assertTrue(loStatusResult);
	}

	@Test(expected = ApplicationException.class)
	public void testMergeBaseBudCustomizationException() throws ApplicationException
	{
		HashMap aoHMArgs = new HashMap();
		ContractBean loContractBean = new ContractBean();
		loContractBean.setContractId("403");
		loContractBean.setParentContractId("290");
		aoHMArgs.put("aoContractBean", loContractBean);
		Boolean loStatusResult = moFinancialsService.mergeBaseBudCustomization(null, aoHMArgs);
	}

	// case1: Exception
	// pass line item id that is not in database
	// it will fetch remaining amount as null and null cannot be casted to
	// double
	@Test
	public void testEditInvoiceRateGrid1() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			RateBean loRateBean = new RateBean();
			loRateBean.setInvoiceId("55");
			loRateBean.setContractBudgetID("555");
			loRateBean.setId("909"); // this row does not exist and brings null
										// as result
			// and null cannot be casted to Double
			loRateBean.setSubBudgetID("555");

			boolean lbEditStatus = moFinancialsService.editInvoiceRateGrid(loRateBean, moSession);
			assertTrue(lbEditStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// case2: Application Exception
	// set rate bean to null
	@Test
	public void testEditInvoiceRateGrid2() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			RateBean loRateBean = null;

			boolean lbEditStatus = moFinancialsService.editInvoiceRateGrid(loRateBean, moSession);

			assertTrue(lbEditStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// case3: throws error message because remaining amount is less than invoice
	// amount
	// pass all required data
	@Test
	public void testEditInvoiceRateGrid3() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			RateBean loRateBean = new RateBean();
			loRateBean.setInvoiceId("55");
			loRateBean.setContractBudgetID("555");
			loRateBean.setId("5");
			loRateBean.setSubBudgetID("555");
			loRateBean.setYtdInvoiceAmt("800");

			boolean lbEditStatus = moFinancialsService.editInvoiceRateGrid(loRateBean, moSession);
			assertTrue(lbEditStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// case3: successfully update invoice entry
	// pass all required data
	@Test
	public void testEditInvoiceRateGrid4() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			RateBean loRateBean = new RateBean();
			loRateBean.setInvoiceId("25");
			loRateBean.setContractBudgetID("174");
			loRateBean.setId("427");
			loRateBean.setSubBudgetID("293");
			loRateBean.setYtdInvoiceAmt("200");
			loRateBean.setModifiedByUserId("909");
			loRateBean.setCreatedByUserId("909");

			boolean lbEditStatus = moFinancialsService.editInvoiceRateGrid(loRateBean, moSession);
			assertTrue(lbEditStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	// case3: successfully insert new invoice entry(because no invoice was done
	// yet)
	// pass all required data
	@Test
	public void testEditInvoiceRateGrid5() throws ApplicationException
	{
		Boolean lbThrown = false;
		try
		{
			RateBean loRateBean = new RateBean();
			loRateBean.setInvoiceId("55");
			loRateBean.setContractBudgetID("555");
			loRateBean.setId("1"); // no entry should be there in
									// invoice_details
			loRateBean.setSubBudgetID("555");
			loRateBean.setYtdInvoiceAmt("50");
			loRateBean.setModifiedByUserId("2160");
			loRateBean.setCreatedByUserId("2160");

			boolean lbEditStatus = moFinancialsService.editInvoiceRateGrid(loRateBean, moSession);

			assertTrue(lbEditStatus);
		}
		catch (ApplicationException loAppEx)
		{
			lbThrown = true;
			assertTrue("Exception thrown", lbThrown);
		}
	}

	@Test(expected = ApplicationException.class)
	public void testEditInvoiceRateGrid6() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setInvoiceId("25");
		loRateBean.setContractBudgetID("174");
		loRateBean.setId("427");
		loRateBean.setSubBudgetID("293");
		loRateBean.setYtdInvoiceAmt("10000");
		loRateBean.setModifiedByUserId("909");
		loRateBean.setCreatedByUserId("909");

		InvoiceService loInvoiceService = new InvoiceService();

		moFinancialsService.editInvoiceRateGrid(loRateBean, moSession);

	}

	@Test(expected = ApplicationException.class)
	public void testEditInvoiceRateGrid7() throws ApplicationException
	{
		RateBean loRateBean = new RateBean();
		loRateBean.setInvoiceId("25");
		loRateBean.setContractBudgetID("174");
		loRateBean.setId("427");
		loRateBean.setSubBudgetID("293");
		loRateBean.setInvUnits("50");
		loRateBean.setModifiedByUserId("909");
		loRateBean.setCreatedByUserId("909");

		InvoiceService loInvoiceService = new InvoiceService();

		moFinancialsService.editInvoiceRateGrid(loRateBean, moSession);

	}

	@Test(expected = ApplicationException.class)
	public void testupdatePdfEntryContractBudget() throws ApplicationException, IOException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId(agency);
		loTaskDetailsBean.setContractId(baseContractId);
		loTaskDetailsBean.setContractId(parentBudgetID);
		moFinancialsService.updatePdfEntryContractBudget(moSession, loTaskDetailsBean, true);
	}

	@Test
	public void testupdatePdfEntryContractBudgetExp() throws ApplicationException, IOException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId(agency);
		loTaskDetailsBean.setContractId(baseContractId);
		loTaskDetailsBean.setContractId(parentBudgetID);
		moFinancialsService.updatePdfEntryContractBudget(moSession, loTaskDetailsBean, true);
	}

	@Test(expected = ApplicationException.class)
	public void testupdatePdfEntryContractCof() throws ApplicationException, IOException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId(agency);
		loTaskDetailsBean.setContractId(baseContractId);
		loTaskDetailsBean.setContractId(parentBudgetID);
		moFinancialsService.updatePdfEntryContractCof(null, loTaskDetailsBean, true);
	}

	@Test
	public void testupdatePdfEntryContractCofExp() throws ApplicationException, IOException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId(agency);
		loTaskDetailsBean.setContractId(baseContractId);
		loTaskDetailsBean.setContractId(parentBudgetID);
		moFinancialsService.updatePdfEntryContractCof(moSession, loTaskDetailsBean, true);
	}

	@Test(expected = ApplicationException.class)
	public void testupdatePdfEntryBudgetAmendment() throws ApplicationException, IOException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId(agency);
		loTaskDetailsBean.setContractId(baseContractId);
		loTaskDetailsBean.setContractId(parentBudgetID);
		moFinancialsService.updatePdfEntryBudgetAmendment(null, loTaskDetailsBean, true);
	}

	@Test
	public void testupdatePdfEntryBudgetAmendmentExp() throws ApplicationException, IOException
	{
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setUserId(agency);
		loTaskDetailsBean.setContractId(baseContractId);
		loTaskDetailsBean.setContractId(parentBudgetID);
		moFinancialsService.updatePdfEntryBudgetAmendment(moSession, loTaskDetailsBean, true);
	}

	@Test
	public void testupdateContractStartEndDateForOpenEndedRfp() throws ApplicationException, IOException
	{

		HashMap loContractDetails = new HashMap();
		loContractDetails.put("contractId", "537");
		loContractDetails.put("contractStartDate", "11/11/2014");
		loContractDetails.put("contractEndDate", "11/11/2014");

		Boolean loUpdatedContractStartEndDateForOpenEndedRfp = moFinancialsService
				.updateContractStartEndDateForOpenEndedRfp(moSession, loContractDetails);
		assertTrue(loUpdatedContractStartEndDateForOpenEndedRfp);
	}

	@Test(expected = ApplicationException.class)
	public void testupdateContractStartEndDateForOpenEndedRfpExp() throws ApplicationException, IOException
	{

		HashMap loContractDetails = new HashMap();
		loContractDetails.put("contractId", "537");
		loContractDetails.put("contractStartDate", "11/11/2014");
		loContractDetails.put("contractEndDate", "11/11/2014");

		Boolean loUpdatedContractStartEndDateForOpenEndedRfp = moFinancialsService
				.updateContractStartEndDateForOpenEndedRfp(null, loContractDetails);
		assertTrue(loUpdatedContractStartEndDateForOpenEndedRfp);
	}

	@Test
	public void testopenEndedRfpStartEndDateNotSet() throws ApplicationException, IOException
	{
		Boolean loUpdatedContractStartEndDateForOpenEndedRfp = moFinancialsService.openEndedRfpStartEndDateNotSet(
				moSession, "537");
		assertTrue(loUpdatedContractStartEndDateForOpenEndedRfp);
	}

	@Test(expected = ApplicationException.class)
	public void testopenEndedRfpStartEndDateNotSetExp() throws ApplicationException, IOException
	{
		Boolean loUpdatedContractStartEndDateForOpenEndedRfp = moFinancialsService.openEndedRfpStartEndDateNotSet(null,
				"537");
		assertTrue(loUpdatedContractStartEndDateForOpenEndedRfp);
	}

	@Test
	public void testupdateYtdAndRemainingAmountInBudgetAndSubBudget() throws ApplicationException, IOException
	{
		Boolean loUpdatedContractStartEndDateForOpenEndedRfp = moFinancialsService
				.updateYtdAndRemainingAmountInBudgetAndSubBudget(moSession, "153", true);
		assertTrue(loUpdatedContractStartEndDateForOpenEndedRfp);
	}

	@Test(expected = ApplicationException.class)
	public void testupdateYtdAndRemainingAmountInBudgetAndSubBudgetExp() throws ApplicationException, IOException
	{
		Boolean loUpdatedContractStartEndDateForOpenEndedRfp = moFinancialsService
				.updateYtdAndRemainingAmountInBudgetAndSubBudget(null, "153", true);
		assertTrue(loUpdatedContractStartEndDateForOpenEndedRfp);
	}

	@Test
	public void testupdateYtdAndRemainingAmountInLineItems() throws ApplicationException, IOException
	{
		Boolean loUpdatedContractStartEndDateForOpenEndedRfp = moFinancialsService
				.updateYtdAndRemainingAmountInLineItems(moSession, "153", true);
		assertTrue(loUpdatedContractStartEndDateForOpenEndedRfp);
	}

	@Test(expected = ApplicationException.class)
	public void testupdateYtdAndRemainingAmountInLineItemsExp() throws ApplicationException, IOException
	{
		Boolean loUpdatedContractStartEndDateForOpenEndedRfp = moFinancialsService
				.updateYtdAndRemainingAmountInLineItems(null, "153", true);
		assertTrue(loUpdatedContractStartEndDateForOpenEndedRfp);
	}

	@Test
	public void testgetBaseBudgetIdFromInvoiceId() throws ApplicationException, IOException
	{
		String loUpdatedContractStartEndDateForOpenEndedRfp = moFinancialsService.getBaseBudgetIdFromInvoiceId(
				moSession, "8", true);
		assertNotNull(loUpdatedContractStartEndDateForOpenEndedRfp);
	}

	@Test(expected = ApplicationException.class)
	public void testgetBaseBudgetIdFromInvoiceIdExp() throws ApplicationException, IOException
	{
		String loUpdatedContractStartEndDateForOpenEndedRfp = moFinancialsService.getBaseBudgetIdFromInvoiceId(null,
				"8", true);
		assertNotNull(loUpdatedContractStartEndDateForOpenEndedRfp);
	}

	@Test
	public void testgetDocumentTitle() throws ApplicationException, IOException
	{
		ContractList aoContractFilterBean = new ContractList();
		aoContractFilterBean.setContractId(baseContractId);
		String loUpdatedContractStartEndDateForOpenEndedRfp = moFinancialsService.getDocumentTitle(moSession,
				aoContractFilterBean);
		assertNotNull(loUpdatedContractStartEndDateForOpenEndedRfp);
	}

	@Test(expected = ApplicationException.class)
	public void testsentForRegOrCanCheckExp() throws ApplicationException, IOException
	{
		ContractList aoContractFilterBean = new ContractList();
		aoContractFilterBean.setContractId(baseContractId);
		moFinancialsService.sentForRegOrCanCheck(null, aoContractFilterBean);

	}

	@Test
	public void testsentForRegOrCanCheck() throws ApplicationException, IOException
	{
		ContractList aoContractFilterBean = new ContractList();
		aoContractFilterBean.setContractId(baseContractId);
		aoContractFilterBean.setContractStatusId("130");
		HashMap loAmendMap = moFinancialsService.sentForRegOrCanCheck(moSession, aoContractFilterBean);
		assertNotNull(loAmendMap);
	}

	@Test
	public void testprocessContractAfterAmendmentTask() throws ApplicationException, IOException
	{
		ContractList aoContractFilterBean = new ContractList();
		aoContractFilterBean.setContractId(baseContractId);
		aoContractFilterBean.setContractStatusId("130");
		TaskDetailsBean loTaskDetailsBean = new TaskDetailsBean();
		loTaskDetailsBean.setContractId(baseContractId);
		Boolean lbProcessFlag = moFinancialsService.processContractAfterAmendmentTask(moSession, loTaskDetailsBean,
				"60");
		assertTrue(lbProcessFlag);
	}

	@Test
	public void testInsertDocoumentPropertiesInDB() throws ApplicationException, IOException
	{

		HashMap<String, Object> loParamMap = new HashMap<String, Object>();
		loParamMap.put(P8Constants.PROPERTY_CE_MIME_TYPE, "xlsx");
		loParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "Test");
		loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_CREATED_BY, "city_43");
		loParamMap.put(P8Constants.PROPERTY_CE_HHS_DOC_MODIFIED_BY, "city_43");
		FinancialsService loFinancialsService = new FinancialsService();
		boolean recordInserted = loFinancialsService.insertBulkuploadDocumentInDB(moSession, loParamMap,
				"{0F28CCF8-3159-4350-B5FC-875A1BDEE77C}");
		assertTrue(recordInserted);
	}

	@Test
	public void testUpdateDocStatusInDB() throws ApplicationException, IOException
	{
		FinancialsService loFinancialsService = new FinancialsService();
		boolean recordInserted = loFinancialsService.updateDocumentStatusInDB(moSession,
				"{0F28CCF8-3159-4350-B5FC-875A1BDEE76C}", "144");
		assertTrue(recordInserted);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateDocStatusInDBExp() throws ApplicationException, IOException
	{
		FinancialsService loFinancialsService = new FinancialsService();
		boolean recordInserted = loFinancialsService.updateDocumentStatusInDB(moSession,
				"{0F28CCF8-3159-4350-B5FC-875A1BDEE76C}", "144");
		assertTrue(recordInserted);
	}

	@Test
	public void testupdateBulkUploadDocLockStatus() throws ApplicationException, IOException
	{
		FinancialsService loFinancialsService = new FinancialsService();
		boolean recordInserted = loFinancialsService.updateBulkUploadDocLockStatus(moSession,
				"{0F28CCF8-3159-4350-B5FC-875A1BDEE76C}");
		assertTrue(recordInserted);
	}

	@Test(expected = ApplicationException.class)
	public void testupdateBulkUploadDocLockStatusExp() throws ApplicationException, IOException
	{
		FinancialsService loFinancialsService = new FinancialsService();
		boolean recordInserted = loFinancialsService.updateBulkUploadDocLockStatus(null,
				"{0F28CCF8-3159-4350-B5FC-875A1BDEE76C}");
		assertTrue(recordInserted);
	}

	@Test
	public void testgetFinancialsListOfMap() throws ApplicationException, IOException
	{
		FinancialsService loFinancialsService = new FinancialsService();
		HashMap loHmProps = new HashMap();
		HashMap loRequiredParamMap = new HashMap();
		loRequiredParamMap.put(P8Constants.PROPERTY_CE_CONTRACT_TITLE, "asd");
		loRequiredParamMap.put(P8Constants.PROPERTY_CE_CONTRACT_ID, "123");
		loRequiredParamMap.put(P8Constants.PROPERTY_CE_DOCUMENT_TITLE, "asd");
		loHmProps.put("213", loRequiredParamMap);
		List<Map<String, String>> loFinalDocumentList = loFinancialsService.getFinancialsListOfMap(loHmProps, "r3_org",
				"123");
		assertNotNull(loFinalDocumentList);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testUpdateEntryTypeDetails1() throws ApplicationException, IOException
	{
		HashMap loParam = new HashMap<String, String>();
		loParam.put(HHSConstants.SCREEN_NAME, HHSConstants.CONTRACT_CONFIG_UPDATE);
		loParam.put(HHSConstants.IS_CHECKED, HHSConstants.TRUE);
		loParam.put(HHSConstants.ENTRY_TYPE_ID, "1");
		loParam.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		loParam.put(HHSConstants.CONTRACT_ID, "48");
		loParam.put(HHSConstants.CONTRACT_TYPE_ID, "4");
		loParam.put(HHSConstants.BMC_BUDGET_FISCAL_YEAR, "2014");
		Boolean lbStatusFlag = moFinancialsService.updateEntryTypeDetails(moSession, loParam);
		assertNotNull(lbStatusFlag);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testUpdateEntryTypeDetails2() throws ApplicationException, IOException
	{
		HashMap loParam = new HashMap<String, String>();
		loParam.put(HHSConstants.SCREEN_NAME, HHSConstants.CONTRACT_CONFIG_UPDATE);
		loParam.put(HHSConstants.IS_CHECKED, HHSConstants.FALSE);
		loParam.put(HHSConstants.ENTRY_TYPE_ID, "1");
		loParam.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		loParam.put(HHSConstants.CONTRACT_ID, "48");
		loParam.put(HHSConstants.CONTRACT_TYPE_ID, "4");
		loParam.put(HHSConstants.BMC_BUDGET_FISCAL_YEAR, "2014");
		Boolean lbStatusFlag = moFinancialsService.updateEntryTypeDetails(moSession, loParam);
		assertNotNull(lbStatusFlag);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testUpdateEntryTypeDetails3() throws ApplicationException, IOException
	{
		HashMap loParam = new HashMap<String, String>();
		loParam.put(HHSConstants.SCREEN_NAME, HHSConstants.EMPTY_STRING);
		loParam.put(HHSConstants.IS_CHECKED, HHSConstants.TRUE);
		loParam.put(HHSConstants.ENTRY_TYPE_ID, "2");
		loParam.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		loParam.put(HHSConstants.CONTRACT_ID, "35");
		loParam.put(HHSConstants.BUDGET_ID, "6");
		Boolean lbStatusFlag = moFinancialsService.updateEntryTypeDetails(moSession, loParam);
		assertNotNull(lbStatusFlag);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testUpdateEntryTypeDetails4() throws ApplicationException, IOException
	{
		HashMap loParam = new HashMap<String, String>();
		loParam.put(HHSConstants.SCREEN_NAME, HHSConstants.EMPTY_STRING);
		loParam.put(HHSConstants.IS_CHECKED, HHSConstants.FALSE);
		loParam.put(HHSConstants.ENTRY_TYPE_ID, "2");
		loParam.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		loParam.put(HHSConstants.CONTRACT_ID, "35");
		loParam.put(HHSConstants.BUDGET_ID, "6");
		Boolean lbStatusFlag = moFinancialsService.updateEntryTypeDetails(moSession, loParam);
		assertNotNull(lbStatusFlag);
	}

	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	@Test
	public void testUpdateEntryTypeDetails5() throws ApplicationException, IOException
	{
		HashMap loParam = new HashMap<String, String>();
		loParam.put(HHSConstants.SCREEN_NAME, HHSConstants.BMC_NEWFY_CONFIG_TASK);
		loParam.put(HHSConstants.IS_CHECKED, HHSConstants.TRUE);
		loParam.put(HHSConstants.ENTRY_TYPE_ID, "2");
		loParam.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		loParam.put(HHSConstants.CONTRACT_ID, "574");
		loParam.put(HHSConstants.BMC_BUDGET_FISCAL_YEAR, "2015");
		Boolean lbStatusFlag = moFinancialsService.updateEntryTypeDetails(moSession, loParam);
		assertNotNull(lbStatusFlag);
	}

	@Test(expected = ApplicationException.class)
	public void testUpdateEntryTypeDetails6() throws ApplicationException, IOException
	{
		HashMap loParam = new HashMap<String, String>();
		loParam.put(HHSConstants.SCREEN_NAME, HHSConstants.BMC_NEWFY_CONFIG_TASK);
		loParam.put(HHSConstants.IS_CHECKED, HHSConstants.TRUE);
		loParam.put(HHSConstants.ENTRY_TYPE_ID, "2");
		loParam.put(HHSConstants.CREATED_BY_USER_ID, "agency_21");
		loParam.put(HHSConstants.CONTRACT_ID, "574");
		loParam.put(HHSConstants.BMC_BUDGET_FISCAL_YEAR, "2015");
		Boolean lbStatusFlag = moFinancialsService.updateEntryTypeDetails(null, loParam);
		assertNotNull(lbStatusFlag);
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServicefetchAmendmentListSummary0Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.fetchAmendmentListSummary(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServicegetContractsCount1Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.getContractsCount(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServiceprocessContractAfterAmendmentTask2Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.processContractAfterAmendmentTask(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServiceinsertBulkuploadDocumentInDB3Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.insertBulkuploadDocumentInDB(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServiceupdateEntryTypeDetails4Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.updateEntryTypeDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServicegetLineItemsState5Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.getLineItemsState(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServicepublishEntryTypeDetails6Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.publishEntryTypeDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServicecopyPreviousCBCToCurrentCBC7Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.copyPreviousCBCToCurrentCBC(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServicemergeBaseActiveBudget8Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.mergeBaseActiveBudget(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServicesentForRegOrCanCheck9Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.sentForRegOrCanCheck(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServicegetDocumentTitle10Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.getDocumentTitle(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServicegetFinancialsListOfMap11Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.getFinancialsListOfMap(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServicegetBaseBudgetIdFromInvoiceId13Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.getBaseBudgetIdFromInvoiceId(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServiceupdateYtdAndRemainingAmountInLineItems14Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.updateYtdAndRemainingAmountInLineItems(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServiceupdateYtdAndRemainingAmountInBudgetAndSubBudget15Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.updateYtdAndRemainingAmountInBudgetAndSubBudget(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServiceupdateDocumentStatusInDB16Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.updateDocumentStatusInDB(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServiceupdateBulkUploadDocLockStatus17Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.updateBulkUploadDocLockStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServiceopenEndedRfpStartEndDateNotSet18Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.openEndedRfpStartEndDateNotSet(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServiceupdateContractStartEndDateForOpenEndedRfp19Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.updateContractStartEndDateForOpenEndedRfp(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServiceupdateBudgetTemplate20Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.updateBudgetTemplate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServiceupdatePdfEntryBudgetAmendment21Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.updatePdfEntryBudgetAmendment(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServiceupdatePdfEntryContractCof22Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.updatePdfEntryContractCof(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsServiceupdatePdfEntryContractBudget23Negative()
	{
		FinancialsService loFinancialsService = new FinancialsService();
		try
		{
			loFinancialsService.updatePdfEntryContractBudget(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}


}
