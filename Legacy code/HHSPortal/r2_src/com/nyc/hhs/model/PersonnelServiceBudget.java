package com.nyc.hhs.model;


import com.nyc.hhs.constants.HHSConstants;

public class PersonnelServiceBudget extends CBGridBean
{

	private String id = HHSConstants.ONE;
	//@RegExp(value ="^\\d{0,22}")
	private String empPosition = HHSConstants.STRING_ZERO;
	private String unit = HHSConstants.STRING_ZERO;
	private String budgetAmount = HHSConstants.STRING_ZERO;
	private String invoicedAmount = HHSConstants.STRING_ZERO;
	private String positionId;
	private String empType;
	private String remainingAmount = HHSConstants.STRING_ZERO;
	private String modificationAmount = HHSConstants.STRING_ZERO;
	private String amendmentAmount = HHSConstants.STRING_ZERO;
	private String proposedBudgetAmount = HHSConstants.STRING_ZERO;
	private String fringeBenifits = HHSConstants.FRINGE_TOTAL;
	private String parentId;
	private String modificationUnit = HHSConstants.STRING_ZERO;
	//@RegExp(value ="^\\d{0,22}")
	private String amendmentUnit = HHSConstants.STRING_ZERO;
	// R6 Changes
	private String internalTitle = HHSConstants.EMPTY_STRING;
	private String annualSalary = HHSConstants.STRING_ZERO;
	private String hourPerYear = HHSConstants.STRING_ZERO;
	private String rate = HHSConstants.STRING_ZERO;
	private String totalPositions;
	private String totalCityFtes;
	private String typeId;
	
	public String getTypeId()
	{
		return typeId;
	}

	public void setTypeId(String typeId)
	{
		this.typeId = typeId;
	}

	public String getTotalCityFtes()
	{
		return totalCityFtes;
	}

	public void setTotalCityFtes(String totalCityFtes)
	{
		this.totalCityFtes = totalCityFtes;
	}

	public String getTotalPositions()
	{
		return totalPositions;
	}

	public void setTotalPositions(String totalPositions)
	{
		this.totalPositions = totalPositions;
	}

	public String getRate()
	{
		return rate;
	}

	public void setRate(String rate)
	{
		this.rate = rate;
	}

	public String getInternalTitle()
	{
		return internalTitle;
	}

	public void setInternalTitle(String internalTitle)
	{
		this.internalTitle = internalTitle;
	}

	public String getAnnualSalary()
	{
		return annualSalary;
	}

	public void setAnnualSalary(String annualSalary)
	{
		this.annualSalary = annualSalary;
	}

	public String getHourPerYear()
	{
		return hourPerYear;
	}

	public void setHourPerYear(String hourPerYear)
	{
		this.hourPerYear = hourPerYear;
	}

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
	 * @return the empPosition
	 */
	public String getEmpPosition()
	{
		return empPosition;
	}

	/**
	 * @param empPosition the empPosition to set
	 */
	public void setEmpPosition(String empPosition)
	{
		this.empPosition = empPosition;
	}

	/**
	 * @return the unit
	 */
	public String getUnit()
	{
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(String unit)
	{
		this.unit = unit;
	}

	/**
	 * @return the budgetAmount
	 */
	public String getBudgetAmount()
	{
		return budgetAmount;
	}

	/**
	 * @param budgetAmount the budgetAmount to set
	 */
	public void setBudgetAmount(String budgetAmount)
	{
		this.budgetAmount = budgetAmount;
	}

	/**
	 * @return the invoicedAmount
	 */
	public String getInvoicedAmount()
	{
		return invoicedAmount;
	}

	/**
	 * @param invoicedAmount the invoicedAmount to set
	 */
	public void setInvoicedAmount(String invoicedAmount)
	{
		this.invoicedAmount = invoicedAmount;
	}

	/**
	 * @return the positionId
	 */
	public String getPositionId()
	{
		return positionId;
	}

	/**
	 * @param positionId the positionId to set
	 */
	public void setPositionId(String positionId)
	{
		this.positionId = positionId;
	}

	/**
	 * @return the empType
	 */
	public String getEmpType()
	{
		return empType;
	}

	/**
	 * @param empType the empType to set
	 */
	public void setEmpType(String empType)
	{
		this.empType = empType;
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
	 * @return the proposedBudgetAmount
	 */
	public String getProposedBudgetAmount()
	{
		return proposedBudgetAmount;
	}

	/**
	 * @param proposedBudgetAmount the proposedBudgetAmount to set
	 */
	public void setProposedBudgetAmount(String proposedBudgetAmount)
	{
		this.proposedBudgetAmount = proposedBudgetAmount;
	}

	/**
	 * @return the fringeBenifits
	 */
	public String getFringeBenifits()
	{
		return fringeBenifits;
	}

	/**
	 * @param fringeBenifits the fringeBenifits to set
	 */
	public void setFringeBenifits(String fringeBenifits)
	{
		this.fringeBenifits = fringeBenifits;
	}

	/**
	 * @return the parentId
	 */
	public String getParentId()
	{
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(String parentId)
	{
		this.parentId = parentId;
	}

	/**
	 * @return the modificationUnit
	 */
	public String getModificationUnit()
	{
		return modificationUnit;
	}

	/**
	 * @param modificationUnit the modificationUnit to set
	 */
	public void setModificationUnit(String modificationUnit)
	{
		this.modificationUnit = modificationUnit;
	}

	public String getAmendmentAmount()
	{
		return amendmentAmount;
	}

	public void setAmendmentAmount(String amendmentAmount)
	{
		this.amendmentAmount = amendmentAmount;
	}

	public String getAmendmentUnit()
	{
		return amendmentUnit;
	}

	public void setAmendmentUnit(String amendmentUnit)
	{
		this.amendmentUnit = amendmentUnit;
	}

	@Override
	public String toString()
	{
		return "PersonnelServiceBudget [id=" + id + ", empPosition=" + empPosition + ", unit=" + unit
				+ ", budgetAmount=" + budgetAmount + ", invoicedAmount=" + invoicedAmount + ", positionId="
				+ positionId + ", empType=" + empType + ", remainingAmount=" + remainingAmount
				+ ", modificationAmount=" + modificationAmount + ", amendmentAmount=" + amendmentAmount
				+ ", proposedBudgetAmount=" + proposedBudgetAmount + ", fringeBenifits=" + fringeBenifits
				+ ", parentId=" + parentId + ", modificationUnit=" + modificationUnit + ", amendmentUnit="
				+ amendmentUnit + ",internalTitle=" + internalTitle + ",annualSalary=" + annualSalary +
				",hourPerYear=" + hourPerYear +",rate=" + rate + ",totalPositions=" + totalPositions + 
				",totalCityFtes=" + totalCityFtes +"]";
	}

}
