package com.nyc.hhs.model;

import java.util.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * This class is a bean which maintains Review_process table mapping.
 * 
 */

public class ReviewProcessBean
{

	// Review_process table mapping
	@RegExp(value ="^\\d{0,22}")
	//REVIEW_PROCESS_ID
	private int reviewProcessId;
	@Length(max = 55)
	//REVIEW_PROCESS
	private String reviewProcess;
	private String reviewProcessDescription;
	private String reviewProcessConfigFlag;
	private Date createdDate;
	private String createdByUserId;
	private Date modifiedDate;
	private String modifiedByUserId;

	// end

	/**
	 * @return the reviewProcessId
	 */
	public int getReviewProcessId()
	{
		return reviewProcessId;
	}

	/**
	 * @param reviewProcessId the reviewProcessId to set
	 */
	public void setReviewProcessId(int reviewProcessId)
	{
		this.reviewProcessId = reviewProcessId;
	}

	/**
	 * @return the reviewProcess
	 */
	public String getReviewProcess()
	{
		return reviewProcess;
	}

	/**
	 * @param reviewProcess the reviewProcess to set
	 */
	public void setReviewProcess(String reviewProcess)
	{
		this.reviewProcess = reviewProcess;
	}

	/**
	 * @return the reviewProcessDescription
	 */
	public String getReviewProcessDescription()
	{
		return reviewProcessDescription;
	}

	/**
	 * @param reviewProcessDescription the reviewProcessDescription to set
	 */
	public void setReviewProcessDescription(String reviewProcessDescription)
	{
		this.reviewProcessDescription = reviewProcessDescription;
	}

	/**
	 * @return the reviewProcessConfigFlag
	 */
	public String getReviewProcessConfigFlag()
	{
		return reviewProcessConfigFlag;
	}

	/**
	 * @param reviewProcessConfigFlag the reviewProcessConfigFlag to set
	 */
	public void setReviewProcessConfigFlag(String reviewProcessConfigFlag)
	{
		this.reviewProcessConfigFlag = reviewProcessConfigFlag;
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

	@Override
	public String toString()
	{
		return "ReviewProcessBean [reviewProcessId=" + reviewProcessId + ", reviewProcess=" + reviewProcess
				+ ", reviewProcessDescription=" + reviewProcessDescription + ", reviewProcessConfigFlag="
				+ reviewProcessConfigFlag + ", createdDate=" + createdDate + ", createdByUserId=" + createdByUserId
				+ ", modifiedDate=" + modifiedDate + ", modifiedByUserId=" + modifiedByUserId + "]";
	}

}