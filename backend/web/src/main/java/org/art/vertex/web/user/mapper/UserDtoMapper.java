package org.art.vertex.web.user.mapper;

import org.art.vertex.domain.user.model.User;
import org.art.vertex.web.user.dto.UserDto;

public class UserDtoMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
            .id(user.getId().toString())
            .email(user.getEmail())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
