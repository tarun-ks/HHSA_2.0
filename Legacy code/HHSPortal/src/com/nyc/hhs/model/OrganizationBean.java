package com.nyc.hhs.model;

import java.util.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the Organization information.
 * 
 */

public class OrganizationBean {

	@Length(max = 20)
	String msOrgId;
	String msOrgType;
	String msOrgStatus;
	String msMissionStatement;
	@Length(max = 100)
	String msOrgLegalName;
	String msOrgActiveFlag;
	String msDunsId;
	Date msOrgCreationDate;
	Date msOrgExpirationDate;
	String msAcctPeriodStrtMonth;
	String msAcctPeriodEndMonth;
	@Length(max = 50)
	String msEinId;
	String msOverridingFlag;
	String msOverridingReason;
	String msEntityTypeId;
	String msEntityTypeOthers;
	String msAlternateName;
	String msSubmittedBy;
	String msCorpStrucId;
	String msEmailAddress;
	String msPhoneNo;
	String msProcStatus;
	Date msSubmissionDate;
	Date msOrgStrtDate;

	String msAddress1;
	String msAddress2;
	String msCity;
	String msState;
	String msBorough;
	String msZipCode;
	String msWebSite;
	String msFaxNumber;
	String msFormName;
	String msFormId;
	String msFormVersion;
	
	
	private String msStatusDescriptionText="";
	private String msStatusReason="";
	private String msNormHouseNumber="";
	private String msStreetNumberText="";
	private String msValCity="";
	private String msValState="";
	private String msValZipCode="";
	private String msValXCoordinate="";
	private String msValYCoordinate="";
	private String msCommDistt="";
	private String msCivilCourtDistt="";
	private String msSchoolDisttName="";
	private String msHealthArea="";
	private String msBuildIdNumber="";
	private String msValBorough="";
	private String msTaxBlock="";
	private String msTaxLot="";
	private String msCongressionalDistrictName="";
	private String msSenatorDistt="";
	private String msAssemblyDistt="";
	private String msCouncilDistt="";
	private String msLowEndCrossStreetNo="";
	private String msHighEndCrossStreetNo="";
	private String msLowEndCrossStreetName="";
	private String msHighEndCrossStreetName="";
	private String msLatitude="";
	private String msLongitude="";
	

	public String getMsFormName() {
		return msFormName;
	}

	public void setMsFormName(String msFormName) {
		this.msFormName = msFormName;
	}

	public String getMsFormId() {
		return msFormId;
	}

	public void setMsFormId(String msFormId) {
		this.msFormId = msFormId;
	}

	public String getMsFormVersion() {
		return msFormVersion;
	}

	public void setMsFormVersion(String msFormVersion) {
		this.msFormVersion = msFormVersion;
	}

	public String getMsOrgId() {
		return msOrgId;
	}

	public void setMsOrgId(String msOrgId) {
		this.msOrgId = msOrgId;
	}

	public String getMsOrgType() {
		return msOrgType;
	}

	public void setMsOrgType(String msOrgType) {
		this.msOrgType = msOrgType;
	}

	public String getMsOrgStatus() {
		return msOrgStatus;
	}

	public void setMsOrgStatus(String msOrgStatus) {
		this.msOrgStatus = msOrgStatus;
	}

	public String getMsMissionStatement() {
		return msMissionStatement;
	}

	public void setMsMissionStatement(String msMissionStatement) {
		this.msMissionStatement = msMissionStatement;
	}

	public String getMsOrgLegalName() {
		return msOrgLegalName;
	}

	public void setMsOrgLegalName(String msOrgLegalName) {
		this.msOrgLegalName = msOrgLegalName;
	}

	public String getMsOrgActiveFlag() {
		return msOrgActiveFlag;
	}

	public void setMsOrgActiveFlag(String msOrgActiveFlag) {
		this.msOrgActiveFlag = msOrgActiveFlag;
	}

	public String getMsDunsId() {
		return msDunsId;
	}

	public void setMsDunsId(String msDunsId) {
		this.msDunsId = msDunsId;
	}

	public Date getMsOrgCreationDate() {
		return msOrgCreationDate;
	}

	public void setMsOrgCreationDate(Date msOrgCreationDate) {
		this.msOrgCreationDate = msOrgCreationDate;
	}

	public Date getMsOrgExpirationDate() {
		return msOrgExpirationDate;
	}

