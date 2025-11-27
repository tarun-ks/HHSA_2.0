import { ReactNode } from 'react';
import { SearchBar, type SearchFilter } from './SearchBar';

export interface ListPageLayoutProps {
  title: string;
  subtitle?: string;
  searchPlaceholder?: string;
  searchFilters?: SearchFilter[];
  onSearch: (searchTerm: string, filters: Record<string, any>) => void;
  onReset?: () => void;
  actions?: ReactNode;
  children: ReactNode;
  className?: string;
}

/**
 * Enterprise-grade List Page Layout
 * Framework-based: Reusable pattern for any list screen
 * 
 * Provides:
 * - Consistent header with title and actions
 * - Integrated search bar
 * - Filter support
 * - Modern, enterprise UI
 */
export const ListPageLayout = ({
  title,
  subtitle,
  searchPlaceholder,
  searchFilters,
  onSearch,
  onReset,
  actions,
  children,
  className = '',
}: ListPageLayoutProps) => {
  return (
    <div className={`space-y-6 ${className}`}>
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">{title}</h1>
          {subtitle && (
            <p className="text-sm text-gray-500 dark:text-gray-400 mt-1">{subtitle}</p>
          )}
        </div>
        {actions && <div className="flex-shrink-0">{actions}</div>}
      </div>

      {/* Search Bar */}
      {(searchPlaceholder || searchFilters) && (
        <SearchBar
          placeholder={searchPlaceholder}
          filters={searchFilters}
          onSearch={onSearch}
          onReset={onReset}
          showAdvancedFilters={!!searchFilters && searchFilters.length > 0}
        />
      )}

      {/* Content */}
      {children}
    </div>
  );
};

