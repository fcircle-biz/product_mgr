-- データベース初期化スクリプト（Flyway無効化後）

-- ユーザーテーブル
CREATE TABLE IF NOT EXISTS users (
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
CREATE TABLE IF NOT EXISTS categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 商品テーブル
CREATE TABLE IF NOT EXISTS products (
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
CREATE TABLE IF NOT EXISTS inventory_histories (
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

-- システム設定テーブル
CREATE TABLE IF NOT EXISTS system_settings (
    id SERIAL PRIMARY KEY,
    key VARCHAR(100) NOT NULL UNIQUE,
    value VARCHAR(500) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 初期データ（存在しない場合のみ挿入）
INSERT INTO users (username, password, full_name, role, enabled)
SELECT 'admin', '$2a$12$sjRM4ziNZGOElrZrLa3WFeGFgmn4qJNLyMkG/Q8hdHcOvfgiDiIYK', '管理者', 'ADMIN', true
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

-- カテゴリの初期データ
INSERT INTO categories (name, description)
SELECT '文房具', '文房具カテゴリには、筆記用具や紙製品などが含まれます。'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '文房具');

INSERT INTO categories (name, description)
SELECT '家電', '家電カテゴリには、電子機器や電化製品が含まれます。'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '家電');

INSERT INTO categories (name, description)
SELECT '食品', '食品カテゴリには、各種食料品や飲料が含まれます。'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '食品');

INSERT INTO categories (name, description)
SELECT '日用品', '日用品カテゴリには、日常生活で使用する消耗品や雑貨が含まれます。'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '日用品');