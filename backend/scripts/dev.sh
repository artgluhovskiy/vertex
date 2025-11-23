#!/bin/bash

# Vertex Development Environment Manager
# Usage: ./dev.sh [start|stop|restart|status|logs|clean]

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
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

# Function to check if containers are running
is_running() {
    docker ps --format '{{.Names}}' | grep -q "vertex-$1"
}

# Function to check if models are pulled
models_exist() {
    if ! is_running "ollama"; then
        return 1
    fi

    local models=$(docker exec vertex-ollama ollama list 2>/dev/null | grep -c "nomic-embed-text" || echo "0")
    [ "$models" -gt 0 ]
}

# Start command
cmd_start() {
    print_info "Starting Vertex development environment..."
    echo ""

    # Check if already running
    if is_running "postgres" && is_running "ollama"; then
        print_warning "Containers are already running"
        docker-compose ps

        # Check models
        if models_exist; then
            echo ""
            print_success "Environment is ready!"
            cmd_status
            return 0
        else
            echo ""
            print_warning "Models not found, pulling them now..."
        fi
    else
        # Start containers
        print_info "Starting Docker containers..."
        docker-compose up -d

        if [ $? -ne 0 ]; then
            print_error "Failed to start containers"
            exit 1
        fi

        echo ""
        print_success "Containers started"
    fi

    # Wait for services to be ready
    print_info "Waiting for services to be ready..."

    # Wait for PostgreSQL
    print_info "Waiting for PostgreSQL..."
    max_attempts=30
    attempt=0
    while [ $attempt -lt $max_attempts ]; do
        if docker exec vertex-postgres pg_isready -U vertex -d vertex > /dev/null 2>&1; then
            print_success "PostgreSQL is ready"
            break
        fi
        attempt=$((attempt + 1))
        if [ $attempt -eq $max_attempts ]; then
            print_error "PostgreSQL did not become ready in time"
            exit 1
        fi
        sleep 1
    done

    # Wait for Ollama
    print_info "Waiting for Ollama..."
    max_attempts=30
    attempt=0
    while [ $attempt -lt $max_attempts ]; do
        if curl -f -s http://localhost:11434/api/tags > /dev/null 2>&1; then
            print_success "Ollama is ready"
            break
        fi
        attempt=$((attempt + 1))
        if [ $attempt -eq $max_attempts ]; then
            print_error "Ollama did not become ready in time"
            exit 1
        fi
        sleep 1
    done

    echo ""

    # Check and pull models if needed
    if models_exist; then
        print_success "Embedding models already available"
    else
        print_info "Pulling embedding models (this may take 2-5 minutes)..."
        echo ""

        # Pull nomic-embed-text
        print_info "Pulling nomic-embed-text (274 MB)..."
        if docker exec vertex-ollama ollama pull nomic-embed-text > /dev/null 2>&1; then
            print_success "nomic-embed-text pulled successfully"
        else
            print_error "Failed to pull nomic-embed-text"
            exit 1
        fi

        # Pull mxbai-embed-large (optional)
        print_info "Pulling mxbai-embed-large (669 MB, optional)..."
        if docker exec vertex-ollama ollama pull mxbai-embed-large > /dev/null 2>&1; then
            print_success "mxbai-embed-large pulled successfully"
        else
            print_warning "Failed to pull mxbai-embed-large (continuing anyway)"
        fi
    fi

    echo ""
    print_success "🚀 Development environment is ready!"
    echo ""
    cmd_status
}

# Stop command
cmd_stop() {
    print_info "Stopping Vertex development environment..."

    if ! is_running "postgres" && ! is_running "ollama"; then
        print_warning "Containers are not running"
        return 0
    fi

    docker-compose down

    if [ $? -eq 0 ]; then
        print_success "Environment stopped"
    else
        print_error "Failed to stop environment"
        exit 1
    fi
}

# Restart command
cmd_restart() {
    print_info "Restarting Vertex development environment..."
    cmd_stop
    echo ""
    cmd_start
}

# Status command
cmd_status() {
    print_info "Environment Status:"
    echo ""

    # Container status
    if is_running "postgres"; then
        print_success "PostgreSQL: Running (port 5432)"
    else
        print_error "PostgreSQL: Not running"
    fi

    if is_running "ollama"; then
        print_success "Ollama: Running (port 11434)"

        # Check models
        if models_exist; then
            echo ""
            print_info "Available models:"
            docker exec vertex-ollama ollama list 2>/dev/null || echo "  (unable to list models)"
        else
            print_warning "No embedding models found"
        fi
    else
        print_error "Ollama: Not running"
    fi

    echo ""
    print_info "Connection URLs:"
    echo "  PostgreSQL: jdbc:postgresql://localhost:5432/vertex"
    echo "  Ollama API: http://localhost:11434"
}

