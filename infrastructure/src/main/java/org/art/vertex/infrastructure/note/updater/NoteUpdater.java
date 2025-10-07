package org.art.vertex.infrastructure.note.updater;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.infrastructure.note.entity.NoteEntity;
import org.art.vertex.infrastructure.tag.mapper.TagEntityMapper;

/**
 * Component responsible for updating NoteEntity with values from Note domain object.
 * Handles all field updates including tag synchronization.
 */
@RequiredArgsConstructor
public class NoteUpdater {

    private final TagEntityMapper tagMapper;

    /**
     * Updates the existing entity with values from the domain note.
     * Synchronizes all fields including the many-to-many tag relationship.
     *
     * @param existingEntity The existing JPA entity to update
     * @param note The domain note with updated values
     */
    public void updateEntity(NoteEntity existingEntity, Note note) {
        // Update simple fields
        existingEntity.setTitle(note.getTitle());
        existingEntity.setContent(note.getContent());
        existingEntity.setSummary(note.getSummary());
        existingEntity.setDirectoryId(note.getDirectory() != null ? note.getDirectory().getId() : null);
        existingEntity.setMetadata(note.getMetadata());
        existingEntity.setUpdatedAt(note.getUpdatedTs());
        existingEntity.setVersion(note.getVersion());

        // Synchronize tags - clear existing and add new ones
        // JPA will handle the junction table updates automatically
        synchronizeTags(existingEntity, note);
    }

    /**
     * Synchronizes the tag collection between entity and domain object.
     * Clears existing tags and adds new ones from the domain note.
     *
     * @param entity The entity to update
     * @param note The domain note with the desired tags
     */
    private void synchronizeTags(NoteEntity entity, Note note) {
        entity.getTags().clear();

        if (note.getTags() != null && !note.getTags().isEmpty()) {
            note.getTags().forEach(tag ->
                entity.getTags().add(tagMapper.toEntity(tag))
            );
        }
    }
}
