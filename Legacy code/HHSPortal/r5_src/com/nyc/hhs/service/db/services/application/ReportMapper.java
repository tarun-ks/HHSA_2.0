package com.nyc.hhs.service.db.services.application;

import java.util.List;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.model.ProposalStatusInfo;
import com.nyc.hhs.model.ReportBean;
/**
 *This is a mapper class which has queries 
 *to perform functions and get data for 
 *Report class
 */

public interface ReportMapper
{
	
	List<ReportBean> getBudgetUtlizationDetails(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getBudgetUtlizationDetails_ForGrid(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getBudgetUtlizationDetails_ForGridInvoice(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getBudgetCatUtilization(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getBudgetCatUtilization_ForGrid(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getFundingSummaryDetails(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getProposalSummaryDetails(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getFundingSummaryDetails_ForGrid(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getRecievablesDetails(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getRecievablesDetailsChart(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getRecievablesDetails_ForGrid(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getAdvRecoupment(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getAdvRecoupmentChart(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getAdvRecoupment_ForGrid(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getProposalDetails(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getProposalDetailsChart(ReportBean aoReportBean) throws ApplicationException;
	
	List<ReportBean> getProposalDetails_ForGrid(ReportBean aoReportBean) throws ApplicationException;
	
	List<Integer> getFirstFiscalYear(ReportBean aoReportBean) throws ApplicationException;
	
	List<Integer> getFirstFiscalYearDashBoard(ReportBean aoReportBean) throws ApplicationException;
	
	List<ProposalStatusInfo> getProposalStatusInfo() throws ApplicationException;
	
	List<ReportBean> getBudgetUtlizationDetailsUpdated(ReportBean aoReportBean) throws ApplicationException;
	
}
