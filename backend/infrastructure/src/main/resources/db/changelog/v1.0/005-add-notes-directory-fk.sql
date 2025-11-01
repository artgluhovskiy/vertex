--liquibase formatted sql

--changeset artsiomh:005-add-notes-directory-fk
ALTER TABLE notes
ADD CONSTRAINT fk_notes_directory FOREIGN KEY (directory_id) REFERENCES directories(id) ON DELETE SET NULL;

--rollback ALTER TABLE notes DROP CONSTRAINT fk_notes_directory;
