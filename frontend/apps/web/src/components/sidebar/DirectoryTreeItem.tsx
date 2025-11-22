import type { DirectoryTreeNode } from '@/types/directory-tree';

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

export function DirectoryTreeItem({
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
  const indentPx = Math.max(0, level - 1) * 16;

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
        style={{ paddingLeft: `${4 + indentPx}px` }}
      >
        {/* Expand/Collapse Chevron */}
        <span
          className={`
            flex-shrink-0 w-5 h-5 flex items-center justify-center
            transition-transform
            ${hasChildren ? 'text-light-text-muted dark:text-dark-text-muted' : 'invisible'}
          `}
          style={{
            transform: isExpanded && hasChildren ? 'rotate(90deg)' : 'rotate(0deg)',
          }}
        >
          <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
            <path d="M5 3l6 5-6 5V3z"/>
          </svg>
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
            maxHeight: isExpanded ? `${children.length * 32}px` : '0px',
            opacity: isExpanded ? 1 : 0,
          }}
        >
          {/* Vertical line indicator */}
          {isExpanded && (
            <div
              className="absolute top-0 bottom-0 w-px bg-light-text-muted/20 dark:bg-dark-text-muted/20"
              style={{
                left: `${4 + indentPx + 10}px`, // Position at the center of the chevron
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
}
