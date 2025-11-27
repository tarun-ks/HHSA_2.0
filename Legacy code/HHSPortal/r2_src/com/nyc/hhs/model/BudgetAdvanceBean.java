package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class BudgetAdvanceBean
{
	private String advanceNumber;
	@RegExp(value ="^\\d{0,22}")
	private String contractId;
	@RegExp(value ="^\\d{0,22}")
	private String budgetId;
	private String advAmntRequested;
	private String ctNumber;
	private String providerName;
	private String advRequestedDate;
	private String description;
	private String fiscalYear;
	private String userId;
	private String status;
	private String procId;
	private String procTitle;
	private String epin;
	private String agencyId;
	private String orgId;
	private String programId;
	private String paymentVoucherNumber;
	private String invoiceId;
	private String period;
	private String processFlag;
	private String workflowId;
	private String procStatusId;
	private String orgType;
	private String modifyByProvider;
	private String budgetAdvanceId;

    /** Start QC9149  R 7.7.0 */
    private Integer negativeAmendCnt;
    public  Integer getNegativeAmendCnt() {
        return negativeAmendCnt;
    }
    public void setNegativeAmendCnt(Integer negativeAmendCnt) {
        this.negativeAmendCnt = negativeAmendCnt;
    }
    /** End QC9149  R 7.7.0 */

	public String getBudgetAdvanceId()
	{
		return budgetAdvanceId;
	}

	public void setBudgetAdvanceId(String budgetAdvanceId)
	{
		this.budgetAdvanceId = budgetAdvanceId;
	}

	public String getModifyByProvider()
	{
		return modifyByProvider;
	}

	public void setModifyByProvider(String modifyByProvider)
	{
		this.modifyByProvider = modifyByProvider;
	}

	public String getModifyByAgency()
	{
		return modifyByAgency;
	}

	public void setModifyByAgency(String modifyByAgency)
	{
		this.modifyByAgency = modifyByAgency;
	}

	private String modifyByAgency;

	public String getOrgType()
	{
		return orgType;
	}

	public void setOrgType(String orgType)
	{
		this.orgType = orgType;
	}

	public String getPaymentVoucherNumber()
	{
		return paymentVoucherNumber;
	}

	public void setPaymentVoucherNumber(String paymentVoucherNumber)
	{
		this.paymentVoucherNumber = paymentVoucherNumber;
	}

	public String getInvoiceId()
	{
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId)
	{
		this.invoiceId = invoiceId;
	}

	public String getPeriod()
	{
		return period;
	}

	public void setPeriod(String period)
	{
		this.period = period;
	}

	public String getProcessFlag()
	{
		return processFlag;
	}

	public void setProcessFlag(String processFlag)
	{
		this.processFlag = processFlag;
	}

	public String getWorkflowId()
	{
		return workflowId;
	}

	public void setWorkflowId(String workflowId)
	{
		this.workflowId = workflowId;
	}

	public String getProcStatusId()
	{
		return procStatusId;
	}

	public void setProcStatusId(String procStatusId)
	{
		this.procStatusId = procStatusId;
	}

	public String getProcId()
	{
		return procId;
	}

	public void setProcId(String procId)
	{
		this.procId = procId;
	}

	public String getProcTitle()
	{
		return procTitle;
	}

	public void setProcTitle(String procTitle)
	{
		this.procTitle = procTitle;
	}

	public String getEpin()
	{
		return epin;
	}

	public void setEpin(String epin)
	{
		this.epin = epin;
	}

	public String getAgencyId()
	{
		return agencyId;
	}

	public void setAgencyId(String agencyId)
	{
		this.agencyId = agencyId;
	}

	public String getOrgId()
	{
		return orgId;
	}

	public void setOrgId(String orgId)
	{
		this.orgId = orgId;
	}

	public String getProgramId()
	{
		return programId;
	}

	public void setProgramId(String programId)
	{
		this.programId = programId;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getUserId()
	{
		return userId;
	}

	public void setUserId(String userId)
	{
		this.userId = userId;
	}

	public String getAdvanceNumber()
	{
		return advanceNumber;
	}

	public void setAdvanceNumber(String advanceNumber)
	{
		this.advanceNumber = advanceNumber;
	}

	public String getContractId()
	{
		return contractId;
	}

	public void setContractId(String contractId)
	{
		this.contractId = contractId;
	}

	public String getBudgetId()
	{
		return budgetId;
	}

	public void setBudgetId(String budgetId)
	{
		this.budgetId = budgetId;
	}

	public String getAdvAmntRequested()
	{
		return advAmntRequested;
	}

	public void setAdvAmntRequested(String advAmntRequested)
	{
		this.advAmntRequested = advAmntRequested;
	}

	public String getCtNumber()
	{
		return ctNumber;
	}

	public void setCtNumber(String ctNumber)
	{
		this.ctNumber = ctNumber;
	}

	public String getProviderName()
	{
		return providerName;
	}

	public void setProviderName(String providerName)
	{
		this.providerName = providerName;
	}

	public String getAdvRequestedDate()
	{
		return advRequestedDate;
	}

	public void setAdvRequestedDate(String advRequestedDate)
	{
		this.advRequestedDate = advRequestedDate;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String getFiscalYear()
	{
		return fiscalYear;
	}

	public void setFiscalYear(String fiscalYear)
	{
		this.fiscalYear = fiscalYear;
	}

	@Override
	public String toString()
	{
		return "BudgetAdvanceBean [advanceNumber=" + advanceNumber + ", contractId=" + contractId + ", budgetId="
				+ budgetId + ", advAmntRequested=" + advAmntRequested + ", ctNumber=" + ctNumber + ", providerName="
				+ providerName + ", advRequestedDate=" + advRequestedDate + ", description=" + description
				+ ", fiscalYear=" + fiscalYear + ", userId=" + userId + ", status=" + status + ", procId=" + procId
				+ ", procTitle=" + procTitle + ", epin=" + epin + ", agencyId=" + agencyId + ", orgId=" + orgId
				+ ", programId=" + programId + ", paymentVoucherNumber=" + paymentVoucherNumber + ", invoiceId="
				+ invoiceId + ", period=" + period + ", processFlag=" + processFlag + ", workflowId=" + workflowId
				+ ", procStatusId=" + procStatusId + ", orgType=" + orgType + ", modifyByProvider=" + modifyByProvider
				+ ", budgetAdvanceId=" + budgetAdvanceId + ", modifyByAgency=" + modifyByAgency + "]";
	}

}
