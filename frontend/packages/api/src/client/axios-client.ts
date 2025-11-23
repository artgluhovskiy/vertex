import axios, { type AxiosInstance } from 'axios';

// Use relative URL to leverage Vite's proxy configuration
// This avoids CORS issues during development
// In production, ensure VITE_API_BASE_URL is set to the full backend URL
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';

export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Request interceptor for adding auth token
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('auth_token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Response interceptor for handling errors
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Handle unauthorized - clear token
      // Don't redirect here to avoid infinite loops
      // Let React Query and the auth hook handle the redirect
      localStorage.removeItem('auth_token');
    }
    return Promise.reject(error);
  }
);
