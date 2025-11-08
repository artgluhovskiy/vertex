import { apiClient } from '../client';
import type { Note, CreateNoteData, UpdateNoteData } from '@synapse/types/domain';

export const notesService = {
  getAll: async (): Promise<Note[]> => {
    const response = await apiClient.get<Note[]>('/notes');
    return response.data;
  },

  getById: async (id: string): Promise<Note> => {
    const response = await apiClient.get<Note>(`/notes/${id}`);
    return response.data;
  },

  create: async (data: CreateNoteData): Promise<Note> => {
    const response = await apiClient.post<Note>('/notes', data);
    return response.data;
  },

  update: async (id: string, data: UpdateNoteData): Promise<Note> => {
    const response = await apiClient.put<Note>(`/notes/${id}`, data);
    return response.data;
  },

  delete: async (id: string): Promise<void> => {
    await apiClient.delete(`/notes/${id}`);
  },
};
