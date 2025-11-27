package com.nyc.hhs.model;

import java.util.List;

/**
 * This class is a bean which maintains the Work Item Inbox details.
 *
 */

public class WorkItemInbox {

	List<String> taskTypeList;
	List<String> statusList;
	
	public List<String> getTaskTypeList() {
		return taskTypeList;
	}
	
	public void setTaskTypeList(List<String> taskTypeList) {
		this.taskTypeList = taskTypeList;
	}
	
	public List<String> getStatusList() {
		return statusList;
	}
	
	public void setStatusList(List<String> statusList) {
		this.statusList = statusList;
	}

	@Override
	public String toString() {
		return "WorkItemInbox [taskTypeList=" + taskTypeList + ", statusList="
				+ statusList + "]";
	}
	
}
