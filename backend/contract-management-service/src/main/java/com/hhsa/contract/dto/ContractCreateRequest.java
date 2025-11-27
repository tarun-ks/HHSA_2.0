package com.hhsa.contract.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request DTO for creating a contract
 */
public class ContractCreateRequest {

    @NotBlank(message = "Contract number is required")
    @Size(max = 30, message = "Contract number must not exceed 30 characters")
    private String contractNumber;

    @NotBlank(message = "Contract title is required")
    @Size(max = 255, message = "Contract title must not exceed 255 characters")
    private String contractTitle;

    @NotNull(message = "Contract value is required")
    @DecimalMin(value = "0.01", message = "Contract value must be greater than 0")
    private BigDecimal contractValue;

    @NotNull(message = "Contract amount is required")
    @DecimalMin(value = "0.01", message = "Contract amount must be greater than 0")
    private BigDecimal contractAmount;

    @NotNull(message = "Contract start date is required")
    private LocalDate contractStartDate;

    @NotNull(message = "Contract end date is required")
    private LocalDate contractEndDate;

    @NotBlank(message = "Agency ID is required")
    @Size(max = 50)
    private String agencyId;

    @Size(max = 50)
    private String programId;

    @NotBlank(message = "Provider ID is required")
    @Size(max = 50)
    private String providerId;

    @NotBlank(message = "Organization ID is required")
    @Size(max = 50)
    private String organizationId;

    @NotBlank(message = "E-PIN is required")
    @Size(max = 50)
    @JsonProperty("ePin")
    private String ePin; // Award E-PIN (required for creation)

    @Size(max = 50)
    private String procurementId;

    private Long parentContractId; // For amendments

    private Long contractTypeId;

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
}


