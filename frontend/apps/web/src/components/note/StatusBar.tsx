export function StatusBar() {
  // TODO: Calculate word count
  // TODO: Calculate character count
  // TODO: Calculate read time
  // TODO: Show sync status
  // TODO: Show last synced timestamp

  return (
    <div className="border-t border-light-border-primary dark:border-dark-border-primary px-6 py-2 flex items-center justify-between text-xs text-light-text-muted dark:text-dark-text-muted">
      {/* Left side - Stats */}
      <div className="flex items-center gap-4">
        <span>61 words</span>
        <span>•</span>
        <span>396 characters</span>
        <span>•</span>
        <span>1 min read</span>
      </div>

      {/* Right side - Sync status */}
      <div className="flex items-center gap-2">
        <span className="w-2 h-2 rounded-full bg-success"></span>
        <span>Synced 2s ago</span>
      </div>
    </div>
  );
}
