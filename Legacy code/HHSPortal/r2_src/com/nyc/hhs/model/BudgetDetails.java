package com.nyc.hhs.model;

import java.math.BigDecimal;
import java.util.Date;

public class BudgetDetails
{

	private String title;

	private BigDecimal approvedBudget = BigDecimal.ZERO;

	private BigDecimal remainingAmount = BigDecimal.ZERO;

	private BigDecimal proposedBudget = BigDecimal.ZERO;

	private BigDecimal invoicedAmount = BigDecimal.ZERO;

	private BigDecimal ytdInvoicedAmount = BigDecimal.ZERO;

	private BigDecimal ytdActualPaid = BigDecimal.ZERO;

	private BigDecimal modificationAmount = BigDecimal.ZERO;

	private BigDecimal updateAmount = BigDecimal.ZERO;

	private BigDecimal paymentAmount = BigDecimal.ZERO;

	private BigDecimal advanceAmount = BigDecimal.ZERO;

	private Date startDate;

	private Date endDate;

	private BigDecimal cashBalance = BigDecimal.ZERO;

	private BigDecimal amendmentAmount = BigDecimal.ZERO;

	//Added in R6 for Return Payment While updating Notification details
	private String unRecoupedAmount; 
	// R6 End
	public BigDecimal getApprovedBudget()
	{
		return approvedBudget;
	}

	public void setApprovedBudget(BigDecimal approvedBudget)
	{
		this.approvedBudget = approvedBudget;
	}

	public BigDecimal getRemainingAmount()
	{
		return remainingAmount;
	}

	public void setRemainingAmount(BigDecimal remainingAmount)
	{
		this.remainingAmount = remainingAmount;
	}

	public BigDecimal getProposedBudget()
	{
		return proposedBudget;
	}

	public void setProposedBudget(BigDecimal proposedBudget)
	{
		this.proposedBudget = proposedBudget;
	}

	public BigDecimal getInvoicedAmount()
	{
		return invoicedAmount;
	}

	public void setInvoicedAmount(BigDecimal invoicedAmount)
	{
		this.invoicedAmount = invoicedAmount;
	}

	public BigDecimal getYtdInvoicedAmount()
	{
		return ytdInvoicedAmount;
	}

	public void setYtdInvoicedAmount(BigDecimal ytdInvoicedAmount)
	{
		this.ytdInvoicedAmount = ytdInvoicedAmount;
	}

	public BigDecimal getYtdActualPaid()
	{
		return ytdActualPaid;
	}

	public void setYtdActualPaid(BigDecimal ytdActualPaid)
	{
		this.ytdActualPaid = ytdActualPaid;
	}

	public BigDecimal getModificationAmount()
	{
		return modificationAmount;
	}

	public void setModificationAmount(BigDecimal modificationAmount)
	{
		this.modificationAmount = modificationAmount;
	}

	public BigDecimal getUpdateAmount()
	{
		return updateAmount;
	}

	public void setUpdateAmount(BigDecimal updateAmount)
	{
		this.updateAmount = updateAmount;
	}

	public BigDecimal getPaymentAmount()
	{
		return paymentAmount;
	}

	public void setPaymentAmount(BigDecimal paymentAmount)
	{
		this.paymentAmount = paymentAmount;
	}

	public BigDecimal getAdvanceAmount()
	{
		return advanceAmount;
	}

	public void setAdvanceAmount(BigDecimal advanceAmount)
	{
		this.advanceAmount = advanceAmount;
	}

	public BigDecimal getCashBalance()
	{
		return cashBalance;
	}

	public void setCashBalance(BigDecimal cashBalance)
	{
		this.cashBalance = cashBalance;
	}

	public BigDecimal getAmendmentAmount()
	{
		return amendmentAmount;
	}

	public void setAmendmentAmount(BigDecimal amendmentAmount)
	{
		this.amendmentAmount = amendmentAmount;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate()
	{
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate()
	{
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate)
	{
		this.endDate = endDate;
	}
	
	//Added in R6
	/**
	 * @return the unRecoupedAmount
	 */
	public String getUnRecoupedAmount() {
		return unRecoupedAmount;
	}

	/**
	 * @param unRecoupedAmount the unRecoupedAmount to set
	 */
	public void setUnRecoupedAmount(String unRecoupedAmount) {
		this.unRecoupedAmount = unRecoupedAmount;
	}
//R6 End
	@Override
	public String toString()
	{
		return "BudgetDetails [title=" + title + ", approvedBudget=" + approvedBudget + ", remainingAmount="
				+ remainingAmount + ", proposedBudget=" + proposedBudget + ", invoicedAmount=" + invoicedAmount
				+ ", YTDInvoicedAmount=" + ytdInvoicedAmount + ", ytdActualPaid=" + ytdActualPaid
				+ ", modificationAmount=" + modificationAmount + ", updateAmount=" + updateAmount + ", paymentAmount="
				+ paymentAmount + ", advanceAmount=" + advanceAmount + ", startDate=" + startDate + ", endDate="
				+ endDate + ", cashBalance=" + cashBalance + ", amendmentAmount=" + amendmentAmount + "]";
	}
	//added in R7
		private String budgetModification;
		public String getBudgetModification() {
			return budgetModification;
		}

		public void setBudgetModification(String budgetModification) {
			this.budgetModification = budgetModification;
		}
		//R7 End
}
