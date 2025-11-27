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

public class HHSNegativeApplicationExceptionTest
{
	@Test(expected = ApplicationException.class)
	public void testAgencySettingServicefetchAgencyAndReviewProcessData0Negative() throws ApplicationException
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		loAgencySettingService.fetchAgencyAndReviewProcessData(null);
	}

	@Test(expected = ApplicationException.class)
	public void testAgencySettingServicefetchReviewLevels1Negative() throws ApplicationException
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		loAgencySettingService.fetchReviewLevels(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAgencySettingServicesaveReviewLevels2Negative() throws ApplicationException
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		loAgencySettingService.saveReviewLevels(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAgencySettingServicefetchAllReviewProcessData3Negative() throws ApplicationException
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		loAgencySettingService.fetchAllReviewProcessData(null);
	}

	@Test(expected = ApplicationException.class)
	public void testAgencySettingServicefetchAgencySetAssgndUsrData4Negative() throws ApplicationException
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		loAgencySettingService.fetchAgencySetAssgndUsrData(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAgencySettingServicesaveAgencyLevelUsers5Negative() throws ApplicationException
	{
		AgencySettingService loAgencySettingService = new AgencySettingService();
		loAgencySettingService.saveAgencyLevelUsers(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAwardDetails0Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAwardDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAwardId1Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAwardId(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAwardDocuments2Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAwardDocuments(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAwardReviewStatus3Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAwardReviewStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceremoveAwardDocuments4Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.removeAwardDocuments(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceinsertAwardDocumentDetails5Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.insertAwardDocumentDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAwardsDetails6Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAwardsDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceupdateAwardStatus7Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.updateAwardStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceupdateRelatedProposal8Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.updateRelatedProposal(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceupdateAwardReviewStatus9Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.updateAwardReviewStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceactionGetawardAndContractsList10Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.actionGetawardAndContractsList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchDocumentsForAwardDocTask11Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchDocumentsForAwardDocTask(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceaptProgressView12Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.aptProgressView(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceremoveAwardTaskDocs13Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.removeAwardTaskDocs(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicesaveAwardDocumentConfig14Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.saveAwardDocumentConfig(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceinsertAwardTaskDocDetails15Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.insertAwardTaskDocDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceawardAndContractsCount16Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.awardAndContractsCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAwardEPinDetails17Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAwardEPinDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicefetchAmountProviderDetails18Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.fetchAmountProviderDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceassignAwardEpin19Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.assignAwardEpin(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServiceupdateAwardDetailsFromTask20Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.updateAwardDetailsFromTask(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testAwardServicectNotCancelledClosedRegistered21Negative() throws ApplicationException
	{
		AwardService loAwardService = new AwardService();
		loAwardService.ctNotCancelledClosedRegistered(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testBudgetManagementServicefetchContractCofTaskDetails3Negative() throws ApplicationException
	{
		BudgetManagementService loBudgetManagementService = new BudgetManagementService();
		loBudgetManagementService.fetchContractCofTaskDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchProcurementDetails0Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchProcurementDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceprocStatusSet1Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.procStatusSet(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchPCOFCoADetails2Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchPCOFCoADetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchContractConfFundingDetails3Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchContractConfFundingDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchPCOFFundingSourcesDetails4Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchPCOFFundingSourcesDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceeditContractConfFundingDetails5Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.editContractConfFundingDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceinsertCoADetails6Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.insertCoADetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceupdateCoADetails7Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.updateCoADetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicedeleteProcurementCoADetails8Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.deleteProcurementCoADetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceupdateFundingSourcesDetails9Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.updateFundingSourcesDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceprocessContractAfterCOFTask10Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.processContractAfterCOFTask(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceupdateContractStatusToPendingConfig11Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.updateContractStatusToPendingConfig(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchContractConfigDetails12Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchContractConfigDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchContractConfigBudgetDetails13Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchContractConfigBudgetDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchContractConfCOADetails14Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchContractConfCOADetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceaddContractConfCOADetails15Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.addContractConfCOADetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicedelContractConfCOADetails16Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.delContractConfCOADetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceeditContractConfCOADetails17Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.editContractConfCOADetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchContractConfSubBudgetDetails118Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchContractConfSubBudgetDetails1(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchBudgetDetailsByFYAndContractId19Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchBudgetDetailsByFYAndContractId(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceinsertContractConfSubBudgetDetails20Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.insertContractConfSubBudgetDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceinsertNewBudgetDetails21Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.insertNewBudgetDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceupdateBudgetFYTotalBudgetAmount22Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.updateBudgetFYTotalBudgetAmount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceeditContractConfSubBudgetDetails23Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.editContractConfSubBudgetDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicedelContractConfSubBudgetDetails25Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.delContractConfSubBudgetDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicesetProcurementCOFStatus26Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.setProcurementCOFStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchContractConfUpdateDetails27Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchContractConfUpdateDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceaddContractConfUpdateTaskDetails28Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.addContractConfUpdateTaskDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceeditContractConfUpdateDetails29Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.editContractConfUpdateDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceupdateFetchedContractDetails30Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.updateFetchedContractDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchContractConfUpdateActualDetails31Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchContractConfUpdateActualDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchFYAndContractId32Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchFYAndContractId(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchContractConfUpdateSubBudgetDetails33Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchContractConfUpdateSubBudgetDetails(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceaddContractConfUpdateBudgetDetails34Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.addContractConfUpdateBudgetDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceinsertUpdatedSubBudgetDetails35Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.insertUpdatedSubBudgetDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceeditContractConfUpdateSubBudgetDetails36Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.editContractConfUpdateSubBudgetDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceeditNewFYConfCOADetails37Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.editNewFYConfCOADetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicecopyPreviousFYSubBudgetToCurrentFY38Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.copyPreviousFYSubBudgetToCurrentFY(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchFYPlannedAmount39Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchFYPlannedAmount(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchNewFYSubBudgetDetails40Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchNewFYSubBudgetDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServiceupdateBudgetForNewFYConfigurationTask41Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.updateBudgetForNewFYConfigurationTask(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicegetContractEndDate42Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.getContractEndDate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicecheckIfBudgetExists43Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.checkIfBudgetExists(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchConfigurableYearBudgetAmount44Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchConfigurableYearBudgetAmount(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicevalidateContractConfigUpdateAmount45Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.validateContractConfigUpdateAmount(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicedelContractConfUpdateTaskDetails46Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.delContractConfUpdateTaskDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicedelContractConfUpdateSubBudgetDetails47Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.delContractConfUpdateSubBudgetDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicemergeContractConfUpdateFinishTask48Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.mergeContractConfUpdateFinishTask(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicefetchPlannedAmtForUpdatedContractId49Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.fetchPlannedAmtForUpdatedContractId(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicecheckBudgetDetails50Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.checkBudgetDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testConfigurationServicecreateDuplicateRows51Negative() throws ApplicationException
	{
		ConfigurationService loConfigurationService = new ConfigurationService();
		loConfigurationService.createDuplicateRows(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServicefetchProgramIncomeAmendment0Negative() throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.fetchProgramIncomeAmendment(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServiceupdateProgramIncomeAmendment1Negative() throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.updateProgramIncomeAmendment(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServicefetchContractedServicesAmendmentConsultants2Negative()
			throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.fetchContractedServicesAmendmentConsultants(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServicefetchContractedServicesAmendmentSubContractors3Negative()
			throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.fetchContractedServicesAmendmentSubContractors(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServicefetchContractedServicesAmendmentVendors4Negative()
			throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.fetchContractedServicesAmendmentVendors(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServiceaddContractedServicesAmendment5Negative() throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.addContractedServicesAmendment(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServiceeditContractedServicesAmendment6Negative()
			throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.editContractedServicesAmendment(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServicedeleteContractedServicesAmendment7Negative()
			throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.deleteContractedServicesAmendment(null, null);
	}

	/*
	 * @Test(expected = ApplicationException.class) public void
	 * testContractBudgetAmendmentServicefetchNonGridContractedServicesModification8Negative
	 * () throws ApplicationException { ContractBudgetAmendmentService
	 * loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
	 * loContractBudgetAmendmentService
	 * .fetchNonGridContractedServicesModification(null, null); }
	 */

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServicefetchFyBudgetSummary9Negative() throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.fetchFyBudgetSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServicegetCbGridDataForSession10Negative() throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.getCbGridDataForSession(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServicefetchCMSubBudgetSummary11Negative() throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.fetchCMSubBudgetSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServiceupdateAmendmentUnallocatedFunds12Negative()
			throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.updateAmendmentUnallocatedFunds(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServicefetchAmendmentUnallocatedFunds13Negative()
			throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.fetchAmendmentUnallocatedFunds(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServicefetchProfServicesDetailsAmendment14Negative()
			throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.fetchProfServicesDetailsAmendment(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetAmendmentServiceeditProfServicesDetailsAmendment15Negative()
			throws ApplicationException
	{
		ContractBudgetAmendmentService loContractBudgetAmendmentService = new ContractBudgetAmendmentService();
		loContractBudgetAmendmentService.editProfServicesDetailsAmendment(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchCMSubBudgetSummary0Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchCMSubBudgetSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceinsertModificationBudgetDetails1Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.insertModificationBudgetDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchModifiedBudgetId2Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchModifiedBudgetId(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceinsertModificationSubBudgetDetails3Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.insertModificationSubBudgetDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicecbmFetchProfServicesDetails4Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.cbmFetchProfServicesDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicecbmEditProfServicesDetails5Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.cbmEditProfServicesDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicegetCbGridDataForSession6Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.getCbGridDataForSession(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceupdateModificationUnallocatedFunds7Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.updateModificationUnallocatedFunds(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchModificationUnallocatedFunds8Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchModificationUnallocatedFunds(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchMilestone9Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchMilestone(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicegetSeqForMilestone10Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.getSeqForMilestone(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceaddMilestone11Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.addMilestone(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceupdateMilestone12Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.updateMilestone(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicedeleteMilestone13Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.deleteMilestone(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchContractBudgetModificationRate14Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchContractBudgetModificationRate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceinsertContractBudgetModificationRateInfo15Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.insertContractBudgetModificationRateInfo(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceupdateContractBudgetModificationRateInfo16Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.updateContractBudgetModificationRateInfo(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicedeleteContractBudgetModificationRateInfo17Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.deleteContractBudgetModificationRateInfo(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchProgramIncomeModification18Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchProgramIncomeModification(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceupdateProgramIncomeModification19Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.updateProgramIncomeModification(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchSalariedEmployeeBudgetForModification20Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchSalariedEmployeeBudgetForModification(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchHourlyEmployeeBudgetForModification21Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchHourlyEmployeeBudgetForModification(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchSeasonalEmployeeBudgetForModification22Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchSeasonalEmployeeBudgetForModification(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchFringeBenifitsForModification23Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchFringeBenifitsForModification(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceaddEmployeeBudgetForModification24Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.addEmployeeBudgetForModification(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceeditEmployeeBudgetForModification25Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.editEmployeeBudgetForModification(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceeditFringeBenifitsForModification26Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.editFringeBenifitsForModification(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchOpAndSupportModPageData27Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchOpAndSupportModPageData(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchModificationOTPS28Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchModificationOTPS(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchModificationEquipment29Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchModificationEquipment(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceeditOperationAndSupportModificationDetails30Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.editOperationAndSupportModificationDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceaddEquipmentModificationDetails31Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.addEquipmentModificationDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceeditEquipmentModificationDetails32Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.editEquipmentModificationDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicedelEquipmentModificationDetails33Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.delEquipmentModificationDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicemergeBudgetModificationDocument34Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.mergeBudgetModificationDocument(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchContractedServicesModificationConsultants35Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchContractedServicesModificationConsultants(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchContractedServicesModificationSubContractors36Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchContractedServicesModificationSubContractors(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchContractedServicesModificationVendors37Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchContractedServicesModificationVendors(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceaddContractedServicesModification38Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.addContractedServicesModification(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceeditContractedServicesModification39Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.editContractedServicesModification(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicedeleteContractedServicesModification40Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.deleteContractedServicesModification(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchNonGridContractedServicesModification41Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchNonGridContractedServicesModification(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicefetchModificationRent42Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.fetchModificationRent(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceupdateModificationRent43Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.updateModificationRent(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServiceinsertContractBudgetModificationRent44Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.insertContractBudgetModificationRent(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicedeleteRentModification45Negative() throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.deleteRentModification(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetModificationServicevalidateModificationAmountTotal46Negative()
			throws ApplicationException
	{
		ContractBudgetModificationService loContractBudgetModificationService = new ContractBudgetModificationService();
		loContractBudgetModificationService.validateModificationAmountTotal(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchBudgetSummary0Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchBudgetSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchModificationBudgetSummary1Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchModificationBudgetSummary(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceupdateUnallocatedFunds3Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.updateUnallocatedFunds(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchUnallocatedFunds4Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchUnallocatedFunds(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchContractBudgetRent5Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchContractBudgetRent(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceupdateContractBudgetRent6Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.updateContractBudgetRent(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceinsertContractBudgetRent7Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.insertContractBudgetRent(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicedeleteContractBudgetRent8Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.deleteContractBudgetRent(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicegetSeqForRent9Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.getSeqForRent(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchProgramIncome10Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchProgramIncome(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceupdateProgramIncome11Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.updateProgramIncome(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceupdateIndirectRate12Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.updateIndirectRate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchIndirectRate13Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchIndirectRate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceupdateIndirectRatePercentage14Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.updateIndirectRatePercentage(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchUtilities15Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchUtilities(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceupdateUtilities16Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.updateUtilities(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchProfServicesDetails17Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchProfServicesDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceeditProfServicesDetails18Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.editProfServicesDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchSalariedEmployeeBudget19Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchSalariedEmployeeBudget(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceaddEmployeeBudget20Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.addEmployeeBudget(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicedelEmployeeBudget21Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.delEmployeeBudget(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceeditEmployeeBudget22Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.editEmployeeBudget(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchHourlyEmployeeBudget23Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchHourlyEmployeeBudget(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchSeasonalEmployeeBudget24Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchSeasonalEmployeeBudget(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchFringeBenifits25Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchFringeBenifits(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceeditFringeBenifits26Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.editFringeBenifits(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchMilestone27Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchMilestone(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicegetSeqForMilestone28Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.getSeqForMilestone(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceaddMilestone29Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.addMilestone(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceupdateMilestone30Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.updateMilestone(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicedeleteMilestone31Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.deleteMilestone(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchOpAndSupportPageData32Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchOpAndSupportPageData(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchOperationAndSupportDetails33Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchOperationAndSupportDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceeditOperationAndSupportDetails34Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.editOperationAndSupportDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchEquipmentDetails35Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchEquipmentDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceaddEquipmentDetails36Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.addEquipmentDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceeditEquipmentDetails37Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.editEquipmentDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicedeleteEquipmentDetails38Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.deleteEquipmentDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchContractedServicesConsultants40Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchContractedServicesConsultants(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchContractedServicesSubContractors41Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchContractedServicesSubContractors(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchContractedServicesVendors42Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchContractedServicesVendors(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceaddContractedServices43Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.addContractedServices(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceeditContractedServices44Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.editContractedServices(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicedeleteContractedServices45Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.deleteContractedServices(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchNonGridContractedServices46Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchNonGridContractedServices(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchRate47Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchRate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceupdateRate48Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.updateRate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceinsertRate49Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.insertRate(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicedeleteRate50Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.deleteRate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchContractSummary51Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchContractSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchSubBudgetSummary52Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchSubBudgetSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchFyBudgetSummary53Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchFyBudgetSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicegetCbGridDataForSession54Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.getCbGridDataForSession(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchAssignmentSummary55Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchAssignmentSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceinsertBudgetDocumentDetails56Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.insertBudgetDocumentDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceinsertInvoiceDocumentDetails57Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.insertInvoiceDocumentDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicegetSeqForRate58Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.getSeqForRate(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchFinancialDocuments59Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchFinancialDocuments(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceinsertContractDocumentDetails60Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.insertContractDocumentDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceremoveFinancialDocs61Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.removeFinancialDocs(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicesetContractBudgetStatus62Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.setContractBudgetStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchPersonnelServiceData63Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchPersonnelServiceData(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceinsertStandardRowsSubBudgetLevel65Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.insertStandardRowsSubBudgetLevel(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchCurrentCBStatus66Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchCurrentCBStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchPersonnelServiceMasterData67Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchPersonnelServiceMasterData(null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServiceinsertContractDetailsFromAwardTask72Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.insertContractDetailsFromAwardTask(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicefetchAdvanceDetails73Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.fetchAdvanceDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testContractBudgetServicecreateReplicaOfBudget74Negative() throws ApplicationException
	{
		ContractBudgetService loContractBudgetService = new ContractBudgetService();
		loContractBudgetService.createReplicaOfBudget(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationScores0Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationScores(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationReviewScores1Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationReviewScores(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetEvaluationReviewScore2Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getEvaluationReviewScore(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefindEvaluationTaskSent3Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.findEvaluationTaskSent(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetEvaluationCount4Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getEvaluationCount(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetInternalEvaluationsList5Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getInternalEvaluationsList(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetExternalEvaluationsList6Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getExternalEvaluationsList(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicesaveEvaluationDetails8Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.saveEvaluationDetails(null, null, null, null, null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetProcurementAgencyId9Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getProcurementAgencyId(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProposalDetails10Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProposalDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProposalComments11Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProposalComments(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetEvaluationScores12Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getEvaluationScores(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationResultsSelections13Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationResultsSelections(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationResultsCount14Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationResultsCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchFinalizeResultsVisibiltyStatus15Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchFinalizeResultsVisibiltyStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchUpdateResultsVisibiltyStatus16Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchUpdateResultsVisibiltyStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchInternalEvaluatorUsers18Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchInternalEvaluatorUsers(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchExternalEvaluatorUsers19Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchExternalEvaluatorUsers(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchReqProposalDetails20Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchReqProposalDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchAwardStatusId21Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchAwardStatusId(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateModifiedFlag22Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateModifiedFlag(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateSelectedProposalDetails23Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateSelectedProposalDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateNotSelectedProposalDetails24Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateNotSelectedProposalDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationDetails26Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchAccoComments28Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchAccoComments(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationScoresDetails30Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationScoresDetails(null, new HashMap(), null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationScoresDetails31Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationScoresDetails(null, "", null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluatorCommentsDetails32Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluatorCommentsDetails(null, new HashMap(), null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluatorCommentsDetails33Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluatorCommentsDetails(null, "", null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProcurementValue34Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProcurementValue(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchAwardAmount35Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchAwardAmount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicecountFinalizeProcurementDetails36Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.countFinalizeProcurementDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateProcurementStatus37Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateProcurementStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicemarkProposalNonResponsive38Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.markProposalNonResponsive(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateAwardReviewStatus39Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateAwardReviewStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateProposalStatus41Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateProposalStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchIntExtProposalDetails45Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchIntExtProposalDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceinsertEvaluationStatus46Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.insertEvaluationStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicedeleteEvaluationSettingData47Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.deleteEvaluationSettingData(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateProposalStatus48Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateProposalStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationCriteriaDetails49Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationCriteriaDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetDBDDocsList51Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getDBDDocsList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicedownloadDBDDocumentsAndZip52Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.downloadDBDDocumentsAndZip(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProviderNameList53Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProviderNameList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchExtAndIntEvaluator55Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchExtAndIntEvaluator(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationScoreDetails57Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationScoreDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetCloseButtonVisibiltyStatus58Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getCloseButtonVisibiltyStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetDownloadDBDDocsVisibiltyStatus59Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getDownloadDBDDocsVisibiltyStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetCancelEvalTaskVisibiltyStatus60Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getCancelEvalTaskVisibiltyStatus(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetSendEvaluationTasksVisibiltyStatus61Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getSendEvaluationTasksVisibiltyStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicegetTotalEvaluationData62Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.getTotalEvaluationData(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicesaveEvaluationScoreDetails63Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.saveEvaluationScoreDetails(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchNoOfProviders64Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchNoOfProviders(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchNoOfProposals65Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchNoOfProposals(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProposalCount67Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProposalCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceconfirmReturnForAction68Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.confirmReturnForAction(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicemodifyProposalStatus69Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.modifyProposalStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateEvaluationStatus71Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateEvaluationStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchProcurementDetailsForAwardWF72Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchProcurementDetailsForAwardWF(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluatorDetails73Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluatorDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateEvaluationReviewsStatus74Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateEvaluationReviewsStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefinishEvaluationReviewsStatus75Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.finishEvaluationReviewsStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServiceupdateEvaluationResult76Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.updateEvaluationResult(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchEvaluationResultsScores77Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchEvaluationResultsScores(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchSelectionCommentsForAwardTask78Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchSelectionCommentsForAwardTask(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testEvaluationServicefetchAwardAppDate79Negative() throws ApplicationException
	{
		EvaluationService loEvaluationService = new EvaluationService();
		loEvaluationService.fetchAwardAppDate(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsBudgetServicefetchBudgetListSummary0Negative() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		loFinancialsBudgetService.fetchBudgetListSummary(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsBudgetServicegetBudgetListCount1Negative() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		loFinancialsBudgetService.getBudgetListCount(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsBudgetServicegetModifyBudgetFeasibility2Negative() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		loFinancialsBudgetService.getModifyBudgetFeasibility(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsBudgetServicefetchRequestAdvance3Negative() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		loFinancialsBudgetService.fetchRequestAdvance(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsBudgetServicecancelModificationBudget4Negative() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		loFinancialsBudgetService.cancelModificationBudget(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsBudgetServiceterminateWorkflowForBudget5Negative() throws ApplicationException
	{
		FinancialsBudgetService loFinancialsBudgetService = new FinancialsBudgetService();
		loFinancialsBudgetService.terminateWorkflowForBudget(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsInvoiceListServicefetchInvoiceListSummary0Negative() throws ApplicationException
	{
		FinancialsInvoiceListService loFinancialsInvoiceListService = new FinancialsInvoiceListService();
		loFinancialsInvoiceListService.fetchInvoiceListSummary(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsInvoiceListServicegetInvoiceCount1Negative() throws ApplicationException
	{
		FinancialsInvoiceListService loFinancialsInvoiceListService = new FinancialsInvoiceListService();
		loFinancialsInvoiceListService.getInvoiceCount(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsInvoiceListServicewithdrawInvoiceList2Negative() throws ApplicationException
	{
		FinancialsInvoiceListService loFinancialsInvoiceListService = new FinancialsInvoiceListService();
		loFinancialsInvoiceListService.withdrawInvoiceList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsInvoiceListServicedeleteInvoiceList3Negative() throws ApplicationException
	{
		FinancialsInvoiceListService loFinancialsInvoiceListService = new FinancialsInvoiceListService();
		loFinancialsInvoiceListService.deleteInvoiceList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsInvoiceListServiceselectWithdrawInvoiceWorkFlowDetails5Negative()
			throws ApplicationException
	{
		FinancialsInvoiceListService loFinancialsInvoiceListService = new FinancialsInvoiceListService();
		loFinancialsInvoiceListService.selectWithdrawInvoiceWorkFlowDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicefetchContractListSummary0Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.fetchContractListSummary(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicefetchAgencyNames1Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.fetchAgencyNames(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicegetContractsCount2Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.getContractsCount(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicegetContractsValue3Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.getContractsValue(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServiceselectContractAmendmentId4Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.selectContractAmendmentId(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServiceupdateContractAmendmentStatus5Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.updateContractAmendmentStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServiceupdateAmenBudgetStatus6Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.updateAmenBudgetStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicefindContractDetailsByEPIN7Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.findContractDetailsByEPIN(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicefindContractDetailsByEPINforNew8Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.findContractDetailsByEPINforNew(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServiceaddNewContractDetails9Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.addNewContractDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicevalidateCloseContract10Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.validateCloseContract(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicecloseContract11Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.closeContract(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicecancelContract12Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.cancelContract(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicevalidateRenewContractDetails13Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.validateRenewContractDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicevalidateProvider14Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.validateProvider(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicerenewContractDetails15Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.renewContractDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicesuspendContract16Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.suspendContract(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServiceupdateConfigurationErrorCheckRule17Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.updateConfigurationErrorCheckRule(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicegetFinancialWFProperty18Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.getFinancialWFProperty(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicegetNextSeqFromTable19Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.getNextSeqFromTable(null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServiceunSuspendContract20Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.unSuspendContract(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicesuspendContractRelatedWorkflow21Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.suspendContractRelatedWorkflow(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServiceunsuspendContractRelatedWorkflow22Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.unsuspendContractRelatedWorkflow(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicecheckStatusIdForSuspended23Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.checkStatusIdForSuspended(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicecheckStatusIdForUnSuspended24Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.checkStatusIdForUnSuspended(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicerenewalRecordExist25Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.renewalRecordExist(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicevalidateAmendContract26Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.validateAmendContract(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServiceamendContractDetails28Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.amendContractDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicecancelContractErrorCheckRule29Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.cancelContractErrorCheckRule(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServiceterminateCancelContractWorkFlows30Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.terminateCancelContractWorkFlows(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testFinancialsListServicenewFYConfigErrorCheckRule31Negative() throws ApplicationException
	{
		FinancialsListService loFinancialsListService = new FinancialsListService();
		loFinancialsListService.newFYConfigErrorCheckRule(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHhsAuditServicehhsauditInsert0Negative() throws ApplicationException
	{
		HhsAuditService loHhsAuditService = new HhsAuditService();
		loHhsAuditService.hhsauditInsert(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHhsAuditServicehhsMultiAuditInsert1Negative() throws ApplicationException
	{
		HhsAuditService loHhsAuditService = new HhsAuditService();
		loHhsAuditService.hhsMultiAuditInsert(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHhsAuditServicedeleteFromUserComment2Negative() throws ApplicationException
	{
		HhsAuditService loHhsAuditService = new HhsAuditService();
		loHhsAuditService.deleteFromUserComment(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testHhsAuditServicecopyCommentHistory3Negative() throws ApplicationException
	{
		HhsAuditService loHhsAuditService = new HhsAuditService();
		loHhsAuditService.copyCommentHistory(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchOperationAndSupportDetails0Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchOperationAndSupportDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicevalidateOpSupportInvoiceAmount1Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.validateOpSupportInvoiceAmount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditOperationAndSupportDetails2Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editOperationAndSupportDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchEquipmentDetails3Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchEquipmentDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicevalidateEquipmentInvoiceAmount4Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.validateEquipmentInvoiceAmount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditEquipmentDetails5Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editEquipmentDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceStatus6Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceTotalForOTPS7Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceTotalForOTPS(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchYTDInvoiced8Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchYTDInvoiced(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceUnallocatedFunds9Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceUnallocatedFunds(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchProgramIncomeInvoice10Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchProgramIncomeInvoice(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceupdateProgramIncomeInvoice11Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.updateProgramIncomeInvoice(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoicingUtilities12Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoicingUtilities(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceupdateInvoicingUtilities13Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.updateInvoicingUtilities(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceIndirectRate14Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceIndirectRate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceupdateInvoicingIndirectRate15Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.updateInvoicingIndirectRate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchMilestoneInvoice16Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchMilestoneInvoice(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceupdateMilestoneInvoice17Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.updateMilestoneInvoice(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceSummary18Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchProfServicesDetails19Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchProfServicesDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditProfServicesDetails20Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editProfServicesDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractInvoiceRent21Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractInvoiceRent(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceupdateContractInvoiceRent22Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.updateContractInvoiceRent(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractInvoiceFyBudgetSummary23Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractInvoiceFyBudgetSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractInvoiceSummary24Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractInvoiceSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractInvoiceInformation25Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractInvoiceInformation(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceupdateInvoiceStatus26Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.updateInvoiceStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchCurrInvoiceStatus27Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchCurrInvoiceStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicegetAgencyIdByContractForWF28Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.getAgencyIdByContractForWF(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditEmployeeInvoice29Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editEmployeeInvoice(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditFringeBenefits30Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editFringeBenefits(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchSalariedEmployeeBudget31Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchSalariedEmployeeBudget(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchHourlyEmployeeBudget32Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchHourlyEmployeeBudget(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchSeasonalEmployeeBudget33Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchSeasonalEmployeeBudget(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchFringeBenefits34Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchFringeBenefits(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceAssignmentSummary35Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceAssignmentSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditInvoiceAssignmentSummary36Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editInvoiceAssignmentSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceRateGrid37Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceRateGrid(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditContractedServicesInvoicing39Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editContractedServicesInvoicing(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractedServicesInvoicingConsultants40Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractedServicesInvoicingConsultants(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractedServicesInvoicingSubContractors41Negative()
			throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractedServicesInvoicingSubContractors(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchContractedServicesInvoicingVendors42Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchContractedServicesInvoicingVendors(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceerrorCheckInvoiceReviewTask43Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.errorCheckInvoiceReviewTask(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicesetStatusForInvoiceReviewTask44Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.setStatusForInvoiceReviewTask(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchCurrentAssignmentStatus45Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchCurrentAssignmentStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicesaveAgencyInvoiceNumber46Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.saveAgencyInvoiceNumber(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceInfo47Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceInfo(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchVendorList48Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchVendorList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicevalidateAssignee49Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.validateAssignee(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceaddAssigneeForBudget50Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.addAssigneeForBudget(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicegetNextSeqFromInvoiceTable51Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.getNextSeqFromInvoiceTable(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchSubBudgetSummary52Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchSubBudgetSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicegetCbGridDataForSession53Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.getCbGridDataForSession(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceupdateInvoiceDetails54Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.updateInvoiceDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicedelContractInvoiceAssignment55Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.delContractInvoiceAssignment(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicevalidateInvoiceAdvanceStatus56Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.validateInvoiceAdvanceStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServiceeditInvoiceAdvanceDetails57Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.editInvoiceAdvanceDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testInvoiceServicefetchInvoiceAdvanceDetails58Negative() throws ApplicationException
	{
		InvoiceService loInvoiceService = new InvoiceService();
		loInvoiceService.fetchInvoiceAdvanceDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentListServicefetchPaymentListSummary0Negative() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		loPaymentListService.fetchPaymentListSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentListServicegetPaymentCount1Negative() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		loPaymentListService.getPaymentCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentListServicegetFiscalInformation2Negative() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		loPaymentListService.getFiscalInformation(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentListServicegetSatusList3Negative() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		loPaymentListService.getSatusList(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentListServicegetAgencyList4Negative() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		loPaymentListService.getAgencyList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testPaymentListServicegetProgramName5Negative() throws ApplicationException
	{
		PaymentListService loPaymentListService = new PaymentListService();
		loPaymentListService.getProgramName(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchActiveProcurements0Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchActiveProcurements(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicegetProcurementCount1Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.getProcurementCount(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicegetProgramName2Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.getProgramName(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServiceinsertUpdateServiceAcceleratorService3Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.insertUpdateServiceAcceleratorService(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchEpinDetails4Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchEpinDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicegetAcceleratorContactDetails5Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.getAcceleratorContactDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicesaveProcurementSummary6Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.saveProcurementSummary(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicegetStatusId7Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.getStatusId(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicegetProcurementSummary8Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.getProcurementSummary(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchRfpReleaseDocsDetails9Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchRfpReleaseDocsDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicegetProcurementId10Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.getProcurementId(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicedisplayApprovedProvidersList11Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.displayApprovedProvidersList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicecheckForEvidenceFlag13Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.checkForEvidenceFlag(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServiceupdateProcurementDataOnPublish14Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.updateProcurementDataOnPublish(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServiceupdateProcurementServiceData15Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.updateProcurementServiceData(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicecheckIfUserOfSameAgency16Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.checkIfUserOfSameAgency(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicecheckIfUserOfSameAgency17Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.checkIfUserOfSameAgency(null, null, null, true);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicecheckIfAwardApproved18Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.checkIfAwardApproved(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicegetProcurementDetailsForNav19Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.getProcurementDetailsForNav(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServiceinsertRfpDocumentDetails20Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.insertRfpDocumentDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServiceremoveRfpDocs21Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.removeRfpDocs(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchProcurementCustomQuestionAnswer22Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchProcurementCustomQuestionAnswer(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchSelectedServices23Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchSelectedServices(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchServicesList24Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchServicesList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchApprovedProvDetails25Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchApprovedProvDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchApprovedProvidersList26Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchApprovedProvidersList(null, null, null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchApprovedProvidersListAfterRelease27Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchApprovedProvidersListAfterRelease(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicegetOrganizationDetail28Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.getOrganizationDetail(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchDropDownValue29Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchDropDownValue(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicesaveApprovedProvDetails30Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.saveApprovedProvDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchProcurementCoNDetails31Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchProcurementCoNDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicegetProviderStatus32Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.getProviderStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicedeleteProvidersData33Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.deleteProvidersData(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicepreserveOldStatus34Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.preserveOldStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicegetProgramNameForAgencyId35Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.getProgramNameForAgencyId(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicegetSavedServicesList36Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.getSavedServicesList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServiceupdateProcurementStatus37Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.updateProcurementStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchDocumentIdList38Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchDocumentIdList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicegetProcurementSummaryForNav39Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.getProcurementSummaryForNav(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicegetProcurementTitle40Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.getProcurementTitle(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchApprovedProvidersForNotification41Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchApprovedProvidersForNotification(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchProposalCustomQuestions42Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchProposalCustomQuestions(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchProposalDocumentType43Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchProposalDocumentType(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicesaveProposalCustomQuestions44Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.saveProposalCustomQuestions(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicesaveProposalDocumentType45Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.saveProposalDocumentType(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServiceupdateProcurementDataWithRelease46Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.updateProcurementDataWithRelease(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchRfpReleaseDocIdsList47Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchRfpReleaseDocIdsList(null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServiceconsolidateAllDocsProperties48Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.consolidateAllDocsProperties(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchProcTitleAndOrgList49Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchProcTitleAndOrgList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchDocumentIdsList50Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchDocumentIdsList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicefetchProcurementAddendumData51Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.fetchProcurementAddendumData(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProcurementServicecloseProcurement52Negative() throws ApplicationException
	{
		ProcurementService loProcurementService = new ProcurementService();
		loProcurementService.closeProcurement(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchProposalSiteDetails0Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchProposalSiteDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicesaveProposalDetails1Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.saveProposalDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchMemberDetails2Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchMemberDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchAllOrganizationMembers3Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchAllOrganizationMembers(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicecheckAllRequiredFieldsCompleted4Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.checkAllRequiredFieldsCompleted(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicecheckForDueDate5Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.checkForDueDate(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicesubmitProposal6Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.submitProposal(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicegetProposalSummary7Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.getProposalSummary(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceretractProposal8Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.retractProposal(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicecancelProposal9Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.cancelProposal(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicecheckProposalEdit10Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.checkProposalEdit(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicecheckProposalCancel11Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.checkProposalCancel(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicegetDocumentIdList12Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.getDocumentIdList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchProposalTitle13Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchProposalTitle(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchProposalDetails14Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchProposalDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceshowProposalDetailsReadonly15Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.showProposalDetailsReadonly(null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchPermittedUsers16Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchPermittedUsers(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchProposalDocuments17Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchProposalDocuments(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceupdateProposalDocumentAndDetailStatus18Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.updateProposalDocumentAndDetailStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchRequiredOptionalDocuments19Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchRequiredOptionalDocuments(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceinsertNewProposalDetails20Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.insertNewProposalDetails(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicegetProposalDocumentList22Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.getProposalDocumentList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceinsertProposalDocumentDetails23Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.insertProposalDocumentDetails(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceremoveProposalDocs24Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.removeProposalDocs(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchProposalDetailsForTask25Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchProposalDetailsForTask(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicegetProposalSiteDetails26Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.getProposalSiteDetails(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicegetProposalDocuments27Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.getProposalDocuments(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchProcurementTitle28Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchProcurementTitle(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchRFPDocListForTask29Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchRFPDocListForTask(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceupdateProposalDocumentStatusForTask30Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.updateProposalDocumentStatusForTask(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicefetchProcTitleAndOrgId31Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.fetchProcTitleAndOrgId(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceaddProposalAndProcurementStatus32Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.addProposalAndProcurementStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceupdateApprovedProviderStatus33Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.updateApprovedProviderStatus(null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceupdateProposalDocumentProperties34Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.updateProposalDocumentProperties(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServicegetOrgIdsForSelectedProposals35Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.getOrgIdsForSelectedProposals(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceupdateProposalStatusFromTask36Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.updateProposalStatusFromTask(null, null, null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testProposalServiceupdateProposalPreviousStatus37Negative() throws ApplicationException
	{
		ProposalService loProposalService = new ProposalService();
		loProposalService.updateProposalPreviousStatus(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServicevalidateEPIN0Negative() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.validateEPIN(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServicevalidateRfpPreRequisites1Negative() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.validateRfpPreRequisites(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServicecheckCofApproval2Negative() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.checkCofApproval(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServiceinsertAppProviderList3Negative() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.insertAppProviderList(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServicefetchEvaluationCriteria4Negative() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.fetchEvaluationCriteria(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServiceupdateProcurementData5Negative() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.updateProcurementData(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServiceupdateRfpDocument6Negative() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.updateRfpDocument(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServiceupdateProcDocumentConfig7Negative() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.updateProcDocumentConfig(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServiceupdateProcQuestionConfig8Negative() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.updateProcQuestionConfig(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServiceupdateEvaluationCriteria9Negative() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.updateEvaluationCriteria(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServicedeleteAddendumData10Negative() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.deleteAddendumData(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServicesaveEvaluationCriteria12Negative() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.saveEvaluationCriteria(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServicegetProcurementStatus13Negative() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.getProcurementStatus(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testRFPReleaseServiceupdateRfpDocumentStatus14Negative() throws ApplicationException
	{
		RFPReleaseService loRFPReleaseService = new RFPReleaseService();
		loRFPReleaseService.updateRfpDocumentStatus(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicefetchProcurementCountForAccHomePage0Negative()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.fetchProcurementCountForAccHomePage(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicefetchProcurementCountForProvHomePage1Negative()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.fetchProcurementCountForProvHomePage(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicefetchAccFinancialsPortletCount2Negative()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.fetchAccFinancialsPortletCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicefetchProviderFinancialCount3Negative()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.fetchProviderFinancialCount(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicegetMasterStatus4Negative() throws ApplicationException
	{
		new SolicitationFinancialsGeneralService().getMasterStatus(null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicegetProcurementChangeControlWidget5Negative()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.getProcurementChangeControlWidget(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServiceauthenticateLoginUser6Negative() throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.authenticateLoginUser(null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicefetchEpinList7Negative() throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.fetchEpinList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicefetchContractNoList8Negative() throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.fetchContractNoList(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServiceupdateLastModifiedDetails9Negative()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.updateLastModifiedDetails(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicegetProviderWidgetDetils10Negative() throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.getProviderWidgetDetils(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicecheckDocumentExistsInAnyTable11Negative()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.checkDocumentExistsInAnyTable(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testSolicitationFinancialsGeneralServicecheckDocumentsExistsInAnyTable12Negative()
			throws ApplicationException
	{
		SolicitationFinancialsGeneralService loSolicitationFinancialsGeneralService = new SolicitationFinancialsGeneralService();
		loSolicitationFinancialsGeneralService.checkDocumentsExistsInAnyTable(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicereassignTask0Negative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.reassignTask(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicelaunchFinancialWorkflow1Negative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.launchFinancialWorkflow(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServiceterminateWorkflow2Negative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.terminateWorkflow(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicecloseAllOpenTask3Negative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.closeAllOpenTask(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicesetPropertyInWF4Negative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.setPropertyInWF(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefinishTask5Negative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.finishTask(null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchTaskDetails6Negative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchTaskDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchAgencyTaskHistory7Negative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchAgencyTaskHistory(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchProviderTaskHistory8Negative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchProviderTaskHistory(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchAgencyDetails9Negative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchAgencyDetails(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchLastComment10Negative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchLastComment(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchUserLastComment11Negative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchUserLastComment(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchAgencyTaskHistory12Negative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchAgencyTaskHistory(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaskServicefetchAcceleratorTaskHistory13Negative() throws ApplicationException
	{
		TaskService loTaskService = new TaskService();
		loTaskService.fetchAcceleratorTaskHistory(null, null, null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaxonomyTaggingServicegetProcurementProposalDetails0Negative() throws ApplicationException
	{
		TaxonomyTaggingService loTaxonomyTaggingService = new TaxonomyTaggingService();
		loTaxonomyTaggingService.getProcurementProposalDetails(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaxonomyTaggingServicegetTaxonomyTaggingList1Negative() throws ApplicationException
	{
		TaxonomyTaggingService loTaxonomyTaggingService = new TaxonomyTaggingService();
		loTaxonomyTaggingService.getTaxonomyTaggingList(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaxonomyTaggingServicedeleteTaxonomyTags4Negative() throws ApplicationException
	{
		TaxonomyTaggingService loTaxonomyTaggingService = new TaxonomyTaggingService();
		loTaxonomyTaggingService.deleteTaxonomyTags(null, null);
	}

	@Test(expected = ApplicationException.class)
	public void testTaxonomyTaggingServiceselectProcurementRecordCount5Negative() throws ApplicationException
	{
		TaxonomyTaggingService loTaxonomyTaggingService = new TaxonomyTaggingService();
		loTaxonomyTaggingService.selectProcurementRecordCount(null, null);
	}
}
