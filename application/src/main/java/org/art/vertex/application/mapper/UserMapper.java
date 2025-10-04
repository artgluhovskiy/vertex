package org.art.vertex.application.mapper;

import org.art.vertex.application.dto.UserDto;
import org.art.vertex.domain.model.user.User;

public class UserMapper {

    public UserDto toDto(User user) {
        return UserDto.builder()
            .id(user.getId().toString())
            .email(user.getEmail())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
