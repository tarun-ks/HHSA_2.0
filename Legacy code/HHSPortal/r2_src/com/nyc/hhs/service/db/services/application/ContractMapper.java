package com.nyc.hhs.service.db.services.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.batch.bulkupload.BulkUploadContractInfo;
import com.batch.bulkupload.BulkUploadFileInformation;
import com.nyc.hhs.model.BaseFilter;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.ContractFinancialBean;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.model.FinancialWFBean;
import com.nyc.hhs.model.PDFBatch;
import com.nyc.hhs.model.PaymentSortAndFilter;
import com.nyc.hhs.model.UnallocatedFunds;

/**
 * This is a mapper class which is used to execute queries and fetch data for
 * Contract
 */
public interface ContractMapper
{
	public ArrayList<ContractList> fetchContractListProvider(ContractList aoContractFilter);

	public ArrayList<ContractList> fetchContractListAgency(ContractList aoContractFilter);

	public ArrayList<ContractList> fetchContractListAccelerator(ContractList aoContractFilter);
	
	//public ArrayList<ContractList> requestPartialMergeContractList(ContractList aoContractFilter);

	public List<String> fetchAmendmentListProvider(ContractList aoContractFilter);

	// Release 3.8.0 Enhancement 6481
	public List<String> fetchContractUpdateForApprovedActiveBudget(ContractList aoContractFilter);

	// Release 5 Contract Restriction
	public List<String> fetchContractForRestriction(BaseFilter aoBaseFilter);

	public Integer fetchContractForRestrictionCount(HashMap aoHashMap);

	public Integer fetchContractForRestrictionCountInvoice(HashMap aoHashMap);

	public List<Document> fetchDocumentListRestrictedContracts(HashMap aoHashMap);

	public List<Document> fetchDocumentListProcurementUser(HashMap aoHashMap);

	public String getPermissionType(HashMap aoHashMap);

	// Release 5 Contract Restriction

	public List<HashMap<String, String>> fetchAgencyNames();

	public List<String> fetchAmendmentListAgency(ContractList aoContractFilter);

	public List<String> fetchAmendmentListAccelearator(ContractList aoContractFilter);

	public String fetchContractAmountProvider(ContractList aoContractFilter);

	public int fetchContractCountProvider(ContractList aoContractFilter);

	public int fetchContractCountAgency(ContractList aoContractFilter);

	public int fetchContractCountAccelerator(ContractList aoContractFilter);

	public int updateAmendContractInfo(ContractList aoAmendContractBean);

	public boolean validateContract(String asContractId);

	public int closeContract(HashMap asHMReqdProp);
	
	//Start R7: defect 8644 part3 for partial merging
	public int updateRequestPartialMergeContractList(String asContractId);
	public List<ContractList> getPartialMergeRequestList(HashMap asHMReqdProp);
	
	//Start R7: defect 8644 part3 for partial merging

	public int closeBudgetContract(HashMap asHMReqdProp);

	public Integer updateContractStatus(HashMap loHMReqProp);

	public boolean updateUnallocatedFunds(UnallocatedFunds aoUnallocatedFunds);

	public boolean insertUnallocatedFunds(UnallocatedFunds aoUnallocatedFunds);

	public List<UnallocatedFunds> fetchUnallocatedFunds(String asSubBudgetId);

	public List<String> fetchAllContractId(String asContractId);
	

	public List<String> fetchAllContractIdForUpdateCheck(String asContractId);

	public List<String> getAllContractIds(HashMap aoHMReqdProp);

	//Changed parameter in Release 6: APT 
	public EPinDetailBean findContractDetailsByEPIN(EPinDetailBean ePinDetailBean);
	//Changed parameter in Release 6: APT end
	public EPinDetailBean findContractDetailsByEPINforNew(EPinDetailBean aoEPinDetailBean);

	public Integer validateRenewContractDetails(String asEPin);

	public Integer validateProviderInAccelerator(String asVendorFmsId);

	public Integer addNewContract(EPinDetailBean aoContractDetailByEpin);

	public Integer renewContractDetails(EPinDetailBean aoContractDetailByEpin);

	public String fetchContractConfTaskStatus(HashMap aoHMReqdProp);

	public String fetchContractDisbursedAmount(HashMap aoHMReqdProp);

	public String fetchContractBudgetStatus(HashMap aoHMReqdProp);

