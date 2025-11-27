import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse, InternalAxiosRequestConfig } from 'axios';

/**
 * Centralized API client for all API calls.
 * Handles authentication, error handling, and request/response interceptors.
 */
class ApiClient {
  private client: AxiosInstance;
  private baseURL: string;
  private refreshPromise: Promise<string | null> | null = null;
  private cachedToken: string | null = null;

  constructor() {
    // Default to auth service, but can be overridden via env variable
    this.baseURL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';
    this.client = axios.create({
      baseURL: this.baseURL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  /**
   * Setup request and response interceptors
   */
  private setupInterceptors(): void {
    // Request interceptor - Add auth token
    this.client.interceptors.request.use(
      async (config: InternalAxiosRequestConfig) => {
        let token = this.getAuthToken();
        
        // Check if token is expired and refresh if needed
        if (token && this.isTokenExpired(token)) {
          this.invalidateTokenCache();
          token = await this.refreshToken();
        }
        
        if (token && config.headers) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor - Handle errors and token refresh
    this.client.interceptors.response.use(
      (response: AxiosResponse) => {
        return response;
      },
      async (error) => {
        const originalRequest = error.config;

        // Handle 401 Unauthorized - Try token refresh
        if (error.response?.status === 401 && !originalRequest._retry) {
          originalRequest._retry = true;

          try {
            const newToken = await this.refreshToken();
            if (newToken && originalRequest.headers) {
              originalRequest.headers.Authorization = `Bearer ${newToken}`;
              return this.client(originalRequest);
            }
          } catch (refreshError) {
            // Refresh failed - redirect to login
            this.handleLogout();
            return Promise.reject(refreshError);
          }
        }

        return Promise.reject(error);
      }
    );
  }

  /**
   * Get authentication token from storage (with caching)
   */
  private getAuthToken(): string | null {
    if (this.cachedToken) {
      return this.cachedToken;
    }
    this.cachedToken = localStorage.getItem('accessToken');
    return this.cachedToken;
  }

  /**
   * Invalidate token cache (call when token changes)
   */
  private invalidateTokenCache(): void {
    this.cachedToken = null;
  }

  /**
   * Check if token is expired
   */
  private isTokenExpired(token: string | null): boolean {
    if (!token) return true;
    
    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const expirationTime = payload.exp * 1000; // Convert to milliseconds
      return expirationTime < Date.now();
    } catch {
      return true; // If can't decode, consider expired
    }
  }

  /**
   * Refresh authentication token (with race condition protection)
   */
  private async refreshToken(): Promise<string | null> {
    // If refresh is already in progress, return the existing promise
    if (this.refreshPromise) {
      return this.refreshPromise;
    }

    // Create new refresh promise
    this.refreshPromise = this.performRefresh();
    
    try {
      const result = await this.refreshPromise;
      return result;
    } finally {
      // Clear promise after completion
      this.refreshPromise = null;
    }
  }

  /**
   * Perform the actual token refresh
   */
  private async performRefresh(): Promise<string | null> {
    try {
      const refreshToken = localStorage.getItem('refreshToken');
      if (!refreshToken) {
        throw new Error('No refresh token available');
      }

      const response = await axios.post(`${this.baseURL}/api/v1/auth/refresh`, {
        refreshToken,
      });

      const { accessToken, refreshToken: newRefreshToken } = response.data.data;
      
      if (accessToken) {
        localStorage.setItem('accessToken', accessToken);
        this.invalidateTokenCache(); // Clear cache to force reload
        this.cachedToken = accessToken; // Update cache
        
        if (newRefreshToken) {
          localStorage.setItem('refreshToken', newRefreshToken);
        }
        return accessToken;
      }

      return null;
    } catch (error) {
      console.error('Token refresh failed:', error);
      this.invalidateTokenCache();
      return null;
    }
  }

  /**
   * Handle logout
   */
  private handleLogout(): void {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    this.invalidateTokenCache();
    this.refreshPromise = null; // Clear any pending refresh
    window.location.href = '/login';
  }

  /**
   * Get the axios instance
   */
  public getClient(): AxiosInstance {
    return this.client;
  }

  /**
   * GET request
   */
  public async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.get<T>(url, config);
    return response.data;
  }

  /**
   * POST request
   */
  public async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.post<T>(url, data, config);
    return response.data;
  }

  /**
   * PUT request
   */
  public async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.put<T>(url, data, config);
    return response.data;
  }

  /**
   * DELETE request
   */
  public async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.delete<T>(url, config);
    return response.data;
  }

  /**
   * PATCH request
   */
  public async patch<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.patch<T>(url, data, config);
    return response.data;
  }
}

// Export singleton instance
export const apiClient = new ApiClient();

// Export types
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
  error?: {
    code: string;
    description: string;
  };
}

export interface PageResponse<T> {
  content: T[];
  metadata: {
    page: number;
    size: number;
    totalElements: number;
    totalPages: number;
    first: boolean;
    last: boolean;
    hasNext: boolean;
    hasPrevious: boolean;
  };
}

