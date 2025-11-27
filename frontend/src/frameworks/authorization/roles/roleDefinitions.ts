/**
 * Role definitions for NYC Procure application.
 * These roles match the roles defined in Keycloak.
 */
export const ROLES = {
  ACCO_STAFF: 'ACCO_STAFF',
  ACCO_MANAGER: 'ACCO_MANAGER',
  ACCO_ADMIN_STAFF: 'ACCO_ADMIN_STAFF',
  PROGRAM_MANAGER: 'PROGRAM_MANAGER',
  FINANCE_STAFF: 'FINANCE_STAFF',
  FINANCE_MANAGER: 'FINANCE_MANAGER',
} as const;

export type Role = typeof ROLES[keyof typeof ROLES];

/**
 * Role display names for UI
 */
export const ROLE_DISPLAY_NAMES: Record<Role, string> = {
  [ROLES.ACCO_STAFF]: 'Accounting Staff',
  [ROLES.ACCO_MANAGER]: 'Accounting Manager',
  [ROLES.ACCO_ADMIN_STAFF]: 'Accounting Admin Staff',
  [ROLES.PROGRAM_MANAGER]: 'Program Manager',
  [ROLES.FINANCE_STAFF]: 'Finance Staff',
  [ROLES.FINANCE_MANAGER]: 'Finance Manager',
};

