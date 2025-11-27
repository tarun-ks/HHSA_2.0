package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * This class is a bean which maintains details of payment module.
 * 
 */
public class PaymentBean
{

	@RegExp(value ="^\\d{0,22}")
	String paymentId;
	@Length(max = 20)
	String agencyCode;
	@Length(max = 50)
	String paymentVoucherNo;
	String budgetFYId;
	String fiscalYearId;
	String period;
	String workflowId;
	String invoiceNo;
	String invoiceSubmittedDate;
	String invoiceApprovedDate;
	String invoiceStartDate;
	String invoiceEndDate;
	String vendorCode;
	String vendorAddrCode;
	String payeeName;
	@Length(max = 20)
	String payeeVendorCode;
	@Length(max = 20)
	String payeeVendorAddrCode;
	String checkNum;
	String disbursementDate;
	String paymentAmount;
	String invoiceId;
	String budgetAdvanceId;
	String budgetAdvanceNumber;
	String advanceDescription;
	String advanceRequestDate;
	String advanceApprovedDate;
	// attributes added as part of release 3.1.0 for enhancement 6023
	String rejectedDate;
	String paymentStatus;

	/**
	 * @return the paymentId
	 */
	public String getPaymentId()
	{
		return paymentId;
	}

	/**
	 * @param paymentId the paymentId to set
	 */
	public void setPaymentId(String paymentId)
	{
		this.paymentId = paymentId;
	}

	/**
	 * @return the agencyCode
	 */
	public String getAgencyCode()
	{
		return agencyCode;
	}

	/**
	 * @param agencyCode the agencyCode to set
	 */
	public void setAgencyCode(String agencyCode)
	{
		this.agencyCode = agencyCode;
	}

	/**
	 * @return the paymentVoucherNo
	 */
	public String getPaymentVoucherNo()
	{
		return paymentVoucherNo;
	}

	/**
	 * @param paymentVoucherNo the paymentVoucherNo to set
	 */
	public void setPaymentVoucherNo(String paymentVoucherNo)
	{
		this.paymentVoucherNo = paymentVoucherNo;
	}

	/**
	 * @return the budgetFYId
	 */
	public String getBudgetFYId()
	{
		return budgetFYId;
	}

	/**
	 * @param budgetFYId the budgetFYId to set
	 */
	public void setBudgetFYId(String budgetFYId)
	{
		this.budgetFYId = budgetFYId;
	}

	/**
	 * @return the fiscalYearId
	 */
	public String getFiscalYearId()
	{
		return fiscalYearId;
	}

	/**
	 * @param fiscalYearId the fiscalYearId to set
	 */
	public void setFiscalYearId(String fiscalYearId)
	{
		this.fiscalYearId = fiscalYearId;
	}

