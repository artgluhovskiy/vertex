import { Badge } from '@synapse/ui';

interface NoteHeaderProps {
  title?: string;
  breadcrumb?: string;
  tags?: string[];
}

export function NoteHeader({
  title = 'Context – Tech – Highly Effective RAG',
  breadcrumb = 'Tech',
  tags = ['rag', 'retrieval', 'ai']
}: NoteHeaderProps) {
  return (
    <div className="border-b border-light-border-primary dark:border-dark-border-primary px-8 py-6">
      {/* Breadcrumb */}
      <div className="flex items-center gap-2 text-xs text-light-text-muted dark:text-dark-text-muted mb-3">
        <span>🏠</span>
        <span>›</span>
        <span className="hover:text-primary cursor-pointer transition-colors">{breadcrumb}</span>
      </div>

      {/* Title */}
      <h1 className="text-2xl font-bold text-light-text-primary dark:text-dark-text-primary mb-4">
        {title}
      </h1>

      {/* Tags */}
      {tags && tags.length > 0 && (
        <div className="flex gap-2 flex-wrap">
          {tags.map((tag, index) => (
            <Badge key={index} variant="primary" size="sm">
              {tag}
            </Badge>
          ))}
        </div>
      )}
    </div>
  );
}
