import { NotesSection, FoldersSection, SmartShelvesSection } from '../sidebar';

export function LeftSidebar() {
  // TODO: Add search bar
  // TODO: Add "New Note" button
  // TODO: Add sort/filter buttons

  return (
    <div className="h-full flex flex-col p-4 overflow-y-auto">
      <div className="text-light-text-primary dark:text-dark-text-primary">
        <h3 className="font-semibold mb-4">Left Sidebar</h3>

        {/* TODO: Search Bar */}
        {/* TODO: New Note Button */}
        {/* TODO: Sort & Filter */}

        {/* Sections */}
        <div className="space-y-2">
          <NotesSection />
          <FoldersSection />
          <SmartShelvesSection />
        </div>
      </div>
    </div>
  );
}
