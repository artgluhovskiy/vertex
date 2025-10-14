package org.art.vertex.application.tag;

import org.art.vertex.application.tag.command.CreateTagCommand;
import org.art.vertex.application.tag.command.UpdateTagCommand;
import org.art.vertex.application.tag.command.UpsertTagCommand;
import org.art.vertex.domain.tag.model.Tag;

import java.util.List;
import java.util.UUID;

public class DefaultTagApplicationService implements TagApplicationService {
    @Override
    public Tag createTag(CreateTagCommand command) {
        return null;
    }

    @Override
    public Tag updateTag(UUID tagId, UpdateTagCommand command) {
        return null;
    }

    @Override
    public List<Tag> upsertTags(UUID userId, List<UpsertTagCommand> commands) {
        return List.of();
    }

    @Override
    public Tag getTag(UUID tagId) {
        return null;
    }

    @Override
    public List<Tag> getUserTags(UUID userId) {
        return List.of();
    }

    @Override
    public List<Tag> getPopularTags(UUID userId, int limit) {
        return List.of();
    }

    @Override
    public void deleteTag(UUID tagId) {

    }
}
