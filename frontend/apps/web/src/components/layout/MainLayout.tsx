import { ReactNode } from 'react';
import { LeftSidebar } from './LeftSidebar';
import { RightSidebar } from './RightSidebar';
import { IconToolbar } from './IconToolbar';
import { useResizable } from '@/hooks/useResizable';

interface MainLayoutProps {
  children: ReactNode;
  onNoteSelect?: (noteId: string) => void;
  selectedNoteId?: string | null;
}

const LEFT_SIDEBAR_DEFAULT_WIDTH = 280;
const LEFT_SIDEBAR_MIN_WIDTH = 200;
const LEFT_SIDEBAR_MAX_WIDTH = 500;

export function MainLayout({ children, onNoteSelect, selectedNoteId }: MainLayoutProps) {
  // TODO: Add state management for sidebar visibility
  // TODO: Connect to UI store for theme and layout preferences

  const {
    width: leftSidebarWidth,
    isResizing,
    handleMouseDown,
  } = useResizable({
    initialWidth: LEFT_SIDEBAR_DEFAULT_WIDTH,
    minWidth: LEFT_SIDEBAR_MIN_WIDTH,
    maxWidth: LEFT_SIDEBAR_MAX_WIDTH,
  });

  return (
    <div className="flex h-screen bg-light-bg-secondary dark:bg-dark-bg-primary">
      {/* Left Sidebar - Resizable */}
      <aside
        className="flex-shrink-0 border-r border-light-border-primary dark:border-dark-border-primary bg-light-bg-primary dark:bg-dark-bg-secondary relative"
        style={{ width: `${leftSidebarWidth}px` }}
      >
        <LeftSidebar onNoteSelect={onNoteSelect} selectedNoteId={selectedNoteId} />

        {/* Resize Handle */}
        <div
          onMouseDown={handleMouseDown}
          className={`
            absolute top-0 right-0 bottom-0 w-1 cursor-col-resize
            hover:bg-primary/30 transition-colors
            ${isResizing ? 'bg-primary/50' : ''}
          `}
          style={{ touchAction: 'none' }}
        />
      </aside>

      {/* Main Content Area - Flexible */}
      <main className="flex-1 flex flex-col overflow-hidden">
        {/* TODO: Add TabBar component here */}
        <div className="flex-1 overflow-y-auto">
          {children}
        </div>
      </main>

      {/* Right Sidebar - 280px collapsible */}
      <aside className="w-[280px] flex-shrink-0 border-l border-light-border-primary dark:border-dark-border-primary bg-light-bg-primary dark:bg-dark-bg-secondary">
        <RightSidebar />
      </aside>

      {/* Right Icon Toolbar - 48px fixed */}
      <aside className="w-12 flex-shrink-0 border-l border-light-border-primary dark:border-dark-border-primary bg-light-bg-secondary dark:bg-dark-bg-tertiary">
        <IconToolbar />
      </aside>
    </div>
  );
}
