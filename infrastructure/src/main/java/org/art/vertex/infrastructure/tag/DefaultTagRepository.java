package org.art.vertex.infrastructure.tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.tag.TagRepository;
import org.art.vertex.domain.tag.model.Tag;
import org.art.vertex.domain.user.model.User;
import org.art.vertex.infrastructure.tag.entity.TagEntity;
import org.art.vertex.infrastructure.tag.jpa.TagJpaRepository;
import org.art.vertex.infrastructure.tag.mapper.TagEntityMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DefaultTagRepository implements TagRepository {

    private final TagJpaRepository tagJpaRepository;

    private final TagEntityMapper tagEntityMapper;

    @Override
    @Transactional
    public Tag save(Tag tag) {
        log.debug("Saving tag. Tag id: {}, name: {}", tag.getId(), tag.getName());

        TagEntity entity = tagEntityMapper.toEntity(tag);
        TagEntity savedEntity = tagJpaRepository.save(entity);

        log.debug("Tag saved successfully. Tag id: {}", savedEntity.getId());

        return tagEntityMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Tag getById(UUID id) {
        log.debug("Fetching tag by id. Tag id: {}", id);

        TagEntity entity = tagJpaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Tag not found. Id: " + id));

        return tagEntityMapper.toDomain(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> findById(UUID id) {
        log.debug("Finding tag by id. Tag id: {}", id);

        return tagJpaRepository.findById(id)
            .map(tagEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findByIdsAndUser(List<UUID> ids, User user) {
        log.debug("Finding tags by ids and user. Ids count: {}, user id: {}", ids.size(), user.getId());

        return tagJpaRepository.findAllByIdIn(ids).stream()
            .filter(entity -> entity.getUserId().equals(user.getId()))
            .map(tagEntityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> findByNameAndUser(String name, User user) {
        log.debug("Finding tag by name and user. Name: {}, user id: {}", name, user.getId());

        return tagJpaRepository.findByUserIdAndName(user.getId(), name)
            .map(tagEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Tag> findByNameAndUserId(String name, UUID userId) {
        log.debug("Finding tag by name and user id. Name: {}, user id: {}", name, userId);

        return tagJpaRepository.findByUserIdAndName(userId, name)
            .map(tagEntityMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Tag> findAllByUserOrderByName(User user) {
        log.debug("Finding all tags by user. User id: {}", user.getId());

        return tagJpaRepository.findAllByUserIdOrderByNameAsc(user.getId()).stream()
            .map(tagEntityMapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByNameAndUser(String name, User user) {
        log.debug("Checking if tag exists by name and user. Name: {}, user id: {}", name, user.getId());

        return tagJpaRepository.existsByUserIdAndName(user.getId(), name);
    }

    @Override
    @Transactional(readOnly = true)
    public long countByUser(User user) {
        log.debug("Counting tags by user. User id: {}", user.getId());

        return tagJpaRepository.findAllByUserIdOrderByNameAsc(user.getId()).size();
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        log.debug("Deleting tag by id. Tag id: {}", id);

        tagJpaRepository.deleteById(id);

        log.info("Tag deleted successfully. Tag id: {}", id);
    }
}
