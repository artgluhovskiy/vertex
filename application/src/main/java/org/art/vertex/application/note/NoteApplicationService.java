package org.art.vertex.application.note;

import org.art.vertex.domain.note.model.Note;

import java.util.List;
import java.util.UUID;

public interface NoteApplicationService {

    Note createNote(UUID userId, Note note);

    Note updateNote(UUID userId, UUID noteId, Note note);

    Note getNote(UUID userId, UUID noteId);

    List<Note> getNotesByUser(UUID userId);

    void deleteNote(UUID userId, UUID noteId);

    Note addTagToNote(UUID noteId, UUID tagId);

    Note removeTagFromNote(UUID noteId, UUID tagId);
}