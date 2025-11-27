package com.nyc.hhs.service.db.services.application;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.AgencyTaskBean;
import com.nyc.hhs.model.AssignementDetailsBean;
import com.nyc.hhs.model.ContractDetailsBean;
import com.nyc.hhs.model.EvaluationReassignDetailsBean;
import com.nyc.hhs.model.EvaluationStatusBean;
import com.nyc.hhs.model.Evaluator;
import com.nyc.hhs.model.FinancialWFBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProposalDetailsBean;

/**
 * Mapper interface for all the P8Service methods
 * 
 */
public interface HHSComponentP8Mapper
{
	/**
	 * Method to find the proposal Ids
	 * @param aoHashMap Properties Map
	 * @return list of all the proposal Ids
	 */
	public List<String> fetchProposalIdList(Map aoHashMap);

	/**
	 * Method to find the Procurement Summary Details
	 * @param aoHashMap Properties Map
	 * @return Procurement bean containing all the Procurement Summary Details
	 */
	public Procurement fetchProcurementSummaryProvider(Map aoHashMap);

	/**
	 * Method to find the Proposal Details
	 * @param aoHashMap Properties Map
	 * @return ProposalDetailsBean containing all the Proposal Details
	 */
	public ProposalDetailsBean fetchProposalDetails(Map aoHashMap);

	/**
	 * Method to find the multiple proposal details
	 * @param aoHashMap Properties Map
	 * @return list of all the Proposal Details
	 */
	public List<ProposalDetailsBean> fetchMultipleProposalsDetails(Map aoHashMap);

	/**
	 * Method to find the multiple proposal details
	 * @param aoHashMap Properties Map
	 * @return list of all the Proposal Details
	 */
	public List<ProposalDetailsBean> fetchRequiredProposalsDetails(Map aoHashMap);

	/**
	 * Method to find the Proposal Details for Proposal Id
	 * @param aoHashMap Properties Map
	 * @return list of all the Proposal Details
	 */
	public List<ProposalDetailsBean> fetchMultipleProposalsDetailsByProposal(Map aoHashMap);

	/**
	 * Method to find the contract details
	 * @param aoHashMap Properties Map
	 * @return list of all the contract details
	 */
	public List<ContractDetailsBean> fetchMultipleContractDetails(Map aoHashMap);

	/**
	 * Method to find the internal evaluators
	 * @param aoHashMap Properties Map
	 * @return list of all the internal evaluator details
	 */
	public List<EvaluationReassignDetailsBean> fetchEvaluationReassignInternalDetails(Map aoHashMap);

	/**
	 * Method to find the external evaluator details
	 * @param aoHashMap Properties Map
	 * @return list of all the external evaluator details
	 */
	public List<EvaluationReassignDetailsBean> fetchEvaluationReassignExternalDetails(Map aoHashMap);

	/**
	 * Method to find the internal evaluator details
	 * @param aoHashMap Properties Map
	 * @return list of all the internal evaluators details
	 */
	public List<String> fetchEvaluationStatusIdsByInternalEvaluatorDetails(Map aoHashMap);

	/**
	 * Method to find the external evaluator details
	 * @param aoHashMap Properties Map
	 * @return list of all the external evaluators details
	 */
	public List<String> fetchEvaluationStatusIdsByExternalEvaluatorDetails(Map aoHashMap);

	/**
	 * Method to find the Proposal Config Details
	 * @param aoHashMap Properties Map
	 * @return list of all the Proposal Config Details
	 */
	public List<EvaluationStatusBean> fetchProposalConfigDetails(Map aoHashMap);

	/**
	 * Method to delete the evaluation score
	 * @param evaluationStatusId evaluation status id
	 * @return list of all the internal evaluators
	 */
	public Integer deleteEvaluationScore(String evaluationStatusId);

	/**
	 * Method to delete the evaluation status
	 * @param evaluationStatusId evaluation status id
	 * @return list of all the internal evaluators
	 */
	public Integer deleteEvaluationStatus(String evaluationStatusId);

	/**
	 * Method to find the proposal config details
	 * @param procurementId procurement id
	 * @return list of all the proposal config details
	 */
	public List<EvaluationStatusBean> fetchProposalConfigDetails(String procurementId);

	/**
	 * Method to insert the evaluation status
	 * @param evaluationStatusBean evaluation status bean
	 * @return number of records inserted
	 */
	public Integer insertEvaluationStatus(EvaluationStatusBean evaluationStatusBean);

	/**
	 * Method to find the evaluation Id for internal evaluators
	 * @param aoHashMap Properties Map
	 * @return evaluationId for the internal evaluators
	 */
	public String fetchEvalIdInternal(Map aoHashMap);

