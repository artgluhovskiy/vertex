import { useMemo, useState, useCallback } from 'react';
import { useDirectories } from '@synapse/api/hooks';
import { useNotes } from '@synapse/api/hooks';
import { DirectoryTree } from './DirectoryTree';
import { buildDirectoryTree } from '@/utils/buildDirectoryTree';
import { ChevronIcon } from '@/components/icons/ChevronIcon';
import { SECTION_MAX_HEIGHT } from './constants';

export function FoldersSection() {
  // Fetch directories and notes from API
  const { directories, rootDirectory, isLoading, error } = useDirectories();
  const { notes } = useNotes();

  // Local state for expand/collapse and selection
  const [expandedIds, setExpandedIds] = useState<Set<string>>(new Set());
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [isSectionCollapsed, setIsSectionCollapsed] = useState(false);

  // Build the tree structure (memoized for performance)
  const treeData = useMemo(() => {
    // TODO: Backend - Ensure ROOT directory is created on user registration
    // For now, create a mock ROOT if it doesn't exist
    if (!rootDirectory) {
      return null;
    }

    return buildDirectoryTree(directories, rootDirectory, notes, {
      expandedIds,
      includeChildNoteCounts: true,
      sortAlphabetically: true,
    });
  }, [directories, rootDirectory, notes, expandedIds]);

  // Handle expand/collapse toggle
  const handleToggle = useCallback((directoryId: string) => {
    setExpandedIds((prev) => {
      const next = new Set(prev);
      if (next.has(directoryId)) {
        next.delete(directoryId);
      } else {
        next.add(directoryId);
      }
      return next;
    });
  }, []);

  // Handle directory selection
  const handleSelect = useCallback((directoryId: string) => {
    setSelectedId(directoryId);
    // TODO: Filter notes by selected directory
  }, []);

  // Loading state
  if (isLoading) {
    return (
      <div className="mb-4">
        <div className="px-2 py-4 text-center text-sm text-light-text-muted dark:text-dark-text-muted">
          Loading folders...
        </div>
      </div>
    );
  }

  // Error state
  if (error) {
    return (
      <div className="mb-4">
        <div className="px-2 py-4 text-center text-sm text-error">
          <p>Failed to load folders</p>
          <p className="text-xs mt-1">{error.message}</p>
        </div>
      </div>
    );
  }

  // No ROOT directory state
  // TODO: Backend - This should never happen after backend creates ROOT on registration
  if (!rootDirectory) {
    return (
      <div className="mb-4">
        <div className="px-2 py-4 text-center text-sm text-light-text-muted dark:text-dark-text-muted">
          <p className="mb-2">⚠️</p>
          <p>No root directory found</p>
          <p className="text-xs mt-1">
            TODO: Backend should create ROOT &quot;Folders&quot; on user registration
          </p>
        </div>
      </div>
    );
  }

  // No tree data (shouldn't happen if rootDirectory exists)
  if (!treeData) {
    return null;
  }

  // Render the collapsible section with directory tree
  return (
    <div className="mb-4">
      {/* Section Header */}
      <button
        className="w-full flex items-center justify-between px-1 py-1.5 text-sm font-medium text-light-text-primary dark:text-dark-text-primary hover:bg-light-bg-hover/50 dark:hover:bg-dark-bg-hover/50 rounded transition-colors"
        onClick={() => setIsSectionCollapsed(!isSectionCollapsed)}
      >
        <div className="flex items-center gap-1">
          <span className="text-light-text-muted dark:text-dark-text-muted flex items-center justify-center w-5 h-5">
            <ChevronIcon rotated={!isSectionCollapsed} />
          </span>
          <span>Folders</span>
        </div>
      </button>

      {/* Directory Tree */}
      <div
        className="overflow-hidden transition-all duration-300 ease-in-out"
        style={{
          maxHeight: isSectionCollapsed ? '0px' : `${SECTION_MAX_HEIGHT}px`,
          opacity: isSectionCollapsed ? 0 : 1,
        }}
      >
        <div className="mt-1">
          <DirectoryTree
            rootNode={treeData}
            selectedId={selectedId}
            onSelect={handleSelect}
            onToggle={handleToggle}
          />
        </div>
      </div>
    </div>
  );
}
