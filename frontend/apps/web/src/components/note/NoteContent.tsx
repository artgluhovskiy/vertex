interface NoteContentProps {
  content: string;
}

export function NoteContent({ content }: NoteContentProps) {
  // TODO: Implement markdown rendering with react-markdown
  // TODO: Add syntax highlighting for code blocks
  // TODO: Add support for wikilinks [[Note Title]]
  // TODO: Add support for embedded images
  // TODO: Style with prose classes

  return (
    <div className="prose dark:prose-invert max-w-none">
      <div className="text-light-text-primary dark:text-dark-text-primary">
        <p className="text-sm text-light-text-muted dark:text-dark-text-muted mb-4">
          TODO: Markdown rendering
        </p>
        <div className="whitespace-pre-wrap">{content}</div>
      </div>
    </div>
  );
}
