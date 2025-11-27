package com.hhsa.contract.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Request DTO for creating/updating contract budgets.
 */
public record ContractBudgetRequest(
    @NotNull(message = "Contract ID is required")
    Long contractId,

    @NotEmpty(message = "At least one budget allocation is required")
    List<BudgetAllocation> allocations
) {
    /**
     * Budget allocation line item.
     */
    public record BudgetAllocation(
        @NotBlank(message = "Fiscal year is required")
        @Size(max = 10, message = "Fiscal year must not exceed 10 characters")
        String fiscalYear,

        @NotBlank(message = "Budget code is required")
        @Size(max = 50, message = "Budget code must not exceed 50 characters")
        String budgetCode,

        @NotBlank(message = "Object code is required")
        @Size(max = 50, message = "Object code must not exceed 50 characters")
        String objectCode,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
        BigDecimal amount
    ) {
    }
}

