package com.hhsa.contract.entity;

import com.hhsa.common.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Contract Funding Source Fiscal Year entity.
 * Stores fiscal year breakdown for each funding source allocation.
 * Each ContractFundingSource can have multiple fiscal year entries (FY12-FY16).
 */
@Entity
@Table(name = "contract_funding_source_fiscal_years", indexes = {
    @Index(name = "idx_funding_fiscal_year_source", columnList = "contract_funding_source_id"),
    @Index(name = "idx_funding_fiscal_year_year", columnList = "fiscal_year")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_funding_fiscal_year_source_year", columnNames = {"contract_funding_source_id", "fiscal_year"})
})
public class ContractFundingSourceFiscalYear extends BaseEntity {

    @NotNull
    @Column(name = "contract_funding_source_id", nullable = false)
    private Long contractFundingSourceId;

    @NotBlank
    @Size(max = 10)
    @Column(name = "fiscal_year", nullable = false, length = 10)
    private String fiscalYear; // e.g., "FY12", "FY13", "FY14", "FY15", "FY16"

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_funding_source_id", insertable = false, updatable = false)
    private ContractFundingSource contractFundingSource;

    // Getters and Setters

    public Long getContractFundingSourceId() {
        return contractFundingSourceId;
    }

    public void setContractFundingSourceId(Long contractFundingSourceId) {
        this.contractFundingSourceId = contractFundingSourceId;
    }

    public String getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public ContractFundingSource getContractFundingSource() {
        return contractFundingSource;
    }

    public void setContractFundingSource(ContractFundingSource contractFundingSource) {
        this.contractFundingSource = contractFundingSource;
    }
}

