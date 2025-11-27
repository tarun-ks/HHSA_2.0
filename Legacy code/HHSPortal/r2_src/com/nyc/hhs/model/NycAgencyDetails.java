package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class NycAgencyDetails
{

	@Length(max = 100)
	private String agencyName;
	@Length(max = 20)
	private String agencyId;

	public String getAgencyName()
	{
		return agencyName;
	}

	public void setAgencyName(String agencyName)
	{
		this.agencyName = agencyName;
	}

	public String getAgencyId()
	{
		return agencyId;
	}

	public void setAgencyId(String agencyId)
	{
		this.agencyId = agencyId;
	}

	@Override
	public String toString()
	{
		return "NycAgencyDetails [agencyName=" + agencyName + ", agencyId=" + agencyId + "]";
	}

}
