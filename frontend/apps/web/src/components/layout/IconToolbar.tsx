import { useUiStore } from '@synapse/core/stores';

export function IconToolbar() {
  const { theme, toggleTheme } = useUiStore();

  // TODO: Add fullscreen button
  // TODO: Add graph view button
  // TODO: Add settings button
  // TODO: Add links button
  // TODO: Add tags button
  // TODO: Add help button (bottom)

  return (
    <div className="h-full flex flex-col items-center py-4 gap-2">
      {/* Placeholder buttons */}
      <button
        className="w-8 h-8 flex items-center justify-center text-light-text-muted dark:text-dark-text-muted hover:bg-light-bg-hover dark:hover:bg-dark-bg-hover rounded transition-colors"
        title="Fullscreen"
      >
        ↗
      </button>

      <button
        className="w-8 h-8 flex items-center justify-center text-light-text-muted dark:text-dark-text-muted hover:bg-light-bg-hover dark:hover:bg-dark-bg-hover rounded transition-colors"
        title="Graph View"
      >
        🕸
      </button>

      <button
        className="w-8 h-8 flex items-center justify-center text-light-text-muted dark:text-dark-text-muted hover:bg-light-bg-hover dark:hover:bg-dark-bg-hover rounded transition-colors"
        title="Settings"
      >
        ⚙
      </button>

      <button
        className="w-8 h-8 flex items-center justify-center text-light-text-muted dark:text-dark-text-muted hover:bg-light-bg-hover dark:hover:bg-dark-bg-hover rounded transition-colors"
        title="Links"
      >
        🔗
      </button>

      <button
        className="w-8 h-8 flex items-center justify-center text-light-text-muted dark:text-dark-text-muted hover:bg-light-bg-hover dark:hover:bg-dark-bg-hover rounded transition-colors"
        title="Tags"
      >
        🏷
      </button>

      {/* Theme Toggle - Active state */}
      <button
        onClick={toggleTheme}
        className="w-8 h-8 flex items-center justify-center bg-primary text-white rounded transition-colors"
        title="Toggle Theme"
      >
        {theme === 'dark' ? '🌙' : '☀'}
      </button>

      {/* Spacer */}
      <div className="flex-1" />

      {/* Help - Bottom */}
      <button
        className="w-8 h-8 flex items-center justify-center text-light-text-muted dark:text-dark-text-muted hover:bg-light-bg-hover dark:hover:bg-dark-bg-hover rounded transition-colors"
        title="Help"
      >
        ?
      </button>
    </div>
  );
}
