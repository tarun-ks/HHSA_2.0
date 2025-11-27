/**
 * 
 */
package com.nyc.hhs.model;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/**
 * This class will store the param name and values for the
 * corresponding group notification id
 *
 */
public class NotificationParamBean
{

	private String notificationParamId;
	@RegExp(value ="^\\d{0,22}")
	private String groupNotificationId;
	private String paramName;
	private String paramValue;
	/**
	 * @return the notificationParamId
	 */
	public String getNotificationParamId()
	{
		return notificationParamId;
	}
	/**
	 * @param notificationParamId the notificationParamId to set
	 */
	public void setNotificationParamId(String notificationParamId)
	{
		this.notificationParamId = notificationParamId;
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
	 * @return the paramName
	 */
	public String getParamName()
	{
		return paramName;
	}
	/**
	 * @param paramName the paramName to set
	 */
	public void setParamName(String paramName)
	{
		this.paramName = paramName;
	}
	/**
	 * @return the paramValue
	 */
	public String getParamValue()
	{
		return paramValue;
	}
	/**
	 * @param paramValue the paramValue to set
	 */
	public void setParamValue(String paramValue)
	{
		this.paramValue = paramValue;
	}
	
	
}
