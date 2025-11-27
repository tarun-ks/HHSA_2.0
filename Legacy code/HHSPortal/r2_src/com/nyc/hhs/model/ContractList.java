package com.nyc.hhs.model;

import java.util.Date;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
/**
 *This class is a bean that store details for contract list
 * <p>
 * This class is updated in Release 7
 * <p>
 */
public class ContractList extends BaseFilter
{

	public ContractList()
	{
		setDefaultSortData();
	}

	private String contractTitle;
	private String baseContractTitle;
	private String procurementTitle;
	private String programName;
	private String contractAgencyName;
	private String provider;
	private String ctId;
	private String awardEpin;
	private String procEpin;
	private String extCT;
	private String contractValueFrom;
	private String contractValueTo;
	private String contractStatus;
	private String contractStatusId;
	private String orgName;
	private String orgType;
	private List<String> contractStatusList;
	private Date contractStartDate;
	private Date contractEndDate;
	private String contractValue;
	private String lastUpdateDate;
	private String amendAmount;
	private Date amendStartDate;
	private Date amendEndDate;
	private String amendReason;
	private String newTotalContractAmount;
	private String actions;
	@RegExp(value ="^\\d{0,22}")
	private String contractId;
	private String amendmentContractId;
	private String budgetType;
	private String statusIdAmendErrorMsg;
	private boolean firstLoad = false;
	private boolean pendingAmendment = false;
	private boolean isSuspended = false;
	private boolean alreadyRenewed = false;
	// Release 3.8.0 Enhancement 6481
	private boolean contractUpdate = false;
	private boolean filterClicked = false;
	private String modifyBy;
	@RegExp(value ="^\\d{0,22}")
	private String contractTypeId;
	private String contractSourceId;
	private String workFlowId;
	private String budgetTypeId;
	private String budgetStatusId;
	private String budgetStatus;
	private String taskStatus;
	private String procStatusIds;
	private String statusCheck;
	private Boolean isNewFYConfigPending;
	private String fyConfigFiscalYear;
	@Length(max = 20)
	private String agencyId;
	@RegExp(value ="^\\d{0,22}")
	private String budgetId;
	@RegExp(value ="^\\d{0,22}")
	private String invoiceId;
	private String invoiceStatusId;
	private String certFundsRequired;
	private String parentContractId;
	private Boolean isViewCOF = false;
	private String providerId;
	private String providerOrgId;
	private String dateLastUpdateFrom;
	private String dateLastUpdateTo;
	// R4 Change Log
	private String amendEpin;
	


	// Start : R5 Added
	private String invoiceCount;
	private String paymentCount;
	private String budgetCount;
	private String providerAdmin;
	
	// Start : R6 Added
	// This attribute is changed from existingBudget to usesFte
	private String usesFte;
	private String approvedDate;
	private String existingBudget;
	private String returnedPaymentAmount;
	private int noOfReturnedPayments;
	private String programId;
	//Added in R7
	private String contractMessage;
	private String contractFlagged;
	private boolean filterFlaggedContracts=false;
	
	//<!-- [Start] R9.7.3 QC9719 -->
	private Integer actionDisable = 0;
	public Integer getActionDisable() {
	    
        return actionDisable;
    }
    public void setActionDisable(Integer actionDisable) {
        this.actionDisable = actionDisable;
    }
    //<!-- [End] R9.7.3 QC9719 -->
	
	//<!-- [Start] R9.7.6 QC9730 -->
	private Integer actionException = 0;
	public Integer getActionException() {
	    
        return actionException;
    }
    public void setActionException(Integer actionException) {
        this.actionException = actionException;
    }
	//<!-- [End] R9.7.6 QC9730 -->

    
    
    
	/* [Start] R7.12.0 QC9314 */
	private Integer onGoingAmendCount = HHSConstants.INT_ONE;

    public Integer getOnGoingAmendCount() {
		return onGoingAmendCount;
	}
	public void setOnGoingAmendCount(Integer onGoingAmendCount) {
		this.onGoingAmendCount = onGoingAmendCount;
	}
	/* [End] R7.12.0 QC9314 */

    /* [Start] R7.2.0 QC8914 Add indicator for Access control */
	private String userSubRole = "";
	
