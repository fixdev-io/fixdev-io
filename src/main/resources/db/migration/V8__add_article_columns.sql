ALTER TABLE blog.articles ADD COLUMN view_count BIGINT NOT NULL DEFAULT 0;
ALTER TABLE blog.articles ADD COLUMN priority INT NOT NULL DEFAULT 0;
ALTER TABLE blog.articles ADD COLUMN version INT NOT NULL DEFAULT 1;
ALTER TABLE blog.articles ADD COLUMN locale VARCHAR(5) NOT NULL DEFAULT 'ru';

CREATE INDEX idx_articles_locale ON blog.articles(locale);
CREATE INDEX idx_articles_priority ON blog.articles(priority DESC);
