import { BusinessRule } from '../../engine/RuleEngine';
import { RuleContext } from '../../engine/RuleContext';
import { createSuccessResult, createFailureResult } from '../../engine/RuleResult';

/**
 * Common calculation-based rules
 */

/**
 * Sum equals target value rule
 */
export function createSumEqualsRule<T>(
  fieldName: string,
  targetValue: number | ((context: RuleContext) => number),
  getValue: (data: T) => number,
  getAllItems: (context: RuleContext) => T[]
): BusinessRule<T> {
  return {
    name: `SUM_EQUALS_${fieldName.toUpperCase()}`,
    description: `Sum of ${fieldName} must equal target value`,
    validate: (data: T, context: RuleContext) => {
      const allItems = getAllItems(context);
      const sum = allItems.reduce((total, item) => total + getValue(item), 0);
      const target = typeof targetValue === 'function' ? targetValue(context) : targetValue;
      
      if (Math.abs(sum - target) > 0.01) { // Allow small floating point differences
        return createFailureResult(
          `Sum of ${fieldName} (${sum.toFixed(2)}) must equal ${target.toFixed(2)}`
        );
      }
      return createSuccessResult({ sum, target });
    },
  };
}

/**
 * No duplicates rule
 */
export function createNoDuplicatesRule<T>(
  getKey: (data: T) => string,
  keyDescription: string = 'combination'
): BusinessRule<T> {
  return {
    name: `NO_DUPLICATES_${keyDescription.toUpperCase()}`,
    description: `No duplicate ${keyDescription} allowed`,
    validate: (data: T, context: RuleContext) => {
      const allItems = context.allRows || [];
      const currentKey = getKey(data);
      const duplicates = allItems.filter((item) => getKey(item) === currentKey);
      
      if (duplicates.length > 1) {
        return createFailureResult(`Duplicate ${keyDescription} not allowed`);
      }
      return createSuccessResult();
    },
  };
}

