package com.nyc.hhs.model;

import java.sql.Date;

import com.nyc.hhs.constants.HHSConstants;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This DTO class with multiple getter and setters used to store the documents
 * and section details which later used to launch new applications and start
 * work flow.\
 * 
 */

public class DocumentBean
{
	// Added in R5
	@Length(max = 20)
	private String businessId;
	@Length(max = 20)
	private String businessAppId;
	private String appStatus;
	private String contractAccess = HHSConstants.EMPTY_STRING;

	public String getAppStatus()
	{
		return appStatus;
	}

	public String getBusinessId()
	{
		return businessId;
	}

	public void setBusinessId(String businessId)
	{
		this.businessId = businessId;
	}

	public String getBusinessAppId()
	{
		return businessAppId;
	}

	public void setBusinessAppId(String businessAppId)
	{
		this.businessAppId = businessAppId;
	}

	public void setAppStatus(String appStatus)
	{
		this.appStatus = appStatus;
	}

	private String ctNum;

	public String getCtNum()
	{
		return ctNum;
	}

	public void setCtNum(String ctNum)
	{
		this.ctNum = ctNum;
	}

	private String fiscalYearId;
	private String budgetTypeId;

	public String getFiscalYearId()
	{
		return fiscalYearId;
	}

	public void setFiscalYearId(String fiscalYearId)
	{
		this.fiscalYearId = fiscalYearId;
	}

	public String getBudgetTypeId()
	{
		return budgetTypeId;
	}

	public void setBudgetTypeId(String budgetTypeId)
	{
		this.budgetTypeId = budgetTypeId;
	}

	private String organizationId;
	private String contractId;
	private String statusIdProcurement;

	public String getOrganizationId()
	{
		return organizationId;
	}

	public void setOrganizationId(String organizationId)
	{
		this.organizationId = organizationId;
	}

	public String getContractId()
	{
		return contractId;
	}

	public void setContractId(String contractId)
	{
		this.contractId = contractId;
	}

	public String getStatusIdProcurement()
	{
		return statusIdProcurement;
	}

	public void setStatusIdProcurement(String statusIdProcurement)
	{
		this.statusIdProcurement = statusIdProcurement;
	}

	private String parentEntityName;

	public String getParentEntityName()
	{
		return parentEntityName;
	}

	public void setParentEntityName(String parentEntityName)
	{
		this.parentEntityName = parentEntityName;
	}

	// R5 Ends
	private String msOrgID;
	private String msUserID;
	private String msSectionID;
	private String msAppID;
	private String msDocCategory;
	private String msDocID;
	private String msDocType;
	private String msDocStatus;
	private Date msModifiedDate;
	private String msModifiedBy;
	private Date msCreatedDate;
	private String msCreatedBy;
	private String msQuestionID;
	private String msFormID;
	private String msFormName;
	private String msFormVersion;
	private String msProcDocStatus;
	private String msDocTitle;
	private String msCeoName;
	private String msCfoName;
	private boolean mbIsCeoName = false;
	private boolean mbIsCfoName = false;
	private boolean mbIsCeoActive = false;
	private String entityLinked;
	private String date;
	private String entityId;
	private String parentId;
	private String permissionType;
	private String userRole;
	/**  QC 8914 read only role R 7.2 **/
	private String userSubRole;

	private String serviceAppId;
	private String appId;

	public String getEntityId()
	{
		return entityId;
	}

	public void setEntityId(String entityId)
	{
		this.entityId = entityId;
	}

	public String getParentId()
	{
		return parentId;
	}

	public void setParentId(String parentId)
	{
		this.parentId = parentId;
	}

	public DocumentBean()
	{
		// No Action Required
	}

	public boolean isMbIsCeoActive()
	{
		return mbIsCeoActive;
	}

	public void setMbIsCeoActive(boolean mbIsCeoActive)
	{
		this.mbIsCeoActive = mbIsCeoActive;
	}

	public String getMsCeoName()
	{
		return msCeoName;
	}

	public void setMsCeoName(String msCeoName)
	{
		this.msCeoName = msCeoName;
	}

	public String getMsCfoName()
	{
		return msCfoName;
	}

	public void setMsCfoName(String msCfoName)
	{
		this.msCfoName = msCfoName;
	}

	public boolean isMbIsCeoName()
	{
		return mbIsCeoName;
	}

	public void setMbIsCeoName(boolean mbIsCeoName)
	{
		this.mbIsCeoName = mbIsCeoName;
	}

	public boolean isMbIsCfoName()
	{
		return mbIsCfoName;
	}

	public void setMbIsCfoName(boolean mbIsCfoName)
	{
		this.mbIsCfoName = mbIsCfoName;
	}

	public String getOrgID()
	{
		return msOrgID;
	}

	public void setOrgID(String msOrgID)
	{
		this.msOrgID = msOrgID;
	}

	public String getUserID()
	{
		return msUserID;
	}

	public void setUserID(String msUserID)
	{
		this.msUserID = msUserID;
	}

	public String getSectionID()
	{
		return msSectionID;
	}

	public void setSectionID(String msSectionID)
	{
		this.msSectionID = msSectionID;
	}

	public String getAppID()
	{
		return msAppID;
	}

	public void setAppID(String msAppID)
	{
		this.msAppID = msAppID;
	}

	public String getDocCategory()
	{
		return msDocCategory;
	}

