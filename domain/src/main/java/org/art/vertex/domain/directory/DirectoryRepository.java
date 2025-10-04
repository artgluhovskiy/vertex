package org.art.vertex.domain.directory;

import org.art.vertex.domain.directory.Directory;
import org.art.vertex.domain.user.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DirectoryRepository {

    Directory save(Directory directory);

    Directory getById(UUID id);

    Optional<Directory> findById(UUID id);

    /**
     * Find all root directories for a user (directories with no parent).
     *
     * @param user directory owner
     * @return list of root directories
     */
    List<Directory> findAllRootDirectoriesByUser(User user);

    /**
     * Find all child directories of a parent directory.
     *
     * @param parent parent directory
     * @return list of child directories
     */
    List<Directory> findByParent(Directory parent);

    /**
     * Find all directories for a user.
     *
     * @param user directory owner
     * @return list of all user's directories
     */
    List<Directory> findByUser(User user);

    /**
     * Get all descendant directories (recursive children).
     *
     * @param directory parent directory
     * @return list of all descendant directories
     */
    List<Directory> findDescendants(Directory directory);

    /**
     * Check if directory has any child directories.
     *
     * @param directory directory to check
     * @return true if has children, false otherwise
     */
    boolean hasChildren(Directory directory);

    void deleteById(UUID id);
}