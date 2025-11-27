
package com.nyc.hhs.model;

import java.util.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the Doc Lapsing Rule information.
 *
 */ 

public class DocLapsingRuleBean {

	@Length(max = 20)
	private String msProviderId;
	private String msDocType;
	private Date mdDueDate;
	private int miNumDays;
	
	public int getNumDays() {
		return miNumDays;
	}
	public void setNumDays(int miNumDays) {
		this.miNumDays = miNumDays;
	}
	public String getProviderId() {
		return msProviderId;
	}
	public void setProviderId(String msProviderId) {
		this.msProviderId = msProviderId;
	}
	public String getDocType() {
		return msDocType;
	}
	public void setDocType(String msDocType) {
		this.msDocType = msDocType;
	}	
	public Date getDueDate() {
		return mdDueDate;
	}
	public void setDueDate(Date mdDueDate) {
		this.mdDueDate = mdDueDate;
	}
	@Override
	public String toString() {
		return "DocLapsingRuleBean [msProviderId=" + msProviderId
				+ ", msDocType=" + msDocType + ", mdDueDate=" + mdDueDate
				+ ", miNumDays=" + miNumDays + "]";
	}	
	
}
