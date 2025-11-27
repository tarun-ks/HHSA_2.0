package com.nyc.hhs.model;

import java.util.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/**
 * This class is a bean which maintains the Contact-Us information.
 *
 */

public class ContactUsBean {

	@RegExp(value ="^\\d{0,22}")
	private Integer msProviderID;
	//@RegExp(value ="^\\d{0,3}")
	private Integer msTopicID;
	@Length(max = 50)
	private String msTopic;
	private String msQuestion;
	private String msProviderName;
	private String msStatus;
	private String msContactMedium;
	private String msCreationUser;
	private Integer msApplicationID;
	private Date msCreationDate;
	private Integer msSequenceID;
	private String msOrganisationId;

	public String getMsOrganisationId() {
		return msOrganisationId;
	}

	public void setMsOrganisationId(String msOrganisationId) {
		this.msOrganisationId = msOrganisationId;
	}

	public final Integer getMsSequenceID() {
		return msSequenceID;
	}

	public final void setMsSequenceID(Integer msSequenceID) {
		this.msSequenceID = msSequenceID;
	}

	public Integer getMsProviderID() {
		return msProviderID;
	}

	public void setMsProviderID(Integer msProviderID) {
		this.msProviderID = msProviderID;
	}

	public Integer getMsTopicID() {
		return msTopicID;
	}

	public void setMsTopicID(Integer msTopicID) {
		this.msTopicID = msTopicID;
	}

	public String getMsTopic() {
		return msTopic;
	}

	public void setMsTopic(String msTopic) {
		this.msTopic = msTopic;
	}

	public String getMsQuestion() {
		return msQuestion;
	}

	public void setMsQuestion(String msQuestion) {
		this.msQuestion = msQuestion;
	}

	public String getMsContactMedium() {
		return msContactMedium;
	}

	public void setMsContactMedium(String msContactMedium) {
		this.msContactMedium = msContactMedium;
	}

	public String getMsCreationUser() {
		return msCreationUser;
	}

	public void setMsCreationUser(String msCreationUser) {
		this.msCreationUser = msCreationUser;
	}

	public Date getMsCreationDate() {
		return msCreationDate;
	}

	public void setMsCreationDate(Date msCreationDate) {
		this.msCreationDate = msCreationDate;
	}

	public final String getMsProviderName() {
		return msProviderName;
	}

	public final void setMsProviderName(String msProviderName) {
		this.msProviderName = msProviderName;
	}

	public final String getMsStatus() {
		return msStatus;
	}

	public final void setMsStatus(String msStatus) {
		this.msStatus = msStatus;
	}

	public final Integer getMsApplicationID() {
		return msApplicationID;
	}

	public final void setMsApplicationID(Integer msApplicationID) {
		this.msApplicationID = msApplicationID;
	}

	@Override
	public String toString() {
		return "ContactUsBean [msProviderID=" + msProviderID + ", msTopicID="
				+ msTopicID + ", msTopic=" + msTopic + ", msQuestion="
				+ msQuestion + ", msProviderName=" + msProviderName
				+ ", msStatus=" + msStatus + ", msContactMedium="
				+ msContactMedium + ", msCreationUser=" + msCreationUser
				+ ", msApplicationID=" + msApplicationID + ", msCreationDate="
				+ msCreationDate + ", msSequenceID=" + msSequenceID
				+ ", msOrganisationId=" + msOrganisationId + "]";
	}

}
