package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

public class EvaluationGroupsProposalBean extends BaseFilter
{
	@RegExp(value ="^\\d{0,22}")
	private String evaluationGroupId;
	private String evaluationGroupTitle;
	private String closingDate;
	private String providersSubmitted;
	private String proposalsSubmitted;
	private String evaluationStatus;
	private String evaluationsInProgress;
	private String evaluationsComplete;
	private String procurementId;

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
	 * @return the closingDate
	 */
	public String getClosingDate()
	{
		return closingDate;
	}

	/**
	 * @param closingDate the closingDate to set
	 */
	public void setClosingDate(String closingDate)
	{
		this.closingDate = closingDate;
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

	public EvaluationGroupsProposalBean()
	{
		setFirstSort(HHSConstants.EVALUATION_GROUP);
		setSecondSort(HHSConstants.STAT);
		setFirstSortType(HHSConstants.DESCENDING);
		setSecondSortType(HHSConstants.ASCENDING);
		setSortColumnName(HHSConstants.EVAL_GROUP_TITLE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "EvaluationGroupsProposalBean [evaluationGroupId=" + evaluationGroupId + ", evaluationGroupTitle="
				+ evaluationGroupTitle + ", closingDate=" + closingDate + ", providersSubmitted=" + providersSubmitted
				+ ", proposalsSubmitted=" + proposalsSubmitted + ", evaluationStatus=" + evaluationStatus
				+ ", evaluationsInProgress=" + evaluationsInProgress + ", evaluationsComplete=" + evaluationsComplete
				+ ", procurementId=" + procurementId + "]";
	}
}
