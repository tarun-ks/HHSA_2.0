package com.nyc.hhs.service.db.services.application;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nyc.hhs.model.ApplicationAuditBean;
import com.nyc.hhs.model.CommentsHistoryBean;
import com.nyc.hhs.model.EvaluationDetailBean;

/**
 * This Mapper helps to set data into ApplicationAuditBean and get Filings
 * Information
 * 
 */
public interface AuditHistoryMapper
{
	public List<ApplicationAuditBean> fetchOrganizationFilingsAuditView(ApplicationAuditBean aoApplicationAuditBean);
	
	public List<String> fetchOrganizationFilingsAuditViewFilingDropDown(ApplicationAuditBean aoApplicationAuditBean);
	
	public Integer fetchOrganizationFilingsAuditViewCount(ApplicationAuditBean aoApplicationAuditBean);
	
	public Map getFilingsInformationHomePage(String asOrgId);
	
	public List<CommentsHistoryBean> fetchProposalTaskHistory(HashMap aoHMApplicationAudit);
	
	public List<EvaluationDetailBean> getEvaluationProgress(Map<String, String> aoParameterMap);
	
	public int exportAllTask(HashMap<String, String> aoParameterMap);
}
