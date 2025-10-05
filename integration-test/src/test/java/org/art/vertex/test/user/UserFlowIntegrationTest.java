package org.art.vertex.test.user;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.art.vertex.test.BaseIntegrationTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class UserFlowIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api/v1/auth";
    }

    @Test
    void shouldRegisterNewUser() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "newuser@example.com",
                    "password": "password123"
                }
                """)
        .when()
            .post("/register")
        .then()
            .statusCode(201)
            .body("accessToken", notNullValue())
            .body("tokenType", equalTo("Bearer"))
            .body("user.email", equalTo("newuser@example.com"))
            .body("user.id", notNullValue())
            .body("user.createdAt", notNullValue());
    }

    @Test
    void shouldLoginWithValidCredentials() {
        // GIVEN - Register user
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "login@example.com",
                    "password": "correctPassword"
                }
                """)
        .when()
            .post("/register")
        .then()
            .statusCode(201);

        // WHEN & THEN - Login with valid credentials
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "login@example.com",
                    "password": "correctPassword"
                }
                """)
        .when()
            .post("/login")
        .then()
            .statusCode(200)
            .body("accessToken", notNullValue())
            .body("tokenType", equalTo("Bearer"))
            .body("user.email", equalTo("login@example.com"))
            .body("user.id", notNullValue());
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() {
        // GIVEN - Register user
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "user@example.com",
                    "password": "correctPassword"
                }
                """)
        .when()
            .post("/register")
        .then()
            .statusCode(201);

        // WHEN & THEN - Login with wrong password
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "user@example.com",
                    "password": "wrongPassword"
                }
                """)
        .when()
            .post("/login")
        .then()
            .statusCode(401)
            .body("message", equalTo("Invalid email or password"))
            .body("status", equalTo(401))
            .body("path", equalTo("/api/v1/auth/login"));
    }

    @Test
    void shouldFailRegisterWithDuplicateEmail() {
        // GIVEN - Register first user
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "duplicate@example.com",
                    "password": "password123"
                }
                """)
        .when()
            .post("/register")
        .then()
            .statusCode(201);

        // WHEN & THEN - Try to register with same email
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "duplicate@example.com",
                    "password": "password456"
                }
                """)
        .when()
            .post("/register")
        .then()
            .statusCode(409)
            .body("message", equalTo("User with this email already exists. Email: duplicate@example.com"))
            .body("status", equalTo(409))
            .body("path", equalTo("/api/v1/auth/register"));
    }

    @Test
    void shouldFailRegisterWithInvalidEmail() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "invalid-email",
                    "password": "password123"
                }
                """)
        .when()
            .post("/register")
        .then()
            .statusCode(400)
            .body("status", equalTo(400))
            .body("path", equalTo("/api/v1/auth/register"));
    }

    @Test
    void shouldFailRegisterWithShortPassword() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "valid@example.com",
                    "password": "short"
                }
                """)
        .when()
            .post("/register")
        .then()
            .statusCode(400)
            .body("status", equalTo(400))
            .body("path", equalTo("/api/v1/auth/register"));
    }

    @Test
    void shouldGetCurrentUserWithValidToken() {
        // GIVEN - Register and get token
        String token = given()
            .contentType(ContentType.JSON)
            .body("""
                {
                    "email": "currentuser@example.com",
                    "password": "password123"
                }
                """)
        .when()
            .post("/register")
        .then()
            .statusCode(201)
            .extract()
            .path("accessToken");

        // WHEN & THEN - Get current user with token
        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .get("/me")
        .then()
            .statusCode(200)
            .body("email", equalTo("currentuser@example.com"))
            .body("id", notNullValue())
            .body("createdAt", notNullValue());
    }

    @Test
    void shouldFailGetCurrentUserWithoutToken() {
        given()
        .when()
            .get("/me")
        .then()
            .statusCode(401);
    }
}
