package org.art.vertex.infrastructure.directory.jpa;

import org.art.vertex.infrastructure.directory.entity.DirectoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DirectoryJpaRepository extends JpaRepository<DirectoryEntity, UUID> {

    @Query("""
        SELECT d FROM DirectoryEntity d
        WHERE d.id = :dirId
        AND d.userId = :userId
        """)
    Optional<DirectoryEntity> findByIdAndUserId(
        @Param("dirId") UUID dirId,
        @Param("userId") UUID userId
    );

    @Query("""
        SELECT d FROM DirectoryEntity d
        WHERE d.userId = :userId
        AND d.parentId IS NULL
        ORDER BY d.name ASC
        """)
    List<DirectoryEntity> findAllRootDirectoriesByUserId(
        @Param("userId") UUID userId
    );

    @Query("""
        SELECT d FROM DirectoryEntity d
        WHERE d.parentId = :parentId
        ORDER BY d.name ASC
        """)
    List<DirectoryEntity> findByParentId(
        @Param("parentId") UUID parentId
    );

    @Query("""
        SELECT d FROM DirectoryEntity d
        WHERE d.userId = :userId
        ORDER BY d.name ASC
        """)
    List<DirectoryEntity> findByUserId(
        @Param("userId") UUID userId
    );

    @Query("""
        SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
        FROM DirectoryEntity d
        WHERE d.parentId = :parentId
        """)
    boolean existsByParentId(
        @Param("parentId") UUID parentId
    );
}
