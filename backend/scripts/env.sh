#!/bin/bash

# Vertex Environment Manager
# Usage: ./env.sh <environment> <command> [args]
# Environments: dev, preprod
# Commands: start, stop, restart, status, logs, backup, restore, clean, etc.

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

print_header() {
    local env_name=$(echo "$1" | tr '[:lower:]' '[:upper:]')
    echo -e "${CYAN}╔══════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║  Vertex $env_name Environment Manager      ${NC}"
    echo -e "${CYAN}╚══════════════════════════════════════════╝${NC}"
    echo ""
}

# Environment configuration
setup_env_config() {
    local env=$1

    case $env in
        dev)
            ENV_NAME="dev"
            COMPOSE_FILE="docker-compose.yml"
            POSTGRES_CONTAINER="vertex-postgres"
            OLLAMA_CONTAINER="vertex-ollama"
            POSTGRES_PORT="5432"
            OLLAMA_PORT="11434"
            POSTGRES_DB="vertex"
            POSTGRES_USER="vertex"
            DB_PASSWORD="vertex"
            NETWORK_NAME="vertex-network"
            DATA_DIR=""
            BACKUP_DIR=""
            ENV_FILE=""
            SUPPORTS_BACKUP=false
            AUTO_RESTART=false
            ;;
        preprod)
            ENV_NAME="preprod"
            COMPOSE_FILE="docker-compose.preprod.yml"
            POSTGRES_CONTAINER="vertex-postgres-preprod"
            OLLAMA_CONTAINER="vertex-ollama-preprod"
            POSTGRES_PORT="5433"
            OLLAMA_PORT="11435"
            POSTGRES_DB="vertex_preprod"
            POSTGRES_USER="vertex_preprod"
            DB_PASSWORD=""  # Will be loaded from .env.preprod
            NETWORK_NAME="vertex-preprod-network"
            DATA_DIR="${PROJECT_ROOT}/preprod-data"
            BACKUP_DIR="${PROJECT_ROOT}/backups"
            ENV_FILE="${PROJECT_ROOT}/.env.preprod"
            SUPPORTS_BACKUP=true
            AUTO_RESTART=true
            ;;
        *)
            print_error "Unknown environment: $env"
            echo "Available environments: dev, preprod"
            exit 1
            ;;
    esac
}

# Function to ensure directories exist (preprod only)
ensure_directories() {
    if [ "$ENV_NAME" != "preprod" ]; then
        return 0
    fi

    if [ ! -d "$DATA_DIR" ]; then
        print_info "Creating $ENV_NAME data directory..."
        mkdir -p "$DATA_DIR/postgres"
        mkdir -p "$DATA_DIR/ollama"
        print_success "Data directory created: $DATA_DIR"
    fi

    if [ ! -d "$BACKUP_DIR" ]; then
        print_info "Creating backup directory..."
        mkdir -p "$BACKUP_DIR"
        print_success "Backup directory created: $BACKUP_DIR"
    fi
}

# Function to ensure .env file exists (preprod only)
ensure_env_file() {
    if [ "$ENV_NAME" != "preprod" ] || [ -f "$ENV_FILE" ]; then
        return 0
    fi

    print_info "Creating $ENV_NAME environment file..."
    local generated_password="vertex_preprod_$(date +%s | sha256sum | base64 | head -c 32)"
    cat > "$ENV_FILE" << EOF
# Vertex Preprod Environment Configuration
# Generated automatically - customize as needed

# PostgreSQL Configuration
POSTGRES_PASSWORD=$generated_password
POSTGRES_PORT=$POSTGRES_PORT

# Data directories (absolute paths)
PREPROD_DATA_DIR=$DATA_DIR

# Ollama Configuration
OLLAMA_PORT=$OLLAMA_PORT

# Application Configuration (for reference)
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:$POSTGRES_PORT/$POSTGRES_DB
SPRING_DATASOURCE_USERNAME=$POSTGRES_USER
SPRING_DATASOURCE_PASSWORD=\${POSTGRES_PASSWORD}
SPRING_AI_OLLAMA_BASE_URL=http://localhost:$OLLAMA_PORT
EOF
    print_success "Environment file created: $ENV_FILE"
    print_warning "Review and update $ENV_FILE with secure passwords"
}

