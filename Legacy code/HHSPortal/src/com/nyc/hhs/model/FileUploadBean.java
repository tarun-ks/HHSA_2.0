package com.nyc.hhs.model;

import java.util.Date;

import javax.mail.internet.MimeBodyPart;

/**
 * This class will be used to upload file on server and file details in 
 * database.
 *  
 */

public class FileUploadBean
{
	private String msDocCategory;
	private String msDocType;
	private String msDocName;
	private String msFileType;
	private String msContentType;
	private String msFilePath;
	private String msStatus;
	private MimeBodyPart moFiledata;

	private String msApplicationId;
	private String msDocumentId;
	private Date moEffectiveDate;
	private String msLastModifiedBy;
	private Date moLastModifiedDate;
	private String msActions;
	private Integer moSeqNo;
	private String msDate;

	
	public String getDocCategory()
	{
		return msDocCategory;
	}

	public void setDocCategory(String asDocCategory)
	{
		this.msDocCategory = asDocCategory;
	}

	public String getDocType()
	{
		return msDocType;
	}

	public void setDocType(String asDocType)
	{
		this.msDocType = asDocType;
	}

	public String getDocName()
	{
		return msDocName;
	}

	public void setDocName(String asDocName)
	{
		this.msDocName = asDocName;
	}

	public String getFileType()
	{
		return msFileType;
	}

	public void setFileType(String asFileType)
	{
		this.msFileType = asFileType;
	}

	public String getContentType()
	{
		return msContentType;
	}

	public void setContentType(String asContentType)
	{
		this.msContentType = asContentType;
	}

	public MimeBodyPart getFiledata()
	{
		return moFiledata;
	}

	public void setFiledata(MimeBodyPart aoFiledata)
	{
		this.moFiledata = aoFiledata;
	}

	public String getFilePath()
	{
		return msFilePath;
	}

	public void setFilePath(String asFilePath)
	{
		this.msFilePath = asFilePath;
	}

	public String getApplicationId()
	{
		return msApplicationId;
	}

	public void setApplicationId(String asApplicationId)
	{
		this.msApplicationId = asApplicationId;
	}

	public String getDocumentId()
	{
		return msDocumentId;
	}

	public void setDocumentId(String asDocumentId)
	{
		this.msDocumentId = asDocumentId;
	}

	public Date getEffectiveDate()
	{
		return moEffectiveDate;
	}

	public void setEffectiveDate(Date aoEffectiveDate)
	{
		this.moEffectiveDate = aoEffectiveDate;
	}

	public String getStatus()
	{
		return msStatus;
	}

	public void setStatus(String asStatus)
	{
		this.msStatus = asStatus;
	}

	public String getLastModifiedBy()
	{
		return msLastModifiedBy;
	}

	public void setLastModifiedBy(String asLastModifiedBy)
	{
		this.msLastModifiedBy = asLastModifiedBy;
	}

	public Date getLastModifiedDate()
	{
		return moLastModifiedDate;
	}

	public void setLastModifiedDate(Date aoLastModifiedDate)
	{
		this.moLastModifiedDate = aoLastModifiedDate;
	}

	public String getActions()
	{
		return msActions;
	}

	public void setActions(String asActions)
	{
		this.msActions = asActions;
	}

	public Integer getSeqNo()
	{
		return moSeqNo;
	}

	public void setSeqNo(Integer aoSeqNo)
	{
		this.moSeqNo = aoSeqNo;
	}

	public String getDate()
	{
		return msDate;
	}

	public void setDate(String asDate)
	{
		this.msDate = asDate;
	}

	@Override
	public String toString() {
		return "FileUploadBean [msDocCategory=" + msDocCategory
				+ ", msDocType=" + msDocType + ", msDocName=" + msDocName
				+ ", msFileType=" + msFileType + ", msContentType="
				+ msContentType + ", msFilePath=" + msFilePath + ", msStatus="
				+ msStatus + ", moFiledata=" + moFiledata
				+ ", msApplicationId=" + msApplicationId + ", msDocumentId="
				+ msDocumentId + ", moEffectiveDate=" + moEffectiveDate
				+ ", msLastModifiedBy=" + msLastModifiedBy
				+ ", moLastModifiedDate=" + moLastModifiedDate + ", msActions="
				+ msActions + ", moSeqNo=" + moSeqNo + ", msDate=" + msDate
				+ "]";
	}
}
