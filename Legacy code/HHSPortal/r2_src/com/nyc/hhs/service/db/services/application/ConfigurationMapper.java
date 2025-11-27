/**
 * 
 */
package com.nyc.hhs.service.db.services.application;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.AccountsAllocationBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.ContractCOFDetails;
import com.nyc.hhs.model.ContractFinancialBean;
import com.nyc.hhs.model.CostCenterServicesMappingList;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.model.FundingAllocationBean;
import com.nyc.hhs.model.PaymentAllocationBean;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.RefContractFMSBean;
import com.nyc.hhs.model.SiteDetailsBean;
import com.nyc.hhs.model.TaskDetailsBean;

/**
 * This is a configuration mapper class which has queries to fetch data
 */
public interface ConfigurationMapper
{

	public ProcurementCOF fetchProcurementCOFDetails(Map<String, String> aoQueryMap);

	public ProcurementCOF fetchProcurementAddendumCOFDetails(Map<String, String> aoQueryMap);

	public String fetchProcurementCOFStatus(String asProcurementID);

	public List<AccountsAllocationBean> fetchProcurementCoADetails(CBGridBean aoCbGridBean);

	public List<AccountsAllocationBean> fetchProcurementFundingSourceDetails(CBGridBean aoCbGridBean);

	public void insertProcurementCoADetails(AccountsAllocationBean aoCoABean);

	public void insertProcurementCoADetailsDuplicate(CBGridBean aoCbGridBean);

	public void updateProcurementCoADetails(AccountsAllocationBean aoCoABean);

	public void updateStatusForProcurement(TaskDetailsBean aoTaskDetailsBean);

	public void deleteRowsWithTypeUpdate(TaskDetailsBean aoTaskDetailsBean);

	public void insertTaskRowsAsTypeUpdate(TaskDetailsBean aoTaskDetailsBean);

	public void deleteRowsWithTypeTaskRows(TaskDetailsBean aoTaskDetailsBean);

	public void deleteProcFundRowsWithTypeUpdate(TaskDetailsBean aoTaskDetailsBean);

	public void insertProcFundTaskRowsAsTypeUpdate(TaskDetailsBean aoTaskDetailsBean);

	public void deleteProcFundRowsWithTypeTaskRows(TaskDetailsBean aoTaskDetailsBean);

	public void deleteProcurementCoADetails(AccountsAllocationBean aoCoABean);

	public void updateProcurementFundingSourceDetails(HashMap aoMap);

	public void insertProcurementFundingSourceDetails(CBGridBean aoCbGridBean);

	public Integer addContractConfFundingDetails(CBGridBean aoCBGridBean);

	public List<FundingAllocationBean> fetchContractConfFundingDetails(String asContractID);

	public Integer editContractConfFundingDetails(HashMap<String, String> aoSetClause);

	public Integer addContractAmendmentFundingDetails(CBGridBean aoCBGridBean);

	public List<FundingAllocationBean> fetchContractAmendmentFundingDetails(CBGridBean aoCBGridBean);

	public Integer editContractAmendmentFundingDetails(HashMap<String, String> aoSetClause);

	public List<AccountsAllocationBean> fetchContractConfCOADetails(CBGridBean aoCBGridBean);

	public List<AccountsAllocationBean> fetchContractConfCOADetailsAmendment(CBGridBean aoCBGridBean);

	public List<AccountsAllocationBean> fetchR2ContractConfCOADetails(CBGridBean aoCBGridBean);

	public List<AccountsAllocationBean> fetchContractConfCurrCOADetails(CBGridBean aoCBGridBean);

	public void addContractConfCOADetails(AccountsAllocationBean aoAccountsAllocationBean);

	public void delContractConfCOADetails(AccountsAllocationBean aoAccountsAllocationBean);

	public Integer editContractConfCOADetails(AccountsAllocationBean aoAccountsAllocationBean);

	public ProcurementCOF fetchContractConfigDetails(Map<String, Object> aoQueryMap);

