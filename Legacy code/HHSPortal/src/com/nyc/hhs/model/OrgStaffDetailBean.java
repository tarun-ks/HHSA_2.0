package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the Organization Staff Details.
 *
 */

public class OrgStaffDetailBean {
	
	@Length(max = 50)
	private String msOrgEinTinNumber = "";
	@Length(max = 100)
	private String msOrgLegalName ="";
	@Length(max = 50)
	private String msOrgCorpStructure="";
	private String msOrgEntityType="";
	private String msOrgEntityTypeOther="";
	private String msOrgDunsNumber="";
	private String msOrgDoingBusAs="";
	private String msOrgAcctPeriodStart="";
	private String msOrgAcctPeriodEnd="";
	

	private String msExecAddrLine1="";
	private String msExecAddrLine2="";
	private String msExecCity="";
	private String msExecBorough="";
	private String msExecState="";
	private String msExecZipCode="";
	private String msExecPhoneNo="";
	private String msExecFaxNo="";
	private String msExecWebSite="";
	

	private String msAdminNYCId="";
	private String msAdminUserDN="";
	private String msTitle="";		
	private String msFirstName="";
	private String msMiddleInitial="";
	private String msLastName="";
	private String msPhoneNo="";
	private String msEmailAdd="";
	
	
	public String getMsOrgEinTinNumber() {
		return msOrgEinTinNumber;
	}
	
	public void setMsOrgEinTinNumber(String msOrgEinTinNumber) {
		this.msOrgEinTinNumber = msOrgEinTinNumber;
	}
	
	public String getMsOrgLegalName() {
		return msOrgLegalName;
	}
	
	public void setMsOrgLegalName(String msOrgLegalName) {
		this.msOrgLegalName = msOrgLegalName;
	}
	
	public String getMsOrgCorpStructure() {
		return msOrgCorpStructure;
	}
	
	public void setMsOrgCorpStructure(String msOrgCorpStructure) {
		this.msOrgCorpStructure = msOrgCorpStructure;
	}
	
	public String getMsOrgEntityType() {
		return msOrgEntityType;
	}
	
	public void setMsOrgEntityType(String msOrgEntityType) {
		this.msOrgEntityType = msOrgEntityType;
	}
	
	public String getMsOrgEntityTypeOther() {
		return msOrgEntityTypeOther;
	}
	
	public void setMsOrgEntityTypeOther(String msOrgEntityTypeOther) {
		this.msOrgEntityTypeOther = msOrgEntityTypeOther;
	}
	
	public String getMsOrgDunsNumber() {
		return msOrgDunsNumber;
	}
	
	public void setMsOrgDunsNumber(String msOrgDunsNumber) {
		this.msOrgDunsNumber = msOrgDunsNumber;
	}
	
	public String getMsOrgDoingBusAs() {
		return msOrgDoingBusAs;
	}
	
	public void setMsOrgDoingBusAs(String msOrgDoingBusAs) {
		this.msOrgDoingBusAs = msOrgDoingBusAs;
	}
	
	public String getMsOrgAcctPeriodStart() {
		return msOrgAcctPeriodStart;
	}
	
	public void setMsOrgAcctPeriodStart(String msOrgAcctPeriodStart) {
		this.msOrgAcctPeriodStart = msOrgAcctPeriodStart;
	}
	
	public String getMsOrgAcctPeriodEnd() {
		return msOrgAcctPeriodEnd;
	}
	
	public void setMsOrgAcctPeriodEnd(String msOrgAcctPeriodEnd) {
		this.msOrgAcctPeriodEnd = msOrgAcctPeriodEnd;
	}
	
	public String getMsExecAddrLine1() {
		return msExecAddrLine1;
	}
	
	public void setMsExecAddrLine1(String msExecAddrLine1) {
		this.msExecAddrLine1 = msExecAddrLine1;
	}
	
	public String getMsExecAddrLine2() {
		return msExecAddrLine2;
	}
	
	public void setMsExecAddrLine2(String msExecAddrLine2) {
		this.msExecAddrLine2 = msExecAddrLine2;
	}
	
	public String getMsExecCity() {
		return msExecCity;
	}
	
	public void setMsExecCity(String msExecCity) {
		this.msExecCity = msExecCity;
	}
	
	public String getMsExecBorough() {
		return msExecBorough;
	}
	
