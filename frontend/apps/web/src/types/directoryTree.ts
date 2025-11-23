import type { Directory, Note } from '@synapse/types/domain';

/**
 * Represents a node in the directory tree structure.
 * Contains the directory data, its children, notes, note count, and UI state.
 */
export interface DirectoryTreeNode {
  /** The directory data from the API */
  directory: Directory;

  /** Child directories (nested structure) */
  children: DirectoryTreeNode[];

  /** Notes that belong to this directory (direct children only, not recursive) */
  notes: Note[];

  /** Total number of notes in this directory (recursive count includes children) */
  noteCount: number;

  /** Whether this node is currently expanded in the UI */
  isExpanded: boolean;

  /** Depth level in the tree (0 = root, 1 = first level, etc.) */
  level: number;
}

/**
 * Options for building the directory tree
 */
export interface BuildTreeOptions {
  /** Set of directory IDs that should be expanded */
  expandedIds: Set<string>;

  /** Whether to calculate recursive note counts (includes children's notes) */
  includeChildNoteCounts?: boolean;

  /** Whether to sort directories alphabetically */
  sortAlphabetically?: boolean;
}