	public ProcurementCOF fetchContractConfigDetailsForR3Contract(Map<String, Object> aoQueryMap);

	public Integer updateContractCofTaskStatus(TaskDetailsBean aoTaskDetailsBean);

	public Integer fetchCountOfTaskApproved(TaskDetailsBean aoTaskDetailsBean);

	public Integer updateContractStatus(TaskDetailsBean aoTaskDetailsBean);

	public Integer updateContractStatusCof(TaskDetailsBean aoTaskDetailsBean);

	public Integer updateAmendmentContractStatus(Map<String, Object> aoHashMap);

	public Integer updateAmendmentContractStatusCoF(HashMap<String, String> aoQueryMap);

	public Integer updateAmendmentBudgetStatus(TaskDetailsBean aoTaskDetailsBean);

	public Integer updateContractConfigurationTaskStatus(TaskDetailsBean aoTaskDetailsBean);

	public Integer updateContractStatusToPendingConfig(TaskDetailsBean aoTaskDetailsBean);

	public Integer updateContractCofTaskStatusToReturnForRevision(TaskDetailsBean aoTaskDetailsBean);

	public List<FundingAllocationBean> fetchContractConfSubBudgetDetails(Map<String, String> aoBudgetInfo);

	public List<FundingAllocationBean> fetchContractConfSubBudgetDetails1(Map<String, String> aoBudgetInfo);

	public List<FundingAllocationBean> fetchBudgetDetailsByFYAndContractId(Map<String, String> aoBudgetInfo);

	public List<FundingAllocationBean> fetchBudgetDetailsActiveOrApproved(Map<String, String> aoBudgetInfo);

	public List<ContractBudgetBean> fetchAmendmentBudgetDetails(Map<String, String> aoBudgetInfo);

	public void insertContractConfSubBudgetDetails(ContractBudgetBean aosubBudgetList);

	public void editContractConfSubBudgetDetails(ContractBudgetBean aosubBudgetList);

	public void delContractConfSubBudgetDetails(ContractBudgetBean aosubBudgetList);

	public String getSubBudgetsSumTotal(HashMap<String, String> aoBudgetInfo);

	public EPinDetailBean getDataWFbyProcId(String asProcId);

	public void setPCOFStatus(CBGridBean aoCBGridBean);

	public void insertNewBudgetDetails(ContractBudgetBean aosubBudgetList);

	public void insertNewAmendmentBudgetDetails(ContractBudgetBean aosubBudgetList);

	public void insertNewUpdateBudgetDetails(ContractBudgetBean aosubBudgetList);

	public void updateBudgetFYTotalBudgetAmount(ContractBudgetBean aosubBudgetList);

	public String fetchContractSourceId(TaskDetailsBean aoTaskDetailsBean);

	public List<AccountsAllocationBean> fetchContractConfUpdateDetails(CBGridBean aoCBGridBean);

	public List<AccountsAllocationBean> fetchContractConfAmendmentDetails(CBGridBean aoCBGridBean);

	public Integer editContractConfUpdateDetails(AccountsAllocationBean aoAccountsAllocationBean);

	public Integer editContractConfAmendmentDetails(AccountsAllocationBean aoAccountsAllocationBean);

	public Integer editContractConfAmendmentDetailsOld(AccountsAllocationBean aoAccountsAllocationBean);

	public void addContractConfAmendmentTaskDetails(AccountsAllocationBean aoAccountsAllocationBean);

	public void addContractConfUpdateTaskDetails(AccountsAllocationBean aoAccountsAllocationBean);

	public void fetchContractConfActualUpdateDetails(AccountsAllocationBean aoAccountsAllocationBean);

	public List<ContractFinancialBean> fetchedContractDetails(String asContractId);

	public Integer insertFetchedContractDetails(ContractFinancialBean aoContractFinancialBean);

	public List<ContractFinancialBean> fetchedContractFinancialDetails(String asContractId);

