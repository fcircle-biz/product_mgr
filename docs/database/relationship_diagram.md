# 商品管理システム テーブル関連図

このドキュメントでは、商品管理システムのテーブル間の関連性をMermaid記法で記述しています。リレーションシップの詳細や制約についても記載しています。

## テーブル関連図

```mermaid
flowchart TB
    subgraph Database
        direction TB
        
        subgraph EntityRelationships
            direction LR
            Products --- |1| hasCategory{belongs to} --- |*| Categories
            Categories --- |0..1| hasParent{has parent} --- |*| Categories
            Products --- |1| hasHistory{has} --- |*| InventoryHistories
            Users --- |1| operates{operates} --- |*| InventoryHistories
        end
        
        subgraph Tables
            direction TB
            
            Products[<strong>products</strong>]
            Categories[<strong>categories</strong>]
            InventoryHistories[<strong>inventory_histories</strong>]
            Users[<strong>users</strong>]
            SystemSettings[<strong>system_settings</strong>]
        end
        
        subgraph Products
            direction TB
            p_id["🔑 id (PK)"]
            p_name["name"]
            p_jan["jan_code"]
            p_desc["description"]
            p_price["price"]
            p_cat["category_id (FK)"]
            p_qty["stock_quantity"]
            p_unit["stock_unit"]
            p_status["status"]
            p_created["created_at"]
            p_updated["updated_at"]
        end
        
        subgraph Categories
            direction TB
            c_id["🔑 id (PK)"]
            c_name["name"]
            c_desc["description"]
            c_parent["parent_id (FK, self-ref)"]
            c_created["created_at"]
            c_updated["updated_at"]
        end
        
        subgraph InventoryHistories
            direction TB
            ih_id["🔑 id (PK)"]
            ih_prod["product_id (FK)"]
            ih_change["quantity_change"]
            ih_reason["reason"]
            ih_op["operated_by (FK)"]
            ih_created["created_at"]
        end
        
        subgraph Users
            direction TB
            u_id["🔑 id (PK)"]
            u_name["username"]
            u_pass["password"]
            u_full["full_name"]
            u_role["role"]
            u_enabled["enabled"]
            u_created["created_at"]
            u_updated["updated_at"]
        end
        
        subgraph SystemSettings
            direction TB
            s_id["🔑 id (PK)"]
            s_key["setting_key"]
            s_val["setting_value"]
            s_group["setting_group"]
            s_desc["description"]
            s_edit["editable"]
            s_created["created_at"]
            s_updated["updated_at"]
        end
        
        %% Foreign Key links
        p_cat -..-> c_id
        c_parent -..-> c_id
        ih_prod -..-> p_id
        ih_op -..-> u_id
    end
    
    style Products fill:#f9f7f7,stroke:#577399,color:#333
    style Categories fill:#f9f7f7,stroke:#577399,color:#333
    style InventoryHistories fill:#f9f7f7,stroke:#577399,color:#333
    style Users fill:#f9f7f7,stroke:#577399,color:#333
    style SystemSettings fill:#f9f7f7,stroke:#577399,color:#333
    
    classDef primaryKey color:#e63946,font-weight:bold
    classDef foreignKey color:#2a9d8f,font-style:italic
    
    class p_id,c_id,ih_id,u_id,s_id primaryKey
    class p_cat,c_parent,ih_prod,ih_op foreignKey
```

## リレーションシップ詳細

### 商品 ⟷ カテゴリ間のリレーション
```mermaid
flowchart LR
    Products[商品] --"N (多)"--- rel1{belongs to} --"1 (1)"--- Categories[カテゴリ]
    
    subgraph Constraints["制約詳細"]
        direction TB
        c1["・各商品は必ず1つのカテゴリに所属する (NOT NULL)"]
        c2["・カテゴリを削除する前に、所属する商品を確認/移動する必要がある (ON DELETE RESTRICT)"]
        c3["・カテゴリが更新された場合、関連する商品のカテゴリIDも自動的に更新される (ON UPDATE CASCADE)"]
    end
```

### カテゴリの階層構造（自己参照）
```mermaid
flowchart TB
    subgraph "カテゴリ階層構造"
        direction TB
        Cat1["電化製品 (ID: 1)"] --> |"parent_id = NULL"| Root["(Root)"]
        Cat2["ノートPC (ID: 2)"] --> |"parent_id = 1"| Cat1
        Cat3["デスクトップPC (ID: 3)"] --> |"parent_id = 1"| Cat1
        Cat4["Windowsノート (ID: 4)"] --> |"parent_id = 2"| Cat2
        Cat5["Macノート (ID: 5)"] --> |"parent_id = 2"| Cat2
    end
```

### 商品 ⟷ 在庫履歴間のリレーション
```mermaid
flowchart LR
    Products[商品] --"1 (1)"--- rel1{has} --"N (多)"--- Histories[在庫履歴]
    
    subgraph Operation["在庫操作フロー"]
        direction TB
        op1["1. 在庫増減操作実行"]
        op2["2. inventory_historiesテーブルに記録"]
        op3["3. トリガーにより商品テーブルの在庫数を自動更新"]
        op1 --> op2 --> op3
    end
```

### ユーザー ⟷ 在庫履歴間のリレーション
```mermaid
flowchart LR
    Users[ユーザー] --"1 (1)"--- rel1{operates} --"N (多)"--- Histories[在庫履歴]
    
    subgraph Authorization["権限制御"]
        direction TB
        auth1["・ADMINロール: すべての操作が可能"]
        auth2["・USERロール: 在庫操作のみ可能"]
        auth3["・監査ログ: 誰がいつどのような操作をしたかを追跡"]
    end
```

## トリガーによる自動処理

### 在庫更新トリガー
```mermaid
sequenceDiagram
    actor User as ユーザー
    participant UI as ユーザーインターフェース
    participant App as アプリケーション
    participant IH as inventory_histories
    participant Trigger as データベーストリガー
    participant Products as products
    
    User->>UI: 在庫数変更操作
    UI->>App: 入出庫リクエスト送信
    App->>IH: 履歴レコード挿入
    IH->>Trigger: INSERT操作発火
    Trigger->>Products: stock_quantity自動更新
    Products-->>App: 在庫数変更完了
    App-->>UI: 操作完了表示
    UI-->>User: 成功メッセージ表示
```

## データアクセスパターン

以下は、主要なデータアクセスパターンを示します：

1. **商品検索**: カテゴリID、商品名、JANコードによる絞り込み検索
2. **在庫管理**: 商品IDによる在庫の増減操作とその履歴表示
3. **カテゴリ管理**: 階層構造を持つカテゴリの追加、編集、表示
4. **ユーザー認証**: ユーザー名とパスワードによる認証
5. **システム設定**: キーと値のペアによる設定管理