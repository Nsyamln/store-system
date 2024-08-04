CREATE DATABASE ElinStore;
USE ElinStore;

CREATE TABLE users (
    user_id CHAR(5) PRIMARY KEY,
    name VARCHAR (255),
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    phone VARCHAR(15) NOT NULL,   
    created_by CHAR(5) NOT NULL,
    updated_by CHAR(5),
    deleted_by CHAR(5),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

INSERT INTO users (
    name,
    email,
    password,
    role,
    phone,
    created_by,
    updated_by,
    deleted_by,
    created_at,
    updated_at,
    deleted_at
) VALUES (
    'Nasya Aroenia',       -- name
    'aroeni@gmail.com', -- email
    '$2a$10$uIBEJjpFQcMlzSM40cBOBOKrmd6QdcHiPiwtBjm/WjMkrsHgarVTO',-- password (sebaiknya hashed)
    'ADMIN',          -- role
    '08123456789',   -- phone
    'US001',      -- created_by
    NULL,            -- updated_by
    NULL,            -- deleted_by
    NOW(),           -- created_at
    NULL,            -- updated_at
    NULL             -- deleted_at
);


CREATE TABLE addresses (
    address_id CHAR(5) PRIMARY KEY,
    user_id CHAR(5) NOT NULL,
    street TEXT NOT NULL,
    rt VARCHAR(3) NOT NULL,
    rw VARCHAR(3) NOT NULL,
    village VARCHAR(255) NOT NULL,
    district VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    postal_code CHAR(5) NOT NULL
    
);


CREATE TABLE products (
    product_id CHAR(5) PRIMARY KEY,
    product_name VARCHAR(255),
    description TEXT,
    unit INT,
    price DECIMAL(10, 2),
    stock INT,
    product_image VARCHAR(255),

    supplier_id CHAR(5),
    purchase_price DECIMAL(10, 2),
    created_by CHAR(5) NOT NULL,
    updated_by CHAR(5),
    deleted_by CHAR(5),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);


CREATE TABLE sales (
    sale_id CHAR(5) PRIMARY KEY,
    sale_date TIMESTAMP,
    total_price DECIMAL(10, 2),
    customer_id CHAR(5),
    order_id CHAR(5),
    amount_paid DECIMAL(10, 2),
    payment_method VARCHAR(50)
);


CREATE TABLE sale_details (
    detail_id CHAR(5) PRIMARY KEY,
    sale_id CHAR(5),
    product_id CHAR(5),
    product_name VARCHAR(255),
    quantity INT,
    price INT,
    unit INT
);



CREATE TABLE profit_sharing (
    profit_sharing_id CHAR(5) PRIMARY KEY,
    sale_id CHAR(5),
    product_id CHAR(5),
    supplier_id CHAR(5),
    product_quantity INT,
    total_purchase_price DECIMAL(10, 2),
    total_sale_price DECIMAL(10, 2),
    status VARCHAR (32),
    payment_date TIMESTAMP
);

-- Create table for orders
CREATE TABLE orders (
    order_id CHAR(5) PRIMARY KEY,
    order_date TIMESTAMP NOT NULL,
    customer_id CHAR(5) NOT NULL,
    delivery_address TEXT NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_by CHAR(5) NOT NULL,
    updated_by CHAR(5),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);


ALTER TABLE orders
ADD COLUMN shipping_cost DECIMAL(10, 2),
ADD COLUMN tracking_number VARCHAR(50),
ADD COLUMN courier VARCHAR(50),
ADD COLUMN shipping_method VARCHAR(50),
ADD COLUMN estimated_delivery_date TIMESTAMP,
ADD COLUMN actual_delivery_date TIMESTAMP;

alter table addresses add constraint fk_addr1 foreign key (user_id) references users (user_id);
ALTER TABLE sales ADD CONSTRAINT fk_sale2 FOREIGN KEY (order_id) REFERENCES orders (order_id);
ALTER TABLE products ADD CONSTRAINT fk_prod1 FOREIGN KEY (supplier_id) REFERENCES users (user_id);
ALTER TABLE sales ADD CONSTRAINT fk_sale1 FOREIGN KEY (customer_id) REFERENCES users (user_id);
ALTER TABLE sale_details ADD CONSTRAINT fk_detail1 FOREIGN KEY (sale_id) REFERENCES sales (sale_id);
ALTER TABLE sale_details ADD CONSTRAINT fk_detail2 FOREIGN KEY (product_id) REFERENCES products (product_id);
ALTER TABLE profit_sharing ADD CONSTRAINT fk_pros1 FOREIGN KEY (sale_id) REFERENCES sales (sale_id);
ALTER TABLE profit_sharing ADD CONSTRAINT fk_pros2 FOREIGN KEY (product_id) REFERENCES products (product_id);
ALTER TABLE profit_sharing ADD CONSTRAINT fk_pros3 FOREIGN KEY (supplier_id) REFERENCES users (user_id);

-----------------------------------------------------------------------------------------------------------
-- generate id product 
CREATE OR REPLACE FUNCTION generate_product_id() RETURNS TRIGGER AS $$
DECLARE
    last_id INT;
    new_id VARCHAR(10);
BEGIN
    -- Mendapatkan nilai terbesar dari ID yang ada
    SELECT COALESCE(MAX(CAST(SUBSTRING(product_id FROM 3) AS INTEGER)), 0) 
    INTO last_id 
    FROM products;

    -- Menambahkan 1 pada nilai terbesar untuk ID baru
    new_id := CONCAT('PR', LPAD((last_id + 1)::TEXT, 3, '0'));

    -- Mengatur ID baru pada baris yang akan dimasukkan
    NEW.product_id := new_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER generate_product_id
BEFORE INSERT ON products
FOR EACH ROW
EXECUTE FUNCTION generate_product_id();
------------------------------------------ generate id address 
CREATE OR REPLACE FUNCTION generate_address_id() RETURNS TRIGGER AS $$
DECLARE
    last_id INT;
    new_id VARCHAR(10);
BEGIN
    -- Mendapatkan nilai terbesar dari ID yang ada
    SELECT COALESCE(MAX(CAST(SUBSTRING(address_id FROM 3) AS INTEGER)), 0) 
    INTO last_id 
    FROM addresses;

    -- Menambahkan 1 pada nilai terbesar untuk ID baru
    new_id := CONCAT('AD', LPAD((last_id + 1)::TEXT, 3, '0'));

    -- Mengatur ID baru pada baris yang akan dimasukkan
    NEW.address_id := new_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER generate_address_id
BEFORE INSERT ON addresses
FOR EACH ROW
EXECUTE FUNCTION generate_address_id();

------------------------------------------ generate id user
CREATE OR REPLACE FUNCTION generate_user_id() RETURNS TRIGGER AS $$
DECLARE
    last_id INT;
    new_id VARCHAR(10);
BEGIN
    -- Mendapatkan nilai terbesar dari ID yang ada
    SELECT COALESCE(MAX(CAST(SUBSTRING(user_id FROM 3) AS INTEGER)), 0) 
    INTO last_id 
    FROM users;

    -- Menambahkan 1 pada nilai terbesar untuk ID baru
    new_id := CONCAT('US', LPAD((last_id + 1)::TEXT, 3, '0'));

    -- Mengatur ID baru pada baris yang akan dimasukkan
    NEW.user_id := new_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER generate_user_id
BEFORE INSERT ON users
FOR EACH ROW
EXECUTE FUNCTION generate_user_id();

------------------------------------------ generate id sale
CREATE OR REPLACE FUNCTION generate_sale_id() RETURNS TRIGGER AS $$
DECLARE
    last_id INT;
    new_id VARCHAR(10);
BEGIN
    -- Mendapatkan nilai terbesar dari ID yang ada
    SELECT COALESCE(MAX(CAST(SUBSTRING(sale_id FROM 3) AS INTEGER)), 0) 
    INTO last_id 
    FROM sales;

    -- Menambahkan 1 pada nilai terbesar untuk ID baru
    new_id := CONCAT('PJ', LPAD((last_id + 1)::TEXT, 3, '0'));

    -- Mengatur ID baru pada baris yang akan dimasukkan
    NEW.sale_id := new_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER generate_sale_id
BEFORE INSERT ON sales
FOR EACH ROW
EXECUTE FUNCTION generate_sale_id();
------------------------------------------ generate id provit sharing
CREATE OR REPLACE FUNCTION generate_sharing_id() RETURNS TRIGGER AS $$
DECLARE
    last_id INT;
    new_id VARCHAR(10);
BEGIN
    -- Mendapatkan nilai terbesar dari ID yang ada
    SELECT COALESCE(MAX(CAST(SUBSTRING(profit_sharing_id FROM 3) AS INTEGER)), 0) 
    INTO last_id 
    FROM profit_sharing;

    -- Menambahkan 1 pada nilai terbesar untuk ID baru
    new_id := CONCAT('PS', LPAD((last_id + 1)::TEXT, 3, '0'));

    -- Mengatur ID baru pada baris yang akan dimasukkan
    NEW.profit_sharing_id := new_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER generate_sharing_id
BEFORE INSERT ON profit_sharing
FOR EACH ROW
EXECUTE FUNCTION generate_sharing_id();
------------------------------------------ generate id sale details
CREATE OR REPLACE FUNCTION generate_detail_id() RETURNS TRIGGER AS $$
DECLARE
    last_id INT;
    new_id VARCHAR(10);
BEGIN
    -- Mendapatkan nilai terbesar dari ID yang ada
    SELECT COALESCE(MAX(CAST(SUBSTRING(detail_id FROM 3) AS INTEGER)), 0) 
    INTO last_id 
    FROM sale_details ;

    -- Menambahkan 1 pada nilai terbesar untuk ID baru
    new_id := CONCAT('DP', LPAD((last_id + 1)::TEXT, 3, '0'));

    -- Mengatur ID baru pada baris yang akan dimasukkan
    NEW.detail_id := new_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER generate_detail_id
BEFORE INSERT ON sale_details
FOR EACH ROW
EXECUTE FUNCTION generate_detail_id();
------------------------------------------------------------ generate id order
CREATE OR REPLACE FUNCTION generate_order_id() RETURNS TRIGGER AS $$
DECLARE
last_id INT;
    new_id VARCHAR(10);
BEGIN
    -- Mendapatkan nilai terbesar dari ID yang ada
SELECT COALESCE(MAX(CAST(SUBSTRING(order_id FROM 3) AS INTEGER)), 0)
INTO last_id
FROM orders;

-- Menambahkan 1 pada nilai terbesar untuk ID baru
new_id := CONCAT('OR', LPAD((last_id + 1)::TEXT, 3, '0'));

    -- Mengatur ID baru pada baris yang akan dimasukkan
    NEW.order_id := new_id;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER generate_order_id
    BEFORE INSERT ON orders
    FOR EACH ROW
    EXECUTE FUNCTION generate_order_id();

------------------------------------------------------------ update total harga  
CREATE OR REPLACE FUNCTION update_total_price() RETURNS TRIGGER AS $$
DECLARE
    local_order_id CHAR(5);  -- Menggunakan nama variabel yang berbeda untuk menghindari ambiguitas
    local_shipping_cost DECIMAL(10, 2);
BEGIN
    -- Ambil order_id dari tabel sales berdasarkan sale_id
    SELECT s.order_id INTO local_order_id FROM sales s WHERE s.sale_id = NEW.sale_id;

    -- Ambil shipping_cost dari tabel orders berdasarkan local_order_id
    SELECT COALESCE(o.shipping_cost, 0) INTO local_shipping_cost
    FROM orders o
    WHERE o.order_id = local_order_id;

    -- Update total_price di tabel sales
    UPDATE sales
    SET total_price = (
        SELECT COALESCE(SUM(sd.quantity * sd.price), 0) 
        FROM sale_details sd 
        WHERE sd.sale_id = NEW.sale_id
    ) + local_shipping_cost
    WHERE sale_id = NEW.sale_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;



CREATE TRIGGER update_total_price
AFTER INSERT ON sale_details
FOR EACH ROW
EXECUTE FUNCTION update_total_price();




------------------------------------------------------------minus stok 
CREATE OR REPLACE FUNCTION minus_stock() RETURNS TRIGGER AS $$
BEGIN
    UPDATE products
    SET stock = stock - NEW.quantity
    WHERE product_id = NEW.product_id;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER minus_stock
AFTER INSERT ON sale_details
FOR EACH ROW
EXECUTE FUNCTION minus_stock();

------------------------------------------------------------insert revenue 
CREATE OR REPLACE FUNCTION insert_revenue() RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO profit_sharing (sale_id, product_id, supplier_id, product_quantity, total_purchase_price, total_sale_price)
    SELECT
        NEW.sale_id,
        NEW.product_id,
        pr.supplier_id,
        NEW.quantity,
        (NEW.quantity * pr.purchase_price) AS total_purchase_price,
        (NEW.quantity * NEW.price) AS total_sale_price
    FROM
        products pr
    WHERE
        pr.product_id = NEW.product_id
    LIMIT 1;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER insert_revenue
AFTER INSERT ON sale_details
FOR EACH ROW
EXECUTE FUNCTION insert_revenue();




delete from users where user_id = 'US002';
delete from users where user_id = 'US003';
delete from users where user_id = 'US004';

delete from users where user_id = 'US005';

delete from users where user_id = 'US007';
delete from users where user_id = 'US008';

DELETE FROM addresses WHERE user_id = 'US005';
DELETE FROM addresses WHERE user_id = 'US007';
DELETE FROM addresses WHERE user_id = 'US008';


delete from addresses where address_id = 'AD005';
delete from addresses where address_id = 'AD007';
delete from addresses where address_id = 'AD008';

INSERT INTO orders (order_date, customer_id, delivery_address, status, created_by, created_at)
VALUES (CURRENT_TIMESTAMP, 'US003', 'Jl. Budiasih Kecamatan Sindangkasih Kabupaten Ciamis 46268', 'PENDING', 'US003', CURRENT_TIMESTAMP);


