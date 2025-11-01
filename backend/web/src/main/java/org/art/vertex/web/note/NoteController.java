package org.art.vertex.web.note;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.note.NoteApplicationService;
import org.art.vertex.application.note.command.CreateNoteCommand;
import org.art.vertex.application.note.command.UpdateNoteCommand;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.web.note.dto.NoteDto;
import org.art.vertex.web.note.mapper.NoteCommandMapper;
import org.art.vertex.web.note.mapper.NoteDtoMapper;
import org.art.vertex.web.note.request.CreateNoteRequest;
import org.art.vertex.web.note.request.UpdateNoteRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteApplicationService noteService;

    private final NoteCommandMapper noteCommandMapper;

    private final NoteDtoMapper noteDtoMapper;

    @PostMapping
    public ResponseEntity<NoteDto> createNote(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody CreateNoteRequest request
    ) {
        log.trace("Processing create note request. User id: {}, title: {}", userId, request.title());

        CreateNoteCommand command = noteCommandMapper.toCommand(request);

        Note createdNote = noteService.createNote(UUID.fromString(userId), command);

        NoteDto createdNoteDto = noteDtoMapper.toDto(createdNote);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdNoteDto);
    }

    @PutMapping("/{noteId}")
    public ResponseEntity<NoteDto> updateNote(
        @AuthenticationPrincipal String userId,
        @PathVariable UUID noteId,
        @Valid @RequestBody UpdateNoteRequest request
    ) {
        log.trace("Processing update note request. Note id: {}, user id: {}", noteId, userId);

        UpdateNoteCommand command = noteCommandMapper.toCommand(request);

        Note updatedNote = noteService.updateNote(UUID.fromString(userId), noteId, command);

        NoteDto updatedNoteDto = noteDtoMapper.toDto(updatedNote);

        return ResponseEntity.ok(updatedNoteDto);
    }

    @DeleteMapping("/{noteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(
        @PathVariable UUID noteId,
        @AuthenticationPrincipal String userId
    ) {
        log.trace("Processing delete note request. Note id: {}, user id: {}", noteId, userId);

        noteService.deleteNote(noteId, UUID.fromString(userId));
    }

    @GetMapping("/{noteId}")
    public ResponseEntity<NoteDto> getNote(
        @PathVariable UUID noteId,
        @AuthenticationPrincipal String userId
    ) {
        log.trace("Fetching note. Note id: {}, user id: {}", noteId, userId);

        Note note = noteService.getNote(noteId, UUID.fromString(userId));

        NoteDto noteDto = noteDtoMapper.toDto(note);

        return ResponseEntity.ok(noteDto);
    }

    @GetMapping
    public ResponseEntity<List<NoteDto>> getUserNotes(@AuthenticationPrincipal String userId) {
        log.trace("Fetching notes for user. User id: {}", userId);

        List<Note> notes = noteService.getAllNotes(UUID.fromString(userId));

        List<NoteDto> noteDtos = notes.stream()
            .map(noteDtoMapper::toDto)
            .toList();

        return ResponseEntity.ok(noteDtos);
    }
}
