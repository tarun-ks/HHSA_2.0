package com.nyc.hhs.model;

import java.sql.Date;
import java.sql.Timestamp;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This class is used to maintain the data for application summary. 
 * 
 */

public class ApplicationSummary {
	
	@Length(max = 20) 
	private String msBusinessAppId;
	
	@Length(max = 20) 
	private String msServiceAppId;
	
	@Length(max = 20) 
	private String msApplicationId;
	
	private String msAppName;
	
	private String msServiceElementId;
	
	private String msAppStatus;
	
	private String msAppSubmittedBy;
	
	private Timestamp   mdAppSubmissionDate;
	
	private Date   mdAppStartDate;
	
	private Date   mdAppExpirationDate;
	
	private String msAppType;
	
	@Length(max = 20) 
	private String msOrgId;
	
	private String msStatusId;
	
	private Date mdRequestDate;
	
	private String msRequester;
	
	private String msWithdrawanAppId;
	
	private String msWithdrawanStatus;
	
	private String msId;
	
	private String msComments;
	
	private String msProviderStatus;
	
	private Date msExpirationDate;
	
	private Boolean isDraftServiceLink;
	
	private String msDeactivatedStatus = null;
	
	private Timestamp timeStampStatus = null;
	
	private String timeStampStatus1 = null;
	
	private String msSuperSedingStatus = null;
	
	private Boolean displayExclamanationBusiness = false;
	
	private Boolean displayExclamanationService = false;
	
	private String displaySuspendValue = null;
	
	private String displayConditionallyValue = null;
	
	private String msWorkflowId = null;
	
	private String topBusinessAppId = null;
	
	private Date createdDate = null;
	
	private Boolean moreThanOne = false;
	
	private String statusSetBy = null;

	private Timestamp applicationCreatedDate = null;
	
	private Timestamp cityUserEffectiveDate = null;
	
	private String eventName = null;
	
	private String msSection = null;
	
	private Boolean finalViewBusinessExclamanationSign = null;
	
	private Boolean finalViewServicdExclamanationSign = null;
	
	private Boolean displayHistoryDropDown = false;
	
	private String msSuperSedingEntityType ="";
	private String msSuperSedingEntityId ="";
	
	public Boolean getDisplayHistoryDropDown() {
		return displayHistoryDropDown;
	}

	public void setDisplayHistoryDropDown(Boolean displayHistoryDropDown) {
		this.displayHistoryDropDown = displayHistoryDropDown;
	}

	/**
	 * @return the finalViewBusinessExclamanationSign
	 */
	public Boolean getFinalViewBusinessExclamanationSign() {
		return finalViewBusinessExclamanationSign;
	}

	/**
	 * @param finalViewBusinessExclamanationSign the finalViewBusinessExclamanationSign to set
	 */
	public void setFinalViewBusinessExclamanationSign(
			Boolean finalViewBusinessExclamanationSign) {
		this.finalViewBusinessExclamanationSign = finalViewBusinessExclamanationSign;
	}

	/**
	 * @return the finalViewServicdExclamanationSign
	 */
	public Boolean getFinalViewServicdExclamanationSign() {
		return finalViewServicdExclamanationSign;
	}

	/**
	 * @param finalViewServicdExclamanationSign the finalViewServicdExclamanationSign to set
	 */
	public void setFinalViewServicdExclamanationSign(
			Boolean finalViewServicdExclamanationSign) {
		this.finalViewServicdExclamanationSign = finalViewServicdExclamanationSign;
	}

	public String getMsSection()
	{
		return msSection;
	}

	public void setMsSection(String msSection)
	{
		this.msSection = msSection;
	}

	/**
	 * @return the cityUserEffectiveDate
	 */
	public Timestamp getCityUserEffectiveDate() {
		return cityUserEffectiveDate;
	}

	/**
	 * @param cityUserEffectiveDate the cityUserEffectiveDate to set
	 */
	public void setCityUserEffectiveDate(Timestamp cityUserEffectiveDate) {
		this.cityUserEffectiveDate = cityUserEffectiveDate;
	}

	/**
	 * @return the applicationCreatedDate
	 */
	public Timestamp getApplicationCreatedDate() {
		return applicationCreatedDate;
	}

	/**
	 * @param applicationCreatedDate the applicationCreatedDate to set
	 */
	public void setApplicationCreatedDate(Timestamp applicationCreatedDate) {
		this.applicationCreatedDate = applicationCreatedDate;
	}

	/**
	 * @return the statusSetBy
	 */
	public String getStatusSetBy() {
		return statusSetBy;
	}

	/**
	 * @param statusSetBy the statusSetBy to set
	 */
	public void setStatusSetBy(String statusSetBy) {
		this.statusSetBy = statusSetBy;
	}

	/**
	 * @return the moreThanOne
	 */
	public Boolean getMoreThanOne() {
		return moreThanOne;
	}

