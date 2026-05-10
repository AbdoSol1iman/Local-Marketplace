-- Dev-only seed data loaded via application-dev.yml flyway.locations
-- Provides ready-to-use records for UI integration.

INSERT INTO customers (name, email, phone, address, username, password, blocked)
SELECT 'Demo Buyer', 'demo.buyer@wafrnalak.local', '+201000000001', 'Nasr City, Cairo', 'demo_buyer',
       '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiA6fK8S4M4B9xjM90oCbGyF/F7fs/6', FALSE
WHERE NOT EXISTS (
    SELECT 1 FROM customers WHERE username = 'demo_buyer'
);

INSERT INTO customers (name, email, phone, address, username, password, blocked)
SELECT 'Family Shopper', 'family.shopper@wafrnalak.local', '+201000000002', 'New Cairo, Cairo', 'demo_family',
       '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiA6fK8S4M4B9xjM90oCbGyF/F7fs/6', FALSE
WHERE NOT EXISTS (
    SELECT 1 FROM customers WHERE username = 'demo_family'
);

INSERT INTO customers (name, email, phone, address, username, password, blocked)
SELECT 'Blocked Account', 'blocked.user@wafrnalak.local', '+201000000003', 'Heliopolis, Cairo', 'demo_blocked',
       '$2a$10$7EqJtq98hPqEX7fNZaFWoOHiA6fK8S4M4B9xjM90oCbGyF/F7fs/6', TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM customers WHERE username = 'demo_blocked'
);

INSERT INTO product_category (category_name)
SELECT 'Fruits'
WHERE NOT EXISTS (SELECT 1 FROM product_category WHERE category_name = 'Fruits');

INSERT INTO product_category (category_name)
SELECT 'Vegetables'
WHERE NOT EXISTS (SELECT 1 FROM product_category WHERE category_name = 'Vegetables');

INSERT INTO product_category (category_name)
SELECT 'Bakery'
WHERE NOT EXISTS (SELECT 1 FROM product_category WHERE category_name = 'Bakery');

INSERT INTO product_category (category_name)
SELECT 'Dairy'
WHERE NOT EXISTS (SELECT 1 FROM product_category WHERE category_name = 'Dairy');

INSERT INTO product_catalog (product_name, description, price, quantity_in_stock, category_id)
SELECT 'Fresh Oranges', 'Locally sourced Valencia oranges (1kg).', 55.0000, 120, c.category_id
FROM product_category c
WHERE c.category_name = 'Fruits'
  AND NOT EXISTS (SELECT 1 FROM product_catalog WHERE product_name = 'Fresh Oranges');

INSERT INTO product_catalog (product_name, description, price, quantity_in_stock, category_id)
SELECT 'Bananas', 'Sweet bananas bunch (1kg).', 42.0000, 90, c.category_id
FROM product_category c
WHERE c.category_name = 'Fruits'
  AND NOT EXISTS (SELECT 1 FROM product_catalog WHERE product_name = 'Bananas');

INSERT INTO product_catalog (product_name, description, price, quantity_in_stock, category_id)
SELECT 'Tomatoes', 'Red ripe tomatoes (1kg).', 30.0000, 150, c.category_id
FROM product_category c
WHERE c.category_name = 'Vegetables'
  AND NOT EXISTS (SELECT 1 FROM product_catalog WHERE product_name = 'Tomatoes');

INSERT INTO product_catalog (product_name, description, price, quantity_in_stock, category_id)
SELECT 'Cucumbers', 'Fresh crunchy cucumbers (1kg).', 22.0000, 110, c.category_id
FROM product_category c
WHERE c.category_name = 'Vegetables'
  AND NOT EXISTS (SELECT 1 FROM product_catalog WHERE product_name = 'Cucumbers');

INSERT INTO product_catalog (product_name, description, price, quantity_in_stock, category_id)
SELECT 'Whole Wheat Bread', 'Baked daily whole wheat loaf.', 28.0000, 60, c.category_id
FROM product_category c
WHERE c.category_name = 'Bakery'
  AND NOT EXISTS (SELECT 1 FROM product_catalog WHERE product_name = 'Whole Wheat Bread');

INSERT INTO product_catalog (product_name, description, price, quantity_in_stock, category_id)
SELECT 'Milk 1L', 'Full cream milk 1 liter carton.', 36.0000, 80, c.category_id
FROM product_category c
WHERE c.category_name = 'Dairy'
  AND NOT EXISTS (SELECT 1 FROM product_catalog WHERE product_name = 'Milk 1L');

INSERT INTO product_images (image_url, product_id, image_order)
SELECT 'https://images.unsplash.com/photo-1619566636858-adf3ef46400b?w=800', p.product_id, 1
FROM product_catalog p
WHERE p.product_name = 'Fresh Oranges'
  AND NOT EXISTS (
      SELECT 1 FROM product_images pi
      WHERE pi.product_id = p.product_id AND pi.image_order = 1
  );

