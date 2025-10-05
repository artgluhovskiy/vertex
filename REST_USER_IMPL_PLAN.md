# REST Layer Implementation Plan for User Operations

## Overview
Implement the REST API layer for User authentication and profile operations following Spring Boot best practices, hexagonal architecture principles, and the loaded development rules.

**Current State:**
- ✅ Domain layer: User entity, UserRepository interface
- ✅ Application layer: UserApplicationService with register, login, getCurrentUser methods
- ✅ Infrastructure layer: JPA entities, repository implementations, security adapters
- ❌ Web layer: **MISSING** - No REST controllers exist yet

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                      Web Layer (REST)                        │
│  ┌──────────────────┐    ┌──────────────────────────────┐  │
│  │  AuthController  │    │ RestExceptionHandler         │  │
│  │  /api/auth/*     │    │ @RestControllerAdvice        │  │
│  └────────┬─────────┘    └──────────────────────────────┘  │
└───────────┼──────────────────────────────────────────────────┘
            │
┌───────────▼──────────────────────────────────────────────────┐
│              Application Layer (Services)                     │
│  ┌─────────────────────────────────────────────────────┐    │
│  │  UserApplicationService                              │    │
│  │  - register(RegisterUserCommand)                     │    │
│  │  - login(LoginCommand)                               │    │
│  │  - getCurrentUser(String userId)                     │    │
│  └─────────────────────────────────────────────────────┘    │
└───────────────────────────────────────────────────────────────┘
```

---

## 1. Web Layer Components to Implement

### 1.1 REST Controller

**File: `web/src/main/java/org/art/vertex/web/controller/AuthController.java`**

**Responsibilities:**
- Handle HTTP requests/responses
- Validate request payloads using Bean Validation
- Extract authenticated user context from Spring Security
- Delegate business logic to UserApplicationService
- Map application responses to HTTP responses

**Endpoints:**

| Method | Path              | Description                  | Authentication |
|--------|-------------------|------------------------------|----------------|
| POST   | /api/auth/register| Register new user            | No             |
| POST   | /api/auth/login   | Login existing user          | No             |
| GET    | /api/auth/me      | Get current user profile     | Yes (JWT)      |

**Implementation Pattern:**
```java
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserApplicationService userApplicationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
        @Valid @RequestBody RegisterUserCommand command
    ) {
        log.trace("Processing registration request. Email: {}", command.email());
        AuthenticationResponse response = userApplicationService.register(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
        @Valid @RequestBody LoginCommand command
    ) {
        log.trace("Processing login request. Email: {}", command.email());
        AuthenticationResponse response = userApplicationService.login(command);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(
        @AuthenticationPrincipal String userId
    ) {
        log.trace("Fetching current user. User id: {}", userId);
        UserDto user = userApplicationService.getCurrentUser(userId);
        return ResponseEntity.ok(user);
    }
}
```

**Key Design Decisions:**
- Use `@RestController` instead of `@Controller` for automatic JSON serialization
- Use `@Valid` for automatic Bean Validation
- Use `@AuthenticationPrincipal` to extract authenticated user from SecurityContext
- Return `ResponseEntity<T>` for explicit HTTP status control
- Use constructor injection via `@RequiredArgsConstructor`
- Log at TRACE level for request processing (as per rules)

---

### 1.2 Global Exception Handler

**File: `web/src/main/java/org/art/vertex/web/exception/RestExceptionHandler.java`**

**Responsibilities:**
- Centralize exception handling for all controllers
- Map domain exceptions to HTTP status codes
- Provide consistent error response format
- Log exceptions appropriately

**Exception Mapping:**

| Domain Exception              | HTTP Status | Description                    |
|-------------------------------|-------------|--------------------------------|
| UserNotFoundException         | 404         | User not found by ID           |
| DuplicateEmailException       | 409         | Email already registered       |
| InvalidCredentialsException   | 401         | Invalid email/password         |
| InvalidTokenException         | 401         | Invalid/expired JWT token      |
| MethodArgumentNotValidException | 400       | Bean validation failed         |
| Exception (catch-all)         | 500         | Internal server error          |

**Implementation Pattern:**
```java
@Slf4j
@RestControllerAdvice(basePackages = "org.art.vertex.web.controller")
public class RestExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFound(UserNotFoundException e, HttpServletRequest request) {
        log.warn("User not found. Message: {}", e.getMessage());
        return ErrorResponse.builder()
            .message(e.getMessage())
            .status(HttpStatus.NOT_FOUND.value())
            .timestamp(LocalDateTime.now())
            .path(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateEmail(DuplicateEmailException e, HttpServletRequest request) {
        log.warn("Duplicate email. Message: {}", e.getMessage());
        return ErrorResponse.builder()
            .message(e.getMessage())
            .status(HttpStatus.CONFLICT.value())
            .timestamp(LocalDateTime.now())
            .path(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidCredentials(InvalidCredentialsException e, HttpServletRequest request) {
        log.warn("Invalid credentials. Message: {}", e.getMessage());
        return ErrorResponse.builder()
            .message("Invalid email or password")
            .status(HttpStatus.UNAUTHORIZED.value())
            .timestamp(LocalDateTime.now())
            .path(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidToken(InvalidTokenException e, HttpServletRequest request) {
        log.warn("Invalid token. Message: {}", e.getMessage());
        return ErrorResponse.builder()
            .message("Invalid or expired token")
            .status(HttpStatus.UNAUTHORIZED.value())
            .timestamp(LocalDateTime.now())
            .path(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        var errors = e.getBindingResult().getFieldErrors().stream()
            .map(error -> "Field '%s': %s".formatted(error.getField(), error.getDefaultMessage()))
            .toList();

        log.warn("Validation failed. Errors: {}", errors);

        return ErrorResponse.builder()
            .message("Validation failed: " + String.join(", ", errors))
            .status(HttpStatus.BAD_REQUEST.value())
            .timestamp(LocalDateTime.now())
            .path(request.getRequestURI())
            .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGeneric(Exception e, HttpServletRequest request) {
        log.error("Unexpected error occurred. Path: {}", request.getRequestURI(), e);
        return ErrorResponse.builder()
            .message("An unexpected error occurred")
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .timestamp(LocalDateTime.now())
            .path(request.getRequestURI())
            .build();
    }
}
```

**Key Design Decisions:**
- Use `@RestControllerAdvice` with `basePackages` for scoped exception handling
- Log at WARN level for expected exceptions (4xx), ERROR for unexpected (5xx)
- Don't expose internal error details to clients for security
- Include request path in error response for debugging
- Use structured logging with context

---

### 1.3 Error Response DTO

**File: `web/src/main/java/org/art/vertex/web/model/ErrorResponse.java`**

**Purpose:** Consistent error response format across all endpoints

**Implementation:**
```java
@Builder
public record ErrorResponse(
    String message,
    int status,
    LocalDateTime timestamp,
    String path
) {
}
```

**Example Error Response:**
```json
{
  "message": "Validation failed: Field 'email': Email must be valid",
  "status": 400,
  "timestamp": "2025-10-05T14:30:00",
  "path": "/api/auth/register"
}
```

---

## 2. Package Structure

```
web/src/main/java/org/art/vertex/web/
├── controller/
│   └── AuthController.java
├── exception/
│   └── RestExceptionHandler.java
└── model/
    └── ErrorResponse.java
```

**Rationale:**
- Follows functional package organization (as per `.rules/java/120-rules-design.md`)
- Separates concerns: controllers, exception handling, models
- Makes codebase easy to navigate
- Follows Spring Boot conventions

---

## 3. Dependencies

**File: `web/pom.xml`**

Currently has:
```xml
<dependencies>
    <dependency>
        <groupId>org.art.vertex</groupId>
        <artifactId>domain</artifactId>
    </dependency>
    <dependency>
        <groupId>org.art.vertex</groupId>
        <artifactId>application</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>
```

**Additional needed:**
```xml
<!-- Validation API -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Spring Security (for @AuthenticationPrincipal) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

---

## 4. Bean Configuration

**File: `web/src/main/java/org/art/vertex/web/config/WebConfig.java`**

**Purpose:** Configure web layer beans and CORS

```java
@Configuration(proxyBeanMethods = false)
public class WebConfig {

    @Bean
    public AuthController authController(UserApplicationService userApplicationService) {
        return new AuthController(userApplicationService);
    }

    @Bean
    public RestExceptionHandler restExceptionHandler() {
        return new RestExceptionHandler();
    }
}
```

**CORS Configuration (if needed for frontend):**
```java
@Configuration(proxyBeanMethods = false)
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000", "http://localhost:5173")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

---

## 5. Integration with Spring Security

The `/api/auth/me` endpoint requires authentication. Spring Security configuration (from infrastructure module) should:

1. **Permit public endpoints:**
   - `/api/auth/register`
   - `/api/auth/login`

2. **Secure authenticated endpoints:**
   - `/api/auth/me`
   - All other `/api/**` endpoints

3. **JWT Authentication Filter:**
   - Extract JWT from `Authorization: Bearer <token>` header
   - Validate token using `JwtTokenProvider`
   - Set `SecurityContextHolder` with user ID as principal

**Example Security Config (infrastructure module):**
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
            .requestMatchers("/api/**").authenticated()
            .anyRequest().permitAll()
        )
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}
```

---

## 6. Testing Strategy

### 6.1 Unit Tests

**File: `web/src/test/java/org/art/vertex/web/controller/AuthControllerTest.java`**

**Test Cases:**
- `register_WithValidData_ReturnsCreated()`
- `register_WithInvalidEmail_ReturnsBadRequest()`
- `register_WithDuplicateEmail_ReturnsConflict()`
- `login_WithValidCredentials_ReturnsOk()`
- `login_WithInvalidCredentials_ReturnsUnauthorized()`
- `getCurrentUser_WithValidToken_ReturnsUser()`
- `getCurrentUser_WithoutToken_ReturnsUnauthorized()`

**Testing Approach:**
- Use `@WebMvcTest(AuthController.class)` for controller testing
- Mock `UserApplicationService` with `@MockBean`
- Use `MockMvc` to perform HTTP requests
- Verify HTTP status codes and response bodies

**Example Test:**
```java
@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserApplicationService userApplicationService;

    @Test
    void register_WithValidData_ReturnsCreated() throws Exception {
        // Given
        RegisterUserCommand command = RegisterUserCommand.builder()
            .email("test@example.com")
            .password("password123")
            .build();

        AuthenticationResponse response = AuthenticationResponse.builder()
            .accessToken("jwt-token")
            .tokenType("Bearer")
            .user(UserDto.builder()
                .id("user-id")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .build())
            .build();

        when(userApplicationService.register(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").value("jwt-token"))
            .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }
}
```

### 6.2 Integration Tests

**File: `integration-test/src/test/java/org/art/vertex/test/user/AuthControllerIntegrationTest.java`**

**Test Cases:**
- Full registration flow with database
- Full login flow with JWT validation
- Protected endpoint access with valid/invalid tokens
- Error responses for various scenarios

**Testing Approach:**
- Use `@SpringBootTest(webEnvironment = RANDOM_PORT)`
- Use `TestRestTemplate` for HTTP requests
- Test against real database (TestContainers PostgreSQL)
- Verify end-to-end flows

---

## 7. API Documentation (OpenAPI/Swagger)

**Optional but recommended:**

Add Swagger UI for API documentation:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.5.0</version>
</dependency>
```

**Annotations:**
```java
@Operation(summary = "Register new user", description = "Creates a new user account")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "User registered successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request"),
    @ApiResponse(responseCode = "409", description = "Email already exists")
})
@PostMapping("/register")
public ResponseEntity<AuthenticationResponse> register(@Valid @RequestBody RegisterUserCommand command) {
    // ...
}
```

Access Swagger UI at: `http://localhost:8080/swagger-ui.html`

---

## 8. Implementation Order

### Phase 1: Core REST Layer
1. ✅ Add validation dependency to `web/pom.xml`
2. ✅ Create `ErrorResponse` DTO
3. ✅ Create `RestExceptionHandler` with all exception mappings
4. ✅ Create `AuthController` with all endpoints
5. ✅ Create `WebConfig` for bean declarations
6. ✅ Verify Spring Security permits public endpoints

### Phase 2: CORS Configuration (if needed)
7. ✅ Create `CorsConfig` for frontend integration
8. ✅ Test CORS headers with Postman/curl

### Phase 3: Testing
9. ✅ Write unit tests for `AuthController`
10. ✅ Write unit tests for `RestExceptionHandler`
11. ✅ Write integration tests for auth flows
12. ✅ Run all tests: `mvn test`

### Phase 4: Verification
13. ✅ Build project: `mvn clean compile`
14. ✅ Start application: `mvn spring-boot:run -pl bundle`
15. ✅ Test registration endpoint
16. ✅ Test login endpoint
17. ✅ Test protected endpoint with JWT
18. ✅ Verify error responses

---

## 9. Testing Endpoints (Manual)

### 9.1 Register New User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**Expected Response (201 Created):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "createdAt": "2025-10-05T14:30:00"
  }
}
```

### 9.2 Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**Expected Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "user": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "user@example.com",
    "createdAt": "2025-10-05T14:30:00"
  }
}
```

### 9.3 Get Current User (Authenticated)
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

**Expected Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "email": "user@example.com",
  "createdAt": "2025-10-05T14:30:00"
}
```

### 9.4 Error Response Examples

**Validation Error (400):**
```json
{
  "message": "Validation failed: Field 'email': Email must be valid",
  "status": 400,
  "timestamp": "2025-10-05T14:30:00",
  "path": "/api/auth/register"
}
```

**Duplicate Email (409):**
```json
{
  "message": "User with email user@example.com already exists",
  "status": 409,
  "timestamp": "2025-10-05T14:30:00",
  "path": "/api/auth/register"
}
```

**Invalid Credentials (401):**
```json
{
  "message": "Invalid email or password",
  "status": 401,
  "timestamp": "2025-10-05T14:30:00",
  "path": "/api/auth/login"
}
```

**Unauthorized Access (401):**
```json
{
  "message": "Invalid or expired token",
  "status": 401,
  "timestamp": "2025-10-05T14:30:00",
  "path": "/api/auth/me"
}
```

---

## 10. Key Design Decisions

### 10.1 Controller Pattern
- **REST Controller with delegation:** Controllers handle HTTP concerns only
- **Bean Validation:** Use `@Valid` for automatic validation
- **Explicit HTTP status:** Use `ResponseEntity<T>` for control
- **Constructor injection:** Use `@RequiredArgsConstructor` for clean DI

### 10.2 Exception Handling
- **Centralized with @RestControllerAdvice:** Single point for all error handling
- **Domain exceptions → HTTP status:** Map domain concepts to HTTP semantics
- **Consistent error format:** Use `ErrorResponse` DTO
- **Security-aware:** Don't expose sensitive details in error messages

### 10.3 Security Integration
- **JWT-based authentication:** Stateless, scalable approach
- **Public endpoints:** Registration and login don't require authentication
- **Protected endpoints:** Use `@AuthenticationPrincipal` to extract user
- **SecurityContext integration:** JWT filter sets Spring Security context

### 10.4 Testing Strategy
- **Unit tests:** Test controllers in isolation with MockMvc
- **Integration tests:** Test full stack with real database
- **Manual testing:** Provide curl examples for verification

### 10.5 Code Organization
- **Package by feature:** Separate controller, exception, model packages
- **Bean configuration:** Explicit `@Bean` declarations in `@Configuration` class
- **No component scanning:** Follow explicit bean configuration pattern
- **Immutable DTOs:** Use Java records with `@Builder`

---

## 11. Success Criteria

✅ **Functional Requirements:**
- User can register with email and password via REST API
- User can login with credentials and receive JWT token
- User can access protected endpoint with valid JWT token
- Invalid requests return appropriate error responses with correct status codes
- Validation errors return detailed field-level messages

✅ **Non-Functional Requirements:**
- All endpoints respond within acceptable latency (<500ms)
- Error messages don't expose sensitive information
- Code follows Spring Boot and DDD best practices
- All tests pass (unit + integration)
- Application starts without errors

✅ **Code Quality:**
- Follows loaded development rules (common, design, Spring, controller)
- Uses constructor injection via `@RequiredArgsConstructor`
- Implements structured logging with context
- Uses immutable DTOs (Java records)
- Centralizes exception handling
- Explicit bean configuration

---

## 12. Next Steps After Completion

After completing the REST layer for User operations:

1. **Phase 2: Note CRUD Operations**
   - Implement Note domain model
   - Create NoteApplicationService
   - Build REST API for notes

2. **Phase 3: Search & AI Features**
   - Implement hybrid search (full-text + semantic)
   - Add AI-powered suggestions
   - Build graph visualization endpoints

3. **Frontend Integration**
   - PWA development
   - Connect to REST API
   - Implement JWT token management

---

## 13. Compliance with Loaded Rules

This plan follows all loaded development rules:

✅ **Common Rules (common.md, 000-rules-mandatory.md, 110-rules-common.md):**
- Constructor injection with `@RequiredArgsConstructor`
- Immutable DTOs using Java records with `@Builder`
- Structured logging with SLF4J and context
- Custom domain-specific exceptions
- No unused methods, imports, or variables
- Organized imports (Java → Third-party → Application)

✅ **Design Rules (120-rules-design.md):**
- Package organization by functional concerns
- Separate DTOs from domain models (ErrorResponse is web-specific)
- Clear separation of responsibilities (controller → service → repository)

✅ **Spring Rules (130-rules-spring.md):**
- Constructor injection pattern
- Configuration classes with explicit `@Bean` methods
- `@Configuration(proxyBeanMethods = false)` for performance
- Centralized exception handling with `@RestControllerAdvice`

✅ **Controller Rules (131-rules-spring-controller-open-api.md):**
- Delegate pattern (controller → service)
- Package organization (controller, exception, model)
- Centralized exception handling
- Bean configuration for delegates

---

**This plan provides a complete roadmap for implementing the REST layer for User operations following all architectural principles and development rules.**
