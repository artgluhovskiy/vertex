# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Development Rules Loading Protocol (CRITICAL - READ FIRST)

**BEFORE starting ANY planning or development task, you MUST:**

1. Read `.rules/java/rules.md` to understand which rules apply
2. Load the mandatory rules (ALWAYS):
    - `.rules/common/common.md`
    - `.rules/java/000-rules-mandatory.md`
3. Based on task keywords, load contextual rules from `.rules/java/rules.md`

**This is MANDATORY for all planning, implementation, and refactoring tasks.**

**Before ANY task:** Review `.rules/java/RULES_CHECKLIST.md` and load all applicable rules from `.rules/java/rules.md`.

## Project Documentation
- `PRD.md`
- `ROADMAP.md`
- `DATA-MODEL.md`
- `DDD.md`
- `SYS_DESIGN.md`
- `SEARCH.md`
- `IMPLEMENTATION_PLAN.md`

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
- **DDD.md** - Domain-Driven Design architecture, aggregate boundaries, entity patterns, and implementation strategy

These files contain critical information about:
- Product vision and differentiation (Zettelkasten + AI + local-first)
- Technical architecture (Spring Boot + PostgreSQL + pgvector)
- Data model with directory hierarchy and graph relationships
- DDD design patterns (controlled mutable entities, aggregates, services)
- Domain module organization and package structure
- Development roadmap and implementation phases
- API endpoints and performance requirements

## Conditional Rule Loading

**IMPORTANT**: Apply relevant rules based on user's request context. Rules are organized by concern:

**Always Apply:**
- `.claude/0-rules-mandatory.md` - MANDATORY rules for all development tasks

**Apply When Relevant to User Request:**
- `.claude/1-rules-common.md` - General development practices (constructor injection, etc.)
- `.claude/2-rules-design.md` - Architecture and design decisions (package organization, DDD)
- `.claude/4-rules-spring.md` - Spring Boot specific patterns and configurations
- `.claude/6-rules-messaging.md` - Message handling and communication patterns
- `.claude/7-rules-build.md` - Build system, Maven, compilation rules
- `.claude/8-rules-observability.md` - Logging, monitoring, metrics guidelines
- `.claude/9-rules-testing.md` - Testing strategies and patterns

**How to Apply:**
1. **Identify request context** - What is the user asking for?
2. **Load mandatory rules** - Always read `.claude/0-rules-mandatory.md`
3. **Load relevant rules** - Only read rule files that match the request context
4. **Apply rules** - Follow the loaded rules during implementation

**Examples:**
- User asks about "Spring configuration" → Load mandatory + spring + common rules
- User asks about "testing" → Load mandatory + testing + common rules
- User asks about "domain model" → Load mandatory + design + common rules
- User asks about "build issues" → Load mandatory + build + common rules
- Always create mapper class for mapping between models