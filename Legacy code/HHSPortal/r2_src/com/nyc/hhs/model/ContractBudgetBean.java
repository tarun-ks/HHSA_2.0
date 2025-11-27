package com.nyc.hhs.model;

import java.util.HashMap;

import com.nyc.hhs.constants.HHSConstants;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class ContractBudgetBean extends BaseFilter
{

	private String id;
	@RegExp(value ="^\\d{0,22}")
	private String budgetId;
	private int budgetTypeId;
	private String budgetfiscalYear = HHSConstants.EMPTY_STRING;
	private String contractValue = HHSConstants.EMPTY_STRING;
	private String plannedAmount = HHSConstants.EMPTY_STRING;
	private String totalbudgetAmount = HHSConstants.EMPTY_STRING;
	@RegExp(value ="^\\d{0,22}")
	private String subBudgetId = HHSConstants.EMPTY_STRING;
	private String parentSubBudgetId = HHSConstants.EMPTY_STRING;
	private String subbudgetName = HHSConstants.EMPTY_STRING;
	private String subbudgetNameBackup = HHSConstants.EMPTY_STRING;
	private String subbudgetAmount = HHSConstants.EMPTY_STRING;
	private String createdByUserId = HHSConstants.EMPTY_STRING;
	private String modifiedByUserId = HHSConstants.EMPTY_STRING;
	@RegExp(value ="^\\d{0,22}")
	private String contractId = HHSConstants.EMPTY_STRING;
	private String amendmentContractId = HHSConstants.EMPTY_STRING;
	private String contractTypeId = HHSConstants.EMPTY_STRING;
	private String activeFlag = HHSConstants.ONE;
	private String statusId = HHSConstants.EMPTY_STRING;
	private String programId = HHSConstants.EMPTY_STRING;
	private String agencyId = HHSConstants.EMPTY_STRING;
	private String organizationId = HHSConstants.EMPTY_STRING;
	private String budgetStartDate = HHSConstants.EMPTY_STRING;
	private String budgetEndDate = HHSConstants.EMPTY_STRING;
	private String invoiceAmount = HHSConstants.EMPTY_STRING;
	private String selectedFYId = HHSConstants.EMPTY_STRING;
	private String parentId = HHSConstants.EMPTY_STRING;
	private String modifiedAmount = HHSConstants.STRING_ZERO;
	private String remAmt = HHSConstants.STRING_ZERO;
	private String amendAmt = HHSConstants.STRING_ZERO;
	private String posAmendAmt = HHSConstants.STRING_ZERO;
	private String negAmendAmt = HHSConstants.STRING_ZERO;
	private String proposedBudgetAmount = HHSConstants.EMPTY_STRING;
	private String totalProposedBudgetAmount = HHSConstants.EMPTY_STRING;
	private String totalInvoicedBudgetAmount = HHSConstants.EMPTY_STRING;
	private String totalModifiedAmount = HHSConstants.EMPTY_STRING;
	private String totalSubBudgetAmount = HHSConstants.EMPTY_STRING;
	private String deleteFlag = HHSConstants.EMPTY_STRING;
	// Start: Added in R7
	private String modifiedUnits = HHSConstants.STRING_ZERO;
	/*[Start] R8.5.0 QC9465  add column for unallocated fund */
	private String unallocatedFundCount = HHSConstants.STRING_ZERO;
	/*[End] R8.5.0 QC9465  add column for unallocated fund */
	private HashMap<String,Object> servicesBudgetDetails = new HashMap<String,Object>();
	
	/*[Start] R8.5.0 QC9465  add column for unallocated fund  setter & getter */
	public String getUnallocatedFundCount() {
		return unallocatedFundCount;
	}

	public void setUnallocatedFundCount(String unallocatedFundCount) {
		this.unallocatedFundCount = unallocatedFundCount;
	}
	/*[End] R8.5.0 QC9465  add column for unallocated fund   setter & getter */

	
	public HashMap<String, Object> getServicesBudgetDetails() {
		return servicesBudgetDetails;
	}

	public void setServicesBudgetDetails(
			HashMap<String, Object> servicesBudgetDetails) {
		this.servicesBudgetDetails = servicesBudgetDetails;
	}
	
	public String getModifiedUnits() {
		return modifiedUnits;
	}

	public void setModifiedUnits(String modifiedUnits) {
		this.modifiedUnits = modifiedUnits;
	}
	
	// End: Added in R7

	

	/**
	 * <li>This method was added in R4 </li>
	 * @return the amendAmt
	 */
	public String getAmendAmt()
	{
		return amendAmt;
	}

	/**
	 * <li>This method was added in R4 </li>
	 * @param amendAmt the amendAmt to set
	 */
	public void setAmendAmt(String amendAmt)
	{
		this.amendAmt = amendAmt;
	}

	/**
	 * <li>This method was added in R4 </li>
	 * @return the remAmt
	 */
	public String getRemAmt()
	{
		return remAmt;
	}

	/**
	 * <li>This method was added in R4 </li>
	 * @param remAmt the remAmt to set
	 */
	public void setRemAmt(String remAmt)
	{
		this.remAmt = remAmt;
	}

	/**
	 * <li>This method was added in R4 </li>
	 * @return the posAmendAmt
	 */
	public String getPosAmendAmt()
	{
		return posAmendAmt;
	}

	/**
	 * <li>This method was added in R4 </li>
	 * @param posAmendAmt the posAmendAmt to set
	 */
	public void setPosAmendAmt(String posAmendAmt)
	{
		this.posAmendAmt = posAmendAmt;
	}

	/**
	 * <li>This method was added in R4 </li>
	 * @return the negAmendAmt
	 */
	public String getNegAmendAmt()
	{
		return negAmendAmt;
	}

	/**
	 * <li>This method was added in R4 </li>
	 * @param negAmendAmt the negAmendAmt to set
	 */
	public void setNegAmendAmt(String negAmendAmt)
	{
		this.negAmendAmt = negAmendAmt;
	}

	/**
	 * <li>This method was added in R4 </li>
	 * @return the amendmentContractId
	 */
	public String getAmendmentContractId()
	{
		return amendmentContractId;
	}

	/**
	 * <li>This method was added in R4 </li>
	 * @param amendmentContractId the amendmentContractId to set
	 */
	public void setAmendmentContractId(String amendmentContractId)
	{
		this.amendmentContractId = amendmentContractId;
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

	public String getDeleteFlag()
	{
		return deleteFlag;
	}

	public void setDeleteFlag(String deleteFlag)
	{
		this.deleteFlag = deleteFlag;
	}

	public String getTotalModifiedAmount()
	{
		return totalModifiedAmount;
	}

	public void setTotalModifiedAmount(String totalModifiedAmount)
	{
		this.totalModifiedAmount = totalModifiedAmount;
	}

	public String getTotalSubBudgetAmount()
	{
		return totalSubBudgetAmount;
	}

	public void setTotalSubBudgetAmount(String totalSubBudgetAmount)
	{
		this.totalSubBudgetAmount = totalSubBudgetAmount;
	}

	public String getTotalProposedBudgetAmount()
	{
		return totalProposedBudgetAmount;
	}

	public void setTotalProposedBudgetAmount(String totalProposedBudgetAmount)
	{
		this.totalProposedBudgetAmount = totalProposedBudgetAmount;
	}

	public String getTotalInvoicedBudgetAmount()
	{
		return totalInvoicedBudgetAmount;
	}

	public void setTotalInvoicedBudgetAmount(String totalInvoicedBudgetAmount)
	{
		this.totalInvoicedBudgetAmount = totalInvoicedBudgetAmount;
	}

	public String getProposedBudgetAmount()
	{
		return proposedBudgetAmount;
	}

	public void setProposedBudgetAmount(String proposedBudgetAmount)
	{
		this.proposedBudgetAmount = proposedBudgetAmount;
	}

	public String getModifiedAmount()
	{
		return modifiedAmount;
	}

	public void setModifiedAmount(String modifiedAmount)
	{
		this.modifiedAmount = modifiedAmount;
	}

	public String getParentId()
	{
		return parentId;
	}

	public void setParentId(String parentId)
	{
		this.parentId = parentId;
	}

	public String getSelectedFYId()
	{
		return selectedFYId;
	}

	public void setSelectedFYId(String selectedFYId)
	{
		this.selectedFYId = selectedFYId;
	}

	public String getInvoiceAmount()
	{
		return invoiceAmount;
	}

	public void setInvoiceAmount(String invoiceAmount)
	{
		this.invoiceAmount = invoiceAmount;
	}

	public String getModifiedByUserId()
	{
		return modifiedByUserId;
	}

	public void setModifiedByUserId(String modifiedByUserId)
	{
		this.modifiedByUserId = modifiedByUserId;
	}

	public String getContractId()
	{
		return contractId;
	}

	public void setContractId(String contractId)
	{
		this.contractId = contractId;
	}

	public String getActiveFlag()
	{
		return activeFlag;
	}

	public void setActiveFlag(String activeFlag)
	{
		this.activeFlag = activeFlag;
	}

	public String getStatusId()
	{
		return statusId;
	}

	public void setStatusId(String statusId)
	{
		this.statusId = statusId;
	}

	public String getProgramId()
	{
		return programId;
	}

	public void setProgramId(String programId)
	{
		this.programId = programId;
	}

	public String getAgencyId()
	{
		return agencyId;
	}

	public void setAgencyId(String agencyId)
	{
		this.agencyId = agencyId;
	}

	public String getOrganizationId()
	{
		return organizationId;
	}

	public void setOrganizationId(String organizationId)
	{
		this.organizationId = organizationId;
	}

	public String getBudgetStartDate()
	{
		return budgetStartDate;
	}

	public void setBudgetStartDate(String budgetStartDate)
	{
		this.budgetStartDate = budgetStartDate;
	}

	public String getBudgetEndDate()
	{
		return budgetEndDate;
	}

	public void setBudgetEndDate(String budgetEndDate)
	{
		this.budgetEndDate = budgetEndDate;
	}

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getCreatedByUserId()
	{
		return createdByUserId;
	}

	public void setCreatedByUserId(String createdByUserId)
	{
		this.createdByUserId = createdByUserId;
	}

	public String getBudgetId()
	{
		return budgetId;
	}

	public void setBudgetId(String budgetId)
	{
		this.budgetId = budgetId;
	}

	public int getBudgetTypeId()
	{
		return budgetTypeId;
	}

	public void setBudgetTypeId(int budgetTypeId)
	{
		this.budgetTypeId = budgetTypeId;
	}

	public String getBudgetfiscalYear()
	{
		return budgetfiscalYear;
	}

	public void setBudgetfiscalYear(String budgetfiscalYear)
	{
		this.budgetfiscalYear = budgetfiscalYear;
	}

	public String getContractValue()
	{
		return contractValue;
	}

	public void setContractValue(String contractValue)
	{
		this.contractValue = contractValue;
	}

	public String getPlannedAmount()
	{
		return plannedAmount;
	}

	public void setPlannedAmount(String plannedAmount)
	{
		this.plannedAmount = plannedAmount;
	}

	public String getTotalbudgetAmount()
	{
		return totalbudgetAmount;
	}

	public String getSubBudgetId()
	{
		return subBudgetId;
	}

	public void setSubBudgetId(String subBudgetId)
	{
		this.subBudgetId = subBudgetId;
	}

	public String getParentSubBudgetId()
	{
		return parentSubBudgetId;
	}

	public void setParentSubBudgetId(String parentSubBudgetId)
	{
		this.parentSubBudgetId = parentSubBudgetId;
	}

	public void setTotalbudgetAmount(String totalbudgetAmount)
	{
		this.totalbudgetAmount = totalbudgetAmount;
	}

	public String getSubbudgetName()
	{
		return subbudgetName;
	}

	public void setSubbudgetName(String subbudgetName)
	{
		this.subbudgetName = subbudgetName;
	}

	public String getSubbudgetAmount()
	{
		return subbudgetAmount;
	}

	public void setSubbudgetAmount(String subbudgetAmount)
	{
		this.subbudgetAmount = subbudgetAmount;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((budgetfiscalYear == null) ? 0 : budgetfiscalYear.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		ContractBudgetBean other = (ContractBudgetBean) obj;
		if (budgetfiscalYear == null)
		{
			if (other.budgetfiscalYear != null)
			{
				return false;
			}
		}
		else if (!budgetfiscalYear.equals(other.budgetfiscalYear))
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString()
	{
		return "ContractBudgetBean [id=" + id + ", budgetId=" + budgetId + ", budgetTypeId=" + budgetTypeId
				+ ", budgetfiscalYear=" + budgetfiscalYear + ", contractValue=" + contractValue + ", plannedAmount="
				+ plannedAmount + ", totalbudgetAmount=" + totalbudgetAmount + ", subBudgetId=" + subBudgetId
				+ ", parentSubBudgetId=" + parentSubBudgetId + ", subbudgetName=" + subbudgetName
				+ ", subbudgetAmount=" + subbudgetAmount + ", createdByUserId=" + createdByUserId
				+ ", modifiedByUserId=" + modifiedByUserId + ", contractId=" + contractId + ", contractTypeId="
				+ contractTypeId + ", activeFlag=" + activeFlag + ", statusId=" + statusId + ", programId=" + programId
				+ ", agencyId=" + agencyId + ", organizationId=" + organizationId + ", budgetStartDate="
				+ budgetStartDate + ", budgetEndDate=" + budgetEndDate + ", invoiceAmount=" + invoiceAmount
				+ ", selectedFYId=" + selectedFYId + ", parentId=" + parentId + ", modifiedAmount=" + modifiedAmount
				+ ", proposedBudgetAmount=" + proposedBudgetAmount + ", totalProposedBudgetAmount="
				+ totalProposedBudgetAmount + ", totalInvoicedBudgetAmount=" + totalInvoicedBudgetAmount
				+ ", totalModifiedAmount=" + totalModifiedAmount + ", totalSubBudgetAmount=" + totalSubBudgetAmount
				+ ", deleteFlag=" + deleteFlag + "]";
	}

	public void setSubbudgetNameBackup(String subbudgetNameBackup) {
		this.subbudgetNameBackup = subbudgetNameBackup;
	}

	public String getSubbudgetNameBackup() {
		return subbudgetNameBackup;
	}
	//R7 : Added for Modification Auto Approval
		private String modificationCount = HHSConstants.EMPTY_STRING;
		private String baseCount = HHSConstants.EMPTY_STRING;
		private String autoApprovalEligible = HHSConstants.EMPTY_STRING;
		private String modificationPercentage = HHSConstants.EMPTY_STRING;
		private String entryTypeID = HHSConstants.EMPTY_STRING;
		private String modAutoApprovedPerc = HHSConstants.EMPTY_STRING;
		private String totalModification = HHSConstants.EMPTY_STRING;
		private String modAgencyThreshold = HHSConstants.EMPTY_STRING;


	// Added for R7 : Modification Auto Approval
		    public String getModificationPercentage()
		    {
			    return modificationPercentage;
		    }
			public void setModificationPercentage(String modificationPercentage)
			{
				this.modificationPercentage = modificationPercentage;
			}

			public String getEntryTypeID()
			{
				return entryTypeID;
			}

			public void setEntryTypeID(String entryTypeID)
			{
				this.entryTypeID = entryTypeID;
			}

			public String getModAutoApprovedPerc()
			{
				return modAutoApprovedPerc;
			}

			public void setModAutoApprovedPerc(String modAutoApprovedPerc)
			{
				this.modAutoApprovedPerc = modAutoApprovedPerc;
			}

			public String getTotalModification()
			{
				return totalModification;
			}

			public void setTotalModification(String totalModification)
			{
				this.totalModification = totalModification;
			}

			public String getModAgencyThreshold()
			{
				return modAgencyThreshold;
			}

			public void setModAgencyThreshold(String modAgencyThreshold)
			{
				this.modAgencyThreshold = modAgencyThreshold;
			}

			public String getModificationCount()
			{
				return modificationCount;
			}

			public void setModificationCount(String modificationCount)
			{
				this.modificationCount = modificationCount;
			}

			public String getBaseCount()
			{
				return baseCount;
			}

			public void setBaseCount(String baseCount)
			{
				this.baseCount = baseCount;
			}

			public String getAutoApprovalEligible()
			{
				return autoApprovalEligible;
			}

			public void setAutoApprovalEligible(String autoApprovalEligible)
			{
				this.autoApprovalEligible = autoApprovalEligible;
			}

}
