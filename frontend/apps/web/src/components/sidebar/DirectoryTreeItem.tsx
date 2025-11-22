import { memo } from 'react';
import type { DirectoryTreeNode } from '@/types/directoryTree';
import { ChevronIcon } from '@/components/icons/ChevronIcon';
import {
  TREE_ITEM_HEIGHT,
  TREE_INDENT_INCREMENT,
  CHEVRON_CENTER_OFFSET,
  TREE_ITEM_BASE_PADDING,
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
}

export const DirectoryTreeItem = memo(function DirectoryTreeItem({
  node,
  selectedId,
  onSelect,
  onToggle,
}: DirectoryTreeItemProps) {
  const { directory, children, isExpanded, level } = node;

  const isSelected = selectedId === directory.id;
  const hasChildren = children.length > 0;

  // Calculate indentation based on tree level
  // Level 0 = ROOT (not rendered), Level 1 = 0px, Level 2 = 16px, etc.
  const indentPx = Math.max(0, level - 1) * TREE_INDENT_INCREMENT;

  const handleClick = () => {
    // If folder has children, toggle expand/collapse
    if (hasChildren) {
      onToggle(directory.id);
    }
    // Always select the directory
    onSelect(directory.id);
  };

  return (
    <div className="relative">
      {/* Directory Item */}
      <button
        role="treeitem"
        aria-expanded={hasChildren ? isExpanded : undefined}
        aria-selected={isSelected}
        aria-label={`${directory.name}${hasChildren ? `, ${children.length} ${children.length === 1 ? 'subfolder' : 'subfolders'}` : ''}`}
        onClick={handleClick}
        className={`
          w-full flex items-center gap-1 px-1 py-1 text-sm
          transition-colors rounded relative
          ${
            isSelected
              ? 'bg-light-bg-hover dark:bg-dark-bg-hover'
              : 'hover:bg-light-bg-hover/50 dark:hover:bg-dark-bg-hover/50'
          }
        `}
        style={{ paddingLeft: `${TREE_ITEM_BASE_PADDING + indentPx}px` }}
      >
        {/* Expand/Collapse Chevron */}
        <span
          className={`
            flex-shrink-0 w-5 h-5 flex items-center justify-center
            ${hasChildren ? 'text-light-text-muted dark:text-dark-text-muted' : 'invisible'}
          `}
        >
          <ChevronIcon rotated={isExpanded && hasChildren} />
        </span>

        {/* Directory Name */}
        <span className="truncate text-light-text-primary dark:text-dark-text-primary">
          {directory.name}
        </span>
      </button>

      {/* Render Children (if expanded) */}
      {hasChildren && (
        <div
          className="overflow-hidden transition-all duration-200 ease-in-out relative"
          style={{
            maxHeight: isExpanded ? `${children.length * TREE_ITEM_HEIGHT}px` : '0px',
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
          {children.map((childNode) => (
            <DirectoryTreeItem
              key={childNode.directory.id}
              node={childNode}
              selectedId={selectedId}
              onSelect={onSelect}
              onToggle={onToggle}
            />
          ))}
        </div>
      )}
    </div>
  );
});
