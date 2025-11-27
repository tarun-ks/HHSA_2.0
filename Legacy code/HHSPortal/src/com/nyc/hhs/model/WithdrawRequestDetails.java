package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;
/**
 * This class is a bean which maintains the Withdrawal request details.
 *
 */

public class WithdrawRequestDetails {

	@Length(max = 20)
	private String msAppId="";
	private String msProviderName = ""; 
	private String msBAwithdrawlId = "";
	@Length(max = 20)
	private String msBusinessAppId="";
	private String msProviderStatus="";
	
	
	public String getMsAppId() {
		return msAppId;
	}
	public void setMsAppId(String msAppId) {
		this.msAppId = msAppId;
	}
	public String getMsProviderName() {
		return msProviderName;
	}
	public void setMsProviderName(String msProviderName) {
		this.msProviderName = msProviderName;
	}
	public String getMsBAwithdrawlId() {
		return msBAwithdrawlId;
	}
	public void setMsBAwithdrawlId(String msBAwithdrawlId) {
		this.msBAwithdrawlId = msBAwithdrawlId;
	}
	public String getMsBusinessAppId() {
		return msBusinessAppId;
	}
	public void setMsBusinessAppId(String msBusinessAppId) {
		this.msBusinessAppId = msBusinessAppId;
	}
	public String getMsProviderStatus() {
		return msProviderStatus;
	}
	public void setMsProviderStatus(String msProviderStatus) {
		this.msProviderStatus = msProviderStatus;
	}
	@Override
	public String toString() {
		return "WithdrawRequestDetails [msAppId=" + msAppId
				+ ", msProviderName=" + msProviderName + ", msBAwithdrawlId="
				+ msBAwithdrawlId + ", msBusinessAppId=" + msBusinessAppId
				+ ", msProviderStatus=" + msProviderStatus + "]";
	}
	
	
	
}
