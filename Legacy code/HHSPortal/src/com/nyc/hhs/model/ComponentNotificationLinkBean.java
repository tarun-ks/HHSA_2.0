package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class ComponentNotificationLinkBean
{
	private String fiscalYearId;
	@RegExp(value ="^\\d{0,22}")
	private String invoiceID;
	@RegExp(value ="^\\d{0,22}")
	private String procurementId;
	@RegExp(value ="^\\d{0,22}")
	private String budgetId;
	private String contractId;
	@RegExp(value ="^\\d{0,22}")
	private String parentBudgetId ;
	private String launchByOrgType;
	private String entityId;
	private String entityType;
	
	
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
	 * @return the fiscalYearId
	 */
	public String getFiscalYearId()
	{
		return fiscalYearId;
	}
	/**
	 * @param fiscalYearId the fiscalYearId to set
	 */
	public void setFiscalYearId(String fiscalYearId)
	{
		this.fiscalYearId = fiscalYearId;
	}
	/**
	 * @return the invoiceID
	 */
	public String getInvoiceID()
	{
		return invoiceID;
	}
	/**
	 * @param invoiceID the invoiceID to set
	 */
	public void setInvoiceID(String invoiceID)
	{
		this.invoiceID = invoiceID;
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
	 * @return the launchByOrgType
	 */
	public String getLaunchByOrgType()
	{
		return launchByOrgType;
	}
	/**
	 * @param launchByOrgType the launchByOrgType to set
	 */
	public void setLaunchByOrgType(String launchByOrgType)
	{
		this.launchByOrgType = launchByOrgType;
	}
	
	
	
}