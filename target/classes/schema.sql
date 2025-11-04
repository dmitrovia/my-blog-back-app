CREATE TABLE IF NOT EXISTS posts (
                                     id BIGSERIAL PRIMARY KEY,
                                     title VARCHAR(255) NOT NULL,
    text TEXT NOT NULL,
    image BYTEA,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS tags (
                                    id BIGSERIAL PRIMARY KEY,
                                    post_id BIGINT NULL,
                                    tag VARCHAR(100) NOT NULL,
    FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS comments (
                                        id BIGSERIAL PRIMARY KEY,
                                        post_id BIGINT NULL,
                                        text TEXT NOT NULL,
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                        FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE SET NULL
    );

CREATE TABLE IF NOT EXISTS likes (
                                     id BIGSERIAL PRIMARY KEY,
                                     post_id BIGINT NULL,
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE SET NULL
    );