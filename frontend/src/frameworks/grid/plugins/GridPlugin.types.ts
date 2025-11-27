import { ReactNode } from 'react';

/**
 * Props passed to grid plugins
 */
export interface GridPluginProps<T> {
  /** Current data rows */
  data: T[];
  /** Column definitions */
  columns: any[];
  /** Callback when data changes */
  onChange?: (data: T[]) => void;
  /** Additional plugin-specific props */
  [key: string]: any;
}

/**
 * Grid plugin interface
 */
export interface GridPlugin<T = any> {
  /** Unique plugin name */
  name: string;
  /** Plugin priority (lower = higher priority, executed first) */
  priority?: number;
  /** Render toolbar buttons */
  renderToolbar?: (props: GridPluginProps<T>) => ReactNode;
  /** Render header content */
  renderHeader?: (props: GridPluginProps<T>) => ReactNode;
  /** Render footer content */
  renderFooter?: (props: GridPluginProps<T>) => ReactNode;
  /** Callback when data changes */
  onDataChange?: (data: T[], props: GridPluginProps<T>) => void;
  /** Callback when row is selected */
  onRowSelect?: (row: T, index: number, props: GridPluginProps<T>) => void;
  /** Plugin-specific configuration */
  config?: Record<string, any>;
}

/**
 * Plugin registry for managing plugins
 */
export class PluginRegistry {
  private plugins: Map<string, GridPlugin> = new Map();

  /**
   * Register a plugin
   */
  register<T>(plugin: GridPlugin<T>): void {
    this.plugins.set(plugin.name, plugin);
  }

  /**
   * Get a plugin by name
   */
  get(name: string): GridPlugin | undefined {
    return this.plugins.get(name);
  }

  /**
   * Get all plugins sorted by priority
   */
  getAll(): GridPlugin[] {
    return Array.from(this.plugins.values()).sort((a, b) => 
      (a.priority || 100) - (b.priority || 100)
    );
  }

  /**
   * Remove a plugin
   */
  remove(name: string): void {
    this.plugins.delete(name);
  }

  /**
   * Clear all plugins
   */
  clear(): void {
    this.plugins.clear();
  }
}

// Global plugin registry instance
export const pluginRegistry = new PluginRegistry();

