package com.nyc.hhs.model;

import java.util.Date;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class AcceptProposalTaskBean
{
	@RegExp(value ="^\\d{0,22}")
	private String procurementId;
	@RegExp(value ="^\\d{0,22}")
	private String proposalId;
	private String procurementTitle;
	private String proposalTitle;
	@Length(max = 20)
	private String providerId;
	private String procurementEpin;
	private String agencyId;
	private Date updateFirstRoundApprovalDate;
	private String agencyPrimaryContact;
	private String agencySecondaryContact;
	private String awardAmount;
	private String agencyName;
	private String agencyPrimaryContactId;
	private String agencySecondaryContactId;
	private String accPrimaryContactId;
	private String accSecondaryContactId;
	private String accPrimaryContact;
	private String accSecondaryContact;
	private String competitionPoolTitle;
	private String evaluationGroupTitle;
	private String isOpenEndedRfp;

	public String getProcurementId()
	{
		return procurementId;
	}

	public void setProcurementId(String procurementId)
	{
		this.procurementId = procurementId;
	}

	public String getProposalId()
	{
		return proposalId;
	}

	public void setProposalId(String proposalId)
	{
		this.proposalId = proposalId;
	}

	public String getProcurementTitle()
	{
		return procurementTitle;
	}

	public void setProcurementTitle(String procurementTitle)
	{
		this.procurementTitle = procurementTitle;
	}

	public String getProposalTitle()
	{
		return proposalTitle;
	}

	public void setProposalTitle(String proposalTitle)
	{
		this.proposalTitle = proposalTitle;
	}

	public String getProviderId()
	{
		return providerId;
	}

	public void setProviderId(String providerId)
	{
		this.providerId = providerId;
	}

	public String getProcurementEpin()
	{
		return procurementEpin;
	}

	public void setProcurementEpin(String procurementEpin)
	{
		this.procurementEpin = procurementEpin;
	}

	public Date getUpdateFirstRoundApprovalDate()
	{
		return updateFirstRoundApprovalDate;
	}

	public void setUpdateFirstRoundApprovalDate(Date updateFirstRoundApprovalDate)
	{
		this.updateFirstRoundApprovalDate = updateFirstRoundApprovalDate;
	}

	public String getAgencyId()
	{
		return agencyId;
	}

	public void setAgencyId(String agencyId)
	{
		this.agencyId = agencyId;
	}

	public String getAgencyPrimaryContact()
	{
		return agencyPrimaryContact;
	}

	public void setAgencyPrimaryContact(String agencyPrimaryContact)
	{
		this.agencyPrimaryContact = agencyPrimaryContact;
	}

	public String getAgencySecondaryContact()
	{
		return agencySecondaryContact;
	}

	public void setAgencySecondaryContact(String agencySecondaryContact)
	{
		this.agencySecondaryContact = agencySecondaryContact;
	}

	public String getAwardAmount()
	{
		return awardAmount;
	}

	public void setAwardAmount(String awardAmount)
	{
		this.awardAmount = awardAmount;
	}

	public String getAgencyName()
	{
		return agencyName;
	}

	public void setAgencyName(String agencyName)
	{
		this.agencyName = agencyName;
	}

	public String getAgencyPrimaryContactId()
	{
		return agencyPrimaryContactId;
	}

	public void setAgencyPrimaryContactId(String agencyPrimaryContactId)
	{
		this.agencyPrimaryContactId = agencyPrimaryContactId;
	}

	public String getAgencySecondaryContactId()
	{
		return agencySecondaryContactId;
	}

	public void setAgencySecondaryContactId(String agencySecondaryContactId)
	{
		this.agencySecondaryContactId = agencySecondaryContactId;
	}

	public String getAccPrimaryContactId()
	{
		return accPrimaryContactId;
	}

	public void setAccPrimaryContactId(String accPrimaryContactId)
	{
		this.accPrimaryContactId = accPrimaryContactId;
	}

	public String getAccSecondaryContactId()
	{
		return accSecondaryContactId;
	}

	public void setAccSecondaryContactId(String accSecondaryContactId)
	{
		this.accSecondaryContactId = accSecondaryContactId;
	}

	public String getAccPrimaryContact()
	{
		return accPrimaryContact;
	}

	public void setAccPrimaryContact(String accPrimaryContact)
	{
		this.accPrimaryContact = accPrimaryContact;
	}

	public String getAccSecondaryContact()
	{
		return accSecondaryContact;
	}

	public void setAccSecondaryContact(String accSecondaryContact)
	{
		this.accSecondaryContact = accSecondaryContact;
	}

	/**
	 * @return the competitionPoolTitle
	 */
	public String getCompetitionPoolTitle()
	{
		return competitionPoolTitle;
	}

	/**
	 * @param competitionPoolTitle the competitionPoolTitle to set
	 */
	public void setCompetitionPoolTitle(String competitionPoolTitle)
	{
		this.competitionPoolTitle = competitionPoolTitle;
	}

	/**
	 * @return the evaluationGroupTitle
	 */
	public String getEvaluationGroupTitle()
	{
		return evaluationGroupTitle;
	}

	/**
	 * @param evaluationGroupTitle the evaluationGroupTitle to set
	 */
	public void setEvaluationGroupTitle(String evaluationGroupTitle)
	{
		this.evaluationGroupTitle = evaluationGroupTitle;
	}

	/**
	 * @return the isOpenEndedRfp
	 */
	public String getIsOpenEndedRfp()
	{
		return isOpenEndedRfp;
	}

	/**
	 * @param isOpenEndedRfp the isOpenEndedRfp to set
	 */
	public void setIsOpenEndedRfp(String isOpenEndedRfp)
	{
		this.isOpenEndedRfp = isOpenEndedRfp;
	}

	@Override
	public String toString()
	{
		return "AcceptProposalTaskBean [procurementId=" + procurementId + ", proposalId=" + proposalId
				+ ", procurementTitle=" + procurementTitle + ", proposalTitle=" + proposalTitle + ", providerId="
				+ providerId + ", procurementEpin=" + procurementEpin + ", agencyId=" + agencyId
				+ ", updateFirstRoundApprovalDate=" + updateFirstRoundApprovalDate + ", agencyPrimaryContact="
				+ agencyPrimaryContact + ", agencySecondaryContact=" + agencySecondaryContact + ", awardAmount="
				+ awardAmount + ", agencyName=" + agencyName + ", agencyPrimaryContactId=" + agencyPrimaryContactId
				+ ", agencySecondaryContactId=" + agencySecondaryContactId + ", accPrimaryContactId="
				+ accPrimaryContactId + ", accSecondaryContactId=" + accSecondaryContactId + ", accPrimaryContact="
				+ accPrimaryContact + ", accSecondaryContact=" + accSecondaryContact + "]";
	}
}