	public String getUserSubRole() {
	    
        return userSubRole;
    }
    public void setUserSubRole(String userSubRole) {
        this.userSubRole = userSubRole;
    }
    /* [End] R7.2.0 QC8914 Add indicator for Access control */


    //[Start] R7.3.0 Add Amendment Title  
    private String amendmentTitle;
    
    public String getAmendmentTitle() {
        return amendmentTitle;
    }
    public void setAmendmentTitle(String amendmentTitle) {
        this.amendmentTitle = amendmentTitle;
    }
    //[End] R7.3.0 Add Amendment Title
    
    /* [Start] R7.11.0 QC9122 Add indicator for Access control */
	private Boolean needAmendRemoved = false;

    public Boolean getNeedAmendRemoved() {
		return needAmendRemoved;
	}
	public void setNeedAmendRemoved(Boolean needAmendRemoved) {
		this.needAmendRemoved = needAmendRemoved;
	}
    /* [End] R7.11.0 QC9122 Add indicator for Access control */
    
	// [Start] QC9517 R 8.7.0 Cancel option should not be available for a Negative Amendment after Budget has been Approved
	private Boolean negativeAmend = false;
	private Integer amendBudgetApprovedCount = 0;
	
	public Boolean getNegativeAmend() {
		return negativeAmend;
	}
	public void setNegativeAmend(Boolean negativeAmend) {
		this.negativeAmend = negativeAmend;
	}
	public Integer getAmendBudgetApprovedCount() {
		return amendBudgetApprovedCount;
	}
	public void setAmendBudgetApprovedCount(Integer amendBudgetApprovedCount) {
		this.amendBudgetApprovedCount = amendBudgetApprovedCount;
	}
	// [Start] QC9517 R 8.7.0 Cancel option should not be available for a Negative Amendment after Budget has been Approved
    
	// [Start] QC9304 R 8.8.0 Do not allow Cancel Amendment after an Out-Year Amendment has been Marked as Registered
	private Integer partialMergeCount = 0;
	
	public Integer getPartialMergeCount() {
		return partialMergeCount;
	}
	public void setPartialMergeCount(Integer partialMergeCount) {
		this.partialMergeCount = partialMergeCount;
	}
	// [End] QC9304 R 8.8.0 Do not allow Cancel Amendment after an Out-Year Amendment has been Marked as Registered
	
	public String getContractFlagged()
	{
		return contractFlagged;
	}
	public void setContractFlagged(String contractFlagged)
	{
		this.contractFlagged = contractFlagged;
	}
	public String getContractMessage()
	{
		return contractMessage;
	}

	public void setContractMessage(String contractMessage)
	{
		this.contractMessage = contractMessage;
	}
	
	public boolean getFilterFlaggedContracts()
	{
		return filterFlaggedContracts;
	}
	public void setFilterFlaggedContracts(boolean filterFlaggedContracts)
	{
		this.filterFlaggedContracts = filterFlaggedContracts;
	}
	
	// Start : Added for R7: 8644 part 2
	private String isBudgetApproved;
	
	//start : Added for R7: 8644 part3
	private String markAsFmsRegistered;
	
	/**
	 * @return the isBudgetApproved
	 */
	public String getIsBudgetApproved()
	{
		return isBudgetApproved;
	}
	/**
	 * @param isBudgetApproved the isBudgetApproved to set
	 */
	public void setIsBudgetApproved(String isBudgetApproved)
	{
		this.isBudgetApproved = isBudgetApproved;
	}
	public String getMarkAsFmsRegistered()
	{
		return markAsFmsRegistered;
	}
	public void setMarkAsFmsRegistered(String markAsFmsRegistered)
	{
		this.markAsFmsRegistered = markAsFmsRegistered;
	}
	//End : Added in R7
	// Start: Added in R7 for Cost Center
	private String costCenterOpted;
	
	
	public String getCostCenterOpted()
	{
		return costCenterOpted;
	}
	public void setCostCenterOpted(String costCenterOpted)
	{
		this.costCenterOpted = costCenterOpted;
	}
	// End: Added in R7 for Cost Center
	//R7: Program Income changes start
	private String oldPIFlag;
	