	public void insertFetchedContractFinancialDetails(ContractFinancialBean aoContractFinancialBean);

	public List<PaymentAllocationBean> fetchContractConfUpdateActualDetails(CBGridBean aoCBGridBean);

	public List<String> fetchFYAndContractId(String asContractId);

	public List<ContractBudgetBean> fetchContractConfUpdateSubBudgetDetails(Map<String, String> aoBudgetInfo);

	public List<ContractBudgetBean> fetchContractConfAmendSubBudgetDetails(Map<String, String> aoBudgetInfo);

	public void addContractConfUpdateBudgetDetails(ContractBudgetBean aoContractBudgetBean);

	public void addContractConfAmendmentBudgetDetails(ContractBudgetBean aoContractBudgetBean);

	public List<ContractBudgetBean> insertUpdatedSubBudgetDetails(Map<String, String> aoBudgetInfo);

	public void insertContractConfUpdateSubBudgetDetails(ContractBudgetBean aoContractBudgetBean);

	public void insertContractConfAmendmentSubBudgetDetails(ContractBudgetBean aoContractBudgetBean);

	public void editContractConfUpdateSubBudgetDetails(ContractBudgetBean aosubBudgetList);

	public void editContractConfAmendSubBudgetDetails(ContractBudgetBean aosubBudgetList);

	public String fetchInvoiceAmount(String asParentId);

	public String fetchModifiedAmountByParentId(ContractBudgetBean aoContractBudgetBean);

	public void insertContractConfSubBudgetDetailsWithParentId(ContractBudgetBean aosubBudgetList);

	public void updateBudgetForNewFYConfigurationTask(ContractBudgetBean aosubBudgetList);

	public Integer editNewFYConfCOADetails(AccountsAllocationBean aoAccountsAllocationBean);

	public String fetchFYPlannedAmount(Map<String, String> aoCBGridBeanMapInfo);

	public String fetchAmendmentFYPlannedAmount(Map<String, String> aoCBGridBeanMapInfo);

	public String fetchUpdateFYPlannedAmount(Map<String, String> aoCBGridBeanMapInfo);

	public ContractBudgetBean contractBudgetAmendFYData(Map<String, String> aoCBGridBeanMapInfo);

	public String getContractEndDate(String asContractId);

	public void editContractConfSubBudgetDetails1(ContractBudgetBean aosubBudgetList);
	
	/*Start: added in release 7 for defect 6596  */
	public void updateSubBudgetName(ContractBudgetBean aosubBudgetList);
	/*Ends: added in release 7 for defect 6596  */

	public String fetchBudgetIdIfExists(Map<String, String> aoBudgetMapInfo);

	public String fetchSubBudgetTotalAmount(Map<String, String> aoSubBudgetMapInfo);

	public Integer updateProcStatus(String asProcurementId);

	public String fetchSubBudgetAmountByParentId(ContractBudgetBean aoContractBudgetBean);

	public String fetchAllAmendedSubBudgetAmount(ContractBudgetBean aoContractBudgetBean);

	public List<AccountsAllocationBean> fetchContractConfUpdateActualDetailsBYFY(CBGridBean aoCBGridBean);

	public List<AccountsAllocationBean> fetchContractConfUpdateFYIAmount(CBGridBean aoCBGridBean);

	public void delContractConfUpdateTaskDetails(AccountsAllocationBean aoAccountsAllocationBean);

	public void delContractConfAmendTaskDetails(AccountsAllocationBean aoAccountsAllocationBean);

	public void deleteContractConfAmendmentDetails(AccountsAllocationBean aoAccountsAllocationBean);

	public String validateContractConfigUpdateAmount(List fiscalYearList, String asContractID);

	public String fetchFYPlannedAmount(CBGridBean aoCBGridBean);

	public List<AccountsAllocationBean> fetchContractConfUpdateNewDetails(CBGridBean aoCBGridBean);