	/**
	 * Method to find the evaluation Id for external evaluators
	 * @param aoHashMap Properties Map
	 * @return evaluationId for the external evaluators
	 */
	public String fetchEvalIdExternal(Map aoHashMap);

	/**
	 * Method to find the accepted proposal Ids
	 * @param aoHashMap Properties Map
	 * @return list of all the accepted proposal Ids
	 */
	public List<String> fetchAcceptedProposalID(Map aoHashMap);

	/**
	 * Method to find the contract details for a contract ID
	 * @param aoHashMap Properties Map
	 * @return list of all the contract details
	 */
	public FinancialWFBean findContractDetailsByContractIdForWF(Map aoHashMap);

	/**
	 * Method to find the internal evaluations List
	 * @param aoHashMap Properties Map
	 * @return list of all the internal evaluations List
	 */
	List<Evaluator> fetchInternalEvaluationsList(Map aoHashMap);

	/**
	 * Method to find the external evaluations List
	 * @param aoHashMap Properties Map
	 * @return list of all the external evaluations List
	 */
	List<Evaluator> fetchExternalEvaluationsList(Map aoHashMap);

	/**
	 * Method to find the contract IDs
	 * @param aoHashMap Properties Map
	 * @return list of all the Contract Ids
	 */
	public List<String> fetchContractIdByProcurement(Map aoHashMap);

	/**
	 * Method to find the count of selected proposals
	 * @param aoHashMap Properties Map
	 * @return count of the selected proposals
	 */
	public Integer fetchCountofSelectedProposals(Map aoHashMap);

	/**
	 * Method to find the evaluation Ids for the reopened evaluation tasks
	 * @param aoHashMap Properties Map
	 * @return List of evaluation IDs
	 */
	public List<String> fetchReopenedEvaluationTaskIds(Map aoHashMap);

	/**
	 * Method to delete the evaluation settings External data
	 * @param asProcurementId procurement id
	 */
	public Integer deleteEvaluatorExternal(HashMap aoHashMap);

	/**
	 * Method to delete the evaluation settings Internal data
	 * @param asProcurementId procurement id
	 */
	public Integer deleteEvaluatorInternal(HashMap aoHashMap);

	/**
	 * Method to update the evaluatior external table
	 * @param asProcurementId procurement id
	 */
	public Integer updateEvaluatorExternal(HashMap aoHashMap);

	/**
	 * Method to update the evaluatior internal table
	 * @param asProcurementId procurement id
	 */
	public Integer updateEvaluatorInternal(HashMap aoHashMap);

	/**
	 * Method to fetch the Accelerator Comments
	 * @param aoHashMap
	 */
	public String fetchAcceleratorComments(Map aoHashMap);

	/**
	 * Method to fetch the Agency Comments
	 * @param aoHashMap
	 */
	public String fetchAgencyComments(Map aoHashMap);

	/**
	 * Method to fetch the Evaluator Ids
	 * @param aoHashMap
	 */
	public List<String> fetchEvaluatorIdsEvaluation(HashMap aoHashMap);

	/**
	 * Method to fetch the Evaluator Ids for review scores
	 * @param aoHashMap
	 */
	public List<String> fetchEvaluatorIdsReviewScore(String asProposalId);

	/**
	 * Update the evaluation result set with the correct modified flag
	 * 
	 * @param aoHashMap
	 * @return
	 */
	public Integer updateModifiedFlag(Map aoHashMap);

	/**
	 * Inserts the data into the payment table
	 * 
	 * @param aoHashMap
	 * @return
	 */
	public Integer insertIntoPayment(Map aoHashMap);

	/**
	 * Inserts the data into the payment allocation table
	 * 
	 * @param aoHashMap
	 * @return
	 */
	public Integer insertIntoPaymentAllocation(Map aoHashMap);

	/**
	 * Retrieves the vendor assignment information
	 * 
	 * @param aoHashMap
	 * @return
	 */
	public List<AssignementDetailsBean> fetchAssignments(Map aoHashMap);

	/**
	 * Updates the process flag on the record in the payment table
	 * 
	 * @param aoHashMap
	 * @return
	 */
	public Integer updateProcessFlagOnPayment(Map aoHashMap);

	/**
	 * Retrieves the fiscal year for the given budgetId
	 * 
	 * @param aoHashMap
	 * @return
	 */
	public Integer fetchFisalYearId(Map aoHashMap);

	/**
	 * Retrieves the budgetId by the contractId and the fiscalyearId
	 * 
	 * @param aoHashMap
	 * @return
	 */
	public String fetchBudgetId(Map aoHashMap);

