INSERT INTO categories (category_id, category_name)
VALUES (UUID(), 'Vegetables'),
       (UUID(), 'Fruits'),
       (UUID(), 'rakia');


INSERT INTO owners (owner_id, owner_name)
VALUES (UUID(), "TestUser1"),
       (UUID(),"TestUser2"),
       (UUID() , "TestUser3");


INSERT INTO products (product_id, name, brand, description, price, units_in_stock, image_url, category_id, owner_id)
VALUES (
           UUID(),
           'Tomatoes',
           'Pink',
           'Very delicious vegetables',
           699.99,
           100,
           'tomato.jpg',
           (SELECT category_id FROM categories WHERE category_name = 'Vegetables'),
           (SELECT owner_id FROM owners WHERE owner_name = 'TestUser1')
       ),
    (
        UUID(),
        'Onion',
        'Purple',
        'Very delicious vegetables',
        299.99,
        120,
        'onion.jpg',
        (SELECT category_id FROM categories WHERE category_name = 'Vegetables'),
        (SELECT owner_id FROM owners WHERE owner_name = 'TestUser2')
    ),
    (
           UUID(),
           'Apple',
           'Green',
           'Very delicious fruit',
           699.99,
           100,
           'apple.jpg',
           (SELECT category_id FROM categories WHERE category_name = 'Fruits'),
           (SELECT owner_id FROM owners WHERE owner_name = 'TestUser3')
       );