/**
 * 
 */
package com.nyc.hhs.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.util.AutoPopulatingList;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MaxLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.MinLength;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.CommonUtil;

/**
 * This class is a bean that maintain proposal details
 */
public class ProposalDetailsBean extends BaseFilter
{
	private String providerName;
	private String providerContactId;
	private String providerEmailId;
	private String providerPhone;
	private String providerOfficeTitle;
	private String procurementId;
	private String organizationId;
	private String organizationLegalName;
	private String proposalId;
	//@MinLength(value = 3, message = "3")
	@MaxLength(value = 90, message = "90")
	private String proposalTitle;
	//@NotBlank
	@Length(max = 5)
	private String totalNumberOfService;
	//@NotBlank
	@Length(max = 18)
	private String totalFundingRequest;
	//@NotBlank
	@Length(max = 18)
	private String costPerUnit;
	private String createdBy;
	private String modifiedBy;
	private List<SiteDetailsBean> siteDetailsList;
	private List<ProposalQuestionAnswerBean> questionAnswerBeanList;
	private List<ExtendedDocument> documentList;
	private List<ExtendedDocument> requiredDocumentList;
	private List<ExtendedDocument> optionalDocumentList;
	private String procurementStatus;
	private String proposalStatus;
	private String proposalStatusName;
	private String evaluationScore;
	private Date modifiedDate;
	private Integer rank;
	private String action;
	private String lastModifiedDate;
	private String procurementDocumentId;
	private String proposalStatusId;
	private Date proposalDueDate;
	private String userRole;
	private String procReviewStatusId;
	private String serviceUnitFlag;
	private String competitionPool;
	private String competitionPoolTitle;
	private String evaluationGroupStatus;
	private String evaluationGroupTitle;
	private String isOpenEndedRFP;
	private String evaluationPoolMappingId;
	private String hiddenCompPoolId;
	private String restrictSubmit;
	private String versionNoQuestion;
	private String versionNoDocument;

	private String latestVersionQues;
	private String latestVersionDoc;
	private String quesVersion;
	private String docVersion;
	private String evalGrpQuesVersion;
	private String evalGrpDocVersion;

	// Start : Added in R5
	private String competitionpoolid;
	private String procurementtitle;
	private String agencyId;
	private String evaluationGroupId;
	private List<String> proposalStatusList;
	private Date modifiedDateFrom;
	private Date modifiedDateTo;
	private String evaluationPoolMappingStatus;
	
	//R7.2.0 QC8914 
	private String roleCurrent;

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
	 * @return the proposalStatusList
	 */
	public List<String> getProposalStatusList()
	{
		return proposalStatusList;
	}

	/**
	 * @param proposalStatusList the proposalStatusList to set
	 */
	public void setProposalStatusList(List<String> proposalStatusList)
	{
		this.proposalStatusList = proposalStatusList;
	}

	/**
	 * @return the modifiedDateFrom
	 */
	public Date getModifiedDateFrom()
	{
		return modifiedDateFrom;
	}

	/**
	 * @param modifiedDateFrom the modifiedDateFrom to set
	 */
	public void setModifiedDateFrom(Date modifiedDateFrom)
	{
		this.modifiedDateFrom = modifiedDateFrom;
	}

	/**
	 * @return the modifiedDateTo
	 */
	public Date getModifiedDateTo()
	{
		return modifiedDateTo;
	}

	/**
	 * @param modifiedDateTo the modifiedDateTo to set
	 */
	public void setModifiedDateTo(Date modifiedDateTo)
	{
		this.modifiedDateTo = modifiedDateTo;
	}

	/**
	 * @return the evaluationPoolMappingStatus
	 */
	public String getEvaluationPoolMappingStatus()
	{
		return evaluationPoolMappingStatus;
	}

	/**
	 * @param evaluationPoolMappingStatus the evaluationPoolMappingStatus to set
	 */
	public void setEvaluationPoolMappingStatus(String evaluationPoolMappingStatus)
	{
		this.evaluationPoolMappingStatus = evaluationPoolMappingStatus;
	}

