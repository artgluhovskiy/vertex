import { useMemo } from 'react';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

interface NoteContentProps {
  content?: string;
}

export function NoteContent({ content }: NoteContentProps) {
  // Memoize markdown components to prevent recreation on every render
  const markdownComponents = useMemo(() => ({
    h1: ({ children }: any) => (
      <h1 className="text-xl font-bold text-light-text-primary dark:text-dark-text-primary mb-4 mt-6 first:mt-0">
        {children}
      </h1>
    ),
    h2: ({ children }: any) => (
      <h2 className="text-lg font-semibold text-light-text-primary dark:text-dark-text-primary mb-3 mt-5">
        {children}
      </h2>
    ),
    p: ({ children }: any) => (
      <p className="text-light-text-primary dark:text-dark-text-primary mb-4 leading-relaxed">
        {children}
      </p>
    ),
    em: ({ children }: any) => (
      <em className="text-light-text-muted dark:text-dark-text-muted italic">
        {children}
      </em>
    ),
    strong: ({ children }: any) => (
      <strong className="font-bold text-light-text-primary dark:text-dark-text-primary">
        {children}
      </strong>
    ),
    code: ({ children, className }: any) => {
      const isInline = !className;
      return isInline ? (
        <code className="px-1.5 py-0.5 bg-light-bg-tertiary dark:bg-dark-bg-tertiary text-primary rounded text-sm font-mono">
          {children}
        </code>
      ) : (
        <code className={className}>{children}</code>
      );
    },
    ul: ({ children }: any) => (
      <ul className="list-disc list-inside mb-4 space-y-2 text-light-text-primary dark:text-dark-text-primary">
        {children}
      </ul>
    ),
    ol: ({ children }: any) => (
      <ol className="list-decimal list-inside mb-4 space-y-2 text-light-text-primary dark:text-dark-text-primary">
        {children}
      </ol>
    ),
  }), []);

  if (!content) {
    return (
      <div className="flex items-center justify-center h-full text-light-text-muted dark:text-dark-text-muted">
        <p className="italic">No content available</p>
      </div>
    );
  }

  return (
    <div className="prose prose-sm dark:prose-invert max-w-none px-8 py-6">
      <ReactMarkdown
        remarkPlugins={[remarkGfm]}
        components={markdownComponents}
      >
        {content}
      </ReactMarkdown>
    </div>
  );
}
