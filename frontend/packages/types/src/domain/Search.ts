import type { Note } from './Note';

/**
 * Search request payload matching backend SearchRequest
 * Endpoint: POST /api/v1/notes/search
 */
export interface SearchRequest {
  /** Search query string (required) */
  query: string;

  /** Search type (optional, defaults to SEMANTIC in backend) */
  type?: SearchType;

  /** Maximum number of results to return (optional, must be positive) */
  maxResults?: number;
}

/**
 * Search type enum matching backend SearchType
 * Currently only SEMANTIC is supported
 */
export type SearchType = 'SEMANTIC';
// Future types: 'FULL_TEXT' | 'GRAPH' | 'HYBRID'

/**
 * Search result response matching backend SearchResultDto
 */
export interface SearchResult {
  /** List of search hits */
  hits: SearchHit[];

  /** Total number of hits found */
  totalHits: number;
}

/**
 * Individual search hit matching backend SearchHitDto
 */
export interface SearchHit {
  /** The matched note */
  note: Note;

  /** Relevance score for this hit */
  score: number;
}
