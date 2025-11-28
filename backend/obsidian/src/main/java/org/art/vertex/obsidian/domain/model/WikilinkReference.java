package org.art.vertex.obsidian.domain.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WikilinkReference {

    /**
     * The target note name referenced in the wikilink.
     * Example: "Note Name" from [[Note Name]]
     */
    String targetNoteName;

    /**
     * Optional display text when using alias syntax.
     * Example: "Display Text" from [[Note Name|Display Text]]
     */
    String displayText;

    /**
     * Optional anchor reference (heading or block-id).
     * Example: "Heading" from [[Note Name#Heading]]
     * Example: "block-id" from [[Note Name^block-id]]
     */
    String anchor;

    /**
     * Whether this is an embedded reference (starts with !).
     * Example: ![[image.png]] or ![[Note]]
     */
    boolean isEmbedded;

    /**
     * Line number in the source file where this wikilink was found.
     * Used for error reporting and debugging.
     */
    int lineNumber;
}
