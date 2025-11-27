package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

/**
 * Added class in R7 for Auto Approval configuration screen
 *
 */
public class AutoApprovalConfigBean
{
	//@RegExp(value ="^\\d{0,22}")
	//AUTO_APP_CONFIG_PCT_TH_ID
	private String id=HHSConstants.ONE;
	private int autoapprovalConfigId;
	@Length(max = 20)
	private String agencyId;
	//@RegExp(value ="^\\d{0,22}")
	private int thresholdPercentage;
	@Length(max = 20)
	private String organizationId;
	private String createdDate;
	private String createdByUserId;
	private String modifiedDate;
	private String modifiedByUserId;
	private String modifiedByUserName;
	private int versionId;
	private int reviewProcessId;
	private String empPosition = HHSConstants.EMPTY_STRING;
	private String organizationName;
	
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getModifiedByUserName()
	{
		return modifiedByUserName;
	}
	public void setModifiedbByUserName(String modifiedByUserName)
	{
		this.modifiedByUserName = modifiedByUserName;
	}
	public String getOrganizationName()
	{
		return organizationName;
	}
	public void setOrganizationName(String organizationName)
	{
		this.organizationName = organizationName;
	}
	public String getEmpPosition()
	{
		return empPosition;
	}
	public void setEmpPosition(String empPosition)
	{
		this.empPosition = empPosition;
	}
	public int getAutoapprovalConfigId()
	{
		return autoapprovalConfigId;
	}
	public void setAutoapprovalConfigId(int autoapprovalConfigId)
	{
		this.autoapprovalConfigId = autoapprovalConfigId;
	}
	public String getAgencyId()
	{
		return agencyId;
	}
	public void setAgencyId(String agencyId)
	{
		this.agencyId = agencyId;
	}
	public int getThresholdPercentage()
	{
		return thresholdPercentage;
	}
	public void setThresholdPercentage(int thresholdPercentage)
	{
		this.thresholdPercentage = thresholdPercentage;
	}
	public String getOrganizationId()
	{
		return organizationId;
	}
	public void setOrganizationId(String organizationId)
	{
		this.organizationId = organizationId;
	}
	public String getCreatedDate()
	{
		return createdDate;
	}
	public void setCreatedDate(String createdDate)
	{
		this.createdDate = createdDate;
	}
	public String getCreatedByUserId()
	{
		return createdByUserId;
	}
	public void setCreatedByUserId(String createdByUserId)
	{
		this.createdByUserId = createdByUserId;
	}
	public String getModifiedDate()
	{
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate)
	{
		this.modifiedDate = modifiedDate;
	}
	public String getModifiedByUserId()
	{
		return modifiedByUserId;
	}
	public void setModifiedByUserId(String modifiedByUserId)
	{
		this.modifiedByUserId = modifiedByUserId;
	}
	public int getVersionId()
	{
		return versionId;
	}
	public void setVersionId(int versionId)
	{
		this.versionId = versionId;
	}
	public int getReviewProcessId()
	{
		return reviewProcessId;
	}
	public void setReviewProcessId(int reviewProcessId)
	{
		this.reviewProcessId = reviewProcessId;
	}
	@Override
	public String toString()
	{
		return "AutoApprovalConfigBean [autoapprovalConfigId=" + autoapprovalConfigId + ", agencyId=" + agencyId
		+ ", thresholdPercentage=" + thresholdPercentage + ", organizationId=" + organizationId + ", createdDate="
		+ createdDate + ", createdByUserId=" + createdByUserId + ", modifiedDate=" + modifiedDate
		+ ", modifiedByUserId=" + modifiedByUserId + ", versionId=" + versionId
		+ ", reviewProcessId=" + reviewProcessId + "]";
	}
	

}
