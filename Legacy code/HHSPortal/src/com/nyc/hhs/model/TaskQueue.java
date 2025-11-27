package com.nyc.hhs.model;

import java.util.HashMap;

/**
 * This class is a bean which maintains the Task Queue information.
 *
 */

public class TaskQueue {
	
public String msTaskName;
public String msProviderName;
public String moDateCreated;
public String moLastAssigned;
public String msStatus;
public String msWobNumber;
public HashMap msNoOfTask;
public int miTaskCounter;
public String msAssignedTo;
public String msApplicationId;
public String msProcurementTitle; // Added for QC5446

public boolean mbIsManagerRevStep;
public boolean mbManagerRole;
public boolean mbIsTaskLocked;
public boolean mbIsManagerReviewStep;

//Added in release 7 
private String msProviderId;
private String msBaStatus;




public String getMsProviderId() {
	return msProviderId;
}
public void setMsProviderId(String msProviderId) {
	this.msProviderId = msProviderId;
}
public String getMsBaStatus() {
	return msBaStatus;
}
public void setMsBaStatus(String msBaStatus) {
	this.msBaStatus = msBaStatus;
}
public String getMsApplicationId() {
	return msApplicationId;
}
public void setMsApplicationId(String msApplicationId) {
	this.msApplicationId = msApplicationId;
}

public boolean isMbIsManagerRevStep() {
	return mbIsManagerRevStep;
}
public void setMbIsManagerRevStep(boolean mbIsManagerRevStep) {
	this.mbIsManagerRevStep = mbIsManagerRevStep;
}
public boolean isMbManagerRole() {
	return mbManagerRole;
}
public void setMbManagerRole(boolean mbManagerRole) {
	this.mbManagerRole = mbManagerRole;
}
public boolean isMbIsTaskLocked()
{
	return mbIsTaskLocked;
}
public void setMbIsTaskLocked(boolean mbIsTaskLocked)
{
	this.mbIsTaskLocked = mbIsTaskLocked;
}
public boolean isMbIsManagerReviewStep()
{
	return mbIsManagerReviewStep;
}
public void setMbIsManagerReviewStep(boolean mbIsManagerReviewStep)
{
	this.mbIsManagerReviewStep = mbIsManagerReviewStep;
}
public String getMsAssignedTo() {
	return msAssignedTo;
}
public void setMsAssignedTo(String msAssignedTo) {
	this.msAssignedTo = msAssignedTo;
}
public String getMsTaskName() {
	return msTaskName;
}
public void setMsTaskName(String msTaskName) {
	this.msTaskName = msTaskName;
}
public String getMsProviderName() {
	return msProviderName;
}
public void setMsProviderName(String msProviderName) {
	this.msProviderName = msProviderName;
}
public String getMoDateCreated() {
	return moDateCreated;
}
public void setMoDateCreated(String moDateCreated) {
	this.moDateCreated = moDateCreated;
}
public String getMoLastAssigned() {
	return moLastAssigned;
}
public void setMoLastAssigned(String moLastAssigned) {
	this.moLastAssigned = moLastAssigned;
}
public String getMsStatus() {
	return msStatus;
}
public void setMsStatus(String msStatus) {
	this.msStatus = msStatus;
}
public String getMsProcurementTitle() {
	return msProcurementTitle;
}
public void setMsProcurementTitle(String msProcurementTitle) {
	this.msProcurementTitle = msProcurementTitle;
}
public String getMsWobNumber() {
	return msWobNumber;
}
public void setMsWobNumber(String msWobNumber) {
	this.msWobNumber = msWobNumber;
}
public HashMap getMsNoOfTask() {
	return msNoOfTask;
}
public void setMsNoOfTask(HashMap msNoOfTask) {
	this.msNoOfTask = msNoOfTask;
}

public int getMiTaskCounter() {
	return miTaskCounter;
}
public void setMiTaskCounter(int miTaskCounter) {
	this.miTaskCounter = miTaskCounter;
}
@Override
public String toString() {
	return "TaskQueue [msTaskName=" + msTaskName 
			+ ", msProviderName=" + msProviderName 
			+ ", msProcurementTitle=" + msProcurementTitle 
			+ ", moDateCreated=" + moDateCreated
			+ ", moLastAssigned=" + moLastAssigned 
			+ ", msStatus=" + msStatus
			+ ", msWobNumber=" + msWobNumber 
			+ ", msNoOfTask=" + msNoOfTask
			+ ", miTaskCounter=" + miTaskCounter 
			+ ", msAssignedTo=" + msAssignedTo 
			+ ", msApplicationId=" + msApplicationId
			+ ", mbIsManagerRevStep=" + mbIsManagerRevStep 
			+ ", mbManagerRole="	+ mbManagerRole 
			+ ", msBaStatus=" + msBaStatus 
			+ ", msPrividerId=" + msProviderId 
			+ ", mbIsTaskLocked=" + mbIsTaskLocked
			+ ", mbIsManagerReviewStep=" + mbIsManagerReviewStep + "]";
}

}
