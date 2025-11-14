package org.art.vertex.infrastructure.note.search.indexing;

import org.art.vertex.domain.note.model.Note;

/**
 * Strategy for creating searchable text from notes.
 */
public interface TextIndexingStrategy {

    /**
     * Create searchable text from note for full-text indexing.
     */
    String createIndexText(Note note);
}
