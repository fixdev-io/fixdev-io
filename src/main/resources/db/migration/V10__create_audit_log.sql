CREATE TABLE blog.audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES blog.users(id) ON DELETE SET NULL,
    action VARCHAR(20) NOT NULL,
    entity_type VARCHAR(30) NOT NULL,
    entity_id BIGINT NOT NULL,
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_audit_log_created_at ON blog.audit_log(created_at DESC);
CREATE INDEX idx_audit_log_entity ON blog.audit_log(entity_type, entity_id);
