package org.art.vertex.infrastructure.directory;

import lombok.RequiredArgsConstructor;
import org.art.vertex.domain.directory.DirectoryRepository;
import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.user.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class DefaultDirectoryRepository implements DirectoryRepository {

    @Override
    public Directory save(Directory directory) {
        return null;
    }

    @Override
    public Directory getById(UUID id) {
        return null;
    }

    @Override
    public Directory getByIdAndUser(UUID id, User user) {
        return null;
    }

    @Override
    public Optional<Directory> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public Optional<Directory> findByIdAndUser(UUID id, User user) {
        return Optional.empty();
    }

    @Override
    public List<Directory> findAllRootDirectoriesByUser(User user) {
        return List.of();
    }

    @Override
    public List<Directory> findByParent(Directory parent) {
        return List.of();
    }

    @Override
    public List<Directory> findByUser(User user) {
        return List.of();
    }

    @Override
    public List<Directory> findDescendants(Directory directory) {
        return List.of();
    }

    @Override
    public boolean hasChildren(Directory directory) {
        return false;
    }

    @Override
    public void deleteById(UUID id) {

    }
}
