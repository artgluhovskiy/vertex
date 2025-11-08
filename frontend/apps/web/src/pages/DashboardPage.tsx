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
    <div className="min-h-screen bg-gray-50 dark:bg-gray-950">
      {/* Header */}
      <header className="bg-white dark:bg-gray-900 border-b border-gray-200 dark:border-gray-800 px-6 py-4">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold dark:text-gray-100">Synapse</h1>
          <div className="flex items-center gap-4">
            <button
              onClick={toggleTheme}
              className="px-4 py-2 text-sm bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 dark:hover:bg-gray-700 rounded-lg transition-colors dark:text-gray-100"
            >
              {theme === 'dark' ? '☀️ Light' : '🌙 Dark'}
            </button>
            <span className="text-sm text-gray-600 dark:text-gray-400">
              {user?.email}
            </span>
            <button
              onClick={logout}
              className="px-4 py-2 text-sm bg-red-500 hover:bg-red-600 text-white rounded-lg transition-colors"
            >
              Logout
            </button>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <main className="max-w-7xl mx-auto p-6">
        <div className="mb-8">
          <h2 className="text-3xl font-bold mb-2 dark:text-gray-100">
            Welcome to Synapse
          </h2>
          <p className="text-gray-600 dark:text-gray-400">
            Your AI-powered second brain for knowledge management
          </p>
          {isDemoMode && (
            <div className="mt-4 p-4 bg-purple-50 dark:bg-purple-900/20 border border-purple-200 dark:border-purple-800 rounded-lg">
              <p className="text-sm text-purple-800 dark:text-purple-200">
                🚀 <strong>Demo Mode</strong> - You're viewing mock data. Connect to backend to use full features.
              </p>
            </div>
          )}
        </div>

        {/* Notes Section */}
        <section className="bg-white dark:bg-gray-900 rounded-lg shadow-sm border border-gray-200 dark:border-gray-800 p-6">
          <h3 className="text-xl font-semibold mb-4 dark:text-gray-100">
            Recent Notes
          </h3>

          {isLoading ? (
            <div className="text-center py-8 text-gray-500 dark:text-gray-400">
              Loading notes...
            </div>
          ) : notes.length === 0 ? (
            <div className="text-center py-8">
              <p className="text-gray-500 dark:text-gray-400 mb-4">
                No notes yet. Create your first note to get started!
              </p>
              <button className="px-6 py-2 bg-blue-500 hover:bg-blue-600 text-white rounded-lg transition-colors">
                Create Note
              </button>
            </div>
          ) : (
            <div className="space-y-3">
              {notes.map((note) => (
                <div
                  key={note.id}
                  className="p-4 bg-gray-50 dark:bg-gray-800 rounded-lg hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors cursor-pointer"
                >
                  <h4 className="font-medium mb-1 dark:text-gray-100">
                    {note.title}
                  </h4>
                  <p className="text-sm text-gray-600 dark:text-gray-400 line-clamp-2">
                    {note.content}
                  </p>
                </div>
              ))}
            </div>
          )}
        </section>

        {/* Info Cards */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mt-6">
          <div className="bg-white dark:bg-gray-900 rounded-lg shadow-sm border border-gray-200 dark:border-gray-800 p-6">
            <h4 className="font-semibold mb-2 dark:text-gray-100">
              📝 Notes
            </h4>
            <p className="text-2xl font-bold text-blue-500">
              {notes.length}
            </p>
          </div>

          <div className="bg-white dark:bg-gray-900 rounded-lg shadow-sm border border-gray-200 dark:border-gray-800 p-6">
            <h4 className="font-semibold mb-2 dark:text-gray-100">
              📁 Folders
            </h4>
            <p className="text-2xl font-bold text-green-500">
              0
            </p>
          </div>

          <div className="bg-white dark:bg-gray-900 rounded-lg shadow-sm border border-gray-200 dark:border-gray-800 p-6">
            <h4 className="font-semibold mb-2 dark:text-gray-100">
              🏷️ Tags
            </h4>
            <p className="text-2xl font-bold text-purple-500">
              0
            </p>
          </div>
        </div>
      </main>
    </div>
  );
};
