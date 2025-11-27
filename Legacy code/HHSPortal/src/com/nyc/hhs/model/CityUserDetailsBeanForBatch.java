package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Email;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which is used to get rows from the table CITY_USER_DETAILS
 * 
 */

public class CityUserDetailsBeanForBatch {

	String msFirstName;
	String msLastName;		
	@Length(max = 20) 
	String msUserId;
	@Email
	String msEmailId;
	String msUserRole;
	String msUserDn;
	String msUserType;
	String msOrgName;
	String msActiveFlag;
	
	public String getMsFirstName() {
		return msFirstName;
	}
	public void setMsFirstName(String msFirstName) {
		this.msFirstName = msFirstName;
	}
	public String getMsLastName() {
		return msLastName;
	}
	public void setMsLastName(String msLastName) {
		this.msLastName = msLastName;
	}
	public String getMsUserId() {
		return msUserId;
	}
	public void setMsUserId(String msUserId) {
		this.msUserId = msUserId;
	}
	public String getMsEmailId() {
		return msEmailId;
	}
	public void setMsEmailId(String msEmailId) {
		this.msEmailId = msEmailId;
	}
	public String getMsUserRole() {
		return msUserRole;
	}
	public void setMsUserRole(String msUserRole) {
		this.msUserRole = msUserRole;
	}
	public String getMsUserDn() {
		return msUserDn;
	}
	public void setMsUserDn(String msUserDn) {
		this.msUserDn = msUserDn;
	}
	public String getMsUserType() {
		return msUserType;
	}
	public void setMsUserType(String msUserType) {
		this.msUserType = msUserType;
	}
	public String getMsOrgName() {
		return msOrgName;
	}
	public void setMsOrgName(String msOrgName) {
		this.msOrgName = msOrgName;
	}
	public String getMsActiveFlag() {
		return msActiveFlag;
	}
	public void setMsActiveFlag(String msActiveFlag) {
		this.msActiveFlag = msActiveFlag;
	}
	
 }
