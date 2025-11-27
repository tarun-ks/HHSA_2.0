package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 *This class is a bean that store details for evaluation
 */
public class EvaluationDetailBean
{
	@RegExp(value ="^\\d{0,22}")
	private String procurementId;
	@RegExp(value ="^\\d{0,22}")
	private String proposalId;
	private String evaluatorId;
	@Length(max = 20)
	private String organizationId;
	private String statusId;
	@RegExp(value ="^\\d{0,22}")
	private String evaluationStatusId;
	private String procStatusId;
	private String procUser;
	private String workflowId;
	private String createdDate;
	private String createdByUserId;
	private String modifiedDate;
	private String modifiedByUserId;
	@RegExp(value ="^\\d{0,22}")
	private String evaSettingsIntId;
	@RegExp(value ="^\\d{0,22}")
	private String evaSettingsExtId;
	@RegExp(value ="^\\d{0,22}")
	private String evaluationPoolMappingId;
	
	/*Starts R5 : Enhanced Evaluation*/
	private String evaluatorName;
	private String proposalTitle;
	private String providerName;
	private String evaluationCompletedStatus;

	/**
	 * @return the evaluatorName
	 */
	public String getEvaluatorName()
	{
		return evaluatorName;
	}

	/**
	 * @param evaluatorName the evaluatorName to set
	 */
	public void setEvaluatorName(String evaluatorName)
	{
		this.evaluatorName = evaluatorName;
	}

	/**
	 * @return the proposalTitle
	 */
	public String getProposalTitle()
	{
		return proposalTitle;
	}

	/**
	 * @param proposalTitle the proposalTitle to set
	 */
	public void setProposalTitle(String proposalTitle)
	{
		this.proposalTitle = proposalTitle;
	}

	/**
	 * @return the providerName
	 */
	public String getProviderName()
	{
		return providerName;
	}

	/**
	 * @param providerName the providerName to set
	 */
	public void setProviderName(String providerName)
	{
		this.providerName = providerName;
	}

	/**
	 * @return the evaluationCompletedStatus
	 */
	public String getEvaluationCompletedStatus()
	{
		return evaluationCompletedStatus;
	}
	
	/*Ends R5 : Enhanced Evaluation*/
	
	/**
	 * @param evaluationCompletedStatus the evaluationCompletedStatus to set
	 */
	public void setEvaluationCompletedStatus(String evaluationCompletedStatus)
	{
		this.evaluationCompletedStatus = evaluationCompletedStatus;
	}

	public String getEvaSettingsIntId()
	{
		return evaSettingsIntId;
	}

	public void setEvaSettingsIntId(String evaSettingsIntId)
	{
		this.evaSettingsIntId = evaSettingsIntId;
	}

	public String getEvaSettingsExtId()
	{
		return evaSettingsExtId;
	}

	public void setEvaSettingsExtId(String evaSettingsExtId)
	{
		this.evaSettingsExtId = evaSettingsExtId;
	}

	/**
	 * @return the procurementId
	 */
	public String getProcurementId()
	{
		return procurementId;
	}

	/**
	 * @param procurementId the procurementId to set
	 */
	public void setProcurementId(String procurementId)
	{
		this.procurementId = procurementId;
	}

	/**
	 * @return the proposalId
	 */
	public String getProposalId()
	{
		return proposalId;
	}

	/**
	 * @param proposalId the proposalId to set
	 */
	public void setProposalId(String proposalId)
	{
		this.proposalId = proposalId;
	}

	/**
	 * @return the evaluatorId
	 */
	public String getEvaluatorId()
	{
		return evaluatorId;
	}

	/**
	 * @param evaluatorId the evaluatorId to set
	 */
	public void setEvaluatorId(String evaluatorId)
	{
		this.evaluatorId = evaluatorId;
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
	 * @return the statusId
	 */
	public String getStatusId()
	{
		return statusId;
	}

	/**
	 * @param statusId the statusId to set
	 */
	public void setStatusId(String statusId)
	{
		this.statusId = statusId;
	}

	/**
	 * @return the evaluationStatusId
	 */
	public String getEvaluationStatusId()
	{
		return evaluationStatusId;
	}

	/**
	 * @param evaluationStatusId the evaluationStatusId to set
	 */
	public void setEvaluationStatusId(String evaluationStatusId)
	{
		this.evaluationStatusId = evaluationStatusId;
	}

	/**
	 * @return the procStatusId
	 */
	public String getProcStatusId()
	{
		return procStatusId;
	}

	/**
	 * @param procStatusId the procStatusId to set
	 */
	public void setProcStatusId(String procStatusId)
	{
		this.procStatusId = procStatusId;
	}

	/**
	 * @return the procUser
	 */
	public String getProcUser()
	{
		return procUser;
	}

	/**
	 * @param procUser the procUser to set
	 */
	public void setProcUser(String procUser)
	{
		this.procUser = procUser;
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
	 * @return the createdDate
	 */
	public String getCreatedDate()
	{
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate)
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
	public String getModifiedDate()
	{
		return modifiedDate;
	}

	/**
	 * @param workflowId the workflowId to set
	 */
	public void setModifiedDate(String modifiedDate)
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
	 * @return the evaluationPoolMappingId
	 * 
	 *  <li>This method was added in R4</li>
	 */
	public String getEvaluationPoolMappingId()
	{
		return evaluationPoolMappingId;
	}

	/**
	 *  <li>This method was added in R4</li>
	 * @param evaluationPoolMappingId the evaluationPoolMappingId to set
	 */
	public void setEvaluationPoolMappingId(String evaluationPoolMappingId)
	{
		this.evaluationPoolMappingId = evaluationPoolMappingId;
	}

	//Start R5 : Updated toString 
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "EvaluationDetailBean [procurementId=" + procurementId + ", proposalId=" + proposalId + ", evaluatorId="
				+ evaluatorId + ", organizationId=" + organizationId + ", statusId=" + statusId
				+ ", evaluationStatusId=" + evaluationStatusId + ", procStatusId=" + procStatusId + ", procUser="
				+ procUser + ", workflowId=" + workflowId + ", createdDate=" + createdDate + ", createdByUserId="
				+ createdByUserId + ", modifiedDate=" + modifiedDate + ", modifiedByUserId=" + modifiedByUserId
				+ ", evaSettingsIntId=" + evaSettingsIntId + ", evaSettingsExtId=" + evaSettingsExtId
				+ ", evaluationPoolMappingId=" + evaluationPoolMappingId + ", evaluatorName=" + evaluatorName
				+ ", proposalTitle=" + proposalTitle + ", providerName=" + providerName
				+ ", evaluationCompletedStatus=" + evaluationCompletedStatus + "]";
	}
	//End R5 : Updated toString
}