#!/usr/bin/env bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

COMPOSE_FILE="docker-compose.test.yml"

echo -e "${BLUE}🛑 Stopping Vertex Test Environment${NC}"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running.${NC}"
    exit 1
fi

# Check if docker-compose file exists
if [ ! -f "${COMPOSE_FILE}" ]; then
    echo -e "${RED}❌ ${COMPOSE_FILE} not found${NC}"
    exit 1
fi

echo -e "${YELLOW}🔄 Stopping services...${NC}"
docker compose -f "${COMPOSE_FILE}" stop

echo ""
echo -e "${GREEN}✨ Test environment stopped${NC}"
echo ""
echo -e "${BLUE}📝 Useful Commands:${NC}"
echo -e "  Start:   ${YELLOW}./scripts/start-test-env.sh${NC}"
echo -e "  Restart: ${YELLOW}docker compose -f ${COMPOSE_FILE} restart${NC}"
echo -e "  Down:    ${YELLOW}docker compose -f ${COMPOSE_FILE} down${NC} (stops and removes containers)"
echo -e "  Clean:   ${YELLOW}docker compose -f ${COMPOSE_FILE} down -v${NC} (removes containers and volumes)"
