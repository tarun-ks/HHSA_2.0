package com.nyc.hhs.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.internet.MimeBodyPart;

import com.nyc.hhs.constants.HHSConstants;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is used to store multiple values and document properties which are
 * later used to generate the document grid.
 */

public class Document extends BaseFilter
{
	// added in R5
	private String lsReplicationId;
	//New Filling Check after 4.0.2, to set delete flag to 0 or 1 
	private boolean deletedViaParent;
	public boolean isDeletedViaParent() {
		return deletedViaParent;
	}

	public void setDeletedViaParent(boolean deletedViaParent) {
		this.deletedViaParent = deletedViaParent;
	}

	private String lsFiledInPath;

	public String getFiledInPath()
	{
		return lsFiledInPath;
	}

	public void setFiledInPath(String lsFiledInPath)
	{
		this.lsFiledInPath = lsFiledInPath;
	}

	public String getReplicationId()
	{
		return lsReplicationId;
	}

	public void setReplicationId(String lsReplicationId)
	{
		this.lsReplicationId = lsReplicationId;
	}

	private String lsPath;

	public String getPath()
	{
		return lsPath;
	}

	public void setPath(String lsPath)
	{
		this.lsPath = lsPath;
	}

	private String lsHelpDocDesc;
	private String lsCurrentOrgId;
	private String lsSharedEntityId;

	public String getCurrentOrgId()
	{
		return lsCurrentOrgId;
	}

	public String getSharedEntityId()
	{
		return lsSharedEntityId;
	}

	public void setSharedEntityId(String lsSharedEntityId)
	{
		this.lsSharedEntityId = lsSharedEntityId;
	}

	public void setCurrentOrgId(String lsCurrentOrgId)
	{
		this.lsCurrentOrgId = lsCurrentOrgId;
	}

	private String lsProviderId;

	public String getProviderId()
	{
		return lsProviderId;
	}

	public void setProviderId(String lsProviderId)
	{
		this.lsProviderId = lsProviderId;
	}

	private String sharingOrgName;

	public String getSharingOrgName()
	{
		return sharingOrgName;
	}

	public void setSharingOrgName(String sharingOrgName)
	{
		this.sharingOrgName = sharingOrgName;
	}

	private String lsHelpRadioButton;

	public String getHelpRadioButton()
	{
		return lsHelpRadioButton;
	}

	public void setHelpRadioButton(String lsHelpRadioButton)
	{
		this.lsHelpRadioButton = lsHelpRadioButton;
	}

	public String getHelpDocDesc()
	{
		return lsHelpDocDesc;
	}

	public void setHelpDocDesc(String lsHelpDocDesc)
	{
		this.lsHelpDocDesc = lsHelpDocDesc;
	}

	private String lsSharedBy;

	public String getOrgName()
	{
		return lsSharedBy;
	}

	public void setOrgName(String lsSharedBy)
	{
		this.lsSharedBy = lsSharedBy;
	}

	private boolean mbpermamantDeleteFromDb;

	public boolean isPermamantDeleteFromDb()
	{
		return mbpermamantDeleteFromDb;
	}

	public void setPermamantDeleteFromDb(boolean mbpermamantDeleteFromDb)
	{
		this.mbpermamantDeleteFromDb = mbpermamantDeleteFromDb;
	}

	private boolean mbChar500Flag;

	public boolean isChar500Flag()
	{
		return mbChar500Flag;
	}

	public void setChar500Flag(boolean mbChar500Flag)
	{
		this.mbChar500Flag = mbChar500Flag;
	}

	private String msMoveFromPath;
	private String msDeletedBy;

	public String getDeletedBy()
	{
		return msDeletedBy;
	}

	public void setDeletedBy(String msDeletedBy)
	{
		this.msDeletedBy = msDeletedBy;
	}

	public String getMoveFromPath()
	{
		return msMoveFromPath;
	}

	public void setMoveFromPath(String msMoveFromPath)
	{
		this.msMoveFromPath = msMoveFromPath;
	}

	private String msMoveToPath;

	public String getMoveToPath()
	{
		return msMoveToPath;
	}

	public void setMoveToPath(String msMoveToPath)
	{
		this.msMoveToPath = msMoveToPath;
	}

