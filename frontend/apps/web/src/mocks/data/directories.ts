import type { Directory } from '@synapse/types/domain';

export const mockDirectories: Directory[] = [
  // ROOT directory (hidden from UI)
  {
    id: 'root-1',
    userId: 'user-1',
    name: 'Folders',
    parentId: null,
    createdAt: '2024-01-01T00:00:00Z',
  },

  // Top-level directories
  {
    id: 'dir-1',
    userId: 'user-1',
    name: 'Work',
    parentId: 'root-1',
    createdAt: '2024-01-02T00:00:00Z',
  },
  {
    id: 'dir-4',
    userId: 'user-1',
    name: 'Personal',
    parentId: 'root-1',
    createdAt: '2024-01-02T00:00:00Z',
  },

  // Work subdirectories
  {
    id: 'dir-2',
    userId: 'user-1',
    name: 'Projects',
    parentId: 'dir-1',
    createdAt: '2024-01-03T00:00:00Z',
  },
  {
    id: 'dir-3',
    userId: 'user-1',
    name: 'Meetings',
    parentId: 'dir-1',
    createdAt: '2024-01-03T00:00:00Z',
  },
];
