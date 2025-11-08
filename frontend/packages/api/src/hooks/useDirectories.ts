import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { directoriesService } from '../services';
import { queryKeys } from '../query-keys';
import type { CreateDirectoryData, UpdateDirectoryData } from '@synapse/types/domain';

export const useDirectories = () => {
  const queryClient = useQueryClient();

  const directoriesQuery = useQuery({
    queryKey: queryKeys.directories.list(),
    queryFn: directoriesService.getAll,
  });

  const rootDirectoriesQuery = useQuery({
    queryKey: queryKeys.directories.root(),
    queryFn: directoriesService.getRoot,
  });

  const createMutation = useMutation({
    mutationFn: directoriesService.create,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.directories.all });
    },
  });

  const updateMutation = useMutation({
    mutationFn: ({ id, data }: { id: string; data: UpdateDirectoryData }) =>
      directoriesService.update(id, data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.directories.all });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: directoriesService.delete,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: queryKeys.directories.all });
    },
  });

  return {
    directories: directoriesQuery.data ?? [],
    rootDirectories: rootDirectoriesQuery.data ?? [],
    isLoading: directoriesQuery.isLoading || rootDirectoriesQuery.isLoading,
    error: directoriesQuery.error || rootDirectoriesQuery.error,
    createDirectory: (data: CreateDirectoryData) => createMutation.mutateAsync(data),
    updateDirectory: (id: string, data: UpdateDirectoryData) =>
      updateMutation.mutateAsync({ id, data }),
    deleteDirectory: (id: string) => deleteMutation.mutateAsync(id),
  };
};
