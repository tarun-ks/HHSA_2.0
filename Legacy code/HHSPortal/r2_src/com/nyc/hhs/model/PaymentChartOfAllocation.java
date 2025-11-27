package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

/**
 * This class is a bean which maintains PaymentChartOfAllocation details of
 * payment module.
 * 
 */

public class PaymentChartOfAllocation extends PaymentAllocationBean
{
	private String paymentAllocationId = HHSConstants.EMPTY_STRING;
	@RegExp(value ="^\\d{0,22}")
	private String paymentId = HHSConstants.EMPTY_STRING;
	private String commodityLine = HHSConstants.EMPTY_STRING;
	@Length(max = 20)
	private String commodityLineCode = HHSConstants.EMPTY_STRING;
	@Length(max = 20)
	private String actgLn;
	private String fmsEncumbrance = HHSConstants.STRING_ZERO;
	private String remainingEncumbrance = HHSConstants.STRING_ZERO;
	private String paymentAmount = HHSConstants.STRING_ZERO;
	private String reportingCategory = HHSConstants.EMPTY_STRING;
	private String paymentChartOfAccount = HHSConstants.STRING_ZERO;
    //added BFY in PaymentAllocationBean for enhancement 6515 release 3.6.0
	private String bfy = HHSConstants.EMPTY_STRING;
	//Added in R7-To Fix Defect #7211(fetching previous date hour(4PM))
	private String prevDateHour = HHSConstants.EMPTY_STRING;
	
   /**
    * 
    * @return the prevDateHour
    */
	public String getPrevDateHour() {
		return prevDateHour;
	}

	/**
	 * 
	 * @param prevDateHour
	 */
	public void setPrevDateHour(String prevDateHour) {
		this.prevDateHour = prevDateHour;
	}

	/**
	 * @return the paymentAllocationId
	 */
	public String getPaymentAllocationId()
	{
		return paymentAllocationId;
	}

	/**
	 * @param paymentAllocationId the paymentAllocationId to set
	 */
	public void setPaymentAllocationId(String paymentAllocationId)
	{
		this.paymentAllocationId = paymentAllocationId;
	}

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
	 * @return the commodityLine
	 */
	public String getCommodityLine()
	{
		return commodityLine;
	}

	/**
	 * @param commodityLine the commodityLine to set
	 */
	public void setCommodityLine(String commodityLine)
	{
		this.commodityLine = commodityLine;
	}

	/**
	 * @return the actgLn
	 */
	public String getActgLn()
	{
		return actgLn;
	}

	/**
	 * @param actgLn the actgLn to set
	 */
	public void setActgLn(String actgLn)
	{
		this.actgLn = actgLn;
	}

	/**
	 * @return the fmsEncumbrance
	 */
	public String getFmsEncumbrance()
	{
		return fmsEncumbrance;
	}

	/**
	 * @param fmsEncumbrance the fmsEncumbrance to set
	 */
	public void setFmsEncumbrance(String fmsEncumbrance)
	{
		this.fmsEncumbrance = fmsEncumbrance;
	}

	/**
	 * @return the remainingEncumbrance
	 */
	public String getRemainingEncumbrance()
	{
		return remainingEncumbrance;
	}

	/**
	 * @param remainingEncumbrance the remainingEncumbrance to set
	 */
	public void setRemainingEncumbrance(String remainingEncumbrance)
	{
		this.remainingEncumbrance = remainingEncumbrance;
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
	 * @return the reportingCategory
	 */
	public String getReportingCategory()
	{
		return reportingCategory;
	}

	/**
	 * @param reportingCategory the reportingCategory to set
	 */
	public void setReportingCategory(String reportingCategory)
	{
		this.reportingCategory = reportingCategory;
	}

	/**
	 * @return the paymentChartOfAccount
	 */
	public String getPaymentChartOfAccount()
	{
		return paymentChartOfAccount;
	}

	/**
	 * @param paymentChartOfAccount the paymentChartOfAccount to set
	 */
	public void setPaymentChartOfAccount(String paymentChartOfAccount)
	{
		this.paymentChartOfAccount = paymentChartOfAccount;
	}

	/**
	 * @return the commodityLineCode
	 */
	public String getCommodityLineCode()
	{
		return commodityLineCode;
	}

	/**
	 * @param commodityLineCode the commodityLineCode to set
	 */
	public void setCommodityLineCode(String commodityLineCode)
	{
		this.commodityLineCode = commodityLineCode;
	}

	 //added BFY in PaymentAllocationBean for enhancement 6515 release 3.6.0
	@Override
	public String toString()
	{
		return "PaymentChartOfAllocation [paymentAllocationId=" + paymentAllocationId + ", paymentId=" + paymentId
				+ ", commodityLine=" + commodityLine + ", commodityLineCode=" + commodityLineCode + ", actgLn="
				+ actgLn + ", fmsEncumbrance=" + fmsEncumbrance + ", remainingEncumbrance=" + remainingEncumbrance
				+ ", paymentAmount=" + paymentAmount + ", reportingCategory=" + reportingCategory
				+ ", paymentChartOfAccount=" + paymentChartOfAccount + ", bfy=" + bfy +"]";
	}

	public String getBfy() {
		return bfy;
	}

	public void setBfy(String bfy) {
		this.bfy = bfy;
	}

}
