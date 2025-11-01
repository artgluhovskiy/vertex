package org.art.vertex.infrastructure.tag.mapper;

import org.art.vertex.domain.tag.model.Tag;
import org.art.vertex.infrastructure.tag.entity.TagEntity;

public class TagEntityMapper {

    public TagEntity toEntity(Tag tag) {
        return TagEntity.builder()
            .id(tag.getId())
            .userId(tag.getUserId())
            .name(tag.getName())
            .createdAt(tag.getCreatedAt())
            .updatedAt(tag.getUpdatedAt())
            .version(tag.getVersion())
            .build();
    }

    public Tag toDomain(TagEntity entity) {
        return Tag.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .name(entity.getName())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .version(entity.getVersion())
            .build();
    }
}
