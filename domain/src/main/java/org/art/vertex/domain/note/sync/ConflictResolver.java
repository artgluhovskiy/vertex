package org.art.vertex.domain.note.sync;

import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.note.sync.model.ConflictResolution;
import org.art.vertex.domain.note.sync.model.ResolutionStrategy;
import org.art.vertex.domain.note.sync.model.SyncConflict;

import java.time.LocalDateTime;

/**
 * Domain service for conflict resolution logic.
 * Contains business rules for resolving synchronization conflicts.
 */
public class ConflictResolver {

    /**
     * Apply business rules to automatically resolve conflicts when possible.
     */
    public ConflictResolution resolveConflict(SyncConflict conflict) {
        Note local = conflict.getLocalVersion();
        Note remote = conflict.getRemoteVersion();

        // Business rule: If versions are the same, no conflict
        if (local.getVersion().equals(remote.getVersion())) {
            return ConflictResolution.builder()
                .resolvedNote(local)
                .strategy(ResolutionStrategy.KEEP_LOCAL)
                .reason("Versions are identical")
                .build();
        }

        // Business rule: If only metadata changed, prefer remote
        if (contentEquals(local, remote)) {
            return ConflictResolution.builder()
                .resolvedNote(remote)
                .strategy(ResolutionStrategy.KEEP_REMOTE)
                .reason("Only metadata changed, keeping remote version")
                .build();
        }

        // Business rule: For minor edits within a time window, merge
        if (canAutoMerge(local, remote)) {
            Note merged = attemptMerge(local, remote);
            return ConflictResolution.builder()
                .resolvedNote(merged)
                .strategy(ResolutionStrategy.MERGE)
                .reason("Automatically merged non-conflicting changes")
                .build();
        }

        // Default: Last write wins
        Note winner = local.getUpdatedTs().isAfter(remote.getUpdatedTs()) ? local : remote;
        return ConflictResolution.builder()
            .resolvedNote(winner)
            .strategy(ResolutionStrategy.KEEP_LOCAL)
            .reason("Last write wins based on timestamp")
            .build();
    }

    private boolean contentEquals(Note note1, Note note2) {
        if (note1.getContent() == null && note2.getContent() == null) {
            return true;
        }
        if (note1.getContent() == null || note2.getContent() == null) {
            return false;
        }
        return note1.getContent().equals(note2.getContent()) &&
               note1.getTitle().equals(note2.getTitle());
    }

    private boolean canAutoMerge(Note local, Note remote) {
        // Business rule: Only auto-merge if changes are recent (within 1 hour)
        LocalDateTime now = LocalDateTime.now();
        return local.getUpdatedTs().isAfter(now.minusHours(1)) &&
               remote.getUpdatedTs().isAfter(now.minusHours(1));
    }

    private Note attemptMerge(Note local, Note remote) {
        // Simplified merge logic - in reality would be more sophisticated
        // Take the longer content as the merged version
        String mergedContent = local.getContent().length() >= remote.getContent().length()
            ? local.getContent()
            : remote.getContent();

        return Note.builder()
            .id(local.getId())
            .user(local.getUser())
            .directory(local.getDirectory())
            .title(local.getTitle())
            .content(mergedContent)
            .tags(local.getTags())
            .metadata(local.getMetadata())
            .updatedTs(LocalDateTime.now())
            .createdTs(local.getCreatedTs())
            .version(Math.max(local.getVersion(), remote.getVersion()) + 1)
            .build();
    }
}