package com.hhsa.contract.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Contract DTO for API responses
 */
public class ContractDTO {

    private Long id;
    private String contractNumber;
    private String contractTitle;
    private BigDecimal contractValue;
    private BigDecimal contractAmount;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private Integer statusId;
    private String statusName; // Derived from statusId
    private String agencyId;
    private String programId;
    private String providerId;
    private String organizationId;
    private String ePin;
    private String registrationFlag;
    private String procurementId;
    private Long parentContractId;
    private Long contractTypeId;
    private String configurationWorkflowInstanceKey; // WF302 process instance key
    private String cofWorkflowInstanceKey; // WF303 process instance key
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContractTitle() {
        return contractTitle;
    }

    public void setContractTitle(String contractTitle) {
        this.contractTitle = contractTitle;
    }

    public BigDecimal getContractValue() {
        return contractValue;
    }

    public void setContractValue(BigDecimal contractValue) {
        this.contractValue = contractValue;
    }

    public BigDecimal getContractAmount() {
        return contractAmount;
    }

    public void setContractAmount(BigDecimal contractAmount) {
        this.contractAmount = contractAmount;
    }

    public LocalDate getContractStartDate() {
        return contractStartDate;
    }

    public void setContractStartDate(LocalDate contractStartDate) {
        this.contractStartDate = contractStartDate;
    }

    public LocalDate getContractEndDate() {
        return contractEndDate;
    }

    public void setContractEndDate(LocalDate contractEndDate) {
        this.contractEndDate = contractEndDate;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(String agencyId) {
        this.agencyId = agencyId;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getEPin() {
        return ePin;
    }

    public void setEPin(String ePin) {
        this.ePin = ePin;
    }

    public String getRegistrationFlag() {
        return registrationFlag;
    }

    public void setRegistrationFlag(String registrationFlag) {
        this.registrationFlag = registrationFlag;
    }

    public String getProcurementId() {
        return procurementId;
    }

    public void setProcurementId(String procurementId) {
        this.procurementId = procurementId;
    }

    public Long getParentContractId() {
        return parentContractId;
    }

    public void setParentContractId(Long parentContractId) {
        this.parentContractId = parentContractId;
    }

    public Long getContractTypeId() {
        return contractTypeId;
    }

    public void setContractTypeId(Long contractTypeId) {
        this.contractTypeId = contractTypeId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getConfigurationWorkflowInstanceKey() {
        return configurationWorkflowInstanceKey;
    }

    public void setConfigurationWorkflowInstanceKey(String configurationWorkflowInstanceKey) {
        this.configurationWorkflowInstanceKey = configurationWorkflowInstanceKey;
    }

    public String getCofWorkflowInstanceKey() {
        return cofWorkflowInstanceKey;
    }

    public void setCofWorkflowInstanceKey(String cofWorkflowInstanceKey) {
        this.cofWorkflowInstanceKey = cofWorkflowInstanceKey;
    }
}


