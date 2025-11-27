/**
 * 
 */
package com.nyc.hhs.model;

//import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

/**
 * This class is a bean which maintains screen S316 OTPS - Professional Service
 * details.
 * 
 * <ul>
 * <li>This bean is used for grid functionality and as buffer for to/for of
 * values in grid
 * </ul>
 * 
 */
public class CBProfessionalServicesBean extends CBGridBean
{

	//@RegExp(value ="^\\d{0,22}")
	//PROFESSIONAL_SERVICE_ID
	private String id;
	private String cbmId;
	//@RegExp(value ="^\\d{0,22}")
	private String profServiceTypeId;
	private String profServiceNewTypeId;
	private String professionalServiceName;
	private String fyBudget = HHSConstants.EMPTY_STRING;
	private String ytdInvoicedAmt = HHSConstants.STRING_ZERO;
	private String remainingAmt = HHSConstants.STRING_ZERO;
	private String invoiceAmount = HHSConstants.STRING_ZERO;
	private String invoiceDetailId;

	private String modifyAmount = HHSConstants.EMPTY_STRING;
	private String proposedBudget = HHSConstants.STRING_ZERO;

	/**
	 * @return the id
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id)
	{
		this.id = id;
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
	 * @return the profServiceNewTypeId
	 */
	public String getProfServiceNewTypeId()
	{
		return profServiceNewTypeId;
	}

	/**
	 * @param profServiceNewTypeId the profServiceNewTypeId to set
	 */
	public void setProfServiceNewTypeId(String profServiceNewTypeId)
	{
		this.profServiceNewTypeId = profServiceNewTypeId;
	}

	/**
	 * @return the professionalServiceName
	 */
	public String getProfessionalServiceName()
	{
		return professionalServiceName;
	}

	/**
	 * @param professionalServiceName the professionalServiceName to set
	 */
	public void setProfessionalServiceName(String professionalServiceName)
	{
		this.professionalServiceName = professionalServiceName;
	}

	/**
	 * @return the fyBudget
	 */
	public String getFyBudget()
	{
		return fyBudget;
	}

	/**
	 * @param fyBudget the fyBudget to set
	 */
	public void setFyBudget(String fyBudget)
	{
		this.fyBudget = fyBudget;
	}

	/**
	 * @return the ytdInvoicedAmt
	 */
	public String getYtdInvoicedAmt()
	{
		return ytdInvoicedAmt;
	}

	/**
	 * @param ytdInvoicedAmt the ytdInvoicedAmt to set
	 */
	public void setYtdInvoicedAmt(String ytdInvoicedAmt)
	{
		this.ytdInvoicedAmt = ytdInvoicedAmt;
	}

	/**
	 * @return the remainingAmt
	 */
	public String getRemainingAmt()
	{
		return remainingAmt;
	}

	/**
	 * @param remainingAmt the remainingAmt to set
	 */
	public void setRemainingAmt(String remainingAmt)
	{
		this.remainingAmt = remainingAmt;
	}

	/**
	 * @return the invoiceAmount
	 */
	public String getInvoiceAmount()
	{
		return invoiceAmount;
	}

	/**
	 * @param invoiceAmount the invoiceAmount to set
	 */
	public void setInvoiceAmount(String invoiceAmount)
	{
		this.invoiceAmount = invoiceAmount;
	}

	/**
	 * @return the invoiceDetailId
	 */
	public String getInvoiceDetailId()
	{
		return invoiceDetailId;
	}

	/**
	 * @param invoiceDetailId the invoiceDetailId to set
	 */
	public void setInvoiceDetailId(String invoiceDetailId)
	{
		this.invoiceDetailId = invoiceDetailId;
	}

	/**
	 * @return the cbmId
	 */
	public String getCbmId()
	{
		return cbmId;
	}

	/**
	 * @param cbmId the cbmId to set
	 */
	public void setCbmId(String cbmId)
	{
		this.cbmId = cbmId;
	}

	/**
	 * @return the modifYAmount
	 */
	public String getModifyAmount()
	{
		return modifyAmount;
	}

	/**
	 * @param modifYAmount the modifYAmount to set
	 */
	public void setModifyAmount(String modifyAmount)
	{
		this.modifyAmount = modifyAmount;
	}

	/**
	 * @return the proposedBudget
	 */
	public String getProposedBudget()
	{
		return proposedBudget;
	}

	/**
	 * @param proposedBudget the proposedBudget to set
	 */
	public void setProposedBudget(String proposedBudget)
	{
		this.proposedBudget = proposedBudget;
	}

	@Override
	public String toString()
	{
		return "CBProfessionalServicesBean [id=" + id + ", cbmId=" + cbmId + ", profServiceTypeId=" + profServiceTypeId
				+ ", profServiceNewTypeId=" + profServiceNewTypeId + ", professionalServiceName="
				+ professionalServiceName + ", fyBudget=" + fyBudget + ", ytdInvoicedAmt=" + ytdInvoicedAmt
				+ ", remainingAmt=" + remainingAmt + ", invoiceAmount=" + invoiceAmount + ", invoiceDetailId="
				+ invoiceDetailId + ", modifyAmount=" + modifyAmount + ", proposedBudget=" + proposedBudget + "]";
	}

}
