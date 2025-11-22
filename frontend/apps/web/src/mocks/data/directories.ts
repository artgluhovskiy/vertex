import type { Directory } from '@synapse/types/domain';

/**
 * Mock directory data for development and testing.
 *
 * Structure:
 * - ROOT "Folders" (id: root-1, parentId: null)
 *   - Work (id: dir-1)
 *     - Projects (id: dir-2)
 *     - Meetings (id: dir-3)
 *   - Personal (id: dir-4)
 *     - Learning (id: dir-5)
 *   - Archive (id: dir-6)
 */
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
  {
    id: 'dir-6',
    userId: 'user-1',
    name: 'Archive',
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

  // Personal subdirectories
  {
    id: 'dir-5',
    userId: 'user-1',
    name: 'Learning',
    parentId: 'dir-4',
    createdAt: '2024-01-03T00:00:00Z',
  },
];
