export interface Directory {
  id: string;
  userId: string;
  name: string;
  parentId: string | null;
  createdAt: string;
}

export interface CreateDirectoryData {
  name: string;
  parentId?: string | null;
}

export interface UpdateDirectoryData {
  name?: string;
  parentId?: string | null;
}