	private String msDeletedDate;

	public String getDeletedDate()
	{
		return msDeletedDate;
	}

	public void setDeletedDate(String msDeletedDate)
	{
		this.msDeletedDate = msDeletedDate;
	}

	private com.filenet.api.core.Folder msParent;
	private String msRBPath;

	public String getRBPath()
	{
		return msRBPath;
	}

	public void setRBPath(String msRBPath)
	{
		this.msRBPath = msRBPath;
	}

	private int msDocumentCount;

	public Integer getDocumentCount()
	{
		return msDocumentCount;
	}

	public void setDocumentCount(Integer msDocumentCount)
	{
		this.msDocumentCount = msDocumentCount;
	}

	private boolean mbShowInRB;

	public Boolean getShowInRB()
	{
		return mbShowInRB;
	}

	public void setShowInRB(Boolean mbShowInRB)
	{
		this.mbShowInRB = mbShowInRB;
	}

	private Integer msDeleteFlag;
	private String msDeletionEntityId;

	public String getDeletionEntityId()
	{
		return msDeletionEntityId;
	}

	public void setDeletionEntityId(String msDeletionEntityId)
	{
		this.msDeletionEntityId = msDeletionEntityId;
	}

	public Integer getDeleteFlag()
	{
		return msDeleteFlag;
	}

	public void setDeleteFlag(Integer msdDeleteFlag)
	{
		this.msDeleteFlag = msdDeleteFlag;
	}

	// R5 Ends
	@Length(max = 20)
	private String msOrganizationId;
	private String msDocCategory;
	private String msDocType;
	private String msDocName;
	private String msFileType;
	private String msContentType;
	private String msFilePath;
	private String msStatus;
	private MimeBodyPart moFiledata;
	@Length(max = 20)
	private String msApplicationId;
	@Length(max = 40)
	private String msDocumentId;
	private Date moEffectiveDate;
	private String msEffDate;
	private String msLastModifiedBy;
	private Date moLastModifiedDate;
	private String msActions;
	private Integer miSeqNo;
	private String msDate;
	private Integer miVersionNo;
	private String msShareStatus;
	private ArrayList<String> moCategoryList;
	private ArrayList<String> moTypeList;
	private List<DocumentPropertiesBean> moDocumentProperties;
	private String msUserOrg;
	private String msUserId;
	private String msCategoryString;
	private String msFilterModifiedFrom;
	private String msFilterModifiedTo;
	private String msFilterProviderId;
	private String msFilterNYCAgency;
	private boolean mbLinkToApplication;
	private boolean mbIsDocumentShared;

	private String msSubmissionBy;
	private Date moSubmissionDate;
	private String msFormId;
	private String msFormName;
	private String msFormVersion;

	private String msDocumentDescription;
	private ArrayList<String> moHelpCategoryList;
	private ArrayList<String> moSampleCategoryList;
	private ArrayList<String> moSampleTypeList;
	private String msSampleCategory;
	private String msSampleType;
	private ArrayList<String> moImplementationStatus;
	private String msServiceAppID;
	private String msSectionId;
	private String msHelpCategory;
	private String msDocDescription;
	private boolean msDisplayHelpOnApp;
	private String msDocShareStatus;
	private String msReadOnly = "";

	private String msAgencyId;
	private String msAgencyName = "";

	private String msFilterDocCategory;
	private String msFilterDocType;

	private boolean mbIsCeo = true;
	private boolean mbIsCfo = true;

	private String msCeoName;
	private String msCfoName;
	private boolean mbIsCeoName = false;
	private String msFilterSampleCategory;
	private String msFilterSampleType;
	// New Added variables as part of Defect #1805 fix
	private String msEntityId;
	private String msEntityType;
	private String msServiceDocumentName;
	private boolean mbIsStaffFundername;
	// Added for R5
	private String msLinkStatus;
	private String msfileOptions;
	private String msSharedWith;
	// Added for Release 5- document create property
	private String msCreatedBy;
	private String msCreatedDate;

	// Added folder properties
	private String msFolderName;
	private String msFolderLocation = HHSConstants.EMPTY_STRING;
	private int miFolderCount;

	private String maskedDocType;
	private String permissionType;
	private String userRole;
	// QC 8914 R7.2 read only role  
	private String userSubRole;
	