	public List<String> fetchContractInvoiceStatus(HashMap aoHMReqdProp);

	public List<String> fetchContractPaymentStatus(HashMap aoHMReqdProp);

	public String getContractAmendmentAmmount(HashMap aoHMReqdProp);

	public String fetchContractSource(String asContractId);

	public Integer insertContractConfiguration(HashMap aoContractConfigDetail);

	public EPinDetailBean findContractDetailsByContract(String asContractId);

	public FinancialWFBean findContractDetailsByContractForWF(String asContractId);

	public FinancialWFBean findProcurementDetailsForWF(HashMap aoHMReqdMap);

	public Integer getContractSeqFromTable();

	public Integer renewalRecordExist(HashMap aoContractStatus);

	public List<String> renewalRecordExistForContractDropDown(HashMap aoContractStatus);

	public int getBudgetNotApprovedCount(HashMap asHMReqdProp);

	public int getNotApprovedInvoiceCount(HashMap asHMReqdProp);

	public int getNotDisbursedPaymentCount(HashMap asHMReqdProp);

	Integer updateContractSuspend(HashMap<String, String> aoContractInfo);

	Integer updateBudgetSuspend(HashMap<String, String> aoContractInfo);

	Integer updateInvoiceSuspend(HashMap<String, String> aoContractInfo);

	Integer updatePaymentSuspend(HashMap<String, String> aoContractInfo);

	Integer updateContractUnsuspend(HashMap<String, String> aoContractInfo);

	// audit entries
	List<PaymentSortAndFilter> selectBudgetSuspend(HashMap<String, String> aoContractInfo);

	List<PaymentSortAndFilter> selectInvoiceSuspend(HashMap<String, String> aoContractInfo);

	List<PaymentSortAndFilter> selectPaymentSuspend(HashMap<String, String> aoContractInfo);

	Integer updateBudgetUnsuspend(HashMap<String, String> aoContractInfo);

	Integer updateInvoiceUnsuspend(HashMap<String, String> aoContractInfo);

	Integer updatePaymentUnsuspend(HashMap<String, String> aoContractInfo);

	Integer updateContractReason(HashMap<String, String> aoContractInfo);

	// audit entries
	List<PaymentSortAndFilter> selectBudgetUnsuspend(HashMap<String, String> aoContractInfo);

	List<PaymentSortAndFilter> selectInvoiceUnsuspend(HashMap<String, String> aoContractInfo);

	List<PaymentSortAndFilter> selectPaymentUnsuspend(HashMap<String, String> aoContractInfo);

	Integer checkStatusForSusOrUnSus(String asContractId);

	// Start Release 3.8.0 Enhancement 6481
	Integer getPendingRegContractApprovedBudgetCount(String asContractId);

	String fetchContractForUpdateTask(String asContractId);

	// End Release 3.8.0 Enhancement 6481

	public EPinDetailBean getDataWFbyContractId(String asContractId);

	public Integer validateAmendContract(HashMap aoReqMap);

	public Map selectContractAmendmentId(ContractList aoContractListBean);

	public Integer updateContractAmendStatus(ContractList aoContractListBean);

	public Integer updateAmenBudgetStatus(ContractList aoContractListBean);

	public List<PaymentSortAndFilter> selectAmenBudgetStatus(ContractList aoContractListBean);

	public Integer amendContractDetails(EPinDetailBean aoContractDetailByEpin);

	public Map fetchCancelContractDetails(String asContractId);

	public Integer updateContractBudgetStatus(HashMap loHMReqProp);

	public List<PaymentSortAndFilter> selectContractBudgetStatus(HashMap loHMReqProp);

	public int isFYBudgetEntry(Map<String, Object> loQueryMap);

	public String fetchWorkFlowStatus(Map<String, Object> loQueryMap);

	public Integer fetchBudgetCount(HashMap loHMReqProp);

	public List<ContractBean> fetchContractsForBatchProcess(HashMap aoHMArgs);

	public List<ContractBean> getUpdateTypeContractRecord(HashMap aoHMArgs);

	public List<ContractBean> fetchAmendmentContractsForBatchProcess(HashMap aoHMArgs);

	public List<ContractBudgetBean> getApprovedBudgetsOfContract(HashMap aoHMArgs);

	public Integer batchUpdateBudgetModificationAndUpdateStatus(ContractBean aoContractBean);

	public Integer setContractStatusAsRegistered(ContractBean aoContractBean);

