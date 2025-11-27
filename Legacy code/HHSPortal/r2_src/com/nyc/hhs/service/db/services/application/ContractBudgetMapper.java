package com.nyc.hhs.service.db.services.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.AdvanceSummaryBean;
import com.nyc.hhs.model.AssignmentsSummaryBean;
import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.model.CBEquipmentBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBIndirectRateBean;
import com.nyc.hhs.model.CBMileStoneBean;
import com.nyc.hhs.model.CBOperationSupportBean;
import com.nyc.hhs.model.CBProfessionalServicesBean;
import com.nyc.hhs.model.CBProgramIncomeBean;
import com.nyc.hhs.model.CBServicesBean;
import com.nyc.hhs.model.CBUtilities;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.ContractBudgetSummary;
import com.nyc.hhs.model.ContractList;
import com.nyc.hhs.model.ContractedServicesBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.PersonnelServiceBudget;
import com.nyc.hhs.model.PersonnelServicesData;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.Rent;
import com.nyc.hhs.model.ReturnPaymentNotification;
import com.nyc.hhs.model.ReturnedPayment;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.UnallocatedFunds;

/**
 * This is mapper class for contract budget which has queries to get data
 * Updated in R6
 */
public interface ContractBudgetMapper
{

	public List<CBProgramIncomeBean> fetchProgramIncome(CBGridBean aoCBGridBeanObj);

	public Integer updateProgramIncome(CBProgramIncomeBean aoProgramIncomeBean);

	public Integer insertProgramIncome(CBProgramIncomeBean aoCBCBProgramIncomeBeanObj);

	public List<CBProgramIncomeBean> fetchProgramIncomeMasterTypes(CBGridBean aoCBGridBeanObj);

	public Integer fetchProgramIncomeForOther(Integer aoProgramIncomeId);

	public Integer fetchCountractBudgetCountForFY(HashMap aoBudgetInfo);

	public Integer addProgramIncomeForOther(CBProgramIncomeBean aoCBCBProgramIncomeBeanObj);

	public Integer updateProgramIncomeForOther(CBProgramIncomeBean aoCBCBProgramIncomeBeanObj);

	public Integer updateIndirectRate(CBIndirectRateBean aoIndirectRate);

	public BigDecimal getRemainingAmountIndirectRate(CBIndirectRateBean aoIndirectRate);

	public Integer getIndirectRateCount(String asSubBudgetId);

	public List<CBIndirectRateBean> fetchIndirectRate(CBGridBean asCBGridBean);

	public Integer insertIndirectRate(CBGridBean aoCBGridBean);

	public String fecthPrevApprovedBudget(CBIndirectRateBean aoCBIndirectRateBean);

	public Integer insertIndirectRateModification(CBIndirectRateBean aoCBGridBean);

	public Integer updateIndirectRatePercentage(CBGridBean aoIndirectRate);

	public Integer updateIndirectRateModificaitonPercentage(CBGridBean aoIndirectRate);

	public String fetchIndirectRatePercentage(CBGridBean aoIndirectRate);

	public Integer updateIndirectRateModification(CBIndirectRateBean aoIndirectRate);

	public List<CBIndirectRateBean> fetchIndirectRateModification(CBGridBean asCBGridBean);

	public List<CBIndirectRateBean> fetchIndirectRateAmendmentNewRecord(CBGridBean asCBGridBean);

	public List<BudgetDetails> fetchBudgetSummary(CBGridBean aoCBGridBeanObj);

	public BudgetDetails fetchCityFundedBudget(CBGridBean aoCBGridBeanObj);

	public List<ContractBudgetSummary> fetchUpdateBudgetSummary(String asBudgetId);

	public Integer updateIndirectRateAmendment(CBIndirectRateBean aoIndirectRate);

	public List<CBIndirectRateBean> fetchIndirectRateAmendment(CBGridBean asCBGridBean);

	public Integer updateContractBudgetRent(Rent rentId);

	public Integer insertContractBudgetRent(Rent rentId);

	public List<Rent> fetchContractBudgetRent(CBGridBean asCBGridBean);

	public Integer deleteContractBudgetRent(Rent rentId);

	public Integer getSeqForRent();

