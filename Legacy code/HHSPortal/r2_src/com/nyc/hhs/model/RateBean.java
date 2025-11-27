package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

public class RateBean extends CBGridBean
{

	//@RegExp(value ="^\\d{0,22}")
	//RATE_ID
	private String id;
	@Length(max = 50)
	private String unitDesc;
	//@RegExp(value ="^\\d{0,22}")
	private String units = HHSConstants.STRING_ZERO;
	private String fyBudget = HHSConstants.STRING_ZERO;
	private String ytdInvoiceAmt = HHSConstants.STRING_ZERO;
	private String remainAmt = HHSConstants.STRING_ZERO;
	private String lsModifyAmount = HHSConstants.STRING_ZERO;
	private String lsModifyUnits = HHSConstants.STRING_ZERO;
	private String lsProposedBudget = HHSConstants.STRING_ZERO;
	private String lsParentId;
	private String invUnits = HHSConstants.STRING_ZERO;
	private String ytdUnits = HHSConstants.STRING_ZERO;
	private String remUnits = HHSConstants.STRING_ZERO;

	/**
	 * @return the unitDesc
	 */
	public String getUnitDesc()
	{
		return unitDesc;
	}

	/**
	 * @param unitDesc the unitDesc to set
	 */
	public void setUnitDesc(String unitDesc)
	{
		this.unitDesc = unitDesc;
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
	 * @return the ytdInvoiceAmt
	 */
	public String getYtdInvoiceAmt()
	{
		return ytdInvoiceAmt;
	}

	/**
	 * @param ytdInvoiceAmt the ytdInvoiceAmt to set
	 */
	public void setYtdInvoiceAmt(String ytdInvoiceAmt)
	{
		this.ytdInvoiceAmt = ytdInvoiceAmt;
	}

	/**
	 * @return the remainAmt
	 */
	public String getRemainAmt()
	{
		return remainAmt;
	}

	/**
	 * @param remainAmt the remainAmt to set
	 */
	public void setRemainAmt(String remainAmt)
	{
		this.remainAmt = remainAmt;
	}

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

	public String getLsProposedBudget()
	{
		return lsProposedBudget;
	}

	public void setLsProposedBudget(String lsProposedBudget)
	{
		this.lsProposedBudget = lsProposedBudget;
	}

	public String getLsParentId()
	{
		return lsParentId;
	}

	public void setLsParentId(String lsParentId)
	{
		this.lsParentId = lsParentId;
	}

	public String getLsModifyAmount()
	{
		return lsModifyAmount;
	}

	public void setLsModifyAmount(String lsModifyAmount)
	{
		this.lsModifyAmount = lsModifyAmount;
	}

	public String getLsModifyUnits()
	{
		return lsModifyUnits;
	}

	public void setLsModifyUnits(String lsModifyUnits)
	{
		this.lsModifyUnits = lsModifyUnits;
	}

	public String getInvUnits()
	{
		return invUnits;
	}

	public void setInvUnits(String invUnits)
	{
		this.invUnits = invUnits;
	}

	public String getYtdUnits()
	{
		return ytdUnits;
	}

	public void setYtdUnits(String ytdUnits)
	{
		this.ytdUnits = ytdUnits;
	}

	public String getRemUnits()
	{
		return remUnits;
	}

	public void setRemUnits(String remUnits)
	{
		this.remUnits = remUnits;
	}

	@Override
	public String toString()
	{
		return "RateBean [id=" + id + ", unitDesc=" + unitDesc + ", units=" + units + ", fyBudget=" + fyBudget
				+ ", ytdInvoiceAmt=" + ytdInvoiceAmt + ", remainAmt=" + remainAmt + ", lsModifyAmount="
				+ lsModifyAmount + ", lsModifyUnits=" + lsModifyUnits + ", lsProposedBudget=" + lsProposedBudget
				+ ", lsParentId=" + lsParentId + ", invUnits=" + invUnits + ", ytdUnits=" + ytdUnits + ",remUnits="
				+ remUnits + "]";
	}

}
