import { useMemo, ReactNode } from 'react';
import { GridPlugin, GridPluginProps } from '../plugins/GridPlugin.types';

/**
 * Hook for managing grid plugins
 */
export function useGridPlugins<T>(
  plugins: GridPlugin<T>[] = []
) {
  return useMemo(() => {
    // Sort plugins by priority
    const sortedPlugins = [...plugins].sort((a, b) => 
      (a.priority || 100) - (b.priority || 100)
    );

    /**
     * Render all toolbar plugins
     */
    const renderToolbar = (props: GridPluginProps<T>): ReactNode[] => {
      return sortedPlugins
        .map((plugin) => plugin.renderToolbar?.(props))
        .filter((node): node is ReactNode => node != null);
    };

    /**
     * Render all header plugins
     */
    const renderHeader = (props: GridPluginProps<T>): ReactNode[] => {
      return sortedPlugins
        .map((plugin) => plugin.renderHeader?.(props))
        .filter((node): node is ReactNode => node != null);
    };

    /**
     * Render all footer plugins
     */
    const renderFooter = (props: GridPluginProps<T>): ReactNode[] => {
      return sortedPlugins
        .map((plugin) => plugin.renderFooter?.(props))
        .filter((node): node is ReactNode => node != null);
    };

    /**
     * Notify all plugins of data changes
     */
    const notifyDataChange = (data: T[], props: GridPluginProps<T>): void => {
      sortedPlugins.forEach((plugin) => {
        plugin.onDataChange?.(data, props);
      });
    };

    /**
     * Notify all plugins of row selection
     */
    const notifyRowSelect = (row: T, index: number, props: GridPluginProps<T>): void => {
      sortedPlugins.forEach((plugin) => {
        plugin.onRowSelect?.(row, index, props);
      });
    };

    return {
      plugins: sortedPlugins,
      renderToolbar,
      renderHeader,
      renderFooter,
      notifyDataChange,
      notifyRowSelect,
    };
  }, [plugins]);
}

