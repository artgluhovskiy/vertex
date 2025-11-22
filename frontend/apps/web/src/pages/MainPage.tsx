import { MainLayout } from '@/components/layout';
import { NoteViewer, NoteContent } from '@/components/note';

export const MainPage: React.FC = () => {
  // TODO: Get note data from API
  // TODO: Handle routing for different notes
  // TODO: Add loading states
  // TODO: Add error states

  const mockNoteContent = `
# Welcome to Synapse

This is the main application skeleton with placeholder components.

## What's Implemented

- ✅ Three-panel layout structure
- ✅ Left sidebar (navigation)
- ✅ Right sidebar (context/AI)
- ✅ Icon toolbar with theme toggle
- ✅ Note viewer structure
- ✅ Status bar with stats

## What's Next (TODOs)

### Design System
- Implement Button variants and styling
- Implement Input with icon support
- Implement Badge colors and variants
- Implement Card glassmorphism effect

### Layout Components
- Add collapsible sidebar functionality
- Implement TabBar with multi-tab support
- Add breadcrumb navigation

### Feature Components
- Wire up NotesSection to real API
- Implement FoldersSection tree structure
- Implement SmartShelvesSection with tags
- Add markdown rendering
- Connect AI suggestions

### Polish
- Add animations and transitions
- Implement responsive design
- Add keyboard shortcuts
- Improve accessibility
`;

  return (
    <MainLayout>
      <NoteViewer>
        <NoteContent content={mockNoteContent} />
      </NoteViewer>
    </MainLayout>
  );
};
