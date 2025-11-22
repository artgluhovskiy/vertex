interface ChevronIconProps {
  /** Optional CSS classes to apply */
  className?: string;
  /** Whether the chevron should be rotated 90 degrees (pointing down) */
  rotated?: boolean;
}

/**
 * Reusable chevron icon component.
 * Used for expand/collapse indicators in tree structures and collapsible sections.
 */
export function ChevronIcon({ className, rotated = false }: ChevronIconProps) {
  return (
    <svg
      width="16"
      height="16"
      viewBox="0 0 16 16"
      fill="currentColor"
      className={className}
      style={{
        transform: rotated ? 'rotate(90deg)' : 'rotate(0deg)',
        transition: 'transform 0.2s ease-in-out',
      }}
    >
      <path d="M5 3l6 5-6 5V3z" />
    </svg>
  );
}
