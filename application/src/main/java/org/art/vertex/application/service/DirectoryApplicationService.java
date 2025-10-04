package org.art.vertex.application.service;

import org.art.vertex.application.command.CreateDirectoryCommand;
import org.art.vertex.application.command.MoveDirectoryCommand;
import org.art.vertex.application.command.RenameDirectoryCommand;
import org.art.vertex.application.dto.DirectoryDto;
import org.art.vertex.application.dto.DirectoryTreeDto;

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