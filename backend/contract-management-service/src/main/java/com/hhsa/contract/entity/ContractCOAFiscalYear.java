package com.hhsa.contract.entity;

import com.hhsa.common.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Contract COA Fiscal Year entity.
 * Stores fiscal year breakdown for each COA allocation.
 * Each ContractConfiguration can have multiple fiscal year entries (FY12-FY16).
 */
@Entity
@Table(name = "contract_coa_fiscal_years", indexes = {
    @Index(name = "idx_coa_fiscal_year_config", columnList = "contract_configuration_id"),
    @Index(name = "idx_coa_fiscal_year_year", columnList = "fiscal_year")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_coa_fiscal_year_config_year", columnNames = {"contract_configuration_id", "fiscal_year"})
})
public class ContractCOAFiscalYear extends BaseEntity {

    @NotNull
    @Column(name = "contract_configuration_id", nullable = false)
    private Long contractConfigurationId;

    @NotBlank
    @Size(max = 10)
    @Column(name = "fiscal_year", nullable = false, length = 10)
    private String fiscalYear; // e.g., "FY12", "FY13", "FY14", "FY15", "FY16"

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_configuration_id", insertable = false, updatable = false)
    private ContractConfiguration contractConfiguration;

    // Getters and Setters

    public Long getContractConfigurationId() {
        return contractConfigurationId;
    }

    public void setContractConfigurationId(Long contractConfigurationId) {
        this.contractConfigurationId = contractConfigurationId;
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

    public ContractConfiguration getContractConfiguration() {
        return contractConfiguration;
    }

    public void setContractConfiguration(ContractConfiguration contractConfiguration) {
        this.contractConfiguration = contractConfiguration;
    }
}

