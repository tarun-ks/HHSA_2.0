package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

public class PDFBatch 
{
	@RegExp(value ="^\\d{0,22}")
	private String entityId = HHSConstants.EMPTY_STRING;
	@RegExp(value ="^\\d{0,22}")
	private String subEntityId = HHSConstants.EMPTY_STRING;
	@Length(max = 40)
	private String entityType = HHSConstants.EMPTY_STRING;
	@Length(max = 40)
	private String subEntityType = HHSConstants.EMPTY_STRING;
	public void setEntityId(String entityId)
	{
		this.entityId = entityId;
	}
	public String getEntityId()
	{
		return entityId;
	}
	public void setSubEntityId(String subEntityId)
	{
		this.subEntityId = subEntityId;
	}
	public String getSubEntityId()
	{
		return subEntityId;
	}
	public void setEntityType(String entityType)
	{
		this.entityType = entityType;
	}
	public String getEntityType()
	{
		return entityType;
	}
	public void setSubEntityType(String subEntityType)
	{
		this.subEntityType = subEntityType;
	}
	public String getSubEntityType()
	{
		return subEntityType;
	}
}
