package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * 
 * This is a bean class to get and set different values related to the
 * notification which will be used to insert the notification details into the
 * notification tables
 * 
 */
public class SMAlertNotificationBean
{
	private String procurementTitle = null;
	private int noOfDays = 0;
	@RegExp(value ="^\\d{0,22}")
	private String procurementId = null;
	@Length(max = 20)
	private String procurementAgencyId = null;
	private String evaluationGroupId = null;
	private String competitionPoolId = null;
	private String evaluationPoolMappingId = null;	

	/**
	 * @return the evaluationGroupId
	 */
	public String getEvaluationGroupId()
	{
		return evaluationGroupId;
	}

	/**
	 * @param evaluationGroupId the evaluationGroupId to set
	 */
	public void setEvaluationGroupId(String evaluationGroupId)
	{
		this.evaluationGroupId = evaluationGroupId;
	}

	/**
	 * @return the competitionPoolId
	 */
	public String getCompetitionPoolId()
	{
		return competitionPoolId;
	}

	/**
	 * @param competitionPoolId the competitionPoolId to set
	 */
	public void setCompetitionPoolId(String competitionPoolId)
	{
		this.competitionPoolId = competitionPoolId;
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
	 * @return the noOfDays
	 */
	public int getNoOfDays()
	{
		return noOfDays;
	}

	/**
	 * @param noOfDays the noOfDays to set
	 */
	public void setNoOfDays(int noOfDays)
	{
		this.noOfDays = noOfDays;
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
	 * @return the procurementAgencyId
	 */
	public String getProcurementAgencyId()
	{
		return procurementAgencyId;
	}

	/**
	 * @param procurementAgencyId the procurementAgencyId to set
	 */
	public void setProcurementAgencyId(String procurementAgencyId)
	{
		this.procurementAgencyId = procurementAgencyId;
	}

	@Override
	public String toString() {
		return "SMAlertNotificationBean [procurementTitle=" + procurementTitle
				+ ", noOfDays=" + noOfDays + ", procurementId=" + procurementId
				+ ", procurementAgencyId=" + procurementAgencyId + "]";
	}

}
