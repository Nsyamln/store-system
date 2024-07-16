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
    supplier_id CHAR(5),
    product_image VARCHAR(255),
    FOREIGN KEY (supplier_id) REFERENCES users(user_id)
);

CREATE TABLE consignment_products (
    consignment_id CHAR(5) PRIMARY KEY,
    product_id CHAR(5),
    supplier_id CHAR(5),
    consignment_date DATE,
    consignment_quantity INT,
    purchase_price DECIMAL(10,2),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (supplier_id) REFERENCES users(user_id)
);

CREATE TABLE sales (
    sale_id CHAR(5) PRIMARY KEY,
    sale_date DATE,
    total_price DECIMAL(10, 2),
    customer_id CHAR(5),
    FOREIGN KEY (customer_id) REFERENCES users(user_id)
);

CREATE TABLE sale_details (
    detail_id CHAR(5) PRIMARY KEY,
    sale_id CHAR(5),
    product_id CHAR(5),
    quantity INT,
    price DECIMAL(10, 2),
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

CREATE TABLE profit_sharing (
    profit_sharing_id CHAR(5) PRIMARY KEY,
    sale_id CHAR(5),
    product_id CHAR(5),
    supplier_id CHAR(5),
    product_quantity INT,
    total_purchase_price DECIMAL(10, 2),
    total_sale_price DECIMAL(10, 2),
    payment_date DATE,
    FOREIGN KEY (sale_id) REFERENCES sales(sale_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    FOREIGN KEY (supplier_id) REFERENCES users(user_id)
);

##------------------------------------------------------------


-- Membuat trigger untuk mengenerate ID Produk secara otomatis
DELIMITER //

CREATE TRIGGER generate_product_id
BEFORE INSERT ON products
FOR EACH ROW
BEGIN
    DECLARE last_id INT;
    DECLARE new_id VARCHAR(10);

    -- Mendapatkan nilai terbesar dari ID yang ada
    SELECT COALESCE(MAX(CAST(SUBSTRING(product_id, 3) AS UNSIGNED)), 0) INTO last_id FROM Produk;

    -- Menambahkan 1 pada nilai terbesar untuk ID baru
    SET new_id = CONCAT('PR', LPAD(last_id + 1, 3, '0'));

    -- Mengatur ID baru pada baris yang akan dimasukkan
    SET NEW.product_id = new_id;
END //

DELIMITER ;


-- Membuat trigger untuk mengenerate ID Penjualan secara otomatis
DELIMITER //

CREATE TRIGGER generate_sale_id
BEFORE INSERT ON sales
FOR EACH ROW
BEGIN
    DECLARE last_id INT;
    DECLARE new_id VARCHAR(10);

    -- Mendapatkan nilai terbesar dari ID yang ada
    SELECT COALESCE(MAX(CAST(SUBSTRING(sale_id, 3) AS UNSIGNED)), 0) INTO last_id FROM Penjualan;

    -- Menambahkan 1 pada nilai terbesar untuk ID baru
    SET new_id = CONCAT('JL', LPAD(last_id + 1, 3, '0'));

    -- Mengatur ID baru pada baris yang akan dimasukkan
    SET NEW.sale_id = new_id;
END //

DELIMITER ;


-- Membuat trigger untuk mengenerate ID USER secara otomatis
DELIMITER //

CREATE TRIGGER generate_user_id
BEFORE INSERT ON users
FOR EACH ROW
BEGIN
    DECLARE last_id INT;
    DECLARE new_id VARCHAR(10);

    -- Mendapatkan nilai terbesar dari ID yang ada
    SELECT COALESCE(MAX(CAST(SUBSTRING(user_id, 3) AS UNSIGNED)), 0) INTO last_id FROM user;

    -- Menambahkan 1 pada nilai terbesar untuk ID baru
    SET new_id = CONCAT('US', LPAD(last_id + 1, 3, '0'));

    -- Mengatur ID baru pada baris yang akan dimasukkan
    SET NEW.user_id = new_id;
END //

DELIMITER ;

-- Membuat trigger untuk mengenerate ID Produk Titipan secara otomatis
DELIMITER //

CREATE TRIGGER generate_consignment_id
BEFORE INSERT ON consignment_products
FOR EACH ROW
BEGIN
    DECLARE last_id INT;
    DECLARE new_id VARCHAR(10);

    -- Mendapatkan nilai terbesar dari ID yang ada
    SELECT COALESCE(MAX(CAST(SUBSTRING(consignment_id, 3) AS UNSIGNED)), 0) INTO last_id FROM produktitipan;

    -- Menambahkan 1 pada nilai terbesar untuk ID baru
    SET new_id = CONCAT('CP', LPAD(last_id + 1, 3, '0'));

    -- Mengatur ID baru pada baris yang akan dimasukkan
    SET NEW.consignment_id = new_id;
END //

DELIMITER ;

-- Membuat trigger untuk mengenerate ID Detail Penjualan secara otomatis
DELIMITER //

CREATE TRIGGER generate_detail_id
BEFORE INSERT ON sale_details 
FOR EACH ROW
BEGIN
    DECLARE last_id INT;
    DECLARE new_id VARCHAR(10);

    -- Mendapatkan nilai terbesar dari ID yang ada
    SELECT COALESCE(MAX(CAST(SUBSTRING(detail_id, 3) AS UNSIGNED)), 0) INTO last_id FROM detailpenjualan;

    -- Menambahkan 1 pada nilai terbesar untuk ID baru
    SET new_id = CONCAT('DS', LPAD(last_id + 1, 3, '0'));

    -- Mengatur ID baru pada baris yang akan dimasukkan
    SET NEW.detail_id = new_id;
END //

DELIMITER ;

-- Membuat trigger untuk mengenerate ID Pembagian Hasil secara otomatis
DELIMITER //

CREATE TRIGGER generate_sharing_id
BEFORE INSERT ON profit_sharing 
FOR EACH ROW
BEGIN
    DECLARE last_id INT;
    DECLARE new_id VARCHAR(10);

    -- Mendapatkan nilai terbesar dari ID yang ada
    SELECT COALESCE(MAX(CAST(SUBSTRING(profit_sharing_id , 3) AS UNSIGNED)), 0) INTO last_id FROM pembagianhasil;

    -- Menambahkan 1 pada nilai terbesar untuk ID baru
    SET new_id = CONCAT('PS', LPAD(last_id + 1, 3, '0'));

    -- Mengatur ID baru pada baris yang akan dimasukkan
    SET NEW.profit_sharing_id  = new_id;
END //

DELIMITER ;

-- Membuat trigger untuk mendapatkan id penjualan terakhir
DELIMITER //

CREATE TRIGGER get_last_sale_id
AFTER INSERT ON sales 
FOR EACH ROW 
BEGIN 
    DECLARE last_id CHAR(5);
    SET last_id = LAST_INSERT_ID();
END //
DELIMITER ;

-- Membuat trigger untuk update total harga di tabel penjualan 
DELIMITER //

CREATE TRIGGER update_total_price
AFTER INSERT ON sale_details
FOR EACH ROW
BEGIN
    DECLARE new_id CHAR(5);
    SET new_id = NEW.sale_id;
    UPDATE sales SET total_price = (SELECT SUM(quantity*price) FROM sale_details WHERE sale_id = new_id)
    WHERE sale_id = new_id;
END //
DELIMITER ;

-- Membuat trigger untuk insert revenue ke tabel profit_sharing
DELIMITER //

CREATE TRIGGER insert_revenue
AFTER INSERT ON sale_details  
FOR EACH ROW 
BEGIN 
    DECLARE new_sale_id CHAR(5);
    DECLARE new_product_id CHAR(5);
    DECLARE new_quantity INT;

    SET new_sale_id = NEW.sale_id;
    SET new_product_id = NEW.product_id;
    SET new_quantity = NEW.quantity;

    INSERT INTO profit_sharing (sale_id, product_id, supplier_id, product_quantity, total_purchase_price, total_sale_price, payment_date)
    SELECT
        new_sale_id,
        new_product_id,
        pr.supplier_id,
        new_quantity,
        (new_quantity * pr.price) AS total_purchase_price,
        (new_quantity * NEW.price) AS total_sale_price,
        CURDATE() -- Gunakan tanggal hari ini sebagai tanggal pembayaran
    FROM
        products pr
    WHERE
        pr.product_id = new_product_id;
END //
DELIMITER ;

-- Membuat trigger untuk pengurangan stok di tabel produk
DELIMITER //

CREATE TRIGGER minus_stock 
AFTER INSERT ON sale_details 
FOR EACH ROW
BEGIN 
    UPDATE products 
    SET stock = stock - NEW.quantity 
    WHERE product_id = NEW.product_id;
END //
DELIMITER ;

-- Membuat trigger untuk penambahan stok di tabel produkTitipan
DELIMITER //
CREATE TRIGGER plus_stock 
AFTER INSERT ON consignment_products
FOR EACH ROW 
BEGIN 
    UPDATE products 
    SET stock = stock + NEW.consignment_quantity
    WHERE product_id = NEW.product_id;
END //
DELIMITER ;

#---------------------------------------------------------------------
INSERT INTO Produk ( nama_produk, deskripsi, harga, stok, id_pemasok, gambar_produk) VALUES
( 'Peuyeum Bandung', 'Tape ketan khas Bandung yang manis dan legit', 25000.00, 50, 'PM001', 'peuyeum_bandung.jpg'),
( 'Oncom Raos', 'Oncom goreng renyah khas Sunda', 20000.00, 100, 'PM002', 'oncom_raos.jpg'),
( 'Sale Pisang', 'Sale pisang khas Jawa Barat yang manis dan gurih', 30000.00, 75, 'PM003', 'sale_pisang.jpg'),
( 'Emping Melinjo', 'Emping melinjo renyah khas Sunda', 35000.00, 120, 'PM004', 'emping_melinjo.jpg'),
( 'Dodol Garut', 'Dodol manis dan kenyal khas Garut', 40000.00, 60, 'PM005', 'dodol_garut.jpg');

INSERT INTO User (email, password, role) VALUES
('nasya@gmail.com', 'smallgirl', 'ADMIN');

INSERT INTO ProdukTitipan ( id_produk, id_pemasok, tanggal_titip, jumlah_titip, harga_beli) VALUES
( 'PR001', 'PM001', '2024-06-01', 50, 20000.00),
( 'PR002', 'PM002', '2024-06-02', 100, 15000.00),
( 'PR003', 'PM003', '2024-06-03', 75, 25000.00),
( 'PR004', 'PM004', '2024-06-04', 120, 30000.00),
( 'PR005', 'PM005', '2024-06-05', 60, 35000.00);

INSERT INTO Penjualan ( tanggal_penjualan, total_harga, id_pelanggan) VALUES
( '2024-06-14', 0, 'PG002');

INSERT INTO DetailPenjualan ( id_penjualan, id_produk, jumlah, harga) VALUES
( 'JL002', 'PR001', 3, 25000.00),
( 'JL002', 'PR002', 2, 20000.00);


SELECT sum(total_harga_beli) FROM pembagianhasil WHERE id_pemasok = 'PM001';

ALTER TABLE user CHANGE username email VARCHAR (255);
ALTER TABLE user MODIFY id_user CHAR(5)