	// added for R5
	public String getMaskedDocType()
	{
		return maskedDocType;
	}

	public void setMaskedDocType(String maskedDocType)
	{
		this.maskedDocType = maskedDocType;
	}

	public com.filenet.api.core.Folder getParent()
	{
		return msParent;
	}

	public void setParent(com.filenet.api.core.Folder msParent)
	{
		this.msParent = msParent;
	}

	public String getSharedWith()
	{
		return msSharedWith;
	}

	public void setSharedWith(String msSharedWith)
	{
		this.msSharedWith = msSharedWith;
	}

	public String getFolderName()
	{
		return msFolderName;
	}

	public void setFolderName(String asFolderName)
	{
		this.msFolderName = asFolderName;
	}

	public String getFolderLocation()
	{
		return msFolderLocation;
	}

	public void setFolderLocation(String asFolderLocation)
	{
		this.msFolderLocation = asFolderLocation;
	}

	public int getFolderCount()
	{
		return miFolderCount;
	}

	public void setFolderCount(int aiFolderCount)
	{
		this.miFolderCount = aiFolderCount;
	}

	public String getCreatedBy()
	{
		return msCreatedBy;
	}

	public void setCreatedBy(String asCreatedBy)
	{
		this.msCreatedBy = asCreatedBy;
	}

	public String getCreatedDate()
	{
		return msCreatedDate;
	}

	public void setCreatedDate(String aoCreatedDate)
	{
		this.msCreatedDate = aoCreatedDate;
	}

	// R5 Ends
	public String getLinkStatus()
	{
		return msLinkStatus;
	}

	public void setLinkStatus(String asLinkStatus)
	{
		this.msLinkStatus = asLinkStatus;
	}

	public String getFileOptions()
	{
		return msfileOptions;
	}

	public void setFileOptions(String asFileOptions)
	{
		this.msfileOptions = asFileOptions;
	}

	// End
	public boolean isMbIsStaffFundername()
	{
		return mbIsStaffFundername;
	}

	public void setMbIsStaffFundername(boolean mbIsStaffFundername)
	{
		this.mbIsStaffFundername = mbIsStaffFundername;
	}

	public String getMsServiceDocumentName()
	{
		return msServiceDocumentName;
	}

	public void setMsServiceDocumentName(String msServiceDocumentName)
	{
		this.msServiceDocumentName = msServiceDocumentName;
	}

	public String getMsEntityId()
	{
		return msEntityId;

	}

	public String getMsEntityType()
	{
		return msEntityType;
	}

	public void setMsEntityId(String asEntityId)
	{
		this.msEntityId = asEntityId;
	}

	public void setMsEntityType(String asEntityType)
	{
		this.msEntityType = asEntityType;
	}

	public String getMsCeoName()
	{
		return msCeoName;
	}

	public void setMsCeoName(String asCeoName)
	{
		this.msCeoName = asCeoName;
	}

	public String getMsCfoName()
	{
		return msCfoName;
	}

	public void setMsCfoName(String asCfoName)
	{
		this.msCfoName = asCfoName;
	}

	public boolean isMbIsCeoName()
	{
		return mbIsCeoName;
	}

	public void setMbIsCeoName(boolean abIsCeoName)
	{
		this.mbIsCeoName = abIsCeoName;
	}

	private boolean mbIsCfoName = false;

	public boolean isMbIsCfoName()
	{
		return mbIsCfoName;
	}

	public void setMbIsCfoName(boolean abIsCfoName)
	{
		this.mbIsCfoName = abIsCfoName;
	}

	private String msOrgType;

	public String getMsOrgType()
	{
		return msOrgType;
	}

	public void setMsOrgType(String asOrgType)
	{
		this.msOrgType = asOrgType;
	}

	public boolean isMbIsCeo()
	{
		return mbIsCeo;
	}

	public void setMbIsCeo(boolean abIsCeo)
	{
		this.mbIsCeo = abIsCeo;
	}

	public boolean isMbIsCfo()
	{
		return mbIsCfo;
	}

	public void setMbIsCfo(boolean abIsCfo)
	{
		this.mbIsCfo = abIsCfo;
	}

