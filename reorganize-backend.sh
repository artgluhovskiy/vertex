#!/bin/bash

# Script to reorganize backend Maven modules into backend/ directory

set -e  # Exit on error

echo "🔄 Reorganizing backend structure..."
echo ""

# Create backend directory
echo "📁 Creating backend directory..."
mkdir -p backend

# Move Maven modules
echo "📦 Moving Maven modules..."
mv application backend/
mv bundle backend/
mv domain backend/
mv infrastructure backend/
mv web backend/
mv integration-test backend/

# Move Maven wrapper and config
echo "⚙️  Moving Maven files..."
mv mvnw backend/
mv mvnw.cmd backend/
mv .mvn backend/
mv pom.xml backend/

# Move docker-compose files (backend-related)
echo "🐳 Moving Docker files..."
mv docker-compose.yml backend/ 2>/dev/null || true
mv docker-compose.test.yml backend/ 2>/dev/null || true

# Update .gitignore
echo "📝 Updating .gitignore..."
cat >> .gitignore << 'EOF'

# Backend
backend/target/
backend/**/target/
EOF

# Create new root README pointing to backend and frontend
echo "📄 Creating navigation README..."
cat > README_ROOT.md << 'EOF'
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
EOF

echo ""
echo "✅ Backend reorganization complete!"
echo ""
echo "📋 Next steps:"
echo "   1. Update IntelliJ IDEA project structure (File → Project Structure)"
echo "   2. Re-import Maven project from backend/pom.xml"
echo "   3. Update run configurations to use backend/ path"
echo "   4. Update CI/CD scripts if any"
echo ""
echo "🚀 New structure:"
echo "   Backend:  ./backend/"
echo "   Frontend: ./apps/web/"
echo ""
