package org.art.vertex.application.note.search;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.note.search.VectorSearchService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class NoteIndexingApplicationService {

    private final VectorSearchService vectorSearchService;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void indexNoteAsync(Note note) {
        try {
            log.debug("Indexing note asynchronously: {}", note.getId());
            vectorSearchService.index(note);
            log.info("Successfully indexed note: {}", note.getId());
        } catch (Exception e) {
            log.error("Failed to index note: {}. Note is saved but not searchable.",
                note.getId(), e);
        }
    }

    @Transactional
    public void indexNoteSync(Note note) {
        log.debug("Indexing note synchronously: {}", note.getId());
        vectorSearchService.index(note);
        log.info("Successfully indexed note: {}", note.getId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reindexNoteAsync(Note note) {
        try {
            log.debug("Reindexing note asynchronously: {}", note.getId());
            vectorSearchService.reindex(note);
            log.info("Successfully reindexed note: {}", note.getId());
        } catch (Exception e) {
            log.error("Failed to reindex note: {}", note.getId(), e);
        }
    }

    @Transactional
    public void reindexNoteSync(Note note) {
        log.debug("Reindexing note synchronously: {}", note.getId());
        vectorSearchService.reindex(note);
        log.info("Successfully reindexed note: {}", note.getId());
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void removeNoteFromIndexAsync(UUID noteId) {
        try {
            log.debug("Removing note from index asynchronously: {}", noteId);
            vectorSearchService.remove(noteId);
            log.info("Successfully removed note from index: {}", noteId);
        } catch (Exception e) {
            log.error("Failed to remove note from index: {}", noteId, e);
        }
    }

    @Transactional
    public void removeNoteFromIndexSync(UUID noteId) {
        log.debug("Removing note from index synchronously: {}", noteId);
        vectorSearchService.remove(noteId);
        log.info("Successfully removed note from index: {}", noteId);
    }
}
