export interface SearchQuery {
  query: string;
  type?: SearchType;
  directoryId?: string;
  tags?: string[];
  limit?: number;
  offset?: number;
}

export type SearchType = 'full_text' | 'semantic' | 'hybrid';

export interface SearchResult {
  hits: SearchHit[];
  totalHits: number;
  searchTimeMs: number;
  type: SearchType;
}

export interface SearchHit {
  noteId: string;
  title: string;
  content: string;
  score: number;
  matchType: SearchType;
  highlights: string[];
}
