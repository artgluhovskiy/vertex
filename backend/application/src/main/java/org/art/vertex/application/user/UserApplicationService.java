package org.art.vertex.application.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.domain.user.exception.UserNotFoundException;
import org.art.vertex.domain.user.model.User;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;

    public User getById(UUID userId) {
        log.debug("Fetching user by id. User id: {}", userId);

        return userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
