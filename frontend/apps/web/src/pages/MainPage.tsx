import { useState } from 'react';
import { MainLayout } from '@/components/layout';
import { NoteViewer, NoteContent } from '@/components/note';
import { useNote } from '@synapse/api/hooks';

export const MainPage: React.FC = () => {
  const [selectedNoteId, setSelectedNoteId] = useState<string | null>(null);

  // Fetch selected note data
  const { note, isLoading, error } = useNote(selectedNoteId || '');

  const handleNoteSelect = (noteId: string) => {
    setSelectedNoteId(noteId);
  };

  return (
    <MainLayout onNoteSelect={handleNoteSelect} selectedNoteId={selectedNoteId}>
      {isLoading ? (
        <div className="flex items-center justify-center h-full">
          <div className="text-light-text-muted dark:text-dark-text-muted">
            Loading note...
          </div>
        </div>
      ) : error ? (
        <div className="flex items-center justify-center h-full">
          <div className="text-error">
            Failed to load note: {error.message}
          </div>
        </div>
      ) : note ? (
        <NoteViewer>
          <NoteContent content={note.content} />
        </NoteViewer>
      ) : (
        <div className="flex flex-col items-center justify-center h-full text-center px-8">
          <div className="text-6xl mb-4">📝</div>
          <h2 className="text-xl font-semibold text-light-text-primary dark:text-dark-text-primary mb-2">
            Select a note to view
          </h2>
          <p className="text-light-text-muted dark:text-dark-text-muted">
            Click on a note from the folders section to display its content here
          </p>
        </div>
      )}
    </MainLayout>
  );
};
