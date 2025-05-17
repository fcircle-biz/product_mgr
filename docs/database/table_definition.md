# 商品管理システム テーブル定義書

## 1. 概要

本書は、商品管理システムで使用するデータベースのテーブル定義を記載したものです。
データベースエンジンにはPostgreSQLを使用し、文字コードはUTF-8を採用しています。

## 2. テーブル一覧

| No. | 物理名 | 論理名 | 説明 |
|-----|--------|--------|------|
| 1 | products | 商品 | 商品の基本情報と在庫情報を管理するマスタテーブル |
| 2 | categories | カテゴリ | 商品カテゴリを管理するマスタテーブル |
| 3 | inventory_histories | 在庫履歴 | 在庫の入出庫履歴を記録するテーブル |
| 4 | users | ユーザー | システムのユーザー情報を管理するテーブル |
| 5 | system_settings | システム設定 | アプリケーション全体の設定を管理するテーブル |

## 3. テーブル定義

### 3.1 商品テーブル (products)

**説明：** 商品の基本情報を管理するマスタテーブル

| No. | 物理名 | 論理名 | データ型 | NULL | 主キー | 外部キー | デフォルト | 説明 |
|-----|--------|--------|----------|------|--------|----------|------------|------|
| 1 | id | ID | SERIAL | NO | PK | | AUTO_INCREMENT | 商品を一意に識別するID |
| 2 | name | 商品名 | VARCHAR(100) | NO | | | | 商品の名称 |
| 3 | jan_code | JANコード | VARCHAR(13) | YES | | | NULL | 商品のJANコード |
| 4 | description | 説明 | TEXT | YES | | | NULL | 商品の詳細説明 |
| 5 | price | 価格 | NUMERIC(10,0) | NO | | | 0 | 商品の価格（税抜） |
| 6 | category_id | カテゴリID | INTEGER | NO | | FK (categories.id) | | 商品が属するカテゴリのID |
| 7 | stock_quantity | 在庫数量 | INTEGER | NO | | | 0 | 現在の在庫数 |
| 8 | stock_unit | 在庫単位 | VARCHAR(20) | NO | | | '個' | 在庫を管理する単位（個、箱、kg等） |
| 9 | status | 商品状態 | VARCHAR(20) | NO | | | '販売中' | 商品の状態（販売中、入荷待ち等） |
| 10 | created_at | 作成日時 | TIMESTAMP | NO | | | CURRENT_TIMESTAMP | レコード作成日時 |
| 11 | updated_at | 更新日時 | TIMESTAMP | NO | | | CURRENT_TIMESTAMP | レコード更新日時 |

### 3.2 カテゴリテーブル (categories)

**説明：** 商品カテゴリを管理するマスタテーブル

| No. | 物理名 | 論理名 | データ型 | NULL | 主キー | 外部キー | デフォルト | 説明 |
|-----|--------|--------|----------|------|--------|----------|------------|------|
| 1 | id | ID | INT | NO | PK | | AUTO_INCREMENT | カテゴリを一意に識別するID |
| 2 | name | カテゴリ名 | VARCHAR(50) | NO | | | | カテゴリの名称 |
| 3 | parent_id | 親カテゴリID | INT | YES | | FK (categories.id) | NULL | 親カテゴリのID（最上位カテゴリの場合はNULL） |
| 4 | created_at | 作成日時 | TIMESTAMP | NO | | | CURRENT_TIMESTAMP | レコード作成日時 |
| 5 | updated_at | 更新日時 | TIMESTAMP | YES | | | NULL ON UPDATE CURRENT_TIMESTAMP | レコード更新日時 |

### 3.3 在庫情報（products テーブルに統合）

**説明：** 在庫関連のカラムは products テーブルに統合されています

| No. | 物理名 | 論理名 | データ型 | NULL | 主キー | 外部キー | デフォルト | 説明 |
|-----|--------|--------|----------|------|--------|----------|------------|------|
| 1 | stock_quantity | 在庫数量 | INTEGER | NO | | | 0 | 現在の在庫数（products テーブルのカラム） |
| 2 | stock_unit | 在庫単位 | VARCHAR(20) | NO | | | '個' | 在庫を管理する単位（個、箱、kg等）（products テーブルのカラム） |
| 3 | status | 商品状態 | VARCHAR(20) | NO | | | '販売中' | 商品の状態（販売中、入荷待ち等）（products テーブルのカラム） |

### 3.4 在庫履歴テーブル (inventory_histories)

**説明：** 在庫の入出庫履歴を記録するテーブル

