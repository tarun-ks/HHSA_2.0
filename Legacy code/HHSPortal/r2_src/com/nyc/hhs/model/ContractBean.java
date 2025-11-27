/**
 * 
 */

package com.nyc.hhs.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

/**
 * This class is a bean that store details for contract.
 */
public class ContractBean
{
	@RegExp(value ="^\\d{0,22}")
	private String contractId = HHSConstants.EMPTY_STRING;
	private String contractTitle = HHSConstants.EMPTY_STRING;
	private String contractType = HHSConstants.EMPTY_STRING;
	@RegExp(value ="^\\d{0,22}")
	private String contractTypeId = HHSConstants.EMPTY_STRING;
	private Date contractStartDate;
	private Date contractEndDate;
	private String contractAmount = HHSConstants.EMPTY_STRING;
	private Date fmsContractStartDate;
	private Date fmsContractEndDate;
	private String fmsContractAmount = HHSConstants.EMPTY_STRING;
	private String parentContractId = HHSConstants.EMPTY_STRING;
	private String parentContractTitle = HHSConstants.EMPTY_STRING;
	private String parentContractAmount = HHSConstants.EMPTY_STRING;
	private Date parentContractStartDate;
	private Date parentContractEndDate;
	private String discrepancyFlag = HHSConstants.STRING_ZERO;
	private String statusId = HHSConstants.EMPTY_STRING;
	private Integer contractStartFiscalYear;
	private Integer contractEndFiscalYear;
	private Integer totalFiscalYearsCount;
	private Integer fmsContractStartFiscalYear;
	private Integer fmsContractEndFiscalYear;
	private Integer fmsTotalFiscalYearsCount;

	private boolean discrepancyInContractAmount = HHSConstants.BOOLEAN_FALSE;
	private boolean discrepancyInStartDate = HHSConstants.BOOLEAN_FALSE;
	private boolean discrepancyInEndDate = HHSConstants.BOOLEAN_FALSE;

	private boolean batchRunNeeded = HHSConstants.BOOLEAN_TRUE;

	private String fmsContractAmountAfterMerge = HHSConstants.EMPTY_STRING;
	private String extCtNumber = HHSConstants.EMPTY_STRING;
	private String parentExtCtNumber = HHSConstants.EMPTY_STRING;

	private String organizationId = HHSConstants.EMPTY_STRING;
	private String agencyId = HHSConstants.EMPTY_STRING;
	private String parentOrganizationId = HHSConstants.EMPTY_STRING;
	private String parentAgencyId = HHSConstants.EMPTY_STRING;
	private String awardEpin = HHSConstants.EMPTY_STRING;
	private String amendAffectedBudgetId = HHSConstants.EMPTY_STRING;
	// Start Added in R5
	private String userName = HHSConstants.EMPTY_STRING;
	private String userId = HHSConstants.EMPTY_STRING;
	private List<String> userListWithoutAccess = new ArrayList<String>();
	private String staffId = HHSConstants.EMPTY_STRING;
	private String accessFlag = HHSConstants.EMPTY_STRING;
	private String email = HHSConstants.EMPTY_STRING;
	private String permission_level = HHSConstants.EMPTY_STRING;
	private String contractRestrictionId = HHSConstants.EMPTY_STRING;
	//Release 6: Added for Apt Interface batch
	private String budgetStartYear = HHSConstants.EMPTY_STRING;

	public String getContractRestrictionId()
	{
		return contractRestrictionId;
	}

	public void setContractRestrictionId(String contractRestrictionId)
	{
		this.contractRestrictionId = contractRestrictionId;
	}

	public String getStaffId()
	{
		return staffId;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}

	public String getPermission_level()
	{
		return permission_level;
	}

	public void setPermission_level(String permission_level)
	{
		this.permission_level = permission_level;
	}

	public void setStaffId(String staffId)
	{
		this.staffId = staffId;
	}

	public String getAccessFlag()
	{
		return accessFlag;
	}

