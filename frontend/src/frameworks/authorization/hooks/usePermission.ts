import { useMemo } from 'react';
import { useAppSelector } from '../../../store/hooks';
import { Permission } from '../roles/rolePermissions';
import { hasPermission, hasAnyPermission } from '../utils/roleUtils';

/**
 * Hook for permission checking.
 * Simplified version focused on permissions only.
 */
export function usePermission() {
  const user = useAppSelector((state) => state.auth.user);
  const userRoles = useMemo(() => (user?.roles || []) as any[], [user?.roles]);

  return useMemo(
    () => ({
      hasPermission: (permission: Permission) => hasPermission(userRoles, permission),
      hasAnyPermission: (permissions: Permission[]) => hasAnyPermission(userRoles, permissions),
    }),
    [userRoles]
  );
}

