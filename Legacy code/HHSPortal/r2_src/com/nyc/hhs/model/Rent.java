package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

/**
 * This is the bean class for rent screen containing getters and setters for the
 * bean associated with rent, invoice, invoiceDetails
 * @author virender.x.kumar
 * 
 */
public class Rent extends CBGridBean
{
	//@RegExp(value ="^\\d{0,22}")
	//RENT_ID
	private String id = HHSConstants.ONE;
	@Length(max = 100)
	private String location = HHSConstants.EMPTY_STRING;
	@Length(max = 50)
	private String managementCompanyName = HHSConstants.EMPTY_STRING;
	@Length(max = 50)
	private String propertyOwner = HHSConstants.EMPTY_STRING;
	private String publicSchoolSpace = HHSConstants.EMPTY_STRING;
	private String percentChargedToContract = HHSConstants.EMPTY_STRING;
	private String fyBudget = HHSConstants.STRING_ZERO;
	private String ytdInvoiceAmt = HHSConstants.STRING_ZERO;
	private String remainingAmt = HHSConstants.STRING_ZERO;
	private String lineItemInvoiceAmt = HHSConstants.STRING_ZERO;
	private String createdUserId;
	@RegExp(value ="^\\d{0,22}")
	private int subBudgetId = HHSConstants.INT_ONE;
	@RegExp(value ="^\\d{0,22}")
	private int budgetId = HHSConstants.INT_ONE;
	private String parentId;
	private String proposedBudget = HHSConstants.STRING_ZERO;
	private String modifyAmount;
	private String amendmentAmount = HHSConstants.STRING_ZERO;

	/**
	 * getter for getBudgetId()
	 * @return budgetId
	 */
	public int getBudgetId()
	{
		return budgetId;
	}

	/**
	 * setter for setBudgetId
	 * @param budgetId
	 */
	public void setBudgetId(int budgetId)
	{
		this.budgetId = budgetId;
	}

	/**
	 * 
	 * getter for getSubBudgetId
	 * @return subBudgetId
	 */

	public int getSubBudgetId()
	{
		return subBudgetId;
	}

	/**
	 * setter for setSubBudgetId
	 * @param subBudgetId
	 */
	public void setSubBudgetId(int subBudgetId)
	{
		this.subBudgetId = subBudgetId;
	}

	/**
	 * getter for getCreatedUserId
	 * @return createdUserId
	 */
	public String getCreatedUserId()
	{
		return createdUserId;
	}

	/**
	 * setter for setCreatedUserId
	 * @param createdUserId
	 */
	public void setCreatedUserId(String createdUserId)
	{
		this.createdUserId = createdUserId;
	}

	/**
	 * getter for getLocation
	 * @return location
	 */
	public String getLocation()
	{
		return location;
	}

	/**
	 * getter for getFyBudget
	 * @return fyBudget
	 */
	public String getFyBudget()
	{
		return fyBudget;
	}

	/**
	 * setter for setFyBudget
	 * @param fyBudget
	 * 
	 */
	public void setFyBudget(String fyBudget)
	{
		this.fyBudget = fyBudget;
	}

	/**
	 * getter for getYtdInvoiceAmt
	 * @return ytdInvoiceAmt
	 */
	public String getYtdInvoiceAmt()
	{
		return ytdInvoiceAmt;
	}

	/**
	 * setter for setYtdInvoiceAmt
	 * @param ytdInvoiceAmt
	 */
	public void setYtdInvoiceAmt(String ytdInvoiceAmt)
	{
		this.ytdInvoiceAmt = ytdInvoiceAmt;
	}

	/**
	 * getter for getRemainingAmt
	 * @return remainingAmt
	 */
	public String getRemainingAmt()
	{
		return remainingAmt;
	}

	/**
	 * setter for setRemainingAmt
	 * @param remainingAmt
	 */
	public void setRemainingAmt(String remainingAmt)
	{
		this.remainingAmt = remainingAmt;
	}

	/**
	 * setter for setLocation
	 * @param location
	 */

