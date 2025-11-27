package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;

import com.nyc.hhs.model.ApplicationAuditBean;

/**
 *  TaskHistoryMapper is an interface between the DAO and database layer 
 *  to fetch task history & comments from different tables.
 *  
 */
public interface TaskHistoryMapper {

	void updateTaskHistoryAudit(ApplicationAuditBean aoApplicationAudit);
	
	List<ApplicationAuditBean> fetchTaskHistoryAudit(HashMap aoHMApplicationAudit);
	List<ApplicationAuditBean> fetchTaskHistoryWithdrawalAudit(HashMap aoHMApplicationAudit);
	List<ApplicationAuditBean> fetchAllAppTaskHistoryAudit(HashMap aoHMApplicationAudit);
	List<ApplicationAuditBean> fetchLastProviderComments(HashMap aoHMApplicationAudit);
	List<ApplicationAuditBean> fetchLastProviderCommentsWithdrawal(HashMap aoHMApplicationAudit);
	List<ApplicationAuditBean> fetchBappComments(HashMap aoHMApplicationAudit);
	List<ApplicationAuditBean> fetchLastProviderCommentsGeneral(HashMap aoHMApplicationAudit);
	List<ApplicationAuditBean> fetchTaskHistoryAuditGeneral(HashMap aoHMApplicationAudit);
	String checkServiceTaskStatus(String asSectionId);
	void updateAuditStatus(HashMap aoProps);
	void updateAuditStatusWithdrawal(HashMap aoProps);
	void updateAuditforDoc(HashMap aoProps);
}
