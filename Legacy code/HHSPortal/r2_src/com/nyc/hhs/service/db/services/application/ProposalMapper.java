/**
 * 
 */
package com.nyc.hhs.service.db.services.application;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.AutoCompleteBean;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.ProposalQuestionAnswerBean;
import com.nyc.hhs.model.ProviderSelectionBean;
import com.nyc.hhs.model.SiteDetailsBean;
import com.nyc.hhs.model.UserBean;
/**
 * This is mapper class for Proposal which has queries to get data
 */
public interface ProposalMapper
{
	ProposalDetailsBean fetchProposalDetails(String asProposalId);

	List<SiteDetailsBean> fetchProposalSiteDetails(Map<String, String> aoParameterMap);

	// Release 3.6.0 Enhancement id 6484
	List<SiteDetailsBean> fetchSubBudgetSiteDetails(Map<String, String> aoParameterMap);

	Integer recordBeforeRelease(Map<String, String> aoParameterMap);

	String fetchSubBudgetStatusId(Map<String, String> aoParameterMap);

	List<Map<String, String>> fetchAllOrganizationMembers(String asOrganizationId);

	Map<String, String> fetchMemberDetails(Map<String, String> aoData);

	public String fetchEvaluatorDetails(String asEvaluationStatusId);

	Integer updateProposalDetails();

	Integer insertProposalDetails();

	Date getDueDate(String asProcurementId);

	Integer updateProposalStatus(HashMap<String, String> aoPropMap);

	String getProposalSubmittedDate(HashMap<String, String> aoPropMap);

	Integer cancelProposal(String asProposalId);

	ProposalDetailsBean checkProposalStatus(String asProposalId);

	Integer retractProposal(String asProposalId);

	List<String> fetchProposalDocumentId(String asProposalId);

	ProposalDetailsBean checkProcurementProposalStatus(String asProposalId);

	Integer deleteProposalDocument(String asProposalId);

	Integer deleteProposalSite(String asProposalId);

	Integer deleteProposalQuestionResponse(String asProposalId);

	Integer getProposalDetailsCount(String asProposalId);

	Integer getProposalDocumentCount(HashMap<String, String> aoPropMap);

	Integer getProposalQuesResponseCount(HashMap<String, String> aoPropMap);

	Integer getProposalSiteCount(HashMap<String, String> aoPropMap);

	List<ExtendedDocument> fetchRequiredOptionalDocuments(Map<String, String> aoParamMap);

	List<ProposalDetailsBean> getProposalSummary(ProposalDetailsBean aoProposalDetailsBean);

	Integer getProposalCount(ProposalDetailsBean aoProposalDetailsBean);

	List<ProposalDetailsBean> getProposalSummaryProposalDueDate(ProposalDetailsBean aoProposalDetailsBean);

	Map<String, String> getProposalSummaryStatusDetails(String asProcurementId);

	Integer insertNewProposalDetails(Map<String, String> aoProposalDetailMap);

	public Integer updateProposalDetails(ProposalDetailsBean aoProposalDetailsBean);

	public void insertProposalDetails(ProposalDetailsBean aoProposalDetailsBean);

	public Integer updateProposalAnswers(ProposalQuestionAnswerBean aoProposalQuestionAnswerBean);

	public void insertProposalAnswers(ProposalQuestionAnswerBean aoProposalQuestionAnswerBean);

	public void deleteProposalSiteDetails(SiteDetailsBean aoSiteDetailsBean);

	public void deleteSubBudgetSiteDetails(SiteDetailsBean aoSiteDetailsBean);

	public void insertProposalSiteDetails(SiteDetailsBean aoSiteDetailsBean);

	public void insertSubBudgetSiteDetails(SiteDetailsBean aoSiteDetailsBean);

	public void updateProposalSiteDetails(SiteDetailsBean aoSiteDetailsBean);

	public void updateSubBudgetSiteDetails(SiteDetailsBean aoSiteDetailsBean);

	List<Map<String, String>> getProposalDocInfo(String asProcurementId);

	String getProposalId();

	List<ExtendedDocument> getProposalDocumentList(Map<String, String> aoParamMap);

	Integer insertProposalDocumentDetails(Map<String, Object> aoParamMap);

	Integer removeProposalDocs(Map<String, Object> aoParamMap);

	public Map<String, String> getProposalTitle(String asProposalId);

	public List<ExtendedDocument> fetchProposalDocuments(HashMap<String, String> aoProcProposalMap);

	public Map<String, String> fetchProposalDetailsForTask(String asProposalId);

	// Made changes for enhancement 6467 release 3.4.0
	public Integer fetchRequiredQuestionDocumentCount(String asProposalId);

	public Integer updateProposalDocumentStatus(ExtendedDocument aoDocumentObj);

	public List<UserBean> fetchPermittedUsers(HashMap<String, String> aoUserMap);

	public List<ExtendedDocument> getProposalDocuments(Map<String, String> aoParamMap);

	public String fetchProcurementTitle(String asProcurementId);

