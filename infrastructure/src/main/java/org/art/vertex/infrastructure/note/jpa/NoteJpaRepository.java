package org.art.vertex.infrastructure.note.jpa;

import org.art.vertex.infrastructure.note.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteJpaRepository extends JpaRepository<NoteEntity, UUID> {

    List<NoteEntity> findAllByUserIdOrderByUpdatedAtDesc(UUID userId);

    List<NoteEntity> findAllByDirectoryId(UUID directoryId);

    @Query("""
        SELECT DISTINCT n FROM NoteEntity n
        JOIN n.tags t
        WHERE n.userId = :userId
        AND t.id IN :tagIds
        ORDER BY n.updatedAt DESC
        """)
    List<NoteEntity> findAllByUserIdAndTagIds(
        @Param("userId") UUID userId,
        @Param("tagIds") List<UUID> tagIds
    );

    @Query("""
        SELECT DISTINCT n FROM NoteEntity n
        JOIN n.tags t
        WHERE n.userId = :userId
        AND t.name IN :tagNames
        ORDER BY n.updatedAt DESC
        """)
    List<NoteEntity> findAllByUserIdAndTagNames(
        @Param("userId") UUID userId,
        @Param("tagNames") List<String> tagNames
    );
}
