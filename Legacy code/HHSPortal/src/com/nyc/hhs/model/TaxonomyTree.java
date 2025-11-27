package com.nyc.hhs.model;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

/**
 * This class is a bean which maintains the Taxonomy tree information.
 * 
 */

public class TaxonomyTree
{
	@Length(max = 50)
	private String msElementid;
	private String msBranchid;
	private String msElementName;
	private String msElementType;
	private String msParentid;
	private String msElementDescription;
	private String msEvidenceReqd; // evidence required
	private String msActiveFlag; // taxonomy item
	private String msSelectionFlag; // approval required
	private String msChildId;
	private String msTaxonomyId;
	@Length(max = 50)
	private String msLinkageId;
	private String msDisplayName;
	private String msLevel;
	private String msCurrentElementId;
	private String msIsDuplicate;
	private String msDeleteStatus;
	private List<TaxonomyLinkageBean> msLinkageList;
	private List<TaxonomySynonymBean> msSynonymList;
	private boolean mbEvidenceChanged;
	private boolean mbApprovalChanged;
	private boolean mbTaxonomyChanged;
	private String msTransactionEvent;
	private String msStatus;
	private Date moModifiedDate;
	private String msLeaf;
	private String selectedPopulation;
	private String ageFrom;
	private String ageTo;
	private String otherData;

	private String msTaxTranRecStatus;
	private String msTaxTranRecEvent;
	private List<String> msParentElementIdList;

	private String msParElmntIdLst;

	private String msEvedenceToNonEvedenceLst;
	private String msApprovalToNonApprovalLst;
	private String msServiceStatus;

	private String msModifyBy;
	private String msCreatedBy;
	private Date moCreatedDate;
	private Date moTMModifiedDate;
	private String msEvidenceCount;

	private String msCount = " ";
	private Timestamp moTimeStamp;

	public String getOtherData()
	{
		return otherData;
	}

	public void setOtherData(String otherData)
	{
		this.otherData = otherData;
	}

	public String getAgeFrom()
	{
		return ageFrom;
	}

	public void setAgeFrom(String ageFrom)
	{
		this.ageFrom = ageFrom;
	}

	public String getAgeTo()
	{
		return ageTo;
	}

	public void setAgeTo(String ageTo)
	{
		this.ageTo = ageTo;
	}

	public String getSelectedPopulation()
	{
		return selectedPopulation;
	}

	public void setSelectedPopulation(String selectedPopulation)
	{
		this.selectedPopulation = selectedPopulation;
	}

	public List<TaxonomyLinkageBean> getMsLinkageList()
	{
		return msLinkageList;
	}

	public void setMsLinkageList(List<TaxonomyLinkageBean> msLinkageList)
	{
		this.msLinkageList = msLinkageList;
	}

	public List<TaxonomySynonymBean> getMsSynonymList()
	{
		return msSynonymList;
	}

	public void setMsSynonymList(List<TaxonomySynonymBean> msSynonymList)
	{
		this.msSynonymList = msSynonymList;
	}

	public String getMsDisplayName()
	{
		return msDisplayName;
	}

	public void setMsDisplayName(String msDisplayName)
	{
		this.msDisplayName = msDisplayName;
	}

	public String getMsChildId()
	{
		return msChildId;
	}

	public void setMsChildId(String msChildId)
	{
		this.msChildId = msChildId;
	}

	public String getMsTaxonomyId()
	{
		return msTaxonomyId;
	}

	public void setMsTaxonomyId(String msTaxonomyId)
	{
		this.msTaxonomyId = msTaxonomyId;
	}

	public String getMsLinkageId()
	{
		return msLinkageId;
	}

	public void setMsLinkageId(String msLinkageId)
	{
		this.msLinkageId = msLinkageId;
	}

	public String getMsElementid()
	{
		return msElementid;
	}

	public void setMsElementid(String msElementid)
	{
		this.msElementid = msElementid;
	}

	public String getMsBranchid()
	{
		return msBranchid;
	}

	public void setMsBranchid(String msBranchid)
	{
		this.msBranchid = msBranchid;
	}

	public String getMsElementName()
	{
		return msElementName;
	}

	public void setMsElementName(String msElementName)
	{
		this.msElementName = msElementName;
	}

	public String getMsElementType()
	{
		return msElementType;
	}

	public void setMsElementType(String msElementType)
	{
		this.msElementType = msElementType;
	}

	public String getMsParentid()
	{
		return msParentid;
	}

	public void setMsParentid(String msParentid)
	{
		this.msParentid = msParentid;
	}

	public String getMsElementDescription()
	{
		return msElementDescription;
	}

	public void setMsElementDescription(String msElementDescription)
	{
		this.msElementDescription = msElementDescription;
	}

	public String getMsEvidenceReqd()
	{
		return msEvidenceReqd;
	}

