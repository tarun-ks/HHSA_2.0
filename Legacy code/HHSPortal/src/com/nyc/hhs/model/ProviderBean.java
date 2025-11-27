package com.nyc.hhs.model;

import java.io.Serializable;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This DTO class with multiple getters and setters is used to capture the details of all providers
 *
 */

public class ProviderBean implements Serializable, Comparable
{
	private String msDisplayValue;
	@Length(max = 20) 
	private String msHiddenValue;
	private String msStartMonth;
	private String msEndMonth;
	private String msStartYear;
	private String msEndYear;

	
	public String getDisplayValue()
	{
		return msDisplayValue;
	}

	
	public void setDisplayValue(String displayValue)
	{
		this.msDisplayValue = displayValue;
	}

	
	public String getHiddenValue()
	{
		return msHiddenValue;
	}

	public void setHiddenValue(String hiddenValue)
	{
		this.msHiddenValue = hiddenValue;
	}

	public String getStartMonth()
	{
		return msStartMonth;
	}

	public void setStartMonth(String startMonth)
	{
		this.msStartMonth = startMonth;
	}

	
	public String getEndMonth()
	{
		return msEndMonth;
	}

	
	public void setEndMonth(String endMonth)
	{
		this.msEndMonth = endMonth;
	}

	public String getStartYear()
	{
		return msStartYear;
	}

	public void setStartYear(String startYear)
	{
		this.msStartYear = startYear;
	}

	public String getEndYear()
	{
		return msEndYear;
	}

	public void setEndYear(String endYear)
	{
		this.msEndYear = endYear;
	}

	@Override
	public int compareTo(Object loObject)
	{
		return this.msDisplayValue.compareTo(((ProviderBean) loObject).msDisplayValue);
	}

	@Override
	public String toString() {
		return "ProviderBean [msDisplayValue=" + msDisplayValue
				+ ", msHiddenValue=" + msHiddenValue + ", msStartMonth="
				+ msStartMonth + ", msEndMonth=" + msEndMonth
				+ ", msStartYear=" + msStartYear + ", msEndYear=" + msEndYear
				+ "]";
	}
	
	
}
