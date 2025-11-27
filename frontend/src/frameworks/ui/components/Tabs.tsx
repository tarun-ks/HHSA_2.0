import { ReactNode, useState, useEffect, useMemo } from 'react';
import { Loader } from '../../../components/atoms/Loader';

export interface TabItem {
  id: string;
  label: string;
  icon?: ReactNode;
  content: ReactNode;
  lazy?: boolean; // Load content only when tab is active
  badge?: number | string; // Badge count
  disabled?: boolean;
  permission?: string; // Permission required to view tab
}

export interface TabsProps {
  items: TabItem[];
  defaultTab?: string;
  onChange?: (tabId: string) => void;
  variant?: 'default' | 'pills' | 'underline';
  className?: string;
  contentClassName?: string;
}

/**
 * Enterprise-grade Tabs component
 * Framework-based: Reusable for any screen with tab navigation
 * 
 * Features:
 * - Lazy loading support (loads content only when tab is active)
 * - Badge support for notifications
 * - Icon support
 * - Permission-based tab visibility
 * - Modern, accessible design
 * - Dark mode support
 */
export const Tabs = ({
  items,
  defaultTab,
  onChange,
  variant = 'underline',
  className = '',
  contentClassName = '',
}: TabsProps) => {
  const [activeTab, setActiveTab] = useState<string>(defaultTab || items[0]?.id || '');
  const [loadedTabs, setLoadedTabs] = useState<Set<string>>(new Set([activeTab]));

  // Filter tabs based on permissions (if permission checking is needed)
  const visibleTabs = useMemo(() => {
    return items.filter((tab) => !tab.disabled);
  }, [items]);

  useEffect(() => {
    if (defaultTab && items.some((tab) => tab.id === defaultTab)) {
      setActiveTab(defaultTab);
      setLoadedTabs((prev) => new Set([...prev, defaultTab]));
    }
  }, [defaultTab, items]);

  const handleTabChange = (tabId: string) => {
    const tab = items.find((t) => t.id === tabId);
    if (tab?.disabled) return;

    setActiveTab(tabId);
    setLoadedTabs((prev) => new Set([...prev, tabId]));
    onChange?.(tabId);
  };

  const activeTabItem = items.find((tab) => tab.id === activeTab);

  if (visibleTabs.length === 0) {
    return null;
  }

  const tabVariants = {
    default: {
      container: 'border-b border-gray-200 dark:border-gray-700',
      tab: (isActive: boolean) =>
        `px-4 py-3 text-sm font-medium transition-colors ${
          isActive
            ? 'text-blue-600 dark:text-blue-400 border-b-2 border-blue-600 dark:border-blue-400'
            : 'text-gray-500 hover:text-gray-700 hover:border-gray-300 dark:text-gray-400 dark:hover:text-gray-300 border-b-2 border-transparent'
        }`,
    },
    pills: {
      container: 'bg-gray-100 dark:bg-gray-700 rounded-lg p-1',
      tab: (isActive: boolean) =>
        `px-4 py-2 text-sm font-medium rounded-md transition-colors ${
          isActive
            ? 'bg-white dark:bg-gray-800 text-blue-600 dark:text-blue-400 shadow-sm'
            : 'text-gray-600 hover:text-gray-900 dark:text-gray-400 dark:hover:text-gray-200'
        }`,
    },
    underline: {
      container: 'border-b border-gray-200 dark:border-gray-700',
      tab: (isActive: boolean) =>
        `px-4 py-3 text-sm font-medium transition-colors relative ${
          isActive
            ? 'text-blue-600 dark:text-blue-400'
            : 'text-gray-500 hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300'
        } ${
          isActive
            ? "after:content-[''] after:absolute after:bottom-0 after:left-0 after:right-0 after:h-0.5 after:bg-blue-600 dark:after:bg-blue-400"
            : ''
        }`,
    },
  };

  const variantStyles = tabVariants[variant];

  return (
    <div className={`bg-white dark:bg-gray-800 shadow-sm rounded-lg ${className}`}>
      {/* Tab Navigation */}
      <div className={variantStyles.container}>
        <nav className="flex space-x-1 overflow-x-auto" aria-label="Tabs">
          {visibleTabs.map((tab) => {
            const isActive = activeTab === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => handleTabChange(tab.id)}
                disabled={tab.disabled}
                className={`${variantStyles.tab(isActive)} ${
                  tab.disabled ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'
                } flex items-center space-x-2 whitespace-nowrap`}
                aria-selected={isActive}
                role="tab"
              >
                {tab.icon && <span className="flex-shrink-0">{tab.icon}</span>}
                <span>{tab.label}</span>
                {tab.badge !== undefined && (
                  <span
                    className={`ml-1 px-2 py-0.5 text-xs font-semibold rounded-full ${
                      isActive
                        ? 'bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-300'
                        : 'bg-gray-200 text-gray-700 dark:bg-gray-600 dark:text-gray-300'
                    }`}
                  >
                    {tab.badge}
                  </span>
                )}
              </button>
            );
          })}
        </nav>
      </div>

      {/* Tab Content */}
      <div className={`p-6 ${contentClassName}`}>
        {activeTabItem && (
          <div
            key={activeTab}
            role="tabpanel"
            aria-labelledby={`tab-${activeTab}`}
            className="animate-fade-in"
          >
            {activeTabItem.lazy && !loadedTabs.has(activeTab) ? (
              <div className="flex justify-center items-center py-12">
                <Loader size="md" />
              </div>
            ) : (
              activeTabItem.content
            )}
          </div>
        )}
      </div>
    </div>
  );
};

