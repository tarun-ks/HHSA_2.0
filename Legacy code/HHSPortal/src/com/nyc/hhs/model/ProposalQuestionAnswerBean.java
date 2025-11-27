package com.nyc.hhs.model;

import java.sql.Timestamp;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

public class ProposalQuestionAnswerBean
{
	//@NotBlank
	private String questionSeqNo;
	private String procurementQnId;
	private String questionFlag;
	private String questionText;
	//@NotBlank
	//@Length(max = 250)
	@Length(max = 1000)
	private String answerText;
	private String createdBy;
	private String modifiedBy;
	private Timestamp createdDate;
	private Timestamp modifiedDate;
	private String procurementId;
	private String isAddendum;
	private String proposalId;
	private String versionNo;
	private String addendumId;

	/**
	 * @return the addendumId
	 */
	public String getAddendumId()
	{
		return addendumId;
	}

	/**
	 * @param addendumId the addendumId to set
	 */
	public void setAddendumId(String addendumId)
	{
		this.addendumId = addendumId;
	}

	/**
	 * @return the versionNo
	 */
	public String getVersionNo()
	{
		return versionNo;
	}

	/**
	 * @param versionNo the versionNo to set
	 */
	public void setVersionNo(String versionNo)
	{
		this.versionNo = versionNo;
	}

	/**
	 * @return the procurementQnId
	 */
	public String getProcurementQnId()
	{
		return procurementQnId;
	}

	/**
	 * @param procurementQnId the procurementQnId to set
	 */
	public void setProcurementQnId(String procurementQnId)
	{
		this.procurementQnId = procurementQnId;
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
	 * @return the questionSeqNo
	 */
	public String getQuestionSeqNo()
	{
		return questionSeqNo;
	}

	/**
	 * @param questionSeqNo the questionSeqNo to set
	 */
	public void setQuestionSeqNo(String questionSeqNo)
	{
		this.questionSeqNo = questionSeqNo;
	}

	/**
	 * @return the questionFlag
	 */
	public String getQuestionFlag()
	{
		return questionFlag;
	}

	/**
	 * @param questionFlag the questionFlag to set
	 */
	public void setQuestionFlag(String questionFlag)
	{
		this.questionFlag = questionFlag;
	}

	/**
	 * @return the questionText
	 */
	public String getQuestionText()
	{
		return questionText;
	}

	/**
	 * @param questionText the questionText to set
	 */
	public void setQuestionText(String questionText)
	{
		this.questionText = questionText;
	}

	/**
	 * @return the answerText
	 */
	public String getAnswerText()
	{
		return answerText;
	}

	/**
	 * @param answerText the answerText to set
	 */
	public void setAnswerText(String answerText)
	{
		this.answerText = answerText;
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
	 * @return the createdDate
	 */
	public Timestamp getCreatedDate()
	{
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Timestamp createdDate)
	{
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedDate
	 */
	public Timestamp getModifiedDate()
	{
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Timestamp modifiedDate)
	{
		this.modifiedDate = modifiedDate;
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
	 * @return the isAddendum
	 */
	public String getIsAddendum()
	{
		return isAddendum;
	}

	/**
	 * @param isAddendum the isAddendum to set
	 */
	public void setIsAddendum(String isAddendum)
	{
		this.isAddendum = isAddendum;
	}

	@Override
	public String toString()
	{
		return "ProposalQuestionAnswerBean [questionSeqNo=" + questionSeqNo + ", procurementQnId=" + procurementQnId
				+ ", questionFlag=" + questionFlag + ", questionText=" + questionText + ", answerText=" + answerText
				+ ", createdBy=" + createdBy + ", modifiedBy=" + modifiedBy + ", createdDate=" + createdDate
				+ ", modifiedDate=" + modifiedDate + ", procurementId=" + procurementId + ", isAddendum=" + isAddendum
				+ ", proposalId=" + proposalId + ", addendumId=" + addendumId + ", versionNo=" + versionNo + "]";
	}
}
