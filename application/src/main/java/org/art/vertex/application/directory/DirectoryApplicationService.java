package org.art.vertex.application.directory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.directory.command.CreateDirectoryCommand;
import org.art.vertex.domain.directory.model.Directory;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class DirectoryApplicationService {

    public Directory createDirectory(CreateDirectoryCommand command) {
        return null;
    }

    public Directory getByDirId(UUID dirId) {
        return null;
    }

    void deleteDirectory(UUID dirId) {

    }
}