package com.nyc.hhs.model;

import java.io.Serializable;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class EntityStatusBean implements Serializable
{

	@Length(max = 20)
	String msEntityName;
	List<MasterStatusBean> msStatusBean;

	public String getEntityName()
	{
		return msEntityName;
	}

	public void setEntityName(String msEntityName)
	{
		this.msEntityName = msEntityName;
	}

	public List<MasterStatusBean> getStatusBean()
	{
		return msStatusBean;
	}

	public void setStatusBean(List<MasterStatusBean> msStatusBean)
	{
		this.msStatusBean = msStatusBean;
	}

	@Override
	public String toString()
	{
		return "EntityStatusBean [msEntityName=" + msEntityName + ", msStatusBean=" + msStatusBean + "]";
	}

}
