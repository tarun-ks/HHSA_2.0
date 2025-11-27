package com.nyc.hhs.model;
import java.util.Date;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This class is a bean which maintains the Section information.
 *
 */

public class SectionBean {
	
	@Length(max = 20) 
	private String	msSectionId;
	@Length(max = 20)
	private String	msOrganizationId;
	@Length(max = 20)
	private String	msApplicationId;
	private String	msUserId;
	private String	msSectionStatus;
	private String	msModifiedBy;
	private Date	moModifiedDate;
	private String	msSubmittedBy;
	private Date	moSubmissionDate;
	private String	msFormId;
	private String	msFormName;
	private String	msFormVersion;
	private String	msLockFlag;
	private String	msStatusId;
	
	private String msProcSectionStatus;
	private String msProcAppStatus;
	private String msApplicationStatus;
	
	public String getApplicationStatus() {
		return msApplicationStatus;
	}
	public void setApplicationStatus(String applicationStatus) {
		this.msApplicationStatus = applicationStatus;
	}
	public String getProcAppStatus() {
		return msProcAppStatus;
	}
	public void setProcAppStatus(String procAppStatus) {
		this.msProcAppStatus = procAppStatus;
	}
	public String getProcSectionStatus() {
		return msProcSectionStatus;
	}
	public void setProcSectionStatus(String procSectionStatus) {
		this.msProcSectionStatus = procSectionStatus;
	}
	public String getSectionId()
	{
		return msSectionId;
	}
	public void setSectionId(String sectionId)
	{
		this.msSectionId = sectionId;
	}
	public String getOrganizationId()
	{
		return msOrganizationId;
	}
	public void setOrganizationId(String organizationId)
	{
		this.msOrganizationId = organizationId;
	}
	public String getApplicationId()
	{
		return msApplicationId;
	}
	public void setApplicationId(String applicationId)
	{
		this.msApplicationId = applicationId;
	}
	public String getUserId()
	{
		return msUserId;
	}
	public void setUserId(String userId)
	{
		this.msUserId = userId;
	}
	public String getSectionStatus()
	{
		return msSectionStatus;
	}
	public void setSectionStatus(String sectionStatus)
	{
		this.msSectionStatus = sectionStatus;
	}
	public String getModifiedBy()
	{
		return msModifiedBy;
	}
	public void setModifiedBy(String modifiedBy)
	{
		this.msModifiedBy = modifiedBy;
	}
	public Date getModifiedDate()
	{
		return moModifiedDate;
	}
	public void setModifiedDate(Date modifiedDate)
	{
		this.moModifiedDate = modifiedDate;
	}
	public String getSubmittedBy()
	{
		return msSubmittedBy;
	}
	public void setSubmittedBy(String submittedBy)
	{
		this.msSubmittedBy = submittedBy;
	}
	public Date getSubmissionDate()
	{
		return moSubmissionDate;
	}
	public void setSubmissionDate(Date submissionDate)
	{
		this.moSubmissionDate = submissionDate;
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
	public String getLockFlag()
	{
		return msLockFlag;
	}
	public void setLockFlag(String lockFlag)
	{
		this.msLockFlag = lockFlag;
	}
	public String getStatusId()
	{
		return msStatusId;
	}
	public void setStatusId(String statusId)
	{
		this.msStatusId = statusId;
	}
	@Override
	public String toString() {
		return "SectionBean [msSectionId=" + msSectionId
				+ ", msOrganizationId=" + msOrganizationId
				+ ", msApplicationId=" + msApplicationId + ", msUserId="
				+ msUserId + ", msSectionStatus=" + msSectionStatus
				+ ", msModifiedBy=" + msModifiedBy + ", moModifiedDate="
				+ moModifiedDate + ", msSubmittedBy=" + msSubmittedBy
				+ ", moSubmissionDate=" + moSubmissionDate + ", msFormId="
				+ msFormId + ", msFormName=" + msFormName + ", msFormVersion="
				+ msFormVersion + ", msLockFlag=" + msLockFlag
				+ ", msStatusId=" + msStatusId + ", msProcSectionStatus="
				+ msProcSectionStatus + ", msProcAppStatus=" + msProcAppStatus
				+ ", msApplicationStatus=" + msApplicationStatus + "]";
	}
	
}
