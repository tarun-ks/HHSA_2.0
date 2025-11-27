package com.nyc.hhs.model;

import java.util.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.DateUtil;

/**
 * This class is a bean which maintains the Faq Form information.
 * 
 */

public class CommentsHistoryBean
{

	@Length(max = 100)
	//EVENT_NAME
	private String action;
	private String detail;
	private String user;
	private String dateTime;
	@Length(max = 100)
	//EVENT_TYPE
	private String taskName;
	private Date auditDate;

	/**
	 * @return the action
	 */
	public String getAction()
	{
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action)
	{
		this.action = action;
	}

	/**
	 * @return the detail
	 */
	public String getDetail()
	{
		return detail;
	}

	/**
	 * @param detail the detail to set
	 */
	public void setDetail(String detail)
	{
		this.detail = detail;
	}

	/**
	 * @return the user
	 */
	public String getUser()
	{
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(String user)
	{
		this.user = user;
	}

	/**
	 * @return the dateTime
	 */
	public String getDateTime()
	{
		return dateTime;
	}

	/**
	 * @param dateTime the dateTime to set
	 */
	public void setDateTime(String dateTime)
	{
		this.dateTime = dateTime;
	}

	/**
	 * @return the taskName
	 */
	public String getTaskName()
	{
		return taskName;
	}

	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName)
	{
		this.taskName = taskName;
	}

	/**
	 * @return the auditDate
	 */
	public Date getAuditDate()
	{
		return auditDate;
	}

	/**
	 * @param auditDate the auditDate to set
	 * @throws ApplicationException
	 */
	public void setAuditDate(Date auditDate) throws ApplicationException
	{
		setDateTime(DateUtil.getDateMMddYYYYHHMMFormat((auditDate)));
		this.auditDate = auditDate;
	}

	@Override
	public String toString()
	{
		return "CommentsHistoryBean [action=" + action + ", detail=" + detail + ", user=" + user + ", dateTime="
				+ dateTime + ", taskName=" + taskName + ", auditDate=" + auditDate + "]";
	}
}
