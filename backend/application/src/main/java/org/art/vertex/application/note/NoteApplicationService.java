package org.art.vertex.application.note;

import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.directory.DirectoryApplicationService;
import org.art.vertex.application.note.command.CreateNoteCommand;
import org.art.vertex.application.note.command.CreateNotesCommand;
import org.art.vertex.application.note.command.UpdateNoteCommand;
import org.art.vertex.application.tag.TagApplicationService;
import org.art.vertex.application.tag.command.UpsertTagCommand;
import org.art.vertex.application.user.UserApplicationService;
import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.note.event.NoteCreatedEvent;
import org.art.vertex.domain.note.event.NoteDeletedEvent;
import org.art.vertex.domain.note.event.NoteUpdatedEvent;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.art.vertex.domain.tag.model.Tag;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class NoteApplicationService {

    private final UserApplicationService userService;

    private final DirectoryApplicationService directoryService;

    private final TagApplicationService tagService;

    private final NoteRepository noteRepository;

    private final UuidGenerator uuidGenerator;

    private final Clock clock;

    private final ApplicationEventPublisher eventPublisher;

    public NoteApplicationService(
        UserApplicationService userService,
        DirectoryApplicationService directoryService,
        TagApplicationService tagService,
        NoteRepository noteRepository,
        UuidGenerator uuidGenerator,
        Clock clock,
        ApplicationEventPublisher eventPublisher
    ) {
        this.userService = userService;
        this.directoryService = directoryService;
        this.tagService = tagService;
        this.noteRepository = noteRepository;
        this.uuidGenerator = uuidGenerator;
        this.clock = clock;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Note createNote(UUID userId, CreateNoteCommand command) {
        log.debug("Creating note. User id: {}, title: {}", userId, command.getTitle());

        CreateNotesCommand batchCommand = CreateNotesCommand.builder()
            .notes(List.of(command))
            .build();

        List<Note> createdNotes = createNotes(userId, batchCommand);

        Note savedNote = createdNotes.get(0);
        log.info("Note created successfully. Note id: {}, user id: {}", savedNote.getId(), userId);

        return savedNote;
    }

    @Transactional
    public List<Note> createNotes(UUID userId, CreateNotesCommand command) {
        log.debug("Creating notes in batch. User id: {}, count: {}", userId, command.getNotes().size());

        LocalDateTime now = clock.now();
        List<Note> notesToSave = new ArrayList<>();

        for (CreateNoteCommand noteCommand : command.getNotes()) {
            Directory dir = directoryService.getByDirId(noteCommand.getDirId());

            Set<Tag> tags = tagService.upsertTags(userId,
                noteCommand.getTags().stream()
                    .map(tagName -> UpsertTagCommand.builder()
                        .name(tagName)
                        .build())
                    .collect(Collectors.toSet())
            );

            Note newNote = Note.create(
                uuidGenerator.generate(),
                userId,
                noteCommand.getTitle(),
                noteCommand.getContent(),
                dir,
                tags,
                now
            );

            notesToSave.add(newNote);
        }

        List<Note> savedNotes = noteRepository.saveAll(notesToSave);

        for (Note savedNote : savedNotes) {
            eventPublisher.publishEvent(new NoteCreatedEvent(
                savedNote,
                now
            ));
            log.debug("Note created successfully in batch. Note id: {}", savedNote.getId());
        }

        log.info("Batch note creation completed. User id: {}, created: {}", userId, savedNotes.size());
        return savedNotes;
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

        String previousTitle = existingNote.getTitle();
        String previousContent = existingNote.getContent();

        updatedNote = noteRepository.update(updatedNote);

        log.info("Note updated successfully. Note id: {}", noteId);

        eventPublisher.publishEvent(new NoteUpdatedEvent(
            updatedNote,
            previousTitle,
            previousContent,
            now
        ));

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

        eventPublisher.publishEvent(new NoteDeletedEvent(
            noteId,
            userId,
            clock.now()
        ));
    }
}