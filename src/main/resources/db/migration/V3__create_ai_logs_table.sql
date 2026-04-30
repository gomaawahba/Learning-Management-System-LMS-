CREATE TABLE ai_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);