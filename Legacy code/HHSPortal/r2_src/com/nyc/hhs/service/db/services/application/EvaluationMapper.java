/**
 * 
 */
package com.nyc.hhs.service.db.services.application;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.AcceptProposalTaskBean;
import com.nyc.hhs.model.AutoCompleteBean;
import com.nyc.hhs.model.AwardsContractSummaryBean;
import com.nyc.hhs.model.DocumentVisibility;
import com.nyc.hhs.model.EvaluationBean;
import com.nyc.hhs.model.EvaluationDetailBean;
import com.nyc.hhs.model.EvaluationFilterBean;
import com.nyc.hhs.model.EvaluationGroupsProposalBean;
import com.nyc.hhs.model.EvaluationSummaryBean;
import com.nyc.hhs.model.Evaluator;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.ProposalDetailsBean;
import com.nyc.hhs.model.ProposalFilterBean;

/**
 * This mapper interface is used for xml mapping with the EvaluationMapper.xml
 * all the method which defined here mapped with EvaluationMapper.xml
 * 
 * */

public interface EvaluationMapper
{
	List<EvaluationBean> fetchProviderEvaluationScores(Map<String, String> aoProposalDetails);

	Map<String, String> fetchProviderProposalHeader(String asProposalId);

	List<EvaluationBean> fetchEvaluationResultsSelections(EvaluationFilterBean aoEvalBean);

	int fetchEvaluationResultsCount(EvaluationFilterBean aoEvalBean);

	EvaluationBean fetchReqProposalDetails(String asProposalId);

	Integer updateSelectedProposalAwardAmount(EvaluationBean aoEvaluationBean);

	Integer updateSelectedProposalComments(EvaluationBean aoEvaluationBean);

	Integer updateProposalReviewStatus(Map<String, Object> aoMap);

	Integer updateNotSelectedProposalAwardAmount(EvaluationBean aoEvaluationBean);

	Integer updateNotSelectedProposalStatus(EvaluationBean aoEvaluationBean);

	String fetchProcurementValue(String asProcurementId);

	Map<String, String> fetchAwardAmount(Map<String, String> aoInputMap);

	Integer fetchFinalizeProcurementCount(String asProcurementId);

	String fetchProcurementStatus(String asProcurementId);

	Integer updateProcurementStatus(Map<String, String> aoParamMap);

	Integer updateproposalstatusnonresponsive(Map<String, Object> aoMarkProposalNonResponsiveMap);

	Integer updateAwardReviewStatus(Map<String, Object> aoProcurementInfoMap);

	Integer insertAwardReviewStatus(Map<String, Object> aoProcurementInfoMap);

	List<EvaluationBean> fetchEvaluationDetails(EvaluationBean aoEvaluationBean);

	Integer fetchProposalCount(EvaluationBean aoEvaluationBean);

	Integer updateProposalStatus(Map<String, String> aoParamMap);

	Integer updateEvaluatorCount(Map<String, String> aoParamMap);

	List<Evaluator> fetchEvaluationTaskDetails(String asProcurementId);

	String fetchReviewAwardcomment(String asProposalId);

	Boolean updateProposalStatus(EvaluationBean aoEvaluationBean);

	List<ExtendedDocument> getAwardDocumentsInfo(String asAwardId);

	List<Evaluator> fetchEvaluationSettingsInternal(String asProcurementId);

	List<Evaluator> fetchEvaluationSettingsExternal(String asProcurementId);

	List<AutoCompleteBean> fetchInternalEvaluatorUsers(Map<String, String> aoInputParamMap);

	List<AutoCompleteBean> fetchExternalEvaluatorUsers(Map<String, String> aoInputParamMap);

	List<EvaluationBean> fetchEvaluationResultsScores(HashMap<String, Object> aoDataMap);

	List<EvaluationBean> getEvaluationScores(Map<String, List<ProposalDetailsBean>> aoProposalIdMap);

	List<ProposalFilterBean> fetchProposalComments(String asProposalId);