	/**
	 * @return the procurementtitle
	 */
	public String getProcurementtitle()
	{
		return procurementtitle;
	}

	/**
	 * @param procurementtitle the procurementtitle to set
	 */
	public void setProcurementtitle(String procurementtitle)
	{
		this.procurementtitle = procurementtitle;
	}

	/**
	 * @return the competitionpoolid
	 */
	public String getCompetitionpoolid()
	{
		return competitionpoolid;
	}

	/**
	 * @param competitionpoolid the competitionpoolid to set
	 */
	public void setCompetitionpoolid(String competitionpoolid)
	{
		this.competitionpoolid = competitionpoolid;
	}

	// End : Added in R5
	/**
	 * @return the evalGrpQuesVersion
	 */
	public String getEvalGrpQuesVersion()
	{
		return evalGrpQuesVersion;
	}

	/**
	 * @param evalGrpQuesVersion the evalGrpQuesVersion to set
	 */
	public void setEvalGrpQuesVersion(String evalGrpQuesVersion)
	{
		this.evalGrpQuesVersion = evalGrpQuesVersion;
	}

	/**
	 * @return the evalGrpDocVersion
	 */
	public String getEvalGrpDocVersion()
	{
		return evalGrpDocVersion;
	}

	/**
	 * @param evalGrpDocVersion the evalGrpDocVersion to set
	 */
	public void setEvalGrpDocVersion(String evalGrpDocVersion)
	{
		this.evalGrpDocVersion = evalGrpDocVersion;
	}

	/**
	 * @return the quesVersion
	 */
	public String getQuesVersion()
	{
		return quesVersion;
	}

	/**
	 * @param quesVersion the quesVersion to set
	 */
	public void setQuesVersion(String quesVersion)
	{
		this.quesVersion = quesVersion;
	}

	/**
	 * @return the docVersion
	 */
	public String getDocVersion()
	{
		return docVersion;
	}

	/**
	 * @param docVersion the docVersion to set
	 */
	public void setDocVersion(String docVersion)
	{
		this.docVersion = docVersion;
	}

	/**
	 * @return the latestVersionQues
	 */
	public String getLatestVersionQues()
	{
		return latestVersionQues;
	}

	/**
	 * @param latestVersionQues the latestVersionQues to set
	 */
	public void setLatestVersionQues(String latestVersionQues)
	{
		this.latestVersionQues = latestVersionQues;
	}

	/**
	 * @return the latestVersionDoc
	 */
	public String getLatestVersionDoc()
	{
		return latestVersionDoc;
	}

	/**
	 * @param latestVersionDoc the latestVersionDoc to set
	 */
	public void setLatestVersionDoc(String latestVersionDoc)
	{
		this.latestVersionDoc = latestVersionDoc;
	}

	/**
	 * @return the versionNoQuestion
	 */
	public String getVersionNoQuestion()
	{
		return versionNoQuestion;
	}

	/**
	 * @param versionNoQuestion the versionNoQuestion to set
	 */
	public void setVersionNoQuestion(String versionNoQuestion)
	{
		this.versionNoQuestion = versionNoQuestion;
	}

	/**
	 * @return the versionNoDocument
	 */
	public String getVersionNoDocument()
	{
		return versionNoDocument;
	}

