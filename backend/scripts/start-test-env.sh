#!/usr/bin/env bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

COMPOSE_FILE="docker-compose.test.yml"

echo -e "${BLUE}🚀 Starting Vertex Test Environment${NC}"
echo ""

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running. Please start Docker and try again.${NC}"
    exit 1
fi

# Check if docker-compose file exists
if [ ! -f "${COMPOSE_FILE}" ]; then
    echo -e "${RED}❌ ${COMPOSE_FILE} not found${NC}"
    exit 1
fi

echo -e "${BLUE}📦 Starting services with Docker Compose...${NC}"
docker compose -f "${COMPOSE_FILE}" up -d

echo ""
echo -e "${BLUE}⏳ Waiting for services to be healthy...${NC}"

# Wait for services to be healthy
MAX_ATTEMPTS=30
ATTEMPT=0
while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    HEALTH_STATUS=$(docker compose -f "${COMPOSE_FILE}" ps --format json | jq -r '.Health' 2>/dev/null || echo "starting")

    if [ "$HEALTH_STATUS" = "healthy" ]; then
        echo -e "${GREEN}✅ All services are healthy!${NC}"
        break
    fi

    ATTEMPT=$((ATTEMPT + 1))
    if [ $ATTEMPT -eq $MAX_ATTEMPTS ]; then
        echo -e "${RED}❌ Services failed to become healthy within expected time${NC}"
        echo -e "${YELLOW}Run 'docker compose -f ${COMPOSE_FILE} logs' to see details${NC}"
        exit 1
    fi

    echo -e "${YELLOW}   Waiting... (attempt ${ATTEMPT}/${MAX_ATTEMPTS})${NC}"
    sleep 2
done

echo ""
echo -e "${GREEN}🎉 Test environment is ready!${NC}"
echo ""
echo -e "${BLUE}📝 Useful Commands:${NC}"
echo -e "  Status:  ${YELLOW}docker compose -f ${COMPOSE_FILE} ps${NC}"
echo -e "  Stop:    ${YELLOW}docker compose -f ${COMPOSE_FILE} stop${NC}"
echo -e "  Start:   ${YELLOW}docker compose -f ${COMPOSE_FILE} start${NC}"
echo -e "  Down:    ${YELLOW}docker compose -f ${COMPOSE_FILE} down${NC}"
echo -e "  Logs:    ${YELLOW}docker compose -f ${COMPOSE_FILE} logs -f${NC}"
echo -e "  Connect: ${YELLOW}docker exec -it vertex-postgres-test psql -U vertex -d vertex${NC}"
echo ""
echo -e "${GREEN}✨ You can now run integration tests with: ${YELLOW}./mvnw test -pl integration-test${NC}"
