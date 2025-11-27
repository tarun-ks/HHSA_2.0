package com.nyc.hhs.model;

import java.util.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * This class is a bean which maintains the Faq Form Master information.
 *
 */

public class FaqFormMasterBean {

	@RegExp(value ="^\\d{0,3}")
	private int miTopicId;
	@Length(max = 60)
	private String msTopicName;
	@Length(max = 20)
	private String msType;
	private String msCreatedBy;
	private String msModifiedBy;
	private Date moModifiedDate;
	
	public int getMiTopicId() {
		return miTopicId;
	}
	
	public void setMiTopicId(int miTopicId) {
		this.miTopicId = miTopicId;
	}
	
	public String getMsTopicName() {
		return msTopicName;
	}
	
	public void setMsTopicName(String msTopicName) {
		this.msTopicName = msTopicName;
	}
	
	public String getMsType() {
		return msType;
	}
	
	public void setMsType(String msType) {
		this.msType = msType;
	}
	
	public String getMsCreatedBy() {
		return msCreatedBy;
	}
	
	public void setMsCreatedBy(String msCreatedBy) {
		this.msCreatedBy = msCreatedBy;
	}

	public String getMsModifiedBy() {
		return msModifiedBy;
	}
	
	public void setMsModifiedBy(String msModifiedBy) {
		this.msModifiedBy = msModifiedBy;
	}
	
	public Date getMoModifiedDate() {
		return moModifiedDate;
	}

	public void setMoModifiedDate(Date moModifiedDate) {
		this.moModifiedDate = moModifiedDate;
	}

	@Override
	public String toString() {
		return "FaqFormMasterBean [miTopicId=" + miTopicId + ", msTopicName="
				+ msTopicName + ", msType=" + msType + ", msCreatedBy="
				+ msCreatedBy + ", msModifiedBy=" + msModifiedBy
				+ ", moModifiedDate=" + moModifiedDate + "]";
	}
}