# Load environment variables (preprod only)
load_env_vars() {
    if [ "$ENV_NAME" = "preprod" ] && [ -f "$ENV_FILE" ]; then
        export $(grep -v '^#' "$ENV_FILE" | xargs)
        export PREPROD_DATA_DIR="$DATA_DIR"
        DB_PASSWORD="${POSTGRES_PASSWORD:-vertex_preprod}"
    fi
}

# Function to check if containers are running
is_running() {
    docker ps --format '{{.Names}}' | grep -q "^${1}$"
}

# Function to check if models are pulled
models_exist() {
    if ! is_running "$OLLAMA_CONTAINER"; then
        return 1
    fi

    local models=$(docker exec "$OLLAMA_CONTAINER" ollama list 2>/dev/null | grep -c "nomic-embed-text" || echo "0")
    [ "${models:-0}" -gt 0 ]
}

# Start command
cmd_start() {
    print_header "$ENV_NAME"
    print_info "Starting Vertex $ENV_NAME environment..."
    echo ""

    ensure_directories
    ensure_env_file
    load_env_vars

    # Check if already running
    if is_running "$POSTGRES_CONTAINER" && is_running "$OLLAMA_CONTAINER"; then
        print_warning "Containers are already running"
        docker-compose -f "$COMPOSE_FILE" ps

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
        docker-compose -f "$COMPOSE_FILE" up -d

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
        if docker exec "$POSTGRES_CONTAINER" pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB" > /dev/null 2>&1; then
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
        if curl -f -s "http://localhost:$OLLAMA_PORT/api/tags" > /dev/null 2>&1; then
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
        if docker exec "$OLLAMA_CONTAINER" ollama pull nomic-embed-text > /dev/null 2>&1; then
            print_success "nomic-embed-text pulled successfully"
        else
            print_error "Failed to pull nomic-embed-text"
            exit 1
        fi

        # Pull mxbai-embed-large (optional)
        print_info "Pulling mxbai-embed-large (669 MB, optional)..."
        if docker exec "$OLLAMA_CONTAINER" ollama pull mxbai-embed-large > /dev/null 2>&1; then
            print_success "mxbai-embed-large pulled successfully"
        else
            print_warning "Failed to pull mxbai-embed-large (continuing anyway)"
        fi
    fi

    echo ""
    print_success "🚀 $ENV_NAME environment is ready!"
    echo ""
    cmd_status
}

# Stop command
cmd_stop() {
    print_info "Stopping Vertex $ENV_NAME environment..."

    if ! is_running "$POSTGRES_CONTAINER" && ! is_running "$OLLAMA_CONTAINER"; then
        print_warning "Containers are not running"
        return 0
    fi

    docker-compose -f "$COMPOSE_FILE" stop

    if [ $? -eq 0 ]; then
        if [ "$SUPPORTS_BACKUP" = true ]; then
            print_success "Environment stopped (data preserved)"
        else
            print_success "Environment stopped"
        fi
    else
        print_error "Failed to stop environment"
        exit 1
    fi
}

# Restart command
cmd_restart() {
    print_info "Restarting Vertex $ENV_NAME environment..."
    cmd_stop
    echo ""
    cmd_start
}

