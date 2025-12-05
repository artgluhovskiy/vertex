import { useState, useCallback } from 'react';
import { NotesSection, FoldersSection, SmartShelvesSection } from '../sidebar';
import { SearchBarWithResults } from '../search';
import { useResizable } from '@/hooks/useResizable';
import { useSearch } from '@synapse/api/hooks';

interface LeftSidebarProps {
  onNoteSelect?: (noteId: string) => void;
  selectedNoteId?: string | null;
  defaultWidth?: number;
  minWidth?: number;
  maxWidth?: number;
}

export function LeftSidebar({
  onNoteSelect,
  selectedNoteId,
  defaultWidth = 280,
  minWidth = 200,
  maxWidth = 500,
}: LeftSidebarProps) {
  // TODO: Add "New Note" button
  // TODO: Add sort/filter buttons

  const [searchQuery, setSearchQuery] = useState('');

  const {
    width,
    isResizing,
    handleMouseDown,
  } = useResizable({
    initialWidth: defaultWidth,
    minWidth,
    maxWidth,
  });

  // Search hook - automatically fetches results when query changes
  const { results, isLoading, error } = useSearch({
    query: searchQuery,
    maxResults: 10,
  });

  const handleSearchChange = useCallback((query: string) => {
    // Called immediately on every keystroke
    console.log('[Immediate] Search query changed:', query);
  }, []);

  const handleSearch = useCallback((query: string) => {
    // Called after user stops typing (debounced)
    // Update the search query state to trigger the API call
    console.log('[Debounced] Performing search for:', query);
    setSearchQuery(query);
  }, []);

  const handleResultSelect = useCallback(
    (noteId: string) => {
      console.log('[Result Selected] Note ID:', noteId);
      onNoteSelect?.(noteId);
    },
    [onNoteSelect]
  );

  return (
    <aside
      className="flex-shrink-0 border-r border-light-border-primary dark:border-dark-border-primary bg-light-bg-primary dark:bg-dark-bg-secondary relative"
      style={{ width: `${width}px` }}
    >
      <div className="h-full flex flex-col p-4 overflow-y-auto">
        <div className="text-light-text-primary dark:text-dark-text-primary">
          {/* Search Bar with Results */}
          <SearchBarWithResults
            placeholder="Search ..."
            results={results}
            isLoading={isLoading}
            error={error}
            onChange={handleSearchChange}
            onSearch={handleSearch}
            onResultSelect={handleResultSelect}
            debounceDelay={500}
            className="mb-4"
          />

          {/* TODO: New Note Button */}
          {/* TODO: Sort & Filter */}

          {/* Sections */}
          <div className="space-y-2">
            <NotesSection />
            <FoldersSection onNoteSelect={onNoteSelect} selectedNoteId={selectedNoteId} />
            <SmartShelvesSection />
          </div>
        </div>
      </div>

      {/* Resize Handle */}
      <div
        onMouseDown={handleMouseDown}
        className={`
          absolute top-0 right-0 bottom-0 w-1 cursor-col-resize
          hover:bg-primary/30 transition-colors
          ${isResizing ? 'bg-primary/50' : ''}
        `}
        style={{ touchAction: 'none' }}
        aria-label="Resize sidebar"
      />
    </aside>
  );
}
