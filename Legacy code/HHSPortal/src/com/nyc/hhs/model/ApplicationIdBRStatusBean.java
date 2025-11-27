package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class ApplicationIdBRStatusBean {

	@Length(max = 20) 
	private String msApplicationId;	
	private String msApplicationStatus;	
	private String msProcApplicationStatus;
	private String msStatusId;
	private String msWorkflowId;
	private String msProviderId;
	private String miNumDays;
	private String mdStartDate;
	private String mdCreatedDate;
	private String msCreatedBy;
	private String mdSubmissionDate;
	private String msSubmittedBy;
	private String msUserId;
	private String mdCityStatusSetDate;
	private String mdModifiedDate;
	private String msModifiedBy;
	private String msCityStatusSetBy;
	private String mdExpiryDate;
	
	
	public String getMsApplicationId() {
		return msApplicationId;
	}



	public void setMsApplicationId(String msApplicationId) {
		this.msApplicationId = msApplicationId;
	}



	public String getMsApplicationStatus() {
		return msApplicationStatus;
	}



	public void setMsApplicationStatus(String msApplicationStatus) {
		this.msApplicationStatus = msApplicationStatus;
	}



	public String getMsProcApplicationStatus() {
		return msProcApplicationStatus;
	}



	public void setMsProcApplicationStatus(String msProcApplicationStatus) {
		this.msProcApplicationStatus = msProcApplicationStatus;
	}



	public String getMsStatusId() {
		return msStatusId;
	}



	public void setMsStatusId(String msStatusId) {
		this.msStatusId = msStatusId;
	}


	
	public String getMsWorkflowId() {
		return msWorkflowId;
	}



	public void setMsWorkflowId(String msWorkflowId) {
		this.msWorkflowId = msWorkflowId;
	}



	public String getMsProviderId() {
		return msProviderId;
	}



	public void setMsProviderId(String msProviderId) {
		this.msProviderId = msProviderId;
	}



	public String getMiNumDays() {
		return miNumDays;
	}



	public void setMiNumDays(String miNumDays) {
		this.miNumDays = miNumDays;
	}



	public String getMdStartDate() {
		return mdStartDate;
	}



	public void setMdStartDate(String mdStartDate) {
		this.mdStartDate = mdStartDate;
	}



	public String getMdCreatedDate() {
		return mdCreatedDate;
	}



	public void setMdCreatedDate(String mdCreatedDate) {
		this.mdCreatedDate = mdCreatedDate;
	}



	public String getMsCreatedBy() {
		return msCreatedBy;
	}



	public void setMsCreatedBy(String msCreatedBy) {
		this.msCreatedBy = msCreatedBy;
	}



	public String getMdSubmissionDate() {
		return mdSubmissionDate;
	}



	public void setMdSubmissionDate(String mdSubmissionDate) {
		this.mdSubmissionDate = mdSubmissionDate;
	}



	public String getMsSubmittedBy() {
		return msSubmittedBy;
	}



	public void setMsSubmittedBy(String msSubmittedBy) {
		this.msSubmittedBy = msSubmittedBy;
	}



	public String getMsUserId() {
		return msUserId;
	}



	public void setMsUserId(String msUserId) {
		this.msUserId = msUserId;
	}



	public String getMdCityStatusSetDate() {
		return mdCityStatusSetDate;
	}



	public void setMdCityStatusSetDate(String mdCityStatusSetDate) {
		this.mdCityStatusSetDate = mdCityStatusSetDate;
	}



	public String getMdModifiedDate() {
		return mdModifiedDate;
	}



	public void setMdModifiedDate(String mdModifiedDate) {
		this.mdModifiedDate = mdModifiedDate;
	}



	public String getMsModifiedBy() {
		return msModifiedBy;
	}



	public void setMsModifiedBy(String msModifiedBy) {
		this.msModifiedBy = msModifiedBy;
	}



	public String getMsCityStatusSetdBy() {
		return msCityStatusSetBy;
	}



	public void setMsCityStatusSetdBy(String msCityStatusSetdBy) {
		this.msCityStatusSetBy = msCityStatusSetBy;
	}



	public String getMdExpiryDate() {
		return mdExpiryDate;
	}



	public void setMdExpiryDate(String mdExpiryDate) {
		this.mdExpiryDate = mdExpiryDate;
	}



	
	@Override
	public String toString() {
		return "ApplicationExpiryRuleBean [msApplicationId=" + msApplicationId
				+ ", msApplicationStatus=" + msApplicationStatus
				+ ", msProcApplicationStatus=" + msProcApplicationStatus
				+ ", msStatusId=" + msStatusId
				+ ", msWorkflowId=" + msWorkflowId
				+ ", msProviderId=" + msProviderId 
				+ ", miNumDays=" + miNumDays
				+ ", mdStartDate=" + mdStartDate 
				+ ", mdExpiryDate="	+ mdExpiryDate
				+ ", mdCreatedDate="	+ mdCreatedDate
				+ ", msCreatedBy"	+  msCreatedBy
				+ ", mdModifiedDate="	+ mdModifiedDate
				+ ", msModifiedBy"	+  msModifiedBy
				+ ", mdSubmissionDate="	+ mdSubmissionDate
				+ ", msSubmittedBy="	+ msSubmittedBy
				+ ", mdCityStatusSetDate"	+ mdCityStatusSetDate
				+ ", msCityStatusSetdBy"	+  msCityStatusSetBy
				+ ", msUserId"	+  msUserId
				+ "]";
	}

}