	public Integer resetDiscrepancyStatus(ContractBean aoContractBean);

	public Integer removeContractDiscrepancies(ContractBean aoContractBean);

	public Integer mergeAmendmentDatesInBaseContract(ContractBean aoContractBean);

	public Integer removeContractDiscrepancyAndMarkRegistered(ContractBean aoContractBean);

	public List<ContractBudgetBean> fetchContractBudgets(HashMap aoHMArgs);

	public Integer deleteFiscalYear(HashMap aoHMArgs);

	public List<ContractFinancialBean> fetchedContractFinancialDetails(HashMap aoHMArgs);

	public Integer markApprovedBudgetsAsActive(HashMap aoHMArgs);

	public Integer updateBudgetsExtCtNumber(HashMap aoHMArgs);

	public List<String> getContractL2Providers(HashMap aoHMArgs);

	public List<String> getContractAgencyUsers(HashMap aoHMArgs);

	public EPinDetailBean fetchBaseContractDetailsForUpdate(HashMap aoHMArgs);

	public Integer addNewContractForUpdate(EPinDetailBean aoEPinDetailBean);

	public void insertFetchedContractFinancialDetails(ContractFinancialBean aoContractFinancialBean);

	public List<ContractList> fetchBaseAmendmentContractDetails(String asContractId);

	public List<ContractList> fetchBaseAmendmentContractDetailsAmendmentList(HashMap loFetchBaseAmendMap);

	public String renewContractDateValidation(String aoContractId);

	public EPinDetailBean findContractDetailsByContractForAmend(String asContractId);

	public List<BulkUploadFileInformation> getBulkUploadFileInfo(HashMap aoFileStatus);

	public int insertBulkUploadStatusByRecord(BulkUploadContractInfo aoBulkUploadContractInfo);

	public Integer updateBulkUploadDocStatus(HashMap aoDocInfo);

	public Integer updateBulkUploadDocLockStatus(HashMap aoDocInfo);

	public Integer bulkUploadSystemFailure(HashMap aoDocInfo);

	public FinancialWFBean findProcEpinR3ContractForWF(String asContractId);

	public int fetchNewFYTaskDaysValue();

	public String fetchBaseAwardEpin(String asContractId);

	public String fetchContractBudgetUpdateStatus(HashMap aoHMReqdProp);

	public Integer fetchContractBudgetAmendmentStatus(HashMap aoHMReqdProp);

	public Integer fetchLastFYConfigured(String asContractId);

	public Map fetchContractInfo(String asContractId);

	public Integer revertContractToBaseValue(HashMap loContractMergeHashMap);

	public Map fetchUpdateContractId(String asContractId);

	public int deleteModificationBudget(Map<String, String> aoHashmap);

	public int deleteUpdateBudget(Map<String, String> aoHashmap);

	public int deleteUddateContract(Map<String, String> aoHashmap);

	public int deleteBaseBudget(Map<String, String> aoHashmap);

	public int deleteBaseContract(Map<String, String> aoHashmap);

	public ArrayList<ContractList> fetchAmendmentListScreenAccelerator(ContractList aoContractFilter);

	public ArrayList<ContractList> fetchAmendmentListScreenAgency(ContractList aoContractFilter);

	public ArrayList<ContractList> fetchAmendmentListScreenProvider(ContractList aoContractFilter);

	public int fetchAmendmentCountAccelerator(ContractList aoContractFilter);

	public int fetchAmendmentCountProvider(ContractList aoContractFilter);

	public int fetchAmendmentCountAgency(ContractList aoContractFilter);

	public String fetchAllNegativeAmendmentAmounts(String asContractId);

	public Integer updateBudgetCount(String asContractId);

	public Integer negativeAmendment(String asContractId);

	public Integer insertBulkUploadDocumentProperties(HashMap loDocProperties);

	Integer updateContractStartEndDateForOpenEndedRfp(HashMap loContractDetails);

	Integer isOpenEndedRfpStartEndDateNotSet(String asContractId);

	Integer sentForRegOrCanCheck(ContractList aoContractFilterBean);

	Integer isStatusSentForReg(ContractList aoContractFilterBean);

	Integer negAmendRegOrCanCheck(ContractList aoContractFilterBean);

	Integer updateStatusToSentForReg(ContractList aoContractFilterBean);

	String getBudgetIdsFromContractId(ContractList aoContractFilterBean);

