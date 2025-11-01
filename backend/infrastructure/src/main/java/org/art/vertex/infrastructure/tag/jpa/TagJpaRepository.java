package org.art.vertex.infrastructure.tag.jpa;

import org.art.vertex.infrastructure.tag.entity.TagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TagJpaRepository extends JpaRepository<TagEntity, UUID> {

    List<TagEntity> findAllByUserIdOrderByNameAsc(UUID userId);

    Optional<TagEntity> findByUserIdAndName(UUID userId, String name);

    boolean existsByUserIdAndName(UUID userId, String name);

    List<TagEntity> findAllByIdIn(List<UUID> tagIds);
}
