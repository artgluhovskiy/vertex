--liquibase formatted sql

--changeset artsiomh:002-create-notes-table
CREATE TABLE notes (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    directory_id UUID,
    title TEXT NOT NULL,
    content TEXT,
    summary TEXT,
    metadata JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version INTEGER NOT NULL,
    CONSTRAINT fk_notes_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_notes_user_id ON notes(user_id);
CREATE INDEX idx_notes_directory_id ON notes(directory_id);
CREATE INDEX idx_notes_updated_at ON notes(updated_at DESC);

--rollback DROP TABLE notes;
