import { ReactNode } from 'react';
import { LeftSidebar } from './LeftSidebar';
import { RightSidebar } from './RightSidebar';
import { IconToolbar } from './IconToolbar';

interface MainLayoutProps {
  children: ReactNode;
  onNoteSelect?: (noteId: string) => void;
  selectedNoteId?: string | null;
}

export function MainLayout({ children, onNoteSelect, selectedNoteId }: MainLayoutProps) {
  // TODO: Add state management for sidebar visibility
  // TODO: Connect to UI store for theme and layout preferences

  return (
    <div className="flex h-screen bg-light-bg-secondary dark:bg-dark-bg-primary">
      {/* Left Sidebar - Resizable */}
      <LeftSidebar
        onNoteSelect={onNoteSelect}
        selectedNoteId={selectedNoteId}
        defaultWidth={280}
        minWidth={200}
        maxWidth={500}
      />

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