	public void setMsOrgExpirationDate(Date msOrgExpirationDate) {
		this.msOrgExpirationDate = msOrgExpirationDate;
	}

	public String getMsAcctPeriodStrtMonth() {
		return msAcctPeriodStrtMonth;
	}

	public void setMsAcctPeriodStrtMonth(String msAcctPeriodStrtMonth) {
		this.msAcctPeriodStrtMonth = msAcctPeriodStrtMonth;
	}

	public String getMsAcctPeriodEndMonth() {
		return msAcctPeriodEndMonth;
	}

	public void setMsAcctPeriodEndMonth(String msAcctPeriodEndMonth) {
		this.msAcctPeriodEndMonth = msAcctPeriodEndMonth;
	}

	public String getMsEinId() {
		return msEinId;
	}

	public void setMsEinId(String msEinId) {
		this.msEinId = msEinId;
	}

	public String getMsOverridingFlag() {
		return msOverridingFlag;
	}

	public void setMsOverridingFlag(String msOverridingFlag) {
		this.msOverridingFlag = msOverridingFlag;
	}

	public String getMsOverridingReason() {
		return msOverridingReason;
	}

	public void setMsOverridingReason(String msOverridingReason) {
		this.msOverridingReason = msOverridingReason;
	}

	public String getMsEntityTypeId() {
		return msEntityTypeId;
	}

	public void setMsEntityTypeId(String msEntityTypeId) {
		this.msEntityTypeId = msEntityTypeId;
	}

	public String getMsAlternateName() {
		return msAlternateName;
	}

	public void setMsAlternateName(String msAlternateName) {
		this.msAlternateName = msAlternateName;
	}

	public String getMsSubmittedBy() {
		return msSubmittedBy;
	}

	public void setMsSubmittedBy(String msSubmittedBy) {
		this.msSubmittedBy = msSubmittedBy;
	}

	public String getMsCorpStrucId() {
		return msCorpStrucId;
	}

	public void setMsCorpStrucId(String msCorpStrucId) {
		this.msCorpStrucId = msCorpStrucId;
	}

	public String getMsEmailAddress() {
		return msEmailAddress;
	}

	public void setMsEmailAddress(String msEmailAddress) {
		this.msEmailAddress = msEmailAddress;
	}

	public String getMsPhoneNo() {
		return msPhoneNo;
	}

	public void setMsPhoneNo(String msPhoneNo) {
		this.msPhoneNo = msPhoneNo;
	}

	public String getMsProcStatus() {
		return msProcStatus;
	}

	public void setMsProcStatus(String msProcStatus) {
		this.msProcStatus = msProcStatus;
	}

	public Date getMsSubmissionDate() {
		return msSubmissionDate;
	}

	public void setMsSubmissionDate(Date msSubmissionDate) {
		this.msSubmissionDate = msSubmissionDate;
	}

	public Date getMsOrgStrtDate() {
		return msOrgStrtDate;
	}

	public void setMsOrgStrtDate(Date msOrgStrtDate) {
		this.msOrgStrtDate = msOrgStrtDate;
	}

	public String getMsAddress1() {
		return msAddress1;
	}

	public void setMsAddress1(String msAddress1) {
		this.msAddress1 = msAddress1;
	}

	public String getMsAddress2() {
		return msAddress2;
	}

	public void setMsAddress2(String msAddress2) {
		this.msAddress2 = msAddress2;
	}

	public String getMsCity() {
		return msCity;
	}

	public void setMsCity(String msCity) {
		this.msCity = msCity;
	}

	public String getMsState() {
		return msState;
	}

	public void setMsState(String msState) {
		this.msState = msState;
	}

	public String getMsBorough() {
		return msBorough;
	}

	public void setMsBorough(String msBorough) {
		this.msBorough = msBorough;
	}

	public String getMsZipCode() {
		return msZipCode;
	}

	public void setMsZipCode(String msZipCode) {
		this.msZipCode = msZipCode;
	}

	public String getMsWebSite() {
		return msWebSite;
	}

	public void setMsWebSite(String msWebSite) {
		this.msWebSite = msWebSite;
	}

	public String getMsFaxNumber() {
		return msFaxNumber;
	}

	public void setMsFaxNumber(String msFaxNumber) {
		this.msFaxNumber = msFaxNumber;
	}

	/**
	 * @return the msEntityTypeOthers
	 */
	public String getMsEntityTypeOthers() {
		return msEntityTypeOthers;
	}

