-- サンプルデータ（任意）

-- 商品サンプルデータ
INSERT INTO products (name, jan_code, category_id, price, stock_quantity, stock_unit, description, status)
SELECT 'ボールペン（黒）', '4902778001066', 1, 150, 100, '本', '滑らかな書き心地の油性ボールペン', '販売中'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE jan_code = '4902778001066');

INSERT INTO products (name, jan_code, category_id, price, stock_quantity, stock_unit, description, status)
SELECT 'ノート A4', '4901480321066', 1, 200, 50, '冊', 'A4サイズの方眼ノート', '販売中'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE jan_code = '4901480321066');

INSERT INTO products (name, jan_code, category_id, price, stock_quantity, stock_unit, description, status)
SELECT '電卓', '4903767019885', 2, 1500, 5, '個', '12桁表示の実務電卓', '販売中'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE jan_code = '4903767019885');

INSERT INTO products (name, jan_code, category_id, price, stock_quantity, stock_unit, description, status)
SELECT 'インスタントコーヒー', '4901111231166', 3, 500, 30, '個', '90g入りインスタントコーヒー', '販売中'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE jan_code = '4901111231166');

INSERT INTO products (name, jan_code, category_id, price, stock_quantity, stock_unit, description, status)
SELECT 'ティッシュペーパー', '4901750177105', 4, 300, 0, '箱', '5箱入りティッシュペーパー', '販売中'
WHERE NOT EXISTS (SELECT 1 FROM products WHERE jan_code = '4901750177105');

-- システム設定の初期データ
INSERT INTO system_settings (key, value, description)
SELECT 'company_name', '株式会社サンプル', '会社名'
WHERE NOT EXISTS (SELECT 1 FROM system_settings WHERE key = 'company_name');

INSERT INTO system_settings (key, value, description)
SELECT 'low_stock_threshold', '10', '在庫少の判定基準'
WHERE NOT EXISTS (SELECT 1 FROM system_settings WHERE key = 'low_stock_threshold');

INSERT INTO system_settings (key, value, description)
SELECT 'backup_interval', '7', 'バックアップ間隔（日）'
WHERE NOT EXISTS (SELECT 1 FROM system_settings WHERE key = 'backup_interval');