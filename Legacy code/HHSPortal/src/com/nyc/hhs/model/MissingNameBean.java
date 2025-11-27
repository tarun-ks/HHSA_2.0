package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the Missing Name information.
 *
 */

public class MissingNameBean {
	
	//Org details
	private String msOrgEinTinNumber = "";
	private String msOrgLegalName ="";
	private String msOrgCorpStructure="";
	private String msOrgEntityType="";
	private String msOrgEntityTypeOther="";
	private String msOrgDunsNumber="";
	private String msOrgDoingBusAs="";
	private String msOrgAcctPeriodStart="";
	private String msOrgAcctPeriodEnd="";
	
	//Exec details
	private String msExecAddrLine1="";
	private String msExecAddrLine2="";
	private String msExecCity="";
	private String msExecBorough="";
	private String msExecState="";
	private String msExecZipCode="";
	private String msExecPhoneNo="";
	private String msExecFaxNo="";
	private String msExecWebSite="";
	
	//Account admin details
	private String msAdminNYCId="";
	private String msAdminFirstName="";
	private String msAdminMiddleInitial="";
	private String msAdminLastName="";
	private String msAdminOfficeTitle="";
	private String msAdminPhoneNo="";
	private String msAdminEmailAdd="";
	
	//Ceo details
	private String msCeoFirstName="";
	private String msCeoMiddleInitial="";
	private String msCeoLastName="";
	private String msCeoPhoneNo="";
	private String msCeoEmailAdd="";
	
	//President details
	private String msPresFirstName="";
	private String msPresMiddleInitial="";
	private String msPresLastName="";
	private String msPresPhoneNo="";
	private String msPresEmailAdd="";
	
	private String msRequestId="";
	
	private String msCfoFirstName="";
	private String msCfoMiddleInitial="";
	private String msCfoLastName="";
	private String msCfoPhoneNo="";
	private String msCfoEmailAdd="";
	private String msFirstName="";
	private String msMiddleName="";
	private String msLastName="";
	private String msUserId="";
	
	private String msStaffDetailTitle="";
	@Length(max = 20)
	private String msOrgId="";
	@Length(max = 50)
	private String msUserDN="";	
	
	private Boolean msWorkFlowRequired=false;
	
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
	private String msBorough="";
	
	public String getMsUserId() {
		return msUserId;
	}

	public void setMsUserId(String msUserId) {
		this.msUserId = msUserId;
	}

	public String getMsFirstName() {
		return msFirstName;
	}

	public void setMsFirstName(String msFirstName) {
		this.msFirstName = msFirstName;
	}

	public String getMsMiddleName() {
		return msMiddleName;
	}

	public void setMsMiddleName(String msMiddleName) {
		this.msMiddleName = msMiddleName;
	}

	public String getMsLastName() {
		return msLastName;
	}

	public void setMsLastName(String msLastName) {
		this.msLastName = msLastName;
	}

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

	public String getMsAdminNYCId() {
		return msAdminNYCId;
	}

	public void setMsAdminNYCId(String msAdminNYCId) {
		this.msAdminNYCId = msAdminNYCId;
	}

	public String getMsAdminFirstName() {
		return msAdminFirstName;
	}

	public void setMsAdminFirstName(String msAdminFirstName) {
		this.msAdminFirstName = msAdminFirstName;
	}

	public String getMsAdminMiddleInitial() {
		return msAdminMiddleInitial;
	}

	public void setMsAdminMiddleInitial(String msAdminMiddleInitial) {
		this.msAdminMiddleInitial = msAdminMiddleInitial;
	}

	public String getMsAdminLastName() {
		return msAdminLastName;
	}

	public void setMsAdminLastName(String msAdminLastName) {
		this.msAdminLastName = msAdminLastName;
	}

	public String getMsAdminOfficeTitle() {
		return msAdminOfficeTitle;
	}

	public void setMsAdminOfficeTitle(String msAdminOfficeTitle) {
		this.msAdminOfficeTitle = msAdminOfficeTitle;
	}

	public String getMsAdminPhoneNo() {
		return msAdminPhoneNo;
	}

