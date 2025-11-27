import { BusinessRule } from '../../engine/RuleEngine';
import { RuleContext } from '../../engine/RuleContext';
import { createSuccessResult, createFailureResult } from '../../engine/RuleResult';
import { createRequiredRule, createPositiveNumberRule } from '../common';

/**
 * Budget Allocation interface
 */
export interface BudgetAllocation {
  fiscalYear: string;
  budgetCode: string;
  objectCode: string;
  amount: number;
}

/**
 * Contract Budget Rules
 */

/**
 * Rule: All budget fields required
 */
export const BUDGET_ALL_REQUIRED_FIELDS: BusinessRule<BudgetAllocation> = {
  name: 'BUDGET_ALL_REQUIRED_FIELDS',
  description: 'All budget fields (Fiscal Year, Budget Code, Object Code, Amount) are required',
  validate: (data: BudgetAllocation) => {
    const errors: string[] = [];
    
    if (!data.fiscalYear || data.fiscalYear.trim() === '') {
      errors.push('Fiscal Year is required');
    }
    if (!data.budgetCode || data.budgetCode.trim() === '') {
      errors.push('Budget Code is required');
    }
    if (!data.objectCode || data.objectCode.trim() === '') {
      errors.push('Object Code is required');
    }
    if (!data.amount || data.amount <= 0) {
      errors.push('Amount must be greater than 0');
    }
    
    if (errors.length > 0) {
      return createFailureResult(errors.join(', '));
    }
    return createSuccessResult();
  },
};

/**
 * Rule: Fiscal Year format validation
 */
export const BUDGET_FISCAL_YEAR_FORMAT: BusinessRule<BudgetAllocation> = {
  name: 'BUDGET_FISCAL_YEAR_FORMAT',
  description: 'Fiscal Year must be a valid format (YYYY)',
  validate: (data: BudgetAllocation) => {
    if (!data.fiscalYear) {
      return createSuccessResult(); // Let required rule handle this
    }
    
    const fiscalYearPattern = /^\d{4}$/;
    if (!fiscalYearPattern.test(data.fiscalYear.trim())) {
      return createFailureResult('Fiscal Year must be in YYYY format');
    }
    
    const year = parseInt(data.fiscalYear.trim());
    const currentYear = new Date().getFullYear();
    if (year < 2000 || year > currentYear + 10) {
      return createFailureResult(`Fiscal Year must be between 2000 and ${currentYear + 10}`);
    }
    
    return createSuccessResult();
  },
};

/**
 * Rule: Amount must be positive
 */
export const BUDGET_AMOUNT_POSITIVE: BusinessRule<BudgetAllocation> = 
  createPositiveNumberRule<BudgetAllocation>(
    'Amount',
    (data) => data.amount
  );

/**
 * All contract budget rules
 */
export const CONTRACT_BUDGET_RULES = [
  BUDGET_ALL_REQUIRED_FIELDS,
  BUDGET_FISCAL_YEAR_FORMAT,
  BUDGET_AMOUNT_POSITIVE,
];

