package com.nyc.hhs.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;

import com.nyc.hhs.constants.HHSR5Constants;

public class ActionStatusBean {
	
	public static final String ACTION_DROPDOWN_ENABLE_VALUE   = "1";
	public static final String ACTION_DROPDOWN_DISABLE_VALUE   = "0";

	@Length(max = 20) 
	private String agencyId;
	@Length(max = 20) 
	private String providerId;
	@Length(max = 1) 
	private String submitInvoice;
	@Length(max = 1)
	private String budgetMod;
	@Length(max = 1)
	private String cancelMod;
	@Length(max = 1)
    private String requestAdvance;
	@Length(max = 1)
	private String updateBudgetTemp;
	@Length(max = 1)
	private String initiateAdvance;
	@Length(max = 1)
	private String updateContractConf;
	@Length(max = 1)
	private String amendContract;
	@Length(max = 1)
	private String suspendContract;
	@Length(max = 1)
	private String closeContract;
	@Length(max = 1)
	private String flagContract;
	@Length(max = 1)
	private String cancelAmendment;
	@Length(max = 1)
	private String cancelContract;
	@Length(max = 1)
	private String deleteContract;
	@Length(max = 1)
	private String updateContractinfo;
	@Length(max = 1)
	private String newFiscalYear;
	@Length(max = 1)
	private String deleteInvoice;
	@Length(max = 1)
	private String withdrawInvoice;
	@Length(max = 1)
	private String markAsRegistered;
	@Length(max = 1)
	private String returnPayment;
	@Length(max = 1)
	private String returnPaymentCancel;
	@Length(max = 1)
	private String downloadForRegistration;
	@Length(max = 1)
	private String cancelAndMerge;

	private String createdByUserid;
	private String createdDate;
	private String modifiedByUserid;
	private String modifiedDate;

	/*[Start] QC9720*/
	private String notice;
	public String getNotice() {
		return notice;
	}
	public void setNotice(String notice) {
		this.notice = notice;
	}
	/*[End] QC9720*/
	
	public String getCancelAndMerge() {
		return cancelAndMerge;
	}
	public void setCancelAndMerge(String cancelAndMerge) {
		this.cancelAndMerge = cancelAndMerge;
	}
	
