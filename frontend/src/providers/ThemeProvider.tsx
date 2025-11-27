import { useEffect } from 'react';
import { useAppSelector, useAppDispatch } from '../store/hooks';
import { setTheme } from '../store/slices/themeSlice';

interface ThemeProviderProps {
  children: React.ReactNode;
}

/**
 * Theme provider that applies theme to document on mount and changes.
 */
export const ThemeProvider = ({ children }: ThemeProviderProps) => {
  const theme = useAppSelector((state) => state.theme.mode);
  const dispatch = useAppDispatch();

  useEffect(() => {
    // Apply initial theme
    const initialTheme = localStorage.getItem('theme') || 
      (window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');
    
    dispatch(setTheme(initialTheme as 'light' | 'dark'));
  }, [dispatch]);

  useEffect(() => {
    // Apply theme class to document
    if (theme === 'dark') {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [theme]);

  return <>{children}</>;
}




