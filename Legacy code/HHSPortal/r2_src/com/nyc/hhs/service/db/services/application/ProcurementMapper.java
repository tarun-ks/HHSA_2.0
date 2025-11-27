package com.nyc.hhs.service.db.services.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.AccountsAllocationBean;
import com.nyc.hhs.model.ApprovedProvidersBean;
import com.nyc.hhs.model.CBGridBean;
import com.nyc.hhs.model.CommentsHistoryBean;
import com.nyc.hhs.model.EPinDetailBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.FundingAllocationBean;
import com.nyc.hhs.model.Procurement;
import com.nyc.hhs.model.ProcurementCOF;
import com.nyc.hhs.model.ProcurementInfo;
import com.nyc.hhs.model.ProposalQuestionAnswerBean;
import com.nyc.hhs.model.ProposalReportBean;
import com.nyc.hhs.model.PsrBean;
import com.nyc.hhs.model.SelectedServicesBean;
import com.nyc.hhs.model.StaffDetails;

/**
 * This mapper interface is used for xml mapping with the ProcurementMapper.xml
 * all the method which defined here mapped with ProcurementMapper.xml
 * 
 * */

public interface ProcurementMapper
{
	public ArrayList<Procurement> fetchActiveProcurements(Procurement aoProcurementBean);

	public int getProcurementCount(Procurement aoProcurementBean);

	public List<Procurement> getProgramNameList(String asNycAgency);

	//R6: Changed for R6: EPIN Validation changes
	public EPinDetailBean fetchEpinDetails(HashMap<String, String> aoParamMap);

	public List<StaffDetails> getAcceleratorUserList(String asUserType);

	public List<StaffDetails> getAgencyUserList(String asAgencyId);

	public Integer insertNewProcurementDetails(Procurement aoProcBean);

	public Integer getStatusId(Map<String, String> aoStatusInfo);

	public Procurement fetchProcurementSummaryProvider(String asProcurementId);

	public List<ExtendedDocument> fetchRfpReleaseDocsDetails(ExtendedDocument aoDocumentBean);

	public ArrayList<Procurement> fetchActiveProcurementsForProvider(Procurement aoProcurementBean);

	public int getProcurementCountForProvider(Procurement aoProcurementBean);

	public Procurement getProcurementSummary(String asProcurementId);

	public ProcurementInfo getProcurementSummaryForNav(String asProcurementId);

	public String getProcurementId();

	public Integer checkExistingProcurement(Procurement aoProcBean);

	public List<String> getElementIdList(String asProcurementId);

	public List<String> fetchdocumentIdList(String asProcurementId);

	public String getProcurementStatus(String asProcurementId);

	public Integer checkIfUserOfSameAgency(Map<String, String> aoMap);

	public Integer checkIfAwardApproved(Map<String, String> aoMap);

	public Integer checkIfAwardApprovedForEvalPool(Map<String, String> aoMap);

	public Integer getProcurementDetailsForNavE5(Map<String, String> aoMap);

	public Integer getProcurementDetailsForNavE8(Map<String, String> aoMap);

	public Integer getProcurementDetailsForNavE7(Map<String, String> aoMap);

	public Integer getProcurementDetailsForNavE6(Map<String, String> aoMap);

	public Integer updateProcurementDetails(Procurement aoProcBean);

	public Integer insertRfpDocumentDetails(Map<Object, Object> aoPrameterMap);

	public List<SelectedServicesBean> fetchElementId(String asProcurementId);

	public List<SelectedServicesBean> fetchServiceElementId(String asProcurementId);

	public List<ApprovedProvidersBean> fetchApprovedProvidersList(Map<String, Object> aoMap);

	public List<ApprovedProvidersBean> fetchApprovedProvidersListReleased(Map<String, Object> aoMap);

	public List<ApprovedProvidersBean> fetchApprovedProvidersListProvider(Map<String, Object> aoMap);

	public List<ApprovedProvidersBean> fetchApprovedProvidersListAfterRelease(Map<String, Object> aoMap);

