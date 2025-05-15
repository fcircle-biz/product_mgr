# pgAudit 監査機能セットアップガイド

## クイックスタート

1. 現在のブランチに切り替え：
   ```bash
   git checkout pgaudit-monitoring
   ```

2. Docker Composeで起動：
   ```bash
   docker-compose down -v  # 既存のボリュームをクリア
   docker-compose up -d
   ```

3. アプリケーションにアクセス：
   - URL: http://localhost:8080
   - 管理者でログイン
   - ナビゲーションバーの「監査ログ」をクリック

## テスト手順

### 1. 監査ログの生成

以下の操作を実行して監査ログを生成：

```sql
-- PostgreSQLに接続
docker exec -it product-mgr-postgres psql -U productmgr -d productmgr

-- テストクエリの実行
SELECT * FROM products;
INSERT INTO products (name, price) VALUES ('Test Product', 1000);
UPDATE products SET price = 1500 WHERE name = 'Test Product';
DELETE FROM products WHERE name = 'Test Product';
```

### 2. ダッシュボードの確認

1. http://localhost:8080/audit にアクセス
2. 統計情報の確認：
   - 総ログ数
   - 期間別ログ数
   - コマンド別統計
   - ユーザー別統計

### 3. ログ検索機能のテスト

1. 「詳細な検索」をクリック
2. 検索条件でフィルタリング：
   - ユーザー名: productmgr
   - コマンド: SELECT
   - 期間を指定

## 監視対象の操作

pgAuditは以下の操作を記録します：

- **SELECT**: データ読み取り
- **INSERT**: データ追加
- **UPDATE**: データ更新
- **DELETE**: データ削除
- **CREATE/DROP/ALTER**: スキーマ変更
- **GRANT/REVOKE**: 権限管理

## トラブルシューティング

### ログが表示されない場合

1. pgAudit拡張機能の確認：
   ```sql
   SELECT * FROM pg_extension WHERE extname = 'pgaudit';
   ```

2. 設定の確認：
   ```sql
   SHOW pgaudit.log;
   ```

3. ログパーサーの状態確認：
   - Spring Bootのコンソールログを確認
   - スケジュールされたタスクが実行されているか確認

### パフォーマンスの問題

ログ量が多い場合は、特定の操作のみを記録するよう設定：

```sql
-- 書き込み操作とDDLのみをログ
ALTER SYSTEM SET pgaudit.log = 'WRITE,DDL';
SELECT pg_reload_conf();
```

## カスタマイズ

### ログ保存期間の変更

`AuditLogParserService.java`の`rotateOldLogs`メソッドで期間を調整：

```java
// 60日間保存する場合
LocalDateTime cutoffDate = LocalDateTime.now().minusDays(60);
```

### ダッシュボードのカスタマイズ

`audit/dashboard.html`を編集して表示をカスタマイズ：
- グラフの種類変更
- 統計期間の調整
- 新しい集計の追加

## セキュリティ注意事項

1. 監査ログには機密情報が含まれる可能性があります
2. アクセス権限を適切に管理してください
3. 定期的にログを確認し、不正アクセスを検知してください

## 関連ファイル

- `/postgres/init-pgaudit.sql`: pgAudit初期設定
- `/postgres/postgresql.conf`: PostgreSQL設定
- `/src/main/java/com/example/productmgr/controller/AuditController.java`: 監査ログUI
- `/src/main/java/com/example/productmgr/service/AuditLogParserService.java`: ログパーサー
- `/src/main/resources/templates/audit/*.html`: 監査ログ画面