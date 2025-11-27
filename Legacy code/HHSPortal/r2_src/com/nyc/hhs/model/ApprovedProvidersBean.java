package com.nyc.hhs.model;

import java.sql.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class ApprovedProvidersBean
{
	@Length(max = 20)
	private String organizationId;
	private String organizationLegalName;
	private Date baExpDate;
	private Date filingExpDate;

	/**
	 * @return the organizationId
	 */
	public String getOrganizationId()
	{
		return organizationId;
	}

	/**
	 * @param organizationId the organizationId to set
	 */
	public void setOrganizationId(String organizationId)
	{
		this.organizationId = organizationId;
	}

	/**
	 * @return the organizationLegalName
	 */
	public String getOrganizationLegalName()
	{
		return organizationLegalName;
	}

	/**
	 * @param organizationLegalName the organizationLegalName to set
	 */
	public void setOrganizationLegalName(String organizationLegalName)
	{
		this.organizationLegalName = organizationLegalName;
	}

	/**
	 * @return the baExpDate
	 */
	public Date getBaExpDate()
	{
		return baExpDate;
	}

	/**
	 * @param baExpDate the baExpDate to set
	 */
	public void setBaExpDate(Date baExpDate)
	{
		this.baExpDate = baExpDate;
	}

	/**
	 * @return the filingExpDate
	 */
	public Date getFilingExpDate()
	{
		return filingExpDate;
	}

	/**
	 * @param filingExpDate the filingExpDate to set
	 */
	public void setFilingExpDate(Date filingExpDate)
	{
		this.filingExpDate = filingExpDate;
	}

	@Override
	public String toString()
	{
		return "ApprovedProvidersBean [organizationId=" + organizationId + ", organizationLegalName="
				+ organizationLegalName + ", baExpDate=" + baExpDate + ", filingExpDate=" + filingExpDate + "]";
	}
}
