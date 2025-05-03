-- inventory_history テーブルの名前を inventory_histories に変更
ALTER TABLE inventory_history RENAME TO inventory_histories;

-- 既存のデータを一時保存するための一時テーブルを作成
CREATE TEMPORARY TABLE tmp_inventory_histories AS
SELECT id, product_id, type, quantity, reason, note, user_id, created_at
FROM inventory_histories;

-- テーブルを再作成してデータを移行
-- まずは古いテーブルを削除（制約も自動的に削除される）
DROP TABLE inventory_histories;

-- 新しい仕様に沿ったテーブルを作成
CREATE TABLE inventory_histories (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES products(id),
    quantity_change INTEGER NOT NULL,
    reason VARCHAR(255),  -- 仕様書に合わせて型変更 + NULL許容に
    operated_by INTEGER NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- データの移行（type を考慮して quantity_change を設定）
INSERT INTO inventory_histories (id, product_id, quantity_change, reason, operated_by, created_at)
SELECT id, product_id, 
       CASE WHEN type = '入庫' THEN quantity ELSE -quantity END as quantity_change, 
       reason, 
       user_id as operated_by, 
       created_at
FROM tmp_inventory_histories;

-- インデックスの作成
CREATE INDEX idx_inventory_histories_product_id ON inventory_histories(product_id);
CREATE INDEX idx_inventory_histories_created_at ON inventory_histories(created_at);

-- 在庫自動更新のためのトリガー関数を更新
CREATE OR REPLACE FUNCTION update_product_stock() RETURNS TRIGGER AS $$
BEGIN
    -- quantity_change が正なら入庫、負なら出庫
    UPDATE products SET 
        stock_quantity = stock_quantity + NEW.quantity_change,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.product_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- トリガーの再作成
DROP TRIGGER IF EXISTS inventory_history_after_insert ON inventory_histories;

CREATE TRIGGER inventory_histories_after_insert
AFTER INSERT ON inventory_histories
FOR EACH ROW
EXECUTE FUNCTION update_product_stock();