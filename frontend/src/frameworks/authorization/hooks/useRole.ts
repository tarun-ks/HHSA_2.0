import { useMemo } from 'react';
import { useAppSelector } from '../../../store/hooks';
import { Role } from '../roles/roleDefinitions';
import { hasRole, hasAnyRole, hasAllRoles } from '../utils/roleUtils';

/**
 * Hook for role checking.
 * Simplified version focused on roles only.
 */
export function useRole() {
  const user = useAppSelector((state) => state.auth.user);
  const userRoles = useMemo(() => (user?.roles || []) as Role[], [user?.roles]);

  return useMemo(
    () => ({
      userRoles,
      hasRole: (role: Role) => hasRole(userRoles, role),
      hasAnyRole: (roles: Role[]) => hasAnyRole(userRoles, roles),
      hasAllRoles: (roles: Role[]) => hasAllRoles(userRoles, roles),
    }),
    [userRoles]
  );
}

