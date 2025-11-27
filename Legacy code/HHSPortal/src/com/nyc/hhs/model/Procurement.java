/**
 * 
 */
package com.nyc.hhs.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Email;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.HHSUtil;

/**
 * This class is bean class that maintains procurement details
 */
public class Procurement extends ProcurementInfo
{
	//@NotBlank
	private String agencyId;
	private String agencyName;
	private String lastPublishedDate;
	private String releaseDate;
	private String providerStatus;
	private String proposalDueDate;
	private String contractStartDate;
	//@NotBlank
	private String programName;
	//@NotBlank
	private String accPrimaryContact;
	//@NotBlank
	private String accSecondaryContact;
	//@NotBlank
	private String agecncyPrimaryContact;
	//@NotBlank
	private String agecncySecondaryContact;
	//@NotBlank
	@Length(max = 60)
	@Email
	private String email;
	//@NotBlank
	@Length(max = 3500)
	private String procurementDescription;
	private Integer estNumberOfContracts;
	private BigDecimal estProcurementValue;
	@Length(max = 250)
	private String linkToConceptReport;
	private String rfpReleaseDatePlanned;
	private String rfpReleaseDateUpdated;
	private String proposalDueDatePlanned;
	private String proposalDueDateUpdated;
	private String contractStartDatePlanned;
	private String contractStartDateUpdated;
	private String firstRFPEvalDatePlanned;
	private String firstRFPEvalDateUpdated;
	private String finalRFPEvalDatePlanned;
	private String finalRFPEvalDateUpdated;
	private String preProposalConferenceDatePlanned;
	private String preProposalConferenceDateUpdated;
	private String contractEndDatePlanned;
	private String contractEndDateUpdated;
	private String evaluatorTrainingDatePlanned;
	private String evaluatorTrainingDateUpdated;
	private String firstEvalCompletionDatePlanned;
	private String firstEvalCompletionDateUpdated;
	private String finalEvalCompletionDatePlanned;
	private String finalEvalCompletionDateUpdated;
	private String awardSelectionDatePlanned;
	private String awardSelectionDateUpdated;
	private String serviceName;
	private String activeProcStatus;
	private String inactiveProcStatus;
	private Integer programId;
	//@NotBlank
	private String userName;
	//@NotBlank
	private String password;
	private Date lastPublishDate;
	private String lastUpdatedUser;
	private String lastPublishedByUser;
	private String userId;
	private String createdBy;
	private String modifiedBy;
	private String rfpReleaseUserId;
	private String contractId;
	private String organizationId;
	private String staffId;
	private String assignUserId;
	private String approverUserId;
	private String procUser;
	private String workFlowId;
	private String pcofStatusCode;
	private String lastPublishedFrom;
	private String lastPublishedTo;
	private String releaseFrom;
	private String releaseTo;
	private String proposalDueFrom;
	private String proposalDueTo;
	private String contractStartFrom;
	private String contractStartTo;
	private List<String> procurementStatusList;
	private List<String> providerStatusList;
	private String procurementId;
	private String addendumFlag;
	private String serviceFilter;
	private Date submissionCloseDate;
	private Date rfpReleaseDate;
	private String accPrimaryContactName;
	private String accSecondaryContactName;
	private String agencyPrimaryContactName;
	private String agencySecondaryContactName;
	private String accPrimaryEmail;
	private String accSecondaryEmail;
	private String agencyPrimaryEmail;
	private String agencySecondaryEmail;
	private String serviceUnitRequiredFlag;
	private String procurementFavorite;
	private String isFavorite;
	private String isFavoriteDisplayed = "false";
	private List<String> procurementFavorites;
	private String competitionPoolId;
	private String evaluationGroupId;
	private String evaluationGroupTitle;
	private String competitionPoolTitle;
	private String evalGroupStatus;

	// R5 changes start
	private String pcofConctractStartDate;
	private String pcofConctractEndDate;
	private BigDecimal approvedAmount;
	private BigDecimal psrApprovedAmount;
	private String psrConctractStartDate;
	//R6: Epin valiation - field added for ref epin id PK
	private String refEpinId;
	
	//R7.2.0 QC8914 
	private String procurementStatus;
	private String roleCurrent;
	
	
	
	
	public String getRefEpinId()
	{
		return refEpinId;
	}

	public void setRefEpinId(String refEpinId)
	{
		this.refEpinId = refEpinId;
	}

	public BigDecimal getPsrApprovedAmount()
	{
		return psrApprovedAmount;
	}

	public void setPsrApprovedAmount(BigDecimal psrApprovedAmount)
	{
		this.psrApprovedAmount = psrApprovedAmount;
	}

	public String getPsrConctractStartDate()
	{
		return psrConctractStartDate;
	}

	public void setPsrConctractStartDate(String psrConctractStartDate)
	{
		this.psrConctractStartDate = psrConctractStartDate;
	}

	public String getPsrConctractEndDate()
	{
		return psrConctractEndDate;
	}

	public void setPsrConctractEndDate(String psrConctractEndDate)
	{
		this.psrConctractEndDate = psrConctractEndDate;
	}

	private String psrConctractEndDate;

	/**
	 * @return the pcofConctractStartDate
	 */
	public String getPcofConctractStartDate()
	{
		return pcofConctractStartDate;
	}

	/**
	 * @param pcofConctractStartDate the pcofConctractStartDate to set
	 */
	public void setPcofConctractStartDate(String pcofConctractStartDate)
	{
		this.pcofConctractStartDate = pcofConctractStartDate;
	}

	/**
	 * @return the pcofConctractEndDate
	 */
	public String getPcofConctractEndDate()
	{
		return pcofConctractEndDate;
	}

