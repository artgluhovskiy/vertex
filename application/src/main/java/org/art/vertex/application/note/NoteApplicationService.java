package org.art.vertex.application.note;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.directory.DirectoryApplicationService;
import org.art.vertex.application.note.command.CreateNoteCommand;
import org.art.vertex.application.note.command.UpdateNoteCommand;
import org.art.vertex.application.tag.TagApplicationService;
import org.art.vertex.application.tag.command.UpsertTagCommand;
import org.art.vertex.application.user.UserApplicationService;
import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.art.vertex.domain.tag.model.Tag;
import org.art.vertex.domain.user.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class NoteApplicationService {

    private final UserApplicationService userService;

    private final DirectoryApplicationService directoryService;

    private final TagApplicationService tagService;

    private final NoteRepository noteRepository;

    private final UuidGenerator uuidGenerator;

    private final Clock clock;

    @Transactional
    public Note createNote(UUID userId, CreateNoteCommand command) {
        log.debug("Creating note. User id: {}, title: {}", userId, command.getTitle());

        LocalDateTime now = clock.now();

        User user = userService.getById(userId);

        Directory dir = directoryService.getByDirId(command.getDirId());

        Set<Tag> tags = tagService.upsertTags(userId,
            command.getTags().stream()
                .map(tagName -> UpsertTagCommand.builder()
                    .name(tagName)
                    .build())
                .collect(Collectors.toSet())
        );

        Note newNote = Note.create(
            uuidGenerator.generate(),
            user,
            command.getTitle(),
            command.getContent(),
            dir,
            tags,
            now
        );

        Note savedNote = noteRepository.save(newNote);

        log.info("Note created successfully. Note id: {}, user id: {}", savedNote.getId(), user.getId());

        return savedNote;
    }

    @Transactional
    public Note updateNote(UUID userId, UUID noteId, UpdateNoteCommand command) {
        log.debug("Updating note. Note id: {}, user id: {}", noteId, userId);

        Note existingNote = noteRepository.getByNoteIdAndUserId(noteId, userId);

        LocalDateTime now = clock.now();

        Directory dir = directoryService.getByDirId(command.getDirId());

        Set<Tag> tags = tagService.upsertTags(userId,
            command.getTags().stream()
                .map(tagName -> UpsertTagCommand.builder()
                    .name(tagName)
                    .build())
                .collect(Collectors.toSet())
        );

        Note updatedNote = existingNote.update(
            command.getTitle(),
            command.getContent(),
            dir,
            tags,
            now
        );

        updatedNote = noteRepository.update(updatedNote);

        log.info("Note updated successfully. Note id: {}", noteId);

        return updatedNote;
    }

    @Transactional(readOnly = true)
    public Note getNote(UUID noteId, UUID userId) {
        log.debug("Fetching note. Note id: {}, user id: {}", noteId, userId);

        return noteRepository.getByNoteIdAndUserId(noteId, userId);
    }

    @Transactional(readOnly = true)
    public List<Note> getAllNotes(UUID userId) {
        log.debug("Fetching all user notes. User id: {}", userId);

        return noteRepository.findAll(userId);
    }

    @Transactional
    public void deleteNote(UUID noteId, UUID userId) {
        log.debug("Deleting note. Note id: {}, user id: {}", noteId, userId);

        noteRepository.deleteByNoteIdAndUserId(noteId, userId);

        log.info("Note deleted successfully. Note id: {}", noteId);
    }
}