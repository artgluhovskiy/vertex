package org.art.vertex.application.service;

import org.art.vertex.application.command.CreateTagCommand;
import org.art.vertex.application.command.UpdateTagCommand;
import org.art.vertex.application.dto.TagDto;

import java.util.List;
import java.util.UUID;

public interface TagApplicationService {

    TagDto createTag(CreateTagCommand command);

    TagDto updateTag(UUID tagId, UpdateTagCommand command);

    TagDto getTag(UUID tagId);

    List<TagDto> getUserTags(UUID userId);

    List<TagDto> getPopularTags(UUID userId, int limit);

    void deleteTag(UUID tagId);
}