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

  // TODO: Backend - This endpoint should return the single ROOT "Folders" directory
  // GET /api/v1/directories/root should return the directory where parentId = null
  // For now, we'll extract it from the flat list
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

  // Extract single ROOT directory from the list
  // TODO: Backend - After backend creates ROOT "Folders" on user registration,
  // this should always return exactly one directory with parentId = null
  const rootDirectory = directoriesQuery.data?.find((dir) => dir.parentId === null);

  // Get non-root directories (for building the tree)
  const userDirectories = directoriesQuery.data?.filter((dir) => dir.parentId !== null) ?? [];

  return {
    // All directories including ROOT
    directories: directoriesQuery.data ?? [],

    // User-created directories (excluding ROOT)
    userDirectories,

    // Single ROOT "Folders" directory (parentId = null)
    rootDirectory,

    // Legacy: keeping for backwards compatibility
    // TODO: Remove this after updating all consumers
    rootDirectories: rootDirectoriesQuery.data ?? [],

    isLoading: directoriesQuery.isLoading || rootDirectoriesQuery.isLoading,
    error: directoriesQuery.error || rootDirectoriesQuery.error,

    createDirectory: (data: CreateDirectoryData) => createMutation.mutateAsync(data),
    updateDirectory: (id: string, data: UpdateDirectoryData) =>
      updateMutation.mutateAsync({ id, data }),
    deleteDirectory: (id: string) => deleteMutation.mutateAsync(id),
  };
};
