package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class HhsAuditBean
{
	private String eventName;
	private String eventType;
	@Length(max = 20)
	private String userId;
	private String data;
	private String entityType;
	private String entityId;
	private String providerComments;
	private String auditTableIdentifier;
	private String taskId;
	private String workflowId;
	private String internalComments;
	private String agencyId;
	private Boolean isTaskScreen;
	private String contractId;
	private String evalGrp;
	private String compConfId;
	private String taskLevel;
	private String taskEvent;

	/**
	 * @return the compConfId
	 */
	public String getCompConfId()
	{
		return compConfId;
	}

	/**
	 * @param compConfId the compConfId to set
	 */
	public void setCompConfId(String compConfId)
	{
		this.compConfId = compConfId;
	}

	/**
	 * @return the evalGrp
	 */
	public String getEvalGrp()
	{
		return evalGrp;
	}

	/**
	 * @param evalGrp the evalGrp to set
	 */
	public void setEvalGrp(String evalGrp)
	{
		this.evalGrp = evalGrp;
	}

	/**
	 * @return the isTaskScreen
	 */
	public Boolean getIsTaskScreen()
	{
		return isTaskScreen;
	}

	/**
	 * @param isTaskScreen the isTaskScreen to set
	 */
	public void setIsTaskScreen(Boolean isTaskScreen)
	{
		this.isTaskScreen = isTaskScreen;
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
	 * @return the internalComments
	 */
	public String getInternalComments()
	{
		return internalComments;
	}

	/**
	 * @param internalComments the internalComments to set
	 */
	public void setInternalComments(String internalComments)
	{
		this.internalComments = internalComments;
	}

	/**
	 * @return the taskId
	 */
	public String getTaskId()
	{
		return taskId;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId)
	{
		this.taskId = taskId;
	}

	/**
	 * @return the eventName
	 */
	public String getEventName()
	{
		return eventName;
	}

	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName)
	{
		this.eventName = eventName;
	}

	/**
	 * @return the eventType
	 */
	public String getEventType()
	{
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(String eventType)
	{
		this.eventType = eventType;
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
	 * @return the data
	 */
	public String getData()
	{
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data)
	{
		this.data = data;
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
	 * @return the providerComments
	 */
	public String getProviderComments()
	{
		return providerComments;
	}

	/**
	 * @param providerComments the providerComments to set
	 */
	public void setProviderComments(String providerComments)
	{
		this.providerComments = providerComments;
	}

	/**
	 * @return the auditTableIdentifier
	 */
	public String getAuditTableIdentifier()
	{
		return auditTableIdentifier;
	}

	/**
	 * @param auditTableIdentifier the auditTableIdentifier to set
	 */
	public void setAuditTableIdentifier(String auditTableIdentifier)
	{
		this.auditTableIdentifier = auditTableIdentifier;
	}

	/**
	 * @return the contractId
	 */
	public String getContractId()
	{
		return contractId;
	}

	/**
	 * @param contractId the contractId to set
	 */
	public void setContractId(String contractId)
	{
		this.contractId = contractId;
	}

	/**
	 * @return the taskLevel
	 */
	public String getTaskLevel()
	{
		return taskLevel;
	}

	/**
	 * @param taskLevel the taskLevel to set
	 */
	public void setTaskLevel(String taskLevel)
	{
		this.taskLevel = taskLevel;
	}

	/**
	 * @return the taskEvent
	 */
	public String getTaskEvent()
	{
		return taskEvent;
	}

	/**
	 * @param taskEvent the taskEvent to set
	 */
	public void setTaskEvent(String taskEvent)
	{
		this.taskEvent = taskEvent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "HhsAuditBean [eventName=" + eventName + ", eventType=" + eventType + ", userId=" + userId + ", data="
				+ data + ", entityType=" + entityType + ", entityId=" + entityId + ", providerComments="
				+ providerComments + ", auditTableIdentifier=" + auditTableIdentifier + ", taskId=" + taskId
				+ ", workflowId=" + workflowId + ", internalComments=" + internalComments + ", agencyId=" + agencyId
				+ ", isTaskScreen=" + isTaskScreen + ", contractId=" + contractId + ", evalGrp=" + evalGrp
				+ ", compConfId=" + compConfId + ", taskLevel=" + taskLevel + ", taskEvent=" + taskEvent + "]";
	}
}
