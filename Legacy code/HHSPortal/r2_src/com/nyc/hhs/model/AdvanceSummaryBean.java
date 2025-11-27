package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

import com.nyc.hhs.constants.HHSConstants;

public class AdvanceSummaryBean extends CBGridBean
{
	@Length(max = 20)
	//BUDGET_ADVANCE_ID
	private String id; // this is index key/primary key of table BUDGET_ADVANCE
	private String advanceDesc;
	private String advRequestedDate;
	private String status;
	private String advAmount = HHSConstants.STRING_ZERO;
	private String ytdRecoupmentAmount = HHSConstants.STRING_ZERO;
	private String invoiceRecoupedAmt = HHSConstants.STRING_ZERO;
	private String invoiceId;
	private String invoiceAdvanceId;
	private String ytdRecoupmentPrecent;

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	public String getInvoiceId()
	{
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId)
	{
		this.invoiceId = invoiceId;
	}

	public String getYtdRecoupmentPrecent()
	{
		return ytdRecoupmentPrecent;
	}

	public void setYtdRecoupmentPrecent(String ytdRecoupmentPrecent)
	{
		this.ytdRecoupmentPrecent = ytdRecoupmentPrecent;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * @return the advanceDesc
	 */
	public String getAdvanceDesc()
	{
		return advanceDesc;
	}

	/**
	 * @param advanceDesc the advanceDesc to set
	 */
	public void setAdvanceDesc(String advanceDesc)
	{
		this.advanceDesc = advanceDesc;
	}

	/**
	 * @return the advRequestedDate
	 */
	public String getAdvRequestedDate()
	{
		return advRequestedDate;
	}

	/**
	 * @param advRequestedDate the advRequestedDate to set
	 */
	public void setAdvRequestedDate(String advRequestedDate)
	{
		this.advRequestedDate = advRequestedDate;
	}

	/**
	 * @return the status
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status)
	{
		this.status = status;
	}

	/**
	 * @return the advAmount
	 */
	public String getAdvAmount()
	{
		return advAmount;
	}

	/**
	 * @param advAmount the advAmount to set
	 */
	public void setAdvAmount(String advAmount)
	{
		this.advAmount = advAmount;
	}

	/**
	 * @return the ytdRecoupmentAmount
	 */
	public String getYtdRecoupmentAmount()
	{
		return ytdRecoupmentAmount;
	}

	/**
	 * @param ytdRecoupmentAmount the ytdRecoupmentAmount to set
	 */
	public void setYtdRecoupmentAmount(String ytdRecoupmentAmount)
	{
		this.ytdRecoupmentAmount = ytdRecoupmentAmount;
	}

	/**
	 * @return the invoiceRecoupedAmt
	 */
	public String getInvoiceRecoupedAmt()
	{
		return invoiceRecoupedAmt;
	}

	/**
	 * @param invoiceRecoupedAmt the invoiceRecoupedAmt to set
	 */
	public void setInvoiceRecoupedAmt(String invoiceRecoupedAmt)
	{
		this.invoiceRecoupedAmt = invoiceRecoupedAmt;
	}

	/**
	 * @return the invoiceAdvanceId
	 */
	public String getInvoiceAdvanceId()
	{
		return invoiceAdvanceId;
	}

	/**
	 * @param invoiceAdvanceId the invoiceAdvanceId to set
	 */
	public void setInvoiceAdvanceId(String invoiceAdvanceId)
	{
		this.invoiceAdvanceId = invoiceAdvanceId;
	}

	@Override
	public String toString()
	{
		return "AdvanceSummaryBean [id=" + id + ", advanceDesc=" + advanceDesc + ", advRequestedDate="
				+ advRequestedDate + ", status=" + status + ", advAmount=" + advAmount + ", ytdRecoupmentAmount="
				+ ytdRecoupmentAmount + ", invoiceRecoupedAmt=" + invoiceRecoupedAmt + ", invoiceId=" + invoiceId
				+ ", invoiceAdvanceId=" + invoiceAdvanceId + ", ytdRecoupmentPrecent=" + ytdRecoupmentPrecent + "]";
	}

}
