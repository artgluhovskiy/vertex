package org.art.vertex.application.note.index;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.note.search.VectorSearchService;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class NoteIndexingApplicationService {

    private final VectorSearchService vectorSearchService;

    @Transactional
    public void indexNote(Note note) {
        log.debug("Indexing note: {}", note.getId());
        vectorSearchService.index(note);
        log.info("Successfully indexed note: {}", note.getId());
    }

    @Transactional
    public void reindexNote(Note note) {
        log.debug("Reindexing note: {}", note.getId());
        vectorSearchService.reindex(note);
        log.info("Successfully reindexed note: {}", note.getId());
    }

    @Transactional
    public void removeNoteFromIndex(UUID noteId) {
        log.debug("Removing note from index: {}", noteId);
        vectorSearchService.remove(noteId);
        log.info("Successfully removed note from index: {}", noteId);
    }
}
