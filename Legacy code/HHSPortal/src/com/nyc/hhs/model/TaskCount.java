package com.nyc.hhs.model;

/**
 * This class is a bean which maintains the Task count details.
 * 
 */

public class TaskCount
{
	private int msBRAppCount;
	private int msSRAppCount;
	private int msBRAppWithdrawalCount;
	private int msSRWithdrawalAppCount;
	private int brAppMgrTaskCount;
	private int srAppMgrTaskCount;
	private int msContactCount;
	private boolean lbVisibilityFlag;
	private int newFilingCount;
	private int provAccountReqCount;
	private int orgLegalNameTasksCount;
	private int acceptPropTaskCount;
	private int evaluatePropTaskCount;
	private int reviewScoresTaskCount;
	private int awardDocTaskCount;
	private int contConfigTaskCount;
	private int certOfFundsTaskCount;
	private int budgetReviewTaskCount;
	private int invoiceReviewTaskCount;
	private int paymentReviewTaskCount;
	private int awardApprovalTaskCount;
	// entries added as per release 2.7.0 enhancement 5678
	private int advancePaymentRequestTaskCount;
	private int advancePaymentReviewTaskCount;
	private int amendmentCofTaskCount;
	private int contractBudgetAmendmentTaskCount;
	private int contractBudgetModificationTaskCount;
	private int contractBudgetUpdateTaskCount;
	private int contractConfigurationAmendmentTaskCount;
	private int contractConfigurationUpdateTaskCount;
	private int newFyConfigurationTaskCount;
	private int procurementCofTaskCount;
	// added for R5 module Manage Organization
	private int approvedPsrCount;
	private int awardApprovalAmountTaskCount;
	private int completePsrCount;
	private int finalizeAwardAmountCount;
	//Added for R6: return payment review task
	private int returnPaymentReviewTaskCount;
	//Added for R6: return payment review task end
	public int getAwardApprovalAmountTaskCount()
	{
		return awardApprovalAmountTaskCount;
	}

	public void setAwardApprovalAmountTaskCount(int awardApprovalAmountTaskCount)
	{
		this.awardApprovalAmountTaskCount = awardApprovalAmountTaskCount;
	}

	// added for R5 module Manage Organization

	public boolean isLbVisibilityFlag()
	{
		return lbVisibilityFlag;
	}

	public void setLbVisibilityFlag(boolean lbVisibilityFlag)
	{
		this.lbVisibilityFlag = lbVisibilityFlag;
	}

	public int getMsBRAppCount()
	{
		return msBRAppCount;
	}

	public void setMsBRAppCount(int msBRAppCount)
	{
		this.msBRAppCount = msBRAppCount;
	}

	public int getMsSRAppCount()
	{
		return msSRAppCount;
	}

	public void setMsSRAppCount(int msSRAppCount)
	{
		this.msSRAppCount = msSRAppCount;
	}

	public int getMsBRAppWithdrawalCount()
	{
		return msBRAppWithdrawalCount;
	}

	public void setMsBRAppWithdrawalCount(int msBRAppWithdrawalCount)
	{
		this.msBRAppWithdrawalCount = msBRAppWithdrawalCount;
	}

	public int getMsSRWithdrawalAppCount()
	{
		return msSRWithdrawalAppCount;
	}

	public void setMsSRWithdrawalAppCount(int msSRWithdrawalAppCount)
	{
		this.msSRWithdrawalAppCount = msSRWithdrawalAppCount;
	}

	public int getMsContactCount()
	{
		return msContactCount;
	}

	public void setMsContactCount(int msContactCount)
	{
		this.msContactCount = msContactCount;
	}

	/**
	 * @return the acceptPropTaskCount
	 */
	public int getAcceptPropTaskCount()
	{
		return acceptPropTaskCount;
	}

	/**
	 * @param acceptPropTaskCount the acceptPropTaskCount to set
	 */
	public void setAcceptPropTaskCount(int acceptPropTaskCount)
	{
		this.acceptPropTaskCount = acceptPropTaskCount;
	}

	/**
	 * @return the evaluatePropTaskCount
	 */
	public int getEvaluatePropTaskCount()
	{
		return evaluatePropTaskCount;
	}

	/**
	 * @param evaluatePropTaskCount the evaluatePropTaskCount to set
	 */
	public void setEvaluatePropTaskCount(int evaluatePropTaskCount)
	{
		this.evaluatePropTaskCount = evaluatePropTaskCount;
	}

