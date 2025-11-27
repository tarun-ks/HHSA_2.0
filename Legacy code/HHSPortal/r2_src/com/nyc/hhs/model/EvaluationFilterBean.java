package com.nyc.hhs.model;

import java.math.BigDecimal;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

public class EvaluationFilterBean extends BaseFilter
{
	@RegExp(value ="^\\d{0,22}")
	String procurementId;
	String proposalTitle;
	String organizationName;
	Integer evaluationScore;
	String proposalStatus;
	Double awardAmount;
	List<String> proposalStatusList;
	List<String> proposalStatusIdList;
	private String isFiltered;
	private Boolean filteredCheck;
	Integer scoreRangeFrom;
	Integer scoreRangeTo;
	BigDecimal awardAmountFrom;
	BigDecimal awardAmountTo;
	String comments;
	@RegExp(value ="^\\d{0,22}")
	String proposalId;
	String awardReviewStatus;
	String awardReviewStatusId;
	private String isAccoUser;
	private String isAgencyAccoUser;
	private String userRole;
	private Boolean isValidUser;
	private String isPaginationEnable;
	private String orgType;
	@RegExp(value ="^\\d{0,22}")
	private String evaluationPoolMappingId;

	/**
	 * EvaluationFilterBean constructor
	 */
	public EvaluationFilterBean()
	{
		setFirstSort(HHSConstants.PROP_TITLE);
		setSecondSort(HHSConstants.ORGANIZATION_NAME);
		setFirstSortType(HHSConstants.ASCENDING);
		setSecondSortType(HHSConstants.ASCENDING);
		setSortColumnName(HHSConstants.PROPOSAL_TITLE);
	}

	/**
	 * @return the orgType
	 */
	public String getOrgType()
	{
		return orgType;
	}

