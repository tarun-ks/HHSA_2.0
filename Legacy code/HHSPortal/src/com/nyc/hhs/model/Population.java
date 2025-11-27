package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/**
 * This class is a bean which maintains the population information.
 *
 */

public class Population {

	@Length(max = 50)
	private String msElementid;
	@RegExp(value ="^\\d{0,22}")
	private String msAgeFrom;
	@RegExp(value ="^\\d{0,22}")
	private String msAgeTo;
	private String msOrganizationid;
	private String msOther;
	private String msName;
	public String msUserId;
	
	public String getMsUserId()
	{
		return msUserId;
	}
	public void setMsUserId(String msUserId)
	{
		this.msUserId = msUserId;
	}
	public String getMsName() {
		return msName;
	}
	public void setMsName(String msName) {
		this.msName = msName;
	}
	public String getMsOther() {
		return msOther;
	}
	public void setMsOther(String msOther) {
		this.msOther = msOther;
	}
	public String getMsElementid() {
		return msElementid;
	}
	public void setMsElementid(String msElementid) {
		this.msElementid = msElementid;
	}
	public String getMsAgeFrom() {
		return msAgeFrom;
	}
	public void setMsAgeFrom(String msAgeFrom) {
		this.msAgeFrom = msAgeFrom;
	}
	public String getMsAgeTo() {
		return msAgeTo;
	}
	public void setMsAgeTo(String msAgeTo) {
		this.msAgeTo = msAgeTo;
	}
	public String getMsOrganizationid() {
		return msOrganizationid;
	}
	public void setMsOrganizationid(String msOrganizationid) {
		this.msOrganizationid = msOrganizationid;
	}
	@Override
	public String toString() {
		return "Population [msElementid=" + msElementid + ", msAgeFrom="
				+ msAgeFrom + ", msAgeTo=" + msAgeTo + ", msOrganizationid="
				+ msOrganizationid + ", msOther=" + msOther + ", msName="
				+ msName + ", msUserId=" + msUserId + "]";
	}
}
