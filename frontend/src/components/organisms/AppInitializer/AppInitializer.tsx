import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAppDispatch, useAppSelector } from '../../../store/hooks';
import { setCredentials, logout } from '../../../store/slices/authSlice';
import { authService } from '../../../services/authService';
import { useToast } from '../../../hooks/useToast';
import { Loader } from '../../atoms/Loader';

/**
 * AppInitializer Component
 * 
 * Enterprise-level authentication initialization that validates tokens with backend.
 * 
 * Security Features:
 * - Validates JWT token with Keycloak on app initialization
 * - Never trusts client-side data (follows "Never trust client-side authorization" rule)
 * - Handles expired/invalid tokens gracefully
 * - Populates Redux store only after backend validation
 * 
 * Architecture Alignment:
 * - Uses existing authService.getCurrentUser() (no custom auth code)
 * - Uses Redux for state management
 * - Follows "Validate tokens on every request" principle
 * - Production-grade, enterprise-level security
 */
export const AppInitializer = ({ children }: { children: React.ReactNode }) => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const toast = useToast();
  const [isInitializing, setIsInitializing] = useState(true);
  
  const accessToken = useAppSelector((state) => state.auth.accessToken);
  const user = useAppSelector((state) => state.auth.user);

  useEffect(() => {
    const initializeAuth = async () => {
      try {
        // Check if token exists in localStorage (from previous session)
        const storedToken = localStorage.getItem('accessToken');
        const storedRefreshToken = localStorage.getItem('refreshToken');
        const storedUser = localStorage.getItem('user');

        // If no token, user is not authenticated
        if (!storedToken) {
          setIsInitializing(false);
          return;
        }

        // If we already have user in Redux store, skip validation (already validated)
        if (user && accessToken) {
          setIsInitializing(false);
          return;
        }

        // Validate token with backend (Keycloak validation)
        // This follows "Never trust client-side authorization" rule
        // The /api/v1/auth/me endpoint validates JWT with Keycloak's public keys
        // Spring Security validates the token before the endpoint is called
        try {
          // Use apiClient directly to ensure token is sent in Authorization header
          // The apiClient interceptor will add the token from localStorage
          const response = await authService.getCurrentUser(storedToken);

          if (response.success && response.data) {
            // Token is valid - populate Redux store with validated user
            // Backend has already validated the JWT token with Keycloak
            dispatch(
              setCredentials({
                user: response.data,
                accessToken: storedToken,
                refreshToken: storedRefreshToken || '',
              })
            );
          } else {
            // Token validation failed
            throw new Error('Token validation failed');
          }
        } catch (error: any) {
          // Token is invalid or expired (401, 403, etc.)
          console.warn('Token validation failed on app init:', error);

          // Clear invalid tokens and user data
          dispatch(logout());
          localStorage.removeItem('accessToken');
          localStorage.removeItem('refreshToken');
          localStorage.removeItem('user');

          // Only redirect to login if not already on login page
          if (location.pathname !== '/login') {
            navigate('/login', { replace: true });
          }
        }
      } catch (error) {
        console.error('Error during app initialization:', error);
        // On unexpected errors, clear auth state
        dispatch(logout());
      } finally {
        setIsInitializing(false);
      }
    };

    // Listen for logout events from apiClient (when token refresh fails)
    const handleLogoutEvent = (event: CustomEvent) => {
      dispatch(logout());
      if (location.pathname !== '/login') {
        navigate('/login', { replace: true });
      }
    };

    window.addEventListener('auth:logout', handleLogoutEvent as EventListener);

    initializeAuth();

    return () => {
      window.removeEventListener('auth:logout', handleLogoutEvent as EventListener);
    };
  }, [dispatch, navigate, location.pathname, user, accessToken]);

  // Show loading state during initialization
  if (isInitializing) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50 dark:bg-gray-900">
        <div className="text-center">
          <Loader size="lg" />
          <p className="mt-4 text-sm text-gray-600 dark:text-gray-400">
            Initializing application...
          </p>
        </div>
      </div>
    );
  }

  return <>{children}</>;
};