	/**
	 * @param versionNoDocument the versionNoDocument to set
	 */
	public void setVersionNoDocument(String versionNoDocument)
	{
		this.versionNoDocument = versionNoDocument;
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
	 * @return the evaluationGroupStatus
	 */
	public String getEvaluationGroupStatus()
	{
		return evaluationGroupStatus;
	}

	/**
	 * @param evaluationGroupStatus the evaluationGroupStatus to set
	 */
	public void setEvaluationGroupStatus(String evaluationGroupStatus)
	{
		this.evaluationGroupStatus = evaluationGroupStatus;
	}

	/**
	 * 
	 * @return the procReviewStatusId
	 */

	public String getProcReviewStatusId()
	{
		return procReviewStatusId;
	}

	/**
	 * 
	 * @param procReviewStatusId the procReviewStatusId to set
	 */

	public void setProcReviewStatusId(String procReviewStatusId)
	{
		this.procReviewStatusId = procReviewStatusId;
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
	 * @return the proposalDueDate
	 */
	public Date getProposalDueDate()
	{
		return proposalDueDate;
	}

	/**
	 * @param proposalDueDate the proposalDueDate to set
	 */
	public void setProposalDueDate(Date proposalDueDate)
	{
		this.proposalDueDate = proposalDueDate;
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
	 * @return the proposalStatusName
	 */
	public String getProposalStatusName()
	{
		return proposalStatusName;
	}

	/**
	 * @param proposalStatusName the proposalStatusName to set
	 */
	public void setProposalStatusName(String proposalStatusName)
	{
		this.proposalStatusName = proposalStatusName;
	}

	/**
	 * @return the totalNumberOfService Long
	 */
	public Long getTotalNumberOfServiceLong() throws ApplicationException
	{
		return (Long) CommonUtil.convertCurrencyFormatToNumber(totalNumberOfService);
	}

	/**
	 * @return the totalFundingRequest BigDec
	 */
	public BigDecimal getTotalFundingRequestBigDec() throws ApplicationException
	{
		if (null != totalFundingRequest)
		{
			return (BigDecimal) CommonUtil.convertCurrencyFormatToNumber(totalFundingRequest);
		}
		else
		{
			int liBigDecimalInitializer = 0;
			return new BigDecimal(liBigDecimalInitializer);
		}
	}

	/**
	 * @return the procurementDocumentId
	 */
	public String getProcurementDocumentId()
	{
		return procurementDocumentId;
	}

	/**
	 * @param procurementDocumentId the procurementDocumentId to set
	 */
	public void setProcurementDocumentId(String procurementDocumentId)
	{
		this.procurementDocumentId = procurementDocumentId;
	}

	/**
	 * @return the lastModifiedDate
	 */
	public String getLastModifiedDate()
	{
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(String lastModifiedDate)
	{
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * @return the evaluationScore
	 */
	public String getEvaluationScore()
	{
		return evaluationScore;
	}

	/**
	 * @param evaluationScore the evaluationScore to set
	 */
	public void setEvaluationScore(String evaluationScore)
	{
		this.evaluationScore = evaluationScore;
	}

	/**
	 * @return the proposalStatus
	 */
	public String getProposalStatus()
	{
		return proposalStatus;
	}

	/**
	 * @param proposalStatus the proposalStatus to set
	 */
	public void setProposalStatus(String proposalStatus)
	{
		this.proposalStatus = proposalStatus;
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
	 * @return the rank
	 */
	public Integer getRank()
	{
		return rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(Integer rank)
	{
		this.rank = rank;
	}

	public ProposalDetailsBean()
	{
		siteDetailsList = new AutoPopulatingList<SiteDetailsBean>(SiteDetailsBean.class);
		setFirstSort("a.MODIFIED_DATE");
		setSecondSort("PROPOSAL_TITLE");
		setFirstSortType(ApplicationConstants.DESCENDING);
		setSecondSortType(ApplicationConstants.ASCENDING);
		setSortColumnName("modifiedDate");
		setFirstSortDate(true);
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
	 * @return the providerContactId
	 */
	public String getProviderContactId()
	{
		return providerContactId;
	}

	/**
	 * @param providerContactId the providerContactId to set
	 */
	public void setProviderContactId(String providerContactId)
	{
		this.providerContactId = providerContactId;
	}

	/**
	 * @return the totalNumberOfService
	 */
	public String getTotalNumberOfService()
	{
		return totalNumberOfService;
	}

	/**
	 * @param totalNumberOfService the totalNumberOfService to set
	 */
	public void setTotalNumberOfService(String totalNumberOfService)
	{
		this.totalNumberOfService = totalNumberOfService;
	}

	/**
	 * @return the totalFundingRequest
	 */
	public String getTotalFundingRequest()
	{
		return totalFundingRequest;
	}

	/**
	 * @param totalFundingRequest the totalFundingRequest to set
	 */
	public void setTotalFundingRequest(String totalFundingRequest)
	{
		this.totalFundingRequest = totalFundingRequest;
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
	 * @return the modifiedBy
	 */
	public String getModifiedBy()
	{
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the siteDetailsList
	 */
	public List<SiteDetailsBean> getSiteDetailsList()
	{
		return siteDetailsList;
	}

	/**
	 * @param siteDetailsList the siteDetailsList to set
	 */
	public void setSiteDetailsList(List<SiteDetailsBean> siteDetailsList)
	{
		this.siteDetailsList = siteDetailsList;
	}

	/**
	 * @return the questionAnswerBean
	 */
	public List<ProposalQuestionAnswerBean> getQuestionAnswerBeanList()
	{
		return questionAnswerBeanList;
	}

	/**
	 * @param questionAnswerBeanList the questionAnswerBeanList to set
	 */
	public void setQuestionAnswerBeanList(List<ProposalQuestionAnswerBean> questionAnswerBean)
	{
		this.questionAnswerBeanList = questionAnswerBean;
	}

	/**
	 * @return the documentList
	 */
	public List<ExtendedDocument> getDocumentList()
	{
		return documentList;
	}

	/**
	 * @param documentList the documentList to set
	 */
	public void setDocumentList(List<ExtendedDocument> documentList)
	{
		this.documentList = documentList;
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
	 * @return the providerEmailId
	 */
	public String getProviderEmailId()
	{
		return providerEmailId;
	}

	/**
	 * @param providerEmailId the providerEmailId to set
	 */
	public void setProviderEmailId(String providerEmailId)
	{
		this.providerEmailId = providerEmailId;
	}

	/**
	 * @return the providerPhone
	 */
	public String getProviderPhone()
	{
		return providerPhone;
	}

	/**
	 * @param providerPhone the providerPhone to set
	 */
	public void setProviderPhone(String providerPhone)
	{
		this.providerPhone = providerPhone;
	}

	/**
	 * @return the providerOfficeTitle
	 */
	public String getProviderOfficeTitle()
	{
		return providerOfficeTitle;
	}

	/**
	 * @param providerOfficeTitle the providerOfficeTitle to set
	 */
	public void setProviderOfficeTitle(String providerOfficeTitle)
	{
		this.providerOfficeTitle = providerOfficeTitle;
	}

	/**
	 * @return the costPerUnit
	 */
	public String getCostPerUnit()
	{
		return costPerUnit;
	}

	/**
	 * @param costPerUnit the costPerUnit to set
	 */
	public void setCostPerUnit(String costPerUnit)
	{
		this.costPerUnit = costPerUnit;
	}

	/**
	 * @return the requiredDocumentList
	 */
	public List<ExtendedDocument> getRequiredDocumentList()
	{
		return requiredDocumentList;
	}

	/**
	 * @param requiredDocumentList the requiredDocumentList to set
	 */
	public void setRequiredDocumentList(List<ExtendedDocument> requiredDocumentList)
	{
		this.requiredDocumentList = requiredDocumentList;
	}

	/**
	 * @return the optionalDocumentList
	 */
	public List<ExtendedDocument> getOptionalDocumentList()
	{
		return optionalDocumentList;
	}

	/**
	 * @param optionalDocumentList the optionalDocumentList to set
	 */
	public void setOptionalDocumentList(List<ExtendedDocument> optionalDocumentList)
	{
		this.optionalDocumentList = optionalDocumentList;
	}

	/**
	 * @return the procurementStatus
	 */
	public String getProcurementStatus()
	{
		return procurementStatus;
	}

	/**
	 * @return the organizationLegalName
	 */
	public String getOrganizationLegalName()
	{
		return organizationLegalName;
	}

	/**
	 * @param organizationLegalName the organizationLegalName to set
	 */
	public void setOrganizationLegalName(String organizationLegalName)
	{
		this.organizationLegalName = organizationLegalName;
	}

	/**
	 * @param procurementStatus the procurementStatus to set
	 */
	public void setProcurementStatus(String procurementStatus)
	{
		this.procurementStatus = procurementStatus;
	}

	public void setAction(String action)
	{
		this.action = action;
	}

	public String getAction()
	{
		return action;
	}

	/**
	 * @return the serviceUnitFlag
	 */
	public String getServiceUnitFlag()
	{
		return serviceUnitFlag;
	}

	/**
	 * @param serviceUnitFlag the serviceUnitFlag to set
	 */
	public void setServiceUnitFlag(String serviceUnitFlag)
	{
		this.serviceUnitFlag = serviceUnitFlag;
	}

	/**
	 * @return the competitionPool
	 */
	public String getCompetitionPool()
	{
		if (null == competitionPool)
		{
			competitionPool = getHiddenCompPoolId();
		}
		return competitionPool;
	}

	/**
	 * @param competitionPool the competitionPool to set
	 */
	public void setCompetitionPool(String competitionPool)
	{
		this.competitionPool = competitionPool;
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

	/**
	 * @return the hiddenCompPoolId
	 */
	public String getHiddenCompPoolId()
	{
		return hiddenCompPoolId;
	}

	/**
	 * @param hiddenCompPoolId the hiddenCompPoolId to set
	 */
	public void setHiddenCompPoolId(String hiddenCompPoolId)
	{
		this.hiddenCompPoolId = hiddenCompPoolId;
	}

	/**
	 * @return the restrictSubmit
	 */
	public String getRestrictSubmit()
	{
		return restrictSubmit;
	}

	/**
	 * @param restrictSubmit the restrictSubmit to set
	 */
	public void setRestrictSubmit(String restrictSubmit)
	{
		this.restrictSubmit = restrictSubmit;
	}

	public String getRoleCurrent() {
		return roleCurrent;
	}

	public void setRoleCurrent(String roleCurrent) {
		this.roleCurrent = roleCurrent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ProposalDetailsBean [providerName=" + providerName + ", providerContactId=" + providerContactId
		 		+ ", roleCurrent =" + roleCurrent
				+ ", providerEmailId=" + providerEmailId + ", providerPhone=" + providerPhone
				+ ", providerOfficeTitle=" + providerOfficeTitle + ", procurementId=" + procurementId
				+ ", organizationId=" + organizationId + ", organizationLegalName=" + organizationLegalName
				+ ", proposalId=" + proposalId + ", proposalTitle=" + proposalTitle + ", totalNumberOfService="
				+ totalNumberOfService + ", totalFundingRequest=" + totalFundingRequest + ", costPerUnit="
				+ costPerUnit + ", createdBy=" + createdBy + ", modifiedBy=" + modifiedBy + ", siteDetailsList="
				+ siteDetailsList + ", questionAnswerBeanList=" + questionAnswerBeanList + ", documentList="
				+ documentList + ", requiredDocumentList=" + requiredDocumentList + ", optionalDocumentList="
				+ optionalDocumentList + ", procurementStatus=" + procurementStatus + ", proposalStatus="
				+ proposalStatus + ", proposalStatusName=" + proposalStatusName + ", evaluationScore="
				+ evaluationScore + ", modifiedDate=" + modifiedDate + ", rank=" + rank + ", action=" + action
				+ ", lastModifiedDate=" + lastModifiedDate + ", procurementDocumentId=" + procurementDocumentId
				+ ", proposalStatusId=" + proposalStatusId + ", proposalDueDate=" + proposalDueDate + ", userRole="
				+ userRole + ", procReviewStatusId=" + procReviewStatusId + ", serviceUnitFlag=" + serviceUnitFlag
				+ ", competitionPool=" + competitionPool + ", competitionPoolTitle=" + competitionPoolTitle
				+ ", evaluationGroupStatus=" + evaluationGroupStatus + ", evaluationGroupTitle=" + evaluationGroupTitle
				+ ", isOpenEndedRFP=" + isOpenEndedRFP + "]";
	}
}