	public String getProviderId() {
		return providerId;
	}
	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	public String getAgencyId() {
		return agencyId;
	}
	public void setAgencyId(String agencyId) {
		this.agencyId = agencyId;
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
	public String getUpdateBudgetTemp() {
		return updateBudgetTemp;
	}
	public void setUpdateBudgetTemp(String updateBudgetTemp) {
		this.updateBudgetTemp = updateBudgetTemp;
	}
	public String getInitiateAdvance() {
		return initiateAdvance;
	}
	public void setInitiateAdvance(String initiateAdvance) {
		this.initiateAdvance = initiateAdvance;
	}
	public String getUpdateContractConf() {
		return updateContractConf;
	}
	public void setUpdateContractConf(String updateContractConf) {
		this.updateContractConf = updateContractConf;
	}
	public String getAmendContract() {
		return amendContract;
	}
	public void setAmendContract(String amendContract) {
		this.amendContract = amendContract;
	}
	public String getSuspendContract() {
		return suspendContract;
	}
	public void setSuspendContract(String suspendContract) {
		this.suspendContract = suspendContract;
	}
	public String getCloseContract() {
		return closeContract;
	}
	public void setCloseContract(String closeContract) {
		this.closeContract = closeContract;
	}
	public String getFlagContract() {
		return flagContract;
	}
	public void setFlagContract(String flagContract) {
		this.flagContract = flagContract;
	}
	public String getCancelAmendment() {
		return cancelAmendment;
	}
	public void setCancelAmendment(String cancelAmendment) {
		this.cancelAmendment = cancelAmendment;
	}
	public String getCancelContract() {
		return cancelContract;
	}
	public void setCancelContract(String cancelContract) {
		this.cancelContract = cancelContract;
	}
	public String getDeleteContract() {
		return deleteContract;
	}
	public void setDeleteContract(String deleteContract) {
		this.deleteContract = deleteContract;
	}
	public String getUpdateContractinfo() {
		return updateContractinfo;
	}
	public void setUpdateContractinfo(String updateContractinfo) {
		this.updateContractinfo = updateContractinfo;
	}
	public String getNewFiscalYear() {
		return newFiscalYear;
	}
	public void setNewFiscalYear(String newFiscalYear) {
		this.newFiscalYear = newFiscalYear;
	}
	public String getCreatedByUserid() {
		return createdByUserid;
	}
	public void setCreatedByUserid(String createdByUserid) {
		this.createdByUserid = createdByUserid;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getModifiedByUserid() {
		return modifiedByUserid;
	}
	public void setModifiedByUserid(String modifiedByUserid) {
		this.modifiedByUserid = modifiedByUserid;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
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

	public String getMarkAsRegistered() {
		return markAsRegistered;
	}
	public void setMarkAsRegistered(String markAsRegistered) {
		this.markAsRegistered = markAsRegistered;
	}


	public String getReturnPayment() {
		return returnPayment;
	}
	public void setReturnPayment(String returnPayment) {
		this.returnPayment = returnPayment;
	}
	public String getReturnPaymentCancel() {
		return returnPaymentCancel;
	}
	public void setReturnPaymentCancel(String returnPaymentCancel) {
		this.returnPaymentCancel = returnPaymentCancel;
	}
	
	public String getDownloadForRegistration() {
		return downloadForRegistration;
	}
	public void setDownloadForRegistration(String downloadForRegistration) {
		this.downloadForRegistration = downloadForRegistration;
	}

	public Map<Integer,String> toMap(){
		Map<Integer,String> map = new HashMap<Integer,String>();

		map.put(HHSR5Constants.ACTION_DROPDOWN_AGENCY_ID_INX                   , agencyId);  
		map.put(HHSR5Constants.ACTION_DROPDOWN_SUBMIT_INVOICE_INX              , submitInvoice);  
		map.put(HHSR5Constants.ACTION_DROPDOWN_BUDGET_MOD_INX                  , budgetMod);  
		map.put(HHSR5Constants.ACTION_DROPDOWN_CANCEL_MOD_INX                  , cancelMod);  
		map.put(HHSR5Constants.ACTION_DROPDOWN_REQUEST_ADVANCE_INX             , requestAdvance);  
		map.put(HHSR5Constants.ACTION_DROPDOWN_UPDATE_BUDGET_TEMP_INX          , updateBudgetTemp);  
		map.put(HHSR5Constants.ACTION_DROPDOWN_INITIATE_ADVANCE_INX            , initiateAdvance);  
		map.put(HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACT_CONF_INX        , updateContractConf);   
		map.put(HHSR5Constants.ACTION_DROPDOWN_AMEND_CONTRACT_INX              , amendContract);  
		map.put(HHSR5Constants.ACTION_DROPDOWN_SUSPEND_CONTRACT_INX            , suspendContract);  
		map.put(HHSR5Constants.ACTION_DROPDOWN_CLOSE_CONTRACT_INX              , closeContract);  
		map.put(HHSR5Constants.ACTION_DROPDOWN_FLAG_CONTRACT_INX               , flagContract); 
		map.put(HHSR5Constants.ACTION_DROPDOWN_CANCEL_AMENDMENT_INX            , cancelAmendment);  
		map.put(HHSR5Constants.ACTION_DROPDOWN_CANCEL_CONTRACT_INX             , cancelContract);  
		map.put(HHSR5Constants.ACTION_DROPDOWN_DELETE_CONTRACT_INX             , deleteContract); 
		map.put(HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACTINFO_INX         , updateContractinfo); 
		map.put(HHSR5Constants.ACTION_DROPDOWN_NEW_FISCAL_YEAR_INX             , newFiscalYear); 
		map.put(HHSR5Constants.ACTION_DROPDOWN_DELETE_INVOICE_INX              , deleteInvoice); 
		map.put(HHSR5Constants.ACTION_DROPDOWN_WITHDRAW_INVOICE_INX            , withdrawInvoice); 

		map.put(HHSR5Constants.ACTION_DROPDOWN_MARK_AS_REGISTERED_INX          , markAsRegistered); 
		map.put(HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_INX              , returnPayment); 
		map.put(HHSR5Constants.ACTION_DROPDOWN_CANCEL_RETURN_PAYMENT_INX       , returnPaymentCancel); 
		map.put(HHSR5Constants.ACTION_DROPDOWN_DOWNLOAD_FOR_REGISTRATION_INX   , downloadForRegistration); 
		map.put(HHSR5Constants.ACTION_DROPDOWN_CANCEL_MERGE_INX   				, cancelAndMerge); 
		

		return map;
	}

	
	public String toString() {
		StringBuffer loSB = new StringBuffer();

		loSB.append(" [AGENCY_ID                ]:" +  agencyId);  
		loSB.append(" [SUBMIT_INVOICE           ]:" +  submitInvoice);  
		loSB.append(" [BUDGET_MOD               ]:" +  budgetMod);  
		loSB.append(" [CANCEL_MOD               ]:" +  cancelMod);  
		loSB.append(" [REQUEST_ADVANCE          ]:" +  requestAdvance);  
		loSB.append(" [UPDATE_BUDGET_TEMP       ]:" +  updateBudgetTemp);  
		loSB.append(" [INITIATE_ADVANCE         ]:" +  initiateAdvance);  
		loSB.append(" [UPDATE_CONTRACT_CONF     ]:" +  updateContractConf);   
		loSB.append(" [AMEND_CONTRACT           ]:" +  amendContract);  
		loSB.append(" [SUSPEND_CONTRACT         ]:" +  suspendContract);  
		loSB.append(" [CLOSE_CONTRACT           ]:" +  closeContract);  
		loSB.append(" [FLAG_CONTRACT            ]:" +  flagContract); 
		loSB.append(" [CANCEL_AMENDMENT         ]:" +  cancelAmendment);  
		loSB.append(" [CANCEL_CONTRACT          ]:" +  cancelContract);  
		loSB.append(" [DELETE_CONTRACT          ]:" +  deleteContract); 
		loSB.append(" [UPDATE_CONTRACTINFO      ]:" +  updateContractinfo); 
		loSB.append(" [NEW_FISCAL_YEAR          ]:" +  newFiscalYear); 
		loSB.append(" [DELETE_INVOICE           ]:" +  deleteInvoice); 
		loSB.append(" [WITHDRAW_INVOICE         ]:" +  withdrawInvoice); 
		loSB.append(" [MARK_AS_REGISTERED       ]:" +  markAsRegistered); 
		loSB.append(" [RETURN_PAYMENT           ]:" +  returnPayment); 
		loSB.append(" [RETURN_PAYMENT_CANCEL    ]:" +  returnPaymentCancel); 
		loSB.append(" [DOWNLOAD_FOR_REGISTRATION]:" +  downloadForRegistration); 
		loSB.append(" [CANCEL_AND_MERGE]:" 			+  cancelAndMerge); 
		
		return loSB.toString();
	}


	public List<ActionBean> toList() {
		ArrayList<ActionBean> loLst = new ArrayList<ActionBean> ();
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_SUBMIT_INVOICE_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_SUBMIT_INVOICE_INX, submitInvoice ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_BUDGET_MOD_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_BUDGET_MOD_INX, budgetMod ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_CANCEL_MOD_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_CANCEL_MOD_INX, cancelMod ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_REQUEST_ADVANCE_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_REQUEST_ADVANCE_INX, requestAdvance ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_UPDATE_BUDGET_TEMP_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_UPDATE_BUDGET_TEMP_INX, updateBudgetTemp ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_INITIATE_ADVANCE_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_INITIATE_ADVANCE_INX, initiateAdvance ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACT_CONF_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACT_CONF_INX, updateContractConf ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_AMEND_CONTRACT_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_AMEND_CONTRACT_INX, amendContract ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_SUSPEND_CONTRACT_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_SUSPEND_CONTRACT_INX, suspendContract ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_CLOSE_CONTRACT_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_CLOSE_CONTRACT_INX, closeContract ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_FLAG_CONTRACT_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_FLAG_CONTRACT_INX, flagContract ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_CANCEL_AMENDMENT_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_CANCEL_AMENDMENT_INX, cancelAmendment ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_CANCEL_CONTRACT_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_CANCEL_CONTRACT_INX, cancelContract ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_DELETE_CONTRACT_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_DELETE_CONTRACT_INX, deleteContract ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACTINFO_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_UPDATE_CONTRACTINFO_INX, updateContractinfo ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_NEW_FISCAL_YEAR_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_NEW_FISCAL_YEAR_INX, newFiscalYear ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_DELETE_INVOICE_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_DELETE_INVOICE_INX, deleteInvoice ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_WITHDRAW_INVOICE_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_WITHDRAW_INVOICE_INX, withdrawInvoice ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_MARK_AS_REGISTERED_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_MARK_AS_REGISTERED_INX, markAsRegistered ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_RETURN_PAYMENT_INX, returnPayment ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_CANCEL_RETURN_PAYMENT_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_CANCEL_RETURN_PAYMENT_INX, returnPaymentCancel ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_DOWNLOAD_FOR_REGISTRATION_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_DOWNLOAD_FOR_REGISTRATION_INX, downloadForRegistration ));
		loLst.add(new ActionBean(HHSR5Constants.ACTION_DROPDOWN_CANCEL_MERGE_SCREEN_NAME, HHSR5Constants.ACTION_DROPDOWN_CANCEL_MERGE_INX, cancelAndMerge ));

		return loLst;
	}

	public String toStringBi() {
		StringBuffer loSB = new StringBuffer();

		loSB.append(submitInvoice );   
		loSB.append("|" +  budgetMod); 
		loSB.append("|" +  cancelMod);  
		loSB.append("|" +  requestAdvance);  
		loSB.append("|" +  updateBudgetTemp); 
		loSB.append("|" +  initiateAdvance);  
		loSB.append("|" +  updateContractConf);  
		loSB.append("|" +  amendContract);  
		loSB.append("|" +  suspendContract);  
		loSB.append("|" +  closeContract);  
		loSB.append("|" +  flagContract); 
		loSB.append("|" +  cancelAmendment);  
		loSB.append("|" +  cancelContract);  
		loSB.append("|" +  deleteContract); 
		loSB.append("|" +  updateContractinfo); 
		loSB.append("|" +  newFiscalYear); 
		loSB.append("|" +  deleteInvoice); 
		loSB.append("|" +  withdrawInvoice); 
		loSB.append("|" +  markAsRegistered); 
		loSB.append("|" +  returnPayment);
		loSB.append("|" +  returnPaymentCancel);
		loSB.append("|" +  downloadForRegistration);
		loSB.append("|" +  cancelAndMerge);

		return loSB.toString();
	}


	public void reset() {
		this.submitInvoice  = "0";   
		this.budgetMod  = "0"; 
		this.cancelMod  = "0";  
		this.requestAdvance  = "0";
		this.updateBudgetTemp  = "0";
		this.initiateAdvance  = "0";  
		this.updateContractConf  = "0";  
		this.amendContract  = "0";  
		this.suspendContract  = "0";  
		this.closeContract  = "0";  
		this.flagContract  = "0"; 
		this.cancelAmendment  = "0";  
		this.cancelContract  = "0";  
		this.deleteContract  = "0"; 
		this.updateContractinfo  = "0"; 
		this.newFiscalYear  = "0"; 
		this.deleteInvoice  = "0"; 
		this.withdrawInvoice  = "0"; 
		this.markAsRegistered  = "0"; 
		this.returnPayment  = "0"; 
		this.returnPaymentCancel = "0";
		this.downloadForRegistration = "0";
		this.cancelAndMerge = "0";
	}
	
	public void set() {
		this.submitInvoice  = "1";   
		this.budgetMod  = "1"; 
		this.cancelMod  = "1";  
		this.requestAdvance  = "1";
		this.updateBudgetTemp  = "1";
		this.initiateAdvance  = "1";  
		this.updateContractConf  = "1";  
		this.amendContract  = "1";  
		this.suspendContract  = "1";  
		this.closeContract  = "1";  
		this.flagContract  = "1"; 
		this.cancelAmendment  = "1";  
		this.cancelContract  = "1";  
		this.deleteContract  = "1"; 
		this.updateContractinfo  = "1"; 
		this.newFiscalYear  = "1"; 
		this.deleteInvoice  = "1"; 
		this.withdrawInvoice  = "1"; 
		this.markAsRegistered  = "1"; 
		this.returnPayment  = "1";
		this.returnPaymentCancel = "1";
		this.downloadForRegistration = "1";
		this.cancelAndMerge = "1";

	}
	


}



