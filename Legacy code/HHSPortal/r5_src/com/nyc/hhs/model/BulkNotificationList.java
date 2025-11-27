package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

import com.nyc.hhs.constants.HHSR5Constants;

public class BulkNotificationList
{
	private String providerId;
	@Length(max = 20)
	private String providerOrgId;
	private String orgLegalName;
	private String providerName;

	private String orgEIN;
	private String awardEPIN;
	private String ctNumber;

	private String procTitle;
	private String budgetFY;
	private String sentBy;

	private String sentById;
	private String notificationSent;
	private String sentTo;
	@RegExp(value ="^\\d{0,22}")
	private String contractId;

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getOrgLegalName()
	{
		return orgLegalName;
	}

	public String getProviderId()
	{
		return providerId;
	}

	public void setProviderId(String providerId)
	{
		this.providerId = providerId;
	}

	public String getProviderOrgId()
	{
		return providerOrgId;
	}

	public void setProviderOrgId(String providerOrgId)
	{
		this.providerOrgId = providerOrgId;
	}

	public String getProviderName()
	{
		return providerName;
	}

	public void setProviderName(String providerName)
	{
		this.providerName = providerName;
	}

	public String getSentById()
	{
		return sentById;
	}

	public void setSentById(String sentById)
	{
		this.sentById = sentById;
	}

	public void setOrgLegalName(String orgLegalName)
	{
		this.orgLegalName = orgLegalName;
	}

	public String getOrgEIN()
	{
		return orgEIN;
	}

	public void setOrgEIN(String orgEIN)
	{
		this.orgEIN = orgEIN;
	}

	public String getAwardEPIN()
	{
		return awardEPIN;
	}

	public void setAwardEPIN(String awardEPIN)
	{
		this.awardEPIN = awardEPIN;
	}

	public String getCtNumber()
	{
		return ctNumber;
	}

	public void setCtNumber(String ctNumber)
	{
		this.ctNumber = ctNumber;
	}

	public String getProcTitle()
	{
		return procTitle;
	}

	public void setProcTitle(String procTitle)
	{
		this.procTitle = procTitle;
	}

	public String getBudgetFY()
	{
		return budgetFY;
	}

	public void setBudgetFY(String budgetFY)
	{
		this.budgetFY = budgetFY;
	}

	public String getSentBy()
	{
		return sentBy;
	}

	public void setSentBy(String sentBy)
	{
		this.sentBy = sentBy;
	}

	public String getNotificationSent()
	{
		return notificationSent;
	}

	public void setNotificationSent(String notificationSent)
	{
		this.notificationSent = notificationSent;
	}

	public String getSentTo()
	{
		return sentTo;
	}

	public void setSentTo(String sentTo)
	{
		this.sentTo = sentTo;
	}

	@Override
	public String toString()
	{
		return orgLegalName.replaceAll("[,]","") + "," + orgEIN + "," + awardEPIN + "," + ctNumber + "," + procTitle.replaceAll("[,]","") + "," + budgetFY + ","
				+ sentBy + "," + HHSR5Constants.SPACE+notificationSent + "," + providerName.replaceAll("[,]","");
	}
	//Start: defect 8604
	private String groupNotificationId;

	public String getGroupNotificationId()
	{
		return groupNotificationId;
	}

	public void setGroupNotificationId(String groupNotificationId)
	{
		this.groupNotificationId = groupNotificationId;
	}
	//End: defect 8604
	

}
