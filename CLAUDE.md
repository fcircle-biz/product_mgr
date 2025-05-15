# CLAUDE.md - プロジェクト実行ガイド

このドキュメントは、在庫管理システムの構築とpgAuditによるデータベース監査機能の実装手順をまとめたものです。

## システム概要

- **プロジェクト名**: 在庫管理システム（Product Management System）
- **技術スタック**: Spring Boot 3.2, PostgreSQL 15, Docker, Thymeleaf, pgAudit
- **主要機能**: 商品管理、在庫管理、レポート生成、データベース監査

## 実行手順

### 1. 前提条件

- Docker および Docker Compose がインストールされていること
- Git がインストールされていること
- ポート8080（アプリケーション）と5433（PostgreSQL）が空いていること

### 2. 初回セットアップ

```bash
# リポジトリをクローン（または現在のディレクトリで）
cd /home/ichimaru/product_mgr

# pgAudit機能ブランチに切り替え
git checkout pgaudit-monitoring

# 既存のDockerボリュームをクリア（クリーンスタート用）
docker compose down -v

# コンテナをビルドして起動
docker compose up -d
```

### 3. データベースの初期化

データベースはDockerボリューム作成時に自動的に初期化されます。

初期化スクリプトは以下の順序で実行されます：
1. `01-schema.sql`: テーブル定義とトリガー
2. `02-initial-data.sql`: 初期データ（ユーザー、カテゴリ、商品）
3. `03-pgaudit.sql`: pgAudit拡張機能の設定

手動で再初期化する場合：
```bash
# ボリュームを削除して再作成
docker compose down -v
docker compose up -d
```

個別にSQLを実行する場合：
```bash
# スキーマの確認
docker exec product-mgr-postgres psql -U productmgr -d productmgr -c "\dt"

# データの確認
docker exec product-mgr-postgres psql -U productmgr -d productmgr -c "SELECT * FROM products LIMIT 5"
```

### 4. システムへのアクセス

- **URL**: http://localhost:8080
- **管理者アカウント**:
  - ユーザー名: `admin`
  - パスワード: `admin`

### 5. pgAudit監査機能の使用

pgAuditはPostgreSQLのシステムログに監査情報を記録します。

#### PostgreSQLシステムログでpgAuditログを確認

```bash
# PostgreSQLログディレクトリを確認
docker exec product-mgr-postgres ls -la /var/log/postgresql/

# 最新のログファイルを確認
docker exec product-mgr-postgres tail -n 50 /var/log/postgresql/postgresql-*.csv | grep AUDIT

# ログファイルをCSV形式で読み込み
docker exec product-mgr-postgres cat /var/log/postgresql/postgresql-*.csv | grep AUDIT
```

```


## トラブルシューティング

### ログインできない場合

```bash
# パスワードを再設定
docker exec product-mgr-postgres psql -U productmgr -d productmgr -c "UPDATE users SET password = '\$2a\$12\$sjRM4ziNZGOElrZrLa3WFeGFgmn4qJNLyMkG/Q8hdHcOvfgiDiIYK' WHERE username = 'admin';"
```

### データベースが初期化されない場合

```bash
# ボリュームを削除して再起動
docker compose down -v
docker compose up -d

# 手動でデータベーススキーマを初期化
docker exec product-mgr-postgres psql -U productmgr -d productmgr < /docker-entrypoint-initdb.d/01-schema.sql
```

### pgAuditが動作しない場合

```bash
# PostgreSQL拡張機能の確認
docker exec product-mgr-postgres psql -U productmgr -d productmgr -c "SELECT * FROM pg_extension WHERE extname = 'pgaudit';"

# 設定の確認
docker exec product-mgr-postgres psql -U productmgr -d productmgr -c "SHOW pgaudit.log;"
```

## 開発時のコマンド

### コンテナの管理

```bash
# 起動
docker compose up -d

# 停止
docker compose down

# ログ確認
docker logs product-mgr-app -f
docker logs product-mgr-postgres -f

# コンテナに入る
docker exec -it product-mgr-app bash
docker exec -it product-mgr-postgres bash
```

### データベース操作

```bash
# PostgreSQLに接続
docker exec -it product-mgr-postgres psql -U productmgr -d productmgr

# テーブル一覧
docker exec product-mgr-postgres psql -U productmgr -d productmgr -c "\dt"

# ユーザー確認
docker exec product-mgr-postgres psql -U productmgr -d productmgr -c "SELECT username, role FROM users;"
```

### アプリケーション開発

```bash
# テスト実行
./mvnw test

# ビルド
./mvnw package

# Springコンポーネントの生成（パスワードエンコーダー等）
./mvnw spring-boot:run
```

## 設定ファイル

### 重要な設定ファイル

- `docker-compose.yml`: Dockerコンテナ設定
- `src/main/resources/application.yml`: Spring Boot設定
- `postgres/postgresql.conf`: PostgreSQL設定（pgAudit含む）
- `postgres/init-pgaudit.sql`: pgAudit初期化スクリプト

### セキュリティ設定

- BCryptパスワードエンコーダー（コスト12）
- Spring Security設定: `SecurityConfig.java`
- 監査ログアクセス: 管理者権限のみ

## pgAudit機能詳細

### 監査対象操作

- SELECT: データ読み取り
- INSERT/UPDATE/DELETE: データ変更
- CREATE/DROP/ALTER: スキーマ変更
- GRANT/REVOKE: 権限管理

### ログ保存設定

- ログ保存期間: 30日（自動削除）
- ログローテーション: 日次
- ログ形式: CSV

### カスタマイズ

- 監査対象変更: `postgres/postgresql.conf`の`pgaudit.log`設定

## 参考情報

- pgAudit公式ドキュメント: https://github.com/pgaudit/pgaudit
- Spring Security: https://spring.io/projects/spring-security
- Spring Boot: https://spring.io/projects/spring-boot

## 注意事項

- 本番環境では適切なパスワードポリシーを設定すること
- 監査ログには機密情報が含まれる可能性があるため、適切なアクセス制御が必要
- データベースのバックアップを定期的に実行すること