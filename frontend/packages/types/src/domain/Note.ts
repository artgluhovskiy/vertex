export interface Note {
  id: string;
  userId: string;
  directoryId: string | null;
  title: string;
  content: string;
  summary: string | null;
  tags: Tag[];
  metadata: Record<string, unknown>;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface Tag {
  id: string;
  name: string;
  description: string | null;
  color: string | null;
}

export interface CreateNoteData {
  title: string;
  content: string;
  directoryId?: string | null;
  tags?: string[];
  metadata?: Record<string, unknown>;
}

export interface UpdateNoteData {
  title?: string;
  content?: string;
  directoryId?: string | null;
  tags?: string[];
  metadata?: Record<string, unknown>;
}