	public void setMsExecBorough(String msExecBorough) {
		this.msExecBorough = msExecBorough;
	}
	
	public String getMsExecState() {
		return msExecState;
	}
	
	public void setMsExecState(String msExecState) {
		this.msExecState = msExecState;
	}
	
	public String getMsExecZipCode() {
		return msExecZipCode;
	}
	
	public void setMsExecZipCode(String msExecZipCode) {
		this.msExecZipCode = msExecZipCode;
	}
	
	public String getMsExecPhoneNo() {
		return msExecPhoneNo;
	}
	
	public void setMsExecPhoneNo(String msExecPhoneNo) {
		this.msExecPhoneNo = msExecPhoneNo;
	}
	
	public String getMsExecFaxNo() {
		return msExecFaxNo;
	}
	
	public void setMsExecFaxNo(String msExecFaxNo) {
		this.msExecFaxNo = msExecFaxNo;
	}
	
	public String getMsExecWebSite() {
		return msExecWebSite;
	}
	
	public void setMsExecWebSite(String msExecWebSite) {
		this.msExecWebSite = msExecWebSite;
	}

	public String getMsTitle() {
		return msTitle;
	}
	
	public void setMsTitle(String msTitle) {
		this.msTitle = msTitle;
	}
	
	public String getMsFirstName() {
		return msFirstName;
	}
	
	public void setMsFirstName(String msFirstName) {
		this.msFirstName = msFirstName;
	}
	
	public String getMsMiddleInitial() {
		return msMiddleInitial;
	}
	
	public void setMsMiddleInitial(String msMiddleInitial) {
		this.msMiddleInitial = msMiddleInitial;
	}
	
	public String getMsLastName() {
		return msLastName;
	}
	
	public void setMsLastName(String msLastName) {
		this.msLastName = msLastName;
	}
	
	public String getMsPhoneNo() {
		return msPhoneNo;
	}
	
	public void setMsPhoneNo(String msPhoneNo) {
		this.msPhoneNo = msPhoneNo;
	}
	
	public String getMsEmailAdd() {
		return msEmailAdd;
	}
	
	public void setMsEmailAdd(String msEmailAdd) {
		this.msEmailAdd = msEmailAdd;
	}
	
	public String getMsAdminNYCId() {
		return msAdminNYCId;
	}
	
	public void setMsAdminNYCId(String msAdminNYCId) {
		this.msAdminNYCId = msAdminNYCId;
	}
	
	public String getMsAdminUserDN() {
		return msAdminUserDN;
	}
	
	public void setMsAdminUserDN(String msAdminUserDN) {
		this.msAdminUserDN = msAdminUserDN;
	}

	@Override
	public String toString() {
		return "OrgStaffDetailBean [msOrgEinTinNumber=" + msOrgEinTinNumber
				+ ", msOrgLegalName=" + msOrgLegalName
				+ ", msOrgCorpStructure=" + msOrgCorpStructure
				+ ", msOrgEntityType=" + msOrgEntityType
				+ ", msOrgEntityTypeOther=" + msOrgEntityTypeOther
				+ ", msOrgDunsNumber=" + msOrgDunsNumber + ", msOrgDoingBusAs="
				+ msOrgDoingBusAs + ", msOrgAcctPeriodStart="
				+ msOrgAcctPeriodStart + ", msOrgAcctPeriodEnd="
				+ msOrgAcctPeriodEnd + ", msExecAddrLine1=" + msExecAddrLine1
				+ ", msExecAddrLine2=" + msExecAddrLine2 + ", msExecCity="
				+ msExecCity + ", msExecBorough=" + msExecBorough
				+ ", msExecState=" + msExecState + ", msExecZipCode="
				+ msExecZipCode + ", msExecPhoneNo=" + msExecPhoneNo
				+ ", msExecFaxNo=" + msExecFaxNo + ", msExecWebSite="
				+ msExecWebSite + ", msAdminNYCId=" + msAdminNYCId
				+ ", msAdminUserDN=" + msAdminUserDN + ", msTitle=" + msTitle
				+ ", msFirstName=" + msFirstName + ", msMiddleInitial="
				+ msMiddleInitial + ", msLastName=" + msLastName
				+ ", msPhoneNo=" + msPhoneNo + ", msEmailAdd=" + msEmailAdd
				+ "]";
	}	

}
