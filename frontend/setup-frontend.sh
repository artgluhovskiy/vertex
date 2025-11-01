#!/bin/bash

# Synapse Frontend Setup Script
# This script initializes the frontend monorepo

set -e  # Exit on error

echo "🚀 Setting up Synapse frontend monorepo..."
echo ""

# Check if PNPM is installed
if ! command -v pnpm &> /dev/null; then
    echo "❌ PNPM is not installed!"
    echo "📦 Installing PNPM globally..."
    npm install -g pnpm@8.15.0
fi

echo "✅ PNPM version: $(pnpm --version)"
echo ""

# Remove existing node_modules and lock files to start fresh
echo "🧹 Cleaning up existing installations..."
rm -rf node_modules pnpm-lock.yaml
find . -name "node_modules" -type d -prune -exec rm -rf {} \;
echo "✅ Cleanup complete"
echo ""

# Install root dependencies first
echo "📦 Installing root dependencies..."
pnpm install --no-frozen-lockfile
echo "✅ Root dependencies installed"
echo ""

echo "🎉 Frontend monorepo setup complete!"
echo ""
echo "🚀 Next steps:"
echo "   1. Start backend: ./mvnw spring-boot:run -pl bundle"
echo "   2. Start frontend: pnpm dev:web"
echo "   3. Open http://localhost:5173"
echo ""
