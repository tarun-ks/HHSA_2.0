package com.nyc.hhs.model;

/**
 * This class is a bean which maintains the Organization's Name Change information.
 *
 */

public class OrganizationNameChange {
	
	private String msCurrentOrgLegalName;
	private String msProposedOrgLegalName;
	private String msComments;
	private String msModifiedDate;
	private String msLastModifiedBy;
	private String msWorkFlowNumber;
	private String msProcStatus;
	private String msRequestId;
	private String msOrgId;
	public String getMsCurrentOrgLegalName() {
		return msCurrentOrgLegalName;
	}
	public void setMsCurrentOrgLegalName(String msCurrentOrgLegalName) {
		this.msCurrentOrgLegalName = msCurrentOrgLegalName;
	}
	public String getMsProposedOrgLegalName() {
		return msProposedOrgLegalName;
	}
	public void setMsProposedOrgLegalName(String msProposedOrgLegalName) {
		this.msProposedOrgLegalName = msProposedOrgLegalName;
	}
	public String getMsComments() {
		return msComments;
	}
	public void setMsComments(String msComments) {
		this.msComments = msComments;
	}
	public String getMsModifiedDate() {
		return msModifiedDate;
	}
	public void setMsModifiedDate(String msModifiedDate) {
		this.msModifiedDate = msModifiedDate;
	}
	public String getMsLastModifiedBy() {
		return msLastModifiedBy;
	}
	public void setMsLastModifiedBy(String msLastModifiedBy) {
		this.msLastModifiedBy = msLastModifiedBy;
	}
	public String getMsWorkFlowNumber() {
		return msWorkFlowNumber;
	}
	public void setMsWorkFlowNumber(String msWorkFlowNumber) {
		this.msWorkFlowNumber = msWorkFlowNumber;
	}
	public String getMsProcStatus() {
		return msProcStatus;
	}
	public void setMsProcStatus(String msProcStatus) {
		this.msProcStatus = msProcStatus;
	}
	public String getMsRequestId() {
		return msRequestId;
	}
	public void setMsRequestId(String msRequestId) {
		this.msRequestId = msRequestId;
	}
	public String getMsOrgId() {
		return msOrgId;
	}
	public void setMsOrgId(String msOrgId) {
		this.msOrgId = msOrgId;
	}
	@Override
	public String toString() {
		return "OrganizationNameChange [msCurrentOrgLegalName="
				+ msCurrentOrgLegalName + ", msProposedOrgLegalName="
				+ msProposedOrgLegalName + ", msComments=" + msComments
				+ ", msModifiedDate=" + msModifiedDate + ", msLastModifiedBy="
				+ msLastModifiedBy + ", msWorkFlowNumber=" + msWorkFlowNumber
				+ ", msProcStatus=" + msProcStatus + ", msRequestId="
				+ msRequestId + ", msOrgId=" + msOrgId + "]";
	}
}
