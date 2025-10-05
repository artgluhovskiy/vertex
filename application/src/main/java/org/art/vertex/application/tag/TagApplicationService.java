package org.art.vertex.application.tag;

import org.art.vertex.application.tag.command.CreateTagCommand;
import org.art.vertex.application.tag.command.UpdateTagCommand;
import org.art.vertex.domain.tag.model.Tag;

import java.util.List;
import java.util.UUID;

public interface TagApplicationService {

    Tag createTag(CreateTagCommand command);

    Tag updateTag(UUID tagId, UpdateTagCommand command);

    Tag getTag(UUID tagId);

    List<Tag> getUserTags(UUID userId);

    List<Tag> getPopularTags(UUID userId, int limit);

    void deleteTag(UUID tagId);
}