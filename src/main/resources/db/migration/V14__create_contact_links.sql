CREATE TABLE blog.contact_links (
    id BIGSERIAL PRIMARY KEY,
    icon VARCHAR(100),
    label VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL,
    locale VARCHAR(5) NOT NULL DEFAULT 'ru',
    sort_order INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE
);
