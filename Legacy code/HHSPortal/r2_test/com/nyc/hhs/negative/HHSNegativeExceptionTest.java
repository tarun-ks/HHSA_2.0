package com.nyc.hhs.negative;

import java.util.HashMap;

import org.junit.Test;

import com.nyc.hhs.daomanager.service.AgencySettingService;
import com.nyc.hhs.daomanager.service.AwardService;
import com.nyc.hhs.daomanager.service.BudgetManagementService;
import com.nyc.hhs.daomanager.service.ConfigurationService;
import com.nyc.hhs.daomanager.service.ContractBudgetAmendmentService;
import com.nyc.hhs.daomanager.service.ContractBudgetModificationService;
import com.nyc.hhs.daomanager.service.ContractBudgetService;
import com.nyc.hhs.daomanager.service.EvaluationService;
import com.nyc.hhs.daomanager.service.FinancialsBudgetService;
import com.nyc.hhs.daomanager.service.FinancialsInvoiceListService;
import com.nyc.hhs.daomanager.service.FinancialsListService;
import com.nyc.hhs.daomanager.service.HhsAuditService;
import com.nyc.hhs.daomanager.service.InvoiceService;
import com.nyc.hhs.daomanager.service.PaymentListService;
import com.nyc.hhs.daomanager.service.ProcurementService;
import com.nyc.hhs.daomanager.service.ProposalService;
import com.nyc.hhs.daomanager.service.RFPReleaseService;
import com.nyc.hhs.daomanager.service.SolicitationFinancialsGeneralService;
import com.nyc.hhs.daomanager.service.TaskService;
import com.nyc.hhs.daomanager.service.TaxonomyTaggingService;
import com.nyc.hhs.exception.ApplicationException;

