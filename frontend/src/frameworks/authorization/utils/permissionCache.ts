import { Permission, PERMISSIONS } from '../roles/rolePermissions';
import { Role } from '../roles/roleDefinitions';
import { hasPermission } from './roleUtils';

/**
 * Permission Cache for performance optimization.
 * Caches permission checks to avoid repeated calculations.
 * 
 * Performance benefits:
 * - O(1) lookup instead of O(n) calculation
 * - Pre-computed on user login
 * - Invalidated only on role/permission changes
 */
class PermissionCache {
  private cache: Map<string, Map<Permission, boolean>> = new Map();
  private userRolesCache: Map<string, Role[]> = new Map();

  /**
   * Get cached permission check for a user
   */
  get(userId: string, permission: Permission): boolean | undefined {
    return this.cache.get(userId)?.get(permission);
  }

  /**
   * Set cached permission check for a user
   */
  set(userId: string, permission: Permission, hasAccess: boolean): void {
    if (!this.cache.has(userId)) {
      this.cache.set(userId, new Map());
    }
    this.cache.get(userId)!.set(permission, hasAccess);
  }

  /**
   * Pre-compute all permissions for a user (batch operation)
   * This is called once on user login for optimal performance
   */
  precomputePermissions(userId: string, userRoles: Role[]): void {
    const permissionMap = new Map<Permission, boolean>();
    
    // Get all permissions from PERMISSIONS object
    const allPermissions = Object.keys(PERMISSIONS) as Permission[];
    
    // Compute each permission once
    allPermissions.forEach((permission) => {
      const hasAccess = hasPermission(userRoles, permission);
      permissionMap.set(permission, hasAccess);
    });

    this.cache.set(userId, permissionMap);
    this.userRolesCache.set(userId, userRoles);
  }

  /**
   * Check if user roles have changed (for cache invalidation)
   */
  hasRolesChanged(userId: string, userRoles: Role[]): boolean {
    const cachedRoles = this.userRolesCache.get(userId);
    if (!cachedRoles) return true;
    
    if (cachedRoles.length !== userRoles.length) return true;
    
    return !cachedRoles.every((role) => userRoles.includes(role));
  }

  /**
   * Invalidate cache for a user
   */
  invalidate(userId: string): void {
    this.cache.delete(userId);
    this.userRolesCache.delete(userId);
  }

  /**
   * Clear all caches
   */
  clear(): void {
    this.cache.clear();
    this.userRolesCache.clear();
  }
}

// Singleton instance
export const permissionCache = new PermissionCache();

