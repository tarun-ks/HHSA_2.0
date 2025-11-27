package com.nyc.hhs.service.db.services.application;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.BudgetDetails;
import com.nyc.hhs.model.CBEquipmentBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CBMileStoneBean;
import com.nyc.hhs.model.CBOperationSupportBean;
import com.nyc.hhs.model.CBProfessionalServicesBean;
import com.nyc.hhs.model.CBProgramIncomeBean;
import com.nyc.hhs.model.CBServicesBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.ContractedServicesBean;
import com.nyc.hhs.model.PersonnelServiceBudget;
import com.nyc.hhs.model.RateBean;
import com.nyc.hhs.model.Rent;
import com.nyc.hhs.model.UnallocatedFunds;

/**
 * This is mapper class for contract budget modifications which has queries to
 * get data
 */
public interface ContractBudgetModificationMapper
{
	public List<CBGridBean> fetchCMSubBudgetSummary(HashMap<String, String> aoHashmap);

	public List<CBGridBean> fetchCMSubBudgetPrintSummary(HashMap<String, String> aoHashmap);

	public List<CBProfessionalServicesBean> cbmFetchProfServicesDetails(CBGridBean aoProfService);

	public Integer addProfServicesModificationAmount(CBProfessionalServicesBean aoProfService);

	public Integer updateProfServicesModificationAmount(CBProfessionalServicesBean aoProfServicesDetails);

	public Integer insertNewBudgetModificationDetails(ContractBudgetBean aoContractBudgetBean);

	public Integer insertNewSubBudgetModificationDetails(HashMap<String, String> aoInputMap);

	public String fetchModifiedBudgetId(HashMap<String, String> aoInputMap);

	public BigDecimal fetchRemainingAmnt(CBProfessionalServicesBean aoProfServicesDetails);

	public CBGridBean getCbGridDataForSession(HashMap<String, String> aoHashmap);

	public List<RateBean> fetchContractBudgetRateInfo(CBGridBean aoCBGridBeanObj);

	public List<RateBean> fetchContractBudgetModificationAmount(CBGridBean aoCBGridBeanObj);

	public Integer insertContractBudgetModificationRateInfo(RateBean aoRateBeanObj);

	public Integer updateContractBudgetModificationRateInfo(RateBean aoRateBeanObj);

	public Integer updateBudgetModificationRateUnit(RateBean aoRateBeanObj);

	// Made changes for enhancement id 6484 release 3.6.0
	public Integer getSeqForSubBudgetId();

	public Integer deleteContractBudgetModificationRateInfo(RateBean aoRateBeanObj);

	public Integer getBaseUnitContractBudgetModificationRate(RateBean aoRateBeanObj);

	public List<CBMileStoneBean> fetchMilestoneForModification(CBGridBean aoCBGridBeanObj);

	public List<CBMileStoneBean> fetchMilestoneBaseDetails(CBGridBean aoCBGridBeanObj);

	public CBMileStoneBean fetchMilestoneDetailsForValidation(String asId);

	public Integer updateMilestoneDetails(CBMileStoneBean aoCBMilestoneBean);

	public Integer insertNewMilestoneForMod(CBMileStoneBean aoCBMilestoneBean);

	public Integer insertMilestoneDetails(CBMileStoneBean aoCBMilestoneBean);

	public Integer getSeqForMilestone();

	public Integer deleteMilestoneDetails(CBMileStoneBean aoCBMilestoneBean);

	public Integer insertNewContractBudgetModificationRate(RateBean aoRateBeanObj);

	public List<BudgetDetails> fetchModificationBudgetSummary(CBGridBean aoCBGridBeanObj);

	public BigDecimal getRemainingAmountModificationRent(Rent aoRent);

	public Integer updateModificationRent(Rent rentId);

	public Integer editRentModification(Rent rentId);

	public Integer insertContractBudgetModificationRent(Rent rentId);

	public List<Rent> fetchContractBudgetModificationRentNew(CBGridBean aoCBGridBeanObj);

	public List<Rent> fetchModificationRent(CBGridBean asCBGridBean);

	public List<Rent> fetchContractBudgetModificationRent(CBGridBean aoCBGridBeanObj);