	Map<String, String> fetchAwardReviewComments(String asEvalPoolMappingId);

	Integer fetchEvaluationScoreSum(String asProcurementId);

	Integer countEvaluationSettingsUsers(String asProcurementId);

	Integer deleteEvaluationResults(Map<String, Object> aoParamMap);

	Integer deleteEvaluationStatus(Map<String, Object> aoParamMap);

	Integer deleteEvaluationScore(Map<String, Object> aoParamMap);

	List<EvaluationBean> fetchEvaluationCriteriaDetails(Map<String, String> aoParamMap);

	// R5 starts : updated return type to List
	List<Map<String, String>> fetchAccoComments(Map<String, String> aoACCOMap);

	// R5 ends : updated

	List<EvaluationBean> fetchEvaluatorCommentsDetails(Map aoEvalCommentsMap);

	List<EvaluationBean> fetchEvaluationScoresDetails(Map aoEvalCommentsMap);

	/**
	 * Below method is added as per enhancement 5415. This method fetches
	 * Evaluation score, criteria and comments Details corresponding to a
	 * evaluator
	 * @param aoQueryMap input map
	 * @return List
	 */
	List<EvaluationBean> fetchEvaluationScoreDetailsForEvaluator(Map<String, Object> aoQueryMap);

	// R5: update parameter to Map<String,Object>
	List<EvaluationBean> displayEvaluationScoresDetails(Map<String, Object> aoEvalCommentsMap);

	List<Map<String, String>> getDBDDocsList(Map<String, String> aoMap);

	Integer checkDBDDownloadAllowed(String asProcurementId);

	Integer insertEvaluationStatus(EvaluationDetailBean aoEvaluationDetailBean);

	List<EvaluationDetailBean> fetchIntExtProposalDetails(Map<String, String> aoInputParam);

	/**
	 * Method to save the internal evaluation list
	 * 
	 * @param aoInternalEvaluator object of all input parameters
	 * @return integer to save the number the number of rows
	 */
	Integer saveInternalEvaluationDetails(Evaluator aoInternalEvaluator);

	/**
	 * Method to save the external evaluation list
	 * 
	 * @param aoExternalEvaluator object of all input parameters
	 * @return integer to save the number the number of rows
	 */
	Integer saveExternalEvaluationDetails(Evaluator aoExternalEvaluator);

	/**
	 * Method to find the number of evaluators
	 * @param asProcurementId procurement id
	 * @return Integer count of all the evaluators
	 */
	Integer getEvaluationCount(String asProcurementId);

	/**
	 * Method to identify whether evaluation task has been send or not
	 * @param asProcurementId procurement id
	 * @return Integer of the evaluation task send
	 */
	Integer findEvaluationTaskSent(Map<String, String> loDataMap);

	/**
	 * Method to identify whether evaluation review task has been send or not
	 * @param asProcurementId procurement id
	 * @return Integer of the evaluation review task send
	 */
	Integer getEvaluationReviewScore(Map<String, String> loDataMap);

	/**
	 * Method to find the internal evaluators
	 * @param asProcurementId procurement id
	 * @return list of all the internal evaluators
	 */
	List<Evaluator> getInternalEvaluationsList(Map<String, String> loProcurementMap);

	/**
	 * Method to find the external evaluators
	 * @param asProcurementId procurement id
	 * @return list of all the external evaluators
	 */
	List<Evaluator> getExternalEvaluationsList(Map<String, String> loProcurementMap);

	/**
	 * Method to get the agency id based on the procurement id
	 */
	Map<String, Object> getProcurementAgencyId(String asProcurementId);

	/**
	 * This method is used to delete the internal evaluators
	 * @param aoInternalEvaluatorMap input map
	 * @return Integer
	 */
	Integer deleteEvaluationInternal(Map<String, Object> aoInternalEvaluatorMap);

