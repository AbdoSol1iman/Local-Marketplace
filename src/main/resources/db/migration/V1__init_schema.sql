CREATE TABLE customers (
    customer_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    address VARCHAR(200),
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE product_category (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE product_catalog (
    product_id SERIAL PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    price NUMERIC(10, 4) NOT NULL,
    quantity_in_stock INTEGER,
    category_id INTEGER NOT NULL,
    CONSTRAINT fk_product_catalog_category
        FOREIGN KEY (category_id) REFERENCES product_category(category_id)
);

CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    customer_id INTEGER NOT NULL,
    order_date TIMESTAMP,
    total_amount NUMERIC(10, 4),
    status VARCHAR(20),
    CONSTRAINT fk_orders_customer
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE TABLE order_items (
    order_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    price NUMERIC(10, 4),
    total_items_price NUMERIC(10, 4),
    CONSTRAINT pk_order_items PRIMARY KEY (order_id, product_id),
    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id) REFERENCES orders(order_id),
    CONSTRAINT fk_order_items_product
        FOREIGN KEY (product_id) REFERENCES product_catalog(product_id)
);

CREATE TABLE payments (
    payment_id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    amount NUMERIC(10, 4),
    payment_method VARCHAR(50),
    transaction_date TIMESTAMP,
    CONSTRAINT fk_payments_order
        FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

CREATE TABLE shippings (
    shipping_id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    carrier_name VARCHAR(100),
    tracking_number VARCHAR(50),
    shipping_status VARCHAR(20),
    estimated_delivery_date TIMESTAMP,
    actual_delivery_date TIMESTAMP,
    CONSTRAINT fk_shippings_order
        FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

CREATE TABLE product_images (
    image_id SERIAL PRIMARY KEY,
    image_url VARCHAR(400) NOT NULL,
    product_id INTEGER NOT NULL,
    image_order SMALLINT,
    CONSTRAINT fk_product_images_product
        FOREIGN KEY (product_id) REFERENCES product_catalog(product_id)
);

CREATE TABLE reviews (
    review_id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    customer_id INTEGER NOT NULL,
    review_text VARCHAR(500),
    rating NUMERIC(3, 1),
    review_date TIMESTAMP,
    CONSTRAINT fk_reviews_product
        FOREIGN KEY (product_id) REFERENCES product_catalog(product_id),
    CONSTRAINT fk_reviews_customer
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id)
);

CREATE INDEX idx_product_catalog_category_id ON product_catalog(category_id);
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_shippings_order_id ON shippings(order_id);
CREATE INDEX idx_product_images_product_id ON product_images(product_id);
CREATE INDEX idx_reviews_product_id ON reviews(product_id);
CREATE INDEX idx_reviews_customer_id ON reviews(customer_id);
