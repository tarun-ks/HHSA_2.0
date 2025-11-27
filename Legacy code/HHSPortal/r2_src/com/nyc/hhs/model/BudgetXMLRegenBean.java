package com.nyc.hhs.model;

import java.util.Calendar;
import java.util.Date;

import com.nyc.hhs.constants.HHSConstants;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;

public class BudgetXMLRegenBean extends BaseFilter {
	
	@RegExp(value ="^\\d{0,22}")
	private int      contractId ;
	@RegExp(value ="^\\d{0,22}")
	private int      budgetId ;
	private int      versionId      ;
	private int      contractTypeId ;
	private int      budgetTypeId   ;
	private String   prevXMLDocId = HHSConstants.EMPTY_STRING;
	private String   curXMLDocId = HHSConstants.EMPTY_STRING;
	private String   approvedUserId = HHSConstants.EMPTY_STRING;
	private Date     createdDate;
	private String   createdUserId = HHSConstants.EMPTY_STRING;
	private Date     modifiedDate ;
	private String   modifiedUserId = HHSConstants.EMPTY_STRING;
	
	private Date     budgetStartDate ;
	private Date     budgetEndDate ;

	private Date     contractStartDate ;
	private Date     contractEndDate ;

	private int      activeFlag = 0;

	public int getContractId() {
		return contractId;
	}
	public void setContractId(int contractId) {
		this.contractId = contractId;
	}
	public int getBudgetId() {
		return budgetId;
	}
	public void setBudgetId(int budgetId) {
		this.budgetId = budgetId;
	}
	public int getVersionId() {
		return versionId;
	}
	public void setVersionId(int versionId) {
		this.versionId = versionId;
	}
	public int getContractTypeId() {
		return contractTypeId;
	}
	public void setContractTypeId(int contractTypeId) {
		this.contractTypeId = contractTypeId;
	}
	public int getBudgetTypeId() {
		return budgetTypeId;
	}
	public void setBudgetTypeId(int budgetTypeId) {
		this.budgetTypeId = budgetTypeId;
	}
	public String getPrevXMLDocId() {
		return prevXMLDocId;
	}
	public void setPrevXMLDocId(String prevXMLDocId) {
		this.prevXMLDocId = prevXMLDocId;
	}
	public String getCurXMLDocId() {
		return curXMLDocId;
	}
	public void setCurXMLDocId(String curXMLDocId) {
		this.curXMLDocId = curXMLDocId;
	}
	public String getApprovedUserId() {
		return approvedUserId;
	}
	public void setApprovedUserId(String approvedUserId) {
		this.approvedUserId = approvedUserId;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getCreatedUserId() {
		return createdUserId;
	}
	public void setCreatedUserId(String createdUserId) {
		this.createdUserId = createdUserId;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getModifiedUserId() {
		return modifiedUserId;
	}
	public void setModifiedUserId(String modifiedUserId) {
		this.modifiedUserId = modifiedUserId;
	}

	public int getActiveFlag() {
		return activeFlag;
	}
	public void setActiveFlag(int activeFlag) {
		this.activeFlag = activeFlag;
	}
	public void setAvailable() {
		this.activeFlag = HHSConstants.INT_ONE;
	}

	public void resetActiveFlag() {
		this.activeFlag = HHSConstants.INT_ZERO;
	}

	public Date getBudgetStartDate() {
		return budgetStartDate;
	}
	public void setBudgetStartDate(Date budgetStartDate) {
		this.budgetStartDate = budgetStartDate;
	}
	public Date getBudgetEndDate() {
		return budgetEndDate;
	}
	public void setBudgetEndDate(Date budgetEndDate) {
		this.budgetEndDate = budgetEndDate;
	}

/*[Start] R8.5.0 QC9492   add attributes   */
	public Date getContractStartDate() {
		return contractStartDate;
	}
	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
	}
	public Date getContractEndDate() {
		return contractEndDate;
	}
	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = contractEndDate;
	}

	public Date getCorrectBudgetSrtDate(){
		FiscalDate budgetFY = new FiscalDate(budgetStartDate);
		FiscalDate contractSrtFY = new FiscalDate(contractStartDate);

		if( contractSrtFY.getFiscalYear() == budgetFY.getFiscalYear() ){
			return contractStartDate;
		}else if( contractSrtFY.getFiscalYear() < budgetFY.getFiscalYear() ) {
			return budgetStartDate;
		}else{
			return budgetStartDate;
		}
	}

	public Date getCorrectBudgetEndDate(){
		FiscalDate budgetEndFY = new FiscalDate(budgetEndDate);
		//FiscalDate budgetFY;

		FiscalDate contractEndFY = new FiscalDate(contractEndDate);

		if( contractEndFY.getFiscalYear() == budgetEndFY.getFiscalYear() ){
			return contractEndDate;
		}else if( contractEndFY.getFiscalYear() > budgetEndFY.getFiscalYear() ) {
			FiscalDate fyDate = new FiscalDate(budgetEndFY.getCalendarYear(), Calendar.JULY-1, 30 );
			return fyDate.getDate();
		}else{
			return budgetEndDate;
		}
	}

/*[End] R8.5.0 QC9492      */

	@Override
	public String toString() {
		return "BudgetXMLRegenBean [contractId=" + contractId + ", budgetId="
				+ budgetId + ", versionId=" + versionId + ", contractTypeId="
				+ contractTypeId + ", budgetTypeId=" + budgetTypeId
				+ ", prevXMLDocId=" + prevXMLDocId + ", curXMLDocId="
				+ curXMLDocId + ", approvedUserId=" + approvedUserId
				+ ", createdDate=" + createdDate + ", createdUserId="
				+ createdUserId + ", modifiedDate=" + modifiedDate
				+ ", modifiedUserId=" + modifiedUserId + ", budgetStartDate="
				+ budgetStartDate + ", budgetEndDate=" + budgetEndDate
				+ ", contractStartDate=" + contractStartDate
				+ ", contractEndDate=" + contractEndDate + ", activeFlag="
				+ activeFlag + "]";
	}
	

	

	
}


