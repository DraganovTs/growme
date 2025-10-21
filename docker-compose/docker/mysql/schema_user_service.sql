USE growmeusers;

DROP TABLE IF EXISTS user_purchased_orders;
DROP TABLE IF EXISTS user_owned_products;
DROP TABLE IF EXISTS user_owned_roles;
DROP TABLE IF EXISTS users;


CREATE TABLE users
(
    user_id        BINARY(16) PRIMARY KEY,
    username       VARCHAR(20)  NOT NULL UNIQUE,
    email          VARCHAR(255) NOT NULL UNIQUE,
    first_name     VARCHAR(50)  NOT NULL,
    last_name      VARCHAR(50)  NOT NULL,
    phone          VARCHAR(15) UNIQUE,
    account_status VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    street_address VARCHAR(100),
    city           VARCHAR(50),
    state_province VARCHAR(50),
    postal_code    VARCHAR(10),

    CONSTRAINT chk_account_status CHECK (account_status IN ('PENDING', 'ACTIVE', 'INACTIVE', 'SUSPENDED')),
    CONSTRAINT chk_email_format CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_username_length CHECK (CHAR_LENGTH(username) >= 3),

    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_account_status (account_status),
    INDEX idx_created_at (created_at)
) ENGINE = InnoDB;

CREATE TABLE user_owned_roles
(
    user_id    BINARY(16)  NOT NULL,
    role_name  VARCHAR(50) NOT NULL,
    granted_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, role_name),
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,

    CONSTRAINT chk_role_name CHECK (role_name IN ('USER', 'ADMIN', 'MODERATOR', 'PREMIUM'))
) ENGINE = InnoDB;

CREATE TABLE user_owned_products
(
    user_id      BINARY(16) NOT NULL,
    product_id   BINARY(16) NOT NULL,
    purchased_at TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, product_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,

    INDEX idx_purchased_at (purchased_at)
) ENGINE = InnoDB;

CREATE TABLE user_purchased_orders
(
    user_id    BINARY(16) NOT NULL,
    order_id   BINARY(16) NOT NULL,
    order_date TIMESTAMP  NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (user_id, order_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,

    INDEX idx_order_date (order_date)
) ENGINE = InnoDB;
