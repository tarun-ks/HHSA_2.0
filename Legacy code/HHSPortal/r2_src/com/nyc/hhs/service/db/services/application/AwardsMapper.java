package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.AcceptProposalTaskBean;
import com.nyc.hhs.model.AwardBean;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.EvaluationGroupAwardBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.SelectionDetailsSummaryBean;

/**
 * This mapper is used for xml mapping with the awardsMapper.xml all the method
 * which defined here mapped with awardsMapper.xml
 * 
 * */

public interface AwardsMapper
{
	public AwardBean fetchAwardDetails(Map<String, String> aoParamMap);

	public List<ExtendedDocument> fetchAwardDocuments(Map<String, String> aoParamMap);

	public Integer updateAwardApproveStatus(String asProcurementId);

	public ExtendedDocument displayAwardDocuments(ExtendedDocument asDocumentBean);

	public EvaluationBean fetchAwardReviewStatus(Map<String, Object> aoParamMap);

	public Integer insertAwardDocumentDetails(Map<String, Object> aoParamMap);

	public Integer removeAwardDocuments(Map<String, Object> aoParamMap);

	Integer updateAwardStatus(Map<String, String> aoStatusInfoMap);

	AwardBean fetchAwardsDetails(String asAwardId);

	public String fetchAwardId(Map<String, Object> aoParamMap);

	List<AwardBean> awardAndContracts(Map<String, String> aoAwardMap);

	Integer updateRelatedProposal(Map<String, String> aoStatusInfoMap);

	Integer updateModifiedFlagEvalResults(Map<String, String> aoStatusInfoMap);

	Integer updateAwardReviewStatus(Map<String, String> aoStatusInfoMap);

	AwardBean aptProgressView(Map<String, String> aoViewProgressMap);

	public List<ExtendedDocument> fetchDocumentsForAwardDocTask(HashMap<String, String> aoParamMap);

	public List<ExtendedDocument> fetchAwardDocumentTypeForTask(String asProcurementId);

	public Integer removeAwardTaskDocs(HashMap<String, String> aoAwardMap);

	public Integer saveAwardDocumentTypes(ExtendedDocument aoDocTypeBean);

	public Integer insertAwardTaskDocDetails(HashMap<String, String> aoReqPropsMap);

	AwardBean viewAptProgress(Map<String, String> aoViewProgressMap);

	Integer awardAndContractsCount(Map<String, String> loAwardMap);

	// assign award epin
	List<AwardBean> fetchAwardEPinDetails(String asProcurementId);

	AwardBean fetchAmountProviderDetails(String asContractId);

	Integer assignAwardEpin(Map<String, String> loAwardEpinMap);

	public Integer updateAwardDetailsFromTask(HashMap<String, String> aoAwardMap);

	public AwardBean fetchAwardDetailsForFinance(Map<String, String> aoParamMap);

	public Integer updateAwardDocDetails(Map<String, Object> aoParamMap);

	Integer removeAwardDocumentFromVault(String asDocumentId);

	// NT219 fix
	public Integer checkIfAllReqAwardDocsUploaded(HashMap aoHashMap);

	public String getAgencyIdForAwardId(String asAwardId);

	public Integer updateProviderStatus(Map<String, String> aoStatusInfoMap);

	public Integer getContractRegistered(String asProcurementId);

	public AcceptProposalTaskBean fetchAwardAmountForSelectedProposals(Map<String, Object> loDataMap);

	public String fetchOrgNameFromContractId(String asContractId);

	public Integer contractCofApproved(HashMap<String, Object> loHmReqExceProp);

	public Integer contractBudgetApproved(HashMap<String, Object> loHmReqExceProp);

	List<EvaluationGroupAwardBean> fetchEvaluationGroupAwardsList(EvaluationGroupAwardBean aoEvaluationGroupAwardBean);

	Integer fetchEvaluationGroupAwardsListCount(String asProcurementId);

	public String fetchEvalPoolMappingId(String asProcurementId);

	public String fetchDefaultConfigId(String asProcurementId);

	public List<String> awardDocumentList(HashMap<String, Object> aoHmReqExceProp);

	List<SelectionDetailsSummaryBean> fetchSelectionDetailsSummaryList(
			SelectionDetailsSummaryBean aoSelectionDetailsSummaryBean);

	Integer fetchSelectionDetailsSummaryListCount(String asProcurementId);

	Integer updateContractStartEndDate(Map<String, Object> aoDataMap);

	Integer insertDefaultAwardDocDetails(Map<String, Object> aoDataMap);

	public Map<String, String> fetchOrgDetailsFromContractId(String asContractId);

	Integer insertDocumentDownloadRequest(Map<String, String> aoDataMap);

	Integer updateDocumentDownloadRequest(Map<String, String> aoDataMap);

	List<ExtendedDocument> fetchDocumentDetailsforZipBatchProcess(HashMap aoHMArgs);

	Integer isFinancialCheck(Map<String, String> aoParamMap);

	Integer updateProcStatusForDocs(Map<String, String> aoDataMap);

	List<ExtendedDocument> fetchDocumentDetailsforZipDeleteBatchProcess();

	Integer updateDocumentDownloadRequestForBatch(Map<String, String> aoDataMap);

	Integer updateDocumentDownloadDeleteRequestForBatch(Map<String, String> aoDataMap);

	public String fetchDocumentStatus(String asStatusId);

	// Start || Changes done for Enhancement #6429 for Release 3.4.0
	public Integer insertAgencyAwardDocsDetails(HashMap<String, String> aoReqPropsMap);

	public Integer removeAgencyAwardDocs(Map<String, String> aoReqPropsMap);

	public List<ExtendedDocument> fetchAgencyAwardDocuments(Map<String, String> aoParamMap);

	public List<String> fetchAgencyAwardDocumentIds(HashMap<String, String> aoParamMap);

	// End || Changes done for Enhancement #6429 for Release 3.4.0

	// Start Added in R5
	public Integer updateFinalizedAmount(Map<String, String> aoReqPropsMap);

	public Integer updateAwardNegotiationDetails(HashMap<String, String> aoAwardMap);
	// End Added in R5
}
