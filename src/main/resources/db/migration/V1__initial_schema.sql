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
    status VARCHAR(20) NOT NULL DEFAULT '販売中',
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 在庫履歴テーブル
CREATE TABLE inventory_history (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES products(id),
    type VARCHAR(20) NOT NULL,  -- 入庫/出庫
    quantity INTEGER NOT NULL,
    reason VARCHAR(50) NOT NULL,
    note TEXT,
    user_id INTEGER NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 管理者ユーザーの追加
INSERT INTO users (username, password, full_name, role, enabled)
VALUES ('admin', '$2a$10$rJuf3OCQaGLK7O4BGrLBZu.G1QZcfKUOyS28qBOWqWIFRXIlJLu1S', '管理者', 'ADMIN', true);

-- カテゴリのサンプルデータ
INSERT INTO categories (name, description)
VALUES 
    ('文房具', '文房具カテゴリには、筆記用具や紙製品などが含まれます。'),
    ('家電', '家電カテゴリには、電子機器や電化製品が含まれます。'),
    ('食品', '食品カテゴリには、各種食料品や飲料が含まれます。'),
    ('日用品', '日用品カテゴリには、日常生活で使用する消耗品や雑貨が含まれます。');

-- サンプル商品の追加
INSERT INTO products (name, jan_code, category_id, price, stock_quantity, stock_unit, status, description)
VALUES 
    ('高級ボールペン シルバー', '4901234567890', 1, 1200, 120, '個', '販売中', '高級感のあるシルバー仕上げのボールペンです。程よい重量感と書き心地の良さが特徴で、ビジネスシーンでの使用に最適です。替えインクは一般的なタイプが使用可能です。'),
    ('デスクライト LED', '4902345678901', 2, 3500, 5, '個', '販売中', '省エネLEDライトを使用したデスクライトです。3段階の明るさ調整と色温度調整機能付きで、様々な用途に対応します。'),
    ('ワイヤレスヘッドフォン', '4903456789012', 2, 12800, 0, '個', '入荷待ち', '高音質と快適な装着感を両立したワイヤレスヘッドフォンです。ノイズキャンセリング機能とバッテリー長持ち設計で、移動中の使用にも最適です。'),
    ('オーガニックコーヒー 200g', '4904567890123', 3, 980, 45, '個', '販売中', '有機栽培されたコーヒー豆を使用したオーガニックコーヒーです。濃厚な味わいと香りが特徴で、朝の一杯に最適です。'),
    ('メモ帳 A5サイズ', '4905678901234', 1, 350, 200, '個', '販売中', 'A5サイズの使いやすいメモ帳です。罫線入りで筆記がしやすく、ビジネスやスタディーにも最適です。');

-- サンプル在庫履歴
INSERT INTO inventory_history (product_id, type, quantity, reason, note, user_id)
VALUES 
    (1, '入庫', 100, '初期在庫', '初期登録', 1),
    (1, '入庫', 50, '通常入荷', '定期発注分', 1),
    (1, '出庫', 30, '販売', 'オンライン注文', 1),
    (2, '入庫', 10, '初期在庫', '初期登録', 1),
    (2, '出庫', 5, '販売', '店舗販売', 1),
    (3, '入庫', 5, '初期在庫', '初期登録', 1),
    (3, '出庫', 5, '販売', 'オンライン注文', 1),
    (4, '入庫', 50, '初期在庫', '初期登録', 1),
    (4, '出庫', 5, '販売', '店舗販売', 1),
    (5, '入庫', 200, '初期在庫', '初期登録', 1);

-- 在庫自動更新のためのトリガー関数
CREATE OR REPLACE FUNCTION update_product_stock() RETURNS TRIGGER AS $$
BEGIN
    IF NEW.type = '入庫' THEN
        UPDATE products SET 
            stock_quantity = stock_quantity + NEW.quantity,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = NEW.product_id;
    ELSIF NEW.type = '出庫' THEN
        UPDATE products SET 
            stock_quantity = stock_quantity - NEW.quantity,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = NEW.product_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER inventory_history_after_insert
AFTER INSERT ON inventory_history
FOR EACH ROW
EXECUTE FUNCTION update_product_stock();