package com.nyc.hhs.model;

public class ActionBean {
	private String actionName = null;
	private int    actionInx = 0;
	private String actionStatus = null;
	
	ActionBean(String actionNm, int inx, String actionStat){
		this.actionName = actionNm;
		this.actionInx = inx;
		this.actionStatus = actionStat;
	}

	public String getActionName() {
		return actionName;
	}
	public void setActionName(String actionName) {
		this.actionName = actionName;
	}
	public int getActionInx() {
		return actionInx;
	}
	public void setActionInx(int actionInx) {
		this.actionInx = actionInx;
	}
	public String getActionStatus() {
		return actionStatus;
	}
	public void setActionStatus(String actionStatus) {
		this.actionStatus = actionStatus;
	}
			
	
}
