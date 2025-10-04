package org.art.vertex.application.note.graph;

import org.art.vertex.application.note.graph.GraphQueryCommand;
import org.art.vertex.application.note.graph.GraphDto;

import java.util.List;
import java.util.UUID;

public interface GraphApplicationService {

    GraphDto getNodeGraph(UUID noteId, int depth);

    GraphDto getUserGraph(UUID userId);

    List<UUID> findShortestPath(UUID sourceNoteId, UUID targetNoteId);

    GraphDto executeGraphQuery(GraphQueryCommand command);
}