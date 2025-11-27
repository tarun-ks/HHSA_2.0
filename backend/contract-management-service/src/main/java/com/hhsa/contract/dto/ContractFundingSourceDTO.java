package com.hhsa.contract.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Contract Funding Source DTO
 */
public class ContractFundingSourceDTO {

    private Long id;
    private Long contractId;
    private String fundingSourceId; // e.g., "Federal", "State", "City", "Other"
    private BigDecimal amount; // Total amount (sum of all fiscal years)
    private Map<String, BigDecimal> fiscalYearAmounts; // Fiscal year breakdown (FY12, FY13, etc.)

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

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