	/**
	 * @param orgType the orgType to set
	 */
	public void setOrgType(String orgType)
	{
		this.orgType = orgType;
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
	 * @return the evaluationScore
	 */
	public Integer getEvaluationScore()
	{
		return evaluationScore;
	}

	/**
	 * @param evaluationScore the evaluationScore to set
	 */
	public void setEvaluationScore(Integer evaluationScore)
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
	 * @return the awardAmount
	 */
	public Double getAwardAmount()
	{
		return awardAmount;
	}

	/**
	 * @param awardAmount the awardAmount to set
	 */
	public void setAwardAmount(Double awardAmount)
	{
		this.awardAmount = awardAmount;
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
	 * @return the proposalStatusIdList
	 */
	public List<String> getProposalStatusIdList()
	{
		return proposalStatusIdList;
	}

	/**
	 * @param proposalStatusIdList the proposalStatusIdList to set
	 */
	public void setProposalStatusIdList(List<String> proposalStatusIdList)
	{
		this.proposalStatusIdList = proposalStatusIdList;
	}

	/**
	 * @return the isFiltered
	 */
	public String getIsFiltered()
	{
		return isFiltered;
	}

	/**
	 * @param isFiltered the isFiltered to set
	 */
	public void setIsFiltered(String isFiltered)
	{
		this.isFiltered = isFiltered;
	}

	/**
	 * @return the filteredCheck
	 */
	public Boolean getFilteredCheck()
	{
		return filteredCheck;
	}

	/**
	 * @param filteredCheck the filteredCheck to set
	 */
	public void setFilteredCheck(Boolean filteredCheck)
	{
		this.filteredCheck = filteredCheck;
	}

	/**
	 * @return the scoreRangeFrom
	 */
	public Integer getScoreRangeFrom()
	{
		return scoreRangeFrom;
	}

	/**
	 * @param scoreRangeFrom the scoreRangeFrom to set
	 */
	public void setScoreRangeFrom(Integer scoreRangeFrom)
	{
		this.scoreRangeFrom = scoreRangeFrom;
	}

	/**
	 * @return the scoreRangeTo
	 */
	public Integer getScoreRangeTo()
	{
		return scoreRangeTo;
	}

	/**
	 * @param scoreRangeTo the scoreRangeTo to set
	 */
	public void setScoreRangeTo(Integer scoreRangeTo)
	{
		this.scoreRangeTo = scoreRangeTo;
	}

	/**
	 * @return the awardAmountFrom
	 */
	public BigDecimal getAwardAmountFrom()
	{
		return awardAmountFrom;
	}

	/**
	 * @param awardAmountFrom the awardAmountFrom to set
	 */
	public void setAwardAmountFrom(BigDecimal awardAmountFrom)
	{
		this.awardAmountFrom = awardAmountFrom;
	}

	/**
	 * @return the awardAmountTo
	 */
	public BigDecimal getAwardAmountTo()
	{
		return awardAmountTo;
	}

	/**
	 * @param awardAmountTo the awardAmountTo to set
	 */
	public void setAwardAmountTo(BigDecimal awardAmountTo)
	{
		this.awardAmountTo = awardAmountTo;
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
	 * @return the awardReviewStatusId
	 */
	public String getAwardReviewStatusId()
	{
		return awardReviewStatusId;
	}

	/**
	 * @param awardReviewStatusId the awardReviewStatusId to set
	 */
	public void setAwardReviewStatusId(String awardReviewStatusId)
	{
		this.awardReviewStatusId = awardReviewStatusId;
	}

	/**
	 * @return the isAccoUser
	 */
	public String getIsAccoUser()
	{
		return isAccoUser;
	}

	/**
	 * @param isAccoUser the isAccoUser to set
	 */
	public void setIsAccoUser(String isAccoUser)
	{
		this.isAccoUser = isAccoUser;
	}

	/**
	 * @return the isAgencyAccoUser
	 */
	public String getIsAgencyAccoUser()
	{
		return isAgencyAccoUser;
	}

	/**
	 * @param isAgencyAccoUser the isAgencyAccoUser to set
	 */
	public void setIsAgencyAccoUser(String isAgencyAccoUser)
	{
		this.isAgencyAccoUser = isAgencyAccoUser;
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
	 * @return the isValidUser
	 */
	public Boolean getIsValidUser()
	{
		return isValidUser;
	}

	/**
	 * @param isValidUser the isValidUser to set
	 */
	public void setIsValidUser(Boolean isValidUser)
	{
		this.isValidUser = isValidUser;
	}

	/**
	 * @return the isPaginationEnable
	 */
	public String getIsPaginationEnable()
	{
		return isPaginationEnable;
	}

	/**
	 * @param isPaginationEnable the isPaginationEnable to set
	 */
	public void setIsPaginationEnable(String isPaginationEnable)
	{
		this.isPaginationEnable = isPaginationEnable;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "EvaluationFilterBean [procurementId=" + procurementId + ", proposalTitle=" + proposalTitle
				+ ", organizationName=" + organizationName + ", evaluationScore=" + evaluationScore
				+ ", proposalStatus=" + proposalStatus + ", awardAmount=" + awardAmount + ", proposalStatusList="
				+ proposalStatusList + ", proposalStatusIdList=" + proposalStatusIdList + ", isFiltered=" + isFiltered
				+ ", filteredCheck=" + filteredCheck + ", scoreRangeFrom=" + scoreRangeFrom + ", scoreRangeTo="
				+ scoreRangeTo + ", awardAmountFrom=" + awardAmountFrom + ", awardAmountTo=" + awardAmountTo
				+ ", comments=" + comments + ", proposalId=" + proposalId + ", awardReviewStatus=" + awardReviewStatus
				+ ", awardReviewStatusId=" + awardReviewStatusId + ", isAccoUser=" + isAccoUser + ", isAgencyAccoUser="
				+ isAgencyAccoUser + ", userRole=" + userRole + ", isValidUser=" + isValidUser
				+ ", isPaginationEnable=" + isPaginationEnable + ", orgType=" + orgType + ", evaluationPoolMappingId="
				+ evaluationPoolMappingId + "]";
	}
}
