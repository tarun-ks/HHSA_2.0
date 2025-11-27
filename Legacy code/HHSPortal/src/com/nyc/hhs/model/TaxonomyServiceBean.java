package com.nyc.hhs.model;

import java.sql.Date;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This class is used to persist the data for taxonomy services
 * 
 */

public class TaxonomyServiceBean
{

	@Length(max = 20)
	private String serviceApplicationId;
	@Length(max = 20)
	private String businessApplicationId;
	@Length(max = 20)
	private String serviceElementId;

	private String serviceElementName;
	@Length(max = 20)
	private String organizationId;

	private String userId;

	private String submittedBy;

	private Date submittionDate;

	private String modifiedBy;

	private Date modifiedDate;

	private Date startDate;

	private Date expirationDate;

	private String removedFlag;

	private String inActiveFlag;

	private String statusId;

	private String serviceStatus;

	private String processStatus;

	private String applicationId;

	private String oldServiceApplicationId;

	private String createdBy;

	private Date createdDate;

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * @param createdBy
	 *            the createdBy to set
	 */
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate()
	{
		return createdDate;
	}

	/**
	 * @param createdDate
	 *            the createdDate to set
	 */
	public void setCreatedDate(Date createdDate)
	{
		this.createdDate = createdDate;
	}

	public String getApplicationId()
	{
		return applicationId;
	}

	public void setApplicationId(String applicationId)
	{
		this.applicationId = applicationId;
	}

	public String getServiceElementName()
	{
		return serviceElementName;
	}

	public void setServiceElementName(String serviceElementName)
	{
		this.serviceElementName = serviceElementName;
	}

	public String getServiceApplicationId()
	{
		return serviceApplicationId;
	}

	public void setServiceApplicationId(String serviceApplicationId)
	{
		this.serviceApplicationId = serviceApplicationId;
	}

	public String getBusinessApplicationId()
	{
		return businessApplicationId;
	}

	public void setBusinessApplicationId(String businessApplicationId)
	{
		this.businessApplicationId = businessApplicationId;
	}

	public String getServiceElementId()
	{
		return serviceElementId;
	}

	public void setServiceElementId(String serviceElementId)
	{
		this.serviceElementId = serviceElementId;
	}

	public String getOrganizationId()
	{
		return organizationId;
	}

	public void setOrganizationId(String organizationId)
	{
		this.organizationId = organizationId;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getSubmittedBy()
	{
		return submittedBy;
	}

	public void setSubmittedBy(String submittedBy)
	{
		this.submittedBy = submittedBy;
	}

	public Date getSubmittionDate()
	{
		return submittionDate;
	}

	public void setSubmittionDate(Date submittionDate)
	{
		this.submittionDate = submittionDate;
	}

	public String getModifiedBy()
	{
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate()
	{
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate)
	{
		this.modifiedDate = modifiedDate;
	}

	public Date getStartDate()
	{
		return startDate;
	}

	public void setStartDate(Date startDate)
	{
		this.startDate = startDate;
	}

	public Date getExpirationDate()
	{
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate)
	{
		this.expirationDate = expirationDate;
	}

	public String getRemovedFlag()
	{
		return removedFlag;
	}

	public void setRemovedFlag(String removedFlag)
	{
		this.removedFlag = removedFlag;
	}

	public String getInActiveFlag()
	{
		return inActiveFlag;
	}

	public void setInActiveFlag(String inActiveFlag)
	{
		this.inActiveFlag = inActiveFlag;
	}

	public String getStatusId()
	{
		return statusId;
	}

	public void setStatusId(String statusId)
	{
		this.statusId = statusId;
	}

	public String getServiceStatus()
	{
		return serviceStatus;
	}

	public void setServiceStatus(String serviceStatus)
	{
		this.serviceStatus = serviceStatus;
	}

	public String getProcessStatus()
	{
		return processStatus;
	}

	public void setProcessStatus(String processStatus)
	{
		this.processStatus = processStatus;
	}

	public String getOldServiceApplicationId()
	{
		return oldServiceApplicationId;
	}

	public void setOldServiceApplicationId(String oldServiceApplicationId)
	{
		this.oldServiceApplicationId = oldServiceApplicationId;
	}

	@Override
	public String toString() {
		return "TaxonomyServiceBean [serviceApplicationId="
				+ serviceApplicationId + ", businessApplicationId="
				+ businessApplicationId + ", serviceElementId="
				+ serviceElementId + ", serviceElementName="
				+ serviceElementName + ", organizationId=" + organizationId
				+ ", userId=" + userId + ", submittedBy=" + submittedBy
				+ ", submittionDate=" + submittionDate + ", modifiedBy="
				+ modifiedBy + ", modifiedDate=" + modifiedDate
				+ ", startDate=" + startDate + ", expirationDate="
				+ expirationDate + ", removedFlag=" + removedFlag
				+ ", inActiveFlag=" + inActiveFlag + ", statusId=" + statusId
				+ ", serviceStatus=" + serviceStatus + ", processStatus="
				+ processStatus + ", applicationId=" + applicationId
				+ ", oldServiceApplicationId=" + oldServiceApplicationId
				+ ", createdBy=" + createdBy + ", createdDate=" + createdDate
				+ "]";
	}
}
