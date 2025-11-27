package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the User details.
 * 
 */

public class UserDetailBean
{

	@Length(max = 20)
	private String msUserId;
	private String msFirstName;
	private String msMiddleName;
	private String msLastName;
	private String msCaptchaImg;
	private String msCaptchaTxt;
	private String msEmailAddress;
	private String msConfirmEmailAddress;
	private String msPassword;
	private String msConfirmPassword;
	private int miSecurityQuestionId1;
	private int miSecurityQuestionId2;
	private int miSecurityQuestionId3;
	private String msAnswerId1;
	private String msAnswerId2;
	private String msAnswerID3;
	private String msAuditDetails;

	public String getMsUserId()
	{
		return msUserId;
	}

	public void setMsUserId(String msUserId)
	{
		this.msUserId = msUserId;
	}

	public String getMsFirstName()
	{
		return msFirstName;
	}

	public void setMsFirstName(String msFirstName)
	{
		this.msFirstName = msFirstName;
	}

	public String getMsMiddleName()
	{
		return msMiddleName;
	}

	public void setMsMiddleName(String msMiddleName)
	{
		this.msMiddleName = msMiddleName;
	}

	public String getMsLastName()
	{
		return msLastName;
	}

	public String getMsCaptchaImg()
	{
		return msCaptchaImg;
	}

	public void setMsCaptchaImg(String msCaptchaImg)
	{
		this.msCaptchaImg = msCaptchaImg;
	}

	public String getMsCaptchaTxt()
	{
		return msCaptchaTxt;
	}

	public void setMsCaptchaTxt(String msCaptchaTxt)
	{
		this.msCaptchaTxt = msCaptchaTxt;
	}

	public void setMsLastName(String msLastName)
	{
		this.msLastName = msLastName;
	}

	public String getMsEmailAddress()
	{
		return msEmailAddress;
	}

	public void setMsEmailAddress(String msEmailAddress)
	{
		this.msEmailAddress = msEmailAddress;
	}

	public String getMsConfirmEmailAddress()
	{
		return msConfirmEmailAddress;
	}

	public void setMsConfirmEmailAddress(String msConfirmEmailAddress)
	{
		this.msConfirmEmailAddress = msConfirmEmailAddress;
	}

	public String getMsPassword()
	{
		return msPassword;
	}

	public void setMsPassword(String msPassword)
	{
		this.msPassword = msPassword;
	}

	public String getMsConfirmPassword()
	{
		return msConfirmPassword;
	}

	public void setMsConfirmPassword(String msConfirmPassword)
	{
		this.msConfirmPassword = msConfirmPassword;
	}

	public int getMiSecurityQuestionId1()
	{
		return miSecurityQuestionId1;
	}

	public void setMiSecurityQuestionId1(int miSecurityQuestionId1)
	{
		this.miSecurityQuestionId1 = miSecurityQuestionId1;
	}

	public int getMiSecurityQuestionId2()
	{
		return miSecurityQuestionId2;
	}

	public void setMiSecurityQuestionId2(int miSecurityQuestionId2)
	{
		this.miSecurityQuestionId2 = miSecurityQuestionId2;
	}

	public int getMiSecurityQuestionId3()
	{
		return miSecurityQuestionId3;
	}

	public void setMiSecurityQuestionId3(int miSecurityQuestionId3)
	{
		this.miSecurityQuestionId3 = miSecurityQuestionId3;
	}

	public String getMsAnswerId1()
	{
		return msAnswerId1;
	}

	public void setMsAnswerId1(String msAnswerId1)
	{
		this.msAnswerId1 = msAnswerId1;
	}

	public String getMsAnswerId2()
	{
		return msAnswerId2;
	}

	public void setMsAnswerId2(String msAnswerId2)
	{
		this.msAnswerId2 = msAnswerId2;
	}

	public String getMsAnswerID3()
	{
		return msAnswerID3;
	}

	public void setMsAnswerID3(String msAnswerID3)
	{
		this.msAnswerID3 = msAnswerID3;
	}

	public String getMsAuditDetails()
	{
		return msAuditDetails;
	}

	public void setMsAuditDetails(String msAuditDetails)
	{
		this.msAuditDetails = msAuditDetails;
	}

	@Override
	public String toString()
	{
		return "UserDetailBean [msUserId=" + msUserId + ", msFirstName=" + msFirstName + ", msMiddleName="
				+ msMiddleName + ", msLastName=" + msLastName + ", msCaptchaImg=" + msCaptchaImg + ", msCaptchaTxt="
				+ msCaptchaTxt + ", msEmailAddress=" + msEmailAddress + ", msConfirmEmailAddress="
				+ msConfirmEmailAddress + ", msPassword=" + msPassword + ", msConfirmPassword=" + msConfirmPassword
				+ ", miSecurityQuestionId1=" + miSecurityQuestionId1 + ", miSecurityQuestionId2="
				+ miSecurityQuestionId2 + ", miSecurityQuestionId3=" + miSecurityQuestionId3 + ", msAnswerId1="
				+ msAnswerId1 + ", msAnswerId2=" + msAnswerId2 + ", msAnswerID3=" + msAnswerID3 + ", msAuditDetails="
				+ msAuditDetails + "]";
	}

}
