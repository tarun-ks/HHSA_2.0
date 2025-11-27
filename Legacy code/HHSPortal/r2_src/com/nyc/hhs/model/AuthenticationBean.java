package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.NotBlank;

import com.nyc.hhs.constants.HHSConstants;

/**
 * This class is used to store Authentication related data
 */
/**
 */
public class AuthenticationBean
{
	@NotBlank(message = HHSConstants.MODEL_ATTRIB_MEG_REQUIRED_FIELD)
	private String userName;
	@NotBlank(message = HHSConstants.MODEL_ATTRIB_MEG_REQUIRED_FIELD)
	private String password;

	private String procurementId;

	private Boolean authStatusFlag;

	private String contractId;
	@NotBlank(message = HHSConstants.MODEL_ATTRIB_MEG_REQUIRED_FIELD)
	private String reason;
	private String invoiceNumber;
	private String invoiceId;

	public Boolean getAuthStatusFlag()
	{
		return authStatusFlag;
	}

	public void setAuthStatusFlag(Boolean authStatusFlag)
	{
		this.authStatusFlag = authStatusFlag;
	}

	public String getUserName()
	{
		return userName;
	}

	public void setUserName(String userName)
	{
		this.userName = userName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getProcurementId()
	{
		return procurementId;
	}

	public void setProcurementId(String procurementId)
	{
		this.procurementId = procurementId;
	}

	public void setContractId(String contractId)
	{
		this.contractId = contractId;
	}

	public String getContractId()
	{
		return contractId;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	public String getReason()
	{
		return reason;
	}

	public void setInvoiceNumber(String invoiceNumber)
	{
		this.invoiceNumber = invoiceNumber;
	}

	public String getInvoiceNumber()
	{
		return invoiceNumber;
	}

	/**
	 * @param invoiceId the invoiceId to set
	 */
	public void setInvoiceId(String invoiceId)
	{
		this.invoiceId = invoiceId;
	}

	/**
	 * @return the invoiceId
	 */
	public String getInvoiceId()
	{
		return invoiceId;
	}

	@Override
	public String toString()
	{
		return "AuthenticationBean [userName=" + userName + ", password=" + "**********" + ", procurementId="
				+ procurementId + ", authStatusFlag=" + authStatusFlag + ", contractId=" + contractId + ", reason="
				+ reason + ", invoiceNumber=" + invoiceNumber + ", invoiceId=" + invoiceId + "]";
	}
}
