DELETE FROM users;
INSERT INTO users (id, email, nickname, password, is_deleted, created_at)
VALUES
    ('123e4567-e89b-12d3-a456-426614174000', 'test@example.com', '테스트유저', 'test1234!', false, CURRENT_TIMESTAMP);