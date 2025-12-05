import { apiClient } from '../client';
import type { SearchRequest, SearchResult } from '@synapse/types/domain';

/**
 * Search service for note search operations.
 * Provides methods for searching notes using various search types.
 */
export const searchService = {
  /**
   * Search notes using the backend search API.
   * Endpoint: POST /notes/search
   *
   * @param request - Search request parameters
   * @returns Promise resolving to search results
   */
  search: async (request: SearchRequest): Promise<SearchResult> => {
    const response = await apiClient.post<SearchResult>('/notes/search', request);
    return response.data;
  },
};
