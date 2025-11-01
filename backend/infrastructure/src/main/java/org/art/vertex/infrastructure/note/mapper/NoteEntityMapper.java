package org.art.vertex.infrastructure.note.mapper;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.directory.DirectoryRepository;
import org.art.vertex.domain.note.model.Note;
import org.art.vertex.infrastructure.note.entity.NoteEntity;
import org.art.vertex.infrastructure.tag.mapper.TagEntityMapper;

import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class NoteEntityMapper {

    private final DirectoryRepository directoryRepository;

    private final TagEntityMapper tagEntityMapper;

    public NoteEntity toEntity(Note note) {
        NoteEntity entity = NoteEntity.builder()
            .id(note.getId())
            .userId(note.getUserId())
            .dirId(note.getDir() != null ? note.getDir().getId() : null)
            .title(note.getTitle())
            .content(note.getContent())
            .summary(note.getSummary())
            .metadata(note.getMetadata())
            .createdAt(note.getCreatedTs())
            .updatedAt(note.getUpdatedTs())
            .version(note.getVersion())
            .build();

        // Map tags
        if (note.getTags() != null && !note.getTags().isEmpty()) {
            entity.setTags(note.getTags().stream()
                .map(tagEntityMapper::toEntity)
                .collect(Collectors.toSet()));
        }

        return entity;
    }

    public Note toDomain(NoteEntity entity) {
        return Note.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .dir(entity.getDirId() != null ? directoryRepository.getById(entity.getDirId()) : null)
            .title(entity.getTitle())
            .content(entity.getContent())
            .summary(entity.getSummary())
            .tags(entity.getTags() != null ?
                entity.getTags().stream()
                    .map(tagEntityMapper::toDomain)
                    .collect(Collectors.toSet()) : Set.of())
            .metadata(entity.getMetadata())
            .createdTs(entity.getCreatedAt())
            .updatedTs(entity.getUpdatedAt())
            .version(entity.getVersion())
            .build();
    }
}
