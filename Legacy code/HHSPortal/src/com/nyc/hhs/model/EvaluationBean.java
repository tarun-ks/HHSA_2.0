package com.nyc.hhs.model;

import java.util.Date;
import java.util.List;

import com.nyc.hhs.constants.ApplicationConstants;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/**
 * This class is bean class that maintains details of evaluation
 */
public class EvaluationBean extends BaseFilter
{
	@RegExp(value ="^\\d{0,22}")
	private String procurementId;
	private String comments;
	@RegExp(value ="^\\d{0,22}")
	private String proposalId;
	private String organizationId;
	@Length(max = 20)
	private String evaluatorId;
	private String score;
	private String scoreSeqNum;
	private String updateBy;
	private String organizationName;
	private String proposalTitle;
	private String evaluationScore;
	private String awardAmount;
	private String proposalStatus;
	private String procurementStatus;
	private String estimatedProcurementValue;
	private int awardCount;
	private int organizationCount;
	private String awardReviewStatus;
	private String awardReviewStatusId;
	private String awardId;
	private String userOrgType;
	private String userRole;
	private String scoreCriteria;
	private String maximumScore;
	private String proposalScore;
	private Integer proposalStatusId;
	private String providerName;
	private Integer evalutionsInProgress;
	private Integer evalutionsCompleted;
	private String actions;
	private String sendEvaluationStatus;
	private List<String> proposalStatusList;
	private List<String> evaluationStatusList;
	private String evaluatorFirstName;
	private String evaluatorSecondName;
	private String externalEvaluatorName;
	private List<EvaluationBean> evaluatorList;
	private String submissionCloseDate;
	private Date updProposalDueDate;
	private String evaluationStatusId;
	private String evaluationCriteriaId;
	private Integer totalEvaluationCompleted;
	private Integer totalEvaluationInProgess;
	private String modifiedFlag;
	private Float percInProgress;
	private Float percCompleted;
	private String paginationEnable;
	private String evaluationSummary;
	private String createdByUserId;
	private String modifiedByUserId;
	private Date awardApprovalDate;
	private String isFiltered;
	private String procStatusId;
	private Integer rowNum;
	private String evaluatorDisplayId;
	private boolean selectedFlag;
	private Integer proposalCount;
	private String evaluationSent;
	private String notStartedStatus;
	private String sentEvalStatusNonResponsive;
	private String approvedStatus;
	private String inProgressStatus;
	private String completedStatus;
	private String scoreAmended;
	private String actionPerformed;
	private String generalComments;
	private String evaluationPoolMappingId;
	private String isOpenEndedRFP;
	private String evalPoolMappingStatus;

	// R5 code starts
	private String versionNumber;
	private String scoreChangeType;
	private String commentChangeFlag;
	private String generalCommentChangeFlag;
	private String generalScoreChangeType;
	private String roundInfo;
	private String statusId;
	private String returnFlag;
	private String negotiatedAmount;
	private String ExtEpin;
	private String ExtCtNumber;
	private String isPendingNegotiationFlag;
	private String isNegotiationFlag;

	public String getIsNegotiationFlag()
	{
		return isNegotiationFlag;
	}

	public void setIsNegotiationFlag(String isNegotiationFlag)
	{
		this.isNegotiationFlag = isNegotiationFlag;
	}

	public String getIsPendingNegotiationFlag()
	{
		return isPendingNegotiationFlag;
	}

	public void setIsPendingNegotiationFlag(String isPendingNegotiationFlag)
	{
		this.isPendingNegotiationFlag = isPendingNegotiationFlag;
	}

	public String getExtEpin()
	{
		return ExtEpin;
	}

	public void setExtEpin(String extEpin)
	{
		ExtEpin = extEpin;
	}

	public String getExtCtNumber()
	{
		return ExtCtNumber;
	}

	public void setExtCtNumber(String extCtNumber)
	{
		ExtCtNumber = extCtNumber;
	}

	public String getNegotiatedAmount()
	{
		return negotiatedAmount;
	}

	public void setNegotiatedAmount(String negotiatedAmount)
	{
		this.negotiatedAmount = negotiatedAmount;
	}

