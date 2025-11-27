package com.nyc.hhs.model;

import java.util.Date;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/**
 * This class is a bean which maintains the Faq Form Detail information.
 *
 */ 

public class FaqFormDetailBean {
	@RegExp(value ="^\\d{0,3}")
	private int miQuestionId;
	private String msQuestion;
	private String msAnswer;
	@RegExp(value ="^\\d{0,3}")
	private int miTopicId;
	private String msCreatedBy;
	private String msModifiedBy;
	private Date moModifiedDate;
	
	public int getMiQuestionId() {
		return miQuestionId;
	}
	
	public void setMiQuestionId(int miQuestionId) {
		this.miQuestionId = miQuestionId;
	}
	
	public String getMsQuestion() {
		return msQuestion;
	}
	
	public void setMsQuestion(String msQuestion) {
		this.msQuestion = msQuestion;
	}
	
	public String getMsAnswer() {
		return msAnswer;
	}
	
	public void setMsAnswer(String msAnswer) {
		this.msAnswer = msAnswer;
	}
	
	public int getMiTopicId() {
		return miTopicId;
	}
	
	public void setMiTopicId(int miTopicId) {
		this.miTopicId = miTopicId;
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
		return "FaqFormDetailBean [miQuestionId=" + miQuestionId
				+ ", msQuestion=" + msQuestion + ", msAnswer=" + msAnswer
				+ ", miTopicId=" + miTopicId + ", msCreatedBy=" + msCreatedBy
				+ ", msModifiedBy=" + msModifiedBy + ", moModifiedDate="
				+ moModifiedDate + "]";
	}
}

