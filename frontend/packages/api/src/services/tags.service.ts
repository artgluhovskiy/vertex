import { apiClient } from '../client';
import type { Tag, CreateTagData, UpdateTagData } from '@synapse/types/domain';

// Note: Tag endpoints not yet implemented in backend
export const tagsService = {
  getAll: async (): Promise<Tag[]> => {
    // TODO: Implement when backend endpoint is ready
    const response = await apiClient.get<Tag[]>('/tags');
    return response.data;
  },

  create: async (data: CreateTagData): Promise<Tag> => {
    const response = await apiClient.post<Tag>('/tags', data);
    return response.data;
  },

  update: async (id: string, data: UpdateTagData): Promise<Tag> => {
    const response = await apiClient.put<Tag>(`/tags/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/tags/${id}`);
  },
};