	public String getAgencyId()
	{
		return msAgencyId;
	}

	public void setAgencyId(String asAgencyId)
	{
		this.msAgencyId = asAgencyId;
	}

	public String getMsAgencyName()
	{
		return msAgencyName;
	}

	public void setMsAgencyName(String asAgencyName)
	{
		this.msAgencyName = asAgencyName;
	}

	public String getReadOnly()
	{
		return msReadOnly;
	}

	public void setReadOnly(String asReadOnly)
	{
		this.msReadOnly = asReadOnly;
	}

	public boolean isDisplayHelpOnApp()
	{
		return msDisplayHelpOnApp;
	}

	public void setDisplayHelpOnApp(boolean abDisplayHelpOnApp)
	{
		this.msDisplayHelpOnApp = abDisplayHelpOnApp;
	}

	public String getHelpCategory()
	{
		return msHelpCategory;
	}

	public void setHelpCategory(String asHelpCategory)
	{
		this.msHelpCategory = asHelpCategory;
	}

	public String getDocDescription()
	{
		return msDocDescription;
	}

	public void setDocDescription(String asDocDescription)
	{
		this.msDocDescription = asDocDescription;
	}

	public String getOrganizationId()
	{
		return msOrganizationId;
	}

	public void setOrganizationId(String asOrganizationId)
	{
		this.msOrganizationId = asOrganizationId;
	}

	public String getDocCategory()
	{
		return msDocCategory;
	}

	public void setDocCategory(String asDocCategory)
	{
		this.msDocCategory = asDocCategory;
	}

	public String getDocType()
	{
		return msDocType;
	}

	public void setDocType(String asDocType)
	{
		this.msDocType = asDocType;
	}

	public String getDocName()
	{
		return msDocName;
	}

	public void setDocName(String asDocName)
	{
		this.msDocName = asDocName;
	}

	public String getFileType()
	{
		return msFileType;
	}

	public void setFileType(String asFileType)
	{
		this.msFileType = asFileType;
	}

	public String getContentType()
	{
		return msContentType;
	}

	public void setContentType(String asContentType)
	{
		this.msContentType = asContentType;
	}

	public String getFilePath()
	{
		return msFilePath;
	}

	public void setFilePath(String asFilePath)
	{
		this.msFilePath = asFilePath;
	}

	public String getStatus()
	{
		return msStatus;
	}

	public void setStatus(String asStatus)
	{
		this.msStatus = asStatus;
	}

	public MimeBodyPart getFiledata()
	{
		return moFiledata;
	}

	public void setFiledata(MimeBodyPart aoFiledata)
	{
		this.moFiledata = aoFiledata;
	}

	public String getApplicationId()
	{
		return msApplicationId;
	}

	public void setApplicationId(String asApplicationId)
	{
		this.msApplicationId = asApplicationId;
	}

	public String getDocumentId()
	{
		return msDocumentId;
	}

	public void setDocumentId(String asDocumentId)
	{
		this.msDocumentId = asDocumentId;
	}

	public Date getEffectiveDate()
	{
		return moEffectiveDate;
	}

	public void setEffectiveDate(Date aoEffectiveDate)
	{
		this.moEffectiveDate = aoEffectiveDate;
	}

	public String getLastModifiedBy()
	{
		return msLastModifiedBy;
	}

	public void setLastModifiedBy(String asLastModifiedBy)
	{
		this.msLastModifiedBy = asLastModifiedBy;
	}

	public Date getLastModifiedDate()
	{
		return moLastModifiedDate;
	}

	public void setLastModifiedDate(Date aoLastModifiedDate)
	{
		this.moLastModifiedDate = aoLastModifiedDate;
	}

	public String getActions()
	{
		return msActions;
	}

	public void setActions(String asActions)
	{
		this.msActions = asActions;
	}

	public Integer getSeqNo()
	{
		return miSeqNo;
	}

	public void setSeqNo(Integer aiSeqNo)
	{
		this.miSeqNo = aiSeqNo;
	}

	public String getDate()
	{
		return msDate;
	}

	public void setDate(String asDate)
	{
		this.msDate = asDate;
	}

	public Integer getVersionNo()
	{
		return miVersionNo;
	}

