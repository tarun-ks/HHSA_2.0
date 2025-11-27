package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;

import com.nyc.hhs.model.AssigneeList;
import com.nyc.hhs.model.DefaultAssignment;
import com.nyc.hhs.model.TaskDetailsBean;
/**
 * <p>
 * ApplicationMapper is an interface between the DAO and database layer
 * This file contains queries for default 
 * user notification screen
 * </p>
 */
public interface UserRoleMapper
{
	public DefaultAssignment fetchDefaultAssigneeDetails(HashMap<String, Object> aoHMArgs);

	public int deleteDafaultAssignment(DefaultAssignment aoDefaultAssignmentBean);

	public int deleteDafaultRessignment(DefaultAssignment aoDefaultAssignmentBean);

	public int insertDafaultAssignment(DefaultAssignment aoDefaultAssignmentBean);

	public int updateDafaultAssignment(DefaultAssignment aoDefaultAssignmentBean);

	public int updateDafaultAssignmentAskAgain(DefaultAssignment aoDefaultAssignmentBean);

	public String getContractForContractReviewTask(String aoEntityId);

	public String getContractForBudgetReviewTask(String aoEntityId);

	public String getContractForInvoiceReviewTask(String aoEntityId);

	public String getContractForPaymentReviewTask(String aoEntityId);

	public String getContractForAdvanceReviewTask(String aoEntityId);

	public String checkAskAgainFlag(HashMap<String, Object> aoHmap);

	public List<AssigneeList> getReassigneeList(HashMap<String, Object> aoHmap);

	public TaskDetailsBean getAssigneeData(HashMap<String, Object> aoHmap);

	public void updateReviewLevelsDetails();
	
	public String fetchReviewLevelsDefaulAssignment(HashMap aoHashMap);
	
	public DefaultAssignment getAssigneeReturnForRevision(HashMap<String,Integer> aoHmap);
	
	//Added for R6- Return Payment Review Task
	public String getContractForReturnPaymentReviewTask(String aoEntityId);
	//Added for R6- Return Payment Review Task end
	
}
