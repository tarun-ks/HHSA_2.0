package com.nyc.hhs.model;

import java.util.ArrayList;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;

/**
 * This bean class is used to maintain the data for Invoice List.
 * 
 */

public class InvoiceList extends BaseFilter
{

	public InvoiceList(String asOrgnizationType)
	{
		setDefaultSortData(asOrgnizationType);
	}

	public void setDefaultSortData()
	{
		setFirstSort(HHSConstants.STATUS_PROCESS_TYPE_ID);
		setSecondSort(HHSConstants.INVOICE_NUMBER_SORT);
		setFirstSortType(HHSConstants.ASCENDING);
		setSecondSortType(HHSConstants.DESCENDING);
		setSortColumnName(HHSConstants.INVOICE_STATUS);
	}

	public InvoiceList()
	{	
		//No Action Required
	}

	public void setDefaultSortData(String asOrgnizationType)
	{
		if (asOrgnizationType != null && asOrgnizationType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
		{
			setFirstSort(HHSConstants.STATUS);
			setSecondSort(HHSConstants.INVOICE_NUMBER_SORT);
			setFirstSortType(HHSConstants.ASCENDING);
			setSecondSortType(HHSConstants.DESCENDING);
			setSortColumnName(HHSConstants.INVOICE_STATUS);
		}
		else
		{
			setFirstSort(HHSConstants.STATUS_PROCESS_TYPE_ID);
			setSecondSort(HHSConstants.INVOICE_NUMBER_SORT);
			setFirstSortType(HHSConstants.ASCENDING);
			setSecondSortType(HHSConstants.DESCENDING);
			setSortColumnName(HHSConstants.INVOICE_STATUS);
		}

	}

	/**
	 * String invoiceStatusId
	 */
	private String invoiceStatusId;
	/**
	 * String invoiceContractTitle
	 */
	@Length(max = 120)
	private String invoiceContractTitle;
	@Length(max = 32)
	private String invoiceCtId;
	private String invoiceFiscalYear;
	private String invoiceFiscalYearId;
	private String invoiceProgramName;
	private String invoiceValueFrom;
	private String invoiceValueTo;
	private String dateSubmittedFrom;
	private String dateSubmittedTo;
	private String dateApprovedFrom;
	private String dateApprovedTo;
	private String agency;
	private Boolean isFilter = false;
	@RegExp(value ="^\\d{0,22}")
	private String invoiceId;
	private String lsRequestFromHomePage;
	private String providerId;
	//Made changes for defect id 6248 release 3.3.0
	private String invoiceAgencyId;
	//Changes for enhancement  id 6461  release 3.4.0.
	@RegExp(value ="^\\d{0,22}")
	private String invoiceContractId;
	@RegExp(value ="^\\d{0,22}")
	private String invoiceBudgetId;

	
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

	//Start : R5 Added
	private String paymentCount;
	
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
	//End : R5 Added
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
	 * @return the lsRequestFromHomePage
	 */
	public String getLsRequestFromHomePage()
	{
		return lsRequestFromHomePage;
	}

	/**
	 * @param lsRequestFromHomePage the lsRequestFromHomePage to set
	 */
	public void setLsRequestFromHomePage(String lsRequestFromHomePage)
	{
		this.lsRequestFromHomePage = lsRequestFromHomePage;
	}

	/**
	 * String invoiceNumber
	 */
	private String invoiceNumber = HHSConstants.EMPTY_STRING;
	/**
	 * String invoiceDateSubmitted
	 */
	private String invoiceDateSubmitted;
	/**
	 * String invoiceDateApproved
	 */
	private String invoiceDateApproved;
	/**
	 * String invoiceValue
	 */
	private String invoiceValue = HHSConstants.EMPTY_STRING;
	/**
	 * String invoiceStatus
	 */
	private String invoiceStatus;
	/**
	 * String invoiceLastUpdateDate
	 */
	private String invoiceLastUpdateDate;
	/**
	 * String invoiceProvider
	 */
	private String invoiceProvider;
	/**
	 * String orgId
	 */
	private String orgId;
	/**
	 * String orgType
	 */
	private String orgType;
	/**
	 * getters for invoiceLastUpdateDate
	 * @return invoiceLastUpdateDate
	 */
	private String invoiceAction;
	/**
	 * String invoiceStatusId
	 */
	private String invoiceStartDate;

	private String invoiceEndDate;

	private String assignmentValue;

	private String advanceValue;

	private String totalValue;

	public String getInvoiceValueFrom()
	{
		return invoiceValueFrom;
	}

	public void setInvoiceValueFrom(String invoiceValueFrom)
	{
		this.invoiceValueFrom = invoiceValueFrom;
	}

	public String getInvoiceValueTo()
	{
		return invoiceValueTo;
	}

	public void setInvoiceValueTo(String invoiceValueTo)
	{
		this.invoiceValueTo = invoiceValueTo;
	}

	public String getDateSubmittedFrom()
	{
		return dateSubmittedFrom;
	}

	public void setDateSubmittedFrom(String dateSubmittedFrom)
	{
		this.dateSubmittedFrom = dateSubmittedFrom;
	}

	public String getDateSubmittedTo()
	{
		return dateSubmittedTo;
	}

	public void setDateSubmittedTo(String dateSubmittedTo)
	{
		this.dateSubmittedTo = dateSubmittedTo;
	}

	public String getInvoiceContractTitle()
	{
		return invoiceContractTitle;
	}

	public void setInvoiceContractTitle(String invoiceContractTitle)
	{
		this.invoiceContractTitle = invoiceContractTitle;
	}

	public String getInvoiceCtId()
	{
		return invoiceCtId;
	}

	public void setInvoiceCtId(String invoiceCtId)
	{
		this.invoiceCtId = invoiceCtId;
	}

	public String getInvoiceFiscalYear()
	{
		return invoiceFiscalYear;
	}

	public void setInvoiceFiscalYear(String invoiceFiscalYear)
	{
		this.invoiceFiscalYear = invoiceFiscalYear;
	}

	public String getInvoiceFiscalYearId()
	{
		return invoiceFiscalYearId;
	}

	public void setInvoiceFiscalYearId(String invoiceFiscalYearId)
	{
		this.invoiceFiscalYearId = invoiceFiscalYearId;
	}

	public String getInvoiceStatusId()
	{
		return invoiceStatusId;
	}

	public void setInvoiceStatusId(String invoiceStatusId)
	{
		this.invoiceStatusId = invoiceStatusId;
	}

	public void setInvoiceAction(String invoiceAction)
	{
		this.invoiceAction = invoiceAction;
	}

	public String getInvoiceAction()
	{
		return invoiceAction;
	}

	private List<String> invoiceStatusList = new ArrayList<String>();

	public List<String> getInvoiceStatusList()
	{
		return invoiceStatusList;
	}

	public void setInvoiceStatusList(List<String> invoiceStatusList)
	{
		this.invoiceStatusList = invoiceStatusList;
	}

	public String getInvoiceLastUpdateDate()
	{
		return invoiceLastUpdateDate;
	}

	/**
	 * setter for invoiceLastUpdateDate
	 * @param invoiceLastUpdateDate
	 */
	public void setInvoiceLastUpdateDate(String invoiceLastUpdateDate)
	{
		this.invoiceLastUpdateDate = invoiceLastUpdateDate;
	}

	/**
	 * getters for invoiceLastUpdateDate
	 * @return invoiceLastUpdateDate
	 */

	public String getInvoiceProvider()
	{
		return invoiceProvider;
	}

	/**
	 * setter for invoiceProvider
	 * @param invoiceProvider
	 */
	public void setInvoiceProvider(String invoiceProvider)
	{
		this.invoiceProvider = invoiceProvider;
	}

	/**
	 * getters for orgId
	 * @return orgId
	 */

	public String getOrgId()
	{
		return orgId;
	}

	/**
	 * setter for orgId
	 * @param orgId
	 */
	public void setOrgId(String orgId)
	{
		this.orgId = orgId;
	}

	/**
	 * getters for invoiceNumber
	 * @return invoiceNumber
	 */

	public String getInvoiceNumber()
	{
		return invoiceNumber;
	}

	/**
	 * setter for invoiceNumber
	 * @param invoiceNumber
	 */
	public void setInvoiceNumber(String invoiceNumber)
	{
		this.invoiceNumber = invoiceNumber;
	}

	/**
	 * getters for invoiceDateSubmitted
	 * @return invoiceDateSubmitted
	 */

	public String getInvoiceDateSubmitted()
	{
		return invoiceDateSubmitted;
	}

	/**
	 * setter for invoiceDateSubmitted
	 * @param invoiceDateSubmitted
	 */
	public void setInvoiceDateSubmitted(String invoiceDateSubmitted)
	{
		this.invoiceDateSubmitted = invoiceDateSubmitted;
	}

	/**
	 * getters for invoiceDateApproved
	 * @return invoiceDateApproved
	 */

	public String getInvoiceDateApproved()
	{
		return invoiceDateApproved;
	}

	/**
	 * setter for invoiceDateApproved
	 * @param invoiceDateApproved
	 */
	public void setInvoiceDateApproved(String invoiceDateApproved)
	{
		this.invoiceDateApproved = invoiceDateApproved;
	}

	/**
	 * getters for invoiceValue
	 * @return invoiceValue
	 */

	public String getInvoiceValue()
	{
		return invoiceValue;
	}

	/**
	 * setter for invoiceValue
	 * @param invoiceValue
	 */
	public void setInvoiceValue(String invoiceValue)
	{
		this.invoiceValue = invoiceValue;
	}

	/**
	 * getters for invoiceStatus
	 * 
	 * @return invoiceStatus
	 */

	public String getInvoiceStatus()
	{
		return invoiceStatus;
	}

	/**
	 * setter for invoiceStatus
	 * @param invoiceStatus
	 */
	public void setInvoiceStatus(String invoiceStatus)
	{
		this.invoiceStatus = invoiceStatus;
	}

	public void setOrgType(String orgType)
	{
		this.orgType = orgType;
	}

	public String getOrgType()
	{
		return orgType;
	}

	public void setInvoiceProgramName(String invoiceProgramName)
	{
		this.invoiceProgramName = invoiceProgramName;
	}

	public String getInvoiceProgramName()
	{
		return invoiceProgramName;
	}

	public void setDateApprovedFrom(String dateApprovedFrom)
	{
		this.dateApprovedFrom = dateApprovedFrom;
	}

	public String getDateApprovedFrom()
	{
		return dateApprovedFrom;
	}

	public void setDateApprovedTo(String dateApprovedTo)
	{
		this.dateApprovedTo = dateApprovedTo;
	}

	public String getDateApprovedTo()
	{
		return dateApprovedTo;
	}

	public void setAgency(String agency)
	{
		this.agency = agency;
	}

	public String getAgency()
	{
		return agency;
	}

	public void setIsFilter(Boolean isFilter)
	{
		this.isFilter = isFilter;
	}

	public Boolean getIsFilter()
	{
		return isFilter;
	}

	/**
	 * @return the invoiceStartDate
	 */
	public String getInvoiceStartDate()
	{
		return invoiceStartDate;
	}

	/**
	 * @param invoiceStartDate the invoiceStartDate to set
	 */
	public void setInvoiceStartDate(String invoiceStartDate)
	{
		this.invoiceStartDate = invoiceStartDate;
	}

	/**
	 * @return the invoiceEndDate
	 */
	public String getInvoiceEndDate()
	{
		return invoiceEndDate;
	}

	/**
	 * @param invoiceEndDate the invoiceEndDate to set
	 */
	public void setInvoiceEndDate(String invoiceEndDate)
	{
		this.invoiceEndDate = invoiceEndDate;
	}

	/**
	 * @return the assignmentValue
	 */
	public String getAssignmentValue()
	{
		return assignmentValue;
	}

	/**
	 * @param assignmentValue the assignmentValue to set
	 */
	public void setAssignmentValue(String assignmentValue)
	{
		this.assignmentValue = assignmentValue;
	}

	/**
	 * @return the advanceValue
	 */
	public String getAdvanceValue()
	{
		return advanceValue;
	}

	/**
	 * @param advanceValue the advanceValue to set
	 */
	public void setAdvanceValue(String advanceValue)
	{
		this.advanceValue = advanceValue;
	}

	/**
	 * @return the totalValue
	 */
	public String getTotalValue()
	{
		return totalValue;
	}

	/**
	 * @param totalValue the totalValue to set
	 */
	public void setTotalValue(String totalValue)
	{
		this.totalValue = totalValue;
	}

	public void setInvoiceId(String invoiceId)
	{
		this.invoiceId = invoiceId;
	}

	public String getInvoiceId()
	{
		return invoiceId;
	}

	@Override
	public String toString()
	{
		return "InvoiceList [invoiceStatusId=" + invoiceStatusId + ", invoiceContractTitle=" + invoiceContractTitle
				+ ", invoiceCtId=" + invoiceCtId + ", invoiceFiscalYear=" + invoiceFiscalYear
				+ ", invoiceFiscalYearId=" + invoiceFiscalYearId + ", invoiceProgramName=" + invoiceProgramName
				+ ", invoiceValueFrom=" + invoiceValueFrom + ", invoiceValueTo=" + invoiceValueTo
				+ ", dateSubmittedFrom=" + dateSubmittedFrom + ", dateSubmittedTo=" + dateSubmittedTo
				+ ", dateApprovedFrom=" + dateApprovedFrom + ", dateApprovedTo=" + dateApprovedTo + ", agency="
				+ agency + ", isFilter=" + isFilter + ", invoiceId=" + invoiceId + ", lsRequestFromHomePage="
				+ lsRequestFromHomePage + ", providerId=" + providerId + ", invoiceNumber=" + invoiceNumber
				+ ", invoiceDateSubmitted=" + invoiceDateSubmitted + ", invoiceDateApproved=" + invoiceDateApproved
				+ ", invoiceValue=" + invoiceValue + ", invoiceStatus=" + invoiceStatus + ", invoiceLastUpdateDate="
				+ invoiceLastUpdateDate + ", invoiceProvider=" + invoiceProvider + ", orgId=" + orgId + ", orgType="
				+ orgType + ", invoiceAction=" + invoiceAction + ", invoiceStartDate=" + invoiceStartDate
				+ ", invoiceEndDate=" + invoiceEndDate + ", assignmentValue=" + assignmentValue + ", advanceValue="
				+ advanceValue + ", totalValue=" + totalValue + ", invoiceStatusList=" + invoiceStatusList + ", userSubRole=" + userSubRole + "]";
	}

	public void setInvoiceAgencyId(String invoiceAgencyId)
	{
		this.invoiceAgencyId = invoiceAgencyId;
	}

	public String getInvoiceAgencyId()
	{
		return invoiceAgencyId;
	}

	public void setInvoiceContractId(String invoiceContractId) {
		this.invoiceContractId = invoiceContractId;
	}

	public String getInvoiceContractId() {
		return invoiceContractId;
	}

	public void setInvoiceBudgetId(String invoiceBudgetId) {
		this.invoiceBudgetId = invoiceBudgetId;
	}

	public String getInvoiceBudgetId() {
		return invoiceBudgetId;
	}
	
	//[Start: Add column only for Invoice Review -- R6.1.0 enhancement #8665 and QC6522]
	public String genInvoiceSvcDate() {
		if( invoiceStartDate == null ||  invoiceEndDate == null) return ""; 
			
		return this.invoiceStartDate + HHSConstants.SYMBOL_SEPERATOR + invoiceEndDate;		
	}
	//[End: Add column only for Invoice Review -- R6.1.0 enhancement #8665 and QC6522]	
	
    /* [Start] R7.2.0 QC8914 Add indicator for Access control */
    private String  userSubRole                         = "";

    public String getUserSubRole() {
        return userSubRole;
    }

    public void setUserSubRole(String userSubRole) {
        this.userSubRole = userSubRole;
    }
    /* [End] R7.2.0 QC8914 Add indicator for Access control */

}
