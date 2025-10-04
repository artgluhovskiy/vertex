package org.art.vertex.domain.model.sync;

public enum ConflictType {
    VERSION_MISMATCH,
    CONTENT_CONFLICT,
    DELETE_CONFLICT,
    METADATA_CONFLICT
}