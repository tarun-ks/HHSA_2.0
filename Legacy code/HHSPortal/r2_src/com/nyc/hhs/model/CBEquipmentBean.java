package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

/**
 * This class is a bean which maintains screen S316 Equipment details.
 * 
 * <ul>
 * <li>This bean is used for grid functionality and as buffer for to/for of
 * values in grid
 * </ul>
 * 
 */
public class CBEquipmentBean extends CBGridBean
{

	//@RegExp(value ="^\\d{0,22}")
	//EQUIPMENT_ID
	private String id; // this is index key/primary key of table referenced
	private String invoiceDetailId;
	private String invoicedAmt = HHSConstants.STRING_ZERO;
	private String equipment;
	private String units;
	private String fyBudget = HHSConstants.STRING_ZERO;
	private String ytdInvoicedAmt = HHSConstants.STRING_ZERO;
	private String modificationAmt = HHSConstants.STRING_ZERO;
	private String updateAmt = HHSConstants.STRING_ZERO;
	private String amendAmt = HHSConstants.STRING_ZERO;
	private String remainingAmt = HHSConstants.STRING_ZERO;
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
	 * @return the equipment
	 */
	public String getEquipment()
	{
		return equipment;
	}

	/**
	 * @param equipment the equipment to set
	 */
	public void setEquipment(String equipment)
	{
		this.equipment = equipment;
	}

	/**
	 * @return the units
	 */
	public String getUnits()
	{
		return units;
	}

	/**
	 * @param units the units to set
	 */
	public void setUnits(String units)
	{
		this.units = units;
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
	 * @return the modificationAmt
	 */
	public String getModificationAmt()
	{
		return modificationAmt;
	}

	/**
	 * @param modificationAmt the modificationAmt to set
	 */
	public void setModificationAmt(String modificationAmt)
	{
		this.modificationAmt = modificationAmt;
	}

	/**
	 * @return the updateAmt
	 */
	public String getUpdateAmt()
	{
		return updateAmt;
	}

	/**
	 * @param updateAmt the updateAmt to set
	 */
	public void setUpdateAmt(String updateAmt)
	{
		this.updateAmt = updateAmt;
	}

	/**
	 * @return the amendAmt
	 */
	public String getAmendAmt()
	{
		return amendAmt;
	}

	/**
	 * @param amendAmt the amendAmt to set
	 */
	public void setAmendAmt(String amendAmt)
	{
		this.amendAmt = amendAmt;
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
	 * @return the invoicedAmt
	 */
	public String getInvoicedAmt()
	{
		return invoicedAmt;
	}

	/**
	 * @param invoicedAmt the invoicedAmt to set
	 */
	public void setInvoicedAmt(String invoicedAmt)
	{
		this.invoicedAmt = invoicedAmt;
	}

	public String getProposedBudget()
	{
		return proposedBudget;
	}

	public void setProposedBudget(String proposedBudget)
	{
		this.proposedBudget = proposedBudget;
	}

	@Override
	public String toString()
	{
		return "CBEquipmentBean [id=" + id + ", invoiceDetailId=" + invoiceDetailId + ", invoicedAmt=" + invoicedAmt
				+ ", equipment=" + equipment + ", units=" + units + ", fyBudget=" + fyBudget + ", ytdInvoicedAmt="
				+ ytdInvoicedAmt + ", modificationAmt=" + modificationAmt + ", updateAmt=" + updateAmt + ", amendAmt="
				+ amendAmt + ", remainingAmt=" + remainingAmt + ", proposedBudget=" + proposedBudget + "]";
	}

}