	/**
	 * @return the reviewScoresTaskCount
	 */
	public int getReviewScoresTaskCount()
	{
		return reviewScoresTaskCount;
	}

	/**
	 * @param reviewScoresTaskCount the reviewScoresTaskCount to set
	 */
	public void setReviewScoresTaskCount(int reviewScoresTaskCount)
	{
		this.reviewScoresTaskCount = reviewScoresTaskCount;
	}

	/**
	 * @return the awardDocTaskCount
	 */
	public int getAwardDocTaskCount()
	{
		return awardDocTaskCount;
	}

	/**
	 * @param awardDocTaskCount the awardDocTaskCount to set
	 */
	public void setAwardDocTaskCount(int awardDocTaskCount)
	{
		this.awardDocTaskCount = awardDocTaskCount;
	}

	/**
	 * @return the contConfigTaskCount
	 */
	public int getContConfigTaskCount()
	{
		return contConfigTaskCount;
	}

	/**
	 * @param contConfigTaskCount the contConfigTaskCount to set
	 */
	public void setContConfigTaskCount(int contConfigTaskCount)
	{
		this.contConfigTaskCount = contConfigTaskCount;
	}

	/**
	 * @return the certOfFundsTaskCount
	 */
	public int getCertOfFundsTaskCount()
	{
		return certOfFundsTaskCount;
	}

	/**
	 * @param certOfFundsTaskCount the certOfFundsTaskCount to set
	 */
	public void setCertOfFundsTaskCount(int certOfFundsTaskCount)
	{
		this.certOfFundsTaskCount = certOfFundsTaskCount;
	}

	/**
	 * @return the budgetReviewTaskCount
	 */
	public int getBudgetReviewTaskCount()
	{
		return budgetReviewTaskCount;
	}

	/**
	 * @param budgetReviewTaskCount the budgetReviewTaskCount to set
	 */
	public void setBudgetReviewTaskCount(int budgetReviewTaskCount)
	{
		this.budgetReviewTaskCount = budgetReviewTaskCount;
	}

	/**
	 * @return the invoiceReviewTaskCount
	 */
	public int getInvoiceReviewTaskCount()
	{
		return invoiceReviewTaskCount;
	}

	/**
	 * @param invoiceReviewTaskCount the invoiceReviewTaskCount to set
	 */
	public void setInvoiceReviewTaskCount(int invoiceReviewTaskCount)
	{
		this.invoiceReviewTaskCount = invoiceReviewTaskCount;
	}

	/**
	 * @return the paymentReviewTaskCount
	 */
	public int getPaymentReviewTaskCount()
	{
		return paymentReviewTaskCount;
	}

	/**
	 * @param paymentReviewTaskCount the paymentReviewTaskCount to set
	 */
	public void setPaymentReviewTaskCount(int paymentReviewTaskCount)
	{
		this.paymentReviewTaskCount = paymentReviewTaskCount;
	}

	/**
	 * @return the awardApprovalTaskCount
	 */
	public int getAwardApprovalTaskCount()
	{
		return awardApprovalTaskCount;
	}

	/**
	 * @param awardApprovalTaskCount the awardApprovalTaskCount to set
	 */
	public void setAwardApprovalTaskCount(int awardApprovalTaskCount)
	{
		this.awardApprovalTaskCount = awardApprovalTaskCount;
	}

	/**
	 * @return the newFilingCount
	 */
	public int getNewFilingCount()
	{
		return newFilingCount;
	}

	/**
	 * @param newFilingCount the newFilingCount to set
	 */
	public void setNewFilingCount(int newFilingCount)
	{
		this.newFilingCount = newFilingCount;
	}

	/**
	 * @return the provAccountReqCount
	 */
	public int getProvAccountReqCount()
	{
		return provAccountReqCount;
	}

	/**
	 * @param provAccountReqCount the provAccountReqCount to set
	 */
	public void setProvAccountReqCount(int provAccountReqCount)
	{
		this.provAccountReqCount = provAccountReqCount;
	}

	/**
	 * @return the orgLegalNameTasksCount
	 */
	public int getOrgLegalNameTasksCount()
	{
		return orgLegalNameTasksCount;
	}

