package com.nyc.hhs.model;

/**
 * This class is a bean which maintains the Task details.
 *
 */

public class Task {
	public String msProviderName="";
	public String msSubmittedby="";
	public String msPhone="";
	public String msEmailAdd="";
	public String msDateSubmitted="";
	public String msDateLastModified;
	public String msCurrentProvStatus="";
	public String msTaskName;
	public String msAssignedTo;
	public String msReassignTask;
	public String msDateAssigned;
	public String msLastModified;
	public String msProcessStatus;
	public boolean moTaskHistory;
	public int miNoOfServices;
	public String msBusinessAppSatus="";
	
	public String getMsDateLastModified() {
		return msDateLastModified;
	}
	public void setMsDateLastModified(String msDateLastModified) {
		this.msDateLastModified = msDateLastModified;
	}
	
	public String getMsBusinessAppSatus() {
		return msBusinessAppSatus;
	}
	public void setMsBusinessAppSatus(String msBusinessAppSatus) {
		if(msBusinessAppSatus==null)
		{
			msBusinessAppSatus="";
		}
		this.msBusinessAppSatus = msBusinessAppSatus;
	}
	public String getMsProcessStatus()
	{
		return msProcessStatus;
	}
	public void setMsProcessStatus(String msProcessStatus)
	{
	
		this.msProcessStatus = msProcessStatus;
	}
	
	public int getNoOfServices() {
		return miNoOfServices;
	}
	public void setNoOfServices(int aiNoOfServices) {
		
		this.miNoOfServices = aiNoOfServices;
	}
	public String getProviderName() {
		return msProviderName;
	}
	public void setProviderName(String asProviderName) {
		if(asProviderName==null)
		{
			asProviderName="";
		}
		this.msProviderName = asProviderName;
	}
	public String getSubmittedby() {
		return msSubmittedby;
	}
	public void setSubmittedby(String asSubmittedby) {
		if(asSubmittedby==null)
		{
			asSubmittedby="";
		}
		this.msSubmittedby = asSubmittedby;
	}
	public String getPhone() {
		return msPhone;
	}
	public void setPhone(String asPhone) {
		if(asPhone==null)
		{
			asPhone="";
		}
		this.msPhone = asPhone;
	}
	public String getEmailAdd() {
		return msEmailAdd;
	}
	public void setEmailAdd(String asEmailAdd) {
		
		if(asEmailAdd==null)
		{
			asEmailAdd="";
		}
			
		this.msEmailAdd = asEmailAdd;
	}
	public String getDateSubmitted() {
		return msDateSubmitted;
	}
	public void setDateSubmitted(String aoDateSubmitted) {
		if(aoDateSubmitted==null)
		{
			aoDateSubmitted="";
		}
		this.msDateSubmitted = aoDateSubmitted;
	}
	public String getCurrentProvStatus() {
		return msCurrentProvStatus;
	}
	public void setCurrentProvStatus(String asCurrentProvStatus) {
		
		this.msCurrentProvStatus = asCurrentProvStatus;
	}
	public String getTaskName() {
		return msTaskName;
	}
	public void setTaskName(String asTaskName) {
		if(asTaskName==null)
		{
			asTaskName="";
		}
		this.msTaskName = asTaskName;
	}
	public String getAssignedTo() {
		return msAssignedTo;
	}
	public void setAssignedTo(String asAssignedTo) {
		if(asAssignedTo==null)
		{
			asAssignedTo="";
		}
		this.msAssignedTo = asAssignedTo;
	}
	public String getReassignTask() {
		return msReassignTask;
	}
	public void setReassignTask(String asReassignTask) {
		this.msReassignTask = asReassignTask;
	}
	public String getDateAssigned() {
		return msDateAssigned;
	}
	public void setDateAssigned(String aoDateAssigned) {
		if(aoDateAssigned==null)
		{
			aoDateAssigned="";
		}
		this.msDateAssigned = aoDateAssigned;
	}
	public String getLastModified() {
		return msLastModified;
	}
	public void setLastModified(String aoLastModified) {
		if(aoLastModified==null)
		{
			aoLastModified="";
		}
		this.msLastModified = aoLastModified;
	}
	public boolean isTaskHistory() {
		return moTaskHistory;
	}
	public void setTaskHistory(boolean aoTaskHistory) {
		this.moTaskHistory = aoTaskHistory;
	}
	@Override
	public String toString() {
		return "Task [msProviderName=" + msProviderName + ", msSubmittedby="
				+ msSubmittedby + ", msPhone=" + msPhone + ", msEmailAdd="
				+ msEmailAdd + ", msDateSubmitted=" + msDateSubmitted
				+ ", msDateLastModified=" + msDateLastModified
				+ ", msCurrentProvStatus=" + msCurrentProvStatus
				+ ", msTaskName=" + msTaskName + ", msAssignedTo="
				+ msAssignedTo + ", msReassignTask=" + msReassignTask
				+ ", msDateAssigned=" + msDateAssigned + ", msLastModified="
				+ msLastModified + ", msProcessStatus=" + msProcessStatus
				+ ", moTaskHistory=" + moTaskHistory + ", miNoOfServices="
				+ miNoOfServices + ", msBusinessAppSatus=" + msBusinessAppSatus
				+ "]";
	}

}