	public void setMsAdminPhoneNo(String msAdminPhoneNo) {
		this.msAdminPhoneNo = msAdminPhoneNo;
	}

	public String getMsAdminEmailAdd() {
		return msAdminEmailAdd;
	}

	public void setMsAdminEmailAdd(String msAdminEmailAdd) {
		this.msAdminEmailAdd = msAdminEmailAdd;
	}

	public String getMsCeoFirstName() {
		return msCeoFirstName;
	}

	public void setMsCeoFirstName(String msCeoFirstName) {
		this.msCeoFirstName = msCeoFirstName;
	}

	public String getMsCeoMiddleInitial() {
		return msCeoMiddleInitial;
	}

	public void setMsCeoMiddleInitial(String msCeoMiddleInitial) {
		this.msCeoMiddleInitial = msCeoMiddleInitial;
	}

	public String getMsCeoLastName() {
		return msCeoLastName;
	}

	public void setMsCeoLastName(String msCeoLastName) {
		this.msCeoLastName = msCeoLastName;
	}

	public String getMsCeoPhoneNo() {
		return msCeoPhoneNo;
	}

	public void setMsCeoPhoneNo(String msCeoPhoneNo) {
		this.msCeoPhoneNo = msCeoPhoneNo;
	}

	public String getMsCeoEmailAdd() {
		return msCeoEmailAdd;
	}

	public void setMsCeoEmailAdd(String msCeoEmailAdd) {
		this.msCeoEmailAdd = msCeoEmailAdd;
	}

	public String getMsPresFirstName() {
		return msPresFirstName;
	}

	public void setMsPresFirstName(String msPresFirstName) {
		this.msPresFirstName = msPresFirstName;
	}

	public String getMsPresMiddleInitial() {
		return msPresMiddleInitial;
	}

	public void setMsPresMiddleInitial(String msPresMiddleInitial) {
		this.msPresMiddleInitial = msPresMiddleInitial;
	}

	public String getMsPresLastName() {
		return msPresLastName;
	}

	public void setMsPresLastName(String msPresLastName) {
		this.msPresLastName = msPresLastName;
	}

	public String getMsPresPhoneNo() {
		return msPresPhoneNo;
	}

	public void setMsPresPhoneNo(String msPresPhoneNo) {
		this.msPresPhoneNo = msPresPhoneNo;
	}

	public String getMsPresEmailAdd() {
		return msPresEmailAdd;
	}

	public void setMsPresEmailAdd(String msPresEmailAdd) {
		this.msPresEmailAdd = msPresEmailAdd;
	}

	public String getMsRequestId() {
		return msRequestId;
	}

	public void setMsRequestId(String msRequestId) {
		this.msRequestId = msRequestId;
	}

	public String getMsCfoFirstName() {
		return msCfoFirstName;
	}

	public void setMsCfoFirstName(String msCfoFirstName) {
		this.msCfoFirstName = msCfoFirstName;
	}

	public String getMsCfoMiddleInitial() {
		return msCfoMiddleInitial;
	}

	public void setMsCfoMiddleInitial(String msCfoMiddleInitial) {
		this.msCfoMiddleInitial = msCfoMiddleInitial;
	}

	public String getMsCfoLastName() {
		return msCfoLastName;
	}

	public void setMsCfoLastName(String msCfoLastName) {
		this.msCfoLastName = msCfoLastName;
	}

	public String getMsCfoPhoneNo() {
		return msCfoPhoneNo;
	}

	public void setMsCfoPhoneNo(String msCfoPhoneNo) {
		this.msCfoPhoneNo = msCfoPhoneNo;
	}

	public String getMsCfoEmailAdd() {
		return msCfoEmailAdd;
	}

	public void setMsCfoEmailAdd(String msCfoEmailAdd) {
		this.msCfoEmailAdd = msCfoEmailAdd;
	}

	public String getMsStaffDetailTitle() {
		return msStaffDetailTitle;
	}

	public void setMsStaffDetailTitle(String msStaffDetailTitle) {
		this.msStaffDetailTitle = msStaffDetailTitle;
	}

	public String getMsOrgId() {
		return msOrgId;
	}

