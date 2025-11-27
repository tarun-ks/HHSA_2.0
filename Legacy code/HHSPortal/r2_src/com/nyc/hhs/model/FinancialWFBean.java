package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

public class FinancialWFBean
{
	@RegExp(value ="^\\d{0,22}")
	private String procurementId = HHSConstants.EMPTY_STRING;
	@RegExp(value ="^\\d{0,22}")
	private String contractId = HHSConstants.EMPTY_STRING;
	@RegExp(value ="^\\d{0,22}")
	private String budgetId = HHSConstants.EMPTY_STRING;
	@Length(max = 120)
	private String procurementTitle = HHSConstants.EMPTY_STRING;
	private String procEpin = HHSConstants.EMPTY_STRING;
	private String contractNum = HHSConstants.EMPTY_STRING;
	private String awardEpin = HHSConstants.EMPTY_STRING;
	private String providerId = HHSConstants.EMPTY_STRING;
	private String agencyId = HHSConstants.EMPTY_STRING;
	@Length(max = 120)
	private String contractTitle = HHSConstants.EMPTY_STRING;
	private String reviewLevel = HHSConstants.EMPTY_STRING;
	private String contractConfigurationId = HHSConstants.EMPTY_STRING;
	private String advanceNumber = HHSConstants.EMPTY_STRING;
	private String launchBy = HHSConstants.EMPTY_STRING;
	private String userId = HHSConstants.EMPTY_STRING;
	private String budgetTypeId;
	private String fiscalYearId;
	private String invoiceId;
	private String providerName;
	private String programName;
	private Boolean launchCOF = false;
	private String competitionPoolTitle;
	//Added for R6: return payment review task
	private String returnPaymentDetailsId = HHSConstants.EMPTY_STRING;
	//Added for R6: return payment review task end
	//Added for R6: Launch by org type field required
	private String launchByOrgType = HHSConstants.EMPTY_STRING;
	/**
	 * @return the launchCOF
	 */
	public Boolean getLaunchCOF()
	{
		return launchCOF;
	}

	/**
	 * @param launchCOF the launchCOF to set
	 */
	public void setLaunchCOF(Boolean launchCOF)
	{
		this.launchCOF = launchCOF;
	}

	/**
	 * @return the programName
	 */
	public String getProgramName()
	{
		return programName;
	}

	/**
	 * @param programName the programName to set
	 */
	public void setProgramName(String programName)
	{
		this.programName = programName;
	}

	/**
	 * @return the providerName
	 */
	public String getProviderName()
	{
		return providerName;
	}

	/**
	 * @param providerName the providerName to set
	 */
	public void setProviderName(String providerName)
	{
		this.providerName = providerName;
	}

	private String procUser;

	/**
	 * @return the userId
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	/**
	 * @return the launchBy
	 */
	public String getLaunchBy()
	{
		return launchBy;
	}

	/**
	 * @param launchBy the launchBy to set
	 */
	public void setLaunchBy(String launchBy)
	{
		this.launchBy = launchBy;
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
	 * @return the contractNum
	 */
	public String getContractNum()
	{
		return contractNum;
	}

	/**
	 * @param contractNum the contractNum to set
	 */
	public void setContractNum(String contractNum)
	{
		this.contractNum = contractNum;
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
	 * @return the reviewLevel
	 */
	public String getReviewLevel()
	{
		return reviewLevel;
	}

	/**
	 * @param reviewLevel the reviewLevel to set
	 */
	public void setReviewLevel(String reviewLevel)
	{
		this.reviewLevel = reviewLevel;
	}

	/**
	 * @return the contractConfigurationId
	 */
	public String getContractConfigurationId()
	{
		return contractConfigurationId;
	}

	/**
	 * @param contractConfigurationId the contractConfigurationId to set
	 */
	public void setContractConfigurationId(String contractConfigurationId)
	{
		this.contractConfigurationId = contractConfigurationId;
	}

	/**
	 * @return the advanceNumber
	 */
	public String getAdvanceNumber()
	{
		return advanceNumber;
	}

	/**
	 * @param advanceNumber the advanceNumber to set
	 */
	public void setAdvanceNumber(String advanceNumber)
	{
		this.advanceNumber = advanceNumber;
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
	 * @return the fiscalYearId
	 */
	public String getFiscalYearId()
	{
		return fiscalYearId;
	}

	/**
	 * @param fiscalYearId the fiscalYearId to set
	 */
	public void setFiscalYearId(String fiscalYearId)
	{
		this.fiscalYearId = fiscalYearId;
	}

	/**
	 * @return the procUser
	 */
	public String getProcUser()
	{
		return procUser;
	}

	/**
	 * @param procUser the procUser to set
	 */
	public void setProcUser(String procUser)
	{
		this.procUser = procUser;
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
	 * @return the competitionPoolTitle
	 */
	public String getCompetitionPoolTitle()
	{
		return competitionPoolTitle;
	}

	/**
	 * @param competitionPoolTitle the competitionPoolTitle to set
	 */
	public void setCompetitionPoolTitle(String competitionPoolTitle)
	{
		this.competitionPoolTitle = competitionPoolTitle;
	}

	@Override
	public String toString()
	{
		return "FinancialWFBean [procurementId=" + procurementId + ", contractId=" + contractId + ", budgetId="
				+ budgetId + ", procurementTitle=" + procurementTitle + ", procEpin=" + procEpin + ", contractNum="
				+ contractNum + ", awardEpin=" + awardEpin + ", providerId=" + providerId + ", agencyId=" + agencyId
				+ ", contractTitle=" + contractTitle + ", reviewLevel=" + reviewLevel + ", contractConfigurationId="
				+ contractConfigurationId + ", advanceNumber=" + advanceNumber + ", launchBy=" + launchBy + ", userId="
				+ userId + ", budgetTypeId=" + budgetTypeId + ", fiscalYearId=" + fiscalYearId + ", invoiceId="
				+ invoiceId + ", providerName=" + providerName + ", programName=" + programName + ", launchCOF="
				+ launchCOF + ", procUser=" + procUser + "]";
	}
	//Added for R6: return payment review task - setter and getter
	public String getReturnPaymentDetailsId() {
		return returnPaymentDetailsId;
	}

	public void setReturnPaymentDetailsId(String returnPaymentDetailsId) {
		this.returnPaymentDetailsId = returnPaymentDetailsId;
	}
	//Added for R6: return payment review task end

	/**
	 * @return the launchByOrgType
	 */
	public String getLaunchByOrgType() {
		return launchByOrgType;
	}

	/**
	 * @param launchByOrgType the launchByOrgType to set
	 */
	public void setLaunchByOrgType(String launchByOrgType) {
		this.launchByOrgType = launchByOrgType;
	}


}
