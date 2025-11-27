package com.hhsa.contract.dto;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Contract Configuration DTO (Chart of Accounts)
 */
public class ContractConfigurationDTO {

    private Long id;
    private Long contractId;
    private String uobc; // Unit of Appropriation
    private String subOc; // Sub Object Code
    private String rc; // Responsibility Center
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

    public Map<String, BigDecimal> getFiscalYearAmounts() {
        return fiscalYearAmounts;
    }

    public void setFiscalYearAmounts(Map<String, BigDecimal> fiscalYearAmounts) {
        this.fiscalYearAmounts = fiscalYearAmounts;
    }
}


