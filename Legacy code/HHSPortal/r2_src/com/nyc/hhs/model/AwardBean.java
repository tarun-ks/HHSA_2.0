package com.nyc.hhs.model;

import java.sql.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/**
 *This class is a bean that store details for Award..
 */
public class AwardBean extends BaseFilter
{
	private String procurementId;
	private String proposalId;
	private String epin;
	private String contractNumber;
	private String contractStatus;
	private String awardAmount;
	private String providerName;
	private String status;
	private String procurementAwardEpin;
	private String awardAgencyId;
	private String awardedVendorName;
	private String vendorFmsId;
	private String epinType;
	private String procurementEpinId;
	private String awardEpinId;
	private String awardDocumentId;
	private String awardId;
	private String isFinancial;
	private String contractTypeId;
	private String organizationId;
	@RegExp(value ="^\\d{0,22}")
	private String contractId;
	private String ctNumber;
	private String aptMilestone;
	private String organizationLegalName;
	private Date mocsApprovalDate;
	private Date dateSentToComptroller;
	private Date registrationDate;
	private String actions;
	private String userRole;
	private String paginationEnable;
	private String contractStartDate;
	private String contractEndDate;
	private String contractTitle;
	private String contractModifiedDate;
	private String contractModifiedBy;
	private String orgType;
	private String budgetId;
	private String budgetModifiedDate;
	private String budgetModifiedByStaffId;
	private String procurementTitle;
	private String procurementStatus;
	private String evaluationPoolMappingId;
	private String isOpenEndedRFP;
	private String evaluationGroupId;
	// Added in R5
	private String awardNegotiationFlag;
	//Added in R6
	private String refAptEpinId;
	private String procurementAgencyId;
	/**  QC 8914 read only role R 7.2 **/
	private String roleCurrent;

	
	public String getProcurementAgencyId()
	{
		return procurementAgencyId;
	}

	public void setProcurementAgencyId(String procurementAgencyId)
	{
		this.procurementAgencyId = procurementAgencyId;
	}

	public String getRefAptEpinId()
	{
		return refAptEpinId;
	}

	public void setRefAptEpinId(String refAptEpinId)
	{
		this.refAptEpinId = refAptEpinId;
	}

	/**
	 * @return the awardNegotiationFlag
	 */
	public String getAwardNegotiationFlag()
	{
		return awardNegotiationFlag;
	}

	/**
	 * @param awardNegotiationFlag the awardNegotiationFlag to set
	 */
	public void setAwardNegotiationFlag(String awardNegotiationFlag)
	{
		this.awardNegotiationFlag = awardNegotiationFlag;
	}

	/**
	 * @return the evaluationGroupId
	 */
	public String getEvaluationGroupId()
	{
		return evaluationGroupId;
	}

	/**
	 * @param evaluationGroupId the evaluationGroupId to set
	 */
	public void setEvaluationGroupId(String evaluationGroupId)
	{
		this.evaluationGroupId = evaluationGroupId;
	}

	/**
	 * @return the isOpenEndedRFP
	 */
	public String getIsOpenEndedRFP()
	{
		return isOpenEndedRFP;
	}

	/**
	 * @param isOpenEndedRFP the isOpenEndedRFP to set
	 */
	public void setIsOpenEndedRFP(String isOpenEndedRFP)
	{
		this.isOpenEndedRFP = isOpenEndedRFP;
	}

	/**
	 * @return the evaluationPoolMappingId
	 */
	public String getEvaluationPoolMappingId()
	{
		return evaluationPoolMappingId;
	}

	/**
	 * @param evaluationPoolMappingId the evaluationPoolMappingId to set
	 */
	public void setEvaluationPoolMappingId(String evaluationPoolMappingId)
	{
		this.evaluationPoolMappingId = evaluationPoolMappingId;
	}

	public String getProcurementStatus()
	{
		return procurementStatus;
	}

