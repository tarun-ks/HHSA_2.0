package com.nyc.hhs.model;

import java.sql.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the Taxonomy Linkage information.
 *
 */

public class TaxonomyLinkageBean {

	@Length(max = 50)
	private String msTaxonomyId;
	@Length(max = 50)
	private String msTaxonomyLinkageId;	
	private String msDeleteStatus;

	private String msModifyBy;
	private String msCreatedBy;
	private Date moCreatedDate;
	private Date moTMModifiedDate;
	
	public String getMsDeleteStatus() {
		return msDeleteStatus;
	}

	public void setMsDeleteStatus(String msDeleteStatus) {
		this.msDeleteStatus = msDeleteStatus;
	}

	
	public String getMsTaxonomyId() {
		return msTaxonomyId;
	}
	
	public void setMsTaxonomyId(String msTaxonomyId) {
		this.msTaxonomyId = msTaxonomyId;
	}

	
	public String getMsTaxonomyLinkageId() {
		return msTaxonomyLinkageId;
	}

	public void setMsTaxonomyLinkageId(String msTaxonomyLinkageId) {
		this.msTaxonomyLinkageId = msTaxonomyLinkageId;
	}

	/**
	 * @return the msModifyBy
	 */
	public String getMsModifyBy() {
		return msModifyBy;
	}

	/**
	 * @param msModifyBy the msModifyBy to set
	 */
	public void setMsModifyBy(String msModifyBy) {
		this.msModifyBy = msModifyBy;
	}

	/**
	 * @return the msCreatedBy
	 */
	public String getMsCreatedBy() {
		return msCreatedBy;
	}

	/**
	 * @param msCreatedBy the msCreatedBy to set
	 */
	public void setMsCreatedBy(String msCreatedBy) {
		this.msCreatedBy = msCreatedBy;
	}

	/**
	 * @return the moCreatedDate
	 */
	public Date getMoCreatedDate() {
		return moCreatedDate;
	}

	/**
	 * @param moCreatedDate the moCreatedDate to set
	 */
	public void setMoCreatedDate(Date moCreatedDate) {
		this.moCreatedDate = moCreatedDate;
	}

	/**
	 * @return the moTMModifiedDate
	 */
	public Date getMoTMModifiedDate() {
		return moTMModifiedDate;
	}

	/**
	 * @param moTMModifiedDate the moTMModifiedDate to set
	 */
	public void setMoTMModifiedDate(Date moTMModifiedDate) {
		this.moTMModifiedDate = moTMModifiedDate;
	}

	@Override
	public String toString() {
		return "TaxonomyLinkageBean [msTaxonomyId=" + msTaxonomyId
				+ ", msTaxonomyLinkageId=" + msTaxonomyLinkageId
				+ ", msDeleteStatus=" + msDeleteStatus + ", msModifyBy="
				+ msModifyBy + ", msCreatedBy=" + msCreatedBy
				+ ", moCreatedDate=" + moCreatedDate + ", moTMModifiedDate="
				+ moTMModifiedDate + "]";
	}
}
