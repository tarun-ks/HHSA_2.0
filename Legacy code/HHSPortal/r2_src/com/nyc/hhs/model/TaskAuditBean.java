package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class TaskAuditBean
{
	@Length(max = 50)
	private String workflowId;
	//@RegExp(value ="^\\d{0,22}")
	private String taskSequence;
	@Length(max = 100)
	private String eventType;
	@Length(max = 100)
	private String taskType;
	private String entityId;
	private String entityName;
	private String taskLevel;
	private String userType;
	private String contractId;
	private String createdBy;
	private String modifiedBy;
	private String createdByStaffId;
	private String modifiedByStaffId;
	private String assignedTo;
	private String nextLevel;
	private String linkedWobNo;

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
	 * @return the taskId
	 */
	public String getTaskSequence()
	{
		return taskSequence;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskSequence(String taskSequence)
	{
		this.taskSequence = taskSequence;
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
	 * @return the entityName
	 */
	public String getEntityName()
	{
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName)
	{
		this.entityName = entityName;
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
	 * @return the userType
	 */
	public String getUserType()
	{
		return userType;
	}

	/**
	 * @param userType the userType to set
	 */
	public void setUserType(String userType)
	{
		this.userType = userType;
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
	 * @return the createdBy
	 */
	public String getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy()
	{
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the createdByStaffId
	 */
	public String getCreatedByStaffId()
	{
		return createdByStaffId;
	}

	/**
	 * @param createdByStaffId the createdByStaffId to set
	 */
	public void setCreatedByStaffId(String createdByStaffId)
	{
		this.createdByStaffId = createdByStaffId;
	}

	/**
	 * @return the modifiedByStaffId
	 */
	public String getModifiedByStaffId()
	{
		return modifiedByStaffId;
	}

	/**
	 * @param modifiedByStaffId the modifiedByStaffId to set
	 */
	public void setModifiedByStaffId(String modifiedByStaffId)
	{
		this.modifiedByStaffId = modifiedByStaffId;
	}

	/**
	 * @return the assignedTo
	 */
	public String getAssignedTo()
	{
		return assignedTo;
	}

	/**
	 * @param assignedTo the assignedTo to set
	 */
	public void setAssignedTo(String assignedTo)
	{
		this.assignedTo = assignedTo;
	}

	/**
	 * @return the nextLevel
	 */
	public String getNextLevel()
	{
		return nextLevel;
	}

	/**
	 * @param nextLevel the nextLevel to set
	 */
	public void setNextLevel(String nextLevel)
	{
		this.nextLevel = nextLevel;
	}

	/**
	 * @return the linkedWobNo
	 */
	public String getLinkedWobNo()
	{
		return linkedWobNo;
	}

	/**
	 * @param linkedWobNo the linkedWobNo to set
	 */
	public void setLinkedWobNo(String linkedWobNo)
	{
		this.linkedWobNo = linkedWobNo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "TaskAuditBean [workflowId=" + workflowId + ", taskSequence=" + taskSequence + ", eventType="
				+ eventType + ", taskType=" + taskType + ", entityId=" + entityId + ", entityName=" + entityName
				+ ", taskLevel=" + taskLevel + ", userType=" + userType + ", contractId=" + contractId + ", createdBy="
				+ createdBy + ", modifiedBy=" + modifiedBy + ", createdByStaffId=" + createdByStaffId
				+ ", modifiedByStaffId=" + modifiedByStaffId + ", assignedTo=" + assignedTo + ", nextLevel="
				+ nextLevel + ", linkedWobNo=" + linkedWobNo + "]";
	}
}
