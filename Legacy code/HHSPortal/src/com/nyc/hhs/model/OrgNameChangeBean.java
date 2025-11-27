package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the Organization Name information.
 *
 */

public class OrgNameChangeBean {

	@Length(max = 100)
	private String lsCurrentOrgLegalName;
	@Length(max = 100)
	private String lsProposesOrgLegalName;
	private String lsReasonsComments;
	private String lsModifiedBy;
	private String lsModifiedDate;
	
	public String getLsCurrentOrgLegalName() {
		return lsCurrentOrgLegalName;
	}
	public void setLsCurrentOrgLegalName(String lsCurrentOrgLegalName) {
		this.lsCurrentOrgLegalName = lsCurrentOrgLegalName;
	}
	public String getLsProposesOrgLegalName() {
		return lsProposesOrgLegalName;
	}
	public void setLsProposesOrgLegalName(String lsProposesOrgLegalName) {
		this.lsProposesOrgLegalName = lsProposesOrgLegalName;
	}
	public String getLsReasonsComments() {
		return lsReasonsComments;
	}
	public void setLsReasonsComments(String lsReasonsComments) {
		this.lsReasonsComments = lsReasonsComments;
	}
	public String getLsModifiedBy() {
		return lsModifiedBy;
	}
	public void setLsModifiedBy(String lsModifiedBy) {
		this.lsModifiedBy = lsModifiedBy;
	}
	public String getLsModifiedDate() {
		return lsModifiedDate;
	}
	public void setLsModifiedDate(String lsModifiedDate) {
		this.lsModifiedDate = lsModifiedDate;
	}
	@Override
	public String toString() {
		return "OrgNameChangeBean [lsCurrentOrgLegalName="
				+ lsCurrentOrgLegalName + ", lsProposesOrgLegalName="
				+ lsProposesOrgLegalName + ", lsReasonsComments="
				+ lsReasonsComments + ", lsModifiedBy=" + lsModifiedBy
				+ ", lsModifiedDate=" + lsModifiedDate + "]";
	}
	
	
}
