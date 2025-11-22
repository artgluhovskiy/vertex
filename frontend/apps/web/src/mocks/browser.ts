import { setupWorker } from 'msw/browser';
import { handlers } from './handlers';

/**
 * MSW browser worker for intercepting API requests in development.
 *
 * This creates a Service Worker that intercepts HTTP requests
 * and returns mock responses based on the handlers.
 */
export const worker = setupWorker(...handlers);
