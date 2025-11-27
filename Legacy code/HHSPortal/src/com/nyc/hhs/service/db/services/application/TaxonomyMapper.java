package com.nyc.hhs.service.db.services.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.GeneralAuditBean;
import com.nyc.hhs.model.KeyValue;
import com.nyc.hhs.model.ServiceSummary;
import com.nyc.hhs.model.TaxonomyLinkageBean;
import com.nyc.hhs.model.TaxonomyServiceBean;
import com.nyc.hhs.model.TaxonomySynonymBean;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.model.WithdrawRequestDetails;

/**
 * TaxonomyMapper is an interface between the DAO and database layer for
 * business and service application,organization profile related transactions to
 * insert, update and select entries.
 * 
 */
public interface TaxonomyMapper
{

	ArrayList selectPopulationTaxonomyTree(String asTaxonomyType);

	List<TaxonomyTree> selectTaxonomyDetails();

	List<TaxonomyTree> getTaxonomyTreeByType(final String asElementType);

	Integer deleteServices(final Map<String, String> aoQueryMap);

	List<TaxonomyServiceBean> getSelectedService(final Map<String, String> aoQueryMap);

	Integer saveSelectedServices(final TaxonomyServiceBean aoTaxonomyServiceBean);

	Integer deleteSelectedService(final Map<String, String> aoQueryMap);

	Integer updateSelectedService(final Map<String, Object> aoQueryMap);

	List<String> getSelectedGeography(Map<String, Object> aoRequiredFields);

	List<ServiceSummary> selectServiceSummaryDetails(Map<String, Object> aoQueryMap);

	List<ServiceSummary> selectServiceSummaryDetailsAfterSubmit(Map<String, Object> aoQueryMap);

	List<String> selectServiceSummary(Map<String, Object> aoQueryMap);

	void deleteServiceSummaryDetails(HashMap<String, Object> aoServiceInfo);

	void deleteFromContractServiceMappingTable(Map<String, Object> aoServiceInfo);

	void deleteFromStaffServiceMappingTable(Map<String, Object> aoServiceInfo);

	void deleteFromServiceQuestionTable(Map<String, Object> aoServiceInfo);

	void deleteFromServiceSettingTable(Map<String, Object> aoServiceInfo);

	void deleteFromDocumentTable(Map<String, Object> aoServiceInfo);

	void deleteFromSubSectionSummaryTable(Map<String, Object> aoServiceInfo);

	List<KeyValue> getDocumentStatus(Map<String, Object> aoMap);

	List<KeyValue> getAppSettingStatus(Map<String, Object> aoMap);

	List<ServiceSummary> getAllSpecilizationSetting(Map<String, Object> aoMap);

	List<Map<String, Object>> selectServiceAppIDs(Map<String, Object> aoHmParameter);

	Integer updateBAStatusOnAppSubmission(Map<String, Object> aoQueryMap);
	Integer updateBAStatusOnAppSubmissionForCA(Map<String, Object> aoQueryMap);

	Integer updateSAStatusOnAppSubmission(Map<String, Object> aoQueryMap);
	Integer updateSAStatusOnAppSubmissionForCA(Map<String, Object> aoQueryMap);

	Integer updateSectionStatusOnAppSubmission(Map<String, Object> aoQueryMap);

	Integer updateSSSStatusOnAppSubmission(Map<String, Object> aoQueryMap);

	Integer updateDocumentOnAppSubmission(Map<String, Object> aoQueryMap);

	List<KeyValue> getServiceInformation(Map<String, Object> aoMap);

	Integer updateSSSStatusOnServiceSubmission(Map<String, Object> aoQueryMap);

	Integer updateDocumentOnServiceSubmission(Map<String, Object> aoQueryMap);

	Integer updateServiceAppOnServiceSubmission(Map<String, Object> aoQueryMap);
	Integer updateServiceAppOnServiceSubmissionForCA(Map<String, Object> aoQueryMap);
	
	
	Integer updateServiceAppOnServicePostSubmission(Map<String, Object> aoQueryMap);
	Integer updateServiceAppOnServicePostSubmissionForCA(Map<String, Object> aoQueryMap);

