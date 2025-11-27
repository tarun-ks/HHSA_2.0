package com.nyc.hhs.model;

/**
 * This class is a bean which maintains the Contract Service Mapping information.
 *
 */

public class ContractServiceMapping {
	
	private String msContractId;
	private String msOrgId;
	private String msServiceId;
	private String msAppId;
	private String msContractAddedOn;
	
	public String getMsContractId() {
		return msContractId;
	}
	
	public void setMsContractId(String msContractId) {
		this.msContractId = msContractId;
	}
	
	public String getMsOrgId() {
		return msOrgId;
	}
	
	public void setMsOrgId(String msOrgId) {
		this.msOrgId = msOrgId;
	}
	
	public String getMsServiceId() {
		return msServiceId;
	}
	
	public void setMsServiceId(String msServiceId) {
		this.msServiceId = msServiceId;
	}
	
	public String getMsAppId() {
		return msAppId;
	}
	
	public void setMsAppId(String msAppId) {
		this.msAppId = msAppId;
	}
	
	public String getMsContractAddedOn() {
		return msContractAddedOn;
	}
	
	public void setMsContractAddedOn(String msContractAddedOn) {
		this.msContractAddedOn = msContractAddedOn;
	}

	@Override
	public String toString() {
		return "ContractServiceMapping [msContractId=" + msContractId
				+ ", msOrgId=" + msOrgId + ", msServiceId=" + msServiceId
				+ ", msAppId=" + msAppId + ", msContractAddedOn="
				+ msContractAddedOn + "]";
	}
	
}
