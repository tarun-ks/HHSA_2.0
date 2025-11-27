/**
 * 
 */
package com.nyc.hhs.model;

/**
 * This bean will be used to display Procurement Summary on
 * Home page
 */
public class ProcurementSummaryBean
{

	private String scheduledRFPWithIn10Days;
	private String scheduledRFPWithIn60Days;
	private String rfpInReleasedStatus;
	private String proposalDueDateIn10Days;
	private String rfpWithProposalsReceived;
	private String rfpWithEvaluationsComplete;
	private String approveAwardTaskCount;
	private String rfpReleaseIn30Days;
	private String rfpDueDateIn30Days;
	private String rfpWith1Draft;
	private String rfpWith1Proposal;
	private String rfpEligibleForAward;
	private String pendingCOF;
	
	//Start : R5 Added
	private String proposalReturnRevision;
	
	/**
	 * @return the proposalReturnRevision
	 */
	public String getProposalReturnRevision()
	{
		return proposalReturnRevision;
	}

	/**
	 * @param proposalReturnRevision the proposalReturnRevision to set
	 */
	public void setProposalReturnRevision(String proposalReturnRevision)
	{
		this.proposalReturnRevision = proposalReturnRevision;
	}
	//End : R5 Added
	/**
	 * @return the scheduledRFPWithIn10Days
	 */
	public String getScheduledRFPWithIn10Days()
	{
		return scheduledRFPWithIn10Days;
	}

	/**
	 * @param scheduledRFPWithIn10Days the scheduledRFPWithIn10Days to set
	 */
	public void setScheduledRFPWithIn10Days(String scheduledRFPWithIn10Days)
	{
		this.scheduledRFPWithIn10Days = scheduledRFPWithIn10Days;
	}

	/**
	 * @return the scheduledRFPWithIn60Days
	 */
	public String getScheduledRFPWithIn60Days()
	{
		return scheduledRFPWithIn60Days;
	}

	/**
	 * @param scheduledRFPWithIn60Days the scheduledRFPWithIn60Days to set
	 */
	public void setScheduledRFPWithIn60Days(String scheduledRFPWithIn60Days)
	{
		this.scheduledRFPWithIn60Days = scheduledRFPWithIn60Days;
	}

	/**
	 * @return the rfpInReleasedStatus
	 */
	public String getRfpInReleasedStatus()
	{
		return rfpInReleasedStatus;
	}

	/**
	 * @param rfpInReleasedStatus the rfpInReleasedStatus to set
	 */
	public void setRfpInReleasedStatus(String rfpInReleasedStatus)
	{
		this.rfpInReleasedStatus = rfpInReleasedStatus;
	}

	/**
	 * @return the proposalDueDateIn10Days
	 */
	public String getProposalDueDateIn10Days()
	{
		return proposalDueDateIn10Days;
	}

	/**
	 * @param proposalDueDateIn10Days the proposalDueDateIn10Days to set
	 */
	public void setProposalDueDateIn10Days(String proposalDueDateIn10Days)
	{
		this.proposalDueDateIn10Days = proposalDueDateIn10Days;
	}

	/**
	 * @return the rfpWithProposalsReceived
	 */
	public String getRfpWithProposalsReceived()
	{
		return rfpWithProposalsReceived;
	}

	/**
	 * @param rfpWithProposalsReceived the rfpWithProposalsReceived to set
	 */
	public void setRfpWithProposalsReceived(String rfpWithProposalsReceived)
	{
		this.rfpWithProposalsReceived = rfpWithProposalsReceived;
	}

	/**
	 * @return the loRfpWithEvaluationsComplete
	 */
	public String getRfpWithEvaluationsComplete()
	{
		return rfpWithEvaluationsComplete;
	}

	/**
	 * @param loRfpWithEvaluationsComplete the loRfpWithEvaluationsComplete to
	 *            set
	 */
	public void setRfpWithEvaluationsComplete(String rfpWithEvaluationsComplete)
	{
		this.rfpWithEvaluationsComplete = rfpWithEvaluationsComplete;
	}

