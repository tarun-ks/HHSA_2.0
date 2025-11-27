package com.hhsa.contract.entity;

import com.hhsa.common.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Contract entity.
 * Core contract entity with financials, status, and relationships.
 */
@Entity
@Table(name = "contracts", indexes = {
    @Index(name = "idx_contracts_number", columnList = "contract_number"),
    @Index(name = "idx_contracts_status", columnList = "status_id"),
    @Index(name = "idx_contracts_agency", columnList = "agency_id"),
    @Index(name = "idx_contracts_provider", columnList = "provider_id")
})
public class Contract extends BaseEntity {

    @NotBlank
    @Size(max = 30)
    @Column(name = "contract_number", nullable = false, unique = true, length = 30)
    private String contractNumber;

    @NotBlank
    @Size(max = 255)
    @Column(name = "contract_title", nullable = false, length = 255)
    private String contractTitle;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "contract_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal contractValue;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "contract_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal contractAmount;

    @NotNull
    @Column(name = "contract_start_date", nullable = false)
    private LocalDate contractStartDate;

    @NotNull
    @Column(name = "contract_end_date", nullable = false)
    private LocalDate contractEndDate;

    @NotNull
    @Min(59)
    @Max(69)
    @Column(name = "status_id", nullable = false)
    private Integer statusId; // 59-69

    @NotBlank
    @Size(max = 50)
    @Column(name = "agency_id", nullable = false, length = 50)
    private String agencyId;

    @Size(max = 50)
    @Column(name = "program_id", length = 50)
    private String programId;

    @NotBlank
    @Size(max = 50)
    @Column(name = "provider_id", nullable = false, length = 50)
    private String providerId;

    @NotBlank
    @Size(max = 50)
    @Column(name = "organization_id", nullable = false, length = 50)
    private String organizationId;

    @NotBlank
    @Size(max = 50)
    @Column(name = "e_pin", nullable = false, length = 50)
    private String ePin; // Award E-PIN (required for creation)

    @Size(max = 1)
    @Column(name = "registration_flag", length = 1)
    private String registrationFlag;

    @Size(max = 50)
    @Column(name = "procurement_id", length = 50)
    private String procurementId;

    @Column(name = "parent_contract_id")
    private Long parentContractId; // For amendments

    @Column(name = "contract_type_id")
    private Long contractTypeId;

    // Workflow integration fields
    @Column(name = "configuration_workflow_instance_key")
    private String configurationWorkflowInstanceKey; // WF302 process instance key

    @Column(name = "cof_workflow_instance_key")
    private String cofWorkflowInstanceKey; // WF303 process instance key

    // Getters and Setters

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
