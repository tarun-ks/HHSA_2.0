import { ReactNode } from 'react';
import { useAuthorization } from '../hooks/useAuthorization';
import { Permission } from '../roles/rolePermissions';

interface RoleGateProps {
  /** Permission required to render children */
  permission?: Permission;
  /** Roles required to render children (alternative to permission) */
  roles?: string[];
  /** Whether user must have ALL roles (default: false, any role) */
  requireAll?: boolean;
  /** Children to render if user has permission */
  children: ReactNode;
  /** Fallback to render if user doesn't have permission */
  fallback?: ReactNode;
}

/**
 * RoleGate component.
 * Conditionally renders children based on user roles/permissions.
 */
export function RoleGate({
  permission,
  roles,
  requireAll = false,
  children,
  fallback = null,
}: RoleGateProps) {
  const { hasPermission, hasAnyRole, hasAllRoles } = useAuthorization();

  let hasAccess = false;

  if (permission) {
    hasAccess = hasPermission(permission);
  } else if (roles && roles.length > 0) {
    hasAccess = requireAll ? hasAllRoles(roles as any[]) : hasAnyRole(roles as any[]);
  } else {
    // No permission or roles specified, allow access
    hasAccess = true;
  }

  return hasAccess ? <>{children}</> : <>{fallback}</>;
}

