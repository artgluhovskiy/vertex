export interface GraphData {
  nodes: GraphNode[];
  edges: GraphEdge[];
}

export interface GraphNode {
  id: string;
  label: string;
  type: 'note';
  tags?: string[];
}

export interface GraphEdge {
  id: string;
  source: string;
  target: string;
  type: 'manual' | 'suggested' | 'semantic';
  weight?: number;
}

export interface GraphQuery {
  noteId: string;
  depth?: number;
  includeSemanticLinks?: boolean;
}