	/**
	 * @param msEntityTypeOthers the msEntityTypeOthers to set
	 */
	public void setMsEntityTypeOthers(String msEntityTypeOthers) {
		this.msEntityTypeOthers = msEntityTypeOthers;
	}

	/**
	 * @return the msStatusDescriptionText
	 */
	public String getMsStatusDescriptionText() {
		return msStatusDescriptionText;
	}

	/**
	 * @param msStatusDescriptionText the msStatusDescriptionText to set
	 */
	public void setMsStatusDescriptionText(String msStatusDescriptionText) {
		this.msStatusDescriptionText = msStatusDescriptionText;
	}

	/**
	 * @return the msStatusReason
	 */
	public String getMsStatusReason() {
		return msStatusReason;
	}

	/**
	 * @param msStatusReason the msStatusReason to set
	 */
	public void setMsStatusReason(String msStatusReason) {
		this.msStatusReason = msStatusReason;
	}

	/**
	 * @return the msNormHouseNumber
	 */
	public String getMsNormHouseNumber() {
		return msNormHouseNumber;
	}

	/**
	 * @param msNormHouseNumber the msNormHouseNumber to set
	 */
	public void setMsNormHouseNumber(String msNormHouseNumber) {
		this.msNormHouseNumber = msNormHouseNumber;
	}

	/**
	 * @return the msStreetNumberText
	 */
	public String getMsStreetNumberText() {
		return msStreetNumberText;
	}

	/**
	 * @param msStreetNumberText the msStreetNumberText to set
	 */
	public void setMsStreetNumberText(String msStreetNumberText) {
		this.msStreetNumberText = msStreetNumberText;
	}

	/**
	 * @return the msValCity
	 */
	public String getMsValCity() {
		return msValCity;
	}

	/**
	 * @param msValCity the msValCity to set
	 */
	public void setMsValCity(String msValCity) {
		this.msValCity = msValCity;
	}

	/**
	 * @return the msValState
	 */
	public String getMsValState() {
		return msValState;
	}

	/**
	 * @param msValState the msValState to set
	 */
	public void setMsValState(String msValState) {
		this.msValState = msValState;
	}

	/**
	 * @return the msValZipCode
	 */
	public String getMsValZipCode() {
		return msValZipCode;
	}

	/**
	 * @param msValZipCode the msValZipCode to set
	 */
	public void setMsValZipCode(String msValZipCode) {
		this.msValZipCode = msValZipCode;
	}

	/**
	 * @return the msValXCoordinate
	 */
	public String getMsValXCoordinate() {
		return msValXCoordinate;
	}

	/**
	 * @param msValXCoordinate the msValXCoordinate to set
	 */
	public void setMsValXCoordinate(String msValXCoordinate) {
		this.msValXCoordinate = msValXCoordinate;
	}

	/**
	 * @return the msValYCoordinate
	 */
	public String getMsValYCoordinate() {
		return msValYCoordinate;
	}

	/**
	 * @param msValYCoordinate the msValYCoordinate to set
	 */
	public void setMsValYCoordinate(String msValYCoordinate) {
		this.msValYCoordinate = msValYCoordinate;
	}

	/**
	 * @return the msCommDistt
	 */
	public String getMsCommDistt() {
		return msCommDistt;
	}

	/**
	 * @param msCommDistt the msCommDistt to set
	 */
	public void setMsCommDistt(String msCommDistt) {
		this.msCommDistt = msCommDistt;
	}

	/**
	 * @return the msCivilCourtDistt
	 */
	public String getMsCivilCourtDistt() {
		return msCivilCourtDistt;
	}

	/**
	 * @param msCivilCourtDistt the msCivilCourtDistt to set
	 */
	public void setMsCivilCourtDistt(String msCivilCourtDistt) {
		this.msCivilCourtDistt = msCivilCourtDistt;
	}

	/**
	 * @return the msSchoolDisttName
	 */
	public String getMsSchoolDisttName() {
		return msSchoolDisttName;
	}

	/**
	 * @param msSchoolDisttName the msSchoolDisttName to set
	 */
	public void setMsSchoolDisttName(String msSchoolDisttName) {
		this.msSchoolDisttName = msSchoolDisttName;
	}

	/**
	 * @return the msHealthArea
	 */
	public String getMsHealthArea() {
		return msHealthArea;
	}

