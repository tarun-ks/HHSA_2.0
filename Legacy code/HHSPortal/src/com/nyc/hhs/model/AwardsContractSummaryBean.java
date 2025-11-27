package com.nyc.hhs.model;

import com.nyc.hhs.constants.HHSConstants;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class AwardsContractSummaryBean extends BaseFilter
{
	
	//@Digits(integer=22, fraction=0)
	@RegExp(value ="^\\d{0,22}")
	private String competitionPoolId;
	private String competitionPoolTitle;
	@RegExp(value ="^\\d{0,22}")
	private String evaluationGroupId;
	@RegExp(value ="^\\d{0,22}")
	private String evaluationPoolMappingId;
	@RegExp(value ="^\\d{0,22}")
	private String procurementId;
	@RegExp(value ="^\\d{0,22}")
	private Integer numberOfAwards;
	private String totalAwardAmount;
	/**
	 * 6577 for Release 3.10.0
	 */
	private String evalPoolStatus;

	public String getEvalPoolStatus() {
		return evalPoolStatus;
	}

	public void setEvalPoolStatus(String evalPoolStatus) {
		this.evalPoolStatus = evalPoolStatus;
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
	 * @return the numberOfAwards
	 */
	public Integer getNumberOfAwards()
	{
		return numberOfAwards;
	}

	/**
	 * @param numberOfAwards the numberOfAwards to set
	 */
	public void setNumberOfAwards(Integer numberOfAwards)
	{
		this.numberOfAwards = numberOfAwards;
	}

	/**
	 * @return the totalAwardAmount
	 */
	public String getTotalAwardAmount()
	{
		return totalAwardAmount;
	}

	/**
	 * @param totalAwardAmount the totalAwardAmount to set
	 */
	public void setTotalAwardAmount(String totalAwardAmount)
	{
		this.totalAwardAmount = totalAwardAmount;
	}

	public AwardsContractSummaryBean()
	{
		setFirstSort(HHSConstants.COMP_POOL_TITLE);
		setSecondSort(HHSConstants.AWARD_COUNT);
		setFirstSortType(HHSConstants.ASCENDING);
		setSecondSortType(HHSConstants.DESCENDING);
		setSecondSortDate(true);
		setSortColumnName(HHSConstants.COMPETITION_POOL_TITLE);
	}

	@Override
	public String toString()
	{
		return "AwardsContractSummaryBean [competitionPoolId=" + competitionPoolId + ", competitionPoolTitle="
				+ competitionPoolTitle + ", evaluationGroupId=" + evaluationGroupId + ", evaluationPoolMappingId="
				+ evaluationPoolMappingId + ", procurementId=" + procurementId + ", numberOfAwards=" + numberOfAwards
				+ ", totalAwardAmount=" + totalAwardAmount + "]";
	}
}
