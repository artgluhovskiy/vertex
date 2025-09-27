# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Structure

This is a Spring Boot 3.5.6 multi-module Maven project with Java 21:

- **Root module**: `vertex` - Parent POM with shared configuration
- **`domain` module**: Contains domain classes and business logic
- **`bundle` module**: Contains the main Spring Boot application (`VertexApplication.java`)

## Development Commands

### Build
```bash
mvnw clean compile
```

### Run Application
```bash
mvnw spring-boot:run -pl bundle
```

### Run Tests
```bash
mvnw test
```

### Package
```bash
mvnw clean package
```

## Architecture

This is a modular Spring Boot application following Domain-Driven Design principles:

- **`bundle` module**: Entry point and application configuration. Contains `VertexApplication.java` with `@SpringBootApplication` annotation
- **`domain` module**: Business domain classes separate from framework concerns

The project uses:
- Spring Boot Web starter for REST endpoints
- Lombok for reducing boilerplate code
- Maven wrapper (`mvnw`) for consistent builds across environments

## Key Dependencies

- Spring Boot 3.5.6 (Web)
- Java 21
- Lombok
- Spring AI BOM 1.0.2 (managed dependency)

Commented dependencies suggest future features for:
- JPA/Database integration
- Liquibase migrations
- PostgreSQL with PgVector for AI/ML features

## Module Dependencies

The `bundle` module depends on `domain` through the parent POM structure, allowing the application to use domain classes while keeping concerns separated.

## Project Documentation

**IMPORTANT**: Always read these files for complete project context:

- **README.md** - Complete project overview, architecture, roadmap, and technical specifications for the Synapse AI-powered note-taking application
- **DATA-MODEL.md** - Comprehensive database schema, entity relationships, and SQL queries for the hybrid search and directory hierarchy system

These files contain critical information about:
- Product vision and differentiation
- Technical architecture (Spring Boot + PostgreSQL + pgvector)
- Data model with directory hierarchy and graph relationships
- Development roadmap and implementation phases
- API endpoints and performance requirements