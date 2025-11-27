import { Permission, getRolesForPermission } from '../roles/rolePermissions';
import { Role } from '../roles/roleDefinitions';

/**
 * Get all permissions that a role has
 */
export function getPermissionsForRole(role: Role): Permission[] {
  const permissions: Permission[] = [];
  
  // Iterate through all permissions and check if role has access
  (Object.keys(getRolesForPermission) as Permission[]).forEach((permission) => {
    const allowedRoles = getRolesForPermission(permission);
    if (allowedRoles.includes(role)) {
      permissions.push(permission);
    }
  });
  
  return permissions;
}

/**
 * Get all permissions that a set of roles have (union)
 */
export function getPermissionsForRoles(roles: Role[]): Permission[] {
  const permissionSet = new Set<Permission>();
  
  roles.forEach((role) => {
    const permissions = getPermissionsForRole(role);
    permissions.forEach((permission) => permissionSet.add(permission));
  });
  
  return Array.from(permissionSet);
}

