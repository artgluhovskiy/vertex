package org.art.vertex.domain.tag;

import org.art.vertex.domain.tag.model.Tag;
import org.art.vertex.domain.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository {

    Tag save(Tag tag);

    Tag getById(UUID id);

    Optional<Tag> findById(UUID id);

    List<Tag> findByIdsAndUser(List<UUID> ids, User user);

    Optional<Tag> findByNameAndUser(String name, User user);

    Optional<Tag> findByNameAndUserId(String name, UUID userId);

    List<Tag> findAllByUserOrderByName(User user);

    boolean existsByNameAndUser(String name, User user);

    long countByUser(User user);

    void deleteById(UUID id);
}