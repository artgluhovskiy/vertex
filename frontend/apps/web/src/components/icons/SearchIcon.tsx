interface SearchIconProps {
  /** Optional CSS classes to apply */
  className?: string;
  /** Icon size (width and height in pixels) */
  size?: number;
}

/**
 * Reusable search icon component.
 * Used for search input fields and search-related actions.
 */
export function SearchIcon({ className, size = 16 }: SearchIconProps) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 16 16"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.5"
      strokeLinecap="round"
      strokeLinejoin="round"
      className={className}
      aria-hidden="true"
    >
      <circle cx="6.5" cy="6.5" r="4.5" />
      <path d="M9.5 9.5l3.5 3.5" />
    </svg>
  );
}
