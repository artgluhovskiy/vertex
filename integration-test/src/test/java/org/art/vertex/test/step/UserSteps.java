package org.art.vertex.test.step;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.art.vertex.web.user.dto.AuthenticationResponse;
import org.art.vertex.web.user.dto.UserDto;
import org.art.vertex.web.user.request.UserLoginRequest;
import org.art.vertex.web.user.request.UserRegistrationRequest;

import static io.restassured.RestAssured.given;

public class UserSteps {

    public AuthenticationResponse register(String email, String password) {
        var request = UserRegistrationRequest.builder()
            .email(email)
            .password(password)
            .build();

        return given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/auth/register")
            .then()
            .statusCode(201)
            .extract()
            .as(AuthenticationResponse.class);
    }

    public ValidatableResponse registerAndGetResponse(String email, String password) {
        var request = UserRegistrationRequest.builder()
            .email(email)
            .password(password)
            .build();

        return given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/auth/register")
            .then();
    }

    public AuthenticationResponse login(String email, String password) {
        var request = UserLoginRequest.builder()
            .email(email)
            .password(password)
            .build();

        return given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/auth/login")
            .then()
            .statusCode(200)
            .extract()
            .as(AuthenticationResponse.class);
    }

    public ValidatableResponse loginAndGetResponse(String email, String password) {
        var request = UserLoginRequest.builder()
            .email(email)
            .password(password)
            .build();

        return given()
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/auth/login")
            .then();
    }

    public UserDto getCurrentUser(String accessToken) {
        return given()
            .header("Authorization", "Bearer " + accessToken)
            .when()
            .get("/auth/me")
            .then()
            .statusCode(200)
            .extract()
            .as(UserDto.class);
    }

    public ValidatableResponse getCurrentUserResponse(String accessToken) {
        if (accessToken != null) {
            return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/auth/me")
                .then();
        } else {
            return given()
                .when()
                .get("/auth/me")
                .then();
        }
    }

    public String registerAndGetToken(String email, String password) {
        return register(email, password).accessToken();
    }
}
