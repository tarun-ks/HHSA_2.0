package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the Component Role Mapping information.
 *
 */

public class ComponentRoleMappingBean {

	@Length(max = 50) 
	private String msComponentId;
	@Length(max = 50)
	private String msRole;
	@Length(max = 20) //ORGANIZATION_TYPE
	private String msOrgType;	
	private String msDisplay;
	private String msPermissionType;
	
	public String getMsPermissionType()
	{
		return msPermissionType;
	}

	public void setMsPermissionType(String msRoleType)
	{
		this.msPermissionType = msRoleType;
	}

	public String getMsComponentId() {
		return msComponentId;
	}
	
	public void setMsComponentId(String msComponentId) {
		this.msComponentId = msComponentId;
	}
	
	public String getMsRole() {
		return msRole;
	}
	
	public void setMsRole(String msRole) {
		this.msRole = msRole;
	}
	
	public String getMsOrgType() {
		return msOrgType;
	}
	
	public void setMsOrgType(String msOrgType) {
		this.msOrgType = msOrgType;
	}
	
	public String getMsDisplay() {
		return msDisplay;
	}
	
	public void setMsDisplay(String msDisplay) {
		this.msDisplay = msDisplay;
	}

	@Override
	public String toString() {
		return "ComponentRoleMappingBean [msComponentId=" + msComponentId
				+ ", msRole=" + msRole + ", msOrgType=" + msOrgType
				+ ", msDisplay=" + msDisplay + "]";
	}


}
