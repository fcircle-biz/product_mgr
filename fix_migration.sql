-- V1__initial_schema.sql の内容をコピー
-- ユーザーテーブル
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- カテゴリテーブル
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 商品テーブル
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    jan_code VARCHAR(13),
    category_id INTEGER NOT NULL REFERENCES categories(id),
    price NUMERIC(10, 0) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    stock_unit VARCHAR(20) NOT NULL DEFAULT '個',
    reorder_point INTEGER NOT NULL DEFAULT 0,
    description TEXT,
    image_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 在庫履歴テーブル
CREATE TABLE inventory_histories (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES products(id),
    quantity_change INTEGER NOT NULL,
    before_quantity INTEGER NOT NULL,
    after_quantity INTEGER NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    description TEXT,
    created_by INTEGER NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- デフォルト管理者ユーザー（パスワード: admin123）
INSERT INTO users (username, password, full_name, role) 
VALUES ('admin', '$2a$10$HBQZVLHOm1VIHP0.2/JyOuXvdCQe7hMoGxZGvvJ6sXcGJhBWYnJcC', '管理者', 'ADMIN');

-- デフォルトカテゴリ
INSERT INTO categories (name, description) VALUES 
('食料品', '食品・飲料などの商品'),
('日用品', '生活に必要な日用雑貨'),
('文房具', '筆記用具・事務用品など'),
('電化製品', '家電・電子機器類');

-- Flywayの履歴に登録
INSERT INTO flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, installed_on, execution_time, success)
VALUES (1, '1', 'initial schema', 'SQL', 'V1__initial_schema.sql', -1, 'productmgr', NOW(), 0, true);