package com.hhsa.contract.entity;

import com.hhsa.common.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

/**
 * Contract Budget Template entity (junction table).
 * Links contracts to selected budget templates.
 */
@Entity
@Table(name = "contract_budget_templates", indexes = {
    @Index(name = "idx_contract_budget_template_contract", columnList = "contract_id"),
    @Index(name = "idx_contract_budget_template_template", columnList = "budget_template_id")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_contract_budget_template", columnNames = {"contract_id", "budget_template_id"})
})
public class ContractBudgetTemplate extends BaseEntity {

    @NotNull
    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @NotNull
    @Column(name = "budget_template_id", nullable = false)
    private Long budgetTemplateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", insertable = false, updatable = false)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_template_id", insertable = false, updatable = false)
    private BudgetTemplate budgetTemplate;

    // Getters and Setters

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getBudgetTemplateId() {
        return budgetTemplateId;
    }

    public void setBudgetTemplateId(Long budgetTemplateId) {
        this.budgetTemplateId = budgetTemplateId;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public BudgetTemplate getBudgetTemplate() {
        return budgetTemplate;
    }

    public void setBudgetTemplate(BudgetTemplate budgetTemplate) {
        this.budgetTemplate = budgetTemplate;
    }
}