	public List<SelectedServicesBean> fetchAppProvidersList(SelectedServicesBean loSelectedServicesBean);

	public String fetchApprovedProvDetails(String asProcurementId);

	public List<HashMap<String, Object>> getFormDetails(HashMap<String, String> aoMap);

	public Map<String, String> getFormDetailsOfOrg(HashMap<String, String> aoHashmap);

	public Map getCorpStructureValue(HashMap<String, String> aoHashmap);

	public List<SelectedServicesBean> fetchWidgetDetails(SelectedServicesBean loSelectedServicesBean);

	public List<SelectedServicesBean> fetchEvidenceFlagList(SelectedServicesBean aoSelectedServicesBean);

	public Integer removeRfpDocs(Map<String, String> aoParameterMap);

	public List<ProposalQuestionAnswerBean> fetchProcurementCustomQuestionAnswer(Map<String, String> aoParameterMap);

	public List<CommentsHistoryBean> fetchProviderComments(Map<String, String> aoParameterMap);

	public ProcurementCOF fetchProcurementCONDetails(String asContractID);

	public ProcurementCOF fetchAmendmentConfigurationDetails(HashMap<String, String> aoHashMap);

	public ProcurementCOF fetchUpdateConfigurationDetails(HashMap<String, String> aoHashMap);

	public ProcurementCOF fetchProcurementCONDetailsForR3Contract(String asContractID);

	public String fetchProcurementDetailsForR3Contract(String asContractID);

	public String fetchDropDownValue(String asProcId);

	public Integer updateProcurementCoFStatus(Map aoProcurementMap);

	public Integer updateProcurementStatusOnPublish(Map aoProcurementMap);

	public Integer updateProcurementDataOnPublish(Map aoProcurementMap);

	public Boolean updateProcurementStatusClosed(String asProcurementId);

	public Integer deleteProvidersData(String asProcurementId);

	public Integer deleteProposalDocument(String asProcurementId);

	public Integer updateProcurementStatus(Map aoProcurementMap);

	public List displayApprovedProvidersList(SelectedServicesBean loSelectedServicesBean);

	public Integer updateDropDownValueDraft(SelectedServicesBean aoSelectedServBean);

	public Integer updateDropDownValuePlanned(SelectedServicesBean aoSelectedServBean);

	public Integer insertDropDownValuePlanned(SelectedServicesBean aoSelectedServBean);

	public String checkBeforeSaveApprovedProvDetails(SelectedServicesBean selectedServBean);

	public int deleteRfpDocumentDetails(Map<String, String> aoParameterMap);

	public int deleteRfpAddendumDocumentDetails(Map<String, String> aoParameterMap);

	public Integer saveSelectedServicesList(SelectedServicesBean aoSelectedServicesBean);

	public Integer saveAddendumServicesList(SelectedServicesBean aoSelectedServicesBean);

	public Integer updateSelectedServicesList(SelectedServicesBean aoSelectedServicesBean);

	public Integer updateAddendumServicesList(SelectedServicesBean aoSelectedServicesBean);

	public Integer deleteSelectedServicesList(Map<String, List<SelectedServicesBean>> aoSelectedServiceMap);

	public Integer deleteAddendumServicesList(Map<String, List<SelectedServicesBean>> aoSelectedServiceMap);

	public List<SelectedServicesBean> getSavedServicesList(String asProcurementId);

	public Integer updateDraftProcurementDetails(Procurement aoProcBean);

	public Integer insertAdendumDocumentDetails(Map<Object, Object> aoPrameterMap);

	public Integer insertProcurementAddendumDetails(Procurement aoProcurementBean);

	public Integer updateProcurementAddendumDetails(Procurement aoProcurementBean);

	public Procurement getReleasedProcurementSummary(String asProcurementId);

	public Integer updateProcurementLastModifiedDetails(Map<Object, Object> aoParameterMap);

	public Integer preserveOldStatus(String asProcurementId);

	public String getProviderStatus(Map<String, String> loProviderDetails);

	public List<ApprovedProvidersBean> displayAppProviderOnPageLoad(String asProcurementId);

