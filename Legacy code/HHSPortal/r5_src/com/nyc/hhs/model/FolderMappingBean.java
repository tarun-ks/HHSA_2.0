package com.nyc.hhs.model;
//This is a Bean class added for folder mapping functionality
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class FolderMappingBean
{

	private String attachmentFlag;
	private JsonTreeBean data;
	private List<String> parentIdList;
	private String createdBy;
	private String createdDate;
	private int documentCount;
	private int folderCount;
	private String folderFilenetId;
	private String folderMappingId;
	private String folderName;
	private String modifiedBy;
	private String modifiedDate;
	private String movedToRecycleBin;
	private String organizationId;
	private String organizationType;
	private String parentFolderFilenetId;
	private String processing;
	private String sharedFlag;
	private String type;
	private String documentId;
	private String entityName;
	
	@Length(max = 40)
	//FOLDER_FILENET_ID
	private String id;
	@Length(max = 50)
	//FOLDER_NAME
	private String text;
	@Length(max = 40)
	private String parent;
	private FolderTreeState state;

	
	public FolderMappingBean()
	{
		super();
	}

	public FolderTreeState getState()
	{
		return state;
	}

	public void setState(FolderTreeState state)
	{
		this.state = state;
	}

	public String getOrganizationType()
	{
		return organizationType;
	}

	public void setOrganizationType(String organizationType)
	{
		this.organizationType = organizationType;
	}

	public JsonTreeBean getData()
	{
		return data;
	}

	public void setData(JsonTreeBean data)
	{
		this.data = data;
	}

	public List<String> getParentIdList()
	{
		return parentIdList;
	}

	public void setParentIdList(List<String> parentIdList)
	{
		this.parentIdList = parentIdList;
	}

	public String getDocumentId()
	{
		return documentId;
	}

	public void setDocumentId(String documentId)
	{
		this.documentId = documentId;
	}

	public String getAttachmentFlag()
	{
		return attachmentFlag;
	}

	public void setAttachmentFlag(String attachmentFlag)
	{
		this.attachmentFlag = attachmentFlag;
	}

	public String getCreatedBy()
	{
		return createdBy;
	}

	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	public String getCreatedDate()
	{
		return createdDate;
	}

	public void setCreatedDate(String createdDate)
	{
		this.createdDate = createdDate;
	}

	public String getFolderFilenetId()
	{
		return folderFilenetId;
	}

	public void setFolderFilenetId(String folderFilenetId)
	{
		this.folderFilenetId = folderFilenetId;
	}

	public String getFolderMappingId()
	{
		return folderMappingId;
	}

	public void setFolderMappingId(String folderMappingId)
	{
		this.folderMappingId = folderMappingId;
	}

	public String getFolderName()
	{
		return folderName;
	}

	public void setFolderName(String folderName)
	{
		this.folderName = folderName;
	}

	public String getModifiedBy()
	{
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}

	public String getModifiedDate()
	{
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate)
	{
		this.modifiedDate = modifiedDate;
	}

	public String getMovedToRecycleBin()
	{
		return movedToRecycleBin;
	}

	public void setMovedToRecycleBin(String movedToRecycleBin)
	{
		this.movedToRecycleBin = movedToRecycleBin;
	}

	public String getOrganizationId()
	{
		return organizationId;
	}

	public void setOrganizationId(String organizationId)
	{
		this.organizationId = organizationId;
	}

	public String getParentFolderFilenetId()
	{
		return parentFolderFilenetId;
	}

	public void setParentFolderFilenetId(String parentFolderFilenetId)
	{
		this.parentFolderFilenetId = parentFolderFilenetId;
	}

	public String getProcessing()
	{
		return processing;
	}

	public void setProcessing(String processing)
	{
		this.processing = processing;
	}

	public String getSharedFlag()
	{
		return sharedFlag;
	}

	public void setSharedFlag(String sharedFlag)
	{
		this.sharedFlag = sharedFlag;
	}

	public String getType()
	{
		return type;
	}

	public void setType(String type)
	{
		this.type = type;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getParent()
	{
		return parent;
	}

	public void setParent(String parent)
	{
		this.parent = parent;
	}

	public void setDocumentCount(int documentCount)
	{
		this.documentCount = documentCount;
	}

	public int getDocumentCount()
	{
		return documentCount;
	}

	public void setFolderCount(int folderCount)
	{
		this.folderCount = folderCount;
	}

	public int getFolderCount()
	{
		return folderCount;
	}

	public String getEntityName()
	{
		return entityName;
	}

	public void setEntityName(String entityName)
	{
		this.entityName = entityName;
	}

}
