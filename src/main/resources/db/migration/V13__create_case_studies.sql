CREATE TABLE blog.case_studies (
    id BIGSERIAL PRIMARY KEY,
    industry VARCHAR(255),
    title VARCHAR(500) NOT NULL,
    description TEXT,
    locale VARCHAR(5) NOT NULL DEFAULT 'ru',
    sort_order INT NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE blog.case_metrics (
    id BIGSERIAL PRIMARY KEY,
    case_study_id BIGINT NOT NULL REFERENCES blog.case_studies(id) ON DELETE CASCADE,
    metric_value VARCHAR(50) NOT NULL,
    label VARCHAR(100) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0
);
