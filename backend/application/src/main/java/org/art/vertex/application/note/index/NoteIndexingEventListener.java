package org.art.vertex.application.note.index;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.note.event.NoteCreatedEvent;
import org.art.vertex.domain.note.event.NoteDeletedEvent;
import org.art.vertex.domain.note.event.NoteUpdatedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class NoteIndexingEventListener {

    private final NoteIndexingApplicationService indexingService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNoteCreated(NoteCreatedEvent event) {
        log.debug("Handling NoteCreatedEvent for note: {}", event.getNote().getId());

        try {
            indexingService.indexNote(event.getNote());
        } catch (Exception e) {
            log.error("Failed to index note after creation: {}", event.getNote().getId(), e);
            // Don't throw - we don't want to fail the entire operation if indexing fails
        }
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNoteUpdated(NoteUpdatedEvent event) {
        log.debug("Handling NoteUpdatedEvent for note: {}", event.getNote().getId());

        try {
            indexingService.reindexNote(event.getNote());
        } catch (Exception e) {
            log.error("Failed to reindex note after update: {}", event.getNote().getId(), e);
            // Don't throw - we don't want to fail the entire operation if indexing fails
        }
    }

    /**
     * Handle note deletion event and remove the note from index after the transaction commits.
     * Runs asynchronously to avoid blocking the main transaction and ensure data is visible.
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleNoteDeleted(NoteDeletedEvent event) {
        log.debug("Handling NoteDeletedEvent for note: {}", event.getNoteId());

        try {
            indexingService.removeNoteFromIndex(event.getNoteId());
        } catch (Exception e) {
            log.error("Failed to remove note from index after deletion: {}", event.getNoteId(), e);
            // Don't throw - we don't want to fail the entire operation if index removal fails
        }
    }
}
