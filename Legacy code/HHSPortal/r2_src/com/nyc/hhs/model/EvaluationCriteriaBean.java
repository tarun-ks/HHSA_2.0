/**
 * 
 */
package com.nyc.hhs.model;

import java.util.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * @author arun.k.arora
 * 
 */
public class EvaluationCriteriaBean
{
	
	private String scoreFlag;
	private String scoreCriteria;
	private String prevScoreCriteria;
	@RegExp(value ="^\\d{0,22}")
	private Integer maximumScore;
	@RegExp(value ="^\\d{0,22}")
	private Integer scoreSeqNumber;
	private String createdByUserId;
	private String modifiedByUserId;
	@RegExp(value ="^\\d{0,22}")
	private String procurementId;
	private Date lastModifiedDate;
	private Date createdDate;

	/**
	 * @return the prevScoreCriteria
	 */
	public String getPrevScoreCriteria()
	{
		return prevScoreCriteria;
	}

	/**
	 * @param prevScoreCriteria the prevScoreCriteria to set
	 */
	public void setPrevScoreCriteria(String prevScoreCriteria)
	{
		this.prevScoreCriteria = prevScoreCriteria;
	}

	/**
	 * @return the scoreFlag
	 */
	public String getScoreFlag()
	{
		return scoreFlag;
	}

	/**
	 * @param scoreFlag the scoreFlag to set
	 */
	public void setScoreFlag(String scoreFlag)
	{
		this.scoreFlag = scoreFlag;
	}

	/**
	 * @return the scoreCriteria
	 */
	public String getScoreCriteria()
	{
		return scoreCriteria;
	}

	/**
	 * @param scoreCriteria the scoreCriteria to set
	 */
	public void setScoreCriteria(String scoreCriteria)
	{
		this.scoreCriteria = scoreCriteria;
	}

	/**
	 * @return the maximumScore
	 */
	public Integer getMaximumScore()
	{
		return maximumScore;
	}

	/**
	 * @param maximumScore the maximumScore to set
	 */
	public void setMaximumScore(Integer maximumScore)
	{
		this.maximumScore = maximumScore;
	}

	/**
	 * @return the scoreSeqNumber
	 */
	public Integer getScoreSeqNumber()
	{
		return scoreSeqNumber;
	}

	/**
	 * @param scoreSeqNumber the scoreSeqNumber to set
	 */
	public void setScoreSeqNumber(Integer scoreSeqNumber)
	{
		this.scoreSeqNumber = scoreSeqNumber;
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
	 * @return the modifiedByUserId
	 */
	public String getModifiedByUserId()
	{
		return modifiedByUserId;
	}

	/**
	 * @param modifiedByUserId the modifiedByUserId to set
	 */
	public void setModifiedByUserId(String modifiedByUserId)
	{
		this.modifiedByUserId = modifiedByUserId;
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
	 * @return the lastModifiedDate
	 */
	public Date getLastModifiedDate()
	{
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(Date lastModifiedDate)
	{
		this.lastModifiedDate = lastModifiedDate;
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

	@Override
	public String toString()
	{
		return "EvaluationCriteriaBean [scoreFlag=" + scoreFlag + ", scoreCriteria=" + scoreCriteria
				+ ", prevScoreCriteria=" + prevScoreCriteria + ", maximumScore=" + maximumScore + ", scoreSeqNumber="
				+ scoreSeqNumber + ", createdByUserId=" + createdByUserId + ", modifiedByUserId=" + modifiedByUserId
				+ ", procurementId=" + procurementId + ", lastModifiedDate=" + lastModifiedDate + ", createdDate="
				+ createdDate + "]";
	}

}