	/**
	 * @param msHealthArea the msHealthArea to set
	 */
	public void setMsHealthArea(String msHealthArea) {
		this.msHealthArea = msHealthArea;
	}

	/**
	 * @return the msBuildIdNumber
	 */
	public String getMsBuildIdNumber() {
		return msBuildIdNumber;
	}

	/**
	 * @param msBuildIdNumber the msBuildIdNumber to set
	 */
	public void setMsBuildIdNumber(String msBuildIdNumber) {
		this.msBuildIdNumber = msBuildIdNumber;
	}

	/**
	 * @return the msValBorough
	 */
	public String getMsValBorough() {
		return msValBorough;
	}

	/**
	 * @param msValBorough the msValBorough to set
	 */
	public void setMsValBorough(String msValBorough) {
		this.msValBorough = msValBorough;
	}

	/**
	 * @return the msTaxBlock
	 */
	public String getMsTaxBlock() {
		return msTaxBlock;
	}

	/**
	 * @param msTaxBlock the msTaxBlock to set
	 */
	public void setMsTaxBlock(String msTaxBlock) {
		this.msTaxBlock = msTaxBlock;
	}

	/**
	 * @return the msTaxLot
	 */
	public String getMsTaxLot() {
		return msTaxLot;
	}

	/**
	 * @param msTaxLot the msTaxLot to set
	 */
	public void setMsTaxLot(String msTaxLot) {
		this.msTaxLot = msTaxLot;
	}

	/**
	 * @return the msCongressionalDistrictName
	 */
	public String getMsCongressionalDistrictName() {
		return msCongressionalDistrictName;
	}

	/**
	 * @param msCongressionalDistrictName the msCongressionalDistrictName to set
	 */
	public void setMsCongressionalDistrictName(String msCongressionalDistrictName) {
		this.msCongressionalDistrictName = msCongressionalDistrictName;
	}

	/**
	 * @return the msSenatorDistt
	 */
	public String getMsSenatorDistt() {
		return msSenatorDistt;
	}

	/**
	 * @param msSenatorDistt the msSenatorDistt to set
	 */
	public void setMsSenatorDistt(String msSenatorDistt) {
		this.msSenatorDistt = msSenatorDistt;
	}

	/**
	 * @return the msAssemblyDistt
	 */
	public String getMsAssemblyDistt() {
		return msAssemblyDistt;
	}

	/**
	 * @param msAssemblyDistt the msAssemblyDistt to set
	 */
	public void setMsAssemblyDistt(String msAssemblyDistt) {
		this.msAssemblyDistt = msAssemblyDistt;
	}

	/**
	 * @return the msCouncilDistt
	 */
	public String getMsCouncilDistt() {
		return msCouncilDistt;
	}

	/**
	 * @param msCouncilDistt the msCouncilDistt to set
	 */
	public void setMsCouncilDistt(String msCouncilDistt) {
		this.msCouncilDistt = msCouncilDistt;
	}

	/**
	 * @return the msLowEndCrossStreetNo
	 */
	public String getMsLowEndCrossStreetNo() {
		return msLowEndCrossStreetNo;
	}

	/**
	 * @param msLowEndCrossStreetNo the msLowEndCrossStreetNo to set
	 */
	public void setMsLowEndCrossStreetNo(String msLowEndCrossStreetNo) {
		this.msLowEndCrossStreetNo = msLowEndCrossStreetNo;
	}

	/**
	 * @return the msHighEndCrossStreetNo
	 */
	public String getMsHighEndCrossStreetNo() {
		return msHighEndCrossStreetNo;
	}

	/**
	 * @param msHighEndCrossStreetNo the msHighEndCrossStreetNo to set
	 */
	public void setMsHighEndCrossStreetNo(String msHighEndCrossStreetNo) {
		this.msHighEndCrossStreetNo = msHighEndCrossStreetNo;
	}

	/**
	 * @return the msLowEndCrossStreetName
	 */
	public String getMsLowEndCrossStreetName() {
		return msLowEndCrossStreetName;
	}

	/**
	 * @param msLowEndCrossStreetName the msLowEndCrossStreetName to set
	 */
	public void setMsLowEndCrossStreetName(String msLowEndCrossStreetName) {
		this.msLowEndCrossStreetName = msLowEndCrossStreetName;
	}

	/**
	 * @return the msHighEndCrossStreetName
	 */
	public String getMsHighEndCrossStreetName() {
		return msHighEndCrossStreetName;
	}