	/**
	 * @return the versionNumber
	 */
	public String getVersionNumber()
	{
		return versionNumber;
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
	 * @param versionNumber the versionNumber to set
	 */
	public void setVersionNumber(String versionNumber)
	{
		this.versionNumber = versionNumber;
	}

	/**
	 * @return the scoreChangeType
	 */
	public String getScoreChangeType()
	{
		return scoreChangeType;
	}

	/**
	 * @param scoreChangeType the scoreChangeType to set
	 */
	public void setScoreChangeType(String scoreChangeType)
	{
		this.scoreChangeType = scoreChangeType;
	}

	/**
	 * @return the commentChangeFlag
	 */
	public String getCommentChangeFlag()
	{
		return commentChangeFlag;
	}

	/**
	 * @param commentChangeFlag the commentChangeFlag to set
	 */
	public void setCommentChangeFlag(String commentChangeFlag)
	{
		this.commentChangeFlag = commentChangeFlag;
	}

	/**
	 * @return the generalCommentChangeFlag
	 */
	public String getGeneralCommentChangeFlag()
	{
		return generalCommentChangeFlag;
	}

	// added in R5
	/**
	 * @param generalCommentChangeFlag the generalCommentChangeFlag to set
	 */
	public void setGeneralCommentChangeFlag(String generalCommentChangeFlag)
	{
		this.generalCommentChangeFlag = generalCommentChangeFlag;
	}

	/**
	 * @return the generalScoreChangeType
	 */
	public String getGeneralScoreChangeType()
	{
		return generalScoreChangeType;
	}

	/**
	 * @param generalScoreChangeType the generalScoreChangeType to set
	 */
	public void setGeneralScoreChangeType(String generalScoreChangeType)
	{
		this.generalScoreChangeType = generalScoreChangeType;
	}

	/**
	 * @return the roundInfo
	 */
	public String getRoundInfo()
	{
		return roundInfo;
	}

	/**
	 * @param roundInfo the roundInfo to set
	 */
	public void setRoundInfo(String roundInfo)
	{
		this.roundInfo = roundInfo;
	}

	/**
	 * @return the returnFlag
	 */
	public String getReturnFlag()
	{
		return returnFlag;
	}

	/**
	 * @param returnFlag the returnFlag to set
	 */
	public void setReturnFlag(String returnFlag)
	{
		this.returnFlag = returnFlag;
	}

	// R5 code ends
	/**
	 * @return the generalComments
	 */
	public String getGeneralComments()
	{
		return generalComments;
	}

	/**
	 * @param generalComments the generalComments to set
	 */
	public void setGeneralComments(String generalComments)
	{
		this.generalComments = generalComments;
	}

	public String getScoreAmended()
	{
		return scoreAmended;
	}

	public void setScoreAmended(String scoreAmended)
	{
		this.scoreAmended = scoreAmended;
	}

	public String getInProgressStatus()
	{
		return inProgressStatus;
	}

	public void setInProgressStatus(String inProgressStatus)
	{
		this.inProgressStatus = inProgressStatus;
	}

	public String getCompletedStatus()
	{
		return completedStatus;
	}

	public void setCompletedStatus(String completedStatus)
	{
		this.completedStatus = completedStatus;
	}

	public String getApprovedStatus()
	{
		return approvedStatus;
	}

	public void setApprovedStatus(String approvedStatus)
	{
		this.approvedStatus = approvedStatus;
	}

	public String getSentEvalStatusNonResponsive()
	{
		return sentEvalStatusNonResponsive;
	}

	public void setSentEvalStatusNonResponsive(String sentEvalStatusNonResponsive)
	{
		this.sentEvalStatusNonResponsive = sentEvalStatusNonResponsive;
	}

	public String getNotStartedStatus()
	{
		return notStartedStatus;
	}

	public void setNotStartedStatus(String notStartedStatus)
	{
		this.notStartedStatus = notStartedStatus;
	}

	public String getEvaluationSent()
	{
		return evaluationSent;
	}

	public void setEvaluationSent(String evaluationSent)
	{
		this.evaluationSent = evaluationSent;
	}

	public Integer getProposalCount()
	{
		return proposalCount;
	}

	public void setProposalCount(Integer proposalCount)
	{
		this.proposalCount = proposalCount;
	}

	public boolean isSelectedFlag()
	{
		return selectedFlag;
	}

	public void setSelectedFlag(boolean selectedFlag)
	{
		this.selectedFlag = selectedFlag;
	}

	public String getEvaluatorDisplayId()
	{
		return evaluatorDisplayId;
	}

	public void setEvaluatorDisplayId(String evaluatorDisplayId)
	{
		this.evaluatorDisplayId = evaluatorDisplayId;
	}

	/**
	 * @return the rowNum
	 */
	public Integer getRowNum()
	{
		return rowNum;
	}

	/**
	 * @param rowNum the rowNum to set
	 */
	public void setRowNum(Integer rowNum)
	{
		this.rowNum = rowNum;
	}

	/**
	 * @return the procStatusId
	 */
	public String getProcStatusId()
	{
		return procStatusId;
	}

	/**
	 * @param procStatusId the procStatusId to set
	 */
	public void setProcStatusId(String procStatusId)
	{
		this.procStatusId = procStatusId;
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

	public String getIsFiltered()
	{
		return isFiltered;
	}

	public void setIsFiltered(String isFiltered)
	{
		this.isFiltered = isFiltered;
	}

	public Date getAwardApprovalDate()
	{
		return awardApprovalDate;
	}

	public void setAwardApprovalDate(Date awardApprovalDate)
	{
		this.awardApprovalDate = awardApprovalDate;
	}

	private String agencyUserId;

	public String getPaginationEnable()
	{
		return paginationEnable;
	}

	public void setPaginationEnable(String paginationEnable)
	{
		this.paginationEnable = paginationEnable;
	}

	public Integer getTotalEvaluationCompleted()
	{
		return totalEvaluationCompleted;
	}

	public void setTotalEvaluationCompleted(Integer totalEvaluationCompleted)
	{
		this.totalEvaluationCompleted = totalEvaluationCompleted;
	}

	public Integer getTotalEvaluationInProgess()
	{
		return totalEvaluationInProgess;
	}

	public void setTotalEvaluationInProgess(Integer totalEvaluationInProgess)
	{
		this.totalEvaluationInProgess = totalEvaluationInProgess;
	}

	public String getModifiedFlag()
	{
		return modifiedFlag;
	}

	public void setModifiedFlag(String modifiedFlag)
	{
		this.modifiedFlag = modifiedFlag;
	}

	public Float getPercInProgress()
	{
		return percInProgress;
	}

	public void setPercInProgress(Float percInProgress)
	{
		this.percInProgress = percInProgress;
	}

	public Float getPercCompleted()
	{
		return percCompleted;
	}

	public void setPercCompleted(Float percCompleted)
	{
		this.percCompleted = percCompleted;
	}

	public String getEvaluationStatusId()
	{
		return evaluationStatusId;
	}

	public void setEvaluationStatusId(String evaluationStatusId)
	{
		this.evaluationStatusId = evaluationStatusId;
	}

	public String getEvaluationCriteriaId()
	{
		return evaluationCriteriaId;
	}

	public void setEvaluationCriteriaId(String evaluationCriteriaId)
	{
		this.evaluationCriteriaId = evaluationCriteriaId;
	}

	public String getSubmissionCloseDate()
	{
		return submissionCloseDate;
	}

	public void setSubmissionCloseDate(String submissionCloseDate)
	{
		this.submissionCloseDate = submissionCloseDate;
	}

	public Date getUpdProposalDueDate()
	{
		return updProposalDueDate;
	}

	public void setUpdProposalDueDate(Date updProposalDueDate)
	{
		this.updProposalDueDate = updProposalDueDate;
	}

	/**
	 * @return the externalEvaluatorName
	 */
	public String getExternalEvaluatorName()
	{
		return externalEvaluatorName;
	}

	/**
	 * @return the evaluatorList
	 */
	public List<EvaluationBean> getEvaluatorList()
	{
		return evaluatorList;
	}

	/**
	 * @param evaluatorList the evaluatorList to set
	 */
	public void setEvaluatorList(List<EvaluationBean> evaluatorList)
	{
		this.evaluatorList = evaluatorList;
	}

	/**
	 * @param externalEvaluatorName the externalEvaluatorName to set
	 */
	public void setExternalEvaluatorName(String externalEvaluatorName)
	{
		this.externalEvaluatorName = externalEvaluatorName;
	}

	/**
	 * @return the evaluatorFirstName
	 */
	public String getEvaluatorFirstName()
	{
		return evaluatorFirstName;
	}

	/**
	 * @param evaluatorFirstName the evaluatorFirstName to set
	 */
	public void setEvaluatorFirstName(String evaluatorFirstName)
	{
		this.evaluatorFirstName = evaluatorFirstName;
	}

	/**
	 * @return the evaluatorSecondName
	 */
	public String getEvaluatorSecondName()
	{
		return evaluatorSecondName;
	}

	/**
	 * @param evaluatorSecondName the evaluatorSecondName to set
	 */
	public void setEvaluatorSecondName(String evaluatorSecondName)
	{
		this.evaluatorSecondName = evaluatorSecondName;
	}

	public EvaluationBean()
	{
		setFirstSort("Evalution_Completed");
		setSecondSort("ORGANIZATION_LEGAL_NAME");
		setFirstSortType(ApplicationConstants.SORT_ASCENDING);
		setSecondSortType(ApplicationConstants.SORT_ASCENDING);
		setSortColumnName("evalutionsCompleted");
	}

	public String getSendEvaluationStatus()
	{
		return sendEvaluationStatus;
	}

	public void setSendEvaluationStatus(String sendEvaluationStatus)
	{
		this.sendEvaluationStatus = sendEvaluationStatus;
	}

	public String getActions()
	{
		return actions;
	}

	public void setActions(String actions)
	{
		this.actions = actions;
	}

	public String getProviderName()
	{
		return providerName;
	}

	public void setProviderName(String providerName)
	{
		this.providerName = providerName;
	}

	public Integer getEvalutionsInProgress()
	{
		return evalutionsInProgress;
	}

	public void setEvalutionsInProgress(Integer evalutionsInProgress)
	{
		this.evalutionsInProgress = evalutionsInProgress;
	}

	public Integer getEvalutionsCompleted()
	{
		return evalutionsCompleted;
	}

	public void setEvalutionsCompleted(Integer evalutionsCompleted)
	{
		this.evalutionsCompleted = evalutionsCompleted;
	}

	public Integer getProposalStatusId()
	{
		return proposalStatusId;
	}

	public void setProposalStatusId(Integer proposalStatusId)
	{
		this.proposalStatusId = proposalStatusId;
	}

	public String getProposalScore()
	{
		return proposalScore;
	}

	public void setProposalScore(String proposalScore)
	{
		this.proposalScore = proposalScore;
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
	 * @return the comments
	 */
	public String getComments()
	{
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments)
	{
		this.comments = comments;
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
	 * @return the evaluatorId
	 */
	public String getEvaluatorId()
	{
		return evaluatorId;
	}

	/**
	 * @param evaluatorId the evaluatorId to set
	 */
	public void setEvaluatorId(String evaluatorId)
	{
		this.evaluatorId = evaluatorId;
	}

	/**
	 * @return the score
	 */
	public String getScore()
	{
		return score;
	}

	/**
	 * @param score the score to set
	 */
	public void setScore(String score)
	{
		this.score = score;
	}

	/**
	 * @return the scoreSeqNum
	 */
	public String getScoreSeqNum()
	{
		return scoreSeqNum;
	}

	/**
	 * @param scoreSeqNum the scoreSeqNum to set
	 */
	public void setScoreSeqNum(String scoreSeqNum)
	{
		this.scoreSeqNum = scoreSeqNum;
	}

	/**
	 * @return the updateBy
	 */
	public String getUpdateBy()
	{
		return updateBy;
	}

	/**
	 * @param updateBy the updateBy to set
	 */
	public void setUpdateBy(String updateBy)
	{
		this.updateBy = updateBy;
	}

	/**
	 * @return the organizationName
	 */
	public String getOrganizationName()
	{
		return organizationName;
	}

	/**
	 * @param organizationName the organizationName to set
	 */
	public void setOrganizationName(String organizationName)
	{
		this.organizationName = organizationName;
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
	 * @return the procurementStatus
	 */
	public String getProcurementStatus()
	{
		return procurementStatus;
	}

	/**
	 * @param procurementStatus the procurementStatus to set
	 */
	public void setProcurementStatus(String procurementStatus)
	{
		this.procurementStatus = procurementStatus;
	}

	/**
	 * @return the estimatedProcurementValue
	 */
	public String getEstimatedProcurementValue()
	{
		return estimatedProcurementValue;
	}

	/**
	 * @param estimatedProcurementValue the estimatedProcurementValue to set
	 */
	public void setEstimatedProcurementValue(String estimatedProcurementValue)
	{
		this.estimatedProcurementValue = estimatedProcurementValue;
	}

	/**
	 * @return the awardCount
	 */
	public int getAwardCount()
	{
		return awardCount;
	}

	/**
	 * @param awardCount the awardCount to set
	 */
	public void setAwardCount(int awardCount)
	{
		this.awardCount = awardCount;
	}

	/**
	 * @return the organizationCount
	 */
	public int getOrganizationCount()
	{
		return organizationCount;
	}

	/**
	 * @param organizationCount the organizationCount to set
	 */
	public void setOrganizationCount(int organizationCount)
	{
		this.organizationCount = organizationCount;
	}

	/**
	 * @return the awardReviewStatus
	 */
	public String getAwardReviewStatus()
	{
		return awardReviewStatus;
	}

	/**
	 * @param awardReviewStatus the awardReviewStatus to set
	 */
	public void setAwardReviewStatus(String awardReviewStatus)
	{
		this.awardReviewStatus = awardReviewStatus;
	}

	/**
	 * @return the userOrgType
	 */
	public String getUserOrgType()
	{
		return userOrgType;
	}

	/**
	 * @param userOrgType the userOrgType to set
	 */
	public void setUserOrgType(String userOrgType)
	{
		this.userOrgType = userOrgType;
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

	public String getScoreCriteria()
	{
		return scoreCriteria;
	}

	public void setScoreCriteria(String scoreCriteria)
	{
		this.scoreCriteria = scoreCriteria;
	}

	public String getMaximumScore()
	{
		return maximumScore;
	}

	public void setMaximumScore(String maximumScore)
	{
		this.maximumScore = maximumScore;
	}

	public List<String> getProposalStatusList()
	{
		return proposalStatusList;
	}

	public void setProposalStatusList(List<String> proposalStatusList)
	{
		this.proposalStatusList = proposalStatusList;
	}

	public List<String> getEvaluationStatusList()
	{
		return evaluationStatusList;
	}

	public void setEvaluationStatusList(List<String> evaluationStatusList)
	{
		this.evaluationStatusList = evaluationStatusList;
	}

	/**
	 * @return the evaluationSummary
	 */
	public String getEvaluationSummary()
	{
		return evaluationSummary;
	}

	/**
	 * @param evaluationSummary the evaluationSummary to set
	 */
	public void setEvaluationSummary(String evaluationSummary)
	{
		this.evaluationSummary = evaluationSummary;
	}

	public String getCreatedByUserId()
	{
		return createdByUserId;
	}

	public void setCreatedByUserId(String createdByUserId)
	{
		this.createdByUserId = createdByUserId;
	}

	public String getModifiedByUserId()
	{
		return modifiedByUserId;
	}

	public void setModifiedByUserId(String modifiedByUserId)
	{
		this.modifiedByUserId = modifiedByUserId;
	}

	public String getAwardReviewStatusId()
	{
		return awardReviewStatusId;
	}

	public void setAwardReviewStatusId(String awardReviewStatusId)
	{
		this.awardReviewStatusId = awardReviewStatusId;
	}

	/**
	 * @return the agencyUserId
	 */
	public String getAgencyUserId()
	{
		return agencyUserId;
	}

	/**
	 * @param agencyUserId the agencyUserId to set
	 */
	public void setAgencyUserId(String agencyUserId)
	{
		this.agencyUserId = agencyUserId;
	}

	/**
	 * @return the actionPerformed
	 */
	public String getActionPerformed()
	{
		return actionPerformed;
	}

	/**
	 * @param actionPerformed the actionPerformed to set
	 */
	public void setActionPerformed(String actionPerformed)
	{
		this.actionPerformed = actionPerformed;
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
	 * @return the evalPoolMappingStatus
	 */
	public String getEvalPoolMappingStatus()
	{
		return evalPoolMappingStatus;
	}

	/**
	 * @param evalPoolMappingStatus the evalPoolMappingStatus to set
	 */
	public void setEvalPoolMappingStatus(String evalPoolMappingStatus)
	{
		this.evalPoolMappingStatus = evalPoolMappingStatus;
	}

	// Start R5 : Updated toString
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "EvaluationBean [procurementId=" + procurementId + ", comments=" + comments + ", proposalId="
				+ proposalId + ", organizationId=" + organizationId + ", evaluatorId=" + evaluatorId + ", score="
				+ score + ", scoreSeqNum=" + scoreSeqNum + ", updateBy=" + updateBy + ", organizationName="
				+ organizationName + ", proposalTitle=" + proposalTitle + ", evaluationScore=" + evaluationScore
				+ ", awardAmount=" + awardAmount + ", proposalStatus=" + proposalStatus + ", procurementStatus="
				+ procurementStatus + ", estimatedProcurementValue=" + estimatedProcurementValue + ", awardCount="
				+ awardCount + ", organizationCount=" + organizationCount + ", awardReviewStatus=" + awardReviewStatus
				+ ", awardReviewStatusId=" + awardReviewStatusId + ", awardId=" + awardId + ", userOrgType="
				+ userOrgType + ", userRole=" + userRole + ", scoreCriteria=" + scoreCriteria + ", maximumScore="
				+ maximumScore + ", proposalScore=" + proposalScore + ", proposalStatusId=" + proposalStatusId
				+ ", providerName=" + providerName + ", evalutionsInProgress=" + evalutionsInProgress
				+ ", evalutionsCompleted=" + evalutionsCompleted + ", actions=" + actions + ", sendEvaluationStatus="
				+ sendEvaluationStatus + ", proposalStatusList=" + proposalStatusList + ", evaluationStatusList="
				+ evaluationStatusList + ", evaluatorFirstName=" + evaluatorFirstName + ", evaluatorSecondName="
				+ evaluatorSecondName + ", externalEvaluatorName=" + externalEvaluatorName + ", evaluatorList="
				+ evaluatorList + ", submissionCloseDate=" + submissionCloseDate + ", updProposalDueDate="
				+ updProposalDueDate + ", evaluationStatusId=" + evaluationStatusId + ", evaluationCriteriaId="
				+ evaluationCriteriaId + ", totalEvaluationCompleted=" + totalEvaluationCompleted
				+ ", totalEvaluationInProgess=" + totalEvaluationInProgess + ", modifiedFlag=" + modifiedFlag
				+ ", percInProgress=" + percInProgress + ", percCompleted=" + percCompleted + ", paginationEnable="
				+ paginationEnable + ", evaluationSummary=" + evaluationSummary + ", createdByUserId="
				+ createdByUserId + ", modifiedByUserId=" + modifiedByUserId + ", awardApprovalDate="
				+ awardApprovalDate + ", isFiltered=" + isFiltered + ", procStatusId=" + procStatusId + ", rowNum="
				+ rowNum + ", evaluatorDisplayId=" + evaluatorDisplayId + ", selectedFlag=" + selectedFlag
				+ ", proposalCount=" + proposalCount + ", evaluationSent=" + evaluationSent + ", notStartedStatus="
				+ notStartedStatus + ", sentEvalStatusNonResponsive=" + sentEvalStatusNonResponsive
				+ ", approvedStatus=" + approvedStatus + ", inProgressStatus=" + inProgressStatus
				+ ", completedStatus=" + completedStatus + ", scoreAmended=" + scoreAmended + ", actionPerformed="
				+ actionPerformed + ", generalComments=" + generalComments + ", evaluationPoolMappingId="
				+ evaluationPoolMappingId + ", isOpenEndedRFP=" + isOpenEndedRFP + ", evalPoolMappingStatus="
				+ evalPoolMappingStatus + ", versionNumber=" + versionNumber + ", scoreChangeType=" + scoreChangeType
				+ ", commentChangeFlag=" + commentChangeFlag + ", generalCommentChangeFlag=" + generalCommentChangeFlag
				+ ", generalScoreChangeType=" + generalScoreChangeType + ", roundInfo=" + roundInfo + ", statusId="
				+ statusId + ", returnFlag=" + returnFlag + ", agencyUserId=" + agencyUserId + "]";
	}
	// End R5 : Updated toString
}