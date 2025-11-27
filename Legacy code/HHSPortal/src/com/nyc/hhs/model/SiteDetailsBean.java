/**
 * 
 */
package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class SiteDetailsBean extends AddressValidationBean
{
	@RegExp(value ="^\\d{0,22}")
	private String procurementId;
	private String siteName;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String zipCode;
	private String proposalSiteId;
	private String actionTaken;
	private String createdBy;
	private String modifiedBy;
	private String addressRelatedData;
	//@Digits(integer=22, fraction=0)
	@RegExp(value ="^\\d{0,22}")
	private String proposalId;
	//Made changes for Release 3.6.0 for Enhancement id 6484
	private String subBudgetSiteId;
	private String subBudgetId;
	private String parentSubBudgetId;
	private String statusId;

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
	 * @return the proposalId
	 */
	public String getProposalId()
	{
		return proposalId;
	}

	/**
	 * @param proposalId the proposalId to set
	 */
	public void setProposalId(String proposalId)
	{
		this.proposalId = proposalId;
	}

	/**
	 * @return the addressRelatedData
	 */
	public String getAddressRelatedData()
	{
		return addressRelatedData;
	}

	/**
	 * @param addressRelatedData the addressRelatedData to set
	 */
	public void setAddressRelatedData(String addressRelatedData)
	{
		this.addressRelatedData = addressRelatedData;
	}

	/**
	 * @return the actionTaken
	 */
	public String getActionTaken()
	{
		return actionTaken;
	}

	/**
	 * @param actionTaken the actionTaken to set
	 */
	public void setActionTaken(String actionTaken)
	{
		this.actionTaken = actionTaken;
	}

	/**
	 * @return the proposalSiteId
	 */
	public String getProposalSiteId()
	{
		return proposalSiteId;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy()
	{
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy)
	{
		this.createdBy = createdBy;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy()
	{
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy)
	{
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the siteName
	 */
	public String getSiteName()
	{
		return siteName;
	}

	/**
	 * @param siteName the siteName to set
	 */
	public void setSiteName(String siteName)
	{
		this.siteName = siteName;
	}

	/**
	 * @return the address1
	 */
	public String getAddress1()
	{
		return address1;
	}

	/**
	 * @param address1 the address1 to set
	 */
	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}

	/**
	 * @return the address2
	 */
	public String getAddress2()
	{
		return address2;
	}

	/**
	 * @param address2 the address2 to set
	 */
	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}

	/**
	 * @return the city
	 */
	public String getCity()
	{
		return city;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city)
	{
		this.city = city;
	}

	/**
	 * @return the state
	 */
	public String getState()
	{
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	/**
	 * @return the zipCode
	 */
	public String getZipCode()
	{
		return zipCode;
	}

	/**
	 * @param zipCode the zipCode to set
	 */
	public void setZipCode(String zipCode)
	{
		this.zipCode = zipCode;
	}

	/**
	 * @return the proposalSiteId
	 */
	public String getSiteSeqNo()
	{
		return proposalSiteId;
	}

	/**
	 * @param proposalSiteId the proposalSiteId to set
	 */
	public void setProposalSiteId(String proposalSiteId)
	{
		this.proposalSiteId = proposalSiteId;
	}

	@Override
	public String toString() {
		return "SiteDetailsBean [procurementId=" + procurementId
				+ ", siteName=" + siteName + ", address1=" + address1
				+ ", address2=" + address2 + ", city=" + city + ", state="
				+ state + ", zipCode=" + zipCode + ", proposalSiteId="
				+ proposalSiteId + ", actionTaken=" + actionTaken
				+ ", createdBy=" + createdBy + ", modifiedBy=" + modifiedBy
				+ ", addressRelatedData=" + addressRelatedData
				+ ", proposalId=" + proposalId + "]";
	}


	public void setSubBudgetId(String subBudgetId) {
		this.subBudgetId = subBudgetId;
	}

	public String getSubBudgetId() {
		return subBudgetId;
	}

	public void setSubBudgetSiteId(String subBudgetSiteId) {
		this.subBudgetSiteId = subBudgetSiteId;
	}

	public String getSubBudgetSiteId() {
		return subBudgetSiteId;
	}

	public void setParentSubBudgetId(String parentSubBudgetId) {
		this.parentSubBudgetId = parentSubBudgetId;
	}

	public String getParentSubBudgetId() {
		return parentSubBudgetId;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getStatusId() {
		return statusId;
	}
}
