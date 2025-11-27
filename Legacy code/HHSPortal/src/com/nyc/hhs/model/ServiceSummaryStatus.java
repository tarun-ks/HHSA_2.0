package com.nyc.hhs.model;

/**
 * This class is a bean which maintains the Service Summary Status details.
 *
 */ 

public class ServiceSummaryStatus {

	private String msQuestionStatus;
	private String msDocumentStatus;
	private String msSelectedSpecizationNames;
	private String msSelectedSettigNames;
	
	private String msQuestionClass;
	private String msDocumentClass;
	private String msSelectedSpecizationClass;
	private String msSelectedSettigClass;
	
	public String getQuestionStatus() {
		return msQuestionStatus;
	}
	public void setQuestionStatus(String asQuestionStatus) {
		this.msQuestionStatus = asQuestionStatus;
	}
	public String getDocumentStatus() {
		return msDocumentStatus;
	}
	public void setDocumentStatus(String asDocumentStatus) {
		this.msDocumentStatus = asDocumentStatus;
	}
	public String getSelectedSpecizationNames() {
		return msSelectedSpecizationNames;
	}
	public void setSelectedSpecizationNames(String asSelectedSpecizationNames) {
		this.msSelectedSpecizationNames = asSelectedSpecizationNames;
	}
	public String getSelectedSettigNames() {
		return msSelectedSettigNames;
	}
	public void setSelectedSettigNames(String asSelectedSettigNames) {
		this.msSelectedSettigNames = asSelectedSettigNames;
	}
	public String getQuestionClass() {
		return msQuestionClass;
	}
	public void setQuestionClass(String questionClass) {
		this.msQuestionClass = questionClass;
	}
	public String getDocumentClass() {
		return msDocumentClass;
	}
	public void setDocumentClass(String documentClass) {
		this.msDocumentClass = documentClass;
	}
	public String getSelectedSpecizationClass() {
		return msSelectedSpecizationClass;
	}
	public void setSelectedSpecizationClass(String selectedSpecizationClass) {
		this.msSelectedSpecizationClass = selectedSpecizationClass;
	}
	public String getSelectedSettigClass() {
		return msSelectedSettigClass;
	}
	public void setSelectedSettigClass(String selectedSettigClass) {
		this.msSelectedSettigClass = selectedSettigClass;
	}
	@Override
	public String toString() {
		return "ServiceSummaryStatus [msQuestionStatus=" + msQuestionStatus
				+ ", msDocumentStatus=" + msDocumentStatus
				+ ", msSelectedSpecizationNames=" + msSelectedSpecizationNames
				+ ", msSelectedSettigNames=" + msSelectedSettigNames
				+ ", msQuestionClass=" + msQuestionClass + ", msDocumentClass="
				+ msDocumentClass + ", msSelectedSpecizationClass="
				+ msSelectedSpecizationClass + ", msSelectedSettigClass="
				+ msSelectedSettigClass + "]";
	}
}
