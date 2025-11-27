import { apiClient, ApiResponse } from './apiClient';

export interface LoginRequest {
  username: string;
  password: string;
  provider?: 'keycloak' | 'local'; // Optional for backward compatibility, defaults to 'keycloak'
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  expiresAt: string;
  user: {
    id: string;
    username: string;
    email: string;
    firstName?: string;
    lastName?: string;
    roles?: string[];
  };
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

/**
 * Authentication service.
 * Handles login, logout, token refresh, and user info.
 */
export const authService = {
  /**
   * Login user
   */
  async login(credentials: LoginRequest): Promise<ApiResponse<LoginResponse>> {
    return apiClient.post<ApiResponse<LoginResponse>>('/api/v1/auth/login', credentials);
  },

  /**
   * Refresh access token
   */
  async refreshToken(refreshToken: string): Promise<ApiResponse<LoginResponse>> {
    return apiClient.post<ApiResponse<LoginResponse>>('/api/v1/auth/refresh', {
      refreshToken,
    });
  },

  /**
   * Logout user
   */
  async logout(token: string): Promise<ApiResponse<void>> {
    return apiClient.post<ApiResponse<void>>('/api/v1/auth/logout', null, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  },

  /**
   * Get current user info
   */
  async getCurrentUser(token: string): Promise<ApiResponse<LoginResponse['user']>> {
    return apiClient.get<ApiResponse<LoginResponse['user']>>('/api/v1/auth/me', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  },
};




