package org.art.vertex.obsidian.infrastructure.reader;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class DefaultObsidianFileReader implements ObsidianFileReader {

    private static final String MARKDOWN_EXTENSION = ".md";

    @Override
    public List<Path> discoverMarkdownFiles(Path vaultPath) {
        log.debug("Discovering markdown files in vault: {}", vaultPath);

        validateVaultPath(vaultPath);

        try (Stream<Path> paths = Files.walk(vaultPath)) {
            return paths
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(MARKDOWN_EXTENSION))
                .toList();
        } catch (IOException e) {
            log.error("Failed to discover markdown files in vault: {}", vaultPath, e);
            throw new ObsidianFileReadException("Failed to discover markdown files in vault: " + vaultPath, e);
        }
    }

    @Override
    public String readFile(Path filePath) {
        log.debug("Reading file: {}", filePath);

        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            log.error("Failed to read file: {}", filePath, e);
            throw new ObsidianFileReadException("Failed to read file: " + filePath, e);
        }
    }

    @Override
    public LocalDateTime getFileCreatedTime(Path filePath) {
        try {
            return LocalDateTime.ofInstant(
                Files.readAttributes(filePath, java.nio.file.attribute.BasicFileAttributes.class)
                    .creationTime()
                    .toInstant(),
                ZoneId.systemDefault()
            );
        } catch (IOException e) {
            log.warn("Failed to get creation time for file: {}. Using current time.", filePath, e);
            return LocalDateTime.now();
        }
    }

    @Override
    public LocalDateTime getFileModifiedTime(Path filePath) {
        try {
            return LocalDateTime.ofInstant(
                Files.getLastModifiedTime(filePath).toInstant(),
                ZoneId.systemDefault()
            );
        } catch (IOException e) {
            log.warn("Failed to get modification time for file: {}. Using current time.", filePath, e);
            return LocalDateTime.now();
        }
    }

    private void validateVaultPath(Path vaultPath) {
        if (!Files.exists(vaultPath)) {
            throw new ObsidianFileReadException("Vault path does not exist: " + vaultPath);
        }

        if (!Files.isDirectory(vaultPath)) {
            throw new ObsidianFileReadException("Vault path is not a directory: " + vaultPath);
        }
    }
}
