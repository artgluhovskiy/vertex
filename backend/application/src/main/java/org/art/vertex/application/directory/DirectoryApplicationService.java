package org.art.vertex.application.directory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.directory.command.CreateDirectoryCommand;
import org.art.vertex.application.directory.command.UpdateDirectoryCommand;
import org.art.vertex.domain.directory.DirectoryRepository;
import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.domain.shared.time.Clock;
import org.art.vertex.domain.shared.uuid.UuidGenerator;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DirectoryApplicationService {

    private final DirectoryRepository directoryRepository;

    private final UuidGenerator uuidGenerator;

    private final Clock clock;

    @Transactional
    public Directory createDirectory(UUID userId, CreateDirectoryCommand command) {
        log.debug("Creating directory. User id: {}, name: {}", userId, command.getName());

        LocalDateTime now = clock.now();

        Directory parent = null;
        if (command.getParentId() != null) {
            parent = directoryRepository.getByIdAndUserId(command.getParentId(), userId);
        }

        Directory newDirectory = Directory.create(
            uuidGenerator.generate(),
            userId,
            command.getName(),
            parent,
            now
        );

        Directory savedDirectory = directoryRepository.save(newDirectory);

        log.info("Directory created successfully. Directory id: {}, user id: {}", savedDirectory.getId(), userId);

        return savedDirectory;
    }

    @Transactional
    public Directory updateDirectory(UUID userId, UUID dirId, UpdateDirectoryCommand command) {
        log.debug("Updating directory. Directory id: {}, user id: {}", dirId, userId);

        Directory existingDirectory = directoryRepository.getByIdAndUserId(dirId, userId);

        LocalDateTime now = clock.now();

        // Determine new parent if parentId is provided
        Directory newParent = null;
        if (command.getParentId() != null) {
            // Prevent circular references
            if (command.getParentId().equals(dirId)) {
                throw new IllegalArgumentException("Directory cannot be its own parent");
            }
            newParent = directoryRepository.getByIdAndUserId(command.getParentId(), userId);
        }

        // Determine new name (keep existing if not provided)
        String newName = command.getName() != null ? command.getName() : existingDirectory.getName();

        // Create updated directory
        Directory updatedDirectory = Directory.builder()
            .id(existingDirectory.getId())
            .userId(existingDirectory.getUserId())
            .name(newName)
            .parent(newParent != null ? newParent : existingDirectory.getParent())
            .createdTs(existingDirectory.getCreatedTs())
            .updatedTs(now)
            .version(existingDirectory.getVersion())
            .build();

        updatedDirectory = directoryRepository.save(updatedDirectory);

        log.info("Directory updated successfully. Directory id: {}", dirId);

        return updatedDirectory;
    }

    @Transactional(readOnly = true)
    public Directory getDirectory(UUID dirId, UUID userId) {
        log.debug("Fetching directory. Directory id: {}, user id: {}", dirId, userId);

        return directoryRepository.getByIdAndUserId(dirId, userId);
    }

    @Transactional(readOnly = true)
    public Directory getByDirId(UUID dirId) {
        log.debug("Fetching directory by id. Directory id: {}", dirId);

        return directoryRepository.getById(dirId);
    }

    @Transactional(readOnly = true)
    public List<Directory> getAllDirectories(UUID userId) {
        log.debug("Fetching all user directories. User id: {}", userId);

        return directoryRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Directory> getRootDirectories(UUID userId) {
        log.debug("Fetching root directories. User id: {}", userId);

        return directoryRepository.findAllRootDirectoriesByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Directory> getChildDirectories(UUID dirId, UUID userId) {
        log.debug("Fetching child directories. Directory id: {}, user id: {}", dirId, userId);

        // First verify the parent directory belongs to the user
        Directory parentDirectory = directoryRepository.getByIdAndUserId(dirId, userId);

        return directoryRepository.findByParent(parentDirectory);
    }

    @Transactional
    public void deleteDirectory(UUID dirId, UUID userId) {
        log.debug("Deleting directory. Directory id: {}, user id: {}", dirId, userId);

        // Verify the directory belongs to the user
        directoryRepository.getByIdAndUserId(dirId, userId);

        // Check if directory has children
        Directory directory = directoryRepository.getById(dirId);
        if (directoryRepository.hasChildren(directory)) {
            throw new IllegalStateException("Cannot delete directory with children. Directory id: " + dirId);
        }

        directoryRepository.deleteById(dirId);

        log.info("Directory deleted successfully. Directory id: {}", dirId);
    }
}