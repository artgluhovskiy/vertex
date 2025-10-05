📋 IMPLEMENTATION PLAN: Phase 1.2 - Basic Note CRUD

Overview

Implement Create, Read, Update, Delete operations for Notes with proper persistence layer, business logic, REST API, and comprehensive testing.

  ---
Current State Analysis

✅ Already Implemented

- Domain Model: Note.java - Immutable domain entity with @Value and @Builder
- Repository Interface: NoteRepository.java - Domain contract with CRUD methods
- Application Service Interface: NoteApplicationService.java - Application layer contract
- DTOs: NoteDto.java, CreateNoteCommand.java, UpdateNoteCommand.java
- User & Directory aggregates: Basic structure exists

❌ Missing Components

1. Infrastructure Layer: JPA entities, repository implementations
2. Application Service Implementation: Business logic orchestration
3. Mapper: Domain ↔ Entity, Domain ↔ DTO conversions
4. REST Controller: HTTP endpoints
5. Exception Handling: Global error handling
6. Configuration: JPA, datasource setup
7. Database Schema: Liquibase migrations
8. Tests: Unit, integration, functional tests

  ---
Implementation Steps

Step 1: Infrastructure - Database Setup

1.1 Create Liquibase Migration

- File: infrastructure/src/main/resources/db/changelog/001-create-notes-table.sql
- Content: Create notes table with:
    - id UUID PRIMARY KEY
    - user_id UUID NOT NULL (FK to users)
    - directory_id UUID (FK to directories, nullable)
    - title TEXT NOT NULL
    - content TEXT
    - summary TEXT
    - metadata JSONB
    - created_ts TIMESTAMPTZ NOT NULL
    - updated_ts TIMESTAMPTZ NOT NULL
    - version INTEGER NOT NULL DEFAULT 1
    - Indexes: idx_notes_user_id, idx_notes_directory_id, idx_notes_updated_ts
    - Constraints: FK to users, FK to directories (ON DELETE SET NULL)

1.2 Create Liquibase Changelog Master

- File: infrastructure/src/main/resources/db/changelog/db.changelog-master.yaml
- Content: Include all migration files

  ---
Step 2: Infrastructure - JPA Entities

2.1 Create Note JPA Entity

- File: infrastructure/src/main/java/org/art/vertex/infrastructure/persistence/entity/NoteEntity.java
- Annotations: @Entity, @Table(name = "notes")
- Fields: Map all Note domain fields to JPA columns
- Use: @Column, @ManyToOne for User/Directory relationships
- No business logic - pure persistence mapping

2.2 Create UserEntity (if not exists)

- File: infrastructure/.../entity/UserEntity.java
- Basic JPA entity for users table

2.3 Create DirectoryEntity (if not exists)

- File: infrastructure/.../entity/DirectoryEntity.java
- Basic JPA entity for directories table

  ---
Step 3: Infrastructure - Repository Implementation

3.1 Create Spring Data JPA Repository

- File: infrastructure/.../persistence/NoteJpaDataRepository.java
- Extends: JpaRepository<NoteEntity, UUID>
- Methods:
    - Default CRUD from JpaRepository
    - Custom query methods: findAllByUserIdOrderByUpdatedTsDesc(UUID userId)
    - Custom query: findAllByDirectoryId(UUID directoryId)

3.2 Create Repository Implementation

- File: infrastructure/.../persistence/NoteRepositoryImpl.java
- Implements: NoteRepository (domain interface)
- Dependencies: NoteJpaDataRepository, NoteMapper
- Methods:
    - save(Note note) → map to entity → save → map back to domain
    - getById(UUID id) → find + throw exception if not found
    - findById(UUID id) → return Optional
    - findAll(User user) → query by userId
    - deleteById(UUID id) → delete entity

  ---
Step 4: Infrastructure - Mappers

4.1 Create Note Entity Mapper

- File: infrastructure/.../mapper/NoteEntityMapper.java
- Annotation: Component (no stereotype)
- Methods:
    - NoteEntity toEntity(Note note) - domain → entity
    - Note toDomain(NoteEntity entity) - entity → domain
    - Handle User, Directory relationships (fetch if needed or use lazy loading)

4.2 Create Note DTO Mapper

- File: application/.../note/mapper/NoteMapper.java
- Annotation: Component
- Methods:
    - NoteDto toDto(Note note) - domain → DTO
    - Note toDomain(CreateNoteCommand command, User user, Directory directory) - command → domain
    - Extract userId, directoryId for DTO

  ---
Step 5: Application Service Implementation

5.1 Create Application Service Implementation

- File: application/.../note/NoteApplicationServiceImpl.java
- Implements: NoteApplicationService
- Annotations: @Transactional, @RequiredArgsConstructor, @Slf4j
- Dependencies:
    - NoteRepository noteRepository
    - UserRepository userRepository
    - DirectoryRepository directoryRepository
    - NoteMapper noteMapper

5.2 Implement Business Methods

