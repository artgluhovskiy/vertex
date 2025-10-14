package org.art.vertex.infrastructure.note.updater;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.domain.tag.model.Tag;
import org.art.vertex.infrastructure.note.entity.NoteEntity;
import org.art.vertex.infrastructure.tag.entity.TagEntity;
import org.art.vertex.infrastructure.tag.mapper.TagEntityMapper;
import org.art.vertex.infrastructure.util.CollectionUpdaterUtil;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NoteUpdater {

    private final TagEntityMapper tagMapper;

    public void updateNote(NoteEntity existingEntity, Note note) {
        existingEntity.setTitle(note.getTitle());
        existingEntity.setContent(note.getContent());
        existingEntity.setSummary(note.getSummary());
        existingEntity.setDirId(note.getDir() != null ? note.getDir().getId() : null);
        existingEntity.setMetadata(note.getMetadata());
        existingEntity.setUpdatedAt(note.getUpdatedTs());
        existingEntity.setVersion(note.getVersion());

        updateTags(existingEntity.getTags(), note.getTags());
    }

    private void updateTags(Set<TagEntity> existingTags, Set<Tag> updatedTags) {
        Set<TagEntity> newTags = updatedTags.stream()
            .map(tagMapper::toEntity)
            .collect(Collectors.toSet());

        CollectionUpdaterUtil.updateCollection(existingTags, newTags);
    }
}
