/**
 * Context for rule execution.
 * Contains additional data needed for rule validation.
 */
export interface RuleContext {
  /** Contract value (for COA validation) */
  contractValue?: number;
  /** All rows in the current dataset */
  allRows?: any[];
  /** Additional context data */
  [key: string]: any;
}

/**
 * Create a rule context
 */
export function createRuleContext(data: Partial<RuleContext> = {}): RuleContext {
  return {
    ...data,
  };
}

