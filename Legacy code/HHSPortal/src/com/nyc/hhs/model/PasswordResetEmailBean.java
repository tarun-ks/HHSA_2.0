package com.nyc.hhs.model;

/**
 * This class is a bean which maintains the Password reset Email information.
 *
 */

public class PasswordResetEmailBean {

	private String msEmailAddress;
	private String msPwdResetMethod; 
	private String msSecurityQues1;
	private String msAnswer1; 
	private String msSecurityQues2;
	private String msAnswe2;
	private String msNewNycIdpwd;
	private String msConfirmNewNycIdPwd;
	
	
	public String getMsEmailAddress() {
		return msEmailAddress;
	}
	
	public void setMsEmailAddress(String msEmailAddress) {
		this.msEmailAddress = msEmailAddress;
	}
	
	public String getMsPwdResetMethod() {
		return msPwdResetMethod;
	}
	
	public void setMsPwdResetMethod(String msPwdResetMethod) {
		this.msPwdResetMethod = msPwdResetMethod;
	}
	
	public String getMsSecurityQues1() {
		return msSecurityQues1;
	}
	
	public void setMsSecurityQues1(String msSecurityQues1) {
		this.msSecurityQues1 = msSecurityQues1;
	}
	
	public String getMsAnswer1() {
		return msAnswer1;
	}
	
	public void setMsAnswer1(String msAnswer1) {
		this.msAnswer1 = msAnswer1;
	}
	
	public String getMsSecurityQues2() {
		return msSecurityQues2;
	}
	
	public void setMsSecurityQues2(String msSecurityQues2) {
		this.msSecurityQues2 = msSecurityQues2;
	}
	
	public String getMsAnswe2() {
		return msAnswe2;
	}
	
	public void setMsAnswe2(String msAnswe2) {
		this.msAnswe2 = msAnswe2;
	}
	
	public String getMsNewNycIdpwd() {
		return msNewNycIdpwd;
	}
	
	public void setMsNewNycIdpwd(String msNewNycIdpwd) {
		this.msNewNycIdpwd = msNewNycIdpwd;
	}
	
	public String getMsConfirmNewNycIdPwd() {
		return msConfirmNewNycIdPwd;
	}
	
	public void setMsConfirmNewNycIdPwd(String msConfirmNewNycIdPwd) {
		this.msConfirmNewNycIdPwd = msConfirmNewNycIdPwd;
	}

	@Override
	public String toString() {
		return "PasswordResetEmailBean [msEmailAddress=" + msEmailAddress
				+ ", msPwdResetMethod=" + msPwdResetMethod
				+ ", msSecurityQues1=" + msSecurityQues1 + ", msAnswer1="
				+ msAnswer1 + ", msSecurityQues2=" + msSecurityQues2
				+ ", msAnswe2=" + msAnswe2 + ", msNewNycIdpwd=" + msNewNycIdpwd
				+ ", msConfirmNewNycIdPwd=" + msConfirmNewNycIdPwd + "]";
	}

	
}
