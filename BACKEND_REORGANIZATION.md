# 🔄 Backend Reorganization Guide

**Purpose**: Move Maven modules into `backend/` directory for cleaner separation from frontend.

---

## 📋 Current Structure (Mixed)

```
vertex/
├── application/          # Backend module
├── bundle/               # Backend module
├── domain/               # Backend module
├── infrastructure/       # Backend module
├── web/                  # Backend module
├── integration-test/     # Backend module
├── pom.xml               # Backend POM
├── mvnw, mvnw.cmd       # Backend Maven wrapper
├── apps/                 # Frontend apps ❌ Mixed!
├── packages/             # Frontend packages ❌ Mixed!
└── package.json          # Frontend root ❌ Mixed!
```

**Problem**: Backend and frontend files are mixed at root level.

---

## ✅ Proposed Structure (Clean)

```
vertex/
├── backend/              # 🔧 All Spring Boot code
│   ├── application/
│   ├── bundle/
│   ├── domain/
│   ├── infrastructure/
│   ├── web/
│   ├── integration-test/
│   ├── pom.xml
│   ├── mvnw, mvnw.cmd
│   ├── .mvn/
│   ├── docker-compose.yml
│   └── README.md
│
├── apps/                 # 🎨 Frontend applications
│   └── web/
│
├── packages/             # 📦 Frontend shared code
│   ├── types/
│   └── config/
│
├── docs/                 # 📚 Project documentation
├── scripts/              # 🛠️ Utility scripts
├── mockups/              # 🎨 UI mockups
├── package.json          # Frontend workspace root
├── pnpm-workspace.yaml
└── README.md             # Project root README
```

---

## 🚀 Migration Steps

### **Option A: Automated Script**

```bash
# Run the reorganization script
./reorganize-backend.sh
```

This will:
1. ✅ Create `backend/` directory
2. ✅ Move all Maven modules
3. ✅ Move Maven wrapper files
4. ✅ Move Docker Compose files
5. ✅ Update .gitignore
6. ✅ Create navigation README

### **Option B: Manual Steps**

If you prefer to do it manually:

```bash
# 1. Create backend directory
mkdir backend

# 2. Move Maven modules
mv application bundle domain infrastructure web integration-test backend/

# 3. Move Maven files
mv pom.xml mvnw mvnw.cmd .mvn backend/

# 4. Move Docker files
mv docker-compose.yml docker-compose.test.yml backend/

# 5. Update .gitignore (add backend/target/)
```

---

## 🔧 Post-Migration Tasks

### **1. Update IntelliJ IDEA**

#### Reimport Maven Project:
1. File → Close Project
2. Open Project → Select `vertex/backend/pom.xml`
3. Or: Right-click `backend/pom.xml` → Add as Maven Project

#### Update Module Paths:
1. File → Project Structure
2. Modules → Verify paths point to `backend/...`
3. Sources → Update source roots if needed

### **2. Update Run Configurations**

For Spring Boot run configuration:
1. Run → Edit Configurations
2. Spring Boot → Your config
3. Update Working Directory: `$PROJECT_DIR$/backend`
4. Update Module: Select `bundle` from dropdown

### **3. Update Scripts**

If you have any scripts referencing modules:

**Before**:
```bash
./mvnw spring-boot:run -pl bundle
```

**After**:
```bash
cd backend && ./mvnw spring-boot:run -pl bundle
```

### **4. Update Docker Compose**

If you have docker-compose files:

**Before**:
```yaml
volumes:
  - ./application:/app/application
```

**After**:
```yaml
volumes:
  - ./backend/application:/app/application
```

### **5. Update CI/CD**

If you have GitHub Actions or other CI:

**Before**:
```yaml
- name: Build backend
  run: ./mvnw clean install
```

**After**:
```yaml
- name: Build backend
  working-directory: ./backend
  run: ./mvnw clean install
```

---

## 📝 Updated Development Workflow

### **Backend Development**

```bash
# Navigate to backend
cd backend

# Build
./mvnw clean install

# Run
./mvnw spring-boot:run -pl bundle

# Test
./mvnw test

# Package
./mvnw clean package
```

### **Frontend Development**

```bash
# From root directory
pnpm install
pnpm dev:web
```

### **Full Stack Development**

```bash
# Terminal 1: Backend
cd backend && ./mvnw spring-boot:run -pl bundle

# Terminal 2: Frontend
pnpm dev:web
```

