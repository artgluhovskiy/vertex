-- Seed test user for development
-- This script creates a test user with sample folders and notes
--
-- TEST USER CREDENTIALS:
-- Email: test@example.com
-- Password: Test123!
--
-- Password hash generated with BCrypt (strength 10)

-- Clear existing test data (if any)
DO $$
BEGIN
    -- Delete test user and cascade will delete related data
    DELETE FROM users WHERE email = 'test@example.com';
    RAISE NOTICE 'Cleared existing test user data';
END
$$;

-- Create test user
-- Password: Test123! (hashed with BCrypt strength 12)
INSERT INTO users (id, email, password_hash, created_at, updated_at, settings, version)
VALUES (
    '00000000-0000-0000-0000-000000000001'::uuid,
    'test@example.com',
    '$2a$12$wlo1SIKpHDG62vqBdTgzDe5wUMu6I.HkYRG/8zHTXHzHkKeTM0m8G', -- BCrypt hash for "Test123!" with strength 12
    NOW(),
    NOW(),
    '{
        "theme": "dark",
        "aiFeatures": true,
        "language": "en"
    }'::jsonb,
    0
);

-- Create ROOT directory (required for all users)
INSERT INTO directories (id, user_id, name, parent_id, created_at, updated_at, version)
VALUES (
    '10000000-0000-0000-0000-000000000001'::uuid,
    '00000000-0000-0000-0000-000000000001'::uuid,
    'Folders',
    NULL,
    NOW(),
    NOW(),
    0
);

-- Create sample directory structure
INSERT INTO directories (id, user_id, name, parent_id, created_at, updated_at, version)
VALUES
    -- Work folder
    (
        '10000000-0000-0000-0000-000000000002'::uuid,
        '00000000-0000-0000-0000-000000000001'::uuid,
        'Work',
        '10000000-0000-0000-0000-000000000001'::uuid,
        NOW() - INTERVAL '7 days',
        NOW() - INTERVAL '7 days',
        0
    ),
    -- Work > Projects subfolder
    (
        '10000000-0000-0000-0000-000000000003'::uuid,
        '00000000-0000-0000-0000-000000000001'::uuid,
        'Projects',
        '10000000-0000-0000-0000-000000000002'::uuid,
        NOW() - INTERVAL '7 days',
        NOW() - INTERVAL '7 days',
        0
    ),
    -- Work > Meetings subfolder
    (
        '10000000-0000-0000-0000-000000000004'::uuid,
        '00000000-0000-0000-0000-000000000001'::uuid,
        'Meetings',
        '10000000-0000-0000-0000-000000000002'::uuid,
        NOW() - INTERVAL '7 days',
        NOW() - INTERVAL '7 days',
        0
    ),
    -- Personal folder
    (
        '10000000-0000-0000-0000-000000000005'::uuid,
        '00000000-0000-0000-0000-000000000001'::uuid,
        'Personal',
        '10000000-0000-0000-0000-000000000001'::uuid,
        NOW() - INTERVAL '14 days',
        NOW() - INTERVAL '14 days',
        0
    ),
    -- Personal > Journal subfolder
    (
        '10000000-0000-0000-0000-000000000006'::uuid,
        '00000000-0000-0000-0000-000000000001'::uuid,
        'Journal',
        '10000000-0000-0000-0000-000000000005'::uuid,
        NOW() - INTERVAL '14 days',
        NOW() - INTERVAL '14 days',
        0
    ),
    -- Archive folder
    (
        '10000000-0000-0000-0000-000000000007'::uuid,
        '00000000-0000-0000-0000-000000000001'::uuid,
        'Archive',
        '10000000-0000-0000-0000-000000000001'::uuid,
        NOW() - INTERVAL '30 days',
        NOW() - INTERVAL '30 days',
        0
    );

