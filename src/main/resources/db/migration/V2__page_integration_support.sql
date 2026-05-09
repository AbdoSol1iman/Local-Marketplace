ALTER TABLE customers
    ADD COLUMN blocked BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE stores (
    store_id SERIAL PRIMARY KEY,
    store_name VARCHAR(120) NOT NULL,
    owner_name VARCHAR(120) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE cart_items (
    customer_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT pk_cart_items PRIMARY KEY (customer_id, product_id),
    CONSTRAINT fk_cart_items_customer
        FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    CONSTRAINT fk_cart_items_product
        FOREIGN KEY (product_id) REFERENCES product_catalog(product_id),
    CONSTRAINT chk_cart_items_quantity_positive CHECK (quantity > 0)
);

CREATE INDEX idx_cart_items_customer_id ON cart_items(customer_id);
CREATE INDEX idx_cart_items_product_id ON cart_items(product_id);
CREATE INDEX idx_stores_active ON stores(active);
