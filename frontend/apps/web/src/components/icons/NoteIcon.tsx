interface NoteIconProps {
  /** Optional CSS classes to apply */
  className?: string;
}

/**
 * Reusable note/document icon component.
 * Used to indicate notes in tree structures and lists.
 */
export function NoteIcon({ className }: NoteIconProps) {
  return (
    <svg
      width="14"
      height="14"
      viewBox="0 0 16 16"
      fill="currentColor"
      className={className}
    >
      <path d="M4 2a2 2 0 0 0-2 2v8a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V4a2 2 0 0 0-2-2H4zm0 1h8a1 1 0 0 1 1 1v8a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1z M5 5h6v1H5V5z M5 7h6v1H5V7z M5 9h4v1H5V9z" />
    </svg>
  );
}
