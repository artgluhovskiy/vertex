import { ContextCard } from './ContextCard';

export function SuggestedLinksSection() {
  // TODO: Connect to AI suggested links API (when available)
  // TODO: Display AI-proposed connections
  // TODO: Add accept/dismiss actions
  // TODO: Show confidence scores

  const mockSuggestedLinks = [
    { id: '1', title: 'Architecture Patterns Overview', score: 0.76 },
  ];

  return (
    <div className="mb-6">
      <h4 className="text-sm font-semibold text-light-text-primary dark:text-dark-text-primary mb-3">
        Suggested Links
      </h4>
      <div className="space-y-2">
        {mockSuggestedLinks.map((link) => (
          <ContextCard
            key={link.id}
            title={link.title}
            confidenceScore={link.score}
          />
        ))}
      </div>
      <p className="text-xs text-light-text-muted dark:text-dark-text-muted mt-2">
        TODO: AI link suggestions
      </p>
    </div>
  );
}