	public String getOldPIFlag()
	{
		return oldPIFlag;
	}
	public void setOldPIFlag(String oldPIFlag)
	{
		this.oldPIFlag = oldPIFlag;
	}
	//R7: program Income changes ends
	/**
	 * @return the approvedDate
	 */
	public String getApprovedDate()
	{
		return approvedDate;
	}
	/**
	 * @param approvedDate the approvedDate to set
	 */
	public void setApprovedDate(String approvedDate)
	{
		this.approvedDate = approvedDate;
	}
	/**
	 * @return the usesFte
	 */	
	public String getUsesFte()
	{
		return usesFte;
	}
	/**
	 * @param usesFte the usesFte to set
	 */
	public void setUsesFte(String usesFte)
	{
		this.usesFte = usesFte;
	}
	/**
	 * @return the programId
	 */
	public String getProgramId()
	{
		return programId;
	}
	/**
	 * @param programId the programId to set
	 */
	public void setProgramId(String programId)
	{
		this.programId = programId;
	}
	/**
	 * @return the returnedPaymentAmount
	 */
	public String getReturnedPaymentAmount() {
		return returnedPaymentAmount;
	}
	/**
	 * @return the noOfReturnedPayments
	 */
	public int getNoOfReturnedPayments() {
		return noOfReturnedPayments;
	}
	/**
	 * @param noOfReturnedPayments the noOfReturnedPayments to set
	 */
	public void setNoOfReturnedPayments(int noOfReturnedPayments) {
		this.noOfReturnedPayments = noOfReturnedPayments;
	}
	/**
	 * @param returnedPaymentAmount the returnedPaymentAmount to set
	 */
	public void setReturnedPaymentAmount(String returnedPaymentAmount) {
		this.returnedPaymentAmount = returnedPaymentAmount;
	}
	/**
	 * @return the existingBudget
	 */	
	public String getExistingBudget()
	{
		return existingBudget;
	}
	/**
	 * @param existingBudget the existingBudget to set
	 */
	public void setExistingBudget(String existingBudget)
	{
		this.existingBudget = existingBudget;
	}
	// End : R6 Added
		/**
	 * @return the invoiceCount
	 */
	public String getInvoiceCount()
	{
		return invoiceCount;
	}

	/**
	 * @param invoiceCount the invoiceCount to set
	 */
	public void setInvoiceCount(String invoiceCount)
	{
		this.invoiceCount = invoiceCount;
	}

	/**
	 * @return the paymentCount
	 */
	public String getPaymentCount()
	{
		return paymentCount;
	}

	/**
	 * @param paymentCount the paymentCount to set
	 */
	public void setPaymentCount(String paymentCount)
	{
		this.paymentCount = paymentCount;
	}

	/**
	 * @return the budgetExistCount
	 */
	public String getBudgetCount()
	{
		return budgetCount;
	}

	/**
	 * @param budgetExistCount the budgetExistCount to set
	 */
	public void setBudgetCount(String budgetCount)
	{
		this.budgetCount = budgetCount;
	}

	/**
	 * @return the providerAdmin
	 */
	public String getProviderAdmin()
	{
		return providerAdmin;
	}

	/**
	 * @param providerAdmin the providerAdmin to set
	 */
	public void setProviderAdmin(String providerAdmin)
	{
		this.providerAdmin = providerAdmin;
	}

	// End : R5 Added
	/**
	 * @return amendEpin
	 */
	public String getAmendEpin()
	{
		return amendEpin;
	}

	/**
	 * @param amendEpin
	 */
	public void setAmendEpin(String amendEpin)
	{
		this.amendEpin = amendEpin;
	}

	/**
	 * @return the providerId
	 */
	public String getProviderId()
	{
		return providerId;
	}

	/**
	 * @param providerId the providerId to set
	 */
	public void setProviderId(String providerId)
	{
		this.providerId = providerId;
	}

	/**
	 * @return boolean
	 */
	public Boolean getIsViewCOF()
	{
		return isViewCOF;
	}

	/**
	 * @param isViewCOF
	 */
	public void setIsViewCOF(Boolean isViewCOF)
	{
		this.isViewCOF = isViewCOF;
	}