	/**
	 * @param orgLegalNameTasksCount the orgLegalNameTasksCount to set
	 */
	public void setOrgLegalNameTasksCount(int orgLegalNameTasksCount)
	{
		this.orgLegalNameTasksCount = orgLegalNameTasksCount;
	}

	/**
	 * @return the brAppMgrTaskCount
	 */
	public int getBrAppMgrTaskCount()
	{
		return brAppMgrTaskCount;
	}

	/**
	 * @param brAppMgrTaskCount the brAppMgrTaskCount to set
	 */
	public void setBrAppMgrTaskCount(int brAppMgrTaskCount)
	{
		this.brAppMgrTaskCount = brAppMgrTaskCount;
	}

	/**
	 * @return the srAppMgrTaskCount
	 */
	public int getSrAppMgrTaskCount()
	{
		return srAppMgrTaskCount;
	}

	/**
	 * @param srAppMgrTaskCount the srAppMgrTaskCount to set
	 */
	public void setSrAppMgrTaskCount(int srAppMgrTaskCount)
	{
		this.srAppMgrTaskCount = srAppMgrTaskCount;
	}

	@Override
	public String toString()
	{
		return "TaskCount [msBRAppCount=" + msBRAppCount + ", msSRAppCount=" + msSRAppCount
				+ ", msBRAppWithdrawalCount=" + msBRAppWithdrawalCount + ", msSRWithdrawalAppCount="
				+ msSRWithdrawalAppCount + ", brAppMgrTaskCount=" + brAppMgrTaskCount + ", srAppMgrTaskCount="
				+ srAppMgrTaskCount + ", msContactCount=" + msContactCount + ", lbVisibilityFlag=" + lbVisibilityFlag
				+ ", newFilingCount=" + newFilingCount + ", provAccountReqCount=" + provAccountReqCount
				+ ", orgLegalNameTasksCount=" + orgLegalNameTasksCount + ", acceptPropTaskCount=" + acceptPropTaskCount
				+ ", evaluatePropTaskCount=" + evaluatePropTaskCount + ", reviewScoresTaskCount="
				+ reviewScoresTaskCount + ", awardDocTaskCount=" + awardDocTaskCount + ", contConfigTaskCount="
				+ contConfigTaskCount + ", certOfFundsTaskCount=" + certOfFundsTaskCount + ", budgetReviewTaskCount="
				+ budgetReviewTaskCount + ", invoiceReviewTaskCount=" + invoiceReviewTaskCount
				+ ", paymentReviewTaskCount=" + paymentReviewTaskCount + ", awardApprovalTaskCount="
				+ awardApprovalTaskCount + "]";
	}

	/**
	 * @return the advancePaymentRequestTaskCount
	 */
	public int getAdvancePaymentRequestTaskCount()
	{
		return advancePaymentRequestTaskCount;
	}

	/**
	 * @param advancePaymentRequestTaskCount the advancePaymentRequestTaskCount
	 *            to set
	 */
	public void setAdvancePaymentRequestTaskCount(int advancePaymentRequestTaskCount)
	{
		this.advancePaymentRequestTaskCount = advancePaymentRequestTaskCount;
	}

	/**
	 * @return the advancePaymentReviewTaskCount
	 */
	public int getAdvancePaymentReviewTaskCount()
	{
		return advancePaymentReviewTaskCount;
	}

	/**
	 * @param advancePaymentReviewTaskCount the advancePaymentReviewTaskCount to
	 *            set
	 */
	public void setAdvancePaymentReviewTaskCount(int advancePaymentReviewTaskCount)
	{
		this.advancePaymentReviewTaskCount = advancePaymentReviewTaskCount;
	}

	/**
	 * @return the amendmentCofTaskCount
	 */
	public int getAmendmentCofTaskCount()
	{
		return amendmentCofTaskCount;
	}

	/**
	 * @param amendmentCofTaskCount the amendmentCofTaskCount to set
	 */
	public void setAmendmentCofTaskCount(int amendmentCofTaskCount)
	{
		this.amendmentCofTaskCount = amendmentCofTaskCount;
	}

	/**
	 * @return the contractBudgetAmendmentTaskCount
	 */
	public int getContractBudgetAmendmentTaskCount()
	{
		return contractBudgetAmendmentTaskCount;
	}

