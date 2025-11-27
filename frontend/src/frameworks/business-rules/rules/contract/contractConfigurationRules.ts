import { BusinessRule } from '../../engine/RuleEngine';
import { RuleContext } from '../../engine/RuleContext';
import { createSuccessResult, createFailureResult } from '../../engine/RuleResult';
import { createSumEqualsRule, createNoDuplicatesRule } from '../common';

/**
 * COA Allocation interface
 */
export interface COAAllocation {
  uobc: string;
  subOc: string;
  rc: string;
  amount: number; // Total amount (sum of all fiscal years)
  fiscalYearAmounts?: Record<string, number>; // Fiscal year breakdown: { "FY12": 200000, "FY13": 200000, ... }
}

/**
 * Contract Configuration Rules
 */

/**
 * Rule: COA total must equal contract value
 */
export const COA_TOTAL_MUST_EQUAL_CONTRACT_VALUE: BusinessRule<COAAllocation> = {
  name: 'COA_TOTAL_MUST_EQUAL_CONTRACT_VALUE',
  description: 'Total COA allocation must equal contract value',
  validate: (data: COAAllocation, context: RuleContext) => {
    const allAllocations = (context.allRows || []) as COAAllocation[];
    const total = allAllocations.reduce((sum, alloc) => sum + (alloc.amount || 0), 0);
    const contractValue = context.contractValue || 0;
    
    if (Math.abs(total - contractValue) > 0.01) {
      return createFailureResult(
        `Total allocation ($${total.toFixed(2)}) must equal contract value ($${contractValue.toFixed(2)})`
      );
    }
    return createSuccessResult({ total, contractValue });
  },
};

/**
 * Rule: No duplicate COA combinations
 */
export const COA_NO_DUPLICATES: BusinessRule<COAAllocation> = createNoDuplicatesRule<COAAllocation>(
  (alloc) => `${alloc.uobc}-${alloc.subOc}-${alloc.rc}`,
  'COA combination'
);

/**
 * Rule: All COA fields required
 */
export const COA_ALL_REQUIRED_FIELDS: BusinessRule<COAAllocation> = {
  name: 'COA_ALL_REQUIRED_FIELDS',
  description: 'All COA fields (UOBC, Sub OC, RC, Amount) are required',
  validate: (data: COAAllocation) => {
    const errors: string[] = [];
    
    if (!data.uobc || data.uobc.trim() === '') {
      errors.push('UOBC is required');
    }
    if (!data.subOc || data.subOc.trim() === '') {
      errors.push('Sub Object Code is required');
    }
    if (!data.rc || data.rc.trim() === '') {
      errors.push('RC is required');
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
 * Row-level rules (applied to individual rows in the grid)
 * These rules validate each row independently
 */
export const CONTRACT_CONFIGURATION_ROW_RULES = [
  COA_ALL_REQUIRED_FIELDS,
  COA_NO_DUPLICATES,
];

/**
 * Form-level rules (applied when validating all rows together)
 * These rules validate the entire form/collection
 */
export const CONTRACT_CONFIGURATION_FORM_RULES = [
  COA_TOTAL_MUST_EQUAL_CONTRACT_VALUE,
];

/**
 * All contract configuration rules (for backward compatibility)
 * Note: Use ROW_RULES for per-row validation and FORM_RULES for form-level validation
 */
export const CONTRACT_CONFIGURATION_RULES = [
  ...CONTRACT_CONFIGURATION_ROW_RULES,
  ...CONTRACT_CONFIGURATION_FORM_RULES,
];

