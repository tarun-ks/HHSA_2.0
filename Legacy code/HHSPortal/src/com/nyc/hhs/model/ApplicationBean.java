package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * This class will be used to maintain Application information which includes
 * Application Id, User Id, Updated By and Updated Date.
 * 
 */

public class ApplicationBean
{
	@Length(max = 20) 
	private String lsAppId;
	private String lsUserId;	
	private String lsUpdatedBy;
	private String lsUpdatedDate;
	

	public String getAppId()
	{
		return lsAppId;
	}

	public void setAppId(String asAppId)
	{
		this.lsAppId = asAppId;
	}

	public String getUserId()
	{
		return lsUserId;
	}

	public void setUserId(String asUserId)
	{
		this.lsUserId = asUserId;
	}	
	public String getUpdatedBy()
	{
		return lsUpdatedBy;
	}

	public void setUpdatedBy(String asUpdatedBy)
	{
		this.lsUpdatedBy = asUpdatedBy;
	}

	public String getUpdatedDate()
	{
		return lsUpdatedDate;
	}

	public void setUpdatedDate(String updatedDate)
	{
		this.lsUpdatedDate = updatedDate;
	}

	@Override
	public String toString() {
		return "ApplicationBean [lsAppId=" + lsAppId + ", lsUserId=" + lsUserId
				+ ", lsUpdatedBy=" + lsUpdatedBy + ", lsUpdatedDate="
				+ lsUpdatedDate + "]";
	}
}




