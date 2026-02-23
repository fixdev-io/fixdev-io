CREATE TABLE blog.article_revisions (
    id BIGSERIAL PRIMARY KEY,
    article_id BIGINT NOT NULL REFERENCES blog.articles(id) ON DELETE CASCADE,
    version INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    excerpt VARCHAR(1000),
    editor_id BIGINT REFERENCES blog.users(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_article_revisions_article ON blog.article_revisions(article_id, version DESC);
