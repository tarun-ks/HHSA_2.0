package com.batch.bulkupload;

import com.nyc.hhs.constants.HHSConstants;

public class BulkUploadContractInfo
{

	private String contractType = HHSConstants.EMPTY_STRING; // same as inflight option

	private String epin = HHSConstants.EMPTY_STRING;
	private String agency = HHSConstants.EMPTY_STRING;
	private String accProgramName = HHSConstants.EMPTY_STRING;
	private String accProgramId = HHSConstants.EMPTY_STRING;
	private String contractTitle = HHSConstants.EMPTY_STRING;
	private String contractValue = HHSConstants.EMPTY_STRING;
	private String contractStartDate = HHSConstants.EMPTY_STRING;
	private String contractEndDate = HHSConstants.EMPTY_STRING;
	private String rowNumber = HHSConstants.EMPTY_STRING;
	private String errorMessage = HHSConstants.EMPTY_STRING;
	private String uploadFlag = HHSConstants.EMPTY_STRING;
	private String createdByUserId = HHSConstants.EMPTY_STRING;
	private String modifiedByUserId = HHSConstants.EMPTY_STRING;
	private String fileUploadId = HHSConstants.EMPTY_STRING;
	private String bulkUploadDataId = HHSConstants.EMPTY_STRING;
	//Added in R6: non-apt epins
	private String refAptEpinId = HHSConstants.EMPTY_STRING;;
    
	//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	private String budgetFiscalYearStartYear =HHSConstants.EMPTY_STRING;
	//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	
	public String getRefAptEpinId()
	{
		return refAptEpinId;
	}

	public void setRefAptEpinId(String refAptEpinId)
	{
		this.refAptEpinId = refAptEpinId;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage()
	{
		return errorMessage;
	}

	/**
	 * @return the accProgramId
	 */
	public String getAccProgramId()
	{
		return accProgramId;
	}

	/**
	 * @param accProgramId the accProgramId to set
	 */
	public void setAccProgramId(String accProgramId)
	{
		this.accProgramId = accProgramId;
	}

	/**
	 * @return the createdByUserId
	 */
	public String getCreatedByUserId()
	{
		return createdByUserId;
	}

	/**
	 * @return the modifiedByUserId
	 */
	public String getModifiedByUserId()
	{
		return modifiedByUserId;
	}

	/**
	 * @param createdByUserId the createdByUserId to set
	 */
	public void setCreatedByUserId(String createdByUserId)
	{
		this.createdByUserId = createdByUserId;
	}

	/**
	 * @return the fileUploadId
	 */
	public String getFileUploadId()
	{
		return fileUploadId;
	}

	/**
	 * @param fileUploadId the fileUploadId to set
	 */
	public void setFileUploadId(String fileUploadId)
	{
		this.fileUploadId = fileUploadId;
	}

	/**
	 * @param modifiedByUserId the modifiedByUserId to set
	 */
	public void setModifiedByUserId(String modifiedByUserId)
	{
		this.modifiedByUserId = modifiedByUserId;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage)
	{
		this.errorMessage = errorMessage;
	}


	/**
	 * @param rowNumber the rowNumber to set
	 */
	public void setRowNumber(String rowNumber)
	{
		this.rowNumber = rowNumber;
	}

	//[Start] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
	@Override
	public String toString()
	{
		return "BulkUploadContractInfo [contractType=" + contractType + ", epin=" + epin + ", agency=" + agency
				+ ", accProgramName=" + accProgramName + ", accProgramId=" + accProgramId + ", contractTitle="
				+ contractTitle + ", contractValue=" + contractValue + ", contractStartDate=" + contractStartDate
				+ ", contractEndDate=" + contractEndDate + ", rowNumber=" + rowNumber + ", errorMessage="
				+ errorMessage + ", uploadFlag=" + uploadFlag + ", createdByUserId=" + createdByUserId
				+ ", modifiedByUserId=" + modifiedByUserId + ", fileUploadId=" + fileUploadId + ", bulkUploadDataId="
				+ bulkUploadDataId + ", refAptEpinId=" + refAptEpinId + ",budgetFiscalYearStartYear=" + budgetFiscalYearStartYear + "]";
	}
	public String getBudgetFiscalYearStartYear() {
		return budgetFiscalYearStartYear;
	}

	public void setBudgetFiscalYearStartYear(String budgetFiscalYearStartYear) {
		this.budgetFiscalYearStartYear = budgetFiscalYearStartYear;
	}
	//[End] R8.4.1 QC_9506 Bulk Upload Template rejecting EPINS
		

	/**
	 * @param string the uploadFlag to set
	 */
	public void setUploadFlag(String string)
	{
		this.uploadFlag = string;
	}

	public String getContractType()
	{
		return contractType;
	}

	public void setContractType(String lscontractType)
	{
		contractType = lscontractType;
	}

	public String getEpin()
	{
		return epin;
	}

	public void setEpin(String epin)
	{
		this.epin = epin;
	}

	public String getAgency()
	{
		return agency;
	}

	public void setAgency(String agency)
	{
		this.agency = agency;
	}

	public String getAccProgramName()
	{
		return accProgramName;
	}

	public void setAccProgramName(String accProgramName)
	{
		this.accProgramName = accProgramName;
	}

	public String getContractTitle()
	{
		return contractTitle;
	}

	public void setContractTitle(String contractTitle)
	{
		this.contractTitle = contractTitle;
	}

	public String getContractValue()
	{
		return contractValue;
	}

	public void setContractValue(String contractValue)
	{
		this.contractValue = contractValue;
	}

	public String getContractStartDate()
	{
		return contractStartDate;
	}

	public void setContractStartDate(String contractStartDate)
	{
		this.contractStartDate = contractStartDate;
	}

	public String getContractEndDate()
	{
		return contractEndDate;
	}

	public void setContractEndDate(String contractEndDate)
	{
		this.contractEndDate = contractEndDate;
	}

	/**
	 * @return the bulkUploadDataId
	 */
	public String getBulkUploadDataId()
	{
		return bulkUploadDataId;
	}

	/**
	 * @param bulkUploadDataId the bulkUploadDataId to set
	 */
	public void setBulkUploadDataId(String bulkUploadDataId)
	{
		this.bulkUploadDataId = bulkUploadDataId;
	}

	

}