- createNote(CreateNoteCommand):
  a. Load User by userId (throw exception if not found)
  b. Load Directory by directoryId if provided (optional)
  c. Generate UUID for note
  d. Create Note domain object using Note.create(...)
  e. Save via repository
  f. Map to DTO and return
  g. Log debug: "Note created. Note id: {}, user id: {}"
- updateNote(UUID noteId, UpdateNoteCommand):
  a. Load existing Note by id
  b. Create new Note with updated fields using .toBuilder()
  c. Increment version
  d. Save via repository
  e. Map to DTO and return
  f. Log debug: "Note updated. Note id: {}"
- getNote(UUID noteId):
  a. Load Note by id (throw exception if not found)
  b. Map to DTO and return
- getNotesByUser(UUID userId):
  a. Load User by id
  b. Query notes via repository
  c. Map list to DTOs
  d. Return list
- deleteNote(UUID noteId):
  a. Verify note exists
  b. Delete via repository
  c. Log info: "Note deleted. Note id: {}"

  ---
Step 6: Domain - Business Logic Enhancement

6.1 Add Business Methods to Note Domain

- File: domain/.../note/model/Note.java
- Add methods:
    - Note updateContent(String newContent, LocalDateTime ts) - return new Note with updated content
    - Note updateTitle(String newTitle, LocalDateTime ts) - with validation
    - Note moveToDirectory(Directory newDirectory, LocalDateTime ts) - change directory
    - Validation: validateTitle(String title), validateContent(String content)

  ---
Step 7: REST API Layer

7.1 Create REST Controller

- File: web/src/main/java/org/art/vertex/web/controller/NoteController.java
- Annotations: @RestController, @RequestMapping("/api/v1/notes"), @RequiredArgsConstructor, @Slf4j
- Dependencies: NoteApplicationService

7.2 Implement Endpoints

- POST /api/v1/notes - Create note
    - Request body: CreateNoteCommand
    - Response: NoteDto (201 CREATED)
    - Validation: @Valid on command
- GET /api/v1/notes/{noteId} - Get single note
    - Path variable: noteId
    - Response: NoteDto (200 OK)
- PUT /api/v1/notes/{noteId} - Update note
    - Path variable: noteId
    - Request body: UpdateNoteCommand
    - Response: NoteDto (200 OK)
- DELETE /api/v1/notes/{noteId} - Delete note
    - Path variable: noteId
    - Response: 204 NO CONTENT
- GET /api/v1/notes - Get all user notes
    - Query param: userId (UUID)
    - Response: List<NoteDto> (200 OK)

  ---
Step 8: Exception Handling

8.1 Create Domain Exceptions

- File: domain/.../note/exception/NoteNotFoundException.java
- Extend RuntimeException
- Constructor: NoteNotFoundException(UUID noteId)
- Message: "Note not found. Note id: {noteId}"

8.2 Create Global Exception Handler

- File: web/.../exception/GlobalExceptionHandler.java
- Annotation: @RestControllerAdvice, @Slf4j
- Handle:
    - NoteNotFoundException → 404 NOT FOUND
    - UserNotFoundException → 404 NOT FOUND
    - IllegalArgumentException → 400 BAD REQUEST
    - MethodArgumentNotValidException → 400 BAD REQUEST (validation errors)
    - Exception → 500 INTERNAL SERVER ERROR

8.3 Create Error Response DTO

- File: web/.../dto/ErrorResponse.java
- Fields: message, timestamp, path, status

  ---
Step 9: Configuration

9.1 JPA Configuration

- File: infrastructure/.../config/JpaConfig.java
- Annotation: @Configuration(proxyBeanMethods = false), @EnableJpaRepositories
- Bean: EntityManagerFactory, transaction manager if needed
- Scan: Repository packages

9.2 Application Properties

- File: bundle/src/main/resources/application.yml
- Configure:
    - DataSource: PostgreSQL connection
    - JPA: Hibernate dialect, ddl-auto=validate (use Liquibase)
    - Liquibase: enabled, changelog path
    - Logging: debug for org.art.vertex

9.3 Test Properties

- File: bundle/src/main/resources/application-test.yml
- Configure H2 in-memory or Testcontainers PostgreSQL

  ---
Step 10: Testing - Unit Tests

10.1 Note Domain Tests

- File: domain/.../note/model/NoteTest.java
- Test:
    - shouldCreateNoteWithValidData()
    - shouldUpdateContent()
    - shouldUpdateTitle()
    - shouldThrowExceptionForInvalidTitle()
    - shouldIncrementVersionOnUpdate()

10.2 Application Service Unit Tests

- File: application/.../note/NoteApplicationServiceTest.java
- Annotations: @MockitoSettings, @InjectMocks, @Mock
- Mock: All repositories and mappers
- Test:
    - shouldCreateNoteSuccessfully()
    - shouldUpdateNoteSuccessfully()
    - shouldGetNoteById()
    - shouldThrowExceptionWhenNoteNotFound()
    - shouldDeleteNote()
    - shouldGetNotesByUser()

10.3 Mapper Tests