# Status command
cmd_status() {
    print_info "$ENV_NAME Environment Status:"
    echo ""

    # Container status
    if is_running "$POSTGRES_CONTAINER"; then
        print_success "PostgreSQL: Running (port $POSTGRES_PORT)"

        # Show database size
        db_size=$(docker exec "$POSTGRES_CONTAINER" psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -t -c "SELECT pg_size_pretty(pg_database_size('$POSTGRES_DB'));" 2>/dev/null | xargs)
        if [ -n "$db_size" ]; then
            echo "    Database size: $db_size"
        fi
    else
        print_error "PostgreSQL: Not running"
    fi

    if is_running "$OLLAMA_CONTAINER"; then
        print_success "Ollama: Running (port $OLLAMA_PORT)"

        # Check models
        if models_exist; then
            echo ""
            print_info "Available models:"
            docker exec "$OLLAMA_CONTAINER" ollama list 2>/dev/null || echo "  (unable to list models)"
        else
            print_warning "No embedding models found"
        fi
    else
        print_error "Ollama: Not running"
    fi

    # Show data locations for preprod
    if [ "$ENV_NAME" = "preprod" ]; then
        echo ""
        print_info "Data locations:"
        echo "  PostgreSQL: $DATA_DIR/postgres"
        echo "  Ollama: $DATA_DIR/ollama"
        echo "  Backups: $BACKUP_DIR"
    fi

    echo ""
    print_info "Connection URLs:"
    echo "  PostgreSQL: jdbc:postgresql://localhost:$POSTGRES_PORT/$POSTGRES_DB"
    if [ "$ENV_NAME" = "preprod" ]; then
        echo "  Credentials: $POSTGRES_USER / (see .env.preprod)"
    else
        echo "  Credentials: $POSTGRES_USER / $DB_PASSWORD"
    fi
    echo "  Ollama API: http://localhost:$OLLAMA_PORT"
}

# Logs command
cmd_logs() {
    local service="$1"
    if [ -n "$service" ]; then
        # Map service name to container name
        case $service in
            postgres|pg|psql)
                service="postgres"
                ;;
            ollama)
                service="ollama"
                ;;
        esac

        # For preprod, append -preprod suffix
        if [ "$ENV_NAME" = "preprod" ]; then
            service="$service-preprod"
        fi

        docker-compose -f "$COMPOSE_FILE" logs -f "$service"
    else
        docker-compose -f "$COMPOSE_FILE" logs -f
    fi
}

# Backup command (preprod only)
cmd_backup() {
    if [ "$SUPPORTS_BACKUP" = false ]; then
        print_error "Backup is only supported for preprod environment"
        exit 1
    fi

    if ! is_running "$POSTGRES_CONTAINER"; then
        print_error "PostgreSQL is not running. Start $ENV_NAME environment first."
        exit 1
    fi

    local backup_name="vertex_${ENV_NAME}_$(date +%Y%m%d_%H%M%S).sql"
    local backup_path="$BACKUP_DIR/$backup_name"

    print_info "Creating backup: $backup_name"

    docker exec "$POSTGRES_CONTAINER" pg_dump -U "$POSTGRES_USER" -d "$POSTGRES_DB" -F c -f "/backups/$backup_name"

    if [ $? -eq 0 ] && [ -f "$backup_path" ]; then
        local size=$(du -h "$backup_path" | cut -f1)
        print_success "Backup created: $backup_path ($size)"

        # Keep only last 10 backups
        local backup_count=$(ls -1 "$BACKUP_DIR"/vertex_${ENV_NAME}_*.sql 2>/dev/null | wc -l)
        if [ $backup_count -gt 10 ]; then
            print_info "Removing old backups (keeping last 10)..."
            ls -1t "$BACKUP_DIR"/vertex_${ENV_NAME}_*.sql | tail -n +11 | xargs rm -f
            print_success "Old backups cleaned up"
        fi
    else
        print_error "Backup failed"
        exit 1
    fi
}

