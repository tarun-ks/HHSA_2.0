package com.nyc.hhs.model;

import java.sql.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.DateUtil;

/**
 * This class is a bean which maintains the Contract Details information.
 *
 */

public class ContractDetails {

	@Length(max = 50)
	private String msContractType="";
	@Length(max = 100)
	private String msContractNYCAgency = ""; 
	private String msContractFunderName = "";
	private String msContractRefFirstName = "";
	private String msContractRefMidName = "";
	private String msContractRefLastName = "";
	private String msContractRefTitle = "";
	private String msContractRefPhone = "";
	private String msContractRefEmail = "";
	private String msContractID = "";
	private String msContractDescription = "";
	private Date msContractStartDate = null;
	private Date msContractEndDate = null;
	private String msContractBudget = "";
	private String msOrgId = "";
	private String msActions;
	private String msOldContractID;
	private String readOnly ="";
	private String msStartDateToDisplay = "";
	private String msEndDateToDisplay = "";
	private String msCreatedBy = "";
	private String msOldContractNYCAgency = ""; 
	private String msOldContractFunderName = "";
	private String msOldContractDescription = "";
	private String msContractDetailsId = "";
	
	public String getMsStartDateToDisplay() throws ApplicationException {
		if(msContractStartDate != null) {
			msStartDateToDisplay = DateUtil.getDateMMddYYYYFormat(msContractStartDate);
		}
		return msStartDateToDisplay;
	}

	public void setMsStartDateToDisplay(String msStartDateToDisplay) {
		this.msStartDateToDisplay = msStartDateToDisplay;
	}

	public String getMsEndDateToDisplay() throws ApplicationException {
		if(msContractEndDate != null) {
			msEndDateToDisplay = DateUtil.getDateMMddYYYYFormat(msContractEndDate);
		}
		return msEndDateToDisplay;
	}

	public void setMsEndDateToDisplay(String msEndDateToDisplay) {
		this.msEndDateToDisplay = msEndDateToDisplay;
	}

	public String getReadOnly() {
		return readOnly;
	}
	
	public void setReadOnly(String readOnly) {
		this.readOnly = readOnly;
	}
	public String getMsOldContractID() {
		return msOldContractID;
	}
	public void setMsOldContractID(String msOldContractID) {
		this.msOldContractID = msOldContractID;
	}
	public String getMsContractType() {
		return msContractType;
	}
	public void setMsContractType(String msContractType) {
		this.msContractType = msContractType;
	}
	public String getMsContractNYCAgency() {
		return msContractNYCAgency;
	}
	public void setMsContractNYCAgency(String msContractNYCAgency) {
		this.msContractNYCAgency = msContractNYCAgency;
	}
	public String getMsContractFunderName() {
		return msContractFunderName;
	}
	public void setMsContractFunderName(String msContractFunderName) {
		this.msContractFunderName = msContractFunderName;
	}
	public String getMsContractRefFirstName() {
		return msContractRefFirstName;
	}
	public void setMsContractRefFirstName(String msContractRefFirstName) {
		this.msContractRefFirstName = msContractRefFirstName;
	}
	public String getMsContractRefMidName() {
		return msContractRefMidName;
	}
	public void setMsContractRefMidName(String msContractRefMidName) {
		this.msContractRefMidName = msContractRefMidName;
	}
	public String getMsContractRefLastName() {
		return msContractRefLastName;
	}
	public void setMsContractRefLastName(String msContractRefLastName) {
		this.msContractRefLastName = msContractRefLastName;
	}
	public String getMsContractRefTitle() {
		return msContractRefTitle;
	}
	public void setMsContractRefTitle(String msContractRefTitle) {
		this.msContractRefTitle = msContractRefTitle;
	}
	public String getMsContractRefPhone() {
		return msContractRefPhone;
	}
	public void setMsContractRefPhone(String msContractRefPhone) {
		this.msContractRefPhone = msContractRefPhone;
	}
	public String getMsContractRefEmail() {
		return msContractRefEmail;
	}
	public void setMsContractRefEmail(String msContractRefEmail) {
		this.msContractRefEmail = msContractRefEmail;
	}
	public String getMsContractID() {
		return msContractID;
	}
	public void setMsContractID(String msContractID) {
		this.msContractID = msContractID;
	}
	public String getMsContractDescription() {
		return msContractDescription;
	}
	public void setMsContractDescription(String msContractDescription) {
		this.msContractDescription = msContractDescription;
	}
	public Date getMsContractStartDate() {
		return msContractStartDate;
	}
	public void setMsContractStartDate(Date msContractStartDate) {
		this.msContractStartDate = msContractStartDate;
	}
	public Date getMsContractEndDate() {
		return msContractEndDate;
	}
	public void setMsContractEndDate(Date msContractEndDate) {
		this.msContractEndDate = msContractEndDate;
	}
	public String getMsContractBudget() {
		return msContractBudget;
	}
	public void setMsContractBudget(String msContractBudget) {
		this.msContractBudget = msContractBudget;
	}
	public String getMsOrgId() {
		return msOrgId;
	}
	public void setMsOrgId(String msOrgId) {
		this.msOrgId = msOrgId;
	}
	