# Logs command
cmd_logs() {
    if [ -n "$1" ]; then
        # Show logs for specific service
        docker-compose logs -f "$1"
    else
        # Show logs for all services
        docker-compose logs -f
    fi
}

# Clean command
cmd_clean() {
    print_warning "This will stop containers and remove all data (PostgreSQL + Ollama models)"
    read -p "Are you sure? (yes/no): " -r
    echo

    if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
        print_info "Clean operation cancelled"
        return 0
    fi

    print_info "Cleaning up..."
    docker-compose down -v

    if [ $? -eq 0 ]; then
        print_success "Environment cleaned"
    else
        print_error "Failed to clean environment"
        exit 1
    fi
}

# PS command (process status)
cmd_ps() {
    docker-compose ps
}

# Test command (verify environment)
cmd_test() {
    print_info "Testing environment..."
    echo ""

    # Test PostgreSQL
    print_info "Testing PostgreSQL connection..."
    if docker exec vertex-postgres psql -U vertex -d vertex -c "SELECT 1;" > /dev/null 2>&1; then
        print_success "PostgreSQL connection successful"
    else
        print_error "PostgreSQL connection failed"
        return 1
    fi

    # Test pgvector extension
    print_info "Testing pgvector extension..."
    if docker exec vertex-postgres psql -U vertex -d vertex -c "SELECT extname FROM pg_extension WHERE extname = 'vector';" | grep -q "vector"; then
        print_success "pgvector extension available"
    else
        print_warning "pgvector extension not installed"
    fi

    # Test Ollama API
    print_info "Testing Ollama API..."
    if curl -f -s http://localhost:11434/api/tags > /dev/null 2>&1; then
        print_success "Ollama API responding"
    else
        print_error "Ollama API not responding"
        return 1
    fi

    # Test embedding generation
    if models_exist; then
        print_info "Testing embedding generation..."
        response=$(curl -s -X POST http://localhost:11434/api/embeddings \
            -H "Content-Type: application/json" \
            -d '{"model": "nomic-embed-text", "prompt": "test"}' 2>/dev/null)

        if echo "$response" | grep -q "embedding"; then
            print_success "Embedding generation working"
        else
            print_warning "Embedding generation failed or not tested"
        fi
    fi

    echo ""
    print_success "All tests passed!"
}

# Shell command (open psql or ollama shell)
cmd_shell() {
    if [ "$1" = "postgres" ] || [ "$1" = "pg" ] || [ "$1" = "psql" ]; then
        print_info "Opening PostgreSQL shell..."
        docker exec -it vertex-postgres psql -U vertex -d vertex
    elif [ "$1" = "ollama" ]; then
        print_info "Opening Ollama container shell..."
        docker exec -it vertex-ollama /bin/bash
    else
        print_error "Unknown shell type: $1"
        echo "Usage: ./dev.sh shell [postgres|ollama]"
        exit 1
    fi
}

# Main command dispatcher
case "${1:-}" in
    start)
        cmd_start
        ;;
    stop)
        cmd_stop
        ;;
    restart)
        cmd_restart
        ;;
    status)
        cmd_status
        ;;
    logs)
        cmd_logs "${2:-}"
        ;;
    clean)
        cmd_clean
        ;;
    ps)
        cmd_ps
        ;;
    test)
        cmd_test
        ;;
    shell)
        cmd_shell "${2:-postgres}"
        ;;
    *)
        echo "Vertex Development Environment Manager"
        echo ""
        echo "Usage: ./dev.sh <command>"
        echo ""
        echo "Commands:"
        echo "  start         Start the development environment (containers + models)"
        echo "  stop          Stop the development environment"
        echo "  restart       Restart the development environment"
        echo "  status        Show status of services and models"
        echo "  logs [svc]    Show logs (optionally for specific service: postgres, ollama)"
        echo "  ps            Show container status"
        echo "  test          Test all services are working correctly"
        echo "  shell <type>  Open interactive shell (postgres, ollama)"
        echo "  clean         Stop and remove all data (requires confirmation)"
        echo ""
        echo "Examples:"
        echo "  ./dev.sh start              # Start everything"
        echo "  ./dev.sh status             # Check status"
        echo "  ./dev.sh logs postgres      # View PostgreSQL logs"
        echo "  ./dev.sh shell postgres     # Open psql shell"
        echo "  ./dev.sh stop               # Stop everything"
        echo ""
        exit 1
        ;;
esac
