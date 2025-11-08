import { apiClient } from '../client';
import type { User, AuthCredentials, AuthResponse } from '@synapse/types/domain';

export const authService = {
  login: async (credentials: AuthCredentials): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/auth/login', credentials);
    return response.data;
  },

  register: async (credentials: AuthCredentials): Promise<AuthResponse> => {
    const response = await apiClient.post<AuthResponse>('/auth/register', credentials);
    return response.data;
  },

  getCurrentUser: async (): Promise<User> => {
    const response = await apiClient.get<User>('/auth/me');
    return response.data;
  },

  logout: (): void => {
    localStorage.removeItem('auth_token');
  },
};