public class HHSNegativeExceptionTest
{
	@Test(expected = Exception.class)
	public void testAgencySettingServicefetchAgencyAndReviewProcessData0Negative()
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		try
		{

			loAgencySettingService.fetchAgencyAndReviewProcessData(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAgencySettingServicefetchReviewLevels1Negative()
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		try
		{
			loAgencySettingService.fetchReviewLevels(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAgencySettingServicesaveReviewLevels2Negative()
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		try
		{
			loAgencySettingService.saveReviewLevels(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAgencySettingServicefetchAllReviewProcessData3Negative()
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		try
		{
			loAgencySettingService.fetchAllReviewProcessData(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAgencySettingServicefetchAgencySetAssgndUsrData4Negative()
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		try
		{
			loAgencySettingService.fetchAgencySetAssgndUsrData(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAgencySettingServicesaveAgencyLevelUsers5Negative()
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		try
		{
			loAgencySettingService.saveAgencyLevelUsers(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAwardDetails0Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAwardDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAwardId1Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAwardId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAwardDocuments2Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAwardDocuments(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAwardReviewStatus3Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAwardReviewStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceremoveAwardDocuments4Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.removeAwardDocuments(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceinsertAwardDocumentDetails5Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.insertAwardDocumentDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAwardsDetails6Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAwardsDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceupdateAwardStatus7Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.updateAwardStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceupdateRelatedProposal8Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.updateRelatedProposal(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceupdateAwardReviewStatus9Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.updateAwardReviewStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceactionGetawardAndContractsList10Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.actionGetawardAndContractsList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchDocumentsForAwardDocTask11Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchDocumentsForAwardDocTask(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceaptProgressView12Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.aptProgressView(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceremoveAwardTaskDocs13Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.removeAwardTaskDocs(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicesaveAwardDocumentConfig14Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.saveAwardDocumentConfig(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceinsertAwardTaskDocDetails15Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.insertAwardTaskDocDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceawardAndContractsCount16Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.awardAndContractsCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAwardEPinDetails17Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAwardEPinDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicefetchAmountProviderDetails18Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.fetchAmountProviderDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceassignAwardEpin19Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.assignAwardEpin(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServiceupdateAwardDetailsFromTask20Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.updateAwardDetailsFromTask(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testAwardServicectNotCancelledClosedRegistered21Negative()
	{
		AwardService loAwardService = new AwardService();
		try
		{
			loAwardService.ctNotCancelledClosedRegistered(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * @Test(expected = java.lang.Exception.class) public void
	 * testBudgetManagementServicefetchContractConfCOADetails0Negative() {
	 * BudgetManagementService loBudgetManagementService = new
	 * BudgetManagementService(); try {
	 * loBudgetManagementService.fetchContractConfCOADetails(null, null); }
	 * catch (ApplicationException e) { e.printStackTrace(); } }
	 */

	@Test(expected = java.lang.Exception.class)
	public void testBudgetManagementServicefetchContractCofTaskDetails13Negative()
	{
		BudgetManagementService loBudgetManagementService = new BudgetManagementService();
		try
		{
			loBudgetManagementService.fetchContractCofTaskDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
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
	public void testConfigurationServicefetchPCOFCoADetails2Negative()
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
	public void testConfigurationServicefetchContractConfFundingDetails3Negative()
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
	public void testConfigurationServicefetchPCOFFundingSourcesDetails4Negative()
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
	public void testConfigurationServiceeditContractConfFundingDetails5Negative()
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
	public void testConfigurationServiceinsertCoADetails6Negative()
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
	public void testConfigurationServiceupdateCoADetails7Negative()
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
	public void testConfigurationServicedeleteProcurementCoADetails8Negative()
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
	public void testConfigurationServiceupdateFundingSourcesDetails9Negative()
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
	public void testConfigurationServiceprocessContractAfterCOFTask10Negative()
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
	public void testConfigurationServiceupdateContractStatusToPendingConfig11Negative()
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
	public void testConfigurationServicefetchContractConfigDetails12Negative()
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
	public void testConfigurationServicefetchContractConfigBudgetDetails13Negative()
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
	public void testConfigurationServicefetchContractConfCOADetails14Negative()
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
	public void testConfigurationServiceaddContractConfCOADetails15Negative()
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
	public void testConfigurationServicedelContractConfCOADetails16Negative()
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
	public void testConfigurationServiceeditContractConfCOADetails17Negative()
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
	public void testConfigurationServicefetchContractConfSubBudgetDetails118Negative()
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
	public void testConfigurationServicefetchBudgetDetailsByFYAndContractId19Negative()
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
	public void testConfigurationServiceinsertContractConfSubBudgetDetails20Negative()
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
	public void testConfigurationServiceinsertNewBudgetDetails21Negative()
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
	public void testConfigurationServiceupdateBudgetFYTotalBudgetAmount22Negative()
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
	public void testConfigurationServiceeditContractConfSubBudgetDetails23Negative()
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

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicedelContractConfSubBudgetDetails25Negative()
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
	public void testConfigurationServicesetProcurementCOFStatus26Negative()
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
	public void testConfigurationServicefetchContractConfUpdateDetails27Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchContractConfUpdateDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceaddContractConfUpdateTaskDetails28Negative()
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
	public void testConfigurationServiceeditContractConfUpdateDetails29Negative()
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
	public void testConfigurationServiceupdateFetchedContractDetails30Negative()
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
	public void testConfigurationServicefetchContractConfUpdateActualDetails31Negative()
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
	public void testConfigurationServicefetchFYAndContractId32Negative()
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
	public void testConfigurationServicefetchContractConfUpdateSubBudgetDetails33Negative()
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
	public void testConfigurationServiceaddContractConfUpdateBudgetDetails34Negative()
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
	public void testConfigurationServiceinsertUpdatedSubBudgetDetails35Negative()
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
	public void testConfigurationServiceeditContractConfUpdateSubBudgetDetails36Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.editContractConfUpdateSubBudgetDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServiceeditNewFYConfCOADetails37Negative()
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
	public void testConfigurationServicecopyPreviousFYSubBudgetToCurrentFY38Negative()
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
	public void testConfigurationServicefetchFYPlannedAmount39Negative()
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
	public void testConfigurationServicefetchNewFYSubBudgetDetails40Negative()
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
	public void testConfigurationServiceupdateBudgetForNewFYConfigurationTask41Negative()
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
	public void testConfigurationServicegetContractEndDate42Negative()
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
	public void testConfigurationServicecheckIfBudgetExists43Negative()
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
	public void testConfigurationServicefetchConfigurableYearBudgetAmount44Negative()
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
	public void testConfigurationServicevalidateContractConfigUpdateAmount45Negative()
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
	public void testConfigurationServicedelContractConfUpdateTaskDetails46Negative()
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
	public void testConfigurationServicedelContractConfUpdateSubBudgetDetails47Negative()
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
	public void testConfigurationServicemergeContractConfUpdateFinishTask48Negative()
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
	public void testConfigurationServicefetchPlannedAmtForUpdatedContractId49Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.fetchPlannedAmtForUpdatedContractId(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testConfigurationServicecheckBudgetDetails50Negative()
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
	public void testConfigurationServicecreateDuplicateRows51Negative()
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		try
		{
			loConfigurationService.createDuplicateRows(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchProgramIncomeAmendment0Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchProgramIncomeAmendment(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceupdateProgramIncomeAmendment1Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.updateProgramIncomeAmendment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchContractedServicesAmendmentConsultants2Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchContractedServicesAmendmentConsultants(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchContractedServicesAmendmentSubContractors3Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchContractedServicesAmendmentSubContractors(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchContractedServicesAmendmentVendors4Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchContractedServicesAmendmentVendors(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceaddContractedServicesAmendment5Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.addContractedServicesAmendment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceeditContractedServicesAmendment6Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.editContractedServicesAmendment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicedeleteContractedServicesAmendment7Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.deleteContractedServicesAmendment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * @Test(expected = java.lang.Exception.class) public void
	 * testContractBudgetAmendmentServicefetchNonGridContractedServicesModification8Negative
	 * () { ContractBudgetAmendmentService loContractBudgetAmendmentService =
	 * new ContractBudgetAmendmentService(); try {
	 * loContractBudgetAmendmentService
	 * .fetchNonGridContractedServicesModification(null, null); } catch
	 * (ApplicationException e) { e.printStackTrace(); } }
	 */

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchFyBudgetSummary9Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchFyBudgetSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicegetCbGridDataForSession10Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.getCbGridDataForSession(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchCMSubBudgetSummary11Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchCMSubBudgetSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceupdateAmendmentUnallocatedFunds12Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.updateAmendmentUnallocatedFunds(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchAmendmentUnallocatedFunds13Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchAmendmentUnallocatedFunds(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServicefetchProfServicesDetailsAmendment14Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.fetchProfServicesDetailsAmendment(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetAmendmentServiceeditProfServicesDetailsAmendment15Negative()
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		try
		{
			loContractBudgetAmendmentService.editProfServicesDetailsAmendment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchCMSubBudgetSummary0Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchCMSubBudgetSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceinsertModificationBudgetDetails1Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.insertModificationBudgetDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchModifiedBudgetId2Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchModifiedBudgetId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceinsertModificationSubBudgetDetails3Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.insertModificationSubBudgetDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicecbmFetchProfServicesDetails4Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.cbmFetchProfServicesDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicecbmEditProfServicesDetails5Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.cbmEditProfServicesDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicegetCbGridDataForSession6Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.getCbGridDataForSession(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceupdateModificationUnallocatedFunds7Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.updateModificationUnallocatedFunds(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchModificationUnallocatedFunds8Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchModificationUnallocatedFunds(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchMilestone9Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchMilestone(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicegetSeqForMilestone10Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.getSeqForMilestone(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceaddMilestone11Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.addMilestone(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceupdateMilestone12Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.updateMilestone(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicedeleteMilestone13Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.deleteMilestone(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchContractBudgetModificationRate14Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchContractBudgetModificationRate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceinsertContractBudgetModificationRateInfo15Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.insertContractBudgetModificationRateInfo(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceupdateContractBudgetModificationRateInfo16Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.updateContractBudgetModificationRateInfo(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicedeleteContractBudgetModificationRateInfo17Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.deleteContractBudgetModificationRateInfo(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchProgramIncomeModification18Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchProgramIncomeModification(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceupdateProgramIncomeModification19Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.updateProgramIncomeModification(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchSalariedEmployeeBudgetForModification20Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchSalariedEmployeeBudgetForModification(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchHourlyEmployeeBudgetForModification21Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchHourlyEmployeeBudgetForModification(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchSeasonalEmployeeBudgetForModification22Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchSeasonalEmployeeBudgetForModification(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchFringeBenifitsForModification23Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchFringeBenifitsForModification(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceaddEmployeeBudgetForModification24Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.addEmployeeBudgetForModification(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceeditEmployeeBudgetForModification25Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.editEmployeeBudgetForModification(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceeditFringeBenifitsForModification26Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.editFringeBenifitsForModification(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchOpAndSupportModPageData27Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchOpAndSupportModPageData(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchModificationOTPS28Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchModificationOTPS(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchModificationEquipment29Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchModificationEquipment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceeditOperationAndSupportModificationDetails30Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.editOperationAndSupportModificationDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceaddEquipmentModificationDetails31Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.addEquipmentModificationDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceeditEquipmentModificationDetails32Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.editEquipmentModificationDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicedelEquipmentModificationDetails33Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.delEquipmentModificationDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicemergeBudgetModificationDocument34Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.mergeBudgetModificationDocument(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchContractedServicesModificationConsultants35Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchContractedServicesModificationConsultants(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchContractedServicesModificationSubContractors36Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchContractedServicesModificationSubContractors(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchContractedServicesModificationVendors37Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchContractedServicesModificationVendors(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceaddContractedServicesModification38Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.addContractedServicesModification(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceeditContractedServicesModification39Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.editContractedServicesModification(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicedeleteContractedServicesModification40Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.deleteContractedServicesModification(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchNonGridContractedServicesModification41Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchNonGridContractedServicesModification(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicefetchModificationRent42Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.fetchModificationRent(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceupdateModificationRent43Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.updateModificationRent(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServiceinsertContractBudgetModificationRent44Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.insertContractBudgetModificationRent(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicedeleteRentModification45Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.deleteRentModification(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetModificationServicevalidateModificationAmountTotal46Negative()
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		try
		{
			loContractBudgetModificationService.validateModificationAmountTotal(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchBudgetSummary0Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchBudgetSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchModificationBudgetSummary1Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchModificationBudgetSummary(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceupdateUnallocatedFunds3Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.updateUnallocatedFunds(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchUnallocatedFunds4Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchUnallocatedFunds(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchContractBudgetRent5Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchContractBudgetRent(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceupdateContractBudgetRent6Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.updateContractBudgetRent(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceinsertContractBudgetRent7Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.insertContractBudgetRent(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicedeleteContractBudgetRent8Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.deleteContractBudgetRent(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicegetSeqForRent9Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.getSeqForRent(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchProgramIncome10Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchProgramIncome(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceupdateProgramIncome11Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.updateProgramIncome(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceupdateIndirectRate12Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.updateIndirectRate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchIndirectRate13Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchIndirectRate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceupdateIndirectRatePercentage14Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.updateIndirectRatePercentage(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchUtilities15Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchUtilities(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceupdateUtilities16Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.updateUtilities(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchProfServicesDetails17Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchProfServicesDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceeditProfServicesDetails18Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.editProfServicesDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchSalariedEmployeeBudget19Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchSalariedEmployeeBudget(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceaddEmployeeBudget20Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.addEmployeeBudget(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicedelEmployeeBudget21Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.delEmployeeBudget(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceeditEmployeeBudget22Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.editEmployeeBudget(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchHourlyEmployeeBudget23Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchHourlyEmployeeBudget(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchSeasonalEmployeeBudget24Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchSeasonalEmployeeBudget(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchFringeBenifits25Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchFringeBenifits(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceeditFringeBenifits26Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.editFringeBenifits(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchMilestone27Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchMilestone(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicegetSeqForMilestone28Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.getSeqForMilestone(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceaddMilestone29Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.addMilestone(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceupdateMilestone30Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.updateMilestone(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicedeleteMilestone31Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.deleteMilestone(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchOpAndSupportPageData32Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchOpAndSupportPageData(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchOperationAndSupportDetails33Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchOperationAndSupportDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceeditOperationAndSupportDetails34Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.editOperationAndSupportDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchEquipmentDetails35Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchEquipmentDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceaddEquipmentDetails36Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.addEquipmentDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceeditEquipmentDetails37Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.editEquipmentDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicedeleteEquipmentDetails38Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.deleteEquipmentDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchContractedServicesConsultants40Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchContractedServicesConsultants(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchContractedServicesSubContractors41Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchContractedServicesSubContractors(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchContractedServicesVendors42Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchContractedServicesVendors(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceaddContractedServices43Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.addContractedServices(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceeditContractedServices44Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.editContractedServices(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicedeleteContractedServices45Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.deleteContractedServices(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchNonGridContractedServices46Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchNonGridContractedServices(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchRate47Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchRate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceupdateRate48Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.updateRate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceinsertRate49Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.insertRate(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicedeleteRate50Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.deleteRate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchContractSummary51Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchContractSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchSubBudgetSummary52Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchSubBudgetSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchFyBudgetSummary53Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchFyBudgetSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicegetCbGridDataForSession54Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.getCbGridDataForSession(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchAssignmentSummary55Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchAssignmentSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceinsertBudgetDocumentDetails56Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.insertBudgetDocumentDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceinsertInvoiceDocumentDetails57Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.insertInvoiceDocumentDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicegetSeqForRate58Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.getSeqForRate(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchFinancialDocuments59Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchFinancialDocuments(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceinsertContractDocumentDetails60Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.insertContractDocumentDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceremoveFinancialDocs61Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.removeFinancialDocs(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicesetContractBudgetStatus62Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.setContractBudgetStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchPersonnelServiceData63Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchPersonnelServiceData(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceinsertStandardRowsSubBudgetLevel65Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.insertStandardRowsSubBudgetLevel(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchCurrentCBStatus66Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchCurrentCBStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchPersonnelServiceMasterData67Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchPersonnelServiceMasterData(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServiceinsertContractDetailsFromAwardTask72Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.insertContractDetailsFromAwardTask(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicefetchAdvanceDetails73Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.fetchAdvanceDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testContractBudgetServicecreateReplicaOfBudget74Negative()
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		try
		{
			loContractBudgetService.createReplicaOfBudget(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationScores0Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationScores(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationReviewScores1Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationReviewScores(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetEvaluationReviewScore2Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getEvaluationReviewScore(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefindEvaluationTaskSent3Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.findEvaluationTaskSent(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetEvaluationCount4Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getEvaluationCount(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetInternalEvaluationsList5Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getInternalEvaluationsList(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetExternalEvaluationsList6Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getExternalEvaluationsList(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicesaveEvaluationDetails8Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.saveEvaluationDetails(null, null, null, null, null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetProcurementAgencyId9Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getProcurementAgencyId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchProposalDetails10Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchProposalDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchProposalComments11Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchProposalComments(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetEvaluationScores12Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getEvaluationScores(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationResultsSelections13Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationResultsSelections(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationResultsCount14Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationResultsCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchFinalizeResultsVisibiltyStatus15Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchFinalizeResultsVisibiltyStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchUpdateResultsVisibiltyStatus16Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchUpdateResultsVisibiltyStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchInternalEvaluatorUsers18Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchInternalEvaluatorUsers(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchExternalEvaluatorUsers19Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchExternalEvaluatorUsers(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchReqProposalDetails20Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchReqProposalDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchAwardStatusId21Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchAwardStatusId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateModifiedFlag22Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateModifiedFlag(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateSelectedProposalDetails23Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateSelectedProposalDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateNotSelectedProposalDetails24Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateNotSelectedProposalDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationDetails26Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchAccoComments28Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchAccoComments(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationScoresDetails30Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationScoresDetails(null, new HashMap(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationScoresDetails31Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationScoresDetails(null, "", null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluatorCommentsDetails32Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluatorCommentsDetails(null, "", null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluatorCommentsDetails33Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluatorCommentsDetails(null, new HashMap(), null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchProcurementValue34Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchProcurementValue(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchAwardAmount35Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchAwardAmount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicecountFinalizeProcurementDetails36Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.countFinalizeProcurementDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateProcurementStatus37Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateProcurementStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicemarkProposalNonResponsive38Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.markProposalNonResponsive(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateAwardReviewStatus39Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateAwardReviewStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateProposalStatus41Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateProposalStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchIntExtProposalDetails45Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchIntExtProposalDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceinsertEvaluationStatus46Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.insertEvaluationStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicedeleteEvaluationSettingData47Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.deleteEvaluationSettingData(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateProposalStatus48Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateProposalStatus(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationCriteriaDetails49Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationCriteriaDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetDBDDocsList51Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getDBDDocsList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicedownloadDBDDocumentsAndZip52Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.downloadDBDDocumentsAndZip(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchProviderNameList53Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchProviderNameList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchExtAndIntEvaluator55Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchExtAndIntEvaluator(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationScoreDetails57Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationScoreDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetCloseButtonVisibiltyStatus58Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getCloseButtonVisibiltyStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetDownloadDBDDocsVisibiltyStatus59Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getDownloadDBDDocsVisibiltyStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetCancelEvalTaskVisibiltyStatus60Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getCancelEvalTaskVisibiltyStatus(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetSendEvaluationTasksVisibiltyStatus61Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getSendEvaluationTasksVisibiltyStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicegetTotalEvaluationData62Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.getTotalEvaluationData(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicesaveEvaluationScoreDetails63Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.saveEvaluationScoreDetails(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchNoOfProviders64Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchNoOfProviders(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchNoOfProposals65Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchNoOfProposals(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchProposalCount67Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchProposalCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceconfirmReturnForAction68Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.confirmReturnForAction(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicemodifyProposalStatus69Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.modifyProposalStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateEvaluationStatus71Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateEvaluationStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchProcurementDetailsForAwardWF72Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchProcurementDetailsForAwardWF(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluatorDetails73Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluatorDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateEvaluationReviewsStatus74Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateEvaluationReviewsStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefinishEvaluationReviewsStatus75Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.finishEvaluationReviewsStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServiceupdateEvaluationResult76Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.updateEvaluationResult(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchEvaluationResultsScores77Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchEvaluationResultsScores(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchSelectionCommentsForAwardTask78Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchSelectionCommentsForAwardTask(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testEvaluationServicefetchAwardAppDate79Negative()
	{
		EvaluationService loEvaluationService = new EvaluationService();
		try
		{
			loEvaluationService.fetchAwardAppDate(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsBudgetServicefetchBudgetListSummary0Negative()
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		try
		{
			loFinancialsBudgetService.fetchBudgetListSummary(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsBudgetServicegetBudgetListCount1Negative()
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		try
		{
			loFinancialsBudgetService.getBudgetListCount(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsBudgetServicegetModifyBudgetFeasibility2Negative()
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		try
		{
			loFinancialsBudgetService.getModifyBudgetFeasibility(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsBudgetServicefetchRequestAdvance3Negative()
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		try
		{
			loFinancialsBudgetService.fetchRequestAdvance(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsBudgetServicecancelModificationBudget4Negative()
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		try
		{
			loFinancialsBudgetService.cancelModificationBudget(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsBudgetServiceterminateWorkflowForBudget5Negative()
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		try
		{
			loFinancialsBudgetService.terminateWorkflowForBudget(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsInvoiceListServicefetchInvoiceListSummary0Negative()
	{
		FinancialsInvoiceListService loFinancialsInvoiceListService = new FinancialsInvoiceListService();
		try
		{
			loFinancialsInvoiceListService.fetchInvoiceListSummary(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsInvoiceListServicegetInvoiceCount1Negative()
	{
		FinancialsInvoiceListService loFinancialsInvoiceListService = new FinancialsInvoiceListService();
		try
		{
			loFinancialsInvoiceListService.getInvoiceCount(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsInvoiceListServicewithdrawInvoiceList2Negative()
	{
		FinancialsInvoiceListService loFinancialsInvoiceListService = new FinancialsInvoiceListService();
		try
		{
			loFinancialsInvoiceListService.withdrawInvoiceList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsInvoiceListServicedeleteInvoiceList3Negative()
	{
		FinancialsInvoiceListService loFinancialsInvoiceListService = new FinancialsInvoiceListService();
		try
		{
			loFinancialsInvoiceListService.deleteInvoiceList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsInvoiceListServiceselectWithdrawInvoiceWorkFlowDetails5Negative()
	{
		FinancialsInvoiceListService loFinancialsInvoiceListService = new FinancialsInvoiceListService();
		try
		{
			loFinancialsInvoiceListService.selectWithdrawInvoiceWorkFlowDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicefetchContractListSummary0Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.fetchContractListSummary(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicefetchAgencyNames1Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.fetchAgencyNames(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicegetContractsCount2Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.getContractsCount(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicegetContractsValue3Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.getContractsValue(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceselectContractAmendmentId4Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.selectContractAmendmentId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceupdateContractAmendmentStatus5Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.updateContractAmendmentStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceupdateAmenBudgetStatus6Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.updateAmenBudgetStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicefindContractDetailsByEPIN7Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.findContractDetailsByEPIN(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicefindContractDetailsByEPINforNew8Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.findContractDetailsByEPINforNew(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceaddNewContractDetails9Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.addNewContractDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicevalidateCloseContract10Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.validateCloseContract(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicecloseContract11Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.closeContract(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicecancelContract12Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.cancelContract(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicevalidateRenewContractDetails13Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.validateRenewContractDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicevalidateProvider14Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.validateProvider(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicerenewContractDetails15Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.renewContractDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicesuspendContract16Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.suspendContract(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceupdateConfigurationErrorCheckRule17Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.updateConfigurationErrorCheckRule(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicegetFinancialWFProperty18Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.getFinancialWFProperty(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicegetNextSeqFromTable19Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.getNextSeqFromTable(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceunSuspendContract20Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.unSuspendContract(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicesuspendContractRelatedWorkflow21Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.suspendContractRelatedWorkflow(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceunsuspendContractRelatedWorkflow22Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.unsuspendContractRelatedWorkflow(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicecheckStatusIdForSuspended23Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.checkStatusIdForSuspended(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicecheckStatusIdForUnSuspended24Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.checkStatusIdForUnSuspended(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicerenewalRecordExist25Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.renewalRecordExist(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicevalidateAmendContract26Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.validateAmendContract(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceamendContractDetails28Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.amendContractDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicecancelContractErrorCheckRule29Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.cancelContractErrorCheckRule(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServiceterminateCancelContractWorkFlows30Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.terminateCancelContractWorkFlows(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testFinancialsListServicenewFYConfigErrorCheckRule31Negative()
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		try
		{
			loFinancialsListService.newFYConfigErrorCheckRule(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHhsAuditServicehhsauditInsert0Negative()
	{
		HhsAuditService loHhsAuditService = new HhsAuditService();
		try
		{
			loHhsAuditService.hhsauditInsert(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHhsAuditServicehhsMultiAuditInsert1Negative()
	{
		HhsAuditService loHhsAuditService = new HhsAuditService();
		try
		{
			loHhsAuditService.hhsMultiAuditInsert(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHhsAuditServicedeleteFromUserComment2Negative()
	{
		HhsAuditService loHhsAuditService = new HhsAuditService();
		try
		{
			loHhsAuditService.deleteFromUserComment(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testHhsAuditServicecopyCommentHistory3Negative()
	{
		HhsAuditService loHhsAuditService = new HhsAuditService();
		try
		{
			loHhsAuditService.copyCommentHistory(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchOperationAndSupportDetails0Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchOperationAndSupportDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicevalidateOpSupportInvoiceAmount1Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.validateOpSupportInvoiceAmount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditOperationAndSupportDetails2Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editOperationAndSupportDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchEquipmentDetails3Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchEquipmentDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicevalidateEquipmentInvoiceAmount4Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.validateEquipmentInvoiceAmount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditEquipmentDetails5Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editEquipmentDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceStatus6Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceTotalForOTPS7Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceTotalForOTPS(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchYTDInvoiced8Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchYTDInvoiced(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceUnallocatedFunds9Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceUnallocatedFunds(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchProgramIncomeInvoice10Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchProgramIncomeInvoice(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceupdateProgramIncomeInvoice11Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.updateProgramIncomeInvoice(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoicingUtilities12Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoicingUtilities(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceupdateInvoicingUtilities13Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.updateInvoicingUtilities(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceIndirectRate14Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceIndirectRate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceupdateInvoicingIndirectRate15Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.updateInvoicingIndirectRate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchMilestoneInvoice16Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchMilestoneInvoice(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceupdateMilestoneInvoice17Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.updateMilestoneInvoice(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceSummary18Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchProfServicesDetails19Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchProfServicesDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditProfServicesDetails20Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editProfServicesDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractInvoiceRent21Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractInvoiceRent(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceupdateContractInvoiceRent22Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.updateContractInvoiceRent(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractInvoiceFyBudgetSummary23Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractInvoiceFyBudgetSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractInvoiceSummary24Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractInvoiceSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractInvoiceInformation25Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractInvoiceInformation(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceupdateInvoiceStatus26Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.updateInvoiceStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchCurrInvoiceStatus27Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchCurrInvoiceStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicegetAgencyIdByContractForWF28Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.getAgencyIdByContractForWF(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditEmployeeInvoice29Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editEmployeeInvoice(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditFringeBenefits30Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editFringeBenefits(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchSalariedEmployeeBudget31Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchSalariedEmployeeBudget(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchHourlyEmployeeBudget32Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchHourlyEmployeeBudget(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchSeasonalEmployeeBudget33Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchSeasonalEmployeeBudget(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchFringeBenefits34Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchFringeBenefits(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceAssignmentSummary35Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceAssignmentSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditInvoiceAssignmentSummary36Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editInvoiceAssignmentSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceRateGrid37Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceRateGrid(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditContractedServicesInvoicing39Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editContractedServicesInvoicing(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractedServicesInvoicingConsultants40Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractedServicesInvoicingConsultants(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractedServicesInvoicingSubContractors41Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractedServicesInvoicingSubContractors(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchContractedServicesInvoicingVendors42Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchContractedServicesInvoicingVendors(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceerrorCheckInvoiceReviewTask43Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.errorCheckInvoiceReviewTask(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicesetStatusForInvoiceReviewTask44Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.setStatusForInvoiceReviewTask(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchCurrentAssignmentStatus45Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchCurrentAssignmentStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicesaveAgencyInvoiceNumber46Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.saveAgencyInvoiceNumber(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceInfo47Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceInfo(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchVendorList48Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchVendorList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicevalidateAssignee49Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.validateAssignee(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceaddAssigneeForBudget50Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.addAssigneeForBudget(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicegetNextSeqFromInvoiceTable51Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.getNextSeqFromInvoiceTable(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchSubBudgetSummary52Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchSubBudgetSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicegetCbGridDataForSession53Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.getCbGridDataForSession(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceupdateInvoiceDetails54Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.updateInvoiceDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicedelContractInvoiceAssignment55Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.delContractInvoiceAssignment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicevalidateInvoiceAdvanceStatus56Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.validateInvoiceAdvanceStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServiceeditInvoiceAdvanceDetails57Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.editInvoiceAdvanceDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testInvoiceServicefetchInvoiceAdvanceDetails58Negative()
	{
		InvoiceService loInvoiceService = new InvoiceService();
		try
		{
			loInvoiceService.fetchInvoiceAdvanceDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentListServicefetchPaymentListSummary0Negative()
	{
		PaymentListService loPaymentListService = new PaymentListService();
		try
		{
			loPaymentListService.fetchPaymentListSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentListServicegetPaymentCount1Negative()
	{
		PaymentListService loPaymentListService = new PaymentListService();
		try
		{
			loPaymentListService.getPaymentCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentListServicegetFiscalInformation2Negative()
	{
		PaymentListService loPaymentListService = new PaymentListService();
		try
		{
			loPaymentListService.getFiscalInformation(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentListServicegetSatusList3Negative()
	{
		PaymentListService loPaymentListService = new PaymentListService();
		try
		{
			loPaymentListService.getSatusList(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentListServicegetAgencyList4Negative()
	{
		PaymentListService loPaymentListService = new PaymentListService();
		try
		{
			loPaymentListService.getAgencyList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testPaymentListServicegetProgramName5Negative()
	{
		PaymentListService loPaymentListService = new PaymentListService();
		try
		{
			loPaymentListService.getProgramName(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchActiveProcurements0Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchActiveProcurements(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProcurementCount1Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProcurementCount(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProgramName2Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProgramName(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceinsertUpdateServiceAcceleratorService3Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.insertUpdateServiceAcceleratorService(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchEpinDetails4Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchEpinDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetAcceleratorContactDetails5Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getAcceleratorContactDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicesaveProcurementSummary6Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.saveProcurementSummary(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetStatusId7Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getStatusId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProcurementSummary8Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProcurementSummary(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchRfpReleaseDocsDetails9Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchRfpReleaseDocsDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProcurementId10Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProcurementId(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicedisplayApprovedProvidersList11Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.displayApprovedProvidersList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicecheckForEvidenceFlag13Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.checkForEvidenceFlag(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceupdateProcurementDataOnPublish14Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.updateProcurementDataOnPublish(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceupdateProcurementServiceData15Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.updateProcurementServiceData(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicecheckIfUserOfSameAgency16Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.checkIfUserOfSameAgency(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicecheckIfUserOfSameAgency17Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.checkIfUserOfSameAgency(null, null, null, true);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicecheckIfAwardApproved18Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.checkIfAwardApproved(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProcurementDetailsForNav19Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProcurementDetailsForNav(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceinsertRfpDocumentDetails20Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.insertRfpDocumentDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceremoveRfpDocs21Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.removeRfpDocs(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchProcurementCustomQuestionAnswer22Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchProcurementCustomQuestionAnswer(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchSelectedServices23Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchSelectedServices(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchServicesList24Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchServicesList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchApprovedProvDetails25Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchApprovedProvDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchApprovedProvidersList26Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchApprovedProvidersList(null, null, null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchApprovedProvidersListAfterRelease27Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchApprovedProvidersListAfterRelease(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetOrganizationDetail28Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getOrganizationDetail(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchDropDownValue29Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchDropDownValue(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicesaveApprovedProvDetails30Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.saveApprovedProvDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchProcurementCoNDetails31Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchProcurementCoNDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProviderStatus32Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProviderStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicedeleteProvidersData33Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.deleteProvidersData(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicepreserveOldStatus34Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.preserveOldStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProgramNameForAgencyId35Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProgramNameForAgencyId(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetSavedServicesList36Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getSavedServicesList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceupdateProcurementStatus37Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.updateProcurementStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchDocumentIdList38Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchDocumentIdList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProcurementSummaryForNav39Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProcurementSummaryForNav(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicegetProcurementTitle40Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.getProcurementTitle(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchApprovedProvidersForNotification41Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchApprovedProvidersForNotification(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchProposalCustomQuestions42Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchProposalCustomQuestions(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchProposalDocumentType43Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchProposalDocumentType(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicesaveProposalCustomQuestions44Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.saveProposalCustomQuestions(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicesaveProposalDocumentType45Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.saveProposalDocumentType(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceupdateProcurementDataWithRelease46Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.updateProcurementDataWithRelease(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchRfpReleaseDocIdsList47Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchRfpReleaseDocIdsList(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServiceconsolidateAllDocsProperties48Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.consolidateAllDocsProperties(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchProcTitleAndOrgList49Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchProcTitleAndOrgList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchDocumentIdsList50Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchDocumentIdsList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicefetchProcurementAddendumData51Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.fetchProcurementAddendumData(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProcurementServicecloseProcurement52Negative()
	{
		ProcurementService loProcurementService = new ProcurementService();
		try
		{
			loProcurementService.closeProcurement(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProposalSiteDetails0Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProposalSiteDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicesaveProposalDetails1Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.saveProposalDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchMemberDetails2Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchMemberDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchAllOrganizationMembers3Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchAllOrganizationMembers(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicecheckAllRequiredFieldsCompleted4Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.checkAllRequiredFieldsCompleted(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicecheckForDueDate5Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.checkForDueDate(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicesubmitProposal6Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.submitProposal(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetProposalSummary7Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getProposalSummary(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceretractProposal8Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.retractProposal(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicecancelProposal9Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.cancelProposal(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicecheckProposalEdit10Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.checkProposalEdit(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicecheckProposalCancel11Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.checkProposalCancel(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetDocumentIdList12Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getDocumentIdList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProposalTitle13Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProposalTitle(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProposalDetails14Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProposalDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceshowProposalDetailsReadonly15Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.showProposalDetailsReadonly(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchPermittedUsers16Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchPermittedUsers(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProposalDocuments17Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProposalDocuments(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceupdateProposalDocumentAndDetailStatus18Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.updateProposalDocumentAndDetailStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchRequiredOptionalDocuments19Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchRequiredOptionalDocuments(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceinsertNewProposalDetails20Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.insertNewProposalDetails(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetProposalDocumentList22Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getProposalDocumentList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceinsertProposalDocumentDetails23Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.insertProposalDocumentDetails(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceremoveProposalDocs24Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.removeProposalDocs(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProposalDetailsForTask25Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProposalDetailsForTask(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetProposalSiteDetails26Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getProposalSiteDetails(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetProposalDocuments27Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getProposalDocuments(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProcurementTitle28Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProcurementTitle(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchRFPDocListForTask29Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchRFPDocListForTask(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceupdateProposalDocumentStatusForTask30Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.updateProposalDocumentStatusForTask(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicefetchProcTitleAndOrgId31Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.fetchProcTitleAndOrgId(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceaddProposalAndProcurementStatus32Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.addProposalAndProcurementStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceupdateApprovedProviderStatus33Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.updateApprovedProviderStatus(null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceupdateProposalDocumentProperties34Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.updateProposalDocumentProperties(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServicegetOrgIdsForSelectedProposals35Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.getOrgIdsForSelectedProposals(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceupdateProposalStatusFromTask36Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.updateProposalStatusFromTask(null, null, null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testProposalServiceupdateProposalPreviousStatus37Negative()
	{
		ProposalService loProposalService = new ProposalService();
		try
		{
			loProposalService.updateProposalPreviousStatus(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicevalidateEPIN0Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.validateEPIN(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicevalidateRfpPreRequisites1Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.validateRfpPreRequisites(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicecheckCofApproval2Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.checkCofApproval(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceinsertAppProviderList3Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.insertAppProviderList(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicefetchEvaluationCriteria4Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.fetchEvaluationCriteria(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceupdateProcurementData5Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.updateProcurementData(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceupdateRfpDocument6Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.updateRfpDocument(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceupdateProcDocumentConfig7Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.updateProcDocumentConfig(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceupdateProcQuestionConfig8Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.updateProcQuestionConfig(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceupdateEvaluationCriteria9Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.updateEvaluationCriteria(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicedeleteAddendumData10Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.deleteAddendumData(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicesaveEvaluationCriteria12Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.saveEvaluationCriteria(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServicegetProcurementStatus13Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.getProcurementStatus(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testRFPReleaseServiceupdateRfpDocumentStatus14Negative()
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		try
		{
			loRFPReleaseService.updateRfpDocumentStatus(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicefetchProcurementCountForAccHomePage0Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.fetchProcurementCountForAccHomePage(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicefetchProcurementCountForProvHomePage1Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.fetchProcurementCountForProvHomePage(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicefetchAccFinancialsPortletCount2Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.fetchAccFinancialsPortletCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicefetchProviderFinancialCount3Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.fetchProviderFinancialCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicegetMasterStatus4Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.getMasterStatus(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicegetProcurementChangeControlWidget5Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.getProcurementChangeControlWidget(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServiceauthenticateLoginUser6Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.authenticateLoginUser(null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicefetchEpinList7Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.fetchEpinList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicefetchContractNoList8Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.fetchContractNoList(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServiceupdateLastModifiedDetails9Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.updateLastModifiedDetails(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicegetProviderWidgetDetils10Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.getProviderWidgetDetils(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicecheckDocumentExistsInAnyTable11Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.checkDocumentExistsInAnyTable(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testSolicitationFinancialsGeneralServicecheckDocumentsExistsInAnyTable12Negative()
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		try
		{
			loSolicitationFinancialsGeneralService.checkDocumentsExistsInAnyTable(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicereassignTask0Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.reassignTask(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicelaunchFinancialWorkflow1Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.launchFinancialWorkflow(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServiceterminateWorkflow2Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.terminateWorkflow(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServiceterminateWorkflow3Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.terminateWorkflow(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicecloseAllOpenTask4Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.closeAllOpenTask(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicesetPropertyInWF5Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.setPropertyInWF(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefinishTask6Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.finishTask(null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchTaskDetails7Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchTaskDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchAgencyTaskHistory8Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchAgencyTaskHistory(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchProviderTaskHistory9Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchProviderTaskHistory(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchAgencyDetails10Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchAgencyDetails(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchLastComment11Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchLastComment(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchUserLastComment12Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchUserLastComment(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchAgencyTaskHistory13Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchAgencyTaskHistory(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaskServicefetchAcceleratorTaskHistory14Negative()
	{
		TaskService loTaskService = new TaskService();
		try
		{
			loTaskService.fetchAcceleratorTaskHistory(null, null, null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaxonomyTaggingServicegetProcurementProposalDetails0Negative()
	{
		TaxonomyTaggingService loTaxonomyTaggingService = new TaxonomyTaggingService();
		try
		{
			loTaxonomyTaggingService.getProcurementProposalDetails(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaxonomyTaggingServicegetTaxonomyTaggingList1Negative()
	{
		TaxonomyTaggingService loTaxonomyTaggingService = new TaxonomyTaggingService();
		try
		{
			loTaxonomyTaggingService.getTaxonomyTaggingList(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaxonomyTaggingServicedeleteTaxonomyTags4Negative()
	{
		TaxonomyTaggingService loTaxonomyTaggingService = new TaxonomyTaggingService();
		try
		{
			loTaxonomyTaggingService.deleteTaxonomyTags(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}

	@Test(expected = java.lang.Exception.class)
	public void testTaxonomyTaggingServiceselectProcurementRecordCount5Negative()
	{
		TaxonomyTaggingService loTaxonomyTaggingService = new TaxonomyTaggingService();
		try
		{
			loTaxonomyTaggingService.selectProcurementRecordCount(null, null);
		}
		catch (ApplicationException e)
		{
			e.printStackTrace();
		}
	}
}
