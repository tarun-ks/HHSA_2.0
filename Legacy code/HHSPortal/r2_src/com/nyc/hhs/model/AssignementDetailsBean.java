package com.nyc.hhs.model;

import java.math.BigDecimal;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class AssignementDetailsBean
{
	@Length(max = 20)
	String msVendorId;
	BigDecimal mdAmount;

	public String getMsVendorId()
	{
		return msVendorId;
	}

	public void setMsVendorId(String msVendorId)
	{
		this.msVendorId = msVendorId;
	}

	/**
	 * @return the mdAmount
	 */
	public BigDecimal getMdAmount()
	{
		return mdAmount;
	}

	/**
	 * @param mdAmount the mdAmount to set
	 */
	public void setMdAmount(BigDecimal mdAmount)
	{
		this.mdAmount = mdAmount;
	}

	@Override
	public String toString()
	{
		return "AssignementDetailsBean [msVendorId=" + msVendorId + ", mdAmount=" + mdAmount + "]";
	}

}
