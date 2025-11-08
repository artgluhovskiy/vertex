import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { notesService } from '../services';
import { queryKeys } from '../query-keys';
import type { CreateNoteData, UpdateNoteData } from '@synapse/types/domain';

export const useNotes = () => {
  const queryClient = useQueryClient();

  const notesQuery = useQuery({
    queryKey: queryKeys.notes.list(),
    queryFn: notesService.getAll,
  });

  const createNoteMutation = useMutation({
    mutationFn: notesService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.notes.all });
    },
  });

  const updateNoteMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateNoteData }) =>
      notesService.update(id, data),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: queryKeys.notes.detail(variables.id) });
      queryClient.invalidateQueries({ queryKey: queryKeys.notes.all });
    },
  });

  const deleteNoteMutation = useMutation({
    mutationFn: notesService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.notes.all });
    },
  });

  return {
    notes: notesQuery.data ?? [],
    isLoading: notesQuery.isLoading,
    error: notesQuery.error,
    createNote: (data: CreateNoteData) => createNoteMutation.mutateAsync(data),
    updateNote: (id: string, data: UpdateNoteData) =>
      updateNoteMutation.mutateAsync({ id, data }),
    deleteNote: (id: string) => deleteNoteMutation.mutateAsync(id),
  };
};

export const useNote = (id: string) => {
  const queryClient = useQueryClient();

  const noteQuery = useQuery({
    queryKey: queryKeys.notes.detail(id),
    queryFn: () => notesService.getById(id),
    enabled: !!id,
  });

  const updateMutation = useMutation({
    mutationFn: (data: UpdateNoteData) => notesService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.notes.detail(id) });
      queryClient.invalidateQueries({ queryKey: queryKeys.notes.all });
    },
  });

  return {
    note: noteQuery.data,
    isLoading: noteQuery.isLoading,
    error: noteQuery.error,
    updateNote: (data: UpdateNoteData) => updateMutation.mutateAsync(data),
  };
};
