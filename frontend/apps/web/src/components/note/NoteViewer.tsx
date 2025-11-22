import { ReactNode } from 'react';
import { NoteHeader } from './NoteHeader';
import { StatusBar } from './StatusBar';

interface NoteViewerProps {
  children: ReactNode;
}

export function NoteViewer({ children }: NoteViewerProps) {
  // TODO: Add TabBar at the top
  // TODO: Add markdown rendering
  // TODO: Add tag display
  // TODO: Add metadata

  return (
    <div className="h-full flex flex-col">
      {/* TODO: TabBar component will go here */}
      <div className="border-b border-light-border-primary dark:border-dark-border-primary p-2 text-xs text-light-text-muted dark:text-dark-text-muted">
        TODO: Tab Bar (RAG Implementation × | Vector Database... | Semantic...)
      </div>

      {/* Note Header */}
      <NoteHeader />

      {/* Note Content */}
      <div className="flex-1 overflow-y-auto p-6">
        {children}
      </div>

      {/* Status Bar */}
      <StatusBar />
    </div>
  );
}