	public void setDefaultSortData()
	{
		setFirstSort(HHSConstants.STATUS_PROCESS_TYPE_ID);
		setSecondSort(HHSConstants.LAST_UPDATE_DATE);
		setFirstSortDate(true);
		setSecondSortDate(true);
		setFirstSortType(HHSConstants.ASCENDING);
		setSecondSortType(HHSConstants.DESCENDING);
		setSortColumnName(HHSConstants.CONTRACT_STATUS);
	}

	/**
	 * @return the procurementTitle
	 */
	public String getProcurementTitle()
	{
		return procurementTitle;
	}

	/**
	 * @param procurementTitle the procurementTitle to set
	 */
	public void setProcurementTitle(String procurementTitle)
	{
		this.procurementTitle = procurementTitle;
	}

	/**
	 * @return boolean
	 */
	public boolean isFilterClicked()
	{
		return filterClicked;
	}

	/**
	 * @param filterClicked
	 */
	public void setFilterClicked(boolean filterClicked)
	{
		this.filterClicked = filterClicked;
	}

	/**
	 * @return string
	 */
	public String getOrgName()
	{
		return orgName;
	}

	/**
	 * @param orgName
	 */
	public void setOrgName(String orgName)
	{
		this.orgName = orgName;
	}

	/**
	 * @return string
	 */
	public String getOrgType()
	{
		return orgType;
	}

	/**
	 * @param orgType
	 */
	public void setOrgType(String orgType)
	{
		this.orgType = orgType;
	}

	/**
	 * @return string
	 */
	public String getContractStatusId()
	{
		return contractStatusId;
	}

	/**
	 * @param contractStatusId
	 */
	public void setContractStatusId(String contractStatusId)
	{
		this.contractStatusId = contractStatusId;
	}

	/**
	 * @return string
	 */
	public String getContractSourceId()
	{
		return contractSourceId;
	}

	/**
	 * @param contractSourceId
	 */
	public void setContractSourceId(String contractSourceId)
	{
		this.contractSourceId = contractSourceId;
	}

	/**
	 * @return string
	 */
	public String getBudgetType()
	{
		return budgetType;
	}

	/**
	 * @param budgetType
	 */
	public void setBudgetType(String budgetType)
	{
		this.budgetType = budgetType;
	}

	/**
	 * @return string
	 */
	public String getStatusIdAmendErrorMsg()
	{
		return statusIdAmendErrorMsg;
	}

	/**
	 * @param statusIdAmendErrorMsg
	 */
	public void setStatusIdAmendErrorMsg(String statusIdAmendErrorMsg)
	{
		this.statusIdAmendErrorMsg = statusIdAmendErrorMsg;
	}

	/**
	 * @return string
	 */
	public String getContractId()
	{
		return contractId;
	}

	/**
	 * @param contractId
	 */
	public void setContractId(String contractId)
	{
		this.contractId = contractId;
	}

	/**
	 * @return boolean
	 */
	public boolean isFirstLoad()
	{
		return firstLoad;
	}

	/**
	 * @param firstLoad
	 */
	public void setFirstLoad(boolean firstLoad)
	{
		this.firstLoad = firstLoad;
	}

	/**
	 * @return list
	 */
	public List<String> getContractStatusList()
	{
		return contractStatusList;
	}

	/**
	 * @param contractStatusList
	 */
	public void setContractStatusList(List<String> contractStatusList)
	{
		this.contractStatusList = contractStatusList;
	}

	/**
	 * @return string
	 */
	public String getContractTitle()
	{
		return contractTitle;
	}

	/**
	 * @param contractTitle
	 */
	public void setContractTitle(String contractTitle)
	{
		this.contractTitle = contractTitle;
	}

	/**
	 * @return string
	 */
	public String getProgramName()
	{
		return programName;
	}

	public void setProgramName(String programName)
	{
		this.programName = programName;
	}

	public String getContractAgencyName()
	{
		return contractAgencyName;
	}

	public void setContractAgencyName(String contractAgencyName)
	{
		this.contractAgencyName = contractAgencyName;
	}

	public boolean isPendingAmendment()
	{
		return pendingAmendment;
	}

	public void setPendingAmendment(boolean pendingAmendment)
	{
		this.pendingAmendment = pendingAmendment;
	}

	public String getProvider()
	{
		return provider;
	}

	public void setProvider(String provider)
	{
		this.provider = provider;
	}

