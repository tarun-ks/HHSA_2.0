package com.nyc.hhs.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.HHSUtil;

/**
 * This class is a bean which maintains agency settings module details.
 * 
 */

public class TaxonomyTaggingBean extends BaseFilter
{
	private String completeBranchPath;
	private String organizationId;
	@RegExp(value ="^\\d{0,22}")
	private String procurementId;
	private String proposalTitle;
	private String providerName;
	private String procurementContractTitle;
	private String competitionPoolTitle;
	private String approvalDate;
	private String awardAmount;
	private String isTagged = HHSConstants.EMPTY_STRING;
	private String action = HHSConstants.MODEL_TAXONOMY_EDIT_TAGS;
	@RegExp(value ="^\\d{0,22}")
	private String proposalId;
	private String proposalStatusId = HHSConstants.EMPTY_STRING;
	private String contractStatusId = HHSConstants.EMPTY_STRING;
	private String type;
	private String taxonomyTaggingId;
	private List<String> taxonomyTaggingIdList = new ArrayList<String>();
	private String elementId;
	private String modifierBranchId = HHSConstants.EMPTY_STRING;
	private String modifierElementId;
	@RegExp(value ="^\\d{0,22}")
	private String contractId;
	private List<String> contractIdList;
	private List<String> proposalIdList;
	private List<String> procurementIdList;
	private Date lastUpdatedDate;
	private String activeFlag;
	private Date createdDate;
	private String createdByUserId;
	private Date modifiedDate;
	private String modifyByUserId;
	private String serviceFunctionName = HHSConstants.EMPTY_STRING;
	private String modifiers = HHSConstants.EMPTY_STRING;
	private String actions = HHSConstants.EMPTY_STRING;
	private List<String> previousIds;
	private List<String> elementIdAlreadyPresent;
	// filter variables
	private String dateApprovedFrom;
	private String dateApprovedTo;
	private String awardAmountFrom;
	private String awardAmountTo;
	private List<String> tagged;
	private List<String> taggedElementName;
	private String selectedTaxonomy = HHSConstants.PE_GRID_PAGE_SIZE;
	private String competitionPoolId;
	private String contractAgencyName;
	private String programName;
	private String awardEpin;

	/**
	 * Default constructor
	 */
	public TaxonomyTaggingBean()
	{
		setFirstSort(HHSConstants.TAXONOMY_TAGGING_ACTIVE_FLAG);
		setSecondSort(HHSConstants.AWARD_APPROVAL_DATE);
		setFirstSortType(HHSConstants.ASCENDING);
		setSecondSortType(HHSConstants.DESCENDING);
		setSortColumnName(HHSConstants.IS_TAGGED);
		setSecondSortDate(true);
		List<String> loCheckboxList = new ArrayList<String>();
		loCheckboxList.add(HHSConstants.STRING_ZERO);
		loCheckboxList.add(HHSConstants.ONE);
		setTagged(loCheckboxList);
	}

