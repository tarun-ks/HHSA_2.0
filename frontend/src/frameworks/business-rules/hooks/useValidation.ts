import { useMemo, useCallback } from 'react';
import { BusinessRule } from '../engine/RuleEngine';
import { RuleContext } from '../engine/RuleContext';
import { useBusinessRules } from './useBusinessRules';

/**
 * Hook for form/table validation using business rules
 */
export function useValidation<T>(
  rules: BusinessRule<T>[],
  context: RuleContext = {}
) {
  const { validateAll, allPass, getErrors } = useBusinessRules();

  /**
   * Validate a single item
   */
  const validateItem = useCallback(
    (item: T, itemContext: RuleContext = {}): Record<string, string> | null => {
      const mergedContext = { ...context, ...itemContext };
      const errors = getErrors(rules, item, mergedContext);
      
      if (errors.length === 0) {
        return null;
      }
      
      // Return errors as a record (for EditableTable compatibility)
      return {
        _row: errors.join('; '), // Combine all errors
      };
    },
    [rules, context, getErrors]
  );

  /**
   * Validate all items
   */
  const validateAllItems = useCallback(
    (items: T[], itemContext: RuleContext = {}): boolean => {
      const mergedContext = { ...context, ...itemContext, allRows: items };
      return items.every((item) => allPass(rules, item, mergedContext));
    },
    [rules, context, allPass]
  );

  /**
   * Get validation function for EditableTable
   */
  const getValidationFunction = useCallback(
    () => (row: T, allRows: T[]): Record<string, string> | null => {
      return validateItem(row, { allRows });
    },
    [validateItem]
  );

  return useMemo(
    () => ({
      validateItem,
      validateAllItems,
      getValidationFunction,
      isValid: (items: T[]) => validateAllItems(items),
    }),
    [validateItem, validateAllItems, getValidationFunction]
  );
}

