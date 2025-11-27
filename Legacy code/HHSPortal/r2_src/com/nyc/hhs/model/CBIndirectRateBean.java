package com.nyc.hhs.model;

//import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;

public class CBIndirectRateBean extends CBGridBean
{

	//@RegExp(value ="^\\d{0,22}")
	//INDIRECT_RATE_ID, parent_id
	private String id = HHSConstants.ONE;
	private String indirectAmount = HHSConstants.STRING_ZERO;
	//@RegExp(value ="^\\d{0,22}")
	private String indirectRate = HHSConstants.STRING_ZERO;
	private String budgetType;
	private String ytdInvoiceAmount = HHSConstants.STRING_ZERO;
	private String indirectRemainingAmount = HHSConstants.STRING_ZERO;
	private String indirectCost = HHSConstants.INDIRECT_COST;
	private String indirectInvoiceAmount = HHSConstants.STRING_ZERO;
	private String indirectModificationAmount = HHSConstants.STRING_ZERO;
	private String indirectProposedBudget = HHSConstants.STRING_ZERO;
	private String prevApprovedAmount = HHSConstants.EMPTY_STRING;
	//Start : Added in R7 for Program income
	private String indirectPIRate=HHSR5Constants.STRING_ZERO;
	
	public String getIndirectPIRate()
	{
		return indirectPIRate;
	}

	public void setIndirectPIRate(String indirectPIRate)
	{
		this.indirectPIRate = indirectPIRate;
	}
   // End : R7 program income changes
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getIndirectAmount()
	{
		return indirectAmount;
	}

	public void setIndirectAmount(String indirectAmount)
	{
		this.indirectAmount = indirectAmount;
	}

	public String getIndirectRate()
	{
		return indirectRate;
	}

	public void setIndirectRate(String indirectRate)
	{
		this.indirectRate = indirectRate;
	}

	public String getBudgetType()
	{
		return budgetType;
	}

	public void setBudgetType(String budgetType)
	{
		this.budgetType = budgetType;
	}

	public String getYtdInvoiceAmount()
	{
		return ytdInvoiceAmount;
	}

	public void setYtdInvoiceAmount(String ytdInvoiceAmount)
	{
		this.ytdInvoiceAmount = ytdInvoiceAmount;
	}

	public String getIndirectRemainingAmount()
	{
		return indirectRemainingAmount;
	}

	public void setIndirectRemainingAmount(String indirectRemainingAmount)
	{
		this.indirectRemainingAmount = indirectRemainingAmount;
	}

	public String getIndirectCost()
	{
		return indirectCost;
	}

	public void setIndirectCost(String indirectCost)
	{
		this.indirectCost = indirectCost;
	}

	public void setIndirectInvoiceAmount(String indirectInvoiceAmount)
	{
		this.indirectInvoiceAmount = indirectInvoiceAmount;
	}

	public String getIndirectInvoiceAmount()
	{
		return indirectInvoiceAmount;
	}

	public void setIndirectModificationAmount(String indirectModificationAmount)
	{
		this.indirectModificationAmount = indirectModificationAmount;
	}

	public String getIndirectModificationAmount()
	{
		return indirectModificationAmount;
	}

	public void setIndirectProposedBudget(String indirectProposedBudget)
	{
		this.indirectProposedBudget = indirectProposedBudget;
	}

	public String getIndirectProposedBudget()
	{
		return indirectProposedBudget;
	}

	public void setPrevApprovedAmount(String prevApprovedAmount)
	{
		this.prevApprovedAmount = prevApprovedAmount;
	}

	public String getPrevApprovedAmount()
	{
		return prevApprovedAmount;
	}

	@Override
	public String toString()
	{
		return "CBIndirectRateBean [id=" + id + ", indirectAmount=" + indirectAmount + ", indirectRate=" + indirectRate
				+ ", budgetType=" + budgetType + ", ytdInvoiceAmount=" + ytdInvoiceAmount
				+ ", indirectRemainingAmount=" + indirectRemainingAmount + ", indirectCost=" + indirectCost
				+ ", indirectInvoiceAmount=" + indirectInvoiceAmount + ", indirectModificationAmount="
				+ indirectModificationAmount + ", indirectProposedBudget=" + indirectProposedBudget
				+ ", prevApprovedAmount=" + prevApprovedAmount + "]";
	}
}