	public boolean insertModificationRent(Rent rentId);

	public boolean deleteModificationRent(Rent rentId);

	public List<CBUtilities> fetchUtilitiesDetails(CBGridBean aoCBGridBeanObj);

	public List<CBUtilities> fetchUtilitiesTypeDetails(CBGridBean aoCBGridBeanObj);

	public List<CBUtilities> fetchUtilitiesModifyDetails(CBGridBean aoCBGridBeanObj);

	public List<CBUtilities> fetchUtilitiesUpdateDetails(String asSubBudgetID);

	public List<CBUtilities> fetchUtilitiesAmendmentDetails(String asSubBudgetID);

	public Integer updateUtilitiesDetails(CBUtilities aoCBUtilities);

	public Integer insertUtilitiesDetails(CBGridBean aoCBUtilities);

	public Integer updateUtilitiesModifyDetails(CBUtilities aoCBUtilities);

	public Integer insertUtilitiesModifyDetails(CBUtilities aoCBUtilities);

	public String fetchUtilitiesTypeId(CBUtilities aoCBUtilities);

	public String fetchApprovedBudgetAmnt(CBUtilities aoCBUtilities);

	public boolean updateUtilitiesUpdateDetails(CBUtilities aoCBUtilities);

	public boolean updateUtilitiesAmendmentDetails(CBUtilities aoCBUtilities);

	public List<CBMileStoneBean> fetchMilestoneDetails(CBGridBean aoCBGridBeanObj);

	public Integer updateMilestoneDetails(CBMileStoneBean aoCBMilestoneBean);

	public Integer insertMilestoneDetails(CBMileStoneBean aoCBMilestoneBean);

	public Integer getSeqForMilestone();

	public Integer deleteMilestoneDetails(CBMileStoneBean aoCBMilestoneBean);

	public List<CBMileStoneBean> fetchMilestoneInvoiceDetails(CBGridBean aoCBGridBeanObj);

	public Long fetchRemainingAmountMilestone(CBMileStoneBean aoCBMilestoneBean);

	public Integer checkExistingInvoiceForMilestoneItem(CBMileStoneBean aoCBMilestoneBean);

	public Integer insertInvoiceDetailForMilestoneItem(CBMileStoneBean aoCBMilestoneBean);

	public Integer updateMilestoneInvoiceDetails(CBMileStoneBean aoCBMilestoneBean);

	public List<CBMileStoneBean> fetchMileStoneModificationDetails(String asSubBudgetId);

	public boolean updateMileStoneModificationDetails(CBMileStoneBean aoCBMileStoneBean);

	public boolean insertMileStoneModificationDetails(CBMileStoneBean aoCBMileStoneBean);

	public boolean deleteMileStoneModificationDetails(CBMileStoneBean aoCBMileStoneBean);

	public List<CBMileStoneBean> fetchMileStoneUpdateDetails(String asSubBudgetId);

	public boolean updateMileStoneUpdateDetails(CBMileStoneBean aoCBMileStoneBean);

	public boolean insertMileStoneUpdateDetails(CBMileStoneBean aoCBMileStoneBean);

	public boolean deleteMileStoneUpdateDetails(CBMileStoneBean aoCBMileStoneBean);

	public List<CBMileStoneBean> fetchMileStoneAmendmentDetails(String asSubBudgetId);

	public boolean updateMileStoneAmendmentDetails(CBMileStoneBean aoCBMileStoneBean);

	public boolean insertMileStoneAmendmentDetails(CBMileStoneBean aoCBMileStoneBean);

	public boolean deleteMileStoneAmendmentDetails(CBMileStoneBean aoCBMileStoneBean);

	public List<CBGridBean> fetchSubBudgetIDList(Map<String, Object> aoQueryMap);

	public Integer fetchOperationSupportItemsCount(String asSubBudgetId);

	public List<CBOperationSupportBean> fetchOperationSuppMasterList();

	public void insertStandardRowsOperationSupport(CBOperationSupportBean aoCBOperationSupportBean);

	public Integer fetchOpAndSupprtForOther(Integer aoProfServiceId);

	public Integer addOpAndSupprtForOther(CBOperationSupportBean aoCBOperationSupportBean);

