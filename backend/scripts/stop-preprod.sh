#!/bin/bash

# Stop Vertex Preprod Environment
# This script stops the preprod infrastructure (PostgreSQL + Ollama)
# Containers are stopped but not removed - data is preserved

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
    echo -e "${CYAN}║  Stop Vertex Preprod Environment       ║${NC}"
    echo -e "${CYAN}╚══════════════════════════════════════════╝${NC}"
    echo ""
}

# Check if Spring Boot is running with preprod profile
check_spring_boot_running() {
    # Look for Spring Boot process with preprod profile
    local spring_pids=$(ps aux | grep -E "spring-boot:run.*preprod" | grep -v grep | awk '{print $2}')

    if [ -z "$spring_pids" ]; then
        return 1  # Not running
    else
        echo "$spring_pids"
        return 0  # Running
    fi
}

# Check if preprod infrastructure is running
check_preprod_status() {
    local postgres_running=false
    local ollama_running=false

    if docker ps --format '{{.Names}}' | grep -q "vertex-postgres-preprod"; then
        postgres_running=true
    fi

    if docker ps --format '{{.Names}}' | grep -q "vertex-ollama-preprod"; then
        ollama_running=true
    fi

    if [ "$postgres_running" = false ] && [ "$ollama_running" = false ]; then
        return 1  # Not running
    else
        return 0  # Running
    fi
}

# Stop Spring Boot application
stop_spring_boot() {
    local spring_pids

    if spring_pids=$(check_spring_boot_running); then
        print_warning "Spring Boot application is running (preprod profile)"
        echo ""
        echo "Found Spring Boot process(es): $spring_pids"
        echo ""

        local stop_app=false

        # Check if --all flag is set
        if [ "$STOP_ALL" = true ]; then
            stop_app=true
        else
            read -p "Stop Spring Boot application? (y/n): " -n 1 -r
            echo
            if [[ $REPLY =~ ^[Yy]$ ]]; then
                stop_app=true
            fi
        fi

        if [ "$stop_app" = true ]; then
            print_info "Stopping Spring Boot application..."
            for pid in $spring_pids; do
                kill -TERM "$pid" 2>/dev/null && print_success "Sent shutdown signal to PID $pid" || print_warning "Could not stop PID $pid"
            done

            # Wait a bit for graceful shutdown
            sleep 2

            # Check if still running
            if spring_pids=$(check_spring_boot_running); then
                print_warning "Spring Boot still running, you may need to stop it manually (Ctrl+C in the terminal)"
            else
                print_success "Spring Boot application stopped"
            fi
            echo ""
        else
            print_info "Skipping Spring Boot shutdown"
            print_warning "Remember to stop it manually (Ctrl+C in the terminal)"
            echo ""
        fi
    fi
}

# Stop preprod infrastructure
stop_preprod() {
    print_header

    # Check and stop Spring Boot first
    stop_spring_boot

    if ! check_preprod_status; then
        print_warning "Preprod infrastructure is not running"
        echo ""
        print_info "Nothing to stop"
        return 0
    fi

    print_info "Stopping preprod infrastructure..."
    echo ""

    # Show what's currently running
    print_info "Currently running containers:"
    docker ps --filter "name=vertex-.*-preprod" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    echo ""

    # Stop the containers
    "$SCRIPT_DIR/env.sh" preprod stop

    if [ $? -eq 0 ]; then
        echo ""
        print_success "Preprod infrastructure stopped successfully"
        echo ""
        print_info "Data preserved in: preprod-data/"
        print_info "Backups preserved in: backups/"
        echo ""
        print_info "To restart: ./run-preprod (or ./scripts/preprod.sh start)"
    else
        echo ""
        print_error "Failed to stop preprod infrastructure"
        exit 1
    fi
}

# Show status after stop
show_status() {
    print_info "Container status:"
    docker ps -a --filter "name=vertex-.*-preprod" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
}

# Main execution
main() {
    local show_status_flag=false
    STOP_ALL=false

    # Parse arguments
    for arg in "$@"; do
        case $arg in
            --status)
                show_status_flag=true
                ;;
            --all)
                STOP_ALL=true
                ;;
        esac
    done

    stop_preprod

    if [ "$show_status_flag" = true ]; then
        echo ""
        show_status
    fi
}

# Handle script arguments
case "${1:-}" in
    --help|-h)
        print_header
        echo "Usage: ./stop-preprod.sh [options]"
        echo ""
        echo "Options:"
        echo "  --all         Stop Spring Boot app + infrastructure (no prompt)"
        echo "  --status      Show container status after stopping"
        echo "  --help, -h    Show this help message"
        echo ""
        echo "What this script does:"
        echo "  1. Checks if Spring Boot application is running"
        echo "  2. Prompts to stop Spring Boot (or auto-stops with --all)"
        echo "  3. Checks if preprod containers are running"
        echo "  4. Stops preprod infrastructure (PostgreSQL + Ollama)"
        echo "  5. Preserves all data in preprod-data/ directory"
        echo "  6. Preserves all backups in backups/ directory"
        echo ""
        echo "Note:"
        echo "  - Containers are STOPPED, not removed"
        echo "  - All data is preserved"
        echo "  - Can be restarted anytime with ./run-preprod"
        echo ""
        echo "Examples:"
        echo "  ./stop-preprod.sh              # Stop (prompts for Spring Boot)"
        echo "  ./stop-preprod.sh --all        # Stop everything (no prompt)"
        echo "  ./stop-preprod.sh --status     # Stop and show status"
        echo ""
        exit 0
        ;;
    *)
        main "$@"
        ;;
esac
