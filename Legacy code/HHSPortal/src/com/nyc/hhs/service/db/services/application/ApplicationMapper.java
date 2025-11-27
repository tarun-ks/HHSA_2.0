package com.nyc.hhs.service.db.services.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.ApplicationAuditBean;
import com.nyc.hhs.model.ApplicationBean;
import com.nyc.hhs.model.BusinessApplicationSummary;
import com.nyc.hhs.model.ContractDetails;
import com.nyc.hhs.model.Document;
import com.nyc.hhs.model.DocumentBean;
import com.nyc.hhs.model.OrganizationStatusBean;
import com.nyc.hhs.model.Population;
import com.nyc.hhs.model.ServiceQuestions;
import com.nyc.hhs.model.ServiceSettingBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.SubSectionBean;
import com.nyc.hhs.model.TaxonomyTree;
import com.nyc.hhs.model.WithdrawRequestDetails;

/**
 * ApplicationMapper is an interface between the DAO and database layer for
 * business application and organization profile related transactions to insert
 * , update, select entries.
 * 
 */

public interface ApplicationMapper
{

	ArrayList<ApplicationBean> selectUserAppInfo(String asOrgId);

	void insertUserAppInfo(Map<String, Object> aoHashmap);

	Integer insertDocTypeInfo(Map<String, String> aoMap);

	ArrayList<Document> selectDocTypeInfo(Map<String, String> aoMap);

	ArrayList<Document> selectDocTypeInfoForOrg(Map<String, String> aoMap);

	ArrayList<Document> selectDocIdFromServiceApplication(Map<String, String> aoMap);

	void deleteDocTypeInfo(Map<String, String> aoMap);

	void deleteDocTypeInfoForOrg(Map<String, String> aoMap);

	ArrayList<Population> selectPopulation(String asOrganizationID);

	void savePopulation(Population aoPopulationBean);

	void deletePopulation(String asOrganizationID);

	void insertOtherPopulation(HashMap aoOtherPopulationDataMap);

	String getOtherPopulation(String asOrganizationID);

	void deleteOtherPopulation(String asOrganizationID);

	Integer insertLanguageInfo(HashMap<String, Object> aoLanguageInfo);

	Integer deleteLanguageInfo(HashMap<String, Object> aoLanguageInfo);

	ArrayList<String> selectLanguageInfo(String asOrgId);

	ArrayList<String> selectLanguageInfointerpretation(String asOrgId);

	void addGeogaphyToApplication(HashMap<String, Object> aoUserInfo);

	void deleteGeographyTypeInfo(HashMap aoMap);

	void addSettingToService(HashMap<String, Object> aoUserInfo);

	void deleteSettingTypeInfo(HashMap aoMap);

	void updateDocuments(Map<String, Object> aoInputParamMap);

	void updateDocumentsBasics(Map<String, Object> aoInputParamMap);

	void removeDocuments(Map<String, Object> aoRemDocMap);

	List<Map<String, String>> getSelectedSettingList(Map<String, Object> aoMapRequiredDetails);

	int getDocStatusCount(Map<String, Object> aoMap);

	int getDocStatusCountForOrg(Map<String, Object> aoMap);

	void updateDocStatus(Map<String, Object> aoMap);

	void updateDocStatusForOrg(Map<String, Object> aoMap);

	void insertDocStatus(Map<String, Object> aoMap);

	List<SubSectionBean> getStatusMapForQueDoc(Map<String, String> aoMap);

	int getSubSecStatus(Map<String, String> aoMap);

	List<DocumentBean> getDocumentList(Map<String, String> aoMap);

	List<DocumentBean> documentDetails(HashMap asAppId);

	List<SubSectionBean> subSectionDetails(HashMap asAppId);

	void insertSubSectionDetails(Map<String, Object> aoRequiredDetailsM);

	void deleteSubSectionDetails(Map<String, Object> aoRequiredDetailsM);

	List<TaxonomyTree> getRelatedServices(Map<String, Object> aoRequiredDetailsM);

	List<TaxonomyTree> getRelatedServicesAll(Map<String, Object> aoRequiredDetailsM);

	List<Map<String, String>> getLangForPrint(Map<String, String> aoMap);

	List<Map<String, String>> getGeoForPrint(Map<String, String> aoMap);

	List<Population> getPopulationForPrint(Map<String, String> aoMap);

	void deleteServiceSubSectionDetails(Map<String, Object> aoRequiredDetailsM);

	List<TaxonomyTree> getSearchResultService(Map<String, String> aoMap);

	Map<String, String> getOrgDetailsPrint(Map<String, String> aoMap);

	List<Map<String, String>> getServiceDetailForPrint(Map<String, String> aoMap);

	void createNewBusinessApplication(Map<String, Object> aoMap);

	void removeDocumentsServiceSummary(Map<String, Object> aoRemDocMap);

	void updateDocumentsServiceSummary(Map<String, Object> aoInputParamMap);

	ServiceQuestions getServiceQuestionDetail(Map<String, String> aoMap);

	List<DocumentBean> getServiceDocumentDetail(Map<String, String> aoMap);

	List<StaffDetails> getServiceStaffDetail(Map<String, String> aoMap);

	List<ContractDetails> getServiceContractDetail(Map<String, String> aoMap);

	List<ServiceSettingBean> getServiceSettingDetail(Map<String, String> aoMap);

	List<SubSectionBean> getServiceAppStatusMap(Map<String, String> aoProps);

