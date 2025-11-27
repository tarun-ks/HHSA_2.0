package com.nyc.hhs.model;

import java.sql.Timestamp;
import java.util.List;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/**
 * This class is bean class that maintains extended document details
 */
public class ExtendedDocument extends BaseFilter
{
	@Length(max = 40)
	private String documentId;
	private String status;
	@RegExp(value ="^\\d{0,22}")
	private String statusId;
	private String modifiedDate;
	private String lastModifiedById;
	private String lastModifiedByName;
	private String documentTitle;
	private String documentType;
	private String proposalId;
	private String requiredFlag;
	private String actions;
	private String documentCategory;
	private String createdDate;
	private String createdBy;
	@RegExp(value ="^\\d{0,22}")
	private String procurementStatusId;
	private String procurementId;
	private String documentSeqNumber;
	private Timestamp createdSqlDate;
	private Timestamp modifiedSqlDate;
	private String addendumType;
	private String documentCreatedDate;
	private String documentCreatedByUser;
	private String isAddendum;
	private String documentStatus;
	private String isRequiredDoc;
	private String referenceDocSeqNo;
	private String organizationType;
	private String assignStatus;
	private String proposalTitle;
	private String documentSeq;
	private String orgTypeInSession;
	private String tableName;
	private String proposalStatusId;
	private String userRole;
	private String procurementDocumentId;

	// added in build 2.6.0, defect id 5667
	private String docDeleteFlag;
	private String evaluationPoolMappingId;

	// added for 3.1.0 enhancement: 6025
	private String organizationId;
	private String awardId;
	private String contractId;
	private String procurementTitle;
	private List<String> docProposalIds;
	private String organizationName;
	private String addendumDocumentId;
	private String versionNo;
	private String quesVersion;
	private String docVersion;
	private String evalGrpQuesVersion;
	private String evalGrpDocVersion;
	// Made changes for enhancement 6467 release 3.4.0
	private Integer requiredQuesDocCount;

	// Start || Changes done for Enhancement #6429 for Release 3.4.0
	private String agencyAward;
	private String customLabelName;
	
	//Added in R6 for Return Payment:Start
	private String currentReviewLevel;
	private String taskName;
	//Added in R6 for Return Payment:End

	// Start || Changes done for Enhancement #6485 for Release 3.6.0
	// custom label property added
	

	// R5 changes start
	private String visibility;

	/**
	 * @return the visibility
	 */
	public String getVisibility()
	{
		return visibility;
	}

	/**
	 * @param visibility the visibility to set
	 */
	public void setVisibility(String visibility)
	{
		this.visibility = visibility;
	}

	private com.filenet.api.core.Folder msParent;

	public com.filenet.api.core.Folder getParent()
	{
		return msParent;
	}

	public void setParent(com.filenet.api.core.Folder msParent)
	{
		this.msParent = msParent;
	}

	private String filePath;

	public String getFilePath()
	{
		return filePath;
	}

	public void setFilePath(String asFilePath)
	{
		this.filePath = asFilePath;
	}

	// R5 changes ends
	/**
	 * @return the customLabelName
	 */
	public String getCustomLabelName()
	{
		return customLabelName;
	}

	/**
	 * @param customLabelName the customLabelName to set
	 */
	public void setCustomLabelName(String customLabelName)
	{
		this.customLabelName = customLabelName;
	}

	// End || Changes done for Enhancement #6485 for Release 3.6.0

	/**
	 * @return the agencyAward
	 */
	public String getAgencyAward()
	{
		return agencyAward;
	}

	/**
	 * @param agencyAward the agencyAward to set
	 */
	public void setAgencyAward(String agencyAward)
	{
		this.agencyAward = agencyAward;
	}

	// End || Changes done for Enhancement #6429 for Release 3.4.0

	/**
	 * @return the evalGrpQuesVersion
	 */
	public String getEvalGrpQuesVersion()
	{
		return evalGrpQuesVersion;
	}

	/**
	 * @param evalGrpQuesVersion the evalGrpQuesVersion to set
	 */
	public void setEvalGrpQuesVersion(String evalGrpQuesVersion)
	{
		this.evalGrpQuesVersion = evalGrpQuesVersion;
	}

