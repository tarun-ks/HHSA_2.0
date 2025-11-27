package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This class is a bean which maintains the User information.
 * 
 */

public class UserBean
{

	@Length(max = 20)
	private String msUserId;
	private String msLoginId;
	private String msUserName;
	private String msOrgType;
	private String msOrgId;
	private String msRole;
	private String msPassword;
	private String msOrgName;
	private String msUserDN;
	private String msUserEmail;
	// R4
	private String msPermissionType;
	private String msPermissionLevel;

	public String getMsPermissionLevel()
	{
		return msPermissionLevel;
	}

	public void setMsPermissionLevel(String msPermissionLevel)
	{
		this.msPermissionLevel = msPermissionLevel;
	}

	public String getMsPermissionType()
	{
		return msPermissionType;
	}

	public void setMsPermissionType(String msPermissionType)
	{
		this.msPermissionType = msPermissionType;
	}

	public String getMsUserEmail()
	{
		return msUserEmail;
	}

	public void setMsUserEmail(String msUserEmail)
	{
		this.msUserEmail = msUserEmail;
	}

	public String getMsUserId()
	{
		return msUserId;
	}

	public void setMsUserId(String msUserId)
	{
		this.msUserId = msUserId;
	}

	public String getMsUserName()
	{
		return msUserName;
	}

	public void setMsUserName(String msUserName)
	{
		this.msUserName = msUserName;
	}

	public String getMsOrgType()
	{
		return msOrgType;
	}

	public void setMsOrgType(String msOrgType)
	{
		this.msOrgType = msOrgType;
	}

	public String getMsOrgId()
	{
		return msOrgId;
	}

	public void setMsOrgId(String msOrgId)
	{
		this.msOrgId = msOrgId;
	}

	public String getMsRole()
	{
		return msRole;
	}

	public void setMsRole(String msRole)
	{
		this.msRole = msRole;
	}

	public String getMsPassword()
	{
		return msPassword;
	}

	public void setMsPassword(String msPassword)
	{
		this.msPassword = msPassword;
	}

	public String getMsLoginId()
	{
		return msLoginId;
	}

	public void setMsLoginId(String msLoginId)
	{
		this.msLoginId = msLoginId;
	}

	public final String getMsOrgName()
	{
		return msOrgName;
	}

	public final void setMsOrgName(String msOrgName)
	{
		this.msOrgName = msOrgName;
	}

	public String getMsUserDN()
	{
		return msUserDN;
	}

	public void setMsUserDN(String msUserDN)
	{
		this.msUserDN = msUserDN;
	}

	@Override
	public String toString()
	{
		return "UserBean [msUserId=" + msUserId + ", msLoginId=" + msLoginId + ", msUserName=" + msUserName
				+ ", msOrgType=" + msOrgType + ", msOrgId=" + msOrgId + ", msRole=" + msRole + ", msPassword="
				+ msPassword + ", msOrgName=" + msOrgName + ", msUserDN=" + msUserDN + ", msUserEmail=" + msUserEmail
				+ ", msPermissionType=" + msPermissionType + ", msPermissionLevel=" + msPermissionLevel + "]";
	}

}
