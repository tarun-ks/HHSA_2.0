package com.hhsa.contract.entity;

import com.hhsa.common.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Contract Budget entity.
 * Represents budget allocation line items for a contract by fiscal year.
 */
@Entity
@Table(name = "contract_budgets", indexes = {
    @Index(name = "idx_contract_budget_contract", columnList = "contract_id"),
    @Index(name = "idx_contract_budget_fiscal_year", columnList = "contract_id, fiscal_year")
})
public class ContractBudget extends BaseEntity {

    @NotNull
    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @NotBlank
    @Size(max = 10)
    @Column(name = "fiscal_year", nullable = false, length = 10)
    private String fiscalYear;

    @NotBlank
    @Size(max = 50)
    @Column(name = "budget_code", nullable = false, length = 50)
    private String budgetCode;

    @NotBlank
    @Size(max = 50)
    @Column(name = "object_code", nullable = false, length = 50)
    private String objectCode;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", insertable = false, updatable = false)
    private Contract contract;

    // Getters and Setters

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public String getFiscalYear() {
        return fiscalYear;
    }

    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }

    public String getBudgetCode() {
        return budgetCode;
    }

    public void setBudgetCode(String budgetCode) {
        this.budgetCode = budgetCode;
    }

    public String getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(String objectCode) {
        this.objectCode = objectCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }
}