	public String fetchSubBudgetParentId(ContractBudgetBean aosubBudgetbean);

	public String fetchSubBudgetParentIdAmendment(ContractBudgetBean aosubBudgetbean);

	public void mergeContractConfUpdateFinishTask(List fiscalYearList, String asContractID,
			TaskDetailsBean aoTaskDetailsBean);

	public void updateContractConfSubBudgetAmt(ContractBudgetBean aosubBudgetbean);

	public void delContractConfUpdateSubBudgetOldDetails(ContractBudgetBean aosubBudgetList);

	public void insertContractConfUpdateFinishTask(AccountsAllocationBean aoAccountsAllocationBean);

	public void updateContractConfFiscalAmt(AccountsAllocationBean aoAccountsAllocationBean);

	public void delContractConfUpdateNewRecordFinishTask(AccountsAllocationBean aoAccountsAllocationBean);

	public void delContractConfUpdateOldRecordFinishTask(AccountsAllocationBean aoAccountsAllocationBean);

	public void delContractConfUpdateSubBudgetDetails(ContractBudgetBean aosubBudgetbean);

	public void delContractConfAmendSubBudgetDetails(ContractBudgetBean aosubBudgetbean);

	public String checkContractDetails(HashMap<String, String> aoHashMap);

	public String fetchPlannedAmtForUpdatedContractId(Map<String, String> aoBudgetMapInfo);

	public String checkBudgetDetails(Map<String, String> aoBudgetMapInfo);

	public List<AccountsAllocationBean> fetchProcurementCoADetails(AccountsAllocationBean aoAccountsAllocationBean);

	public BigDecimal validateAmendmentAmount(AccountsAllocationBean aoAccountsAllocationBean);

	public BigDecimal fetchAmendmentAmountExceptChangedOne(AccountsAllocationBean aoAccountsAllocationBean);

	public BigDecimal fetchFiscalYearAmount(AccountsAllocationBean aoAccountsAllocationBean);

	// Enhancement 6414 release 3.10.0
	public BigDecimal fetchFiscalYearAmountUpdateConf(AccountsAllocationBean aoAccountsAllocationBean);

	public void delContractUpdatedId(String asContractId);

	public void updateBudgetStatus(String asContractId);

	// Release 3.6.0 Enhancement id 6484
	public ArrayList<String> fetchSubBudgetDetailsForUpdateConf(HashMap asContractId);

	public ArrayList<String> fetchSubBudgetDetailsForAmendmentConf(String asContractId);

	public ArrayList<SiteDetailsBean> fetchSubBudgetDetailsBudgetUpdateTask(String budgetId);

	public void softDeleteParent(SiteDetailsBean aoSiteDetailsBean);

	public void updateParentSubBudgetSite(SiteDetailsBean aoSiteDetailsBean);

	public void insertSubBudgetSiteDetailsForUpdate(Integer asSubBudgetId);

	public void updateBudgetConfiguration(TaskDetailsBean aoTaskDetailsBean);

	public void updateBudgetProcSelectionsMade(TaskDetailsBean aoTaskDetailsBean);

	public void updateContractProcSelectionsMade(TaskDetailsBean aoTaskDetailsBean);

	public String fetchContractSourceType(String asContractId);

	public Map fetchContractFiscalYears(String asContractId);

	public ProcurementCOF fetchProcurementCOFDetailsFinancials(String asProcurementID);

	public String fetchBaseContractId(String asContractId);

	public String fetchUpdateContractId(String asContractId);

	public void updateBudgetFiscalYearAmount(Map<String, String> aoBudgetMapInfo);

	public ProcurementCOF fetchProcurementAddendumCOFDetailsFinancials(String asProcurementID);

	public List<String> fechActiveApprovedFiscalYears(String asAmendContractId);

	public void copyProcurementFundingSourceDetails(CBGridBean aoCBGridBean);

	public String fetchContractType(String asContractId);

