package org.art.vertex.test.migration;

import liquibase.integration.spring.SpringLiquibase;
import org.art.vertex.test.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class LiquibaseIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private SpringLiquibase liquibase;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateUsersTable() {
        // Given: Database is initialized with Liquibase migrations

        // When: Checking if users table exists
        Integer tableCount = jdbcTemplate.queryForObject(
            """
                SELECT COUNT(*)
                FROM information_schema.tables
                WHERE table_schema = 'public'
                AND table_name = 'users'
                """,
            Integer.class
        );

        // Then: Users table should exist
        assertThat(tableCount).isEqualTo(1);
    }

    @Test
    void shouldCreateUsersTableWithCorrectColumns() {
        // Given: Database is initialized with Liquibase migrations

        // When: Checking users table columns
        var columns = jdbcTemplate.queryForList(
            """
                SELECT column_name
                FROM information_schema.columns
                WHERE table_schema = 'public'
                AND table_name = 'users'
                ORDER BY ordinal_position
                """,
            String.class
        );

        // Then: Users table should have all required columns
        assertThat(columns)
            .hasSizeGreaterThanOrEqualTo(7)
            .contains(
                "id",
                "email",
                "password_hash",
                "created_at",
                "updated_at",
                "settings",
                "version"
            );
    }
}
