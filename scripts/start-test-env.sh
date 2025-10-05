#!/usr/bin/env bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
POSTGRES_CONTAINER_NAME="vertex-postgres-test"
POSTGRES_IMAGE="pgvector/pgvector:pg16"
POSTGRES_PORT=5432
POSTGRES_DB="vertex"
POSTGRES_USER="vertex"
POSTGRES_PASSWORD="vertex"

echo -e "${BLUE}🚀 Starting Vertex Test Environment${NC}"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running. Please start Docker and try again.${NC}"
    exit 1
fi

# Check if container already exists
if docker ps -a --format '{{.Names}}' | grep -q "^${POSTGRES_CONTAINER_NAME}$"; then
    echo -e "${YELLOW}📦 Container '${POSTGRES_CONTAINER_NAME}' already exists${NC}"

    # Check if it's running
    if docker ps --format '{{.Names}}' | grep -q "^${POSTGRES_CONTAINER_NAME}$"; then
        echo -e "${GREEN}✅ Container is already running${NC}"
    else
        echo -e "${YELLOW}🔄 Starting existing container...${NC}"
        docker start "${POSTGRES_CONTAINER_NAME}"
        echo -e "${GREEN}✅ Container started${NC}"
    fi
else
    echo -e "${BLUE}📦 Creating and starting PostgreSQL container...${NC}"
    docker run -d \
        --name "${POSTGRES_CONTAINER_NAME}" \
        -e POSTGRES_DB="${POSTGRES_DB}" \
        -e POSTGRES_USER="${POSTGRES_USER}" \
        -e POSTGRES_PASSWORD="${POSTGRES_PASSWORD}" \
        -p ${POSTGRES_PORT}:5432 \
        --health-cmd="pg_isready -U ${POSTGRES_USER}" \
        --health-interval=10s \
        --health-timeout=5s \
        --health-retries=5 \
        "${POSTGRES_IMAGE}"

    echo -e "${GREEN}✅ Container created and started${NC}"
fi

echo ""
echo -e "${BLUE}⏳ Waiting for PostgreSQL to be ready...${NC}"

# Wait for PostgreSQL to be healthy
MAX_ATTEMPTS=30
ATTEMPT=0
while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if docker exec "${POSTGRES_CONTAINER_NAME}" pg_isready -U "${POSTGRES_USER}" > /dev/null 2>&1; then
        echo -e "${GREEN}✅ PostgreSQL is ready!${NC}"
        break
    fi

    ATTEMPT=$((ATTEMPT + 1))
    if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
        echo -e "${RED}❌ PostgreSQL failed to start within expected time${NC}"
        exit 1
    fi

    echo -e "${YELLOW}   Waiting... (attempt ${ATTEMPT}/${MAX_ATTEMPTS})${NC}"
    sleep 2
done

echo ""
echo -e "${GREEN}🎉 Test environment is ready!${NC}"
echo ""
echo -e "${BLUE}Container Details:${NC}"
echo -e "  Name:     ${POSTGRES_CONTAINER_NAME}"
echo -e "  Image:    ${POSTGRES_IMAGE}"
echo -e "  Port:     ${POSTGRES_PORT}"
echo -e "  Database: ${POSTGRES_DB}"
echo -e "  User:     ${POSTGRES_USER}"
echo -e "  Password: ${POSTGRES_PASSWORD}"
echo ""
echo -e "${BLUE}📝 Useful Commands:${NC}"
echo -e "  Stop:    ${YELLOW}docker stop ${POSTGRES_CONTAINER_NAME}${NC}"
echo -e "  Start:   ${YELLOW}docker start ${POSTGRES_CONTAINER_NAME}${NC}"
echo -e "  Remove:  ${YELLOW}docker rm -f ${POSTGRES_CONTAINER_NAME}${NC}"
echo -e "  Connect: ${YELLOW}docker exec -it ${POSTGRES_CONTAINER_NAME} psql -U ${POSTGRES_USER} -d ${POSTGRES_DB}${NC}"
echo -e "  Logs:    ${YELLOW}docker logs ${POSTGRES_CONTAINER_NAME}${NC}"
echo ""
echo -e "${GREEN}✨ You can now run integration tests with: ${YELLOW}./mvnw test -pl integration-test${NC}"
