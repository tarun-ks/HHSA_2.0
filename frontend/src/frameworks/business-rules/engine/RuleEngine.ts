import { RuleResult } from './RuleResult';
import { RuleContext } from './RuleContext';

/**
 * Business rule definition
 */
export interface BusinessRule<T> {
  /** Unique rule name */
  name: string;
  /** Human-readable description */
  description: string;
  /** Validation function */
  validate: (data: T, context: RuleContext) => RuleResult;
  /** Optional custom error message generator */
  message?: string | ((data: T, context: RuleContext) => string);
}

/**
 * Rule engine for executing business rules
 */
export class RuleEngine {
  /**
   * Validate data against a single rule
   */
  static validate<T>(rule: BusinessRule<T>, data: T, context: RuleContext = {}): RuleResult {
    try {
      const result = rule.validate(data, context);
      
      // Use custom message if provided and rule failed
      if (!result.isValid && rule.message) {
        const message = typeof rule.message === 'function' 
          ? rule.message(data, context)
          : rule.message;
        return {
          ...result,
          message: message || result.message,
        };
      }
      
      return result;
    } catch (error) {
      return {
        isValid: false,
        message: `Rule validation error: ${error instanceof Error ? error.message : 'Unknown error'}`,
        metadata: { error },
      };
    }
  }

  /**
   * Validate data against multiple rules
   * Returns all results (doesn't short-circuit)
   */
  static validateAll<T>(
    rules: BusinessRule<T>[],
    data: T,
    context: RuleContext = {}
  ): RuleResult[] {
    return rules.map((rule) => this.validate(rule, data, context));
  }

  /**
   * Validate data against multiple rules
   * Returns first failure (short-circuits)
   */
  static validateAny<T>(
    rules: BusinessRule<T>[],
    data: T,
    context: RuleContext = {}
  ): RuleResult {
    for (const rule of rules) {
      const result = this.validate(rule, data, context);
      if (!result.isValid) {
        return result;
      }
    }
    return { isValid: true };
  }

  /**
   * Check if all rules pass
   */
  static allPass<T>(
    rules: BusinessRule<T>[],
    data: T,
    context: RuleContext = {}
  ): boolean {
    return this.validateAll(rules, data, context).every((result) => result.isValid);
  }

  /**
   * Get all error messages from failed rules
   */
  static getErrors<T>(
    rules: BusinessRule<T>[],
    data: T,
    context: RuleContext = {}
  ): string[] {
    return this.validateAll(rules, data, context)
      .filter((result) => !result.isValid)
      .map((result) => result.message || 'Validation failed')
      .filter((msg): msg is string => !!msg);
  }
}

