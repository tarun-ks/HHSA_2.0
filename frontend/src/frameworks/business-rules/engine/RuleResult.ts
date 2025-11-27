/**
 * Result of a business rule validation
 */
export interface RuleResult {
  /** Whether the rule passed */
  isValid: boolean;
  /** Error message if rule failed */
  message?: string;
  /** Additional metadata about the validation */
  metadata?: Record<string, any>;
}

/**
 * Create a successful rule result
 */
export function createSuccessResult(metadata?: Record<string, any>): RuleResult {
  return {
    isValid: true,
    metadata,
  };
}

/**
 * Create a failed rule result
 */
export function createFailureResult(message: string, metadata?: Record<string, any>): RuleResult {
  return {
    isValid: false,
    message,
    metadata,
  };
}

