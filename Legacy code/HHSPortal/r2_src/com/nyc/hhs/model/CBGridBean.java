package com.nyc.hhs.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.nyc.hhs.constants.HHSConstants;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * This class is a bean which maintains grid keys along with standard data that
 * includes audit information
 * 
 * <ul>
 * <li>This bean is used for grid functionality and as buffer for to/for of
 * values in grid
 * </ul>
 * 
 */
public class CBGridBean
{

	@RegExp(value ="^\\d{0,22}")
	private String procurementID = HHSConstants.EMPTY_STRING;
	@RegExp(value ="^\\d{0,22}")
	private String contractID = HHSConstants.EMPTY_STRING;
	private String amendmentContractID = HHSConstants.EMPTY_STRING;
	private String fiscalYearID;
	private String contractTypeId;
	private String contractBudgetID;
	private List<String> amendedContractSubBudgetID;
	private String amendedContractSubBudgetIdComma;
	private String modifiedContractBudgetID;
	@RegExp(value ="^\\d{0,22}")
	private String subBudgetID;
	private String modifiedSubBudgetID;
	private String subBudgetName;
	private Date createdDate;
	private String createdByUserId;
	private Date modifiedDate;
	private String modifiedByUserId;
	private String counter;
	private String gridType;
	private Integer fiscalYearCounter;
	private int noOfyears;
	private String budgetTypeId;
	private String invoiceId;
	private String subBudgetAmount;
	private String[] invoiceStatusIdList = new String[0];
	private String entryTypeId;
	private String profServiceTypeId;
	private String subHeader;
	private String documentUploadTo;
	private String contractStatusId;
	private String budgetStatusId;
	private String budgetStatusName;
	private String transactionName;
	private String invoiceStatus;
	private String personnelServiceTypeId;
	private String agencyId;
	private String parentBudgetId;
	private String parentSubBudgetId;
	private String budgetAmount;
	private String modifyByProvider = HHSConstants.EMPTY_STRING;
	private String modifyByAgency = HHSConstants.EMPTY_STRING;
	private String type;
	private Boolean coaDocType = Boolean.FALSE;
	private String statusId;
	private String amendmentType = HHSConstants.EMPTY_STRING;
	private String budgetAdvanceId;
	private Boolean isNewFYScreen = false;
	private boolean isProcCerTaskScreen = HHSConstants.BOOLEAN_FALSE;
	private String budgetStartYear = "";

	private String tableName = "";
	private String tableAmountColumn = "";
	private String tableId = "";
	private String invoiceAmountCurrent = "";
	private String invoiceUnitsCurrent = "";
	private String lineItemId = "";
	private List<String> partialMergedRowId = new ArrayList<String>();
	// Start: Added in R6
	// This attribute is changed from existingBudget to usesFte
	private String usesFte;
	private String defaultCityFte;
	// Added for Defect-8478
	private String newRecord;
	//Added in R7 for Program Income
	private String empPosition = HHSConstants.STRING_ZERO;
	private String budgetCategory = HHSConstants.EMPTY_STRING;
	private String PIEntryTypeId = HHSConstants.EMPTY_STRING;
	private String isOldPI = HHSConstants.EMPTY_STRING;
	// Start QC 8394 R 7.8.0 Add Unallocated Fund line
	private String unallocatedFund = HHSConstants.EMPTY_STRING;
	
	public String getUnallocatedFund() {
		return unallocatedFund;
	}

	public void setUnallocatedFund(String unallocatedFund) {
		this.unallocatedFund = unallocatedFund;
	}
	// End QC 8394 R 7.8.0 Add Unallocated Fund line
	public String getIsOldPI()
	{
		return isOldPI;
	}

	public void setIsOldPI(String isOldPI)
	{
		this.isOldPI = isOldPI;
	}

	public String getPIEntryTypeId()
	{
		return PIEntryTypeId;
	}