	public Integer deleteRFPDocumentDetials(Map aoRfpDocDetailsMap);

	public Integer updateRFPdocumentStatus(Map aoProcurementMap);

	public Map<String, String> getProcurementTitle(String asProcurementId);

	public List<String> fetchApprovedProviders(String asProcurementId);

	public Integer updateProcurementServiceData(String asProcurementId);

	public Integer deleteProcurementServiceData(String asProcurementId);

	public Integer fetchServiceDetails(String asProcurementId);

	List<ProposalQuestionAnswerBean> fetchProposalCustomQuestions(HashMap<String, String> aoHashmap);

	List<ExtendedDocument> fetchProposalDocumentType(HashMap<String, String> aoHashmap);

	Integer insertProposalCustomQuestions(ProposalQuestionAnswerBean aoProposalQuestionBean);

	Integer updateProposalCustomQuestions(ProposalQuestionAnswerBean aoProposalQuestionBean);

	Integer updateProposalDocumentType(ExtendedDocument aoDocTypeBean);

	Integer insertProposalDocumentType(ExtendedDocument aoDocTypeBean);

	Integer insertAddendumProposalCustomQuestions(ProposalQuestionAnswerBean aoProposalQuestionBean);

	Integer updateAddendumProposalCustomQuestions(ProposalQuestionAnswerBean aoProposalQuestionBean);

	Integer updateAddendumProposalDocumentType(ExtendedDocument aoDocTypeBean);

	Integer insertAddendumProposalDocumentType(ExtendedDocument aoDocTypeBean);

	public Integer deleteProposalCustomQuestions(String asQuesSeqNo);

	public Integer deleteAddendumProposalCustomQuestions(ProposalQuestionAnswerBean aoProposalQuestionBean);

	public Integer deleteProposalDocumentType(String asDocSeqNo);

	public Integer deleteAddendumProposalDocumentType(ExtendedDocument aoDocTypeBean);

	public Integer updateProcurementData(Map aoProcDetails);

	public Integer updateProcurementEpinInMasterTable(HashMap<String, String> aoProcMap);

	public Integer editProcurmentConfFundingDetails(HashMap<String, String> loSetClause);

	public void addProcurmentConfFundingDetails(CBGridBean aoCBGridBean);

	public Integer addProcurementCoFInfo(Map aoProcurementMap);

	public List<ProcurementInfo> fetchProcTitleAndOrgList(HashMap<String, String> aoProcMap);

	public Integer fetchProcurementAddendumData(String asProcurementId);

	Integer deleteProcurementDocFromVault(String asDocumentId);

	public Integer deleteProcurementProviderData(String asProcurementId);

	public Integer fetchProcurementStatusId(String asProcurementId);

	public Integer deleteProviderQuesResponseData(String asProcurementId);

	public Integer deleteProviderSiteData(String asProcurementId);

	public Map<String, Object> fetchPrevProcStatusId(String asProcurementId);

	public String fetchServiceUnitFlag(String asProcurementId);

	public Integer deleteRemoveCompetitionPool(Map aoData);

	public Integer insertNewCompetitionPool(Map aoData);
	
	//[Start] R6.3 QC 8683 insert new competition pool
	public Integer insertNewCompetitionPoolNotIn(Map aoData);
	//[End] R6.3 QC 8683 insert new competition pool

	public List<String> fetchCompetitionPoolData(String asProcurementId);

	public Integer checkIfFavoriteExists(Map aoData);

	public Integer saveFavorites(Map aoData);

	public Integer deleteFavorites(Map aoData);

	public Integer insertEvaluationGroup(Procurement aoProcBean);

	public Integer delEvalPoolMappingData(String asProcurementId);

	public Integer delEvalGroupData(String asProcurementId);

	public Integer isOpenEndedOrZeroValue(String asProcurementId);

	public List<Map<String, String>> fetchProcurementContractTitleList(Map aoDataMap);

	public List<Map<String, String>> fetchProcurementTitleList(Map aoDataMap); // QC5446

