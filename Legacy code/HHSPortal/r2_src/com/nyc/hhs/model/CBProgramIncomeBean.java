package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

public class CBProgramIncomeBean extends CBGridBean
{

	//@RegExp(value ="^\\d{0,22}")
	private String id = HHSConstants.EMPTY_STRING;
	private String programIncomeId = HHSConstants.EMPTY_STRING;
	@RegExp(value ="^\\d{0,22}")
	private String parentId = HHSConstants.EMPTY_STRING;
	private String programTitle = HHSConstants.EMPTY_STRING;
	@RegExp(value ="^\\d{0,22}")
	private String budgetId = HHSConstants.EMPTY_STRING; // Already defined in
															// CBGridBean
	@RegExp(value ="^\\d{0,22}")
	private String subBudgetId = HHSConstants.EMPTY_STRING;// Already defined in
															// CBGridBean
	private String fYBudget = HHSConstants.STRING_ZERO;
	private String income = HHSConstants.STRING_ZERO;
	private String remainingAmount = HHSConstants.STRING_ZERO;
	private String programIncomeTypeId = HHSConstants.EMPTY_STRING;
	private String budgetType = HHSConstants.EMPTY_STRING;// Already defined in
															// CBGridBean
	private String approvedFYBudget = HHSConstants.STRING_ZERO;
	private String modificationAmount = HHSConstants.STRING_ZERO;
	private String proposedBudget = HHSConstants.STRING_ZERO;
	private String amendmentAmount = HHSConstants.STRING_ZERO;
	private String activeFlag = HHSConstants.ONE;
	private String prevApprovedBudget = HHSConstants.EMPTY_STRING;
	//Added in R7 for program income
	private String entryTypeId = HHSConstants.EMPTY_STRING;
	private String description = HHSConstants.EMPTY_STRING;
	/**
	 * @return the entryTypeId
	 */
	public String getEntryTypeId()
	{
		return entryTypeId;
	}
	/**
	 * @param set entryTypeId
	 */
	public void setEntryTypeId(String entryTypeId)
	{
		this.entryTypeId = entryTypeId;
	}
	/**
	 * @return the description
	 */
	public String getDescription()
	{
		return description;
	}
	/**
	 * @param set description
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
    //R7 changes end
	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the programIncomeId
	 */
	public String getProgramIncomeId()
	{
		return programIncomeId;
	}

	/**
	 * @param programIncomeId the programIncomeId to set
	 */
	public void setProgramIncomeId(String programIncomeId)
	{
		this.programIncomeId = programIncomeId;
	}

	/**
	 * @return the programTitle
	 */
	public String getProgramTitle()
	{
		return programTitle;
	}

	/**
	 * @param programTitle the programTitle to set
	 */
	public void setProgramTitle(String programTitle)
	{
		this.programTitle = programTitle;
	}

	/**
	 * @return the budgetId
	 */
	public String getBudgetId()
	{
		return budgetId;
	}

	/**
	 * @param budgetId the budgetId to set
	 */
	public void setBudgetId(String budgetId)
	{
		this.budgetId = budgetId;
	}

	/**
	 * @return the subBudgetId
	 */
	public String getSubBudgetId()
	{
		return subBudgetId;
	}

	/**
	 * @param subBudgetId the subBudgetId to set
	 */
	public void setSubBudgetId(String subBudgetId)
	{
		this.subBudgetId = subBudgetId;
	}

	/**
	 * @return the fYBudget
	 */
	public String getfYBudget()
	{
		return fYBudget;
	}

	/**
	 * @param fYBudget the fYBudget to set
	 */
	public void setfYBudget(String fYBudget)
	{
		this.fYBudget = fYBudget;
	}

	/**
	 * @return the fYBudget
	 */
	public String getFYBudget()
	{
		return fYBudget;
	}

	/**
	 * @param fYBudget the fYBudget to set
	 */
	public void setFYBudget(String fYBudget)
	{
		this.fYBudget = fYBudget;
	}

	/**
	 * @return the income
	 */
	public String getIncome()
	{
		return income;
	}

