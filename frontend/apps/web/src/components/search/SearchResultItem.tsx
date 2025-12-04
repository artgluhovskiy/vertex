import { memo } from 'react';
import type { SearchHit } from '@synapse/types/domain';

interface SearchResultItemProps {
  /** The search hit to display */
  hit: SearchHit;

  /** Whether this item is currently selected/highlighted */
  isSelected: boolean;

  /** Callback when item is clicked */
  onClick: (noteId: string) => void;

  /** Callback when item is hovered */
  onMouseEnter: () => void;
}

/**
 * Individual search result item component.
 * Displays note title, content preview, score, and metadata.
 */
export const SearchResultItem = memo(function SearchResultItem({
  hit,
  isSelected,
  onClick,
  onMouseEnter,
}: SearchResultItemProps) {
  const { note, score } = hit;

  // Generate content preview (first 100 characters)
  const contentPreview = note.summary ?? note.content.slice(0, 100);
  const shouldShowEllipsis = contentPreview.length >= 100;

  const handleClick = () => {
    onClick(note.id);
  };

  return (
    <button
      role="option"
      aria-selected={isSelected}
      onClick={handleClick}
      onMouseEnter={onMouseEnter}
      className={`
        w-full px-4 py-3 text-left transition-colors
        focus:outline-none
        ${
          isSelected
            ? 'bg-light-bg-hover dark:bg-dark-bg-hover'
            : 'hover:bg-light-bg-hover/50 dark:hover:bg-dark-bg-hover/50'
        }
      `}
    >
      {/* Title and Score */}
      <div className="flex items-start justify-between gap-2 mb-1">
        <h4 className="font-medium text-sm text-light-text-primary dark:text-dark-text-primary truncate flex-1">
          {note.title}
        </h4>
        {score > 0 && (
          <span className="text-xs text-light-text-muted dark:text-dark-text-muted flex-shrink-0">
            {(score * 100).toFixed(0)}%
          </span>
        )}
      </div>

      {/* Content Preview */}
      {contentPreview && (
        <p className="text-xs text-light-text-secondary dark:text-dark-text-secondary line-clamp-2">
          {contentPreview}
          {shouldShowEllipsis && '...'}
        </p>
      )}

      {/* Tags */}
      {note.tags.length > 0 && (
        <div className="flex gap-1 mt-2 flex-wrap">
          {note.tags.slice(0, 3).map((tag) => (
            <span
              key={tag.id}
              className="text-xs px-2 py-0.5 rounded-full bg-light-bg-secondary dark:bg-dark-bg-tertiary text-light-text-muted dark:text-dark-text-muted"
            >
              {tag.name}
            </span>
          ))}
          {note.tags.length > 3 && (
            <span className="text-xs text-light-text-muted dark:text-dark-text-muted">
              +{note.tags.length - 3} more
            </span>
          )}
        </div>
      )}
    </button>
  );
});
