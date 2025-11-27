package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.model.NotificationAlertMasterBean;
import com.nyc.hhs.model.NotificationBean;
import com.nyc.hhs.model.NotificationParamBean;
import com.nyc.hhs.model.NotificationURLBean;
import com.nyc.hhs.model.ReturnedPayment;
import com.nyc.hhs.model.UserEmailIdBean;

/**
 * NotificationMapper is an interface between the DAO and database layer for
 * Notification to retrieve, insert and update entries.
 * This class Updated in R6 for Return Payment.
 * 
 */

public interface NotificationMapper
{
	public void insertInUserNotification(NotificationBean aoNotificationBean);

	public Integer updateNotificationStatus(HashMap<String, Object> aoNotificationMap);

	public Integer updateGroupNotificationStatus(HashMap<String, Object> aoNotificationMap);

	public void insertNotAlertMaster(HashMap aoNotificationMap);

	public List<NotificationAlertMasterBean> getNotificationAlertMasterDetails(HashMap aoNotificationMap);

	public void insertIntoGroupNotificationTable(NotificationBean aoNotificationBean);

	public Integer getNextGroupNotificationId();

	public Integer getNextNotificationAlertUrlId();

	public List<NotificationBean> getUnsentGroupNotificationsList();

	public void insertIntoNotificationAlertUrl(HashMap<String, Object> aoUrlParamMap);

	public void updateGroupNotificationTable(HashMap<String, Object> aoMsgBodyMap);

	public List<UserEmailIdBean> getUserEmailIds(HashMap<String, Object> aoOrgGroupMap);

	public List<UserEmailIdBean> getUserEmailIds012(HashMap<String, Object> aoOrgGroupMap);

	public void insertInNotificationTable(NotificationBean aoNotificationBean);

	public NotificationURLBean getUrlNotificationDetails(String asNotificationUrlId);

	public List<UserEmailIdBean> getAgencyContacts(String asEntityId);

	public List<UserEmailIdBean> fetchUserEmailIdsForNT220(String asProposalId);

	public List<UserEmailIdBean> fetchCityUserNT234(HashMap<String, Object> aoEntityDetails);

	public List<UserEmailIdBean> fetchCityUserNT232(HashMap<String, Object> aoEntityDetails);

	public List<UserEmailIdBean> fetchUserEmailIdsForNT210(HashMap<String, Object> aoEntityDetails);

	public List<UserEmailIdBean> fetchDataForIndividualUser(String asUserId);

	public List<UserEmailIdBean> fetchInitiatorsDetails(HashMap<String, String> aoParamMap);

	public void insertInNotificationParams(NotificationParamBean aoNotificationParamBean);

	public List<NotificationParamBean> fetchNotificationsParams(Integer aoGroupNotificationId);

	public List<UserEmailIdBean> fetchBulkUploadNotificationData(String asEntityId);

	public List<UserEmailIdBean> fetchAgencyUsersForDocumentSharing(HashMap<String, Object> aoParamMap);

	public List<UserEmailIdBean> getUserEmailIds019(HashMap<String, Object> aoOrgGroupMap);

	public List<UserEmailIdBean> fetchInitiatorsDetailsForAL305(HashMap<String, String> aoParamMap);

	public List<UserEmailIdBean> fetchUserIdForEmailId(String aoEmailId);

	// Start || Added for enhancement 5978 for Release 3.11.0
	public String fetchAgencyUserIdForNT225(String aoEntityId);

	public String fetchAgencyUserIdForProc(String aoEntityId);

	public List<UserEmailIdBean> fetchUsersForNT225(HashMap<String, Object> aoOrgGroupMap);

	// End ||Added for enhancement 5978 for Release 3.11.0

	// Start || Added for enhancement 6620 for Release 3.11.0
	public String fetchAgencyNameFromBudget(String aoEntityId);

	public String fetchAgencyNameFromInvoice(String aoEntityId);

	public String fetchCommentsForAL314(Map aoHashMap);

	public String fetchAgencyNameForNT207AL212(String aoEntityId);

	// End || Added for enhancement 6620 for Release 3.11.0
	
	//Added in R6 for Return Payment
	public String fetchMaxGroupNotificationId();
	
	public List<ReturnedPayment> fetchUpdatedNotificationId(HashMap<String, String> aoHashMap);
	
	public String fetchNotificationSequenceMappingId();
	
	public Boolean insertIntoPaymentNotificationMapping(HashMap<String, String> aoHashMap);
	
	public List<UserEmailIdBean> getUserEmailIdsForBulkNotification(HashMap<String, Object> aoOrgGroupMap);
		
	public List<BudgetList> getBulkInputRequestData(String asExportStatus);
	
	public List<BudgetList> downloadFileData(Map<String,Object> aoHashMap);
	
	public List<UserEmailIdBean> fetchCityUserNTEXPORT(HashMap<String, Object> aoEntityDetails);

	public List<UserEmailIdBean> fetchCityUserNT_BULK_ACK(HashMap<String, Object> aoEntityDetails);

	public List<NotificationBean> fetchAckNotificationList();
	//R6 End
	
	//[Start]Added in R7.4.0 for QC9134
	//public void rewriteMsgBodyNT318ByAgency();
	//new added for QC 9630 R 9.0.1
	public void rewriteMsgBodyNT318ByAgency(HashMap<String, Object> loAgencyDescMap);
		
    public List<UserEmailIdBean> getUserEmailIdsNT318(HashMap<String, Object>  aoHashMap);
    //[End]Added in R7.4.0 for QC9134


    //[Start]Added in R8.1.0 for 9165
    public List<Integer> fetchOldUserNotification();
    public Integer archiveUserNotificationData(HashMap<String, Object> aoHashMap);
    public Integer shrinkUserNotificationData(HashMap<String, Object> aoHashMap);

    
    public List<Integer> fetchOldNotificationAlertUrl();
    public Integer archiveNotificationAlertUrlData(HashMap<String, Object> aoHashMap);
    public Integer shrinkNotificationAlertUrlData(HashMap<String, Object> aoHashMap);
    //[End]Added in R8.1.0 for 9165
    
    
    //[Start]Added in R8.4.1 for 9513
    public List<Integer> fetchOldGroupNotification();
    public Integer archiveGroupNotificationData(HashMap<String, Object> aoHashMap);
    public Integer shrinkGroupNotificationData(HashMap<String, Object> aoHashMap);

    public Integer archiveNotificationData(HashMap<String, Object> aoHashMap);
    public Integer shrinkNotificationData(HashMap<String, Object> aoHashMap);

    public Integer archiveNotificationParamValData(HashMap<String, Object> aoHashMap);
    public Integer shrinkNotificationParamValData(HashMap<String, Object> aoHashMap);

    public Integer archiveReturnPaymentNotifHistoryData(HashMap<String, Object> aoHashMap);
    public Integer shrinkReturnPaymentNotifHistoryData(HashMap<String, Object> aoHashMap);

    public Integer archiveReturnPaymentNotifMappingData(HashMap<String, Object> aoHashMap);
    public Integer shrinkReturnPaymentNotifMappingData(HashMap<String, Object> aoHashMap);
    //[End]Added in R8.4.1 for 9513
    // Start QC9630 R 9.0.1 - Optimize NT318 Notifications
    public List<NotificationBean> fetchNT318Description();
    public void updateNotificationStatusNT318();
    // End QC9630 R 9.0.1 - Optimize NT318 Notifications
}


