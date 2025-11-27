/**
 * 
 */
package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

public class ProcurementCOF
{
	@RegExp(value ="^\\d{0,22}")
	private String procurementId = null;
	private int procurementCOFId;
	private String procurementTitle = HHSConstants.EMPTY_STRING;
	@Length(max = 20)
	private String agencyId;
	@Length(max = 100)
	private String agencyName;
	@Length(max = 20)
	private String agencyCode;
	private String procurementValue = HHSConstants.EMPTY_STRING;
	private String contractStartDate = HHSConstants.EMPTY_STRING;
	private String contractEndDate = HHSConstants.EMPTY_STRING;
	private String contractValue = HHSConstants.STRING_ZERO;
	private String origContractStartDate = HHSConstants.EMPTY_STRING;
	private String origContractEndDate = HHSConstants.EMPTY_STRING;
	private String origContractValue = HHSConstants.STRING_ZERO;

	private String amendmentValue = HHSConstants.STRING_ZERO;
	private String amendmentType;
	private String procurementStatus;
	private int totConfiguredBudget;
	private String providerName = HHSConstants.EMPTY_STRING;;
	private String procEpin;
	private String awardEpin;
	private String amendmentStartDate = HHSConstants.EMPTY_STRING;
	private String amendmentEndDate = HHSConstants.EMPTY_STRING;

	private String createdDate;
	private String createdByUserId;
	private String modifiedDate;
	private String modifiedByUserId;
	private String userFirstName;
	private String userLastName;
	private String approverFirstName;
	private String approverLastName;
	private String approvedDate;

	private String updatedContractStartDate = HHSConstants.EMPTY_STRING;
	private String updatedContractEndDate = HHSConstants.EMPTY_STRING;
	private String updatedContractId = null;

	private String contractTypeId;
	private String newContractValue;
	private String status;

	private String positiveAmendmentValue = HHSConstants.STRING_ZERO;
	private String negativeAmendmentValue = HHSConstants.STRING_ZERO;

	private String amendedContractValue;
	private String amendedProcurementValue;

	private int amendmentCount;

	private String amendmentEpin;
	private String amendmentTitle;
	
	private String compPoolTitle;
	
	private String contractTermDate;
	
	private String amendmentTermDate;
	/**
	 * @return the contractTermDate
	 */
	public String getContractTermDate()
	{
		return contractTermDate;
	}
	
	/**
	 * @param contractTermDate the contractTermDate to set
	 */
	public void setContractTermDate(String contractTermDate)
	{
		this.contractTermDate = contractTermDate;
	}

	/**
	 * @return the compPoolTitle
	 */
	public String getCompPoolTitle()
	{
		return compPoolTitle;
	}

	/**
	 * @param compPoolTitle the compPoolTitle to set
	 */
	public void setCompPoolTitle(String compPoolTitle)
	{
		this.compPoolTitle = compPoolTitle;
	}

	/**
	 * @return the amendmentTitle
	 */
	public String getAmendmentTitle()
	{
		return amendmentTitle;
	}

	/**
	 * @param amendmentTitle the amendmentTitle to set
	 */
	public void setAmendmentTitle(String amendmentTitle)
	{
		this.amendmentTitle = amendmentTitle;
	}

	/**
	 * @return the amendmentEpin
	 */
	public String getAmendmentEpin()
	{
		return amendmentEpin;
	}

	/**
	 * @param amendmentEpin the amendmentEpin to set
	 */
	public void setAmendmentEpin(String amendmentEpin)
	{
		this.amendmentEpin = amendmentEpin;
	}

	/**
	 * @return the amendmentCount
	 */
	public int getAmendmentCount()
	{
		return amendmentCount;
	}

	/**
	 * @param amendmentCount the amendmentCount to set
	 */
	public void setAmendmentCount(int amendmentCount)
	{
		this.amendmentCount = amendmentCount;
	}

	/**
	 * @return the positiveAmendmentValue
	 */
	public String getPositiveAmendmentValue()
	{
		return positiveAmendmentValue;
	}