	public List<Map<String, String>> fetchCompetitionPoolTitleList(Map aoDataMap);

	public Integer deleteProvidersDataGroup(String asProcurementId);

	public Integer deleteProviderQuesResponseDataGroup(String asProcurementId);

	public Integer deleteProviderSiteDataGroup(String asProcurementId);

	public Integer deleteProposalDocumentGroup(String asProcurementId);

	public String getCompetitionPoolStatus(Map aoDataMap);

	public Integer deleteCompetitionPools(Map aoData);
	
	//[Start] R6.3 added for QC6627 
	public Integer deleteCompetitionPoolsForAddendum(Map aoData);
	//[End] R6.3 added for QC6627
		
	public Integer getProvRestrictSubmitFlag(Map aoData);

	public Integer getProvRestrictSubmitFlagProposal(HashMap aoData);

	Integer insertProposalMappingQues(ProposalQuestionAnswerBean aoProposalQuestionBean);

	public Integer deleteProposalMappingQues(String asQuesSeqNo);

	Integer updateProposalMappingQues(ProposalQuestionAnswerBean aoProposalQuestionBean);

	List<ProposalQuestionAnswerBean> fetchPropCustomQuesForPlanned(HashMap<String, String> aoHashmap);

	List<ProposalQuestionAnswerBean> fetchPropCustomQuesForReleased(HashMap<String, String> aoHashmap);

	Integer insertProposalMappingDoc(ExtendedDocument aoDocTypeBean);

	public Integer deleteProposalMappingDoc(String asDocSeqNo);

	Integer updateProposalMappingDoc(ExtendedDocument aoDocTypeBean);

	List<ProposalQuestionAnswerBean> fetchPropDocTypeForPlanned(HashMap<String, String> aoHashmap);

	List<ProposalQuestionAnswerBean> fetchPropDocTypeForReleased(HashMap<String, String> aoHashmap);

	public List<ExtendedDocument> fetchRfpDocsDetailsForTasks(ExtendedDocument aoDocumentBean);

	public Integer fetchNewDocsCount(String asProcurementId);

	// Below fetchProcurementTitle is added in Release 3.6.0 for defect# 6498
	public String fetchProcurementTitle(String asProcurementId);

	public List<AccountsAllocationBean> fetchProcurementCoADetails(HashMap<String, Object> loHMContextData1);

	// Start R5 : Added
	public Integer insertNewPsrDetails(PsrBean aoPsrBean);

	public PsrBean getPsrSummary(String asProcurementId);

	public Integer updatePsrDetails(PsrBean aoPsrBean);

	public List<Procurement> getPsrServicesList(String asProcurementId);

	public ProcurementCOF getPcofPsrdetails(String asProcurementId);

	public Integer updatePsrStatusFlag(HashMap<String, String> loContextDataMap);

	public Integer updatePsrStatusFlagAgency(HashMap<String, String> loContextDataMap);

	public Integer updatePsrPdfStatusFlag(HashMap<String, String> loContextDataMap);

	public List fetchPdfFlagList(String asProcurementId);

	public List<AccountsAllocationBean> fetchPCOFFinanceDetails(String aoProcurementId);

	public Integer updatePcofPsrVersionNumber(String asProcurementId);

	public Procurement validateProcurementPSRPCOF(String asProcId);

	public Integer resetGeneratePDFFlag(String asProcId);

	public Integer fetchPSRApprovedCount(String asProcId);

	public Integer fetchPSRCount(String asProcId);

	public Integer updateGeneratePDFFlag(Procurement aoProcurementBean);

	public Integer updatePCOFContractDates(String asProcurementId);
	
	public List<FundingAllocationBean> fetchPsrConfFundingDetails(String asProcurementId);
	
	public Integer showPsrFundingSubGrid(String asProcurementId);
	// End R5 : Added
	// * Start QC 9401 R 8.5
	public List<EPinDetailBean> getProcurementForEpin(String asEpinId);
	public List<ProposalReportBean> getRfpReportData(String asProcurementId);
	// * End QC 9401 R 8.5
}