package com.nyc.hhs.model;

import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * @author manish.grover
 * 
 */
public class AutoSaveBean
{
	public AutoSaveBean()
	{
		super();
	}

	private List<TempBean> tempBean;
	@Length(max = 40) //increase the length to 40 for the entityId, it could be filenet document id for query(the getAutoSaveInfo), view document information (Procurements -- RFP document, view document info)
	private String entityId;
	@Length(max = 200)
	private String entityName;
	@Length(max = 20)
	private String userId;
	@Length(max = 100)
	//PAGE_NAME
	private String jspName;
	private String textareaName;
	private String textareaValue;
	private String orgType;

	/**
	 * @return the tempBean
	 */
	public List<TempBean> getTempBean()
	{
		return tempBean;
	}

	/**
	 * @param tempBean the tempBean to set
	 */
	public void setTempBean(List<TempBean> tempBean)
	{
		this.tempBean = tempBean;
	}

	/**
	 * @return the entityId
	 */
	public String getEntityId()
	{
		return entityId;
	}

	/**
	 * @param entityId the entityId to set
	 */
	public void setEntityId(String entityId)
	{
		this.entityId = entityId;
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName()
	{
		return entityName;
	}

	/**
	 * @param entityName the entityName to set
	 */
	public void setEntityName(String entityName)
	{
		this.entityName = entityName;
	}

	/**
	 * @return the userId
	 */
	public String getUserId()
	{
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	/**
	 * @return the jspName
	 */
	public String getJspName()
	{
		return jspName;
	}

	/**
	 * @param jspName the jspName to set
	 */
	public void setJspName(String jspName)
	{
		this.jspName = jspName;
	}

	/**
	 * @return the textareaName
	 */
	public String getTextareaName()
	{
		return textareaName;
	}

	/**
	 * @param textareaName the textareaName to set
	 */
	public void setTextareaName(String textareaName)
	{
		this.textareaName = textareaName;
	}

	/**
	 * @return the textareaValue
	 */
	public String getTextareaValue()
	{
		return textareaValue;
	}

	/**
	 * @param textareaValue the textareaValue to set
	 */
	public void setTextareaValue(String textareaValue)
	{
		this.textareaValue = textareaValue;
	}

	/**
	 * @return the orgType
	 */
	public String getOrgType()
	{
		return orgType;
	}

	/**
	 * @param orgType the orgType to set
	 */
	public void setOrgType(String orgType)
	{
		this.orgType = orgType;
	}
}
