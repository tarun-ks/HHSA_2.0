package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;

/**
 * This DTO class with multiple getter and setter is used to fetch/display
 * custom properties for document from doc type details configuration
 * 
 */

public class DocumentPropertiesBean
{
	public String propSymbolicName;
	@NotNull
	public String propertyId;
	public String propertyType;
	public String propDisplayName;
	private Object propValue;
	public boolean isdisabled;
	public boolean isdropdown;
	/**
	 * @return the propSymbolicName
	 */
	public String getPropSymbolicName() {
		return propSymbolicName;
	}
	/**
	 * @param propSymbolicName the propSymbolicName to set
	 */
	public void setPropSymbolicName(String propSymbolicName) {
		this.propSymbolicName = propSymbolicName;
	}
	/**
	 * @return the propertyId
	 */
	public String getPropertyId() {
		return propertyId;
	}
	/**
	 * @param propertyId the propertyId to set
	 */
	public void setPropertyId(String propertyId) {
		this.propertyId = propertyId;
	}
	/**
	 * @return the propertyType
	 */
	public String getPropertyType() {
		return propertyType;
	}
	/**
	 * @param propertyType the propertyType to set
	 */
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}
	/**
	 * @return the propDisplayName
	 */
	public String getPropDisplayName() {
		return propDisplayName;
	}
	/**
	 * @param propDisplayName the propDisplayName to set
	 */
	public void setPropDisplayName(String propDisplayName) {
		this.propDisplayName = propDisplayName;
	}
	/**
	 * @return the propValue
	 */
	public Object getPropValue() {
		return propValue;
	}
	/**
	 * @param propValue the propValue to set
	 */
	public void setPropValue(Object propValue) {
		this.propValue = propValue;
	}
	/**
	 * @return the isdisabled
	 */
	public boolean isIsdisabled() {
		return isdisabled;
	}
	/**
	 * @param isdisabled the isdisabled to set
	 */
	public void setIsdisabled(boolean isdisabled) {
		this.isdisabled = isdisabled;
	}
	/**
	 * @return the isdropdown
	 */
	public boolean isIsdropdown() {
		return isdropdown;
	}
	/**
	 * @param isdropdown the isdropdown to set
	 */
	public void setIsdropdown(boolean isdropdown) {
		this.isdropdown = isdropdown;
	}
	@Override
	public String toString() {
		return "DocumentPropertiesBean [propSymbolicName=" + propSymbolicName
				+ ", propertyId=" + propertyId + ", propertyType="
				+ propertyType + ", propDisplayName=" + propDisplayName
				+ ", propValue=" + propValue + ", isdisabled=" + isdisabled
				+ ", isdropdown=" + isdropdown + "]";
	}

	
}
