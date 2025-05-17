# 商品管理システム テーブル定義書 (PostgreSQL)

## 1. 概要

本書は、商品管理システムで使用するPostgreSQLデータベースの詳細定義を記載したものです。PostgreSQL固有の機能や型を使用した実装詳細を含みます。

## 2. PostgreSQL特有の設定

### 2.1 使用バージョン

PostgreSQL 15.x

### 2.2 文字セットと照合順序

- **文字セット**: UTF-8
- **照合順序**: C.UTF-8

### 2.3 スキーマ

`public`スキーマを使用

## 3. テーブル詳細定義

### 3.1 商品テーブル (products)

```sql
CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    jan_code VARCHAR(13),
    description TEXT,
    price NUMERIC(10,0) NOT NULL DEFAULT 0,
    category_id INTEGER NOT NULL REFERENCES categories(id),
    stock_quantity INTEGER NOT NULL DEFAULT 0,
    stock_unit VARCHAR(20) NOT NULL DEFAULT '個',
    status VARCHAR(20) NOT NULL DEFAULT '販売中',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_name ON products(name);
```

### 3.2 カテゴリテーブル (categories)

```sql
CREATE TABLE categories (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description TEXT,
    parent_id INTEGER REFERENCES categories(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_categories_parent_id ON categories(parent_id);
```

### 3.3 在庫履歴テーブル (inventory_histories)

```sql
CREATE TABLE inventory_histories (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL REFERENCES products(id),
    quantity_change INTEGER NOT NULL,
    reason VARCHAR(255),
    operated_by INTEGER NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_inventory_histories_product_id ON inventory_histories(product_id);
CREATE INDEX idx_inventory_histories_created_at ON inventory_histories(created_at);
```

### 3.4 ユーザーテーブル (users)

```sql
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_users_username ON users(username);
```

### 3.5 システム設定テーブル (system_settings)

```sql
CREATE TABLE system_settings (
    id SERIAL PRIMARY KEY,
    setting_key VARCHAR(50) NOT NULL UNIQUE,
    setting_value TEXT,
    setting_group VARCHAR(50) NOT NULL,
    description TEXT,
    editable BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_system_settings_key ON system_settings(setting_key);
CREATE INDEX idx_system_settings_group ON system_settings(setting_group);
```

## 4. トリガーと関数

### 4.1 更新日時自動更新トリガー

```sql
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_products_timestamp
BEFORE UPDATE ON products
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER update_categories_timestamp
BEFORE UPDATE ON categories
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER update_users_timestamp
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION update_timestamp();

CREATE TRIGGER update_system_settings_timestamp
BEFORE UPDATE ON system_settings
FOR EACH ROW EXECUTE FUNCTION update_timestamp();
```

### 4.2 在庫更新トリガー

```sql
CREATE OR REPLACE FUNCTION update_stock_after_inventory_change()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE products
    SET stock_quantity = stock_quantity + NEW.quantity_change
    WHERE id = NEW.product_id;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER after_inventory_history_insert
AFTER INSERT ON inventory_histories
FOR EACH ROW EXECUTE FUNCTION update_stock_after_inventory_change();
```

## 5. シーケンス

| シーケンス名 | 開始値 | 増分値 | 最小値 | 最大値 | 循環 | 所有テーブル.カラム |
|--------------|--------|--------|--------|--------|------|---------------------|
| products_id_seq | 1 | 1 | 1 | 2147483647 | NO | products.id |
| categories_id_seq | 1 | 1 | 1 | 2147483647 | NO | categories.id |
| inventory_histories_id_seq | 1 | 1 | 1 | 2147483647 | NO | inventory_histories.id |
| users_id_seq | 1 | 1 | 1 | 2147483647 | NO | users.id |
| system_settings_id_seq | 1 | 1 | 1 | 2147483647 | NO | system_settings.id |

## 6. パーティショニング

このシステムではパーティショニングは使用していませんが、将来的に以下のテーブルにパーティショニングの導入が検討されます：

- **inventory_histories**: 日付によるレンジパーティショニング
  - 履歴データが増えた場合に検索パフォーマンスを向上させるため

## 7. バックアップ対象

| バックアップ対象 | バックアップ頻度 | 保存期間 | 備考 |
|------------------|------------------|----------|------|
| 全データベース | 日次 | 30日 | フルバックアップ |
| WALログ | 1時間毎 | 7日 | ポイントインタイムリカバリ用 |

## 8. PostgreSQL特有の注意事項

- **SERIAL型**: 内部的にはINTEGER型とシーケンスの組み合わせで実装
- **タイムスタンプ**: タイムゾーンなしのTIMESTAMPを使用（システム内で統一的に日本時間で処理）
- **インデックス**: B-treeインデックスをデフォルトで使用
- **外部キー制約**: カスケード更新と制限削除をデフォルトで使用

---

文書作成日: 2025年5月17日  
文書管理番号: DB-DEF-002  
バージョン: 1.0