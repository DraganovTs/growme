USE growmeusers;

DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users
(
    user_id        BINARY(36) PRIMARY KEY,
    username       VARCHAR(20)  NOT NULL UNIQUE,
    email          VARCHAR(255) NOT NULL UNIQUE,
    first_name     VARCHAR(50),
    last_name      VARCHAR(50),
    phone          VARCHAR(15) UNIQUE,
    account_status VARCHAR(20)  NOT NULL,
    created_at     TIMESTAMP    NOT NULL,
    updated_at     TIMESTAMP    NOT NULL,

    -- Embedded Address fields
    street         VARCHAR(100),
    city           VARCHAR(50),
    state          VARCHAR(50),
    zip_code       VARCHAR(10),

    -- Indexes for better query performance
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_account_status (account_status)
    );

DROP TABLE IF EXISTS user_owned_roles;

CREATE TABLE IF NOT EXISTS user_owned_roles
(
    user_id BINARY(36) NOT NULL,
    role    VARCHAR(50),
    UNIQUE (user_id, role)
    );

DROP TABLE IF EXISTS user_owned_products;

CREATE TABLE IF NOT EXISTS user_owned_products
(
    user_id    BINARY(36) NOT NULL,
    product_id BINARY(36) NOT NULL,
    PRIMARY KEY (user_id, product_id),
    CONSTRAINT fk_user_products_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
    );

DROP TABLE IF EXISTS user_purchased_orders;

CREATE TABLE IF NOT EXISTS user_purchased_orders
(
    user_id  BINARY(36) NOT NULL,
    order_id BINARY(36) NOT NULL,
    PRIMARY KEY (user_id, order_id),
    CONSTRAINT fk_user_orders_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
    );