	public Integer getBaseContractBudgetModificationRent(Rent aoRent);

	public Integer getCountForRentModification(Rent aoRent);

	public void insertRentModDetails(Rent aoRent);

	public Rent fetchContractBudgetModificationRentAmount(Rent aoRent);

	public Integer delRentModification(Rent aoRent);

	public List<UnallocatedFunds> fetchModificationUnallocatedFunds(CBGridBean aoCBGridBeanObj);

	public int updateModificationUnallocatedFunds(UnallocatedFunds aoUnallocatedFunds);

	public int updateModificationUnallocatedFundsForApproved(UnallocatedFunds aoUnallocatedFunds);

	public int insertModificationUnallocatedFunds(CBGridBean aoCBGridBeanObj);

	public int insertModUnallocatedFunds(CBGridBean aoCBGridBeanObj);

	public List<CBProgramIncomeBean> fetchProgramIncomeModification(CBGridBean aoCBGridBeanObj);

	public List<CBProgramIncomeBean> fetchProgramIncomeModificationParentEqualSub(CBGridBean aoCBGridBeanObj);

	public List<CBProgramIncomeBean> fetchProgramIncomeModAmtDetails(CBGridBean aoCBGridBeanObj);

	public Integer updateProgramIncomeModification(CBProgramIncomeBean aoProgramIncomeBean);

	public Integer insertProgramIncomeModification(CBProgramIncomeBean aoCBCBProgramIncomeBeanObj);

	public List<PersonnelServiceBudget> fetchSalriedEmployeeForModification(CBGridBean asCBGridBean);

