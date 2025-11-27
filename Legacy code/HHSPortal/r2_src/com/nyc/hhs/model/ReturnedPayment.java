/**
 * 
 */
package com.nyc.hhs.model;

import com.nyc.hhs.constants.HHSConstants;
import java.util.HashMap;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;



/**
 *This class is a bean that store details for returned payment checks
 */
public class ReturnedPayment extends ContractBudgetBean{
	
	@RegExp(value ="^\\d{0,20}")
	//CHECK_NUMBER
	private String checkNumber;
	private String receivedDate;
	private String approvedBy;
	private String checkAmount;
	private String checkStatus;
	private String description;
	private String checkReceived;
	private String budgetId;
	private String createdByUserId;
	private String modifiedByUserId;
	private String action;
	private String returnedPaymentId;
	private String approvedDate;
	private String checkDate;
	private String checkStatusName;
	private String unrecoupedAdvAmount;
	private String lastNotifiedDate;
	@Length(max = 20)
	//AGENCY_TRACKING_NUMBER
	private String agencyTrackingNumber;
	private List<String> docIdList;
	private HashMap<String, HashMap<String, String>> docPropMap; 
	private boolean isReadOnly;
	private String loggedInUserRole;
	private String loggedInUserOrgType;
	private String budgetStatusId;
	private String orgType;
	private String groupNotificationId;
	private String updatedDate;
	private String updatedByUser;

	
	
	
	
	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}

	public String getUpdatedByUser() {
		return updatedByUser;
	}

	public void setUpdatedByUser(String updatedByUser) {
		this.updatedByUser = updatedByUser;
	}

	public String getGroupNotificationId() {
		return groupNotificationId;
	}

	public void setGroupNotificationId(String groupNotificationId) {
		this.groupNotificationId = groupNotificationId;
	}

	public String getBudgetStatusId() {
		return budgetStatusId;
	}

	public void setBudgetStatusId(String budgetStatusId) {
		this.budgetStatusId = budgetStatusId;
	}

	public String getLoggedInUserRole() {
		return loggedInUserRole;
	}

	public void setLoggedInUserRole(String loggedInUserRole) {
		this.loggedInUserRole = loggedInUserRole;
	}

	public String getLoggedInUserOrgType() {
		return loggedInUserOrgType;
	}

	public void setLoggedInUserOrgType(String loggedInUserOrgType) {
		this.loggedInUserOrgType = loggedInUserOrgType;
	}

	public boolean isReadOnly() {
		return isReadOnly;
	}

	public void setReadOnly(boolean isReadOnly) {
		this.isReadOnly = isReadOnly;
	}

	public ReturnedPayment()
	{
		setDefaultSortData();
	}
	
	public void setDefaultSortData()
	{
		setFirstSort("RECEIVED_DATE");
		setSecondSort("CHECK_AMOUNT");
		setFirstSortDate(true);
		setSecondSortDate(false);
		setFirstSortType(HHSConstants.ASCENDING);
		setSecondSortType(HHSConstants.DESCENDING);
		setSortColumnName("receivedDate");
	}
	
	
	//<!-- [Start] R9.7.5 QC9719 -->
	private Integer actionDisable = 0;
	public Integer getActionDisable() {
	    
        return actionDisable;
    }
    public void setActionDisable(Integer actionDisable) {
        this.actionDisable = actionDisable;
    }
	//<!-- [End] R9.7.5 QC9719 -->
	
	//<!-- [Start] R9.7.6 QC9730 -->
	private Integer actionException = 0;
	public Integer getActionException() {
	    
        return actionException;
    }
    public void setActionException(Integer actionException) {
        this.actionException = actionException;
    }
	//<!-- [End] R9.7.6 QC9730 -->

	/**
	 * @return the agencyTrackingNumber
	 */
	public String getAgencyTrackingNumber() {
		return agencyTrackingNumber;
	}
	/**
	 * @param agencyTrackingNumber the agencyTrackingNumber to set
	 */
	public void setAgencyTrackingNumber(String agencyTrackingNumber) {
		this.agencyTrackingNumber = agencyTrackingNumber;
	}
	/**
	 * @return the unrecoupedAdvAmount
	 */
	public String getUnrecoupedAdvAmount() {
		return unrecoupedAdvAmount;
	}
	/**
	 * @param unrecoupedAdvAmount the unrecoupedAdvAmount to set
	 */
	public void setUnrecoupedAdvAmount(String unrecoupedAdvAmount) {
		this.unrecoupedAdvAmount = unrecoupedAdvAmount;
	}
	/**
	 * @return the lastNotifiedDate
	 */
	public String getLastNotifiedDate() {
		return lastNotifiedDate;
	}
	/**
	 * @param lastNotifiedDate the lastNotifiedDate to set
	 */
	public void setLastNotifiedDate(String lastNotifiedDate) {
		this.lastNotifiedDate = lastNotifiedDate;
	}
	/**
	 * @return the checkStatusName
	 */
	public String getCheckStatusName() {
		return checkStatusName;
	}
	/**
	 * @param checkStatusName the checkStatusName to set
	 */
	public void setCheckStatusName(String checkStatusName) {
		this.checkStatusName = checkStatusName;
	}
	/**
	 * @return the approvedDate
	 */
	public String getApprovedDate() {
		return approvedDate;
	}
	/**
	 * @param approvedDate the approvedDate to set
	 */
	public void setApprovedDate(String approvedDate) {
		this.approvedDate = approvedDate;
	}
	/**
	 * @return the checkDate
	 */
	public String getCheckDate() {
		return checkDate;
	}
	/**
	 * @param checkDate the checkDate to set
	 */
	public void setCheckDate(String checkDate) {
		this.checkDate = checkDate;
	}
	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}
	/**
	 * @return the returnedPaymentId
	 */
	public String getReturnedPaymentId() {
		return returnedPaymentId;
	}
	/**
	 * @param returnedPaymentId the returnedPaymentId to set
	 */
	public void setReturnedPaymentId(String returnedPaymentId) {
		this.returnedPaymentId = returnedPaymentId;
	}
	/**
	 * @return the createdByUserId
	 */
	public String getCreatedByUserId() {
		return createdByUserId;
	}
	/**
	 * @param createdByUserId the createdByUserId to set
	 */
	public void setCreatedByUserId(String createdByUserId) {
		this.createdByUserId = createdByUserId;
	}
	/**
	 * @return the modifiedByUserId
	 */
	public String getModifiedByUserId() {
		return modifiedByUserId;
	}
	/**
	 * @param modifiedByUserId the modifiedByUserId to set
	 */
	public void setModifiedByUserId(String modifiedByUserId) {
		this.modifiedByUserId = modifiedByUserId;
	}
	/**
	 * @param receivedDate the receivedDate to set
	 */
	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}
	/**
	 * @return the budgetId
	 */
	public String getBudgetId() {
		return budgetId;
	}
	/**
	 * @param budgetId the budgetId to set
	 */
	public void setBudgetId(String budgetId) {
		this.budgetId = budgetId;
	}
	public String getCheckReceived() {
		return checkReceived;
	}
	public void setCheckReceived(String checkReceived) {
		this.checkReceived = checkReceived;
	}
	public String getNotifyProvider() {
		return notifyProvider;
	}
	public void setNotifyProvider(String notifyProvider) {
		this.notifyProvider = notifyProvider;
	}
	private String notifyProvider;
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * @return the checkNumber
	 */
	public String getCheckNumber() {
		return checkNumber;
	}
	/**
	 * @param checkNumber the checkNumber to set
	 */
	public void setCheckNumber(String checkNumber) {
		this.checkNumber = checkNumber;
	}
	/**
	 * @return the receivedDate
	 */
	public String getReceivedDate() {
		return receivedDate;
	}
	/**
	 * @param receivedDate the receivedDate to set
	 */
	/*public void setReceivedDate( receivedDate) {
		this.receivedDate = receivedDate;
	}*/
	/**
	 * @return the approvedBy
	 */
	public String getApprovedBy() {
		return approvedBy;
	}
	/**
	 * @param approvedBy the approvedBy to set
	 */
	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}
	/**
	 * @return the checkAmount
	 */
	public String getCheckAmount() {
		return checkAmount;
	}
	/**
	 * @param checkAmount the checkAmount to set
	 */
	public void setCheckAmount(String checkAmount) {
		this.checkAmount = checkAmount;
	}
	/**
	 * @return the checkStatus
	 */
	public String getCheckStatus() {
		return checkStatus;
	}
	/**
	 * @param checkStatus the checkStatus to set
	 */
	public void setCheckStatus(String checkStatus) {
		this.checkStatus = checkStatus;
	}
	public List<String> getDocIdList() {
		return docIdList;
	}
	public void setDocIdList(List<String> docIdList) {
		this.docIdList = docIdList;
	}
	public HashMap<String, HashMap<String, String>> getDocPropMap() {
		return docPropMap;
	}
	public void setDocPropMap(HashMap<String, HashMap<String, String>> docPropMap) {
		this.docPropMap = docPropMap;
	}

	public String getOrgType()
	{
		return orgType;
	}

	public void setOrgType(String orgType)
	{
		this.orgType = orgType;
	}

	
}