	Integer budgetApprovedCount(HashMap loHashMap);

	Integer negAmendCount(HashMap loHashMap);

	Integer isPDFAmendmentCofGenerated(HashMap loHashMap);

	Integer isPDFBudgetAmendmentGenerated(HashMap loHashMap);

	// R4 Change: Queries added for merging amendment with base in Budget
	// Customization
	public List<String> getAmendAffectedBudgetIds(ContractBean aoContractBean);

	public List<String> fetchEntryTypeForBaseAmendMerge(ContractBean aoContractBean);

	public void mergeBaseBudCustomization(HashMap<String, String> aoHashMap);

	public Integer updateStatusForPdfAfterUpload(HashMap aoParamMap);

	public Integer updateStatusForPdf(HashMap aoParamMap);

	public List<PDFBatch> fetchEntityIdForPdf(HashMap aoParamMap);

	public List<String> fetchEntityIdForPdfR2(HashMap aoParamMap);

	public String fetchApprovedBaseBudgetId(String asContractId);

	public String fetchAmendAffectedBaseBudgetIds(String aoContractId);

	public Integer getTotalContracts(String asBulkUploadId);

	public List<BulkUploadContractInfo> getErrorList(String asBulkUploadId);

	public String getBaseContractId(String asContractId);

	public void updatePdfStatusNotStarted(HashMap aoHashMap);

	// Release 3.1.0 for defect fix 6398
	public void insertFetchedContractFinancialDetailsBatch(ContractFinancialBean aoContractFinancialBean);

	public List<ContractFinancialBean> fetchedContractFinancialDetailsBatch(HashMap aoHMArgs);

	// Start || Added For Enhancement 6000 for Release 3.8.0
	public Integer fetchApproveActiveBudget(String asContractId);

	public String fetchParentContractIdList(String asContractId);

	public Integer deletePersonnelService(String asContractId);

	public Integer deleteOpSupportOthers(String asContractId);

	public Integer deleteOpSupport(String asContractId);

	public Integer deleteUtilities(String asContractId);

	public Integer deleteProffServiceOthers(String asContractId);

	public Integer deleteProffService(String asContractId);

	public Integer deleteRent(String asContractId);

	public Integer deleteContractedService(String asContractId);

	public Integer deleteRate(String asContractId);

	public Integer deleteMilestone(String asContractId);

	public Integer deleteUnallocated(String asContractId);

	public Integer deleteIndirectRate(String asContractId);

	public Integer deleteProgIncomeOthers(String asContractId);

	public Integer deleteProgramIncome(String asContractId);

	public Integer deleteFringeBenefit(String asContractId);

	public Integer deleteEquipment(String asContractId);

	public Integer deleteSubBudgetSite(String asContractId);

	public Integer deleteSubBudget(String asContractId);

	public Integer deleteBudgetCustomiz(String asContractId);

	public Integer deleteBudgetDoc(String asContractId);

	public Integer deleteAssociatedBudgets(String asContractId);

	public Integer deleteContractFinFunding(String asContractId);

	public Integer deleteContractFinancials(String asContractId);

	public Integer deleteContractDoc(String asContractId);

	public Integer deleteContract(String asContractId);

	public Map fetchContractTitleAndOrgID(String asContractId);

	public Integer fetchPendingBudget(String asContractId);

	public List<String> fetchBudgetsForContract(String asContractId);

	public List<String> fetchContractBudgetDocs(String asContractId);

	// End || Added For Enhancement 6000 for Release 3.8.0

	// Start || Added for Enhancement 6482 for Release 3.8.0
	//Changed result parameter for R6:Apt Interface
	public Map<String, String> findContractEpin(String contractId);
	//R6 changes end

	public String findAgencyName(String contractId);

	public int updateContractForTitleProgramName(HashMap asHMReqdProp);

	public int updateBudgetForTitleProgramName(HashMap asHMReqdProp);

	public List<ContractBudgetBean> mismatchContractFinancialsAndBudgetAmount(HashMap asHMReqdProp);

	// End || Added for Enhancement 6482 for Release 3.8.0

	public Integer chechContractIsDeleted(String asContractId);

	// Start Release 3.12.0 Enhancement 6601
	public Integer markAmendmentETLRegistredWhichAreRegisteredInFMS(HashMap asHMReqdProp);

	public List<ContractList> getAmendmentRegisterdInFms(HashMap asHMReqdProp);

