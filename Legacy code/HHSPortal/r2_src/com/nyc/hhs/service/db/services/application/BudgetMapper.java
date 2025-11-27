package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.BudgetAdvanceBean;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.ContractBudgetBean;
import com.nyc.hhs.model.FundingAllocationBean;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.TaskDetailsBean;
/**
 *This is a mapper class to execute queries and get
 *data for the Budget functionality
 */
public interface BudgetMapper
{

	public List<BudgetList> fetchBudgetListForProvider(BudgetList aoBudgetBean);
	
	public String fetchBudgetListForProviderFandFP(BudgetList aoBudgetBean);

	public List<BudgetList> fetchBudgetListForAgency(BudgetList aoBudgetBean);

	public List<BudgetList> fetchBudgetListForCity(BudgetList aoBudgetBean);

	public int numberOfAmendmentsInProgress(String asBudgetId);

	public List<String> contractAmendmentsIdsInProgress(String asBudgetId);

	public List<String> contractUpdateIdsInProgress(String asBudgetId);

	public int numberOfBudgetAmendmentsOrModificationsOrUpdatesInProgress(HashMap<String, String> asBudgetId);

	public int numberOfInvoicesInProgress(String asBudgetId);

	public int numberOfPaymentsInProgress(String asBudgetId);
	
	//Enhancement 6591 release 3.10.0
	public int numberOfPaymentsInProgressWhenInvoiceApproved(String asBudgetId);

	public Integer fetchBudgetListForProviderCount(BudgetList aoBudgetBean);

	public Integer fetchBudgetListForAgencyCount(BudgetList aoBudgetBean);

	public Integer fetchBudgetListForCityCount(BudgetList aoBudgetBean);

	public ProcurementCOF fetchContractConfigDetails(Map<String, Object> loQueryMap);

	public List<FundingAllocationBean> fetchContractConfSubBudgetDetails(Map<String, String> loBudgetInfo);

	public void insertContractConfSubBudgetDetails(List<ContractBudgetBean> aosubBudgetList);

	public void editContractConfSubBudgetDetails(List<ContractBudgetBean> aosubBudgetList);

	public void delContractConfSubBudgetDetails(String asSubBudgetId);

	public BudgetAdvanceBean fetchRequestAdvance(String asBudgetId);

	public Integer setModificationBudgetStatus(String asBudgetId);

	public String fetchWorkflowIdForBudget(String asBudgetId);

	public ProcurementCOF fetchContractCofTaskDetails(String asContractId);

	public Integer fetchAdvanceCount();

	public Integer insertBudgetAdvanceDetail(BudgetAdvanceBean aoBudgetAdvanceBean);

	public ProcurementCOF fetchBaseAmendmentContractAmount(String asContractId);

	public Integer getBudgetAdvanceNextSeq();

	public ProcurementCOF fetchR2ContractCofDetails(String asContractId);

	public Map fetchR3ContractCofDetails(String asContractId);

	public Integer updateSubmittedInfoForCOFDoc(TaskDetailsBean aoTaskDetailsBean);

	public Integer updateApprovedInfoForCOFDoc(TaskDetailsBean aoTaskDetailsBean);

	public Integer fetchAdvanceNumberCount(String asAdvanceNumber);

	public Integer numberOfNegAmendmentsInProgress(String asBudgetId);

	public List<String> contractNegAmendmentsIdsInProgress(String asBudgetId);

	public List<HashMap> fetchBudgetAdvanceFromETL();

	public void updateBudgetAdvanceIdForBatch(HashMap aoHMWFRequiredProps);
	
	public String fetchBaseContractAmount(String asContractId);
	
	// Release 3.2.0, enhancement :6262
	public String fetchContractAmountForValidation(String asBudgetId);
	
	//R7 Start: Added for R7
	public ContractBudgetBean fetchDetailsForBudget(String asBudgetId);
	//R7 Ended
	
	//added in R7
	public List<BudgetList> fetchBudgetListForModificationsBudget(BudgetList aoBudgetBean);
	
	public Integer fetchModificationsBudgetListCount(BudgetList aoBudgetBean);
	
	// R7 Start
	public List<String> fetchBudgetIdsForDeletion(Map<String, Object> loBudget);
			
	//Start: Added in R7 for Cost Center
	public void updateBudgetServicesConfig(HashMap aoHMWFRequiredProps);
	
	public void insertUpdatedServicesConf(HashMap aoHMWFRequiredProps);
	
	public void updatedServicesConf(HashMap aoHMWFRequiredProps);
	
	public String fetchBaseBudgetStatus(CBGridBean aoCBGridBean);
	//End: Added in R7 for Cost Center
	//Start: Added in R7 for defect 8705
	public Integer deleteServices(String asBudgetId);
	public Integer deleteServicesBudget(String asBudgetId);
	public Integer deleteCostCenter(String asBudgetId);
	public Integer deleteServicesConfig(String asBudgetId);
	
	// Added for Program Income in R7
	public void updateIsOldPiFlagForNewFyFinish(HashMap aoHMWFRequiredProps);
	//End
	//Start: Added in R7 for defect 8644
	public List<String> fetchBudgetList(String contractId);
	//public List<String> fetchFiscalYearsList(String aoAgencyId);
	//public List<ContractList> getPartialMergeRequestList(HashMap asHMReqdProp);
	//End
	
	/** BEGIN Fix Multi-Tab Browsing QC8691 R7.1.0 */
    public String getBudgetIdFromSubBudget(String subBudgetId);
    /** END Fix Multi-Tab Browsing QC8691 R7.1.0 */
    //[Start] QC 9490 R 8.4 validate  Budget Update(s) for deletion 
    public Integer countBudgetUpdateApproved (String asContractId);
    //[End] QC 9490 R 8.4 validate  Budget Update(s) for deletion 

    /*--   [Start] QC9681 R9.5   Contract Update Config Muliti-Tab Browsing   -*/
	public List<HashMap<String, String>> fetchContractUpdateBudgetStatus(CBGridBean aoCBGridBean);
    /*--   [End] QC9681 R9.5   Contract Update Config Muliti-Tab Browsing   -*/

    /*--   [Start] QC9605 R9.6   Contract Update Config Muliti-Tab Browsing   -*/
	public void cancelBudgetMod(HashMap<String, String> asBudgetInfo);
    /*--   [End] QC9605 R9.6   Contract Update Config Muliti-Tab Browsing   -*/


}