	/**
	 * @param msHighEndCrossStreetName the msHighEndCrossStreetName to set
	 */
	public void setMsHighEndCrossStreetName(String msHighEndCrossStreetName) {
		this.msHighEndCrossStreetName = msHighEndCrossStreetName;
	}

	/**
	 * @return the msLatitude
	 */
	public String getMsLatitude() {
		return msLatitude;
	}

	/**
	 * @param msLatitude the msLatitude to set
	 */
	public void setMsLatitude(String msLatitude) {
		this.msLatitude = msLatitude;
	}

	/**
	 * @return the msLongitude
	 */
	public String getMsLongitude() {
		return msLongitude;
	}

	/**
	 * @param msLongitude the msLongitude to set
	 */
	public void setMsLongitude(String msLongitude) {
		this.msLongitude = msLongitude;
	}

	@Override
	public String toString() {
		return "OrganizationBean [msOrgId=" + msOrgId + ", msOrgType="
				+ msOrgType + ", msOrgStatus=" + msOrgStatus
				+ ", msMissionStatement=" + msMissionStatement
				+ ", msOrgLegalName=" + msOrgLegalName + ", msOrgActiveFlag="
				+ msOrgActiveFlag + ", msDunsId=" + msDunsId
				+ ", msOrgCreationDate=" + msOrgCreationDate
				+ ", msOrgExpirationDate=" + msOrgExpirationDate
				+ ", msAcctPeriodStrtMonth=" + msAcctPeriodStrtMonth
				+ ", msAcctPeriodEndMonth=" + msAcctPeriodEndMonth
				+ ", msEinId=" + msEinId + ", msOverridingFlag="
				+ msOverridingFlag + ", msOverridingReason="
				+ msOverridingReason + ", msEntityTypeId=" + msEntityTypeId
				+ ", msEntityTypeOthers=" + msEntityTypeOthers
				+ ", msAlternateName=" + msAlternateName + ", msSubmittedBy="
				+ msSubmittedBy + ", msCorpStrucId=" + msCorpStrucId
				+ ", msEmailAddress=" + msEmailAddress + ", msPhoneNo="
				+ msPhoneNo + ", msProcStatus=" + msProcStatus
				+ ", msSubmissionDate=" + msSubmissionDate + ", msOrgStrtDate="
				+ msOrgStrtDate + ", msAddress1=" + msAddress1
				+ ", msAddress2=" + msAddress2 + ", msCity=" + msCity
				+ ", msState=" + msState + ", msBorough=" + msBorough
				+ ", msZipCode=" + msZipCode + ", msWebSite=" + msWebSite
				+ ", msFaxNumber=" + msFaxNumber + ", msFormName=" + msFormName
				+ ", msFormId=" + msFormId + ", msFormVersion=" + msFormVersion
				+ ", msStatusDescriptionText=" + msStatusDescriptionText
				+ ", msStatusReason=" + msStatusReason + ", msNormHouseNumber="
				+ msNormHouseNumber + ", msStreetNumberText="
				+ msStreetNumberText + ", msValCity=" + msValCity
				+ ", msValState=" + msValState + ", msValZipCode="
				+ msValZipCode + ", msValXCoordinate=" + msValXCoordinate
				+ ", msValYCoordinate=" + msValYCoordinate + ", msCommDistt="
				+ msCommDistt + ", msCivilCourtDistt=" + msCivilCourtDistt
				+ ", msSchoolDisttName=" + msSchoolDisttName
				+ ", msHealthArea=" + msHealthArea + ", msBuildIdNumber="
				+ msBuildIdNumber + ", msValBorough=" + msValBorough
				+ ", msTaxBlock=" + msTaxBlock + ", msTaxLot=" + msTaxLot
				+ ", msCongressionalDistrictName="
				+ msCongressionalDistrictName + ", msSenatorDistt="
				+ msSenatorDistt + ", msAssemblyDistt=" + msAssemblyDistt
				+ ", msCouncilDistt=" + msCouncilDistt
				+ ", msLowEndCrossStreetNo=" + msLowEndCrossStreetNo
				+ ", msHighEndCrossStreetNo=" + msHighEndCrossStreetNo
				+ ", msLowEndCrossStreetName=" + msLowEndCrossStreetName
				+ ", msHighEndCrossStreetName=" + msHighEndCrossStreetName
				+ ", msLatitude=" + msLatitude + ", msLongitude=" + msLongitude
				+ "]";
	}

}