	/**
	 * @return the evalGrpDocVersion
	 */
	public String getEvalGrpDocVersion()
	{
		return evalGrpDocVersion;
	}

	/**
	 * @param evalGrpDocVersion the evalGrpDocVersion to set
	 */
	public void setEvalGrpDocVersion(String evalGrpDocVersion)
	{
		this.evalGrpDocVersion = evalGrpDocVersion;
	}

	/**
	 * @return the quesVersion
	 */
	public String getQuesVersion()
	{
		return quesVersion;
	}

	/**
	 * @param quesVersion the quesVersion to set
	 */
	public void setQuesVersion(String quesVersion)
	{
		this.quesVersion = quesVersion;
	}

	/**
	 * @return the docVersion
	 */
	public String getDocVersion()
	{
		return docVersion;
	}

	/**
	 * @param docVersion the docVersion to set
	 */
	public void setDocVersion(String docVersion)
	{
		this.docVersion = docVersion;
	}

	/**
	 * @return the versionNo
	 */
	public String getVersionNo()
	{
		return versionNo;
	}

	/**
	 * @param versionNo the versionNo to set
	 */
	public void setVersionNo(String versionNo)
	{
		this.versionNo = versionNo;
	}

	/**
	 * @return the addendumDocumentId
	 */
	public String getAddendumDocumentId()
	{
		return addendumDocumentId;
	}

	/**
	 * @param addendumDocumentId the addendumDocumentId to set
	 */
	public void setAddendumDocumentId(String addendumDocumentId)
	{
		this.addendumDocumentId = addendumDocumentId;
	}

	/**
	 * @return the organizationName
	 */
	public String getOrganizationName()
	{
		return organizationName;
	}

	/**
	 * @param organizationName the organizationName to set
	 */
	public void setOrganizationName(String organizationName)
	{
		this.organizationName = organizationName;
	}

	/**
	 * @return the docProposalIds
	 */
	public List<String> getDocProposalIds()
	{
		return docProposalIds;
	}

	/**
	 * @param docProposalIds the docProposalIds to set
	 */
	public void setDocProposalIds(List<String> docProposalIds)
	{
		this.docProposalIds = docProposalIds;
	}

	/**
	 * @return the procurementTitle
	 */
	public String getProcurementTitle()
	{
		return procurementTitle;
	}

	/**
	 * @param procurementTitle the procurementTitle to set
	 */
	public void setProcurementTitle(String procurementTitle)
	{
		this.procurementTitle = procurementTitle;
	}

	/**
	 * @return the contractId
	 */
	public String getContractId()
	{
		return contractId;
	}

	/**
	 * @param contractId the contractId to set
	 */
	public void setContractId(String contractId)
	{
		this.contractId = contractId;
	}

	/**
	 * @return the budgetId
	 */
	public String getBudgetId()
	{
		return budgetId;
	}

	/**
	 * @param budgetId the budgetId to set
	 */
	public void setBudgetId(String budgetId)
	{
		this.budgetId = budgetId;
	}

	private String budgetId;

	/**
	 * @return the organizationId
	 */
	public String getOrganizationId()
	{
		return organizationId;
	}

	/**
	 * @param organizationId the organizationId to set
	 */
	public void setOrganizationId(String organizationId)
	{
		this.organizationId = organizationId;
	}

	/**
	 * @return the awardId
	 */
	public String getAwardId()
	{
		return awardId;
	}

	/**
	 * @param awardId the awardId to set
	 */
	public void setAwardId(String awardId)
	{
		this.awardId = awardId;
	}

	/**
	 * @return the documentCreatedDate
	 */
	public String getDocumentCreatedDate()
	{
		return documentCreatedDate;
	}

	/**
	 * @param documentCreatedDate the documentCreatedDate to set
	 */
	public void setDocumentCreatedDate(String documentCreatedDate)
	{
		this.documentCreatedDate = documentCreatedDate;
	}

	/**
	 * @return the documentCreatedByUser
	 */
	public String getDocumentCreatedByUser()
	{
		return documentCreatedByUser;
	}

