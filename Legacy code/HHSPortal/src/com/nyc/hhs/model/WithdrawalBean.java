package com.nyc.hhs.model;

import java.util.ArrayList;
import java.util.Date;
import org.springmodules.validation.bean.conf.loader.annotation.handler.RegExp;
import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This class is a bean which maintains the Withdrawal information. 
 *
 */

public class WithdrawalBean {

	@Length(max = 50)
	private String msWorkFlowId;
	@Length(max = 20)
	private String msServiceApplicationId;
	private String msComments;
	private ArrayList<String> msStatusList;
	private String msPWOBNumber;
	@RegExp(value ="^\\d{0,22}")
	private String msWithDrawalReqId;
	private String msBusinessApplicationId;
	private String msStatus;
	private String msWithdrawStatus;
	private boolean mbToBeTerminate;
	private String msApprovedBy;
	private Date moApprovedDate;
	
		
	public String getMsApprovedBy() {
		return msApprovedBy;
	}
	public void setMsApprovedBy(String msApprovedBy) {
		this.msApprovedBy = msApprovedBy;
	}
	public Date getMoApprovedDate() {
		return moApprovedDate;
	}
	public void setMoApprovedDate(Date moApprovedDate) {
		this.moApprovedDate = moApprovedDate;
	}
	public boolean isMbToBeTerminate() {
		return mbToBeTerminate;
	}
	public void setMbToBeTerminate(boolean mbToBeTerminate) {
		this.mbToBeTerminate = mbToBeTerminate;
	}
	
	public String getMsWithdrawStatus() {
		return msWithdrawStatus;
	}
	
	public void setMsWithdrawStatus(String msWithdrawStatus) {
		this.msWithdrawStatus = msWithdrawStatus;
	}
	
	public String getMsStatus() {
		return msStatus;
	}
	
	public void setMsStatus(String msStatus) {
		this.msStatus = msStatus;
	}
	
	public String getMsBusinessApplicationId() {
		return msBusinessApplicationId;
	}
	
	public void setMsBusinessApplicationId(String msBusinessApplicationId) {
		this.msBusinessApplicationId = msBusinessApplicationId;
	}
	public ArrayList<String> getMsStatusList() {
		return msStatusList;
	}
	public void setMsStatusList(ArrayList<String> msStatusList) {
		this.msStatusList = msStatusList;
	}
	
	public String getMsWorkFlowId() {
		return msWorkFlowId;
	}

	public void setMsWorkFlowId(String msWorkFlowId) {
		this.msWorkFlowId = msWorkFlowId;
	}

	public String getMsServiceApplicationId() {
		return msServiceApplicationId;
	}
	
	public void setMsServiceApplicationId(String msServiceApplicationId) {
		this.msServiceApplicationId = msServiceApplicationId;
	}
	
	public String getMsComments() {
		return msComments;
	}
	
	public void setMsComments(String msComments) {
		this.msComments = msComments;
	}
	
	public String getMsPWOBNumber() {
		return msPWOBNumber;
	}
	
	public void setMsPWOBNumber(String msPWOBNumber) {
		this.msPWOBNumber = msPWOBNumber;
	}

	public String getMsWithDrawalReqId() {
		return msWithDrawalReqId;
	}
	
	public void setMsWithDrawalReqId(String msWithDrawalReqId) {
		this.msWithDrawalReqId = msWithDrawalReqId;
	}
	@Override
	public String toString() {
		return "WithdrawalBean [msWorkFlowId=" + msWorkFlowId
				+ ", msServiceApplicationId=" + msServiceApplicationId
				+ ", msComments=" + msComments + ", msStatusList="
				+ msStatusList + ", msPWOBNumber=" + msPWOBNumber
				+ ", msWithDrawalReqId=" + msWithDrawalReqId
				+ ", msBusinessApplicationId=" + msBusinessApplicationId
				+ ", msStatus=" + msStatus + ", msWithdrawStatus="
				+ msWithdrawStatus + ", mbToBeTerminate=" + mbToBeTerminate
				+ ", msApprovedBy=" + msApprovedBy + ", moApprovedDate="
				+ moApprovedDate + "]";
	}

	
}
