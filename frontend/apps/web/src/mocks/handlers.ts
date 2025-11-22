import { http, HttpResponse } from 'msw';
import { mockDirectories } from './data/directories';
import { mockNotes } from './data/notes';

// API base URL - must match the axios client baseURL
const API_BASE_URL = 'http://localhost:8080/api/v1';

/**
 * MSW handlers for mocking API endpoints during development.
 *
 * These handlers intercept HTTP requests and return mock data,
 * allowing the frontend to work without a running backend.
 */
export const handlers = [
  // GET /api/v1/directories - Get all directories
  http.get(`${API_BASE_URL}/directories`, () => {
    return HttpResponse.json(mockDirectories, {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }),

  // GET /api/v1/directories/root - Get ROOT directory
  http.get(`${API_BASE_URL}/directories/root`, () => {
    const rootDirectory = mockDirectories.find((dir) => dir.parentId === null);
    if (!rootDirectory) {
      return HttpResponse.json(
        { error: 'Root directory not found' },
        { status: 404 }
      );
    }
    return HttpResponse.json(rootDirectory, {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }),

  // GET /api/v1/directories/:id - Get directory by ID
  http.get(`${API_BASE_URL}/directories/:id`, ({ params }) => {
    const { id } = params;
    const directory = mockDirectories.find((dir) => dir.id === id);

    if (!directory) {
      return HttpResponse.json(
        { error: 'Directory not found' },
        { status: 404 }
      );
    }

    return HttpResponse.json(directory, {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }),

  // GET /api/v1/notes - Get all notes
  http.get(`${API_BASE_URL}/notes`, () => {
    return HttpResponse.json(mockNotes, {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }),

  // GET /api/v1/notes/:id - Get note by ID
  http.get(`${API_BASE_URL}/notes/:id`, ({ params }) => {
    const { id } = params;
    const note = mockNotes.find((n) => n.id === id);

    if (!note) {
      return HttpResponse.json(
        { error: 'Note not found' },
        { status: 404 }
      );
    }

    return HttpResponse.json(note, {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }),

  // POST /api/v1/directories - Create directory
  http.post(`${API_BASE_URL}/directories`, async ({ request }) => {
    const body = await request.json();
    const newDirectory = {
      id: `dir-${Date.now()}`,
      userId: 'user-1',
      ...(body as any),
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    // In a real scenario, we'd add this to the mock data
    // For now, just return the created directory
    return HttpResponse.json(newDirectory, {
      status: 201,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }),

  // POST /api/v1/notes - Create note
  http.post(`${API_BASE_URL}/notes`, async ({ request }) => {
    const body = await request.json();
    const newNote = {
      id: `note-${Date.now()}`,
      userId: 'user-1',
      ...(body as any),
      createdAt: new Date(),
      updatedAt: new Date(),
    };

    return HttpResponse.json(newNote, {
      status: 201,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }),

  // PUT /api/v1/directories/:id - Update directory
  http.put(`${API_BASE_URL}/directories/:id`, async ({ params, request }) => {
    const { id } = params;
    const body = await request.json();
    const directory = mockDirectories.find((dir) => dir.id === id);

    if (!directory) {
      return HttpResponse.json(
        { error: 'Directory not found' },
        { status: 404 }
      );
    }

    const updatedDirectory = {
      ...directory,
      ...(body as any),
      updatedAt: new Date(),
    };

    return HttpResponse.json(updatedDirectory, {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }),

  // PUT /api/v1/notes/:id - Update note
  http.put(`${API_BASE_URL}/notes/:id`, async ({ params, request }) => {
    const { id } = params;
    const body = await request.json();
    const note = mockNotes.find((n) => n.id === id);

    if (!note) {
      return HttpResponse.json(
        { error: 'Note not found' },
        { status: 404 }
      );
    }

    const updatedNote = {
      ...note,
      ...(body as any),
      updatedAt: new Date(),
    };

    return HttpResponse.json(updatedNote, {
      status: 200,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }),

  // DELETE /api/v1/directories/:id - Delete directory
  http.delete(`${API_BASE_URL}/directories/:id`, ({ params }) => {
    const { id } = params;
    const directory = mockDirectories.find((dir) => dir.id === id);

    if (!directory) {
      return HttpResponse.json(
        { error: 'Directory not found' },
        { status: 404 }
      );
    }

    return HttpResponse.json(
      { message: 'Directory deleted successfully' },
      { status: 200 }
    );
  }),

  // DELETE /api/v1/notes/:id - Delete note
  http.delete(`${API_BASE_URL}/notes/:id`, ({ params }) => {
    const { id } = params;
    const note = mockNotes.find((n) => n.id === id);

    if (!note) {
      return HttpResponse.json(
        { error: 'Note not found' },
        { status: 404 }
      );
    }

    return HttpResponse.json(
      { message: 'Note deleted successfully' },
      { status: 200 }
    );
  }),
];
