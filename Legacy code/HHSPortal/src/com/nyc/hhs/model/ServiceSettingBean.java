package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the Service Setting information.
 *
 */

public class ServiceSettingBean
{

	@Length(max = 20)
	private String msServiceAppId;
	@Length(max = 20)
	private String msOrgId;
	@Length(max = 50)
	private String msElementId;	
	private String msNoSettingFlag;
	private String msElementType;
	
	public String getServiceAppId()
	{
		return msServiceAppId;
	}
	public void setServiceAppId(String serviceAppId)
	{
		msServiceAppId = serviceAppId;
	}
	public String getOrgID()
	{
		return msOrgId;
	}
	public void setOrgID(String orgID)
	{
		msOrgId = orgID;
	}
	public String getElementId()
	{
		return msElementId;
	}
	public void setElementId(String elementId)
	{
		msElementId = elementId;
	}
	public String getNoSettingFlag()
	{
		return msNoSettingFlag;
	}
	public void setNoSettingFlag(String noSettingFlag)
	{
		msNoSettingFlag = noSettingFlag;
	}
	public String getMsElementType()
	{
		return msElementType;
	}
	public void setMsElementType(String msElementType)
	{
		this.msElementType = msElementType;
	}
	@Override
	public String toString() {
		return "ServiceSettingBean [msServiceAppId=" + msServiceAppId
				+ ", msOrgId=" + msOrgId + ", msElementId=" + msElementId
				+ ", msNoSettingFlag=" + msNoSettingFlag + ", msElementType="
				+ msElementType + "]";
	}
	
	
}
