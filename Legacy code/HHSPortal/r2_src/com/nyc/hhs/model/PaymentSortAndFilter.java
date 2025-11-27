/**
 * 
 */
package com.nyc.hhs.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;

/**
 * This is a bean class for payment sorting
 * and filter
 */
public class PaymentSortAndFilter extends BaseFilter
{
	public PaymentSortAndFilter()
	{
		// No Action Required
	}

	public PaymentSortAndFilter(String asOrgnizationType)
	{
		setDefaultSortData(asOrgnizationType);
	}

	public void setDefaultSortData(String asOrgnizationType)
	{
		if (asOrgnizationType != null && asOrgnizationType.equalsIgnoreCase(ApplicationConstants.PROVIDER_ORG))
		{
			setFirstSort(HHSConstants.DISBURSEMENT_DATE);
			setFirstSortDate(true);
			setFirstSortType(HHSConstants.DESCENDING);
			setSecondSort(HHSConstants.PAYMENT_VOUCHER_NUMBER);
			setSecondSortType(HHSConstants.DESCENDING);
			setSortColumnName(HHSConstants.PAYMENT_DIS_DATE);
		}
		else
		{
			setFirstSort(HHSConstants.STATUS_PROCESS_TYPE_ID);
			setSecondSort(HHSConstants.LAST_UPDATE_DATE);
			setSecondSortDate(true);
			setFirstSortType(HHSConstants.ASCENDING);
			setSecondSortType(HHSConstants.DESCENDING);
			setSortColumnName(HHSConstants.PAYMENT_STATUS_COLUMN);
		}
	}

