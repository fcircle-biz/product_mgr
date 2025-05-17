-- E2Eテスト用スキーマ
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

-- 商品テーブル（本番環境互換）
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    jan_code VARCHAR(13) UNIQUE,
    category_id INTEGER NOT NULL REFERENCES categories(id),
    price NUMERIC(10, 0) NOT NULL,
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    stock_unit VARCHAR(20) NOT NULL DEFAULT '個',
    status VARCHAR(20) NOT NULL DEFAULT '販売中',
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 在庫履歴テーブル
CREATE TABLE IF NOT EXISTS inventory_histories (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES products(id),
    quantity_change INTEGER NOT NULL,
    reason VARCHAR(100),
    operated_by INTEGER REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- システム設定テーブル
CREATE TABLE IF NOT EXISTS system_settings (
    id SERIAL PRIMARY KEY,
    key VARCHAR(100) NOT NULL UNIQUE,
    value VARCHAR(500) NOT NULL,
    description TEXT,
    category VARCHAR(50),
    data_type VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 初期データ
-- 管理者ユーザー（パスワード: admin）
INSERT INTO users (username, password, full_name, role, enabled)
VALUES ('admin', '$2a$12$sjRM4ziNZGOElrZrLa3WFeGFgmn4qJNLyMkG/Q8hdHcOvfgiDiIYK', '管理者', 'ADMIN', true);

-- テスト用ユーザー（パスワード: test123）
INSERT INTO users (username, password, full_name, role, enabled)
VALUES ('testuser', '$2a$10$KxT.qWe7kN7u6FoizfGmse7tCRWQ9Frs9.jPJj7aAELAiCBWgBpxO', 'テストユーザー', 'USER', true);

-- カテゴリ
INSERT INTO categories (name, description) VALUES
('文房具', '文房具カテゴリには、筆記用具や紙製品などが含まれます。'),
('家電', '家電カテゴリには、電子機器や電化製品が含まれます。'),
('食品', '食品カテゴリには、各種食料品や飲料が含まれます。'),
('日用品', '日用品カテゴリには、日常生活で使用する消耗品や雑貨が含まれます。');

-- 商品データ
INSERT INTO products (name, jan_code, category_id, price, stock_quantity, stock_unit, status, description) VALUES
('ボールペン（黒）', '4902778001066', 1, 150, 100, '本', '販売中', '滑らかな書き心地の油性ボールペン'),
('ノートA4', '4901480010543', 1, 250, 50, '冊', '販売中', 'A4サイズの大学ノート'),
('蛍光ペン5色セット', '4901480274157', 1, 500, 30, 'セット', '販売中', '5色の蛍光ペンセット'),
('電卓12桁', '4974052804441', 2, 1500, 20, '個', '販売中', '12桁表示の電卓'),
('USBメモリ32GB', '4988755041379', 2, 2000, 15, '個', '販売中', '32GBのUSBメモリ'),
('モバイルバッテリー', '4589584290214', 2, 3500, 10, '個', '販売中', '10000mAhモバイルバッテリー'),
('インスタントコーヒー', '4901111270194', 3, 800, 25, '個', '販売中', 'インスタントコーヒー100g'),
('緑茶ティーバッグ', '4901085614082', 3, 450, 40, '箱', '販売中', '緑茶ティーバッグ20袋入り'),
('チョコレート', '4902555116464', 3, 300, 60, '個', '販売中', 'ミルクチョコレート'),
('ティッシュペーパー', '4901750436520', 4, 350, 80, '箱', '販売中', 'ティッシュペーパー5箱パック'),
('洗剤', '4902430884952', 4, 600, 35, '個', '販売中', '衣類用洗剤1.5kg'),
('シャンプー', '4901872468546', 4, 750, 25, '本', '販売中', 'シャンプー500ml');

-- 在庫履歴
INSERT INTO inventory_histories (product_id, quantity_change, reason, operated_by) VALUES
(1, 100, '初期在庫', 1),
(2, 50, '初期在庫', 1),
(3, 30, '初期在庫', 1),
(4, 20, '初期在庫', 1),
(5, 15, '初期在庫', 1);

-- システム設定
INSERT INTO system_settings (key, value, description, category, data_type) VALUES
('company_name', '在庫管理システム', '会社名', 'general', 'STRING'),
('currency_symbol', '¥', '通貨記号', 'general', 'STRING'),
('date_format', 'yyyy-MM-dd', '日付形式', 'general', 'STRING'),
('items_per_page', '20', '1ページあたりの表示件数', 'display', 'INTEGER'),
('low_stock_threshold', '10', '在庫少警告の閾値', 'inventory', 'INTEGER'),
('enable_notifications', 'true', '通知機能の有効化', 'notification', 'BOOLEAN'),
('business_hours_start', '09:00', '営業開始時間', 'business', 'STRING'),
('business_hours_end', '18:00', '営業終了時間', 'business', 'STRING'),
('tax_rate', '10', '消費税率（%）', 'finance', 'INTEGER');