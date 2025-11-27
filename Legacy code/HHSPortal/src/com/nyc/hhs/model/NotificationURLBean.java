package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class NotificationURLBean
{

	private String url;
	@RegExp(value ="^\\d{0,22}")
	private String notificationUrlId;
	private String organizationId;
	@RegExp(value ="^\\d{0,22}")
	private String groupNotificationId;
	private String orgType;
	/**
	 * @return the url
	 */
	public String getUrl()
	{
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public void setUrl(String url)
	{
		this.url = url;
	}
	/**
	 * @return the notificationUrlId
	 */
	public String getNotificationUrlId()
	{
		return notificationUrlId;
	}
	/**
	 * @param notificationUrlId the notificationUrlId to set
	 */
	public void setNotificationUrlId(String notificationUrlId)
	{
		this.notificationUrlId = notificationUrlId;
	}
	/**
	 * @return the organizationId
	 */
	public String getOrganizationId()
	{
		return organizationId;
	}
	/**
	 * @param organizationId the organizationId to set
	 */
	public void setOrganizationId(String organizationId)
	{
		this.organizationId = organizationId;
	}
	/**
	 * @return the groupNotificationId
	 */
	public String getGroupNotificationId()
	{
		return groupNotificationId;
	}
	/**
	 * @param groupNotificationId the groupNotificationId to set
	 */
	public void setGroupNotificationId(String groupNotificationId)
	{
		this.groupNotificationId = groupNotificationId;
	}
	/**
	 * @return the orgType
	 */
	public String getOrgType()
	{
		return orgType;
	}
	/**
	 * @param orgType the orgType to set
	 */
	public void setOrgType(String orgType)
	{
		this.orgType = orgType;
	}
	
	
}
