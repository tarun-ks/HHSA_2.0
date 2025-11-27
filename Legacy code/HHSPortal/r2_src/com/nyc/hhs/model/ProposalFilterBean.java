/**
 * 
 */
package com.nyc.hhs.model;

import java.util.Date;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class ProposalFilterBean
{
	@RegExp(value ="^\\d{0,22}")
	private String procId;
	private Integer statusId;
	private String userId;
	private String userComment;
	private Date auditDate;
	@Length(max = 100)
	private String agencyName;

	public String getUserComment()
	{
		return userComment;
	}

	public void setUserComment(String userComment)
	{
		this.userComment = userComment;
	}

	public Date getAuditDate()
	{
		return auditDate;
	}

	public void setAuditDate(Date auditDate)
	{
		this.auditDate = auditDate;
	}

	public String getAgencyName()
	{
		return agencyName;
	}

	public void setAgencyName(String agencyName)
	{
		this.agencyName = agencyName;
	}

	public String getProcId()
	{
		return procId;
	}

	public void setProcId(String procId)
	{
		this.procId = procId;
	}

	public Integer getStatusId()
	{
		return statusId;
	}

	public void setStatusId(Integer statusId)
	{
		this.statusId = statusId;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	@Override
	public String toString()
	{
		return "ProposalFilterBean [procId=" + procId + ", statusId=" + statusId + ", userId=" + userId
				+ ", userComment=" + userComment + ", auditDate=" + auditDate + ", agencyName=" + agencyName + "]";
	}

}
