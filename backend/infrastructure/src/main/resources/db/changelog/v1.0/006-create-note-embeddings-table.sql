--liquibase formatted sql

--changeset artsiomh:006-create-note-embeddings-table

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE note_embeddings (
    id UUID PRIMARY KEY,
    note_id UUID NOT NULL,

    embedding_small vector(768),
    embedding_medium vector(1024),
    embedding_large vector(1536),

    model VARCHAR(255) NOT NULL,
    dimension INTEGER NOT NULL,

    chunk_index INTEGER,
    chunk_text TEXT,

    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT fk_note_embeddings_note FOREIGN KEY (note_id)
        REFERENCES notes(id) ON DELETE CASCADE,

    CONSTRAINT note_embeddings_note_chunk_unique
        UNIQUE (note_id, chunk_index)
);

-- Create HNSW indexes for fast approximate nearest neighbor search
-- HNSW (Hierarchical Navigable Small World) parameters:
--   m = 16: max connections per layer (higher = better recall, more memory)
--   ef_construction = 64: dynamic candidate list size during build (higher = better quality, slower build)

CREATE INDEX idx_note_embeddings_small_hnsw
    ON note_embeddings
    USING hnsw (embedding_small vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

CREATE INDEX idx_note_embeddings_medium_hnsw
    ON note_embeddings
    USING hnsw (embedding_medium vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

CREATE INDEX idx_note_embeddings_large_hnsw
    ON note_embeddings
    USING hnsw (embedding_large vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

CREATE INDEX idx_note_embeddings_note_id
    ON note_embeddings(note_id);

CREATE INDEX idx_note_embeddings_note_model
    ON note_embeddings(note_id, model);

CREATE INDEX idx_note_embeddings_chunk
    ON note_embeddings(note_id, chunk_index)
    WHERE chunk_index IS NOT NULL;

--rollback DROP INDEX IF EXISTS idx_note_embeddings_chunk;
--rollback DROP INDEX IF EXISTS idx_note_embeddings_note_model;
--rollback DROP INDEX IF EXISTS idx_note_embeddings_note_id;
--rollback DROP INDEX IF EXISTS idx_note_embeddings_large_hnsw;
--rollback DROP INDEX IF EXISTS idx_note_embeddings_medium_hnsw;
--rollback DROP INDEX IF EXISTS idx_note_embeddings_small_hnsw;
--rollback DROP TABLE note_embeddings;
--rollback DROP EXTENSION IF EXISTS vector;
