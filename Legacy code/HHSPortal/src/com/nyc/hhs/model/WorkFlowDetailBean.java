package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the Work-flow details.
 *
 */

public class WorkFlowDetailBean
{

	public String msQuestionDocumentName;
	public String msDocType;
	public String msDocInfo;
	public String msCurrentStatus;
	public String msModifiedDate;
	public String msModifiedBy;
	public String msAssignedStatus;
	@Length(max = 20)
	public String msOrgID;
	public String msDocOrSectionID;
	public String getMsQuestionDocumentName() {
		return msQuestionDocumentName;
	}
	public void setMsQuestionDocumentName(String msQuestionDocumentName) {
		this.msQuestionDocumentName = msQuestionDocumentName;
	}
	public String getMsDocType() {
		return msDocType;
	}
	public void setMsDocType(String msDocType) {
		this.msDocType = msDocType;
	}
	public String getMsDocInfo() {
		return msDocInfo;
	}
	public void setMsDocInfo(String msDocInfo) {
		this.msDocInfo = msDocInfo;
	}
	public String getMsCurrentStatus() {
		return msCurrentStatus;
	}
	public void setMsCurrentStatus(String msCurrentStatus) {
		this.msCurrentStatus = msCurrentStatus;
	}
	public String getMsModifiedDate() {
		return msModifiedDate;
	}
	public void setMsModifiedDate(String msModifiedDate) {
		this.msModifiedDate = msModifiedDate;
	}
	public String getMsModifiedBy() {
		return msModifiedBy;
	}
	public void setMsModifiedBy(String msModifiedBy) {
		this.msModifiedBy = msModifiedBy;
	}
	public String getMsAssignedStatus() {
		return msAssignedStatus;
	}
	public void setMsAssignedStatus(String msAssignedStatus) {
		this.msAssignedStatus = msAssignedStatus;
	}
	public String getMsOrgID() {
		return msOrgID;
	}
	public void setMsOrgID(String msOrgID) {
		this.msOrgID = msOrgID;
	}
	public String getMsDocOrSectionID() {
		return msDocOrSectionID;
	}
	public void setMsDocOrSectionID(String msDocOrSectionID) {
		this.msDocOrSectionID = msDocOrSectionID;
	}
	public WorkFlowDetailBean(String msQuestionDocumentName, String msDocType,
			String msDocInfo, String msCurrentStatus, String msModifiedDate,
			String msModifiedBy, String msAssignedStatus, String msOrgID,
			String msDocOrSectionID) {
		this.msQuestionDocumentName = msQuestionDocumentName;
		this.msDocType = msDocType;
		this.msDocInfo = msDocInfo;
		this.msCurrentStatus = msCurrentStatus;
		this.msModifiedDate = msModifiedDate;
		this.msModifiedBy = msModifiedBy;
		this.msAssignedStatus = msAssignedStatus;
		this.msOrgID = msOrgID;
		this.msDocOrSectionID = msDocOrSectionID;
	}
	@Override
	public String toString() {
		return "WorkFlowDetailBean [msQuestionDocumentName="
				+ msQuestionDocumentName + ", msDocType=" + msDocType
				+ ", msDocInfo=" + msDocInfo + ", msCurrentStatus="
				+ msCurrentStatus + ", msModifiedDate=" + msModifiedDate
				+ ", msModifiedBy=" + msModifiedBy + ", msAssignedStatus="
				+ msAssignedStatus + ", msOrgID=" + msOrgID
				+ ", msDocOrSectionID=" + msDocOrSectionID + "]";
	}

}
