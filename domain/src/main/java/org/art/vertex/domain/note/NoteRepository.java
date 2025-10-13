package org.art.vertex.domain.note;

import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.tag.model.Tag;
import org.art.vertex.domain.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteRepository {

    Note save(Note note);

    /**
     * Update an existing note. This method handles all update scenarios including:
     * - Content/title updates
     * - Tag additions/removals
     * - Directory changes
     * - Metadata updates
     *
     * The method will manage the many-to-many relationship with tags properly,
     * ensuring that the tag associations are correctly synchronized.
     *
     * @param note The updated note with new values
     * @return The updated note from the database
     */
    Note update(Note note);

    Note getById(UUID id);

    Note getByIdAndUser(UUID id, User user);

    Optional<Note> findById(UUID id);

    Optional<Note> findByIdAndUser(UUID id, User user);

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

    void deleteByIdAndUser(UUID id, User user);
}