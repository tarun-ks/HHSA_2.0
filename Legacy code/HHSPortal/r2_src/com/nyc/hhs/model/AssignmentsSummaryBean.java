package com.nyc.hhs.model;

import java.util.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class AssignmentsSummaryBean extends CBGridBean
{
	@Length(max = 20)
	//ADVANCE_ASSIGNMENT_ID
	private String id;
	private String assignmentName;
	private String assignmentAmount;
	private String invoiceAmount;
	private String ytdAssignmentAmount;
	private String budgetId;
	private String organizationId;
	private String vendorTypeId;
	private Date createdDate;
	private String createdByUserId;
	private Date modifiedDate;
	private String modifiedByUserId;

	/**
	 * @return the assignmentName
	 */
	public String getAssignmentName()
	{
		return assignmentName;
	}

	/**
	 * @param assignmentName the assignmentName to set
	 */
	public void setAssignmentName(String assignmentName)
	{
		this.assignmentName = assignmentName;
	}

	/**
	 * @return the assignmentAmount
	 */
	public String getAssignmentAmount()
	{
		return assignmentAmount;
	}

	/**
	 * @param assignmentAmount the assignmentAmount to set
	 */
	public void setAssignmentAmount(String assignmentAmount)
	{
		this.assignmentAmount = assignmentAmount;
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
	 * @return the invoiceAmount
	 */
	public String getInvoiceAmount()
	{
		return invoiceAmount;
	}

	/**
	 * @param invoiceAmount the invoiceAmount to set
	 */
	public void setInvoiceAmount(String invoiceAmount)
	{
		this.invoiceAmount = invoiceAmount;
	}

	/**
	 * @return the budgetId
	 */
	public String getBudgetId()
	{
		return budgetId;
	}

	/**
	 * @param budgetId the budgetId to set
	 */
	public void setBudgetId(String budgetId)
	{
		this.budgetId = budgetId;
	}

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
	 * @return the vendorTypeId
	 */
	public String getVendorTypeId()
	{
		return vendorTypeId;
	}

	/**
	 * @param vendorTypeId the vendorTypeId to set
	 */
	public void setVendorTypeId(String vendorTypeId)
	{
		this.vendorTypeId = vendorTypeId;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate()
	{
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate)
	{
		this.createdDate = createdDate;
	}

	/**
	 * @return the createdByUserId
	 */
	public String getCreatedByUserId()
	{
		return createdByUserId;
	}

	/**
	 * @param createdByUserId the createdByUserId to set
	 */
	public void setCreatedByUserId(String createdByUserId)
	{
		this.createdByUserId = createdByUserId;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate()
	{
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate)
	{
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the modifiedByUserId
	 */
	public String getModifiedByUserId()
	{
		return modifiedByUserId;
	}

	/**
	 * @param modifiedByUserId the modifiedByUserId to set
	 */
	public void setModifiedByUserId(String modifiedByUserId)
	{
		this.modifiedByUserId = modifiedByUserId;
	}

	/**
	 * @return the ytdAssignmentAmount
	 */
	public String getYtdAssignmentAmount()
	{
		return ytdAssignmentAmount;
	}

	/**
	 * @param ytdAssignmentAmount the ytdAssignmentAmount to set
	 */
	public void setYtdAssignmentAmount(String ytdAssignmentAmount)
	{
		this.ytdAssignmentAmount = ytdAssignmentAmount;
	}

	@Override
	public String toString()
	{
		return "AssignmentsSummaryBean [id=" + id + ", assignmentName=" + assignmentName + ", assignmentAmount="
				+ assignmentAmount + ", invoiceAmount=" + invoiceAmount + ", ytdAssignmentAmount="
				+ ytdAssignmentAmount + ", budgetId=" + budgetId + ", organizationId=" + organizationId
				+ ", vendorTypeId=" + vendorTypeId + ", createdDate=" + createdDate + ", createdByUserId="
				+ createdByUserId + ", modifiedDate=" + modifiedDate + ", modifiedByUserId=" + modifiedByUserId + "]";
	}

}
