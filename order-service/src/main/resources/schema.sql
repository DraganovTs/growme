DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS delivery_methods;
DROP TABLE IF EXISTS owners;

CREATE TABLE IF NOT EXISTS delivery_methods
(
    delivery_method_id INT PRIMARY KEY AUTO_INCREMENT,
    short_name         VARCHAR(50)    NOT NULL UNIQUE,
    delivery_time      VARCHAR(20)    NOT NULL,
    description        TEXT,
    price              DECIMAL(19, 2) NOT NULL CHECK (price >= 0)
);

CREATE TABLE IF NOT EXISTS owners
(
    owner_id     BINARY(16) PRIMARY KEY,
    owner_name   VARCHAR(255) NOT NULL,
    owner_email  VARCHAR(255) NOT NULL UNIQUE,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS orders
(
    order_id           BINARY(16) PRIMARY KEY,
    buyer_email        VARCHAR(255)   NOT NULL,
    order_date         TIMESTAMP      NOT NULL,
    status             VARCHAR(20)    NOT NULL,
    sub_total          DECIMAL(19, 2) NOT NULL,
    payment_intent_id  VARCHAR(255),
    owner_id           BINARY(16),
    delivery_method_id INT            NOT NULL,

    -- Embedded address fields (matches Address embeddable class)
    first_name         VARCHAR(100)   NOT NULL,
    last_name          VARCHAR(100)   NOT NULL,
    street             VARCHAR(255)   NOT NULL,
    city               VARCHAR(100)   NOT NULL,
    state              VARCHAR(100)   NOT NULL,
    zip_code           VARCHAR(20)    NOT NULL,

    CONSTRAINT fk_order_owner FOREIGN KEY (owner_id) REFERENCES owners (owner_id) ON DELETE SET NULL,
    CONSTRAINT fk_order_delivery_method FOREIGN KEY (delivery_method_id) REFERENCES delivery_methods (delivery_method_id)
);

CREATE TABLE IF NOT EXISTS order_items
(
    order_item_id BINARY(16) PRIMARY KEY,
    order_id      BINARY(16)     NOT NULL,
    product_id    BINARY(16)     NOT NULL,
    product_name  VARCHAR(255)   NOT NULL,
    image_url     VARCHAR(255),
    quantity      INT            NOT NULL CHECK (quantity > 0),
    price         DECIMAL(19, 2) NOT NULL CHECK (price >= 0),

    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE
);