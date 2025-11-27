/**
 * 
 */
package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.AgencyDetailsBean;
import com.nyc.hhs.model.AgencySettingsBean;
import com.nyc.hhs.model.AutoApprovalConfigBean;
import com.nyc.hhs.model.BudgetList;
import com.nyc.hhs.model.BulkNotificationList;
import com.nyc.hhs.model.CityUserDetailsBean;
import com.nyc.hhs.model.OrganizationBean;
import com.nyc.hhs.model.ProgramNameInfo;
import com.nyc.hhs.model.ReviewProcessBean;
import com.nyc.hhs.model.TaskDetailsBean;

/**
 * This is a mapper class to execute queries and get
 * data for the Agency settings functionality
 * This class has been updated in R6 for Return Payment. 
 * Method added for Return Payment.
 * This class has been updated in R7 for Modification Auto Approval Enhancement. 
 */
public interface AgencySettingsMapper
{
	public List<AgencyDetailsBean> fetchAgencyNames();

	public List<ReviewProcessBean> fetchReviewProcess(Map<String, Object> aoQueryMap);

	public Integer fetchReviewLevels(Map<String, Object> aoQueryMap);

	public Integer updateReviewLevels(AgencySettingsBean aoAgencySettingsBean);

	public void insertReviewLevels(AgencySettingsBean aoAgencySettingsBean);

	public void deleteLevelUsersViaAccelerator(AgencySettingsBean aoAgencySettingsBean);

	public List<CityUserDetailsBean> fetchAgencyUserNames(Map<String, Object> aoQueryMap);

	public List<CityUserDetailsBean> fetchAssgndUserNames(Map<String, Object> aoQueryMap);

	public List<CityUserDetailsBean> fetchLevel1UsersIfCoFTask(Map<String, Object> aoQueryMap);

	public void deleteLevelUsers(AgencySettingsBean aoAgencySettingsBean);

	public void insertLevelUsers(AgencySettingsBean aoAgencySettingsBean);
	
	//method added as a part of release 3.8.0 enhancement 6534
	public int updateReviewInProgressFlag(Map aoAgencyDetailsMap);
	
	public String fetchReviewInProgressFlag(HashMap<String, String> aoAgencyDetailsMap);
	
	//Added in R6 for Return Payment
	public List<String> fetchFiscalYearsList(String aoAgencyId);
	
	public List<BudgetList> fetchProviderNotificationsList(Map<String, String> aoDetailsMap);
	
	public List<BulkNotificationList> exportBulkNotificationDetails(Map<String, String> aoDetailsMap);
	
	public Integer insertBulkNotificationRequestExport(Map<String, String> aoDetailsMap);
	
	public void updateExportNotificationStatus(Map<String, Object> aoDetailsMap);
	
	public String sequenceForBulkRequest();

	public TaskDetailsBean getCreateDateForExportNotification(String asRequestId);
	
	public List<BudgetList> fetchProviderNotificationListForRecoupTask(Map<String, String> aoDetailsMap);
	
	public List<ProgramNameInfo> getProgramNameNotification(HashMap<String, String> loProgramMap);
	
	// R6 End
	
	//R7 start :Modification Auto Approval Enhancement
	public AutoApprovalConfigBean fetchAutoApprovalThreshold(Map<String, Object> aoQueryMap);
	public Boolean updateAutoApprovalConfig(Map<String, Object> aoQueryMap);
	public Boolean insertAutoApprovalThresholdHistory(String asAgencyId);
	public List<OrganizationBean> fetchAgencyProviders(String asAgencyId);
	public List<AutoApprovalConfigBean> fetchAutoApprovalDetailsList(String asAgencyId);
	public void delAutoApprovalDetails(AutoApprovalConfigBean aoAutoApprovalConfigBean);
	public void addAutoApprovalDetails(AutoApprovalConfigBean aoAutoApprovalConfigBean);
	public void addAutoApprovalDetailsHistory(AutoApprovalConfigBean aoAutoApprovalConfigBean);
	public AutoApprovalConfigBean fetchAutoApproveProvider(AutoApprovalConfigBean aoAutoApprovalConfigBean);
	public void updateAddAutoApprovalDetailsHistory(AutoApprovalConfigBean aoAutoApprovalConfigBean);
	public void editAutoApprovalDetails(AutoApprovalConfigBean aoAutoApprovalConfigBean);
	public Boolean updateAutoApprovalVersionId(String asAgencyId);
	public List<String> fetchAutoApproverUserList();
	//R7 end :Modification Auto Approval Enhancement
	
}
