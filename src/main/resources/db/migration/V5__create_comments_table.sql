CREATE TABLE comments (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    text TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_comments_article_id ON comments(article_id);
CREATE INDEX idx_comments_status ON comments(status);