-- Create sample notes
INSERT INTO notes (id, user_id, directory_id, title, content, created_at, updated_at, version)
VALUES
    -- Notes in Work/Projects
    (
        '20000000-0000-0000-0000-000000000001'::uuid,
        '00000000-0000-0000-0000-000000000001'::uuid,
        '10000000-0000-0000-0000-000000000003'::uuid,
        'Project Alpha - Requirements',
        E'# Project Alpha Requirements\n\n## Overview\nBuild a note-taking application with vector search capabilities.\n\n## Key Features\n- Markdown support\n- Vector similarity search\n- Hierarchical folders\n- Tags and links',
        NOW() - INTERVAL '5 days',
        NOW() - INTERVAL '1 day',
        0
    ),
    (
        '20000000-0000-0000-0000-000000000002'::uuid,
        '00000000-0000-0000-0000-000000000001'::uuid,
        '10000000-0000-0000-0000-000000000003'::uuid,
        'Technical Architecture',
        E'# System Architecture\n\n## Backend\n- Spring Boot\n- PostgreSQL with pgvector\n- REST API\n\n## Frontend\n- React 18\n- TypeScript\n- Vite\n- TailwindCSS',
        NOW() - INTERVAL '4 days',
        NOW() - INTERVAL '2 days',
        0
    ),
    -- Notes in Work/Meetings
    (
        '20000000-0000-0000-0000-000000000003'::uuid,
        '00000000-0000-0000-0000-000000000001'::uuid,
        '10000000-0000-0000-0000-000000000004'::uuid,
        'Team Sync - Nov 20',
        E'# Team Sync Meeting\n\n**Date:** November 20, 2024\n\n## Attendees\n- Alice\n- Bob\n- Charlie\n\n## Discussion\n- Sprint review\n- Next milestone planning\n- Tech debt priorities',
        NOW() - INTERVAL '3 days',
        NOW() - INTERVAL '3 days',
        0
    ),
    -- Notes in Personal/Journal
    (
        '20000000-0000-0000-0000-000000000004'::uuid,
        '00000000-0000-0000-0000-000000000001'::uuid,
        '10000000-0000-0000-0000-000000000006'::uuid,
        'Daily Journal - Nov 22',
        E'# Daily Journal\n\n## What went well\n- Completed folder tree implementation\n- Fixed CORS issues\n- Added accessibility attributes\n\n## What to improve\n- Add keyboard navigation\n- Write more tests',
        NOW() - INTERVAL '1 day',
        NOW() - INTERVAL '1 day',
        0
    ),
    (
        '20000000-0000-0000-0000-000000000005'::uuid,
        '00000000-0000-0000-0000-000000000001'::uuid,
        '10000000-0000-0000-0000-000000000006'::uuid,
        'Learning Goals',
        E'# 2024 Learning Goals\n\n## Technical Skills\n- [ ] Master React Query\n- [ ] Learn vector databases\n- [x] Improve TypeScript skills\n\n## Soft Skills\n- [ ] Better time management\n- [ ] Technical writing',
        NOW() - INTERVAL '10 days',
        NOW() - INTERVAL '5 days',
        0
    ),
    -- Notes in Personal (root)
    (
        '20000000-0000-0000-0000-000000000006'::uuid,
        '00000000-0000-0000-0000-000000000001'::uuid,
        '10000000-0000-0000-0000-000000000005'::uuid,
        'Book List',
        E'# Reading List\n\n## Currently Reading\n- "Designing Data-Intensive Applications"\n\n## Want to Read\n- "Clean Architecture"\n- "Domain-Driven Design"',
        NOW() - INTERVAL '7 days',
        NOW() - INTERVAL '2 days',
        0
    ),
    -- Notes in Archive
    (
        '20000000-0000-0000-0000-000000000007'::uuid,
        '00000000-0000-0000-0000-000000000001'::uuid,
        '10000000-0000-0000-0000-000000000007'::uuid,
        'Old Project Notes',
        E'# Legacy Project\n\nArchived notes from previous project.\nKept for reference.',
        NOW() - INTERVAL '30 days',
        NOW() - INTERVAL '30 days',
        0
    );

-- Log success
DO $$
BEGIN
    RAISE NOTICE '';
    RAISE NOTICE '===============================================';
    RAISE NOTICE 'Test user created successfully!';
    RAISE NOTICE '';
    RAISE NOTICE 'LOGIN CREDENTIALS:';
    RAISE NOTICE '  Email: test@example.com';
    RAISE NOTICE '  Password: Test123!';
    RAISE NOTICE '';
    RAISE NOTICE 'SAMPLE DATA CREATED:';
    RAISE NOTICE '  - 1 test user';
    RAISE NOTICE '  - 7 folders (Work, Projects, Meetings, Personal, Journal, Archive)';
    RAISE NOTICE '  - 7 sample notes';
    RAISE NOTICE '';
    RAISE NOTICE 'You can now login at: http://localhost:5173/login';
    RAISE NOTICE '===============================================';
    RAISE NOTICE '';
END
$$;
