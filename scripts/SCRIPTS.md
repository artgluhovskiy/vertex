# Test Environment Scripts

This directory contains scripts to manage the local test environment for Vertex integration tests.

## Overview

The test environment uses Docker containers to provide dependencies for integration tests. The `TestContainerManager` automatically detects if external containers are running on the expected ports and uses them instead of starting new TestContainers.

## Scripts

### `start-test-env.sh`

Starts the test environment with all required containers.

**Usage:**
```bash
./scripts/start-test-env.sh
```

**What it does:**
- Checks if Docker is running
- Creates/starts PostgreSQL container (`vertex-postgres-test`)
- Waits for PostgreSQL to be ready
- Displays connection details

**Container Configuration:**
- **Name:** `vertex-postgres-test`
- **Image:** `pgvector/pgvector:pg16`
- **Port:** `5432`
- **Database:** `vertex`
- **User:** `vertex`
- **Password:** `vertex`

### `stop-test-env.sh`

Stops the test environment containers.

**Usage:**
```bash
./scripts/stop-test-env.sh
```

**What it does:**
- Stops the PostgreSQL container
- Does NOT remove the container (data is preserved)

## Integration with Tests

When you run integration tests, the `TestContainerManager` will:

1. **Check for external containers** on `localhost:5432`
2. **If found:** Use the external container (faster, no Docker overhead)
3. **If not found:** Start a TestContainer automatically

This means you can:
- ✅ Run tests **without** starting containers (TestContainers starts them)
- ✅ Run tests **with** pre-started containers (faster test execution)
- ✅ Use the same container across multiple test runs (data persists between runs until you stop/remove it)

## Workflow Examples

### Run tests with external container (faster):
```bash
./scripts/start-test-env.sh
./mvnw test -pl integration-test
```

### Run tests with TestContainers (no pre-setup needed):
```bash
./scripts/stop-test-env.sh  # Ensure external container is stopped
./mvnw test -pl integration-test
```

### Stop and clean up:
```bash
./scripts/stop-test-env.sh
docker rm vertex-postgres-test  # Remove container and data
```

## Useful Docker Commands

```bash
# Check container status
docker ps -a | grep vertex-postgres-test

# View container logs
docker logs vertex-postgres-test

# Connect to PostgreSQL
docker exec -it vertex-postgres-test psql -U vertex -d vertex

# Restart container
docker restart vertex-postgres-test

# Remove container (deletes data)
docker rm -f vertex-postgres-test
```

## Adding New Containers

To add support for new containers (e.g., Redis, Elasticsearch):

1. Update `start-test-env.sh` to start the new container
2. Update `stop-test-env.sh` to stop the new container
3. Update `TestContainerManager` to detect and use the external container
4. Document the new container configuration in this README

## Troubleshooting

### Container won't start
```bash
# Check if port is already in use
lsof -i :5432

# Check Docker logs
docker logs vertex-postgres-test

# Force remove and recreate
docker rm -f vertex-postgres-test
./scripts/start-test-env.sh
```

### Tests don't detect external container
```bash
# Verify container is running and healthy
docker ps --filter "name=vertex-postgres-test"
docker exec vertex-postgres-test pg_isready -U vertex

# Check if port is accessible
nc -zv localhost 5432
```

### Data persistence
The container stores data in a Docker volume. To completely reset:
```bash
docker rm -f vertex-postgres-test
docker volume prune  # Remove all unused volumes
./scripts/start-test-env.sh
```
