package org.art.vertex.application.note.graph;

import org.art.vertex.application.note.graph.command.GraphQueryCommand;
import org.art.vertex.domain.note.graph.model.GraphData;

import java.util.List;
import java.util.UUID;

public interface GraphApplicationService {

    GraphData getNodeGraph(UUID noteId, int depth);

    GraphData getUserGraph(UUID userId);

    List<UUID> findShortestPath(UUID sourceNoteId, UUID targetNoteId);

    GraphData executeGraphQuery(GraphQueryCommand command);
}