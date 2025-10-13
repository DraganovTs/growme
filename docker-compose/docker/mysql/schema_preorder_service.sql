USE growmepreorders;

CREATE TABLE IF NOT EXISTS categories
(
    category_id BINARY(36) PRIMARY KEY,
    category_name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS task_users
(
    task_user_id BINARY(36) PRIMARY KEY,
    task_user_name  VARCHAR(30) NOT NULL,
    task_user_email VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS tasks
(
    task_id BINARY(36) PRIMARY KEY,
    title             VARCHAR(100)  NOT NULL,
    description       VARCHAR(1000) NOT NULL,
    category_id BINARY(36) NOT NULL,
    status            VARCHAR(50)   NOT NULL,
    user_id BINARY(36) NOT NULL,
    budget            NUMERIC(12, 2),
    deadline          TIMESTAMP,
    quantity          INTEGER       NOT NULL CHECK (quantity >= 1 AND quantity <= 10000),
    unit              VARCHAR(20)   NOT NULL,
    quality           VARCHAR(50)   NOT NULL,
    harvest_date      DATE          NOT NULL,
    delivery_date     DATE          NOT NULL,
    flexible_dates    BOOLEAN       NOT NULL DEFAULT false,
    delivery_location VARCHAR(100)  NOT NULL,
    delivery_method   VARCHAR(50)   NOT NULL,
    willing_to_ship   BOOLEAN       NOT NULL DEFAULT false,
    price_model       VARCHAR(50)   NOT NULL,
    photos_required   BOOLEAN       NOT NULL DEFAULT false,
    visit_farm        BOOLEAN       NOT NULL DEFAULT false,
    created_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_task_category
        FOREIGN KEY (category_id)
            REFERENCES categories (category_id)
);

CREATE TABLE IF NOT EXISTS bids
(
    bid_id BINARY(36) PRIMARY KEY,
    price                 NUMERIC(12, 2) NOT NULL CHECK (price > 0),
    message               VARCHAR(500)   NOT NULL,
    status                VARCHAR(20)    NOT NULL,
    user_id BINARY(36) NOT NULL,
    user_name             VARCHAR(255)   NOT NULL,
    task_id BINARY(36) NOT NULL,
    delivery_method       VARCHAR(20),
    proposed_harvest_date DATE,
    delivery_included     BOOLEAN        NOT NULL DEFAULT false,
    counter_offer_price   NUMERIC(12, 2),
    counter_offer_message TEXT,
    created_at            TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at            TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_bid_task
        FOREIGN KEY (task_id)
            REFERENCES tasks (task_id)
            ON DELETE CASCADE
);


INSERT INTO categories (category_id, category_name)
VALUES
    (UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440000'), 'Vegetables'),
    (UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440001'), 'Fruits'),
    (UUID_TO_BIN('550e8400-e29b-41d4-a716-446655440002'), 'Rakia');