# Restore command (preprod only)
cmd_restore() {
    if [ "$SUPPORTS_BACKUP" = false ]; then
        print_error "Restore is only supported for preprod environment"
        exit 1
    fi

    if ! is_running "$POSTGRES_CONTAINER"; then
        print_error "PostgreSQL is not running. Start $ENV_NAME environment first."
        exit 1
    fi

    local backup_file="$1"

    if [ -z "$backup_file" ]; then
        print_info "Available backups:"
        ls -1t "$BACKUP_DIR"/vertex_${ENV_NAME}_*.sql 2>/dev/null | head -n 10 | while read file; do
            local filename=$(basename "$file")
            local size=$(du -h "$file" | cut -f1)
            echo "  - $filename ($size)"
        done
        echo ""
        print_error "Usage: ./env.sh $ENV_NAME restore <backup_file>"
        exit 1
    fi

    # Resolve backup file path
    if [ ! -f "$BACKUP_DIR/$backup_file" ] && [ ! -f "$backup_file" ]; then
        print_error "Backup file not found: $backup_file"
        exit 1
    fi

    if [ ! -f "$BACKUP_DIR/$backup_file" ]; then
        backup_file="$backup_file"
    else
        backup_file="$BACKUP_DIR/$backup_file"
    fi

    print_warning "This will drop and recreate the database!"
    read -p "Continue? (yes/no): " -r
    echo

    if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
        print_info "Restore cancelled"
        return 0
    fi

    print_info "Restoring from backup..."

    # Drop and recreate database
    docker exec "$POSTGRES_CONTAINER" psql -U "$POSTGRES_USER" -d postgres -c "DROP DATABASE IF EXISTS $POSTGRES_DB;"
    docker exec "$POSTGRES_CONTAINER" psql -U "$POSTGRES_USER" -d postgres -c "CREATE DATABASE $POSTGRES_DB;"

    # Restore
    local backup_name=$(basename "$backup_file")
    docker exec "$POSTGRES_CONTAINER" pg_restore -U "$POSTGRES_USER" -d "$POSTGRES_DB" -F c "/backups/$backup_name"

    if [ $? -eq 0 ]; then
        print_success "Database restored successfully"
    else
        print_error "Restore failed"
        exit 1
    fi
}

# Clean command
cmd_clean() {
    if [ "$ENV_NAME" = "preprod" ]; then
        print_warning "This will PERMANENTLY DELETE all $ENV_NAME data!"
        print_warning "PostgreSQL database and Ollama models will be removed."
        echo ""
        read -p "Type 'DELETE' to confirm: " -r
        echo

        if [[ $REPLY != "DELETE" ]]; then
            print_info "Clean operation cancelled"
            return 0
        fi
    else
        print_warning "This will stop containers and remove all data (PostgreSQL + Ollama models)"
        read -p "Are you sure? (yes/no): " -r
        echo

        if [[ ! $REPLY =~ ^[Yy][Ee][Ss]$ ]]; then
            print_info "Clean operation cancelled"
            return 0
        fi
    fi

    print_info "Cleaning up..."
    docker-compose -f "$COMPOSE_FILE" down -v

    # Remove data directory for preprod
    if [ "$ENV_NAME" = "preprod" ] && [ -d "$DATA_DIR" ]; then
        rm -rf "$DATA_DIR"
        print_success "Data directory removed"
        print_info "Backups are preserved in: $BACKUP_DIR"
    fi

    print_success "Environment cleaned"
}

# PS command
cmd_ps() {
    docker-compose -f "$COMPOSE_FILE" ps
}

# Test command
cmd_test() {
    print_info "Testing $ENV_NAME environment..."
    echo ""

    # Test PostgreSQL
    print_info "Testing PostgreSQL connection..."
    if docker exec "$POSTGRES_CONTAINER" psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SELECT 1;" > /dev/null 2>&1; then
        print_success "PostgreSQL connection successful"
    else
        print_error "PostgreSQL connection failed"
        return 1
    fi

    # Test pgvector extension
    print_info "Testing pgvector extension..."
    if docker exec "$POSTGRES_CONTAINER" psql -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SELECT extname FROM pg_extension WHERE extname = 'vector';" | grep -q "vector"; then
        print_success "pgvector extension available"
    else
        print_warning "pgvector extension not installed"
    fi

    # Test Ollama API
    print_info "Testing Ollama API..."
    if curl -f -s "http://localhost:$OLLAMA_PORT/api/tags" > /dev/null 2>&1; then
        print_success "Ollama API responding"
    else
        print_error "Ollama API not responding"
        return 1
    fi

    # Test embedding generation
    if models_exist; then
        print_info "Testing embedding generation..."
        response=$(curl -s -X POST "http://localhost:$OLLAMA_PORT/api/embeddings" \
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

# Shell command
cmd_shell() {
    local shell_type="${1:-postgres}"

    case $shell_type in
        postgres|pg|psql)
            print_info "Opening PostgreSQL shell..."
            docker exec -it "$POSTGRES_CONTAINER" psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"
            ;;
        ollama)
            print_info "Opening Ollama container shell..."
            docker exec -it "$OLLAMA_CONTAINER" /bin/bash
            ;;
        *)
            print_error "Unknown shell type: $shell_type"
            echo "Usage: ./env.sh $ENV_NAME shell [postgres|ollama]"
            exit 1
            ;;
    esac
}

