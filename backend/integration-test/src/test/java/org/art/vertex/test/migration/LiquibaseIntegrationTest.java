package org.art.vertex.test.migration;

import org.art.vertex.test.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LiquibaseIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldCreateUsersTable() {
        // WHEN
        var tableCount = jdbcTemplate.queryForObject(
            """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                AND table_name = 'users'
                """,
            Integer.class
        );

        // THEN
        assertThat(tableCount).isEqualTo(1);
    }

    @Test
    void shouldCreateNotesTable() {
        // WHEN
        var tableCount = jdbcTemplate.queryForObject(
            """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                AND table_name = 'notes'
                """,
            Integer.class
        );

        // THEN
        assertThat(tableCount).isEqualTo(1);
    }

    @Test
    void shouldCreateDirectoriesTable() {
        // WHEN
        var tableCount = jdbcTemplate.queryForObject(
            """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                AND table_name = 'directories'
                """,
            Integer.class
        );

        // THEN
        assertThat(tableCount).isEqualTo(1);
    }
}