	public String getMsActions() {
		return msActions;
	}
	
	public void setMsActions(String msActions) {
		this.msActions = msActions;
	}

	public String getMsCreatedBy()
	{
		return msCreatedBy;
	}

	public void setMsCreatedBy(String msCreatedBy)
	{
		this.msCreatedBy = msCreatedBy;
	}

	public String getMsOldContractNYCAgency()
	{
		return msOldContractNYCAgency;
	}

	public void setMsOldContractNYCAgency(String msOldContractNYCAgency)
	{
		this.msOldContractNYCAgency = msOldContractNYCAgency;
	}

	public String getMsOldContractFunderName()
	{
		return msOldContractFunderName;
	}

	public void setMsOldContractFunderName(String msOldContractFunderName)
	{
		this.msOldContractFunderName = msOldContractFunderName;
	}

	public String getMsOldContractDescription()
	{
		return msOldContractDescription;
	}

	public void setMsOldContractDescription(String msOldContractDescription)
	{
		this.msOldContractDescription = msOldContractDescription;
	}

	public String getMsContractDetailsId()
	{
		return msContractDetailsId;
	}

	public void setMsContractDetailsId(String msContractDetailsId)
	{
		this.msContractDetailsId = msContractDetailsId;
	}

	@Override
	public String toString() {
		return "ContractDetails [msContractType=" + msContractType
				+ ", msContractNYCAgency=" + msContractNYCAgency
				+ ", msContractFunderName=" + msContractFunderName
				+ ", msContractRefFirstName=" + msContractRefFirstName
				+ ", msContractRefMidName=" + msContractRefMidName
				+ ", msContractRefLastName=" + msContractRefLastName
				+ ", msContractRefTitle=" + msContractRefTitle
				+ ", msContractRefPhone=" + msContractRefPhone
				+ ", msContractRefEmail=" + msContractRefEmail
				+ ", msContractID=" + msContractID + ", msContractDescription="
				+ msContractDescription + ", msContractStartDate="
				+ msContractStartDate + ", msContractEndDate="
				+ msContractEndDate + ", msContractBudget=" + msContractBudget
				+ ", msOrgId=" + msOrgId + ", msActions=" + msActions
				+ ", msOldContractID=" + msOldContractID + ", readOnly="
				+ readOnly + ", msStartDateToDisplay=" + msStartDateToDisplay
				+ ", msEndDateToDisplay=" + msEndDateToDisplay
				+ ", msCreatedBy=" + msCreatedBy + ", msOldContractNYCAgency="
				+ msOldContractNYCAgency + ", msOldContractFunderName="
				+ msOldContractFunderName + ", msOldContractDescription="
				+ msOldContractDescription + ", msContractDetailsId="
				+ msContractDetailsId + "]";
	}
	
}
