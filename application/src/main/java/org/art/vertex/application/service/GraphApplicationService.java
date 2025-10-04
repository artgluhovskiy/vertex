package org.art.vertex.application.service;

import org.art.vertex.application.command.GraphQueryCommand;
import org.art.vertex.application.dto.GraphDto;

import java.util.List;
import java.util.UUID;

public interface GraphApplicationService {

    GraphDto getNodeGraph(UUID noteId, int depth);

    GraphDto getUserGraph(UUID userId);

    List<UUID> findShortestPath(UUID sourceNoteId, UUID targetNoteId);

    GraphDto executeGraphQuery(GraphQueryCommand command);
}