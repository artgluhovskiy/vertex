import { useAuth } from '@synapse/api/hooks';
import { useNotes } from '@synapse/api/hooks';
import { useUiStore } from '@synapse/core/stores';

// Mock notes data for demo mode
const MOCK_NOTES = [
  {
    id: '1',
    userId: 'demo-user-123',
    directoryId: null,
    title: 'Welcome to Synapse',
    content: 'This is a demo note. In demo mode, you can explore the UI without a backend. Click "Create Note" to see the interface (note: actual creation requires backend).',
    summary: null,
    tags: [],
    metadata: {},
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    version: 1,
  },
  {
    id: '2',
    userId: 'demo-user-123',
    directoryId: null,
    title: 'Getting Started with Notes',
    content: 'Synapse is your AI-powered second brain. Create notes, organize them in folders, add tags, and let AI help you find connections between your ideas.',
    summary: 'A guide to getting started with note-taking',
    tags: [],
    metadata: {},
    createdAt: new Date(Date.now() - 86400000).toISOString(),
    updatedAt: new Date(Date.now() - 86400000).toISOString(),
    version: 1,
  },
  {
    id: '3',
    userId: 'demo-user-123',
    directoryId: null,
    title: 'Features Overview',
    content: '- Markdown support\n- AI-powered search\n- Automatic linking\n- Graph visualization\n- Tag management\n- Directory organization',
    summary: null,
    tags: [],
    metadata: {},
    createdAt: new Date(Date.now() - 172800000).toISOString(),
    updatedAt: new Date(Date.now() - 172800000).toISOString(),
    version: 1,
  },
];

export const DashboardPage: React.FC = () => {
  const { user, logout } = useAuth();
  const { notes: apiNotes, isLoading, error } = useNotes();
  const { theme, toggleTheme } = useUiStore();

  // Use mock notes if in demo mode (API error) or if no notes from API
  const isDemoMode = localStorage.getItem('auth_token') === 'demo-token-12345';
  const notes = isDemoMode || error ? MOCK_NOTES : apiNotes;

  return (
    <div className="min-h-screen bg-light-bg-secondary dark:bg-dark-bg-primary">
      {/* Header */}
      <header className="bg-light-bg-primary dark:bg-dark-bg-secondary border-b border-light-border-primary dark:border-dark-border-primary px-6 py-4">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold text-light-text-primary dark:text-dark-text-primary">Synapse</h1>
          <div className="flex items-center gap-4">
            <button
              onClick={toggleTheme}
              className="px-4 py-2 text-sm bg-light-bg-tertiary dark:bg-dark-bg-tertiary hover:bg-light-bg-hover dark:hover:bg-dark-bg-hover rounded-lg transition-colors text-light-text-primary dark:text-dark-text-primary"
            >
              {theme === 'dark' ? '☀️ Light' : '🌙 Dark'}
            </button>
            <span className="text-sm text-light-text-secondary dark:text-dark-text-secondary">
              {user?.email}
            </span>
            <button
              onClick={logout}
              className="px-4 py-2 text-sm bg-error hover:bg-error-hover text-white rounded-lg transition-colors"
            >
              Logout
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto p-6">
        <div className="mb-8">
          <h2 className="text-3xl font-bold mb-2 text-light-text-primary dark:text-dark-text-primary">
            Welcome to Synapse
          </h2>
          <p className="text-light-text-secondary dark:text-dark-text-secondary">
            Your AI-powered second brain for knowledge management
          </p>
          {isDemoMode && (
            <div className="mt-4 p-4 bg-secondary-light/10 dark:bg-secondary/20 border border-secondary-light dark:border-secondary rounded-lg">
              <p className="text-sm text-secondary-dark dark:text-secondary-light">
                🚀 <strong>Demo Mode</strong> - You&apos;re viewing mock data. Connect to backend to use full features.
              </p>
            </div>
          )}
        </div>

        {/* Notes Section */}
        <section className="card p-6">
          <h3 className="text-xl font-semibold mb-4 text-light-text-primary dark:text-dark-text-primary">
            Recent Notes
          </h3>

          {isLoading ? (
            <div className="text-center py-8 text-light-text-muted dark:text-dark-text-muted">
              Loading notes...
            </div>
          ) : notes.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-light-text-muted dark:text-dark-text-muted mb-4">
                No notes yet. Create your first note to get started!
              </p>
              <button className="btn-primary">
                Create Note
              </button>
            </div>
          ) : (
            <div className="space-y-3">
              {notes.map((note) => (
                <div
                  key={note.id}
                  className="p-4 bg-light-bg-tertiary dark:bg-dark-bg-tertiary rounded-lg hover:bg-light-bg-hover dark:hover:bg-dark-bg-hover transition-colors cursor-pointer border border-light-border-primary dark:border-dark-border-primary"
                >
                  <h4 className="font-medium mb-1 text-light-text-primary dark:text-dark-text-primary">
                    {note.title}
                  </h4>
                  <p className="text-sm text-light-text-secondary dark:text-dark-text-secondary line-clamp-2">
                    {note.content}
                  </p>
                </div>
              ))}
            </div>
          )}
        </section>

        {/* Info Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-6">
          <div className="card p-6">
            <h4 className="font-semibold mb-2 text-light-text-primary dark:text-dark-text-primary">
              📝 Notes
            </h4>
            <p className="text-2xl font-bold text-primary">
              {notes.length}
            </p>
          </div>

          <div className="card p-6">
            <h4 className="font-semibold mb-2 text-light-text-primary dark:text-dark-text-primary">
              📁 Folders
            </h4>
            <p className="text-2xl font-bold text-success">
              0
            </p>
          </div>

          <div className="card p-6">
            <h4 className="font-semibold mb-2 text-light-text-primary dark:text-dark-text-primary">
              🏷️ Tags
            </h4>
            <p className="text-2xl font-bold text-secondary">
              0
            </p>
          </div>
        </div>
      </main>
    </div>
  );
};
