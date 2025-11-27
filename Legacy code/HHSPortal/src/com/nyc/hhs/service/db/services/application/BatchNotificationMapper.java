package com.nyc.hhs.service.db.services.application;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.ApplicationExpiryRuleBean;
import com.nyc.hhs.model.ApplicationIdBRStatusBean;
import com.nyc.hhs.model.ApplicationIdStatusBean;
import com.nyc.hhs.model.CityUserDetailsBeanForBatch;
import com.nyc.hhs.model.DocLapsingRuleBean;
import com.nyc.hhs.model.NotificationSettingsBean;
import com.nyc.hhs.model.PrintViewGenerationBean;
import com.nyc.hhs.model.ProviderExpiryRuleBean;
import com.nyc.hhs.model.SMAlertNotificationBean;
import com.nyc.hhs.model.SupersedingStatusBean;

/**
 *  BatchNotificationMapper is an interface between the DAO and database 
 *  layer for batches notification to retrieve, insert and update
 *  entries related to Batch Notification.
 *  
 */

public interface BatchNotificationMapper {
	
	public List<DocLapsingRuleBean> fetchDocLapsingRule(Map aoHashMap);
	
	public List<NotificationSettingsBean> fetchNotificationSettings(Map aoHashMap);
	
	public String fetchDueDateReminderCount(Map aoHashMap);
	
	public void insertIntoDueDateReminder(Map aoHashMap);
	
	public int updateProviderExpiryStatus(Map aoHashMap);
	
	public List<ProviderExpiryRuleBean> fetchProviderExpiryRule(Map aoHashMap);
	
	public int updateProviderStatus(Map aoHashMap);
	
	public int updateSuperSeedingStatus(Map aoHashMap);
	
	public List<ApplicationExpiryRuleBean> fetchApplicationExpiryRule(Map aoHashMap);
	
	public List<ApplicationExpiryRuleBean> fetchApplicationExpiryForDraft(Map aoHashMap);
	
	public int updateBRApplicationExpiryStatus(Map aoHashMap);
	
	public int updateServiceApplicationExpiryStatus(Map aoHashMap);
	
	public int deleteConditionallyApprovedfromSuperseding(Map aoHashMap);
	
	public String fetchLatestBRAppIdforProvider(Map aoHashMap);
	
	public int insertIntoSuperSeeding(Map aoHashMap);
	
	public List<HashMap<String, String>> selectConditionallyApprovedProvidersfromSuperseding(Map aoHashMap);
	
	public List<String> selectSCIDsForBR(Map aoHashMap);
	
	public String fetchDuplicateProviderApplicationCount(Map aoHashMap);
	
	public String fetchBusinessApplicationStatus(Map aoHashMap);
	
	public List<PrintViewGenerationBean> fetchPrintViewGeneration(Map aoHashMap);
	
	public int updatePrintViewGenerated(Map aoHashMap);
	
	public int setExpiryDatetoNull(HashMap aoHashMap);
	
	public int setExpiryDatetoNullForSC(HashMap aoHashMap);
	
	public List<ApplicationIdStatusBean> fetchApplicationIdStatus(Map aoHashMap);
	
	public String fetchProviderNameBatch(String asOrgId);
	
	public String fetchAgencyNameBatch(String asAgencyId);
	
	public List<SMAlertNotificationBean> fetchProposalDueDateAlertDetails(Map aoParamMap);
	
	public List<String> fetchApprovedProvidersList(Map<String,String> aoParamMap);
	
	public List<SMAlertNotificationBean> fetchRfpReleaseDueDateAlertDetails(Map aoParamMap);
	
	public List<SMAlertNotificationBean> fetchFirstRoundEvaluationDueDateAlertDetails(Map aoParamMap);
	
	public List<SMAlertNotificationBean> fetchFinalEvaluationDueDateAlertDetails(Map aoParamMap);
	
	public List<HashMap<String, String>> filterNotificationListForList(Map aoParamMap);
	
	public List<HashMap<String, String>> filterNotificationListForString(Map aoParamMap);
	
	public int deleteConditionallyApprovedExpiredServiceApp(Map aoHashMap);
	
	public int setExpiryDatetoNullForExpiredSA(Map aoHashMap);
	
	public List<CityUserDetailsBeanForBatch> fetchCityUserDetailsForBatch(String  asActiveFlag);
	
	public int updateUserDNCityUserDetailsForBatch(Map aoHashMap);
	
	public List<ApplicationIdBRStatusBean> fetchApplicationIdBRStatus(HashMap aoHashMap);
	
	public String fetchBusinessApplicationCount(HashMap aoHashMap);
	
	List getAllNYCAgencyId();
	
	public int deleteErroneousFromSuperseding(Map aoHashMap);
	
	public List<SupersedingStatusBean> fetchSupersedingStatusBeforeDelete(HashMap aoHashMap);
	
	public String fetchSupersedingStatusCount(HashMap aoHashMap);
	
	public Boolean deleteEventFromSupersedingStatusForProvider(HashMap aoHashMap);
	
	public List<ApplicationIdStatusBean> fetchLatestBADetailsForProvider(HashMap aoHashMap);
}
