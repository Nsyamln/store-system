CREATE DATABASE ElinStore;
USE ElinStore;

CREATE TABLE users (
    user_id CHAR(5) PRIMARY KEY,
    name VARCHAR (255),
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL,
    address TEXT NOT NULL,
    phone VARCHAR(15) NOT NULL,   
    created_by CHAR(5) NOT NULL,
    updated_by CHAR(5),
    deleted_by CHAR(5),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE products (
    product_id CHAR(5) PRIMARY KEY,
    product_name VARCHAR(255),
    description TEXT,
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
    sale_date DATE,
    total_price DECIMAL(10, 2),
    customer_id CHAR(5)
);

CREATE TABLE sale_details (
    detail_id CHAR(5) PRIMARY KEY,
    sale_id CHAR(5),
    product_id CHAR(5),
    quantity INT,
    price DECIMAL(10, 2)
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
    payment_date DATE
);

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

------------------------------------------ generate id user
CREATE OR REPLACE FUNCTION generate_user_id() RETURNS TRIGGER AS $$
DECLARE
    last_id INT;
    new_id VARCHAR(10);
BEGIN
    -- Mendapatkan nilai terbesar dari ID yang ada
    SELECT COALESCE(MAX(CAST(SUBSTRING(product_id FROM 3) AS INTEGER)), 0) 
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
    SELECT COALESCE(MAX(CAST(SUBSTRING(product_id FROM 3) AS INTEGER)), 0) 
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
    SELECT COALESCE(MAX(CAST(SUBSTRING(product_id FROM 3) AS INTEGER)), 0) 
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
    SELECT COALESCE(MAX(CAST(SUBSTRING(product_id FROM 3) AS INTEGER)), 0) 
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

------------------------------------------------------------ update total harga  
CREATE OR REPLACE FUNCTION update_total_price() RETURNS TRIGGER AS $$
BEGIN
    UPDATE sales
    SET total_price = (SELECT COALESCE(SUM(quantity * price), 0) FROM sale_details WHERE sale_id = NEW.sale_id)
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



