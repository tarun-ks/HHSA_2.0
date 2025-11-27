package com.nyc.hhs.model;

import java.util.ArrayList;
import java.util.Date;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Digits;
//import javax.validation.constraints.Size;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/**
 * This class is a bean which maintains the Alert Inbox information which
 * includes Notification Id, Notification Type, Notification Name, Notification
 * Description User Id, Group Id, Notification Delete, Notification Read,
 * Notification Date, Alert Type List, Selected Alert Type, Filtered Modified
 * Date and From Filtered Modified Date.
 */

public class AlertInboxBean
{

	//@Digits(integer=22, fraction=0)
	@RegExp(value ="^\\d{0,22}")
	String msNotificationId;
	String msNotificationType;
	String msNotificationName;
	String msNotificationDesc;
	@Length(max = 20 ) 
	String msUserIds;
	String msGroupIds;
	String msNotificationDelete;
	String msNotificationRead;
	String msNotificationDate;
	ArrayList<String> msAlertTypeList;
	String msSelectedAlertType;
	Date moToFilterModifyDate;
	Date moFromFilterModifyDate;
	String msToFilterDate;
	String msFromFilterDate;
	// Added for R4
	String msPermissionType;
	String msPermissionLevel;

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

	public String getMsToFilterDate()
	{
		return msToFilterDate;
	}

	public void setMsToFilterDate(String msToFilterDate)
	{
		this.msToFilterDate = msToFilterDate;
	}

	public String getMsFromFilterDate()
	{
		return msFromFilterDate;
	}

	public void setMsFromFilterDate(String msFromFilterDate)
	{
		this.msFromFilterDate = msFromFilterDate;
	}

	public String getMsSelectedAlertType()
	{
		return msSelectedAlertType;
	}

	public void setMsSelectedAlertType(String asSelectedAlertType)
	{
		this.msSelectedAlertType = asSelectedAlertType;
	}

	public Date getMsToFilterModifyDate()
	{
		return moToFilterModifyDate;
	}

	public void setMsToFilterModifyDate(Date aoToFilterModifyDate)
	{
		this.moToFilterModifyDate = aoToFilterModifyDate;
	}

	public Date getMsFromFilterModifyDate()
	{
		return moFromFilterModifyDate;
	}

	public void setMsFromFilterModifyDate(Date aoFromFilterModifyDate)
	{
		this.moFromFilterModifyDate = aoFromFilterModifyDate;
	}

	public ArrayList<String> getMsAlertTypeList()
	{
		return msAlertTypeList;
	}

	public void setMsAlertTypeList(ArrayList<String> asAlertTypeList)
	{
		this.msAlertTypeList = asAlertTypeList;
	}

	public String getMsNotificationId()
	{
		return msNotificationId;
	}

	public void setMsNotificationId(String asNotificationId)
	{
		this.msNotificationId = asNotificationId;
	}

	public String getMsNotificationType()
	{
		return msNotificationType;
	}

	public void setMsNotificationType(String asNotificationType)
	{
		this.msNotificationType = asNotificationType;
	}

	public String getMsNotificationName()
	{
		return msNotificationName;
	}

	public void setMsNotificationName(String asNotificationName)
	{
		this.msNotificationName = asNotificationName;
	}

	public String getMsNotificationDesc()
	{
		return msNotificationDesc;
	}

	public void setMsNotificationDesc(String asNotificationDesc)
	{
		this.msNotificationDesc = asNotificationDesc;
	}

	public String getMsUserIds()
	{
		return msUserIds;
	}

	public void setMsUserIds(String asUserIds)
	{
		this.msUserIds = asUserIds;
	}

	public String getMsGroupIds()
	{
		return msGroupIds;
	}

	public void setMsGroupIds(String asGroupIds)
	{
		this.msGroupIds = asGroupIds;
	}

	public String getMsNotificationDelete()
	{
		return msNotificationDelete;
	}

	public void setMsNotificationDelete(String asNotificationDelete)
	{
		this.msNotificationDelete = asNotificationDelete;
	}

	public String getMsNotificationRead()
	{
		return msNotificationRead;
	}

	public void setMsNotificationRead(String asNotificationRead)
	{
		this.msNotificationRead = asNotificationRead;
	}

	public String getMsNotificationDate()
	{
		return msNotificationDate;
	}

	public void setMsNotificationDate(String asNotificationDate)
	{
		this.msNotificationDate = asNotificationDate;
	}

	@Override
	public String toString()
	{
		return "AlertInboxBean [msNotificationId=" + msNotificationId + ", msNotificationType=" + msNotificationType
				+ ", msNotificationName=" + msNotificationName + ", msNotificationDesc=" + msNotificationDesc
				+ ", msUserIds=" + msUserIds + ", msGroupIds=" + msGroupIds + ", msNotificationDelete="
				+ msNotificationDelete + ", msNotificationRead=" + msNotificationRead + ", msNotificationDate="
				+ msNotificationDate + ", msAlertTypeList=" + msAlertTypeList + ", msSelectedAlertType="
				+ msSelectedAlertType + ", moToFilterModifyDate=" + moToFilterModifyDate + ", moFromFilterModifyDate="
				+ moFromFilterModifyDate + ", msPermissionType=" + msPermissionType + ", msUserLevel=" + msPermissionLevel
				+ ", msToFilterDate=" + msToFilterDate + ", msFromFilterDate=" + msFromFilterDate + "]";
	}
	//Added for defect 8577
	String msEventId;

	public String getMsEventId()
	{
		return msEventId;
	}

	public void setMsEventId(String msEventId)
	{
		this.msEventId = msEventId;
	}
	//defect 8577 End
}
