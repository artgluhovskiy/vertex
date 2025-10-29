package org.art.vertex.infrastructure.directory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.domain.directory.DirectoryRepository;
import org.art.vertex.domain.directory.exception.DirectoryNotFoundException;
import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.infrastructure.directory.entity.DirectoryEntity;
import org.art.vertex.infrastructure.directory.jpa.DirectoryJpaRepository;
import org.art.vertex.infrastructure.directory.mapper.DirectoryEntityMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DefaultDirectoryRepository implements DirectoryRepository {

    private final DirectoryJpaRepository directoryJpaRepository;

    private final DirectoryEntityMapper directoryMapper;

    @Override
    @Transactional
    public Directory save(Directory directory) {
        DirectoryEntity entity = directoryMapper.toEntity(directory);
        DirectoryEntity savedEntity = directoryJpaRepository.save(entity);
        return directoryMapper.toDomain(savedEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Directory getById(UUID id) {
        return findById(id)
            .orElseThrow(() ->
                new DirectoryNotFoundException("Directory cannot be found. Directory id: %s".formatted(id))
            );
    }

    @Override
    @Transactional(readOnly = true)
    public Directory getByIdAndUserId(UUID id, UUID userId) {
        return findByIdAndUserId(id, userId)
            .orElseThrow(() ->
                new DirectoryNotFoundException("Directory cannot be found. Directory id: %s, user id: %s"
                    .formatted(id, userId))
            );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Directory> findById(UUID id) {
        return directoryJpaRepository.findById(id)
            .map(directoryMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Directory> findByIdAndUserId(UUID id, UUID userId) {
        return directoryJpaRepository.findByIdAndUserId(id, userId)
            .map(directoryMapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Directory> findAllRootDirectoriesByUserId(UUID userId) {
        return directoryJpaRepository.findAllRootDirectoriesByUserId(userId)
            .stream()
            .map(directoryMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Directory> findByParent(Directory parent) {
        return directoryJpaRepository.findByParentId(parent.getId())
            .stream()
            .map(directoryMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Directory> findByUserId(UUID userId) {
        return directoryJpaRepository.findByUserId(userId)
            .stream()
            .map(directoryMapper::toDomain)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Directory> findDescendants(Directory directory) {
        List<Directory> descendants = new ArrayList<>();
        collectDescendants(directory, descendants);
        return descendants;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasChildren(Directory directory) {
        return directoryJpaRepository.existsByParentId(directory.getId());
    }

    @Override
    @Transactional
    public void deleteById(UUID id) {
        DirectoryEntity entity = directoryJpaRepository.findById(id)
            .orElseThrow(() ->
                new DirectoryNotFoundException("Directory cannot be found. Directory id: %s".formatted(id))
            );

        directoryJpaRepository.delete(entity);
    }

    private void collectDescendants(Directory parent, List<Directory> descendants) {
        List<Directory> children = findByParent(parent);
        descendants.addAll(children);

        for (Directory child : children) {
            collectDescendants(child, descendants);
        }
    }
}

