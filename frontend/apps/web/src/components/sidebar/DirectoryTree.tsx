import type { DirectoryTreeNode } from '@/types/directoryTree';
import { DirectoryTreeItem } from './DirectoryTreeItem';

interface DirectoryTreeProps {
  /** Root node of the tree (typically the ROOT "Folders" directory) */
  rootNode: DirectoryTreeNode;

  /** ID of the currently selected directory */
  selectedId: string | null;

  /** Callback when a directory is selected */
  onSelect: (directoryId: string) => void;

  /** Callback when expand/collapse is triggered */
  onToggle: (directoryId: string) => void;

  /** Callback when a note is clicked */
  onNoteSelect?: (noteId: string) => void;

  /** ID of the currently selected note */
  selectedNoteId?: string | null;

  /** Whether to render the root node itself or just its children */
  renderRoot?: boolean;
}

/**
 * DirectoryTree component renders a hierarchical tree of directories.
 *
 * By default, it renders only the children of the root node (not the root itself),
 * because the ROOT "Folders" directory is a system directory that shouldn't be visible.
 *
 * @example
 * ```tsx
 * <DirectoryTree
 *   rootNode={treeData}
 *   selectedId={selectedId}
 *   onSelect={handleSelect}
 *   onToggle={handleToggle}
 * />
 * ```
 */
export function DirectoryTree({
  rootNode,
  selectedId,
  onSelect,
  onToggle,
  onNoteSelect,
  selectedNoteId,
  renderRoot = false,
}: DirectoryTreeProps) {
  // If no children, show empty state
  if (rootNode.children.length === 0) {
    return (
      <div className="px-2 py-8 text-center text-sm text-light-text-muted dark:text-dark-text-muted">
        <p className="mb-2">📁</p>
        <p>No folders yet</p>
        <p className="text-xs mt-1">Create your first folder to organize notes</p>
      </div>
    );
  }

  // If renderRoot is true, render the root node itself
  if (renderRoot) {
    return (
      <DirectoryTreeItem
        node={rootNode}
        selectedId={selectedId}
        onSelect={onSelect}
        onToggle={onToggle}
        onNoteSelect={onNoteSelect}
        selectedNoteId={selectedNoteId}
      />
    );
  }

  // Default: Render only the children of the root (hide the ROOT "Folders" directory)
  return (
    <div className="space-y-0.5" role="tree" aria-label="Directory tree">
      {rootNode.children.map((childNode) => (
        <DirectoryTreeItem
          key={childNode.directory.id}
          node={childNode}
          selectedId={selectedId}
          onSelect={onSelect}
          onToggle={onToggle}
          onNoteSelect={onNoteSelect}
          selectedNoteId={selectedNoteId}
        />
      ))}
    </div>
  );
}
