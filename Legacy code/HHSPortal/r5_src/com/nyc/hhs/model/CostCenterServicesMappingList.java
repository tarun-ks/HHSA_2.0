package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * This bean is added in R7 for Cost Center. It maintains the relationship
 * among services and cost center.
 */
public class CostCenterServicesMappingList
{
	@RegExp(value ="^\\d{0,22}")
	private String costCenterServiceMappingId;
	private String serviceMasterId;
	private String costCenterMasterId;
	private String enabledServiceName;
	private String disabledServiceName;
	public String getCostCenterServiceMappingId()
	{
		return costCenterServiceMappingId;
	}
	public void setCostCenterServiceMappingId(String costCenterServiceMappingId)
	{
		this.costCenterServiceMappingId = costCenterServiceMappingId;
	}
	public String getServiceMasterId()
	{
		return serviceMasterId;
	}
	public void setServiceMasterId(String serviceMasterId)
	{
		this.serviceMasterId = serviceMasterId;
	}
	public String getCostCenterMasterId()
	{
		return costCenterMasterId;
	}
	public void setCostCenterMasterId(String costCenterMasterId)
	{
		this.costCenterMasterId = costCenterMasterId;
	}
	public String getEnabledServiceName()
	{
		return enabledServiceName;
	}
	public void setEnabledServiceName(String enabledServiceName)
	{
		this.enabledServiceName = enabledServiceName;
	}
	public String getDisabledServiceName()
	{
		return disabledServiceName;
	}
	public void setDisabledServiceName(String disabledServiceName)
	{
		this.disabledServiceName = disabledServiceName;
	}
	
}
