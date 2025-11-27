package com.nyc.hhs.service.db.services.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.ApplicationSummary;
import com.nyc.hhs.model.ContractDetails;
import com.nyc.hhs.model.NYCAgency;
import com.nyc.hhs.model.ServiceQuestions;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.WithdrawRequestDetails;

/**
 * ApplicationSummaryMapper is an interface between the DAO and database layer
 * service application and organization profile related transactions to insert ,
 * update , select entries.
 * 
 */

public interface ApplicationSummaryMapper
{

	ArrayList<ApplicationSummary> selectAppInfo(Map<String, String> aoMapSummary);

	Integer insertBusinessAppInfo(HashMap aoHashmap);

	Integer insertIntoAuditTable(final Map<String, Object> aoHashmap);

	Integer updateServiceApplicationTable(final Map<String, Object> aoHashmap);

	Integer updateBusinessApplicationTable(final Map<String, Object> aoHashmap);

	Integer updateWithdrawnTable(final Map<String, Object> aoHashmap);

	Integer insertIntoSuperSedingTable(final Map<String, Object> aoHashmap);

	Integer updateBusinessAppExpirationDate(final Map<String, Object> aoHashmap);

	Integer insertIntoPrintViewGeneration(final Map<String, Object> aoHashmap);

	Integer updatePrintViewGeneration(final Map<String, Object> aoHashmap);

	Integer bussAppExpDateConditionallyApprove(final Map<String, Object> aoHashmap);

	Integer serviceAppExpDateConditionallyApprove(final Map<String, Object> aoHashmap);

	Integer serviceAppExpDateSuspend(final Map<String, Object> aoHashmap);

	List<ApplicationSummary> getApplicationSummaryHistory(final Map<String, Object> aoHashmap);

	List<ApplicationSummary> getApplicationServiceHistory(final Map<String, Object> aoHashmap);

	ApplicationSummary getProviderData(final String asOrgId);

	ContractDetails getContractById(Map<String, String> aoContractInfo);

	List<ContractDetails> getAllContract(String asOrgId);

	void insertContract(ContractDetails aoContract);

	public Integer getStaffCount(Map aoServiceQuesMap);

	public Integer getContractCount(Map aoServiceQuesMap);

	void updateContract(ContractDetails aoContract);

	List<NYCAgency> getAllNYCAgency();

	void insertContractMapping(Map<String, Object> aoContractMapping);

	StaffDetails getStaffById(Map<String, String> aoParamMap);

	StaffDetails getRecentlyAddedStaffId();

	List<StaffDetails> getAllStaff(String asOrgId);

	String getStaffIdSequence();

	void insertStaff(StaffDetails aoStaff);

	void insertStaffOrgMapping(StaffDetails aoStaff);

	public List<Map<String, Object>> getMemberTitles(Map aoMemberTitle);

	Integer getAdminCountToDeactivate(Map<String, String> aoParamMap);

	void updateStaff(StaffDetails aoStaff);

	void updateMemberStaff(StaffDetails aoStaff);

	void linkMemberToUser(StaffDetails aoStaff);

	void insertStaffMapping(Map<String, Object> aoStaffMapping);

	List<ContractDetails> getContractListForGrid(Map<String, String> aoContractMappingDetails);

	List<ServiceQuestions> getQuestionDetailList(Map<String, String> aoQuestionDetailList);

	Integer insertServiceQuesInfo(Map<String, Object> aoServiceQuesMap);

	List<StaffDetails> getStaffListForGrid(Map<String, String> aoStaffMappingDetails);

	void deleteSelectedStaff(String asStaffId);

	void deleteSelectedContract(String asContractId);

	void deleteStaffMapping(Map<String, Object> aoServiceQuesMap);

	void deleteContractMapping(Map<String, Object> aoServiceQuesMap);

	void insertServiceQuesInQuestion(Map<String, Object> aoServiceQuesMap);

	void deleteServiceQuesInQuestion(Map<String, Object> aoServiceQuesMap);

	void deleteServiceQuesInSubSection(Map<String, Object> aoServiceQuesMap);

	void updateServiceQuesInDocument(Map<String, Object> aoServiceQuesMap);

	void updateServiceQuesInQuestion(Map<String, Object> aoServiceQuesMap);

	List<StaffDetails> getOrgMemberListForGrid(String aoOrgId);

	void insertServiceQuesInSubSectionSummary(Map<String, Object> aoServiceQuesMap);

	void updateServiceQuesInSubSectionSummary(Map<String, Object> aoServiceQuesMap);

	void updateContractMapping(Map<String, String> aoMap);

	WithdrawRequestDetails getBAWithdrawlId();

	void insertBusinessWithdrawlRequest(Map<String, String> aoBusinessWithdrawl);

	public int updateBusinessIfExists(Map<String, String> aoBusinessWithdrawl);

	void updateBusinessWithdrawlRequest(Map<String, String> aoBusinessWithdrawl);

	void updateBusinessApplication(Map<String, String> aoBusinessIdOrgId);

	WithdrawRequestDetails getParentApplicationId(Map<String, String> aoServiceWithdrawl);

	void insertServiceWithdrawlRequest(Map<String, String> aoServiceWithdrawl);

	public int UpdateServiceIfExist(Map<String, String> aoServiceWithdrawl);

	void updateServiceWithdrawlRequest(Map<String, String> aoServiceWithdrawl);

	void updateServiceApplication(Map<String, String> aoServiceIdOrgId);

	WithdrawRequestDetails getServiceParentApplicationId(Map<String, String> aoServiceWithdrawl);

	WithdrawRequestDetails getSAWithdrawlId();

	public StaffDetails checkCEOOfficer(Map<String, String> aoParamMap);

