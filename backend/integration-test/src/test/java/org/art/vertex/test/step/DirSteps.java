package org.art.vertex.test.step;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.art.vertex.web.directory.request.CreateDirectoryRequest;
import org.art.vertex.web.directory.dto.DirectoryDto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class DirSteps {

    public DirectoryDto createRootDirectory(String accessToken, String name) {
        return createDirectory(accessToken, name, null);
    }

    public DirectoryDto createDirectory(String accessToken, String name, UUID parentId) {
        var request = CreateDirectoryRequest.builder()
            .name(name)
            .parentId(parentId)
            .build();

        return given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/directories")
            .then()
            .statusCode(201)
            .extract()
            .as(DirectoryDto.class);
    }

    public ValidatableResponse createDirectoryAndGetResponse(String accessToken, String name, UUID parentId) {
        Map<String, Object> request = new HashMap<>();
        request.put("name", name);
        if (parentId != null) {
            request.put("parentId", parentId);
        }

        if (accessToken != null) {
            return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/directories")
                .then();
        } else {
            return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/directories")
                .then();
        }
    }

    public DirectoryDto getDirectory(String accessToken, UUID dirId) {
        return given()
            .header("Authorization", "Bearer " + accessToken)
            .when()
            .get("/directories/" + dirId)
            .then()
            .statusCode(200)
            .extract()
            .as(DirectoryDto.class);
    }

    public ValidatableResponse getDirectoryAndGetResponse(String accessToken, UUID dirId) {
        if (accessToken != null) {
            return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/directories/" + dirId)
                .then();
        } else {
            return given()
                .when()
                .get("/directories/" + dirId)
                .then();
        }
    }

    public DirectoryDto renameDirectory(String accessToken, UUID dirId, String newName) {
        Map<String, Object> request = new HashMap<>();
        request.put("name", newName);

        return given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .put("/directories/" + dirId)
            .then()
            .statusCode(200)
            .extract()
            .as(DirectoryDto.class);
    }

    public DirectoryDto moveDirectory(String accessToken, UUID dirId, UUID newParentId) {
        Map<String, Object> request = new HashMap<>();
        request.put("parentId", newParentId);

        return given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .put("/directories/" + dirId)
            .then()
            .statusCode(200)
            .extract()
            .as(DirectoryDto.class);
    }

    public void deleteDirectory(String accessToken, UUID dirId) {
        given()
            .header("Authorization", "Bearer " + accessToken)
            .when()
            .delete("/directories/" + dirId)
            .then()
            .statusCode(204);
    }

    public ValidatableResponse deleteDirectoryAndGetResponse(String accessToken, UUID dirId) {
        if (accessToken != null) {
            return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/directories/" + dirId)
                .then();
        } else {
            return given()
                .when()
                .delete("/directories/" + dirId)
                .then();
        }
    }
}