	public void setMsOrgId(String msOrgId) {
		this.msOrgId = msOrgId;
	}

	public String getMsUserDN() {
		return msUserDN;
	}

	public void setMsUserDN(String msUserDN) {
		this.msUserDN = msUserDN;
	}

	/**
	 * @return the msWorkFlowRequired
	 */
	public Boolean getMsWorkFlowRequired() {
		return msWorkFlowRequired;
	}

	/**
	 * @param msWorkFlowRequired the msWorkFlowRequired to set
	 */
	public void setMsWorkFlowRequired(Boolean msWorkFlowRequired) {
		this.msWorkFlowRequired = msWorkFlowRequired;
	}
	
	public String getStatusDescriptionText()
	{
		return msStatusDescriptionText;
	}

	public void setStatusDescriptionText(String msStatusDescriptionText)
	{
		this.msStatusDescriptionText = msStatusDescriptionText;
	}

	public String getStatusReason()
	{
		return msStatusReason;
	}

	public void setStatusReason(String msStatusReason)
	{
		this.msStatusReason = msStatusReason;
	}

	public String getStreetNumberText()
	{
		return msStreetNumberText;
	}

	public void setStreetNumberText(String msStreetNumberText)
	{
		this.msStreetNumberText = msStreetNumberText;
	}

	public String getCongressionalDistrictName()
	{
		return msCongressionalDistrictName;
	}

	public void setCongressionalDistrictName(String msCongressionalDistrictName)
	{
		this.msCongressionalDistrictName = msCongressionalDistrictName;
	}

	public String getLatitude()
	{
		return msLatitude;
	}

	public void setLatitude(String msLatitude)
	{
		this.msLatitude = msLatitude;
	}

	public String getLongitude()
	{
		return msLongitude;
	}

	public void setLongitude(String msLongitude)
	{
		this.msLongitude = msLongitude;
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

	/**
	 * @return the msBorough
	 */
	public String getMsBorough() {
		return msBorough;
	}

	/**
	 * @param msBorough the msBorough to set
	 */
	public void setMsBorough(String msBorough) {
		this.msBorough = msBorough;
	}

	@Override
	public String toString() {
		return "MissingNameBean [msOrgEinTinNumber=" + msOrgEinTinNumber
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
				+ ", msAdminFirstName=" + msAdminFirstName
				+ ", msAdminMiddleInitial=" + msAdminMiddleInitial
				+ ", msAdminLastName=" + msAdminLastName
				+ ", msAdminOfficeTitle=" + msAdminOfficeTitle
				+ ", msAdminPhoneNo=" + msAdminPhoneNo + ", msAdminEmailAdd="
				+ msAdminEmailAdd + ", msCeoFirstName=" + msCeoFirstName
				+ ", msCeoMiddleInitial=" + msCeoMiddleInitial
				+ ", msCeoLastName=" + msCeoLastName + ", msCeoPhoneNo="
				+ msCeoPhoneNo + ", msCeoEmailAdd=" + msCeoEmailAdd
				+ ", msPresFirstName=" + msPresFirstName
				+ ", msPresMiddleInitial=" + msPresMiddleInitial
				+ ", msPresLastName=" + msPresLastName + ", msPresPhoneNo="
				+ msPresPhoneNo + ", msPresEmailAdd=" + msPresEmailAdd
				+ ", msRequestId=" + msRequestId + ", msCfoFirstName="
				+ msCfoFirstName + ", msCfoMiddleInitial=" + msCfoMiddleInitial
				+ ", msCfoLastName=" + msCfoLastName + ", msCfoPhoneNo="
				+ msCfoPhoneNo + ", msCfoEmailAdd=" + msCfoEmailAdd
				+ ", msFirstName=" + msFirstName + ", msMiddleName="
				+ msMiddleName + ", msLastName=" + msLastName + ", msUserId="
				+ msUserId + ", msStaffDetailTitle=" + msStaffDetailTitle
				+ ", msOrgId=" + msOrgId + ", msUserDN=" + msUserDN
				+ ", msWorkFlowRequired=" + msWorkFlowRequired
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
				+ ", msBorough=" + msBorough + "]";
	}

}
