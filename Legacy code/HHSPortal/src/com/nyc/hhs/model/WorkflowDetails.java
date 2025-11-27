package com.nyc.hhs.model;

import java.util.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the Work-flow details.
 *
 */

public class WorkflowDetails {

	private String msApplicationId;
	private String msDocumentId;
	private String msDocumentCategory;
	private String msDocumentType;
	private Date msEffectiveDate;
	private String msCurrentStatus;
	private String msDocumentName;
	private String msModifiedBy;
	private Date msModifiedDate;
	private String msProvidername;
	private String msSubmittedBy;
	@Length(max = 128)
	private String msEmailAddress;
	private String msPhone;
	private String moDateSumitted;
	@Length(max = 50)
	private String msProviderSatus;
	private int miNoOfServices;
	private String msBusinessAppStatus;


	public WorkflowDetails() {
		//No Action Required
	}

	public String getMsBusinessAppStatus() {
		return msBusinessAppStatus;
	}

	public void setMsBusinessAppStatus(String msBusinessAppStatus) {
		this.msBusinessAppStatus = msBusinessAppStatus;
	}

	public String getMsProvidername() {
		return msProvidername;
	}

	public void setMsProvidername(String msProvidername) {
		this.msProvidername = msProvidername;
	}

	public String getMsSubmittedBy() {
		return msSubmittedBy;
	}

	public void setMsSubmittedBy(String msSubmittedBy) {
		this.msSubmittedBy = msSubmittedBy;
	}

	public String getMsEmailAddress() {
		return msEmailAddress;
	}

	public void setMsEmailAddress(String msEmailAddress) {
		this.msEmailAddress = msEmailAddress;
	}

	public String getMsPhone() {
		return msPhone;
	}

	public void setMsPhone(String msPhone) {
		this.msPhone = msPhone;
	}

	public String getMoDateSumitted() {
		return moDateSumitted;
	}

	public void setMoDateSumitted(String moDateSumitted) {
		this.moDateSumitted = moDateSumitted;
	}

	public String getMsProviderSatus() {
		return msProviderSatus;
	}

	public void setMsProviderSatus(String msProviderSatus) {
		this.msProviderSatus = msProviderSatus;
	}

	public int getMiNoOfServices() {
		return miNoOfServices;
	}

	public void setMiNoOfServices(int miNoOfServices) {
		this.miNoOfServices = miNoOfServices;
	}

	public String getMsModifiedBy() {
		return msModifiedBy;
	}

	public void setMsModifiedBy(String msModifiedBy) {
		this.msModifiedBy = msModifiedBy;
	}

	public Date getMsModifiedDate() {
		return msModifiedDate;
	}

	public void setMsModifiedDate(Date msModifiedDate) {
		this.msModifiedDate = msModifiedDate;
	}

	public String getMsDocumentName() {
		return msDocumentName;
	}

	public void setMsDocumentName(String msDocumentName) {
		this.msDocumentName = msDocumentName;
	}

	public String getMsCurrentStatus() {
		return msCurrentStatus;
	}

	public void setMsCurrentStatus(String msCurrentStatus) {
		this.msCurrentStatus = msCurrentStatus;
	}



	public String getMsDocumentId() {
		return msDocumentId;
	}

	public void setMsDocumentId(String msDocumentId) {
		this.msDocumentId = msDocumentId;
	}

	public String getMsDocumentCategory() {
		return msDocumentCategory;
	}

	public void setMsDocumentCategory(String msDocumentCategory) {
		this.msDocumentCategory = msDocumentCategory;
	}

	public String getMsDocumentType() {
		return msDocumentType;
	}

	public void setMsDocumentType(String msDocumentType) {
		this.msDocumentType = msDocumentType;
	}


	public String getMsApplicationId() {
		return msApplicationId;
	}


	public void setMsApplicationId(String msApplicationId) {
		this.msApplicationId = msApplicationId;
	}

	public Date getMsEffectiveDate() {
		return msEffectiveDate;
	}

	public void setMsEffectiveDate(Date msEffectiveDate) {
		this.msEffectiveDate = msEffectiveDate;
	}

	@Override
	public String toString() {
		return "WorkflowDetails [msApplicationId=" + msApplicationId
		+ ", msDocumentId=" + msDocumentId + ", msDocumentCategory="
		+ msDocumentCategory + ", msDocumentType=" + msDocumentType
		+ ", msEffectiveDate=" + msEffectiveDate + ", msCurrentStatus="
		+ msCurrentStatus + ", msDocumentName=" + msDocumentName
		+ ", msModifiedBy=" + msModifiedBy + ", msModifiedDate="
		+ msModifiedDate + ", msProvidername=" + msProvidername
		+ ", msSubmittedBy=" + msSubmittedBy + ", msEmailAddress="
		+ msEmailAddress + ", msPhone=" + msPhone + ", moDateSumitted="
		+ moDateSumitted + ", msProviderSatus=" + msProviderSatus
		+ ", miNoOfServices=" + miNoOfServices
		+ ", msBusinessAppStatus=" + msBusinessAppStatus + "]";
	}

}
