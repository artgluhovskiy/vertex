# Project Structure

This is a full-stack monorepo with separate backend and frontend.

## Backend (Spring Boot)

Location: `./backend/`

```bash
cd backend
./mvnw spring-boot:run -pl bundle
```

See [Backend README](./backend/README.md) for details.

## Frontend (React)

Location: `./apps/web/`

```bash
pnpm dev:web
```

See [Frontend README](./FRONTEND_README.md) for details.

## Quick Start

1. **Backend**:
   ```bash
   cd backend && ./mvnw spring-boot:run -pl bundle
   ```

2. **Frontend**:
   ```bash
   pnpm install
   pnpm dev:web
   ```

3. Open http://localhost:5173
