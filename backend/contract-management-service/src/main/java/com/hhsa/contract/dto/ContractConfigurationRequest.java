package com.hhsa.contract.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for contract configuration (Chart of Accounts)
 */
public class ContractConfigurationRequest {

    @NotNull(message = "Contract ID is required")
    private Long contractId;

    @NotEmpty(message = "At least one COA allocation is required")
    @Valid
    private List<COAAllocation> coaAllocations;

    // Getters and Setters

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public List<COAAllocation> getCoaAllocations() {
        return coaAllocations;
    }

    public void setCoaAllocations(List<COAAllocation> coaAllocations) {
        this.coaAllocations = coaAllocations;
    }

    public static class COAAllocation {
        @NotBlank(message = "UOBC is required")
        @Size(max = 50)
        private String uobc;

        @NotBlank(message = "Sub Object Code is required")
        @Size(max = 50)
        private String subOc;

        @NotBlank(message = "Responsibility Center is required")
        @Size(max = 50)
        private String rc;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount; // Total amount (sum of all fiscal years)

        // Fiscal year breakdown (optional - if not provided, amount is allocated evenly or to current FY)
        private java.util.Map<String, BigDecimal> fiscalYearAmounts; // Key: "FY12", "FY13", etc., Value: amount

        // Getters and Setters

        public String getUobc() {
            return uobc;
        }

        public void setUobc(String uobc) {
            this.uobc = uobc;
        }

        public String getSubOc() {
            return subOc;
        }

        public void setSubOc(String subOc) {
            this.subOc = subOc;
        }

        public String getRc() {
            return rc;
        }

        public void setRc(String rc) {
            this.rc = rc;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public java.util.Map<String, BigDecimal> getFiscalYearAmounts() {
            return fiscalYearAmounts;
        }

        public void setFiscalYearAmounts(java.util.Map<String, BigDecimal> fiscalYearAmounts) {
            this.fiscalYearAmounts = fiscalYearAmounts;
        }
    }
}

