package com.nyc.hhs.model;

import java.util.Date;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

public class EPinDetailBean
{
	@Length(max = 30)
	private String epinId = HHSConstants.EMPTY_STRING;
	private String procurementStartDate = HHSConstants.EMPTY_STRING;
	private String agencyDiv = HHSConstants.EMPTY_STRING;
	private String projProg = HHSConstants.EMPTY_STRING;
	private String description = HHSConstants.EMPTY_STRING;
	@Length(max = 20)
	private String agencyId = HHSConstants.EMPTY_STRING;
	private String procMethod = HHSConstants.EMPTY_STRING;
	private String programName = HHSConstants.EMPTY_STRING;
	private String vendorFmsId = HHSConstants.EMPTY_STRING;
	private String vendorFmsName = HHSConstants.EMPTY_STRING;
	private String contractStart = HHSConstants.EMPTY_STRING;
	private String contractEnd = HHSConstants.EMPTY_STRING;
	private String contractValue = HHSConstants.EMPTY_STRING;
	private String providerLegalName = HHSConstants.EMPTY_STRING;
	private String contractTitle = HHSConstants.EMPTY_STRING;
	private String procDescription = HHSConstants.EMPTY_STRING;
	private String provLegalName = HHSConstants.EMPTY_STRING;
	private String awardEpin = HHSConstants.EMPTY_STRING;
	private String contractId = HHSConstants.EMPTY_STRING;
	private String programNameId = HHSConstants.EMPTY_STRING;
	private String awardAgencyId = HHSConstants.EMPTY_STRING;
	private String contractTypeId = HHSConstants.EMPTY_STRING;
	private String contractSourceId = HHSConstants.EMPTY_STRING;
	private String programId = HHSConstants.EMPTY_STRING;
	private String contractAmount = HHSConstants.EMPTY_STRING;
	private String statusId = HHSConstants.EMPTY_STRING;
	private String registrationFlag = HHSConstants.EMPTY_STRING;
	private String updateFlag = HHSConstants.EMPTY_STRING;
	private String prevStatusId = HHSConstants.EMPTY_STRING;
	private String createByUserId = HHSConstants.EMPTY_STRING;
	private String modifyByUserId = HHSConstants.EMPTY_STRING;
	private String organizationId = HHSConstants.EMPTY_STRING;
	private String parentContractId = HHSConstants.EMPTY_STRING;
	private String ctExtNum = HHSConstants.EMPTY_STRING;
	@RegExp(value ="^\\d{0,22}")
	private String procurementId = HHSConstants.EMPTY_STRING;
	private String amendmentReason = HHSConstants.EMPTY_STRING;
	private String amendmentAmount = HHSConstants.EMPTY_STRING;
	private String amendmentStart = HHSConstants.EMPTY_STRING;
	private String amendmentEnd = HHSConstants.EMPTY_STRING;
	private String proposedContractEnd = HHSConstants.EMPTY_STRING;
	private String amendValue = HHSConstants.EMPTY_STRING;
	private String newTotalContractAmount = HHSConstants.EMPTY_STRING;
	private String procurementTitle = HHSConstants.EMPTY_STRING;
	private String discrepancyFlag = HHSConstants.EMPTY_STRING;
	private String deleteFlag = HHSConstants.EMPTY_STRING;
	private String chkContractCertFundsFlag = HHSConstants.EMPTY_STRING;
	private Boolean launchCOF = true;
	private String procurementMethod = HHSConstants.EMPTY_STRING;
	private Date contractStartDate;
	private Date contractEndDate;
	private String budgetStartYear;
	/* R6: Field added for EPIN Validation */
	private String refAptEpinId = HHSConstants.EMPTY_STRING;
	
	
	//START || Changes done for enhancement 6482 for Release 3.8.0
	private String agencyName; 
	private List<ProgramNameInfo> programNameList;
	
	//Start QC 9401 R 8.5
	private String status; 
	
	/**
	 * @return the statusId
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param statusId the statusId to set
	 */
	public void setStatus(String status)
	{
		this.status = status;
	}
  //End QC 9401 R 8.5
	
	

	/**
	 * @return the programNameList
	 */
	public List<ProgramNameInfo> getProgramNameList()
	{
		return programNameList;
	}