	/**
	 * @param positiveAmendmentValue the positiveAmendmentValue to set
	 */
	public void setPositiveAmendmentValue(String positiveAmendmentValue)
	{
		this.positiveAmendmentValue = positiveAmendmentValue;
	}

	/**
	 * @return the negativeAmendmentValue
	 */
	public String getNegativeAmendmentValue()
	{
		return negativeAmendmentValue;
	}

	/**
	 * @param negativeAmendmentValue the negativeAmendmentValue to set
	 */
	public void setNegativeAmendmentValue(String negativeAmendmentValue)
	{
		this.negativeAmendmentValue = negativeAmendmentValue;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getAmendmentType()
	{
		return amendmentType;
	}

	public void setAmendmentType(String amendmentType)
	{
		this.amendmentType = amendmentType;
	}

	public String getUpdatedContractId()
	{
		return updatedContractId;
	}

	public void setUpdatedContractId(String updatedContractId)
	{
		this.updatedContractId = updatedContractId;
	}

	public String getUpdatedContractStartDate()
	{
		return updatedContractStartDate;
	}

	public void setUpdatedContractStartDate(String updatedContractStartDate)
	{
		this.updatedContractStartDate = updatedContractStartDate;
	}

	public String getUpdatedContractEndDate()
	{
		return updatedContractEndDate;
	}

	public void setUpdatedContractEndDate(String updatedContractEndDate)
	{
		this.updatedContractEndDate = updatedContractEndDate;
	}

	/**
	 * @return the amendmentStartDate
	 */
	public String getAmendmentStartDate()
	{
		return amendmentStartDate;
	}

	/**
	 * @param amendmentStartDate the amendmentStartDate to set
	 */
	public void setAmendmentStartDate(String amendmentStartDate)
	{
		this.amendmentStartDate = amendmentStartDate;
	}

	/**
	 * @return the amendmentEndDate
	 */
	public String getAmendmentEndDate()
	{
		return amendmentEndDate;
	}

	/**
	 * @param amendmentEndDate the amendmentEndDate to set
	 */
	public void setAmendmentEndDate(String amendmentEndDate)
	{
		this.amendmentEndDate = amendmentEndDate;
	}

	/**
	 * @return the amendmentValue
	 */
	public String getAmendmentValue()
	{
		return amendmentValue;
	}

	/**
	 * @param amendmentValue the amendmentValue to set
	 */
	public void setAmendmentValue(String amendmentValue)
	{
		this.amendmentValue = amendmentValue;
	}

	/**
	 * @return the approverFirstName
	 */
	public String getApproverFirstName()
	{
		return approverFirstName;
	}

	/**
	 * @param approverFirstName the approverFirstName to set
	 */
	public void setApproverFirstName(String approverFirstName)
	{
		this.approverFirstName = approverFirstName;
	}

	/**
	 * @return the agencyCode
	 */
	public String getAgencyCode()
	{
		return agencyCode;
	}

	/**
	 * @param agencyCode the agencyCode to set
	 */
	public void setAgencyCode(String agencyCode)
	{
		this.agencyCode = agencyCode;
	}

	/**
	 * @return the approverLastName
	 */
	public String getApproverLastName()
	{
		return approverLastName;
	}

	/**
	 * @param approverLastName the approverLastName to set
	 */
	public void setApproverLastName(String approverLastName)
	{
		this.approverLastName = approverLastName;
	}

	/**
	 * @return the userFirstName
	 */
	public String getUserFirstName()
	{
		return userFirstName;
	}

	/**
	 * @param userFirstName the userFirstName to set
	 */
	public void setUserFirstName(String userFirstName)
	{
		this.userFirstName = userFirstName;
	}

	/**
	 * @return the userLastName
	 */
	public String getUserLastName()
	{
		return userLastName;
	}

	/**
	 * @param userLastName the userLastName to set
	 */
	public void setUserLastName(String userLastName)
	{
		this.userLastName = userLastName;
	}

	/**
	 * @return the createdDate
	 */
	public String getCreatedDate()
	{
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate)
	{
		this.createdDate = createdDate;
	}

	/**
	 * @return the createdByUserId
	 */
	public String getCreatedByUserId()
	{
		return createdByUserId;
	}

	/**
	 * @param createdByUserId the createdByUserId to set
	 */
	public void setCreatedByUserId(String createdByUserId)
	{
		this.createdByUserId = createdByUserId;
	}

	/**
	 * @return the modifiedDate
	 */
	public String getModifiedDate()
	{
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(String modifiedDate)
	{
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the modifiedByUserId
	 */
	public String getModifiedByUserId()
	{
		return modifiedByUserId;
	}

	/**
	 * @param modifiedByUserId the modifiedByUserId to set
	 */
	public void setModifiedByUserId(String modifiedByUserId)
	{
		this.modifiedByUserId = modifiedByUserId;
	}

	public String getProviderName()
	{
		return providerName;
	}

	public void setProviderName(String providerName)
	{
		this.providerName = providerName;
	}

	/**
	 * @return the procurementCOFId
	 */
	public int getProcurementCOFId()
	{
		return procurementCOFId;
	}

	/**
	 * @param procurementCOFId the procurementCOFId to set
	 */
	public void setProcurementCOFId(int procurementCOFId)
	{
		this.procurementCOFId = procurementCOFId;
	}

	/**
	 * @return the procurementId
	 */
	public String getProcurementId()
	{
		return procurementId;
	}

	/**
	 * @param procurementId the procurementId to set
	 */
	public void setProcurementId(String procurementId)
	{
		this.procurementId = procurementId;
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
	 * @return the agencyName
	 */
	public String getAgencyName()
	{
		return agencyName;
	}

	/**
	 * @param agencyName the agencyName to set
	 */
	public void setAgencyName(String agencyName)
	{
		this.agencyName = agencyName;
	}

	/**
	 * @return the procurementValue
	 */
	public String getProcurementValue()
	{
		return procurementValue;
	}

	/**
	 * @param procurementValue the procurementValue to set
	 */
	public void setProcurementValue(String procurementValue)
	{
		this.procurementValue = procurementValue;
	}

	/**
	 * @return the contractStartDate
	 */
	public String getContractStartDate()
	{
		return contractStartDate;
	}

	/**
	 * @param contractStartDate the contractStartDate to set
	 */
	public void setContractStartDate(String contractStartDate)
	{
		this.contractStartDate = contractStartDate;
	}

	/**
	 * @return the contractEndDate
	 */
	public String getContractEndDate()
	{
		return contractEndDate;
	}

	/**
	 * @param contractEndDate the contractEndDate to set
	 */
	public void setContractEndDate(String contractEndDate)
	{
		this.contractEndDate = contractEndDate;
	}

	/**
	 * @param contractValue the contractValue to set
	 */
	public void setContractValue(String contractValue)
	{
		this.contractValue = contractValue;
	}

	/**
	 * @return the contractValue
	 */
	public String getContractValue()
	{
		return contractValue;
	}

	/**
	 * @return procurementStatus
	 */
	public String getProcurementStatus()
	{
		return procurementStatus;
	}

	/**
	 * @param procurementStatus to be set
	 */
	public void setProcurementStatus(String procurementStatus)
	{
		this.procurementStatus = procurementStatus;
	}

	/**
	 * @return the totConfiguredBudget
	 */
	public int getTotConfiguredBudget()
	{
		return totConfiguredBudget;
	}

	/**
	 * @param totConfiguredBudget the totConfiguredBudget to set
	 */
	public void setTotConfiguredBudget(int totConfiguredBudget)
	{
		this.totConfiguredBudget = totConfiguredBudget;
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

	public String getAwardEpin()
	{
		return awardEpin;
	}

	public void setAwardEpin(String awardEpin)
	{
		this.awardEpin = awardEpin;
	}

	public String getAmendedContractValue()
	{
		return amendedContractValue;
	}

	public void setAmendedContractValue(String amendedContractValue)
	{
		this.amendedContractValue = amendedContractValue;
	}

	public String getContractTypeId()
	{
		return contractTypeId;
	}

	public void setContractTypeId(String contractTypeId)
	{
		this.contractTypeId = contractTypeId;
	}

	public String getNewContractValue()
	{
		return newContractValue;
	}

	public void setNewContractValue(String newContractValue)
	{
		this.newContractValue = newContractValue;
	}

	public void setApprovedDate(String approvedDate)
	{
		this.approvedDate = approvedDate;
	}

	public String getApprovedDate()
	{
		return approvedDate;
	}

	public String getOrigContractStartDate()
	{
		return origContractStartDate;
	}

	public void setOrigContractStartDate(String origContractStartDate)
	{
		this.origContractStartDate = origContractStartDate;
	}

	public String getOrigContractEndDate()
	{
		return origContractEndDate;
	}

	public void setOrigContractEndDate(String origContractEndDate)
	{
		this.origContractEndDate = origContractEndDate;
	}

	public String getOrigContractValue()
	{
		return origContractValue;
	}

	public void setOrigContractValue(String origContractValue)
	{
		this.origContractValue = origContractValue;
	}

	@Override
	public String toString()
	{
		return "ProcurementCOF [procurementId=" + procurementId + ", procurementCOFId=" + procurementCOFId
				+ ", procurementTitle=" + procurementTitle + ", agencyId=" + agencyId + ", agencyName=" + agencyName
				+ ", agencyCode=" + agencyCode + ", procurementValue=" + procurementValue + ", contractStartDate="
				+ contractStartDate + ", contractEndDate=" + contractEndDate + ", contractValue=" + contractValue
				+ ", origContractStartDate=" + origContractStartDate + ", origContractEndDate=" + origContractEndDate
				+ ", origContractValue=" + origContractValue + ", amendmentValue=" + amendmentValue
				+ ", amendmentType=" + amendmentType + ", procurementStatus=" + procurementStatus
				+ ", totConfiguredBudget=" + totConfiguredBudget + ", providerName=" + providerName + ", procEpin="
				+ procEpin + ", awardEpin=" + awardEpin + ", amendmentStartDate=" + amendmentStartDate
				+ ", amendmentEndDate=" + amendmentEndDate + ", createdDate=" + createdDate + ", createdByUserId="
				+ createdByUserId + ", modifiedDate=" + modifiedDate + ", modifiedByUserId=" + modifiedByUserId
				+ ", userFirstName=" + userFirstName + ", userLastName=" + userLastName + ", approverFirstName="
				+ approverFirstName + ", approverLastName=" + approverLastName + ", approvedDate=" + approvedDate
				+ ", updatedContractStartDate=" + updatedContractStartDate + ", updatedContractEndDate="
				+ updatedContractEndDate + ", updatedContractId=" + updatedContractId + ", amendedContractValue="
				+ amendedContractValue + ", contractTypeId=" + contractTypeId + ", contractTermDate="
				+ contractTermDate + ", newContractValue=" +"amendmentTermDate="+amendmentTermDate+ newContractValue + ", status=" + status + "]";
	}

	/**
	 * @return the amendedProcurementValue
	 */
	public String getAmendedProcurementValue()
	{
		return amendedProcurementValue;
	}

	/**
	 * @param amendedProcurementValue the amendedProcurementValue to set
	 */
	public void setAmendedProcurementValue(String amendedProcurementValue)
	{
		this.amendedProcurementValue = amendedProcurementValue;
	}

	/**
	 * @param amendmentTermDate the amendmentTermDate to set
	 */
	public void setAmendmentTermDate(String amendmentTermDate)
	{
		this.amendmentTermDate = amendmentTermDate;
	}

	/**
	 * @return the amendmentTermDate
	 */
	public String getAmendmentTermDate()
	{
		return amendmentTermDate;
	}

}