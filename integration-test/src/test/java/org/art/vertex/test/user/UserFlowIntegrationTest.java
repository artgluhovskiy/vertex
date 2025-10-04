package org.art.vertex.test.user;

import org.art.vertex.test.BaseIntegrationTest;
import org.art.vertex.application.user.UserApplicationService;
import org.art.vertex.application.user.command.LoginCommand;
import org.art.vertex.application.user.command.RegisterUserCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UserFlowIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserApplicationService userApplicationService;

    @Test
    void shouldLoginWithValidCredentials() {
        // GIVEN
        var registerCommand = RegisterUserCommand.builder()
            .email("login@example.com")
            .password("correctPassword")
            .build();
        userApplicationService.register(registerCommand);

        // WHEN
        var loginCommand = LoginCommand.builder()
            .email("login@example.com")
            .password("correctPassword")
            .build();
        var response = userApplicationService.login(loginCommand);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.accessToken()).isNotNull();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.user()).isNotNull();
        assertThat(response.user().email()).isEqualTo("login@example.com");
    }
}
