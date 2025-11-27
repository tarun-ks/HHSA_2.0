package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class StatusR3
{

	@RegExp(value ="^\\d{0,22}")
	private String statusId;
	@Length(max = 50)
	private String processType;
	//@RegExp(value ="^\\d{0,22}")
	private String statusProcessTypeId;
	@Length(max = 50)
	private String status;
	private String statusFilter;

	public String getStatusId()
	{
		return statusId;
	}

	public void setStatusId(String statusId)
	{
		this.statusId = statusId;
	}

	public String getProcessType()
	{
		return processType;
	}

	public void setProcessType(String processType)
	{
		this.processType = processType;
	}

	public String getStatusProcessTypeId()
	{
		return statusProcessTypeId;
	}

	public void setStatusProcessTypeId(String statusProcessTypeId)
	{
		this.statusProcessTypeId = statusProcessTypeId;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public void setStatusFilter(String statusFilter)
	{
		this.statusFilter = statusFilter;
	}

	public String getStatusFilter()
	{
		return statusFilter;
	}

	@Override
	public String toString()
	{
		return "StatusR3 [statusId=" + statusId + ", processType=" + processType + ", statusProcessTypeId="
				+ statusProcessTypeId + ", status=" + status + ", statusFilter=" + statusFilter + "]";
	}

}
