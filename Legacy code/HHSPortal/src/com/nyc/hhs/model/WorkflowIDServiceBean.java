package com.nyc.hhs.model;

import org.springmodules.validation.bean.conf.loader.annotation.handler.Length;


/**
 * This class is a bean which maintains the Work-flow Id information.
 *
 */

public class WorkflowIDServiceBean {

	@Length(max = 50)
	private String msWorkFlowId;
	@Length(max = 20)
	private String msServiceApplicationId;
	
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

	@Override
	public String toString() {
		return "WorkflowIDServiceBean [msWorkFlowId=" + msWorkFlowId
				+ ", msServiceApplicationId=" + msServiceApplicationId + "]";
	}
	
}