	public void setAccessFlag(String accessFlag)
	{
		this.accessFlag = accessFlag;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public List<String> getUserListWithoutAccess()
	{
		return userListWithoutAccess;
	}

	public void setUserListWithoutAccess(List<String> userListWithoutAccess)
	{
		this.userListWithoutAccess = userListWithoutAccess;
	}

	// End Added in R5
	/**
	 * @return the contractId
	 */
	public String getContractId()
	{
		return contractId;
	}

	/**
	 * @param contractId the contractId to set
	 */
	public void setContractId(String contractId)
	{
		this.contractId = contractId;
	}

	/**
	 * @return the contractTitle
	 */
	public String getContractTitle()
	{
		return contractTitle;
	}

	/**
	 * @param contractTitle the contractTitle to set
	 */
	public void setContractTitle(String contractTitle)
	{
		this.contractTitle = contractTitle;
	}

	/**
	 * @return the contractType
	 */
	public String getContractType()
	{
		return contractType;
	}

	/**
	 * @param contractType the contractType to set
	 */
	public void setContractType(String contractType)
	{
		this.contractType = contractType;
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
	 * @return the contractStartDate
	 */
	public Date getContractStartDate()
	{
		return contractStartDate;
	}

	/**
	 * @param contractStartDate the contractStartDate to set
	 */
	public void setContractStartDate(Date contractStartDate)
	{
		this.contractStartDate = contractStartDate;
	}

	/**
	 * @return the contractEndDate
	 */
	public Date getContractEndDate()
	{
		return contractEndDate;
	}

	/**
	 * @param contractEndDate the contractEndDate to set
	 */
	public void setContractEndDate(Date contractEndDate)
	{
		this.contractEndDate = contractEndDate;
	}

	/**
	 * @return the contractAmount
	 */
	public String getContractAmount()
	{
		return contractAmount;
	}

	/**
	 * @param contractAmount the contractAmount to set
	 */
	public void setContractAmount(String contractAmount)
	{
		this.contractAmount = contractAmount;
	}

	/**
	 * @return the fmsContractStartDate
	 */
	public Date getFmsContractStartDate()
	{
		return fmsContractStartDate;
	}

	/**
	 * @param fmsContractStartDate the fmsContractStartDate to set
	 */
	public void setFmsContractStartDate(Date fmsContractStartDate)
	{
		this.fmsContractStartDate = fmsContractStartDate;
	}

	/**
	 * @return the fmsContractEndDate
	 */
	public Date getFmsContractEndDate()
	{
		return fmsContractEndDate;
	}

	/**
	 * @param fmsContractEndDate the fmsContractEndDate to set
	 */
	public void setFmsContractEndDate(Date fmsContractEndDate)
	{
		this.fmsContractEndDate = fmsContractEndDate;
	}

	/**
	 * @return the fmsContractAmount
	 */
	public String getFmsContractAmount()
	{
		return fmsContractAmount;
	}

	/**
	 * @param fmsContractAmount the fmsContractAmount to set
	 */
	public void setFmsContractAmount(String fmsContractAmount)
	{
		this.fmsContractAmount = fmsContractAmount;
	}

	/**
	 * @return the parentContractId
	 */
	public String getParentContractId()
	{
		return parentContractId;
	}

	/**
	 * @param parentContractId the parentContractId to set
	 */
	public void setParentContractId(String parentContractId)
	{
		this.parentContractId = parentContractId;
	}

	/**
	 * @return the parentContractTitle
	 */
	public String getParentContractTitle()
	{
		return parentContractTitle;
	}

	/**
	 * @param parentContractTitle the parentContractTitle to set
	 */
	public void setParentContractTitle(String parentContractTitle)
	{
		this.parentContractTitle = parentContractTitle;
	}

	/**
	 * @return the parentContractAmount
	 */
	public String getParentContractAmount()
	{
		return parentContractAmount;
	}

	/**
	 * @param parentContractAmount the parentContractAmount to set
	 */
	public void setParentContractAmount(String parentContractAmount)
	{
		this.parentContractAmount = parentContractAmount;
	}

	/**
	 * @return the parentContractStartDate
	 */
	public Date getParentContractStartDate()
	{
		return parentContractStartDate;
	}

	/**
	 * @param parentContractStartDate the parentContractStartDate to set
	 */
	public void setParentContractStartDate(Date parentContractStartDate)
	{
		this.parentContractStartDate = parentContractStartDate;
	}

	/**
	 * @return the parentContractEndDate
	 */
	public Date getParentContractEndDate()
	{
		return parentContractEndDate;
	}

	/**
	 * @param parentContractEndDate the parentContractEndDate to set
	 */
	public void setParentContractEndDate(Date parentContractEndDate)
	{
		this.parentContractEndDate = parentContractEndDate;
	}

	/**
	 * @return the discrepancyFlag
	 */
	public String getDiscrepancyFlag()
	{
		return discrepancyFlag;
	}

	/**
	 * @return the statusId
	 */
	public String getStatusId()
	{
		return statusId;
	}

	/**
	 * @param statusId the statusId to set
	 */
	public void setStatusId(String statusId)
	{
		this.statusId = statusId;
	}

	/**
	 * @param discrepancyFlag the discrepancyFlag to set
	 */
	public void setDiscrepancyFlag(String discrepancyFlag)
	{
		this.discrepancyFlag = discrepancyFlag;
	}

	/**
	 * @return the totalFiscalYearsCount
	 */
	public Integer getTotalFiscalYearsCount()
	{
		return totalFiscalYearsCount;
	}

	/**
	 * @param totalFiscalYearsCount the totalFiscalYearsCount to set
	 */
	public void setTotalFiscalYearsCount(Integer totalFiscalYearsCount)
	{
		this.totalFiscalYearsCount = totalFiscalYearsCount;
	}

	/**
	 * @return the fmsTotalFiscalYearsCount
	 */
	public Integer getFmsTotalFiscalYearsCount()
	{
		return fmsTotalFiscalYearsCount;
	}

	/**
	 * @param fmsTotalFiscalYearsCount the fmsTotalFiscalYearsCount to set
	 */
	public void setFmsTotalFiscalYearsCount(Integer fmsTotalFiscalYearsCount)
	{
		this.fmsTotalFiscalYearsCount = fmsTotalFiscalYearsCount;
	}

	/**
	 * @return the discrepancyInContractAmount
	 */
	public boolean isDiscrepancyInContractAmount()
	{
		return discrepancyInContractAmount;
	}

	/**
	 * @param discrepancyInContractAmount the discrepancyInContractAmount to set
	 */
	public void setDiscrepancyInContractAmount(boolean discrepancyInContractAmount)
	{
		this.discrepancyInContractAmount = discrepancyInContractAmount;
	}

	/**
	 * @return the discrepancyInStartDate
	 */
	public boolean isDiscrepancyInStartDate()
	{
		return discrepancyInStartDate;
	}

	/**
	 * @param discrepancyInStartDate the discrepancyInStartDate to set
	 */
	public void setDiscrepancyInStartDate(boolean discrepancyInStartDate)
	{
		this.discrepancyInStartDate = discrepancyInStartDate;
	}

	/**
	 * @return the discrepancyInEndDate
	 */
	public boolean isDiscrepancyInEndDate()
	{
		return discrepancyInEndDate;
	}

	/**
	 * @param discrepancyInEndDate the discrepancyInEndDate to set
	 */
	public void setDiscrepancyInEndDate(boolean discrepancyInEndDate)
	{
		this.discrepancyInEndDate = discrepancyInEndDate;
	}

	/**
	 * @return the contractStartFiscalYear
	 */
	public Integer getContractStartFiscalYear()
	{
		return contractStartFiscalYear;
	}

	/**
	 * @param contractStartFiscalYear the contractStartFiscalYear to set
	 */
	public void setContractStartFiscalYear(Integer contractStartFiscalYear)
	{
		this.contractStartFiscalYear = contractStartFiscalYear;
	}

	/**
	 * @return the contractEndFiscalYear
	 */
	public Integer getContractEndFiscalYear()
	{
		return contractEndFiscalYear;
	}

	/**
	 * @param contractEndFiscalYear the contractEndFiscalYear to set
	 */
	public void setContractEndFiscalYear(Integer contractEndFiscalYear)
	{
		this.contractEndFiscalYear = contractEndFiscalYear;
	}

	/**
	 * @return the fmsContractStartFiscalYear
	 */
	public Integer getFmsContractStartFiscalYear()
	{
		return fmsContractStartFiscalYear;
	}

	/**
	 * @param fmsContractStartFiscalYear the fmsContractStartFiscalYear to set
	 */
	public void setFmsContractStartFiscalYear(Integer fmsContractStartFiscalYear)
	{
		this.fmsContractStartFiscalYear = fmsContractStartFiscalYear;
	}

	/**
	 * @return the fmsContractEndFiscalYear
	 */
	public Integer getFmsContractEndFiscalYear()
	{
		return fmsContractEndFiscalYear;
	}

	/**
	 * @param fmsContractEndFiscalYear the fmsContractEndFiscalYear to set
	 */
	public void setFmsContractEndFiscalYear(Integer fmsContractEndFiscalYear)
	{
		this.fmsContractEndFiscalYear = fmsContractEndFiscalYear;
	}

	/**
	 * @return the batchRunNeeded
	 */
	public boolean isBatchRunNeeded()
	{
		return batchRunNeeded;
	}

	/**
	 * @param batchRunNeeded the batchRunNeeded to set
	 */
	public void setBatchRunNeeded(boolean batchRunNeeded)
	{
		this.batchRunNeeded = batchRunNeeded;
	}

	/**
	 * @return the fmsContractAmountAfterMerge
	 */
	public String getFmsContractAmountAfterMerge()
	{
		return fmsContractAmountAfterMerge;
	}

	/**
	 * @param fmsContractAmountAfterMerge the fmsContractAmountAfterMerge to set
	 */
	public void setFmsContractAmountAfterMerge(String fmsContractAmountAfterMerge)
	{
		this.fmsContractAmountAfterMerge = fmsContractAmountAfterMerge;
	}

	/**
	 * @return the extCtNumber
	 */
	public String getExtCtNumber()
	{
		return extCtNumber;
	}

	/**
	 * @param extCtNumber the extCtNumber to set
	 */
	public void setExtCtNumber(String extCtNumber)
	{
		this.extCtNumber = extCtNumber;
	}

	/**
	 * @return the parentExtCtNumber
	 */
	public String getParentExtCtNumber()
	{
		return parentExtCtNumber;
	}

	/**
	 * @param parentExtCtNumber the parentExtCtNumber to set
	 */
	public void setParentExtCtNumber(String parentExtCtNumber)
	{
		this.parentExtCtNumber = parentExtCtNumber;
	}

	/**
	 * @return the organizationId
	 */
	public String getOrganizationId()
	{
		return organizationId;
	}

	/**
	 * @param organizationId the organizationId to set
	 */
	public void setOrganizationId(String organizationId)
	{
		this.organizationId = organizationId;
	}

	/**
	 * @return the agencyId
	 */
	public String getAgencyId()
	{
		return agencyId;
	}

	/**
	 * @param agencyId the agencyId to set
	 */
	public void setAgencyId(String agencyId)
	{
		this.agencyId = agencyId;
	}

	/**
	 * @return the parentOrganizationId
	 */
	public String getParentOrganizationId()
	{
		return parentOrganizationId;
	}

	/**
	 * @param parentOrganizationId the parentOrganizationId to set
	 */
	public void setParentOrganizationId(String parentOrganizationId)
	{
		this.parentOrganizationId = parentOrganizationId;
	}

	/**
	 * @return the parentAgencyId
	 */
	public String getParentAgencyId()
	{
		return parentAgencyId;
	}

	/**
	 * @param parentAgencyId the parentAgencyId to set
	 */
	public void setParentAgencyId(String parentAgencyId)
	{
		this.parentAgencyId = parentAgencyId;
	}

	/**
	 * @return the awardEpin
	 */
	public String getAwardEpin()
	{
		return awardEpin;
	}

	/**
	 * @param awardEpin the awardEpin to set
	 */
	public void setAwardEpin(String awardEpin)
	{
		this.awardEpin = awardEpin;
	}

	/**
	 * @return the amendAffectedBudgetId
	 */
	public String getAmendAffectedBudgetId()
	{
		return amendAffectedBudgetId;
	}

	/**
	 * @param amendAffectedBudgetId the amendAffectedBudgetId to set
	 */
	public void setAmendAffectedBudgetId(String amendAffectedBudgetId)
	{
		this.amendAffectedBudgetId = amendAffectedBudgetId;
	}
	public String getBudgetStartYear()
	{
		return budgetStartYear;
	}

	public void setBudgetStartYear(String budgetStartYear)
	{
		this.budgetStartYear = budgetStartYear;
	}

/* [Start] R8.10.0 QC9399    */
	public Date getNewContractEndDate(){
	    if( contractEndDate == null || parentContractEndDate == null ){
	        return contractEndDate;
	    }
	    
	    
	    return contractEndDate;
	}
/* [End]  R8.10.0 QC9399  */

	@Override
	public String toString()
	{
		return "ContractBean [contractId=" + contractId + ", contractTitle=" + contractTitle + ", contractType="
				+ contractType + ", contractTypeId=" + contractTypeId + ", contractStartDate=" + contractStartDate
				+ ", contractEndDate=" + contractEndDate + ", contractAmount=" + contractAmount
				+ ", fmsContractStartDate=" + fmsContractStartDate + ", fmsContractEndDate=" + fmsContractEndDate
				+ ", fmsContractAmount=" + fmsContractAmount + ", parentContractId=" + parentContractId
				+ ", parentContractTitle=" + parentContractTitle + ", parentContractAmount=" + parentContractAmount
				+ ", parentContractStartDate=" + parentContractStartDate + ", parentContractEndDate="
				+ parentContractEndDate + ", discrepancyFlag=" + discrepancyFlag + ", statusId=" + statusId
				+ ", contractStartFiscalYear=" + contractStartFiscalYear + ", contractEndFiscalYear="
				+ contractEndFiscalYear + ", totalFiscalYearsCount=" + totalFiscalYearsCount
				+ ", fmsContractStartFiscalYear=" + fmsContractStartFiscalYear + ", fmsContractEndFiscalYear="
				+ fmsContractEndFiscalYear + ", fmsTotalFiscalYearsCount=" + fmsTotalFiscalYearsCount
				+ ", discrepancyInContractAmount=" + discrepancyInContractAmount + ", discrepancyInStartDate="
				+ discrepancyInStartDate + ", discrepancyInEndDate=" + discrepancyInEndDate + ", batchRunNeeded="
				+ batchRunNeeded + ", fmsContractAmountAfterMerge=" + fmsContractAmountAfterMerge + ", extCtNumber="
				+ extCtNumber + ", parentExtCtNumber=" + parentExtCtNumber + ", organizationId=" + organizationId
				+ ", agencyId=" + agencyId + ", parentOrganizationId=" + parentOrganizationId + ", parentAgencyId="
				+ parentAgencyId + ", awardEpin=" + awardEpin + ", amendAffectedBudgetId=" + amendAffectedBudgetId
				+ ", userName=" + userName + ", userListWithoutAccess=" + userListWithoutAccess + "]";
	}

}
