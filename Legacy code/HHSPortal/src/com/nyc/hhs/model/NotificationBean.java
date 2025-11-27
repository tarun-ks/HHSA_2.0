/**
 * 
 */
package com.nyc.hhs.model;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/**
 * This class is a bean which maintains the Notification information.
 * 
 */

public class NotificationBean
{
	private String msNotificationType;
	private String msSubject;
	private String msMessageBody;
	private String msUserId;
	private String msGroupName;
	private String msDeleteNotification;
	private String msReadNotification;
	private Date msNotificationDate;
	private String msProviderId;
	private String msGeneralized;
	private String msAlertType;
	@RegExp(value ="^\\d{0,22}")
	private int msGroupNotificationId;
	private int msNotificationId;
	private String msProviderName;
	private String msEmailNotificationId;
	private String entityType;
	private String entityId;
	private String eventId;
	private String notificationAlertId;
	private String moduleName;
	private String orgType;
	private String notificationSent;
	private String createdBy;
	private String modifiedBy;
	private HashMap<String,String> linkMap;
	private HashMap<String,String> paramMap;
	private ArrayList<String> fiscalYear ;
	public String unRecoupAmount;
	private String notificationCount;
	private String totalCount;

	public String getUnRecoupAmount() {
		return unRecoupAmount;
	}

	public void setUnRecoupAmount(String unRecoupAmount) {
		this.unRecoupAmount = unRecoupAmount;
	}

	/**
	 * @return the msNotificationType
	 */
	public String getNotificationType()
	{
		return msNotificationType;
	}

	/**
	 * @param msNotificationType
	 *            the msNotificationType to set
	 */
	public void setNotificationType(String asNotificationType)
	{
		this.msNotificationType = asNotificationType;
	}

	/**
	 * @return the msSubject
	 */
	public String getSubject()
	{
		return msSubject;
	}

	/**
	 * @param msSubject
	 *            the msSubject to set
	 */
	public void setSubject(String asSubject)
	{
		this.msSubject = asSubject;
	}

	/**
	 * @return the msMessageBody
	 */
	public String getMessageBody()
	{
		return msMessageBody;
	}

	/**
	 * @param msMessageBody
	 *            the msMessageBody to set
	 */
	public void setMessageBody(String asMessageBody)
	{
		this.msMessageBody = asMessageBody;
	}

	/**
	 * @return the msUserId
	 */
	public String getUserId()
	{
		return msUserId;
	}

	/**
	 * @param msUserId
	 *            the msUserId to set
	 */
	public void setUserId(String asUserId)
	{
		this.msUserId = asUserId;
	}

	/**
	 * @return the msGroupName
	 */
	public String getGroupName()
	{
		return msGroupName;
	}

	/**
	 * @param msGroupName
	 *            the msGroupName to set
	 */
	public void setGroupName(String asGroupName)
	{
		this.msGroupName = asGroupName;
	}

	/**
	 * @return the msDeleteNotification
	 */
	public String getDeleteNotification()
	{
		return msDeleteNotification;
	}

	/**
	 * @param msDeleteNotification
	 *            the msDeleteNotification to set
	 */
	public void setDeleteNotification(String asDeleteNotification)
	{
		this.msDeleteNotification = asDeleteNotification;
	}

	/**
	 * @return the msReadNotification
	 */
	public String getReadNotification()
	{
		return msReadNotification;
	}

	/**
	 * @param msReadNotification
	 *            the msReadNotification to set
	 */
	public void setReadNotification(String asReadNotification)
	{
		this.msReadNotification = asReadNotification;
	}

	/**
	 * @return the moNotificationDate
	 */
	public Date getNotificationDate()
	{
		return msNotificationDate;
	}

	/**
	 * @param moNotificationDate
	 *            the moNotificationDate to set
	 */
	public void setNotificationDate(Date asNotificationDate)
	{
		this.msNotificationDate = asNotificationDate;
	}

	/**
	 * @return the msProviderId
	 */
	public String getProviderId()
	{
		return msProviderId;
	}

	/**
	 * @param msProviderId
	 *            the msProviderId to set
	 */
	public void setProviderId(String asProviderId)
	{
		this.msProviderId = asProviderId;
	}

	/**
	 * @return the msGeneralized
	 */
	public String getGeneralized()
	{
		return msGeneralized;
	}

	/**
	 * @param msGeneralized
	 *            the msGeneralized to set
	 */
	public void setGeneralized(String asGeneralized)
	{
		this.msGeneralized = asGeneralized;
	}

	/**
	 * @return the msAlertType
	 */
	public String getAlertType()
	{
		return msAlertType;
	}

