package org.art.vertex.domain.note.search;

import org.art.vertex.domain.note.model.Note;

import java.util.List;
import java.util.UUID;

public interface VectorSearchService extends IndexService, SearchService {

    List<Note> findKNearest(UUID noteId, int k);
}
