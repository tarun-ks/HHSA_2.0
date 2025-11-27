package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the Business Application Summary 
 * information which includes Section Id, Question Document Name, Document Type,
 * Status, Modified Date, Modified By, Document Id, Form Id, Form Version
 * and Form Name.
 *
 */

public class BusinessApplicationSummary {
	
	@Length(max = 20) 
	private String msSectionID;
	private String msQuestionsDocumentName;
	private String msDocumentType;
	private String msStatus;
	private String msModifiedDate;
	private String msModifiedBy;
	private String msDocumentID;
	private String msFormID;
	private String msFormVersion;
	private String msFormName;
	
	
	
	public BusinessApplicationSummary(String msSectionID, String msQuestionsDocumentName, String msDocumentType,
			String msStatus, String msModifiedDate, String msModifiedBy, String msDocumentID, String msFormID,
			String msFormVersion, String msFormName)
	{
		this.msSectionID = msSectionID;
		this.msQuestionsDocumentName = msQuestionsDocumentName;
		this.msDocumentType = msDocumentType;
		this.msStatus = msStatus;
		this.msModifiedDate = msModifiedDate;
		this.msModifiedBy = msModifiedBy;
		this.msDocumentID = msDocumentID;
		this.msFormID = msFormID;
		this.msFormVersion = msFormVersion;
		this.msFormName = msFormName;
	}
	
	public String getMsSectionID()
	{
		return msSectionID;
	}
	public void setMsSectionID(String msSectionID)
	{
		this.msSectionID = msSectionID;
	}
	public String getMsQuestionsDocumentName()
	{
		return msQuestionsDocumentName;
	}
	public void setMsQuestionsDocumentName(String msQuestionsDocumentName)
	{
		this.msQuestionsDocumentName = msQuestionsDocumentName;
	}
	public String getMsDocumentType()
	{
		return msDocumentType;
	}
	public void setMsDocumentType(String msDocumentType)
	{
		this.msDocumentType = msDocumentType;
	}
	public String getMsStatus()
	{
		return msStatus;
	}
	public void setMsStatus(String msStatus)
	{
		this.msStatus = msStatus;
	}
	public String getMsModifiedDate()
	{
		return msModifiedDate;
	}
	public void setMsModifiedDate(String msModifiedDate)
	{
		this.msModifiedDate = msModifiedDate;
	}
	public String getMsModifiedBy()
	{
		return msModifiedBy;
	}
	public void setMsModifiedBy(String msModifiedBy)
	{
		this.msModifiedBy = msModifiedBy;
	}
	public String getMsDocumentID()
	{
		return msDocumentID;
	}
	public void setMsDocumentID(String msDocumentID)
	{
		this.msDocumentID = msDocumentID;
	}
	public String getMsFormID()
	{
		return msFormID;
	}
	public void setMsFormID(String msFormID)
	{
		this.msFormID = msFormID;
	}
	public String getMsFormVersion()
	{
		return msFormVersion;
	}
	public void setMsFormVersion(String msFormVersion)
	{
		this.msFormVersion = msFormVersion;
	}
	public String getMsFormName()
	{
		return msFormName;
	}
	public void setMsFormName(String msFormName)
	{
		this.msFormName = msFormName;
	}

	@Override
	public String toString() {
		return "BusinessApplicationSummary [msSectionID=" + msSectionID
				+ ", msQuestionsDocumentName=" + msQuestionsDocumentName
				+ ", msDocumentType=" + msDocumentType + ", msStatus="
				+ msStatus + ", msModifiedDate=" + msModifiedDate
				+ ", msModifiedBy=" + msModifiedBy + ", msDocumentID="
				+ msDocumentID + ", msFormID=" + msFormID + ", msFormVersion="
				+ msFormVersion + ", msFormName=" + msFormName + "]";
	}
	
}
