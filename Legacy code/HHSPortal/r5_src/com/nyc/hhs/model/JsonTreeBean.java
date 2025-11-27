package com.nyc.hhs.model;
//This is a Bean class added for JSON tree functionality
public class JsonTreeBean
{
	private String organizationId;
	private String organizationType;
	
	public JsonTreeBean()
	{
		super();
	}
	public String getOrganizationId()
	{
		return organizationId;
	}
	public void setOrganizationId(String organizationId)
	{
		this.organizationId = organizationId;
	}
	public String getOrganizationType()
	{
		return organizationType;
	}
	public void setOrganizationType(String organizationType)
	{
		this.organizationType = organizationType;
	}
}
