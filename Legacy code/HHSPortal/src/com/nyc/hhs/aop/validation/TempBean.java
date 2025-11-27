package com.nyc.hhs.aop.validation;

import java.util.Map;
import java.util.TreeMap;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

/*
 * The TempBean is to use for the mybatis mappers that use hashMap as input arg, and handle validation for common fields
 */

public class TempBean {
	
	public static Map<String, String> map;
	static {	     
	      map = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
	      //key, value
	      // value is the bean field in the TempBean , the key(TreeMap -- case insensitive) is to use to find match for the fields in mapper the input argument is hashMap
	      map.put("proposalId", "proposalId"); 
	      map.put("asProposalId", "proposalId"); 
	      map.put("evaluationPoolMappingId", "evaluationPoolMappingId");
	      map.put("asEvaluationPoolMappingId", "evaluationPoolMappingId");
	      map.put("procurementID", "procurementID");
	      map.put("asProcurementID", "procurementID");
	      map.put("contractID", "contractID");
	      map.put("asContractId", "contractID");
	      map.put("amendcontractid","amendContractId");
	      map.put("asAmendContractId","amendContractId");
	      map.put("budgetID", "budgetID");
	      map.put("asBudgetId", "budgetID");
	      map.put("subBudgetID", "subBudgetID");
	      map.put("asSubBudgetID", "subBudgetID");
	      map.put("invoiceId", "invoiceId");
	      map.put("asInvoiceId", "invoiceId");
	      map.put("paymentId", "paymentId");
	      map.put("asPaymentId", "paymentId");
	      
	      map.put("asOrgId", "orgId");
	      map.put("userOrgId", "userOrgId");
	      map.put("asProviderOrgID", "providerOrgId");
	  }
	public static Map<String, String> getMap() {
		return map;
	}

	@RegExp(value ="^\\d{0,22}")
	private String proposalId;
	@RegExp(value ="^\\d{0,22}")
	private String evaluationPoolMappingId;
	@RegExp(value ="^\\d{0,22}")
	private String procurementID;
	@RegExp(value ="^\\d{0,22}")
	private String contractID ;
	@RegExp(value ="^\\d{0,22}")
	private String amendContractId;
	@RegExp(value ="^\\d{0,22}")
	private String budgetID;
	@RegExp(value ="^\\d{0,22}")
	private String subBudgetID;
	@RegExp(value ="^\\d{0,22}")
	private String invoiceId;
	@RegExp(value ="^\\d{0,22}")
	private String paymentId;
	
	@Length(max = 20)
	private String orgId;
	@Length(max = 20)
	private String userOrgId;
	@Length(max = 20)
	private String providerOrgId;
	
	
	public String getProposalId() {
		return proposalId;
	}
	public void setProposalId(String proposalId) {
		this.proposalId = proposalId;
	}
	public String getEvaluationPoolMappingId() {
		return evaluationPoolMappingId;
	}
	public void setEvaluationPoolMappingId(String evaluationPoolMappingId) {
		this.evaluationPoolMappingId = evaluationPoolMappingId;
	}
	public String getProcurementID() {
		return procurementID;
	}
	public void setProcurementID(String procurementID) {
		this.procurementID = procurementID;
	}
	public String getContractID() {
		return contractID;
	}
	public void setContractID(String contractID) {
		this.contractID = contractID;
	}
		
	public String getAmendContractId() {
		return amendContractId;
	}
	public void setAmendContractId(String amendContractId) {
		this.amendContractId = amendContractId;
	}
	public String getBudgetID() {
		return budgetID;
	}
	public void setBudgetID(String budgetID) {
		this.budgetID = budgetID;
	}
	public String getSubBudgetID() {
		return subBudgetID;
	}
	public void setSubBudgetID(String subBudgetID) {
		this.subBudgetID = subBudgetID;
	}
	public String getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}
	public String getPaymentId() {
		return paymentId;
	}
	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getUserOrgId() {
		return userOrgId;
	}
	public void setUserOrgId(String userOrgId) {
		this.userOrgId = userOrgId;
	}
	public String getProviderOrgId() {
		return providerOrgId;
	}
	public void setProviderOrgId(String providerOrgId) {
		this.providerOrgId = providerOrgId;
	}
	
	
}
