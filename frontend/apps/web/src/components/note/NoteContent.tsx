import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';

interface NoteContentProps {
  content?: string;
}

const defaultContent = `# How to implement highly effective RAG with semantic search?

## How to implement highly effective RAG in the application with semantic search support?

*ChatGPT said:*

Cześć Artem! Here's a compact, battle-tested blueprint for building a **highly effective RAG** with hybrid (vector + lexical + graph) search in Java/Spring.

Start with pgvector for your embeddings store, combine it with PostgreSQL's full-text search capabilities, and use a weighted scoring function to merge results. The key is to tune your ranking formula based on user feedback and query patterns.`;

export function NoteContent({ content = defaultContent }: NoteContentProps) {
  return (
    <div className="prose prose-sm dark:prose-invert max-w-none px-8 py-6">
      <ReactMarkdown
        remarkPlugins={[remarkGfm]}
        components={{
          h1: ({ children }) => (
            <h1 className="text-xl font-bold text-light-text-primary dark:text-dark-text-primary mb-4 mt-6 first:mt-0">
              {children}
            </h1>
          ),
          h2: ({ children }) => (
            <h2 className="text-lg font-semibold text-light-text-primary dark:text-dark-text-primary mb-3 mt-5">
              {children}
            </h2>
          ),
          p: ({ children }) => (
            <p className="text-light-text-primary dark:text-dark-text-primary mb-4 leading-relaxed">
              {children}
            </p>
          ),
          em: ({ children }) => (
            <em className="text-light-text-muted dark:text-dark-text-muted italic">
              {children}
            </em>
          ),
          strong: ({ children }) => (
            <strong className="font-bold text-light-text-primary dark:text-dark-text-primary">
              {children}
            </strong>
          ),
          code: ({ children, className }) => {
            const isInline = !className;
            return isInline ? (
              <code className="px-1.5 py-0.5 bg-light-bg-tertiary dark:bg-dark-bg-tertiary text-primary rounded text-sm font-mono">
                {children}
              </code>
            ) : (
              <code className={className}>{children}</code>
            );
          },
          ul: ({ children }) => (
            <ul className="list-disc list-inside mb-4 space-y-2 text-light-text-primary dark:text-dark-text-primary">
              {children}
            </ul>
          ),
          ol: ({ children }) => (
            <ol className="list-decimal list-inside mb-4 space-y-2 text-light-text-primary dark:text-dark-text-primary">
              {children}
            </ol>
          ),
        }}
      >
        {content}
      </ReactMarkdown>
    </div>
  );
}
