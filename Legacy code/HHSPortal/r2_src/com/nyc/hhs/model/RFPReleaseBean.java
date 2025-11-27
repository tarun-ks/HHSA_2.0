package com.nyc.hhs.model;

import java.sql.Date;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Max;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Min;

public class RFPReleaseBean
{
	private String epinValue;
	private String elementId;
	private String procCof;
	private Integer reqDocTypeCount;
	private Integer reqDocCount;
	private Integer evaluationCriteriaCount;
	private Integer competitionPoolCount;
	private String organizationId;
	private String procurementId;
	private String lastPublishedByUser;
	private Date lastPublishedDate;
	private Date lastModifiedDate;
	private String releasedRfpVersion;
	private String createdByUserId;
	private String modifiedByUserId;
	private List<EvaluationCriteriaBean> loEvaluationCriteriaBeanList;
	@Max(value = 100)
	@Min(value = 100)
	private Integer totalMaxScore;
	private String nullCheck;
	private String evalGroupStatus;
	private Integer duplicatePoolCount;

	/**
	 * @return the nullCheck
	 */
	public String getNullCheck()
	{
		return nullCheck;
	}

	/**
	 * @param nullCheck the nullCheck to set
	 */
	public void setNullCheck(String nullCheck)
	{
		this.nullCheck = nullCheck;
	}

	/**
	 * @return the totalMaxScore
	 */
	public Integer getTotalMaxScore()
	{
		Integer totalScore = Integer.valueOf(0);
		if (null != loEvaluationCriteriaBeanList && !loEvaluationCriteriaBeanList.isEmpty())
		{
			totalScore = Integer.valueOf(0);
			for (EvaluationCriteriaBean loEvaluationCriteriaBean : loEvaluationCriteriaBeanList)
			{
				if (loEvaluationCriteriaBean != null && loEvaluationCriteriaBean.getMaximumScore() != null)
				{
					totalScore = totalScore + loEvaluationCriteriaBean.getMaximumScore();
				}
			}
			totalMaxScore = totalScore;
			if (totalScore == 0)
			{
				totalMaxScore = 100;
			}
		}
		return totalMaxScore;
	}

