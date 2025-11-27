import { memo } from 'react';
import type { DirectoryTreeNode } from '@/types/directoryTree';
import { ChevronIcon } from '@/components/icons/ChevronIcon';
import { NoteIcon } from '@/components/icons/NoteIcon';
import {
  TREE_ITEM_HEIGHT,
  TREE_INDENT_INCREMENT,
  CHEVRON_CENTER_OFFSET,
  TREE_ITEM_BASE_PADDING,
  NOTE_INDENT_OFFSET,
} from './constants';

interface DirectoryTreeItemProps {
  /** The tree node to render */
  node: DirectoryTreeNode;

  /** ID of the currently selected directory */
  selectedId: string | null;

  /** Callback when directory is clicked/selected */
  onSelect: (directoryId: string) => void;

  /** Callback when expand/collapse icon is clicked */
  onToggle: (directoryId: string) => void;

  /** Callback when a note is clicked */
  onNoteSelect?: (noteId: string) => void;

  /** ID of the currently selected note */
  selectedNoteId?: string | null;
}

export const DirectoryTreeItem = memo(function DirectoryTreeItem({
  node,
  selectedId,
  onSelect,
  onToggle,
  onNoteSelect,
  selectedNoteId,
}: DirectoryTreeItemProps) {
  const { directory, children, notes, isExpanded, level } = node;

  const hasChildren = children.length > 0;
  const hasNotes = notes.length > 0;

  // Calculate indentation based on tree level
  // Level 0 = ROOT (not rendered), Level 1 = 0px, Level 2 = 16px, etc.
  const indentPx = Math.max(0, level - 1) * TREE_INDENT_INCREMENT;

  // Calculate total height needed for expanded content (notes + child directories)
  const totalItemCount = notes.length + children.length;
  const expandedHeight = totalItemCount * TREE_ITEM_HEIGHT;

  const handleClick = () => {
    // Toggle expand/collapse if folder has children or notes
    if (hasChildren || hasNotes) {
      onToggle(directory.id);
    }
  };

  return (
    <div className="relative">
      {/* Directory Item */}
      <button
        role="treeitem"
        aria-expanded={hasChildren || hasNotes ? isExpanded : undefined}
        aria-label={`${directory.name}${hasChildren ? `, ${children.length} ${children.length === 1 ? 'subfolder' : 'subfolders'}` : ''}${hasNotes ? `, ${notes.length} ${notes.length === 1 ? 'note' : 'notes'}` : ''}`}
        onClick={handleClick}
        className="w-full flex items-center gap-1 px-1 py-1 text-sm transition-colors rounded relative hover:bg-light-bg-hover/50 dark:hover:bg-dark-bg-hover/50"
        style={{ paddingLeft: `${TREE_ITEM_BASE_PADDING + indentPx}px` }}
      >
        {/* Expand/Collapse Chevron */}
        <span
          className={`
            flex-shrink-0 w-5 h-5 flex items-center justify-center
            ${hasChildren || hasNotes ? 'text-light-text-muted dark:text-dark-text-muted' : 'text-light-text-muted/30 dark:text-dark-text-muted/30'}
          `}
        >
          <ChevronIcon rotated={isExpanded && (hasChildren || hasNotes)} />
        </span>

        {/* Directory Name */}
        <span className="truncate text-light-text-primary dark:text-dark-text-primary">
          {directory.name}
        </span>
      </button>

      {/* Render Children and Notes (if expanded) */}
      {(hasChildren || hasNotes) && (
        <div
          className="overflow-hidden transition-all duration-100 ease-in-out relative"
          style={{
            maxHeight: isExpanded ? `${expandedHeight}px` : '0px',
            opacity: isExpanded ? 1 : 0,
          }}
        >
          {/* Vertical line indicator */}
          {isExpanded && (
            <div
              className="absolute top-0 bottom-0 w-px bg-light-text-muted/20 dark:bg-dark-text-muted/20"
              style={{
                left: `${TREE_ITEM_BASE_PADDING + indentPx + CHEVRON_CENTER_OFFSET}px`, // Position at the center of the chevron
              }}
            />
          )}

          {/* Render Notes first */}
          {hasNotes && (
            <div className="mt-0.5">
              {notes.map((note) => {
                const isNoteSelected = selectedNoteId === note.id;
                return (
                  <button
                    key={note.id}
                    role="treeitem"
                    aria-selected={isNoteSelected}
                    aria-label={`Note: ${note.title}`}
                    onClick={() => onNoteSelect?.(note.id)}
                    className={`
                      w-full flex items-center gap-1 px-1 py-1 text-sm transition-colors rounded
                      focus:outline-none text-light-text-secondary dark:text-dark-text-secondary
                      ${
                        isNoteSelected
                          ? 'bg-light-bg-hover dark:bg-dark-bg-hover'
                          : 'hover:bg-light-bg-hover/50 dark:hover:bg-dark-bg-hover/50'
                      }
                    `}
                    style={{ paddingLeft: `${TREE_ITEM_BASE_PADDING + indentPx + NOTE_INDENT_OFFSET}px` }}
                  >
                    <span className="flex items-center gap-2 truncate">
                      <span className="flex-shrink-0 text-light-text-muted dark:text-dark-text-muted" aria-hidden="true">
                        <NoteIcon />
                      </span>
                      <span className="truncate text-sm">{note.title}</span>
                    </span>
                  </button>
                );
              })}
            </div>
          )}

          {/* Then render child directories */}
          {children.map((childNode) => (
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
      )}
    </div>
  );
});
