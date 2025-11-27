package com.nyc.hhs.model;

import java.util.Date;

import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.DateUtil;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This class is added in R6 for Return Payment
 * It is used to store multiple values and properties which are
 * used when displaying Notification History.
 */
public class ReturnPaymentNotification {
	
	private String sentDate;
	private String sentBy;
	private String sentTo;
	@Length(max = 50) 
	private String role;
	private Date notificationDate;
	
	public String getSentDate() {
		return sentDate;
	}
	public void setSentDate(String sentDate) {
		this.sentDate = sentDate;
	}
	public String getSentBy() {
		return sentBy;
	}
	public void setSentBy(String sentBy) {
		this.sentBy = sentBy;
	}
	public String getSentTo() {
		return sentTo;
	}
	public void setSentTo(String sentTo) {
		this.sentTo = sentTo;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public Date getNotificationDate()
	{
		return notificationDate;
	}
	public void setNotificationDate(Date notificationDate) throws ApplicationException
	{
		setSentDate(DateUtil.getDateMMddYYYYHHMMFormat((notificationDate)));
		this.notificationDate = notificationDate;
	}
	

}
