# Frontend Architecture Explained for Backend Developers

> **For backend developers who want to understand how this frontend is built**

## 🎯 High-Level Overview

Think of the frontend like this:
- **Backend**: You have controllers, services, repositories, models
- **Frontend**: We have pages, components, hooks, stores, services

The frontend is essentially a **client application** that runs in the browser (like a desktop app, but in a browser). It talks to your backend API via HTTP requests.

---

## 📦 The Monorepo Structure (Like Microservices, but for Frontend)

### What is a Monorepo?
Instead of having separate repositories for different parts, everything lives in one repo but is organized into **packages** (like microservices, but they're libraries, not services).

```
frontend/
├── apps/web/          # The actual application (like your main Spring Boot app)
└── packages/          # Shared libraries (like your common modules)
    ├── api/           # HTTP client & API calls (like your REST client)
    ├── core/          # Business logic & state (like your service layer)
    ├── types/         # TypeScript types (like your DTOs/models)
    └── config/        # Configuration (like your application.yml)
```

**Backend Analogy:**
- `apps/web` = Your main Spring Boot application
- `packages/api` = Your HTTP client library (like RestTemplate or WebClient)
- `packages/core` = Your service layer with business logic
- `packages/types` = Your DTOs and domain models

---

## 🛠️ Core Technologies (The "Stack")

### 1. **React** - The UI Framework
**What it is:** A library for building user interfaces using components.

**Backend Analogy:** 
- Think of React components like **Java classes** or **functions**
- Each component is like a method that returns HTML (but called JSX)
- Components can have **state** (like instance variables)
- Components can receive **props** (like method parameters)

**Example:**
```tsx
// This is like a Java method that returns HTML
function LoginPage() {
  const [email, setEmail] = useState('');  // Like a private field
  
  return (
    <div>  {/* This is HTML, but written in JavaScript */}
      <input value={email} />
    </div>
  );
}
```

### 2. **TypeScript** - Type-Safe JavaScript
**What it is:** JavaScript with types (like Java vs JavaScript).

**Backend Analogy:**
- TypeScript = Java (statically typed)
- JavaScript = Python (dynamically typed)
- You get compile-time errors, autocomplete, and type safety

**Example:**
```typescript
// TypeScript (like Java)
interface User {
  id: string;
  email: string;
}

function getUser(): User {
  return { id: "1", email: "test@example.com" };
}

// vs JavaScript (like Python - no types)
function getUser() {
  return { id: "1", email: "test@example.com" };
}
```

### 3. **Vite** - Build Tool & Dev Server
**What it is:** Like Maven/Gradle, but for frontend. It:
- Compiles TypeScript to JavaScript
- Bundles all files into optimized packages
- Runs a dev server (like Spring Boot's embedded server)
- Hot Module Replacement (HMR) - changes appear instantly without refresh

**Backend Analogy:**
- `vite build` = `mvn package` (creates production build)
- `vite dev` = `mvn spring-boot:run` (runs dev server)
- The dev server proxies API calls to your backend (like a reverse proxy)

### 4. **React Router** - URL Routing
**What it is:** Handles different URLs and shows different components.

**Backend Analogy:**
- Like `@RequestMapping` in Spring
- `/login` → shows LoginPage component
- `/` → shows MainPage component
- Like your REST controllers, but for UI pages

**Example:**
```tsx
// Like @GetMapping("/login")
<Route path="/login" element={<LoginPage />} />

// Like @GetMapping("/")
<Route path="/" element={<MainPage />} />
```

### 5. **Zustand** - State Management (Client State)
**What it is:** A lightweight state management library (like a global cache/singleton).

**Backend Analogy:**
- Like a **singleton service** or **application-scoped bean**
- Stores data that needs to be shared across components
- Similar to storing data in `HttpSession` or `ApplicationContext`

**What it stores:**
- User authentication state (like session data)
- UI preferences (theme, sidebar collapsed, etc.)
- Persists to browser's localStorage (like a database for client-side data)

**Example:**
```typescript
// Like a Spring @Service with @ApplicationScope
const useAuthStore = create((set) => ({
  user: null,
  token: null,
  setAuth: (user, token) => set({ user, token }),
}));

// Any component can use it:
const { user, token } = useAuthStore(); // Like @Autowired
```

### 6. **React Query (TanStack Query)** - Server State Management
**What it is:** Manages data fetched from your backend API.

**Backend Analogy:**
- Like a **smart cache** for API responses
- Automatically handles:
  - Caching (like Redis cache)
  - Refetching (like cache invalidation)
  - Loading states
  - Error handling
  - Optimistic updates

**Why we need it:**
- Without it: Every component would manually fetch data, no caching, duplicate requests
- With it: Data is cached, shared across components, automatically refetched when stale

**Example:**
```typescript
// This hook automatically:
// 1. Fetches data from /api/notes
// 2. Caches the result
// 3. Shows loading state
// 4. Handles errors
// 5. Refetches when needed
const { data: notes, isLoading } = useQuery({
  queryKey: ['notes'],
  queryFn: () => apiClient.get('/notes')
});
```

### 7. **Axios** - HTTP Client
**What it is:** Like RestTemplate or WebClient in Spring.

**Backend Analogy:**
- Makes HTTP requests to your backend
- Has interceptors (like Spring interceptors)
  - Request interceptor: Adds auth token to every request
  - Response interceptor: Handles 401 errors, redirects to login

**Example:**
```typescript
// Like RestTemplate with interceptors
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token');
  config.headers.Authorization = `Bearer ${token}`;
  return config;
});
```

### 8. **Tailwind CSS** - Styling Framework
**What it is:** Utility-first CSS framework (like Bootstrap, but more flexible).

**Backend Analogy:**
- Instead of writing custom CSS, you use pre-built utility classes
- Like using a library of pre-written methods instead of writing everything from scratch

**Example:**
```tsx
// Instead of writing CSS:
<div className="bg-blue-500 text-white p-4 rounded-lg">
  Button
</div>

// This is equivalent to:
// background-color: blue;
// color: white;
// padding: 1rem;
// border-radius: 0.5rem;
```

---

## 🏗️ How the Application Works (Request Flow)

### 1. **User Opens Browser**
```
Browser → Loads index.html → Downloads JavaScript bundle → React app starts
```

### 2. **User Navigates to `/login`**
```
React Router → Matches route → Shows LoginPage component
```

### 3. **User Enters Credentials and Clicks Submit**
```
LoginPage component
  ↓
handleSubmit() function (like a controller method)
  ↓
useAuth().login() hook (like calling a service)
  ↓
authService.login() (like your service layer)
  ↓
apiClient.post('/auth/login') (like RestTemplate.postForObject)
  ↓
Axios interceptor adds token (if exists)
  ↓
HTTP Request → Your Backend API
  ↓
Response comes back
  ↓
Token saved to localStorage (like saving to session)
  ↓
User data saved to React Query cache
  ↓
Navigate to '/' (redirect)
```

### 4. **User Sees Main Page**
```
React Router → ProtectedRoute checks authentication
  ↓
If authenticated → Shows MainPage
  ↓
MainPage component renders
  ↓
useNotes() hook fetches notes from API
  ↓
React Query checks cache → If stale, fetches from API
  ↓
Notes displayed in UI
```

---

## 📁 Package Breakdown (What Each Package Does)

### `apps/web` - The Application
**Like:** Your main Spring Boot application

**Contains:**
- `src/main.tsx` - Entry point (like `main()` method)
- `src/App.tsx` - Root component (like `@SpringBootApplication`)
- `src/pages/` - Page components (like controllers)
- `src/routes/` - Route configuration (like `@RequestMapping`)
- `src/components/` - Reusable UI components (like utility classes)

### `packages/api` - API Client
**Like:** Your HTTP client library or service layer

**Contains:**
- `client/axios-client.ts` - HTTP client setup (like RestTemplate configuration)
- `services/*.service.ts` - API service methods (like your service layer)
- `hooks/*.ts` - React hooks that use services (like wrapper methods)

**Example Flow:**
```typescript
// Component uses hook
const { notes } = useNotes();

// Hook uses service
export const useNotes = () => {
  return useQuery({
    queryFn: notesService.getAll  // Service makes HTTP call
  });
};

// Service uses HTTP client
export const notesService = {
  getAll: () => apiClient.get('/notes')
};
```

### `packages/core` - Business Logic & State
**Like:** Your service layer and application state

**Contains:**
- `stores/` - Zustand stores (like singleton services)
  - `authStore.ts` - Authentication state
  - `uiStore.ts` - UI preferences (theme, sidebar, etc.)
- `hooks/` - Custom React hooks (like utility methods)
- `features/` - Feature modules (like feature packages)

### `packages/types` - Type Definitions
**Like:** Your DTOs, entities, and domain models

**Contains:**
- `domain/` - Domain models (Note, User, Directory, etc.)
- `api/` - API request/response types
- `common/` - Shared types

**Example:**
```typescript
// Like a Java class or interface
export interface Note {
  id: string;
  title: string;
  content: string;
  createdAt: string;
}
```

---

## 🔄 State Management (The "Database" of Frontend)

### Client State (Zustand) - Like Session/Application State
**Stores:**
- User authentication (user object, token)
- UI preferences (theme, sidebar collapsed)
- Temporary UI state

**Persistence:**
- Saved to `localStorage` (like a database, but in the browser)
- Survives page refreshes

### Server State (React Query) - Like Cache for API Responses
**Stores:**
- Data fetched from your backend API
- Notes, directories, tags, etc.

**Features:**
- Automatic caching (like Redis)
- Automatic refetching when stale
- Shared across all components
- Optimistic updates

**Example:**
```typescript
// Component A fetches notes
const { data: notes } = useNotes();

// Component B also needs notes
const { data: notes } = useNotes();

// React Query: "I already have this data cached, I'll return it immediately"
// No duplicate API call!
```

---

## 🎨 How UI Works (Component Rendering)

### Components = Reusable UI Pieces
**Backend Analogy:** Like methods that return HTML

**Example:**
```tsx
// Like a method that returns HTML
function Button({ text, onClick }) {
  return (
    <button onClick={onClick}>
      {text}
    </button>
  );
}

// Usage (like calling a method)
<Button text="Click me" onClick={handleClick} />
```

### JSX = HTML in JavaScript
**What it is:** HTML syntax inside JavaScript/TypeScript

```tsx
// This looks like HTML, but it's actually JavaScript
<div className="container">
  <h1>Hello</h1>
  <button onClick={handleClick}>Click</button>
</div>

// Gets compiled to JavaScript:
React.createElement('div', { className: 'container' },
  React.createElement('h1', null, 'Hello'),
  React.createElement('button', { onClick: handleClick }, 'Click')
);
```

### Props = Parameters
**Backend Analogy:** Like method parameters

```tsx
// Component definition (like method signature)
function UserCard({ user, onEdit }) {
  return <div>{user.name}</div>;
}

// Usage (like calling a method)
<UserCard user={currentUser} onEdit={handleEdit} />
```

### State = Instance Variables
**Backend Analogy:** Like private fields in a class

```tsx
function LoginForm() {
  const [email, setEmail] = useState('');  // Like: private String email = "";
  
  // When user types, update state
  const handleChange = (e) => {
    setEmail(e.target.value);  // Like: this.email = newValue;
  };
  
  return <input value={email} onChange={handleChange} />;
}
```

---

## 🔐 Authentication Flow (Like Spring Security)

### 1. **Login**
```
User submits form
  ↓
authService.login() makes POST /auth/login
  ↓
Backend returns token + user
  ↓
Token saved to localStorage (like session)
  ↓
User saved to React Query cache
  ↓
Redirect to main page
```

### 2. **Making Authenticated Requests**
```
Component needs to fetch data
  ↓
useNotes() hook called
  ↓
notesService.getAll() called
  ↓
apiClient.get('/notes') called
  ↓
Axios interceptor runs (like Spring interceptor)
  ↓
Reads token from localStorage
  ↓
Adds "Authorization: Bearer <token>" header
  ↓
Request sent to backend
```

### 3. **Protected Routes**
```
User navigates to '/'
  ↓
ProtectedRoute component checks authentication
  ↓
useAuth() hook checks if user exists
  ↓
If no user → Redirect to /login
  ↓
If user exists → Show MainPage
```

### 4. **Handling 401 Errors**
```
Backend returns 401 Unauthorized
  ↓
Axios response interceptor catches it
  ↓
Removes token from localStorage
  ↓
Clears React Query cache
  ↓
Redirects to /login
```

---

## 🚀 Build & Development Process

### Development Mode
```bash
pnpm dev:web
```

**What happens:**
1. Vite starts dev server on port 5173
2. TypeScript compiles on-the-fly
3. Browser connects to dev server
4. Changes trigger Hot Module Replacement (instant updates)
5. API calls proxied to `http://localhost:8080/api`

**Backend Analogy:**
- Like `mvn spring-boot:run` with hot reload
- Dev server = Spring Boot embedded server
- HMR = Automatic restart on code changes

### Production Build
```bash
pnpm build:web
```

**What happens:**
1. TypeScript compiles to JavaScript
2. All files bundled and minified
3. Output in `dist/` folder
4. Static files ready to serve (HTML, CSS, JS)

**Backend Analogy:**
- Like `mvn package` creating a JAR
- `dist/` folder = Your JAR file
- Can be served by any web server (Nginx, Apache, etc.)

---

## 📊 Data Flow Example: Loading Notes

```
1. User opens MainPage
   ↓
2. MainPage component renders
   ↓
3. Component calls: const { notes } = useNotes();
   ↓
4. useNotes() hook checks React Query cache
   ↓
5. Cache miss → Calls notesService.getAll()
   ↓
6. notesService.getAll() → apiClient.get('/notes')
   ↓
7. Axios interceptor adds auth token
   ↓
8. HTTP GET request → Your Backend: GET /api/v1/notes
   ↓
9. Backend returns JSON: [{ id: "1", title: "Note 1", ... }]
   ↓
10. React Query caches the response
   ↓
11. useNotes() returns { notes: [...], isLoading: false }
   ↓
12. MainPage component receives notes
   ↓
13. Component renders notes in UI
```

**If user navigates away and comes back:**
- Step 4: React Query finds data in cache
- Returns cached data immediately (no API call)
- Background refetch if data is stale

---

## 🎯 Key Concepts Summary

| Frontend Concept | Backend Analogy |
|-----------------|-----------------|
| **Component** | Method/Class that returns HTML |
| **Props** | Method parameters |
| **State** | Instance variables |
| **Hook** | Utility method/service call |
| **Store (Zustand)** | Singleton service / Application-scoped bean |
| **React Query** | Smart cache for API responses |
| **Service** | Your service layer |
| **Axios** | RestTemplate / WebClient |
| **Route** | @RequestMapping |
| **TypeScript** | Java (statically typed) |
| **Vite** | Maven/Gradle (build tool) |
| **localStorage** | Session storage / Database |

---

## 🔍 Real Code Example: Login Flow

Let's trace through the actual login code:

### 1. User sees LoginPage
```tsx
// apps/web/src/pages/LoginPage.tsx
export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('');  // Component state
  const { login } = useAuth();              // Get login function from hook
  
  const handleSubmit = async (e) => {
    await login({ email, password });        // Call login
    navigate('/');                           // Redirect on success
  };
  
  return <form onSubmit={handleSubmit}>...</form>;
};
```

### 2. useAuth hook handles the logic
```tsx
// packages/api/src/hooks/useAuth.ts
export const useAuth = () => {
  const loginMutation = useMutation({
    mutationFn: authService.login,          // Use service to make API call
    onSuccess: (data) => {
      localStorage.setItem('auth_token', data.accessToken);  // Save token
      queryClient.setQueryData(queryKeys.auth.me, data.user); // Cache user
    },
  });
  
  return {
    login: (credentials) => loginMutation.mutateAsync(credentials)
  };
};
```

### 3. authService makes HTTP call
```tsx
// packages/api/src/services/auth.service.ts
export const authService = {
  login: async (credentials) => {
    const response = await apiClient.post('/auth/login', credentials);
    return response.data;  // Returns { accessToken, user }
  }
};
```

### 4. apiClient sends request
```tsx
// packages/api/src/client/axios-client.ts
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;  // Add token if exists
  }
  return config;
});

// POST /api/v1/auth/login
// Headers: { "Content-Type": "application/json" }
// Body: { "email": "...", "password": "..." }
```

### 5. Backend responds
```
HTTP 200 OK
{
  "accessToken": "eyJhbGc...",
  "user": { "id": "123", "email": "user@example.com" }
}
```

### 6. Response flows back
```
authService.login() returns data
  ↓
useAuth hook's onSuccess runs
  ↓
Token saved to localStorage
  ↓
User saved to React Query cache
  ↓
LoginPage's handleSubmit completes
  ↓
navigate('/') redirects user
```

---

## 💡 Common Patterns

### 1. **Fetching Data Pattern**
```tsx
// Component
const { data, isLoading, error } = useNotes();

// Hook (packages/api/src/hooks/useNotes.ts)
export const useNotes = () => {
  return useQuery({
    queryKey: ['notes'],
    queryFn: notesService.getAll  // Makes API call
  });
};

// Service (packages/api/src/services/notes.service.ts)
export const notesService = {
  getAll: () => apiClient.get('/notes')
};
```

### 2. **Creating Data Pattern**
```tsx
// Component
const { createNote } = useNotes();
await createNote({ title: "New Note", content: "..." });

// Hook
const createMutation = useMutation({
  mutationFn: notesService.create,
  onSuccess: () => {
    queryClient.invalidateQueries({ queryKey: ['notes'] }); // Refetch list
  }
});
```

### 3. **Accessing Global State**
```tsx
// Any component can access
const { user, token } = useAuthStore();
const { theme, toggleTheme } = useUiStore();
```

---

## 🎓 Learning Path for Backend Developers

1. **Understand React basics**
   - Components, props, state
   - JSX syntax
   - Event handling

2. **Understand the data flow**
   - How components fetch data
   - How state is managed
   - How API calls work

3. **Understand the architecture**
   - Monorepo structure
   - Package organization
   - Separation of concerns

4. **Practice reading code**
   - Start with simple components
   - Trace data flow
   - Understand hooks

---

## 🚨 Common Confusions

### "Why not just use jQuery?"
- React manages the entire UI state
- Automatic re-rendering when data changes
- Component-based architecture
- Better for complex applications

### "Why TypeScript instead of JavaScript?"
- Type safety (like Java vs Python)
- Better IDE support
- Catch errors at compile time
- Better refactoring

### "Why React Query? Can't we just use fetch()?"
- Automatic caching
- No duplicate requests
- Loading/error states handled
- Optimistic updates
- Cache invalidation

### "Why Zustand? Can't we just use React state?"
- Global state shared across components
- Persistence to localStorage
- Better performance
- Simpler than Context API

---

## 📚 Next Steps

1. **Read the code** - Start with `LoginPage.tsx` and trace through
2. **Understand hooks** - Read `useAuth.ts` and `useNotes.ts`
3. **See the services** - Check `auth.service.ts` and `notes.service.ts`
4. **Explore stores** - Look at `authStore.ts` and `uiStore.ts`
5. **Try making changes** - Add a console.log, see what happens

---

## 🎯 Summary

**The frontend is:**
- A client application running in the browser
- Built with React (UI framework)
- TypeScript (type-safe JavaScript)
- Organized in a monorepo (like microservices)
- Uses React Query for API data (like a smart cache)
- Uses Zustand for client state (like session storage)
- Uses Axios for HTTP requests (like RestTemplate)
- Uses Vite for building (like Maven)

**The flow:**
1. User interacts with UI
2. Component calls hook
3. Hook calls service
4. Service makes HTTP request via Axios
5. Backend responds
6. Data flows back through the chain
7. UI updates automatically

**It's like your backend, but:**
- Runs in the browser instead of on a server
- Returns HTML/UI instead of JSON
- Manages UI state instead of database state
- Makes HTTP requests instead of receiving them