	/**
	 * This method is used to delete the internal evaluators
	 * @param aoInternalEvaluatorMap input map
	 * @return Integer
	 */
	Integer deleteEvaluationSettingsInternal(Map<String, Object> aoDataMap);

	/**
	 * This method is used to delete the external evaluators
	 * @param aoInternalEvaluatorMap input map
	 * @return Integer
	 */
	Integer deleteEvaluationExternal(Map<String, Object> aoDataMap);

	/**
	 * This method is used to delete the external evaluators
	 * @param aoInternalEvaluatorMap input map
	 * @return Integer
	 */
	Integer deleteEvaluationSettingsExternal(Map<String, Object> aoDataMap);

	/**
	 * This method is used to delete the internal evaluators after evaluation
	 * @param aoRemovedEvaluatorMap input map
	 * @return Integer
	 */
	Integer deleteEvaluatorInternalAfterEvaluation(Map<String, Object> aoRemovedEvaluatorMap);

	/**
	 * This method is used to delete the external evaluators after evaluation
	 * @param aoRemovedEvaluatorMap input map
	 * @return Integer
	 */
	Integer deleteEvaluatorExternalAfterEvaluation(Evaluator aoEvaluator);

	/**
	 * This method is used to update the internal evaluators after evaluation
	 * @param aoRemovedEvaluatorMap input map
	 * @return Integer
	 */
	Integer updateEvaluatorInternalAfterEvaluation(Evaluator aoEvaluator);

	/**
	 * This method is used to update the external evaluators after evaluation
	 * @param aoRemovedEvaluatorMap input map
	 * @return Integer
	 */
	Integer updateEvaluatorExternalAfterEvaluation(Evaluator aoEvaluator);

	List<HashMap<String, Object>> getDetailsToLaunchAcceptProposalWF(String asProcurementId);

	List<HashMap<String, String>> fetchExtAndIntEvaluator(String asProcurementId);

	List<AcceptProposalTaskBean> fetchProposalDetailsForEvaluationTask(String asProcurementId);

	/**
	 * This method is used to fetch the evaluation details
	 * @param asParamaterMap EvcaluationBean
	 * @return List EvaluationBean
	 */
	List<EvaluationBean> fetchEvaluationScoreDetail(Map<String, String> aoParamaterMap);

	/**
	 * This method is used to update the score for
	 * @param aoEvalBean EvcaluationBean
	 * @return Integer
	 */
	Integer updateScoreDetails(EvaluationBean aoEvalBean);

	/**
	 * This method is used to insert the score for
	 * @param aoEvalBean EvcaluationBean
	 * @return Integer
	 */
	Integer insertScoreDetails(EvaluationBean aoEvalBean);

	/**
	 * @param asProcurementId
	 * @return
	 */
	Integer fetchReviewScoreCount(Map<String, String> aoParamaterMap);

	/**
	 * @param asProcurementId
	 * @return
	 */
	EvaluationBean fetchProcurementDates(HashMap<String, Object> asDataMap);

	/**
	 * @param aoEvaluationBean
	 * @return
	 */
	Integer fetchTotalEvaluationComplete(EvaluationBean aoEvaluationBean);

	/**
	 * @param aoEvaluationBean
	 * @return
	 */
	Integer fetchTotalEvaluationInProgess(EvaluationBean aoEvaluationBean);

	/**
	 * @param lsProviderName
	 * @return
	 */
	List<String> fetchProviderNameList(String asProviderName);

	public EvaluationBean fetchSelectionCommentsForAwardTask(HashMap<String, String> aoCommentsMap);

	/**
	 * @param asProcurementId
	 * @return
	 */
	public Integer fetchNoOfProviders(Map<String, Object> aoInputParam);

	/**
	 * @param asProcurementId
	 * @return
	 */
	public Integer fetchNoOfProposals(Map<String, Object> aoInputParam);

	/**
	 * @param loInputParam
	 * @return
	 */
	public Integer updateProcurementForCloseSubmissions(Map<String, Object> loInputParam);

