CREATE TABLE subscription_plans (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(50)  NOT NULL,
    price          INT          NOT NULL,
    duration_months INT         NOT NULL,
    is_active      TINYINT(1)   NOT NULL DEFAULT 1,
    created_at     DATETIME(6)  NOT NULL,
    updated_at     DATETIME(6),
    UNIQUE INDEX uq_subscription_plans_name (name)
);

INSERT INTO subscription_plans (name, price, duration_months, is_active, created_at)
VALUES ('PREMIUM_MONTHLY', 9900, 1, 1, NOW());