	/**
	 * @param moreThanOne the moreThanOne to set
	 */
	public void setMoreThanOne(Boolean moreThanOne) {
		this.moreThanOne = moreThanOne;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the topBusinessAppId
	 */
	public String getTopBusinessAppId() {
		return topBusinessAppId;
	}

	/**
	 * @param topBusinessAppId the topBusinessAppId to set
	 */
	public void setTopBusinessAppId(String topBusinessAppId) {
		this.topBusinessAppId = topBusinessAppId;
	}

	public String getMsWorkflowId() {
		return msWorkflowId;
	}
	
	public void setMsWorkflowId(String msWorkflowId) {
		this.msWorkflowId = msWorkflowId;
	}
	
	public String getDisplaySuspendValue() {
		return displaySuspendValue;
	}
	
	public void setDisplaySuspendValue(String displaySuspendValue) {
		this.displaySuspendValue = displaySuspendValue;
	}
	
	public String getDisplayConditionallyValue() {
		return displayConditionallyValue;
	}
	
	public void setDisplayConditionallyValue(String displayConditionallyValue) {
		this.displayConditionallyValue = displayConditionallyValue;
	}
	
	public Boolean getDisplayExclamanationBusiness() {
		return displayExclamanationBusiness;
	}
	
	public void setDisplayExclamanationBusiness(Boolean displayExclamanationBusiness) {
		this.displayExclamanationBusiness = displayExclamanationBusiness;
	}

	public Boolean getDisplayExclamanationService() {
		return displayExclamanationService;
	}

	public void setDisplayExclamanationService(Boolean displayExclamanationService) {
		this.displayExclamanationService = displayExclamanationService;
	}
	
	public String getMsSuperSedingStatus() {
		return msSuperSedingStatus;
	}
	
	public void setMsSuperSedingStatus(String msSuperSedingStatus) {
		this.msSuperSedingStatus = msSuperSedingStatus;
	}
	
	public String getTimeStampStatus1() {
		return timeStampStatus1;
	}
	
	public void setTimeStampStatus1(String timeStampStatus1) {
		this.timeStampStatus1 = timeStampStatus1;
	}
	
	public Timestamp getTimeStampStatus() {
		return timeStampStatus;
	}

	public void setTimeStampStatus(Timestamp timeStampStatus) {
		this.timeStampStatus = timeStampStatus;
	}

	public String getMsDeactivatedStatus() {
		return msDeactivatedStatus;
	}

	public void setMsDeactivatedStatus(String msDeactivatedStatus) {
		this.msDeactivatedStatus = msDeactivatedStatus;
	}
	
	public Boolean getIsDraftServiceLink() {
		return isDraftServiceLink;
	}
	
	public void setIsDraftServiceLink(Boolean isDraftServiceLink) {
		this.isDraftServiceLink = isDraftServiceLink;
	}
	
	public String getMsBusinessAppId() {
		return msBusinessAppId;
	}
	
	public void setMsBusinessAppId(String msBusinessAppId) {
		this.msBusinessAppId = msBusinessAppId;
	}
	
	public String getMsServiceAppId() {
		return msServiceAppId;
	}

	public void setMsServiceAppId(String msServiceAppId) {
		this.msServiceAppId = msServiceAppId;
	}
	
	public String getMsApplicationId() {
		return msApplicationId;
	}
	
	public void setMsApplicationId(String msApplicationId) {
		this.msApplicationId = msApplicationId;
	}
	
	public String getMsAppName() {
		return msAppName;
	}

	public void setMsAppName(String msAppName) {
		this.msAppName = msAppName;
	}
	
	public String getMsServiceElementId() {
		return msServiceElementId;
	}
	
	public void setMsServiceElementId(String msServiceElementId) {
		this.msServiceElementId = msServiceElementId;
	}
	
	public String getMsAppStatus() {
		return msAppStatus;
	}
	
	public void setMsAppStatus(String msAppStatus) {
		this.msAppStatus = msAppStatus;
	}
	
	public String getMsAppSubmittedBy() {
		return msAppSubmittedBy;
	}
	
	public void setMsAppSubmittedBy(String msAppSubmittedBy) {
		this.msAppSubmittedBy = msAppSubmittedBy;
	}
	
	public Timestamp getMdAppSubmissionDate() {
		return mdAppSubmissionDate;
	}
	
	public void setMdAppSubmissionDate(Timestamp mdAppSubmissionDate) {
		this.mdAppSubmissionDate = mdAppSubmissionDate;
	}

	public Date getMdAppStartDate() {
		return mdAppStartDate;
	}
	
	public void setMdAppStartDate(Date mdAppStartDate) {
		this.mdAppStartDate = mdAppStartDate;
	}
	
	public Date getMdAppExpirationDate() {
		return mdAppExpirationDate;
	}

	public void setMdAppExpirationDate(Date mdAppExpirationDate) {
		this.mdAppExpirationDate = mdAppExpirationDate;
	}
	
	public String getMsAppType() {
		return msAppType;
	}
	
	public void setMsAppType(String msAppType) {
		this.msAppType = msAppType;
	}

	public String getMsOrgId() {
		return msOrgId;
	}
	
	public void setMsOrgId(String msOrgId) {
		this.msOrgId = msOrgId;
	}
	
	public String getMsStatusId() {
		return msStatusId;
	}
	
	public void setMsStatusId(String msStatusId) {
		this.msStatusId = msStatusId;
	}

	public Date getMdRequestDate() {
		return mdRequestDate;
	}

	public void setMdRequestDate(Date mdRequestDate) {
		this.mdRequestDate = mdRequestDate;
	}
	
	public String getMsRequester() {
		return msRequester;
	}
	
	public void setMsRequester(String msRequester) {
		this.msRequester = msRequester;
	}
	
	public String getMsWithdrawanAppId() {
		return msWithdrawanAppId;
	}
	
	public void setMsWithdrawanAppId(String msWithdrawanAppId) {
		this.msWithdrawanAppId = msWithdrawanAppId;
	}
	
	public String getMsWithdrawanStatus() {
		return msWithdrawanStatus;
	}
	
	public void setMsWithdrawanStatus(String msWithdrawanStatus) {
		this.msWithdrawanStatus = msWithdrawanStatus;
	}
	
	public String getMsId() {
		return msId;
	}
	
	public void setMsId(String msId) {
		this.msId = msId;
	}
	
	public String getMsComments() {
		return msComments;
	}
	
	public void setMsComments(String msComments) {
		this.msComments = msComments;
	}
	
	public String getMsProviderStatus() {
		return msProviderStatus;
	}
	
	public void setMsProviderStatus(String msProviderStatus) {
		this.msProviderStatus = msProviderStatus;
	}
	
	public Date getMsExpirationDate() {
		return msExpirationDate;
	}
	
	public void setMsExpirationDate(Date msExpirationDate) {
		this.msExpirationDate = msExpirationDate;
	}

	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getMsSuperSedingEntityType() {
		return msSuperSedingEntityType;
	}

	public void setMsSuperSedingEntityType(String msSuperSedingEntityType) {
		this.msSuperSedingEntityType = msSuperSedingEntityType;
	}

	public String getMsSuperSedingEntityId() {
		return msSuperSedingEntityId;
	}

	public void setMsSuperSedingEntityId(String msSuperSedingEntityId) {
		this.msSuperSedingEntityId = msSuperSedingEntityId;
	}

	@Override
	public String toString() {
		return "ApplicationSummary [msBusinessAppId=" + msBusinessAppId
				+ ", msServiceAppId=" + msServiceAppId + ", msApplicationId="
				+ msApplicationId + ", msAppName=" + msAppName
				+ ", msServiceElementId=" + msServiceElementId
				+ ", msAppStatus=" + msAppStatus + ", msAppSubmittedBy="
				+ msAppSubmittedBy + ", mdAppSubmissionDate="
				+ mdAppSubmissionDate + ", mdAppStartDate=" + mdAppStartDate
				+ ", mdAppExpirationDate=" + mdAppExpirationDate
				+ ", msAppType=" + msAppType + ", msOrgId=" + msOrgId
				+ ", msStatusId=" + msStatusId + ", mdRequestDate="
				+ mdRequestDate + ", msRequester=" + msRequester
				+ ", msWithdrawanAppId=" + msWithdrawanAppId
				+ ", msWithdrawanStatus=" + msWithdrawanStatus + ", msId="
				+ msId + ", msComments=" + msComments + ", msProviderStatus="
				+ msProviderStatus + ", msExpirationDate=" + msExpirationDate
				+ ", isDraftServiceLink=" + isDraftServiceLink
				+ ", msDeactivatedStatus=" + msDeactivatedStatus
				+ ", timeStampStatus=" + timeStampStatus
				+ ", timeStampStatus1=" + timeStampStatus1
				+ ", msSuperSedingStatus=" + msSuperSedingStatus
				+ ", displayExclamanationBusiness="
				+ displayExclamanationBusiness
				+ ", displayExclamanationService="
				+ displayExclamanationService + ", displaySuspendValue="
				+ displaySuspendValue + ", displayConditionallyValue="
				+ displayConditionallyValue + ", msWorkflowId=" + msWorkflowId
				+ ", topBusinessAppId=" + topBusinessAppId + ", createdDate="
				+ createdDate + ", moreThanOne=" + moreThanOne
				+ ", statusSetBy=" + statusSetBy + ", applicationCreatedDate="
				+ applicationCreatedDate + ", cityUserEffectiveDate="
				+ cityUserEffectiveDate + ", eventName=" + eventName
				+ ", msSection=" + msSection
				+ ", finalViewBusinessExclamanationSign="
				+ finalViewBusinessExclamanationSign
				+ ", finalViewServicdExclamanationSign="
				+ finalViewServicdExclamanationSign
				+ ", displayHistoryDropDown=" + displayHistoryDropDown
				+ ", msSuperSedingEntityType=" + msSuperSedingEntityType
				+ ", msSuperSedingEntityId=" + msSuperSedingEntityId + "]";
	}
}