	/**
	 * @param asProcurmentId
	 * @return
	 */
	List<Integer> getProposalStatusIdCount(EvaluationFilterBean aoEvaluationBean);

	/**
	 * @param asProcurmentId
	 * @return
	 */
	String getAwardReviewStatusId(EvaluationFilterBean aoEvaluationBean);

	Integer updateReturnForRevision(Map<String, String> aoStatusMap);

	Integer updateDocReturnForRevision(Map<String, String> aoStatusMap);

	Integer updateEvaluationStatusReturned(Map<String, String> aoStatusMap);

	Map sendNotification(String asProposalId);

	public Integer checkForSendEvalTaskButton(String asProcurementId);

	Map<String, Object> fetchAwardStatusId(EvaluationBean aoEvalBean);

	Integer updateModifiedFlagEvalResults(EvaluationBean aoEvalBean);

	public Integer countEvaluationUsers(String asProcurementId);

	List<String> fetchEvaluationIntStatusId(String statusId);

	List<String> fetchEvaluationExtStatusId(String statusId);

	Integer updateProposalStatusAccForEval(Map loStatusInfo);

	AcceptProposalTaskBean fetchProcurementDetailsForAwardWF(Map<String, Object> aoMap);

	Integer updateEvaluationStatus(HashMap<String, Object> aoEvalMap);

	void updateEvaluationReviewDetails(EvaluationBean aoEvalBean);

	void finishEvaluationReviewsStatus(Map<String, Object> aoMap);

	void finishEvaluationReviewsStatusCompleted(Map<String, Object> aoMap);

	void insertEvaluationResult(Map<String, String> aoMap);

	Integer updateEvaluationResult(Map<String, String> aoMap);

	Integer modifyProposalStatus(Map<String, String> aoModifyProposalStatusMap);

	void deleteFromEvaluationScoreInternal(Map<String, Object> aoRemovedEvaluatorMap);

	void deleteFromEvaluationStatusInternal(Map<String, Object> aoRemovedEvaluatorMap);

	void deleteFromEvaluationScoreExternal(Evaluator aoEvaluator);

	void deleteFromEvaluationStatusExternal(Evaluator aoEvaluator);

	Integer insertSelectedProposalComments(EvaluationBean aoEvaluationBean);

	List<Integer> deletedInternalEvalautionStatusId(Map<String, Object> aoRemovedEvaluatorMap);

	Integer deletedExternalEvalautionStatusId(Evaluator aoEvaluator);

	List<Integer> fetchInternalEvaluatorId(String asProcurementId);

	List<Integer> fetchExternalEvaluatorId(String asProcurementId);

	Integer updateNotSelectedProposalComments(EvaluationBean aoEvaluationBean);

	String fetchAwardAppDate(EvaluationFilterBean aoEvaluationFilterBean);

	List<EvaluationDetailBean> fetchEvaluatorsList(Map<String, String> aoParamMap);

	List<String> fetchUserEmailIds(String asProposalId);

	String fetchReturnedScoresUserEmailIds(String asEvaluationStatusId);

	Integer updateAddDelFlag(Map<String, Object> aoParamMap);

	Timestamp getUpdatedProposalDueDate(String asProcurementID);

	Integer fetchEvaluationStatusCount(Map<String, String> aoParamMap);

	public Integer updateProcurementTableRecords(String asProcurementId);

	Integer fetchReqDocCount(Map<String, String> aoParamMap);

	public Integer fetchNotNonResponsiveCount(Map aoProcMap);

	Integer updateEvaluationSentFlag(Map<String, Object> aoParamMap);

	String fetchEvaluationSentFlag(String asProcurementId);

	String fetchUpdatedAwardAmount(Map<String, Object> aoDataMap);

	Integer fetchEvaluatorCount(Map<String, Object> aoDataMap);

	Map<String, String> fetchProposalAndOrgName(String asProposalId);

	Integer evaluationVersionArchive(Map<String, Object> aoQueryMap);

