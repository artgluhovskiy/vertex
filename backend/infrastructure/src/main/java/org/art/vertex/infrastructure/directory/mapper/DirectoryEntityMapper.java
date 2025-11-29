package org.art.vertex.infrastructure.directory.mapper;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.infrastructure.directory.entity.DirectoryEntity;
import org.art.vertex.infrastructure.directory.jpa.DirectoryJpaRepository;

@RequiredArgsConstructor
public class DirectoryEntityMapper {

    private final DirectoryJpaRepository jpaRepository;

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
        Directory parent = null;
        if (entity.getParentId() != null) {
            // Load parent directory without recursion (parent of parent is not loaded)
            parent = jpaRepository.findById(entity.getParentId())
                .map(this::toDomainWithoutParent)
                .orElse(null);
        }

        return Directory.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .name(entity.getName())
            .parent(parent)
            .createdTs(entity.getCreatedAt())
            .updatedTs(entity.getUpdatedAt())
            .version(entity.getVersion())
            .build();
    }

    private Directory toDomainWithoutParent(DirectoryEntity entity) {
        return Directory.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .name(entity.getName())
            .parent(null) // Stop recursion - only load one level of parent
            .createdTs(entity.getCreatedAt())
            .updatedTs(entity.getUpdatedAt())
            .version(entity.getVersion())
            .build();
    }
}

