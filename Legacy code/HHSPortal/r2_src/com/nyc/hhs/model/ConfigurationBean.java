package com.nyc.hhs.model;

/**
 * This class is a bean which maintains all configuration module details.
 * 
 */
public class ConfigurationBean
{
	private int procurementId;
	private int procurementCOFId;
	private String procurementTitle;
	private String contractId;
	private String agencyId;
	private String agencyName;
	private String procurementValue;
	private String contractStartDate;
	private String contractEndDate;
	private String contractValue;
	private String procurementStatus;
	private int totConfiguredBudget;

	/**
	 * @return the procurementId
	 */
	public int getProcurementId()
	{
		return procurementId;
	}

	/**
	 * @param procurementId the procurementId to set
	 */
	public void setProcurementId(int procurementId)
	{
		this.procurementId = procurementId;
	}

	/**
	 * @return the procurementCOFId
	 */
	public int getProcurementCOFId()
	{
		return procurementCOFId;
	}

	/**
	 * @param procurementCOFId the procurementCOFId to set
	 */
	public void setProcurementCOFId(int procurementCOFId)
	{
		this.procurementCOFId = procurementCOFId;
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
	 * @return the agencyName
	 */
	public String getAgencyName()
	{
		return agencyName;
	}

	/**
	 * @param agencyName the agencyName to set
	 */
	public void setAgencyName(String agencyName)
	{
		this.agencyName = agencyName;
	}

	/**
	 * @return the procurementValue
	 */
	public String getProcurementValue()
	{
		return procurementValue;
	}

	/**
	 * @param procurementValue the procurementValue to set
	 */
	public void setProcurementValue(String procurementValue)
	{
		this.procurementValue = procurementValue;
	}

	/**
	 * @return the contractStartDate
	 */
	public String getContractStartDate()
	{
		return contractStartDate;
	}

	/**
	 * @param contractStartDate the contractStartDate to set
	 */
	public void setContractStartDate(String contractStartDate)
	{
		this.contractStartDate = contractStartDate;
	}

	/**
	 * @return the contractEndDate
	 */
	public String getContractEndDate()
	{
		return contractEndDate;
	}

	/**
	 * @param contractEndDate the contractEndDate to set
	 */
	public void setContractEndDate(String contractEndDate)
	{
		this.contractEndDate = contractEndDate;
	}

	/**
	 * @return the contractValue
	 */
	public String getContractValue()
	{
		return contractValue;
	}

	/**
	 * @param contractValue the contractValue to set
	 */
	public void setContractValue(String contractValue)
	{
		this.contractValue = contractValue;
	}

	/**
	 * @return the procurementStatus
	 */
	public String getProcurementStatus()
	{
		return procurementStatus;
	}

	/**
	 * @param procurementStatus the procurementStatus to set
	 */
	public void setProcurementStatus(String procurementStatus)
	{
		this.procurementStatus = procurementStatus;
	}

	/**
	 * @return the totConfiguredBudget
	 */
	public int getTotConfiguredBudget()
	{
		return totConfiguredBudget;
	}

	/**
	 * @param totConfiguredBudget the totConfiguredBudget to set
	 */
	public void setTotConfiguredBudget(int totConfiguredBudget)
	{
		this.totConfiguredBudget = totConfiguredBudget;
	}

	@Override
	public String toString()
	{
		return "ConfigurationBean [procurementId=" + procurementId + ", procurementCOFId=" + procurementCOFId
				+ ", procurementTitle=" + procurementTitle + ", contractId=" + contractId + ", agencyId=" + agencyId
				+ ", agencyName=" + agencyName + ", procurementValue=" + procurementValue + ", contractStartDate="
				+ contractStartDate + ", contractEndDate=" + contractEndDate + ", contractValue=" + contractValue
				+ ", procurementStatus=" + procurementStatus + ", totConfiguredBudget=" + totConfiguredBudget + "]";
	}

}