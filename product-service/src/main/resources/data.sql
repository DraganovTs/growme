
INSERT INTO categories (category_id, category_name)
VALUES
    ('550e8400-e29b-41d4-a716-446655440000', 'Vegetables'),
    ('550e8400-e29b-41d4-a716-446655440001', 'Fruits'),
    ('550e8400-e29b-41d4-a716-446655440002', 'Rakia');

INSERT INTO owners (owner_id, owner_name)
VALUES
    ('550e8400-e29b-41d4-a716-446655440003', 'TestUser1'),
    ('550e8400-e29b-41d4-a716-446655440004', 'TestUser2'),
    ('550e8400-e29b-41d4-a716-446655440005', 'TestUser3');


INSERT INTO products (product_id, name, brand, description, price, units_in_stock, image_url, category_id, owner_id)
VALUES
    (
        '550e8400-e29b-41d4-a716-446655440006',
        'Tomatoes',
        'Pink',
        'Very delicious vegetables',
        699.99,
        100,
        'tomato.jpg',
        '550e8400-e29b-41d4-a716-446655440000',
        '550e8400-e29b-41d4-a716-446655440003'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440007',
        'Onion',
        'Purple',
        'Very delicious vegetables',
        299.99,
        120,
        'onion.jpg',
        '550e8400-e29b-41d4-a716-446655440000',
        '550e8400-e29b-41d4-a716-446655440004'
    ),
    (
        '550e8400-e29b-41d4-a716-446655440008',
        'Apple',
        'Green',
        'Very delicious fruit',
        699.99,
        100,
        'apple.jpg',
        '550e8400-e29b-41d4-a716-446655440001',
        '550e8400-e29b-41d4-a716-446655440005'
    );