	// Used for Sorting
	private String paymentPayeeName;
	private String paymentVoucherNumber;
	private String paymentValue;
	private Date paymentDisDate;
	private List<String> paymentIdList;
	private String paymentId;
	private Date paymentLastUpdateDate;
	private String serviceName;
	private String orgId;
	private String orgType;
	private String agency;
	private String paymentStatus;
	private String paymentStatusId;
	// Made changes in this query for defect id 6248 release 3.3.0
	private String paymentAgencyId;
	/* Start : Added in R5 */
	private String action;
	private String paymentCount;

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
	 * @return the action
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action)
	{
		this.action = action;
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

	/* End : Added in R5 */
	public String getPaymentStatusId()
	{
		return paymentStatusId;
	}

	public void setPaymentStatusId(String paymentStatusId)
	{
		this.paymentStatusId = paymentStatusId;
	}

	private String paymentFiscalYear;
	// Used for Sorting and filter
	private String paymentDisNum;
	private String paymentCtId;
	private String paymentFiscalYearId;
	private String paymentContractTitle;
	private String paymentProcurementTitle;
	private String paymentProvider;
	private String dateLastUpdate;
	// Used for Filter
	private String paymentProgramName;
	private String dateDisbursedFrom;
	private String dateDisbursedTo;
	private String dateLastUpdateFrom;
	private String dateLastUpdateTo;
	private String paymentValueFrom;
	private String paymentValueTo;
	private List<String> paymentStatusList = new ArrayList<String>();
	private String whereClause;
	// for fiscal year filter
	private String fiscalYearId;
	private String fiscalYear;
	// for status master filter
	private String statusId;
	private String prevStatusId;
	private String processType;
	private String statusProcessTypeId;
	private String status;
	// for program name filter
	private String programId;
	private String programName;
	// for agency name filter
	private String agencyName;
	private String agencyId;
	private Boolean isFilter = false;

	private String invoiceId;
	@RegExp(value ="^\\d{0,22}")
	private String budgetId;
	@RegExp(value ="^\\d{0,22}")
	private String contractId;
	private String budgetAdvanceId;
	private String providerId;
	@RegExp(value ="^\\d{0,22}")
	private String budgetTypeId;

    /* [Start] R7.2.0 QC8914 Add indicator for Access control */
    private String  userSubRole                         = "";

    public String getUserSubRole() {
        return userSubRole;
    }

    public void setUserSubRole(String userSubRole) {
        this.userSubRole = userSubRole;
    }
    /* [End] R7.2.0 QC8914 Add indicator for Access control */
    
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

	public String getInvoiceId()
	{
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId)
	{
		this.invoiceId = invoiceId;
	}

	public String getBudgetId()
	{
		return budgetId;
	}

	public void setBudgetId(String budgetId)
	{
		this.budgetId = budgetId;
	}

	public String getContractId()
	{
		return contractId;
	}

	public void setContractId(String contractId)
	{
		this.contractId = contractId;
	}

	public String getDateDisbursedFrom()
	{
		return dateDisbursedFrom;
	}

	public void setDateDisbursedFrom(String dateDisbursedFrom)
	{
		this.dateDisbursedFrom = dateDisbursedFrom;
	}

	public String getDateDisbursedTo()
	{
		return dateDisbursedTo;
	}

	public void setDateDisbursedTo(String dateDisbursedTo)
	{
		this.dateDisbursedTo = dateDisbursedTo;
	}

	public String getDateLastUpdateFrom()
	{
		return dateLastUpdateFrom;
	}

	public void setDateLastUpdateFrom(String dateLastUpdateFrom)
	{
		this.dateLastUpdateFrom = dateLastUpdateFrom;
	}

	public String getDateLastUpdateTo()
	{
		return dateLastUpdateTo;
	}

	public void setDateLastUpdateTo(String dateLastUpdateTo)
	{
		this.dateLastUpdateTo = dateLastUpdateTo;
	}

	public String getAgencyName()
	{
		return agencyName;
	}

	public void setAgencyName(String agencyName)
	{
		this.agencyName = agencyName;
	}

	public String getAgencyId()
	{
		return agencyId;
	}

	public void setAgencyId(String agencyId)
	{
		this.agencyId = agencyId;
	}

	public String getProgramId()
	{
		return programId;
	}

	public void setProgramId(String programId)
	{
		this.programId = programId;
	}

	public String getProgramName()
	{
		return programName;
	}

	public void setProgramName(String programName)
	{
		this.programName = programName;
	}

	public String getStatusId()
	{
		return statusId;
	}

	public void setStatusId(String statusId)
	{
		this.statusId = statusId;
	}

	public String getProcessType()
	{
		return processType;
	}

	public void setProcessType(String processType)
	{
		this.processType = processType;
	}

	public String getStatusProcessTypeId()
	{
		return statusProcessTypeId;
	}

	public void setStatusProcessTypeId(String statusProcessTypeId)
	{
		this.statusProcessTypeId = statusProcessTypeId;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getFiscalYearId()
	{
		return fiscalYearId;
	}

	public void setFiscalYearId(String fiscalYearId)
	{
		this.fiscalYearId = fiscalYearId;
	}

	public String getFiscalYear()
	{
		return fiscalYear;
	}

	public void setFiscalYear(String fiscalYear)
	{
		this.fiscalYear = fiscalYear;
	}

	public String getDateLastUpdate()
	{
		return dateLastUpdate;
	}

	public void setDateLastUpdate(String dateLastUpdate)
	{
		this.dateLastUpdate = dateLastUpdate;
	}

	public String getPaymentProgramName()
	{
		return paymentProgramName;
	}

	public void setPaymentProgramName(String paymentProgramName)
	{
		this.paymentProgramName = paymentProgramName;
	}

	public String getPaymentStatus()
	{
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus)
	{
		this.paymentStatus = paymentStatus;
	}

	public String getPaymentFiscalYear()
	{
		return paymentFiscalYear;
	}

	public void setPaymentFiscalYear(String paymentFiscalYear)
	{
		this.paymentFiscalYear = paymentFiscalYear;
	}

	public String getPaymentFiscalYearId()
	{
		return paymentFiscalYearId;
	}

	public void setPaymentFiscalYearId(String paymentFiscalYearId)
	{
		this.paymentFiscalYearId = paymentFiscalYearId;
	}

	public String getOrgType()
	{
		return orgType;
	}

	public void setOrgType(String orgType)
	{
		this.orgType = orgType;
	}

	public String getOrgId()
	{
		return orgId;
	}

	public void setOrgId(String orgId)
	{
		this.orgId = orgId;
	}

	public List<String> getPaymentIdList()
	{
		return paymentIdList;
	}

	public void setPaymentIdList(List<String> paymentIdList)
	{
		this.paymentIdList = paymentIdList;
	}

	public String getPaymentId()
	{
		return paymentId;
	}

	public void setPaymentId(String paymentId)
	{
		this.paymentId = paymentId;
	}

	public String getPaymentContractTitle()
	{
		return paymentContractTitle;
	}

	public void setPaymentContractTitle(String paymentContractTitle)
	{
		this.paymentContractTitle = paymentContractTitle;
	}

	public String getPaymentPayeeName()
	{
		return paymentPayeeName;
	}

	public void setPaymentPayeeName(String paymentPayeeName)
	{
		this.paymentPayeeName = paymentPayeeName;
	}

	public String getPaymentCtId()
	{
		return paymentCtId;
	}

	public void setPaymentCtId(String paymentCtId)
	{
		this.paymentCtId = paymentCtId;
	}

	public String getPaymentVoucherNumber()
	{
		return paymentVoucherNumber;
	}

	public void setPaymentVoucherNumber(String paymentVoucherNumber)
	{
		this.paymentVoucherNumber = paymentVoucherNumber;
	}

	public String getPaymentValue()
	{
		return paymentValue;
	}

	public void setPaymentValue(String paymentValue)
	{
		this.paymentValue = paymentValue;
	}

	public String getPaymentDisNum()
	{
		return paymentDisNum;
	}

	public void setPaymentDisNum(String paymentDisNum)
	{
		this.paymentDisNum = paymentDisNum;
	}

	public Date getPaymentDisDate()
	{
		return paymentDisDate;
	}

	public void setPaymentDisDate(Date paymentDisDate)
	{
		this.paymentDisDate = paymentDisDate;
	}

	public String getPaymentProvider()
	{
		return paymentProvider;
	}

	public void setPaymentProvider(String paymentProvider)
	{
		this.paymentProvider = paymentProvider;
	}

	public List<String> getPaymentStatusList()
	{
		return paymentStatusList;
	}

	public void setPaymentStatusList(List<String> paymentStatusList)
	{
		this.paymentStatusList = paymentStatusList;
	}

	public Date getPaymentLastUpdateDate()
	{
		return paymentLastUpdateDate;
	}

	public void setPaymentLastUpdateDate(Date paymentLastUpdateDate)
	{
		this.paymentLastUpdateDate = paymentLastUpdateDate;
	}

	public String getServiceName()
	{
		return serviceName;
	}

	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	public String getAgency()
	{
		return agency;
	}

	public void setAgency(String agency)
	{
		this.agency = agency;
	}

	public String getWhereClause()
	{
		return whereClause;
	}

	public void setWhereClause(String whereClause)
	{
		this.whereClause = whereClause;
	}

	public String getPaymentValueFrom()
	{
		return paymentValueFrom;
	}

	public void setPaymentValueFrom(String paymentValueFrom)
	{
		this.paymentValueFrom = paymentValueFrom;
	}

	public String getPaymentValueTo()
	{
		return paymentValueTo;
	}

	public void setPaymentValueTo(String paymentValueTo)
	{
		this.paymentValueTo = paymentValueTo;
	}

	public void setIsFilter(Boolean isFilter)
	{
		this.isFilter = isFilter;
	}

	public Boolean getIsFilter()
	{
		return isFilter;
	}

	public void setPaymentProcurementTitle(String paymentProcurementTitle)
	{
		this.paymentProcurementTitle = paymentProcurementTitle;
	}

	public String getPaymentProcurementTitle()
	{
		return paymentProcurementTitle;
	}

	public void setBudgetAdvanceId(String budgetAdvanceId)
	{
		this.budgetAdvanceId = budgetAdvanceId;
	}

	public String getBudgetAdvanceId()
	{
		return budgetAdvanceId;
	}

	public void setBudgetTypeId(String budgetTypeId)
	{
		this.budgetTypeId = budgetTypeId;
	}

	public String getBudgetTypeId()
	{
		return budgetTypeId;
	}

	public void setPrevStatusId(String prevStatusId)
	{
		this.prevStatusId = prevStatusId;
	}

	public String getPrevStatusId()
	{
		return prevStatusId;
	}

	@Override
	public String toString()
	{
		return "PaymentSortAndFilter [paymentPayeeName=" + paymentPayeeName + ", paymentVoucherNumber="
				+ paymentVoucherNumber + ", paymentValue=" + paymentValue + ", paymentDisDate=" + paymentDisDate
				+ ", paymentIdList=" + paymentIdList + ", paymentId=" + paymentId + ", paymentLastUpdateDate="
				+ paymentLastUpdateDate + ", serviceName=" + serviceName + ", orgId=" + orgId + ", orgType=" + orgType
				+ ", agency=" + agency + ", paymentStatus=" + paymentStatus + ", paymentStatusId=" + paymentStatusId
				+ ", paymentFiscalYear=" + paymentFiscalYear + ", paymentDisNum=" + paymentDisNum + ", paymentCtId="
				+ paymentCtId + ", paymentFiscalYearId=" + paymentFiscalYearId + ", paymentContractTitle="
				+ paymentContractTitle + ", paymentProcurementTitle=" + paymentProcurementTitle + ", paymentProvider="
				+ paymentProvider + ", dateLastUpdate=" + dateLastUpdate + ", paymentProgramName=" + paymentProgramName
				+ ", dateDisbursedFrom=" + dateDisbursedFrom + ", dateDisbursedTo=" + dateDisbursedTo
				+ ", dateLastUpdateFrom=" + dateLastUpdateFrom + ", dateLastUpdateTo=" + dateLastUpdateTo
				+ ", paymentValueFrom=" + paymentValueFrom + ", paymentValueTo=" + paymentValueTo
				+ ", paymentStatusList=" + paymentStatusList + ", whereClause=" + whereClause + ", fiscalYearId="
				+ fiscalYearId + ", fiscalYear=" + fiscalYear + ", statusId=" + statusId + ", prevStatusId="
				+ prevStatusId + ", processType=" + processType + ", statusProcessTypeId=" + statusProcessTypeId
				+ ", status=" + status + ", programId=" + programId + ", programName=" + programName + ", agencyName="
				+ agencyName + ", agencyId=" + agencyId + ", isFilter=" + isFilter + ", invoiceId=" + invoiceId
				+ ", budgetId=" + budgetId + ", contractId=" + contractId + ", budgetAdvanceId=" + budgetAdvanceId
				+ ", providerId=" + providerId + ", budgetTypeId=" + budgetTypeId +  ", userSubRole=" + userSubRole + "]";
	}

	public void setPaymentAgencyId(String paymentAgencyId)
	{
		this.paymentAgencyId = paymentAgencyId;
	}

	public String getPaymentAgencyId()
	{
		return paymentAgencyId;
	}
}
