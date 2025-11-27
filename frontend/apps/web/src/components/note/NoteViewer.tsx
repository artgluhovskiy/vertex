import { ReactNode } from 'react';
import { NoteHeader } from './NoteHeader';
import { StatusBar } from './StatusBar';

interface NoteViewerProps {
  children: ReactNode;
  title?: string;
  breadcrumb?: string;
  tags?: string[];
}

export function NoteViewer({ children, title, breadcrumb, tags }: NoteViewerProps) {
  // TODO: Add TabBar at the top

  return (
    <div className="h-full flex flex-col">
      {/* TODO: TabBar component will go here */}
      <div className="border-b border-light-border-primary dark:border-dark-border-primary p-2 text-xs text-light-text-muted dark:text-dark-text-muted">
        TODO: Tab Bar (RAG Implementation × | Vector Database... | Semantic...)
      </div>

      {/* Note Header */}
      <NoteHeader title={title} breadcrumb={breadcrumb} tags={tags} />

      {/* Note Content */}
      <div className="flex-1 overflow-y-auto">
        {children}
      </div>

      {/* Status Bar */}
      <StatusBar />
    </div>
  );
}
