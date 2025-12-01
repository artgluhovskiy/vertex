import { memo, useState, useCallback, useEffect, ChangeEvent, KeyboardEvent } from 'react';
import { SearchIcon } from '@/components/icons';
import { useDebounce } from '@/hooks';

interface SearchBarProps {
  /** Placeholder text for the search input */
  placeholder?: string;

  /** Callback when search query changes (called immediately on every keystroke) */
  onChange?: (query: string) => void;

  /** Callback when debounced search query changes (called after user stops typing) */
  onSearch?: (query: string) => void;

  /** Callback when Enter key is pressed */
  onSubmit?: (query: string) => void;

  /** Debounce delay in milliseconds (default: 300ms) */
  debounceDelay?: number;

  /** Whether the search input is disabled */
  disabled?: boolean;

  /** Optional CSS classes to apply */
  className?: string;
}

/**
 * SearchBar component for entering search queries.
 * Displays a search icon on the left and handles user input.
 *
 * Features:
 * - Immediate updates via onChange callback (optional)
 * - Debounced search via onSearch callback (default 300ms delay)
 * - Enter key submission via onSubmit callback
 * - Accessible with proper ARIA labels
 * - Responsive dark/light theme support
 */
export const SearchBar = memo(function SearchBar({
  placeholder = 'Search ...',
  onChange,
  onSearch,
  onSubmit,
  debounceDelay = 300,
  disabled = false,
  className = '',
}: SearchBarProps) {
  const [query, setQuery] = useState('');
  const debouncedQuery = useDebounce(query, debounceDelay);

  // Call onSearch when debounced query changes
  useEffect(() => {
    onSearch?.(debouncedQuery);
  }, [debouncedQuery, onSearch]);

  const handleChange = useCallback(
    (event: ChangeEvent<HTMLInputElement>) => {
      const newQuery = event.target.value;
      setQuery(newQuery);
      onChange?.(newQuery);
    },
    [onChange]
  );

  const handleKeyDown = useCallback(
    (event: KeyboardEvent<HTMLInputElement>) => {
      if (event.key === 'Enter' && !disabled) {
        event.preventDefault();
        onSubmit?.(query);
      }
    },
    [query, onSubmit, disabled]
  );

  return (
    <div className={`relative ${className}`}>
      {/* Search Icon */}
      <div className="absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none">
        <SearchIcon
          size={16}
          className="text-light-text-muted dark:text-dark-text-muted"
        />
      </div>

      {/* Search Input */}
      <input
        type="text"
        value={query}
        onChange={handleChange}
        onKeyDown={handleKeyDown}
        disabled={disabled}
        placeholder={placeholder}
        aria-label="Search notes"
        className={`
          w-full pl-9 pr-3 py-2 text-sm rounded-lg
          bg-light-bg-secondary dark:bg-dark-bg-tertiary
          border border-light-border-primary dark:border-dark-border-primary
          text-light-text-primary dark:text-dark-text-primary
          placeholder:text-light-text-muted dark:placeholder:text-dark-text-muted
          transition-colors
          focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary
          hover:border-light-border-hover dark:hover:border-dark-border-hover
          disabled:opacity-50 disabled:cursor-not-allowed
        `}
      />
    </div>
  );
});
