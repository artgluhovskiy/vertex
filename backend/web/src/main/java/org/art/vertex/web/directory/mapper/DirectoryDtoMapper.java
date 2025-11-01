package org.art.vertex.web.directory.mapper;

import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.web.directory.dto.DirectoryDto;

public class DirectoryDtoMapper {

    public DirectoryDto toDto(Directory directory) {
        return DirectoryDto.builder()
            .id(directory.getId())
            .userId(directory.getUserId())
            .name(directory.getName())
            .parentId(directory.getParent() != null ? directory.getParent().getId() : null)
            .createdAt(directory.getCreatedTs())
            .updatedAt(directory.getUpdatedTs())
            .build();
    }
}
