package org.art.vertex.domain.repository;

import org.art.vertex.domain.model.tag.Tag;
import org.art.vertex.domain.model.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagRepository {

    Tag save(Tag tag);

    Tag getById(UUID id);

    Optional<Tag> findById(UUID id);

    Optional<Tag> findByNameAndUser(String name, User user);

    List<Tag> findAllByUserOrderByName(User user);

    boolean existsByNameAndUser(String name, User user);

    long countByUser(User user);

    void deleteById(UUID id);
}