import { useState } from 'react';

function App() {
  const [count, setCount] = useState(0);

  return (
    <div className="min-h-screen flex flex-col items-center justify-center">
      <h1 className="text-4xl font-bold mb-8 text-accent-primary">
        Synapse
      </h1>
      <p className="text-text-secondary mb-4">
        AI-Powered Note-Taking Application
      </p>
      <div className="bg-bg-card border border-border-primary rounded-lg p-8">
        <button
          onClick={() => setCount(count + 1)}
          className="bg-accent-primary hover:bg-accent-secondary text-white px-6 py-2 rounded-lg transition-colors"
        >
          Count: {count}
        </button>
      </div>
      <p className="mt-8 text-text-muted text-sm">
        Frontend monorepo initialized successfully! 🎉
      </p>
    </div>
  );
}

export default App;
