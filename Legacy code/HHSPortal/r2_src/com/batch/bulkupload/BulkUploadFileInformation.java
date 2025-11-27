package com.batch.bulkupload;

import com.nyc.hhs.constants.HHSConstants;

public class BulkUploadFileInformation
{

	private String fileId = HHSConstants.EMPTY_STRING;
	private String documentId = HHSConstants.EMPTY_STRING;
	private String fileName = HHSConstants.EMPTY_STRING;
	private String fileStatus = HHSConstants.EMPTY_STRING;
	private String createdBy = HHSConstants.EMPTY_STRING;
	private String bulkUploadId = HHSConstants.EMPTY_STRING;
	private String lockTime = HHSConstants.EMPTY_STRING;

	/**
	 * @return the lockTime
	 */
	public String getLockTime()
	{
		return lockTime;
	}

	/**
	 * @param lockTime the lockTime to set
	 */
	public void setLockTime(String lockTime)
	{
		this.lockTime = lockTime;
	}

	/**
	 * @return the bulkUploadId
	 */
	public String getBulkUploadId()
	{
		return bulkUploadId;
	}

	/**
	 * @param bulkUploadId the bulkUploadId to set
	 */
	public void setBulkUploadId(String bulkUploadId)
	{
		this.bulkUploadId = bulkUploadId;
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

	public String getFileId()
	{
		return fileId;
	}

	public void setFileId(String fileId)
	{
		this.fileId = fileId;
	}

	public String getDocumentId()
	{
		return documentId;
	}

	public void setDocumentId(String documentId)
	{
		this.documentId = documentId;
	}

	public String getFileName()
	{
		return fileName;
	}

	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}

	public String getFileStatus()
	{
		return fileStatus;
	}

	public void setFileStatus(String fileStatus)
	{
		this.fileStatus = fileStatus;
	}

}
