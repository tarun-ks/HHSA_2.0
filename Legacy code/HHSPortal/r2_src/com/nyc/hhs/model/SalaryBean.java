package com.nyc.hhs.model;

import com.nyc.hhs.constants.HHSConstants;

/**
 * This class is created for grid poc purpose. Should be deleted later
 */

public class SalaryBean
{

	private String id = HHSConstants.EMPTY_STRING;
	private String salEmp = HHSConstants.EMPTY_STRING;
	private String fte = HHSConstants.EMPTY_STRING;
	private String budget = HHSConstants.EMPTY_STRING;
	private String amendmentAmount = HHSConstants.EMPTY_STRING;

	/**
	 * @return the id
	 */
	public final String getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the salEmp
	 */
	public final String getSalEmp()
	{
		return salEmp;
	}

	/**
	 * @param salEmp the salEmp to set
	 */
	public final void setSalEmp(String salEmp)
	{
		this.salEmp = salEmp;
	}

	/**
	 * @return the fte
	 */
	public final String getFte()
	{
		return fte;
	}

	/**
	 * @param fte the fte to set
	 */
	public final void setFte(String fte)
	{
		this.fte = fte;
	}

	/**
	 * @return the budget
	 */
	public final String getBudget()
	{
		return budget;
	}

	/**
	 * @param budget the budget to set
	 */
	public final void setBudget(String budget)
	{
		this.budget = budget;
	}

	/**
	 * @return the amendmentAmount
	 */
	public final String getAmendmentAmount()
	{
		return amendmentAmount;
	}

	/**
	 * @param amendmentAmount the amendmentAmount to set
	 */
	public final void setAmendmentAmount(String amendmentAmount)
	{
		this.amendmentAmount = amendmentAmount;
	}

	@Override
	public String toString()
	{
		return "SalaryBean [id=" + id + ", salEmp=" + salEmp + ", fte=" + fte + ", budget=" + budget
				+ ", amendmentAmount=" + amendmentAmount + "]";
	}

}
