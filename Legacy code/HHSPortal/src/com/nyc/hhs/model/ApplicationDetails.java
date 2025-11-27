package com.nyc.hhs.model;

import java.sql.Date;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.NotNull;
/**
 * This class will be use to maintain application information and document 
 * details which includes Application Id, Document Id, Document Category, Document Type,
 * Effective date and Current Status.
 * 
 */

public class ApplicationDetails
{

	@Length(max = 20) 
	private String msApplicationId;
	private String msDocumentId;
	private String msDocumentCategory;
	private String msDocumentType;
	private Date msEffectiveDate;
	private String msCurrentStatus;

	public String getCurrentStatus()
	{
		return msCurrentStatus;
	}

	public void setCurrentStatus(String asCurrentStatus)
	{
		this.msCurrentStatus = asCurrentStatus;
	}

	public ApplicationDetails()
	{
		//No Action Required
	}

	public String getDocumentId()
	{
		return msDocumentId;
	}

	public void setDocumentId(String asDocumentId)
	{
		this.msDocumentId = asDocumentId;
	}

	public String getDocumentCategory()
	{
		return msDocumentCategory;
	}

	public void setDocumentCategory(String asDocumentCategory)
	{
		this.msDocumentCategory = asDocumentCategory;
	}

	public String getDocumentType()
	{
		return msDocumentType;
	}

	public void setDocumentType(String asDocumentType)
	{
		this.msDocumentType = asDocumentType;
	}

	public String getApplicationId()
	{
		return msApplicationId;
	}
   
	public void setApplicationId(String asApplicationId)
	{
		this.msApplicationId = asApplicationId;
	}

	public Date getEffectiveDate()
	{
		return msEffectiveDate;
	}

	public void setEffectiveDate(Date aoEffectiveDate)
	{
		this.msEffectiveDate = aoEffectiveDate;
	}

	@Override
	public String toString() {
		return "ApplicationDetails [msApplicationId=" + msApplicationId
				+ ", msDocumentId=" + msDocumentId + ", msDocumentCategory="
				+ msDocumentCategory + ", msDocumentType=" + msDocumentType
				+ ", msEffectiveDate=" + msEffectiveDate + ", msCurrentStatus="
				+ msCurrentStatus + "]";
	}

}