	public String getCtId()
	{
		return ctId;
	}

	public void setCtId(String ctId)
	{
		this.ctId = ctId;
	}

	public String getAwardEpin()
	{
		return awardEpin;
	}

	public void setAwardEpin(String awardEpin)
	{
		this.awardEpin = awardEpin;
	}

	public String getContractValueFrom()
	{
		return contractValueFrom;
	}

	public void setContractValueFrom(String contractValueFrom)
	{
		this.contractValueFrom = contractValueFrom;
	}

	public String getContractValueTo()
	{
		return contractValueTo;
	}

	public void setContractValueTo(String contractValueTo)
	{
		this.contractValueTo = contractValueTo;
	}

	public String getContractStatus()
	{
		return contractStatus;
	}

	public void setContractStatus(String contractStatus)
	{
		this.contractStatus = contractStatus;
	}

	public Date getContractStartDate() throws ApplicationException
	{
		return contractStartDate;
	}

	public void setContractStartDate(Date contractStartDate)
	{
		this.contractStartDate = contractStartDate;
	}

	public Date getContractEndDate() throws ApplicationException
	{
		return contractEndDate;
	}

	public void setContractEndDate(Date contractEndDate)
	{
		this.contractEndDate = contractEndDate;
	}

	public String getNewTotalContractAmount()
	{
		return newTotalContractAmount;
	}

	public void setNewTotalContractAmount(String newTotalContractAmount)
	{
		this.newTotalContractAmount = newTotalContractAmount;
	}

	public String getActions()
	{
		return actions;
	}

	public void setActions(String actions)
	{
		this.actions = actions;
	}

	public String getAmendAmount()
	{
		return amendAmount;
	}

	public void setAmendAmount(String amendAmount)
	{
		this.amendAmount = amendAmount;
	}

	public Date getAmendStartDate()
	{
		return amendStartDate;
	}

	public void setAmendStartDate(Date amendStartDate)
	{
		this.amendStartDate = amendStartDate;
	}

	public Date getAmendEndDate()
	{
		return amendEndDate;
	}

	public void setAmendEndDate(Date amendEndDate)
	{
		this.amendEndDate = amendEndDate;
	}

	public String getAmendReason()
	{
		return amendReason;
	}

	public void setAmendReason(String amendReason)
	{
		this.amendReason = amendReason;
	}

	public String getContractValue()
	{
		return contractValue;
	}

	public void setContractValue(String contractValue)
	{
		this.contractValue = contractValue;
	}

	public String getLastUpdateDate()
	{
		return lastUpdateDate;
	}

	public void setLastUpdateDate(String lastUpdateDate)
	{
		this.lastUpdateDate = lastUpdateDate;
	}

	/**
	 * @return the modifyBy
	 */
	public String getModifyBy()
	{
		return modifyBy;
	}

	/**
	 * @param modifyBy the modifyBy to set
	 */
	public void setModifyBy(String modifyBy)
	{
		this.modifyBy = modifyBy;
	}

	/**
	 * @return the contractTypeId
	 */
	public String getContractTypeId()
	{
		return contractTypeId;
	}

	/**
	 * @param contractTypeId the contractTypeId to set
	 */
	public void setContractTypeId(String contractTypeId)
	{
		this.contractTypeId = contractTypeId;
	}

	/**
	 * @return the statusCheck
	 */
	public String getStatusCheck()
	{
		return statusCheck;
	}

	/**
	 * @param statusCheck the statusCheck to set
	 */
	public void setStatusCheck(String statusCheck)
	{
		this.statusCheck = statusCheck;
	}

	/**
	 * @return the workFlowId
	 */
	public String getWorkFlowId()
	{
		return workFlowId;
	}

	/**
	 * @param workFlowId the workFlowId to set
	 */
	public void setWorkFlowId(String workFlowId)
	{
		this.workFlowId = workFlowId;
	}

	/**
	 * @return the budgetTypeId
	 */
	public String getBudgetTypeId()
	{
		return budgetTypeId;
	}

	/**
	 * @param budgetTypeId the budgetTypeId to set
	 */
	public void setBudgetTypeId(String budgetTypeId)
	{
		this.budgetTypeId = budgetTypeId;
	}

