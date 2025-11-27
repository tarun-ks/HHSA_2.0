package com.nyc.hhs.model;

import java.util.Date;
import java.util.List;

import com.nyc.hhs.constants.ApplicationConstants;
import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.service.filenetmanager.p8transferobject.P8UserSession;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
/**
 * This class is a bean which maintains the Faq Form information.
 * 
 */

public class TaskDetailsBean extends DefaultAssignment
{
	private String procurementTitle;
	private String procurementEpin;
	@RegExp(value ="^\\d{0,22}")
	private String procurementId = "";
	@RegExp(value ="^\\d{0,22}")
	private String budgetId = "";
	@RegExp(value ="^\\d{0,22}")
	private String contractId = "";
	private String proposalId = "";
	private String provider;
	private String awardEpin = "";
	private String ct = "";
	private String taskName;
	private String submittedBy;
	private String submittedDate;
	private String assignedTo;
	private String assignedToUserName;
	private String assignedDate;
	private String taskAction;
	private List taskActions;
	private String comment;
	private String workFlowId;
	private String taskId;
	private String reassignUserName;
	private String reassignUserId;
	private String level = "1";
	private String totalLevel;
	private String taskStatus;
	private List commentsHistory;
	private String taskType;
	private String applicationId;
	private String organizationId;
	private String organizationName;
	private String userId;
	private String agencyId;
	private String providerComment;
	private String internalComment;
	private String agencyQueryId;
	private String providerQueryId;
	private String entityId;
	private String entityType;
	private P8UserSession p8UserSession;
	private String contractSourceId = "";
	private String proposalTitle;
	private String firstRoundEvalCompDate;
	private String lastModifiedDate;
	private String contractConfWob;
	private String invoiceId;
	private Boolean isTaskScreen;
	private String newFYId;
	private String startFiscalYear;
	private String subBudgetId;
	private String eventName;
	private String eventType;
	private String entityTypeForAgency;
	private String agencyPrimaryContactId;
	private String agencySecondaryContactId;
	private String awardAmount;
	private String evaluationStatusId;
	private Boolean isFirstLaunch = false;
	private String previousTaskStatus;
	private String amendmentType;
	private String currentTab;
	private String userRole;
	private Boolean isTaskAssigned = false;
	private Boolean isAssignableOperation = false;
	private Boolean launchCOF = true;
	private String agencyName;
	private String launchOrgType;
	private String submittedByName;
	private String linkedWobNum;
	private String budgetAdvanceId;
	private String paymentId;
	private String period;
	private String finalizeEvaluationDate;
	private String awardSelectionDate;
	private String currentTaskStatus;
	private String baseContractId;
	private String entityStatus;
	private Boolean isFirstReached = false;
	private String agencyPrimaryContact;
	private String agencySecondaryContact;
	private String accPrimaryContactId;
	private String accSecondaryContactId;
	private String accPrimaryContact;
	private String accSecondaryContact;
	private String auditUserId;
	// added as part of build 2.6.0, defect id 5653
	private String oldContStartDateTask;
	private String oldContEndDateTask;
	private String oldProcValTask;
	private String competitionPoolTitle;
	private String evaluationPoolMappingId;
	private String evaluationGroupTitle;
	private String isOpenEndedRfp;
	private String taskSource;
	// added as part of build 3.12.0, enhancement id 6602
	private Boolean isAlreadyLaunchedFYTask;
	// Added in R5
	private Boolean isNegotiationRequired;
	private Boolean isNewLaunch;
	//Added for R6: Returned payment review task
	private String entityName;
	private Date taskRequestedDate;
	private String returnPaymentDetailId = "";
	//Ended R6: Returned payment review task
	public Boolean getIsNewLaunch()
	{
		return isNewLaunch;
	}

	public void setIsNewLaunch(Boolean isNewLaunch)
	{
		this.isNewLaunch = isNewLaunch;
	}

	/**
	 * @return the isNegotiationRequired
	 */
	public Boolean getIsNegotiationRequired()
	{
		return isNegotiationRequired;
	}

	/**
	 * @param isNegotiationRequired the isNegotiationRequired to set
	 */
	public void setIsNegotiationRequired(Boolean isNegotiationRequired)
	{
		this.isNegotiationRequired = isNegotiationRequired;
	}

	/**
	 * @return the isAlreadyLaunchedFYTask
	 */
	public Boolean getIsAlreadyLaunchedFYTask()
	{
		return isAlreadyLaunchedFYTask;
	}

	/**
	 * @param isAlreadyLaunchedFYTask the isAlreadyLaunchedFYTask to set
	 */
	public void setIsAlreadyLaunchedFYTask(Boolean isAlreadyLaunchedFYTask)
	{
		this.isAlreadyLaunchedFYTask = isAlreadyLaunchedFYTask;
	}

	// added as part of release 3.8.0, enhancement id 6483
	private Boolean discFlagForUpdate = false;

	/**
	 * @return the discFlagForUpdate
	 */
	public Boolean getDiscFlagForUpdate()
	{
		return discFlagForUpdate;
	}

	/**
	 * @param discFlagForUpdate the discFlagForUpdate to set
	 */
	public void setDiscFlagForUpdate(Boolean discFlagForUpdate)
	{
		this.discFlagForUpdate = discFlagForUpdate;
	}

	/**
	 * @return the taskSource
	 */
	public String getTaskSource()
	{
		return taskSource;
	}

	/**
	 * @param taskSource the taskSource to set
	 */
	public void setTaskSource(String taskSource)
	{
		this.taskSource = taskSource;
	}

	// R4: Tab Level Comments
	private String entityTypeTabLevel;
	private Boolean isEntityTypeTabLevel = false;

	public Boolean getIsEntityTypeTabLevel()
	{
		return isEntityTypeTabLevel;
	}

	public void setIsEntityTypeTabLevel(Boolean isEntityTypeTabLevel)
	{
		this.isEntityTypeTabLevel = isEntityTypeTabLevel;
	}

	public String getEntityTypeTabLevel()
	{
		return entityTypeTabLevel;
	}

	public void setEntityTypeTabLevel(String entityTypeTabLevel)
	{
		this.entityTypeTabLevel = entityTypeTabLevel;
	}

