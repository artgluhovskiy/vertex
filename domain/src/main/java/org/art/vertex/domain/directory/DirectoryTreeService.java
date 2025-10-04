package org.art.vertex.domain.directory;

import org.art.vertex.domain.directory.Directory;

import java.util.ArrayList;
import java.util.List;

/**
 * Domain service for directory hierarchy operations.
 * Contains business logic for managing the directory tree structure.
 */
public class DirectoryTreeService {

    /**
     * Calculate the full path of a directory by traversing up the hierarchy.
     */
    public String calculatePath(Directory directory) {
        if (directory == null) {
            return "/";
        }

        List<String> pathComponents = new ArrayList<>();
        Directory current = directory;

        while (current != null) {
            pathComponents.add(0, current.getName());
            current = current.getParent();
        }

        return "/" + String.join("/", pathComponents);
    }

    /**
     * Validate that moving a directory won't create a cycle.
     * Business rule: A directory cannot be moved to its own descendant.
     */
    public boolean canMoveDirectory(Directory directory, Directory newParent) {
        if (directory == null || directory.equals(newParent)) {
            return false;
        }

        // Check if newParent is a descendant of directory
        Directory current = newParent;
        while (current != null) {
            if (current.equals(directory)) {
                return false; // Would create a cycle
            }
            current = current.getParent();
        }

        return true;
    }

    /**
     * Calculate the depth of a directory in the hierarchy.
     */
    public int calculateDepth(Directory directory) {
        int depth = 0;
        Directory current = directory;

        while (current != null && current.getParent() != null) {
            depth++;
            current = current.getParent();
        }

        return depth;
    }

    /**
     * Check if a directory name is valid according to business rules.
     */
    public boolean isValidDirectoryName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // Business rules for directory names
        if (name.length() > 255) {
            return false;
        }

        // Disallow certain characters
        String[] invalidChars = {"/", "\\", ":", "*", "?", "\"", "<", ">", "|"};
        for (String invalidChar : invalidChars) {
            if (name.contains(invalidChar)) {
                return false;
            }
        }

        return true;
    }
}