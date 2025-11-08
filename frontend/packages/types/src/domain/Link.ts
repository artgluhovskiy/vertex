export interface NoteLink {
  id: string;
  userId: string;
  sourceNoteId: string;
  targetNoteId: string;
  type: LinkType;
  createdAt: string;
}

export type LinkType = 'manual' | 'suggested' | 'semantic';

export interface CreateLinkData {
  targetNoteId: string;
  type: LinkType;
}
