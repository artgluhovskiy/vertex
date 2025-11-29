package org.art.vertex.test.step;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.obsidian.api.dto.MigrationRequest;
import org.art.vertex.obsidian.api.dto.MigrationResultDto;

import static io.restassured.RestAssured.given;

@Slf4j
public class ObsidianMigrationSteps {

    public MigrationResultDto migrateVault(String accessToken, String vaultPath) {
        log.info("Migrating vault with token: {} (length: {})",
            accessToken != null ? accessToken.substring(0, Math.min(20, accessToken.length())) + "..." : "null",
            accessToken != null ? accessToken.length() : 0);

        var request = MigrationRequest.builder()
            .vaultPath(vaultPath)
            .build();

        return given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/migration/obsidian")
            .then()
            .log().all()
            .statusCode(200)
            .extract()
            .as(MigrationResultDto.class);
    }

    public ValidatableResponse migrateVaultAndGetResponse(String accessToken, String vaultPath) {
        var request = MigrationRequest.builder()
            .vaultPath(vaultPath)
            .build();

        return given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/migration/obsidian")
            .then();
    }
}
