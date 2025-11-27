package com.nyc.hhs.model;

import com.nyc.hhs.constants.HHSConstants;

public class TaskLookupBean
{

	private String agencyId = HHSConstants.EMPTY_STRING;
	private String workflowId = HHSConstants.EMPTY_STRING;
	private int contractId;
	private String taskType = HHSConstants.EMPTY_STRING;
	private String entityType = HHSConstants.EMPTY_STRING;
	private String entityId = HHSConstants.EMPTY_STRING;
	private String procStatusId;
	private String levelId;
	private String levelReviewerId;
	private String userId;

	/**
	 * @return the contractId
	 */
	public int getContractId()
	{
		return contractId;
	}

	/**
	 * @param contractId the contractId to set
	 */
	public void setContractId(int contractId)
	{
		this.contractId = contractId;
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
	 * @return the taskType
	 */
	public String getTaskType()
	{
		return taskType;
	}

	/**
	 * @param taskType the taskType to set
	 */
	public void setTaskType(String taskType)
	{
		this.taskType = taskType;
	}

	/**
	 * @return the entityType
	 */
	public String getEntityType()
	{
		return entityType;
	}

	/**
	 * @param entityType the entityType to set
	 */
	public void setEntityType(String entityType)
	{
		this.entityType = entityType;
	}

	/**
	 * @return the entityId
	 */
	public String getEntityId()
	{
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId)
	{
		this.entityId = entityId;
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
	 * @return the levelId
	 */
	public String getLevelId()
	{
		return levelId;
	}

	/**
	 * @param levelId the levelId to set
	 */
	public void setLevelId(String levelId)
	{
		this.levelId = levelId;
	}

	/**
	 * @return the levelReviewerId
	 */
	public String getLevelReviewerId()
	{
		return levelReviewerId;
	}

	/**
	 * @param levelReviewerId the levelReviewerId to set
	 */
	public void setLevelReviewerId(String levelReviewerId)
	{
		this.levelReviewerId = levelReviewerId;
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

	@Override
	public String toString()
	{
		return "TaskLookupBean [agencyId=" + agencyId + ", workflowId=" + workflowId + ", contractId=" + contractId
				+ ", taskType=" + taskType + ", entityType=" + entityType + ", entityId=" + entityId
				+ ", procStatusId=" + procStatusId + ", levelId=" + levelId + ", levelReviewerId=" + levelReviewerId
				+ ", userId=" + userId + "]";
	}

}