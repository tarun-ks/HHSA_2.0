package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * This class is a bean which maintains the Faq Form information.
 *
 */

public class FaqFormBean {

	private String msQuestion;
	private String msAnswer;
	private String msCategory;
	@RegExp(value ="^\\d{0,3}")
	private int msQuestionId;
	@RegExp(value ="^\\d{0,3}")
	private int msTopicId;
	private String msTopic;
	private String msType;
	
	public final String getMsQuestion() {
		return msQuestion;
	}
	
	public final void setMsQuestion(String msQuestion) {
		this.msQuestion = msQuestion;
	}
	
	public final String getMsAnswer() {
		return msAnswer;
	}
	
	public final void setMsAnswer(String msAnswer) {
		this.msAnswer = msAnswer;
	}
	
	public final String getMsCategory() {
		return msCategory;
	}
	
	public final void setMsCategory(String msCategory) {
		this.msCategory = msCategory;
	}
	
	public final int getMsQuestionId() {
		return msQuestionId;
	}
	
	public final void setMsQuestionId(int msQuestionId) {
		this.msQuestionId = msQuestionId;
	}
	
	public final int getMsTopicId() {
		return msTopicId;
	}
	
	public final void setMsTopicId(int msTopicId) {
		this.msTopicId = msTopicId;
	}
	
	public final String getMsTopic() {
		return msTopic;
	}
	
	public final void setMsTopic(String msTopic) {
		this.msTopic = msTopic;
	}
	
	public final String getMsType() {
		return msType;
	}
	
	public final void setMsType(String msType) {
		this.msType = msType;
	}

	@Override
	public String toString() {
		return "FaqFormBean [msQuestion=" + msQuestion + ", msAnswer="
				+ msAnswer + ", msCategory=" + msCategory + ", msQuestionId="
				+ msQuestionId + ", msTopicId=" + msTopicId + ", msTopic="
				+ msTopic + ", msType=" + msType + "]";
	}
	
}
