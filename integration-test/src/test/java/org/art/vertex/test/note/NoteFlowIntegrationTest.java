package org.art.vertex.test.note;

import org.art.vertex.test.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class NoteFlowIntegrationTest extends BaseIntegrationTest {

    // ========== HAPPY PATH TESTS ==========

    @Test
    void shouldCreateNoteSuccessfully() {
        // GIVEN
        var auth = userSteps.register("user@example.com", "password123");
        var token = auth.accessToken();
        var userId = auth.user().id();

        // GIVEN - User creates a directory
        var dir = dirSteps.createRootDirectory(token, "My Notes");
        var dirId = dir.id();

        // WHEN
        var note = noteSteps.createNote(token, "My First Note", "This is the content of my note", dirId);

        // THEN
        assertThat(note).isNotNull();
        assertThat(note.id()).isNotNull();
        assertThat(note.dirId()).isEqualTo(dirId);
        assertThat(note.title()).isEqualTo("My First Note");
        assertThat(note.content()).isEqualTo("This is the content of my note");
        assertThat(note.userId()).isEqualTo(UUID.fromString(userId));
        assertThat(note.createdAt()).isNotNull();
        assertThat(note.updatedAt()).isNotNull();
    }

    @Test
    void shouldGetNoteById() {
        // GIVEN
        var auth = userSteps.register("user@example.com", "password123");
        var token = auth.accessToken();
        var userId = auth.user().id();

        var dir = dirSteps.createRootDirectory(token, "My Notes");
        var dirId = dir.id();

        var createdNote = noteSteps.createNote(token, "Note Title", "Note Content", dirId);

        // WHEN
        var retrievedNote = noteSteps.getNote(token, createdNote.id());

        // THEN
        assertThat(retrievedNote).isNotNull();
        assertThat(retrievedNote.id()).isEqualTo(createdNote.id());
        assertThat(retrievedNote.userId()).isEqualTo(UUID.fromString(userId));
        assertThat(retrievedNote.dirId()).isEqualTo(dirId);
        assertThat(retrievedNote.title()).isEqualTo("Note Title");
        assertThat(retrievedNote.content()).isEqualTo("Note Content");
        assertThat(retrievedNote.createdAt()).isNotNull();
        assertThat(retrievedNote.updatedAt()).isNotNull();
    }

    @Test
    void shouldUpdateNoteTitle() {
        // GIVEN
        var auth = userSteps.register("user@example.com", "password123");
        var token = auth.accessToken();
        var userId = auth.user().id();

        var dir = dirSteps.createRootDirectory(token, "My Notes");
        var dirId = dir.id();
        var createdNote = noteSteps.createNote(token, "Original Title", "Original Content", dirId);

        // WHEN
        var updatedNote = noteSteps.updateNote(token, createdNote.id(), "Updated Title", "Original Content", dirId);

        // THEN
        assertThat(updatedNote).isNotNull();
        assertThat(updatedNote.id()).isEqualTo(createdNote.id());
        assertThat(updatedNote.userId()).isEqualTo(UUID.fromString(userId));
        assertThat(updatedNote.dirId()).isEqualTo(dirId);
        assertThat(updatedNote.title()).isEqualTo("Updated Title");
        assertThat(updatedNote.content()).isEqualTo("Original Content");
        assertThat(updatedNote.updatedAt()).isAfter(createdNote.updatedAt());
    }

    @Test
    void shouldUpdateNoteContent() {
        // GIVEN - User creates a directory and a note
        var auth = userSteps.register("user@example.com", "password123");
        var token = auth.accessToken();
        var userId = auth.user().id();

        var dir = dirSteps.createRootDirectory(token, "My Notes");
        var dirId = dir.id();

        var createdNote = noteSteps.createNote(token, "Title", "Original Content", dirId);

        // WHEN
        var updatedNote = noteSteps.updateNote(token, createdNote.id(), "Title", "Updated Content", dirId);

        // THEN
        assertThat(updatedNote).isNotNull();
        assertThat(updatedNote.id()).isEqualTo(createdNote.id());
        assertThat(updatedNote.userId()).isEqualTo(UUID.fromString(userId));
        assertThat(updatedNote.dirId()).isEqualTo(dirId);
        assertThat(updatedNote.title()).isEqualTo("Title");
        assertThat(updatedNote.content()).isEqualTo("Updated Content");
        assertThat(updatedNote.updatedAt()).isAfter(createdNote.updatedAt());
    }

    @Test
    void shouldUpdateNoteTitleAndContent() {
        // GIVEN
        var auth = userSteps.register("user@example.com", "password123");
        var token = auth.accessToken();
        var userId = auth.user().id();

        var dir = dirSteps.createRootDirectory(token, "My Notes");
        var dirId = dir.id();

        var createdNote = noteSteps.createNote(token, "Original Title", "Original Content", dirId);

        // WHEN
        var updatedNote = noteSteps.updateNote(
            token,
            createdNote.id(),
            "New Title",
            "New Content",
            dirId
        );

        // THEN
        assertThat(updatedNote).isNotNull();
        assertThat(updatedNote.id()).isEqualTo(createdNote.id());
        assertThat(updatedNote.userId()).isEqualTo(UUID.fromString(userId));
        assertThat(updatedNote.dirId()).isEqualTo(dirId);
        assertThat(updatedNote.title()).isEqualTo("New Title");
        assertThat(updatedNote.content()).isEqualTo("New Content");
        assertThat(updatedNote.updatedAt()).isAfter(createdNote.updatedAt());
    }

    @Test
    void shouldDeleteNote() {
        // GIVEN
        var auth = userSteps.register("user@example.com", "password123");
        var token = auth.accessToken();

        var dir = dirSteps.createRootDirectory(token, "My Notes");

        var createdNote = noteSteps.createNote(token, "Note to Delete", "Content", dir.id());

        // WHEN
        noteSteps.deleteNote(token, createdNote.id());

        // THEN
        noteSteps.getNoteAndGetResponse(token, createdNote.id())
            .statusCode(404);
    }

    @Test
    void shouldGetAllUserNotes() {
        // GIVEN
        var auth = userSteps.register("user@example.com", "password123");
        var token = auth.accessToken();

        var dir = dirSteps.createRootDirectory(token, "My Notes");
        var dirId = dir.id();

        noteSteps.createNote(token, "Note 1", "Content 1", dirId);
        noteSteps.createNote(token, "Note 2", "Content 2", dirId);
        noteSteps.createNote(token, "Note 3", "Content 3", dirId);

        // WHEN
        var notes = noteSteps.getUserNotes(token);

        // THEN
        assertThat(notes).hasSize(3);
        assertThat(notes).extracting("title")
            .containsExactlyInAnyOrder("Note 1", "Note 2", "Note 3");
    }

    @Test
    void shouldIsolateUserNotes() {
        // GIVEN
        var auth1 = userSteps.register("user1@example.com", "password123");
        var token1 = auth1.accessToken();
        var userId1 = auth1.user().id();

        var auth2 = userSteps.register("user2@example.com", "password123");
        var token2 = auth2.accessToken();
        var userId2 = auth2.user().id();

        var dir1 = dirSteps.createRootDirectory(token1, "User 1 Notes");
        var dir2 = dirSteps.createRootDirectory(token2, "User 2 Notes");

        noteSteps.createNote(token1, "User 1 Note", "Content 1", dir1.id());
        var note2 = noteSteps.createNote(token2, "User 2 Note", "Content 2", dir2.id());

        // WHEN
        var user1Notes = noteSteps.getUserNotes(token1);
        var user2Notes = noteSteps.getUserNotes(token2);

        // THEN
        assertThat(user1Notes).hasSize(1);
        assertThat(user1Notes.getFirst().userId()).isEqualTo(UUID.fromString(userId1));
        assertThat(user1Notes.getFirst().title()).isEqualTo("User 1 Note");
        assertThat(user1Notes.getFirst().content()).isEqualTo("Content 1");

        assertThat(user2Notes).hasSize(1);
        assertThat(user2Notes.getFirst().userId()).isEqualTo(UUID.fromString(userId2));
        assertThat(user2Notes.getFirst().title()).isEqualTo("User 2 Note");
        assertThat(user2Notes.getFirst().content()).isEqualTo("Content 2");

        // THEN - User 1 cannot access User 2's note
        // Note: Depending on security implementation, this could be 403 or 404
        // For now we expect it to fail (not 200)
        noteSteps.getNoteAndGetResponse(token1, note2.id())
            .statusCode(org.hamcrest.Matchers.not(200));
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoNotes() {
        // GIVEN
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        // WHEN
        var notes = noteSteps.getUserNotes(token);

        // THEN
        assertThat(notes).isEmpty();
    }

    // ========== ERROR SCENARIO TESTS ==========

    @Test
    void shouldFailCreateNoteWithoutAuthentication() {
        // GIVEN
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        var dir = dirSteps.createRootDirectory(token, "My Notes");

        // WHEN - Attempt to create note without token
        // THEN - Request is rejected with 401 Unauthorized
        noteSteps.createNoteAndGetResponse(null, "Title", "Content", dir.id())
            .statusCode(401);
    }

    @Test
    void shouldFailCreateNoteWithBlankTitle() {
        // GIVEN
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        var dir = dirSteps.createRootDirectory(token, "My Notes");

        // WHEN - Attempt to create note with blank title
        // THEN - Validation error 400
        noteSteps.createNoteAndGetResponse(token, "", "Content", dir.id())
            .statusCode(400)
            .body("status", equalTo(400))
            .body("path", equalTo("/api/v1/notes"));
    }

    @Test
    void shouldFailCreateNoteWithNullTitle() {
        // GIVEN
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        var dir = dirSteps.createRootDirectory(token, "My Notes");

        // WHEN - Attempt to create note with null title
        // THEN - Validation error 400
        noteSteps.createNoteAndGetResponse(token, null, "Content", dir.id())
            .statusCode(400)
            .body("status", equalTo(400))
            .body("path", equalTo("/api/v1/notes"));
    }

    @Test
    void shouldFailCreateNoteWithTooLongTitle() {
        // GIVEN
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        var dir = dirSteps.createRootDirectory(token, "My Notes");

        // WHEN
        String longTitle = "a".repeat(256);

        // THEN
        noteSteps.createNoteAndGetResponse(token, longTitle, "Content", dir.id())
            .statusCode(400)
            .body("status", equalTo(400))
            .body("path", equalTo("/api/v1/notes"));
    }

    @Test
    void shouldFailGetNonExistentNote() {
        // GIVEN
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        // WHEN
        UUID nonExistentId = UUID.randomUUID();

        // THEN - 404 Not Found
        noteSteps.getNoteAndGetResponse(token, nonExistentId)
            .statusCode(404)
            .body("status", equalTo(404))
            .body("message", notNullValue())
            .body("path", equalTo("/api/v1/notes/" + nonExistentId));
    }

    @Test
    void shouldFailUpdateNonExistentNote() {
        // GIVEN
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        var dir = dirSteps.createRootDirectory(token, "My Notes");

        // WHEN
        UUID nonExistentId = UUID.randomUUID();

        // THEN - 404 Not Found
        noteSteps.updateNoteAndGetResponse(token, nonExistentId, "New Title", "New Content", dir.id())
            .statusCode(404)
            .body("status", equalTo(404))
            .body("path", equalTo("/api/v1/notes/" + nonExistentId));
    }

    @Test
    void shouldFailDeleteNonExistentNote() {
        // GIVEN
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        // WHEN
        UUID nonExistentId = UUID.randomUUID();

        // THEN - 404 Not Found
        noteSteps.deleteNoteAndGetResponse(token, nonExistentId)
            .statusCode(404)
            .body("status", equalTo(404))
            .body("path", equalTo("/api/v1/notes/" + nonExistentId));
    }

    @Test
    void shouldFailGetNoteWithoutAuthentication() {
        // GIVEN
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        var dir = dirSteps.createRootDirectory(token, "My Notes");

        var note = noteSteps.createNote(token, "Title", "Content", dir.id());

        // WHEN - Attempt to get note without authentication
        // THEN - 401 Unauthorized
        noteSteps.getNoteAndGetResponse(null, note.id())
            .statusCode(401);
    }

    @Test
    void shouldFailUpdateNoteWithoutAuthentication() {
        // GIVEN
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        var dir = dirSteps.createRootDirectory(token, "My Notes");
        var dirId = dir.id();

        var note = noteSteps.createNote(token, "Title", "Content", dirId);

        // WHEN - Attempt to update note without authentication
        // THEN - 401 Unauthorized
        noteSteps.updateNoteAndGetResponse(null, note.id(), "New Title", "New Content", dirId)
            .statusCode(401);
    }

    @Test
    void shouldFailDeleteNoteWithoutAuthentication() {
        // GIVEN
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        var dir = dirSteps.createRootDirectory(token, "My Notes");

        var note = noteSteps.createNote(token, "Title", "Content", dir.id());

        // WHEN - Attempt to delete note without authentication
        // THEN - 401 Unauthorized
        noteSteps.deleteNoteAndGetResponse(null, note.id())
            .statusCode(401);
    }

    @Test
    void shouldFailGetUserNotesWithoutAuthentication() {
        // WHEN - Attempt to get user notes without authentication
        // THEN - 401 Unauthorized
        noteSteps.getUserNotesAndGetResponse(null)
            .statusCode(401);
    }
}
