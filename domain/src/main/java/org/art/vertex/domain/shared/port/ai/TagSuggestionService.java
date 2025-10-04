package org.art.vertex.domain.shared.port.ai;

import org.art.vertex.domain.note.Note;
import org.art.vertex.domain.tag.Tag;

import java.util.List;

public interface TagSuggestionService {

    List<Tag> suggestTags(Note note);
}