package com.hhsa.contract.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Request DTO for contract funding source allocation
 */
public class ContractFundingSourceRequest {

    @NotNull(message = "Contract ID is required")
    private Long contractId;

    @NotEmpty(message = "At least one funding source allocation is required")
    @Valid
    private List<FundingSourceAllocation> fundingSourceAllocations;

    // Getters and Setters

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public List<FundingSourceAllocation> getFundingSourceAllocations() {
        return fundingSourceAllocations;
    }

    public void setFundingSourceAllocations(List<FundingSourceAllocation> fundingSourceAllocations) {
        this.fundingSourceAllocations = fundingSourceAllocations;
    }

    public static class FundingSourceAllocation {
        @NotBlank(message = "Funding Source ID is required")
        @Size(max = 50)
        private String fundingSourceId; // e.g., "Federal", "State", "City", "Other"

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount; // Total amount (sum of all fiscal years)

        // Fiscal year breakdown (optional - if not provided, amount is allocated evenly or to current FY)
        private Map<String, BigDecimal> fiscalYearAmounts; // Key: "FY12", "FY13", etc., Value: amount

        // Getters and Setters

        public String getFundingSourceId() {
            return fundingSourceId;
        }

        public void setFundingSourceId(String fundingSourceId) {
            this.fundingSourceId = fundingSourceId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public Map<String, BigDecimal> getFiscalYearAmounts() {
            return fiscalYearAmounts;
        }

        public void setFiscalYearAmounts(Map<String, BigDecimal> fiscalYearAmounts) {
            this.fiscalYearAmounts = fiscalYearAmounts;
        }
    }
}