	// R4: Tab Level Comments Ends

	/**
	 * @return the auditUserId
	 */
	public String getAuditUserId()
	{
		return auditUserId;
	}

	/**
	 * @param auditUserId the auditUserId to set
	 */
	public void setAuditUserId(String auditUserId)
	{
		this.auditUserId = auditUserId;
	}

	/**
	 * @return the entityStatus
	 */
	public String getEntityStatus()
	{
		return entityStatus;
	}

	/**
	 * @param entityStatus the entityStatus to set
	 */
	public void setEntityStatus(String entityStatus)
	{
		this.entityStatus = entityStatus;
	}

	/**
	 * @return the baseContractId
	 */
	public String getBaseContractId()
	{
		return baseContractId;
	}

	/**
	 * @param baseContractId the baseContractId to set
	 */
	public void setBaseContractId(String baseContractId)
	{
		this.baseContractId = baseContractId;
	}

	/**
	 * @return the currentTaskStatus
	 */
	public String getCurrentTaskStatus()
	{
		return currentTaskStatus;
	}

	/**
	 * @param currentTaskStatus the currentTaskStatus to set
	 */
	public void setCurrentTaskStatus(String currentTaskStatus)
	{
		this.currentTaskStatus = currentTaskStatus;
	}

	/**
	 * @return the linkedWobNum
	 */
	public String getLinkedWobNum()
	{
		return linkedWobNum;
	}

	/**
	 * @param linkedWobNum the linkedWobNum to set
	 */
	public void setLinkedWobNum(String linkedWobNum)
	{
		this.linkedWobNum = linkedWobNum;
	}

	/**
	 * @return the submittedByName
	 */
	public String getSubmittedByName()
	{
		return submittedByName;
	}

	/**
	 * @param submittedByName the submittedByName to set
	 */
	public void setSubmittedByName(String submittedByName)
	{
		if (null == submittedByName || submittedByName.isEmpty())
		{
			this.submittedByName = HHSConstants.SYSTEM_USER;
		}
		else
		{
			this.submittedByName = submittedByName;
		}
	}

	/**
	 * @return the launchOrgType
	 */
	public String getLaunchOrgType()
	{
		return launchOrgType;
	}

	/**
	 * @param launchOrgType the launchOrgType to set
	 */
	public void setLaunchOrgType(String launchOrgType)
	{
		this.launchOrgType = launchOrgType;
	}

	/**
	 * @return the userRole
	 */
	public String getUserRole()
	{
		return userRole;
	}

	/**
	 * @return the launchCOF
	 */
	public Boolean getLaunchCOF()
	{
		return launchCOF;
	}

	/**
	 * @param launchCOF the launchCOF to set
	 */
	public void setLaunchCOF(Boolean launchCOF)
	{
		this.launchCOF = launchCOF;
	}

	/**
	 * @param userRole the userRole to set
	 */
	public void setUserRole(String userRole)
	{
		this.userRole = userRole;
	}

	/**
	 * @return the isTaskAssigned
	 */
	public Boolean getIsTaskAssigned()
	{
		return isTaskAssigned;
	}

	/**
	 * @param isTaskAssigned the isTaskAssigned to set
	 */
	public void setIsTaskAssigned(Boolean isTaskAssigned)
	{
		this.isTaskAssigned = isTaskAssigned;
	}

	/**
	 * @return the isAssignableOperation
	 */
	public Boolean getIsAssignableOperation()
	{
		return isAssignableOperation;
	}

	/**
	 * @param isAssignableOperation the isAssignableOperation to set
	 */
	public void setIsAssignableOperation(Boolean isAssignableOperation)
	{
		this.isAssignableOperation = isAssignableOperation;
	}

	/**
	 * @return the currentTab
	 */
	public String getCurrentTab()
	{
		return currentTab;
	}

	/**
	 * @param currentTab the currentTab to set
	 */
	public void setCurrentTab(String currentTab)
	{
		this.currentTab = currentTab;
	}

	/**
	 * @return the entityTypeForAgency
	 */
	public String getEntityTypeForAgency()
	{
		return entityTypeForAgency;
	}

	/**
	 * @param entityTypeForAgency the entityTypeForAgency to set
	 */
	public void setEntityTypeForAgency(String entityTypeForAgency)
	{
		this.entityTypeForAgency = entityTypeForAgency;
	}

