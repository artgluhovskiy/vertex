package org.art.vertex.application.user.model;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.user.model.User;

@Value
@Builder
public class AuthenticationResult {
    String accessToken;
    User user;
}
