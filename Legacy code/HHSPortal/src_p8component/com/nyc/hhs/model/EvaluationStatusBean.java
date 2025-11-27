package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * Data model for the evaluation status record in the database
 */
public class EvaluationStatusBean
{

	// Initialize the required variables
	@RegExp(value ="^\\d{0,22}")
	private String procurementId;
	@RegExp(value ="^\\d{0,22}")
	private String proposalId;
	@Length(max = 20)
	private String organizationId;
	private String evalSettingsIntId;
	private String evalSettingsExtId;
	private String statusId;
	@RegExp(value ="^\\d{0,22}")
	//PROC_STATUS_ID
	private String procStatusId;
	private String createdDate;
	private String createdByUserId;
	private String modifiedDate;
	private String modifiedByUserId;
	private String evaluationPoolMappingId;

	/*
	 * For each variable a getter and a setter method is created.
	 */
	public String getProcurementId()
	{
		return procurementId;
	}

	public void setProcurementId(String procurementId)
	{
		this.procurementId = procurementId;
	}

	public String getProposalId()
	{
		return proposalId;
	}

	public void setProposalId(String proposalId)
	{
		this.proposalId = proposalId;
	}

	public String getOrganizationId()
	{
		return organizationId;
	}

	public void setOrganizationId(String organizationId)
	{
		this.organizationId = organizationId;
	}

	public String getEvalSettingsIntId()
	{
		return evalSettingsIntId;
	}

	public void setEvalSettingsIntId(String evalSettingsIntId)
	{
		this.evalSettingsIntId = evalSettingsIntId;
	}

	public String getEvalSettingsExt()
	{
		return evalSettingsExtId;
	}

	public void setEvalSettingsExt(String evalSettingsExt)
	{
		this.evalSettingsExtId = evalSettingsExt;
	}

	public String getStatusId()
	{
		return statusId;
	}

	public void setStatusId(String statusId)
	{
		this.statusId = statusId;
	}

	public String getProcStatusId()
	{
		return procStatusId;
	}

	public void setProcStatusId(String procStatusId)
	{
		this.procStatusId = procStatusId;
	}

	public String getCreatedDate()
	{
		return createdDate;
	}

	public void setCreatedDate(String createdDate)
	{
		this.createdDate = createdDate;
	}

	public String getCreatedByUserId()
	{
		return createdByUserId;
	}

	public void setCreatedByUserId(String createdByUserId)
	{
		this.createdByUserId = createdByUserId;
	}

	public String getModifiedDate()
	{
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate)
	{
		this.modifiedDate = modifiedDate;
	}

	public String getModifiedByUserId()
	{
		return modifiedByUserId;
	}

	public void setModifiedByUserId(String modifiedByUserId)
	{
		this.modifiedByUserId = modifiedByUserId;
	}

	/**
	 * <li>The transaction used: insertNewBudgetDetails</li>
	 * 	 * @return the evaluationPoolMappingId
	 */
	public String getEvaluationPoolMappingId()
	{
		return evaluationPoolMappingId;
	}

	/**
	 * <li>The transaction used: insertNewBudgetDetails</li>
	 * @param evaluationPoolMappingId the evaluationPoolMappingId to set
	 */
	public void setEvaluationPoolMappingId(String evaluationPoolMappingId)
	{
		this.evaluationPoolMappingId = evaluationPoolMappingId;
	}

}
