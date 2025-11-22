import { RelevantNotesSection, SuggestedLinksSection, MetadataDisplay } from '@/components/context';

export function RightSidebar() {
  // TODO: Add collapse button functionality
  // TODO: Connect to real AI APIs when available

  return (
    <div className="h-full flex flex-col">
      {/* Header */}
      <div className="p-4 border-b border-light-border-primary dark:border-dark-border-primary">
        <div className="flex items-center justify-between">
          <h3 className="font-semibold text-light-text-primary dark:text-dark-text-primary">
            Relevant
          </h3>
          {/* TODO: Add collapse button */}
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-y-auto p-4">
        <RelevantNotesSection />
        <SuggestedLinksSection />
        <MetadataDisplay />
      </div>
    </div>
  );
}
