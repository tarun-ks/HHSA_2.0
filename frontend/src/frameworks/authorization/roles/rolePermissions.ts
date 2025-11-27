import { ROLES, Role } from './roleDefinitions';

/**
 * Permission definitions.
 * Each permission maps to a list of roles that have access.
 */
export const PERMISSIONS = {
  // Contract permissions
  CONTRACT_CREATE: [ROLES.ACCO_STAFF, ROLES.ACCO_MANAGER, ROLES.ACCO_ADMIN_STAFF, ROLES.PROGRAM_MANAGER],
  CONTRACT_VIEW: [ROLES.ACCO_STAFF, ROLES.ACCO_MANAGER, ROLES.ACCO_ADMIN_STAFF, ROLES.PROGRAM_MANAGER, ROLES.FINANCE_STAFF, ROLES.FINANCE_MANAGER],
  CONTRACT_CONFIGURE: [ROLES.ACCO_STAFF, ROLES.ACCO_MANAGER, ROLES.ACCO_ADMIN_STAFF],
  CONTRACT_BUDGET_SAVE: [ROLES.ACCO_STAFF, ROLES.ACCO_MANAGER, ROLES.ACCO_ADMIN_STAFF, ROLES.FINANCE_STAFF, ROLES.FINANCE_MANAGER],
  CONTRACT_EDIT: [ROLES.ACCO_STAFF, ROLES.ACCO_MANAGER, ROLES.ACCO_ADMIN_STAFF],
  CONTRACT_DELETE: [ROLES.ACCO_MANAGER, ROLES.ACCO_ADMIN_STAFF],
  
  // Workflow permissions
  WORKFLOW_TASK_VIEW: [ROLES.ACCO_STAFF, ROLES.ACCO_MANAGER, ROLES.ACCO_ADMIN_STAFF, ROLES.PROGRAM_MANAGER, ROLES.FINANCE_STAFF, ROLES.FINANCE_MANAGER],
  WORKFLOW_TASK_APPROVE: [ROLES.ACCO_MANAGER, ROLES.ACCO_ADMIN_STAFF, ROLES.FINANCE_MANAGER],
  
  // Services permissions (based on Contract Financials and Contract Budget Design.pdf)
  SERVICES_ENABLE: [ROLES.ACCO_STAFF, ROLES.ACCO_MANAGER, ROLES.ACCO_ADMIN_STAFF], // Service-Enabled and Service-Configurable agencies
  SERVICES_VIEW: [ROLES.ACCO_STAFF, ROLES.ACCO_MANAGER, ROLES.ACCO_ADMIN_STAFF, ROLES.PROGRAM_MANAGER, ROLES.FINANCE_STAFF, ROLES.FINANCE_MANAGER],
  
  // Budget template permissions
  BUDGET_TEMPLATE_SELECT: [ROLES.ACCO_STAFF, ROLES.ACCO_MANAGER, ROLES.ACCO_ADMIN_STAFF],
  BUDGET_TEMPLATE_REMOVE: [ROLES.ACCO_MANAGER, ROLES.ACCO_ADMIN_STAFF], // Cannot remove after approval
} as const;

export type Permission = keyof typeof PERMISSIONS;

/**
 * Get all roles that have a specific permission
 */
export function getRolesForPermission(permission: Permission): Role[] {
  return [...PERMISSIONS[permission]] as Role[];
}

/**
 * Check if a role has a specific permission
 */
export function roleHasPermission(role: Role, permission: Permission): boolean {
  const roles = PERMISSIONS[permission];
  return (roles as readonly string[]).includes(role);
}

