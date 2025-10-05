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

echo -e "${BLUE}🛑 Stopping Vertex Test Environment${NC}"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running.${NC}"
    exit 1
fi

# Check if container exists
if ! docker ps -a --format '{{.Names}}' | grep -q "^${POSTGRES_CONTAINER_NAME}$"; then
    echo -e "${YELLOW}⚠️  Container '${POSTGRES_CONTAINER_NAME}' does not exist${NC}"
    exit 0
fi

# Check if container is running
if docker ps --format '{{.Names}}' | grep -q "^${POSTGRES_CONTAINER_NAME}$"; then
    echo -e "${YELLOW}🔄 Stopping PostgreSQL container...${NC}"
    docker stop "${POSTGRES_CONTAINER_NAME}"
    echo -e "${GREEN}✅ Container stopped${NC}"
else
    echo -e "${YELLOW}ℹ️  Container is already stopped${NC}"
fi

echo ""
echo -e "${GREEN}✨ Test environment stopped${NC}"
echo ""
echo -e "${BLUE}📝 Useful Commands:${NC}"
echo -e "  Start:   ${YELLOW}./scripts/start-test-env.sh${NC}"
echo -e "  Remove:  ${YELLOW}docker rm ${POSTGRES_CONTAINER_NAME}${NC}"
echo -e "  Restart: ${YELLOW}docker restart ${POSTGRES_CONTAINER_NAME}${NC}"