# Info command
cmd_info() {
    print_header "$ENV_NAME"
    echo "$ENV_NAME Environment Information"
    echo ""

    if [ "$ENV_NAME" = "dev" ]; then
        echo "Purpose: Local development and testing"
        echo "Data persistence: Named Docker volumes"
        echo ""
        print_info "Configuration:"
        echo "  - PostgreSQL port: $POSTGRES_PORT"
        echo "  - Ollama port: $OLLAMA_PORT"
        echo "  - Network: $NETWORK_NAME"
        echo "  - Auto-restart: No"
        echo "  - Backup support: No"
    else
        echo "Purpose: Long-lived environment for manual testing and demos"
        echo "Data persistence: Bind-mounted local directories"
        echo ""
        print_info "Key features:"
        echo "  - Different ports (5433, 11435) - no conflicts with dev"
        echo "  - Auto-restart on system reboot"
        echo "  - Backup/restore support"
        echo "  - Data stored in: $DATA_DIR"
        echo "  - Backups stored in: $BACKUP_DIR"
    fi

    echo ""
    print_info "Available commands:"
    echo "  start, stop, restart, status, logs, ps, test, shell"
    if [ "$SUPPORTS_BACKUP" = true ]; then
        echo "  backup, restore, clean (preprod only)"
    else
        echo "  clean"
    fi
    echo ""
}

# Show usage
show_usage() {
    echo "Vertex Environment Manager"
    echo ""
    echo "Usage: ./env.sh <environment> <command> [args]"
    echo ""
    echo "Environments:"
    echo "  dev              Development environment (ports 5432, 11434)"
    echo "  preprod          Pre-production environment (ports 5433, 11435)"
    echo ""
    echo "Commands:"
    echo "  start            Start the environment"
    echo "  stop             Stop the environment"
    echo "  restart          Restart the environment"
    echo "  status           Show status and connection info"
    echo "  logs [service]   Show logs (optionally for: postgres, ollama)"
    echo "  ps               Show container status"
    echo "  test             Test all services are working"
    echo "  shell <type>     Open interactive shell (postgres, ollama)"
    echo "  info             Show detailed environment information"
    echo "  clean            Remove all data (requires confirmation)"
    echo ""
    echo "Preprod-only commands:"
    echo "  backup           Create database backup"
    echo "  restore <file>   Restore database from backup"
    echo ""
    echo "Examples:"
    echo "  ./env.sh dev start                    # Start dev environment"
    echo "  ./env.sh dev status                   # Check dev status"
    echo "  ./env.sh preprod start                # Start preprod"
    echo "  ./env.sh preprod backup               # Backup preprod DB"
    echo "  ./env.sh preprod restore backup.sql   # Restore from backup"
    echo "  ./env.sh dev shell postgres           # Open psql in dev"
    echo "  ./env.sh preprod logs postgres        # View preprod logs"
    echo ""
}

# Main command dispatcher
if [ $# -lt 1 ]; then
    show_usage
    exit 1
fi

ENV_ARG="$1"
COMMAND="${2:-}"

# Setup environment configuration
setup_env_config "$ENV_ARG"

# Dispatch command
case "$COMMAND" in
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
        cmd_logs "${3:-}"
        ;;
    backup)
        cmd_backup
        ;;
    restore)
        cmd_restore "${3:-}"
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
        cmd_shell "${3:-postgres}"
        ;;
    info)
        cmd_info
        ;;
    "")
        show_usage
        exit 1
        ;;
    *)
        print_error "Unknown command: $COMMAND"
        echo ""
        show_usage
        exit 1
        ;;
esac
