package com.nyc.hhs.model;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.nyc.hhs.constants.HHSConstants;
import com.nyc.hhs.exception.ApplicationException;
import com.nyc.hhs.util.DateUtil;
import com.nyc.hhs.util.HHSUtil;

/**
 * This class is a bean which maintains the NYC Agency information.
 * 
 */

public class AgencyTaskBean extends BaseFilter
{
	public String taskName = HHSConstants.EMPTY_STRING;
	// Start Added in R5
	public String submittedBy = HHSConstants.EMPTY_STRING;
	public String submittedById = HHSConstants.EMPTY_STRING;

	public String getSubmittedById()
	{
		return submittedById;
	}

	public void setSubmittedById(String submittedById)
	{
		this.submittedById = submittedById;
	}

	public String ctNumber = HHSConstants.EMPTY_STRING;

	public String getCtNumber()
	{
		return ctNumber;
	}

	public String getSubmittedBy()
	{
		return submittedBy;
	}

	public void setSubmittedBy(String submittedBy)
	{
		this.submittedBy = submittedBy;
	}

	public void setCtNumber(String ctNumber)
	{
		this.ctNumber = ctNumber;
	}

	public String awardEpin = HHSConstants.EMPTY_STRING;

	public String getAwardEpin()
	{
		return awardEpin;
	}

	public void setAwardEpin(String awardEpin)
	{
		this.awardEpin = awardEpin;
	}

	public String procurementmentEpin = HHSConstants.EMPTY_STRING;

	public String getProcurementmentEpin()
	{
		return procurementmentEpin;
	}

	public void setProcurementmentEpin(String procurementmentEpin)
	{
		this.procurementmentEpin = procurementmentEpin;
	}

	public List<String> taskNameList;

	public List<String> getTaskNameList()
	{
		return taskNameList;
	}

	public void setTaskNameList(List<String> taskNameList)
	{
		this.taskNameList = taskNameList;
	}

	// End Added in R5
	public String agencyId = HHSConstants.EMPTY_STRING;
	public String providerName = HHSConstants.EMPTY_STRING;
	public String dateCreated;
	public String lastAssigned;
	public String status = HHSConstants.EMPTY_STRING;
	public String wobNumber;
	public HashMap noOfTask;
	public String assignedTo = HHSConstants.EMPTY_STRING;
	public String procurementTitle = HHSConstants.EMPTY_STRING;
	public String taskLevel;
	public HashMap filterProp;
	public int paginationNum = 1;
	public String orderBy;
	public String programName;
	public String r3Task;
	public String entityId;
	public String entityType;
	public String r2TaskSelectAllDisable;
	public String taskId;

	public String submittedFromDate;
	// Start Added in R5
	public Date submittedDate;
	public Date taskAssignDate;

	public Date getSubmittedDate()
	{
		return submittedDate;
	}

	public void setSubmittedDate(Date submittedDate)
	{
		this.submittedDate = submittedDate;
	}

	public Date getTaskAssignDate()
	{
		return taskAssignDate;
	}

	public void setTaskAssignDate(Date taskAssignDate)
	{
		this.taskAssignDate = taskAssignDate;
	}

	// End Added in R5
	public String submittedToDate = HHSConstants.EMPTY_STRING;
	public String assignedFromDate;
	public String assignedToDate = HHSConstants.EMPTY_STRING;

	public String procurementId;
	public String competitionPoolTitle = HHSConstants.EMPTY_STRING;
	public String competitionPoolId;

	// Start || Changes done for enhancement 6636 for Release 3.12.0
	public Integer proposalId;
	// Start Added in R5
	public String proposalIdExport = HHSConstants.EMPTY_STRING;

	public String getProposalIdExport()
	{
		return proposalIdExport;
	}

	public void setProposalIdExport(String proposalIdExport)
	{
		this.proposalIdExport = proposalIdExport;
	}

	// End Added in R5
	public Integer getProposalId()
	{
		return proposalId;
	}

	public void setProposalId(Integer proposalId)
	{
		this.proposalId = proposalId;
	}

	// End || Changes done for enhancement 6636 for Release 3.12.0

	public String getProcurementId()
	{
		return procurementId;
	}

	public void setProcurementId(String procurementId)
	{
		this.procurementId = procurementId;
	}

	public String getCompetitionPoolTitle()
	{
		return competitionPoolTitle;
	}

	public void setCompetitionPoolTitle(String competitionPoolTitle)
	{
		this.competitionPoolTitle = competitionPoolTitle;
	}

