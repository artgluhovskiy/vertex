import { useQuery } from '@tanstack/react-query';
import { searchService } from '../services';
import { queryKeys } from '../query-keys';
import type { SearchRequest } from '@synapse/types/domain';

interface UseSearchOptions {
  /** Search query string */
  query: string;

  /** Search type (optional, defaults to SEMANTIC in backend) */
  type?: SearchRequest['type'];

  /** Maximum number of results (optional) */
  maxResults?: number;

  /** Whether the search query should be enabled (default: true when query is not empty) */
  enabled?: boolean;
}

/**
 * Custom hook for searching notes using TanStack Query.
 * Automatically caches results and manages loading/error states.
 *
 * @param options - Search options
 * @returns Search results, loading state, and error
 *
 * @example
 * const { results, isLoading, error } = useSearch({
 *   query: 'typescript',
 *   maxResults: 10,
 * });
 */
export const useSearch = ({
  query,
  type = 'SEMANTIC',
  maxResults = 20,
  enabled,
}: UseSearchOptions) => {
  const trimmedQuery = query.trim();

  // Only enable query if query is not empty and enabled is not explicitly false
  const shouldEnable = enabled !== false && trimmedQuery.length > 0;

  const searchQuery = useQuery({
    queryKey: queryKeys.search.query(trimmedQuery),
    queryFn: () =>
      searchService.search({
        query: trimmedQuery,
        type,
        maxResults,
      }),
    enabled: shouldEnable,
    // Keep previous data while fetching new results for better UX
    placeholderData: (previousData) => previousData,
    // Cache results for 5 minutes
    staleTime: 5 * 60 * 1000,
  });

  return {
    results: searchQuery.data ?? null,
    isLoading: searchQuery.isLoading,
    isFetching: searchQuery.isFetching,
    error: searchQuery.error?.message ?? null,
    refetch: searchQuery.refetch,
  };
};
