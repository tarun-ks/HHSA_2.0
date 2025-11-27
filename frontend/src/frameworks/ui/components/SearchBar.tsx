import { useState, useCallback, useEffect } from 'react';
import { Input } from '../../../components/atoms/Input';
import { Button } from '../../../components/atoms/Button';

export interface SearchFilter {
  key: string;
  label: string;
  type: 'text' | 'select' | 'date' | 'dateRange' | 'number';
  placeholder?: string;
  options?: { value: string; label: string }[];
}

export interface SearchBarProps {
  onSearch: (searchTerm: string, filters: Record<string, any>) => void;
  onReset?: () => void;
  placeholder?: string;
  filters?: SearchFilter[];
  showAdvancedFilters?: boolean;
  debounceMs?: number;
  className?: string;
}

/**
 * Enterprise-grade SearchBar component
 * Framework-based: Reusable for any list screen
 * 
 * Features:
 * - Real-time search with debouncing
 * - Advanced filters support
 * - Reset functionality
 * - Modern, accessible design
 * - Dark mode support
 */
export const SearchBar = ({
  onSearch,
  onReset,
  placeholder = 'Search...',
  filters = [],
  showAdvancedFilters = false,
  debounceMs = 300,
  className = '',
}: SearchBarProps) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [filterValues, setFilterValues] = useState<Record<string, any>>({});
  const [showFilters, setShowFilters] = useState(showAdvancedFilters);

  // Debounced search
  useEffect(() => {
    const timer = setTimeout(() => {
      onSearch(searchTerm, filterValues);
    }, debounceMs);

    return () => clearTimeout(timer);
  }, [searchTerm, filterValues, debounceMs, onSearch]);

  const handleFilterChange = useCallback((key: string, value: any) => {
    setFilterValues((prev) => ({
      ...prev,
      [key]: value || undefined,
    }));
  }, []);

  const handleReset = useCallback(() => {
    setSearchTerm('');
    setFilterValues({});
    onReset?.();
    onSearch('', {});
  }, [onReset, onSearch]);

  return (
    <div className={`bg-white dark:bg-gray-800 shadow-sm rounded-lg p-4 ${className}`}>
      {/* Main Search Bar */}
      <div className="flex items-center space-x-3">
        <div className="flex-1">
          <div className="relative">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <svg
                className="h-5 w-5 text-gray-400"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                />
              </svg>
            </div>
            <Input
              type="text"
              placeholder={placeholder}
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="pl-10"
            />
          </div>
        </div>

        {filters.length > 0 && (
          <Button
            variant="outline"
            size="sm"
            onClick={() => setShowFilters(!showFilters)}
            className="whitespace-nowrap"
          >
            <svg
              className="h-4 w-4 mr-2"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M3 4a1 1 0 011-1h16a1 1 0 011 1v2.586a1 1 0 01-.293.707l-6.414 6.414a1 1 0 00-.293.707V17l-4 4v-6.586a1 1 0 00-.293-.707L3.293 7.293A1 1 0 013 6.586V4z"
              />
            </svg>
            Filters
            {Object.keys(filterValues).filter((key) => filterValues[key]).length > 0 && (
              <span className="ml-2 px-2 py-0.5 text-xs font-semibold rounded-full bg-blue-100 text-blue-800 dark:bg-blue-900/30 dark:text-blue-300">
                {Object.keys(filterValues).filter((key) => filterValues[key]).length}
              </span>
            )}
          </Button>
        )}

        {(searchTerm || Object.keys(filterValues).some((key) => filterValues[key])) && (
          <Button variant="ghost" size="sm" onClick={handleReset}>
            Clear
          </Button>
        )}
      </div>

      {/* Advanced Filters Panel */}
      {showFilters && filters.length > 0 && (
        <div className="mt-4 pt-4 border-t border-gray-200 dark:border-gray-700">
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {filters.map((filter) => (
              <div key={filter.key}>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  {filter.label}
                </label>
                {filter.type === 'text' && (
                  <Input
                    type="text"
                    placeholder={filter.placeholder}
                    value={filterValues[filter.key] || ''}
                    onChange={(e) => handleFilterChange(filter.key, e.target.value)}
                  />
                )}
                {filter.type === 'select' && (
                  <select
                    value={filterValues[filter.key] || ''}
                    onChange={(e) => handleFilterChange(filter.key, e.target.value)}
                    className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                  >
                    <option value="">All</option>
                    {filter.options?.map((option) => (
                      <option key={option.value} value={option.value}>
                        {option.label}
                      </option>
                    ))}
                  </select>
                )}
                {filter.type === 'date' && (
                  <Input
                    type="date"
                    value={filterValues[filter.key] || ''}
                    onChange={(e) => handleFilterChange(filter.key, e.target.value)}
                  />
                )}
                {filter.type === 'number' && (
                  <Input
                    type="number"
                    placeholder={filter.placeholder}
                    value={filterValues[filter.key] || ''}
                    onChange={(e) => handleFilterChange(filter.key, e.target.value)}
                  />
                )}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

