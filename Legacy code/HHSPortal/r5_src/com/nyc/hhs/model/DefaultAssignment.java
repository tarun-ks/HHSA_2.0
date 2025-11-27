package com.nyc.hhs.model;

import java.io.InputStream;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

//This is a Bean class added for default assignment functionality
public class DefaultAssignment implements Comparable<DefaultAssignment>
{
	private String defaultAssignmentId;
	@RegExp(value ="^\\d{0,22}")
	private String contractId;
	private String assigneeUserId;
	@Length(max = 55)
	//REVIEW_PROCESS
	private String taskType;
	@Length(max = 20)
	//TASK_LEVEL
	private String taskLevel;
	private String createdByUserId;
	private String modifiedByUserId;
	private String askFlag;
	private String defaultAssignments;
	private String keepDefault;
	private String isfinancials;
	private String entityId;

	private String fileType;
	private String orgName;
	private String docTitle;
	private double contentSize;
	private String documentId;
	private InputStream contentElements;
	private String lsBaseFolderName;

	
	public String getBaseFolderName()
	{
		return lsBaseFolderName;
	}

	public void setBaseFolderName(String lsBaseFolderName)
	{
		this.lsBaseFolderName = lsBaseFolderName;
	}

	public DefaultAssignment()
	{
		super();
	}

	public String getFileType()
	{
		return fileType;
	}

	public void setFileType(String fileType)
	{
		this.fileType = fileType;
	}

	public String getOrgName()
	{
		return orgName;
	}

	public void setOrgName(String orgName)
	{
		this.orgName = orgName;
	}

	public String getDocTitle()
	{
		return docTitle;
	}

	public void setDocTitle(String docTitle)
	{
		this.docTitle = docTitle;
	}

	public String getDefaultAssignments()
	{
		return defaultAssignments;
	}

	public void setDefaultAssignments(String defaultAssignments)
	{
		this.defaultAssignments = defaultAssignments;
	}

	public String getKeepDefault()
	{
		return keepDefault;
	}

	public void setKeepDefault(String keepDefault)
	{
		this.keepDefault = keepDefault;
	}

	public String getDefaultAssignmentId()
	{
		return defaultAssignmentId;
	}

	public void setDefaultAssignmentId(String defaultAssignmentId)
	{
		this.defaultAssignmentId = defaultAssignmentId;
	}

	public String getContractId()
	{
		return contractId;
	}

	public void setContractId(String contractId)
	{
		this.contractId = contractId;
	}

	public String getAssigneeUserId()
	{
		return assigneeUserId;
	}

	public void setAssigneeUserId(String assigneeUserId)
	{
		this.assigneeUserId = assigneeUserId;
	}

	public String getTaskType()
	{
		return taskType;
	}

	public void setTaskType(String taskType)
	{
		this.taskType = taskType;
	}

	public String getTaskLevel()
	{
		return taskLevel;
	}

	public void setTaskLevel(String taskLevel)
	{
		this.taskLevel = taskLevel;
	}

	public String getCreatedByUserId()
	{
		return createdByUserId;
	}

	public void setCreatedByUserId(String createdByUserId)
	{
		this.createdByUserId = createdByUserId;
	}

	public String getModifiedByUserId()
	{
		return modifiedByUserId;
	}

	public void setModifiedByUserId(String modifiedByUserId)
	{
		this.modifiedByUserId = modifiedByUserId;
	}

	public String getAskFlag()
	{
		return askFlag;
	}

	public void setAskFlag(String askFlag)
	{
		this.askFlag = askFlag;
	}

	public String getIsfinancials()
	{
		return isfinancials;
	}

	public void setIsfinancials(String isfinancials)
	{
		this.isfinancials = isfinancials;
	}

	public String getEntityId()
	{
		return entityId;
	}

	public void setEntityId(String entityId)
	{
		this.entityId = entityId;
	}

	/**
	 * @return the contentSize
	 */
	public double getContentSize()
	{
		return contentSize;
	}

	/**
	 * @param contentSize the contentSize to set
	 */
	public void setContentSize(double contentSize)
	{
		this.contentSize = contentSize;
	}

	/**
	 * @return the documentId
	 */
	public String getDocumentId()
	{
		return documentId;
	}

	/**
	 * @param documentId the documentId to set
	 */
	public void setDocumentId(String documentId)
	{
		this.documentId = documentId;
	}

	@Override
	public int compareTo(DefaultAssignment loDefaultAssignment)
	{
		return loDefaultAssignment.getOrgName().compareTo(this.getOrgName());
	}

	/**
	 * @return the contentElements
	 */
	public InputStream getContentElements()
	{
		return contentElements;
	}

	/**
	 * @param contentElements the contentElements to set
	 */
	public void setContentElements(InputStream contentElements)
	{
		this.contentElements = contentElements;
	}

}