	/**
	 * @param totalMaxScore the totalMaxScore to set
	 */
	public void setTotalMaxScore(Integer totalMaxScore)
	{
		this.totalMaxScore = getTotalMaxScore();
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
	 * @return the loEvaluationCriteriaBeanList
	 */
	public List<EvaluationCriteriaBean> getLoEvaluationCriteriaBeanList()
	{
		return loEvaluationCriteriaBeanList;
	}

	/**
	 * @param loEvaluationCriteriaBeanList the loEvaluationCriteriaBeanList to
	 *            set
	 */
	public void setLoEvaluationCriteriaBeanList(List<EvaluationCriteriaBean> loEvaluationCriteriaBeanList)
	{
		this.loEvaluationCriteriaBeanList = loEvaluationCriteriaBeanList;
	}

	public Date getLastModifiedDate()
	{
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate)
	{
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getReleasedRfpVersion()
	{
		return releasedRfpVersion;
	}

	public void setReleasedRfpVersion(String releasedRfpVersion)
	{
		this.releasedRfpVersion = releasedRfpVersion;
	}

	public String getEpinValue()
	{
		return epinValue;
	}

	public void setEpinValue(String epinValue)
	{
		this.epinValue = epinValue;
	}

	public String getElementId()
	{
		return elementId;
	}

	public String getProcurementId()
	{
		return procurementId;
	}

	public void setProcurementId(String procurementId)
	{
		this.procurementId = procurementId;
	}

	public String getLastPublishedByUser()
	{
		return lastPublishedByUser;
	}

	public void setLastPublishedByUser(String lastPublishedByUser)
	{
		this.lastPublishedByUser = lastPublishedByUser;
	}

	public Date getLastPublishedDate()
	{
		return lastPublishedDate;
	}

	public void setLastPublishedDate(Date lastPublishedDate)
	{
		this.lastPublishedDate = lastPublishedDate;
	}

	public void setElementId(String elementId)
	{
		this.elementId = elementId;
	}

	public String getProcCof()
	{
		return procCof;
	}

	public void setProcCof(String procCof)
	{
		this.procCof = procCof;
	}

	public String getOrganizationId()
	{
		return organizationId;
	}

	public void setOrganizationId(String organizationId)
	{
		this.organizationId = organizationId;
	}

	/**
	 * @return the reqDocTypeCount
	 */
	public Integer getReqDocTypeCount()
	{
		return reqDocTypeCount;
	}

	/**
	 * @param reqDocTypeCount the reqDocTypeCount to set
	 */
	public void setReqDocTypeCount(Integer reqDocTypeCount)
	{
		this.reqDocTypeCount = reqDocTypeCount;
	}

	/**
	 * @return the reqDocCount
	 */
	public Integer getReqDocCount()
	{
		return reqDocCount;
	}

	/**
	 * @param reqDocCount the reqDocCount to set
	 */
	public void setReqDocCount(Integer reqDocCount)
	{
		this.reqDocCount = reqDocCount;
	}

	/**
	 * @return the evaluationCriteriaCount
	 */
	public Integer getEvaluationCriteriaCount()
	{
		return evaluationCriteriaCount;
	}

	/**
	 * @param evaluationCriteriaCount the evaluationCriteriaCount to set
	 */
	public void setEvaluationCriteriaCount(Integer evaluationCriteriaCount)
	{
		this.evaluationCriteriaCount = evaluationCriteriaCount;
	}

	/**
	 * <li>This method was added in R4</li>
	 * @return the competitionPoolCount
	 */
	public Integer getCompetitionPoolCount()
	{
		return competitionPoolCount;
	}

	/**
	 * <li>This method was added in R4</li>
	 * @param competitionPoolCount the competitionPoolCount to set
	 */
	public void setCompetitionPoolCount(Integer competitionPoolCount)
	{
		this.competitionPoolCount = competitionPoolCount;
	}

	/**
	 * <li>This method was added in R4</li>
	 * @return the evalGroupStatus
	 */
	public String getEvalGroupStatus()
	{
		return evalGroupStatus;
	}

	/**
	 * <li>This method was added in R4</li>
	 * @param evalGroupStatus the evalGroupStatus to set
	 */
	public void setEvalGroupStatus(String evalGroupStatus)
	{
		this.evalGroupStatus = evalGroupStatus;
	}

	/**
	 * @return the duplicatePoolCount
	 */
	public Integer getDuplicatePoolCount()
	{
		return duplicatePoolCount;
	}

	/**
	 * @param duplicatePoolCount the duplicatePoolCount to set
	 */
	public void setDuplicatePoolCount(Integer duplicatePoolCount)
	{
		this.duplicatePoolCount = duplicatePoolCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "RFPReleaseBean [epinValue=" + epinValue + ", elementId=" + elementId + ", procCof=" + procCof
				+ ", reqDocTypeCount=" + reqDocTypeCount + ", reqDocCount=" + reqDocCount
				+ ", evaluationCriteriaCount=" + evaluationCriteriaCount + ", competitionPoolCount="
				+ competitionPoolCount + ", organizationId=" + organizationId + ", procurementId=" + procurementId
				+ ", lastPublishedByUser=" + lastPublishedByUser + ", lastPublishedDate=" + lastPublishedDate
				+ ", lastModifiedDate=" + lastModifiedDate + ", releasedRfpVersion=" + releasedRfpVersion
				+ ", createdByUserId=" + createdByUserId + ", modifiedByUserId=" + modifiedByUserId
				+ ", loEvaluationCriteriaBeanList=" + loEvaluationCriteriaBeanList + ", totalMaxScore=" + totalMaxScore
				+ ", nullCheck=" + nullCheck + ", evalGroupStatus=" + evalGroupStatus + ", duplicatePoolCount="
				+ duplicatePoolCount + "]";
	}
}
