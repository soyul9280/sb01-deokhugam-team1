-- 도서
CREATE TABLE books (
                       id UUID PRIMARY KEY NOT NULL,
                       created_at TIMESTAMP NOT NULL default now(),
                       updated_at TIMESTAMP NOT NULL,
                       author VARCHAR(50) NOT NULL,
                       description TEXT, -- 길이 제한 늘림
                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                       isbn VARCHAR(50) UNIQUE,
                       published_date DATE NOT NULL,
                       publisher VARCHAR(50) NOT NULL,
                       rating DOUBLE PRECISION NOT NULL DEFAULT 0.0, -- 정렬을 위해 추가
                       review_count INTEGER NOT NULL DEFAULT 0, -- 정렬을 위해 추가
                       thumbnail_url VARCHAR(255),
                       title VARCHAR(50) NOT NULL
);

-- 사용자
CREATE TABLE users (
                       id UUID PRIMARY KEY NOT NULL,
                       nickname VARCHAR(50) NOT NULL,
                       password VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       email VARCHAR(50) NOT NULL UNIQUE,
                       is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

-- 리뷰
CREATE TABLE reviews (
                         id UUID PRIMARY KEY,
                         book_id UUID NOT NULL,
                         user_id UUID NOT NULL,
                         content VARCHAR(255) NOT NULL,
                         rating INT NOT NULL CHECK (rating BETWEEN 0 AND 5),
                         like_count INT NOT NULL DEFAULT 0,
                         comment_count INT NOT NULL DEFAULT 0,
                         is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
                         created_at TIMESTAMP NOT NULL,
                         updated_at TIMESTAMP NOT NULL,

                         CONSTRAINT fk_reviews_book
                             FOREIGN KEY (book_id)
                                 REFERENCES books(id),
                         CONSTRAINT fk_reviews_user
                             FOREIGN KEY (user_id)
                                 REFERENCES users(id)
);

-- 댓글
CREATE TABLE comments (
                          id UUID PRIMARY KEY NOT NULL,
                          created_at TIMESTAMP NOT NULL default now(),
                          updated_at TIMESTAMP NOT NULL,
                          user_id UUID NOT NULL,
                          review_id UUID NOT NULL,
                          content VARCHAR(225) NOT NULL,
                          is_deleted BOOLEAN NOT NULL,

                          CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                          CONSTRAINT fk_comment_review FOREIGN KEY (review_id) REFERENCES reviews(id)  ON DELETE CASCADE
);


-- 알림
CREATE TABLE notifications (
                               id              UUID PRIMARY KEY NOT NULL,
                               review_id       UUID, -- nullable, ON DELETE SET NULL 가능
                               user_id         UUID, -- nullable, ON DELETE SET NULL 가능
                               review_title    VARCHAR(255)     NOT NULL,
                               content         VARCHAR(255)     NOT NULL,
                               confirmed       BOOLEAN          NOT NULL,
                               created_at      TIMESTAMP        NOT NULL,
                               updated_at      TIMESTAMP        NOT NULL,

                               CONSTRAINT fk_notification_review FOREIGN KEY (review_id) REFERENCES reviews (id) ON DELETE SET NULL,

                               CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
);


-- 인기유저
CREATE TABLE power_user (
                            id UUID PRIMARY KEY NOT NULL,
                            review_score_sum DOUBLE PRECISION,
                            like_count INT,
                            comment_count INT,
                            user_id UUID NOT NULL,
                            period VARCHAR(20) NOT NULL CHECK (period IN ('DAILY', 'WEEKLY', 'MONTHLY', 'ALL_TIME')), -- 문자열로 수정
                            created_at TIMESTAMP NOT NULL,
                            score DOUBLE PRECISION,
                            rank INT,
                            CONSTRAINT fk_poweruser_user
                                FOREIGN KEY (user_id)
                                    REFERENCES users(id)
                                    ON DELETE CASCADE
);

-- 인기도서
CREATE TABLE popular_book (
                              id UUID PRIMARY KEY NOT NULL,
                              book_id UUID NOT NULL,
                              created_at TIMESTAMP NOT NULL,
                              period VARCHAR(20) NOT NULL CHECK (period IN ('DAILY', 'WEEKLY', 'MONTHLY', 'ALL_TIME')), -- 문자열로 수정
                              review_count INT,
                              rating DOUBLE PRECISION,
                              rank INT,
                              score DOUBLE PRECISION,
                              CONSTRAINT fk_popularbook_book
                                  FOREIGN KEY (book_id)
                                      REFERENCES books(id)
                                      ON DELETE CASCADE
);

-- 인기리뷰
CREATE TABLE popular_reviews (
                                 id UUID PRIMARY KEY,
                                 review_id UUID NOT NULL,
                                 review_rating DOUBLE PRECISION NOT NULL CHECK (review_rating BETWEEN 0 AND 5),
                                 period VARCHAR(20) NOT NULL CHECK (period IN ('DAILY', 'WEEKLY', 'MONTHLY', 'ALL_TIME')), -- 문자열로 수정
                                 created_at TIMESTAMP NOT NULL,
                                 like_count INT,
                                 comment_count INT,
                                 score DOUBLE PRECISION,
                                 rank INT,
                                 CONSTRAINT fk_popular_reviews
                                     FOREIGN KEY (review_id)
                                         REFERENCES reviews(id)
                                         ON DELETE CASCADE
);