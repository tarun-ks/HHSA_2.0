package com.nyc.hhs.model;

import java.sql.Date;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This class is a bean which maintains the Taxonomy Synonym information.
 *
 */

public class TaxonomySynonymBean {

	@Length(max = 50)
	private String msServiceElementIdFk;
	@Length(max = 500)
	private String msTaxonomySyn;	
	private String msDeleteStatus;
	
	private String msModifyBy;
	private String msCreatedBy;
	private Date moCreatedDate;
	private Date moTMModifiedDate;
	
	
	public String getMsServiceElementIdFk() {
		return msServiceElementIdFk;
	}
	
	public void setMsServiceElementIdFk(String msServiceElementIdFk) {
		this.msServiceElementIdFk = msServiceElementIdFk;
	}
	
	
	public String getMsDeleteStatus() {
		return msDeleteStatus;
	}
	
	public void setMsDeleteStatus(String msDeleteStatus) {
		this.msDeleteStatus = msDeleteStatus;
	}
	
	
	public String getMsTaxonomySyn() {
		return msTaxonomySyn;
	}
	
	public void setMsTaxonomySyn(String msTaxonomySyn) {
		this.msTaxonomySyn = msTaxonomySyn;
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
		return "TaxonomySynonymBean [msServiceElementIdFk="
				+ msServiceElementIdFk + ", msTaxonomySyn=" + msTaxonomySyn
				+ ", msDeleteStatus=" + msDeleteStatus + ", msModifyBy="
				+ msModifyBy + ", msCreatedBy=" + msCreatedBy
				+ ", moCreatedDate=" + moCreatedDate + ", moTMModifiedDate="
				+ moTMModifiedDate + "]";
	}

}
