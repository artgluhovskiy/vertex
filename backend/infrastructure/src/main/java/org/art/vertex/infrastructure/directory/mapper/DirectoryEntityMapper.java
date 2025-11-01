package org.art.vertex.infrastructure.directory.mapper;

import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.infrastructure.directory.entity.DirectoryEntity;

public class DirectoryEntityMapper {

    public DirectoryEntity toEntity(Directory directory) {
        return DirectoryEntity.builder()
            .id(directory.getId())
            .userId(directory.getUserId())
            .name(directory.getName())
            .parentId(directory.getParent() != null ? directory.getParent().getId() : null)
            .createdAt(directory.getCreatedTs())
            .updatedAt(directory.getUpdatedTs())
            .version(directory.getVersion())
            .build();
    }

    public Directory toDomain(DirectoryEntity entity) {
        return Directory.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .name(entity.getName())
            .parent(null) // Parent will be loaded separately when needed to avoid recursive loading
            .createdTs(entity.getCreatedAt())
            .updatedTs(entity.getUpdatedAt())
            .version(entity.getVersion())
            .build();
    }
}