	public void setPIEntryTypeId(String pIEntryTypeId)
	{
		PIEntryTypeId = pIEntryTypeId;
	}

	public String getBudgetCategory()
	{
		return budgetCategory;
	}

	public void setBudgetCategory(String budgetCategory)
	{
		this.budgetCategory = budgetCategory;
	}
	
	public String getEmpPosition()
	{
		return empPosition;
	}

	public void setEmpPosition(String empPosition)
	{
		this.empPosition = empPosition;
	}
    // R7 changes end
	/**
	 * @return the usesFte
	 */
	public String getUsesFte()
	{
		return usesFte;
	}

	/**
	 * @param usesFte the usesFte to set
	 */
	public void setUsesFte(String usesFte)
	{
		this.usesFte = usesFte;
	}

	/**
	 * @return the defaultCityFte
	 */
	public String getDefaultCityFte()
	{
		return defaultCityFte;
	}

	/**
	 * @param defaultCityFte the defaultCityFte to set
	 */
	public void setDefaultCityFte(String defaultCityFte)
	{
		this.defaultCityFte = defaultCityFte;
	}

	/**
	 * @return the newRecord
	 */
	public String getNewRecord()
	{
		return newRecord;
	}

	/**
	 * @param newRecord the newRecord to set
	 */
	public void setNewRecord(String newRecord)
	{
		this.newRecord = newRecord;
	}

	// End: Added in R6

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public String getTableAmountColumn()
	{
		return tableAmountColumn;
	}

	public void setTableAmountColumn(String tableAmountColumn)
	{
		this.tableAmountColumn = tableAmountColumn;
	}

	public boolean getIsProcCerTaskScreen()
	{
		return isProcCerTaskScreen;
	}

	public void setIsProcCerTaskScreen(boolean isProcCerTaskScreen)
	{
		this.isProcCerTaskScreen = isProcCerTaskScreen;
	}

	public Boolean getIsNewFYScreen()
	{
		return isNewFYScreen;
	}

	public void setIsNewFYScreen(Boolean isNewFYScreen)
	{
		this.isNewFYScreen = isNewFYScreen;
	}

	/**
	 * @return the contractTypeId
	 */
	public String getContractTypeId()
	{
		return contractTypeId;
	}

	/**
	 * @return the amendmentContractID
	 */
	public String getAmendmentContractID()
	{
		return amendmentContractID;
	}

	/**
	 * @param amendmentContractID the amendmentContractID to set
	 */
	public void setAmendmentContractID(String amendmentContractID)
	{
		this.amendmentContractID = amendmentContractID;
	}

	/**
	 * @param contractTypeId the contractTypeId to set
	 */
	public void setContractTypeId(String contractTypeId)
	{
		this.contractTypeId = contractTypeId;
	}

	public Boolean getCoaDocType()
	{
		return coaDocType;
	}

