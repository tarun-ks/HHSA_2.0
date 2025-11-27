package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class ContractedServicesBean extends CBGridBean
{

	private String subHeader;
	@Length(max = 50)
	private String descOfService;
	private String fyBudget;
	private String ytdInvoiceAmt;
	private String remaingAmt;
	private String ytdTotalInvoiceAmt;
	private String totalContractedServices;
	private String csName;
	private String id;
	private String invoiceAmt;
	private String modificationAmt;
	private String proposedAmt;
	@RegExp(value ="^\\d{0,22}")
	private String parentId;
	private String amendmentAmt;

	/**
	 * @return the amendmentAmt
	 */
	public String getAmendmentAmt()
	{
		return amendmentAmt;
	}

	/**
	 * @param amendmentAmt the amendmentAmt to set
	 */
	public void setAmendmentAmt(String amendmentAmt)
	{
		this.amendmentAmt = amendmentAmt;
	}

	/**
	 * @return the subHeader
	 */
	public String getSubHeader()
	{
		return subHeader;
	}

	/**
	 * @param subHeader the subHeader to set
	 */
	public void setSubHeader(String subHeader)
	{
		this.subHeader = subHeader;
	}

	/**
	 * @return the descOfService
	 */
	public String getDescOfService()
	{
		return descOfService;
	}

	/**
	 * @param descOfService the descOfService to set
	 */
	public void setDescOfService(String descOfService)
	{
		this.descOfService = descOfService;
	}

	/**
	 * @return the fyBudget
	 */
	public String getFyBudget()
	{
		return fyBudget;
	}

	/**
	 * @param fyBudget the fyBudget to set
	 */
	public void setFyBudget(String fyBudget)
	{
		this.fyBudget = fyBudget;
	}

	/**
	 * @return the ytdInvoiceAmt
	 */
	public String getYtdInvoiceAmt()
	{
		return ytdInvoiceAmt;
	}

	/**
	 * @param ytdInvoiceAmt the ytdInvoiceAmt to set
	 */
	public void setYtdInvoiceAmt(String ytdInvoiceAmt)
	{
		this.ytdInvoiceAmt = ytdInvoiceAmt;
	}

	/**
	 * @return the remaingAmt
	 */
	public String getRemaingAmt()
	{
		return remaingAmt;
	}

	/**
	 * @param remaingAmt the remaingAmt to set
	 */
	public void setRemaingAmt(String remaingAmt)
	{
		this.remaingAmt = remaingAmt;
	}

	/**
	 * @return the ytdTotalInvoiceAmt
	 */
	public String getYtdTotalInvoiceAmt()
	{
		return ytdTotalInvoiceAmt;
	}

	/**
	 * @param ytdTotalInvoiceAmt the ytdTotalInvoiceAmt to set
	 */
	public void setYtdTotalInvoiceAmt(String ytdTotalInvoiceAmt)
	{
		this.ytdTotalInvoiceAmt = ytdTotalInvoiceAmt;
	}

	/**
	 * @return the totalContractedServices
	 */
	public String getTotalContractedServices()
	{
		return totalContractedServices;
	}

	/**
	 * @param totalContractedServices the totalContractedServices to set
	 */
	public void setTotalContractedServices(String totalContractedServices)
	{
		this.totalContractedServices = totalContractedServices;
	}

	/**
	 * @return the csName
	 */
	public String getCsName()
	{
		return csName;
	}

	/**
	 * @param csName the csName to set
	 */
	public void setCsName(String csName)
	{
		this.csName = csName;
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
	 * @return the invoiceAmt
	 */
	public String getInvoiceAmt()
	{
		return invoiceAmt;
	}

	/**
	 * @param invoiceAmt the invoiceAmt to set
	 */
	public void setInvoiceAmt(String invoiceAmt)
	{
		this.invoiceAmt = invoiceAmt;
	}

	/**
	 * @return the modificationAmt
	 */
	public String getModificationAmt()
	{
		return modificationAmt;
	}

	/**
	 * @param modificationAmt the modificationAmt to set
	 */
	public void setModificationAmt(String modificationAmt)
	{
		this.modificationAmt = modificationAmt;
	}

	/**
	 * @return the proposedAmt
	 */
	public String getProposedAmt()
	{
		return proposedAmt;
	}

	/**
	 * @param proposedAmt the proposedAmt to set
	 */
	public void setProposedAmt(String proposedAmt)
	{
		this.proposedAmt = proposedAmt;
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
		return "ContractedServicesBean [subHeader=" + subHeader + ", descOfService=" + descOfService + ", fyBudget="
				+ fyBudget + ", ytdInvoiceAmt=" + ytdInvoiceAmt + ", remaingAmt=" + remaingAmt
				+ ", ytdTotalInvoiceAmt=" + ytdTotalInvoiceAmt + ", totalContractedServices=" + totalContractedServices
				+ ", csName=" + csName + ", id=" + id + ", invoiceAmt=" + invoiceAmt + ", modificationAmt="
				+ modificationAmt + ", proposedAmt=" + proposedAmt + ", parentId=" + parentId + ", amendmentAmt="
				+ amendmentAmt + "]";
	}

}