	public List<AccountsAllocationBean> fetchContractConfCOAOriginalDetails(CBGridBean aoCBGridBean);

	public List<AccountsAllocationBean> fetchContractConfCOADeletedRows(AccountsAllocationBean aoAccountsAllocationBean);

	public void updateR2BudgetStatusToPendSub(String asEvaluationPoolMappingId);

	public void updateR2ContractStatusToPendReg(String asEvaluationPoolMappingId);

	public void setOriginalProcurementValues(CBGridBean aoCBGridBean);

	public ProcurementCOF fetchProcurementOrigDetails(String asProcurementID);

	public void deleteRowsCoaDatesChanged(Map aoQueryMap);

	public void insertRowsCoaDatesChanged(Map aoQueryMap);

	public void deleteRowsFundingDatesChanged(Map aoQueryMap);

	public void insertRowsFundingDatesChanged(Map aoQueryMap);

	public BigDecimal fetchTotSubBudgetAmt(ContractBudgetBean aoContractBudgetBean);

	public ContractBudgetBean fetchAmendBudgetDetails(ContractBudgetBean aoContractBudgetBean);

	public ProcurementCOF fetchProcDatesPublishedAndAddendum(String asProcurementID);

	public String validateProcValueAndAllocatedValue(TaskDetailsBean aoTaskDetailsBean);

	public void insertBudgetCustomization(HashMap<String, String> aoHashMap);

	public void deleteBudgetCustomization(HashMap<String, String> aoHashMap);

	public List<String> fetchEntryTypeDetails(HashMap<String, String> aoHashMap);

	public void updateBudgetCustomization(HashMap<String, String> aoHashMap);

	public String getBudgetFromContractAndFiscalYEar(HashMap<String, String> aoHashMap);

	public Integer countEntryTypeFromCBC(HashMap<String, String> aoHashMap);

	public List<String> fetchEntryTypeDetailsForUpdateContract(HashMap<String, String> aoHashMap);

	public void insertBudgetCustomizationForUpdate(HashMap<String, String> aoHashMap);

	public void deleteBudgetCustomizationForUpdate(HashMap<String, String> aoHashMap);

	public void updateBudgetCustomizationForUpdate(HashMap<String, String> aoHashMap);

	public List<String> fetchEntryTypeDetailsForContractUpdateLanding(HashMap<String, String> aoHashMap);

	public void mergeBudCustomizationCBUpdateReview(HashMap<String, String> aoHashMap);

	public List<String> fetchEntryTypeDetailsForAmendment(HashMap<String, String> aoHashMap);

	public Integer getAmendFromBudCustomization(ContractBean aoContractBean);

	public void insertPdfBatchForAmendmentCof(HashMap loHashMap);

	public List<String> fetchEntryForBaseAmendMerge(ContractBean aoContractBean);

	public void mergeBaseBudCustomization(HashMap<String, String> aoHashMap);

	public List<String> fetchEntryTypeDetailsFromModification(HashMap<String, String> aoHashMap);

	public void deleteAmendmentBudgetRow(AccountsAllocationBean aoAccountsAllocationBean);

	public void deleteAmendmentSubBudgetRow(AccountsAllocationBean aoAccountsAllocationBean);

	public Map<String, Object> fetchAmendmentSubBudgetCount(AccountsAllocationBean aoAccountsAllocationBean);

	public Integer checkForNewFY(HashMap<String, String> aoHashMap);

	public List<String> getParentSubBudgetId(HashMap<String, String> aoHashMap);

	public List<String> getBaseSubBudgetId(HashMap<String, String> aoHashMap);

	public Integer checkLineItemDefaultEntries(ContractBudgetBean aoSubBudgetBean);

	public Integer deleteLineItemDefaultEntries(HashMap<String, String> aoQueryMap);

	public Map<String, Object> fetchContractDetails(CBGridBean aoCBGridBean);

	// Start of changes for release 3.2.0 enhancement 5684