	public void setVersionNo(Integer aiVersionNo)
	{
		this.miVersionNo = aiVersionNo;
	}

	public String getShareStatus()
	{
		return msShareStatus;
	}

	public void setShareStatus(String asShareStatus)
	{
		this.msShareStatus = asShareStatus;
	}

	public ArrayList<String> getCategoryList()
	{
		return moCategoryList;
	}

	public void setCategoryList(ArrayList<String> aoCategoryList)
	{
		this.moCategoryList = aoCategoryList;
	}

	public ArrayList<String> getTypeList()
	{
		return moTypeList;
	}

	public void setTypeList(ArrayList<String> aoTypeList)
	{
		this.moTypeList = aoTypeList;
	}

	public List<DocumentPropertiesBean> getDocumentProperties()
	{
		return moDocumentProperties;
	}

	public void setDocumentProperties(List<DocumentPropertiesBean> aoDocumentProperties)
	{
		this.moDocumentProperties = aoDocumentProperties;
	}

	public String getUserOrg()
	{
		return msUserOrg;
	}

	public void setUserOrg(String asUserOrg)
	{
		this.msUserOrg = asUserOrg;
	}

	public String getEffDate()
	{
		return msEffDate;
	}

	public void setEffDate(String asEffDate)
	{
		this.msEffDate = asEffDate;
	}

	public String getCategoryString()
	{
		return msCategoryString;
	}

	public void setCategoryString(String asCategoryString)
	{
		this.msCategoryString = asCategoryString;
	}

	public String getFilterModifiedFrom()
	{
		return msFilterModifiedFrom;
	}

	public void setFilterModifiedFrom(String asFilterModifiedFrom)
	{
		this.msFilterModifiedFrom = asFilterModifiedFrom;
	}

	public String getFilterModifiedTo()
	{
		return msFilterModifiedTo;
	}

	public void setFilterModifiedTo(String asFilterModifiedTo)
	{
		this.msFilterModifiedTo = asFilterModifiedTo;
	}

	public String getFilterProviderId()
	{
		return msFilterProviderId;
	}

	public void setFilterProviderId(String asFilterProviderId)
	{
		this.msFilterProviderId = asFilterProviderId;
	}

	public String getFilterNYCAgency()
	{
		return msFilterNYCAgency;
	}

	public void setFilterNYCAgency(String asFilterNYCAgency)
	{
		this.msFilterNYCAgency = asFilterNYCAgency;
	}

	public boolean isLinkToApplication()
	{
		return mbLinkToApplication;
	}

	public void setLinkToApplication(boolean abLinkToApplication)
	{
		this.mbLinkToApplication = abLinkToApplication;
	}

	public String getSubmissionBy()
	{
		return msSubmissionBy;
	}

	public void setSubmissionBy(String asSubmissionBy)
	{
		this.msSubmissionBy = asSubmissionBy;
	}

	public Date getSubmissionDate()
	{
		return moSubmissionDate;
	}

	public void setSubmissionDate(Date aoSubmissionDate)
	{
		this.moSubmissionDate = aoSubmissionDate;
	}

	public String getFormId()
	{
		return msFormId;
	}

	public void setFormId(String asFormId)
	{
		this.msFormId = asFormId;
	}

	public String getFormName()
	{
		return msFormName;
	}

	public void setFormName(String asFormName)
	{
		this.msFormName = asFormName;
	}

	public String getFormVersion()
	{
		return msFormVersion;
	}

	public void setFormVersion(String asFormVersion)
	{
		this.msFormVersion = asFormVersion;
	}

	public boolean isDocumentShared()
	{
		return mbIsDocumentShared;
	}

	public void setDocumentShared(boolean abIsDocumentShared)
	{
		this.mbIsDocumentShared = abIsDocumentShared;
	}

	public String getDocumentDescription()
	{
		return msDocumentDescription;
	}

	public void setDocumentDescription(String asDocumentDescription)
	{
		this.msDocumentDescription = asDocumentDescription;
	}

	public ArrayList<String> getHelpCategoryList()
	{
		return moHelpCategoryList;
	}

	public void setHelpCategoryList(ArrayList<String> aoHelpCategoryList)
	{
		this.moHelpCategoryList = aoHelpCategoryList;
	}