	/**
	 * @param income the income to set
	 */
	public void setIncome(String income)
	{
		this.income = income;
	}

	/**
	 * @return the budgetType
	 */
	public String getBudgetType()
	{
		return budgetType;
	}

	/**
	 * @param budgetType the budgetType to set
	 */
	public void setBudgetType(String budgetType)
	{
		this.budgetType = budgetType;
	}

	/**
	 * @return the approvedFYBudget
	 */
	public String getApprovedFYBudget()
	{
		return approvedFYBudget;
	}

	/**
	 * @param approvedFYBudget the approvedFYBudget to set
	 */
	public void setApprovedFYBudget(String approvedFYBudget)
	{
		this.approvedFYBudget = approvedFYBudget;
	}

	/**
	 * @return the modificationAmount
	 */
	public String getModificationAmount()
	{
		return modificationAmount;
	}

	/**
	 * @param modificationAmount the modificationAmount to set
	 */
	public void setModificationAmount(String modificationAmount)
	{
		this.modificationAmount = modificationAmount;
	}

	/**
	 * @return the proposedBudget
	 */
	public String getProposedBudget()
	{
		return proposedBudget;
	}

	/**
	 * @param proposedBudget the proposedBudget to set
	 */
	public void setProposedBudget(String proposedBudget)
	{
		this.proposedBudget = proposedBudget;
	}

	/**
	 * @return the prevApprovedBudget
	 */
	public String getPrevApprovedBudget()
	{
		return prevApprovedBudget;
	}

	/**
	 * @param prevApprovedBudget the prevApprovedBudget to set
	 */
	public void setPrevApprovedBudget(String prevApprovedBudget)
	{
		this.prevApprovedBudget = prevApprovedBudget;
	}

	/**
	 * @return the amendmentAmount
	 */
	public String getAmendmentAmount()
	{
		return amendmentAmount;
	}

	/**
	 * @return the remainingAmount
	 */
	public String getRemainingAmount()
	{
		return remainingAmount;
	}

	/**
	 * @param remainingAmount the remainingAmount to set
	 */
	public void setRemainingAmount(String remainingAmount)
	{
		this.remainingAmount = remainingAmount;
	}

	/**
	 * @param amendmentAmount the amendmentAmount to set
	 */
	public void setAmendmentAmount(String amendmentAmount)
	{
		this.amendmentAmount = amendmentAmount;
	}

	/**
	 * @return the programIncomeTypeId
	 */
	public String getProgramIncomeTypeId()
	{
		return programIncomeTypeId;
	}

	/**
	 * @param programIncomeTypeId the programIncomeTypeId to set
	 */
	public void setProgramIncomeTypeId(String programIncomeTypeId)
	{
		this.programIncomeTypeId = programIncomeTypeId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(String parentId)
	{
		this.parentId = parentId;
	}

	/**
	 * @return the parentId
	 */
	public String getParentId()
	{
		return parentId;
	}

	/**
	 * @return the activeFlag
	 */
	public String getActiveFlag()
	{
		return activeFlag;
	}

	/**
	 * @param activeFlag the activeFlag to set
	 */
	public void setActiveFlag(String activeFlag)
	{
		this.activeFlag = activeFlag;
	}

	@Override
	public String toString()
	{
		return "CBProgramIncomeBean [id=" + id + ", programIncomeId=" + programIncomeId + ", parentId=" + parentId
				+ ", programTitle=" + programTitle + ", budgetId=" + budgetId + ", subBudgetId=" + subBudgetId
				+ ", fYBudget=" + fYBudget + ", income=" + income + ", remainingAmount=" + remainingAmount
				+ ", programIncomeTypeId=" + programIncomeTypeId + ", budgetType=" + budgetType + ", approvedFYBudget="
				+ approvedFYBudget + ", modificationAmount=" + modificationAmount + ", proposedBudget="
				+ proposedBudget + ", amendmentAmount=" + amendmentAmount + ", activeFlag=" + activeFlag
				+ ", prevApprovedBudget=" + prevApprovedBudget+ ", description=" + description+", " 
				+"entryTypeId=" + entryTypeId + "]";
	}

}
