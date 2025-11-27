import { ReactNode } from 'react';
import { RoleGate } from './RoleGate';
import { Permission } from '../roles/rolePermissions';

interface ProtectedComponentProps {
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
 * ProtectedComponent - alias for RoleGate for semantic clarity.
 * Use this when you want to conditionally render a component.
 */
export function ProtectedComponent(props: ProtectedComponentProps) {
  return <RoleGate {...props} />;
}

