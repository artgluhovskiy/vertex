--liquibase formatted sql

--changeset artsiomh:004-create-directories-table
CREATE TABLE directories (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    name TEXT NOT NULL,
    parent_id UUID,
    created_at TIMESTAMPTZ NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL,
    version INTEGER NOT NULL,
    CONSTRAINT fk_directories_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_directories_parent FOREIGN KEY (parent_id) REFERENCES directories(id) ON DELETE CASCADE
);

CREATE INDEX idx_directories_user_id ON directories(user_id);
CREATE INDEX idx_directories_parent_id ON directories(parent_id);
CREATE INDEX idx_directories_name ON directories(name);

--rollback DROP TABLE directories;