	Integer evaluationScoreArchive(Map<String, Object> aoQueryMap);

	Integer evaluationGenCommentArchive(Map<String, Object> aoQueryMap);

	List<EvaluationSummaryBean> fetchEvaluationSummary(EvaluationSummaryBean aoEvaluationSummaryBean);

	List<EvaluationGroupsProposalBean> fetchEvaluationGroupProposal(EvaluationGroupsProposalBean loEvalGroupProposalBean);

	List<Map<String, String>> fetchEvaluationGroupsProcurement(Map<String, String> aoDataMap);

	List<Map<String, String>> fetchCompetitionPoolTitle(Map<String, String> aoDataMap);

	Map<String, String> fetchGroupTitleAndDate(String asEvaluationPoolMappingId);

	Map<String, String> fetchGroupTitleAndDateGroup(Map<String, Object> aoDataMap);

	Integer updateEvalGroupStatus(Map<String, Object> aoDataMap);

	Integer updateCompPoolStatus(Map<String, Object> aoDataMap);

	List<Map<String, Object>> fetchProposalCountAndCompId(Map<String, String> aoDataMap);

	Integer fetchEvaluationSummaryCount(EvaluationSummaryBean aoEvaluationSummaryBean);

	Integer fetchEvalGroupProposalCount(EvaluationGroupsProposalBean aoEvalGroupsProposalBean);

	String fetchEvaluationGroupId(String asProcurementId);

	public Integer getMinPoolMappingStatusId(Map aoParamMap);

	public HashMap<String, Object> checkSendEvalVisiblityStatus(Map<String, String> aoParamMap);

	public Integer checkCancelEvalVisibilityStatus(String asEvaluationPoolMappingId);

	public Integer deleteAwardData(Map<String, Object> aoParamMap);

	public String fetchRfpReleasedBeforeR4Flag(String asProcurementId);

	public Integer getMinGroupStatusId(Map<String, String> aoDataMap);

	Integer updateProcurementStatusBasedOnGroup(Map<String, String> aoDataMap);

	public Integer updateEvalPoolMappingStatus(Map<String, Object> aoDataMap);

	public List<AwardsContractSummaryBean> fetchGroupAwardsContracts(
			AwardsContractSummaryBean aoAwardsContractSummaryBean);

	public Integer fetchGroupAwardsContractsCount(AwardsContractSummaryBean aoAwardsContractSummaryBean);

	List<Map<String, Object>> getProposalCountAndCompId(Map<String, Object> aoDataMap);

	Integer updateCompPoolStatusInPropResubmit(Map<String, Object> aoDataMap);

	String fetchEvaluationGroupStatus(Map<String, Object> aoDataMap);

	Integer checkIfPublishedReleased(String asProcurementId);

	Integer removeBafoDocsFromVault(String asDocumentId);

	public Integer updateDefaultConfigurationId(Map<String, Object> aoInputParam);

	List<Map<String, String>> fetchEvaluationGroupsWithAwards(Map<String, String> aoDataMap);

	String fetchEvalGroupIdFromPoolMappingId(String asEvaluationPoolMappingId);

	Integer fetchNotNonResponsivePropCount(Map<String, Object> aoDataMap);

	Integer fetchNonResponsiveCompPoolCount(Map<String, Object> aoDataMap);

	Integer updateNonResponsiveEvalGroup(Map<String, Object> aoDataMap);

	Integer updateEvalGroupWithVersionInfo(Map<String, Object> aoDataMap);

	// Added as a part of release 3.6.0 for enhancement request 5905
	Integer getEvaluationProgressing(Map<String, Object> loDataMap);

	Integer updateEvalProgressStatus(Map<String, String> aoParamMap);

	Map<String, String> getEvalProgressStatus(Map<String, String> loDataMap);

	Integer getAllEvalProgressStatusFlag(Map<String, String> loDataMap);

	// Start || Changes done for Enhancement #6577 for Release 3.10.0
	HashMap<String, String> fetchCompTitleAndProcTitle(Map<String, String> aoParamMap);