	/**
	 * @return the approveAwardTaskCount
	 */
	public String getApproveAwardTaskCount()
	{
		return approveAwardTaskCount;
	}

	/**
	 * @param approveAwardTaskCount the approveAwardTaskCount to set
	 */
	public void setApproveAwardTaskCount(String approveAwardTaskCount)
	{
		this.approveAwardTaskCount = approveAwardTaskCount;
	}

	/**
	 * @return the pendingCOF
	 */
	public String getPendingCOF()
	{
		return pendingCOF;
	}

	/**
	 * @param pendingCOF the pendingCOF to set
	 */
	public void setPendingCOF(String pendingCOF)
	{
		this.pendingCOF = pendingCOF;
	}

	/**
	 * @return the rfpReleaseIn30Days
	 */
	public String getRfpReleaseIn30Days()
	{
		return rfpReleaseIn30Days;
	}

	/**
	 * @param rfpReleaseIn30Days the rfpReleaseIn30Days to set
	 */
	public void setRfpReleaseIn30Days(String rfpReleaseIn30Days)
	{
		this.rfpReleaseIn30Days = rfpReleaseIn30Days;
	}

	/**
	 * @return the rfpDueDateIn30Days
	 */
	public String getRfpDueDateIn30Days()
	{
		return rfpDueDateIn30Days;
	}

	/**
	 * @param rfpDueDateIn30Days the rfpDueDateIn30Days to set
	 */
	public void setRfpDueDateIn30Days(String rfpDueDateIn30Days)
	{
		this.rfpDueDateIn30Days = rfpDueDateIn30Days;
	}

	/**
	 * @return the rfpWith1Draft
	 */
	public String getRfpWith1Draft()
	{
		return rfpWith1Draft;
	}

	/**
	 * @param rfpWith1Draft the rfpWith1Draft to set
	 */
	public void setRfpWith1Draft(String rfpWith1Draft)
	{
		this.rfpWith1Draft = rfpWith1Draft;
	}

	/**
	 * @return the rfpWith1Proposal
	 */
	public String getRfpWith1Proposal()
	{
		return rfpWith1Proposal;
	}

	/**
	 * @param rfpWith1Proposal the rfpWith1Proposal to set
	 */
	public void setRfpWith1Proposal(String rfpWith1Proposal)
	{
		this.rfpWith1Proposal = rfpWith1Proposal;
	}

	/**
	 * @return the rfpEligibleForAward
	 */
	public String getRfpEligibleForAward()
	{
		return rfpEligibleForAward;
	}

	/**
	 * @param rfpEligibleForAward the rfpEligibleForAward to set
	 */
	public void setRfpEligibleForAward(String rfpEligibleForAward)
	{
		this.rfpEligibleForAward = rfpEligibleForAward;
	}

	/* (non-Javadoc) update in R5
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return "ProcurementSummaryBean [scheduledRFPWithIn10Days=" + scheduledRFPWithIn10Days
				+ ", scheduledRFPWithIn60Days=" + scheduledRFPWithIn60Days + ", rfpInReleasedStatus="
				+ rfpInReleasedStatus + ", proposalDueDateIn10Days=" + proposalDueDateIn10Days
				+ ", rfpWithProposalsReceived=" + rfpWithProposalsReceived + ", rfpWithEvaluationsComplete="
				+ rfpWithEvaluationsComplete + ", approveAwardTaskCount=" + approveAwardTaskCount
				+ ", rfpReleaseIn30Days=" + rfpReleaseIn30Days + ", rfpDueDateIn30Days=" + rfpDueDateIn30Days
				+ ", rfpWith1Draft=" + rfpWith1Draft + ", rfpWith1Proposal=" + rfpWith1Proposal
				+ ", rfpEligibleForAward=" + rfpEligibleForAward + ", pendingCOF=" + pendingCOF
				+ ", proposalReturnRevision=" + proposalReturnRevision + "]";
	}
}
