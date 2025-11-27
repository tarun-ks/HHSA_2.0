package com.nyc.hhs.model;

import java.util.List;

public class MasterBean
{
	private String budgetId;
	private List<LineItemMasterBean> masterBeanList;

	// Start : Amendment Preserve for Fiscal, Advance, Assignment
	private BudgetDetails budgetDetails;
	@SuppressWarnings("rawtypes")
	private List assignmentsSummaryBean; 
	@SuppressWarnings("rawtypes")
	private List advanceSummaryBean;
	// End : Amendment Preserve for Fiscal, Advance, Assignment

	
	public void setBudgetId(String budgetId)
	{
		this.budgetId = budgetId;
	}

	public BudgetDetails getBudgetDetails()
	{
		return budgetDetails;
	}

	public void setBudgetDetails(BudgetDetails budgetDetails)
	{
		this.budgetDetails = budgetDetails;
	}

	

	@SuppressWarnings("rawtypes")
	public List getAssignmentsSummaryBean()
	{
		return assignmentsSummaryBean;
	}

	@SuppressWarnings("rawtypes")
	public void setAssignmentsSummaryBean(List assignmentsSummaryBean)
	{
		this.assignmentsSummaryBean = assignmentsSummaryBean;
	}

	@SuppressWarnings("rawtypes")
	public List getAdvanceSummaryBean()
	{
		return advanceSummaryBean;
	}

	@SuppressWarnings("rawtypes")
	public void setAdvanceSummaryBean(List advanceSummaryBean)
	{
		this.advanceSummaryBean = advanceSummaryBean;
	}

	public String getBudgetId()
	{
		return budgetId;
	}

	public void setMasterBeanList(List<LineItemMasterBean> masterBeanList)
	{
		this.masterBeanList = masterBeanList;
	}

	public List<LineItemMasterBean> getMasterBeanList()
	{
		return masterBeanList;
	}

	@Override
	public String toString()
	{
		return "MasterBean [budgetId=" + budgetId + ", masterBeanList=" + masterBeanList + ", budgetDetails="
				+ budgetDetails + ", assignmentsSummaryBean=" + assignmentsSummaryBean + ", advanceSummaryBean="
				+ advanceSummaryBean + "]";
	}

}
