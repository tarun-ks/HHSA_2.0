import { useMemo, useEffect } from 'react';
import { useAppSelector } from '../../../store/hooks';
import { Role } from '../roles/roleDefinitions';
import { Permission } from '../roles/rolePermissions';
import { hasRole, hasAnyRole, hasAllRoles, hasPermission, hasAnyPermission } from '../utils/roleUtils';
import { permissionCache } from '../utils/permissionCache';

/**
 * Main authorization hook.
 * Provides role and permission checking utilities with performance optimization.
 * 
 * Performance features:
 * - Permission caching for O(1) lookup
 * - Pre-computation on user login
 * - Automatic cache invalidation on role changes
 */
export function useAuthorization() {
  const user = useAppSelector((state) => state.auth.user);
  const accessToken = useAppSelector((state) => state.auth.accessToken);
  
  // Use string comparison to prevent unnecessary re-renders when array reference changes but content is same
  const userRolesString = useMemo(() => user?.roles?.join(',') || '', [user?.roles?.join(',')]);
  const userRoles = useMemo(() => {
    if (!user?.roles || user.roles.length === 0) return [];
    return user.roles as Role[];
  }, [userRolesString]);

  // Pre-compute permissions on user login or role change (performance optimization)
  useEffect(() => {
    if (user?.id && userRoles.length > 0) {
      // Check if roles changed to invalidate cache
      if (permissionCache.hasRolesChanged(user.id, userRoles)) {
        // Pre-compute all permissions once for this user
        permissionCache.precomputePermissions(user.id, userRoles);
      }
    } else if (user?.id) {
      // User logged out or no roles
      permissionCache.invalidate(user.id);
    }
  }, [user?.id, userRolesString]);

  // Cached permission check function
  const cachedHasPermission = useMemo(() => {
    return (permission: Permission): boolean => {
      if (!user?.id || userRoles.length === 0) return false;
      
      // Try cache first (O(1) lookup)
      const cached = permissionCache.get(user.id, permission);
      if (cached !== undefined) {
        return cached;
      }
      
      // Fallback to calculation if cache miss
      const hasAccess = hasPermission(userRoles, permission);
      permissionCache.set(user.id, permission, hasAccess);
      return hasAccess;
    };
  }, [user?.id, userRolesString]);

  return useMemo(
    () => ({
      user,
      userRoles: userRoles as Role[],
      // Enterprise-level: User must be validated by backend AND token must exist
      // This follows "Never trust client-side authorization" rule
      // User is only set after successful validation via /api/v1/auth/me
      isAuthenticated: !!user && !!accessToken,
      hasRole: (role: Role) => hasRole(userRoles as Role[], role),
      hasAnyRole: (roles: Role[]) => hasAnyRole(userRoles as Role[], roles),
      hasAllRoles: (roles: Role[]) => hasAllRoles(userRoles as Role[], roles),
      hasPermission: cachedHasPermission, // Use cached version
      hasAnyPermission: (permissions: Permission[]) => hasAnyPermission(userRoles as Role[], permissions),
    }),
    [user, accessToken, userRoles, cachedHasPermission]
  );
}

