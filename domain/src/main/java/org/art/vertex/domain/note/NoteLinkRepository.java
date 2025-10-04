package org.art.vertex.domain.note;

import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.note.model.NoteLink;
import org.art.vertex.domain.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NoteLinkRepository {

    NoteLink save(NoteLink noteLink);

    NoteLink getById(UUID id);

    Optional<NoteLink> findById(UUID id);

    /**
     * Find outgoing links from a note.
     *
     * @param sourceNote source note
     * @return list of links originating from the note
     */
    List<NoteLink> findAllBySourceNote(Note sourceNote);

    /**
     * Find incoming links to a note (backlinks).
     *
     * @param targetNote target note
     * @return list of links pointing to the note
     */
    List<NoteLink> findAllByTargetNote(Note targetNote);

    /**
     * Find all links for a note (both incoming and outgoing).
     *
     * @param note note to find links for
     * @return list of all links connected to the note
     */
    List<NoteLink> findAllByNote(Note note);

    List<NoteLink> findAllByUser(User user);

    /**
     * Check if link exists between two notes.
     *
     * @param sourceNote source note
     * @param targetNote target note
     * @return true if any link exists between the notes
     */
    boolean existsBetweenNotes(Note sourceNote, Note targetNote);

    void deleteById(UUID id);

    void deleteByNote(Note note);
}