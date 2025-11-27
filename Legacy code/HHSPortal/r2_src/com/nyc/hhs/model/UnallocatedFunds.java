package com.nyc.hhs.model;

import java.util.Date;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSConstants;

public class UnallocatedFunds extends CBGridBean
{
	//@RegExp(value ="^\\d{0,22}")
	private String id;
	private int unallocatedId;
	// QC 8394 R 7.8.0 Add Unallocated Fund line
	@Length(max = 50)
	private String unallocatedFund;
	@RegExp(value ="^\\d{0,22}")
	private int budgetId = HHSConstants.INT_ZERO;
	@RegExp(value ="^\\d{0,22}")
	private int subBudgetId = HHSConstants.INT_ZERO;
	//private String unallocatedType = HHSConstants.UNALLOCATED_FUNDS;
	private String ammount = HHSConstants.STRING_ZERO;
	private int approvedBudget;
	private Date createdDate;
	private String createdUserId;
	private Date modifiedDate;
	private String modifiedUserId;
	private String invoiceAmount = HHSConstants.EMPTY_STRING;
	private String modificationAmount = HHSConstants.STRING_ZERO;
	private String proposedBudget = HHSConstants.STRING_ZERO;
	private String ammountMod = HHSConstants.STRING_ZERO;
	private int modCount = HHSConstants.INT_ZERO;
	private int orgCount = HHSConstants.INT_ZERO;
	private String modifyByProvider = HHSConstants.EMPTY_STRING;
	private String modifyByAgency = HHSConstants.EMPTY_STRING;
	private String parentBudgetId;
	private String parentSubBudgetId;
	private String parentId;
	private String childId;
	
	public String getChildId() {
		return childId;
	}

	public void setChildId(String childId) {
		this.childId = childId;
	}

	private String type;
	
	
	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

		
	public String getParentBudgetId() {
		return parentBudgetId;
	}

	public void setParentBudgetId(String parentBudgetId) {
		this.parentBudgetId = parentBudgetId;
	}

	public String getParentSubBudgetId() {
		return parentSubBudgetId;
	}

	public void setParentSubBudgetId(String parentSubBudgetId) {
		this.parentSubBudgetId = parentSubBudgetId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	

	/**
	 * @return the approvedBudget
	 */
	public int getApprovedBudget()
	{
		return approvedBudget;
	}

	/**
	 * @param approvedBudget the approvedBudget to set
	 */
	public void setApprovedBudget(int approvedBudget)
	{
		this.approvedBudget = approvedBudget;
	}

	/**
	 * @return the unallocatedId
	 */
	public int getUnallocatedId()
	{
		return unallocatedId;
	}

	/**
	 * @param unallocatedId the unallocatedId to set
	 */
	public void setUnallocatedId(int unallocatedId)
	{
		this.unallocatedId = unallocatedId;
	}

	/**
	 * @return the budgetId
	 */
	public int getBudgetId()
	{
		return budgetId;
	}

	/**
	 * @param budgetId the budgetId to set
	 */
	public void setBudgetId(int budgetId)
	{
		this.budgetId = budgetId;
	}

	/**
	 * @return the subBudgetId
	 */
	public int getSubBudgetId()
	{
		return subBudgetId;
	}

	/**
	 * @param subBudgetId the subBudgetId to set
	 */
	public void setSubBudgetId(int subBudgetId)
	{
		this.subBudgetId = subBudgetId;
	}

	
	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate()
	{
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate)
	{
		this.createdDate = createdDate;
	}

	/**
	 * @return the createdUserId
	 */
	public String getCreatedUserId()
	{
		return createdUserId;
	}

	/**
	 * @param createdUserId the createdUserId to set
	 */
	public void setCreatedUserId(String createdUserId)
	{
		this.createdUserId = createdUserId;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate()
	{
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate)
	{
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the modifiedUserId
	 */
	public String getModifiedUserId()
	{
		return modifiedUserId;
	}

	/**
	 * @param modifiedUserId the modifiedUserId to set
	 */
	public void setModifiedUserId(String modifiedUserId)
	{
		this.modifiedUserId = modifiedUserId;
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
	 * @return the ammount
	 */
	public String getAmmount()
	{
		return ammount;
	}

	/**
	 * @param ammount the ammount to set
	 */
	public void setAmmount(String ammount)
	{
		this.ammount = ammount;
	}

	/**
	 * @return the modificationAmount
	 */
	public String getModificationAmount()
	{
		return modificationAmount;
	}

	/**
	 * @param modificationAmount the modificationAmount to set
	 */
	public void setModificationAmount(String modificationAmount)
	{
		this.modificationAmount = modificationAmount;
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

	/**
	 * @return the ammountMod
	 */
	public String getAmmountMod()
	{
		return ammountMod;
	}

	/**
	 * @param ammountMod the ammountMod to set
	 */
	public void setAmmountMod(String ammountMod)
	{
		this.ammountMod = ammountMod;
	}

	/**
	 * @return the modCount
	 */
	public int getModCount()
	{
		return modCount;
	}

	/**
	 * @param modCount the modCount to set
	 */
	public void setModCount(int modCount)
	{
		this.modCount = modCount;
	}

	/**
	 * @return the orgCount
	 */
	public int getOrgCount()
	{
		return orgCount;
	}

	/**
	 * @param orgCount the orgCount to set
	 */
	public void setOrgCount(int orgCount)
	{
		this.orgCount = orgCount;
	}

	/**
	 * @return the modifyByProvider
	 */
	public String getModifyByProvider()
	{
		return modifyByProvider;
	}

	/**
	 * @param modifyByProvider the modifyByProvider to set
	 */
	public void setModifyByProvider(String modifyByProvider)
	{
		this.modifyByProvider = modifyByProvider;
	}

	/**
	 * @return the modifyByAgency
	 */
	public String getModifyByAgency()
	{
		return modifyByAgency;
	}

	/**
	 * @param modifyByProvider the modifyByProvider to set
	 */
	public void setModifyByAgency(String modifyByAgency)
	{
		this.modifyByAgency = modifyByAgency;
	}

	public void setUnallocatedFund(String unallocatedFund) {
		this.unallocatedFund = unallocatedFund;
	}

	public String getUnallocatedFund() {
		return unallocatedFund;
	}
	
	@Override
	public String toString() {
		return "UnallocatedFunds [id=" + id 
				+ ", unallocatedId=" + unallocatedId 
				+ ", unallocatedFund=" + unallocatedFund
				+ ", budgetId=" + budgetId 
				+ ", subBudgetId=" + subBudgetId
				+ ", ammount=" + ammount 
				+ ", approvedBudget=" + approvedBudget
				+ ", createdDate=" + createdDate 
				+ ", createdUserId=" + createdUserId 
				+ ", modifiedDate=" + modifiedDate
				+ ", modifiedUserId=" + modifiedUserId 
				+ ", invoiceAmount=" + invoiceAmount 
				+ ", modificationAmount=" + modificationAmount
				+ ", proposedBudget=" + proposedBudget 
				+ ", ammountMod=" + ammountMod 
				+ ", modCount=" + modCount 
				+ ", orgCount=" + orgCount 
				+ ", modifyByProvider=" + modifyByProvider
				+ ", modifyByAgency=" 	+ modifyByAgency 
				+ ", parentBudgetId="	+ parentBudgetId
				+ ", parentId=" + parentId 
				+ ", childId=" + childId 
				+ ", parentSubBudgetId="+ parentSubBudgetId 
				+ ", type=" + type + "]";
	}

}
