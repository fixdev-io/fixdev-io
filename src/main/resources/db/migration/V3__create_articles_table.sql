CREATE TABLE articles (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    slug VARCHAR(500) NOT NULL UNIQUE,
    content TEXT,
    excerpt VARCHAR(1000),
    cover_image_url VARCHAR(512),
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    seo_title VARCHAR(200),
    seo_description VARCHAR(500),
    published_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_articles_slug ON articles(slug);
CREATE INDEX idx_articles_status ON articles(status);
CREATE INDEX idx_articles_published_at ON articles(published_at DESC);