	public Integer updateFlagAmendmentRegisteredInFMS(HashMap asHMReqdProp);

	public Integer resetFlagAmendmentRegisteredInFMS(HashMap asHMReqdProp);

	public List<ContractBudgetBean> fetchAmendmentContractBudgets(HashMap aoHMArgs);

	public List<ContractBudgetBean> fetchAmendmentContractBudgetsAlreadyMerged(HashMap aoHMArgs);

	public HashMap getContractInformation(HashMap aoHMArgs);

	public String getContractFinancialsInformation(HashMap aoHMArgs);

	public Integer cancelAmendmentCheckRegisteredInFms(HashMap aoHMArgs);

	// End Release 3.12.0 Enhancement 6601

	// release 3.14.0
	public Integer fetchLastFYConfiguredNextNewFy(String asContractId);

	public List<String> fetchAllContractIdForNewFYCheck(String asContractId);

	public Integer isNewFYCreatedWithMergedValuesWhenAmendmentIsRegInFMS(String asContractId);

	// start Added in R5
	public List<ContractBean> fetchUserAccessDetails(HashMap<String, Object> aoHMArgs);

	public int deleteContractRestriction(HashMap<String, Object> aoHMArgs);

	public int insertContractRestrictonDetails(HashMap<String, Object> aoHMArgs);

	public List<ContractBean> checkContractRestricted(HashMap<String, Object> aoHMArgs);

	public int fetchL2Count(ContractBean aoContractBean);

	public Integer updateContractForNegotiationSeleted(HashMap<String, String> aoHMArgs);

	public Integer updateContractAmountAfterNegotiation(HashMap<String, String> aoHMArgs);

	public String getParentContractIdForCancelAllAwards(String asEvaluationMappingId);

	public Integer fetchEnableDisableCancelAllCount(String asEvaluationMappingId);

	public Integer deleteDefaultAssignment(String asContractId);

	public Integer deleteContractRestrictions(String asContractId);

	public Integer ViewUserAccessDropdownFlag(BaseFilter aoBaseFilter);
	
	public Integer checkApprovalForFinance(String asEvalPoolMappingId);
	// end Added in R5
	// start Added in R6
	public Integer deletePersonnelServicesDetails(String asContractId);
	
	public Integer deleteFringeBenefitDetail(String asContractId);
	
	//Added for R6: Apt Interface
	public Integer setContractStatusAsRegisteredForCOF(ContractBean aoContractBean);
	
	public Integer setContractStatusAsRegisteredForAmendmentCOF(ContractBean aoContractBean);
	//R6: Epin validation method
	public Integer validateEpinIsUnique(EPinDetailBean aoEPinDetailBean);
	//End R6
	// Start Added in R7 Defect 8644 cancel and merge
	public Integer addYearToContractEndDate(String asContractId);
	
	//Start R7
	public Integer saveContractLevelMessage(Map aoHMArgs);
	
	public Integer saveContractLevelMessageHistory(Map aoHMArgs);
	
	public ContractList retrieveContractMessage(String contractId);
	
	public ContractList fetchUnflagOverlayDetails(String contractId);
	
	public Integer updateContractLevelMessage(String contractId);
	
	public ContractList fetchFlagOverlayDetail(String contractId);
	
	//Start: Added in R7 for Cost Center
	public Integer updateCostCenterEnabled(HashMap<String, String> aoHMArgs);
	
	public Integer updateCostCenterOptedForConCofReturn(HashMap<String, String> aoHMArgs);
	//End: Added in R7 for Cost Center
	//end : R7
	
	public Integer deletePersonnelServicesDetailForFY(String asBudgetId);
			
	public Integer deleteFringeBenefitDetailForFY(String asBudgetId);
	
	////Start: Added for R7 8644
	public Integer markAmendmentETLRegistredWithPartialMergeRequest(HashMap asHMReqdProp);
	//public Integer getAmendmentRegisterdInFmsWithPM(HashMap asHMReqdProp);
	// Start Added in R7 Defect 8644 cancel and merge
	public Integer addYearToContractFinancials(String asContractId);
	//End
	//Start:Added in R7 for Cost-Center 
	public Integer deleteServices(String asBudgetId);
	public Integer deleteCostCenter(String asBudgetId);
	public Integer deleteServicesConfig(String asBudgetId);
	//End:Added in R7 for Cost-Center
	