	public Integer denyUserRequestProfile(Map<String, String> aoParamMap);

	void insertApplicationAudit(Map<String, String> aoApplicationAuditEntry);

	List<WithdrawRequestDetails> getServiceApplicationIds(Map<String, String> aoBusinessWithdrawl);

	WithdrawRequestDetails getServiceName(Map<String, String> aoServiceWithdrawl);

	void updateServiceApplicationForSubmission(Map<String, String> aoServiceIdOrgId);

	void updateServiceAppForBusinessWithdrawal(Map<String, String> aoServiceIdOrgId);

	void insertSuperSedingData(Map<String, String> aoWithdrawalMap);

	void insertOrgNameChange(Map<String, String> aoNewOrgNameMap);

	void insertNewAccountingPeriod(Map<String, Object> aoNewAccountPeriodMap);

	void deleteExistingAccountingPeriod(Map<String, Object> aoNewAccountPeriodMap);

	void deleteServiceQuesInDocument(Map<String, Object> aoServiceQuesMap);

	void deleteStaffMappingOnQuestionSave(Map<String, Object> aoServiceDetails);

	void deleteContractMappingOnQuestionSave(Map<String, Object> aoServiceDetails);

	List<WithdrawRequestDetails> getDraftServiceApplicationId(Map<String, String> aoServiceInfoMap);

	public StaffDetails getCfoEntry(Map<String, String> aoGetCeoEntry);

	public Integer getApprovedProviderStatus(String asProviderStatus);

	public List<Map<String, Object>> getDraftReviewRevisionProviderStatus(
			Map<String, Object> aoDraftReviewRevisionProviderStatusMap);

	WithdrawRequestDetails getProviderStatus(String asOrgId);

	public Map<String, Object> getBussAppUpdatedStatus(final Map<String, Object> aoParamMap);

	public Map<String, Object> getServiceAppUpdatedStatus(final Map<String, Object> aoParamMap);

	public List<Map<String, Object>> displayApplicationHistoryInfo(Map<String, String> aoDisplayAppHIstoryMap);

	public List<Map<String, Object>> displayServiceApplicationHistoryInfo(Map<String, String> aoDisplayAppHIstoryMap);

	public void updateOrganizationTable(Map<String, String> aoOrgDetails);

	public String fetchOrganizationStatus(String asOrgId);

	public String fetchBussAppWithdrawStatus(String asBusinessAppId);

	public String fetchServiceAppWithdrawStatus(String asServiceAppId);

	public String serviceBusAppWithdrwStatus(String asServiceAppId);

	public void updateOrgAccountingPeriod(Map<String, String> aoNewAccountPeriodMap);

	public String fetchServiceAppStatus(String asServiceAppId);

	public String fetchServiceWorkFlowId(String asServiceAppId);

	void updateServiceApplicationForReSubmission(Map<String, String> aoServiceIdOrgId);

	public String checkStaffMapping(Map<String, Object> aoStaffMapping);

	public String checkContractMapping(Map<String, Object> aoContractMapping);

	public List<ApplicationSummary> numberOfBrAppAgainstOrg(Map<String, Object> aoInputParams);

	public List<Map<String, Object>> fetchAccReqInfo(String asOrgId);

	void updateServiceStaffDetails(StaffDetails aoStaff);

	void updateServiceStaffOrgMappingDetails(StaffDetails aoStaff);

	public String fetchAppStatus(String asBussAppId);

	public List<Map<String, Object>> getServiceComments(Map<String, String> aoServiceInfoMap);

	public List<Map<String, Object>> getDocLapsingInformation(Map<String, Object> aoServiceInfoMap);

	public String getDeactivatedService(Map<String, String> aoServiceInfoMap);

	public List<String> getDeactivatedServiceForApp(Map<String, String> aoBusinessInfoMap);

	public String getRecentlyAddedContractId();

	public String checkExistingContractId(Map<String, String> aoServiceInfoMap);

	public List<Map<String, Object>> getAllApplicationsOfProvider(String asOrgId);

	public List<Map<String, Object>> recacheTaxonomy(Map<String, String> aoParamMap);

	public String getCityUserName(String asUserId);

	public Map<String, Object> getSupersedingandExpiry(Map<String, String> aoQueryMap);

	public List<Map<String, String>> getDeletedServiceName(Map<String, String> aoActionMap);

	public String getSupersedingStatus(String asServiceAppId);

	ArrayList<ApplicationSummary> selectAppInfoByOrgId(Map<String, String> aoMapSummary);

	void updateMemberStaffOrgMapping(StaffDetails aoStaff);

	Integer getStaffCountToDelete(StaffDetails aoStaff);

	void linkMemberToUserDeleteAllStaff(StaffDetails aoStaff);

	Integer getStaffCountToDeleteDenyUserRequestProfile(Map<String, String> aoParamMap);

	Integer getStaffCountToSkipUserdnUpdate(StaffDetails aoStaff);

	void denyUserRequestProfileDeleteAllStaff(Map<String, String> aoParamMap);

	void deactivateUser(StaffDetails aoStaff);

	// Start : R5 Added
	String getBussAppExpiringDate(HashMap<String, String> aoParam);

	String getServiceAppExpiringDate(HashMap<String, String> aoParam);

	int getContractRestrictionCountDeactivatedUser(StaffDetails aoStaff);
	// End : R5 Added
	
	// Start SAML QC 9165 R 7.8.0
	public List<StaffDetails> getAllActiveOrgStaff();
	public void updateStaffOrganizationUserStatus(StaffDetails aoStaffDetails);
	public String getSettingsValue(String componentName);
	public void updateSettingsValue(HashMap<String, String> loParam);
	
	// End   SAML QC 9165 R 7.8.0
}
