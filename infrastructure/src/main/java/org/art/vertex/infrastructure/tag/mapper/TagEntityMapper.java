package org.art.vertex.infrastructure.tag.mapper;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.tag.model.Tag;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.infrastructure.tag.entity.TagEntity;

@RequiredArgsConstructor
public class TagEntityMapper {

    private final UserRepository userRepository;

    public TagEntity toEntity(Tag tag) {
        return TagEntity.builder()
            .id(tag.getId())
            .userId(tag.getUser().getId())
            .name(tag.getName())
            .createdAt(tag.getCreatedAt())
            .updatedAt(tag.getUpdatedAt())
            .version(tag.getVersion())
            .build();
    }

    public Tag toDomain(TagEntity entity) {
        return Tag.builder()
            .id(entity.getId())
            .user(userRepository.getById(entity.getUserId()))
            .name(entity.getName())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .version(entity.getVersion())
            .build();
    }
}
