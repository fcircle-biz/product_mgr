# pgAudit監査ログモニタリング

このドキュメントでは、pgAuditを使用したデータベース監査ログの収集と監視について説明します。

## 概要

pgAuditは、PostgreSQLの拡張機能で、データベースの操作を詳細に記録し、セキュリティ監査とコンプライアンスの要件を満たすために使用されます。

## アーキテクチャ

pgAudit はPostgreSQLのシステムログに監査情報を記録します：

```
PostgreSQL Database
    ↓ (pgAudit logs)
PostgreSQL System Logs (/var/log/postgresql/)
```

## セットアップ

### 1. PostgreSQL設定

`docker-compose.yml`でpgAuditが自動的に設定されます：

- pgAudit拡張機能のインストール
- 監査ログ設定の適用
- ログファイルの保存設定

### 2. アプリケーション設定

以下のコンポーネントが追加されています：

- `AuditController`: 監査ログUIのコントローラー
- `AuditLog`: 監査ログのエンティティ
- `AuditService`: 監査ログの検索・集計サービス
- `AuditLogParserService`: ログファイルのパース処理

## 使用方法

### 監査ログの確認

pgAuditはPostgreSQLのシステムログに記録されます。以下の方法で確認できます：

```bash
# PostgreSQLログディレクトリを確認
docker exec product-mgr-postgres ls -la /var/log/postgresql/

# 最新のログファイルを確認
docker exec product-mgr-postgres tail -n 50 /var/log/postgresql/postgresql-*.csv | grep AUDIT

# ログファイルをCSV形式で読み込み
docker exec product-mgr-postgres cat /var/log/postgresql/postgresql-*.csv | grep AUDIT
```

## 監査対象

pgAuditは以下の操作を記録します：

- **READ**: SELECT操作
- **WRITE**: INSERT, UPDATE, DELETE操作
- **FUNCTION**: 関数の実行
- **ROLE**: ロール管理操作
- **DDL**: CREATE, DROP, ALTER操作
- **MISC**: その他の操作

## ログ保存とローテーション

- ログは30日間保存されます
- 古いログは自動的に削除されます
- PostgreSQLログファイルは日次でローテーション
- 圧縮により、ストレージを効率的に使用

## セキュリティ考慮事項

1. **アクセス制御**: 監査ログへのアクセスは管理者権限が必要
2. **機密情報**: クエリパラメータに機密情報が含まれる可能性があるため、適切なアクセス制御が必要
3. **ログ保存期間**: コンプライアンス要件に応じて調整

## トラブルシューティング

### ログが表示されない場合

1. PostgreSQLコンテナが正常に起動しているか確認
2. pgAudit拡張機能がインストールされているか確認：
   ```sql
   SELECT * FROM pg_extension WHERE extname = 'pgaudit';
   ```
3. ログパーサーサービスが実行されているか確認

### ログが多すぎる場合

pgAudit設定を調整してログ量を制御：

```sql
-- 特定の操作のみをログ
ALTER SYSTEM SET pgaudit.log = 'WRITE,DDL';
-- ログレベルの変更
ALTER SYSTEM SET pgaudit.log_level = 'warning';
```

## 設定ファイル

### postgres/postgresql.conf

```conf
# pgAudit設定
shared_preload_libraries = 'pgaudit'
pgaudit.log = 'ALL'
pgaudit.log_catalog = on
pgaudit.log_client = on
pgaudit.log_level = 'log'
pgaudit.log_parameter = on
pgaudit.log_relation = on
pgaudit.log_statement_once = off
```

### docker-compose.yml

```yaml
postgres:
  volumes:
    - postgres-logs:/var/log/postgresql
    - ./postgres/init-pgaudit.sql:/docker-entrypoint-initdb.d/10-init-pgaudit.sql
    - ./postgres/postgresql.conf:/etc/postgresql/postgresql.conf
  command: postgres -c config_file=/etc/postgresql/postgresql.conf
```

## メンテナンス

定期的なメンテナンス作業：

1. ログボリュームの監視
2. 古いログの削除確認
3. パフォーマンスの監視
4. 必要に応じた設定の調整

## 今後の改善案

1. リアルタイムアラート機能の追加
2. より詳細なフィルタリング機能
3. レポートのエクスポート機能
4. 異常検知機能の実装
5. 外部SIEMシステムとの連携