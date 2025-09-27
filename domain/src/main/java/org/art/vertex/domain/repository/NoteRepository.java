package org.art.vertex.domain.repository;

import org.art.vertex.domain.model.directory.Directory;
import org.art.vertex.domain.model.note.Note;
import org.art.vertex.domain.model.tag.Tag;
import org.art.vertex.domain.model.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository {

    Note save(Note note);

    Note getById(UUID id);

    Optional<Note> findById(UUID id);

    List<Note> findAll(User user);

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

    /**
     * Find notes by tags.
     * @param user Note owner
     * @param tags List of tags to search for
     * @return List of notes containing any of the specified tags
     */
    List<Note> findAllByTags(User user, List<Tag> tags);

    /**
     * Find notes by tag names.
     * @param user Note owner
     * @param tagNames List of tag names to search for
     * @return List of notes containing any of the specified tag names
     */
    List<Note> findAllByTagNames(User user, List<String> tagNames);

    /**
     * Search all notes by search terms (semantic + full text search).
     *
     * @param user       user
     * @param searchTerm search term
     * @return List of notes matching search terms with relevance scoring
     */
    List<Note> findAllBySearchTerm(User user, String searchTerm);

    void deleteById(UUID id);
}