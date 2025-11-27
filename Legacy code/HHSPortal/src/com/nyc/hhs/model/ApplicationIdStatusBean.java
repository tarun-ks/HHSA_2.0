
package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This class is a bean which maintains the Application Expiry Rule information 
 * which includes Provider Id, Days-Number, Submission Date, Expiry Date.
 *
 */

public class ApplicationIdStatusBean {

	@Length(max = 20) 
	private String msApplicationId;	
	
	private String msStatus;
	
	private String msExpiryDate;
	
	private String msNumDays;
	
	public String getMsExpiryDate() {
		return msExpiryDate;
	}

	public void setMsExpiryDate(String msExpiryDate) {
		this.msExpiryDate = msExpiryDate;
	}

	public String getMsNumDays() {
		return msNumDays;
	}

	public void setMsNumDays(String msNumDays) {
		this.msNumDays = msNumDays;
	}

	public String getApplicationId() {
		return msApplicationId;
	}

	public void setApplicationId(String msApplicationId) {
		this.msApplicationId = msApplicationId;
	}

	public String getStatus() {
		return msStatus;
	}

	public void setStatus(String msStatus) {
		this.msStatus = msStatus;
	}

	@Override
	public String toString() {
		return "ApplicationIdStatusBean [msApplicationId=" + msApplicationId
				+ ", msStatus=" + msStatus + "]";
	}
	
}
