package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.ProviderBean;

/**
 * FileUploadMapper is an interface between the DAO and database layer for
 * business and service application, document vault related transactions to
 * insert , update , select entries.
 * 
 */

public interface FileUploadMapper
{

	void updateDocumentDetails(Document aoFileUpload);

	List<HashMap<String, Object>> selectFormDetails(HashMap aoHashMap);

	// Added in R5
	String checkFilingStatusInDb(String aoOrgId);

	// R5 ends
	List<Document> selectDocumentDetails(HashMap aoHashMap);

	List<Document> selectDocumentDetailsBasics(HashMap aoHashMap);

	List<String> getProviders();

	List<ProviderBean> getAgencies();

	List<ProviderBean> getProvidersAjax();

	List<ProviderBean> getAccountingPeriod(String aoProviderId);

	String getDocumentStatus(String aoDocumentId);

	Integer getDocLapsingInsertStatus(HashMap aoInsertPropMap);

	String checkExtension(String asProviderId);

	String getEndYearForChar500(String asProviderId);

	List<ProviderBean> getAccountingPeriodForProviderFromOrg(String asProviderId);

	Integer updateDocLapsingMasterDueDate(HashMap aoInsertPropMap);

	void updateDocumentDetails1(Document aoFileUpload);

	void updateDocumentDetailsBasics(Document aoFileUpload);

	Integer deleteShortFilingEntry(String asProviderId);

	Integer updateDocLapsingMaster(String asProviderId);

	Integer getCeoCount(HashMap asFormInfo);

	Integer getCfoCount(HashMap asFormInfo);

	String getCeoName(HashMap asFormInfo);

	String getCfoName(HashMap asFormInfo);

	String getDocIdForDocType(HashMap asFileUplaodInfo);

	String getDocIdForDocTypeService(HashMap asFileUplaodInfo);

	List<String> checkApplicationStatus(HashMap aoApplicationStatusMap);

	Integer updateDocumentModifiedInfo(HashMap aoModifiedInfoMap);

	List<Document> selectDocumentDetailsServiceSummary(HashMap aoHashMap);

	void updateDocumentDetails1ServiceSummary(Document aoFileUpload);

	List<HashMap<String, Object>> getDocTypeAndWorkFlowID(Map<String, String> aoProviderMap);

	Integer updateProcStatusForTerminatedWobNo(HashMap aoHashMap);

	String getLawType(String asProviderId);

	String getUserName(String asUserId);

	String getApplicationSettings(HashMap aoGetContentMap);

	String getDueDate(String asProviderId);

	int updateTermaAndConditionFlag();

	int updateDeletedDocumentDetails(Map<String, String> aoMap);

	String getApplicationStatus(HashMap aoApplicationStatusMap);

	HashMap<String, String> getProviderStatusDetails(HashMap aoArgumentsMap);

	HashMap<String, String> getProviderStatusDetailsBatch(HashMap aoArgumentsMap);

	// Updated for Release 3.2.0, Defect 5641
	List<String> getProviderBRAppDetails(HashMap aoApplicationStatusMap);

	int deleteDueDateReminderEntry(String asOrganizationId);

	List<HashMap<String, Object>> getDocumentDetails(String asDolcumentId);

	void setDocIdWithSameDocName(Document aoFileUpload);

	void setDocIdWithSameDocNameService(Document aoFileUpload);

	int updateOldDocumentId(Map<String, String> asDocumentIds);

	String checkSectionStatus(HashMap aoApplicationStatusMap);

	String checkServiceStatus(HashMap aoApplicationStatusMap);

	int documentIdCount(HashMap aoHMSection);

	int statusDocumentIdCount(HashMap aoHMSection);

	List<String> getLinkedToObjectName(String asDocumentId);

	// Start: Updated for Release 3.5.0, Defect 5630
	// Changing Return Type from String to map in new filling issue after 4.0.2
	List<String> getLinkedToObjectNameNotInDraft(Map<String,String> aoDataMap);

	List<String> getLinkedToObjectNameInDraft(String asDocumentId);

	// End: Updated for Release 3.5.0, Defect 5630

	Integer deleteSMAndFinanceDocs(String asDocumentId);
}