- File: infrastructure/.../mapper/NoteEntityMapperTest.java
- Test:
    - shouldMapDomainToEntity()
    - shouldMapEntityToDomain()
    - shouldHandleNullDirectory()

  ---
Step 11: Testing - Integration Tests

11.1 Repository Integration Tests

- File: infrastructure/.../persistence/NoteRepositoryImplIT.java
- Annotations: @DataJpaTest, @AutoConfigureTestDatabase
- Use: Testcontainers PostgreSQL or H2
- Test:
    - shouldSaveAndRetrieveNote()
    - shouldFindNotesByUser()
    - shouldUpdateNote()
    - shouldDeleteNote()
    - shouldThrowExceptionWhenNoteNotFound()

  ---
Step 12: Testing - Functional Tests

12.1 Note API Functional Tests

- File: bundle/.../note/NoteFunctionalTest.java
- Annotations: @SpringBootTest(webEnvironment = RANDOM_PORT), @ActiveProfiles("test")
- Use: TestRestTemplate or RestAssured
- Test Full Flows:
    - shouldCreateNoteSuccessfully() - POST → 201
    - shouldGetNoteById() - GET → 200
    - shouldUpdateNote() - PUT → 200
    - shouldDeleteNote() - DELETE → 204
    - shouldReturnNotFoundForInvalidNoteId() - GET → 404
    - shouldGetAllNotesForUser() - GET → 200 with list
    - shouldValidateCreateNoteCommand() - POST with invalid data → 400

  ---
Dependencies to Add (pom.xml)

Infrastructure Module

  <!-- PostgreSQL -->
  <dependency>
      <groupId>org.postgresql</groupId>
      <artifactId>postgresql</artifactId>
  </dependency>

  <!-- Liquibase -->
  <dependency>
      <groupId>org.liquibase</groupId>
      <artifactId>liquibase-core</artifactId>
  </dependency>

  <!-- Spring Data JPA -->
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
  </dependency>

  <!-- Hibernate Types for JSONB -->
  <dependency>
      <groupId>io.hypersistence</groupId>
      <artifactId>hypersistence-utils-hibernate-63</artifactId>
      <version>3.7.0</version>
  </dependency>

Bundle Module (Test)

  <!-- Testcontainers PostgreSQL -->
  <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>postgresql</artifactId>
      <scope>test</scope>
  </dependency>

  <!-- H2 for fast unit tests -->
  <dependency>
      <groupId>com.h2database</groupId>
      <artifactId>h2</artifactId>
      <scope>test</scope>
  </dependency>

  ---
Validation Rules (Bean Validation)

CreateNoteCommand

- @NotNull UUID userId
- @NotBlank String title - max 255 chars
- String content - optional
- UUID directoryId - optional
- List<UUID> tagIds - optional (for later)

UpdateNoteCommand

- String title - optional, if present max 255 chars
- String content - optional
- UUID directoryId - optional

  ---
Success Criteria

Functional

✅ User can create a note with title and content✅ User can retrieve a single note by ID✅ User can update note title and content✅ User can delete a note✅ User can get
all their notes✅ Notes are associated with users and directories

Technical

✅ All unit tests pass (>90% coverage)✅ All integration tests pass✅ All functional tests pass✅ Build succeeds: mvn clean package✅ Application starts successfully✅
Database migrations run correctly✅ REST API returns proper HTTP status codes✅ Validation works correctly✅ Exception handling works globally

  ---
Implementation Order (Priority)

1. Database setup (Step 1) - Foundation
2. JPA entities (Step 2) - Data layer
3. Repository implementation (Step 3) - Persistence
4. Mappers (Step 4) - Translation layer
5. Application service (Step 5) - Business orchestration
6. REST controller (Step 7) - API exposure
7. Exception handling (Step 8) - Error management
8. Configuration (Step 9) - Wire everything together
9. Unit tests (Step 10) - Verify logic
10. Integration tests (Step 11) - Verify persistence
11. Functional tests (Step 12) - End-to-end verification
12. Domain enhancements (Step 6) - As needed during implementation

  ---
Notes

- Immutability: Keep domain objects immutable, use .toBuilder() for updates
- Validation: Domain validates business rules, Bean Validation validates API input
- Transactions: Application service methods are @Transactional
- Logging: Use structured logging with SLF4J + Lombok @Slf4j
- Constructor Injection: Use @RequiredArgsConstructor everywhere
- No circular dependencies: Keep dependency graph acyclic
- Tests: Follow Given-When-Then structure, use AssertJ assertions
- Exception handling: Domain throws business exceptions, REST layer maps to HTTP status
- Security: Will be added in next phase (Phase 1.1 User Auth should handle this)

  ---
Risks & Mitigations

| Risk                                | Mitigation                                   |
  |-------------------------------------|----------------------------------------------|
| User/Directory aggregates not ready | Create minimal implementations if needed     |
| PostgreSQL not available locally    | Use Testcontainers for tests                 |
| JPA entity mapping complexity       | Start simple, add complexity incrementally   |
| Test context caching issues         | Use base test classes, avoid @DirtiesContext |

  ---
Estimated Effort: 2-3 days for complete implementation + testing