| No. | 物理名 | 論理名 | データ型 | NULL | 主キー | 外部キー | デフォルト | 説明 |
|-----|--------|--------|----------|------|--------|----------|------------|------|
| 1 | id | ID | SERIAL | NO | PK | | AUTO_INCREMENT | 在庫履歴を一意に識別するID |
| 2 | product_id | 商品ID | INTEGER | NO | | FK (products.id) | | 在庫変更が行われた商品のID |
| 3 | quantity_change | 数量変化 | INTEGER | NO | | | | 在庫の変化量（正の値：入庫、負の値：出庫） |
| 4 | reason | 理由 | VARCHAR(255) | YES | | | NULL | 在庫変更の理由 |
| 5 | operated_by | 操作者ID | INTEGER | NO | | FK (users.id) | | 在庫変更を行ったユーザーのID |
| 6 | created_at | 作成日時 | TIMESTAMP | NO | | | CURRENT_TIMESTAMP | 在庫変更が行われた日時 |

### 3.5 ユーザーテーブル (users)

**説明：** システムのユーザー情報を管理するテーブル

| No. | 物理名 | 論理名 | データ型 | NULL | 主キー | 外部キー | デフォルト | 説明 |
|-----|--------|--------|----------|------|--------|----------|------------|------|
| 1 | id | ID | SERIAL | NO | PK | | AUTO_INCREMENT | ユーザーを一意に識別するID |
| 2 | username | ユーザー名 | VARCHAR(50) | NO | | | | ログイン用のユーザー名 |
| 3 | password | パスワード | VARCHAR(100) | NO | | | | ハッシュ化されたパスワード |
| 4 | full_name | 氏名 | VARCHAR(100) | NO | | | | ユーザーの氏名 |
| 5 | role | 権限 | VARCHAR(20) | NO | | | 'USER' | ユーザーの権限レベル (ADMIN, USER, ...) |
| 6 | enabled | 有効フラグ | BOOLEAN | NO | | | TRUE | アカウントが有効かどうか |
| 7 | created_at | 作成日時 | TIMESTAMP | NO | | | CURRENT_TIMESTAMP | ユーザーアカウント作成日時 |
| 8 | updated_at | 更新日時 | TIMESTAMP | NO | | | CURRENT_TIMESTAMP | ユーザー情報更新日時 |

## 4. インデックス定義

| No. | テーブル名 | インデックス名 | カラム | 種類 | 説明 |
|-----|------------|----------------|--------|------|------|
| 1 | products | PRIMARY | id | PRIMARY | 商品テーブルの主キー |
| 2 | products | idx_products_code | code | UNIQUE | 商品コードによる高速検索用 |
| 3 | products | idx_products_category_id | category_id | INDEX | カテゴリIDによる絞り込み用 |
| 4 | products | idx_products_name | name | INDEX | 商品名による検索用 |
| 5 | products | idx_products_deleted_at | deleted_at | INDEX | 論理削除フラグによる絞り込み用 |
| 6 | categories | PRIMARY | id | PRIMARY | カテゴリテーブルの主キー |
| 7 | categories | idx_categories_parent_id | parent_id | INDEX | 親カテゴリによる絞り込み用 |
| 8 | inventories | PRIMARY | id | PRIMARY | 在庫テーブルの主キー |
| 9 | inventories | idx_inventories_product_id | product_id | UNIQUE | 商品IDによる在庫検索用（一商品につき一つの在庫レコード） |
| 10 | inventory_histories | PRIMARY | id | PRIMARY | 在庫履歴テーブルの主キー |
| 11 | inventory_histories | idx_inventory_histories_product_id | product_id | INDEX | 商品IDによる履歴検索用 |
| 12 | inventory_histories | idx_inventory_histories_created_at | created_at | INDEX | 日時による履歴検索用 |
| 13 | users | PRIMARY | id | PRIMARY | ユーザーテーブルの主キー |
| 14 | users | idx_users_username | username | UNIQUE | ユーザー名によるログイン認証用 |
| 15 | users | idx_users_email | email | UNIQUE | メールアドレスによるユーザー識別用 |
| 16 | users | idx_users_deleted_at | deleted_at | INDEX | 論理削除フラグによる絞り込み用 |

## 5. リレーションシップ定義

| No. | 関連元テーブル | 関連元カラム | 関連先テーブル | 関連先カラム | 関連名 | カーディナリティ | ON DELETE | ON UPDATE |
|-----|----------------|--------------|--------------|--------------|--------|-------------------|-----------|-----------|
| 1 | products | category_id | categories | id | products_categories_fk | 多対1 | RESTRICT | CASCADE |
| 2 | categories | parent_id | categories | id | categories_parent_fk | 多対1 | RESTRICT | CASCADE |
| 3 | inventories | product_id | products | id | inventories_products_fk | 1対1 | CASCADE | CASCADE |
| 4 | inventory_histories | product_id | products | id | inventory_histories_products_fk | 多対1 | RESTRICT | CASCADE |
| 5 | inventory_histories | operated_by | users | id | inventory_histories_users_fk | 多対1 | RESTRICT | CASCADE |

---

文書作成日: 2025年5月4日  
文書管理番号: DB-DEF-001  
バージョン: 1.0