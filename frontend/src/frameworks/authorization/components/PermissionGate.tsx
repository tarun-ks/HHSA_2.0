import { ReactNode, ReactElement, Children, isValidElement, useMemo, cloneElement } from 'react';
import { useAuthorization } from '../hooks/useAuthorization';
import { Permission } from '../roles/rolePermissions';

export type PermissionMode = 'hide' | 'readonly' | 'disabled';

interface PermissionGateProps {
  /** Permission required to render children */
  permission?: Permission;
  /** Roles required to render children (alternative to permission) */
  roles?: string[];
  /** Whether user must have ALL roles (default: false, any role) */
  requireAll?: boolean;
  /** Mode: hide (default), readonly, or disabled */
  mode?: PermissionMode;
  /** Children to render */
  children: ReactNode;
  /** Fallback to render if user doesn't have permission (only for hide mode) */
  fallback?: ReactNode;
}

/**
 * PermissionGate component - Enhanced version with hide/readonly/disabled modes.
 * Framework-based: Single component handles all permission-based UI states.
 * 
 * Features:
 * - hide: Hide element if no permission (default)
 * - readonly: Show element but make it readonly
 * - disabled: Show element but disable interactions
 * - Smart prop injection: Automatically detects child types and applies appropriate props
 * - Performance: Uses cached permission checks
 */
export function PermissionGate({
  permission,
  roles,
  requireAll = false,
  mode = 'hide',
  children,
  fallback = null,
}: PermissionGateProps) {
  const { hasPermission, hasAnyRole, hasAllRoles } = useAuthorization();

  // Check permission/role access
  const hasAccess = useMemo(() => {
    if (permission) {
      return hasPermission(permission);
    } else if (roles && roles.length > 0) {
      return requireAll ? hasAllRoles(roles as any[]) : hasAnyRole(roles as any[]);
    } else {
      // No permission or roles specified, allow access
      return true;
    }
  }, [permission, roles, requireAll, hasPermission, hasAnyRole, hasAllRoles]);

  // Mode: hide (default behavior - conditional rendering)
  if (mode === 'hide' || !mode) {
    return hasAccess ? <>{children}</> : <>{fallback}</>;
  }

  // Mode: readonly or disabled
  // If no access: apply readonly/disabled props
  // If has access: render normally (but can be overridden by contract state)
  if (mode === 'readonly' || mode === 'disabled') {
    if (!hasAccess) {
      // No permission - apply readonly/disabled props
      return (
        <>
          {Children.map(children, (child) => {
            if (isValidElement(child)) {
              const injectedProps = getInjectedProps(child, mode);
              return cloneElement(child as ReactElement<any>, injectedProps);
            }
            return child;
          })}
        </>
      );
    }
    // Has access - render normally
    return <>{children}</>;
  }

  // Default: render normally
  return <>{children}</>;
}

/**
 * Helper function to detect child type and return appropriate props
 * Smart detection: Works with buttons, inputs, tables, and any component
 */
function getInjectedProps(child: ReactElement, mode: PermissionMode): Record<string, any> {
  const props: Record<string, any> = {};
  const childProps = child.props || {};
  const componentName = typeof child.type === 'function' 
    ? (child.type as any).name || (child.type as any).displayName
    : String(child.type);

  // Check if it's a Button component (by name or props)
  const isButton = 
    componentName?.includes('Button') || 
    childProps.variant !== undefined ||
    childProps.onClick !== undefined ||
    child.type === 'button';

  // Check if it's an Input component (by name or props)
  const isInput = 
    componentName?.includes('Input') || 
    child.type === 'input' || 
    childProps.type !== undefined ||
    childProps.value !== undefined && childProps.onChange !== undefined;

  // Check if it's EditableTable
  const isEditableTable = 
    componentName?.includes('EditableTable') || 
    childProps.readOnly !== undefined ||
    (childProps.data !== undefined && childProps.onChange !== undefined);

  // Check if it's a checkbox
  const isCheckbox = 
    child.type === 'input' && childProps.type === 'checkbox' ||
    componentName?.includes('Checkbox');

  // Apply props based on component type and mode
  if (isButton) {
    props.disabled = mode === 'disabled' || mode === 'readonly';
  }

  if (isInput && !isCheckbox) {
    props.readOnly = mode === 'readonly';
    props.disabled = mode === 'disabled';
  }

  if (isCheckbox) {
    props.disabled = mode === 'disabled' || mode === 'readonly';
  }

  if (isEditableTable) {
    props.readOnly = mode === 'readonly';
    // EditableTable doesn't have disabled prop, so readonly is the equivalent
  }

  // Apply disabled styling for disabled mode
  if (mode === 'disabled') {
    const existingClassName = childProps.className || '';
    props.className = existingClassName
      ? `${existingClassName} opacity-50 cursor-not-allowed`
      : 'opacity-50 cursor-not-allowed';
  }

  // Preserve existing props and merge
  return { ...childProps, ...props };
}

