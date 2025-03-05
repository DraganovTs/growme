DROP TABLE IF EXISTS categories;

CREATE TABLE IF NOT EXISTS categories
(
    category_id   CHAR(36) PRIMARY KEY, -- UUID stored as string
    category_name VARCHAR(255) NOT NULL UNIQUE
);

DROP TABLE IF EXISTS owners;

CREATE TABLE IF NOT EXISTS owners
(
    owner_id   CHAR(36) PRIMARY KEY, -- UUID stored as string
    owner_name VARCHAR(255)
);



DROP TABLE IF EXISTS products;

CREATE TABLE IF NOT EXISTS products
(
    product_id     CHAR(36) PRIMARY KEY, -- UUID stored as string
    name           VARCHAR(255)   NOT NULL,
    brand          VARCHAR(255),
    description    TEXT           NOT NULL,
    price          DECIMAL(19, 2) NOT NULL,
    units_in_stock INT,
    image_url      VARCHAR(255),
    date_created   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated   TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    category_id    CHAR(36)       NOT NULL,
    owner_id       CHAR(36),
    CONSTRAINT fk_category FOREIGN KEY (category_id) REFERENCES categories (category_id),
    CONSTRAINT fk_owner FOREIGN KEY (owner_id) REFERENCES owners (owner_id)
);