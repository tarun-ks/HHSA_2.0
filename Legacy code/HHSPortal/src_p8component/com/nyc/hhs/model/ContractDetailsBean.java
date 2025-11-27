package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/**
 * 
 * ContractDetailsBean class
 * 
 */
public class ContractDetailsBean
{

	// Initialize the required variables
	@RegExp(value ="^\\d{0,22}")
	private String contractId = "";
	private String contractTitle = "";
	private String contractNumber = "";
	private String contractConfigurationId = "";
	@Length(max = 20)
	//ORGANIZATION_ID
	private String providerId = "";
	private String providerName = "";

	/*
	 * For each variable a getter and a setter method is created.
	 */
	public String getProviderId()
	{
		return providerId;
	}

	public void setProviderId(String providerId)
	{
		this.providerId = providerId;
	}

	public String getContractId()
	{
		return contractId;
	}

	public void setContractId(String contractId)
	{
		this.contractId = contractId;
	}

	public String getContractTitle()
	{
		return contractTitle;
	}

	public void setContractTitle(String contractTitle)
	{
		this.contractTitle = contractTitle;
	}

	public String getContractNumber()
	{
		return contractNumber;
	}

	public void setContractNumber(String contractNumber)
	{
		this.contractNumber = contractNumber;
	}

	public String getContractConfigurationId()
	{
		return contractConfigurationId;
	}

	public void setContractConfigurationId(String contractConfigurationId)
	{
		this.contractConfigurationId = contractConfigurationId;
	}

	public String getProviderName()
	{
		return providerName;
	}

	public void setProviderName(String providerName)
	{
		this.providerName = providerName;
	}
}
