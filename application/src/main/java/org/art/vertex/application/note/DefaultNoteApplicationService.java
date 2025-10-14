package org.art.vertex.application.note;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.note.command.CreateNoteCommand;
import org.art.vertex.application.note.command.UpdateNoteCommand;
import org.art.vertex.domain.note.NoteRepository;
import org.art.vertex.domain.note.model.Note;
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

    @Override
    public Note createNote(CreateNoteCommand command) {
        log.debug("Creating note. User id: {}, title: {}", command.getUserId(), command.getTitle());

        User user = userRepository.getById(command.getUserId());

        LocalDateTime now = LocalDateTime.now();

        Note note = Note.create(
            uuidGenerator.generate(),
            user,
            command.getTitle(),
            command.getContent(),
            null,
            now
        );

        note = noteRepository.save(note);

        log.info("Note created successfully. Note id: {}, user id: {}", note.getId(), user.getId());

        return note;
    }

    @Override
    public Note updateNote(UUID userId, UUID noteId, UpdateNoteCommand command) {
        log.debug("Updating note. Note id: {}, user id: {}", noteId, userId);

        User user = userRepository.getById(userId);
        Note note = noteRepository.getByIdAndUser(noteId, user);

        LocalDateTime now = LocalDateTime.now();

        Note updatedNote = note.update(command.getTitle(), command.getContent(), now);

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
