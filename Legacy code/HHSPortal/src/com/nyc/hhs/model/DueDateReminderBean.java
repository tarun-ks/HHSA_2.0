package com.nyc.hhs.model;

/**
 * This class is a bean which maintains the Due Date Reminder information.
 *
 */
 
public class DueDateReminderBean {
	
	private String lsProviderId;
	private String ldDueDate;
	private String lsNotificationName;
	
	public String getProviderId() {
		return lsProviderId;
	}
	public void setProviderId(String lsProviderId) {
		this.lsProviderId = lsProviderId;
	}
	public String getDueDate() {
		return ldDueDate;
	}
	public void setDueDate(String ldDueDate) {
		this.ldDueDate = ldDueDate;
	}
	public String getNotificationName() {
		return lsNotificationName;
	}
	public void setNotificationName(String lsNotificationName) {
		this.lsNotificationName = lsNotificationName;
	}
	@Override
	public String toString() {
		return "DueDateReminderBean [lsProviderId=" + lsProviderId
				+ ", ldDueDate=" + ldDueDate + ", lsNotificationName="
				+ lsNotificationName + "]";
	}
		

}
