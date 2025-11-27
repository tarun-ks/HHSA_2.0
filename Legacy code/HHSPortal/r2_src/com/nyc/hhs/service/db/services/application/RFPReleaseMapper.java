package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.EvaluationCriteriaBean;
import com.nyc.hhs.model.ExtendedDocument;
import com.nyc.hhs.model.ProposalQuestionAnswerBean;
import com.nyc.hhs.model.RFPReleaseBean;

/**
 * This mapper interface is used for xml mapping with the RFPReleaseMapperr.xml
 * all the method which defined here mapped with RFPReleaseMapper.xml
 * 
 * */
public interface RFPReleaseMapper
{

	public String fetchEpinValue(String asProcId);

	public List<String> fetchElementId(String asProcId);

	public RFPReleaseBean fetchRfpPreRequisites(String asProcId);

	public Integer fetchProcCertOfFunds(String asProcId);

	public RFPReleaseBean fetchEvaluationCriteria(String asProcurementId);

	public Boolean insertAppProvList(RFPReleaseBean aoRfpBean);

	public List<RFPReleaseBean> fetchRfpDates(String asProcId);

	public Integer updateRfpVersion(String asProcId);

	public String getProcurementStatus(String asProcurementId);

	public String getProcurementPrevStatus(String asProcurementId);

	public Integer saveEvaluationCriteria(EvaluationCriteriaBean aoEvaluationCriteriaBean);

	public Integer updateEvaluationCriteria(EvaluationCriteriaBean aoEvaluationCriteriaBean);

	public RFPReleaseBean fetchAddendumEvaluationCriteria(String asProcurementId);

	public Integer saveAddendumEvaluationCriteria(EvaluationCriteriaBean aoEvaluationCriteriaBean);

	public Integer updateAddendumEvaluationCriteria(EvaluationCriteriaBean aoEvaluationCriteriaBean);

	public Integer insertRfpDocumentData(HashMap loMap);

	public Integer deleteRfpDocAddendunData(String asProcId);

	public Integer deleteProcAddendumDocument(String asProcId);

	public Integer insertProcQuestionConfig(String asProcId);

	public Integer deleteAddendumQuestionConfig(String asProcId);

	public Integer deleteProcAddendunData(String asProcId);

	public Integer deleteProcAddendumService(String asProcId);

	public Integer deleteEvaluationCriteriaData(String asProcId);

	public Integer insertEvaluationCriteria(Map<String, Object> aoData);

	public Integer deleteAddendumEvalCriteria(String asProcId);

	public Integer updateAppProviders(HashMap loHashMap);

	public Integer saveApprovedProviders(HashMap aoReqMap);

	public Integer saveApprovedProvidersServices(HashMap aoReqMap);

	public Integer updateRFPdocumentStatus(HashMap aoReqMap);

	public Integer deleteEvaluationCriteria(EvaluationCriteriaBean aoEvaluationCriteriaBean);

	public Integer deleteAddendumEvaluationCriteria(EvaluationCriteriaBean aoEvaluationCriteriaBean);

	public List<ExtendedDocument> checkRfpDocumentType(String asProcId);

	public Integer hardDeleteRfpDocumentDetails(String asProcId);

	public Integer insertGroupCompetitionMapping(RFPReleaseBean aoRFPReleaseBean);
	
	//[Start] R6.3 QC6627 add ability to create new competition pool and delete during addendum.
	public Integer insertGroupCompetitionAddendumMapping(RFPReleaseBean aoRFPReleaseBean);
	//[End] R6.3 QC6627 add ability to create new competition pool and delete during addendum.

	public List<String> listOfRFPdocuments(HashMap<String, Object> loHMContextData);

	public Integer insertQuestionMappingReleaseAddendum(ProposalQuestionAnswerBean aoProposalQuestionAnswerBean);

	public Integer getMaxVersionProcurementQuesitons(String asProcurementId);

	public Integer getMaxVersionProcurementDocuments(String asProcurementId);

	public Integer getMaxVersionEvalCriteria(String asProcurementId);

	public Integer insertProcDocumentConfig(ExtendedDocument aoExtendedDocument);

	public Integer insertProcDocumentConfigMapping(ExtendedDocument aoExtendedDocument);

	public Integer checkEvaluationInProgress(Map<String, String> aoInputMap);

}