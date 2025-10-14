package org.art.vertex.application.note;

import org.art.vertex.application.note.command.CreateNoteCommand;
import org.art.vertex.application.note.command.UpdateNoteCommand;
import org.art.vertex.domain.note.model.Note;

import java.util.List;
import java.util.UUID;

public interface NoteApplicationService {

    Note createNote(UUID userId, CreateNoteCommand command);

    Note updateNote(UUID userId, UUID noteId, UpdateNoteCommand command);

    Note getNote(UUID userId, UUID noteId);

    List<Note> getNotesByUser(UUID userId);

    void deleteNote(UUID userId, UUID noteId);

    Note addTagToNote(UUID noteId, UUID tagId);

    Note removeTagFromNote(UUID noteId, UUID tagId);
}