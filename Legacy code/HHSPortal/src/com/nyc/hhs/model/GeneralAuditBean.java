package com.nyc.hhs.model;

import java.util.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the General Audit information.
 *
 */

public class GeneralAuditBean {
	
	private String msOrgId;
	private String msEventname;
	private String msEventtype;
	private Date msAuditDate;
	@Length(max = 20)
	private String msUserid;
	private String msData;
	private String msEntityType;
	private String msEntityId;
	private String msStatus;
	private String msProviderVisibilityFlag;
	private String msUserName;
	
	public String getMsOrgId() {
		return msOrgId;
	}
	
	public void setMsOrgId(String msOrgId) {
		this.msOrgId = msOrgId;
	}
	
	public String getMsEventname() {
		return msEventname;
	}
	
	public void setMsEventname(String msEventname) {
		this.msEventname = msEventname;
	}

	public String getMsEventtype() {
		return msEventtype;
	}
	
	public void setMsEventtype(String msEventtype) {
		this.msEventtype = msEventtype;
	}
	
	public Date getMsAuditDate() {
		return msAuditDate;
	}
	
	public void setMsAuditDate(Date msAuditDate) {
		this.msAuditDate = msAuditDate;
	}
	
	public String getMsUserid() {
		return msUserid;
	}
	
	public void setMsUserid(String msUserid) {
		this.msUserid = msUserid;
	}
	
	public String getMsData() {
		return msData;
	}
	
	public void setMsData(String msData) {
		this.msData = msData;
	}
	
	public String getMsEntityType() {
		return msEntityType;
	}
	
	public void setMsEntityType(String msEntityType) {
		this.msEntityType = msEntityType;
	}
	
	public String getMsEntityId() {
		return msEntityId;
	}
	
	public void setMsEntityId(String msEntityId) {
		this.msEntityId = msEntityId;
	}
	
	public String getMsStatus() {
		return msStatus;
	}
	
	public void setMsStatus(String msStatus) {
		this.msStatus = msStatus;
	}
	
	public String getMsProviderVisibilityFlag() {
		return msProviderVisibilityFlag;
	}
	
	public void setMsProviderVisibilityFlag(String msProviderVisibilityFlag) {
		this.msProviderVisibilityFlag = msProviderVisibilityFlag;
	}

	/**
	 * @return the msUserName
	 */
	public String getMsUserName()
	{
		return msUserName;
	}

	/**
	 * @param msUserName the msUserName to set
	 */
	public void setMsUserName(String msUserName)
	{
		this.msUserName = msUserName;
	}

	@Override
	public String toString() {
		return "GeneralAuditBean [msOrgId=" + msOrgId + ", msEventname="
				+ msEventname + ", msEventtype=" + msEventtype
				+ ", msAuditDate=" + msAuditDate + ", msUserid=" + msUserid
				+ ", msData=" + msData + ", msEntityType=" + msEntityType
				+ ", msEntityId=" + msEntityId + ", msStatus=" + msStatus
				+ ", msProviderVisibilityFlag=" + msProviderVisibilityFlag
				+ ", msUserName=" + msUserName + "]";
	}
}
