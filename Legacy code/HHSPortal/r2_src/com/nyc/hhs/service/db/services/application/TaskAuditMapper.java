package com.nyc.hhs.service.db.services.application;

import java.util.List;

import com.nyc.hhs.model.TaskAuditBean;

public interface TaskAuditMapper
{
	public void insertTaskAudit(TaskAuditBean aoTaskAuditBean);

	public void insertTaskAuditForLaunch(TaskAuditBean aoTaskAuditBean);

	public void updateEndDateForPreviousStep(TaskAuditBean aoTaskAuditBean);
	
	/* [Start] R7.4.0:QC 9008 Added for deleting contract budget update */
	public List<TaskAuditBean> fetchMaxBudgetUpdateAuditDataByContractId(String asContractId);
    public void insertAuditForDeletingBudgetUpdate(TaskAuditBean aoTaskAuditBean);
	/* [End] R7.4.0:QC 9008 Added for deleting contract budget update */


}
