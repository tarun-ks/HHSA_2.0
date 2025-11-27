package com.hhsa.contract.entity;

import com.hhsa.common.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Contract Funding Source entity.
 * Represents funding source allocation line items for a contract.
 */
@Entity
@Table(name = "contract_funding_sources", indexes = {
    @Index(name = "idx_contract_funding_contract", columnList = "contract_id")
})
public class ContractFundingSource extends BaseEntity {

    @NotNull
    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @NotBlank
    @Size(max = 50)
    @Column(name = "funding_source_id", nullable = false, length = 50)
    private String fundingSourceId;

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

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }
}


