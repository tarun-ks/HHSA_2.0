package com.nyc.hhs.negative;

import org.junit.Test;

import com.nyc.hhs.controllers.util.BaseControllerUtil;
import com.nyc.hhs.controllers.util.BmcControllerUtil;
import com.nyc.hhs.controllers.util.ContractBudgetControllerUtils;
import com.nyc.hhs.controllers.util.ContractListUtils;
import com.nyc.hhs.controllers.util.ControllerUtils;
import com.nyc.hhs.controllers.util.InvoiceUtils;
import com.nyc.hhs.exception.ApplicationException;

public class HHSNegativeApplicationExceptionControllerUtilTest
{
	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilexecuteStaticGridTransaction0Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.executeStaticGridTransaction(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilcatchTaskError1Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.catchTaskError(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilsetRequiredParam2Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.setRequiredParam(null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilfinishTaskApproveUtil3Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.finishTaskApproveUtil(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilreAssignTaskUtil4Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.reAssignTaskUtil(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilviewCommentsHistoryUtil5Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.viewCommentsHistoryUtil(null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilgetEpinListUtil6Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.getEpinListUtil(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilgetContractFiscalYearsUtil7Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.getContractFiscalYearsUtil(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilshowAccountMainGridUtil8Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.showAccountMainGridUtil(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilshowAccountSubGridUtil9Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.showAccountSubGridUtil(null, null, null, null, 0, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilvalidateChartAllocationFYIUtil10Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.validateChartAllocationFYIUtil(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtiladdDocumentFromVaultActionUtil11Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.addDocumentFromVaultActionUtil(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilsetParametersMapValue12Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.setParametersMapValue(null, null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilgetSortDetailsFromXMLUtil13Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.getSortDetailsFromXMLUtil(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilsaveCommentNonAuditUtil15Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.saveCommentNonAuditUtil(null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilcopyCBGridBeanToAllocBeanUtil16Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.copyCBGridBeanToAllocBeanUtil(null, null, null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilgetFiscalYearUtil17Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.getFiscalYearUtil(null, 0);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilgetFundingMainHeaderUtil18Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.getFundingMainHeaderUtil(null, 0);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilsaveDocumentPropertiesActionUtil19Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.saveDocumentPropertiesActionUtil(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilfYRowDataUtil20Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.fYRowDataUtil(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilpopulateBeanFromRequestUtil21Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.populateBeanFromRequestUtil(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilgetTermsAndCondition22Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.getTermsAndCondition(null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilsetCOFAccountHeaderDataInSessionUtil23Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.setCOFAccountHeaderDataInSessionUtil(null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilinsertDocumentDetailsInDBOnUploadUtil24Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil
				.insertDocumentDetailsInDBOnUploadUtil(null, null, null, null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtildeleteTempFile25Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.deleteTempFile(null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilgetFiscalYearCustomSubGridPropUtil26Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.getFiscalYearCustomSubGridPropUtil(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilvalidateUserUtil27Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.validateUserUtil(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilsettingDefaultChannel28Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.settingDefaultChannel(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilsaveDocHashMapUtil29Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.saveDocHashMapUtil(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtildisplaySuccessUtil30Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.displaySuccessUtil(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilsettingGridBeanObj31Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.settingGridBeanObj(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilshowFundingMainGridUtil32Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.showFundingMainGridUtil(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtillsOperationUpperCaseUtil33Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.lsOperationUpperCaseUtil(null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilactionRemoveDocChannelUtil34Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.actionRemoveDocChannelUtil(null, null, null, null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilactionRemoveDocNxtChannelUtil35Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.actionRemoveDocNxtChannelUtil(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilreAssignTaskDefPara36Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.reAssignTaskDefPara(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilfetchTaskDetailsFromFilenetUtil37Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.fetchTaskDetailsFromFilenetUtil(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBaseControllerUtilclosingPrintWriter38Negative() throws ApplicationException
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		loBaseControllerUtil.closingPrintWriter(null);
	}

	@Test(expected = ApplicationException.class)
	public void testBmcControllerUtilfetchNewFYBudgetAmount0Negative() throws ApplicationException
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		loBmcControllerUtil.fetchNewFYBudgetAmount(null);
	}

	@Test(expected = ApplicationException.class)
	public void testBmcControllerUtilexecuteGridTransactionForBudgetConfig1Negative() throws ApplicationException
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		loBmcControllerUtil.executeGridTransactionForBudgetConfig(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBmcControllerUtilexecuteGridTransactionForBudgetConfigUpdateTask2Negative()
			throws ApplicationException
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		loBmcControllerUtil.executeGridTransactionForBudgetConfigUpdateTask(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBmcControllerUtilgetBudgetDetails3Negative() throws ApplicationException
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		loBmcControllerUtil.getBudgetDetails(null);
	}

	@Test(expected = ApplicationException.class)
	public void testBmcControllerUtilgetFYList4Negative() throws ApplicationException
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		loBmcControllerUtil.getFYList(null);
	}

	@Test(expected = ApplicationException.class)
	public void testBmcControllerUtilgetFYBudgetPlannedAmount5Negative() throws ApplicationException
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		loBmcControllerUtil.getFYBudgetPlannedAmount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBmcControllerUtilexecuteGridTransactionForBudgetConfigUpdate6Negative() throws ApplicationException
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		loBmcControllerUtil.executeGridTransactionForBudgetConfigUpdate(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBmcControllerUtilgetFYBudgetPlannedForUpdatedContractId7Negative() throws ApplicationException
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		loBmcControllerUtil.getFYBudgetPlannedForUpdatedContractId(null);
	}

	@Test(expected = ApplicationException.class)
	public void testBmcControllerUtilcheckBudgetDetails8Negative() throws ApplicationException
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		loBmcControllerUtil.checkBudgetDetails(null);
	}

	@Test(expected = ApplicationException.class)
	public void testBmcControllerUtiladdNewBudget9Negative() throws ApplicationException
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		loBmcControllerUtil.addNewBudget(null, null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetControllerUtilsgetlsJspPath1Negative() throws ApplicationException
	{
		ContractBudgetControllerUtils loContractBudgetControllerUtils = new ContractBudgetControllerUtils();
		loContractBudgetControllerUtils.getlsJspPath(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetControllerUtilsvalidateBudgetStatus2Negative() throws ApplicationException
	{
		ContractBudgetControllerUtils loContractBudgetControllerUtils = new ContractBudgetControllerUtils();
		loContractBudgetControllerUtils.validateBudgetStatus(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetControllerUtilsfetchErrorMsg3Negative() throws ApplicationException
	{
		ContractBudgetControllerUtils loContractBudgetControllerUtils = new ContractBudgetControllerUtils();
		loContractBudgetControllerUtils.fetchErrorMsg(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetControllerUtilsfetchCurrentBudgetStatus4Negative() throws ApplicationException
	{
		ContractBudgetControllerUtils loContractBudgetControllerUtils = new ContractBudgetControllerUtils();
		loContractBudgetControllerUtils.fetchCurrentBudgetStatus(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetControllerUtilsgetbudgetStatus5Negative() throws ApplicationException
	{
		ContractBudgetControllerUtils loContractBudgetControllerUtils = new ContractBudgetControllerUtils();
		loContractBudgetControllerUtils.getbudgetStatus(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractListUtilsgetProgramNameList0Negative() throws ApplicationException
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		loContractListUtils.getProgramNameList(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractListUtilsgetAgencyDetails1Negative() throws ApplicationException
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		loContractListUtils.getAgencyDetails(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractListUtilsgetRenewalRecordExist2Negative() throws ApplicationException
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		loContractListUtils.getRenewalRecordExist(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractListUtilssetModifiedBy3Negative() throws ApplicationException
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		loContractListUtils.setModifiedBy(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractListUtilsgetAmendErrorCheck4Negative() throws ApplicationException
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		loContractListUtils.getAmendErrorCheck(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractListUtilsselectContractAmendmentId5Negative() throws ApplicationException
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		loContractListUtils.selectContractAmendmentId(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractListUtilsvalidateEpin6Negative() throws ApplicationException
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		loContractListUtils.validateEpin(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractListUtilsvalidateProviderAccelerator7Negative() throws ApplicationException
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		loContractListUtils.validateProviderAccelerator(null);
	}

	@Test(expected = ApplicationException.class)
	public void testControllerUtilsaddAssignee0Negative() throws ApplicationException
	{
		ControllerUtils loControllerUtils = new ControllerUtils();
		loControllerUtils.addAssignee(null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceUtilsaddAssignee0Negative() throws ApplicationException
	{
		InvoiceUtils loInvoiceUtils = new InvoiceUtils();
		loInvoiceUtils.addAssignee(null);
	}

	/*
	 * @Test(expected = ApplicationException.class) public void
	 * testInvoiceUtilsinvoiceReviewLevelUtil1Negative() throws
	 * ApplicationException { InvoiceUtils loInvoiceUtils = new InvoiceUtils();
	 * loInvoiceUtils.invoiceReviewLevelUtil(null, null, null, null); }
	 */

	@Test(expected = ApplicationException.class)
	public void testInvoiceUtilssubmitInvoiceConfirmationOverlayUtil2Negative() throws ApplicationException
	{
		InvoiceUtils loInvoiceUtils = new InvoiceUtils();
		loInvoiceUtils.submitInvoiceConfirmationOverlayUtil(null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceUtilsvalidateInvoiceStatus3Negative() throws ApplicationException
	{
		InvoiceUtils loInvoiceUtils = new InvoiceUtils();
		loInvoiceUtils.validateInvoiceStatus(null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceUtilssetUserForUserType4Negative() throws ApplicationException
	{
		InvoiceUtils loInvoiceUtils = new InvoiceUtils();
		loInvoiceUtils.setUserForUserType(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceUtilssetJSPNameForInvoiceReviewTask5Negative() throws ApplicationException
	{
		InvoiceUtils loInvoiceUtils = new InvoiceUtils();
		loInvoiceUtils.setJSPNameForInvoiceReviewTask(null, null);
	}
}
