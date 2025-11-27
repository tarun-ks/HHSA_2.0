/**
 * 
 */
package com.nyc.hhs.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a bean which maintains the Register NYC Id information.
 *
 */

@SuppressWarnings("rawtypes")
public class RegisterNycIdBean implements Serializable{
	
	private static final long serialVersionUID = 4345571085131544543L;
	private String msFirstName = "";
	private String msMiddleName = ""; 
	private String msLastName ="";
	private String msEmailAddress="";
	private String msConfirmEmailAddress="";
	private String msPassword="";
	private String msConfirmPassword="";
	private int miSecurityQuestion1Id;
	private int miSecurityQuestion2Id;
	private int miSecurityQuestion3Id;
	private String msAnswer1 ="";
	private String msAnswer2="";
	private String msAnswer3="";
	private String msQues1Text ="";
	private String msQues2Text="";
	private String msQues3Text="";
	private List moSecurityQuestion1List = new ArrayList();;
	private List moSecurityQuestion2List =new ArrayList();
	private List moSecurityQuestion3List=new ArrayList();
	
	public final String getMsFirstName() {
		return msFirstName;
	}
	
	public final void setMsFirstName(String msFirstName) {
		this.msFirstName = msFirstName;
	}
	
	public final String getMsMiddleName() {
		return msMiddleName;
	}
	
	public final void setMsMiddleName(String msMiddleName) {
		this.msMiddleName = msMiddleName;
	}
	
	public final String getMsLastName() {
		return msLastName;
	}
	
	public final void setMsLastName(String msLastName) {
		this.msLastName = msLastName;
	}
	
	public final String getMsEmailAddress() {
		return msEmailAddress;
	}
	
	public final void setMsEmailAddress(String msEmailAddress) {
		this.msEmailAddress = msEmailAddress;
	}
	
	public final String getMsConfirmEmailAddress() {
		return msConfirmEmailAddress;
	}
	
	public final void setMsConfirmEmailAddress(String msConfirmEmailAddress) {
		this.msConfirmEmailAddress = msConfirmEmailAddress;
	}
	
	public final String getMsPassword() {
		return msPassword;
	}
	
	public final void setMsPassword(String msPassword) {
		this.msPassword = msPassword;
	}
	
	public final String getMsConfirmPassword() {
		return msConfirmPassword;
	}
	
	public final void setMsConfirmPassword(String msConfirmPassword) {
		this.msConfirmPassword = msConfirmPassword;
	}
	
	public final int getMiSecurityQuestion1Id() {
		return miSecurityQuestion1Id;
	}
	
	public final void setMiSecurityQuestion1Id(int miSecurityQuestion1Id) {
		this.miSecurityQuestion1Id = miSecurityQuestion1Id;
	}
	
	public final int getMiSecurityQuestion2Id() {
		return miSecurityQuestion2Id;
	}
	
	public final void setMiSecurityQuestion2Id(int miSecurityQuestion2Id) {
		this.miSecurityQuestion2Id = miSecurityQuestion2Id;
	}
	
	public final int getMiSecurityQuestion3Id() {
		return miSecurityQuestion3Id;
	}
	
	public final void setMiSecurityQuestion3Id(int miSecurityQuestion3Id) {
		this.miSecurityQuestion3Id = miSecurityQuestion3Id;
	}
	
	public final String getMsAnswer1() {
		return msAnswer1;
	}
	
	public final void setMsAnswer1(String msAnswer1) {
		this.msAnswer1 = msAnswer1;
	}
	
	public final String getMsAnswer2() {
		return msAnswer2;
	}
	
	public final void setMsAnswer2(String msAnswer2) {
		this.msAnswer2 = msAnswer2;
	}
	
	public final String getMsAnswer3() {
		return msAnswer3;
	}
	
	public final void setMsAnswer3(String msAnswer3) {
		this.msAnswer3 = msAnswer3;
	}
	
	public final List getMoSecurityQuestion1List() {
		return moSecurityQuestion1List;
	}
	
	public final void setMoSecurityQuestion1List(List moSecurityQuestion1List) {
		this.moSecurityQuestion1List = moSecurityQuestion1List;
	}
	
	public final List getMoSecurityQuestion2List() {
		return moSecurityQuestion2List;
	}
	
	public final void setMoSecurityQuestion2List(List moSecurityQuestion2List) {
		this.moSecurityQuestion2List = moSecurityQuestion2List;
	}
	
	public final List getMoSecurityQuestion3List() {
		return moSecurityQuestion3List;
	}
	
	public final void setMoSecurityQuestion3List(List moSecurityQuestion3List) {
		this.moSecurityQuestion3List = moSecurityQuestion3List;
	}
	
	public String getMsQues1Text() {
		return msQues1Text;
	}
	
	public void setMsQues1Text(String msQues1Text) {
		this.msQues1Text = msQues1Text;
	}
	
	public String getMsQues2Text() {
		return msQues2Text;
	}
	
	public void setMsQues2Text(String msQues2Text) {
		this.msQues2Text = msQues2Text;
	}
	
	public String getMsQues3Text() {
		return msQues3Text;
	}
	
	public void setMsQues3Text(String msQues3Text) {
		this.msQues3Text = msQues3Text;
	}

	@Override
	public String toString() {
		return "RegisterNycIdBean [msFirstName=" + msFirstName
				+ ", msMiddleName=" + msMiddleName + ", msLastName="
				+ msLastName + ", msEmailAddress=" + msEmailAddress
				+ ", msConfirmEmailAddress=" + msConfirmEmailAddress
				+ ", msPassword=" + msPassword + ", msConfirmPassword="
				+ msConfirmPassword + ", miSecurityQuestion1Id="
				+ miSecurityQuestion1Id + ", miSecurityQuestion2Id="
				+ miSecurityQuestion2Id + ", miSecurityQuestion3Id="
				+ miSecurityQuestion3Id + ", msAnswer1=" + msAnswer1
				+ ", msAnswer2=" + msAnswer2 + ", msAnswer3=" + msAnswer3
				+ ", msQues1Text=" + msQues1Text + ", msQues2Text="
				+ msQues2Text + ", msQues3Text=" + msQues3Text
				+ ", moSecurityQuestion1List=" + moSecurityQuestion1List
				+ ", moSecurityQuestion2List=" + moSecurityQuestion2List
				+ ", moSecurityQuestion3List=" + moSecurityQuestion3List + "]";
	}
	
	
}
