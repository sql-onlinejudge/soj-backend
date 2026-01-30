CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid CHAR(36) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    nickname VARCHAR(100),
    profile_image_url VARCHAR(500),
    provider ENUM('ANONYMOUS', 'GITHUB', 'GOOGLE') NOT NULL,
    provider_id VARCHAR(255),
    role ENUM('ANONYMOUS', 'USER', 'ADMIN') NOT NULL DEFAULT 'ANONYMOUS',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME,
    INDEX idx_provider_provider_id (provider, provider_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