	public Integer updateOpAndSupprtForOther(CBOperationSupportBean aoCBOperationSupportBean);

	public CBOperationSupportBean fetchOpAndSupportPageData(CBGridBean aoCBGridBeanObj);

	public List<CBOperationSupportBean> fetchOperationAndSupportDetails(CBGridBean aoCBGridBeanObj);

	public Integer editOperationAndSupportDetails(CBOperationSupportBean aoCBOperationSupportBean);

	public List<CBEquipmentBean> fetchEquipmentDetails(CBGridBean aoCBGridBeanObj);

	public void addEquipmentDetails(CBEquipmentBean aoCBEquipmentBean);

	public Integer editEquipmentDetails(CBEquipmentBean aoCBEquipmentBean);

	public Integer deleteEquipmentDetails(CBEquipmentBean aoCBEquipmentBean);

	public boolean updateBudgetModificationStatus(ContractBudgetBean aoContractBudgetBean);

	public boolean updateBudgetUpdateStatus(ContractBudgetBean aoContractBudgetBean);

	public List<RateBean> fetchRateList(CBGridBean aoCBGridBeanObj);

	public Integer updateRateList(RateBean rateId);

	public Integer insertRateList(RateBean rateId);

	public Integer insertInvoiceDetails(RateBean rateId);

	public Integer deleteRateList(RateBean rateId);

	public Integer getSeqForRate();

	public ContractList fetchContractSummary(HashMap<String, String> aoHashmap);

	public ContractList fetchContractSummaryAmendment(HashMap<String, String> aoHashmap);

	public List<String> fetchSubBudgetSummary(HashMap<String, String> aoHashmap);

	public BudgetDetails fetchFyBudgetSummary(HashMap<String, String> aoHashmap);

	public BudgetDetails fetchModificationFyBudgetSummary(HashMap<String, String> aoHashmap);

	public BigDecimal getInvoiceAmount(HashMap<String, String> aoHashmap);

	public BigDecimal getInvoiceAmountForModification(HashMap<String, String> aoHashmap);

	public Integer getActPaidAmount(HashMap<String, String> aoHashmap);

	public List<CBProfessionalServicesBean> fetchProfessionalServicesDetails(CBGridBean aoProfService);

	public Integer updateProfessionalServicesDetails(CBProfessionalServicesBean aoProfServicesDetails);

	public List<String> fetchProfessionalServicesTypeId(CBGridBean aoProfService);

	public Integer addProfessionalServicesDetails(CBGridBean aoProfService);

	public Integer addProfServicesDetails(CBProfessionalServicesBean aoProfService);

	public Integer fetchProfServicesItemsCount(Integer subBudgetId);

	public Integer fetchProfServicesForOther(Integer aoProfServiceId);

	public List<ContractedServicesBean> fetchContractedServicesConsultants(CBGridBean aoCBGridBeanObj);

	public List<ContractedServicesBean> fetchContractedServicesSubContractors(CBGridBean aoCBGridBeanObj);

	public List<ContractedServicesBean> fetchContractedServicesVendors(CBGridBean aoCBGridBeanObj);

	public ContractedServicesBean fetchNonGridContractedServices(CBGridBean aoCBGridBeanObj);

	public Integer addContractedServices(ContractedServicesBean aoCBGridBeanObj);

	public Integer editContractedServices(ContractedServicesBean aoCBGridBeanObj);

	public Integer delContractedServices(ContractedServicesBean aoCBGridBeanObj);

	public Integer addProfServicesForOther(CBProfessionalServicesBean aoProfService);

	public Integer updateProfServicesForOther(CBProfessionalServicesBean aoProfServicesDetails);

	public CBGridBean getCbGridDataForSession(HashMap<String, String> aoHashmap);

	public List<AssignmentsSummaryBean> fetchAssignmentSummary(CBGridBean aoCBGridBean);

	public List<AssignmentsSummaryBean> fetchAssignmentSummaryForParentBudget(CBGridBean aoCBGridBean);

	Integer insertContractDocumentDetails(Map<String, Object> aoParamMap);

	Integer insertBudgetDocumentDetails(Map<String, Object> aoParamMap);

	Integer insertInvoiceDocumentDetails(Map<String, Object> aoParamMap);

