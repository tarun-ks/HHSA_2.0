package com.nyc.hhs.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.constants.HHSR5Constants;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/**
 * Bean to be used for render of budgetlist Updated in Release 6:Added
 * properties invoiceSuspendedStatus,unRecoupAmount,requestNotificationId for
 * return payment This Bean is updated in R7.
 */
public class BudgetList extends BaseFilter {
    private String  contractTitle;
    private String  budgetValue;
    private String  dateOfLastUpdate;
    private String  status;
    private String  actions;
    private String  budgetType;
    private String  agency;
    @RegExp(value ="^\\d{0,22}")
    private String  budgetId;
    @RegExp(value ="^\\d{0,22}")
    private String  contractId;
    private String  providerName;
    private String  agencyName;
    @Length(max = 20)
    private String  orgId;
    private String  statusId;
    private String  orgType;
    private String  lsRequestFromHomePage;
    private Boolean lsInvoiceBudgetStatus;
    private Boolean IsFandFP                            = false;
    private Integer amendAmount;
    private String  userId;
    private Integer programId;
    private String  providerId;
    private String  amendmentContractId;

    /* Start :R5 Added */
    private String  invoiceCount;
    private String  paymentCount;
    private String  amendmentCount;
    // Added in Release 6: Return payment
    private String  invoiceSuspendedStatus;
    private String  unRecoupAmount;
    private String  requestNotificationId;
    // Added in Release 7
    private String  autoApprovedFlag;
    private Boolean filterModificationBudgetRedirection = false;
    private String  costCenterOpted;
    
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

	//<!-- [Start] R9.7.8 QC9742 -->
	private String invoiceAdvOnly ;
	public String getInvoiceAdvOnly() {
        return invoiceAdvOnly;
    }
    public void setInvoiceAdvOnly(String invoiceAdvOnly) {
        this.invoiceAdvOnly = invoiceAdvOnly;
    }
    public boolean getInvoiceActionSetting() {
    	if( invoiceAdvOnly != null && invoiceAdvOnly.length() == 2 ) {
	    	if( invoiceAdvOnly.equalsIgnoreCase("11")
		    		|| invoiceAdvOnly.equalsIgnoreCase("10") ) return true;
	    	else return false;
    	} else {
    		return false;
    	}
    }
    public boolean getAdvanceActionSetting() {
    	if( invoiceAdvOnly != null  && invoiceAdvOnly.length() == 2 ) {
	    	if( invoiceAdvOnly.equalsIgnoreCase("11")
	    		|| invoiceAdvOnly.equalsIgnoreCase("01") ) return true;
	    	else return false;
    	} else {
    		return false;
    	}
    }
	//<!-- [End] R9.7.8 QC9742 -->
    
    /* [Start] R7.7.0 QC9149 Add indicator for Access control */
    private Integer  negativeAmendCnt;
    
    public Integer getNegativeAmendCnt() {
        return negativeAmendCnt;
    }

    public void setNegativeAmendCnt(Integer negativeAmendCnt) {
        this.negativeAmendCnt = negativeAmendCnt;
    }
    /* [End] R7.7.0 QC9149 Add indicator for Access control */

    /* [Start] R 8.4 QC9490 Add indicator for Delete Budget Update Access control */
    private Integer  deleteBudgetUpdateFlag;
    
    
    public Integer getDeleteBudgetUpdateFlag() {
		return deleteBudgetUpdateFlag;
	}

	public void setDeleteBudgetUpdateFlag(Integer deleteBudgetUpdateFlag) {
		this.deleteBudgetUpdateFlag = deleteBudgetUpdateFlag;
	}
	/* [End] R 8.4 QC9490 Add indicator for Delete Budget Update Access control */
	
	/* [Start] R7.4.0 QC9008 Add indicator for Access control */
    private String  ctNumber  = "";
    private String  ePin  = "";

    public String getCtNumber() {
        return ctNumber;
    }

    public void setCtNumber(String ctNumber) {
        this.ctNumber = ctNumber;
    }

    public String getePin() {
        return ePin;
    }

