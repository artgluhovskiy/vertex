#!/bin/bash

# Run Vertex Backend in Preprod Mode
# This script starts the Spring Boot application connected to the preprod environment

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
cd "$PROJECT_ROOT"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

print_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_header() {
    echo -e "${CYAN}╔══════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║  Vertex Backend - Preprod Mode          ║${NC}"
    echo -e "${CYAN}╚══════════════════════════════════════════╝${NC}"
    echo ""
}

# Check if preprod environment is running, start if needed
ensure_preprod_env() {
    print_info "Checking preprod environment..."

    local postgres_running=false
    local ollama_running=false

    # Check if containers are running
    if docker ps --format '{{.Names}}' | grep -q "vertex-postgres-preprod"; then
        postgres_running=true
    fi

    if docker ps --format '{{.Names}}' | grep -q "vertex-ollama-preprod"; then
        ollama_running=true
    fi

    # If both are running, verify they're healthy
    if [ "$postgres_running" = true ] && [ "$ollama_running" = true ]; then
        print_success "Preprod containers are running"

        # Test PostgreSQL connection
        print_info "Verifying PostgreSQL connection..."
        if docker exec vertex-postgres-preprod pg_isready -U vertex_preprod -d vertex_preprod > /dev/null 2>&1; then
            print_success "PostgreSQL is ready"
        else
            print_warning "PostgreSQL is not ready, restarting preprod..."
            "$SCRIPT_DIR/env.sh" preprod restart
            return
        fi

        # Test Ollama connection
        print_info "Verifying Ollama connection..."
        if curl -f -s http://localhost:11435/api/tags > /dev/null 2>&1; then
            print_success "Ollama is ready"
        else
            print_warning "Ollama is not ready, restarting preprod..."
            "$SCRIPT_DIR/env.sh" preprod restart
            return
        fi

        print_success "Preprod environment is healthy"
        return
    fi

    # If not running, start preprod environment
    print_warning "Preprod environment is not running"
    print_info "Starting preprod environment..."
    echo ""

    "$SCRIPT_DIR/env.sh" preprod start

    if [ $? -ne 0 ]; then
        print_error "Failed to start preprod environment"
        exit 1
    fi

    echo ""
    print_success "Preprod environment started successfully"
}

# Load environment variables from .env.preprod
load_env_vars() {
    local env_file="$PROJECT_ROOT/.env.preprod"

    if [ -f "$env_file" ]; then
        print_info "Loading preprod configuration from .env.preprod"
        export $(grep -v '^#' "$env_file" | xargs)
    else
        print_warning ".env.preprod not found, using default password"
    fi
}

# Main execution
main() {
    print_header

    ensure_preprod_env
    load_env_vars

    echo ""
    print_info "Starting Vertex Backend..."
    print_info "Profile: preprod"
    print_info "Database: jdbc:postgresql://localhost:5433/vertex_preprod"
    print_info "Ollama: http://localhost:11435"
    print_info "Server: http://localhost:8080"
    echo ""

    # Check if we should compile first
    if [ ! -d "bundle/target" ] || [ "$1" = "--build" ]; then
        print_info "Building project..."
        mvn clean package -DskipTests -q
        print_success "Build complete"
        echo ""
    fi

    print_success "Starting application..."
    echo ""
    echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
    echo ""

    # Run with preprod profile
    mvn spring-boot:run -pl bundle -Dspring-boot.run.profiles=preprod
}

# Handle script arguments
case "${1:-}" in
    --help|-h)
        print_header
        echo "Usage: ./run-preprod.sh [options]"
        echo ""
        echo "Options:"
        echo "  --build       Clean build before running"
        echo "  --help, -h    Show this help message"
        echo ""
        echo "Prerequisites:"
        echo "  - Docker must be running"
        echo "  - Preprod infrastructure will start automatically if not running"
        echo ""
        echo "What this script does:"
        echo "  1. Checks preprod environment status"
        echo "  2. Starts preprod infrastructure if needed (PostgreSQL + Ollama)"
        echo "  3. Loads configuration from .env.preprod"
        echo "  4. Starts Spring Boot with 'preprod' profile"
        echo "  5. Connects to preprod database (port 5433)"
        echo "  6. Connects to preprod Ollama (port 11435)"
        echo ""
        exit 0
        ;;
    *)
        main "$@"
        ;;
esac
