package org.art.vertex.obsidian.infrastructure.reader;

/**
 * Exception thrown when file reading operations fail during Obsidian vault migration.
 */
public class ObsidianFileReadException extends RuntimeException {

    public ObsidianFileReadException(String message) {
        super(message);
    }

    public ObsidianFileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