	/**
	 * @param pcofConctractEndDate the pcofConctractEndDate to set
	 */
	public void setPcofConctractEndDate(String pcofConctractEndDate)
	{
		this.pcofConctractEndDate = pcofConctractEndDate;
	}

	/**
	 * @return the approvedAmount
	 */
	public BigDecimal getApprovedAmount()
	{
		return approvedAmount;
	}

	/**
	 * @param approvedAmount the approvedAmount to set
	 */
	public void setApprovedAmount(BigDecimal approvedAmount)
	{
		this.approvedAmount = approvedAmount;
	}

	// R5 changes ends

	public Date getRfpReleaseDate()
	{
		return rfpReleaseDate;
	}

	public void setRfpReleaseDate(Date rfpReleaseDate)
	{
		this.rfpReleaseDate = rfpReleaseDate;
	}

	/**
	 * @return the submissionCloseDate
	 */
	public Date getSubmissionCloseDate()
	{
		return submissionCloseDate;
	}

	/**
	 * @param submissionCloseDate the submissionCloseDate to set
	 */
	public void setSubmissionCloseDate(Date submissionCloseDate)
	{
		this.submissionCloseDate = submissionCloseDate;
	}

	/**
	 * @return the serviceFilter
	 */
	public String getServiceFilter()
	{
		return serviceFilter;
	}

	/**
	 * @param serviceFilter the serviceFilter to set
	 */
	public void setServiceFilter(String serviceFilter)
	{
		this.serviceFilter = serviceFilter;
	}

	/**
	 * @return the addendumFlag
	 */
	public String getAddendumFlag()
	{
		return addendumFlag;
	}

	/**
	 * @param addendumFlag the addendumFlag to set
	 */
	public void setAddendumFlag(String addendumFlag)
	{
		this.addendumFlag = addendumFlag;
	}

	public Procurement()
	{
		// Begin QC 6531 REL 3.9.0
		setFirstSort(HHSConstants.STATUS_PROCESS_TYPE_ID);
		setSecondSort(HHSConstants.UPD_PROPOSAL_DUE_DATE);
		setFirstSortType(HHSConstants.ASCENDING);
		setSecondSortType(HHSConstants.ASCENDING);
		setSortColumnName(HHSConstants.PROCUREMENT_STATUS);
		setSecondSortDate(true);
		// End QC 6531 REL 3.9.0
	}

	/**
	 * @return the rfpReleaseUserId
	 */
	public String getRfpReleaseUserId()
	{
		return rfpReleaseUserId;
	}