	/**
	 * @return the awardAmountFromNoComma
	 */
	public String getAwardAmountFromNoComma()
	{
		return awardAmountFrom.replaceAll(HHSConstants.COMMA, HHSConstants.EMPTY_STRING);
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
	 * @return the awardAmountToNoComma
	 */
	public String getAwardAmountToNoComma()
	{
		return awardAmountTo.replaceAll(HHSConstants.COMMA, HHSConstants.EMPTY_STRING);
	}

	/**
	 * @return the approvedDateFrom
	 * @throws ApplicationException
	 */
	public Date getApprovedDateFrom() throws ApplicationException
	{
		return HHSUtil.setDateToFrom(dateApprovedFrom, dateApprovedTo, HHSConstants.FROM);
	}

	/**
	 * @return the approvedDateTo
	 * @throws ApplicationException
	 */
	public Date getApprovedDateTo() throws ApplicationException
	{
		return HHSUtil.setDateToFrom(dateApprovedFrom, dateApprovedTo, HHSConstants.TO_STR);
	}

	/**
	 * @return the elementIdAlreadyPresent
	 */
	public List<String> getElementIdAlreadyPresent()
	{
		return elementIdAlreadyPresent;
	}

	/**
	 * @param elementIdAlreadyPresent the elementIdAlreadyPresent to set
	 */
	public void setElementIdAlreadyPresent(List<String> elementIdAlreadyPresent)
	{
		this.elementIdAlreadyPresent = elementIdAlreadyPresent;
	}

	/**
	 * @return the previousIds
	 */
	public List<String> getPreviousIds()
	{
		return previousIds;
	}

	/**
	 * @param previousIds the previousIds to set
	 */
	public void setPreviousIds(List<String> previousIds)
	{
		this.previousIds = previousIds;
	}

	/**
	 * @return the taggedElementName
	 */
	public List<String> getTaggedElementName()
	{
		return taggedElementName;
	}

	/**
	 * @param taggedElementName the taggedElementName to set
	 */
	public void setTaggedElementName(List<String> taggedElementName)
	{
		this.taggedElementName = taggedElementName;
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
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
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
	 * @return the proposalTitle
	 */
	public String getProposalTitle()
	{
		return proposalTitle;
	}

	/**
	 * @param proposalTitle the proposalTitle to set
	 */
	public void setProposalTitle(String proposalTitle)
	{
		this.proposalTitle = proposalTitle;
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

	/**
	 * @return the procurementContractTitle
	 */
	public String getProcurementContractTitle()
	{
		return procurementContractTitle;
	}

	/**
	 * @param procurementContractTitle the procurementContractTitle to set
	 */
	public void setProcurementContractTitle(String procurementContractTitle)
	{
		this.procurementContractTitle = procurementContractTitle;
	}

	/**
	 * @return the approvalDate
	 */
	public String getApprovalDate()
	{
		return approvalDate;
	}

	/**
	 * @param approvalDate the approvalDate to set
	 */
	public void setApprovalDate(String approvalDate)
	{
		this.approvalDate = approvalDate;
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

	/**
	 * @return the isTagged
	 */
	public String getIsTagged()
	{
		return isTagged;
	}

	/**
	 * @param isTagged the isTagged to set
	 */
	public void setIsTagged(String isTagged)
	{
		this.isTagged = isTagged;
	}

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
	 * @return the proposalStatusId
	 */
	public String getProposalStatusId()
	{
		return proposalStatusId;
	}

	/**
	 * @param proposalStatusId the proposalStatusId to set
	 */
	public void setProposalStatusId(String proposalStatusId)
	{
		this.proposalStatusId = proposalStatusId;
	}

	/**
	 * @return the contractStatusId
	 */
	public String getContractStatusId()
	{
		return contractStatusId;
	}

	/**
	 * @param contractStatusId the contractStatusId to set
	 */
	public void setContractStatusId(String contractStatusId)
	{
		this.contractStatusId = contractStatusId;
	}

	/**
	 * @return the taxonomyTaggingId
	 */
	public String getTaxonomyTaggingId()
	{
		return taxonomyTaggingId;
	}

	/**
	 * @param taxonomyTaggingId the taxonomyTaggingId to set
	 */
	public void setTaxonomyTaggingId(String taxonomyTaggingId)
	{
		this.taxonomyTaggingId = taxonomyTaggingId;
	}

	/**
	 * @return the elementId
	 */
	public String getElementId()
	{
		return elementId;
	}

	/**
	 * @param elementId the elementId to set
	 */
	public void setElementId(String elementId)
	{
		this.elementId = elementId;
	}

	/**
	 * @return the modifierBranchId
	 */
	public String getModifierBranchId()
	{
		return modifierBranchId;
	}

	/**
	 * @param modifierBranchId the modifierBranchId to set
	 */
	public void setModifierBranchId(String modifierBranchId)
	{
		this.modifierBranchId = modifierBranchId;
	}

	/**
	 * @return the modifierElementId
	 */
	public String getModifierElementId()
	{
		return modifierElementId;
	}

	/**
	 * @param modifierElementId the modifierElementId to set
	 */
	public void setModifierElementId(String modifierElementId)
	{
		this.modifierElementId = modifierElementId;
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
	 * @return the lastUpdatedDate
	 */
	public Date getLastUpdatedDate()
	{
		return lastUpdatedDate;
	}

	/**
	 * @param lastUpdatedDate the lastUpdatedDate to set
	 */
	public void setLastUpdatedDate(Date lastUpdatedDate)
	{
		this.lastUpdatedDate = lastUpdatedDate;
	}

	/**
	 * @return the activeFlag
	 */
	public String getActiveFlag()
	{
		return activeFlag;
	}

	/**
	 * @param activeFlag the activeFlag to set
	 */
	public void setActiveFlag(String activeFlag)
	{
		this.activeFlag = activeFlag;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate()
	{
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate)
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
	public Date getModifiedDate()
	{
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate)
	{
		this.modifiedDate = modifiedDate;
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
	 * @return the serviceFunctionName
	 */
	public String getServiceFunctionName()
	{
		return serviceFunctionName;
	}

	/**
	 * @param serviceFunctionName the serviceFunctionName to set
	 */
	public void setServiceFunctionName(String serviceFunctionName)
	{
		this.serviceFunctionName = serviceFunctionName;
	}

	/**
	 * @return the modifiers
	 */
	public String getModifiers()
	{
		return modifiers;
	}

	/**
	 * @param modifiers the modifiers to set
	 */
	public void setModifiers(String modifiers)
	{
		this.modifiers = modifiers;
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
	 * @return the dateApprovedFrom
	 */
	public String getDateApprovedFrom()
	{
		return dateApprovedFrom;
	}

	/**
	 * @param dateApprovedFrom the dateApprovedFrom to set
	 */
	public void setDateApprovedFrom(String dateApprovedFrom)
	{
		this.dateApprovedFrom = dateApprovedFrom;
	}

	/**
	 * @return the dateApprovedTo
	 */
	public String getDateApprovedTo()
	{
		return dateApprovedTo;
	}

	/**
	 * @param dateApprovedTo the dateApprovedTo to set
	 */
	public void setDateApprovedTo(String dateApprovedTo)
	{
		this.dateApprovedTo = dateApprovedTo;
	}

	/**
	 * @return the awardAmountFrom
	 */
	public String getAwardAmountFrom()
	{
		return awardAmountFrom;
	}

	/**
	 * @param awardAmountFrom the awardAmountFrom to set
	 */
	public void setAwardAmountFrom(String awardAmountFrom)
	{
		this.awardAmountFrom = awardAmountFrom;
	}

	/**
	 * @return the awardAmountTo
	 */
	public String getAwardAmountTo()
	{
		return awardAmountTo;
	}

	/**
	 * @param awardAmountTo the awardAmountTo to set
	 */
	public void setAwardAmountTo(String awardAmountTo)
	{
		this.awardAmountTo = awardAmountTo;
	}

	/**
	 * @return the tagged
	 */
	public List<String> getTagged()
	{
		return tagged;
	}

	/**
	 * @param tagged the tagged to set
	 */
	public void setTagged(List<String> tagged)
	{
		this.tagged = tagged;
	}

	public void setSelectedTaxonomy(String selectedTaxonomy)
	{
		this.selectedTaxonomy = selectedTaxonomy;
	}

	public String getSelectedTaxonomy()
	{
		return selectedTaxonomy;
	}

	public List<String> getContractIdList()
	{
		return contractIdList;
	}

	public void setContractIdList(List<String> contractIdList)
	{
		this.contractIdList = contractIdList;
	}

	public List<String> getProposalIdList()
	{
		return proposalIdList;
	}

	public void setProposalIdList(List<String> proposalIdList)
	{
		this.proposalIdList = proposalIdList;
	}

	public List<String> getProcurementIdList()
	{
		return procurementIdList;
	}

	public void setProcurementIdList(List<String> procurementIdList)
	{
		this.procurementIdList = procurementIdList;
	}

	public void setTaxonomyTaggingIdList(List<String> taxonomyTaggingIdList)
	{
		this.taxonomyTaggingIdList = taxonomyTaggingIdList;
	}

	public List<String> getTaxonomyTaggingIdList()
	{
		return taxonomyTaggingIdList;
	}

	public String getCompleteBranchPath()
	{
		return completeBranchPath;
	}

	public void setCompleteBranchPath(String completeBranchPath)
	{
		this.completeBranchPath = completeBranchPath;
	}

	public String getContractAgencyName() {
		return contractAgencyName;
	}

	public void setContractAgencyName(String contractAgencyName) {
		this.contractAgencyName = contractAgencyName;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getAwardEpin() {
		return awardEpin;
	}

	public void setAwardEpin(String awardEpin) {
		this.awardEpin = awardEpin;
	}

	@Override
	public String toString()
	{
		return "TaxonomyTaggingBean [completeBranchPath=" + completeBranchPath + ", organizationId=" + organizationId
				+ ", procurementId=" + procurementId + ", proposalTitle=" + proposalTitle + ", providerName="
				+ providerName + ", procurementContractTitle=" + procurementContractTitle + ", competitionPoolTitle="
				+ competitionPoolTitle + ", contractAgencyName=" + contractAgencyName + ", programName=" 
				+ programName + ", awardEpin=" + awardEpin + ", approvalDate=" + approvalDate + ", awardAmount=" + awardAmount
				+ ", isTagged=" + isTagged + ", action=" + action + ", proposalId=" + proposalId
				+ ", proposalStatusId=" + proposalStatusId + ", contractStatusId=" + contractStatusId + ", type="
				+ type + ", taxonomyTaggingId=" + taxonomyTaggingId + ", taxonomyTaggingIdList="
				+ taxonomyTaggingIdList + ", elementId=" + elementId + ", modifierBranchId=" + modifierBranchId
				+ ", modifierElementId=" + modifierElementId + ", contractId=" + contractId + ", contractIdList="
				+ contractIdList + ", proposalIdList=" + proposalIdList + ", procurementIdList=" + procurementIdList
				+ ", lastUpdatedDate=" + lastUpdatedDate + ", activeFlag=" + activeFlag + ", createdDate="
				+ createdDate + ", createdByUserId=" + createdByUserId + ", modifiedDate=" + modifiedDate
				+ ", modifyByUserId=" + modifyByUserId + ", serviceFunctionName=" + serviceFunctionName
				+ ", modifiers=" + modifiers + ", actions=" + actions + ", previousIds=" + previousIds
				+ ", elementIdAlreadyPresent=" + elementIdAlreadyPresent + ", dateApprovedFrom=" + dateApprovedFrom
				+ ", dateApprovedTo=" + dateApprovedTo + ", awardAmountFrom=" + awardAmountFrom + ", awardAmountTo="
				+ awardAmountTo + ", tagged=" + tagged + ", taggedElementName=" + taggedElementName
				+ ", selectedTaxonomy=" + selectedTaxonomy + ", competitionPoolId=" + competitionPoolId + "]";
	}
}