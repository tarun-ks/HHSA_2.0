package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the work-flow information in database.
 * 
 */

public class ApplicationInsertWorkFlow
{
	@Length(max = 50) 
	private String msWobNumber;
	@Length(max = 20)
	private String msApplicationID;
	private String msStatus;


	public String getWobNumber()
	{
		return msWobNumber;
	}

	public void setWobNumber(String asWobNumber)
	{
		this.msWobNumber = asWobNumber;
	}

	public String getApplicationID()
	{
		return msApplicationID;
	}

	public void setApplicationID(String asApplicationID)
	{
		this.msApplicationID = asApplicationID;
	}

	public String getStatus()
	{
		return msStatus;
	}

	public void setStatus(String asStatus)
	{
		this.msStatus = asStatus;
	}

	@Override
	public String toString() {
		return "ApplicationInsertWorkFlow [msWobNumber=" + msWobNumber
				+ ", msApplicationID=" + msApplicationID + ", msStatus="
				+ msStatus + "]";
	}

}