	/**
	 * @return the budgetStatusId
	 */
	public String getBudgetStatusId()
	{
		return budgetStatusId;
	}

	/**
	 * @param budgetStatusId the budgetStatusId to set
	 */
	public void setBudgetStatusId(String budgetStatusId)
	{
		this.budgetStatusId = budgetStatusId;
	}

	/**
	 * @return the taskStatus
	 */
	public String getTaskStatus()
	{
		return taskStatus;
	}

	/**
	 * @param taskStatus the taskStatus to set
	 */
	public void setTaskStatus(String taskStatus)
	{
		this.taskStatus = taskStatus;
	}

	/**
	 * @return the procStatusIds
	 */
	public String getProcStatusIds()
	{
		return procStatusIds;
	}

	/**
	 * @param procStatusIds the procStatusIds to set
	 */
	public void setProcStatusIds(String procStatusIds)
	{
		this.procStatusIds = procStatusIds;
	}

	public Boolean getIsNewFYConfigPending()
	{
		return isNewFYConfigPending;
	}

	public void setIsNewFYConfigPending(Boolean isNewFYConfigPending)
	{
		this.isNewFYConfigPending = isNewFYConfigPending;
	}

	/**
	 * @return the procEpin
	 */
	public String getProcEpin()
	{
		return procEpin;
	}

	/**
	 * @param procEpin the procEpin to set
	 */
	public void setProcEpin(String procEpin)
	{
		this.procEpin = procEpin;
	}

	/**
	 * @return the extCT
	 */
	public String getExtCT()
	{
		return extCT;
	}

	/**
	 * @param extCT the extCT to set
	 */
	public void setExtCT(String extCT)
	{
		this.extCT = extCT;
	}

	public String getFyConfigFiscalYear()
	{
		return fyConfigFiscalYear;
	}

	public void setFyConfigFiscalYear(String fyConfigFiscalYear)
	{
		this.fyConfigFiscalYear = fyConfigFiscalYear;
	}

	public String getAgencyId()
	{
		return agencyId;
	}

	public void setAgencyId(String agencyId)
	{
		this.agencyId = agencyId;
	}

	/**
	 * @return the budgetStatus
	 */
	public String getBudgetStatus()
	{
		return budgetStatus;
	}

	/**
	 * @param budgetStatus the budgetStatus to set
	 */
	public void setBudgetStatus(String budgetStatus)
	{
		this.budgetStatus = budgetStatus;
	}

	/**
	 * @return the budgetId
	 */
	public String getBudgetId()
	{
		return budgetId;
	}

	/**
	 * @param budgetId the budgetId to set
	 */
	public void setBudgetId(String budgetId)
	{
		this.budgetId = budgetId;
	}

	/**
	 * @return the invoiceId
	 */
	public String getInvoiceId()
	{
		return invoiceId;
	}

	/**
	 * @param invoiceId the invoiceId to set
	 */
	public void setInvoiceId(String invoiceId)
	{
		this.invoiceId = invoiceId;
	}

	/**
	 * @return the invoiceStatusId
	 */
	public String getInvoiceStatusId()
	{
		return invoiceStatusId;
	}

	/**
	 * @param invoiceStatusId the invoiceStatusId to set
	 */
	public void setInvoiceStatusId(String invoiceStatusId)
	{
		this.invoiceStatusId = invoiceStatusId;
	}

	public void setCertFundsRequired(String certFundsRequired)
	{
		this.certFundsRequired = certFundsRequired;
	}

	public String getCertFundsRequired()
	{
		return certFundsRequired;
	}

	public void setParentContractId(String parentContractId)
	{
		this.parentContractId = parentContractId;
	}

	public String getParentContractId()
	{
		return parentContractId;
	}

	public void setAlreadyRenewed(boolean alreadyRenewed)
	{
		this.alreadyRenewed = alreadyRenewed;
	}

	public boolean isAlreadyRenewed()
	{
		return alreadyRenewed;
	}

	public void setProviderOrgId(String providerOrgId)
	{
		this.providerOrgId = providerOrgId;
	}

	public String getProviderOrgId()
	{
		return providerOrgId;
	}

	public void setSuspended(boolean isSuspended)
	{
		this.isSuspended = isSuspended;
	}

