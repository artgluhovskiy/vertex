package org.art.vertex.infrastructure.note.link.jpa;

import org.art.vertex.infrastructure.note.link.entity.NoteLinkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteLinkJpaRepository extends JpaRepository<NoteLinkEntity, UUID> {

    @Query("""
        SELECT COUNT(nl) > 0 FROM NoteLinkEntity nl
        WHERE nl.sourceNoteId = :sourceNoteId
        AND nl.targetNoteId = :targetNoteId
        AND nl.userId = :userId
        """)
    boolean existsBetweenNotes(
        @Param("sourceNoteId") UUID sourceNoteId,
        @Param("targetNoteId") UUID targetNoteId,
        @Param("userId") UUID userId
    );

    List<NoteLinkEntity> findAllByUserId(UUID userId);

    List<NoteLinkEntity> findAllBySourceNoteId(UUID sourceNoteId);

    List<NoteLinkEntity> findAllByTargetNoteId(UUID targetNoteId);
}
