/**
 * 
 */
package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * @author sumit.vasudeva
 *
 */
public class NotificationAlertMasterBean
{

	@Length(max = 20)
	private String notificationALertMasterId;
	private String subject;
	private String messageBody;
	@Length(max = 20)
	private String notificationALertId;
	private String generalized;
	private String alertType;
	private int noOfUrl;
	private String orgType;
	private String groupRole;
	private String userLevel;
	private String moduleName;
	private String preProcessingClass;
	private String preProcessingRequired;
	private String permissionType;
	/**
	 * @return the notificationALertMasterId
	 */
	public String getNotificationALertMasterId()
	{
		return notificationALertMasterId;
	}
	/**
	 * @param notificationALertMasterId the notificationALertMasterId to set
	 */
	public void setNotificationALertMasterId(String notificationALertMasterId)
	{
		this.notificationALertMasterId = notificationALertMasterId;
	}
	/**
	 * @return the subject
	 */
	public String getSubject()
	{
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject)
	{
		this.subject = subject;
	}
	/**
	 * @return the messageBody
	 */
	public String getMessageBody()
	{
		return messageBody;
	}
	/**
	 * @param messageBody the messageBody to set
	 */
	public void setMessageBody(String messageBody)
	{
		this.messageBody = messageBody;
	}
	/**
	 * @return the notificationALertId
	 */
	public String getNotificationALertId()
	{
		return notificationALertId;
	}
	/**
	 * @param notificationALertId the notificationALertId to set
	 */
	public void setNotificationALertId(String notificationALertId)
	{
		this.notificationALertId = notificationALertId;
	}
	/**
	 * @return the generalized
	 */
	public String getGeneralized()
	{
		return generalized;
	}
	/**
	 * @param generalized the generalized to set
	 */
	public void setGeneralized(String generalized)
	{
		this.generalized = generalized;
	}
	/**
	 * @return the alertType
	 */
	public String getAlertType()
	{
		return alertType;
	}
	/**
	 * @param alertType the alertType to set
	 */
	public void setAlertType(String alertType)
	{
		this.alertType = alertType;
	}
	/**
	 * @return the noOfUrl
	 */
	public int getNoOfUrl()
	{
		return noOfUrl;
	}
	/**
	 * @param noOfUrl the noOfUrl to set
	 */
	public void setNoOfUrl(int noOfUrl)
	{
		this.noOfUrl = noOfUrl;
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
	/**
	 * @return the groupRole
	 */
	public String getGroupRole()
	{
		return groupRole;
	}
	/**
	 * @param groupRole the groupRole to set
	 */
	public void setGroupRole(String groupRole)
	{
		this.groupRole = groupRole;
	}
	/**
	 * @return the userLevel
	 */
	public String getUserLevel()
	{
		return userLevel;
	}
	/**
	 * @param userLevel the userLevel to set
	 */
	public void setUserLevel(String userLevel)
	{
		this.userLevel = userLevel;
	}
	/**
	 * @return the moduleName
	 */
	public String getModuleName()
	{
		return moduleName;
	}
	/**
	 * @param moduleName the moduleName to set
	 */
	public void setModuleName(String moduleName)
	{
		this.moduleName = moduleName;
	}

	/**
	 * @return the preProcessingClass
	 */
	public String getPreProcessingClass()
	{
		return preProcessingClass;
	}
	/**
	 * @param preProcessingClass the preProcessingClass to set
	 */
	public void setPreProcessingClass(String preProcessingClass)
	{
		this.preProcessingClass = preProcessingClass;
	}
	/**
	 * @return the preProcessingRequired
	 */
	public String getPreProcessingRequired()
	{
		return preProcessingRequired;
	}
	/**
	 * @param preProcessingRequired the preProcessingRequired to set
	 */
	public void setPreProcessingRequired(String preProcessingRequired)
	{
		this.preProcessingRequired = preProcessingRequired;
	}
	/**
	 * @return the permissionType
	 */
	public String getPermissionType()
	{
		return permissionType;
	}
	/**
	 * @param permissionType the permissionType to set
	 */
	public void setPermissionType(String permissionType)
	{
		this.permissionType = permissionType;
	}
	
	
}
