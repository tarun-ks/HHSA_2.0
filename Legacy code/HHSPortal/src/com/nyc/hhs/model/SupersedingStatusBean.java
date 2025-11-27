package com.nyc.hhs.model;

public class SupersedingStatusBean {
	private String entityType;	
	private String entityId;	
	private String event;
	private String flag;
	private String status;
	private String timestamp;
	private String userId;
	private String orgId;
	private String requestId;
	private String createdBy;
	private String modifiedBy;
	private String createdDate;
	private String modifiedDate;
	private String supersedingStatusId;
	private String reportingModifiedBy;
	private String reportingModifiedDate;
	
	public String getEntityType() {
		return entityType;
	}
	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getOrgId() {
		return orgId;
	}
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}
	public String getRequestId() {
		return requestId;
	}
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getModifiedBy() {
		return modifiedBy;
	}
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	public String getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getSupersedingStatusId() {
		return supersedingStatusId;
	}
	public void setSupersedingStatusId(String supersedingStatusId) {
		this.supersedingStatusId = supersedingStatusId;
	}
	public String getReportingModifiedBy() {
		return reportingModifiedBy;
	}
	public void setReportingModifiedBy(String reportingModifiedBy) {
		this.reportingModifiedBy = reportingModifiedBy;
	}
	public String getReportingModifiedDate() {
		return reportingModifiedDate;
	}
	public void setReportingModifiedDate(String reportingModifiedDate) {
		this.reportingModifiedDate = reportingModifiedDate;
	}
	@Override
	public String toString() {
		return "SupersedingStatusBean [entityType=" + entityType
				+ ", entityId=" + entityId + ", event=" + event + ", flag="
				+ flag + ", status=" + status + ", timestamp=" + timestamp
				+ ", userId=" + userId + ", orgId=" + orgId + ", requestId="
				+ requestId + ", createdBy=" + createdBy + ", modifiedBy="
				+ modifiedBy + ", createdDate=" + createdDate
				+ ", modifiedDate=" + modifiedDate + ", supersedingStatusId="
				+ supersedingStatusId + ", reportingModifiedBy="
				+ reportingModifiedBy + ", reportingModifiedDate="
				+ reportingModifiedDate + "]";
	}
}
