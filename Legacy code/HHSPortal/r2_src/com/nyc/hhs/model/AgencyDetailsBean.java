package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the NYC Agency information.
 * 
 */

public class AgencyDetailsBean
{
	@Length(max = 100)
	private String agencyName;
	@Length(max = 20)
	private String agencyId;

	/**
	 * @return the agencyName
	 */
	public String getAgencyName()
	{
		return agencyName;
	}

	/**
	 * @param agencyName the agencyName to set
	 */
	public void setAgencyName(String agencyName)
	{
		this.agencyName = agencyName;
	}

	/**
	 * @return the agencyId
	 */
	public String getAgencyId()
	{
		return agencyId;
	}

	/**
	 * @param agencyId the agencyId to set
	 */
	public void setAgencyId(String agencyId)
	{
		this.agencyId = agencyId;
	}

	@Override
	public String toString()
	{
		return "AgencyDetailsBean [agencyName=" + agencyName + ", agencyId=" + agencyId + "]";
	}

}
