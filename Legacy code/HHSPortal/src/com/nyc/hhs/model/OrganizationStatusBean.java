package com.nyc.hhs.model;

import java.sql.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

import com.nyc.hhs.constants.ApplicationConstants;


public class OrganizationStatusBean {

	@Length(max = 20)
	private String orgId = null;
	private String businessAppId = null;
	private String orgStatus = null;
	private String businessAppStatus = null;
	private String filingStatus = null;
	private String expectedOrgStatus = null;
	
	private Integer approvedSvcCount = null;
	
	private Date   businessAppExpDate = null;
	private Date   filingDueDate = null;

	//Super seeding
	private String   businessAppEvent = null;
	private String   superSeedingStatus = null;
	private Date     superSeedingTimestamp = null;
	private String   superSeedingAppId = null;	

	
	
	public Date getSuperSeedingTimestamp() {
		return superSeedingTimestamp;
	}
	public void setSuperSeedingTimestamp(Date superSeedingTimestamp) {
		this.superSeedingTimestamp = superSeedingTimestamp;
	}
	public String getSuperSeedingAppId() {
		return superSeedingAppId;
	}
	public void setSuperSeedingAppId(String superSeedingAppId) {
		this.superSeedingAppId = superSeedingAppId;
	}
	
	public String getOrgId() {
		return orgId;
	}
	public String getBusinessAppEvent() {
		return businessAppEvent;
	}
	public void setBusinessAppEvent(String businessAppEvent) {
		this.businessAppEvent = businessAppEvent;
	}
	public String getSuperSeedingStatus() {
		return superSeedingStatus;
	}
	public void setSuperSeedingStatus(String superSeedingStatus) {
		this.superSeedingStatus = superSeedingStatus;
	}
	public void setBusinessAppStatus(String businessAppStatus) {
		this.businessAppStatus = businessAppStatus;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public Integer getApprovedSvcCount() {
		return approvedSvcCount;
	}
	public void setApprovedSvcCount(Integer approvedSvcCount) {
		this.approvedSvcCount = approvedSvcCount;
	}
	public String getBusinessAppId() {
		return businessAppId;
	}
	public void setBusinessAppId(String businessAppId) {
		this.businessAppId = businessAppId;
	}
	public String getOrgStatus() {
		return orgStatus;
	}
	public void setOrgStatus(String orgStatus) {
		this.orgStatus = orgStatus;
	}
	public String getBusinessAppStatus() {
		return businessAppStatus;
	}
	public void setBusinessApplicationStatus(String businessAppStatus) {
		this.businessAppStatus = businessAppStatus;
	}
	public String getFilingStatus() {
		return filingStatus;
	}
	public void setFilingStatus(String filingStatus) {
		this.filingStatus = filingStatus;
	}
	public String getExpectedOrgStatus() {
		return expectedOrgStatus;
	}
	public void setExpectedOrgStatus(String caculatedOrgStatus) {
		this.expectedOrgStatus = caculatedOrgStatus;
	}
	public Date getBusinessAppExpDate() {
		return businessAppExpDate;
	}
	public void setBusinessAppExpDate(
			Date businessAppExpDate) {
		this.businessAppExpDate = businessAppExpDate;
	}
	public Date getFilingDueDate() {
		return filingDueDate;
	}
	public void setFilingDueDate(Date filingDueDate) {
		this.filingDueDate = filingDueDate;
	}

	
	
	@Override
	public String toString() {
		return "OrganizationStatusBean [orgId=" + orgId + ", businessAppId="
				+ businessAppId + ", orgStatus=" + orgStatus
				+ ", businessAppStatus=" + businessAppStatus
				+ ", filingStatus=" + filingStatus + ", expectedOrgStatus="
				+ expectedOrgStatus + ", approvedSvcCount=" + approvedSvcCount
				+ ", businessAppExpDate=" + businessAppExpDate
				+ ", filingDueDate=" + filingDueDate + ", businessAppEvent="
				+ businessAppEvent + ", superSeedingStatus="
				+ superSeedingStatus + ", superSeedingTimestamp="
				+ superSeedingTimestamp + ", superSeedingAppId="
				+ superSeedingAppId + "]";
	}
	/*
	 * Compare current provider status and expected provider status
	 * Then return true if identical
	 *      return False if different
	 */
	public boolean needStatusInspection(){
		if( !isDataReady() )  return false;
		
		String expectedStatus =expectedOrgStatus();

		if ( !expectedStatus.equalsIgnoreCase(orgStatus)  ){
			return true;
		}

		return false;
	}

	/*
	 * when all data is ready, computing status data from business application, Organization and Filing
	 * and Generating expected provider status.
	 * 
	 */
	public String expectedOrgStatus(){
		if( !isDataReady() )  return null;

		if( expectedOrgStatus.equalsIgnoreCase( ApplicationConstants.STATUS_APPROVED ) 
			|| ( expectedOrgStatus.equalsIgnoreCase( ApplicationConstants.STATUS_CONDITIONALLY_APPROVED ) 
                  && approvedSvcCount != null && approvedSvcCount >=1)
			)
		{
			if(  needSuperSeeding() ) {
				return checkSuperseeding();
			}else { 
				if(  filingStatus.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED ) ){
					return ApplicationConstants.STATUS_SUSPEND_FILING_EXPIRED ;
				} else if( filingStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED ) ){
					return ApplicationConstants.STATUS_APPROVED ;
				} else{
					//There is no filing , return Business Application status.
					//to skip the process of checking filing status
					return expectedOrgStatus ;
				}
			}
		} else if (expectedOrgStatus.equalsIgnoreCase( ApplicationConstants.STATUS_SUSPEND ) ){
			return ApplicationConstants.STATUS_SUSPEND;
		} else if (expectedOrgStatus.equalsIgnoreCase( ApplicationConstants.STATUS_WITHDRAWN ) ){
			return ApplicationConstants.STATUS_WITHDRAWN;
		} else if (expectedOrgStatus.equalsIgnoreCase( ApplicationConstants.STATUS_REJECTED ) ){
			return ApplicationConstants.STATUS_REJECTED;
		} else if (expectedOrgStatus.equalsIgnoreCase( ApplicationConstants.STATUS_DEFFERED ) ){
			return ApplicationConstants.STATUS_EXPIRED;
		} else if (expectedOrgStatus.equalsIgnoreCase( ApplicationConstants.STATUS_DRAFT ) 
				|| expectedOrgStatus.equalsIgnoreCase( ApplicationConstants.STATUS_IN_REVIEW )
				|| expectedOrgStatus.equalsIgnoreCase( ApplicationConstants.STATUS_RETURNED_FOR_REVISIONS )){
			if(  needSuperSeeding() ) {
				return checkSuperseeding();
			} else {
				return ApplicationConstants.STATUS_EXPIRED;
			}
		}else {
			//In this case all previous Business Application is expired and renewal is in process. 
			//Then renewal process of business application is up to Workflow.
			//Need to change in component
			return ApplicationConstants.STATUS_EXPIRED;
		}
	}

	private String checkSuperseeding(){
		//Renewal application is conditionally approved
		if( businessAppEvent.equalsIgnoreCase( ApplicationConstants.STATUS_CONDITIONALLY_APPROVED)
				&& approvedSvcCount != null && approvedSvcCount >=1 ){
			if(  filingStatus.equalsIgnoreCase(ApplicationConstants.STATUS_EXPIRED ) ){
				return ApplicationConstants.STATUS_APPROVED ;
			} else if( filingStatus.equalsIgnoreCase(ApplicationConstants.STATUS_APPROVED ) ){
				return ApplicationConstants.STATUS_APPROVED ;
			}
			return ApplicationConstants.STATUS_APPROVED;
		} else {
			if( superSeedingAppId.equalsIgnoreCase(businessAppId)   ){
				//Previous BA is expired and Current is 'CONDITIONALLY_APPROVED' with 0 service application either APPROVED or CONDITIONALLY_APPROVED
				return ApplicationConstants.STATUS_EXPIRED;
			}else {
				return expectedOrgStatus;
			}
		}
	}

	private boolean needSuperSeeding(){
		if(  	businessAppEvent.equalsIgnoreCase( ApplicationConstants.STATUS_CONDITIONALLY_APPROVED) ){
			return true;
		}
		else { 
			return false;
		}
	}
	
	/*
	 * Examine all data ready  
	 */
	private boolean isDataReady(){
		if ( orgId == null || businessAppId == null
				|| orgStatus == null || businessAppStatus == null
				|| filingStatus == null || expectedOrgStatus == null
				|| approvedSvcCount == null ){
			return false;
		}

		return true;
	}
	
}
