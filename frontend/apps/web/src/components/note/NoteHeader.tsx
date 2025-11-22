export function NoteHeader() {
  // TODO: Add breadcrumb navigation (🏠 › Tech)
  // TODO: Add note title
  // TODO: Add tag pills below title
  // TODO: Add last updated timestamp

  return (
    <div className="border-b border-light-border-primary dark:border-dark-border-primary p-6">
      <div className="text-xs text-light-text-muted dark:text-dark-text-muted mb-2">
        🏠 › Tech
      </div>
      <h1 className="text-2xl font-bold text-light-text-primary dark:text-dark-text-primary mb-3">
        TODO: Note Title
      </h1>
      <div className="flex gap-2">
        <span className="px-2 py-1 text-xs bg-primary/10 text-primary rounded-full">
          TODO: tag1
        </span>
        <span className="px-2 py-1 text-xs bg-secondary/10 text-secondary rounded-full">
          TODO: tag2
        </span>
      </div>
    </div>
  );
}
