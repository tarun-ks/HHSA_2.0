package com.hhsa.contract.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Request DTO for saving contract budget templates
 */
public class ContractBudgetTemplateRequest {

    @NotNull(message = "Contract ID is required")
    private Long contractId;

    @NotNull(message = "Template IDs are required")
    private List<Long> templateIds;

    // Getters and Setters

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public List<Long> getTemplateIds() {
        return templateIds;
    }

    public void setTemplateIds(List<Long> templateIds) {
        this.templateIds = templateIds;
    }
}

