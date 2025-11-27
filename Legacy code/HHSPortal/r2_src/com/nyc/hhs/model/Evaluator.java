package com.nyc.hhs.model;

import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/**
 * This is a bean class for evaluator
 */
public class Evaluator
{
	private String extEvaluatorName;
	private String agencyUserId;
	@RegExp(value ="^\\d{0,22}")
	private String procurementId;
	private String agencyId;
	private String evaluatorCount;
	private String createdByUserId;
	private String modifiedByUserId;
	private String internalEvaluatorName;
	private String name;
	private String userId;
	private String evalType;
	private List<String> evalStatusIdList;
	private String evalSettingId;
	@RegExp(value ="^\\d{0,22}")
	private String proposalId;
	private String organizationId;
	private String statusId;
	private String status;
	private String evaluationGroupId;
	private String evaluationPoolMappingId;

	// R5 code starts
	private List<DocumentVisibility> documentVisibilityList;

	/**
	 * @return the documentVisibilityList
	 */
	public List<DocumentVisibility> getDocumentVisibilityList()
	{
		return documentVisibilityList;
	}

	/**
	 * @param documentVisibilityList the documentVisibilityList to set
	 */
	public void setDocumentVisibilityList(List<DocumentVisibility> documentVisibilityList)
	{
		this.documentVisibilityList = documentVisibilityList;
	}

	// R5 code ends
	/**
	 * @return the status
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status)
	{
		this.status = status;
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
	 * @return the evalSettingId
	 */
	public String getEvalSettingId()
	{
		return evalSettingId;
	}

	/**
	 * @param evalSettingId the evalSettingId to set
	 */
	public void setEvalSettingId(String evalSettingId)
	{
		this.evalSettingId = evalSettingId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	public Evaluator()
	{
		// No Action Required
	}

	public Evaluator(String asProcurementId, String asAgencyId, String asInternalEvaluatorsId, String asEvaluatorCount,
			String asUserId, String asEvaluationGroupId, String asEvaluationPoolMappingId)
	{
		this.procurementId = asProcurementId;
		this.agencyId = asAgencyId;
		this.internalEvaluatorName = asInternalEvaluatorsId;
		this.evaluatorCount = asEvaluatorCount;
		this.createdByUserId = asUserId;
		this.modifiedByUserId = asUserId;
		this.evaluationGroupId = asEvaluationGroupId;
		this.evaluationPoolMappingId = asEvaluationPoolMappingId;
	}

	public Evaluator(String asProcurementId, String asAgencyId, String asExtEvaluatorName, String asUserId,
			String asAgencyUserId, String emptyString, String asEvaluationGroupId, String asEvaluationPoolMappingId)
	{
		this.procurementId = asProcurementId;
		this.agencyId = asAgencyId;
		this.extEvaluatorName = asExtEvaluatorName;
		this.createdByUserId = asUserId;
		this.modifiedByUserId = asUserId;
		this.agencyUserId = asAgencyUserId;
		this.evaluationGroupId = asEvaluationGroupId;
		this.evaluationPoolMappingId = asEvaluationPoolMappingId;
	}

	/**
	 * @return the internalEvaluatorName
	 */
	public String getInternalEvaluatorName()
	{
		return internalEvaluatorName;
	}

	/**
	 * @param internalEvaluatorName the internalEvaluatorName to set
	 */
	public void setInternalEvaluatorName(String internalEvaluatorName)
	{
		this.internalEvaluatorName = internalEvaluatorName;
	}

	/**
	 * @return the extEvaluatorName
	 */
	public String getExtEvaluatorName()
	{
		return extEvaluatorName;
	}

	/**
	 * @param extEvaluatorName the extEvaluatorName to set
	 */
	public void setExtEvaluatorName(String extEvaluatorName)
	{
		this.extEvaluatorName = extEvaluatorName;
	}

	/**
	 * @return the agencyUserId
	 */
	public String getAgencyUserId()
	{
		return agencyUserId;
	}

	/**
	 * @param agencyUserId the agencyUserId to set
	 */
	public void setAgencyUserId(String agencyUserId)
	{
		this.agencyUserId = agencyUserId;
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
	 * @return the agencyId
	 */
	public String getAgencyId()
	{
		return agencyId;
	}

	/**
	 * @param agencyId the agencyId to set
	 */
	public void setAgencyId(String agencyId)
	{
		this.agencyId = agencyId;
	}

	/**
	 * @return the evaluatorCount
	 */
	public String getEvaluatorCount()
	{
		return evaluatorCount;
	}

	/**
	 * @param evaluatorCount the evaluatorCount to set
	 */
	public void setEvaluatorCount(String evaluatorCount)
	{
		this.evaluatorCount = evaluatorCount;
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
	 * @return the evalType
	 */
	public String getEvalType()
	{
		return evalType;
	}

	/**
	 * @param evalType the evalType to set
	 */
	public void setEvalType(String evalType)
	{
		this.evalType = evalType;
	}

	/**
	 * @return the evalStatusIdList
	 */
	public List<String> getEvalStatusIdList()
	{
		return evalStatusIdList;
	}

	/**
	 * @param evalStatusIdList the evalStatusIdList to set
	 */
	public void setEvalStatusIdList(List<String> evalStatusIdList)
	{
		this.evalStatusIdList = evalStatusIdList;
	}

	/**
	 * @return the evaluationGroupId
	 * 
	 *         <li>This method was added in R4</li>
	 */
	public String getEvaluationGroupId()
	{
		return evaluationGroupId;
	}

	/**
	 * @param evaluationGroupId the evaluationGroupId to set
	 * 
	 *            <li>This method was added in R4</li>
	 */
	public void setEvaluationGroupId(String evaluationGroupId)
	{
		this.evaluationGroupId = evaluationGroupId;
	}

	/**
	 * <li>This method was added in R4</li>
	 * @return the evaluationPoolMappingId
	 */
	public String getEvaluationPoolMappingId()
	{
		return evaluationPoolMappingId;
	}

	/**
	 * <li>This method was added in R4</li>
	 * @param evaluationPoolMappingId the evaluationPoolMappingId to set
	 */
	public void setEvaluationPoolMappingId(String evaluationPoolMappingId)
	{
		this.evaluationPoolMappingId = evaluationPoolMappingId;
	}

	/*
	 * (non-Javadoc) <li>This method was added in R4</li>
	 * 
	 * @see java.lang.Object#toString() updated in R5
	 */
	@Override
	public String toString()
	{
		return "Evaluator [extEvaluatorName=" + extEvaluatorName + ", agencyUserId=" + agencyUserId
				+ ", procurementId=" + procurementId + ", agencyId=" + agencyId + ", evaluatorCount=" + evaluatorCount
				+ ", createdByUserId=" + createdByUserId + ", modifiedByUserId=" + modifiedByUserId
				+ ", internalEvaluatorName=" + internalEvaluatorName + ", name=" + name + ", userId=" + userId
				+ ", evalType=" + evalType + ", evalStatusIdList=" + evalStatusIdList + ", evalSettingId="
				+ evalSettingId + ", proposalId=" + proposalId + ", organizationId=" + organizationId + ", statusId="
				+ statusId + ", status=" + status + ", evaluationGroupId=" + evaluationGroupId
				+ ", evaluationPoolMappingId=" + evaluationPoolMappingId + ", documentVisibilityList="
				+ documentVisibilityList + "]";
	}
}