	public void insertTaskRowsAsTypeApproved(TaskDetailsBean aoTaskDetailsBean);

	public void insertProcFundTaskRowsAsTypeApproved(TaskDetailsBean aoTaskDetailsBean);

	public String fetchPCOFStatus(String asProcurementID);

	public Integer fetchProcFinancialEntry(String asProcurementID);

	public void deleteRowsWithTypeApproved(TaskDetailsBean aoTaskDetailsBean);

	public void deleteProcFundRowsWithTypeApproved(TaskDetailsBean aoTaskDetailsBean);

	// End of changes for release 3.2.0 enhancement 5684

	public HashMap fetchBudgetDetailsForNT403(HashMap aoParamMap);

	// start of changes for release 3.8.0 enhancement 6483
	public void deleteBudgetCustForCancelUpdate(String asContractId);

	public void deleteSubbudgetForCancelUpdate(String asContractId);

	public void deleteBudgetForCancelUpdate(String asContractId);

	public void deleteConFinForCancelUpdate(String asContractId);

	public void deleteConFundingForCancelUpdate(String asContractId);

	public void deleteContractForCancelUpdate(String asContractId);

	public void deleteUpdateDocuments(String asContractId);

	public List<String> fetchUpdateContractDocs(String asContractId);

	public String fetchDiscrepencyDetailsForUpdateTask(String asContractId);

	// End of changes for release 3.8.0 enhancement 6483

	// [Start] release 3.9.0 enhancement 6524
	public List<ContractCOFDetails> getContractRenewalDetailInfo(String asContractId);

	// [End] release 3.9.0 enhancement 6524

	// start of changes for release 3.12.0 enhancement 6602

	public void deleteBudgetCustForCancelConfigureNewFY(ContractBudgetBean aoContractBudgetBean);

	public void deleteSubbudgetForCancelConfigureNewFY(ContractBudgetBean aoContractBudgetBean);

	public void deleteBudgetForCancelConfigureNewFY(ContractBudgetBean aoContractBudgetBean);

	public void copyFYAmountToPreviousAmountForFinancials(HashMap<String, String> aoParamMap);

	public void copyPreviousAmountToAmountForFinancials(ContractBudgetBean aoContractBudgetBean);

	public void deleteNewlyAddedContractFinancialsRows(ContractBudgetBean aoContractBudgetBean);

	public void copyFYAmountToPreviousAmountForFinFunding(HashMap<String, String> aoParamMap);

	public void copyPreviousAmountToAmountForFinFunding(ContractBudgetBean aoContractBudgetBean);

	public void deleteNewFYDocuments(ContractBudgetBean aoContractBudgetBean);

	public BigDecimal isAlreadyLaunchedFYTask(String asContractId);

	public List<String> fetcNewFYContractDocs(ContractBudgetBean aoContractBudgetBean);

	// release 3.12.0 enhancement 6601 11jan
	public List<String> getPendingAmendmentBudgetFiscalYearId(AccountsAllocationBean aoCoABean);

	public String getContractFinancialUpdateAmount(AccountsAllocationBean aoCoABean);

	public List<String> fetchFYAndContractIdAmendment(String asContractId);

	// release 3.12.0 enhancement 6601 11jan
	public List<String> fetchAmendmentBudgetIds(ContractBudgetBean aoContractBudgetBean);

	public Integer deletePersonnelServiceForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteOpSupportOthersForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteOpSupportForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteUtilitiesForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteProffServiceOthersForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteProffServiceForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteRentForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteContractedServiceForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteRateForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteMilestoneForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteUnallocatedForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteIndirectRateForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteProgIncomeOthersForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteProgramIncomeForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteFringeBenefitForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteEquipmentForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteSubBudgetSiteForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteSubBudgetForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteBudgetCustomizForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteBudgetDocForNewFYCancel(String asAmendmentBudgetId);