	public String getCompetitionPoolId()
	{
		return competitionPoolId;
	}

	public void setCompetitionPoolId(String competitionPoolId)
	{
		this.competitionPoolId = competitionPoolId;
	}

	/**
	 * default constructor
	 */
	public AgencyTaskBean()
	{
		setFirstSort(HHSConstants.PROPERTY_PE_SUBMITTED_DATE);
		setSecondSort(HHSConstants.PROPERTY_PE_TASK_TYPE);
		setFirstSortType(HHSConstants.DESCENDING);
		setSecondSortType(HHSConstants.ASCENDING);
		setSortColumnName(HHSConstants.DATE_CREATED);
		setFirstSortDate(true);
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
	 * @return the r2TaskSelectAllDisable
	 */
	public String getR2TaskSelectAllDisable()
	{
		return r2TaskSelectAllDisable;
	}

	/**
	 * @param r2TaskSelectAllDisable the r2TaskSelectAllDisable to set
	 */
	public void setR2TaskSelectAllDisable(String r2TaskSelectAllDisable)
	{
		this.r2TaskSelectAllDisable = r2TaskSelectAllDisable;
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
	 * @return the r3Task
	 */
	public String getR3Task()
	{
		return r3Task;
	}

	/**
	 * @param r3Task the r3Task to set
	 */
	public void setR3Task(String r3Task)
	{
		this.r3Task = r3Task;
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
	 * @return the submittedFromDate
	 */
	public String getSubmittedFromDate()
	{
		return submittedFromDate;
	}

	/**
	 * @param submittedFromDate the submittedFromDate to set
	 */
	public void setSubmittedFromDate(String submittedFromDate)
	{
		this.submittedFromDate = submittedFromDate;
	}

	/**
	 * @return the submittedToDate
	 */
	public String getSubmittedToDate()
	{
		return submittedToDate;
	}

	/**
	 * @param submittedToDate the submittedToDate to set
	 */
	public void setSubmittedToDate(String submittedToDate)
	{
		this.submittedToDate = submittedToDate;
	}

	/**
	 * @return the assignedFromDate
	 */
	public String getAssignedFromDate()
	{
		return assignedFromDate;
	}

	/**
	 * @param assignedFromDate the assignedFromDate to set
	 */
	public void setAssignedFromDate(String assignedFromDate)
	{
		this.assignedFromDate = assignedFromDate;
	}

	/**
	 * @return the assignedToDate
	 */
	public String getAssignedToDate()
	{
		return assignedToDate;
	}

	/**
	 * @param assignedToDate the assignedToDate to set
	 */
	public void setAssignedToDate(String assignedToDate)
	{
		this.assignedToDate = assignedToDate;
	}

	/**
	 * @return the programName
	 */
	public String getProgramName()
	{
		return programName;
	}

	/**
	 * @param programName the programName to set
	 */
	public void setProgramName(String programName)
	{
		this.programName = programName;
	}

	/**
	 * @return the orderBy
	 */
	public String getOrderBy()
	{
		return orderBy;
	}

	/**
	 * @param orderBy the orderBy to set
	 */
	public void setOrderBy(String orderBy)
	{
		this.orderBy = orderBy;
	}

	/**
	 * @return the taskLevel
	 */
	public String getTaskLevel()
	{
		return taskLevel;
	}

	/**
	 * @param taskLevel the taskLevel to set
	 */
	public void setTaskLevel(String taskLevel)
	{
		this.taskLevel = taskLevel;
	}

	/**
	 * @return the filterProp
	 */
	public HashMap getFilterProp()
	{
		return filterProp;
	}

	/**
	 * @param filterProp the filterProp to set
	 */
	public void setFilterProp(HashMap filterProp)
	{
		this.filterProp = filterProp;
	}

	/**
	 * @return the paginationNum
	 */
	public int getPaginationNum()
	{
		return paginationNum;
	}

	/**
	 * @param paginationNum the paginationNum to set
	 */
	public void setPaginationNum(int paginationNum)
	{
		this.paginationNum = paginationNum;
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
	 * @return the providerName
	 */
	public String getProviderName()
	{
		return providerName;
	}

	/**
	 * @param providerName the providerName to set
	 */
	public void setProviderName(String providerName)
	{
		this.providerName = providerName;
	}

	/**
	 * @return the dateCreated
	 */
	public String getDateCreated()
	{
		return dateCreated;
	}

	/**
	 * @param dateCreated the dateCreated to set
	 * @throws ApplicationException
	 */
	public void setDateCreated(String dateCreated) throws ApplicationException
	{
		this.dateCreated = DateUtil.getDateMMddYYYYFormat(HHSUtil.getDateFromEpochTime(dateCreated));
	}

	/**
	 * @return the lastAssigned
	 */
	public String getLastAssigned()
	{
		return lastAssigned;
	}

	/**
	 * @param lastAssigned the lastAssigned to set
	 * @throws ApplicationException
	 */
	public void setLastAssigned(String lastAssigned) throws ApplicationException
	{

		this.lastAssigned = DateUtil.getDateMMddYYYYFormat(HHSUtil.getDateFromEpochTime(lastAssigned));
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
	 * @return the wobNumber
	 */
	public String getWobNumber()
	{
		return wobNumber;
	}

	/**
	 * @param wobNumber the wobNumber to set
	 */
	public void setWobNumber(String wobNumber)
	{
		this.wobNumber = wobNumber;
	}

	/**
	 * @return the noOfTask
	 */
	public HashMap getNoOfTask()
	{
		return noOfTask;
	}

	/**
	 * @param noOfTask the noOfTask to set
	 */
	public void setNoOfTask(HashMap noOfTask)
	{
		this.noOfTask = noOfTask;
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

	/*
	 * [Start]Add InvoiceNumber for R3.7.0 to get Service date from portal DB
	 */
	public String invoiceNumber;
	public String serviceStartDate = HHSConstants.EMPTY_STRING;
	public String serviceEndDate = HHSConstants.EMPTY_STRING;
	public String invoiceSvcDate = HHSConstants.EMPTY_STRING;

	public String getInvoiceSvcDate() {
		return invoiceSvcDate;
	}

	public void setInvoiceSvcDate(String invoiceSvcDate) {
		this.invoiceSvcDate = invoiceSvcDate;
	}

	public String getInvoiceNumber()
	{
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber)
	{
		this.invoiceNumber = invoiceNumber;
	}

	public String getServiceStartDate()
	{
		return serviceStartDate;
	}

	public void setServiceStartDate(String serviceStartDate)
	{
		this.serviceStartDate = serviceStartDate;
	}

	public String getServiceEndDate()
	{
		return this.serviceEndDate;
	}

	public void setServiceEndDate(String serviceEndDate)
	{
		this.serviceEndDate = serviceEndDate;
	}

	/*
	 * [End]Add InvoiceNumber for R3.7.9 to get Service date from portal DB
	 */

	@Override
	public String toString()
	{
		return "AgencyTaskBean [taskName=" + taskName + ", agencyId=" + agencyId + ", providerName=" + providerName
				+ ", dateCreated=" + dateCreated + ", lastAssigned=" + lastAssigned + ", status=" + status
				+ ", wobNumber=" + wobNumber + ", noOfTask=" + noOfTask + ", assignedTo=" + assignedTo
				+ ", procurementTitle=" + procurementTitle + ", taskLevel=" + taskLevel + ", filterProp=" + filterProp
				+ ", paginationNum=" + paginationNum + ", orderBy="
				+ orderBy
				+ ", programName="
				+ programName
				// Add InvoiceNumber(InvoiceId in Invoice Table at Portal DB)
				// R3.7.0
				+ ", r3Task=" + r3Task + ", InvoiceNumber=" + invoiceNumber + ", entityId=" + entityId
				+ ", entityType=" + entityType + ", r2TaskSelectAllDisable=" + r2TaskSelectAllDisable + ", taskId="
				+ taskId + ", submittedFromDate=" + submittedFromDate + ", submittedToDate=" + submittedToDate
				+ ", assignedFromDate=" + assignedFromDate + ", assignedToDate=" + assignedToDate
				+ ", ServiceStartDate=" + serviceStartDate + ", ServiceEndDate=" + serviceEndDate + "]";
	}
	
	public String toStringForExport()
	{
		//Emergency build 4.0.1 defectId 8354 Space Added for serviceStartDate, serviceEndDate
		return agencyId + "," + taskName + "," + procurementTitle + "," + procurementmentEpin + "," + awardEpin + ","
				+ providerName + "," + ctNumber + ", " + serviceStartDate + ", " + serviceEndDate + "," + submittedBy
				+ "," + submittedDate + "," + assignedTo + "," + taskAssignDate + "," + status + ","
				+ competitionPoolTitle + "," + proposalIdExport;
	}

}
