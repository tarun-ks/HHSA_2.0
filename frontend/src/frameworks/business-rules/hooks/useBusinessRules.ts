import { useMemo } from 'react';
import { BusinessRule, RuleEngine } from '../engine/RuleEngine';
import { RuleContext } from '../engine/RuleContext';
import { RuleResult } from '../engine/RuleResult';

/**
 * Hook for executing business rules
 */
export function useBusinessRules() {
  return useMemo(
    () => ({
      /**
       * Validate data against a single rule
       */
      validate: <T>(rule: BusinessRule<T>, data: T, context: RuleContext = {}): RuleResult => {
        return RuleEngine.validate(rule, data, context);
      },

      /**
       * Validate data against multiple rules
       * Returns all results
       */
      validateAll: <T>(
        rules: BusinessRule<T>[],
        data: T,
        context: RuleContext = {}
      ): RuleResult[] => {
        return RuleEngine.validateAll(rules, data, context);
      },

      /**
       * Validate data against multiple rules
       * Returns first failure (short-circuits)
       */
      validateAny: <T>(
        rules: BusinessRule<T>[],
        data: T,
        context: RuleContext = {}
      ): RuleResult => {
        return RuleEngine.validateAny(rules, data, context);
      },

      /**
       * Check if all rules pass
       */
      allPass: <T>(
        rules: BusinessRule<T>[],
        data: T,
        context: RuleContext = {}
      ): boolean => {
        return RuleEngine.allPass(rules, data, context);
      },

      /**
       * Get all error messages from failed rules
       */
      getErrors: <T>(
        rules: BusinessRule<T>[],
        data: T,
        context: RuleContext = {}
      ): string[] => {
        return RuleEngine.getErrors(rules, data, context);
      },
    }),
    []
  );
}

