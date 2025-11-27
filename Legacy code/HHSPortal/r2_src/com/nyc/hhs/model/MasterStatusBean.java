package com.nyc.hhs.model;

import java.io.Serializable;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class MasterStatusBean implements Serializable
{
	@Length(max = 50)
	private String msStatus;
	@Length(max = 50)
	private String msProcessType;
	@RegExp(value ="^\\d{0,22}")
	private int msStatusId;

	/**
	 * @return the msStatus
	 */
	public String getStatus()
	{
		return msStatus;
	}

	/**
	 * @param msStatus the msStatus to set
	 */
	public void setStatus(String msStatus)
	{
		this.msStatus = msStatus;
	}

	/**
	 * @return the msProcessType
	 */
	public String getProcessType()
	{
		return msProcessType;
	}

	/**
	 * @param msProcessType the msProcessType to set
	 */
	public void setProcessType(String msProcessType)
	{
		this.msProcessType = msProcessType;
	}

	/**
	 * @return the msStatusId
	 */
	public int getStatusId()
	{
		return msStatusId;
	}

	/**
	 * @param msStatusId the msStatusId to set
	 */
	public void setStatusId(int msStatusId)
	{
		this.msStatusId = msStatusId;
	}

	@Override
	public String toString()
	{
		return "MasterStatusBean [msStatus=" + msStatus + ", msProcessType=" + msProcessType + ", msStatusId="
				+ msStatusId + "]";
	}

}
