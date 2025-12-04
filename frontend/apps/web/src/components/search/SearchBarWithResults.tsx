import { memo, useState, useCallback, useRef, useEffect, KeyboardEvent } from 'react';
import type { SearchResult } from '@synapse/types/domain';
import { SearchBar } from './SearchBar';
import { SearchResultsDropdown } from './SearchResultsDropdown';

interface SearchBarWithResultsProps {
  /** Placeholder text for the search input */
  placeholder?: string;

  /** Search results to display */
  results: SearchResult | null;

  /** Whether search is currently loading */
  isLoading: boolean;

  /** Error message if search failed */
  error: string | null;

  /** Callback when search query changes (called immediately on every keystroke) */
  onChange?: (query: string) => void;

  /** Callback when debounced search query changes (called after user stops typing) */
  onSearch?: (query: string) => void;

  /** Callback when a result is selected */
  onResultSelect: (noteId: string) => void;

  /** Debounce delay in milliseconds (default: 300ms) */
  debounceDelay?: number;

  /** Whether the search input is disabled */
  disabled?: boolean;

  /** Optional CSS classes to apply */
  className?: string;
}

/**
 * SearchBarWithResults component that combines SearchBar with SearchResultsDropdown.
 * Handles keyboard navigation (arrow keys, Enter, Escape) and result selection.
 *
 * Features:
 * - Integrated search input with results dropdown
 * - Keyboard navigation with arrow keys
 * - Enter to select highlighted result
 * - Escape to close dropdown
 * - Click outside to close dropdown
 * - Automatic scrolling to selected item
 */
export const SearchBarWithResults = memo(function SearchBarWithResults({
  placeholder = 'Search ...',
  results,
  isLoading,
  error,
  onChange,
  onSearch,
  onResultSelect,
  debounceDelay = 300,
  disabled = false,
  className = '',
}: SearchBarWithResultsProps) {
  const [query, setQuery] = useState('');
  const [selectedIndex, setSelectedIndex] = useState(-1);
  const [isOpen, setIsOpen] = useState(false);
  const containerRef = useRef<HTMLDivElement>(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setIsOpen(false);
        setSelectedIndex(-1);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  // Open dropdown when results change
  useEffect(() => {
    if (results || isLoading || error) {
      setIsOpen(true);
    }
  }, [results, isLoading, error]);

  // Reset selected index when results change
  useEffect(() => {
    setSelectedIndex(-1);
  }, [results]);

  const handleChange = useCallback(
    (newQuery: string) => {
      setQuery(newQuery);
      onChange?.(newQuery);

      // Close dropdown if query is empty
      if (!newQuery.trim()) {
        setIsOpen(false);
        setSelectedIndex(-1);
      }
    },
    [onChange]
  );

  const handleSearch = useCallback(
    (searchQuery: string) => {
      onSearch?.(searchQuery);
      if (searchQuery.trim()) {
        setIsOpen(true);
      }
    },
    [onSearch]
  );

  const handleResultClick = useCallback(
    (noteId: string) => {
      onResultSelect(noteId);
      setIsOpen(false);
      setSelectedIndex(-1);
      // Clear the search query after selection
      setQuery('');
    },
    [onResultSelect]
  );

  const handleKeyDown = useCallback(
    (event: KeyboardEvent<HTMLDivElement>) => {
      if (!results || results.hits.length === 0) return;

      switch (event.key) {
        case 'ArrowDown':
          event.preventDefault();
          setSelectedIndex((prev) =>
            prev < results.hits.length - 1 ? prev + 1 : 0
          );
          setIsOpen(true);
          break;

        case 'ArrowUp':
          event.preventDefault();
          setSelectedIndex((prev) =>
            prev > 0 ? prev - 1 : results.hits.length - 1
          );
          setIsOpen(true);
          break;

        case 'Enter':
          if (selectedIndex >= 0 && selectedIndex < results.hits.length) {
            event.preventDefault();
            const selectedNote = results.hits[selectedIndex].note;
            handleResultClick(selectedNote.id);
          }
          break;

        case 'Escape':
          event.preventDefault();
          setIsOpen(false);
          setSelectedIndex(-1);
          break;

        default:
          break;
      }
    },
    [results, selectedIndex, handleResultClick]
  );

  return (
    <div
      ref={containerRef}
      className={`relative ${className}`}
      onKeyDown={handleKeyDown}
    >
      <SearchBar
        placeholder={placeholder}
        onChange={handleChange}
        onSearch={handleSearch}
        debounceDelay={debounceDelay}
        disabled={disabled}
      />

      <SearchResultsDropdown
        results={results}
        isLoading={isLoading}
        error={error}
        query={query}
        selectedIndex={selectedIndex}
        onResultClick={handleResultClick}
        onSelectedIndexChange={setSelectedIndex}
        isOpen={isOpen}
      />
    </div>
  );
});
