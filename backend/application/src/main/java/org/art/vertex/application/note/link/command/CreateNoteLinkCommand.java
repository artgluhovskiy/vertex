package org.art.vertex.application.note.link.command;

import lombok.Builder;
import lombok.Value;
import org.art.vertex.domain.note.model.LinkType;

import java.util.UUID;

@Value
@Builder
public class CreateNoteLinkCommand {

    UUID sourceNoteId;

    UUID targetNoteId;

    @Builder.Default
    LinkType type = LinkType.MANUAL;
}
