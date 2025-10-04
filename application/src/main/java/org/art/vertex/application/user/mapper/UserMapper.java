package org.art.vertex.application.user.mapper;

import org.art.vertex.application.user.dto.UserDto;
import org.art.vertex.domain.user.model.User;

public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
            .id(user.getId().toString())
            .email(user.getEmail())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
