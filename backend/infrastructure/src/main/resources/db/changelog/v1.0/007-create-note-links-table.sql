--liquibase formatted sql

--changeset artsiomh:007-create-note-links-table
CREATE TABLE note_links (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    source_note_id UUID NOT NULL,
    target_note_id UUID NOT NULL,
    type VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_note_links_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_note_links_source FOREIGN KEY (source_note_id) REFERENCES notes(id) ON DELETE CASCADE,
    CONSTRAINT fk_note_links_target FOREIGN KEY (target_note_id) REFERENCES notes(id) ON DELETE CASCADE,
    CONSTRAINT unique_note_link UNIQUE (source_note_id, target_note_id, type)
);

CREATE INDEX idx_note_links_user_id ON note_links(user_id);
CREATE INDEX idx_note_links_source_note_id ON note_links(source_note_id);
CREATE INDEX idx_note_links_target_note_id ON note_links(target_note_id);

--rollback DROP TABLE note_links;