	public boolean isSuspended()
	{
		return isSuspended;
	}

	/**
	 * @param baseContractTitle
	 */
	public void setBaseContractTitle(String baseContractTitle)
	{
		this.baseContractTitle = baseContractTitle;
	}

	/**
	 * @return string
	 */
	public String getBaseContractTitle()
	{
		return baseContractTitle;
	}

	/**
	 * @param dateLastUpdateFrom
	 */
	public void setDateLastUpdateFrom(String dateLastUpdateFrom)
	{
		this.dateLastUpdateFrom = dateLastUpdateFrom;
	}

	/**
	 * @return string
	 */
	public String getDateLastUpdateFrom()
	{
		return dateLastUpdateFrom;
	}

	/**
	 * @param dateLastUpdateTo
	 */
	public void setDateLastUpdateTo(String dateLastUpdateTo)
	{
		this.dateLastUpdateTo = dateLastUpdateTo;
	}

	/**
	 * @return string
	 */
	public String getDateLastUpdateTo()
	{
		return dateLastUpdateTo;
	}

	public void setContractUpdate(boolean contractUpdate)
	{
		this.contractUpdate = contractUpdate;
	}

	public boolean isContractUpdate()
	{
		return contractUpdate;
	}

	/**
	 * @return the amendmentContractId
	 */
	public String getAmendmentContractId()
	{
		return amendmentContractId;
	}

	/**
	 * @param amendmentContractId the amendmentContractId to set
	 */
	public void setAmendmentContractId(String amendmentContractId)
	{
		this.amendmentContractId = amendmentContractId;
	}

