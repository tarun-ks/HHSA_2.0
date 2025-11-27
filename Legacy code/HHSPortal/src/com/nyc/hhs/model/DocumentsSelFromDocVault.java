package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

import com.filenet.api.util.Id;

/**
 * This class is a bean which maintains the Document Selected from Document
 * Vault information.
 * 
 */

public class DocumentsSelFromDocVault
{

	public String msLastModifiedBy;
	public String msLastModifiedDate;
	public String msDocumentName;
	@NotNull
	private Id msDocumentId;
	private String msDocumnetType;
	private String msOrganisationId;
	private String msFormName;
	private String msFormVersion;
	private String msSubmittedBy;
	private String msSubmittedDate;
	private String msDocumnetCategory;
	private String msUserId;
	public String msDocumentTitle;
	public String msFormId;
	public String msServiceAppId;
	public String msSectionId;
	// Added for Release 5
	private com.filenet.api.core.Folder msParent;

	public com.filenet.api.core.Folder getParent()
	{
		return msParent;
	}

	public void setParent(com.filenet.api.core.Folder msParent)
	{
		this.msParent = msParent;
	}

	private String filePath;

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String asFilePath)
	{
		this.filePath = asFilePath;
	}

	// Release 5 ends
	public String getMsSectionId()
	{
		return msSectionId;
	}

	public void setMsSectionId(String msSectionId)
	{
		this.msSectionId = msSectionId;
	}

	public String getMsFormId()
	{
		return msFormId;
	}

	public void setMsFormId(String msFormId)
	{
		this.msFormId = msFormId;
	}

	public String getMsDocumentTitle()
	{
		return msDocumentTitle;
	}

	public void setMsDocumentTitle(String msDocumentTitle)
	{
		this.msDocumentTitle = msDocumentTitle;
	}

	public String getMsUserId()
	{
		return msUserId;
	}

	public void setMsUserId(String msUserId)
	{
		this.msUserId = msUserId;
	}

	public String getMsDocumnetCategory()
	{
		return msDocumnetCategory;
	}

	public void setMsDocumnetCategory(String msDocumnetCategory)
	{
		this.msDocumnetCategory = msDocumnetCategory;
	}

	public String getMsLastModifiedBy()
	{
		return msLastModifiedBy;
	}

	public void setMsLastModifiedBy(String msLastModifiedBy)
	{
		this.msLastModifiedBy = msLastModifiedBy;
	}

	public String getMsDocumentName()
	{
		return msDocumentName;
	}

	public void setMsDocumentName(String msDocumentName)
	{
		this.msDocumentName = msDocumentName;
	}

	public Id getMsDocumentId()
	{
		return msDocumentId;
	}

	public void setMsDocumentId(Id msDocumentId)
	{
		this.msDocumentId = msDocumentId;
	}

	public String getMsDocumnetType()
	{
		return msDocumnetType;
	}

	public void setMsDocumnetType(String msDocumnetType)
	{
		this.msDocumnetType = msDocumnetType;
	}

	public String getMsOrganisationId()
	{
		return msOrganisationId;
	}

	public void setMsOrganisationId(String msOrganisationId)
	{
		this.msOrganisationId = msOrganisationId;
	}

	public String getMsFormName()
	{
		return msFormName;
	}

	public void setMsFormName(String msFormName)
	{
		this.msFormName = msFormName;
	}

	public String getMsLastModifiedDate()
	{
		return msLastModifiedDate;
	}

	public void setMsLastModifiedDate(String msLastModifiedDate)
	{
		this.msLastModifiedDate = msLastModifiedDate;
	}

	public String getMsSubmittedDate()
	{
		return msSubmittedDate;
	}

	public void setMsSubmittedDate(String msSubmittedDate)
	{
		this.msSubmittedDate = msSubmittedDate;
	}

	public String getMsFormVersion()
	{
		return msFormVersion;
	}

	public void setMsFormVersion(String msFormVersion)
	{
		this.msFormVersion = msFormVersion;
	}

	public String getMsSubmittedBy()
	{
		return msSubmittedBy;
	}

	public void setMsSubmittedBy(String msSubmittedBy)
	{
		this.msSubmittedBy = msSubmittedBy;
	}

	public String getMsServiceAppId()
	{
		return msServiceAppId;
	}

	public void setMsServiceAppId(String msServiceAppId)
	{
		this.msServiceAppId = msServiceAppId;
	}

	@Override
	public String toString()
	{
		return "DocumentsSelFromDocVault [msLastModifiedBy=" + msLastModifiedBy + ", msLastModifiedDate="
				+ msLastModifiedDate + ", msDocumentName=" + msDocumentName + ", msDocumentId=" + msDocumentId
				+ ", msDocumnetType=" + msDocumnetType + ", msOrganisationId=" + msOrganisationId + ", msFormName="
				+ msFormName + ", msFormVersion=" + msFormVersion + ", msSubmittedBy=" + msSubmittedBy
				+ ", msSubmittedDate=" + msSubmittedDate + ", msDocumnetCategory=" + msDocumnetCategory + ", msUserId="
				+ msUserId + ", msDocumentTitle=" + msDocumentTitle + ", msFormId=" + msFormId + ", msServiceAppId="
				+ msServiceAppId + ", msSectionId=" + msSectionId + ", msParent=" + msParent + ", filePath=" + filePath
				+ "]";
	}
}