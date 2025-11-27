package com.nyc.hhs.negative;

import java.util.HashMap;

import org.junit.Test;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.HHSUtil;

public class HHSNegativeExceptionUtilTest
{

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetServicesList0Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getServicesList();
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsortByValues2Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.sortByValues(new HashMap());
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetZeroTimeDate3Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getZeroTimeDate(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgenerateDelimitedResponse4Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.generateDelimitedResponse(null, null, 0);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgenerateDelimitedAutoCompleteResponse5Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.generateDelimitedAutoCompleteResponse(null, null, 0);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetToDate6Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getToDate();
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsetDateToFrom7Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.setDateToFrom(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsetHhsAudit8Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.setHhsAudit(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtiladdAuditDataToChannel9Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.addAuditDataToChannel(null, null, null, null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsetAuditOnFinancialFinishTask10Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.setAuditOnFinancialFinishTask(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilpopulateSubGridRows11Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.populateSubGridRows(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetHeader12Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getHeader(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetHeaderProp13Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getHeaderProp(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetSubGridProp14Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getSubGridProp(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetSubGridHeaderRow15Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getSubGridHeaderRow(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsetMasterStatus16Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.setMasterStatus();
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetStatusName17Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getStatusName(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetStatusID18Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getStatusID(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetStatusMap19Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getStatusMap(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetAgencyMap20Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getAgencyMap();
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetCurrentTimestampDate21Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getCurrentTimestampDate();
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetSubStringCount22Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getSubStringCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsetFinancialWFProperty23Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.setFinancialWFProperty(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetFinancialEntityId24Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getFinancialEntityId(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtiladdFromClause25Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.addFromClause(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtiladdToClause26Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.addToClause(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilconvertCurrencyFormatToNumber27Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.convertCurrencyFormatToNumber(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilformatAmount28Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.formatAmount(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilconvertAddressValidationFields29Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.convertAddressValidationFields(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetFYDetails30Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getFYDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetNewBudgetStartDate31Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getNewBudgetStartDate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetNewBudgetEndDate32Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getNewBudgetEndDate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetFYForContractBudgetConfig33Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getFYForContractBudgetConfig(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilisEmptyList34Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.isEmptyList(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtildeleteAllDownloadedTemplates35Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.deleteAllDownloadedTemplates(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetAgencyName36Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getAgencyName(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetBudgetFY37Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getBudgetFY(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilround38Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.round((new Float(0.0)), 0);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilisAgencyNonAccoUser40Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.isAgencyNonAccoUser(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilisAcceleratorUser42Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.isAcceleratorUser(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtildocumentTypeTransactionName43Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.documentTypeTransactionName(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtiladdDocumentFromVaultTransactionName44Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.addDocumentFromVaultTransactionName(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetTransactionNameInsertDocumentDetailsInDBOnUpload45Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getTransactionNameInsertDocumentDetailsInDBOnUpload(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilsortList46Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.sortList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilconvertStringToArray47Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.convertStringToArray(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetTransactionName48Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getTransactionName(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilcopyListToList49Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.copyListToList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilhomeProcurementTransaction50Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.homeProcurementTransaction(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilhomeFinancialTransaction51Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.homeFinancialTransaction(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetEvidenceFlag52Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getEvidenceFlag(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetStarDoubleStarStatus53Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getStarDoubleStarStatus(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetDateFromEpochTime54Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getDateFromEpochTime(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetEpochTimeFromDate55Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getEpochTimeFromDate(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtiladdAuditDataToChannel60Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.addAuditDataToChannel(null, null, null, null, null, null, null, null, null, null, null, null,
					null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetApplicationSettings64Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getApplicationSettings();
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetTaskDetailsBeanFromMap72Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getTaskDetailsBeanFromMap(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHHSUtilgetReassignUserMap74Negative()
	{
		HHSUtil loHHSUtil = new HHSUtil();
		try
		{
			loHHSUtil.getReassignUserMap(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

}
