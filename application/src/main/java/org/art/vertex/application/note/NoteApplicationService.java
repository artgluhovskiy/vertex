package org.art.vertex.application.note;

import org.art.vertex.application.note.command.CreateNoteCommand;
import org.art.vertex.application.note.command.UpdateNoteCommand;
import org.art.vertex.application.note.dto.NoteDto;

import java.util.List;
import java.util.UUID;

public interface NoteApplicationService {

    NoteDto createNote(CreateNoteCommand command);

    NoteDto updateNote(UUID noteId, UpdateNoteCommand command);

    NoteDto getNote(UUID noteId);

    List<NoteDto> getNotesByUser(UUID userId);

    void deleteNote(UUID noteId);

    NoteDto addTagToNote(UUID noteId, UUID tagId);

    NoteDto removeTagFromNote(UUID noteId, UUID tagId);
}