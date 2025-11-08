// Centralized query keys for React Query
export const queryKeys = {
  auth: {
    me: ['auth', 'me'] as const,
  },
  notes: {
    all: ['notes'] as const,
    list: (filters?: Record<string, unknown>) => ['notes', 'list', filters] as const,
    detail: (id: string) => ['notes', 'detail', id] as const,
  },
  directories: {
    all: ['directories'] as const,
    list: (filters?: Record<string, unknown>) => ['directories', 'list', filters] as const,
    detail: (id: string) => ['directories', 'detail', id] as const,
    root: () => ['directories', 'root'] as const,
    children: (id: string) => ['directories', 'children', id] as const,
  },
  tags: {
    all: ['tags'] as const,
    list: () => ['tags', 'list'] as const,
  },
  search: {
    query: (query: string) => ['search', query] as const,
  },
  graph: {
    data: (noteId: string) => ['graph', noteId] as const,
  },
} as const;
