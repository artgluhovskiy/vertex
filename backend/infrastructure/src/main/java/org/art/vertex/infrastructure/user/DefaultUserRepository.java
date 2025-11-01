package org.art.vertex.infrastructure.user;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.domain.user.exception.UserNotFoundException;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.infrastructure.user.entity.UserEntity;
import org.art.vertex.infrastructure.user.jpa.UserJpaRepository;
import org.art.vertex.infrastructure.user.mapper.UserEntityMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class DefaultUserRepository implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    private final UserEntityMapper userMapper;

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity savedEntity = userJpaRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(UUID id) {
        return findById(id)
            .orElseThrow(() ->
                new UserNotFoundException("User cannot be found. User id: %s".formatted(id.toString()))
            );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return userJpaRepository.findById(id)
            .map(userMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return findByEmail(email)
            .orElseThrow(() ->
                new UserNotFoundException("User cannot be found by email. User email: %s".formatted(email))
            );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email)
            .map(userMapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }
}
