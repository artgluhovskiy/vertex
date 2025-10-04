package org.art.vertex.domain.port.ai;

import org.art.vertex.domain.model.note.Note;
import org.art.vertex.domain.model.tag.Tag;

import java.util.List;

public interface TagSuggestionService {

    List<Tag> suggestTags(Note note);
}