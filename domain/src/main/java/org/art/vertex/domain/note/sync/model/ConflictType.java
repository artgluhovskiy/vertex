package org.art.vertex.domain.note.sync.model;

public enum ConflictType {
    VERSION_MISMATCH,
    CONTENT_CONFLICT,
    DELETE_CONFLICT,
    METADATA_CONFLICT
}