    public void setePin(String ePin) {
        this.ePin = ePin;
    }
    /* [End] R7.4.0 QC9008 Add indicator for Access control */

    /* [Start] R7.2.0 QC8914 Add indicator for Access control */
    private String  userSubRole                         = "";

    public String getUserSubRole() {
        return userSubRole;
    }

    public void setUserSubRole(String userSubRole) {
        this.userSubRole = userSubRole;
    }
    /* [End] R7.2.0 QC8914 Add indicator for Access control */

    public String getAutoApprovedFlag() {
        return autoApprovedFlag;
    }

    public void setAutoApprovedFlag(String autoApprovedFlag) {
        this.autoApprovedFlag = autoApprovedFlag;
    }

    public String getCostCenterOpted() {
        return costCenterOpted;
    }

    public void setCostCenterOpted(String costCenterOpted) {
        this.costCenterOpted = costCenterOpted;
    }

    public Boolean getFilterModificationBudgetRedirection() {
        return filterModificationBudgetRedirection;
    }

    public void setFilterModificationBudgetRedirection(
            Boolean filterModificationBudgetRedirection) {
        this.filterModificationBudgetRedirection = filterModificationBudgetRedirection;
    }

    private String mergeOutYearFlag = HHSR5Constants.ZERO;

    public String getMergeOutYearFlag() {
        return mergeOutYearFlag;
    }

    public void setMergeOutYearFlag(String mergeOutYearFlag) {
        this.mergeOutYearFlag = mergeOutYearFlag;
    }

    // Added in R7
    private String parentId;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    // End R7
    public String getRequestNotificationId() {
        return requestNotificationId;
    }

    public void setRequestNotificationId(String requestNotificationId) {
        this.requestNotificationId = requestNotificationId;
    }

    public String getUnRecoupAmount() {
        return unRecoupAmount;
    }

    public void setUnRecoupAmount(String unRecoupAmount) {
        this.unRecoupAmount = unRecoupAmount;
    }

    // Added in Release 6: Return payment end
    /**
     * @return the invoiceCount
     */
    public String getInvoiceCount() {
        return invoiceCount;
    }

    /**
     * @param invoiceCount
     *            the invoiceCount to set
     */
    public void setInvoiceCount(String invoiceCount) {
        this.invoiceCount = invoiceCount;
    }

    /**
     * @return the paymentCount
     */
    public String getPaymentCount() {
        return paymentCount;
    }

    /**
     * @param paymentCount
     *            the paymentCount to set
     */
    public void setPaymentCount(String paymentCount) {
        this.paymentCount = paymentCount;
    }

    /**
     * @return the amendmentCount
     */
    public String getAmendmentCount() {
        return amendmentCount;
    }

    /**
     * @param amendmentCount
     *            the amendmentCount to set
     */
    public void setAmendmentCount(String amendmentCount) {
        this.amendmentCount = amendmentCount;
    }

    /* End :R5 Added */
    /**
     * @return the providerId
     */
    public String getProviderId() {
        return providerId;
    }

    /**
     * @param providerId
     *            the providerId to set
     */
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    /**
     * @return the programId
     */
    public Integer getProgramId() {
        return programId;
    }

    /**
     * @param programId
     *            the programId to set
     */
    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    /**
     * @return the amendAmount
     */
    public Integer getAmendAmount() {
        return amendAmount;
    }

    /**
     * @param amendAmount
     *            the amendAmount to set
     */
    public void setAmendAmount(Integer amendAmount) {
        this.amendAmount = amendAmount;
    }

    /**
     * @return the lsInvoiceBudgetStatus
     */
    public Boolean getLsInvoiceBudgetStatus() {
        return lsInvoiceBudgetStatus;
    }

    /**
     * @param lsInvoiceBudgetStatus
     *            the lsInvoiceBudgetStatus to set
     */
    public void setLsInvoiceBudgetStatus(Boolean lsInvoiceBudgetStatus) {
        this.lsInvoiceBudgetStatus = lsInvoiceBudgetStatus;
    }

    /**
     * @return the lsRequestFromHomePage
     */
    public String getLsRequestFromHomePage() {
        return lsRequestFromHomePage;
    }