	public List<ExtendedDocument> fetchFinancialDocuments(Map<String, Object> aoParamMap);

	public int deleteContractFinancialDoc(Map<String, String> aoParameterMap);

	public int deleteBudgetFinancialDoc(Map<String, String> aoParameterMap);

	public int deleteInvoiceFinancialDoc(Map<String, String> aoParameterMap);

	public List<UnallocatedFunds> fetchUnallocatedFunds(UnallocatedFunds unallocatedFunds);

	public int updateUnallocatedFunds(UnallocatedFunds aoUnallocatedFunds);

	public int insertUnallocatedFunds(UnallocatedFunds aoUnallocatedFunds);

	public void setAmendmentContractStatusPendingApproval(HashMap<String, String> aoHashmap);

	public void setContractBudgetStatus(HashMap<String, String> aoHashmap);

	public HashMap<String, Integer> getAmendmentBudgetsCount(HashMap<String, String> aoHashmap);

	public List<PersonnelServiceBudget> fetchSalariedEmployee(CBGridBean asCBGridBean);

	public Integer insertPersonnelServices(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public Integer updatePersonnelServices(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public Integer deletePersonnelServices(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public List<PersonnelServiceBudget> fetchHourlyEmployee(CBGridBean asCBGridBean);

	public List<PersonnelServiceBudget> fetchSeasonalEmployee(CBGridBean asCBGridBean);

	public List<PersonnelServiceBudget> fetchFringBenifits(CBGridBean asCBGridBean);

	public Integer insertFringeBenifits(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public Integer updateFringeBenifits(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public String fetchTotalSalary(CBGridBean aoCBGridBeanObj);

	public String fetchTotalFringes(CBGridBean aoCBGridBeanObj);

	public BigDecimal fetchSalariedYTDInvoicedAmount(CBGridBean aoCBGridBeanObj);

	public BigDecimal fetchFringesYTDInvoicedAmount(CBGridBean aoCBGridBeanObj);

	public Integer setContractBudgetStatusForReviewTask(Map<String, Object> aoHashMap);

	// start release 3.14.0
	public Integer setContractBudgetStatusForReviewTaskForAmendment(Map<String, Object> aoHashMap);

	public Integer setContractBudgetStatusForReviewTaskForAmendmentInConfigurationStatus(Map<String, Object> aoHashMap);

	// end release 3.14.0

	public Integer fetchProgramIncomeItemsCount(CBGridBean aoCBGridBean);

	public Integer fetchUtilityItemsCount(CBGridBean aoCBGridBeanObj);

	public Integer fetchUnallocatedFundsCount(String asSubBudgetId);

	public String fetchCurrentCBStatus(String asBudgetID);

	public List<String> fetchSubBudgetSummaryPrint(HashMap<String, String> aoHashmap);

	public ContractList fetchAmendmentFiscialEpinDetails(HashMap<String, String> aoHashmap);

	public List<String> fetchPersonnelServiceMasterData(String asDummyString);

	public Integer insertContractDetailsFromAwardTask(HashMap<String, String> aoContractMap);

	public List<AdvanceSummaryBean> fetchAdvanceDetails(CBGridBean aoCBGridBean);

	public List<AdvanceSummaryBean> fetchAdvanceDetailsForParentBudget(CBGridBean aoCBGridBean);

	public CBUtilities fetchUtilitiesDetailsForValidation(String asId);

	public int removeContractFinacialdocFromVault(String asDocumentId);

	public int removeBudgetDocumentFromVault(String asDocumentId);

	public int removeInvoiceDocumentFromVault(String asDocumentId);

	public Integer fetchOriginalContractCount(Map<String, String> aoHashMap);

	public Integer createContractReplica(Map<String, String> aoHashMap);

	public Integer createBudgetReplica(Map<String, String> aoHashMap);

	public List<String> fetchSubBudgetList(Map<String, String> aoHashMap);

	public Integer createSubBudgetReplica(Map<String, String> aoHashMap);

	public Integer createPersonnelServiceReplica(Map<String, String> aoHashMap);

	public Integer createOperationAndSupportReplica(Map<String, String> aoHashMap);

	public Integer createUtilitiesReplica(Map<String, String> aoHashMap);

	public Integer createProfessionalServiceReplica(Map<String, String> aoHashMap);

	public Integer createRentReplica(Map<String, String> aoHashMap);

	public Integer createContractedServicesReplica(Map<String, String> aoHashMap);

	public Integer createRateReplica(Map<String, String> aoHashMap);

	public Integer createMilestoneReplica(Map<String, String> aoHashMap);

	public Integer createUnallocatedReplica(Map<String, String> aoHashMap);

	public Integer createIndirectRateReplica(Map<String, String> aoHashMap);

	public Integer createProgramIncomeReplica(Map<String, String> aoHashMap);

	public Integer createFringeReplica(Map<String, String> aoHashMap);

	public Integer createEquipmentReplica(Map<String, String> aoHashMap);

	public BigDecimal getUpdateAmountBudget(HashMap<String, String> aoHashMap);

	public BudgetDetails fetchUpdateFyBudgetSummary(HashMap<String, String> aoHashmap);

	public BigDecimal fetchAmountTotal(HashMap<String, String> aoHashMap);

	public String fetchBudgetType(String asBudgetId);

	// Release 3.6.0 Enhancement id 6484
	public Integer subBudgetSiteCount(String asBudgetId);

	public Integer createPersonnelServiceReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer createOperationAndSupportReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer createUtilitiesReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer createProfessionalServiceReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer createRentReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer createContractedServicesReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer createRateReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer createMilestoneReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer createUnallocatedReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer createIndirectRateReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer createProgramIncomeReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer createFringeReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer createEquipmentReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public List<ContractBudgetBean> fetchSubBudgetListNewFY(TaskDetailsBean aoTaskDetailsBean);

	public Integer updateParentOfDerivedSubBudgets(ContractBudgetBean aoContractBudgetBean);

	public Integer insertContractFinReplicaForOriginal(Map<String, Object> aoHashMap);

	public Integer deleteContractFinancialEntries(Map<String, Object> aoHashMap);

	public Integer getFringeBenefitCount(String asSubBudgetId);

	public Integer insertStandardFringeBenefits(CBGridBean aoCBGridBean);

	public Integer deleteContractFinFundingEntries(Map<String, Object> aoHashMap);

	public Integer insertContractFinFundingReplicaForOriginal(Map<String, Object> aoHashMap);

	@SuppressWarnings("rawtypes")
	public HashMap getAmendEpinAndAmount(HashMap aoHashMap);

	// Start release 3.12.0 for enhancement request 6643
	public Integer updatePersonnelServiceModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateOperationAndSupportModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateUtilitiesModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateProfessionalServiceModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateRentModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateContractedServicesModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateRateModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateMilestoneModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateUnallocatedModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateIndirectRateModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateProgramIncomeModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateFringeModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateEquipmentModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateBudgetModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateSubBudgetModifiedDate(Map<String, Object> aoHashMap);

	public Integer updateSubBudgetYtdInvoiceAmoutnt(Map<String, Object> aoHashMap);

	public Integer updateBudgetYtdInvoiceAmoutnt(Map<String, Object> aoHashMap);

	public Integer updatedModifiedDateInContract(Map<String, Object> aoHashMap);

	public Integer updateModifiedDateInAssignment(Map<String, Object> aoHashMap);

	public Integer updateModifiedDateInAssignmentInAdvanceReview(Map<String, Object> aoHashMap);

	// End release 3.12.0 for enhancement request 6643
	// R6 change starts
	public Integer insertPersonnelServicesDetailed(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public List<PersonnelServiceBudget> fetchPsDetailedEmployee(CBGridBean asCBGridBean);

	public Integer deletePersonnelServicesDetails(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public Integer updatePersonnelServicesDetails(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public List<PersonnelServiceBudget> fetchHourlyDetailedEmployee(CBGridBean asCBGridBean);

	public Integer deletePersonnelServicesForDetail(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public Integer insertPersonnelServicesFromDetails(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public String fetchPersonnelServicePositionId(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public String fetchTotalPositions(CBGridBean aoCBGridBeanObj);

	public Integer getPersonnelDetailData(String asSubBudgetId);

	public Integer updatePersonnelServicesForDetail(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public Integer updatePsDetailSummaryId(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public String getBudgetExistInfo(CBGridBean aoCBGridBeanObj);

	public List<String> fetchFringBenefitsMasterList();

	public Integer insertFringeBenefitDetail(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public List<PersonnelServiceBudget> fetchFringeBenefitsDetail(CBGridBean asCBGridBean);

	public Integer updateFringeBenefitsDetails(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public Integer updateFringeBenefitsSummary(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public PersonnelServicesData fetchFringeBenefitsSummaryData(CBGridBean asCBGridBean);

	public String fetchBudgetApprovedDate(CBGridBean aoCBGridBeanObj);

	public Integer checkIfOtherBudgetApproved(String asSubBudgetId);

	public PersonnelServicesData fetchFringeBenefitsDetailData(CBGridBean asCBGridBean);

	public Integer createPSDetailReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer createPSSummaryAggregateReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer createFringeDetailReplicaNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer updatePSDetailSummaryIdNewFY(ContractBudgetBean aoContractBudgetBean);

	public String fetchPsSummaryPositions(String asSubBudgetId);

	// Added for Defect: 8459
	public Integer getFringeBenefitDetailCount(String asSubBudgetId);
	
	public Integer createPersonnelServiceDetailReplica(Map<String, String> aoHashMap);
	
    public Integer createFringeDetailReplica(Map<String, String> aoHashMap);
	// R6 change ends
    //Start: Added in Release 5.1.0 for FTE calculations
    public String getFullTimeEmpHrPerYear(CBGridBean aoCBGridBeanObj);
    //End: Added in Release 5.1.0 for FTE calculations
	//Start Release 6 defect id 8428 and 8257
	public ArrayList<String> fetchSubBudgetDetailsForAmendmentConf(HashMap aoHashMap);
	//End Release 6 defect id 8428 and 8257
	
	public Integer updateReturnedPaymentStatus(ReturnedPayment aoReturnedPaymentObj);

	public Integer insertReturnPayment(ReturnedPayment loReturnedPayment);

	public List<ReturnPaymentNotification> getNotificationHistory(String asBudgetId);


	public List<ReturnedPayment> getReturnedPaymentSumaryDB(ReturnedPayment aoReturnedPayment);
	
	public List<String> getDocIFordReturnedPaymentSumaryDB(String asBudgetId);

	public String getLastNotifiedDate(String asBudgetId);

	public String getUnRecoupedAmount(String asBudgetId);

	public ReturnedPayment getReturnedPaymentSummary(String returnedPaymentId);
	
	public ReturnedPayment fetchReturnedPaymentSummary(HashMap<String, String> aoHashmap);
	
	public Integer updateReturnPaymentDetail(HashMap<String, String> aoHashmap);
	
	public Integer setReturnPaymentStatus(HashMap<String, String> aoHashmap);
	
	
	public String getTotalApprovedRetPayAmount(String asBudgetId);
	
	public String getLatestReturnedPaymentId(String asLoggedInUser);
	
	public Integer insertReturnedPaymentDocumentDetails(Map<String, Object> aoParamMap);
	
	public List<ExtendedDocument> fetchReturnedPaymentDocuments(Map<String, Object> aoParamMap);
	
	public int deleteReturnedPaymentDoc(Map<String, String> aoParameterMap);
	
	public String getReturnedPaymentStatus(String asReturnedPaymentId);
	
	//Start: Added in R7 For Cost-Center
	public String fetchCostCenterOpted(Map<String, Object> loQueryMap);
	
	public void insertCostCentreDetails(CBGridBean loCBGridBean);
	
	public void deleteCostCenterDetails(CBGridBean loCBGridBean);
	
	public void insertServicesDetails(CBGridBean loCBGridBean);
	
	public void deleteServicesDetails(CBGridBean loCBGridBean);
	
	public Integer fetchFlag(String contract_id);
	
	public List<CBGridBean> fetchSubBudgetIDListForUpdate(Map<String, Object> aoQueryMap);
	
	//End: Added in R7 For Cost-Center
	
	
	//Start: R7 Program Income changes
	public String isPICategoryInBudgetCustomization(Map<String, String> aoHashMap);
	
	public Integer deleteDefaultPIEntries(Map<String, String> aoHashMap);
	
	public Integer updateIsOldPIFlag(Map<String, String> aoHashMap);
	
	public String isPIInBudgetCustomizationForBaseBudget(Map<String, String> aoHashMap);
	
	public String isOldPI(String asBudgetId);
	
	public String fetchPIIndirectRatePercentage(CBGridBean aoCBGridBeanObj);
	
	public Integer updatePIIndirectRatePercentage(CBGridBean aoIndirectRate);

	public Integer updatePIIndirectRateModificaitonPercentage(CBGridBean aoIndirectRate);
	
	public List<ContractBean> fetchAffectedBudgetFYList(Map<String, String> aoHashMap);
	
	public String prevAmendUpdtPIBudgetCustCount(Map<String, String> aoHashMap);
	
	public String isOldPIFromContract(Map<String, String> aoHashMap);
	
	public List<String> fetchSources(String asDummyString);

	public Integer insertProgramIncomeGrid(CBProgramIncomeBean aoCBCBProgramIncomeBeanObj);

	public Integer deleteProgramIncome(CBProgramIncomeBean programIncomeId);
	
	public List<CBGridBean> fetchSubBudgetDetails(Map<String,String> aoMap);
	
	public String fetchBudgetTypeId(String asBudgetId);
	
	public Integer updateIsOldPIFlagForOldPI(String asBudgetId);
	//End: R7 Program Income changes
	
	//Start: Added in R7 for cost-center
	public List<CBServicesBean> fetchServiceData(CBGridBean aoCBGridBeanObj);

	public List<CBServicesBean> fetchCostData(CBGridBean aoCBGridBeanObj);

	public Integer updateServicesList(CBServicesBean servicesDetailId);

	public Integer updateCostCenterList(CBServicesBean servicesDetailId);

	public BigDecimal fetchCostCenterAmountTotal(HashMap<String, String> aoHashMap);

	public BigDecimal fetchSubBudgetAmount(String aoSubBudgetId);
	
	public List<CBGridBean> fetchOutYearAmendmentSubBudgetIDList(Map<String, Object> aoQueryMap);
	//End: Added in R7 for cost-center

	//Start: Added in R7.4.0 for QC9008
	public List<BudgetList>  fetchBudgetUpdate4Del(HashMap<String, String> aoQueryMap); 
	
	public void deleteBudgetUpdateTask(HashMap <String, String> aoParamMap);
	//End: Added in R7.4.0 for QC9008
	
	//Start: QC 9156 R 7.5.0 Accenture fix Cost Center Records not created for "Original" Budget (BUDGET_TYPE_ID = 5)
	public Integer createCostCenterReplica(Map<String, String> aoHashMap);
	public Integer createServicesReplica(Map<String, String> aoHashMap);
	public Integer createServicesConfigReplica(Map<String, String> aoHashMap);
	//End: QC 9156 R 7.5.0 Accenture fix Cost Center Records not created for "Original" Budget (BUDGET_TYPE_ID = 5)
	
	//Start QC 8394 R 7.9.0 Add lines to Unallocated Fund 
	public int addUnallocatedFunds(UnallocatedFunds aoUnallocatedFunds);
	public int deleteUnallocatedFunds(UnallocatedFunds aoUnallocatedFunds);
	//End QC 8394 R 7.9.0 Add lines to Unallocated Fund 
	
	//Start QC 9202 R 7.10.0 Indirect Rate 
	public String getFiscalYearIdFromSubBudget(String asSubBudgetId);
	public String getFiscalYearIdFromBudget(String asContractBudgetId);
	//End QC 9202 R 7.10.0 Indirect Rate 
	
	//*** Start 9592 R 8.10.0 - Remaining Amount in Sub Budgets Created in Update Config Task are NULL when merged with Base
	public Integer updateSubBudgetYtdInvoiceAmoutntBase(Map<String, Object> aoHashMap);
	//***End 9592 R 8.10.0 - Remaining Amount in Sub Budgets Created in Update Config Task are NULL when merged with Base

	
}
