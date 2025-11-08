import { apiClient } from '../client';
import type { Directory, CreateDirectoryData, UpdateDirectoryData } from '@synapse/types/domain';

export const directoriesService = {
  getAll: async (): Promise<Directory[]> => {
    const response = await apiClient.get<Directory[]>('/directories');
    return response.data;
  },

  getById: async (id: string): Promise<Directory> => {
    const response = await apiClient.get<Directory>(`/directories/${id}`);
    return response.data;
  },

  getRoot: async (): Promise<Directory[]> => {
    const response = await apiClient.get<Directory[]>('/directories/root');
    return response.data;
  },

  getChildren: async (id: string): Promise<Directory[]> => {
    const response = await apiClient.get<Directory[]>(`/directories/${id}/children`);
    return response.data;
  },

  create: async (data: CreateDirectoryData): Promise<Directory> => {
    const response = await apiClient.post<Directory>('/directories', data);
    return response.data;
  },

  update: async (id: string, data: UpdateDirectoryData): Promise<Directory> => {
    const response = await apiClient.put<Directory>(`/directories/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/directories/${id}`);
  },
};
