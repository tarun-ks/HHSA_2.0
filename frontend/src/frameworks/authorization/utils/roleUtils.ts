import { Role } from '../roles/roleDefinitions';
import { Permission, roleHasPermission } from '../roles/rolePermissions';

/**
 * Check if user has a specific role
 */
export function hasRole(userRoles: Role[] | undefined, role: Role): boolean {
  if (!userRoles || userRoles.length === 0) {
    return false;
  }
  return userRoles.includes(role);
}

/**
 * Check if user has any of the specified roles
 */
export function hasAnyRole(userRoles: Role[] | undefined, roles: Role[]): boolean {
  if (!userRoles || userRoles.length === 0) {
    return false;
  }
  return roles.some((role) => userRoles.includes(role));
}

/**
 * Check if user has all of the specified roles
 */
export function hasAllRoles(userRoles: Role[] | undefined, roles: Role[]): boolean {
  if (!userRoles || userRoles.length === 0) {
    return false;
  }
  return roles.every((role) => userRoles.includes(role));
}

/**
 * Check if user has a specific permission
 */
export function hasPermission(userRoles: Role[] | undefined, permission: Permission): boolean {
  if (!userRoles || userRoles.length === 0) {
    return false;
  }
  return userRoles.some((role) => roleHasPermission(role, permission));
}

/**
 * Check if user has any of the specified permissions
 */
export function hasAnyPermission(userRoles: Role[] | undefined, permissions: Permission[]): boolean {
  if (!userRoles || userRoles.length === 0) {
    return false;
  }
  return permissions.some((permission) => hasPermission(userRoles, permission));
}

