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
        // GIVEN - User is registered and authenticated
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        // GIVEN - User creates a directory
        var directory = dirSteps.createRootDirectory(token, "My Notes");

        // WHEN - User creates a note
        var note = noteSteps.createNote(token, "My First Note", "This is the content of my note", directory.getId());

        // THEN - Note is created with correct properties
        assertThat(note).isNotNull();
        assertThat(note.id()).isNotNull();
        assertThat(note.title()).isEqualTo("My First Note");
        assertThat(note.content()).isEqualTo("This is the content of my note");
        assertThat(note.userId()).isNotNull();
        assertThat(note.createdAt()).isNotNull();
        assertThat(note.updatedAt()).isNotNull();
    }

    @Test
    void shouldGetNoteById() {
        // GIVEN - User creates a directory and a note
        var token = userSteps.registerAndGetToken("user@example.com", "password123");
        var directory = dirSteps.createRootDirectory(token, "My Notes");
        var createdNote = noteSteps.createNote(token, "Note Title", "Note Content", directory.getId());

        // WHEN - User retrieves the note by ID
        var retrievedNote = noteSteps.getNote(token, createdNote.id());

        // THEN - Retrieved note matches created note
        assertThat(retrievedNote).isNotNull();
        assertThat(retrievedNote.id()).isEqualTo(createdNote.id());
        assertThat(retrievedNote.title()).isEqualTo("Note Title");
        assertThat(retrievedNote.content()).isEqualTo("Note Content");
    }

    @Test
    void shouldUpdateNoteTitle() {
        // GIVEN - User creates a directory and a note
        var token = userSteps.registerAndGetToken("user@example.com", "password123");
        var directory = dirSteps.createRootDirectory(token, "My Notes");
        var createdNote = noteSteps.createNote(token, "Original Title", "Original Content", directory.getId());

        // WHEN - User updates the note title
        var updatedNote = noteSteps.updateNote(token, createdNote.id(), "Updated Title", "Original Content", directory.getId());

        // THEN - Note title is updated, content remains unchanged
        assertThat(updatedNote).isNotNull();
        assertThat(updatedNote.id()).isEqualTo(createdNote.id());
        assertThat(updatedNote.title()).isEqualTo("Updated Title");
        assertThat(updatedNote.content()).isEqualTo("Original Content");
        assertThat(updatedNote.updatedAt()).isAfter(createdNote.updatedAt());
    }

    @Test
    void shouldUpdateNoteContent() {
        // GIVEN - User creates a directory and a note
        var token = userSteps.registerAndGetToken("user@example.com", "password123");
        var directory = dirSteps.createRootDirectory(token, "My Notes");
        var createdNote = noteSteps.createNote(token, "Title", "Original Content", directory.getId());

        // WHEN - User updates the note content
        var updatedNote = noteSteps.updateNote(token, createdNote.id(), "Title", "Updated Content", directory.getId());

        // THEN - Note content is updated, title remains unchanged
        assertThat(updatedNote).isNotNull();
        assertThat(updatedNote.id()).isEqualTo(createdNote.id());
        assertThat(updatedNote.title()).isEqualTo("Title");
        assertThat(updatedNote.content()).isEqualTo("Updated Content");
    }

    @Test
    void shouldUpdateNoteTitleAndContent() {
        // GIVEN - User creates a directory and a note
        var token = userSteps.registerAndGetToken("user@example.com", "password123");
        var directory = dirSteps.createRootDirectory(token, "My Notes");
        var createdNote = noteSteps.createNote(token, "Original Title", "Original Content", directory.getId());

        // WHEN - User updates both title and content
        var updatedNote = noteSteps.updateNote(
            token,
            createdNote.id(),
            "New Title",
            "New Content",
            directory.getId()
        );

        // THEN - Both title and content are updated
        assertThat(updatedNote).isNotNull();
        assertThat(updatedNote.title()).isEqualTo("New Title");
        assertThat(updatedNote.content()).isEqualTo("New Content");
    }

    @Test
    void shouldDeleteNote() {
        // GIVEN - User creates a directory and a note
        var token = userSteps.registerAndGetToken("user@example.com", "password123");
        var directory = dirSteps.createRootDirectory(token, "My Notes");
        var createdNote = noteSteps.createNote(token, "Note to Delete", "Content", directory.getId());

        // WHEN - User deletes the note
        noteSteps.deleteNote(token, createdNote.id());

        // THEN - Note no longer exists
        noteSteps.getNoteAndGetResponse(token, createdNote.id())
            .statusCode(404);
    }

    @Test
    void shouldGetAllUserNotes() {
        // GIVEN - User creates a directory and multiple notes
        var token = userSteps.registerAndGetToken("user@example.com", "password123");
        var directory = dirSteps.createRootDirectory(token, "My Notes");
        noteSteps.createNote(token, "Note 1", "Content 1", directory.getId());
        noteSteps.createNote(token, "Note 2", "Content 2", directory.getId());
        noteSteps.createNote(token, "Note 3", "Content 3", directory.getId());

        // WHEN - User retrieves all their notes
        var notes = noteSteps.getUserNotes(token);

        // THEN - All notes are returned
        assertThat(notes).hasSize(3);
        assertThat(notes).extracting("title")
            .containsExactlyInAnyOrder("Note 1", "Note 2", "Note 3");
    }

    @Test
    void shouldIsolateUserNotes() {
        // GIVEN - Two users create directories and notes
        var token1 = userSteps.registerAndGetToken("user1@example.com", "password123");
        var token2 = userSteps.registerAndGetToken("user2@example.com", "password123");

        var directory1 = dirSteps.createRootDirectory(token1, "User 1 Notes");
        var directory2 = dirSteps.createRootDirectory(token2, "User 2 Notes");

        var note1 = noteSteps.createNote(token1, "User 1 Note", "Content 1", directory1.getId());
        var note2 = noteSteps.createNote(token2, "User 2 Note", "Content 2", directory2.getId());

        // WHEN - Each user retrieves their notes
        var user1Notes = noteSteps.getUserNotes(token1);
        var user2Notes = noteSteps.getUserNotes(token2);

        // THEN - Each user sees only their own notes
        assertThat(user1Notes).hasSize(1);
        assertThat(user1Notes.getFirst().title()).isEqualTo("User 1 Note");

        assertThat(user2Notes).hasSize(1);
        assertThat(user2Notes.getFirst().title()).isEqualTo("User 2 Note");

        // THEN - User 1 cannot access User 2's note
        // Note: Depending on security implementation, this could be 403 or 404
        // For now we expect it to fail (not 200)
        noteSteps.getNoteAndGetResponse(token1, note2.id())
            .statusCode(org.hamcrest.Matchers.not(200));
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoNotes() {
        // GIVEN - User with no notes
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        // WHEN - User retrieves their notes
        var notes = noteSteps.getUserNotes(token);

        // THEN - Empty list is returned
        assertThat(notes).isEmpty();
    }

    // ========== ERROR SCENARIO TESTS ==========

    @Test
    void shouldFailCreateNoteWithoutAuthentication() {
        // GIVEN - User creates a directory
        var token = userSteps.registerAndGetToken("user@example.com", "password123");
        var directory = dirSteps.createRootDirectory(token, "My Notes");

        // WHEN - Attempt to create note without token
        // THEN - Request is rejected with 401 Unauthorized
        noteSteps.createNoteAndGetResponse(null, "Title", "Content", directory.getId())
            .statusCode(401);
    }

    @Test
    void shouldFailCreateNoteWithBlankTitle() {
        // GIVEN - Authenticated user with directory
        var token = userSteps.registerAndGetToken("user@example.com", "password123");
        var directory = dirSteps.createRootDirectory(token, "My Notes");

        // WHEN - Attempt to create note with blank title
        // THEN - Validation error 400
        noteSteps.createNoteAndGetResponse(token, "", "Content", directory.getId())
            .statusCode(400)
            .body("status", equalTo(400))
            .body("path", equalTo("/api/v1/notes"));
    }

    @Test
    void shouldFailCreateNoteWithNullTitle() {
        // GIVEN - Authenticated user with directory
        var token = userSteps.registerAndGetToken("user@example.com", "password123");
        var directory = dirSteps.createRootDirectory(token, "My Notes");

        // WHEN - Attempt to create note with null title
        // THEN - Validation error 400
        noteSteps.createNoteAndGetResponse(token, null, "Content", directory.getId())
            .statusCode(400)
            .body("status", equalTo(400))
            .body("path", equalTo("/api/v1/notes"));
    }

    @Test
    void shouldFailCreateNoteWithTooLongTitle() {
        // GIVEN - Authenticated user with directory
        var token = userSteps.registerAndGetToken("user@example.com", "password123");
        var directory = dirSteps.createRootDirectory(token, "My Notes");

        // WHEN - Attempt to create note with title exceeding 255 characters
        String longTitle = "a".repeat(256);

        // THEN - Validation error 400
        noteSteps.createNoteAndGetResponse(token, longTitle, "Content", directory.getId())
            .statusCode(400)
            .body("status", equalTo(400))
            .body("path", equalTo("/api/v1/notes"));
    }

    @Test
    void shouldFailGetNonExistentNote() {
        // GIVEN - Authenticated user
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        // WHEN - Attempt to get non-existent note
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
        // GIVEN - Authenticated user with directory
        var token = userSteps.registerAndGetToken("user@example.com", "password123");
        var directory = dirSteps.createRootDirectory(token, "My Notes");

        // WHEN - Attempt to update non-existent note
        UUID nonExistentId = UUID.randomUUID();

        // THEN - 404 Not Found
        noteSteps.updateNoteAndGetResponse(token, nonExistentId, "New Title", "New Content", directory.getId())
            .statusCode(404)
            .body("status", equalTo(404))
            .body("path", equalTo("/api/v1/notes/" + nonExistentId));
    }

    @Test
    void shouldFailDeleteNonExistentNote() {
        // GIVEN - Authenticated user
        var token = userSteps.registerAndGetToken("user@example.com", "password123");

        // WHEN - Attempt to delete non-existent note
        UUID nonExistentId = UUID.randomUUID();

        // THEN - 404 Not Found
        noteSteps.deleteNoteAndGetResponse(token, nonExistentId)
            .statusCode(404)
            .body("status", equalTo(404))
            .body("path", equalTo("/api/v1/notes/" + nonExistentId));
    }

    @Test
    void shouldFailGetNoteWithoutAuthentication() {
        // GIVEN - A directory and note exist
        var token = userSteps.registerAndGetToken("user@example.com", "password123");
        var directory = dirSteps.createRootDirectory(token, "My Notes");
        var note = noteSteps.createNote(token, "Title", "Content", directory.getId());

        // WHEN - Attempt to get note without authentication
        // THEN - 401 Unauthorized
        noteSteps.getNoteAndGetResponse(null, note.id())
            .statusCode(401);
    }

    @Test
    void shouldFailUpdateNoteWithoutAuthentication() {
        // GIVEN - A directory and note exist
        var token = userSteps.registerAndGetToken("user@example.com", "password123");
        var directory = dirSteps.createRootDirectory(token, "My Notes");
        var note = noteSteps.createNote(token, "Title", "Content", directory.getId());

        // WHEN - Attempt to update note without authentication
        // THEN - 401 Unauthorized
        noteSteps.updateNoteAndGetResponse(null, note.id(), "New Title", "New Content", directory.getId())
            .statusCode(401);
    }

    @Test
    void shouldFailDeleteNoteWithoutAuthentication() {
        // GIVEN - A directory and note exist
        var token = userSteps.registerAndGetToken("user@example.com", "password123");
        var directory = dirSteps.createRootDirectory(token, "My Notes");
        var note = noteSteps.createNote(token, "Title", "Content", directory.getId());

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
