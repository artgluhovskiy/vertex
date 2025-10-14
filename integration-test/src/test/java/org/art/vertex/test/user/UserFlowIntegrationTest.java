package org.art.vertex.test.user;

import org.art.vertex.test.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;

class UserFlowIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldRegisterNewUser() {
        // WHEN
        var response = userSteps.register("newuser@example.com", "password123");

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isNotNull();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.user()).isNotNull();
        assertThat(response.user().email()).isEqualTo("newuser@example.com");
        assertThat(response.user().id()).isNotNull();
        assertThat(response.user().createdAt()).isNotNull();
    }

    @Test
    void shouldLoginWithValidCredentials() {
        // GIVEN
        userSteps.register("login@example.com", "correctPassword");

        // WHEN
        var response = userSteps.login("login@example.com", "correctPassword");

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isNotNull();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.user()).isNotNull();
        assertThat(response.user().email()).isEqualTo("login@example.com");
        assertThat(response.user().id()).isNotNull();
    }

    @Test
    void shouldFailLoginWithInvalidCredentials() {
        // GIVEN
        userSteps.register("user@example.com", "correctPassword");

        // WHEN - Login with wrong password
        // THEN - Verify error response
        userSteps.loginAndGetResponse("user@example.com", "wrongPassword")
            .statusCode(401)
            .body("message", equalTo("Invalid email or password"))
            .body("status", equalTo(401))
            .body("path", equalTo("/api/v1/auth/login"));
    }

    @Test
    void shouldFailRegisterWithDuplicateEmail() {
        // GIVEN
        userSteps.register("duplicate@example.com", "password123");

        // WHEN - Try to register with same email
        // THEN - Verify conflict error
        userSteps.registerAndGetResponse("duplicate@example.com", "password456")
            .statusCode(409)
            .body("message", equalTo("User with this email already exists. Email: duplicate@example.com"))
            .body("status", equalTo(409))
            .body("path", equalTo("/api/v1/auth/register"));
    }

    @Test
    void shouldFailRegisterWithInvalidEmail() {
        userSteps.registerAndGetResponse("invalid-email", "password123")
            .statusCode(400)
            .body("status", equalTo(400))
            .body("path", equalTo("/api/v1/auth/register"));
    }

    @Test
    void shouldFailRegisterWithShortPassword() {
        userSteps.registerAndGetResponse("valid@example.com", "short")
            .statusCode(400)
            .body("status", equalTo(400))
            .body("path", equalTo("/api/v1/auth/register"));
    }

    @Test
    void shouldGetCurrentUserWithValidToken() {
        // GIVEN
        var token = userSteps.registerAndGetToken("currentuser@example.com", "password123");

        // WHEN
        var user = userSteps.getCurrentUser(token);

        // THEN
        assertThat(user).isNotNull();
        assertThat(user.email()).isEqualTo("currentuser@example.com");
        assertThat(user.id()).isNotNull();
        assertThat(user.createdAt()).isNotNull();
    }

    @Test
    void shouldFailGetCurrentUserWithoutToken() {
        userSteps.getCurrentUserResponse(null)
            .statusCode(401);
    }
}
