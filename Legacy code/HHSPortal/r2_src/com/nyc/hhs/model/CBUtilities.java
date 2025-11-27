package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

public class CBUtilities extends CBGridBean
{

	private String budgetType;
	@Length(max = 50)
	private String utilitiesDesc;
	private String fyAmount = HHSConstants.STRING_ZERO;
	private String invoiceAmount = HHSConstants.STRING_ZERO;
	private String remainingAmt = HHSConstants.STRING_ZERO;
	private String id;
	//@RegExp(value ="^\\d{0,22}")
	private String utilitiesTypeID;
	private String lineItemInvoiceAmt = HHSConstants.STRING_ZERO;
	private String lineItemModifiedAmt = HHSConstants.STRING_ZERO;
	private String proposedBudget = HHSConstants.STRING_ZERO;
	private String prevApprovedBudget = HHSConstants.STRING_ZERO;

	/**
	 * @return prevApprovedBudget
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
	 * @return proposedBudget
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
	 * @return lineItemModifiedAmt
	 */
	public String getLineItemModifiedAmt()
	{
		return lineItemModifiedAmt;
	}

	/**
	 * @param lineItemModifiedAmt the lineItemModifiedAmt to set
	 */
	public void setLineItemModifiedAmt(String lineItemModifiedAmt)
	{
		this.lineItemModifiedAmt = lineItemModifiedAmt;
	}

	/**
	 * @return lineItemInvoiceAmt
	 */
	public String getLineItemInvoiceAmt()
	{
		return lineItemInvoiceAmt;
	}

	/**
	 * @param lineItemInvoiceAmt the lineItemInvoiceAmt to set
	 */
	public void setLineItemInvoiceAmt(String lineItemInvoiceAmt)
	{
		this.lineItemInvoiceAmt = lineItemInvoiceAmt;
	}

	/**
	 * @return utilitiesTypeID
	 */
	public String getUtilitiesTypeID()
	{
		return utilitiesTypeID;
	}

	/**
	 * @param utilitiesTypeID the utilitiesTypeID to set
	 */
	public void setUtilitiesTypeID(String utilitiesTypeID)
	{
		this.utilitiesTypeID = utilitiesTypeID;
	}

	/**
	 * @return budgetType
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
	 * @return utilitiesDesc
	 */
	public String getUtilitiesDesc()
	{
		return utilitiesDesc;
	}

	/**
	 * @param utilitiesDesc the utilitiesDesc to set
	 */
	public void setUtilitiesDesc(String utilitiesDesc)
	{
		this.utilitiesDesc = utilitiesDesc;
	}

	/**
	 * @return fyAmount
	 */
	public String getFyAmount()
	{
		return fyAmount;
	}

	/**
	 * @param fyAmount the fyAmount to set
	 */
	public void setFyAmount(String fyAmount)
	{
		this.fyAmount = fyAmount;
	}

	/**
	 * @return invoiceAmount
	 */
	public String getInvoiceAmount()
	{
		return invoiceAmount;
	}

	/**
	 * @param invoiceAmount the invoiceAmount to set
	 */
	public void setInvoiceAmount(String invoiceAmount)
	{
		this.invoiceAmount = invoiceAmount;
	}

	/**
	 * @return remainingAmt
	 */
	public String getRemainingAmt()
	{
		return remainingAmt;
	}

	/**
	 * @param remainingAmt the remainingAmt to set
	 */
	public void setRemainingAmt(String remainingAmt)
	{
		this.remainingAmt = remainingAmt;
	}

	/**
	 * @return id
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

	@Override
	public String toString()
	{
		return "CBUtilities [budgetType=" + budgetType + ", utilitiesDesc=" + utilitiesDesc + ", fyAmount=" + fyAmount
				+ ", invoiceAmount=" + invoiceAmount + ", remainingAmt=" + remainingAmt + ", id=" + id
				+ ", utilitiesTypeID=" + utilitiesTypeID + ", lineItemInvoiceAmt=" + lineItemInvoiceAmt
				+ ", lineItemModifiedAmt=" + lineItemModifiedAmt + ", proposedBudget=" + proposedBudget
				+ ", prevApprovedBudget=" + prevApprovedBudget + "]";
	}

}