    /**
     * @param lsRequestFromHomePage
     *            the lsRequestFromHomePage to set
     */
    public void setLsRequestFromHomePage(String lsRequestFromHomePage) {
        this.lsRequestFromHomePage = lsRequestFromHomePage;
    }

    // Date Formatter
    SimpleDateFormat     formatter        = new SimpleDateFormat(
                                                  HHSConstants.MMDDYY_FORMAT_2);

    // Used for Filter Screen
    private String       agencyId;
    private String       programName;
    private String       fiscalYearId;
    private String       dateLastUpdateFrom;
    private String       dateLastUpdateTo;
    private String       budgetValueFrom;
    private String       budgetValueTo;
    private List<String> budgetStatusList = new ArrayList<String>();
    private List<String> budgetTypeList   = new ArrayList<String>();
    private String       awardEpin;
    private Boolean      isFilter         = false;
    private String       fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    private String ctId;
    private String fiscalYear;

    public BudgetList() {
        // No Action Required
    }

    public BudgetList(String asOrgnizationType) {
        setDefaultSortData(asOrgnizationType);
    }

    public void setDefaultSortData(String asOrgnizationType) {
        if (asOrgnizationType != null
                && asOrgnizationType
                        .equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG)) {
            setFirstSort(HHSConstants.STATUS);
            setSecondSort(HHSConstants.LAST_UPDATE_DATE);
            setSecondSortDate(true);
            setFirstSortType(HHSConstants.ASCENDING);
            setSecondSortType(HHSConstants.ASCENDING);
            setSortColumnName(HHSConstants.STATUS_COLUMN);
        } else {
            setFirstSort(HHSConstants.BUD_DOT.concat(HHSConstants.STATUS));
            setSecondSort(HHSConstants.LAST_UPDATE_DATE);
            setSecondSortDate(true);
            setFirstSortType(HHSConstants.ASCENDING);
            setSecondSortType(HHSConstants.ASCENDING);
            setSortColumnName(HHSConstants.STATUS_COLUMN);
        }

    }

    public void setDefaultSortData() {
        setFirstSort(HHSConstants.BUD_DOT.concat(HHSConstants.STATUS));
        setSecondSort(HHSConstants.LAST_UPDATE_DATE);
        setSecondSortDate(true);
        setFirstSortType(HHSConstants.ASCENDING);
        setSecondSortType(HHSConstants.ASCENDING);
        setSortColumnName(HHSConstants.STATUS_COLUMN);
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public SimpleDateFormat getFormatter() {
        return formatter;
    }

    public void setFormatter(SimpleDateFormat formatter) {
        this.formatter = formatter;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getContractTitle() {
        return contractTitle;
    }

    public void setContractTitle(String contractTitle) {
        this.contractTitle = contractTitle;
    }

    public String getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(String budgetId) {
        this.budgetId = budgetId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getBudgetValue() {
        return budgetValue;
    }

    public void setBudgetValue(String budgetValue) {
        this.budgetValue = budgetValue;
    }

    public String getDateOfLastUpdate() {
        return dateOfLastUpdate;
    }

    public void setDateOfLastUpdate(String dateOfLastUpdate) {
        this.dateOfLastUpdate = dateOfLastUpdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getActions() {
        return actions;
    }

    public void setActions(String actions) {
        this.actions = actions;
    }

    public String getBudgetType() {
        return budgetType;
    }

    public void setBudgetType(String budgetType) {
        this.budgetType = budgetType;
    }

    public String getAgency() {
        return agency;
    }

    public void setAgency(String agency) {
        this.agency = agency;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getBudgetValueFrom() {
        return budgetValueFrom;
    }

    public void setBudgetValueFrom(String budgetValueFrom) {
        this.budgetValueFrom = budgetValueFrom;
    }

    public String getBudgetValueTo() {
        return budgetValueTo;
    }

    public void setBudgetValueTo(String budgetValueTo) {
        this.budgetValueTo = budgetValueTo;
    }

    public List<String> getBudgetStatusList() {
        return budgetStatusList;
    }

    public void setBudgetStatusList(List<String> budgetStatusList) {
        this.budgetStatusList = budgetStatusList;
    }

    public String getFiscalYearId() {
        return fiscalYearId;
    }

    public void setFiscalYearId(String fiscalYearId) {
        this.fiscalYearId = fiscalYearId;
    }

    public String getDateLastUpdateFrom() {
        return dateLastUpdateFrom;
    }

    public void setDateLastUpdateFrom(String dateLastUpdateFrom) {
        this.dateLastUpdateFrom = dateLastUpdateFrom;
    }

    public String getDateLastUpdateTo() {
        return dateLastUpdateTo;
    }

    public void setDateLastUpdateTo(String dateLastUpdateTo) {
        this.dateLastUpdateTo = dateLastUpdateTo;
    }

    public String getCtId() {
        return ctId;
    }

    public void setCtId(String ctId) {
        this.ctId = ctId;
    }

    public String getAwardEpin() {
        return awardEpin;
    }

    public void setAwardEpin(String awardEpin) {
        this.awardEpin = awardEpin;
    }

    public List<String> getBudgetTypeList() {
        return budgetTypeList;
    }

    public void setBudgetTypeList(List<String> budgetTypeList) {
        this.budgetTypeList = budgetTypeList;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public Boolean getIsFilter() {
        return isFilter;
    }

    public void setIsFilter(Boolean isFilter) {
        this.isFilter = isFilter;
    }

    @Override
    public String toString() {
        return "BudgetList [contractTitle=" + contractTitle + ", budgetValue="
                + budgetValue + ", dateOfLastUpdate=" + dateOfLastUpdate
                + ", status=" + status + ", actions=" + actions
                + ", budgetType=" + budgetType + ", agency=" + agency
                + ", budgetId=" + budgetId + ", contractId=" + contractId
                + ", providerName=" + providerName + ", agencyName="
                + agencyName + ", orgId=" + orgId + ", statusId=" + statusId
                + ", orgType=" + orgType + ", lsRequestFromHomePage="
                + lsRequestFromHomePage + ", lsInvoiceBudgetStatus="
                + lsInvoiceBudgetStatus + ", amendAmount=" + amendAmount
                + ", programId=" + programId + ", providerId=" + providerId
                + ", formatter=" + formatter + ", agencyId=" + agencyId
                + ", programName=" + programName + ", fiscalYearId="
                + fiscalYearId + ", dateLastUpdateFrom=" + dateLastUpdateFrom
                + ", dateLastUpdateTo=" + dateLastUpdateTo
                + ", budgetValueFrom=" + budgetValueFrom + ", budgetValueTo="
                + budgetValueTo + ", budgetStatusList=" + budgetStatusList
                + ", budgetTypeList=" + budgetTypeList + ", awardEpin="
                + awardEpin + ", isFilter=" + isFilter + ", ctId=" + ctId
                + ", negativeAmendCnt = "+negativeAmendCnt 
                + ", deleteBudgetUpdateFlag = "+ deleteBudgetUpdateFlag   
                + ", fiscalYear=" + fiscalYear + ", userSubRole=" + userSubRole + "]";
    }

    public void setIsFandFP(Boolean isFandFP) {
        IsFandFP = isFandFP;
    }

    public Boolean getIsFandFP() {
        return IsFandFP;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public String getAmendmentContractId() {
        return amendmentContractId;
    }

    public void setAmendmentContractId(String amendmentContractId) {
        this.amendmentContractId = amendmentContractId;
    }

    public String getInvoiceSuspendedStatus() {
        return invoiceSuspendedStatus;
    }

    public void setInvoiceSuspendedStatus(String invoiceSuspendedStatus) {
        this.invoiceSuspendedStatus = invoiceSuspendedStatus;
    }

    // Added in R7
    private Boolean isApprovedModificationChecked = false;

    public Boolean getIsApprovedModificationChecked() {
        return isApprovedModificationChecked;
    }

    public void setIsApprovedModificationChecked(
            Boolean isApprovedModificationChecked) {
        this.isApprovedModificationChecked = isApprovedModificationChecked;
    }

    // R7 End

}
