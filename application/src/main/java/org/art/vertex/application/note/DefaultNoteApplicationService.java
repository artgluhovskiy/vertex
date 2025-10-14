package org.art.vertex.application.note;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.domain.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DefaultNoteApplicationService implements NoteApplicationService {

    private final NoteRepository noteRepository;

    private final UserRepository userRepository;

    private final UuidGenerator uuidGenerator;

    private final Clock clock;

    @Override
    public Note createNote(UUID userId, Note note) {
        log.debug("Creating note. User id: {}, title: {}", userId, note.getTitle());

        User user = userRepository.getById(userId);

        LocalDateTime now = clock.now();

        // TODO: Support directories and tags when implementations are available
        Note newNote = Note.create(
            uuidGenerator.generate(),
            user,
            note.getTitle(),
            note.getContent(),
            null,  // Directory support to be implemented
            now
        );

        newNote = noteRepository.save(newNote);

        log.info("Note created successfully. Note id: {}, user id: {}", newNote.getId(), user.getId());

        return newNote;
    }

    @Override
    public Note updateNote(UUID userId, UUID noteId, Note updatedNote) {
        log.debug("Updating note. Note id: {}, user id: {}", noteId, userId);

        User user = userRepository.getById(userId);
        Note existingNote = noteRepository.getByIdAndUser(noteId, user);

        LocalDateTime now = clock.now();

        // TODO: Support directories and tags when implementations are available
        Note result = existingNote.update(
            updatedNote.getTitle(),
            updatedNote.getContent(),
            now
        );

        result = noteRepository.update(result);

        log.info("Note updated successfully. Note id: {}", noteId);

        return result;
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
