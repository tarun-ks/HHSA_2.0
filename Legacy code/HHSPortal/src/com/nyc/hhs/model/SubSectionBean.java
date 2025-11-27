package com.nyc.hhs.model;
import java.util.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the sub-section information.
 *
 */

public class SubSectionBean {
	
	@Length(max = 20)
	private String	msOrganizationId   ;     
	private String	msUserId    ; 
	@Length(max = 20)
	private String	msSectionId ;
	@Length(max = 20)
	private String	msApplicationId ;        
	private String	msDocumentCategory ;     
	private String	msDocumentId  ;          
	private String	msDocumentType  ;        
	private String	msSubSectionStatus ;     
	private Date	moModifiedDate  ;        
	private String	msModifiedBy    ;        
	private Date	moSubmissionDate   ;     
	private String	msSubmittedBy   ;        
	private String	msQuestionId   ;         
	private String	msFormId     ;           
	private String	msFormName   ;           
	private String	msFormVersion  ;         
	private String	msProcSubSectionType  ;  
	private String	msProcSubSectionStatus  ;
	private String	msSubSectionID  ;
	private String msServiceAppId;
	
	public String getServiceAppId()
	{
		return msServiceAppId;
	}
	public void setServiceAppId(String msServiceAppId)
	{
		this.msServiceAppId = msServiceAppId;
	}
	public String getSubSectionID()
	{
		return msSubSectionID;
	}
	public void setSubSectionID(String msSubSectionID)
	{
		this.msSubSectionID = msSubSectionID;
	}
	public String getOrganizationId()
	{
		return msOrganizationId;
	}
	public void setOrganizationId(String organizationId)
	{
		this.msOrganizationId = organizationId;
	}
	public String getUserId()
	{
		return msUserId;
	}
	public void setUserId(String userId)
	{
		this.msUserId = userId;
	}
	public String getSectionId()
	{
		return msSectionId;
	}
	public void setSectionId(String sectionId)
	{
		this.msSectionId = sectionId;
	}
	public String getApplicationId()
	{
		return msApplicationId;
	}
	public void setApplicationId(String applicationId)
	{
		this.msApplicationId = applicationId;
	}
	public String getDocumentCategory()
	{
		return msDocumentCategory;
	}
	public void setDocumentCategory(String documentCategory)
	{
		this.msDocumentCategory = documentCategory;
	}
	public String getDocumentId()
	{
		return msDocumentId;
	}
	public void setDocumentId(String documentId)
	{
		this.msDocumentId = documentId;
	}
	public String getDocumentType()
	{
		return msDocumentType;
	}
	public void setDocumentType(String documentType)
	{
		this.msDocumentType = documentType;
	}
	public String getSubSectionStatus()
	{
		return msSubSectionStatus;
	}
	public void setSubSectionStatus(String subSectionStatus)
	{
		this.msSubSectionStatus = subSectionStatus;
	}
	public Date getModifiedDate()
	{
		return moModifiedDate;
	}
	public void setModifiedDate(Date modifiedDate)
	{
		this.moModifiedDate = modifiedDate;
	}
	public String getModifiedBy()
	{
		return msModifiedBy;
	}
	public void setModifiedBy(String modifiedBy)
	{
		this.msModifiedBy = modifiedBy;
	}
	public Date getSubmissionDate()
	{
		return moSubmissionDate;
	}
	public void setSubmissionDate(Date submissionDate)
	{
		this.moSubmissionDate = submissionDate;
	}
	public String getSubmittedBy()
	{
		return msSubmittedBy;
	}
	public void setSubmittedBy(String submittedBy)
	{
		this.msSubmittedBy = submittedBy;
	}
	
	public String getQuestionId()
	{
		return msQuestionId;
	}
	public void setQuestionId(String msQuestionId)
	{
		this.msQuestionId = msQuestionId;
	}
	public String getFormId()
	{
		return msFormId;
	}
	public void setFormId(String formId)
	{
		this.msFormId = formId;
	}
	public String getFormName()
	{
		return msFormName;
	}
	public void setFormName(String formName)
	{
		this.msFormName = formName;
	}
	public String getFormVersion()
	{
		return msFormVersion;
	}
	public void setFormVersion(String formVersion)
	{
		this.msFormVersion = formVersion;
	}
	public String getProcSubSectionType()
	{
		return msProcSubSectionType;
	}
	public void setProcSubSectionType(String procSubSectionType)
	{
		this.msProcSubSectionType = procSubSectionType;
	}
	public String getProcSubSectionStatus()
	{
		return msProcSubSectionStatus;
	}
	public void setProcSubSectionStatus(String procSubSectionStatus)
	{
		this.msProcSubSectionStatus = procSubSectionStatus;
	}
	@Override
	public String toString() {
		return "SubSectionBean [msOrganizationId=" + msOrganizationId
				+ ", msUserId=" + msUserId + ", msSectionId=" + msSectionId
				+ ", msApplicationId=" + msApplicationId
				+ ", msDocumentCategory=" + msDocumentCategory
				+ ", msDocumentId=" + msDocumentId + ", msDocumentType="
				+ msDocumentType + ", msSubSectionStatus=" + msSubSectionStatus
				+ ", moModifiedDate=" + moModifiedDate + ", msModifiedBy="
				+ msModifiedBy + ", moSubmissionDate=" + moSubmissionDate
				+ ", msSubmittedBy=" + msSubmittedBy + ", msQuestionId="
				+ msQuestionId + ", msFormId=" + msFormId + ", msFormName="
				+ msFormName + ", msFormVersion=" + msFormVersion
				+ ", msProcSubSectionType=" + msProcSubSectionType
				+ ", msProcSubSectionStatus=" + msProcSubSectionStatus
				+ ", msSubSectionID=" + msSubSectionID + ", msServiceAppId="
				+ msServiceAppId + "]";
	}
	
	

}