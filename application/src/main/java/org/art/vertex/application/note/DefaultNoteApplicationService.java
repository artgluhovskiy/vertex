package org.art.vertex.application.note;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.note.command.CreateNoteCommand;
import org.art.vertex.application.note.command.UpdateNoteCommand;
import org.art.vertex.application.tag.TagApplicationService;
import org.art.vertex.application.tag.command.UpsertTagCommand;
import org.art.vertex.domain.directory.DirectoryRepository;
import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.art.vertex.domain.tag.model.Tag;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.domain.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DefaultNoteApplicationService implements NoteApplicationService {

    private final UserRepository userRepository;

    private final NoteRepository noteRepository;

    private final DirectoryRepository directoryRepository;

    private final TagApplicationService tagService;

    private final UuidGenerator uuidGenerator;

    private final Clock clock;

    @Override
    public Note createNote(UUID userId, CreateNoteCommand command) {
        log.debug("Creating note. User id: {}, title: {}", userId, command.getTitle());

        LocalDateTime now = clock.now();

        User user = userRepository.getById(userId);

        // Handle optional directory
        Directory dir = null;
        if (command.getDirId() != null) {
            dir = directoryRepository.getById(command.getDirId());
        }

        // Handle optional tags
        List<Tag> tags = List.of();
        if (command.getTags() != null && !command.getTags().isEmpty()) {
            tags = tagService.upsertTags(userId,
                command.getTags().stream()
                    .map(tagName -> UpsertTagCommand.builder()
                        .name(tagName)
                        .build())
                    .toList()
            );
        }

        Note newNote = Note.create(
            uuidGenerator.generate(),
            user,
            command.getTitle(),
            command.getContent(),
            dir,
            tags,
            now
        );

        newNote = noteRepository.save(newNote);

        log.info("Note created successfully. Note id: {}, user id: {}", newNote.getId(), user.getId());

        return newNote;
    }

    @Override
    public Note updateNote(UUID userId, UUID noteId, UpdateNoteCommand command) {
        log.debug("Updating note. Note id: {}, user id: {}", noteId, userId);

        User user = userRepository.getById(userId);
        Note existingNote = noteRepository.getByIdAndUser(noteId, user);

        LocalDateTime now = clock.now();

        // Handle optional directory
        Directory dir = null;
        if (command.getDirId() != null) {
            dir = directoryRepository.getById(command.getDirId());
        }

        // Handle optional tags
        List<Tag> tags = null;
        if (command.getTags() != null) {
            if (command.getTags().isEmpty()) {
                tags = List.of();
            } else {
                tags = tagService.upsertTags(userId,
                    command.getTags().stream()
                        .map(tagName -> UpsertTagCommand.builder()
                            .name(tagName)
                            .build())
                        .toList()
                );
            }
        }

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

    @Override
    public Note getNote(UUID userId, UUID noteId) {
        log.debug("Fetching note. Note id: {}, user id: {}", noteId, userId);

        User user = userRepository.getById(userId);
        return noteRepository.getByIdAndUser(noteId, user);
    }

    @Override
    public List<Note> getNotesByUser(UUID userId) {
        log.debug("Fetching notes for user. User id: {}", userId);

        User user = userRepository.getById(userId);

        return noteRepository.findAll(user);
    }

    @Override
    public void deleteNote(UUID userId, UUID noteId) {
        log.debug("Deleting note. Note id: {}, user id: {}", noteId, userId);

        User user = userRepository.getById(userId);
        noteRepository.deleteByIdAndUser(noteId, user);

        log.info("Note deleted successfully. Note id: {}", noteId);
    }

    @Override
    public Note addTagToNote(UUID noteId, UUID tagId) {
        log.debug("Adding tag to note. Note id: {}, tag id: {}", noteId, tagId);
        // TODO: Implement tag addition
        throw new UnsupportedOperationException("Tag functionality not yet implemented");
    }

    @Override
    public Note removeTagFromNote(UUID noteId, UUID tagId) {
        log.debug("Removing tag from note. Note id: {}, tag id: {}", noteId, tagId);
        // TODO: Implement tag removal
        throw new UnsupportedOperationException("Tag functionality not yet implemented");
    }
}
