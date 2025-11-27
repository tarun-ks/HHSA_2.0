package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

/**
 * This class is a bean which maintains the user Address information which
 * includes Address, City, State, Zip code and Address type.
 * 
 */

public class EvaluationSummaryBean extends BaseFilter
{
	@RegExp(value ="^\\d{0,22}")
	private String competitionPoolId;
	private String competitionPoolTitle;
	private String providersSubmitted;
	private String proposalsSubmitted;
	private String evaluationStatus;
	private String evaluationsInProgress;
	private String evaluationsComplete;
	private String actions;
	@RegExp(value ="^\\d{0,22}")
	private String evaluationGroupId;
	@RegExp(value ="^\\d{0,22}")
	private String evaluationPoolMappingId;
	private String procurementId;
	private String userRole;
	private int awardApprovalCount;
	private String roleCurrent;
	
	// START || Changes done for Enhancement #6577 for Release 3.10.0
	private String userType;

	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @param userType the userType to set
	 */
	public void setUserType(String userType) {
		this.userType = userType;
	}
	//End || Changes done for Enhancement #6577 for Release 3.10.0
	
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
	 * @return the providersSubmitted
	 */
	public String getProvidersSubmitted()
	{
		return providersSubmitted;
	}

	/**
	 * @param providersSubmitted the providersSubmitted to set
	 */
	public void setProvidersSubmitted(String providersSubmitted)
	{
		this.providersSubmitted = providersSubmitted;
	}

	/**
	 * @return the proposalsSubmitted
	 */
	public String getProposalsSubmitted()
	{
		return proposalsSubmitted;
	}

	/**
	 * @param proposalsSubmitted the proposalsSubmitted to set
	 */
	public void setProposalsSubmitted(String proposalsSubmitted)
	{
		this.proposalsSubmitted = proposalsSubmitted;
	}

	/**
	 * @return the evaluationStatus
	 */
	public String getEvaluationStatus()
	{
		return evaluationStatus;
	}

	/**
	 * @param evaluationStatus the evaluationStatus to set
	 */
	public void setEvaluationStatus(String evaluationStatus)
	{
		this.evaluationStatus = evaluationStatus;
	}

	/**
	 * @return the evaluationsInProgress
	 */
	public String getEvaluationsInProgress()
	{
		return evaluationsInProgress;
	}

	/**
	 * @param evaluationsInProgress the evaluationsInProgress to set
	 */
	public void setEvaluationsInProgress(String evaluationsInProgress)
	{
		this.evaluationsInProgress = evaluationsInProgress;
	}

	/**
	 * @return the evaluationsComplete
	 */
	public String getEvaluationsComplete()
	{
		return evaluationsComplete;
	}

	/**
	 * @param evaluationsComplete the evaluationsComplete to set
	 */
	public void setEvaluationsComplete(String evaluationsComplete)
	{
		this.evaluationsComplete = evaluationsComplete;
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
	 * @return the awardApprovalCount
	 */
	public int getAwardApprovalCount()
	{
		return awardApprovalCount;
	}

	/**
	 * @param awardApprovalCount the awardApprovalCount to set
	 */
	public void setAwardApprovalCount(int awardApprovalCount)
	{
		this.awardApprovalCount = awardApprovalCount;
	}



	public String getRoleCurrent() {
		return roleCurrent;
	}

	public void setRoleCurrent(String roleCurrent) {
		this.roleCurrent = roleCurrent;
	}

	public EvaluationSummaryBean()
	{
		setFirstSort(HHSConstants.COMP_POOL_TITLE);
		setSecondSort(HHSConstants.STAT);
		setFirstSortType(HHSConstants.ASCENDING);
		setSecondSortType(HHSConstants.ASCENDING);
		setSortColumnName(HHSConstants.COMPETITION_POOL_TITLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "EvaluationSummaryBean [competitionPoolId=" + competitionPoolId + ", competitionPoolTitle="
				+ competitionPoolTitle + ", roleCurrent=" + roleCurrent + ", providersSubmitted=" + providersSubmitted + ", proposalsSubmitted="
				+ proposalsSubmitted + ", evaluationStatus=" + evaluationStatus + ", evaluationsInProgress="
				+ evaluationsInProgress + ", evaluationsComplete=" + evaluationsComplete + ", actions=" + actions
				+ ", evaluationGroupId=" + evaluationGroupId + ", evaluationPoolMappingId=" + evaluationPoolMappingId
				+ ", procurementId=" + procurementId + "]";
	}

}
