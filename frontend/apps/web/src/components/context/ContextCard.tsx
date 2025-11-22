interface ContextCardProps {
  title: string;
  confidenceScore: number;
  badge?: string;
  onClick?: () => void;
}

export function ContextCard({
  title,
  confidenceScore,
  badge,
  onClick,
}: ContextCardProps) {
  // TODO: Implement card hover effects
  // TODO: Add info tooltip for confidence score
  // TODO: Style with glassmorphism

  return (
    <button
      onClick={onClick}
      className="w-full p-3 bg-light-bg-tertiary dark:bg-dark-bg-tertiary border border-light-border-primary dark:border-dark-border-primary rounded-md hover:border-primary transition-all text-left"
    >
      <div className="flex items-start justify-between mb-1">
        <div className="flex-1">
          <div className="font-medium text-sm text-light-text-primary dark:text-dark-text-primary">
            {title}
          </div>
        </div>
        <span className="ml-2 px-2 py-0.5 text-xs bg-secondary/10 text-secondary rounded-full">
          {confidenceScore.toFixed(2)}
        </span>
      </div>
      {badge && (
        <div className="flex items-center gap-2 mt-2">
          <span className="px-2 py-0.5 text-xs bg-light-bg-hover dark:bg-dark-bg-hover text-light-text-secondary dark:text-dark-text-secondary rounded">
            {badge}
          </span>
          <button className="text-light-text-muted dark:text-dark-text-muted hover:text-light-text-primary dark:hover:text-dark-text-primary text-xs">
            ⓘ
          </button>
        </div>
      )}
    </button>
  );
}
