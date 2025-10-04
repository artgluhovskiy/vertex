package org.art.vertex.application.directory;

import org.art.vertex.application.directory.command.CreateDirectoryCommand;
import org.art.vertex.application.directory.command.MoveDirectoryCommand;
import org.art.vertex.application.directory.command.RenameDirectoryCommand;
import org.art.vertex.application.directory.dto.DirectoryDto;
import org.art.vertex.application.directory.dto.DirectoryTreeDto;

import java.util.List;
import java.util.UUID;

public interface DirectoryApplicationService {

    DirectoryDto createDirectory(CreateDirectoryCommand command);

    DirectoryDto renameDirectory(UUID directoryId, RenameDirectoryCommand command);

    DirectoryDto moveDirectory(UUID directoryId, MoveDirectoryCommand command);

    DirectoryDto getDirectory(UUID directoryId);

    List<DirectoryDto> getUserRootDirectories(UUID userId);

    DirectoryTreeDto getDirectoryTree(UUID directoryId);

    void deleteDirectory(UUID directoryId);
}