package org.art.vertex.web.directory.mapper;

import org.art.vertex.application.directory.command.CreateDirectoryCommand;
import org.art.vertex.application.directory.command.UpdateDirectoryCommand;
import org.art.vertex.web.directory.request.CreateDirectoryRequest;
import org.art.vertex.web.directory.request.UpdateDirectoryRequest;

public class DirectoryCommandMapper {

    public CreateDirectoryCommand toCommand(CreateDirectoryRequest request) {
        return CreateDirectoryCommand.builder()
            .name(request.name())
            .parentId(request.parentId())
            .build();
    }

    public UpdateDirectoryCommand toCommand(UpdateDirectoryRequest request) {
        return UpdateDirectoryCommand.builder()
            .name(request.name())
            .parentId(request.parentId())
            .build();
    }
}