	List<String> fetchProvidersInCompetition(Map<String, String> aoParamMap);

	List<String> fetchContractsForCancellingTasks(HashMap<String, String> aoProcurementInfoMap);

	Integer fetchRegOrClosedContractsCount(HashMap<String, String> aoProcurementInfoMap);

	// End || Changes done for Enhancement #6577 for Release 3.10.0

	// START || Added as a part of release 3.11.0 for enhancement request 5978
	HashMap<String, String> fetchInfoForReturnedPropNotificatn(String aoProposalId);

	HashMap<String, String> fetchLastComment(String aoProposalId);

	// END || Added as a part of release 3.11.0 for enhancement request 5978

	// Start || Changes done for Enhancement #6574 for Release 3.10.0
	List<String> getAwardConfigDocs(EvaluationFilterBean aoEvaluationSummaryBean);

	List<String> fetchUpdatedContracts(Map<String, Object> aoProcurementInfoMap);

	Integer updateModifiedFlagForOrg(EvaluationBean aoEvalBean);

	String fetchEvalPoolMappingStatus(String asEvaluationPoolMappingId);

	Integer fetchSelectedProposals(String asEvaluationPoolMappingId);

	Integer fetchContractCount(String asEvaluationPoolMappingId);

	// End || Changes done for Enhancement #6574 for Release 3.10.0

	// Start || R5 code
	Integer insertDefaultDocumentVisibility(Map<String, Object> aoDataMap);

	Integer updateHiddenDocumentVisibility(Map<String, Object> aoDataMap);

	List<DocumentVisibility> fetchDocumentVisibilityDetails(Map<String, String> aoDataMap);

	List<DocumentVisibility> fetchDocumentVisibilityDetailsAfterRelease(Map<String, String> aoDataMap);

	Integer updateDocumentVisibility(DocumentVisibility aoDocumentVisibility);

	Integer insertDocumentVisibility(DocumentVisibility aoDocumentVisibility);

	Integer updateScoreChangeToDefault(Map<String, Object> aoQueryMap);

	List<EvaluationBean> fetchEvaluationRoundScoreDetails(Map<String, String> aoParamaterMap);

	List<EvaluationBean> fetchRoundDropdownDetails(Map<String, Object> aoParamaterMap);

	List<EvaluationBean> fetchEvalScoreOfSelectRound(Map<String, String> aoParamaterMap);

	List<EvaluationBean> fetchAllRoundDropdownDetails(Map<String, Object> aoParamaterMap);

	List<EvaluationDetailBean> fetchEvaluationProgress(Map<String, Object> aoParamaterMap);

	Integer fetchCountEvaluationScoreArchive(Map<String, Object> aoParamaterMap);

	List<EvaluationBean> fetchLatestEvaluationScore(Map<String, Object> aoParamaterMap);

	Integer updateEvaluationVersionArchive(Map<String, Object> aoQueryMap);

	String getAwardReviewStatusIdForEvaluationSetting(HashMap<String, String> aoParamaterHashMap);

	public Integer updateEvaluationNegotiationStatus(HashMap<String, String> aoEvalMap);

	public Integer setNegotiationEvaluationAmount(String asEvaluationPoolMappingId);

	public Integer clearNegotiationData(HashMap<String, String> aoEvalMap);

	public Integer updateEvaluationAmountAfterNegotiation(HashMap<String, String> aoEvalMap);

	Integer setRequestAmendmentFlag(Map<String, Object> aoQueryMap);

	Integer updateReturnStatusForEvalGenCommentArchive(Map<String, Object> aoQueryMap);

	String getRequestAmendmentFlagFromEvaluationResult(Map<String, Object> aoQueryMap);

	List<String> getEvaluationStatusList(String asProposalId);

	Integer setReturnStatusEvaluationGeneralArchive(Map<String, Object> aoQueryMap);
	// END || R5 code
}