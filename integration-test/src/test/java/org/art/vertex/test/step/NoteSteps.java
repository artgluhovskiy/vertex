package org.art.vertex.test.step;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.art.vertex.web.note.dto.NoteDto;
import org.art.vertex.web.note.request.CreateNoteRequest;
import org.art.vertex.web.note.request.UpdateNoteRequest;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;

public class NoteSteps {

    public NoteDto createNote(String accessToken, String title, String content) {
        var request = CreateNoteRequest.builder()
            .title(title)
            .content(content)
            .build();

        return given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/notes")
            .then()
            .statusCode(201)
            .extract()
            .as(NoteDto.class);
    }

    public NoteDto createNote(String accessToken, String title, String content, UUID directoryId) {
        var request = CreateNoteRequest.builder()
            .title(title)
            .content(content)
            .directoryId(directoryId)
            .build();

        return given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .post("/notes")
            .then()
            .statusCode(201)
            .extract()
            .as(NoteDto.class);
    }

    public ValidatableResponse createNoteAndGetResponse(String accessToken, String title, String content) {
        var request = CreateNoteRequest.builder()
            .title(title)
            .content(content)
            .build();

        if (accessToken != null) {
            return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/notes")
                .then();
        } else {
            return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/notes")
                .then();
        }
    }

    public NoteDto getNote(String accessToken, UUID noteId) {
        return given()
            .header("Authorization", "Bearer " + accessToken)
            .when()
            .get("/notes/" + noteId)
            .then()
            .statusCode(200)
            .extract()
            .as(NoteDto.class);
    }

    public ValidatableResponse getNoteAndGetResponse(String accessToken, UUID noteId) {
        if (accessToken != null) {
            return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/notes/" + noteId)
                .then();
        } else {
            return given()
                .when()
                .get("/notes/" + noteId)
                .then();
        }
    }

    public NoteDto updateNote(String accessToken, UUID noteId, String title, String content) {
        var request = UpdateNoteRequest.builder()
            .title(title)
            .content(content)
            .build();

        return given()
            .header("Authorization", "Bearer " + accessToken)
            .contentType(ContentType.JSON)
            .body(request)
            .when()
            .put("/notes/" + noteId)
            .then()
            .statusCode(200)
            .extract()
            .as(NoteDto.class);
    }

    public ValidatableResponse updateNoteAndGetResponse(String accessToken, UUID noteId, String title, String content) {
        var request = UpdateNoteRequest.builder()
            .title(title)
            .content(content)
            .build();

        if (accessToken != null) {
            return given()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/notes/" + noteId)
                .then();
        } else {
            return given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .put("/notes/" + noteId)
                .then();
        }
    }

    public void deleteNote(String accessToken, UUID noteId) {
        given()
            .header("Authorization", "Bearer " + accessToken)
            .when()
            .delete("/notes/" + noteId)
            .then()
            .statusCode(204);
    }

    public ValidatableResponse deleteNoteAndGetResponse(String accessToken, UUID noteId) {
        if (accessToken != null) {
            return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/notes/" + noteId)
                .then();
        } else {
            return given()
                .when()
                .delete("/notes/" + noteId)
                .then();
        }
    }

    public List<NoteDto> getUserNotes(String accessToken) {
        return given()
            .header("Authorization", "Bearer " + accessToken)
            .when()
            .get("/notes")
            .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getList(".", NoteDto.class);
    }

    public ValidatableResponse getUserNotesAndGetResponse(String accessToken) {
        if (accessToken != null) {
            return given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .get("/notes")
                .then();
        } else {
            return given()
                .when()
                .get("/notes")
                .then();
        }
    }
}
