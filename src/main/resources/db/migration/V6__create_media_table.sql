CREATE TABLE blog.media (
    id BIGSERIAL PRIMARY KEY,
    filename VARCHAR(500) NOT NULL,
    stored_path VARCHAR(1000) NOT NULL,
    mime_type VARCHAR(100) NOT NULL,
    size_bytes BIGINT NOT NULL,
    article_id BIGINT REFERENCES articles(id) ON DELETE SET NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT NOW()
);