	/**
	 * @return the period
	 */
	public String getPeriod()
	{
		return period;
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(String period)
	{
		this.period = period;
	}

	/**
	 * @return the workflowId
	 */
	public String getWorkflowId()
	{
		return workflowId;
	}

	/**
	 * @param workflowId the workflowId to set
	 */
	public void setWorkflowId(String workflowId)
	{
		this.workflowId = workflowId;
	}

	/**
	 * @return the invoiceNo
	 */
	public String getInvoiceNo()
	{
		return invoiceNo;
	}

	/**
	 * @param invoiceNo the invoiceNo to set
	 */
	public void setInvoiceNo(String invoiceNo)
	{
		this.invoiceNo = invoiceNo;
	}

	/**
	 * @return the invoiceSubmittedDate
	 */
	public String getInvoiceSubmittedDate()
	{
		return invoiceSubmittedDate;
	}

	/**
	 * @param invoiceSubmittedDate the invoiceSubmittedDate to set
	 */
	public void setInvoiceSubmittedDate(String invoiceSubmittedDate)
	{
		this.invoiceSubmittedDate = invoiceSubmittedDate;
	}

	/**
	 * @return the invoiceApprovedDate
	 */
	public String getInvoiceApprovedDate()
	{
		return invoiceApprovedDate;
	}

	/**
	 * @param invoiceApprovedDate the invoiceApprovedDate to set
	 */
	public void setInvoiceApprovedDate(String invoiceApprovedDate)
	{
		this.invoiceApprovedDate = invoiceApprovedDate;
	}

	/**
	 * @return the invoiceStartDate
	 */
	public String getInvoiceStartDate()
	{
		return invoiceStartDate;
	}

	/**
	 * @param invoiceStartDate the invoiceStartDate to set
	 */
	public void setInvoiceStartDate(String invoiceStartDate)
	{
		this.invoiceStartDate = invoiceStartDate;
	}

	/**
	 * @return the invoiceEndDate
	 */
	public String getInvoiceEndDate()
	{
		return invoiceEndDate;
	}

	/**
	 * @param invoiceEndDate the invoiceEndDate to set
	 */
	public void setInvoiceEndDate(String invoiceEndDate)
	{
		this.invoiceEndDate = invoiceEndDate;
	}

	/**
	 * @return the vendorCode
	 */
	public String getVendorCode()
	{
		return vendorCode;
	}

	/**
	 * @param vendorCode the vendorCode to set
	 */
	public void setVendorCode(String vendorCode)
	{
		this.vendorCode = vendorCode;
	}

	/**
	 * @return the vendorAddrCode
	 */
	public String getVendorAddrCode()
	{
		return vendorAddrCode;
	}

	/**
	 * @param vendorAddrCode the vendorAddrCode to set
	 */
	public void setVendorAddrCode(String vendorAddrCode)
	{
		this.vendorAddrCode = vendorAddrCode;
	}

	/**
	 * @return the payeeName
	 */
	public String getPayeeName()
	{
		return payeeName;
	}

	/**
	 * @param payeeName the payeeName to set
	 */
	public void setPayeeName(String payeeName)
	{
		this.payeeName = payeeName;
	}

	/**
	 * @return the payeeVendorCode
	 */
	public String getPayeeVendorCode()
	{
		return payeeVendorCode;
	}

	/**
	 * @param payeeVendorCode the payeeVendorCode to set
	 */
	public void setPayeeVendorCode(String payeeVendorCode)
	{
		this.payeeVendorCode = payeeVendorCode;
	}

	/**
	 * @return the payeeVendorAddrCode
	 */
	public String getPayeeVendorAddrCode()
	{
		return payeeVendorAddrCode;
	}

	/**
	 * @param payeeVendorAddrCode the payeeVendorAddrCode to set
	 */
	public void setPayeeVendorAddrCode(String payeeVendorAddrCode)
	{
		this.payeeVendorAddrCode = payeeVendorAddrCode;
	}

	/**
	 * @return the checkNum
	 */
	public String getCheckNum()
	{
		return checkNum;
	}

	/**
	 * @param checkNum the checkNum to set
	 */
	public void setCheckNum(String checkNum)
	{
		this.checkNum = checkNum;
	}

	/**
	 * @return the disbursementDate
	 */
	public String getDisbursementDate()
	{
		return disbursementDate;
	}

	/**
	 * @param disbursementDate the disbursementDate to set
	 */
	public void setDisbursementDate(String disbursementDate)
	{
		this.disbursementDate = disbursementDate;
	}

	/**
	 * @return the paymentAmount
	 */
	public String getPaymentAmount()
	{
		return paymentAmount;
	}

	/**
	 * @param paymentAmount the paymentAmount to set
	 */
	public void setPaymentAmount(String paymentAmount)
	{
		this.paymentAmount = paymentAmount;
	}

	/**
	 * @return the invoiceId
	 */
	public String getInvoiceId()
	{
		return invoiceId;
	}

	/**
	 * @param invoiceId the invoiceId to set
	 */
	public void setInvoiceId(String invoiceId)
	{
		this.invoiceId = invoiceId;
	}

	/**
	 * @return the budgetAdvanceId
	 */
	public String getBudgetAdvanceId()
	{
		return budgetAdvanceId;
	}

	/**
	 * @param budgetAdvanceId the budgetAdvanceId to set
	 */
	public void setBudgetAdvanceId(String budgetAdvanceId)
	{
		this.budgetAdvanceId = budgetAdvanceId;
	}

	/**
	 * @return the budgetAdvanceNumber
	 */
	public String getBudgetAdvanceNumber()
	{
		return budgetAdvanceNumber;
	}

	/**
	 * @param budgetAdvanceNumber the budgetAdvanceNumber to set
	 */
	public void setBudgetAdvanceNumber(String budgetAdvanceNumber)
	{
		this.budgetAdvanceNumber = budgetAdvanceNumber;
	}

	/**
	 * @return the advanceDescription
	 */
	public String getAdvanceDescription()
	{
		return advanceDescription;
	}

	/**
	 * @param advanceDescription the advanceDescription to set
	 */
	public void setAdvanceDescription(String advanceDescription)
	{
		this.advanceDescription = advanceDescription;
	}

	/**
	 * @return the advanceRequestDate
	 */
	public String getAdvanceRequestDate()
	{
		return advanceRequestDate;
	}

	/**
	 * @param advanceRequestDate the advanceRequestDate to set
	 */
	public void setAdvanceRequestDate(String advanceRequestDate)
	{
		this.advanceRequestDate = advanceRequestDate;
	}

	/**
	 * @return the advanceApprovedDate
	 */
	public String getAdvanceApprovedDate()
	{
		return advanceApprovedDate;
	}

	/**
	 * @param advanceApprovedDate the advanceApprovedDate to set
	 */
	public void setAdvanceApprovedDate(String advanceApprovedDate)
	{
		this.advanceApprovedDate = advanceApprovedDate;
	}

	@Override
	public String toString()
	{
		return "PaymentBean [paymentId=" + paymentId + ", agencyCode=" + agencyCode + ", paymentVoucherNo="
				+ paymentVoucherNo + ", budgetFYId=" + budgetFYId + ", fiscalYearId=" + fiscalYearId + ", period="
				+ period + ", workflowId=" + workflowId + ", invoiceNo=" + invoiceNo + ", invoiceSubmittedDate="
				+ invoiceSubmittedDate + ", invoiceApprovedDate=" + invoiceApprovedDate + ", invoiceStartDate="
				+ invoiceStartDate + ", invoiceEndDate=" + invoiceEndDate + ", vendorCode=" + vendorCode
				+ ", vendorAddrCode=" + vendorAddrCode + ", payeeName=" + payeeName + ", payeeVendorCode="
				+ payeeVendorCode + ", payeeVendorAddrCode=" + payeeVendorAddrCode + ", checkNum=" + checkNum
				+ ", disbursementDate=" + disbursementDate + ", paymentAmount=" + paymentAmount + ", invoiceId="
				+ invoiceId + ", budgetAdvanceId=" + budgetAdvanceId + ", budgetAdvanceNumber=" + budgetAdvanceNumber
				+ ", advanceDescription=" + advanceDescription + ", advanceRequestDate=" + advanceRequestDate
				+ ", advanceApprovedDate=" + advanceApprovedDate + "]";
	}

	public String getRejectedDate() {
		return rejectedDate;
	}

	public void setRejectedDate(String rejectedDate) {
		this.rejectedDate = rejectedDate;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

}