	/**
	 * @return the eventName
	 */
	public String getEventName()
	{
		return eventName;
	}

	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName)
	{
		this.eventName = eventName;
	}

	/**
	 * @return the eventType
	 */
	public String getEventType()
	{
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(String eventType)
	{
		this.eventType = eventType;
	}

	/**
	 * @return the startFiscalYear
	 */
	public String getStartFiscalYear()
	{
		return startFiscalYear;
	}

	/**
	 * @param startFiscalYear the startFiscalYear to set
	 */
	public void setStartFiscalYear(String startFiscalYear)
	{
		this.startFiscalYear = startFiscalYear;
	}

	/**
	 * @return the newFYId
	 */
	public String getNewFYId()
	{
		return newFYId;
	}

	/**
	 * @param newFYId the newFYId to set
	 */
	public void setNewFYId(String newFYId)
	{
		this.newFYId = newFYId;
	}

	/**
	 * @return the isTaskScreen
	 */
	public Boolean getIsTaskScreen()
	{
		return isTaskScreen;
	}

	/**
	 * @param isTaskScreen the isTaskScreen to set
	 */
	public void setIsTaskScreen(Boolean isTaskScreen)
	{
		this.isTaskScreen = isTaskScreen;
	}

	/**
	 * @return the contractConfWob
	 */
	public String getContractConfWob()
	{
		return contractConfWob;
	}

	/**
	 * @param contractConfWob the contractConfWob to set
	 */
	public void setContractConfWob(String contractConfWob)
	{
		this.contractConfWob = contractConfWob;
	}

	public String getContractSourceId()
	{
		return contractSourceId;
	}

	public void setContractSourceId(String contractSourceId)
	{
		this.contractSourceId = contractSourceId;
	}

	/**
	 * @return the totalLevel
	 */
	public String getTotalLevel()
	{
		return totalLevel;
	}

	/**
	 * @param totalLevel the totalLevel to set
	 */
	public void setTotalLevel(String totalLevel)
	{
		this.totalLevel = totalLevel;
	}

	/**
	 * @return the p8UserSession
	 */
	public P8UserSession getP8UserSession()
	{
		return p8UserSession;
	}

	/**
	 * @param p8UserSession the p8UserSession to set
	 */
	public void setP8UserSession(P8UserSession p8UserSession)
	{
		this.p8UserSession = p8UserSession;
	}

	/**
	 * @return the entityType
	 */
	public String getEntityType()
	{
		return entityType;
	}

	/**
	 * @param entityType the entityType to set
	 */
	public void setEntityType(String entityType)
	{
		this.entityType = entityType;
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
	 * @return the taskId
	 */
	public String getTaskId()
	{
		return taskId;
	}

	/**
	 * @param taskId the taskId to set
	 */
	public void setTaskId(String taskId)
	{
		this.taskId = taskId;
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
	 * @return the providerComment
	 */
	public String getProviderComment()
	{
		return providerComment;
	}

	/**
	 * @param providerComment the providerComment to set
	 */
	public void setProviderComment(String providerComment)
	{
		this.providerComment = providerComment;
	}

	/**
	 * @return the internalComment
	 */
	public String getInternalComment()
	{
		return internalComment;
	}

	/**
	 * @param internalComment the internalComment to set
	 */
	public void setInternalComment(String internalComment)
	{
		this.internalComment = internalComment;
	}

	/**
	 * @return the agencyQueryId
	 */
	public String getAgencyQueryId()
	{
		return agencyQueryId;
	}

	/**
	 * @param agencyQueryId the agencyQueryId to set
	 */
	public void setAgencyQueryId(String agencyQueryId)
	{
		this.agencyQueryId = agencyQueryId;
	}

	/**
	 * @return the providerQueryId
	 */
	public String getProviderQueryId()
	{
		return providerQueryId;
	}

	/**
	 * @param providerQueryId the providerQueryId to set
	 */
	public void setProviderQueryId(String providerQueryId)
	{
		this.providerQueryId = providerQueryId;
	}

	/**
	 * @return the agencyId
	 */
	public String getAgencyId()
	{
		return agencyId;
	}

	/**
	 * @param agencyId the agencyId to set
	 */
	public void setAgencyId(String agencyId)
	{
		this.agencyId = agencyId;
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
	 * @return the procurementEpin
	 */
	public String getProcurementEpin()
	{
		return procurementEpin;
	}

	/**
	 * @param procurementEpin the procurementEpin to set
	 */
	public void setProcurementEpin(String procurementEpin)
	{
		this.procurementEpin = checkNullCondition(procurementEpin);
	}

	/**
	 * @return the provider
	 */
	public String getProvider()
	{
		return provider;
	}

	/**
	 * @param provider the provider to set
	 */
	public void setProvider(String provider)
	{
		this.provider = checkNullCondition(provider);
	}

	/**
	 * @return the awardEpin
	 */
	public String getAwardEpin()
	{
		return awardEpin;
	}

	/**
	 * @param awardEpin the awardEpin to set
	 */
	public void setAwardEpin(String awardEpin)
	{
		this.awardEpin = checkNullCondition(awardEpin);
	}

	/**
	 * @return the ct
	 */
	public String getCt()
	{
		return ct;
	}

	/**
	 * @param ct the ct to set
	 */
	public void setCt(String ct)
	{
		this.ct = checkNullCondition(ct);
	}

	/**
	 * @return the taskName
	 */
	public String getTaskName()
	{
		return taskName;
	}

	/**
	 * @param taskName the taskName to set
	 */
	public void setTaskName(String taskName)
	{
		this.taskName = taskName;
	}

	/**
	 * @return the submittedBy
	 */
	public String getSubmittedBy()
	{
		return submittedBy;
	}

	/**
	 * @param submittedBy the submittedBy to set
	 */
	public void setSubmittedBy(String submittedBy)
	{
		this.submittedBy = submittedBy;
	}

	/**
	 * @return the submittedDate
	 */
	public String getSubmittedDate()
	{
		return submittedDate;
	}

	/**
	 * @param submittedDate the submittedDate to set
	 */
	public void setSubmittedDate(String submittedDate)
	{
		this.submittedDate = submittedDate;
	}

	/**
	 * @return the assignedTo
	 */
	public String getAssignedTo()
	{
		return assignedTo;
	}

	/**
	 * @param assignedTo the assignedTo to set
	 */
	public void setAssignedTo(String assignedTo)
	{
		this.assignedTo = assignedTo;
	}

	/**
	 * @return the assignedDate
	 */
	public String getAssignedDate()
	{
		return assignedDate;
	}

	/**
	 * @param assignedDate the assignedDate to set
	 */
	public void setAssignedDate(String assignedDate)
	{
		this.assignedDate = assignedDate;
	}

	/**
	 * @return the taskAction
	 */
	public String getTaskAction()
	{
		return taskAction;
	}

	/**
	 * @param taskAction the taskAction to set
	 */
	public void setTaskAction(String taskAction)
	{
		this.taskAction = taskAction;
	}

	/**
	 * @return the taskActions
	 */
	public List getTaskActions()
	{
		return taskActions;
	}

	/**
	 * @param taskActions the taskActions to set
	 */
	public void setTaskActions(List taskActions)
	{
		this.taskActions = taskActions;
	}

	/**
	 * @return the comment
	 */
	public String getComment()
	{
		return comment;
	}

	/**
	 * @param comment the comment to set
	 */
	public void setComment(String comment)
	{
		this.comment = comment;
	}

	/**
	 * @return the workFlowId
	 */
	public String getWorkFlowId()
	{
		return workFlowId;
	}

	/**
	 * @param workFlowId the workFlowId to set
	 */
	public void setWorkFlowId(String workFlowId)
	{
		this.workFlowId = workFlowId;
	}

	/**
	 * @return the reassignUserName
	 */
	public String getReassignUserName()
	{
		return reassignUserName;
	}

	/**
	 * @param reassignUserName the reassignUserName to set
	 */
	public void setReassignUserName(String reassignUserName)
	{
		this.reassignUserName = reassignUserName;
	}

	/**
	 * @return the reassignUserId
	 */
	public String getReassignUserId()
	{
		return reassignUserId;
	}

	/**
	 * @param reassignUserId the reassignUserId to set
	 */
	public void setReassignUserId(String reassignUserId)
	{
		this.reassignUserId = reassignUserId;
	}

	/**
	 * @return the level
	 */
	public String getLevel()
	{
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level)
	{
		this.level = level;
	}

	/**
	 * @return the taskStatus
	 */
	public String getTaskStatus()
	{
		return taskStatus;
	}

	/**
	 * @param taskStatus the taskStatus to set
	 */
	public void setTaskStatus(String taskStatus)
	{
		this.taskStatus = taskStatus;
	}

	/**
	 * @return the commentsHistory
	 */
	public List getCommentsHistory()
	{
		return commentsHistory;
	}

	/**
	 * @param commentsHistory the commentsHistory to set
	 */
	public void setCommentsHistory(List commentsHistory)
	{
		this.commentsHistory = commentsHistory;
	}

	/**
	 * @return the taskType
	 */
	public String getTaskType()
	{
		return taskType;
	}

	/**
	 * @param taskType the taskType to set
	 */
	public void setTaskType(String taskType)
	{
		this.taskType = taskType;
	}

	/**
	 * @return the applicationId
	 */
	public String getApplicationId()
	{
		return applicationId;
	}

	/**
	 * @param applicationId the applicationId to set
	 */
	public void setApplicationId(String applicationId)
	{
		this.applicationId = applicationId;
	}

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
	 * @return the firstRoundEvalCompDate
	 */
	public String getFirstRoundEvalCompDate()
	{
		return firstRoundEvalCompDate;
	}

	/**
	 * @param firstRoundEvalCompDate the firstRoundEvalCompDate to set
	 */
	public void setFirstRoundEvalCompDate(String firstRoundEvalCompDate)
	{
		this.firstRoundEvalCompDate = firstRoundEvalCompDate;
	}

	/**
	 * @return the lastModifiedDate
	 */
	public String getLastModifiedDate()
	{
		return lastModifiedDate;
	}

	/**
	 * @param lastModifiedDate the lastModifiedDate to set
	 */
	public void setLastModifiedDate(String lastModifiedDate)
	{
		this.lastModifiedDate = lastModifiedDate;
	}

	/**
	 * @return the invoiceId
	 */
	public String getInvoiceId()
	{
		return invoiceId;
	}

	/**
	 * @param invoiceId the invoiceId to set
	 */
	public void setInvoiceId(String invoiceId)
	{
		if (HHSConstants.EMPTY_STRING.equalsIgnoreCase(invoiceId))
		{
			invoiceId = null;
		}
		this.invoiceId = invoiceId;
	}

	/**
	 * @return the subBudgetId
	 */
	public String getSubBudgetId()
	{
		return subBudgetId;
	}

	/**
	 * @param subBudgetId the subBudgetId to set
	 */
	public void setSubBudgetId(String subBudgetId)
	{
		this.subBudgetId = subBudgetId;
	}

	/**
	 * @return the assignedToUserName
	 */
	public String getAssignedToUserName()
	{
		return assignedToUserName;
	}

	/**
	 * @param assignedToUserName the assignedToUserName to set
	 */
	public void setAssignedToUserName(String assignedToUserName)
	{
		this.assignedToUserName = assignedToUserName;
	}

	/**
	 * @return the agencyPrimaryContactId
	 */
	public String getAgencyPrimaryContactId()
	{
		return agencyPrimaryContactId;
	}

	/**
	 * @param agencyPrimaryContactId the agencyPrimaryContactId to set
	 */
	public void setAgencyPrimaryContactId(String agencyPrimaryContactId)
	{
		this.agencyPrimaryContactId = agencyPrimaryContactId;
	}

	/**
	 * @return the agencySecondaryContactId
	 */
	public String getAgencySecondaryContactId()
	{
		return agencySecondaryContactId;
	}

	/**
	 * @param agencySecondaryContactId the agencySecondaryContactId to set
	 */
	public void setAgencySecondaryContactId(String agencySecondaryContactId)
	{
		this.agencySecondaryContactId = agencySecondaryContactId;
	}

	/**
	 * @return the awardAmount
	 */
	public String getAwardAmount()
	{
		return awardAmount;
	}

	/**
	 * @param awardAmount the awardAmount to set
	 */
	public void setAwardAmount(String awardAmount)
	{
		this.awardAmount = awardAmount;
	}

	/**
	 * @return the evaluationStatusId
	 */
	public String getEvaluationStatusId()
	{
		return evaluationStatusId;
	}

	/**
	 * @param evaluationStatusId the evaluationStatusId to set
	 */
	public void setEvaluationStatusId(String evaluationStatusId)
	{
		this.evaluationStatusId = evaluationStatusId;
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
	 * @return the isFirstLaunch
	 */
	public Boolean getIsFirstLaunch()
	{
		return isFirstLaunch;
	}

	/**
	 * @param isFirstLaunch the isFirstLaunch to set
	 */
	public void setIsFirstLaunch(Boolean isFirstLaunch)
	{
		this.isFirstLaunch = isFirstLaunch;
	}

	/**
	 * @return the previousTaskStatus
	 */
	public String getPreviousTaskStatus()
	{
		return previousTaskStatus;
	}

	/**
	 * @param previousTaskStatus the previousTaskStatus to set
	 */
	public void setPreviousTaskStatus(String previousTaskStatus)
	{
		this.previousTaskStatus = previousTaskStatus;
	}

	/**
	 * @return the amendmentType
	 */
	public String getAmendmentType()
	{
		return amendmentType;
	}

	/**
	 * @param amendmentType the amendmentType to set
	 */
	public void setAmendmentType(String amendmentType)
	{
		this.amendmentType = amendmentType;
	}

	public String getAgencyName()
	{
		return agencyName;
	}

	public void setAgencyName(String agencyName)
	{
		this.agencyName = agencyName;
	}

	/**
	 * @return the budgetAdvanceId
	 */
	public String getBudgetAdvanceId()
	{
		return budgetAdvanceId;
	}

	/**
	 * @param budgetAdvanceId the budgetAdvanceId to set
	 */
	public void setBudgetAdvanceId(String budgetAdvanceId)
	{
		if (HHSConstants.EMPTY_STRING.equalsIgnoreCase(budgetAdvanceId))
		{
			budgetAdvanceId = null;
		}
		this.budgetAdvanceId = budgetAdvanceId;
	}

	/**
	 * @return the paymentId
	 */
	public String getPaymentId()
	{
		return paymentId;
	}

	/**
	 * @param paymentId the paymentId to set
	 */
	public void setPaymentId(String paymentId)
	{
		this.paymentId = paymentId;
	}

	/**
	 * This method return by default value if input is null or empty
	 * @param asInputVal Input value
	 * @return Output value
	 */
	private String checkNullCondition(String asInputVal)
	{
		if (null == asInputVal || asInputVal.isEmpty())
		{
			return ApplicationConstants.NA_KEY;
		}
		else
		{
			return asInputVal;
		}
	}

	/**
	 * @return the period
	 */
	public String getPeriod()
	{
		return period;
	}

	/**
	 * @param period
	 */
	public void setPeriod(String period)
	{
		this.period = period;
	}

	/**
	 * @return the finalizeEvaluationDate
	 */
	public String getFinalizeEvaluationDate()
	{
		return finalizeEvaluationDate;
	}

	/**
	 * @param finalizeEvaluationDate
	 */
	public void setFinalizeEvaluationDate(String finalizeEvaluationDate)
	{
		this.finalizeEvaluationDate = finalizeEvaluationDate;
	}

	/**
	 * @return the awardSelectionDate
	 */
	public String getAwardSelectionDate()
	{
		return awardSelectionDate;
	}

	/**
	 * @param awardSelectionDate
	 */
	public void setAwardSelectionDate(String awardSelectionDate)
	{
		this.awardSelectionDate = awardSelectionDate;
	}

	public Boolean getIsFirstReached()
	{
		return isFirstReached;
	}

	public void setIsFirstReached(Boolean isFirstReached)
	{
		this.isFirstReached = isFirstReached;
	}

	public String getAgencyPrimaryContact()
	{
		return agencyPrimaryContact;
	}

	public void setAgencyPrimaryContact(String agencyPrimaryContact)
	{
		this.agencyPrimaryContact = agencyPrimaryContact;
	}

	public String getAgencySecondaryContact()
	{
		return agencySecondaryContact;
	}

	public void setAgencySecondaryContact(String agencySecondaryContact)
	{
		this.agencySecondaryContact = agencySecondaryContact;
	}

	public String getAccPrimaryContactId()
	{
		return accPrimaryContactId;
	}

	public void setAccPrimaryContactId(String accPrimaryContactId)
	{
		this.accPrimaryContactId = accPrimaryContactId;
	}

	public String getAccSecondaryContactId()
	{
		return accSecondaryContactId;
	}

	public void setAccSecondaryContactId(String accSecondaryContactId)
	{
		this.accSecondaryContactId = accSecondaryContactId;
	}

	public String getAccPrimaryContact()
	{
		return accPrimaryContact;
	}

	public void setAccPrimaryContact(String accPrimaryContact)
	{
		this.accPrimaryContact = accPrimaryContact;
	}

	public String getAccSecondaryContact()
	{
		return accSecondaryContact;
	}

	public void setAccSecondaryContact(String accSecondaryContact)
	{
		this.accSecondaryContact = accSecondaryContact;
	}

	/**
	 * @return the oldContStartDateTask
	 */
	public String getOldContStartDateTask()
	{
		return oldContStartDateTask;
	}

	/**
	 * @param oldContStartDateTask the oldContStartDateTask to set
	 */
	public void setOldContStartDateTask(String oldContStartDateTask)
	{
		this.oldContStartDateTask = oldContStartDateTask;
	}

	/**
	 * @return the oldContEndDateTask
	 */
	public String getOldContEndDateTask()
	{
		return oldContEndDateTask;
	}

	/**
	 * @param oldContEndDateTask the oldContEndDateTask to set
	 */
	public void setOldContEndDateTask(String oldContEndDateTask)
	{
		this.oldContEndDateTask = oldContEndDateTask;
	}

	/**
	 * @return the oldProcValTask
	 */
	public String getOldProcValTask()
	{
		return oldProcValTask;
	}

	/**
	 * @param oldProcValTask the oldProcValTask to set
	 */
	public void setOldProcValTask(String oldProcValTask)
	{
		this.oldProcValTask = oldProcValTask;
	}

	/**
	 * @return the competitionPoolTitle
	 */
	public String getCompetitionPoolTitle()
	{
		return competitionPoolTitle;
	}

	/**
	 * @param competitionPoolTitle the competitionPoolTitle to set
	 */
	public void setCompetitionPoolTitle(String competitionPoolTitle)
	{
		this.competitionPoolTitle = checkNullCondition(competitionPoolTitle);
	}

	/**
	 * @return the evaluationPoolMappingId
	 */
	public String getEvaluationPoolMappingId()
	{
		return evaluationPoolMappingId;
	}

	/**
	 * @param evaluationPoolMappingId the evaluationPoolMappingId to set
	 */
	public void setEvaluationPoolMappingId(String evaluationPoolMappingId)
	{
		this.evaluationPoolMappingId = evaluationPoolMappingId;
	}

	/**
	 * @return the evaluationGroupTitle
	 */
	public String getEvaluationGroupTitle()
	{
		return evaluationGroupTitle;
	}

	/**
	 * @param evaluationGroupTitle the evaluationGroupTitle to set
	 */
	public void setEvaluationGroupTitle(String evaluationGroupTitle)
	{
		this.evaluationGroupTitle = evaluationGroupTitle;
	}

	/**
	 * @return the isOpenEndedRfp
	 */
	public String getIsOpenEndedRfp()
	{
		return isOpenEndedRfp;
	}

	/**
	 * @param isOpenEndedRfp the isOpenEndedRfp to set
	 */
	public void setIsOpenEndedRfp(String isOpenEndedRfp)
	{
		this.isOpenEndedRfp = isOpenEndedRfp;
	}

	/* Updated in Release 6
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return filterToString();
		//[Start]R7.12.0 QC9311 Minimize Debug
		/*
		return "TaskDetailsBean [procurementTitle=" + procurementTitle + ", procurementEpin=" + procurementEpin
				+ ", procurementId=" + procurementId + ", budgetId=" + budgetId + ", contractId=" + contractId
				+ ", proposalId=" + proposalId + ", provider=" + provider + ", awardEpin=" + awardEpin + ", ct=" + ct
				+ ", taskName=" + taskName + ", submittedBy=" + submittedBy + ", submittedDate=" + submittedDate
				+ ", assignedTo=" + assignedTo + ", assignedToUserName=" + assignedToUserName + ", assignedDate="
				+ assignedDate + ", taskAction=" + taskAction + ", taskActions=" + taskActions + ", comment=" + comment
				+ ", workFlowId=" + workFlowId + ", taskId=" + taskId + ", reassignUserName=" + reassignUserName
				+ ", reassignUserId=" + reassignUserId + ", level=" + level + ", totalLevel=" + totalLevel
				+ ", taskStatus=" + taskStatus + ", commentsHistory=" + commentsHistory + ", taskType=" + taskType
				+ ", applicationId=" + applicationId + ", organizationId=" + organizationId + ", organizationName="
				+ organizationName + ", userId=" + userId + ", agencyId=" + agencyId + ", providerComment="
				+ providerComment + ", internalComment=" + internalComment + ", agencyQueryId=" + agencyQueryId
				+ ", providerQueryId=" + providerQueryId + ", entityId=" + entityId + ", entityType=" + entityType
				+ ", p8UserSession=" + p8UserSession + ", contractSourceId=" + contractSourceId + ", proposalTitle="
				+ proposalTitle + ", firstRoundEvalCompDate=" + firstRoundEvalCompDate + ", lastModifiedDate="
				+ lastModifiedDate + ", contractConfWob=" + contractConfWob + ", invoiceId=" + invoiceId
				+ ", isTaskScreen=" + isTaskScreen + ", newFYId=" + newFYId + ", startFiscalYear=" + startFiscalYear
				+ ", subBudgetId=" + subBudgetId + ", eventName=" + eventName + ", eventType=" + eventType
				+ ", entityTypeForAgency=" + entityTypeForAgency + ", agencyPrimaryContactId=" + agencyPrimaryContactId
				+ ", agencySecondaryContactId=" + agencySecondaryContactId + ", awardAmount=" + awardAmount
				+ ", evaluationStatusId=" + evaluationStatusId + ", isFirstLaunch=" + isFirstLaunch
				+ ", previousTaskStatus=" + previousTaskStatus + ", amendmentType=" + amendmentType + ", currentTab="
				+ currentTab + ", userRole=" + userRole + ", isTaskAssigned=" + isTaskAssigned
				+ ", isAssignableOperation=" + isAssignableOperation + ", launchCOF=" + launchCOF + ", agencyName="
				+ agencyName + ", launchOrgType=" + launchOrgType + ", submittedByName=" + submittedByName
				+ ", linkedWobNum=" + linkedWobNum + ", budgetAdvanceId=" + budgetAdvanceId + ", paymentId="
				+ paymentId + ", period=" + period + ", finalizeEvaluationDate=" + finalizeEvaluationDate
				+ ", awardSelectionDate=" + awardSelectionDate + ", currentTaskStatus=" + currentTaskStatus
				+ ", baseContractId=" + baseContractId + ", entityStatus=" + entityStatus + ", isFirstReached="
				+ isFirstReached + ", agencyPrimaryContact=" + agencyPrimaryContact + ", agencySecondaryContact="
				+ agencySecondaryContact + ", accPrimaryContactId=" + accPrimaryContactId + ", accSecondaryContactId="
				+ accSecondaryContactId + ", accPrimaryContact=" + accPrimaryContact + ", accSecondaryContact="
				+ accSecondaryContact + ", auditUserId=" + auditUserId + ", oldContStartDateTask="
				+ oldContStartDateTask + ", oldContEndDateTask=" + oldContEndDateTask + ", oldProcValTask="
				+ oldProcValTask + ", competitionPoolTitle=" + competitionPoolTitle + ", evaluationPoolMappingId="
				+ evaluationPoolMappingId + ", evaluationGroupTitle=" + evaluationGroupTitle + ", isOpenEndedRfp="
				+ isOpenEndedRfp + ", taskSource=" + taskSource + ", isAlreadyLaunchedFYTask="
				+ isAlreadyLaunchedFYTask + ", isNegotiationRequired=" + isNegotiationRequired + ", isNewLaunch="
				+ isNewLaunch + ", taskRequestedDate=" + taskRequestedDate + ", returnPaymentDetailId="
				+ returnPaymentDetailId + ", discFlagForUpdate=" + discFlagForUpdate + ", entityTypeTabLevel="
				+ entityTypeTabLevel + ", isEntityTypeTabLevel=" + isEntityTypeTabLevel + "]";
		 */
	}

	private String filterToString(){
		StringBuffer sb = new StringBuffer();
		
		sb.append("TaskDetailsBean [");
		if( procurementTitle != null && !procurementTitle.isEmpty() ) {sb.append(", procurementTitle=" + procurementTitle ) ;}
		if( procurementEpin != null && !procurementEpin.isEmpty() ) {sb.append(", procurementEpin=" + procurementEpin ) ;}
		if( budgetId != null && !budgetId.isEmpty() ) {sb.append(", budgetId=" + budgetId ) ;}
		if( contractId != null && !contractId.isEmpty() ) {sb.append(", contractId=" + contractId ) ;}
		if( proposalId != null && !proposalId.isEmpty() ) {sb.append(", proposalId=" + proposalId ) ;}
		if( provider != null && !provider.isEmpty() ) {sb.append(", provider=" + provider ); }
		if( submittedBy != null && !submittedBy.isEmpty() ) {sb.append(", submittedBy=" + submittedBy ); }
		if( submittedDate != null && !submittedDate.isEmpty() ) {sb.append(", submittedDate=" + submittedDate ); }
		if( assignedTo != null && !assignedTo.isEmpty() ) {sb.append(", assignedTo=" + assignedTo ) ;}
		if( assignedToUserName != null && !assignedToUserName.isEmpty() ) {sb.append(", assignedToUserName=" + assignedToUserName ); }
		if( assignedDate != null && !assignedDate.isEmpty() ) {sb.append(", assignedDate=" + assignedDate ); }
		if( taskAction != null && !taskAction.isEmpty() ) {sb.append(", taskAction=" + taskAction ); }
		if( taskActions != null && !taskActions.isEmpty() ) {sb.append(", taskActions=" + taskActions ); }
		if( reassignUserName != null && !reassignUserName.isEmpty() ) {sb.append(", reassignUserName=" + reassignUserName ); }
		if( reassignUserId != null && !reassignUserId.isEmpty() ) {sb.append(", reassignUserId=" + reassignUserId ); }
		if( level != null && !level.isEmpty() ) {sb.append(", level=" + level ); }
		if( totalLevel != null && !totalLevel.isEmpty() ) {sb.append(", totalLevel=" + totalLevel ) ;}
		if( taskStatus != null && !taskStatus.isEmpty() ) {sb.append(", taskStatus=" + taskStatus ) ;}
		if( commentsHistory != null && !commentsHistory.isEmpty() ) {sb.append(", commentsHistory=" + commentsHistory ); }
		if( taskType != null && !taskType.isEmpty() ) {sb.append(", taskType=" + taskType ); }
		if( applicationId != null && !applicationId.isEmpty() ) {sb.append(", applicationId=" + applicationId ); }
		if( organizationId != null && !organizationId.isEmpty() ) {sb.append(", organizationId=" + organizationId ); }
		if( organizationName != null && !organizationName.isEmpty() ) {sb.append(", organizationName=" + organizationName ); }
		if( userId != null && !userId.isEmpty() ) {sb.append(", userId=" + userId ); }
		if( agencyId != null && !agencyId.isEmpty() ) {sb.append(", agencyId=" + agencyId ); }
		if( providerComment != null && !providerComment.isEmpty() ) {sb.append(", providerComment=" + providerComment ); }
		if( internalComment != null && !internalComment.isEmpty() ) {sb.append(", internalComment=" + internalComment ) ;}
		if( agencyQueryId != null && !agencyQueryId.isEmpty() ) {sb.append(", agencyQueryId=" + agencyQueryId ) ;}
		if( providerQueryId != null && !providerQueryId.isEmpty() ) {sb.append(", providerQueryId=" + providerQueryId ) ;}
		if( entityId != null && !entityId.isEmpty() ) {sb.append(", entityId=" + entityId ); }
		if( entityType != null && !entityType.isEmpty() ) {sb.append(", entityType=" + entityType ); }
		if( p8UserSession != null  ) {sb.append(", p8UserSession=" + p8UserSession ); }
		if( contractSourceId != null && !contractSourceId.isEmpty() ) {sb.append(", contractSourceId=" + contractSourceId ); }
		if( proposalTitle != null && !proposalTitle.isEmpty() ) {sb.append(", proposalTitle=" + proposalTitle ) ;}
		if( firstRoundEvalCompDate != null && !firstRoundEvalCompDate.isEmpty() ) {sb.append(", firstRoundEvalCompDate=" + firstRoundEvalCompDate ) ;}
		if( lastModifiedDate != null && !lastModifiedDate.isEmpty() ) {sb.append(", lastModifiedDate=" + lastModifiedDate ) ;}
		if( contractConfWob != null && !contractConfWob.isEmpty() ) {sb.append(", contractConfWob=" + contractConfWob ); }
		if( invoiceId != null && !invoiceId.isEmpty() ) {sb.append(", invoiceId=" + invoiceId ); }
		if( isTaskScreen != null  ) {sb.append(", isTaskScreen=" + isTaskScreen ); }
		if( newFYId != null && !newFYId.isEmpty() ) {sb.append(", newFYId=" + newFYId ); }
		if( startFiscalYear != null && !startFiscalYear.isEmpty() ) {sb.append(", startFiscalYear=" + startFiscalYear ) ;}
		if( subBudgetId != null && !subBudgetId.isEmpty() ) {sb.append(", subBudgetId=" + subBudgetId ) ;}
		if( eventName != null && !eventName.isEmpty() ) {sb.append(", eventName=" + eventName ) ;}
		if( eventType != null && !eventType.isEmpty() ) {sb.append(", eventType=" + eventType ); }
		if( entityTypeForAgency != null && !entityTypeForAgency.isEmpty() ) {sb.append(", entityTypeForAgency=" + entityTypeForAgency ); }
		if( agencyPrimaryContactId != null && !agencyPrimaryContactId.isEmpty() ) {sb.append(", agencyPrimaryContactId=" + agencyPrimaryContactId ); }
		if( agencySecondaryContactId != null && !agencySecondaryContactId.isEmpty() ) {sb.append(", agencySecondaryContactId=" + agencySecondaryContactId ); }
		if( awardAmount != null && !awardAmount.isEmpty() ) {sb.append(", awardAmount=" + awardAmount ) ;}
		if( evaluationStatusId != null && !evaluationStatusId.isEmpty() ) {sb.append(", evaluationStatusId=" + evaluationStatusId ) ;}
		if( isFirstLaunch != null  ) {sb.append(", isFirstLaunch=" + isFirstLaunch ) ;}
		if( previousTaskStatus != null && !previousTaskStatus.isEmpty() ) {sb.append(", previousTaskStatus=" + previousTaskStatus ); }
		if( amendmentType != null && !amendmentType.isEmpty() ) {sb.append(", amendmentType=" + amendmentType ); }
		if( currentTab != null && !currentTab.isEmpty() ) {sb.append(", currentTab=" + currentTab ); }
		if( userRole != null && !userRole.isEmpty() ) {sb.append(", userRole=" + userRole ); }
		if( isTaskAssigned != null  ) {sb.append(", isTaskAssigned=" + isTaskAssigned ) ;}
		if( isAssignableOperation != null   ) {sb.append(", isAssignableOperation=" + isAssignableOperation ) ;}
		if( launchCOF != null ) {sb.append(", launchCOF=" + launchCOF ) ;}
		if( agencyName != null && !agencyName.isEmpty() ) {sb.append(", agencyName=" + agencyName ); }
		if( launchOrgType != null && !launchOrgType.isEmpty() ) {sb.append(", launchOrgType=" + launchOrgType ); }
		if( submittedByName != null && !submittedByName.isEmpty() ) {sb.append(", submittedByName=" + submittedByName ); }
		if( linkedWobNum != null && !linkedWobNum.isEmpty() ) {sb.append(", linkedWobNum=" + linkedWobNum ); }
		if( budgetAdvanceId != null && !budgetAdvanceId.isEmpty() ) {sb.append(", budgetAdvanceId=" + budgetAdvanceId ) ;}
		sb.append(", paymentId=" + paymentId ) ;
		if( period != null && !period.isEmpty() ) {sb.append(", period=" + period ) ;}
		if( finalizeEvaluationDate != null && !finalizeEvaluationDate.isEmpty() ) {sb.append(", finalizeEvaluationDate=" + finalizeEvaluationDate ); }
		if( awardSelectionDate != null && !awardSelectionDate.isEmpty() ) {sb.append(", awardSelectionDate=" + awardSelectionDate ); }
		if( currentTaskStatus != null && !currentTaskStatus.isEmpty() ) {sb.append(", currentTaskStatus=" + currentTaskStatus ); }
		if( baseContractId != null && !baseContractId.isEmpty() ) {sb.append(", baseContractId=" + baseContractId ); }
		if( entityStatus != null && !entityStatus.isEmpty() ) {sb.append(", entityStatus=" + entityStatus ) ;}
		sb.append(", isFirstReached=" + isFirstReached ) ;
		if( agencyPrimaryContact != null && !agencyPrimaryContact.isEmpty() ) {sb.append(", agencyPrimaryContact=" + agencyPrimaryContact ) ;}
		if( agencySecondaryContact != null && !agencySecondaryContact.isEmpty() ) {sb.append(", agencySecondaryContact=" + agencySecondaryContact ); }
		if( accPrimaryContactId != null && !accPrimaryContactId.isEmpty() ) {sb.append(", accPrimaryContactId=" + accPrimaryContactId ); }
		if( accSecondaryContactId != null && !accSecondaryContactId.isEmpty() ) {sb.append(", accSecondaryContactId=" + accSecondaryContactId ); }
		if( accPrimaryContact != null && !accPrimaryContact.isEmpty() ) {sb.append(", accPrimaryContact=" + accPrimaryContact ); }
		if( accSecondaryContact != null && !accSecondaryContact.isEmpty() ) {sb.append(", accSecondaryContact=" + accSecondaryContact ) ;}
		if( auditUserId != null && !auditUserId.isEmpty() ) {sb.append(", auditUserId=" + auditUserId ) ;}
		if( oldContStartDateTask != null && !oldContStartDateTask.isEmpty() ) {sb.append(", oldContStartDateTask=" + oldContStartDateTask ) ;}
		if( oldContEndDateTask != null && !oldContEndDateTask.isEmpty() ) {sb.append(", oldContEndDateTask=" + oldContEndDateTask ); }
		if( oldProcValTask != null && !oldProcValTask.isEmpty() ) {sb.append(", oldProcValTask=" + oldProcValTask ); }
		if( competitionPoolTitle != null && !competitionPoolTitle.isEmpty() ) {sb.append(", competitionPoolTitle=" + competitionPoolTitle ); }
		if( evaluationPoolMappingId != null && !evaluationPoolMappingId.isEmpty() ) {sb.append(", evaluationPoolMappingId=" + evaluationPoolMappingId ); }
		if( evaluationGroupTitle != null && !evaluationGroupTitle.isEmpty() ) {sb.append(", evaluationGroupTitle=" + evaluationGroupTitle ) ;}
		sb.append(", isOpenEndedRfp=" + isOpenEndedRfp ) ;
		sb.append(", workFlowId=" + workFlowId  ) ; // added R 8.10.0
		if( taskSource != null && !taskSource.isEmpty() ) {sb.append(", taskSource=" + taskSource ) ;}
		if( isAlreadyLaunchedFYTask != null ) {sb.append(", isAlreadyLaunchedFYTask=" + isAlreadyLaunchedFYTask ); }
		if( isNegotiationRequired != null  ) {sb.append(", isNegotiationRequired=" + isNegotiationRequired ); }
		if( isNewLaunch != null  ) {sb.append(", isNewLaunch=" + isNewLaunch ); }
		if( returnPaymentDetailId != null && !returnPaymentDetailId.isEmpty() ) {sb.append(", returnPaymentDetailId=" + returnPaymentDetailId ); }
		if( discFlagForUpdate != null  ) {sb.append(", discFlagForUpdate=" + discFlagForUpdate ) ;}
		if( entityTypeTabLevel != null  ) {sb.append(", entityTypeTabLevel=" + entityTypeTabLevel ); }
		if( isEntityTypeTabLevel != null  ) {sb.append(", isEntityTypeTabLevel=" + isEntityTypeTabLevel ) ;}
		sb.append("]");
		
		return sb.toString();
	}
	//[End]R7.12.0 QC9311 Minimize Debug	
	
	//Added for R6: return payment review task
		public String getReturnPaymentDetailId() {
			return returnPaymentDetailId;
		}

	public void setReturnPaymentDetailId(String returnPaymentDetailId) {
		this.returnPaymentDetailId = returnPaymentDetailId;
	}
	
	public Date getTaskRequestedDate()
	{
		return taskRequestedDate;
	}

	public void setTaskRequestedDate(Date taskRequestedDate)
	{
		this.taskRequestedDate = taskRequestedDate;
	}

	public String getEntityName()
	{
		return entityName;
	}

	public void setEntityName(String entityName)
	{
		this.entityName = entityName;
	}
	
	//R6 End: return payment review task
}
