package com.hhsa.contract.dto;

import java.math.BigDecimal;

/**
 * DTO for Contract Budget.
 */
public record ContractBudgetDTO(
    Long id,
    Long contractId,
    String fiscalYear,
    String budgetCode,
    String objectCode,
    BigDecimal amount
) {
}

