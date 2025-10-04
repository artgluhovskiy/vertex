package org.art.vertex.domain.note;

public enum LinkType {
    MANUAL,     // User-created [[wikilinks]]
    SUGGESTED,  // AI-proposed connections pending user approval
    SEMANTIC    // AI-generated based on content similarity
}