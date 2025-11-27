package com.nyc.hhs.model;

import java.sql.Date;
import java.util.List;

public class InvoiceSort extends BaseFilter

{
	private String orgType;

	private String invoiceNumber;
	private String invoiceDateSubmitted;
	private String invoiceDateApproved;
	private String invoiceValue;
	private String invoiceStatus;
	private String invoiceAction;

	private Date invoiceLastUpdateDate;

	private String invoiceProvider;

	private String orgId;
	private List<String> invoiceIdList;
	private List<String> invoiceStatusList;

	public List<String> getInvoiceStatusList()
	{
		return invoiceStatusList;
	}

	public void setInvoiceStatusList(List<String> invoiceStatusList)
	{
		this.invoiceStatusList = invoiceStatusList;
	}

	public List<String> getInvoiceIdList()
	{
		return invoiceIdList;
	}

	public void setInvoiceIdList(List<String> invoiceIdList)
	{
		this.invoiceIdList = invoiceIdList;
	}

	public String getOrgType()
	{
		return orgType;
	}

	public void setOrgType(String orgType)
	{
		this.orgType = orgType;
	}

	public String getInvoiceNumber()
	{
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber)
	{
		this.invoiceNumber = invoiceNumber;
	}

	public String getInvoiceDateSubmitted()
	{
		return invoiceDateSubmitted;
	}

	public void setInvoiceDateSubmitted(String invoiceDateSubmitted)
	{
		this.invoiceDateSubmitted = invoiceDateSubmitted;
	}

	public String getInvoiceDateApproved()
	{
		return invoiceDateApproved;
	}

	public void setInvoiceDateApproved(String invoiceDateApproved)
	{
		this.invoiceDateApproved = invoiceDateApproved;
	}

	public String getInvoiceValue()
	{
		return invoiceValue;
	}

	public void setInvoiceValue(String invoiceValue)
	{
		this.invoiceValue = invoiceValue;
	}

	public String getInvoiceStatus()
	{
		return invoiceStatus;
	}

	public void setInvoiceStatus(String invoiceStatus)
	{
		this.invoiceStatus = invoiceStatus;
	}

	public Date getInvoiceLastUpdateDate()
	{
		return invoiceLastUpdateDate;
	}

	public void setInvoiceLastUpdateDate(Date invoiceLastUpdateDate)
	{
		this.invoiceLastUpdateDate = invoiceLastUpdateDate;
	}

	public String getInvoiceProvider()
	{
		return invoiceProvider;
	}

	public void setInvoiceProvider(String invoiceProvider)
	{
		this.invoiceProvider = invoiceProvider;
	}

	public String getOrgId()
	{
		return orgId;
	}

	public void setOrgId(String orgId)
	{
		this.orgId = orgId;
	}

	public void setInvoiceAction(String invoiceAction)
	{
		this.invoiceAction = invoiceAction;
	}

	public String getInvoiceAction()
	{
		return invoiceAction;
	}

	@Override
	public String toString()
	{
		return "InvoiceSort [orgType=" + orgType + ", invoiceNumber=" + invoiceNumber + ", invoiceDateSubmitted="
				+ invoiceDateSubmitted + ", invoiceDateApproved=" + invoiceDateApproved + ", invoiceValue="
				+ invoiceValue + ", invoiceStatus=" + invoiceStatus + ", invoiceAction=" + invoiceAction
				+ ", invoiceLastUpdateDate=" + invoiceLastUpdateDate + ", invoiceProvider=" + invoiceProvider
				+ ", orgId=" + orgId + ", invoiceIdList=" + invoiceIdList + ", invoiceStatusList=" + invoiceStatusList
				+ "]";
	}

}