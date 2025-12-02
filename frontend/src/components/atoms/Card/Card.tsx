import { ReactNode } from 'react';
import { cn } from '../../../utils/cn';

interface CardProps {
  children: ReactNode;
  className?: string;
  hover?: boolean;
  padding?: 'none' | 'sm' | 'md' | 'lg';
}

/**
 * Reusable Card component with consistent styling.
 * Uses theme configuration for easy customization.
 */
export const Card = ({ 
  children, 
  className, 
  hover = false,
  padding = 'md',
}: CardProps) => {
  const paddingClasses = {
    none: '',
    sm: 'p-4',
    md: 'p-6',
    lg: 'p-8',
  };

  return (
    <div
      className={cn(
        'card',
        hover && 'card-hover',
        paddingClasses[padding],
        className
      )}
    >
      {children}
    </div>
  );
};

