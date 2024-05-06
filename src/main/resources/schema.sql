-- mysql dialect
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS purchase_order;
drop table if exists book_author;
drop table if exists book;
drop table if exists author;
drop table if exists minion;

CREATE TABLE customer (
                          customer_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                          first_name VARCHAR(20) NOT NULL,
                          date_of_birth DATE NOT NULL
);

CREATE TABLE purchase_order (
                                purchase_order_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                                shipping_address VARCHAR(255)
);

CREATE TABLE order_item (
                            purchase_order BIGINT NOT NULL,
                            quantity INT,
                            product VARCHAR(50),
                            FOREIGN KEY (purchase_order) REFERENCES purchase_order(purchase_order_id)
);

CREATE TABLE book(
    book_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    title varchar(100)
);

CREATE TABLE author(
    author_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name varchar(50)
);

CREATE TABLE book_author(
    book   BIGINT NOT NULL,
    author BIGINT NOT NULL,
    FOREIGN KEY (book) REFERENCES book (book_id),
    FOREIGN KEY (author) REFERENCES author (author_id)
);

CREATE TABLE minion(
    minion_id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name varchar(50)
);