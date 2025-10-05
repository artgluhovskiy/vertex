package org.art.vertex.application.directory;

import org.art.vertex.application.directory.command.CreateDirectoryCommand;
import org.art.vertex.application.directory.command.MoveDirectoryCommand;
import org.art.vertex.application.directory.command.RenameDirectoryCommand;
import org.art.vertex.domain.directory.model.Directory;

import java.util.List;
import java.util.UUID;

public interface DirectoryApplicationService {

    Directory createDirectory(CreateDirectoryCommand command);

    Directory renameDirectory(UUID directoryId, RenameDirectoryCommand command);

    Directory moveDirectory(UUID directoryId, MoveDirectoryCommand command);

    Directory getDirectory(UUID directoryId);

    List<Directory> getUserRootDirectories(UUID userId);

    Directory getDirectoryTree(UUID directoryId);

    void deleteDirectory(UUID directoryId);
}