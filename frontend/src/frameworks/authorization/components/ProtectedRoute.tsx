import { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthorization } from '../hooks/useAuthorization';
import { Permission } from '../roles/rolePermissions';

interface ProtectedRouteProps {
  /** Permission required to access route */
  permission?: Permission;
  /** Roles required to access route (alternative to permission) */
  roles?: string[];
  /** Whether user must have ALL roles (default: false, any role) */
  requireAll?: boolean;
  /** Children to render if user has permission */
  children: ReactNode;
  /** Fallback route if user doesn't have permission */
  fallback?: string;
}

/**
 * ProtectedRoute component.
 * Protects routes based on user roles/permissions.
 */
export function ProtectedRoute({
  permission,
  roles,
  requireAll = false,
  children,
  fallback = '/unauthorized',
}: ProtectedRouteProps) {
  const { isAuthenticated, hasPermission, hasAnyRole, hasAllRoles } = useAuthorization();

  // First check authentication
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Then check authorization
  let hasAccess = false;

  if (permission) {
    hasAccess = hasPermission(permission);
  } else if (roles && roles.length > 0) {
    hasAccess = requireAll ? hasAllRoles(roles as any[]) : hasAnyRole(roles as any[]);
  } else {
    // No permission or roles specified, allow access
    hasAccess = true;
  }

  if (!hasAccess) {
    return <Navigate to={fallback} replace />;
  }

  return <>{children}</>;
}

