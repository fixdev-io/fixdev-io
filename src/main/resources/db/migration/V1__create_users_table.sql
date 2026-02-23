create schema if not exists blog;

CREATE TABLE blog.users (
    id BIGSERIAL PRIMARY KEY,
    keycloak_sub VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    avatar_url VARCHAR(512),
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_users_keycloak_sub ON users(keycloak_sub);