	@Override
	public String toString()
	{
		return "ContractList [" + (contractTitle != null ? "contractTitle=" + contractTitle + ", " : "")
				+ (baseContractTitle != null ? "baseContractTitle=" + baseContractTitle + ", " : "")
				+ (procurementTitle != null ? "procurementTitle=" + procurementTitle + ", " : "")
				+ (programName != null ? "programName=" + programName + ", " : "")
				+ (contractAgencyName != null ? "contractAgencyName=" + contractAgencyName + ", " : "")
				+ (provider != null ? "provider=" + provider + ", " : "") + (ctId != null ? "ctId=" + ctId + ", " : "")
				+ (awardEpin != null ? "awardEpin=" + awardEpin + ", " : "")
				+ (procEpin != null ? "procEpin=" + procEpin + ", " : "")
				+ (extCT != null ? "extCT=" + extCT + ", " : "")
				+ (contractValueFrom != null ? "contractValueFrom=" + contractValueFrom + ", " : "")
				+ (contractValueTo != null ? "contractValueTo=" + contractValueTo + ", " : "")
				+ (contractStatus != null ? "contractStatus=" + contractStatus + ", " : "")
				+ (contractStatusId != null ? "contractStatusId=" + contractStatusId + ", " : "")
				+ (orgName != null ? "orgName=" + orgName + ", " : "")
				+ (orgType != null ? "orgType=" + orgType + ", " : "")
				+ (contractStatusList != null ? "contractStatusList=" + contractStatusList + ", " : "")
				+ (contractStartDate != null ? "contractStartDate=" + contractStartDate + ", " : "")
				+ (contractEndDate != null ? "contractEndDate=" + contractEndDate + ", " : "")
				+ (contractValue != null ? "contractValue=" + contractValue + ", " : "")
				+ (lastUpdateDate != null ? "lastUpdateDate=" + lastUpdateDate + ", " : "")
				+ (amendAmount != null ? "amendAmount=" + amendAmount + ", " : "")
				+ (amendStartDate != null ? "amendStartDate=" + amendStartDate + ", " : "")
				+ (amendEndDate != null ? "amendEndDate=" + amendEndDate + ", " : "")
				+ (amendReason != null ? "amendReason=" + amendReason + ", " : "")
				+ (newTotalContractAmount != null ? "newTotalContractAmount=" + newTotalContractAmount + ", " : "")
				+ (actions != null ? "actions=" + actions + ", " : "")
				+ (contractId != null ? "contractId=" + contractId + ", " : "")
				+ (amendmentContractId != null ? "amendmentContractId=" + amendmentContractId + ", " : "")
				+ (budgetType != null ? "budgetType=" + budgetType + ", " : "")
				+ (statusIdAmendErrorMsg != null ? "statusIdAmendErrorMsg=" + statusIdAmendErrorMsg + ", " : "")
				+ "firstLoad=" + firstLoad + ", pendingAmendment=" + pendingAmendment + ", isSuspended=" + isSuspended
				+ ", alreadyRenewed=" + alreadyRenewed + ", contractUpdate=" + contractUpdate + ", filterClicked="
				+ filterClicked + ", " + (modifyBy != null ? "modifyBy=" + modifyBy + ", " : "")
				+ (contractTypeId != null ? "contractTypeId=" + contractTypeId + ", " : "")
				+ (contractSourceId != null ? "contractSourceId=" + contractSourceId + ", " : "")
				+ (workFlowId != null ? "workFlowId=" + workFlowId + ", " : "")
				+ (budgetTypeId != null ? "budgetTypeId=" + budgetTypeId + ", " : "")
				+ (budgetStatusId != null ? "budgetStatusId=" + budgetStatusId + ", " : "")
				+ (budgetStatus != null ? "budgetStatus=" + budgetStatus + ", " : "")
				+ (taskStatus != null ? "taskStatus=" + taskStatus + ", " : "")
				+ (procStatusIds != null ? "procStatusIds=" + procStatusIds + ", " : "")
				+ (statusCheck != null ? "statusCheck=" + statusCheck + ", " : "")
				+ (isNewFYConfigPending != null ? "isNewFYConfigPending=" + isNewFYConfigPending + ", " : "")
				+ (fyConfigFiscalYear != null ? "fyConfigFiscalYear=" + fyConfigFiscalYear + ", " : "")
				+ (agencyId != null ? "agencyId=" + agencyId + ", " : "")
				+ (budgetId != null ? "budgetId=" + budgetId + ", " : "")
				+ (invoiceId != null ? "invoiceId=" + invoiceId + ", " : "")
				+ (invoiceStatusId != null ? "invoiceStatusId=" + invoiceStatusId + ", " : "")
				+ (certFundsRequired != null ? "certFundsRequired=" + certFundsRequired + ", " : "")
				+ (parentContractId != null ? "parentContractId=" + parentContractId + ", " : "")
				+ (isViewCOF != null ? "isViewCOF=" + isViewCOF + ", " : "")
				+ (providerId != null ? "providerId=" + providerId + ", " : "")
				+ (providerOrgId != null ? "providerOrgId=" + providerOrgId + ", " : "")
				+ (dateLastUpdateFrom != null ? "dateLastUpdateFrom=" + dateLastUpdateFrom + ", " : "")
				+ (dateLastUpdateTo != null ? "dateLastUpdateTo=" + dateLastUpdateTo + ", " : "")
				+ (amendEpin != null ? "amendEpin=" + amendEpin + ", " : "")
				+ (invoiceCount != null ? "invoiceCount=" + invoiceCount + ", " : "")
				+ (paymentCount != null ? "paymentCount=" + paymentCount + ", " : "")
				+ (budgetCount != null ? "budgetCount=" + budgetCount + ", " : "")
				+ (providerAdmin != null ? "providerAdmin=" + providerAdmin + ", " : "")
				+ (usesFte != null ? "usesFte=" + usesFte + ", " : "")
				+ (approvedDate != null ? "approvedDate=" + approvedDate + ", " : "")
				+ (existingBudget != null ? "existingBudget=" + existingBudget + ", " : "")
				+ (returnedPaymentAmount != null ? "returnedPaymentAmount=" + returnedPaymentAmount + ", " : "")
				+ "noOfReturnedPayments=" + noOfReturnedPayments + ", "
				+ (programId != null ? "programId=" + programId + ", " : "")
				+ "needAmendRemoved "+ needAmendRemoved+ ", "
				+ "markAsFmsRegistered "+ markAsFmsRegistered +", "
				+ "negativeAmend : "+ negativeAmend +", "
				+ "amendBudgetApprovedCount : "+ amendBudgetApprovedCount +", "
				+ (oldPIFlag != null ? "oldPIFlag=" + oldPIFlag : "") + ", userSubRole=" + userSubRole +"]";
	}

}
