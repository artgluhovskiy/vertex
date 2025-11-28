import { NotesSection, FoldersSection, SmartShelvesSection } from '../sidebar';
import { useResizable } from '@/hooks/useResizable';

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
  // TODO: Add search bar
  // TODO: Add "New Note" button
  // TODO: Add sort/filter buttons

  const {
    width,
    isResizing,
    handleMouseDown,
  } = useResizable({
    initialWidth: defaultWidth,
    minWidth,
    maxWidth,
  });

  return (
    <aside
      className="flex-shrink-0 border-r border-light-border-primary dark:border-dark-border-primary bg-light-bg-primary dark:bg-dark-bg-secondary relative"
      style={{ width: `${width}px` }}
    >
      <div className="h-full flex flex-col p-4 overflow-y-auto">
        <div className="text-light-text-primary dark:text-dark-text-primary">
          <h3 className="font-semibold mb-4">Left Sidebar</h3>

          {/* TODO: Search Bar */}
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
