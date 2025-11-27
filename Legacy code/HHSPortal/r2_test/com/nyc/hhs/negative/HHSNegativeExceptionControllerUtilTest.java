package com.nyc.hhs.negative;

import org.junit.Test;

import com.nyc.hhs.controllers.util.BaseControllerUtil;
import com.nyc.hhs.controllers.util.BmcControllerUtil;
import com.nyc.hhs.controllers.util.ContractBudgetControllerUtils;
import com.nyc.hhs.controllers.util.ContractListUtils;
import com.nyc.hhs.controllers.util.ControllerUtils;
import com.nyc.hhs.controllers.util.InvoiceUtils;
import com.nyc.hhs.exception.ApplicationException;

public class HHSNegativeExceptionControllerUtilTest
{
	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilexecuteStaticGridTransaction0Negative()
	{
		try
		{
			BaseControllerUtil.executeStaticGridTransaction(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilcatchTaskError1Negative() throws ApplicationException
	{
		BaseControllerUtil.catchTaskError(null, null);
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilsetRequiredParam2Negative() throws ApplicationException
	{
		BaseControllerUtil.setRequiredParam(null);
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilfinishTaskApproveUtil3Negative()
	{
		try
		{
			BaseControllerUtil.finishTaskApproveUtil(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilreAssignTaskUtil4Negative()
	{
		try
		{
			BaseControllerUtil.reAssignTaskUtil(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilviewCommentsHistoryUtil5Negative()
	{
		try
		{
			BaseControllerUtil.viewCommentsHistoryUtil(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilgetEpinListUtil6Negative()
	{
		try
		{
			BaseControllerUtil.getEpinListUtil(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilgetContractFiscalYearsUtil7Negative() throws ApplicationException
	{
		BaseControllerUtil.getContractFiscalYearsUtil(null, null, null);

	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilshowAccountMainGridUtil8Negative() throws ApplicationException
	{
		BaseControllerUtil.showAccountMainGridUtil(null, null);

	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilshowAccountSubGridUtil9Negative() throws ApplicationException
	{
		BaseControllerUtil.showAccountSubGridUtil(null, null, null, null, 0, null);

	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilvalidateChartAllocationFYIUtil10Negative()
	{
		try
		{
			BaseControllerUtil.validateChartAllocationFYIUtil(null, null, null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtiladdDocumentFromVaultActionUtil11Negative()
	{
		try
		{
			BaseControllerUtil.addDocumentFromVaultActionUtil(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilsetParametersMapValue12Negative()
	{
		try
		{
			BaseControllerUtil.setParametersMapValue(null, null, null, null, null, null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilgetSortDetailsFromXMLUtil13Negative()
	{
		try
		{
			BaseControllerUtil.getSortDetailsFromXMLUtil(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilsaveCommentNonAuditUtil15Negative()
	{
		try
		{
			BaseControllerUtil.saveCommentNonAuditUtil(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilcopyCBGridBeanToAllocBeanUtil16Negative()
	{
		try
		{
			BaseControllerUtil.copyCBGridBeanToAllocBeanUtil(null, null, null, null, null, null, null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilgetFiscalYearUtil17Negative()
	{
		try
		{
			BaseControllerUtil.getFiscalYearUtil(null, 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilgetFundingMainHeaderUtil18Negative()
	{
		try
		{
			BaseControllerUtil.getFundingMainHeaderUtil(null, 0);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilsaveDocumentPropertiesActionUtil19Negative()
	{
		try
		{
			BaseControllerUtil.saveDocumentPropertiesActionUtil(null, null, null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilfYRowDataUtil20Negative()
	{
		try
		{
			BaseControllerUtil.fYRowDataUtil(null, null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilpopulateBeanFromRequestUtil21Negative()
	{
		try
		{
			BaseControllerUtil.populateBeanFromRequestUtil(null, null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilgetTermsAndCondition22Negative()
	{
		try
		{
			BaseControllerUtil.getTermsAndCondition(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilsetCOFAccountHeaderDataInSessionUtil23Negative()
	{
		try
		{
			BaseControllerUtil.setCOFAccountHeaderDataInSessionUtil(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilinsertDocumentDetailsInDBOnUploadUtil24Negative()
	{
		try
		{
			BaseControllerUtil.insertDocumentDetailsInDBOnUploadUtil(null, null, null, null, null, null, null, null,
					null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtildeleteTempFile25Negative()
	{
		try
		{
			BaseControllerUtil.deleteTempFile(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilgetFiscalYearCustomSubGridPropUtil26Negative()
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		try
		{
			loBaseControllerUtil.getFiscalYearCustomSubGridPropUtil(null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilvalidateUserUtil27Negative()
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		try
		{
			loBaseControllerUtil.validateUserUtil(null, null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilsettingDefaultChannel28Negative()
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		try
		{
			loBaseControllerUtil.settingDefaultChannel(null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilsaveDocHashMapUtil29Negative()
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		try
		{
			loBaseControllerUtil.saveDocHashMapUtil(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtildisplaySuccessUtil30Negative()
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		try
		{
			loBaseControllerUtil.displaySuccessUtil(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilsettingGridBeanObj31Negative()
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		try
		{
			loBaseControllerUtil.settingGridBeanObj(null, null, null, null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilshowFundingMainGridUtil32Negative()
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		try
		{
			loBaseControllerUtil.showFundingMainGridUtil(null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtillsOperationUpperCaseUtil33Negative()
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		try
		{
			loBaseControllerUtil.lsOperationUpperCaseUtil(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilactionRemoveDocChannelUtil34Negative()
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		try
		{
			loBaseControllerUtil.actionRemoveDocChannelUtil(null, null, null, null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilactionRemoveDocNxtChannelUtil35Negative()
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		try
		{
			loBaseControllerUtil.actionRemoveDocNxtChannelUtil(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilreAssignTaskDefPara36Negative()
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		try
		{
			loBaseControllerUtil.reAssignTaskDefPara(null, null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilfetchTaskDetailsFromFilenetUtil37Negative()
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		try
		{
			loBaseControllerUtil.fetchTaskDetailsFromFilenetUtil(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBaseControllerUtilclosingPrintWriter38Negative()
	{
		BaseControllerUtil loBaseControllerUtil = new BaseControllerUtil();
		try
		{
			loBaseControllerUtil.closingPrintWriter(null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBmcControllerUtilfetchNewFYBudgetAmount0Negative()
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		try
		{
			loBmcControllerUtil.fetchNewFYBudgetAmount(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBmcControllerUtilexecuteGridTransactionForBudgetConfig1Negative()
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		try
		{
			loBmcControllerUtil.executeGridTransactionForBudgetConfig(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBmcControllerUtilexecuteGridTransactionForBudgetConfigUpdateTask2Negative()
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		try
		{
			loBmcControllerUtil.executeGridTransactionForBudgetConfigUpdateTask(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBmcControllerUtilgetBudgetDetails3Negative()
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		try
		{
			loBmcControllerUtil.getBudgetDetails(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBmcControllerUtilgetFYList4Negative()
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		try
		{
			loBmcControllerUtil.getFYList(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBmcControllerUtilgetFYBudgetPlannedAmount5Negative()
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		try
		{
			loBmcControllerUtil.getFYBudgetPlannedAmount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBmcControllerUtilexecuteGridTransactionForBudgetConfigUpdate6Negative()
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		try
		{
			loBmcControllerUtil.executeGridTransactionForBudgetConfigUpdate(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBmcControllerUtilgetFYBudgetPlannedForUpdatedContractId7Negative()
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		try
		{
			loBmcControllerUtil.getFYBudgetPlannedForUpdatedContractId(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBmcControllerUtilcheckBudgetDetails8Negative()
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		try
		{
			loBmcControllerUtil.checkBudgetDetails(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testBmcControllerUtiladdNewBudget9Negative()
	{
		BmcControllerUtil loBmcControllerUtil = new BmcControllerUtil();
		try
		{
			loBmcControllerUtil.addNewBudget(null, null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetControllerUtilsgetlsJspPath1Negative()
	{
		ContractBudgetControllerUtils loContractBudgetControllerUtils = new ContractBudgetControllerUtils();
		try
		{
			loContractBudgetControllerUtils.getlsJspPath(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetControllerUtilsvalidateBudgetStatus2Negative()
	{
		ContractBudgetControllerUtils loContractBudgetControllerUtils = new ContractBudgetControllerUtils();
		try
		{
			loContractBudgetControllerUtils.validateBudgetStatus(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetControllerUtilsfetchErrorMsg3Negative()
	{
		ContractBudgetControllerUtils loContractBudgetControllerUtils = new ContractBudgetControllerUtils();
		try
		{
			loContractBudgetControllerUtils.fetchErrorMsg(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetControllerUtilsfetchCurrentBudgetStatus4Negative()
	{
		ContractBudgetControllerUtils loContractBudgetControllerUtils = new ContractBudgetControllerUtils();
		try
		{
			loContractBudgetControllerUtils.fetchCurrentBudgetStatus(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetControllerUtilsgetbudgetStatus5Negative()
	{
		ContractBudgetControllerUtils loContractBudgetControllerUtils = new ContractBudgetControllerUtils();
		try
		{
			loContractBudgetControllerUtils.getbudgetStatus(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractListUtilsgetProgramNameList0Negative()
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		try
		{
			loContractListUtils.getProgramNameList(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractListUtilsgetAgencyDetails1Negative()
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		try
		{
			loContractListUtils.getAgencyDetails(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractListUtilsgetRenewalRecordExist2Negative()
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		try
		{
			loContractListUtils.getRenewalRecordExist(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractListUtilssetModifiedBy3Negative()
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		try
		{
			loContractListUtils.setModifiedBy(null, null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractListUtilsgetAmendErrorCheck4Negative()
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		try
		{
			loContractListUtils.getAmendErrorCheck(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractListUtilsselectContractAmendmentId5Negative()
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		try
		{
			loContractListUtils.selectContractAmendmentId(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractListUtilsvalidateEpin6Negative()
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		try
		{
			loContractListUtils.validateEpin(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractListUtilsvalidateProviderAccelerator7Negative()
	{
		ContractListUtils loContractListUtils = new ContractListUtils();
		try
		{
			loContractListUtils.validateProviderAccelerator(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testControllerUtilsaddAssignee0Negative()
	{
		ControllerUtils loControllerUtils = new ControllerUtils();
		try
		{
			loControllerUtils.addAssignee(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceUtilsaddAssignee0Negative()
	{
		InvoiceUtils loInvoiceUtils = new InvoiceUtils();
		try
		{
			loInvoiceUtils.addAssignee(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * @Test(expected = java.lang.Exception.class) public void
	 * testInvoiceUtilsinvoiceReviewLevelUtil1Negative() { InvoiceUtils
	 * loInvoiceUtils = new InvoiceUtils(); try {
	 * loInvoiceUtils.invoiceReviewLevelUtil(null, null, null, null); } catch
	 * (ApplicationException e) { e.printStackTrace(); } }
	 */

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceUtilssubmitInvoiceConfirmationOverlayUtil2Negative()
	{
		InvoiceUtils loInvoiceUtils = new InvoiceUtils();
		try
		{
			loInvoiceUtils.submitInvoiceConfirmationOverlayUtil(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceUtilsvalidateInvoiceStatus3Negative()
	{
		InvoiceUtils loInvoiceUtils = new InvoiceUtils();
		try
		{
			loInvoiceUtils.validateInvoiceStatus(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceUtilssetUserForUserType4Negative()
	{
		InvoiceUtils loInvoiceUtils = new InvoiceUtils();
		try
		{
			loInvoiceUtils.setUserForUserType(null, null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceUtilssetJSPNameForInvoiceReviewTask5Negative()
	{
		InvoiceUtils loInvoiceUtils = new InvoiceUtils();
		try
		{
			loInvoiceUtils.setJSPNameForInvoiceReviewTask(null, null);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