	public ArrayList<String> getSampleCategoryList()
	{
		return moSampleCategoryList;
	}

	public void setSampleCategoryList(ArrayList<String> asSampleCategoryList)
	{
		this.moSampleCategoryList = asSampleCategoryList;
	}

	public ArrayList<String> getSampleTypeList()
	{
		return moSampleTypeList;
	}

	public void setSampleTypeList(ArrayList<String> aoSampleTypeList)
	{
		this.moSampleTypeList = aoSampleTypeList;
	}

	public String getSampleCategory()
	{
		return msSampleCategory;
	}

	public void setSampleCategory(String asSampleCategory)
	{
		this.msSampleCategory = asSampleCategory;
	}

	public String getSampleType()
	{
		return msSampleType;
	}

	public void setSampleType(String asSampleType)
	{
		this.msSampleType = asSampleType;
	}

	public String getUserId()
	{
		return msUserId;
	}

	public void setUserId(String asUserId)
	{
		this.msUserId = asUserId;
	}

	public ArrayList<String> getImplementationStatus()
	{
		return moImplementationStatus;
	}

	public void setImplementationStatus(ArrayList<String> aoImplementationStatus)
	{
		this.moImplementationStatus = aoImplementationStatus;
	}

	public String getServiceAppID()
	{
		return msServiceAppID;
	}

	public void setServiceAppID(String asServiceAppID)
	{
		this.msServiceAppID = asServiceAppID;
	}

	public String getSectionId()
	{
		return msSectionId;
	}

	public void setSectionId(String asSectionId)
	{
		this.msSectionId = asSectionId;
	}

	public String getDocSharedStatus()
	{
		return msDocShareStatus;
	}

	public void setDocSharedStatus(String asDocSharedStatus)
	{
		this.msDocShareStatus = asDocSharedStatus;
	}

	public String getFilterDocCategory()
	{
		return msFilterDocCategory;
	}

	public void setFilterDocCategory(String asFilterDocCategory)
	{
		this.msFilterDocCategory = asFilterDocCategory;
	}

	public String getFilterDocType()
	{
		return msFilterDocType;
	}

	public void setFilterDocType(String asFilterDocType)
	{
		this.msFilterDocType = asFilterDocType;
	}

	/**
	 * @return the msFilterSampleCategory
	 */
	public String getFilterSampleCategory()
	{
		return msFilterSampleCategory;
	}

	/**
	 * @param msFilterSampleCategory the msFilterSampleCategory to set
	 */
	public void setFilterSampleCategory(String msFilterSampleCategory)
	{
		this.msFilterSampleCategory = msFilterSampleCategory;
	}

	/**
	 * @return the msFilterSampleType
	 */
	public String getFilterSampleType()
	{
		return msFilterSampleType;
	}

	/**
	 * @param msFilterSampleType the msFilterSampleType to set
	 */
	public void setFilterSampleType(String msFilterSampleType)
	{
		this.msFilterSampleType = msFilterSampleType;
	}