	public void setDocCategory(String msDocCategory)
	{
		this.msDocCategory = msDocCategory;
	}

	public String getDocID()
	{
		return msDocID;
	}

	public void setDocID(String msDocID)
	{
		this.msDocID = msDocID;
	}

	public String getDocType()
	{
		return msDocType;
	}

	public void setDocType(String msDocType)
	{
		this.msDocType = msDocType;
	}

	public String getDocStatus()
	{
		return msDocStatus;
	}

	public void setDocStatus(String msDocStatus)
	{
		this.msDocStatus = msDocStatus;
	}

	public Date getModifiedDate()
	{
		return msModifiedDate;
	}

	public void setModifiedDate(Date msModifiedDate)
	{
		this.msModifiedDate = msModifiedDate;
	}

	public String getModifiedBy()
	{
		return msModifiedBy;
	}

	public void setModifiedBy(String msModifiedBy)
	{
		this.msModifiedBy = msModifiedBy;
	}

	public Date getCreatedDate()
	{
		return msCreatedDate;
	}

	public void setCreatedDate(Date msCreatedDate)
	{
		this.msCreatedDate = msCreatedDate;
	}

	public String getCreatedBy()
	{
		return msCreatedBy;
	}

	public void setCreatedBy(String msCreatedBy)
	{
		this.msCreatedBy = msCreatedBy;
	}

	public String getQuestionID()
	{
		return msQuestionID;
	}

	public void setQuestionID(String msQuestionID)
	{
		this.msQuestionID = msQuestionID;
	}

	public String getFormID()
	{
		return msFormID;
	}

	public void setFormID(String msFormID)
	{
		this.msFormID = msFormID;
	}

	public String getFormName()
	{
		return msFormName;
	}

	public void setFormName(String msFormName)
	{
		this.msFormName = msFormName;
	}

	public String getFormVersion()
	{
		return msFormVersion;
	}

	public void setFormVersion(String msFormVersion)
	{
		this.msFormVersion = msFormVersion;
	}

	public String getProcDocStatus()
	{
		return msProcDocStatus;
	}

	public void setProcDocStatus(String msProcDocStatus)
	{
		this.msProcDocStatus = msProcDocStatus;
	}

	public String getDocTitle()
	{
		return msDocTitle;
	}

	public void setDocTitle(String msDocTitle)
	{
		this.msDocTitle = msDocTitle;
	}

	// Changes in R5
	@Override
	public String toString()
	{
		return "DocumentBean [msOrgID=" + msOrgID + ", msUserID=" + msUserID + ", msSectionID=" + msSectionID
				+ ", msAppID=" + msAppID + ", msDocCategory=" + msDocCategory + ", msDocID=" + msDocID + ", msDocType="
				+ msDocType + ", msDocStatus=" + msDocStatus + ", msModifiedDate=" + msModifiedDate + ", msModifiedBy="
				+ msModifiedBy + ", msCreatedDate=" + msCreatedDate + ", msCreatedBy=" + msCreatedBy
				+ ", msQuestionID=" + msQuestionID + ", msFormID=" + msFormID + ", msFormName=" + msFormName
				+ ", msFormVersion=" + msFormVersion + ", msProcDocStatus=" + msProcDocStatus + ", msDocTitle="
				+ msDocTitle + ", msCeoName=" + msCeoName + ", msCfoName=" + msCfoName + ", mbIsCeoName=" + mbIsCeoName
				+ ", mbIsCfoName=" + mbIsCfoName + ", entityLinked=" + entityLinked + ", userSubRole=" + userSubRole + ", mbIsCeoActive=" + mbIsCeoActive + "]";
	}

	public String getEntityLinked()
	{
		return entityLinked;
	}

	public void setEntityLinked(String entityLinked)
	{
		this.entityLinked = entityLinked;
	}

	public String getDate()
	{
		return date;
	}

	public void setDate(String date)
	{
		this.date = date;
	}

	/**
	 * @return the permissionType
	 */
	public String getPermissionType()
	{
		return permissionType;
	}

	/**
	 * @param permissionType the permissionType to set
	 */
	public void setPermissionType(String permissionType)
	{
		this.permissionType = permissionType;
	}

	/**
	 * @return the userRole
	 */
	public String getUserRole()
	{
		return userRole;
	}

	/**
	 * @param userRole the userRole to set
	 */
	public void setUserRole(String userRole)
	{
		this.userRole = userRole;
	}

	/**
	 * @return the serviceAppId
	 */
	public String getServiceAppId()
	{
		return serviceAppId;
	}

	/**
	 * @param serviceAppId the serviceAppId to set
	 */
	public void setServiceAppId(String serviceAppId)
	{
		this.serviceAppId = serviceAppId;
	}

	/**
	 * @return the appId
	 */
	public String getAppId()
	{
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId)
	{
		this.appId = appId;
	}

	/**
	 * @return the contractAccess
	 */
	public String getContractAccess()
	{
		return contractAccess;
	}

	/**
	 * @param contractAccess the contractAccess to set
	 */
	public void setContractAccess(String contractAccess)
	{
		this.contractAccess = contractAccess;
	}
	// R5 Changes ends

	public String getUserSubRole() {
		return userSubRole;
	}

	public void setUserSubRole(String userSubRole) {
		this.userSubRole = userSubRole;
	}
}
