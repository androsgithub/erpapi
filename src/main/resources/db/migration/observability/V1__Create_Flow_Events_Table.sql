-- Flyway migration for erpapi_logs database
-- Creates the flow_events table to store observability logs
CREATE TABLE
    IF NOT EXISTS flow_events (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        trace_id VARCHAR(36) NOT NULL COMMENT 'Unique trace identifier for request tracking',
        step_id INT NOT NULL COMMENT 'Step identifier within the flow',
        step_name VARCHAR(255) NOT NULL COMMENT 'Step name for human readability',
        status INT NOT NULL COMMENT 'Status code: 0=START, 1=SUCCESS, 2+=ERROR',
        execution_time_ms INT NOT NULL DEFAULT 0 COMMENT 'Execution time in milliseconds',
        timestamp DATETIME (6) NOT NULL COMMENT 'When the event occurred',
        created_at DATETIME (6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT 'When record was created',
        -- Índices para otimizar buscas
        INDEX idx_trace_id (trace_id),
        INDEX idx_step_id (step_id),
        INDEX idx_status (status),
        INDEX idx_timestamp (timestamp),
        INDEX idx_created_at (created_at),
        INDEX idx_trace_timestamp (trace_id, timestamp)
    ) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'Stores flow events for observability and tracing purposes';