	public Integer deleteAssociatedBudgetsForNewFYCancel(String asAmendmentBudgetId);

	// end of changes for release 3.12.0 enhancement 6602

	// release 3.14.0
	public List<FundingAllocationBean> getNextNewFYBudgetDetails(Map<String, String> aoBudgetInfo);

	public Integer addContractConfAmendmentBudgetDetailsAddItsParent(ContractBudgetBean aoContractBudgetBean);

	public Integer addContractConfAmendmentBudgetDetailsForNextNewFy(ContractBudgetBean aoContractBudgetBean);

	public void editContractConfAmendSubBudgetDetailsNextNewFy(ContractBudgetBean aosubBudgetList);

	public List<ContractBudgetBean> fetchContractConfAmendSubBudgetDetailsNextNewFY(Map<String, String> aoBudgetInfo);

	public Integer updateAmendmentBudgetStatusNextNewFY(ContractBudgetBean aoContractBudgetBean);

	public List<String> selectAmendmentBudgetStatusNextNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer updateAmendmentContractStatusNextNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer updateAmendmentRegisteredInFMSBudgetStatusNextNewFY(ContractBudgetBean aoContractBudgetBean);

	public List<ContractBudgetBean> budgetAmendmentRegisteredInFms(ContractBudgetBean aoContractBudgetBean);

	public Integer mergeContractFinancialForAmendmentFMSRegisteredContract(Map<String, String> aoHashMap);
	
	public Integer deleteBudgetWithAllSub(AccountsAllocationBean aoAccountsAllocationBean);
	public void delContractConfAmendSubBudgetDetailsNextNewFY(ContractBudgetBean aosubBudgetbean);

	public void delContractConfAmendSubBudgetParentDetailsNextNewFY(ContractBudgetBean aosubBudgetbean);

	String getParentSubBudgetIdNextNewFY(ContractBudgetBean aosubBudgetbean);

	String getSubBudgetName(ContractBudgetBean aosubBudgetbean);

	List<String> getSubBudgetNameForSameAmendmentBudget(ContractBudgetBean aosubBudgetbean);

	Integer getAmendSubBudgetNameSameWithAnotherAmendment(ContractBudgetBean aosubBudgetbean);

	Integer updateParentWhileEditingAmendment(ContractBudgetBean aosubBudgetbean);

	Integer updateParentWhileEditingAmendmentWhenParentNotExist(ContractBudgetBean aosubBudgetbean);

	String getParenSubBudgetIdForAlreadyLinkedAmendment(ContractBudgetBean aosubBudgetbean);

	public void editContractConfAmendSubBudgetDetailsParentNameNextNewFy(ContractBudgetBean aosubBudgetList);

	public Integer mergeContractForAmendmentFMSRegisteredContractNextNewFY(Map<String, String> aoHashMap);

	public Integer mergeContractFinancialForAmendmentFMSRegisteredContractNextNewFY(Map<String, String> aoHashMap);

	public Integer createContractFinancialReplicaFMSRegisteredContractNextNewFY(Map<String, String> aoHashMap);

	public Integer markContractFinancialAsDeletedFMSRegisteredContractNextNewFY(Map<String, String> aoHashMap);

	public Integer getParentSubBudgetCount(Map<String, String> aoHashMap);

	public void updateSubbudgetForCancelConfigureNewFY(ContractBudgetBean aoContractBudgetBean);

	public void updateBudgetForCancelConfigureNewFY(ContractBudgetBean aoContractBudgetBean);

	public Integer subBudgetExistingAmendmentErrorCheck(ContractBudgetBean aoContractBudgetBean);

	public List<String> fetchFYAndContractIdAmendmentNegative(String asContractId);

	public HashMap<String, String> getAmendmentStartEndDate(String asAmendmentContractId);

	public List<AccountsAllocationBean> fetchContractConfAmendmentDetailsPartialMerging(CBGridBean aoCBGridBean);

