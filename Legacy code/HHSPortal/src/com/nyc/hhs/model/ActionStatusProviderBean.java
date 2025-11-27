package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

public class ActionStatusProviderBean {

	
	@Length(max = 20) 
	private String providerId;
	@Length(max = 20) 
	private String agencyId;
	@Length(max = 1) 
	private String submitInvoice;
	@Length(max = 1)
	private String budgetMod;
	@Length(max = 1)
	private String cancelMod;
	@Length(max = 1)
    private String requestAdvance;
	@Length(max = 1)
	private String deleteInvoice;
	@Length(max = 1)
	private String withdrawInvoice;

	
	public String getAgencyId() {
		return agencyId;
	}
	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
	}

	
	public String getProviderId() {
		return providerId;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	public String getSubmitInvoice() {
		return submitInvoice;
	}
	public void setSubmitInvoice(String submitInvoice) {
		this.submitInvoice = submitInvoice;
	}
	public String getBudgetMod() {
		return budgetMod;
	}
	public void setBudgetMod(String budgetMod) {
		this.budgetMod = budgetMod;
	}
	public String getCancelMod() {
		return cancelMod;
	}
	public void setCancelMod(String cancelMod) {
		this.cancelMod = cancelMod;
	}
	public String getRequestAdvance() {
		return requestAdvance;
	}
	public void setRequestAdvance(String requestAdvance) {
		this.requestAdvance = requestAdvance;
	}
	public String getDeleteInvoice() {
		return deleteInvoice;
	}
	public void setDeleteInvoice(String deleteInvoice) {
		this.deleteInvoice = deleteInvoice;
	}
	public String getWithdrawInvoice() {
		return withdrawInvoice;
	}
	public void setWithdrawInvoice(String withdrawInvoice) {
		this.withdrawInvoice = withdrawInvoice;
	}



}
