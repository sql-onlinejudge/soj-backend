CREATE TABLE problems (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    description TEXT NOT NULL,
    schema_sql TEXT NOT NULL,
    schema_metadata JSON,
    difficulty INT NOT NULL,
    time_limit INT NOT NULL,
    is_order_sensitive BOOLEAN NOT NULL DEFAULT FALSE,
    solved_count INT NOT NULL DEFAULT 0,
    submitted_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME,
    INDEX idx_difficulty (difficulty),
    INDEX idx_deleted_at (deleted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE test_cases (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    init_sql TEXT NOT NULL,
    init_metadata JSON,
    answer TEXT NOT NULL,
    answer_metadata JSON,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME,
    INDEX idx_problem_id (problem_id),
    INDEX idx_deleted_at (deleted_at),
    CONSTRAINT fk_test_cases_problem FOREIGN KEY (problem_id) REFERENCES problems(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE submissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    problem_id BIGINT NOT NULL,
    user_id CHAR(36) NOT NULL,
    query TEXT NOT NULL,
    status ENUM('PENDING', 'RUNNING', 'COMPLETED') NOT NULL,
    verdict ENUM('ACCEPTED', 'WRONG_ANSWER', 'TIME_LIMIT_EXCEEDED', 'RUNTIME_ERROR'),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME,
    INDEX idx_user_id (user_id),
    INDEX idx_problem_user (problem_id, user_id),
    INDEX idx_deleted_at (deleted_at),
    CONSTRAINT fk_submissions_problem FOREIGN KEY (problem_id) REFERENCES problems(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uuid CHAR(36) NOT NULL UNIQUE,
    email VARCHAR(255) UNIQUE,
    nickname VARCHAR(100),
    password VARCHAR(255),
    profile_image_url VARCHAR(500),
    provider ENUM('LOCAL', 'GITHUB', 'GOOGLE') NOT NULL,
    provider_id VARCHAR(255),
    role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME ON UPDATE CURRENT_TIMESTAMP,
    deleted_at DATETIME,
    INDEX idx_provider_provider_id (provider, provider_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
