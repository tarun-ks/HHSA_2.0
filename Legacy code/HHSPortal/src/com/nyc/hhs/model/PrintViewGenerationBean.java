package com.nyc.hhs.model;

import java.sql.Timestamp;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class PrintViewGenerationBean {
	
	@Length(max = 50)
	private String msPrintViewId;
	@Length(max = 20)
	private String msOrgId;
	@Length(max = 50)
	private String msTaskType;
	private Timestamp msModifiedDate;
	
	public Timestamp getMsModifiedDate() {
		return msModifiedDate;
	}

	public void setMsModifiedDate(Timestamp msModifiedDate) {
		this.msModifiedDate = msModifiedDate;
	}

	public String getPrintViewId() {
		return msPrintViewId;
	}
	
	public void setPrintViewId(String msPrintViewId) {
		this.msPrintViewId = msPrintViewId;
	}
	
	public String getOrgId() {
		return msOrgId;
	}
	
	public void setOrgId(String msOrgId) {
		this.msOrgId = msOrgId;
	}
	
	public String getTaskType() {
		return msTaskType;
	}
	
	public void setTaskType(String msTaskType) {
		this.msTaskType = msTaskType;
	}

	@Override
	public String toString() {
		return "PrintViewGenerationBean [msPrintViewId=" + msPrintViewId
				+ ", msOrgId=" + msOrgId + ", msTaskType=" + msTaskType
				+ ", msModifiedDate=" + msModifiedDate + "]";
	}
	
	
	

}
