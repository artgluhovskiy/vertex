export interface Tag {
  id: string;
  userId: string;
  name: string;
  description: string | null;
  color: string | null;
  createdAt: string;
  updatedAt: string;
  version: number;
}

export interface CreateTagData {
  name: string;
  description?: string;
  color?: string;
}

export interface UpdateTagData {
  name?: string;
  description?: string;
  color?: string;
}
