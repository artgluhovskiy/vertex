import { memo, useCallback, useEffect, useRef } from 'react';
import type { SearchResult } from '@synapse/types/domain';
import { SearchResultItem } from './SearchResultItem';

interface SearchResultsDropdownProps {
  /** The search results to display */
  results: SearchResult | null;

  /** Whether search is currently loading */
  isLoading: boolean;

  /** Error message if search failed */
  error: string | null;

  /** The current search query */
  query: string;

  /** Currently selected result index (for keyboard navigation) */
  selectedIndex: number;

  /** Callback when a result is clicked */
  onResultClick: (noteId: string) => void;

  /** Callback when selected index changes (keyboard navigation) */
  onSelectedIndexChange: (index: number) => void;

  /** Whether the dropdown should be visible */
  isOpen: boolean;
}

/**
 * Dropdown component that displays search results.
 * Handles loading, empty, and error states.
 * Supports keyboard navigation with arrow keys.
 */
export const SearchResultsDropdown = memo(function SearchResultsDropdown({
  results,
  isLoading,
  error,
  query,
  selectedIndex,
  onResultClick,
  onSelectedIndexChange,
  isOpen,
}: SearchResultsDropdownProps) {
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Scroll selected item into view
  useEffect(() => {
    if (selectedIndex >= 0 && dropdownRef.current) {
      const selectedElement = dropdownRef.current.querySelector(
        `[role="option"][aria-selected="true"]`
      );
      selectedElement?.scrollIntoView({ block: 'nearest', behavior: 'smooth' });
    }
  }, [selectedIndex]);

  const handleMouseEnter = useCallback(
    (index: number) => {
      onSelectedIndexChange(index);
    },
    [onSelectedIndexChange]
  );

  // Don't render if not open
  if (!isOpen) {
    return null;
  }

  // Don't render if query is empty
  if (!query.trim()) {
    return null;
  }

  return (
    <div
      ref={dropdownRef}
      role="listbox"
      className="absolute top-full left-0 right-0 mt-2 bg-light-bg-primary dark:bg-dark-bg-secondary border border-light-border-primary dark:border-dark-border-primary rounded-lg shadow-lg overflow-hidden z-50 max-h-96 overflow-y-auto"
    >
      {/* Loading State */}
      {isLoading && (
        <div className="px-4 py-8 text-center text-light-text-muted dark:text-dark-text-muted">
          <div className="inline-block animate-spin rounded-full h-6 w-6 border-b-2 border-primary mb-2" />
          <p className="text-sm">Searching...</p>
        </div>
      )}

      {/* Error State */}
      {!isLoading && error && (
        <div className="px-4 py-8 text-center">
          <p className="text-sm text-red-500 dark:text-red-400 mb-2">
            Failed to search
          </p>
          <p className="text-xs text-light-text-muted dark:text-dark-text-muted">
            {error}
          </p>
        </div>
      )}

      {/* Empty State */}
      {!isLoading && !error && results && results.hits.length === 0 && (
        <div className="px-4 py-8 text-center text-light-text-muted dark:text-dark-text-muted">
          <p className="text-sm mb-1">No results found</p>
          <p className="text-xs">
            Try different keywords or check your spelling
          </p>
        </div>
      )}

      {/* Results */}
      {!isLoading && !error && results && results.hits.length > 0 && (
        <>
          {/* Results Header */}
          <div className="px-4 py-2 border-b border-light-border-primary dark:border-dark-border-primary bg-light-bg-secondary dark:bg-dark-bg-tertiary">
            <p className="text-xs text-light-text-muted dark:text-dark-text-muted">
              Found {results.totalHits} {results.totalHits === 1 ? 'result' : 'results'}
            </p>
          </div>

          {/* Results List */}
          <div className="divide-y divide-light-border-primary dark:divide-dark-border-primary">
            {results.hits.map((hit, index) => (
              <SearchResultItem
                key={hit.note.id}
                hit={hit}
                isSelected={index === selectedIndex}
                onClick={onResultClick}
                onMouseEnter={() => handleMouseEnter(index)}
              />
            ))}
          </div>

          {/* Show More Hint */}
          {results.hits.length < results.totalHits && (
            <div className="px-4 py-2 border-t border-light-border-primary dark:border-dark-border-primary bg-light-bg-secondary dark:bg-dark-bg-tertiary">
              <p className="text-xs text-light-text-muted dark:text-dark-text-muted text-center">
                Showing {results.hits.length} of {results.totalHits} results
              </p>
            </div>
          )}
        </>
      )}
    </div>
  );
});
