package org.art.vertex.application.note;

import org.art.vertex.application.note.command.CreateNoteCommand;
import org.art.vertex.application.note.command.UpdateNoteCommand;
import org.art.vertex.domain.note.model.Note;

import java.util.List;
import java.util.UUID;

public interface NoteApplicationService {

    Note createNote(CreateNoteCommand command);

    Note updateNote(UUID noteId, UpdateNoteCommand command);

    Note getNote(UUID noteId);

    List<Note> getNotesByUser(UUID userId);

    void deleteNote(UUID noteId);

    Note addTagToNote(UUID noteId, UUID tagId);

    Note removeTagFromNote(UUID noteId, UUID tagId);
}