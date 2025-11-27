package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class ProviderSelectionBean
{
	@Length(max = 20)
	String miProviderId;
	String miSelectionStatus;

	public String getMiProviderId()
	{
		return miProviderId;
	}

	public void setMiProviderId(String miProviderId)
	{
		this.miProviderId = miProviderId;
	}

	public String getMiSelectionStatus()
	{
		return miSelectionStatus;
	}

	public void setMiSelectionStatus(String miSelectionStatus)
	{
		this.miSelectionStatus = miSelectionStatus;
	}

	@Override
	public String toString()
	{
		return "ProviderSelectionBean [miProviderId=" + miProviderId + ", miSelectionStatus=" + miSelectionStatus + "]";
	}

}
