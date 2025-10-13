USE growmeorders;

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
    owner_id     BINARY(36) PRIMARY KEY,
    owner_name   VARCHAR(255) NOT NULL,
    owner_email  VARCHAR(255) NOT NULL UNIQUE,
    date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS orders
(
    order_id           BINARY(36) PRIMARY KEY,
    buyer_email        VARCHAR(255)   NOT NULL,
    order_date         TIMESTAMP      NOT NULL,
    status             VARCHAR(20)    NOT NULL,
    sub_total          DECIMAL(19, 2) NOT NULL,
    payment_intent_id  VARCHAR(255),
    owner_id           BINARY(36),
    delivery_method_id INT            NOT NULL,

    -- Address fields
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
    order_item_id BINARY(36) PRIMARY KEY DEFAULT (UUID_TO_BIN(UUID())),
    product_id    BINARY(36)    ,
    product_name  VARCHAR(255)  ,
    image_url     VARCHAR(255),
    quantity      INT            NOT NULL CHECK (quantity > 0),
    price         DECIMAL(19, 2) NOT NULL CHECK (price >= 0),
    order_id      BINARY(36)     NOT NULL,

    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders (order_id) ON DELETE CASCADE
    );

INSERT INTO delivery_methods (short_name, delivery_time, description, price)
VALUES
    ('Standard', '3-5 days', 'Regular ground shipping with tracking', 5.99),
    ('Express', '1-2 days', 'Priority shipping with faster delivery', 12.99),
    ('Overnight', '1 day', 'Guaranteed next-day delivery', 24.99),
    ('Free Shipping', '5-7 days', 'Economy shipping at no cost', 0.00);
