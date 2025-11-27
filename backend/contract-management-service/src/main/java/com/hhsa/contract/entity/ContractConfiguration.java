package com.hhsa.contract.entity;

import com.hhsa.common.infrastructure.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Contract Configuration entity (Chart of Accounts).
 * Represents COA allocation line items for a contract.
 */
@Entity
@Table(name = "contract_configurations", indexes = {
    @Index(name = "idx_contract_config_contract", columnList = "contract_id"),
    @Index(name = "idx_contract_config_coa", columnList = "uobc, sub_oc, rc")
})
public class ContractConfiguration extends BaseEntity {

    @NotNull
    @Column(name = "contract_id", nullable = false)
    private Long contractId;

    @NotBlank
    @Size(max = 50)
    @Column(name = "uobc", nullable = false, length = 50)
    private String uobc; // Unit of Appropriation

    @NotBlank
    @Size(max = 50)
    @Column(name = "sub_oc", nullable = false, length = 50)
    private String subOc; // Sub Object Code

    @NotBlank
    @Size(max = 50)
    @Column(name = "rc", nullable = false, length = 50)
    private String rc; // Responsibility Center

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

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }
}