	Integer getServiceSuperStatus(Map<String, Object> aoQueryMap);
	
	// taxonomy maintenance start
	TaxonomyTree getTaxonomyItemDetails(Integer aiCurrentSeq);

	TaxonomyTree getTaxonomyItemDetails();

	void saveNewTaxonomyObject(TaxonomyTree aoTaxonomyTree);

	void updateTaxonomyItemDetails(TaxonomyTree aoTaxonomyTreeBean);

	Integer updateTaxonomyDeleteStatus(TaxonomyTree aoTaxonomyTree);

	Integer updateLinkageDeleteStatus(TaxonomyTree aoTaxonomyTree);

	Integer updateSynonymDeleteStatus(TaxonomyTree aoTaxonomyTree);

	TaxonomyTree getCurrentElementId();

	List<TaxonomySynonymBean> getTaxonomySynonymData();

	List<TaxonomyLinkageBean> getTaxonomyLinkageData();

	TaxonomyTree getTaxonomyItemDetailsLeftMenu(String aoElementId);

	List<TaxonomyTree> getLinkageForSelectedElement(TaxonomyTree aoTaxonomyTreeBean);

	List<TaxonomySynonymBean> getTaxonomySynonymDataLeftMenu(String aoElementId);

	List<TaxonomyLinkageBean> getTaxonomyLinkageDataLeftMenu(String aoElementId);

	void deleteTaxonomySynonymDetails(TaxonomyTree aoTaxonomyTreeBean);

	void deleteTaxonomyLinkageDetails(TaxonomyTree aoTaxonomyTreeBean);

	void insertTaxonomySynonymDetails(TaxonomySynonymBean aoTaxonomySynonymBean);

	void insertTaxonomyLinkageDetails(TaxonomyLinkageBean aoTaxonomyLinkageBean);

	void insertTaxonomyChangeDetailsLog(TaxonomyTree aoTaxonomyTreeBean);

	List<TaxonomyTree> selectTaxonomyDetailsFromDB();

	TaxonomyTree isDuplicateElementName(TaxonomyTree aoTaxonomyTree);

	void deleteFromTaxonomyTranRec();

	List<TaxonomyTree> reCacheEvidenceValidation();

	void insertGeneralAuditDetails(GeneralAuditBean aoGeneralAuditBean);

	GeneralAuditBean getLastUpdatedTaxonomyRecacheDetails(GeneralAuditBean aoGeneralAuditBeanRecache);

	GeneralAuditBean getLastUpdatedTaxonomyDetails(GeneralAuditBean aoGeneralAuditBean);

	Integer getCurrentSeqFromTable();

	void updateTaxonomyChildrenActiveFlag(TaxonomyTree aoTaxonomyTree);
	// taxonomy maintenance end

	List<WithdrawRequestDetails> checkBeforeAddingService(Map<String, String> aoCheckForService);

	void updateSectionStatus(Map<String, Object> aoQueryMap);

	void updateApplicationStatus(Map<String, Object> aoQueryMap);
	void updateApplicationStatusForCA(Map<String, Object> aoQueryMap);
	
	void updateSubSectionStatus(Map<String, Object> aoQueryMap);

	void updateDocumentStatus(Map<String, Object> aoQueryMap);

	void updateSubSectionStatusDef(Map<String, Object> aoQueryMap);

	void updateDocumentStatusDef(Map<String, Object> aoQueryMap);

	Integer totalNoOfServices(final Map<String, Object> aoQueryMap);

	void updateBusinessApplication(Map<String, Object> aoServiceInfo);

	void updateSectionComments(Map<String, Object> aoQueryMap);

	void updateBusinessComments(Map<String, Object> aoQueryMap);
	
	String selectRecacheTime();
	void insertRecacheTime(String asCurrentTime);
	
	Integer plannedProcurementCount(Map aoProcurementMap);
	Integer releasedProcurementCount(Map aoProcurementMap);
	// Added below method for Defect fix # 8455 Scenario 1
	List<String> getDocumentIdFromDocumentTable(Map aoDataMap);
}
