USE growmeproducts;

DROP TABLE IF EXISTS categories;

CREATE TABLE IF NOT EXISTS categories
(
    category_id  BINARY(16) PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL UNIQUE
    );

DROP TABLE IF EXISTS owners;

CREATE TABLE IF NOT EXISTS owners (
                                      owner_id BINARY(16) PRIMARY KEY,
    owner_name VARCHAR(255) NOT NULL,
    owner_email VARCHAR(255) NOT NULL UNIQUE,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

DROP TABLE IF EXISTS products;

CREATE TABLE IF NOT EXISTS products
(
    product_id     BINARY(16) PRIMARY KEY,
    name           VARCHAR(255)   NOT NULL,
    brand          VARCHAR(255),
    description    TEXT           NOT NULL,
    price          DECIMAL(19, 2) NOT NULL,
    units_in_stock INT,
    image_url      VARCHAR(255),
    date_created   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    category_id    BINARY(16)     NOT NULL,
    owner_id       BINARY(16),
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories (category_id),
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES owners (owner_id)
    );

INSERT INTO categories (category_id, category_name)
VALUES
    (UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440000'), 'Vegetables'),
    (UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440001'), 'Fruits'),
    (UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440002'), 'Rakia');