	/**
	 * Retrieves invoiceNumber by invoiceId
	 * 
	 * @param aoHashMap
	 * @return
	 */
	public String fetchInvoiceNumber(Map aoHashMap);

	/**
	 * Retrieves advanceNumber by advanceId
	 * 
	 * @param aoHashMap
	 * @return
	 */
	public String fetchAdvanceNumber(Map aoHashMap);

	/**
	 * Retrieves the amount for the invoiceId
	 * 
	 * @param aoHashMap
	 * @return
	 */
	public Double fetchAmountforInvoiceWF(Map aoHashMap);

	/**
	 * Retrieves the amount for the budgetadvanceId
	 * @param aoHashMap
	 * @return
	 */
	public Double fetchAmountforAdvanceWF(Map aoHashMap);

	public Integer updateMultipleContractsDetails(String asContractId);

	public Map fetchContractInfo(String asContractId);

	public FinancialWFBean findProcEpinR3ContractForWF(String asContractId);

	public String fetchAgencyCityProviderEmail(String asUserId);

	public List<AgencyTaskBean> fetchAgencyCityProviderName();
	

	public List<AgencyTaskBean> fetchServceStartEndDate();
	
	public String fetchBaseBudIdFromModBudId(String asModBudId);

	public String fetchBaseContractId(String asBudgetId);

	public Integer getSelPropCountForEvalPoolMappingId(Map aoHashMap);
	
	public String fetchCompetitionPoolTitle(String asContractId);
   
	//Made changes in this method for defect 6453 and Release 3.4.0
	public Integer fetchProcurementStatus(HashMap aoHashMap);
	
	//	Made changes in this method for Enhancement 6405 and Release 3.3.0
	//Changes reverted for Enhancement 6405 as a part of Release 3.3.1.
	
	//Release 3.6.0 Enhancement 6405    
	public List<String>   getVendorAddressIdFromTCondFirst(HashMap aoHashMap);
	public List<String>   getVendorAddressIdFromTCondSecond(HashMap aoHashMap);
	public List<String>   getVendorAddressIdFromTCondSecondHalf(HashMap aoHashMap);
	public List<String>   getVendorAddressIdFromTCondThird(HashMap aoHashMap);
	public List<String>   getVendorAddressIdFromTCondFourth(HashMap aoHashMap);
	
	public List<String>   getPaymentVendorAddressIdFromTCondFirst(HashMap aoHashMap);
	public List<String>   getPaymentVendorAddressIdFromTCondSecond(HashMap aoHashMap);
	public List<String>   getPaymentVendorAddressIdFromTCondSecondHalf(HashMap aoHashMap);
	public List<String>   getPaymentVendorAddressIdFromTCondThird(HashMap aoHashMap);
	public List<String>   getPaymentVendorAddressIdFromTCondFourth(HashMap aoHashMap);
	//Release 3.8.0 Enhancement 6536 
	public Map  fetchBudgetFiscalYearInformation(String asPaymentId);
	public Integer insertIntoPaymentAllocationForBFYgreaterThanFY(Map aoHashMap);
	
	// Added for Payment Batch - Updating Accounting Line
	public Date selectMaxInterimPerionEndDate();
	public List<String> fetchPendingApprovalPayments();
	public Integer updatePendingApprovalPayments(Integer aiCurrYear);
	public Integer insertIntoPAOutsideInterimForBFYgreaterThanFY(Map aoHashMap);
	public Integer deletePendingApprovalPayments();
	public Integer insertIntoPAOutsideInterimForBFYgreaterThanFY1(Map aoHashMap);
	//method added as a part of release 3.8.1 enhancement 6536
	public Integer insertIntoPAOutsideInterimForFYgreaterThanBFY(Map aoHashMap);
	
	//method added as a part of release 3.12.0 enhancement 6578
	public List<String> fetchPendingApprovalPaymentsAtLevel1(Map aoHashMap);
	public List<String> fetchUpdatedAccountingLines();
	public Integer deletePendingApprovalPaymentsAtLevel1(Map aoHashMap);
	public Integer updateBatchInProgressFlag(Map aoHashMap);
	public Integer updateBatchInProgressFlagForActiveRecords();
	// Added in R5
	public ContractDetailsBean fetchNegotiationsContractsDetails(Map aoHashMap);
	//Method Added in R7-To Fix Defect #7211(delete payment Accounting lines while doing 'Return for Revision' apart from level 1 ) 
	public List<String> fetchPendingApprovalPaymentsNotAtLevel1(Map aoHashMap);

}