INSERT INTO product_images (image_url, product_id, image_order)
SELECT 'https://images.unsplash.com/photo-1574226516831-e1dff420e37f?w=800', p.product_id, 1
FROM product_catalog p
WHERE p.product_name = 'Tomatoes'
  AND NOT EXISTS (
      SELECT 1 FROM product_images pi
      WHERE pi.product_id = p.product_id AND pi.image_order = 1
  );

INSERT INTO product_images (image_url, product_id, image_order)
SELECT 'https://images.unsplash.com/photo-1608198093002-ad4e005484ec?w=800', p.product_id, 1
FROM product_catalog p
WHERE p.product_name = 'Whole Wheat Bread'
  AND NOT EXISTS (
      SELECT 1 FROM product_images pi
      WHERE pi.product_id = p.product_id AND pi.image_order = 1
  );

INSERT INTO product_images (image_url, product_id, image_order)
SELECT 'https://images.unsplash.com/photo-1550583724-b2692b85b150?w=800', p.product_id, 1
FROM product_catalog p
WHERE p.product_name = 'Milk 1L'
  AND NOT EXISTS (
      SELECT 1 FROM product_images pi
      WHERE pi.product_id = p.product_id AND pi.image_order = 1
  );

INSERT INTO stores (store_name, owner_name, active)
SELECT 'Green Basket Market', 'Ahmed Samir', TRUE
WHERE NOT EXISTS (SELECT 1 FROM stores WHERE store_name = 'Green Basket Market');

INSERT INTO stores (store_name, owner_name, active)
SELECT 'Daily Fresh Foods', 'Nour Hassan', TRUE
WHERE NOT EXISTS (SELECT 1 FROM stores WHERE store_name = 'Daily Fresh Foods');

INSERT INTO stores (store_name, owner_name, active)
SELECT 'Sunrise Bakery', 'Mona Farouk', TRUE
WHERE NOT EXISTS (SELECT 1 FROM stores WHERE store_name = 'Sunrise Bakery');

INSERT INTO cart_items (customer_id, product_id, quantity, created_at, updated_at)
SELECT cu.customer_id, pr.product_id, 2, NOW(), NOW()
FROM customers cu
JOIN product_catalog pr ON pr.product_name = 'Fresh Oranges'
WHERE cu.username = 'demo_buyer'
  AND NOT EXISTS (
      SELECT 1 FROM cart_items ci
      WHERE ci.customer_id = cu.customer_id AND ci.product_id = pr.product_id
  );

INSERT INTO cart_items (customer_id, product_id, quantity, created_at, updated_at)
SELECT cu.customer_id, pr.product_id, 1, NOW(), NOW()
FROM customers cu
JOIN product_catalog pr ON pr.product_name = 'Milk 1L'
WHERE cu.username = 'demo_buyer'
  AND NOT EXISTS (
      SELECT 1 FROM cart_items ci
      WHERE ci.customer_id = cu.customer_id AND ci.product_id = pr.product_id
  );

INSERT INTO orders (customer_id, order_date, total_amount, status)
SELECT cu.customer_id, TIMESTAMP '2026-04-30 14:20:00', 83.0000, 'PENDING'
FROM customers cu
WHERE cu.username = 'demo_buyer'
  AND NOT EXISTS (
      SELECT 1 FROM orders o
      WHERE o.customer_id = cu.customer_id
        AND o.order_date = TIMESTAMP '2026-04-30 14:20:00'
  );

INSERT INTO orders (customer_id, order_date, total_amount, status)
SELECT cu.customer_id, TIMESTAMP '2026-05-02 11:15:00', 114.0000, 'COMPLETED'
FROM customers cu
WHERE cu.username = 'demo_family'
  AND NOT EXISTS (
      SELECT 1 FROM orders o
      WHERE o.customer_id = cu.customer_id
        AND o.order_date = TIMESTAMP '2026-05-02 11:15:00'
  );

INSERT INTO order_items (order_id, product_id, quantity, price, total_items_price)
SELECT o.order_id, p.product_id, 1, 55.0000, 55.0000
FROM orders o
JOIN customers cu ON cu.customer_id = o.customer_id
JOIN product_catalog p ON p.product_name = 'Fresh Oranges'
WHERE cu.username = 'demo_buyer'
  AND o.order_date = TIMESTAMP '2026-04-30 14:20:00'
  AND NOT EXISTS (
      SELECT 1 FROM order_items oi
      WHERE oi.order_id = o.order_id AND oi.product_id = p.product_id
  );