	/**
	 * @param contractBudgetAmendmentTaskCount the
	 *            contractBudgetAmendmentTaskCount to set
	 */
	public void setContractBudgetAmendmentTaskCount(int contractBudgetAmendmentTaskCount)
	{
		this.contractBudgetAmendmentTaskCount = contractBudgetAmendmentTaskCount;
	}

	/**
	 * @return the contractBudgetModificationTaskCount
	 */
	public int getContractBudgetModificationTaskCount()
	{
		return contractBudgetModificationTaskCount;
	}

	/**
	 * @param contractBudgetModificationTaskCount the
	 *            contractBudgetModificationTaskCount to set
	 */
	public void setContractBudgetModificationTaskCount(int contractBudgetModificationTaskCount)
	{
		this.contractBudgetModificationTaskCount = contractBudgetModificationTaskCount;
	}

	/**
	 * @return the contractBudgetUpdateTaskCount
	 */
	public int getContractBudgetUpdateTaskCount()
	{
		return contractBudgetUpdateTaskCount;
	}

	/**
	 * @param contractBudgetUpdateTaskCount the contractBudgetUpdateTaskCount to
	 *            set
	 */
	public void setContractBudgetUpdateTaskCount(int contractBudgetUpdateTaskCount)
	{
		this.contractBudgetUpdateTaskCount = contractBudgetUpdateTaskCount;
	}

	/**
	 * @return the contractConfigurationAmendmentTaskCount
	 */
	public int getContractConfigurationAmendmentTaskCount()
	{
		return contractConfigurationAmendmentTaskCount;
	}

	/**
	 * @param contractConfigurationAmendmentTaskCount the
	 *            contractConfigurationAmendmentTaskCount to set
	 */
	public void setContractConfigurationAmendmentTaskCount(int contractConfigurationAmendmentTaskCount)
	{
		this.contractConfigurationAmendmentTaskCount = contractConfigurationAmendmentTaskCount;
	}

	/**
	 * @return the contractConfigurationUpdateTaskCount
	 */
	public int getContractConfigurationUpdateTaskCount()
	{
		return contractConfigurationUpdateTaskCount;
	}

	/**
	 * @param contractConfigurationUpdateTaskCount the
	 *            contractConfigurationUpdateTaskCount to set
	 */
	public void setContractConfigurationUpdateTaskCount(int contractConfigurationUpdateTaskCount)
	{
		this.contractConfigurationUpdateTaskCount = contractConfigurationUpdateTaskCount;
	}

	/**
	 * @return the newFyConfigurationTaskCount
	 */
	public int getNewFyConfigurationTaskCount()
	{
		return newFyConfigurationTaskCount;
	}

	/**
	 * @param newFyConfigurationTaskCount the newFyConfigurationTaskCount to set
	 */
	public void setNewFyConfigurationTaskCount(int newFyConfigurationTaskCount)
	{
		this.newFyConfigurationTaskCount = newFyConfigurationTaskCount;
	}

	/**
	 * @return the procurementCofTaskCount
	 */
	public int getProcurementCofTaskCount()
	{
		return procurementCofTaskCount;
	}

	/**
	 * @param procurementCofTaskCount the procurementCofTaskCount to set
	 */
	public void setProcurementCofTaskCount(int procurementCofTaskCount)
	{
		this.procurementCofTaskCount = procurementCofTaskCount;
	}

	// added for R5 module Manage Organization
	public int getCompletePsrCount()
	{
		return completePsrCount;
	}

	public void setCompletePsrCount(int completePsrCount)
	{
		this.completePsrCount = completePsrCount;
	}

	public int getFinalizeAwardAmountCount()
	{
		return finalizeAwardAmountCount;
	}

	public void setFinalizeAwardAmountCount(int finalizeAwardAmountCount)
	{
		this.finalizeAwardAmountCount = finalizeAwardAmountCount;
	}

	public int getApprovedPsrCount()
	{
		return approvedPsrCount;
	}

	public void setApprovedPsrCount(int approvedPsrCount)
	{
		this.approvedPsrCount = approvedPsrCount;
	}
	// Ends R5 Changes module Manage Organization
	//Added for R6: return payment review task
	public int getReturnPaymentReviewTaskCount()
	{
		return returnPaymentReviewTaskCount;
	}

	public void setReturnPaymentReviewTaskCount(int returnPaymentReviewTaskCount)
	{
		this.returnPaymentReviewTaskCount = returnPaymentReviewTaskCount;
	}
	//Added for R6: return payment review task
}
