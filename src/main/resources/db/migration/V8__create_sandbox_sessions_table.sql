CREATE TABLE sandbox_sessions
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_key   VARCHAR(36)  NOT NULL UNIQUE,
    user_id       VARCHAR(36)  NULL,
    schema_name   VARCHAR(64)  NOT NULL,
    extracted_sql TEXT         NOT NULL,
    expires_at    DATETIME     NOT NULL,
    created_at    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME              ON UPDATE CURRENT_TIMESTAMP,
    deleted_at    DATETIME     NULL,
    INDEX idx_sandbox_session_key (session_key),
    INDEX idx_sandbox_user_id (user_id),
    INDEX idx_sandbox_expires_at (expires_at)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
