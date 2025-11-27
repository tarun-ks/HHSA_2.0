package com.nyc.hhs.model;

import java.math.BigDecimal;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;


/**
 * 
 */
public class ContractCOFDetails {

	@RegExp(value ="^\\d{0,22}")
	 private String contractId                  = HHSConstants.EMPTY_STRING;
	 private String procurementTitle            = HHSConstants.EMPTY_STRING;
	 private String epin                  		= HHSConstants.EMPTY_STRING;
	 private String awardEpin                   = HHSConstants.EMPTY_STRING;
	 private String agencyName                  = HHSConstants.EMPTY_STRING;
	 private String agencyCode                  = HHSConstants.EMPTY_STRING;
	 private BigDecimal    contractAmount       = null;
	 @RegExp(value ="^\\d{0,22}")
	 private String procurementId               = HHSConstants.EMPTY_STRING;
     private String contractStartDate           = HHSConstants.EMPTY_STRING;
     private String contract_endDate            = HHSConstants.EMPTY_STRING;
     private String organizationLegalName       = HHSConstants.EMPTY_STRING;
     private String firstName                   = HHSConstants.EMPTY_STRING;
     private String lastName                    = HHSConstants.EMPTY_STRING;
     private String approvedFirstName           = HHSConstants.EMPTY_STRING;
     private String approvedLastName            = HHSConstants.EMPTY_STRING;
     private String approvedBy                  = HHSConstants.EMPTY_STRING;
     private String approvedDate                = HHSConstants.EMPTY_STRING;
	 private String unitOfAppropriation         = HHSConstants.EMPTY_STRING;
     private String budgetCode                  = HHSConstants.EMPTY_STRING;
     private String objectCode                  = HHSConstants.EMPTY_STRING;
     private String subObjectCode               = HHSConstants.EMPTY_STRING;
     private String reportingCategory           = HHSConstants.EMPTY_STRING;
     private String fiscalYearId                = HHSConstants.EMPTY_STRING;
     private BigDecimal   amount                = null;
     private BigDecimal   federalAmount         = null;
     private BigDecimal   stateAmount           = null;
     private BigDecimal   cityAmount            = null;
     private BigDecimal   otherAmount           = null;
     private String modifiedDate                = HHSConstants.EMPTY_STRING;
     private String modifiedByUserId            = HHSConstants.EMPTY_STRING;


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
	public String getAgencyCode() {
		return agencyCode;
	}
	public void setAgencyCode(String agencyCode) {
		this.agencyCode = agencyCode;
	}
	public BigDecimal getContractAmount() {
		return contractAmount;
	}
	public void setContractAmount(BigDecimal contractAmount) {
		this.contractAmount = contractAmount;
	}
	public String getProcurementId() {
		return procurementId;
	}
	public void setProcurementId(String procurementId) {
		this.procurementId = procurementId;
	}
	public String getContractStartDate() {
		return contractStartDate;
	}
	public void setContractStartDate(String contractStartDate) {
		this.contractStartDate = contractStartDate;
	}
	public String getContract_endDate() {
		return contract_endDate;
	}
	public void setContract_endDate(String contract_endDate) {
		this.contract_endDate = contract_endDate;
	}
	public String getOrganizationLegalName() {
		return organizationLegalName;
	}
	public void setOrganizationLegalName(String organizationLegalName) {
		this.organizationLegalName = organizationLegalName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getApprovedFirstName() {
		return approvedFirstName;
	}
	public void setApprovedFirstName(String approvedFirstName) {
		this.approvedFirstName = approvedFirstName;
	}
	public String getApprovedLastName() {
		return approvedLastName;
	}
	public void setApprovedLastName(String approvedLastName) {
		this.approvedLastName = approvedLastName;
	}
	public String getApprovedBy() {
		return approvedBy;
	}
	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}
	public String getUnitOfAppropriation() {
		return unitOfAppropriation;
	}
	public void setUnitOfAppropriation(String unitOfAppropriation) {
		this.unitOfAppropriation = unitOfAppropriation;
	}
	public String getBudgetCode() {
		return budgetCode;
	}
	public void setBudgetCode(String budgetCode) {
		this.budgetCode = budgetCode;
	}
	public String getObjectCode() {
		return objectCode;
	}
	public void setObjectCode(String objectCode) {
		this.objectCode = objectCode;
	}
	public String getSubObjectCode() {
		return subObjectCode;
	}
	public void setSubObjectCode(String subObjectCode) {
		this.subObjectCode = subObjectCode;
	}
	public String getReportingCategory() {
		return reportingCategory;
	}
	public void setReportingCategory(String reportingCategory) {
		this.reportingCategory = reportingCategory;
	}
	public String getFiscalYearId() {
		return fiscalYearId;
	}
	public void setFiscalYearId(String fiscalYearId) {
		this.fiscalYearId = fiscalYearId;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public BigDecimal getFederalAmount() {
		return federalAmount;
	}
	public void setFederalAmount(BigDecimal federalAmount) {
		this.federalAmount = federalAmount;
	}
	public BigDecimal getStateAmount() {
		return stateAmount;
	}
	public void setStateAmount(BigDecimal stateAmount) {
		this.stateAmount = stateAmount;
	}
	public BigDecimal getCityAmount() {
		return cityAmount;
	}
	public void setCityAmount(BigDecimal cityAmount) {
		this.cityAmount = cityAmount;
	}
	public BigDecimal getOtherAmount() {
		return otherAmount;
	}
	public void setOtherAmount(BigDecimal otherAmount) {
		this.otherAmount = otherAmount;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getModifiedByUserId() {
		return modifiedByUserId;
	}
	public void setModifiedByUserId(String modifiedByUserId) {
		this.modifiedByUserId = modifiedByUserId;
	}
	public String getProcurementTitle() {
		return procurementTitle;
	}
	public void setProcurementTitle(String procurementTitle) {
		this.procurementTitle = procurementTitle;
	}
	public String getEpin() {
		return epin;
	}
	public void setEpin(String epin) {
		this.epin = epin;
	}
	public String getAwardEpin() {
		return awardEpin;
	}
	public void setAwardEpin(String awardEpin) {
		this.awardEpin = awardEpin;
	}
    public String getApprovedDate() {
		return approvedDate;
	}
	public void setApprovedDate(String approvedDate) {
		this.approvedDate = approvedDate;
	}

	public ProcurementCOF toProcurementCOF(){
		ProcurementCOF loPprocureCof = new ProcurementCOF();
		
		loPprocureCof.setUpdatedContractId(this.contractId);
		loPprocureCof.setProcurementTitle(procurementTitle);
		loPprocureCof.setProcEpin(this.epin);
		loPprocureCof.setAwardEpin(this.awardEpin);
		loPprocureCof.setAgencyName(this.agencyName);
		loPprocureCof.setAgencyId(this.agencyCode);
		loPprocureCof.setAgencyCode(this.agencyCode);

		loPprocureCof.setContractStartDate(this.contractStartDate);
		loPprocureCof.setContractEndDate(this.contract_endDate);
		loPprocureCof.setContractValue(String.valueOf(this.contractAmount));
		
		loPprocureCof.setUserFirstName(this.firstName);
		loPprocureCof.setUserLastName(this.lastName);
		loPprocureCof.setModifiedDate(this.modifiedDate);
		
		
		loPprocureCof.setApproverFirstName(this.approvedFirstName);
		loPprocureCof.setApproverLastName(this.approvedLastName);
		loPprocureCof.setApprovedDate(this.approvedDate);

		loPprocureCof.setAmendedContractValue(String.valueOf(this.contractAmount));
		loPprocureCof.setAmendmentStartDate(this.contractStartDate);
		loPprocureCof.setAmendmentEndDate(this.contract_endDate);
		loPprocureCof.setAmendmentEpin(this.awardEpin);
		loPprocureCof.setAmendmentTitle(this.procurementTitle);
		loPprocureCof.setNewContractValue(String.valueOf(this.contractAmount));

		return loPprocureCof;
	}

	public FundingAllocationBean toFundingAllocationBean( FundingAllocationBean loFunding , int inx){
		loFunding.setFyAmount(0, amount);
		return loFunding;

	}

	public FundingAllocationBean toFundingAllocationBean(String asSource){
		FundingAllocationBean loFunding = new FundingAllocationBean();
		loFunding.setFundingSource(asSource);
		loFunding.setFyAmount(0, amount);

		return loFunding;
	}           
		
	
}
