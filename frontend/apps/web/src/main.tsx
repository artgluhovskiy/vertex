import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import './styles/globals.css';

/**
 * Enable MSW (Mock Service Worker) in development if VITE_ENABLE_API_MOCKING is true.
 * This allows the app to work without a running backend by intercepting API calls.
 */
async function enableMocking() {
  if (import.meta.env.VITE_ENABLE_API_MOCKING !== 'true') {
    return;
  }

  const { worker } = await import('./mocks/browser');

  // Start the worker and wait for it to be ready
  return worker.start({
    onUnhandledRequest: 'bypass', // Don't warn about unhandled requests
  });
}

enableMocking().then(() => {
  ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
      <App />
    </React.StrictMode>
  );
});