	public void setMsEvidenceReqd(String msEvidenceReqd)
	{
		this.msEvidenceReqd = msEvidenceReqd;
	}

	public String getMsActiveFlag()
	{
		return msActiveFlag;
	}

	public void setMsActiveFlag(String msActiveFlag)
	{
		this.msActiveFlag = msActiveFlag;
	}

	public String getMsSelectionFlag()
	{
		return msSelectionFlag;
	}

	public void setMsSelectionFlag(String msSelectionFlag)
	{
		this.msSelectionFlag = msSelectionFlag;
	}

	public String getChildId()
	{
		return msChildId;
	}

	public void setChildId(String aoChildId)
	{
		this.msChildId = aoChildId;
	}

	public String getMsLevel()
	{
		return msLevel;
	}

	public void setMsLevel(String msLevel)
	{
		this.msLevel = msLevel;
	}

	public String getMsCurrentElementId()
	{
		return msCurrentElementId;
	}

	public void setMsCurrentElementId(String msCurrentElementId)
	{
		this.msCurrentElementId = msCurrentElementId;
	}

	public String getMsIsDuplicate()
	{
		return msIsDuplicate;
	}

	public void setMsIsDuplicate(String msIsDuplicate)
	{
		this.msIsDuplicate = msIsDuplicate;
	}

	public String getMsDeleteStatus()
	{
		return msDeleteStatus;
	}

	public void setMsDeleteStatus(String msDeleteStatus)
	{
		this.msDeleteStatus = msDeleteStatus;
	}

	public boolean isMbEvidenceChanged()
	{
		return mbEvidenceChanged;
	}

	public void setMbEvidenceChanged(boolean mbEvidenceChanged)
	{
		this.mbEvidenceChanged = mbEvidenceChanged;
	}

	public boolean isMbApprovalChanged()
	{
		return mbApprovalChanged;
	}

	public void setMbApprovalChanged(boolean mbApprovalChanged)
	{
		this.mbApprovalChanged = mbApprovalChanged;
	}

	public boolean isMbTaxonomyChanged()
	{
		return mbTaxonomyChanged;
	}

	public void setMbTaxonomyChanged(boolean mbTaxonomyChanged)
	{
		this.mbTaxonomyChanged = mbTaxonomyChanged;
	}

	public String getMsTransactionEvent()
	{
		return msTransactionEvent;
	}

	public void setMsTransactionEvent(String msTransactionEvent)
	{
		this.msTransactionEvent = msTransactionEvent;
	}

	public String getMsStatus()
	{
		return msStatus;
	}

	public void setMsStatus(String msStatus)
	{
		this.msStatus = msStatus;
	}

	public Date getMoModifiedDate()
	{
		return moModifiedDate;
	}

	public void setMoModifiedDate(Date moModifiedDate)
	{
		this.moModifiedDate = moModifiedDate;
	}

	public String getMsLeaf()
	{
		return msLeaf;
	}

	public void setMsLeaf(String msLeaf)
	{
		this.msLeaf = msLeaf;
	}

	public String getMsTaxTranRecStatus()
	{
		return msTaxTranRecStatus;
	}

	public void setMsTaxTranRecStatus(String msTaxTranRecStatus)
	{
		this.msTaxTranRecStatus = msTaxTranRecStatus;
	}

	public String getMsTaxTranRecEvent()
	{
		return msTaxTranRecEvent;
	}

	public void setMsTaxTranRecEvent(String msTaxTranRecEvent)
	{
		this.msTaxTranRecEvent = msTaxTranRecEvent;
	}

	public List<String> getMsParentElementIdList()
	{
		return msParentElementIdList;
	}

	public void setMsParentElementIdList(List<String> msParentElementIdList)
	{
		this.msParentElementIdList = msParentElementIdList;
	}

	public String getMsParElmntIdLst()
	{
		return msParElmntIdLst;
	}

	public void setMsParElmntIdLst(String msParElmntIdLst)
	{
		this.msParElmntIdLst = msParElmntIdLst;
	}

	public String getMsEvedenceToNonEvedenceLst()
	{
		return msEvedenceToNonEvedenceLst;
	}

	public void setMsEvedenceToNonEvedenceLst(String msEvedenceToNonEvedenceLst)
	{
		this.msEvedenceToNonEvedenceLst = msEvedenceToNonEvedenceLst;
	}

	public String getMsApprovalToNonApprovalLst()
	{
		return msApprovalToNonApprovalLst;
	}

	public void setMsApprovalToNonApprovalLst(String msApprovalToNonApprovalLst)
	{
		this.msApprovalToNonApprovalLst = msApprovalToNonApprovalLst;
	}

	public String getMsServiceStatus()
	{
		return msServiceStatus;
	}

	public void setMsServiceStatus(String msServiceStatus)
	{
		this.msServiceStatus = msServiceStatus;
	}

