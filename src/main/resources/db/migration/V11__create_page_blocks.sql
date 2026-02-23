CREATE TABLE blog.page_blocks (
    id BIGSERIAL PRIMARY KEY,
    page VARCHAR(50) NOT NULL,
    section VARCHAR(50) NOT NULL,
    key VARCHAR(100) NOT NULL,
    value TEXT,
    locale VARCHAR(5) NOT NULL DEFAULT 'ru',
    sort_order INT NOT NULL DEFAULT 0,
    UNIQUE(page, section, key, locale)
);
