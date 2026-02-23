CREATE TABLE blog.services (
    id BIGSERIAL PRIMARY KEY,
    icon VARCHAR(100),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    locale VARCHAR(5) NOT NULL DEFAULT 'ru',
    sort_order INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE blog.service_tags (
    id BIGSERIAL PRIMARY KEY,
    service_id BIGINT NOT NULL REFERENCES blog.services(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0
);