	//Start: Added for R7 defect 8705
	public Integer budgetEndDateUpdatedInAmendment(HashMap asHMReqdProp);
	public Integer budgetEndDateUpdatedInCBudget(HashMap asHMReqdProp);
	public String fetchFYAmendContractEndDate(HashMap asHMReqdProp);
	public String fetchFYBaseContractEndDate(HashMap asHMReqdProp);
	public String asRequestPartialMergeStatus(String contractId);
	public Integer baseContractBudgetStatus(String budgetId);
	//End: Added for R7 defect 8705
	//Start: Added for R7 defect 8644
	public String fetchRequestPartialMergeValue(String contractId);
	public HashMap fetchParentContractIdOfAmendmentCId(CBGridBean aoCBGridBean);
	public int checkContractTypeId(CBGridBean aoCBGridBean);
	//End: Added for R7 defect 8644
	//Start: Added in R7 for defect 8705.
	public List<HashMap<String, String>> fetchBudgetDetails(HashMap loHMArgs);
	//End
	//Start: Added in R7.11.0 QC9122
	public ArrayList<ContractList> fetchContractBudgetStatusId(ContractList aoContractFilter);
	//End: Added in R7.11.0 QC9122

    //Start: Added in R8.11.1
    public ArrayList<ContractList> fetchContractBudgetStatusId_N(Map <String, List <String>> loMap);
    //End: Added in R8.11.1
	
	
	//Start: Added in R7.12.0 QC9314
	public Integer setAmendmentStatusAsRegistered();
	public Integer setBudgetFromAmendStatusAsActive();
	//End: Added in R7.12.0 QC9314	
	
	//Start: QC9122 R 8.1
	public ArrayList<ContractList> fetchContractBudgetApprovedStatusId(ContractList aoContractFilter);
	public String fetchBaseContractStauts(String asContractId);
	public Integer fetchBaseContractStatusId(String asContractId);
	//End: QC9122 R 8.1
	//Start QC 9438 R 8.2
	public Integer getNegativeAmendmentCountForFY(HashMap<String, String> aoHMArgs);
	//End QC 9438 R 8.2
	
	// Start QC 9388 R 8.4
	public ContractBudgetBean fetchLastBaseApprovedFY(String asContractId);
	public ContractBean fetchBaseContractStartEndDate(String asContractId);
	// End QC 9388 R 8.4
	
	
	//Start R8.4.0 qc_8537 $0 Amendments do not appear in Amendment CoF task list after configuration
	public EPinDetailBean findContractAmendInfo(String asContractId);
	public Integer fetchContractFinancialsMaxYear (String contractId);
	public void addNextYearContractFinancialsZeroDollarAmd(HashMap loHMArgs);
	public void addNextYearContractFinFundingStreamZeroDollarAmd(HashMap loHMArgs);
	//End R8.4.0 qc_8537 $0 Amendments do not appear in Amendment CoF task list after configuration
	
	// Start QC 9452 R 8.5.0
	public Integer deleteContractLevelMessageHistory(String contractId);
	// End QC 9452 R 8.5.0

	
	//  Start QC 9400 R 8.8.0
	public Integer fetchAmendmentContractStatusId (HashMap aoReqMap);
	//  End QC 9400 R 8.8.0
	
	/* [Start] R8.10.0 QC9399    */
	public List<ContractBean> fetchAMDBaseDate(HashMap aoReqMap);
	/* [End] R8.10.0 QC9399    */
//    <select id="fetchAMDBaseDate" parameterType="HashMap"    resultMap="amendmentContractsForBatchProcessList">
	
	
	/*<!--[Start] R9.2.0  QC9572  --> */
	public List<ContractBean> fetchContractsForSrtEndDisc(HashMap<String, String> aoReqMap);
	
	public Integer updateContractStartEndDateForDiscripancy(HashMap<String, String> aoReqMap);

	public Integer updateBudgetStartDateForDiscripancy(HashMap<String, String> aoReqMap);
	public Integer updateBudgetEndDateForDiscripancy(HashMap<String, String> aoReqMap);
	public Integer addContractFinalcialsForDiscripancy(HashMap<String, String> aoReqMap);
	public Integer addContractFinStreamForDiscripancy(HashMap<String, String> aoReqMap);

	public List<ContractBudgetBean> fetchContractFinalcialsFY(HashMap<String, String> aoReqMap);	
	/*<!--[end] R9.2.0  QC9572  --> */

}
