/**
 * 
 */
package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

/**
 * This is a bean class which set and get data for selection details summary
 */
public class SelectionDetailsSummaryBean extends BaseFilter
{
	@RegExp(value ="^\\d{0,22}")
	private String procurementId;
	@RegExp(value ="^\\d{0,22}")
	private String competitionPoolId;
	private String competitionPoolTitle;
	private String contractNumber;
	private String contractStatus;
	@RegExp(value ="^\\d{0,22}")
	private String contractId;
	private String amount;
	private String evaluationPoolMapingId;
	private String awardEpin;
	private String organizationId;
	private int contractTypeId;
	private int contractSourceId;
	// Start Added in R5
	private String status;

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

	// End Added in R5
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
		this.competitionPoolTitle = competitionPoolTitle;
	}

	/**
	 * @return the contractNumber
	 */
	public String getContractNumber()
	{
		return contractNumber;
	}

	/**
	 * @param contractNumber the contractNumber to set
	 */
	public void setContractNumber(String contractNumber)
	{
		this.contractNumber = contractNumber;
	}

	/**
	 * @return the contractStatus
	 */
	public String getContractStatus()
	{
		return contractStatus;
	}

	/**
	 * @param contractStatus the contractStatus to set
	 */
	public void setContractStatus(String contractStatus)
	{
		this.contractStatus = contractStatus;
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
	 * @return the amount
	 */
	public String getAmount()
	{
		return amount;
	}

	/**
	 * @param amount the amount to set
	 */
	public void setAmount(String amount)
	{
		this.amount = amount;
	}

	/**
	 * @return the evaluationPoolMapingId
	 */
	public String getEvaluationPoolMapingId()
	{
		return evaluationPoolMapingId;
	}

	/**
	 * @param evaluationPoolMapingId the evaluationPoolMapingId to set
	 */
	public void setEvaluationPoolMapingId(String evaluationPoolMapingId)
	{
		this.evaluationPoolMapingId = evaluationPoolMapingId;
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
		this.awardEpin = awardEpin;
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
	 * @return the contractTypeId
	 */
	public int getContractTypeId()
	{
		return contractTypeId;
	}

	/**
	 * @param contractTypeId the contractTypeId to set
	 */
	public void setContractTypeId(int contractTypeId)
	{
		this.contractTypeId = contractTypeId;
	}

	/**
	 * @return the contractSourceId
	 */
	public int getContractSourceId()
	{
		return contractSourceId;
	}

	/**
	 * @param contractSourceId the contractSourceId to set
	 */
	public void setContractSourceId(int contractSourceId)
	{
		this.contractSourceId = contractSourceId;
	}

	public SelectionDetailsSummaryBean()
	{

		setFirstSort(HHSConstants.CONTRACT_STATUS_COLUMN);
		setSecondSort(HHSConstants.COMP_POOL_TITLE);
		setFirstSortType(HHSConstants.ASCENDING);
		setSecondSortType(HHSConstants.ASCENDING);
		setSortColumnName(HHSConstants.CONTRACT_STATUS);
	}

}
