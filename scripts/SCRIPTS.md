# Test Environment Scripts

This directory contains scripts to manage the local test environment for Vertex integration tests using Docker Compose.

## Overview

The test environment uses Docker Compose to orchestrate containers required for integration tests. The `TestContainerManager` automatically detects if external containers are running on the expected ports and uses them instead of starting new TestContainers.

## Files

### `docker-compose.test.yml`

Docker Compose configuration for the test environment.

**Services:**
- **postgres**: PostgreSQL with pgvector extension
  - Container: `vertex-postgres-test`
  - Image: `pgvector/pgvector:pg16`
  - Port: `5432`
  - Database: `vertex`
  - User: `vertex`
  - Password: `vertex`
  - Volume: `vertex-test-data` (persistent storage)
  - Health check: enabled

### `start-test-env.sh`

Starts the test environment using Docker Compose.

**Usage:**
```bash
./scripts/start-test-env.sh
```

**What it does:**
- Checks if Docker is running
- Starts all services defined in `docker-compose.test.yml`
- Waits for services to be healthy
- Displays useful commands

### `stop-test-env.sh`

Stops the test environment.

**Usage:**
```bash
./scripts/stop-test-env.sh
```

**What it does:**
- Stops all services
- Preserves containers and data
- Shows restart/cleanup commands

## Integration with Tests

When you run integration tests, the `TestContainerManager` will:

1. **Check for external containers** on `localhost:5432`
2. **If found:** Use the external container (faster, no Docker overhead)
3. **If not found:** Start a TestContainer automatically

This means you can:
- ✅ Run tests **without** starting containers (TestContainers starts them)
- ✅ Run tests **with** pre-started containers (faster test execution)
- ✅ Use the same containers across multiple test runs (data persists)

## Workflow Examples

### Run tests with external containers (faster):
```bash
./scripts/start-test-env.sh
./mvnw test -pl integration-test
```

### Run tests with TestContainers (no pre-setup needed):
```bash
./scripts/stop-test-env.sh  # Ensure external containers are stopped
./mvnw test -pl integration-test
```

### Stop environment:
```bash
./scripts/stop-test-env.sh
```

### Clean up completely:
```bash
docker compose -f docker-compose.test.yml down -v  # Removes containers and volumes
```

## Docker Compose Commands

All commands should be run from the project root directory:

```bash
# Start services
docker compose -f docker-compose.test.yml up -d

# Stop services (preserve data)
docker compose -f docker-compose.test.yml stop

# Start stopped services
docker compose -f docker-compose.test.yml start

# Restart services
docker compose -f docker-compose.test.yml restart

# View status
docker compose -f docker-compose.test.yml ps

# View logs
docker compose -f docker-compose.test.yml logs -f

# Stop and remove containers (preserve volumes)
docker compose -f docker-compose.test.yml down

# Stop and remove containers and volumes (full cleanup)
docker compose -f docker-compose.test.yml down -v
```

## Useful Docker Commands

```bash
# Connect to PostgreSQL
docker exec -it vertex-postgres-test psql -U vertex -d vertex

# View PostgreSQL logs
docker logs vertex-postgres-test

# Check container health
docker inspect vertex-postgres-test --format='{{.State.Health.Status}}'

# List volumes
docker volume ls | grep vertex

# Remove specific volume
docker volume rm vertex-test-data
```

## Adding New Services

To add support for new services (e.g., Redis, Elasticsearch):

1. Add service definition to `docker-compose.test.yml`:
```yaml
services:
  redis:
    container_name: vertex-redis-test
    image: redis:7-alpine
    ports:
      - "6379:6379"
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
```

2. Update `TestContainerManager` to detect and use the external service
3. Document the new service configuration in this file

## Troubleshooting

### Services won't start
```bash
# Check what's using the ports
lsof -i :5432

# View detailed logs
docker compose -f docker-compose.test.yml logs

# Force recreate
docker compose -f docker-compose.test.yml down
docker compose -f docker-compose.test.yml up -d --force-recreate
```

### Tests don't detect external containers
```bash
# Verify services are running
docker compose -f docker-compose.test.yml ps

# Check service health
docker compose -f docker-compose.test.yml ps --format json | jq '.[].Health'

# Test connectivity
nc -zv localhost 5432
```

### Data persistence issues
```bash
# List volumes
docker volume ls | grep vertex

# Inspect volume
docker volume inspect vertex-test-data

# Complete reset (WARNING: deletes all data)
docker compose -f docker-compose.test.yml down -v
./scripts/start-test-env.sh
```

### jq not found error
The start script uses `jq` to parse JSON output. If you get a "command not found" error:

```bash
# macOS
brew install jq

# Ubuntu/Debian
sudo apt-get install jq

# Or remove the jq check and just wait for PostgreSQL to be ready
# The script will still work, just with less accurate health checking
```

## Benefits of Docker Compose

✅ **Declarative configuration** - All services defined in one file
✅ **Easy to extend** - Add new services with just a few lines
✅ **Network isolation** - Services automatically get a shared network
✅ **Volume management** - Data persists across restarts
✅ **Health checks** - Built-in service health monitoring
✅ **Simple commands** - `up`, `down`, `restart` - that's it
✅ **Version control** - Configuration is part of the codebase
