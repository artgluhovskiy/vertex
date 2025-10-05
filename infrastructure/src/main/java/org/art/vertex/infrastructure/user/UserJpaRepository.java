package org.art.vertex.infrastructure.user;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.user.exception.UserNotFoundException;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.domain.user.UserRepository;
import org.art.vertex.infrastructure.user.entity.UserEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UserJpaRepository implements UserRepository {

    private final UserEntityRepository userEntityRepository;
    private final UserEntityMapper userMapper;

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity savedEntity = userEntityRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public User getById(UUID id) {
        return findById(id)
            .orElseThrow(() -> new UserNotFoundException(id.toString()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UUID id) {
        return userEntityRepository.findById(id)
            .map(userMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userEntityRepository.findByEmail(email)
            .map(userMapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        userEntityRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userEntityRepository.existsByEmail(email);
    }
}