	/**
	 * @param rfpReleaseUserId the rfpReleaseUserId to set
	 */
	public void setRfpReleaseUserId(String rfpReleaseUserId)
	{
		this.rfpReleaseUserId = rfpReleaseUserId;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	/**
	 * @return the modibiedBy
	 */
	public String getModifiedBy()
	{
		return modifiedBy;
	}

	/**
	 * @param modibiedBy the modibiedBy to set
	 */
	public void setModifiedBy(String modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the lastPublishedByUser
	 */
	public String getLastPublishedByUser()
	{
		return lastPublishedByUser;
	}

	/**
	 * @param lastPublishedByUser the lastPublishedByUser to set
	 */
	public void setLastPublishedByUser(String lastPublishedByUser)
	{
		this.lastPublishedByUser = lastPublishedByUser;
	}

	/**
	 * @return the lastUpdatedUser
	 */
	public String getLastUpdatedUser()
	{
		return lastUpdatedUser;
	}

	/**
	 * @param lastUpdatedUser the lastUpdatedUser to set
	 */
	public void setLastUpdatedUser(String lastUpdatedUser)
	{
		this.lastUpdatedUser = lastUpdatedUser;
	}

	/**
	 * @return the lastPublishDate
	 */
	public Date getLastPublishDate()
	{
		return lastPublishDate;
	}

	/**
	 * @param lastPublishDate the lastPublishDate to set
	 */
	public void setLastPublishDate(Date lastPublishDate)
	{
		this.lastPublishDate = lastPublishDate;
	}

	/**
	 * @return the programId
	 */
	public Integer getProgramId()
	{
		return programId;
	}

	/**
	 * @param programId the programId to set
	 */
	public void setProgramId(Integer programId)
	{
		this.programId = programId;
	}

	/**
	 * @return the agencyId
	 */
	public String getAgencyId()
	{
		if (null != agencyId && !agencyId.isEmpty())
		{
			agencyId = agencyId.trim();
		}
		else
		{
			agencyId = null;
		}
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
	 * @return the lastPublishedDate
	 */
	public String getLastPublishedDate()
	{
		return lastPublishedDate;
	}

	/**
	 * @param lastPublishedDate the lastPublishedDate to set
	 */
	public void setLastPublishedDate(String lastPublishedDate)
	{
		this.lastPublishedDate = lastPublishedDate;
	}

	/**
	 * @return the releaseDate
	 */
	public String getReleaseDate()
	{
		return releaseDate;
	}

	/**
	 * @param releaseDate the releaseDate to set
	 */
	public void setReleaseDate(String releaseDate)
	{
		this.releaseDate = releaseDate;
	}

	/**
	 * @return the providerStatus
	 */
	public String getProviderStatus()
	{
		return providerStatus;
	}

	/**
	 * @param providerStatus the providerStatus to set
	 */
	public void setProviderStatus(String providerStatus)
	{
		this.providerStatus = providerStatus;
	}

	/**
	 * @return the proposalDueDate
	 */
	public String getProposalDueDate()
	{
		return proposalDueDate;
	}

	/**
	 * @param proposalDueDate the proposalDueDate to set
	 */
	public void setProposalDueDate(String proposalDueDate)
	{
		this.proposalDueDate = proposalDueDate;
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
	 * @return the programName
	 */
	public String getProgramName()
	{
		if (null != programName && !programName.isEmpty())
		{
			programName = programName.trim();
		}
		else
		{
			programName = null;
		}
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
	 * @return the accPrimaryContact
	 */
	public String getAccPrimaryContact()
	{
		return accPrimaryContact;
	}

	/**
	 * @param accPrimaryContact the accPrimaryContact to set
	 */
	public void setAccPrimaryContact(String accPrimaryContact)
	{
		this.accPrimaryContact = accPrimaryContact;
	}

	/**
	 * @return the accSecondaryContact
	 */
	public String getAccSecondaryContact()
	{
		return accSecondaryContact;
	}

	/**
	 * @param accSecondaryContact the accSecondaryContact to set
	 */
	public void setAccSecondaryContact(String accSecondaryContact)
	{
		this.accSecondaryContact = accSecondaryContact;
	}

	/**
	 * @return the agecncyPrimaryContact
	 */
	public String getAgecncyPrimaryContact()
	{
		return agecncyPrimaryContact;
	}

	/**
	 * @param agecncyPrimaryContact the agecncyPrimaryContact to set
	 */
	public void setAgecncyPrimaryContact(String agecncyPrimaryContact)
	{
		this.agecncyPrimaryContact = agecncyPrimaryContact;
	}

	/**
	 * @return the agecncySecondaryContact
	 */
	public String getAgecncySecondaryContact()
	{
		return agecncySecondaryContact;
	}

	/**
	 * @param agecncySecondaryContact the agecncySecondaryContact to set
	 */
	public void setAgecncySecondaryContact(String agecncySecondaryContact)
	{
		this.agecncySecondaryContact = agecncySecondaryContact;
	}

	/**
	 * @return the email
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}

	/**
	 * @return the procurementDescription
	 */
	public String getProcurementDescription()
	{
		return procurementDescription;
	}

	/**
	 * @param procurementDescription the procurementDescription to set
	 */
	public void setProcurementDescription(String procurementDescription)
	{
		this.procurementDescription = procurementDescription;
	}

	/**
	 * @return the estNumberOfContracts
	 */
	public Integer getEstNumberOfContracts()
	{
		return estNumberOfContracts;
	}

	/**
	 * @param estNumberOfContracts the estNumberOfContracts to set
	 */
	public void setEstNumberOfContracts(Integer estNumberOfContracts)
	{
		this.estNumberOfContracts = estNumberOfContracts;
	}

	/**
	 * @return the estProcurementValue
	 */
	public BigDecimal getEstProcurementValue()
	{
		return estProcurementValue;
	}

	/**
	 * @param estProcurementValue the estProcurementValue to set
	 */
	public void setEstProcurementValue(BigDecimal estProcurementValue)
	{
		this.estProcurementValue = estProcurementValue;
	}

	/**
	 * @return the linkToConceptReport
	 */
	public String getLinkToConceptReport()
	{
		return linkToConceptReport;
	}

	/**
	 * @param linkToConceptReport the linkToConceptReport to set
	 */
	public void setLinkToConceptReport(String linkToConceptReport)
	{
		this.linkToConceptReport = linkToConceptReport;
	}

	/**
	 * @return the rfpReleaseDatePlanned
	 */
	public String getRfpReleaseDatePlanned()
	{
		return rfpReleaseDatePlanned;
	}

	/**
	 * @param rfpReleaseDatePlanned the rfpReleaseDatePlanned to set
	 */
	public void setRfpReleaseDatePlanned(String rfpReleaseDatePlanned)
	{
		this.rfpReleaseDatePlanned = rfpReleaseDatePlanned;
	}

	/**
	 * @return the rfpReleaseDateUpdated
	 */
	public String getRfpReleaseDateUpdated()
	{
		return rfpReleaseDateUpdated;
	}

	/**
	 * @param rfpReleaseDateUpdated the rfpReleaseDateUpdated to set
	 */
	public void setRfpReleaseDateUpdated(String rfpReleaseDateUpdated)
	{
		this.rfpReleaseDateUpdated = rfpReleaseDateUpdated;
	}

	/**
	 * @return the proposalDueDatePlanned
	 */
	public String getProposalDueDatePlanned()
	{
		return proposalDueDatePlanned;
	}

	/**
	 * @param proposalDueDatePlanned the proposalDueDatePlanned to set
	 */
	public void setProposalDueDatePlanned(String proposalDueDatePlanned)
	{
		this.proposalDueDatePlanned = proposalDueDatePlanned;
	}

	/**
	 * @return the proposalDueDateUpdated
	 */
	public String getProposalDueDateUpdated()
	{
		return proposalDueDateUpdated;
	}

	/**
	 * @param proposalDueDateUpdated the proposalDueDateUpdated to set
	 */
	public void setProposalDueDateUpdated(String proposalDueDateUpdated)
	{
		this.proposalDueDateUpdated = proposalDueDateUpdated;
	}

	/**
	 * @return the contractStartDatePlanned
	 */
	public String getContractStartDatePlanned()
	{
		return contractStartDatePlanned;
	}

	/**
	 * @param contractStartDatePlanned the contractStartDatePlanned to set
	 */
	public void setContractStartDatePlanned(String contractStartDatePlanned)
	{
		this.contractStartDatePlanned = contractStartDatePlanned;
	}

	/**
	 * @return the contractStartDateUpdated
	 */
	public String getContractStartDateUpdated()
	{
		return contractStartDateUpdated;
	}

	/**
	 * @param contractStartDateUpdated the contractStartDateUpdated to set
	 */
	public void setContractStartDateUpdated(String contractStartDateUpdated)
	{
		this.contractStartDateUpdated = contractStartDateUpdated;
	}

	/**
	 * @return the firstRFPEvalDatePlanned
	 */
	public String getFirstRFPEvalDatePlanned()
	{
		return firstRFPEvalDatePlanned;
	}

	/**
	 * @param firstRFPEvalDatePlanned the firstRFPEvalDatePlanned to set
	 */
	public void setFirstRFPEvalDatePlanned(String firstRFPEvalDatePlanned)
	{
		this.firstRFPEvalDatePlanned = firstRFPEvalDatePlanned;
	}

	/**
	 * @return the firstRFPEvalDateUpdated
	 */
	public String getFirstRFPEvalDateUpdated()
	{
		return firstRFPEvalDateUpdated;
	}

	/**
	 * @param firstRFPEvalDateUpdated the firstRFPEvalDateUpdated to set
	 */
	public void setFirstRFPEvalDateUpdated(String firstRFPEvalDateUpdated)
	{
		this.firstRFPEvalDateUpdated = firstRFPEvalDateUpdated;
	}

	/**
	 * @return the finalRFPEvalDatePlanned
	 */
	public String getFinalRFPEvalDatePlanned()
	{
		return finalRFPEvalDatePlanned;
	}

	/**
	 * @param finalRFPEvalDatePlanned the finalRFPEvalDatePlanned to set
	 */
	public void setFinalRFPEvalDatePlanned(String finalRFPEvalDatePlanned)
	{
		this.finalRFPEvalDatePlanned = finalRFPEvalDatePlanned;
	}

	/**
	 * @return the finalRFPEvalDateUpdated
	 */
	public String getFinalRFPEvalDateUpdated()
	{
		return finalRFPEvalDateUpdated;
	}

	/**
	 * @param finalRFPEvalDateUpdated the finalRFPEvalDateUpdated to set
	 */
	public void setFinalRFPEvalDateUpdated(String finalRFPEvalDateUpdated)
	{
		this.finalRFPEvalDateUpdated = finalRFPEvalDateUpdated;
	}

	/**
	 * @return the preProposalConferenceDatePlanned
	 */
	public String getPreProposalConferenceDatePlanned()
	{
		return preProposalConferenceDatePlanned;
	}

	/**
	 * @param preProposalConferenceDatePlanned the
	 *            preProposalConferenceDatePlanned to set
	 */
	public void setPreProposalConferenceDatePlanned(String preProposalConferenceDatePlanned)
	{
		this.preProposalConferenceDatePlanned = preProposalConferenceDatePlanned;
	}

	/**
	 * @return the preProposalConferenceDateUpdated
	 */
	public String getPreProposalConferenceDateUpdated()
	{
		return preProposalConferenceDateUpdated;
	}

	/**
	 * @param preProposalConferenceDateUpdated the
	 *            preProposalConferenceDateUpdated to set
	 */
	public void setPreProposalConferenceDateUpdated(String preProposalConferenceDateUpdated)
	{
		this.preProposalConferenceDateUpdated = preProposalConferenceDateUpdated;
	}

	/**
	 * @return the contractEndDatePlanned
	 */
	public String getContractEndDatePlanned()
	{
		return contractEndDatePlanned;
	}

	/**
	 * @param contractEndDatePlanned the contractEndDatePlanned to set
	 */
	public void setContractEndDatePlanned(String contractEndDatePlanned)
	{
		this.contractEndDatePlanned = contractEndDatePlanned;
	}

	/**
	 * @return the contractEndDateUpdated
	 */
	public String getContractEndDateUpdated()
	{
		return contractEndDateUpdated;
	}

	/**
	 * @param contractEndDateUpdated the contractEndDateUpdated to set
	 */
	public void setContractEndDateUpdated(String contractEndDateUpdated)
	{
		this.contractEndDateUpdated = contractEndDateUpdated;
	}

	/**
	 * @return the evaluatorTrainingDatePlanned
	 */
	public String getEvaluatorTrainingDatePlanned()
	{
		return evaluatorTrainingDatePlanned;
	}

	/**
	 * @param evaluatorTrainingDatePlanned the evaluatorTrainingDatePlanned to
	 *            set
	 */
	public void setEvaluatorTrainingDatePlanned(String evaluatorTrainingDatePlanned)
	{
		this.evaluatorTrainingDatePlanned = evaluatorTrainingDatePlanned;
	}

	/**
	 * @return the evaluatorTrainingDateUpdated
	 */
	public String getEvaluatorTrainingDateUpdated()
	{
		return evaluatorTrainingDateUpdated;
	}

	/**
	 * @param evaluatorTrainingDateUpdated the evaluatorTrainingDateUpdated to
	 *            set
	 */
	public void setEvaluatorTrainingDateUpdated(String evaluatorTrainingDateUpdated)
	{
		this.evaluatorTrainingDateUpdated = evaluatorTrainingDateUpdated;
	}

	/**
	 * @return the firstEvalCompletionDatePlanned
	 */
	public String getFirstEvalCompletionDatePlanned()
	{
		return firstEvalCompletionDatePlanned;
	}

	/**
	 * @param firstEvalCompletionDatePlanned the firstEvalCompletionDatePlanned
	 *            to set
	 */
	public void setFirstEvalCompletionDatePlanned(String firstEvalCompletionDatePlanned)
	{
		this.firstEvalCompletionDatePlanned = firstEvalCompletionDatePlanned;
	}

	/**
	 * @return the firstEvalCompletionDateUpdated
	 */
	public String getFirstEvalCompletionDateUpdated()
	{
		return firstEvalCompletionDateUpdated;
	}

	/**
	 * @param firstEvalCompletionDateUpdated the firstEvalCompletionDateUpdated
	 *            to set
	 */
	public void setFirstEvalCompletionDateUpdated(String firstEvalCompletionDateUpdated)
	{
		this.firstEvalCompletionDateUpdated = firstEvalCompletionDateUpdated;
	}

	/**
	 * @return the finalEvalCompletionDatePlanned
	 */
	public String getFinalEvalCompletionDatePlanned()
	{
		return finalEvalCompletionDatePlanned;
	}

	/**
	 * @param finalEvalCompletionDatePlanned the finalEvalCompletionDatePlanned
	 *            to set
	 */
	public void setFinalEvalCompletionDatePlanned(String finalEvalCompletionDatePlanned)
	{
		this.finalEvalCompletionDatePlanned = finalEvalCompletionDatePlanned;
	}

	/**
	 * @return the finalEvalCompletionDateUpdated
	 */
	public String getFinalEvalCompletionDateUpdated()
	{
		return finalEvalCompletionDateUpdated;
	}

	/**
	 * @param finalEvalCompletionDateUpdated the finalEvalCompletionDateUpdated
	 *            to set
	 */
	public void setFinalEvalCompletionDateUpdated(String finalEvalCompletionDateUpdated)
	{
		this.finalEvalCompletionDateUpdated = finalEvalCompletionDateUpdated;
	}

	/**
	 * @return the awardSelectionDatePlanned
	 */
	public String getAwardSelectionDatePlanned()
	{
		return awardSelectionDatePlanned;
	}

	/**
	 * @param awardSelectionDatePlanned the awardSelectionDatePlanned to set
	 */
	public void setAwardSelectionDatePlanned(String awardSelectionDatePlanned)
	{
		this.awardSelectionDatePlanned = awardSelectionDatePlanned;
	}

	/**
	 * @return the awardSelectionDateUpdated
	 */
	public String getAwardSelectionDateUpdated()
	{
		return awardSelectionDateUpdated;
	}

	/**
	 * @param awardSelectionDateUpdated the awardSelectionDateUpdated to set
	 */
	public void setAwardSelectionDateUpdated(String awardSelectionDateUpdated)
	{
		this.awardSelectionDateUpdated = awardSelectionDateUpdated;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName()
	{
		if (null != serviceName && !serviceName.isEmpty())
		{
			serviceName = serviceName.trim();
		}
		else
		{
			serviceName = null;
		}
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	/**
	 * @return the activeProcStatus
	 */
	public String getActiveProcStatus()
	{
		return activeProcStatus;
	}

	/**
	 * @param activeProcStatus the activeProcStatus to set
	 */
	public void setActiveProcStatus(String activeProcStatus)
	{
		this.activeProcStatus = activeProcStatus;
	}

	/**
	 * @return the inactiveProcStatus
	 */
	public String getInactiveProcStatus()
	{
		return inactiveProcStatus;
	}

	/**
	 * @param inactiveProcStatus the inactiveProcStatus to set
	 */
	public void setInactiveProcStatus(String inactiveProcStatus)
	{
		this.inactiveProcStatus = inactiveProcStatus;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getContractId()
	{
		return contractId;
	}

	public void setContractId(String contractId)
	{
		this.contractId = contractId;
	}

	public String getAssignUserId()
	{
		return assignUserId;
	}

	public void setAssignUserId(String assignUserId)
	{
		this.assignUserId = assignUserId;
	}

	public String getApproverUserId()
	{
		return approverUserId;
	}

	public void setApproverUserId(String approverUserId)
	{
		this.approverUserId = approverUserId;
	}

	public String getProcUser()
	{
		return procUser;
	}

	public void setProcUser(String procUser)
	{
		this.procUser = procUser;
	}

	public String getWorkFlowId()
	{
		return workFlowId;
	}

	public void setWorkFlowId(String workFlowId)
	{
		this.workFlowId = workFlowId;
	}

	public String getPcofStatusCode()
	{
		return pcofStatusCode;
	}

	public void setPcofStatusCode(String pcofStatusCode)
	{
		this.pcofStatusCode = pcofStatusCode;
	}

	/**
	 * @return the lastPublishedDateFrom
	 * @throws ApplicationException
	 */
	public Date getLastPublishedDateFrom() throws ApplicationException
	{
		return HHSUtil.setDateToFrom(lastPublishedFrom, lastPublishedTo, HHSConstants.FROM);
	}

	/**
	 * @return the lastPublishedDateTo
	 * @throws ApplicationException
	 */
	public Date getLastPublishedDateTo() throws ApplicationException
	{
		return HHSUtil.setDateToFrom(lastPublishedFrom, lastPublishedTo, HHSConstants.TO_STR);
	}

	/**
	 * @return the releaseDateFrom
	 * @throws ApplicationException
	 */
	public Date getReleaseDateFrom() throws ApplicationException
	{
		return HHSUtil.setDateToFrom(releaseFrom, releaseTo, HHSConstants.FROM);
	}

	/**
	 * @return the releaseDateTo
	 * @throws ApplicationException
	 */
	public Date getReleaseDateTo() throws ApplicationException
	{
		return HHSUtil.setDateToFrom(releaseFrom, releaseTo, HHSConstants.TO_STR);
	}

	/**
	 * @return the proposalDueDateFrom
	 * @throws ApplicationException
	 */
	public Date getProposalDueDateFrom() throws ApplicationException
	{
		return HHSUtil.setDateToFrom(proposalDueFrom, proposalDueTo, HHSConstants.FROM);
	}

	/**
	 * @return the proposalDueDateTo
	 * @throws ApplicationException
	 */
	public Date getProposalDueDateTo() throws ApplicationException
	{
		return HHSUtil.setDateToFrom(proposalDueFrom, proposalDueTo, HHSConstants.TO_STR);
	}

	/**
	 * @return the contractStartDateFrom
	 * @throws ApplicationException
	 */
	public Date getContractStartDateFrom() throws ApplicationException
	{
		return HHSUtil.setDateToFrom(contractStartFrom, contractStartTo, HHSConstants.FROM);
	}

	/**
	 * @return the contractStartDateTo
	 * @throws ApplicationException
	 */
	public Date getContractStartDateTo() throws ApplicationException
	{
		return HHSUtil.setDateToFrom(contractStartFrom, contractStartTo, HHSConstants.TO_STR);
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
	 * @return the staffId
	 */
	public String getStaffId()
	{
		return staffId;
	}

	/**
	 * @param staffId the staffId to set
	 */
	public void setStaffId(String staffId)
	{
		this.staffId = staffId;
	}

	/**
	 * @return the releaseFrom
	 */
	public String getReleaseFrom()
	{
		return releaseFrom;
	}

	/**
	 * @param releaseFrom the releaseFrom to set
	 */
	public void setReleaseFrom(String releaseFrom)
	{
		this.releaseFrom = releaseFrom;
	}

	/**
	 * @return the releaseTo
	 */
	public String getReleaseTo()
	{
		return releaseTo;
	}

	/**
	 * @param releaseTo the releaseTo to set
	 */
	public void setReleaseTo(String releaseTo)
	{
		this.releaseTo = releaseTo;
	}

	/**
	 * @return the proposalDueFrom
	 */
	public String getProposalDueFrom()
	{
		return proposalDueFrom;
	}

	/**
	 * @param proposalDueFrom the proposalDueFrom to set
	 */
	public void setProposalDueFrom(String proposalDueFrom)
	{
		this.proposalDueFrom = proposalDueFrom;
	}

	/**
	 * @return the proposalDueTo
	 */
	public String getProposalDueTo()
	{
		return proposalDueTo;
	}

	/**
	 * @param proposalDueTo the proposalDueTo to set
	 */
	public void setProposalDueTo(String proposalDueTo)
	{
		this.proposalDueTo = proposalDueTo;
	}

	/**
	 * @return the procurementStatusList
	 */
	public List<String> getProcurementStatusList()
	{
		return procurementStatusList;
	}

	/**
	 * @param procurementStatusList the procurementStatusList to set
	 */
	public void setProcurementStatusList(List<String> procurementStatusList)
	{
		this.procurementStatusList = procurementStatusList;
	}

	/**
	 * @return the providerStatusList
	 */
	public List<String> getProviderStatusList()
	{
		return providerStatusList;
	}

	/**
	 * @param providerStatusList the providerStatusList to set
	 */
	public void setProviderStatusList(List<String> providerStatusList)
	{
		this.providerStatusList = providerStatusList;
	}

	/**
	 * @param releaseDateFrom the releaseDateFrom to set
	 */
	/*
	 * public void setReleaseDateFrom(Date releaseDateFrom) {
	 * this.releaseDateFrom = releaseDateFrom; }
	 *//**
	 * @param releaseDateTo the releaseDateTo to set
	 */
	/*
	 * public void setReleaseDateTo(Date releaseDateTo) { this.releaseDateTo =
	 * releaseDateTo; }
	 *//**
	 * @param proposalDueDateFrom the proposalDueDateFrom to set
	 */
	/*
	 * public void setProposalDueDateFrom(Date proposalDueDateFrom) {
	 * this.proposalDueDateFrom = proposalDueDateFrom; }
	 *//**
	 * @param proposalDueDateTo the proposalDueDateTo to set
	 */
	/*
	 * public void setProposalDueDateTo(Date proposalDueDateTo) {
	 * this.proposalDueDateTo = proposalDueDateTo; }
	 */

	/**
	 * @return the lastPublishedFrom
	 */
	public String getLastPublishedFrom()
	{
		return lastPublishedFrom;
	}

	/**
	 * @param lastPublishedFrom the lastPublishedFrom to set
	 */
	public void setLastPublishedFrom(String lastPublishedFrom)
	{
		this.lastPublishedFrom = lastPublishedFrom;
	}

	/**
	 * @return the lastPublishedTo
	 */
	public String getLastPublishedTo()
	{
		return lastPublishedTo;
	}

	/**
	 * @param lastPublishedTo the lastPublishedTo to set
	 */
	public void setLastPublishedTo(String lastPublishedTo)
	{
		this.lastPublishedTo = lastPublishedTo;
	}

	/**
	 * @return the contractStartFrom
	 */
	public String getContractStartFrom()
	{
		return contractStartFrom;
	}

	/**
	 * @param contractStartFrom the contractStartFrom to set
	 */
	public void setContractStartFrom(String contractStartFrom)
	{
		this.contractStartFrom = contractStartFrom;
	}

	/**
	 * @return the contractStartTo
	 */
	public String getContractStartTo()
	{
		return contractStartTo;
	}

	/**
	 * @param contractStartTo the contractStartTo to set
	 */
	public void setContractStartTo(String contractStartTo)
	{
		this.contractStartTo = contractStartTo;
	}

	public String getProcurementId()
	{
		return procurementId;
	}

	public void setProcurementId(String procurementId)
	{
		this.procurementId = procurementId;
	}

	public String getAccPrimaryContactName()
	{
		return accPrimaryContactName;
	}

	public void setAccPrimaryContactName(String accPrimaryContactName)
	{
		this.accPrimaryContactName = accPrimaryContactName;
	}

	public String getAccSecondaryContactName()
	{
		return accSecondaryContactName;
	}

	public void setAccSecondaryContactName(String accSecondaryContactName)
	{
		this.accSecondaryContactName = accSecondaryContactName;
	}

	public String getAgencyPrimaryContactName()
	{
		return agencyPrimaryContactName;
	}

	public void setAgencyPrimaryContactName(String agencyPrimaryContactName)
	{
		this.agencyPrimaryContactName = agencyPrimaryContactName;
	}

	public String getAgencySecondaryContactName()
	{
		return agencySecondaryContactName;
	}

	public void setAgencySecondaryContactName(String agencySecondaryContactName)
	{
		this.agencySecondaryContactName = agencySecondaryContactName;
	}

	@Override
	public String toString()
	{
		return "Procurement [agencyId=" + agencyId + ", agencyName=" + agencyName + ", lastPublishedDate="
				+ lastPublishedDate + ", releaseDate=" + releaseDate + ", procurementStatus=" + procurementStatus + ", providerStatus=" + providerStatus
				+ ", proposalDueDate=" + proposalDueDate + ", contractStartDate=" + contractStartDate
				+ ", programName=" + programName + ", accPrimaryContact=" + accPrimaryContact
				+ ", accSecondaryContact=" + accSecondaryContact + ", agecncyPrimaryContact=" + agecncyPrimaryContact
				+ ", agecncySecondaryContact=" + agecncySecondaryContact + ", email=" + email
				+ ", procurementDescription=" + procurementDescription + ", estNumberOfContracts="
				+ estNumberOfContracts + ", estProcurementValue=" + estProcurementValue + ", linkToConceptReport="
				+ linkToConceptReport + ", rfpReleaseDatePlanned=" + rfpReleaseDatePlanned + ", rfpReleaseDateUpdated="
				+ rfpReleaseDateUpdated + ", proposalDueDatePlanned=" + proposalDueDatePlanned
				+ ", proposalDueDateUpdated=" + proposalDueDateUpdated + ", contractStartDatePlanned="
				+ contractStartDatePlanned + ", contractStartDateUpdated=" + contractStartDateUpdated
				+ ", firstRFPEvalDatePlanned=" + firstRFPEvalDatePlanned + ", firstRFPEvalDateUpdated="
				+ firstRFPEvalDateUpdated + ", finalRFPEvalDatePlanned=" + finalRFPEvalDatePlanned
				+ ", finalRFPEvalDateUpdated=" + finalRFPEvalDateUpdated + ", preProposalConferenceDatePlanned="
				+ preProposalConferenceDatePlanned + ", preProposalConferenceDateUpdated="
				+ preProposalConferenceDateUpdated + ", contractEndDatePlanned=" + contractEndDatePlanned
				+ ", contractEndDateUpdated=" + contractEndDateUpdated + ", evaluatorTrainingDatePlanned="
				+ evaluatorTrainingDatePlanned + ", evaluatorTrainingDateUpdated=" + evaluatorTrainingDateUpdated
				+ ", firstEvalCompletionDatePlanned=" + firstEvalCompletionDatePlanned
				+ ", firstEvalCompletionDateUpdated=" + firstEvalCompletionDateUpdated
				+ ", finalEvalCompletionDatePlanned=" + finalEvalCompletionDatePlanned
				+ ", finalEvalCompletionDateUpdated=" + finalEvalCompletionDateUpdated + ", awardSelectionDatePlanned="
				+ awardSelectionDatePlanned + ", awardSelectionDateUpdated=" + awardSelectionDateUpdated
				+ ", serviceName=" + serviceName + ", activeProcStatus=" + activeProcStatus + ", inactiveProcStatus="
				+ inactiveProcStatus + ", programId=" + programId + ", userName=" + userName + ", password=" + password
				+ ", lastPublishDate=" + lastPublishDate + ", lastUpdatedUser=" + lastUpdatedUser
				+ ", lastPublishedByUser=" + lastPublishedByUser + ", userId=" + userId + ", createdBy=" + createdBy
				+ ", modifiedBy=" + modifiedBy + ", rfpReleaseUserId=" + rfpReleaseUserId + ", contractId="
				+ contractId + ", organizationId=" + organizationId + ", staffId=" + staffId + ", assignUserId="
				+ assignUserId + ", approverUserId=" + approverUserId + ", procUser=" + procUser + ", workFlowId="
				+ workFlowId + ", pcofStatusCode=" + pcofStatusCode + ", lastPublishedFrom=" + lastPublishedFrom
				+ ", lastPublishedTo=" + lastPublishedTo + ", releaseFrom=" + releaseFrom + ", releaseTo=" + releaseTo
				+ ", proposalDueFrom=" + proposalDueFrom + ", proposalDueTo=" + proposalDueTo + ", contractStartFrom="
				+ contractStartFrom + ", contractStartTo=" + contractStartTo + ", procurementStatusList="
				+ procurementStatusList + ", providerStatusList=" + providerStatusList + ", procurementId="
				+ procurementId + ", addendumFlag=" + addendumFlag + ", serviceFilter=" + serviceFilter
				+ ", submissionCloseDate=" + submissionCloseDate + ", rfpReleaseDate=" + rfpReleaseDate
				+ ", accPrimaryContactName=" + accPrimaryContactName + ", accSecondaryContactName="
				+ accSecondaryContactName + ", agencyPrimaryContactName=" + agencyPrimaryContactName
				+ ", agencySecondaryContactName=" + agencySecondaryContactName + ", accPrimaryEmail=" + accPrimaryEmail
				+ ", accSecondaryEmail=" + accSecondaryEmail + ", agencyPrimaryEmail=" + agencyPrimaryEmail
				+ ", agencySecondaryEmail=" + agencySecondaryEmail + ", serviceUnitRequiredFlag="
				+ serviceUnitRequiredFlag + ", procurementFavorite=" + procurementFavorite + ", isFavorite="
				+ isFavorite + ", isFavoriteDisplayed=" + isFavoriteDisplayed + ", procurementFavorites="
				+ procurementFavorites + ", competitionPoolId=" + competitionPoolId + ", evaluationGroupId="
				+ evaluationGroupId + ", evaluationGroupTitle=" + evaluationGroupTitle + ", competitionPoolTitle="
				+ competitionPoolTitle + ", evalGroupStatus=" + evalGroupStatus + ", pcofConctractStartDate="
				+ pcofConctractStartDate + ", pcofConctractEndDate=" + pcofConctractEndDate + ", approvedAmount="
				+ approvedAmount + ", psrApprovedAmount=" + psrApprovedAmount + ", psrConctractStartDate="
				+ psrConctractStartDate + ", refEpinId=" + refEpinId + ", psrConctractEndDate=" + psrConctractEndDate
				+ "]";
	}

	public String getAccPrimaryEmail()
	{
		return accPrimaryEmail;
	}

	public void setAccPrimaryEmail(String accPrimaryEmail)
	{
		this.accPrimaryEmail = accPrimaryEmail;
	}

	public String getAccSecondaryEmail()
	{
		return accSecondaryEmail;
	}

	public void setAccSecondaryEmail(String accSecondaryEmail)
	{
		this.accSecondaryEmail = accSecondaryEmail;
	}

	public String getAgencyPrimaryEmail()
	{
		return agencyPrimaryEmail;
	}

	public void setAgencyPrimaryEmail(String agencyPrimaryEmail)
	{
		this.agencyPrimaryEmail = agencyPrimaryEmail;
	}

	public String getAgencySecondaryEmail()
	{
		return agencySecondaryEmail;
	}

	public void setAgencySecondaryEmail(String agencySecondaryEmail)
	{
		this.agencySecondaryEmail = agencySecondaryEmail;
	}

	/**
	 * @return the serviceUnitRequiredFlag
	 */
	public String getServiceUnitRequiredFlag()
	{
		return serviceUnitRequiredFlag;
	}

	/**
	 * @param serviceUnitRequiredFlag the serviceUnitRequiredFlag to set
	 */
	public void setServiceUnitRequiredFlag(String serviceUnitRequiredFlag)
	{
		this.serviceUnitRequiredFlag = serviceUnitRequiredFlag;
	}

	/**
	 * @return the procurementFavorite
	 */
	public String getProcurementFavorite()
	{
		return procurementFavorite;
	}

	/**
	 * @param procurementFavorite the procurementFavorite to set
	 */
	public void setProcurementFavorite(String procurementFavorite)
	{
		this.procurementFavorite = procurementFavorite;
	}

	/**
	 * @return the procurementFavorites
	 */
	public List<String> getProcurementFavorites()
	{
		return procurementFavorites;
	}

	/**
	 * @param procurementFavorites the procurementFavorites to set
	 */
	public void setProcurementFavorites(List<String> procurementFavorites)
	{
		this.procurementFavorites = procurementFavorites;
	}

	/**
	 * @return the isFavoriteDisplayed
	 */
	public String getIsFavoriteDisplayed()
	{
		return isFavoriteDisplayed;
	}

	/**
	 * @param isFavoriteDisplayed the isFavoriteDisplayed to set
	 */
	public void setIsFavoriteDisplayed(String isFavoriteDisplayed)
	{
		this.isFavoriteDisplayed = isFavoriteDisplayed;
	}

	/**
	 * @return the isFavorite
	 */
	public String getIsFavorite()
	{
		return isFavorite;
	}

	/**
	 * @param isFavorite the isFavorite to set
	 */
	public void setIsFavorite(String isFavorite)
	{
		this.isFavorite = isFavorite;
	}

	/**
	 * @return the competitionPoolId
	 */
	public String getCompetitionPoolId()
	{
		return competitionPoolId;
	}

	/**
	 * @param competitionPoolId the competitionPoolId to set
	 */
	public void setCompetitionPoolId(String competitionPoolId)
	{
		this.competitionPoolId = competitionPoolId;
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
	 * @return the evaluationGroupTitle
	 */
	public String getEvaluationGroupTitle()
	{
		return evaluationGroupTitle;
	}

	/**
	 * @param evaluationGroupTitle the evaluationGroupTitle to set
	 */
	public void setEvaluationGroupTitle(String evaluationGroupTitle)
	{
		this.evaluationGroupTitle = evaluationGroupTitle;
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

	/**
	 * @return the evalGroupStatus
	 */
	public String getEvalGroupStatus()
	{
		return evalGroupStatus;
	}

	/**
	 * @param evalGroupStatus the evalGroupStatus to set
	 */
	public void setEvalGroupStatus(String evalGroupStatus)
	{
		this.evalGroupStatus = evalGroupStatus;
	}

	/** Begin R7.2.0  QC8914 */
	public String getProcurementStatus() {
		return procurementStatus;
	}

	public void setProcurementStatus(String procurementStatus) {
		this.procurementStatus = procurementStatus;
	}
	/** End R7.2.0  QC8914 */

	public String getRoleCurrent() {
		return roleCurrent;
	}

	public void setRoleCurrent(String roleCurrent) {
		this.roleCurrent = roleCurrent;
	}
}