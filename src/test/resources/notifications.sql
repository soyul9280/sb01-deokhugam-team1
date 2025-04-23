-- ================================
-- 1) USERS – 알림 수신자 및 트리거 유저
-- ================================
INSERT INTO users (id, nickname, password, email, created_at, updated_at, is_deleted)
VALUES
    ('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'receiver', 'pass', 'recv@example.com', now(), now(), false),
    ('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', 'trigger',  'pass', 'trig@example.com', now(), now(), false);

-- ================================
-- 2) BOOKS – 리뷰가 참조하는 도서
-- ================================
INSERT INTO books (id, title, author, publisher, published_date, isbn, created_at, updated_at, is_deleted)
VALUES
    ('cccccccc-cccc-cccc-cccc-cccccccccccc', '테스트 도서', '홍길동', '테스트출판사', '2020-01-01', 'ISBN-1234', now(), now(), false);

-- ================================
-- 3) REVIEWS – 알림이 달릴 리뷰
-- ================================
INSERT INTO reviews (
    id, user_id, book_id, content, rating,
    like_count, comment_count, is_deleted, created_at, updated_at
) VALUES (
             'dddddddd-dddd-dddd-dddd-dddddddddddd',  -- reviewId
             'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',  -- 작성자(receiver)
             'cccccccc-cccc-cccc-cccc-cccccccccccc',  -- 도서
             '정말 재미있었어요!', 5,
             2, 3, false, now(), now()
         );

-- ================================
-- 4) NOTIFICATIONS – 미리 생성된 알림
-- ================================
INSERT INTO notifications (
    id, review_id, user_id, review_title, content,
    confirmed, created_at, updated_at
) VALUES (
             'eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee',  -- notificationId
             'dddddddd-dddd-dddd-dddd-dddddddddddd',  -- review_id
             'aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa',  -- user_id (receiver)
             '정말 재미있었어요!',                     -- review_title
             '[trigger]님이 나의 리뷰를 좋아합니다.',  -- content
             false, now(), now()
         );