	/**
	 * @param documentCreatedByUser the documentCreatedByUser to set
	 */
	public void setDocumentCreatedByUser(String documentCreatedByUser)
	{
		this.documentCreatedByUser = documentCreatedByUser;
	}

	/**
	 * @return the documentId
	 */
	public String getDocumentId()
	{
		return documentId;
	}

	/**
	 * @param documentId the documentId to set
	 */
	public void setDocumentId(String documentId)
	{
		this.documentId = documentId;
	}

	/**
	 * @return the status
	 */
	public String getStatus()
	{
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status)
	{
		this.status = status;
	}

	/**
	 * @return the statusId
	 */
	public String getStatusId()
	{
		return statusId;
	}

	/**
	 * @param statusId the statusId to set
	 */
	public void setStatusId(String statusId)
	{
		this.statusId = statusId;
	}

	/**
	 * @return the modifiedDate
	 */
	public String getModifiedDate()
	{
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(String modifiedDate)
	{
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the lastModifiedBy
	 */
	public String getLastModifiedById()
	{
		return lastModifiedById;
	}

	/**
	 * @param lastModifiedBy the lastModifiedBy to set
	 */
	public void setLastModifiedById(String lastModifiedById)
	{
		this.lastModifiedById = lastModifiedById;
	}

	/**
	 * @return the documentTitle
	 */
	public String getDocumentTitle()
	{
		return documentTitle;
	}

	/**
	 * @param documentTitle the documentTitle to set
	 */
	public void setDocumentTitle(String documentTitle)
	{
		this.documentTitle = documentTitle;
	}

	/**
	 * @return the documentType
	 */
	public String getDocumentType()
	{
		return documentType;
	}

	/**
	 * @param documentType the documentType to set
	 */
	public void setDocumentType(String documentType)
	{
		this.documentType = documentType;
	}

	/**
	 * @return the proposalId
	 */
	public String getProposalId()
	{
		return proposalId;
	}

	/**
	 * @param proposalId the proposalId to set
	 */
	public void setProposalId(String proposalId)
	{
		this.proposalId = proposalId;
	}

	/**
	 * @return the requiredFlag
	 */
	public String getRequiredFlag()
	{
		return requiredFlag;
	}

	/**
	 * @param requiredFlag the requiredFlag to set
	 */
	public void setRequiredFlag(String requiredFlag)
	{
		this.requiredFlag = requiredFlag;
	}

	/**
	 * @return the actions
	 */
	public String getActions()
	{
		return actions;
	}

	/**
	 * @param actions the actions to set
	 */
	public void setActions(String actions)
	{
		this.actions = actions;
	}

	/**
	 * @return the documentCategory
	 */
	public String getDocumentCategory()
	{
		return documentCategory;
	}

	/**
	 * @param documentCategory the documentCategory to set
	 */
	public void setDocumentCategory(String documentCategory)
	{
		this.documentCategory = documentCategory;
	}

	/**
	 * @return the createdDate
	 */
	public String getCreatedDate()
	{
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate)
	{
		this.createdDate = createdDate;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	/**
	 * @return the procurementStatusId
	 */
	public String getProcurementStatusId()
	{
		return procurementStatusId;
	}

	/**
	 * @param procurementStatusId the procurementStatusId to set
	 */
	public void setProcurementStatusId(String procurementStatusId)
	{
		this.procurementStatusId = procurementStatusId;
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
	 * @return the documentSeqNumber
	 */
	public String getDocumentSeqNumber()
	{
		return documentSeqNumber;
	}

	/**
	 * @param documentSeqNumber the documentSeqNumber to set
	 */
	public void setDocumentSeqNumber(String documentSeqNumber)
	{
		this.documentSeqNumber = documentSeqNumber;
	}

	/**
	 * @return the createdSqlDate
	 */
	public Timestamp getCreatedSqlDate()
	{
		return createdSqlDate;
	}

	/**
	 * @param createdSqlDate the createdSqlDate to set
	 */
	public void setCreatedSqlDate(Timestamp createdSqlDate)
	{
		this.createdSqlDate = createdSqlDate;
	}

	/**
	 * @return the modifiedSqlDate
	 */
	public Timestamp getModifiedSqlDate()
	{
		return modifiedSqlDate;
	}

	/**
	 * @param modifiedSqlDate the modifiedSqlDate to set
	 */
	public void setModifiedSqlDate(Timestamp modifiedSqlDate)
	{
		this.modifiedSqlDate = modifiedSqlDate;
	}

	public String getAddendumType()
	{
		return addendumType;
	}

	public void setAddendumType(String addendumType)
	{
		this.addendumType = addendumType;
	}

	/**
	 * @return the isAddendum
	 */
	public String getIsAddendum()
	{
		return isAddendum;
	}

	/**
	 * @param isAddendum the isAddendum to set
	 */
	public void setIsAddendum(String isAddendum)
	{
		this.isAddendum = isAddendum;
	}

	public String getDocumentStatus()
	{
		return documentStatus;
	}

	public void setDocumentStatus(String documentStatus)
	{
		this.documentStatus = documentStatus;
	}

	public String getLastModifiedByName()
	{
		return lastModifiedByName;
	}

	public void setLastModifiedByName(String lastModifiedByName)
	{
		this.lastModifiedByName = lastModifiedByName;
	}

	/**
	 * @return the isRequiredDoc
	 */
	public String getIsRequiredDoc()
	{
		return isRequiredDoc;
	}

	/**
	 * @param isRequiredDoc the isRequiredDoc to set
	 */
	public void setIsRequiredDoc(String isRequiredDoc)
	{
		this.isRequiredDoc = isRequiredDoc;
	}

	/**
	 * @return the procurementDocId
	 */
	public String getReferenceDocSeqNo()
	{
		return referenceDocSeqNo;
	}

	/**
	 * @param procurementDocId the procurementDocId to set
	 */
	public void setReferenceDocSeqNo(String referenceDocSeqNo)
	{
		this.referenceDocSeqNo = referenceDocSeqNo;
	}

	/**
	 * @return the organizationType
	 */
	public String getOrganizationType()
	{
		return organizationType;
	}

	/**
	 * @param organizationType the organizationType to set
	 */
	public void setOrganizationType(String organizationType)
	{
		this.organizationType = organizationType;
	}

	/**
	 * @return the assignStatus
	 */
	public String getAssignStatus()
	{
		return assignStatus;
	}

	/**
	 * @param assignStatus the assignStatus to set
	 */
	public void setAssignStatus(String assignStatus)
	{
		this.assignStatus = assignStatus;
	}

	/**
	 * @return the proposalTitle
	 */
	public String getProposalTitle()
	{
		return proposalTitle;
	}

	/**
	 * @param proposalTitle the proposalTitle to set
	 */
	public void setProposalTitle(String proposalTitle)
	{
		this.proposalTitle = proposalTitle;
	}

	/**
	 * @return the documentSeq
	 */
	public String getDocumentSeq()
	{
		return documentSeq;
	}

	/**
	 * @param documentSeq the documentSeq to set
	 */
	public void setDocumentSeq(String documentSeq)
	{
		this.documentSeq = documentSeq;
	}

	/**
	 * @return the orgTypeInSession
	 */
	public String getOrgTypeInSession()
	{
		return orgTypeInSession;
	}

	/**
	 * @param orgTypeInSession the orgTypeInSession to set
	 */
	public void setOrgTypeInSession(String orgTypeInSession)
	{
		this.orgTypeInSession = orgTypeInSession;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName()
	{
		return tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public String getProposalStatusId()
	{
		return proposalStatusId;
	}

	public void setProposalStatusId(String proposalStatusId)
	{
		this.proposalStatusId = proposalStatusId;
	}

	public String getUserRole()
	{
		return userRole;
	}

	public void setUserRole(String userRole)
	{
		this.userRole = userRole;
	}

	public String getProcurementDocumentId()
	{
		return procurementDocumentId;
	}

	public void setProcurementDocumentId(String procurementDocumentId)
	{
		this.procurementDocumentId = procurementDocumentId;
	}

	/**
	 * @return the docDeleteFlag
	 */
	public String getDocDeleteFlag()
	{
		return docDeleteFlag;
	}

	/**
	 * @param docDeleteFlag the docDeleteFlag to set
	 */
	public void setDocDeleteFlag(String docDeleteFlag)
	{
		this.docDeleteFlag = docDeleteFlag;
	}

	/**
	 * @return the evaluationPoolMappingId Updated Method in R4
	 */
	public String getEvaluationPoolMappingId()
	{
		return evaluationPoolMappingId;

	}

	/**
	 * @param evaluationPoolMappingId the evaluationPoolMappingId to set Updated
	 *            Method in R4
	 */
	public void setEvaluationPoolMappingId(String evaluationPoolMappingId)
	{
		this.evaluationPoolMappingId = evaluationPoolMappingId;
	}

	

	public void setRequiredQuesDocCount(Integer requiredQuesDocCount)
	{
		this.requiredQuesDocCount = requiredQuesDocCount;
	}

	public Integer getRequiredQuesDocCount()
	{
		return requiredQuesDocCount;
	}
	//Added in R6 for Return Payment:Start
	public String getTaskName() {
		return taskName;
	}
	
	//Updated toString method for Return Payment
	@Override
	public String toString()
	{
		return "ExtendedDocument [documentId=" + documentId + ", status=" + status + ", statusId=" + statusId
				+ ", modifiedDate=" + modifiedDate + ", lastModifiedById=" + lastModifiedById + ", lastModifiedByName="
				+ lastModifiedByName + ", documentTitle=" + documentTitle + ", documentType=" + documentType
				+ ", proposalId=" + proposalId + ", requiredFlag=" + requiredFlag + ", actions=" + actions
				+ ", documentCategory=" + documentCategory + ", createdDate=" + createdDate + ", createdBy="
				+ createdBy + ", procurementStatusId=" + procurementStatusId + ", procurementId=" + procurementId
				+ ", documentSeqNumber=" + documentSeqNumber + ", createdSqlDate=" + createdSqlDate
				+ ", modifiedSqlDate=" + modifiedSqlDate + ", addendumType=" + addendumType + ", documentCreatedDate="
				+ documentCreatedDate + ", documentCreatedByUser=" + documentCreatedByUser + ", isAddendum="
				+ isAddendum + ", documentStatus=" + documentStatus + ", isRequiredDoc=" + isRequiredDoc
				+ ", referenceDocSeqNo=" + referenceDocSeqNo + ", organizationType=" + organizationType
				+ ", assignStatus=" + assignStatus + ", proposalTitle=" + proposalTitle + ", documentSeq="
				+ documentSeq + ", orgTypeInSession=" + orgTypeInSession + ", tableName=" + tableName
				+ ", proposalStatusId=" + proposalStatusId + ", userRole=" + userRole + ", procurementDocumentId="
				+ procurementDocumentId + ", docDeleteFlag=" + docDeleteFlag + ", evaluationPoolMappingId="
				+ evaluationPoolMappingId + ", organizationId=" + organizationId + ", awardId=" + awardId
				+ ", contractId=" + contractId + ", procurementTitle=" + procurementTitle + ", docProposalIds="
				+ docProposalIds + ", organizationName=" + organizationName + ", addendumDocumentId="
				+ addendumDocumentId + ", versionNo=" + versionNo + ", quesVersion=" + quesVersion + ", docVersion="
				+ docVersion + ", evalGrpQuesVersion=" + evalGrpQuesVersion + ", evalGrpDocVersion="
				+ evalGrpDocVersion + ", requiredQuesDocCount=" + requiredQuesDocCount + ", agencyAward=" + agencyAward
				+ ", customLabelName=" + customLabelName + ", currentReviewLevel=" + currentReviewLevel + ", taskName="
				+ taskName + ", visibility=" + visibility + ", msParent=" + msParent + ", filePath=" + filePath
				+ ", budgetId=" + budgetId + "]";
	}
	//Updated toString method for Return Payment
	
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	
	public String getCurrentReviewLevel() {
		return currentReviewLevel;
	}

	public void setCurrentReviewLevel(String currentReviewLevel) {
		this.currentReviewLevel = currentReviewLevel;
	}
	//Added in R6 for Return Payment:End
}