	@Override
	public String toString()
	{
		return "Document [lsHelpDocDesc=" + lsHelpDocDesc + ", lsProviderId=" + lsProviderId + ", sharingOrgName="
				+ sharingOrgName + ", lsHelpRadioButton=" + lsHelpRadioButton + ", lsSharedBy=" + lsSharedBy
				+ ", mbpermamantDeleteFromDb=" + mbpermamantDeleteFromDb + ", mbChar500Flag=" + mbChar500Flag
				+ ", msMoveFromPath=" + msMoveFromPath + ", msDeletedBy=" + msDeletedBy + ", msMoveToPath="
				+ msMoveToPath + ", msDeletedDate=" + msDeletedDate + ", msParent=" + msParent + ", msRBPath="
				+ msRBPath + ", msDocumentCount=" + msDocumentCount + ", mbShowInRB=" + mbShowInRB + ", msDeleteFlag="
				+ msDeleteFlag + ", msDeletionEntityId=" + msDeletionEntityId + ", msOrganizationId="
				+ msOrganizationId + ", msDocCategory=" + msDocCategory + ", msDocType=" + msDocType + ", msDocName="
				+ msDocName + ", msFileType=" + msFileType + ", msContentType=" + msContentType + ", msFilePath="
				+ msFilePath + ", msStatus=" + msStatus + ", moFiledata=" + moFiledata + ", msApplicationId="
				+ msApplicationId + ", msDocumentId=" + msDocumentId + ", moEffectiveDate=" + moEffectiveDate
				+ ", msEffDate=" + msEffDate + ", msLastModifiedBy=" + msLastModifiedBy + ", moLastModifiedDate="
				+ moLastModifiedDate + ", msActions=" + msActions + ", miSeqNo=" + miSeqNo + ", msDate=" + msDate
				+ ", miVersionNo=" + miVersionNo + ", msShareStatus=" + msShareStatus + ", moCategoryList="
				+ moCategoryList + ", moTypeList=" + moTypeList + ", moDocumentProperties=" + moDocumentProperties
				+ ", msUserOrg=" + msUserOrg + ", msUserId=" + msUserId + ", msCategoryString=" + msCategoryString
				+ ", msFilterModifiedFrom=" + msFilterModifiedFrom + ", msFilterModifiedTo=" + msFilterModifiedTo
				+ ", msFilterProviderId=" + msFilterProviderId + ", msFilterNYCAgency=" + msFilterNYCAgency
				+ ", mbLinkToApplication=" + mbLinkToApplication + ", mbIsDocumentShared=" + mbIsDocumentShared
				+ ", msSubmissionBy=" + msSubmissionBy + ", moSubmissionDate=" + moSubmissionDate + ", msFormId="
				+ msFormId + ", msFormName=" + msFormName + ", msFormVersion=" + msFormVersion
				+ ", msDocumentDescription=" + msDocumentDescription + ", moHelpCategoryList=" + moHelpCategoryList
				+ ", moSampleCategoryList=" + moSampleCategoryList + ", moSampleTypeList=" + moSampleTypeList
				+ ", msSampleCategory=" + msSampleCategory + ", msSampleType=" + msSampleType
				+ ", moImplementationStatus=" + moImplementationStatus + ", msServiceAppID=" + msServiceAppID
				+ ", msSectionId=" + msSectionId + ", msHelpCategory=" + msHelpCategory + ", msDocDescription="
				+ msDocDescription + ", msDisplayHelpOnApp=" + msDisplayHelpOnApp + ", msDocShareStatus="
				+ msDocShareStatus + ", msReadOnly=" + msReadOnly + ", msAgencyId=" + msAgencyId + ", msAgencyName="
				+ msAgencyName + ", msFilterDocCategory=" + msFilterDocCategory + ", msFilterDocType="
				+ msFilterDocType + ", mbIsCeo=" + mbIsCeo + ", mbIsCfo=" + mbIsCfo + ", msCeoName=" + msCeoName
				+ ", msCfoName=" + msCfoName + ", mbIsCeoName=" + mbIsCeoName + ", msFilterSampleCategory="
				+ msFilterSampleCategory + ", msFilterSampleType=" + msFilterSampleType + ", msEntityId=" + msEntityId
				+ ", msEntityType=" + msEntityType + ", msServiceDocumentName=" + msServiceDocumentName
				+ ", mbIsStaffFundername=" + mbIsStaffFundername + ", msLinkStatus=" + msLinkStatus
				+ ", msfileOptions=" + msfileOptions + ", msSharedWith=" + msSharedWith + ", msCreatedBy="
				+ msCreatedBy + ", msCreatedDate=" + msCreatedDate + ", msFolderName=" + msFolderName
				+ ", msFolderLocation=" + msFolderLocation + ", miFolderCount=" + miFolderCount + ", mbIsCfoName="
				+ mbIsCfoName + ", userSubRole=" + userSubRole + ", msOrgType=" + msOrgType + "]";
	}

	// added for R5
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

	// R5 ends

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((msDocumentId == null) ? 0 : msDocumentId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		Document loother = (Document) obj;
		if (msDocumentId == null)
		{
			if (loother.msDocumentId != null)
				return false;
		}
		else if (!msDocumentId.equals(loother.msDocumentId))
			return false;
		return true;
	}

	public String getUserSubRole() {
		return userSubRole;
	}

	public void setUserSubRole(String userSubRole) {
		this.userSubRole = userSubRole;
	}
}
