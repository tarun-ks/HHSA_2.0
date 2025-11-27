import { ReactNode, Suspense } from 'react';
import { Loader } from '../../../components/atoms/Loader';

export interface LazyTabContentProps {
  children: ReactNode;
  fallback?: ReactNode;
}

/**
 * LazyTabContent - Wrapper for async tab content
 * Shows loading state while content is being loaded
 */
export const LazyTabContent = ({ children, fallback }: LazyTabContentProps) => {
  const defaultFallback = (
    <div className="flex justify-center items-center py-12">
      <Loader size="md" />
      <span className="ml-3 text-sm text-gray-500 dark:text-gray-400">Loading...</span>
    </div>
  );

  return <Suspense fallback={fallback || defaultFallback}>{children}</Suspense>;
};

