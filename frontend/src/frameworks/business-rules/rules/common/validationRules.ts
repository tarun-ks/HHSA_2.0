import { BusinessRule } from '../../engine/RuleEngine';
import { RuleContext, createRuleContext } from '../../engine/RuleContext';
import { createSuccessResult, createFailureResult } from '../../engine/RuleResult';

/**
 * Common validation rules that can be reused across different domains
 */

/**
 * Required field validation rule
 */
export function createRequiredRule<T>(
  fieldName: string,
  getValue: (data: T) => any
): BusinessRule<T> {
  return {
    name: `REQUIRED_${fieldName.toUpperCase()}`,
    description: `${fieldName} is required`,
    validate: (data: T) => {
      const value = getValue(data);
      if (value === null || value === undefined || value === '' || 
          (Array.isArray(value) && value.length === 0)) {
        return createFailureResult(`${fieldName} is required`);
      }
      return createSuccessResult();
    },
  };
}

/**
 * Minimum value validation rule
 */
export function createMinValueRule<T>(
  fieldName: string,
  minValue: number,
  getValue: (data: T) => number
): BusinessRule<T> {
  return {
    name: `MIN_VALUE_${fieldName.toUpperCase()}`,
    description: `${fieldName} must be at least ${minValue}`,
    validate: (data: T) => {
      const value = getValue(data);
      if (typeof value !== 'number' || value < minValue) {
        return createFailureResult(`${fieldName} must be at least ${minValue}`);
      }
      return createSuccessResult();
    },
  };
}

/**
 * Maximum value validation rule
 */
export function createMaxValueRule<T>(
  fieldName: string,
  maxValue: number,
  getValue: (data: T) => number
): BusinessRule<T> {
  return {
    name: `MAX_VALUE_${fieldName.toUpperCase()}`,
    description: `${fieldName} must be at most ${maxValue}`,
    validate: (data: T) => {
      const value = getValue(data);
      if (typeof value !== 'number' || value > maxValue) {
        return createFailureResult(`${fieldName} must be at most ${maxValue}`);
      }
      return createSuccessResult();
    },
  };
}

/**
 * Positive number validation rule
 */
export function createPositiveNumberRule<T>(
  fieldName: string,
  getValue: (data: T) => number
): BusinessRule<T> {
  return {
    name: `POSITIVE_${fieldName.toUpperCase()}`,
    description: `${fieldName} must be a positive number`,
    validate: (data: T) => {
      const value = getValue(data);
      if (typeof value !== 'number' || value <= 0) {
        return createFailureResult(`${fieldName} must be greater than 0`);
      }
      return createSuccessResult();
    },
  };
}

/**
 * String length validation rule
 */
export function createStringLengthRule<T>(
  fieldName: string,
  minLength: number,
  maxLength: number,
  getValue: (data: T) => string
): BusinessRule<T> {
  return {
    name: `STRING_LENGTH_${fieldName.toUpperCase()}`,
    description: `${fieldName} must be between ${minLength} and ${maxLength} characters`,
    validate: (data: T) => {
      const value = getValue(data);
      if (typeof value !== 'string') {
        return createFailureResult(`${fieldName} must be a string`);
      }
      const length = value.trim().length;
      if (length < minLength || length > maxLength) {
        return createFailureResult(
          `${fieldName} must be between ${minLength} and ${maxLength} characters`
        );
      }
      return createSuccessResult();
    },
  };
}