---

## 🔄 Git Workflow

### **Before Committing**

The reorganization will show as file renames in Git:

```bash
git status
# Shows: renamed: application/ -> backend/application/
```

Git automatically detects renames, so commit normally:

```bash
git add .
git commit -m "Reorganize backend modules into backend/ directory

Move all Spring Boot Maven modules into dedicated backend/ directory
for cleaner separation from frontend monorepo structure.

Changes:
- Move Maven modules: application, bundle, domain, infrastructure, web, integration-test
- Move Maven wrapper files and .mvn directory
- Move backend-related Docker Compose files
- Update .gitignore with backend-specific patterns"
```

---

## 📊 Before vs After Comparison

### **Commands**

| Task | Before | After |
|------|--------|-------|
| **Build Backend** | `./mvnw clean install` | `cd backend && ./mvnw clean install` |
| **Run Backend** | `./mvnw spring-boot:run -pl bundle` | `cd backend && ./mvnw spring-boot:run -pl bundle` |
| **Run Frontend** | `pnpm dev:web` | `pnpm dev:web` (unchanged) |
| **Backend Tests** | `./mvnw test` | `cd backend && ./mvnw test` |

### **Project Structure**

| Aspect | Before | After |
|--------|--------|-------|
| **Backend Location** | Root level (mixed) | `backend/` (isolated) |
| **Frontend Location** | `apps/`, `packages/` | `apps/`, `packages/` (unchanged) |
| **Root Cleanliness** | Mixed files | Clean separation |
| **Navigation** | Confusing | Clear |

---

## ⚠️ Important Notes

### **Advantages**

✅ **Clear Separation**: Backend vs Frontend
✅ **Easier Navigation**: Know where everything is
✅ **Better IDE Support**: Can open backend as separate project
✅ **Cleaner Root**: Less clutter at root level
✅ **Independent Deployment**: Easier to deploy separately
✅ **Team Scalability**: Backend and frontend teams have clear boundaries

### **Considerations**

⚠️ **Need to update paths** in:
- IntelliJ IDEA configurations
- CI/CD workflows
- Docker Compose files
- Custom scripts
- Documentation

⚠️ **One-time setup**: Takes ~10 minutes to update all tooling

⚠️ **Git history**: File history preserved via git's rename detection

---

## 🎯 Recommendation

**Do the reorganization now** because:

1. ✅ Project is still young (easy to change)
2. ✅ Matches industry standard (monorepo best practices)
3. ✅ Prevents future confusion
4. ✅ Clean structure attracts contributors
5. ✅ Easier to onboard new developers

**Alternative**: Keep current structure if:
- Already have extensive CI/CD that would break
- Team is resistant to change
- Very close to production deadline

---

## 🚀 Quick Start After Reorganization

### **First Time Setup**

```bash
# 1. Backend
cd backend
./mvnw clean install
./mvnw spring-boot:run -pl bundle

# 2. Frontend (separate terminal)
cd ..
pnpm install
pnpm dev:web
```

### **Daily Development**

Create convenience scripts at root:

**`start-backend.sh`**:
```bash
#!/bin/bash
cd backend && ./mvnw spring-boot:run -pl bundle
```

**`start-frontend.sh`**:
```bash
#!/bin/bash
pnpm dev:web
```

**`start-all.sh`**:
```bash
#!/bin/bash
trap 'kill 0' EXIT

cd backend && ./mvnw spring-boot:run -pl bundle &
cd .. && pnpm dev:web &

wait
```

---

## 📚 References

- **Frontend README**: `FRONTEND_README.md`
- **Backend README**: `backend/README.md` (after migration)
- **Quick Start**: `QUICK_START.md`

---

## ✅ Final Structure Overview

After reorganization:

```
vertex/                    # Monorepo root
├── backend/              # 🔧 Spring Boot (Java)
├── apps/                 # 🎨 Frontend apps (React)
├── packages/             # 📦 Shared frontend code
├── docs/                 # 📚 Documentation
├── scripts/              # 🛠️ Utility scripts
├── mockups/              # 🎨 UI designs
├── package.json          # Frontend workspace
└── README.md             # Project overview
```

Clean, organized, professional! 🎉

---

**Ready to reorganize?** Run: `./reorganize-backend.sh`
