#!/bin/bash

# Vertex Development Environment - Convenience wrapper
# This is a wrapper around env.sh for backward compatibility

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Forward all commands to env.sh with 'dev' environment
exec "$SCRIPT_DIR/env.sh" dev "$@"