	public void setCoaDocType(Boolean coaDocType)
	{
		this.coaDocType = coaDocType;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * @return the budgetAmount
	 */
	public String getBudgetAmount()
	{
		return budgetAmount;
	}

	/**
	 * @param budgetAmount the budgetAmount to set
	 */
	public void setBudgetAmount(String budgetAmount)
	{
		this.budgetAmount = budgetAmount;
	}

	/**
	 * @return the baseContractBudgetID
	 */
	public String getModifiedContractBudgetID()
	{
		return modifiedContractBudgetID;
	}

	/**
	 * @param baseContractBudgetID the baseContractBudgetID to set
	 */
	public void setModifiedContractBudgetID(String modifiedContractBudgetID)
	{
		this.modifiedContractBudgetID = modifiedContractBudgetID;
	}

	/**
	 * @return the baseSubBudgetID
	 */
	public String getModifiedSubBudgetID()
	{
		return modifiedSubBudgetID;
	}

	/**
	 * @param baseSubBudgetID the baseSubBudgetID to set
	 */
	public void setModifiedSubBudgetID(String modifiedSubBudgetID)
	{
		this.modifiedSubBudgetID = modifiedSubBudgetID;
	}

	/**
	 * @return the personnelServiceTypeId
	 */
	public String getPersonnelServiceTypeId()
	{
		return personnelServiceTypeId;
	}

	/**
	 * @param personnelServiceTypeId the personnelServiceTypeId to set
	 */
	public void setPersonnelServiceTypeId(String personnelServiceTypeId)
	{
		this.personnelServiceTypeId = personnelServiceTypeId;
	}

	public String getTransactionName()
	{
		return transactionName;
	}

	public void setTransactionName(String transactionName)
	{
		this.transactionName = transactionName;
	}

	/**
	 * 
	 * @return
	 */
	public String getSubHeader()
	{
		return subHeader;
	}

	/**
	 * 
	 * @param subHeader
	 */
	public void setSubHeader(String subHeader)
	{
		this.subHeader = subHeader;
	}

	/**
	 * @return the gridType
	 */
	public String getGridType()
	{
		return gridType;
	}

	/**
	 * @param gridType the gridType to set
	 */
	public void setGridType(String gridType)
	{
		this.gridType = gridType;
	}

	/**
	 * @return the fiscalYearCounter
	 */
	public Integer getFiscalYearCounter()
	{
		return fiscalYearCounter;
	}

	/**
	 * @param fiscalYearCounter the fiscalYearCounter to set
	 */
	public void setFiscalYearCounter(Integer fiscalYearCounter)
	{
		this.fiscalYearCounter = fiscalYearCounter;
	}

	/**
	 * @return the noOfyears
	 */
	public int getNoOfyears()
	{
		return noOfyears;
	}

	/**
	 * @param noOfyears the noOfyears to set
	 */
	public void setNoOfyears(int noOfyears)
	{
		this.noOfyears = noOfyears;
	}

	/**
	 * @return the procurementID
	 */
	public String getProcurementID()
	{
		return procurementID;
	}

	/**
	 * @param procurementID the procurementID to set
	 */
	public void setProcurementID(String procurementID)
	{
		this.procurementID = procurementID;
	}

	/**
	 * @return the contractID
	 */
	public String getContractID()
	{
		return contractID;
	}

	/**
	 * @param contractID the contractID to set
	 */
	public void setContractID(String contractID)
	{
		this.contractID = contractID;
	}

	/**
	 * @return the fiscalYearID
	 */
	public String getFiscalYearID()
	{
		return fiscalYearID;
	}

	/**
	 * @param fiscalYearID the fiscalYearID to set
	 */
	public void setFiscalYearID(String fiscalYearID)
	{
		this.fiscalYearID = fiscalYearID;
	}

	/**
	 * @return the contractBudgetID
	 */
	public String getContractBudgetID()
	{
		return contractBudgetID;
	}

	/**
	 * @param contractBudgetID the contractBudgetID to set
	 */
	public void setContractBudgetID(String contractBudgetID)
	{
		this.contractBudgetID = contractBudgetID;
	}

	/**
	 * @return the subBudgetID
	 */
	public String getSubBudgetID()
	{
		return subBudgetID;
	}

	/**
	 * @param subBudgetID the subBudgetID to set
	 */
	public void setSubBudgetID(String subBudgetID)
	{
		this.subBudgetID = subBudgetID;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate()
	{
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate)
	{
		this.createdDate = createdDate;
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
	 * @return the modifiedDate
	 */
	public Date getModifiedDate()
	{
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate)
	{
		this.modifiedDate = modifiedDate;
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

	/**
	 * @return the counter
	 */
	public String getCounter()
	{
		return counter;
	}

	/**
	 * @return the invoiceStatus
	 */
	public String getInvoiceStatus()
	{
		return invoiceStatus;
	}

	/**
	 * @param invoiceStatus the invoiceStatus to set
	 */
	public void setInvoiceStatus(String invoiceStatus)
	{
		this.invoiceStatus = invoiceStatus;
	}

	/**
	 * @param counter the counter to set
	 */
	public void setCounter(String counter)
	{
		this.counter = counter;
	}

	public void setBudgetTypeId(String budgetTypeId)
	{
		this.budgetTypeId = budgetTypeId;
	}

	public String getBudgetTypeId()
	{
		return budgetTypeId;
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
		this.invoiceId = invoiceId;
	}

	/**
	 * @return the subBudgetAmount
	 */
	public String getSubBudgetAmount()
	{
		return subBudgetAmount;
	}

	/**
	 * @param subBudgetAmount the subBudgetAmount to set
	 */
	public void setSubBudgetAmount(String subBudgetAmount)
	{
		this.subBudgetAmount = subBudgetAmount;
	}

	public void setInvoiceStatusIdList(String[] invoiceStatusIdList)
	{
		// update in R5
		this.invoiceStatusIdList = invoiceStatusIdList.clone();
	}

	public String[] getInvoiceStatusIdList()
	{
		// update in R5
		String[] invoiceStatusList = new String[invoiceStatusIdList.length];
		System.arraycopy(invoiceStatusIdList, 0, invoiceStatusList, 0, invoiceStatusIdList.length);
		return invoiceStatusList;
	}

	public void setEntryTypeId(String entryTypeId)
	{
		this.entryTypeId = entryTypeId;
	}

	public String getEntryTypeId()
	{
		return entryTypeId;
	}

	/**
	 * @return the profServiceTypeId
	 */
	public String getProfServiceTypeId()
	{
		return profServiceTypeId;
	}

	/**
	 * @param profServiceTypeId the profServiceTypeId to set
	 */
	public void setProfServiceTypeId(String profServiceTypeId)
	{
		this.profServiceTypeId = profServiceTypeId;
	}

	/**
	 * @return the subBudgetName
	 */
	public String getSubBudgetName()
	{
		return subBudgetName;
	}

	/**
	 * @param subBudgetName the subBudgetName to set
	 */
	public void setSubBudgetName(String subBudgetName)
	{
		this.subBudgetName = subBudgetName;
	}

	/**
	 * @return the documentUploadTo
	 */
	public String getDocumentUploadTo()
	{
		return documentUploadTo;
	}

	/**
	 * @param documentUploadTo the documentUploadTo to set
	 */
	public void setDocumentUploadTo(String documentUploadTo)
	{
		this.documentUploadTo = documentUploadTo;
	}

	/**
	 * @return the contractStatusId
	 */
	public String getContractStatusId()
	{
		return contractStatusId;
	}

	/**
	 * @param contractStatusId the contractStatusId to set
	 */
	public void setContractStatusId(String contractStatusId)
	{
		this.contractStatusId = contractStatusId;
	}

	/**
	 * @return the budgetStatusId
	 */
	public String getBudgetStatusId()
	{
		return budgetStatusId;
	}

	/**
	 * @param budgetStatusId the budgetStatusId to set
	 */
	public void setBudgetStatusId(String budgetStatusId)
	{
		this.budgetStatusId = budgetStatusId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((modifiedDate == null) ? 0 : modifiedDate.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		CBGridBean loOther = (CBGridBean) obj;
		if (modifiedDate == null)
		{
			if (loOther.modifiedDate != null)
			{
				return false;
			}
		}
		else if (!modifiedDate.equals(loOther.modifiedDate))
		{
			return false;
		}
		return true;
	}

	/**
	 * @return the budgetStatusName
	 */
	public String getBudgetStatusName()
	{
		return budgetStatusName;
	}

	/**
	 * @param budgetStatusName the budgetStatusName to set
	 */
	public void setBudgetStatusName(String budgetStatusName)
	{
		this.budgetStatusName = budgetStatusName;
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
	 * @return the parentBudgetId
	 */
	public String getParentBudgetId()
	{
		return parentBudgetId;
	}

	/**
	 * @param parentBudgetId the parentBudgetId to set
	 */
	public void setParentBudgetId(String parentBudgetId)
	{
		this.parentBudgetId = parentBudgetId;
	}

	/**
	 * @return the parentSubBudgetId
	 */
	public String getParentSubBudgetId()
	{
		return parentSubBudgetId;
	}

	/**
	 * @param parentSubBudgetId the parentSubBudgetId to set
	 */
	public void setParentSubBudgetId(String parentSubBudgetId)
	{
		this.parentSubBudgetId = parentSubBudgetId;
	}

	/**
	 * @return the modifyByProvider
	 */
	public String getModifyByProvider()
	{
		return modifyByProvider;
	}

	/**
	 * @param modifyByProvider the modifyByProvider to set
	 */
	public void setModifyByProvider(String modifyByProvider)
	{
		this.modifyByProvider = modifyByProvider;
	}

	/**
	 * @return the modifyByAgency
	 */
	public String getModifyByAgency()
	{
		return modifyByAgency;
	}

	/**
	 * @param modifyByAgency the modifyByAgency to set
	 */
	public void setModifyByAgency(String modifyByAgency)
	{
		this.modifyByAgency = modifyByAgency;
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
		this.budgetAdvanceId = budgetAdvanceId;
	}

	//[Start]R7.12.0 QC9311 Minimize Debug
	@Override
	public String toString()
	{
		return filterToString();
		
/*		return "CBGridBean [procurementID=" + procurementID + ", contractID=" + contractID + ", fiscalYearID="
				+ fiscalYearID + ", contractTypeId=" + contractTypeId + ", contractBudgetID=" + contractBudgetID
				+ ", modifiedContractBudgetID=" + modifiedContractBudgetID + ", subBudgetID=" + subBudgetID
				+ ", modifiedSubBudgetID=" + modifiedSubBudgetID + ", subBudgetName=" + subBudgetName
				+ ", createdDate=" + createdDate + ", createdByUserId=" + createdByUserId + ", modifiedDate="
				+ modifiedDate + ", modifiedByUserId=" + modifiedByUserId + ", counter=" + counter + ", gridType="
				+ gridType + ", fiscalYearCounter=" + fiscalYearCounter + ", noOfyears=" + noOfyears
				+ ", budgetTypeId=" + budgetTypeId + ", invoiceId=" + invoiceId + ", subBudgetAmount="
				+ subBudgetAmount + ", invoiceStatusIdList=" + Arrays.toString(invoiceStatusIdList) + ", entryTypeId="
				+ entryTypeId + ", profServiceTypeId=" + profServiceTypeId + ", subHeader=" + subHeader
				+ ", documentUploadTo=" + documentUploadTo + ", contractStatusId=" + contractStatusId
				+ ", budgetStatusId=" + budgetStatusId + ", budgetStatusName=" + budgetStatusName
				+ ", transactionName=" + transactionName + ", invoiceStatus=" + invoiceStatus
				+ ", personnelServiceTypeId=" + personnelServiceTypeId + ", agencyId=" + agencyId + ", parentBudgetId="
				+ parentBudgetId + ", parentSubBudgetId=" + parentSubBudgetId + ", budgetAmount=" + budgetAmount
				+ ", modifyByProvider=" + modifyByProvider + ", modifyByAgency=" + modifyByAgency + ", type=" + type
				+ ", coaDocType=" + coaDocType + ", statusId=" + statusId + ", amendmentType=" + amendmentType
				+ ", budgetAdvanceId=" + budgetAdvanceId + ", isNewFYScreen=" + isNewFYScreen
				+ ", isProcCerTaskScreen=" + isProcCerTaskScreen +", empPosition=" + empPosition 
				+", unallocatedFund=" + unallocatedFund 
				+", PIEntryTypeId=" + PIEntryTypeId+ "]";
*/
	}
	private String filterToString(){
		StringBuffer sb = new StringBuffer();
		sb.append("CBGridBean [");

		if( procurementID != null && !procurementID.isEmpty() ) {sb.append(", procurementID=" + procurementID ); }
		if( contractID != null && !contractID.isEmpty() ) {sb.append(", contractID=" + contractID ); }
		if( fiscalYearID != null && !fiscalYearID.isEmpty() ) {sb.append(", fiscalYearID=" + fiscalYearID ); }
		if( contractTypeId != null && !contractTypeId.isEmpty() ) {sb.append(", contractTypeId=" + contractTypeId ); }
		if( contractBudgetID != null && !contractBudgetID.isEmpty() ) {sb.append(", contractBudgetID=" + contractBudgetID ) ;}
		if( modifiedContractBudgetID != null && !modifiedContractBudgetID.isEmpty() ) {sb.append(", modifiedContractBudgetID=" + modifiedContractBudgetID ); }
		if( subBudgetID != null && !subBudgetID.isEmpty() ) {sb.append(", subBudgetID=" + subBudgetID ); }
		if( modifiedSubBudgetID != null && !modifiedSubBudgetID.isEmpty() ) {sb.append(", modifiedSubBudgetID=" + modifiedSubBudgetID ); }
		if( subBudgetName != null && !subBudgetName.isEmpty() ) {sb.append(", subBudgetName=" + subBudgetName ) ;}
		if( createdDate != null ) {sb.append(", createdDate=" + createdDate ); }
		if( createdByUserId != null && !createdByUserId.isEmpty() ) {sb.append(", createdByUserId=" + createdByUserId ); }
		if( modifiedByUserId != null && !modifiedByUserId.isEmpty() ) {sb.append(", modifiedByUserId=" + modifiedByUserId ); }
		if( counter != null && !counter.isEmpty() ) {sb.append(", counter=" + counter ); }
		if( gridType != null && !gridType.isEmpty() ) {sb.append(", gridType=" + gridType ); }
		sb.append(", fiscalYearCounter=" + fiscalYearCounter );
		sb.append(", noOfyears=" + noOfyears );
		if( budgetTypeId != null && !budgetTypeId.isEmpty() ) {sb.append(", budgetTypeId=" + budgetTypeId ) ;}
		if( invoiceId != null && !invoiceId.isEmpty() ) {sb.append(", invoiceId=" + invoiceId ); }
		if( subBudgetAmount != null && !subBudgetAmount.isEmpty() ) {sb.append(", subBudgetAmount=" + subBudgetAmount ); }
		if( invoiceStatusIdList != null && invoiceStatusIdList.length > 0 ) {sb.append(", invoiceStatusIdList=" + Arrays.toString(invoiceStatusIdList) ) ;}
		if( entryTypeId != null && !entryTypeId.isEmpty() ) {sb.append(", entryTypeId=" + entryTypeId ); }
		if( profServiceTypeId != null && !profServiceTypeId.isEmpty() ) {sb.append(", profServiceTypeId=" + profServiceTypeId ); }
		if( subHeader != null && !subHeader.isEmpty() ) {sb.append(", subHeader=" + subHeader ); }
		if( documentUploadTo != null && !documentUploadTo.isEmpty() ) {sb.append(", documentUploadTo=" + documentUploadTo ); }
		if( contractStatusId != null && !contractStatusId.isEmpty() ) {sb.append(", contractStatusId=" + contractStatusId ); }
		if( budgetStatusId != null && !budgetStatusId.isEmpty() ) {sb.append(", budgetStatusId=" + budgetStatusId ); }
		if( budgetStatusName != null && !budgetStatusName.isEmpty() ) {sb.append(", budgetStatusName=" + budgetStatusName ); }
		if( transactionName != null && !transactionName.isEmpty() ) {sb.append(", transactionName=" + transactionName ); }
		if( invoiceStatus != null && !invoiceStatus.isEmpty() ) {sb.append(", invoiceStatus=" + invoiceStatus ); }
		if( personnelServiceTypeId != null && !personnelServiceTypeId.isEmpty() ) {sb.append(", personnelServiceTypeId=" + personnelServiceTypeId ); }
		if( agencyId != null && !agencyId.isEmpty() ) {sb.append(", agencyId=" + agencyId ); }
		if( parentBudgetId != null && !parentBudgetId.isEmpty() ) {sb.append(", parentBudgetId=" + parentBudgetId ); }
		if( parentSubBudgetId != null && !parentSubBudgetId.isEmpty() ) {sb.append(", parentSubBudgetId=" + parentSubBudgetId ); }
		if( budgetAmount != null && !budgetAmount.isEmpty() ) {sb.append(", budgetAmount=" + budgetAmount ); }
		if( coaDocType != null ) {sb.append(", coaDocType=" + coaDocType ); }
		if( statusId != null && !statusId.isEmpty() ) {sb.append(", statusId=" + statusId ); }
		if( amendmentType != null && !amendmentType.isEmpty() ) {sb.append(", amendmentType=" + amendmentType ); }
		if( budgetAdvanceId != null && !budgetAdvanceId.isEmpty() ) {sb.append(", budgetAdvanceId=" + budgetAdvanceId ); }
		if( isNewFYScreen != null  ) {sb.append(", isNewFYScreen=" + isNewFYScreen ) ;}
		sb.append(", isProcCerTaskScreen=" + isProcCerTaskScreen ); 
		if( empPosition != null && !empPosition.isEmpty() ) {sb.append(", empPosition=" + empPosition ); }
		if( unallocatedFund != null && !unallocatedFund.isEmpty() ) {sb.append(", unallocatedFund=" + unallocatedFund ); }
		if( PIEntryTypeId != null && !PIEntryTypeId.isEmpty() ) {sb.append(", PIEntryTypeId=" + PIEntryTypeId ); }
		sb.append("]");

		return sb.toString();
	}
	//[End]R7.12.0 QC9311 Minimize Debug
	
	public void setAmendedContractSubBudgetID(List<String> amendedContractSubBudgetID)
	{
		this.amendedContractSubBudgetID = amendedContractSubBudgetID;
	}

	public List<String> getAmendedContractSubBudgetID()
	{
		return amendedContractSubBudgetID;
	}

	public void setAmendedContractSubBudgetIdComma(String amendedContractSubBudgetIdComma)
	{
		this.amendedContractSubBudgetIdComma = amendedContractSubBudgetIdComma;
	}

	public String getAmendedContractSubBudgetIdComma()
	{
		return amendedContractSubBudgetIdComma;
	}

	public String getBudgetStartYear()
	{
		return budgetStartYear;
	}

	public void setBudgetStartYear(String budgetStartYear)
	{
		this.budgetStartYear = budgetStartYear;
	}

	public void setTableId(String tableId)
	{
		this.tableId = tableId;
	}

	public String getTableId()
	{
		return tableId;
	}

	public void setInvoiceAmountCurrent(String invoiceAmountCurrent)
	{
		this.invoiceAmountCurrent = invoiceAmountCurrent;
	}

	public String getInvoiceAmountCurrent()
	{
		return invoiceAmountCurrent;
	}

	public void setLineItemId(String lineItemId)
	{
		this.lineItemId = lineItemId;
	}

	public String getLineItemId()
	{
		return lineItemId;
	}

	public void setInvoiceUnitsCurrent(String invoiceUnitsCurrent)
	{
		this.invoiceUnitsCurrent = invoiceUnitsCurrent;
	}

	public String getInvoiceUnitsCurrent()
	{
		return invoiceUnitsCurrent;
	}

	public void setPartialMergedRowId(List<String> partialMergedRowId)
	{
		this.partialMergedRowId = partialMergedRowId;
	}

	public List<String> getPartialMergedRowId()
	{
		return partialMergedRowId;
	}

}
