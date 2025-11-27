package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the Provider Status information.
 *
 */

public class ProviderStatusBean
{

	@Length(max = 20) 
	public String msApplicationId;
	public String msApplicationStatus;	
	public String msSupersedingStatus;

	
	

	public String getSupersedingStatus() {
		return msSupersedingStatus;
	}
	public void setSupersedingStatus(String supersedingStatus) {
		this.msSupersedingStatus = supersedingStatus;
	}
	public String getApplicationId() {
		return msApplicationId;
	}
	public void setApplicationId(String applicationId) {
		this.msApplicationId = applicationId;
	}
	public String getApplicationStatus() {
		return msApplicationStatus;
	}
	public void setApplicationStatus(String applicationStatus) {
		this.msApplicationStatus = applicationStatus;
	}
	@Override
	public String toString() {
		return "ProviderStatusBean [msApplicationId=" + msApplicationId
				+ ", msApplicationStatus=" + msApplicationStatus
				+ ", msSupersedingStatus=" + msSupersedingStatus + "]";
	}
	
	
	

}
