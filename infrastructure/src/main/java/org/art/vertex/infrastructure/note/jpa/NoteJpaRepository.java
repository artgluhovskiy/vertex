package org.art.vertex.infrastructure.note.jpa;

import org.art.vertex.infrastructure.note.entity.NoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NoteJpaRepository extends JpaRepository<NoteEntity, UUID> {

    List<NoteEntity> findAllByUserIdOrderByUpdatedAtDesc(UUID userId);

    List<NoteEntity> findAllByDirectoryId(UUID directoryId);
}
