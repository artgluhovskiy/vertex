package org.art.vertex.domain.note;

import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository {

    Note save(Note note);

    Note update(Note note);

    Note getByNoteIdAndUserId(UUID noteId, UUID userId);

    Optional<Note> findByNoteIdAndUserId(UUID noteId, UUID userId);

    List<Note> findAll(UUID userId);

    /**
     * Find notes in directory.
     *
     * @param directory root directory
     * @return List of notes in directory tree
     */
    List<Note> findAllByDirectory(Directory directory);

    /**
     * Find notes in directory and all subdirectories.
     *
     * @param directory root directory
     * @return List of notes in directory tree
     */
    List<Note> findAllByDirectoryTree(Directory directory);

    void deleteByNoteIdAndUserId(UUID id, UUID userId);
}