	public Integer updateProposalTaskStatus(Map<String, String> aoProposalMap);

	public Integer updateProposalDocumentStatusForTask(HashMap<String, String> aoProposalMap);

	public Map<String, String> fetchProcTitleAndOrgId(String asProposalId);

	String fetchProposalStaffId(String asProposalId);

	ProposalDetailsBean fetchNewProposalDetails(String asProposalId);

	public String fetchProposalTitle(String asProposalId);

	public void updateDocumentStatusSubmitted(HashMap<String, String> aoPropMap);

	public String fetchProposalStatusId(String asProposalId);

	public String fetchProcurementStatusId(String asProposalId);

	void resetDocumentStatusCompleted(String asProposalId);

	public Integer updateApprovedProviderStatus(Map<String, String> aoMap);

	public Map<String, String> getApprovedProviderDetailForProposal(Map<String, String> aoMap);

	public List<String> getProposalDocumentStatus(Map<String, String> aoParamMap);

	public Integer setDocumentStatusCompleted(Map<String, String> aoParamMap);

	public List<String> getOrgIdsForSelectedProposals(HashMap<String, String> aoProposalMap);

	public Integer updateProposalStatusFromTask(String procurementId);

	Integer updateProposalPreviousStatus(HashMap<String, String> aoPropMap);

	public String getProposalReviewStatusId(String asProposalId);

	Integer updateProposalDocDetails(Map<String, Object> aoParamMap);

	Integer removeProposalDocsFromVault(String asDocumentId);

	public Integer fetchCountofSelectedProposals(HashMap<String, String> aoDataMap);

	public Integer updateModifiedFlagFromAward(HashMap<String, String> aoDataMap);

	public List<ProviderSelectionBean> fetchProviderIdFirstRound(HashMap<String, String> aoDataMap);

	public List<ProviderSelectionBean> fetchProviderIdSecondRound(HashMap<String, String> aoDataMap);

	public Integer insertProposalDocForSubmittedProposal(ExtendedDocument aoDocumentObj);

	List<Map<String, String>> fetchAllCompetitionPool(String asProcurementId);

	public Integer updateEvaluationGroupForProposal(Map<String, Object> aoParamMap);

	public Map<String, Object> getProposalAndPoolStatus(String asProposalId);

	Integer updatePropStatFromTaskForPoolId(String asEvalPoolMappingId);

	Integer getProposalStatus(Map<String, String> aoMap);

	String fetchEvaluationSentFlag(String asProposalId);

	// Method to insert BAFO document in BAFO_DOCUMENT table
	Integer insertBAFODocumentDetails(Map<String, Object> aoParamMap);

	Integer moveCompetitionPoolIdFromTemp(Map<String, Object> aoParamMap);

	ExtendedDocument fetchAwardDocDetails(Map<String, String> inputParam);

	public List<String> fetchProposalId(Map<String, String> aoParamMap);

	public List<String> fetchProposalDocumentsForZip(HashMap<String, Object> loParamMap);

	ProposalDetailsBean fetchVersionInformation(Map<String, String> aoParameterMap);

	Integer getVersionNoForProposalDocuments(Map<String, String> aoParamMap);

	Integer updateDocVersionNo(Map<String, String> aoParamMap);

	// Start || Changes done for Enhancement #6577 for Release 3.10.0
	Integer updateProposalStatusForCancelComp(HashMap aoParamMap);

	Integer updateCompetitionPoolCancelled(HashMap aoParamMap);

	List<String> fetchProposalForCancelComp(HashMap aoParamMap);

	HashMap<String, String> fetchCompPoolInfo(HashMap aoParamMap);

	Integer updateCancelledContract(HashMap aoParamMap);

	Integer updateCancelledBudget(HashMap aoParamMap);

	Integer updateEvalGroupCancelled(HashMap aoParamMap);

	String checkCompPoolStatus(String asProposalId);

	String checkCompPoolWithoutEvalGroup(String asProposalId);

	// End || Changes done for Enhancement #6577 for Release 3.10.0
	// R5 starts
	String fetchProposalCount(ProposalDetailsBean aoProposalDetailsBean);

	List<ProposalDetailsBean> fetchProposalDetailForCity(ProposalDetailsBean aoProposalDetailsBean);

	List<AutoCompleteBean> fetchProcurmentTitle(Map<String, String> aoParamMap);

	List<AutoCompleteBean> fetchCompetitionPool(Map<String, String> aoParamMap);

	List<EvaluationBean> fetchFinalizeAwardScores(HashMap<String, Object> aoDataMap);

	List<EvaluationBean> fetchFinalizeAwardOtherScores(HashMap<String, Object> aoDataMap);
	
	public List<ExtendedDocument> fetchProposalDocumentsForEvaluation(HashMap<String, String> aoProcProposalMap);
	// R5 ends
	
	//[Start] R7.6 QC 8899
	   List<Map<String, String>> fetchAllCompetitionPoolWithProposalId(HashMap aoParamMap);
    //[END] R6.3 QC 6627  

		
}