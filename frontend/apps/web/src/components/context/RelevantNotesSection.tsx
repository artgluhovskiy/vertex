import { ContextCard } from './ContextCard';

export function RelevantNotesSection() {
  // TODO: Connect to AI context API (when available)
  // TODO: Display semantically similar notes
  // TODO: Show confidence scores
  // TODO: Add click handler to navigate to notes

  const mockRelevantNotes = [
    { id: '1', title: 'Postmortem Guidelines', score: 0.82, badge: '' },
    { id: '2', title: 'Incident Retrospective 2024', score: 0.75, badge: '1 hop' },
    { id: '3', title: 'Kafka Retry Strategy', score: 0.68, badge: 'same domain' },
  ];

  return (
    <div className="mb-6">
      <h4 className="text-sm font-semibold text-light-text-primary dark:text-dark-text-primary mb-3">
        Relevant Notes
      </h4>
      <div className="space-y-2">
        {mockRelevantNotes.map((note) => (
          <ContextCard
            key={note.id}
            title={note.title}
            confidenceScore={note.score}
            badge={note.badge}
          />
        ))}
      </div>
      <p className="text-xs text-light-text-muted dark:text-dark-text-muted mt-2">
        TODO: AI-powered suggestions
      </p>
    </div>
  );
}