	public void setLocation(String location)
	{
		this.location = location;
	}

	/**
	 * getter for getManagementCompanyName
	 * @return managementCompanyName
	 */
	public String getManagementCompanyName()
	{
		return managementCompanyName;
	}

	/**
	 * setter for setManagementCompanyName
	 * @param managementCompanyName
	 */
	public void setManagementCompanyName(String managementCompanyName)
	{
		this.managementCompanyName = managementCompanyName;
	}

	/**
	 * getter for getPropertyOwner
	 * @return propertyOwner
	 */
	public String getPropertyOwner()
	{
		return propertyOwner;
	}

	/**
	 * setter for setPropertyOwner
	 * @param propertyOwner
	 */
	public void setPropertyOwner(String propertyOwner)
	{
		this.propertyOwner = propertyOwner;
	}

	/**
	 * getter for getPublicSchoolSpace
	 * @return publicSchoolSpace
	 */
	public String getPublicSchoolSpace()
	{
		return publicSchoolSpace;
	}

	/**
	 * setter for setPublicSchoolSpace
	 * @param publicSchoolSpace
	 */
	public void setPublicSchoolSpace(String publicSchoolSpace)
	{
		this.publicSchoolSpace = publicSchoolSpace;
	}

	/**
	 * getter for getPercentChargedToContract
	 * @return percentChargedToContract
	 */
	public String getPercentChargedToContract()
	{
		return percentChargedToContract;
	}

	/**
	 * setter for setPercentChargedToContract
	 * @param percentChargedToContract
	 */
	public void setPercentChargedToContract(String percentChargedToContract)
	{
		this.percentChargedToContract = percentChargedToContract;
	}

	/**
	 * setter for setId
	 * @param id
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 * getter for getId
	 * @return
	 */
	public String getId()
	{
		return id;
	}

	/**
	 * setter for setLineItemInvoiceAmt
	 * @param lineItemInvoiceAmt
	 */
	public void setLineItemInvoiceAmt(String lineItemInvoiceAmt)
	{
		this.lineItemInvoiceAmt = lineItemInvoiceAmt;
	}

	/**
	 * getter for getLineItemInvoiceAmt
	 * @return
	 */
	public String getLineItemInvoiceAmt()
	{
		return lineItemInvoiceAmt;
	}

	public void setModifyAmount(String modifyAmount)
	{
		this.modifyAmount = modifyAmount;
	}

	public String getModifyAmount()
	{
		return modifyAmount;
	}

	public void setParentId(String parentId)
	{
		this.parentId = parentId;
	}

	public String getParentId()
	{
		return parentId;
	}

	public void setProposedBudget(String proposedBudget)
	{
		this.proposedBudget = proposedBudget;
	}

	public String getProposedBudget()
	{
		return proposedBudget;
	}

	/**
	 * @param amendmentAmount the amendmentAmount to set
	 */
	public void setAmendmentAmount(String amendmentAmount)
	{
		this.amendmentAmount = amendmentAmount;
	}

	/**
	 * @return the amendmentAmount
	 */
	public String getAmendmentAmount()
	{
		return amendmentAmount;
	}

	@Override
	public String toString()
	{
		return "Rent [id=" + id + ", location=" + location + ", managementCompanyName=" + managementCompanyName
				+ ", propertyOwner=" + propertyOwner + ", publicSchoolSpace=" + publicSchoolSpace
				+ ", percentChargedToContract=" + percentChargedToContract + ", fyBudget=" + fyBudget
				+ ", ytdInvoiceAmt=" + ytdInvoiceAmt + ", remainingAmt=" + remainingAmt + ", lineItemInvoiceAmt="
				+ lineItemInvoiceAmt + ", createdUserId=" + createdUserId + ", subBudgetId=" + subBudgetId
				+ ", budgetId=" + budgetId + ", parentId=" + parentId + ", proposedBudget=" + proposedBudget
				+ ", modifyAmount=" + modifyAmount + ", amendmentAmount=" + amendmentAmount + "]";
	}
}