	public void setProcurementStatus(String procurementStatus)
	{
		this.procurementStatus = procurementStatus;
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

	public String getOrgType()
	{
		return orgType;
	}

	public void setOrgType(String orgType)
	{
		this.orgType = orgType;
	}

	/**
	 * @return the contractModifiedDate
	 */
	public String getContractModifiedDate()
	{
		return contractModifiedDate;
	}

	/**
	 * @param contractModifiedDate the contractModifiedDate to set
	 */
	public void setContractModifiedDate(String contractModifiedDate)
	{
		this.contractModifiedDate = contractModifiedDate;
	}

	/**
	 * @return the userRole
	 */
	public String getUserRole()
	{
		return userRole;
	}

	/**
	 * @param userRole the userRole to set
	 */
	public void setUserRole(String userRole)
	{
		this.userRole = userRole;
	}

	/**
	 * @return the paginationEnable
	 */
	public String getPaginationEnable()
	{
		return paginationEnable;
	}

	/**
	 * @param paginationEnable the paginationEnable to set
	 */
	public void setPaginationEnable(String paginationEnable)
	{
		this.paginationEnable = paginationEnable;
	}

	/**
	 * @return the actions
	 */
	public String getActions()
	{
		return actions;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(String actions)
	{
		this.actions = actions;
	}

	/**
	 * @return the organizationId
	 */
	public String getOrganizationId()
	{
		return organizationId;
	}

	/**
	 * @return the isFinancial
	 */
	public String getIsFinancial()
	{
		return isFinancial;
	}

	/**
	 * @param isFinancial the isFinancial to set
	 */
	public void setIsFinancial(String isFinancial)
	{
		this.isFinancial = isFinancial;
	}

	/**
	 * @param organizationId the organizationId to set
	 */
	public void setOrganizationId(String organizationId)
	{
		this.organizationId = organizationId;
	}

	public String getCtNumber()
	{
		return ctNumber;
	}

	public void setCtNumber(String ctNumber)
	{
		this.ctNumber = ctNumber;
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
	 * @return the awardDocumentId
	 */
	public String getAwardDocumentId()
	{
		return awardDocumentId;
	}

	/**
	 * @param awardDocumentId the awardDocumentId to set
	 */
	public void setAwardDocumentId(String awardDocumentId)
	{
		this.awardDocumentId = awardDocumentId;
	}

	/**
	 * @return the awardId
	 */
	public String getAwardId()
	{
		return awardId;
	}

	/**
	 * @param awardId the awardId to set
	 */
	public void setAwardId(String awardId)
	{
		this.awardId = awardId;
	}

	/**
	 * @return the procurementAwardEpin
	 */
	public String getProcurementAwardEpin()
	{
		return procurementAwardEpin;
	}

	/**
	 * @param procurementAwardEpin the procurementAwardEpin to set
	 */
	public void setProcurementAwardEpin(String procurementAwardEpin)
	{
		this.procurementAwardEpin = procurementAwardEpin;
	}

	/**
	 * @return the awardAgencyId
	 */
	public String getAwardAgencyId()
	{
		return awardAgencyId;
	}

	/**
	 * @param awardAgencyId the awardAgencyId to set
	 */
	public void setAwardAgencyId(String awardAgencyId)
	{
		this.awardAgencyId = awardAgencyId;
	}

	/**
	 * @return the awardedVendorName
	 */
	public String getAwardedVendorName()
	{
		return awardedVendorName;
	}

	/**
	 * @param awardedVendorName the awardedVendorName to set
	 */
	public void setAwardedVendorName(String awardedVendorName)
	{
		this.awardedVendorName = awardedVendorName;
	}

	/**
	 * @return the vendorFmsId
	 */
	public String getVendorFmsId()
	{
		return vendorFmsId;
	}

	/**
	 * @param vendorFmsId the vendorFmsId to set
	 */
	public void setVendorFmsId(String vendorFmsId)
	{
		this.vendorFmsId = vendorFmsId;
	}

	/**
	 * @return the epinType
	 */
	public String getEpinType()
	{
		return epinType;
	}

	/**
	 * @param epinType the epinType to set
	 */
	public void setEpinType(String epinType)
	{
		this.epinType = epinType;
	}

	/**
	 * @return the procurementEpinId
	 */
	public String getProcurementEpinId()
	{
		return procurementEpinId;
	}

	/**
	 * @param procurementEpinId the procurementEpinId to set
	 */
	public void setProcurementEpinId(String procurementEpinId)
	{
		this.procurementEpinId = procurementEpinId;
	}

	/**
	 * @return the awardEpinId
	 */
	public String getAwardEpinId()
	{
		return awardEpinId;
	}

	/**
	 * @param awardEpinId the awardEpinId to set
	 */
	public void setAwardEpinId(String awardEpinId)
	{
		this.awardEpinId = awardEpinId;
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
	 * @return the proposalId
	 */
	public String getProposalId()
	{
		return proposalId;
	}

	/**
	 * @param proposalId the proposalId to set
	 */
	public void setProposalId(String proposalId)
	{
		this.proposalId = proposalId;
	}

	/**
	 * @return the epin
	 */
	public String getEpin()
	{
		return epin;
	}

	/**
	 * @param epin the epin to set
	 */
	public void setEpin(String epin)
	{
		this.epin = epin;
	}

	/**
	 * @return the contractNumber
	 */
	public String getContractNumber()
	{
		return contractNumber;
	}

	/**
	 * @param contractNumber the contractNumber to set
	 */
	public void setContractNumber(String contractNumber)
	{
		this.contractNumber = contractNumber;
	}

	/**
	 * @return the contractStatus
	 */
	public String getContractStatus()
	{
		return contractStatus;
	}

	/**
	 * @param contractStatus the contractStatus to set
	 */
	public void setContractStatus(String contractStatus)
	{
		this.contractStatus = contractStatus;
	}

	/**
	 * @return the awardAmount
	 */
	public String getAwardAmount()
	{
		return awardAmount;
	}

	/**
	 * @param awardAmount the awardAmount to set
	 */
	public void setAwardAmount(String awardAmount)
	{
		this.awardAmount = awardAmount;
	}

	public String getProviderName()
	{
		return providerName;
	}

	public void setProviderName(String providerName)
	{
		this.providerName = providerName;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	/**
	 * @param aptMilestone the aptMilestone to set
	 */
	public void setAptMilestone(String aptMilestone)
	{
		this.aptMilestone = aptMilestone;
	}

	/**
	 * @return the aptMilestone
	 */
	public String getAptMilestone()
	{
		return aptMilestone;
	}

	/**
	 * @param organizationLegalName the organizationLegalName to set
	 */
	public void setOrganizationLegalName(String organizationLegalName)
	{
		this.organizationLegalName = organizationLegalName;
	}

	/**
	 * @return the organizationLegalName
	 */
	public String getOrganizationLegalName()
	{
		return organizationLegalName;
	}

	/**
	 * @param mocsApprovalDate the mocsApprovalDate to set
	 */
	public void setMocsApprovalDate(Date mocsApprovalDate)
	{
		this.mocsApprovalDate = mocsApprovalDate;
	}

	/**
	 * @return the mocsApprovalDate
	 */
	public Date getMocsApprovalDate()
	{
		return mocsApprovalDate;
	}

	/**
	 * @param dateSentToComptroller the dateSentToComptroller to set
	 */
	public void setDateSentToComptroller(Date dateSentToComptroller)
	{
		this.dateSentToComptroller = dateSentToComptroller;
	}

	/**
	 * @return the dateSentToComptroller
	 */
	public Date getDateSentToComptroller()
	{
		return dateSentToComptroller;
	}

	/**
	 * @param registrationDate the registrationDate to set
	 */
	public void setRegistrationDate(Date registrationDate)
	{
		this.registrationDate = registrationDate;
	}

	/**
	 * @return the registrationDate
	 */
	public Date getRegistrationDate()
	{
		return registrationDate;
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
	 * @return the contractModifiedBy
	 */
	public String getContractModifiedBy()
	{
		return contractModifiedBy;
	}

	/**
	 * @param contractModifiedBy the contractModifiedBy to set
	 */
	public void setContractModifiedBy(String contractModifiedBy)
	{
		this.contractModifiedBy = contractModifiedBy;
	}

	/**
	 * @return the budgetModifiedDate
	 */
	public String getBudgetModifiedDate()
	{
		return budgetModifiedDate;
	}

	/**
	 * @param budgetModifiedDate the budgetModifiedDate to set
	 */
	public void setBudgetModifiedDate(String budgetModifiedDate)
	{
		this.budgetModifiedDate = budgetModifiedDate;
	}

	/**
	 * @return the budgetModifiedByStaffId
	 */
	public String getBudgetModifiedByStaffId()
	{
		return budgetModifiedByStaffId;
	}

	/**
	 * @param budgetModifiedByStaffId the budgetModifiedByStaffId to set
	 */
	public void setBudgetModifiedByStaffId(String budgetModifiedByStaffId)
	{
		this.budgetModifiedByStaffId = budgetModifiedByStaffId;
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

	@Override
	public String toString()
	{
		return "AwardBean [procurementId=" + procurementId + ", proposalId=" + proposalId + ", epin=" + epin
				+ ", contractNumber=" + contractNumber + ", contractStatus=" + contractStatus + ", awardAmount="
				+ awardAmount + ", providerName=" + providerName + ", status=" + status + ", procurementAwardEpin="
				+ procurementAwardEpin + ", awardAgencyId=" + awardAgencyId + ", awardedVendorName="
				+ awardedVendorName + ", vendorFmsId=" + vendorFmsId + ", epinType=" + epinType
				+ ", procurementEpinId=" + procurementEpinId + ", awardEpinId=" + awardEpinId + ", awardDocumentId="
				+ awardDocumentId + ", awardId=" + awardId + ", isFinancial=" + isFinancial + ", contractTypeId="
				+ contractTypeId + ", organizationId=" + organizationId + ", contractId=" + contractId + ", ctNumber="
				+ ctNumber + ", aptMilestone=" + aptMilestone + ", organizationLegalName=" + organizationLegalName
				+ ", mocsApprovalDate=" + mocsApprovalDate + ", dateSentToComptroller=" + dateSentToComptroller
				+ ", registrationDate=" + registrationDate + ", actions=" + actions + ", userRole=" + userRole
				+ ", paginationEnable=" + paginationEnable + ", contractStartDate=" + contractStartDate
				+ ", contractEndDate=" + contractEndDate + ", contractTitle=" + contractTitle
				+ ", contractModifiedDate=" + contractModifiedDate + ", contractModifiedBy=" + contractModifiedBy
				+ ", orgType=" + orgType + ", budgetId=" + budgetId + ", budgetModifiedDate=" + budgetModifiedDate
				+ ", budgetModifiedByStaffId=" + budgetModifiedByStaffId + ", procurementTitle=" + procurementTitle
				+ ", procurementStatus=" + procurementStatus + ", evaluationPoolMappingId=" + evaluationPoolMappingId
				+ ", isOpenEndedRFP=" + isOpenEndedRFP + ", evaluationGroupId=" + evaluationGroupId
				+ ", awardNegotiationFlag=" + awardNegotiationFlag + ", refAptEpinId=" + refAptEpinId
				+ ", roleCurrent=" + roleCurrent
				+ ", procurementAgencyId=" + procurementAgencyId + "]";
	}

	public String getRoleCurrent() {
		return roleCurrent;
	}

	public void setRoleCurrent(String roleCurrent) {
		this.roleCurrent = roleCurrent;
	}

}
