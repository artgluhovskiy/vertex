package org.art.vertex.web.user.dto;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
    String accessToken,
    String tokenType,
    UserDto user
) {
    public AuthenticationResponse(String accessToken, String tokenType, UserDto user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.user = user;
    }
}
