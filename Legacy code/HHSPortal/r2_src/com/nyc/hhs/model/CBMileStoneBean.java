package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

public class CBMileStoneBean extends CBGridBean
{

	//@RegExp(value ="^\\d{0,22}")
	//MILESTONE_ID
	private String id = HHSConstants.EMPTY_STRING;
	@Length(max = 50)
	private String mileStone = HHSConstants.EMPTY_STRING;
	private String amount = HHSConstants.STRING_ZERO;
	private String invoiceAmount = HHSConstants.STRING_ZERO;
	private String remainAmt = HHSConstants.STRING_ZERO;

	private String modificationAmount = HHSConstants.STRING_ZERO;
	private String proposedAmount = HHSConstants.STRING_ZERO;
	private String parentId = HHSConstants.EMPTY_STRING;

	/**
	 * @return the remainAmt
	 */
	public String getRemainAmt()
	{
		return remainAmt;
	}

	/**
	 * @param remainAmt the remainAmt to set
	 */
	public void setRemainAmt(String remainAmt)
	{
		this.remainAmt = remainAmt;
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
	 * @return mileStone
	 */
	public String getMileStone()
	{
		return mileStone;
	}

	/**
	 * @param mileStone - mileStone to set
	 */
	public void setMileStone(String mileStone)
	{
		this.mileStone = mileStone;
	}

	/**
	 * @return amount
	 */
	public String getAmount()
	{
		return amount;
	}

	/**
	 * @param amount - amount to set
	 */
	public void setAmount(String amount)
	{
		this.amount = amount;
	}

	/**
	 * @return invoiceAmount
	 */
	public String getInvoiceAmount()
	{
		return invoiceAmount;
	}

	/**
	 * @param invoiceAmount - invoiceAmount to set
	 */
	public void setInvoiceAmount(String invoiceAmount)
	{
		this.invoiceAmount = invoiceAmount;
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
	 * @return the proposedAmount
	 */
	public String getProposedAmount()
	{
		return proposedAmount;
	}

	/**
	 * @param proposedAmount the proposedAmount to set
	 */
	public void setProposedAmount(String proposedAmount)
	{
		this.proposedAmount = proposedAmount;
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

	@Override
	public String toString()
	{
		return "CBMileStoneBean [id=" + id + ", mileStone=" + mileStone + ", amount=" + amount + ", invoiceAmount="
				+ invoiceAmount + ", remainAmt=" + remainAmt + ", modificationAmount=" + modificationAmount
				+ ", proposedAmount=" + proposedAmount + ", parentId=" + parentId + "]";
	}
}
