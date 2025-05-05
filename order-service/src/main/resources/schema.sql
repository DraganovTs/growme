DROP TABLE IF EXISTS delivery_methods;

CREATE TABLE IF NOT EXISTS delivery_methods
(
    delivery_method_id INT PRIMARY KEY AUTO_INCREMENT,
    short_name VARCHAR(50) NOT NULL UNIQUE,
    delivery_time VARCHAR(20) NOT NULL,
    description TEXT,
    price DECIMAL(19, 2) NOT NULL CHECK (price >= 0)
    );
