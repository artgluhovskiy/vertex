package org.art.vertex.web.directory;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.art.vertex.application.directory.DirectoryApplicationService;
import org.art.vertex.application.directory.command.CreateDirectoryCommand;
import org.art.vertex.application.directory.command.UpdateDirectoryCommand;
import org.art.vertex.domain.directory.model.Directory;
import org.art.vertex.web.directory.dto.DirectoryDto;
import org.art.vertex.web.directory.mapper.DirectoryCommandMapper;
import org.art.vertex.web.directory.mapper.DirectoryDtoMapper;
import org.art.vertex.web.directory.request.CreateDirectoryRequest;
import org.art.vertex.web.directory.request.UpdateDirectoryRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/directories")
@RequiredArgsConstructor
public class DirectoryController {

    private final DirectoryApplicationService directoryService;

    private final DirectoryCommandMapper directoryCommandMapper;

    private final DirectoryDtoMapper directoryDtoMapper;

    @PostMapping
    public ResponseEntity<DirectoryDto> createDirectory(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody CreateDirectoryRequest request
    ) {
        log.trace("Processing create directory request. User id: {}, name: {}", userId, request.name());

        CreateDirectoryCommand command = directoryCommandMapper.toCommand(request);

        Directory createdDirectory = directoryService.createDirectory(UUID.fromString(userId), command);

        DirectoryDto createdDirectoryDto = directoryDtoMapper.toDto(createdDirectory);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdDirectoryDto);
    }

    @PutMapping("/{dirId}")
    public ResponseEntity<DirectoryDto> updateDirectory(
        @AuthenticationPrincipal String userId,
        @PathVariable UUID dirId,
        @Valid @RequestBody UpdateDirectoryRequest request
    ) {
        log.trace("Processing update directory request. Directory id: {}, user id: {}", dirId, userId);

        UpdateDirectoryCommand command = directoryCommandMapper.toCommand(request);

        Directory updatedDirectory = directoryService.updateDirectory(UUID.fromString(userId), dirId, command);

        DirectoryDto updatedDirectoryDto = directoryDtoMapper.toDto(updatedDirectory);

        return ResponseEntity.ok(updatedDirectoryDto);
    }

    @DeleteMapping("/{dirId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDirectory(
        @PathVariable UUID dirId,
        @AuthenticationPrincipal String userId
    ) {
        log.trace("Processing delete directory request. Directory id: {}, user id: {}", dirId, userId);

        directoryService.deleteDirectory(dirId, UUID.fromString(userId));
    }

    @GetMapping("/{dirId}")
    public ResponseEntity<DirectoryDto> getDirectory(
        @PathVariable UUID dirId,
        @AuthenticationPrincipal String userId
    ) {
        log.trace("Fetching directory. Directory id: {}, user id: {}", dirId, userId);

        Directory directory = directoryService.getDirectory(dirId, UUID.fromString(userId));

        DirectoryDto directoryDto = directoryDtoMapper.toDto(directory);

        return ResponseEntity.ok(directoryDto);
    }

    @GetMapping
    public ResponseEntity<List<DirectoryDto>> getUserDirectories(@AuthenticationPrincipal String userId) {
        log.trace("Fetching directories for user. User id: {}", userId);

        List<Directory> directories = directoryService.getAllDirectories(UUID.fromString(userId));

        List<DirectoryDto> directoryDtos = directories.stream()
            .map(directoryDtoMapper::toDto)
            .toList();

        return ResponseEntity.ok(directoryDtos);
    }

    @GetMapping("/root")
    public ResponseEntity<List<DirectoryDto>> getRootDirectories(@AuthenticationPrincipal String userId) {
        log.trace("Fetching root directories for user. User id: {}", userId);

        List<Directory> rootDirectories = directoryService.getRootDirectories(UUID.fromString(userId));

        List<DirectoryDto> directoryDtos = rootDirectories.stream()
            .map(directoryDtoMapper::toDto)
            .toList();

        return ResponseEntity.ok(directoryDtos);
    }

    @GetMapping("/{dirId}/children")
    public ResponseEntity<List<DirectoryDto>> getChildDirectories(
        @PathVariable UUID dirId,
        @AuthenticationPrincipal String userId
    ) {
        log.trace("Fetching child directories. Directory id: {}, user id: {}", dirId, userId);

        List<Directory> childDirectories = directoryService.getChildDirectories(dirId, UUID.fromString(userId));

        List<DirectoryDto> directoryDtos = childDirectories.stream()
            .map(directoryDtoMapper::toDto)
            .toList();

        return ResponseEntity.ok(directoryDtos);
    }
}
