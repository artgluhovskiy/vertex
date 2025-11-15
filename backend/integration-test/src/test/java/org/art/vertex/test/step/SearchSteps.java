package org.art.vertex.test.step;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.art.vertex.domain.note.search.model.SearchType;
import org.art.vertex.web.note.search.dto.SearchResultDto;
import org.art.vertex.web.note.search.request.SearchRequest;

import static io.restassured.RestAssured.given;

public class SearchSteps {

    public SearchResultDto search(String accessToken, String query) {
        return search(accessToken, query, null, null);
    }

    public SearchResultDto search(String accessToken, String query, SearchType type) {
        return search(accessToken, query, type, null);
    }

    public SearchResultDto search(String accessToken, String query, SearchType type, Integer maxResults) {
        var request = SearchRequest.builder()
            .query(query)
            .type(type)
            .maxResults(maxResults)
            .build();

        return given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/notes/search")
            .then()
            .statusCode(200)
            .extract()
            .as(SearchResultDto.class);
    }

    public ValidatableResponse searchAndGetResponse(String accessToken, String query) {
        return searchAndGetResponse(accessToken, query, null, null);
    }

    public ValidatableResponse searchAndGetResponse(String accessToken, String query, SearchType type, Integer maxResults) {
        var request = SearchRequest.builder()
            .query(query)
            .type(type)
            .maxResults(maxResults)
            .build();

        if (accessToken != null) {
            return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/notes/search")
                .then();
        } else {
            return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/notes/search")
                .then();
        }
    }
}