	/**
	 * @param msAlertType
	 *            the msAlertType to set
	 */
	public void setAlertType(String asAlertType)
	{
		this.msAlertType = asAlertType;
	}

	/**
	 * @return the msGroupNotificationId
	 */
	public int getGroupNotificationId()
	{
		return msGroupNotificationId;
	}

	/**
	 * @param msGroupNotificationId
	 *            the msGroupNotificationId to set
	 */
	public void setGroupNotificationId(int asGroupNotificationId)
	{
		this.msGroupNotificationId = asGroupNotificationId;
	}

	/**
	 * @return the msNotificationId
	 */
	public int getNotificationId()
	{
		return msNotificationId;
	}

	/**
	 * @param msNotificationId
	 *            the msNotificationId to set
	 */
	public void setNotificationId(int asNotificationId)
	{
		this.msNotificationId = asNotificationId;
	}

	/**
	 * @return the msProviderName
	 */
	public String getProviderName()
	{
		return msProviderName;
	}

	/**
	 * @param msProviderName
	 *            the msProviderName to set
	 */
	public void setProviderName(String asProviderName)
	{
		this.msProviderName = asProviderName;
	}

	/**
	 * @return the msEmailNotificationId
	 */
	public String getEmailNotificationId()
	{
		return msEmailNotificationId;
	}

	/**
	 * @param msEmailNotificationId
	 *            the msEmailNotificationId to set
	 */
	public void setEmailNotificationId(String asEmailNotificationId)
	{
		this.msEmailNotificationId = asEmailNotificationId;
	}

	/**
	 * @return the entityType
	 */
	public String getEntityType()
	{
		return entityType;
	}

	/**
	 * @param entityType the entityType to set
	 */
	public void setEntityType(String entityType)
	{
		this.entityType = entityType;
	}

	/**
	 * @return the entityId
	 */
	public String getEntityId()
	{
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId)
	{
		this.entityId = entityId;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId()
	{
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId)
	{
		this.eventId = eventId;
	}
	

	/**
	 * @return the notificationAlertId
	 */
	public String getNotificationAlertId()
	{
		return notificationAlertId;
	}

	/**
	 * @param notificationAlertId the notificationAlertId to set
	 */
	public void setNotificationAlertId(String notificationAlertId)
	{
		this.notificationAlertId = notificationAlertId;
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
	 * @return the notificationSent
	 */
	public String getNotificationSent()
	{
		return notificationSent;
	}

	/**
	 * @param notificationSent the notificationSent to set
	 */
	public void setNotificationSent(String notificationSent)
	{
		this.notificationSent = notificationSent;
	}
	


	/**
	 * @return the createdBy
	 */
	public String getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}
	
	

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy()
	{
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the linkMap
	 */
	public HashMap<String, String> getLinkMap()
	{
		return linkMap;
	}

	/**
	 * @param linkMap the linkMap to set
	 */
	public void setLinkMap(HashMap<String, String> linkMap)
	{
		this.linkMap = linkMap;
	}
	

	/**
	 * @return the paramMap
	 */
	public HashMap<String, String> getParamMap()
	{
		return paramMap;
	}

	/**
	 * @param paramMap the paramMap to set
	 */
	public void setParamMap(HashMap<String, String> paramMap)
	{
		this.paramMap = paramMap;
	}

	@Override
	public String toString() {
		return "NotificationBean [msNotificationType=" + msNotificationType
				+ ", msSubject=" + msSubject + ", msMessageBody="
				+ msMessageBody + ", msUserId=" + msUserId + ", msGroupName="
				+ msGroupName + ", msDeleteNotification="
				+ msDeleteNotification + ", msReadNotification="
				+ msReadNotification + ", msNotificationDate="
				+ msNotificationDate + ", msProviderId=" + msProviderId
				+ ", msGeneralized=" + msGeneralized + ", msAlertType="
				+ msAlertType + ", msGroupNotificationId="
				+ msGroupNotificationId + ", msNotificationId="
				+ msNotificationId + ", msProviderName=" + msProviderName
				+ ", msEmailNotificationId=" + msEmailNotificationId
				+ ", entityType=" + entityType + ", entityId=" + entityId
				+ ", eventId=" + eventId + "]";
	}

	public ArrayList<String> getFiscalYear() {
		return fiscalYear;
	}

	public void setFiscalYear(ArrayList<String> fiscalYear) {
		this.fiscalYear = fiscalYear;
	}

	public String getNotificationCount()
	{
		return notificationCount;
	}

	public void setNotificationCount(String notificationCount)
	{
		this.notificationCount = notificationCount;
	}

	public String getTotalCount()
	{
		return totalCount;
	}

	public void setTotalCount(String totalCount)
	{
		this.totalCount = totalCount;
	}
	
	
}
