package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class SelectedServicesBean extends BaseFilter
{
	private String procurementServiceId;
	@RegExp(value ="^\\d{0,22}")
	private String procurementId;
	private String serviceName;
	private String approvedForDropDown;
	private String procStatusId;
	private String elementId;
	private String activeFlag;
	private String createdByUserId;
	private String modifiedByUserId;
	private String userId;

	/**
	 * @return the procurementServiceId
	 */
	public String getProcurementServiceId()
	{
		return procurementServiceId;
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
	 * @param procurementServiceId the procurementServiceId to set
	 */
	public void setProcurementServiceId(String procurementServiceId)
	{
		this.procurementServiceId = procurementServiceId;
	}

	/**
	 * @return the procurementId
	 */
	public String getProcurementId()
	{
		return procurementId;
	}

	/**
	 * @param procurementId the procurementId to set
	 */
	public void setProcurementId(String procurementId)
	{
		this.procurementId = procurementId;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName()
	{
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	/**
	 * @return the approvedForDropDown
	 */
	public String getApprovedForDropDown()
	{
		return approvedForDropDown;
	}

	/**
	 * @param approvedForDropDown the approvedForDropDown to set
	 */
	public void setApprovedForDropDown(String approvedForDropDown)
	{
		this.approvedForDropDown = approvedForDropDown;
	}

	/**
	 * @return the procStatusId
	 */
	public String getProcStatusId()
	{
		return procStatusId;
	}

	/**
	 * @param procStatusId the procStatusId to set
	 */
	public void setProcStatusId(String procStatusId)
	{
		this.procStatusId = procStatusId;
	}

	/**
	 * @return the elementId
	 */
	public String getElementId()
	{
		return elementId;
	}

	/**
	 * @param elementId the elementId to set
	 */
	public void setElementId(String elementId)
	{
		this.elementId = elementId;
	}

	/**
	 * @return the activeFlag
	 */
	public String getActiveFlag()
	{
		return activeFlag;
	}

	/**
	 * @param activeFlag the activeFlag to set
	 */
	public void setActiveFlag(String activeFlag)
	{
		this.activeFlag = activeFlag;
	}

	/**
	 * @return the createdByUserId
	 */
	public String getCreatedByUserId()
	{
		return createdByUserId;
	}

	/**
	 * @param createdByUserId the createdByUserId to set
	 */
	public void setCreatedByUserId(String createdByUserId)
	{
		this.createdByUserId = createdByUserId;
	}

	/**
	 * @return the modifiedByUserId
	 */
	public String getModifiedByUserId()
	{
		return modifiedByUserId;
	}

	/**
	 * @param modifiedByUserId the modifiedByUserId to set
	 */
	public void setModifiedByUserId(String modifiedByUserId)
	{
		this.modifiedByUserId = modifiedByUserId;
	}

	@Override
	public String toString()
	{
		return "SelectedServicesBean [procurementServiceId=" + procurementServiceId + ", procurementId="
				+ procurementId + ", serviceName=" + serviceName + ", approvedForDropDown=" + approvedForDropDown
				+ ", procStatusId=" + procStatusId + ", elementId=" + elementId + ", activeFlag=" + activeFlag
				+ ", createdByUserId=" + createdByUserId + ", modifiedByUserId=" + modifiedByUserId + ", userId="
				+ userId + "]";
	}

}
