-- テスト用初期データ
INSERT INTO users (username, password, full_name, role, enabled)
VALUES 
    ('testuser', '$2a$12$EAiLrpUhvUG2HPvtNu7cwujnaT8mnBRpjcKzuA0jXCdQI90tFiuUS', 'テストユーザー', 'USER', true),
    ('testadmin', '$2a$12$p.RumIjyXExXnToZtVwtxOGe51/CLUic7rODfP7C4DHstadcX0gjO', 'テスト管理者', 'ADMIN', true);

INSERT INTO categories (name, description)
VALUES 
    ('テストカテゴリ1', 'テスト用カテゴリ1の説明'),
    ('テストカテゴリ2', 'テスト用カテゴリ2の説明');

INSERT INTO system_settings (key, value, description, category, data_type)
VALUES 
    ('low_stock_threshold', '10', '在庫少の閾値', 'inventory', 'INTEGER'),
    ('company_name', 'テスト会社', '会社名', 'general', 'STRING');
    
-- 商品データは必要に応じてテストケース内で作成する