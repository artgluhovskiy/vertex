package org.art.vertex.domain.note.search;

import org.art.vertex.domain.note.model.Note;

import java.util.UUID;

public interface IndexService {

    void index(Note note);

    void remove(UUID noteId);

    void reindex(Note note);

    void optimizeIndex();
}
