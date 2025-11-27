
package com.nyc.hhs.model;

import java.util.Date;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * This class is a bean which maintains the Application Expiry Rule information 
 * which includes Provider Id, Days-Number, Submission Date, Expiry Date.
 *
 */

public class ApplicationExpiryRuleBean {

	@Length(max = 20) 
	private String msApplicationId;	
	
	//@Digits(integer=22, fraction=0)
	@RegExp(value ="^\\d{0,22}")
	private String msProviderId;
	
	@RegExp(value ="^\\d{0,10}")
	private int miNumDays;
	
	private Date mdSubmissionDate;
	
	private Date mdExpiryDate;
	
	public String getApplicationId() {
		return msApplicationId;
	}
	public void setApplicationId(String msApplicationId) {
		this.msApplicationId = msApplicationId;
	}
	
	public int getNumDays() {
		return miNumDays;
	}
	public void setNumDays(int miNumDays) {
		this.miNumDays = miNumDays;
	}
	public String getProviderId() {
		return msProviderId;
	}
	public void setProviderId(String msProviderId) {
		this.msProviderId = msProviderId;
	}
	public void setSubmissionDate(Date mdSubmissionDate) {
		this.mdSubmissionDate = mdSubmissionDate;
	}
	public Date getSubmissionDate() {
		return mdSubmissionDate;
	}
	public Date getExpiryDate() {
		return mdExpiryDate;
	}
	public void setExpiryDate(Date mdExpiryDate) {
		this.mdExpiryDate = mdExpiryDate;
	}
	@Override
	public String toString() {
		return "ApplicationExpiryRuleBean [msApplicationId=" + msApplicationId
				+ ", msProviderId=" + msProviderId + ", miNumDays=" + miNumDays
				+ ", mdSubmissionDate=" + mdSubmissionDate + ", mdExpiryDate="
				+ mdExpiryDate + "]";
	}
	
	
}
