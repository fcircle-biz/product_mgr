# 商品管理システム ER図

このドキュメントでは、商品管理システムのデータベースER図をMermaid記法で記述しています。

## ER図（Mermaid）

```mermaid
erDiagram
    PRODUCTS ||--o{ INVENTORY_HISTORIES : "has"
    CATEGORIES ||--o{ PRODUCTS : "contains"
    CATEGORIES ||--o{ CATEGORIES : "is parent of"
    USERS ||--o{ INVENTORY_HISTORIES : "operates"
    
    PRODUCTS {
        int id PK
        string name
        string jan_code
        text description
        decimal price
        int category_id FK
        int stock_quantity
        string stock_unit
        string status
        timestamp created_at
        timestamp updated_at
    }
    
    CATEGORIES {
        int id PK
        string name
        text description
        int parent_id FK
        timestamp created_at
        timestamp updated_at
    }
    
    INVENTORY_HISTORIES {
        int id PK
        int product_id FK
        int quantity_change
        string reason
        int operated_by FK
        timestamp created_at
    }
    
    USERS {
        int id PK
        string username
        string password
        string full_name
        string role
        boolean enabled
        timestamp created_at
        timestamp updated_at
    }
    
    SYSTEM_SETTINGS {
        int id PK
        string setting_key
        text setting_value
        string setting_group
        text description
        boolean editable
        timestamp created_at
        timestamp updated_at
    }
```

## テーブル関連図（詳細な関係性）

```mermaid
graph TD
    Products[商品 products] --> |belongs to| Categories[カテゴリ categories]
    Categories --> |can have parent| Categories
    Products --> |has many| InventoryHistories[在庫履歴 inventory_histories]
    InventoryHistories --> |operated by| Users[ユーザー users]
    
    subgraph Products
        p_id[id PK]
        p_name[name]
        p_price[price]
        p_stock[stock_quantity]
    end
    
    subgraph Categories
        c_id[id PK]
        c_name[name]
        c_parent[parent_id FK]
    end
    
    subgraph InventoryHistories
        ih_id[id PK]
        ih_product[product_id FK]
        ih_change[quantity_change]
        ih_operator[operated_by FK]
    end
    
    subgraph Users
        u_id[id PK]
        u_name[username]
        u_role[role]
    end
    
    subgraph SystemSettings
        ss_id[id PK]
        ss_key[setting_key]
        ss_value[setting_value]
    end
```

## 主要リレーション

1. **商品(PRODUCTS) - カテゴリ(CATEGORIES)**
   - 多対1の関係
   - 各商品は1つのカテゴリに所属する
   - 各カテゴリは複数の商品を持つことができる

2. **カテゴリ(CATEGORIES) - カテゴリ(CATEGORIES)**
   - 自己参照の多対1の関係
   - 各カテゴリは1つの親カテゴリを持つことができる（親カテゴリがない場合もある）
   - 各カテゴリは複数の子カテゴリを持つことができる

3. **商品(PRODUCTS) - 在庫履歴(INVENTORY_HISTORIES)**
   - 1対多の関係
   - 各商品は複数の在庫履歴を持つことができる
   - 各在庫履歴は1つの商品に関連付けられる

4. **ユーザー(USERS) - 在庫履歴(INVENTORY_HISTORIES)**
   - 1対多の関係
   - 各ユーザーは複数の在庫操作（在庫履歴の作成）を行うことができる
   - 各在庫履歴は1人のユーザーによって操作される

## テーブル概要

- **PRODUCTS**: 商品の基本情報と在庫情報を管理するマスタテーブル
- **CATEGORIES**: 商品カテゴリを管理するマスタテーブル
- **INVENTORY_HISTORIES**: 在庫の入出庫履歴を記録するテーブル
- **USERS**: システムのユーザー情報を管理するテーブル
- **SYSTEM_SETTINGS**: アプリケーション全体の設定を管理するテーブル

## 注記

- 在庫数量(stock_quantity)は products テーブルに直接保持される
- 在庫変更は inventory_histories テーブルに記録され、トリガーによって products テーブルの在庫数量が更新される
- カテゴリは階層構造を持ち、自己参照によって表現される