	/**
	 * @param programNameList the programNameList to set
	 */
	public void setProgramNameList(List<ProgramNameInfo> programNameList)
	{
		this.programNameList = programNameList;
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
	//END || Changes done for enhancement 6482 for Release 3.8.0
	
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
	 * @return the msEpinId
	 */
	public String getEpinId()
	{
		return epinId;
	}

	/**
	 * @return the amendmentReason
	 */
	public String getAmendmentReason()
	{
		return amendmentReason;
	}

	/**
	 * @param amendmentReason the amendmentReason to set
	 */
	public void setAmendmentReason(String amendmentReason)
	{
		this.amendmentReason = amendmentReason;
	}

	/**
	 * @param msEpinId the msEpinId to set
	 */
	public void setEpinId(String asEpinId)
	{
		this.epinId = asEpinId;
	}

	/**
	 * @return the moProcurementStartDate
	 */
	public String getProcurementStartDate()
	{
		return procurementStartDate;
	}

	/**
	 * @param moProcurementStartDate the moProcurementStartDate to set
	 */
	public void setProcurementStartDate(String asProcurementStartDate)
	{
		this.procurementStartDate = asProcurementStartDate;
	}

	/**
	 * @return the msAgencyDiv
	 */
	public String getAgencyDiv()
	{
		return agencyDiv;
	}

	/**
	 * @param msAgencyDiv the msAgencyDiv to set
	 */
	public void setAgencyDiv(String asAgencyDiv)
	{
		this.agencyDiv = asAgencyDiv;
	}

	/**
	 * @return the msProjProg
	 */
	public String getProjProg()
	{
		return projProg;
	}

	/**
	 * @param msProjProg the msProjProg to set
	 */
	public void setProjProg(String asProjProg)
	{
		this.projProg = asProjProg;
	}

	/**
	 * @return the msDescription
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * @param msDescription the msDescription to set
	 */
	public void setDescription(String asDescription)
	{
		this.description = asDescription;
	}

	/**
	 * @return the msAgencyId
	 */
	public String getAgencyId()
	{
		return agencyId;
	}

	/**
	 * @param msAgencyId the msAgencyId to set
	 */
	public void setAgencyId(String asAgencyId)
	{
		this.agencyId = asAgencyId;
	}

	/**
	 * @return the procMethod
	 */
	public String getProcMethod()
	{
		return procMethod;
	}

	/**
	 * @param procMethod the procMethod to set
	 */
	public void setProcMethod(String procMethod)
	{
		this.procMethod = procMethod;
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
	 * @return the vendorFmsName
	 */
	public String getVendorFmsName()
	{
		return vendorFmsName;
	}

	/**
	 * @param vendorFmsName the vendorFmsName to set
	 */
	public void setVendorFmsName(String vendorFmsName)
	{
		this.vendorFmsName = vendorFmsName;
	}

	/**
	 * @return the contractStart
	 */
	public String getContractStart()
	{
		return contractStart;
	}

	/**
	 * @param contractStart the contractStart to set
	 */
	public void setContractStart(String contractStart)
	{
		this.contractStart = contractStart;
	}

	/**
	 * @return the contractEnd
	 */
	public String getContractEnd()
	{
		return contractEnd;
	}

	/**
	 * @param contractEnd the contractEnd to set
	 */
	public void setContractEnd(String contractEnd)
	{
		this.contractEnd = contractEnd;
	}

	/**
	 * @return the contractValue
	 */
	public String getContractValue()
	{
		return contractValue;
	}

	/**
	 * @param contractValue the contractValue to set
	 */
	public void setContractValue(String contractValue)
	{
		this.contractValue = contractValue;
	}

	/**
	 * @return the providerLegalName
	 */
	public String getProviderLegalName()
	{
		return providerLegalName;
	}

	/**
	 * @param providerLegalName the providerLegalName to set
	 */
	public void setProviderLegalName(String providerLegalName)
	{
		this.providerLegalName = providerLegalName;
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
	 * @return the procDescription
	 */
	public String getProcDescription()
	{
		return procDescription;
	}

	/**
	 * @param procDescription the procDescription to set
	 */
	public void setProcDescription(String procDescription)
	{
		this.procDescription = procDescription;
	}

	/**
	 * @return the provLegalName
	 */
	public String getProvLegalName()
	{
		return provLegalName;
	}

	/**
	 * @param provLegalName the provLegalName to set
	 */
	public void setProvLegalName(String provLegalName)
	{
		this.provLegalName = provLegalName;
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
	 * @return the programNameId
	 */
	public String getProgramNameId()
	{
		return programNameId;
	}

	/**
	 * @param programNameId the programNameId to set
	 */
	public void setProgramNameId(String programNameId)
	{
		this.programNameId = programNameId;
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
	 * @return the contractSourceId
	 */
	public String getContractSourceId()
	{
		return contractSourceId;
	}

	/**
	 * @param contractSourceId the contractSourceId to set
	 */
	public void setContractSourceId(String contractSourceId)
	{
		this.contractSourceId = contractSourceId;
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
	 * @return the registrationFlag
	 */
	public String getRegistrationFlag()
	{
		return registrationFlag;
	}

	/**
	 * @param registrationFlag the registrationFlag to set
	 */
	public void setRegistrationFlag(String registrationFlag)
	{
		this.registrationFlag = registrationFlag;
	}

	/**
	 * @return the updateFlag
	 */
	public String getUpdateFlag()
	{
		return updateFlag;
	}

	/**
	 * @param updateFlag the updateFlag to set
	 */
	public void setUpdateFlag(String updateFlag)
	{
		this.updateFlag = updateFlag;
	}

	/**
	 * @return the prevStatusId
	 */
	public String getPrevStatusId()
	{
		return prevStatusId;
	}

	/**
	 * @param prevStatusId the prevStatusId to set
	 */
	public void setPrevStatusId(String prevStatusId)
	{
		this.prevStatusId = prevStatusId;
	}

	/**
	 * @return the createByUserId
	 */
	public String getCreateByUserId()
	{
		return createByUserId;
	}

	/**
	 * @param createByUserId the createByUserId to set
	 */
	public void setCreateByUserId(String createByUserId)
	{
		this.createByUserId = createByUserId;
	}

	/**
	 * @return the modifyByUserId
	 */
	public String getModifyByUserId()
	{
		return modifyByUserId;
	}

	/**
	 * @param modifyByUserId the modifyByUserId to set
	 */
	public void setModifyByUserId(String modifyByUserId)
	{
		this.modifyByUserId = modifyByUserId;
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
	 * @return the ctExtNum
	 */
	public String getCtExtNum()
	{
		return ctExtNum;
	}

	/**
	 * @param ctExtNum the ctExtNum to set
	 */
	public void setCtExtNum(String ctExtNum)
	{
		this.ctExtNum = ctExtNum;
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
	 * @return the amendmentAmount
	 */
	public String getAmendmentAmount()
	{
		return amendmentAmount;
	}

	/**
	 * @param amendmentAmount the amendmentAmount to set
	 */
	public void setAmendmentAmount(String amendmentAmount)
	{
		this.amendmentAmount = amendmentAmount;
	}

	/**
	 * @return the amendmentStart
	 */
	public String getAmendmentStart()
	{
		return amendmentStart;
	}

	/**
	 * @param amendmentStart the amendmentStart to set
	 */
	public void setAmendmentStart(String amendmentStart)
	{
		this.amendmentStart = amendmentStart;
	}

	/**
	 * @return the amendmentEnd
	 */
	public String getAmendmentEnd()
	{
		return amendmentEnd;
	}

	/**
	 * @param amendmentEnd the amendmentEnd to set
	 */
	public void setAmendmentEnd(String amendmentEnd)
	{
		this.amendmentEnd = amendmentEnd;
	}

	/**
	 * @return the proposedContractEnd
	 */
	public String getProposedContractEnd()
	{
		return proposedContractEnd;
	}

	/**
	 * @param proposedContractEnd the proposedContractEnd to set
	 */
	public void setProposedContractEnd(String proposedContractEnd)
	{
		this.proposedContractEnd = proposedContractEnd;
	}

	/**
	 * @return the amendValue
	 */
	public String getAmendValue()
	{
		return amendValue;
	}

	/**
	 * @param amendValue the amendValue to set
	 */
	public void setAmendValue(String amendValue)
	{
		this.amendValue = amendValue;
	}

	/**
	 * @return the newTotalContractAmount
	 */
	public String getNewTotalContractAmount()
	{
		return newTotalContractAmount;
	}

	/**
	 * @param newTotalContractAmount the newTotalContractAmount to set
	 */
	public void setNewTotalContractAmount(String newTotalContractAmount)
	{
		this.newTotalContractAmount = newTotalContractAmount;
	}

	public String getProcurementTitle()
	{
		return procurementTitle;
	}

	public void setProcurementTitle(String procurementTitle)
	{
		this.procurementTitle = procurementTitle;
	}

	/**
	 * @return the discrepancyFlag
	 */
	public String getDiscrepancyFlag()
	{
		return discrepancyFlag;
	}

	/**
	 * @param discrepancyFlag the discrepancyFlag to set
	 */
	public void setDiscrepancyFlag(String discrepancyFlag)
	{
		this.discrepancyFlag = discrepancyFlag;
	}

	/**
	 * @return the deleteFlag
	 */
	public String getDeleteFlag()
	{
		return deleteFlag;
	}

	/**
	 * @param deleteFlag the deleteFlag to set
	 */
	public void setDeleteFlag(String deleteFlag)
	{
		this.deleteFlag = deleteFlag;
	}

	/**
	 * @return the chkContractCertFundsFlag
	 */
	public String getChkContractCertFundsFlag()
	{
		return chkContractCertFundsFlag;
	}

	/**
	 * @param chkContractCertFundsFlag the chkContractCertFundsFlag to set
	 */
	public void setChkContractCertFundsFlag(String chkContractCertFundsFlag)
	{
		this.chkContractCertFundsFlag = chkContractCertFundsFlag;
	}

	/**
	 * @return the procurementMethod
	 */
	public String getProcurementMethod()
	{
		return procurementMethod;
	}

	/**
	 * @param procurementMethod the procurementMethod to set
	 */
	public void setProcurementMethod(String procurementMethod)
	{
		this.procurementMethod = procurementMethod;
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
	 * @return the budgetStartYear
	 */
	public String getBudgetStartYear() {
		return budgetStartYear;
	}

	/**
	 * @param budgetStartYear the budgetStartYear to set
	 */
	public void setBudgetStartYear(String budgetStartYear) {
		this.budgetStartYear = budgetStartYear;
	}

	/*
	 * R6 for APT changes start
	 */
	public String getRefAptEpinId()
	{
		return refAptEpinId;
	}

	public void setRefAptEpinId(String refAptEpinId)
	{
		this.refAptEpinId = refAptEpinId;
	}
	
	
	/*
	 * [Start]R7.12.0 QC9311 Minimize Debug
	 */
	
	/*
	 * R6 for APT changes ends
	 */
	@Override
	public String toString()
	{
		return filterToString();
		
/*		return "EPinDetailBean [epinId=" + epinId + ", procurementStartDate=" + procurementStartDate + ", agencyDiv="
				+ agencyDiv + ", projProg=" + projProg + ", description=" + description + ", agencyId=" + agencyId
				+ ", procMethod=" + procMethod + ", programName=" + programName + ", vendorFmsId=" + vendorFmsId
				+ ", vendorFmsName=" + vendorFmsName + ", contractStart=" + contractStart + ", contractEnd="
				+ contractEnd + ", contractValue=" + contractValue + ", providerLegalName=" + providerLegalName
				+ ", contractTitle=" + contractTitle + ", procDescription=" + procDescription + ", provLegalName="
				+ provLegalName + ", awardEpin=" + awardEpin + ", contractId=" + contractId + ", programNameId="
				+ programNameId + ", awardAgencyId=" + awardAgencyId + ", contractTypeId=" + contractTypeId
				+ ", contractSourceId=" + contractSourceId + ", programId=" + programId + ", contractAmount="
				+ contractAmount + ", statusId=" + statusId + ", registrationFlag=" + registrationFlag
				+ ", updateFlag=" + updateFlag + ", prevStatusId=" + prevStatusId + ", createByUserId="
				+ createByUserId + ", modifyByUserId=" + modifyByUserId + ", organizationId=" + organizationId
				+ ", parentContractId=" + parentContractId + ", ctExtNum=" + ctExtNum + ", procurementId="
				+ procurementId + ", amendmentReason=" + amendmentReason + ", amendmentAmount=" + amendmentAmount
				+ ", amendmentStart=" + amendmentStart + ", amendmentEnd=" + amendmentEnd + ", proposedContractEnd="
				+ proposedContractEnd + ", amendValue=" + amendValue + ", newTotalContractAmount="
				+ newTotalContractAmount + ", procurementTitle=" + procurementTitle + ", discrepancyFlag="
				+ discrepancyFlag + ", deleteFlag=" + deleteFlag + ", chkContractCertFundsFlag="
				+ chkContractCertFundsFlag + ", launchCOF=" + launchCOF + ", procurementMethod=" + procurementMethod
				+ ", contractStartDate=" + contractStartDate + ", contractEndDate=" + contractEndDate
				+ ", budgetStartYear=" + budgetStartYear + ", refAptEpinId=" + refAptEpinId + ", agencyName="
				+ agencyName + ", programNameList=" + programNameList + "]";*/
	}
	
	
	private String filterToString(){
		StringBuilder sb = new StringBuilder();
		
		sb.append("EPinDetailBean [");
		if( epinId != null && !epinId.isEmpty() ) {sb.append("epinId=" + epinId );		}
		if( procurementStartDate != null && !procurementStartDate.isEmpty() ) {sb.append(", procurementStartDate=" + procurementStartDate) ;} 
		if( agencyDiv != null && !agencyDiv.isEmpty() ) {sb.append(", agencyDiv="+ agencyDiv ); }
		if( projProg != null && !projProg.isEmpty() ) {sb.append(", projProg=" + projProg ); }
		if( description != null && !description.isEmpty() ) {sb.append(", description=" + description );}
		if( agencyId != null && !agencyId.isEmpty() ) {sb.append(", agencyId=" + agencyId ); }
		if( procMethod != null && !procMethod.isEmpty() ) {sb.append( ", procMethod=" + procMethod ); }
		if( programName != null && !programName.isEmpty() ) {sb.append(", programName=" + programName ); }
		if( vendorFmsId != null && !vendorFmsId.isEmpty() ) {sb.append(", vendorFmsId=" + vendorFmsId ); }
		if( vendorFmsName != null && !vendorFmsName.isEmpty() ) {sb.append(", vendorFmsName=" + vendorFmsName ); }
		if( contractStart != null && !contractStart.isEmpty() ) {sb.append(", contractStart=" + contractStart ); }
		if( contractEnd != null && !contractEnd.isEmpty() ) {sb.append(", contractEnd=" + contractEnd ); }
		if( contractValue != null && !contractValue.isEmpty() ) {sb.append(", contractValue=" + contractValue ); }
		if( providerLegalName != null && !providerLegalName.isEmpty() ) {sb.append(", providerLegalName=" + providerLegalName ); }
		if( contractTitle != null && !contractTitle.isEmpty() ) {sb.append(", contractTitle=" + contractTitle ); }
		if( procDescription != null && !procDescription.isEmpty() ) {sb.append(", procDescription=" + procDescription ); }
		if( provLegalName != null && !provLegalName.isEmpty() ) {sb.append(", provLegalName=" + provLegalName ); }
		if( awardEpin != null && !awardEpin.isEmpty() ) {sb.append(", awardEpin=" + awardEpin ); }
		if( contractId != null && !contractId.isEmpty() ) {sb.append(", contractId=" + contractId ); }
		if( programNameId != null && !programNameId.isEmpty() ) {sb.append(", programNameId=" + programNameId ); }
		if( awardAgencyId != null && !awardAgencyId.isEmpty() ) {sb.append(", awardAgencyId=" + awardAgencyId ); }
		if( contractTypeId != null && !contractTypeId.isEmpty() ) {sb.append(", contractTypeId=" + contractTypeId ) ;}
		if( contractSourceId != null && !contractSourceId.isEmpty() ) {sb.append(", contractSourceId=" + contractSourceId ); }
		if( programId != null && !programId.isEmpty() ) {sb.append(", programId=" + programId ); }
		if( contractAmount != null && !contractAmount.isEmpty() ) {sb.append(", contractAmount=" + contractAmount ); }
		if( statusId != null && !statusId.isEmpty() ) {sb.append(", statusId=" + statusId ); }
		if( status != null && !status.isEmpty() ) {sb.append(", status=" + status ); } // QC 9401 R 8.5.0
		if( registrationFlag != null && !registrationFlag.isEmpty() ) {sb.append(", registrationFlag=" + registrationFlag ); }
		if( updateFlag != null && !updateFlag.isEmpty() ) {sb.append(", updateFlag=" + updateFlag ) ;}
		if( prevStatusId != null && !prevStatusId.isEmpty() ) {sb.append(", prevStatusId=" + prevStatusId ); }
		if( createByUserId != null && !createByUserId.isEmpty() ) {sb.append(", createByUserId=" + createByUserId ) ;}
		if( modifyByUserId != null && !modifyByUserId.isEmpty() ) {sb.append(", modifyByUserId=" + modifyByUserId ); }
		if( organizationId != null && !organizationId.isEmpty() ) {sb.append(", organizationId=" + organizationId ); }
		if( parentContractId != null && !parentContractId.isEmpty() ) {sb.append(", parentContractId=" + parentContractId ); }
		if( ctExtNum != null && !ctExtNum.isEmpty() ) {sb.append(", ctExtNum=" + ctExtNum ); }
		if( procurementId != null && !procurementId.isEmpty() ) {sb.append(", procurementId=" + procurementId ); }
		if( amendmentReason != null && !amendmentReason.isEmpty() ) {sb.append(", amendmentReason=" + amendmentReason ); }
		if( amendmentAmount != null && !amendmentAmount.isEmpty() ) {sb.append(", amendmentAmount=" + amendmentAmount ); }
		if( amendmentStart != null && !amendmentStart.isEmpty() ) {sb.append(", amendmentStart=" + amendmentStart ); }
		if( amendmentEnd != null && !amendmentEnd.isEmpty() ) {sb.append(", amendmentEnd=" + amendmentEnd ) ;}
		if( proposedContractEnd != null && !proposedContractEnd.isEmpty() ) {sb.append(", proposedContractEnd=" + proposedContractEnd ); }
		if( amendValue != null && !amendValue.isEmpty() ) {sb.append(", amendValue=" + amendValue ); }
		if( newTotalContractAmount != null && !newTotalContractAmount.isEmpty() ) {sb.append(", newTotalContractAmount=" + newTotalContractAmount ); }
		if( procurementTitle != null && !procurementTitle.isEmpty() ) {sb.append(", procurementTitle=" + procurementTitle ); }
		if( discrepancyFlag != null && !discrepancyFlag.isEmpty() ) {sb.append(", discrepancyFlag=" + discrepancyFlag ); }
		if( deleteFlag != null && !deleteFlag.isEmpty() ) {sb.append(", deleteFlag=" + deleteFlag ); }
		if( chkContractCertFundsFlag != null && !chkContractCertFundsFlag.isEmpty() ) {sb.append(", chkContractCertFundsFlag=" + chkContractCertFundsFlag ); }
		if( launchCOF != null  ) {sb.append(", launchCOF=" + launchCOF ); }
		if( procurementMethod != null && !procurementMethod.isEmpty() ) {sb.append(", procurementMethod=" + procurementMethod ); }
		if( contractStartDate != null  ) {sb.append(", contractStartDate=" + contractStartDate ) ;}
		if( contractEndDate != null  ) {sb.append(", contractEndDate=" + contractEndDate ) ;}
		if( budgetStartYear != null && !budgetStartYear.isEmpty() ) {sb.append(", budgetStartYear=" + budgetStartYear ) ;}
		if( refAptEpinId != null && !refAptEpinId.isEmpty() ) {sb.append(", refAptEpinId=" + refAptEpinId ); }
		if( agencyName != null && !agencyName.isEmpty() ) {sb.append(", agencyName=" + agencyName ) ;}
		if( programNameList != null && !programNameList.isEmpty() ) {sb.append(", programNameList=" + programNameList ); }
		
		sb.append(" ]");

		return sb.toString();
	}
	/*
	 * [End]R7.12.0 QC9311 Minimize Debug
	 */

}