INSERT INTO order_items (order_id, product_id, quantity, price, total_items_price)
SELECT o.order_id, p.product_id, 1, 28.0000, 28.0000
FROM orders o
JOIN customers cu ON cu.customer_id = o.customer_id
JOIN product_catalog p ON p.product_name = 'Whole Wheat Bread'
WHERE cu.username = 'demo_buyer'
  AND o.order_date = TIMESTAMP '2026-04-30 14:20:00'
  AND NOT EXISTS (
      SELECT 1 FROM order_items oi
      WHERE oi.order_id = o.order_id AND oi.product_id = p.product_id
  );

INSERT INTO order_items (order_id, product_id, quantity, price, total_items_price)
SELECT o.order_id, p.product_id, 2, 36.0000, 72.0000
FROM orders o
JOIN customers cu ON cu.customer_id = o.customer_id
JOIN product_catalog p ON p.product_name = 'Milk 1L'
WHERE cu.username = 'demo_family'
  AND o.order_date = TIMESTAMP '2026-05-02 11:15:00'
  AND NOT EXISTS (
      SELECT 1 FROM order_items oi
      WHERE oi.order_id = o.order_id AND oi.product_id = p.product_id
  );

INSERT INTO order_items (order_id, product_id, quantity, price, total_items_price)
SELECT o.order_id, p.product_id, 1, 42.0000, 42.0000
FROM orders o
JOIN customers cu ON cu.customer_id = o.customer_id
JOIN product_catalog p ON p.product_name = 'Bananas'
WHERE cu.username = 'demo_family'
  AND o.order_date = TIMESTAMP '2026-05-02 11:15:00'
  AND NOT EXISTS (
      SELECT 1 FROM order_items oi
      WHERE oi.order_id = o.order_id AND oi.product_id = p.product_id
  );

INSERT INTO payments (order_id, amount, payment_method, transaction_date)
SELECT o.order_id, 114.0000, 'CARD', TIMESTAMP '2026-05-02 11:20:00'
FROM orders o
JOIN customers cu ON cu.customer_id = o.customer_id
WHERE cu.username = 'demo_family'
  AND o.order_date = TIMESTAMP '2026-05-02 11:15:00'
  AND NOT EXISTS (SELECT 1 FROM payments p WHERE p.order_id = o.order_id);

INSERT INTO shippings (order_id, carrier_name, tracking_number, shipping_status, estimated_delivery_date, actual_delivery_date)
SELECT o.order_id, 'Aramex', 'TRK-DEV-0001', 'IN_TRANSIT',
       TIMESTAMP '2026-05-06 18:00:00', NULL
FROM orders o
JOIN customers cu ON cu.customer_id = o.customer_id
WHERE cu.username = 'demo_buyer'
  AND o.order_date = TIMESTAMP '2026-04-30 14:20:00'
  AND NOT EXISTS (SELECT 1 FROM shippings s WHERE s.order_id = o.order_id);

INSERT INTO shippings (order_id, carrier_name, tracking_number, shipping_status, estimated_delivery_date, actual_delivery_date)
SELECT o.order_id, 'DHL', 'TRK-DEV-0002', 'DELIVERED',
       TIMESTAMP '2026-05-04 18:00:00', TIMESTAMP '2026-05-04 14:30:00'
FROM orders o
JOIN customers cu ON cu.customer_id = o.customer_id
WHERE cu.username = 'demo_family'
  AND o.order_date = TIMESTAMP '2026-05-02 11:15:00'
  AND NOT EXISTS (SELECT 1 FROM shippings s WHERE s.order_id = o.order_id);

INSERT INTO reviews (product_id, customer_id, review_text, rating, review_date)
SELECT p.product_id, cu.customer_id, 'Very fresh and sweet. Will buy again!', 4.5, TIMESTAMP '2026-05-03 09:00:00'
FROM product_catalog p
JOIN customers cu ON cu.username = 'demo_family'
WHERE p.product_name = 'Fresh Oranges'
  AND NOT EXISTS (
      SELECT 1 FROM reviews r
      WHERE r.product_id = p.product_id
        AND r.customer_id = cu.customer_id
        AND r.review_date = TIMESTAMP '2026-05-03 09:00:00'
  );

INSERT INTO reviews (product_id, customer_id, review_text, rating, review_date)
SELECT p.product_id, cu.customer_id, 'Soft texture and tastes great toasted.', 5.0, TIMESTAMP '2026-05-03 10:30:00'
FROM product_catalog p
JOIN customers cu ON cu.username = 'demo_buyer'
WHERE p.product_name = 'Whole Wheat Bread'
  AND NOT EXISTS (
      SELECT 1 FROM reviews r
      WHERE r.product_id = p.product_id
        AND r.customer_id = cu.customer_id
        AND r.review_date = TIMESTAMP '2026-05-03 10:30:00'
  );