	/**
	 * @return the msModifyBy
	 */
	public String getMsModifyBy()
	{
		return msModifyBy;
	}

	/**
	 * @param msModifyBy
	 *            the msModifyBy to set
	 */
	public void setMsModifyBy(String msModifyBy)
	{
		this.msModifyBy = msModifyBy;
	}

	/**
	 * @return the msCreatedBy
	 */
	public String getMsCreatedBy()
	{
		return msCreatedBy;
	}

	/**
	 * @param msCreatedBy
	 *            the msCreatedBy to set
	 */
	public void setMsCreatedBy(String msCreatedBy)
	{
		this.msCreatedBy = msCreatedBy;
	}

	/**
	 * @return the moCreatedDate
	 */
	public Date getMoCreatedDate()
	{
		return moCreatedDate;
	}

	/**
	 * @param moCreatedDate
	 *            the moCreatedDate to set
	 */
	public void setMoCreatedDate(Date moCreatedDate)
	{
		this.moCreatedDate = moCreatedDate;
	}

	/**
	 * @return the moTMModifiedDate
	 */
	public Date getMoTMModifiedDate()
	{
		return moTMModifiedDate;
	}

	/**
	 * @param moTMModifiedDate
	 *            the moTMModifiedDate to set
	 */
	public void setMoTMModifiedDate(Date moTMModifiedDate)
	{
		this.moTMModifiedDate = moTMModifiedDate;
	}

	/**
	 * @return the msEvidenceCount
	 */
	public String getMsEvidenceCount()
	{
		return msEvidenceCount;
	}

	/**
	 * @param msEvidenceCount
	 *            the msEvidenceCount to set
	 */
	public void setMsEvidenceCount(String msEvidenceCount)
	{
		this.msEvidenceCount = msEvidenceCount;
	}

	/**
	 * @return the msCount
	 */
	public String getMsCount()
	{
		return msCount;
	}

	/**
	 * @param msCount
	 *            the msCount to set
	 */
	public void setMsCount(String msCount)
	{
		this.msCount = msCount;
	}

	/**
	 * @return the moTimeStamp
	 */
	public Timestamp getMoTimeStamp()
	{
		return moTimeStamp;
	}

	/**
	 * @param moTimeStamp
	 *            the moTimeStamp to set
	 */
	public void setMoTimeStamp(Timestamp moTimeStamp)
	{
		this.moTimeStamp = moTimeStamp;
	}

	@Override
	public String toString() {
		return "TaxonomyTree [msElementid=" + msElementid + ", msBranchid="
				+ msBranchid + ", msElementName=" + msElementName
				+ ", msElementType=" + msElementType + ", msParentid="
				+ msParentid + ", msElementDescription=" + msElementDescription
				+ ", msEvidenceReqd=" + msEvidenceReqd + ", msActiveFlag="
				+ msActiveFlag + ", msSelectionFlag=" + msSelectionFlag
				+ ", msChildId=" + msChildId + ", msTaxonomyId=" + msTaxonomyId
				+ ", msLinkageId=" + msLinkageId + ", msDisplayName="
				+ msDisplayName + ", msLevel=" + msLevel
				+ ", msCurrentElementId=" + msCurrentElementId
				+ ", msIsDuplicate=" + msIsDuplicate + ", msDeleteStatus="
				+ msDeleteStatus + ", msLinkageList=" + msLinkageList
				+ ", msSynonymList=" + msSynonymList + ", mbEvidenceChanged="
				+ mbEvidenceChanged + ", mbApprovalChanged="
				+ mbApprovalChanged + ", mbTaxonomyChanged="
				+ mbTaxonomyChanged + ", msTransactionEvent="
				+ msTransactionEvent + ", msStatus=" + msStatus
				+ ", moModifiedDate=" + moModifiedDate + ", msLeaf=" + msLeaf
				+ ", selectedPopulation=" + selectedPopulation + ", ageFrom="
				+ ageFrom + ", ageTo=" + ageTo + ", otherData=" + otherData
				+ ", msTaxTranRecStatus=" + msTaxTranRecStatus
				+ ", msTaxTranRecEvent=" + msTaxTranRecEvent
				+ ", msParentElementIdList=" + msParentElementIdList
				+ ", msParElmntIdLst=" + msParElmntIdLst
				+ ", msEvedenceToNonEvedenceLst=" + msEvedenceToNonEvedenceLst
				+ ", msApprovalToNonApprovalLst=" + msApprovalToNonApprovalLst
				+ ", msServiceStatus=" + msServiceStatus + ", msModifyBy="
				+ msModifyBy + ", msCreatedBy=" + msCreatedBy
				+ ", moCreatedDate=" + moCreatedDate + ", moTMModifiedDate="
				+ moTMModifiedDate + ", msEvidenceCount=" + msEvidenceCount
				+ ", msCount=" + msCount + ", moTimeStamp=" + moTimeStamp + "]";
	}

}