	ArrayList<Document> selectDocTypeInfoService(Map<String, String> aoMap);

	int getDocStatusCountService(Map<String, Object> aoMap);

	void updateDocStatusService(Map<String, Object> aoMap);

	void insertDocStatusService(Map<String, Object> aoMap);

	List<BusinessApplicationSummary> getBusinessApplicationSummary(Map<String, String> aoMap);

	WithdrawRequestDetails checkForExistingApplication(Map<String, Object> aoMap);

	Integer updateSectionStatus(Map<String, Object> aoMap);

	void insertSectionStatus(Map<String, Object> aoMap);

	int selectSectionStatusCount(Map<String, Object> aoMap);

	List<Map<String, String>> selectSectionStatus(Map<String, Object> aoMap);

	void insertSubSectionStatus(Map<String, Object> aoMap);

	void insertSubSectionStatusBasic(Map<String, Object> aoMap);

	List<ApplicationAuditBean> fetchLastProviderComments(Map<String, String> aoMap);

	List<String> getExistingServices(Map<String, Object> aoMap);

	void reEnterExistingServices(Map<String, Object> aoMap);

	Integer updateServiceAppStatus(Map<String, Object> aoMap);

	void updateBusinessAppStatus(Map<String, Object> aoMap);

	String gettBusinessApplicationStatus(Map<String, String> aoMap);

	String getServiceApplicationStatus(Map<String, String> aoMap);

	Map<String, String> getUserDataFromDB(Map<String, String> aoMap);

	String getOldServiceId(Map<String, String> aoMap);

	void updateUserData(Map<String, String> aoMap);

	String getWobNo(Map<String, String> aoMapRequiredParam);

	String getBusinessAppStatusForGivenProvider(Map<String, String> aoMap);

	void updateUserProfileEmail(Map<String, String> aoMap);

	void updateUserProfileEmailStaff(Map<String, String> aoMap);

	void updateUserProfileEmailOrg(Map<String, String> aoMap);

	List<HashMap<String, String>> getApplicationSettingFromDB();

	int getStatusForExistingApprovedRejected(String asOrgId);

	void updateBusiAppModifiedDate(Map<String, String> aoMap);

	void updateBusinessAppModifiedDate(Map<String, String> aoMap);

	void updateBusinessAppIdModifiedDate(Map<String, String> aoMap);

	void updateServiceAppModifiedDate(Map<String, String> aoMap);

	void updateServiceApplicationModifiedDate(Map<String, String> aoMap);

	void updateBusiServiceAppIdModifiedDate(Map<String, String> aoMap);

	void updateBusiServiceAppModifiedDate(Map<String, String> aoMap);

	void updateBusiServiceApplicationModifiedDate(Map<String, String> aoMap);

	void updateServiceAppIdModifiedDate(Map<String, String> aoMap);

	List<Map<String, Object>> sectionStatusMap(String asBusAppId);

	List<Map<String, Object>> subSectionStatusMap(Map<String, Object> aoMap);

	List<Map<String, Object>> subSectionStatusMapForDocument(Map<String, Object> aoMap);
	
	//New Addition for Defect #1805 fix
	
	List<String> getServiceContractMappingIds(Map<String, String> aoMap);
	
	List<String> getServiceStaffMappingIds(Map<String, String> aoMap);
	
	Integer insertServiceQuesInfo(Map<String, Object> aoServiceQuesMap);
	
	void deleteServiceDocumentonEntityType(Map<String, Object> aoServiceQuesMap);
	
	void deleteServiceDocumentonStaff(Map<String, Object> aoServiceQuesMap);
	
	void deleteServiceDocumentonContract(Map<String, Object> aoServiceQuesMap);
	
	void deleteServiceDocumentonMappingId(Map<String, Object> aoServiceQuesMap);
	
	void deleteServiceDocumentCapabilityStatement(Map<String, Object> aoServiceQuesMap);
	
	void deleteServiceDocumentsNotCapabilityStatement(Map<String, Object> aoServiceQuesMap);
	
	String fetchContractNameForgridDisplay(HashMap lohmWhereClause);
	
	String fetchStaffNameForgridDisplay(HashMap lohmWhereClause);
	
	public List<Map<String,String>> fetchProcuringNonProcuringAgenciesFromDB();
	
	// Start of changes for Release 3.10.0 : Enhancement 6572
	String fetchCSfromDb(String asOrgId);
	
	void deleteFilingDocs(String asAppId);
	
	void deleteFilingInfo(String asAppId);
	
	void updateSubSectionSummary(HashMap<String, String> aoUpdateSubSectionMap);
	
	String getSectionStatusForFilings(String asAppId);
	
	void updateSectionStatusForFilings(HashMap<String, String> aoUpdateSubSectionMap);
	// End of changes for Release 3.10.0 : Enhancement 6572


	//Start: Add for R6.1.0
	List<OrganizationStatusBean> pullStatusOfOrgApplicationFiling();
	List<OrganizationStatusBean> pullSuperSeedingEventForLastBusinessApp();
	//End: Add for R6.1.0 
	//Start R7 for modification
	String getPreprocessingClassFromApplicationSetting(String asReviewProcessId);
	
	String getReviewProcessId(String asTaskType);
	//End R7 for modification
	
	//Start: R 7.1.0 QC6674
	String getOrgFromBusinessApp(String businessAppId);
	//End: R 7.1.0 QC6674
}