	public void updateBudgetCustomizationOnNewFyBudget(ContractBudgetBean loContractBudgetBean);
	// release 3.14.0
	//Start: Added in Release 5.1.0 for FTE calculations
	@SuppressWarnings("rawtypes")
	public Integer updateUsesFte(HashMap loContractId);
	//End: Added in Release 5.1.0 for FTE calculations
	
	//release 6: Added for in bound interface out of year amendment start
	public String fetchBudgetIdIfExistsForAmendBatch(Map<String, String> aoBudgetMapInfo);
	
	public void updateParentBudgetIdForEtlAmendment(Map<String, String> aHashMap);
	//release 6: Added for in bound interface out of year amendment end
	
	// Start : Added in R7 for 8645 part 3: updates the contract table column REQUEST_PARTIAL_MERGE= 2 when its current value is 1.
    
    public void updatePartialMergeAction (String lsContractId);
    public ArrayList<SiteDetailsBean> fetchSubBudgetListDetails(String asBudgetId);
    // End : Added in R7 for 8644
 
	// Start: Added in R7 for Cost Center
	public List<CostCenterServicesMappingList> fetchServicesForAgency(CBGridBean aoCBGridBean);
	
	public List<CostCenterServicesMappingList> fetchUpdateServicesForAgency(CBGridBean aoCBGridBean);
	
	public List<CostCenterServicesMappingList> fetchSelectedServicesForUpdate(CBGridBean aoCBGridBean);
	
	public List<CostCenterServicesMappingList> fetchSelectedServices(CBGridBean aoCBGridBean);
	
	public List<CostCenterServicesMappingList> fetchUpdatedSelectedServices(CBGridBean aoCBGridBean);

	public String fetchServicesConfFlag(CBGridBean aoCBGridBean);
	
	public Integer fetchServicesCount(CBGridBean aoCBGridBean);
	
	public Integer insertDefaultServicesConfigurations(CBGridBean aoCBGridBean);
	
	public Integer insertDefaultServicesConfFromPreviousFy(CBGridBean aoCBGridBean);
	
	public Integer fetchIfProgramIncomeAlreadySelected(HashMap<String, String> aHashMap);
	
	public Integer insertAllServicesForNewFy(CBGridBean aoCBGridBean);
	
	public String fetchAgencyServiceFlag(CBGridBean aoCBGridBean);
	
	// Start: added in R7 for multiple Amendments for Defect 8831
	public List<String> fetchBudgetListsDetails(ContractBudgetBean aosubBudgetList);
	// End: added in R7 for multiple Amendments for Defect 8831
	
	/*Start: added in release 7 for defect 8884 :it will fetch and delete newly added sub budgets during NFY task on cancellation of NFY task  */
	public List<String> fetchSubBudgetListsDetails(ContractBudgetBean aosubBudgetList);
	
	public void deleteNewlyAddedSubBudgets(ContractBudgetBean aosubBudgetList);
	/*Ends: added in release 7 for defect 8884  */
	// Added in R7 for cost center
	public void deleteServicesConfigration(ContractBudgetBean aoContractBudgetBean);
	
	// Start QC 9145 R 8.8 Registered Contracts not marked as registered in Accelerator
	public RefContractFMSBean fetchRefContractFMS(String contractId);
	public RefContractFMSBean fetchDocVersionAndCommodityCodeFMS(RefContractFMSBean aoRefContractFMSBean);
	public RefContractFMSBean fetchVendorTinFMS(RefContractFMSBean aoRefContractFMSBean);
	public String fetchAgencyIdFMS(RefContractFMSBean aoRefContractFMSBean);
	public String fetchEinId(String organizationId);
	public Integer updateContractWithCTandStatus(RefContractFMSBean aoRefContractFMSBean);
	public Integer updateContractWithCTandFMSInfo(RefContractFMSBean aoRefContractFMSBean);
    // End QC 9145 R 8.8 Registered Contracts not marked as registered in Accelerator

}