	public Integer updatePersonnelServicesForModification(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public Integer insertFirstPersonnelServicesForModification(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public Integer insertPersonnelServicesModification(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public List<PersonnelServiceBudget> fetchHourlyEmployeeForModification(CBGridBean asCBGridBean);

	public List<PersonnelServiceBudget> fetchSeasonalEmployeeForModification(CBGridBean asCBGridBean);

	public Integer updateFringeBenifitsForModification(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public Integer insertFirstFringeBenefitsForModification(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public List<PersonnelServiceBudget> fetchFringeBenefitsForModification(CBGridBean aoPersonnelServiceBudget);

	public CBOperationSupportBean fetchOpAndSupportModPageData(CBGridBean aoCBGridBeanObj);

	public List<CBOperationSupportBean> fetchOperationAndSupportModDetails(CBGridBean aoCBGridBeanObj);

	public List<CBOperationSupportBean> fetchOperationAndSupportModDetailsParentEqualSub(CBGridBean aoCBGridBeanObj);

	public List<CBOperationSupportBean> fetchOperationAndSupportModAmtDetails(CBGridBean aoCBGridBeanObj);

	public List<CBEquipmentBean> fetchEquipmentModDetails(CBGridBean aoCBGridBeanObj);

	public List<CBEquipmentBean> fetchEquipmentModAmtDetails(CBGridBean aoCBGridBeanObj);

	public void addEquipmentModificationDetails(CBEquipmentBean aoCBEquipmentBean);

	public void editInsertEquipmentModificationDetails(CBEquipmentBean aoCBEquipmentBean);

	public Integer editEquipmentModificationDetails(CBEquipmentBean aoCBEquipmentBean);

	public Integer delEquipmentModificationDetails(CBEquipmentBean aoCBEquipmentBean);

	public Integer getBaseUnitForEquipment(CBEquipmentBean aoCBEquipmentBean);

	public Integer editOperationAndSupportModDetails(CBOperationSupportBean aoCBOperationSupportBean);

	public CBEquipmentBean fetchEquipmentDetailsForValidation(String asId);

	public CBOperationSupportBean fetchOTPSDetailsForValidation(String asId);

	public CBProgramIncomeBean fetchProgIncomeDetailsForValidation(String asId);

	public String getUnitDescContractBudgetModificationRate(RateBean aoCBGridBeanObj);

	public Integer getCountForOTPSMod(CBOperationSupportBean aoCBOperationSupportBean);

	public void insertOperationAndSupportModDetails(CBOperationSupportBean aoCBOperationSupportBean);

	public BigDecimal getRemainingAmountModificationContractedServices(
			ContractedServicesBean aoContractedServicesBeanObj);

	@SuppressWarnings("rawtypes")
	public String fetchParentBudgetId(Map aoMap);

	@SuppressWarnings("rawtypes")
	public Integer mergeBudgetModificationDocument(Map aoMap);

	public Double getBaseUnitForPersonnelService(PersonnelServiceBudget aoPersonnelServiceBudgetBean);

	public List<ContractedServicesBean> fetchContractedServicesModificationConsultants(CBGridBean aoCBGridBeanObj);

	public List<ContractedServicesBean> fetchContractedServicesNewModificationConsultants(CBGridBean aoCBGridBeanObj);

	public List<ContractedServicesBean> fetchContractedServicesModificationSubContractors(CBGridBean aoCBGridBeanObj);

	public List<ContractedServicesBean> fetchContractedServicesModificationVendors(CBGridBean aoCBGridBeanObj);

	public Integer addContractedServicesModification(ContractedServicesBean aoCBGridBeanObj);

	public Integer updateContractedServicesModification(ContractedServicesBean aoCBGridBeanObj);

	public Integer editContractedServicesModification(ContractedServicesBean aoCBGridBeanObj);

	public Integer delContractedServicesModification(ContractedServicesBean aoCBGridBeanObj);

	public ContractedServicesBean fetchInsertContractedServicesModification(ContractedServicesBean aoCBGridBeanObj);

	public ContractedServicesBean fetchNonGridContractedServices(CBGridBean aoCBGridBeanObj);

	public BigDecimal getRemainingAmountModificationRate(RateBean aoRateBeanObj);

	@SuppressWarnings("rawtypes")
	public Map fetchPSDetailsForValidation(String asLineItemId);

	public BigDecimal fetchFringeAmountForValidation(String asLineItemId);

	public String fetchModificationAmountTotal(String aoBudgetId);

	public Integer mergePersonnelServiceReplica(Map<String, String> aoHashMap);

	public Integer mergeOperationAndSupportReplica(Map<String, String> aoHashMap);

	public Integer mergeUtilitiesReplica(Map<String, String> aoHashMap);

	public Integer mergeProfessionalServiceReplica(Map<String, String> aoHashMap);

	public Integer mergeRentReplica(Map<String, String> aoHashMap);

	public Integer mergeContractedServicesReplica(Map<String, String> aoHashMap);

	public Integer mergeRateReplica(Map<String, String> aoHashMap);

	public Integer mergeMilestoneReplica(Map<String, String> aoHashMap);

	public Integer mergeUnallocatedReplica(Map<String, String> aoHashMap);

	public Integer mergeIndirectRateReplica(Map<String, String> aoHashMap);

	public Integer mergeProgramIncomeReplica(Map<String, String> aoHashMap);

	public Integer mergeFringeReplica(Map<String, String> aoHashMap);

	public Integer mergeEquipmentReplica(Map<String, String> aoHashMap);

	public List<String> fetchSubBudgetList(String budgetId);

	// Start Changes for agency outbound interafce 6644
	public Integer markPersonnelServiceAsDeleted(Map<String, String> aoHashMap);

	public Integer markOperationAndSupportAsDeleted(Map<String, String> aoHashMap);

	public Integer markUtilitiesAsDeleted(Map<String, String> aoHashMap);

	public Integer markProfessionalServiceAsDeleted(Map<String, String> aoHashMap);

	public Integer markRentAsDeleted(Map<String, String> aoHashMap);

	public Integer markContractedServicesAsDeleted(Map<String, String> aoHashMap);

	public Integer markRateAsDeleted(Map<String, String> aoHashMap);

	public Integer markMilestoneAsDeleted(Map<String, String> aoHashMap);

	public Integer markUnallocatedAsDeleted(Map<String, String> aoHashMap);

	public Integer markIndirectRateAsDeleted(Map<String, String> aoHashMap);

	public Integer markProgramIncomeAsDeleted(Map<String, String> aoHashMap);

	public Integer markFringeAsDeleted(Map<String, String> aoHashMap);

	public Integer markEquipmentAsDeleted(Map<String, String> aoHashMap);

	public Integer markSubBudgetsAsDeleted(Map<String, String> aoHashMap);

	public Integer markBudgetAsDeleted(Map<String, String> aoHashMap);

	// end changes for agency outbound interafce 6644
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

	// changes for agency outbound interafce 6644
	public Integer mergeSubBudgetForUpdate(Map<String, String> aoHashMap);

	// changes for agency outbound interafce 6644
	public Integer mergeBudgetForUpdate(Map<String, String> aoHashMap);

	// changes for agency outbound interafce 6644
	public Integer mergeContractForUpdate(Map<String, String> aoHashMap);

	@SuppressWarnings("rawtypes")
	public Integer mergeBudgetUpdateDocument(Map aoMap);

	@SuppressWarnings("rawtypes")
	public Integer mergeContractUpdateDocument(Map aoMap);

	@SuppressWarnings("rawtypes")
	public String fetchParentContractId(Map aoMap);
	public String fetchAmendContractId(Map aoMap);

	// Made changes for enhancement id 6263 release 3.6.0
	public BigDecimal fetchContractAmendmentAmount(String aoMap);

	public BigDecimal fetchUpdateAmountTotal(String aoSubBudgetId);

	public BigDecimal fetchSubBudgetAmount(String aoSubBudgetId);

	// changes for agency outbound interafce 6644
	public Integer mergeContractForAmendment(Map<String, String> aoHashMap);

	public Integer fetchCountOfApprovedBudget(String aoContractId);

	public String getFYBudgetContractBudgetModificationRate(RateBean aoCBGridBeanObj);

	// changes for agency outbound interafce 6644
	public Integer updateContractEndDate(Map<String, String> aoHashMap);

	public List<CBGridBean> fetchModificationSubBudgetSummary(HashMap<String, String> aoHashmap);

	public List<CBGridBean> fetchUpdateSubBudgetSummary(HashMap<String, String> aoHashmap);

	@SuppressWarnings("rawtypes")
	public String fetchBudgetStatus(Map aoMap);

	public List<String> fetchNewlyAddedSubBudgetList(String aoBudgetId);

	public Integer linkPersonnelServicesToBase(Map<String, String> aoHashMap);

	public Integer linkOperationsAndSupportToBase(Map<String, String> aoHashMap);

	public Integer linkUtilitiesToBase(Map<String, String> aoHashMap);

	public Integer linkProfessionalServiceToBase(Map<String, String> aoHashMap);

	public Integer linkRentToBase(Map<String, String> aoHashMap);

	public Integer linkContractedServicesToBase(Map<String, String> aoHashMap);

	public Integer linkRateToBase(Map<String, String> aoHashMap);

	public Integer linkMilestoneToBase(Map<String, String> aoHashMap);

	public Integer linkUnallocatedToBase(Map<String, String> aoHashMap);

	public Integer linkIndirectToBase(Map<String, String> aoHashMap);

	public Integer linkProgramIncomeToBase(Map<String, String> aoHashMap);

	public Integer linkFringeToBase(Map<String, String> aoHashMap);

	public Integer linkEquipmemtToBase(Map<String, String> aoHashMap);

	public Integer linkSubBudgetToBase(Map<String, String> aoHashMap);

	// changes for agency outbound interafce 6644
	public Integer markContractAsDeleted(Map<String, String> aoHashMap);

	// changes for agency outbound interafce 6644
	public Integer mergeContractFinancialForAmendment(Map<String, String> aoHashMap);

	// changes for agency outbound interafce 6644
	public Integer mergeContractFinancialForUpdate(Map<String, String> aoHashMap);

	public Integer createContractFinancialReplica(Map<String, String> aoHashMap);

	// changes for agency outbound interafce 6644
	public Integer markContractFinancialAsDeleted(Map<String, String> aoHashMap);

	public Integer fetchRateValidationRemngUnits(RateBean aoRateBean);

	// START || Added as a part of release 3.12.0 for enhancement request 6601
	public Integer isAmendmentRegisteredInFMS(String lsContractId);

	public Integer mergeContractForAmendmentFMSRegisteredContract(Map<String, String> aoHashMap);

	public Integer mergeContractFinancialForAmendmentFMSRegisteredContract(Map<String, String> aoHashMap);

	public Integer createContractFinancialReplicaFMSRegisteredContract(Map<String, String> aoHashMap);

	public Integer markContractFinancialAsDeletedFMSRegisteredContract(Map<String, String> aoHashMap);
	// END || Added as a part of release 3.12.0 for enhancement request 6601

	//Start Release 6 defect id 8480
	public Integer insertNewSubBudgetModificationDetailsInAmendment(Map<String, String> aoInputMap);
	//End Release 6 defect id 8480
	
	//Start R7 for modification
	public HashMap<String, Object> fetchConfiguredThreshold(String asContractId);
	
	public Integer insertAutoApprovalDetails(HashMap aoHashMap);
	
	public Integer fetchInfoForAutoApproval(String asSubBudgetID);
	
	public Integer updateApprovalDetailsInBudget(HashMap<String, Object> aoHashMap);
	
	public Integer updateShowInfoFlagInBudget(HashMap<String, Object> aoHashMap);
	
	public ContractBudgetBean calculatePercentageForAutoApproval(HashMap<String, Object> aoMap);
	
	public ContractBudgetBean calculatePercentageForCostCenterAutoApproval(HashMap<String, Object> aoMap);
	
	public String getAutoApproverUserNameForAgency();
	
	public String fetchparentBudgetAgencyId(String asContractId);
	//End R7 for modification
	
	// R7 modification auto approval
	
	public ContractBudgetBean sumOfModifiedUnitsForCostCenterAutoApproval(HashMap<String, Object> aoMap);

	public Integer fetchThresholdForBudgetCategories(HashMap<String, String> loHashMap);

	public Integer updateAutoApprovalDetails(HashMap<String, String> loHashMap);

	public String fetchBudgetModificationUrlCount(String asBudgetId);

	public Integer updateAutoApprovalReviewFlag(String asBudgetId);
	
	public String getStatusIdForModification(String asBudgetId);
	
	public List<CBGridBean> fetchApprovedModificationSubBudgetSummary(HashMap<String, String> aoHashmap);
	
	public String getFinalIsShowMessageFlag(HashMap<String, String> aoHashmap);
	
	public String getTotalModificationOfBaseBudget(HashMap<String, String> aoHashmap);
	
	public ContractBudgetBean fetchSubBudgetModificationAmt(HashMap<String, Object> aoMap);
	// End R7 : Modification Auto Approve
	
	//Start: added in R7 for Cost Center
		public List<CBServicesBean>fetchContractServicesModificationDetails(CBGridBean aoCBGridBeanObj);
		
		public List<CBServicesBean>fetchContractServicesModificationDetailsBase(CBGridBean aoCBGridBeanObj);
		
		public List<CBServicesBean>fetchContractServicesModificationDetailsMod(CBGridBean aoCBGridBeanObj);
		
		public List<CBServicesBean>fetchContractCostCenterModificationDetails(CBGridBean aoCBGridBeanObj);
		
		public List<CBServicesBean>fetchContractCostCenterModificationDetailsMod(CBGridBean aoCBGridBeanObj);
		
		public List<CBServicesBean>fetchContractCostCenterModificationDetailsBase(CBGridBean aoCBGridBeanObj);
		
		public Integer updateContractCostCentersModifyDetails(CBServicesBean aoCBGridBeanObj);
		
		public Integer insertContractCostCenterModifyDetails(CBServicesBean aoCBGridBeanObj);
		
		public CBServicesBean fetchContractCostCenterDetailsForValidation(String asId);
		
		public Integer updateContractServicesModifyDetails(CBServicesBean aoCBGridBeanObj);
		
		public Integer insertContractServicesModifyDetails(CBServicesBean aoCBGridBeanObj);
		
		public CBServicesBean fetchContractServicesDetailsForValidation(String asId);
		
		public BigDecimal fetchServicesModAmountTotal(HashMap<String, String> aoHashmap);
		
		public BigDecimal fetchPIModificationAmount(HashMap<String, String> aoHashmap);
		
		public BigDecimal fetchCostCenterModAmountTotal(HashMap<String, String> aoHashmap);
		
		public Integer mergeServicesReplica(Map<String, String> aoHashMap);
		
		public Integer mergeCostCenterReplica(Map<String, String> aoHashMap);
		
		public Integer createServicesReplica(Map<String, String> aoHashMap);
		
		public Integer createCostCenterReplica(Map<String, String> aoHashMap);
		
		public Integer markServiceAsDeleted(Map<String, String> aoHashMap);
		
		public Integer markCostCenterAsDeleted(Map<String, String> aoHashMap);
		
		public Integer mergeServicesForAmendment(Map<String, String> aoHashMap);
		
		public Integer mergeCostCenterForAmendment(Map<String, String> aoHashMap);
		
		public Integer insertAmendmentServicesToBase(Map<String, String> aoHashMap);
		
		public Integer updateBaseFlagForAmendServices(Map<String, String> aoHashMap);
		//End: Added in R7 for Cost Center
		// R7 changes for Program Income
		public List<CBProgramIncomeBean> fetchPIForModification(CBGridBean aoCBGridBeanObj);

		public Integer insertProgramIncomeModificationGrid(CBProgramIncomeBean aoCBCBProgramIncomeBeanObj);
		
		public String fetchIsOldPI(String asParentBudgetId);
		// R7 changes end
		public Integer linkServicesToBase(Map<String, String> aoHashMap);
		
		public Integer linkCostCenterToBase(Map<String, String> aoHashMap);	
		
		//Added for R7: Auto Update
		public Integer fetchCountForConfigurationChangeInUpdate(HashMap<String, String> aoHashmap);
		
		public Integer fetchCountForFiscalAmountChangeInUpdate(String asUpdateContractId);
		
		public String fetchUpdateBudgetId(HashMap<String, String> aoHashmap);
		
		public String getUpdateBudgeAutoApproveFlag(HashMap<String, String> aoHashmap);
		
		public List<String> getActiveFiscalYear(String asUpdateContractId);
		//End for R7: Auto Update
		// Added in R7 for 8916
		public Integer linkSiteDetailsToBase(Map<String, String> aoHashMap);
		
		//Added for R7:program income
		public BigDecimal getRemainingAmountModificationPI(CBProgramIncomeBean aoCBProgramIncomeBean);
		
		public String fetchIsOldPIForValidation(String asSubBudgetId);
		
		// Start: Added for defect QC 9151 R 7.3.2
		public String getAutoApproveFlag(HashMap<String, String> aoHashmap);
		public Integer resetBudgetAutoApprovalFlag(Map<String, String> aoHashMap);
		// End: Added for defect QC 9151 r 7.3.2

		// Start: QC 9152 r 7.5.0 Accenture fix: Update Task (Auto-Approval) Never Finished
		public Integer markContractFinacialsAsInactive(Map<String, String> aoHashMap);
		// End: QC 9152 r 7.5.0 Accenture fix: Update Task (Auto-Approval) Never Finished

        // [Start] R7.5.0 QC9146 Professional Service Grid issue for MOD
		public Integer mergeProfServicesModAmount(CBProfessionalServicesBean aoProfService);
        // [End] R7.5.0 QC9146 Professional Service Grid issue for MOD
		
		
		// Start:  QC 8394 R 7.9.0 Add/delete line to Unallocated Funds 
		public List<UnallocatedFunds> fetchModificationUnallocatedFundsToBaseLine(CBGridBean aoCBGridBeanObj);
		
		public int addModificationUnallocatedFunds(UnallocatedFunds aoUnallocatedFunds);
			    
	    public int deleteModificationUnallocatedFunds(UnallocatedFunds aoUnallocatedFunds);
	 
	    public int insertNewUnallocatedFundsForModification(UnallocatedFunds aoUnallocatedFunds);
	    
	    public UnallocatedFunds fetchUnallocatedFundsChildRecord(UnallocatedFunds aoUnallocatedFunds);
	    
	    public UnallocatedFunds fetchUnallocatedFundsParentRecord(UnallocatedFunds aoUnallocatedFunds);
		// End:  QC 8394 R 7.9.0 Add/delete line to Unallocated Funds 
	
 
}
