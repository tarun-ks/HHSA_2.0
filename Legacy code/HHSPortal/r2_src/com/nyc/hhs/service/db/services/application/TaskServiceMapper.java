package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;

import com.nyc.hhs.model.AgencyTaskBean;
import com.nyc.hhs.model.CommentsHistoryBean;
import com.nyc.hhs.model.StaffDetails;
import com.nyc.hhs.model.TaskDetailsBean;
import com.nyc.hhs.model.TaskLookupBean;

/**
 * This mapper interface is used for xml mapping with the TaskServiceMapper.xml
 * all the method which defined here mapped with TaskServiceMapper.xml
 * 
 * */
public interface TaskServiceMapper
{
	public List<CommentsHistoryBean> fetchAgencyTaskHistory(HashMap aoHMApplicationAudit);

	public List<CommentsHistoryBean> fetchAgencyTaskHistoryTabLevel(HashMap aoHMApplicationAudit);

	public List<CommentsHistoryBean> fetchProviderTaskHistory(HashMap aoHMApplicationAudit);

	public List<CommentsHistoryBean> fetchProviderTaskHistoryTabLevel(HashMap aoHMApplicationAudit);

	public List<StaffDetails> fetchAgencyDetails(HashMap aoHMReqdProp);

	public List<StaffDetails> fetchAgencyDetailsForInbox(HashMap aoHMReqdProp);

	public List<StaffDetails> fetchAgencyUserDetails(HashMap aoHMReqdProp);

	public HashMap fetchLastTaskComment(TaskDetailsBean aoTaskDetailsBean);

	public HashMap fetchLastComment(TaskDetailsBean aoTaskDetailsBean);

	public TaskLookupBean fetchTaskIdStatus(TaskLookupBean aoTaskLookupBean);

	public Integer insertTaskLookup(TaskLookupBean aoTaskLookupBean);

	public Integer updateTaskStatus(TaskLookupBean aoTaskLookupBean);

	public List<CommentsHistoryBean> fetchAcceleratorTaskHistory(HashMap aoHMApplicationAudit);

	String fetchCityAgencyUserName(String asUserId);

	String fetchProviderUserName(String asUserId);

	public HashMap getContractInfo(String asContractId);

	public HashMap fetchEvaluationLastComment(TaskDetailsBean aoTaskDetailsBean);

	public HashMap fetchUserCommentsTabLevel(TaskDetailsBean aoTaskDetailsBean);

	public List<String> fetchProviderTabHighlightFromAgencyAudit(HashMap loParamMap);

	public Integer highlightTabInsert(TaskDetailsBean aoTaskDetailsBean);

	public Integer highlightTabUpdate(TaskDetailsBean aoTaskDetailsBean);

	public Integer highlightTabDelete(TaskDetailsBean aoTaskDetailsBean);

	public Integer highlightTabUpdateOnAgencyTaskFinish(TaskDetailsBean aoTaskDetailsBean);

	// Added method in build 3.2.0 for enhancement #6361
	public String fetchSelectedUserAssignedLevel(HashMap<String, String> aoFilterMap);

	// Change for enhancement #6361 End
	// Start Added in R5
	public Integer checkAuditInsert(HashMap aoHMApplicationAudit);

	public HashMap fetchFinalizeAwardLastComment(TaskDetailsBean aoTaskDetailsBean);

	public HashMap fetchInvoiceDetailForTaskExport(AgencyTaskBean aoAgencyTaskBean);

	public String fetchUserNameForTaskExport(AgencyTaskBean aoAgencyTaskBean);

	public List<HashMap> getExportTaskList(String asStatus);
	// End Added in R5
}
