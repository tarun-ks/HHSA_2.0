package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

//This is a Bean class added for document visibility functionality
public class DocumentVisibility
{
	@RegExp(value ="^\\d{0,22}")
	private String procurementId;
	@RegExp(value ="^\\d{0,22}")
	private String procurementDocumentId;
	private String visibility;
	@RegExp(value ="^\\d{0,22}")
	private String evaluationPoolMappingId;
	@Length(max = 100)
	private String documentType;
	//@RegExp(value ="^\\d{0,22}")
	private String documentVisibilityId;
	private String userId;
	private String requiredFlag;
	
	
	public DocumentVisibility()
	{
		super();
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
	 * @return the procurementDocumentId
	 */
	public String getProcurementDocumentId()
	{
		return procurementDocumentId;
	}

	/**
	 * @param procurementDocumentId the procurementDocumentId to set
	 */
	public void setProcurementDocumentId(String procurementDocumentId)
	{
		this.procurementDocumentId = procurementDocumentId;
	}

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
	 * @return the documentVisibilityId
	 */
	public String getDocumentVisibilityId()
	{
		return documentVisibilityId;
	}

	/**
	 * @param documentVisibilityId the documentVisibilityId to set
	 */
	public void setDocumentVisibilityId(String documentVisibilityId)
	{
		this.documentVisibilityId = documentVisibilityId;
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

}
