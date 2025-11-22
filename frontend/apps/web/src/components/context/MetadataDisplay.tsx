export function MetadataDisplay() {
  // TODO: Display note source URL
  // TODO: Display last updated timestamp
  // TODO: Display created date
  // TODO: Display note version

  return (
    <div className="pt-4 border-t border-light-border-primary dark:border-dark-border-primary">
      <div className="space-y-2 text-xs text-light-text-muted dark:text-dark-text-muted">
        <div>
          <span className="font-medium">Source:</span>{' '}
          <a href="#" className="text-primary hover:underline">
            medium.com
          </a>
        </div>
        <div>
          <span className="font-medium">Updated:</span> 3d ago
        </div>
        <div>
          <span className="font-medium">Created:</span> 2024-01-15
        </div>
      </div>
      <p className="text-xs text-light-text-muted dark:text-dark-text-muted mt-3">
        TODO: Real metadata display
      </p>
    </div>
  );
}
