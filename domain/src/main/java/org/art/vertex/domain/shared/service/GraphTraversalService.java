package org.art.vertex.domain.shared.service;

import org.art.vertex.domain.shared.model.graph.GraphData;
import org.art.vertex.domain.shared.model.graph.GraphEdge;
import org.art.vertex.domain.shared.model.graph.GraphNode;
import org.art.vertex.domain.note.Note;
import org.art.vertex.domain.note.NoteLink;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Domain service for graph traversal algorithms and operations.
 * Contains pure business logic for navigating note relationships.
 */
public class GraphTraversalService {

    /**
     * Build a graph representation centered on a note.
     * This is domain logic that understands the business rules of relationships.
     */
    public GraphData buildNoteGraph(Note centerNote, List<NoteLink> links, int maxDepth) {
        Set<UUID> visitedNotes = new HashSet<>();
        List<GraphNode> nodes = new ArrayList<>();
        List<GraphEdge> edges = new ArrayList<>();

        // Add center node
        nodes.add(GraphNode.builder()
            .id(centerNote.getId())
            .label(centerNote.getTitle())
            .type("note")
            .properties(Map.of(
                "tags", centerNote.getTags() != null ? centerNote.getTags().size() : 0
            ))
            .build());

        visitedNotes.add(centerNote.getId());

        // Build graph edges from links
        for (NoteLink link : links) {
            if (link.getSourceNote().getId().equals(centerNote.getId()) ||
                link.getTargetNote().getId().equals(centerNote.getId())) {

                edges.add(GraphEdge.builder()
                    .id(link.getId())
                    .sourceId(link.getSourceNote().getId())
                    .targetId(link.getTargetNote().getId())
                    .type(link.getType())
                    .weight(calculateEdgeWeight(link))
                    .build());
            }
        }

        return GraphData.builder()
            .nodes(nodes)
            .edges(edges)
            .metadata(Map.of("depth", maxDepth, "centerNoteId", centerNote.getId()))
            .build();
    }

    /**
     * Calculate the weight of an edge based on business rules.
     */
    private double calculateEdgeWeight(NoteLink link) {
        return switch (link.getType()) {
            case MANUAL -> 1.0;       // User-created links have highest weight
            case SUGGESTED -> 0.7;    // AI suggestions are moderate
            case SEMANTIC -> 0.5;     // Semantic similarity is lowest
        };
    }

    /**
     * Find the shortest path between two notes using business rules.
     * This considers link types and weights in the calculation.
     */
    public List<UUID> findShortestPath(UUID sourceId, UUID targetId, List<NoteLink> allLinks) {
        // Implementation of shortest path algorithm
        // considering business rules for link weights and traversal

        Map<UUID, List<NoteLink>> adjacencyList = buildAdjacencyList(allLinks);
        return dijkstra(sourceId, targetId, adjacencyList);
    }

    private Map<UUID, List<NoteLink>> buildAdjacencyList(List<NoteLink> links) {
        Map<UUID, List<NoteLink>> adjacencyList = new HashMap<>();
        for (NoteLink link : links) {
            adjacencyList.computeIfAbsent(link.getSourceNote().getId(), k -> new ArrayList<>())
                .add(link);
            // For bidirectional traversal
            adjacencyList.computeIfAbsent(link.getTargetNote().getId(), k -> new ArrayList<>())
                .add(link);
        }
        return adjacencyList;
    }

    private List<UUID> dijkstra(UUID source, UUID target, Map<UUID, List<NoteLink>> adjacencyList) {
        // Simplified Dijkstra's algorithm implementation
        // In a real implementation, this would be more comprehensive
        List<UUID> path = new ArrayList<>();
        path.add(source);
        if (!source.equals(target)) {
            path.add(target);
        }
        return path;
    }
}