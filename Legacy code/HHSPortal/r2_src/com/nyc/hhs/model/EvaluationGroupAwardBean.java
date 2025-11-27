/**
 * 
 */
package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

/**
 * @author sumit.vasudeva
 * 
 */
public class EvaluationGroupAwardBean extends BaseFilter
{

	@Length(max = 25)
	private String evaluationGroupTitle;
	@RegExp(value ="^\\d{0,22}")
	private String evaluationGroupId;
	private String submissionCloseDate;
	private String awardAmount;
	private Integer noOfAwards;
	@RegExp(value ="^\\d{0,22}")
	private String procurementId;

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
	 * @return the submissionCloseDate
	 */
	public String getSubmissionCloseDate()
	{
		return submissionCloseDate;
	}

	/**
	 * @param submissionCloseDate the submissionCloseDate to set
	 */
	public void setSubmissionCloseDate(String submissionCloseDate)
	{
		this.submissionCloseDate = submissionCloseDate;
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
	 * @return the noOfAwards
	 */
	public Integer getNoOfAwards()
	{
		return noOfAwards;
	}

	/**
	 * @param noOfAwards the noOfAwards to set
	 */
	public void setNoOfAwards(Integer noOfAwards)
	{
		this.noOfAwards = noOfAwards;
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

	public EvaluationGroupAwardBean()
	{
		setFirstSort(HHSConstants.EVALUATION_GROUP);
		setSecondSort(HHSConstants.SUBMISSION_CLOSE_DATE);
		setFirstSortType(HHSConstants.DESCENDING);
		setSecondSortType(HHSConstants.DESCENDING);
		setSortColumnName(HHSConstants.EVAL_GROUP_TITLE);
	}

}
